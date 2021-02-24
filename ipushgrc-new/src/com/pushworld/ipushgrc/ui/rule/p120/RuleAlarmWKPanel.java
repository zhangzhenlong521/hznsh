package com.pushworld.ipushgrc.ui.rule.p120;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;
import com.pushworld.ipushgrc.ui.rule.p010.RuleShowHtmlDialog;

/**
 * 制度预警
 * 
 * @author xch
 * 
 */
public class RuleAlarmWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {

	private BillListPanel billlist_alarm;

	private WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
	private WLTButton btn_deal;

	public void initialize() {
		billlist_alarm = new BillListPanel("RULE_ALARM_CODE1");
		btn_deal = new WLTButton("处理");
		btn_deal.addActionListener(this);
		billlist_alarm.addBatchBillListButton(new WLTButton[] { btn_deal, btn_show });
		billlist_alarm.repaintBillListButton();
		billlist_alarm.addBillListHtmlHrefListener(this);
		this.add(billlist_alarm);
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillVO selectVO = billlist_alarm.getSelectedBillVO();
		if (selectVO == null) {
			return;
		}
		String key = _event.getItemkey();
		String templetcode = selectVO.getStringValue("ALARMTARGETTAB", "RULE_RULE").toUpperCase() + "_CODE1";
		BillCardPanel cardPanel = null;
		if ("rulename".equals(key)) {//点击名称查看需要修改的表单和制度记录
			cardPanel = new BillCardPanel(templetcode);
			cardPanel.queryDataByCondition("id='" + selectVO.getStringValue("ruleid", "-999") + "'"); //
		} else {
			//			if ("law_law".equals(billlist_alarm.getSelectedBillVO().getStringValue("alarmsourcetab"))) {
			//				showLawHtml();
			//			} else {
			//				showRuleHtml();
			//			}
			templetcode = selectVO.getStringValue("alarmsourcetab", "RULE_RULE").toUpperCase() + "_CODE1";
			cardPanel = new BillCardPanel(templetcode);
			cardPanel.queryDataByCondition("id='" + selectVO.getStringValue("alarmsourcepk", "-999") + "'"); //
		}
		BillCardDialog dialog = new BillCardDialog(this, templetcode, cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true); //
	}

	/*
	 * 用html方式打开 法规。
	 */
	private void showLawHtml() {
		String lawid = billlist_alarm.getSelectedBillVO().getStringValue("alarmsourcepk");
		new LawShowHtmlDialog(this, lawid);
	}

	/*
	 * 用html方式打开 制度
	 */
	private void showRuleHtml() {
		String ruleid = billlist_alarm.getSelectedBillVO().getStringValue("alarmsourcepk");
		new RuleShowHtmlDialog(this, ruleid, null);
	}

	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == btn_deal) {
			onDeal();
		}
	}

	/**
	 * 处理按钮的逻辑【李春娟/2015-12-29】
	 */
	private void onDeal() {
		BillVO[] selectVOs = billlist_alarm.getSelectedBillVOs();
		if (selectVOs == null || selectVOs.length == 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		int option = MessageBox.showOptionDialog(this, "请选择处理方式", "", new String[] { "已更新", "忽略" });
		try {
			String userid = ClientEnvironment.getInstance().getLoginUserID();
			String userdept = ClientEnvironment.getInstance().getLoginUserDeptId();
			String currdate = TBUtil.getTBUtil().getCurrDate();
			ArrayList ids = new ArrayList();
			for (int i = 0; i < selectVOs.length; i++) {
				ids.add(selectVOs[i].getStringValue("id"));
			}
			String str_ids = TBUtil.getTBUtil().getInCondition(ids);
			if (option == 0) {
				UIUtil.executeUpdateByDS(null, "update rule_alarm set state='已更新',manager='" + userid + "',managedept='" + userdept + "',managedate='" + currdate + "' where id in(" + str_ids + ")");
			} else if (option == 1) {
				UIUtil.executeUpdateByDS(null, "update rule_alarm set state='忽略',manager='" + userid + "',managedept='" + userdept + "',managedate='" + currdate + "' where id in(" + str_ids + ")");
			} else {
				return;
			}
			billlist_alarm.refreshData();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
