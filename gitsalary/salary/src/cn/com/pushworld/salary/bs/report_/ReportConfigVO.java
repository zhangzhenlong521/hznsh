package cn.com.pushworld.salary.bs.report_;

public class ReportConfigVO {
	private String[] lefttitle = new String[]{}; // A;B
	private String[] toptitle = new String[]{}; // C;D
	private String[] cross = new String[]{}; // 数量;B 
	private ReportConfigTitleVO[] leftvos = new ReportConfigTitleVO[]{}; 
	private ReportConfigTitleVO[] topvos = new ReportConfigTitleVO[]{}; 
	private ReportConfigCrossVO[] crossvos = new ReportConfigCrossVO[]{};
	private String[] topagain = new String[]{}; // 合计;平均 再次计算_top
	private String[] topagain_name = new String[]{}; // 合计;平均 再次计算_top 重命名
	private boolean separation = true; 
    private boolean front_leftagain = false; // 再次计算_left 是否在前
    private boolean front_topagain = false; // 再次计算_top 是否在前
    private boolean combine_left = true; // 左表头 是否合并
    private String sql = ""; // sql
    
	public String[] getLefttitle() {
		return lefttitle;
	}
	public void setLefttitle(String[] lefttitle) {
		this.lefttitle = lefttitle;
	}
	public String[] getToptitle() {
		return toptitle;
	}
	public void setToptitle(String[] toptitle) {
		this.toptitle = toptitle;
	}
	public String[] getCross() {
		return cross;
	}
	public void setCross(String[] cross) {
		this.cross = cross;
	}
	public ReportConfigTitleVO[] getLeftvos() {
		return leftvos;
	}
	public void setLeftvos(ReportConfigTitleVO[] leftvos) {
		this.leftvos = leftvos;
	}
	public ReportConfigTitleVO[] getTopvos() {
		return topvos;
	}
	public void setTopvos(ReportConfigTitleVO[] topvos) {
		this.topvos = topvos;
	}
	public ReportConfigCrossVO[] getCrossvos() {
		return crossvos;
	}
	public void setCrossvos(ReportConfigCrossVO[] crossvos) {
		this.crossvos = crossvos;
	}
	public String[] getTopagain() {
		return topagain;
	}
	public void setTopagain(String[] topagain) {
		this.topagain = topagain;
	}
	public String[] getTopagain_name() {
		if(topagain_name.length!=topagain.length){
			topagain_name = getTopagain();
		}
		return topagain_name;
	}
	public void setTopagain_name(String[] topagain_name) {
		this.topagain_name = topagain_name;
	}
	public boolean isSeparation() {
		return separation;
	}
	public void setSeparation(boolean separation) {
		this.separation = separation;
	}
	public boolean isFront_leftagain() {
		return front_leftagain;
	}
	public void setFront_leftagain(boolean front_leftagain) {
		this.front_leftagain = front_leftagain;
	}
	public boolean isFront_topagain() {
		return front_topagain;
	}
	public void setFront_topagain(boolean front_topagain) {
		this.front_topagain = front_topagain;
	}
	public boolean isCombine_left() {
		return combine_left;
	}
	public void setCombine_left(boolean combine_left) {
		this.combine_left = combine_left;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
}
