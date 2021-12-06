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
 * ����Ҫ�� ��ĸ�λ����(�����û�����ְ��)
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
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(btn_confirm);
		southPanel.add(btn_cancel);

		billTree_dept = new BillTreePanel("PUB_CORP_DEPT_1"); // ����
		billTree_dept.queryDataByCondition(null); //
		billTree_dept.addBillTreeSelectListener(this);

		billList_post = new BillListPanel("PUB_POST_CODE1");
		billList_post.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		billList_post.getQuickQueryPanel().setVisible(false);
		billList_post.setAllBillListBtnVisiable(false);

		billList_post.addBillListSelectListener(this);
		billList_post.repaintBillListButton();

		billList_task = new BillListPanel("CMP_POSTDUTY_CODE1");

		WLTSplitPane splitPane1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billList_post, billList_task); // ���ҵķָ���

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, splitPane1); // ���ҵķָ���
		splitPane.setDividerLocation(220); //
		splitPane.setDividerSize(2);
		WLTPanel panel = new WLTPanel(new BorderLayout());
		panel.add(splitPane, BorderLayout.CENTER);
		panel.add(southPanel, BorderLayout.SOUTH);
		tabPane.addTab("������λ", panel);
		list_postgroup = new BillListPanel("PUB_POST_CODE2"); //��λ
		list_postgroup.QueryDataByCondition(" deptid is null ");
		list_postgroup.setDataFilterCustCondition(" deptid is null ");
		list_postgroup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_postgroup.addBillListSelectListener(this);
		list_postgroupduty = new BillListPanel(billList_task.getTempletVO());
		WLTSplitPane splitp = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, list_postgroup, list_postgroupduty);
		tabPane.addTab("��λ��", splitp);
		this.getContentPane().add(tabPane, BorderLayout.CENTER);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (btn_confirm == e.getSource()) {
			onConfirm();//ȷ����
		} else if (btn_cancel == e.getSource()) {
			onCancel(); //ȡ��
		}
	}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose();
	}

	private void onConfirm() {
		BillVO[] billvos = null; //ѡ��ĸ���
		if (tabPane.getSelectedIndex() == 0) { //���ѡ����Ǹ�λ����ҳǩ
			BillVO billvo = billList_post.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(this, "��ѡ��һ����λ��¼��");
				return;
			}
			billvos = billList_task.getSelectedBillVOs();
			HashVO hashvo = new HashVO();
			hashvo.setAttributeValue("id", new StringItemVO(billvo.getStringValue("id")));
			hashvo.setAttributeValue("code", new StringItemVO(""));
			hashvo.setAttributeValue("name", new StringItemVO(billvo.getStringValue("name")));
			refItemVO = new RefItemVO(hashvo); // ���÷��صĲ��գ�
		} else { //����Ǹ�λ��ҳǩ
			BillVO billvo = list_postgroup.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(this, "��ѡ��һ����λ���¼��");
				return;
			}
			billvos = list_postgroupduty.getSelectedBillVOs();
			HashVO hashvo = new HashVO();
			hashvo.setAttributeValue("id", new StringItemVO(billvo.getStringValue("id")));
			hashvo.setAttributeValue("code", new StringItemVO(""));
			hashvo.setAttributeValue("name", new StringItemVO(billvo.getStringValue("name")));
			refItemVO = new RefItemVO(hashvo); //���÷��صĲ���ֵ
		}
		if (billvos == null || billvos.length == 0) { //�ж��Ƿ�ѡ���˸���
			billcard.setValueAt("postduty", new StringItemVO(""));//��λְ��
			billcard.setValueAt("task", null);//��������
		} else {
			StringBuffer sb_task = new StringBuffer(";");
			sb_task.append(billvos[0].getStringValue("id"));
			sb_task.append(";");

			StringBuffer sb_taskname = new StringBuffer();
			String task = billvos[0].getStringValue("task", "");
			if (task != null && !"".equals(task.trim())) {//�����/2014-10-10��
				sb_taskname.append(task);
				sb_taskname.append(";");
			}
			for (int i = 0; i < billvos.length - 1; i++) {
				if (billvos[i].getStringValue("dutyname") != null && !billvos[i].getStringValue("dutyname").equals(billvos[i + 1].getStringValue("dutyname"))) {
					MessageBox.show(this, "��ѡ����ͬ��λְ��Ĺ�������");
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
			billcard.setValueAt("postduty", new StringItemVO(billvos[0].getStringValue("dutyname")));//��λְ��
			billcard.setValueAt("task", new RefItemVO(sb_task.toString(), "", sb_taskname.toString()));//��������
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
