package com.pushworld.ipushgrc.ui.law.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;

/**
 * ������༭!!!
 * ���б�(law_law),����ѯ�����,Ȼ�������а�ť�������,����󵯳�����,�Ȳ�ѯ�����������Щ���(law_exposit),Ȼ������������!!
 * ͬʱ���С��鿴��桿��ť,����������ǿ�Ƭ,��ʾ�����������,���������ҷָ�,����������ϸ(law_law_item)�����ͽṹ,�ұ��������ϸ(law_law_item)�Ŀ�Ƭ
 * 
 * @author xch
 * 
 */
public class LawExpositEditWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener, BillListSelectListener {

	private BillListPanel billList_law = null; // �����б�

	private BillListPanel billList_exposit; // ����鿴�����ť��������dialog�е� ������

	private BillListDialog billListDialog_exposit; // ����б�Ի���!

	private WLTButton btn_exposit, btn_viewExposit, btn_deleteExpoist, btn_updateExposit, btn_list;

	private String loginUserID = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // ��½��ԱID

	public void initialize() {

		billList_law = new BillListPanel("LAW_LAW_CODE2");
		btn_exposit = new WLTButton("�½����");
		btn_exposit.addActionListener(this);
		btn_viewExposit = new WLTButton("�鿴���");
		btn_viewExposit.addActionListener(this);
		billList_law.addBillListHtmlHrefListener(this);
		billList_law.addBatchBillListButton(new WLTButton[] { btn_exposit, btn_viewExposit });
		billList_law.repaintBillListButton();
		billList_law.setItemVisible("expositcount", true);
		billList_law.setItemVisible("exposittime", true);
		this.add(billList_law); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_exposit) {
			onExposit();
		} else if (obj == btn_viewExposit) {
			onViewAllExposit();
		} else if (obj == btn_updateExposit) {
			onUpdateExposit();
		}
	}

	public void onUpdateExposit() {
		BillVO lawVO = billList_exposit.getSelectedBillVO();
		if (lawVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (!loginUserID.equals(lawVO.getStringValue("exposituserid"))) {
			MessageBox.show(this, "�˼�¼ֻ�д����˿����޸ģ�");
			return;
		}
		BillCardDialog billcard_exposit = new BillCardDialog(this, "�޸Ľ��", "LAW_EXPOSIT_CODE1", 700, 550, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		BillCardPanel cardPanel = billcard_exposit.getBillcardPanel();
		cardPanel.setBillVO(lawVO);
		billcard_exposit.setVisible(true);
		if (billcard_exposit.getCloseType() == 1) {
			billList_exposit.refreshCurrSelectedRow();
		}
	}

	/*
	 * ���
	 */
	public void onExposit() {
		BillVO lawVO = billList_law.getSelectedBillVO();
		if (lawVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardDialog billcard_exposit = new BillCardDialog(this, "�½����", "LAW_EXPOSIT_CODE1", 600, 430, WLTConstants.BILLDATAEDITSTATE_INSERT);
		BillCardPanel cardPanel = billcard_exposit.getBillcardPanel();
		cardPanel.setValueAt("lawid", new StringItemVO(lawVO.getStringValue("id")));
		cardPanel.setValueAt("lawname", new StringItemVO(lawVO.getStringValue("lawname")));
		billcard_exposit.setVisible(true);
		if (billcard_exposit.getCloseType() == 1) {
			try {
				// �����һ�ξ͸��½������
				billList_law.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * �鿴һ������ �����н��
	 */
	public void onViewAllExposit() {
		BillVO lawVO = billList_law.getSelectedBillVO();
		if (lawVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_law.refreshCurrSelectedRow();
		lawVO = billList_law.getSelectedBillVO();
		if (lawVO.getStringValue("expositcount") == null || "".equals(lawVO.getStringValue("expositcount"))) {//���û�н����û�б�Ҫ�������ˣ���Ϊ������ֻ���޸ġ�ɾ������������������������������һ������������/2012-03-26��
			MessageBox.show(this, "�ü�¼û�н��!");
			return;
		}
		billListDialog_exposit = new BillListDialog(this, "�鿴���桾" + lawVO.getStringValue("lawname") + "���Ľ��", "LAW_EXPOSIT_CODE1", 700, 550);
		billListDialog_exposit.getBtn_cancel().setText("����");
		billListDialog_exposit.getBtn_confirm().setVisible(false);
		billList_exposit = billListDialog_exposit.getBilllistPanel();
		billList_exposit.setQuickQueryPanelVisiable(false); // ���ò�ѯ�������,��ǰ������billList_exposit.getQuickQueryPanel().setVisible(false) ;ֻ�ǽ���ѯ��������ˣ�
		billList_exposit.QueryDataByCondition(" lawid = " + lawVO.getStringValue("id"));
		billList_exposit.setDataFilterCustCondition(" lawid = " + lawVO.getStringValue("id"));
		btn_updateExposit = new WLTButton("�޸�");
		btn_updateExposit.addActionListener(this);
		billList_exposit.addBillListHtmlHrefListener(this);
		btn_deleteExpoist = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		billList_exposit.addBatchBillListButton(new WLTButton[] { btn_updateExposit, btn_deleteExpoist, btn_list });
		billList_exposit.repaintBillListButton();
		billList_exposit.addBillListSelectListener(this);
		billListDialog_exposit.setVisible(true);
		billList_law.refreshCurrSelectedRow();
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().equalsIgnoreCase("expositcount")) {
			onViewAllExposit();
		} else if (_event.getBillListPanel().getTempletVO().getTempletcode().contains("LAW_EXPOSIT")) {
			BillVO vo = _event.getBillListPanel().getSelectedBillVO();
			new LawShowHtmlDialog(this, vo.getStringValue("lawid"), null, new String[] { vo.getStringValue("lawitemid") }); // ���������������Ŀ���⡣
		} else {
			new LawShowHtmlDialog(billList_law);
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO _vo = billList_exposit.getSelectedBillVO();
		if (loginUserID.equals(_vo.getStringValue("exposituserid"))) { // ֻ�м�¼�Ĵ����߲ſ��Ա༭�˼�¼����
			btn_updateExposit.setEnabled(true);
			btn_deleteExpoist.setEnabled(true);
		} else {
			btn_updateExposit.setEnabled(false);
			btn_deleteExpoist.setEnabled(false);
		}
	}

}
