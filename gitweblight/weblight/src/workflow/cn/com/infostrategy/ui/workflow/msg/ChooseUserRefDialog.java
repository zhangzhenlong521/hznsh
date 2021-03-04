package cn.com.infostrategy.ui.workflow.msg;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

public class ChooseUserRefDialog extends AbstractRefDialog implements BillTreeSelectListener, ActionListener {

	private BillTreePanel billTree_corp = null;
	private BillListPanel billList_user = null;
	private BillListPanel billList_user_shopcar = null;
	private WLTButton btn_addShopCarUser, btn_addAllShopCarUser, btn_delShopCarUser, btn_delAllShopCarUser;
	private WLTButton btn_confirm, btn_cancel;
	private RefItemVO returnRefItemVO = null;
	private String depttempletCode = "PUB_CORP_DEPT_CODE1";
	private String usertemplet = "PUB_USER_POST_DEFAULT";

	public ChooseUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		this.returnRefItemVO = refItemVO;
	}

	public ChooseUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _depttempletCode, String _usertemplet) {
		super(_parent, _title, refItemVO, panel);
		if (_depttempletCode != null && !"".equals(_depttempletCode)) {
			depttempletCode = _depttempletCode;
		}
		if (_usertemplet != null && !"".equals(_usertemplet)) {
			usertemplet = _usertemplet;
		}
		returnRefItemVO = refItemVO;
	}

	public void initialize() {
		try {
			billTree_corp = new BillTreePanel(depttempletCode);
			billList_user = new BillListPanel(usertemplet);
			billList_user.setQuickQueryPanelVisiable(false);
			billList_user.getBillListBtnPanel().setVisible(true);
			billTree_corp.queryDataByCondition("1=1");
			billTree_corp.addBillTreeSelectListener(this);
			billList_user_shopcar = new BillListPanel(billList_user.getTempletVO().deepClone());
			billList_user_shopcar.setTitleLabelText("已选择的人员");
			billList_user_shopcar.setQuickQueryPanelVisiable(false);
			btn_addShopCarUser = new WLTButton("添加", "office_059.gif");
			btn_delShopCarUser = new WLTButton("删除", "office_081.gif");
			btn_addAllShopCarUser = new WLTButton("全部添加", "office_160.gif");
			btn_delAllShopCarUser = new WLTButton("全部删除", "office_125.gif");
			btn_addShopCarUser.setToolTipText("将上方选中的人员添加进来");
			btn_delShopCarUser.setToolTipText("将下方已选择的人员删除掉");
			btn_addShopCarUser.addActionListener(this);
			btn_delShopCarUser.addActionListener(this);
			btn_addAllShopCarUser.addActionListener(this);
			btn_delAllShopCarUser.addActionListener(this);
			billList_user_shopcar.addBatchBillListButton(new WLTButton[] { btn_addShopCarUser, btn_delShopCarUser, btn_addAllShopCarUser, btn_delAllShopCarUser });
			billList_user_shopcar.repaintBillListButton();
			WLTSplitPane splitPanel_2 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_user, billList_user_shopcar);
			splitPanel_2.setDividerLocation(this.getHeight() / 2);
			WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_corp, splitPanel_2);
			splitPanel.setDividerLocation(250);
			this.getContentPane().add(splitPanel, BorderLayout.CENTER);
			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billVO_tree = billTree_corp.getSelectedVO();
		if (billVO_tree != null) {
			billList_user.QueryDataByCondition(" deptid = '" + billVO_tree.getStringValue("id") + "'");
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_confirm) {
			onConfirm();
		} else if (_event.getSource() == btn_cancel) {
			onCancel();
		} else if (_event.getSource() == btn_addShopCarUser) {
			onAddShopCarUser();
		} else if (_event.getSource() == btn_delShopCarUser) {
			onDelShopCarUser();
		} else if (_event.getSource() == btn_addAllShopCarUser) {
			onAddAllShopCarUser();
		} else if (_event.getSource() == btn_delAllShopCarUser) {
			onDelAllShopCarUser();
		}
	}

	private void onConfirm() {
		BillVO[] selVOS = null;
		selVOS = billList_user_shopcar.getAllBillVOs();
		if (selVOS == null || selVOS.length == 0) {
			MessageBox.show(this, "请选择一个人员!");
			return;
		}
		StringBuilder userids = new StringBuilder(";");
		StringBuilder useridnames = new StringBuilder("");
		for (int i = 0; i < selVOS.length; i++) {
			userids.append(selVOS[i].getStringValue("userid") + ";");
			useridnames.append(selVOS[i].getStringValue("username") + ";");
		}
		returnRefItemVO = new RefItemVO(userids.toString(), "", useridnames.toString());
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose();
	}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL);
		this.dispose();
	}

	private void onAddShopCarUser() {
		BillVO[] billVOs = billList_user.getSelectedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "请从上方人员列表中选择一个或多个人员进行此操作!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	/**
	 * 全部添加
	 */
	private void onAddAllShopCarUser() {
		BillVO[] billVOs = billList_user.getAllBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "没有可以添加的人员,无法进行此操作!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	private void onAddShopCarUserByBillVOs(BillVO[] _billVOs) {
		HashSet hst = new HashSet();
		BillVO[] billVOs_shopCardUser = billList_user_shopcar.getAllBillVOs();
		if (billVOs_shopCardUser != null && billVOs_shopCardUser.length > 0) {
			for (int i = 0; i < billVOs_shopCardUser.length; i++) {
				String str_userid = billVOs_shopCardUser[i].getStringValue("userid");
				if (str_userid != null) {
					hst.add(str_userid);
				}
			}
		}
		StringBuilder sb_reduplicate_user = new StringBuilder();
		for (int i = 0; i < _billVOs.length; i++) {
			String str_userid = _billVOs[i].getStringValue("userid");
			String str_usercode = _billVOs[i].getStringValue("usercode");
			String str_username = _billVOs[i].getStringValue("username");
			if (hst.contains(str_userid)) {
				sb_reduplicate_user.append("【" + str_usercode + "/" + str_username + "】");
			}
		}
		String str_reduplicate_user = sb_reduplicate_user.toString();
		if (!str_reduplicate_user.equals("")) {
			MessageBox.show(this, "用户" + str_reduplicate_user + "已经添加过了,不能重复添加,请重新选择!");
			return;
		}
		billList_user_shopcar.addBillVOs(_billVOs);
	}

	private void onDelShopCarUser() {
		BillVO[] billVOs = billList_user_shopcar.getSelectedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "请从下方人员列表中选择一个或多个人员进行此操作!");
			return;
		}
		if (!MessageBox.confirm(this, "您确定要删除选中的人员吗?")) {
			return;
		}
		billList_user_shopcar.removeSelectedRows();
	}

	private void onDelAllShopCarUser() {
		int li_count = billList_user_shopcar.getRowCount();
		if (li_count <= 0) {
			MessageBox.show(this, "已选择的人员为空,无法进行此操作!");
			return;
		}
		if (!MessageBox.confirm(this, "您确定要删除所有已选择的人员么?")) {
			return;
		}
		billList_user_shopcar.clearTable();
	}

	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

}
