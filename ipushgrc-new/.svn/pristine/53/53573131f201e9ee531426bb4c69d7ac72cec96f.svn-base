package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

import com.pushworld.ipushgrc.ui.rule.p010.RuleShowHtmlDialog;

public class ImportRuleItemDialog extends BillDialog implements BillTreeSelectListener, ActionListener, BillListHtmlHrefListener {
	private String cmpfileid;
	private String cmpfilename;
	private String processid;
	private String processcode;
	private String processname;
	private String activityid;
	private String activitycode;
	private String activityname;
	private String ruleid;
	private String rulename;
	private String wftype;
	private boolean editable;
	private WLTButton btn_importrule, btn_deleterule, btn_importruleitem, btn_linkAll, btn_linkItem, btn_showall, btn_confirm, btn_cancel, btn_close;
	private BillTreePanel treePanel = null; //树型面板
	private BillCardPanel cardPanel = null;
	private BillListPanel billlist_rule, billlist_importrule;
	private BillListDialog dialog_rule;
	private BillDialog dialog_ruleitem;
	private HashMap tempMap = new HashMap();
	private HashMap addtempMap = new HashMap();
	private ArrayList insertsqls = new ArrayList();
	private boolean canClose = false;
	private String loginuserid = ClientEnvironment.getInstance().getLoginUserID(); //登录用户的id

	public ImportRuleItemDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, boolean _editable) {
		this(_parent, _title, _cmpfileid, _cmpfilename, _processid, _processcode, _processname, null, null, null, WFGraphEditItemPanel.TYPE_WF, _editable);
	}

	public ImportRuleItemDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, String _activityid, String _activitycode, String _activityname, String _wftype, boolean _editable) {
		super(_parent, _title, 900, 650);
		this.getContentPane().setLayout(new BorderLayout()); //
		this.cmpfileid = _cmpfileid;
		this.cmpfilename = _cmpfilename;
		this.processid = _processid;
		this.processcode = _processcode;
		this.processname = _processname;
		this.activityid = _activityid;
		this.activitycode = _activitycode;
		this.activityname = _activityname;
		this.wftype = _wftype;
		this.editable = _editable;
		billlist_rule = new BillListPanel("CMP_CMPFILE_RULE_CODE2");
		billlist_rule.addBillListHtmlHrefListener(this);
		if (WFGraphEditItemPanel.TYPE_WF.equals(this.wftype)) {
			billlist_rule.getTempletItemVO("wfactivity_name").setCardisshowable(false);
			billlist_rule.setDataFilterCustCondition("relationtype='" + WFGraphEditItemPanel.TYPE_WF + "' and wfprocess_id=" + this.processid);
			billlist_rule.QueryDataByCondition(null);
		} else {
			billlist_rule.setDataFilterCustCondition("wfactivity_id=" + this.activityid);
			billlist_rule.QueryDataByCondition(null);
		}
		if (editable) {
			if (billlist_rule.getRowCount() > 0) {
				BillVO[] billvos = billlist_rule.getAllBillVOs();
				for (int i = 0; i < billvos.length; i++) {
					tempMap.put(billvos[i].getStringValue("rule_id") + "-" + billvos[i].getStringValue("ruleitem_id"), billvos[i]);
				}
			}
			btn_importrule = new WLTButton("关联制度");
			btn_importrule.addActionListener(this);
			btn_deleterule = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
			btn_deleterule.addActionListener(this);
			billlist_rule.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			billlist_rule.addBatchBillListButton(new WLTButton[] { btn_importrule, btn_deleterule });
		}
		billlist_rule.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		billlist_rule.repaintBillListButton();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billlist_rule, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * 判断是否显示本窗口，如果没有相关信息，则提示是否新增，如果选择【是】，则弹出新增界面，如果选择【否】则不显示本窗口。【李春娟/2012-05-28】
	 * @return
	 */
	public boolean isShowDialog() {
		if (this.editable && billlist_rule.getRowCount() == 0) {
			if (MessageBox.confirm(this, "该" + this.wftype + "没有相关制度,是否新增?")) {//如果可编辑并且列表中没有记录，则提示是否新增【韩静/2012-03-13】
				onImportRule();
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_importrule) {
			onImportRule();
		} else if (e.getSource() == btn_deleterule) {
			onDeleterule();
		} else if (e.getSource() == btn_linkItem) {
			onNext();
		} else if (e.getSource() == btn_importruleitem) { //
			onImportRuleItem();
		} else if (e.getSource() == btn_showall) {
			onShow();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == btn_close) {
			onClose();
		} else if (e.getSource() == btn_linkAll) {// 关联制度全文
			onLinkAll();
		}
	}

	private void onClose() {
		this.dispose();
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_close = new WLTButton("关闭");
		btn_close.addActionListener(this); //
		panel.add(btn_close); //
		return panel;
	}

	private void onImportRule() {
		dialog_rule = new BillListDialog(this, "请选择一条制度，点击下一步", "RULE_RULE_CODE2", 700, 640);
		billlist_importrule = dialog_rule.getBilllistPanel();
		billlist_importrule.addBillListHtmlHrefListener(this);
		billlist_importrule.setItemVisible("showdetail", false);
		billlist_importrule.setItemVisible("evalcount", false);

		btn_linkItem = dialog_rule.getBtn_cancel();
		btn_linkItem.setText("关联条目");
		btn_linkItem.setToolTipText("关联条目");
		btn_linkItem.setPreferredSize(new Dimension(100, 23));
		btn_linkItem.setIcon(UIUtil.getImage("page_link.png"));
		btn_linkItem.addActionListener(this);

		btn_linkAll = dialog_rule.getBtn_confirm();
		btn_linkAll.setText("关联全文");
		btn_linkAll.setToolTipText("关联全文");
		btn_linkAll.setPreferredSize(new Dimension(100, 23));
		btn_linkAll.setIcon(UIUtil.getImage("book_link.png"));
		btn_linkAll.addActionListener(this);

		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //卡片浏览按钮
		billlist_importrule.addBatchBillListButton(new WLTButton[] { btn_show });
		billlist_importrule.repaintBillListButton();
		billlist_importrule.setRowNumberChecked(true);
		dialog_rule.setVisible(true);
	}

	private void onDeleterule() {
		BillVO billvo = billlist_rule.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.showConfirmDialog(this, "您确定要删除吗?") != JOptionPane.YES_OPTION) {
			return; //
		}
		try {
			UIUtil.executeUpdateByDS(null, "delete from cmp_cmpfile_rule where id=" + billvo.getStringValue("id"));
			billlist_rule.removeRow(billlist_rule.getSelectedRow()); //
			MessageBox.show(this, "删除成功!");
			tempMap.remove(billvo.getStringValue("rule_id") + "-" + billvo.getStringValue("ruleitem_id"));
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onNext() {
		if (dialog_rule.getBilllistPanel().getCheckedBillVOs().length != 1) {
			MessageBox.showSelectOne(this);
			return; //
		}
		BillVO billvo_rule = dialog_rule.getBilllistPanel().getCheckedBillVOs()[0];
		dialog_rule.dispose();//关闭制度页面
		this.ruleid = billvo_rule.getStringValue("id");
		this.rulename = billvo_rule.getStringValue("rulename");

		dialog_ruleitem = new BillDialog(this, "请选择一条制度条文，点击加入", 800, 600);//制度条文
		WLTSplitPane splitPanel = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, getTreePanel(), getCardPanel());
		splitPanel.setDividerLocation(230); //
		canClose = false;
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		panel.setLayout(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);
		panel.add(btn_confirm);
		panel.add(btn_cancel);

		dialog_ruleitem.getContentPane().add(splitPanel, BorderLayout.CENTER);
		dialog_ruleitem.getContentPane().add(panel, BorderLayout.SOUTH);
		dialog_ruleitem.setVisible(true);
		if (dialog_ruleitem.getCloseType() == -1) {
			this.onCancel();
		}
	}

	private BillTreePanel getTreePanel() {
		treePanel = new BillTreePanel("RULE_RULE_ITEM_CODE1");
		treePanel.setMoveUpDownBtnVisiable(false);
		BillTreeNodeVO treenode = new BillTreeNodeVO(-1, this.rulename);
		treePanel.getRootNode().setUserObject(treenode);
		treePanel.queryDataByCondition("ruleid=" + this.ruleid);
		treePanel.addBillTreeSelectListener(this);
		return treePanel;
	}

	private BillCardPanel getCardPanel() {
		cardPanel = new BillCardPanel("RULE_RULE_ITEM_CODE1");
		btn_importruleitem = new WLTButton("加入", "office_199.gif");
		btn_showall = new WLTButton("查看(" + tempMap.size() + ")", "office_062.gif");
		btn_showall.addActionListener(this);
		btn_importruleitem.addActionListener(this);
		cardPanel.addBatchBillCardButton(new WLTButton[] { btn_importruleitem, btn_showall });
		cardPanel.repaintBillCardButton();
		return cardPanel;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billvo = _event.getCurrSelectedVO();
		if (billvo != null) {
			cardPanel.queryDataByCondition("id=" + billvo.getStringValue("id"));
		} else {
			cardPanel.clear();
		}
	}

	private void onImportRuleItem() {
		try {
			String ruleitem_id = null;
			String ruleitem_title = null;
			String ruleitem_content;
			if (treePanel.getSelectedNode() == treePanel.getRootNode()) {
				if (tempMap.containsKey(this.ruleid + "-null") || addtempMap.containsKey(this.ruleid + "-null")) {
					MessageBox.show(this, "该制度已加入，请勿重复加入!");
					return;
				}
				ruleitem_title = "【全文】";
				ruleitem_content = "【全文】";
			} else {
				BillVO billvo = treePanel.getSelectedVO();
				if (billvo == null) {
					MessageBox.show(this, "请选择一个节点进行此操作!");
					return;
				}
				ruleitem_id = billvo.getStringValue("id");
				ruleitem_title = billvo.getStringValue("itemtitle");
				ruleitem_content = billvo.getStringValue("itemcontent");
				if (tempMap.containsKey(this.ruleid + "-" + ruleitem_id) || addtempMap.containsKey(this.ruleid + "-" + ruleitem_id)) {
					MessageBox.show(this, "该制度条文已加入，请勿重复加入!");
					return;
				}
			}
			BillVO billvo = null;
			if (billlist_rule.getRowCount() > 0) {
				billvo = billlist_rule.getBillVO(0);
			} else {
				int a = billlist_rule.addEmptyRow();
				billvo = billlist_rule.getBillVO(a);
				billlist_rule.removeRow();
			}
			billvo.setObject("cmpfile_name", new StringItemVO(this.cmpfilename));
			billvo.setObject("wfprocess_code", new StringItemVO(this.processcode));
			billvo.setObject("wfprocess_name", new StringItemVO(this.processname));
			billvo.setObject("wfactivity_name", new StringItemVO(this.activityname));
			billvo.setObject("rule_id", new StringItemVO(this.ruleid));
			billvo.setObject("rule_name", new StringItemVO(this.rulename));
			billvo.setObject("ruleitem_title", new StringItemVO(ruleitem_title));
			billvo.setObject("ruleitem_content", new StringItemVO(ruleitem_content));
			addtempMap.put(this.ruleid + "-" + ruleitem_id, billvo);

			InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_rule"); // 
			String str_id = UIUtil.getSequenceNextValByDS(null, "S_CMP_CMPFILE_RULE");
			isql_insert.putFieldValue("id", str_id);
			isql_insert.putFieldValue("cmpfile_id", this.cmpfileid);
			isql_insert.putFieldValue("cmpfile_name", this.cmpfilename);
			isql_insert.putFieldValue("wfprocess_id", this.processid);
			isql_insert.putFieldValue("wfprocess_code", this.processcode);
			isql_insert.putFieldValue("wfprocess_name", this.processname);
			isql_insert.putFieldValue("rule_id", this.ruleid);
			isql_insert.putFieldValue("rule_name", this.rulename);
			isql_insert.putFieldValue("ruleitem_id", ruleitem_id);
			isql_insert.putFieldValue("ruleitem_title", ruleitem_title);
			isql_insert.putFieldValue("ruleitem_content", ruleitem_content);
			if (this.activityid == null) {
				isql_insert.putFieldValue("relationtype", WFGraphEditItemPanel.TYPE_WF);
			} else {
				isql_insert.putFieldValue("relationtype", WFGraphEditItemPanel.TYPE_ACTIVITY);
				isql_insert.putFieldValue("wfactivity_id", this.activityid);
				isql_insert.putFieldValue("wfactivity_code", this.activitycode);
				isql_insert.putFieldValue("wfactivity_name", this.activityname);
			}
			insertsqls.add(isql_insert.getSQL());
			btn_showall.setText("查看(" + (tempMap.size() + addtempMap.size()) + ")");
			//MessageBox.show(this, "加入成功!");//展开窗口太多，为减少点击次数，这里不提示了【李春娟/2015-04-21】
			canClose = true;
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	private void onShow() {
		if (tempMap.size() + addtempMap.size() == 0) {//如果没有相关的检查要点，则提示加入后再查看【李春娟/2012-03-27】
			MessageBox.show(this, "请先加入再进行查看!");
			return;
		}
		BillListDialog billdialog = new BillListDialog(dialog_ruleitem, "所有相关制度", "CMP_CMPFILE_RULE_CODE2", 850, 550);
		BillListPanel billlist_showrule = billdialog.getBilllistPanel();
		if (cmpfileid == null) {
			billlist_showrule.getTempletItemVO("cmpfile_name").setCardisshowable(false);//如果不是流程文件里的关联，则不显示流程文件名称【李春娟/2012-05-11】
			billlist_showrule.setItemVisible("cmpfile_name", false);//因为上面的billlist_showrule 已经初始化完了，故用setListisshowable(false) 是不起作用的，只能这样设置。
		}
		billlist_showrule.addBillListHtmlHrefListener(this);
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD2);
		btn_show.setText("浏览");
		btn_show.setPreferredSize(new Dimension(45, 23));
		billlist_showrule.addBillListButton(btn_show);
		billlist_showrule.repaintBillListButton();
		if (WFGraphEditItemPanel.TYPE_WF.equals(this.wftype)) {
			billlist_showrule.setItemVisible("wfactivity_name", false);
		}
		billlist_showrule.setQuickQueryPanelVisiable(false);
		Iterator it1 = tempMap.keySet().iterator();
		Iterator it2 = addtempMap.keySet().iterator();
		while (it1.hasNext()) {
			BillVO billvo = (BillVO) tempMap.get(it1.next());
			billlist_showrule.addRow(billvo);
		}
		while (it2.hasNext()) {
			BillVO billvo = (BillVO) addtempMap.get(it2.next());
			billlist_showrule.addRow(billvo);
		}
		billdialog.getBtn_confirm().setVisible(false);
		billdialog.getBtn_cancel().setText("关闭");
		billdialog.setVisible(true);
	}

	private void onConfirm() {
		if (!canClose) {
			MessageBox.show(this, "请选择一条法规条文，点击加入");
			return;
		}
		if (insertsqls.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, insertsqls);
				billlist_rule.QueryDataByCondition(null);
				tempMap.putAll(addtempMap);
				addtempMap.clear();
				insertsqls.clear();
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
		dialog_ruleitem.setCloseType(1);
		dialog_ruleitem.dispose();
	}

	private void onCancel() {
		addtempMap.clear();
		insertsqls.clear();
		dialog_ruleitem.setCloseType(2);
		dialog_ruleitem.dispose();
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		showHtmlDialog(_event.getBillListPanel());
	}

	/**
	 * 打开 html
	 * 
	 * @param _listPanel
	 */
	public void showHtmlDialog(BillListPanel _listpanel) {
		BillVO billvo = _listpanel.getSelectedBillVO();
		String str_ruleid = null;
		if ("".equals(billvo.getStringValue("rule_id", ""))) {
			str_ruleid = billvo.getStringValue("id");
		} else {
			str_ruleid = billvo.getStringValue("rule_id");
		}
		new RuleShowHtmlDialog(_listpanel, str_ruleid, null);
	}

	private void onLinkAll() {

		BillVO checkedVOs[] = dialog_rule.getBilllistPanel().getCheckedBillVOs();
		ArrayList sqlList = new ArrayList();
		String ruleitem_title = "【全文】";
		String ruleitem_content = "【全文】";
		String key = "";
		if (checkedVOs.length < 1) {
			MessageBox.show(this, "请至少选择一条记录,执行此操作");
			return;
		}
		for (BillVO billVO : checkedVOs) {
			key = billVO.getStringValue("id") + "-null";
			if (tempMap.containsKey(key)) {
				continue;
			}

			BillVO billvo = null;
			if (billlist_rule.getRowCount() > 0) {
				billvo = billlist_rule.getBillVO(0);
			} else {
				int a = billlist_rule.addEmptyRow();
				billvo = billlist_rule.getBillVO(a);
				billlist_rule.removeRow();
			}

			billvo.setObject("cmpfile_name", new StringItemVO(this.cmpfilename));
			billvo.setObject("wfprocess_code", new StringItemVO(this.processcode));
			billvo.setObject("wfprocess_name", new StringItemVO(this.processname));
			billvo.setObject("wfactivity_name", new StringItemVO(this.activityname));
			billvo.setObject("rule_id", new StringItemVO(billVO.getStringValue("id")));
			billvo.setObject("rule_name", new StringItemVO(billVO.getStringValue("rulename")));
			billvo.setObject("ruleitem_title", new StringItemVO(ruleitem_title));
			billvo.setObject("ruleitem_content", new StringItemVO(ruleitem_content));
			tempMap.put(key, billvo);

			InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_rule"); // 
			String str_id = null;
			try {
				str_id = UIUtil.getSequenceNextValByDS(null, "S_CMP_CMPFILE_RULE");
			} catch (Exception e) {
				e.printStackTrace();
			}
			isql_insert.putFieldValue("id", str_id);
			isql_insert.putFieldValue("cmpfile_id", this.cmpfileid);
			isql_insert.putFieldValue("cmpfile_name", this.cmpfilename);
			isql_insert.putFieldValue("wfprocess_id", this.processid);
			isql_insert.putFieldValue("wfprocess_code", this.processcode);
			isql_insert.putFieldValue("wfprocess_name", this.processname);
			isql_insert.putFieldValue("rule_id", billVO.getStringValue("id"));
			isql_insert.putFieldValue("rule_name", billVO.getStringValue("rulename"));
			isql_insert.putFieldValue("ruleitem_id", "");
			isql_insert.putFieldValue("ruleitem_title", ruleitem_title);
			isql_insert.putFieldValue("ruleitem_content", ruleitem_content);
			if (this.activityid == null) {
				isql_insert.putFieldValue("relationtype", WFGraphEditItemPanel.TYPE_WF);
			} else {
				isql_insert.putFieldValue("relationtype", WFGraphEditItemPanel.TYPE_ACTIVITY);
				isql_insert.putFieldValue("wfactivity_id", this.activityid);
				isql_insert.putFieldValue("wfactivity_code", this.activitycode);
				isql_insert.putFieldValue("wfactivity_name", this.activityname);
			}
			sqlList.add(isql_insert.getSQL());
		}
		if (sqlList.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, sqlList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			billlist_rule.QueryDataByCondition(null);
		}
		dialog_rule.dispose();
	}

	public BillListPanel getBilllist_rule() {
		return billlist_rule;
	}
}
