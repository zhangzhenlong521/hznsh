/**************************************************************************
 * $RCSfile: RefDialog_QueryTree.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.ListAndTableFactoty;

/**
 * 获得树型参照
 * @author Administrator
 *
 */
public class RefDialog_QueryTree extends AbstractRefDialog {
	private static final long serialVersionUID = 1L;

	private static int id_index = 0;

	private static int name_index = 2;

	private TableDataStruct struct = null;

	private JTree jt_menu = null;

	private JTree jt_sub = null;

	private String str_ref_pk;

	private String str_ref_code;

	private String str_ref_name;

	private JTable table = null;

	private String str_RefName;

	private String str_sql;

	private String tree_ID = null;

	private String tree_parentID = "parentmenuid";

	private JButton[] jbt_operator = null;

	private JTextField jtf_search = null;

	private String[][] str_data = null;

	private DefaultTableModel tableModel = null;

	private String[] table_header = null;

	private boolean tableFlag = false;

	private boolean treeFlag = false;

	private HashVO[] subtree_vo = null;

	private HashVO selectedHashVO = null; // 直接的数据

	private int li_closeType = -1;

	private JPanel jpn_btn = null;

	private ActionListener listener = null;

	private KeyAdapter adapter = null;

	private MouseAdapter m_adapter = null;

	private String str_datasourcename = null; //数据源名称

	public RefDialog_QueryTree(Container _parent, String _name, RefItemVO _refinitvalue, BillPanel _billPanel, CommUCDefineVO _dfvo) throws Exception {
		super(_parent, _name, _refinitvalue, _billPanel); //

		if (_refinitvalue != null) {
			str_RefName = _refinitvalue.getId();
		}

		this.str_datasourcename = _dfvo.getConfValue("数据源"); ///
		this.str_sql = _dfvo.getConfValue("SQL语句"); //
		this.tree_ID = _dfvo.getConfValue("PKField"); //
		this.tree_parentID = _dfvo.getConfValue("ParentPKField"); //
	}

	public void initialize() {
		try {
			getData();
			listener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dealAcitonPerform(e);
				}
			};
			adapter = new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					dealKeyPerform(e);
				}
			};
			m_adapter = new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					dealMousePerform(e);
				}
			};
			getMenuTree();
			getSubMenuTree(null);

			JPanel jpn_left = new JPanel();
			jpn_left.setLayout(new BorderLayout());

			JScrollPane jsp_tree = new JScrollPane(jt_menu);
			JScrollPane jsp_subtree = new JScrollPane(jt_sub);
			jt_sub.setBackground(new Color(240, 240, 240));
			JSplitPane splitMenuPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jsp_tree, jsp_subtree);

			splitMenuPane.setDividerLocation(275);
			splitMenuPane.setDividerSize(5);
			splitMenuPane.setOneTouchExpandable(true);

			jbt_operator = new JButton[4];
			JPanel searchPanel = getSearchPanel();
			jpn_left.add(splitMenuPane, BorderLayout.CENTER);
			jpn_left.add(searchPanel, BorderLayout.NORTH);

			JPanel jpn_right = new JPanel();

			JScrollPane jsp_table = getJSPTable();
			int temp = getInitRow();
			if (temp >= 0) {
				Rectangle rect = table.getCellRect(temp, 0, true);
				table.scrollRectToVisible(rect);
				table.setRowSelectionInterval(getInitRow(), getInitRow());
			}
			jbt_operator[0] = getBtn("确定", new Dimension(85, 20));
			jbt_operator[1] = getBtn("取消", new Dimension(85, 20));
			jbt_operator[2] = getBtn("取消", new Dimension(85, 20)); //将原来关闭改名为取消,将原来的取消注销了!!

			jpn_btn = new JPanel();
			jpn_btn.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
			jpn_btn.add(jbt_operator[0]);
			jpn_btn.add(jbt_operator[2]);
			jpn_btn.addMouseListener(m_adapter);

			jpn_right.setLayout(new BorderLayout());
			jpn_right.add(jsp_table, BorderLayout.CENTER);
			jpn_right.add(new JLabel("  "), BorderLayout.EAST);
			jpn_right.add(new JLabel("  "), BorderLayout.WEST);

			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jpn_left, jpn_right);
			splitPane.setDividerLocation(300);
			splitPane.setDividerSize(10);
			splitPane.setOneTouchExpandable(true);

			this.getContentPane().add(splitPane, BorderLayout.CENTER);
			this.getContentPane().add(jpn_btn, BorderLayout.SOUTH);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getInitWidth() {
		return 900;
	}

	public int getInitHeight() {
		return 556;
	}

	/**
	 * 集中处理鼠标事件
	 * @param e
	 */
	protected void dealMousePerform(MouseEvent e) {
		Object obj = e.getSource();
		if (obj.equals(jpn_btn)) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				showSQL();
			}
		} else if (obj.equals(table)) {
			if (treeFlag) {
				return;
			}
			tableFlag = true;
			resetTree();
			getFocuse();
			tableFlag = false;
		}
	}

	/**
	 * 集中处理按钮事件
	 * @param e
	 */
	protected void dealAcitonPerform(ActionEvent e) {
		Object obj = e.getSource();
		if (obj.equals(jbt_operator[0])) {
			onConfirm();
		} else if (obj.equals(jbt_operator[1])) {
			onCancel();
		} else if (obj.equals(jbt_operator[2])) {
			onClose();
		} else if (obj.equals(jbt_operator[3])) {
			dealSearch(jtf_search.getText());
		}
	}

	/**
	 * 处理键盘事件
	 * @param e
	 */
	protected void dealKeyPerform(KeyEvent e) {
		Object obj = e.getSource();
		if (obj.equals(jtf_search)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				dealSearch(jtf_search.getText());
			}
		} else if (obj.equals(jt_menu)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) jt_menu.getLastSelectedPathComponent();

				if (node.isLeaf()) {
					return;
				}
				DefaultMutableTreeNode node_child = (DefaultMutableTreeNode) node.getChildAt(0);

				DefaultTreeModel model = (DefaultTreeModel) jt_menu.getModel();

				TreeNode[] nodes = model.getPathToRoot(node_child);
				TreePath path = new TreePath(nodes);

				jt_menu.makeVisible(path);
				jt_menu.scrollPathToVisible(path);
			}
		}
	}

	/**
	 * 获得左边树上边的搜索框
	 * @return
	 */
	private JPanel getSearchPanel() {
		JPanel panel = new JPanel();

		jtf_search = new JTextField();
		jtf_search.setPreferredSize(new Dimension(90, 20));
		jtf_search.addKeyListener(adapter);

		jbt_operator[3] = getBtn(UIUtil.getImage("find.gif"), new Dimension(18, 18));

		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.CENTER);
		panel.setLayout(layout);
		panel.add(jtf_search);
		panel.add(jbt_operator[3]);

		return panel;
	}

	/**
	 * 定制所有按钮
	 * 
	 * @param _obj:StringORIcon
	 * @param _demension:初始大小
	 * @return
	 */
	private JButton getBtn(Object _obj, Dimension _demension) {
		JButton jbt_temp = null;
		if (_obj instanceof String) {
			jbt_temp = new JButton(_obj.toString());
		} else if (_obj instanceof Icon) {
			jbt_temp = new JButton((Icon) _obj);
		}
		jbt_temp.setPreferredSize(_demension);
		jbt_temp.addActionListener(listener);
		jbt_temp.addKeyListener(adapter);
		return jbt_temp;
	}

	/**
	 * 根据_name来处理搜索
	 * @param _name
	 */
	private void dealSearch(String _name) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) jt_menu.getModel().getRoot();
		Enumeration e = rootNode.preorderEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node.isRoot()) {
				continue;
			}
			HashVO vo = (HashVO) node.getUserObject();
			String temp_name = vo.getStringValue(2); //
			int compareCount = temp_name.indexOf(_name.trim());
			if (compareCount == 0) {
				DefaultTreeModel model = (DefaultTreeModel) jt_menu.getModel();

				TreeNode[] nodes = model.getPathToRoot(node);
				TreePath path = new TreePath(nodes);

				jt_menu.makeVisible(path);
				jt_menu.setSelectionPath(path);
				jt_menu.scrollPathToVisible(path);
				return;
			}
		}
		JOptionPane.showMessageDialog(this, "非常抱歉,没有检测到您要找的节点!");
		return;
	}

	/**
	 * 根据母节点_node来构建子树
	 * @param _node
	 */
	private void getSubMenuTree(DefaultMutableTreeNode _node) {
		subtree_vo = getSubHashVO(_node);

		if (subtree_vo == null) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("根节点"); // 创建根结点
			DefaultTreeModel subtree_model = new DefaultTreeModel(root);
			jt_sub = new JTree(subtree_model);

			TreeNode[] nodes = subtree_model.getPathToRoot(root);
			TreePath path = new TreePath(nodes);
			jt_sub.scrollPathToVisible(path);
			jt_sub.setSelectionPath(path);
			jt_sub.makeVisible(path);

			SubMenuTreeRender aMyTreeRender = new SubMenuTreeRender(path);
			jt_sub.setCellRenderer(aMyTreeRender);

			return;
		}
		if (jt_sub == null) {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("根节点"); // 创建根结点
			DefaultTreeModel subtree_model = new DefaultTreeModel(root);
			jt_sub = new JTree(subtree_model);
		}

		DefaultTreeModel subtree_model = (DefaultTreeModel) jt_sub.getModel();
		if (((DefaultMutableTreeNode) subtree_model.getRoot()).getChildCount() > 0) {
			subtree_model.removeNodeFromParent((DefaultMutableTreeNode) subtree_model.getChild(subtree_model.getRoot(), 0));
		}
		DefaultMutableTreeNode node_root = (DefaultMutableTreeNode) subtree_model.getRoot(); // 创建根结点
		DefaultMutableTreeNode newChild = null;
		for (int i = 0; i < subtree_vo.length; i++) {
			newChild = new DefaultMutableTreeNode(subtree_vo[i]);
			subtree_model.insertNodeInto(newChild, node_root, 0);
			node_root = (DefaultMutableTreeNode) subtree_model.getChild(node_root, 0);

			TreeNode[] nodes = subtree_model.getPathToRoot(node_root);
			TreePath path = new TreePath(nodes);
			jt_sub.scrollPathToVisible(path);
			jt_sub.setSelectionPath(path);
			jt_sub.makeVisible(path);

			SubMenuTreeRender aMyTreeRender = new SubMenuTreeRender(path);
			jt_sub.setCellRenderer(aMyTreeRender);
		}
	}

	/**
	 * 根据母节点来获得子树的HashVO[]
	 * @param _node
	 * @return
	 */
	private HashVO[] getSubHashVO(DefaultMutableTreeNode _node) {
		if (_node == null) {
			return null;
		}
		Vector _vec = new Vector();
		while (true) {
			if (_node.isRoot()) {
				break;
			}
			HashVO vo = (HashVO) _node.getUserObject();
			_vec.add(vo);
			_node = (DefaultMutableTreeNode) _node.getParent();
		}
		if (_vec.size() > 0) {
			HashVO[] _vo = new HashVO[_vec.size()];
			for (int i = 0; i < _vec.size(); i++) {
				_vo[i] = (HashVO) _vec.get(_vec.size() - 1 - i);
			}
			return _vo;
		}
		return null;
	}

	/**
	 * 获得表数据的初始行，主要在初始化时调用
	 * @return
	 */
	private int getInitRow() {
		if (str_RefName == null || str_RefName.equals("")) {
			return -1;
		}
		int li_rowcount = table.getModel().getRowCount();
		String temp_str;
		for (int i = 0; i < li_rowcount; i++) {
			temp_str = (String) this.table.getModel().getValueAt(i, name_index);
			if (temp_str.equals(this.str_RefName)) {
				String selected_id = (String) table.getModel().getValueAt(i, id_index);
				tableFlag = true;
				getTreePath(selected_id);
				tableFlag = false;
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获得带有滚动条的JTable
	 * @return
	 */
	private JScrollPane getJSPTable() {
		ListAndTableFactoty latf_table = new ListAndTableFactoty();

		JScrollPane jsp_1 = latf_table.getJSPTable(str_data, table_header, null);
		latf_table.setAllColumnUnEditeable();
		table = latf_table.getTable();
		tableModel = latf_table.getTableModel();
		table.addMouseListener(m_adapter);
		return jsp_1;
	}

	/**
	 * 根据表的选择行，重新设置树
	 */
	protected void resetTree() {
		int selected_index = table.getSelectedRow();
		String selected_id = (String) table.getModel().getValueAt(selected_index, id_index);
		getTreePath(selected_id);
	}

	/**
	 * 获得选择的节点,主要指jt_menu
	 * @param _id
	 * @return
	 */
	private DefaultMutableTreeNode getSelectedNode(String _id) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) jt_menu.getModel().getRoot();
		Enumeration e = rootNode.breadthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node.isRoot()) {
				continue;
			}
			HashVO vo = (HashVO) node.getUserObject();
			String temp_id = vo.getStringValue(0);
			if (temp_id.equals(_id)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * 设置选择路径的颜色
	 * @param _node
	 */
	private void setSelectedPathColor(DefaultMutableTreeNode _node) {
		if (_node == null) {
			return;
		}

		DefaultTreeModel model = (DefaultTreeModel) jt_menu.getModel();

		TreeNode[] nodes = model.getPathToRoot(_node);
		TreePath path = new TreePath(nodes);

		MyTreeRender aMyTreeRender = new MyTreeRender(path);
		jt_menu.setCellRenderer(aMyTreeRender);
	}

	/**
	 * 根据_id来获得树的路径
	 * @param _id
	 * @return
	 */
	private TreePath getTreePath(String _id) {
		DefaultMutableTreeNode node = getSelectedNode(_id);
		setSelectedPathColor(node);
		this.getSubMenuTree(node);
		if (node == null) {
			return null;
		}
		DefaultTreeModel model = (DefaultTreeModel) jt_menu.getModel();

		TreeNode[] nodes = model.getPathToRoot(node);
		TreePath path = new TreePath(nodes);

		jt_menu.makeVisible(path);
		jt_menu.setSelectionPath(path);
		jt_menu.scrollPathToVisible(path);
		return path;
	}

	/**
	 * 初始化表数据和表头
	 * @return
	 * @throws Exception
	 */
	private Object[][] getData() throws Exception {
		if (str_data == null) {
			String _sql = str_sql;
			struct = UIUtil.getTableDataStructByDS(this.str_datasourcename, _sql); //
			table_header = struct.getHeaderName();
			str_data = struct.getBodyData();
		}
		return str_data;
	}

	/**
	 * 获得menu树
	 */
	private void getMenuTree() {
		if (jt_menu == null) {
			jt_menu = getJTree("根节点", tree_ID, tree_parentID, getHashVO());
			jt_menu.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent evt) {
					if (tableFlag) {
						return;
					}
					treeFlag = true;
					TreePath[] paths = evt.getPaths();

					for (int i = 0; i < paths.length; i++) {
						if (evt.isAddedPath(i)) {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
							onChangeSelectTree(node); // This node has been
						}
					}
					treeFlag = false;
				}
			});
			jt_menu.addKeyListener(adapter);
			jt_menu.setRootVisible(false); //
			jt_menu.setShowsRootHandles(true); //

		}
		return;
	}

	/**
	 * 获得menu树的HashVO[]
	 * @return
	 */
	private HashVO[] getHashVO() {
		Vector vResult = new Vector();
		HashVO voTmp = null;
		for (int i = 0; i < str_data.length; i++) {
			voTmp = new HashVO();
			for (int j = 0; j < table_header.length; j++) {
				voTmp.setAttributeValue(table_header[j], str_data[i][j]);
			}
			vResult.add(voTmp);
		}
		HashVO[] vos = new HashVO[vResult.size()];
		vResult.copyInto(vos);
		return vos;
	}

	/**
	 * 根据参数来构建树
	 * @param par_roottitle
	 * @param _keyname
	 * @param _parentkeyname
	 * @param _vo
	 * @return
	 */
	private JTree getJTree(String par_roottitle, String _keyname, String _parentkeyname, HashVO[] _vo) {
		HashVO[] hashVOs = _vo; //	
		DefaultMutableTreeNode node_root = new DefaultMutableTreeNode(par_roottitle); // 创建根结点
		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[hashVOs.length]; // 创建所有结点数组

		for (int i = 0; i < hashVOs.length; i++) {
			node_level_1[i] = new DefaultMutableTreeNode(hashVOs[i]); // 创建各个结点
			node_root.add(node_level_1[i]); // 加入根结点
		}
		// 构建树
		for (int i = 0; i < node_level_1.length; i++) {
			HashVO nodeVO = (HashVO) node_level_1[i].getUserObject();
			String str_pk_parentPK = nodeVO.getStringValue(_parentkeyname); // 父亲主键
			for (int j = 0; j < node_level_1.length; j++) {
				HashVO nodeVO_2 = (HashVO) node_level_1[j].getUserObject();
				String str_pk_2 = nodeVO_2.getStringValue(_keyname); // 主键
				if (str_pk_2.equals(str_pk_parentPK)) // 如果发现该结点主键正好是上层循环的父亲结点,则将其作为我的儿子处理加入
				{
					node_level_1[j].add(node_level_1[i]);
				}
			}
		}

		JTree aJTree = new JTree(new DefaultTreeModel(node_root));
		if (node_root.getChildCount() > 0) {
			DefaultMutableTreeNode firstNode = (DefaultMutableTreeNode) node_root.getChildAt(0); //
			TreePath path = new TreePath(((DefaultTreeModel) aJTree.getModel()).getPathToRoot(firstNode));
			aJTree.expandPath(path); //
		}

		return aJTree;
	}

	/**
	 * 处理树的选择节点的变更的事件
	 * @param _node
	 */
	private void onChangeSelectTree(DefaultMutableTreeNode _node) {
		if (_node.isRoot()) {
			refreshTable();
		} else {
			setSelectedPathColor(_node);
			getSubMenuTree(_node);
			HashVO vo_node = (HashVO) _node.getUserObject();

			String str_id = vo_node.getStringValue(0); //

			int li_rowcount = str_data.length;
			String temp_str = "";
			for (int i = 0; i < li_rowcount; i++) {
				temp_str = (String) str_data[i][0];
				if ((temp_str).equals(str_id)) {
					refreshTable();
					if (table.getRowCount() > 0) {
						table.setRowSelectionInterval(0, 0);
					}
				}
			}
		}
	}

	/**
	 * 刷新数据表
	 */
	private void refreshTable() {
		if (str_data.length <= 0) {
			return;
		}
		clearTable();
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) jt_menu.getLastSelectedPathComponent();
		Enumeration e = rootNode.preorderEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node.isRoot()) {
				continue;
			}
			HashVO vo = (HashVO) node.getUserObject();
			String temp_id = vo.getStringValue(0);

			int index = 0;
			for (int i = 0; i < str_data.length; i++) {
				if ((str_data[i][id_index]).equals(temp_id)) {
					Vector _vec = new Vector();
					//	_vec.add("" + index);
					index++;
					for (int j = 0; j < str_data[i].length; j++) {
						_vec.add(str_data[i][j]);
					}
					tableModel.addRow(_vec);
				}
			}

		}
		table.updateUI();
	}

	/**
	 * 清空数据表
	 */
	public void clearTable() {
		int li_rowcount = tableModel.getRowCount();
		for (int i = li_rowcount - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		table.updateUI();
	}

	protected void getFocuse() {
	}

	protected void onConfirm() {
		//setCloseType(1);
		li_closeType = 1;
		int li_selectedRow = jt_menu.getSelectionCount(); //
		if (li_selectedRow <= 0) {
			MessageBox.showSelectOne(this);
			this.requestFocus();
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jt_menu.getLastSelectedPathComponent();

		TreePath path = jt_menu.getSelectionPath();

		if (node.isRoot()) {
			MessageBox.show(this, "请勿操作根节点!");
			this.requestFocus();
			return;
		}

		Object[] objs = path.getPath(); //
		DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[objs.length];
		String str_name = "";
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = (DefaultMutableTreeNode) objs[i];
			if (nodes[i].isRoot()) {
				continue;
			}
			str_name = str_name + nodes[i];
			if (i != nodes.length - 1) {
				str_name = str_name + "->";
			}

		}

		HashVO _vo = (HashVO) node.getUserObject(); //
		str_ref_pk = _vo.getStringValue(0); //
		str_ref_code = _vo.getStringValue(1); //
		str_ref_name = str_name;//_vo.getStringValue(2); //

		selectedHashVO = _vo; //
		this.dispose();
	}

	private void onCancel() {
		str_ref_pk = null;
		str_ref_code = null;
		selectedHashVO = null;
		setCloseType(2);
		this.dispose();
	}

	protected void onClose() {
		li_closeType = 2;
		this.dispose();
	}

	public String getRefPK() {
		return this.str_ref_pk;
	}

	public String getRefCode() {
		return this.str_ref_code;
	}

	public String getRefName() {
		return str_ref_pk; //
	}

	public RefItemVO getReturnRefItemVO() {
		return new RefItemVO(str_ref_pk, str_ref_code, str_ref_name, selectedHashVO); //
	}

	class MyCellEditor extends DefaultCellEditor {
		private static final long serialVersionUID = 1259100940105603890L;

		public MyCellEditor() {
			this(new JTextField());
		}

		public MyCellEditor(JTextField _textfield) {
			super(_textfield);
		}

		public boolean isCellEditable(EventObject evt) {
			if (evt instanceof MouseEvent) {
				int li_count = ((MouseEvent) evt).getClickCount();
				if (li_count >= 3) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		}

	}

	class MyTreeRender extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 4104163070905939850L;

		TreePath path;

		public MyTreeRender(TreePath path) {
			this.path = path;
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			Component cell = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			DefaultMutableTreeNode[] nodes = (DefaultMutableTreeNode[]) Arrays.asList(path.getPath()).toArray(new DefaultMutableTreeNode[0]);
			boolean iffind = false;
			for (int i = 0; i < nodes.length; i++) {
				if (nodes[i] == node) {
					iffind = true;
					break;
				}
			}

			if (iffind) {
				cell.setForeground(Color.red);
			} else {
				cell.setForeground(Color.black);
			}
			return cell;
		}
	}

	public int getLi_closeType() {
		return li_closeType;
	}

	public int getCloseType() {
		return li_closeType;
	}

	public HashVO getSelectedHashVO() {
		return selectedHashVO;
	}

	private void showSQL() {
		System.out.println(this.str_sql);
		JOptionPane.showMessageDialog(this, this.str_sql); //
	}

	class SubMenuTreeRender extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 2347459122605778243L;

		TreePath path;

		public SubMenuTreeRender(TreePath path) {
			this.path = path;
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel cell = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			cell.setOpaque(true);
			cell.setBackground(new Color(250, 250, 250));
			cell.setBackground(new Color(240, 240, 240));
			cell.setEnabled(false);
			return cell;
		}
	}

	public String getRefID() {
		return null;
	}

}
/**************************************************************************
 * $RCSfile: RefDialog_QueryTree.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: RefDialog_QueryTree.java,v $
 * Revision 1.6  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:41:01  Administrator
 * *** empty log message ***
 *
 * Revision 1.5  2012/08/06 09:55:35  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.4  2011/11/04 13:36:22  xch123
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:46  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:58  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:54  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/17 15:21:14  wangjian
 * *** empty log message ***
 *
 * Revision 1.6  2008/06/13 15:19:23  wangjian
 * *** empty log message ***
 *
 * Revision 1.5  2008/05/30 03:09:02  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2008/04/21 20:35:28  wangjian
 * *** empty log message ***
 *
 * Revision 1.3  2008/04/11 01:44:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/04/02 03:16:42  wangjian
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:20  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:30  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:42  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/09/20 05:43:19  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:24  xch
 * *** empty log message ***
 *
 * Revision 1.7  2007/04/05 09:40:50  shxch
 * *** empty log message ***
 *
 * Revision 1.6  2007/03/28 11:36:28  shxch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/28 10:13:01  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/02/27 06:03:02  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/10 08:51:57  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:14:30  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
