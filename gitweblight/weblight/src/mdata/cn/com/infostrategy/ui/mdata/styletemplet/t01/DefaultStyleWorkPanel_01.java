/**************************************************************************
 * $RCSfile: DefaultStyleWorkPanel_01.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t01;

public class DefaultStyleWorkPanel_01 extends AbstractStyleWorkPanel_01 {

	private static final long serialVersionUID = 1L;

	public String getTempletcode() {
		return getMenuConfMapValueAsStr("$TempletCode"); //
	}

	public String getCustBtnPanelName() {
		return getMenuConfMapValueAsStr("$CustPanel"); //
	}

	public String getUiinterceptor() {
		return getMenuConfMapValueAsStr("$UIIntercept"); //
	}

	public String getMenuConfMapValueAsStr() {
		return getMenuConfMapValueAsStr("$BSIntercept"); //
	}

}
