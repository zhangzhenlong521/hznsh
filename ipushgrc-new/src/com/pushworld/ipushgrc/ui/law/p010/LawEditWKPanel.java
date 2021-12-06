package com.pushworld.ipushgrc.ui.law.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListButtonActinoListener;
import cn.com.infostrategy.ui.mdata.BillListButtonClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.ZipFileUtil;
import com.pushworld.ipushgrc.ui.law.LawOrRuleImportDialog;
import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;

/**
 * 外规维护!! 先新建一条外规主表记录，然后点击编写条目进行内容编辑。
 * 
 * @author xch
 * 
 */
public class LawEditWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener, BillListButtonActinoListener {

	private BillListPanel billList_law = null; // 外规主表列表
	private WLTButton btn_showword; // 导出word
	private WLTButton btn_editlawitem = null, btn_import; // 编辑条目 按钮
	private WLTButton btn_insertLaw = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); // 新增按钮
	private WLTButton btn_updateLaw = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); // 修改按钮
	private WLTButton btn_deleteLaw = WLTButton.createButtonByType(WLTButton.LIST_DELETE); // 删除按钮
	private WLTButton btn_export; // 导出所有
	private WLTButton btn_format; // 格式化发布机构。
	private WLTButton btn_importAllLaw;
	private String cachePath = System.getProperty("ClientCodeCache"); // 得到客户端缓存位置。
	private TBUtil tbUtil = new TBUtil();
	private String serverPath = null;
	private String dbName = null; // 导入导出 选择的 数据源
	private HashMap compareTable = null; // 导入数据包时，两个表名以及字段的对比存放map
	private String ifShowImportAndExportAllButton = tbUtil.getSysOptionStringValue("是否显示法规导入导出按钮", "N"); // 是否显示导入导出所有法规的按钮
	private String ImportAndExportDBName = tbUtil.getSysOptionStringValue("法规导入导出数据源", "null;null"); // 导入导出的数据源
	private String exportTableNames[] = new String[] { "law_law", "law_law_item" };
	private boolean importFlag = true; // 全部导入是否成功标志
	private String importMsg = null;//导入回馈信息

	/**
	 * 初始化面板!
	 */
	public void initialize() {
		billList_law = new BillListPanel("LAW_LAW_CODE1"); //
		billList_law.addBillListHtmlHrefListener(this); // 点击html事件
		btn_deleteLaw.addActionListener(this); // 删除事件
		btn_showword = new WLTButton("Word导出", "zt_009.gif");
		btn_showword.addActionListener(this);
		btn_editlawitem = new WLTButton("编辑条目"); // 编辑条目，弹出一个条目对话框。法规的子表。左右结构。
		btn_editlawitem.addActionListener(this); //
		btn_import = new WLTButton("导入"); //
		btn_import.addActionListener(this);
		String[] ifshowbtn = ifShowImportAndExportAllButton.split(";"); // 得到系统参数中配置的
		// 按钮选项。是否显示导入所有，导出所有，格式化。
		List btnList = new ArrayList();
		btnList.add(btn_insertLaw); //
		btnList.add(btn_updateLaw);
		btnList.add(btn_deleteLaw);
		btnList.add(btn_editlawitem);
		btnList.add(btn_import);
		if (ifshowbtn[0] != null && "Y".equals(ifshowbtn[0])) { // 导入所有按钮显示
			btn_importAllLaw = new WLTButton("导入所有");
			btn_importAllLaw.addActionListener(this);
			btnList.add(btn_importAllLaw);
		}
		if (ifshowbtn.length > 1 && ifshowbtn[1] != null && "Y".equals(ifshowbtn[1])) { // 导出所有按钮显示
			btn_export = new WLTButton("导出所有");
			btn_export.addActionListener(this);
			btnList.add(btn_export);
		}
		if (ifshowbtn.length > 2 && ifshowbtn[2] != null && "Y".equals(ifshowbtn[2])) { // 这个按钮以后可去掉。按钮用于把发布机构数字ID转换为名称。
			btn_format = new WLTButton("格式化");
			btn_format.addActionListener(this);
			btnList.add(btn_format);
		}
		btnList.add(btn_showword);
		billList_law.addBatchBillListButton((WLTButton[]) btnList.toArray(new WLTButton[0]));
		billList_law.addBillListButtonActinoListener(this);//新增和修改法规后处理状态逻辑【李春娟/2015-04-21】
		billList_law.repaintBillListButton(); //
		this.add(billList_law); //

	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_editlawitem) {
			onEditLawItem(); //
		} else if (obj == btn_deleteLaw) {
			onDeleteLawItem();
		} else if (obj == btn_import) {
			onImport();
		} else if (obj == btn_export) {
			onExportAllLaw();
		} else if (obj == btn_importAllLaw) {
			onImportAllLaw();
		} else if (obj == btn_format) {
			onFormat();
		} else if (obj == btn_showword) {
			onWordshow();
		}
	}

	/**
	 * 编辑外规条目
	 */
	private void onEditLawItem() {
		BillVO selectLaw = billList_law.getSelectedBillVO();
		if (selectLaw == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		LawItemEditDialog dialog = new LawItemEditDialog(this, "编辑条目(修改后需点击保存！)", 800, 550, selectLaw); //
		dialog.setVisible(true); //
		if (dialog.isHaveChanged()) { // 如果对条目进行过修改或新增。那么就添加内规预警!
			insertAlarm(selectLaw, "update");
		}
	}

	/**
	 * 判断并插入内规预警! 目前内规预警做的比较简陋，后期需要补充 对某条外规做了哪些修改，比如：修改了2条条目内容，删除了1条条目，新增了3条。
	 * 最好能做出cvs对比文字效果。
	 */
	public void insertAlarm(BillVO _selectLaw, String _type) {
		try {
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select * from rule_rule where reflaw like '%;" + _selectLaw.getStringValue("id") + ";%'"); // 查找所有和此法规关联的制度。
			if (vos == null || vos.length == 0) {
				return;
			}
			//优化制度预警【李春娟/2015-12-26】
			ArrayList insertSQLlist = new ArrayList();
			String currdate = UIUtil.getServerCurrDate();
			String userid = ClientEnvironment.getInstance().getLoginUserID();
			String userdept = ClientEnvironment.getInstance().getLoginUserDeptId();
			InsertSQLBuilder sql = new InsertSQLBuilder("rule_alarm");
			sql.putFieldValue("alarmsource", "法律法规--法规维护");
			sql.putFieldValue("alarmsourcetab", "law_law");
			sql.putFieldValue("alarmtargettab", "rule_rule");
			sql.putFieldValue("state", "未处理");
			sql.putFieldValue("alarmdate", currdate);
			sql.putFieldValue("creator", userid);
			sql.putFieldValue("createdept", userdept);
			sql.putFieldValue("alarmsourcepk", _selectLaw.getStringValue("id"));
			for (int i = 0; i < vos.length; i++) {
				sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_RULE_ALARM"));
				sql.putFieldValue("ruleid", vos[i].getStringValue("id"));
				sql.putFieldValue("rulecode", vos[i].getStringValue("rulecode"));
				sql.putFieldValue("rulename", vos[i].getStringValue("rulename"));
				if (_type.equals("update")) {
					sql.putFieldValue("alarmreason", "《" + _selectLaw.getStringValue("lawname") + "》法规内容已修改，请更新相关制度!");
				} else {
					sql.putFieldValue("alarmreason", "《" + _selectLaw.getStringValue("lawname") + "》法规已删除，请更新相关制度!");
				}
				insertSQLlist.add(sql.getSQL());
			}
			UIUtil.executeBatchByDS(null, insertSQLlist);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 删除外规!
	 */
	private void onDeleteLawItem() {
		try {
			BillVO[] selectLaw = billList_law.getSelectedBillVOs();
			if (selectLaw.length == 0) {
				MessageBox.showSelectOne(this);
				return;
			} else {
				if (MessageBox.showConfirmDialog(this, "您确定删除这[" + (selectLaw.length) + "]条记录吗?") != JOptionPane.YES_OPTION) {
					return;
				}
			}
			List list = new ArrayList();
			for (int i = 0; i < selectLaw.length; i++) {
				list.add(selectLaw[i].getStringValue("id"));
			}
			String insql = tbUtil.getInCondition(list);
			List sqllist = new ArrayList();
			sqllist.add("delete from law_law where id in (" + insql + ")"); // 删除外规主表数据
			sqllist.add("delete from law_law_item where lawid in ( " + insql + ")");// 级联删除条文记录
			UIUtil.executeBatchByDS(null, sqllist);
			// insertAlarm(selectLaw, "delete"); // 制度预警! 删除先不做预警。
			billList_law.removeSelectedRows();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		showLawHtml();
	}

	/*
	 * 导入法规
	 */
	private void onImport() {
		LawOrRuleImportDialog importDialog = new LawOrRuleImportDialog(billList_law, "法律法规导入", 800, 700, "law");
		importDialog.setVisible(true);
		if (importDialog.getCloseType() == 1) { // 已经导入成功过
			billList_law.refreshData(); // 刷新列表数据。
		}
	}

	/**
	 * 填出html框，显示外规的详细
	 */
	private void showLawHtml() {
		String lawid = billList_law.getSelectedBillVO().getStringValue("id");
		new LawShowHtmlDialog(billList_law, lawid);
	}

	/**
	 * 导出所有法规。
	 */
	public void onExportAllLaw() {
		String dbNames[] = ImportAndExportDBName.split(";"); // 从平台配置中读取导出数据源配置
		if (dbNames.length > 1) {
			if (dbNames[1] != null && !"null".equals(dbNames[1]) && !"".equals(dbNames[1])) {
				dbName = dbNames[1];
			} else {
				dbName = null;
			}
		} else {
			dbName = null;
		}
		if (dbName == null) {
			if (!MessageBox.confirm(this, "您确定要导出默认数据库中的数据吗？")) {
				return;
			}
		}

		DataSourceVO[] dsVo = ClientEnvironment.getInstance().getDataSourceVOs();
		if (dbName != null) {
			boolean ifHaveTheDb = false;
			for (int i = 0; i < dsVo.length; i++) {
				if (dbName.equals(dsVo[i].getName())) { // 如果存在设定数据源
					ifHaveTheDb = true;
					break;
				}
				;
			}
			if (!ifHaveTheDb) {
				MessageBox.show(this, "系统weblight.xml中不存在数据源【" + dbName + "】\r\n解决办法：\r\n在系统工具->系统参数中配置【法规导入导出数据源】使参数值与weblight.xml数据源名称一致");
				return;
			}
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("请选择存放目录");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int flag = chooser.showSaveDialog(this);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		final String str_path = chooser.getSelectedFile().getAbsolutePath(); //
		if (str_path == null) {
			return;
		}
		File f = new File(str_path);
		if (!f.exists()) {
			MessageBox.show(this, "路径:" + str_path + " 不存在!");
			return;
		}
		if (dbName != null) {
			String tableNames = JOptionPane.showInputDialog(this, "请输入要导出的表名,用分号隔开");
			if (tableNames != null && !tableNames.equals("")) {
				exportTableNames = tableNames.split(";");
			}
		}
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				long li_1 = System.currentTimeMillis();
				operationExport((SplashWindow) e.getSource(), str_path, exportTableNames); // 导出
			}
		}, 600, 130, 300, 300, false); // 提示信息显示不完整，故增加窗口高度【李春娟/2012-03-27】

	}

	/*
	 * 导出
	 */
	private void operationExport(SplashWindow _splash, String _path, String[] tables) { // 导出!
		StringBuffer showMessage_sb = new StringBuffer();
		StringBuffer sb = new StringBuffer(); // 写main.xml主文件
		String version = "1.0";
		sb.append("<?xml version=\"" + version + "\" encoding=\"GBK\"?>\r\n");
		String currTime = null;
		try {
			currTime = UIUtil.getServerCurrTime();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String p = "exportDB//" + System.currentTimeMillis();
		List<String> sql_insert_history = new ArrayList<String>();
		String table_sb = "";
		for (int i = 0; i < tables.length; i++) {
			String tablename = tables[i];
			StringBuffer tableSB = new StringBuffer();
			tableSB.append("<table name=\"" + tablename + "\">\r\n");
			try {
				// 创建表名的目录!!!
				String str_newPath = cachePath + "//" + p; // 加上表名!
				File filenewdir = new File(str_newPath); //
				if (!filenewdir.exists()) {
					filenewdir.mkdirs(); // 创建目录!!!以后再搞序号前辍
				}
				int li_beginNo = 0; // 起始号!
				int li_cout = 0; //

				String con_sql = "select count(*) from " + tablename + " where 1=1";
				int li_countall = Integer.parseInt(UIUtil.getStringValueByDS(dbName, con_sql)); //
				if (li_countall == 0) {
					continue;
				}
				int li_downedCount = 0; //
				if (tables[i].equals("law_lawprop_1")) {
					showMessage_sb.append("共导出外规【" + li_countall + "】条");
				} else if (tables[i].equals("law_law")) {
					showMessage_sb.append("共导出外规【" + li_countall + "】条");
				}

				while (1 == 1) { // 死循环
					long ll_1 = System.currentTimeMillis(); //
					if (li_beginNo >= li_countall) {
						break;
					}
					HashMap returnMap = getService().getXMlFromTable1000Records(dbName, tablename, li_beginNo);
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
					String str_fileName = tablename + "_" + (100000 + li_cout) + ".xml"; //
					FileOutputStream fileOut = new FileOutputStream(str_newPath + "\\" + str_fileName, false); //
					tbUtil.writeStrToOutputStream(fileOut, str_xml); // 输出至文件
					long ll_2 = System.currentTimeMillis(); //
					tableSB.append("<file path=\"" + str_fileName + "\" count=\"" + li_recordCount + "\"/>\r\n");
					_splash.setWaitInfo("处理[" + str_fileName + "],耗时[" + (ll_2 - ll_1) + "]!!\r\n本表完成比例[" + li_downedCount + "/" + li_countall + "=" + li_perCent + "%],总比例[" + (i + 1) + "/" + tables.length + "]"); //
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
			tableSB.append("</table>\r\n");
			table_sb += tableSB.toString();
		}
		sb.append("<root createdate=\"" + currTime + "\">\r\n");
		sb.append(table_sb);
		sb.append("</root>");
		FileOutputStream fileout = null;
		try {
			_splash.setWaitInfo(" 生成main.xml主文件!");
			fileout = new FileOutputStream(cachePath + "\\" + p + "\\main.xml", false);
			_splash.setWaitInfo(" 正在保存导出历史...");
			UIUtil.executeBatchByDS(null, sql_insert_history);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tbUtil.writeStrToOutputStream(fileout, sb.toString());
		try {
			ZipFileUtil zip = new ZipFileUtil();
			_splash.setWaitInfo(" 正在打包!请稍等...");
			String fileName = "外规数据包.jar";
			zip.zip(_path + "\\" + fileName, cachePath + "\\" + p);
			_splash.dispose();
			MessageBox.show(this, "数据导出成功!" + showMessage_sb.toString());
			File file = new File(cachePath + "\\" + p);
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
		}
	}

	public void initXML() { // 加载xml 数据表结构对比xml
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("外规表结构(*.xml)", "xml");
		chooser.addChoosableFileFilter(filter);
		int flag = chooser.showOpenDialog(this);
		File file = chooser.getSelectedFile();
		if (file != null) {
			SAXBuilder builder = new SAXBuilder();
			try {
				org.jdom.Document rootNode = builder.build(file);
				Element rootElement = rootNode.getRootElement();
				List tableList = rootElement.getChildren();
				if (tableList.size() > 0) {
					compareTable = new HashMap();
				}
				for (int i = 0; i < tableList.size(); i++) {
					Element tableElement = (Element) tableList.get(i);
					String tableName = tableElement.getAttributeValue("name").toUpperCase();
					String tableReName = tableElement.getAttributeValue("rename").toUpperCase();
					List colList = tableElement.getChildren();
					compareTable.put(tableName.toUpperCase(), tableReName);
					for (int j = 0; j < colList.size(); j++) {
						Element colElement = (Element) colList.get(j);
						String colName = colElement.getAttributeValue("name").toUpperCase();
						String colReName = colElement.getAttributeValue("rename").toUpperCase();
						compareTable.put(tableName + "_" + colName, colReName);
					}
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 导入所有的法规。这个方法已经扩展
	 */
	public void onImportAllLaw() {
		String dbNames[] = ImportAndExportDBName.split(";"); // 从平台配置中读取导入数据源名称
		if (dbNames.length > 1) {
			if (dbNames[0] != null && !"null".equals(dbNames[0]) && !"".equals(dbNames[0])) {
				dbName = dbNames[0];
			} else {
				dbName = null;
			}
		}
		/*
		 * if (MessageBox.showConfirmDialog(this, "是否有法规表结构对比的xml文件？", "法规更新提示",
		 * JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { initXML(); }
		 */
		final HashMap conditionMap = new HashMap();
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("法规更新包(*.zip,*.rar)", "zip", "rar");
		chooser.addChoosableFileFilter(filter);
		int flag = chooser.showOpenDialog(this);
		if (flag == 1) {
			return;
		}
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		try {
			getService().createFolder("importDB");
			serverPath = UIUtil.upLoadFile("importDB", file.getName(), false, file.getAbsolutePath(), "", true);
			if (!serverPath.contains("importDB")) { //平台后来升级后，上传返回路径发生变化！
				String str = System.getProperty("WLTUPLOADFILEDIR");
				serverPath = str + "/" + "importDB" + serverPath;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (serverPath == null || serverPath.equals("")) {
			MessageBox.show(this, "文件上传失败!请联系管理员!");
			return;
		}
		final int returnsel = MessageBox.showOptionDialog(this, "请选择本次法规升级类型[增量导入、整体替换普信法规库]", "请选择升级类型", new String[] { "增量导入", "整体替换" });
		// 备份数据到temp表。

		if (returnsel == 0) {
			// 增量导入 需要判断本库中是否有负记录，然后找到绝对值最大的负的 进行导入.
			conditionMap.put("type", "增量");
		} else if (returnsel == 1) {
			conditionMap.put("type", "替换");
			// 覆盖法规库。 可以直接把负数据清空掉。然后全部导入
		} else if (returnsel == -1) {
			// 点击叉。
			return;
		}
		//		returnsel = MessageBox.showConfirmDialog(this, "是否需要备份法规表!", "备份提示!", JOptionPane.YES_NO_OPTION);
		//		if (returnsel == JOptionPane.YES_OPTION) {
		//			// 需要备份
		//			conditionMap.put("dump", true);
		//		} else if (returnsel == JOptionPane.NO_OPTION) {
		//			conditionMap.put("dump", false);
		//		} else {
		//			return;
		//		}
		conditionMap.put("dump", false);
		HashMap nagetiveItem = new HashMap();
		nagetiveItem.put("law_law_id", null);
		nagetiveItem.put("law_law_item_id", null);
		nagetiveItem.put("law_law_item_lawid", null);
		nagetiveItem.put("law_law_item_parentid", null);
		nagetiveItem.put("law_law_reflaw", null);//修订的法规【李春娟/2015-04-21】
		nagetiveItem.put("law_law_reflaw2", null);//废止的法规【李春娟/2015-04-21】
		conditionMap.put("nagetiveitem", nagetiveItem); // 需要负的字段
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(final ActionEvent e) {
				try {
					new Timer().schedule(new TimerTask() {
						public void run() { // 需要用线程来控制是否已经把数据加载进来了。
							String schedule = null;
							try {
								schedule = getService().getUpdataLawDataSchedule();
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (schedule != null && schedule.equalsIgnoreCase("success")) {
								this.cancel();
							} else {
								SplashWindow w = (SplashWindow) e.getSource();
								w.setWaitInfo(schedule);
							}
						}
					}, 20, 1000);
					importMsg = getService().importXmlToTable1000Records(dbName, serverPath, compareTable, conditionMap);
				} catch (Exception e1) {
					importFlag = false;
					MessageBox.showException((SplashWindow) e.getSource(), new Throwable(e1));
					e1.printStackTrace();
					((SplashWindow) e.getSource()).closeSplashWindow();
				}
			}
		}, 600, 130, 300, 300, false); // 提示信息显示不完整，故增加窗口高度【李春娟/2012-03-27】
		if (importFlag == true) {
			String msg = "导入成功!";
			if (importMsg != null && !importMsg.equals("")) {
				msg += importMsg;
			}
			MessageBox.show(this, msg);
		}
	}

	public IPushGRCServiceIfc getService() {
		IPushGRCServiceIfc service = null;
		try {
			service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return service;
	}

	/**
	 * 将所有发部机构的ID改为名称
	 */
	public void onFormat() {
		if (MessageBox.showConfirmDialog(this, "格式化功能是把发布机构数字ID改为名称\r\n是否继续？") != JOptionPane.YES_OPTION) {
			return;
		}
		SplashWindow win = new SplashWindow(this, new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				try {
					SplashWindow w = (SplashWindow) e.getSource();
					HashMap map = UIUtil.getHashMapBySQLByDS(null, "select id,name from bsd_lawissuecorp");
					int count = Integer.parseInt(UIUtil.getStringValueByDS(null, "select count(id) from law_law"));
					for (int i = 0; i < 5; i++) {
						int from = i * 500;
						w.setWaitInfo("总计：" + count + "  当前" + from);
						List updateList = new ArrayList();
						String laws[][] = UIUtil.getStringArrayByDS(null, "select id, issuecorp from law_law limit " + from + ",500"); // 每次500
						for (int j = 0; j < laws.length; j++) {
							if (laws[j][1] != null && !laws[j][1].equals("")) {
								String issuecorp[] = laws[j][1].split(";");
								StringBuffer newissuecorp = new StringBuffer();
								int index = 0;
								for (int k = 0; k < issuecorp.length; k++) {
									if (issuecorp[k] != null && !issuecorp[k].equals("")) {
										Object obj = map.get(issuecorp[k]);
										if (index == 0) {
											if (obj == null) {
												newissuecorp.append(";" + issuecorp[k] + ";");
											} else {
												newissuecorp.append(";" + obj.toString() + ";");
											}
										} else {
											if (obj == null) {
												newissuecorp.append(issuecorp[k] + ";");
											} else {
												newissuecorp.append(obj.toString() + ";");
											}
										}
										index++;
									}
								}
								String updateSql = "update law_law set issuecorp = '" + newissuecorp.toString() + "' where id = '" + laws[j][0] + "'";
								updateList.add(updateSql);
							}
						}
						UIUtil.executeBatchByDS(null, updateList);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		});
	}

	/**
	 * word预览
	 * */
	private void onWordshow() {
		BillVO billVO = billList_law.getSelectedBillVO();
		if (billVO == null || "".equals(billVO)) {
			MessageBox.showSelectOne(this);
			return;
		}
		new LawWordBuilder(this, billVO.getStringValue("lawname"), billVO.getStringValue("id"));
	}

	/**
	 * 新增或修改法规后执行逻辑，修改修订和废止的法规的状态【李春娟/2015-04-21】
	 * @param _billvo
	 */
	public void afterAddOrEditLaw(BillVO _billvo) {
		if (_billvo == null) {
			return;
		}
		//更新修订和废止的法规状态
		ArrayList sqlList = new ArrayList();
		//修订的法规数据格式：;-23248;-23251;-23217$-335630;
		String reflaw = _billvo.getStringValue("reflaw");
		ArrayList reflawList = new ArrayList();//修订的法规主表id
		ArrayList reflawitemList = new ArrayList();//修订的法规子表id
		if (reflaw != null && reflaw.contains(";")) {
			String[] splits = TBUtil.getTBUtil().split(reflaw, ";");
			if (splits != null) {
				for (int k = 0; k < splits.length; k++) {
					if (splits[k] != null && splits[k].contains("$")) {//如果关联了修订的法规子表
						String[] ids = TBUtil.getTBUtil().split(splits[k], "$");
						if (!reflawList.contains(ids[0])) {//去重复
							reflawList.add(ids[0]);
						}
						if (!reflawitemList.contains(ids[1])) {//去重复
							reflawitemList.add(ids[1]);
						}
					} else {//如果只关联了修订的法规主表
						if (!reflawList.contains(splits[k])) {//去重复
							reflawList.add(splits[k]);
						}
					}
				}
				if (reflawList.size() > 0) {
					//如果已经设置为"失效"，则无需再设置为"部分失效"
					sqlList.add("update law_law set state='部分失效' where state<>'失效' and id in(" + TBUtil.getTBUtil().getInCondition(reflawList) + ")");
				}
				if (reflawitemList.size() > 0) {
					sqlList.add("update law_law_item set state='Y' where id in(" + TBUtil.getTBUtil().getInCondition(reflawitemList) + ")");
				}
			}
		}
		//废止的法规数据格式：;-23264;-23263;-23262;-23261;
		String reflaw2 = _billvo.getStringValue("reflaw2");
		ArrayList reflaw2List = new ArrayList();//废止的法规id
		if (reflaw2 != null && reflaw2.contains(";")) {
			String[] splits = TBUtil.getTBUtil().split(reflaw2, ";");
			if (splits != null) {
				for (int k = 0; k < splits.length; k++) {
					if (!reflaw2List.contains(splits[k])) {//去重复
						reflaw2List.add(splits[k]);
					}
				}
				if (reflaw2List.size() > 0) {
					sqlList.add("update law_law set state='失效' where id in(" + TBUtil.getTBUtil().getInCondition(reflaw2List) + ")");
				}
			}
		}
		try {
			if (sqlList.size() > 0) {
				UIUtil.executeBatchByDS(null, sqlList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onBillListAddButtonClicked(BillListButtonClickedEvent event) throws Exception {
		afterAddOrEditLaw(event.getCardPanel().getBillVO());
	}

	public void onBillListEditButtonClicked(BillListButtonClickedEvent event) {
		afterAddOrEditLaw(event.getCardPanel().getBillVO());
	}

	public void onBillListAddButtonClicking(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListButtonClicked(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListDeleteButtonClicked(BillListButtonClickedEvent event) {

	}

	public void onBillListDeleteButtonClicking(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListEditButtonClicking(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListLookAtButtonClicked(BillListButtonClickedEvent event) {

	}

	public void onBillListLookAtButtonClicking(BillListButtonClickedEvent event) {

	}
}
