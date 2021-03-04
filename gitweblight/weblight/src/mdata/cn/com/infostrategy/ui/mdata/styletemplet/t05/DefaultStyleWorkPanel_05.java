package cn.com.infostrategy.ui.mdata.styletemplet.t05;

/**
 * 风格模板5的默认页面!!!
 * @author xch
 *
 */
public class DefaultStyleWorkPanel_05 extends AbstractStyleWorkPanel_05 {
	private static final long serialVersionUID = 1L;

	public String getTreeTempeltCode() {
		return getMenuConfMapValueAsStr("$TreeTempletCode"); //
	}

	public String getTreeAssocField() {
		return getMenuConfMapValueAsStr("$TreeJoinField"); //
	}

	public String getTableTempletCode() {
		return getMenuConfMapValueAsStr("$TableTempletCode"); //
	}

	public String getTableAssocField() {
		return getMenuConfMapValueAsStr("$TableJoinField"); //
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

}
