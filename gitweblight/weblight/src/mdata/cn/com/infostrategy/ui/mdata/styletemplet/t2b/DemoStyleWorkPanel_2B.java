package cn.com.infostrategy.ui.mdata.styletemplet.t2b;

/**
 * 风格模板11的例子
 * @author xch
 *
 */
public class DemoStyleWorkPanel_2B extends AbstractStyleWorkPanel_2B {

	private static final long serialVersionUID = 2041852785071703768L;

	/**
	 * 主表模板编码
	 */
	protected String getParentTempletCode() {
		return "demo_bill_CODE1";
	}

	/**
	 * 主表关联字段
	 */
	protected String getParentAssocField() {
		return "id";
	}

	/**
	 * 子表模板编码
	 */
	protected String getChildTempletCode() {
		return "demo_bill_b_CODE1";
	}

	/**
	 * 子表关联字段
	 */
	protected String getChildAssocField() {
		return "parentid";
	}

	/**
	 * 用户自定义面板
	 */
	public String getCustBtnPanelName() {
		return null;
	}

	/**
	 * UI端拦截器
	 */
	public String getUiinterceptor() {
		return null;
	}

	/**
	 * BS端拦截器
	 */
	public String getBsinterceptor() {
		return null;
	}

}
