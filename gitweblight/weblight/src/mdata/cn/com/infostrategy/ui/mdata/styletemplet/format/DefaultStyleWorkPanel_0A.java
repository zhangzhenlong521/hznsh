/**************************************************************************
 * $RCSfile: DefaultStyleWorkPanel_0A.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.format;

public class DefaultStyleWorkPanel_0A extends AbstractStyleWorkPanel_0A {

	private static final long serialVersionUID = 1L;

	@Override
	public String getFormatFormula() {
		return (String) getCommandMap().get("FORMATFORMULA"); //公式说明
	}

	@Override
	public String getUIInterceptName() {
		return (String) getCommandMap().get("INTERCEPTNAME"); //拦截器说明
	}

}

