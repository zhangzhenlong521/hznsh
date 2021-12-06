package com.pushworld.ipushgrc.ui.icheck.p040;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * 查看帮助提示按钮
 * 显示相关内容 
 * 外规相关、制度相关、风险相关、流程相关、控制相关
 * @author shaochunyun
 *
 */
public class ShowHelpInfoBtnAction implements WLTActionListener {
	private BillCardPanel cardPanel;
	
	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		WLTButton btn = (WLTButton) _event.getSource();
		onCheckHelp("检查帮助提示", "相关");
	}

	private void onCheckHelp(String _title, String _hideGroup) {
		BillCardDialog dialog_card = new BillCardDialog(cardPanel, "CK_MANUSCRIPT_DESIGN_SCY_Q01", WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog_card.setTitle(_title);
		dialog_card.billcardPanel.queryDataByCondition(" id='" + cardPanel.getBillVO().getStringValue("id") + "'");
		dialog_card.getBtn_save().setVisible(false);
		dialog_card.getBtn_confirm().setVisible(false);
		dialog_card.setVisible(true);
	}

}
