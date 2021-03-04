package cn.com.pushworld.salary.bs.dinterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.salary.to.SalaryTBUtil;

/**
 * ���ݽӿڷ�������ͨ�÷�����
 * 
 * @author haoming create by 2014-7-2
 */
public class DataInterfaceDMO extends AbstractDMO {
	private TBUtil tbutil = new TBUtil();
	private CommDMO dmo = new CommDMO();
	private SalaryTBUtil stbutil = new SalaryTBUtil();

	public static final String Syn_Log_Exec_Type_TBSJ = "ͬ������"; // ����ͬ��;ɾ������;������;�޸ı�;
	public static final String Syn_Log_Exec_Type_SCSJ = "ɾ������";
	public static final String Syn_Log_Exec_Type_CJB = "������";
	public static final String Syn_Log_Exec_Type_XGB = "�޸ı�";
	public static final String Syn_Log_Exec_Type_Other = "����"; // �����ʶ��������Ҫ��һ��ϵͳ�����õı��������·����ļ��Աȱ������Ǹ��࣬�Ǹ��١�

	public static final String Syn_Log_Convert_Type_SJZH = "����ת��"; // ����ת��;��������;ɾ������;������;
	public static final String Syn_Log_Convert_Type_BFSJ = "��������";
	public static final String Syn_Log_Convert_Type_SCSJ = "ɾ������";
	public static final String Syn_Log_Convert_Type_CJB = "������";

	public static final String Syn_Log_Exec_State_Success = "�ɹ�";
	public static final String Syn_Log_Exec_State_Fail = "ʧ��";

	protected String ftppath = tbutil
			.getSysOptionStringValue("���ݽӿ�FTP·��", null);
	protected String datacode = tbutil.getSysOptionStringValue("���ݽӿڱ����ʽ",
			"GBK");
	protected boolean flg = tbutil.getSysOptionBooleanValue("�ӿ������Ƿ����deptid",
			false);// zzl[2017/6/2]
	
	protected boolean soleflg = tbutil.getSysOptionBooleanValue("���ݽӿ������Ƿ����Ψһ�ֶ�ID",
			false);// zzl[2018-10-25]
	
	protected String splitStr = tbutil.getSysOptionStringValue("���ݽӿڵ������ݵķָ�����",
			";").trim();// zzl[2018-10-25]

	/*
	 * ���ݽӿ����ýӿڣ��ֹ�ִ�д�����
	 */
	public void createTableByConfig(String _dataIFCmainID) throws Exception {
		createTableByConfig(_dataIFCmainID, "", "");
	}

	/*
	 * �������ݱ������
	 */
	private void createTableByConfig(String _dataIFCmainID, String _jobid,
			String _datadate) throws Exception {
		HashVO data_ifc_mainVOs[] = dmo.getHashVoArrayByDS(null,
				"select * from sal_data_interface_main where id = "
						+ _dataIFCmainID); // �ҵ��ӿ���������
		if (data_ifc_mainVOs.length == 0) {
			throw new Exception("����Ľӿ�ͬ��������������Ϊ" + _dataIFCmainID
					+ "��������,����!");
		}
		HashVO[] data_ifc_item_config = dmo.getHashVoArrayByDS(null,
				"select * from sal_data_interface_item_conf where parentid = "
						+ _dataIFCmainID + " order by seq");// �ӿ��ֶ�����

		if (data_ifc_item_config.length == 0) {
			throw new Exception("�ӿ�û�����ñ�ṹ.");
		}
		String str_dbtype = ServerEnvironment.getDefaultDataSourceType(); // ����Դ����!�ؼ�
		String str_tname = data_ifc_mainVOs[0].getStringValue("tablename"); // ����
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("create table " + str_tname + "("); //
		sb_sql.append(" logid decimal(22),");
		if (flg) {
			sb_sql.append(" deptid decimal(22),");
		}
		if(soleflg){
			sb_sql.append(" id decimal(22) primary key,");//zzl[2018-11-23] ���������ή�Ͳ�����ٶ�
		}
		List indexItem = new ArrayList();
		StringBuffer msg = new StringBuffer();
		for (int i = 0; i < data_ifc_item_config.length; i++) {
			HashVO itemvo = data_ifc_item_config[i];
			String str_colName = stbutil.convertIntColToEn(i + 1,false); //
			String str_colType = itemvo.getStringValue("itemtype"); // ����,����varchar,decimal
			String str_colLength = itemvo.getStringValue("itemwidth"); //
			String itemChinaName = itemvo.getStringValue("itemname");
			String isindex = itemvo.getStringValue("ifindex");
			sb_sql.append(str_colName + " "
					+ convertRealColType(str_colType, str_dbtype) + "("
					+ str_colLength + ") "); //
			if (str_dbtype.equalsIgnoreCase("MYSQL")
					&& !tbutil.isEmpty(itemChinaName)) {
				sb_sql.append(" comment '" + itemChinaName + "'");
			}
			if (i != data_ifc_item_config.length - 1) { // ���û�ж�������,���������һ��
				sb_sql.append(",\r\n"); // //
			}
			if ("Y".equals(isindex)) {
				indexItem.add(str_colName);
			}
		}
		sb_sql.append(")"); //
		if (str_dbtype.equalsIgnoreCase("MYSQL")) {
			sb_sql.append(" DEFAULT CHARSET = GBK"); //
			sb_sql.append(" comment '"
					+ data_ifc_mainVOs[0].getStringValue("iname") + "'"); //
		}
		// if (str_dbtype.equalsIgnoreCase("DB2")) {
		// sb_sql.append(" IN PUSHSPACE"); //
		// }

		String str_createsql = sb_sql.toString(); //
		if (str_dbtype.equalsIgnoreCase("DB2")) {
			str_createsql = str_createsql.toUpperCase(); //
		}
		dmo.executeUpdateByDS(null, str_createsql); //
		
		msg.append(str_createsql);
		// ��������!!!
		if (indexItem != null && indexItem.size() > 0) { // ����û��������
			for (int j = 0; j < indexItem.size(); j++) {
				String str_indexCols = (String) indexItem.get(j); //
				String str_indexName = "PK_" + str_tname.toUpperCase() + "_"
						+ j;
				String str_indexsql = "create index " + str_indexName + " on "
						+ str_tname + "(" + str_indexCols + ")"; //
				str_indexsql = str_indexsql.trim(); //
				if (str_dbtype.equalsIgnoreCase("DB2")) {
					str_indexsql = str_indexsql.toUpperCase(); // ת��д!!
				}
				dmo.executeUpdateByDS(null, str_indexsql); //
				msg.append(";\r\n" + str_indexsql);
			}
		}
		String logid = dmo.getSequenceNextValByDS(null,
				"S_SAL_DATA_INTERFACE_LOG");
		InsertSQLBuilder loginsertsql = getSynLog(logid, _dataIFCmainID,
				_jobid, "", data_ifc_mainVOs[0].getStringValue("iname"), "",
				Syn_Log_Exec_Type_CJB, Syn_Log_Exec_State_Success, "��"
						+ str_tname + "�����ɹ�;\r\nִ�����" + msg.toString());
		UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
				"sal_data_interface_main");
		updateMainTable.setWhereCondition(" id=" + _dataIFCmainID);
		updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
		dmo.executeBatchByDSImmediately(null, new String[] {
				loginsertsql.getSQL(), updateMainTable.getSQL() }, false);
	}

	/*
	 * �����ӿ�ͬ����־
	 */
	private InsertSQLBuilder getSynLog(String _id, String _parentid,
			String _jobid, String _dataDate, String _name, String _filepath,
			String _exectype, String _state, String descr) throws Exception {
		InsertSQLBuilder loginsertsql = new InsertSQLBuilder(
				"sal_data_interface_log");
		if (tbutil.isEmpty(_id)) {
			_id = dmo.getSequenceNextValByDS(null, "S_SAL_DATA_INTERFACE_LOG");
		}
		loginsertsql.putFieldValue("id", _id);
		loginsertsql.putFieldValue("parentid", _parentid);
		loginsertsql.putFieldValue("jobid", _jobid);
		loginsertsql.putFieldValue("datadate", _dataDate);
		loginsertsql.putFieldValue("exectime", tbutil.getCurrTime());
		loginsertsql.putFieldValue("iname", _name); // �ӿ�����
		loginsertsql.putFieldValue("filepath", _filepath);// �ļ�·��
		loginsertsql.putFieldValue("exectype", _exectype);
		loginsertsql.putFieldValue("state", _state);
		if (descr != null && descr.length() > 1950) {
			descr = descr.substring(0, 1950) + "...";
		}
		loginsertsql.putFieldValue("descr", descr);
		return loginsertsql;
	}

	/*
	 * �������ϴ�Ŀ¼��ȡ���ݲ����뵽���ݿ� ��¼��ȡ��־ _currFolder �������·�ƽ̨�������ļ���
	 * zzl[ͬ���ĺ�������   ����Ҫ���ŵĵ������ݷ�����̨�������ָ��    ���м�ֵϵͳֻ��Ҫ��������ֵ������]
	 */
	public void readDataFromFile(String _dataIFCmainID, String _dataDate,
			String _jobid) throws Exception {
		HashVO data_ifc_mainVOs[] = dmo.getHashVoArrayByDS(null,
				"select * from sal_data_interface_main where id = "
						+ _dataIFCmainID); //
		if (data_ifc_mainVOs.length == 0) {
			throw new Exception("����Ľӿ�ͬ��������������Ϊ" + _dataIFCmainID
					+ "��������,����!");
		}

		HashVO[] data_ifc_item_config = dmo.getHashVoArrayByDS(null,
				"select * from sal_data_interface_item_conf where parentid = "
						+ _dataIFCmainID);
				syn_data_from_file(data_ifc_mainVOs[0], data_ifc_item_config,
					_dataDate, _jobid);


	}

	/*
	 * ͬ�����ݺ����߼�
	 * zzl  ��������ID  ����DEPTID
	 */
	private void syn_data_from_file(HashVO _mainVO, HashVO[] _childItemVOs,
			String _datadate, String _jobid) throws Exception {
		if (tbutil.isEmpty(ftppath)) {
			throw new Exception("ϵͳ����[���ݽӿ�FTP·��]Ϊ�գ�������!");
		}
		String _folder = _datadate.replace("-", ""); // ����������ڣ���ȡΪͬ��Ŀ¼
		String parentFolder = _folder.substring(0, 6);
		String folderAbsolutePath = tbutil.replaceAll(ftppath, "\\",
				File.separator)
				+ File.separator
				+ parentFolder
				+ File.separator
				+ _folder
				+ File.separator; //
		String fileAbsolutePath = folderAbsolutePath+ _mainVO.getStringValue("filename");
		if (flg) {
			// ������û����ʷ�����еĻ�����ɾ������Ȼ����ִ��.
			HashVO[] old_his = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_log where filepath like '%"
							+ _folder + "%' and exectype='"
							+ Syn_Log_Exec_Type_TBSJ + "' and parentid="
							+ _mainVO.getStringValue("id")
							+ " order by exectime desc"); // ������ͬ���ļ�¼
			if (old_his != null && old_his.length > 0) {
				List list = new ArrayList();
				StringBuffer msg = new StringBuffer();
				for (int i = 0; i < old_his.length; i++) {
					HashVO loghisvo = old_his[i];
					msg.append(loghisvo.getStringValue("exectime") + ",");
					String deloldsql = "delete from "
							+ _mainVO.getStringValue("tablename")
							+ " where logid = " + loghisvo.getStringValue("id");
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+deloldsql);
					list.add(deloldsql);
				}
				String newlogid = dmo.getSequenceNextValByDS(null,
						"S_SAL_DATA_INTERFACE_LOG");
				InsertSQLBuilder dellogsql = getSynLog(newlogid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_SCSJ,
						Syn_Log_Exec_State_Success, "ϵͳ���ָýӿ��Ѿ���["
								+ msg.substring(0, msg.length() - 1)
								+ "]ͬ����,��������ʷ����.");
				list.add(dellogsql);
				dmo.executeBatchByDSImmediately(null, list);
			}
			ArrayList a = getFiles(folderAbsolutePath);
			if(a.size()<=0){
				throw new Exception("�ļ�������,����û�ϴ�");
			}
			for (int f = 0; f < a.size(); f++) {
				File filesum = (File) a.get(f);
				fileAbsolutePath=folderAbsolutePath+"/"+filesum.getName()+"/"+ _mainVO.getStringValue("filename");
		File file = new File(fileAbsolutePath);
//		if (!file.exists()) {
//			throw new Exception(fileAbsolutePath + "�ļ������ڣ�����Ҫ��δ�ϴ�����˫���ӿ����ò�һ��.");
//		}
		String str = tbutil.readFromInputStreamToStr(new FileInputStream(file));
		String[] allline = str.split("\n");
//		for (int i = 0; i < allline.length; i++) {
//			String values[] = allline[i].split(splitStr);
//			for (int j = 0; j < values.length; j++) {
//				System.out.println(values[j]);
//			}
//		}
		InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), datacode);
		BufferedReader filereader = new BufferedReader(read);
		String strcontent = null;

		String tableName = _mainVO.getStringValue("tablename");
		// �����Ƿ����,��������жϽṹ�Ƿ�仯
		try {
			dmo.getStringValueByDS(null, "select * from " + tableName
					+ " where 1=2");
			String[][] different = compareDBTOConfig(_mainVO, _childItemVOs);// ����ṹ��û�б仯��һ���ǲ���ġ�
			if (different != null && different.length > 0) {
				List sql = new ArrayList();
				StringBuffer descr = new StringBuffer();
				for (int i = 0; i < different.length; i++) {
					sql.add(different[i][3]);
					descr.append(different[i][3] + "\r\n");
				}
				dmo.executeBatchByDSImmediately(null, sql, false); //
				InsertSQLBuilder logsql = getSynLog(null, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_XGB,
						Syn_Log_Exec_State_Success, "ϵͳ���ֱ�ṹ�仯���Զ�ִ�нű�:\r\n"
								+ descr);
				dmo.executeUpdateByDSImmediately(null, logsql);
			}
		} catch (Exception ex) { // �����ڣ�ֱ�Ӵ���
			createTableByConfig(_mainVO.getStringValue("id"));
		}
		ArrayList list = new ArrayList();
		//
		int rowcount = 0; // ��¼���ɶ���������.
		String logid = dmo.getSequenceNextValByDS(null,
				"S_SAL_DATA_INTERFACE_LOG");
		String deptid=dmo.getStringValueByDS(null,"select id from pub_corp_dept where code='"+filesum.getName()+"'");
		String countid=null;
		if(soleflg ){
			countid=dmo.getStringValueByDS(null,"select max(id) from "+tableName);
			if(countid.equals(null) || countid==null || countid.equals("") || countid.equals(" "))
				countid="0";
			}else{
				countid=String.valueOf(Integer.parseInt(countid)+10);
		}
		int countvalue=Integer.parseInt(countid);
		while ((strcontent = filereader.readLine()) != null) { // ���ж�ȡ�������߼������ò����滻�����԰��ж�ȡ��Ҳ������xml��ȡ��
			String itemvalues[] = strcontent.split(splitStr); // ��ȡʵ��ֵ
			rowcount++;
			if (itemvalues.length != _childItemVOs.length) {
				filereader.close(); // һ��Ҫ�ر���
				InsertSQLBuilder inisert = getSynLog(logid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), fileAbsolutePath,
						Syn_Log_Exec_Type_Other, Syn_Log_Exec_State_Fail,
						fileAbsolutePath + "�ļ��ֶγ��Ⱥ�ϵͳ���ò�һֱ.�ļ�["
								+ itemvalues.length + "]��,ϵͳ����["
								+ _childItemVOs.length + "]��");
				dmo.executeUpdateByDSImmediately(null, inisert);
				throw new Exception(fileAbsolutePath + "�ļ��ֶγ��Ⱥ�ϵͳ���ò�һֱ.�ļ�["
						+ itemvalues.length + "]��,ϵͳ����[" + _childItemVOs.length
						+ "]��");
			}
			InsertSQLBuilder insert = new InsertSQLBuilder(tableName);
			insert.putFieldValue("logid", logid);
			insert.putFieldValue("deptid", deptid);
			if(soleflg){
				countvalue++;		
				insert.putFieldValue("id", countvalue);
			}
			for (int i = 0; i < itemvalues.length; i++) {
				String value = itemvalues[i];
				value = value.replace("\"", "").trim();
				insert.putFieldValue(stbutil.convertIntColToEn(i + 1,false), value);
			}
			list.add(insert);
			if (list.size() == 5000) { // 5000���ύһ�Ρ��������ܸ�����
				dmo.executeBatchByDSImmediately(null, list, false);
				list.clear();
			}
		}
		filereader.close();
		InsertSQLBuilder inisert = getSynLog(logid, _mainVO
				.getStringValue("id"), _jobid, _datadate, _mainVO
				.getStringValue("iname"), fileAbsolutePath,
				Syn_Log_Exec_Type_TBSJ, Syn_Log_Exec_State_Success, "����ͬ��"
						+ rowcount + "����¼.");
		list.add(inisert); // ������־
		UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
				"sal_data_interface_main");
		updateMainTable
				.setWhereCondition(" id=" + _mainVO.getStringValue("id"));
		updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
		list.add(updateMainTable);
		dmo.executeBatchByDSImmediately(null, list, false);
			}
		}else{
			File file = new File(fileAbsolutePath);
			if (!file.exists()) {
				throw new Exception(fileAbsolutePath + "�ļ������ڣ�����Ҫ��δ�ϴ�����˫���ӿ����ò�һ��.");
			}
			String str = tbutil.readFromInputStreamToStr(new FileInputStream(file));
			String[] allline = str.split("\n");
//			for (int i = 0; i < allline.length; i++) {
//				String values[] = allline[i].split(splitStr);
//				for (int j = 0; j < values.length; j++) {
//					System.out.println(values[j]);
//				}
//			}
			InputStreamReader read = new InputStreamReader(
					new FileInputStream(file), datacode);
			BufferedReader filereader = new BufferedReader(read);
			String strcontent = null;

			String tableName = _mainVO.getStringValue("tablename");
			// �����Ƿ����,��������жϽṹ�Ƿ�仯
			try {
				dmo.getStringValueByDS(null, "select * from " + tableName
						+ " where 1=2");
				String[][] different = compareDBTOConfig(_mainVO, _childItemVOs);// ����ṹ��û�б仯��һ���ǲ���ġ�
				if (different != null && different.length > 0) {
					List sql = new ArrayList();
					StringBuffer descr = new StringBuffer();
					for (int i = 0; i < different.length; i++) {
						sql.add(different[i][3]);
						descr.append(different[i][3] + "\r\n");
					}
					dmo.executeBatchByDSImmediately(null, sql, false); //
					InsertSQLBuilder logsql = getSynLog(null, _mainVO
							.getStringValue("id"), _jobid, _datadate, _mainVO
							.getStringValue("iname"), "", Syn_Log_Exec_Type_XGB,
							Syn_Log_Exec_State_Success, "ϵͳ���ֱ�ṹ�仯���Զ�ִ�нű�:\r\n"
									+ descr);
					dmo.executeUpdateByDSImmediately(null, logsql);
				}
			} catch (Exception ex) { // �����ڣ�ֱ�Ӵ���
				createTableByConfig(_mainVO.getStringValue("id"));
			}

			// ������û����ʷ�����еĻ�����ɾ������Ȼ����ִ��.
			HashVO[] old_his = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_log where filepath like '%"
							+ _folder + "%' and exectype='"
							+ Syn_Log_Exec_Type_TBSJ + "' and parentid="
							+ _mainVO.getStringValue("id")
							+ " order by exectime desc"); // ������ͬ���ļ�¼
			if (old_his != null && old_his.length > 0) {
				List list = new ArrayList();
				StringBuffer msg = new StringBuffer();
				for (int i = 0; i < old_his.length; i++) {
					HashVO loghisvo = old_his[i];
					msg.append(loghisvo.getStringValue("exectime") + ",");
					String deloldsql = "delete from "
							+ _mainVO.getStringValue("tablename")
							+ " where logid = " + loghisvo.getStringValue("id");
					list.add(deloldsql);
				}
				String newlogid = dmo.getSequenceNextValByDS(null,
						"S_SAL_DATA_INTERFACE_LOG");
				InsertSQLBuilder dellogsql = getSynLog(newlogid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_SCSJ,
						Syn_Log_Exec_State_Success, "ϵͳ���ָýӿ��Ѿ���["
								+ msg.substring(0, msg.length() - 1)
								+ "]ͬ����,��������ʷ����.");
				list.add(dellogsql);
				dmo.executeBatchByDSImmediately(null, list);
			}
			ArrayList list = new ArrayList();
			//
			int rowcount = 0; // ��¼���ɶ���������.
			String logid = dmo.getSequenceNextValByDS(null,
					"S_SAL_DATA_INTERFACE_LOG");
			String countid=null;
			if(soleflg ){
				countid=dmo.getStringValueByDS(null,"select max(id) from "+tableName);
				if(countid.equals(null) || countid==null || countid.equals("") || countid.equals(" "))
					countid="0";
				}else{
					countid=String.valueOf(Integer.parseInt(countid)+10);
			}
			int countvalue=Integer.parseInt(countid);
			while ((strcontent = filereader.readLine()) != null) { // ���ж�ȡ�������߼������ò����滻�����԰��ж�ȡ��Ҳ������xml��ȡ��
				String itemvalues[] = strcontent.split(splitStr); // ��ȡʵ��ֵ
				rowcount++;
				if (itemvalues.length != _childItemVOs.length) {
					filereader.close(); // һ��Ҫ�ر���
					InsertSQLBuilder inisert = getSynLog(logid, _mainVO
							.getStringValue("id"), _jobid, _datadate, _mainVO
							.getStringValue("iname"), fileAbsolutePath,
							Syn_Log_Exec_Type_Other, Syn_Log_Exec_State_Fail,
							fileAbsolutePath + "�ļ��ֶγ��Ⱥ�ϵͳ���ò�һֱ.�ļ�["
									+ itemvalues.length + "]��,ϵͳ����["
									+ _childItemVOs.length + "]��");
					dmo.executeUpdateByDSImmediately(null, inisert);
					throw new Exception(fileAbsolutePath + "�ļ��ֶγ��Ⱥ�ϵͳ���ò�һֱ.�ļ�["
							+ itemvalues.length + "]��,ϵͳ����[" + _childItemVOs.length
							+ "]��");
				}
				InsertSQLBuilder insert = new InsertSQLBuilder(tableName);
				insert.putFieldValue("logid", logid);
				if(soleflg){
					countvalue++;
					insert.putFieldValue("id", countvalue);
				}
				for (int i = 0; i < itemvalues.length; i++) {
					String value = itemvalues[i];
					value = value.replace("\"", "").trim();
					insert.putFieldValue(stbutil.convertIntColToEn(i + 1,false), value);
				}
				list.add(insert);
				if (list.size() == 5000) { // 5000���ύһ�Ρ��������ܸ�����
					dmo.executeBatchByDSImmediately(null, list, false);
					list.clear();
				}
			}
			filereader.close();
			InsertSQLBuilder inisert = getSynLog(logid, _mainVO
					.getStringValue("id"), _jobid, _datadate, _mainVO
					.getStringValue("iname"), fileAbsolutePath,
					Syn_Log_Exec_Type_TBSJ, Syn_Log_Exec_State_Success, "����ͬ��"
							+ rowcount + "����¼.");
			list.add(inisert); // ������־
			UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
					"sal_data_interface_main");
			updateMainTable
					.setWhereCondition(" id=" + _mainVO.getStringValue("id"));
			updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
			list.add(updateMainTable);
			dmo.executeBatchByDSImmediately(null, list, false);
			
		}
	}
	private String convertRealColType(String _type, String _dbType) {
		if (_type.equalsIgnoreCase("decimal")
				|| _type.equalsIgnoreCase("number")) { // �������������
			if (_dbType.equalsIgnoreCase("ORACLE")) {
				return "number"; //
			} else {
				return "decimal"; //
			}
		} else if (_type.equalsIgnoreCase("varchar")) {
			if (_dbType.equalsIgnoreCase("ORACLE")) {
				return "varchar2"; //
			} else {
				return "varchar"; //
			}
		} else if (_type.equalsIgnoreCase("char")) {
			return "char"; //
		} else {
			return _type; //
		}
	}

	/*
	 * ��ȡ����Ŀ¼
	 * 
	 * @��Ӫ��
	 */
	public String[] getFiledesc() {
		if (ftppath == null) {
			return null;
		}
		File file = new File(ftppath);
		List list = new ArrayList();
		if (file.isDirectory()) {
			File file2[] = file.listFiles();
			for (int i = 0; i < file2.length; i++) {
				if (file2[i].isDirectory()) {
					File[] listfiles = file2[i].listFiles();
					for (int j = 0; j < listfiles.length; j++) {
						if (listfiles[j].isDirectory()) {
							String pathes = listfiles[j].getName();
							list.add(pathes);
						}
					}
				}
			}
		}
		String filepath[] = (String[]) list.toArray(new String[0]);
		for (int i = 0; i < filepath.length - 1; i++) {
			for (int j = i + 1; j < filepath.length; j++) {
				if (filepath[i].compareToIgnoreCase(filepath[j]) < 0) {
					String temp = filepath[i];
					filepath[i] = filepath[j];
					filepath[j] = temp;
				}
			}
		}
		return filepath;
	}

	/*
	 * ��ʱ��һ���賿3�㣬���ôκ��� ͨ����ʱ�����õ�����ͬ�� log���JobIDΪ�գ�˵�����ֶ�ִ��
	 */
	public boolean syn_data_bytimer(String _jobid, String _datadate)
			throws Exception {
		// String currFolder = "20140709"; // �Զ�ͬ���Ļ�������ͬ���������ڵ����ݡ�
		//
		String filefolder = _datadate.replace("-", "");
		/*
		 * File ok_file = new File(ftppath + File.separator + filefolder +
		 * File.separator + "OK.DAT"); // �ɹ�����ļ�. if (!ok_file.exists()) {
		 * String logid = dmo.getSequenceNextValByDS(null,
		 * "S_SAL_DATA_INTERFACE_LOG"); InsertSQLBuilder logsql =
		 * getSynLog(logid, "-1", _jobid, _datadate, "���ݽӿڱ��OK.DAT�ļ�",
		 * ok_file.getAbsolutePath(), Syn_Log_Exec_Type_TBSJ,
		 * Syn_Log_Exec_State_Fail, "ϵͳû�з���" + filefolder + "Ŀ¼��OK.DAT�ļ�.");
		 * dmo.executeUpdateByDSImmediately(null, logsql); // throw new
		 * Exception("ϵͳû�з���" + ok_file.getAbsolutePath() + "�ļ�."); }
		 */
		HashVO[] data_ifc_mainVOs = dmo
				.getHashVoArrayByDS(null,
						"select * from sal_data_interface_main where activeflag='Y' order by seq"); // �ҳ�������Ҫ����Ľӿ�
		HashVO[] data_ifc_item_config = dmo.getHashVoArrayByDS(null,
				"select * from sal_data_interface_item_conf order by seq");// �ӿ��ֶ�����

		HashMap<String, List> main_item_config_map = new HashMap(); // ���ӱ��Ӧ

		for (int i = 0; i < data_ifc_item_config.length; i++) {
			String mainid = data_ifc_item_config[i].getStringValue("parentid"); // ����ID
			if (main_item_config_map.containsKey(mainid)) {
				List list = main_item_config_map.get(mainid);
				list.add(data_ifc_item_config[i]);
			} else {
				List list = new ArrayList();
				list.add(data_ifc_item_config[i]);
				main_item_config_map.put(mainid, list);
			}
		}
		int syn_success = 0;
		StringBuffer err = new StringBuffer();
		for (int i = 0; i < data_ifc_mainVOs.length; i++) {
			try {
				List childList = main_item_config_map.get(data_ifc_mainVOs[i]
						.getStringValue("id"));
				if (childList.size() > 0) {
					syn_data_from_file(data_ifc_mainVOs[i],
							(HashVO[]) childList.toArray(new HashVO[0]),
							_datadate, _jobid);
				}
				syn_success++;
			} catch (Exception e) {
				err.append(e.toString() + "\r\n");
			}
		}
		if (err.length() > 0) {
			throw new Exception(err.toString());
		}
		return true;
	}

	/*
	 * �Ա�����ƽ̨�ϴ����ļ� �� ���� ���õ� ���졣
	 * ͨ��������ļ�·������ȡ�����������ļ����ƺ�ϵͳsal_data_interface_main���м�¼�����Աȡ����ղ��뵽log����
	 */
	public void compareDataIFCAndConfig(String _currFolder) {

	}

	/*
	 * �Զ��Ƚϱ��Ƿ����仯��������ִ��sql.
	 */
	public String[][] compareDBTOConfig(HashVO hashVO, HashVO[] hashVOs)
			throws Exception {
		if (hashVOs == null || hashVOs.length < 1) {
			return null;
		}
		List<String[]> list = new ArrayList<String[]>();
		HashMap<String, String> dbmap = new HashMap<String, String>();
		String tablename = hashVO.getStringValue("tablename");
		TableDataStruct dataStruct = dmo.getTableDataStructByDS(null,
				"select * from " + tablename + " where 1=2");
		if (dataStruct == null) {
			return null;
		}
		String[] headername = dataStruct.getHeaderName();
		int[] headerlength = dataStruct.getPrecision();
		int[] scale = dataStruct.getScale();
		for (int i = 0; i < headername.length; i++) {
			if (scale[i] == 0) {
				dbmap.put(headername[i], headerlength[i] + "");
			} else {
				dbmap.put(headername[i], headerlength[i] + "," + scale[i]);
			}
		}
		for (int i = 0; i < hashVOs.length; i++) {
			String itemname = hashVOs[i].getStringValue("itemname");
			String itemcode = hashVOs[i].getStringValue("itemcode");
			String itemwidth = hashVOs[i].getStringValue("itemwidth");
			String itemtype = hashVOs[i].getStringValue("itemtype");
			if (dbmap.containsKey(itemcode)) {
				String itemdbwidth = dbmap.get(itemcode);
				if (itemdbwidth.equals(itemwidth)) {
					continue;
				} else {
					String sql = "alter table " + tablename + " modify "
							+ itemcode + " " + itemtype + "(" + itemwidth + ")";
					String[] info = new String[] { itemname, itemcode,
							itemtype, sql, "�޸���", itemwidth, itemdbwidth };
					list.add(info);
				}
			} else {
				String sql = "alter table  " + tablename + " add " + itemcode
						+ " " + itemtype + "(" + itemwidth + ")";
				String[] info = new String[] { itemname, itemcode, itemtype,
						sql, "������", itemwidth };
				list.add(info);
			}
		}
		return list.toArray(new String[0][0]);
	}

	/*
	 * ����ͬ����ɺ󣬵��ø÷�����������ת����
	 */
	public boolean convertIFCDataToReport(String _jobid, String dataDate)
			throws Exception {
		HashVO[] convertMainVOS = dmo.getHashVoArrayByDS(null,
				"select * from sal_convert_ifcdata order by seq"); // �ҳ�����ת������
		HashVO[] convertChildConfVOs = dmo.getHashVoArrayByDS(null,
				"select * from sal_convert_ifcdata_item_conf order by seq");
		HashMap<String, List> main_item_config_map = new HashMap(); // ���ӱ��Ӧ
		for (int i = 0; i < convertChildConfVOs.length; i++) {
			String mainid = convertChildConfVOs[i].getStringValue("parentid"); // ����ID
			if (main_item_config_map.containsKey(mainid)) {
				List list = main_item_config_map.get(mainid);
				list.add(convertChildConfVOs[i]);
			} else {
				List list = new ArrayList();
				list.add(convertChildConfVOs[i]);
				main_item_config_map.put(mainid, list);
			}
		}
		StringBuffer err = new StringBuffer();
		for (int i = 0; i < convertMainVOS.length; i++) {
			List childList = main_item_config_map.get(convertMainVOS[i]
					.getStringValue("id"));
			if (childList.size() > 0) {
				try {
					convertIFCDataToReport(convertMainVOS[i],
							(HashVO[]) childList.toArray(new HashVO[0]),
							_jobid, dataDate); // ��ʼִ��
				} catch (Exception ex) {
					err.append(ex.toString() + "\r\n");
				}
			}
		}
		if (err.length() > 0) {
			throw new Exception(err.toString());
		}
		return true;
	}

	/*
	 * �ֶ�ִ�е���
	 */
	public void convertIFCDataToReportByHand(String _mainid, String dataDate)
			throws Exception {
		HashVO[] convertMainVOS = dmo.getHashVoArrayByDS(null,
				"select * from sal_convert_ifcdata where id = " + _mainid); // �ҳ�����ת������
		HashVO[] convertChildConfVOs = dmo.getHashVoArrayByDS(null,
				"select * from sal_convert_ifcdata_item_conf where parentid="
						+ _mainid + " order by seq");
		if (convertMainVOS.length > 0) {
			convertIFCDataToReport(convertMainVOS[0], convertChildConfVOs,
					null, dataDate);
		}
	}

	/*
	 * �ѽӿ�����ת��Ϊϵͳ��ʶ��ı�������. ��Ҫ�ж����������ǲ����µ����ݡ�����ǣ��������ݱ��ݡ�
	 */
	private void convertIFCDataToReport(HashVO _mainConfigVO,
			HashVO[] _itemConfigvos, String _jobid, String dataDate)
			throws Exception {
		// ������ڷ񣬲�����ֱ�Ӵ���������б�����logid�ֶΣ������꣬���ֶΡ�
		autoCheckTableExistsAndCreate(_mainConfigVO, _itemConfigvos, _jobid,
				dataDate);
		// �ҵ�ͬ�����ݵ���־ID��

		HashVO[] old_his = dmo.getHashVoArrayByDS(null,
				"select * from sal_convert_ifcdata_log where datadate='"
						+ dataDate + "' and exectype='"
						+ Syn_Log_Convert_Type_SJZH + "' and parentid="
						+ _mainConfigVO.getStringValue("id")
						+ " order by exectime desc"); // ������ͬ���ļ�¼
		if (old_his != null && old_his.length > 0) {
			List list = new ArrayList();
			StringBuffer msg = new StringBuffer();
			for (int i = 0; i < old_his.length; i++) {
				HashVO loghisvo = old_his[i];
				msg.append(loghisvo.getStringValue("exectime") + ",");
				String deloldsql = "delete from "
						+ _mainConfigVO.getStringValue("tablename")
						+ " where logid = " + loghisvo.getStringValue("id");
				list.add(deloldsql);
			}
			InsertSQLBuilder dellogsql = getConvertLog(null, _mainConfigVO
					.getStringValue("id"), _mainConfigVO
					.getStringValue("reportname"), _jobid, dataDate,
					Syn_Log_Convert_Type_SCSJ, Syn_Log_Exec_State_Success,
					"ϵͳ����[" + dataDate + "]�����Ѿ���["
							+ msg.substring(0, msg.length() - 1)
							+ "]ת����,��������ʷ����.");
			list.add(dellogsql);
			dmo.executeBatchByDSImmediately(null, list, false);
		}
		String policyClass = _mainConfigVO.getStringValue("etlpolicy");
		if (!tbutil.isEmpty(policyClass) || _itemConfigvos == null
				|| _itemConfigvos.length == 0) { // ���û�������ӱ���ô˵�����ò�����
			try {
				Object obj = Class.forName(policyClass).newInstance();
				if (obj instanceof DataIFCConvertToReport) {
					((DataIFCConvertToReport) obj).convert(_mainConfigVO,
							_jobid, dataDate);
				} else {
					InsertSQLBuilder dellogsql = getConvertLog(
							null,
							_mainConfigVO.getStringValue("id"),
							_mainConfigVO.getStringValue("reportname"),
							_jobid,
							dataDate,
							Syn_Log_Convert_Type_SJZH,
							Syn_Log_Exec_State_Fail,
							"["
									+ dataDate
									+ "]����ת��ʧ��,ԭ��"
									+ _mainConfigVO
											.getStringValue("reportname")
									+ "�ӿ�����ת��Ϊ�����������ò�����Ҫʵ��[DataIFCConvertToReport]�ӿ�.");
					dmo.executeUpdateByDSImmediately(null, dellogsql);
					throw new Exception(_mainConfigVO
							.getStringValue("reportname")
							+ "�ӿ�����ת��Ϊ�����������ò�����Ҫʵ��[DataIFCConvertToReport]�ӿ�.");
				}
			} catch (Exception ex) {
				InsertSQLBuilder dellogsql = getConvertLog(null, _mainConfigVO
						.getStringValue("id"), _mainConfigVO
						.getStringValue("reportname"), _jobid, dataDate,
						Syn_Log_Convert_Type_SJZH, Syn_Log_Exec_State_Fail, "["
								+ dataDate + "]����ת��ʧ��,ԭ��" + ex.getMessage());
				dmo.executeUpdateByDSImmediately(null, dellogsql);
				throw ex;
			}
		} else {
			String sourceifc = _mainConfigVO.getStringValue("sourceifc"); // �õ��ӿ���������ID
			HashVO[] ifcconfig = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_main where id ="
							+ sourceifc);// ���ñ�
			if (ifcconfig == null || ifcconfig.length == 0) {
				throw new Exception(_mainConfigVO.getStringValue("reportname")
						+ "�ӿ�����ת�����õ����ݽӿ���ԴΪ��.");
			}
			String syndatalogid = null;
			//
			if (tbutil.isEmpty(_jobid) || "-1".equals(_jobid)) { // ���Զ�����ִ�С�
				syndatalogid = dmo.getStringValueByDS(null,
						"select id from sal_data_interface_log where parentid = "
								+ ifcconfig[0].getStringValue("id")
								+ " and exectype='" + Syn_Log_Exec_Type_TBSJ
								+ "' and state='" + Syn_Log_Exec_State_Success
								+ "' and datadate='" + dataDate
								+ "' order by exectime desc");
				if (tbutil.isEmpty(syndatalogid)) { // ���û���ҵ�ͬ����־��ֱ�ӱ���
					throw new Exception("[" + dataDate + "]����û��ͬ����,����.");
				}
			} else {
				syndatalogid = dmo.getStringValueByDS(null,
						"select id from sal_data_interface_log where parentid = "
								+ ifcconfig[0].getStringValue("id")
								+ " and exectype='" + Syn_Log_Exec_Type_TBSJ
								+ "' and state='" + Syn_Log_Exec_State_Success
								+ "' and jobid=" + _jobid);
			}

			HashVO[] datas = dmo.getHashVoArrayByDS(null, "select * from "
					+ ifcconfig[0].getStringValue("tablename")
					+ " where logid = " + syndatalogid); // �ҵ�����ͬ����������������
			String convertDataLogid = dmo.getSequenceNextValByDS(null,
					"S_SAL_CONVERT_IFCDATA_LOG");
			List insertDatasSql = new ArrayList();
			String year = dataDate.substring(0, 4);
			String month = dataDate.substring(5, 7);
			for (int i = 0; i < datas.length; i++) { //
				InsertSQLBuilder insert = new InsertSQLBuilder(_mainConfigVO
						.getStringValue("tablename"));
				insert.putFieldValue("logid", convertDataLogid);
				insert.putFieldValue("year", year);
				insert.putFieldValue("month", month);
				for (int j = 0; j < _itemConfigvos.length; j++) { //
					String itemcode = _itemConfigvos[j]
							.getStringValue("itemcode");// ���������е��ֶ�
					String ifcitem = _itemConfigvos[j]
							.getStringValue("ifcitem"); // �ӿڱ��е��ֶ�
					boolean flag = datas[i].containsKey(ifcitem.toUpperCase())
							|| datas[i].containsKey(ifcitem.toLowerCase()); // �Ƿ�������ֶ�
					if (flag) {
						insert.putFieldValue(itemcode, datas[i]
								.getStringValue(ifcitem));
					}
				}
				insertDatasSql.add(insert);
			}
			// ����ִ����ʷ
			InsertSQLBuilder logsql = getConvertLog(convertDataLogid,
					_mainConfigVO.getStringValue("id"), _mainConfigVO
							.getStringValue("reportname"), _jobid, dataDate,
					Syn_Log_Convert_Type_SJZH, Syn_Log_Exec_State_Success,
					"����ת���ɹ�������" + insertDatasSql.size() + "��");
			insertDatasSql.add(logsql);

			UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
					"sal_convert_ifcdata");
			updateMainTable.setWhereCondition(" id="
					+ _mainConfigVO.getStringValue("id"));
			updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
			insertDatasSql.add(updateMainTable);
			dmo.executeBatchByDSImmediately(null, insertDatasSql, false); // �������ݿ�
		}
	}

	/*
	 * �Զ������� _datadate ����Ϊ��
	 */
	public void autoCheckTableExistsAndCreate(HashVO _mainConfvo,
			HashVO[] _childConfig, String _jobid, String _datadate)
			throws Exception {
		String tablename = _mainConfvo.getStringValue("tablename");
		try {
			dmo.getStringValueByDS(null, "select * from " + tablename
					+ " where 1=2"); // �����Ƿ����
		} catch (Exception ex) {
			if (_childConfig == null || _childConfig.length == 0) {
				InsertSQLBuilder logsql = getConvertLog(null, _mainConfvo
						.getStringValue("id"), _mainConfvo
						.getStringValue("reportname"), _jobid, _datadate,
						Syn_Log_Convert_Type_CJB, Syn_Log_Exec_State_Fail,
						"������[" + tablename + "]ʧ�ܣ�ԭ��:û���ҵ���ṹ����");
				dmo.executeUpdateByDSImmediately(null, logsql);
				throw new Exception("ϵͳ�����Զ�����["
						+ _mainConfvo.getStringValue("reportname")
						+ "]����ṹ.ԭ�򣺱�ṹû������");
			}
			StringBuffer sb = new StringBuffer();
			String dbtype = ServerEnvironment.getDefaultDataSourceType();
			sb.append("create table " + tablename + "(");
			sb.append("year " + convertRealColType("varchar", dbtype) + "(" + 4
					+ "),"); //
			sb.append("month " + convertRealColType("varchar", dbtype) + "("
					+ 2 + "),"); //
			sb.append("logid " + convertRealColType("decimal", dbtype) + "("
					+ 22 + ")"); //
			for (int i = 0; i < _childConfig.length; i++) {
				String type = "varchar";
				if (_childConfig[i].getStringValue("itemwidth", "20").contains(
						",")) {
					type = "decimal";
				}
				sb.append("," + _childConfig[i].getStringValue("itemcode")
						+ " " + convertRealColType(type, dbtype) + "("
						+ _childConfig[i].getStringValue("itemwidth", "20")
						+ ") "); //
				if (dbtype.equalsIgnoreCase("MYSQL")
						&& !tbutil.isEmpty(_childConfig[i]
								.getStringValue("itemname"))) {
					sb.append(" comment '"
							+ _childConfig[i].getStringValue("itemname") + "'");
				}
			}
			sb.append(")");
			if (dbtype.equalsIgnoreCase("MYSQL")) {
				sb.append(" DEFAULT CHARSET = GBK"); //
				sb.append(" comment '"
						+ _mainConfvo.getStringValue("reportname") + "'"); //
			}
			dmo.executeUpdateByDS(null, sb.toString());
			InsertSQLBuilder logsql = getConvertLog(null, _mainConfvo
					.getStringValue("id"), _mainConfvo
					.getStringValue("reportname"), _jobid, _datadate,
					Syn_Log_Convert_Type_CJB, Syn_Log_Exec_State_Success,
					"������[" + tablename + "],ִ�����" + sb.toString());
			dmo.executeUpdateByDSImmediately(null, logsql);
		}
	}

	/*
	 * ��ȡͨ��ת��sql
	 */
	public InsertSQLBuilder getConvertLog(String _id, String _parentid,
			String _name, String _jobid, String _datadate, String _exectype,
			String _state, String descr) throws Exception {
		InsertSQLBuilder loginsertsql = new InsertSQLBuilder(
				"sal_convert_ifcdata_log");
		if (tbutil.isEmpty(_id)) { // һ����־ID�Ǵ���ģ������ط����õ���
			_id = dmo.getSequenceNextValByDS(null, "S_SAL_CONVERT_IFCDATA_LOG");
		}
		loginsertsql.putFieldValue("id", _id);
		loginsertsql.putFieldValue("parentid", _parentid);
		loginsertsql.putFieldValue("jobid", _jobid);// �ļ�·��
		loginsertsql.putFieldValue("datadate", _datadate); // �ӿ�����
		loginsertsql.putFieldValue("exectime", tbutil.getCurrTime());
		loginsertsql.putFieldValue("reportname", _name); // �ӿ�����
		loginsertsql.putFieldValue("exectype", _exectype);
		loginsertsql.putFieldValue("state", _state);
		if (descr != null && descr.length() > 1950) {
			descr = descr.substring(0, 1950) + "...";
		}
		loginsertsql.putFieldValue("descr", descr);
		return loginsertsql;
	}

	/**
	 * 
	 * @param zzl
	 *            ��2017/6/2��
	 * @return �����ļ�
	 */
	public static ArrayList getFiles(String aDir) {
		ArrayList files = new ArrayList();
		try {
			File dirx = new File(aDir);
			File[] dirFilesx = dirx.listFiles();
				for (int k = 0; k < dirFilesx.length; k++) {
					System.out.print("|");
					File file = dirFilesx[k];
					String fileName = file.getName();
					System.out.println("-" + fileName);
					if (file.isDirectory()) {
						String dirx1 = aDir + "\\" + fileName;
						getFiles(dirx1);
					}
					files.add(file);
				}
		} catch (Exception e) {	
			e.printStackTrace();
		}
		return files;

	}
/**
 * 
 * @param dataIFCmainID
 * @param datadate
 * @param string
 * @return zzl[2018-11-23] ����ͬ�������ӿ�
 */
	public String impReadDataFromFile(String _dataIFCmainID, String _dataDate,
			String _jobid) {
		HashVO data_ifc_mainVOs[];
		String str="";
		try {
			data_ifc_mainVOs = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_main where id = "
							+ _dataIFCmainID);		if (data_ifc_mainVOs.length == 0) {
								throw new Exception("����Ľӿ�ͬ��������������Ϊ" + _dataIFCmainID
										+ "��������,����!");
							}

							HashVO[] data_ifc_item_config = dmo.getHashVoArrayByDS(null,
									"select * from sal_data_interface_item_conf where parentid = "
											+ _dataIFCmainID);
							str=imp_all_data_from_file(data_ifc_mainVOs[0], data_ifc_item_config,
										_dataDate, _jobid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //

		return str;
	}
	
	public String imp_all_data_from_file(HashVO _mainVO, HashVO[] _childItemVOs,
			String _datadate, String _jobid) throws Exception {
		String str="";
		if (tbutil.isEmpty(ftppath)) {
			return str="ϵͳ����[���ݽӿ�FTP·��]Ϊ�գ�������!";
		}
		String _folder = _datadate.replace("-", ""); // ����������ڣ���ȡΪͬ��Ŀ¼
		String parentFolder = _folder.substring(0, 6);
		String folderAbsolutePath = tbutil.replaceAll(ftppath, "\\",
				File.separator)
				+ File.separator
				+ parentFolder
				+ File.separator
				+ _folder
				+ File.separator; //
		String fileAbsolutePath = folderAbsolutePath+ _mainVO.getStringValue("filename");
		String filename=_mainVO.getStringValue("iname");
		if (flg) {
			// ������û����ʷ�����еĻ�����ɾ������Ȼ����ִ��.
			HashVO[] old_his = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_log where filepath like '%"
							+ _folder + "%' and exectype='"
							+ Syn_Log_Exec_Type_TBSJ + "' and parentid="
							+ _mainVO.getStringValue("id")
							+ " order by exectime desc"); // ������ͬ���ļ�¼
			if (old_his != null && old_his.length > 0) {
				List list = new ArrayList();
				StringBuffer msg = new StringBuffer();
				for (int i = 0; i < old_his.length; i++) {
					HashVO loghisvo = old_his[i];
					msg.append(loghisvo.getStringValue("exectime") + ",");
					String deloldsql = "delete from "
							+ _mainVO.getStringValue("tablename")
							+ " where logid = " + loghisvo.getStringValue("id");
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+deloldsql);
					list.add(deloldsql);
				}
				String newlogid = dmo.getSequenceNextValByDS(null,
						"S_SAL_DATA_INTERFACE_LOG");
				InsertSQLBuilder dellogsql = getSynLog(newlogid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_SCSJ,
						Syn_Log_Exec_State_Success, "ϵͳ���ָýӿ��Ѿ���["
								+ msg.substring(0, msg.length() - 1)
								+ "]ͬ����,��������ʷ����.");
				list.add(dellogsql);
				dmo.executeBatchByDSImmediately(null, list);
			}
			ArrayList a = getFiles(folderAbsolutePath);
			if(a.size()<=0){
				return str="["+filename+"]�ļ�������,����û�ϴ�";
			}
			for (int f = 0; f < a.size(); f++) {
				File filesum = (File) a.get(f);
				fileAbsolutePath=folderAbsolutePath+"/"+filesum.getName()+"/"+ _mainVO.getStringValue("filename");
		File file = new File(fileAbsolutePath);
		if (!file.exists()) {
			return str=fileAbsolutePath + "�ļ�������,���ϴ�����";
		}
//		String str = tbutil.readFromInputStreamToStr(new FileInputStream(file));
//		String[] allline = str.split("\n");
//		for (int i = 0; i < allline.length; i++) {
//			String values[] = allline[i].split(splitStr);
//			for (int j = 0; j < values.length; j++) {
//				System.out.println(values[j]);
//			}
//		}
		InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), datacode);
		BufferedReader filereader = new BufferedReader(read);
		String strcontent = null;

		String tableName = _mainVO.getStringValue("tablename");
		// �����Ƿ����,��������жϽṹ�Ƿ�仯
		try {
			dmo.getStringValueByDS(null, "select * from " + tableName
					+ " where 1=2");
			String[][] different = compareDBTOConfig(_mainVO, _childItemVOs);// ����ṹ��û�б仯��һ���ǲ���ġ�
			if (different != null && different.length > 0) {
				List sql = new ArrayList();
				StringBuffer descr = new StringBuffer();
				for (int i = 0; i < different.length; i++) {
					sql.add(different[i][3]);
					descr.append(different[i][3] + "\r\n");
				}
				dmo.executeBatchByDSImmediately(null, sql, false); //
				InsertSQLBuilder logsql = getSynLog(null, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_XGB,
						Syn_Log_Exec_State_Success, "ϵͳ���ֱ�ṹ�仯���Զ�ִ�нű�:\r\n"
								+ descr);
				dmo.executeUpdateByDSImmediately(null, logsql);
			}
		} catch (Exception ex) { // �����ڣ�ֱ�Ӵ���
			createTableByConfig(_mainVO.getStringValue("id"));
		}
		ArrayList list = new ArrayList();
		//
		int rowcount = 0; // ��¼���ɶ���������.
		String logid = dmo.getSequenceNextValByDS(null,
				"S_SAL_DATA_INTERFACE_LOG");
		String deptid=dmo.getStringValueByDS(null,"select id from pub_corp_dept where code='"+filesum.getName()+"'");
		String countid=null;
		if(soleflg ){
			countid=dmo.getStringValueByDS(null,"select max(id) from "+tableName);
			if(countid.equals(null) || countid==null || countid.equals("") || countid.equals(" "))
				countid="0";
			}else{
				countid=String.valueOf(Integer.parseInt(countid)+10);
		}
		int countvalue=Integer.parseInt(countid);
		while ((strcontent = filereader.readLine()) != null) { // ���ж�ȡ�������߼������ò����滻�����԰��ж�ȡ��Ҳ������xml��ȡ��
			String itemvalues[] = strcontent.split(splitStr); // ��ȡʵ��ֵ
			rowcount++;
			if (itemvalues.length != _childItemVOs.length) {
				filereader.close(); // һ��Ҫ�ر���
				InsertSQLBuilder inisert = getSynLog(logid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), fileAbsolutePath,
						Syn_Log_Exec_Type_Other, Syn_Log_Exec_State_Fail,
						fileAbsolutePath + "�ļ��ֶγ��Ⱥ�ϵͳ���ò�һֱ.�ļ�["
								+ itemvalues.length + "]��,ϵͳ����["
								+ _childItemVOs.length + "]��");
				dmo.executeUpdateByDSImmediately(null, inisert);
				return str=fileAbsolutePath + "�ļ��ֶγ��Ⱥ�ϵͳ���ò�һֱ.�ļ�["
				+ itemvalues.length + "]��,ϵͳ����[" + _childItemVOs.length
				+ "]��";
			}
			InsertSQLBuilder insert = new InsertSQLBuilder(tableName);
			insert.putFieldValue("logid", logid);
			insert.putFieldValue("deptid", deptid);
			if(soleflg){
				countvalue++;		
				insert.putFieldValue("id", countvalue);
			}
			for (int i = 0; i < itemvalues.length; i++) {
				String value = itemvalues[i];
				value = value.replace("\"", "").trim();
				insert.putFieldValue(stbutil.convertIntColToEn(i + 1,false), value);
			}
			list.add(insert);
			if (list.size() == 5000) { // 5000���ύһ�Ρ��������ܸ�����
				dmo.executeBatchByDSImmediately(null, list, false);
				list.clear();
			}
		}
		filereader.close();
		InsertSQLBuilder inisert = getSynLog(logid, _mainVO
				.getStringValue("id"), _jobid, _datadate, _mainVO
				.getStringValue("iname"), fileAbsolutePath,
				Syn_Log_Exec_Type_TBSJ, Syn_Log_Exec_State_Success, "����ͬ��"
						+ rowcount + "����¼.");
		list.add(inisert); // ������־
		UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
				"sal_data_interface_main");
		updateMainTable
				.setWhereCondition(" id=" + _mainVO.getStringValue("id"));
		updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
		list.add(updateMainTable);
		dmo.executeBatchByDSImmediately(null, list, false);
			}
		}else{
			File file = new File(fileAbsolutePath);
			if (!file.exists()) {
				return str=fileAbsolutePath + "�ļ������ڣ����ϴ�����";
			}
//			String str = tbutil.readFromInputStreamToStr(new FileInputStream(file));
//			String[] allline = str.split("\n");
//			for (int i = 0; i < allline.length; i++) {
//				String values[] = allline[i].split(splitStr);
//				for (int j = 0; j < values.length; j++) {
//					System.out.println(values[j]);
//				}
//			}
			InputStreamReader read = new InputStreamReader(
					new FileInputStream(file), datacode);
			BufferedReader filereader = new BufferedReader(read);
			String strcontent = null;

			String tableName = _mainVO.getStringValue("tablename");
			// �����Ƿ����,��������жϽṹ�Ƿ�仯
			try {
				dmo.getStringValueByDS(null, "select * from " + tableName
						+ " where 1=2");
				String[][] different = compareDBTOConfig(_mainVO, _childItemVOs);// ����ṹ��û�б仯��һ���ǲ���ġ�
				if (different != null && different.length > 0) {
					List sql = new ArrayList();
					StringBuffer descr = new StringBuffer();
					for (int i = 0; i < different.length; i++) {
						sql.add(different[i][3]);
						descr.append(different[i][3] + "\r\n");
					}
					dmo.executeBatchByDSImmediately(null, sql, false); //
					InsertSQLBuilder logsql = getSynLog(null, _mainVO
							.getStringValue("id"), _jobid, _datadate, _mainVO
							.getStringValue("iname"), "", Syn_Log_Exec_Type_XGB,
							Syn_Log_Exec_State_Success, "ϵͳ���ֱ�ṹ�仯���Զ�ִ�нű�:\r\n"
									+ descr);
					dmo.executeUpdateByDSImmediately(null, logsql);
				}
			} catch (Exception ex) { // �����ڣ�ֱ�Ӵ���
				createTableByConfig(_mainVO.getStringValue("id"));
			}

			// ������û����ʷ�����еĻ�����ɾ������Ȼ����ִ��.
			HashVO[] old_his = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_log where filepath like '%"
							+ _folder + "%' and exectype='"
							+ Syn_Log_Exec_Type_TBSJ + "' and parentid="
							+ _mainVO.getStringValue("id")
							+ " order by exectime desc"); // ������ͬ���ļ�¼
			if (old_his != null && old_his.length > 0) {
				List list = new ArrayList();
				StringBuffer msg = new StringBuffer();
				for (int i = 0; i < old_his.length; i++) {
					HashVO loghisvo = old_his[i];
					msg.append(loghisvo.getStringValue("exectime") + ",");
					String deloldsql = "delete from "
							+ _mainVO.getStringValue("tablename")
							+ " where logid = " + loghisvo.getStringValue("id");
					list.add(deloldsql);
				}
				String newlogid = dmo.getSequenceNextValByDS(null,
						"S_SAL_DATA_INTERFACE_LOG");
				InsertSQLBuilder dellogsql = getSynLog(newlogid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_SCSJ,
						Syn_Log_Exec_State_Success, "ϵͳ���ָýӿ��Ѿ���["
								+ msg.substring(0, msg.length() - 1)
								+ "]ͬ����,��������ʷ����.");
				list.add(dellogsql);
				dmo.executeBatchByDSImmediately(null, list);
			}
			ArrayList list = new ArrayList();
			//
			int rowcount = 0; // ��¼���ɶ���������.
			String logid = dmo.getSequenceNextValByDS(null,
					"S_SAL_DATA_INTERFACE_LOG");
			String countid=null;
			if(soleflg ){
				countid=dmo.getStringValueByDS(null,"select max(id) from "+tableName);
				if(countid.equals(null) || countid==null || countid.equals("") || countid.equals(" "))
					countid="0";
				}else{
					countid=String.valueOf(Integer.parseInt(countid)+10);
			}
			int countvalue=Integer.parseInt(countid);
			while ((strcontent = filereader.readLine()) != null) { // ���ж�ȡ�������߼������ò����滻�����԰��ж�ȡ��Ҳ������xml��ȡ��
				String itemvalues[] = strcontent.split(splitStr); // ��ȡʵ��ֵ
				rowcount++;
				if (itemvalues.length != _childItemVOs.length) {
					filereader.close(); // һ��Ҫ�ر���
					InsertSQLBuilder inisert = getSynLog(logid, _mainVO
							.getStringValue("id"), _jobid, _datadate, _mainVO
							.getStringValue("iname"), fileAbsolutePath,
							Syn_Log_Exec_Type_Other, Syn_Log_Exec_State_Fail,
							fileAbsolutePath + "�ļ��ֶγ��Ⱥ�ϵͳ���ò�һֱ.�ļ�["
									+ itemvalues.length + "]��,ϵͳ����["
									+ _childItemVOs.length + "]��");
					dmo.executeUpdateByDSImmediately(null, inisert);
					return str=fileAbsolutePath + "�ļ��ֶγ��Ⱥ�ϵͳ���ò�һֱ.�ļ�["
					+ itemvalues.length + "]��,ϵͳ����[" + _childItemVOs.length
					+ "]��";
				}
				InsertSQLBuilder insert = new InsertSQLBuilder(tableName);
				insert.putFieldValue("logid", logid);
				if(soleflg){
					countvalue++;
					insert.putFieldValue("id", countvalue);
				}
				for (int i = 0; i < itemvalues.length; i++) {
					String value = itemvalues[i];
					value = value.replace("\"", "").trim();
					insert.putFieldValue(stbutil.convertIntColToEn(i + 1,false), value);
				}
				list.add(insert);
				if (list.size() == 5000) { // 5000���ύһ�Ρ��������ܸ�����
					dmo.executeBatchByDSImmediately(null, list, false);
					list.clear();
				}
			}
			filereader.close();
			InsertSQLBuilder inisert = getSynLog(logid, _mainVO
					.getStringValue("id"), _jobid, _datadate, _mainVO
					.getStringValue("iname"), fileAbsolutePath,
					Syn_Log_Exec_Type_TBSJ, Syn_Log_Exec_State_Success, "����ͬ��"
							+ rowcount + "����¼.");
			list.add(inisert); // ������־
			UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
					"sal_data_interface_main");
			updateMainTable
					.setWhereCondition(" id=" + _mainVO.getStringValue("id"));
			updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
			list.add(updateMainTable);
			dmo.executeBatchByDSImmediately(null, list, false);
			return "["+filename+"]�ļ�ͬ���ɹ�";
		}

	return str;
		
	}
}
