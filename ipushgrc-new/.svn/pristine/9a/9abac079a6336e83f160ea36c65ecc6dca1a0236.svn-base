package com.pushworld.ipushgrc.ui.cmpcheck.p040;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.cmpcheck.p030.CheckItemListPanel;

/**
 * 检查结果处理
 * @author Gwang
 *
 */
public class CheckResultSubmitWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billList_check; //
	private WLTButton btn_viewdetail, btn_addidea, btn_submit; //
	private BillDialog listDialog; 
	@Override
	public void initialize() {
		billList_check = new BillListPanel("CMP_CHECK_CODE1"); //检查活动列表
		btn_viewdetail = new WLTButton("查看检查结果", "refsearch.gif");
		btn_viewdetail.addActionListener(this);
		btn_addidea = new WLTButton("录入检查意见", "zt_057.gif");
		btn_addidea.addActionListener(this);
		btn_submit = new WLTButton("结束检查", "office_092.gif");
		btn_submit.addActionListener(this);
		billList_check.addBatchBillListButton(new WLTButton[] { btn_viewdetail, btn_addidea, btn_submit });
		billList_check.repaintBillListButton();
		
		billList_check.getQuickQueryPanel().setVisiable("status",false);//这里只显示状态为2，即实施中的记录，故该状态查询框去掉【李春娟/2013-06-20】
		billList_check.QueryDataByCondition("status = 2");
		billList_check.setDataFilterCustCondition("status = 2");
		this.add(billList_check);

	}


	public void actionPerformed(ActionEvent e) {
		BillVO selectedVO = billList_check.getSelectedBillVO();
		if(selectedVO == null){
			MessageBox.showSelectOne(this);
			return;
		}
		
		Object obj = e.getSource();
		if (obj == btn_viewdetail) {
			onViewDetail(selectedVO);
		}else if (obj == btn_addidea){
			onAddIdea(selectedVO);
		}else if (obj == btn_submit) {
			onSubmit(selectedVO);			
		}else{
			listDialog.dispose();
		}		
	}
	
	private void onViewDetail(BillVO selectedVO) {
		BillListPanel billList = new CheckItemListPanel("CMP_CHECK_ITEM_CODE2",2);
		listDialog = new BillDialog(this,700,500);
		listDialog.getContentPane().add(billList,BorderLayout.CENTER);
		billList.queryDataByCondition(" cmp_check_id = " + selectedVO.getStringValue("id"), null);
		//增加查看"工作底稿"按钮
		billList.addBatchBillListButton(new WLTButton[]{WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "浏览")});
		billList.repaintBillListButton();
		WLTPanel btnPanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT,new FlowLayout(),false);
		WLTButton btn_cancel = new WLTButton("关闭");
		btn_cancel.addActionListener(this);
		btnPanel.add(btn_cancel);
		listDialog.add(btnPanel,BorderLayout.SOUTH);
		listDialog.setVisible(true);
		
	}
	
	private void onAddIdea(BillVO selectedVO) {
		if("3".equals(selectedVO.getStringValue("status"))){
			MessageBox.show(this, "此活动已经实施完成!!!");
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_CHECK_CODE1");
		cardPanel.setBillVO(selectedVO);
		BillCardDialog cardDialog = new BillCardDialog(this,"录入意见",cardPanel,WLTConstants.BILLDATAEDITSTATE_UPDATE);
		cardPanel.setEditable(false);
		cardPanel.setEditable("checkideasum", true);
		cardPanel.setEditable("attachfile",true);
		cardDialog.setVisible(true);
		if(cardDialog.getCloseType() == 1){
			billList_check.refreshCurrSelectedRow();
		}
	}
	/**
	 * 这个地方以后需要做 数据同步校验。
	 */
	private void onSubmit(BillVO selectedVO) {
		if(MessageBox.showConfirmDialog(this, "检查结束后数据将不能再修改, 确定结束吗?")!= JOptionPane.YES_OPTION){
			return;
		}
		try {
			UIUtil.executeBatchByDS(null, new String[]{"update cmp_check set status = '3' where id = '"+ selectedVO.getStringValue("id")+"'"});
			billList_check.removeSelectedRow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
