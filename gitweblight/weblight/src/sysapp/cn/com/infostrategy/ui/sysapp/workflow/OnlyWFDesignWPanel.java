package cn.com.infostrategy.ui.sysapp.workflow;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

/**
 * 有时客户要求只查看工作流的维护界面,即其他流程图不显示!!
 * @author xch
 *
 */
public class OnlyWFDesignWPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		WorkFlowDesignWPanel wfPanel = new WorkFlowDesignWPanel() {
			@Override
			public String getWFProcessCondition() {
				return "wftype is null or wftype='工作流'"; //只能查看工作流的,像体系文件什么的都不能处理
			}

		}; //
		this.add(wfPanel); //
	}

}
