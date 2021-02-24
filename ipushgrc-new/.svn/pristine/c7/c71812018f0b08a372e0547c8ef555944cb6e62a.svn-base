package com.pushworld.ipushgrc.ui.score.p050;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

import com.pushworld.ipushgrc.ui.score.ScoreUIUtil;

/**
 * 违规积分-》积分减免申请 卡片中按钮查询已计积分的点击事件【李春娟/2013-05-16】
 * @author lcj
 * */

public class ShowUserScoreWLTAction implements WLTActionListener {
	public void actionPerformed(WLTActionEvent _event) throws Exception {
		BillCardPanel cardPanel = (BillCardPanel) _event.getBillPanelFrom();//取得按钮事件父面板
		BillVO billVO = cardPanel.getBillVO();
		if (billVO == null) {
			MessageBox.show(cardPanel, "当前记录为空,不能查看!");
			return;
		} else if (billVO.getStringValue("userid") == null || billVO.getStringValue("userid").equals("")) {
			MessageBox.show(cardPanel, "当前申请人为空,不能查看!");
			return;
		}
		String userid = billVO.getStringValue("userid");
		new ScoreUIUtil().showOneUserScore(cardPanel, null, billVO.getStringValue("corpid"), userid, billVO.getStringViewValue("userid"));
	}

}
