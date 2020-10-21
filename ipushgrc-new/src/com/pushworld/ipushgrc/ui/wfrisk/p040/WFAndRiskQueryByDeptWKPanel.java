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

public class WFAndRiskQueryByDeptWKPanel extends AbstractWorkPanel implements BillTreeSelectListener {

	private BillTreePanel billtree_dept;
	private BillListPanel billlist_cmpfile;
	private boolean editable;//

	public WFAndRiskQueryByDeptWKPanel(boolean _editable) {
		this.editable = _editable; //
		initialize(); //
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billtree_dept = new BillTreePanel("PUB_CORP_DEPT_CODE1");
		billtree_dept.setMoveUpDownBtnVisiable(false);
		billtree_dept.queryDataByCondition(null);
		billtree_dept.addBillTreeSelectListener(this);

		WFAndRiskEditWKPanel panel_wfAndRisk = new WFAndRiskEditWKPanel();
		panel_wfAndRisk.setEditable(this.editable);
		panel_wfAndRisk.initialize();
		billlist_cmpfile = panel_wfAndRisk.getBillList_cmpfile();
		billlist_cmpfile.setQuickQueryPanelVisiable(false);
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billtree_dept, panel_wfAndRisk);
		splitPane.setDividerLocation(240);
		this.add(splitPane);
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billvo = billtree_dept.getSelectedVO();
		if (billvo == null) {
			return;
		}
		BillVO[] billvos = billtree_dept.getSelectedChildPathBillVOs();
		List idlist = new ArrayList();
		for (int i = 0; i < billvos.length; i++) {
			idlist.add(billvos[i].getStringValue("id"));
		}
		String insql = new TBUtil().getInCondition(idlist);//有防止id串过长，sql报错的处理
		billlist_cmpfile.QueryDataByCondition("filestate='3' and blcorpid in(" + insql + ")");
	}
}
