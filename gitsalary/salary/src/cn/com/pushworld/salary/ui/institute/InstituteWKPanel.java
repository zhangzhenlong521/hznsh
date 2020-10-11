package cn.com.pushworld.salary.ui.institute;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/***
 * �����ƶ�ά��
 * @author ��Ӫ����2014-01-03��
 * */
public class InstituteWKPanel extends AbstractWorkPanel implements ActionListener {
	private WLTButton  showplan_btn, showparam_btn = null;
	private BillListPanel billListPanel = null;
	private BillListDialog dialog = null;

	@Override
	public void initialize() {
		billListPanel = new BillListPanel("SAL_EVAL_INSTITUTION_YQ_E01");
		showplan_btn = new WLTButton("�鿴���˷���");
		showplan_btn.addActionListener(this);
		showparam_btn = new WLTButton("�鿴���˲���");
		showparam_btn.addActionListener(this);
		billListPanel.addBatchBillListButton(new WLTButton[] { showplan_btn, showparam_btn });
		billListPanel.repaintBillListButton();
		billListPanel.setVisible(true);
		this.add(billListPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == showplan_btn) {
			onShowCheckPlan();
		} else if (e.getSource() == showparam_btn) {
			onShowOptionConfig();
		}
	}

	/**
	 * �鿴���˷���
	 * */
	private void onShowCheckPlan() {
		BillListPanel listPanel = new BillListPanel("SAL_PERSON_CHECK_PLAN_CODE1");
		dialog = new BillListDialog(billListPanel, "���˷����鿴", listPanel);
		dialog.getBtn_confirm().setVisible(false);
		dialog.getBtn_cancel().setText("ȷ��");
		dialog.getBtn_cancel().setToolTipText("ȷ��");
		dialog.setVisible(true);
	}

	/**
	 * �鿴���˲���
	 * */
	private void onShowOptionConfig() {
		BillListPanel listPanel = new BillListPanel("pub_option_ZYC_CODE1");
		dialog = new BillListDialog(billListPanel, "���˲���", listPanel);
		dialog.getBtn_confirm().setVisible(false);
		dialog.getBtn_cancel().setText("ȷ��");
		dialog.getBtn_cancel().setToolTipText("ȷ��");
		dialog.setVisible(true);
	}

}
