package com.pushworld.icase.ui.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.ObjectUtils;
import cn.com.infostrategy.bs.common.WLTStringUtils;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.AbstractBillListQueryCallback;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 排查问题维护
 * @author zane
 *  ning 
 */

public class DetectionQuestionMaintainPanel extends AbstractWorkPanel implements ActionListener,BillListSelectListener,BillListHtmlHrefListener {


	private static final long serialVersionUID = -6909843946321417628L;
	private Logger log = WLTLogger.getLogger(DetectionQuestionMaintainPanel.class);
	private BillListPanel listPanel, childListPanel;
	private WLTButton btn_submit;
	private WLTButton question_add, question_edit, question_del, question_look;
	private BillVO selVO;
	private WLTButton btn_duty;
	private WLTButton btn_chenk_begin;
	private WLTButton btn_chenk_end;
	public void initialize() {
		this.setLayout(new BorderLayout());
		WLTSplitPane main_splitpane = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT);
		listPanel = new BillListPanel("CP_IMPLEMENT_CODE2");
		btn_chenk_begin=new WLTButton("排查实施开始");
		btn_chenk_begin.addActionListener(this);
		listPanel.addBillListButton(btn_chenk_begin);
		btn_chenk_end=new WLTButton("排查实施结束");
		btn_chenk_end.addActionListener(this);
		listPanel.addBillListButton(btn_chenk_end);
		listPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		
		listPanel.repaintBillListButton();
		listPanel.addBillListSelectListener(this);
		listPanel.addBillListHtmlHrefListener(this);
		
		String deptid = ClientEnvironment.getCurrLoginUserVO().getPKDept();
		
		Map<String, String> roleMap = ClientEnvironment.getCurrLoginUserVO().getRoleMap();
		//AR001-案防管理岗（总行案防管理部）;AR002-总行案防管理部门负责人
		if(!(roleMap.containsKey("AR001") || roleMap.containsKey("AR002"))){
			listPanel.setDataFilterCustCondition("channels='案防排查' and (check_dept="+deptid+" or assistance_dept like '%;"+deptid+";%')");
		}else{
			listPanel.setDataFilterCustCondition("channels='案防排查'");
		}
		listPanel.setListQueryCallback(new MyAbstractBillListQueryCallback());
		
		childListPanel = new BillListPanel("CP_QUESTION_CODE1");
		
		question_add = new WLTButton("问题录入");	
		question_add.addActionListener(this);
		
		
		childListPanel.addBillListButton(question_add);
		
		question_edit = new WLTButton("问题修改");

		question_edit.addActionListener(this);
		childListPanel.addBillListButton(question_edit);
		
		question_del = new WLTButton("问题删除");

		question_del.addActionListener(this);
		childListPanel.addBillListButton(question_del);
		
		btn_submit = new WLTButton("下发整改");
		btn_submit.addActionListener(this);
		childListPanel.addBillListButton(btn_submit);
		
		btn_duty = new WLTButton("直接问责");
		btn_duty.addActionListener(this);
//		childListPanel.addBillListButton(btn_duty);
	
		
		
		question_look = new WLTButton("浏览/打印");

		question_look.addActionListener(this);
		childListPanel.addBillListButton(question_look);
		
		childListPanel.repaintBillListButton();
		childListPanel.addBillListSelectListener(this);
		
		// 默认按钮不可编辑
		question_add.setEnabled(false);
		question_edit.setEnabled(false);
		question_del.setEnabled(false);
		btn_submit.setEnabled(false);
		btn_duty.setEnabled(false);
		
		main_splitpane.add(listPanel, 1);
		main_splitpane.add(childListPanel, 2);
		this.add(main_splitpane,BorderLayout.CENTER);
	}

	
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(e.getSource()==btn_chenk_begin){
			onCheckBegin();
		}else if(e.getSource()==btn_chenk_end){
			onCheckEnd();
		}else if(obj==btn_submit){
			onSubmit();
		}else if(obj==question_add){
			onQuestionAdd();
		}else if(obj==question_edit){
			onQuestionEdit();	
		}else if(obj==question_del){
			onQuestionDel();
		}else if(obj==question_look){
			onQuestionLook();
		}else if(obj==btn_duty){
			onDuty();
		}
	}
	
	
	
	private void onCheckEnd() {
		if(listPanel.getSelectedBillVO()==null){
			MessageBox.showSelectOne(listPanel);
			return;
		}
		BillVO childVO=listPanel.getSelectedBillVO();
		final BillCardDialog dialog = new BillCardDialog(listPanel, "检查实施", "CP_IMPLEMENT_CODE1", 600, 700, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.queryDataByCondition("id="+childVO.getStringValue("id"));
		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.setEditableByEditInit();
		
		dialog.billcardPanel.setValueAt("state", new StringItemVO("实施结束"));
		// 计算检查结果信息
		
		int questions=childListPanel.getAllBillVOs().length;
		if(questions>0){
			dialog.billcardPanel.setValueAt("isfandquestion", new ComBoxItemVO("是","是","是"));
		}else{
			dialog.billcardPanel.setValueAt("isfandquestion", new ComBoxItemVO("否","否","否"));
		}

		dialog.setSaveBtnVisiable(false);
		dialog.setVisible(true);
		if(dialog.getCloseType()==1){
			MessageBox.show(dialog,"排查实施已结束！");
			
			btn_chenk_end.setEnabled(false);
			listPanel.refreshCurrSelectedRow();
			question_add.setEnabled(false);
			question_edit.setEnabled(false);
			question_del.setEnabled(false);
			
		}
		
		
		
		
		
	}

	
	/**
	 * 
	 * 检查通知下发
	 */
	private void onCheckBegin() {
		if(listPanel.getSelectedBillVO()==null){
			MessageBox.showSelectOne(listPanel);
			return;
		}
		BillVO childVO=listPanel.getSelectedBillVO();
		final BillCardDialog dialog = new BillCardDialog(listPanel, "检查实施", "CP_IMPLEMENT_CODE1", 600, 700, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.queryDataByCondition("id="+childVO.getStringValue("id"));
		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.setEditableByEditInit();
		
		//隐藏检查结束时填写的信息
		String[] strings=new String[]{"examinebusinestrokecoun","examinebusinemoney","isfandquestion"};
		dialog.billcardPanel.setVisiable(strings, false);
		
		
		dialog.billcardPanel.setValueAt("state", new StringItemVO("实施中"));
		String implementdate = dialog.billcardPanel.getBillVO().getStringValue("implementdate");
		if(!WLTStringUtils.hasLength(implementdate)){
			try {
				dialog.billcardPanel.setValueAt("implementdate", new StringItemVO(UIUtil.getServerCurrDate()));
				dialog.billcardPanel.setValueAt("implementpeople", new RefItemVO(ClientEnvironment.getCurrLoginUserVO().getId(), ClientEnvironment.getCurrLoginUserVO().getCode(), ClientEnvironment.getCurrLoginUserVO().getName()));
			} catch (Exception e) {
				log.error(e);
			}
		}

		dialog.setSaveBtnVisiable(false);
		dialog.setVisible(true);
		if(dialog.getCloseType()==1){
			MessageBox.show(dialog,"排查实施已开始！");
			
			listPanel.refreshCurrSelectedRow();
			
			btn_chenk_begin.setEnabled(false);
			
			question_add.setEnabled(true);
			question_edit.setEnabled(true);
			question_del.setEnabled(true);
			btn_submit.setEnabled(true);
			btn_duty.setEnabled(true);
		}
		
		
		
		
		
	}

	private void onQuestionAdd(){
		if(getMessage()){
			return;
		}
		
		String byestingdept = selVO.getRefItemVOValue("byestingdept").getId();
		CasePreventionQuestionOperateListener co=new CasePreventionQuestionOperateListener(childListPanel, "CP_QUESTION_CODE1_1", 1, "cp_implement_id",  selVO.getStringValue("id"),selVO.getStringValue("PROJECTAPPROVAL_AUDIT_ID"), byestingdept, "案防排查");
		co.actionPerformed(null);
		if(co.getClicksCount()==1){
			System.out.println(">>>>>>>>>>>>>>>>>>>");
			listPanel.refreshCurrSelectedRow();
		}
	}
	
	private void onQuestionEdit(){
		if(getMessage()){
			return;
		}
		
		String byestingdept = selVO.getRefItemVOValue("byestingdept").getId();
		new CasePreventionQuestionOperateListener(childListPanel, "CP_QUESTION_CODE1_1", 2, "cp_implement_id",  selVO.getStringValue("id"),selVO.getStringValue("PROJECTAPPROVAL_AUDIT_ID"), byestingdept, "案防排查").actionPerformed(null);
	}
	
	private void onQuestionDel(){
		if(getMessage()){
			return;
		}
		
		String byestingdept = selVO.getRefItemVOValue("byestingdept").getId();
		new CasePreventionQuestionOperateListener(listPanel,"CP_QUESTION_CODE1_1", 3, "cp_implement_id",  selVO.getStringValue("id"),selVO.getStringValue("PROJECTAPPROVAL_AUDIT_ID"), byestingdept, "案防排查").actionPerformed(null);
	}
	
	private void onQuestionLook(){
		if(getMessage()){
			return;
		}
		
		String byestingdept = selVO.getRefItemVOValue("byestingdept").getId();
		new CasePreventionQuestionOperateListener(childListPanel, "CP_QUESTION_CODE1_1", 4, "cp_implement_id",  selVO.getStringValue("id"),selVO.getStringValue("PROJECTAPPROVAL_AUDIT_ID"), byestingdept, "案防排查").actionPerformed(null);
	}

	private void onSubmit() {
		BillVO vo = childListPanel.getSelectedBillVO();
		if(null==vo){
			MessageBox.showSelectOne(childListPanel);
			return;
		}
		
		String state = vo.getStringValue("reform_state");
		if("已下发".equals(state)){
			MessageBox.show(childListPanel, "问题整改已下发!");
			return;
		}
		
		try {
			
			List<String> _sqllist = new ArrayList<String>();
			if(!MessageBox.confirm(childListPanel, "是否下发整改？")){
				return;
			}
			

//			if(!"0".equals(count)){
//				_sqllist.add("update cp_case set reform_state='待整改',ability_state='待问责' where id in (select id from cp_case where cp_implement_id="+selVO.getStringValue("id")+")");
//			}
//			_sqllist.add("update cp_implement set state='已实施', q_reform_state='待整改', q_ability_state='待问责',c_reform_state='"+("0".equals(count1)?"无案件":"待整改")+"', c_ability_state='"+("0".equals(count1)?"无案件":"待问责")+"' where id="+selVO.getStringValue("id"));
			_sqllist.add("update cp_question set ability_state='待问责', reform_state='已下发',state='已下发' where id="+vo.getStringValue("id"));
//			_sqllist.add("update cp_question set state='待整改' where id in (select id from cp_question where cp_implement_id="+selVO.getStringValue("id")+")");
			UIUtil.executeBatchByDS(null, _sqllist);
			childListPanel.refreshCurrSelectedRow();
			question_edit.setEnabled(false);
			question_del.setEnabled(false);
			btn_duty.setEnabled(true);
			String accepter = vo.getStringValue("abarbeitunguser");
			String mailneirong=TBUtil.getTBUtil().getSysOptionStringValue("马上消费合规检查邮件内容-下发整改", null);
			String email=UIUtil.getStringValueByDS(null,"select email from pub_user where id='"+accepter+"'");
			String tile=TBUtil.getTBUtil().getSysOptionStringValue("合规检查邮件主题", null);
//			new SendmailUtil(email,tile,mailneirong);
			MessageBox.show(childListPanel, "下发整改成功！");
		} catch (WLTRemoteException e) {
			log.error(e);
			MessageBox.show(childListPanel, "下发整改失败！");
		} catch (Exception e) {
			log.error(e);
			MessageBox.show(childListPanel, "下发整改失败！");
		}
	}

	private void onInputCase() {
		if(getMessage()){
			return;
		}
//		int index=listPanel.getSelectedRow();
//		String state = selVO.getStringValue("state");
		
		final BillListDialog listdialog = new BillListDialog(listPanel, "案件录入", "CP_CASE_CODE2");
		listdialog.maxToScreenSizeBy1280AndLocationCenter();
		listdialog.getBilllistPanel().setDataFilterCustCondition("cp_implement_id="+selVO.getStringValue("id"));
		String byestingdept = selVO.getRefItemVOValue("byestingdept").getId();
		
//		if("实施中".equals(state)){
			WLTButton case_add = new WLTButton("新增");
			case_add.addActionListener(new CasePreventionCaseOperateListener(listdialog.getBilllistPanel(), "CP_CASE_CODE2", 1, "cp_implement_id", selVO.getStringValue("id"), selVO.getStringValue("projectapproval_audit_id"), false, byestingdept));
			listdialog.getBilllistPanel().addBillListButton(case_add);
			
			WLTButton case_edit = new WLTButton("修改");
			case_edit.addActionListener(new CasePreventionCaseOperateListener(listdialog.getBilllistPanel(), "CP_CASE_CODE2", 2, "cp_implement_id", selVO.getStringValue("id"), selVO.getStringValue("projectapproval_audit_id"), false, byestingdept));
			listdialog.getBilllistPanel().addBillListButton(case_edit);
			
			WLTButton case_del = new WLTButton("删除");
			case_del.addActionListener(new CasePreventionCaseOperateListener(listdialog.getBilllistPanel(), "CP_CASE_CODE2", 3, "cp_implement_id", selVO.getStringValue("id"), selVO.getStringValue("projectapproval_audit_id"), false, byestingdept));
			listdialog.getBilllistPanel().addBillListButton(case_del);
//		}
		
		WLTButton case_look = new WLTButton("浏览/打印");
		case_look.addActionListener(new CasePreventionCaseOperateListener(listdialog.getBilllistPanel(), "CP_CASE_CODE2", 4, "cp_implement_id", selVO.getStringValue("id"), selVO.getStringValue("projectapproval_audit_id"), false, byestingdept));
		listdialog.getBilllistPanel().addBillListButton(case_look);
		listdialog.getBilllistPanel().repaintBillListButton();
		listdialog.getBilllistPanel().QueryDataByCondition(null);
		listdialog.getBtn_confirm().setVisible(false);
		listdialog.setVisible(true);
		
		if(listdialog.getCloseType()!=1){
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

	
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if(_event.getBillListPanel()==this.listPanel){  // 实施计划
			
			BillVO vo = _event.getCurrSelectedVO();
			childListPanel.QueryDataByCondition("cp_implement_id="+vo.getStringValue("id"));
			String reform_state = vo.getStringValue("state");
			boolean isedit;
			if("实施中".equals(reform_state)){
				isedit=true;
				// 
				btn_chenk_end.setEnabled(true);
				btn_chenk_begin.setEnabled(false);
				question_add.setEnabled(isedit);
				question_edit.setEnabled(isedit);
				question_del.setEnabled(isedit);
				btn_submit.setEnabled(isedit);
				btn_duty.setEnabled(isedit);
			}else if("实施结束".equals(reform_state)){
				isedit=true;
				//
				
				btn_chenk_begin.setEnabled(false);
				btn_chenk_end.setEnabled(false);
				question_add.setEnabled(false);
				question_edit.setEnabled(false);
				question_del.setEnabled(false);
				btn_submit.setEnabled(isedit);
				btn_duty.setEnabled(isedit);
			}else{
				isedit=false;
				btn_chenk_begin.setEnabled(true);
				btn_chenk_end.setEnabled(false);
				question_add.setEnabled(isedit);
				question_edit.setEnabled(isedit);
				question_del.setEnabled(isedit);
				btn_submit.setEnabled(isedit);
				btn_duty.setEnabled(isedit);
			}
		}else{
			
		}
	
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		selVO = listPanel.getSelectedBillVO();
		if(_event.getItemkey().equals("temp1")){
			final BillListDialog listdialog = new BillListDialog(listPanel, "问题", "CP_QUESTION_CODE1");
			listdialog.maxToScreenSizeBy1280AndLocationCenter();
			listdialog.getBilllistPanel().setDataFilterCustCondition("cp_implement_id="+listPanel.getSelectedBillVO().getStringValue("id"));
			listdialog.getBilllistPanel().getBillListBtnPanel().setVisible(false);
			listdialog.getBtn_confirm().setVisible(false);
			listdialog.getBilllistPanel().QueryDataByCondition(null);
			WLTButton case_look = new WLTButton("浏览/打印");
			case_look.addActionListener(new CasePreventionQuestionOperateListener(listdialog.getBilllistPanel(), "CP_QUESTION_CODE1", 4, "cp_implement_id", selVO.getStringValue("id"),selVO.getStringValue("projectapproval_audit_id"), null, "案防排查"));
			listdialog.getBilllistPanel().addBillListButton(case_look);
			listdialog.getBilllistPanel().repaintBillListButton();
			listdialog.setVisible(true);
		}else if(_event.getItemkey().equals("temp2")){
			final BillListDialog listdialog = new BillListDialog(listPanel, "案件", "CP_CASE_CODE2");
			listdialog.maxToScreenSizeBy1280AndLocationCenter();
			listdialog.getBilllistPanel().setDataFilterCustCondition("cp_implement_id="+listPanel.getSelectedBillVO().getStringValue("id"));
			listdialog.getBilllistPanel().QueryDataByCondition(null);
			listdialog.getBilllistPanel().getBillListBtnPanel().setVisible(false);
			listdialog.getBtn_confirm().setVisible(false);
			WLTButton case_look = new WLTButton("浏览/打印");
			case_look.addActionListener(new CasePreventionQuestionOperateListener(listdialog.getBilllistPanel(), "CP_CASE_CODE2", 4, "cp_implement_id", selVO.getStringValue("id"),selVO.getStringValue("projectapproval_audit_id"), null, "案防排查"));
			listdialog.getBilllistPanel().addBillListButton(case_look);
			listdialog.getBilllistPanel().repaintBillListButton();
			listdialog.setVisible(true);
		}else if(_event.getItemkey().equals("add_case")){
			onInputCase();
		}
	
	}
	
	class MyAbstractBillListQueryCallback extends AbstractBillListQueryCallback{

		@Override
		public void queryCallback() {
			try {
				BillVO[] vos = listPanel.getAllBillVOs();
				int index = 0;
				for(BillVO vo : vos){
					String temp2 = UIUtil.getStringValueByDS(null, "select count(1) from CP_CASE where cp_implement_id="+vo.getStringValue("id"));
					HashVO[] temp1 = UIUtil.getHashVoArrayByDS(null, "select count(1) temp1, sum(questionstrokecount) questionstrokecount from CP_QUESTION where cp_implement_id="+vo.getStringValue("id"));
					
					listPanel.setValueAt(new StringItemVO(temp2), index, "temp2");
					if(ObjectUtils.isEmpty(temp1)){
						listPanel.setValueAt(new StringItemVO("0"), index, "temp1");
						listPanel.setValueAt(new StringItemVO("0"), index, "questionstrokecount");
					}else{
						listPanel.setValueAt(new StringItemVO(temp1[0].getStringValue("temp1")), index, "temp1");
						listPanel.setValueAt(new StringItemVO(temp1[0].getStringValue("questionstrokecount")), index, "questionstrokecount");
					}
					
					index+=1;
				}
			} catch (WLTRemoteException e) {
				log.error(e);
			} catch (Exception e) {
				log.error(e);
			}
		}
		
		
	}
	
	private void onDuty() {
		new AccountabilityOperateListener(childListPanel, "CP_ILLEGAL_EMPLOYEE_CODE3", "排查问题问责", "案防排查", listPanel.getSelectedBillVO().getRefItemVOValue("check_dept").getId()).actionPerformed(null);
	}
	
	public BillListPanel getBillListPanel(){
		return  listPanel;
		
	}
	public BillListPanel getChildBillListPanel(){
		return  childListPanel;
		
	}
}
