package cn.com.infostrategy.ui.mdata.styletemplet.t08;

/**
 * 第8种模板,即主子表模板!!!
 * 主表是卡片,子表是列表
 * @author sunxf
 */
public class DefaultStyleWorkPanel_08 extends AbstractStyleWorkPanel_08 {

	private static final long serialVersionUID = -9032511661963080263L;

	public String getParentTableTempletcode() {
		return getMenuConfMapValueAsStr("$ParentTempletCode"); //
	}

	public String getParentAssocField() {
		return getMenuConfMapValueAsStr("$ParentJoinField"); //
	}

	public String getChildTableTempletcode() {
		return getMenuConfMapValueAsStr("$ChildTempletCode"); //
	}

	public String getChildAssocField() {
		return getMenuConfMapValueAsStr("$ChildJoinField"); //
	}

	public String getChildTableFK() {
		return (String) getCommandMap().get("CHILD_FORPKNAME");
	}

	public String getCustBtnPanelName() {
		return getMenuConfMapValueAsStr("$CustPanel"); //
	}

	public String getUiinterceptor() {
		return getMenuConfMapValueAsStr("$UIIntercept"); //
	}

	public String getBsinterceptor() {
		return getMenuConfMapValueAsStr("$BSIntercept"); //
	}

	public boolean isShowsystembutton() {
		String str_isshowBtn = getMenuConfMapValueAsStr("$IsShowSysButton"); //
		if (str_isshowBtn != null && str_isshowBtn.equals("N")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isCanDelete() {
		return true;
	}

	public boolean isCanEdit() {
		return true;
	}

	public boolean isCanInsert() {
		return true;
	}

	public boolean isCanWorkFlowDeal() {
		return false;
	}
}
