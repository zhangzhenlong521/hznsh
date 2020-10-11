package cn.com.infostrategy.ui.workflow;

/**
 * 业务动态二次过滤
 */
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListButtonActinoListener;
import cn.com.infostrategy.ui.mdata.BillListButtonClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.styletemplet.format.AbstractStyleWorkPanel_0A;
import cn.com.infostrategy.ui.mdata.styletemplet.format.IUIIntercept_0A;

public class Intecept_dynfilter2 implements IUIIntercept_0A, BillListButtonActinoListener,BillListSelectListener,BillListAfterQueryListener {
	private AbstractStyleWorkPanel_0A panel;
	private BillListPanel billListPanel_up;
	private BillListPanel billListPanel_down;

	public void afterSysInitialize(AbstractStyleWorkPanel_0A _panel) throws Exception {
		panel = _panel;
		billListPanel_up = panel.getBillListPanelByTempletCode("pub_workflowassign_CODE2");
		billListPanel_down = panel.getBillListPanelByTempletCode("pub_workflowassign_dynfilter2_CODE1");
		billListPanel_down.addBillListButtonActinoListener(this);
		billListPanel_up.addBillListSelectListener(this);
		billListPanel_up.addBillListAfterQueryListener(this);
	}

	public void onBillListAddButtonClicked(BillListButtonClickedEvent _event) throws Exception {
		// TODO Auto-generated method stub

	}

	public void onBillListAddButtonClicking(BillListButtonClickedEvent _event) throws Exception {
		if (billListPanel_up.getSelectedBillVO() == null) {
			throw new WLTAppException("请选择一条记录!!");
		}
		
		

	}

	public void onBillListButtonClicked(BillListButtonClickedEvent _event) {
		// TODO Auto-generated method stub

	}

	public void onBillListDeleteButtonClicked(BillListButtonClickedEvent _event) {
		// TODO Auto-generated method stub

	}

	public void onBillListDeleteButtonClicking(BillListButtonClickedEvent _event) {
		// TODO Auto-generated method stub

	}

	public void onBillListEditButtonClicked(BillListButtonClickedEvent _event) {
		// TODO Auto-generated method stub

	}

	public void onBillListEditButtonClicking(BillListButtonClickedEvent _event) throws Exception {
		// TODO Auto-generated method stub

	}

	public void onBillListLookAtButtonClicked(BillListButtonClickedEvent _event) {
		// TODO Auto-generated method stub

	}

	public void onBillListLookAtButtonClicking(BillListButtonClickedEvent _event) {
		// TODO Auto-generated method stub

	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		
		billListPanel_down.QueryDataByCondition("assignid="+billListPanel_up.getSelectedBillVO().getStringValue("id"));
	}

	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		
		billListPanel_down.clearTable();
	}

}
