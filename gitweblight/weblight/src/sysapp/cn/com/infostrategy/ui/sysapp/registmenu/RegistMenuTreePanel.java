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
 * ʵ�ʵ�������ʾ�Ĳ˵�!! ��������ͽṹ!�ұ��ǿ�Ƭ�ṹ!!
 * ���ͽṹ�е�������ͨ��RegisterMenu.xml�ж�ȡ��!!! ͨ�����ƵĽṹ�������͹�ϵ!!
 * ѡ��������ͽ��ʱˢ���ұߵĿ�Ƭ!!
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
		jtree.getSelectionModel().addTreeSelectionListener(this); //����ѡ������¼�!!
		JScrollPane scrollPanel = new JScrollPane(jtree); //
		billCard = new BillCardPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_registmenu.xml")); //
		WLTSplitPane split = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPanel, billCard); //
		split.setDividerLocation(250); //
		this.setLayout(new BorderLayout()); //
		this.add(split, BorderLayout.CENTER); //

		//����İ�ť
		JPanel panel_btn = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, LookAndFeel.defaultShadeColor1, false); //
		panel_btn.setLayout(new FlowLayout(FlowLayout.LEFT)); //
		btn_run = new WLTButton("����"); //
		btn_run.addActionListener(this); //
		panel_btn.add(btn_run); //

		btn_reverseadd = new WLTButton("�������"); //
		btn_reverseadd.addActionListener(this); //
		panel_btn.add(btn_reverseadd); //

		this.add(panel_btn, BorderLayout.NORTH); //
	}

	/**
	 * ȡ��������!!
	 * @return
	 */
	public JTree getMenuTree() {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("���ű�׼��Ʒ"); //�Ժ�ͳһ���������!!
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
				String str_icon = str_menuInfo[5]; //��·��������ͼ��
				String str_menucommandtype = str_menuInfo[6];//����
				HashVO hvo = new HashVO(); //
				hvo.setAttributeValue("xmlfile", str_xmlfile); //
				hvo.setAttributeValue("menuname", str_menuName); //
				hvo.setAttributeValue("command", str_command); //
				hvo.setAttributeValue("descr", str_descr); //
				hvo.setAttributeValue("conf", str_conf); //
				hvo.setAttributeValue("commandtype", str_menucommandtype);
				if (str_icon != null) {
					if (str_icon.contains("[") && str_icon.contains("]")) {//�ж��Ƿ���� []
						hvo.setAttributeValue("icon", str_icon.substring(str_icon.lastIndexOf("[") + 1, str_icon.lastIndexOf("]"))); //�����һ��������ĩ�ڵ�ͼ��
					}
				}
				String str_viewName = str_menuName; //
				if (str_viewName.indexOf(".") > 0) {
					str_viewName = str_viewName.substring(str_viewName.lastIndexOf(".") + 1, str_viewName.length()); //
				}
				hvo.setAttributeValue("menunviewame", str_viewName); //
				hvo.setToStringFieldName("menunviewame"); //

				DefaultMutableTreeNode itmNode = new DefaultMutableTreeNode(hvo); //
				map_allDirNode.put(str_menuName, itmNode); //������

				String[] str_nameItems = tbUtil.split(str_menuName, "."); //
				String[] icons = null; //���е�ͼ������
				if (str_icon != null) {
					icons = str_icon.split("]");
				}
				for (int j = 0; j < str_nameItems.length; j++) {
					String str_thisOneLevelParentPath = ""; //
					for (int k = 0; k <= j; k++) {
						str_thisOneLevelParentPath = str_thisOneLevelParentPath + str_nameItems[k] + ".";
					}
					str_thisOneLevelParentPath = str_thisOneLevelParentPath.substring(0, str_thisOneLevelParentPath.length() - 1); //����ĳһ���ϼ���ȫ·��
					if (!map_allDirNode.containsKey(str_thisOneLevelParentPath)) { //���û��,�����!!!
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
								parentNodeVO.setAttributeValue("icon", ic); //����ͼ��
							}
						}
						parentNodeVO.setToStringFieldName("menunviewame"); //
						DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(parentNodeVO); //
						map_allDirNode.put(str_thisOneLevelParentPath, tmpNode); ////����
					}
				}
			}
			DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) map_allDirNode.values().toArray(new DefaultMutableTreeNode[0]); //
			for (int i = 0; i < allNodes.length; i++) {
				HashVO hvo_item = (HashVO) allNodes[i].getUserObject(); //
				String str_text = hvo_item.getStringValue("menuname"); //
				if (tbUtil.findCount(str_text, ".") <= 0) { //����ǵ�һ��,��ֱ�Ӽ�������!!
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
		tree.putClientProperty("MyRootIconImg", "office_042.gif"); //�밴ťͼ��һ��!!
		tree.setBackground(LookAndFeel.defaultShadeColor1); //
		tree.setUI(new WLTTreeUI());
		tree.setOpaque(false); //һ��Ҫ���͸��,������ٹ���ʱ����ְ���,������Ϊ����ĥ�úܾ�!!!!!!
		tree.setCellRenderer(new BillTreeCheckCellRender(false)); //���ù�ѡ��

		popMenu = new JPopupMenu(); //�Ҽ������Ĳ˵�!!
		menuItem = new JMenuItem("�������"); //
		menuItem.addActionListener(this); //
		popMenu.add(menuItem); //

		tree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) { //������Ҽ�!!
					showPopMenu((JTree) e.getSource(), e.getX(), e.getY()); //
				}
			}
		}); //
		this.jtree = tree; //
		return tree; //
	}

	private void showPopMenu(JTree _tree, int _x, int _y) {
		popMenu.show(_tree, _x, _y); //��ʾ�����˵�!!
		System.out.println("��ʾ�����˵�"); //
	}

	public void valueChanged(TreeSelectionEvent e) {
		billCard.reset(); //
		TreePath selPath = jtree.getSelectionPath(); //
		if (selPath == null) {
			return;
		}
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent(); //
		if (selNode.isLeaf()) { //ֻ����Ҷ�ӽ���ˢ������
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
			onReverseAdd(); //�������ϵͳ�˵�!!
		} else if (e.getSource() == menuItem) {
			onReverseAdd(); //�������ϵͳ�˵�!!
		}
	}

	private void onRun() {
		TreePath selPath = jtree.getSelectionPath(); //
		if (selPath == null) {
			MessageBox.show(this, "��ѡ��һ��ĩ(Ҷ��)���"); //
			return; //
		}
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent(); //
		if (!selNode.isLeaf()) { //�������Ҷ�ӽ��
			MessageBox.show(this, "��ѡ��һ��ĩ(Ҷ��)���"); //
			return; //
		}
		HashVO hvo = (HashVO) selNode.getUserObject(); //
		String str_menuname = hvo.getStringValue("menuname"); //
		String str_command = hvo.getStringValue("command"); //
		if (str_command == null || str_command.trim().equals("")) {
			MessageBox.show(this, "ѡ�еĹ��ܵ��·��Ϊ��!"); //
			return; //
		}
		try {
			AbstractWorkPanel workPanel = (AbstractWorkPanel) Class.forName(str_command).newInstance(); //
			workPanel.setLayout(new BorderLayout()); //
			workPanel.initialize(); //��ʼ������

			JFrame frame = new JFrame("����[" + str_menuname + "]"); //
			frame.setSize(1000, 700); //
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //
			frame.getContentPane().add(workPanel); //
			frame.setVisible(true); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * �������ϵͳ�˵�!!
	 * ��Ҫ�и��ж�,�����Ծ������Լ���������Ŀ¼������? ����ֻҪ����Ŀ¼������??
	 * ������߼��ĳ�,��ֱ��������ʽ,���������ƹ���,ͬʱ����Ʒ��·���������ڲ˵��ı�ע��!!!
	 */
	private void onReverseAdd() {
		try {
			DefaultMutableTreeNode[] selectedAllNodes = getSelectedNodeAllChildrens(); //
			if (selectedAllNodes == null) {
				return; //
			}

			ReverseAddCorpDialog dialog = new ReverseAddCorpDialog(this, "��ѡ��Ҫ�����λ��", 500, 500); //
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) { //�����ȷ������!!
				String str_addtoMenuId = dialog.getRetrunMenuId(); //��Ҫ�������ĸ����׽������!!
				HashMap mapMenuNameNewID = new HashMap(); //
				ArrayList al_sqls = new ArrayList(); //
				for (int i = 0; i < selectedAllNodes.length; i++) {
					DefaultMutableTreeNode itemNode = selectedAllNodes[i]; //
					HashVO itemVO = (HashVO) itemNode.getUserObject(); //
					String str_itemMenuName = itemVO.getStringValue("menuname"); //�˵�����
					String commandtype = itemVO.getStringValue("commandtype");
					boolean isParentIn = isMyParentAlreadyIn(itemNode, selectedAllNodes); //�Ƿ��׽��Ҳ������!
					String str_newid = UIUtil.getSequenceNextValByDS(null, "S_PUB_MENU"); //�µ�����!!!
					mapMenuNameNewID.put(str_itemMenuName, str_newid); //
					InsertSQLBuilder isql = new InsertSQLBuilder("pub_menu"); //
					isql.putFieldValue("id", str_newid); //
					isql.putFieldValue("code", itemVO.getStringValue("menunviewame")); //
					isql.putFieldValue("name", itemVO.getStringValue("menunviewame")); //
					isql.putFieldValue("ename", itemVO.getStringValue("menunviewame")); //��Ӣ������Ҳ���롾���/2016-05-10��
					if (isParentIn) { //����ҵĸ����Ѿ�����,��Ҫ�ҳ��Ҹ��׵��µ�id
						String str_myparentMenuName = str_itemMenuName.substring(0, str_itemMenuName.lastIndexOf(".")); //�Ҹ��׵Ľ������!!
						isql.putFieldValue("parentmenuid", (String) mapMenuNameNewID.get(str_myparentMenuName)); ////
					} else { //����ҵĸ��ײ���,���ұ�����ǵ�һ����!!
						if (str_addtoMenuId.equals("ROOT")) { //���������Ǹ����!!����
						} else {
							isql.putFieldValue("parentmenuid", str_addtoMenuId); //
						}
					}
					isql.putFieldValue("seq", str_newid); //
					isql.putFieldValue("isautostart", "N"); //
					isql.putFieldValue("isalwaysopen", "N"); //������Զ����
					if (itemNode.isLeaf()) { //�����������Ҷ�ӽ��,���������²���!!!
						isql.putFieldValue("usecmdtype", "1"); ////
						if ("0A".equals(commandtype) || "ST".equals(commandtype)) {
							isql.putFieldValue("commandtype", commandtype); //�Զ���WorkpPanel����
						} else {
							isql.putFieldValue("commandtype", "00"); //�Զ���WorkpPanel����
						}
						isql.putFieldValue("command", itemVO.getStringValue("command")); //�˵�·�����á�
						isql.putFieldValue("comments", str_itemMenuName + ";" + itemVO.getStringValue("xmlfile") + "\n" + itemVO.getStringValue("descr", ""));//˵���м���XMLע�Ṧ�ܵ����Ϣ������˵���˵����Ϣ[����2012-03-28]
						isql.putFieldValue("conf", itemVO.getStringValue("conf")); //����˵�����������Ϣ����ǰΪ�˰Ѳ˵������ٵ��룬�ظ�ȥ�����ͰѴ�����Ϣ�ŵ���˵���С�[2012-07-11]����				
					}
					isql.putFieldValue("icon", itemVO.getStringValue("icon")); //ͼ��
					al_sqls.add(isql.toString()); ////
					//System.out.println("���[" + itemVO.getStringValue("menuname") + "],�Ƿ���Ҳ������[" + isParentIn + "]"); //
				}
				UIUtil.executeBatchByDS(null, al_sqls); //
				MessageBox.show(this, "����ɹ�, �����µ�¼!"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private boolean isMyParentAlreadyIn(DefaultMutableTreeNode _thisNode, DefaultMutableTreeNode[] _allNodes) {
		String str_menuName = ((HashVO) _thisNode.getUserObject()).getStringValue("menuname"); //�˵�����
		if (str_menuName.indexOf(".") < 0) {
			return false; //����Լ����ǵ�һ��,��϶������и��׽����!!!
		}
		String str_myparentMenuName = str_menuName.substring(0, str_menuName.lastIndexOf(".")); //�Ҹ��׵Ľ������!!
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
			MessageBox.show(this, "��ѡ��һ�����!"); //
			return null;
		}
		Vector vector = new Vector();
		for (int i = 0; i < currNodes.length; i++) { //��������ѡ�еĽ��!!
			visitAllNodes(vector, currNodes[i]); //�ҳ��ý���µ����н��!!
		}

		DefaultMutableTreeNode[] returnNodes = (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //
		for (int i = 0; i < returnNodes.length; i++) {
			if (returnNodes[i].isRoot()) {
				MessageBox.show(this, "����ѡ�и����!"); //
				return null;
			}
			//HashVO hvo = (HashVO) returnNodes[i].getUserObject(); //
			//System.out.println("ѡ�н�������[" + hvo.getStringValue("menuname") + "]"); //
		}
		return returnNodes; //
	}

	/**
	 * ȡ������ѡ�еĽ��!
	 * @return
	 */
	private DefaultMutableTreeNode[] getSelectedNodes() {
		TreePath[] selPaths = jtree.getSelectionPaths(); //
		if (selPaths == null || selPaths.length == 0) {
			return null;
		}
		DefaultMutableTreeNode[] selNodes = new DefaultMutableTreeNode[selPaths.length]; //
		for (int i = 0; i < selPaths.length; i++) { //��������!
			selNodes[i] = (DefaultMutableTreeNode) selPaths[i].getLastPathComponent(); //
		}
		return selNodes; //���ظý��!!
	}

	private void visitAllNodes(Vector _vector, TreeNode node) {
		_vector.add(node); // ����ý��
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // �ҵ��ö���!!
				visitAllNodes(_vector, childNode); // �������Ҹö���
			}
		}
	}

}
