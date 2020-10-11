package cn.com.infostrategy.ui.mdata.styletemplet.t02;

public class CommonHyperLinkworkPanel_02 extends AbstractStyleWorkPanel_02 {

	private static final long serialVersionUID = -7531361295717624499L;
	String str_templetCode = null;

	public CommonHyperLinkworkPanel_02(String _templetCode) {
		super();  //
		str_templetCode = _templetCode;
	}

	public String getTempletcode() {
		return str_templetCode;
	}

	public String getBsinterceptor() {
		return null;
	}

	public String getCustBtnPanelName() {
		return null;
	}

	public String getUiinterceptor() {
		return null;
	}

	public boolean isCanInsert() {
		return true;
	}

	public boolean isCanDelete() {
		return true;
	}

	public boolean isCanEdit() {
		return true; //
	}

	public boolean isShowsystembutton() {
		return true;
	}

	public boolean isCanWorkFlowDeal() {
		return false;
	}

}
