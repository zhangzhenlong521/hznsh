package com.pushworld.ipushgrc.ui.lawcase.p020;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class LookUpCaseAction implements WLTActionListener {

	public void actionPerformed(WLTActionEvent _event) throws Exception {

		BillCardPanel cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		BillVO billVO = cardPanel.getBillVO();
		RefItemVO itemvo = billVO.getRefItemVOValue("refcase");
		if(itemvo==null||itemvo.getId()==null||itemvo.getId().trim().equals("")){
			MessageBox.show(cardPanel,"没有相关主诉案件！");
			return;
		}
		BillVO[] vos=UIUtil.getBillVOsByDS(null, "select * from lbs_case where id="+itemvo.getId(), UIUtil.getPub_Templet_1VO("LBS_CASE_CODE1"));
		if(vos.length==0){
			MessageBox.show(cardPanel, "相关主诉案件可能已经删除！");
			return;
		}
		BillCardDialog carddia=new BillCardDialog(cardPanel,"案件查看", "LBS_CASE_CODE1", 600, 800, vos[0]);
		carddia.setSaveBtnVisiable(false);
		carddia.getBtn_confirm().setVisible(false);
		carddia.setVisible(true);
	

	}

}
