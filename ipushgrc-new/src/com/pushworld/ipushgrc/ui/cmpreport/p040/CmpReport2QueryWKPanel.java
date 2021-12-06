package com.pushworld.ipushgrc.ui.cmpreport.p040;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 合规专项报告查询!!!
 * 就是对合规报告(cmp_report2)的单列表
 * @author xch
 *
 */
public class CmpReport2QueryWKPanel extends AbstractWorkPanel {

	private BillListPanel billList = null; //

	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_REPORT2_CODE1"); //
		boolean wf = TBUtil.getTBUtil().getSysOptionBooleanValue("合规报告是否走工作流", true);//项目实施时，综合报告直接将结果提交，故增加该配置【loj/2015-05-21】
		if (!wf) {
			billList.setItemVisible("state", false);//如果不走工作流，则隐藏状态【loj/2015-05-21】
		}
		billList.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });//该列表以前没有按钮，很突兀，故增加浏览按钮【李春娟/2012-03-23】
		billList.repaintBillListButton();
		this.add(billList); //
	}

}
