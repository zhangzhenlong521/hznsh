package cn.com.infostrategy.ui.report.style1;

/**
 * ��񱨱�1,����򵥵ı���,�����ϸ���ѯ��,�����Ǹ����!!
 * ����ѯ���е������͵���̨,��̨���һHashVO[],Ȼ��ǰ̨�����������������!!
 * @author xch
 *
 */
public class DefaultStyleReportPanel_1 extends AbstractStyleReportPanel_1 {

	private static final long serialVersionUID = 1L;
	public String billQueryTempletCode = null;
	public String billListTempletCode = null;
	public String buildDataClass = null;

	/**
	 * ���췽��..
	 */
	private DefaultStyleReportPanel_1() {
	}

	/**
	 * ָ����������
	 * @param _queryTempletCode
	 * @param _billTempletCode
	 * @param _className
	 */
	public DefaultStyleReportPanel_1(String _queryTempletCode, String _billTempletCode, String _className) {
		super(false); //
		billQueryTempletCode = _queryTempletCode;
		billListTempletCode = _billTempletCode;
		buildDataClass = _className;
		initialize(); //��ʼ��ҳ��!
	}

	@Override
	public String getBillQueryTempletCode() {
		return billQueryTempletCode;
	}

	@Override
	public String getBillListTempletCode() {
		return billListTempletCode;
	}

	@Override
	public String getBSBuildDataClass() {
		return buildDataClass;
	}

}
