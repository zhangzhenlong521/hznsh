package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;

public class LookOpereqDialog extends BillDialog implements ActionListener {
	private String cmpfileid;
	private String cmpfilename;
	private String processid;
	private String processcode;
	private String processname;
	private String activityid;
	private String activitycode;
	private String activityname;
	private boolean editable;
	private WLTButton btn_showall;
	private WLTButton btn_delete, btn_confirm, btn_save, btn_cancel;
	private BillCardPanel billcard_wfopereq;

	public LookOpereqDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, String _activityid, String _activitycode, String _activityname, boolean _editable) {
		super(_parent, _title, 650, 600);
		this.setLayout(new BorderLayout()); //
		this.cmpfileid = _cmpfileid;
		this.cmpfilename = _cmpfilename;
		this.processid = _processid;
		this.processcode = _processcode;
		this.processname = _processname;
		this.activityid = _activityid;
		this.activitycode = _activitycode;
		this.activityname = _activityname;
		this.editable = _editable;
		billcard_wfopereq = new BillCardPanel("CMP_CMPFILE_WFOPEREQ_CODE1");
		if (cmpfileid == null) {
			billcard_wfopereq.setVisiable("cmpfile_name", false);//如果不是流程文件里的关联，则不显示流程文件名称。因为上面的billcard_wfopereq 已经初始化完了，故只用setCardisshowable(false) 是不起作用的，只能这样设置。【李春娟/2012-05-11】
		}
		BillVO[] billvos = null;
		try {
			billvos = UIUtil.getBillVOsByDS(null, "select * from cmp_cmpfile_wfopereq where wfactivity_id = " + this.activityid, billcard_wfopereq.getTempletVO());
			if (billvos == null || billvos.length == 0) {
				if (this.editable) {
					billcard_wfopereq.setEditState("INSERT");
					billcard_wfopereq.insertRow();
					billcard_wfopereq.setEditableByInsertInit();
					billcard_wfopereq.setRealValueAt("cmpfile_id", this.cmpfileid);
					billcard_wfopereq.setRealValueAt("cmpfile_name", this.cmpfilename);
					billcard_wfopereq.setRealValueAt("wfprocess_id", this.processid);
					billcard_wfopereq.setRealValueAt("wfprocess_code", this.processcode);
					billcard_wfopereq.setRealValueAt("wfprocess_name", this.processname);
					billcard_wfopereq.setRealValueAt("wfactivity_id", this.activityid);
					billcard_wfopereq.setRealValueAt("wfactivity_code", this.activitycode);
					billcard_wfopereq.setRealValueAt("wfactivity_name", this.activityname);
				}
			} else {
				billcard_wfopereq.setBillVO(billvos[0]);
				String task = billvos[0].getStringValue("task");
				if (task != null && task.contains(";")) {
					String dutyname = UIUtil.getStringValueByDS(null, "select dutyname from CMP_POSTDUTY where id=" + task.split(";")[1]);
					billcard_wfopereq.setRealValueAt("postduty", dutyname);
				}
				if (this.editable) {
					billcard_wfopereq.setEditState("UPDATE");
					billcard_wfopereq.setEditableByEditInit();
				}
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		if (editable) {
			btn_delete = new WLTButton("删除"); //
			btn_delete.addActionListener(this);
			billcard_wfopereq.addBillCardButton(btn_delete);
		}
		//btn_showall = new WLTButton("浏览所有"); //
		//btn_showall.addActionListener(this);
		//billcard_wfopereq.addBillCardButton(btn_showall);
		billcard_wfopereq.repaintBillCardButton();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billcard_wfopereq, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		if (editable) {
			btn_confirm = new WLTButton("确定");
			btn_save = new WLTButton("保存"); //
			btn_cancel = new WLTButton("取消");
			btn_confirm.addActionListener(this);
			btn_save.addActionListener(this);
			btn_cancel.addActionListener(this);
			panel.add(btn_confirm);
			panel.add(btn_save);
			panel.add(btn_cancel);
		} else {
			btn_cancel = new WLTButton("关闭");
			btn_cancel.addActionListener(this);
			panel.add(btn_cancel);
		}
		return panel;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_save) {
			onSave();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == btn_showall) {
			onShowAllWfopereq();
		}
	}

	private void onDelete() {
		try {
			if (MessageBox.showConfirmDialog(this, "您真的想删除该记录吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
			String str_sql = "delete from cmp_cmpfile_wfopereq  where id=" + billcard_wfopereq.getBillVO().getStringValue("id"); //
			UIUtil.executeUpdateByDS(null, str_sql); //提交数据库
			billcard_wfopereq.setEditState("INSERT");
			billcard_wfopereq.insertRow();
			billcard_wfopereq.setEditableByInsertInit();
			billcard_wfopereq.setRealValueAt("cmpfile_id", this.cmpfileid);
			billcard_wfopereq.setRealValueAt("cmpfile_name", this.cmpfilename);
			billcard_wfopereq.setRealValueAt("wfprocess_id", this.processid);
			billcard_wfopereq.setRealValueAt("wfprocess_code", this.processcode);
			billcard_wfopereq.setRealValueAt("wfprocess_name", this.processname);
			billcard_wfopereq.setRealValueAt("wfactivity_id", this.activityid);
			billcard_wfopereq.setRealValueAt("wfactivity_code", this.activitycode);
			billcard_wfopereq.setRealValueAt("wfactivity_name", this.activityname);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onShowAllWfopereq() {

	}

	private void onConfirm() {
		try {
			if (!billcard_wfopereq.checkValidate()) {
				return;
			}
			billcard_wfopereq.updateData();
			closeType = BillDialog.CONFIRM;
			this.dispose();
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onSave() {
		try {
			if (!billcard_wfopereq.checkValidate()) {
				return;
			}
			billcard_wfopereq.updateData();
			MessageBox.show(this, "保存成功!");
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onCancel() {
		closeType = BillDialog.CANCEL;
		this.dispose();
	}
}
