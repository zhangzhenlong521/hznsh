package com.pushworld.icase.ui.p010;

import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillHtmlPanel;

public class IllegalEmployeeUtil{

	private org.apache.log4j.Logger log = WLTLogger.getLogger(IllegalEmployeeUtil.class);
	
	private BillDialog parentDialog;
	private BillCardPanel cardPanel;
	private BillListPanel listpanel;
	private String templetcode;
	private String deptid;
	private String channels;
	
	private BillVO selVO;
	
	public IllegalEmployeeUtil(BillDialog parentDialog, BillCardPanel cardPanel, BillListPanel listpanel, String templetcode, String deptid, String channels){
		this.parentDialog = parentDialog;
		this.cardPanel = cardPanel;
		this.listpanel = listpanel;
		this.templetcode = templetcode;
		this.deptid = deptid;
		this.channels = channels;
	}
	
	/**
	 * 
	 * @param importCaseId
	 */
	public void onEmployeeDel(){
		BillVO[] billVOs = listpanel.getSelectedBillVOs();
		if(ObjectUtils.isEmpty(billVOs)){
			MessageBox.showSelectOne(listpanel);
			return;
		}
		
		if(!MessageBox.confirm(parentDialog, "是否删除违规员工？")){
			return;
		}
		
		List<String> _sqllist = new ArrayList<String>();
		for (BillVO billVO : billVOs) {
			_sqllist.add("delete from cp_illegal_employee where id="+billVO.getStringValue("id"));
		}
		
		try {
			
			UIUtil.executeBatchByDS(null, _sqllist);
			MessageBox.show(listpanel, "删除违规员工成功！");
			listpanel.QueryDataByCondition(null);
		} catch (WLTRemoteException e1) {
			log.error(e1);
			MessageBox.show(listpanel, "删除违规员工失败！");
		} catch (Exception e1) {
			log.error(e1);
			MessageBox.show(listpanel, "删除违规员工失败！");
		}
	}
	
	public void onEmployeeAdd(){
		final BillCardDialog dialog = new BillCardDialog(listpanel, templetcode, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.setTitle("问题录入");
		dialog.billcardPanel.setValueAt("parentid", new StringItemVO(cardPanel.getBillVO().getStringValue("id")));
		dialog.billcardPanel.setValueAt("project_approval_id", new StringItemVO(cardPanel.getBillVO().getStringValue("projectapproval_audit_id")));
		dialog.billcardPanel.setValueAt("channels", new StringItemVO(channels));
		if(WLTStringUtils.hasText(deptid)){
			if(deptid.startsWith(";")){
				dialog.billcardPanel.setValueAt("byestingdept", new StringItemVO(deptid.substring(1, deptid.length()-1).replaceAll(";", ",")));
				}else{
					dialog.billcardPanel.setValueAt("byestingdept", new StringItemVO(deptid));
				}
				
		}else{
			if(templetcode.equals("CP_ILLEGAL_EMPLOYEE_CODE2_1")){
				if(null==cardPanel.getBillVO().getRefItemVOValue("dutydept")){
					MessageBox.show(cardPanel, "案件没有责任机构！");
					return;
				}
				
				deptid=cardPanel.getBillVO().getRefItemVOValue("dutydept").getId();
				//dialog.billcardPanel.setValueAt("byestingdept", new StringItemVO(deptid.substring(1, deptid.length()-1).replaceAll(";", ",")));
			}
		}
	
		dialog.setVisible(true);
		
		if(dialog.getCloseType()==1){
			listpanel.refreshData();
		}
	}
	
	public void onEmployeeEdit(){
		if(getMessage()){
			return;
		}
		
		BillCardDialog dialog = new BillCardDialog(listpanel, "修改违规人员", templetcode, 600, 700, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.queryDataByCondition("id="+selVO.getStringValue("id"));
		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.setEditableByEditInit();
		
		if(WLTStringUtils.hasText(deptid)){
			dialog.billcardPanel.setValueAt("byestingdept", new StringItemVO(deptid));
		}else{
			if(templetcode.equals("CP_ILLEGAL_EMPLOYEE_CODE2_1")){
				if(null==cardPanel.getBillVO().getRefItemVOValue("dutydept")){
					MessageBox.show(cardPanel, "案件没有责任机构！");
					return;
				}
				dialog.billcardPanel.setValueAt("byestingdept", new StringItemVO(cardPanel.getBillVO().getRefItemVOValue("dutydept").getId()));
			}
		}
		
		dialog.setVisible(true);
		if(dialog.getCloseType()==1){
			listpanel.refreshCurrSelectedRow();
		}
	}
	
	public void onEmployeeLook(){
		if(getMessage()){
			return;
		}
		
		BillCardPanel temp = new BillCardPanel(templetcode);
		temp.queryDataByCondition("id="+selVO.getStringValue("id"));
		temp.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		
		WLTRadioPane rpanel = new WLTRadioPane();
		rpanel.addTab("控件风格", temp);
		BillHtmlPanel html1=new BillHtmlPanel();
		html1.loadHtml(temp.getExportHtml());
		rpanel.addTab("打印预览", html1);
		
		BillDialog caseSelfDialog = new BillDialog(listpanel);
		caseSelfDialog.add(rpanel);
		
		caseSelfDialog.setSize(760, 690);
		caseSelfDialog.locationToCenterPosition();
		caseSelfDialog.setVisible(true);
		
//		final BillCardDialog dialog = new BillCardDialog(listpanel, templetcode, WLTConstants.BILLDATAEDITSTATE_INIT);
//		dialog.billcardPanel.queryDataByCondition("id="+selVO.getStringValue("id"));
//		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
//		
//		dialog.setSaveBtnVisiable(false);
//		dialog.getBtn_confirm().setVisible(false);
//			
//		dialog.setSize(960, 690);
//		dialog.locationToCenterPosition();
//		dialog.setVisible(true);
	}
	
	private boolean getMessage(){
		selVO = listpanel.getSelectedBillVO();
		if(null==selVO){
			MessageBox.showSelectOne(listpanel);
			return true;
		}
		
		return false;
	}
	
}
