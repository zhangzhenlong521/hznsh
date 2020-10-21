package com.pushworld.ipushgrc.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillTreeDefaultMutableTreeNode;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.BillTreeCheckEditEvent;
import cn.com.infostrategy.ui.mdata.BillTreeCheckEditListener;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 复杂参照 左边机构树，右边属于左边选中机构的人员 可以选择加入多个人员，并且可以看到该人员的所属机构。
 * 后来增加了构造方法，可以配置选择人员是否单选，默认多选【李春娟/2012-07-16】
 * 加入人员精确定位系统参数中配置【zzl 2017-11-23】
 * 
 * @author hm
 */
public class CommSelectUserRefDialog extends AbstractRefDialog implements ActionListener, BillTreeCheckEditListener, BillCardEditListener, BillTreeSelectListener {
	private static final long serialVersionUID = 1L;
	private BillFormatPanel billFormatPanel = null; //
	private BillTreePanel billTreePanel = null; //
	private BillListPanel billListPanel = null; //
	private BillListPanel billListPanel_add = null; //
	private BillQueryPanel billQueryPanel = null;
	private BillCardPanel billCardPanel = null;
	private WLTButton btn_confirm, btn_cancel, btn_adduser, btn_deleteuser;
	private RefItemVO returnRefItemVO = null; //
	private HashMap haveSelected = new HashMap();
	private String depttempletCode = "PUB_CORP_DEPT_CODE1_IC";
	private StringBuilder roleids = null;//记录过滤的角色id
	private StringBuilder ids = null;//记录选择的部门id
	private boolean isSingleSelect = false;//是否是单选
	private boolean isrolelinked = false;//是否进行角色过滤
	private int count;//配置的人员显示上限
	private DefaultMutableTreeNode[] billVO_oldtree;
	private String roles;
	private String roleid;
	private Boolean canSearch=TBUtil.getTBUtil().getSysOptionBooleanValue(
			"人员扁平查看是否显示", true);
	private WLTTabbedPane tabbedPane = null; 
	private BillListPanel billListPanel_user_flat = null; // 人员扁平查看
	private WLTButton btn_gotodept, btn_edituser, btn_lookdeptinfo2;// 扁平查看的系列按钮
	private final static String MSG_NEED_USER = "请选择一个人员!";
	public CommSelectUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		this(_parent, _title, refItemVO, panel, null);
	}

	/*
	 * type参数是用来控制树节点显示到哪层。
	 * type:本机构、默认、
	 */
	public CommSelectUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _depttempletCode) {
		super(_parent, _title, refItemVO, panel);
		if (_depttempletCode != null) {
			depttempletCode = _depttempletCode; //设置机构模板，用来做机构数据权限过滤。
		}
		returnRefItemVO = refItemVO;
	}

	/*
	 * type参数是用来控制树节点显示到哪层。
	 * type:本机构、默认、
	 */
	public CommSelectUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _depttempletCode, String _singleSelect) {
		super(_parent, _title, refItemVO, panel);
		if (_depttempletCode != null) {
			depttempletCode = _depttempletCode; //设置机构模板，用来做机构数据权限过滤。
		}
		returnRefItemVO = refItemVO;
		if ("Y".equalsIgnoreCase(_singleSelect)) {
			isSingleSelect = true;
		}
	}

	public CommSelectUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _depttempletCode, String _singleSelect, String _isrolelinked) {
		super(_parent, _title, refItemVO, panel);
		if (_depttempletCode != null) {
			depttempletCode = _depttempletCode; //设置机构模板，用来做机构数据权限过滤。
		}
		returnRefItemVO = refItemVO;
		if ("Y".equalsIgnoreCase(_singleSelect)) {
			isSingleSelect = true;
		}
		if ("Y".equalsIgnoreCase(_isrolelinked)) {
			isrolelinked = true;
		}
	}

	public void initialize() {
		this.setLayout(new BorderLayout()); //
		count = new TBUtil().getSysOptionIntegerValue("机构人员参照显示人数上限", 100);
		if (isSingleSelect) {
			billFormatPanel = new BillFormatPanel("getSplit(getTree(\"" + depttempletCode + "\"),getList(\"PUB_USER_POST_DEFAULT\"),\"左右\",225)"); //
			billTreePanel = billFormatPanel.getBillTreePanel(); // 机构树
			billTreePanel.reSetTreeChecked(false);
			billListPanel = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT", 1); // 人员
			billListPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			billTreePanel.addBillTreeSelectListener(this);
		} else {
			if (isrolelinked) {
				billFormatPanel = new BillFormatPanel("getSplit(getTree(\"" + depttempletCode + "\"),getSplit(getList(\"PUB_USER_POST_DEFAULT_code1_IC\"),getList(\"PUB_USER_POST_DEFAULT\"),\"上下\",250),\"左右\",225)"); //
				billListPanel = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT_code1_IC", 1); // 人员
				billListPanel_add = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT", 1); //
				billQueryPanel = billListPanel.getQuickQueryPanel();
				billQueryPanel.removeAll();
				billQueryPanel.setLayout(new BorderLayout());
				billCardPanel = new BillCardPanel("PUB_ROLE_1_IC");
				billCardPanel.setVisible(true);
				billCardPanel.setEditable(true);
				billCardPanel.addBillCardEditListener(this);
				billQueryPanel.add(billCardPanel);
				billQueryPanel.updateUI();
				billListPanel.setQuickQueryPanelVisiable(true);
			} else {
				billFormatPanel = new BillFormatPanel("getSplit(getTree(\"" + depttempletCode + "\"),getSplit(getList(\"PUB_USER_POST_DEFAULT\"),getList(\"PUB_USER_POST_DEFAULT\"),\"上下\",250),\"左右\",225)"); //
				billListPanel = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT", 1); // 人员
				billListPanel_add = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT", 2); //
			}
			billTreePanel = billFormatPanel.getBillTreePanel(); // 机构树
			billTreePanel.reSetTreeChecked(true);
			billTreePanel.addBillTreeCheckEditListener(this); //
			btn_adduser = new WLTButton("加入");
			btn_deleteuser = new WLTButton("删除");
			btn_deleteuser.addActionListener(this);
			btn_adduser.addActionListener(this);
			billListPanel_add.addBatchBillListButton(new WLTButton[] { btn_adduser, btn_deleteuser }); //
			billListPanel_add.repaintBillListButton(); // 重绘按钮!
			if (billListPanel_add.getQuickQueryPanel() != null) {
				billListPanel_add.getQuickQueryPanel().setVisible(false); //
			}
			if (returnRefItemVO != null) {
				String id = returnRefItemVO.getId();
				if (id != null && !id.equals("")) {
					String sql = billListPanel_add.getSQL("userid in (" + new TBUtil().getInCondition(id) + ")");
					BillVO billVOs[] = null;
					try {
						billVOs = UIUtil.getBillVOsByDS(null, sql, billListPanel_add.templetVO);
					} catch (WLTRemoteException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < billVOs.length; i++) {
						if (haveSelected.containsKey(billVOs[i].getStringValue("userid"))) {

						} else {
							billListPanel_add.addRow(billVOs[i]);
							haveSelected.put(billVOs[i].getStringValue("userid"), null);
						}
					}
				}
			}
		}
		billTreePanel.getBtnPanel().setVisible(false);
		billTreePanel.queryDataByCondition(null);
		if(canSearch){
			tabbedPane = new WLTTabbedPane(); //
			tabbedPane.addTab("信息维护", UIUtil.getImage("office_134.gif"), billFormatPanel); //
			tabbedPane.addTab("人员查找", UIUtil.getImage("office_194.gif"), this.getUserFlatPanel()); //
		}else{
			this.add(billFormatPanel, BorderLayout.CENTER); //
		}
		this.add(tabbedPane);
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
		this.setSize(700, 600);
	}
	/**
	 * zzl 把人员定位加上了
	 * @return
	 */
	private BillListPanel getUserFlatPanel() {
		if (this.billListPanel_user_flat == null) {
			billListPanel_user_flat = new BillListPanel("PUB_USER_ICN"); // 扁平查看!
			btn_gotodept = new WLTButton("精确定位"); //
			btn_lookdeptinfo2 = new WLTButton("兼职情况"); //
			btn_gotodept.addActionListener(this); //
			btn_lookdeptinfo2.addActionListener(this); //
			boolean showEditUser = TBUtil.getTBUtil().getSysOptionBooleanValue(
					"人员扁平查看是否可编辑", true);// 在江西银行发现，分支行系统管理员虽然不可看到总行机构，但在扁平查看中能查询到总行人员，进行修改密码，故增加参数设置【李春娟/2016-06-28】
			if (showEditUser) {
				btn_edituser = new WLTButton("编辑"); //
				btn_edituser.addActionListener(this); //
				billListPanel_user_flat.addBatchBillListButton(new WLTButton[] {
						btn_gotodept}); // //
			} else {
				billListPanel_user_flat.addBatchBillListButton(new WLTButton[] {
						btn_gotodept}); // //
			}
			billListPanel_user_flat.repaintBillListButton(); // 重新按钮绘制!!!
			billListPanel_user_flat.getQuickQueryPanel().setVisible(true); //
		}
		return billListPanel_user_flat;
	}
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_adduser) {
			onAddUser();
		} else if (e.getSource() == btn_deleteuser) {
			onDeleteUser();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}else if(e.getSource() == btn_gotodept){
			try {
				onGoToDept();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
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

		DefaultMutableTreeNode node = billTreePanel
				.findNodeByKey(str_deptid); //
		if (node == null) {
			MessageBox.show(this, "【" + str_name + "】属于机构【" + str_deptname
					+ "】,您没有权限操作!"); //
			return; //
		}

		billTreePanel.expandOneNode(node); //
		if (billListPanel != null) {
			int li_row = billListPanel.findRow("userid", str_id); //
			if (li_row >= 0) {
				billListPanel.setSelectedRow(li_row); //
			}
		}
		tabbedPane.setSelectedIndex(0);
	}

	private void onAddUser() {
		BillVO[] billVO_list = billListPanel.getSelectedBillVOs(); //
		if (billVO_list.length == 0) {
			MessageBox.show(this, "请至少选择一个人员!"); //
			return;
		}
		// 把选择取消
		JTable table = billListPanel_add.getTable();
		if (table.getRowCount() > 0) {
			table.removeRowSelectionInterval(0, table.getRowCount() - 1);
		}
		for (int i = 0; i < billVO_list.length; i++) {
			String userid = billVO_list[i].getStringValue("userid");
			if (!haveSelected.containsKey(userid)) {
				billListPanel_add.addRow(billVO_list[i]);
				billListPanel_add.addSelectedRow(billListPanel_add.getRowCount() - 1);
				haveSelected.put(userid, null);
			} else {
				int rowNum = billListPanel_add.findRow("userid", userid);
				if (rowNum >= 0) {
					billListPanel_add.addSelectedRow(rowNum);
				}
			}
		}
	}

	private void onDeleteUser() {
		BillVO[] billVO_addlist = billListPanel_add.getSelectedBillVOs(); //
		if (billVO_addlist == null) {
			MessageBox.show(this, "请选择一个人员!"); //
			return;
		}
		for (int i = 0; i < billVO_addlist.length; i++) {
			haveSelected.remove(billVO_addlist[i].getStringValue("userid"));
		}
		billListPanel_add.removeSelectedRows();
	}

	private void onConfirm() {
		if (isSingleSelect) {
			BillVO billvo = billListPanel.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(this, "请选择一个人员!"); //
				return;
			}
			returnRefItemVO = new RefItemVO(billvo.getStringValue("userid"), "", billvo.getStringValue("username")); //
		} else {
			BillVO[] billVO_list = billListPanel_add.getAllBillVOs(); //
			if (billVO_list.length == 0) {
				MessageBox.show(this, "请至少加入一个人员!"); //
				return;
			}
			StringBuilder userids = new StringBuilder(";");
			StringBuilder useridnames = new StringBuilder(";");
			for (int i = 0; i < billVO_list.length; i++) {
				userids.append(billVO_list[i].getStringValue("userid") + ";");
				useridnames.append(billVO_list[i].getStringValue("username") + ";");
			}
			returnRefItemVO = new RefItemVO(userids.toString(), "", useridnames.toString()); //
		}
		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose(); //
	}

	public void onBillTreeCheckEditChanged(BillTreeCheckEditEvent _event) {
		BillVO billVO_tree[] = billTreePanel.getCheckedVOs();
		DefaultMutableTreeNode[] billVO_trees = billTreePanel.getCheckedNodes();
		ids = new StringBuilder();
		if (billVO_tree != null) {
			for (int i = 0; i < billVO_tree.length; i++) {
				if (i == billVO_tree.length - 1) {
					ids.append(billVO_tree[i].getStringValue("id"));
				} else {
					ids.append(billVO_tree[i].getStringValue("id"));
					ids.append(",");
				}
			}
			if (ids.length() > 0) {
				String sql = null;
				if (isSingleSelect) {//判断是单选的还是多选的
					sql = " deptid in( " + ids + ")";
				} else {
					String role = null;
					if (isrolelinked) {
						role = billCardPanel.getBillVO().getStringValue("rolechoose");
					}
					if (role != null && !role.equals("")) {//判断是否选择角色过滤
						roleids = new StringBuilder(new TBUtil().getInCondition(role));
					} else {
						roleids = null;
					}
					if (roleids == null || roleids.equals("")) {
						sql = " deptid in( " + ids + ")";
					} else {
						sql = "deptid in( " + ids + ") and userid in( select userid from pub_user_role where roleid in  (" + roleids.toString() + "))";
					}
				}

				BillVO billVOs[];
				try {
					billVOs = billListPanel.queryBillVOsByCondition(sql);
					if (billVOs.length < count) {//判断是否超过人员显示上限
						billListPanel.QueryDataByCondition(sql);
						billVO_oldtree = billTreePanel.getCheckedNodes();
					} else {
						billTreePanel.clearSelection();
						MessageBox.show(this, "您选择的人数已超过" + count + "人，请重新选择部门");
						ids = new StringBuilder();
						if (billVO_oldtree != null && !billVO_oldtree.equals("")) {
							for (int k = 0; k < billVO_trees.length; k++) {
								boolean ishave = true;
								for (int j = 0; j < billVO_oldtree.length; j++) {
									if (billVO_trees[k].equals(billVO_oldtree[j])) {
										((BillTreeDefaultMutableTreeNode) billVO_trees[k]).setChecked(true);
										ishave = false;
										ids.append(billVO_tree[k].getStringValue("id") + ",");
										break;
									}
								}
								if (ishave) {
									((BillTreeDefaultMutableTreeNode) billVO_trees[k]).setChecked(false);
								}
							}
						} else {
							for (int k = 0; k < billVO_tree.length; k++) {
								((BillTreeDefaultMutableTreeNode) billVO_trees[k]).setChecked(false);
							}
						}
						if (ids.length() > 0) {
							ids.append("-99999");
						}
						billTreePanel.repaint();
					}
				} catch (Exception e) {
					MessageBox.showException(this, e);
					return;
				}
			} else {
				billListPanel.removeAllRows();//清空人员列表数据
				billVO_oldtree = billTreePanel.getCheckedNodes();
			}
		}
	}

	public void onBillCardValueChanged(BillCardEditEvent _evt) {
		String role = null;
		if (isrolelinked) {
			role = billCardPanel.getBillVO().getStringValue("rolechoose");
		}

		String sql = null;
		if (role != null && !role.equals("")) {//判断是否选择角色过滤
			if (ids != null && ids.length() > 0) {//判断是否选择部门
				roleids = new StringBuilder(new TBUtil().getInCondition(role));
				sql = "deptid in( " + ids + ") and userid in( select userid from pub_user_role where roleid in (" + roleids.toString() + "))";
				try {
					BillVO billVO[] = billListPanel.queryBillVOsByCondition(sql);
					if (billVO.length < count) {
						billListPanel.QueryDataByCondition(sql); //
						roles = billCardPanel.getBillVO().getStringViewValue("rolechoose");
						roleid = role;
					} else {
						MessageBox.show(this, "您选择的人数已超过" + count + "人，请重新选择部门");
						RefItemVO itemvo = new RefItemVO(roleid, null, roles);
						billCardPanel.setValueAt("rolechoose", itemvo);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				roles = billCardPanel.getBillVO().getStringViewValue("rolechoose");
				roleid = role;
			}
		} else {
			sql = "deptid in( " + ids + ")";
			try {
				BillVO billVO[] = billListPanel.queryBillVOsByCondition(sql);
				if (billVO.length < count) {//清空角色过滤时，判断所选部门里面的人员是否超过配置的显示上限
					billListPanel.QueryDataByCondition(sql);
				} else {
					MessageBox.show(this, "您选择的人数已超过" + count + "人，请重新选择部门");
					if (isrolelinked) {
						RefItemVO itemvo = new RefItemVO(roleid, null, roles);
						billCardPanel.setValueAt("rolechoose", itemvo);
					}

				}
			} catch (Exception e) {
				return;
			}
		}
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (billTreePanel.getSelectedNode().isRoot()) {
			return;
		}
		BillVO billvo = billTreePanel.getSelectedVO();
		if (billvo == null) {
			return;
		}
		billListPanel.QueryDataByCondition("deptid=" + billvo.getStringValue("id"));
	}
}
