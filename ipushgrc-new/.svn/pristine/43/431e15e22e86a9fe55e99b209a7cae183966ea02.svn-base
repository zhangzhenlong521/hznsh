package com.pushworld.ipushgrc.ui.law.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.favorite.MyFavoriteQueryWKPanel;
import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;

/**
 * 外规查询面板!!! 查询面板可以更具关键字查询！
 * 
 * @author xch
 * 
 */
public class LawQueryWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {

	private static final long serialVersionUID = 1L; //

	private BillListPanel billList_law = null; //

	private String[] prikey = null; // 关键字数组

	private WLTButton btn_list, btn_joinfavority; //浏览，加入收藏

	private BillListDialog billListDialog_exposit; //解读查看对话框

	public void initialize() {
		String templetcode = this.getMenuConfMapValueAsStr("模板", "LAW_LAW_CODE3");//在企业内控内控规范文本要用特定字段排序，故需要可配置模板【李春娟/2014-03-03】
		billList_law = new BillListPanel(templetcode); //
		billList_law.getBillListBtnPanel().setVisible(false);
		billList_law.addBillListHtmlHrefListener(this);
		billList_law.getQuickQueryPanel().addBillQuickActionListener(this);
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		btn_joinfavority = MyFavoriteQueryWKPanel.getJoinFavorityButton("法律法规", this.getClass().getName(), "lawname");
		billList_law.addBatchBillListButton(new WLTButton[] { btn_list, btn_joinfavority });
		billList_law.repaintBillListButton();
		this.add(billList_law); //

		//如果菜单中参数"查询条件", 则自动查询并隐藏查询条件面版
		String queryWhere = this.getMenuConfMapValueAsStr("查询条件", "无");
		if (queryWhere != "无") {
			billList_law.setQuickQueryPanelVisiable(false);
			billList_law.QueryDataByCondition(queryWhere);//以前直接设定死用lawname排序,觉得不太合理，故直接按模板排序【李春娟/2014-03-03】
		}

	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().equals("lawname")) {
			showLawHtml();
		} else if (_event.getItemkey().equals("expositcount")) { //解读次数
			onViewAllExposit();
		}
	}

	private void showLawHtml() {
		String lawid = billList_law.getSelectedBillVO().getStringValue("id");
		new LawShowHtmlDialog(billList_law, lawid, prikey);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == billList_law.getQuickQueryPanel()) {
			queryData();
		}
	}

	private void queryData() {
		String initsql = billList_law.getQuickQueryPanel().getQuerySQL(billList_law.getQuickQueryPanel().getAllQuickQuerySaveCompents());
		if (billList_law.getQuickQueryPanel().getValueAt("primarykey") != null) {
			String primarykey = billList_law.getQuickQueryPanel().getValueAt("primarykey").toString();
			if (!"".equals(primarykey)) {
				if (primarykey.indexOf(" ") >= 0) {
					primarykey = primarykey.trim();
					prikey = new TBUtil().split(primarykey, " ");
				} else {
					prikey = new String[1];
					prikey[0] = primarykey;
				}
				StringBuffer law_sb = new StringBuffer();
				for (int i = 0; i < prikey.length; i++) {
					if (i == prikey.length - 1) {
						law_sb.append("  law_law_item.itemcontent like '%" + prikey[i] + "%'");
					} else {
						law_sb.append("  law_law_item.itemcontent like '%" + prikey[i] + "%' or ");
					}
				}
				String law_where = "select distinct law.Id from  law_law_item ,law_law law where law.id=law_law_item.lawid and (" + law_sb.toString() + ")";
				try {
					billList_law.QueryData(initsql + " and ((id in (" + law_where + ")))");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else {
			String str_ordercons = billList_law.getOrderCondition(); //
			if (str_ordercons != null && !str_ordercons.trim().equals("")) {
				billList_law.QueryData(initsql + " order by " + str_ordercons); //publishdate desc
			} else {
				billList_law.QueryData(initsql); //publishdate desc
			}
			prikey = null;
		}
		if (billList_law.getRowCount() == 0) {
			MessageBox.show(billList_law, "没有查到数据");
		}
	}

	/*
	 * 查看法规所有解读
	 */
	public void onViewAllExposit() {
		BillVO lawVO = billList_law.getSelectedBillVO();
		if (lawVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		billListDialog_exposit = new BillListDialog(this, "查看法规【" + lawVO.getStringValue("lawname") + "】的解读", "LAW_EXPOSIT_CODE1", 700, 550);
		billListDialog_exposit.getBtn_cancel().setText("关闭");
		BillListPanel billList_exposit = billListDialog_exposit.getBilllistPanel();
		billList_exposit.setQuickQueryPanelVisiable(false);
		billList_exposit.addBatchBillListButton(new WLTButton[] { btn_list });
		billList_exposit.repaintBillListButton();
		billList_exposit.QueryDataByCondition(" lawid = " + lawVO.getStringValue("id"));
		billList_exposit.setDataFilterCustCondition(" lawid = " + lawVO.getStringValue("id"));
		billListDialog_exposit.getBtn_confirm().setVisible(false);
		billListDialog_exposit.setVisible(true);
	}

	public BillListPanel getlistPanel() {
		return billList_law;
	}
}
