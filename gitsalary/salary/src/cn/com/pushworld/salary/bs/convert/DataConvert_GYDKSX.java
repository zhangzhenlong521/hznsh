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
 * 处理客户经理贷款收息表的转换
 * **/
public class DataConvert_GYDKSX implements DataIFCConvertToReport {

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
		logger.info("日期为：【"+dataDate+"】的【客户经理贷款收息】数据开始同步");
		HashVO[] datas = dmo.getHashVoArrayByDS(null, "select dept.shortname,t2.name,t2.tellerno,sum(t1.F) as num from " + ifcconfig[0].getStringValue("tablename") + " t1 left join pub_corp_dept dept on t1.B = dept.code left join v_sal_personinfo  t2 on instr(t2.tellerno,t1.D)>0 where t1.logid = '" + syndatalogid + "' group by t2.tellerno,t2.name,dept.shortname order by t1.B");
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
				String ifcitem = configitemvos[j].getStringValue("ifcitem"); //接口表中的字段
				insert.putFieldValue(itemcode, datas[i].getStringValue(j));
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
		logger.info("日期为：【"+dataDate+"】的【客户经理贷款收息】数据同步成功");
		insertDatasSql.clear();
		/*
		 *  把数据插入到原有excel中。
		 *  由于此数据是实时性的数据，因此不需要计算本月所有同步数据的和，只需要将最后一次同步的数据插入excel表中即可
		 * */
		logger.info(year+"-"+month+"的上传报表【客户经理贷款收息】数据开始转换");
		HashVO monthSum[] = dmo.getHashVoArrayByDS(null, "select t1.b as B,t1.c as C,sum(t1.D) num from " + mainConfig.getStringValue("tablename") + " t1  where t1.year='" + year + "' and month='" + month + "' group by t1.c,t1.b");
		HashMap<String, String> tellerno_deptname = dmo.getHashMapBySQLByDS(null, "select t1.tellerno,t2.shortname from v_sal_personinfo t1 left join pub_corp_dept t2 on t1.deptid = t2.id");
		DeleteSQLBuilder delhisdata = new DeleteSQLBuilder("excel_tab_1");
		delhisdata.setWhereCondition(" year='" + year + "' and month='" + month + "'");
		insertDatasSql.add(delhisdata);
		String time = tbutil.getCurrTime();
		for (int i = 0; i < monthSum.length; i++) {
			InsertSQLBuilder insertsql = new InsertSQLBuilder("excel_tab_1");
			String id = dmo.getSequenceNextValByDS(null, "s_excel_tab_1");
			insertsql.putFieldValue("id", id);
			insertsql.putFieldValue("year", year);
			insertsql.putFieldValue("month", month);
			insertsql.putFieldValue("creattime", time);
			insertsql.putFieldValue("A", tellerno_deptname.get(monthSum[i].getStringValue("C")));//根据柜员号提取所在部门
			insertsql.putFieldValue("B", monthSum[i].getStringValue("B"));
			insertsql.putFieldValue("C", monthSum[i].getStringValue("C"));
			insertsql.putFieldValue("D", monthSum[i].getStringValue("num"));
			insertDatasSql.add(insertsql);
		}
		UpdateSQLBuilder lasteditSql = new UpdateSQLBuilder("EXCEL_TAB");
		lasteditSql.setWhereCondition(" TABLENAME='excel_tab_1'");
		lasteditSql.putFieldValue("UPDATETIME", time);
		insertDatasSql.add(lasteditSql);
		dmo.executeBatchByDSImmediately(null, insertDatasSql,false);
		logger.info(year+"-"+month+"的上传报表【客户经理贷款收息】数据转换成功");
		return "成功";
	}

}
