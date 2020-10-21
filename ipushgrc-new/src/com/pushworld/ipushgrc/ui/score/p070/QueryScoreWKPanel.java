package com.pushworld.ipushgrc.ui.score.p070;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * Υ�����-����λ���ֲ�ѯ�����/2013-05-14��
 * @author lcj
 *
 */
public class QueryScoreWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_query;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("SCORE_USER_LCJ_Q06");
		btn_query = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		btn_query.setText("���");
		btn_query.setToolTipText("�鿴��ϸ��Ϣ");
		listPanel.addBillListButton(btn_query);
		listPanel.repaintBillListButton();
		String curryear = TBUtil.getTBUtil().getCurrDate().substring(0, 4);
		listPanel.QueryDataByCondition("EFFECTDATE like '" + curryear + "%'");//��ѯ����ȵļ�¼
		this.setLayout(new BorderLayout());
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
	}
}
