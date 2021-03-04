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
 * 处理电子银行的转换
 * **/
public class DataConvert_GYDZYH implements DataIFCConvertToReport {

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
		logger.info("日期为：【" + dataDate + "】的【电子银行】数据开始同步");
		HashVO[] datas = dmo.getHashVoArrayByDS(null, "select dept.shortname as a,t1.b as b,t1.c as c,t1.d as d,t1.e as e,t1.f as f,t1.g as g,t1.h as h,t1.i as i,t1.j as j,t1.k as k,t1.l as l ,t1.m as m  from " + ifcconfig[0].getStringValue("tablename") + " t1 left join pub_corp_dept dept on t1.A = dept.code where t1.logid = '" + syndatalogid + "' order by t1.A");
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
		logger.info("日期为：【" + dataDate + "】的【电子银行】数据同步成功");
		insertDatasSql.clear();

		//取得系数管理里面的上传的数据。
		HashMap<String, HashVO> deptmap = new HashMap<String, HashVO>();
		HashVO[] hashVOs = dmo.getHashVoArrayByDS(null, "select * from sal_data_base_005");
		if (hashVOs != null && hashVOs.length > 0) {
			for (int i = 0; i < hashVOs.length; i++) {
				deptmap.put(hashVOs[i].getStringValue("A"), hashVOs[i]);
			}
		}
		/*
		 *  把数据插入到原有excel中。
		 * */
		logger.info(year + "-" + month + "的上传报表【电子银行】数据开始转换");
		HashVO monthSum[] = dmo.getHashVoArrayByDS(null, "select t1.* from " + mainConfig.getStringValue("tablename") + " t1  where t1.year='" + year + "' and month='" + month + "' and logid='" + convertDataLogid + "'");
		HashMap<String, String> tellerno_deptname = dmo.getHashMapBySQLByDS(null, "select t1.tellerno,t2.shortname from v_sal_personinfo t1 left join pub_corp_dept t2 on t1.deptid = t2.id");
		DeleteSQLBuilder delhisdata = new DeleteSQLBuilder("excel_tab_9");
		delhisdata.setWhereCondition(" year='" + year + "' and month='" + month + "'");
		insertDatasSql.add(delhisdata);
		String time = tbutil.getCurrTime();
		for (int i = 0; i < monthSum.length; i++) {
			InsertSQLBuilder insertsql = new InsertSQLBuilder("excel_tab_9");
			String id = dmo.getSequenceNextValByDS(null, "s_excel_tab_9");
			insertsql.putFieldValue("id", id);
			insertsql.putFieldValue("year", year);
			insertsql.putFieldValue("month", month);
			insertsql.putFieldValue("creattime", time);
			String deptname = monthSum[i].getStringValue("A");
			insertsql.putFieldValue("A", deptname);
			insertsql.putFieldValue("B", monthSum[i].getStringValue("B"));
			insertsql.putFieldValue("C", monthSum[i].getStringValue("C"));
			insertsql.putFieldValue("D", monthSum[i].getStringValue("D"));
			insertsql.putFieldValue("E", monthSum[i].getStringValue("E"));
			insertsql.putFieldValue("F", monthSum[i].getStringValue("F"));
			insertsql.putFieldValue("G", monthSum[i].getStringValue("G"));
			insertsql.putFieldValue("H", monthSum[i].getStringValue("H"));
			insertsql.putFieldValue("I", monthSum[i].getStringValue("I"));
			insertsql.putFieldValue("J", monthSum[i].getStringValue("J"));
			insertsql.putFieldValue("K", monthSum[i].getStringValue("K"));
			insertsql.putFieldValue("L", monthSum[i].getStringValue("L"));
			insertsql.putFieldValue("M", monthSum[i].getStringValue("M"));
			insertsql.putFieldValue("N", monthSum[i].getStringValue("N"));
			insertsql.putFieldValue("O", monthSum[i].getStringValue("O"));
			insertsql.putFieldValue("P", monthSum[i].getStringValue("P"));
			insertsql.putFieldValue("Q", monthSum[i].getStringValue("Q"));
			if (deptmap.get(deptname) != null) {
				insertsql.putFieldValue("R", deptmap.get(deptname).getStringValue("B"));
				insertsql.putFieldValue("T", deptmap.get(deptname).getStringValue("C"));
				insertsql.putFieldValue("U", deptmap.get(deptname).getStringValue("D"));
				insertsql.putFieldValue("V", deptmap.get(deptname).getStringValue("E"));
				insertsql.putFieldValue("S", deptmap.get(deptname).getStringValue("F"));
				insertsql.putFieldValue("W", deptmap.get(deptname).getStringValue("G"));
				insertsql.putFieldValue("X", deptmap.get(deptname).getStringValue("H"));
				insertsql.putFieldValue("Y", deptmap.get(deptname).getStringValue("I"));
				insertsql.putFieldValue("Z", deptmap.get(deptname).getStringValue("J"));
				insertsql.putFieldValue("AA", deptmap.get(deptname).getStringValue("K"));
				insertsql.putFieldValue("AB", deptmap.get(deptname).getStringValue("L"));
			}
			insertDatasSql.add(insertsql);
		}
		UpdateSQLBuilder lasteditSql = new UpdateSQLBuilder("EXCEL_TAB");
		lasteditSql.setWhereCondition(" TABLENAME='excel_tab_9'");
		lasteditSql.putFieldValue("UPDATETIME", time);
		insertDatasSql.add(lasteditSql);
		dmo.executeBatchByDSImmediately(null, insertDatasSql, false);
		logger.info(year + "-" + month + "的上传报表【电子银行】数据转换成功");
		return "成功";
	}

}
