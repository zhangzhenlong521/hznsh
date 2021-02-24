/**************************************************************************
 * $RCSfile: DefaultStyleWorkPanel_09.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t09;

import java.util.ArrayList;

/**
 * 第9种模板,即多子表 一个主表拖几个子表,所有子表都有一个外键指向主表的主键!!! 所有子表的增删改都是直接在列表中直接进行,而不是切换到卡片!
 * @author xch
 *
 */
public class DefaultStyleWorkPanel_09 extends AbstractStyleWorkPanel_09 {

	public ArrayList getChildTableFK() {
		ArrayList child_templet_FK = new ArrayList();
		String childtempletcodes = (String) getCommandMap().get("CHILD_FORPKNAME");
		String[] ctemplets = childtempletcodes.split(",");
		for (int i = 0; i < ctemplets.length; i++)
			child_templet_FK.add(ctemplets[i]);
		return child_templet_FK;
	}

	public ArrayList getChildTablePK() {
		ArrayList child_templet_PK = new ArrayList();
		String childtempletcodes = (String) getCommandMap().get("CHILD_PKNAME");
		String[] ctemplets = childtempletcodes.split(",");
		for (int i = 0; i < ctemplets.length; i++)
			child_templet_PK.add(ctemplets[i]);
		return child_templet_PK;
	}

	public ArrayList getChildTableTempletcode() {
		ArrayList child_templet_code = new ArrayList();
		String childtempletcodes = (String) getCommandMap().get("CHILDTEMPLETE_CODE");
		String[] ctemplets = childtempletcodes.split(",");
		for (int i = 0; i < ctemplets.length; i++)
			child_templet_code.add(ctemplets[i]);
		return child_templet_code;
	}

	public String getParentTablePK() {
		return (String) getCommandMap().get("PARENT_PKNAME");
	}

	public String getParentTableTempletcode() {
		return (String) getCommandMap().get("PARENTTEMPLETE_CODE");
	}

	public String[] getSys_Selection_Path() {
		return (String[]) getCommandMap().get("SYS_SELECTION_PATH"); //
	}

	public String getCustomerpanel() {
		return (String) getCommandMap().get("CUSTOMERPANEL"); //
	}

	public String getUiinterceptor() {
		return (String) getCommandMap().get("UIINTERCEPTOR"); //
	}

	public String getBsinterceptor() {
		return (String) getCommandMap().get("BSINTERCEPTOR"); //
	}

	public boolean isShowsystembutton() {
		if (getCommandMap().get("SHOWSYSBUTTON") == null)
			return true;
		return ((String) getCommandMap().get("SHOWSYSBUTTON")).equals("是") ? true : false;
	}

	public String getCustBtnPanelName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isCanDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCanEdit() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCanInsert() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCanWorkFlowDeal() {
		// TODO Auto-generated method stub
		return false;
	}

}
