package cn.com.infostrategy.ui.mdata.styletemplet.t2b;

/**
 * ���ģ��11������
 * @author xch
 *
 */
public class DemoStyleWorkPanel_2B extends AbstractStyleWorkPanel_2B {

	private static final long serialVersionUID = 2041852785071703768L;

	/**
	 * ����ģ�����
	 */
	protected String getParentTempletCode() {
		return "demo_bill_CODE1";
	}

	/**
	 * ��������ֶ�
	 */
	protected String getParentAssocField() {
		return "id";
	}

	/**
	 * �ӱ�ģ�����
	 */
	protected String getChildTempletCode() {
		return "demo_bill_b_CODE1";
	}

	/**
	 * �ӱ�����ֶ�
	 */
	protected String getChildAssocField() {
		return "parentid";
	}

	/**
	 * �û��Զ������
	 */
	public String getCustBtnPanelName() {
		return null;
	}

	/**
	 * UI��������
	 */
	public String getUiinterceptor() {
		return null;
	}

	/**
	 * BS��������
	 */
	public String getBsinterceptor() {
		return null;
	}

}
