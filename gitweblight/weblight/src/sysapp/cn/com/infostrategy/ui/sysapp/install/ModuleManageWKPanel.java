package cn.com.infostrategy.ui.sysapp.install;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillTreeDefaultMutableTreeNode;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillTreeCheckEditEvent;
import cn.com.infostrategy.ui.mdata.BillTreeCheckEditListener;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.sysapp.install.ModuleManageWKPanel 
 * @Description: 模块管理工具类
 * @author haoming
 * @date May 20, 2013 4:50:32 PM
 *  
*/
public class ModuleManageWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeCheckEditListener, DocumentListener {

	private static final long serialVersionUID = 1L;
	private WLTButton btn_export_condition;
	private JTextArea text_config, text_main, text_condition; //主子表 主外键配置关系;根据主表导出；根据sql条件导出;
	private TBUtil tbutil = new TBUtil();
	private BillTreePanel treepanel;

	@Override
	public void initialize() {
		WLTTabbedPane tabPanel = new WLTTabbedPane();
		tabPanel.addTab("<html><font color='blue'><b>数据导出</b></font></html>", getExportDataPanel());
		tabPanel.addTab("<html><font color='#203928'><b>代码引用校验</b></font></html>", new JPanel());
		tabPanel.addTab("<html><font color='#102837'><b>模块开关导出</b></font></html>", new JPanel());
		tabPanel.addTab("<html><font color='#102837'><b>生成setting.xml</b></font></html>", new JPanel());
		this.add(tabPanel);
	}

	//导出数据面板
	private JComponent getExportDataPanel() {
		WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT);
		splitPanel.setDividerLocation(265);
		splitPanel.setOpaque(false);
		try {
			HashMap modules = UIUtil.commMethod("cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallDMO", "getAllModuleTablesTree", new HashMap());
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) modules.get("moduletables");// v太简单。
			Vector alltables = new Vector();//所有表
			for (int i = 0; i < node.getChildCount(); i++) {
				visitAllNodes(alltables, node.getChildAt(i), true);
			}
			treepanel = new BillTreePanel(new TMO_PUB_MODULEINSTALL());
			treepanel.getJTree().setRootVisible(false);
			treepanel.queryDataByDS((HashVO[]) alltables.toArray(new HashVO[0]), 0);
			treepanel.addBillTreeCheckEditListener(this);
			splitPanel.setLeftComponent(treepanel);
			JPanel rightPanel = new JPanel(null);
			rightPanel.setOpaque(false);
			//导出数据 表关系配置框
			text_config = new WLTTextArea(2, 2);
			text_config.getDocument().addDocumentListener(this);
			text_config.setToolTipText("<html>${主表(主键,默认ID可忽略不填);子表.外键;...}<br>例子${pub_wf_process;pub_workflowassign.processid;t3.processid}</html>");
			WLTLabel label_config = new WLTLabel();
			label_config.setBounds(20, 2, 120, 40);
			label_config.setText("主子表关系配置区:");
			label_config.setToolTipText("<html>${主表(主键,默认ID可忽略不填);子表.外键;...}<br>例子${pub_wf_process;pub_workflowassign.processid;t3.processid}</html>");

			JScrollPane scroll_config = new JScrollPane(text_config);
			scroll_config.setBounds(20, 35, 550, 90);
			rightPanel.add(scroll_config);
			rightPanel.add(label_config);
			//导出数据主表条件，目前支持只支持ID
			text_main = new WLTTextArea(2, 2);
			text_main.getDocument().addDocumentListener(this);
			text_main.setAutoscrolls(true);

			WLTLabel label_ids = new WLTLabel();
			label_ids.setBounds(20, 120, 120, 40);
			label_ids.setText("主表ID过滤条件:");
			label_ids.setToolTipText("<html>填写主表ID值，用逗号分割开<br>1,3,4,5,7,8</html>");

			JScrollPane scroll_ids = new JScrollPane(text_main);
			scroll_ids.setBounds(20, 150, 550, 80);
			rightPanel.add(label_ids);
			rightPanel.add(scroll_ids);
			//最终的导出sql。
			WLTLabel label_sql = new WLTLabel("导出的SQL语句:");
			label_sql.setBounds(20, 220, 120, 40);
			rightPanel.add(label_sql);

			text_condition = new WLTTextArea(2, 2);
			text_condition.setAutoscrolls(true);
			btn_export_condition = new WLTButton("根据SQL导出");
			btn_export_condition.addActionListener(this);
			btn_export_condition.setBounds(20, 510, 120, 25);

			JScrollPane scroll_condition = new JScrollPane(text_condition);
			scroll_condition.setBounds(20, 250, 550, 250);
			rightPanel.add(scroll_condition);
			rightPanel.add(btn_export_condition);
			rightPanel.setPreferredSize(new Dimension(580, 550));
			JScrollPane rightScrollPane = new JScrollPane(rightPanel);
			splitPanel.setRightComponent(rightScrollPane);
			return splitPanel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//得到选择的第一层节点
	private void getCheckFirstLevel(Vector _vector, TreeNode _node) {
		BillTreeDefaultMutableTreeNode node = (BillTreeDefaultMutableTreeNode) _node;
		if (node.isChecked()) {
			_vector.add(node);
		} else {
			if (node.getChildCount() > 0) {
				for (Enumeration e = node.children(); e.hasMoreElements();) {
					DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
					getCheckFirstLevel(_vector, childNode); // 继续查找该儿子
				}
			}
		}
	}

	//获取全部节点
	private void visitAllNodes(Vector _vector, TreeNode node, boolean getUserObject) {
		if (getUserObject) {
			_vector.add(((DefaultMutableTreeNode) node).getUserObject()); // 加入该结点
		} else {
			_vector.add(((DefaultMutableTreeNode) node)); // 加入该结点
		}
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				visitAllNodes(_vector, childNode, getUserObject); // 继续查找该儿子
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_export_condition) {
			doExport();
		}
	}

	//
	public void onBillTreeCheckEditChanged(BillTreeCheckEditEvent _event) {
		BillTreePanel treePanel = _event.getBillTreePanel();
		BillTreeDefaultMutableTreeNode treeNode = _event.getEditNode();
		if (treeNode != null) {
			BillVO selectVO = treePanel.getSelectedVO();
			String id = (String) selectVO.getStringValue("id");
			if (Integer.parseInt(id) < 0 && treeNode.isChecked()) {
				MessageBox.show(treePanel, "不要选择模块节点");
				_event.getBillTreePanel().setSelected(treePanel.getRootNode());
				setAllCheck(treeNode, false);
				treePanel.repaint();
				return;
			}
			BillTreeDefaultMutableTreeNode pNode = (BillTreeDefaultMutableTreeNode) treeNode.getParent(); //\
			StringBuffer configSB = new StringBuffer();
			Vector vc = new Vector();
			getCheckFirstLevel(vc, treepanel.getRootNode());
			for (Object object : vc) {
				BillTreeDefaultMutableTreeNode firstLevelNode = (BillTreeDefaultMutableTreeNode) object;
				configSB.append(buildTableConfig(firstLevelNode));
			}
			text_config.setText(configSB.toString());
		}
	}

	//根据一个节点，获取该节点下的表关系配置。
	private String buildTableConfig(BillTreeDefaultMutableTreeNode _node) {
		Vector vc = new Vector();
		visitAllNodes(vc, _node, false);
		StringBuffer configSB = new StringBuffer();
		String mainTable = "";
		for (Object object : vc) {
			BillTreeDefaultMutableTreeNode node = (BillTreeDefaultMutableTreeNode) object;
			BillVO nodeBillVO = treepanel.getBillVOFromNode(node);
			if (object == _node) {
				configSB.append("${");
				mainTable = nodeBillVO.getStringValue("name"); //获取主表
				configSB.append(nodeBillVO.getStringValue("name"));
				if (nodeBillVO.getStringValue("code") == null) {
					configSB.append("(无主键)");
				} else if (!"id".equalsIgnoreCase(nodeBillVO.getStringValue("code"))) {
					configSB.append("(" + nodeBillVO.getStringValue("code") + ")");

				}
				configSB.append(";");
			} else if (node.isChecked()) {
				BillTreeDefaultMutableTreeNode parentNode = (BillTreeDefaultMutableTreeNode) node.getParent();
				if (parentNode.isChecked()) { //如果
					String tableName = nodeBillVO.getStringValue("name");
					String foreign = nodeBillVO.getStringValue("control");
					String[] key_foreign = foreign.split("=");
					if (key_foreign.length > 0) {
						if (key_foreign[1] != null && key_foreign[1].substring(0, key_foreign[1].indexOf(".")).equalsIgnoreCase(mainTable)) {
							configSB.append(tableName + "." + key_foreign[0] + ";");
						} else {
							configSB.append(tableName + "." + foreign + ";");
						}
					} else {
						configSB.append(tableName + "." + foreign + ";");
					}
				}
			}
		}
		configSB = configSB.replace(configSB.length() - 1, configSB.length(), "");
		configSB.append("}\r\n");
		return configSB.toString();
	}

	private void setAllCheck(BillTreeDefaultMutableTreeNode _node, boolean _ischeck) {
		_node.setChecked(_ischeck);
		int childCount = _node.getChildCount();
		if (_node.getChildCount() >= 0) {
			for (Enumeration e = _node.children(); e.hasMoreElements();) {
				BillTreeDefaultMutableTreeNode childNode = (BillTreeDefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				setAllCheck(childNode, _ischeck); // 继续查找该儿子
			}
		}
	}

	public void changedUpdate(DocumentEvent e) {
	}

	//文本框内容发生变化
	public void insertUpdate(DocumentEvent e) {
		try {
			refreshSqlText();
		} catch (Exception e1) {
		}
	}

	//文本框内容发生变化
	public void removeUpdate(DocumentEvent e) {
		try {
			refreshSqlText();
		} catch (Exception e1) {
		}
	}

	/*
	 * 根据SQL导出Xml文件。
	 * 调用原理和原数据导出一致。
	 */
	private void doExport() {
		String allExportSql_text = text_condition.getText();
		if (allExportSql_text == null || allExportSql_text.trim().equals("")) {
			MessageBox.show(text_condition, "没有发现可用的导出SQL");
			return;
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("请选择存放目录");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int flag = chooser.showSaveDialog(this);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		String str_path = chooser.getSelectedFile().getAbsolutePath(); //
		if (str_path == null) {
			return;
		}
		File f = new File(str_path);
		if (!f.exists()) {
			MessageBox.show(this, "路径:" + str_path + " 不存在!");
			return;
		}
		String allsql[] = tbutil.split(allExportSql_text, ";");
		try {
			for (int j = 0; j < allsql.length; j++) {
				if (allsql[j] != null && !allsql[j].trim().equals("")) {
					String otherSql = allsql[j].substring(allsql[j].indexOf(" from ")).trim();
					String con_sql = "select count(*) " + otherSql;
					String tableName = "";
					if (otherSql.substring(4).trim().contains(" ")) {
						tableName = otherSql.substring(4).trim();
						tableName = tableName.substring(0, tableName.indexOf(" ")).trim();
					} else {
						tableName = otherSql.substring(4);
						tableName = tableName.replaceAll(";", "").trim();
					}
					int li_beginNo = 0; // 起始号!
					int li_cout = 0; //
					int li_countall = Integer.parseInt(UIUtil.getStringValueByDS(null, con_sql)); //
					if (li_countall == 0) {
						continue;
					}
					String leftjoin = "";
					if (otherSql.indexOf(" left ") > 0) {
						if (otherSql.indexOf(" where ") > 0) { //如果有where
							leftjoin = otherSql.substring(otherSql.indexOf(" left "), otherSql.indexOf(" where "));
						} else {
							leftjoin = otherSql.substring(otherSql.indexOf(" left ")).replaceAll(";", "");
						}
					}
					String condition = "";
					if (otherSql.indexOf(" where ") > 0) {
						condition = otherSql.substring(otherSql.indexOf(" where ")).replaceAll(";", "");
					}
					int li_downedCount = 0; //
					if (li_countall <= 500) {
						HashMap exportMap = new HashMap();
						exportMap.put("dotype", "0");
						exportMap.put("dsname", null);
						exportMap.put("table", tableName);
						exportMap.put("fullsql", allsql[j]);
						exportMap.put("beginNo", "0");
						HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallDMO", "getXMlFromTable500RecordsByCondition", exportMap);
						String str_xml = (String) returnMap.get("内容"); //
						String str_fileName = tableName + "_" + (100000 + li_cout) + ".xml"; //
						FileOutputStream fileOut = new FileOutputStream(str_path + File.separator + str_fileName, false); //
						tbutil.writeStrToOutputStream(fileOut, str_xml); // 输出至文件
					} else {
						while (1 == 1) { // 死循环
							long ll_1 = System.currentTimeMillis(); //
							if (li_beginNo >= li_countall) {
								break;
							}
							HashMap exportMap = new HashMap();
							exportMap.put("dsname", null);
							exportMap.put("table", tableName);
							exportMap.put("fullsql", allsql[j]);
							exportMap.put("beginNo", li_beginNo + "");
							exportMap.put("joinsql", leftjoin);
							exportMap.put("condition", condition);
							HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallDMO", "getXMlFromTable500RecordsByCondition", exportMap);
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
							String str_fileName = tableName + "_" + (100000 + li_cout) + ".xml"; //
							FileOutputStream fileOut = new FileOutputStream(str_path + File.separator + str_fileName, false); //
							tbutil.writeStrToOutputStream(fileOut, str_xml); // 输出至文件
						}
					}

				}
			}
			if(MessageBox.confirm(this, "导出成功!是否立即打开"+str_path+"路径")){
				try {
					Desktop.open(f); //
				} catch (Exception ex) {
					ex.printStackTrace(); //
					try {
						Runtime.getRuntime().exec("explorer.exe \"" + str_path + "\"");
					} catch (Exception exx) {
						exx.printStackTrace(); //
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//刷新sql文本框内容
	//根据前两个文本框的配置信息，生成导出数据的sql。
	private void refreshSqlText() throws Exception {
		String txt = "";
		txt = text_config.getText();
		String[] mainDatas = tbutil.getMacroList(txt); //得到一组数据配置
		StringBuffer allSql = new StringBuffer();
		String condition = text_main.getText(); //得到配置的ID
		for (int i = 0; i < mainDatas.length; i++) {
			String oneConfig = mainDatas[i];//得到一条主子表配置。
			if (oneConfig.trim().equals("")) {
				continue;
			}
			oneConfig = tbutil.replaceAll(oneConfig, "${", "");
			oneConfig = tbutil.replaceAll(oneConfig, "}", "");
			String allTable[] = oneConfig.split(";"); //取到所有表
			HashMap map = new HashMap();
			for (int j = 0; j < allTable.length; j++) {

				if (allTable[j].indexOf(".") < allTable[j].lastIndexOf(".")) {
					String tableName = allTable[j].substring(0, allTable[j].indexOf(".") - 1);
					String foreignConfig = allTable[j].substring(allTable[j].indexOf(".") - 1);
					map.put(tableName, foreignConfig);
				} else if (allTable[j].indexOf(".") == allTable[j].lastIndexOf(".")) { //
					String[] table_foreign = tbutil.split(allTable[j], ".");
					if (table_foreign.length == 1) {
						map.put(table_foreign[0], "");
					} else {
						map.put(table_foreign[0], table_foreign[1]);
					}
				} else {
					map.put(allTable[j], "");

				}
			}
			//第一张表是主表
			String mainTable = allTable[0];
			String mainTableName = mainTable;
			String mainTablepk = "id";
			if (mainTable.contains("(") && mainTable.contains(")")) {
				mainTableName = mainTable.substring(0, mainTable.indexOf("("));
				mainTablepk = mainTable.substring(mainTable.indexOf("(") + 1, mainTable.indexOf(")"));
			}
			StringBuffer mainSql = new StringBuffer("select * from " + mainTableName);
			String mainCondition = "";
			if (condition != null && !condition.equals("") && mainTablepk != null && !"无主键".equals(mainTablepk)) {
				mainCondition = mainTablepk + " in(" + condition + ")";
				mainSql.append(" where " + mainCondition);
			}
			allSql.append(mainSql.toString() + ";\r\n");
			for (int j = 1; j < allTable.length; j++) {
				StringBuffer itemSql = new StringBuffer();
				String item_table = allTable[j].substring(0, allTable[j].indexOf("."));
				String item_foreign_config = allTable[j].substring(allTable[j].indexOf(".") + 1);
				String itemTableName = item_table;
				String itemTableForiegn = item_foreign_config; //得到配置
				String itemTableForiegnName = itemTableForiegn;//外键表名
				if (itemTableForiegn.indexOf(".") > 0 && itemTableForiegn.indexOf("=") > 0) {
					itemTableForiegnName = itemTableForiegn.substring(itemTableForiegn.indexOf("=") + 1, itemTableForiegn.indexOf("."));
				} else {
					itemTableForiegnName = mainTableName; //默认为主表
				}
				if (itemTableForiegnName.equalsIgnoreCase(mainTableName)) {
					itemSql.append("select " + itemTableName + ".* from " + itemTableName + " left join " + mainTableName + " on " + itemTableName + "." + itemTableForiegn + "=" + mainTableName + "." + mainTablepk);
				} else {
					itemSql.append("select " + itemTableName + ".* from " + itemTableName + " left join " + itemTableForiegnName + " on " + itemTableName + "." + itemTableForiegn + " ");
					if (map.containsKey(itemTableForiegnName)) {
						String z_foreign = (String) map.get(itemTableForiegnName); //得到中间过度的外表
						if (z_foreign != null && !z_foreign.equals("")) {
							if (z_foreign.indexOf("=") < 0) { //表示它上面直接是主表。
								itemSql.append(" left join " + mainTableName + " on " + itemTableForiegnName + "." + z_foreign + "=" + mainTableName + "." + mainTablepk);
							}
						}
					}
				}
				if (!mainCondition.equals("")) {
					itemSql.append(" where " + mainTableName + "." + mainCondition);
				}
				allSql.append(itemSql + ";\r\n");
			}
		}
		text_condition.setText(allSql.toString());
	}
}
