package com.pushworld.icheck.ui.p040;

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
 * 问题录入-问题重要性分组帮助提示
 * @author scy
 *
 */
public class WLT_Help4 implements WLTActionListener {
	
	private BillCardPanel card_panel;
	public void actionPerformed(WLTActionEvent arg0) throws Exception {
		card_panel=(BillCardPanel) arg0.getBillPanelFrom();
		HashVO[] hvo=UIUtil.getHashVoArrayByDS(null, "select id as c4, code as c5,name as c6 from PUB_COMBOBOXDICT where 1=1  and ( type ='重要性分组')  order by  seq asc");
		
		BillListDialog dialog_list=new BillListDialog(card_panel,"问题重要性分组","WLTDUAL_SCY_Q01");
		dialog_list.getBilllistPanel().deleteColumn("c1");
		dialog_list.getBilllistPanel().deleteColumn("c2");
		dialog_list.getBilllistPanel().deleteColumn("c3");
		dialog_list.getBtn_confirm().setVisible(false);
		dialog_list.getBtn_cancel().setText("关闭");
		dialog_list.getBilllistPanel().queryDataByHashVOs(hvo);
		dialog_list.getBilllistPanel().autoSetRowHeight();
		dialog_list.setVisible(true);
		String str="";
		if(dialog_list.getCloseType()==1){
			if (card_panel.getEditState() != null && card_panel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) { //初始化状态不修改字段数据
				return;
			}
			str=dialog_list.getBilllistPanel().getSelectedBillVO().getStringValue("c3");
			card_panel.setValueAt("defect_t4", new ComBoxItemVO(str,str,str));
		}
	}
}
