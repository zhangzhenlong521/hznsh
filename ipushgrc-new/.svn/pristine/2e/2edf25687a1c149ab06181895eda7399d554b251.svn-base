package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 流程和环节的相关罚则【李春娟/2014-09-24】
 * @author lcj
 *
 */
public class ImportPunishDialog extends BillDialog implements ActionListener {
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
	private WLTButton btn_import, btn_deletepunish, btn_importpunish, btn_showall, btn_confirm, btn_close;
	private BillListPanel billlist_punish, billlist_importpunish;
	private BillListDialog dialog_punish;
	private HashMap tempMap = new HashMap();
	private HashMap addtempMap = new HashMap();
	private ArrayList insertsqls = new ArrayList();
	private boolean canClose = false;

	public ImportPunishDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, boolean _editable) {
		this(_parent, _title, _cmpfileid, _cmpfilename, _processid, _processcode, _processname, null, null, null, WFGraphEditItemPanel.TYPE_WF, _editable);
	}

	public ImportPunishDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, String _activityid, String _activitycode, String _activityname, String _wftype, boolean _editable) {
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
		billlist_punish = new BillListPanel("CMP_CMPFILE_PUNISH_CODE1");
		if (WFGraphEditItemPanel.TYPE_WF.equals(this.wftype)) {
			billlist_punish.getTempletItemVO("wfactivity_name").setCardisshowable(false);
			billlist_punish.setDataFilterCustCondition("relationtype='" + WFGraphEditItemPanel.TYPE_WF + "' and wfprocess_id=" + this.processid);
		} else {
			billlist_punish.setDataFilterCustCondition("wfactivity_id=" + this.activityid);
		}
		billlist_punish.QueryDataByCondition(null);
		if (editable) {
			if (billlist_punish.getRowCount() > 0) {
				BillVO[] billvos = billlist_punish.getAllBillVOs();
				for (int i = 0; i < billvos.length; i++) {
					tempMap.put(billvos[i].getStringValue("stand_id"), billvos[i]);
				}
			}
			btn_import = new WLTButton("关联罚则");
			btn_import.addActionListener(this);
			btn_deletepunish = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
			btn_deletepunish.addActionListener(this);
			billlist_punish.addBatchBillListButton(new WLTButton[] { btn_import, btn_deletepunish });
		}
		billlist_punish.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		billlist_punish.repaintBillListButton();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billlist_punish, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * 判断是否显示本窗口，如果没有相关信息，则提示是否新增，如果选择【是】，则弹出新增界面，如果选择【否】则不显示本窗口。【李春娟/2012-05-28】
	 * @return
	 */
	public boolean isShowDialog() {
		if (this.editable && billlist_punish.getRowCount() == 0) {
			if (MessageBox.confirm(this, "该" + this.wftype + "没有相关罚则,是否新增?")) {//如果可编辑并且列表中没有记录，则提示是否新增【韩静/2012-03-13】
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
		} else if (e.getSource() == btn_deletepunish) {
			onDeletePunish();
		} else if (e.getSource() == btn_importpunish) { //
			onImportPunish();
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
		dialog_punish = new BillListDialog(this, "请选择一条记录，点击加入", "SCORE_STANDARD2_LCJ_Q01", 700, 640);
		billlist_importpunish = dialog_punish.getBilllistPanel();
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //卡片浏览按钮
		btn_importpunish = new WLTButton("加入", "office_199.gif");
		btn_showall = new WLTButton("查看(" + tempMap.size() + ")", "office_062.gif");
		btn_importpunish.addActionListener(this);
		btn_showall.addActionListener(this);
		billlist_importpunish.addBatchBillListButton(new WLTButton[] { btn_importpunish, btn_showall, btn_show });
		billlist_importpunish.repaintBillListButton();

		btn_confirm = dialog_punish.getBtn_confirm();
		btn_confirm.addActionListener(this);
		canClose = false;
		dialog_punish.setVisible(true);
		if (dialog_punish.getCloseType() == -1) {
			addtempMap.clear();
			insertsqls.clear();
			dialog_punish.dispose();
		}
	}

	private void onDeletePunish() {
		BillVO billvo = billlist_punish.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.showConfirmDialog(this, "您确定要删除吗?") != JOptionPane.YES_OPTION) {
			return; //
		}
		try {
			UIUtil.executeUpdateByDS(null, "delete from cmp_cmpfile_punish where id=" + billvo.getStringValue("id"));
			billlist_punish.removeRow(billlist_punish.getSelectedRow()); //
			MessageBox.show(this, "删除成功!");
			tempMap.remove(billvo.getStringValue("stand_id"));
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onImportPunish() {
		try {
			BillVO billvo = billlist_importpunish.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.showSelectOne(this);
				return;
			}
			String scorestandid = billvo.getStringValue("id");
			if (tempMap.containsKey(scorestandid) || addtempMap.containsKey(scorestandid)) {
				MessageBox.show(this, "该记录已加入，请勿重复加入!");
				return;
			}
			BillVO billvo1 = null;
			if (billlist_punish.getRowCount() > 0) {
				billvo1 = billlist_punish.getBillVO(0);
			} else {
				int a = billlist_punish.addEmptyRow();
				billvo1 = billlist_punish.getBillVO(a);
				billlist_punish.removeRow();
			}
			billvo1.setObject("cmpfile_name", new StringItemVO(this.cmpfilename));
			billvo1.setObject("wfprocess_name", new StringItemVO(this.processname));
			billvo1.setObject("wfactivity_name", new StringItemVO(this.activityname));
			billvo1.setObject("stand_id", new StringItemVO(billvo.getStringValue("id")));
			billvo1.setObject("stand_pointcode", new StringItemVO(billvo.getStringValue("pointcode")));
			billvo1.setObject("stand_point", new StringItemVO(billvo.getStringValue("point")));
			billvo1.setObject("stand_score", new StringItemVO(billvo.getStringValue("score")));
			billvo1.setObject("stand_scoretype", (RefItemVO)billvo.getObject("scoretype"));
			addtempMap.put(scorestandid, billvo1);

			InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_punish"); // 
			String str_id = UIUtil.getSequenceNextValByDS(null, "S_CMP_CMPFILE_PUNISH");
			isql_insert.putFieldValue("id", str_id);
			isql_insert.putFieldValue("cmpfile_id", this.cmpfileid);
			isql_insert.putFieldValue("cmpfile_name", this.cmpfilename);
			isql_insert.putFieldValue("wfprocess_id", this.processid);
			isql_insert.putFieldValue("wfprocess_code", this.processcode);
			isql_insert.putFieldValue("wfprocess_name", this.processname);

			isql_insert.putFieldValue("stand_id", billvo.getStringValue("id"));
			isql_insert.putFieldValue("stand_pointcode", billvo.getStringValue("pointcode"));
			isql_insert.putFieldValue("stand_point", billvo.getStringValue("point"));
			isql_insert.putFieldValue("stand_score", billvo.getStringValue("score"));
			isql_insert.putFieldValue("stand_scoretype", billvo.getStringValue("scoretype"));

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
		BillListDialog billdialog = new BillListDialog(dialog_punish, "所有相关罚则", "CMP_CMPFILE_PUNISH_CODE1", 850, 550);
		BillListPanel billlist_showpunish = billdialog.getBilllistPanel();
		billlist_showpunish.removeAllRows();//这个模板是自加载的，所以需要先清空数据
		if (cmpfileid == null) {
			billlist_showpunish.getTempletItemVO("cmpfile_name").setCardisshowable(false);//如果不是流程文件里的关联，则不显示流程文件名称【李春娟/2012-05-11】
			billlist_showpunish.setItemVisible("cmpfile_name", false);//因为上面的billlist_showpunish 已经初始化完了，故用setListisshowable(false) 是不起作用的，只能这样设置。
		}
		billlist_showpunish.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		billlist_showpunish.repaintBillListButton();
		if (WFGraphEditItemPanel.TYPE_WF.equals(this.wftype)) {
			billlist_showpunish.setItemVisible("wfactivity_name", false);
		}
		billlist_showpunish.setQuickQueryPanelVisiable(false);
		Iterator it1 = tempMap.keySet().iterator();
		Iterator it2 = addtempMap.keySet().iterator();
		while (it1.hasNext()) {
			BillVO billvo = (BillVO) tempMap.get(it1.next());
			billlist_showpunish.addRow(billvo);
		}
		while (it2.hasNext()) {
			BillVO billvo = (BillVO) addtempMap.get(it2.next());
			billlist_showpunish.addRow(billvo);
		}
		billdialog.getBtn_confirm().setVisible(false);
		billdialog.getBtn_cancel().setText("关闭");
		billdialog.setVisible(true);
	}

	private void onConfirm() {
		if (!canClose) {
			MessageBox.show(this, "请选择一条记录，点击加入");
			return;
		}
		if (insertsqls.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, insertsqls);
				billlist_punish.QueryDataByCondition(null);
				tempMap.putAll(addtempMap);
				addtempMap.clear();
				insertsqls.clear();
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
		dialog_punish.setCloseType(1);
		dialog_punish.dispose();
	}
}
