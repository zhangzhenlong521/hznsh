package com.pushworld.ipushgrc.ui.icheck.p040;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * 
 * ����¼���Ƿ���ɲ�����ʧ������ʾ
 * @author scy
 *
 */
public class WLT_Help2 implements WLTActionListener {
	
	private BillCardPanel card_panel;
	public void actionPerformed(WLTActionEvent arg0) throws Exception {
		card_panel=(BillCardPanel) arg0.getBillPanelFrom();
		HashVO[] hvo=UIUtil.getHashVoArrayByDS(null, "select code as c1,name as c3 from PUB_COMBOBOXDICT where 1=1  and ( type ='�Ƿ���ɲ�����ʧ')  order by  seq asc");
		
		BillListDialog dialog_list=new BillListDialog(card_panel,"��������ɵĲ�����ʧ˵��","WLTDUAL_SCY_Q01");
		dialog_list.getBilllistPanel().deleteColumn("c2");
		dialog_list.getBilllistPanel().deleteColumn("c4");
		dialog_list.getBilllistPanel().deleteColumn("c5");
		dialog_list.getBilllistPanel().deleteColumn("c6");
		dialog_list.getBtn_confirm().setVisible(false);
		dialog_list.getBtn_cancel().setText("�ر�");
		dialog_list.getBilllistPanel().queryDataByHashVOs(hvo);
		dialog_list.getBilllistPanel().autoSetRowHeight();
		dialog_list.setVisible(true);
		String str="";
		if(dialog_list.getCloseType()==1){
			if (card_panel.getEditState() != null && card_panel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) { //��ʼ��״̬���޸��ֶ�����
				return;
			}
			str=dialog_list.getBilllistPanel().getSelectedBillVO().getStringValue("c3");
			card_panel.setValueAt("defect_t4", new ComBoxItemVO(str,str,str));
		}
	}
}
