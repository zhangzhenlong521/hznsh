package com.pushworld.ipushgrc.ui.law.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;

/**
 * 外规解读编辑!!!
 * 单列表(law_law),即查询出外规,然后上面有按钮【解读】,点击后弹出窗口,先查询出该外规有哪些解读(law_exposit),然后可以新增解读!!
 * 同时还有【查看外规】按钮,点击后上面是卡片,显示外规自身属性,下面是左右分割,左边是外规明细(law_law_item)的树型结构,右边上外规明细(law_law_item)的卡片
 * 
 * @author xch
 * 
 */
public class LawExpositEditWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener, BillListSelectListener {

	private BillListPanel billList_law = null; // 法规列表。

	private BillListPanel billList_exposit; // 点击查看解读按钮弹出来的dialog中的 解读面板

	private BillListDialog billListDialog_exposit; // 解读列表对话框!

	private WLTButton btn_exposit, btn_viewExposit, btn_deleteExpoist, btn_updateExposit, btn_list;

	private String loginUserID = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // 登陆人员ID

	public void initialize() {

		billList_law = new BillListPanel("LAW_LAW_CODE2");
		btn_exposit = new WLTButton("新建解读");
		btn_exposit.addActionListener(this);
		btn_viewExposit = new WLTButton("查看解读");
		btn_viewExposit.addActionListener(this);
		billList_law.addBillListHtmlHrefListener(this);
		billList_law.addBatchBillListButton(new WLTButton[] { btn_exposit, btn_viewExposit });
		billList_law.repaintBillListButton();
		billList_law.setItemVisible("expositcount", true);
		billList_law.setItemVisible("exposittime", true);
		this.add(billList_law); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_exposit) {
			onExposit();
		} else if (obj == btn_viewExposit) {
			onViewAllExposit();
		} else if (obj == btn_updateExposit) {
			onUpdateExposit();
		}
	}

	public void onUpdateExposit() {
		BillVO lawVO = billList_exposit.getSelectedBillVO();
		if (lawVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (!loginUserID.equals(lawVO.getStringValue("exposituserid"))) {
			MessageBox.show(this, "此记录只有创建人可以修改！");
			return;
		}
		BillCardDialog billcard_exposit = new BillCardDialog(this, "修改解读", "LAW_EXPOSIT_CODE1", 700, 550, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		BillCardPanel cardPanel = billcard_exposit.getBillcardPanel();
		cardPanel.setBillVO(lawVO);
		billcard_exposit.setVisible(true);
		if (billcard_exposit.getCloseType() == 1) {
			billList_exposit.refreshCurrSelectedRow();
		}
	}

	/*
	 * 解读
	 */
	public void onExposit() {
		BillVO lawVO = billList_law.getSelectedBillVO();
		if (lawVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardDialog billcard_exposit = new BillCardDialog(this, "新建解读", "LAW_EXPOSIT_CODE1", 600, 430, WLTConstants.BILLDATAEDITSTATE_INSERT);
		BillCardPanel cardPanel = billcard_exposit.getBillcardPanel();
		cardPanel.setValueAt("lawid", new StringItemVO(lawVO.getStringValue("id")));
		cardPanel.setValueAt("lawname", new StringItemVO(lawVO.getStringValue("lawname")));
		billcard_exposit.setVisible(true);
		if (billcard_exposit.getCloseType() == 1) {
			try {
				// 解读过一次就更新解读次数
				billList_law.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查看一条法规 的所有解读
	 */
	public void onViewAllExposit() {
		BillVO lawVO = billList_law.getSelectedBillVO();
		if (lawVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_law.refreshCurrSelectedRow();
		lawVO = billList_law.getSelectedBillVO();
		if (lawVO.getStringValue("expositcount") == null || "".equals(lawVO.getStringValue("expositcount"))) {//如果没有解读就没有必要弹出框了，因为弹出后只能修改、删除、浏览，但这三个操作都必须基于一条解读！【李春娟/2012-03-26】
			MessageBox.show(this, "该记录没有解读!");
			return;
		}
		billListDialog_exposit = new BillListDialog(this, "查看法规【" + lawVO.getStringValue("lawname") + "】的解读", "LAW_EXPOSIT_CODE1", 700, 550);
		billListDialog_exposit.getBtn_cancel().setText("返回");
		billListDialog_exposit.getBtn_confirm().setVisible(false);
		billList_exposit = billListDialog_exposit.getBilllistPanel();
		billList_exposit.setQuickQueryPanelVisiable(false); // 设置查询面板隐藏,以前代码是billList_exposit.getQuickQueryPanel().setVisible(false) ;只是将查询面板收起了！
		billList_exposit.QueryDataByCondition(" lawid = " + lawVO.getStringValue("id"));
		billList_exposit.setDataFilterCustCondition(" lawid = " + lawVO.getStringValue("id"));
		btn_updateExposit = new WLTButton("修改");
		btn_updateExposit.addActionListener(this);
		billList_exposit.addBillListHtmlHrefListener(this);
		btn_deleteExpoist = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		billList_exposit.addBatchBillListButton(new WLTButton[] { btn_updateExposit, btn_deleteExpoist, btn_list });
		billList_exposit.repaintBillListButton();
		billList_exposit.addBillListSelectListener(this);
		billListDialog_exposit.setVisible(true);
		billList_law.refreshCurrSelectedRow();
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().equalsIgnoreCase("expositcount")) {
			onViewAllExposit();
		} else if (_event.getBillListPanel().getTempletVO().getTempletcode().contains("LAW_EXPOSIT")) {
			BillVO vo = _event.getBillListPanel().getSelectedBillVO();
			new LawShowHtmlDialog(this, vo.getStringValue("lawid"), null, new String[] { vo.getStringValue("lawitemid") }); // 高亮出来解读的条目标题。
		} else {
			new LawShowHtmlDialog(billList_law);
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO _vo = billList_exposit.getSelectedBillVO();
		if (loginUserID.equals(_vo.getStringValue("exposituserid"))) { // 只有记录的创建者才可以编辑此记录！！
			btn_updateExposit.setEnabled(true);
			btn_deleteExpoist.setEnabled(true);
		} else {
			btn_updateExposit.setEnabled(false);
			btn_deleteExpoist.setEnabled(false);
		}
	}

}
