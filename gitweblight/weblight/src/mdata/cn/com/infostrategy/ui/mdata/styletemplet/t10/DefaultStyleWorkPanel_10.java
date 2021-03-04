/**************************************************************************
 * $RCSfile: DefaultStyleWorkPanel_10.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t10;


/**
 * ��10��ģ��,���������,���ű�һ�𱣴�
 * ������,�ڶ��ű��и����ָ����������,�����ű��и����ָ��ڶ��ű������
 * @author user
 *
 */
public class DefaultStyleWorkPanel_10 extends AbstractStyleWorkPanel_10 {

	public String getChildTableFK() {
		return (String) getCommandMap().get("CHILD_FORPKNAME"); // �ӱ����
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
		return (String) getCommandMap().get("GRANDCHILDTEMPLETE_CODE");// ������
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
		return ((String) getCommandMap().get("SHOWSYSBUTTON")).equals("��") ? true : false;
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
