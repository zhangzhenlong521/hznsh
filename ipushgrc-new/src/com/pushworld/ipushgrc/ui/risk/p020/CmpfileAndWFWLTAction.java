package com.pushworld.ipushgrc.ui.risk.p020;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphFrame;

public class CmpfileAndWFWLTAction implements WLTActionListener {
	BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		BillVO billVO = cardPanel.getBillVO();
		String cmpfile_id = billVO.getStringValue("cmpfile_id");
		if (cmpfile_id == null || "".equals(cmpfile_id)) {
			if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(cardPanel.getEditState())) {//�����/2014-03-03��
				MessageBox.show(cardPanel, "δ���������ļ�.");
			} else {
				MessageBox.show(cardPanel, "����ѡ��һ�������ļ�.");
			}
			return;
		}
		CmpfileAndWFGraphFrame graphframe = new CmpfileAndWFGraphFrame(cardPanel, "�鿴�ļ�������", cmpfile_id);
		BillVO filevo = graphframe.getCardpanel_cmpfile().getBillVO();
		if (filevo == null || filevo.getStringValue("id") == null || "".equals(filevo.getStringValue("id"))) {
			MessageBox.show(cardPanel, "��������ѱ�ɾ��, ���ܽ��в鿴.");//��ǰ��ʾ���Դ��ڣ��������Զ�Ϊ�գ�����ֱ����ʾһ�¡����/2014-03-03��
			return;
		}
		graphframe.setVisible(true);
	}
}