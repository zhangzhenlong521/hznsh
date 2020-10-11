package cn.com.pushworld.salary.bs.convert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.pushworld.salary.bs.dinterface.DataIFCConvertToReport;
import cn.com.pushworld.salary.bs.dinterface.DataInterfaceDMO;

/**
 * 处理支行及营业部业务统计表的转换
 * **/
public class DataConvert_GYZHYWTJ implements DataIFCConvertToReport {

	public String convert(HashVO mainConfig, String jobid, String dataDate) throws Exception {
		CommDMO dmo = new CommDMO();
		HashVO configitemvos[] = dmo.getHashVoArrayByDS(null, "select * from sal_convert_ifcdata_item_conf where parentid = " + mainConfig.getStringValue("id") + " order by seq");
		TBUtil tbutil = new TBUtil();
		String sourceifc = mainConfig.getStringValue("sourceifc"); //得到接口配置主表ID
		HashVO[] ifcconfig = dmo.getHashVoArrayByDS(null, "select * from sal_data_interface_main where id =" + sourceifc);//配置表
		if (ifcconfig == null || ifcconfig.length == 0) {
			throw new Exception(mainConfig.getStringValue("reportname") + "接口数据转换配置的数据接口来源为空.");
		}
		String syndatalogid = null;
		//
		if (tbutil.isEmpty(jobid) || "-1".equals(jobid)) { //非自动任务执行。
			syndatalogid = dmo.getStringValueByDS(null, "select id from sal_data_interface_log where parentid = " + ifcconfig[0].getStringValue("id") + " and exectype='" + DataInterfaceDMO.Syn_Log_Exec_Type_TBSJ + "' and state='" + DataInterfaceDMO.Syn_Log_Exec_State_Success + "' and datadate='" + dataDate + "' order by exectime desc");
			if (tbutil.isEmpty(syndatalogid)) { //如果没有找到同步日志。直接报错。
				throw new Exception("[" + dataDate + "]数据没有没有同步过,请检查.");
			}
		} else {
			syndatalogid = dmo.getStringValueByDS(null, "select id from sal_data_interface_log where parentid = " + ifcconfig[0].getStringValue("id") + " and exectype='" + DataInterfaceDMO.Syn_Log_Exec_Type_TBSJ + "' and state='" + DataInterfaceDMO.Syn_Log_Exec_State_Success + "' and jobid=" + jobid);
		}
		logger.info("日期为：【" + dataDate + "】的【支行及营业部业务统计表】数据开始同步");
		HashVO[] datas = dmo.getHashVoArrayByDS(null, "select dept.shortname as B,sum(t1.c) as C,sum(t1.d) as D,sum(t1.e) as E,sum(t1.f) as F,sum(t1.g) as G,sum(t1.h) as H  from " + ifcconfig[0].getStringValue("tablename") + " t1 left join pub_corp_dept dept on t1.A = dept.code where t1.logid = '" + syndatalogid + "' group by dept.shortname order by t1.A");
		String convertDataLogid = dmo.getSequenceNextValByDS(null, "S_SAL_CONVERT_IFCDATA_LOG");
		String year = dataDate.substring(0, 4);
		String month = dataDate.substring(5, 7);
		List insertDatasSql = new ArrayList();
		for (int i = 0; i < datas.length; i++) { //
			InsertSQLBuilder insert = new InsertSQLBuilder(mainConfig.getStringValue("tablename"));
			insert.putFieldValue("logid", convertDataLogid);
			insert.putFieldValue("year", year);
			insert.putFieldValue("month", month);
			for (int j = 0; j < configitemvos.length; j++) { //
				String itemcode = configitemvos[j].getStringValue("itemcode");//报表数据中的字段
				String ifcitem = configitemvos[j].getStringValue("ifcitem", ""); //接口表中的字段
				insert.putFieldValue(itemcode, datas[i].getStringValue(ifcitem, ""));
			}
			insertDatasSql.add(insert);
		}
		InsertSQLBuilder logsql = new DataInterfaceDMO().getConvertLog(convertDataLogid, mainConfig.getStringValue("id"), mainConfig.getStringValue("reportname"), jobid, dataDate, DataInterfaceDMO.Syn_Log_Convert_Type_SJZH, DataInterfaceDMO.Syn_Log_Exec_State_Success, "数据转换成功，共计" + insertDatasSql.size() + "条");
		insertDatasSql.add(logsql);
		UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder("sal_convert_ifcdata");
		updateMainTable.setWhereCondition(" id=" + mainConfig.getStringValue("id"));
		updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
		insertDatasSql.add(updateMainTable);
		dmo.executeBatchByDSImmediately(null, insertDatasSql, false);
		logger.info("日期为：【" + dataDate + "】的【支行及营业部业务统计表】数据同步成功");
		insertDatasSql.clear();

		HashVO[] lastdate = null;

		if (year.equalsIgnoreCase("2014")) {
			lastdate = dmo.getHashVoArrayByDS(null, "select a ,b,c,d,e,f,g from sal_data_base_003");
		} else {
			int lastyear = Integer.getInteger(year) - 1;
			lastdate = dmo.getHashVoArrayByDS(null, "select a,b,d as c,f as d,h as e,j as f,k as g from excel_tab_4 where year ='" + lastyear + "' and month ='12'");
		}

		HashMap<String, HashVO> hashMap = new HashMap<String, HashVO>();
		if (lastdate != null && lastdate.length > 0) {
			for (int i = 0; i < lastdate.length; i++) {
				hashMap.put(lastdate[i].getStringValue("a"), lastdate[i]);
			}
		}

		/*
		 *  把数据插入到原有excel中。
		 *  数据是实时性的，因此只需取得最后一次同步的数据即可
		 * */

		logger.info(year + "-" + month + "的上传报表【支行及营业部业务统计表】数据开始转换");
		HashVO monthSum[] = dmo.getHashVoArrayByDS(null, "select t1.a as A,sum(t1.b)/10000 as num,sum(t1.c)/10000 as num1,sum(t1.d)/10000 as num2,sum(t1.e)/10000 as num3,sum(t1.f)/10000 as num4,sum(t1.g)/10000 as num5,sum(t1.h)/10000 as num6,sum(t1.i)/10000 as num7,sum(t1.j)/10000 as num8,sum(t1.k)/10000 as num9,sum(t1.l)/10000 as num10,sum(t1.m)/10000 as num11 from " + mainConfig.getStringValue("tablename") + " t1  where t1.year='" + year + "' and t1.month='"
				+ month + "' and logid ='" + convertDataLogid + "' group by t1.A");
		DeleteSQLBuilder delhisdata = new DeleteSQLBuilder("excel_tab_4");
		delhisdata.setWhereCondition(" year='" + year + "' and month='" + month + "'");
		insertDatasSql.add(delhisdata);
		String time = tbutil.getCurrTime();
		for (int i = 0; i < monthSum.length; i++) {
			InsertSQLBuilder insertsql = new InsertSQLBuilder("excel_tab_4");
			String id = dmo.getSequenceNextValByDS(null, "s_excel_tab_4", 100);
			insertsql.putFieldValue("id", id);
			insertsql.putFieldValue("year", year);
			insertsql.putFieldValue("month", month);
			insertsql.putFieldValue("creattime", time);
			insertsql.putFieldValue("A", monthSum[i].getStringValue("A"));
			if (hashMap.get(monthSum[i].getStringValue("A")) != null) {
				insertsql.putFieldValue("B", hashMap.get(monthSum[i].getStringValue("A")).getStringValue("b"));
				insertsql.putFieldValue("D", hashMap.get(monthSum[i].getStringValue("A")).getStringValue("c"));
				insertsql.putFieldValue("F", hashMap.get(monthSum[i].getStringValue("A")).getStringValue("d"));
				insertsql.putFieldValue("H", hashMap.get(monthSum[i].getStringValue("A")).getStringValue("e"));
				insertsql.putFieldValue("J", hashMap.get(monthSum[i].getStringValue("A")).getStringValue("f"));
				insertsql.putFieldValue("K", hashMap.get(monthSum[i].getStringValue("A")).getStringValue("g"));
			} else {
				insertsql.putFieldValue("B", monthSum[i].getStringValue("num"));
				insertsql.putFieldValue("D", monthSum[i].getStringValue("num2"));
				insertsql.putFieldValue("F", monthSum[i].getStringValue("num4"));
				insertsql.putFieldValue("H", monthSum[i].getStringValue("num6"));
				insertsql.putFieldValue("J", monthSum[i].getStringValue("num8"));
				insertsql.putFieldValue("K", monthSum[i].getStringValue("num9"));
			}
			insertsql.putFieldValue("C", monthSum[i].getStringValue("num1"));
			insertsql.putFieldValue("E", monthSum[i].getStringValue("num3"));
			insertsql.putFieldValue("G", monthSum[i].getStringValue("num5"));
			insertsql.putFieldValue("I", monthSum[i].getStringValue("num7"));
			insertsql.putFieldValue("L", monthSum[i].getStringValue("num10"));
			insertsql.putFieldValue("M", monthSum[i].getStringValue("num11"));
			insertDatasSql.add(insertsql);
		}
		UpdateSQLBuilder lasteditSql = new UpdateSQLBuilder("EXCEL_TAB");
		lasteditSql.setWhereCondition(" TABLENAME='excel_tab_4'");
		lasteditSql.putFieldValue("UPDATETIME", time);
		insertDatasSql.add(lasteditSql);
		dmo.executeBatchByDSImmediately(null, insertDatasSql, false);
		logger.info(year + "-" + month + "的上传报表【支行及营业部业务统计表】数据转换成功");
		return "成功";
	}

}
