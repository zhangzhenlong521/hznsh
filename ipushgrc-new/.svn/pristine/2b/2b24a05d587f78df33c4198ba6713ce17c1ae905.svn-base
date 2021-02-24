package com.pushworld.icase.ui.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 排查问题整改
 * @author zane
 *
 */
public class DetectionQuestionReformPanel extends AbstractWorkPanel implements BillListSelectListener, ActionListener {

	private static final long serialVersionUID = -6909843946321417628L;
	
	private org.apache.log4j.Logger log = WLTLogger.getLogger(DetectionQuestionReformPanel.class);
	
	private BillListPanel listPanel;
	private BillListPanel childListPanel;
	private WLTButton btn_confirm,btn_add,btn_edit,btn_show;
	private BillVO selVO;

	@Override
	public void initialize() {
		
		WLTSplitPane main_splitpane = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		listPanel = new BillListPanel("CP_QUESTION_CODE2");
		
		WLTButton case_look = new WLTButton("浏览/打印");
		case_look.addActionListener(new CasePreventionQuestionOperateListener(listPanel, "CP_QUESTION_CODE2", 4, "cp_implement_id", null,null, null, "案防排查"));
		listPanel.addBillListButton(case_look);
		
		listPanel.repaintBillListButton();
		listPanel.addBillListSelectListener(this);
		
		listPanel.setDataFilterCustCondition("channels='案防排查'");
	                                      	
		childListPanel = new BillListPanel("CP_ABARBEITUNGMENU_CODE2");
		
		btn_confirm = new WLTButton("问题确认");
		btn_confirm.addActionListener(this);
		childListPanel.addBillListButton(btn_confirm);
		
	
		btn_add= new WLTButton("问题整改措施");
		btn_add.addActionListener(this);
		childListPanel.addBillListButton(btn_add);
		
	
		btn_edit=new WLTButton("整改完成");
		btn_edit.addActionListener(this);
		childListPanel.addBillListButton(btn_edit);
	
		
		
		
		btn_show=WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		childListPanel.addBillListButton(btn_show);
				
		childListPanel.addBillListSelectListener(this);
		childListPanel.repaintBillListButton();
		
		
		btn_confirm.setEnabled(false);
		btn_add.setEnabled(false);
		
		main_splitpane.add(listPanel, 1);
		main_splitpane.add(childListPanel, 2);
		this.add(main_splitpane);
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if(_event.getBillListPanel()==listPanel){  // 问题
			selVO = listPanel.getSelectedBillVO();
			childListPanel.QueryDataByCondition("question_id="+listPanel.getSelectedBillVO().getStringValue("id"));
			String state=selVO.getStringValue("STATE");
			if(state.equals("未确认")||state.equals("已下发")){
				btn_confirm.setEnabled(true);
				btn_add.setEnabled(false);
			}else{
				btn_confirm.setEnabled(false);
				btn_add.setEnabled(true);
			}
			
			
		}else if(_event.getBillListPanel()==childListPanel){ // 整改
			
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj==btn_confirm){
			onConfirm();
		}else if(obj==btn_add){
			onAdd();
		}else if(obj==btn_edit){
			onEdit();
		}
	}
 
	private void onEdit() {
		final BillVO vo = childListPanel.getSelectedBillVO();
		if(vo==null){
			MessageBox.show(childListPanel,"请选择整改记录！");
			return;
		}
		
		if(vo.getStringValue("state").equals("已整改")){
			
			MessageBox.show(childListPanel,vo.getStringValue("state")+"！");
			return;
			
		}
		
		
		final BillCardDialog dialog = new BillCardDialog(childListPanel, "问题整改", "CP_ABARBEITUNGMENU_CODE2", 600, 700, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.maxToScreenSize();
		dialog.billcardPanel.setBillVO(vo);
		dialog.billcardPanel.setValueAt("state", new ComBoxItemVO("已整改","已整改","已整改"));
		
		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.setEditableByEditInit();
		dialog.billcardPanel.setEditable("state", false);
		dialog.getBtn_confirm().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!dialog.billcardPanel.checkValidate()){
					return;
				}
				
				String abarbeitungfinishdate = selVO.getStringValue("abarbeitungfinishdate");
				Calendar calendar1 = CalendarUtils.formatYearMonthDayToDate(abarbeitungfinishdate);
				String abarbeitungtime = dialog.billcardPanel.getBillVO().getStringValue("abarbeitungtime");
				Calendar calendar2 = CalendarUtils.formatYearMonthDayToDate(abarbeitungtime);
				
				String abarbeitungcause = dialog.billcardPanel.getBillVO().getStringValue("abarbeitungcause");
				if(calendar1.getTimeInMillis()-calendar2.getTimeInMillis()<0){
					if(!WLTStringUtils.hasText(abarbeitungcause)){
						MessageBox.show(dialog, "整改情况录入时间晚于违规问题的整改完成日期，必须填写整改延期原因！");
						return;
					}
				}
				
				String state = dialog.billcardPanel.getBillVO().getComBoxItemVOValue("state").getId();
				if("已整改".equals(state) || "无法整改".equals(state)){
					if(!MessageBox.confirm(dialog, "确认"+state+"问题？")){
						return;
					}
				}
				
				List<String> _sqllist = new ArrayList<String>();
				_sqllist.add(dialog.billcardPanel.getUpdateDataSQL());
				_sqllist.add("update cp_question set reform_state='"+state+"' where id="+selVO.getStringValue("id"));
				try {
					UIUtil.executeBatchByDS(null, _sqllist);
					dialog.dispose();
					
					if("已整改".equals(state) || "无法整改".equals(state)){
						listPanel.refreshData();
						childListPanel.clearTable();
					}else{
						listPanel.refreshCurrSelectedRow();
						childListPanel.QueryDataByCondition("question_id="+selVO.getStringValue("id"));
					}
					MessageBox.show(childListPanel,"操作成功！");
				} catch (WLTRemoteException ex) {
					MessageBox.show(childListPanel,"操作失败");
					log.error(ex);
				} catch (Exception ex) {
					MessageBox.show(childListPanel,"操作失败");
					log.error(ex);
				}
				
			}
		});
		dialog.setSaveBtnVisiable(false);
		dialog.setVisible(true);
		
		
	}

	private void onAdd() {
		final BillVO vo = childListPanel.getSelectedBillVO();

		final BillCardDialog dialog = new BillCardDialog(childListPanel, "问题整改", "CP_ABARBEITUNGMENU_CODE2", 600, 700, WLTConstants.BILLDATAEDITSTATE_INSERT);
		
		dialog.maxToScreenSize();
		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.billcardPanel.setEditableByInsertInit();
		dialog.billcardPanel.setValueAt("channels", new StringItemVO(selVO.getStringValue("channels")));
		dialog.billcardPanel.setValueAt("question_id", new StringItemVO(selVO.getStringValue("id")));
		dialog.billcardPanel.setValueAt("question", new StringItemVO(selVO.getStringValue("questioname")));
		
		
			
		dialog.getBtn_confirm().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!dialog.billcardPanel.checkValidate()){
					return;
				}
				
				String abarbeitungfinishdate = selVO.getStringValue("abarbeitungfinishdate");
				Calendar calendar1 = CalendarUtils.formatYearMonthDayToDate(abarbeitungfinishdate);
				String abarbeitungtime = dialog.billcardPanel.getBillVO().getStringValue("abarbeitungtime");
				Calendar calendar2 = CalendarUtils.formatYearMonthDayToDate(abarbeitungtime);
				
				String abarbeitungcause = dialog.billcardPanel.getBillVO().getStringValue("abarbeitungcause");
				if(calendar1.getTimeInMillis()-calendar2.getTimeInMillis()<0){
					if(!WLTStringUtils.hasText(abarbeitungcause)){
						MessageBox.show(dialog, "整改情况录入时间晚于违规问题的整改完成日期，必须填写整改延期原因！");
						return;
					}
				}
				
				String state = dialog.billcardPanel.getBillVO().getComBoxItemVOValue("state").getId();
				if("已整改".equals(state) || "无法整改".equals(state)){
					if(!MessageBox.confirm(dialog, "确认"+state+"问题？")){
						return;
					}
				}
				
				List<String> _sqllist = new ArrayList<String>();
				if(null==vo){
					_sqllist.add(dialog.billcardPanel.getInsertSQL());
				}else{
					_sqllist.add(dialog.billcardPanel.getUpdateDataSQL());
				}
				_sqllist.add("update cp_question set reform_state='"+state+"' where id="+selVO.getStringValue("id"));
				try {
					UIUtil.executeBatchByDS(null, _sqllist);
					dialog.dispose();
					
					if("已整改".equals(state) || "无法整改".equals(state)){
						listPanel.refreshData();
						childListPanel.clearTable();
					}else{
						listPanel.refreshCurrSelectedRow();
						childListPanel.QueryDataByCondition("question_id="+selVO.getStringValue("id"));
					}
					MessageBox.show(childListPanel,"保存成功！");
				} catch (WLTRemoteException ex) {
					MessageBox.show(childListPanel,"保存失败");
					log.error(ex);
				} catch (Exception ex) {
					MessageBox.show(childListPanel,"保存失败");
					log.error(ex);
				}
				
			}
		});
		dialog.setSaveBtnVisiable(false);
		dialog.setVisible(true);
		
	}

	private void onConfirm(){
		if(getMessage()){
			return;
		}
		String reform_state = selVO.getStringValue("reform_state");
		
		if("已下发".equals(reform_state)){
			if(!MessageBox.confirm(listPanel, "是否确认问题属实?")){
				return;
			}
			
			List<String> _sqllist = new ArrayList<String>();
			try {
				_sqllist.add("update cp_question set STATE='已确认',questiocfdate='"+UIUtil.getCurrTime()+"' where id="+selVO.getStringValue("id"));
				
				UIUtil.executeBatchByDS(null, _sqllist);
				listPanel.refreshCurrSelectedRow();
				childListPanel.QueryDataByCondition("question_id="+selVO.getStringValue("id"));
				
				btn_confirm.setEnabled(false);
				btn_add.setEnabled(true);
				MessageBox.show(childListPanel, "问题已确认！");
			} catch (Exception e1) {
				log.error(e1);
				MessageBox.show(childListPanel, "问题确认失败！");
			}
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


}
