package cn.com.pushworld.salary.bs.report_;

public class ReportConfigTitleVO {
    private String tname = "";
    private String[] subagain = new String[]{}; // 小合计;小平均 小计计算
    private String[] subagain_name = new String[]{}; // 小合计;小平均 小计计算 重命名
    private String[] orders = new String[]{}; // 排序
    
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}
	public String[] getSubagain() {
		return subagain;
	}
	public void setSubagain(String[] subagain) {
		this.subagain = subagain;
	}
	public String[] getSubagain_name() {
		if(subagain_name.length!=subagain.length){
			subagain_name = getSubagain();
		}
		return subagain_name;
	}
	public void setSubagain_name(String[] subagain_name) {
		this.subagain_name = subagain_name;
	}
	public String[] getOrders() {
		return orders;
	}
	public void setOrders(String[] orders) {
		this.orders = orders;
	}
}
