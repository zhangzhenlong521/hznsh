package com.pushworld.ipushgrc.ui.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
public class ExportDBTableToXml extends AbstractWorkPanel implements ActionListener {

	private JComboBox dsComBobox_export = null; //数据源!!!
	private JTextArea textArea = null; //
	private WLTButton btn_custtables = null; //

	private JComboBox dsComBobox_import = null; //导入时选择的数据源!!!
	private WLTButton btn_import = null; //

	private TBUtil tbUtil = new TBUtil(); //

	private FrameWorkMetaDataServiceIfc services = null; //

	public void initialize() {
		DataSourceVO[] dsVOs = ClientEnvironment.getInstance().getDataSourceVOs(); //所有数据源!!!
		dsComBobox_export = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			dsComBobox_export.addItem(dsVOs[i].getName()); //数据源名称!!
		}
		dsComBobox_export.setBounds(10, 10, 95, 20); //

		textArea = new JTextArea("cmp_issue,cmp_event,cmp_ward,cmp_check_item"); //
		textArea.setFont(LookAndFeel.font); ////
		textArea.setLineWrap(true); //
		JScrollPane scrollPanel = new JScrollPane(textArea); //
		scrollPanel.setBounds(10, 40, 500, 150); //

		btn_custtables = new WLTButton("导出指定表");
		btn_custtables.setBounds(520, 40, 125, 20); //
		btn_custtables.addActionListener(this); //

		JPanel panel_1 = WLTPanel.createDefaultPanel(null); //
		panel_1.add(dsComBobox_export); //
		panel_1.add(scrollPanel); //
		panel_1.add(btn_custtables); //

		//导入页签!!
		dsComBobox_import = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			dsComBobox_import.addItem(dsVOs[i].getName()); //数据源名称!!
		}
		dsComBobox_import.setBounds(10, 10, 100, 20); //

		btn_import = new WLTButton("导入XML文件"); //
		btn_import.setBounds(120, 10, 125, 20); //
		btn_import.addActionListener(this); //

		JPanel panel_2 = WLTPanel.createDefaultPanel(null); //
		panel_2.add(dsComBobox_import); //
		panel_2.add(btn_import); //

		JTabbedPane tabbedPanel = new JTabbedPane(); //
		tabbedPanel.addTab("导出应用", panel_1); //
		tabbedPanel.addTab("导入应用", panel_2); //

		this.setLayout(new BorderLayout()); //
		this.add(tabbedPanel); //
	}

	public void actionPerformed(ActionEvent e) {
	     if (e.getSource() == btn_custtables) { //导出指定表!!
			String str_text = textArea.getText(); //
			if (str_text == null || str_text.trim().equals("")) {
				JOptionPane.showMessageDialog(this, "必须表名"); //
				return; //
			}
			str_text = str_text.trim(); //
			String[] str_tables = tbUtil.split(str_text, ","); //
			String[][] str_tabPKs = new String[str_tables.length][2]; //
			for (int i = 0; i < str_tables.length; i++) {
				String str_tabName = str_tables[i]; //
				String str_pk = "id"; //
				if (str_tabName.indexOf("(") > 0) { //如果有括号,即指定了主键名
					str_tabName = str_tables[i].substring(0, str_tables[i].indexOf("(")); //表名!
					str_pk = str_tables[i].substring(str_tables[i].indexOf("(") + 1, str_tables[i].indexOf(")")); //主键字段名!
					if (str_pk.trim().equals("")) {
						str_pk = null;
					}
				}
				//System.out.println("表名[" + str_tabName + "],主键[" + str_pk + "]"); ///
				str_tabPKs[i] = new String[] { str_tabName, str_pk }; //
			}
			onExportXml(str_tabPKs); //
			if(MessageBox.confirm(this, "数据导出成功！是否清空原有数据！")){
				List delSql = new ArrayList();
				try {
					dumpTable(null,new String []{"cmp_issue","cmp_event","cmp_ward","cmp_check_item"},"temp");
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				delSql.add("delete from cmp_issue where 1=1 ");
				delSql.add("delete from cmp_event where 1=1 ");
				delSql.add("delete from cmp_ward where 1=1 ");
				delSql.add("delete from cmp_check_item where 1=1 ");
				try {
					UIUtil.executeBatchByDS(null, delSql);
					MessageBox.show(this, "数据备份成功！");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} else if (e.getSource() == btn_import) { //导入!!!
			onImportXml(); //
		}
	}
	public void dumpTable(String dbName, String tableName[], String dumpName) throws Exception {
		String[][] str_allTables = UIUtil.getCommonService().getAllSysTableAndDescr(null, null, false, true); //
		List createList= new ArrayList();
		for (int j = 0; j < tableName.length; j++) {
			String tempTableName = tableName[j]+"_"+dumpName;
			for (int i = 0; i < str_allTables.length; i++) {
				
				if (tempTableName.equalsIgnoreCase(str_allTables[i][0])) {
					// 如果找到了备份表。那么就删掉备份表。重新备份
					String dropTableSql = "drop table " + tempTableName;
					UIUtil.executeUpdateByDS(dbName, dropTableSql);
					// 删除成功在备份
					break;
				}
			}
			String createTableSql = "create table " + tempTableName + " as select * from " + tableName[j];
			createList.add(createTableSql);
		}
		// 重备份
	
		UIUtil.executeBatchByDS(dbName, createList);
		// 备份成功！
	}
	private void onExportXml(final String[][] _tablePKs) {
		if (JOptionPane.showConfirmDialog(this, "你真的想执行此导出操作么?!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		//弹出文件选择框!选择一个路径!!!
		final String str_path = getChooseDir(); //
		if (str_path == null) {
			return;
		}
		long ll_begin = System.currentTimeMillis();
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				export((SplashWindow) e.getSource(), str_path, _tablePKs); //导出
			}
		}, 600, 125, 300, 300); ///
		long ll_end = System.currentTimeMillis();
		JOptionPane.showMessageDialog(this, "处理结束,总共耗时[" + ((ll_end - ll_begin) / 1000) + "]秒!!"); //
	}

	private String getChooseDir() {
		JFileChooser chooser = new JFileChooser(new File("C:/"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("选择一个目录!"); //
		int li_rewult = chooser.showSaveDialog(this);
		if (li_rewult == JFileChooser.CANCEL_OPTION) {
			return null;// 点击取消什么也不做
		}
		if (chooser.getSelectedFile().isFile()) {
			MessageBox.show(this, "必须选择一个目录,而不是文件!!"); //
			return null;
		}
		String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
		if (str_pathdir.endsWith("\\")) {
			str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
		}
		return str_pathdir; //
	}

	/**
	 * 导出!!!
	 * @param _splash
	 * @param _path
	 * @param _tablePKs
	 */
	private void export(SplashWindow _splash, String _path, String[][] _tablePKs) {
		try {
			for (int i = 0; i < _tablePKs.length; i++) { //遍历所有表!
				String str_tableName = _tablePKs[i][0]; //表名
				String str_pkName = _tablePKs[i][1]; //主键名

				//创建表名的目录!!!
				String str_newPath = _path + "\\" + str_tableName; //加上表名!
				File filenewdir = new File(str_newPath); //
				if (!filenewdir.exists()) {
					filenewdir.mkdirs(); //创建目录!!!以后再搞序号前辍
				}
				String str_daName = (String) dsComBobox_export.getSelectedItem(); //

				int li_beginNo = 0; //起始号!因为有负数,所以需要搞一把!
				if (str_pkName != null) {
					String str_minValue = UIUtil.getStringValueByDS(str_daName, "select min(" + str_pkName + ") from " + str_tableName); //
					if (str_minValue != null && !str_minValue.equals("")) {
						//System.out.println(str_pkName + "," + str_tableName);  //
						li_beginNo = Integer.parseInt(str_minValue)-1; //用最小值!!!
					}
				}
				int li_cout = 0; //
				int li_countall = Integer.parseInt(UIUtil.getStringValueByDS(str_daName, "select count(*) from " + str_tableName)); //
				int li_downedCount = 0; //
				while (1 == 1) { //死循环
					long ll_1 = System.currentTimeMillis(); //
					HashMap returnMap = getService().getXMlFromTable1000Records(str_daName, str_tableName, str_pkName, li_beginNo); //
					if (returnMap == null) { //如果为空了则直接返回
						break; //
					}
					int li_recordCount = (Integer) returnMap.get("记录数"); //实际的记录数,应该除最后一页外,其他的都是500
					li_downedCount = li_downedCount + li_recordCount; //
					int li_perCent = ((li_downedCount * 100) / li_countall); //百分比!  50/100
					li_beginNo = (Integer) returnMap.get("结束点"); //实际内容的结束号!					
					String str_xml = (String) returnMap.get("内容"); //
					li_cout++; //
					String str_fileName = str_tableName + "_" + (100000 + li_cout) + ".xml"; //
					FileOutputStream fileOut = new FileOutputStream(str_newPath + "\\" + str_fileName, false); //
					tbUtil.writeStrToOutputStream(fileOut, str_xml); //输出至文件
					long ll_2 = System.currentTimeMillis(); //
					_splash.setWaitInfo("处理[" + str_fileName + "],耗时[" + (ll_2 - ll_1) + "]!!\r\n本表完成比例[" + li_downedCount + "/" + li_countall + "=" + li_perCent + "%],总比例[" + (i + 1) + "/" + _tablePKs.length + "]"); //
					if (str_pkName == null) { //如果主键为空,因为是一次取出所有数据!则直接退出!!!比如pub_sequence表!!
						break;
					}
					Thread.currentThread().sleep(100); //休息一会!!
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}


	//点击导入按钮!!
	private void onImportXml() {
		if (JOptionPane.showConfirmDialog(this, "你真的想执行此导入操作么?!\r\n强烈建议操作之前先备份一下数据库!!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}

		//弹出文件选择框!选择一个路径!!!
		final String str_path = getChooseDir(); //
		if (str_path == null) {
			return;
		}
		long ll_begin = System.currentTimeMillis();
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				importXml((SplashWindow) e.getSource(), str_path); //
			}
		}, 600, 125, 300, 300); ///
		long ll_end = System.currentTimeMillis();
		JOptionPane.showMessageDialog(this, "导入结束,总共耗时[" + ((ll_end - ll_begin) / 1000) + "]秒!!"); //
	}

	/**
	 * 导入数据!!!
	 * @param _splash
	 * @param _rootDir
	 */
	private void importXml(SplashWindow _splash, String _rootDir) {
		try {
			String str_dsName = (String) dsComBobox_import.getSelectedItem(); //
			File rootDir = new File(_rootDir); //
			File[] tableDirFiles = rootDir.listFiles(); //表名目录!!
			for (int f = 0; f < tableDirFiles.length; f++) { //遍历所有目录
				if (tableDirFiles[f].isDirectory()) { //必须是目录
					String str_tableName = tableDirFiles[f].getName(); //目录名就是表名!!!
					File[] files = tableDirFiles[f].listFiles(); //遍历所有xml文件!!
					for (int i = 0; i < files.length; i++) {
						long ll_1 = System.currentTimeMillis(); //
						String str_xml = tbUtil.readFromInputStreamToStr(new FileInputStream(files[i])); //
						getService().importXmlToTable1000Records(str_dsName, files[i].getName(), str_xml); //导入一张表!!
						long ll_2 = System.currentTimeMillis(); //
						_splash.setWaitInfo("导入[" + files[i].getName() + "],耗时[" + (ll_2 - ll_1) + "],本表完成比例[" + (i + 1) + "/" + files.length + "],总共比例[" + (f + 1) + "/" + tableDirFiles.length + "]"); //
						Thread.currentThread().sleep(100); //休息一会!!
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
			MessageBox.showException(this, e);
		}
	}

	public FrameWorkMetaDataServiceIfc getService() throws Exception {
		if (services != null) {
			return services; //
		}
		services = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
		return services; //
	}
}
