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
 * 工作流处理中心工厂!
 * 以前的工作流处理有两个地方
 * 一个是 WorkFlowDealBtnPanel,一个是WorkflowProcessPanel 
 * 但有的人还自己搞了按钮,然后还自定义了Action调
 * 处理-在主界面中通过列表处理
 * 提交-在弹出界面中
 * @author Administrator
 *
 */
public class WorkFlowDealActionFactory {

	/**
	 * 处理动作!!
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
	 * 处理
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
				MessageBox.show(_billList, "该处理任务关联的业务单据没有查询到数据!\n这是因为系统测试阶段的数据造成的,但流程仍然能够处理!\n系统上线后或清除垃圾数据后该问题应该不会再存在,请知悉!"); //
			}

			String str_loginUserId = ClientEnvironment.getInstance().getLoginUserID(); //登录人员id
			String str_task_id = getTaskId(billVO); //消息任务id
			String str_prDealPool_id = getPrDealPoolId(billVO); //流程任务id
			String str_wfinstanceid = getPrinstanceId(billVO); //流程实例!
			//System.out.println("taskid=[" + str_task_id + "],prdealpoolid=[" + str_prDealPool_id + "],prinstanceid=[" + str_wfinstanceid + "]"); //

			WorkFlowProcessDialog processDialog = null; //
			BillCardPanel cardPanel = new BillCardPanel(_billList.getTempletVO()); //创建一个卡片面板!!
			cardPanel.setBillVO(billVO.deepClone()); //在卡片中设置数据,需要克隆一把!!
			if (str_wfinstanceid == null || str_wfinstanceid.trim().equals("")) { //如果流程实例为空,则说明还没启动,则直接处理
				processDialog = new WorkFlowProcessDialog(_billList, "流程处理", cardPanel, _billList); //抄送!!!既然这里流程实例为空，则消息任务id、流程任务id和流程实例id三个值都为null，故标题中不要显示了【李春娟/2012-03-28】
				processDialog.setVisible(true); //
				if (processDialog.getCloseType() == 1) { //如果是确定返回的,则要刷新数据!!
					afterDealWorkFlow(_billList, 1, str_wfinstanceid, str_prDealPool_id, _listeners); //
				}
				return;
			}

			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); ////
			//以前的机制是如果发现有问题就弹出对话框,然后就直接退出不让处理了,新的机制是永远弹出对话框,然后在对话框中显示不能处理的原因!!!
			//这样的好处是客户仍然能看到表单中的内容,以及历史处理意见!!!
			HashVO hvoIsHaveDeal = service.judgeTaskDeal(str_wfinstanceid, str_prDealPool_id, str_loginUserId); //后来的最新的判断是否存在任务的方法! 极其关键!!
			boolean isHaveDeal = hvoIsHaveDeal.getBooleanValue("任务是否有效"); //
			String str_reason = hvoIsHaveDeal.getStringValue("原因说明"); //

			processDialog = new WorkFlowProcessDialog(_billList, "流程处理[taskid=" + str_task_id + "][dealpoolid=" + str_prDealPool_id + "][prinstanceid=" + str_wfinstanceid + "]", cardPanel, _billList, str_task_id, str_prDealPool_id, str_wfinstanceid, !isHaveDeal, str_reason, isYjbd); //抄送!!!
			processDialog.setVisible(true); //
			if (processDialog.getCloseType() == 1) { //如果是确定返回的,则要刷新数据!!
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
	 * 处理结束后,需要通知所有监听者刷新或者做某种处理!!
	 * @param _billList
	 * @param _dealType
	 * @param _prInstanceId
	 * @param _prdealPoolId
	 * @param _listeners
	 */
	private void afterDealWorkFlow(BillListPanel _billList, int _dealType, String _prInstanceId, String _prdealPoolId, WorkFlowDealListener[] _listeners) {
		if (_billList != null) {
			_billList.refreshData(true); //重新查询了一把???
		}
		if (_listeners != null && _listeners.length > 0) {
			for (int i = 0; i < _listeners.length; i++) {
				WorkFlowDealEvent event = new WorkFlowDealEvent(this, _dealType, _prInstanceId, _prdealPoolId); //
				_listeners[i].onDealWorkFlow(event); //执行!!
			}
		}

	}

	private void afterDealWorkFlow() {

	}

	//取得流程实例id
	private String getPrinstanceId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //如果没有定义hvo,即以前的机制!!则直接取
			return _billVO.getStringValue("WFPRINSTANCEID"); //直接从BillVO中取
		} else {
			return hvo.getStringValue("task_prinstanceid"); //从行号中取
		}
	}

	//取得流程实例id
	private String getPrDealPoolId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //如果没有定义hvo,即以前的机制!!则直接取
			return null; //
		} else {
			return hvo.getStringValue("task_prdealpoolid"); //从行号中取
		}
	}

	private String getTaskId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //如果没有定义hvo,即以前的机制!!则直接取
			return null; //
		} else {
			return hvo.getStringValue("task_taskdealid"); //从行号中取
		}
	}

	//取得流程实例id
	private String getTaskOffId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //如果没有定义hvo,即以前的机制!!则直接取
			return null; //
		} else {
			return hvo.getStringValue("task_taskoffid"); //从行号中取
		}
	}

}
