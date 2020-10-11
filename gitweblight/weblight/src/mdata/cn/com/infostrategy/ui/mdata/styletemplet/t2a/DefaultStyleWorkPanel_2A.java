package cn.com.infostrategy.ui.mdata.styletemplet.t2a;

public class DefaultStyleWorkPanel_2A extends AbstractStyleWorkPanel_2A {

	private static final long serialVersionUID = -2607028435828523644L;

	/**
	 * 主表模板名
	 */
	protected String getParentTempletCode() {
		return (String) getCommandMap().get("ParentTempletCode");
	}

	/**
	 * 主表关联字段名
	 */
	protected String getParentAssocField() {
		return (String) getCommandMap().get("ParentAssocField");
	}

	/**
	 * 子表模板名
	 */
	protected String getChildTempletCode() {
		return (String) getCommandMap().get("ChildTempletCode");
	}

	/**
	 * 子表关联字段名
	 */
	protected String getChildAssocField() {
		return (String) getCommandMap().get("ChildAssocField");
	}

	/**
	 * 自定义面板名
	 */
	public String getCustBtnPanelName() {
		return (String) getCommandMap().get("CustBtnPanelName");
	}

	/**
	 * UI端拦截器反射类名
	 */
	public String getUiinterceptor() {
		return (String) getCommandMap().get("Uiinterceptor");
	}

	/**
	 * BS端拦截器反射类名
	 */
	public String getBsinterceptor() {
		return (String) getCommandMap().get("Bsinterceptor");
	}

	/**
	 * 是否允许新增
	 */
	public boolean isCanInsert() {
		return new Boolean((String) getCommandMap().get("isCanInsert")).booleanValue();
	}

	/**
	 * 是否允许删除
	 */
	public boolean isCanDelete() {
		return new Boolean((String) getCommandMap().get("isCanInsert")).booleanValue();
	}

	/**
	 * 是否允许编辑操作
	 */
	public boolean isCanEdit() {
		return new Boolean((String) getCommandMap().get("isCanEdit")).booleanValue();
	}

	/**
	 * 是否显示系统按扭栏
	 */
	public boolean isShowsystembutton() {
		return new Boolean((String) getCommandMap().get("isShowsystembutton")).booleanValue();
	}

	/**
	 * 是否显示工作流按钮栏
	 */
	public boolean isCanWorkFlowDeal() {
		return new Boolean((String) getCommandMap().get("isShowWorkFlowButton")).booleanValue();
	}

}
