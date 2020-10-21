package com.pushworld.ipushgrc.ui.risk.p020;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphFrame;

public class CmpfileAndWFWLTAction implements WLTActionListener {
	BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		BillVO billVO = cardPanel.getBillVO();
		String cmpfile_id = billVO.getStringValue("cmpfile_id");
		if (cmpfile_id == null || "".equals(cmpfile_id)) {
			if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(cardPanel.getEditState())) {//【李春娟/2014-03-03】
				MessageBox.show(cardPanel, "未关联流程文件.");
			} else {
				MessageBox.show(cardPanel, "请先选择一个流程文件.");
			}
			return;
		}
		CmpfileAndWFGraphFrame graphframe = new CmpfileAndWFGraphFrame(cardPanel, "查看文件和流程", cmpfile_id);
		BillVO filevo = graphframe.getCardpanel_cmpfile().getBillVO();
		if (filevo == null || filevo.getStringValue("id") == null || "".equals(filevo.getStringValue("id"))) {
			MessageBox.show(cardPanel, "相关流程已被删除, 不能进行查看.");//以前显示属性窗口，各个属性都为空，不如直接提示一下【李春娟/2014-03-03】
			return;
		}
		graphframe.setVisible(true);
	}
}