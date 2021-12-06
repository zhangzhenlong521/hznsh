package com.pushworld.ipushgrc.ui.cmpevent.p040;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.favorite.MyFavoriteQueryWKPanel;
import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;

/**
 * 违规事件查询!!!
 * @author xch
 *
 */
public class CmpEventQueryWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billList; //

	private WLTButton btn_ShowTrack, btn_ShowFlow, btn_ShowRisk; //跟踪查询,流程文件查询，风险点查询

	public void initialize() {
		currInit("CMP_EVENT_CODE2");
	}

	public void currInit(String _templetCode) {
		billList = new BillListPanel(_templetCode); //
		WLTButton btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		btn_ShowTrack = new WLTButton("整改情况查看");
		btn_ShowFlow = new WLTButton("流程文件查看");
		btn_ShowRisk = new WLTButton("风险点查看");
		btn_ShowTrack.addActionListener(this);
		btn_ShowFlow.addActionListener(this);
		btn_ShowRisk.addActionListener(this);
		//加入收藏夹按钮
		WLTButton btn_joinFavority = MyFavoriteQueryWKPanel.getJoinFavorityButton("违规事件", this.getClass().getName(), "eventname");
		billList.addBatchBillListButton(new WLTButton[] { btn_list, btn_joinFavority, btn_ShowTrack, btn_ShowFlow, btn_ShowRisk }); //
		billList.repaintBillListButton(); //刷新按钮!!!
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_ShowTrack) {
			onShowTrack();
		} else if (obj == btn_ShowFlow) {
			onShowFlow();
		} else if (obj == btn_ShowRisk) {
			onShowRisk();
		}
	}

	/**
	 * 整改方案
	 */
	public void onShowTrack() {
		BillVO vo = billList.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(billList);
			return;
		}
		BillListDialog listDialog = new BillListDialog(this, "整改方案", "CMP_EVENT_ADJUSTPROJECT_CODE1");
		BillListPanel listPanel = listDialog.getBilllistPanel();
		listPanel.QueryDataByCondition(" eventid = '" + vo.getStringValue("id") + "'");
		if (listPanel.getRowCount() == 0) {
			MessageBox.show(billList, "该事件没有整改方案.");
			return;
		}
		listPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "查看整改措施"));
		listPanel.repaintBillListButton();
		listPanel.setQuickQueryPanelVisiable(false);
		listDialog.getBtn_confirm().setVisible(false);
		listDialog.setVisible(true);
	}

	/**
	 * 流程文件查询
	 */
	public void onShowFlow() {
		BillVO vo = billList.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(billList);
			return;
		}
		String cmpfile_id = vo.getStringValue("cmp_cmpfile_id");
		if (cmpfile_id == null || "".equals(cmpfile_id)) {
			MessageBox.show(billList, "该事件没有关联流程文件.");
			return;
		}
		CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "查看文件和流程", vo.getStringValue("cmp_cmpfile_id"));
		dialog.setVisible(true);
	}

	/**
	 * 风险点查询
	 */
	public void onShowRisk() {
		BillVO vo = billList.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(billList);
			return;
		}
		String refrisks = vo.getStringValue("refrisks");
		if (refrisks != null && !refrisks.equals("")) {
			String riskIds = new TBUtil().getInCondition(refrisks);
			BillListDialog listDialog = new BillListDialog(this, "关联的风险点", "CMP_RISK_CODE1");
			BillListPanel listPanel = listDialog.getBilllistPanel();
			listPanel.QueryDataByCondition(" id in(" + riskIds + ")");
			if (listPanel.getRowCount() == 0) {//判断是否有相关的风险点，如果没有就不要打开空空的列表了，直接提示然后返回【李春娟/2012-03-28】
				MessageBox.show(billList, "未找到相关风险点！");
				return;
			}
			listPanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));//增加浏览按钮
			listPanel.repaintBillListButton();
			listDialog.getBtn_confirm().setVisible(false);
			listDialog.setVisible(true);
		} else {
			MessageBox.show(billList, "该事件没有关联风险点.");
			return;
		}

	}

}
