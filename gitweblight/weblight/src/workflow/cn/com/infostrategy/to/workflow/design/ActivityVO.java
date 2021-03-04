package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

public class ActivityVO implements Serializable {

	private static final long serialVersionUID = -4900363756753098731L;

	private Integer id = null;

	private String processid;

	private String activitytype = null; //��������
	private String code = null;

	private String wfname = null;

	private String uiname = null;

	private Integer x;
	private Integer y;
	private Integer width; //���
	private Integer height; //�߶�
	private Integer viewtype; //����Ԫ������,Ŀǰ��4��(����,����,��Բ,��Բ)

	//��������ɫ
	private String fonttype; //��������
	private Integer fontsize; //�����С
	private String foreground; //ǰ����ɫ
	private String background; //������ɫ

	private String canhalfstart = null; //�Ƿ���԰�·����
	private String halfstartrole = null; //��·�����Ľ�ɫ
	private String halfstartdepttype = null; //��·�����Ļ������� gaofeng
	private String canhalfend = null; //�Ƿ���԰�·����
	private String canselfaddparticipate = null; //�Ƿ�����Լ���Ӳ�����

	private String isselfcycle = null; //�Ƿ������ѭ��!
	private String selfcyclerolemap; //��ѭ���Ľ�ɫӳ�䣡����

	private String autocommit = null; //�Ƿ��Զ��ύ
	private String iscanback = null; //�Ƿ���Ի���

	private String isassignapprover = null; //�Ƿ�ָ��������!!
	private String isneedmsg = null; //�Ƿ������������
	private String isneedreport = null; //�Ƿ�����ӡ����
	private String iscanlookidea = null; //�Ƿ���Ȩ�޲鿴�������
	private String approvemodel; //��ǩģʽ
	private Integer approvenumber; //��ǩ����
	private String childwfcode; //�����̱���

	private String showparticimode; //��ʾ������ģʽ
	private String participate_user; //�������

	private String participate_corp; //����Ļ�����Χ,����ĳ��������Χ��Χ��,���ص���һ��������Id,Ȼ���ҳ��û����µ������ӻ���!���硾���ݷ��С�,�����Խ�ԭ���Ķ�̬������ת���ֱ������������!!ʡȥһ������!!�ȼ�������,�����׶�,���ܽ�����ݻ���ѡ���˵�����!!
	private String participate_group; //����Ľ�ɫ������!!

	private String participate_dynamic; //��̬������!!

	private String ccto_user; //������Ա.
	private String ccto_corp; //���ͻ���,��ѡ!֧�ֹ�ʽ����
	private String ccto_role; //���ͽ�ɫ,��ѡ!

	private String messageformat; //��Ϣ����
	private String messagereceiver; //��Ϣ������

	private String belongdeptgroup; //
	private String belongstationgroup; //

	private String desc = null;

	private String intercept1 = null; //������1
	private String intercept2 = null; //������2
	private String imgstr = null; //ͼƬ���ڵ�ͼƬ
	private RiskVO riskVO = null; //���յ���������,��Ҫ�Ƕ����������ķ��շֱ��м���!

	private Integer[] myAllParentIds = null; //�ҵ����и���
	private Integer[] myAllSonIds = null; //�ҵ����ж���

	private String userdef01;
	private String userdef02;
	private String userdef03;
	private String userdef04 = null;
	private String isHideTransSend = null;//�Ƿ�����ת��
	private String extconfmap = null;//������չ����

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
