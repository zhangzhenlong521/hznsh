package cn.com.infostrategy.bs.workflow;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.workflow.engine.WorkflowDynamicParticipateIfc;

/**
 * ��ȫ�������ҵ���Ϊʲô�Ľ�ɫ��������!!!
 * @author xch
 *
 */
public class WDPUser_HeadBankRole implements WorkflowDynamicParticipateIfc {

	private String str_deptid = null; //
	private String str_rolecode = null; //

	/**
	 * Ĭ�ϲ�ѯ����
	 */
	private WDPUser_HeadBankRole() {
	}

	public WDPUser_HeadBankRole(String _loginUserID, String _rolename) {
		this.str_deptid = _loginUserID; //
		this.str_rolecode = _rolename; //
	}

	private String getDefineInfo() {
		return "��̬�����߶���,��Χ:[����],��ɫ����Ϊ��:[" + str_rolecode + "]"; //
	}

	/**
	 * �õ�һ��������
	 */
	public WorkFlowParticipantBean getDynamicParUsers(String _loginuserid, BillVO _billvo, HashVO _dealpool, String code, String dealTyp, String code2, String _curractivityCode, String _curractivityName) throws Exception {
		CommDMO commDmo = new CommDMO(); //
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		parBean.setParticipantDeptType("����"); //��������

		if (str_deptid == null || str_deptid.trim().equals("") || str_deptid.trim().equalsIgnoreCase("null")) {
			parBean.setParticiptMsg(getDefineInfo() + ",����¼��Ա����������Ϊ��!!"); ///
			return parBean; //ֱ�ӷ���
		}

		parBean.setParticipantDeptId(null); //��������.

		if (str_rolecode == null || str_rolecode.trim().equals("")) { //����������ɫ,��ֱ�ӷ���!!��ʱ�͵����Ƿ��ʼ���������!!!
			parBean.setParticiptMsg(getDefineInfo() + ",������Ȼ����ֱ���ҵ���,������ͨ���Ӳ���ѡ����!"); ///
			return parBean; //ֱ�ӷ���
		}

		//��������˽�ɫ!!!!!!!!!!
		HashVO[] hvo_role = commDmo.getHashVoArrayByDS(null, "select id,code,name from pub_role where code='" + str_rolecode + "'"); //
		String str_roleid = null;
		String str_rolename = null;
		if (hvo_role.length > 0) {
			str_roleid = hvo_role[0].getStringValue("id"); //
			str_rolename = hvo_role[0].getStringValue("name"); //
		} else {
			parBean.setParticiptMsg(getDefineInfo() + ",���ñ����ڽ�ɫ��������û���ҵ���Ӧ�ļ�¼!!"); ///
			return parBean; //ֱ�ӷ���
		}

		TBUtil tbUtil = new TBUtil();
		String[] str_allDeptIDs = commDmo.getStringArrayFirstColByDS(null, "select id from pub_corp_dept where corptype like '����%'"); //
		String str_deptincondition = tbUtil.getInCondition(str_allDeptIDs); //

		//�ҳ������ŵ�������,����һ���˿��Լ�ְ��������,������������ǰ�������!!
		HashMap map_user_dept = commDmo.getHashMapBySQLByDS(null, "select userid c1,userid c2 from pub_user_post where userdept in (" + str_deptincondition + ")"); //�ҳ���������������Ա
		HashMap map_user_role = commDmo.getHashMapBySQLByDS(null, "select userid c1,userid c2 from pub_user_role where roleid='" + str_roleid + "'"); //
		String[] str_users_1 = (String[]) (map_user_dept.keySet().toArray(new String[0]));
		String[] str_users_2 = (String[]) (map_user_role.keySet().toArray(new String[0]));
		String[] str_userids = tbUtil.getInterSectionFromTwoArray(str_users_1, str_users_2); //
		String str_condition = new TBUtil().getInCondition(str_userids); //

		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		sb_sql.append("deptid,"); //
		sb_sql.append("deptcode,"); //
		sb_sql.append("deptname,"); //
		sb_sql.append("postid,"); //
		sb_sql.append("postcode,"); //
		sb_sql.append("postname,"); //
		sb_sql.append("userid,"); //
		sb_sql.append("usercode,"); //
		sb_sql.append("username "); //
		sb_sql.append("from v_pub_user_post_1 "); //
		sb_sql.append("where userid in (" + str_condition + ")"); //

		HashVO[] hvs = commDmo.getHashVoArrayByDS(null, sb_sql.toString()); //
		parBean.setParticiptMsg(getDefineInfo() + ",��[" + str_allDeptIDs.length + "]���ӻ���,�ҵ���Ա����[" + hvs.length + "]"); //
		if (hvs != null && hvs.length > 0) {
			WorkFlowParticipantUserBean[] userBeans = new WorkFlowParticipantUserBean[hvs.length]; //
			for (int i = 0; i < userBeans.length; i++) {
				userBeans[i] = new WorkFlowParticipantUserBean(); //

				userBeans[i].setUserid(hvs[i].getStringValue("userid")); //��Ա
				userBeans[i].setUsercode(hvs[i].getStringValue("usercode")); //
				userBeans[i].setUsername(hvs[i].getStringValue("username")); //

				userBeans[i].setUserdeptid(hvs[i].getStringValue("deptid")); //�����ߴ���Ļ���
				userBeans[i].setUserdeptcode(hvs[i].getStringValue("deptcode")); //
				userBeans[i].setUserdeptname(hvs[i].getStringValue("deptname")); //

				userBeans[i].setUserroleid(str_roleid); //�����ߴ���Ľ�ɫ
				userBeans[i].setUserrolecode(str_rolecode); //
				userBeans[i].setUserrolename(str_rolename); //

				userBeans[i].setParticipantType("��̬������"); //
				userBeans[i].setSuccessParticipantReason("���㶯̬����������,[����],��ɫ[" + str_rolename + "]"); ///
			}
			parBean.setParticipantUserBeans(userBeans); //
		}
		return parBean; //
	}
}
