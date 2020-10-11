package cn.com.infostrategy.bs.sysapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.mdata.JepFormulaParseAtBS;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * Ȩ��Ȩ�޲��Է������˵Ĵ�����!! ����Ȩ�޵ĺ����߼���������!!! �ǳ��ؼ�����! Ҳ�ܸ���!!!
 * �������ؼ�����,һ���Ǵ�Ȩ�޲��Ա���ȡ�������嵥���й���! һ���Ǹ��ݹ�ʽ���й���!(��������ʹ��),һ���Ƕ�����̽�ķ���!!!
 * @author xch
 *
 */
public class DataPolicyDMO {

	private CommDMO commDMO = null; //
	private TBUtil tbUtil = null; //
	private JepFormulaParseAtBS jepParse = null; //
	private WLTInitContext initContext = null; //

	/**
	 * ������Ҫ��,����һ������id,һ����������,ȥϵͳ���ҵ������ڻ���!!!
	 * ��ֻ��Ҫ���ػ���id,������������SQL,�����Ժ����
	 * @param _deptId
	 * @param _datapolicy
	 * @param _returnCol
	 * @return
	 * @throws Exception
	 */
	public HashMap getDataPolicyTargetCorpsByCorpId(String _deptId, String _datapolicy, String _returnCol) throws Exception { //
		String[] str_corpInfo = new String[3];
		String[][] str_corpData = getCommDMO().getStringArrayByDS(null, "select id,name,corptype from pub_corp_dept where id='" + _deptId + "'"); //
		if (str_corpData != null && str_corpData.length > 0) {
			str_corpInfo[0] = str_corpData[0][0]; //
			str_corpInfo[1] = str_corpData[0][1]; //
			str_corpInfo[2] = str_corpData[0][2]; //
		}
		return getTargetCorps(_datapolicy, str_corpInfo, null, _returnCol); //
	}

	/**
	 * ������Ҫ��, ����һ����Աid,һ������,ȥ�ҵ����л���!!
	 * @param _loginUserid
	 * @param _datapolicy
	 * @param _returnCol
	 * @return
	 * @throws Exception
	 */
	public HashMap getDataPolicyTargetCorpsByUserId(String _loginUserid, String _datapolicy, String _returnCol) throws Exception { //
		String[] str_corpInfo = getUserCorp(_loginUserid); //
		String[] str_myRoleCodes = getUserAllroles(_loginUserid); //
		return getTargetCorps(_datapolicy, str_corpInfo, str_myRoleCodes, _returnCol); //
	}

	/**
	 * ���ݲ��Ի�ȡ��Ա��Ϣ
	 * @param _loginUserid
	 * @param _datapolicy
	 * @param _returnCol
	 * @return
	 * @throws Exception
	 */
	public HashMap getDataPolicyTargetCorpsByUserId(HashMap _paramMap) throws Exception {
		String _userid = (String) _paramMap.get("userid");
		String _datapolicy = (String) _paramMap.get("datapolicy");
		String _returnCol = (String) _paramMap.get("returnCol");
		return this.getDataPolicyTargetCorpsByUserId(_userid, _datapolicy, _returnCol);
	}

	public HashMap getDataPolicyTargetUsersByUserId(String _loginUserid, String _datapolicy, String _returnCol) throws Exception {
		String[] str_corpInfo = getUserCorp(_loginUserid);
		String[] str_myRoleCodes = getUserAllroles(_loginUserid);
		String[] str_datapolicyData = getDataPolicyCondition(_datapolicy, "���˷�ʽ=��Ա����;��Ա�ֶ���=id", str_corpInfo, str_myRoleCodes);
		HashMap map = new HashMap();
		if (str_datapolicyData.length == 5) {
			String str_sql = str_datapolicyData[1];
			if (str_sql != null) {
				if ("id".equals(_returnCol.toLowerCase())) {
					if (str_sql.indexOf("and") > -1) { // ��������չsql ֻ�ܲ�ѯһ�ѷ��� sunfujun/20130130/bug�޸�
						String[] str_items = getCommDMO().getStringArrayFirstColByDS(null, "select " + _returnCol + " from pub_user where " + str_datapolicyData[1]); //
						map.put("AllUser" + _returnCol.toUpperCase() + "s", str_items);
					} else { //���ֻ��id in�ķ�ʽ����Բ�����id
						String[] items = str_sql.split("or");
						if (items != null && items.length > 0) {
							String item = null;
							StringBuffer sb = new StringBuffer();
							for (int oo = 0; oo < items.length; oo++) {
								item = items[oo];
								if (item != null && !"".equals(item)) {
									item = item.replaceAll(" ", "");
									item = item.replaceAll("in", "");
									item = item.replaceAll("id", "");
									item = item.replaceAll("\\(", "");
									item = item.replaceAll("\\)", "");
									item = item.replaceAll("\'", "");
									if (oo == 0) {
										sb.append(item);
									} else {
										sb.append("," + item);
									}
								}
							}
							String[] ids = sb.toString().split(",");
							map.put("AllUser" + _returnCol.toUpperCase() + "s", ids);
						}
					}
				} else {
					String[] str_items = getCommDMO().getStringArrayFirstColByDS(null, "select " + _returnCol + " from pub_user where " + str_datapolicyData[1]); //
					map.put("AllUser" + _returnCol.toUpperCase() + "s", str_items);
				}
			}
		}
		return map;
	}

	//���ݲ���,���ڻ�����Ϣ,��Ա��ɫ��Ϣ,��������пɿ�����!
	private HashMap getTargetCorps(String _datapolicy, String[] str_corpInfo, String[] str_myRoleCodes, String _returnCol) throws Exception {
		String[] str_datapolicyData = getDataPolicyCondition(_datapolicy, "���˷�ʽ=��������;", str_corpInfo, str_myRoleCodes); //
		HashMap map = new HashMap(); //
		map.put("$����˵��", "������Ӧ������������AllCorpIDs,isAllCorp,�ֱ��ʾ���л���id����,�Լ��Ƿ���ȫ������!"); //
		map.put("AllCorpIDs", new String[0]); //
		map.put("isAllCorp", ""); //
		if (str_datapolicyData.length == 5) { //�������������,
			String str_virtualIds = str_datapolicyData[2]; //������!
			if (str_virtualIds != null && !str_virtualIds.equals("")) {
				str_virtualIds = str_virtualIds.substring(str_virtualIds.indexOf("(") + 1, str_virtualIds.indexOf(")")); //
				String[] str_virtualIdArray = getTBUtil().split(str_virtualIds, ","); //�ָ�!!
				map.put("VirtualCorpIDs", str_virtualIdArray); //����������:
			}

			String str_ids = str_datapolicyData[3]; //
			String str_isAllCorp = str_datapolicyData[4]; //
			if (str_ids != null) {
				String[] str_idArrays = getTBUtil().split(str_ids, ";"); //�ָ������!
				map.put("AllCorpIDs", str_idArrays); //��Զ�������!!
				if (!_returnCol.equalsIgnoreCase("id")) { //����뷵�ص��ֶ�������id,��ֱ�ӷ���,���ٲ����ݿ���,�Ӷ��������!
					if ("Y".equals(str_isAllCorp)) { //��������л���,��ʹ��1=1��ѯ,�Ӷ��������!
						String[] str_items = getCommDMO().getStringArrayFirstColByDS(null, "select " + _returnCol + " from pub_corp_dept where 1=1"); //
						map.put("AllCorp" + _returnCol.toUpperCase() + "s", str_items);
					} else {
						String[] str_items = getCommDMO().getStringArrayFirstColByDS(null, "select " + _returnCol + " from pub_corp_dept where id in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_idArrays) + ")"); //
						map.put("AllCorp" + _returnCol.toUpperCase() + "s", str_items);
					}
				}
			}
			map.put("isAllCorp", str_isAllCorp); //
		}
		return map; //
	}

	/**
	 * �ҳ���¼��Ա����!
	 * @param _loginUserid
	 * @return
	 * @throws Exception
	 */
	private String[] getUserCorp(String _loginUserid) throws Exception {
		//���ҳ���¼��Ա�Ļ�������!���ɫ,��������������ɫƥ��������������!!
		String str_myCorpId = null; //�ұ��˵����ڻ���!!����Ϊ��,��Ϊû��Ϊ��¼��Ա������������!!
		String str_myCorpName = null; //����ʵ�ʻ�������
		String str_myCorpType = null; //��(��¼��Ա)�Ļ�������!!!
		String str_myExtcorptype = null; //�ҵĻ�����չ����
		HashVO[] hvs_myCorpInfo = getCommDMO().getHashVoArrayByDS(null, "select t1.userid,t1.userdept,t1.isdefault,t2.corptype,t2.name userdeptname,t2.extcorptype from pub_user_post t1 left join pub_corp_dept t2 on t1.userdept=t2.id where t1.userid='" + _loginUserid + "'"); //�ҳ��ҵ����ڻ���!!
		if (hvs_myCorpInfo != null && hvs_myCorpInfo.length > 0) { //����ҵ�����!!
			for (int i = 0; i < hvs_myCorpInfo.length; i++) {
				if ("Y".equals(hvs_myCorpInfo[i].getStringValue("isdefault"))) {
					str_myCorpId = hvs_myCorpInfo[i].getStringValue("userdept"); //�û��Ļ���!!!
					str_myCorpName = hvs_myCorpInfo[i].getStringValue("userdeptname"); //
					str_myCorpType = hvs_myCorpInfo[i].getStringValue("corptype"); //
					str_myExtcorptype = hvs_myCorpInfo[i].getStringValue("extcorptype"); //
					break; //�ж�ѭ��!
				}
			}
			if (str_myCorpId == null) { //�����һû�������Ƿ�Ĭ�ϻ���,��ֱ��ѡ���һ��!!
				str_myCorpId = hvs_myCorpInfo[0].getStringValue("userdept"); //
				str_myCorpName = hvs_myCorpInfo[0].getStringValue("userdeptname"); //
				str_myCorpType = hvs_myCorpInfo[0].getStringValue("corptype"); //
				str_myExtcorptype = hvs_myCorpInfo[0].getStringValue("extcorptype"); //
			}
		}
		return new String[] { str_myCorpId, str_myCorpName, str_myCorpType, str_myExtcorptype }; //
	}

	/**
	 * �ҳ���¼��Ա��ɫ
	 * @param _loginUserid
	 * @return
	 * @throws Exception
	 */
	private String[] getUserAllroles(String _loginUserid) throws Exception {
		//�ҳ���¼��Ա�����н�ɫ����!!
		String[] str_myRoleCodes = null; //�ҵ����н�ɫ����!! ����[�����ƶȹ���Ա][������]
		HashVO[] hvs_myRolesInfo = getCommDMO().getHashVoArrayByDS(null, "select t1.userid,t1.roleid,t2.code rolecode from pub_user_role t1 left join pub_role t2 on t1.roleid=t2.id where t1.userid='" + _loginUserid + "'"); //�ҵ����н�ɫ!!
		if (hvs_myRolesInfo != null && hvs_myRolesInfo.length > 0) {
			HashSet hst_roleCode = new HashSet(); //
			for (int i = 0; i < hvs_myRolesInfo.length; i++) {
				if (hvs_myRolesInfo[i].getStringValue("rolecode") != null) {
					hst_roleCode.add(hvs_myRolesInfo[i].getStringValue("rolecode")); //
				}
			}
			if (hst_roleCode.size() > 0) { //���������!!
				str_myRoleCodes = (String[]) hst_roleCode.toArray(new String[0]); //�õ��ҵ����н�ɫ!!!
			}
		}
		return str_myRoleCodes;
	}

	/**
	 * ֱ��ͨ��API����!
	 * @param _loginUserid ��¼��Աid
	 * @param _datapolicy  ��������
	 * @param _type  ��������,һ�������֣�1-��������,2-��Ա����
	 * @param _corpFieldName  �����ֶ�����,������blcorp,�����ͷ��� blcorp in (....),֮���Ա��뽫���ֶ��ͽ���,������ֱ�ӷ���һ��id�嵥������Ϊ��������С�����SQL�����������ʱ�����޷�ƴ����!!
	 * @param _userField ��Ա�ֶ�����,����createuser,�����ͷ��� createuser in ()...
	 * @return ����һ��String[],��һ������ϸ�������˵��,�ڶ����Ƿ��ص�sql����,���� blcorp in ('12','15');
	 * @throws Exception
	 */
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, int _type, String _corpFieldName, String _userFieldName) throws Exception {
		String str_formula = ""; //
		if (_type == 1) {
			str_formula = str_formula + "���˷�ʽ=��������;"; //
		} else if (_type == 2) {
			str_formula = str_formula + "���˷�ʽ=��Ա����;"; //
		} else {
			str_formula = str_formula + "���˷�ʽ=��������;"; //
		}

		if (_corpFieldName != null) {
			str_formula = str_formula + "�����ֶ���=" + _corpFieldName + ";"; //������¶����˻����ֶ���!Ĭ����createcorp
		}

		if (_userFieldName != null) {
			str_formula = str_formula + "��Ա�ֶ���=" + _userFieldName + ";"; //������¶�������Ա�ֶ���,Ĭ����createuser
		}

		return getDataPolicyCondition(_loginUserid, _datapolicy, str_formula); //
	}

	/**
	 * ����Ȩ�޲��Լ������!! ���,��ǿ��ĵط�,�����ݵ�¼��Ա,Ȩ�޲���Ȼ��ȥ�ҳ�����,��Ա�嵥,Ȼ��ƴ��SQL! �� blcorpid in ('')
	 * ������߼��Ƿ�����ҵ���״������ٶ�����̽����!! 
	 * ���������480������,������̽��120������!�ܹ�600������!!����������600�д���ǳ��ؼ���ʵ��!����һ���Խ�������������ص�Ȩ������!!�Ժ������ǲ���,��������,API����,�ȵȣ����ת��������!
	 * @param _loginUserid
	 * @param _datapolicy
	 * @param _datapolicyMap
	 * @return һ��String[],��һ������ϸ�������˵��,�ڶ����Ƿ��ص�sql����,���� blcorp in ('12','15');
	 * @throws Exception
	 */
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, String _datapolicyMap) throws Exception {
		String[] str_corpInfo = getUserCorp(_loginUserid); //
		String[] str_myRoleCodes = getUserAllroles(_loginUserid); //
		return getDataPolicyCondition(_datapolicy, _datapolicyMap, str_corpInfo, str_myRoleCodes); //
	}

	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, String _datapolicyMap, boolean isAddfieldName) throws Exception {
		String[] str_corpInfo = getUserCorp(_loginUserid); //
		String[] str_myRoleCodes = getUserAllroles(_loginUserid); //
		return getDataPolicyCondition(_datapolicy, _datapolicyMap, str_corpInfo, str_myRoleCodes, isAddfieldName); //
	}

	//�˷���Ϊԭ��ƽ̨��
	public String[] getDataPolicyCondition(String _datapolicy, String _datapolicyMap, String[] _loginUserCorpInfo, String[] _loginUserRoles) throws Exception {
		return getDataPolicyCondition(_datapolicy, _datapolicyMap, _loginUserCorpInfo, _loginUserRoles, false);
	}

	//̫ƽ��Ŀ�������˸�isAddfieldName���� 
	public String[] getDataPolicyCondition(String _datapolicy, String _datapolicyMap, String[] _loginUserCorpInfo, String[] _loginUserRoles, boolean isAddfieldName) throws Exception {
		StringBuilder sb_returnInfo = new StringBuilder("\r\n"); //
		String str_billFilterType = "��������"; //��[��������/��Ա����֮��]
		String str_billCorpField = "createcorp"; //��Ҫ����Map�õ�createcorp
		String str_billUserField = "createuser"; //��Ҫ����Map�õ�
		HashMap<String, String> confMap = new HashMap<String, String>(); //
		if (_datapolicyMap != null && !_datapolicyMap.trim().equals("")) { //�����ӳ�䶨��,����ʱ�����ʹ��ͬһ������,�����ɵ��ǵ��ֶ�����һ��,��ʱ��Ҫһ��state=status������ӳ��
			int li_pos = _datapolicyMap.indexOf("@"); //
			if (li_pos > 0) {
				String str_1 = _datapolicyMap.substring(0, li_pos); //
				String str_2 = _datapolicyMap.substring(li_pos + 1, _datapolicyMap.length()); //
				confMap.putAll(getTBUtil().convertStrToMapByExpress(str_1)); //������aa=123;bb=456;������,Ҫת��һ��Map
				confMap.putAll(getTBUtil().convertStrToMapByExpress(str_2)); //
			} else {
				confMap.putAll(getTBUtil().convertStrToMapByExpress(_datapolicyMap)); //
			}
		}
		if (confMap.containsKey("���˷�ʽ")) {
			str_billFilterType = (String) confMap.get("���˷�ʽ"); //
		}
		if (confMap.containsKey("�����ֶ���")) {
			str_billCorpField = (String) confMap.get("�����ֶ���"); //
		}
		if (confMap.containsKey("��Ա�ֶ���")) {
			str_billUserField = (String) confMap.get("��Ա�ֶ���"); //
		}
		boolean isNumberStr = getTBUtil().isStrAllNunbers(_datapolicy); //���Ƿ���������id���͵�,��Ϊ��XML����ʹ��������������
		HashVO[] hvs_policy = null; //
		if (isNumberStr) {
			hvs_policy = getCommDMO().getHashVoArrayByDS(null, "select * from pub_datapolicy where id='" + _datapolicy + "'"); //�������������id��ѯ
		} else {
			hvs_policy = getCommDMO().getHashVoArrayByDS(null, "select * from pub_datapolicy where name='" + _datapolicy + "'"); //������ַ���,��name��ѯ!!!
		}
		if (hvs_policy == null || hvs_policy.length <= 0) {
			sb_returnInfo.append("û���ҵ���Ӧ����Ϊ[" + _datapolicy + "]�Ĳ���,ֱ����Ϊ��ȫ���ܿ�!\r\n"); //
			return new String[] { sb_returnInfo.toString(), "'�޲��Զ���'='�޲��Զ���'" }; //
		}
		String str_dataPolicyId = hvs_policy[0].getStringValue("id"); //
		String str_dataPolicyName = hvs_policy[0].getStringValue("name"); //
		HashVO[] hvs_policy_b = getCommDMO().getHashVoArrayByDS(null, "select * from pub_datapolicy_b where datapolicy_id='" + str_dataPolicyId + "' order by seq,id"); //����ϸ!!
		if (hvs_policy_b == null || hvs_policy_b.length <= 0) {
			sb_returnInfo.append("��Ϊ[" + str_dataPolicyName + "]�Ĳ���û�ж���һ���嵥,ֱ����Ϊ��ȫ���ܿ�!\r\n"); //
			return new String[] { sb_returnInfo.toString(), "'�޲����嵥����'='�޲����嵥����'" }; //
		}
		sb_returnInfo.append("��ʼ�ж�:���õ�Ȩ�޲�����[" + str_dataPolicyName + "],���㷽ʽ��[" + str_billFilterType + "],�����ֶ���[" + str_billCorpField + "],��Ա�ֶ���[" + str_billUserField + "]\r\n"); //

		//�ҳ���¼��Ա������Ϣ���ɫ��Ϣ
		String str_myCorpId = _loginUserCorpInfo[0]; //�ұ��˵����ڻ���!!����Ϊ��,��Ϊû��Ϊ��¼��Ա������������!!
		String str_myCorpName = _loginUserCorpInfo[1]; //����ʵ�ʻ�������
		String str_myCorpType = _loginUserCorpInfo[2]; //��(��¼��Ա)�Ļ�������!!!
		String str_myExtcorptype = _loginUserCorpInfo[3]; //�ҵĻ�����չ����
		String[] str_myRoleCodes = _loginUserRoles; //
		sb_returnInfo.append("�ﱾ�����ڻ���[" + str_myCorpId + "/" + str_myCorpName + "],����������[" + str_myCorpType + "],");
		if (str_myRoleCodes != null && str_myRoleCodes.length > 0) {
			sb_returnInfo.append("�������н�ɫ��["); //
			for (int i = 0; i < str_myRoleCodes.length; i++) {
				sb_returnInfo.append(str_myRoleCodes[i] + ";"); //
			}
			sb_returnInfo.append("],����\"һ���û�\",\"��ͨԱ��\"���ֽ�ɫ��Ĭ�ϵ�!\r\n\r\n"); //
		} else {
			sb_returnInfo.append("���˵����н�ɫΪ��!����\"һ���û�\",\"��ͨԱ��\"���ֽ�ɫ��Ĭ�ϵ�!\r\n\r\n"); //
		}
		String[] str_appendRoleCodes = new String[] { "һ��Ա��", "һ����Ա", "һ���û�", "������Ա", "��ͨԱ��", "��ͨ��Ա", "��ͨ�û�" }; //Ϊ�˼���������,���һ���˲����κν�ɫ,��Ĭ����Ϊ�Զ�ӵ���⼸�ֽ�ɫ!Ȼ���ڲ������Զ���Ч������xch/2012-08-14��
		if (str_myRoleCodes == null || str_myRoleCodes.length <= 0) {
			str_myRoleCodes = str_appendRoleCodes; //
		} else {
			String[] str_spanRoles = new String[str_myRoleCodes.length + str_appendRoleCodes.length]; //
			System.arraycopy(str_myRoleCodes, 0, str_spanRoles, 0, str_myRoleCodes.length); //
			System.arraycopy(str_appendRoleCodes, 0, str_spanRoles, str_myRoleCodes.length, str_appendRoleCodes.length); //
			str_myRoleCodes = str_spanRoles; //
		}

		//����ƥ��������㣡���Լ���ĵ�һ�����������ҳ�һ���м������Ա���ƥ������,���һ��ûƥ����,��ֱ�ӷ��ء�����
		sb_returnInfo.append("���ʼ���ݱ��˵����ڻ����������ɫ,��[" + hvs_policy_b.length + "]��������ϸ��������ƥ��������\r\n"); //
		ArrayList al_matchPolicy = new ArrayList(); //
		for (int i = 0; i < hvs_policy_b.length; i++) {
			hvs_policy_b[i].setAttributeValue("$ԭ��������", "" + (i + 1)); //������,Ϊ������ʾ������Ϣʱ����֪�������ǵڼ�����������������!!
			String str_corptypes_m = hvs_policy_b[i].getStringValue("corptypes_m"); //����Ļ�������!
			String str_roles_m = hvs_policy_b[i].getStringValue("roles_m"); //�����ɫ
			String str_extcorptype_m = hvs_policy_b[i].getStringValue("extcorptype_m"); //���������չ���� by haoming 2016-04-08
			String str_corptype_g1 = hvs_policy_b[i].getStringValue("corptype_g1"); //�״��Ϸ��ʵĻ���
			String str_corptype_g2 = hvs_policy_b[i].getStringValue("corptype_g2"); //������̽�Ļ�������
			String str_extcorptype_g2 = hvs_policy_b[i].getStringValue("extcorptype_g2"); //������̽����չ����
			String str_roles_g = hvs_policy_b[i].getStringValue("roles_g"); //������̽����չ����
			String str_appendsqlcons = hvs_policy_b[i].getStringValue("appendsqlcons"); //SQL����

			StringBuilder sb_subfix = new StringBuilder("����������[" + (str_corptypes_m == null ? "" : str_corptypes_m) + "],��ɫ������[" + (str_roles_m == null ? "" : str_roles_m) + "],"); //
			if (str_extcorptype_m != null) {
				sb_subfix.append("������չ����������[" + str_extcorptype_m + "],"); //
			}
			if (str_corptype_g1 != null) {
				sb_subfix.append("�״����ݵĻ�������[" + str_corptype_g1 + "],"); //
			}

			if (str_corptype_g2 != null) {
				sb_subfix.append("��̽�Ļ�������[" + str_corptype_g2 + "],"); //
			}
			if (str_extcorptype_g2 != null) {
				sb_subfix.append("��̽����չ����[" + str_extcorptype_g2 + "],"); //
			}
			if (str_roles_g != null) {
				sb_subfix.append("��̽�Ľ�ɫ[" + str_roles_g + "],"); //
			}
			if (str_appendsqlcons != null) {
				sb_subfix.append("����SQL����[" + str_appendsqlcons + "],"); //
			}

			//System.out.println("�����������[" + str_corptypes_m + "],�����ɫ[" + str_roles_m + "]"); //
			boolean isCorpMatch = true; //�����Ƿ�ƥ����
			if (str_corptypes_m != null && !str_corptypes_m.trim().equals("")) { //�������������Ͳ�Ϊ��!
				if (str_myCorpType == null || str_myCorpType.trim().equals("")) {
					isCorpMatch = false; //
				} else {
					String[] str_items = getTBUtil().split(str_corptypes_m, ";"); //�ָ�һ��,�������ж����������!
					isCorpMatch = getTBUtil().isExistInArray(str_myCorpType, str_items); //�Ƿ񱾻�������������!
				}
			}

			boolean is_extcorptype_Match = true; //������չ�����Ƿ�ƥ����

			if (!getTBUtil().isEmpty(str_extcorptype_m)) {
				if (getTBUtil().isEmpty(str_myExtcorptype)) {
					is_extcorptype_Match = false;
				} else {
					String exporttypes[] = getTBUtil().split(str_myExtcorptype, ";");
					String str_extcorptype_ms[] = getTBUtil().split(str_extcorptype_m, ";");
					is_extcorptype_Match = getTBUtil().containTwoArrayCompare(str_extcorptype_ms, exporttypes); //
				}
			}

			boolean isRoleMatch = true; //��ɫ�Ƿ�ƥ����
			if (str_roles_m != null && !str_roles_m.trim().equals("")) { //
				if (str_myRoleCodes == null || str_myRoleCodes.length <= 0) {
					isRoleMatch = false; //
				} else {
					String[] str_items = getTBUtil().split(str_roles_m, ";"); //�ָ�,��Ϊ�����ж����ɫ
					isRoleMatch = getTBUtil().containTwoArrayCompare(str_myRoleCodes, str_items); //
				}
			}
			if (isCorpMatch && isRoleMatch && is_extcorptype_Match) { //����������ɫ��ƥ������,���¼������
				al_matchPolicy.add(hvs_policy_b[i]); //
				sb_returnInfo.append("[" + (i + 1) + "��]ƥ��ɹ���" + sb_subfix.toString() + "\r\n\r\n"); //
			} else {
				sb_returnInfo.append("[" + (i + 1) + "��]ƥ��ʧ�ܣ�" + sb_subfix.toString() + "\r\n\r\n"); //
			}
		}
		if (al_matchPolicy.size() <= 0) { //���ûƥ����һ������,��ֱ�ӷ���!!
			sb_returnInfo.append("û��һ�������嵥��������,ֱ�Ӿ������ܲ�ѯһ����¼!"); //
			return new String[] { sb_returnInfo.toString(), "'ƥ��������'='false'" }; //
		}
		//��ѯ�����л���,��Ϊ����������̽����ʱʹ��!!!ȡ���л�������߼��Ժ�������ɻ���!!!
		//֮���������ѯ���л���,����Ϊ���ǰ��û��ƥ��ɹ�һ��������ֱ�ӷ�����,����Ҫ��ѯ���л�����,�������������!!
		//		HashVO[] hvs_allCorps= getCommDMO().getHashVoArrayAsTreeStructByDS(null, "select id,name,parentid,corptype,extcorptype,seq from pub_corp_dept", "id", "parentid", "seq", null); //�ҳ����л�������,֮�����ҳ�����,����Ϊ���ӵļ�����Ҫ�ڻ��������������¶������,����Ҫ����������!
		HashVO[] hvs_allCorps = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate();
		int li_allCorpCount = hvs_allCorps.length; //���л�����������!! �����ȫ��,�򷵻س����Ľ�����������������,��SQL������������!
		HashMap allCorpMap = new HashMap(); //��key��������ݴ���һ����ϣ����,����������Ķ�!!
		for (int i = 0; i < hvs_allCorps.length; i++) {
			String str_corpId = hvs_allCorps[i].getStringValue("id"); //����id
			allCorpMap.put(str_corpId, hvs_allCorps[i]); //
		}
		ArrayList al_myParentCorpTypeList = null; //�洢�ҵĸ��׻������͵��б�!!����¼��Ա����ϵ����,үү...���硾�Ͼ�����->��������->����֧�С�
		if (str_myCorpId != null) { //������л���!
			HashVO myCorpHVO = (HashVO) allCorpMap.get(str_myCorpId); //
			String str_parentids = myCorpHVO.getStringValue("$parentpathids"); //�ҵ��ҵ����и���·��!
			if (str_parentids != null && !str_parentids.equals("")) { //������и���!!
				al_myParentCorpTypeList = new ArrayList(); //
				String[] str_myParentIdItems = getTBUtil().split(str_parentids, ";"); //�ָ�!!!
				for (int i = 0; i < str_myParentIdItems.length; i++) { //�����ҵĸ���!!
					HashVO parentItemVO = (HashVO) allCorpMap.get(str_myParentIdItems[i]); //����VO
					al_myParentCorpTypeList.add(new String[] { parentItemVO.getStringValue("id"), parentItemVO.getStringValue("corptype"), parentItemVO.getStringValue("$parentpathnamelink"), parentItemVO.getStringValue("extcorptype") }); //������id���������
				}
			}
		}
		HashVO myCorpVO = (HashVO) allCorpMap.get(str_myCorpId); //����VO
		str_myCorpName = myCorpVO.getStringValue("$parentpathnamelink"); //һ��ʼȡ���ҵĻ��������ǲ���·����,��������һ��·��!!!
		HashMap corpCacheMap = new HashMap(); //������,��Ϊ������ѭ�����ж�����̽,Ϊ�������Ȱѻ���ȡ��,�����Ͳ���Ҫÿ�μ���ʱȡ���ݿ���!!�Ӷ��������!!
		corpCacheMap.put("AllCorpHashVOs", hvs_allCorps); //���뻺��!
		corpCacheMap.put("AllCorpHashMap", allCorpMap); //���뻺��!

		//��ʼ�����״�����->������̽����,������븴�ӵ��߼�,��Լ160�д��룡����
		HashVO[] hvs_matchPolicy = (HashVO[]) al_matchPolicy.toArray(new HashVO[0]); //ƥ���ϵĲ����嵥!!!
		ArrayList al_corp_role_sql = new ArrayList(); //
		HashMap allCorpTypeMap = null; //�������ֵ����,�洢�������͵�ӳ��,���С�$�����š���Щ��̬��������!
		sb_returnInfo.append("\r\n��������ʼ������ƥ��ɹ���[" + hvs_matchPolicy.length + "]��������ϸ����\"������̽\"�������"); //
		for (int i = 0; i < hvs_matchPolicy.length; i++) { //����ƥ���ϵļ�¼
			//���״����ݼ���!!!�����ҵĸ�ϵ·�������ҵ���һ������ָ���������͵Ļ���,���û��,���ȫ��������һ��!
			sb_returnInfo.append("\r\n");
			String str_oldIndexNo = hvs_matchPolicy[i].getStringValue("$ԭ��������"); //������,Ϊ������ʾ������Ϣʱ����֪�������ǵڼ�����������������!!
			String str_corptype_g1 = hvs_matchPolicy[i].getStringValue("corptype_g1"); //�״��ϷõĻ�������!!����Ҳ���,��ȫ�з�Χ��Ѱ��! �����$������,$������,$������,����ݱ��˵�ʵ�ʻ�������ȥϵͳ������Ѱ�ҵ�ʵ�ʵ���Ҫ�ϷõĻ�������!!!
			String str_corptype_g2 = hvs_matchPolicy[i].getStringValue("corptype_g2"); //������̽�Ļ�������!!
			String str_extcorptype_g2 = hvs_matchPolicy[i].getStringValue("extcorptype_g2"); //������̽����չ��������!!
			String str_roles_g = hvs_matchPolicy[i].getStringValue("roles_g"); //������̽����Ա��ɫ!!
			String str_appendsqlcons = hvs_matchPolicy[i].getStringValue("appendsqlcons", ""); //SQL����
			if (str_corptype_g1 != null && str_corptype_g1.startsWith("$")) { //����Ƕ�̬���״��ϷõĻ�������!����Ҫ����һ��!!����ϵͳ������ȡ!!//
				String[] str_convertCorpTye = getDynamicCorpType(str_corptype_g1, allCorpTypeMap, str_myCorpType); //
				String str_infoPrefix = "���״����ܼ���(ת�����ܶ�̬����):��[" + str_oldIndexNo + "]�������嵥���״����ܵ��Ƕ�̬����[" + str_corptype_g1 + "],����ʵ�ʻ�����������[" + str_myCorpId + "/" + str_myCorpName + "/" + str_myCorpType + "],�����ӳ����[" + str_convertCorpTye[1] + "]"; //
				if (str_convertCorpTye[0] == null) { //���û�ҵ�
					sb_returnInfo.append(str_infoPrefix + ",���û�гɹ�ת��,��˵�������������?\r\n"); //
				} else {
					sb_returnInfo.append(str_infoPrefix + ",�ɹ�ת����õ�[" + str_convertCorpTye[0] + "]!\r\n"); //
					str_corptype_g1 = str_convertCorpTye[0]; //
				}
			}

			//�״������ҳ�ʵ�ʵĸ����!!!
			String str_rootCorpId_1 = null; //
			String str_rootCorpName_1 = null; //
			//����������״����ݵĻ�������,��������������!!!
			if (str_corptype_g1 != null && !str_corptype_g1.trim().equals("")) {
				if (al_myParentCorpTypeList != null) { //�����ҵĸ���!!
					for (int j = 0; j < al_myParentCorpTypeList.size(); j++) { //����!!
						String[] str_parentItem = (String[]) al_myParentCorpTypeList.get(j); //
						if (str_corptype_g1.equals(str_parentItem[1])) { //�������ָ������Ҫ�״��Ϸ��ʵĻ�������,��ֱ���˳�!�����/2018-08-24��
							str_rootCorpId_1 = str_parentItem[0]; //
							str_rootCorpName_1 = str_parentItem[2]; //
							break; //
						}
					}
					if (str_rootCorpId_1 == null) { //������ҵĸ���·����û�ҵ�,��ȫ��ȥ��,����ֱ�������[����/ȫ��]��,������������ָ����ֱ��ȥ�һ���·����ȥ�ҵ�,���µ�������̫����,�ʽ���·������,�Ҳ�����ȫ����,��Ϊһ�������ܲ�����������򲻻�������!!��������Ϻ����С��������͵�ȴû��,��ܿ����ҵ����Ͼ����С�ͷ��ȥ!���Ǹ�����!!
						for (int j = 0; j < hvs_allCorps.length; j++) {
							if (hvs_allCorps[j].getStringValue("corptype", "").equals(str_corptype_g1)) {
								str_rootCorpId_1 = hvs_allCorps[j].getStringValue("id"); //
								str_rootCorpName_1 = hvs_allCorps[j].getStringValue("$parentpathnamelink"); //
								break; //
							}
						}
					}
				}
				if (str_rootCorpId_1 == null) {
					sb_returnInfo.append("���״����ܼ���:��[" + str_oldIndexNo + "]�������嵥����ʵ����������[" + str_corptype_g1 + "],û���ҵ������͵Ļ���!\r\n"); //
				} else {
					sb_returnInfo.append("���״����ܼ���:��[" + str_oldIndexNo + "]�������嵥����ʵ����������[" + str_corptype_g1 + "],�ҵ�����[" + str_rootCorpId_1 + "/" + str_rootCorpName_1 + "]!\r\n"); //
				}
			}
			//������̽����,���״������ҵ�������,������Ѱ�������¿����������л���!
			boolean isSqlDefineNull = (str_appendsqlcons == null || str_appendsqlcons.trim().equals("")) ? true : false; //
			boolean isRoleDefineNull = (str_roles_g == null || str_roles_g.trim().equals("")) ? true : false; //
			if (str_rootCorpId_1 != null) { //����ҵ��˶�Ӧ�Ļ���!!
				if (str_extcorptype_g2 != null && str_extcorptype_g2.indexOf("$") >= 0) { //���������̽Ҳ���ڶ�̬��,��ҲҪ����һ��!�������[$��ͬ������Χ],�����滻��rootid��������չ����!!
					StringBuilder sb_newCorptype_g2 = new StringBuilder(";"); //�����и��ֺ�!
					String[] str_splitItems = getTBUtil().split(str_extcorptype_g2, ";"); //�ȷָ�!!
					for (int j = 0; j < str_splitItems.length; j++) { //����!!!
						if (str_splitItems[j].startsWith("$")) { //����Ƕ�̬��!!
							String[] str_convertCorpTye = getDynamicCorpType(str_splitItems[j], allCorpTypeMap, str_myCorpType); //ת��һ��!�õ�ʵ�ʵĻ�������!
							String str_infoPrefix = "�������̽����(ת����̬��չ����):��[" + str_oldIndexNo + "]�������嵥������̽�ж�̬����[" + str_splitItems[j] + "],����ʵ�ʻ�����������[" + str_myCorpId + "/" + str_myCorpName + "/" + str_myCorpType + "],�����ӳ����[" + str_convertCorpTye[1] + "]"; //
							if (str_convertCorpTye[0] == null) {
								sb_returnInfo.append(str_infoPrefix + ",��û���ҵ���Ӧ�Ļ�������,����Ըö�̬��չ������������!\r\n"); //
							} else { //�ҵ�ָ���һ�������,Ȼ�����ҵĸ���·�����ҵ���Ӧ�ĵ�һ����������͵ĸ��׵���չ����!!
								String str_findCorpId = null, str_findCorpName = null, str_findCorpType = null, str_findExtCorpType = null; ////
								if (al_myParentCorpTypeList != null) { //�����ҵĸ���!!
									for (int k = 0; k < al_myParentCorpTypeList.size(); k++) { //�����ҵ����и���
										String[] str_parentItem = (String[]) al_myParentCorpTypeList.get(k); //
										if (str_parentItem[1].equals(str_convertCorpTye[0])) { //�������ָ������Ҫ�״��Ϸ��ʵĻ�������,��ֱ���˳�!
											str_findCorpId = str_parentItem[0]; //
											str_findCorpType = str_parentItem[1]; //
											str_findCorpName = str_parentItem[2]; //
											str_findExtCorpType = str_parentItem[3]; //
											break; //
										}
									}
								}
								if (str_findCorpId == null) { //���û�ҵ��������,
									sb_returnInfo.append(str_infoPrefix + ",��Ȼ�ɹ�ת�����˻�������[" + str_convertCorpTye[0] + "],�����ڱ��˸�������û���ҵ������͵Ļ���,����Ҳ���Ըö�̬��չ������������!\r\n"); //
								} else { //����ҵ��������!!
									if (str_findExtCorpType == null || str_findExtCorpType.trim().equals("")) {
										sb_returnInfo.append(str_infoPrefix + ",�ɹ�ת�����˻�������[" + str_convertCorpTye[0] + "],Ҳ�ɹ��ڸ��������ҵ��˶�Ӧ����[" + str_findCorpId + "/" + str_findCorpName + "],���û�������չ����Ϊ��,����Ҳ���Ըö�̬��չ������������!\r\n"); //
									} else {
										if (str_findExtCorpType.startsWith(";")) { //Ҫȥͷ!!!
											str_findExtCorpType = str_findExtCorpType.substring(1, str_findExtCorpType.length()); //
										}
										if (str_findExtCorpType.endsWith(";")) { //Ҫȥβ!!
											str_findExtCorpType = str_findExtCorpType.substring(0, str_findExtCorpType.length() - 1); //; //������ǷֺŽ�β,�����!!!
										}
										sb_newCorptype_g2.append(str_findExtCorpType + ";"); //
										sb_returnInfo.append(str_infoPrefix + ",�ɹ�ת�����˻�������[" + str_convertCorpTye[0] + "],Ҳ�ɹ��ڸ��������ҵ��˶�Ӧ����[" + str_findCorpId + "/" + str_findCorpName + "],�Ҹû�������չ����[" + str_findExtCorpType + "]�ֲ�Ϊ��,��ƴ�Ӽ���!\r\n"); //
									}
								}
							}
						} else {
							sb_newCorptype_g2.append(str_splitItems[j] + ";"); //
						}
					}
					str_extcorptype_g2 = sb_newCorptype_g2.toString(); //�������¸�ֲ
				}

				//ʵ�ʿ�ʼ������̽!!ת����һ�����ӵĶ�����̽���㺯��!!!֮���Խ�������̽��װ����һ������,����Ϊ��������Ҳ�õ��Ǹ�!!
				boolean bo_iscontainroot = hvs_matchPolicy[i].getBooleanValue("iscontainroot", false); //��̽ʱ�Ƿ���������?
				boolean bo_iscontainchildren = hvs_matchPolicy[i].getBooleanValue("iscontainchildren", false); //��̽���Ƿ����������?
				String str_uniontype = hvs_matchPolicy[i].getStringValue("uniontype", "����"); //��̽ʱ�Ƿ���������?

				ArrayList al_returnCorpIdList = secondDownFindAllCorpChildrensByCondition(str_rootCorpId_1, str_corptype_g1, str_corptype_g2, str_extcorptype_g2, null, bo_iscontainroot, bo_iscontainchildren, str_uniontype, corpCacheMap); //

				//�����Ժ���Կ��ǽ�������̽����ϸ������־Ҳȡ����,Ȼ����뱾��־�С�����
				String str_secondDownInfoPrefix = "�������̽����:��[" + str_oldIndexNo + "]�������嵥���״����ܻ���[" + str_rootCorpId_1 + "/" + str_rootCorpName_1 + "]�ķ�Χ��,����ʵ�ʵ���̽������������[" + (str_corptype_g2 == null ? "" : str_corptype_g2) + "],��չ������������[" + (str_extcorptype_g2 == null ? "" : str_extcorptype_g2) + "],���ҵ�[" + al_returnCorpIdList.size() + "]���ӽṹ"; //
				if (al_returnCorpIdList.size() >= li_allCorpCount) { //������������л�����,��ֱ�ӷ���
					if (str_billFilterType.equals("��������")) {
						if (isSqlDefineNull) { //���SQL����Ϊ��,��ֱ�ӷ���!
							sb_returnInfo.append(str_secondDownInfoPrefix + ",��Ϊ�������������л�����,��ֱ�ӷ������л���!\r\n"); //
							return new String[] { sb_returnInfo.toString(), "'ȫ������'='ȫ������'" }; //ֱ�ӷ���,ɶ��������!!!
						} else {
							al_corp_role_sql.add(new Object[] { al_returnCorpIdList, str_roles_g, str_appendsqlcons, str_oldIndexNo }); ////
						}
					} else if (str_billFilterType.equals("��Ա����")) {
						if (isRoleDefineNull && isSqlDefineNull) { //�����ɫҲΪ��,SQL����ҲΪ��!!!��ֱ�ӷ���
							sb_returnInfo.append(str_secondDownInfoPrefix + ",��Ϊ�������������л�����,��ֱ�ӷ���������Ա!\r\n"); //
							return new String[] { sb_returnInfo.toString(), "'ȫ����Ա'='ȫ����Ա'" }; //ֱ�ӷ���,ɶ��������!!!
						} else {
							al_corp_role_sql.add(new Object[] { al_returnCorpIdList, str_roles_g, str_appendsqlcons, str_oldIndexNo }); ////
						}
					}
				} else { //���������������ȫ��!
					al_corp_role_sql.add(new Object[] { al_returnCorpIdList, str_roles_g, str_appendsqlcons, str_oldIndexNo }); ////
				}
				if (str_billFilterType.equals("��������")) {
					sb_returnInfo.append(str_secondDownInfoPrefix + "," + (isSqlDefineNull ? "������Ψһ�Ի����ϲ�!" : "��������Ψһ�Ի����ϲ�!") + "\r\n"); //
				} else if (str_billFilterType.equals("��Ա����")) {
					sb_returnInfo.append(str_secondDownInfoPrefix + "," + ((isSqlDefineNull && isRoleDefineNull) ? "������Ψһ����Ա�ϲ�!" : "��������Ψһ����Ա�ϲ�!") + "\r\n"); //
				}
			} else { //���û�����״����ݵĻ�������,���߸���û�ҵ������͵Ļ���,���޷��������ݼ���,��ֱ�Ӵ���SQL��������(��ʹ��������̽Ҳ����)!!������ʱ����������,������Ա�����Բ鿴���ܼ�Ϊ���ܡ����ƶ�,��ʱ��ֻ��Ҫ����SQL����!���ݲ���Ҫ����ʲô���ݵĻ�������!
				String str_infoPrefix = "��Ƕ�����̽����:��[" + str_oldIndexNo + "]�������嵥û���ҵ����ܵĻ���,���Բ����ж�����̽"; //
				if (str_billFilterType.equals("��������")) {
					if (isSqlDefineNull) { //�����û�ж���SQL��������,��ʲô������,����������¸���������ʵû������!�������,�����!
						sb_returnInfo.append(str_infoPrefix + ",��û��SQL����,��������������!\r\n"); //
					} else { //
						if (getTBUtil().isExistInArray(str_appendsqlcons.trim(), new String[] { "99=99", "\"99=99\"", "1=1", "\"1=1\"" })) { //���������Լ����99=99,1=1,�����⴦��!����ʱ����Ҫ�鿴����!����ĳ�ֽ�ɫ���˿��Կ�����!
							sb_returnInfo.append(str_infoPrefix + ",��SQL�������������ض���" + str_appendsqlcons + ",��ֱ�ӷ���!!\r\n"); //
							return new String[] { sb_returnInfo.toString(), "99=99" }; //ֱ�ӷ���,ɶ��������!!!
						} else {
							al_corp_role_sql.add(new Object[] { null, str_roles_g, str_appendsqlcons, str_oldIndexNo }); ////
							sb_returnInfo.append(str_infoPrefix + ",����SQL����,�����������!\r\n"); //
						}
					}
				} else if (str_billFilterType.equals("��Ա����")) {
					if (isSqlDefineNull && isRoleDefineNull) {
						sb_returnInfo.append(str_infoPrefix + ",��û��SQL���ɫ����,��������Ա����!\r\n"); //
					} else {
						if (getTBUtil().isExistInArray(str_appendsqlcons.trim(), new String[] { "99=99", "\"99=99\"", "1=1", "\"1=1\"" })) { //���������Լ����99=99,1=1,�����⴦��
							sb_returnInfo.append(str_infoPrefix + ",��SQL�������������ض���" + str_appendsqlcons + ",��ֱ�ӷ���������Ա!!\r\n"); //
							return new String[] { sb_returnInfo.toString(), "99=99" }; //ֱ�ӷ���,ɶ��������!!!
						} else {
							al_corp_role_sql.add(new Object[] { null, str_roles_g, str_appendsqlcons, str_oldIndexNo }); ////
							sb_returnInfo.append(str_infoPrefix + ",����SQL���ɫ����,������Ա����!\r\n"); //
						}
					}
				}

			}
		} //end for,��ѭ������!!

		//�����������ƴ��SQL����(��Լ150�д���)!��Ϊǰ��������̽����������Ľ��ֻ��һ��ArrayList�����嵥,�еĻ���sql��������!��ʱ��Ҫ����ƴ��SQL!
		sb_returnInfo.append("\r\n");
		String str_realsql = ""; //
		String str_virtualCorpSql = null; //�������id
		String str_allCorpIds = null; //���л���,��ÿ�����Լ�������Ĵ�����,������������(����SQL)!!��Ϊ�����ɺ���������,�����������뷵��һ��SQL,ȥ��һ����!����ֱ�ӷ��ػ���!�����ò�������ʵ�ָ���ĳ������,�ҵ�ĳ���ϼ���������Ʒ�Χ!
		String str_isAllCorps = "N"; //�Ƿ��ҳ������л���?���������л���id����ʱ,�ͻ����Զ�ȥƴ��SQLʱ,������������л���,��SQL����1=1,������Ҫ����һ�����!
		if (str_billFilterType.equals("��������")) { //����ǡ��������ˡ����ͣ���������
			HashSet hst_allCorpids = new HashSet(); //��¼���л���,������������丸��·���ϵ�������!!
			HashSet hst_distinctCorpids = new HashSet(); //���ڼ�¼Ψһ�Ժϲ��Ļ���!
			ArrayList al_realsqls = new ArrayList(); //��¼�������Լ��������sql����,���硾blcorp in ('15','17','19')��
			String str_spansIndex = ""; //��Ҫ����ϲ�����Ĳ�����ϸ!�������и��ǳ��ؼ����߼�,�����ÿ�����Զ�û�ж��帽��SQL����,����ʱ�������Լ�������Ļ����п����ظ�!��������������һ��Ψһ�Ժϲ�!
			for (int i = 0; i < al_corp_role_sql.size(); i++) { //for_3
				Object[] rowObjs = (Object[]) al_corp_role_sql.get(i); //
				ArrayList al_corpids = (ArrayList) rowObjs[0]; //��һ��,���ǻ����嵥,����Ϊ��!!����ֻ����SQL��������,��û�ж������ݻ�������!!����:�κ��˶��ɲ鿴������='����'�����ƶ�
				String str_sqlcons = (String) rowObjs[2]; //
				String str_indexNo = (String) rowObjs[3]; //
				if (al_corpids != null) {
					hst_allCorpids.addAll(al_corpids); //�������л���������
				}
				if (str_sqlcons == null || str_sqlcons.trim().equals("")) { //�������SQL����Ϊ��,��˵����Ҫ����Ψһ�Ժϲ�����!
					if (al_corpids != null) { //����л����嵥!!
						hst_distinctCorpids.addAll(al_corpids); //��¼�û������μ�Ψһ�Ժϲ�
						str_spansIndex = str_spansIndex + str_indexNo + ","; //����ʱ����֪��,���׺ϲ��ļ�������!
					} else {
						//�������SQLΪ��,�һ����嵥��Ϊ��,��û������,����ʲô������!!�����������һ���������!!
					}
				} else { //��������˸���SQL����,��û�취����Ψһ�Ժϲ�,��Ϊ�����߼��Ǳ������븽��SQL����"and"�����!���Ҳ����Ψһ�Ժϲ�,���߼��ʹ���!!
					String str_sqlItem = ""; //
					str_sqlcons = convertSqlCons(str_sqlcons, confMap); //��SQL�������й�ʽ����
					if (al_corpids == null) { //�������û��,��ֻ��SQL����,����ֻ��sql����������='����'��,��ֱ�Ӽ���SQL
						str_sqlItem = "(" + str_sqlcons + ")"; //
					} else { //����л���,�����ߺϲ�!!
						if (al_corpids.size() >= li_allCorpCount) { //��������л���!!
							str_sqlItem = "('ȫ������'='ȫ������' and (" + str_sqlcons + "))"; //
						} else {
							if (isAddfieldName) {//̫ƽϵͳ����ƽ̨����Ҫ��Ӵ�LiGuoli�Ĵ��롣by haoming 2016-04-14
								str_sqlItem = "(" + getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) al_corpids.toArray(new String[0]), str_billCorpField) + " and (" + str_sqlcons + "))"; //
							} else {
								str_sqlItem = "(" + str_billCorpField + " in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) al_corpids.toArray(new String[0])) + ") and (" + str_sqlcons + "))"; //
							}
						}
					}
					al_realsqls.add(str_sqlItem); //����ʵ��!
					sb_returnInfo.append("����SQL:��[" + str_indexNo + "]�����Ե����������SQL��[(" + str_sqlItem + ")]\r\n"); //
				}
			} //end for_3

			//����ж������������Ϊ����SQLΪ��,�Ӷ���Ҫ����Ψһ�Ժϲ�������������,�����Ψһ�Ժϲ�����!!
			if (hst_distinctCorpids.size() > 0) { //�����ֵ,��˵����Ҫ����!
				String str_sqlItem = null; //
				if (hst_distinctCorpids.size() >= li_allCorpCount) { //����ϲ���������ȫ������,��
					str_sqlItem = "'ȫ������'='ȫ������'"; //
				} else {
					if (isAddfieldName) { //̫ƽϵͳ����ƽ̨����Ҫ��Ӵ�LiGuoli�Ĵ��롣by haoming 2016-04-14
						str_sqlItem = getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) hst_distinctCorpids.toArray(new String[0]), str_billCorpField); //Ψһ�Թ���!!!
					} else {
						str_sqlItem = str_billCorpField + " in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) hst_distinctCorpids.toArray(new String[0])) + ")"; //Ψһ�Թ���!!!
					}
				}
				al_realsqls.add(str_sqlItem); //
				sb_returnInfo.append("����SQL:Ψһ�Ժϲ�[" + str_spansIndex + "]�����Լ������SQL��[" + str_sqlItem + "]\r\n"); //
			}

			//Ϊ���л�����������������·���ϵ����С������㡱����Ϊ�����ͻ���չʾʱ,���������˺�Ļ����������Ρ��ˣ�����ʱ��Ҫ��·��������������,ֻ����Ҫ��һЩ�������㡱����
			if (hst_allCorpids.size() > 0) { //����л���
				str_virtualCorpSql = getVirtualCorpids(hst_allCorpids, allCorpMap, str_billCorpField); //
				sb_returnInfo.append("����SQL:����������,����SQL��[" + str_virtualCorpSql + "]\r\n"); //
				StringBuilder sb_allCorpIdAppend = new StringBuilder(); //
				Iterator it_allcorps = hst_allCorpids.iterator(); //
				while (it_allcorps.hasNext()) {
					sb_allCorpIdAppend.append((String) it_allcorps.next() + ";"); //
				}
				str_allCorpIds = sb_allCorpIdAppend.toString(); //
				if (hst_distinctCorpids.size() >= li_allCorpCount) {
					str_isAllCorps = "Y"; //
				}
			}

			//����������ص�SQL,���ǽ��������Լ��������SQLʹ��OR��ϵ,��ƴ��!!! 
			StringBuilder sb_realsql = new StringBuilder();
			for (int i = 0; i < al_realsqls.size(); i++) { //
				sb_realsql.append((String) al_realsqls.get(i)); ////
				if (i != al_realsqls.size() - 1) { //����������һ��,�����or
					sb_realsql.append(" or "); //
				}
			}
			str_realsql = sb_realsql.toString(); //
		} else if (str_billFilterType.equals("��Ա����")) { //����ǡ���Ա���ˡ����ͣ���������
			HashSet hst_corpids = new HashSet(); //
			ArrayList al_realsqls = new ArrayList(); //
			String str_spansIndex = ""; //
			HashMap allRoleCodeIdMap = null; //
			for (int i = 0; i < al_corp_role_sql.size(); i++) { //for_4,ѭ������!!
				Object[] rowObjs = (Object[]) al_corp_role_sql.get(i); //
				ArrayList al_corpids = (ArrayList) rowObjs[0]; //
				String str_roles = (String) rowObjs[1]; //���н�ɫ����!!
				String str_sqlcons = (String) rowObjs[2]; //SQL����
				String str_indexNo = (String) rowObjs[3]; //������
				if ((str_roles == null || str_roles.trim().equals("")) && (str_sqlcons == null || str_sqlcons.trim().equals(""))) { //�����ɫΪ��,SQL����ҲΪ��,��������Ψһ�Ժϲ�
					if (al_corpids != null) {
						hst_corpids.addAll(al_corpids); //Ψһ�Լ���!!
						str_spansIndex = str_spansIndex + str_indexNo + ","; //
					}
				} else { //����н�ɫ��SQL����!�����And����!!
					String str_sqlItem = " "; //
					String str_tmpinfo = ""; //
					if (al_corpids == null || al_corpids.size() >= li_allCorpCount) { //�������Ϊ��,���߾���ȫ������,��ʹ���Ӳ�ѯ��,����SQL�ᳬ��!!
						str_sqlItem = str_sqlItem + "'������Ա'='������Ա' "; //
						if (str_roles != null && !str_roles.trim().equals("")) { //����н�ɫ!!
							if (allRoleCodeIdMap == null) {
								allRoleCodeIdMap = getCommDMO().getHashMapBySQLByDS(null, "select code,id from pub_role"); //�ҳ����н�ɫ!!!
							}
							String[] str_roleids = getRoleIdsByCodeInfo(allRoleCodeIdMap, str_roles); //���ݽ�ɫ���붨�巴��id���� 
							str_sqlItem = str_sqlItem + "and " + str_billUserField + " in (select userid from pub_user_role where roleid in (" + getTBUtil().getInCondition(str_roleids) + ")) "; //ʹ���Ӳ�ѯ��!
							str_tmpinfo = ",��Ϊ�����л�����Χ����ʹ���Ӳ�ѯ"; //
						}
						str_sqlcons = convertSqlCons(str_sqlcons, confMap); //
						if (str_sqlcons != null && !str_sqlcons.trim().equals("")) { //���SQL����!
							str_sqlItem = str_sqlItem + "and (" + str_sqlcons + ") "; // 
						}
					} else { //����������Ļ���,���Ȳ�ѯ�������е�������Ա,Ȼ���ٲ�ѯ��Ա,��Щ���ܻ����Щ!��ΪSQL��,��û���Ӳ�ѯ��!�������Ժ�Ҳ�����Ӳ�ѯ!����Ϊһ�����ܼ������оͼ��ٺ���,����Ӧ�ò���������!!!
						String str_corpinCondition = getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) al_corpids.toArray(new String[0])); //ƴ�ӻ�����id
						if (str_roles != null && !str_roles.trim().equals("")) { //����н�ɫ!!��ʹ�ý�ɫ���Ӳ�ѯ!!һ��SQL�ҳ�����������������Ա!!
							if (allRoleCodeIdMap == null) {
								allRoleCodeIdMap = getCommDMO().getHashMapBySQLByDS(null, "select code,id from pub_role"); //
							}
							String[] str_roleids = getRoleIdsByCodeInfo(allRoleCodeIdMap, str_roles); //���ݽ�ɫ���붨�巴��id���� 
							String[] str_userids = getCommDMO().getStringArrayFirstColByDS(null, "select t1.userid from pub_user_post t1,pub_user_role t2 where t1.userid=t2.userid and t1.userdept in (" + str_corpinCondition + ") and t2.roleid in (" + getTBUtil().getInCondition(str_roleids) + ")"); //
							str_sqlItem = str_sqlItem + str_billUserField + " in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_userids) + ") "; //������ֻ����������Ա!
							str_tmpinfo = ",Ԥ�Ƚ��л�������Ա��ɫ������ѯ"; //
						} else { //���û�н�ɫ,��ֱ��ʹ�û�����ѯ!!!
							String[] str_userids = getCommDMO().getStringArrayFirstColByDS(null, "select userid from pub_user_post where userdept in (" + str_corpinCondition + ")"); //�ҳ���Щ������id
							str_sqlItem = str_sqlItem + str_billUserField + " in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_userids) + ") "; //������ֻ����������Ա!
							str_tmpinfo = ",Ԥ�Ƚ��л�������Ա������ѯ"; //
						}
						str_sqlcons = convertSqlCons(str_sqlcons, confMap); //
						if (str_sqlcons != null && !str_sqlcons.trim().equals("")) { //���SQL����,��ֱ�Ӽ���!!
							str_sqlItem = str_sqlItem + "and (" + str_sqlcons + ") "; // 
						}
					}
					al_realsqls.add("(" + str_sqlItem + ")"); //
					sb_returnInfo.append("����SQL:��[" + str_indexNo + "]�����Ե����������SQL��[(" + str_sqlItem + ")]" + str_tmpinfo + "\r\n"); //
				}
			} //end for_4
			//����ж������������Ϊ����SQLΪ��,�Ӷ���Ҫ����Ψһ�Ժϲ�������������,�����Ψһ�Ժϲ�����!!
			if (hst_corpids.size() > 0) { //�����Ψһ�Թ���
				String str_pp = null; //
				String str_tmpinfo = ""; //
				if (hst_corpids.size() >= li_allCorpCount) { //����ϲ���������ȫ������,��
					str_pp = "'ȫ����Ա'='ȫ����Ա'"; //
				} else { //����ͨ��SQL��ѯ!
					String str_corpinCondition = getCommDMO().getSubSQLFromTempSQLTableByIDs((String[]) hst_corpids.toArray(new String[0])); //
					String[] str_userids = getCommDMO().getStringArrayFirstColByDS(null, "select userid from pub_user_post where userdept in (" + str_corpinCondition + ")"); //�ҳ���Щ������id/
					str_pp = str_billUserField + " in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_userids) + ")"; //
					str_tmpinfo = ",Ԥ�Ƚ��л�������Ա������ѯ"; //
				}
				al_realsqls.add(str_pp); //
				sb_returnInfo.append("����SQL:Ψһ�Ժϲ�[" + str_spansIndex + "]�����Լ������SQL��[" + str_pp + "]" + str_tmpinfo + "\r\n"); //
			}

			//Ϊ���л�����������������·���ϵ����С������㡱����Ϊ�����ͻ���չʾʱ,���������˺�Ļ����������Ρ��ˣ�����ʱ��Ҫ��·��������������,ֻ����Ҫ��һЩ�������㡱����

			//����������ص�SQL,���ǽ��������Լ��������SQLʹ��OR��ϵ,��ƴ��!!! 
			StringBuilder sb_realsql = new StringBuilder(); // 
			for (int i = 0; i < al_realsqls.size(); i++) { //
				sb_realsql.append((String) al_realsqls.get(i)); ////
				if (i != al_realsqls.size() - 1) { //����������һ��,�����or
					sb_realsql.append(" or "); //
				}
			}
			str_realsql = sb_realsql.toString(); //
		} //�����"��Ա����"�жϽ���!!

		if (str_realsql.trim().equals("")) {
			str_realsql = "'��Ȼ���ؿմ�'='Y'"; //
		}
		return new String[] { sb_returnInfo.toString(), str_realsql, str_virtualCorpSql, str_allCorpIds, str_isAllCorps }; //
	}

	/**
	 * ���ǹ�������ʹ�õķ���!��Ϊ����Ժ�����,���Է���һ������!!
	 * ���ݹ�ʽ����������̽������Ȩ�޹���,��������ʹ�õ�,�������淽ʽ����������,�����Ǵ�Ȩ�޲��Ա��й���,������������,�������к����״������������̽,���������ֱ��ȥ��������̽,�����ǹ�ʽ!!
	 * �ǳ���Ҫ�ķ���,������Խ�׳���ȶ�!! �Ժ��Ժ���Ҫ�Ϳ���һ��������! �÷��������淽����Щ���ֿ����ٺϲ�!! ��֮����������һ��Ҫ��׳���ȶ�!!
	 * ȡ��ĳһ����Ա��ĳ�ֻ������͵��ϼ�����!!!
	 * ���Ǹ��ݵ�¼��Ա���Ǵ����˶������յ��ø÷���!!
	 * ����ȡ������Ҳ��ʹ�ñ�����!! �취����ȡ�ø������ڵĻ����Ļ�������,Ȼ�������������Ϊ�������뱾����!! 
	 * ����֮ǰ,������ָ��˵����ڻ�������"��������"��β��,���Զ��ص�! ��������Ը�ǿ!! �����뽫�������͵�����֮���ĳ��Լ��ֱ����Ϊһ�����ö������书��!!
	 * ���Һ���������ֱ�Ӹ��ݲ�������������!!! ������ʡȥ������չ���͵��鷳! ��Ϊ����!!! 
	 * @param _userId
	 * @param _up1RootCorpType
	 * @param _down2CorpType
	 * @param _down2ExtCorpType
	 * @param _isDdown2ContainChildren
	 * @return
	 * @throws Exception
	 */
	public String[] getOnerUserSomeTypeParentCorpID(String _userId, String _up1RootCorpType, String _down2CorpType, String _down2ExtCorpType, String _down2CorpName, boolean _isDdown2ContainChildren, HashMap _cacheMap) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] hvs_wfcreaterCorps = commDMO.getHashVoArrayByDS(null, "select t1.userdept,t2.corptype,t1.isdefault from pub_user_post t1,pub_corp_dept t2 where t1.userdept=t2.id and t1.userid='" + _userId + "'"); //������Ĭ�ϻ���!!
		if (hvs_wfcreaterCorps == null || hvs_wfcreaterCorps.length == 0) { //��������ߵĻ���Ϊ��,��ֱ�ӷ���!!
			getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "�����ݵ�¼��/������[" + _userId + "]Ѱ�������ڻ���ʱ,����û�л���,���Է��ؿ�!<br>"); //
			return null;
		}
		if (_up1RootCorpType == null || _up1RootCorpType.trim().equals("")) { //��������ߵĻ���Ϊ��,��ֱ�ӷ���!!
			getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "��û��ָ���״����ݵĻ�������,���Է��ؿ�!<br>"); //
			return null;
		}

		HashVO hvs_wfcreaterCorp = null; //
		for (int i = 0; i < hvs_wfcreaterCorps.length; i++) { //
			if ("Y".equals(hvs_wfcreaterCorps[i].getStringValue("isdefault"))) {
				hvs_wfcreaterCorp = hvs_wfcreaterCorps[i]; //
				break; //
			}
		}
		if (hvs_wfcreaterCorp == null) {
			hvs_wfcreaterCorp = hvs_wfcreaterCorps[0]; //
		}

		String str_createrCorpId = hvs_wfcreaterCorp.getStringValue("userdept"); //����Ա���ڵĻ���
		String str_createrCorpType = hvs_wfcreaterCorp.getStringValue("corptype"); //�������ڻ����Ļ�������
		if (str_createrCorpType.endsWith("��������")) { //�ص����4����!!��Ϊʵ��������������������϶�������丸��·����
			str_createrCorpType = str_createrCorpType.substring(0, str_createrCorpType.length() - 4); //ȥ������ĸ���!
		}
		String str_realCorpType = null; //ʵ�ʻ�������,���������������ҳ�ʵ�����ϷõĻ�������!!
		String[] str_parCorpTypes = tbUtil.split(_up1RootCorpType, ";"); //�״��ϷõĻ������ͽ��зָ�
		if (str_parCorpTypes.length == 1) {
			if (str_parCorpTypes[0].indexOf("=>") > 0) { //�����=>
				String str_corpTypecontition = str_parCorpTypes[0].substring(0, str_parCorpTypes[0].indexOf("=>")); //����
				if (str_corpTypecontition.equalsIgnoreCase("*") || str_corpTypecontition.equals(str_createrCorpType)) {
					str_realCorpType = str_parCorpTypes[0].substring(str_parCorpTypes[0].indexOf("=>") + 2, str_parCorpTypes[0].length()); //ȡ�����
				}
			} else { //���û������!
				str_realCorpType = str_parCorpTypes[0]; //���ֻ��һ��,���ʾֱ�Ӷ����!! ��������*��,���*������治Ҫ������!
			}
		} else { //����ж��,���ʾ����������,����=>
			for (int i = 0; i < str_parCorpTypes.length; i++) { //����!!!
				int li_pos = str_parCorpTypes[i].indexOf("=>"); //
				String str_key = str_parCorpTypes[i].substring(0, li_pos); //
				String str_value = str_parCorpTypes[i].substring(li_pos + 2, str_parCorpTypes[i].length()); //
				String[] str_key_items = tbUtil.split(str_key, "/"); //
				boolean isfind = false; //
				for (int j = 0; j < str_key_items.length; j++) {
					if (str_key_items[j].equals(str_createrCorpType)) {
						str_realCorpType = str_value; //
						isfind = true;
						break;
					}
				}
				if (isfind) { //���������,��ֱ���˳�,���������һ��=>����!!!
					break;
				}
			}
		}

		//����Ҫ����һ������һ��֧��$������,$�����ŵĶ�̬����!!!
		if (str_realCorpType == null || str_realCorpType.equals("*")) { //���һ��ûƷ����,ֱ���ô����ߵĻ�������!�����û����,��Ĭ���ұ��˵Ļ���!!
			str_realCorpType = str_createrCorpType; //
			getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "������������[" + _up1RootCorpType + "]���м���(һ��Ҫע��������Ƿ���ȷ,���������Ϊtype���Ե��¼��㲻��),�ҵ����㱾�������Ļ�������=[" + str_realCorpType + "](����ûƷ����,��ֱ�����Լ�������)<br>"); //
		} else {
			getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "������������[" + _up1RootCorpType + "]���м���(һ��Ҫע��������Ƿ���ȷ,���������Ϊtype���Ե��¼��㲻��),�ҵ����㱾�������Ļ�������=[" + str_realCorpType + "]<br>"); //
		}

		//�������л���,�Ժ����Ҫ�û���!
		//���ڵĻ�������������,�Ժ�����ط�Ҫ�Ż�,Ӧ�����ҳ����˻�����blparentcorpids,Ȼ��ƴ��where id in (blparentcorpids),Ȼ��ѭ���ҳ���Щ������corptype����ƥ��Ļ�������!
		HashVO[] hvs_allCorps = null;//
		HashMap allCorpsMap = null; //
		if (_cacheMap != null && _cacheMap.containsKey("AllCorpHashVOs") && _cacheMap.containsKey("AllCorpHashMap")) {
			hvs_allCorps = (HashVO[]) _cacheMap.get("AllCorpHashVOs"); //
			allCorpsMap = (HashMap) _cacheMap.get("AllCorpHashMap"); //
		} else {
			hvs_allCorps = commDMO.getHashVoArrayAsTreeStructByDS(null, "select id,name,parentid,blparentcorpids,corptype,extcorptype,seq from pub_corp_dept", "id", "parentid", "seq", null); //�ҳ����л���,���ҷ������ͽṹ!!!
			allCorpsMap = new HashMap(); //�������ù�ϣ�������,��Ϊ����Ҫ�������!!
			for (int i = 0; i < hvs_allCorps.length; i++) {
				allCorpsMap.put(hvs_allCorps[i].getStringValue("id"), hvs_allCorps[i]); //��ע��һ��!Ϊ���������id�һ���ʱҪ�ǳ���!!
			}
			_cacheMap = new HashMap(); //
			_cacheMap.put("AllCorpHashVOs", hvs_allCorps); //���뻺��!
			_cacheMap.put("AllCorpHashMap", allCorpsMap); //���뻺��!
		}

		HashVO hvo_creatercorp = (HashVO) allCorpsMap.get(str_createrCorpId); //
		if (hvo_creatercorp == null) { //����������ڻ�����������,��ֱ�ӷ���
			return null;
		}
		String str_parentPathids = hvo_creatercorp.getStringValue("$parentpathids"); //�������˵����и���·��!!!
		if (str_parentPathids == null || str_parentPathids.trim().equals("")) { //���û�ҵ�,����·��Ϊ��,ֱ�ӷ���!!!
			return null;
		}

		//������׷���ҵ��ҵ��Ǹ�����
		String str_matchedParentCorpId = null; //
		String str_matchedParentCorpName = null; //
		String str_matchedParentCorpBlparentcorpids = null; //
		String[] str_partentIds = tbUtil.split(str_parentPathids, ";"); //
		for (int i = 0; i < str_partentIds.length; i++) { //�����ҵ����и���,��������,�һ���һ�����͵���ָ�������͵Ļ���
			HashVO hvo_item = (HashVO) allCorpsMap.get(str_partentIds[i]); //
			if (hvo_item.getStringValue("corptype", "").equals(str_realCorpType)) { //���������׵����͵���ָ������
				str_matchedParentCorpId = hvo_item.getStringValue("id"); //�ҵ�Ʒ��ĸ��׻���!!!
				break; ////
			}
		}

		if (str_matchedParentCorpId != null) {
			str_matchedParentCorpName = ((HashVO) allCorpsMap.get(str_matchedParentCorpId)).getStringValue("name"); //��������!
			str_matchedParentCorpBlparentcorpids = ((HashVO) allCorpsMap.get(str_matchedParentCorpId)).getStringValue("blparentcorpids"); //��������!
		}
		return new String[] { str_matchedParentCorpId, str_matchedParentCorpName, str_matchedParentCorpBlparentcorpids, str_realCorpType }; //
	}

	/**
	 * ������̽ȡ����,ȡ��һ�������������¼�����,��Ȼ�Ǹ���ָ��������(�ӻ�������/��չ���ͻ�������)!! ��������̽!!!
	 * @param _down2CorpType
	 * @param _down2ExtCorpType
	 * @param _down2CorpsName ֱ�Ӹ�������ģ��ƥ��,��δʵ��,�Ժ����Ҫʵ��! ����ֱ���ҽ�[�칫��/���ղ�]��,�����Ͳ���Ҫ������չ������,��ֱ��!!
	 * @param _isDdown2ContainChildren �Ƿ�����ӽ��
	 * @return
	 */
	public ArrayList secondDownFindAllCorpChildrensByCondition(String _rootCorpId, String _rootCorpType, String _down2CorpType, String _down2ExtCorpType, String _down2CorpsName, boolean _isContainRootBeforeDown2, boolean _isDdown2ContainChildren, String str_uniontype, HashMap _cacheMap) throws Exception {
		ArrayList al_return = new ArrayList(); //
		HashVO[] hvs_allCorps = null; //
		if (_cacheMap != null && _cacheMap.containsKey("AllCorpHashVOs")) { //�������Ϊ��,���߻���û��
			hvs_allCorps = (HashVO[]) _cacheMap.get("AllCorpHashVOs"); //
		} else {
			hvs_allCorps = new CommDMO().getHashVoArrayAsTreeStructByDS(null, "select id,name,parentid,corptype,extcorptype,seq from pub_corp_dept", "id", "parentid", "seq", null);
		}

		boolean isHaveCorpType = (_down2CorpType != null && !_down2CorpType.trim().equals("")) ? true : false; //�Ƿ����˶�����֮̽��������?
		boolean isHaveExtType = (_down2ExtCorpType != null && !_down2ExtCorpType.equals("")) ? true : false; //�Ƿ����˶�����֮̽��չ����?
		boolean isHaveCorpName = (_down2CorpsName != null && !_down2CorpsName.equals("")) ? true : false; //�Ƿ����˶�����֮̽��չ����?

		//������̽����!!
		//���û�ж���2����̽�Ļ�����������չ����,��ֱ��ȫ������!!����һ����һ�����еĹ���Ա������ֱ�ӿ�����������!��������Ҫ�ٽ��ж�����̽!!
		if (!isHaveCorpType && !isHaveExtType && !isHaveCorpName) { //���ʲô��û����
			if (_isDdown2ContainChildren) { //����ǰ��������ӽ��!
				al_return.add(_rootCorpId); //�ȼ�������!��ǰû���,����������,���û�ж�����̽ʱ,Ӧ�����Զ�����������!!
				for (int i = 0; i < hvs_allCorps.length; i++) { //�������л���,���ʹ��linkcode��ֱ�Ӳ������ݿ�!!�Ƿ�Ч�ʸ���?��������������,��ʹ����3000������Ҳ�Ƿǳ����!
					if (hvs_allCorps[i].getStringValue("$parentpathids", "").indexOf(";" + _rootCorpId + ";") >= 0) { //���������¼�ĸ��װ�����!!��˵�����ҵ�����!
						al_return.add(hvs_allCorps[i].getStringValue("id")); //���뷵���嵥!!
					}
				}
				getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "�����ж�����̽,������̽�Ļ�����������չ������Ϊ��,����Ҫ�г���������,���Թ��ҵ�[" + al_return.size() + "]���ӻ���<br>"); //
			} else { //��������ӽṹ!!!��ֻ�����Լ�!
				al_return.add(_rootCorpId); //ֻ�����Լ�!!!
				getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "�����ж�����̽,������̽�Ļ�����������չ������Ϊ��,�Ҳ���Ҫ�г���������,����ֱ���г�����[" + _rootCorpId + "]<br>"); //
			}
		} else { //�����2����̽�Ļ���������2����̽����չ����!���ҳ���Щ������,��Ҫ��һ���ҳ���Щ�����������ӽṹ!!!
			HashSet<String> hst_corptype = new HashSet<String>(); //��¼��������������������Ļ���!!!
			HashSet<String> hst_exttype = new HashSet<String>(); //��¼����������չ���������Ļ���!!!
			HashSet<String> hst_corpname = new HashSet<String>(); //��¼����������չ���������Ļ���!!!
			int li_findCount_1 = 0, li_findCount_2 = 0, li_findCount_3 = 0;
			//�������л���!!!һ������!
			for (int i = 0; i < hvs_allCorps.length; i++) { //����699
				if (hvs_allCorps[i].getStringValue("$parentpathids", "").indexOf(";" + _rootCorpId + ";") < 0) { //���������¼�����ҵ�����,���������!!
					continue; //
				}

				//�������������ҵ�����,��������˶�����̽�Ļ�������!!!�����û����Ƿ���������?
				if (isHaveCorpType) {
					String str_thisCorpType = hvs_allCorps[i].getStringValue("corptype"); //�����α����֮��������!!!
					if (str_thisCorpType != null && !str_thisCorpType.equals("")) {
						boolean isThisMatched = ifFormulaMatchThisItemByCondition(_down2CorpType, str_thisCorpType, _rootCorpType); //�ؼ��߼�!
						if (isThisMatched) {
							hst_corptype.add(hvs_allCorps[i].getStringValue("id")); //
							li_findCount_1++; //
						}
					}
				}

				//��������˶�����̽����չ��������!!!�����û����Ƿ���������?
				if (isHaveExtType) {
					String str_thisExtCorpType = hvs_allCorps[i].getStringValue("extcorptype"); //
					if (str_thisExtCorpType != null && !str_thisExtCorpType.equals("")) { //���벻Ϊ��!
						boolean isThisMatched = ifFormulaMatchThisItemByCondition(_down2ExtCorpType, str_thisExtCorpType, _rootCorpType); //�ؼ��߼�!
						if (isThisMatched) { //���ֱ�����,��ֱ�Ӽ���!!��Ϊ�󲿷����������,���Ի��������!!!
							hst_exttype.add(hvs_allCorps[i].getStringValue("id")); //
							li_findCount_2++; //
						}
					}
				}

				if (isHaveCorpName) { //
					String str_thisCorpName = hvs_allCorps[i].getStringValue("name"); //��������!
					if (str_thisCorpName != null && !str_thisCorpName.equals("")) { //���벻Ϊ��!
						if (matchTwoCorpName(_down2CorpsName, str_thisCorpName)) { //�����������Ƿ�ƥ��
							hst_corpname.add(hvs_allCorps[i].getStringValue("id")); //
							li_findCount_3++; //
						}
					}
				}

			} //����699����!!

			//���н���/��������!������{��������}��{��չ����}�ҳ����Ľ�������߿��Խ��н�/��������!!�����벢�����������󶼿��ܴ���!�������ҳ�������"һ�����в���"������չ������"���ղ�"��!��֮,����Ҳ�е���!
			//�����ǽ������ǲ���,��ʵ������Ψһ�Թ���!!������Ϊʹ����HashSet����!!
			HashSet<String> hst_two_type = new HashSet<String>(); //�������ͽ��кϲ�!
			//boolean isIntersection = true; //�Ƿ񽻼�? ��������Ժ�ҲҪ�����ǲ��������õ�!!!
			if (isHaveCorpType && (isHaveExtType || isHaveCorpName)) { //��������������ж���,����н����ϲ�!
				if ("����".equals(str_uniontype)) { //����ǽ���
					String[] str_allKeys = (String[]) hst_corptype.toArray(new String[0]); //�����ǻ�������!
					for (int i = 0; i < str_allKeys.length; i++) { //�������и��ݻ��������ҵ���!
						//�����չ���ͻ��������ƥ����,�����!
						boolean issuccessExtType = true;
						if (isHaveExtType) { //���������
							issuccessExtType = hst_exttype.contains(str_allKeys[i]); //
						}
						boolean issuccessCorpName = true;
						if (isHaveCorpName) { //
							issuccessCorpName = hst_corpname.contains(str_allKeys[i]); //
						}
						if (issuccessExtType && issuccessCorpName) { //��������������!!
							hst_two_type.add(str_allKeys[i]);
						}
					}
				} else { //����ǲ���!!!
					hst_two_type.addAll(hst_corptype); //���ϻ������͵�
					hst_two_type.addAll(hst_exttype); //������չ���͵�!
					hst_two_type.addAll(hst_corpname); //���ϻ��������ҵ���
				}
			} else { //���ֻ��һ������,���Զ������ߺϲ�,�����������hst_corptype��hst_exttypeֻ��һ����ֵ,��һ��Ϊ��!��ֱ�ӽ����ߺϲ�,��ô��û��!
				hst_two_type.addAll(hst_corptype); //���ϻ������͵�
				hst_two_type.addAll(hst_exttype); //������չ���͵�!
				hst_two_type.addAll(hst_corpname); //���ϻ��������ҵ���
			}

			StringBuilder sb_infoMsg = new StringBuilder(); //
			sb_infoMsg.append("�����ж�����̽,�ڸ�����[" + _rootCorpId + "]��,");
			sb_infoMsg.append("��̽��������Ϊ[" + (_down2CorpType == null ? "" : _down2CorpType) + "]�Ĺ��ҵ�[" + li_findCount_1 + "]��,"); //
			sb_infoMsg.append("��̽��չ����Ϊ[" + (_down2ExtCorpType == null ? "" : _down2ExtCorpType) + "]�Ĺ��ҵ�[" + li_findCount_2 + "]��,"); //
			sb_infoMsg.append("��̽��������Ϊ[" + (_down2CorpsName == null ? "" : _down2CorpsName) + "]�Ĺ��ҵ�[" + li_findCount_3 + "]��,"); //
			sb_infoMsg.append("���н�/������Ψһ�Լ����,����[" + hst_two_type.size() + "]��!��Щ������Id��:["); //
			String[] str_allCorpIds = (String[]) hst_two_type.toArray(new String[0]); //
			for (int i = 0; i < str_allCorpIds.length; i++) {
				sb_infoMsg.append(str_allCorpIds[i]);
				if (i != str_allCorpIds.length - 1) {
					sb_infoMsg.append(","); //
				}
			}
			sb_infoMsg.append("]<br>"); //
			getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", sb_infoMsg.toString()); /////

			//��ζ���Ҫ��Ҫ�ҳ���Щ������������������,��Ҫ�ٱ���һ��!
			if (_isDdown2ContainChildren) { //���������̽ʱ��Ҫ�Զ����������ӽ��,����Ҫ�ٱ���һ��
				for (int i = 0; i < hvs_allCorps.length; i++) { //��������
					String str_parentids = hvs_allCorps[i].getStringValue("$parentpathids"); //
					if (str_parentids != null && !str_parentids.trim().equals("")) {
						String[] str_ids = getTBUtil().split(str_parentids, ";"); //
						for (int j = 0; j < str_ids.length; j++) { //
							if (hst_two_type.contains(str_ids[j])) { //������������ĳ�����������������Ļ����嵥��,��˵�����ҵ��������������,��Ҫ����!
								al_return.add(hvs_allCorps[i].getStringValue("id")); //
							}
						}
					}
				}

				getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "��������̽��,��Ҫͬʱ�г�������������������������,����[" + al_return.size() + "]��!<br>"); //
			} else { //����ж�����̽,���ҵ���һ��,���ҵ�����Ҫ�ټ��������ӽ��!!��ֱ���г�����!!
				al_return.addAll(Arrays.asList(hst_two_type.toArray(new String[0])));
			}

			//���ָ����̽ʱ�Զ����������,������Լ�!!!
			if (_isContainRootBeforeDown2) {
				al_return.add(0, _rootCorpId);
			}
		}
		return al_return; //
	}

	//���㶯̬�������͵��߼�!!
	private String[] getDynamicCorpType(String _dynamicCorpType, HashMap _allCorpTypeMap, String _myCorpType) throws Exception {
		if (_dynamicCorpType.equals("$���˾���ʵ������")) { //��������Ĵ���,ֱ�ӷ����Լ�������!!
			return new String[] { _myCorpType, "����ָ����,����ת��" }; //
		}
		String str_realcorptype = _dynamicCorpType.trim(); //ʵ�ʻ�������,ȥ����һ��$��,�ӵ�һλ�����ȡ!!!
		if (_allCorpTypeMap == null) { //���ҳ����л����ķ��ඨ��,���б����н�����!���ڱ�����ָ���˶�ĳ�˸����͵Ļ���������˵,��ĳ����̬���͵ļ��㷽ʽ��ʲô??
			_allCorpTypeMap = getCommDMO().getHashMapBySQLByDS(null, "select id,code from pub_comboboxdict where type in ('��������','��������') order by seq"); //  
		}
		String str_dyncorptype_mapping = (String) _allCorpTypeMap.get(_myCorpType); //�����ҵ�ʵ�ʻ����������ҳ���ӳ��!
		if (str_dyncorptype_mapping != null && !str_dyncorptype_mapping.trim().equals("")) { //����ҵ�,���ҵ�ȷ��ӳ��,���������һ��HashMap!
			HashMap mappingMap = getTBUtil().convertStrToMapByExpress(str_dyncorptype_mapping, ";", "="); ////
			if (mappingMap.containsKey(str_realcorptype)) { //�������İ���
				return new String[] { (String) mappingMap.get(str_realcorptype), str_dyncorptype_mapping }; //��ֵ!!
			}
		}
		return new String[] { null, str_dyncorptype_mapping };
	}

	/**
	 * Ѱ��һ��������������������������!!
	 * @param allCorpMap 
	 * @param _allCorps 
	 * @param _ids
	 * @return
	 */
	private String getVirtualCorpids(HashSet _hstCorpIds, HashMap _allCorpMap, String _corpFieldName) {
		Iterator items = _hstCorpIds.iterator(); //
		HashVO hvo = null; //
		String str_parentIds = null; //
		String[] str_parentIdItems = null; //
		HashSet hst_virtualCorp = new HashSet(); //�洢���������������!!
		while (items.hasNext()) { //�������л���!!
			String str_corpId = (String) items.next(); //��ǰ����!
			hvo = (HashVO) _allCorpMap.get(str_corpId); //
			str_parentIds = hvo.getStringValue("$parentpathids"); //
			if (str_parentIds != null && !str_parentIds.trim().equals("")) { //�����ֵ
				str_parentIdItems = getTBUtil().split(str_parentIds, ";"); //�ָ�!!
				for (int i = 0; i < str_parentIdItems.length; i++) { //����!!
					if (!_hstCorpIds.contains(str_parentIdItems[i])) { //���ԭ���б��в������������id
						hst_virtualCorp.add(str_parentIdItems[i]); //
					}
				}
			}
		}

		if (hst_virtualCorp.size() <= 0) { //��������ҵ�һ��������,�򷵻�1=2
			return null;
		} else {
			StringBuilder sb_sql = new StringBuilder(); //
			Iterator itVirtualCorps = hst_virtualCorp.iterator(); //
			while (itVirtualCorps.hasNext()) { //�������!!
				sb_sql.append((String) itVirtualCorps.next()); //
				if (itVirtualCorps.hasNext()) { //����������һ��,�����滹��
					sb_sql.append(","); //
				}
			}
			return " " + _corpFieldName + " in (" + sb_sql.toString() + ") "; //
		}
	}

	//���ݽ�ɫ��������,����id����!!!
	private String[] getRoleIdsByCodeInfo(HashMap _allRoleCodeIdMap, String _rolecodes) {
		String[] str_items = _rolecodes.split(";"); //
		String str_id = null; //
		ArrayList al_ids = new ArrayList(); //
		for (int i = 0; i < str_items.length; i++) {
			str_id = (String) _allRoleCodeIdMap.get(str_items[i]); //
			if (str_id != null) {
				al_ids.add(str_id); //
			}
		}
		return (String[]) al_ids.toArray(new String[0]); //
	}

	/**
	 * ��ΪSQL��֧�ֹ�ʽ,����Ҫ����!
	 * @param str_sqlcons
	 * @return
	 */
	private String convertSqlCons(String str_sqlcons, HashMap confMap) {
		String[] keys = (String[]) confMap.keySet().toArray(new String[0]);
		for (int i = 0; i < keys.length; i++) {
			str_sqlcons = getTBUtil().replaceAll(str_sqlcons, "{" + keys[i] + "}", (String) confMap.get(keys[i])); //
		}
		str_sqlcons = getTBUtil().replaceAll(str_sqlcons, "{�����ֶ���}", "createcorp"); //confMap�п���û�����û����ֶ�������Ա�ֶ�����������Ҫ����һ��Ĭ��ֵ
		str_sqlcons = getTBUtil().replaceAll(str_sqlcons, "{��Ա�ֶ���}", "createuser"); //
		String str_value = (String) getJepParse().execFormula(str_sqlcons); //
		if (str_value != null) {
			return str_value;
		} else {
			return str_sqlcons;
		}
	}

	/**
	 * ƥ���������������Ƿ�һ�£�
	 * @param _conditionName
	 * @param _itemCorpName
	 * @return
	 */
	private boolean matchTwoCorpName(String _conditionName, String _itemCorpName) {
		String str_conditionName = _conditionName; //
		String str_itemCorpName = _itemCorpName; //
		str_conditionName = str_conditionName.trim(); //
		str_itemCorpName = str_itemCorpName.trim(); //

		//str_conditionName = trimEndStr(str_conditionName); //
		//str_itemCorpName = trimEndStr(str_itemCorpName); //

		if (str_conditionName.equals(str_itemCorpName)) { //���ֱ�������϶�100%����!
			return true; //
		}

		//�Ժ�����Ҫ�и����ܵ��ж�,����һ��ϵͳ����,�������չ������롾���ɺϹ沿���ȼ���һ������
		return false; //
	}

	private String trimEndStr(String _corpName) {
		String str_corpName = _corpName; //
		if (str_corpName.endsWith("����")) {
			str_corpName = str_corpName.substring(0, str_corpName.length() - 3); //
		}
		if (str_corpName.endsWith("��")) {
			str_corpName = str_corpName.substring(0, str_corpName.length() - 1); //
		}
		if (str_corpName.endsWith("��")) {
			str_corpName = str_corpName.substring(0, str_corpName.length() - 1); //
		}
		return str_corpName; //
	}

	/**
	 * ������̽ʱ�����Ƿ�ƥ�乫ʽ
	 * @param _formula
	 * @param _thisItem
	 * @param _conditionCorpType
	 * @return
	 */
	private boolean ifFormulaMatchThisItemByCondition(String _formula, String _thisItem, String _conditionCorpType) {
		if (_thisItem != null && _thisItem.equals(_formula)) { //���ֱ����Ⱦ�ֱ�ӷ���!!!
			return true;
		}

		if (_formula.indexOf("=>") > 0) { //���������!!!�ڹ������������ǹ�ʽ,��ʵ�Ƕ��!!
			String[] str_formulaItems = getTBUtil().split(_formula, ";"); //�ֺ����!!����ԭ����!!
			for (int i = 0; i < str_formulaItems.length; i++) { //������������!!
				int li_pos = str_formulaItems[i].indexOf("=>"); //
				if (li_pos > 0) {
					String str_prefix = str_formulaItems[i].substring(0, li_pos); //
					String str_subfix = str_formulaItems[i].substring(li_pos + 2, str_formulaItems[i].length()); //
					if (str_prefix.equals(_conditionCorpType)) { //�������������!!!���������������Ȼ��!!!
						String[] str_thisItems = getTBUtil().split(_thisItem, ";"); //�ҵ�����ֵ,�����ҵĻ�������,��չ���͵�!!
						String[] str_valueItems = getTBUtil().split(str_subfix, "/"); //
						return getTBUtil().containTwoArrayCompare(str_thisItems, str_valueItems); //���������һ��Ʒ����,�򷵻�!!!
					}
				}
			}
			return false;
		} else { //���û����!!!
			String[] str_thisItems = getTBUtil().split(_thisItem, ";"); //
			String[] str_formulaItems = getTBUtil().split(_formula, ";"); //�ֺ����!!����ԭ����!!
			return getTBUtil().containTwoArrayCompare(str_thisItems, str_formulaItems); //���������һ��Ʒ����,�򷵻�!!!
		}
	}

	private CommDMO getCommDMO() {
		if (commDMO != null) {
			return commDMO; //
		}
		commDMO = new CommDMO(); //
		return commDMO;
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil(); //
		return tbUtil;
	}

	private JepFormulaParseAtBS getJepParse() {
		if (jepParse != null) {
			return jepParse;
		}
		jepParse = new JepFormulaParseAtBS(); //
		return jepParse; //
	}

	private WLTInitContext getInitContext() {
		if (initContext != null) {
			return initContext;
		}
		initContext = new WLTInitContext(); //
		return initContext;
	}

}
