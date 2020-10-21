package com.pushworld.ipushgrc.ui.wfrisk.p040;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

import com.pushworld.ipushgrc.ui.wfrisk.p010.WFAndRiskEditWKPanel;

public class WFAndRiskQueryByBsactWKPanel extends AbstractWorkPanel implements BillTreeSelectListener {

	private BillTreePanel billtree_bsact;
	private BillListPanel billlist_cmpfile;
	private boolean editable;//

	public WFAndRiskQueryByBsactWKPanel(boolean _editable) {
		this.editable = _editable; //
		initialize(); //
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billtree_bsact = new BillTreePanel("BSD_BSACT_CODE1");
		billtree_bsact.setMoveUpDownBtnVisiable(false);
		billtree_bsact.queryDataByCondition(null);
		billtree_bsact.addBillTreeSelectListener(this);

		WFAndRiskEditWKPanel panel_wfAndRisk = new WFAndRiskEditWKPanel();
		panel_wfAndRisk.setEditable(this.editable);
		panel_wfAndRisk.initialize();
		billlist_cmpfile = panel_wfAndRisk.getBillList_cmpfile();
		billlist_cmpfile.setQuickQueryPanelVisiable(false);
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billtree_bsact, panel_wfAndRisk);
		splitPane.setDividerLocation(240);
		this.add(splitPane);
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (billtree_bsact.getSelectedNode() == null) {
			billlist_cmpfile.clearTable();
			return;
		}
		if (billtree_bsact.getSelectedNode().isRoot()) {//如果选中根节点，则提示【李春娟/2012-03-14】
			billlist_cmpfile.clearTable();
			return;
		}
		BillVO[] billvos = billtree_bsact.getSelectedChildPathBillVOs();
		List idlist = new ArrayList();
		for (int i = 0; i < billvos.length; i++) {
			idlist.add(billvos[i].getStringValue("id"));
		}
		String insql = new TBUtil().getInCondition(idlist);//有防止id串过长，sql报错的处理
		billlist_cmpfile.QueryDataByCondition("filestate='3' and bsactid in(" + insql + ")");
	}
}
