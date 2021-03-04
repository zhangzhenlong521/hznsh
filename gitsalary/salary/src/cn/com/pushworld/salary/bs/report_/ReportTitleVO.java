package cn.com.pushworld.salary.bs.report_;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class ReportTitleVO {
	private String name = "";
	private int level = 0;
	private boolean child = false;
	private Set<String> childset = new LinkedHashSet<String>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isChild() {
		return child;
	}
	public void setChild(boolean child) {
		this.child = child;
	}
	public Set<String> getChildset() {
		return childset;
	}
	
    /**
	 * 获取子节点数组
	 */
	public String[] getChildsetArrays() {
		return (String[])this.childset.toArray(new String[this.childset.size()]);
	}
	
	public void childsetAdd(String _value) {
		this.childset.add(_value);
	}

}
