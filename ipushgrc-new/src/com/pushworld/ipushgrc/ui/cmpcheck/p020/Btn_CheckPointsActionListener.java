package com.pushworld.ipushgrc.ui.cmpcheck.p020;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;

public class Btn_CheckPointsActionListener implements WLTActionListener {
	BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			MessageBox.show(cardPanel, "���״̬������ִ�д˲�����");
			return;
		}
		onRefCheckPoints();
	}

	/**
	 * ��� �ο����Ҫ�� ��ť���߼��������ѵ���������ļ� ���� ���Ҫ����塣ѡ����Ҫ�� ����
	 * [�ļ�����][��������][����][���Ҫ��˵��][���󷽷�] ��ʽ�����ַŵ� ���Ҫ��˵���ֶ��С� hm
	 */
	private void onRefCheckPoints() {
		if (cardPanel != null) {
			CardCPanel_ChildTable childTable = (CardCPanel_ChildTable) cardPanel.getCompentByKey("items");
			if (childTable.getBillListPanel().getBillVOs().length == 0) {
				MessageBox.show(cardPanel, "���ڼ����Ŀ��������ļ���");
				return;
			} else {
				BillVO[] vos = childTable.getBillListPanel().getBillVOs();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < vos.length; i++) {
					String cmpfileID = vos[i].getStringValue("cmp_cmpfile_id");
					if ((i == 0 || sb.length() == 0) && cmpfileID != null && !cmpfileID.equals("")) {
						sb.append("'" + cmpfileID + "'");
					} else if (cmpfileID != null && !cmpfileID.equals("")) {
						sb.append(",'" + cmpfileID + "'");
					}
				}
				if (sb.length() == 0) {
					sb.append("'-99999'");
				}
				BillListDialog listDialog = new BillListDialog(cardPanel, "���Ҫ��", "CMP_CMPFILE_CHECKPOINT_CODE1", 650, 400);
				BillListPanel listpanel = listDialog.getBilllistPanel();
				listpanel.QueryDataByCondition(" cmpfile_id in (" + sb.toString() + ")");
				if (listpanel.getRowCount() == 0) {
					listDialog.dispose();
					MessageBox.show(cardPanel, "�����Ŀ�ж�Ӧ�������ļ�û�м��Ҫ��!");//���û�м��Ҫ�㣬Ҫ������ʾ������Ҫ�����տյ��б����/2012-03-28��
					return;
				}
				listpanel.setItemVisible("checktype_name", false);
				listpanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
				listpanel.repaintBillListButton();
				listDialog.setVisible(true);
				if (listDialog.getCloseType() != 1) {
					return;
				}
				BillVO[] returnVOs = listDialog.getReturnBillVOs();
				StringBuffer pointsb = new StringBuffer();
				for (int i = 0; i < returnVOs.length; i++) {
					if (returnVOs[i].getStringValue("checkitem_point") != null) {
						pointsb.append("[" + returnVOs[i].getStringValue("cmpfile_name", "��") + "]�ļ�-[" + returnVOs[i].getStringValue("wfprocess_name", "��") + "]����-[" + returnVOs[i].getStringValue("wfactivity_name", "��") + "]����\r\n");
						pointsb.append(returnVOs[i].getStringValue("checkitem_point") + "\r\n");
					}
				}
				if (pointsb.length() > 0) {
					String str = cardPanel.getBillVO().getStringValue("checkpoints");
					if (str == null || str.equals("")) {
						cardPanel.setValueAt("checkpoints", new StringItemVO(pointsb.toString()));
					} else {
						cardPanel.setValueAt("checkpoints", new StringItemVO(str + "\r\n" + pointsb.toString()));
					}
				}
			}
		}
	}
}
