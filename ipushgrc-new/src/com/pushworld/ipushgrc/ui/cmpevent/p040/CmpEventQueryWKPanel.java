package com.pushworld.ipushgrc.ui.cmpevent.p040;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.favorite.MyFavoriteQueryWKPanel;
import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;

/**
 * Υ���¼���ѯ!!!
 * @author xch
 *
 */
public class CmpEventQueryWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billList; //

	private WLTButton btn_ShowTrack, btn_ShowFlow, btn_ShowRisk; //���ٲ�ѯ,�����ļ���ѯ�����յ��ѯ

	public void initialize() {
		currInit("CMP_EVENT_CODE2");
	}

	public void currInit(String _templetCode) {
		billList = new BillListPanel(_templetCode); //
		WLTButton btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		btn_ShowTrack = new WLTButton("��������鿴");
		btn_ShowFlow = new WLTButton("�����ļ��鿴");
		btn_ShowRisk = new WLTButton("���յ�鿴");
		btn_ShowTrack.addActionListener(this);
		btn_ShowFlow.addActionListener(this);
		btn_ShowRisk.addActionListener(this);
		//�����ղؼа�ť
		WLTButton btn_joinFavority = MyFavoriteQueryWKPanel.getJoinFavorityButton("Υ���¼�", this.getClass().getName(), "eventname");
		billList.addBatchBillListButton(new WLTButton[] { btn_list, btn_joinFavority, btn_ShowTrack, btn_ShowFlow, btn_ShowRisk }); //
		billList.repaintBillListButton(); //ˢ�°�ť!!!
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_ShowTrack) {
			onShowTrack();
		} else if (obj == btn_ShowFlow) {
			onShowFlow();
		} else if (obj == btn_ShowRisk) {
			onShowRisk();
		}
	}

	/**
	 * ���ķ���
	 */
	public void onShowTrack() {
		BillVO vo = billList.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(billList);
			return;
		}
		BillListDialog listDialog = new BillListDialog(this, "���ķ���", "CMP_EVENT_ADJUSTPROJECT_CODE1");
		BillListPanel listPanel = listDialog.getBilllistPanel();
		listPanel.QueryDataByCondition(" eventid = '" + vo.getStringValue("id") + "'");
		if (listPanel.getRowCount() == 0) {
			MessageBox.show(billList, "���¼�û�����ķ���.");
			return;
		}
		listPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "�鿴���Ĵ�ʩ"));
		listPanel.repaintBillListButton();
		listPanel.setQuickQueryPanelVisiable(false);
		listDialog.getBtn_confirm().setVisible(false);
		listDialog.setVisible(true);
	}

	/**
	 * �����ļ���ѯ
	 */
	public void onShowFlow() {
		BillVO vo = billList.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(billList);
			return;
		}
		String cmpfile_id = vo.getStringValue("cmp_cmpfile_id");
		if (cmpfile_id == null || "".equals(cmpfile_id)) {
			MessageBox.show(billList, "���¼�û�й��������ļ�.");
			return;
		}
		CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "�鿴�ļ�������", vo.getStringValue("cmp_cmpfile_id"));
		dialog.setVisible(true);
	}

	/**
	 * ���յ��ѯ
	 */
	public void onShowRisk() {
		BillVO vo = billList.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(billList);
			return;
		}
		String refrisks = vo.getStringValue("refrisks");
		if (refrisks != null && !refrisks.equals("")) {
			String riskIds = new TBUtil().getInCondition(refrisks);
			BillListDialog listDialog = new BillListDialog(this, "�����ķ��յ�", "CMP_RISK_CODE1");
			BillListPanel listPanel = listDialog.getBilllistPanel();
			listPanel.QueryDataByCondition(" id in(" + riskIds + ")");
			if (listPanel.getRowCount() == 0) {//�ж��Ƿ�����صķ��յ㣬���û�оͲ�Ҫ�򿪿տյ��б��ˣ�ֱ����ʾȻ�󷵻ء����/2012-03-28��
				MessageBox.show(billList, "δ�ҵ���ط��յ㣡");
				return;
			}
			listPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));//���������ť
			listPanel.repaintBillListButton();
			listDialog.getBtn_confirm().setVisible(false);
			listDialog.setVisible(true);
		} else {
			MessageBox.show(billList, "���¼�û�й������յ�.");
			return;
		}

	}

}
