package cn.com.infostrategy.bs.mdata.dataaccessfuns;

import java.util.HashMap;

import cn.com.infostrategy.bs.mdata.DataAccessFunctionIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * ���ݻ���idȡ�����������еĻ���id
 * @author xch
 *
 */
public class GetBelongFHByCorpID implements DataAccessFunctionIFC {

	private HashVO[] allCorpVOs = null; //�洢���л������ݵ������

	private HashMap map_index = new HashMap(); //�洢�����Ĺ�ϣ��,��������ʱ����!!

	/**
	 * ���췽��,��һ���Ӵ����ݿ���ȡ�����л����Ĺ�ϵ,Ȼ�󸳸������
	 */
	public GetBelongFHByCorpID() {
		//�����ݿ���ȡ�����л�������,���������,���Զ������ͽṹ,����ƽ̨�ķ���!!!
	}

	public String getFunValue(String[] _pars) {
		String str_corpid = _pars[0];  //��һ��������ʵ�ʻ���id
		//�������������,�����ϲ����,�ҵ�һ�����������Ϊ���еĻ���,���Ƿ���ֵ!
		return null;
	}

}
