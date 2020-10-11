package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.workflow.engine.WorkflowDynamicParticipateIfc;

/**
 * �ϱ�·�ߵĶ�̬������,�����ϱ�·��1,�ϱ�·��2,�ϱ�·��3
 * @author xch
 *
 */
public abstract class AbstractWDPUser_ReportDeptLine implements WorkflowDynamicParticipateIfc {

	private String str_rolecode = null; //

	public abstract String getReportDeptType(); //�����ϱ�·�ߵ�����..

	/**
	 * Ĭ�ϲ�ѯ����
	 */
	private AbstractWDPUser_ReportDeptLine() {
	}

	public AbstractWDPUser_ReportDeptLine(String _rolename) {
		this.str_rolecode = _rolename; //
	}

	/**
	 * �õ�һ��������
	 */
	public WorkFlowParticipantBean getDynamicParUsers(String _loginuserid, BillVO _billvo, HashVO _dealpool, String code, String dealTyp, String code2, String _curractivityCode, String _curractivityName) throws Exception {
		CommDMO dmo = new CommDMO(); //
		String str_pkdept = _dealpool.getStringValue("participant_userdept"); //�����������������,�������Ǵ����ĸ����Ŵ����....
		String str_pkdept_code = null;
		String str_pkdept_name = null;

		HashVO[] hvo_selfdept = dmo.getHashVoArrayByDS(null, "select id,code,name from pub_corp_dept where id='" + str_pkdept + "'"); //
		if (hvo_selfdept.length > 0) {
			str_pkdept_code = hvo_selfdept[0].getStringValue("code"); //
			str_pkdept_name = hvo_selfdept[0].getStringValue("name"); //
		}

		//�����¼��������
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		String str_reportdeptid = dmo.getStringValueByDS(null, "select reportdept" + getReportDeptType() + " from pub_corp_dept where id='" + str_pkdept + "'"); //ȡ�ñ����ŵ�LinkCode
		if (str_reportdeptid == null || str_reportdeptid.trim().equals("")) { //���û�ж����ϱ�·��1,ֱ�ӷ��ؿ�.
			parBean.setParticipantDeptType(null); //
			parBean.setParticipantDeptId(null); //�ϱ�·�ߵĻ���ID
			parBean.setParticiptMsg("��̬�����߶���,��Χ:[�ϱ�·��" + getReportDeptType() + "],��ɫ:[" + str_rolecode + "],���ֱ��˻���[" + str_pkdept_name + "]���ϱ�·��" + getReportDeptType() + "(pub_corp_dept.reportdept" + getReportDeptType() + ")Ϊ��!"); //
			return parBean; //ֱ�ӷ���
		}

		HashVO[] hvo_reportdept = dmo.getHashVoArrayByDS(null, "select id,code,name,linkcode from pub_corp_dept where id='" + str_reportdeptid + "'"); //
		String str_reportdept_name = hvo_reportdept[0].getStringValue("name"); //
		String str_reportdept_linkcode = hvo_reportdept[0].getStringValue("linkcode"); //

		parBean.setParticipantDeptType("�ϱ�·��" + getReportDeptType()); //
		parBean.setParticipantDeptId(str_reportdeptid); //�ϱ�·�ߵĻ���ID
		parBean.setParticipantDeptLinkcode(str_reportdept_linkcode); //

		if (str_rolecode == null || str_rolecode.trim().equals("")) { //����������ɫ,��ֱ�ӷ���!!��ʱ�͵����Ƿ��ʼ���������!!!
			parBean.setParticiptMsg("��̬�����߶���,��Χ:[�ϱ�·��" + getReportDeptType() + "],���ֱ��˻���[" + str_pkdept_name + "]���ϱ�·�߻���[" + str_reportdept_name + "],��ɫ����Ϊ��:[" + str_rolecode + "],������Ȼû��ֱ���ҵ���,������ͨ���Ӳ���ѡ����!"); ///
			return parBean; //ֱ�ӷ���
		}

		//����н�ɫ����!!!!!!!!!!!!!!!!!!!!!!!
		HashVO[] hvo_role = dmo.getHashVoArrayByDS(null, "select id,code,name from pub_role where code='" + str_rolecode + "'"); //
		String str_roleid = null;
		String str_rolename = null;
		if (hvo_role.length > 0) {
			str_roleid = hvo_role[0].getStringValue("id"); //
			str_rolename = hvo_role[0].getStringValue("name"); //
		}

		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("select ");
		sb_sql.append("t1.userid userid,");
		sb_sql.append("t2.code usercode, ");
		sb_sql.append("t2.name username, ");
		sb_sql.append("t1.userdept userdeptid, ");
		sb_sql.append("t3.code userdeptcode, ");
		sb_sql.append("t3.name userdeptname ");
		sb_sql.append("from pub_user_role t1 ");
		sb_sql.append("left join pub_user t2 on t1.userid=t2.id  ");
		sb_sql.append("left join pub_corp_dept t3 on t1.userdept=t3.id  ");
		sb_sql.append("where t3.linkcode like '" + str_reportdept_linkcode + "%' and t1.roleid='" + str_roleid + "' ");

		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //
		parBean.setParticiptMsg("��̬�����߶���,��Χ:[�ϱ�·��" + getReportDeptType() + "],�ҵ���Ӧ����[" + str_reportdeptid + "," + str_reportdept_name + "],��ɫ:[" + str_rolecode + "],�ҵ���Ա����[" + hvs.length + "]"); //
		if (hvs.length > 0) {
			WorkFlowParticipantUserBean[] userBeans = new WorkFlowParticipantUserBean[hvs.length]; //
			for (int i = 0; i < userBeans.length; i++) {
				userBeans[i] = new WorkFlowParticipantUserBean(); //

				userBeans[i].setUserid(hvs[i].getStringValue("userid")); //��Ա
				userBeans[i].setUsercode(hvs[i].getStringValue("usercode")); //
				userBeans[i].setUsername(hvs[i].getStringValue("username")); //

				userBeans[i].setUserdeptid(hvs[i].getStringValue("userdeptid")); //�����ߴ���Ļ���
				userBeans[i].setUserdeptcode(hvs[i].getStringValue("userdeptcode")); //
				userBeans[i].setUserdeptname(hvs[i].getStringValue("userdeptname")); //

				userBeans[i].setUserroleid(str_roleid); //�����ߴ���Ľ�ɫ
				userBeans[i].setUserrolecode(str_rolecode); //
				userBeans[i].setUserrolename(str_rolename); //

				userBeans[i].setParticipantType("��̬������"); //
				userBeans[i].setSuccessParticipantReason("���㶯̬����������,��������[�ϱ�·��" + getReportDeptType() + "]�Ļ���[" + str_reportdeptid + "],��ɫ[" + str_rolename + "]"); //
			}

			parBean.setParticipantUserBeans(userBeans); //
		}

		return parBean; //
	}

}
