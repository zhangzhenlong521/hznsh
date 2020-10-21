package com.pushworld.icase.ui.p010;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessDialog;

/**
 * 排查立项与审核
 * @author zane
 * ning
 *
 */
public class DetectionProjectApprovalAuditPanel extends AbstractWorkPanel  implements ActionListener,BillListSelectListener{

	private org.apache.log4j.Logger log = WLTLogger.getLogger(DetectionProjectApprovalAuditPanel.class);
	private static final long serialVersionUID = -6909843946321417628L;
	private WLTButton btn_add,btn_edit,btn_del,btn_look,btn_submit, btn_down;;
	private BillListPanel listPanel;
	private WLTButton  btn_checklook;
	@Override
	public void initialize() {
		listPanel = new BillListPanel("CP_PROJECTAPPROVAL_AUDIT_CODE1");
		btn_add = new WLTButton("新增");//WLTButton.createButtonByType(WLTButton.LIST_POPINSERT_PROCESS);
		btn_add.addActionListener(this);
	    listPanel.addBillListButton(btn_add);
		
	    btn_edit = new WLTButton("修改");
	    btn_edit.addActionListener(this);
	    listPanel.addBillListButton(btn_edit);
	    
	    btn_del = new WLTButton("删除");
	    btn_del.addActionListener(this);
	    listPanel.addBillListButton(btn_del);
	    
	    btn_look = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		listPanel.addBillListButton(btn_look);
		btn_submit = WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR);
		listPanel.addBillListButton(btn_submit);
		
		btn_down = new WLTButton("下发");
		btn_down.addActionListener(this);
		listPanel.addBillListButton(btn_down);
		
		
		
		btn_checklook=new WLTButton("检查实施汇总");
		btn_checklook.addActionListener(this);
		listPanel.addBillListButton(btn_checklook);
		
		
		
		listPanel.repaintBillListButton();
		listPanel.addBillListSelectListener(this);
		
		String deptid = ClientEnvironment.getCurrLoginUserVO().getPKDept();
//		Map<String, String> roleMap = ClientEnvironment.getCurrLoginUserVO().getRoleMap();
		String[] rolecode = ClientEnvironment.getInstance().getLoginUserRoleCodes();
		String rolecodes = Arrays.toString(rolecode); 
		if(!(rolecodes.indexOf("AR001")!=-1 || rolecodes.indexOf("AR002")!=-1)){
//		if(!(roleMap.containsKey("AR001") || roleMap.containsKey("AR002"))){
			listPanel.setDataFilterCustCondition("schemeapplyfordept="+deptid+" and channels='案防排查'");
		}else{
			listPanel.setDataFilterCustCondition("channels='案防排查'");
		}
		
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj=e.getSource();
		if(obj ==btn_add){
			onAdd();
		}else if(obj == btn_edit){
			onEdit();
		}else if(obj == btn_del){
			ondel();
		}else if(obj == btn_down){
			onDown();
		}else if(obj == btn_checklook){
			onCehckLook();
		}
		
	}
	
	private void onCehckLook() {
		BillVO vo = listPanel.getSelectedBillVO();
		if(null == vo){
			MessageBox.showSelectOne(listPanel);
			return;
		}
		
		DetectionQuestionMaintainPanel panel=new DetectionQuestionMaintainPanel();
		panel.initialize();
		panel.getBillListPanel().setDataFilterCustCondition("projectapproval_audit_id="+vo.getStringValue("id")+"");
		panel.getBillListPanel().QueryDataByCondition(null);
		panel.getBillListPanel().getBillListBtnPanel().setVisible(false);
		panel.getChildBillListPanel().getBillListBtnPanel().setVisible(false);
		
		BillDialog dialog=new BillDialog(listPanel);
		dialog.add(panel);
		dialog.setTitle("检查实施情况汇总");
		dialog.maxToScreenSizeBy1280AndLocationCenter();
		dialog.setVisible(true);
	}

	private void onDown() {
		BillVO vo = listPanel.getSelectedBillVO();
		if(null == vo){
			MessageBox.showSelectOne(listPanel);
			return;
		}
		
//		String wfstatus = vo.getStringValue("wfstatus");
//		String state = vo.getStringValue("state");
		if(!MessageBox.confirm(listPanel, "是否下发选中的立项信息？")){
			return;
		}
		
		try {
			// 发起机构
			String schemeapplyfordept = vo.getRefItemVOValue("schemeapplyfordept").getId();
			
			//被检查机构
			String byexaminedept = vo.getRefItemVOValue("byexaminedept").getId();
			
			
			// 协查机构
			String assistance_dept = null==vo.getRefItemVOValue("assistance_dept")?null:vo.getRefItemVOValue("assistance_dept").getId();
			String checkingprojectmatter = vo.getStringValue("checkingprojectmatter");
			String schemestarttime = vo.getStringValue("schemestarttime");
			String schemeendtime = vo.getStringValue("schemeendtime");
			
			String[] byexaminedeptStr = assistance_dept.split(";");
			List<String> _sqllist = new ArrayList<String>();
			HashMap<String, String> hashMap=new HashMap<String, String>();
			hashMap.put(schemeapplyfordept, schemeapplyfordept);
			for (String string : byexaminedeptStr) {
				if(!WLTStringUtils.hasText(string)){
					continue;
				}
				hashMap.put(string, string);
			}
	
			Iterator<String> iterator=hashMap.keySet().iterator();
			while(iterator.hasNext()){
				String string=iterator.next();
				InsertSQLBuilder insert = new InsertSQLBuilder("cp_implement");
				insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_cp_implement"));
				insert.putFieldValue("estingimplement", checkingprojectmatter);
				insert.putFieldValue("estingstartdate", schemestarttime);
				insert.putFieldValue("schemestarttime", schemestarttime);
				insert.putFieldValue("estingenddate", schemeendtime);
				insert.putFieldValue("schemeendtime", schemeendtime);
				insert.putFieldValue("check_dept", string); // 
				insert.putFieldValue("byestingdept", byexaminedept);
				insert.putFieldValue("assistance_dept", string);
				insert.putFieldValue("projectapproval_audit_id", vo.getStringValue("id"));
				insert.putFieldValue("channels", "案防排查");
				insert.putFieldValue("state", "待实施");
				_sqllist.add(insert.getSQL());
			}
			

			
			_sqllist.add("update cp_projectapproval_audit set state='已下发' where id="+vo.getStringValue("id"));
			UIUtil.executeBatchByDS(null, _sqllist);
			listPanel.refreshCurrSelectedRow();
			btn_down.setEnabled(false);
			MessageBox.show(listPanel, "下发成功！");
		} catch (WLTRemoteException e) {
			log.error(e);
			MessageBox.show(listPanel, "下发失败！");
		} catch (Exception e) {
			log.error(e);
			MessageBox.show(listPanel, "下发失败！");
		}
	}

	private void ondel() {
		BillVO[] vo = listPanel.getSelectedBillVOs();
        if(ObjectUtils.isEmpty(vo)){
        	MessageBox.showSelectOne(listPanel);
        	return;
        	
        }
		int li_result = MessageBox.showOptionDialog(listPanel, "是否删除排查立项？", "提示", new String[] { "是", "否" }, 350, 120);
		if(li_result == 0){
			List<String> _sqllist = new ArrayList<String>();
			for (BillVO billVO : vo) {
				_sqllist.add("delete from cp_projectapproval_audit where id="+billVO.getStringValue("id"));
			}
			
			try {
				UIUtil.executeBatchByDS(null, _sqllist);
				MessageBox.show(listPanel, "删除排查立项成功！");
				listPanel.QueryDataByCondition(null);
			} catch (WLTRemoteException e1) {
				log.error(e1);
				MessageBox.show(listPanel, "删除排查立项失败！");
			} catch (Exception e1) {
				log.error(e1);
				MessageBox.show(listPanel, "删除排查立项失败！");
			}
		}
	}
	private void onEdit() {
		BillVO vo = listPanel.getSelectedBillVO();
		if(null == vo){
			MessageBox.showSelectOne(listPanel);
			return;
		}
		
		final String type ="修改";
		String id = vo.getStringValue("id");
		final BillCardDialog dialog = new BillCardDialog(listPanel,"CP_PROJECTAPPROVAL_AUDIT_CODE1", WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.queryDataByCondition("id="+id);
		
		dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.billcardPanel.setEditableByEditInit();
		
		dialog.getBtn_confirm().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onTime(dialog, type);
			}
		});
		dialog.setSaveBtnVisiable(false);
		dialog.setVisible(true);
		
		if(dialog.getCloseType()==1){
			listPanel.refreshData();
		}
		
	}

	private void onAdd() {
//		 final BillCardDialog dialog = new BillCardDialog(listPanel, "CP_PROJECTAPPROVAL_AUDIT_CODE1", WLTConstants.BILLDATAEDITSTATE_INSERT);
//		 dialog.billcardPanel.insertRow();
//		 dialog.billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
//		 dialog.billcardPanel.setEditableByEditInit();
//		 dialog.billcardPanel.setValueAt("channels", new StringItemVO("案防排查"));
//		 final String  type = "新增";
//		 dialog.getBtn_confirm().addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				onTime(dialog,type);
//			}
//		});
//		 dialog.setSaveBtnVisiable(false);
//		 dialog.setVisible(true);
//		 if(dialog.getCloseType()==1){			
//			 listPanel.refreshData();
//		 }
		 
		final BillCardPanel cardPanel = new BillCardPanel(listPanel.templetVO); //创建一个卡片面板
		cardPanel.setLoaderBillFormatPanel(listPanel.getLoaderBillFormatPanel()); //将列表的BillFormatPanel的句柄传给卡片
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置
		
		cardPanel.setValueAt("channels", new StringItemVO("案防排查"));
		
		final BillDialog dialog = new BillDialog(listPanel); //弹出卡片新增框
		
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());		
		WLTButton btn_confirm = new WLTButton("确认");
		WLTButton btn_process = new WLTButton("保存并提交");
		WLTButton btn_cancel = new WLTButton("关闭");
		
		btn_confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cardPanel.stopEditing(); //
				if (!cardPanel.checkValidate()) {
					return;
				}
				try {
					cardPanel.updateData();
				} catch (Exception e2) {
					log.error(e2);
					MessageBox.showException(cardPanel, e2);
					return;
				}
				
				dialog.dispose();
				listPanel.refreshData(); //重新查询了一把???
			}
		}); //
		
		btn_process.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cardPanel.stopEditing(); //
				if (!cardPanel.checkValidate()) {
					return;
				}
				try {
					cardPanel.updateData();
				} catch (Exception e2) {
					log.error(e2);
					MessageBox.showException(cardPanel, e2);
					return;
				}
				
				WorkFlowProcessDialog processDialog = new WorkFlowProcessDialog(dialog, "流程处理", cardPanel, null); //抄送!!!既然这里流程实例为空，则消息任务id、流程任务id和流程实例id三个值都为null，故标题中不要显示了【李春娟/2012-03-28】
				processDialog.setVisible(true); //
					dialog.dispose();
					listPanel.refreshData(); //重新查询了一把???
			}
		}); //
		
		btn_cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		}); //
		panel.add(btn_confirm); //
		panel.add(btn_process);
		panel.add(btn_cancel); //
		dialog.add(cardPanel);
		dialog.setSize(600, 690);
		dialog.locationToCenterPosition();
		dialog.getContentPane().add(panel, BorderLayout.SOUTH);
		
		dialog.setVisible(true); //显示卡片窗口
	}
	
	private void onTime(BillCardDialog dialog,String type) {
		if(!dialog.billcardPanel.checkValidate()){
			return;
		}
   
		String schemestarttime = dialog.billcardPanel.getBillVO().getStringValue("schemestarttime");
		String schemeendtime   = dialog.billcardPanel.getBillVO().getStringValue("schemeendtime");
		try {
			String time = UIUtil.getServerCurrDate();
			if(WLTStringUtils.hasText(schemestarttime)&&WLTStringUtils.hasText(schemeendtime)){

				Calendar start = CalendarUtils.formatYearMonthDayToDate(schemestarttime);
				Calendar end = CalendarUtils.formatYearMonthDayToDate(schemeendtime);
				Calendar tm = CalendarUtils.formatYearMonthDayToDate(time);
				if(type.equals("新增")){
					if(start.getTimeInMillis()-end.getTimeInMillis()>=0){
						MessageBox.show(dialog, "方案开始日期不能大于或等于方案结束日期！");
						return;
					}						
					if(start.getTimeInMillis()-tm.getTimeInMillis()<0){
						MessageBox.show(dialog, "方案开始日期不能小于当前日期！");
						return;
					}
				
				}else if(type.equals("修改")){
					if(start.getTimeInMillis()-end.getTimeInMillis()>=0){
						MessageBox.show(dialog, "方案开始日期不能大于或等于方案结束日期！");
						return;
					}
				}
			
			}
			dialog.onSave();
			dialog.dispose();
			
			
		} catch (Exception e) {
            log.error(e);
		}
		
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO vo = _event.getCurrSelectedVO();
		String state = vo.getStringValue("state");
		String wfstatus = vo.getStringValue("wfstatus");
		if (!vo.containsKey("wfprinstanceid")) {// 字段中不包含工作流字段
			return; //
		}
		String wf_id = vo.getStringValue("wfprinstanceid");
		if (wf_id == null || wf_id.trim().isEmpty()) {// 没有对应的工作流,修改和删除按钮可用
			btn_edit.setEnabled(true);
			btn_del.setEnabled(true);
			btn_submit.setEnabled(true);
			btn_down.setEnabled(false);
		} else {
			if((";"+ClientEnvironment.getCurrSessionVO().getLoginUserId()+";").equals(vo.getStringValue("wfprinstanceid_currowner"))&&vo.getStringValue("wfstatus").equals("审批中...")){
				btn_edit.setEnabled(true);
				btn_del.setEnabled(true);
			}else{
				btn_edit.setEnabled(false);
				btn_del.setEnabled(false);
			}
			btn_submit.setEnabled(false);
		}
		
		btn_down.setEnabled(false);
		if("流程结束".equals(wfstatus)){
			if("未下发".equals(state)){
				btn_down.setEnabled(true);
			}
		}
			
	}

}
