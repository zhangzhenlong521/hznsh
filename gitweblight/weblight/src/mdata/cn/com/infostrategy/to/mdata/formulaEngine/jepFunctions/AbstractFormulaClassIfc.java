package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.formulaEngine.SalaryFomulaParseUtil;

/**
 * ����ִ��һ���̶���.
 * @author haoming
 * create by 2013-9-16
 */
public interface AbstractFormulaClassIfc {
	/**
	 * 
	 * @param _factorVO Ŀǰû���õ������Դ���null
	 * @param _baseHashVO ����Ļ������󣬺���Ҫ��
	 * @param _parseUtil �����parseutil��
	 * @param _actionDescr ִ�е��������
	 * @return ����ִ�����ֵ
	 * @throws Exception
	 * �˷�����Ȼ�󷵻�ֵ������Ŀǰ�÷���ֵû�зŵ����ݿ��У���Ҫ�������Լ�дupdate���ȥִ�С�
	 * ����ֵ�������ʱ���õ���
	 */
	public void onExecute(HashVO _factorVO, HashVO _baseHashVO, SalaryFomulaParseUtil _parseUtil) throws Exception;
}
