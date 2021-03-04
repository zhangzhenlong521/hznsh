package cn.com.pushworld.salary.ui.baseinfo.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * @author Gwang
 * 2013-7-16 下午12:01:11
 */
public class TargetCheckUserListWKPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private BillListPanel listPanel;
	private WLTButton btnAdd, btnEdit, btnDel;
	
	/* (non-Javadoc)
	 * @see cn.com.infostrategy.ui.common.AbstractWorkPanel#initialize()
	 */
	@Override
	public void initialize() {
		listPanel = new BillListPanel("SAL_TARGET_CHECK_USER_LIST_CODE1");
		this.initBtn();
		listPanel.QueryDataByCondition(null);
		this.add(listPanel);
	}
	
	public void initBtn() {
		btnAdd = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btnEdit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btnDel = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		listPanel.addBatchBillListButton(new WLTButton[] { btnAdd, btnEdit, btnDel});
		listPanel.repaintBillListButton();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnAdd) {
			onAdd();
		}else if (e.getSource() == btnEdit) {
			onEdit();
		}
	

	}
	
	/**
	 * 新增
	 */
	private void onAdd() {
		BillCardPanel cardPanel = null;
		
		//创建一个卡片面板
		cardPanel = new BillCardPanel(listPanel.getTempletVO()); 
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		
		//弹出框
		BillCardDialog cardDialog = new BillCardDialog(listPanel, "新增考核", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);	
		cardDialog.setVisible(true);
		
		//确定返回
		if (cardDialog.getCloseType() == 1) {
			int li_newrow = listPanel.newRow(false); //
			listPanel.setBillVOAt(li_newrow, cardDialog.getBillVO(), false);
			listPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			listPanel.setSelectedRow(li_newrow); 	
		}
	}
	
	private void onEdit() {
		
	}

}
