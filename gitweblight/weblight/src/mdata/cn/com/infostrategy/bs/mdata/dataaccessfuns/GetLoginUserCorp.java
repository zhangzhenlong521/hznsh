package cn.com.infostrategy.bs.mdata.dataaccessfuns;

import cn.com.infostrategy.bs.mdata.DataAccessFunctionIFC;

/**
 * ȡ�õ�¼��Ա����������
 * @author xch
 *
 */
public class GetLoginUserCorp implements DataAccessFunctionIFC {

	private String str_CorpId = null; //��Ա��������id

	/**
	 * ���췽��
	 */
	public GetLoginUserCorp() {
		//�ڹ��췽���������ֵ,Ȼ��ȡʱֱ�ӷ���
		//��ȡ�õ�¼��Ա��id,Ȼ��ȥ���ݿ��в�ѯ,Ȼ�󸳸����������
		str_CorpId = "1234";
	}

	public String getFunValue(String[] _pars) {
		return str_CorpId;
	}

}
