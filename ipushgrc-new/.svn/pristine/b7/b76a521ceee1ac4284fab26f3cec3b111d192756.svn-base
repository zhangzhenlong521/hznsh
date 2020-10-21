package com.pushworld.icase.ui.p010;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillHtmlPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessDialog;

public class CasePreventionCaseOperateListener extends CasePreventionAbstractListener {
	
	private org.apache.log4j.Logger log = WLTLogger.getLogger(CasePreventionCaseOperateListener.class);
	
	private WLTButton btn_employee_add, btn_employee_edit, btn_employee_del, btn_employee_look;
	
	private IllegalEmployeeUtil employee;
	
	private BillListPanel listpanel3;
	
	private WLTButton btn_confirm, btn_cancel, btn_process;
	
	private WLTRadioPane rpanel;
	
	private String relationname;
	private String relationvalue;
	private String projectapproval_audit_id;
	private String deptid;
	
	private boolean isProcess;
	
	/**
	 * 
	 * @param listPanel
	 * @param templetcode
	 * @param CASE_ID
	 * @param case_type
	 * @param status 1:new 2:edit 3:del 4:look
	 */
	public CasePreventionCaseOperateListener(BillListPanel listPanel, String templetcode, int status, String relationname, String relationvalue, String projectapproval_audit_id, boolean isProcess, String deptid){
		this.case_self_list = listPanel;
		this.templetcode = templetcode;
		this.status = status;
		this.relationname = relationname;
		this.relationvalue = relationvalue;
		this.projectapproval_audit_id = projectapproval_audit_id;
		this.isProcess = isProcess;
		this.deptid = deptid;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = null;
		if(null!=e){
			obj = e.getSource();
		}
		if(null!=obj && obj==btn_process){   // 主诉新增案件卡片中   流程发起按钮
			onSubMitPrecess();
		}else if(null!=obj && obj==btn_employee_add){
			employee.onEmployeeAdd();
		}else if(null!=obj && obj==btn_employee_edit){
			employee.onEmployeeEdit();
		}else if(null!=obj && obj==btn_employee_del){
			employee.onEmployeeDel();
		}else if(null!=obj && obj==btn_employee_look){
			employee.onEmployeeLook();
		}else if(null!=obj && obj==btn_confirm){
			if(!cardPanel.checkValidate()){
				return;
			}
			
			String casehappendate = cardPanel.getBillVO().getStringValue("casehappendate");
			String casefanddate   = cardPanel.getBillVO().getStringValue("casefanddate");
			BillVO[] vos =listpanel3.getBillVOs();
			if(WLTStringUtils.hasText(casehappendate) && WLTStringUtils.hasText(casefanddate)){
				Calendar happend = CalendarUtils.formatYearMonthDayToDate(casehappendate);
				Calendar fangdate  = CalendarUtils.formatYearMonthDayToDate(casefanddate);
				if(fangdate.getTimeInMillis()-happend.getTimeInMillis()<0){
					MessageBox.show(cardPanel,"案件发现日期不能小于案件发生日期！");
					return;
				}
			}
			
			if(vos.length<1){
				MessageBox.show(cardPanel, "涉案员工数据不能为空！");
				return;
			}
			
//			double involvemoney = Double.parseDouble(cardPanel.getBillVO().getStringValue("involvemoney"));
//			double involvemoneyD = 0;
//			for(BillVO vo : vos){
//				double illegal_money = Double.parseDouble(vo.getStringValue("illegal_money"));
//				involvemoneyD = DoubleUtil.add(involvemoneyD, illegal_money);
//			}
//			
//			if(involvemoney-involvemoneyD!=0){
//				MessageBox.show(caseSelfDialog, "所有涉案员工的涉案金额不等于案件的涉案金额！");
//				return;
//			}
			onConfirm(null, null);
		}else if(null!=obj && obj==btn_cancel){
			onCancel();
		}else{
			
			// 
			cardPanel=new BillCardPanel(templetcode);
			if(status!=1){
				caseSelfBillVO=case_self_list.getSelectedBillVO();
				if(caseSelfBillVO==null){
					MessageBox.showSelectOne(case_self_list);
					return;
				}
				cardPanel.setBillVO(caseSelfBillVO);
			}
			WLTTabbedPane tab_panel=new WLTTabbedPane();
			
			if(status==1){
				cardPanel.insertRow();
				cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
				cardPanel.setEditableByInsertInit();
				if(WLTStringUtils.hasText(relationname)){
					cardPanel.setValueAt(relationname, new StringItemVO(relationvalue));
				}
				
				if(WLTStringUtils.hasText(projectapproval_audit_id)){
					cardPanel.setValueAt("relevanceprojectnumber", new StringItemVO(projectapproval_audit_id));
				}
			}else if(status==2){
				cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				cardPanel.setEditableByEditInit();
			}else if(status==4){
				cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
			}else if(status==3){
				if(!MessageBox.confirm(case_self_list, "确认删除案件？")){
					return;
				}
				
				List<String> _sqllist = new ArrayList<String>();
				
				try {
					_sqllist.add("delete from cp_case where id="+caseSelfBillVO.getStringValue("id"));
					_sqllist.add("delete from CP_ILLEGAL_EMPLOYEE where id in (select id from CP_ILLEGAL_EMPLOYEE where type='案件报告' and parentid="+caseSelfBillVO.getStringValue("id")+")");
					UIUtil.executeBatchByDS(null, _sqllist);
					MessageBox.show(case_self_list, "删除成功！");
					case_self_list.refreshData();
					return;
				} catch (WLTRemoteException ex) {
					log.error(ex);
					MessageBox.show(case_self_list, "删除失败！");
					return;
				} catch (Exception ex) {
					log.error(ex);
					MessageBox.show(case_self_list, "删除失败！");
					return;
				}
			}
			
			if(WLTStringUtils.hasText(deptid)){
				cardPanel.setValueAt("byestingdept", new StringItemVO(deptid));
			}
			
//			if(WLTStringUtils.hasText(deptid)){
				listpanel3=new BillListPanel("CP_ILLEGAL_EMPLOYEE_CODE2_1");
//			}else{
//				listpanel3=new BillListPanel("CP_ILLEGAL_EMPLOYEE_CODE2");
//			}
			
			listpanel3.getQuickQueryPanel().setVisible(false);
			if(status==1 || status==2){
				btn_employee_add = new WLTButton("新增");
				btn_employee_add.addActionListener(this);
				listpanel3.addBillListButton(btn_employee_add);
				
				btn_employee_edit = new WLTButton("修改");
				btn_employee_edit.addActionListener(this);
				listpanel3.addBillListButton(btn_employee_edit);
				
				btn_employee_del = new WLTButton("删除");
				btn_employee_del.addActionListener(this);
				listpanel3.addBillListButton(btn_employee_del);
			}
			
			btn_employee_look = new WLTButton("浏览/打印");
			btn_employee_look.addActionListener(this);
			listpanel3.addBillListButton(btn_employee_look);
			listpanel3.repaintBillListButton();
			
			if(null!=caseSelfBillVO){
				listpanel3.setDataFilterCustCondition("type='案件报告' and parentid="+caseSelfBillVO.getStringValue("id"));
				listpanel3.QueryDataByCondition(null);
			}else{
				listpanel3.setDataFilterCustCondition("type='案件报告' and parentid="+cardPanel.getBillVO().getStringValue("id"));
				listpanel3.QueryDataByCondition(null);
			}
			
			//  每个卡片增加网页浏览
			rpanel=new WLTRadioPane();
			rpanel.addTab("控件风格", cardPanel);
			BillHtmlPanel html1=new BillHtmlPanel();
			html1.loadHtml(cardPanel.getExportHtml());
			rpanel.addTab("打印预览", html1);
			
			tab_panel.addTab("案件",rpanel);
			
			tab_panel.addTab("涉案员工",listpanel3);
			
			caseSelfDialog=new BillDialog(case_self_list);
			caseSelfDialog.add(tab_panel);
			
			if(WLTStringUtils.hasText(deptid)){
				employee = new IllegalEmployeeUtil(caseSelfDialog, cardPanel, listpanel3, "CP_ILLEGAL_EMPLOYEE_CODE2_1", deptid, "案件报告");
			}else{
				employee = new IllegalEmployeeUtil(caseSelfDialog, cardPanel, listpanel3, "CP_ILLEGAL_EMPLOYEE_CODE2_1", deptid, "案件报告");
			}
			
			caseSelfDialog.maxToScreenSize();
			caseSelfDialog.locationToCenterPosition();
			caseSelfDialog.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
			caseSelfDialog.setVisible(true);
			
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());	
		if(status==1 || status==2){
			btn_confirm = new WLTButton("保存");
		}
		
		btn_cancel = new WLTButton("关闭");
		if(status==1 || status==2){
			btn_confirm.addActionListener(this); //
		}
		btn_cancel.addActionListener(this); //
		if(status==1 || status==2){
			panel.add(btn_confirm); //
		}
		
		if(isProcess && (status==1 || status==2)){
			btn_process=new WLTButton("流程发起");
			btn_process.addActionListener(this);
			panel.add(btn_process); //
		}
		
		panel.add(btn_cancel); //
		
		
		return panel;
	}
	
	private void onSubMitPrecess() {
		if(!cardPanel.checkValidate()){
			return;
		}
		
		String casehappendate = cardPanel.getBillVO().getStringValue("casehappendate");
		String casefanddate   = cardPanel.getBillVO().getStringValue("casefanddate");
		BillVO[] vos =listpanel3.getBillVOs();
		if(WLTStringUtils.hasText(casehappendate) && WLTStringUtils.hasText(casefanddate)){
			Calendar happend = CalendarUtils.formatYearMonthDayToDate(casehappendate);
			Calendar fangdate  = CalendarUtils.formatYearMonthDayToDate(casefanddate);
			if(fangdate.getTimeInMillis()-happend.getTimeInMillis()<0){
				MessageBox.show(cardPanel,"案件发现日期不能小于案件发生日期！");
				return;
			}
		}
		
		if(vos.length<1){
			MessageBox.show(cardPanel, "涉案员工数据不能为空！");
			return;
		}
		
		try {
			List<String> _sqllist = new ArrayList<String>();
			
			if(status==1){
				_sqllist.add(cardPanel.getInsertSQL());
			}else if(status==2){
				_sqllist.add(cardPanel.getUpdateSQL());
			}
			
			if(null!=updateSql){
				_sqllist.add(updateSql);
			}
			
			UIUtil.executeBatchByDS(null, _sqllist);
			
			WorkFlowProcessDialog processDialog = new WorkFlowProcessDialog(caseSelfDialog, "流程处理", cardPanel, null); //抄送!!!既然这里流程实例为空，则消息任务id、流程任务id和流程实例id三个值都为null，故标题中不要显示了【李春娟/2012-03-28】
			processDialog.setVisible(true); //
			
			caseSelfDialog.dispose();
			
			if(status==2){                                       
				case_self_list.refreshCurrSelectedRow();
			}else{
				case_self_list.refreshData();
			}
			
		} catch (Exception e) {
			log.error(e);
		}
		
	}

}
