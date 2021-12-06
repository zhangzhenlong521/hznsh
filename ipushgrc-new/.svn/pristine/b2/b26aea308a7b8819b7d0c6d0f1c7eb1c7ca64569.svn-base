package com.pushworld.ipushgrc.ui.law;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

import com.pushworld.ipushgrc.ui.rule.p010.RuleShowHtmlDialog;

/**
 * 点击查看详细弹出外规所对应的条目dialog。 左右结构
 * 
 * @author hm
 * 
 */
public class LawAndRuleShowDetailDialog implements ActionListener, BillTreeSelectListener {
	private String templetCode;
	private WLTPanel wltPanel;
	private WLTButton btn_cancel;
	private BillTreePanel billTree_item;
	private BillCardPanel billCard_item;
	private BillListPanel billList;
	private BillDialog dialog; 
	private WLTButton btn_html = new WLTButton("Html浏览");
	boolean ifHaveItem = new TBUtil().getSysOptionBooleanValue("制度是否分条目", true);
	private boolean currhtml = false;
	private LawAndRuleShowDetailDialog() {

	}
	public LawAndRuleShowDetailDialog(String _templetCode, BillListPanel _billList, int _width, int _height) {
		this(_templetCode, _billList, _width, _height, false);
	}
	
	public LawAndRuleShowDetailDialog(String _templetCode, BillListPanel _billList, int _width, int _height,boolean _currhtml) {
		if(_billList.getTempletVO().getTablename().equalsIgnoreCase("rule_rule")){
			if(!ifHaveItem){
				String textfile =  _billList.getSelectedBillVO().getStringValue("textfile");
				if(textfile == null || textfile.equals("")){
					MessageBox.show(_billList, "该制度没有正文！");
					return;
				}
				UIUtil.openRemoteServerFile("office", textfile);
				return;
			}
		}
		
		templetCode = _templetCode;
		billList = _billList;
		this.currhtml = _currhtml;
		if(currhtml){
			onShowHtml();
		}else{
			dialog = new BillDialog(_billList, _billList.getTempletVO().getTempletname() + "条目", _width, _height);
			init();	
		}
	}
	/*
	 * 初始化 操作！
	 */
	public void init() {
		billTree_item = new BillTreePanel(templetCode);
		billCard_item = new BillCardPanel(templetCode);
		billTree_item.setDragable(false);
		billTree_item.setMoveUpDownBtnVisiable(false);
		wltPanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(), LookAndFeel.defaultShadeColor1, false);
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this);
		if (templetCode.contains("LAW_LAW_ITEM_CODE")) {
			billTree_item.getRootNode().setUserObject(new BillTreeNodeVO(0,billList.getSelectedBillVO().getStringValue("lawname")));
			billTree_item.queryDataByDS(null,"select * from law_law_item where lawid = "+ billList.getSelectedBillVO().getStringValue("id") +" order by abs(id)");
		} else {
			billTree_item.getRootNode().setUserObject(new BillTreeNodeVO(0,billList.getSelectedBillVO().getStringValue("rulename")));
			billTree_item.queryDataByCondition(" ruleid = " + billList.getSelectedBillVO().getStringValue("id"));
		}
		billTree_item.addBillTreeSelectListener(this);
		billTree_item.getToolKitBtnPanel().add(btn_html);
		btn_html.addActionListener(this);
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_item, billCard_item);
		splitPane.setDividerLocation(250);
		wltPanel.add(btn_cancel);
		dialog.getContentPane().add(splitPane, BorderLayout.CENTER);
		dialog.getContentPane().add(wltPanel, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn_html){
			onShowHtml();
		}else if(e.getSource() == btn_cancel){
			dialog.dispose();	
		}
	}
	public void onShowHtml(){
		if(templetCode.contains("LAW_LAW_ITEM_CODE")){
			showLawHtml(currhtml?billList:dialog);
		}else{
			new RuleShowHtmlDialog(currhtml?billList:dialog,billList,null);
		}
	}
	private void showLawHtml(Container con) {
		String lawid = billList.getSelectedBillVO().getStringValue("id");
		new LawShowHtmlDialog(con,lawid);
	}
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		Object obj = _event.getSource();
		if (obj == billTree_item) {
			if (billTree_item.getSelectedNode().isRoot()) {
				billCard_item.clear();
				return;
			}
			billCard_item.setBillVO(billTree_item.getSelectedVO());
		}
	}
}
