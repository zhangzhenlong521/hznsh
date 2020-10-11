/**************************************************************************
 * $RCSfile: DemoStyleWorkPanel_0A.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.format;

public class DemoStyleWorkPanel_0A extends AbstractStyleWorkPanel_0A {

	private static final long serialVersionUID = 1L;

	@Override
	public String getFormatFormula() {
		return "getList(\"PUB_USER_CODE_1\")";
	}

	@Override
	public String getUIInterceptName() {
		return null;
	}

}
