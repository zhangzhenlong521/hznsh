package com.pushworld.icase.ui.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 全年排查计划
 * @author zane
 *
 */
public class DetectionPlanPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener{

	private static final long serialVersionUID = -6909843946321417628L;

//	private static final ActionListener ac = null;
	
	private org.apache.log4j.Logger log = WLTLogger.getLogger(DetectionPlanPanel.class);
	
	private BillListPanel listPanel;

	private WLTButton btn_add, btn_edit, btn_del;
	private WLTButton btn_submit;
	private WLTButton btn_look;
//	private BillCardDialog dialog;
	private BillVO selVO;
	
	@Override
	public void initialize() {
		listPanel = new BillListPanel("CP_INVESTIGATION_PLAN_CODE1");
		listPanel.setDataFilterCustCondition("channels='案防排查'");
		
		btn_add = new WLTButton("新增");
		btn_add.addActionListener(this);
		listPanel.addBillListButton(btn_add);
		
		btn_edit = new WLTButton("修改");
		btn_edit.addActionListener(this);
		listPanel.addBillListButton(btn_edit);
		
		btn_del = new WLTButton("删除");
		btn_del.addActionListener(this);
		listPanel.addBillListButton(btn_del);
		
		btn_submit = new WLTButton("提交");
		btn_submit.addActionListener(this);
		listPanel.addBillListButton(btn_submit);
		
		btn_look = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		listPanel.addBillListButton(btn_look);
		
		listPanel.repaintBillListButton();
		listPanel.addBillListSelectListener(this);
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj==btn_add){
			onAdd();
		}else if(obj==btn_edit){
			onEdit();
		}else if(obj==btn_del){
			onDel();
		}else if(obj==btn_submit){
			onSubmit();
		}
	}

	private void onSubmit() {
		if(getMessage()){
			return;
		}
		String state = selVO.getStringValue("state");
		if(!"草稿".equals(state)){
			MessageBox.show(listPanel, "您选择的排查计划已提交，请重新选择！");
			return;
		}
		
		int li_result = MessageBox.showOptionDialog(listPanel, "是否确认提交,请选择？", "确认", new String[] { "确认", "取消" }, 300, 120);	
		try {
			if(li_result==0){
//				CasePreventionServiceIfc casePreventionServiceIfc = (CasePreventionServiceIfc) UIUtil.lookUpRemoteService(CasePreventionServiceIfc.class);
//				String deptCorpType = ClientEnvironment.getCurrLoginUserVO().getDeptCorpType();
//				casePreventionServiceIfc.submitInvestigationPlan(selVO.getStringValue("id"), deptCorpType, "案防排查");
				UpdateSQLBuilder update=new UpdateSQLBuilder("cp_investigation_plan");
				update.setWhereCondition("id="+selVO.getStringValue("id"));
				update.putFieldValue("state","已提交");
				UIUtil.executeUpdateByDS(null,update.getSQL());
				listPanel.reload();
				MessageBox.show(listPanel, "您选择的排查计划已提交成功！");
			}
		} catch (Exception e) {
			log.error(e);
			MessageBox.show(listPanel, "您选择的排查计划提交失败！");
		}
		
	}
	
	private void onDel(){
		BillVO[] billVOs = listPanel.getSelectedBillVOs();
		if(ObjectUtils.isEmpty(billVOs)){
			MessageBox.showSelectOne(listPanel);
			return;
		}
		int li_result = MessageBox.showOptionDialog(listPanel, "您确定要删除检查计划？", "提示", new String[] { "是", "否" }, 350, 120);
		if(li_result==0){
			List<String> _sqllist = new ArrayList<String>();
			for (BillVO billVO : billVOs) {
				_sqllist.add("delete from cp_investigation_plan where id="+billVO.getStringValue("id"));
			}
			
			try {
				UIUtil.executeBatchByDS(null, _sqllist);
				MessageBox.show(listPanel, "删除检查计划成功！");
				listPanel.QueryDataByCondition(null);
			} catch (WLTRemoteException e1) {
				log.error(e1);
				MessageBox.show(listPanel, "删除检查计划失败！");
			} catch (Exception e1) {
				log.error(e1);
				MessageBox.show(listPanel, "删除检查计划失败！");
			}
		}
	}

	private void onEdit() {
		if(getMessage()){
			return;
		}
		
		final BillCardDialog dialog = new BillCardDialog(listPanel, "修改全年排查计划", "CP_INVESTIGATION_PLAN_CODE1", 600, 700, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		final String type="修改";
		dialog.billcardPanel.queryDataByCondition("id="+selVO.getStringValue("id"));
		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.setEditableByEditInit();
		dialog.getBtn_confirm().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getDate(dialog,type);
			}
		});
		dialog.setSaveBtnVisiable(false);
		dialog.setVisible(true);
		if(dialog.getCloseType()==1){
			listPanel.refreshCurrSelectedRow();
		}
	}

	private void onAdd() {
		final BillCardDialog dialog = new BillCardDialog(listPanel, "CP_INVESTIGATION_PLAN_CODE1", WLTConstants.BILLDATAEDITSTATE_INSERT);
		final String type="新增";
		dialog.billcardPanel.insertRow();
		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.billcardPanel.setEditableByInsertInit();
		
		dialog.billcardPanel.setValueAt("channels", new StringItemVO("案防排查"));
		dialog.getBtn_confirm().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getDate(dialog,type);
			}
		});
		dialog.setSaveBtnVisiable(false);
		dialog.setVisible(true);
		
		if(dialog.getCloseType()==1){			
			listPanel.refreshData();
		}
	}
	
	private boolean getMessage(){
		selVO = listPanel.getSelectedBillVO();
		if(null==selVO){
			MessageBox.showSelectOne(listPanel);
			return true;
		}	
		return false;
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO billVO =_event.getCurrSelectedVO();
		String state = billVO.getStringValue("state");
		if(billVO !=null){
			if(state.equals("已提交")){
				btn_edit.setEnabled(false);
				btn_del.setEnabled(false);
				btn_submit.setEnabled(false);
			}else{
				btn_edit.setEnabled(true);
				btn_del.setEnabled(true);
				btn_submit.setEnabled(true);
			}
		}
	}
	
	private void getDate(BillCardDialog dialog2,String type) {
		if(!dialog2.billcardPanel.checkValidate()){
			return;
		}
		
		String planstarttime = dialog2.billcardPanel.getBillVO().getStringValue("planstarttime");
		String planendtime = dialog2.billcardPanel.getBillVO().getStringValue("planendtime");
		try {
			if(WLTStringUtils.hasText(planstarttime)&&WLTStringUtils.hasText(planendtime)){
				Calendar start = CalendarUtils.formatYearMonthDayToDate(planstarttime);
				Calendar end = CalendarUtils.formatYearMonthDayToDate(planendtime);
				if(type.equals("新增")){
					if(start.getTimeInMillis()-end.getTimeInMillis()>=0){
						MessageBox.show(dialog2, "计划开始日期不能大于或等于计划结束日期！");
						return;
					}						

				
				}else if(type.equals("修改")){
					if(start.getTimeInMillis()-end.getTimeInMillis()>=0){
						MessageBox.show(dialog2, "计划开始日期不能大于或等于计划结束日期！");
						return;
					}
				}
			}
			//UIUtil.executeUpdateByDS(null, dialog2.billcardPanel.getUpdateDataSQL());
			dialog2.onSave();
			dialog2.dispose();
			
		} catch (Exception e1) {
			log.error(e1);
		}
		
		
	}

}
