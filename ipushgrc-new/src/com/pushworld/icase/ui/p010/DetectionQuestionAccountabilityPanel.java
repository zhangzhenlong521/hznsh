package com.pushworld.icase.ui.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 排查问题问责
 * @author zane
 *
 */
public class DetectionQuestionAccountabilityPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener {

	private static final long serialVersionUID = -6909843946321417628L;
	
//	private org.apache.log4j.Logger log = WLTLogger.getLogger(DetectionQuestionAccountabilityPanel.class);
	
	private BillListPanel listPanel, childListPanel;
	
	private WLTButton btn_duty;
	
	private BillVO selVO;
	
	@Override
	public void initialize() {
		WLTSplitPane main_splitpane = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		listPanel = new BillListPanel("CP_PROJECTAPPROVAL_AUDIT_CODE4");
		
		listPanel.repaintBillListButton();
		listPanel.addBillListSelectListener(this);
		
		String deptid = ClientEnvironment.getCurrLoginUserVO().getPKDept();
//		Map<String, String> roleMap = ClientEnvironment.getCurrLoginUserVO().getRoleMap();
		String[] rolecode = ClientEnvironment.getInstance().getLoginUserRoleCodes();
		String rolecodes = Arrays.toString(rolecode); 
		if(!(rolecodes.indexOf("AR001")!=-1 || rolecodes.indexOf("AR002")!=-1)){
//		if(!(roleMap.containsKey("AR001") || roleMap.containsKey("AR002"))){
			listPanel.setDataFilterCustCondition("channels='案防排查' and state='已下发' and (schemeapplyfordept="+deptid+" or assistance_dept like '%;"+deptid+";%' or byexaminedept like '%;"+deptid+";%')");
		}else{
			listPanel.setDataFilterCustCondition("channels='案防排查'");
		}
	                                      	
		childListPanel = new BillListPanel("CP_QUESTION_CODE5");
		childListPanel.getQuickQueryPanel().setVisible(false);
		childListPanel.getQuickQueryPanel().setVisible(false);
		btn_duty = new WLTButton("问责");
		btn_duty.addActionListener(this);
		childListPanel.addBillListButton(btn_duty);
		
		WLTButton case_look = new WLTButton("浏览/打印");
		case_look.addActionListener(new CasePreventionQuestionOperateListener(childListPanel, "CP_QUESTION_CODE5", 4, "cp_implement_id", null,null, null, "案防排查"));
		childListPanel.addBillListButton(case_look);
		
		childListPanel.repaintBillListButton();
		
//		if(!(roleMap.containsKey("AR001") || roleMap.containsKey("AR002"))){
//			childListPanel.setDataFilterCustCondition("questiondept="+deptid);
//		}
		
		main_splitpane.add(listPanel, 1);
		main_splitpane.add(childListPanel, 2);
		this.add(main_splitpane);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj==btn_duty){
			onDuty();
		}
	}

	private void onDuty() {
		new AccountabilityOperateListener(childListPanel, "CP_ILLEGAL_EMPLOYEE_CODE3", "排查问题问责", "案防排查", listPanel.getSelectedBillVO().getRefItemVOValue("schemeapplyfordept").getId()).actionPerformed(null);
	}
	
	@Override
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		selVO = listPanel.getSelectedBillVO();
		childListPanel.QueryDataByCondition("relevanceprojectnumber="+selVO.getStringValue("id"));
		
	}

}
