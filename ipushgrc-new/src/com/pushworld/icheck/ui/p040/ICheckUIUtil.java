package com.pushworld.icheck.ui.p040;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.ZipFileUtil;

/**
 * �µļ��ģ��ͻ��˹�����-�½����� �����/2016-08-31��
 * 
 * @author lcj
 * 
 */
public class ICheckUIUtil {

	private static String datesum;
	private String cachePath = System.getProperty("ClientCodeCache"); // �õ��ͻ��˻���λ�á�
	private TBUtil tbUtil = new TBUtil();
	public static int IMPORTDATA_SUCCESS = 1;// ����ɹ�
	public static int IMPORTDATA_FAIL = 0;// ����ʧ��
	public static int IMPORTDATA_NOSELECTFILE = -1;// ����ʱδѡ���ļ�
	private boolean import_flag = true; // �����ļ��ɹ���־

	/*
	 * idΪ��ֵ 14λ+2λ
	 */
	public static String getSequenceNextVal() {
		String nano = System.nanoTime() + "";
		if (nano.length() >= 14) {
			nano = nano.substring(0, 14);
		}
		String new_id = "-" + nano + getRandom();
		return new_id;
	}

	/**
	 * �� ����ֵ = ��+100*��+�� = 2012+100*10+24 4λ
	 * 
	 * @return
	 */
	private static String getDateSum() {
		if (datesum == null) {
			String date = UIUtil.getCurrDate();
			String[] d = TBUtil.getTBUtil().split(date, "-");
			int year = Integer.parseInt(d[0]);
			int month = Integer.parseInt(d[1]);
			int day = Integer.parseInt(d[2]);
			datesum = (year + month * 100 + day) + "";
		}
		return datesum;
	}

	// �򵥻�����������ǰ����˸��ţ��ʽ����������Ϊ2λ�������ܱ�֤�����16λ�����ϸ��ţ�17λ��λ���ٳ���idֵ��˫�����޷���ѯ�����ˣ�������mysql�ײ�bug�����/2016-09-05��
	private static String getRandom() {
		long num = Math.round(Math.random() * 99);
		if (num < 10) {
			return "0" + num;
		} else {
			return "" + num;
		}
	}

	/**
	 * ������Ϊ�������ݣ���������������浼����������ſɴ�����ĵ����浼��
	 * ����浼������ck_scheme_impl,ck_scheme_implement
	 * ,ck_scheme,ck_member_work,ck_manuscript_design
	 * ,pub_user,pub_corp_dept,pub_post,pub_user_post,cmp_cmpfile,cmp_risk
	 * �����浼����
	 * ��ck_scheme_impl,ck_scheme_implement,ck_problem_info,cmp_wardevent_cust
	 * ,cmp_wardevent_user
	 * ,cmp_event,ck_scheme,ck_member_work,ck_manuscript_design,ck_record
	 * 
	 * ����->����浼�����ck_scheme_impl��������ɱ༭�򿽱����ʵʩ������,���ʵʩ����ck_scheme_implement��,�����
	 * ��ck_problem_info��,Υ���¼��漰�ͻ���cmp_wardevent_cust��,Υ���¼��漰Ա����
	 * cmp_wardevent_user��,Υ���¼�����cmp_event��,�����浼��֪ͨ��ȹ�������ck_record��
	 * ����->�����浼�����ck_scheme_impl
	 * ,ck_scheme_implement,ck_problem_info,cmp_wardevent_cust
	 * ,cmp_wardevent_user
	 * ,cmp_event,ck_scheme,ck_member_work,ck_manuscript_design,ck_record
	 * 
	 * @param _window
	 * @param _path
	 * @param _type
	 *            0-����浼�����ݣ�1-�����浼������
	 * @return
	 * @throws Exception
	 * @param _window
	 * @param _path
	 * @param _schemeid
	 *            ����
	 * @param _deptid
	 *            ��������
	 * @param _deptname
	 *            ������������
	 * @param _implids
	 *            �����浼����Ҫ��ck_scheme_impl.id ��
	 * @param _type
	 *            0-����浼�����ݣ�1-�����浼������
	 * @return
	 * @throws Exception
	 */
	public boolean exportDataByCondition(SplashWindow _splash, String _path,
			String _schemeid, String _deptid, String _deptname,
			String _implids, int _type) {
		ArrayList tableList = new ArrayList();
		if (_type == 0) {// ����浼��������ָ�����ʵʩ����
			tableList.add(new String[] {
					"ck_scheme_impl",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });// ���ʵʩ
			tableList.add(new String[] {
					"ck_scheme_implement",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });// ���ʵʩ
			tableList.add(new String[] { "ck_scheme", "",
					"id = '" + _schemeid + "'" });// ��鷽����ѡ�񵼳�
			tableList.add(new String[] { "ck_member_work", "",
					"schemeid = '" + _schemeid + "'" });// ��鷽����Ա�ֹ���ѡ�񵼳�
			tableList.add(new String[] { "ck_manuscript_design", "",
					"schemeid = '" + _schemeid + "'" });// ���׸�
			tableList.add(new String[] { "pub_user", "", "" });// ��Ա����Ϣȫ������
			tableList.add(new String[] { "pub_corp_dept", "", "" });
			tableList.add(new String[] { "pub_post", "", "" });
			tableList.add(new String[] { "pub_user_post", "", "" });
			tableList.add(new String[] { "cmp_cmpfile", "", "" });//
			tableList.add(new String[] { "cmp_risk", "", "" });
			tableList.add(new String[] { "ck_retrival", "",
					"schemeid = '" + _schemeid + "'" });// ��zzl 2017-11-13����������
			String[] imp;
			try {
				imp = UIUtil.getStringArrayFirstColByDS(null,"select id from ck_problem_info where deptid = '" + _deptid + "' and schemeid = '" + _schemeid
								+ "'");
				if(imp.length>0){//[zzl 2017-11-23] ��ʱ����������ϲ���¼��׸�
					tableList.add(new String[] { "ck_problem_info", "",
							" deptid='"+_deptid+"' and schemeid = '" + _schemeid + "'" });
				}
			} catch (WLTRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {// �����浼������ָ�����ʵʩ����
			// cmp_wardevent_cust �� cmp_wardevent_user �ӱ������cmp_event
			// ǰ���ӣ��ȵ��룬��ɸ���cmp_event.deptid��cmp_event.schemeid
			// �ҵ���Ҫɾ�����ӱ���¼�����/2016-09-05��
			tableList.add(new String[] { "ck_scheme_impl", "",
					"id in(" + _implids + ")" });// ���ʵʩ����
			tableList.add(new String[] { "ck_scheme_implement", "",
					"implid in(" + _implids + ")" });// ���ʵʩ�ӱ�
			tableList.add(new String[] { "ck_problem_info", "",
					"implid in(" + _implids + ")" });// ����
			tableList.add(new String[] {
					"cmp_wardevent_cust",
					"",
					"cmp_wardevent_id in (select id from cmp_event where implid in("
							+ _implids + ") )" });// Υ���¼��漰�ͻ�
			tableList.add(new String[] {
					"cmp_wardevent_user",
					"",
					"cmp_wardevent_id in (select id from cmp_event where implid in("
							+ _implids + ") )" });// Υ���¼��漰Ա��
			tableList.add(new String[] { "cmp_event", "",
					"implid in(" + _implids + ")" });// Υ���¼�
			tableList.add(new String[] { "ck_scheme", "",
					"id = '" + _schemeid + "'" });// ��鷽����ѡ�񵼳�
			tableList.add(new String[] { "ck_member_work", "",
					"schemeid = '" + _schemeid + "'" });// ��鷽����Ա�ֹ���ѡ�񵼳�
			tableList.add(new String[] { "ck_manuscript_design", "",
					"schemeid = '" + _schemeid + "'" });// ���׸�
			tableList.add(new String[] { "ck_record", "",
					"implid in(" + _implids + ")" });// ��¼�����浼�����������ʵȷ���顢��������֪ͨ�顢������ʾ֪ͨ��Ĺ����������/2016-09-29��
			tableList.add(new String[] { "ck_dyqindan", "",
					"implid in(" + _implids + ")" });
			tableList.add(new String[] { "ck_retrival", "",
					"schemeid = '" + _schemeid + "'" });// ��zzl
			// 2017-11-13�����������嵥
		}

		StringBuffer sb = new StringBuffer(); // дmain.xml���ļ�
		String version = "1.0";
		sb.append("<?xml version=\"" + version + "\" encoding=\"GBK\"?>\r\n");
		IPushGRCServiceIfc service = null;
		try {
			service = (IPushGRCServiceIfc) UIUtil
					.lookUpRemoteService(IPushGRCServiceIfc.class);
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
		String p = "exportDB" + File.separator + System.currentTimeMillis();
		List<String> sql_insert_history = new ArrayList<String>();
		String table_sb = "";

		for (int i = 0; i < tableList.size(); i++) {
			String[] table = (String[]) tableList.get(i);
			StringBuffer tableSB = new StringBuffer();
			String tablename = table[0];
			String join = table[1];
			String condition = table[2];
			tableSB.append("<table name=\"" + tablename + "\">\r\n");
			try {
				// ����������Ŀ¼!!!
				String str_newPath = cachePath + File.separator + p; // ���ϱ���!
				File filenewdir = new File(str_newPath); //
				if (!filenewdir.exists()) {
					filenewdir.mkdirs(); // ����Ŀ¼!!!�Ժ��ٸ����ǰ�
				}
				int li_beginNo = 0; // ��ʼ��!
				int li_cout = 0; //

				StringBuilder con_sql = new StringBuilder("select count("
						+ tablename + ".id) from " + tablename + " ");
				if (join != null && !"".equals(join)) {
					con_sql.append(join);
				}
				if (condition != null && !condition.equals("")) {
					if (condition.trim().indexOf("where") != 0) {
						con_sql.append(" where " + condition);
					} else {
						con_sql.append(" " + condition);
					}

				}
				int li_countall = Integer.parseInt(UIUtil.getStringValueByDS(
						null, con_sql.toString())); //
				if (li_countall == 0) {
					continue;
				}
				int li_downedCount = 0; //
				while (1 == 1) { // ��ѭ��
					long ll_1 = System.currentTimeMillis(); //
					if (li_beginNo >= li_countall) {
						break;
					}
					HashMap returnMap = service.getXMlFromTable500Records(null,
							tablename, li_beginNo, table[1], table[2]);
					if (returnMap == null) { // ���Ϊ������ֱ�ӷ���
						break; //
					}
					int li_recordCount = (Integer) returnMap.get("��¼��"); // ʵ�ʵļ�¼��,Ӧ�ó����һҳ��,�����Ķ���500
					li_downedCount = li_downedCount + li_recordCount; //
					int li_perCent = ((li_downedCount * 100) / li_countall); // �ٷֱ�!
					// 50/100
					li_beginNo = (Integer) returnMap.get("������"); // ʵ�����ݵĽ�����!
					String str_xml = (String) returnMap.get("����"); //
					li_cout++; //
					String str_fileName = tablename + "_" + (100000 + li_cout)
							+ ".xml"; //
					FileOutputStream fileOut = new FileOutputStream(str_newPath
							+ "//" + str_fileName, false); //
					tbUtil.writeStrToOutputStream(fileOut, str_xml); // ������ļ�
					long ll_2 = System.currentTimeMillis(); //
					tableSB.append("<file path=\"" + str_fileName
							+ "\" count=\"" + li_recordCount + "\"/>\r\n");
					_splash.setWaitInfo("����[" + str_fileName + "],��ʱ["
							+ (ll_2 - ll_1) + "]!!\r\n������ɱ���[" + li_downedCount
							+ "/" + li_countall + "=" + li_perCent + "%],�ܱ���["
							+ (i + 1) + "/" + tableList.size() + "]"); //
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
				return false;
			}
			tableSB.append("</table>\r\n");
			table_sb += tableSB.toString();
		}
		StringBuffer rootContent = new StringBuffer();
		rootContent.append(" createdate=\"" + UIUtil.getCurrTime()
				+ "\" creator=\""
				+ ClientEnvironment.getCurrSessionVO().getLoginUserId() + "\""
				+ " creatorname=\""
				+ ClientEnvironment.getCurrSessionVO().getLoginUserName()
				+ "\"");
		rootContent.append(" userdept=\""
				+ ClientEnvironment.getCurrLoginUserVO().getBlDeptId()
				+ "\" userdeptname=\""
				+ ClientEnvironment.getCurrLoginUserVO().getDeptname()
				+ "\" schemeid =\"" + _schemeid + "\" deptid =\"" + _deptid
				+ "\" deptname =\"" + _deptname + "\" implids=\"" + _implids
				+ "\" ");// �����¼implids

		sb.append("<root " + rootContent.toString() + ">\r\n");
		sb.append(table_sb);
		sb.append("</root>");
		FileOutputStream fileout = null;
		try {
			_splash.setWaitInfo(" ����main.xml���ļ�!");
			fileout = new FileOutputStream(cachePath + File.separator + p
					+ File.separator + "main.xml", false);
			_splash.setWaitInfo(" ���ڱ��浼����ʷ...");
			UIUtil.executeBatchByDS(null, sql_insert_history);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		tbUtil.writeStrToOutputStream(fileout, sb.toString());
		if (_type == 1) {// ck_scheme_implement.descr ck_problem_info.appth
			// cmp_event.attachfile
			// ��������Ҫ�������������/2016-09-07��
			String str_newPath = cachePath + File.separator + p; // �����ļ���
			File filenewdir = new File(str_newPath); //
			if (!filenewdir.exists()) {
				filenewdir.mkdirs(); // ����Ŀ¼
			}

			// ���ʵʩ
			try {
				String[][] impfiles = UIUtil
						.getStringArrayByDS(
								null,
								"select id,descr from ck_scheme_implement where descr is not null and deptid = '"
										+ _deptid
										+ "' and schemeid = '"
										+ _schemeid + "'");
				if (impfiles != null && impfiles.length > 0) {
					for (int j = 0; j < impfiles.length; j++) {
						String id = impfiles[j][0];
						String file = impfiles[j][1];
						String[] files = tbUtil.split(file, ";");
						for (int k = 0; k < files.length; k++) {
							filenewdir = new File(str_newPath
									+ File.separator
									+ files[k].substring(0, files[k]
											.lastIndexOf("/"))); //
							if (!filenewdir.exists()) {
								filenewdir.mkdirs(); // ����Ŀ¼
							}
							String name = UIUtil.downLoadFile("/upload",
									files[k], false, filenewdir
											.getAbsolutePath(), files[k]
											.substring(files[k]
													.lastIndexOf("/")), true); //
							if (name == null || name.trim().equals("")) {
							} else {
							}
						}
					}
				}
				// ����
				String[][] problemfiles = UIUtil.getStringArrayByDS(null,
						"select id,appth from ck_problem_info where appth is not null and deptid = '"
								+ _deptid + "' and schemeid = '" + _schemeid
								+ "'");
				if (problemfiles != null && problemfiles.length > 0) {
					for (int j = 0; j < problemfiles.length; j++) {
						String id = problemfiles[j][0];
						String file = problemfiles[j][1];
						String[] files = tbUtil.split(file, ";");
						for (int k = 0; k < files.length; k++) {
							filenewdir = new File(str_newPath
									+ File.separator
									+ files[k].substring(0, files[k]
											.lastIndexOf("/"))); //
							if (!filenewdir.exists()) {
								filenewdir.mkdirs(); // ����Ŀ¼
							}
							String name = UIUtil.downLoadFile("/upload",
									files[k], false, filenewdir
											.getAbsolutePath(), files[k]
											.substring(files[k]
													.lastIndexOf("/")), true); //
							if (name == null || name.trim().equals("")) {
							} else {
							}
						}
					}
				}
				// Υ���¼�
				String[][] eventfiles = UIUtil
						.getStringArrayByDS(
								null,
								"select id,attachfile from cmp_event where attachfile is not null and deptid = '"
										+ _deptid
										+ "' and schemeid = '"
										+ _schemeid + "'");
				if (eventfiles != null && eventfiles.length > 0) {
					for (int j = 0; j < eventfiles.length; j++) {
						String id = eventfiles[j][0];
						String file = eventfiles[j][1];
						String[] files = tbUtil.split(file, ";");
						for (int k = 0; k < files.length; k++) {
							filenewdir = new File(str_newPath
									+ File.separator
									+ files[k].substring(0, files[k]
											.lastIndexOf("/"))); //
							if (!filenewdir.exists()) {
								filenewdir.mkdirs(); // ����Ŀ¼
							}
							String name = UIUtil.downLoadFile("/upload",
									files[k], false, filenewdir
											.getAbsolutePath(), files[k]
											.substring(files[k]
													.lastIndexOf("/")), true); //
							if (name == null || name.trim().equals("")) {
							} else {
							}
						}
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		try {
			ZipFileUtil zip = new ZipFileUtil();
			_splash.setWaitInfo(" ���ڴ��!���Ե�...");
			String saveFileName = _deptname + "_"
					+ ClientEnvironment.getInstance().getLoginUserName() + "_"
					+ UIUtil.getCurrDate() + "�׸����ݰ�.zip";
			zip.zip(_path + File.separator + saveFileName, cachePath
					+ File.separator + p);
			File file = new File(cachePath + File.separator + p);
			if (file.exists() && file.isDirectory()) {
				File[] files = file.listFiles();
				if (files.length == 0) {
					file.delete();
				}
				for (int j = 0; j < files.length; j++) {
					files[j].delete();
				}
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * �����-��������̨��
	 * 
	 * @param _splash
	 * @param _path
	 * @param _schemeid
	 * @param _deptid
	 * @param _deptname
	 * @param _type
	 * @return
	 */

	public boolean exportAdjust(SplashWindow _splash, String _path,
			String _schemeid, String _deptid, String _deptname, int _type) {
		ArrayList tableList = new ArrayList();
		if (_type == 0) {// �����
			tableList.add(new String[] {
					"ck_problem_info",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });// ���ʵʩ
			tableList
					.add(new String[] {
							"cmp_wardevent_cust",
							"",
							"cmp_wardevent_id in (select id from cmp_event where deptid = '"
									+ _deptid + "' and schemeid = '"
									+ _schemeid + "')" });// Υ���¼��漰�ͻ�
			tableList
					.add(new String[] {
							"cmp_wardevent_user",
							"",
							"cmp_wardevent_id in (select id from cmp_event where deptid = '"
									+ _deptid + "' and schemeid = '"
									+ _schemeid + "')" });// Υ���¼��漰Ա��
			tableList.add(new String[] {
					"cmp_event",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });// Υ���¼�
			tableList.add(new String[] { "ck_scheme", "",
					"id = '" + _schemeid + "'" });// ��鷽����ѡ�񵼳�
			tableList.add(new String[] { "ck_member_work", "",
					"schemeid = '" + _schemeid + "'" });// ��鷽����Ա�ֹ���ѡ�񵼳�
			tableList.add(new String[] { "pub_user", "", "" });// ��Ա����Ϣȫ������
			tableList.add(new String[] { "pub_corp_dept", "", "" });
			tableList.add(new String[] { "pub_post", "", "" });
			tableList.add(new String[] { "pub_user_post", "", "" });
			tableList.add(new String[] { "cmp_cmpfile", "", "" });//
			tableList.add(new String[] { "cmp_risk", "", "" });
			tableList.add(new String[] {
					"ck_scheme_impl",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });
		} else {// ������
			tableList.add(new String[] {
					"ck_problem_info",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });// ����
		}

		StringBuffer sb = new StringBuffer(); // дmain.xml���ļ�
		String version = "1.0";
		sb.append("<?xml version=\"" + version + "\" encoding=\"GBK\"?>\r\n");
		IPushGRCServiceIfc service = null;
		try {
			service = (IPushGRCServiceIfc) UIUtil
					.lookUpRemoteService(IPushGRCServiceIfc.class);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String p = "exportDB" + File.separator + System.currentTimeMillis();
		List<String> sql_insert_history = new ArrayList<String>();
		String table_sb = "";

		for (int i = 0; i < tableList.size(); i++) {
			String[] table = (String[]) tableList.get(i);
			StringBuffer tableSB = new StringBuffer();
			String tablename = table[0];
			String join = table[1];
			String condition = table[2];
			tableSB.append("<table name=\"" + tablename + "\">\r\n");
			try {
				// ����������Ŀ¼!!!
				String str_newPath = cachePath + File.separator + p; // ���ϱ���!
				File filenewdir = new File(str_newPath); //
				if (!filenewdir.exists()) {
					filenewdir.mkdirs(); // ����Ŀ¼!!!�Ժ��ٸ����ǰ�
				}
				int li_beginNo = 0; // ��ʼ��!
				int li_cout = 0; //

				StringBuilder con_sql = new StringBuilder("select count("
						+ tablename + ".id) from " + tablename + " ");
				if (join != null && !"".equals(join)) {
					con_sql.append(join);
				}
				if (condition != null && !condition.equals("")) {
					if (condition.trim().indexOf("where") != 0) {
						con_sql.append(" where " + condition);
					} else {
						con_sql.append(" " + condition);
					}

				}
				int li_countall = Integer.parseInt(UIUtil.getStringValueByDS(
						null, con_sql.toString())); //
				if (li_countall == 0) {
					continue;
				}
				int li_downedCount = 0; //
				while (1 == 1) { // ��ѭ��
					long ll_1 = System.currentTimeMillis(); //
					if (li_beginNo >= li_countall) {
						break;
					}
					HashMap returnMap = service.getXMlFromTable500Records(null,
							tablename, li_beginNo, table[1], table[2]);
					if (returnMap == null) { // ���Ϊ������ֱ�ӷ���
						break; //
					}
					int li_recordCount = (Integer) returnMap.get("��¼��"); // ʵ�ʵļ�¼��,Ӧ�ó����һҳ��,�����Ķ���500
					li_downedCount = li_downedCount + li_recordCount; //
					int li_perCent = ((li_downedCount * 100) / li_countall); // �ٷֱ�!
					// 50/100
					li_beginNo = (Integer) returnMap.get("������"); // ʵ�����ݵĽ�����!
					String str_xml = (String) returnMap.get("����"); //
					li_cout++; //
					String str_fileName = tablename + "_" + (100000 + li_cout)
							+ ".xml"; //
					FileOutputStream fileOut = new FileOutputStream(str_newPath
							+ "//" + str_fileName, false); //
					tbUtil.writeStrToOutputStream(fileOut, str_xml); // ������ļ�
					long ll_2 = System.currentTimeMillis(); //
					tableSB.append("<file path=\"" + str_fileName
							+ "\" count=\"" + li_recordCount + "\"/>\r\n");
					_splash.setWaitInfo("����[" + str_fileName + "],��ʱ["
							+ (ll_2 - ll_1) + "]!!\r\n������ɱ���[" + li_downedCount
							+ "/" + li_countall + "=" + li_perCent + "%],�ܱ���["
							+ (i + 1) + "/" + tableList.size() + "]"); //
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
			tableSB.append("</table>\r\n");
			table_sb += tableSB.toString();
		}
		StringBuffer rootContent = new StringBuffer();
		rootContent.append(" createdate=\"" + UIUtil.getCurrTime()
				+ "\" creator=\""
				+ ClientEnvironment.getCurrSessionVO().getLoginUserId() + "\""
				+ " creatorname=\""
				+ ClientEnvironment.getCurrSessionVO().getLoginUserName()
				+ "\"");
		rootContent.append(" userdept=\""
				+ ClientEnvironment.getCurrLoginUserVO().getBlDeptId()
				+ "\" userdeptname=\""
				+ ClientEnvironment.getCurrLoginUserVO().getDeptname()
				+ "\" schemeid =\"" + _schemeid + "\" deptid =\"" + _deptid
				+ "\" deptname =\"" + _deptname + "\" ");

		sb.append("<root " + rootContent.toString() + ">\r\n");
		sb.append(table_sb);
		sb.append("</root>");
		FileOutputStream fileout = null;
		try {
			_splash.setWaitInfo(" ����main.xml���ļ�!");
			fileout = new FileOutputStream(cachePath + File.separator + p
					+ File.separator + "main.xml", false);
			_splash.setWaitInfo(" ���ڱ��浼����ʷ...");
			UIUtil.executeBatchByDS(null, sql_insert_history);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tbUtil.writeStrToOutputStream(fileout, sb.toString());
		if (_type == 0) {// ck_problem_info.appth cmp_event.attachfile
			// �������Ҫ�������������/2016-09-21��
			String str_newPath = cachePath + File.separator + p
					+ File.separator + "upload"; // �����ļ��У���Ҫ��һ��upload����������ϴ��ĸ�����û��icheck�ļ���
			File filenewdir = new File(str_newPath); //
			if (!filenewdir.exists()) {
				filenewdir.mkdirs(); // ����Ŀ¼
			}
			try {
				// ����
				String[][] problemfiles = UIUtil.getStringArrayByDS(null,
						"select id,appth from ck_problem_info where appth is not null and deptid = '"
								+ _deptid + "' and schemeid = '" + _schemeid
								+ "'");
				if (problemfiles != null && problemfiles.length > 0) {
					for (int j = 0; j < problemfiles.length; j++) {
						String id = problemfiles[j][0];
						String file = problemfiles[j][1];
						String[] files = tbUtil.split(file, ";");
						for (int k = 0; k < files.length; k++) {
							filenewdir = new File(str_newPath
									+ File.separator
									+ files[k].substring(0, files[k]
											.lastIndexOf("/"))); //
							if (!filenewdir.exists()) {
								filenewdir.mkdirs(); // ����Ŀ¼
							}
							String name = UIUtil.downLoadFile("/upload",
									files[k], false, filenewdir
											.getAbsolutePath(), files[k]
											.substring(files[k]
													.lastIndexOf("/")), true); //
							if (name == null || name.trim().equals("")) {
							} else {
							}
						}
					}
				}
				// Υ���¼�
				String[][] eventfiles = UIUtil
						.getStringArrayByDS(
								null,
								"select id,attachfile from cmp_event where attachfile is not null and deptid = '"
										+ _deptid
										+ "' and schemeid = '"
										+ _schemeid + "'");
				if (eventfiles != null && eventfiles.length > 0) {
					for (int j = 0; j < eventfiles.length; j++) {
						String id = eventfiles[j][0];
						String file = eventfiles[j][1];
						String[] files = tbUtil.split(file, ";");
						for (int k = 0; k < files.length; k++) {
							filenewdir = new File(str_newPath
									+ File.separator
									+ files[k].substring(0, files[k]
											.lastIndexOf("/"))); //
							if (!filenewdir.exists()) {
								filenewdir.mkdirs(); // ����Ŀ¼
							}
							String name = UIUtil.downLoadFile("/upload",
									files[k], false, filenewdir
											.getAbsolutePath(), files[k]
											.substring(files[k]
													.lastIndexOf("/")), true); //
							if (name == null || name.trim().equals("")) {
							} else {
							}
						}
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			ZipFileUtil zip = new ZipFileUtil();
			_splash.setWaitInfo(" ���ڴ��!���Ե�...");
			String saveFileName = _deptname + "_"
					+ ClientEnvironment.getInstance().getLoginUserName() + "_"
					+ UIUtil.getCurrDate() + "����̨��.zip";
			zip.zip(_path + File.separator + saveFileName, cachePath
					+ File.separator + p);
			File file = new File(cachePath + File.separator + p);
			if (file.exists() && file.isDirectory()) {
				File[] files = file.listFiles();
				if (files.length == 0) {
					file.delete();
				}
				for (int j = 0; j < files.length; j++) {
					files[j].delete();
				}
				file.delete();
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// ͨ�����������ݣ���ǰ�������ظ���IDͳͳ�ɵ���Ȼ��ȫ�����²��룬����Щ�ӱ���������Ҫȫ��ɾ�����ʸ�Ϊ���ݱ��ж�ɾ���߼���
	// _titleѡ���ļ�����ʾ���ݣ�_type��0-����浼�����ݣ�1-�����浼������
	// return ֵ -1 ��ѡ���ļ������⡣1�ɹ� 0ʧ��
	public int importDataByPackage(Container _parent, String _title,
			final int _nettype) {
		return importDataByPackage(_parent, _title, _nettype, 0);
	}

	/**
	 * 
	 * @param _parent
	 * @param _title
	 *            ��ʾ����
	 * @param _nettype
	 *            �������ͣ�0-����浼�����ݣ�1-�����浼������
	 * @param _filetype
	 *            �������ݣ�0-���ʵʩ��1-����̨��
	 * @return
	 */
	public int importDataByPackage(Container _parent, String _title,
			final int _nettype, final int _filetype) {
		try {
			String p = "importDB" + File.separator + _nettype + File.separator
					+ System.currentTimeMillis();
			final HashMap conditionMap = new HashMap();
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(true); // [zzl2017-11-23����ϣ��������������]���Զ�ѡ
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"���ݰ�(*.zip,*.rar)", "zip", "rar");
			chooser.addChoosableFileFilter(filter);
			int flag = chooser.showOpenDialog(_parent);
			if (flag == 1) {
				return this.IMPORTDATA_NOSELECTFILE;
			}
			File file[] = chooser.getSelectedFiles();
			for (int i = 0; i < file.length; i++) {
				int a = 0;
				if (file == null) {
					return this.IMPORTDATA_NOSELECTFILE;
				}
				final String currPath = cachePath + p;
				File filenewdir = new File(currPath); //
				if (!filenewdir.exists()) {
					filenewdir.mkdirs(); // ����Ŀ¼!!!�Ժ��ٸ����ǰ�
				}
				ZipFileUtil.unzip(file[i].getAbsolutePath(), currPath); // ��ͽ�ѹһ��
				java.io.File mainFile = new java.io.File(currPath
						+ File.separator + "main.xml");
				if (!mainFile.exists()) {
					MessageBox.show(_parent, "ѹ������û��main.xml���ļ������ܵ��룡");
					return this.IMPORTDATA_NOSELECTFILE;
				}
				SAXBuilder builder = new SAXBuilder();
				org.jdom.Document rootNode = builder.build(mainFile);
				Element element = rootNode.getRootElement();
				String schemeid = element.getAttributeValue("schemeid");
				String deptid = element.getAttributeValue("deptid");
				new SplashWindow(_parent, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						try {
							// ������ʵʩ
							import_flag = hbImportImplementData(
									(SplashWindow) e.getSource(),
									currPath, _nettype);

						} catch (Exception e1) {
							e1.printStackTrace();
							import_flag = false;// ����ʧ�ܱ�־��
						}
					}
				});
				
//				if (_nettype == 0) {//
//					String count = UIUtil
//							.getStringValueByDS(
//									null,
//									"select count(*) from ck_scheme_implement where schemeid='"
//											+ schemeid
//											+ "' and deptid ='"
//											+ deptid
//											+ "' and (control is not null or result is not null)");
//					if (count != null && !"0".equals(count)) {// ��zzl//
//																// 2016-09-01��
//						a = MessageBox.showConfirmDialog(_parent, "��"
//								+ file[i].getName() + "������Ǻϲ����룡����񸲸ǵ��룡");
//						if (a == 0) {
//							new SplashWindow(_parent, new AbstractAction() {
//								public void actionPerformed(ActionEvent e) {
//									try {
//										// ������ʵʩ
//										import_flag = hbImportImplementData(
//												(SplashWindow) e.getSource(),
//												currPath, _nettype);
//
//									} catch (Exception e1) {
//										e1.printStackTrace();
//										import_flag = false;// ����ʧ�ܱ�־��
//									}
//								}
//							}, 600, 130, 300, 300, false); // 
//							import_flag = true;
//						} else if (a == 1) {
//							new SplashWindow(_parent, new AbstractAction() {
//								public void actionPerformed(ActionEvent e) {
//									try {
//
//										import_flag = doImportImplementData(
//												(SplashWindow) e.getSource(),
//												currPath, _nettype);
//
//									} catch (Exception e1) {
//										e1.printStackTrace();
//										import_flag = false;// ����ʧ�ܱ�־��
//									}
//								}
//							}, 600, 130, 300, 300, false); // 
//						} else {
//							if (i == file.length - 1 && a == -1) {
//								return IMPORTDATA_NOSELECTFILE;
//							}
//							continue;
//						}
//					}
//				} else {
//					new SplashWindow(_parent, new AbstractAction() {
//						public void actionPerformed(ActionEvent e) {
//							try {
//								import_flag = doImportImplementData(
//										(SplashWindow) e.getSource(), currPath,
//										_nettype);
//							} catch (Exception e1) {
//								e1.printStackTrace();
//								import_flag = false;// ����ʧ�ܱ�־��
//							}
//						}
//					}, 600, 130, 300, 300, false); // 
//				}
//
			}
			if (import_flag) {
				return IMPORTDATA_SUCCESS;// ����ɹ�
			} else {
				return IMPORTDATA_FAIL;// ����ʧ��
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return IMPORTDATA_FAIL;// ����ʧ��
		}
	}

	/**
	 * ������Ϊ��������
	 * 
	 * ����浼������ck_scheme_impl,ck_scheme_implement,ck_scheme,ck_member_work,
	 * ck_manuscript_design
	 * ,pub_user,pub_corp_dept,pub_post,pub_user_post,cmp_cmpfile,cmp_risk
	 * �����浼����
	 * ��ck_scheme_impl,ck_scheme_implement,ck_problem_info,cmp_wardevent_cust
	 * ,cmp_wardevent_user
	 * ,cmp_event,ck_scheme,ck_member_work,ck_manuscript_design,ck_record
	 * 
	 * ����->����浼�����ck_scheme_impl��������ɱ༭�򿽱����ʵʩ������,���ʵʩ����ck_scheme_implement��,�����
	 * ��ck_problem_info��,Υ���¼��漰�ͻ���cmp_wardevent_cust��,Υ���¼��漰Ա����
	 * cmp_wardevent_user��,Υ���¼�����cmp_event��,�����浼��֪ͨ��ȹ�������ck_record��
	 * ����->�����浼�����ck_scheme_impl
	 * ,ck_scheme_implement,ck_problem_info,cmp_wardevent_cust
	 * ,cmp_wardevent_user
	 * ,cmp_event,ck_scheme,ck_member_work,ck_manuscript_design,ck_record
	 * 
	 * @param _window
	 * @param _path
	 * @param _type
	 *            0-����浼�����ݣ�1-�����浼������
	 * @return
	 * @throws Exception
	 *             zzl[2017-1-6]
	 */
	private boolean hbImportImplementData(SplashWindow _window, String _path,
			int _nettype) throws Exception {
		_window.setWaitInfo("���ڽ�ѹ���ݰ�...");
		SAXBuilder builder = new SAXBuilder();
		java.io.File mainFile = new java.io.File(_path + File.separator
				+ "main.xml");
		org.jdom.Document rootNode = builder.build(mainFile);
		Element element = rootNode.getRootElement();
		String schemeid = element.getAttributeValue("schemeid");
		String deptid = element.getAttributeValue("deptid");
		String implids = element.getAttributeValue("implids");// ���ʵʩ���������/2016-09-29��
		List tableFile = element.getChildren("table");
		try {
			for (int j = 0; j < tableFile.size(); j++) { // һ�ű�
				Element tableElement = (Element) tableFile.get(j);
				String tablename = tableElement.getAttributeValue("name")
						.toUpperCase(); // ����
				tablename = tablename.toUpperCase();
				if (_nettype == 0) {// ���������浼������
					if (!"ck_scheme_impl".equalsIgnoreCase(tablename)
							&& !"ck_scheme_implement"
									.equalsIgnoreCase(tablename)
							&& !"ck_problem_info".equalsIgnoreCase(tablename)
							&& !"cmp_wardevent_cust"
									.equalsIgnoreCase(tablename)
							&& !"cmp_wardevent_user"
									.equalsIgnoreCase(tablename)
							&& !"cmp_event".equalsIgnoreCase(tablename)
							&& !"ck_record".equalsIgnoreCase(tablename)) {
						continue;
					}
				}

				// ��������
				TableDataStruct tabstrct = UIUtil.getTableDataStructByDS(null,
						"select * from " + tablename + " where 1=2"); //
				HashSet<String> hstCols = new HashSet<String>(); // ��ŶԱ��ֶ�
				String[] str_cols = tabstrct.getHeaderName(); //
				for (int k = 0; k < str_cols.length; k++) {
					hstCols.add(str_cols[k].toUpperCase()); //
				}
				// ͬ���滻��
				List fileList = tableElement.getChildren("file");
				int fileSize = fileList.size();
				for (int k = 0; k < fileSize; k++) {
					Element fileElement = (Element) fileList.get(k);
					List<String> insertSqlList = new ArrayList<String>();
					java.io.File current_file = null;
					if (fileElement.getAttributeValue("path") != null
							&& !fileElement.getAttributeValue("path")
									.equals("")) {
						current_file = new java.io.File(_path + File.separator
								+ fileElement.getAttributeValue("path"));
					} else {
						break;
					}
					rootNode = builder.build(current_file);
					element = rootNode.getRootElement();
					List recordNodeList = element.getChildren("record");
					UpdateSQLBuilder updateSQL = null;
					InsertSQLBuilder insertSQL = null;
					for (int l = 0; l < recordNodeList.size(); l++) {
						updateSQL = new UpdateSQLBuilder(tablename);
						insertSQL = new InsertSQLBuilder(tablename);
						Element recodeElement = (Element) recordNodeList.get(l);
						List colList = recodeElement.getChildren("col");
						Element colElementid = (Element) colList.get(0);
						String colNameid = colElementid.getAttributeValue(
								"name").toUpperCase();
						String valueid = colElementid.getText();
						String[] rutue = UIUtil.getStringArrayFirstColByDS(
								null, "select id from " + tablename
										+ " where id='" + valueid + "'");
						if (rutue.length > 0) {
							for (int i = 1; i < colList.size(); i++) {
								Element colElement1 = (Element) colList.get(i);
								String colName1 = colElement1
										.getAttributeValue("name")
										.toUpperCase();
								String value1 = colElement1.getText();
								updateSQL.setWhereCondition("id=" + valueid);
								updateSQL.putFieldValue(colName1, value1);
							}
							insertSqlList.add(updateSQL.getSQL());
						} else {
							for (int s = 0; s < colList.size(); s++) {
								Element colElement = (Element) colList.get(s);
								String colName = colElement.getAttributeValue(
										"name").toUpperCase();
								String value = colElement.getText();
								if (hstCols != null
										&& hstCols.contains(colName)) { // ������ڱ����ڵ�����ֶΡ�
									insertSQL.putFieldValue(colName, value);
								}
							}
							insertSqlList.add(insertSQL.getSQL());
						}
					}
					UIUtil.executeBatchByDS(null, insertSqlList);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean doImportImplementData(SplashWindow _window, String _path,
			int _nettype) throws Exception {
		_window.setWaitInfo("���ڽ�ѹ���ݰ�...");
		SAXBuilder builder = new SAXBuilder();
		java.io.File mainFile = new java.io.File(_path + File.separator
				+ "main.xml");
		org.jdom.Document rootNode = builder.build(mainFile);
		Element element = rootNode.getRootElement();
		String schemeid = element.getAttributeValue("schemeid");
		String deptid = element.getAttributeValue("deptid");
		String implids = element.getAttributeValue("implids");// ���ʵʩ���������/2016-09-29��
		List tableFile = element.getChildren("table");
		for (int j = 0; j < tableFile.size(); j++) { // һ�ű�
			Element tableElement = (Element) tableFile.get(j);
			String tablename = tableElement.getAttributeValue("name")
					.toUpperCase(); // ����
			tablename = tablename.toUpperCase();
			if (_nettype == 0) {// ���������浼������
				if (!"ck_scheme_impl".equalsIgnoreCase(tablename)
						&& !"ck_scheme_implement".equalsIgnoreCase(tablename)
						&& !"ck_problem_info".equalsIgnoreCase(tablename)
						&& !"cmp_wardevent_cust".equalsIgnoreCase(tablename)
						&& !"cmp_wardevent_user".equalsIgnoreCase(tablename)
						&& !"cmp_event".equalsIgnoreCase(tablename)
						&& !"ck_record".equalsIgnoreCase(tablename)) {
					continue;
				}
			}

			// ��������
			TableDataStruct tabstrct = UIUtil.getTableDataStructByDS(null,
					"select * from " + tablename + " where 1=2"); //
			HashSet<String> hstCols = new HashSet<String>(); // ��ŶԱ��ֶ�
			String[] str_cols = tabstrct.getHeaderName(); //
			for (int k = 0; k < str_cols.length; k++) {
				hstCols.add(str_cols[k].toUpperCase()); //
			}
			// ͬ���滻��
			List fileList = tableElement.getChildren("file");
			int fileSize = fileList.size();
			for (int k = 0; k < fileSize; k++) {
				Element fileElement = (Element) fileList.get(k);
				List<String> insertSqlList = new ArrayList<String>();
				java.io.File current_file = null;
				if (fileElement.getAttributeValue("path") != null
						&& !fileElement.getAttributeValue("path").equals("")) {
					current_file = new java.io.File(_path + File.separator
							+ fileElement.getAttributeValue("path"));
				} else {
					break;
				}
				rootNode = builder.build(current_file);
				element = rootNode.getRootElement();
				List recordNodeList = element.getChildren("record");
				UpdateSQLBuilder updateSQL = null;
				// InsertSQLBuilder insertSQL = null;
				// for (int l = 0; l < recordNodeList.size(); l++) {
				// updateSQL = new UpdateSQLBuilder(tablename);
				// insertSQL = new InsertSQLBuilder(tablename);
				// Element recodeElement = (Element) recordNodeList.get(l);
				// List colList = recodeElement.getChildren("col");
				// Element colElementid = (Element) colList.get(0);
				// String colNameid = colElementid.getAttributeValue("name")
				// .toUpperCase();
				// String valueid = colElementid.getText();
				// String[] rutue = UIUtil.getStringArrayFirstColByDS(null,
				// "select id from " + tablename + " where id='"
				// + valueid + "'");
				// if (rutue.length > 0) {
				// for (int i = 1; i < colList.size(); i++) {
				// Element colElement1 = (Element) colList.get(i);
				// String colName1 = colElement1.getAttributeValue(
				// "name").toUpperCase();
				// String value1 = colElement1.getText();
				// updateSQL.setWhereCondition("id=" + valueid);
				// updateSQL.putFieldValue(colName1, value1);
				// }
				// insertSqlList.add(updateSQL.getSQL());
				// } else {
				// for (int s = 0; s < colList.size(); s++) {
				// Element colElement = (Element) colList.get(s);
				// String colName = colElement.getAttributeValue(
				// "name").toUpperCase();
				// String value = colElement.getText();
				// if (hstCols != null && hstCols.contains(colName)) { //
				// ������ڱ����ڵ�����ֶΡ�
				// insertSQL.putFieldValue(colName, value);
				// }
				// }
				// insertSqlList.add(insertSQL.getSQL());
				// }
				// }

				for (int l = 0; l < recordNodeList.size(); l++) {
					Element recodeElement = (Element) recordNodeList.get(l);
					List colList = recodeElement.getChildren("col");
					InsertSQLBuilder insertSQL = new InsertSQLBuilder(tablename);
					for (int m = 0; m < colList.size(); m++) {
						Element colElement = (Element) colList.get(m);
						String colName = colElement.getAttributeValue("name")
								.toUpperCase();
						String value = colElement.getText();
						if (hstCols != null && hstCols.contains(colName)) { //
							// ������ڱ����ڵ�����ֶΡ�
							insertSQL.putFieldValue(colName, value);
						}
					}
					insertSqlList.add(insertSQL.getSQL());
				}
				// //
				// ��ʱδָ�����ʵʩ������������浼����ֻѡ�񷽰��ͱ������������ͬһ�����ͱ��������������ʵʩ������ѡ�񣬹��޷�ָ�����ʵʩ��������ָ���˼��ʵʩ�������������浼�������Ժ������Ҫ����������Ƚ��з��ֳ���飬�ٵ��������ֳ���顾���/2016-10-08��
				if ("0".equals(_nettype)) {//
					// ���������浼������,ck_scheme_implement��ck_problem_info��cmp_event
					// // ���ű����� schemeid��deptid ���
					if ("cmp_wardevent_cust".equalsIgnoreCase(tablename)
							|| "cmp_wardevent_user".equalsIgnoreCase(tablename)) {//
						// Υ���¼��漰�ͻ���Ա���������/2016-09-05��
						if (implids == null || "".equals(implids)
								|| "null".equalsIgnoreCase(implids)) {//
							// ���δָ�����ʵʩ��������÷����ͱ��������ļ�¼ȫ���滻�����/2016-09-29��
							insertSqlList
									.add(
											0,
											"delete from "
													+ tablename
													+ " where cmp_wardevent_id in (select id from cmp_event where schemeid='"
													+ schemeid
													+ "' and deptid ='"
													+ deptid
													+ "') or cmp_wardevent_id not in(select id from cmp_event)");
						} else {// ���ָ���˼��ʵʩ���������滻���֡����/2016-09-29��
							insertSqlList
									.add(
											0,
											"delete from "
													+ tablename
													+ " where cmp_wardevent_id in (select id from cmp_event where implid in("
													+ implids
													+ ") or cmp_wardevent_id not in(select id from cmp_event)");
						}
					} else {
						if (implids == null || "".equals(implids)
								|| "null".equalsIgnoreCase(implids)) {//
							// ���δָ�����ʵʩ��������÷����ͱ��������ļ�¼ȫ���滻�����/2016-09-29��
							insertSqlList.add(0, "delete from " + tablename
									+ " where schemeid='" + schemeid
									+ "' and deptid ='" + deptid + "'");
						} else {// ���ָ���˼��ʵʩ���������滻���֡����/2016-09-29��
							if ("ck_scheme_impl".equalsIgnoreCase(tablename)) {//
								// �򵥻����п����Ŵ���Ʊ�ݼ��ʵʩ�������ܣ����������Ҫ����ñ������/2016-10-08��
								insertSqlList.add(0, "delete from " + tablename
										+ " where id in(" + implids + ") ");
							} else {
								insertSqlList.add(0, "delete from " + tablename
										+ " where implid in(" + implids + ") ");
							}
						}
					}
				} else {// �����浼������
					if ("ck_scheme".equalsIgnoreCase(tablename)) {// ��鷽��
						insertSqlList.add(0, "delete from " + tablename
								+ " where id='" + schemeid + "'");
					} else if ("ck_manuscript_design"
							.equalsIgnoreCase(tablename)
							|| "ck_member_work".equalsIgnoreCase(tablename)) {//
						// ���׸����Ա�ֹ�
						insertSqlList.add(0, "delete from " + tablename
								+ " where schemeid='" + schemeid + "'");
					} else if ("ck_scheme_implement"
							.equalsIgnoreCase(tablename)
							|| "ck_problem_info".equalsIgnoreCase(tablename)
							|| "cmp_event".equalsIgnoreCase(tablename)
							|| "ck_record".equalsIgnoreCase(tablename)) {
						if (implids == null || "".equals(implids)
								|| "null".equalsIgnoreCase(implids)) {//
							// ���δָ�����ʵʩ��������÷����ͱ��������ļ�¼ȫ���滻�����/2016-09-29��
							insertSqlList.add(0, "delete from " + tablename
									+ " where schemeid='" + schemeid
									+ "' and deptid ='" + deptid + "'");
						} else {// ���ָ���˼��ʵʩ���������滻���֡����/2016-09-29��
							insertSqlList.add(0, "delete from " + tablename
									+ " where implid in(" + implids + ")");
						}
					} else if ("ck_scheme_impl".equalsIgnoreCase(tablename)) {//
						// ���ʵʩ����
						if (implids == null || "".equals(implids)
								|| "null".equalsIgnoreCase(implids)) {//
							// ���δָ�����ʵʩ��������÷����ͱ��������ļ�¼ȫ���滻�����/2016-09-29��
							insertSqlList.add(0, "delete from " + tablename
									+ " where schemeid='" + schemeid
									+ "' and deptid ='" + deptid + "'");
						} else {// ���ָ���˼��ʵʩ���������滻���֡����/2016-09-29��
							insertSqlList.add(0, "delete from " + tablename
									+ " where id in(" + implids + ")");
						}
					} else {
						insertSqlList.add(0, "delete from " + tablename);// ��Ա�������Ȳ�ѯ��
					}
				}
				UIUtil.executeBatchByDS(null, insertSqlList);
				StringBuffer sb = new StringBuffer("�����ȣ�[" + (j + 1) + "/"
						+ tableFile.size() + "]�����"
						+ ((j + 1) * 100 / tableFile.size()) + "%\r\n");
				sb.append("��ϸ����:��ǰ[" + (k + 1) + "]��[" + fileSize + "],�����"
						+ ((k + 1) * 100 / fileSize) + "%\r\n");
				_window.setWaitInfo(sb.toString());
			}
			_window.setWaitInfo("��ʼ���븽��");
			java.io.File checkFiles = new java.io.File(_path + File.separator
					+ "icheck");
			if (checkFiles.exists()) {// ����и���
				FileInputStream fins = null;
				try {
					File[] dateFiles = checkFiles.listFiles();
					for (int i = 0; i < dateFiles.length; i++) {
						File[] files = dateFiles[i].listFiles();
						for (int l = 0; l < files.length; l++) {
							File uploadfile = files[l];
							int filelength = new Long(uploadfile.length())
									.intValue(); // �ļ���С!
							byte[] filecontent = new byte[filelength]; // һ���Ӷ����ļ�!!!
							fins = new FileInputStream(uploadfile); // �����ļ���!
							fins.read(filecontent);
							ClassFileVO filevo = new ClassFileVO(); //
							filevo.setByteCodes(filecontent); // �����ֽ�!
							filevo.setClassFileName(File.separator + "icheck"
									+ File.separator + dateFiles[i].getName()
									+ File.separator + uploadfile.getName()); // �ļ���
							UIUtil.uploadFileFromClient(filevo, false);
							fins.close(); //
						}
					}

				} catch (Exception ex) {
					MessageBox.showException(_window, ex); //
					return false;
				} finally {
					try {
						if (fins != null) {
							fins.close(); //
						}
					} catch (Exception e) {
						e.printStackTrace(); //
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param _window
	 * @param _path
	 * @param _type
	 * @return
	 * @throws Exception
	 */
	private boolean doImportAdjustData(SplashWindow _window, String _path,
			int _nettype) throws Exception {
		_window.setWaitInfo("���ڽ�ѹ���ݰ�...");
		SAXBuilder builder = new SAXBuilder();
		java.io.File mainFile = new java.io.File(_path + File.separator
				+ "main.xml");
		org.jdom.Document rootNode = builder.build(mainFile);
		Element element = rootNode.getRootElement();
		String schemeid = element.getAttributeValue("schemeid");
		String deptid = element.getAttributeValue("deptid");
		List tableFile = element.getChildren("table");
		for (int j = 0; j < tableFile.size(); j++) { // һ�ű�
			Element tableElement = (Element) tableFile.get(j);
			String tablename = tableElement.getAttributeValue("name")
					.toUpperCase(); // ����
			tablename = tablename.toUpperCase();
			if (_nettype == 0) {// ���������浼������
				if (!"ck_problem_info".equalsIgnoreCase(tablename)) {
					continue;
				}
			}

			// ��������
			TableDataStruct tabstrct = UIUtil.getTableDataStructByDS(null,
					"select * from " + tablename + " where 1=2"); //
			HashSet<String> hstCols = new HashSet<String>(); // ��ŶԱ��ֶ�
			String[] str_cols = tabstrct.getHeaderName(); //
			for (int k = 0; k < str_cols.length; k++) {
				hstCols.add(str_cols[k].toUpperCase()); //
			}
			// ͬ���滻��
			List fileList = tableElement.getChildren("file");
			int fileSize = fileList.size();
			for (int k = 0; k < fileSize; k++) {
				Element fileElement = (Element) fileList.get(k);
				List<String> insertSqlList = new ArrayList<String>();
				java.io.File current_file = null;
				if (fileElement.getAttributeValue("path") != null
						&& !fileElement.getAttributeValue("path").equals("")) {
					current_file = new java.io.File(_path + File.separator
							+ fileElement.getAttributeValue("path"));
				} else {
					break;
				}
				rootNode = builder.build(current_file);
				element = rootNode.getRootElement();
				List recordNodeList = element.getChildren("record");
				for (int l = 0; l < recordNodeList.size(); l++) {
					Element recodeElement = (Element) recordNodeList.get(l);
					List colList = recodeElement.getChildren("col");
					InsertSQLBuilder insertSQL = new InsertSQLBuilder(tablename);
					for (int m = 0; m < colList.size(); m++) {
						Element colElement = (Element) colList.get(m);
						String colName = colElement.getAttributeValue("name")
								.toUpperCase();
						String value = colElement.getText();
						if (hstCols != null && hstCols.contains(colName)) { // ������ڱ����ڵ�����ֶΡ�
							insertSQL.putFieldValue(colName, value);
						}
					}
					insertSqlList.add(insertSQL.getSQL());
				}
				if ("0".equals(_nettype)) {// ���������浼������,ck_problem_info
					insertSqlList.add(0, "delete from " + tablename
							+ " where schemeid='" + schemeid
							+ "' and deptid ='" + deptid + "'");
				} else {// �����浼������
					if ("ck_scheme".equalsIgnoreCase(tablename)) {// ��鷽��
						insertSqlList.add(0, "delete from " + tablename
								+ " where id='" + schemeid + "'");
					} else if ("ck_manuscript_design"
							.equalsIgnoreCase(tablename)
							|| "ck_member_work".equalsIgnoreCase(tablename)) {// ���׸����Ա�ֹ�
						insertSqlList.add(0, "delete from " + tablename
								+ " where schemeid='" + schemeid + "'");
					} else if ("ck_scheme_implement"
							.equalsIgnoreCase(tablename)
							|| "ck_problem_info".equalsIgnoreCase(tablename)
							|| "cmp_event".equalsIgnoreCase(tablename)) {
						insertSqlList.add(0, "delete from " + tablename
								+ " where schemeid='" + schemeid
								+ "' and deptid ='" + deptid + "'");
					} else if ("cmp_wardevent_cust".equalsIgnoreCase(tablename)
							|| "cmp_wardevent_user".equalsIgnoreCase(tablename)) {// Υ���¼��漰�ͻ���Ա���������/2016-09-05��
						insertSqlList
								.add(
										0,
										"delete from "
												+ tablename
												+ " where cmp_wardevent_id in (select id from cmp_event where schemeid='"
												+ schemeid
												+ "' and deptid ='"
												+ deptid
												+ "') or cmp_wardevent_id not in(select id from cmp_event)");
					} else {
						insertSqlList.add(0, "delete from " + tablename);// ��Ա�������Ȳ�ѯ��
					}
				}
				UIUtil.executeBatchByDS(null, insertSqlList);
				StringBuffer sb = new StringBuffer("�����ȣ�[" + (j + 1) + "/"
						+ tableFile.size() + "]�����"
						+ ((j + 1) * 100 / tableFile.size()) + "%\r\n");
				sb.append("��ϸ����:��ǰ[" + (k + 1) + "]��[" + fileSize + "],�����"
						+ ((k + 1) * 100 / fileSize) + "%\r\n");
				_window.setWaitInfo(sb.toString());
			}
			_window.setWaitInfo("��ʼ���븽��");
			java.io.File checkFiles = new java.io.File(_path + File.separator
					+ "upload");// �ȼ��ʵʩ���뵼������һ��upload�ļ��У���Ϊ�����û��icheck�ļ�·��
			if (checkFiles.exists()) {// ����и���
				FileInputStream fins = null;
				try {
					File[] dateFiles = checkFiles.listFiles();
					for (int i = 0; i < dateFiles.length; i++) {
						File[] files = dateFiles[i].listFiles();
						if ("icheck".equals(dateFiles[i].getName())) {// icheck�ļ���
							for (int l = 0; l < files.length; l++) {
								File[] realfiles = files[l].listFiles();
								for (int m = 0; m < realfiles.length; m++) {
									File uploadfile = realfiles[m];
									int filelength = new Long(uploadfile
											.length()).intValue(); // �ļ���С!
									byte[] filecontent = new byte[filelength]; // һ���Ӷ����ļ�!!!
									fins = new FileInputStream(uploadfile); // �����ļ���!
									fins.read(filecontent);
									ClassFileVO filevo = new ClassFileVO(); //
									filevo.setByteCodes(filecontent); // �����ֽ�!
									filevo.setClassFileName(File.separator
											+ "icheck" + File.separator
											+ files[l].getName()
											+ File.separator
											+ uploadfile.getName()); // �ļ���
									UIUtil.uploadFileFromClient(filevo, false);
									fins.close();
								}
							}
						} else {
							for (int l = 0; l < files.length; l++) {
								File uploadfile = files[l];
								int filelength = new Long(uploadfile.length())
										.intValue(); // �ļ���С!
								byte[] filecontent = new byte[filelength]; // һ���Ӷ����ļ�!!!
								fins = new FileInputStream(uploadfile); // �����ļ���!
								fins.read(filecontent);
								ClassFileVO filevo = new ClassFileVO(); //
								filevo.setByteCodes(filecontent); // �����ֽ�!
								filevo
										.setClassFileName(File.separator
												+ dateFiles[i].getName()
												+ File.separator
												+ uploadfile.getName()); // �ļ���
								UIUtil.uploadFileFromClient(filevo, false);
								fins.close(); //
							}
						}
					}

				} catch (Exception ex) {
					MessageBox.showException(_window, ex); //
					return false;
				} finally {
					try {
						if (fins != null) {
							fins.close(); //
						}
					} catch (Exception e) {
						e.printStackTrace(); //
					}
				}
			}
		}
		return true;
	}

	/*
	 * ��һ�������ļ���ѡ��Ի���
	 */
	public String showSaveFileDialog(Container _parent, String _fileName) {
		JFileChooser chooser = new JFileChooser();
		try {
			File f = new File(new File(ClientEnvironment.str_downLoadFileDir
					+ File.separator + _fileName).getCanonicalPath());
			chooser.setSelectedFile(f);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		int li_rewult = chooser.showSaveDialog(_parent);
		if (li_rewult == JFileChooser.APPROVE_OPTION) {
			File curFile = chooser.getSelectedFile(); //
			if (curFile != null) {
				return curFile.getAbsolutePath();
			}
		}
		return "";
	}

}