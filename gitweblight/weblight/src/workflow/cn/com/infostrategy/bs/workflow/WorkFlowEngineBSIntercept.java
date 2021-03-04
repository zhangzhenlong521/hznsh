package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * ����������BS��ִ�е�������!!!
 * �������ж������,��ÿ������ֻ�ᱻע����ĳΨһ�ĵط��Żᱻ����!!���໥�ཻ�����!!
 * һ����˵�����������ط�����ע��,һ������,һ�ǻ���,һ������!!
 * ��������뻷�ڶ�ע����,��ʵ���˸��Է������߼�,������ε�����������!!!
 * 
 * ΪʲôҪ������������Ū��һ�����еĸ�������,�����Ǹ�������??? ԭ������չʱ�ή�ʹ���Ķ���! �����ɸ�����,������һ��ע��ĵط�,����Ҫ���ݿ���ֶ�,�޸�XMLģ��,�޸ļ����뱣��ط����߼�,��֮�Ķ�һ��ѵط�!!!
 * ��Ū��һ����,Ȼ���ǳ�����,������չʱ�ǳ�����!!!
 * @author xch
 *
 */
public abstract class WorkFlowEngineBSIntercept {

	/**
	 * ĳ������ִ��ǰ�Ĵ���,ֻ��ע���ڻ�����,�÷����Żᱻ���ã���
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void beforeActivityAction(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		//ΪʲôҪ������������ʾ?һ���������������ʲôʱ��ִ����,�������ѿ�����Ա�ع����ֹ�ع�����������(�����ǻ������,�����������),ǿ�ҽ��������ع������ϵ�@overwrite��Ҫȥ��,����ǳ����׳��ָķ�����ȴ��֪�������!!
		//���仰˵����,���������Ա������������,ȴû���ع���ȷ�ķ���,��Ϳ���̨�ͻ�������־���,���ѿ�����Ա...
		System.out.println("���Ѷ���������BS��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineBSIntercept]��beforeActivityAction()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
	}

	/**
	 * ĳ��һ��ִ�к�Ĵ���,ֻ��ע���ڻ�����,�÷����Żᱻ���ã���
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void afterActivityAction(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		System.out.println("���Ѷ���������BS��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineBSIntercept]��afterActivityAction()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
	}

	/**
	 * ĳ������ִ�еĴ���! ֻ��ע����������,�÷����Żᱻ���ã���
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void afterTransitionAction(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		System.out.println("���Ѷ���������BS��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineBSIntercept]��afterTransitionAction()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
	}

	/**
	 * �������̽�����ִ�е��߼�,ֻ��ע���������и÷����Żᱻ���ã���
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void afterWorkFlowEnd(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		System.out.println("���Ѷ���������BS��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineBSIntercept]��afterWorkFlowEnd()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
	}

	

}
