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
			MessageBox.show(cardPanel,"û����غ�ͬ��");
			return;
		}
		BillVO[] vos=UIUtil.getBillVOsByDS(null, "select * from lbs_bargain where id="+bargarinid, UIUtil.getPub_Templet_1VO("LBS_BARGAIN_CODE1"));
		if(vos.length==0){
			MessageBox.show(cardPanel, "��غ�ͬ�����Ѿ�ɾ��");
			return;
		}
		BillCardDialog carddia=new BillCardDialog(cardPanel,"��ͬ�鿴", "LBS_BARGAIN_CODE1", 600, 800,WLTConstants.BILLDATAEDITSTATE_INIT, "id="+bargarinid);
		carddia.getBillcardPanel().setGroupVisiable("ǩԼ��Ϣ", true);
		carddia.setVisible(true);
	}
}