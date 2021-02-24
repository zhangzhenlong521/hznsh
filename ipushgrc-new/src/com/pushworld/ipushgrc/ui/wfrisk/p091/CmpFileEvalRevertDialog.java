package com.pushworld.ipushgrc.ui.wfrisk.p091;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.sysapp.login.RoleVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * �����ļ����ۻظ��Ĵ���!! Ҫģ��BBSЧ��,���ؼ����и�!!
 * 
 * @author xch
 * 
 */
public class CmpFileEvalRevertDialog extends BillDialog implements ActionListener, BillListSelectListener {

	private BillListPanel billList_evel_b; //

	private BillCardPanel billCard_evel_b;

	private String CMPFILE_EVAL_ID;

	private WLTButton btn_cancel = WLTButton.createButtonByType(WLTButton.COMM, "����");

	private WLTButton btn_revert = WLTButton.createButtonByType(WLTButton.COMM, "�ظ�");

	private WLTButton btn_update = new WLTButton("�޸�");

	private WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);

	private String loginUserID = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // ��½��ԱID

	private RoleVO[] roles = ClientEnvironment.getCurrLoginUserVO().getRoleVOs();

	public CmpFileEvalRevertDialog(Container _parent, String file_eva_id, int _width, int _height) {
		super(_parent, "�ظ�--����״̬", _width, _height); //
		CMPFILE_EVAL_ID = file_eva_id;
		initialize(); //
	}

	private void initialize() {
		billList_evel_b = new BillListPanel("CMPFILE_EVAL_REPLY_CODE1"); //
		btn_update.addActionListener(this);
		btn_delete.addActionListener(this);
		billList_evel_b.addBatchBillListButton(new WLTButton[] { btn_update, btn_delete });
		billList_evel_b.repaintBillListButton();
		billList_evel_b.queryDataByCondition(" cmpfile_eval_id = " + CMPFILE_EVAL_ID, " revertdate asc");
		billList_evel_b.setDataFilterCustCondition(" cmpfile_eval_id = " + CMPFILE_EVAL_ID);
		billList_evel_b.setOrderCustCondition(" revertdate asc ");
		billList_evel_b.addBillListSelectListener(this);
		billCard_evel_b = new BillCardPanel("CMPFILE_EVAL_REPLY_CODE1");
		billCard_evel_b.insertRow(); // ��ʼ�����ʱ��Ĭ���������ظ���
		billCard_evel_b.setEditableByEditInit();
		billCard_evel_b.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_evel_b, billCard_evel_b);
		split.setDividerLocation(280);
		WLTPanel btnpanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(), LookAndFeel.defaultShadeColor1, false);
		btn_revert.addActionListener(this);
		btn_cancel.addActionListener(this);
		btn_revert.setEnabled(false);
		btn_revert.setEnabled(true);
		btnpanel.add(btn_revert);
		btnpanel.add(btn_cancel);
		billCard_evel_b.setValueAt("cmpfile_eval_id", new StringItemVO(CMPFILE_EVAL_ID));
		this.getContentPane().add(split, BorderLayout.CENTER); //
		this.getContentPane().add(btnpanel, BorderLayout.SOUTH); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_cancel) {
			this.dispose();
		} else if (obj == btn_revert) { // �ظ���
			onRevert();
		} else if (obj == btn_update) { // �޸Ļظ�����
			onUpdate();
		} else if (obj == btn_delete) { // ɾ���ظ���
			onDelete();
		}
	}

	/*
	 * �޸����۵Ļظ���
	 */
	private void onUpdate() {
		if (billList_evel_b.getSelectedBillVO() == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		setBillCardState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
	}

	/*
	 * �ظ��ƶ�����
	 */
	private void onRevert() {
		try {
			if (!billCard_evel_b.checkValidate()) {
				return;
			}
			if (billCard_evel_b.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
				billCard_evel_b.updateData(); // ����ִ������BillCardPanel״̬����Ϊupdate�����з��������������
				billList_evel_b.refreshCurrSelectedRow();
				MessageBox.show(this, "�޸ĳɹ���");
			} else {
				billCard_evel_b.updateData();
				billList_evel_b.insertRow(billList_evel_b.getRowCount(), billCard_evel_b.getBillVO());

				//billList_evel_b.setSelectedRow(billList_evel_b.getRowCount() - 1);
				billList_evel_b.refreshData();
				MessageBox.show(this, "�ظ��ɹ���");
			}
			setBillCardState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	// ���ÿ�Ƭ״̬��
	public void setBillCardState(String type) {
		if (type.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			this.setTitle("�����ļ��ظ�");
			billCard_evel_b.insertRow();
			billCard_evel_b.setValueAt("cmpfile_eval_id", new StringItemVO(CMPFILE_EVAL_ID));
			billCard_evel_b.setEditableByInsertInit();
			billCard_evel_b.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT); // ���������޸��꣬Ĭ��Ϊѡ��״̬��
		} else {
			this.setTitle("�����ļ��ظ�--�޸�");
			billCard_evel_b.setBillVO(billList_evel_b.getSelectedBillVO());
			billCard_evel_b.setEditableByEditInit();
			billCard_evel_b.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		}
	}

	private void onDelete() {
		if (billList_evel_b.getSelectedBillVO() == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫɾ���˼�¼��") == JOptionPane.YES_OPTION) {
			try {
				UIUtil.executeBatchByDS(null, new String[] { "delete from cmpfile_eval_reply where id = '" + billList_evel_b.getSelectedBillVO().getStringValue("id") + "'" });
				billList_evel_b.removeSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (billCard_evel_b.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) { // ����Ǹ���״̬����Ҫɾ��������
			setBillCardState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getCurrSelectedVO() == null) {
			return;
		}
		String selectUserID = _event.getCurrSelectedVO().getStringValue("revertuserid");
		if (billCard_evel_b.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) { // ����Ǹ���״̬�����������ݴ��ڡ������
			setBillCardState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		} else { // �����insert״̬ ��ô����Ҫ���κβ����� ����ID������

		}
		if (loginUserID.equals(selectUserID)) {
			btn_update.setEnabled(true);
			btn_delete.setEnabled(true);
		} else {
			btn_update.setEnabled(false);
			btn_delete.setEnabled(false);
		}
	}

	public BillListPanel getBillListPanel() {
		return billList_evel_b;
	}

}
