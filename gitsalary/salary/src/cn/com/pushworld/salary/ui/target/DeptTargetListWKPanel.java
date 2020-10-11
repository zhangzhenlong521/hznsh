package cn.com.pushworld.salary.ui.target;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ComboBox;

/**
 * 部门指标维护
 * 
 * @author Gwang 2013-6-24 下午10:20:16
 */
public class DeptTargetListWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener, BillListSelectListener {

	private BillTreePanel treePanel; // 机构树
	private BillListPanel listPanel; // 指标列表
	private WLTButton btn_add, btn_edit, btn_delete; // 列表上的所有按钮

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		treePanel = new BillTreePanel("PUB_CORP_DEPT_SELF");
		treePanel.setMoveUpDownBtnVisiable(false);
		treePanel.queryDataByCondition(null);
		treePanel.addBillTreeSelectListener(this);

		listPanel = new BillListPanel("SAL_TARGET_LIST_CODE1");
		listPanel.addBillListSelectListener(this);
		listPanel.setItemVisible("refdeptid", false);
		btn_add = new WLTButton("新增");
		btn_edit = new WLTButton("修改");
		btn_delete = new WLTButton("删除");
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete });
		listPanel.repaintBillListButton();
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, treePanel, listPanel);
		splitPane.setDividerLocation(220);
		this.add(splitPane);
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		// TODO Auto-generated method stub
		if (event.getCurrSelectedNode().isRoot() || event.getCurrSelectedVO() == null) {
			listPanel.clearTable();
		} else {
			BillVO billVO = event.getCurrSelectedVO();
			String deptid = billVO.getStringValue("id");
			listPanel.QueryDataByCondition("deptid=" + deptid + " or refdeptid like '%;"+deptid+";%'");
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAdd();
		} else if (e.getSource() == btn_edit) {
			onEdit();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		}
	}

	/**
	 * 新增
	 */
	private void onAdd() {
		BillVO billVO = treePanel.getSelectedVO();
		if (billVO == null) {
			MessageBox.show(this, "请先选择一个指标类型.");
			return;
		}

		//创建一个卡片面板
		BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		
		//去除下拉框中"全行指标"
		CardCPanel_ComboBox combox = (CardCPanel_ComboBox)cardPanel.getCompentByKey("type");
		combox.getComBox().removeItemAt(0);
		combox.getComBox().removeItemAt(0);
		
		//设置默认值
		cardPanel.setValueAt("deptid", new RefItemVO(billVO.getStringValue("id"), "", billVO.getStringValue("name")));
		cardPanel.setVisiable("catalogid", false);
		cardPanel.setVisiable("refdeptid", false);
		
		//弹出框
		BillCardDialog cardDialog = new BillCardDialog(listPanel, "新增指标", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);

		//确定返回
		if (cardDialog.getCloseType() == 1) {
			int rowCount = listPanel.getRowCount();
			listPanel.insertRow(rowCount, cardPanel.getBillVO());
			listPanel.setRowStatusAs(rowCount, WLTConstants.BILLDATAEDITSTATE_INIT);
			listPanel.setSelectedRow(rowCount);
		}
	}

	/**
	 * 编辑
	 */
	private void onEdit() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		
		//创建一个卡片面板
		BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.setBillVO(billVO); 
		
		//去除下拉框中"全行指标"
		CardCPanel_ComboBox combox = (CardCPanel_ComboBox)cardPanel.getCompentByKey("type");
		combox.getComBox().removeItemAt(1);
		
		//设置默认值
		cardPanel.setVisiable("catalogid", false);
		cardPanel.setVisiable("refdeptid", false);
		
		//弹出框
		BillCardDialog dialog = new BillCardDialog(this, "修改指标", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); 
		
		//确定返回
		if (dialog.getCloseType() == 1) {
			if (listPanel.getSelectedRow() == -1) {
			} else {
				listPanel.setBillVOAt(listPanel.getSelectedRow(), dialog.getBillVO());
				listPanel.setRowStatusAs(listPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		}
	}

	/**
	 * 删除
	 */
	private void onDelete() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("有效".equals(billVO.getStringValue("state"))) {
			MessageBox.show(this, "该记录为[有效]状态, 您不能删除.");
		} else {
			listPanel.doDelete(false);
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO selectedRow = listPanel.getSelectedBillVO();
		if (selectedRow.getStringValue("type").equals("全行指标")) {
			btn_delete.setEnabled(false);
			btn_edit.setEnabled(false);
		}else {
			btn_delete.setEnabled(true);
			btn_edit.setEnabled(true);
		}
	}

}
