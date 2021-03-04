package cn.com.infostrategy.to.mdata.formulaEngine;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/*
 * �������ͽ������߱���̳иĳ����ࡣ������ϵͳ�����ı������֡�Excel�������...
 */
public abstract class AbstractFomulaParse {
	protected TBUtil tbutil = new TBUtil();
	public SalaryTBUtil salaryTBUtil = new SalaryTBUtil(); //�����ڲ��Ĳ�ѯ���ݿ��Ѿ����˿ͻ��˷��������ж�.
	
	/**
	 *  
	 * @param util parseUtil���кܶ໺�����ݣ����봫��.
	 * @param _factorHashVO ���Ӷ���
	 * @param _baseDataHashVO ����� ��������
	 * @param _level ִ�в㼶
	 * @param rtStr ƴ��ִ�й�������
	 * @return
	 * @throws Exception
	 */
	public abstract Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception;

}
