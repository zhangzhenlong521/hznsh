package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

import cn.com.infostrategy.to.mdata.RefItemVO;

public class ActivityBean implements Serializable {

	private static final long serialVersionUID = -5307629314729529589L;
	private int id = 0;

	private String activitytype;
	private String code;
	private String wfname;
	private String uiname; //
	private String processid; //
	private Boolean canhalfstart; //�Ƿ���԰�·����

	private RefItemVO halfstartrole; //��·�����Ľ�ɫ,��ѡ����!!
	private RefItemVO halfstartdepttype; //��·�����Ļ�������,��ѡ����  gaofeng

	private Boolean canhalfend; //�Ƿ���԰�·����
	private Boolean canselfaddparticipate; //�Ƿ�����Լ���Ӳ�����,����ܹؼ��������ζ�ſ������ʼ�ϵͳ�����߹�������๫˾����ν��������������һ�ֻ���

	private Boolean autocommit; //�Ƿ��Զ��ύ
	private Boolean iscanback; //�Ƿ���Ի���
	private Boolean isassignapprover; //�Ƿ�ָ��������!!
	private Boolean isneedmsg; //�Ƿ������������
	private Boolean isselfcycle; //�Ƿ���ѭ��,�ǳ���Ҫ�Ĺ���,���Դ�����������
	private String selfcyclerolemap; //��ѭ���Ľ�ɫӳ�䣡����

	private Boolean isneedreport; //�Ƿ�����ӡ����
	private String iscanlookidea; //�Ƿ���Ȩ�޲鿴�������
	private String approvemodel; //��ǩģʽ,ȡֵ��:��ռ/��ǩ/���������
	private Integer approvenumber; //��ǩ����
	private String childwfcode; //�����̱���,�������һ�����̵�����!

	private String showparticimode; //��ʾ������ģʽ
	private RefItemVO participate_user; //�������

	private String participate_corp; //����Ļ�����Χ,����ĳ��������Χ��Χ��,���ص���һ��������Id,Ȼ���ҳ��û����µ������ӻ���!���硾���ݷ��С�
	private RefItemVO participate_group; //����Ľ�ɫ������!!

	private RefItemVO participate_dynamic; //��̬������!!

	private RefItemVO ccto_user; //������Ա.
	private String ccto_corp; //���ͻ���,��ѡ!֧�ֹ�ʽ����!�����ı���!!!
	private RefItemVO ccto_role; //���ͽ�ɫ,��ѡ!

	private String messageformat; //��Ϣ����
	private String messagereceiver; //��Ϣ������

	private String intercept1 = null; //UI��ͳһ������
	private String intercept2 = null; //BS��ͳ һ������

	private String userdef01 = null;
	private String userdef02 = null;
	private String userdef03 = null;
	private String userdef04 = null;
	private Boolean isHideTransSend = null;//�Ƿ�����ת��
	private String extconfmap = null;//������չ����
	private String smsformat = null;//�Ƿ��Ͷ���
	private Boolean sendsms = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getWfname() {
		return wfname;
	}

	public void setWfname(String wfname) {
		this.wfname = wfname;
	}

	public String getUiname() {
		return uiname;
	}

	public void setUiname(String uiname) {
		this.uiname = uiname;
	}

	//��������̱���!!
	public String getChildwfcode() {
		return childwfcode;
	}

	//���û�������̱���!!
	public void setChildwfcode(String childwfcode) {
		this.childwfcode = childwfcode;
	}

	public String getApprovemodel() {
		return approvemodel;
	}

	public void setApprovemodel(String approvemodel) {
		this.approvemodel = approvemodel;
	}

	public Boolean getAutocommit() {
		return autocommit;
	}

	public void setAutocommit(Boolean autocommit) {
		this.autocommit = autocommit;
	}

	public Integer getApprovenumber() {
		return approvenumber;
	}

	public void setApprovenumber(Integer approvenumber) {
		this.approvenumber = approvenumber;
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

	public RefItemVO getParticipate_user() {
		return participate_user;
	}

	public void setParticipate_user(RefItemVO _participate_user) {
		this.participate_user = _participate_user;
	}

	public RefItemVO getParticipate_group() {
		return participate_group;
	}

	public void setParticipate_group(RefItemVO _participate_group) {
		this.participate_group = _participate_group;
	}

	public String getParticipate_corp() {
		return participate_corp;
	}

	public void setParticipate_corp(String participate_corp) {
		this.participate_corp = participate_corp;
	}

	public RefItemVO getParticipate_dynamic() {
		return participate_dynamic;
	}

	public void setParticipate_dynamic(RefItemVO _participate_dynamic) {
		this.participate_dynamic = _participate_dynamic;
	}

	public RefItemVO getCcto_user() {
		return ccto_user; //������Ա
	}

	public void setCcto_user(RefItemVO ccto_user) {
		this.ccto_user = ccto_user; //������Ա
	}

	public String getCcto_corp() {
		return ccto_corp; //���ͻ���
	}

	public void setCcto_corp(String ccto_corp) {
		this.ccto_corp = ccto_corp; //���ͻ���
	}

	public RefItemVO getCcto_role() {
		return ccto_role; //���ͽ�ɫ
	}

	public void setCcto_role(RefItemVO ccto_role) {
		this.ccto_role = ccto_role; //���ͽ�ɫ
	}

	public Boolean getIsassignapprover() {
		return isassignapprover;
	}

	public void setIsassignapprover(Boolean isassignapprover) {
		this.isassignapprover = isassignapprover;
	}

	public String getActivitytype() {
		return activitytype;
	}

	public void setActivitytype(String activitytype) {
		this.activitytype = activitytype;
	}

	public Boolean getIscanback() {
		return iscanback;
	}

	public void setIscanback(Boolean iscanback) {
		this.iscanback = iscanback;
	}

	public Boolean getIsneedmsg() {
		return isneedmsg;
	}

	public void setIsneedmsg(Boolean isneedmsg) {
		this.isneedmsg = isneedmsg;
	}

	public Boolean getCanhalfstart() {
		return canhalfstart;
	}

	public void setCanhalfstart(Boolean canhalfstart) {
		this.canhalfstart = canhalfstart;
	}

	public RefItemVO getHalfstartrole() {
		return halfstartrole;
	}

	public void setHalfstartrole(RefItemVO _halfstartrole) {
		this.halfstartrole = _halfstartrole;
	}

	public RefItemVO getHalfstartdepttype() {
		return halfstartdepttype;
	}

	public void setHalfstartdepttype(RefItemVO halfstartdepttype) {
		this.halfstartdepttype = halfstartdepttype;
	}

	public Boolean getCanhalfend() {
		return canhalfend;
	}

	public void setCanhalfend(Boolean canhalfend) {
		this.canhalfend = canhalfend;
	}

	public Boolean getCanselfaddparticipate() {
		return canselfaddparticipate;
	}

	public void setCanselfaddparticipate(Boolean canselfaddparticipate) {
		this.canselfaddparticipate = canselfaddparticipate;
	}

	public String getShowparticimode() {
		return showparticimode;
	}

	public void setShowparticimode(String showparticimode) {
		this.showparticimode = showparticimode;
	}

	public Boolean getIsneedreport() {
		return isneedreport;
	}

	public void setIsneedreport(Boolean isneedreport) {
		this.isneedreport = isneedreport;
	}

	public String getIscanlookidea() {
		return iscanlookidea;
	}

	public void setIscanlookidea(String iscanlookidea) {
		this.iscanlookidea = iscanlookidea;
	}

	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}

	public Boolean getIsselfcycle() {
		return isselfcycle;
	}

	public void setIsselfcycle(Boolean isselfcycle) {
		this.isselfcycle = isselfcycle;
	}

	public String getSelfcyclerolemap() {
		return selfcyclerolemap;
	}

	public void setSelfcyclerolemap(String selfcyclerolemap) {
		this.selfcyclerolemap = selfcyclerolemap;
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

	public Boolean getIsHideTransSend() {
		return isHideTransSend;
	}

	public void setIsHideTransSend(Boolean isHideTransSend) {
		this.isHideTransSend = isHideTransSend;
	}

	public String getExtconfmap() {
		return extconfmap;
	}

	public void setExtconfmap(String extconfmap) {
		this.extconfmap = extconfmap;
	}
	public String getSmsformat() {
		return smsformat;
	}

	public void setSmsformat(String smsformat) {
		this.smsformat = smsformat;
	}
	public Boolean getSendsms() {
		return sendsms;
	}

	public void setSendsms(Boolean sendsms) {
		this.sendsms = sendsms;
	}
}
