package com.pushworld.ipushgrc.ui.icheck.p040;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
 * 新的检查模块客户端工具类-新疆昌吉 【李春娟/2016-08-31】
 * 
 * @author lcj
 * 
 */
public class ICheckUIUtil {

	private static String datesum;
	private String cachePath = System.getProperty("ClientCodeCache"); // 得到客户端缓存位置。
	private TBUtil tbUtil = new TBUtil();
	public static int IMPORTDATA_SUCCESS = 1;// 导入成功
	public static int IMPORTDATA_FAIL = 0;// 导入失败
	public static int IMPORTDATA_NOSELECTFILE = -1;// 导入时未选择文件
	private boolean import_flag = true; // 导入文件成功标志

	/*
	 * id为负值 14位+2位
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
	 * 把 返回值 = 年+100*月+日 = 2012+100*10+24 4位
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

	// 因单机版生成主键前面加了负号，故将随机数设置为2位，这样能保证主键最长16位，加上负号，17位，位数再长，id值加双引号无法查询出来了，可能是mysql底层bug【李春娟/2016-09-05】
	private static String getRandom() {
		long num = Math.round(Math.random() * 99);
		if (num < 10) {
			return "0" + num;
		} else {
			return "" + num;
		}
	}

	/**
	 * 本方法为导出数据，单机版必须从网络版导入过方案，才可从另外的单机版导入
	 * 网络版导出表：ck_scheme_impl,ck_scheme_implement
	 * ,ck_scheme,ck_member_work,ck_manuscript_design
	 * ,pub_user,pub_corp_dept,pub_post,pub_user_post,cmp_cmpfile,cmp_risk
	 * 单机版导出表
	 * ：ck_scheme_impl,ck_scheme_implement,ck_problem_info,cmp_wardevent_cust
	 * ,cmp_wardevent_user
	 * ,cmp_event,ck_scheme,ck_member_work,ck_manuscript_design,ck_record
	 * 
	 * 单机->网络版导入表：ck_scheme_impl（单机版可编辑或拷贝检查实施主表）,检查实施表（ck_scheme_implement）,问题表
	 * （ck_problem_info）,违规事件涉及客户（cmp_wardevent_cust）,违规事件涉及员工（
	 * cmp_wardevent_user）,违规事件表（cmp_event）,单机版导出通知书等工作量（ck_record）
	 * 单机->单机版导入表：ck_scheme_impl
	 * ,ck_scheme_implement,ck_problem_info,cmp_wardevent_cust
	 * ,cmp_wardevent_user
	 * ,cmp_event,ck_scheme,ck_member_work,ck_manuscript_design,ck_record
	 * 
	 * @param _window
	 * @param _path
	 * @param _type
	 *            0-网络版导入数据；1-单机版导入数据
	 * @return
	 * @throws Exception
	 * @param _window
	 * @param _path
	 * @param _schemeid
	 *            方案
	 * @param _deptid
	 *            被检查机构
	 * @param _deptname
	 *            被检查机构名称
	 * @param _implids
	 *            单机版导出需要，ck_scheme_impl.id 串
	 * @param _type
	 *            0-网络版导出数据；1-单机版导出数据
	 * @return
	 * @throws Exception
	 */
	public boolean exportDataByCondition(SplashWindow _splash, String _path,
			String _schemeid, String _deptid, String _deptname,
			String _implids, int _type) {
		ArrayList tableList = new ArrayList();
		if (_type == 0) {// 网络版导出，无需指定检查实施主表
			tableList.add(new String[] {
					"ck_scheme_impl",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });// 检查实施
			tableList.add(new String[] {
					"ck_scheme_implement",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });// 检查实施
			tableList.add(new String[] { "ck_scheme", "",
					"id = '" + _schemeid + "'" });// 检查方案，选择导出
			tableList.add(new String[] { "ck_member_work", "",
					"schemeid = '" + _schemeid + "'" });// 检查方案人员分工，选择导出
			tableList.add(new String[] { "ck_manuscript_design", "",
					"schemeid = '" + _schemeid + "'" });// 检查底稿
			tableList.add(new String[] { "pub_user", "", "" });// 人员等信息全部导出
			tableList.add(new String[] { "pub_corp_dept", "", "" });
			tableList.add(new String[] { "pub_post", "", "" });
			tableList.add(new String[] { "pub_user_post", "", "" });
			tableList.add(new String[] { "cmp_cmpfile", "", "" });//
			tableList.add(new String[] { "cmp_risk", "", "" });
			tableList.add(new String[] { "ck_retrival", "",
					"schemeid = '" + _schemeid + "'" });// 【zzl 2017-11-13】导出调阅
			String[] imp;
			try {
				imp = UIUtil.getStringArrayFirstColByDS(null,"select id from ck_problem_info where deptid = '" + _deptid + "' and schemeid = '" + _schemeid
								+ "'");
				if(imp.length>0){//[zzl 2017-11-23] 有时候会在网络上操作录入底稿
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
		} else {// 单机版导出，需指定检查实施主表
			// cmp_wardevent_cust 和 cmp_wardevent_user 子表最好在cmp_event
			// 前添加，先导入，则可根据cmp_event.deptid和cmp_event.schemeid
			// 找到需要删除的子表记录【李春娟/2016-09-05】
			tableList.add(new String[] { "ck_scheme_impl", "",
					"id in(" + _implids + ")" });// 检查实施主表
			tableList.add(new String[] { "ck_scheme_implement", "",
					"implid in(" + _implids + ")" });// 检查实施子表
			tableList.add(new String[] { "ck_problem_info", "",
					"implid in(" + _implids + ")" });// 问题
			tableList.add(new String[] {
					"cmp_wardevent_cust",
					"",
					"cmp_wardevent_id in (select id from cmp_event where implid in("
							+ _implids + ") )" });// 违规事件涉及客户
			tableList.add(new String[] {
					"cmp_wardevent_user",
					"",
					"cmp_wardevent_id in (select id from cmp_event where implid in("
							+ _implids + ") )" });// 违规事件涉及员工
			tableList.add(new String[] { "cmp_event", "",
					"implid in(" + _implids + ")" });// 违规事件
			tableList.add(new String[] { "ck_scheme", "",
					"id = '" + _schemeid + "'" });// 检查方案，选择导出
			tableList.add(new String[] { "ck_member_work", "",
					"schemeid = '" + _schemeid + "'" });// 检查方案人员分工，选择导出
			tableList.add(new String[] { "ck_manuscript_design", "",
					"schemeid = '" + _schemeid + "'" });// 检查底稿
			tableList.add(new String[] { "ck_record", "",
					"implid in(" + _implids + ")" });// 记录单机版导出导出检查事实确认书、限期整改通知书、风险提示通知书的工作量【李春娟/2016-09-29】
			tableList.add(new String[] { "ck_dyqindan", "",
					"implid in(" + _implids + ")" });
			tableList.add(new String[] { "ck_retrival", "",
					"schemeid = '" + _schemeid + "'" });// 【zzl
			// 2017-11-13】导出调阅清单
		}

		StringBuffer sb = new StringBuffer(); // 写main.xml主文件
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
				// 创建表名的目录!!!
				String str_newPath = cachePath + File.separator + p; // 加上表名!
				File filenewdir = new File(str_newPath); //
				if (!filenewdir.exists()) {
					filenewdir.mkdirs(); // 创建目录!!!以后再搞序号前辍
				}
				int li_beginNo = 0; // 起始号!
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
				while (1 == 1) { // 死循环
					long ll_1 = System.currentTimeMillis(); //
					if (li_beginNo >= li_countall) {
						break;
					}
					HashMap returnMap = service.getXMlFromTable500Records(null,
							tablename, li_beginNo, table[1], table[2]);
					if (returnMap == null) { // 如果为空了则直接返回
						break; //
					}
					int li_recordCount = (Integer) returnMap.get("记录数"); // 实际的记录数,应该除最后一页外,其他的都是500
					li_downedCount = li_downedCount + li_recordCount; //
					int li_perCent = ((li_downedCount * 100) / li_countall); // 百分比!
					// 50/100
					li_beginNo = (Integer) returnMap.get("结束点"); // 实际内容的结束号!
					String str_xml = (String) returnMap.get("内容"); //
					li_cout++; //
					String str_fileName = tablename + "_" + (100000 + li_cout)
							+ ".xml"; //
					FileOutputStream fileOut = new FileOutputStream(str_newPath
							+ "//" + str_fileName, false); //
					tbUtil.writeStrToOutputStream(fileOut, str_xml); // 输出至文件
					long ll_2 = System.currentTimeMillis(); //
					tableSB.append("<file path=\"" + str_fileName
							+ "\" count=\"" + li_recordCount + "\"/>\r\n");
					_splash.setWaitInfo("处理[" + str_fileName + "],耗时["
							+ (ll_2 - ll_1) + "]!!\r\n本表完成比例[" + li_downedCount
							+ "/" + li_countall + "=" + li_perCent + "%],总比例["
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
				+ "\" ");// 这里记录implids

		sb.append("<root " + rootContent.toString() + ">\r\n");
		sb.append(table_sb);
		sb.append("</root>");
		FileOutputStream fileout = null;
		try {
			_splash.setWaitInfo(" 生成main.xml主文件!");
			fileout = new FileOutputStream(cachePath + File.separator + p
					+ File.separator + "main.xml", false);
			_splash.setWaitInfo(" 正在保存导出历史...");
			UIUtil.executeBatchByDS(null, sql_insert_history);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		tbUtil.writeStrToOutputStream(fileout, sb.toString());
		if (_type == 1) {// ck_scheme_implement.descr ck_problem_info.appth
			// cmp_event.attachfile
			// 单机版需要导出附件【李春娟/2016-09-07】
			String str_newPath = cachePath + File.separator + p; // 附件文件夹
			File filenewdir = new File(str_newPath); //
			if (!filenewdir.exists()) {
				filenewdir.mkdirs(); // 创建目录
			}

			// 检查实施
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
								filenewdir.mkdirs(); // 创建目录
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
				// 问题
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
								filenewdir.mkdirs(); // 创建目录
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
				// 违规事件
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
								filenewdir.mkdirs(); // 创建目录
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
			_splash.setWaitInfo(" 正在打包!请稍等...");
			String saveFileName = _deptname + "_"
					+ ClientEnvironment.getInstance().getLoginUserName() + "_"
					+ UIUtil.getCurrDate() + "底稿数据包.zip";
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
	 * 网络版-导出整改台账
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
		if (_type == 0) {// 网络版
			tableList.add(new String[] {
					"ck_problem_info",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });// 检查实施
			tableList
					.add(new String[] {
							"cmp_wardevent_cust",
							"",
							"cmp_wardevent_id in (select id from cmp_event where deptid = '"
									+ _deptid + "' and schemeid = '"
									+ _schemeid + "')" });// 违规事件涉及客户
			tableList
					.add(new String[] {
							"cmp_wardevent_user",
							"",
							"cmp_wardevent_id in (select id from cmp_event where deptid = '"
									+ _deptid + "' and schemeid = '"
									+ _schemeid + "')" });// 违规事件涉及员工
			tableList.add(new String[] {
					"cmp_event",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });// 违规事件
			tableList.add(new String[] { "ck_scheme", "",
					"id = '" + _schemeid + "'" });// 检查方案，选择导出
			tableList.add(new String[] { "ck_member_work", "",
					"schemeid = '" + _schemeid + "'" });// 检查方案人员分工，选择导出
			tableList.add(new String[] { "pub_user", "", "" });// 人员等信息全部导出
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
		} else {// 单机版
			tableList.add(new String[] {
					"ck_problem_info",
					"",
					"deptid = '" + _deptid + "' and schemeid = '" + _schemeid
							+ "'" });// 问题
		}

		StringBuffer sb = new StringBuffer(); // 写main.xml主文件
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
				// 创建表名的目录!!!
				String str_newPath = cachePath + File.separator + p; // 加上表名!
				File filenewdir = new File(str_newPath); //
				if (!filenewdir.exists()) {
					filenewdir.mkdirs(); // 创建目录!!!以后再搞序号前辍
				}
				int li_beginNo = 0; // 起始号!
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
				while (1 == 1) { // 死循环
					long ll_1 = System.currentTimeMillis(); //
					if (li_beginNo >= li_countall) {
						break;
					}
					HashMap returnMap = service.getXMlFromTable500Records(null,
							tablename, li_beginNo, table[1], table[2]);
					if (returnMap == null) { // 如果为空了则直接返回
						break; //
					}
					int li_recordCount = (Integer) returnMap.get("记录数"); // 实际的记录数,应该除最后一页外,其他的都是500
					li_downedCount = li_downedCount + li_recordCount; //
					int li_perCent = ((li_downedCount * 100) / li_countall); // 百分比!
					// 50/100
					li_beginNo = (Integer) returnMap.get("结束点"); // 实际内容的结束号!
					String str_xml = (String) returnMap.get("内容"); //
					li_cout++; //
					String str_fileName = tablename + "_" + (100000 + li_cout)
							+ ".xml"; //
					FileOutputStream fileOut = new FileOutputStream(str_newPath
							+ "//" + str_fileName, false); //
					tbUtil.writeStrToOutputStream(fileOut, str_xml); // 输出至文件
					long ll_2 = System.currentTimeMillis(); //
					tableSB.append("<file path=\"" + str_fileName
							+ "\" count=\"" + li_recordCount + "\"/>\r\n");
					_splash.setWaitInfo("处理[" + str_fileName + "],耗时["
							+ (ll_2 - ll_1) + "]!!\r\n本表完成比例[" + li_downedCount
							+ "/" + li_countall + "=" + li_perCent + "%],总比例["
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
			_splash.setWaitInfo(" 生成main.xml主文件!");
			fileout = new FileOutputStream(cachePath + File.separator + p
					+ File.separator + "main.xml", false);
			_splash.setWaitInfo(" 正在保存导出历史...");
			UIUtil.executeBatchByDS(null, sql_insert_history);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tbUtil.writeStrToOutputStream(fileout, sb.toString());
		if (_type == 0) {// ck_problem_info.appth cmp_event.attachfile
			// 网络版需要导出附件【李春娟/2016-09-21】
			String str_newPath = cachePath + File.separator + p
					+ File.separator + "upload"; // 附件文件夹，需要加一层upload，因网络版上传的附件，没有icheck文件夹
			File filenewdir = new File(str_newPath); //
			if (!filenewdir.exists()) {
				filenewdir.mkdirs(); // 创建目录
			}
			try {
				// 问题
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
								filenewdir.mkdirs(); // 创建目录
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
				// 违规事件
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
								filenewdir.mkdirs(); // 创建目录
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
			_splash.setWaitInfo(" 正在打包!请稍等...");
			String saveFileName = _deptname + "_"
					+ ClientEnvironment.getInstance().getLoginUserName() + "_"
					+ UIUtil.getCurrDate() + "整改台账.zip";
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

	// 通过包导入数据，以前把所有重复的ID统统干掉，然后全部重新插入，但有些子表里内容需要全部删除，故改为根据表判断删除逻辑。
	// _title选择文件框提示内容，_type：0-网络版导入数据，1-单机版导入数据
	// return 值 -1 是选择文件有问题。1成功 0失败
	public int importDataByPackage(Container _parent, String _title,
			final int _nettype) {
		return importDataByPackage(_parent, _title, _nettype, 0);
	}

	/**
	 * 
	 * @param _parent
	 * @param _title
	 *            提示内容
	 * @param _nettype
	 *            网络类型：0-网络版导入数据，1-单机版导入数据
	 * @param _filetype
	 *            导入内容：0-检查实施，1-整改台账
	 * @return
	 */
	public int importDataByPackage(Container _parent, String _title,
			final int _nettype, final int _filetype) {
		try {
			String p = "importDB" + File.separator + _nettype + File.separator
					+ System.currentTimeMillis();
			final HashMap conditionMap = new HashMap();
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(true); // [zzl2017-11-23昌吉希望可以批量导入]可以多选
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"数据包(*.zip,*.rar)", "zip", "rar");
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
					filenewdir.mkdirs(); // 创建目录!!!以后再搞序号前辍
				}
				ZipFileUtil.unzip(file[i].getAbsolutePath(), currPath); // 这就解压一把
				java.io.File mainFile = new java.io.File(currPath
						+ File.separator + "main.xml");
				if (!mainFile.exists()) {
					MessageBox.show(_parent, "压缩包中没有main.xml主文件，不能导入！");
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
							// 导入检查实施
							import_flag = hbImportImplementData(
									(SplashWindow) e.getSource(),
									currPath, _nettype);

						} catch (Exception e1) {
							e1.printStackTrace();
							import_flag = false;// 报错失败标志。
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
//					if (count != null && !"0".equals(count)) {// 【zzl//
//																// 2016-09-01】
//						a = MessageBox.showConfirmDialog(_parent, "【"
//								+ file[i].getName() + "】点击是合并导入！点击否覆盖导入！");
//						if (a == 0) {
//							new SplashWindow(_parent, new AbstractAction() {
//								public void actionPerformed(ActionEvent e) {
//									try {
//										// 导入检查实施
//										import_flag = hbImportImplementData(
//												(SplashWindow) e.getSource(),
//												currPath, _nettype);
//
//									} catch (Exception e1) {
//										e1.printStackTrace();
//										import_flag = false;// 报错失败标志。
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
//										import_flag = false;// 报错失败标志。
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
//								import_flag = false;// 报错失败标志。
//							}
//						}
//					}, 600, 130, 300, 300, false); // 
//				}
//
			}
			if (import_flag) {
				return IMPORTDATA_SUCCESS;// 导入成功
			} else {
				return IMPORTDATA_FAIL;// 导入失败
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return IMPORTDATA_FAIL;// 导入失败
		}
	}

	/**
	 * 本方法为导入数据
	 * 
	 * 网络版导出表：ck_scheme_impl,ck_scheme_implement,ck_scheme,ck_member_work,
	 * ck_manuscript_design
	 * ,pub_user,pub_corp_dept,pub_post,pub_user_post,cmp_cmpfile,cmp_risk
	 * 单机版导出表
	 * ：ck_scheme_impl,ck_scheme_implement,ck_problem_info,cmp_wardevent_cust
	 * ,cmp_wardevent_user
	 * ,cmp_event,ck_scheme,ck_member_work,ck_manuscript_design,ck_record
	 * 
	 * 单机->网络版导入表：ck_scheme_impl（单机版可编辑或拷贝检查实施主表）,检查实施表（ck_scheme_implement）,问题表
	 * （ck_problem_info）,违规事件涉及客户（cmp_wardevent_cust）,违规事件涉及员工（
	 * cmp_wardevent_user）,违规事件表（cmp_event）,单机版导出通知书等工作量（ck_record）
	 * 单机->单机版导入表：ck_scheme_impl
	 * ,ck_scheme_implement,ck_problem_info,cmp_wardevent_cust
	 * ,cmp_wardevent_user
	 * ,cmp_event,ck_scheme,ck_member_work,ck_manuscript_design,ck_record
	 * 
	 * @param _window
	 * @param _path
	 * @param _type
	 *            0-网络版导入数据；1-单机版导入数据
	 * @return
	 * @throws Exception
	 *             zzl[2017-1-6]
	 */
	private boolean hbImportImplementData(SplashWindow _window, String _path,
			int _nettype) throws Exception {
		_window.setWaitInfo("正在解压数据包...");
		SAXBuilder builder = new SAXBuilder();
		java.io.File mainFile = new java.io.File(_path + File.separator
				+ "main.xml");
		org.jdom.Document rootNode = builder.build(mainFile);
		Element element = rootNode.getRootElement();
		String schemeid = element.getAttributeValue("schemeid");
		String deptid = element.getAttributeValue("deptid");
		String implids = element.getAttributeValue("implids");// 检查实施主表【李春娟/2016-09-29】
		List tableFile = element.getChildren("table");
		try {
			for (int j = 0; j < tableFile.size(); j++) { // 一张表
				Element tableElement = (Element) tableFile.get(j);
				String tablename = tableElement.getAttributeValue("name")
						.toUpperCase(); // 表名
				tablename = tablename.toUpperCase();
				if (_nettype == 0) {// 如果是网络版导入数据
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

				// 更新类型
				TableDataStruct tabstrct = UIUtil.getTableDataStructByDS(null,
						"select * from " + tablename + " where 1=2"); //
				HashSet<String> hstCols = new HashSet<String>(); // 存放对比字段
				String[] str_cols = tabstrct.getHeaderName(); //
				for (int k = 0; k < str_cols.length; k++) {
					hstCols.add(str_cols[k].toUpperCase()); //
				}
				// 同步替换。
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
					try{
						rootNode = builder.build(current_file);
					}catch(Exception e){
						String strpath=replaceAllXML(current_file);
						rootNode = builder.build(new File(strpath));
					}
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
										&& hstCols.contains(colName)) { // 如果现在表存在导入的字段。
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
		_window.setWaitInfo("正在解压数据包...");
		SAXBuilder builder = new SAXBuilder();
		java.io.File mainFile = new java.io.File(_path + File.separator
				+ "main.xml");
		org.jdom.Document rootNode = builder.build(mainFile);
		Element element = rootNode.getRootElement();
		String schemeid = element.getAttributeValue("schemeid");
		String deptid = element.getAttributeValue("deptid");
		String implids = element.getAttributeValue("implids");// 检查实施主表【李春娟/2016-09-29】
		List tableFile = element.getChildren("table");
		for (int j = 0; j < tableFile.size(); j++) { // 一张表
			Element tableElement = (Element) tableFile.get(j);
			String tablename = tableElement.getAttributeValue("name")
					.toUpperCase(); // 表名
			tablename = tablename.toUpperCase();
			if (_nettype == 0) {// 如果是网络版导入数据
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

			// 更新类型
			TableDataStruct tabstrct = UIUtil.getTableDataStructByDS(null,
					"select * from " + tablename + " where 1=2"); //
			HashSet<String> hstCols = new HashSet<String>(); // 存放对比字段
			String[] str_cols = tabstrct.getHeaderName(); //
			for (int k = 0; k < str_cols.length; k++) {
				hstCols.add(str_cols[k].toUpperCase()); //
			}
			// 同步替换。
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
				// 如果现在表存在导入的字段。
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
							// 如果现在表存在导入的字段。
							insertSQL.putFieldValue(colName, value);
						}
					}
					insertSqlList.add(insertSQL.getSQL());
				}
				// //
				// 暂时未指定检查实施主表（即网络版导出，只选择方案和被检查机构，针对同一方案和被检查机构多条检查实施不进行选择，故无法指定检查实施主表），指定了检查实施主表（即单机版导出），以后可能需要考虑网络版先进行非现场检查，再导出进行现场检查【李春娟/2016-10-08】
				if ("0".equals(_nettype)) {//
					// 如果是网络版导入数据,ck_scheme_implement、ck_problem_info、cmp_event
					// // 三张表都有 schemeid、deptid 外键
					if ("cmp_wardevent_cust".equalsIgnoreCase(tablename)
							|| "cmp_wardevent_user".equalsIgnoreCase(tablename)) {//
						// 违规事件涉及客户和员工表【李春娟/2016-09-05】
						if (implids == null || "".equals(implids)
								|| "null".equalsIgnoreCase(implids)) {//
							// 如果未指定检查实施主表，则该方案和被检查机构的记录全部替换【李春娟/2016-09-29】
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
						} else {// 如果指定了检查实施主表，则替换部分【李春娟/2016-09-29】
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
							// 如果未指定检查实施主表，则该方案和被检查机构的记录全部替换【李春娟/2016-09-29】
							insertSqlList.add(0, "delete from " + tablename
									+ " where schemeid='" + schemeid
									+ "' and deptid ='" + deptid + "'");
						} else {// 如果指定了检查实施主表，则替换部分【李春娟/2016-09-29】
							if ("ck_scheme_impl".equalsIgnoreCase(tablename)) {//
								// 因单机版有拷贝信贷或票据检查实施主表功能，故网络版需要导入该表【李春娟/2016-10-08】
								insertSqlList.add(0, "delete from " + tablename
										+ " where id in(" + implids + ") ");
							} else {
								insertSqlList.add(0, "delete from " + tablename
										+ " where implid in(" + implids + ") ");
							}
						}
					}
				} else {// 单机版导入数据
					if ("ck_scheme".equalsIgnoreCase(tablename)) {// 检查方案
						insertSqlList.add(0, "delete from " + tablename
								+ " where id='" + schemeid + "'");
					} else if ("ck_manuscript_design"
							.equalsIgnoreCase(tablename)
							|| "ck_member_work".equalsIgnoreCase(tablename)) {//
						// 检查底稿或人员分工
						insertSqlList.add(0, "delete from " + tablename
								+ " where schemeid='" + schemeid + "'");
					} else if ("ck_scheme_implement"
							.equalsIgnoreCase(tablename)
							|| "ck_problem_info".equalsIgnoreCase(tablename)
							|| "cmp_event".equalsIgnoreCase(tablename)
							|| "ck_record".equalsIgnoreCase(tablename)) {
						if (implids == null || "".equals(implids)
								|| "null".equalsIgnoreCase(implids)) {//
							// 如果未指定检查实施主表，则该方案和被检查机构的记录全部替换【李春娟/2016-09-29】
							insertSqlList.add(0, "delete from " + tablename
									+ " where schemeid='" + schemeid
									+ "' and deptid ='" + deptid + "'");
						} else {// 如果指定了检查实施主表，则替换部分【李春娟/2016-09-29】
							insertSqlList.add(0, "delete from " + tablename
									+ " where implid in(" + implids + ")");
						}
					} else if ("ck_scheme_impl".equalsIgnoreCase(tablename)) {//
						// 检查实施主表
						if (implids == null || "".equals(implids)
								|| "null".equalsIgnoreCase(implids)) {//
							// 如果未指定检查实施主表，则该方案和被检查机构的记录全部替换【李春娟/2016-09-29】
							insertSqlList.add(0, "delete from " + tablename
									+ " where schemeid='" + schemeid
									+ "' and deptid ='" + deptid + "'");
						} else {// 如果指定了检查实施主表，则替换部分【李春娟/2016-09-29】
							insertSqlList.add(0, "delete from " + tablename
									+ " where id in(" + implids + ")");
						}
					} else {
						insertSqlList.add(0, "delete from " + tablename);// 人员、机构等查询表
					}
				}
				UIUtil.executeBatchByDS(null, insertSqlList);
				StringBuffer sb = new StringBuffer("主进度：[" + (j + 1) + "/"
						+ tableFile.size() + "]完成率"
						+ ((j + 1) * 100 / tableFile.size()) + "%\r\n");
				sb.append("详细进度:当前[" + (k + 1) + "]共[" + fileSize + "],完成率"
						+ ((k + 1) * 100 / fileSize) + "%\r\n");
				_window.setWaitInfo(sb.toString());
			}
			_window.setWaitInfo("开始导入附件");
			java.io.File checkFiles = new java.io.File(_path + File.separator
					+ "icheck");
			if (checkFiles.exists()) {// 如果有附件
				FileInputStream fins = null;
				try {
					File[] dateFiles = checkFiles.listFiles();
					for (int i = 0; i < dateFiles.length; i++) {
						File[] files = dateFiles[i].listFiles();
						for (int l = 0; l < files.length; l++) {
							File uploadfile = files[l];
							int filelength = new Long(uploadfile.length())
									.intValue(); // 文件大小!
							byte[] filecontent = new byte[filelength]; // 一下子读进文件!!!
							fins = new FileInputStream(uploadfile); // 创建文件流!
							fins.read(filecontent);
							ClassFileVO filevo = new ClassFileVO(); //
							filevo.setByteCodes(filecontent); // 设置字节!
							filevo.setClassFileName(File.separator + "icheck"
									+ File.separator + dateFiles[i].getName()
									+ File.separator + uploadfile.getName()); // 文件名
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
		_window.setWaitInfo("正在解压数据包...");
		SAXBuilder builder = new SAXBuilder();
		java.io.File mainFile = new java.io.File(_path + File.separator
				+ "main.xml");
		org.jdom.Document rootNode = builder.build(mainFile);
		Element element = rootNode.getRootElement();
		String schemeid = element.getAttributeValue("schemeid");
		String deptid = element.getAttributeValue("deptid");
		List tableFile = element.getChildren("table");
		for (int j = 0; j < tableFile.size(); j++) { // 一张表
			Element tableElement = (Element) tableFile.get(j);
			String tablename = tableElement.getAttributeValue("name")
					.toUpperCase(); // 表名
			tablename = tablename.toUpperCase();
			if (_nettype == 0) {// 如果是网络版导入数据
				if (!"ck_problem_info".equalsIgnoreCase(tablename)) {
					continue;
				}
			}

			// 更新类型
			TableDataStruct tabstrct = UIUtil.getTableDataStructByDS(null,
					"select * from " + tablename + " where 1=2"); //
			HashSet<String> hstCols = new HashSet<String>(); // 存放对比字段
			String[] str_cols = tabstrct.getHeaderName(); //
			for (int k = 0; k < str_cols.length; k++) {
				hstCols.add(str_cols[k].toUpperCase()); //
			}
			// 同步替换。
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
						if (hstCols != null && hstCols.contains(colName)) { // 如果现在表存在导入的字段。
							insertSQL.putFieldValue(colName, value);
						}
					}
					insertSqlList.add(insertSQL.getSQL());
				}
				if ("0".equals(_nettype)) {// 如果是网络版导入数据,ck_problem_info
					insertSqlList.add(0, "delete from " + tablename
							+ " where schemeid='" + schemeid
							+ "' and deptid ='" + deptid + "'");
				} else {// 单机版导入数据
					if ("ck_scheme".equalsIgnoreCase(tablename)) {// 检查方案
						insertSqlList.add(0, "delete from " + tablename
								+ " where id='" + schemeid + "'");
					} else if ("ck_manuscript_design"
							.equalsIgnoreCase(tablename)
							|| "ck_member_work".equalsIgnoreCase(tablename)) {// 检查底稿或人员分工
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
							|| "cmp_wardevent_user".equalsIgnoreCase(tablename)) {// 违规事件涉及客户和员工表【李春娟/2016-09-05】
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
						insertSqlList.add(0, "delete from " + tablename);// 人员、机构等查询表
					}
				}
				UIUtil.executeBatchByDS(null, insertSqlList);
				StringBuffer sb = new StringBuffer("主进度：[" + (j + 1) + "/"
						+ tableFile.size() + "]完成率"
						+ ((j + 1) * 100 / tableFile.size()) + "%\r\n");
				sb.append("详细进度:当前[" + (k + 1) + "]共[" + fileSize + "],完成率"
						+ ((k + 1) * 100 / fileSize) + "%\r\n");
				_window.setWaitInfo(sb.toString());
			}
			_window.setWaitInfo("开始导入附件");
			java.io.File checkFiles = new java.io.File(_path + File.separator
					+ "upload");// 比检查实施导入导出多了一层upload文件夹，因为网络版没有icheck文件路径
			if (checkFiles.exists()) {// 如果有附件
				FileInputStream fins = null;
				try {
					File[] dateFiles = checkFiles.listFiles();
					for (int i = 0; i < dateFiles.length; i++) {
						File[] files = dateFiles[i].listFiles();
						if ("icheck".equals(dateFiles[i].getName())) {// icheck文件夹
							for (int l = 0; l < files.length; l++) {
								File[] realfiles = files[l].listFiles();
								for (int m = 0; m < realfiles.length; m++) {
									File uploadfile = realfiles[m];
									int filelength = new Long(uploadfile
											.length()).intValue(); // 文件大小!
									byte[] filecontent = new byte[filelength]; // 一下子读进文件!!!
									fins = new FileInputStream(uploadfile); // 创建文件流!
									fins.read(filecontent);
									ClassFileVO filevo = new ClassFileVO(); //
									filevo.setByteCodes(filecontent); // 设置字节!
									filevo.setClassFileName(File.separator
											+ "icheck" + File.separator
											+ files[l].getName()
											+ File.separator
											+ uploadfile.getName()); // 文件名
									UIUtil.uploadFileFromClient(filevo, false);
									fins.close();
								}
							}
						} else {
							for (int l = 0; l < files.length; l++) {
								File uploadfile = files[l];
								int filelength = new Long(uploadfile.length())
										.intValue(); // 文件大小!
								byte[] filecontent = new byte[filelength]; // 一下子读进文件!!!
								fins = new FileInputStream(uploadfile); // 创建文件流!
								fins.read(filecontent);
								ClassFileVO filevo = new ClassFileVO(); //
								filevo.setByteCodes(filecontent); // 设置字节!
								filevo
										.setClassFileName(File.separator
												+ dateFiles[i].getName()
												+ File.separator
												+ uploadfile.getName()); // 文件名
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
	 * 打开一个保存文件的选择对话框。
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
	/**
	 * ZZL 
	 * XML 非法字符替换
	 */
	public String replaceAllXML(File current_file){
	      InputStream in = null;
	      BufferedReader reader = null;
	      StringBuilder sb=new StringBuilder();
	      String path=null;
	        try {
	        	  System.out.println("以行为单位读取文件内容，一次读一整行：");
	              reader = new BufferedReader(new FileReader(current_file));
	              String tempString = null;
	              int line = 1;
	              // 一次读入一行，直到读入null为文件结束
	              while ((tempString = reader.readLine()) != null) {
	                  // 显示行号
//	                  System.out.println("line " + line + ": " + tempString);
//	                  line++;
	            	  tempString=tempString+"\r\n";
	            	  sb.append(tempString);
	                  
	              }
	              reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        String UserHome=System.getProperty("user.home")+"/test.xml";	        
	        try {
	            //创建文件字节输出流对象，准备向d.txt文件中写出数据,true表示在原有的基础上增加内容
	            FileOutputStream fout=new FileOutputStream(UserHome,false);
	    		tbUtil.writeStrToOutputStream(fout, sb.toString().replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", ""));
//	            
//	            System.out.println("请写出一段字符串:");
//	            String msg=sb.toString().replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");
//	            
//	            /******************(方法一)按字节数组写入**********************/
//	            byte[] bytes = msg.getBytes();//msg.getBytes()将字符串转为字节数组
//	            
//	            fout.write(bytes);//使用字节数组输出到文件
//	            /******************(方法一)逐字节写入**********************/
////	            byte[] bytes = msg.getBytes();
////	            for (int i = 0; i < bytes.length; i++) {
////	                fout.write(bytes[i]);//逐字节写文件
////	            }
	            fout.flush();//强制刷新输出流
	            fout.close();//关闭输出流
	            System.out.println("写入完成！");
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
			return UserHome;
	        
	}
}
