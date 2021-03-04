package cn.com.pushworld.salary.ui.target.p060;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTableImport;
import cn.com.infostrategy.ui.workflow.WorkFlowUITool;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowEngineUIIntercept;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel;

/**
 * @author haoming
 * create by 2013-8-24
 */
public class ScoreProcessUIIntercept extends WorkFlowEngineUIIntercept {
	public void afterOpenWFProcessPanel(WorkFlowProcessPanel processPanel, String billtype, String busitype, BillVO billvo, HashVO currActivity, WLTHashMap otherParMap) throws Exception {
		WorkFlowUITool wfUItool = new WorkFlowUITool(currActivity); //
		if (currActivity == null || wfUItool.getCurrActivityCode().equalsIgnoreCase("start")) {
			processPanel.getBillCardPanel().setGroupExpandable("评分详情", false);
		}
		CardCPanel_ChildTableImport cardimport = (CardCPanel_ChildTableImport) processPanel.getBillCardPanel().getCompentByKey("itemids");
		if (cardimport != null) {
			cardimport.getBillListPanel().putClientProperty("reedit", "Y"); //强制设置重新选择分数的框可以编辑
		}
	}
}
