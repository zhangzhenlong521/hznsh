package com.pushworld.icase.ui.p010;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public abstract class CasePreventionAbstractListener implements ActionListener {

	private org.apache.log4j.Logger log = WLTLogger.getLogger(CasePreventionAbstractListener.class);
	
	protected BillListPanel case_self_list;
	protected BillCardPanel cardPanel;
	protected BillDialog caseSelfDialog;
	protected String templetcode;
	protected String case_id;
	protected String case_type;
	protected int status;// 1:new 2:edit 3:del 4:look
	protected String updateSql = null;
	
	protected BillVO caseSelfBillVO;
	
	protected void onCancel(){
		caseSelfDialog.dispose();
	}
	
	public void onConfirm(String preservationField , BillListPanel listpanel) {
		try {
			if(!cardPanel.checkValidate()){
				return;
			}
			
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
			MessageBox.show(case_self_list, "±£´æ³É¹¦£¡");
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
