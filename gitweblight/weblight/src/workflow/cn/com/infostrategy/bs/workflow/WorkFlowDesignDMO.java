package cn.com.infostrategy.bs.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.GroupVO;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.to.workflow.design.TransitionVO;

/**
 * �������༭��DMO,��Ҫ�Ǹ�������ͼ�εı���༭������!!
 * �����������������ϵ�Ǹ������Ҫ�ѵ�֮һ!
 * @author xch
 *
 */
public class WorkFlowDesignDMO extends AbstractDMO {
	CommDMO commDMO = null;
	Logger logger = WLTLogger.getLogger(WorkFlowDesignDMO.class); //��־��

	public WorkFlowDesignDMO() {
	}

	private CommDMO getCommDMO() {
		if (commDMO != null) {
			return commDMO;
		}
		commDMO = new CommDMO();
		return commDMO;
	}

	/**
	 * ���湤����!!!����ƴ��һ��SQL�ύ,���ݾ�����ʵ��ѹ������,����߼��������ǽϸߵ�!!!
	 * ����ͳͳ�ĳ���ʹ��InsertSQLBuilder����,�ȼ���Ч���ȶ��ݴ�!!
	 * @param _processVO
	 * @param _processID
	 * @throws Exception
	 */
	public void saveWFProcess(ProcessVO _processVO, String _processID) throws Exception {
		ArrayList al_sqls = new ArrayList(); //
		al_sqls.add("delete from pub_wf_group      where processid='" + _processID + "'"); //ɾ��������
		al_sqls.add("delete from pub_wf_transition where processid='" + _processID + "'"); //ɾ����������
		al_sqls.add("delete from pub_wf_activity   where processid='" + _processID + "'"); //ɾ�����л���
		al_sqls.add("delete from pub_wf_process    where id='" + _processID + "'"); //ɾ������,�Ա����²���!!
		al_sqls.add(getSQL_InsertProcess(_processVO, _processID)); //�������̵�SQL

		//�������л��ڵ�SQL
		ActivityVO[] activityVOs = _processVO.getActivityVOs(); //
		for (int i = 0; i < activityVOs.length; i++) {
			ActivityVO vo = activityVOs[i];
			al_sqls.add(getSQL_Activity(vo, _processVO, _processID)); //
		}

		//����TransitionVO��Ӧ��sql
		TransitionVO[] transitionVOs = _processVO.getTransitionVOs(); //
		for (int i = 0; i < transitionVOs.length; i++) {
			TransitionVO vo = transitionVOs[i];
			al_sqls.add(getSQL_Transtion(vo, _processVO, _processID)); //
		}

		//����group�е�����..
		CommDMO dmo = getCommDMO();
		GroupVO[] groupVOs = _processVO.getGroupVOs(); //
		int len = groupVOs.length;
		for (int i = 0; i < len; i++) {
			String str_newID = dmo.getSequenceNextValByDS(null, "s_pub_wf_group");
			groupVOs[i].setId(Integer.valueOf(str_newID));
		}
		for (int i = 0; i < len; i++) {
			GroupVO vo = groupVOs[i]; //
			al_sqls.add(getSQL_Group(vo, _processVO, _processID)); //
		}

		//long ll_1 = System.currentTimeMillis(); //
		dmo.executeBatchByDS(null, al_sqls); //ʵ�ʲ������ݿ�,һ������!!
		//long ll_2 = System.currentTimeMillis(); //
		//logger.debug("��������ͼ�ɹ�,������[" + al_sqls.size() + "]��SQL,��ʱ[" + (ll_2 - ll_1) + "]����"); //
	}

	/**
	 * ���촴�����̵�SQL
	 * @param _processVO
	 * @param _processID
	 * @return
	 */
	private String getSQL_InsertProcess(ProcessVO _processVO, String _processID) {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_wf_process"); //
		isql.putFieldValue("id", _processID); //
		isql.putFieldValue("code", _processVO.getCode()); //
		isql.putFieldValue("name", _processVO.getName()); //
		isql.putFieldValue("wftype", _processVO.getWftype()); //

		isql.putFieldValue("wfeguiintercept", _processVO.getWfeguiintercept()); //UI��ͳһ��������
		isql.putFieldValue("wfegbsintercept", _processVO.getWfegbsintercept()); //BS��ͳһ��������
		isql.putFieldValue("userdef01", _processVO.getUserdef01()); //
		isql.putFieldValue("userdef02", _processVO.getUserdef02()); //
		isql.putFieldValue("userdef03", _processVO.getUserdef03()); //
		isql.putFieldValue("userdef04", _processVO.getUserdef04()); //
		isql.putFieldValue("userdef05", _processVO.getUserdef05()); //
		isql.putFieldValue("userdef06", _processVO.getUserdef06()); //
		isql.putFieldValue("userdef07", _processVO.getUserdef07()); //
		isql.putFieldValue("userdef08", _processVO.getUserdef08()); //
		isql.putFieldValue("userdef09", _processVO.getUserdef09()); //
		isql.putFieldValue("userdef10", _processVO.getUserdef10()); //
		isql.putFieldValue("cmpfileid", _processVO.getCmpfileid()); //
		return isql.getSQL(); //
	}

	/**
	 * ���촴�����ڵ�Insert���
	 * @param _activity
	 * @param _process
	 * @param _processID
	 * @return
	 */
	private String getSQL_Activity(ActivityVO _activity, ProcessVO _process, String _processID) {
		InsertSQLBuilder iSB = new InsertSQLBuilder("pub_wf_activity"); //
		iSB.putFieldValue("id", _activity.getId()); //
		iSB.putFieldValue("processid", _processID); //
		iSB.putFieldValue("activitytype", _activity.getActivitytype()); //
		iSB.putFieldValue("code", _activity.getCode()); //
		iSB.putFieldValue("wfname", _activity.getWfname()); //
		iSB.putFieldValue("uiname", _activity.getUiname()); //
		iSB.putFieldValue("x", _activity.getX()); //
		iSB.putFieldValue("y", _activity.getY()); //
		iSB.putFieldValue("width", _activity.getWidth()); //
		iSB.putFieldValue("height", _activity.getHeight()); //
		iSB.putFieldValue("viewtype", _activity.getViewtype()); //��������
		iSB.putFieldValue("fonttype", _activity.getFonttype()); //��������
		iSB.putFieldValue("fontsize", _activity.getFontsize()); //�����С
		iSB.putFieldValue("foreground", _activity.getForeground()); //ǰ����ɫ
		iSB.putFieldValue("background", _activity.getBackground()); //������ɫ
		iSB.putFieldValue("canhalfstart", _activity.getCanhalfstart(), "N"); //�Ƿ���԰�·����
		iSB.putFieldValue("halfstartrole", _activity.getHalfstartrole()); //���԰�·�����Ľ�ɫ
		iSB.putFieldValue("halfstartdepttype", _activity.getHalfstartdepttype()); //���԰�·�����Ļ�������  gaofeng
		iSB.putFieldValue("canhalfend", _activity.getCanhalfend(), "N"); //�Ƿ���԰�·����
		iSB.putFieldValue("canselfaddparticipate", _activity.getCanselfaddparticipate(), "N"); //�Ƿ�����Լ���Ӳ�����
		iSB.putFieldValue("autocommit", _activity.getAutocommit(), "N"); //
		iSB.putFieldValue("isselfcycle", _activity.getIsselfcycle(), "N"); //�Ƿ���ѭ��!!!
		iSB.putFieldValue("selfcyclerolemap", _activity.getSelfcyclerolemap()); //��ѭ��ӳ�������ͼ!!!
		iSB.putFieldValue("iscanback", _activity.getIscanback(), "N"); //
		iSB.putFieldValue("isassignapprover", _activity.getIsassignapprover(), "N"); //�Ƿ�ָ��������,���Ϊnull,��ʹ��"null"
		iSB.putFieldValue("isneedmsg", _activity.getIsneedmsg(), "N"); //
		iSB.putFieldValue("isneedreport", _activity.getIsneedreport(), "Y"); //
		iSB.putFieldValue("iscanlookidea", _activity.getIscanlookidea()); //
		iSB.putFieldValue("approvemodel", _activity.getApprovemodel(), "��ռ"); //����ģʽ�����Ϊnull����ʹ��"��ռ"
		iSB.putFieldValue("approvenumber", _activity.getApprovenumber()); //
		iSB.putFieldValue("childwfcode", _activity.getChildwfcode()); //�����̱���
		iSB.putFieldValue("showparticimode", _activity.getShowparticimode()); //
		iSB.putFieldValue("participate_user", _activity.getParticipate_user()); //
		iSB.putFieldValue("participate_corp", _activity.getParticipate_corp()); //����Ļ���
		iSB.putFieldValue("participate_group", _activity.getParticipate_group()); //�������,Ҳ���ǽ�ɫ!!
		iSB.putFieldValue("participate_dynamic", _activity.getParticipate_dynamic()); //
		iSB.putFieldValue("ccto_user", _activity.getCcto_user()); //������Ա!!
		iSB.putFieldValue("ccto_corp", _activity.getCcto_corp()); //���ͻ���!!
		iSB.putFieldValue("ccto_role", _activity.getCcto_role()); //���ͽ�ɫ!!
		iSB.putFieldValue("messageformat", _activity.getMessageformat()); //
		iSB.putFieldValue("messagereceiver", _activity.getMessagereceiver()); //
		iSB.putFieldValue("belongdeptgroup", _activity.getBelongdeptgroup()); //
		iSB.putFieldValue("belongstationgroup", _activity.getBelongstationgroup()); //
		iSB.putFieldValue("intercept1", _activity.getIntercept1()); //
		iSB.putFieldValue("intercept2", _activity.getIntercept2()); //
		iSB.putFieldValue("userdef01", _activity.getUserdef01()); //
		iSB.putFieldValue("userdef02", _activity.getUserdef02()); //
		iSB.putFieldValue("userdef03", _activity.getUserdef03()); //
		iSB.putFieldValue("userdef04", _activity.getUserdef04()); //
		iSB.putFieldValue("isHideTransSend", _activity.getIsHideTransSend());
		iSB.putFieldValue("extconfmap", _activity.getExtconfmap()); //
		iSB.putFieldValue("descr", _activity.getDesc()); //
		iSB.putFieldValue("imgstr", _activity.getImgstr()); //	
		return iSB.getSQL(); //
	}

	/**
	 * �������ߵ�SQL!!
	 * @param _transition
	 * @param _process
	 * @param _processID
	 * @return
	 */
	private String getSQL_Transtion(TransitionVO _transition, ProcessVO _process, String _processID) {
		List routing = _transition.getPoints(); //�����ϵĶϵ��λ����Ҫ����һ��,�����12,16;15,28;26,34������!!
		String str_points = null; //
		if (routing != null && routing.size() > 1) {
			StringBuffer points = new StringBuffer("");
			routing.remove(0); //ɾ����һ��!
			routing.remove(routing.size() - 1); //ɾ�����һ��!�����sizeӦ�ñ���
			for (int i = 0; i < routing.size(); i++) {
				double[] point = (double[]) routing.get(i); //ȡ��λ��
				points.append(point[0] + "," + point[1]);
				if (i != routing.size() - 1) {
					points.append(";"); //����������һ��,����Ҫ�Ӹ��ֺ�!!!
				}
			}
			str_points = points.toString(); //
		}
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_wf_transition"); //
		isql.putFieldValue("id", _transition.getId()); //
		isql.putFieldValue("processid", _processID); //
		isql.putFieldValue("code", _transition.getCode()); //
		isql.putFieldValue("wfname", _transition.getWfname()); //
		isql.putFieldValue("uiname", _transition.getUiname()); //
		isql.putFieldValue("dealtype", _transition.getDealtype()); //
		isql.putFieldValue("fonttype", _transition.getFonttype()); //
		isql.putFieldValue("fontsize", _transition.getFontsize()); //
		isql.putFieldValue("foreground", _transition.getForeground()); //
		isql.putFieldValue("background", _transition.getBackground()); //
		isql.putFieldValue("mailsubject", _transition.getMailsubject()); //
		isql.putFieldValue("mailcontent", _transition.getMailcontent()); //
		isql.putFieldValue("points", str_points); //
		isql.putFieldValue("fromactivity", _transition.getFromactivity()); //
		isql.putFieldValue("toactivity", _transition.getToactivity()); //
		isql.putFieldValue("conditions", _transition.getCondition()); //
		isql.putFieldValue("reasoncodesql", _transition.getReasoncodesql()); //
		isql.putFieldValue("intercept", _transition.getIntercept()); //
		isql.putFieldValue("linetype", _transition.getLineType()); //
		isql.putFieldValue("issingle", _transition.isSingle() + ""); //
		return isql.toString();
	}

	/**
	 * �����������SQL!��ÿ�ζ��Ǵ���ID��
	 * @param _groupVO
	 * @param _process
	 * @param _processID
	 * @return
	 * @throws Exception
	 */
	private String getSQL_Group(GroupVO _groupVO, ProcessVO _process, String _processID) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_wf_group"); //
		isql.putFieldValue("id", _groupVO.getId()); //
		isql.putFieldValue("processid", _processID); //
		isql.putFieldValue("grouptype", _groupVO.getGrouptype()); //
		isql.putFieldValue("code", _groupVO.getCode()); //
		isql.putFieldValue("wfname", _groupVO.getWfname()); //
		isql.putFieldValue("uiname", _groupVO.getUiname()); //
		isql.putFieldValue("x", _groupVO.getX()); //
		isql.putFieldValue("y", _groupVO.getY()); //
		isql.putFieldValue("width", _groupVO.getWidth()); //
		isql.putFieldValue("height", _groupVO.getHeight()); //
		isql.putFieldValue("fonttype", _groupVO.getFonttype()); //
		isql.putFieldValue("fontsize", _groupVO.getFontsize()); //
		isql.putFieldValue("foreground", _groupVO.getForeground()); //
		isql.putFieldValue("background", _groupVO.getBackground()); //
		isql.putFieldValue("descr", _groupVO.getDescr()); //
		isql.putFieldValue("posts", _groupVO.getPosts()); //
		return isql.getSQL(); //
	}

	public ProcessVO getWFProcessByWFID(String _processid) throws Exception {
		return getWFProcessBySQL("select * from pub_wf_process where id='" + _processid + "'"); //
	}

	public ProcessVO getWFProcessByWFID(String _datasourcename, String _processid) throws Exception {
		return getWFProcessBySQL(_datasourcename, "select * from pub_wf_process where id='" + _processid + "'"); //
	}

	public ProcessVO getHistoryWFProcessByWFID(String _processid) throws Exception {
		return getHistoryWFProcessBySQL("select * from pub_wf_process_copy where id='" + _processid + "'"); //
	}

	/**
	 * �������̱���,ȡ��һ�����̶���!!!
	 * @param code
	 * @return
	 */
	public ProcessVO getWFProcessByWFCode(String _processCode) throws Exception {
		return getWFProcessBySQL("select * from pub_wf_process where code='" + _processCode + "'"); //
	}

	//����SQLȡ!
	private ProcessVO getWFProcessBySQL(String _sql) throws Exception {
		return getWFProcessBySQL(null, _sql);
	}

	//����SQLȡ������VO
	private ProcessVO getWFProcessBySQL(String _datasourcename, String _sql) throws Exception {
		ProcessVO processVO = getProcessVOBySQL(_datasourcename, _sql); //
		if (processVO == null) {
			return null; //
		}
		ActivityVO[] activityvos = getWFActivityVOs(_datasourcename, "" + processVO.getId());
		processVO.setActivityVOs(activityvos);
		processVO.setTransitionVOs(getWFTransitionVOs(_datasourcename, "" + processVO.getId(), activityvos));
		processVO.setGroupVOs(getWFGroupVOs(_datasourcename, "" + processVO.getId())); //
		return processVO;
	}

	//ȡ����ʷ
	private ProcessVO getHistoryWFProcessBySQL(String _sql) throws Exception {
		ProcessVO processVO = getProcessVOBySQL(null, _sql); //
		if (processVO == null) {
			return null; //
		}
		ActivityVO[] activityvos = getHistoryWFActivityVOs("" + processVO.getId());
		processVO.setActivityVOs(activityvos); //
		processVO.setTransitionVOs(getHistoryWFTransitionVOs("" + processVO.getId(), activityvos));
		processVO.setGroupVOs(getHistoryWFGroupVOs("" + processVO.getId())); //
		return processVO;
	}

	//����SQL�ȹ���һ��ProcessVO
	private ProcessVO getProcessVOBySQL(String _datasourcename, String _sql) throws Exception {
		HashVO[] vor = getCommDMO().getHashVoArrayByDS(_datasourcename, _sql); //
		if (vor.length == 0) {
			return null;
		}
		if (vor.length > 0) {
			ProcessVO processVO = new ProcessVO();
			processVO.setId(vor[0].getStringValue("id"));//ũ��һͼ�����õ�sybase���ݿ⣬�ͻ��涨�����ֶα�����char(36),�����ֶα�����char��varchar����������id��Ϊ�ַ�����
			processVO.setCode(vor[0].getStringValue("code"));
			processVO.setName(vor[0].getStringValue("name"));
			processVO.setWftype(vor[0].getStringValue("wftype")); //
			processVO.setWfeguiintercept(vor[0].getStringValue("wfeguiintercept")); //
			processVO.setWfegbsintercept(vor[0].getStringValue("wfegbsintercept")); //
			processVO.setUserdef01(vor[0].getStringValue("userdef01"));
			processVO.setUserdef02(vor[0].getStringValue("userdef02")); //
			processVO.setUserdef03(vor[0].getStringValue("userdef03")); //
			processVO.setUserdef04(vor[0].getStringValue("userdef04")); //
			processVO.setUserdef05(vor[0].getStringValue("userdef05")); //
			processVO.setUserdef06(vor[0].getStringValue("userdef06")); //
			processVO.setUserdef07(vor[0].getStringValue("userdef07")); //
			processVO.setUserdef08(vor[0].getStringValue("userdef08")); //
			processVO.setUserdef09(vor[0].getStringValue("userdef09")); //
			processVO.setUserdef10(vor[0].getStringValue("userdef10")); //
			processVO.setCmpfileid(vor[0].getStringValue("cmpfileid")); //
			return processVO; //
		} else {
			return null; //
		}
	}

	//ȡ�����л���!
	private ActivityVO[] getWFActivityVOs(String _datasourcename, String processid) throws Exception {
		return getWFActivityVOs(_datasourcename, processid, "pub_wf_activity"); //
	}

	//ȡ����ʷ����
	private ActivityVO[] getHistoryWFActivityVOs(String processid) throws Exception {
		return getWFActivityVOs(null, processid, "pub_wf_activity_copy"); //
	}

	//ͬʱ���ݻ��ڱ�����ʷ���ڱ�ķ���!
	private ActivityVO[] getWFActivityVOs(String _datasourcename, String processid, String _tableName) throws Exception {
		ArrayList activityList = new ArrayList();
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_datasourcename, "select * from  " + _tableName + " where processid='" + processid + "' order by uiname"); //
		for (int i = 0; i < hvs.length; i++) {
			ActivityVO _vo = (ActivityVO) hvs[i].convertToRealOBJ(ActivityVO.class);
			activityList.add(_vo); //
		}
		return (ActivityVO[]) activityList.toArray(new ActivityVO[0]);
	}

	//ȡ����������
	private TransitionVO[] getWFTransitionVOs(String _datasourcename, String _processid, ActivityVO[] _activityvos) throws Exception {
		return getWFTransitionVOs(_datasourcename, _processid, _activityvos, "pub_wf_transition"); //
	}

	//ȡ��������ʷ����!�Ժ���ܲ���Ҫ��!��Ϊ�汾����Ҫ���µķ���!
	private TransitionVO[] getHistoryWFTransitionVOs(String _processid, ActivityVO[] _activityvos) throws Exception {
		return getWFTransitionVOs(null, _processid, _activityvos, "pub_wf_transition_copy"); //
	}

	//��������ȡ���ߵķ���,����������ǵ��ı�����
	private TransitionVO[] getWFTransitionVOs(String _datasourcename, String _processid, ActivityVO[] _activityvos, String _tableName) throws Exception {
		ArrayList transitionList = new ArrayList();
		HashVO[] vor = getCommDMO().getHashVoArrayByDS(_datasourcename, "select * from " + _tableName + " where processid = '" + _processid + "'");
		for (int i = 0; i < vor.length; i++) {
			TransitionVO _vo = new TransitionVO();
			_vo.setId(vor[i].getIntegerValue("id").intValue());
			_vo.setProcessid(vor[i].getStringValue("processid"));
			_vo.setCode(vor[i].getStringValue("code"));
			_vo.setWfname(vor[i].getStringValue("wfname"));
			_vo.setUiname(vor[i].getStringValue("uiname"));
			_vo.setDealtype(vor[i].getStringValue("dealtype")); //��������,�����ύ��ܾ�֮��!!!

			_vo.setMailsubject(vor[i].getStringValue("mailsubject")); //�ʼ�����..
			_vo.setMailcontent(vor[i].getStringValue("mailcontent")); //�ʼ�����..

			//��������ɫ
			_vo.setFonttype(vor[i].getStringValue("fonttype")); //��������
			_vo.setFontsize(vor[i].getIntegerValue("fontsize")); //�����С
			_vo.setForeground(vor[i].getStringValue("foreground")); //ǰ����ɫ
			_vo.setBackground(vor[i].getStringValue("background")); //������ɫ

			//����point
			String points = vor[i].getStringValue("points"); //���ݿ��д洢���۵��λ��!!
			String fromactivityId = vor[i].getStringValue("fromactivity");
			String toactivityId = vor[i].getStringValue("toactivity");
			List pointLs = parseToPointsWithStartAndEndPoints(points, fromactivityId, toactivityId, _activityvos); //
			_vo.setPoints(pointLs);

			_vo.setFromactivity(vor[i].getIntegerValue("fromactivity")); //
			_vo.setToactivity(vor[i].getIntegerValue("toactivity")); //
			_vo.setCondition(vor[i].getStringValue("conditions"));
			_vo.setReasoncodesql(vor[i].getStringValue("reasoncodesql")); //
			_vo.setIntercept(vor[i].getStringValue("intercept")); ////
			_vo.setLineType(vor[i].getIntegerValue("linetype", new Integer(2)));
			_vo.setSingle(vor[i].getBooleanValue("issingle", true));
			transitionList.add(_vo);
		}
		return (TransitionVO[]) transitionList.toArray(new TransitionVO[0]);
	}

	//ȡ�����о���!!
	private GroupVO[] getWFGroupVOs(String _datasourcename, String _processid) throws Exception {
		return getWFGroupVOs(_datasourcename, _processid, "pub_wf_group"); //
	}

	//ȡ��������ʷ����!
	private GroupVO[] getHistoryWFGroupVOs(String _processid) throws Exception {
		return getWFGroupVOs(null, _processid, "pub_wf_group_copy"); //
	}

	//����ȡ���������ת������!
	private GroupVO[] getWFGroupVOs(String _datasourcename, String _processid, String _tableName) throws Exception {
		ArrayList<GroupVO> groupList = new ArrayList<GroupVO>();

		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("select * from " + _tableName + " where processid = '" + _processid + "' order by grouptype,x,y"); //�����ڲ����ֳ������������д洢���ٴ�ʱ˳��Ȼ����ԭ����??ԭ����DB2���ݿ��������order by����,�������˳�򲢲��ǲ�����Ⱥ�˳��!!Ȼ��UI���������ж���Ϊ���Ⱥ�����ĵ�˳�򣬶����µ���X���߼�,����������������! 

		HashVO[] vor = getCommDMO().getHashVoArrayByDS(_datasourcename, sb_sql.toString());
		for (int i = 0; i < vor.length; i++) {
			GroupVO _vo = new GroupVO();
			_vo.setId(vor[i].getIntegerValue("id"));
			_vo.setProcessid(vor[i].getStringValue("processid"));
			_vo.setGrouptype(vor[i].getStringValue("grouptype")); //
			_vo.setCode(vor[i].getStringValue("code"));
			_vo.setWfname(vor[i].getStringValue("wfname"));
			_vo.setUiname(vor[i].getStringValue("uiname"));

			_vo.setX(vor[i].getIntegerValue("x")); //
			_vo.setY(vor[i].getIntegerValue("y")); //
			_vo.setWidth(vor[i].getIntegerValue("width")); //
			_vo.setHeight(vor[i].getIntegerValue("height")); //

			_vo.setFonttype(vor[i].getStringValue("fonttype")); //��������
			_vo.setFontsize(vor[i].getIntegerValue("fontsize")); //�����С
			_vo.setForeground(vor[i].getStringValue("foreground")); //ǰ����ɫ
			_vo.setBackground(vor[i].getStringValue("background")); //������ɫ
			_vo.setPosts(vor[i].getStringValue("posts"));
			groupList.add(_vo);
		}
		return (GroupVO[]) groupList.toArray(new GroupVO[0]);
	}

	/**
	 * 
	 * @param _fromid
	 * @param _newCode
	 * @param _newName
	 */
	public void copyWorkFlowProcess(String _fromid, String _newCode, String _newName) throws Exception {
		ArrayList al_sqls = new ArrayList(); //

		//��������!!!
		CommDMO commDMO = new CommDMO(); //
		HashVOStruct hst_process = commDMO.getHashVoStructByDS(null, "select * from pub_wf_process  where id='" + _fromid + "'"); //
		String str_newProcessId = commDMO.getSequenceNextValByDS(null, "S_PUB_WF_PROCESS"); //�µ�����ID
		String[] str_process_cols = hst_process.getHeaderName(); //
		HashVO hvo_process = hst_process.getHashVOs()[0]; //
		InsertSQLBuilder isql_sql = new InsertSQLBuilder("pub_wf_process"); //
		for (int i = 0; i < str_process_cols.length; i++) {
			if (str_process_cols[i].equalsIgnoreCase("id")) { //���id,��
				isql_sql.putFieldValue("id", str_newProcessId); //
			} else if (str_process_cols[i].equalsIgnoreCase("code")) { //���id,��
				isql_sql.putFieldValue("code", _newCode); //
			} else if (str_process_cols[i].equalsIgnoreCase("name")) { //���id,��
				isql_sql.putFieldValue("name", _newName); //
			} else {
				isql_sql.putFieldValue(str_process_cols[i], hvo_process.getStringValue(str_process_cols[i])); //
			}
		}
		al_sqls.add(isql_sql.getSQL()); //

		//���ƻ��ڣ�
		HashVOStruct hst_activity = commDMO.getHashVoStructByDS(null, "select * from pub_wf_activity   where processid='" + _fromid + "'"); //
		String[] str_activity_cols = hst_activity.getHeaderName(); //���ڵ��ֶ�
		HashVO[] hvs_activitys = hst_activity.getHashVOs(); //���л���!
		HashMap oldNewIdMap = new HashMap(); //
		for (int i = 0; i < hvs_activitys.length; i++) { //�������л���
			String str_old_actid = hvs_activitys[i].getStringValue("id"); //ԭ���Ļ���id
			String str_new_actid = commDMO.getSequenceNextValByDS(null, "S_PUB_WF_ACTIVITY"); //
			oldNewIdMap.put(str_old_actid, str_new_actid); //�ɵĻ��ڱ����ɶ???
			InsertSQLBuilder isql_sql_act = new InsertSQLBuilder("pub_wf_activity"); //
			for (int j = 0; j < str_activity_cols.length; j++) { //
				if (str_activity_cols[j].equalsIgnoreCase("id")) {
					isql_sql_act.putFieldValue("id", str_new_actid); //
				} else if (str_activity_cols[j].equalsIgnoreCase("processid")) { //
					isql_sql_act.putFieldValue("processid", str_newProcessId); //
				} else {
					isql_sql_act.putFieldValue(str_activity_cols[j], hvs_activitys[i].getStringValue(str_activity_cols[j])); //
				}
			}
			al_sqls.add(isql_sql_act.getSQL()); //
		}

		//��������!
		HashVOStruct hst_trans = commDMO.getHashVoStructByDS(null, "select * from pub_wf_transition where processid='" + _fromid + "'"); //
		String[] str_trans_cols = hst_trans.getHeaderName(); //���ߵ��ֶ�
		HashVO[] hvs_trans = hst_trans.getHashVOs(); //��������!
		for (int i = 0; i < hvs_trans.length; i++) { //������������
			String str_new_transid = commDMO.getSequenceNextValByDS(null, "S_PUB_WF_TRANSITION"); //���ߣ�
			InsertSQLBuilder isql_sql_tran = new InsertSQLBuilder("pub_wf_transition"); //
			for (int j = 0; j < str_trans_cols.length; j++) { //
				if (str_trans_cols[j].equalsIgnoreCase("id")) {
					isql_sql_tran.putFieldValue("id", str_new_transid); //
				} else if (str_trans_cols[j].equalsIgnoreCase("processid")) { //
					isql_sql_tran.putFieldValue("processid", str_newProcessId); //
				} else if (str_trans_cols[j].equalsIgnoreCase("fromactivity")) { //
					isql_sql_tran.putFieldValue("fromactivity", (String) oldNewIdMap.get(hvs_trans[i].getStringValue("fromactivity"))); //
				} else if (str_trans_cols[j].equalsIgnoreCase("toactivity")) { //
					isql_sql_tran.putFieldValue("toactivity", (String) oldNewIdMap.get(hvs_trans[i].getStringValue("toactivity"))); //
				} else {
					isql_sql_tran.putFieldValue(str_trans_cols[j], hvs_trans[i].getStringValue(str_trans_cols[j])); //
				}
			}
			al_sqls.add(isql_sql_tran.getSQL()); //
		}

		//���Ʒ���
		HashVOStruct hst_group = commDMO.getHashVoStructByDS(null, "select * from pub_wf_group where processid='" + _fromid + "'"); //
		String[] str_group_cols = hst_group.getHeaderName(); //���ڵ��ֶ�
		HashVO[] hvs_groups = hst_group.getHashVOs(); //���л���!
		for (int i = 0; i < hvs_groups.length; i++) { //�������л���
			String str_new_groupid = commDMO.getSequenceNextValByDS(null, "S_PUB_WF_GROUP"); //
			InsertSQLBuilder isql_sql_group = new InsertSQLBuilder("pub_wf_group"); //
			for (int j = 0; j < str_group_cols.length; j++) { //
				if (str_group_cols[j].equalsIgnoreCase("id")) {
					isql_sql_group.putFieldValue("id", str_new_groupid); //
				} else if (str_group_cols[j].equalsIgnoreCase("processid")) { //
					isql_sql_group.putFieldValue("processid", str_newProcessId); //
				} else {
					isql_sql_group.putFieldValue(str_group_cols[j], hvs_groups[i].getStringValue(str_group_cols[j])); //
				}
			}
			al_sqls.add(isql_sql_group.getSQL()); //
		}
		commDMO.executeBatchByDS(null, al_sqls); //
	}

	//ȡ��ϵͳ���еĹ������嵥
	public ProcessVO[] getAllWFProcesses() throws Exception {
		ArrayList al_process = new ArrayList(); //
		HashVO[] vor = getCommDMO().getHashVoArrayByDS(null, "select id, code, name from pub_wf_process where 1=1 order by id");
		for (int i = 0; i < vor.length; i++) {
			ProcessVO _vo = new ProcessVO();
			_vo.setId(vor[i].getStringValue("id"));
			_vo.setCode(vor[i].getStringValue("code"));
			_vo.setName(vor[i].getStringValue("name"));
			al_process.add(_vo);
		}
		return (ProcessVO[]) al_process.toArray(new ProcessVO[0]);
	}

	/**
	 * ȡ��һ�����������л���,���ǰ������ͽṹ�����!����ǳ���Ҫ!
	 * ��Ϊ����ҵ,��������Ŀ�ж������������,����һͼ���������Wordʱ,����ı�����˳�򲻶�!
	 * ���ͻ���Ҫ��˳��ʵ���Ǹ��ݻ���֮���໥ָ������,��A����ָ����B,��B����A�Ķ���! ���൱�������е��ӽ��!! ����ĸ�����û����ָ����,�����Ǹ������!!
	 * ˵���˾�����������ͼ��ʵ����һ�����ͽṹ,ֻ�����е��ӽ���ָ�򸸽��! ����ͬһ��Ľ��,Ҫ��һ������X,Y��˳������!!
	 * ���������ֿͻ�Ҫ����������ϲ���һ���ֲ���Сѭ��ʱ,���뱣֤���Сѭ����ȫ�������Ϻ��ټ���������! ����Ҫ�и�ǿ���ж�!! ��Ϊʱ���ϵ������! �Ժ����Ż�!
	 * @param _dsName
	 * @param _processid
	 * @return
	 */
	public ActivityVO[] getOrderedActivitys(String _dsName, String _processid) throws Exception {
		ActivityVO[] allActivityVOs = getWFActivityVOs(_dsName, _processid); //ȡ�����л���!
		TransitionVO[] allTransVOs = getWFTransitionVOs(_dsName, _processid, allActivityVOs); //��������

		//�������ͽṹ!!�Ҷ��ӵ���Ϸ???�������Ҹ���,��Ϊһ���˿����ж������,������һ����׼����!����һ����ά����
		for (int i = 0; i < allActivityVOs.length; i++) { //�������л���!
			HashSet hst_myallParents = new HashSet(); //��¼�ҵ����и���!
			HashSet hst_myallSons = new HashSet(); //��¼�ҵ����и���!
			for (int j = 0; j < allTransVOs.length; j++) { //������������!
				//System.out.println("���ߵ�Ŀ��=[" + allTransVOs[j].getToactivity() + "],��ǰ����id=[" + allActivityVOs[i].getId() + "]"); //
				//�ҳ��ҵ����и���!!
				if (allTransVOs[j].getToactivity() != null && allTransVOs[j].getToactivity().intValue() == allActivityVOs[i].getId().intValue()) { //����и����ߵ�Ŀ�������,��˵��������ߵ���Դ�����ҵĸ���!
					//System.out.println("������һ���ְ�!!!"); //
					hst_myallParents.add(allTransVOs[j].getFromactivity()); //������������Դ��Ϊ�ҵĸ���!
				}

				//�ҳ��ҵ����ж���!!
				if (allTransVOs[j].getFromactivity() != null && allTransVOs[j].getFromactivity().intValue() == allActivityVOs[i].getId().intValue()) { //����и����ߵ���Դ������,��˵��������ߵ�Ŀ������ҵĶ���!
					//System.out.println("������һ������!!!"); //
					hst_myallSons.add(allTransVOs[j].getToactivity()); //������������Դ��Ϊ�ҵĸ���!
				}
			}

			if (hst_myallParents.size() > 0) {
				Integer[] li_parents = (Integer[]) hst_myallParents.toArray(new Integer[0]); //
				allActivityVOs[i].setMyAllParentIds(li_parents); //�����ҵ����и���
			}
			if (hst_myallSons.size() > 0) {
				Integer[] li_sons = (Integer[]) hst_myallSons.toArray(new Integer[0]); //
				allActivityVOs[i].setMyAllSonIds(li_sons); //
			}
		}

		//������һ��!!
		Arrays.sort(allActivityVOs, new ActivityVOComparator()); //����һ��!!
		//���úø�������Ӻ�,���ҳ�����һ�����,��û�и��׵�
		ArrayList al_roots = new ArrayList(); //
		for (int i = 0; i < allActivityVOs.length; i++) {
			if (allActivityVOs[i].getMyAllParentIds() == null) { //����û���û�и���,��˵���Ǹ����!
				al_roots.add(allActivityVOs[i]); //����!!
			}
		}
		if (al_roots.size() == 0) { //���û�и�����,���õ�һ��,�����������ڻ����γ�Ȧ,��ʱ�Ҳ�������!
			al_roots.add(allActivityVOs[0]); //
		}

		ActivityVO[] rootActivityVOs = (ActivityVO[]) al_roots.toArray(new ActivityVO[0]); //�õ������Ķ���VO,��Ϊ�����������,����û��Ҫ��������!
		ArrayList al_result = new ArrayList(); //����б����!
		for (int i = 0; i < rootActivityVOs.length; i++) { //�ݹ�����ҵ����������ڵ�����������!
			visitAllChildrenVOs(rootActivityVOs[i], al_result, allActivityVOs); //
		}
		return (ActivityVO[]) al_result.toArray(new ActivityVO[0]); //
	}

	//�ݹ��㷨!
	private void visitAllChildrenVOs(ActivityVO _rootVO, ArrayList _alResult, ActivityVO[] _allVOs) {
		if (!isContainVO(_alResult, _rootVO)) {
			_alResult.add(_rootVO); //�ȼ�������!!
		}

		Integer[] li_sons = _rootVO.getMyAllSonIds(); //���еĶ���
		if (li_sons != null && li_sons.length > 0) {
			ActivityVO[] mySonVOs = getMyAllSonsByFilter(li_sons, _allVOs, _alResult); //���ҳ����еĶ���,Ȼ����˵����ҵ���! Ȼ��X,Y����!
			if (mySonVOs != null && mySonVOs.length > 0) {
				Arrays.sort(mySonVOs, new ActivityVOComparator()); //�����еĶ�������!!
				for (int i = 0; i < mySonVOs.length; i++) {
					visitAllChildrenVOs(mySonVOs[i], _alResult, _allVOs); //�ݹ�����Լ�!!!
				}
			}
		}
	}

	//�ҳ��ҵ����ж��ӻ���,����������ҵ��Ľ���е�,����!!
	private ActivityVO[] getMyAllSonsByFilter(Integer[] _ids, ActivityVO[] _allVOs, ArrayList _alreadys) {
		ArrayList alReturnSons = new ArrayList(); //���ص��嵥
		for (int i = 0; i < _ids.length; i++) {
			ActivityVO findedVO = null; //
			for (int j = 0; j < _allVOs.length; j++) { //�������л���
				if (_allVOs[j].getId().intValue() == _ids[i].intValue()) { //����û��ڵ�id�����ҵ��������
					findedVO = _allVOs[j]; //
					break; //�ж�ѭ��,�������!!
				}
			}
			if (findedVO != null) { //����ҵ���,Ӧ�ÿ϶����ҵ�!
				if (!isContainVO(_alreadys, findedVO)) { //���û���ҵ���!�������Ҫ���صĽ����!
					alReturnSons.add(findedVO); //
				}
			}
		}
		if (alReturnSons.size() == 0) {
			return null; //
		}
		return (ActivityVO[]) alReturnSons.toArray(new ActivityVO[0]); //
	}

	//�ж��б����Ƿ����
	private boolean isContainVO(ArrayList _alReadys, ActivityVO _itemVO) {
		for (int k = 0; k < _alReadys.size(); k++) {
			ActivityVO itemVO = (ActivityVO) _alReadys.get(k); //
			if (itemVO.getId().intValue() == _itemVO.getId().intValue()) { //����������ҹ��˵�!
				return true;
			}
		}
		return false; //
	}

	//�������е��۵�����ɶ���,ͬʱ������ʼ��������λ��!!!
	private List parseToPointsWithStartAndEndPoints(String points, String startActivityId, String targetActivityId, ActivityVO[] _activityvos) throws Exception {
		List pointsLs = parseToPoints(points); //�Ƚ����ַ����д洢��!!
		ActivityVO startVO = findMatchActivityVO(startActivityId, _activityvos); //�ҵ���ʼ�Ļ���VO
		ActivityVO targetVO = findMatchActivityVO(targetActivityId, _activityvos); //�ҵ������Ļ���VO
		double[] startPoint = new double[2];
		//������ʼλ��!
		if (startVO == null) {
			startPoint[0] = 0;
			startPoint[1] = 0;
		} else {
			startPoint[0] = startVO.getX().doubleValue();
			startPoint[1] = startVO.getY().doubleValue();
		}
		pointsLs.add(0, startPoint); //�ڵ�һ��λ���ϲ�����ʼ��
		//����������λ��
		double[] targetPoint = new double[2]; //
		if (targetVO == null) {
			targetPoint[0] = 0;
			targetPoint[1] = 0;
		} else {
			targetPoint[0] = targetVO.getX().doubleValue();
			targetPoint[1] = targetVO.getY().doubleValue();
		}
		pointsLs.add(pointsLs.size(), targetPoint); //��ĩβλ�ò��������!!!
		return pointsLs;
	}

	//�����ϵ��ַ���,����һ���б�
	private List parseToPoints(String _pointXYs) {
		List pointsLs = new ArrayList();
		String points[] = null; //
		if (_pointXYs != null && !_pointXYs.trim().equals("")) {
			points = _pointXYs.split(";");
		} else {
			points = new String[0];
		}
		for (int i = 0; i < points.length; i++) {
			String point[] = points[i].split(",");
			double[] pointXY = new double[2];
			pointXY[0] = Double.valueOf(point[0]).doubleValue();
			pointXY[1] = Double.valueOf(point[1]).doubleValue();
			pointsLs.add(pointXY);
		}
		return pointsLs;
	}

	//�����л����������ҵ���һ������!!
	private ActivityVO findMatchActivityVO(String _id, ActivityVO[] _activityvos) {
		for (int i = 0; i < _activityvos.length; i++) {
			if (_activityvos[i].getId().toString().equals(_id)) {
				return _activityvos[i];
			}
		}
		return null;
	}

}
