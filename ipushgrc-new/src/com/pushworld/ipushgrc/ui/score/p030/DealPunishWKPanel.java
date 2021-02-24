package com.pushworld.ipushgrc.ui.score.p030;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * Υ�����-��Υ��ͷ��������/2013-05-15��
 * @author lcj
 *
 */
public class DealPunishWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel listPanel;
	private WLTButton btn_edit;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("SCORE_USER_LCJ_Q02");
		btn_edit = new WLTButton("¼��ͷ����", "report_3.png");
		btn_edit.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_edit });
		listPanel.repaintBillListButton();
		String curryear = TBUtil.getTBUtil().getCurrDate().substring(0, 4);
		listPanel.QueryDataByCondition("EFFECTDATE like '" + curryear + "%'");//��ѯ����ȵļ�¼
		this.setLayout(new BorderLayout());
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_edit) {
			listPanel.doEdit();
		}
	}

}
