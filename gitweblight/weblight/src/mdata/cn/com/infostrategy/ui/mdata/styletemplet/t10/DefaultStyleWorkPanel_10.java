/**************************************************************************
 * $RCSfile: DefaultStyleWorkPanel_10.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t10;


/**
 * 第10种模板,即主子孙表,三张表一起保存
 * 三个表,第二张表有个外键指向主表主键,第三张表有个外键指向第二张表的主键
 * @author user
 *
 */
public class DefaultStyleWorkPanel_10 extends AbstractStyleWorkPanel_10 {

	public String getChildTableFK() {
		return (String) getCommandMap().get("CHILD_FORPKNAME"); // 子表外键
	}

	public String getChildTablePK() {
		return (String) getCommandMap().get("CHILD_PKNAME");
	}

	public String getChildTableTempletcode() {
		return (String) getCommandMap().get("CHILDTEMPLETE_CODE");
	}

	public String getGrandChildTableFK() {
		return (String) getCommandMap().get("GRANDCHILD_FORPKNAME");
	}

	public String getGrandChildTablePK() {
		return (String) getCommandMap().get("GRANDCHILD_PKNAME");
	}

	public String getGrandChildTableTempletcode() {
		return (String) getCommandMap().get("GRANDCHILDTEMPLETE_CODE");// 孙表代码
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
