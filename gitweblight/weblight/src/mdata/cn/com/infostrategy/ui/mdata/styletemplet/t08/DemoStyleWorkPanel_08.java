package cn.com.infostrategy.ui.mdata.styletemplet.t08;

/**
 * ���ģ��8,�����ӱ�
 */
public class DemoStyleWorkPanel_08 extends AbstractStyleWorkPanel_08 {

	private static final long serialVersionUID = 6007513574700457910L;

	/**
	 * ����ģ�����
	 */
	public String getParentTableTempletcode() {
		return "demo_bill_CODE1";
	}

	/**
	 * �ӱ�ģ�����
	 */
	public String getChildTableTempletcode() {
		return "demo_bill_b_CODE1";
	}

	@Override
	public String getParentAssocField() {
		return "id";
	}

	@Override
	public String getChildAssocField() {
		return "parentid";
	}

	public String getCustBtnPanelName() {
		return null;
	}

	public String getUiinterceptor() {
		return null;
	}

	public String getBsinterceptor() {
		return null;
	}

}
