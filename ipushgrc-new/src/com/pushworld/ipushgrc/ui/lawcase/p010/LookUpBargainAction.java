package com.pushworld.ipushgrc.ui.lawcase.p010;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class LookUpBargainAction implements WLTActionListener {
	
	BillCardPanel cardPanel = null;
	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		BillVO billVO = cardPanel.getBillVO();
		String bargarinid=billVO.getStringValue("refbargain");
		if(bargarinid==null||bargarinid.trim().length()==0){
			MessageBox.show(cardPanel,"没有相关合同！");
			return;
		}
		BillVO[] vos=UIUtil.getBillVOsByDS(null, "select * from lbs_bargain where id="+bargarinid, UIUtil.getPub_Templet_1VO("LBS_BARGAIN_CODE1"));
		if(vos.length==0){
			MessageBox.show(cardPanel, "相关合同可能已经删除");
			return;
		}
		BillCardDialog carddia=new BillCardDialog(cardPanel,"合同查看", "LBS_BARGAIN_CODE1", 600, 800,WLTConstants.BILLDATAEDITSTATE_INIT, "id="+bargarinid);
		carddia.getBillcardPanel().setGroupVisiable("签约信息", true);
		carddia.setVisible(true);
	}
}
