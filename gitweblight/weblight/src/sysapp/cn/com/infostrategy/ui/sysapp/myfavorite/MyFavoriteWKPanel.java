package cn.com.infostrategy.ui.sysapp.myfavorite;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.ImageIconFactory;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 收藏夹 【杨科/2012-09-03】
 */

public class MyFavoriteWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener{
	private String userid;
	private WLTSplitPane splitPane = null; 
	private JPanel leftPanel = new JPanel(); 
	private JPanel rightContentPanel = new JPanel(); 
	private BillListPanel billList_favorite = null;
	private BillListPanel billListPanel = null;
	
	private JTree favoriteTree = null; 
	private DefaultMutableTreeNode rootNode; 
	
	private JPopupMenu popMenu = null; //弹出菜单!!
	private JMenuItem menuItem_1, menuItem_2, menuItem_3; 
	
	public MyFavoriteWKPanel() {
		initialize();
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); 
		
		userid = ClientEnvironment.getCurrSessionVO().getLoginUserId();
		
		leftPanel.setLayout(new BorderLayout()); 
		rightContentPanel.setLayout(new BorderLayout()); 
		
		leftPanel.add(new JScrollPane(getTree()), BorderLayout.CENTER); 
		
		billList_favorite = new BillListPanel("PUB_MYFAVORITES_CODE1");	
		billList_favorite.QueryDataByCondition(" 1=2 ");
		billList_favorite.addBillListHtmlHrefListener(this);
		rightContentPanel.add(billList_favorite, BorderLayout.CENTER); 
	
		splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, leftPanel, rightContentPanel);
		splitPane.setDividerLocation(200); 
		this.add(splitPane); 
	}
	
	public JTree getTree() {
		HashVO hvoRoot = new HashVO(); 
		hvoRoot.setAttributeValue("$NodeType", "0"); 
		hvoRoot.setAttributeValue("text", "我的收藏夹"); 
		hvoRoot.setAttributeValue("count", "0"); 
		hvoRoot.setAttributeValue("id", "0"); 
		hvoRoot.setAttributeValue("parentid", "00"); 
		rootNode = new DefaultMutableTreeNode(hvoRoot); 
		
		loadNode(); 
		
		favoriteTree = new JTree(rootNode); 
		favoriteTree.setBackground(LookAndFeel.treebgcolor);
		((BasicTreeUI) favoriteTree.getUI()).setCollapsedIcon(ImageIconFactory.getCollapsedIcon()); 
		((BasicTreeUI) favoriteTree.getUI()).setExpandedIcon(ImageIconFactory.getExpandedIcon()); 
		favoriteTree.setOpaque(false);
		favoriteTree.setRowHeight(17); 
		favoriteTree.setCellRenderer(new MyTreeCellRender()); 
		favoriteTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 

		expandAll(favoriteTree, new TreePath(rootNode), true); //展开全部!
		setSelectNode(favoriteTree, rootNode);

		favoriteTree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent _event) {
				super.mouseClicked(_event);
				if (_event.getButton() == MouseEvent.BUTTON3) { //如果是右键
					onShowPopMenu((JTree) _event.getSource(), _event.getX(), _event.getY()); 
				}
			}

		}); 

		favoriteTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				DefaultMutableTreeNode treeNode = getSelectedNode();
				 
				if(treeNode==null||treeNode.isRoot()){
					return;
				}
				
				HashVO hvo = (HashVO) treeNode.getUserObject(); 
				String id = hvo.getStringValue("id");
				
				billList_favorite.QueryDataByCondition(" dirid='"+id+"' ");
			}
		});
		
		return favoriteTree; 
	}
	
	private void onShowPopMenu(JTree _tree, int _x, int _y) {
		TreePath parentPath = _tree.getClosestPathForLocation(_x, _y);
		if (parentPath != null) { //如果不为空!!
			_tree.setSelectionPath(parentPath); 
		}

		if (popMenu == null) {
			popMenu = new JPopupMenu(); 
			menuItem_1 = new JMenuItem("添加文件夹"); 
			menuItem_2 = new JMenuItem("重命名"); 
			menuItem_3 = new JMenuItem("删除"); 
			menuItem_1.addActionListener(this); 
			menuItem_2.addActionListener(this); 
			menuItem_3.addActionListener(this); 
			popMenu.add(menuItem_1); 
			popMenu.add(menuItem_2); 
			popMenu.add(menuItem_3);
		}

		popMenu.show(this.favoriteTree, _x, _y); //显示!!
	}
	
	private void loadNode() {
		try {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select a.id,a.name,a.parentid,(select count(aa.id) from pub_myfavorites aa where aa.dirid=a.id) cou from pub_myfavoritesdir a where a.userid='"+userid+"'"); 

			if(hvs.length==0){
				ArrayList list_sqls = new ArrayList();			
				list_sqls.add(new InsertSQLBuilder("pub_myfavoritesdir", new String[][] { { "id", UIUtil.getSequenceNextValByDS(null, "pub_myfavoritesdir") }, { "name", "金融法律法规" }, { "parentid", "0" }, { "userid", userid } }).getSQL());
				list_sqls.add(new InsertSQLBuilder("pub_myfavoritesdir", new String[][] { { "id", UIUtil.getSequenceNextValByDS(null, "pub_myfavoritesdir") }, { "name", "内部制度" }, { "parentid", "0" }, { "userid", userid } }).getSQL());
				UIUtil.executeBatchByDS(null, list_sqls);	
				hvs = UIUtil.getHashVoArrayByDS(null, "select a.id,a.name,a.parentid,(select count(aa.id) from pub_myfavorites aa where aa.dirid=a.id) cou from pub_myfavoritesdir a where a.userid='"+userid+"'"); 
			}
			
			HashMap hsnode = new HashMap();
			hsnode.put("0", rootNode);
			for (int i = 0; i < hvs.length; i++) {
				HashVO hvo = new HashVO(); 
				hvo.setAttributeValue("$NodeType", "1"); 
				hvo.setAttributeValue("text", hvs[i].getStringValue("name")); 
				hvo.setAttributeValue("count", hvs[i].getStringValue("cou")); 
				hvo.setAttributeValue("id", hvs[i].getStringValue("id")); 
				hvo.setAttributeValue("parentid", hvs[i].getStringValue("parentid")); 
				DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(hvo); 
					hsnode.put(hvs[i].getStringValue("id"), tempNode);
			}
			
			for (int i = 0; i < hvs.length; i++) {
				if(hsnode.containsKey(hvs[i].getStringValue("parentid"))){
					DefaultMutableTreeNode idNode = (DefaultMutableTreeNode) hsnode.get(hvs[i].getStringValue("id")); 
					DefaultMutableTreeNode pidNode = (DefaultMutableTreeNode)hsnode.get(hvs[i].getStringValue("parentid"));
					pidNode.add(idNode);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); 
		}
	}
	
	public DefaultMutableTreeNode getSelectedNode() {
		TreePath path = favoriteTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return node;
	}	
	
	private void expandAll(JTree tree, TreePath _treePath, boolean _isExpand) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) _treePath.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = _treePath.pathByAddingChild(n); //取得子结点的路径
				expandAll(tree, path, _isExpand);
			}
		}
		if (_isExpand) {
			tree.expandPath(_treePath);
		} else {
			if (!node.isRoot()) {
				tree.collapsePath(_treePath);
			}
		}
	}
	
	private void setSelectNode(JTree _tree, TreeNode _node) {
		if (_node == null) {
			return; 
		}
		DefaultTreeModel model = (DefaultTreeModel) _tree.getModel();
		TreeNode[] nodes = model.getPathToRoot(_node);
		TreePath path = new TreePath(nodes); 
		_tree.setSelectionPath(path); 
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menuItem_1) {
			onAdd();
		} else if (e.getSource() == menuItem_2) { 
			onEdit();
		} else if (e.getSource() == menuItem_3) { 
			onDelete();
		}	
	}
	
    private void onAdd(){
    	try {
			DefaultMutableTreeNode treeNode = getSelectedNode();
			
			if(treeNode==null){
				return;
			}
			
			String str = JOptionPane.showInputDialog(this, "请输入文件夹名称");
			if(str == null || str.equals(""))
			    return;
			
			HashVO hvo = (HashVO) treeNode.getUserObject(); 
			String id = hvo.getStringValue("id");
			
			if(treeNode.isRoot()){
				id = "0";
			}
			
			String newid = UIUtil.getSequenceNextValByDS(null, "pub_myfavoritesdir");
			UIUtil.executeUpdateByDS(null, new InsertSQLBuilder("pub_myfavoritesdir", new String[][] { { "id", newid }, { "name", str }, { "parentid", id }, { "userid", userid } }).getSQL());
			
			HashVO hvonew = new HashVO(); 
			hvonew.setAttributeValue("$NodeType", "1"); 
			hvonew.setAttributeValue("text", str); 
			hvonew.setAttributeValue("count", "0"); 
			hvonew.setAttributeValue("id", newid); 
			hvonew.setAttributeValue("parentid", id); 
			DefaultMutableTreeNode tempNode = new DefaultMutableTreeNode(hvonew); 
			((DefaultTreeModel) favoriteTree.getModel()).insertNodeInto(tempNode, treeNode, treeNode.getChildCount()); 
			favoriteTree.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void onEdit(){
    	try {
			DefaultMutableTreeNode treeNode = getSelectedNode();
			 
			if(treeNode==null||treeNode.isRoot()){
				return;
			}
			
			HashVO hvo = (HashVO) treeNode.getUserObject();
			String id = hvo.getStringValue("id");
			
			String str = JOptionPane.showInputDialog(this, "请输入文件夹名称");
			if(str == null || str.equals(""))
			    return;
			
			ArrayList list_sqls = new ArrayList();
			list_sqls.add("update pub_myfavoritesdir set name = '"+str+"' where id="+id);
			list_sqls.add("update pub_myfavorites set dirname = '"+str+"' where dirid = '"+id+"'");
			UIUtil.executeBatchByDS(null, list_sqls);			
			
			hvo.setAttributeValue("text", str);
			treeNode = new DefaultMutableTreeNode(hvo);
			favoriteTree.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void onDelete(){
    	try {
			DefaultMutableTreeNode treeNode = getSelectedNode();
			 
			if(treeNode==null||treeNode.isRoot()){
				return;
			}
			
    		if(!MessageBox.confirm(this, "您确定要删除该节点及该节点下所收藏的内容吗?")){
    			return;
    		}
			
			HashVO hvo = (HashVO) treeNode.getUserObject();
			String id = hvo.getStringValue("id");
			
		    for (Enumeration e = treeNode.breadthFirstEnumeration(); e.hasMoreElements();) {
		    	DefaultMutableTreeNode childrenNode = (DefaultMutableTreeNode) e.nextElement();
		    	HashVO hvoc = (HashVO) childrenNode.getUserObject();
		    	if(!(id.equals(hvoc.getStringValue("id")))){
		    		id += ", " + hvoc.getStringValue("id");
		    	}
		    }
		    
			ArrayList list_sqls = new ArrayList();
			list_sqls.add("delete from pub_myfavoritesdir where id in ("+id+") and userid = '"+userid+"'");
			list_sqls.add("delete from pub_myfavorites where dirid in ("+id+")");
			UIUtil.executeBatchByDS(null, list_sqls);
			
			((DefaultTreeModel) favoriteTree.getModel()).removeNodeFromParent(treeNode);
			favoriteTree.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if(_event.getItemkey().equals("TOSTR")){	
	    	String id = billList_favorite.getSelectedBillVO().getStringValue("id");
	    	String templetcode = billList_favorite.getSelectedBillVO().getStringValue("templetcode");
	    	String pkey = billList_favorite.getSelectedBillVO().getStringValue("pkey");
	    	String pvalue = billList_favorite.getSelectedBillVO().getStringValue("pvalue");
	    	
	    	billListPanel = new BillListPanel(templetcode);	
	    	billListPanel.QueryDataByCondition(pkey + "=" +pvalue);
	    	billListPanel.setQuickQueryPanelVisiable(false);
	    	billListPanel.setToolbarVisiable(false);
	    	billListPanel.setPagePanelVisible(false);
	    	
	    	billListPanel.addBillListHtmlHrefListener(new BillListHtmlHrefListener(){

				public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
					onBillListHtml(billListPanel, billList_favorite.getSelectedBillVO().getStringValue("pvalue"), billList_favorite.getSelectedBillVO().getStringValue("templetcode"));
				}
	    		
	    	});
	    	
	    	BillCardPanel billCardPanel = new BillCardPanel(templetcode);	
	    	billCardPanel.queryDataByCondition(pkey + "=" +pvalue);
	    	
	    	WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billListPanel, billCardPanel);
	    	splitPane.setDividerSize(0);
			splitPane.setDividerLocation(90); 
			
			BillDialog dialog = new BillDialog(billList_favorite, "收藏内容", 900, 600);
			dialog.getContentPane().setLayout(new BorderLayout());
	        dialog.getContentPane().add(splitPane, "Center");
	        dialog.setVisible(true);
		}
	}
	
	public void onBillListHtml(BillListPanel billListPanel, String pvalue, String templetcode){
		
	}
	
	class MyTreeCellRender extends DefaultTreeCellRenderer {
		private Icon root = UIUtil.getImage("folder_star.png");
		private Icon icon_dir = UIUtil.getImage("office_151.gif");

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			HashVO hvo = (HashVO) ((DefaultMutableTreeNode) value).getUserObject(); 
			int li_type = hvo.getIntegerValue("$NodeType"); 
			String str_text = hvo.getStringValue("text"); 
			int li_count = hvo.getIntegerValue("count"); 
			if (li_count > 0) {
				str_text = str_text + "(" + li_count + ")";
			}
			JLabel label = new JLabel(str_text); 

			//设置图标!
			if (li_type == 0) {
				label.setIcon(root); 
			} else if (li_type == 1) { 
				label.setIcon(icon_dir);
			} 

			//设置字体颜色!
			if (sel) {
				label.setOpaque(true); //如果选中的话,则不透明..
				label.setForeground(Color.RED); 
				label.setBackground(Color.YELLOW); 
			} else {
				label.setOpaque(false); //透明!
			}
			return label;
		}
	}
	
}
