package com.pushworld.ipushgrc.ui.wfrisk.p050;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;

import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFGraphEditItemDialog;
import com.pushworld.ipushgrc.ui.wfrisk.p010.WFAndRiskEditWKPanel;

/**
 * ���̻�����ͼʵ����
 *
 */
public class WFAndRiskBomViewByDeptPanel extends WLTPanel implements ActionListener, BillListHtmlHrefListener {
	private WLTButton btn_lookrisk;
	private BillListPanel billlist_risk;

	public WFAndRiskBomViewByDeptPanel(String _type, BillBomPanel _billBomPanel) {
		this.setLayout(new BorderLayout()); //		
		JPanel mainPanel = new WLTPanel(BackGroundDrawingUtil.INCLINE_NW_TO_SE, new BorderLayout(), LookAndFeel.defaultShadeColor1, false); //
		String bomtype = (String) _billBomPanel.getClientProperty("BOMTYPE");
		if ("RISK".equals(bomtype)) {
			String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("�������������ļ���ģ��", "���յ�ͼ�鿴", "V_RISK_PROCESS_FILE_CODE1");//�����/2012-07-18��
			billlist_risk = new BillListPanel(templetcode);
			billlist_risk.setDataFilterCustCondition("blcorpname like '%" + _type + "' and filestate='3'");
			billlist_risk.QueryDataByCondition(null);
			btn_lookrisk = new WLTButton("���");
			btn_lookrisk.addActionListener(this);
			billlist_risk.addBillListButton(btn_lookrisk);
			billlist_risk.repaintBillListButton();
			billlist_risk.addBillListHtmlHrefListener(this);
			mainPanel.add(billlist_risk, BorderLayout.CENTER);
		} else if ("PROCESS".equals(bomtype)) {
			String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("�������������ļ���ģ��", "���̵�ͼ�鿴", "V_FILE_PROCESS_CODE1");//�����/2012-07-18��
			BillListPanel billlist_process = new BillListPanel(templetcode);
			billlist_process.setDataFilterCustCondition("blcorpname like '%" + _type + "' and filestate='3'");
			billlist_process.QueryDataByCondition(null);
			billlist_process.addBillListHtmlHrefListener(this);
			mainPanel.add(billlist_process, BorderLayout.CENTER);
		} else if ("CMPFILE".equals(bomtype)) {
			WFAndRiskEditWKPanel panel_wfAndRisk = new WFAndRiskEditWKPanel();
			panel_wfAndRisk.setEditable(false);
			panel_wfAndRisk.initialize();
			BillListPanel billlist_cmpfile = panel_wfAndRisk.getBillList_cmpfile();
			billlist_cmpfile.setDataFilterCustCondition("blcorpname like '%" + _type + "' and filestate='3'");
			billlist_cmpfile.QueryDataByCondition(null);
			mainPanel.add(panel_wfAndRisk, BorderLayout.CENTER);
		} else {
			mainPanel.add(new JLabel("��Ϊ[�ļ�/����/����]���ߵĵ�ͼ�鿴�ǹ��ø�ͼ,Ϊ��������Ҫ��BillBomPanel��putClientProperty(\"BOMTYPE\", \"RISK/PROCESS/CMPFILE\")")); //��ǰֱ�Ӵ�ͼ�е���鿴ʱ,�����и��հ�,��֪�ι�,�ɴཫԭ��д��,ʡ�õ��԰����֪��ԭ��!!��xch/2012-03-07��
		}
		this.add(new JLabel("����:" + _type), BorderLayout.NORTH); //
		this.add(mainPanel, BorderLayout.CENTER); //
	}

	public void actionPerformed(ActionEvent e) {
		onLookRisk();
	}

	private void onLookRisk() {
		BillVO billVO = billlist_risk.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("�������������ļ���ģ��", "���յ�ͼ���", "CMP_RISK_CODE3");//�����/2012-07-18��
		BillCardPanel cardPanel = new BillCardPanel(templetcode); //
		cardPanel.queryDataByCondition("id=" + billVO.getStringValue("risk_id"));
		cardPanel.execEditFormula("finalost");
		cardPanel.execEditFormula("cmplost");
		cardPanel.execEditFormula("honorlost");
		BillCardDialog carddialog = new BillCardDialog(billlist_risk, "���յ�[" + billVO.getStringValue("risk_name") + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		cardPanel.setEditable("serious", false);
		carddialog.setVisible(true); //		
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillVO billvo = _event.getBillListPanel().getSelectedBillVO();
		if (billvo == null) {
			return;
		}
		if ("wfprocess_name".equals(_event.getItemkey())) {
			//���յ��ѯ��ѡ�����ĳһ�����ڵ�ĳ�����ա����ڿ��Ը������ڿ�[����2012-03-21]
			WFGraphEditItemDialog itemdialog = new WFGraphEditItemDialog(this, "�鿴����[" + billvo.getStringValue("wfprocess_name") + "]", billvo.getStringValue("wfprocess_id"), new String[] { billvo.getStringValue("wfactivity_id") }, false, true);
			itemdialog.setVisible(true);
		} else {
			CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "�鿴�ļ�������", billvo.getStringValue("cmpfile_id"));
			dialog.setShowprocessid(billvo.getStringValue("wfprocess_id"));
			dialog.setVisible(true);
		}
	}
}
