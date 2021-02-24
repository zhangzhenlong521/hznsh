package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.ui.workflow.engine.WorkflowDynamicParticipateIfc;

/**
 * 选择本分行某一个角色!!!
 * @author xch
 *
 */
public class WDPUser_ReportLine2 extends AbstractWDPUser_ReportDeptLine implements WorkflowDynamicParticipateIfc {

	public WDPUser_ReportLine2(String _rolename) {
		super(_rolename);

	}

	@Override
	public String getReportDeptType() {
		return "2";
	}

}

