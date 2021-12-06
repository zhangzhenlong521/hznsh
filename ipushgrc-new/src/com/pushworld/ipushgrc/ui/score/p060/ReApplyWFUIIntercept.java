package com.pushworld.ipushgrc.ui.score.p060;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowEngineUIIntercept;

/**
 * 违规积分减免申请和复议申请UI端流程拦截器【李春娟/2013-05-13】
 * 与该类相对应的还有BS端流程拦截器com.pushworld.ipushgrc.bs.score.p060.ReApplyWFBSIntercept
 * @author lcj
 *
 */
public class ReApplyWFUIIntercept extends WorkFlowEngineUIIntercept {

	/**
	 * 打开处理面后,只有注册在流程中,这个方法才会被调用!!!
	 * @param _processPanel
	 * @throws Exception
	 */
	public void afterOpenWFProcessPanel(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billVO, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		BillCardPanel billCardPanel = _processPanel.getBillCardPanel();
		String userid = billCardPanel.getRealValueAt("userid");//【申请人】
		if (_busitype.contains("减免")) {//如果是减免申请
			if (_currActivity == null || "未审核".equals(_currActivity.getStringValue("state")) || (userid != null && userid.equals(ClientEnvironment.getCurrLoginUserVO().getId()))) {//如果是发起环节或发起人【李春娟/2014-12-16】
				billCardPanel.setGroupVisiable("审核信息", false);
			} else {
				billCardPanel.setVisiable(new String[] { "examineuser", "examinedept", "examinedate" }, false);
				billCardPanel.setEditable(false);
				billCardPanel.setEditable("dealtype", true);
				billCardPanel.setEditable("realscore", true);
				billCardPanel.setEditable("dealreason", true);
			}
		} else if (_busitype.contains("复议")) {//如果是复议申请
			if (_currActivity == null || "未复议".equals(_currActivity.getStringValue("state")) || (userid != null && userid.equals(ClientEnvironment.getCurrLoginUserVO().getId()))) {//如果是发起环节或发起人【李春娟/2014-12-16】
				billCardPanel.setVisiable(new String[] { "otherremark", "opinion", "rescore", "remoney", "rescoredesc", "reuserid", "redeptid", "redate", "state" }, false);
			} else {//如果不是发起环节则需要隐藏复议审核人，复议日期和状态的同时需要将申请复议理由设置为不可编辑
				billCardPanel.setVisiable(new String[] { "reuserid", "redeptid", "redate", "state" }, false);
				billCardPanel.setEditable(false);
				billCardPanel.setEditable("otherremark", true);
				billCardPanel.setEditable("opinion", true);
				billCardPanel.setEditable("rescore", true);
				billCardPanel.setEditable("remoney", true);
				billCardPanel.setEditable("rescoredesc", true);
			}
			billCardPanel.setGroupVisiable("最终结果", false);
		}
	}
}
