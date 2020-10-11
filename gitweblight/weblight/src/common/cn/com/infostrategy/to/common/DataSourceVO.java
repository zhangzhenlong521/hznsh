package cn.com.infostrategy.to.common;

import java.io.Serializable;

/**
 * 数据源VO
 * @author xch
 *
 */
public class DataSourceVO implements Serializable {

	private static final long serialVersionUID = 6740960093334812932L;

	private String name = null; //数据源名称
	private String descr = null; //数据源说明
	private String dbtype = null; //数据源类型
	private String dbversion = null; //数据库版本
	private String driver = null; //驱动程序
	private String dburl = null; //dburl
	private String user = null; //用户名
	private String pwd = null; //密码
	private String initial_context_factory = null;
	private String provider_url = null;

	private int initsize = 0; //初始化大小

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public String getInitial_context_factory() {
		return initial_context_factory;
	}

	public void setInitial_context_factory(String initial_context_factory) {
		this.initial_context_factory = initial_context_factory;
	}

	public String getProvider_url() {
		return provider_url;
	}

	public void setProvider_url(String provider_url) {
		this.provider_url = provider_url;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getDbtype() {
		return dbtype;
	}

	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getDburl() {
		return dburl;
	}

	public void setDburl(String dburl) {
		this.dburl = dburl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public int getInitsize() {
		return initsize;
	}

	public void setInitsize(int initsize) {
		this.initsize = initsize;
	}

	public String getDbversion() {
		return dbversion;
	}

	public void setDbversion(String _dbversion) {
		this.dbversion = _dbversion; //
	}

	public String toString() {
		return getName();
	}
}
