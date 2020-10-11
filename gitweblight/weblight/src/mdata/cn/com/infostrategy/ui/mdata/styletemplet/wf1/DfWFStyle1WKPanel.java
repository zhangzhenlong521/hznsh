package cn.com.infostrategy.ui.mdata.styletemplet.wf1;

import cn.com.infostrategy.ui.workflow.AbstractWorkFlowStylePanel;

/**
 * 第一种风格的工作流面板!!!
 * @author xch
 *
 */
public class DfWFStyle1WKPanel extends AbstractWorkFlowStylePanel {

	private static final long serialVersionUID = 1L;

	@Override
	public String getBillTempletCode() {
		return getMenuConfMapValueAsStr("$TempletCode");  //
	}

}
