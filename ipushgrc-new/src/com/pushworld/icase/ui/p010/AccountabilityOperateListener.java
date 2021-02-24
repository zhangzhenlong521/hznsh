package com.pushworld.icase.ui.p010;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillHtmlPanel;

public class AccountabilityOperateListener implements ActionListener {
	
	private org.apache.log4j.Logger log = WLTLogger.getLogger(AccountabilityOperateListener.class);
	
	private BillListPanel parentListPanel;
	private String templetcode;
	private String title;
	private String channels;
	private String isentitled_score_corp;
	
	private BillVO selVO;
	private BillVO billVO;
	
	private WLTButton btn_confirm;
	private WLTButton btn_cancel;
	
	private BillCardPanel cardPanel, cardPanel2, cardPanel3, cardPanel4, cardPanel5,cardPanewg;
	
	private WLTRadioPane rpanel, rpanel2, rpanel3, rpanel4, rpanel5;
	private WLTTabbedPane tab_panel;
	
	private List<String> tabindexList;
	private int index=1;
	WLTSplitPane main_splitpane = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT);
	/**
	 * 
	 * @param listPanel
	 * @param templetcode
	 * @param CASE_ID
	 * @param case_type
	 * @param status 1:new 2:edit 3:del 4:look
	 */
	public AccountabilityOperateListener(BillListPanel listPanel, String templetcode, String title, String channels, String isentitled_score_corp){
		this.parentListPanel = listPanel;
		this.templetcode = templetcode;
		this.title = title;
		this.channels = channels;
		this.isentitled_score_corp = isentitled_score_corp;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		selVO = parentListPanel.getSelectedBillVO();
		if(null==selVO){
			MessageBox.showSelectOne(parentListPanel);
			return;
		}
		
		
		final BillListDialog dialog = new BillListDialog(parentListPanel, title, templetcode);
		dialog.setSize(800, 600);
		WLTButton btn = new WLTButton("违规员工问责");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ability(dialog, false);
			}
			
		});
		dialog.getBilllistPanel().addBillListButton(btn);
		
		/*WLTButton btn1 = new WLTButton("追加员工问责");
		btn1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ability(dialog, true);
			}
			
		});
		*/
		//dialog.getBilllistPanel().addBillListButton(btn1);
		
		dialog.getBilllistPanel().addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		dialog.getBilllistPanel().repaintBillListButton();
		dialog.getBilllistPanel().setDataFilterCustCondition("channels='"+channels+"' and parentid="+selVO.getStringValue("id"));
		dialog.getBilllistPanel().QueryDataByCondition(null);
		dialog.getBtn_confirm().setVisible(false);
		dialog.setVisible(true);
			
	}

	/**
	 * 
	 * @param dialog
	 * @param isAdd true追加 false问责
	 */
	
	public void ability(BillListDialog dialog, boolean isAdd) {
		billVO = dialog.getBilllistPanel().getSelectedBillVO();
		if(null==billVO && !isAdd){
			MessageBox.showSelectOne(dialog.getBilllistPanel());
			return;
		}
		index=1;
		try {
			tab_panel=new WLTTabbedPane();
		
			String count1 = null;
			if(!isAdd){
				count1 = UIUtil.getStringValueByDS(null, "select count(1) from CP_CALLTOACCOUNT where type='"+channels+"' and cp_illegal_employee_id="+billVO.getStringValue("id"));
			}else{
				count1 = "0";
			}
		
//			String callToAcount = null;
			if(isAdd){
//				callToAcount = "案防排查".equals(channels)?"CP_CALLTOACCOUNT_CODE2":callToAcount;
//				callToAcount = "合规检查".equals(channels)?"CP_CALLTOACCOUNT_CODE2":callToAcount;
				cardPanel = new BillCardPanel("CP_CALLTOACCOUNT_CODE2");
			}else{
//				callToAcount = "案防排查".equals(channels)?"CP_CALLTOACCOUNT_CODE1":callToAcount;
//				callToAcount = "合规检查".equals(channels)?"CP_CALLTOACCOUNT_CODE5":callToAcount;
				cardPanel = new BillCardPanel("CP_CALLTOACCOUNT_CODE1");
			}
			if("0".equals(count1)){
				cardPanel.insertRow();
				cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
				cardPanel.setEditableByInsertInit();
				
				cardPanel.setValueAt("type", new StringItemVO(channels));
				
				if(!isAdd){
					cardPanel.setValueAt("ability_staff", new RefItemVO(billVO.getRefItemVOValue("illegal_code").getId(), billVO.getRefItemVOValue("illegal_code").getName(), billVO.getStringValue("illegal_name")));
					cardPanel.setValueAt("ability_code", new StringItemVO(billVO.getRefItemVOValue("illegal_code").getName()));
					cardPanel.setValueAt("ability_corp", billVO.getRefItemVOValue("illegal_corp"));
					cardPanel.setValueAt("ability_post", billVO.getRefItemVOValue("illegal_post"));
					cardPanel.setValueAt("cp_illegal_employee_id", new StringItemVO(billVO.getStringValue("id")));
					cardPanel.setValueAt("illegal_stroke_count", new StringItemVO(billVO.getStringValue("illegal_stroke_count")));
					cardPanel.setValueAt("illegal_money", new StringItemVO(billVO.getStringValue("illegal_money")));
//					cardPanel.setValueAt("channels", new StringItemVO(channels));
				}
			}else{
				if(!isAdd){
					cardPanel.queryDataByCondition("cp_illegal_employee_id="+billVO.getStringValue("id"));
				}
				cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				cardPanel.setEditableByEditInit();
				
				cardPanel.setValueAt("illegal_stroke_count", new StringItemVO(billVO.getStringValue("illegal_stroke_count")));
				cardPanel.setValueAt("illegal_money", new StringItemVO(billVO.getStringValue("illegal_money")));
			}
			
//			//  每个卡片增加网页浏览
			rpanel = new WLTRadioPane();
			rpanel.addTab("控件风格", cardPanel);
			BillHtmlPanel html1=new BillHtmlPanel();
			html1.loadHtml(cardPanel.getExportHtml());
			rpanel.addTab("打印预览", html1);
			rpanel.setAutoSelectFirst(true);
			
			cardPanel2 = new BillCardPanel("SCORE_REGISTER_ZZL_E01");//CP_ILLEGAL_SCORE_CODE1
			cardPanewg= new BillCardPanel("SCORE_USER_LCJ_E01");
	
			if("0".equals(count1)){
				cardPanel2.insertRow();
				cardPanel2.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
				cardPanel2.setEditableByInsertInit();
				cardPanel2.setValueAt("cp_calltoaccount_id", new StringItemVO(cardPanel.getBillVO().getStringValue("id")));
				cardPanel2.setValueAt("channels", new StringItemVO(channels));
				cardPanel2.setValueAt("cp_implement_id", new StringItemVO(selVO.getStringValue("cp_implement_id")));
				cardPanel2.setValueAt("illegal_item_summary", new StringItemVO(selVO.getStringValue("getoutoflinequestiondescribe")));
				
				if(!isAdd){
					cardPanel2.setValueAt("score_user", new StringItemVO(billVO.getRefItemVOValue("illegal_code").getId()));
					cardPanel2.setValueAt("score_code", new StringItemVO(billVO.getRefItemVOValue("illegal_code").getName()));
					cardPanel2.setValueAt("score_corp", new StringItemVO(billVO.getRefItemVOValue("illegal_corp").getId()));
					cardPanel2.setValueAt("score_post", new StringItemVO(billVO.getRefItemVOValue("illegal_post").getId()));
				}
				cardPanel2.setValueAt("isentitled_score_corp", new StringItemVO(isentitled_score_corp));
//				cardPanel2.setValueAt("score_affirming_date", new StringItemVO(UIUtil.getCurrDate()));
			}else{
				cardPanel2.queryDataByCondition("cp_calltoaccount_id="+cardPanel.getBillVO().getStringValue("id"));
				cardPanel2.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				cardPanel2.setEditableByEditInit();
				
				cardPanel2.setValueAt("illegal_item_summary", new StringItemVO(selVO.getStringValue("getoutoflinequestiondescribe")));
//				cardPanel2.setValueAt("cp_implement_id", new StringItemVO(selVO.getStringValue("cp_implement_id")));
//				cardPanel2.setValueAt("score_code", new StringItemVO(billVO.getRefItemVOValue("illegal_code").getName()));
//				if(!isAdd){
//					cardPanel2.setValueAt("score_user", new StringItemVO(billVO.getRefItemVOValue("illegal_code").getId()));
//					cardPanel2.setValueAt("score_corp", new StringItemVO(billVO.getRefItemVOValue("illegal_corp").getId()));
//					cardPanel2.setValueAt("score_post", new StringItemVO(billVO.getRefItemVOValue("illegal_post").getId()));
//				}
//				cardPanel2.setValueAt("isentitled_score_corp", new StringItemVO(listPanel.getSelectedBillVO().getRefItemVOValue("schemeapplyfordept").getId()));
//				cardPanel2.setValueAt("score_affirming_date", new StringItemVO(UIUtil.getCurrDate()));
//				cardPanel2.setValueAt("channels", new StringItemVO("案防排查"));
			}
			//  每个卡片增加网页浏览
			rpanel2 = new WLTRadioPane();
			rpanel2.addTab("控件风格", cardPanel2);
			BillHtmlPanel html12=new BillHtmlPanel();
			html12.loadHtml(cardPanel2.getExportHtml());
			rpanel2.addTab("打印预览", html12);
			
			cardPanel3 = new BillCardPanel("CP_GREYLIST_CODE1");
			if("0".equals(count1)){
				cardPanel3.insertRow();
				cardPanel3.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
				cardPanel3.setEditableByInsertInit();
				
				cardPanel3.setValueAt("cp_calltoaccount_id", new StringItemVO(cardPanel.getBillVO().getStringValue("id")));
//				RefItemVO illegal_code = billVO.getRefItemVOValue("illegal_code");
//				String illegal_name = billVO.getStringValue("illegal_name");
//				RefItemVO illegal_corp = billVO.getRefItemVOValue("illegal_corp");
//				
//				cardPanel3.setValueAt("staffname", new RefItemVO(illegal_code.getId(), illegal_code.getName(), illegal_name));
//				cardPanel3.setValueAt("staffjobnumber", new StringItemVO(illegal_code.getName()));
//				cardPanel3.setValueAt("staffdept", illegal_corp);
				
				if(!isAdd){
					cardPanel3.setValueAt("staffname", new RefItemVO(billVO.getRefItemVOValue("illegal_code").getId(), billVO.getRefItemVOValue("illegal_code").getName(), billVO.getStringValue("illegal_name")));
					cardPanel3.setValueAt("staffjobnumber", new StringItemVO(billVO.getRefItemVOValue("illegal_code").getName()));
					cardPanel3.setValueAt("staffdept", billVO.getRefItemVOValue("illegal_corp"));
					cardPanel3.setValueAt("staffpost", billVO.getRefItemVOValue("illegal_post"));
				}
				
				cardPanel3.setValueAt("greylistsource", new ComBoxItemVO(channels, channels, channels));
				cardPanel3.setEditable("greylistsource", false);
			}else{
				cardPanel3.queryDataByCondition("greylistsource='"+channels+"' and cp_calltoaccount_id="+cardPanel.getBillVO().getStringValue("id"));
				cardPanel3.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				cardPanel3.setEditableByEditInit();
				
				cardPanel3.setEditable("greylistsource", false);
			}
				
			//  每个卡片增加网页浏览
			rpanel3 = new WLTRadioPane();
			rpanel3.addTab("控件风格", cardPanel3);
			BillHtmlPanel html13=new BillHtmlPanel();
			html13.loadHtml(cardPanel3.getExportHtml());
			rpanel3.addTab("打印预览", html13);
			
			cardPanel4 = new BillCardPanel("CP_STRIPLINE_ACCOUNTABILITY_CODE1");
			if("0".equals(count1)){
				cardPanel4.insertRow();
				cardPanel4.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
				cardPanel4.setEditableByInsertInit();
				
				cardPanel4.setValueAt("cp_calltoaccount_id", new StringItemVO(cardPanel.getBillVO().getStringValue("id")));
			}else{
				cardPanel4.queryDataByCondition("cp_calltoaccount_id="+cardPanel.getBillVO().getStringValue("id"));
				cardPanel4.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				cardPanel4.setEditableByEditInit();
			}
				
			//  每个卡片增加网页浏览
			rpanel4 = new WLTRadioPane();
			rpanel4.addTab("控件风格", cardPanel4);
			BillHtmlPanel html14=new BillHtmlPanel();
			html14.loadHtml(cardPanel4.getExportHtml());
			rpanel4.addTab("打印预览", html14);
			
			cardPanel5 = new BillCardPanel("CP_MONITORAUDIT_ACCOUNTABILITY_CODE1");
			if("0".equals(count1)){
				cardPanel5.insertRow();
				cardPanel5.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
				cardPanel5.setEditableByInsertInit();
				
				cardPanel5.setValueAt("type", new StringItemVO(channels));
				cardPanel5.setValueAt("cp_calltoaccount_id", new StringItemVO(cardPanel.getBillVO().getStringValue("id")));
			}else{
				cardPanel5.queryDataByCondition("type='"+channels+"' and cp_calltoaccount_id="+cardPanel.getBillVO().getStringValue("id"));
				cardPanel5.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				cardPanel5.setEditableByEditInit();
			}
				
			//  每个卡片增加网页浏览
			rpanel5 = new WLTRadioPane();
			rpanel5.addTab("控件风格", cardPanel5);
			BillHtmlPanel html15=new BillHtmlPanel();
			html15.loadHtml(cardPanel5.getExportHtml());
			rpanel5.addTab("打印预览", html15);
			
			int score_index = 0;
			int greylist_index = 0;
			int accountability_index=0;
			int monitoraudit_index = 0;
			
			tab_panel.addTab("问题问责",rpanel);
			tabindexList = new ArrayList<String>();
			if(!"0".equals(count1)){
				String isbringintointegral = cardPanel.getBillVO().getComBoxItemVOValue("isbringintointegral").getId();
				String isbringintogreylist = cardPanel.getBillVO().getComBoxItemVOValue("isbringintogreylist").getId();
				String istoaccountability = cardPanel.getBillVO().getComBoxItemVOValue("istoaccountability").getId();
				String isauditaccountability = cardPanel.getBillVO().getComBoxItemVOValue("isauditaccountability").getId();
				int index = 0;
				if("是".equals(isbringintointegral)){
					tab_panel.addTab("违规积分",rpanel2);//rpanel2
					score_index = (index+=1);
					tabindexList.add("违规积分");
				}
				
				if("是".equals(isbringintogreylist)){
					tab_panel.addTab("员工灰名单",rpanel3);
					greylist_index = (index+=1);
					tabindexList.add("员工灰名单");
				}

				if("是".equals(istoaccountability)){
					tab_panel.addTab("条线部门问责",rpanel4);
					accountability_index=(index+=1);
					tabindexList.add("条线部门问责");
				}
			
				if("是".equals(isauditaccountability)){
					tab_panel.addTab("监察审计问责",rpanel5);
					monitoraudit_index=(index+=1);
					tabindexList.add("监察审计问责");
				}
			}
			
			cardPanel.addBillCardEditListener(new MyBillCardEditListener(score_index, greylist_index, accountability_index, monitoraudit_index, isAdd));
			
			BillDialog caseSelfDialog = new BillDialog(dialog);
			caseSelfDialog.add(tab_panel);
			
			caseSelfDialog.setSize(960, 690);
			caseSelfDialog.locationToCenterPosition();
			caseSelfDialog.getContentPane().add(getSouthPanel(dialog, caseSelfDialog, isAdd), BorderLayout.SOUTH);
			caseSelfDialog.setVisible(true);
		} catch (WLTRemoteException e1) {
			log.error(e1);
		} catch (Exception e1) {
			log.error(e1);
		}
	}

	private JPanel getSouthPanel(final BillListDialog dialog, final BillDialog caseSelfDialog, final boolean isAdd) {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());	
		btn_confirm = new WLTButton("保存");
		
		btn_cancel = new WLTButton("关闭");
		btn_confirm.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				onConfirm(dialog, caseSelfDialog, billVO, isAdd);
			}
		}); //
		btn_cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel(caseSelfDialog);
			}
		}); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		
		return panel;
	}
	
	private void onConfirm(BillListDialog dialog2, BillDialog dialog, BillVO billVO, boolean isAdd){
		if(!cardPanel.checkValidate()){
			return;
		}
		
		String isbringintointegral = cardPanel.getBillVO().getComBoxItemVOValue("isbringintointegral").getId();
		String isbringintogreylist = cardPanel.getBillVO().getComBoxItemVOValue("isbringintogreylist").getId();
		String istoaccountability = cardPanel.getBillVO().getComBoxItemVOValue("istoaccountability").getId();
		String isauditaccountability = cardPanel.getBillVO().getComBoxItemVOValue("isauditaccountability").getId();
		if("是".equals(isbringintointegral)){
			if(!cardPanel2.checkValidate()){
				return;
			}
			
			String score_area = cardPanel2.getBillVO().getStringValue("score_area");
			String real_score = cardPanel2.getBillVO().getStringValue("real_score");
//			if(Float.parseFloat(real_score)-Float.parseFloat(score_area)>0){
//				MessageBox.show(dialog2, "违规积分认定分值不能大于参考分值！");
//				return;
//			}
		}
		cardPanel2.setValueAt("state", new StringItemVO("是".equals(isbringintointegral)?"已认定":"无效"));
		
		if("是".equals(isbringintogreylist)){
			if(!cardPanel3.checkValidate()){
				return;
			}
		}
		cardPanel3.setValueAt("status", new StringItemVO("是".equals(isbringintogreylist)?"有效":"无效"));

		if("是".equals(istoaccountability)){
			if(!cardPanel4.checkValidate()){
				return;
			}
		}
		cardPanel4.setValueAt("state", new StringItemVO("是".equals(istoaccountability)?"有效":"无效"));
	
		if("是".equals(isauditaccountability)){
			if(!cardPanel5.checkValidate()){
				return;
			}
		}
		cardPanel5.setValueAt("state", new StringItemVO("是".equals(isauditaccountability)?"有效":"无效"));
		
		List<String> _sqllist = new ArrayList<String>();
		String cp_illegal_employee_id = null;
		
		String illegal_stroke_count = cardPanel.getBillVO().getStringValue("illegal_stroke_count");
		String illegal_money = cardPanel.getBillVO().getStringValue("illegal_money");
		
		try {
			if(isAdd){
				RefItemVO ability_staff = cardPanel.getBillVO().getRefItemVOValue("ability_staff");
				RefItemVO ability_corp = cardPanel.getBillVO().getRefItemVOValue("ability_corp");
				RefItemVO ability_post = cardPanel.getBillVO().getRefItemVOValue("ability_post");
				
				InsertSQLBuilder insert = new InsertSQLBuilder("cp_illegal_employee");
				cp_illegal_employee_id = UIUtil.getSequenceNextValByDS(null, "s_cp_illegal_employee");
				insert.putFieldValue("id", cp_illegal_employee_id);
				insert.putFieldValue("type", channels);
				insert.putFieldValue("illegal_code", ability_staff.getId());
				insert.putFieldValue("illegal_name", ability_staff.getName());
				insert.putFieldValue("illegal_corp", ability_corp.getId());
				insert.putFieldValue("illegal_post", ability_post.getId());
				insert.putFieldValue("illegal_stroke_count", illegal_stroke_count);
				insert.putFieldValue("illegal_money", illegal_money);
				insert.putFieldValue("parentid", selVO.getStringValue("id"));
				insert.putFieldValue("channels", channels);
				insert.putFieldValue("state", "已问责");
				_sqllist.add(insert.getSQL());
				
				cardPanel.setValueAt("cp_illegal_employee_id", new StringItemVO(cp_illegal_employee_id));
			}else{
//				if("新增".equals(cardPanel.getBillVO().getStringValue("status"))){
					_sqllist.add("update cp_illegal_employee set state='已问责', illegal_stroke_count="+illegal_stroke_count+", illegal_money="+illegal_money+"  where id="+cardPanel.getBillVO().getStringValue("cp_illegal_employee_id"));
//				}
			}
			
			_sqllist.add(cardPanel.getUpdateDataSQL());
			_sqllist.add(cardPanel2.getUpdateDataSQL());
			_sqllist.add(cardPanel3.getUpdateDataSQL());
			_sqllist.add(cardPanel4.getUpdateDataSQL());
			_sqllist.add(cardPanel5.getUpdateDataSQL());
		
			String count1 = null;
			if(!isAdd){
				count1 = UIUtil.getStringValueByDS(null, "select count(1) from cp_illegal_employee where type='"+channels+"' and state='已问责' and parentid="+billVO.getStringValue("parentid"));
				if("0".equals(count1)){
					_sqllist.add("update cp_question set ability_state='问责中' where id="+billVO.getStringValue("parentid"));
				}
				
				count1 = UIUtil.getStringValueByDS(null, "select count(1) from cp_illegal_employee where type='"+channels+"' and id<>"+cardPanel.getBillVO().getStringValue("cp_illegal_employee_id")+" and state='已整改' and parentid="+billVO.getStringValue("parentid"));
				if("0".equals(count1)){
					_sqllist.add("update cp_question set ability_state='已问责' where id="+billVO.getStringValue("parentid"));
				}
				
				if("0".equals(count1)){
					count1 = UIUtil.getStringValueByDS(null, "select count(1) from cp_question where id<>"+billVO.getStringValue("parentid") +" and (ability_state='待问责' or ability_state='问责中') and cp_implement_id in ("+"select cp_implement_id from cp_question where id="+billVO.getStringValue("parentid")+")");
					if("0".equals(count1)){
						_sqllist.add("update cp_implement set q_ability_state='已问责' where id="+selVO.getStringValue("cp_implement_id"));//cp_implement_id
					}else{
						_sqllist.add("update cp_implement set q_ability_state='问责中' where id="+selVO.getStringValue("cp_implement_id"));//cp_implement_id
					}
				}else{
					_sqllist.add("update cp_implement set q_ability_state='问责中' where id="+selVO.getStringValue("cp_implement_id"));//cp_implement_id
				}
			}else{
				count1 = UIUtil.getStringValueByDS(null, "select count(1) from cp_illegal_employee where type='"+channels+"' and state='已问责' and parentid="+cp_illegal_employee_id);
				if("0".equals(count1)){
					_sqllist.add("update cp_question set ability_state='问责中' where id="+cp_illegal_employee_id);
				}
				
				count1 = UIUtil.getStringValueByDS(null, "select count(1) from cp_illegal_employee where type='"+channels+"' and id<>"+cp_illegal_employee_id+" and state='已整改' and parentid="+cp_illegal_employee_id);
				if("0".equals(count1)){
					_sqllist.add("update cp_question set ability_state='已问责' where id="+cp_illegal_employee_id);
				}
				
				if("0".equals(count1)){
					count1 = UIUtil.getStringValueByDS(null, "select count(1) from cp_question where id<>"+cp_illegal_employee_id +" and (ability_state='待问责' or ability_state='问责中') and cp_implement_id in ("+"select cp_implement_id from cp_question where id="+cp_illegal_employee_id+")");
					if("0".equals(count1)){
						_sqllist.add("update cp_implement set q_ability_state='已问责' where id="+selVO.getStringValue("cp_implement_id"));//cp_implement_id
					}else{
						_sqllist.add("update cp_implement set q_ability_state='问责中' where id="+selVO.getStringValue("cp_implement_id"));//cp_implement_id
					}
				}else{
					_sqllist.add("update cp_implement set q_ability_state='问责中' where id="+selVO.getStringValue("cp_implement_id"));//cp_implement_id
				}
			}
//			parentid
			UIUtil.executeBatchByDS(null, _sqllist);
			dialog2.getBilllistPanel().refreshCurrSelectedRow();
			dialog.dispose();
			
//			if("0".equals(count1)){
			parentListPanel.refreshCurrSelectedRow();
//			}
			if(isAdd){
				dialog2.getBilllistPanel().QueryDataByCondition(null);
			}else{
				dialog2.getBilllistPanel().refreshCurrSelectedRow();
			}
			UpdateSQLBuilder update=new UpdateSQLBuilder("SCORE_USER");
			update.setWhereCondition("id="+cardPanel2.getBillVO().getStringValue("id"));
			update.putFieldValue("state","已生效");
			UIUtil.executeUpdateByDS(null,update.getSQL());
			MessageBox.show(dialog2, "问责成功！");
		} catch (Exception e) {
			log.error(e);
			MessageBox.show(dialog, "问责失败！");
		}
		
	}

	private void onCancel(BillDialog dialog){
		dialog.dispose();
	}
	
	class MyBillCardEditListener implements BillCardEditListener{
		
		int score_index = 0;
		int greylist_index = 0;
		int accountability_index=0;
		int monitoraudit_index = 0;
		boolean isAdd;
		
		public MyBillCardEditListener(int score_index, int greylist_index, int accountability_index, int monitoraudit_index, boolean isAdd){
			this.score_index = score_index;
			this.greylist_index = greylist_index;
			this.accountability_index = accountability_index;
			this.monitoraudit_index = monitoraudit_index;
			this.isAdd = isAdd;
		}

		@Override
		public void onBillCardValueChanged(BillCardEditEvent _evt) {
			
			String key = _evt.getItemKey();
			if("isbringintointegral".equals(key)){
				ComBoxItemVO comboxVO = (ComBoxItemVO) _evt.getNewObject();
				if("是".equals(comboxVO.getId())){
					tab_panel.addTab("违规积分",rpanel2);
					tabindexList.add("违规积分");
					initTabIndex();
					tab_panel.setSelectedIndex(index);
				}else{
					if(score_index==0){
						return;
					}
					tab_panel.removeTabAt(score_index);
					tabindexList.remove("违规积分");
					initTabIndex();
					tab_panel.setSelectedIndex(0);
					
				}
			}else if("isbringintogreylist".equals(key)){
				ComBoxItemVO comboxVO = (ComBoxItemVO) _evt.getNewObject();
				if("是".equals(comboxVO.getId())){
					tab_panel.addTab("员工灰名单",rpanel3);
					tabindexList.add("员工灰名单");
					initTabIndex();
					tab_panel.setSelectedIndex(index);
				}else{
					if(greylist_index==0){
						return;
					}
					tab_panel.removeTabAt(greylist_index);
					tabindexList.remove("员工灰名单");
					initTabIndex();
				
					tab_panel.setSelectedIndex(0);
				}
			}else if("istoaccountability".equals(key)){
				ComBoxItemVO comboxVO = (ComBoxItemVO) _evt.getNewObject();
				if("是".equals(comboxVO.getId())){
					tab_panel.addTab("条线部门问责",rpanel4);
					tabindexList.add("条线部门问责");
					initTabIndex();
					tab_panel.setSelectedIndex(index);
				}else{
					if(accountability_index==0){
						return;
					}
					tab_panel.removeTabAt(accountability_index);
					tabindexList.remove("条线部门问责");
					initTabIndex();
					tab_panel.setSelectedIndex(0);
				}
			}else if("isauditaccountability".equals(key)){
				ComBoxItemVO comboxVO = (ComBoxItemVO) _evt.getNewObject();
				if("是".equals(comboxVO.getId())){
					tab_panel.addTab("监察审计问责",rpanel5);
					tabindexList.add("监察审计问责");
					initTabIndex();
					tab_panel.setSelectedIndex(index);
				}else{
					if(monitoraudit_index==0){
						return;
					}
					tab_panel.removeTabAt(monitoraudit_index);
					tabindexList.remove("监察审计问责");
					initTabIndex();
				
					tab_panel.setSelectedIndex(0);
				}
			}else if("ability_staff".equals(key)){
				RefItemVO ability_staff = cardPanel.getBillVO().getRefItemVOValue("ability_staff");
				RefItemVO ability_corp = cardPanel.getBillVO().getRefItemVOValue("ability_corp");
				RefItemVO ability_post = cardPanel.getBillVO().getRefItemVOValue("ability_post");
				String ability_code = cardPanel.getBillVO().getStringValue("ability_code");
				
				cardPanel3.setValueAt("staffname", ability_staff);
				cardPanel3.setValueAt("staffjobnumber", new StringItemVO(ability_staff.getCode()));
				cardPanel3.setValueAt("staffdept", ability_corp);
				cardPanel3.setValueAt("staffpost", ability_post);
				
				cardPanel2.setValueAt("score_user", new StringItemVO(ability_staff.getId()));
				cardPanel2.setValueAt("score_code", new StringItemVO(ability_code));
				cardPanel2.setValueAt("score_corp", new StringItemVO(ability_corp.getId()));
				cardPanel2.setValueAt("score_post", new StringItemVO(ability_post.getId()));
			}
		}
		
		private void initTabIndex(){
			score_index = 0;
			greylist_index = 0;
			accountability_index=0;
			monitoraudit_index = 0;
			index = 0;
			for(int i=0;i<tabindexList.size();i++){
				index+=1;
				if("违规积分".equals(tabindexList.get(i))){
					score_index = i+1;
				}else if("员工灰名单".equals(tabindexList.get(i))){
					greylist_index = i+1;
				}else if("条线部门问责".equals(tabindexList.get(i))){
					accountability_index = i+1;
				}else if("监察审计问责".equals(tabindexList.get(i))){
					monitoraudit_index = i+1;
				}
			}
		}
		
	}
}
