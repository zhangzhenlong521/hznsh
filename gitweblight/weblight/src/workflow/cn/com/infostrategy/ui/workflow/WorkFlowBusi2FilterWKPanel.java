package cn.com.infostrategy.ui.workflow;

import javax.swing.JSplitPane;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 工作流业务二次过滤界面
 * @author xch
 *
 */
public class WorkFlowBusi2FilterWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillListPanel billList_1 = new BillListPanel("PUB_WORKFLOWASSIGN_CODE1"); //
		BillListPanel billList_2 = new BillListPanel("pub_workflowassign_dynfilter2_CODE1"); //

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billList_1, billList_2); //
		splitPane.setDividerLocation(325); //
		this.add(splitPane); //
	}

}
