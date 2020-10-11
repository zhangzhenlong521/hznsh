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

/***
 * 处理表格【全行及支行不良贷款率】
 * @author 张营闯/2014-07-28
 * ***/
public class DataConvert_GYBLDKL implements DataIFCConvertToReport {

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
		//提取同步过后的数据，然后根据数据接口转换菜单配置的对应关系 将同步后的数据转存到报表表格中。
		logger.info("日期为：【"+dataDate+"】的【全行及支行不良贷款率】数据开始同步");
		HashVO[] datas = dmo.getHashVoArrayByDS(null, "select dept.shortname AS A,t1.B,t1.C from " + ifcconfig[0].getStringValue("tablename") + " t1 left join pub_corp_dept dept on t1.A = dept.CODE where t1.logid = '" + syndatalogid + "' order by t1.A");
		String convertDataLogid = dmo.getSequenceNextValByDS(null, "S_SAL_CONVERT_IFCDATA_LOG");
		String year = dataDate.substring(0, 4);
		String month = dataDate.substring(5, 7);
		List insertDatasSql = new ArrayList();
		for (int i = 0; i < datas.length; i++) { //
			InsertSQLBuilder insert = new InsertSQLBuilder(mainConfig.getStringValue("tablename"));
			insert.putFieldValue("logid", convertDataLogid);//记录同步数据的id,作为报表取得最新数据的唯一标识
			insert.putFieldValue("year", year);
			insert.putFieldValue("month", month);
			for (int j = 0; j < configitemvos.length; j++) { //
				String itemcode = configitemvos[j].getStringValue("itemcode");//报表数据中的字段
				String ifcitem = configitemvos[j].getStringValue("ifcitem", ""); //接口表中的字段
				insert.putFieldValue(itemcode, datas[i].getStringValue(ifcitem, ""));//如果接口中不存在报表需要的数据，则此项值设置为空。
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
		logger.info("日期为：【"+dataDate+"】的【全行及支行不良贷款率】数据同步成功");
		insertDatasSql.clear();

		HashMap<String, String> bmap = null;//上年不良贷款率
		HashMap<String, String> emap = null;//上年新增不良贷款率(支行)
		if (year.equals("2014")) {//2014年从系数管理维护中取得数据
			bmap = dmo.getHashMapBySQLByDS(null, "select a,b from sal_data_base_004 ");
			emap = dmo.getHashMapBySQLByDS(null, "select a,c from sal_data_base_004 ");
		} else {
			int lastyear = Integer.getInteger(year) - 1;//上年年份
			bmap = dmo.getHashMapBySQLByDS(null, "select a,b from excel_tab_19 where year ='" + lastyear + "' and month ='12'");//上年数据
			emap = dmo.getHashMapBySQLByDS(null, "select a,e from excel_tab_19 where year ='" + lastyear + "' and month ='12'");
		}

		/*
		 *  把数据插入到原有excel中。
		 * */
		HashVO monthSum[] = dmo.getHashVoArrayByDS(null, "select t1.* from " + mainConfig.getStringValue("tablename") + " t1  where t1.year='" + year + "' and t1.month='" + month + "' and logid ='" + convertDataLogid + "'");
		DeleteSQLBuilder delhisdata = new DeleteSQLBuilder("excel_tab_19");
		delhisdata.setWhereCondition(" year='" + year + "' and month='" + month + "'");
		insertDatasSql.add(delhisdata);
		String time = tbutil.getCurrTime();
		HashVO[] hashVOs = dmo.getHashVoArrayByDS(null, "select sum(m)/sum(i) as '对私',sum(l)/sum(e) as '对公',sum(m+l)/sum(i+e) as '全行' from sal_reportstore_004 where logid =(select logid from sal_reportstore_004 where  year ='" + year + "' and month ='" + month + "' order by logid desc limit 0,1)");
		int lyear = 0;
		String lmonth = null;
		if (month.equals("01")) {
			lmonth = "12";
			lyear = Integer.parseInt(year) - 1;
		} else {
			lyear = Integer.parseInt(year);
			int mon = Integer.parseInt(month) - 1;
			if (mon > 10) {
				lmonth = mon + "";
			} else {
				lmonth = "0" + mon;
			}
		}
		HashVO[] currhashvo = dmo.getHashVoArrayByDS(null, "select sum(m) as m,sum(i) as i,sum(l) as l,sum(e) as e from sal_reportstore_004 where logid =(select logid from sal_reportstore_004 where  year ='" + year + "' and month ='" + month + "' order by logid desc limit 0,1)");
		HashVO[] lastVo = dmo.getHashVoArrayByDS(null, "select sum(m) as m,sum(i) as i,sum(l) as l,sum(e) as e from sal_reportstore_004 where logid =(select logid from sal_reportstore_004 where  year ='" + lyear + "' and month ='" + lmonth + "' order by logid desc limit 0,1)");
		logger.info(year+"-"+month+"的上传报表【全行及支行不良贷款率】数据开始转换");
		for (int i = 0; i < monthSum.length; i++) {
			InsertSQLBuilder insertsql = new InsertSQLBuilder("excel_tab_19");
			String id = dmo.getSequenceNextValByDS(null, "s_excel_tab_19", 100);
			insertsql.putFieldValue("id", id);
			insertsql.putFieldValue("year", year);
			insertsql.putFieldValue("month", month);
			insertsql.putFieldValue("creattime", time);
			insertsql.putFieldValue("A", monthSum[i].getStringValue("A"));//机构
			if (monthSum[i].getStringValue("A").equals("个人业务部")) {//统计全行对私不良率
				insertsql.putFieldValue("C", hashVOs[0].getStringValue("对私"));//本期末不良贷款率
				Double newbad = Double.parseDouble(currhashvo[0].getStringValue("m")) - Double.parseDouble(lastVo[0].getStringValue("m"));
				insertsql.putFieldValue("F", newbad / Double.parseDouble(currhashvo[0].getStringValue("i")));//本期末新增不良贷款率(支行)

			} else if (monthSum[i].getStringValue("A").equals("公司业务部")) {//统计全行对公不良率
				insertsql.putFieldValue("C", hashVOs[0].getStringValue("对公"));//本期末不良贷款率
				Double newbad = Double.parseDouble(currhashvo[0].getStringValue("l")) - Double.parseDouble(lastVo[0].getStringValue("l"));
				insertsql.putFieldValue("F", newbad / Double.parseDouble(currhashvo[0].getStringValue("e")));//本期末新增不良贷款率(支行)

			} else if (monthSum[i].getStringValue("A").equals("信贷管理部")) {//统计全行总体不良率
				insertsql.putFieldValue("C", hashVOs[0].getStringValue("全行"));//本期末不良贷款率
				Double newbad = Double.parseDouble(currhashvo[0].getStringValue("l")) - Double.parseDouble(lastVo[0].getStringValue("l")) + Double.parseDouble(currhashvo[0].getStringValue("m")) - Double.parseDouble(lastVo[0].getStringValue("m"));
				insertsql.putFieldValue("F", newbad / Double.parseDouble(currhashvo[0].getStringValue("e")) + Double.parseDouble(currhashvo[0].getStringValue("i")));//本期末新增不良贷款率(支行)

			} else {
				insertsql.putFieldValue("C", monthSum[i].getStringValue("C"));//本期末不良贷款率
				insertsql.putFieldValue("F", monthSum[i].getStringValue("F"));//本期末新增不良贷款率(支行)
			}

			if (bmap.containsKey(monthSum[i].getStringValue("A"))) {//判断系数管理维护中是否包含该机构的数据
				insertsql.putFieldValue("B", bmap.get(monthSum[i].getStringValue("A")));//上年不良贷款率
				insertsql.putFieldValue("D", Double.parseDouble(monthSum[i].getStringValue("C")) - Double.parseDouble(bmap.get(monthSum[i].getStringValue("A"))));//不良贷款率反弹(本期-上年)
			} else {
				insertsql.putFieldValue("B", monthSum[i].getStringValue("B"));//上年不良贷款率
				insertsql.putFieldValue("D", monthSum[i].getDoubleValue("C", 0.0) - monthSum[i].getDoubleValue("B", 0.0));//不良贷款率反弹(本期-上年)
			}
			if (emap.containsKey(monthSum[i].getStringValue("A"))) {
				insertsql.putFieldValue("E", emap.get(monthSum[i].getStringValue("A")));//上年新增不良贷款率(支行)
				insertsql.putFieldValue("G", monthSum[i].getDoubleValue("F", 0.0) - Double.parseDouble(emap.get(monthSum[i].getStringValue("A"))));//新增不良贷款率反弹 (本期-上年)
			} else {
				insertsql.putFieldValue("E", monthSum[i].getStringValue("E"));//上年新增不良贷款率(支行)
				insertsql.putFieldValue("G", monthSum[i].getDoubleValue("F", 0.0) - monthSum[i].getDoubleValue("E", 0.0));//新增不良贷款率反弹 (本期-上年)
			}
			insertDatasSql.add(insertsql);
		}
		UpdateSQLBuilder lasteditSql = new UpdateSQLBuilder("EXCEL_TAB");
		lasteditSql.setWhereCondition(" TABLENAME='excel_tab_19'");
		lasteditSql.putFieldValue("UPDATETIME", time);
		insertDatasSql.add(lasteditSql);
		dmo.executeBatchByDSImmediately(null, insertDatasSql, false);
		logger.info(year+"-"+month+"的上传报表【全行及支行不良贷款率】数据转换成功");
		return "成功";
	}

}
