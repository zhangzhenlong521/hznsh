package com.pushworld.ipushgrc.ui.law.p060;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;

/**
 * �������ѯ! ���ǵ��б�(law_exposit),���Զ���ѯ�����µĽ��,�����������ť�������������鿴���桿 �������������ǽ��б��¼ֱ�����
 * ���鿴��桿��ť,����������ǿ�Ƭ,��ʾ�����������,���������ҷָ�,����������ϸ(law_law_item)�����ͽṹ,�ұ��������ϸ(law_law_item)�Ŀ�Ƭ
 * 
 * @author xch
 * 
 */
public class LawExpositQueryWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {
	private JTabbedPane tab; //
	private BillListPanel billList_lawExposit = null;
	private BillListPanel lawExposit_billList = null;
	private BillListDialog billListDialog_exposit;
	private WLTButton btn_viewExposit, btn_list, btn_viewLaw;

	public void initialize() {
		tab = new JTabbedPane(); //
		billList_lawExposit = new BillListPanel("LAW_LAW_CODE2");
		billList_lawExposit.addBillListHtmlHrefListener(this);
		billList_lawExposit.getBillListBtnPanel().setVisible(false);
		billList_lawExposit.setItemVisible("expositcount", true);
		billList_lawExposit.setItemVisible("exposittime", true);
		btn_viewExposit = new WLTButton("�鿴���");
		btn_viewExposit.addActionListener(this);
		billList_lawExposit.addBatchBillListButton(new WLTButton[] { btn_viewExposit, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		billList_lawExposit.repaintBillListButton();

		lawExposit_billList = new BillListPanel("LAW_EXPOSIT_CODE1");
		lawExposit_billList.addBillListHtmlHrefListener(this);
		lawExposit_billList.getBillListBtnPanel().setVisible(false);
		lawExposit_billList.setItemVisible("expositcount", true);
		lawExposit_billList.setItemVisible("exposittime", true);
		btn_viewLaw = new WLTButton("�鿴����");
		btn_viewLaw.addActionListener(this);
		lawExposit_billList.addBatchBillListButton(new WLTButton[] { btn_viewLaw, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		lawExposit_billList.repaintBillListButton();

		tab.addTab("����->���", billList_lawExposit); //
		tab.addTab("���->����", lawExposit_billList); //
		this.add(tab);
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().equals("expositcount")) {
			onViewAllExposit();
		} else if (_event.getBillListPanel().getTempletVO().getTempletcode().contains("LAW_EXPOSIT")) {
			BillVO vo = _event.getBillListPanel().getSelectedBillVO();
			new LawShowHtmlDialog(this, vo.getStringValue("lawid"), null, new String[] { vo.getStringValue("lawitemid") }); // ���������������Ŀ���⡣
		} else {
			new LawShowHtmlDialog(billList_lawExposit);
		}
	}

	/**
	 * �鿴һ��������Ӧ�ķ���
	 */
	public void onViewLaw() {
		BillVO billVo = lawExposit_billList.getSelectedBillVO();
		if (billVo == null) {
			MessageBox.show(this, "��ѡ��һ�������");
			return;
		}
		showLawHtml();
	}

	private void showLawHtml() {
		String lawid = lawExposit_billList.getSelectedBillVO().getStringValue("lawid");
		new LawShowHtmlDialog(lawExposit_billList, lawid);
	}

	/**
	 * �鿴һ������ �����н��
	 */
	public void onViewAllExposit() {
		BillVO lawVO = billList_lawExposit.getSelectedBillVO();
		if (lawVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_lawExposit.refreshCurrSelectedRow();
		lawVO = billList_lawExposit.getSelectedBillVO();
		if (lawVO.getStringValue("expositcount") == null || "".equals(lawVO.getStringValue("expositcount"))) {//���û�н����û�б�Ҫ�������ˣ���Ϊ������ֻ���޸ġ�ɾ������������������������������һ������������/2012-03-26��
			MessageBox.show(this, "�ü�¼û�н��!");
			return;
		}
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		billListDialog_exposit = new BillListDialog(this, "�鿴���桾" + lawVO.getStringValue("lawname") + "���Ľ��", "LAW_EXPOSIT_CODE1", 700, 550);
		billListDialog_exposit.getBtn_cancel().setText("����");
		BillListPanel billList_exposit = billListDialog_exposit.getBilllistPanel();
		billList_exposit.setQuickQueryPanelVisiable(false);
		billList_exposit.addBatchBillListButton(new WLTButton[] { btn_list });
		billList_exposit.repaintBillListButton();
		billList_exposit.addBillListHtmlHrefListener(this);
		billList_exposit.QueryDataByCondition(" lawid = " + lawVO.getStringValue("id"));
		billList_exposit.setDataFilterCustCondition(" lawid = " + lawVO.getStringValue("id"));
		billListDialog_exposit.getBtn_confirm().setVisible(false);
		billListDialog_exposit.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_viewExposit) {
			onViewAllExposit();
		} else if (e.getSource() == btn_viewLaw) {
			onViewLaw();
		}
	}
}
