package com.pushworld.ipushgrc.ui.cmpscore.p020;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 自定义参照 左侧机构树，右侧上方岗位列表，下方人员列表，选择后自动填充机构人员两列
 * @author yinliang
 * @since  2011.12.14
 */
public class WChooseOnePersonRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {
	private WLTButton btn_confirm, btn_ok; //执行按钮
	private WLTButton btn_cancel, btn_cal; //取消按钮
	private BillCardPanel billcard;
	private BillTreePanel billTree_dept; // 机构树
	private BillListPanel billList_user, billList_event; //..岗位列表 用户列表
	private RefItemVO refItemVO;
	private WLTTabbedPane tabbedpane;

	public WChooseOnePersonRefDialog(Container parent, String title, RefItemVO refItemVO, BillPanel panel) {
		super(parent, title, refItemVO, panel);
		this.billcard = (BillCardPanel) panel;
		this.refItemVO = refItemVO;
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return refItemVO;
	}

	/**
	 * 加载
	 */
	@Override
	public void initialize() {
		JPanel panel_1 = new JPanel(); // 分割器所在的panel
		panel_1.setLayout(new BorderLayout());
		panel_1.setSize(800, 600);
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(btn_confirm);
		southPanel.add(btn_cancel);

		// 取得所有机构
		billTree_dept = new BillTreePanel("PUB_CORP_DEPT_1_score2");
		billTree_dept.queryDataByCondition(null); //
		billTree_dept.addBillTreeSelectListener(this);

		billList_user = new BillListPanel("PUB_USER_POST_DEFAULT_score2"); //用户 岗位 参照

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, billList_user); // 左右的分割条
		splitPane.setDividerLocation(220); //
		splitPane.setDividerSize(2);
		panel_1.add(splitPane, BorderLayout.CENTER);
		panel_1.add(southPanel, BorderLayout.SOUTH);

		JPanel panel_2 = new JPanel(); // 分割器所在的panel
		panel_2.setLayout(new BorderLayout());
		panel_2.setSize(800, 600);
		btn_ok = new WLTButton("确定");
		btn_cal = new WLTButton("取消");
		btn_ok.addActionListener(this);
		btn_cal.addActionListener(this);

		JPanel southPanel_2 = new JPanel();
		southPanel_2.setLayout(new FlowLayout(FlowLayout.CENTER));
		southPanel_2.add(btn_ok);
		southPanel_2.add(btn_cal);

		billList_event = new BillListPanel("CMP_WARDEVENT_USER_CODE2_1");

		WLTSplitPane splitPane_2 = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, null, billList_event); // 左右的分割条
		splitPane_2.setDividerSize(2);
		panel_2.add(splitPane_2, BorderLayout.CENTER);
		panel_2.add(southPanel_2, BorderLayout.SOUTH);

		tabbedpane = new WLTTabbedPane();
		if (billcard.getRealValueAt("eventid") != null && !"".equals(billcard.getRealValueAt("eventid"))) {
			tabbedpane.addTab("违规事件中选择", panel_2);
			billList_event.QueryDataByCondition(" CMP_WARDEVENT_ID = " + billcard.getRealValueAt("eventid"));
			//查询按钮加上数据查询过滤条件【杨庆/2012-08-09】
			billList_event.setDataFilterCustCondition(" CMP_WARDEVENT_ID = " + billcard.getRealValueAt("eventid"));
		}
		tabbedpane.addTab("机构直接选择", panel_1);
		this.add(tabbedpane);
	}

	public void actionPerformed(ActionEvent e) {
		if (btn_confirm == e.getSource()) {
			onConfirm(); //点击确定
		} else if (btn_cancel == e.getSource()) {
			onCancel(); //点击取消
		} else if (btn_ok == e.getSource()) {
			onConfirmOk(); //点击确定
		} else if (btn_cal == e.getSource()) {
			onCancel(); //点击取消
		}

	}

	/**
	 * 取消
	 */
	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose();
	}

	/**
	 * 执行
	 */
	private void onConfirm() {
		//判断是否选择了机构
		BillVO billvo_ = billTree_dept.getSelectedVO();
		if (billvo_ == null) {
			MessageBox.show(this, "请选择一个机构！");
			return;
		}
		//判断是否选择了人员
		BillVO[] _billvo = billList_user.getSelectedBillVOs();
		if (_billvo == null) {
			MessageBox.show(this, "请选择一条人员信息！");
			return;
		}
		StringBuffer sbuserid = new StringBuffer(); //选择人员buffer
		StringBuffer sbusername = new StringBuffer(); //选择人员buffer
		String sbdeptid = billvo_.getStringValue("id").toString();
		String sbdeptname = billvo_.getStringValue("name").toString();
		for (int i = 0; i < _billvo.length; i++) {
			if (i == _billvo.length - 1) {
				sbuserid.append(_billvo[i].getStringValue("userid"));
				sbusername.append(_billvo[i].getStringViewValue("username"));
			} else {
				sbuserid.append(_billvo[i].getStringValue("userid") + ";");
				sbusername.append(_billvo[i].getStringViewValue("username") + ";");
			}
		}
		billcard.setValueAt("userid", new RefItemVO(sbuserid.toString(), "", sbusername.toString())); //设置人员数据
		refItemVO = new RefItemVO(sbuserid.toString(), "", sbusername.toString());
		billcard.setValueAt("deptid", new RefItemVO(sbdeptid, "", sbdeptname)); //设置机构数据
		if (billcard.getTempletVO().containsItemKey("userdept"))
			billcard.setValueAt("userdept", new RefItemVO(sbdeptid, "", sbdeptname)); //设置机构数据
		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

	/*
	 * 从事件人员中选择后，点击确定
	 */
	private void onConfirmOk() {
		BillVO[] billvo = billList_event.getSelectedBillVOs();
		if (billvo.length == 0) {
			MessageBox.show(billList_event, "请选择人员信息!");
			return;
		}
		for (int i = 0; i < billvo.length; i++) {
			if ("是".equals(billvo[i].getStringValue("relstate"))) { //如果选中的数据里有已经处理的，那么提示不可以
				MessageBox.show(billList_event, "选择人员中存在被处理过的人员，请重新选择！");
				return;
			}
		}
		StringBuffer sbuserid = new StringBuffer(); //选择人员buffer
		StringBuffer sbusername = new StringBuffer(); //选择人员buffer
		StringBuffer sbdeptid = new StringBuffer(); //人员所属部门buffer
		StringBuffer sbdeptname = new StringBuffer(); //人员所属部门buffer
		for (int i = 0; i < billvo.length; i++) {
			if (i == billvo.length - 1) {
				sbuserid.append(billvo[i].getStringValue("username"));
				sbdeptid.append(billvo[i].getStringValue("userdept"));
				sbusername.append(billvo[i].getStringViewValue("username"));
				sbdeptname.append(billvo[i].getStringViewValue("userdept"));
			} else {
				sbuserid.append(billvo[i].getStringValue("username") + ";");
				sbdeptid.append(billvo[i].getStringValue("userdept") + ";");
				sbusername.append(billvo[i].getStringViewValue("username") + ";");
				sbdeptname.append(billvo[i].getStringViewValue("userdept") + ";");
			}
		}
		billcard.setValueAt("userid", new RefItemVO(sbuserid.toString(), "", sbusername.toString())); //设置人员数据
		billcard.setValueAt("deptid", new RefItemVO(sbdeptid.toString(), "", sbdeptname.toString())); //设置机构数据
		if (billcard.getTempletVO().containsItemKey("userdept"))
			billcard.setValueAt("userdept", new RefItemVO(sbdeptid.toString(), "", sbdeptname.toString())); //设置机构数据
		this.closeMe();
	}

	/**
	 * 选择树节点改变
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billList_user.QueryDataByCondition("deptid='" + _event.getCurrSelectedVO().getStringValue("id") + "'");
	}
}
