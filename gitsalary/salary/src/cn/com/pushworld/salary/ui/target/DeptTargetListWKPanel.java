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
 * ����ָ��ά��
 * 
 * @author Gwang 2013-6-24 ����10:20:16
 */
public class DeptTargetListWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener, BillListSelectListener {

	private BillTreePanel treePanel; // ������
	private BillListPanel listPanel; // ָ���б�
	private WLTButton btn_add, btn_edit, btn_delete; // �б��ϵ����а�ť

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
		btn_add = new WLTButton("����");
		btn_edit = new WLTButton("�޸�");
		btn_delete = new WLTButton("ɾ��");
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
	 * ����
	 */
	private void onAdd() {
		BillVO billVO = treePanel.getSelectedVO();
		if (billVO == null) {
			MessageBox.show(this, "����ѡ��һ��ָ������.");
			return;
		}

		//����һ����Ƭ���
		BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		
		//ȥ����������"ȫ��ָ��"
		CardCPanel_ComboBox combox = (CardCPanel_ComboBox)cardPanel.getCompentByKey("type");
		combox.getComBox().removeItemAt(0);
		combox.getComBox().removeItemAt(0);
		
		//����Ĭ��ֵ
		cardPanel.setValueAt("deptid", new RefItemVO(billVO.getStringValue("id"), "", billVO.getStringValue("name")));
		cardPanel.setVisiable("catalogid", false);
		cardPanel.setVisiable("refdeptid", false);
		
		//������
		BillCardDialog cardDialog = new BillCardDialog(listPanel, "����ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);

		//ȷ������
		if (cardDialog.getCloseType() == 1) {
			int rowCount = listPanel.getRowCount();
			listPanel.insertRow(rowCount, cardPanel.getBillVO());
			listPanel.setRowStatusAs(rowCount, WLTConstants.BILLDATAEDITSTATE_INIT);
			listPanel.setSelectedRow(rowCount);
		}
	}

	/**
	 * �༭
	 */
	private void onEdit() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		
		//����һ����Ƭ���
		BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.setBillVO(billVO); 
		
		//ȥ����������"ȫ��ָ��"
		CardCPanel_ComboBox combox = (CardCPanel_ComboBox)cardPanel.getCompentByKey("type");
		combox.getComBox().removeItemAt(1);
		
		//����Ĭ��ֵ
		cardPanel.setVisiable("catalogid", false);
		cardPanel.setVisiable("refdeptid", false);
		
		//������
		BillCardDialog dialog = new BillCardDialog(this, "�޸�ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); 
		
		//ȷ������
		if (dialog.getCloseType() == 1) {
			if (listPanel.getSelectedRow() == -1) {
			} else {
				listPanel.setBillVOAt(listPanel.getSelectedRow(), dialog.getBillVO());
				listPanel.setRowStatusAs(listPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		}
	}

	/**
	 * ɾ��
	 */
	private void onDelete() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("��Ч".equals(billVO.getStringValue("state"))) {
			MessageBox.show(this, "�ü�¼Ϊ[��Ч]״̬, ������ɾ��.");
		} else {
			listPanel.doDelete(false);
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO selectedRow = listPanel.getSelectedBillVO();
		if (selectedRow.getStringValue("type").equals("ȫ��ָ��")) {
			btn_delete.setEnabled(false);
			btn_edit.setEnabled(false);
		}else {
			btn_delete.setEnabled(true);
			btn_edit.setEnabled(true);
		}
	}

}
