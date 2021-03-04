package cn.com.infostrategy.ui.mdata.styletemplet.t2a;

public class DefaultStyleWorkPanel_2A extends AbstractStyleWorkPanel_2A {

	private static final long serialVersionUID = -2607028435828523644L;

	/**
	 * ����ģ����
	 */
	protected String getParentTempletCode() {
		return (String) getCommandMap().get("ParentTempletCode");
	}

	/**
	 * ��������ֶ���
	 */
	protected String getParentAssocField() {
		return (String) getCommandMap().get("ParentAssocField");
	}

	/**
	 * �ӱ�ģ����
	 */
	protected String getChildTempletCode() {
		return (String) getCommandMap().get("ChildTempletCode");
	}

	/**
	 * �ӱ�����ֶ���
	 */
	protected String getChildAssocField() {
		return (String) getCommandMap().get("ChildAssocField");
	}

	/**
	 * �Զ��������
	 */
	public String getCustBtnPanelName() {
		return (String) getCommandMap().get("CustBtnPanelName");
	}

	/**
	 * UI����������������
	 */
	public String getUiinterceptor() {
		return (String) getCommandMap().get("Uiinterceptor");
	}

	/**
	 * BS����������������
	 */
	public String getBsinterceptor() {
		return (String) getCommandMap().get("Bsinterceptor");
	}

	/**
	 * �Ƿ���������
	 */
	public boolean isCanInsert() {
		return new Boolean((String) getCommandMap().get("isCanInsert")).booleanValue();
	}

	/**
	 * �Ƿ�����ɾ��
	 */
	public boolean isCanDelete() {
		return new Boolean((String) getCommandMap().get("isCanInsert")).booleanValue();
	}

	/**
	 * �Ƿ�����༭����
	 */
	public boolean isCanEdit() {
		return new Boolean((String) getCommandMap().get("isCanEdit")).booleanValue();
	}

	/**
	 * �Ƿ���ʾϵͳ��Ť��
	 */
	public boolean isShowsystembutton() {
		return new Boolean((String) getCommandMap().get("isShowsystembutton")).booleanValue();
	}

	/**
	 * �Ƿ���ʾ��������ť��
	 */
	public boolean isCanWorkFlowDeal() {
		return new Boolean((String) getCommandMap().get("isShowWorkFlowButton")).booleanValue();
	}

}
