package com.pushworld.ipushgrc.ui.risk.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 风险评估申请表!! 即风评的日常工作通过申请表来驱动! 对应的表是 cmp_riskeval
 * A.风评表的数据可以直接录入,也可以由内规变化或风险事件变化时自动插入!!!
 * B.如果是走流程,则有按钮【提交申请】【流程监控】
 * @author xch
 *
 */
public class RiskApplyWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel billList_riskeval = null;
	private WLTButton btn_add = null;
	private WLTButton btn_edit = null;
	private WLTButton btn_process = null;
	private WLTButton btn_del = null;
	@Override
	public void initialize() {
		billList_riskeval = new BillListPanel("CMP_RISKEVAL_CODE1"); //
		btn_add = new WLTButton("新建");
		btn_edit = new WLTButton("修改");
		btn_del = new WLTButton("删除");
		btn_process = WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR);
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_del.addActionListener(this);
		billList_riskeval.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit,btn_del, btn_process });
		billList_riskeval.repaintBillListButton();
		this.add(billList_riskeval); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAddRiskApply();
		} else if (e.getSource() == btn_edit) {
			onEditRiskApply();
		} else if (e.getSource() == btn_del){
			onDelete();
		}
	}
	private void onDelete(){
		BillVO billVO = billList_riskeval.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if(billVO.getStringValue("wfprinstanceid")!=null && !billVO.getStringValue("wfprinstanceid").equals("")){
			MessageBox.show(this, "此记录已提交不可删除!!!"); //
			return;
		}
		if (MessageBox.showConfirmDialog(this, "您确定要删除吗?") != JOptionPane.YES_OPTION) {
			return; //
		}
		try {
			UIUtil.executeBatchByDS(null, new String[]{"delete from "+ billVO.getSaveTableName() +" where id = "+ billVO.getStringValue("id")});
			billList_riskeval.removeSelectedRow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void onAddRiskApply() {
		BillCardPanel cardPanel = new BillCardPanel(billList_riskeval.templetVO); //创建一个卡片面板
		cardPanel.setLoaderBillFormatPanel(billList_riskeval.getLoaderBillFormatPanel()); //将列表的BillFormatPanel的句柄传给卡片
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置

		BillCardDialog dialog = new BillCardDialog(billList_riskeval, billList_riskeval.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		dialog.setVisible(true); //显示卡片窗口
		if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = billList_riskeval.newRow(false); //
			billList_riskeval.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			billList_riskeval.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			billList_riskeval.setSelectedRow(li_newrow); //
		}
	}

	private void onEditRiskApply() {
		BillVO billVO = billList_riskeval.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if(billVO.getStringValue("wfprinstanceid")!=null && !billVO.getStringValue("wfprinstanceid").equals("")){
			MessageBox.show(this, "此记录已提交不可修改!!!"); //
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(billList_riskeval.templetVO);
		cardPanel.setLoaderBillFormatPanel(billList_riskeval.getLoaderBillFormatPanel());
		cardPanel.setBillVO(billVO); //

		BillCardDialog dialog = new BillCardDialog(billList_riskeval, billList_riskeval.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			billList_riskeval.setBillVOAt(billList_riskeval.getSelectedRow(), dialog.getBillVO(), false); //
			billList_riskeval.setRowStatusAs(billList_riskeval.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
		}
	}
}
