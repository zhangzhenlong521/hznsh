package cn.com.infostrategy.bs.report.style1;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

/**
 * ��񱨱�1�Ĺ������ݵĽӿ�
 * @author xch
 *
 */
public interface StyleReport_1_BuildDataIFC {

	/**
	 * ��������
	 * @param _condition  ��ѯ���������Ĳ�ѯ����!!!
	 * @param _loginUserVO ��¼��Ա�������Ϣ
	 * @return
	 * @throws Exception
	 */
	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO _loginUserVO) throws Exception; //

}
