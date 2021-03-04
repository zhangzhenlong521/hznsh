package cn.com.infostrategy.ui.workflow.engine;

import javax.swing.JPanel;

/**
 * 工作流中选择参与者时，右边CheckUser弹出框的名称
 * @author xch
 *
 */
public abstract class AbstractWorkFlowCheckUserPanel extends JPanel {

	private WorkFlowDealDialog workFlowDealDialog2 = null;

	private static final long serialVersionUID = 2811795459998265094L;

	public void setWorkFlowDialog(WorkFlowDealDialog _workFlowDealDialog2) {
		this.workFlowDealDialog2 = _workFlowDealDialog2; //
	}

	public WorkFlowDealDialog getWorkFlowDealDialog() {
		return workFlowDealDialog2;
	}

}
