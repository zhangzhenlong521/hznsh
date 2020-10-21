package com.pushworld.ipushlbs.ui.casemanage.p020;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 主诉案件,被诉案件,总和案件--案件查询
 * 
 * @author wupeng
 * 
 */
public class LawCaseQueryWKPanel extends AbstractWorkPanel implements ActionListener {

	private WLTButton view = null;
	private BillListPanel list = null;

	@Override
	public void initialize() {
		list = new BillListPanel("LBS_CASE_SELF_CODE2");
		view = new WLTButton("查看追踪信息");
		view.addActionListener(this);// 添加监听
		WLTButton view1 = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		list.getBillListBtnPanel().setVisible(false);
		list.addBatchBillListButton(new WLTButton[] { view1, view });// 添加按钮
		list.repaintBillListButton();// 重绘按钮
		this.add(list);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == view) {
			viewTrace();
		}
	}

	/**
	 * 查看追踪信息
	 */
	private void viewTrace() {
		BillVO selVo = list.getSelectedBillVO();

		if (selVo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		WLTTabbedPane tab = new WLTTabbedPane();

		BillListPanel filingList = new BillListPanel("LBS_CASE_FILING_CODE1");// 立案跟踪
		this.init(filingList, selVo);

		BillListPanel judgList = new BillListPanel("LBS_CASE_JUDGMENT_CODE1");
		this.init(judgList, selVo);

		BillListPanel executeList = new BillListPanel("LBS_CASE_EXECUTE_CODE1");// 执行跟踪
		this.init(executeList, selVo);

		BillListPanel endList = new BillListPanel("LBS_CASE_END_CODE1");// 结案报告
		this.init(endList, selVo);

		if (filingList.getBillVOs().length == 0 && executeList.getBillVOs().length == 0 && endList.getBillVOs().length == 0 && judgList.getBillVOs().length == 0) {
			MessageBox.show(list, "该案件没有任何追踪信息");
			return;
		}

		this.addTabPane(tab, filingList, "立案跟踪");
		this.addTabPane(tab, judgList, "审理跟踪");
		this.addTabPane(tab, executeList, "执行跟踪");
		this.addTabPane(tab, endList, "结案总结");

		BillCardPanel card = new BillCardPanel(list.templetVO);
		card.add(tab);
		card.setPreferredSize(new Dimension(700, 800));
		BillCardDialog dialog = new BillCardDialog(list, "查看追踪信息", card, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true);
	}

	// 列表初始化
	private void init(BillListPanel list, BillVO selVo) {
		if (selVo.getStringValue("lawtype") != null && selVo != null) {
			String law_type = "";
			if (selVo.getStringValue("lawtype").equals("起诉"))
				law_type = "'1'";
			else if (selVo.getStringValue("lawtype").equals("被诉"))
				law_type = "'2'";
			String condition = " case_id = " + selVo.getStringValue("id") + " and case_type = " + law_type;
			list.QueryDataByCondition(condition);
			list.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
			list.repaintBillListButton();
		}
	}

	private void addTabPane(WLTTabbedPane tab, BillListPanel listPanel, String title) {// 根据记录数选择是列表面板还是卡片
		if (listPanel.getBillVOs().length == 0) {
			WLTLabel label = new WLTLabel("没有【" + title + "】信息");
			tab.addTab(title, label);
		} else if (listPanel.getBillVOs().length == 1) {// 只有一条记录或没有记录
			BillCardPanel card = new BillCardPanel(listPanel.getTempletVO());
			card.setBillVO(listPanel.getBillVOs()[0]);
			tab.addTab(title, card);
		} else {
			tab.addTab(title, listPanel);
		}

	}

}
