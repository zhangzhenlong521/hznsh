package com.pushworld.ipushlbs.ui.casemanage.p010;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ���߰���,���߰���,�ܺͰ���--������ѯ
 * 
 * @author wupeng
 * 
 */
public class LawCaseQueryWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {

	/**
	 * 
	 */
	private WLTButton view1 = null;
	private WLTButton view = null;
	private BillListPanel list = null;
	private BillListPanel billListPanel = null;
	private BillCardDialog billCardDialog = null;
	private BillCardPanel billCardPanel = null;

	@Override
	public void initialize() {
		list = new BillListPanel("LBS_CASE_SELF_QUERY");
		view = new WLTButton("�鿴׷����Ϣ");
		view.addActionListener(this);// ��Ӽ���
		view1 = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		view1.addActionListener(this);
		list.getBillListBtnPanel().setVisible(false);
		list.addBatchBillListButton(new WLTButton[] { view1, view });// ��Ӱ�ť
		list.repaintBillListButton();// �ػ水ť
		list.mouseDoubleClicked(null);
		list.setCanShowCardInfo(false);
		list.addBillListHtmlHrefListener(this);
		this.add(list);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == view) {
			viewTrace();
		} else if (e.getSource() == view1) {
			onview();
		}
	}

	/**
	 * �鿴׷����Ϣ
	 */
	private void viewTrace() {
		BillVO selVo = list.getSelectedBillVO();

		if (selVo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		WLTTabbedPane tab = new WLTTabbedPane();

		BillListPanel filingList = new BillListPanel("LBS_CASE_FILING_CODE1");// ��������
		this.init(filingList, selVo);

		BillListPanel judgList = new BillListPanel("LBS_CASE_JUDGMENT_CODE1");
		this.init(judgList, selVo);

		BillListPanel executeList = new BillListPanel("LBS_CASE_EXECUTE_CODE1");// ִ�и���
		this.init(executeList, selVo);

		BillListPanel endList = new BillListPanel("LBS_CASE_END_CODE1");// �᰸����
		this.init(endList, selVo);

		if (filingList.getBillVOs().length == 0 && executeList.getBillVOs().length == 0 && endList.getBillVOs().length == 0 && judgList.getBillVOs().length == 0) {
			MessageBox.show(list, "�ð���û���κ�׷����Ϣ");
			return;
		}

		this.addTabPane(tab, filingList, "��������");
		this.addTabPane(tab, judgList, "�������");
		this.addTabPane(tab, executeList, "ִ�и���");
		this.addTabPane(tab, endList, "�᰸�ܽ�");

		BillCardPanel card = new BillCardPanel(list.templetVO);
		card.add(tab);
		card.setPreferredSize(new Dimension(700, 800));
		BillCardDialog dialog = new BillCardDialog(list, "�鿴׷����Ϣ", card, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true);
	}

	// �б��ʼ��
	private void init(BillListPanel list, BillVO selVo) {
		if (selVo.getStringValue("lawtype") != null && selVo != null) {
			String law_type = "";
			if (selVo.getStringValue("lawtype").equals("����"))
				law_type = "'1'";
			else if (selVo.getStringValue("lawtype").equals("����"))
				law_type = "'2'";
			String condition = " case_id = " + selVo.getStringValue("id") + " and case_type = " + law_type;
			list.QueryDataByCondition(condition);
			list.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
			list.repaintBillListButton();
		}
	}

	private void addTabPane(WLTTabbedPane tab, BillListPanel listPanel, String title) {// ���ݼ�¼��ѡ�����б���廹�ǿ�Ƭ
		if (listPanel.getBillVOs().length == 0) {
			WLTLabel label = new WLTLabel("û�С�" + title + "����Ϣ");
			tab.addTab(title, label);
		} else if (listPanel.getBillVOs().length == 1) {// ֻ��һ����¼��û�м�¼
			BillCardPanel card = new BillCardPanel(listPanel.getTempletVO());
			card.setBillVO(listPanel.getBillVOs()[0]);
			tab.addTab(title, card);
		} else {
			tab.addTab(title, listPanel);
		}

	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().equalsIgnoreCase("SENDPROCEEDING")) {
			BillVO billVO = _event.getBillListPanel().getSelectedBillVO();
			String type = billVO.getStringValue("lawtype");
			if (type.equals("����")) {
				billCardPanel = new BillCardPanel("LBS_CASE_SELF_CODE1");
				billCardPanel.setBillVO(billVO);
				billCardPanel.setEditable(false);
				billCardDialog = new BillCardDialog(list, "������Ϣ", billCardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
				billCardDialog.setVisible(true);
			} else if (type.equals("����")) {
				billCardPanel = new BillCardPanel("LBS_CASE_DECASE_CODE1");
				billCardPanel.setBillVO(billVO);
				billCardPanel.setEditable(false);
				billCardDialog = new BillCardDialog(list, "������Ϣ", billCardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
				billCardDialog.setVisible(true);
			}
		}
	}

	public void onview() {
		BillVO billVO = list.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(list);
			return;
		}
		String type = billVO.getStringValue("lawtype");
		if (type.equals("����")) {
			billCardPanel = new BillCardPanel("LBS_CASE_SELF_CODE1");
			billCardPanel.setBillVO(billVO);
			billCardPanel.setEditable(false);
			billCardDialog = new BillCardDialog(list, "������Ϣ", billCardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
			billCardDialog.setVisible(true);
		} else if (type.equals("����")) {
			billCardPanel = new BillCardPanel("LBS_CASE_DECASE_CODE1");
			billCardPanel.setBillVO(billVO);
			billCardPanel.setEditable(false);
			billCardDialog = new BillCardDialog(list, "������Ϣ", billCardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
			billCardDialog.setVisible(true);
		}

	}
}
