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
 * �Զ������ �����������Ҳ��Ϸ���λ�б��·���Ա�б�ѡ����Զ���������Ա����
 * ��Ȩ��Ա 
 * @author yinliang
 * @since 2011.12.14
 */
public class ChooseOnePersonRefDialog2 extends AbstractRefDialog implements ActionListener, BillTreeSelectListener, BillListSelectListener {
	private WLTButton btn_confirm; // ִ�а�ť
	private WLTButton btn_cancel; // ȡ����ť
	private BillCardPanel billcard;
	private BillTreePanel billTree_dept; // ������
	private BillListPanel billList_post, billList_user; // ..��λ�б� �û��б�
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
		billList_post.getBillListBtnPanel().setVisible(false);
		billList_post.addBillListSelectListener(this);

		billList_user = new BillListPanel("PUB_USER_POST_DEFAULT"); // �û� ��λ ����

		WLTSplitPane splitPane1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billList_post, billList_user); // ���µķָ���

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, splitPane1); // ���ҵķָ���
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
		// �ж��Ƿ�ѡ���˻���
		BillVO billvo_ = billTree_dept.getSelectedVO();
		if (billvo_ == null) {
			MessageBox.show(this, "��ѡ��һ��������");
			return;
		}
		// �ж��Ƿ�ѡ���˸�λ
		BillVO billvo = billList_post.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.show(this, "��ѡ��һ����λ��¼��");
			return;
		}
		// �ж��Ƿ�ѡ������Ա
		BillVO _billvo = billList_user.getSelectedBillVO();
		if (_billvo == null) {
			MessageBox.show(this, "��ѡ��һ����Ա��Ϣ��");
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
