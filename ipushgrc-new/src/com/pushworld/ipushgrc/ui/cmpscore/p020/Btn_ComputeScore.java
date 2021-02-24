package com.pushworld.ipushgrc.ui.cmpscore.p020;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class Btn_ComputeScore implements WLTActionListener {
	private TBUtil tbutil;
	public void actionPerformed(WLTActionEvent _event) throws Exception {
		BillCardPanel cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			MessageBox.show(cardPanel, "浏览状态不允许执行此操作！");
			return;
		}
		BillVO vo = cardPanel.getBillVO();
		String refrisks = vo.getStringValue("cmp_risk_ids");
		int scorelost_ref = 0;
		if (refrisks != null && !refrisks.equals("")) {
			String refRiskids = getTBUtil().getInCondition(refrisks);
			try {
				HashVO[] risks = UIUtil.getHashVoArrayByDS(null, "select scorelost from cmp_risk where id in(" + refRiskids + ")");
				for (int i = 0; i < risks.length; i++) {
					int lost = risks[i].getIntegerValue("scorelost",0);
					scorelost_ref +=lost;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cardPanel.setValueAt("scorelost_ref", new StringItemVO(scorelost_ref+""));
		
	}
	
	public TBUtil getTBUtil(){
		if(tbutil == null){
			tbutil = new TBUtil();
		}
		return tbutil;
	}
	

}
