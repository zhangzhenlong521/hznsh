package cn.com.infostrategy.ui.workflow.design;

import cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08;

public class MWPanel_WorkFlowActivity extends AbstractStyleWorkPanel_08 {

	private static final long serialVersionUID = -1386292573505454495L;

	@Override
	public String getParentTableTempletcode() {
		return "pub_wf_process_ref";
	}

	@Override
	public String getChildTableTempletcode() {
		return "pub_wf_activity_CODE2";
	}

	@Override
	public String getParentAssocField() {
		return "id";
	}

	@Override
	public String getChildAssocField() {
		return "processid";
	}

	@Override
	public String getCustBtnPanelName() {
		return null;
	}

	@Override
	public String getUiinterceptor() {
		return null;
	}

	@Override
	public String getBsinterceptor() {
		return null;
	}
}
