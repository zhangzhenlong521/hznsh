package com.pushworld.ipushlbs.ui.powermanage.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
/**
 * 自定义参照 左侧机构树，右侧上方岗位列表，下方人员列表，选择后自动填充机构人员两列
 * 受权人员 
 * @author yinliang
 * @since 2011.12.14
 */
public class ChooseOnePersonRefDialog2 extends AbstractRefDialog implements ActionListener, BillTreeSelectListener, BillListSelectListener {
	private WLTButton btn_confirm; // 执行按钮
	private WLTButton btn_cancel; // 取消按钮
	private BillCardPanel billcard;
	private BillTreePanel billTree_dept; // 机构树
	private BillListPanel billList_post, billList_user; // ..岗位列表 用户列表
	private RefItemVO refItemVO;

	public ChooseOnePersonRefDialog2(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		this.billcard = (BillCardPanel) panel;
		this.refItemVO = refItemVO;
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return refItemVO;
	}

	@Override
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		this.setSize(800, 600);
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(btn_confirm);
		southPanel.add(btn_cancel);

		billTree_dept = new BillTreePanel("PUB_CORP_DEPT_1"); // 机构
		billTree_dept.queryDataByCondition(null); //
		billTree_dept.addBillTreeSelectListener(this);

		billList_post = new BillListPanel("PUB_POST_CODE1");
		billList_post.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		billList_post.getQuickQueryPanel().setVisible(false);
		billList_post.getBillListBtnPanel().setVisible(false);
		billList_post.addBillListSelectListener(this);

		billList_user = new BillListPanel("PUB_USER_POST_DEFAULT"); // 用户 岗位 参照

		WLTSplitPane splitPane1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billList_post, billList_user); // 上下的分割条

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, splitPane1); // 左右的分割条
		splitPane.setDividerLocation(220); //
		splitPane.setDividerSize(2);
		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (btn_confirm == e.getSource()) {
			onConfirm();
		} else if (btn_cancel == e.getSource()) {
			onCancel();
		}
	}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose();
	}

	private void onConfirm() {
		// 判断是否选择了机构
		BillVO billvo_ = billTree_dept.getSelectedVO();
		if (billvo_ == null) {
			MessageBox.show(this, "请选择一个机构！");
			return;
		}
		// 判断是否选择了岗位
		BillVO billvo = billList_post.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.show(this, "请选择一条岗位记录！");
			return;
		}
		// 判断是否选择了人员
		BillVO _billvo = billList_user.getSelectedBillVO();
		if (_billvo == null) {
			MessageBox.show(this, "请选择一条人员信息！");
			return;
		} else {
			billcard.setValueAt("ACCEPTER", new RefItemVO(_billvo.getStringValue("userid"), "", _billvo.getStringValue("username")));//
		}

		HashVO hashvo = new HashVO();
		hashvo.setAttributeValue("id", new StringItemVO(billvo_.getStringValue("id")));
		hashvo.setAttributeValue("code", new StringItemVO(""));
		hashvo.setAttributeValue("name", new StringItemVO(billvo_.getStringValue("name")));
		refItemVO = new RefItemVO(hashvo); //

		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billList_post.queryDataByCondition("deptid='" + _event.getCurrSelectedVO().getStringValue("id") + "'", "seq");
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO billvo = billList_post.getSelectedBillVO();
		if (billvo == null) {
			return;
		}
		billList_user.QueryDataByCondition("postid=" + billvo.getStringValue("id"));
	}
}
