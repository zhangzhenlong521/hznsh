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
 * 问题录入-影响程度帮助提示
 * @author scy
 *
 */
public class WLT_Help1 implements WLTActionListener {

	private BillCardPanel card_panel;

	public void actionPerformed(WLTActionEvent arg0) throws Exception {
		card_panel = (BillCardPanel) arg0.getBillPanelFrom();
		HashVO[] hvo = UIUtil.getHashVoArrayByDS(null, "select code as c1,name as c2 from PUB_COMBOBOXDICT where 1=1  and ( type ='问题性质')  order by  seq asc");
		String str = "";
		BillListDialog dialog_list = new BillListDialog(card_panel, "问题性质说明", "WLTDUAL_SCY_Q01");
		dialog_list.getBilllistPanel().deleteColumn("c3");
		dialog_list.getBilllistPanel().deleteColumn("c4");
		dialog_list.getBilllistPanel().deleteColumn("c5");
		dialog_list.getBilllistPanel().deleteColumn("c6");
		dialog_list.getBtn_confirm().setVisible(false);
		dialog_list.getBtn_cancel().setText("关闭");
		
		
		dialog_list.getBilllistPanel().queryDataByHashVOs(hvo);
		dialog_list.getBilllistPanel().autoSetRowHeight();
		dialog_list.setVisible(true);

		if (dialog_list.getCloseType() == 1) {
			if (card_panel.getEditState() != null && card_panel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
				return;
			}
			str = dialog_list.getBilllistPanel().getSelectedBillVO().getStringValue("c2");
			card_panel.setValueAt("defect_nature", new ComBoxItemVO(str, str, str));
		}

	}

}
