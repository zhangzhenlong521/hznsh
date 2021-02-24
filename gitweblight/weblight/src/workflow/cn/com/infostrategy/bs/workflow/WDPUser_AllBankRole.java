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
public class WDPUser_AllBankRole implements WorkflowDynamicParticipateIfc {

	private String str_rolecode = null; //

	/**
	 * Ĭ�ϲ�ѯ����
	 */
	private WDPUser_AllBankRole() {
	}

	public WDPUser_AllBankRole(String _rolename) {
		this.str_rolecode = _rolename; //
	}

	private String getDefineInfo() {
		return "��̬�����߶���,��Χ:[ȫ��],��ɫ����Ϊ��:[" + str_rolecode + "]"; //
	}

	/**
	 * �õ�һ��������
	 */
	public WorkFlowParticipantBean getDynamicParUsers(String _loginuserid, BillVO _billvo, HashVO _dealpool, String code, String dealTyp, String code2, String _curractivityCode, String _curractivityName) throws Exception {
		CommDMO commDmo = new CommDMO(); //
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		parBean.setParticipantDeptType("ȫ��"); //

		String str_zh_dept_id = "";
		String str_zh_dept_code = "";
		String str_zh_dept_name = "ȫ��";
		String str_zh_dept_linkcode = "%";

		parBean.setParticipantDeptId(str_zh_dept_id); //
		parBean.setParticipantDeptLinkcode(str_zh_dept_linkcode); //

		if (str_rolecode == null || str_rolecode.trim().equals("")) { //����������ɫ,��ֱ�ӷ���!!��ʱ�͵����Ƿ��ʼ���������!!!
			parBean.setParticiptMsg("��̬�����߶���,��Χ:[ȫ��],��ɫ����Ϊ��:[" + str_rolecode + "],������Ȼû��ֱ���ҵ���,������ͨ���Ӳ���ѡ����!"); //
			return parBean; //ֱ�ӷ���
		}

		//��������ɫ
		HashVO[] hvo_role = commDmo.getHashVoArrayByDS(null, "select id,code,name from pub_role where code='" + str_rolecode + "'"); //
		String str_roleid = hvo_role[0].getStringValue("id"); //
		String str_rolename = hvo_role[0].getStringValue("name"); //

		HashMap map_user_role = commDmo.getHashMapBySQLByDS(null, "select userid c1,userid c2 from pub_user_role where roleid='" + str_roleid + "'"); //
		String[] str_users_2 = (String[]) (map_user_role.keySet().toArray(new String[0])); ////
		String str_condition = new TBUtil().getInCondition(str_users_2); ////

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
		parBean.setParticiptMsg(getDefineInfo() + ",�ҵ���Ա����[" + hvs.length + "]"); //
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
				userBeans[i].setSuccessParticipantReason("���㶯̬����������,[ȫ��],��ɫ[" + str_rolename + "]"); ///
			}
			parBean.setParticipantUserBeans(userBeans); //
		}

		return parBean; //
	}
}
