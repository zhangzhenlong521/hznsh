package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class AccountingScoreWKPanel extends AbstractWorkPanel implements ActionListener {
    private BillListPanel listPanel;
	@Override
	public void initialize() {//��ʼ��
		listPanel=new BillListPanel("WN_KJSCORE_TABLE_ZPY_Q01");//ί�ɻ���ϴ�
		this.add(listPanel);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
	}
}