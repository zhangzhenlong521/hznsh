package com.pushworld.ipushgrc.ui.indexpage;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;

/**
 * 首页合规检查点击事件
 * @author lcj
 *
 */
public class CheckAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		HashVO selectVO = (HashVO) this.getValue("DeskTopNewsDataVO");

		BillCardPanel cardPanel = new BillCardPanel("CMP_CHECK_CODE1"); //
		cardPanel.queryDataByCondition("id=" + selectVO.getStringValue("id"));

		BillCardDialog dialog = new BillCardDialog((JPanel) this.getValue("DeskTopPanel"), "合规检查", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true);
	}
}
