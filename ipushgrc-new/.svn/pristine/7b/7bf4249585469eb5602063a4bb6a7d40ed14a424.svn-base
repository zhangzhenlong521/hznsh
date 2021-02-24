package com.pushworld.ipushgrc.ui.rule.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.favorite.MyFavoriteQueryWKPanel;
import com.pushworld.ipushgrc.ui.rule.p010.RuleShowHtmlDialog;
import com.pushworld.ipushgrc.ui.rule.p010.RuleWordBuilder;

/**
 * 内部制度查询！！
 * @author hm
 *
 */
public class RuleQueryWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {

	private BillListPanel billList_rule;
	private String[] prikey; //关键字查询
	private WLTButton btn_showword; // word导出
	private WLTButton btn_exportallword; // word批量导出
	private WLTButton btn_list, btn_joinfavority; //浏览,加入收藏

	public void initialize() {
		currInit("RULE_RULE_CODE6");
	}

	public void currInit(String _mainTemplet) {
		billList_rule = new BillListPanel(_mainTemplet); //制度维护和查询有权限过滤。
		billList_rule.getQuickQueryPanel().addBillQuickActionListener(this);
		billList_rule.addBillListHtmlHrefListener(this);
		btn_joinfavority = MyFavoriteQueryWKPanel.getJoinFavorityButton("内部制度", this.getClass().getName(), "rulename");
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		btn_showword = new WLTButton("Word导出", "zt_009.gif");//台前农商行需求,把维护界面的word导出迁过来[YangQing/2013-06-26]
		btn_showword.addActionListener(this);
		btn_exportallword = new WLTButton("Word批量导出", "report_go.png");
		btn_exportallword.addActionListener(this);
		
		/*太复杂了， 优化一下  Gwang 2016-01-27
		 * 【Word批量导出】只向admin开放
		 * 【Word导出】由系统参数配置，参数名称：“内部制度Word导出按钮开放角色”
		 */		
		boolean expOne = false;//【word导出】按钮是否显示
		String expAllowRole = TBUtil.getTBUtil().getSysOptionStringValue("内部制度Word导出按钮开放角色", "none");		
		if (expAllowRole.equals("none")) {
			//不开放...
		}else {
			//对某些角色开放
			String[] userRole = ClientEnvironment.getInstance().getLoginUserRoleCodes();
			String[] allowRoles = expAllowRole.split(";");
			for (String allowRole : allowRoles) {
				for (String role : userRole) {
					if (allowRole.equals(role)) {
						expOne = true;
						break;
					}
				}
				if (expOne) break;
			}
	
		}
		String usercode = ClientEnvironment.getInstance().getLoginUserCode();
		if (usercode.equalsIgnoreCase("admin")) {//如果是admin，都显示
			billList_rule.addBatchBillListButton(new WLTButton[] { btn_list, btn_joinfavority, btn_showword, btn_exportallword });
		} else if (expOne) {//只显示【word导出】
			billList_rule.addBatchBillListButton(new WLTButton[] { btn_list, btn_joinfavority, btn_showword });
		} else {//都不显示
			billList_rule.addBatchBillListButton(new WLTButton[] { btn_list, btn_joinfavority });
		}

		billList_rule.repaintBillListButton();
		this.add(billList_rule);
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		showRuleHtml();
	}

	private void showRuleHtml() {
		new RuleShowHtmlDialog(billList_rule, prikey); //统一调用一个打开 制度详细。
	}

	private void queryData() {
		String initsql = billList_rule.getQuickQueryPanel().getQuerySQL(billList_rule.getQuickQueryPanel().getAllQuickQuerySaveCompents());
		if (billList_rule.getQuickQueryPanel().getValueAt("primarykey") != null) {
			String primarykey = billList_rule.getQuickQueryPanel().getValueAt("primarykey").toString();
			if (!"".equals(primarykey)) {

				if (primarykey.indexOf(" ") >= 0) {
					primarykey = primarykey.trim();
					prikey = new TBUtil().split(primarykey, " ");
				} else {
					prikey = new String[1];
					prikey[0] = primarykey;
				}

				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < prikey.length; i++) {
					if (i == prikey.length - 1) {
						sb.append("  item.itemcontent like '%" + prikey[i] + "%'");
					} else {
						sb.append("  item.itemcontent like '%" + prikey[i] + "%' and ");
					}
				}
				String where = "select distinct rule.Id from  rule_rule_item item,rule_rule rule where rule.id=item.ruleid and (" + sb.toString() + ")";
				try {
					billList_rule.QueryData(initsql + " and id in (" + where + ") order by publishdate desc");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else {
			String str_ordercons = billList_rule.getOrderCondition(); //
			if (str_ordercons != null && !str_ordercons.trim().equals("")) {
				billList_rule.QueryData(initsql + " order by " + str_ordercons); //publishdate desc
			} else {
				billList_rule.QueryData(initsql); //publishdate desc
			}
			prikey = null;
		}
		if (billList_rule.getRowCount() == 0) {
			MessageBox.show(billList_rule, "没有查到数据");
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_showword) {
			onWordshow();
		} else if (e.getSource() == btn_exportallword) {
			onExportAllshow();//word批量导出
		} else {
			queryData();
		}
	}

	/**
	 * word预览
	 * */
	private void onWordshow() {
		BillVO billVO = billList_rule.getSelectedBillVO();
		if (billVO == null || "".equals(billVO)) {
			MessageBox.showSelectOne(this);
			return;
		}
		new RuleWordBuilder(this, billVO.getStringValue("rulename"), billVO.getStringValue("id"));//【李春娟/2015-01-27】
	}

	/**
	 * word批量导出
	 * */
	private void onExportAllshow() {
		BillVO[] billVO = billList_rule.getSelectedBillVOs();
		if (billVO == null || billVO.length < 1) {
			billVO = billList_rule.getAllBillVOs();
			if (billVO == null || billVO.length < 1) {
				MessageBox.show(this, "没有制度可导出");
				return;
			}
		}
		new RuleWordBuilder(this, billVO);//【李春娟/2015-01-27】
	}

	/*
	 * 获取页面列表面板。[郝明2012-11-16]
	 */
	public BillListPanel getBillList_rule() {
		return billList_rule;
	}
}
