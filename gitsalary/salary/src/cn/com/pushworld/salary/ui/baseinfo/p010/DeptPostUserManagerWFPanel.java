package cn.com.pushworld.salary.ui.baseinfo.p010;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.sysapp.corpdept.CorpDeptBillTreePanel;
import cn.com.infostrategy.ui.sysapp.corpdept.SeqListDialog;
import cn.com.infostrategy.ui.sysapp.corpdept.TransUserDialog;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 *为了不改平台的类， 复制了一份出来项目上使用，不同点有： 1. 扩展了人员信息 2. 机构， 岗位， 人员 都在此功能中维护， 不要到处找了 3.
 * 增加了：“设为主岗位”功能 4. 增加了岗位调动的记录【zzl 2017-11-6】
 */
public class DeptPostUserManagerWFPanel extends AbstractWorkPanel implements
		BillTreeSelectListener, BillListSelectListener, ActionListener,
		BillListMouseDoubleClickedListener {

	private BillTreePanel billTreePanel_Dept = null; // 机构树
	private BillListPanel billListPanel_Post = null; // 岗位表
	private BillListPanel billListPanel_User_Post = null; // 人员岗位表.
	private BillListPanel billListPanel_User_Role = null; // 人员角色关系表.
	private BillListPanel billListPanel_user_flat = null; // 人员扁平查看

	private WLTTabbedPane tabbedPane = null; // 页签，如果菜单参数中配置可以扁平查看并且在维护界面有人员列表，则需要用页签格式显示

	private WLTButton btn_post_insert, btn_post_update, btn_post_delete,
			btn_post_seq; // 岗位列表上的系列按钮
	private WLTButton btn_user_import, btn_user_add, btn_user_update,
			btn_user_trans, btn_user_del, btn_user_seq, btn_lookdeptinfo1; // 人员列表上的系列按钮
	private WLTButton btn_role_add, btn_role_del; // 角色列表上的系列按钮

	private WLTButton btn_gotodept, btn_edituser, btn_lookdeptinfo2;// 扁平查看的系列按钮

	private String str_currdeptid, str_currdeptname = null; // 当前机构ID及机构名称

	private int li_granuType = 3; // 粒度类型,1-最粗的,2-最常用的,3-最细的,即所有的
	private boolean isFilter = false; // 是否权限过滤,即是否根据登录人员进行权限过滤!!!
	private String panelStyle = "5";// 页面布局风格，兼容旧系统，默认是机构、岗位、人员、角色 都有
	private boolean canSearch = true;// 是否有扁平查看页签

	private WLTButton btn_setdefault;

	private WLTButton btn_tree_insert, btn_tree_update, btn_tree_delete;

	public DeptPostUserManagerWFPanel() {
	}

	private final static String MSG_NEED_DEPT = "请选择一个机构!";
	private final static String MSG_NEED_USER = "请选择一个人员!";

	/**
	 * 带两个参数的构造方法!!!
	 * 
	 * @param _granuType
	 * @param _isfilter
	 */
	public DeptPostUserManagerWFPanel(String _granuType, String _isfilter) {
		li_granuType = Integer.parseInt(_granuType); //
		isFilter = Boolean.parseBoolean(_isfilter); //
	}

	@Override
	public void initialize() {
		HashMap confmap = this.getMenuConfMap();// 菜单参数
		if (confmap.get("页面布局") != null) {// 页面布局，有五种：1-机构、岗位;2-机构、岗位、人员;3-机构、人员;4-机构、人员、角色;5-机构、岗位、人员、角色
			panelStyle = (String) confmap.get("页面布局");
		}
		if (confmap.get("是否可以扁平查看") != null) {
			String tmp_str = (String) confmap.get("是否可以扁平查看");
			if (tmp_str.equalsIgnoreCase("N")
					|| tmp_str.equalsIgnoreCase("false")
					|| tmp_str.equalsIgnoreCase("否")) {
				canSearch = false;
			}
		}

		this.setLayout(new BorderLayout()); //
		billTreePanel_Dept = new CorpDeptBillTreePanel(getCorpTempletCode(),
				getGranuType(), isFilter()); // 机构树,定义是否需要进行权限过滤!!

		btn_tree_insert = WLTButton.createButtonByType(WLTButton.TREE_INSERT);
		btn_tree_insert.addActionListener(this);
		btn_tree_update = WLTButton.createButtonByType(WLTButton.TREE_EDIT);
		btn_tree_update.addActionListener(this);
		btn_tree_delete = new WLTButton("删除");
		btn_tree_delete.addActionListener(this);
		billTreePanel_Dept.addBatchBillTreeButton(new WLTButton[] {
				btn_tree_insert, btn_tree_update, btn_tree_delete });
		billTreePanel_Dept.queryDataByCondition(null); // 查看所有机构
		billTreePanel_Dept.addBillTreeSelectListener(this); //
		// billTreePanel_Dept.getBtnPanel().setVisible(true);
		billTreePanel_Dept.getQuickLocatePanel().setVisible(true);
		billTreePanel_Dept.repaintBillTreeButton();
		WLTSplitPane splitPanel_all = null;

		if ("1".equals(panelStyle)) {// 维护页面是否是第一种风格，只有 机构、岗位
			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
					billTreePanel_Dept, this.getPostPanel()); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("2".equals(panelStyle)) {// 是否是第二种风格，只有 机构、岗位、人员
			WLTSplitPane splitPanel_post_user = new WLTSplitPane(
					WLTSplitPane.VERTICAL_SPLIT, this.getPostPanel(), this
							.getUserPanel()); //
			splitPanel_post_user.setDividerLocation(175); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
					billTreePanel_Dept, splitPanel_post_user); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("3".equals(panelStyle)) {// 是否是第三种风格，只有 机构、人员
			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
					billTreePanel_Dept, this.getUserPanel()); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("4".equals(panelStyle)) {// 是否是第四种风格，只有 机构、人员、角色
			WLTSplitPane splitPanel_user_role = new WLTSplitPane(
					WLTSplitPane.VERTICAL_SPLIT, this.getUserPanel(), this
							.getRolePanel()); //
			splitPanel_user_role.setDividerLocation(300); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
					billTreePanel_Dept, splitPanel_user_role); //
			splitPanel_all.setDividerLocation(280); //
		} else {// 第五种风格，也就是 机构、岗位、人员、角色 都有
			WLTSplitPane splitPanel_user_role = new WLTSplitPane(
					WLTSplitPane.HORIZONTAL_SPLIT, this.getUserPanel(), this
							.getRolePanel()); //
			splitPanel_user_role.setDividerLocation(360); //

			WLTSplitPane splitPanel_post_user = new WLTSplitPane(
					WLTSplitPane.VERTICAL_SPLIT, this.getPostPanel(),
					splitPanel_user_role); //
			splitPanel_post_user.setDividerLocation(175); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,
					billTreePanel_Dept, splitPanel_post_user); //
			splitPanel_all.setDividerLocation(175); //
		}

		if (canSearch) {// 如果配置为可以扁平查看，则绘制面板
			tabbedPane = new WLTTabbedPane(); //
			tabbedPane.addTab("信息维护", UIUtil.getImage("office_134.gif"),
					splitPanel_all); //
			tabbedPane.addTab("人员查找", UIUtil.getImage("office_194.gif"), this
					.getUserFlatPanel()); //
			this.add(tabbedPane); //
		} else {// 没有扁平查看，直接加入维护界面
			this.add(splitPanel_all); //
		}
	}

	/**
	 * 获得机构模板
	 * 
	 * @return
	 */
	public String getCorpTempletCode() {
		return "PUB_CORP_DEPT_CODE1"; // 最简单的
	}

	/**
	 * 粒度类型,1-最粗的,2-最常用的,3-最细的,即所有的
	 * 
	 * @return
	 */
	public int getGranuType() {
		return li_granuType; //
	}

	/**
	 * 是否权限过滤,即是否根据登录人员进行权限过滤!!!
	 * 
	 * @return
	 */
	public boolean isFilter() {
		return isFilter;
	}

	/**
	 * 获得岗位列表
	 * 
	 * @return
	 */
	private BillListPanel getPostPanel() {
		if (this.billListPanel_Post == null) {
			billListPanel_Post = new BillListPanel("PUB_POST_CODE1_SALARY"); // 岗位
			// billListPanel_Post.setItemVisible("refpostid", true);//在此显示岗位组信息
			btn_post_insert = new WLTButton("新增"); // 新增岗位!!
			btn_post_update = WLTButton
					.createButtonByType(WLTButton.LIST_POPEDIT); // 修改岗位
			btn_post_delete = WLTButton
					.createButtonByType(WLTButton.LIST_DELETE); // 删除岗位!!
			btn_post_seq = new WLTButton("排序");// 岗位排序
			btn_post_insert.addActionListener(this); //
			btn_post_seq.addActionListener(this);
			btn_post_delete.addActionListener(this);
			billListPanel_Post.addBatchBillListButton(new WLTButton[] {
					btn_post_insert, btn_post_update, btn_post_delete,
					btn_post_seq }); //
			billListPanel_Post.repaintBillListButton(); //
			// 如果有人员，则增加岗位的选择事件
			if ("2".equals(panelStyle) || "5".equals(panelStyle)) {//
				billListPanel_Post.addBillListSelectListener(this); // 岗位选择变化时.
			}
		}
		return billListPanel_Post;
	}

	/**
	 * 获得人员列表
	 * 
	 * @return
	 */
	private BillListPanel getUserPanel() {
		if (this.billListPanel_User_Post == null) {
			billListPanel_User_Post = new BillListPanel("PUB_USER_POST_DEFAULT"); // 人员_岗位的关联表!PUB_USER_POST_DEFAULT
			btn_user_import = new WLTButton("导入");
			btn_user_add = new WLTButton("新增");
			btn_user_update = new WLTButton("修改");
			btn_user_trans = new WLTButton("迁移");
			btn_user_del = new WLTButton("删除");
			btn_user_seq = new WLTButton("排序");// 岗位排序
			btn_lookdeptinfo1 = new WLTButton("兼职情况"); //
			btn_setdefault = new WLTButton("设为主岗位");
			btn_user_import.addActionListener(this);
			btn_user_add.addActionListener(this);
			btn_user_update.addActionListener(this);
			btn_user_trans.addActionListener(this);
			btn_user_del.addActionListener(this);
			btn_user_seq.addActionListener(this);
			btn_lookdeptinfo1.addActionListener(this); //
			btn_setdefault.addActionListener(this);
			billListPanel_User_Post.addBatchBillListButton(new WLTButton[] {
					btn_user_import, btn_user_add, btn_user_update,
					btn_user_trans, btn_user_del, btn_lookdeptinfo1,
					btn_setdefault }); // 为机构_人员_岗位表加上所有按钮!!
			billListPanel_User_Post.repaintBillListButton(); //
			// 如果有角色，则增加人员的选择事件
			if ("4".equals(panelStyle) || "5".equals(panelStyle)) {//
				billListPanel_User_Post.addBillListSelectListener(this); // 人员选择变化时.
			}

			billListPanel_User_Post.addBillListMouseDoubleClickedListener(this);
		}
		return billListPanel_User_Post;
	}

	/**
	 * 获得角色列表
	 * 
	 * @return
	 */
	private BillListPanel getRolePanel() {
		if (this.billListPanel_User_Role == null) {
			billListPanel_User_Role = new BillListPanel("PUB_USER_ROLE_CODE1"); // 人员_角色的关联表!
			btn_role_add = new WLTButton("新增");
			btn_role_del = new WLTButton("删除");
			btn_role_add.addActionListener(this); // 新增角色
			btn_role_del.addActionListener(this); // 删除角色!
			billListPanel_User_Role.addBatchBillListButton(new WLTButton[] {
					btn_role_add, btn_role_del }); //
			billListPanel_User_Role.repaintBillListButton(); //
		}
		return billListPanel_User_Role;
	}

	/**
	 * 获得人员扁平查看列表
	 * 
	 * @return
	 */
	private BillListPanel getUserFlatPanel() {
		if (this.billListPanel_user_flat == null) {
			billListPanel_user_flat = new BillListPanel("PUB_USER_ICN"); // 扁平查看!
			btn_gotodept = new WLTButton("精确定位"); //
			btn_edituser = new WLTButton("编辑"); //
			btn_lookdeptinfo2 = new WLTButton("兼职情况"); //
			btn_gotodept.addActionListener(this); //
			btn_edituser.addActionListener(this); //
			btn_lookdeptinfo2.addActionListener(this); //
			billListPanel_user_flat.addBatchBillListButton(new WLTButton[] {
					btn_gotodept, btn_edituser, btn_lookdeptinfo2 }); // //
			billListPanel_user_flat.repaintBillListButton(); // 重新按钮绘制!!!
			billListPanel_user_flat.getQuickQueryPanel().setVisible(true); //
		}
		return billListPanel_user_flat;
	}

	/**
	 * 机构树的选择事件
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		str_currdeptid = _event.getCurrSelectedVO().getStringValue("id"); //
		str_currdeptname = _event.getCurrSelectedVO().getStringValue("name"); //
		if (billListPanel_Post != null) {
			billListPanel_Post.queryDataByCondition("deptid='" + str_currdeptid
					+ "'", "seq,code"); //
		}
		if (billListPanel_User_Post != null) {
			billListPanel_User_Post.queryDataByCondition("deptid='"
					+ str_currdeptid + "'", "seq,userseq"); //
		}
		if (billListPanel_User_Role != null) {
			billListPanel_User_Role.clearTable(); //
		}
	}

	/**
	 * 列表的选择事件，选择岗位刷新人员，选择人员刷新角色
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == billListPanel_Post
				&& billListPanel_User_Post != null) {// 如果点击的是岗位列表
														// 并且有人员列表则刷新人员列表
			String str_postid = _event.getCurrSelectedVO().getStringValue("id"); //
			billListPanel_User_Post.queryDataByCondition("postid='"
					+ str_postid + "'", "seq"); //
			if (billListPanel_User_Role != null) {
				billListPanel_User_Role.clearTable(); //
			}
		} else if (_event.getSource() == billListPanel_User_Post
				&& billListPanel_User_Role != null) {// 如果点击的是人员列表
														// 并且有角色列表则刷新角色列表
			String str_useridid = _event.getCurrSelectedVO().getStringValue(
					"userid"); //
			billListPanel_User_Role.QueryDataByCondition("userid='"
					+ str_useridid + "'"); //
		}

	}

	/**
	 * 所有按钮的点击事件
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == btn_post_insert) {
				onInsertPost(); // 新增岗位!!
			} else if (e.getSource() == btn_post_seq) {
				onSeqPost();// 岗位排序
			} else if (e.getSource() == btn_user_import) {
				onImportUser(); // 导入人员
			} else if (e.getSource() == btn_user_add) {
				onAddUser(); // 新增人员
			} else if (e.getSource() == btn_user_update) {
				onUpdateUser(); // 编辑人员
			} else if (e.getSource() == btn_user_trans) {
				onTransUser(); // 迁移人员
			} else if (e.getSource() == btn_user_del) {
				onDelUser(); // 删除人员
			} else if (e.getSource() == btn_user_seq) {
				onSeqUser();// 人员排序
			} else if (e.getSource() == btn_lookdeptinfo1) {
				onLookUserDeptInfos(1); // 维护页签，查看人员兼职情况
			} else if (e.getSource() == btn_role_add) {
				onAddRole(); // 新增角色
			} else if (e.getSource() == btn_role_del) {
				onDelRole(); // 删除角色
			} else if (e.getSource() == btn_gotodept) {
				onGoToDept(); // 扁平查看页签，精确定位
			} else if (e.getSource() == btn_edituser) {
				onEditUser(); // 扁平查看页签，编辑人员
			} else if (e.getSource() == btn_lookdeptinfo2) {
				onLookUserDeptInfos(2); // 扁平查看页页签，查看人员兼职情况
			} else if (e.getSource() == btn_setdefault) {
				onSetDefaultPost();
			} else if (e.getSource() == btn_tree_delete) {
				onDeptDelete();
			} else if (e.getSource() == btn_post_delete) {
				onPostDelete();
			} else if (e.getSource() == btn_tree_insert) {
				onDeptAdd();
			} else if (e.getSource() == btn_tree_update) {
				onDeptUpdate();
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 树新增
	 * 
	 * @throws Exception
	 */
	private void onDeptAdd() throws Exception {
		if (billTreePanel_Dept.getSelectedNode() == null) {
			MessageBox.show(this, "请选择一个父结点进行新增操作!"); //
			return;
		}

		BillVO billVO = billTreePanel_Dept.getSelectedVO();
		BillCardPanel insertCardPanel = new BillCardPanel(billTreePanel_Dept
				.getTempletVO()); //
		if (billVO != null) { // 如果选中的不是根结点
			insertCardPanel.insertRow(); // 新增一行
			insertCardPanel.setEditableByInsertInit(); //
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) billTreePanel_Dept
					.getSelectedNode();
			if (!parentNode.isRoot()) { //
				BillVO parentVO = billVO; //
				String parent_id = ((StringItemVO) parentVO
						.getObject(billTreePanel_Dept.getTempletVO()
								.getTreepk())).getStringValue(); //
				insertCardPanel.setCompentObjectValue(billTreePanel_Dept
						.getTempletVO().getTreeparentpk(), new StringItemVO(
						parent_id)); //
			}
		} else { // 如果选中的是根结点
			insertCardPanel.insertRow(); //
			insertCardPanel.setEditableByInsertInit(); //
		}

		BillCardDialog dialog = new BillCardDialog(billTreePanel_Dept, "新增",
				insertCardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = insertCardPanel.getBillVO(); //
			newVO.setToStringFieldName(billTreePanel_Dept.getTempletVO()
					.getTreeviewfield()); //
			billTreePanel_Dept.addNode(newVO); //
			reloaCorpCacheData(); // 刷新 缓存
		}
	}

	/**
	 * 树编辑..
	 * 
	 * @throws Exception
	 */
	private void onDeptUpdate() throws Exception {
		if (billTreePanel_Dept.getSelectedNode() == null) {
			MessageBox.show(this, "请选择一个结点进行编辑操作!"); //
			return;
		}

		BillVO billVO = billTreePanel_Dept.getSelectedVO();
		BillCardPanel editCardPanel = new BillCardPanel(billTreePanel_Dept
				.getTempletVO()); //

		if (billVO != null) { // 如果选中的不是根结点
			editCardPanel.queryDataByCondition("id='" + billVO.getPkValue()
					+ "'"); //
			editCardPanel.setEditableByEditInit(); //
		} else { // 如果选中的是根结点
			MessageBox.show(this, "根结点不可编辑!!"); //
			return; // 如果没有选择一个结点则直接返回
		}

		BillCardDialog dialog = new BillCardDialog(billTreePanel_Dept, "编辑",
				editCardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = editCardPanel.getBillVO(); //
			newVO.setToStringFieldName(billTreePanel_Dept.getTempletVO()
					.getTreeviewfield()); //
			billTreePanel_Dept.setBillVOForCurrSelNode(newVO); // 向树中回写数据
			billTreePanel_Dept.updateUI(); // 以前是billTree.getJTree().updateUI();会改变展开和收缩图标，李春娟2012-02-23修改
			reloaCorpCacheData();
		}
	}

	/*
	 * 岗位删除操作。删除前判断是否有人员，如果有
	 */
	private void onPostDelete() {
		if (billListPanel_User_Post.getAllBillVOs().length > 0) {
			MessageBox.show(this, "请先用人员迁移或删除功能解除该岗位与人员的关系");
			return;
		}
		billListPanel_Post.doDelete(false);
	}

	/**
	 * 设置选中岗位为默认岗位 Gwang 2013-07-30
	 */
	private void onSetDefaultPost() throws Exception {
		BillVO vo = billListPanel_User_Post.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(billListPanel_User_Post);
			return;
		}
		String userid = vo.getStringValue("userid");
		String pk = vo.getStringValue("id");
		String sql_1 = "update pub_user_post set isdefault = null where userid = "
				+ userid;
		String sql_2 = "update pub_user_post set isdefault = 'Y' where id = "
				+ pk;
		String sql_3 = "update pub_user set pk_dept = " + str_currdeptid
				+ " where id = " + userid;
		UIUtil.executeBatchByDS(null, new String[] { sql_1, sql_2, sql_3 });

		// 刷新列表数据
		BillVO postVO = billListPanel_Post.getSelectedBillVO();
		if (postVO != null) {
			billListPanel_User_Post.refreshCurrSelectedRow();
		} else {
			billListPanel_User_Post.QueryDataByCondition("deptid = "
					+ str_currdeptid);
		}
		checkandcreateduties(userid);
	}

	/**
	 * 新增岗位
	 */
	private void onInsertPost() {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}
		HashMap initValueMap = new HashMap(); //
		initValueMap.put("deptid", new RefItemVO(str_currdeptid, "",
				str_currdeptname)); // 定义初始值!因操作要求中相关岗位的需要，将机构设置为了参照，所以需要修改这个为参照vo【李春娟/2012-03-29】
		billListPanel_Post.doInsert(initValueMap); // 做新增操作!!!
	}

	/**
	 * 岗位排序
	 */
	private void onSeqPost() {
		// 一开始用的billListPanel_Post.getBillVOs(),发现删除了的记录也出现了,注意billListPanel_Post.getAllBillVOs()
		// 是获得显示的billvo，不包括删除的billvo！
		SeqListDialog dialog_post = new SeqListDialog(this, "岗位排序",
				billListPanel_Post.getTempletVO(), billListPanel_Post
						.getAllBillVOs());
		dialog_post.setVisible(true);
		if (dialog_post.getCloseType() == 1) {// 如果点击确定返回，则需要刷新一下页面
			billListPanel_Post.queryDataByCondition("deptid='" + str_currdeptid
					+ "'", "seq"); //
		}
	}

	/**
	 * 导入人员
	 */
	private void onImportUser() throws Exception {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}

		String str_postid = null; //
		String str_postname = null; //

		if (billListPanel_Post != null) {// 是否有岗位列表，如果有才提示
			BillVO billVO_Post = billListPanel_Post.getSelectedBillVO(); //
			if (billVO_Post == null) {
				MessageBox.show(this, "请选择一个岗位");
				return; //
			} else {
				str_postid = billVO_Post.getStringValue("id"); // 岗位ID
				str_postname = billVO_Post.getStringValue("name"); // 岗位ID
			}
		}

		BillListDialog dialog = new BillListDialog(this, "选择人员",
				"PUB_USER_ICN", 700, 500); //
		dialog.getBilllistPanel().setAllBillListBtnVisiable(false);
		dialog.getBilllistPanel().setItemEditable(false); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return;
		}

		BillVO[] billVOS = dialog.getReturnBillVOs(); //
		String str_userid = billVOS[0].getStringValue("id"); //
		String str_username = billVOS[0].getStringValue("name"); //

		boolean bo_checked = checkUserDeptPost(str_currdeptid,
				str_currdeptname, str_postid, str_postname, str_userid,
				str_username); // 判断该人员是否在此机构和岗位下，如果在，就不用导入了
		if (!bo_checked) {
			return;
		}

		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null,
				"select deptname,postname from v_pub_user_post_1 where userid='"
						+ str_userid + "'"); //
		if (hvs.length == 0) {
			if (JOptionPane.showConfirmDialog(this, "确定要把【" + str_username
					+ "】导入到【" + str_currdeptname + "】中来吗?", "提示",
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			String str_newpk = UIUtil.getSequenceNextValByDS(null,
					"S_PUB_USER_POST"); //
			String str_sql = getSQL_User_Post(str_newpk, str_userid,
					str_currdeptid, str_postid);
			// 增加对pub_user pk_dept 的同步处理 Gwang 2012-4-17
			String sql = "update pub_user set pk_dept = " + str_currdeptid
					+ " where id = " + str_userid;
			UIUtil.executeBatchByDS(null, new String[] { str_sql, sql });
			billListPanel_User_Post.addBillVOs(billListPanel_User_Post
					.queryBillVOsByCondition("id='" + str_newpk + " '")); //
			remove_user_link_account(str_userid);
			MessageBox.show(this, "导入成功!"); 
			//zzl 增加调整记录
			HashVO [] vo=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
						+ str_userid + "'");
			String []data={"type;新增",
					"old_deptid;"+vo[0].getStringValue("deptid"),
					"old_deptname;"+vo[0].getStringValue("deptname"),
					"old_postid;"+vo[0].getStringValue("postid"),
					"old_postname;"+vo[0].getStringValue("postname"),
					"old_userid;"+vo[0].getStringValue("userid"),
					"old_username;"+vo[0].getStringValue("username"),
					"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
					"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
					"datetime;"+UIUtil.getCurrTime()};
			UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
		} else {
			String str_alreadyDeptNames = "";
			for (int i = 0; i < hvs.length; i++) {
				str_alreadyDeptNames = str_alreadyDeptNames
						+ "机构【"
						+ hvs[i].getStringValue("deptname")
						+ "】,岗位【"
						+ (hvs[i].getStringValue("postname") == null ? ""
								: hvs[i].getStringValue("postname")) + "】\r\n";
			}

			int li_result = -1; //
			if (str_postid != null) {
				li_result = JOptionPane.showOptionDialog(this, "该人员当前所在:\r\n"
						+ str_alreadyDeptNames + "您希望将该人员如何操作?", //
						"提示", JOptionPane.DEFAULT_OPTION, //
						JOptionPane.QUESTION_MESSAGE, null, //
						new String[] { "迁移到本机构岗位", "复制到本机构岗位", "取消" }, //
						"取消"); //
			} else {
				li_result = JOptionPane.showOptionDialog(this, "该人员当前所在:\r\n"
						+ str_alreadyDeptNames + "您希望将该人员如何操作?", //
						"提示", JOptionPane.DEFAULT_OPTION, //
						JOptionPane.QUESTION_MESSAGE, null, //
						new String[] { "迁移到本机构", "复制到本机构", "取消" }, //
						"取消"); //
			}

			if (li_result == 0) { // 迁移到本机构, (删除所有人员和岗位关系, 重新新增一条人员和岗位的对应关系,
									// 并设置为默认岗位 Gwang 2013-07-30)
				// zzl[2017-10-30]增加记录所有岗位调动记录
				HashVO [] vo=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
						+ str_userid + "'");
				String str_newpk = UIUtil.getSequenceNextValByDS(null,
						"S_PUB_USER_POST"); //
				String str_sql = getSQL_User_Post(str_newpk, str_userid,
						str_currdeptid, str_postid);
				// 增加对pub_user pk_dept 的同步处理 Gwang 2012-4-17
				String sql = "update pub_user set pk_dept = " + str_currdeptid
						+ " where id = " + str_userid;
				UIUtil.executeBatchByDS(null, new String[] {
						"delete from pub_user_post where userid=" + str_userid,
						str_sql, sql }); //
				billListPanel_User_Post.addBillVOs(billListPanel_User_Post
						.queryBillVOsByCondition("id='" + str_newpk + " '")); //
				HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
						+ str_userid + "'");
			    String []data={"type;迁移",
						"old_deptid;"+vo[0].getStringValue("deptid"),
						"old_deptname;"+vo[0].getStringValue("deptname"),
						"old_postid;"+vo[0].getStringValue("postid"),
						"old_postname;"+vo[0].getStringValue("postname"),
						"old_userid;"+vo[0].getStringValue("userid"),
						"old_username;"+vo[0].getStringValue("username"),
						"deptid;"+voh[0].getStringValue("deptid"),
						"deptname;"+voh[0].getStringValue("deptname"),
						"postid;"+voh[0].getStringValue("postid"),
						"postname;"+voh[0].getStringValue("postname"),
						"userid;"+voh[0].getStringValue("userid"),
						"username;"+voh[0].getStringValue("username"),
						"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
						"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
						"datetime;"+UIUtil.getCurrTime()};
				UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
			} else if (li_result == 1) { // 复制到本机构(新增人员和岗位的对应关系, 原来的默认岗位不变 Gwang
											// 2013-07-30)
				// zzl[2017-10-30]增加记录所有岗位调动记录
				HashVO [] vo=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
						+ str_userid + "'");
				String str_newpk = UIUtil.getSequenceNextValByDS(null,
						"S_PUB_USER_POST"); //
				String str_sql = getSQL_User_Post(str_newpk, str_userid,
						str_currdeptid, str_postid);
				String sql_update = "update pub_user_post set isdefault = null where id="
						+ str_newpk;

				UIUtil.executeBatchByDS(null, new String[] { str_sql,
						sql_update }); //
				billListPanel_User_Post.addBillVOs(billListPanel_User_Post
						.queryBillVOsByCondition("id='" + str_newpk + " '")); //
				HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where deptid='"
						+ str_currdeptid + "'");
			    String []data={"type;兼职",
						"old_deptid;"+vo[0].getStringValue("deptid"),
						"old_deptname;"+vo[0].getStringValue("deptname"),
						"old_postid;"+vo[0].getStringValue("postid"),
						"old_postname;"+vo[0].getStringValue("postname"),
						"old_userid;"+vo[0].getStringValue("userid"),
						"old_username;"+vo[0].getStringValue("username"),
						"deptid;"+voh[0].getStringValue("deptid"),
						"deptname;"+voh[0].getStringValue("deptname"),
						"postid;"+voh[0].getStringValue("postid"),
						"postname;"+voh[0].getStringValue("postname"),
						"userid;"+voh[0].getStringValue("userid"),
						"username;"+voh[0].getStringValue("username"),
						"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
						"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
						"datetime;"+UIUtil.getCurrTime()};
				UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
			}
			checkandcreateduties(str_userid);
			remove_user_link_account(str_userid);
		}
	}

	/**
	 * 导入人员的sql
	 * 
	 * @param str_newpk
	 *            人员岗位关系表id
	 * @param str_userid
	 *            人员id
	 * @param str_currdeptid2
	 *            当前机构id
	 * @param str_postid
	 *            岗位id
	 * @return
	 */
	private String getSQL_User_Post(String str_newpk, String str_userid,
			String str_currdeptid2, String str_postid) {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_user_post");
		isql.putFieldValue("id", str_newpk);
		isql.putFieldValue("userid", str_userid);
		isql.putFieldValue("postid", str_postid);
		isql.putFieldValue("userdept", str_currdeptid);
		isql.putFieldValue("isdefault", "Y");
		return isql.toString();
	}

	/**
	 * 
	 * @param str_newpk
	 * @param str_userid
	 * @param str_currdeptid2
	 * @param str_postid
	 * @return 记录岗位调动的sql zzl【2017-10-30】
	 */
	private String getSQL_record_Post(String [] data) {
		InsertSQLBuilder isql = new InsertSQLBuilder("hr_recordpost");
		try {
			isql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_HR_RECORDPOST"));
			for(int i=0;i<data.length;i++){
				String [] str=data[i].toString().split(";");
				isql.putFieldValue(str[0].toString(),str[1].toString());
			}
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		isql.putFieldValue("id", str_newpk);
//		isql.putFieldValue("deptid", deptid);
//		isql.putFieldValue("deptname", deptname);
//		isql.putFieldValue("postid", postid);
//		isql.putFieldValue("postname", postname);
//		isql.putFieldValue("userid", userid);
//		isql.putFieldValue("usercode", usercode);
//		isql.putFieldValue("username", username);
//		isql.putFieldValue("datetime", date);
//		isql.putFieldValue("type", date);//调整类型
//		isql.putFieldValue("old_deptid", deptid);
//		isql.putFieldValue("old_deptname", deptname);
//		isql.putFieldValue("old_postid", postid);
//		isql.putFieldValue("old_postname", postname);
//		isql.putFieldValue("old_userid", userid);
//		isql.putFieldValue("old_usercode", usercode);
//		isql.putFieldValue("old_username", username);
//		isql.putFieldValue("tzusercode", username);
//		isql.putFieldValue("tzdeptid", username);
		return isql.toString();
	}

	/**
	 * 新增人员
	 */
	private void onAddUser() throws Exception {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}

		String str_postid = null; //
		if (billListPanel_Post != null) {// 是否有岗位列表，如果有才提示
			BillVO billVO_Post = billListPanel_Post.getSelectedBillVO(); //
			if (billVO_Post == null) {
				MessageBox.show(this, "请选择一个岗位");
				return;
			} else {
				str_postid = billVO_Post.getStringValue("id"); // 岗位ID
			}
		}

		AddOrEditUserDialog dialog = new AddOrEditUserDialog(this, "", null,
				null, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.addInit();
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_newid = dialog.getReturnNewID(); //
			BillVO postvos[] = billListPanel_User_Post
					.queryBillVOsByCondition("id='" + str_newid + " '");
			billListPanel_User_Post.addBillVOs(postvos); //
			checkandcreateduties(dialog.getBillcardPanel().getValueAt("id")
					.toString());
			if (postvos != null && postvos.length > 0) {
				adduser_link_account(postvos[0].getStringValue("userid"));
			}
		    // zzl[2017-10-30]增加记录所有岗位调动记录
			HashVO [] vo=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where postid='"
					+ str_postid + "'");
		    String []data={"type;新增",
				"old_deptid;"+vo[0].getStringValue("deptid"),
				"old_deptname;"+vo[0].getStringValue("deptname"),
				"old_postid;"+vo[0].getStringValue("postid"),
				"old_postname;"+vo[0].getStringValue("postname"),
				"old_userid;"+vo[0].getStringValue("userid"),
				"old_username;"+vo[0].getStringValue("username"),
				"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
				"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
				"datetime;"+UIUtil.getCurrTime()};
		    UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
		}

	}

	/*
	 * 判断并执行新增人员帐套关系
	 */
	private void adduser_link_account(String _userid) {
		try {
			String username = UIUtil.getStringValueByDS(null,
					"select name from pub_user where id = " + _userid);
			HashVO vos[] = UIUtil.getHashVoArrayByDS(null,
					"select * from sal_account_set where isdefault='Y'");
			if (vos.length == 0) {
				MessageBox.show(this,
						"系统没有发现默认薪酬帐套，请在工资薪酬定义功能设定默认值,并手工把此人员添加到人员帐套中.");
			} else {
				String name = vos[0].getStringValue("name");
				String accountid = vos[0].getStringValue("id");
				if (MessageBox.confirm(this, "是否将【" + username + "】绑定到薪酬帐套【"
						+ name + "】?")) {
					InsertSQLBuilder insert = new InsertSQLBuilder(
							"sal_account_personinfo");
					insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(
							null, "S_SAL_ACCOUNT_PERSONINFO"));
					insert.putFieldValue("accountid", accountid);
					insert.putFieldValue("personinfoid", _userid);
					UIUtil.executeBatchByDS(null, new String[] { insert
							.getSQL() });
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 判断并执行移除人员帐套关系
	 */
	private void remove_user_link_account(String str_userid) {
		TBUtil tbutil = new TBUtil();
		String value = tbutil.getSysOptionStringValue("不参加薪酬计算的具体岗位",
				"离职;内退;离职");
		if (TBUtil.getTBUtil().isEmpty(value)) {
			return;
		}
		String[] dontJoinMoneyPosts = tbutil.split(value, ";"); // 不参加计算薪酬的岗位
		try {
			HashVO[] posts = UIUtil.getHashVoArrayByDS(null,
					"select username,postname from v_pub_user_post_1 where userid = "
							+ str_userid);
			boolean flag = false;
			String post = "";
			String name = "";
			for (int i = 0; i < posts.length; i++) {
				for (int j = 0; j < dontJoinMoneyPosts.length; j++) {
					if (dontJoinMoneyPosts[j] != null
							&& dontJoinMoneyPosts[j].trim().equals(
									posts[i].getStringValue("postname", "")
											.trim())) {
						flag = true;
						post = dontJoinMoneyPosts[j];
						name = posts[i].getStringValue("username", "");
						break;
					}
				}
			}
			if (flag) {
				if (MessageBox.confirm(this, "是否解除【" + name + "】与薪酬台帐的关系?")) {
					String delsql = "delete from sal_account_personinfo where personinfoid ="
							+ str_userid;
					UIUtil.executeBatchByDS(null, new String[] { delsql });
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 维护页签，修改人员信息
	 */
	private void onUpdateUser() throws Exception {

		BillVO billVO = billListPanel_User_Post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.showSelectOne(billListPanel_User_Post);
			return; //
		}

		String str_id = billVO.getStringValue("id"); //  
		String str_userid = billVO.getStringValue("userid"); // 

		AddOrEditUserDialog editDialog = new AddOrEditUserDialog(this,
				str_userid, billVO.getStringValue("deptid"), billVO
						.getStringValue("postid"),
				WLTConstants.BILLDATAEDITSTATE_UPDATE);
		String oldposition = editDialog.getBillcardPanel().getBillVO()
				.getStringValue("position");
		editDialog.setVisible(true);
		if (editDialog.getCloseType() == 1) {
			billListPanel_User_Post.setBillVOAt(billListPanel_User_Post
					.getSelectedRow(), billListPanel_User_Post
					.queryBillVOsByCondition("id='" + str_id + "'")[0]);
			editDialog.getBillcardPanel().updateData();
			BillVO newuservo = editDialog.getBillcardPanel().getBillVO();
			// 如果职务发生变化 ,提示是否加履职
			if (!newuservo.getStringValue("position", "").equals(oldposition)) {
				if (MessageBox.confirm(this, newuservo.getStringValue("name")
						+ "的职务从[" + oldposition + "]修改为["
						+ newuservo.getStringValue("position", "")
						+ "],是否更新其履职信息?")) {
					checkandcreateduties(newuservo.getStringValue("id"));
				}
			}

		}
	}

	/**
	 * 迁移人员
	 */
	private void onTransUser() {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}

		BillVO billVO = billListPanel_User_Post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return; //
		}

		String str_id = billVO.getStringValue("id"); //
		String str_userid = billVO.getStringValue("userid"); //
		String str_username = billVO.getStringValue("username"); //
		try {
			HashVO [] vo=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
						+ str_userid + "'");
			TransUserDialog dialog = new TransUserDialog(this, str_id,
					str_currdeptid, str_currdeptname, str_userid, str_username,
					getGranuType(), isFilter());
			dialog.setVisible(true); //
			
			if (dialog.getCloseType() == 1) {
				// 刷新一把
				if (billListPanel_User_Post != null) {
					billListPanel_User_Post.queryDataByCondition("deptid='"
							+ str_currdeptid + "'", "seq,userseq"); //
				}
	
				if (billListPanel_User_Role != null) {// 如果有角色列表才清空列表
					billListPanel_User_Role.clearTable();
				}
				checkandcreateduties(str_userid);
				remove_user_link_account(str_userid);
			}
			HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where userid='"
					+ str_userid + "'");
		    String []data={"type;迁移",
					"old_deptid;"+vo[0].getStringValue("deptid"),
					"old_deptname;"+vo[0].getStringValue("deptname"),
					"old_postid;"+vo[0].getStringValue("postid"),
					"old_postname;"+vo[0].getStringValue("postname"),
					"old_userid;"+vo[0].getStringValue("userid"),
					"old_username;"+vo[0].getStringValue("username"),
					"deptid;"+voh[0].getStringValue("deptid"),
					"deptname;"+voh[0].getStringValue("deptname"),
					"postid;"+voh[0].getStringValue("postid"),
					"postname;"+voh[0].getStringValue("postname"),
					"userid;"+voh[0].getStringValue("userid"),
					"username;"+voh[0].getStringValue("username"),
					"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
					"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
					"datetime;"+UIUtil.getCurrTime()};
			UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * 检查该人员是否需要建立履职数据。如果需要，就建立一条。
	 */
	private void checkandcreateduties(String userid) {
		try {
			HashVO[] hvo = UIUtil.getHashVoArrayByDS(null,
					"select *from v_sal_personinfo where id = " + userid);
			HashVO[] duties = UIUtil.getHashVoArrayByDS(null,
					"select * from sal_personduties_his where userid ='"
							+ userid + "' order by startdate desc");
			if (hvo.length == 0) {
				return;
			}
			HashVO personVO = hvo[0];
			String currDate = UIUtil.getServerCurrDate(); // 得到当前日期
			List sqlList = new ArrayList();
			if (duties.length == 0) { // 如果没有该人员的,判断该人员是不是新员工
				String joinselfbankdate = personVO
						.getStringValue("joinselfbankdate"); // 取得该人员入行时间.
				int joindays = compareTwoDate(currDate, joinselfbankdate); // 入行时间
				InsertSQLBuilder insert = new InsertSQLBuilder(
						"SAL_PERSONDUTIES_HIS");
				insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(null,
						"S_SAL_PERSONDUTIES_HIS"));
				insert.putFieldValue("username", personVO
						.getStringValue("name"));
				insert.putFieldValue("userid", userid);
				insert.putFieldValue("postid", personVO
						.getStringValue("mainstationid"));
				String postdescr = personVO.getStringValue("mainstation");
				if (!TBUtil.isEmpty(personVO.getStringValue("position"))) {
					postdescr = personVO.getStringValue("position", "");
				}
				insert.putFieldValue("postname", postdescr);
				insert.putFieldValue("deptid", personVO
						.getStringValue("deptid"));
				insert.putFieldValue("deptname", personVO
						.getStringValue("deptname"));
				insert.putFieldValue("createuser", ClientEnvironment
						.getCurrSessionVO().getLoginUserName());
				insert.putFieldValue("createdate", currDate);
				insert.putFieldValue("startdate",
						(joindays < 10 && joindays >= 0) ? joinselfbankdate
								: currDate);// 新入职的取入行时间.
				sqlList.add(insert);
				// }
			} else { // 如果有历史记录
				String lastupdateDate = duties[0].getStringValue("createdate");
				if (currDate.equals(lastupdateDate)) {// 如果是今天改的。直接更新原始记录即可。
					UpdateSQLBuilder update = new UpdateSQLBuilder(
							"SAL_PERSONDUTIES_HIS");
					update.setWhereCondition(" id = "
							+ duties[0].getStringValue("id"));
					update.putFieldValue("postid", personVO
							.getStringValue("mainstationid"));
					String postdescr = personVO.getStringValue("mainstation");
					if (!TBUtil.isEmpty(personVO.getStringValue("position"))) {
						postdescr = personVO.getStringValue("position", "");
					}
					update.putFieldValue("postname", postdescr);
					update.putFieldValue("deptid", personVO
							.getStringValue("deptid"));
					update.putFieldValue("deptname", personVO
							.getStringValue("deptname"));
					sqlList.add(update);
				} else if (!personVO.getStringValue("mainstationid").equals(
						duties[0].getStringValue("postid"))) { // 如果不是今天改的。
																// 判断原有的最后一条，和现在的职位是否一样。
					// 如果变了职位。那么新增一条记录，并且把旧的记录enddate改了
					InsertSQLBuilder insert = new InsertSQLBuilder(
							"SAL_PERSONDUTIES_HIS");
					insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(
							null, "S_SAL_PERSONDUTIES_HIS"));
					insert.putFieldValue("username", personVO
							.getStringValue("name"));
					insert.putFieldValue("userid", userid);
					insert.putFieldValue("postid", personVO
							.getStringValue("mainstationid"));
					String postdescr = personVO.getStringValue("mainstation");
					if (!TBUtil.isEmpty(personVO.getStringValue("position"))) {
						postdescr = personVO.getStringValue("position", "");
					}
					insert.putFieldValue("postname", postdescr);
					insert.putFieldValue("deptid", personVO
							.getStringValue("deptid"));
					insert.putFieldValue("deptname", personVO
							.getStringValue("deptname"));
					insert.putFieldValue("createuser", ClientEnvironment
							.getCurrSessionVO().getLoginUserName());
					insert.putFieldValue("createdate", currDate);
					insert.putFieldValue("startdate", currDate);
					sqlList.add(insert);
					UpdateSQLBuilder update = new UpdateSQLBuilder(
							"SAL_PERSONDUTIES_HIS");
					update.setWhereCondition(" id = "
							+ duties[0].getStringValue("id"));
					update.putFieldValue("enddate", currDate);// 更新前一条记录。
					update.putFieldValue("createuser", ClientEnvironment
							.getCurrSessionVO().getLoginUserName());
					sqlList.add(update);
				}
			}
			if (sqlList.size() > 0) {
				UpdateSQLBuilder sql = new UpdateSQLBuilder("sal_personinfo");
				sql.putFieldValue("stationdate", currDate);
				sql.setWhereCondition("id = " + personVO.getStringValue("id"));
				sqlList.add(sql);
				UIUtil.executeBatchByDS(null, sqlList); // 执行数据
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int compareTwoDate(String firstDate, String secondDate)
			throws Exception {
		if (TBUtil.isEmpty(secondDate) || TBUtil.isEmpty(firstDate)) {
			return -1;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = sdf.parse(firstDate);
		Date date2 = sdf.parse(secondDate);
		long mills = date1.getTime() - date2.getTime();
		return (int) (mills / (long) (1000 * 60 * 60 * 24));
	}

	/**
	 * 删除人员
	 * 
	 * @throws Exception
	 */
	private void onDelUser() throws Exception {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}

		BillVO billVO = billListPanel_User_Post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return; //
		}

		String str_userid = billVO.getStringValue("userid"); //
		String str_usercode=billVO.getStringValue("usercode");
		

		int li_result = JOptionPane.showOptionDialog(this, "您想如何操作?", // 
				"提示", //
				JOptionPane.DEFAULT_OPTION, //
				JOptionPane.QUESTION_MESSAGE, null, new String[] {
						"移出【" + str_currdeptname + "】", "彻底删除人员", "取消" }, "取消");

		// 移出人员, 比较复杂， 要处理是否是默认岗位， 是否有多个岗位， Gwang 2013-07-30
		if (li_result == 0) {
			String isDefault = billVO.getStringValue("isdefault");
			String sql[] = null;
			String sql_update = "";
			String sql_del = "delete from pub_user_post where id="
					+ billVO.getStringValue("id");
			if ("Y".equals(isDefault)) { // 移出的是主要岗位
				String otherDefaultDeptID = this.getOtherDefaultDeptID(
						str_userid, str_currdeptid);
				if (otherDefaultDeptID.equals("")) { // 此人只有一个岗位
					HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where usercode='"
							+ str_usercode + "'");
				    String []data={"type;移出",
							"deptid;"+voh[0].getStringValue("deptid"),
							"deptname;"+voh[0].getStringValue("deptname"),
							"postid;"+voh[0].getStringValue("postid"),
							"postname;"+voh[0].getStringValue("postname"),
							"userid;"+voh[0].getStringValue("userid"),
							"username;"+voh[0].getStringValue("username"),
							"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
							"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
							"datetime;"+UIUtil.getCurrTime()};
					UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
					sql_update = "update pub_user set pk_dept = null where id = "
							+ str_userid;
					sql = new String[] { sql_del, sql_update };
				} else { // 如果有多个岗位
					HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where deptid='"
							+ str_currdeptid + "'");
				    String []data={"type;移出",
							"deptid;"+voh[0].getStringValue("deptid"),
							"deptname;"+voh[0].getStringValue("deptname"),
							"postid;"+voh[0].getStringValue("postid"),
							"postname;"+voh[0].getStringValue("postname"),
							"userid;"+voh[0].getStringValue("userid"),
							"username;"+voh[0].getStringValue("username"),
							"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
							"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
							"datetime;"+UIUtil.getCurrTime()};
					UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
					String pk = "";
					if (otherDefaultDeptID.equals("many")) { // 有多于2个岗位,
																// 让用户选择新的主要岗位;
						BillListDialog dialog = new BillListDialog(this,
								"请选择新的主要岗位", "PUB_USER_POST_DEFAULT", 700, 500); //
						dialog.getBilllistPanel().setAllBillListBtnVisiable(
								false);
						dialog.getBilllistPanel().setItemEditable(false);
						dialog.getBilllistPanel().QueryDataByCondition(
								"userid = " + str_userid + " and deptid <> "
										+ str_currdeptid);
						dialog.setVisible(true); //
						if (dialog.getCloseType() != 1) {
							return;
						}
						BillVO returnVO = dialog.getReturnBillVOs()[0];
						pk = returnVO.getStringValue("id");
						otherDefaultDeptID = returnVO.getStringValue("deptid");
					} else { // 此人有2个岗位, 自动将另一个设为主要岗位;
						pk = otherDefaultDeptID.split(",")[0];
						otherDefaultDeptID = otherDefaultDeptID.split(",")[1];
					}
					sql_update = "update pub_user set pk_dept = "
							+ otherDefaultDeptID + " where id = " + str_userid;
					String sql_update2 = "update pub_user_post set isdefault = 'Y' where id = "
							+ pk;
					sql = new String[] { sql_del, sql_update, sql_update2 };
				}

			} else {
				HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where usercode='"
						+ str_usercode + "'");
			    String []data={"type;移出",
						"deptid;"+voh[0].getStringValue("deptid"),
						"deptname;"+voh[0].getStringValue("deptname"),
						"postid;"+voh[0].getStringValue("postid"),
						"postname;"+voh[0].getStringValue("postname"),
						"userid;"+voh[0].getStringValue("userid"),
						"username;"+voh[0].getStringValue("username"),
						"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
						"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
						"datetime;"+UIUtil.getCurrTime()};
				UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
				sql = new String[] { sql_del };
			}
			// for (String s : sql) {
			// System.out.println(s);
			// }

			UIUtil.executeBatchByDS(null, sql);

			// //追加默认部门更改【杨科/2013-05-20】
			// String sql_update = "";
			// HashVO[] hvos_user_post = UIUtil.getHashVoArrayByDS(null,
			// "select * from pub_user_post where userid='" + str_userid +
			// "' and userdept<>'" + str_currdeptid + "' order by isdefault");
			// if(hvos_user_post.length>0){
			// if(!hvos_user_post[0].getStringValue("isdefault",
			// "").equals("Y")){
			// if(!hvos_user_post[0].getStringValue("id", "").equals("")){
			// sql_update =
			// "update pub_user_post set isdefault = 'Y' where id = " +
			// hvos_user_post[0].getStringValue("id", "");
			// }
			// }
			// }
			//			
			// String str_sql = "delete from pub_user_post where id='" +
			// billVO.getStringValue("id") + "'"; //
			// //增加对pub_user pk_dept 的同步处理 Gwang 2012-4-17
			// String sql = "update pub_user set pk_dept = null where id = " +
			// str_userid + " and pk_dept = " + str_currdeptid;
			// if(sql_update.equals("")){
			// UIUtil.executeBatchByDS(null, new String[] { str_sql, sql });
			// }else{
			// UIUtil.executeBatchByDS(null, new String[] { str_sql, sql,
			// sql_update });
			// }
			billListPanel_User_Post.removeSelectedRow(); //
			if (billListPanel_User_Role != null) {// 如果有角色列表才清空列表
				billListPanel_User_Role.clearTable();
			}
		} else if (li_result == 1) { // 彻底删除人员
			ArrayList al_sqls = new ArrayList(); //
			HashVO [] voh=UIUtil.getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where usercode='"
					+ str_usercode + "'");
		    String []data={"type;删除",
					"deptid;"+voh[0].getStringValue("deptid"),
					"deptname;"+voh[0].getStringValue("deptname"),
					"postid;"+voh[0].getStringValue("postid"),
					"postname;"+voh[0].getStringValue("postname"),
					"userid;"+voh[0].getStringValue("userid"),
					"username;"+voh[0].getStringValue("username"),
					"tzusercode;"+ClientEnvironment.getInstance().getLoginUserCode(),
					"tzdeptid;"+ClientEnvironment.getInstance().getLoginUserDeptId(),
					"datetime;"+UIUtil.getCurrTime()};
			UIUtil.executeUpdateByDS(null, getSQL_record_Post(data));
			al_sqls.add("delete from pub_user_post where userid='" + str_userid
					+ "'"); //
			al_sqls.add("delete from pub_user_role where userid='" + str_userid
					+ "'"); //
			al_sqls.add("delete from pub_user_menu where userid='" + str_userid
					+ "'"); //
			al_sqls.add("delete from pub_user where id='" + str_userid
					+ "'"); //
			al_sqls.add("delete from sal_personinfo where id='"
					+ str_userid + "'"); // 把人员详细信息也删除掉。
			al_sqls
					.add("delete from sal_account_personinfo where personinfoid ='"
							+ str_userid + "'"); // 把人员详细信息也删除掉。
			UIUtil.executeBatchByDS(null, al_sqls); //
			billListPanel_User_Post.removeSelectedRow(); //
			if (billListPanel_User_Role != null) {// 如果有角色列表才清空列表
				billListPanel_User_Role.clearTable();
			}
		}
	}

	/**
	 * 得到人员其他岗位情况
	 * 
	 * @param userID
	 * @param deptID
	 * @return
	 */
	private String getOtherDefaultDeptID(String userID, String deptID) {
		String ret = null;
		String sql = "select id, userdept from pub_user_post where userid = "
				+ userID + " and userdept <> " + deptID;
		try {
			String[][] vals = UIUtil.getStringArrayByDS(null, sql);
			if (vals.length == 0) {
				ret = "";
			} else if (vals.length == 1) {
				ret = vals[0][0] + "," + vals[0][1];
			} else {
				ret = "many";
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 人员排序
	 */
	private void onSeqUser() {
		SeqListDialog dialog_user = new SeqListDialog(this, "人员排序",
				billListPanel_User_Post.getTempletVO(), billListPanel_User_Post
						.getAllBillVOs());
		dialog_user.setVisible(true);
		if (dialog_user.getCloseType() == 1) {// 如果点击确定返回，则需要刷新一下页面
			billListPanel_User_Post.queryDataByCondition("deptid='"
					+ str_currdeptid + "'", "seq"); //
		}
	}

	/**
	 * 查看人员兼职信息
	 */
	private void onLookUserDeptInfos(int _type) throws Exception {
		BillVO billVO = null;
		String userID = null; //
		String str_name = null; //
		if (_type == 1) {// 人员维护里的查看
			billVO = billListPanel_User_Post.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.show(this, MSG_NEED_USER); //
				return;
			}
			userID = billVO.getStringValue("userid"); //
			str_name = billVO.getStringValue("username"); //
		} else {// 扁平查看里的查看
			billVO = billListPanel_user_flat.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.show(this, MSG_NEED_USER); //
				return;
			}
			userID = billVO.getStringValue("id"); //
			str_name = billVO.getStringValue("name"); //
		}

		String str_sql_1 = "select deptcode,deptname,postcode,postname,isdefault from v_pub_user_post_1 where userid="
				+ userID + " order by isdefault desc";
		String str_sql_2 = "select rolecode,rolename from v_pub_user_role_1 where userid="
				+ userID; //
		Vector v_return = UIUtil.getHashVoArrayReturnVectorByMark(null,
				new String[] { str_sql_1, str_sql_2 }); //
		HashVO[] hsv_userdepts = (HashVO[]) v_return.get(0); //
		HashVO[] hsv_userroles = (HashVO[]) v_return.get(1); //

		StringBuilder sb_msg = new StringBuilder(); //
		sb_msg.append("【" + str_name + "】所有岗位信息:\r\n"); //

		for (int i = 0; i < hsv_userdepts.length; i++) {
			// sb_msg.append("" + (i + 1) + ")机构编码/名称=[" +
			// (hsv_userdepts[i].getStringValue("deptcode") == null ? "" :
			// hsv_userdepts[i].getStringValue("deptcode")));
			// sb_msg.append("/" + (hsv_userdepts[i].getStringValue("deptname")
			// == null ? "" : hsv_userdepts[i].getStringValue("deptname")) +
			// "],");
			// sb_msg.append("岗位编码/名称=[" +
			// (hsv_userdepts[i].getStringValue("postcode") == null ? "" :
			// hsv_userdepts[i].getStringValue("postcode")));
			// sb_msg.append("/" + (hsv_userdepts[i].getStringValue("postname")
			// == null ? "" : hsv_userdepts[i].getStringValue("postname")) +
			// "]\r\n"); //
			sb_msg
					.append(""
							+ (i + 1)
							+ ")"
							+ (hsv_userdepts[i].getStringValue("isdefault") == null ? ""
									: "[主要岗位] "));
			sb_msg.append(""
					+ (hsv_userdepts[i].getStringValue("deptname") == null ? ""
							: hsv_userdepts[i].getStringValue("deptname"))
					+ "--");
			sb_msg.append(""
					+ (hsv_userdepts[i].getStringValue("postname") == null ? ""
							: hsv_userdepts[i].getStringValue("postname"))
					+ "\r\n"); //			
		}

		sb_msg.append("\r\n\r\n"); //

		sb_msg.append("【" + str_name + "】所有角色信息:\r\n"); //
		for (int i = 0; i < hsv_userroles.length; i++) {
			// sb_msg.append("" + (i + 1) + ")角色编码/名称=[" +
			// hsv_userroles[i].getStringValue("rolecode") + "/" +
			// hsv_userroles[i].getStringValue("rolename") + "]\r\n"); //
			sb_msg.append("" + (i + 1) + ")"
					+ hsv_userroles[i].getStringValue("rolename") + "\r\n");
		}
		MessageBox.showTextArea(this, "【" + str_name + "】", sb_msg.toString(),
				600, 300);
	}

	/**
	 * 增加角色
	 */
	private void onAddRole() throws Exception {
		if (str_currdeptid == null) {
			MessageBox.show(this, MSG_NEED_DEPT); //
			return;
		}

		BillVO billVO = billListPanel_User_Post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return; //
		}

		String str_userid = billVO.getStringValue("userid"); //

		BillListDialog dialog = new BillListDialog(this, "请选择一个角色",
				"PUB_ROLE_1", 700, 500); //
		dialog.getBilllistPanel().setItemEditable(false); //
		dialog.getBilllistPanel().QueryDataByCondition(null); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return;
		}
		// 袁江晓 20120917 添加 解决能批量添加的功能 begin
		BillVO[] billVOS = dialog.getReturnBillVOs(); //
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null,
				"select userid,roleid,userdept from pub_user_role where userid='"
						+ str_userid + "' and userdept='" + str_currdeptid
						+ "'"); // 先获得由userid和userdept组合查询的数据
		String str_roles = ";";// 设置分号主要是为了不让第一个添加的和后面的重复，否则在判断Indexof的时候会判断不出来
		for (int j = 0; j < hvs.length; j++) { // 先查询出之前已经具有的roleid，如果有则跳过，如果没有则插入该roleid
			String old_roleid = hvs[j].getStringValue("roleid");
			str_roles += old_roleid + ";";
		}
		for (int i = 0; i < billVOS.length; i++) {
			String str_roleid = billVOS[i].getStringValue("id"); // 获得id
			String str_rolename = billVOS[i].getStringValue("name"); // 获得name
			if (str_roles.indexOf(str_roleid) > 0) {// 表示新添加的该roleid已经存在在之前添加的权限id中
				continue;
			} else {
				String str_newpk = UIUtil.getSequenceNextValByDS(null,
						"S_PUB_USER_ROLE"); //
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("insert into pub_user_role");
				sb_sql.append("(");
				sb_sql.append("id,"); // id (id)
				sb_sql.append("userid,"); // userid (userid)
				sb_sql.append("roleid,"); // roleid (roleid)
				sb_sql.append("userdept"); // userdept (userdept)
				sb_sql.append(")");
				sb_sql.append(" values ");
				sb_sql.append("(");
				sb_sql.append(str_newpk + ","); // id (id)
				sb_sql.append("'" + str_userid + "',"); // userid (userid)
				sb_sql.append("'" + str_roleid + "',"); // roleid (roleid)
				sb_sql.append("'" + str_currdeptid + "'"); // userdept
															// (userdept)
				sb_sql.append(")");
				UIUtil.executeUpdateByDS(null, sb_sql.toString()); // //
			}
		}
		MessageBox.show(this, "角色增加成功!"); //
		billListPanel_User_Role.QueryDataByCondition("userid='" + str_userid
				+ "'"); // 刷新列表
		// 袁江晓 20120917 添加 解决能批量添加的功能 end
	}

	/**
	 * 删除角色
	 * 
	 * @throws Exception
	 */
	private void onDelRole() throws Exception {
		BillVO billVO = billListPanel_User_Post.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return; //
		}

		BillVO[] billVOS = billListPanel_User_Role.getSelectedBillVOs(); //
		if (billVOS == null || billVOS.length == 0) {
			MessageBox.show(this, "请选择一个角色!"); //
			return;
		}

		// String str_rolenames = "";
		// ArrayList al_sql = new ArrayList(); //
		// for (int i = 0; i < billVOS.length; i++) {
		// String str_id = billVOS[i].getStringValue("id"); //
		// String str_rolename = billVOS[i].getStringValue("rolename"); //
		// str_rolenames += str_rolename + ",";
		// al_sql.add("delete from pub_user_role where id='" + str_id + "'");
		// }
		// str_rolenames = str_rolenames.substring(0, str_rolenames.length()-1);

		// 改为单个角色删除(为了和其它按钮统一) Gwang 2012-4-17
		String rolename = billVOS[0].getStringValue("rolename");
		String roleid = billVOS[0].getStringValue("id");
		String sql = "delete from pub_user_role where id='" + roleid + "'";
		if (JOptionPane.showConfirmDialog(this, "确定要把角色【" + rolename + "】删除吗?",
				"提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		UIUtil.executeUpdateByDS(null, sql);
		billListPanel_User_Role.removeSelectedRow();
		if (billListPanel_User_Role.getRowCount() > 1) {// 这里需要判断一下，如果就只有一个角色，那删除后就没有角色可以选择了【李春娟/2012-05-24】
			billListPanel_User_Role.getTable().removeRowSelectionInterval(0,
					billListPanel_User_Role.getRowCount() - 1);
		}
		MessageBox.show(this, "角色删除成功!"); //
	}

	/**
	 * 判断人员是否在此机构下，如果不在才导入
	 * 
	 * @param _deptid
	 *            机构id
	 * @param _deptName
	 *            机构名称
	 * @param _postid
	 *            岗位id
	 * @param _postName
	 *            岗位名称
	 * @param _userid
	 *            人员id
	 * @param _userName
	 *            人员姓名
	 * @return
	 * @throws Exception
	 */
	private boolean checkUserDeptPost(String _deptid, String _deptName,
			String _postid, String _postName, String _userid, String _userName)
			throws Exception {
		if (_postid == null) {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null,
					"select postname c1 from v_pub_user_post_1 where deptid='"
							+ _deptid + "' and userid='" + _userid + "'");
			if (hvs.length > 0) {
				MessageBox.show(this, "该人员已存在,不能重复操作!"); //
				return false;
			}
		} else {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null,
					"select postname c1 from v_pub_user_post_1 where deptid='"
							+ _deptid + "' and postid='" + _postid
							+ "' and  userid='" + _userid + "'");
			if (hvs.length > 0) {
				MessageBox.show(this, "该人员已存在,不能重复操作!"); //
				return false;
			}
		}
		return true;
	}

	/**
	 * 扁平查看页签，由一个人员定位到其机构
	 * 
	 * @throws Exception
	 */
	private void onGoToDept() throws Exception {
		BillVO billVO = billListPanel_user_flat.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return;
		}

		String str_id = billVO.getStringValue("id"); //
		String str_name = billVO.getStringValue("name"); //

		HashVO[] hvo = UIUtil.getHashVoArrayByDS(null,
				"select userid,username,deptid,deptname from v_pub_user_post_1 where userid='"
						+ str_id + "'"); //
		if (hvo.length == 0) {
			MessageBox.show(this, "找不到【" + str_name + "】所属的机构!"); //
			return;
		}

		String str_deptid = null;
		String str_deptname = null;
		if (hvo.length == 1) {
			str_deptid = hvo[0].getStringValueForDay("deptid"); //
			str_deptname = hvo[0].getStringValueForDay("deptname"); //

		} else {
			ComBoxItemVO[] itemVO = new ComBoxItemVO[hvo.length]; // //
			for (int i = 0; i < hvo.length; i++) {
				itemVO[i] = new ComBoxItemVO(hvo[i].getStringValue("deptid"),
						null, "" + hvo[i].getStringValue("deptname")); //
			}

			JList list = new JList(itemVO); //
			list.setBackground(Color.WHITE); //
			JScrollPane scrollPane = new JScrollPane(list); //
			scrollPane.setPreferredSize(new Dimension(255, 150)); //
			if (JOptionPane.showConfirmDialog(this, scrollPane, "【" + str_name
					+ "】兼职了[" + hvo.length + "]个机构,您想定位到哪一个?",
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			ComBoxItemVO selectedItemVO = (ComBoxItemVO) list
					.getSelectedValue(); //
			str_deptid = selectedItemVO.getId(); //
			str_deptname = selectedItemVO.getName(); //
		}

		if (str_deptid == null || str_deptid.trim().equals("")
				|| str_deptid.trim().equalsIgnoreCase("NULL")) {
			MessageBox.show(this, "【" + str_name + "】所属的机构ID为空!"); //
			return;
		}

		DefaultMutableTreeNode node = billTreePanel_Dept
				.findNodeByKey(str_deptid); //
		if (node == null) {
			MessageBox.show(this, "【" + str_name + "】属于机构【" + str_deptname
					+ "】,您没有权限操作!"); //
			return; //
		}

		billTreePanel_Dept.expandOneNode(node); //
		if (billListPanel_User_Post != null) {
			int li_row = billListPanel_User_Post.findRow("userid", str_id); //
			if (li_row >= 0) {
				billListPanel_User_Post.setSelectedRow(li_row); //
			}
		}
		tabbedPane.setSelectedIndex(0);
	}

	/**
	 * 扁平查看页签，编辑按钮的逻辑
	 * 
	 * @throws Exception
	 */
	private void onEditUser() throws Exception {
		BillVO billVO = billListPanel_user_flat.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.show(this, MSG_NEED_USER); //
			return;
		}

		String[] str_allControlDeptIDs = UIUtil.getLoginUserBLDeptIDs(); //
		if (str_allControlDeptIDs != null) { // 如果不是看全行的权限,则需要校验是否有权修改
			String str_id = billVO.getStringValue("id"); //
			String str_name = billVO.getStringValue("name"); //			
			String[][] str_userdepts = new String[0][0];
			if (str_id == null || str_id.equals("")) {

			} else {
				str_userdepts = UIUtil.getStringArrayByDS(null,
						"select deptid,deptname from v_pub_user_post_1 where userid="
								+ str_id); //

			}
			String[] str_thisuserBLDeptIDs = new String[str_userdepts.length]; //
			String str_deptnames = ""; //
			for (int i = 0; i < str_thisuserBLDeptIDs.length; i++) { //
				str_thisuserBLDeptIDs[i] = str_userdepts[i][0]; //
				str_deptnames += str_userdepts[i][1] + ","; //
			}
			str_deptnames = str_deptnames.substring(0,
					str_deptnames.length() - 1);
			if (!new TBUtil().containTwoArrayCompare(str_allControlDeptIDs,
					str_thisuserBLDeptIDs)) { // 如果登录人员控制的范围包含了这个人!则有权修改,否则报错提示!
				MessageBox.show(this, "【" + str_name + "】属于机构【" + str_deptnames
						+ "】,您没有权限操作!"); //
				return; //
			}
		}
		AddOrEditUserDialog editDialog = new AddOrEditUserDialog(this, billVO
				.getStringValue("id"), null, null,
				WLTConstants.BILLDATAEDITSTATE_UPDATE);
		String oldposition = editDialog.getBillcardPanel().getBillVO()
				.getStringValue("position");
		editDialog.setVisible(true);
		if (editDialog.getCloseType() == 1) {
			billListPanel_user_flat.refreshCurrData();
			BillVO newuservo = editDialog.getBillcardPanel().getBillVO();
			// 如果职务发生变化 ,提示是否加履职
			if (!newuservo.getStringValue("position", "").equals(oldposition)) {
				if (MessageBox.confirm(this, newuservo.getStringValue("name")
						+ "的职务从[" + oldposition + "]修改为["
						+ newuservo.getStringValue("position", "")
						+ "],是否更新其履职信息?")) {
					checkandcreateduties(newuservo.getStringValue("id"));
				}
			}
		}
	}

	private void onDeptDelete() {
		BillTreeNodeVO treeNodeVO = billTreePanel_Dept.getSelectedTreeNodeVO();
		BillVO billVO = billTreePanel_Dept.getSelectedVO();
		if (billVO == null) {
			MessageBox.show(this, "请选择一个结点进行删除操作!"); //
			return;
		}

		if (billVO != null) {
			BillVO[] childVOs = billTreePanel_Dept
					.getSelectedChildPathBillVOs(); // 取得所有选中的
			if (MessageBox.showConfirmDialog(this, "您确定要删除记录【"
					+ treeNodeVO.toString() + "】吗?\r\n这将一并删除其下共【"
					+ childVOs.length + "】条子孙记录,请务必谨慎操作!", "提示",
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			try {
				Vector v_sqls = new Vector(); // 菜单删除
				for (int i = 0; i < childVOs.length; i++) {
					v_sqls.add("delete from " + childVOs[i].getSaveTableName()
							+ " where " + childVOs[i].getPkName() + "='"
							+ childVOs[i].getPkValue() + "'"); //
				}
				UIUtil.executeBatchByDS(null, v_sqls); // 执行数据库操作!!
				billTreePanel_Dept.delCurrNode(); //
				billTreePanel_Dept.getJTree().repaint(); //
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		reloaCorpCacheData();
	}

	private void reloaCorpCacheData() {
		try {
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil
					.lookUpRemoteService(SysAppServiceIfc.class); //
			service.registeCorpCacheData(); // 重新注册一下机构缓存数据!!
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	class AddOrEditUserDialog extends BillCardDialog {
		private String returnNewID; // 新的机构人员岗位表ID
		private String newuserid;
		private String str_deptid, str_postid, pwdwidth; //
		private DESKeyTool des = new DESKeyTool();
		private String editoldCode;
		private boolean haveUserNotHaveInfo = false; // pubuser存在数据，ｉｎｆｏ不存在

		public AddOrEditUserDialog(Container _parent, String _userid,
				String _deptid, String _postId, String _edittype) {
			super(_parent, "SAL_PERSONINFO_CODE2", _edittype);
			str_deptid = _deptid;
			str_postid = _postId;
			if (_edittype == null) {
				return;
			} else if (_edittype
					.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
				addInit();
			} else if (_edittype
					.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
				editInit(_userid);
			}
		}

		private void addInit() {
			try {
				billcardPanel.setEditableByInsertInit();
				billcardPanel
						.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT); //
				if (TBUtil.getTBUtil().isEmpty(str_deptid)) {
					BillVO deptVO = billTreePanel_Dept.getSelectedVO();
					str_deptid = deptVO.getPkValue();
				}
				if (TBUtil.getTBUtil().isEmpty(str_postid)) {
					BillVO postVO = billListPanel_Post.getSelectedBillVO();
					str_postid = postVO.getPkValue();
				}
				newuserid = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER");
				billcardPanel.setValueAt("id", new StringItemVO(newuserid));
				String deptName = UIUtil.getStringValueByDS(null,
						"select name from pub_corp_dept where id = "
								+ str_deptid);
				billcardPanel.setValueAt("maindeptid", new RefItemVO(
						str_deptid, "", deptName));
				String postName = UIUtil.getStringValueByDS(null,
						"select name from pub_post where id = " + str_postid);
				if (postName != null) {
					billcardPanel.setValueAt("mainstation", new StringItemVO(
							postName));
				}
				HashMap menuMap = getMenuConfMap();
				if (menuMap != null && menuMap.containsKey("宽度")) {
					pwdwidth = (String) menuMap.get("宽度");
				} else {
					pwdwidth = new TBUtil().getSysOptionStringValue(
							"初始化人员密码宽度", null);
				}
				if (pwdwidth != null && !pwdwidth.equals("")
						&& !pwdwidth.equals("0")) {
					this.getCardPanel().getCompentByKey("PWD").getLabel()
							.setText("*密码(" + pwdwidth + ")位");
				}
				btn_save.setVisible(false); //
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		private void editInit(String userid) {
			try {
				HashVO[] userVO = UIUtil.getHashVoArrayByDS(null,
						"select *from pub_user where id = " + userid);
				if (userVO.length > 0) { // 找到人员vo
					billcardPanel.queryDataByCondition(" id =" + userid);
					String infoid = billcardPanel.getBillVO().getStringValue(
							"id");
					if (infoid == null || infoid.equals("")) { // pub_user
																// 已经存在该人员，但是ｉｎｆｏ表没有其数据。
						billcardPanel.setEditableByInsertInit();
						billcardPanel
								.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
						haveUserNotHaveInfo = true;
						billcardPanel.setRealValueAt("id", userid);
						billcardPanel.setRealValueAt("name", userVO[0]
								.getStringValue("NAME"));
						billcardPanel.setRealValueAt("code", userVO[0]
								.getStringValue("CODE"));
						if (TBUtil.getTBUtil().isEmpty(str_deptid)
								|| TBUtil.getTBUtil().isEmpty(str_postid)) {
							try {
								HashVO[] all = UIUtil
										.getHashVoArrayByDS(
												null,
												"select userdept, isdefault, postid from pub_user_post where userid='"
														+ userid
														+ "' and userdept in (select id from pub_corp_dept) order by id asc");
								if (all != null && all.length > 0) {
									String maindeptid = all[0]
											.getStringValue("userdept");
									for (int i = 0; i < all.length; i++) {
										if ("Y".equals(all[i]
												.getStringValue("isdefault"))) {
											maindeptid = all[i]
													.getStringValue("userdept");
											break;
										}
									}
									List postid = new ArrayList();
									for (int i = 0; i < all.length; i++) {
										if (all[i].getStringValue("postid") != null
												&& !""
														.equals(all[i]
																.getStringValue("postid"))) {
											postid.add(all[i]
													.getStringValue("postid"));
										}
									}
									FrameWorkCommServiceIfc commService = (FrameWorkCommServiceIfc) UIUtil
											.lookUpRemoteService(FrameWorkCommServiceIfc.class);
									HashMap returnMap = commService
											.getTreePathNameByRecords(null,
													"pub_corp_dept", "id",
													"name", "parentid",
													new String[] { maindeptid });
									billcardPanel.setValueAt("maindeptid",
											new RefItemVO(maindeptid, "",
													returnMap.get(maindeptid)
															+ ""));
									String[][] mainpost = UIUtil
											.getStringArrayByDS(
													null,
													"select name,stationkind from pub_post where deptid='"
															+ maindeptid
															+ "' and name is not null and id in ("
															+ TBUtil
																	.getTBUtil()
																	.getInCondition(
																			postid)
															+ ") order by seq, id ");
									if (mainpost != null && mainpost.length > 0) {
										billcardPanel.setValueAt("mainstation",
												mainpost[0][0]);
										billcardPanel.setRealValueAt(
												"stationkind", mainpost[0][1]);
									}
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						} else {
							String deptName = UIUtil.getStringValueByDS(null,
									"select name from pub_corp_dept where id = "
											+ str_deptid);
							billcardPanel.setValueAt("maindeptid",
									new RefItemVO(str_deptid, "", deptName));
							String post[][] = UIUtil.getStringArrayByDS(null,
									"select name,stationkind from pub_post where id = "
											+ str_postid);
							if (post.length > 0) {
								billcardPanel.setValueAt("mainstation",
										new RefItemVO(post[0][0], "",
												post[0][0]));
								billcardPanel.setValueAt("stationkind",
										new ComBoxItemVO(post[0][1], "",
												post[0][1]));
							}
						}

					} else {
						billcardPanel.setEditableByEditInit();
						billcardPanel
								.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
					}
					String pwd = des.decrypt(userVO[0].getStringValue("pwd"));
					if (pwd == null || pwd.equals("")) {
						pwd = userVO[0].getStringValue("pwd");
					}
					billcardPanel.setValueAt("pwd", new StringItemVO(pwd));
					editoldCode = userVO[0].getStringValue("code");
					billcardPanel.setValueAt("code", new StringItemVO(
							editoldCode));
				}
				btn_save.setVisible(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void showInit() {

		}

		public void onConfirm() {
			try {
				if (!billcardPanel.checkValidate()) {
					return;
				}

				String str_userid = billcardPanel.getRealValueAt("id"); //
				String str_usercode = billcardPanel.getRealValueAt("code")
						.trim(); // 人员编码,最好校验一下该人员编码
				if (pwdwidth != null) {
					if (!checkPwd()) {
						return;
					}
				}
				if (billcardPanel.getEditState().equals(
						WLTConstants.BILLDATAEDITSTATE_UPDATE)
						|| haveUserNotHaveInfo) {
					updateUser();
					this.setCloseType(1);
					this.dispose(); //
					return;
				}

				// 校验该人员是否已存在!!!
				HashVO[] hvos = UIUtil.getHashVoArrayByDS(null,
						"select count(code) c1 from pub_user where code='"
								+ str_usercode + "'"); //
				int li_count = hvos[0].getIntegerValue("c1").intValue(); //
				if (li_count > 0) {
					MessageBox.show(null, "系统中已经存在一个编码叫【" + str_usercode
							+ "】的用户,不能重复录入,请使用导入操作将其导入本机构!"); //
					return;
				}
				// 校验该人员在该机构,该岗位是否存在!!
				String str_insertsql = billcardPanel.getUpdateDataSQL(); //

				// 增加对pub_user pk_dept 的同步处理 Gwang 2012-4-17
				if (this.getCardPanel().getRealValueAt("PK_DEPT") != null
						&& !"".equals(this.getCardPanel().getRealValueAt(
								"PK_DEPT"))) {
					str_deptid = this.getCardPanel().getRealValueAt("PK_DEPT");
				}
				InsertSQLBuilder insertPubUserSql = new InsertSQLBuilder(
						"pub_user"); // 插入pub_user表
				insertPubUserSql.putFieldValue("id", newuserid);
				insertPubUserSql.putFieldValue("code", str_usercode);
				insertPubUserSql.putFieldValue("pwd", des.encrypt(billcardPanel
						.getRealValueAt("pwd"))); //
				insertPubUserSql.putFieldValue("name", billcardPanel
						.getRealValueAt("name").trim());
				insertPubUserSql.putFieldValue("createdate", UIUtil
						.getServerCurrDate());
				insertPubUserSql.putFieldValue("pk_dept", str_deptid);
				insertPubUserSql.putFieldValue("ISFUNFILTER", "Y");
				insertPubUserSql.putFieldValue("creator", ClientEnvironment
						.getCurrSessionVO().getLoginUserName());

				String str_newpk = UIUtil.getSequenceNextValByDS(null,
						"S_PUB_USER_POST"); //
				UIUtil.executeBatchByDS(null, new String[] {
						insertPubUserSql.getSQL(),
						str_insertsql,
						getSQL_User_Post(str_newpk, str_userid, str_deptid,
								str_postid) }); // //
				returnNewID = str_newpk;
				this.setCloseType(1);
				this.dispose(); //
			} catch (Exception e) {
				MessageBox.showException(this, e);
			} //
		}

		// 执行修改人员
		private void updateUser() {
			// editoldCode
			try {
				String str_usercode = billcardPanel.getRealValueAt("code")
						.trim(); // 人员编码,最好校验一下该人员编码
				if (!editoldCode.trim().equals(str_usercode)) {
					HashVO[] hvos = UIUtil.getHashVoArrayByDS(null,
							"select count(code) c1 from pub_user where code='"
									+ str_usercode + "'");
					int li_count = hvos[0].getIntegerValue("c1").intValue(); //
					if (li_count > 0) {
						MessageBox.show(null, "系统中已经存在一个编码叫【" + str_usercode
								+ "】的用户,不能重复!"); //
						return;
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} //
			String updateInfoSql = billcardPanel.getUpdateDataSQL(); //
			UpdateSQLBuilder insertPubUserSql = new UpdateSQLBuilder("pub_user"); // 插入pub_user表
			insertPubUserSql.putFieldValue("code", billcardPanel
					.getRealValueAt("code"));
			insertPubUserSql.putFieldValue("pwd", des.encrypt(billcardPanel
					.getRealValueAt("pwd")));
			insertPubUserSql.putFieldValue("name", billcardPanel
					.getRealValueAt("name"));
			insertPubUserSql.setWhereCondition("id = "
					+ billcardPanel.getRealValueAt("id"));
			try {
				UIUtil.executeBatchByDS(null, new String[] { updateInfoSql,
						insertPubUserSql.getSQL() });
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*
		 * 在初始化用户时候 可以做密码长度校验。太平提出！
		 */
		private boolean checkPwd() {
			String sr_newpwd = this.getCardPanel().getBillVO().getStringValue(
					"PWD");
			if (pwdwidth != null && pwdwidth.contains("-")) {
				String[] str_pwdlength = pwdwidth.split("-");
				if (sr_newpwd.length() < Integer.parseInt(str_pwdlength[0])
						|| sr_newpwd.length() > Integer
								.parseInt(str_pwdlength[1])) { // 10-16位之间
					MessageBox.show(this, "新密码长度必须在" + pwdwidth + "位之间,请修改!"); //
					return false;
				}

			} else if (pwdwidth != null
					&& sr_newpwd.length() != Integer.parseInt(pwdwidth)
					&& !pwdwidth.equals("0")) {
				MessageBox.show(this, "新密码长度必须为" + pwdwidth + "位,请修改!"); //
				return false;
			}
			return true;
		}

		public String getReturnNewID() {
			return returnNewID; //
		}
	}

	/*
	 * 人员列表双击事件 (non-Javadoc)
	 * 
	 * @seecn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener#
	 * onMouseDoubleClicked
	 * (cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent)
	 */
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent event) {

		BillVO billVO = billListPanel_User_Post.getSelectedBillVO();
		String userID = billVO.getStringValue("userid");
		BillCardPanel billCard = new BillCardPanel("SAL_PERSONINFO_CODE1");
		billCard.queryDataByCondition("id = '" + userID + "'");
		BillCardDialog dialog = new BillCardDialog(billListPanel_User_Post,
				"人员信息", billCard, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true);

	}

}
