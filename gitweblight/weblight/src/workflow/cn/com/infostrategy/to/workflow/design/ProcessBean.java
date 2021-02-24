package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

public class ProcessBean implements Serializable {

	private static final long serialVersionUID = 3510486617371440273L;

	private String id;
	private String code;
	private String name;

	private String wfeguiintercept; //UI端统一拦截器,需继承于抽象类WorkFlowEngineUIIntercept
	private String wfegbsintercept; //BS端统一拦截器,需继承于抽象类WorkFlowEngineBSIntercept

	private String userdef01;
	private String userdef02;
	private String userdef03;
	private String userdef04;

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

	public String getWfeguiintercept() {
		return wfeguiintercept;
	}

	public void setWfeguiintercept(String wfeguiintercept) {
		this.wfeguiintercept = wfeguiintercept;
	}

	public String getWfegbsintercept() {
		return wfegbsintercept;
	}

	public void setWfegbsintercept(String wfegbsintercept) {
		this.wfegbsintercept = wfegbsintercept;
	}

	public String getUserdef01() {
		return userdef01;
	}

	public void setUserdef01(String userdef01) {
		this.userdef01 = userdef01;
	}

	public String getUserdef02() {
		return userdef02;
	}

	public void setUserdef02(String userdef02) {
		this.userdef02 = userdef02;
	}

	public String getUserdef03() {
		return userdef03;
	}

	public void setUserdef03(String userdef03) {
		this.userdef03 = userdef03;
	}

	public String getUserdef04() {
		return userdef04;
	}

	public void setUserdef04(String userdef04) {
		this.userdef04 = userdef04;
	}

}
