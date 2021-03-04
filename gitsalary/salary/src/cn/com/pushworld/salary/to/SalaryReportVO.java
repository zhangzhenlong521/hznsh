package cn.com.pushworld.salary.to;

public class SalaryReportVO {
	private String[] title = new String[]{"序号"};
	private String[] field = new String[]{"序号"};
	private String[] field_len = new String[]{"序号"};
	private String[] types = new String[]{};
	private String[] types_nocou = new String[]{};
	private String[] types_field = new String[]{};
	private String[] types_field_nocou = new String[]{};
	private String reportname = "报表";
	private String[] v_count_type = new String[]{}; //"合计","平均"
	private String[] h_count_type = new String[]{}; //"合计","平均"
	private int titlerows = 1; 
	private String excel_code = "A";
	
	public String[] getTitle() {
		return title;
	}
	public void setTitle(String[] title) {
		this.title = title;
	}
	public String[] getField() {
		return field;
	}
	public void setField(String[] field) {
		this.field = field;
	}
	public String[] getField_len() {
		return field_len;
	}
	public void setField_len(String[] field_len) {
		this.field_len = field_len;
	}
	public String[] getTypes() {
		return types;
	}
	public void setTypes(String[] types) {
		this.types = types;
	}
	public String[] getTypes_nocou() {
		return types_nocou;
	}
	public void setTypes_nocou(String[] types_nocou) {
		this.types_nocou = types_nocou;
	}
	public String[] getTypes_field() {
		return types_field;
	}
	public void setTypes_field(String[] types_field) {
		this.types_field = types_field;
	}
	public String[] getTypes_field_nocou() {
		return types_field_nocou;
	}
	public void setTypes_field_nocou(String[] types_field_nocou) {
		this.types_field_nocou = types_field_nocou;
	}
	public String getReportname() {
		return reportname;
	}
	public void setReportname(String reportname) {
		this.reportname = reportname;
	}
	public String[] getV_count_type() {
		return v_count_type;
	}
	public void setV_count_type(String[] v_count_type) {
		this.v_count_type = v_count_type;
	}
	public String[] getH_count_type() {
		return h_count_type;
	}
	public void setH_count_type(String[] h_count_type) {
		this.h_count_type = h_count_type;
	}
	public int getTitlerows() {
		return titlerows;
	}
	public void setTitlerows(int titlerows) {
		this.titlerows = titlerows;
	}
	public String getExcel_code() {
		return excel_code;
	}
	public void setExcel_code(String excel_code) {
		this.excel_code = excel_code;
	} 
}
