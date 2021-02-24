package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

public class ProcessVO implements Serializable {

	private static final long serialVersionUID = 1823298054857554846L;

	private String id = null;

	private String code = null; //编码
	private String name = null; //名称
	private String wftype = null; //类型,目前有两种:工作流,体系流程

	private String wfeguiintercept; //UI端统一拦截器,需继承于抽象类WorkFlowEngineUIIntercept
	private String wfegbsintercept; //BS端统一拦截器,需继承于抽象类WorkFlowEngineBSIntercept

	private String userdef01 = null; //
	private String userdef02 = null; //
	private String userdef03 = null; //
	private String userdef04 = null; //
	private String userdef05 = null; //
	private String userdef06 = null; //
	private String userdef07 = null; //
	private String userdef08 = null; //
	private String userdef09 = null; //
	private String userdef10 = null; //
	private String cmpfileid = null; //

	private ActivityVO[] activityVOs = null; //所有结点
	private TransitionVO[] transitionVOs = null; //连结
	private GroupVO[] groupVOs = null; //分组

	public ProcessVO(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public ProcessVO(String id, String code, String name) {
		this.id = id;
		this.code = code;
		this.name = name;
	}

	public ProcessVO() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public void setId(String _id) {
		this.id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWftype() {
		return wftype;
	}

	public void setWftype(String wftype) {
		this.wftype = wftype;
	}

	public ActivityVO[] getActivityVOs() {
		return activityVOs;
	}

	public void setActivityVOs(ActivityVO[] activityVOs) {
		this.activityVOs = activityVOs;
	}

	public TransitionVO[] getTransitionVOs() {
		return transitionVOs;
	}

	public void setTransitionVOs(TransitionVO[] transitionVOs) {
		this.transitionVOs = transitionVOs;
	}

	public GroupVO[] getGroupVOs() {
		return groupVOs;
	}

	public void setGroupVOs(GroupVO[] groupVOs) {
		this.groupVOs = groupVOs;
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

	public String getUserdef05() {
		return userdef05;
	}

	public void setUserdef05(String userdef05) {
		this.userdef05 = userdef05;
	}

	public String getUserdef06() {
		return userdef06;
	}

	public void setUserdef06(String userdef06) {
		this.userdef06 = userdef06;
	}

	public String getUserdef07() {
		return userdef07;
	}

	public void setUserdef07(String userdef07) {
		this.userdef07 = userdef07;
	}

	public String getUserdef08() {
		return userdef08;
	}

	public void setUserdef08(String userdef08) {
		this.userdef08 = userdef08;
	}

	public String getUserdef09() {
		return userdef09;
	}

	public void setUserdef09(String userdef09) {
		this.userdef09 = userdef09;
	}

	public String getUserdef10() {
		return userdef10;
	}

	public void setUserdef10(String userdef10) {
		this.userdef10 = userdef10;
	}

	public String getCmpfileid() {
		return cmpfileid;
	}

	public void setCmpfileid(String cmpfileid) {
		this.cmpfileid = cmpfileid;
	}

}
