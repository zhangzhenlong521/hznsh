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
 * 柜员业务笔数 自定义转换
 * 最后会按照增量或者全量，放到excel中。
 * @author haoming
 * create by 2014-7-15
 */
public class DataConvert_GYYWBS implements DataIFCConvertToReport {
	private TBUtil tbutil = new TBUtil();
	protected String isconvert = tbutil.getSysOptionStringValue("柜员业务笔数是否折算", "否");

	public String convert(HashVO mainConfig, String jobid, String dataDate) throws Exception {
		CommDMO dmo = new CommDMO();
		HashVO configitemvos[] = dmo.getHashVoArrayByDS(null, "select * from sal_convert_ifcdata_item_conf where parentid = " + mainConfig.getStringValue("id") + " order by seq");

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
		String zstable = dmo.getStringValueByDS(null, "select tablename from sal_data_base_tablebase where basename='柜员业务笔数折算系数'"); //
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append("select dept.shortname,t1.name,t1.code,t1.tellerno");
		if (!tbutil.isEmpty(zstable) && isconvert.equals("是")) { //有折算
			sqlsb
					.append(",sum(t2.E*t3.c)  as num from v_sal_personinfo t1 left join pub_corp_dept dept on t1.deptid = dept.id left join " + ifcconfig[0].getStringValue("tablename") + " t2 on instr(t1.tellerno,t2.B)>0 left join " + zstable + " t3 on t2.c = t3.a  where t2.logid = '" + syndatalogid
							+ "' and t1.stationkind like '%柜员%' group by t1.code,t1.tellerno,t1.name,dept.shortname order by num");
		} else {
			sqlsb.append(",sum(t2.E)  as num from v_sal_personinfo t1 left join pub_corp_dept dept on t1.deptid = dept.id left join " + ifcconfig[0].getStringValue("tablename") + " t2 on instr(t1.tellerno,t2.B)>0 where t2.logid = '" + syndatalogid + "' and t1.stationkind like '%柜员%' group by t1.code,t1.tellerno,t1.name,dept.shortname order by num");
		}

		logger.info("日期为：【" + dataDate + "】的【柜员业务笔数】数据开始同步");
		HashVO[] datas = dmo.getHashVoArrayByDS(null, sqlsb.toString());
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
		logger.info("日期为：【" + dataDate + "】的【柜员业务笔数】数据同步成功");
		insertDatasSql.clear();
		/*
		 *  把数据插入到原有excel中。
		 *  考虑到数据可能会补充执行，需要对数据汇总时进行数据日期判断。
		 * */
		logger.info(year + "-" + month + "的上传报表【柜员业务笔数】数据开始转换");
		HashVO monthSum[] = dmo.getHashVoArrayByDS(null, "select t1.b as B,t1.D as D,sum(t1.E) as num from " + mainConfig.getStringValue("tablename") + " t1 left join sal_convert_ifcdata_log t2 on t1.logid = t2.id  where t1.year='" + year + "' and t1.month='" + month + "' and t2.datadate<='" + dataDate + "' group by t1.c,t1.b");
		HashMap<String, String> tellerno_deptname = dmo.getHashMapBySQLByDS(null, "select t1.tellerno,t2.shortname from v_sal_personinfo t1 left join pub_corp_dept t2 on t1.deptid = t2.id");
		DeleteSQLBuilder delhisdata = new DeleteSQLBuilder("excel_tab_2");
		delhisdata.setWhereCondition(" year='" + year + "' and month='" + month + "'");
		insertDatasSql.add(delhisdata);
		String time = tbutil.getCurrTime();
		for (int i = 0; i < monthSum.length; i++) {
			InsertSQLBuilder insertsql = new InsertSQLBuilder("excel_tab_2");
			String id = dmo.getSequenceNextValByDS(null, "s_excel_tab_2");
			insertsql.putFieldValue("id", id);
			insertsql.putFieldValue("year", year);
			insertsql.putFieldValue("month", month);
			insertsql.putFieldValue("creattime", time);
			insertsql.putFieldValue("A", tellerno_deptname.get(monthSum[i].getStringValue("D")));
			insertsql.putFieldValue("B", monthSum[i].getStringValue("B"));
			insertsql.putFieldValue("C", monthSum[i].getStringValue("D"));
			insertsql.putFieldValue("D", monthSum[i].getStringValue("num"));
			insertDatasSql.add(insertsql);
		}
		UpdateSQLBuilder lasteditSql = new UpdateSQLBuilder("EXCEL_TAB");
		lasteditSql.setWhereCondition(" TABLENAME='excel_tab_2'");
		lasteditSql.putFieldValue("UPDATETIME", time);
		insertDatasSql.add(lasteditSql);
		dmo.executeBatchByDSImmediately(null, insertDatasSql, false);
		logger.info(year + "-" + month + "的上传报表【柜员业务笔数】数据转换成功");
		return "成功";
	}

}
