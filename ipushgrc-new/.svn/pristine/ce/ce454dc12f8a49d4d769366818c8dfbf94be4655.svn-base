package com.pushworld.ipushgrc.ui.law.p060;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;

/**
 * 外规解读查询! 就是单列表(law_exposit),且自动查询出最新的解读,上面的两个按钮【浏览解读】【查看法规】 【浏览解读】就是将列表记录直接浏览
 * 【查看外规】按钮,点击后上面是卡片,显示外规自身属性,下面是左右分割,左边是外规明细(law_law_item)的树型结构,右边上外规明细(law_law_item)的卡片
 * 
 * @author xch
 * 
 */
public class LawExpositQueryWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {
	private JTabbedPane tab; //
	private BillListPanel billList_lawExposit = null;
	private BillListPanel lawExposit_billList = null;
	private BillListDialog billListDialog_exposit;
	private WLTButton btn_viewExposit, btn_list, btn_viewLaw;

	public void initialize() {
		tab = new JTabbedPane(); //
		billList_lawExposit = new BillListPanel("LAW_LAW_CODE2");
		billList_lawExposit.addBillListHtmlHrefListener(this);
		billList_lawExposit.getBillListBtnPanel().setVisible(false);
		billList_lawExposit.setItemVisible("expositcount", true);
		billList_lawExposit.setItemVisible("exposittime", true);
		btn_viewExposit = new WLTButton("查看解读");
		btn_viewExposit.addActionListener(this);
		billList_lawExposit.addBatchBillListButton(new WLTButton[] { btn_viewExposit, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		billList_lawExposit.repaintBillListButton();

		lawExposit_billList = new BillListPanel("LAW_EXPOSIT_CODE1");
		lawExposit_billList.addBillListHtmlHrefListener(this);
		lawExposit_billList.getBillListBtnPanel().setVisible(false);
		lawExposit_billList.setItemVisible("expositcount", true);
		lawExposit_billList.setItemVisible("exposittime", true);
		btn_viewLaw = new WLTButton("查看法规");
		btn_viewLaw.addActionListener(this);
		lawExposit_billList.addBatchBillListButton(new WLTButton[] { btn_viewLaw, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		lawExposit_billList.repaintBillListButton();

		tab.addTab("法规->解读", billList_lawExposit); //
		tab.addTab("解读->法规", lawExposit_billList); //
		this.add(tab);
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().equals("expositcount")) {
			onViewAllExposit();
		} else if (_event.getBillListPanel().getTempletVO().getTempletcode().contains("LAW_EXPOSIT")) {
			BillVO vo = _event.getBillListPanel().getSelectedBillVO();
			new LawShowHtmlDialog(this, vo.getStringValue("lawid"), null, new String[] { vo.getStringValue("lawitemid") }); // 高亮出来解读的条目标题。
		} else {
			new LawShowHtmlDialog(billList_lawExposit);
		}
	}

	/**
	 * 查看一条解读随对应的法规
	 */
	public void onViewLaw() {
		BillVO billVo = lawExposit_billList.getSelectedBillVO();
		if (billVo == null) {
			MessageBox.show(this, "请选择一条解读！");
			return;
		}
		showLawHtml();
	}

	private void showLawHtml() {
		String lawid = lawExposit_billList.getSelectedBillVO().getStringValue("lawid");
		new LawShowHtmlDialog(lawExposit_billList, lawid);
	}

	/**
	 * 查看一条法规 的所有解读
	 */
	public void onViewAllExposit() {
		BillVO lawVO = billList_lawExposit.getSelectedBillVO();
		if (lawVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_lawExposit.refreshCurrSelectedRow();
		lawVO = billList_lawExposit.getSelectedBillVO();
		if (lawVO.getStringValue("expositcount") == null || "".equals(lawVO.getStringValue("expositcount"))) {//如果没有解读就没有必要弹出框了，因为弹出后只能修改、删除、浏览，但这三个操作都必须基于一条解读！【李春娟/2012-03-26】
			MessageBox.show(this, "该记录没有解读!");
			return;
		}
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		billListDialog_exposit = new BillListDialog(this, "查看法规【" + lawVO.getStringValue("lawname") + "】的解读", "LAW_EXPOSIT_CODE1", 700, 550);
		billListDialog_exposit.getBtn_cancel().setText("返回");
		BillListPanel billList_exposit = billListDialog_exposit.getBilllistPanel();
		billList_exposit.setQuickQueryPanelVisiable(false);
		billList_exposit.addBatchBillListButton(new WLTButton[] { btn_list });
		billList_exposit.repaintBillListButton();
		billList_exposit.addBillListHtmlHrefListener(this);
		billList_exposit.QueryDataByCondition(" lawid = " + lawVO.getStringValue("id"));
		billList_exposit.setDataFilterCustCondition(" lawid = " + lawVO.getStringValue("id"));
		billListDialog_exposit.getBtn_confirm().setVisible(false);
		billListDialog_exposit.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_viewExposit) {
			onViewAllExposit();
		} else if (e.getSource() == btn_viewLaw) {
			onViewLaw();
		}
	}
}
