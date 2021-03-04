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
 * ������ȫ�м�֧�в��������ʡ�
 * @author ��Ӫ��/2014-07-28
 * ***/
public class DataConvert_GYBLDKL implements DataIFCConvertToReport {

	public String convert(HashVO mainConfig, String jobid, String dataDate) throws Exception {
		CommDMO dmo = new CommDMO();
		HashVO configitemvos[] = dmo.getHashVoArrayByDS(null, "select * from sal_convert_ifcdata_item_conf where parentid = " + mainConfig.getStringValue("id") + " order by seq");
		TBUtil tbutil = new TBUtil();
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
		//��ȡͬ����������ݣ�Ȼ��������ݽӿ�ת���˵����õĶ�Ӧ��ϵ ��ͬ���������ת�浽�������С�
		logger.info("����Ϊ����"+dataDate+"���ġ�ȫ�м�֧�в��������ʡ����ݿ�ʼͬ��");
		HashVO[] datas = dmo.getHashVoArrayByDS(null, "select dept.shortname AS A,t1.B,t1.C from " + ifcconfig[0].getStringValue("tablename") + " t1 left join pub_corp_dept dept on t1.A = dept.CODE where t1.logid = '" + syndatalogid + "' order by t1.A");
		String convertDataLogid = dmo.getSequenceNextValByDS(null, "S_SAL_CONVERT_IFCDATA_LOG");
		String year = dataDate.substring(0, 4);
		String month = dataDate.substring(5, 7);
		List insertDatasSql = new ArrayList();
		for (int i = 0; i < datas.length; i++) { //
			InsertSQLBuilder insert = new InsertSQLBuilder(mainConfig.getStringValue("tablename"));
			insert.putFieldValue("logid", convertDataLogid);//��¼ͬ�����ݵ�id,��Ϊ����ȡ���������ݵ�Ψһ��ʶ
			insert.putFieldValue("year", year);
			insert.putFieldValue("month", month);
			for (int j = 0; j < configitemvos.length; j++) { //
				String itemcode = configitemvos[j].getStringValue("itemcode");//���������е��ֶ�
				String ifcitem = configitemvos[j].getStringValue("ifcitem", ""); //�ӿڱ��е��ֶ�
				insert.putFieldValue(itemcode, datas[i].getStringValue(ifcitem, ""));//����ӿ��в����ڱ�����Ҫ�����ݣ������ֵ����Ϊ�ա�
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
		logger.info("����Ϊ����"+dataDate+"���ġ�ȫ�м�֧�в��������ʡ�����ͬ���ɹ�");
		insertDatasSql.clear();

		HashMap<String, String> bmap = null;//���겻��������
		HashMap<String, String> emap = null;//������������������(֧��)
		if (year.equals("2014")) {//2014���ϵ������ά����ȡ������
			bmap = dmo.getHashMapBySQLByDS(null, "select a,b from sal_data_base_004 ");
			emap = dmo.getHashMapBySQLByDS(null, "select a,c from sal_data_base_004 ");
		} else {
			int lastyear = Integer.getInteger(year) - 1;//�������
			bmap = dmo.getHashMapBySQLByDS(null, "select a,b from excel_tab_19 where year ='" + lastyear + "' and month ='12'");//��������
			emap = dmo.getHashMapBySQLByDS(null, "select a,e from excel_tab_19 where year ='" + lastyear + "' and month ='12'");
		}

		/*
		 *  �����ݲ��뵽ԭ��excel�С�
		 * */
		HashVO monthSum[] = dmo.getHashVoArrayByDS(null, "select t1.* from " + mainConfig.getStringValue("tablename") + " t1  where t1.year='" + year + "' and t1.month='" + month + "' and logid ='" + convertDataLogid + "'");
		DeleteSQLBuilder delhisdata = new DeleteSQLBuilder("excel_tab_19");
		delhisdata.setWhereCondition(" year='" + year + "' and month='" + month + "'");
		insertDatasSql.add(delhisdata);
		String time = tbutil.getCurrTime();
		HashVO[] hashVOs = dmo.getHashVoArrayByDS(null, "select sum(m)/sum(i) as '��˽',sum(l)/sum(e) as '�Թ�',sum(m+l)/sum(i+e) as 'ȫ��' from sal_reportstore_004 where logid =(select logid from sal_reportstore_004 where  year ='" + year + "' and month ='" + month + "' order by logid desc limit 0,1)");
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
		logger.info(year+"-"+month+"���ϴ�����ȫ�м�֧�в��������ʡ����ݿ�ʼת��");
		for (int i = 0; i < monthSum.length; i++) {
			InsertSQLBuilder insertsql = new InsertSQLBuilder("excel_tab_19");
			String id = dmo.getSequenceNextValByDS(null, "s_excel_tab_19", 100);
			insertsql.putFieldValue("id", id);
			insertsql.putFieldValue("year", year);
			insertsql.putFieldValue("month", month);
			insertsql.putFieldValue("creattime", time);
			insertsql.putFieldValue("A", monthSum[i].getStringValue("A"));//����
			if (monthSum[i].getStringValue("A").equals("����ҵ��")) {//ͳ��ȫ�ж�˽������
				insertsql.putFieldValue("C", hashVOs[0].getStringValue("��˽"));//����ĩ����������
				Double newbad = Double.parseDouble(currhashvo[0].getStringValue("m")) - Double.parseDouble(lastVo[0].getStringValue("m"));
				insertsql.putFieldValue("F", newbad / Double.parseDouble(currhashvo[0].getStringValue("i")));//����ĩ��������������(֧��)

			} else if (monthSum[i].getStringValue("A").equals("��˾ҵ��")) {//ͳ��ȫ�жԹ�������
				insertsql.putFieldValue("C", hashVOs[0].getStringValue("�Թ�"));//����ĩ����������
				Double newbad = Double.parseDouble(currhashvo[0].getStringValue("l")) - Double.parseDouble(lastVo[0].getStringValue("l"));
				insertsql.putFieldValue("F", newbad / Double.parseDouble(currhashvo[0].getStringValue("e")));//����ĩ��������������(֧��)

			} else if (monthSum[i].getStringValue("A").equals("�Ŵ�����")) {//ͳ��ȫ�����岻����
				insertsql.putFieldValue("C", hashVOs[0].getStringValue("ȫ��"));//����ĩ����������
				Double newbad = Double.parseDouble(currhashvo[0].getStringValue("l")) - Double.parseDouble(lastVo[0].getStringValue("l")) + Double.parseDouble(currhashvo[0].getStringValue("m")) - Double.parseDouble(lastVo[0].getStringValue("m"));
				insertsql.putFieldValue("F", newbad / Double.parseDouble(currhashvo[0].getStringValue("e")) + Double.parseDouble(currhashvo[0].getStringValue("i")));//����ĩ��������������(֧��)

			} else {
				insertsql.putFieldValue("C", monthSum[i].getStringValue("C"));//����ĩ����������
				insertsql.putFieldValue("F", monthSum[i].getStringValue("F"));//����ĩ��������������(֧��)
			}

			if (bmap.containsKey(monthSum[i].getStringValue("A"))) {//�ж�ϵ������ά�����Ƿ�����û���������
				insertsql.putFieldValue("B", bmap.get(monthSum[i].getStringValue("A")));//���겻��������
				insertsql.putFieldValue("D", Double.parseDouble(monthSum[i].getStringValue("C")) - Double.parseDouble(bmap.get(monthSum[i].getStringValue("A"))));//���������ʷ���(����-����)
			} else {
				insertsql.putFieldValue("B", monthSum[i].getStringValue("B"));//���겻��������
				insertsql.putFieldValue("D", monthSum[i].getDoubleValue("C", 0.0) - monthSum[i].getDoubleValue("B", 0.0));//���������ʷ���(����-����)
			}
			if (emap.containsKey(monthSum[i].getStringValue("A"))) {
				insertsql.putFieldValue("E", emap.get(monthSum[i].getStringValue("A")));//������������������(֧��)
				insertsql.putFieldValue("G", monthSum[i].getDoubleValue("F", 0.0) - Double.parseDouble(emap.get(monthSum[i].getStringValue("A"))));//�������������ʷ��� (����-����)
			} else {
				insertsql.putFieldValue("E", monthSum[i].getStringValue("E"));//������������������(֧��)
				insertsql.putFieldValue("G", monthSum[i].getDoubleValue("F", 0.0) - monthSum[i].getDoubleValue("E", 0.0));//�������������ʷ��� (����-����)
			}
			insertDatasSql.add(insertsql);
		}
		UpdateSQLBuilder lasteditSql = new UpdateSQLBuilder("EXCEL_TAB");
		lasteditSql.setWhereCondition(" TABLENAME='excel_tab_19'");
		lasteditSql.putFieldValue("UPDATETIME", time);
		insertDatasSql.add(lasteditSql);
		dmo.executeBatchByDSImmediately(null, insertDatasSql, false);
		logger.info(year+"-"+month+"���ϴ�����ȫ�м�֧�в��������ʡ�����ת���ɹ�");
		return "�ɹ�";
	}

}
