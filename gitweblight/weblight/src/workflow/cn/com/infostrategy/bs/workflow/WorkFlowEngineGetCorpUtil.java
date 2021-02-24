package cn.com.infostrategy.bs.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.DataPolicyDMO;
import cn.com.infostrategy.bs.sysapp.SysAppDMO;
import cn.com.infostrategy.to.common.HashVO;

/**
 * ��������Ĺ�ʽ,�Ƿǳ��ؼ��ļ���!!!
 * �Ժ󻹷ǳ�������չ!!
 * @author xch
 *
 */
public class WorkFlowEngineGetCorpUtil {

	private WLTInitContext initContext = null; //

	/**
	 * �õ����л���!!!!
	 * @param _maps
	 * @return
	 */
	public String[] getCorps(HashMap[] _maps, HashMap _cacheMap) {
		try {
			getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "������ʽ��ϸ�������:<br>"); //
			ArrayList al_temp = new ArrayList(); //
			for (int i = 0; i < _maps.length; i++) { //��������
				String str_type = (String) _maps[i].get("type"); //��ȡ����,����ز�����!!!
				if (str_type == null) {
					getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "�������typeΪ��,���ǲ������,��ע�⹫ʽ�����Ƿ���ȷ,���粻����\"getWFCorp(\"��ʼ��!<br>"); //
					break; //
				}
				if (str_type.equals("ĳ�����͵Ļ���")) { //�����޹�,ֱ�Ӹ��ݻ��������ж�!!!
					al_temp.addAll(getOneCorpTypeCorp(_maps[i], _cacheMap)); //
				} else if (str_type.equals("���̴��������ڻ���")) { //
					al_temp.addAll(getWFCreaterCorp(_maps[i], _cacheMap)); //
				} else if (str_type.equals("���̴��������ڻ����ķ�Χ")) { //
					al_temp.addAll(getWFCreaterCorpArea(_maps[i], _cacheMap)); //
				} else if (str_type.equals("���̴�����ĳ���͵��ϼ�����")) { //���̴����˵�ĳ���ϼ�����!!���е�ʱ���������̴���������������!!������
					al_temp.addAll(getWFCreaterSomeTypeParentCorp(_maps[i], _cacheMap)); //
				} else if (str_type.equals("��¼�����ڻ���") || str_type.equals("������") || str_type.equals("������֮����")) { //Ϊ�˼��ݾɵ�����,��ǰ��"������""������֮����"����Ȼ��Ч!!
					al_temp.addAll(getLoginUserCorp(_maps[i], _cacheMap)); //
				} else if (str_type.equals("��¼�����ڻ����ķ�Χ") || str_type.equals("��������Χ") || str_type.equals("��������Χ֮����")) { //Ϊ�˼��ݾɵ�����,��ǰ�� [��������Χ] [��������Χ֮����] ����Ȼ��Ч!!
					al_temp.addAll(getLoginUserCorpArea(_maps[i], _cacheMap)); //
				} else if (str_type.equals("��¼��ĳ���͵��ϼ�����") || str_type.equals("����ĳ���͵��ϼ�����")) { //���̴����˵�ĳ���ϼ�����!!���е�ʱ���������̴���������������!!
					al_temp.addAll(getLoginUserSomeTypeParentCorp(_maps[i], _cacheMap)); //
				} else if (str_type.equals("�Զ�����")) {
					al_temp.addAll(getCustClassCorp(_maps[i], _cacheMap)); //�Զ�����!!!
				} else { //������и��ر���ʾ,˵����������Ͳ���!!
					getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "�������type[" + str_type + "]����,���п�������Ϊ�汾����������ǰ��typeֵ��Ч��,ֻ�����������¼���!<br>"); //
					break; //
				}
			}

			//����Ψһ�Թ���,��Ϊ���ݲ�ͬ���ͼ��������ֵ,�϶������ظ���,����������̫��!!
			HashSet hst = new HashSet(); //
			for (int i = 0; i < al_temp.size(); i++) {
				hst.add(al_temp.get(i)); //
			}
			return (String[]) hst.toArray(new String[0]); //����!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	/**
	 * ȡ��ĳ�����͵Ļ���
	 * @return
	 */
	private ArrayList getOneCorpTypeCorp(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_corptype = (String) _parMap.get("����"); //
		String str_down2CorpType = (String) _parMap.get("������̽���ӻ�������"); //��������չ����!!
		String str_down2ExtCorpType = (String) _parMap.get("������̽���ӻ�����չ����"); //��������չ����!!
		String str_down2CorpName = (String) _parMap.get("������̽���ӻ�������"); //����������!!
		String str_isdown2ContainChildren = (String) _parMap.get("������̽�Ƿ��������"); ////
		boolean isDown2ContainChildren = true; //Ĭ���ǰ����ӽ���
		if ("N".equalsIgnoreCase(str_isdown2ContainChildren)) {
			isDown2ContainChildren = false; //
		}
		SysAppDMO sysDMO = new SysAppDMO(); //
		return sysDMO.getOneCorpTypeAllCorps(str_corptype, str_down2CorpType, str_down2ExtCorpType, str_down2CorpName, isDown2ContainChildren, _cacheMap); //
	}

	//ȡ�����̴��������ڻ���
	private ArrayList getWFCreaterCorp(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_wfcreater = (String) _parMap.get("���̴�����"); //���̴����˵�id!!
		String[] str_corpIdType = getCorpIdAndTypebyUerId(str_wfcreater); //
		if (str_corpIdType == null) {
			return new ArrayList(); //
		}
		String str_corpId = str_corpIdType[0]; //ȡ�ø���Ա�����ڻ�������
		String str_corpType = str_corpIdType[1]; //ȡ�ø���Ա�����ڻ�������
		getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "��ֱ�Ӵ����̴����˵����ڻ���[" + str_corpId + "]���ж�����̽!<br>"); //
		return getOnerCorpAllChildrenCorps(str_corpId, str_corpType, _parMap, _cacheMap); //Ĭ�ϲ������ӽ��!!!
	}

	//ȡ�����̴��������ڻ����ķ�Χ
	private ArrayList getWFCreaterCorpArea(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_wfcreater = (String) _parMap.get("���̴�����"); //���̴����˵�id!!
		String[] str_corpIdType = getCorpIdAndTypebyUerId(str_wfcreater); //
		if (str_corpIdType == null) {
			return new ArrayList(); //
		}
		String str_corpType = str_corpIdType[1]; //ȡ�ø���Ա�����ڻ�������
		if (str_corpType.endsWith("��������")) {
			str_corpType = str_corpType.substring(0, str_corpType.length() - 4); //
		}
		return getOnerUserOneUp1CorpAllChildrenCorps(str_wfcreater, str_corpType, _parMap, _cacheMap); //
	}

	//ȡ�����̴�����ĳ�����͵��ϼ�����!
	private ArrayList getWFCreaterSomeTypeParentCorp(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_wfcreater = (String) _parMap.get("���̴�����"); //���̴����˵�id!!
		String str_up1RootCorptype = (String) _parMap.get("�״����ݵ��ĸ�����"); //��������!!
		return getOnerUserOneUp1CorpAllChildrenCorps(str_wfcreater, str_up1RootCorptype, _parMap, _cacheMap); //
	}

	//ȡ�����̴��������ڻ���
	private ArrayList getLoginUserCorp(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_loginUserID = new WLTInitContext().getCurrSession().getLoginUserId(); //����Ժ�Ҫ�������̴�����,�����������¼�е�"participant_user"�ֶ�ֵ!!!��Ϊ����Ȩʱ,����Ǵ����˵�¼����,�������������ʵ��XX�쵼,�����߼�Ȩ��Ӧ�ð��쵼�ļ���!!!
		String[] str_corpIdType = getCorpIdAndTypebyUerId(str_loginUserID); //
		if (str_corpIdType == null) {
			return new ArrayList(); //
		}
		String str_corpId = str_corpIdType[0]; //ȡ�ø���Ա�����ڻ���
		String str_corpType = str_corpIdType[1]; //ȡ�ø���Ա�����ڻ�������
		getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "��ֱ�Ӵӵ�¼�˵����ڻ���[" + str_corpId + "]���ж�����̽!<br>"); //
		return getOnerCorpAllChildrenCorps(str_corpId, str_corpType, _parMap, _cacheMap); //Ĭ�ϲ������ӽ��!!!
	}

	//ȡ�õ�¼��Ա���ڻ����ķ�Χ!!
	private ArrayList getLoginUserCorpArea(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_loginUserID = new WLTInitContext().getCurrSession().getLoginUserId(); //����Ժ�Ҫ�������̴�����,�����������¼�е�"participant_user"�ֶ�ֵ!!!
		String[] str_corpIdType = getCorpIdAndTypebyUerId(str_loginUserID); //
		if (str_corpIdType == null) {
			return new ArrayList(); //
		}
		String str_corpType = str_corpIdType[1]; //ȡ�ø���Ա�����ڻ�������
		if (str_corpType.endsWith("��������")) {
			str_corpType = str_corpType.substring(0, str_corpType.length() - 4); //
		}
		return getOnerUserOneUp1CorpAllChildrenCorps(str_loginUserID, str_corpType, _parMap, _cacheMap); //
	}

	//ȡ�õ�¼��Աĳ�����͵��ϼ�����!!
	private ArrayList getLoginUserSomeTypeParentCorp(HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_loginUserID = new WLTInitContext().getCurrSession().getLoginUserId(); //����Ժ�Ҫ�������̴�����,�����������¼�е�"participant_user"�ֶ�ֵ!!
		String str_up1RootCorptype = (String) _parMap.get("�״����ݵ��ĸ�����"); //��������!!
		return getOnerUserOneUp1CorpAllChildrenCorps(str_loginUserID, str_up1RootCorptype, _parMap, _cacheMap); //
	}

	/**
	 * ȡ��ĳ���˵�ĳ���ϼ��������͵ĵķ�Χ�µ������ӻ���!!!
	 * @param _userId
	 * @param _up1CorpCondition
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	private ArrayList getOnerUserOneUp1CorpAllChildrenCorps(String _userId, String _up1CorpCondition, HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_down2CorpType = (String) _parMap.get("������̽���ӻ�������"); //��������չ����!!
		String str_down2ExtCorpType = (String) _parMap.get("������̽���ӻ�����չ����"); //��������չ����!!
		String str_down2CorpName = (String) _parMap.get("������̽���ӻ�������"); //����������!!
		String str_isdown2ContainChildren = (String) _parMap.get("������̽�Ƿ��������"); ////
		boolean isDown2ContainChildren = true; //
		if ("N".equalsIgnoreCase(str_isdown2ContainChildren)) {
			isDown2ContainChildren = false; //
		}

		SysAppDMO sysDMO = new SysAppDMO(); //
		if (str_down2CorpName != null && str_down2CorpName.startsWith("$")) { //�������������,���滻��ʵ������,���硾$�����š�=>����ƽ��㲿��
			HashVO[] hvs = sysDMO.getParentCorpVOByMacro(2, _userId, str_down2CorpName); //
			if (hvs != null && hvs.length > 0) { //
				//System.out.println("�������Ļ�������[" + str_down2CorpName + "]����Ѱ�ҵ�ʵ�ʻ�������=[" + hvs[0].getStringValue("name") + "]..."); //
				str_down2CorpName = hvs[0].getStringValue("name"); //
			} else {
				str_down2CorpName = null; //
			}
		}

		return sysDMO.getOnerUserSomeTypeParentCorp(_userId, _up1CorpCondition, str_down2CorpType, str_down2ExtCorpType, str_down2CorpName, isDown2ContainChildren, _cacheMap); //�ҳ����������ڻ�����!!!
	}

	/**
	 * ֱ�Ӹ��ݻ���ȡ��ȡ�������ӽ��!!�����ڱ�����!!
	 * @param _rootCorpId
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	private ArrayList getOnerCorpAllChildrenCorps(String _rootCorpId, String _rootCorpType, HashMap _parMap, HashMap _cacheMap) throws Exception {
		String str_down2CorpType = (String) _parMap.get("������̽���ӻ�������"); //��������չ����!!
		String str_down2ExtCorpType = (String) _parMap.get("������̽���ӻ�����չ����"); //��������չ����!!
		String str_down2CorpName = (String) _parMap.get("������̽���ӻ�������"); //����������!!
		String str_isdown2ContainChildren = (String) _parMap.get("������̽�Ƿ��������"); ////
		boolean isDown2ContainChildren = true; //
		if ("N".equalsIgnoreCase(str_isdown2ContainChildren)) {
			isDown2ContainChildren = false; //
		}
		return new DataPolicyDMO().secondDownFindAllCorpChildrensByCondition(_rootCorpId, _rootCorpType, str_down2CorpType, str_down2ExtCorpType, str_down2CorpName, false, isDown2ContainChildren, "����", _cacheMap); //������̽!
	}

	//������Աid�ҵ�ĳ�����ڵĻ�����������!!
	private String[] getCorpIdAndTypebyUerId(String _userId) throws Exception {
		HashVO[] hvs_wfcreaterCorp = new CommDMO().getHashVoArrayByDS(null, "select t1.userdept,t2.corptype,t1.isdefault from pub_user_post t1,pub_corp_dept t2 where t1.userdept=t2.id and t1.userid='" + _userId + "'"); //������Ĭ�ϻ���!!
		if (hvs_wfcreaterCorp == null || hvs_wfcreaterCorp.length == 0) { //���û��
			return null; //
		}
		for (int i = 0; i < hvs_wfcreaterCorp.length; i++) {
			if ("Y".equalsIgnoreCase(hvs_wfcreaterCorp[i].getStringValue("isdefault"))) { //�����Ĭ�ϲ���!
				return new String[] { hvs_wfcreaterCorp[i].getStringValue("userdept"), hvs_wfcreaterCorp[i].getStringValue("corptype") }; //
			}
		}
		return new String[] { hvs_wfcreaterCorp[0].getStringValue("userdept"), hvs_wfcreaterCorp[0].getStringValue("corptype") }; //
	}

	/**
	 * �Զ�����!!!
	 * @return
	 */
	private ArrayList getCustClassCorp(HashMap _parMap, HashMap _cacheMap) {
		return null; //
	}

	private WLTInitContext getInitContext() {
		if (initContext != null) {
			return initContext; //
		}
		initContext = new WLTInitContext();
		return initContext;
	}
}
