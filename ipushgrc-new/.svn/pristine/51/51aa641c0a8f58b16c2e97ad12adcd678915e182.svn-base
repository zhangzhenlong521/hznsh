package com.pushworld.ipushgrc.ui.favorite;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.sysapp.login.LoginUtil;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushgrc.ui.wfrisk.p010.WFAndRiskEditWKPanel;

/**
 * 我的收藏夹
 * 收藏夹2012-07-11升级。支持自定义文件夹，文件夹可以灵活拖拽，直接收藏页面的面板，所以具有业务数据类的所有逻辑，尤其是文件，风险。
 * 只要MY_FAVORITES_CODE1.xml模板升级到最新，数据库my_favorites字段添加好，兼容原来的收藏。
 * @author hm
 * 
 */
public class MyFavoriteQueryWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener, ChangeListener, BillTreeSelectListener {
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // 记录登陆人员ID
	private BillListPanel listPanel; // 收藏列表
	private BillDialog dialog; // 弹出的对话框，里面放一个BillListPanel
	private WLTButton btn_cancel = new WLTButton("关闭");// 弹出对话框的关闭按钮
	private WLTButton btn_update, btn_delete, btn_refresh;
	private WLTPanel rightPanel; //显示收藏数据的面板。
	private WLTRadioPane radioPane; //如果一个个人文件夹包含多种类型数据（制度、法规、事件），那么需要用radio搞成多个。做懒加载缓存。
	private JMenuItem add_folder, edit_folder, delete_folder; //添加，删除 
	private TBUtil tbutil = new TBUtil();
	private BillTreePanel treePanel;
	private LinkedHashMap classType = null;
	private List tab_list = new ArrayList(); //radio集合
	private String type, classpath;
	private WLTButton btn_moveup, btn_movedown;
	private boolean isTriggerSelectChangeEvent = true; //是否触发选择事件

	public void initialize() {
		createListPanel(); //
		rightPanel = new WLTPanel(new BorderLayout());
		rightPanel.add(listPanel, BorderLayout.CENTER);
		createTreePanel(); //
		treePanel.addBillTreeSelectListener(this);
		WLTSplitPane pane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, treePanel, rightPanel);
		pane.setDividerLocation(200);
		pane.getLeftComponent().setMinimumSize(new Dimension(200, 0));
		this.add(pane);
	}

	/*
	 * 创建我的收藏类型树,树需要把登陆人的所有数据搞出来，然后进行分类显示。
	 */
	private void createTreePanel() {
		try {
			treePanel = new BillTreePanel("MY_FAVORITES_CODE1");
			btn_refresh = new WLTButton("", UIUtil.getImage("office_191.gif"));
			btn_refresh.addActionListener(this);
			btn_moveup = new WLTButton(UIUtil.getImage("up1.gif")); //
			btn_movedown = new WLTButton(UIUtil.getImage("down1.gif")); //
			treePanel.getToolKitBtnPanel().add(btn_refresh);
			treePanel.getToolKitBtnPanel().add(btn_moveup);
			btn_moveup.addActionListener(this);
			treePanel.getToolKitBtnPanel().add(btn_movedown);
			btn_movedown.addActionListener(this);
			treePanel.setDragable(false);
			treePanel.getJTree().setCellRenderer(new MyTreeCellRender(treePanel));
			treePanel.queryDataByCondition("1=2");
			BillVO[] billvos = queryTreeData();
			if (billvos != null) {
				treePanel.addNodes(billvos, false);
			}
			treePanel.repaintBillTreeButton();
			treePanel.setSelected(treePanel.getRootNode());
			add_folder = new JMenuItem("添加文件夹", UIUtil.getImage("office_132.gif"));
			add_folder.addActionListener(this);
			edit_folder = new JMenuItem("重命名", UIUtil.getImage("office_198.gif"));
			edit_folder.addActionListener(this);
			delete_folder = new JMenuItem("删除", UIUtil.getImage("office_061.gif"));
			delete_folder.addActionListener(this);
			treePanel.setAppMenuItems(new JMenuItem[] { add_folder, edit_folder, delete_folder });
			treePanel.getJTree().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					if (treePanel.getSelectedNode() != null && treePanel.getSelectedNode().isRoot()) {
						add_folder.setEnabled(true);
						delete_folder.setEnabled(false);
						edit_folder.setEnabled(false);
					} else if ("Y".equals(treePanel.getSelectedVO().getStringValue("isfolder"))) {
						add_folder.setEnabled(true);
						delete_folder.setEnabled(true);
						edit_folder.setEnabled(true);
					} else {
						add_folder.setEnabled(false);
						delete_folder.setEnabled(true);
						edit_folder.setEnabled(false);
					}
				}

			});
			treePanel.updateUI();
			treePanel.getQuickLocatePanel().getComponent(0).setVisible(false);
			treePanel.getQuickLocatePanel().getComponent(2).setVisible(false);
			addDragSourceAction();
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	private BillVO[] queryTreeData() {
		try {
			BillListPanel listPanel = new BillListPanel(treePanel.getTempletVO());
			IPushGRCServiceIfc ifc = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			HashVO vos[] = ifc.getMyfavoriteTreeBillVO(userid);
			listPanel.queryDataByHashVOs(vos);
			return listPanel.getAllBillVOs();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	DragSource drag = null;
	DragSourceAdapter dragAdapter;

	/*
	 * 自定义拖拽事件。
	 */
	private void addDragSourceAction() {
		final JTree tree = treePanel.getJTree();
		drag = new DragSource();
		dragAdapter = new DragSourceAdapter() {
			public void dragDropEnd(DragSourceDropEvent dsde) {
				Point pt = dsde.getLocation();
				SwingUtilities.convertPointFromScreen(pt, tree);
				TreePath parentPath = tree.getClosestPathForLocation(pt.x, pt.y);
				if (parentPath != null) {
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent(); //

					TreePath selPath = tree.getSelectionPath(); //
					if (selPath == null) {
						return;
					}
					DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
					if (parentNode == selNode) {
						return;
					}
					if (treePanel.getSelectedNode().isRoot()) {
						return;
					}
					BillVO vo = treePanel.getSelectedVO();
					String isfolder = vo.getStringValue("isfolder");
					if (!"Y".equals(isfolder)) {
						MessageBox.show(treePanel, "只能移动自定义的文件夹!");
						return;
					}
					BillVO parentBillVO = treePanel.getBillVOFromNode(parentNode); //
					if (!parentNode.isRoot() && !"Y".equals(parentBillVO.getStringValue("isfolder"))) {
						MessageBox.show(treePanel, "目标只能是自定义文件夹!");
						return;
					}
					if (JOptionPane.showConfirmDialog(treePanel, "您是否真的想将结点 " + selPath + " 移至结点 " + parentPath + " 下?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						return;
					}

					if (parentNode.isRoot()) {
						BillVO selBillVO = treePanel.getBillVOFromNode(selNode); //
						String str_selPKValue = selBillVO.getStringValue(treePanel.getTempletVO().getPkname()); //
						try {
							String str_sql = "update " + treePanel.getTempletVO().getSavedtablename() + " set " + treePanel.getTempletVO().getTreeparentpk() + "=null where " + treePanel.getTempletVO().getPkname() + "='" + str_selPKValue + "'";
							UIUtil.executeUpdateByDS(null, str_sql); //
						} catch (Exception e) {
							e.printStackTrace(); //
						}
					} else {
						BillVO selBillVO = treePanel.getBillVOFromNode(selNode); //
						String str_parentTreePK = parentBillVO.getStringValue(treePanel.getTempletVO().getTreepk()); //
						String str_selPKValue = selBillVO.getStringValue(treePanel.getTempletVO().getPkname()); //
						if (str_parentTreePK.equals(str_selPKValue)) {
							return; //如果两者相同,肯定不能处理!!!
						}
						try {
							String str_sql = "update " + treePanel.getTempletVO().getSavedtablename() + " set " + treePanel.getTempletVO().getTreeparentpk() + "='" + str_parentTreePK + "' where " + treePanel.getTempletVO().getPkname() + "='" + str_selPKValue + "'";
							UIUtil.executeUpdateByDS(null, str_sql); //
						} catch (Exception e) {
							e.printStackTrace(); //
						}
					}

					selNode.removeFromParent(); //
					parentNode.insert(selNode, 0); //
					treePanel.resetChildSeq(); //
					treePanel.updateUI();
				}

			}
		};
		drag.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
			public void dragGestureRecognized(DragGestureEvent dge) {
				Transferable t = new StringSelection("aString");
				drag.startDrag(dge, DragSource.DefaultCopyDrop, t, dragAdapter);
			}
		});

	}

	public BillTreePanel getMyFavoriteTree() {
		if (treePanel == null) {
			createTreePanel();
		}
		return treePanel;
	}

	/*
	 * 收藏列表
	 */
	public void createListPanel() {
		listPanel = new BillListPanel("MY_FAVORITES_CODE1");
		listPanel.setDataFilterCustCondition(" creater = '" + userid + "' and isfolder is null"); // 设置条件
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); // 修改
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); // 删除
		listPanel.addBatchBillListButton(new WLTButton[] { btn_update, btn_delete });
		listPanel.repaintBillListButton();
		listPanel.addBillListHtmlHrefListener(this);
	}

	/*
	 * 点击html事件
	 */
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillListPanel listPanel = null;
		BillVO vo = _event.getBillListPanel().getSelectedBillVO();
		AbstractWorkPanel queryPanel = null;
		String classPath = vo.getStringValue("classpath");
		if (classPath == null || classPath.equals("")) {
			MessageBox.show(this, "类路径为空！");
			return;
		}
		if (classPath.contains("WFAndRiskEditWKPanel")) { // 这是个流程维护面板。特殊处理
			WFAndRiskEditWKPanel wk = new WFAndRiskEditWKPanel();
			wk.setEditable(false);
			wk.initialize();
			listPanel = wk.getBillList_cmpfile();
			listPanel.QueryDataByCondition(" id = " + vo.getStringValue("itemid"));
			listPanel.setDataFilterCustCondition(" id = " + vo.getStringValue("itemid"));
			queryPanel = wk;
		} else {
			try {
				queryPanel = (AbstractWorkPanel) Class.forName(classPath).newInstance(); // 通过类路径加载面板
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (queryPanel == null) {
				MessageBox.show(this, "出错！模板类反射加载失败！");
				return;
			}
			queryPanel.setLayout(new BorderLayout()); //
			queryPanel.initialize();
			Component com = queryPanel.getComponent(0); // 得到BillListPanel
			listPanel = null;
			if (com instanceof BillListPanel) {
				listPanel = (BillListPanel) com;
				listPanel.QueryDataByCondition(" id = " + vo.getStringValue("itemid")); // 设置显示数据唯一
				listPanel.setDataFilterCustCondition(" id = " + vo.getStringValue("itemid"));// 设置显示数据唯一
			}
		}
		//		if (classPath != null && classPath.contains("BargainModelWKPanel")) {
		//			BargainModelWKPanel panel = (BargainModelWKPanel) queryPanel;
		//			panel.setifProtectAndImport(); // 设置可编辑的都隐藏
		//		}
		listPanel.getQuickQueryPanel().setVisible(false); // 隐藏到快速查询面板！
		WLTButton joinFavority = listPanel.getBillListBtn("加入收藏"); // 得到面板中的收藏按钮
		if (joinFavority != null) {
			joinFavority.setVisible(false); // 隐藏到收藏按钮
		}
		dialog = new BillDialog(this, 800, 300);
		dialog.setTitle("我的收藏夹→" + vo.getStringValue("itemtype"));
		btn_cancel.addActionListener(this);
		WLTPanel btn_pane = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(), LookAndFeel.defaultShadeColor1, true);
		btn_pane.add(btn_cancel);
		dialog.add(queryPanel, BorderLayout.CENTER);
		dialog.add(btn_pane, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_cancel) {
			dialog.dispose();
		} else if (e.getSource() == btn_refresh) {
			reloadTreeData();
		} else if (e.getSource() == add_folder) {
			onAdd();
		} else if (e.getSource() == edit_folder) {
			onEdit();
		} else if (e.getSource() == delete_folder) {
			onDelete();
		} else if (((WLTButton) e.getSource()).getText().equals("从收藏夹删除")) {//从收藏夹移除
			onRemove((BillListPanel) ((WLTButton) e.getSource()).getBillPanelFrom());
		} else if (e.getSource() == btn_moveup) { //上移
			moveUpNode();
		} else if (e.getSource() == btn_movedown) {
			moveDownNode();
		}
	}

	/*
	 * 添加新文件夹
	 */
	private void onAdd() {
		String str = JOptionPane.showInputDialog(treePanel, "请输入文件夹名称");
		if (str == null || str.equals("")) {
			return;
		}

		BillVO vo = treePanel.getSelectedVO();
		BillCardPanel cardPanel = new BillCardPanel("MY_FAVORITES_CODE1");
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setValueAt("itemname", new StringItemVO(str));
		cardPanel.setValueAt("itemtype", new StringItemVO(str));
		DefaultMutableTreeNode treeNode = treePanel.getSelectedNode();
		if (treeNode.isRoot()) {
			cardPanel.setValueAt("parentid", new StringItemVO(""));
		} else {
			cardPanel.setValueAt("parentid", new StringItemVO(vo.getStringValue("id")));
		}
		int childNum = treeNode.getChildCount();
		int seq = 1;
		for (int i = 0; i < childNum; i++) {
			BillVO childVO = treePanel.getBillVOFromNode((DefaultMutableTreeNode) treeNode.getChildAt(i));
			String seq_1 = childVO.getStringValue("seq");
			if (seq_1 == null || seq_1.equals("")) {
				continue;
			}
			try {
				if (Integer.parseInt(seq_1) >= seq) {
					seq = Integer.parseInt(seq_1) + 1;
				}
			} catch (Exception e) {
				e.printStackTrace(); //无所谓
			}
		}
		cardPanel.setValueAt("seq", new StringItemVO(seq + ""));
		cardPanel.setValueAt("isfolder", new StringItemVO("Y"));
		try {
			UIUtil.executeBatchByDS(null, new String[] { cardPanel.getInsertSQL() });
			treePanel.addNode(cardPanel.getBillVO());
			treePanel.updateUI();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * 自定义文件夹名称修改
	 */
	private void onEdit() {
		String str = JOptionPane.showInputDialog(treePanel, "请输入文件夹名称");
		BillVO vo = treePanel.getSelectedVO();
		if (str != null && !str.equals("")) {
			if ("Y".equals(vo.getStringValue("isfolder"))) {
				UpdateSQLBuilder sql = new UpdateSQLBuilder("my_favorites");
				sql.putFieldValue("itemtype", str);
				sql.putFieldValue("itemname", str);
				sql.setWhereCondition(" id = " + vo.getStringValue("id"));
				try {
					UIUtil.executeUpdateByDS(null, sql);
					vo.setObject("itemtype", new StringItemVO(str));
					vo.setObject("itemname", new StringItemVO(str));
					treePanel.setBillVO(vo);
					treePanel.updateUI();
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

			}
		}

	}

	/*
	 * 从收藏夹中移除记录
	 */
	private void onRemove(BillListPanel _listPanel) {
		BillVO[] vo = _listPanel.getSelectedBillVOs();
		if (vo.length == 0) {
			MessageBox.show(_listPanel, "请至少选择一条记录!");
			return;
		}
		if (!MessageBox.confirm(this, "确定要删除选择的收藏记录吗？")) {
			return;
		}
		List inids = new ArrayList();
		for (int i = 0; i < vo.length; i++) {
			inids.add(vo[i].getPkValue());
		}
		try {
			UIUtil.executeBatchByDS(null, new String[] { "delete from my_favorites where  itemid in(" + tbutil.getInCondition(inids) + ") and creater=" + userid + " and type='" + type + "' and classpath='" + classpath + "'  and  isfolder is null " });
			_listPanel.removeSelectedRows(); //还需要把查询条件设置一把。
			List list = (List) _listPanel.getClientProperty("ids");
			if (list != null) {
				list.removeAll(inids);
				_listPanel.putClientProperty("ids", list);
			}
			_listPanel.setDataFilterCustCondition(listPanel.getTempletVO().getPkname() + " in ( " + tbutil.getInCondition(list) + ")"); // 重新设置显示数据唯一
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/*
	 * 删除目录树节点
	 */
	private void onDelete() {
		BillVO vo = treePanel.getSelectedVO();
		if ("Y".equals(vo.getStringValue("isfolder"))) {
			BillVO[] vos = treePanel.getSelectedChildPathBillVOs();
			List ids = new ArrayList();
			for (int i = 0; i < vos.length; i++) {
				ids.add(vos[i].getStringValue("id"));
			}
			try {
				String count = UIUtil.getStringValueByDS(null, " select count(id) from my_favorites where isfolder is null and parentid in(" + tbutil.getInCondition(ids) + ") and creater =" + userid);
				StringBuffer msg = new StringBuffer("确定要删除该节点下的所有文件夹");
				if (Integer.parseInt(count) > 0) {
					msg.append("和[" + count + "]条收藏记录");
				}
				msg.append("吗？");
				if (MessageBox.confirm(treePanel, msg)) {
					UIUtil.executeBatchByDS(null, new String[] { "delete from my_favorites where id in(" + tbutil.getInCondition(ids) + ") or parentid in (" + tbutil.getInCondition(ids) + ")" });
					treePanel.getSelectedNode().removeFromParent();
					treePanel.updateUI();
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (MessageBox.confirm(treePanel, "确定要删除此目录下所有收藏记录吗？")) {
				String itemtype = vo.getStringValue("itemtype");
				try {
					UIUtil.executeBatchByDS(null, new String[] { "delete from my_favorites where itemtype='" + itemtype + "' and isfolder is null and creater =" + userid });
					treePanel.getSelectedNode().removeFromParent();
					treePanel.updateUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 上移一个结点
	 */
	private void moveUpNode() {
		try {
			isTriggerSelectChangeEvent = false;
			DefaultMutableTreeNode myself = treePanel.getSelectedNode(); //
			//避免在初始化界面后没有默认选中节点时的空指针异常
			if (myself == null) {
				return;
			}
			DefaultMutableTreeNode father = (DefaultMutableTreeNode) myself.getParent(); //取得父亲
			if (father == null) {
				return;
			}
			int li_index = father.getIndex(myself); //
			if (li_index == 0) {
				return;
			}
			treePanel.getJTreeModel().removeNodeFromParent(myself); //
			treePanel.getJTreeModel().insertNodeInto(myself, father, li_index - 1); //
			treePanel.setSelected(father); //
			treePanel.resetChildSeq();
			treePanel.setSelected(myself);
			isTriggerSelectChangeEvent = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * 下移节点逻辑。
	 */
	private void moveDownNode() {
		try {
			isTriggerSelectChangeEvent = false;
			DefaultMutableTreeNode myself = treePanel.getSelectedNode(); //
			//避免在初始化界面时，未有默认选中节点时的空指针异常
			if (myself == null) {
				return;
			}
			DefaultMutableTreeNode father = (DefaultMutableTreeNode) myself.getParent(); //
			if (father == null) {
				return;
			}
			int li_index = father.getIndex(myself); //
			if (li_index == father.getChildCount() - 1) {
				return; //如果是最后一个了,则不做处理
			}
			treePanel.getJTreeModel().removeNodeFromParent(myself); //
			treePanel.getJTreeModel().insertNodeInto(myself, father, li_index + 1); //
			treePanel.setSelected(father); //
			treePanel.resetChildSeq();
			treePanel.setSelected(myself);
			isTriggerSelectChangeEvent = true;
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 得到加入收藏按钮
	 * 
	 * @param _type
	 *            收藏记录的类型 外规，内规
	 * @param classPath
	 *            类路径 com.pushworld.ipushgrc.ui.law.p020.LawQueryWKPanel
	 * @param _colName
	 *            收藏记录显示的名称列（外规名称，内规名称）
	 * @return
	 */
	public static WLTButton getJoinFavorityButton(String _type, String classPath, String _colName) {
		WLTButton btn_favority = new WLTButton("加入收藏");
		btn_favority.addActionListener(new JoinMyFavoriteBtnAction(_type, classPath, _colName));
		return btn_favority;
	}

	/*
	 *隐藏所有无相关的按钮。 
	 */
	private void setBillListPanelBtnVisible(BillListPanel listPanel) {
		WLTButton[] btn = listPanel.getBillListBtnPanel().getAllButtons();
		for (int i = 0; i < btn.length; i++) {
			if ((btn[i].getBtnDefineVo().getClickedformula() != null && btn[i].getBtnDefineVo().getClickedformula().contains(JoinMyFavoriteBtnAction.class.getName())) || ((btn[i].getCustActionListener() != null && (JoinMyFavoriteBtnAction.class.getName()).equals(btn[i].getCustActionListener().getClass().getName())))) {
				btn[i].setVisible(false);
			} else if (btn[i].getName().contains("修") || btn[i].getName().contains("新") || btn[i].getName().contains("除") || btn[i].getName().contains("编辑")) {
				btn[i].setVisible(false);
			}
		}
	}

	/*
	 * 根据收藏数据类型，业务页面的路径，数据ID穿得到面板。
	 */
	public JPanel getPanel(String type, String classPath, List ids) throws Exception {
		WLTButton btn_remove = new WLTButton("从收藏夹删除", UIUtil.getImage("office_130.gif"));
		btn_remove.addActionListener(this);
		this.type = type;
		this.classpath = classPath;
		if ("0".equals(type)) {
			AbstractWorkPanel queryPanel = (AbstractWorkPanel) Class.forName(classPath).newInstance(); // 通过类路径加载面板
			queryPanel.setLayout(new BorderLayout()); //
			if (queryPanel instanceof WFAndRiskEditWKPanel) {//流程文件查询里有个【加入收藏夹】按钮，使用了维护模板的过滤条件，故导致加入后有的数据查不到。【李春娟/2013-06-26】
				((WFAndRiskEditWKPanel) queryPanel).setEditable(false);
			}
			queryPanel.initialize();
			if (queryPanel.getComponent(0) instanceof BillListPanel) {
				BillListPanel listPanel_0 = (BillListPanel) queryPanel.getComponent(0);//个人觉得这里的列表应该先去掉所有的过滤条件，然后加下面的主键 in()【李春娟/2013-06-26】
				setBillListPanelBtnVisible(listPanel_0);
				listPanel_0.addBatchBillListButton(new WLTButton[] { btn_remove });
				listPanel_0.repaintBillListButton();
				listPanel_0.QueryDataByCondition(listPanel_0.getTempletVO().getPkname() + " in ( " + tbutil.getInCondition(ids) + ")"); // 设置显示数据唯一
				listPanel_0.setDataFilterCustCondition(listPanel_0.getTempletVO().getPkname() + " in ( " + tbutil.getInCondition(ids) + ")"); // 设置显示数据唯一
				listPanel_0.putClientProperty("ids", ids);
			}
			return queryPanel;
		} else if ("1".equals(type)) {
			AbstractWorkPanel queryPanel = new LoginUtil().getWorkPanelByMenuVO(classPath);
			queryPanel.setLayout(new BorderLayout()); //
			queryPanel.initialize();
			if (queryPanel.getComponent(0) instanceof BillListPanel) {
				BillListPanel listPanel_0 = (BillListPanel) queryPanel.getComponent(0);
				setBillListPanelBtnVisible(listPanel_0);
				listPanel_0.addBatchBillListButton(new WLTButton[] { btn_remove });
				listPanel_0.repaintBillListButton();
				listPanel_0.QueryDataByCondition(listPanel_0.getTempletVO().getPkname() + " in ( " + tbutil.getInCondition(ids) + ")"); // 设置显示数据唯一
				listPanel_0.setDataFilterCustCondition(listPanel_0.getTempletVO().getPkname() + " in ( " + tbutil.getInCondition(ids) + ")"); // 设置显示数据唯一
				listPanel_0.putClientProperty("ids", ids);
			}
			return queryPanel;
		} else if ("2".equals(type)) {
			BillListPanel listPanel = new BillListPanel(classPath);
			setBillListPanelBtnVisible(listPanel);
			listPanel.addBatchBillListButton(new WLTButton[] { btn_remove });
			listPanel.repaintBillListButton();
			listPanel.QueryDataByCondition(listPanel.getTempletVO().getPkname() + " in ( " + tbutil.getInCondition(ids) + ")"); // 设置显示数据唯一
			listPanel.setDataFilterCustCondition(listPanel.getTempletVO().getPkname() + " in ( " + tbutil.getInCondition(ids) + ")"); // 设置显示数据唯一
			this.listPanel = listPanel;
			this.listPanel.putClientProperty("ids", ids);
			return listPanel;
		}
		WLTPanel panel = new WLTPanel(new BorderLayout());
		panel.add(new WLTLabel());
		return new WLTPanel();
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent e) {
		DefaultMutableTreeNode node = e.getCurrSelectedNode();
		if (!isTriggerSelectChangeEvent) {
			return;
		}
		if (node.isRoot()) {
			//这里可以全部显示出个人收藏的所有数据。
			createListPanel();
			rightPanel.removeAll();
			rightPanel.setLayout(new BorderLayout());
			rightPanel.add(listPanel);
			rightPanel.updateUI();
		} else {
			BillVO vo = e.getCurrSelectedVO();
			String str = vo.getStringValue("isfolder", "N");
			if ("Y".equals(str)) {
				try {
					tab_list = new ArrayList();
					HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select * from MY_FAVORITES  where parentid = " + vo.getStringValue("id") + " and (isfolder!='Y' or isfolder is null) and creater=" + userid);
					classType = new LinkedHashMap();//该自定义文件夹下有多少种面板。
					HashMap radiotitle = new HashMap();
					for (int i = 0; i < vos.length; i++) {
						String type = vos[i].getStringValue("type", "0");
						String classPath = vos[i].getStringValue("classpath");
						String key = type + "&" + classPath;
						radiotitle.put(key, vos[i].getStringValue("templetname"));
						if (classType.containsKey(key)) {
							List list = (List) classType.get(key);
							list.add(vos[i].getStringValue("itemid"));
							classType.put(key, list);
						} else {
							List list = new ArrayList();
							list.add(vos[i].getStringValue("itemid"));
							classType.put(key, list);
						}
					}
					Iterator it0 = classType.entrySet().iterator();
					while (it0.hasNext()) {
						Entry en = (Entry) it0.next();
						String key = (String) en.getKey(); //得到    2&com.pushworld.com....
						String type = key.split("&")[0];
						String classpath = key.split("&")[1];
						String r_title = (String) radiotitle.get(key);
						tab_list.add(new Object[] { type, classpath, en.getValue(), r_title });
					}
					JPanel rightPanel_1 = new JPanel();
					if (tab_list.size() == 1) {
						Object[] obj = (Object[]) tab_list.get(0);
						String type = (String) obj[0];
						String classPath = (String) obj[1];
						List list = (List) obj[2];
						rightPanel_1 = getPanel(type, classPath, list);
					} else if (classType.size() > 1) { //一个文件夹下有多种类型.搞成多页签.
						radioPane = new WLTRadioPane();
						for (int i = 0; i < tab_list.size(); i++) {
							if (i == 0) {
								Object[] obj = (Object[]) tab_list.get(i);
								String type = (String) obj[0];
								String classPath = (String) obj[1];
								List list = (List) obj[2];
								String title = (String) obj[3];
								if (title != null && !title.equals("")) {
									radioPane.addTab(title, getPanel(type, classPath, list));
								} else {
									radioPane.addTab("(" + (i + 1) + ")", getPanel(type, classPath, list));
								}
							} else {
								Object[] obj = (Object[]) tab_list.get(i);
								WLTPanel panel = new WLTPanel();
								String title = (String) obj[3];
								if (title != null && !title.equals("")) {
									radioPane.addTab(title, panel);
								} else {
									radioPane.addTab("(" + (i + 1) + ")", panel);
								}
							}

						}
						radioPane.addChangeListener(this);
						radioPane.putClientProperty(0, "Y");
						rightPanel_1 = radioPane;
					} else if (tab_list.size() == 0) {
						rightPanel_1.add(new WLTLabel("<html><p color=\"#AA00DD\" ><B>该文件夹没有收藏记录!<B></p></html>"), BorderLayout.NORTH);
					}
					rightPanel.removeAll();
					rightPanel.setLayout(new BorderLayout());
					rightPanel.add(rightPanel_1, BorderLayout.CENTER);
					rightPanel.updateUI();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else { //如果是N 也就是以前收藏机制，不能自定义文件夹
				HashVO vos[] = null;
				try {
					vos = UIUtil.getHashVoArrayByDS(null, "select * from MY_FAVORITES where itemtype = '" + vo.getStringValue("itemtype") + "' and isfolder is null and parentid is null and creater=" + userid);
					tab_list = new ArrayList();
					classType = new LinkedHashMap();//该自定义文件夹下有多少种面板。
					HashMap radiotitle = new HashMap();
					for (int i = 0; i < vos.length; i++) {
						String type = vos[i].getStringValue("type", "0");
						String classPath = vos[i].getStringValue("classpath");
						String key = type + "&" + classPath;
						radiotitle.put(key, vos[i].getStringValue("templetname"));
						if (classType.containsKey(key)) {
							List list = (List) classType.get(key);
							list.add(vos[i].getStringValue("itemid"));
							classType.put(key, list);
						} else {
							List list = new ArrayList();
							list.add(vos[i].getStringValue("itemid"));
							classType.put(key, list);
						}
					}
					JPanel rightPanel_1 = new JPanel();
					Iterator it = classType.entrySet().iterator();
					while (it.hasNext()) {
						Entry en = (Entry) it.next();
						String key = (String) en.getKey(); //得到    2&com.pushworld.com....
						String type = key.split("&")[0];
						String classpath = key.split("&")[1];
						String title = (String) radiotitle.get(key);
						tab_list.add(new Object[] { type, classpath, en.getValue(), title });
					}

					if (tab_list.size() == 1) {
						Object[] obj = (Object[]) tab_list.get(0);
						String type = (String) obj[0];
						String classPath = (String) obj[1];
						List list = (List) obj[2];
						rightPanel_1 = getPanel(type, classPath, list);
					} else if (classType.size() > 1) { //一个文件夹下有多种类型.搞成多页签.
						radioPane = new WLTRadioPane();
						for (int i = 0; i < tab_list.size(); i++) {
							if (i == 0) {
								Object[] obj = (Object[]) tab_list.get(i);
								String type = (String) obj[0];
								String classPath = (String) obj[1];
								List list = (List) obj[2];
								String title = (String) obj[3];
								if (title != null && !title.equals("")) {
									radioPane.addTab(title, getPanel(type, classPath, list));
								} else {
									radioPane.addTab("(" + (i + 1) + ")", getPanel(type, classPath, list));
								}
							} else {
								WLTPanel panel = new WLTPanel();
								Object[] obj = (Object[]) tab_list.get(i);
								String title = (String) obj[3];
								if (title != null && !title.equals("")) {
									radioPane.addTab(title, panel);
								} else {
									radioPane.addTab("(" + (i + 1) + ")", panel);
								}
							}
						}
						radioPane.addChangeListener(this);
						radioPane.putClientProperty(0, "Y");
						rightPanel_1 = radioPane;
					} else if (tab_list.size() == 0) { //如果一个都没有
						rightPanel_1.add(new WLTLabel("<html><p color=\"#AA00DD\" ><B>该文件夹没有收藏记录!<B></p></html>"), BorderLayout.NORTH);
					}
					rightPanel.removeAll();
					rightPanel.setLayout(new BorderLayout());
					rightPanel.add(rightPanel_1);
					rightPanel.updateUI();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == radioPane) {
			int index = radioPane.getSelectIndex();
			String isload = (String) radioPane.getClientProperty(index);
			if (isload == null) {
				Object obj[] = (Object[]) tab_list.get(index);
				String type = (String) obj[0];
				String classPath = (String) obj[1];
				List ids = (List) obj[2];
				try {
					JPanel jp = getPanel(type, classPath, ids);
					JPanel jPanel = (JPanel) radioPane.getTabCompent(index);
					jPanel.removeAll();
					jPanel.setLayout(new BorderLayout());
					jPanel.add(jp);
					jPanel.updateUI();
					radioPane.putClientProperty(index, "Y");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
			}
		}
	}

	/*
	 * 数据刷新，加载数据
	 */
	public void reloadTreeData() {
		BillVO[] vos = queryTreeData();
		treePanel.queryDataByCondition(" 2=1 ");
		treePanel.addNodes(vos, false);
		treePanel.expandOneNode(treePanel.getRootNode());
		treePanel.updateUI();
	}
}

/*
 * 自定义树节点Renderer。
 */
class MyTreeCellRender extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 5500760588674948602L;
	private Icon icon_dir1 = UIUtil.getImage("office_151.gif");
	private Icon icon_dir2 = UIUtil.getImage("office_057.gif");
	private Icon root = UIUtil.getImage("folder_star.png");
	private BillTreePanel panel;

	public MyTreeCellRender(BillTreePanel _panel) {
		panel = _panel;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		JLabel label = null;
		if (((DefaultMutableTreeNode) value).isRoot()) {
			label = new JLabel(((DefaultMutableTreeNode) value).getUserObject().toString()); //
			label.setIcon(root);
		} else {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			BillVO hvo = panel.getBillVOFromNode(node);
			String isfolder = hvo.getStringValue("isfolder");
			String str_text = hvo.getStringValue("itemtype"); //
			label = new JLabel(str_text); //

			//设置图标!
			if ("Y".equals(isfolder)) {
				label.setIcon(icon_dir1);
			} else {
				label.setIcon(icon_dir2); //
			}
		}
		//设置字体颜色!
		if (sel) {
			label.setOpaque(true); //如果选中的话,则不透明..
			label.setForeground(Color.RED); //
			label.setBackground(Color.YELLOW); //
		} else {
			label.setOpaque(false); //透明!
		}
		return label;
	}
}
