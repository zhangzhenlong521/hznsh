package cn.com.infostrategy.bs.mdata;

/**
 * ����Ȩ��֮��ʽ�ӿ�,���еĹ�ʽ���Զ����඼����ʵ�ָýӿ�!���磺
 * getCorpFH({createcorp});  //���ݻ���id�������������
 * getLoginUserBLFH();  //�������¼��Ա��������
 * isSupperAdmin();  //�Ƿ񳬼�����Ա
 * getLoginUserCorp();  //����õ���¼��Ա����������
 * getCorpByUserID({createuserid});  //������ԱID��Ϊ��������õ�����������
 * isZHorFH(); //�����л��Ƿ���
 * 
 * �����д�{}��˵���Ǻ���룬����Ҫ������ʱ��ʵ�����ݵĶ�Ӧ�ֶν����滻��!!
 * ���нӿڵ�ʵ����,Ӧ��ע���ڹ��췽���н��������ݶ�Ԥ�ȴ����ݿ���ȡ������Ȼ��ֻ�ڽӿڷ����н����ڴ���㣬�������ܲŸߣ�
 * ���һ������û��һ����������Ӧ��ֱ���ڹ��췽���ж�����ã�Ȼ���ڽӿڷ�����ֱ�ӷ������������Ŷԣ�
 * @author xch
 */
public interface DataAccessFunctionIFC {

	public static String[][] regFunctions = new String[][] { //���о�̬����,��������������ʹ��,����Դ��ʽ�����ֵ������ʹ��!!!
	{ "getLoginUserCorp()", "cn.com.infostrategy.bs.mdata.dataaccessfuns.GetLoginUserCorp", "ȡ�õ�¼��Ա��������" }, //
			{ "getLoginUserBelongFHCorp()", "", "" }, //ȡ�õ�¼��Ա�������е�id
			{ "getLoginUserIsZHorFH()", "" }, //�жϵ�¼��Ա�����л��Ƿ���,���ء�����/���С�
			{ "getLoginUserRoleCodes()", "" }, //ȡ�õ�¼��Ա���н�ɫ����,���ء��Ϲ����Ա;���ɹ���Ա;��ϴǮ����Ա...��
			{ "getLoginUserPostCodes()", "" }, //ȡ�õ�¼���������ĸ�λ
			{ "isLoginUserInRoleCodes(\"���кϹ����Ա\",\"���з��ɹ���Ա\")", }, //��¼��Ա�Ƿ������Щ��ɫ,���ء���/��
			{ "isLoginUserInPostCodes(\"���ɸ�\",\"��ϴǮ��\",\"�����г�\")", }, //��¼��Ա�Ƿ������Щ��λ
			{ "getBelongFHByCorpID({CreateCorp})", "cn.com.infostrategy.bs.mdata.dataaccessfuns.GetBelongFHByCorpID", "���ݻ���idȡ������������" }, //���ݻ���idȡ������������
			{ "getCorpIDByUserID({CreateUser})", "", "������Աidȡ������������id" }, //������Աidȡ������������id
			{ "getBelongFHByUserID({CreateUser})", "" }, //������Աidȡ���������������������е�id
			{ "getAllRoleCodesByUserID({CreateUser})", "" }, //������Աidȡ���������е����н�ɫ����,�ԷֺŸ���
			{ "getAllPostCodesByUserID({CreateUser})", "" }, //������Աidȡ���������е����и�λ����,�ԷֺŸ���
			{ "getColValueByItemId(\"pub_comboboxdict\",\"name\",\"id\",{filetype},\"type='��ϵ�ļ�����'\")", "" }, //����id���ֶ�ȥһ������ȡ����һ���ֶε�ֵ!!
	};

	/**
	 * ȡ�ü����ֵ
	 * @param _pars ����,������ֻҪһ����������,�����ǵ���չ,֧������!����๦�ܿ���ʹ��һ����������㶨,������Ҫд�����,ά������Ҳ����!!!
	 * @return
	 */
	public String getFunValue(String[] _pars); //////

}
