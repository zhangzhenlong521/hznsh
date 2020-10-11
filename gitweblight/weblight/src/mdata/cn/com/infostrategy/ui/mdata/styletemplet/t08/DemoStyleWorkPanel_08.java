package cn.com.infostrategy.ui.mdata.styletemplet.t08;

/**
 * 风格模板8,即主子表
 */
public class DemoStyleWorkPanel_08 extends AbstractStyleWorkPanel_08 {

	private static final long serialVersionUID = 6007513574700457910L;

	/**
	 * 主表模板编码
	 */
	public String getParentTableTempletcode() {
		return "demo_bill_CODE1";
	}

	/**
	 * 子表模板编码
	 */
	public String getChildTableTempletcode() {
		return "demo_bill_b_CODE1";
	}

	@Override
	public String getParentAssocField() {
		return "id";
	}

	@Override
	public String getChildAssocField() {
		return "parentid";
	}

	public String getCustBtnPanelName() {
		return null;
	}

	public String getUiinterceptor() {
		return null;
	}

	public String getBsinterceptor() {
		return null;
	}

}
