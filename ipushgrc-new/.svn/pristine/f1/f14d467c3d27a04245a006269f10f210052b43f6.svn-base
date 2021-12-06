package com.pushworld.ipushgrc.ui.icheck.ref;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;

/**
 * 左边机构树，右边人员列表加入多选【李春娟/2016-09-09】
 * @author lcj
 *
 */
public class RefDialog_Corp_Users extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {

	
	private BillFormatPanel billFormatPanel = null; //
	private BillTreePanel billTreePanel = null; //
	private BillListPanel billListPanel = null; //
	private BillListPanel billListPanel_add = null; //
	private BillCardPanel billCardPanel = null;
	private WLTButton btn_confirm, btn_cancel, btn_adduser, btn_deleteuser;
	private RefItemVO returnRefItemVO = null; //
	private Container parent;
	private String itemKey;
	private WLTTabbedPane tabbedPane = null; 
	private WLTSplitPane splitPanel_all = null;//[zzl 2017-11-23]昌吉提出想有人员定位这个功能故加上
	private Boolean canSearch=TBUtil.getTBUtil().getSysOptionBooleanValue(
			"人员扁平查看是否显示", true);;
	private BillListPanel billListPanel_user_flat = null; // 人员扁平查看
	private WLTButton btn_gotodept, btn_edituser, btn_lookdeptinfo2;// 扁平查看的系列按钮
	private final static String MSG_NEED_USER = "请选择一个人员!";
	

	public RefDialog_Corp_Users(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		billCardPanel = (BillCardPanel) panel;
		parent = _parent;
		CardCPanel_Ref ref = (CardCPanel_Ref) parent;
		itemKey = ref.getItemKey();
		BillCardPanel cardPanel = (BillCardPanel) panel;
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billFormatPanel = new BillFormatPanel("getSplit(getTree(\"PUB_CORP_DEPT_CODE1\"),getSplit(getList(\"PUB_USER_POST_DEFAULT\"),getList(\"PUB_USER_POST_DEFAULT_1\"),\"上下\",200),\"左右\",225)"); //
		billTreePanel = billFormatPanel.getBillTreePanel(); //
		billListPanel = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT"); //
		billListPanel_add = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT_1"); //
		RefItemVO refItemVO = (RefItemVO) billCardPanel.getValueAt(itemKey);
		if (refItemVO != null) {
			String teamuser = refItemVO.getId();
			if (teamuser != null && !"".equals(teamuser)) {
				String teamusers[] = teamuser.split(";");
				for (int i = 0; i < teamusers.length; i++) {
					try {
						if (teamusers[i] != null && !"".equals(teamusers[i])) {
							String user_id = teamusers[i];
							BillVO[] hashvo_user = UIUtil.getBillVOsByDS(null, "select * from v_pub_user_post_1 where userid=" + user_id + "", billListPanel_add.getTempletVO());
							billListPanel_add.addRow(hashvo_user[0]);
						}
					} catch (WLTRemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		btn_adduser = new WLTButton("加入");
		btn_deleteuser = new WLTButton("删除");
		btn_deleteuser.addActionListener(this);
		btn_adduser.addActionListener(this);
		billListPanel_add.addBatchBillListButton(new WLTButton[] { btn_adduser, btn_deleteuser }); //
		billListPanel_add.repaintBillListButton(); // 重绘按钮!
		billListPanel.getQuickQueryPanel().setVisible(false); //
		billListPanel.setAllBillListBtnVisiable(false); //

		billTreePanel.queryDataByCondition("1=1");
		billTreePanel.addBillTreeSelectListener(this); //
		splitPanel_all = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,
				 billListPanel,billListPanel_add); //
		splitPanel_all.setDividerLocation(175); //
		WLTSplitPane splitPanel_all2 = new WLTSplitPane(
				WLTSplitPane.HORIZONTAL_SPLIT,billTreePanel,
				splitPanel_all); //
		splitPanel_all2.setDividerLocation(175); //
		if (canSearch) {//如果配置为可以扁平查看，则绘制面板
			tabbedPane = new WLTTabbedPane(); //
			tabbedPane.addTab("信息维护", UIUtil.getImage("office_134.gif"), splitPanel_all2); //
			tabbedPane.addTab("人员查找", UIUtil.getImage("office_194.gif"), this.getUserFlatPanel()); //
			this.add(tabbedPane); //
		} else {//没有扁平查看，直接加入维护界面
			this.add(splitPanel_all2); //

		}
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
		
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

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			BillVO[] billVO_list = billListPanel_add.getAllBillVOs();
			if (billVO_list == null || billVO_list.length < 1) {
				MessageBox.show(this, "请加入人员!"); //
				return;
			}
			StringBuilder userids = new StringBuilder(";");
			StringBuilder useridnames = new StringBuilder(";");
			for (int i = 0; i < billVO_list.length; i++) {
				HashVO hvo = billVO_list[i].convertToHashVO(); //
				userids.append(hvo.getStringValue("userid") + ";");
				useridnames.append(hvo.getStringValue("username") + ";");
			}
			returnRefItemVO = new RefItemVO(); //
			returnRefItemVO.setId(userids.toString()); //
			returnRefItemVO.setCode(null);
			returnRefItemVO.setName(useridnames.toString()); //
			this.setCloseType(BillDialog.CONFIRM); //
			this.dispose(); //
		} else if (e.getSource() == btn_cancel) {
			returnRefItemVO = null; //
			this.setCloseType(BillDialog.CANCEL); //
			this.dispose(); //
		} else if (e.getSource() == btn_adduser) {
			BillVO[] billVO_list = billListPanel.getSelectedBillVOs();
			if (billVO_list == null || billVO_list.length < 1) {
				MessageBox.show(this, "请选择人员!"); //
				return;
			}
			BillVO[] billVO_addlist = billListPanel_add.getAllBillVOs();
			if (billVO_addlist == null || billVO_addlist.length < 1) {
				for (int i = 0; i < billVO_list.length; i++) {
					billListPanel_add.addRow(billVO_list[i]);
				}
			} else {
				for (int i = 0; i < billVO_list.length; i++) {
					String userid = billVO_list[i].getStringValue("userid");
					Boolean add = true;
					for (int j = 0; j < billVO_addlist.length; j++) {
						if (userid.equals(billVO_addlist[j].getStringValue("userid"))) {
							add = false;
							break;
						}
					}
					if (add) {
						billListPanel_add.addRow(billVO_list[i]);
					}
				}
			}
		} else if (e.getSource() == btn_deleteuser) {
			BillVO[] billVO_addlist = billListPanel_add.getSelectedBillVOs(); //
			if (billVO_addlist == null) {
				MessageBox.show(this, "请选择人员!"); //
				return;
			}
			billListPanel_add.removeSelectedRows();
		}else if(e.getSource() == btn_gotodept){
			try {
				onGoToDept();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billVO_tree = billTreePanel.getSelectedVO(); //
		billListPanel.QueryDataByCondition("deptid='" + billVO_tree.getStringValue("id") + "'"); //
	}
}
