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

public class LookWfdescDialog extends BillDialog implements ActionListener {
	private String cmpfileid;
	private String cmpfilename;
	private String processid;
	private String processcode;
	private String processname;
	private boolean editable;
	private WLTButton btn_delete, btn_confirm, btn_save, btn_cancel;
	private BillCardPanel billcard_wfdesc;

	public LookWfdescDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, boolean _editable) {
		super(_parent, _title, 650, 600);
		this.setLayout(new BorderLayout()); //
		this.cmpfileid = _cmpfileid;
		this.cmpfilename = _cmpfilename;
		this.processid = _processid;
		this.processcode = _processcode;
		this.processname = _processname;
		this.editable = _editable;
		billcard_wfdesc = new BillCardPanel("CMP_CMPFILE_WFDESC_CODE1");
		if (cmpfileid == null) {
			billcard_wfdesc.setVisiable("cmpfile_name", false);//如果不是流程文件里的关联，则不显示流程文件名称。因为上面的billcard_wfdesc 已经初始化完了，故只用setCardisshowable(false) 是不起作用的，只能这样设置。【李春娟/2012-05-11】
		}
		BillVO[] billvos = null;
		try {
			billvos = UIUtil.getBillVOsByDS(null, "select * from cmp_cmpfile_wfdesc where wfprocess_id = " + this.processid, billcard_wfdesc.getTempletVO());
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		if (billvos == null || billvos.length == 0) {
			if (this.editable) {
				billcard_wfdesc.setEditState("INSERT");
				billcard_wfdesc.insertRow();
				billcard_wfdesc.setEditableByInsertInit();
				billcard_wfdesc.setRealValueAt("cmpfile_id", this.cmpfileid);
				billcard_wfdesc.setRealValueAt("cmpfile_name", this.cmpfilename);
				billcard_wfdesc.setRealValueAt("wfprocess_id", this.processid);
				billcard_wfdesc.setRealValueAt("wfprocess_code", this.processcode);
				billcard_wfdesc.setRealValueAt("wfprocess_name", this.processname);
			}
		} else {
			billcard_wfdesc.setBillVO(billvos[0]);
			if (this.editable) {
				billcard_wfdesc.setEditState("UPDATE");
				billcard_wfdesc.setEditableByEditInit();
			}
		}
		if (editable) {
			btn_delete = new WLTButton("删除"); //
			btn_delete.addActionListener(this);
			billcard_wfdesc.addBillCardButton(btn_delete);
			billcard_wfdesc.repaintBillCardButton();
		}
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billcard_wfdesc, BorderLayout.CENTER);
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
		}
	}

	private void onDelete() {
		try {
			if (MessageBox.showConfirmDialog(this, "您真的想删除该记录吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
			String str_sql = "delete from cmp_cmpfile_wfdesc  where id=" + billcard_wfdesc.getBillVO().getStringValue("id"); //
			UIUtil.executeUpdateByDS(null, str_sql); //提交数据库
			billcard_wfdesc.setEditState("INSERT");
			billcard_wfdesc.insertRow();
			billcard_wfdesc.setEditableByInsertInit();
			billcard_wfdesc.setRealValueAt("cmpfile_id", this.cmpfileid);
			billcard_wfdesc.setRealValueAt("cmpfile_name", this.cmpfilename);
			billcard_wfdesc.setRealValueAt("wfprocess_id", this.processid);
			billcard_wfdesc.setRealValueAt("wfprocess_code", this.processcode);
			billcard_wfdesc.setRealValueAt("wfprocess_name", this.processname);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onConfirm() {
		try {
			if (!billcard_wfdesc.checkValidate()) {
				return;
			}
			billcard_wfdesc.updateData();
			closeType = BillDialog.CONFIRM;
			this.dispose();
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onSave() {
		try {
			if (!billcard_wfdesc.checkValidate()) {
				return;
			}
			billcard_wfdesc.updateData();
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
