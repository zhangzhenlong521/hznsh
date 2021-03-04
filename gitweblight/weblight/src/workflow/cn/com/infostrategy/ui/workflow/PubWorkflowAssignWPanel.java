package cn.com.infostrategy.ui.workflow;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ���̷���,��Ϊָ���ĵ���������ҵ�����ͷ�������!!
 * pub_workflowassign_CODE1
 * @author xch
 *
 */
public class PubWorkflowAssignWPanel extends AbstractWorkPanel {

	private static final long serialVersionUID = 2384368965449364104L;

	private BillListPanel billList; //

	@Override
	public void initialize() {
		billList = new BillListPanel("PUB_WORKFLOWASSIGN_CODE1"); //
		WLTButton btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //
		WLTButton btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //
		WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		WLTButton btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list }); //
		billList.repaintBillListButton(); //ˢ�°�ť!!!
		this.add(billList); //
	}

}
