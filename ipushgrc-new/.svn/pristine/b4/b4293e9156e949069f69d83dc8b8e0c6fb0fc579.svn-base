package com.pushworld.ipushgrc.ui.cmpscore.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CmpScoreMyScoreWKPanel extends AbstractWorkPanel implements ActionListener{

	private BillListPanel listPanel_gr = null;
	private BillListPanel listPanel_bm = null ;
	private WLTButton btn_query = null ;
	
	@Override
	public void initialize() {
		WLTTabbedPane tabpane = new WLTTabbedPane();
		// 个人信息页面
		listPanel_gr = new BillListPanel("CMP_SCORE_RECORD_CODE1_5");
		String userID = ClientEnvironment.getInstance().getLoginUserID();
		listPanel_gr.setDataFilterCustCondition("userid = " + userID);
		listPanel_gr.QueryDataByCondition(null);
		// 部门积分页面
		listPanel_bm = new BillListPanel("V_CMP_SCORE_DEPT_CODE1");
		String deptID = ClientEnvironment.getInstance().getLoginUserDeptId();
		listPanel_bm.setDataFilterCustCondition("deptid = " + deptID);
		listPanel_bm.QueryDataByCondition(null);
		btn_query = new WLTButton("查看明细");
		listPanel_bm.addBillListButton(btn_query);
		btn_query.addActionListener(this);
		listPanel_bm.repaintBillListButton();
		
		tabpane.addTab("个人积分", listPanel_gr);
		tabpane.addTab("部门积分", listPanel_bm);
		
		this.add(tabpane);
		
	}

	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_query)
			onQuery();
	}

	private void onQuery() {
		BillVO vo = listPanel_bm.getSelectedBillVO();
		if(vo == null){
			MessageBox.show(this,"请选择一条数据");
			return ;
		}
		String deptid = vo.getStringValue("deptid");
		BillCardDialog dialog = new BillCardDialog(this,"明细","CMP_SCORE_RECORD_CODE1_5",700,500,WLTConstants.BILLDATAEDITSTATE_INIT); 
		BillListPanel listPanel = new BillListPanel("CMP_SCORE_RECORD_CODE1_5");
		listPanel.setDataFilterCustCondition("deptid = " + deptid);
		listPanel.QueryDataByCondition(null);
		dialog.add(listPanel);
		dialog.setVisible(true);
	}

}
