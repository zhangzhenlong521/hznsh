package com.pushworld.ipushgrc.ui.icheck.p090;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;

/**
 * 【zzl/2017-01-19】
 * @author zzl
 *昌吉客户提出计划中选择了检查人员，方案中只显示计划中的人员
 */
public class CheckRefDialog_Corp_Users extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {

	
	private BillFormatPanel billFormatPanel = null; //
//	private BillTreePanel billTreePanel = null; //
	private BillListPanel billListPanel = null; //
	private BillListPanel billListPanel_add = null; //
	private BillCardPanel billCardPanel = null;
	private WLTButton btn_confirm, btn_cancel, btn_adduser, btn_deleteuser;
	private RefItemVO returnRefItemVO = null; //
	private Container parent;
	private String itemKey;

	public CheckRefDialog_Corp_Users(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
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
		String id=billCardPanel.getBillVO().getStringValue("planid");
		String teamusers=null;
		try {
			teamusers=UIUtil.getStringValueByDS(null,"select teamusers from CK_PLAN where id='"+id+"'");
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		teamusers=teamusers.replace(";", ",");
		teamusers=teamusers.substring(1,teamusers.length()-1);
		billFormatPanel = new BillFormatPanel("getSplit(getList(\"PUB_USER_POST_DEFAULT\"),getList(\"PUB_USER_POST_DEFAULT_1\"),\"上下\",200)"); //
//		billTreePanel = billFormatPanel.getBillTreePanel(); //
		billListPanel = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT"); //
		billListPanel.QueryData("select * from v_pub_user_post_1 where userid in("+teamusers+")");
		billListPanel_add = billFormatPanel.getBillListPanelByTempletCode("PUB_USER_POST_DEFAULT_1"); //
		RefItemVO refItemVO = (RefItemVO) billCardPanel.getValueAt(itemKey);
		if (refItemVO != null) {
			String teamuser = refItemVO.getId();
			if (teamuser != null && !"".equals(teamuser)) {
				String teamuserss[] = teamuser.split(";");
				for (int i = 0; i < teamuserss.length; i++) {
					try {
						if (teamuserss[i] != null && !"".equals(teamuserss[i])) {
							String user_id = teamuserss[i];
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

//		billTreePanel.queryDataByCondition("1=1");
//		billTreePanel.addBillTreeSelectListener(this); //
		this.add(billFormatPanel, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
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
		}

	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
//		BillVO billVO_tree = billTreePanel.getSelectedVO(); //
//		billListPanel.QueryDataByCondition("deptid='" + billVO_tree.getStringValue("id") + "'"); //
	}
}
