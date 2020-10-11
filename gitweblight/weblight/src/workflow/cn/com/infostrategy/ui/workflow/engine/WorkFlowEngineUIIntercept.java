package cn.com.infostrategy.ui.workflow.engine;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * ����������UI��������!!!
 * ��ǰ�Ĺ������������е���,һ����6��,��ƽ̨ԭ��������(UI�˻��ڴ���,BS��),�����߷��������(UI�����̴���,UI�����̽�����),�������ּ�������(UI�����̴���,UI�˻��ڴ���)!
 * �������ã����Ǹо���Ҫ�¼ӷ�����������¼���������������е���,����ά��������,Ҳû��Ҫ!!! ����Ӧ���Ǽ̳г��������!!! Ҳ�������ȸ�һ���ӿ�,��Ūһ��Ĭ���������ĳ�����,���¶��ο�����Ա���ó�����,���Ըɴ�ֻŪһ��������!!
 * ��ǰ�������ǽӿ�,����������,��Ŀ�е���̳нӿں�,�����ʱƽ̨�ټ��·���,����Ŀ���붼���벻����,����Ӧ��Ū�ɳ�����!!!
 * ��������Ժ���Բ��������µķ���!!!���ֲ�Ӱ����Ŀ�еĴ���!!!
 * 
 * �������ж������,��ÿ������ֻ�ᱻע����ĳΨһ�ĵط��Żᱻ����!!���໥�ཻ�����!!
 * һ����˵�����������ط�����ע��,һ������,һ�ǻ���,һ������!!
 * ��������뻷�ڶ�ע����,��ʵ���˸��Է������߼�,������ε�����������!!!
 * 
 * ΪʲôҪ������������Ū��һ�����еĸ�������,�����Ǹ�������??? ԭ������չʱ�ή�ʹ���Ķ���! �����ɸ�����,������һ��ע��ĵط�,����Ҫ���ݿ���ֶ�,�޸�XMLģ��,�޸ļ����뱣��ط����߼�,��֮�Ķ�һ��ѵط�!!!
 * ��Ū��һ����,Ȼ���ǳ�����,������չʱ�ǳ�����!!!
 * @author xch
 *
 */
public abstract class WorkFlowEngineUIIntercept {

	/**
	 * �򿪴������,ֻ��ע����������,��������Żᱻ����!!!
	 * @param _processPanel
	 * @throws Exception
	 */
	public void afterOpenWFProcessPanel(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billVO, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		//ΪʲôҪ������������ʾ?һ���������������ʲôʱ��ִ����,�������ѿ�����Ա�ع����ֹ�ع�����������(�����ǻ������,�����������),ǿ�ҽ��������ع������ϵ�@overwrite��Ҫȥ��,����ǳ����׳��ָķ�����ȴ��֪�������!!
		//���仰˵����,���������Ա������������,ȴû���ع���ȷ�ķ���,��Ϳ���̨�ͻ�������־���,���ѿ�����Ա...
		System.out.println("���Ѷ���������UI��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineUIIntercept]��afterOpenWFProcessPanel()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
	}

	/**
	 * ����ύ��ťǰ��У��,������������̵�!
	 * @param _processPanel
	 * @param _currActivity 
	 * @param _billvo 
	 * @throws Exception
	 */
	public int beforeSubmitWFProcessPanel(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billvo, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		System.out.println("���Ѷ���������UI��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineUIIntercept]beforeSubmitWFProcessPanel()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
		return 0; //Ĭ������������!
	}

	/**
	 * �򿪽����,��ǰ���ڴ�����߼�!!!ֻ��ע���ڻ�����,��������Żᱻ����!!
	 * @param _processPanel
	 * @param _currActivity 
	 * @param _billvo 
	 * @throws Exception
	 */
	public void afterOpenWFProcessPanelByCurrActivity(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billvo, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		System.out.println("���Ѷ���������UI��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineUIIntercept]��afterOpenWFProcessPanelByCurrActivity()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
	}

	/**
	 * ����ύ��ťǰ��У��,����У���Ƿ�ĳһ�����ݲ���?
	 * @param _processPanel
	 * @param _currActivity 
	 * @param _billvo 
	 * @throws Exception
	 */
	public int beforeSubmitWFProcessPanelByCurrActivity(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billvo, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		System.out.println("���Ѷ���������UI��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineUIIntercept]��beforeSubmitWFProcessPanelByCurrActivity()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
		return 0; //Ĭ������������!
	}

	/**
	 * ���̽�������!!!��ֻ��ע����������,��������Żᱻ����!!
	 * @param _processPanel
	 * @param _billvo
	 * @throws Exception
	 */
	public void afterWorkFlowEnd(WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billvo, HashVO _currActivity, WLTHashMap _otherParMap) throws Exception {
		System.out.println("���Ѷ���������UI��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineUIIntercept]��afterWorkFlowEnd()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
	}

	/**
	 * �Զ��������˻ؽ����ˡ������������ж�ִ��
	 * @param _processPanel
	 * @param _billtype
	 * @param _busitype
	 * @param _billvo
	 * @param _currActivity
	 * @param _otherParMap
	 * @return
	 */
	public WFParVO onRejectCustSelectWRPar(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel, String _billtype, String _busitype, BillVO _billvo, HashVO _currActivity, WFParVO _firstTaskVO, WLTHashMap _otherParMap) {
		System.out.println("���Ѷ���������UI��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineUIIntercept]��onRejectCustSelectWRPar()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
		return null;
	}

	/**
	 * �Զ������̴�ѡ����Ա����ִ���߼��������������ж�ִ�С����/2016-04-20��
	 * @param _processPanel
	 * @param _billtype
	 * @param _busitype
	 * @param _billvo
	 * @param _currActivity
	 * @param _firstTaskVO
	 * @param _otherParMap
	 * @return
	 */
	public void afterOpenParticipateUserPanel(ParticipateUserPanel _userPanel, BillVO _billvo, HashVO _currActivity, WFParVO _firstTaskVO, WLTHashMap _otherParMap) {
		System.out.println("���Ѷ���������UI��������[" + this.getClass().getName() + "],ϵͳִ���˻���[WorkFlowEngineUIIntercept]��afterOpenParticipateUserPanel()����,��������Ӧ���ع��÷���!!�����ع��ķ�����������??"); //
	}

}
