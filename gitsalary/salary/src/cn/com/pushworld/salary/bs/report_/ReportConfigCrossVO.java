package cn.com.pushworld.salary.bs.report_;

public class ReportConfigCrossVO {
    private String cname = "����";
    private String cname_name = ""; // ������
    private String calculate = "���"; // ���㷽ʽ
    private String[] leftagain = new String[]{}; // �ϼ�;ƽ�� �ٴμ���_left
    private String[] leftagain_name = new String[]{}; // �ϼ�;ƽ�� �ٴμ���_left ������
    
	public String getCname() {
		if(cname.equals("����")){
			return cname+"_���";	
		}
		return cname+"_"+getCalculate();
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getCname_name() {
		if(cname_name.equals("")){
			cname_name = getCname();
		}
		return cname_name;
	}
	public void setCname_name(String cname_name) {
		this.cname_name = cname_name;
	}
	public String getCalculate() {
		return calculate;
	}
	public void setCalculate(String calculate) {
		this.calculate = calculate;
	}
	public String[] getLeftagain() {
		return leftagain;
	}
	public void setLeftagain(String[] leftagain) {
		this.leftagain = leftagain;
	}
	public String[] getLeftagain_name() {
		if(leftagain_name.length!=leftagain.length){
			leftagain_name = getLeftagain();
		}
		return leftagain_name;
	}
	public void setLeftagain_name(String[] leftagain_name) {
		this.leftagain_name = leftagain_name;
	}
}
