package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * ������2�����ڶ����������д���ʱ��������
 * ���������������ں�ִ̨��
 * @author xch
 *
 */
public interface WFIntercept2IFC {

	public void beforeAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception;

	public void afterAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception;

}
