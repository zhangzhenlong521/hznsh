package com.pushworld.ipushgrc.ui.wfrisk;

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
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
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
 * 操作要求 里的岗位参照(可设置机构和职责)
 */
public class ChooseOnePostRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener, BillListSelectListener {
	private WLTButton btn_confirm;
	private WLTButton btn_cancel;
	private BillCardPanel billcard;
	private BillTreePanel billTree_dept; //..
	private BillListPanel billList_post, billList_task, list_postgroupduty, list_postgroup; //..
	private RefItemVO refItemVO;
	private WLTTabbedPane tabPane = new WLTTabbedPane();

	public ChooseOnePostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
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
		billList_post.setAllBillListBtnVisiable(false);

		billList_post.addBillListSelectListener(this);
		billList_post.repaintBillListButton();

		billList_task = new BillListPanel("CMP_POSTDUTY_CODE1");

		WLTSplitPane splitPane1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billList_post, billList_task); // 左右的分割条

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, splitPane1); // 左右的分割条
		splitPane.setDividerLocation(220); //
		splitPane.setDividerSize(2);
		WLTPanel panel = new WLTPanel(new BorderLayout());
		panel.add(splitPane, BorderLayout.CENTER);
		panel.add(southPanel, BorderLayout.SOUTH);
		tabPane.addTab("机构岗位", panel);
		list_postgroup = new BillListPanel("PUB_POST_CODE2"); //岗位
		list_postgroup.QueryDataByCondition(" deptid is null ");
		list_postgroup.setDataFilterCustCondition(" deptid is null ");
		list_postgroup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_postgroup.addBillListSelectListener(this);
		list_postgroupduty = new BillListPanel(billList_task.getTempletVO());
		WLTSplitPane splitp = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, list_postgroup, list_postgroupduty);
		tabPane.addTab("岗位组", splitp);
		this.getContentPane().add(tabPane, BorderLayout.CENTER);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (btn_confirm == e.getSource()) {
			onConfirm();//确定！
		} else if (btn_cancel == e.getSource()) {
			onCancel(); //取消
		}
	}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose();
	}

	private void onConfirm() {
		BillVO[] billvos = null; //选择的岗责
		if (tabPane.getSelectedIndex() == 0) { //如果选择的是岗位机构页签
			BillVO billvo = billList_post.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(this, "请选择一条岗位记录！");
				return;
			}
			billvos = billList_task.getSelectedBillVOs();
			HashVO hashvo = new HashVO();
			hashvo.setAttributeValue("id", new StringItemVO(billvo.getStringValue("id")));
			hashvo.setAttributeValue("code", new StringItemVO(""));
			hashvo.setAttributeValue("name", new StringItemVO(billvo.getStringValue("name")));
			refItemVO = new RefItemVO(hashvo); // 设置返回的参照！
		} else { //如果是岗位组页签
			BillVO billvo = list_postgroup.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(this, "请选择一条岗位组记录！");
				return;
			}
			billvos = list_postgroupduty.getSelectedBillVOs();
			HashVO hashvo = new HashVO();
			hashvo.setAttributeValue("id", new StringItemVO(billvo.getStringValue("id")));
			hashvo.setAttributeValue("code", new StringItemVO(""));
			hashvo.setAttributeValue("name", new StringItemVO(billvo.getStringValue("name")));
			refItemVO = new RefItemVO(hashvo); //设置返回的参照值
		}
		if (billvos == null || billvos.length == 0) { //判断是否选择了岗责！
			billcard.setValueAt("postduty", new StringItemVO(""));//岗位职责
			billcard.setValueAt("task", null);//工作任务
		} else {
			StringBuffer sb_task = new StringBuffer(";");
			sb_task.append(billvos[0].getStringValue("id"));
			sb_task.append(";");

			StringBuffer sb_taskname = new StringBuffer();
			String task = billvos[0].getStringValue("task", "");
			if (task != null && !"".equals(task.trim())) {//【李春娟/2014-10-10】
				sb_taskname.append(task);
				sb_taskname.append(";");
			}
			for (int i = 0; i < billvos.length - 1; i++) {
				if (billvos[i].getStringValue("dutyname") != null && !billvos[i].getStringValue("dutyname").equals(billvos[i + 1].getStringValue("dutyname"))) {
					MessageBox.show(this, "请选择相同岗位职责的工作任务！");
					return;
				}
				sb_task.append(billvos[i + 1].getStringValue("id"));
				sb_task.append(";");

				task = billvos[i + 1].getStringValue("task", "");
				if (task != null && !"".equals(task.trim())) {
					sb_taskname.append(task);
					sb_taskname.append(";");
				}
			}
			billcard.setValueAt("postduty", new StringItemVO(billvos[0].getStringValue("dutyname")));//岗位职责
			billcard.setValueAt("task", new RefItemVO(sb_task.toString(), "", sb_taskname.toString()));//工作任务
		}
		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billList_post.queryDataByCondition("deptid='" + _event.getCurrSelectedVO().getStringValue("id") + "'", "seq");
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == billList_post) {
			BillVO billvo = billList_post.getSelectedBillVO();
			if (billvo == null) {
				return;
			}
			billList_task.QueryDataByCondition(" postid=" + billvo.getStringValue("id"));
		} else {
			BillVO billvo = list_postgroup.getSelectedBillVO();
			if (billvo == null) {
				return;
			}
			list_postgroupduty.QueryDataByCondition(" postid=" + billvo.getStringValue("id"));
		}
	}

}
