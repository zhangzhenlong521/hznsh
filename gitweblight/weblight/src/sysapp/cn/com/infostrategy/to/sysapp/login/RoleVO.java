package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;

public class RoleVO implements Serializable {
	private static final long serialVersionUID = 5468856041510973251L;

	private String id;
	private String code;
	private String name;
	private String userdeptpk;
	private String userdeptcode;
	private String userdeptname;
	private String descr; //

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getUserdeptpk() {
		return userdeptpk;
	}

	public void setUserdeptpk(String userdeptpk) {
		this.userdeptpk = userdeptpk;
	}

	public String getUserdeptcode() {
		return userdeptcode;
	}

	public void setUserdeptcode(String userdeptcode) {
		this.userdeptcode = userdeptcode;
	}

	public String getUserdeptname() {
		return userdeptname;
	}

	public void setUserdeptname(String userdeptname) {
		this.userdeptname = userdeptname;
	}

}
