package cn.com.infostrategy.ui.workflow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ��������Ȩ����
 * @author xch
 *
 */
public class WorkFlowAccrWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel billList;
	private WLTButton btn_add, btn_edit;
	private BillCardDialog carddialog;
	private BillCardPanel cardPanel;
	private BillVO returnvo;

	@Override
	public void initialize() {
		billList = new BillListPanel("PUB_WORKFLOWACCRPROXY_CODE1"); //
		btn_add = new WLTButton("����");
		btn_edit = new WLTButton("�޸�");
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		billList.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, WLTButton.createButtonByType(WLTButton.LIST_DELETE), WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		billList.repaintBillListButton();
		billList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//̫ƽ�ͻ�����ѡ�����ֻ��ɾ��һ����¼������ֱ������Ϊ��ѡ���ˡ����/2016-04-27��
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAdd();
		} else if (e.getSource() == btn_edit) {
			onEdit();
		} else {
			onConfirm();
		}

	}

	private void onConfirm() {
		try {
			cardPanel.stopEditing(); //
			if (!cardPanel.checkValidate()) {
				return;
			}
			returnvo = cardPanel.getBillVO();
			if (returnvo.getStringValue("ACCRENDTIME") != null && !returnvo.getStringValue("ACCRENDTIME").trim().equals("") && returnvo.getStringValue("ACCRBEGINTIME").compareTo(returnvo.getStringValue("ACCRENDTIME")) > 0) {
				MessageBox.show(carddialog, "��Ȩ��ʼ�����С����Ȩ������!");
				return;
			}
			cardPanel.updateData();
			carddialog.setCloseType(1);
			carddialog.dispose(); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}

	}

	private void onAdd() {
		cardPanel = new BillCardPanel(billList.templetVO); //����һ����Ƭ���
		cardPanel.insertRow(); //��Ƭ����һ��!

		carddialog = new BillCardDialog(billList, billList.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //������Ƭ������
		carddialog.getBtn_save().setVisible(false);
		carddialog.getBtn_confirm().addActionListener(this);

		carddialog.setVisible(true); //��ʾ��Ƭ����
		if (carddialog.getCloseType() == 1) { //�����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
			int li_newrow = billList.newRow(false); //
			billList.setBillVOAt(li_newrow, returnvo, false);
			billList.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.
			billList.setSelectedRow(li_newrow); //
		}
	}

	private void onEdit() {
		BillVO billvo = billList.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(carddialog);
			return;
		}
		cardPanel = new BillCardPanel(billList.templetVO); //����һ����Ƭ���
		cardPanel.insertRow(); //��Ƭ����һ��!
		cardPanel.setBillVO(billvo);

		carddialog = new BillCardDialog(billList, billList.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //������Ƭ������
		carddialog.getBtn_save().setVisible(false);
		carddialog.getBtn_confirm().addActionListener(this);
		carddialog.setVisible(true); //��ʾ��Ƭ����
		if (carddialog.getCloseType() == 1) { //�����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
			int li_row = billList.getSelectedRow();
			billList.setBillVOAt(li_row, returnvo, false);
			billList.setRowStatusAs(li_row, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.
			billList.setSelectedRow(li_row); //
		}
	}

}
