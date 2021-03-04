package cn.com.infostrategy.ui.workflow;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessDialog;

/**
 * �������������Ĺ���!
 * ��ǰ�Ĺ����������������ط�
 * һ���� WorkFlowDealBtnPanel,һ����WorkflowProcessPanel 
 * ���е��˻��Լ����˰�ť,Ȼ���Զ�����Action��
 * ����-����������ͨ���б���
 * �ύ-�ڵ���������
 * @author Administrator
 *
 */
public class WorkFlowDealActionFactory {

	/**
	 * ������!!
	 * @param _type
	 * @param _billList
	 */
	public void dealAction(String _type, BillListPanel _billList, WorkFlowDealListener[] _listeners) {
		if (_type.equalsIgnoreCase("deal")) {
			onDeal(_billList, _listeners); //
		} else if (_type.equalsIgnoreCase("yjbd")) {
			onYJBD(_billList, _listeners);
		}
	}

	/**
	 * ����
	 */
	private void onDeal(BillListPanel _billList, WorkFlowDealListener[] _listeners) {
		this.onDeal(_billList, _listeners, false);
	}
	
	private void onDeal(BillListPanel _billList, WorkFlowDealListener[] _listeners, boolean isYjbd) {
		try {
			BillVO billVO = _billList.getSelectedBillVO(); //
			if (billVO == null) {
				MessageBox.showSelectOne(_billList);
				return; //
			}

			String str_pkValue = billVO.getPkValue(); //
			if (str_pkValue == null || str_pkValue.trim().equals("")) {
				MessageBox.show(_billList, "�ô������������ҵ�񵥾�û�в�ѯ������!\n������Ϊϵͳ���Խ׶ε�������ɵ�,��������Ȼ�ܹ�����!\nϵͳ���ߺ������������ݺ������Ӧ�ò����ٴ���,��֪Ϥ!"); //
			}

			String str_loginUserId = ClientEnvironment.getInstance().getLoginUserID(); //��¼��Աid
			String str_task_id = getTaskId(billVO); //��Ϣ����id
			String str_prDealPool_id = getPrDealPoolId(billVO); //��������id
			String str_wfinstanceid = getPrinstanceId(billVO); //����ʵ��!
			//System.out.println("taskid=[" + str_task_id + "],prdealpoolid=[" + str_prDealPool_id + "],prinstanceid=[" + str_wfinstanceid + "]"); //

			WorkFlowProcessDialog processDialog = null; //
			BillCardPanel cardPanel = new BillCardPanel(_billList.getTempletVO()); //����һ����Ƭ���!!
			cardPanel.setBillVO(billVO.deepClone()); //�ڿ�Ƭ����������,��Ҫ��¡һ��!!
			if (str_wfinstanceid == null || str_wfinstanceid.trim().equals("")) { //�������ʵ��Ϊ��,��˵����û����,��ֱ�Ӵ���
				processDialog = new WorkFlowProcessDialog(_billList, "���̴���", cardPanel, _billList); //����!!!��Ȼ��������ʵ��Ϊ�գ�����Ϣ����id����������id������ʵ��id����ֵ��Ϊnull���ʱ����в�Ҫ��ʾ�ˡ����/2012-03-28��
				processDialog.setVisible(true); //
				if (processDialog.getCloseType() == 1) { //�����ȷ�����ص�,��Ҫˢ������!!
					afterDealWorkFlow(_billList, 1, str_wfinstanceid, str_prDealPool_id, _listeners); //
				}
				return;
			}

			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); ////
			//��ǰ�Ļ������������������͵����Ի���,Ȼ���ֱ���˳����ô�����,�µĻ�������Զ�����Ի���,Ȼ���ڶԻ�������ʾ���ܴ����ԭ��!!!
			//�����ĺô��ǿͻ���Ȼ�ܿ������е�����,�Լ���ʷ�������!!!
			HashVO hvoIsHaveDeal = service.judgeTaskDeal(str_wfinstanceid, str_prDealPool_id, str_loginUserId); //���������µ��ж��Ƿ��������ķ���! ����ؼ�!!
			boolean isHaveDeal = hvoIsHaveDeal.getBooleanValue("�����Ƿ���Ч"); //
			String str_reason = hvoIsHaveDeal.getStringValue("ԭ��˵��"); //

			processDialog = new WorkFlowProcessDialog(_billList, "���̴���[taskid=" + str_task_id + "][dealpoolid=" + str_prDealPool_id + "][prinstanceid=" + str_wfinstanceid + "]", cardPanel, _billList, str_task_id, str_prDealPool_id, str_wfinstanceid, !isHaveDeal, str_reason, isYjbd); //����!!!
			processDialog.setVisible(true); //
			if (processDialog.getCloseType() == 1) { //�����ȷ�����ص�,��Ҫˢ������!!
				afterDealWorkFlow(_billList, 1, str_wfinstanceid, str_prDealPool_id, _listeners); //
			}
		} catch (Exception _ex) {
			MessageBox.showException(_billList, _ex); //
		}
	}
	
	private void onYJBD(BillListPanel _billList, WorkFlowDealListener[] _listeners) {
		this.onDeal(_billList, _listeners, true);
	}

	/**
	 * ���������,��Ҫ֪ͨ���м�����ˢ�»�����ĳ�ִ���!!
	 * @param _billList
	 * @param _dealType
	 * @param _prInstanceId
	 * @param _prdealPoolId
	 * @param _listeners
	 */
	private void afterDealWorkFlow(BillListPanel _billList, int _dealType, String _prInstanceId, String _prdealPoolId, WorkFlowDealListener[] _listeners) {
		if (_billList != null) {
			_billList.refreshData(true); //���²�ѯ��һ��???
		}
		if (_listeners != null && _listeners.length > 0) {
			for (int i = 0; i < _listeners.length; i++) {
				WorkFlowDealEvent event = new WorkFlowDealEvent(this, _dealType, _prInstanceId, _prdealPoolId); //
				_listeners[i].onDealWorkFlow(event); //ִ��!!
			}
		}

	}

	private void afterDealWorkFlow() {

	}

	//ȡ������ʵ��id
	private String getPrinstanceId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //���û�ж���hvo,����ǰ�Ļ���!!��ֱ��ȡ
			return _billVO.getStringValue("WFPRINSTANCEID"); //ֱ�Ӵ�BillVO��ȡ
		} else {
			return hvo.getStringValue("task_prinstanceid"); //���к���ȡ
		}
	}

	//ȡ������ʵ��id
	private String getPrDealPoolId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //���û�ж���hvo,����ǰ�Ļ���!!��ֱ��ȡ
			return null; //
		} else {
			return hvo.getStringValue("task_prdealpoolid"); //���к���ȡ
		}
	}

	private String getTaskId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //���û�ж���hvo,����ǰ�Ļ���!!��ֱ��ȡ
			return null; //
		} else {
			return hvo.getStringValue("task_taskdealid"); //���к���ȡ
		}
	}

	//ȡ������ʵ��id
	private String getTaskOffId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //���û�ж���hvo,����ǰ�Ļ���!!��ֱ��ȡ
			return null; //
		} else {
			return hvo.getStringValue("task_taskoffid"); //���к���ȡ
		}
	}

}
