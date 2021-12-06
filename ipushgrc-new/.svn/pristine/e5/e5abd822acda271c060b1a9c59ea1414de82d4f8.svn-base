package com.pushworld.ipushgrc.ui.cmpevent.p180;

import javax.swing.JSplitPane;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 整改跟踪查看【李春娟/2013-08-26】
 * @author lcj
 *
 */
public class CmpAdjustTrackLookWKPanel extends AbstractWorkPanel implements BillListSelectListener, BillListAfterQueryListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5423260581405561469L;
	private BillListPanel billlist_proj = null; //
	private BillListPanel billList_eventtrack = null; //

	private String getUpCardPanelCode() {//得到分割器上面的卡片code
		return "CMP_EVENT_ADJUSTPROJECT_CODE3";
	}

	private String getDownListPanelCode() {//得到分割器的下方的列表code
		return "CMP_EVENT_TRACK_CODE1_x";
	}

	private String getDownLinkedColumn() {//得到下方列表与上方面板的关联字段,下方列表有个字段参照上方的id
		return "projectid";
	}

	public void initialize() {
		billlist_proj = new BillListPanel(getUpCardPanelCode()); //方案
		billlist_proj.addBillListSelectListener(this);
		billlist_proj.addBillListAfterQueryListener(this);//增加查询后事件，清空子表【李春娟/2012-08-08】
		WLTButton view_btn = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "查看明细");
		billlist_proj.addBatchBillListButton(new WLTButton[] { view_btn });
		billlist_proj.repaintBillListButton();
		billList_eventtrack = new BillListPanel(this.getDownListPanelCode()); //方案跟踪
		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billlist_proj, billList_eventtrack); //
		split.setDividerLocation(380); //
		this.add(split); //
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		billList_eventtrack.QueryDataByCondition(" " + this.getDownLinkedColumn() + "=" + _event.getCurrSelectedVO().getStringValue("id"));
	}

	//增加查询后事件，清空子表 
	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		billList_eventtrack.clearTable();
	}
}
