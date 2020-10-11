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
 * ����ͻ��������ͳ�Ʊ��ת��
 * **/
public class DataConvert_GYDKTJ implements DataIFCConvertToReport {
	private CommDMO dmo = new CommDMO();
	private TBUtil tbutil = new TBUtil();
	protected String isconvert = tbutil.getSysOptionStringValue("ָ�������Ƿ�����", null);

	public String convert(HashVO mainConfig, String jobid, String dataDate) throws Exception {
		HashVO configitemvos[] = dmo.getHashVoArrayByDS(null, "select * from sal_convert_ifcdata_item_conf where parentid = " + mainConfig.getStringValue("id") + " order by seq");
		String sourceifc = mainConfig.getStringValue("sourceifc"); //�õ��ӿ���������ID
		HashVO[] ifcconfig = dmo.getHashVoArrayByDS(null, "select * from sal_data_interface_main where id =" + sourceifc);//���ñ�
		if (ifcconfig == null || ifcconfig.length == 0) {
			throw new Exception(mainConfig.getStringValue("reportname") + "�ӿ�����ת�����õ����ݽӿ���ԴΪ��.");
		}
		String syndatalogid = null;
		//
		if (tbutil.isEmpty(jobid) || "-1".equals(jobid)) { //���Զ�����ִ�С�
			syndatalogid = dmo.getStringValueByDS(null, "select id from sal_data_interface_log where parentid = " + ifcconfig[0].getStringValue("id") + " and exectype='" + DataInterfaceDMO.Syn_Log_Exec_Type_TBSJ + "' and state='" + DataInterfaceDMO.Syn_Log_Exec_State_Success + "' and datadate='" + dataDate + "' order by exectime desc");
			if (tbutil.isEmpty(syndatalogid)) { //���û���ҵ�ͬ����־��ֱ�ӱ���
				throw new Exception("[" + dataDate + "]����û��û��ͬ����,����.");
			}
		} else {
			syndatalogid = dmo.getStringValueByDS(null, "select id from sal_data_interface_log where parentid = " + ifcconfig[0].getStringValue("id") + " and exectype='" + DataInterfaceDMO.Syn_Log_Exec_Type_TBSJ + "' and state='" + DataInterfaceDMO.Syn_Log_Exec_State_Success + "' and jobid=" + jobid);
		}
		logger.info("����Ϊ����" + dataDate + "���ġ��ͻ��������ͳ�Ʊ����ݿ�ʼͬ��");
		HashVO[] datas = dmo.getHashVoArrayByDS(null, "select dept.shortname as a ,t2.name as name ,t2.tellerno as b,sum(t1.c) as c,sum(t1.d) as d from " + ifcconfig[0].getStringValue("tablename") + " t1 left join pub_corp_dept dept on t1.A = dept.code left join v_sal_personinfo  t2 on instr(t2.tellerno,t1.B)>0 where t1.logid = '" + syndatalogid
				+ "' group by t2.tellerno,t2.name,dept.shortname order by t1.A");
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
				String itemcode = configitemvos[j].getStringValue("itemcode");//���������е��ֶ�
				String ifcitem = configitemvos[j].getStringValue("ifcitem", ""); //�ӿڱ��е��ֶ�
				if (configitemvos[j].getStringValue("itemname").equals("����")) {
					insert.putFieldValue(itemcode, datas[i].getStringValue("name"));
				} else {
					insert.putFieldValue(itemcode, datas[i].getStringValue(ifcitem, ""));
				}
			}
			insertDatasSql.add(insert);
		}
		InsertSQLBuilder logsql = new DataInterfaceDMO().getConvertLog(convertDataLogid, mainConfig.getStringValue("id"), mainConfig.getStringValue("reportname"), jobid, dataDate, DataInterfaceDMO.Syn_Log_Convert_Type_SJZH, DataInterfaceDMO.Syn_Log_Exec_State_Success, "����ת���ɹ�������" + insertDatasSql.size() + "��");
		insertDatasSql.add(logsql);
		UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder("sal_convert_ifcdata");
		updateMainTable.setWhereCondition(" id=" + mainConfig.getStringValue("id"));
		updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
		insertDatasSql.add(updateMainTable);
		dmo.executeBatchByDSImmediately(null, insertDatasSql, false);
		logger.info("����Ϊ����" + dataDate + "���ġ��ͻ��������ͳ�Ʊ�����ͬ���ɹ�");
		insertDatasSql.clear();
		/*
		 *  �����ݲ��뵽ԭ��excel�С�
		 *  �˴�������ʵʱ�Եģ����ֻ��ȡ�����һ��ͬ�������ݼ���
		 * */
		logger.info(year + "-" + month + "���ϴ������ͻ��������ͳ�Ʊ����ݿ�ʼת��");
		HashVO monthSum[] = dmo.getHashVoArrayByDS(null, "select t1.b as B,t1.c as C,sum(t1.E)/10000 num1,sum(t1.G)/10000 num3 from " + mainConfig.getStringValue("tablename") + " t1  where t1.year='" + year + "' and month='" + month + "' and logid ='" + convertDataLogid + "' group by t1.c,t1.b");
		HashMap<String, String> tellerno_deptname = dmo.getHashMapBySQLByDS(null, "select t1.tellerno,t2.shortname from v_sal_personinfo t1 left join pub_corp_dept t2 on t1.deptid = t2.id");
		DeleteSQLBuilder delhisdata = new DeleteSQLBuilder("excel_tab_5");
		delhisdata.setWhereCondition(" year='" + year + "' and month='" + month + "'");
		insertDatasSql.add(delhisdata);
		String time = tbutil.getCurrTime();
		HashMap dmap = null;//����������
		HashMap fmap = null;//��������������
		if (year.equals("2014")) {
			dmap = dmo.getHashMapBySQLByDS(null, "select t2.c,t1.c from sal_data_base_002 t1 left join sal_reportstore_002 t2 on instr(t2.c,t1.B)>0 where t2.c>0");
			fmap = dmo.getHashMapBySQLByDS(null, "select t2.c,t1.d from sal_data_base_002 t1 left join sal_reportstore_002 t2 on instr(t2.c,t1.B)>0 where t2.c>0");
		} else {
			int lastyear = Integer.getInteger(year) - 1;
			dmap = dmo.getHashMapBySQLByDS(null, "select c,E from excel_tab_5 where year ='" + lastyear + "' and month ='12'");
			fmap = dmo.getHashMapBySQLByDS(null, "select c,G from excel_tab_5 where year ='" + lastyear + "' and month ='12'");
		}
		HashMap<String, String> convetMap = null;
		if (isconvert != null && isconvert.equals("��")) {//��������Ƿ��۰�
			convetMap = dmo.getHashMapBySQLByDS(null, "select tellerno,name from sal_personinfo where code in (select b from sal_data_base_006)");
		}

		for (int i = 0; i < monthSum.length; i++) {
			InsertSQLBuilder insertsql = new InsertSQLBuilder("excel_tab_5");
			String id = dmo.getSequenceNextValByDS(null, "s_excel_tab_5", 100);
			insertsql.putFieldValue("id", id);
			insertsql.putFieldValue("year", year);
			insertsql.putFieldValue("month", month);
			insertsql.putFieldValue("creattime", time);
			insertsql.putFieldValue("A", tellerno_deptname.get(monthSum[i].getStringValue("C")));
			insertsql.putFieldValue("B", monthSum[i].getStringValue("B"));
			insertsql.putFieldValue("C", monthSum[i].getStringValue("C"));
			insertsql.putFieldValue("D", (String) dmap.get(monthSum[i].getStringValue("C")));
			String num = monthSum[i].getStringValue("num1");
			if (convetMap != null && convetMap.containsKey(monthSum[i].getStringValue("C"))) {
				num = Double.parseDouble(num) / 2 + "";
			}
			insertsql.putFieldValue("E", num);
			insertsql.putFieldValue("F", (String) fmap.get(monthSum[i].getStringValue("C")));
			insertsql.putFieldValue("G", monthSum[i].getStringValue("num3"));
			insertDatasSql.add(insertsql);
		}
		UpdateSQLBuilder lasteditSql = new UpdateSQLBuilder("EXCEL_TAB");
		lasteditSql.setWhereCondition(" TABLENAME='excel_tab_5'");
		lasteditSql.putFieldValue("UPDATETIME", time);
		insertDatasSql.add(lasteditSql);
		dmo.executeBatchByDSImmediately(null, insertDatasSql, false);
		logger.info(year + "-" + month + "���ϴ������ͻ��������ͳ�Ʊ�����ת���ɹ�");
		return "�ɹ�";
	}

}
