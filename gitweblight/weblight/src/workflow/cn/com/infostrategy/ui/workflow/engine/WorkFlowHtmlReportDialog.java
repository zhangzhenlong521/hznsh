package cn.com.infostrategy.ui.workflow.engine;

import java.util.HashMap;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.report.BillHtmlPanel;

public class WorkFlowHtmlReportDialog extends BillDialog {

	private static final long serialVersionUID = 1L;
	private String str_prinstanceid = null; //
	private BillVO billVO = null; //单据中传过来的数据
	private BillHtmlPanel billHtmlPanel = null; //html面板

	public WorkFlowHtmlReportDialog(java.awt.Container _parent, String _prinstanceid, BillVO _billVO) {
		super(_parent, "报表", 1000, 700); //
		this.str_prinstanceid = _prinstanceid; //
		this.billVO = _billVO; //
		initialize(); //
	}

	private void initialize() {
		billHtmlPanel = new BillHtmlPanel(); //
		HashMap map_par = new HashMap(); //
		map_par.put("prinstanceid", str_prinstanceid); //
		map_par.put("billvo", this.billVO); //

		billHtmlPanel.loadhtml("cn.com.infostrategy.bs.workflow.WorkflowHtmlReportDMO", map_par); //
		this.getContentPane().add(billHtmlPanel); //
	}

}

