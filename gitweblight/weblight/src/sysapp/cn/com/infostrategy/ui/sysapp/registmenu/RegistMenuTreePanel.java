package cn.com.infostrategy.ui.sysapp.registmenu;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTreeUI;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.treecomp.BillTreeCheckCellRender;

/**
 * 实际的树型显示的菜单!! 左边是树型结构!右边是卡片结构!!
 * 树型结构中的数据是通过RegisterMenu.xml中读取的!!! 通过名称的结构生成树型关系!!
 * 选择左边树型结点时刷新右边的卡片!!
 * @author xch
 *
 */
public class RegistMenuTreePanel extends JPanel implements TreeSelectionListener, ActionListener {

	private JTree jtree = null; //
	private BillCardPanel billCard = null; //
	private WLTButton btn_run, btn_reverseadd = null; //
	private JPopupMenu popMenu = null; //
	private JMenuItem menuItem = null;

	public RegistMenuTreePanel() {
	}

	public void initialize() {
		jtree = getMenuTree(); //
		jtree.getSelectionModel().addTreeSelectionListener(this); //增加选择监听事件!!
		JScrollPane scrollPanel = new JScrollPane(jtree); //
		billCard = new BillCardPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_registmenu.xml")); //
		WLTSplitPane split = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPanel, billCard); //
		split.setDividerLocation(250); //
		this.setLayout(new BorderLayout()); //
		this.add(split, BorderLayout.CENTER); //

		//上面的按钮
		JPanel panel_btn = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, LookAndFeel.defaultShadeColor1, false); //
		panel_btn.setLayout(new FlowLayout(FlowLayout.LEFT)); //
		btn_run = new WLTButton("运行"); //
		btn_run.addActionListener(this); //
		panel_btn.add(btn_run); //

		btn_reverseadd = new WLTButton("反向加入"); //
		btn_reverseadd.addActionListener(this); //
		panel_btn.add(btn_reverseadd); //

		this.add(panel_btn, BorderLayout.NORTH); //
	}

	/**
	 * 取得树对象!!
	 * @return
	 */
	public JTree getMenuTree() {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("普信标准产品"); //以后统一叫这个名称!!
		try {
			cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc service = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class); //
			ArrayList al_menus = service.getAllRegistMenu(); //
			LinkedHashMap map_allDirNode = new LinkedHashMap(); //
			TBUtil tbUtil = new TBUtil(); //
			for (int i = 0; i < al_menus.size(); i++) {
				String[] str_menuInfo = (String[]) al_menus.get(i); //
				String str_xmlfile = str_menuInfo[0];
				String str_menuName = str_menuInfo[1];
				String str_command = str_menuInfo[2];
				String str_descr = str_menuInfo[3];
				String str_conf = str_menuInfo[4];
				String str_icon = str_menuInfo[5]; //该路径的所有图标
				String str_menucommandtype = str_menuInfo[6];//类型
				HashVO hvo = new HashVO(); //
				hvo.setAttributeValue("xmlfile", str_xmlfile); //
				hvo.setAttributeValue("menuname", str_menuName); //
				hvo.setAttributeValue("command", str_command); //
				hvo.setAttributeValue("descr", str_descr); //
				hvo.setAttributeValue("conf", str_conf); //
				hvo.setAttributeValue("commandtype", str_menucommandtype);
				if (str_icon != null) {
					if (str_icon.contains("[") && str_icon.contains("]")) {//判断是否包含 []
						hvo.setAttributeValue("icon", str_icon.substring(str_icon.lastIndexOf("[") + 1, str_icon.lastIndexOf("]"))); //截最后一个。就是末节点图标
					}
				}
				String str_viewName = str_menuName; //
				if (str_viewName.indexOf(".") > 0) {
					str_viewName = str_viewName.substring(str_viewName.lastIndexOf(".") + 1, str_viewName.length()); //
				}
				hvo.setAttributeValue("menunviewame", str_viewName); //
				hvo.setToStringFieldName("menunviewame"); //

				DefaultMutableTreeNode itmNode = new DefaultMutableTreeNode(hvo); //
				map_allDirNode.put(str_menuName, itmNode); //先置入

				String[] str_nameItems = tbUtil.split(str_menuName, "."); //
				String[] icons = null; //所有的图标数组
				if (str_icon != null) {
					icons = str_icon.split("]");
				}
				for (int j = 0; j < str_nameItems.length; j++) {
					String str_thisOneLevelParentPath = ""; //
					for (int k = 0; k <= j; k++) {
						str_thisOneLevelParentPath = str_thisOneLevelParentPath + str_nameItems[k] + ".";
					}
					str_thisOneLevelParentPath = str_thisOneLevelParentPath.substring(0, str_thisOneLevelParentPath.length() - 1); //本人某一层上级的全路径
					if (!map_allDirNode.containsKey(str_thisOneLevelParentPath)) { //如果没有,则加入!!!
						HashVO parentNodeVO = new HashVO(); //
						parentNodeVO.setAttributeValue("menuname", str_thisOneLevelParentPath); //
						String str_parentNodeViewName = str_thisOneLevelParentPath; //
						if (str_parentNodeViewName.indexOf(".") > 0) {
							str_parentNodeViewName = str_parentNodeViewName.substring(str_parentNodeViewName.lastIndexOf(".") + 1, str_parentNodeViewName.length()); //
						}
						parentNodeVO.setAttributeValue("menunviewame", str_parentNodeViewName); //
						if (icons != null && icons.length >= j) {
							String ic = tbUtil.replaceAll(icons[j], "[", "");
							if (ic != null && !ic.trim().equals("")) {
								parentNodeVO.setAttributeValue("icon", ic); //加入图标
							}
						}
						parentNodeVO.setToStringFieldName("menunviewame"); //
						DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(parentNodeVO); //
						map_allDirNode.put(str_thisOneLevelParentPath, tmpNode); ////置入
					}
				}
			}
			DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) map_allDirNode.values().toArray(new DefaultMutableTreeNode[0]); //
			for (int i = 0; i < allNodes.length; i++) {
				HashVO hvo_item = (HashVO) allNodes[i].getUserObject(); //
				String str_text = hvo_item.getStringValue("menuname"); //
				if (tbUtil.findCount(str_text, ".") <= 0) { //如果是第一层,则直接加入根结点!!
					rootNode.add(allNodes[i]); //
				} else {
					String str_parentText = str_text.substring(0, str_text.lastIndexOf(".")); //
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) map_allDirNode.get(str_parentText); //
					if (parentNode != null) {
						parentNode.add(allNodes[i]); //
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		JTree tree = new JTree(rootNode); //
		tree.putClientProperty("MyRootIconImg", "office_042.gif"); //与按钮图标一致!!
		tree.setBackground(LookAndFeel.defaultShadeColor1); //
		tree.setUI(new WLTTreeUI());
		tree.setOpaque(false); //一定要设成透明,否则快速滚动时会出现白条,我曾经为此折磨得很久!!!!!!
		tree.setCellRenderer(new BillTreeCheckCellRender(false)); //设置勾选框

		popMenu = new JPopupMenu(); //右键弹出的菜单!!
		menuItem = new JMenuItem("反向加入"); //
		menuItem.addActionListener(this); //
		popMenu.add(menuItem); //

		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) { //如果是右键!!
					showPopMenu((JTree) e.getSource(), e.getX(), e.getY()); //
				}
			}
		}); //
		this.jtree = tree; //
		return tree; //
	}

	private void showPopMenu(JTree _tree, int _x, int _y) {
		popMenu.show(_tree, _x, _y); //显示弹出菜单!!
		System.out.println("显示弹出菜单"); //
	}

	public void valueChanged(TreeSelectionEvent e) {
		billCard.reset(); //
		TreePath selPath = jtree.getSelectionPath(); //
		if (selPath == null) {
			return;
		}
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent(); //
		if (selNode.isLeaf()) { //只有是叶子结点才刷新数据
			HashVO hvo = (HashVO) selNode.getUserObject(); //
			billCard.setValueAt("xmlfile", new StringItemVO(hvo.getStringValue("xmlfile"))); //
			billCard.setValueAt("menuname", new StringItemVO(hvo.getStringValue("menuname"))); //
			billCard.setValueAt("command", new StringItemVO(hvo.getStringValue("command"))); //
			billCard.setValueAt("descr", new StringItemVO(hvo.getStringValue("descr"))); //
			billCard.setValueAt("conf", new StringItemVO(hvo.getStringValue("conf"))); //
		}
	}

	public JTree getJTree() {
		return jtree; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_run) {
			onRun(); //
		} else if (e.getSource() == btn_reverseadd) {
			onReverseAdd(); //反向加入系统菜单!!
		} else if (e.getSource() == menuItem) {
			onReverseAdd(); //反向加入系统菜单!!
		}
	}

	private void onRun() {
		TreePath selPath = jtree.getSelectionPath(); //
		if (selPath == null) {
			MessageBox.show(this, "请选择一个末(叶子)结点"); //
			return; //
		}
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent(); //
		if (!selNode.isLeaf()) { //如果不是叶子结点
			MessageBox.show(this, "请选择一个末(叶子)结点"); //
			return; //
		}
		HashVO hvo = (HashVO) selNode.getUserObject(); //
		String str_menuname = hvo.getStringValue("menuname"); //
		String str_command = hvo.getStringValue("command"); //
		if (str_command == null || str_command.trim().equals("")) {
			MessageBox.show(this, "选中的功能点的路径为空!"); //
			return; //
		}
		try {
			AbstractWorkPanel workPanel = (AbstractWorkPanel) Class.forName(str_command).newInstance(); //
			workPanel.setLayout(new BorderLayout()); //
			workPanel.initialize(); //初始化界面

			JFrame frame = new JFrame("运行[" + str_menuname + "]"); //
			frame.setSize(1000, 700); //
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //
			frame.getContentPane().add(workPanel); //
			frame.setVisible(true); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 反向加入系统菜单!!
	 * 需要有个判断,即可以决定将自己带所有子目录都加入? 还是只要将子目录都带入??
	 * 导入的逻辑改成,是直接类名方式,将类名复制过来,同时将产品的路径名复制在菜单的备注中!!!
	 */
	private void onReverseAdd() {
		try {
			DefaultMutableTreeNode[] selectedAllNodes = getSelectedNodeAllChildrens(); //
			if (selectedAllNodes == null) {
				return; //
			}

			ReverseAddCorpDialog dialog = new ReverseAddCorpDialog(this, "请选择要加入的位置", 500, 500); //
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) { //如果是确定返回!!
				String str_addtoMenuId = dialog.getRetrunMenuId(); //需要加入在哪个父亲结点下面!!
				HashMap mapMenuNameNewID = new HashMap(); //
				ArrayList al_sqls = new ArrayList(); //
				for (int i = 0; i < selectedAllNodes.length; i++) {
					DefaultMutableTreeNode itemNode = selectedAllNodes[i]; //
					HashVO itemVO = (HashVO) itemNode.getUserObject(); //
					String str_itemMenuName = itemVO.getStringValue("menuname"); //菜单名称
					String commandtype = itemVO.getStringValue("commandtype");
					boolean isParentIn = isMyParentAlreadyIn(itemNode, selectedAllNodes); //是否父亲结点也在里面!
					String str_newid = UIUtil.getSequenceNextValByDS(null, "S_PUB_MENU"); //新的主键!!!
					mapMenuNameNewID.put(str_itemMenuName, str_newid); //
					InsertSQLBuilder isql = new InsertSQLBuilder("pub_menu"); //
					isql.putFieldValue("id", str_newid); //
					isql.putFieldValue("code", itemVO.getStringValue("menunviewame")); //
					isql.putFieldValue("name", itemVO.getStringValue("menunviewame")); //
					isql.putFieldValue("ename", itemVO.getStringValue("menunviewame")); //将英文名称也加入【李春娟/2016-05-10】
					if (isParentIn) { //如果我的父亲已经在了,则要找出我父亲的新的id
						String str_myparentMenuName = str_itemMenuName.substring(0, str_itemMenuName.lastIndexOf(".")); //我父亲的结点名称!!
						isql.putFieldValue("parentmenuid", (String) mapMenuNameNewID.get(str_myparentMenuName)); ////
					} else { //如果我的父亲不在,即我本身就是第一层了!!
						if (str_addtoMenuId.equals("ROOT")) { //如果加入的是根结点!!则不设
						} else {
							isql.putFieldValue("parentmenuid", str_addtoMenuId); //
						}
					}
					isql.putFieldValue("seq", str_newid); //
					isql.putFieldValue("isautostart", "N"); //
					isql.putFieldValue("isalwaysopen", "N"); //不能永远开发
					if (itemNode.isLeaf()) { //如果这个结点是叶子结点,才设置以下参数!!!
						isql.putFieldValue("usecmdtype", "1"); ////
						if ("0A".equals(commandtype) || "ST".equals(commandtype)) {
							isql.putFieldValue("commandtype", commandtype); //自定义WorkpPanel类型
						} else {
							isql.putFieldValue("commandtype", "00"); //自定义WorkpPanel类型
						}
						isql.putFieldValue("command", itemVO.getStringValue("command")); //菜单路径配置。
						isql.putFieldValue("comments", str_itemMenuName + ";" + itemVO.getStringValue("xmlfile") + "\n" + itemVO.getStringValue("descr", ""));//说明中加入XML注册功能点的信息！加入菜单的说明信息[郝明2012-03-28]
						isql.putFieldValue("conf", itemVO.getStringValue("conf")); //插入菜单参数配置信息，以前为了把菜单导出再导入，重复去做，就把此项信息放到了说明中。[2012-07-11]郝明				
					}
					isql.putFieldValue("icon", itemVO.getStringValue("icon")); //图标
					al_sqls.add(isql.toString()); ////
					//System.out.println("结点[" + itemVO.getStringValue("menuname") + "],是否父亲也在里面[" + isParentIn + "]"); //
				}
				UIUtil.executeBatchByDS(null, al_sqls); //
				MessageBox.show(this, "加入成功, 请重新登录!"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private boolean isMyParentAlreadyIn(DefaultMutableTreeNode _thisNode, DefaultMutableTreeNode[] _allNodes) {
		String str_menuName = ((HashVO) _thisNode.getUserObject()).getStringValue("menuname"); //菜单名称
		if (str_menuName.indexOf(".") < 0) {
			return false; //如果自己就是第一层,则肯定不会有父亲结点了!!!
		}
		String str_myparentMenuName = str_menuName.substring(0, str_menuName.lastIndexOf(".")); //我父亲的结点名称!!
		for (int i = 0; i < _allNodes.length; i++) {
			HashVO itemVO = (HashVO) _allNodes[i].getUserObject(); ////
			String str_itemMenuName = itemVO.getStringValue("menuname"); //
			if (str_itemMenuName.equals(str_myparentMenuName)) {
				return true; //
			}
		}
		return false; //
	}

	private DefaultMutableTreeNode[] getSelectedNodeAllChildrens() {
		DefaultMutableTreeNode[] currNodes = getSelectedNodes(); // jtree.getse
		if (currNodes == null) {
			MessageBox.show(this, "请选择一个结点!"); //
			return null;
		}
		Vector vector = new Vector();
		for (int i = 0; i < currNodes.length; i++) { //遍历所有选中的结点!!
			visitAllNodes(vector, currNodes[i]); //找出该结点下的所有结点!!
		}

		DefaultMutableTreeNode[] returnNodes = (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //
		for (int i = 0; i < returnNodes.length; i++) {
			if (returnNodes[i].isRoot()) {
				MessageBox.show(this, "不能选中根结点!"); //
				return null;
			}
			//HashVO hvo = (HashVO) returnNodes[i].getUserObject(); //
			//System.out.println("选中结点的名称[" + hvo.getStringValue("menuname") + "]"); //
		}
		return returnNodes; //
	}

	/**
	 * 取得所有选中的结点!
	 * @return
	 */
	private DefaultMutableTreeNode[] getSelectedNodes() {
		TreePath[] selPaths = jtree.getSelectionPaths(); //
		if (selPaths == null || selPaths.length == 0) {
			return null;
		}
		DefaultMutableTreeNode[] selNodes = new DefaultMutableTreeNode[selPaths.length]; //
		for (int i = 0; i < selPaths.length; i++) { //遍历所有!
			selNodes[i] = (DefaultMutableTreeNode) selPaths[i].getLastPathComponent(); //
		}
		return selNodes; //返回该结点!!
	}

	private void visitAllNodes(Vector _vector, TreeNode node) {
		_vector.add(node); // 加入该结点
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				visitAllNodes(_vector, childNode); // 继续查找该儿子
			}
		}
	}

}
