package cn.com.infostrategy.ui.mdata.styletemplet.wf1;

import cn.com.infostrategy.ui.workflow.AbstractWorkFlowStylePanel;

/**
 * ��һ�ַ��Ĺ��������!!!
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
