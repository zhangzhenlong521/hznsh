package cn.com.infostrategy.ui.report.style3;

public class DefaultStyleReportPanel_3 extends AbstractStyleReportPanel_3 {

	private static final long serialVersionUID = 1L;
	public String billQueryTempletCode = null;
	public String buildDataClass = null;

	private DefaultStyleReportPanel_3() {
	}

	public DefaultStyleReportPanel_3(String _queryTempletCode, String _buildDataClass) {
		super(false); //
		this.billQueryTempletCode = _queryTempletCode; //
		this.buildDataClass = _buildDataClass; //
		initialize(); // ≥ı ºªØ“≥√Ê!
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
