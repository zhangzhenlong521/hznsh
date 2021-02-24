package cn.com.infostrategy.ui.sysapp.userrole;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.sysapp.login.TMO_Pub_Menu;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillTreeMoveEvent;
import cn.com.infostrategy.ui.mdata.BillTreeMoveListener;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

public class MenuManagerWPanel extends AbstractWorkPanel implements BillTreeSelectListener, BillTreeMoveListener {

	private static final long serialVersionUID = -1768260190442859080L;

	private JButton btn_refresh, btn_save, btn_export = null;
	private JMenuItem item_insert, item_delete, item_setshowinmenubar, item_exporthtml, item_exportxml, item_exportregxml;
	private BillTreePanel treePanel = null; //树型面板

	private BillCardPanel cardPanel = null;

	private boolean bo_ifneedrefresh = true;

	private String rootname = "PushGRC"; //默认根节点！

	public void initialize() {
		this.setLayout(new BorderLayout()); //

		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getTreePanel(), getCardPanel());
		splitPanel.setDividerLocation(235); //
		splitPanel.setDividerSize(5); //
		this.add(getNorthPanel(), BorderLayout.NORTH);
		this.add(splitPanel, BorderLayout.CENTER);
		this.setVisible(true); //
	}

	private JPanel getNorthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_refresh = new WLTButton(UIUtil.getLanguage("刷新"));
		btn_save = new WLTButton(UIUtil.getLanguage("保存"));
		btn_export = new WLTButton(UIUtil.getLanguage("导出"));

		btn_refresh.addActionListener(new MyActionListener()); //这种写法会少生成一些内部类,从而提高下载性能
		btn_save.addActionListener(new MyActionListener()); //这种写法会少生成一些内部类,从而提高下载性能
		btn_export.addActionListener(new MyActionListener()); //

		panel.add(btn_save);
		panel.add(btn_refresh);
		panel.add(btn_export);
		return panel; //
	}

	private BillTreePanel getTreePanel() {
		if (treePanel == null) {
			treePanel = new BillTreePanel(new TMO_Pub_Menu());
			treePanel.queryDataByCondition("1=1"); //
			treePanel.addBillTreeSelectListener(this);
			treePanel.addBillTreeMovedListener(this); //
			treePanel.setDragable(true); //

			item_insert = new JMenuItem(UIUtil.getLanguage("新增")); // 显示SQL
			item_delete = new JMenuItem(UIUtil.getLanguage("删除")); // 显示SQL
			item_setshowinmenubar = new JMenuItem(UIUtil.getLanguage("是否菜单条")); // 显示SQL
			item_exporthtml = new JMenuItem(UIUtil.getLanguage("导出Html")); // 显示SQL
			item_exportxml = new JMenuItem("导出安装数据"); //导出XML.
			item_exportregxml = new JMenuItem("导出注册Xml"); //导出RegisterMenu.xml

			item_insert.setPreferredSize(new Dimension(100, 19));
			item_delete.setPreferredSize(new Dimension(100, 19));
			item_exporthtml.setPreferredSize(new Dimension(100, 19));
			item_exportxml.setPreferredSize(new Dimension(100, 19));
			item_exportregxml.setPreferredSize(new Dimension(100, 19));
			MyActionListener myActionListener = new MyActionListener(); //
			item_insert.addActionListener(myActionListener); //
			item_delete.addActionListener(myActionListener); //
			item_setshowinmenubar.addActionListener(myActionListener); //
			item_exporthtml.addActionListener(myActionListener); //
			item_exportxml.addActionListener(myActionListener); ////
			item_exportregxml.addActionListener(myActionListener);
			treePanel.setAppMenuItems(new JMenuItem[] { item_insert, item_delete, item_setshowinmenubar, item_exporthtml, item_exportxml, item_exportregxml });
		}
		return treePanel;
	}

	private BillCardPanel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new BillCardPanel(new TMO_Pub_Menu());
			cardPanel.setEditable(true); //
			cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //
			//billcardPanel.queryDataByCondition("1=1"); //
		}
		return cardPanel;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (!bo_ifneedrefresh) {
			return;
		}
		if (!_event.getCurrSelectedNode().isRoot()) {
			BillVO billVO = _event.getCurrSelectedVO();
			String str_id = billVO.getPkValue();
			getCardPanel().queryDataByCondition("id='" + str_id + "'");
			getCardPanel().setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
			getCardPanel().setEditableByEditInit(); //
			this.updateUI(); //
		}
	}

	/**
	 * 结点移动时
	 */
	public void onBillTreeNodeMoved(BillTreeMoveEvent _event) {
		if (treePanel.getSelectedNode() != null && !treePanel.getSelectedNode().isRoot()) {
			BillVO billVO = treePanel.getSelectedVO();
			String str_id = billVO.getPkValue();
			cardPanel.queryDataByCondition("id='" + str_id + "'"); //
		} else {
			cardPanel.clear(); //
		}
	}

	private void onInsert() {
		try {
			if (getTreePanel().getSelectedPath() == null) {
				return; //如果没有选择一个结点则直接返回
			}

			BillVO billVO = getTreePanel().getSelectedVO();
			if (billVO != null) { //如果选中的不是根结点
				getCardPanel().insertRow(); //
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) getTreePanel().getSelectedNode();
				if (!parentNode.isRoot()) { //
					BillVO parentVO = getTreePanel().getBillVOFromNode(parentNode); //
					String parent_id = parentVO.getPkValue();
					String parent_code = parentVO.getStringValue("code");
					String parent_name = parentVO.getStringValue("name"); //
					RefItemVO refItemVO = new RefItemVO(parent_id, parent_code, parent_name); //参照VO
					getCardPanel().setCompentObjectValue("PARENTMENUID", new StringItemVO(parent_id)); //
					getCardPanel().setCompentObjectValue("PARENTMENUID_NAME", new StringItemVO(refItemVO.getName())); //
				}
			} else { //如果选中的是根结点
				getCardPanel().insertRow(); //
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
		//getCardPanel().setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT); //
	}

	private void onDelete() {
		BillVO billVO = getTreePanel().getSelectedVO();
		if (billVO != null) {
			if (JOptionPane.showConfirmDialog(this, UIUtil.getLanguage("您确定要删除该菜单吗?这将删除其所有子结点以及与之关联的所有权限数据!"), UIUtil.getLanguage("提示"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			BillVO[] childVOs = getTreePanel().getSelectedChildPathBillVOs();
			Vector v_sql_menu = new Vector(); //菜单删除
			Vector v_sql_user_menu = new Vector();
			Vector v_sql_role_menu = new Vector();
			for (int i = 0; i < childVOs.length; i++) {
				v_sql_menu.add("delete from pub_menu where id='" + childVOs[i].getPkValue() + "'");
				v_sql_user_menu.add("delete from pub_user_menu where menuid='" + childVOs[i].getPkValue() + "'");
				v_sql_role_menu.add("delete from pub_role_menu where menuid='" + childVOs[i].getPkValue() + "'");
			}

			Vector v_all = new Vector();
			v_all.addAll(v_sql_menu); //
			v_all.addAll(v_sql_user_menu); //
			v_all.addAll(v_sql_role_menu); //

			try {
				UIUtil.executeBatchByDS(null, v_all); //执行数据库操作!!
				getTreePanel().delCurrNode(); //
				getCardPanel().clear(); //
			} catch (Exception ex) {
				MessageBox.showException(null, ex); //
			}

		}
	}

	private void onSetShowOnMenuBar() {
		BillVO billVO = getTreePanel().getSelectedVO();
		if (billVO != null) {
			if (JOptionPane.showConfirmDialog(this, UIUtil.getLanguage("你确定要设置所有子结点为菜单条显示吗?"), UIUtil.getLanguage("提示"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			BillVO[] childVOs = getTreePanel().getSelectedChildPathBillVOs();
			Vector v_sql_menu = new Vector(); //菜单删除

			for (int i = 0; i < childVOs.length; i++) {
				v_sql_menu.add("update pub_menu set isinmenubar='Y' where id='" + childVOs[i].getStringValue("id") + "'"); //
			}

			Vector v_all = new Vector(); //
			v_all.addAll(v_sql_menu); //

			try {
				UIUtil.executeBatchByDS(null, v_all); //执行数据库操作!!
				getCardPanel().setValueAt("isinmenubar", new StringItemVO("Y"));
			} catch (Exception ex) {
				MessageBox.showException(null, ex); //
			}

		}
	}

	/**
	 * 导出Html,用于打印出来比较用..
	 */
	private void onExportHtml() {
		try {
			DefaultMutableTreeNode[] selCodes = getTreePanel().getSelectedNodes(); //
			if (selCodes == null) {
				MessageBox.show(this, "请选择一个结点!"); //
				return;
			}

			StringBuffer sb_html = new StringBuffer(); //
			sb_html.append(getHtmlHead());
			//HashMap mapCmdType = UIUtil.getHashMapBySQLByDS(null, "select code,Name From pub_comboboxdict Where type='菜单路径'"); //
			int li_leafcount = 0;
			sb_html.append("<table align=\"center\">\r\n"); //
			sb_html.append("<tr><td width=30 bgcolor=\"#CCCCCC\">序号</td><td width=235 bgcolor=\"#CCCCCC\">菜单路径</td><td width=30 bgcolor=\"#CCCCCC\">路径类型</td><td width=375 bgcolor=\"#CCCCCC\">路径定义</td></tr>\r\n"); //
			for (int k = 0; k < selCodes.length; k++) { //遍历所有选中的结点..
				DefaultMutableTreeNode[] childNodes = getTreePanel().getOneNodeChildPathNodes(selCodes[k]); //取得该结点下的所有子结点
				for (int i = 1; i < childNodes.length; i++) {
					if (childNodes[i].isLeaf()) {
						li_leafcount++;
						BillVO billVO = treePanel.getBillVOFromNode(childNodes[i]);
						String str_usecmd = billVO.getStringValue("usecmdtype"); //
						String str_cmdtype = ""; //
						String str_command = ""; //
						if (str_usecmd != null) {
							if (str_usecmd.trim().equals("1")) {
								str_cmdtype = billVO.getStringValue("commandtype"); //
								str_command = billVO.getStringValue("command"); //
							} else if (str_usecmd.trim().equals("2")) {
								str_cmdtype = billVO.getStringValue("commandtype2"); //
								str_command = billVO.getStringValue("command2"); //
							} else if (str_usecmd.trim().equals("3")) {
								str_cmdtype = billVO.getStringValue("commandtype3"); //
								str_command = billVO.getStringValue("command3"); //
							}
						}

						String str_path = getTreePanel().getOneNodeRootPathToString(childNodes[i], false); //得到整个路径
						sb_html.append("<tr><td>" + li_leafcount + "</td><td>" + str_path + "</td><td>" + str_cmdtype + "</td><td>" + str_command + "</td></tr>\r\n"); //
					}
				}
			}
			sb_html.append("</table>\r\n"); //
			sb_html.append(getHtmlTail());

			JTextArea textArea = new JTextArea(sb_html.toString()); //
			BillDialog dialog = new BillDialog(this);
			dialog.setSize(900, 500);
			dialog.getContentPane().add(new JScrollPane(textArea)); //
			dialog.setVisible(true); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onExportXML() {
		BillVO[] billVOs = getTreePanel().getSelectedChildPathBillVOs(); //
		if (billVOs == null || billVOs.length == 0) {
			MessageBox.show(this, "请选择一个结点"); //
			return;
		}

		try {
			StringBuffer sb_xml = new StringBuffer(); //
			String str_value = null; //
			HashMap mapcache = new HashMap(); //
			for (int i = 0; i < billVOs.length; i++) {
				sb_xml.append("<record tabname=\"pub_menu\">\r\n");
				HashVOStruct hvst = UIUtil.getHashVoStructByDS(null, "select * from pub_menu where id='" + billVOs[i].getStringValue("id") + "'"); //
				String str_pkvalue = hvst.getHashVOs()[0].getStringValue("id");
				mapcache.put(str_pkvalue, "" + (i + 1)); //
				String[] str_keys = hvst.getHeaderName();
				for (int j = 0; j < str_keys.length; j++) {
					str_value = hvst.getHashVOs()[0].getStringValue(str_keys[j], ""); //
					if (str_value.indexOf(">") >= 0 || str_value.indexOf("<") >= 0) {
						str_value = "<![CDATA[" + str_value + "]]>";
					}

					if (str_keys[j].equalsIgnoreCase("id")) {
						str_value = "" + (i + 1); //
						//sb_xml.append("  <id>" + (i + 1) + "</id>\r\n");
					} else if (str_keys[j].equalsIgnoreCase("parentmenuid") && !str_value.trim().equals("")) {
						//sb_xml.append("  <parentmenuid>" + mapcache.get(str_value) + "</parentmenuid>\r\n");
						str_value = (String) mapcache.get(str_value); //
					} else {
						//sb_xml.append("  <col name=\"" + str_keys[j].toLowerCase() + "\">" + str_keys[j].toLowerCase() + "</col>\r\n");
						//sb_xml.append("  <" + str_keys[j].toLowerCase() + ">" + str_value + "</" + str_keys[j].toLowerCase() + ">\r\n");
					}
					sb_xml.append("  <col name=\"" + str_keys[j].toLowerCase() + "\">" + (str_value == null ? "" : str_value) + "</col>\r\n");
				}
				sb_xml.append("</record>\r\n\r\n");
			}
			StringBuffer endSb = new StringBuffer();
			endSb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
			endSb.append("<root>\r\n");
			endSb.append(sb_xml);
			endSb.append("</root>");
			exportXmlDialog(endSb.toString(), "pub_menu_100001.xml");
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	private void onRefresh() {
		DefaultMutableTreeNode currNode = getTreePanel().getSelectedNode(); //取得选中的结点!!
		if (currNode == null) {
			getTreePanel().refreshTree(); //刷新数据
		} else {
			if (currNode.isRoot()) {
				getTreePanel().refreshTree(); //刷新数据
				//getTreePanel().expandOneNode(getTreePanel().getRootNode());
			} else {
				String str_id = getTreePanel().getSelectedVO().getPkValue(); //
				getTreePanel().refreshTree(); //刷新数据
				getTreePanel().expandOneNode(getTreePanel().findNodeByKey(str_id)); //
			}
		}
	}

	/**
	 * 保存数据!!
	 *
	 */
	private void onSave() {
		try {

			if (getCardPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_INSERT) {
				getCardPanel().updateData();

				String str_id = getCardPanel().getRealValueAt("ID");
				FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
				BillVO[] vos = service.getBillVOsByDS(null, "select * from pub_menu where id='" + str_id + "'", treePanel.getTempletVO().getParPub_Templet_1VO()); // 

				bo_ifneedrefresh = false;
				treePanel.addNode(vos[0]); //
				bo_ifneedrefresh = true;
				setInformation(UIUtil.getLanguage("保存菜单数据成功!!"));
			} else if (getCardPanel().getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) {
				getCardPanel().updateData();
				BillVO bilVO = getCardPanel().getBillVO(); //
				bilVO.setToStringFieldName(getMenuTreeViewFieldName());
				treePanel.setBillVO(treePanel.getSelectedNode(), bilVO); //
				treePanel.updateUI(); //
				setInformation(UIUtil.getLanguage("保存菜单数据成功!!"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

	/**
	 * 导出..
	 */
	private void onExport() {
		DefaultMutableTreeNode[] allNodes = treePanel.getSelectedNodes(); //
		if (allNodes == null) {
			MessageBox.show(this, "请至少选择一个节点");
			return;
		}
		int li_maxlevel = 0; //

		Vector v_nodes = new Vector(); //
		for (int i = 0; i < allNodes.length; i++) { //选择的所有结点..
			DefaultMutableTreeNode[] childNodes = treePanel.getOneNodeChildPathNodes(allNodes[i]); //
			for (int j = 0; j < childNodes.length; j++) {
				if (childNodes[j].getLevel() > li_maxlevel) { //
					li_maxlevel = childNodes[j].getLevel(); //
				}
				v_nodes.add(childNodes[j]); //
			}
		}

		String[][] str_data = new String[v_nodes.size()][li_maxlevel]; //
		int li_leafcount = 1; //
		int li_dircount_1 = 1; //
		int li_dircount_2 = 1; //
		for (int i = 0; i < v_nodes.size(); i++) { //
			DefaultMutableTreeNode tmp_node = (DefaultMutableTreeNode) v_nodes.get(i); //
			int li_level = tmp_node.getLevel(); //
			if (tmp_node.isLeaf()) { //如果是叶子结点..
				str_data[i][li_level - 1] = "<font color=\"blue\">(" + li_leafcount + ")" + tmp_node + "</font>"; //
				li_leafcount++;
			} else {
				if (li_level == 0) {
					str_data[i][li_level] = "" + tmp_node; //如果模板中配置显示根节点，此时选中根节点导出时，li_level=0，需要判断一下，否则会报数组越界异常，李春娟2012-02-22修改
				} else if (li_level == 1) {
					str_data[i][li_level - 1] = "(" + li_dircount_1 + ")" + tmp_node; //
					li_dircount_1++; //
				} else if (li_level == 2) {
					str_data[i][li_level - 1] = "(" + li_dircount_2 + ")" + tmp_node; //
					li_dircount_2++;
				} else {
					str_data[i][li_level - 1] = "" + tmp_node; //
				}
			}
		}
		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					String filename = file.getName();
					return filename.endsWith(".html");
				}
			}

			public String getDescription() {
				return "*.html";
			}
		});
		try {
			File file = new File(new File("C:/menuhtml.html").getCanonicalPath());
			chooser.setSelectedFile(file);
			int li_rewult = chooser.showSaveDialog(cardPanel);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {
				File curFile = chooser.getSelectedFile(); //
				if (curFile != null) {
					new TBUtil().writeStringArrayToHtmlTableFile(str_data, curFile.getPath()); // 以前是直接保存到c盘根目录，但win7 的c盘下不能写文件，导致导出失败，所以改为选择路径保存
					MessageBox.show(this, "导出" + curFile.getPath() + "成功!!!"); //
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String getMenuTreeViewFieldName() {
		if (ClientEnvironment.getInstance().getDefaultLanguageType().equalsIgnoreCase(WLTConstants.ENGLISH)) {
			return "ename";
		} else {
			return "name";
		}
	}

	private String getHtmlHead() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<TITLE>导出菜单(数据源:" + ClientEnvironment.getInstance().getDefaultDataSourceName() + ")</TITLE>\r\n"); //标题名
		sb_html.append("<style type=\"text/css\">\r\n");
		sb_html.append(".p_text {\r\n");
		sb_html.append(" font-size: 12px;\r\n");
		sb_html.append("}\r\n");
		sb_html.append("table   {  TABLE-LAYOUT: fixed;WORD-BREAK: break-all;border-collapse:collapse; font-size: 12px;};\r\n");
		sb_html.append("td   {  border:   solid   1px   #888888; font-size: 12px; };\r\n");
		sb_html.append("</style>\r\n");
		sb_html.append("</head>\r\n");
		sb_html.append("<body bgcolor=\"#FFFFFF\" topmargin=20 leftmargin=20 rightmargin=20 bottommargin=20 marginwidth=20 marginheight=20>\r\n");
		return sb_html.toString(); //
	}

	private String getHtmlTail() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("</body>\r\n"); //
		sb_html.append("</html>\r\n"); //
		return sb_html.toString(); //
	}

	private void onExportRegXml() {
		DefaultMutableTreeNode[] allNodes = treePanel.getSelectedNodes(); //
		if (allNodes == null) {
			MessageBox.show(this, "请至少选择一个节点");
		}
		LinkedHashMap map = new LinkedHashMap();
		StringBuffer sb = new StringBuffer();
		HashMap allRegXmlmap = new HashMap();
		try {
			cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc service = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class);
			ArrayList list = service.getAllRegistMenu();
			for (int i = 0; i < list.size(); i++) {
				String str[] = (String[]) list.get(i);
				if (str != null) {
					allRegXmlmap.put(str[1], str);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		JComboBox combobox = new JComboBox();
		combobox.addItem("合规风险管理");
		combobox.addItem("内部控制管理");
		combobox.addItem("全面风险管理");
		combobox.addItem("操作风险管理");
		combobox.setEditable(true); //设置可编辑！
		combobox.setBounds(5, 5, 120, 20); //
		JPanel panel = new JPanel(null); //
		panel.add(combobox); //
		JOptionPane.showMessageDialog(this, panel, "请选择根节点名称", JOptionPane.INFORMATION_MESSAGE); //这个控件可不可以取消。必须确定。可以用8个参数的。
		rootname = ((JTextField) combobox.getEditor().getEditorComponent()).getText();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
		sb.append("<root>\r\n");
		for (int i = 0; i < allNodes.length; i++) {
			DefaultMutableTreeNode node = allNodes[i];
			if (node.isLeaf()) { //如果是功能菜单
				if (!map.containsKey(node)) {
					String xml = getMenuXmlByNode(node, allRegXmlmap);
					if (xml != null && xml.trim().length() > 0) {
						sb.append(xml + "\r\n");
					}
					map.put(node, null);
				}
			} else {//父亲菜单
				DefaultMutableTreeNode allnode[] = treePanel.getOneNodeChildPathNodes(node);
				for (int j = 0; j < allnode.length; j++) {
					if (allnode[j].isLeaf()) {
						if (!map.containsKey(allnode[j])) {
							String xml = getMenuXmlByNode(allnode[j], allRegXmlmap);
							if (xml != null && xml.trim().length() > 0) {
								sb.append(xml + "\r\n");
							}
							map.put(node, null);
						}
					}
				}
			}
		}
		sb.append("</root>");
		exportXmlDialog(sb.toString(), "C:/RegisterMenu.xml");
	}

	/*
	 * 先显示xml内容，有导出按钮！
	 */
	public void exportXmlDialog(String _xml, final String _defaultfileName) {
		final JTextArea xmltext = new JTextArea(_xml);
		WLTButton btn_export = new WLTButton("导出");// 以下是导出逻辑
		WLTButton btn_exit = new WLTButton("关闭");// 
		btn_export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
					public boolean accept(File file) {
						if (file.isDirectory()) {
							return true;
						} else {
							String filename = file.getName();
							return filename.endsWith(".xml");
						}
					}

					public String getDescription() {
						return "*.xml";
					}
				});
				File file = null;
				try {
					file = new File(new File(_defaultfileName).getCanonicalPath());
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				chooser.setSelectedFile(file);
				int li_rewult = chooser.showSaveDialog(cardPanel);
				if (li_rewult == JFileChooser.APPROVE_OPTION) {
					File curFile = chooser.getSelectedFile(); //
					if (curFile != null) {
						try {
							new TBUtil().writeBytesToOutputStream(new FileOutputStream(curFile), xmltext.getText().getBytes("UTF-8"));
							MessageBox.show(xmltext, "导出成功！");
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						} //写文件!!
					}
				}
			}
		});
		final BillDialog dialog = new BillDialog(this, 900, 500);
		btn_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		});
		WLTPanel btnPanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(FlowLayout.CENTER), true);
		btnPanel.add(btn_export);
		btnPanel.add(btn_exit);
		WLTPanel mainpanel = new WLTPanel(WLTPanel.HORIZONTAL_RIGHT_TO_LEFT, new BorderLayout(), true);
		mainpanel.add(new JScrollPane(xmltext), BorderLayout.CENTER);
		mainpanel.add(btnPanel, BorderLayout.SOUTH);
		dialog.getContentPane().add(mainpanel); //
		dialog.setVisible(true); //
	}

	/*
	 * 替换xml中特殊字符！
	 */
	private String replaceSpecialChar(String str) {
		TBUtil tbutil = new TBUtil();
		if (str == null) {
			return "";
		}
		return tbutil.replaceAll(str, "\"", "&quot;");
	}

	/*
	 * 根据一个节点得到<menu>的内容。allXmlRegMenuMap是RegisterMenu.xml中的所有数据
	 * 这个方法把数据库是xml注册关联的位置改为实现类路径。
	 */
	private String getMenuXmlByNode(DefaultMutableTreeNode node, HashMap allXmlRegMenuMap) {
		StringBuffer name = new StringBuffer();
		BillVO billvo = treePanel.getBillVOFromNode(node);
		String usercmdtype = billvo.getStringValue("USECMDTYPE");//采用路径
		String cmdtype = null;
		String command = null;
		String conf = billvo.getStringValue("CONF");
		String comments = billvo.getStringValue("COMMENTS");
		if ("1".equals(usercmdtype)) {
			cmdtype = billvo.getStringValue("COMMANDTYPE");
			command = billvo.getStringValue("COMMAND");
		} else if ("2".equals(usercmdtype)) {
			cmdtype = billvo.getStringValue("COMMANDTYPE2");
			command = billvo.getStringValue("COMMAND2");
		} else if ("3".equals("usercmdtype")) {
			cmdtype = billvo.getStringValue("COMMANDTYPE3");
			command = billvo.getStringValue("COMMAND3");
		} else {
			return null;
		}

		if ("11".equals(cmdtype)) {
			String str_cmd = command.trim(); //
			String str_menuName = str_cmd.substring(0, str_cmd.indexOf(";")); //菜单名!!
			String[] str = (String[]) allXmlRegMenuMap.get(str_menuName);//
			if (str != null) {
				command = str[2];
			} else {
				command = null;
			}
		} else if ("OA".equals(cmdtype)) {

		} else if ("ST".equals(cmdtype)) {

		}
		StringBuffer icon = new StringBuffer();
		DefaultMutableTreeNode nodes[] = treePanel.getOneNodeParentNodes(node);
		for (int j = 0; j < nodes.length; j++) {
			if (nodes[j].isRoot()) { //根节点.如果填写空，那么根不设置名称。这种情况一般是在菜单管理下面又多出来一个系统汇总名字 。比如IPushGRC。 [郝明20131031]
				if (TBUtil.isEmpty(rootname)) {
					continue;
				}
				name.append(rootname);
				icon.append("[]");
			} else {
				BillVO vo = treePanel.getBillVOFromNode(nodes[j]);
				if (name.length() > 0) {
					name.append(".");
				}
				name.append(vo.getStringValue("NAME"));
				icon.append("[" + vo.getStringValue("ICON", "") + "]");
			}
		}
		StringBuffer xml = new StringBuffer();
		if (name.length() > 0) {
			xml.append(" <menu ");
			xml.append(" name=\"" + name + "\" ");
			if ("0A".equals(cmdtype) || "ST".equals(cmdtype)) {
				xml.append(" commandtype=\""+cmdtype+"\" "); //
			}
			xml.append(" command=\"" + replaceSpecialChar(command) + "\" ");
			if (conf != null) {
				xml.append(" conf=\"" + replaceSpecialChar(conf) + "\" ");
			}
			if (comments != null) {
				comments = getDescr(comments); //菜单的说明中导出RegisterMenu.xml时不需要导出XML注册功能点的配置信息。[郝明2012-03-28]
				xml.append(" descr=\"" + replaceSpecialChar(comments) + "\" ");
			}
			if (icon.length() > 0) {
				xml.append(" icon=\"" + replaceSpecialChar(icon.toString()) + "\" ");
			}
			xml.append(" /> ");
		}
		return xml.toString();
	}

	/*
	 * 调整一下导出是菜单的说明信息。包含菜单配置信息的部分截掉。
	 */
	private String getDescr(String descr) {
		StringBuffer desctsb = new StringBuffer();
		if (descr != null && descr.contains(".xml") && descr.contains("/")) {
			String[] s1 = descr.split("\n");
			for (int i = 0; i < s1.length; i++) {
				String s2 = s1[i];
				if (s2 != null && !s2.trim().equals("") && s2.contains("/") && s2.contains(".xml")) {
					desctsb.append(s2.substring(s2.lastIndexOf(".xml") + 4, s2.length()));
					continue;
				} else {
					desctsb.append(s2);
				}
			}
		}
		return desctsb.toString();
	}

	class MyActionListener extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_refresh) {
				onRefresh(); //
			} else if (e.getSource() == btn_save) {
				onSave(); //
			} else if (e.getSource() == btn_export) {
				onExport(); //
			} else if (e.getSource() == item_insert) {
				onInsert(); //
			} else if (e.getSource() == item_delete) {
				onDelete(); //
			} else if (e.getSource() == item_setshowinmenubar) {
				onSetShowOnMenuBar(); //
			} else if (e.getSource() == item_exporthtml) {
				//onExportHtml(); //不导出html的脚本，应该同卡片上的导出，导出一个html文件更清晰些！【李春娟/2012-05-07】
				onExport(); //
			} else if (e.getSource() == item_exportxml) {
				onExportXML(); //
			} else if (e.getSource() == item_exportregxml) {
				onExportRegXml(); //导出菜单为RegisterMenu.xml
			}
		}

	}
}
