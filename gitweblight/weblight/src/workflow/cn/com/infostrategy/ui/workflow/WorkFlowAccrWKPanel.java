package cn.com.infostrategy.ui.workflow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 工作流授权界面
 * @author xch
 *
 */
public class WorkFlowAccrWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel billList;
	private WLTButton btn_add, btn_edit;
	private BillCardDialog carddialog;
	private BillCardPanel cardPanel;
	private BillVO returnvo;

	@Override
	public void initialize() {
		billList = new BillListPanel("PUB_WORKFLOWACCRPROXY_CODE1"); //
		btn_add = new WLTButton("新增");
		btn_edit = new WLTButton("修改");
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		billList.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, WLTButton.createButtonByType(WLTButton.LIST_DELETE), WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		billList.repaintBillListButton();
		billList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//太平客户纠结选择多条只能删除一条记录，这里直接设置为单选得了【李春娟/2016-04-27】
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAdd();
		} else if (e.getSource() == btn_edit) {
			onEdit();
		} else {
			onConfirm();
		}

	}

	private void onConfirm() {
		try {
			cardPanel.stopEditing(); //
			if (!cardPanel.checkValidate()) {
				return;
			}
			returnvo = cardPanel.getBillVO();
			if (returnvo.getStringValue("ACCRENDTIME") != null && !returnvo.getStringValue("ACCRENDTIME").trim().equals("") && returnvo.getStringValue("ACCRBEGINTIME").compareTo(returnvo.getStringValue("ACCRENDTIME")) > 0) {
				MessageBox.show(carddialog, "授权起始点必须小于授权结束点!");
				return;
			}
			cardPanel.updateData();
			carddialog.setCloseType(1);
			carddialog.dispose(); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}

	}

	private void onAdd() {
		cardPanel = new BillCardPanel(billList.templetVO); //创建一个卡片面板
		cardPanel.insertRow(); //卡片新增一行!

		carddialog = new BillCardDialog(billList, billList.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		carddialog.getBtn_save().setVisible(false);
		carddialog.getBtn_confirm().addActionListener(this);

		carddialog.setVisible(true); //显示卡片窗口
		if (carddialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = billList.newRow(false); //
			billList.setBillVOAt(li_newrow, returnvo, false);
			billList.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			billList.setSelectedRow(li_newrow); //
		}
	}

	private void onEdit() {
		BillVO billvo = billList.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(carddialog);
			return;
		}
		cardPanel = new BillCardPanel(billList.templetVO); //创建一个卡片面板
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setBillVO(billvo);

		carddialog = new BillCardDialog(billList, billList.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //弹出卡片新增框
		carddialog.getBtn_save().setVisible(false);
		carddialog.getBtn_confirm().addActionListener(this);
		carddialog.setVisible(true); //显示卡片窗口
		if (carddialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_row = billList.getSelectedRow();
			billList.setBillVOAt(li_row, returnvo, false);
			billList.setRowStatusAs(li_row, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			billList.setSelectedRow(li_row); //
		}
	}

}
