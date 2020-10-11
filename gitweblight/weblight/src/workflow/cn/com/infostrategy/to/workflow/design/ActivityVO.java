package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

public class ActivityVO implements Serializable {

	private static final long serialVersionUID = -4900363756753098731L;

	private Integer id = null;

	private String processid;

	private String activitytype = null; //环节类型
	private String code = null;

	private String wfname = null;

	private String uiname = null;

	private Integer x;
	private Integer y;
	private Integer width; //宽度
	private Integer height; //高度
	private Integer viewtype; //环节元素类型,目前有4种(方框,菱形,椭圆,方圆)

	//字体与颜色
	private String fonttype; //字体类型
	private Integer fontsize; //字体大小
	private String foreground; //前景颜色
	private String background; //背景颜色

	private String canhalfstart = null; //是否可以半路启动
	private String halfstartrole = null; //半路启动的角色
	private String halfstartdepttype = null; //半路启动的机构类型 gaofeng
	private String canhalfend = null; //是否可以半路结束
	private String canselfaddparticipate = null; //是否可以自己添加参与者

	private String isselfcycle = null; //是否可以自循环!
	private String selfcyclerolemap; //自循环的角色映射！！！

	private String autocommit = null; //是否自动提交
	private String iscanback = null; //是否可以回退

	private String isassignapprover = null; //是否指定审批人!!
	private String isneedmsg = null; //是否必须输入批语
	private String isneedreport = null; //是否必须打印报表
	private String iscanlookidea = null; //是否有权限查看审批意见
	private String approvemodel; //会签模式
	private Integer approvenumber; //会签数量
	private String childwfcode; //子流程编码

	private String showparticimode; //显示参与者模式
	private String participate_user; //参与的人

	private String participate_corp; //参与的机构范围,即在某个机构范围范围下,返回的是一个机构的Id,然后找出该机构下的所有子机构!比如【福州分行】,即可以将原来的动态参与者转变成直接在这里配置!!省去一个环节!!既简化了配置,形象易懂,又能解决根据机构选择人的问题!!
	private String participate_group; //参与的角色或者组!!

	private String participate_dynamic; //动态参与者!!

	private String ccto_user; //抄送人员.
	private String ccto_corp; //抄送机构,多选!支持公式与类
	private String ccto_role; //抄送角色,多选!

	private String messageformat; //消息定义
	private String messagereceiver; //消息接收者

	private String belongdeptgroup; //
	private String belongstationgroup; //

	private String desc = null;

	private String intercept1 = null; //拦截器1
	private String intercept2 = null; //拦截器2
	private String imgstr = null; //图片环节的图片
	private RiskVO riskVO = null; //风险点数据描述,主要是定义各个级别的风险分别有几个!

	private Integer[] myAllParentIds = null; //我的所有父亲
	private Integer[] myAllSonIds = null; //我的所有儿子

	private String userdef01;
	private String userdef02;
	private String userdef03;
	private String userdef04 = null;
	private String isHideTransSend = null;//是否隐藏转办
	private String extconfmap = null;//其他扩展参数

	public String getImgstr() {
		return imgstr;
	}

	public void setImgstr(String imgstr) {
		this.imgstr = imgstr;
	}

	//id
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	//processid
	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}

	//code
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	//wfname
	public String getWfname() {
		return wfname;
	}

	public void setWfname(String wfname) {
		this.wfname = wfname;
	}

	//uiname
	public String getUiname() {
		return uiname;
	}

	public void setUiname(String uiname) {
		this.uiname = uiname;
	}

	//x
	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	//y
	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	//desc
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String toString() {
		return getWfname(); //
	}

	public String getAutocommit() {
		return autocommit;
	}

	public void setAutocommit(String autocommit) {
		this.autocommit = autocommit;
	}

	public String getApprovemodel() {
		return approvemodel;
	}

	public String getChildwfcode() {
		return childwfcode;
	}

	public void setChildwfcode(String childwfcode) {
		this.childwfcode = childwfcode;
	}

	public void setApprovemodel(String approvemodel) {
		this.approvemodel = approvemodel;
	}

	public Integer getApprovenumber() {
		return approvenumber;
	}

	public void setApprovenumber(Integer approvenumber) {
		this.approvenumber = approvenumber;
	}

	public String getParticipate_user() {
		return participate_user;
	}

	public void setParticipate_user(String participate_user) {
		this.participate_user = participate_user;
	}

	public String getParticipate_group() {
		return participate_group;
	}

	public void setParticipate_group(String participate_group) {
		this.participate_group = participate_group;
	}

	public String getParticipate_corp() {
		return participate_corp;
	}

	public void setParticipate_corp(String participate_corp) {
		this.participate_corp = participate_corp;
	}

	public String getParticipate_dynamic() {
		return participate_dynamic;
	}

	public void setParticipate_dynamic(String participate_dynamic) {
		this.participate_dynamic = participate_dynamic;
	}

	public String getCcto_user() {
		return ccto_user;
	}

	public void setCcto_user(String ccto_user) {
		this.ccto_user = ccto_user;
	}

	public String getCcto_corp() {
		return ccto_corp;
	}

	public void setCcto_corp(String ccto_corp) {
		this.ccto_corp = ccto_corp;
	}

	public String getCcto_role() {
		return ccto_role;
	}

	public void setCcto_role(String ccto_role) {
		this.ccto_role = ccto_role; //
	}

	public String getMessageformat() {
		return messageformat;
	}

	public void setMessageformat(String messageformat) {
		this.messageformat = messageformat;
	}

	public String getMessagereceiver() {
		return messagereceiver;
	}

	public void setMessagereceiver(String messagereceiver) {
		this.messagereceiver = messagereceiver;
	}

	public String getIsassignapprover() {
		return isassignapprover;
	}

	public void setIsassignapprover(String isassignapprover) {
		this.isassignapprover = isassignapprover;
	}

	public String getActivitytype() {
		return activitytype;
	}

	public void setActivitytype(String activitytype) {
		this.activitytype = activitytype;
	}

	public String getIscanback() {
		return iscanback;
	}

	public void setIscanback(String iscanback) {
		this.iscanback = iscanback;
	}

	public String getIsneedmsg() {
		return isneedmsg;
	}

	public void setIsneedmsg(String isneedmsg) {
		this.isneedmsg = isneedmsg;
	}

	public String getIntercept1() {
		return intercept1;
	}

	public void setIntercept1(String intercept1) {
		this.intercept1 = intercept1;
	}

	public String getIntercept2() {
		return intercept2;
	}

	public void setIntercept2(String intercept2) {
		this.intercept2 = intercept2;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getViewtype() {
		return viewtype;
	}

	public void setViewtype(Integer viewtype) {
		this.viewtype = viewtype;
	}

	public RiskVO getRiskVO() {
		return riskVO;
	}

	public void setRiskVO(RiskVO riskVO) {
		this.riskVO = riskVO;
	}

	public String getBelongdeptgroup() {
		return belongdeptgroup;
	}

	public void setBelongdeptgroup(String belongdeptgroup) {
		this.belongdeptgroup = belongdeptgroup;
	}

	public String getBelongstationgroup() {
		return belongstationgroup;
	}

	public void setBelongstationgroup(String belongstationgroup) {
		this.belongstationgroup = belongstationgroup;
	}

	public String getCanhalfstart() {
		return canhalfstart;
	}

	public void setCanhalfstart(String canhalfstart) {
		this.canhalfstart = canhalfstart;
	}

	public String getHalfstartrole() {
		return halfstartrole;
	}

	public void setHalfstartrole(String halfstartrole) {
		this.halfstartrole = halfstartrole;
	}

	public String getHalfstartdepttype() {
		return halfstartdepttype;
	}

	public void setHalfstartdepttype(String halfstartdepttype) {
		this.halfstartdepttype = halfstartdepttype;
	}

	public String getCanhalfend() {
		return canhalfend;
	}

	public void setCanhalfend(String canhalfend) {
		this.canhalfend = canhalfend;
	}

	public String getCanselfaddparticipate() {
		return canselfaddparticipate;
	}

	public void setCanselfaddparticipate(String canselfaddparticipate) {
		this.canselfaddparticipate = canselfaddparticipate;
	}

	public String getShowparticimode() {
		return showparticimode;
	}

	public void setShowparticimode(String showparticimode) {
		this.showparticimode = showparticimode;
	}

	public String getIsneedreport() {
		return isneedreport;
	}

	public void setIsneedreport(String isneedreport) {
		this.isneedreport = isneedreport;
	}

	public String getFonttype() {
		return fonttype;
	}

	public void setFonttype(String fonttype) {
		this.fonttype = fonttype;
	}

	public Integer getFontsize() {
		return fontsize;
	}

	public void setFontsize(Integer fontsize) {
		this.fontsize = fontsize;
	}

	public String getForeground() {
		return foreground;
	}

	public void setForeground(String foreground) {
		this.foreground = foreground;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getIscanlookidea() {
		return iscanlookidea;
	}

	public void setIscanlookidea(String iscanlookidea) {
		this.iscanlookidea = iscanlookidea;
	}

	public String getIsselfcycle() {
		return isselfcycle;
	}

	public void setIsselfcycle(String isselfcycle) {
		this.isselfcycle = isselfcycle;
	}

	public String getSelfcyclerolemap() {
		return selfcyclerolemap;
	}

	public void setSelfcyclerolemap(String selfcyclerolemap) {
		this.selfcyclerolemap = selfcyclerolemap;
	}

	public Integer[] getMyAllParentIds() {
		return myAllParentIds;
	}

	public void setMyAllParentIds(Integer[] myAllParentIds) {
		this.myAllParentIds = myAllParentIds;
	}

	public Integer[] getMyAllSonIds() {
		return myAllSonIds;
	}

	public void setMyAllSonIds(Integer[] myAllSonIds) {
		this.myAllSonIds = myAllSonIds;
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

	public String getIsHideTransSend() {
		return isHideTransSend;
	}

	public void setIsHideTransSend(String isHideTransSend) {
		this.isHideTransSend = isHideTransSend;
	}

	public String getExtconfmap() {
		return extconfmap;
	}

	public void setExtconfmap(String extconfmap) {
		this.extconfmap = extconfmap;
	}

}
