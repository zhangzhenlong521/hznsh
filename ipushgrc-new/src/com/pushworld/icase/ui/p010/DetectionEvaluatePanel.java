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
 * �Ų������
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
			listPanel.setDataFilterCustCondition("channels='�����Ų�' and state='���·�' and (schemeapplyfordept="+deptid+" or assistance_dept like '%;"+deptid+";%' or byexaminedept like '%;"+deptid+";%')");
		}else{
			listPanel.setDataFilterCustCondition("channels='�����Ų�' and state='���·�'");
		}
	                                      	
		childListPanel = new BillListPanel("CP_QUESTION_CODE3");
		childListPanel.addBillListSelectListener(this);
		
		btn_add = new WLTButton("������");
		btn_add.addActionListener(this);
		childListPanel.addBillListButton(btn_add);
		
		btn_submit = new WLTButton("�ύ");
		btn_submit.addActionListener(this);
		childListPanel.addBillListButton(btn_submit);
		
		WLTButton case_look = new WLTButton("���/��ӡ");
		case_look.addActionListener(new CasePreventionQuestionOperateListener(childListPanel, "CP_QUESTION_CODE3", 4, "cp_implement_id", null,null, null, "�����Ų�"));
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
//		btn_add = new WLTButton("������");
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
		 if(!"������".equals(state)){
			 MessageBox.show(childListPanel, "��ѡ������ⲻ��Ҫ�ύ���߲����ύ��");
			 return;
		 }
		 
		 if(!MessageBox.confirm(childListPanel, "��ѡ�������ȷ���������������")){
			 return;
		 }
		 
		 try{
			 UIUtil.executeUpdateByDS(null, "update cp_question set state='������' where id="+setVo.getStringValue("id"));
			 childListPanel.refreshCurrSelectedRow();
			 btn_submit.setEnabled(false);
			 MessageBox.show(childListPanel, "�Ų������ύ���۳ɹ���");
		 } catch (WLTRemoteException ex) {
			 log.error(ex);
			 MessageBox.show(childListPanel, "�Ų������ύ����ʧ�ܣ�");
		 } catch (Exception ex) {
			 log.error(ex);
			 MessageBox.show(childListPanel, "�Ų������ύ����ʧ�ܣ�");
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
			MessageBox.show(childListPanel, "ϵͳ����");
			return;
		 } catch (Exception e) {
			log.error(e);
			MessageBox.show(childListPanel, "ϵͳ����");
			return;
		 }
		 
		 final BillCardDialog dialog = new BillCardDialog(childListPanel, "CP_CHECKING_EVALUATE_CODE1", "������".equals(state)?WLTConstants.BILLDATAEDITSTATE_INIT:("0".equals(count)?WLTConstants.BILLDATAEDITSTATE_INSERT:WLTConstants.BILLDATAEDITSTATE_UPDATE), true);
		 if("0".equals(count)){
			 dialog.billcardPanel.insertRow();
			 dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			 dialog.billcardPanel.setEditableByInsertInit();
			 dialog.billcardPanel.setValueAt("cp_question_id", new StringItemVO(setVo.getStringValue("id")));
		 }else{
			 dialog.billcardPanel.queryDataByCondition("cp_question_id="+setVo.getStringValue("id"));
			 if(!"������".equals(state)){
				 dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				 dialog.billcardPanel.setEditableByEditInit();
			 }
		 }
		 
		 if(!"������".equals(state)){
			 dialog.getBtn_confirm().addActionListener(new ActionListener() {
			
				 @Override
				 	public void actionPerformed(ActionEvent e) {
					 if(!dialog.billcardPanel.checkValidate()){
						 return;
					 }
				
					 List<String> _sqllist = new ArrayList<String>();
					 _sqllist.add(dialog.billcardPanel.getUpdateDataSQL());
					 //������/��ȷ��
					 //if("������".equals(state) || "��ȷ��".equals(state)){
						 _sqllist.add("update cp_question set state='������' where id="+setVo.getStringValue("id"));
					// }
				
					 try{
						 UIUtil.executeBatchByDS(null, _sqllist);
						 childListPanel.refreshCurrSelectedRow();
						 dialog.dispose();
						 MessageBox.show(childListPanel, "�Ų��������۳ɹ���");
					 } catch (WLTRemoteException ex) {
						 log.error(ex);
						 MessageBox.show(childListPanel, "�Ų���������ʧ�ܣ�");
					 } catch (Exception ex) {
						 log.error(ex);
						 MessageBox.show(childListPanel, "�Ų���������ʧ�ܣ�");
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
			if("������".equals(state)){
				btn_submit.setEnabled(false);
			}else{
				btn_submit.setEnabled(true);
			}
					
		}
	}

}
