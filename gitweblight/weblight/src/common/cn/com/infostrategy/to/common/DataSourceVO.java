package cn.com.infostrategy.to.common;

import java.io.Serializable;

/**
 * ����ԴVO
 * @author xch
 *
 */
public class DataSourceVO implements Serializable {

	private static final long serialVersionUID = 6740960093334812932L;

	private String name = null; //����Դ����
	private String descr = null; //����Դ˵��
	private String dbtype = null; //����Դ����
	private String dbversion = null; //���ݿ�汾
	private String driver = null; //��������
	private String dburl = null; //dburl
	private String user = null; //�û���
	private String pwd = null; //����
	private String initial_context_factory = null;
	private String provider_url = null;

	private int initsize = 0; //��ʼ����С

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
