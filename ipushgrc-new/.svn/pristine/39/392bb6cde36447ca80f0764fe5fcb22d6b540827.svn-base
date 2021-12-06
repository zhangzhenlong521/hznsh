package com.pushworld.icase.ui.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 排查后评价
 * @author zane
 *
 */
public class DetectionEvaluatePanel extends AbstractWorkPanel implements BillListSelectListener, ActionListener {

	private static final long serialVersionUID = -6909843946321417628L;
	
	private org.apache.log4j.Logger log = WLTLogger.getLogger(DetectionEvaluatePanel.class);
	
	private BillListPanel listPanel;
	private BillListPanel childListPanel;
	private BillVO setVo;
	private WLTButton btn_add, btn_submit;
	public void initialize() {
		WLTSplitPane main_splitpane = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		listPanel = new BillListPanel("CP_PROJECTAPPROVAL_AUDIT_CODE4");
		
		listPanel.addBillListSelectListener(this);
		
		String deptid = ClientEnvironment.getCurrLoginUserVO().getPKDept();
//		Map<String, String> roleMap = ClientEnvironment.getCurrLoginUserVO().getRoleMap();
		String[] rolecode = ClientEnvironment.getInstance().getLoginUserRoleCodes();
		String rolecodes = Arrays.toString(rolecode); 
		if(!(rolecodes.indexOf("AR001")!=-1 || rolecodes.indexOf("AR002")!=-1)){
//		if(!(roleMap.containsKey("AR001") || roleMap.containsKey("AR002"))){
			listPanel.setDataFilterCustCondition("channels='案防排查' and state='已下发' and (schemeapplyfordept="+deptid+" or assistance_dept like '%;"+deptid+";%' or byexaminedept like '%;"+deptid+";%')");
		}else{
			listPanel.setDataFilterCustCondition("channels='案防排查' and state='已下发'");
		}
	                                      	
		childListPanel = new BillListPanel("CP_QUESTION_CODE3");
		childListPanel.addBillListSelectListener(this);
		
		btn_add = new WLTButton("后评价");
		btn_add.addActionListener(this);
		childListPanel.addBillListButton(btn_add);
		
		btn_submit = new WLTButton("提交");
		btn_submit.addActionListener(this);
		childListPanel.addBillListButton(btn_submit);
		
		WLTButton case_look = new WLTButton("浏览/打印");
		case_look.addActionListener(new CasePreventionQuestionOperateListener(childListPanel, "CP_QUESTION_CODE3", 4, "cp_implement_id", null,null, null, "案防排查"));
		childListPanel.addBillListButton(case_look);
		
		childListPanel.repaintBillListButton();
		
		main_splitpane.add(listPanel, 1);
		main_splitpane.add(childListPanel, 2);
		this.add(main_splitpane);
		
//		WLTSplitPane main_splitpane = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT);
//		
//		listPanel = new BillListPanel("CP_QUESTION_CODE3");
//		
//		listPanel.repaintBillListButton();
//		listPanel.addBillListSelectListener(this);
//		
//		childListPanel = new BillListPanel("CP_CHECKING_EVALUATE_CODE1");
//		btn_add = new WLTButton("后评价");
//		btn_add.addActionListener(this);
//		childListPanel.addBillListButton(btn_add);
//		
//		//childListPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_POPINSERT));
//		childListPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_POPEDIT));
//		childListPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_DELETE));
//		childListPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
//		
//		childListPanel.repaintBillListButton();
//		main_splitpane.add(listPanel, 1);
//		main_splitpane.add(childListPanel, 2);
//		this.add(main_splitpane);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj==btn_add){
			onAdd();
		}else if(obj==btn_submit){
			onSubmit();
		}
	}
	
	private void onSubmit() {
		 setVo = childListPanel.getSelectedBillVO();
		 if(null == setVo){
			 MessageBox.showSelectOne(childListPanel);
			 return;
		 }
		 
		 String state = setVo.getStringValue("state");
		 if(!"评价中".equals(state)){
			 MessageBox.show(childListPanel, "您选择的问题不需要提交或者不能提交！");
			 return;
		 }
		 
		 if(!MessageBox.confirm(childListPanel, "您选择的问题确认已评价完成了吗？")){
			 return;
		 }
		 
		 try{
			 UIUtil.executeUpdateByDS(null, "update cp_question set state='已评价' where id="+setVo.getStringValue("id"));
			 childListPanel.refreshCurrSelectedRow();
			 btn_submit.setEnabled(false);
			 MessageBox.show(childListPanel, "排查问题提交评价成功！");
		 } catch (WLTRemoteException ex) {
			 log.error(ex);
			 MessageBox.show(childListPanel, "排查问题提交评价失败！");
		 } catch (Exception ex) {
			 log.error(ex);
			 MessageBox.show(childListPanel, "排查问题提交评价失败！");
		 }
	}

	private void onAdd() {
		 setVo = childListPanel.getSelectedBillVO();
		 if(null == setVo){
			 MessageBox.showSelectOne(childListPanel);
			 return;
		 }
		 
		 final String state = setVo.getStringValue("state");
		 
		 String count;
		 try {
			 count = UIUtil.getStringValueByDS(null, "select count(1) from CP_CHECKING_EVALUATE where cp_question_id="+setVo.getStringValue("id"));
		 } catch (WLTRemoteException e) {
			log.error(e);
			MessageBox.show(childListPanel, "系统错误！");
			return;
		 } catch (Exception e) {
			log.error(e);
			MessageBox.show(childListPanel, "系统错误！");
			return;
		 }
		 
		 final BillCardDialog dialog = new BillCardDialog(childListPanel, "CP_CHECKING_EVALUATE_CODE1", "已评价".equals(state)?WLTConstants.BILLDATAEDITSTATE_INIT:("0".equals(count)?WLTConstants.BILLDATAEDITSTATE_INSERT:WLTConstants.BILLDATAEDITSTATE_UPDATE), true);
		 if("0".equals(count)){
			 dialog.billcardPanel.insertRow();
			 dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			 dialog.billcardPanel.setEditableByInsertInit();
			 dialog.billcardPanel.setValueAt("cp_question_id", new StringItemVO(setVo.getStringValue("id")));
		 }else{
			 dialog.billcardPanel.queryDataByCondition("cp_question_id="+setVo.getStringValue("id"));
			 if(!"已评价".equals(state)){
				 dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				 dialog.billcardPanel.setEditableByEditInit();
			 }
		 }
		 
		 if(!"已评价".equals(state)){
			 dialog.getBtn_confirm().addActionListener(new ActionListener() {
			
				 @Override
				 	public void actionPerformed(ActionEvent e) {
					 if(!dialog.billcardPanel.checkValidate()){
						 return;
					 }
				
					 List<String> _sqllist = new ArrayList<String>();
					 _sqllist.add(dialog.billcardPanel.getUpdateDataSQL());
					 //已整改/已确认
					 //if("已整改".equals(state) || "已确认".equals(state)){
						 _sqllist.add("update cp_question set state='评价中' where id="+setVo.getStringValue("id"));
					// }
				
					 try{
						 UIUtil.executeBatchByDS(null, _sqllist);
						 childListPanel.refreshCurrSelectedRow();
						 dialog.dispose();
						 MessageBox.show(childListPanel, "排查问题评价成功！");
					 } catch (WLTRemoteException ex) {
						 log.error(ex);
						 MessageBox.show(childListPanel, "排查问题评价失败！");
					 } catch (Exception ex) {
						 log.error(ex);
						 MessageBox.show(childListPanel, "排查问题评价失败！");
					 }
				
				 }
			 });
		 }else{
			 dialog.getBtn_confirm().setVisible(false); 
		 }
		 dialog.setSaveBtnVisiable(false);
		 dialog.setVisible(true);
		 if(dialog.getCloseType()==1){			
		 }
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if(_event.getBillListPanel()==listPanel){
			setVo = listPanel.getSelectedBillVO();
			childListPanel.QueryDataByCondition("relevanceprojectnumber="+setVo.getStringValue("id"));
		}else if(_event.getBillListPanel()==childListPanel){
			String state = childListPanel.getSelectedBillVO().getStringValue("state");
			if("已评价".equals(state)){
				btn_submit.setEnabled(false);
			}else{
				btn_submit.setEnabled(true);
			}
					
		}
	}

}
