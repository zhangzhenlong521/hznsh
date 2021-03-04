/**************************************************************************
 * $RCSfile: DefaultStyleWorkPanel_04.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t04;

import java.util.HashMap;

public class DefaultStyleWorkPanel_04 extends AbstractStyleWorkPanel_04 {

	private static final long serialVersionUID = 1845996216411976505L; //

	public DefaultStyleWorkPanel_04() {
		super(); //
	}

	public DefaultStyleWorkPanel_04(HashMap _parMap) {
		super(_parMap);
	}

	@Override
	public String getTreeTempeltCode() {
		return (String) getMenuConfMap().get("$TreeTempletCode"); //
	}

	@Override
	public String getTreeAssocField() {
		return (String) getMenuConfMap().get("$TreeJoinField"); //
	}

	@Override
	public String getTableTempletCode() {
		return (String) getMenuConfMap().get("$TableTempletCode"); //
	}

	@Override
	public String getTableAssocField() {
		return (String) getMenuConfMap().get("$TableJoinField"); //
	}

	@Override
	public String getCustAfterInitClass() {
		return (String) getMenuConfMap().get("$CustAfterInitClass");
	}

	//�Ƿ�ɱ༭!!!
	public boolean isCanEdit() {
		if (getMenuConfMap() != null && getMenuConfMap().containsKey("�Ƿ�ɱ༭")) { //
			if ("N".equalsIgnoreCase((String) getMenuConfMap().get("�Ƿ�ɱ༭"))) { //
				return false;
			}
		}
		return super.isCanEdit();
	}
}
