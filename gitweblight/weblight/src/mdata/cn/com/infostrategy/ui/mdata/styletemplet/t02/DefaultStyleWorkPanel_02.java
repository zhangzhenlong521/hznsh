/**************************************************************************
 * $RCSfile: DefaultStyleWorkPanel_02.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t02;

/**
 * 默认的风格模板2
 * @author xch
 *
 */
public class DefaultStyleWorkPanel_02 extends AbstractStyleWorkPanel_02 {

	private static final long serialVersionUID = -7870282092481757876L; //

	public String getTempletcode() {
		return getMenuConfMapValueAsStr("$TempletCode"); //
	}

	
}
