package cn.com.infostrategy.to.workflow.engine;

import java.io.Serializable;
import java.util.HashMap;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * �ȴ����ڶ���,�������������һ�����WFPARVO��..
 * һ�����ڰ����������,Ҳ�����������!!!
 * ��ǰ�Ļ���,ֻ��Ŀ�껷��,������Դ���ڼ�¼��������!
 * �������ֻ�����Ҫ��¼��Դ��������Դ����,��Ϊ�����������Աʱ��Ҫ��Щ��Ϣ!!
 * @author xch
 *
 */
public class DealActivityVO implements Serializable {

	private static final long serialVersionUID = -5335224239603046651L;

	private int sortIndex = 1; //ǿ��������ֶ�,��Ϊ��ѭ����Ҫ��Զ�������ڵ�һλ!

	private String fromActivityId = null; //���ĸ�����id������!
	private String fromActivityCode = null; //���ĸ�����Code������
	private String fromActivityName = null; //���ĸ�����Name������

	private String fromTransitionId = null; //���ߵ�id
	private String fromTransitionCode = null; //���ߵ�id
	private String fromTransitionName = null; //���ߵ�����
	private String fromTransitionIntercept = null; //�ߵĴ���������
	private String fromTransitionMailsubject = null; //�����ϵ��ʼ�����
	private String fromTransitionMailcontent = null; //�����ϵ���������
	private String fromtransitiontype = null; //��Դ��������
	private HashMap fromtransitionExtConfMap = null; //

	private String activityId = null; //����/��ǰ����id
	private String activityCode = null; //����/��ǰ����Code
	private String activityName = null; //����/��ǰ����Name
	private String activityBelongDeptGroupName = null; //����/��ǰ���ڵ����������������!!
	private String activityType = null; //����/��ǰ��������
	private String canselfaddparticipate = null;
	private boolean activityIselfcycle = false; //
	private String activitySelfcyclerolemap = null; //

	private String approvemodel = null; //����ģʽ,��ռ/��ǩ
	private String showparticimode = null; //��ʾ�����ߵ�ģʽ
	private String participate_user = null; //�������
	private String participate_group = null; //�������
	private String participate_corp = null; //����Ļ���
	private String participate_dynamic = null; //��̬������

	private String ccto_user = null; //������Ա,��ǰ���ڶ���ĳ���������Ա
	private String ccto_corp = null; //���ͻ���,��ǰ���ڶ���ĳ������Ļ���
	private String ccto_role = null; //���ͽ�ɫ

	private String participtMsg = ""; //
	private HashMap wnParam = null;//���ܲ�������Ӧextconfmap
	private DealTaskVO[] dealTaskVOs = null; //���������(��Ա)

	public HashMap getWnParam() {
		return wnParam;
	}

	public void setWnParam(HashMap wnParam) {
		this.wnParam = wnParam;
	}

	public String getFromActivityId() {
		return fromActivityId;
	}

	public void setFromActivityId(String fromActivityId) {
		this.fromActivityId = fromActivityId;
	}

	public String getFromActivityCode() {
		return fromActivityCode;
	}

	public void setFromActivityCode(String fromActivityCode) {
		this.fromActivityCode = fromActivityCode;
	}

	public String getFromActivityName() {
		return fromActivityName;
	}

	public void setFromActivityName(String fromActivityName) {
		this.fromActivityName = fromActivityName;
	}

	public String getFromTransitionId() {
		return fromTransitionId;
	}

	public void setFromTransitionId(String fromTransitionId) {
		this.fromTransitionId = fromTransitionId;
	}

	public String getFromTransitionCode() {
		return fromTransitionCode;
	}

	public void setFromTransitionCode(String fromTransitionCode) {
		this.fromTransitionCode = fromTransitionCode;
	}

	public String getFromTransitionName() {
		return fromTransitionName;
	}

	public void setFromTransitionName(String fromTransitionName) {
		this.fromTransitionName = fromTransitionName;
	}

	public String getFromTransitionIntercept() {
		return fromTransitionIntercept;
	}

	public void setFromTransitionIntercept(String fromTransitionIntercept) {
		this.fromTransitionIntercept = fromTransitionIntercept;
	}

	public String getFromTransitionMailsubject() {
		return fromTransitionMailsubject;
	}

	public void setFromTransitionMailsubject(String fromTransitionMailsubject) {
		this.fromTransitionMailsubject = fromTransitionMailsubject;
	}

	public String getFromTransitionMailcontent() {
		return fromTransitionMailcontent;
	}

	public void setFromTransitionMailcontent(String fromTransitionMailcontent) {
		this.fromTransitionMailcontent = fromTransitionMailcontent;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityBelongDeptGroupName() {
		return activityBelongDeptGroupName;
	}

	public void setActivityBelongDeptGroupName(String activityBelongDeptGroupName) {
		this.activityBelongDeptGroupName = activityBelongDeptGroupName;
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
		this.ccto_role = ccto_role;
	}

	public String getParticiptMsg() {
		return participtMsg;
	}

	public void setParticiptMsg(String _participtMsg) {
		this.participtMsg = _participtMsg;
	}

	public String getApprovemodel() {
		return approvemodel;
	}

	public void setApprovemodel(String approvemodel) {
		this.approvemodel = approvemodel;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getShowparticimode() {
		return showparticimode;
	}

	public void setShowparticimode(String showparticimode) {
		this.showparticimode = showparticimode;
	}

	public String getFromtransitiontype() {
		return fromtransitiontype;
	}

	public void setFromtransitiontype(String fromtransitiontype) {
		this.fromtransitiontype = fromtransitiontype;
	}

	//���ߵ���չ����!
	public void setFromtransitionExtConfMap(HashMap _extConfMap) {
		fromtransitionExtConfMap = _extConfMap; //
	}

	public HashMap getFromtransitionExtConfMap() {
		return fromtransitionExtConfMap;
	}

	public DealTaskVO[] getDealTaskVOs() {
		return dealTaskVOs;
	}

	public void setDealTaskVOs(DealTaskVO[] dealTaskVOs) {
		this.dealTaskVOs = dealTaskVOs;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	public boolean isActivityIselfcycle() {
		return activityIselfcycle;
	}

	public void setActivityIselfcycle(boolean activityIselfcycle) {
		this.activityIselfcycle = activityIselfcycle;
	}

	public String getActivitySelfcyclerolemap() {
		return activitySelfcyclerolemap;
	}

	public void setActivitySelfcyclerolemap(String activitySelfcyclerolemap) {
		this.activitySelfcyclerolemap = activitySelfcyclerolemap;
	}

	/**
	 * ȡ��ĳһ�����ڵ���ʾ����,���ڿ��ǵ���ʱ��Ҫ��ʾ�������! ����
	 * @param _isHtml
	 * @return
	 */
	public String getCurrActivityName(boolean _isHtml) {
		TBUtil tbUtil = new TBUtil(); //
		String str_ActName = this.getActivityName(); //��������
		String str_tabTitle = null; //
		if (tbUtil.getSysOptionBooleanValue("�ύ�����Ƿ���ʾ���������������", true)) {
			String str_ActBLGroup = this.getActivityBelongDeptGroupName(); //���������������!!!
			if (str_ActBLGroup == null || "3".equals(this.getApprovemodel())) { //���û������,������������(��Ϊ�е������̻��ھ�������),��ֱ��ʹ�û�������!!
				int li_count = tbUtil.findCount(str_ActName, "\n"); //����û�л���!
				if (li_count <= 0 || !_isHtml) { //���û�л���!
					str_ActName = tbUtil.replaceAll(str_ActName, "\r", ""); //
					str_tabTitle = str_ActName; //���
				} else { //����л���,��ǿ��ʹ��Html������ʾ,��ҵ��Ŀ������˵��̫�������!
					str_ActName = tbUtil.replaceAll(str_ActName, "\r", ""); //
					str_ActName = tbUtil.replaceAll(str_ActName, "\n", "<br>"); //
					str_tabTitle = "<html>" + str_ActName + "</html>"; //ǿ��ʹ��Html������ʾ!!!
				}
			} else { //���Ҫƴ��!
				if (_isHtml) {
					str_ActBLGroup = tbUtil.replaceAll(str_ActBLGroup, "\r", ""); //
					str_ActBLGroup = tbUtil.replaceAll(str_ActBLGroup, "\n", "<>"); //
					str_ActName = tbUtil.replaceAll(str_ActName, "\r", ""); //
					str_ActName = tbUtil.replaceAll(str_ActName, "\n", ""); //
					str_tabTitle = "<html>" + str_ActBLGroup + "<br>" + str_ActName + "</html>"; //�������뻷�����ֳ�����ƴ��һ��!!
				} else {
					str_ActBLGroup = tbUtil.replaceAll(str_ActBLGroup, "\r", ""); //
					str_ActBLGroup = tbUtil.replaceAll(str_ActBLGroup, "\n", ""); //
					str_ActName = tbUtil.replaceAll(str_ActName, "\r", ""); //
					str_ActName = tbUtil.replaceAll(str_ActName, "\n", ""); //
					str_tabTitle = str_ActBLGroup + "-" + str_ActName; //
				}
			}
		} else {
			str_tabTitle = str_ActName;
		}

		return str_tabTitle; //
	}

	public String getCanselfaddparticipate() {
		return canselfaddparticipate;
	}

	public void setCanselfaddparticipate(String canselfaddparticipate) {
		this.canselfaddparticipate = canselfaddparticipate;
	}

}
