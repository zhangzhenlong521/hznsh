package cn.com.infostrategy.ui.sysapp.refdialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 岗位参照
 */
public class CommonOnePostRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {
	private WLTButton btn_confirm;
	private WLTButton btn_cancel;
	private BillTreePanel billTree_dept; //..
	private BillListPanel billList_post; //..
	private RefItemVO refItemVO;

	public CommonOnePostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		this.refItemVO = refItemVO;
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return refItemVO;
	}

	@Override
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		this.setSize(800, 500);
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
		billList_post.getQuickQueryPanel().setVisible(false);
		billList_post.getBillListBtnPanel().setVisible(false);

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, billList_post); // 左右的分割条
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
		BillVO billvo = billList_post.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.show(this, "请选择一条岗位记录！");
			return;
		}
		HashVO hashvo = new HashVO();
		hashvo.setAttributeValue("id", new StringItemVO(billvo.getStringValue("id")));
		hashvo.setAttributeValue("code", new StringItemVO(billvo.getStringValue("code")));
		hashvo.setAttributeValue("name", new StringItemVO(billvo.getStringValue("name")));
		refItemVO = new RefItemVO(hashvo); //
		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billList_post.queryDataByCondition("deptid='" + _event.getCurrSelectedVO().getStringValue("id") + "'", "seq");
	}
}
