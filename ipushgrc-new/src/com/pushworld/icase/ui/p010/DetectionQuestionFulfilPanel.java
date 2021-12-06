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
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
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
 * �Ų��������ĺ�ʵ
 * @author zane
 *
 */
public class DetectionQuestionFulfilPanel extends AbstractWorkPanel implements BillListSelectListener, ActionListener {

	private static final long serialVersionUID = -6909843946321417628L;
	
	private org.apache.log4j.Logger log = WLTLogger.getLogger(DetectionQuestionFulfilPanel.class);
	
	private BillListPanel listPanel;
	private BillListPanel childListPanel;
	private WLTButton btn_pass;
	private BillVO selVO;

	@Override
	public void initialize() {
		
		WLTSplitPane main_splitpane = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		listPanel = new BillListPanel("CP_QUESTION_CODE6");
		
		WLTButton case_look = new WLTButton("���/��ӡ");
		case_look.addActionListener(new CasePreventionQuestionOperateListener(listPanel, "CP_QUESTION_CODE6", 4, "cp_implement_id", null,null, null, "�����Ų�"));
		listPanel.addBillListButton(case_look);
		
		listPanel.repaintBillListButton();
		listPanel.addBillListSelectListener(this);
		
//		Map<String, String> roleMap = ClientEnvironment.getCurrLoginUserVO().getRoleMap();
		//AR001-��������ڣ����а���������;AR002-���а��������Ÿ�����
		String[] rolecode = ClientEnvironment.getInstance().getLoginUserRoleCodes();
		String rolecodes = Arrays.toString(rolecode); 
		if(!(rolecodes.indexOf("AR001")!=-1 || rolecodes.indexOf("AR002")!=-1)){
//		if(!(roleMap.containsKey("AR001") || roleMap.containsKey("AR002"))){
			listPanel.setDataFilterCustCondition("channels='�����Ų�' and CHECK_CORP="+ClientEnvironment.getCurrLoginUserVO().getPKDept());
		}else{
			listPanel.setDataFilterCustCondition("channels='�����Ų�'");
		}
		
		
		//listPanel.setDataFilterCustCondition("channels='�����Ų�'");
		
		childListPanel = new BillListPanel("CP_ABARBEITUNGMENU_CODE1");
		
		childListPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		
		btn_pass = new WLTButton("������֤");
		btn_pass.addActionListener(this);
		childListPanel.addBillListButton(btn_pass);
		
		childListPanel.repaintBillListButton();
		childListPanel.addBillListSelectListener(this);
		main_splitpane.add(listPanel, 1);
		main_splitpane.add(childListPanel, 2);
		this.add(main_splitpane);
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if(listPanel==_event.getBillListPanel()){
			selVO = listPanel.getSelectedBillVO();
			childListPanel.QueryDataByCondition("question_id="+listPanel.getSelectedBillVO().getStringValue("id"));
		}else{
			String state = childListPanel.getSelectedBillVO().getStringValue("state");
			String abarbeitungstatus = childListPanel.getSelectedBillVO().getStringValue("abarbeitungstatus");
			btn_pass.setEnabled(false);
			if("������".equals(state)){
				if(WLTStringUtils.hasText(abarbeitungstatus)){
					if("δͨ��".equals(abarbeitungstatus)){
						btn_pass.setEnabled(true);
					}
				}else{
					btn_pass.setEnabled(true);
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj==btn_pass){
			onPass();
		}
	}
	
	private void onPass() {
		if(getMessage()){
			return;
		}
		BillVO vo=childListPanel.getSelectedBillVO();
		if(null==vo){
			MessageBox.showSelectOne(childListPanel);
			return ;
		}
		final BillCardDialog dialog = new BillCardDialog(listPanel,"CP_ABARBEITUNGMENU_CODE1", WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.queryDataByCondition("id="+vo.getStringValue("id"));
		
		dialog.billcardPanel.setEditableByEditInit();
		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		
		///
		dialog.billcardPanel.setEditable("abarbeitungcause", false);
		dialog.billcardPanel.setEditable("abarbeitungmeasure", false);
		dialog.billcardPanel.setEditable("abarbeitungresult", false);
		dialog.billcardPanel.setEditable("affix", false);
		
		CurrLoginUserVO currLoginUserVO = ClientEnvironment.getCurrLoginUserVO();
		if(null==dialog.billcardPanel.getBillVO().getRefItemVOValue("identifier") || !WLTStringUtils.hasText(dialog.billcardPanel.getBillVO().getRefItemVOValue("identifier").getId())){
			dialog.billcardPanel.setValueAt("identifier", new RefItemVO(currLoginUserVO.getId(), currLoginUserVO.getCode(), currLoginUserVO.getName()));
			dialog.billcardPanel.setValueAt("Verificationdept", new RefItemVO(currLoginUserVO.getPKDept(), currLoginUserVO.getDeptcode(), currLoginUserVO.getDeptname()));
		}
		
		dialog.getBtn_confirm().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String abarbeitungstatus = dialog.billcardPanel.getBillVO().getStringValue("abarbeitungstatus");

				if("ͨ��".equals(abarbeitungstatus)){
					try {
						String count = UIUtil.getStringValueByDS(null, "select count(1) from cp_question where (reform_state='�ݸ�' or reform_state='������' or reform_state='������' or reform_state='������' or reform_state='δ����' or reform_state='�޷�����' or reform_state='����֤' or reform_state='δȷ��') and id <>"+selVO.getStringValue("id")+" and cp_implement_id="+selVO.getStringValue("cp_implement_id"));
						
						List<String> _sqllist = new ArrayList<String>();
						
						if("0".equals(count)){
							_sqllist.add("update cp_implement set q_reform_state='������' where id="+selVO.getStringValue("cp_implement_id"));	
						}else{
							_sqllist.add("update cp_implement set q_reform_state='������' where id="+selVO.getStringValue("cp_implement_id"));	
						}
						_sqllist.add("update cp_question set reform_state='ͨ��' where id="+selVO.getStringValue("id"));
						_sqllist.add("update cp_illegal_employee set state='������' where type='"+selVO.getStringValue("channels")+"' and state<>'������' and parentid="+selVO.getStringValue("id"));
						_sqllist.add(dialog.billcardPanel.getUpdateDataSQL());
						UIUtil.executeBatchByDS(null, _sqllist);
						listPanel.refreshData();
						childListPanel.QueryDataByCondition("1=2");
						MessageBox.show(listPanel, "������֤ͨ����");
					} catch (WLTRemoteException e2) {
						log.error(e2);
						MessageBox.show(listPanel, "������ִ֤��ʧ�ܣ�");
					} catch (Exception e2) {
						log.error(e);
						MessageBox.show(listPanel, "������ִ֤��ʧ�ܣ�");
					}
				}else{
					try {
						
						List<String> _sqllist = new ArrayList<String>();
						_sqllist.add("update cp_question set reform_state='δͨ��' where id="+selVO.getStringValue("id"));
						_sqllist.add(dialog.billcardPanel.getUpdateDataSQL());
						UIUtil.executeBatchByDS(null, _sqllist);
					} catch (Exception e1) {
						log.error(e1);
					}
					MessageBox.show(listPanel, "����֤��");
					listPanel.refreshData();
					childListPanel.QueryDataByCondition("1=2");
				}
				dialog.dispose();
			}
		});
		dialog.maxToScreenSize();
		dialog.setSaveBtnVisiable(false);
		dialog.setVisible(true);

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
