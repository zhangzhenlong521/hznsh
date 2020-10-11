package cn.com.infostrategy.ui.sysapp.corpdept;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.sysapp.login.RoleVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 机构-岗位-人员-角色维护，可以根据菜单参数配置为五种不同风格：1-机构、岗位;2-机构、岗位、人员;3-机构、人员;4-机构、人员、角色;5-机构、岗位
 * 、人员、角色
 * 
 * 这个类配置的菜单可能有以下菜单配置："页面布局","5","是否可以扁平查看","Y", 以后这将成为最重要的,甚至是唯一性机构,岗位,人员维护界面!!
 * 
 * @author xch
 * 
 */
public class DeptPostUserManagerWFPanel extends AbstractWorkPanel implements BillTreeSelectListener, BillListSelectListener, ActionListener {

	private BillTreePanel billTreePanel_Dept = null; // 机构树
	private BillListPanel billListPanel_Post = null; // 岗位表
	private BillListPanel billListPanel_User_Post = null; // 人员岗位表.
	private BillListPanel billListPanel_User_Role = null; // 人员角色关系表.
	private BillListPanel billListPanel_user_flat = null; // 人员扁平查看

	private WLTTabbedPane tabbedPane = null; // 页签，如果菜单参数中配置可以扁平查看并且在维护界面有人员列表，则需要用页签格式显示

	private WLTButton btn_post_insert, btn_post_update, btn_post_delete, btn_post_seq; // 岗位列表上的系列按钮
	private WLTButton btn_user_import, btn_user_add, btn_user_update, btn_user_trans, btn_user_del, btn_user_seq, btn_lookdeptinfo1; // 人员列表上的系列按钮
	private WLTButton btn_role_add, btn_role_del; // 角色列表上的系列按钮

	private WLTButton btn_gotodept, btn_edituser, btn_lookdeptinfo2;// 扁平查看的系列按钮

	private String str_currdeptid, str_currdeptname = null; // 当前机构ID及机构名称

	private int li_granuType = 3; // 粒度类型,1-最粗的,2-最常用的,3-最细的,即所有的
	private boolean isFilter = false; // 是否权限过滤,即是否根据登录人员进行权限过滤!!!
	private String panelStyle = "5";// 页面布局风格，兼容旧系统，默认是机构、岗位、人员、角色 都有
	private boolean canSearch = true;// 是否有扁平查看页签

	private WLTButton btn_setdefault;

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
			if (tmp_str.equalsIgnoreCase("N") || tmp_str.equalsIgnoreCase("false") || tmp_str.equalsIgnoreCase("否")) {
				canSearch = false;
			}
		}
		this.setLayout(new BorderLayout()); //
		// 以前的机构过滤无法满足分支行系统管理员管理自己本分支行的机构和人员，总行可管理所有机构和人员的需求，故为了扩展，可在配置菜单参数，在机构模板中设置机构查询策略即可【李春娟/2016-03-18】
		if (confmap.get("机构模板") != null) {
			String treeTempletCode = (String) confmap.get("机构模板");
			billTreePanel_Dept = new CorpDeptBillTreePanel(treeTempletCode, getGranuType(), isFilter()); // 机构树,定义是否需要进行权限过滤!!
		} else {
			billTreePanel_Dept = new CorpDeptBillTreePanel(getCorpTempletCode(), getGranuType(), isFilter()); // 机构树,定义是否需要进行权限过滤!!
		}
		billTreePanel_Dept.queryDataByCondition(null); // 查看所有机构
		billTreePanel_Dept.addBillTreeSelectListener(this); //

		WLTSplitPane splitPanel_all = null;

		if ("1".equals(panelStyle)) {// 维护页面是否是第一种风格，只有 机构、岗位
			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Dept, this.getPostPanel()); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("2".equals(panelStyle)) {// 是否是第二种风格，只有 机构、岗位、人员
			WLTSplitPane splitPanel_post_user = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, this.getPostPanel(), this.getUserPanel()); //
			splitPanel_post_user.setDividerLocation(175); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Dept, splitPanel_post_user); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("3".equals(panelStyle)) {// 是否是第三种风格，只有 机构、人员
			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Dept, this.getUserPanel()); //
			splitPanel_all.setDividerLocation(300); //
		} else if ("4".equals(panelStyle)) {// 是否是第四种风格，只有 机构、人员、角色
			WLTSplitPane splitPanel_user_role = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, this.getUserPanel(), this.getRolePanel()); //
			splitPanel_user_role.setDividerLocation(300); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Dept, splitPanel_user_role); //
			splitPanel_all.setDividerLocation(280); //
		} else {// 第五种风格，也就是 机构、岗位、人员、角色 都有
			WLTSplitPane splitPanel_user_role = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, this.getUserPanel(), this.getRolePanel()); //
			splitPanel_user_role.setDividerLocation(360); //

			WLTSplitPane splitPanel_post_user = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, this.getPostPanel(), splitPanel_user_role); //
			splitPanel_post_user.setDividerLocation(175); //

			splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_Dept, splitPanel_post_user); //
			splitPanel_all.setDividerLocation(175); //
		}

		if (canSearch) {// 如果配置为可以扁平查看，则绘制面板
			tabbedPane = new WLTTabbedPane(); //
			tabbedPane.addTab("信息维护", UIUtil.getImage("office_050.gif"), splitPanel_all); //
			tabbedPane.addTab("扁平查看", UIUtil.getImage("office_070.gif"), this.getUserFlatPanel()); //
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
		return "PUB_CORP_DEPT_1"; // 最简单的
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
			billListPanel_Post = new BillListPanel("PUB_POST_CODE1"); // 岗位
			billListPanel_Post.setItemVisible("refpostid", true);// 在此显示岗位组信息
			btn_post_insert = new WLTButton("新增"); // 新增岗位!!
			btn_post_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); // 修改岗位
			btn_post_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); // 删除岗位!!
			btn_post_seq = new WLTButton("排序");// 岗位排序
			btn_post_insert.addActionListener(this); //
			btn_post_seq.addActionListener(this);
			billListPanel_Post.addBatchBillListButton(new WLTButton[] { btn_post_insert, btn_post_update, btn_post_delete, btn_post_seq }); //
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
			billListPanel_User_Post = new BillListPanel("PUB_USER_POST_DEFAULT"); // 人员_岗位的关联表!
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
			// 分支行系统管理员可维护本分支行用户，但为防止分支行误删用户，故增加平台参数，可设置哪些角色可删除用户，一般应该是全行系统管理员【李春娟/2016-07-13】
			String deleteRoles = TBUtil.getTBUtil().getSysOptionStringValue("可删除用户的角色", "");// 可支持多个角色的情况，用英文逗号分隔
			boolean canDeleteUser = false;
			if (deleteRoles == null || "".equals(deleteRoles.trim())) {// 如果没有配置或配置为空，则可删除用户【李春娟/2016-07-13】
				canDeleteUser = true;
			} else {
				RoleVO[] roles = ClientEnvironment.getInstance().getCurrLoginUserVO().getRoleVOs();// 登录人员所有角色
				if (roles != null && roles.length > 0) {
					String[] str_deleteRoles = TBUtil.getTBUtil().split(deleteRoles, ",");
					for (int i = 0; i < roles.length; i++) {
						for (int j = 0; j < str_deleteRoles.length; j++) {
							if (str_deleteRoles[j].equalsIgnoreCase(roles[i].getName())) {
								canDeleteUser = true;
								break;
							}
						}
						if (canDeleteUser) {
							break;
						}
					}
				}
			}
			if (TBUtil.getTBUtil().getSysOptionBooleanValue("系统是否屏蔽掉用户新增功能", false)) {// 太平需要屏蔽新增功能【李春娟/2017-04-16】
				if (canDeleteUser) {
					billListPanel_User_Post.addBatchBillListButton(new WLTButton[] { btn_user_import, btn_user_update, btn_user_trans, btn_user_del, btn_user_seq, btn_lookdeptinfo1, btn_setdefault }); // 为机构_人员_岗位表加上所有按钮!!
				} else {
					billListPanel_User_Post.addBatchBillListButton(new WLTButton[] { btn_user_import, btn_user_update, btn_user_trans, btn_user_seq, btn_lookdeptinfo1, btn_setdefault }); // 为机构_人员_岗位表加上所有按钮!!
				}
			} else {
				if (canDeleteUser) {
					billListPanel_User_Post.addBatchBillListButton(new WLTButton[] { btn_user_import, btn_user_add, btn_user_update, btn_user_trans, btn_user_del, btn_user_seq, btn_lookdeptinfo1, btn_setdefault }); // 为机构_人员_岗位表加上所有按钮!!
				} else {
					billListPanel_User_Post.addBatchBillListButton(new WLTButton[] { btn_user_import, btn_user_add, btn_user_update, btn_user_trans, btn_user_seq, btn_lookdeptinfo1, btn_setdefault }); // 为机构_人员_岗位表加上所有按钮!!
				}
			}
			billListPanel_User_Post.repaintBillListButton(); //
			// 如果有角色，则增加人员的选择事件
			if ("4".equals(panelStyle) || "5".equals(panelStyle)) {//
				billListPanel_User_Post.addBillListSelectListener(this); // 人员选择变化时.
			}
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
			billListPanel_User_Role.addBatchBillListButton(new WLTButton[] { btn_role_add, btn_role_del }); //
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
			btn_lookdeptinfo2 = new WLTButton("兼职情况"); //
			btn_gotodept.addActionListener(this); //
			btn_lookdeptinfo2.addActionListener(this); //
			boolean showEditUser = TBUtil.getTBUtil().getSysOptionBooleanValue("人员扁平查看是否可编辑", true);// 在江西银行发现，分支行系统管理员虽然不可看到总行机构，但在扁平查看中能查询到总行人员，进行修改密码，故增加参数设置【李春娟/2016-06-28】
			if (showEditUser) {
				btn_edituser = new WLTButton("编辑"); //
				btn_edituser.addActionListener(this); //
				billListPanel_user_flat.addBatchBillListButton(new WLTButton[] { btn_gotodept, btn_edituser, btn_lookdeptinfo2 }); // //
			} else {
				billListPanel_user_flat.addBatchBillListButton(new WLTButton[] { btn_gotodept, btn_lookdeptinfo2 }); // //
			}
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
			billListPanel_Post.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq,code"); //
		}
		if (billListPanel_User_Post != null) {
			billListPanel_User_Post.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq,usercode"); //
		}
		if (billListPanel_User_Role != null) {
			billListPanel_User_Role.clearTable(); //
		}
	}

	/**
	 * 列表的选择事件，选择岗位刷新人员，选择人员刷新角色
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == billListPanel_Post && billListPanel_User_Post != null) {// 如果点击的是岗位列表
			// 并且有人员列表则刷新人员列表
			String str_postid = _event.getCurrSelectedVO().getStringValue("id"); //
			//岗位新增后，这里获得的id为null，先判断一下，否则有的oracle数据库执行postid='null'会报错【李春娟/2018-08-11】
			if (str_postid == null) {
				billListPanel_User_Post.clearTable();//
			} else {
				billListPanel_User_Post.queryDataByCondition("postid='" + str_postid + "'", "seq"); //
			}
			if (billListPanel_User_Role != null) {
				billListPanel_User_Role.clearTable(); //
			}
		} else if (_event.getSource() == billListPanel_User_Post && billListPanel_User_Role != null) {// 如果点击的是人员列表
			// 并且有角色列表则刷新角色列表
			String str_useridid = _event.getCurrSelectedVO().getStringValue("userid"); //
			billListPanel_User_Role.QueryDataByCondition("userid='" + str_useridid + "'"); //
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
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
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
		String sql_1 = "update pub_user_post set isdefault = null where userid = " + userid;
		String sql_2 = "update pub_user_post set isdefault = 'Y' where id = " + pk;
		String sql_3 = "update pub_user set pk_dept = " + str_currdeptid + " where id = " + userid;
		UIUtil.executeBatchByDS(null, new String[] { sql_1, sql_2, sql_3 });

		if (billListPanel_Post == null) {// 刷新列表数据【李春娟/2014-02-28】
			billListPanel_User_Post.QueryDataByCondition("deptid = " + str_currdeptid);
		} else {
			BillVO postVO = billListPanel_Post.getSelectedBillVO();
			if (postVO != null) {
				billListPanel_User_Post.refreshCurrSelectedRow();
			} else {
				billListPanel_User_Post.QueryDataByCondition("deptid = " + str_currdeptid);
			}
		}
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
		initValueMap.put("deptid", new RefItemVO(str_currdeptid, "", str_currdeptname)); // 定义初始值!因操作要求中相关岗位的需要，将机构设置为了参照，所以需要修改这个为参照vo【李春娟/2012-03-29】
		billListPanel_Post.doInsert(initValueMap); // 做新增操作!!!
	}

	/**
	 * 岗位排序
	 */
	private void onSeqPost() {
		// 一开始用的billListPanel_Post.getBillVOs(),发现删除了的记录也出现了,注意billListPanel_Post.getAllBillVOs()
		// 是获得显示的billvo，不包括删除的billvo！
		SeqListDialog dialog_post = new SeqListDialog(this, "岗位排序", billListPanel_Post.getTempletVO(), billListPanel_Post.getAllBillVOs());
		dialog_post.setVisible(true);
		if (dialog_post.getCloseType() == 1) {// 如果点击确定返回，则需要刷新一下页面
			billListPanel_Post.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq,code"); //
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
				if (JOptionPane.showConfirmDialog(this, "您没有选择岗位, 是否继续?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return; //
				}
			} else {
				str_postid = billVO_Post.getStringValue("id"); // 岗位ID
				str_postname = billVO_Post.getStringValue("name"); // 岗位ID
			}
		}

		BillListDialog dialog = new BillListDialog(this, "选择人员", "PUB_USER_ICN", 700, 500); //
		dialog.getBilllistPanel().setAllBillListBtnVisiable(false);
		dialog.getBilllistPanel().setItemEditable(false); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return;
		}

		BillVO[] billVOS = dialog.getReturnBillVOs(); //
		String str_userid = billVOS[0].getStringValue("id"); //
		String str_username = billVOS[0].getStringValue("name"); //

		boolean bo_checked = checkUserDeptPost(str_currdeptid, str_currdeptname, str_postid, str_postname, str_userid, str_username); // 判断该人员是否在此机构和岗位下，如果在，就不用导入了
		if (!bo_checked) {
			return;
		}

		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select deptname,postname from v_pub_user_post_1 where userid='" + str_userid + "'"); //
		if (hvs.length == 0) {
			if (JOptionPane.showConfirmDialog(this, "确定要把【" + str_username + "】导入到【" + str_currdeptname + "】中来吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			String str_newpk = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_POST"); //
			String str_sql = getSQL_User_Post(str_newpk, str_userid, str_currdeptid, str_postid);
			// 增加对pub_user pk_dept 的同步处理 Gwang 2012-4-17
			String sql = "update pub_user set pk_dept = " + str_currdeptid + " where id = " + str_userid;
			UIUtil.executeBatchByDS(null, new String[] { str_sql, sql });
			billListPanel_User_Post.addBillVOs(billListPanel_User_Post.queryBillVOsByCondition("id='" + str_newpk + " '")); //
			MessageBox.show(this, "导入成功!"); //
		} else {
			String str_alreadyDeptNames = "";
			for (int i = 0; i < hvs.length; i++) {
				str_alreadyDeptNames = str_alreadyDeptNames + "机构【" + hvs[i].getStringValue("deptname") + "】,岗位【" + (hvs[i].getStringValue("postname") == null ? "" : hvs[i].getStringValue("postname")) + "】\r\n";
			}

			int li_result = -1; //
			if (str_postid != null) {
				li_result = JOptionPane.showOptionDialog(this, "该人员当前所在:\r\n" + str_alreadyDeptNames + "您希望将该人员如何操作?", //
						"提示", JOptionPane.DEFAULT_OPTION, //
						JOptionPane.QUESTION_MESSAGE, null, //
						new String[] { "迁移到本机构岗位", "复制到本机构岗位", "取消" }, //
						"取消"); //
			} else {
				li_result = JOptionPane.showOptionDialog(this, "该人员当前所在:\r\n" + str_alreadyDeptNames + "您希望将该人员如何操作?", //
						"提示", JOptionPane.DEFAULT_OPTION, //
						JOptionPane.QUESTION_MESSAGE, null, //
						new String[] { "迁移到本机构", "复制到本机构", "取消" }, //
						"取消"); //
			}

			if (li_result == 0) { // 迁移到本机构, (删除所有人员和岗位关系, 重新新增一条人员和岗位的对应关系,
				// 并设置为默认岗位 Gwang 2013-07-30)
				String str_newpk = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_POST"); //
				String str_sql = getSQL_User_Post(str_newpk, str_userid, str_currdeptid, str_postid);
				// 增加对pub_user pk_dept 的同步处理 Gwang 2012-4-17
				String sql = "update pub_user set pk_dept = " + str_currdeptid + " where id = " + str_userid;
				UIUtil.executeBatchByDS(null, new String[] { "delete from pub_user_post where userid=" + str_userid, str_sql, sql }); //
				billListPanel_User_Post.addBillVOs(billListPanel_User_Post.queryBillVOsByCondition("id='" + str_newpk + " '")); //
			} else if (li_result == 1) { // 复制到本机构(新增人员和岗位的对应关系, 原来的默认岗位不变
				// Gwang 2013-07-30)
				String str_newpk = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_POST"); //
				String str_sql = getSQL_User_Post(str_newpk, str_userid, str_currdeptid, str_postid);
				String sql_update = "update pub_user_post set isdefault = null where id=" + str_newpk;
				UIUtil.executeBatchByDS(null, new String[] { str_sql, sql_update }); //
				billListPanel_User_Post.addBillVOs(billListPanel_User_Post.queryBillVOsByCondition("id='" + str_newpk + " '")); //

			}
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
	private String getSQL_User_Post(String str_newpk, String str_userid, String str_currdeptid2, String str_postid) {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_user_post");
		isql.putFieldValue("id", str_newpk);
		isql.putFieldValue("userid", str_userid);
		isql.putFieldValue("postid", str_postid);
		isql.putFieldValue("userdept", str_currdeptid);
		isql.putFieldValue("isdefault", "Y");
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
				if (JOptionPane.showConfirmDialog(this, "您没有选择岗位,是否继续?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			} else {
				str_postid = billVO_Post.getStringValue("id"); // 岗位ID
			}
		}

		AddUserDialog dialog = new AddUserDialog(this, str_currdeptid, str_postid, getMenuConfMap()); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_newid = dialog.getReturnNewID(); //
			billListPanel_User_Post.addBillVOs(billListPanel_User_Post.queryBillVOsByCondition("id='" + str_newid + " '")); //		
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
		boolean falg = editUserDialog(str_userid);
		if (falg) {
			billListPanel_User_Post.setBillVOAt(billListPanel_User_Post.getSelectedRow(), billListPanel_User_Post.queryBillVOsByCondition("id='" + str_id + "'")[0]);
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

		TransUserDialog dialog = new TransUserDialog(this, str_id, str_currdeptid, str_currdeptname, str_userid, str_username, getGranuType(), isFilter());
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {

			// 刷新一把
			if (billListPanel_User_Post != null) {
				billListPanel_User_Post.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq,postcode"); //
			}

			if (billListPanel_User_Role != null) {// 如果有角色列表才清空列表
				billListPanel_User_Role.clearTable();
			}
		}
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

		int li_result = JOptionPane.showOptionDialog(this, "您想如何操作?", // 
				"提示", //
				JOptionPane.DEFAULT_OPTION, //
				JOptionPane.QUESTION_MESSAGE, null, new String[] { "移出【" + str_currdeptname + "】", "彻底删除人员", "取消" }, "取消");
		boolean candelete = TBUtil.getTBUtil().getSysOptionBooleanValue("是否可以彻底删除人员", true);//太平集团不允许在我们系统中彻底删除人员，故增加该参数【李春娟/2017-101-9】
		int li_result1 = 0;
		if (candelete) {//可以彻底删除
			li_result1 = JOptionPane.showOptionDialog(this, "您想如何操作?", // 
					"提示", //
					JOptionPane.DEFAULT_OPTION, //
					JOptionPane.QUESTION_MESSAGE, null, new String[] { "移出【" + str_currdeptname + "】", "彻底删除人员", "取消" }, "取消");

		} else {
			li_result1 = JOptionPane.showOptionDialog(this, "您想如何操作?", // 
					"提示", //
					JOptionPane.DEFAULT_OPTION, //
					JOptionPane.QUESTION_MESSAGE, null, new String[] { "移出【" + str_currdeptname + "】", "取消" }, "取消");

		}
		// 移出人员, 比较复杂， 要处理是否是默认岗位， 是否有多个岗位， Gwang 2013-07-30
		if (li_result1 == 0) {

			String isDefault = billVO.getStringValue("isdefault");
			String sql[] = null;
			String sql_update = "";
			String sql_del = "delete from pub_user_post where id=" + billVO.getStringValue("id");

			if ("Y".equals(isDefault)) { // 移出的是主要岗位
				String otherDefaultDeptID = this.getOtherDefaultDeptID(str_userid, str_currdeptid);
				if (otherDefaultDeptID.equals("")) { // 此人只有一个岗位
					sql_update = "update pub_user set pk_dept = null where id = " + str_userid;
					sql = new String[] { sql_del, sql_update };
				} else { // 如果有多个岗位
					String pk = "";
					if (otherDefaultDeptID.equals("many")) { // 有多于2个岗位,
						// 让用户选择新的主要岗位;
						BillListDialog dialog = new BillListDialog(this, "请选择新的主要岗位", "PUB_USER_POST_DEFAULT", 700, 500); //
						dialog.getBilllistPanel().setAllBillListBtnVisiable(false);
						dialog.getBilllistPanel().setItemEditable(false);
						dialog.getBilllistPanel().QueryDataByCondition("userid = " + str_userid + " and deptid <> " + str_currdeptid);
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
					sql_update = "update pub_user set pk_dept = " + otherDefaultDeptID + " where id = " + str_userid;
					String sql_update2 = "update pub_user_post set isdefault = 'Y' where id = " + pk;
					sql = new String[] { sql_del, sql_update, sql_update2 };
				}

			} else {
				sql = new String[] { sql_del };
			}
			// for (String s : sql) {
			// System.out.println(s);
			// }

			UIUtil.executeBatchByDS(null, sql);

			// //追加默认部门更改【杨科/2013-05-20】
			// String sql_update = "";
			// HashVO[] hvos_user_post = UIUtil.getHashVoArrayByDS(null, "select
			// * from pub_user_post where userid='" + str_userid + "' and
			// userdept<>'" + str_currdeptid + "' order by isdefault");
			// if(hvos_user_post.length>0){
			// if(!hvos_user_post[0].getStringValue("isdefault",
			// "").equals("Y")){
			// if(!hvos_user_post[0].getStringValue("id", "").equals("")){
			// sql_update = "update pub_user_post set isdefault = 'Y' where id =
			// " + hvos_user_post[0].getStringValue("id", "");
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
		} else if (candelete && li_result1 == 1) { // 彻底删除人员
			ArrayList al_sqls = new ArrayList(); //
			al_sqls.add("delete from pub_user_post where userid='" + str_userid + "'"); //
			al_sqls.add("delete from pub_user_role where userid='" + str_userid + "'"); //
			al_sqls.add("delete from pub_user_menu where userid='" + str_userid + "'"); //
			al_sqls.add("delete from pub_user      where id='" + str_userid + "'"); //
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
		String sql = "select id, userdept from pub_user_post where userid = " + userID + " and userdept <> " + deptID;
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
		SeqListDialog dialog_user = new SeqListDialog(this, "人员排序", billListPanel_User_Post.getTempletVO(), billListPanel_User_Post.getAllBillVOs());
		dialog_user.setVisible(true);
		if (dialog_user.getCloseType() == 1) {// 如果点击确定返回，则需要刷新一下页面
			billListPanel_User_Post.queryDataByCondition("deptid='" + str_currdeptid + "'", "seq,usercode"); //
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

		String str_sql_1 = "select deptcode,deptname,postcode,postname,isdefault from v_pub_user_post_1 where userid=" + userID + " order by isdefault desc";
		String str_sql_2 = "select rolecode,rolename from v_pub_user_role_1 where userid=" + userID; //
		Vector v_return = UIUtil.getHashVoArrayReturnVectorByMark(null, new String[] { str_sql_1, str_sql_2 }); //
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
			sb_msg.append("" + (i + 1) + ")" + (hsv_userdepts[i].getStringValue("isdefault") == null ? "" : "[主要岗位] "));
			sb_msg.append("" + (hsv_userdepts[i].getStringValue("deptname") == null ? "" : hsv_userdepts[i].getStringValue("deptname")) + "--");
			sb_msg.append("" + (hsv_userdepts[i].getStringValue("postname") == null ? "" : hsv_userdepts[i].getStringValue("postname")) + "\r\n"); //			
		}

		sb_msg.append("\r\n\r\n"); //

		sb_msg.append("【" + str_name + "】所有角色信息:\r\n"); //
		for (int i = 0; i < hsv_userroles.length; i++) {
			// sb_msg.append("" + (i + 1) + ")角色编码/名称=[" +
			// hsv_userroles[i].getStringValue("rolecode") + "/" +
			// hsv_userroles[i].getStringValue("rolename") + "]\r\n"); //
			sb_msg.append("" + (i + 1) + ")" + hsv_userroles[i].getStringValue("rolename") + "\r\n");
		}
		MessageBox.showTextArea(this, "【" + str_name + "】", sb_msg.toString(), 600, 300);
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

		BillListDialog dialog = new BillListDialog(this, "请选择一个角色", "PUB_ROLE_1", 700, 500); //
		dialog.getBilllistPanel().setItemEditable(false); //
		dialog.getBilllistPanel().QueryDataByCondition(null); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return;
		}
		// 袁江晓 20120917 添加 解决能批量添加的功能 begin
		BillVO[] billVOS = dialog.getReturnBillVOs(); //
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select userid,roleid,userdept from pub_user_role where userid='" + str_userid + "' and userdept='" + str_currdeptid + "'"); // 先获得由userid和userdept组合查询的数据
		String str_roles = ";";// 设置分号主要是为了不让第一个添加的和后面的重复，否则在判断contains的时候会判断不出来
		for (int j = 0; j < hvs.length; j++) { // 先查询出之前已经具有的roleid，如果有则跳过，如果没有则插入该roleid
			String old_roleid = hvs[j].getStringValue("roleid");
			str_roles += old_roleid + ";";
		}
		for (int i = 0; i < billVOS.length; i++) {
			String str_roleid = billVOS[i].getStringValue("id"); // 获得id
			if (str_roles.contains(";" + str_roleid + ";")) {// 表示新添加的该roleid已经存在在之前添加的权限id中;用contains()方法，indexOf()方法有人经常忽略了第一个字符匹配的时候返回值为0【李春娟/2017-04-28】
				continue;
			} else {
				String str_newpk = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_ROLE"); //
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
		billListPanel_User_Role.QueryDataByCondition("userid='" + str_userid + "'"); // 刷新列表
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
		if (JOptionPane.showConfirmDialog(this, "确定要把角色【" + rolename + "】删除吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		UIUtil.executeUpdateByDS(null, sql);
		billListPanel_User_Role.removeSelectedRow();
		if (billListPanel_User_Role.getRowCount() > 1) {// 这里需要判断一下，如果就只有一个角色，那删除后就没有角色可以选择了【李春娟/2012-05-24】
			billListPanel_User_Role.getTable().removeRowSelectionInterval(0, billListPanel_User_Role.getRowCount() - 1);
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
	private boolean checkUserDeptPost(String _deptid, String _deptName, String _postid, String _postName, String _userid, String _userName) throws Exception {
		if (_postid == null) {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select postname c1 from v_pub_user_post_1 where deptid='" + _deptid + "' and userid='" + _userid + "'");
			if (hvs.length > 0) {
				MessageBox.show(this, "该人员已存在,不能重复操作!"); //
				return false;
			}
		} else {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select postname c1 from v_pub_user_post_1 where deptid='" + _deptid + "' and postid='" + _postid + "' and  userid='" + _userid + "'");
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

		HashVO[] hvo = UIUtil.getHashVoArrayByDS(null, "select userid,username,deptid,deptname from v_pub_user_post_1 where userid='" + str_id + "'"); //
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
				itemVO[i] = new ComBoxItemVO(hvo[i].getStringValue("deptid"), null, "" + hvo[i].getStringValue("deptname")); //
			}

			JList list = new JList(itemVO); //
			list.setBackground(Color.WHITE); //
			JScrollPane scrollPane = new JScrollPane(list); //
			scrollPane.setPreferredSize(new Dimension(255, 150)); //
			if (JOptionPane.showConfirmDialog(this, scrollPane, "【" + str_name + "】兼职了[" + hvo.length + "]个机构,您想定位到哪一个?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			ComBoxItemVO selectedItemVO = (ComBoxItemVO) list.getSelectedValue(); //
			str_deptid = selectedItemVO.getId(); //
			str_deptname = selectedItemVO.getName(); //
		}

		if (str_deptid == null || str_deptid.trim().equals("") || str_deptid.trim().equalsIgnoreCase("NULL")) {
			MessageBox.show(this, "【" + str_name + "】所属的机构ID为空!"); //
			return;
		}

		DefaultMutableTreeNode node = billTreePanel_Dept.findNodeByKey(str_deptid); //
		if (node == null) {
			MessageBox.show(this, "【" + str_name + "】属于机构【" + str_deptname + "】,您没有权限操作!"); //
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
				str_userdepts = UIUtil.getStringArrayByDS(null, "select deptid,deptname from v_pub_user_post_1 where userid=" + str_id); //

			}
			String[] str_thisuserBLDeptIDs = new String[str_userdepts.length]; //
			String str_deptnames = ""; //
			for (int i = 0; i < str_thisuserBLDeptIDs.length; i++) { //
				str_thisuserBLDeptIDs[i] = str_userdepts[i][0]; //
				str_deptnames += str_userdepts[i][1] + ","; //
			}
			str_deptnames = str_deptnames.substring(0, str_deptnames.length() - 1);
			if (!new TBUtil().containTwoArrayCompare(str_allControlDeptIDs, str_thisuserBLDeptIDs)) { // 如果登录人员控制的范围包含了这个人!则有权修改,否则报错提示!
				MessageBox.show(this, "【" + str_name + "】属于机构【" + str_deptnames + "】,您没有权限操作!"); //
				return; //
			}
		}

		boolean flag = editUserDialog(billVO.getStringValue("id"));
		if (flag) {
			billListPanel_user_flat.refreshCurrData();
		}
	}

	/**
	 * 两个页签的编辑人员弹出框
	 * 
	 * @param userid
	 *            人员id
	 * @return
	 */
	private boolean editUserDialog(String userid) {
		BillCardPanel cardPanel = new BillCardPanel("PUB_USER_CODE1") {
			public boolean checkValidate() {
				boolean flag = super.checkValidate();
				if (!flag) {
					return false;
				}
				String pwdlength = new TBUtil().getSysOptionStringValue("初始化人员密码宽度", "0");
				String pwd = this.getBillVO().getStringValue("pwd");

				// 是否进行密码复杂性判断
				String pwdOption = new TBUtil().getSysOptionStringValue("密码复杂性判断", null);

				// 追加管理员密码特殊校验规则 【杨科/2013-04-27】
				String pwd_admin = new TBUtil().getSysOptionStringValue("管理员密码规则", ""); // admin,10-16,2
				if (!pwd_admin.equals("") && pwd_admin.contains(",")) {
					String[] pwd_admins = pwd_admin.split(",");
					String str_user = this.getBillVO().getStringValue("code");

					if (pwd_admins[0] != null && !pwd_admins[0].equals("") && pwd_admins[0].contains(";")) {
						String[] str_users = pwd_admins[0].split(";");
						for (int i = 0; i < str_users.length; i++) {
							if (str_user.equals(str_users[i])) {
								pwdlength = pwd_admins[1];
								pwdOption = pwd_admins[2];
							}
						}
					} else {
						if (str_user.equals(pwd_admins[0])) {
							pwdlength = pwd_admins[1];
							pwdOption = pwd_admins[2];
						}
					}
				}

				if (pwdlength != null && pwdlength.contains("-")) {
					String[] str_pwdlength = pwdlength.split("-");
					if (pwd.length() < Integer.parseInt(str_pwdlength[0]) || pwd.length() > Integer.parseInt(str_pwdlength[1])) { // 10-16位之间
						MessageBox.show(this, "请设置新密码长度在" + pwdlength + "位之间!"); //
						return false;
					}

				} else if (pwdlength != null && pwd.length() != Integer.parseInt(pwdlength) && !pwdlength.equals("0")) {
					MessageBox.show(this, "请设置新密码长度为" + pwdlength + "位!"); //
					return false;
				}

				// 是否进行密码复杂性判断
				if (pwdOption != null) {
					HashMap parmap = new HashMap();
					parmap.put("pwd", pwd);
					String isPass = "";
					String msg = "";
					if (pwdOption.contains(".")) {
						HashMap hashmap = new TBUtil().reflectCallCommMethod(pwdOption, parmap);
						isPass = hashmap.get("isPass").toString();
						msg = hashmap.get("msg") != null ? hashmap.get("msg").toString() : null;
					} else { // 采用系统默认的机制
						int level = 0;
						try {
							level = Integer.parseInt(pwdOption);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						boolean flag_ = new TBUtil().checkPasswordComplex(pwd, level);
						if (flag_) {
							return true;
						} else {
							MessageBox.show(this, "密码必须由大写字母、小写字母、数字、特殊字符中的" + level + "种组成!");
							return false;
						}
					}
					if (isPass.equals("0")) {
						if (msg == null || msg.equals("")) {
							msg = "密码必须由字母、数字、特殊字符中的2种组成!";
						}
						MessageBox.show(this, msg);
						return false;
					}
				}

				return true;
			}

		};
		cardPanel.queryDataByCondition(" id = " + userid);
		cardPanel.setEditableByEditInit();
		String pwdlength = new TBUtil().getSysOptionStringValue("初始化人员密码宽度", "0");
		if (pwdlength != null && !pwdlength.equals("") && !pwdlength.equals("0")) {
			cardPanel.getCompentByKey("PWD").getLabel().setText("*密码(" + pwdlength + "位)");
		}
		BillCardDialog dialog = new BillCardDialog(this, "人员信息修改", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);
		if (dialog.getCloseType() != 1) {
			return false;
		}
		return true;
	}
}
