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
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ImportRuleDialog extends BillDialog implements ActionListener, BillListHtmlHrefListener {
	private String cmpfileid;
	private String cmpfilename;
	private String processid;
	private String processcode;
	private String processname;
	private String activityid;
	private String activitycode;
	private String activityname;
	private String wftype;
	private boolean editable;
	private WLTButton btn_import, btn_deleterule, btn_showall, btn_confirm, btn_close, btn_linkAll;
	private BillListPanel billlist_rule, billlist_importrule;
	private BillListDialog dialog_rule;
	private HashMap tempMap = new HashMap();
	private HashMap addtempMap = new HashMap();
	private ArrayList insertsqls = new ArrayList();

	private String loginuserid = ClientEnvironment.getInstance().getLoginUserID(); //登录用户的id

	public ImportRuleDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, boolean _editable) {
		this(_parent, _title, _cmpfileid, _cmpfilename, _processid, _processcode, _processname, null, null, null, WFGraphEditItemPanel.TYPE_WF, _editable);
	}

	public ImportRuleDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, String _activityid, String _activitycode, String _activityname, String _wftype, boolean _editable) {
		super(_parent, _title, 900, 650);
		this.setLayout(new BorderLayout()); //
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
		billlist_rule = new BillListPanel("CMP_CMPFILE_RULE_CODE1");
		billlist_rule.addBillListHtmlHrefListener(this);
		if (WFGraphEditItemPanel.TYPE_WF.equals(this.wftype)) {
			billlist_rule.getTempletItemVO("wfactivity_name").setCardisshowable(false);
			billlist_rule.setDataFilterCustCondition("relationtype='" + WFGraphEditItemPanel.TYPE_WF + "' and  wfprocess_id=" + this.processid);
			billlist_rule.QueryDataByCondition(null);
		} else {
			billlist_rule.setDataFilterCustCondition("wfactivity_id=" + this.activityid);
			billlist_rule.QueryDataByCondition(null);
		}
		if (editable) {
			if (billlist_rule.getRowCount() > 0) {
				BillVO[] billvos = billlist_rule.getAllBillVOs();
				for (int i = 0; i < billvos.length; i++) {
					tempMap.put(billvos[i].getStringValue("rule_id"), billvos[i]);
				}
			}
			btn_import = new WLTButton("关联制度");
			btn_import.addActionListener(this);
			btn_deleterule = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
			btn_deleterule.addActionListener(this);
			billlist_rule.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			billlist_rule.addBatchBillListButton(new WLTButton[] { btn_import, btn_deleterule });
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
				onImport();
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
		if (e.getSource() == btn_import) {
			onImport();
		} else if (e.getSource() == btn_deleterule) {
			onDeleterule();
		} else if (e.getSource() == btn_linkAll) { //
			onImportRule();
		} else if (e.getSource() == btn_showall) {
			onShow();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_close) {
			onClose();
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

	private void onImport() {
		dialog_rule = new BillListDialog(this, "关联制度", "RULE_RULE_CODE2", 700, 640);
		BillListPanel billlist_rule = dialog_rule.getBilllistPanel();
		billlist_rule.addBillListHtmlHrefListener(this);
		billlist_rule.setItemVisible("showdetail", false);
		billlist_rule.setItemVisible("evalcount", false);
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //卡片浏览按钮
		btn_linkAll = dialog_rule.getBtn_confirm();
		btn_linkAll.setText("关联全文");
		btn_linkAll.setToolTipText("关联全文");
		btn_linkAll.setPreferredSize(new Dimension(100, 23));
		btn_linkAll.setIcon(UIUtil.getImage("book_link.png"));
		btn_linkAll.addActionListener(this);

		dialog_rule.getBtn_cancel().setVisible(false);
		billlist_rule.addBillListButton(btn_show);
		billlist_rule.repaintBillListButton();
		billlist_rule.setRowNumberChecked(true);
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
			tempMap.remove(billvo.getStringValue("rule_id"));
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onImportRule() {
		BillVO checkedVOs[] = dialog_rule.getBilllistPanel().getCheckedBillVOs();
		ArrayList sqlList = new ArrayList();
		String ruleitem_title = "【全文】";
		String ruleitem_content = "【全文】";
		String key = "";
		if (checkedVOs.length < 1) {
			MessageBox.show(this,"请至少选择一条记录,执行此操作");
			return;
		}		
		for (BillVO billVO : checkedVOs) {
			key = billVO.getStringValue("id");
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

	private void onShow() {
		if (tempMap.size() + addtempMap.size() == 0) {//如果没有相关的检查要点，则提示加入后再查看【李春娟/2012-03-27】
			MessageBox.show(this, "请先加入再进行查看!");
			return;
		}
		BillListDialog billdialog = new BillListDialog(dialog_rule, "所有相关制度", "CMP_CMPFILE_RULE_CODE1", 850, 550);
		BillListPanel billlist_showrule = billdialog.getBilllistPanel();
		if (cmpfileid == null) {
			billlist_showrule.getTempletItemVO("cmpfile_name").setCardisshowable(false);//如果不是流程文件里的关联，则不显示流程文件名称【李春娟/2012-05-11】
			billlist_showrule.setItemVisible("cmpfile_name", false);//因为上面的billlist_showrule 已经初始化完了，故用setListisshowable(false) 是不起作用的，只能这样设置。
		}
		billlist_showrule.addBillListHtmlHrefListener(this);
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD2); //
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
		dialog_rule.setCloseType(1);
		dialog_rule.dispose();
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		onShowRule(_event.getBillListPanel());
	}

	private void onShowRule(BillListPanel _listpanel) {//该类是在制度不分条文时调用的，所以制度链接应该打开正文
		BillVO billvo = _listpanel.getSelectedBillVO();
		String textFile = null;
		if (billlist_importrule == _listpanel) {//制度选择页面
			textFile = billvo.getStringValue("textfile");
		} else {//主页面 和 查看页面的链接事件
			try {
				textFile = UIUtil.getStringValueByDS(null, "select textfile from rule_rule where id=" + billvo.getStringValue("rule_id"));
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
		if (textFile == null || textFile.equals("")) {
			MessageBox.show(this, "该制度没有正文！");
			return;
		} else {
			UIUtil.openRemoteServerFile("office", textFile);
		}
	}

	public BillListPanel getBilllist_rule() {
		return billlist_rule;
	}
}
