package cn.com.pushworld.salary.bs.convert;

import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.pushworld.salary.bs.dinterface.DataIFCConvertToReport;
import cn.com.pushworld.salary.bs.dinterface.DataInterfaceDMO;

/***
 * 客户经理维护贷款笔数统计表
 * @author 张营闯/2014-07-28
 * ***/
public class DataConvert_GYWHDKBSTJ implements DataIFCConvertToReport {

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
		//取得同步后的数据，然后插入到转换表格中
		logger.info("日期为：【"+dataDate+"】的【客户经理维护贷款笔数统计表】数据开始同步");
		HashVO[] datas = dmo.getHashVoArrayByDS(null, "select dept.shortname as A,t1.B,t1.C,t1.D,t1.E,t1.F,t1.G,t1.H,t1.I,t1.J,t1.K,t1.L,t1.M,t1.N,t1.O,t1.P,t1.Q from " + ifcconfig[0].getStringValue("tablename") + " t1 left join pub_corp_dept dept on t1.A = dept.code  where t1.logid = '" + syndatalogid + "' order by t1.a");
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
				String ifcitem = configitemvos[j].getStringValue("ifcitem", ""); //接口表中的字段,如果接口配置中未定义，这设置为空
				insert.putFieldValue(itemcode, datas[i].getStringValue(ifcitem, ""));//给相应的字段赋值
			}
			insertDatasSql.add(insert);
		}
		InsertSQLBuilder logsql = new DataInterfaceDMO().getConvertLog(convertDataLogid, mainConfig.getStringValue("id"), mainConfig.getStringValue("reportname"), jobid, dataDate, DataInterfaceDMO.Syn_Log_Convert_Type_SJZH, DataInterfaceDMO.Syn_Log_Exec_State_Success, "数据转换成功，共计" + insertDatasSql.size() + "条");
		insertDatasSql.add(logsql);
		UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder("sal_convert_ifcdata");
		updateMainTable.setWhereCondition(" id=" + mainConfig.getStringValue("id"));
		updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
		insertDatasSql.add(updateMainTable);
		dmo.executeBatchByDSImmediately(null, insertDatasSql,false);
		logger.info("日期为：【"+dataDate+"】的【客户经理维护贷款笔数统计表】数据同步成功");
		insertDatasSql.clear();
		/*
		 *  把数据插入到原有excel中。
		 *  每次同步的都是最新的贷款维护情况，因此在插入到excel表格中只取最新同步数据即可
		 * */
		
		logger.info(year+"-"+month+"的上传报表【客户经理维护贷款笔数统计表】数据开始转换");
		HashVO monthSum[] = dmo.getHashVoArrayByDS(null, "select t1.* from " + mainConfig.getStringValue("tablename") + " t1  where t1.year='" + year + "' and t1.month='" + month + "' and logid ='" + convertDataLogid + "'");
		DeleteSQLBuilder delhisdata = new DeleteSQLBuilder("excel_tab_6");
		delhisdata.setWhereCondition(" year='" + year + "' and month='" + month + "'");
		insertDatasSql.add(delhisdata);
		String time = tbutil.getCurrTime();
		for (int i = 0; i < monthSum.length; i++) {
			InsertSQLBuilder insertsql = new InsertSQLBuilder("excel_tab_6");
			String id = dmo.getSequenceNextValByDS(null, "s_excel_tab_6", 5000);
			insertsql.putFieldValue("id", id);
			insertsql.putFieldValue("year", year);
			insertsql.putFieldValue("month", month);
			insertsql.putFieldValue("creattime", time);
			insertsql.putFieldValue("A", monthSum[i].getStringValue("A"));
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
			insertsql.putFieldValue("R", monthSum[i].getStringValue("R"));
			insertDatasSql.add(insertsql);
		}
		UpdateSQLBuilder lasteditSql = new UpdateSQLBuilder("EXCEL_TAB");
		lasteditSql.setWhereCondition(" TABLENAME='excel_tab_6'");
		lasteditSql.putFieldValue("UPDATETIME", time);
		insertDatasSql.add(lasteditSql);
		dmo.executeBatchByDSImmediately(null, insertDatasSql, false);
		logger.info(year+"-"+month+"的上传报表【客户经理维护贷款笔数统计表】数据转换成功");
		return "成功";
	}

}
