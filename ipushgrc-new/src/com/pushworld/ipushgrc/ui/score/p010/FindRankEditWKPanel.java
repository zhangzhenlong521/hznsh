package com.pushworld.ipushgrc.ui.score.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * Υ�����-��������Ϣ-�������������塾���/2013-05-09��
 * @author lcj
 *
 */
public class FindRankEditWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_help, btn_save, btn_refresh;
	private HashMap findMap = new HashMap();

	@Override
	public void initialize() {
		int model = TBUtil.getTBUtil().getSysOptionIntegerValue("Υ����ֿ۷�ģʽ", 1);
		if (model == 1) {
			listPanel = new BillListPanel("PUB_COMBOBOXDICT_LCJ_E02");
		} else {
			listPanel = new BillListPanel("PUB_COMBOBOXDICT_LCJ_E03");
		}
		btn_refresh = new WLTButton("ˢ��");
		btn_refresh.addActionListener(this);
		btn_save = listPanel.getBillListBtn("$�б���");
		btn_save.addActionListener(this);//�б��水ť�����¼�

		listPanel.addBillListButton(btn_refresh);
		if (model != 1) {
			btn_help = new WLTButton("����");
			btn_help.addActionListener(this);
			listPanel.addBillListButton(btn_help);
		}
		listPanel.repaintBillListButton();
		this.add(listPanel, BorderLayout.CENTER);
		onRefresh();//�Զ���ѯһ��
		onUpdateRiskMap();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			onSave();
		} else if (e.getSource() == btn_refresh) {
			onRefresh();
		} else if (e.getSource() == btn_help) {
			onShowHelp();
		}
	}

	/**
	 * ���水ť�߼�
	 */
	private void onSave() {
		if (!listPanel.checkValidate()) {
			return;
		}
		listPanel.saveData();
		ArrayList sqlList = new ArrayList();
		for (int i = 0; i < listPanel.getRowCount(); i++) {
			String pkvalue = listPanel.getRealValueAtModel(i, "PK_PUB_COMBOBOXDICT");
			String oldvalue = (String) findMap.get(pkvalue);
			String updatevalue = listPanel.getRealValueAtModel(i, "id");
			if (oldvalue != null && !oldvalue.equals(updatevalue)) {
				sqlList.add("update score_standard set findrank='" + updatevalue + "' where findrank='" + oldvalue + "'");//����޸���������ͬʱ�޸�Υ���׼�м�¼
			}
		}
		BillVO[] delBillVOs = listPanel.getDeletedBillVOs();
		if (delBillVOs != null && delBillVOs.length > 0) {
			for (int i = 0; i < delBillVOs.length; i++) {
				sqlList.add("delete from score_standard where findrank='" + delBillVOs[i].getStringValue("id") + "'");//���ɾ����������ͬʱɾ��Υ���׼�м�¼
			}
		}
		if (sqlList.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, sqlList);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
		onUpdateRiskMap();//���·��յȼ�map
	}

	/**
	 * ˢ�°�ť�߼�������ѯһ��
	 */
	private void onRefresh() {
		listPanel.QueryDataByCondition("type='Υ�����_��������'");
	}

	/**
	 * ���·��յȼ�map
	 */
	private void onUpdateRiskMap() {
		findMap.clear();
		for (int i = 0; i < listPanel.getRowCount(); i++) {
			findMap.put(listPanel.getRealValueAtModel(i, "PK_PUB_COMBOBOXDICT"), listPanel.getRealValueAtModel(i, "id"));
		}
	}

	private void onShowHelp() {
		String text = "����һ��Υ����Ϊ�涨�Ļ�����3�֣�\r\n����������ΪԱ���Բ顢�ϼ�������顢�ⲿ��ܲ��ż�����࣬\r\n���ֱ�������Ϊ1����1.5����2����\r\n���������Ϊ1��*3��=3�֣�1.5��*3��=4.5�֣�2��*3��=6�֡�";
		MessageBox.showTextArea(this, text);
	}

}
