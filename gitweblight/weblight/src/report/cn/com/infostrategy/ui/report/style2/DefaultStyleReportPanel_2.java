package cn.com.infostrategy.ui.report.style2;

public class DefaultStyleReportPanel_2 extends AbstractStyleReportPanel_2 {

	private static final long serialVersionUID = 1L;
	public String billQueryTempletCode = null;
	public String buildDataClass = null;

	private DefaultStyleReportPanel_2() {
	}

	public DefaultStyleReportPanel_2(String _queryTempletCode, String _buildDataClass) {
		super(false); //
		this.billQueryTempletCode = _queryTempletCode; //
		this.buildDataClass = _buildDataClass; //
		initialize(); //≥ı ºªØ“≥√Ê!
	}

	@Override
	public String getBillQueryTempletCode() {
		return this.billQueryTempletCode; //
	}

	@Override
	public String getBSBuildDataClass() {
		return this.buildDataClass;
	}

}
