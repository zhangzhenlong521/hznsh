package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * �ڹ�����BS�˾�����Ҫ���ּ���!
 * �����ڻ���������,����������! ���������жϵȵط�������Ҫ���ּ��㣺
 * ��ǰ�����Ƿ����������� ��ǰ�����Ƿ��ǽ�������
 * ��ǰ�����Ƿ�����ѭ���ڲ���ת  �Ƿ��߹�ĳ������  �Ƿ��߹�ĳ������ 
 * ��ǰ������ȥ�Ļ��ڱ�����ʲô,��ǰ�߳�ȥ��������ʲô! 
 * ��ǰ�����Ǵ��ĸ����ڹ�����!
 * @author Administrator
 *
 */
public class WorkFlowEngineTool {

	private CommDMO commDMO = new CommDMO(); //
	private WFParVO secondCallVO = null; //

	private WorkFlowEngineTool() {
	}

	/**
	 * ���췽��
	 * @param _secondCallVO
	 */
	public WorkFlowEngineTool(WFParVO _secondCallVO) {
		this.secondCallVO = _secondCallVO; //
	}

	/**
	 * �Ƿ����������ڣ�����
	 * @return
	 */
	public boolean isStartStep() {
		return secondCallVO.isStartStep(); //
	}

	/**
	 * �Ƿ��ڻ����ڲ�ѭ���ύ��!
	 * @return
	 */
	public boolean isSelfCycleInnerSubmit() {
		return this.secondCallVO.isSecondIsSelfcycleclick(); //�Ƿ�������ѭ��
	}

	/**
	 * �Ƿ��������ύ��!�����ܵ�����Ļ���û��һ�����ڵ�ǰ���ڣ�
	 * ��ѭ���������н�������Ļ����뱾����һ��!
	 * @return
	 */
	public boolean isSubmitOutoffSelfCycle() {
		return !secondCallVO.isSecondIsSelfcycleclick(); //;  //
	}

	//��ʲô��������,����ǵ�һ�ν���,���¼�˾��ж����ɫ!�ǿ��ܲ�׼,��Ϊȡ�õ����ȼ���ߵĻ���!
	public String getSelfCycleBindFromRoleCode() {
		return this.secondCallVO.getSecondSelfcycle_fromrolecode(); //
	}

	/**
	 * ȡ����ѭ��ʱ,��ѭ���������һ����ɫӳ�������ͼ!Ȼ�������Ǹ�����ͼ�д�ʲô���Ľ�ɫ�ġ���ɫ���ơ�������
	 * @return
	 */
	public String getSelfCycleBindFromRole() {
		return this.secondCallVO.getSecondSelfcycle_fromrolename(); //
	}

	//ȥ�����ĸ�����!!�������ѭ��,������Ǿ�ȷ��!!
	public String getSelfCycleBindToRoleCode() { //
		return this.secondCallVO.getSecondSelfcycle_torolecode(); //
	}

	/**
	 * ȡ����ѭ��ʱ,��ѭ���������һ����ɫӳ�������ͼ!Ȼ�������Ǹ�����ͼ��ȥʲô��ɫ�ġ���ɫ���ơ�������
	 * ��������Ǿ�ȷ��,��ʵ���ǵ���İ�ť������!!!
	 * @return
	 */
	public String getSelfCycleBindToRole() { //
		return this.secondCallVO.getSecondSelfcycle_torolename(); //
	}

	/**
	 * ����ĳ�������Ƿ��߹�����������ĺô���,��ʱ����Ҫ���Ǹ�����������·���ˣ�
	 * ֱ�Ӹ������߱���ȥȡ
	 * @param _transitionCode
	 * @return
	 * @throws Exception
	 */
	public int getOneTransitionThrowCount(String _transitionCode) throws Exception {
		String str_prinstanceId = this.secondCallVO.getWfinstanceid(); //����ʵ��Id
		String str_sql = "select count(*) c1 from pub_wf_dealpool t1,pub_wf_transition t2 where t1.transition=t2.id and t1.prinstanceid='" + str_prinstanceId + "' and t2.code='" + _transitionCode + "'"; //
		String str_count = commDMO.getStringValueByDS(null, str_sql); //
		int li_count = Integer.parseInt(str_count); //
		return li_count; //
	}

	/**
	 * ĳ�������Ƿ��߹��������õģ���
	 * @param _transitionCode
	 * @return
	 */
	public boolean isOneTransitionThrowed(String _transitionCode) throws Exception {
		int li_count = getOneTransitionThrowCount(_transitionCode); //
		if (li_count > 0) {
			return true; //
		} else {
			return false; //
		}
	}

	/**
	 * ��ǰ���ڱ���!
	 * @return
	 */
	public String getCurrActivityCode() {
		return this.secondCallVO.getCurractivityCode(); //
	}

	public String getCurrActivityName() {
		return this.secondCallVO.getCurractivityName(); //
	}

	/**
	 * ��ǰ��������ĸ�����ȥ�ˣ�����
	 * @return
	 */
	public String getToActivityCode() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_actCode = dealTaskVOs[i].getCurrActivityCode(); //
			if (str_actCode != null) {
				return str_actCode; //
			}
		}

		return null; //
	}

	/**
	 * ��ǰ��������ĸ�����ȥ�ˣ�����
	 * @return
	 */
	public String getToActivityName() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_actName = dealTaskVOs[i].getCurrActivityName(); //
			if (str_actName != null) {
				return str_actName; //
			}
		}
		return null; //
	}

	/**
	 * �����ĸ���,��ʱ��Ҫ������˵�����
	 * @return
	 */
	public String getToUserId() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_userId = dealTaskVOs[i].getParticipantUserId(); //
			if (str_userId != null) {
				return str_userId; //
			}
		}
		return null; //
	}

	/**
	 * �����ĸ���,��ʱ��Ҫ������˵�����
	 * @return
	 */
	public String getToUserCode() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_userCode = dealTaskVOs[i].getParticipantUserCode(); //
			if (str_userCode != null) {
				return str_userCode; //
			}
		}
		return null; //
	}

	/**
	 * �����ĸ���,��ʱ��Ҫ������˵�����
	 * @return
	 */
	public String getToUserName() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_userName = dealTaskVOs[i].getParticipantUserCode(); //
			if (str_userName != null) {
				return str_userName; //
			}
		}
		return null; //
	}

	/**
	 * ȥ���ĸ����ߣ���
	 * @return
	 */
	public String getToTransitionCode() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null; //
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_trsCode = dealTaskVOs[i].getTransitionCode(); //
			if (str_trsCode != null) {
				return str_trsCode; //
			}
		}
		return null; //
	}

	/**
	 * ȥ���ĸ����ߣ���
	 * @return
	 */
	public String getToTransitionName() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_trsName = dealTaskVOs[i].getTransitionName(); //
			if (str_trsName != null) {
				return str_trsName; //
			}
		}
		return null; //
	}

	/**
	 * ȡ��·�ɱ�ǣ���
	 * @param _key
	 * @return
	 */
	public String getRouteMark(String _key) {
		return null; //
	}

	/**
	 * ����·�ɱ�ǣ���
	 * @param _key
	 */
	public void setRouteMark(String _key) {
	}

}
