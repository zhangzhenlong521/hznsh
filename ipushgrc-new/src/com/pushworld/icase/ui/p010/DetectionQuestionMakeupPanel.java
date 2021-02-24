package com.pushworld.icase.ui.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 排查问题补录
 * @author zane
 *
 */

public class DetectionQuestionMakeupPanel extends AbstractWorkPanel implements ActionListener {


	private static final long serialVersionUID = -6909843946321417628L;
	
	private org.apache.log4j.Logger log = WLTLogger.getLogger(DetectionQuestionMakeupPanel.class);
	
	private BillListPanel listPanel;
	
	private WLTButton btn_down;
	
	private BillVO selVO;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("CP_QUESTION_CODE7");
		
		WLTButton case_add = new WLTButton("新增");	
		case_add.addActionListener(new CasePreventionQuestionOperateListener(listPanel, "CP_QUESTION_CODE7", 1, "cp_implement_id",  null, null, null, "案防排查"));
		listPanel.addBillListButton(case_add);
		
		WLTButton case_edit = new WLTButton("修改");
		case_edit.addActionListener(new CasePreventionQuestionOperateListener(listPanel, "CP_QUESTION_CODE7", 2, "cp_implement_id", null, null, null, "案防排查"));
		listPanel.addBillListButton(case_edit);
		
		WLTButton case_del = new WLTButton("删除");
		case_del.addActionListener(new CasePreventionQuestionOperateListener(listPanel, "CP_QUESTION_CODE7", 3, "cp_implement_id", null, null, null, "案防排查"));
		listPanel.addBillListButton(case_del);
		
		WLTButton case_look = new WLTButton("浏览/打印");
		case_look.addActionListener(new CasePreventionQuestionOperateListener(listPanel, "CP_QUESTION_CODE7", 4, "cp_implement_id", null, null, null, "案防排查"));
		listPanel.addBillListButton(case_look);
		
		btn_down = new WLTButton("下发整改");
		btn_down.addActionListener(this);
		listPanel.addBillListButton(btn_down);
		
		String deptid = ClientEnvironment.getCurrLoginUserVO().getPKDept();
		listPanel.setDataFilterCustCondition("channels='案防排查' and relevanceprojectnumber is not null and create_corp="+deptid);
		
		listPanel.repaintBillListButton();
		
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj==btn_down){
			onDown();
		}
	}

	private void onDown() {
		if(getMessage()){
			return;
		}
		
		try {
			
			List<String> _sqllist = new ArrayList<String>();
			if(!MessageBox.confirm(listPanel, "是否下发整改？")){
				return;
			}
			
			String[][] cp_implement_id = UIUtil.getStringArrayByDS(null,"select id from cp_implement where projectapproval_audit_id="+selVO.getRefItemVOValue("relevanceprojectnumber").getId()+" and check_dept="+selVO.getRefItemVOValue("create_corp").getId());
			_sqllist.add("update cp_question set reform_state='已下发',ability_state='待问责', state='未确定',cp_implement_id="+cp_implement_id[0][0]+" where id="+selVO.getStringValue("id"));
			
			_sqllist.add("update cp_implement set isfandquestion='是' where projectapproval_audit_id="+selVO.getStringValue("relevanceprojectnumber")+" and byestingdept='"+selVO.getStringValue("questiondept")+"'");
			
			UIUtil.executeBatchByDS(null, _sqllist);
			listPanel.refreshData();
			btn_down.setEnabled(false);
			MessageBox.show(listPanel, "下发整改成功！");
			
		} catch (WLTRemoteException e) {
			log.error(e);
			MessageBox.show(listPanel, "下发整改失败！");
		} catch (Exception e) {
			log.error(e);
			MessageBox.show(listPanel, "下发整改失败！");
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
