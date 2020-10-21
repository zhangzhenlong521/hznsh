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

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

public class ImportCheckPointDialog extends BillDialog implements ActionListener, BillTreeSelectListener {
	private String cmpfileid;
	private String cmpfilename;
	private String processid;
	private String processcode;
	private String processname;
	private String activityid;
	private String activitycode;
	private String activityname;
	private WLTButton btn_import, btn_deletecheckpoint, btn_importcheckpoint, btn_showall, btn_confirm, btn_cancel, btn_close;
	private String wftype;
	private BillListPanel billlist_checkpoint, billlist_importcheckpoint;
	private BillTreePanel billtree_checktype;
	private BillDialog dialog_checkpoint;
	private HashMap tempMap = new HashMap();
	private HashMap addtempMap = new HashMap();
	private ArrayList insertsqls = new ArrayList();
	private boolean editable;//流程图是否可编辑

	public ImportCheckPointDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, boolean _editable) {
		this(_parent, _title, _cmpfileid, _cmpfilename, _processid, _processcode, _processname, null, null, null, WFGraphEditItemPanel.TYPE_WF, _editable);
	}

	public ImportCheckPointDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, String _activityid, String _activitycode, String _activityname, String _wftype, boolean _editable) {
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
		billlist_checkpoint = new BillListPanel("CMP_CMPFILE_CHECKPOINT_CODE1");
		if (WFGraphEditItemPanel.TYPE_WF.equals(this.wftype)) {
			billlist_checkpoint.getTempletItemVO("wfactivity_name").setCardisshowable(false);
			billlist_checkpoint.setDataFilterCustCondition("relationtype='" + WFGraphEditItemPanel.TYPE_WF + "' and wfprocess_id=" + this.processid);
		} else {
			billlist_checkpoint.setDataFilterCustCondition("wfactivity_id=" + this.activityid);
		}
		billlist_checkpoint.QueryDataByCondition(null);
		if (editable) {
			if (billlist_checkpoint.getRowCount() > 0) {
				BillVO[] billvos = billlist_checkpoint.getAllBillVOs();
				for (int i = 0; i < billvos.length; i++) {
					tempMap.put(billvos[i].getStringValue("checkpoint_id"), billvos[i]);
				}
			}
			btn_import = new WLTButton("关联检查要点");
			btn_import.addActionListener(this);
			btn_deletecheckpoint = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
			btn_deletecheckpoint.addActionListener(this);
			billlist_checkpoint.addBatchBillListButton(new WLTButton[] { btn_import, btn_deletecheckpoint });
		}
		billlist_checkpoint.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		billlist_checkpoint.repaintBillListButton();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billlist_checkpoint, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * 判断是否显示本窗口，如果没有相关信息，则提示是否新增，如果选择【是】，则弹出新增界面，如果选择【否】则不显示本窗口。【李春娟/2012-05-28】
	 * @return
	 */
	public boolean isShowDialog() {
		if (this.editable && billlist_checkpoint.getRowCount() == 0) {
			if (MessageBox.confirm(this, "该" + this.wftype + "没有相关检查要点,是否新增?")) {//如果可编辑并且列表中没有记录，则提示是否新增【韩静/2012-03-13】
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
		} else if (e.getSource() == btn_deletecheckpoint) {
			onDeletecheckpoint();
		} else if (e.getSource() == btn_importcheckpoint) { //
			onImportRule();
		} else if (e.getSource() == btn_showall) {
			onShow();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
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
		dialog_checkpoint = new BillDialog(this, "请选择一条检查要点，点击加入", 850, 600);
		billtree_checktype = new BillTreePanel("BSD_CHECKTYPE_CODE1");
		billtree_checktype.setMoveUpDownBtnVisiable(false);//设置工具条的上移下移按钮不可见【李春娟/2012-03-13】
		billtree_checktype.queryDataByCondition(null);
		billtree_checktype.addBillTreeSelectListener(this);
		billlist_importcheckpoint = new BillListPanel("BSD_CHECKITEM_CODE1");
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //卡片浏览按钮
		btn_importcheckpoint = new WLTButton("加入", "office_199.gif");
		btn_showall = new WLTButton("查看(" + tempMap.size() + ")", "office_062.gif");
		btn_importcheckpoint.addActionListener(this);
		btn_showall.addActionListener(this);
		billlist_importcheckpoint.addBatchBillListButton(new WLTButton[] { btn_importcheckpoint, btn_showall, btn_show });
		billlist_importcheckpoint.repaintBillListButton();

		WLTSplitPane splitPanel = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billtree_checktype, billlist_importcheckpoint);
		splitPanel.setDividerLocation(230); //

		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		panel.setLayout(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);
		panel.add(btn_confirm);
		panel.add(btn_cancel);

		dialog_checkpoint.getContentPane().add(splitPanel, BorderLayout.CENTER);
		dialog_checkpoint.getContentPane().add(panel, BorderLayout.SOUTH);
		dialog_checkpoint.setVisible(true);
		if (dialog_checkpoint.getCloseType() == -1) {
			addtempMap.clear();
			insertsqls.clear();
			dialog_checkpoint.dispose();
		}
	}

	private void onDeletecheckpoint() {
		BillVO billvo = billlist_checkpoint.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.showConfirmDialog(this, "您确定要删除吗?") != JOptionPane.YES_OPTION) {
			return; //
		}
		try {
			UIUtil.executeUpdateByDS(null, "delete from cmp_cmpfile_checkpoint where id=" + billvo.getStringValue("id"));
			billlist_checkpoint.removeRow(billlist_checkpoint.getSelectedRow()); //
			MessageBox.show(this, "删除成功!");
			tempMap.remove(billvo.getStringValue("checkpoint_id"));
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onImportRule() {
		try {
			BillVO billvo = billlist_importcheckpoint.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.showSelectOne(this);
				return;
			}
			String checkpointid = billvo.getStringValue("id");
			if (tempMap.containsKey(checkpointid) || addtempMap.containsKey(checkpointid)) {
				MessageBox.show(this, "该检查要点已加入，请勿重复加入!");
				return;
			}
			BillVO billvo1 = null;
			if (billlist_checkpoint.getRowCount() > 0) {
				billvo1 = billlist_checkpoint.getBillVO(0);
			} else {
				int a = billlist_checkpoint.addEmptyRow();
				billvo1 = billlist_checkpoint.getBillVO(a);
				billlist_checkpoint.removeRow();
			}
			String checktype_name = UIUtil.getStringValueByDS(null, "select name from BSD_CHECKTYPE where id =" + billvo.getStringValue("checktype_id"));
			billvo1.setObject("cmpfile_name", new StringItemVO(this.cmpfilename));
			billvo1.setObject("wfprocess_code", new StringItemVO(this.processcode));
			billvo1.setObject("wfprocess_name", new StringItemVO(this.processname));
			billvo1.setObject("wfactivity_name", new StringItemVO(this.activityname));
			billvo1.setObject("checkpoint_id", new StringItemVO(checkpointid));
			billvo1.setObject("checktype_id", new RefItemVO(billvo.getStringValue("checktype_id"), "", checktype_name));
			billvo1.setObject("checktype_name", new StringItemVO(checktype_name));
			billvo1.setObject("checkitem_project", new StringItemVO(billvo.getStringValue("checkproject")));
			billvo1.setObject("checkitem_point", new StringItemVO(billvo.getStringValue("checkpointcontent")));
			billvo1.setObject("checkitem_method", new StringItemVO(billvo.getStringValue("checkmethod")));
			addtempMap.put(checkpointid, billvo1);

			InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_checkpoint"); // 
			String str_id = UIUtil.getSequenceNextValByDS(null, "S_CMP_CMPFILE_CHECKPOINT");
			isql_insert.putFieldValue("id", str_id);
			isql_insert.putFieldValue("cmpfile_id", this.cmpfileid);
			isql_insert.putFieldValue("cmpfile_name", this.cmpfilename);
			isql_insert.putFieldValue("wfprocess_id", this.processid);
			isql_insert.putFieldValue("wfprocess_code", this.processcode);
			isql_insert.putFieldValue("wfprocess_name", this.processname);
			isql_insert.putFieldValue("checkpoint_id", checkpointid);
			isql_insert.putFieldValue("checktype_id", billvo.getStringValue("checktype_id"));
			isql_insert.putFieldValue("checktype_name", checktype_name);
			isql_insert.putFieldValue("checkitem_project", billvo.getStringValue("checkproject"));
			isql_insert.putFieldValue("checkitem_point", billvo.getStringValue("checkpointcontent"));
			isql_insert.putFieldValue("checkitem_method", billvo.getStringValue("checkmethod"));
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
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	private void onShow() {
		if (tempMap.size() + addtempMap.size() == 0) {//如果没有相关的检查要点，则提示加入后再查看【李春娟/2012-03-27】
			MessageBox.show(this, "请先加入再进行查看!");
			return;
		}
		BillListDialog billdialog = new BillListDialog(dialog_checkpoint, "所有相关检查要点", "CMP_CMPFILE_CHECKPOINT_CODE1", 850, 550);
		BillListPanel billlist_showcheckpoint = billdialog.getBilllistPanel();
		if (cmpfileid == null) {
			billlist_showcheckpoint.getTempletItemVO("cmpfile_name").setCardisshowable(false);//如果不是流程文件里的关联，则不显示流程文件名称【李春娟/2012-05-11】
			billlist_showcheckpoint.setItemVisible("cmpfile_name", false);//因为上面的billlist_showcheckpoint 已经初始化完了，故用setListisshowable(false) 是不起作用的，只能这样设置。
		}
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD2); //
		btn_show.setText("浏览");
		btn_show.setPreferredSize(new Dimension(45, 23));
		billlist_showcheckpoint.addBillListButton(btn_show);
		billlist_showcheckpoint.repaintBillListButton();
		if (WFGraphEditItemPanel.TYPE_WF.equals(this.wftype)) {
			billlist_showcheckpoint.setItemVisible("wfactivity_name", false);
		}
		billlist_showcheckpoint.setQuickQueryPanelVisiable(false);
		Iterator it1 = tempMap.keySet().iterator();
		Iterator it2 = addtempMap.keySet().iterator();
		while (it1.hasNext()) {
			BillVO billvo = (BillVO) tempMap.get(it1.next());
			billlist_showcheckpoint.addRow(billvo);
		}
		while (it2.hasNext()) {
			BillVO billvo = (BillVO) addtempMap.get(it2.next());
			billlist_showcheckpoint.addRow(billvo);
		}
		billdialog.getBtn_confirm().setVisible(false);
		billdialog.getBtn_cancel().setText("关闭");
		billdialog.setVisible(true);
	}

	private void onConfirm() {
		if (insertsqls.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, insertsqls);
				billlist_checkpoint.QueryDataByCondition(null);
				tempMap.putAll(addtempMap);
				addtempMap.clear();
				insertsqls.clear();
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
		dialog_checkpoint.setCloseType(1);
		dialog_checkpoint.dispose();
	}

	private void onCancel() {
		addtempMap.clear();
		insertsqls.clear();
		dialog_checkpoint.setCloseType(2);
		dialog_checkpoint.dispose();
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billvo = _event.getCurrSelectedVO();
		if (billvo == null) {
			billlist_importcheckpoint.clearTable();
			return;
		}
		billlist_importcheckpoint.QueryDataByCondition("checktype_id=" + billvo.getStringValue("id"));
	}

	public BillListPanel getBilllist_checkpoint() {
		return billlist_checkpoint;
	}
}
