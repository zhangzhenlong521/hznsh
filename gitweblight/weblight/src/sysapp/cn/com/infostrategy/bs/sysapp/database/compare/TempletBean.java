package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.HashMap;

public class TempletBean {

	private String id = "";
	private String database = "";
	private String code = "";
	private String name = "";
	private HashMap subItem = null;
	private String tablename = "";
	private String dataconstraint = "";
	private String savedtablename = "";
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public HashMap getSubItem() {
		if(subItem == null){
			return new HashMap();
		}
		return subItem;
	}
	public void setSubItem(HashMap subItem) {
		this.subItem = subItem;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public String getDataconstraint() {
		return dataconstraint;
	}
	public void setDataconstraint(String dataconstraint) {
		this.dataconstraint = dataconstraint;
	}
	public String getSavedtablename() {
		return savedtablename;
	}
	public void setSavedtablename(String savedtablename) {
		this.savedtablename = savedtablename;
	}
	
}
