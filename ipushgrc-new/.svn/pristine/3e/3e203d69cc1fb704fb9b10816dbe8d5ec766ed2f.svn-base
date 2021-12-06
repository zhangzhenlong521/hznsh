package com.pushworld.ipushgrc.ui.rule.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

import com.pushworld.ipushgrc.ui.law.LawAndRuleShowDetailDialog;

/**
 * 制度评价,就是大家上来对一个制度发表意见,存储在[rule_eval]表中!!! 类似天BBS的味道!!
 * 主界面是评价主表的列表[rule_eval],点击后出现其对应评价子表[RULE_EVAL_B_CODE1]列表,但子表需要
 * 
 * @author xch
 * 
 */
public class RuleEvalWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener, BillListHtmlHrefListener {

	private BillListPanel billList_Rule; //
	private WLTButton btn_revert = null; //
	private WLTButton btn_insert, btn_list, btn_update, btn_delete; //
	private BillListDialog billlistDialog_eval;
	private BillListPanel billlist_eval;
	private String loginUserId = ClientEnvironment.getCurrSessionVO().getLoginUserId();

	@Override
	public void initialize() {
		billList_Rule = new BillListPanel("RULE_RULE_CODE2"); //
		billList_Rule.addBillListHtmlHrefListener(this);
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT, "创建新评价"); //
		btn_list = WLTButton.createButtonByType(WLTButton.COMM, "查看评价");
		btn_list.addActionListener(this);
		btn_insert.addActionListener(this);
		billList_Rule.setItemVisible("evalcount", true);
		billList_Rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_list }); //
		billList_Rule.repaintBillListButton(); // //
		this.add(billList_Rule); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_revert) {
			onRevert(); //回复
		} else if (obj == btn_insert) {
			onInsert(); //创建新评价
		} else if (obj == btn_list) {
			onList(); //查看评价
		} else if (obj == btn_delete) {
			onDelete(); //删除评价
		}
	}

	/**
	 * 回复!
	 */
	private void onRevert() {
		BillVO evelVO = billlist_eval.getSelectedBillVO();
		if (evelVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		RuleEvalRevertDialog dialog = new RuleEvalRevertDialog(this, evelVO.getStringValue("id"), 560, 600); // //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == -1) {
			BillListPanel billListPanel = dialog.getBillListPanel();
			billListPanel.refreshData();
			BillVO[] billVOs = billListPanel.getAllBillVOs();
			if (billVOs != null && billVOs.length > 0) {
				btn_update.setEnabled(false);
				btn_delete.setEnabled(false);
			} else {
				btn_update.setEnabled(true);
				btn_delete.setEnabled(true);
			}
		}
	}

	private void onInsert() {
		BillVO ruleVO = billList_Rule.getSelectedBillVO();
		if (ruleVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardDialog billcard_exposit = new BillCardDialog(this, "创建新评价", "RULE_EVAL_CODE1", 600, 550, WLTConstants.BILLDATAEDITSTATE_INSERT);
		BillCardPanel cardPanel = billcard_exposit.getBillcardPanel();
		cardPanel.setValueAt("ruleid", new RefItemVO(ruleVO.getStringValue("id"), ruleVO.getStringValue("rulecode"), ruleVO.getStringValue("rulename")));
		cardPanel.setValueAt("rulename", new StringItemVO(ruleVO.getStringValue("rulename")));
		billcard_exposit.setVisible(true);
		if (billcard_exposit.getCloseType() == 1) {
			try {
				String updaterule = null;
				if ("".equals(ruleVO.getStringValue("evalcount", ""))) {//这里需要判断一下，否则新建时制度的评价次数没有设置为默认值0，那就有问题了【李春娟/2012-03-27】
					updaterule = "update rule_rule set evalcount = 1 where id = " + ruleVO.getStringValue("id");
				} else {
					updaterule = "update rule_rule set evalcount = (evalcount+1) where id = " + ruleVO.getStringValue("id");
				}

				InsertSQLBuilder insertMsg = new InsertSQLBuilder("msg_alert");
				insertMsg.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "s_msg_alert"));
				insertMsg.putFieldValue("descr", "制度:[" + ruleVO.getStringValue("rulename") + "]被评价...");
				insertMsg.putFieldValue("billtype", "制度评价");
				insertMsg.putFieldValue("busitype", "评价");
				insertMsg.putFieldValue("createdate", UIUtil.getServerCurrDate());
				insertMsg.putFieldValue("linkurl", this.getClass().getName());
				insertMsg.putFieldValue("dataids", ruleVO.getStringValue("id"));
				String roleid = UIUtil.getStringValueByDS(null, "select id from pub_role where CODE ='总行制度管理员'");
				insertMsg.putFieldValue("receivrole", ";" + roleid + ";");
				List sqlList = new ArrayList();
				sqlList.add(updaterule);
				if (roleid != null && !roleid.equals("")) {
					sqlList.add(insertMsg.getSQL());
				}
				UIUtil.executeBatchByDS(null, sqlList);
				billList_Rule.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查看 评价
	 */
	private void onList() {
		BillVO ruleVO = billList_Rule.getSelectedBillVO();
		if (ruleVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_Rule.refreshCurrSelectedRow();
		ruleVO = billList_Rule.getSelectedBillVO();
		String evalcount = ruleVO.getStringValue("evalcount", "");
		if ("".equals(evalcount) || "0".equals(evalcount)) {//如果没有评价直接提醒，不要弹出查看评价框【李春娟/2012-03-27】
			MessageBox.show(this, "该制度没有评价!");
			return;
		}
		billlistDialog_eval = new BillListDialog(this, "查看评价", "RULE_EVAL_CODE1", 700, 600);
		billlistDialog_eval.getBtn_confirm().setVisible(false);
		billlistDialog_eval.getBtn_cancel().setText("返回");
		billlist_eval = billlistDialog_eval.getBilllistPanel();
		billlist_eval.QueryDataByCondition(" ruleid = " + ruleVO.getStringValue("id"));
		billlist_eval.setDataFilterCustCondition(" ruleid = " + ruleVO.getStringValue("id"));
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "修改评价"); //
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE, "删除评价"); //
		btn_delete.addActionListener(this);
		btn_revert = WLTButton.createButtonByType(WLTButton.COMM, "查看回复"); //
		btn_revert.addActionListener(this); //
		billlist_eval.addBatchBillListButton(new WLTButton[] { btn_update, btn_delete, btn_revert });
		billlist_eval.repaintBillListButton();
		billlist_eval.addBillListSelectListener(this);
		billlistDialog_eval.setVisible(true);
	}

	private void onDelete() {
		BillVO evelVO = billlist_eval.getSelectedBillVO();
		if (evelVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.confirmDel(billlistDialog_eval)) {
			try {
				UIUtil.executeBatchByDS(null, new String[] { "delete from rule_eval where id = " + evelVO.getStringValue("id"), "delete from rule_eval_b where rule_eval_id = " + evelVO.getStringValue("id"),
						"update rule_rule set evalcount = (evalcount-1) where id = " + evelVO.getStringValue("ruleid") });
				billlist_eval.removeSelectedRow();
				billList_Rule.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO vo = _event.getCurrSelectedVO();
		if (vo != null) {
			if (loginUserId.equals(vo.getStringValue("evaluserid"))) { // 创建人对记录进行编辑.
				try {
					String count = UIUtil.getStringValueByDS(null, "select count(id) from rule_eval_b where 1=1  and ( rule_eval_id = " + vo.getStringValue("id") + ")");
					if ("0".equals(count)) {
						btn_update.setEnabled(true);
						btn_delete.setEnabled(true);
					} else {
						btn_update.setEnabled(false);
						btn_delete.setEnabled(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				btn_update.setEnabled(false);
				btn_delete.setEnabled(false);
			}
		}
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if ("evalcount".equals(_event.getItemkey())) { // 点击数目
			onList();
		} else { // 点击查看
			new LawAndRuleShowDetailDialog("RULE_RULE_ITEM_CODE1", _event.getBillListPanel(), 850, 550);
		}
	}

}
