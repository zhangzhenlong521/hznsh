package com.pushworld.ipushgrc.ui.risk.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextArea;

/**
 * ������������-����ɾ���ķ��յ����ڻ��ڵ�ѡ��
 * @author lcj
 *
 */
public class SuggestDeleteRiskWLTAction implements WLTActionListener, ActionListener {
	private BillCardPanel cardPanel = null;
	private BillVO billVO = null;
	private BillListDialog listdialog = null;
	private BillListPanel billlist_risk_process = null;
	private WLTButton btn_show = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		billVO = cardPanel.getBillVO();
		String cmpfile_id = billVO.getStringValue("cmpfile_id");
		if (cmpfile_id == null || "".equals(cmpfile_id)) {
			MessageBox.show(cardPanel.getParent(), "����ѡ��һ�������ļ�!");
			return;
		}
		String countid = UIUtil.getStringValueByDS(null, "select count(risk_id) from V_RISK_PROCESS_FILE where cmpfile_id =" + cmpfile_id);
		if ("0".equals(countid)) {
			MessageBox.show(cardPanel.getParent(), "�������ļ�û�з��յ㣬���ܲ鿴!");
			return;
		}
		listdialog = new BillListDialog(cardPanel.getParent(), "��ѡ����h���ķ��յ�", "V_RISK_PROCESS_FILE_CODE3");
		billlist_risk_process = listdialog.getBilllistPanel();
		btn_show = new WLTButton("���");
		btn_show.addActionListener(this);
		billlist_risk_process.getBillListBtnPanel().addButton(btn_show);
		billlist_risk_process.repaintBillListButton();
		billlist_risk_process.QueryDataByCondition("cmpfile_id=" + cmpfile_id);
		CardCPanel_TextArea textarea = (CardCPanel_TextArea) cardPanel.getCompentByKey("sug_delrisks");
		if (!textarea.getArea().isEditable()) {
			listdialog.setTitle("");
			listdialog.getBtn_confirm().setVisible(false);//���ɱ༭ʱ��Ҫ����ȷ����ť���ɼ���ȡ����ť����Ϊ�رա����/2013-09-02��
			listdialog.getBtn_cancel().setText("�ر�");
			listdialog.getBtn_cancel().setToolTipText("�ر�");
			listdialog.setVisible(true);
			return;
		}
		listdialog.setVisible(true);
		if (listdialog.getCloseType() == BillDialog.CONFIRM) {
			afterClose();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_show) {
			onLookRisk();
		}
	}

	private void onLookRisk() {
		BillVO billVO = billlist_risk_process.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listdialog);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_RISK_CODE3"); //
		cardPanel.queryDataByCondition("id=" + billVO.getStringValue("risk_id"));
		cardPanel.execEditFormula("finalost");
		cardPanel.execEditFormula("cmplost");
		cardPanel.execEditFormula("honorlost");
		BillCardDialog carddialog = new BillCardDialog(listdialog, "���յ�[" + billVO.getStringValue("risk_name") + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		carddialog.setVisible(true); //		
	}

	private void afterClose() {
		ArrayList list_id = new ArrayList();
		ArrayList wfrisk = new ArrayList();
		BillVO[] returnbillvos = listdialog.getReturnBillVOs();
		for (int i = 0; i < returnbillvos.length; i++) {
			String wfactivity_id = returnbillvos[i].getStringValue("wfactivity_id");
			if (wfactivity_id == null || ("").equals(wfactivity_id.trim())) {
				wfrisk.add(returnbillvos[i]);
			}
			list_id.add(wfactivity_id);
		}
		String sug_delrisks = billVO.getStringValue("sug_delrisks");
		StringBuffer sb_desc = new StringBuffer();
		int count = 1;
		for (int i = 0; i < wfrisk.size(); i++) {
			if (i == 0 && (sug_delrisks != null && !"".equals(sug_delrisks.trim()))) {
				sb_desc.append(sug_delrisks);
				if (!sug_delrisks.endsWith("\n")) {
					sb_desc.append("\n");
				}
			}
			if (i == 0) {
				sb_desc.append("��������[");
				sb_desc.append(((BillVO) wfrisk.get(i)).getStringValue("WFPROCESS_NAME"));
				sb_desc.append("]��ɾ�����յ�:\n");
			}
			sb_desc.append(count);
			count++;
			sb_desc.append("����������:");
			sb_desc.append(((BillVO) wfrisk.get(i)).getStringValue("risk_name"));
			sb_desc.append("\n");
			sb_desc.append("���շ���:");
			sb_desc.append(((BillVO) wfrisk.get(i)).getStringValue("risk_risktype"));
			sb_desc.append("\n");
			sb_desc.append("���յȼ�:");
			sb_desc.append(((BillVO) wfrisk.get(i)).getStringValue("risk_rank"));
			sb_desc.append("\n");
			sb_desc.append("������:");
			sb_desc.append(((BillVO) wfrisk.get(i)).getStringValue("possible"));
			sb_desc.append("\n");
			sb_desc.append("Ӱ��̶�:");
			sb_desc.append(((BillVO) wfrisk.get(i)).getStringValue("serious"));
			sb_desc.append("\n");
		}
		try {
			HashVO[] activityvos = UIUtil.getHashVoArrayByDS(null, "select pro.name,act.belongdeptgroup,act.belongstationgroup,act.wfname,act.id from pub_wf_activity act left join pub_wf_process pro on act.processid = pro.id where act.id in(" + new TBUtil().getInCondition(list_id) + ")");
			for (int i = 0; i < activityvos.length; i++) {
				if (i == 0 && (sug_delrisks != null && !"".equals(sug_delrisks.trim()))) {
					sb_desc.append(sug_delrisks);
					if (!sug_delrisks.endsWith("\n")) {
						sb_desc.append("\n");
					}
				}
				sb_desc.append("��������[");
				sb_desc.append(activityvos[i].getStringValue("name"));
				sb_desc.append("]��");
				String dept = activityvos[i].getStringValue("belongdeptgroup");
				if (dept != null) {
					sb_desc.append("����Ϊ[");
					sb_desc.append(dept.replace("\n", ""));
					sb_desc.append("]");
				}
				String station = activityvos[i].getStringValue("belongstationgroup");
				if (station != null) {
					sb_desc.append("�׶�Ϊ[");
					sb_desc.append(station.replace("\n", ""));
					sb_desc.append("]");
				}
				sb_desc.append("�Ļ���[");
				sb_desc.append(activityvos[i].getStringValue("wfname").replace("\n", ""));
				sb_desc.append("]ɾ�����յ�:\n");
				for (int j = 0; j < returnbillvos.length; j++) {
					if (activityvos[i].getStringValue("id").equals(returnbillvos[j].getStringValue("wfactivity_id"))) {
						sb_desc.append(count);
						count++;
						sb_desc.append("����������:");
						sb_desc.append(returnbillvos[j].getStringValue("risk_name"));
						sb_desc.append("\n");
						sb_desc.append("���շ���:");
						sb_desc.append(returnbillvos[j].getStringValue("risk_risktype"));
						sb_desc.append("\n");
						sb_desc.append("���յȼ�:");
						sb_desc.append(returnbillvos[j].getStringValue("risk_rank"));
						sb_desc.append("\n");
						sb_desc.append("������:");
						sb_desc.append(returnbillvos[j].getStringValue("possible"));
						sb_desc.append("\n");
						sb_desc.append("Ӱ��̶�:");
						sb_desc.append(returnbillvos[j].getStringValue("serious"));
						sb_desc.append("\n");
					}
				}
			}
			if (sb_desc.length() > 0) {
				cardPanel.setRealValueAt("sug_delrisks", sb_desc.toString());
			}
		} catch (Exception e1) {
			MessageBox.showException(cardPanel.getParent(), e1);
		}
	}
}