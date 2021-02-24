/**************************************************************************
 * $RCSfile: DefaultStyleWorkPanel_07.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t07;

import javax.swing.JSplitPane;

/**
 * 风格模板7的默认设计,通过菜单配置双击打开时调用
 * @author xch
 *
 */
public class DefaultStyleWorkPanel_07 extends AbstractStyleWorkPanel_07 {

	private static final long serialVersionUID = 2600480660902071525L;

	public String getParentTempletCode() {
		return getMenuConfMapValueAsStr("$ParentTempletCode"); //
	}

	public String getParentAssocField() {
		return getMenuConfMapValueAsStr("$ParentJoinField"); //
	}

	public String getChildTempletCode() {
		return getMenuConfMapValueAsStr("$ChildTempletCode"); //
	}

	public String getChildAssocField() {
		return getMenuConfMapValueAsStr("$ChildJoinField"); //
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

	public int getOrientation() {
		String str_Orientation = getMenuConfMapValueAsStr("$Layout"); //
		if (str_Orientation == null || str_Orientation.trim().equals("")) {
			return JSplitPane.VERTICAL_SPLIT; //默认是上下
		}

		if (str_Orientation.equals("上下")) {
			return JSplitPane.VERTICAL_SPLIT;
		} else if (str_Orientation.equals("左右")) {
			return JSplitPane.HORIZONTAL_SPLIT;
		} else {
			return JSplitPane.VERTICAL_SPLIT; //默认是上下
		}
	}

	public boolean isCanInsert() {
		return true;
	}

	public boolean isCanEdit() {
		return true;
	}

	public boolean isCanDelete() {
		return true;
	}

	public boolean isCanWorkFlowDeal() {
		return false;
	}

}
