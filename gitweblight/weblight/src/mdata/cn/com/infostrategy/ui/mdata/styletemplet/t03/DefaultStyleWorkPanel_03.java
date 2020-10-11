/**************************************************************************
 * $RCSfile: DefaultStyleWorkPanel_03.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t03;

/**
 * 风格模板3默认实现,即单表树卡类型!!!
 * @author xch
 *
 */
public class DefaultStyleWorkPanel_03 extends AbstractStyleWorkPanel_03 {

	private static final long serialVersionUID = 4731168295377251121L;

	public String getTempletcode() {
		return getMenuConfMapValueAsStr("$TempletCode"); //
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
