package com.pushworld.ipushgrc.ui.wfrisk.p080;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JLabel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ��ϵ�ļ���ѯȨ������!! ��ƽ̨������Ȩ����μ���??
 * @author xch
 *
 */
public class CmpFileQueryPermitWKPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private BillListPanel billList = null; //

	private WLTButton btn_insert, btn_update, btn_delete, btn_list; //��,ɾ,��
	private WLTButton btn_moveup, btn_movedown, btn_save, btn_refreah; //
	private String str_datapolicy_id = null; //

	@Override
	public void initialize() {
		try {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_datapolicy where name='�����ļ���ѯ����'"); ////
			if (hvs == null || hvs.length <= 0) {
				this.add(new JLabel("û�ж�����Ϊ�������ļ���ѯ���ԡ�������Ȩ�޲���,�붨��֮!")); //
				return; //
			}
			str_datapolicy_id = hvs[0].getStringValue("id"); //����id
			billList = new BillListPanel("PUB_DATAPOLICY_B_CODE1"); //
			billList.QueryDataByCondition("datapolicy_id='" + str_datapolicy_id + "'"); ////
			billList.getTitleLabel().setText("�����ļ���ѯ����"); //
			btn_insert = new WLTButton("����"); //
			btn_insert.addActionListener(this); //
			btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //�޸�
			btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //ɾ��
			btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //���
			btn_moveup = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP); //
			btn_movedown = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN); //
			btn_save = WLTButton.createButtonByType(WLTButton.LIST_SAVE, "����˳��"); //
			btn_refreah = new WLTButton("ˢ��"); //
			btn_refreah.addActionListener(this); //
			billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list, btn_moveup, btn_movedown, btn_save, btn_refreah }); //
			billList.repaintBillListButton(); //
			this.add(billList); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert(); //
		} else if (e.getSource() == btn_refreah) {
			onRefresh(); //
		}
	}

	private void onInsert() {
		HashMap defaultValueMap = new HashMap(); //
		defaultValueMap.put("datapolicy_id", str_datapolicy_id); ////
		billList.doInsert(defaultValueMap); //���������!!
	}

	private void onRefresh() {
		billList.QueryDataByCondition("datapolicy_id='" + str_datapolicy_id + "'"); ////
	}
}
