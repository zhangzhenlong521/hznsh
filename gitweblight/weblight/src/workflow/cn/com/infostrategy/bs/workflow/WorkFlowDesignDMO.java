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
 * 工作流编辑的DMO,主要是负责流程图形的保存编辑等任务!!
 * 处理主键及其关联关系是该类和主要难点之一!
 * @author xch
 *
 */
public class WorkFlowDesignDMO extends AbstractDMO {
	CommDMO commDMO = null;
	Logger logger = WLTLogger.getLogger(WorkFlowDesignDMO.class); //日志类

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
	 * 保存工作流!!!就是拼成一批SQL提交,根据经验与实际压力测试,这段逻辑的性能是较高的!!!
	 * 后来统统改成了使用InsertSQLBuilder构建,既简洁高效又稳定容错!!
	 * @param _processVO
	 * @param _processID
	 * @throws Exception
	 */
	public void saveWFProcess(ProcessVO _processVO, String _processID) throws Exception {
		ArrayList al_sqls = new ArrayList(); //
		al_sqls.add("delete from pub_wf_group      where processid='" + _processID + "'"); //删除所有组
		al_sqls.add("delete from pub_wf_transition where processid='" + _processID + "'"); //删除所有连线
		al_sqls.add("delete from pub_wf_activity   where processid='" + _processID + "'"); //删除所有环节
		al_sqls.add("delete from pub_wf_process    where id='" + _processID + "'"); //删除流程,以便重新插入!!
		al_sqls.add(getSQL_InsertProcess(_processVO, _processID)); //创建流程的SQL

		//生成所有环节的SQL
		ActivityVO[] activityVOs = _processVO.getActivityVOs(); //
		for (int i = 0; i < activityVOs.length; i++) {
			ActivityVO vo = activityVOs[i];
			al_sqls.add(getSQL_Activity(vo, _processVO, _processID)); //
		}

		//生成TransitionVO对应的sql
		TransitionVO[] transitionVOs = _processVO.getTransitionVOs(); //
		for (int i = 0; i < transitionVOs.length; i++) {
			TransitionVO vo = transitionVOs[i];
			al_sqls.add(getSQL_Transtion(vo, _processVO, _processID)); //
		}

		//生成group中的数据..
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
		dmo.executeBatchByDS(null, al_sqls); //实际插入数据库,一批插入!!
		//long ll_2 = System.currentTimeMillis(); //
		//logger.debug("保存流程图成功,共处理[" + al_sqls.size() + "]条SQL,耗时[" + (ll_2 - ll_1) + "]毫秒"); //
	}

	/**
	 * 构造创建流程的SQL
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

		isql.putFieldValue("wfeguiintercept", _processVO.getWfeguiintercept()); //UI端统一的拦截器
		isql.putFieldValue("wfegbsintercept", _processVO.getWfegbsintercept()); //BS端统一的拦截器
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
	 * 构造创建环节的Insert语句
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
		iSB.putFieldValue("viewtype", _activity.getViewtype()); //格子类型
		iSB.putFieldValue("fonttype", _activity.getFonttype()); //字体类型
		iSB.putFieldValue("fontsize", _activity.getFontsize()); //字体大小
		iSB.putFieldValue("foreground", _activity.getForeground()); //前景颜色
		iSB.putFieldValue("background", _activity.getBackground()); //背景颜色
		iSB.putFieldValue("canhalfstart", _activity.getCanhalfstart(), "N"); //是否可以半路启动
		iSB.putFieldValue("halfstartrole", _activity.getHalfstartrole()); //可以半路启动的角色
		iSB.putFieldValue("halfstartdepttype", _activity.getHalfstartdepttype()); //可以半路启动的机构类型  gaofeng
		iSB.putFieldValue("canhalfend", _activity.getCanhalfend(), "N"); //是否可以半路结束
		iSB.putFieldValue("canselfaddparticipate", _activity.getCanselfaddparticipate(), "N"); //是否可以自己添加参与者
		iSB.putFieldValue("autocommit", _activity.getAutocommit(), "N"); //
		iSB.putFieldValue("isselfcycle", _activity.getIsselfcycle(), "N"); //是否自循环!!!
		iSB.putFieldValue("selfcyclerolemap", _activity.getSelfcyclerolemap()); //自循环映射的流程图!!!
		iSB.putFieldValue("iscanback", _activity.getIscanback(), "N"); //
		iSB.putFieldValue("isassignapprover", _activity.getIsassignapprover(), "N"); //是否指定审批者,如果为null,则使用"null"
		iSB.putFieldValue("isneedmsg", _activity.getIsneedmsg(), "N"); //
		iSB.putFieldValue("isneedreport", _activity.getIsneedreport(), "Y"); //
		iSB.putFieldValue("iscanlookidea", _activity.getIscanlookidea()); //
		iSB.putFieldValue("approvemodel", _activity.getApprovemodel(), "抢占"); //审批模式，如果为null，则使用"抢占"
		iSB.putFieldValue("approvenumber", _activity.getApprovenumber()); //
		iSB.putFieldValue("childwfcode", _activity.getChildwfcode()); //子流程编码
		iSB.putFieldValue("showparticimode", _activity.getShowparticimode()); //
		iSB.putFieldValue("participate_user", _activity.getParticipate_user()); //
		iSB.putFieldValue("participate_corp", _activity.getParticipate_corp()); //参与的机构
		iSB.putFieldValue("participate_group", _activity.getParticipate_group()); //参与的组,也就是角色!!
		iSB.putFieldValue("participate_dynamic", _activity.getParticipate_dynamic()); //
		iSB.putFieldValue("ccto_user", _activity.getCcto_user()); //抄送人员!!
		iSB.putFieldValue("ccto_corp", _activity.getCcto_corp()); //抄送机构!!
		iSB.putFieldValue("ccto_role", _activity.getCcto_role()); //抄送角色!!
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
	 * 创建连线的SQL!!
	 * @param _transition
	 * @param _process
	 * @param _processID
	 * @return
	 */
	private String getSQL_Transtion(TransitionVO _transition, ProcessVO _process, String _processID) {
		List routing = _transition.getPoints(); //折线上的断点的位置需要计算一把,即算成12,16;15,28;26,34的样子!!
		String str_points = null; //
		if (routing != null && routing.size() > 1) {
			StringBuffer points = new StringBuffer("");
			routing.remove(0); //删除第一个!
			routing.remove(routing.size() - 1); //删除最后一个!下面的size应该变了
			for (int i = 0; i < routing.size(); i++) {
				double[] point = (double[]) routing.get(i); //取得位置
				points.append(point[0] + "," + point[1]);
				if (i != routing.size() - 1) {
					points.append(";"); //如果不是最后一个,则需要加个分号!!!
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
	 * 创建组的新增SQL!组每次都是创建ID的
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
	 * 根据流程编码,取得一个流程对象!!!
	 * @param code
	 * @return
	 */
	public ProcessVO getWFProcessByWFCode(String _processCode) throws Exception {
		return getWFProcessBySQL("select * from pub_wf_process where code='" + _processCode + "'"); //
	}

	//根据SQL取!
	private ProcessVO getWFProcessBySQL(String _sql) throws Exception {
		return getWFProcessBySQL(null, _sql);
	}

	//根据SQL取得流程VO
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

	//取得历史
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

	//根据SQL先构建一个ProcessVO
	private ProcessVO getProcessVOBySQL(String _datasourcename, String _sql) throws Exception {
		HashVO[] vor = getCommDMO().getHashVoArrayByDS(_datasourcename, _sql); //
		if (vor.length == 0) {
			return null;
		}
		if (vor.length > 0) {
			ProcessVO processVO = new ProcessVO();
			processVO.setId(vor[0].getStringValue("id"));//农行一图两表，用的sybase数据库，客户规定主键字段必须用char(36),其他字段必须用char和varchar，所以这里id改为字符类型
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

	//取得所有环节!
	private ActivityVO[] getWFActivityVOs(String _datasourcename, String processid) throws Exception {
		return getWFActivityVOs(_datasourcename, processid, "pub_wf_activity"); //
	}

	//取得历史环节
	private ActivityVO[] getHistoryWFActivityVOs(String processid) throws Exception {
		return getWFActivityVOs(null, processid, "pub_wf_activity_copy"); //
	}

	//同时兼容环节表与历史环节表的方法!
	private ActivityVO[] getWFActivityVOs(String _datasourcename, String processid, String _tableName) throws Exception {
		ArrayList activityList = new ArrayList();
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_datasourcename, "select * from  " + _tableName + " where processid='" + processid + "' order by uiname"); //
		for (int i = 0; i < hvs.length; i++) {
			ActivityVO _vo = (ActivityVO) hvs[i].convertToRealOBJ(ActivityVO.class);
			activityList.add(_vo); //
		}
		return (ActivityVO[]) activityList.toArray(new ActivityVO[0]);
	}

	//取得所有连线
	private TransitionVO[] getWFTransitionVOs(String _datasourcename, String _processid, ActivityVO[] _activityvos) throws Exception {
		return getWFTransitionVOs(_datasourcename, _processid, _activityvos, "pub_wf_transition"); //
	}

	//取得所有历史连线!以后可能不需要的!因为版本管理要有新的方法!
	private TransitionVO[] getHistoryWFTransitionVOs(String _processid, ActivityVO[] _activityvos) throws Exception {
		return getWFTransitionVOs(null, _processid, _activityvos, "pub_wf_transition_copy"); //
	}

	//兼容两种取连线的方法,即他们最后都是调的本方法
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
			_vo.setDealtype(vor[i].getStringValue("dealtype")); //处理类型,即有提交与拒绝之分!!!

			_vo.setMailsubject(vor[i].getStringValue("mailsubject")); //邮件主题..
			_vo.setMailcontent(vor[i].getStringValue("mailcontent")); //邮件内容..

			//字体与颜色
			_vo.setFonttype(vor[i].getStringValue("fonttype")); //字体类型
			_vo.setFontsize(vor[i].getIntegerValue("fontsize")); //字体大小
			_vo.setForeground(vor[i].getStringValue("foreground")); //前景颜色
			_vo.setBackground(vor[i].getStringValue("background")); //背景颜色

			//设置point
			String points = vor[i].getStringValue("points"); //数据库中存储的折点的位置!!
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

	//取得所有矩阵!!
	private GroupVO[] getWFGroupVOs(String _datasourcename, String _processid) throws Exception {
		return getWFGroupVOs(_datasourcename, _processid, "pub_wf_group"); //
	}

	//取得所有历史矩阵!
	private GroupVO[] getHistoryWFGroupVOs(String _processid) throws Exception {
		return getWFGroupVOs(null, _processid, "pub_wf_group_copy"); //
	}

	//两种取矩阵的最终转调方法!
	private GroupVO[] getWFGroupVOs(String _datasourcename, String _processid, String _tableName) throws Exception {
		ArrayList<GroupVO> groupList = new ArrayList<GroupVO>();

		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("select * from " + _tableName + " where processid = '" + _processid + "' order by grouptype,x,y"); //曾经在蝉城现场发生部门阵列存储后再打开时顺序竟然不是原来的??原因是DB2数据库如果不带order by条件,则出来的顺序并不是插入的先后顺序!!然后UI端正好又有段以为是先后出来的的顺序，而重新调整X的逻辑,结果产生了奇怪现象! 

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

			_vo.setFonttype(vor[i].getStringValue("fonttype")); //字体类型
			_vo.setFontsize(vor[i].getIntegerValue("fontsize")); //字体大小
			_vo.setForeground(vor[i].getStringValue("foreground")); //前景颜色
			_vo.setBackground(vor[i].getStringValue("background")); //背景颜色
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

		//复制流程!!!
		CommDMO commDMO = new CommDMO(); //
		HashVOStruct hst_process = commDMO.getHashVoStructByDS(null, "select * from pub_wf_process  where id='" + _fromid + "'"); //
		String str_newProcessId = commDMO.getSequenceNextValByDS(null, "S_PUB_WF_PROCESS"); //新的流程ID
		String[] str_process_cols = hst_process.getHeaderName(); //
		HashVO hvo_process = hst_process.getHashVOs()[0]; //
		InsertSQLBuilder isql_sql = new InsertSQLBuilder("pub_wf_process"); //
		for (int i = 0; i < str_process_cols.length; i++) {
			if (str_process_cols[i].equalsIgnoreCase("id")) { //如果id,则
				isql_sql.putFieldValue("id", str_newProcessId); //
			} else if (str_process_cols[i].equalsIgnoreCase("code")) { //如果id,则
				isql_sql.putFieldValue("code", _newCode); //
			} else if (str_process_cols[i].equalsIgnoreCase("name")) { //如果id,则
				isql_sql.putFieldValue("name", _newName); //
			} else {
				isql_sql.putFieldValue(str_process_cols[i], hvo_process.getStringValue(str_process_cols[i])); //
			}
		}
		al_sqls.add(isql_sql.getSQL()); //

		//复制环节！
		HashVOStruct hst_activity = commDMO.getHashVoStructByDS(null, "select * from pub_wf_activity   where processid='" + _fromid + "'"); //
		String[] str_activity_cols = hst_activity.getHeaderName(); //环节的字段
		HashVO[] hvs_activitys = hst_activity.getHashVOs(); //所有环节!
		HashMap oldNewIdMap = new HashMap(); //
		for (int i = 0; i < hvs_activitys.length; i++) { //复制所有环节
			String str_old_actid = hvs_activitys[i].getStringValue("id"); //原来的环节id
			String str_new_actid = commDMO.getSequenceNextValByDS(null, "S_PUB_WF_ACTIVITY"); //
			oldNewIdMap.put(str_old_actid, str_new_actid); //旧的环节变成了啥???
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

		//复制连线!
		HashVOStruct hst_trans = commDMO.getHashVoStructByDS(null, "select * from pub_wf_transition where processid='" + _fromid + "'"); //
		String[] str_trans_cols = hst_trans.getHeaderName(); //连线的字段
		HashVO[] hvs_trans = hst_trans.getHashVOs(); //所有连线!
		for (int i = 0; i < hvs_trans.length; i++) { //复制所有连线
			String str_new_transid = commDMO.getSequenceNextValByDS(null, "S_PUB_WF_TRANSITION"); //连线！
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

		//复制分组
		HashVOStruct hst_group = commDMO.getHashVoStructByDS(null, "select * from pub_wf_group where processid='" + _fromid + "'"); //
		String[] str_group_cols = hst_group.getHeaderName(); //环节的字段
		HashVO[] hvs_groups = hst_group.getHashVOs(); //所有环节!
		for (int i = 0; i < hvs_groups.length; i++) { //复制所有环节
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

	//取得系统所有的工作流清单
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
	 * 取得一个流程中所有环节,但是按照树型结构排序的!这个非常重要!
	 * 因为在兴业,大连等项目中都遇到这个问题,即将一图两表输出成Word时,下面的表格输出顺序不对!
	 * 即客户想要的顺序实际是根据环节之间相互指向来的,即A环节指向了B,则B就是A的儿子! 即相当于树型中的子结点!! 如果哪个环节没有人指向他,则他是个根结点!!
	 * 说白了就是整个流程图其实就是一个树型结构,只不过有的子结点会指向父结点! 另外同一层的结点,要有一个按照X,Y的顺序排列!!
	 * 但后来发现客户要求如果发生合并的一个局部的小循环时,必须保证这个小循环先全部输出完毕后再继续往下走! 即还要有更强的判断!! 因为时间关系先这样! 以后再优化!
	 * @param _dsName
	 * @param _processid
	 * @return
	 */
	public ActivityVO[] getOrderedActivitys(String _dsName, String _processid) throws Exception {
		ActivityVO[] allActivityVOs = getWFActivityVOs(_dsName, _processid); //取得所有环节!
		TransitionVO[] allTransVOs = getWFTransitionVOs(_dsName, _processid, allActivityVOs); //所有连线

		//构建树型结构!!找儿子的游戏???不能是找父亲,因为一个人可能有多个父亲,即不是一个标准的树!而是一个多维的树
		for (int i = 0; i < allActivityVOs.length; i++) { //遍历所有环节!
			HashSet hst_myallParents = new HashSet(); //记录我的所有父亲!
			HashSet hst_myallSons = new HashSet(); //记录我的所有父亲!
			for (int j = 0; j < allTransVOs.length; j++) { //遍历所有连线!
				//System.out.println("连线的目标=[" + allTransVOs[j].getToactivity() + "],当前环节id=[" + allActivityVOs[i].getId() + "]"); //
				//找出我的所有父亲!!
				if (allTransVOs[j].getToactivity() != null && allTransVOs[j].getToactivity().intValue() == allActivityVOs[i].getId().intValue()) { //如果有根连线的目标等于我,则说明这根连线的来源就是我的父亲!
					//System.out.println("发现了一个爸爸!!!"); //
					hst_myallParents.add(allTransVOs[j].getFromactivity()); //拿这根连结的来源作为我的父亲!
				}

				//找出我的所有儿子!!
				if (allTransVOs[j].getFromactivity() != null && allTransVOs[j].getFromactivity().intValue() == allActivityVOs[i].getId().intValue()) { //如果有根连线的来源等于我,则说明这根连线的目标就是我的儿子!
					//System.out.println("发现了一个儿子!!!"); //
					hst_myallSons.add(allTransVOs[j].getToactivity()); //拿这根连结的来源作为我的父亲!
				}
			}

			if (hst_myallParents.size() > 0) {
				Integer[] li_parents = (Integer[]) hst_myallParents.toArray(new Integer[0]); //
				allActivityVOs[i].setMyAllParentIds(li_parents); //设置我的所有父亲
			}
			if (hst_myallSons.size() > 0) {
				Integer[] li_sons = (Integer[]) hst_myallSons.toArray(new Integer[0]); //
				allActivityVOs[i].setMyAllSonIds(li_sons); //
			}
		}

		//先排序一把!!
		Arrays.sort(allActivityVOs, new ActivityVOComparator()); //排序一把!!
		//设置好父亲与儿子后,先找出所有一级结点,即没有父亲的
		ArrayList al_roots = new ArrayList(); //
		for (int i = 0; i < allActivityVOs.length; i++) {
			if (allActivityVOs[i].getMyAllParentIds() == null) { //如果该环节没有父亲,则说明是根结点!
				al_roots.add(allActivityVOs[i]); //加入!!
			}
		}
		if (al_roots.size() == 0) { //如果没有根环节,则拿第一个,比如三个环节互相形成圈,这时找不到根的!
			al_roots.add(allActivityVOs[0]); //
		}

		ActivityVO[] rootActivityVOs = (ActivityVO[]) al_roots.toArray(new ActivityVO[0]); //得到根结点的对象VO,因为事先已排序过,所以没必要再排序了!
		ArrayList al_result = new ArrayList(); //结果列表对象!
		for (int i = 0; i < rootActivityVOs.length; i++) { //递归调用找到各个根环节的所有子孙结点!
			visitAllChildrenVOs(rootActivityVOs[i], al_result, allActivityVOs); //
		}
		return (ActivityVO[]) al_result.toArray(new ActivityVO[0]); //
	}

	//递归算法!
	private void visitAllChildrenVOs(ActivityVO _rootVO, ArrayList _alResult, ActivityVO[] _allVOs) {
		if (!isContainVO(_alResult, _rootVO)) {
			_alResult.add(_rootVO); //先加入根结点!!
		}

		Integer[] li_sons = _rootVO.getMyAllSonIds(); //所有的儿子
		if (li_sons != null && li_sons.length > 0) {
			ActivityVO[] mySonVOs = getMyAllSonsByFilter(li_sons, _allVOs, _alResult); //找找出所有的儿子,然后过滤掉已找到的! 然后按X,Y排序!
			if (mySonVOs != null && mySonVOs.length > 0) {
				Arrays.sort(mySonVOs, new ActivityVOComparator()); //对所有的儿子排序!!
				for (int i = 0; i < mySonVOs.length; i++) {
					visitAllChildrenVOs(mySonVOs[i], _alResult, _allVOs); //递归调用自己!!!
				}
			}
		}
	}

	//找出我的所有儿子环节,如果已在曾找到的结果中的,则不算!!
	private ActivityVO[] getMyAllSonsByFilter(Integer[] _ids, ActivityVO[] _allVOs, ArrayList _alreadys) {
		ArrayList alReturnSons = new ArrayList(); //返回的清单
		for (int i = 0; i < _ids.length; i++) {
			ActivityVO findedVO = null; //
			for (int j = 0; j < _allVOs.length; j++) { //遍历所有环节
				if (_allVOs[j].getId().intValue() == _ids[i].intValue()) { //如果该环节的id等于我的这个儿子
					findedVO = _allVOs[j]; //
					break; //中断循环,提高性能!!
				}
			}
			if (findedVO != null) { //如果找到了,应该肯定会找到!
				if (!isContainVO(_alreadys, findedVO)) { //如果没被找到过!则加入需要返回的结果中!
					alReturnSons.add(findedVO); //
				}
			}
		}
		if (alReturnSons.size() == 0) {
			return null; //
		}
		return (ActivityVO[]) alReturnSons.toArray(new ActivityVO[0]); //
	}

	//判断列表中是否包含
	private boolean isContainVO(ArrayList _alReadys, ActivityVO _itemVO) {
		for (int k = 0; k < _alReadys.size(); k++) {
			ActivityVO itemVO = (ActivityVO) _alReadys.get(k); //
			if (itemVO.getId().intValue() == _itemVO.getId().intValue()) { //如果曾经是找过了的!
				return true;
			}
		}
		return false; //
	}

	//将连线中的折点解析成对象,同时加上起始与结束点的位置!!!
	private List parseToPointsWithStartAndEndPoints(String points, String startActivityId, String targetActivityId, ActivityVO[] _activityvos) throws Exception {
		List pointsLs = parseToPoints(points); //先解析字符串中存储的!!
		ActivityVO startVO = findMatchActivityVO(startActivityId, _activityvos); //找到起始的环节VO
		ActivityVO targetVO = findMatchActivityVO(targetActivityId, _activityvos); //找到结束的环节VO
		double[] startPoint = new double[2];
		//处理起始位置!
		if (startVO == null) {
			startPoint[0] = 0;
			startPoint[1] = 0;
		} else {
			startPoint[0] = startVO.getX().doubleValue();
			startPoint[1] = startVO.getY().doubleValue();
		}
		pointsLs.add(0, startPoint); //在第一个位置上插入起始点
		//处理结束点的位置
		double[] targetPoint = new double[2]; //
		if (targetVO == null) {
			targetPoint[0] = 0;
			targetPoint[1] = 0;
		} else {
			targetPoint[0] = targetVO.getX().doubleValue();
			targetPoint[1] = targetVO.getY().doubleValue();
		}
		pointsLs.add(pointsLs.size(), targetPoint); //在末尾位置插入结束点!!!
		return pointsLs;
	}

	//解析断点字符串,返回一个列表
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

	//从所有环节数组中找到茉一个环节!!
	private ActivityVO findMatchActivityVO(String _id, ActivityVO[] _activityvos) {
		for (int i = 0; i < _activityvos.length; i++) {
			if (_activityvos[i].getId().toString().equals(_id)) {
				return _activityvos[i];
			}
		}
		return null;
	}

}
