package cn.com.infostrategy.ui.report.style1;

/**
 * 风格报表1,即最简单的报表,上面上个查询框,下面是个表格!!
 * 将查询框中的条件送到后台,后台输出一HashVO[],然后前台将该数组塞到表格中!!
 * @author xch
 *
 */
public class DefaultStyleReportPanel_1 extends AbstractStyleReportPanel_1 {

	private static final long serialVersionUID = 1L;
	public String billQueryTempletCode = null;
	public String billListTempletCode = null;
	public String buildDataClass = null;

	/**
	 * 构造方法..
	 */
	private DefaultStyleReportPanel_1() {
	}

	/**
	 * 指定本个参数
	 * @param _queryTempletCode
	 * @param _billTempletCode
	 * @param _className
	 */
	public DefaultStyleReportPanel_1(String _queryTempletCode, String _billTempletCode, String _className) {
		super(false); //
		billQueryTempletCode = _queryTempletCode;
		billListTempletCode = _billTempletCode;
		buildDataClass = _className;
		initialize(); //初始化页面!
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
