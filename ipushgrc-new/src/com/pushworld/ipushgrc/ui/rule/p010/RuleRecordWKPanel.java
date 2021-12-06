package com.pushworld.ipushgrc.ui.rule.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListButtonActinoListener;
import cn.com.infostrategy.ui.mdata.BillListButtonClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.law.LawOrRuleImportDialog;

/**
 * 内部制度维护!!
 * 
 * @author xch
 * 
 */
public class RuleRecordWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener, BillListButtonActinoListener {

	private BillListPanel billList_rule;
	private WLTButton btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
	private WLTButton btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
	private WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
	private WLTButton btn_showword; // word导出
	private WLTButton btn_exportallword; // word批量导出
	private WLTButton btn_editRuleitem = null, btn_import; //
	private TBUtil tbutil = new TBUtil();
	private boolean ifHaveItem = tbutil.getSysOptionBooleanValue("制度是否分条目", true);	

	public void initialize() {
		currInit("RULE_RULE_CODE1");
	}

	/*
	 * 在做内控产品和合规产品用的表结构相同，但是模板中部分参照不同。所有以后写类最好再搞一层。可以把所有模板都传进来。
	 */
	public void currInit(String _mainTemplet) {
		billList_rule = new BillListPanel(_mainTemplet);
		if (billList_rule.getQuickQueryPanel().getCompentByKey("primarykey") != null) {
			billList_rule.getQuickQueryPanel().setVisiable("primarykey", false); // 隐藏关键字查询的文本框!
		}
		btn_delete.addActionListener(this);
		btn_showword = new WLTButton("Word导出", "zt_009.gif");
		btn_showword.addActionListener(this);
		btn_exportallword = new WLTButton("Word批量导出", "report_go.png");
		btn_exportallword.addActionListener(this);
		btn_update.addActionListener(this);
		
		/*
		//佛山禅城提出，非管理员admin不允许导出制度，故优化，可菜单配置是否显示【word导出】和【word批量导出】 【李春娟/2015-06-19】
		String showExport = this.getMenuConfMapValueAsStr("word导出", "Y;N");//这里可配置两个值，分别代表是否显示【word导出】和是否显示【word批量导出】，如果是一个值，则全部显示或不显示。
		boolean expOne = true;//为兼容考虑，默认显示【word导出】
		boolean expAll = false;//为兼容考虑，默认不显示【word批量导出】
		if (showExport != null && showExport.length() > 0) {
			if (showExport.contains(";")) {
				String[] shows = TBUtil.getTBUtil().split(showExport, ";");
				if (!"Y".equalsIgnoreCase(shows[0])) {
					expOne = false;
				}
				if (shows.length > 1 && "Y".equalsIgnoreCase(shows[1])) {
					expAll = true;
				}
			} else if ("Y".equalsIgnoreCase(showExport.trim()) || "Y;".equalsIgnoreCase(showExport.trim())) {
				expAll = true;
			} else {
				expOne = false;
			}
		}
		
		if (ifHaveItem) {
			btn_editRuleitem = new WLTButton("编辑条目"); //
			btn_editRuleitem.addActionListener(this); //
			btn_import = new WLTButton("导入");
			btn_import.addActionListener(this);
			if ((expOne && expAll) || usercode.equalsIgnoreCase("admin")) {//如果都显示，或者是管理员admin
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import, btn_showword, btn_exportallword });
			} else if (expOne) {//显示【word导出】，不显示【word批量导出】
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import, btn_showword });
			} else if (expAll) {//不显示【word导出】，显示【word批量导出】
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import, btn_exportallword });
			} else {//不显示【word导出】，不显示【word批量导出】
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import });
			}

		} else {
			if ((expOne && expAll) || usercode.equalsIgnoreCase("admin")) {//如果都显示，或者是管理员admin
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_showword, btn_exportallword });
			} else if (expOne) {//显示【word导出】，不显示【word批量导出】
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_showword });
			} else if (expAll) {//不显示【word导出】，显示【word批量导出】
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_exportallword });
			} else {//不显示【word导出】，不显示【word批量导出】
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete });
			}
		}
		*/
		
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
			
		if (ifHaveItem) {
			btn_editRuleitem = new WLTButton("编辑条目"); //
			btn_editRuleitem.addActionListener(this); //
			btn_import = new WLTButton("导入");
			btn_import.addActionListener(this);			
			
			if (usercode.equalsIgnoreCase("admin")) {//如果是admin，都显示
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import, btn_showword, btn_exportallword });
			} else if (expOne) {//只显示【word导出】
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import, btn_showword });
			} else {//都不显示
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import });
			}

		} else {
			if (usercode.equalsIgnoreCase("admin")) {//如果是admin，都显示
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_showword, btn_exportallword });
			} else if (expOne) {//只显示【word导出】
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_showword });
			} else {//都不显示
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete });
			}
		}
		billList_rule.addBillListHtmlHrefListener(this);
		billList_rule.addBillListButtonActinoListener(this);
		billList_rule.repaintBillListButton();
		this.add(billList_rule);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_editRuleitem) {
			onEditRuleItem();
		} else if (obj == btn_delete) {
			onDeleteRuleItem();
		} else if (obj == btn_import) {
			onImport();
		} else if (obj == btn_showword) {
			onWordshow();
		} else if (obj == btn_exportallword) {
			onExportAllshow();//word批量导出
		} else if (obj == btn_update) {
			onEditRule();
		}
	}

	/*
	 * 修改按钮逻辑【李春娟/2015-12-29】
	 */
	private void onEditRule() {
		BillVO billVO = billList_rule.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel(billList_rule.getTempletVO());
		cardPanel.setBillVO(billVO); //
		String refrule2 = billVO.getStringValue("refrule2", "");
		String formids = billVO.getStringValue("formids", "");

		BillCardDialog dialog = new BillCardDialog(this, billList_rule.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			if (billList_rule.getSelectedRow() != -1) {
				billList_rule.setBillVOAt(billList_rule.getSelectedRow(), dialog.getBillVO());
				billList_rule.setRowStatusAs(billList_rule.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
			BillVO billvo = dialog.getBillVO();
			if (billvo == null) {
				return;
			}
			String new_refrule2 = billvo.getStringValue("refrule2", "");
			String new_formids = billvo.getStringValue("formids", "");
			if (!refrule2.equals(new_refrule2) || !formids.equals(new_formids)) {
				insertAlarm(billvo, "update");
			}
		}

	}

	private void onImport() {
		LawOrRuleImportDialog importDialog = new LawOrRuleImportDialog(billList_rule, "内部制度导入", 800, 700, "rule");
		importDialog.setVisible(true);
		if (importDialog.getCloseType() == 1) {
			billList_rule.refreshData();
		}
	}

	private void onDeleteRuleItem() {
		try {
			BillVO[] selectRule = billList_rule.getSelectedBillVOs();
			if (selectRule.length == 0) {
				MessageBox.showSelectOne(this);
				return;
			} else {
				if (MessageBox.showConfirmDialog(this, "您确定删除这[" + selectRule.length + "]条记录吗?") != JOptionPane.YES_OPTION) {
					return;
				}
			}
			List list = new ArrayList();
			for (int i = 0; i < selectRule.length; i++) {
				list.add(selectRule[i].getStringValue("id"));
			}
			String insql = tbutil.getInCondition(list);
			List sqllist = new ArrayList();
			sqllist.add("delete from rule_rule where id in ( " + insql + " )"); // 删除外规主表数据
			sqllist.add("delete from rule_rule_item where ruleid in ( " + insql + ")");// 级联删除条文记录
			sqllist.add("delete from rule_eval_b where rule_eval_id in ( select id from rule_eval where ruleid in (" + insql + " ))"); //删除制度评价子表信息
			sqllist.add("delete from rule_eval where ruleid in(" + insql + ")"); //删除制度评价
			UIUtil.executeBatchByDS(null, sqllist);
			//			insertAlarm(selectRule,"delete");
			billList_rule.removeSelectedRows();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断并插入内规预警!
	 * 目前内规预警做的比较简陋，后期需要补充 对某条外规做了哪些修改，比如：修改了2条条目内容，删除了1条条目，新增了3条。
	 * 最好能做出cvs对比文字效果。
	 */
	public void insertAlarm(BillVO _selectRule, String _type) {
		try {

			TBUtil tbutil = TBUtil.getTBUtil();
			ArrayList insertSQLlist = new ArrayList();
			String currdate = UIUtil.getServerCurrDate();
			String userid = ClientEnvironment.getInstance().getLoginUserID();
			String userdept = ClientEnvironment.getInstance().getLoginUserDeptId();
			String refrule2 = _selectRule.getStringValue("refrule2", "");
			InsertSQLBuilder sql = new InsertSQLBuilder("rule_alarm");
			sql.putFieldValue("alarmsource", "内部制度--制度维护");
			sql.putFieldValue("alarmsourcetab", "rule_rule");
			sql.putFieldValue("state", "未处理");
			sql.putFieldValue("alarmdate", currdate);
			sql.putFieldValue("creator", userid);
			sql.putFieldValue("createdept", userdept);
			sql.putFieldValue("alarmsourcepk", _selectRule.getStringValue("id"));
			if (refrule2 != null && !"".equals(refrule2)) {
				String[] refruleids = TBUtil.getTBUtil().split(refrule2, ";");
				HashMap ruleMap = UIUtil.getHashMapBySQLByDS(null, "select id,rulename from rule_rule where id in(" + TBUtil.getTBUtil().getInCondition(refrule2) + ")");

				for (int m = 0; m < refruleids.length; m++) {
					//该制度被哪些制度关联了，进行提醒【李春娟/2015-12-29】
					HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select * from rule_rule where refrule like '%;" + refruleids[m] + ";%'");
					if (vos != null && vos.length > 0) {
						for (int i = 0; i < vos.length; i++) {
							sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_RULE_ALARM"));
							sql.putFieldValue("ruleid", vos[i].getStringValue("id"));
							sql.putFieldValue("rulecode", vos[i].getStringValue("rulecode"));
							sql.putFieldValue("rulename", vos[i].getStringValue("rulename"));
							sql.putFieldValue("alarmtargettab", "rule_rule");
							String rulename = refruleids[m];
							if (ruleMap.get(refruleids[m]) != null) {
								rulename = (String) ruleMap.get(refruleids[m]);
							}
							sql.putFieldValue("alarmreason", "《" + rulename + "》制度已废止，请更新相关制度!");
							insertSQLlist.add(sql.getSQL());
						}
					}
				}
				insertSQLlist.add("update rule_rule set state='失效' where id in(" + tbutil.getInCondition(refrule2) + ")");

			}
			//关联的表单【李春娟/2015-12-26】
			String formids = _selectRule.getStringValue("formids");
			if (formids != null && !"".equals(formids.trim())) {
				String tabname = tbutil.getSysOptionStringValue("制度关联的表单表名", "BSD_FORM");//表单里必须有code和name字段
				tabname = tabname.toLowerCase();
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select id,code,name from " + tabname + " where id in(" + tbutil.getInCondition(formids) + ")");
				if (vos != null && vos.length > 0) {
					sql.putFieldValue("alarmreason", "《" + _selectRule.getStringValue("rulename") + "》制度内容已修改，请更新相关表单!");
					for (int i = 0; i < vos.length; i++) {
						sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_RULE_ALARM"));
						sql.putFieldValue("ruleid", vos[i].getStringValue("id"));
						sql.putFieldValue("rulecode", vos[i].getStringValue("code"));
						sql.putFieldValue("rulename", vos[i].getStringValue("name"));
						sql.putFieldValue("alarmtargettab", tabname);
						insertSQLlist.add(sql.getSQL());
					}
				}
			}
			if (insertSQLlist.size() > 0) {
				UIUtil.executeBatchByDS(null, insertSQLlist);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onEditRuleItem() {
		BillVO selectRule = billList_rule.getSelectedBillVO();
		if (selectRule == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		RuleItemEditDialog dialog = new RuleItemEditDialog(this, "编辑条目(修改后需点击保存！)", 900, 600, selectRule); //
		dialog.setVisible(true); //
		if (dialog.isHaveChanged()) {
			insertAlarm(selectRule, "update");
		}
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().equals("rulename"))
			showRuleHtml();
	}

	private void showRuleHtml() {
		new RuleShowHtmlDialog(billList_rule); //统一调用一个打开 制度详细。
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

	public void onBillListAddButtonClicked(BillListButtonClickedEvent event) throws Exception {
		BillCardPanel card = event.getCardPanel();
		Object formids = card.getValueAt("formids");//相关表单
		if (formids == null) {
			return;
		}
		if (formids instanceof RefItemVO) {
			RefItemVO formVO = (RefItemVO) formids;

			String formNames = formVO.getName();
		}
	}

	public void onBillListAddButtonClicking(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListButtonClicked(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListDeleteButtonClicked(BillListButtonClickedEvent event) {

	}

	public void onBillListDeleteButtonClicking(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListEditButtonClicked(BillListButtonClickedEvent event) {

	}

	public void onBillListEditButtonClicking(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListLookAtButtonClicked(BillListButtonClickedEvent event) {

	}

	public void onBillListLookAtButtonClicking(BillListButtonClickedEvent event) {

	}
}
