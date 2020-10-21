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
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ImportRefwfDialog extends BillDialog implements ActionListener {
	private String cmpfileid;
	private String cmpfilename;
	private String processid;
	private String processcode;
	private String processname;
	private boolean editable;
	private WLTButton btn_import, btn_deleterefwf, btn_importrefwf, btn_showall, btn_confirm, btn_close;
	private BillListPanel billlist_refwf, billlist_importrefwf;
	private BillListDialog dialog_refwf;
	private HashMap tempMap = new HashMap();
	private HashMap addtempMap = new HashMap();
	private ArrayList insertsqls = new ArrayList();
	private boolean canClose = false;
	public ImportRefwfDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, boolean _editable) {
		super(_parent, _title, 900, 650);
		this.setLayout(new BorderLayout()); //
		this.cmpfileid = _cmpfileid;
		this.cmpfilename = _cmpfilename;
		this.processid = _processid;
		this.processcode = _processcode;
		this.processname = _processname;
		this.editable = _editable;
		billlist_refwf = new BillListPanel("CMP_CMPFILE_REFWF_CODE1");
		billlist_refwf.setDataFilterCustCondition("wfprocess_id=" + this.processid);
		billlist_refwf.QueryDataByCondition(null);
		if (editable) {
			if (billlist_refwf.getRowCount() > 0) {
				BillVO[] billvos = billlist_refwf.getAllBillVOs();
				for (int i = 0; i < billvos.length; i++) {
					tempMap.put(billvos[i].getStringValue("ref_wfprocess_id"), billvos[i]);
				}
			}
			btn_import = new WLTButton("关联流程");
			btn_import.addActionListener(this);
			btn_deleterefwf = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
			btn_deleterefwf.addActionListener(this);
			billlist_refwf.addBatchBillListButton(new WLTButton[] { btn_import, btn_deleterefwf });
		}
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		billlist_refwf.addBillListButton(btn_show);
		billlist_refwf.repaintBillListButton();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billlist_refwf, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * 判断是否显示本窗口，如果没有相关信息，则提示是否新增，如果选择【是】，则弹出新增界面，如果选择【否】则不显示本窗口。【李春娟/2012-05-28】
	 * @return
	 */
	public boolean isShowDialog() {
		if (this.editable && billlist_refwf.getRowCount() == 0) {
			if (MessageBox.confirm(this, "该流程没有相关流程,是否新增?")) {//如果可编辑并且列表中没有记录，则提示是否新增【韩静/2012-03-13】
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
		} else if (e.getSource() == btn_deleterefwf) {
			onDeleterefwf();
		} else if (e.getSource() == btn_importrefwf) { //
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
		dialog_refwf = new BillListDialog(this, "请选择一个流程，点击加入", "PUB_WF_PROCESS_CODE2", 700, 640);
		billlist_importrefwf = dialog_refwf.getBilllistPanel();
		billlist_importrefwf.setDataFilterCustCondition("cmpfileid is not null and id !=" + this.processid);//必须是其他体系流程【李春娟/2012-09-10】
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //卡片浏览按钮
		btn_importrefwf = new WLTButton("加入", "office_199.gif");
		btn_showall = new WLTButton("查看(" + tempMap.size() + ")", "office_062.gif");
		btn_importrefwf.addActionListener(this);
		btn_showall.addActionListener(this);
		billlist_importrefwf.addBatchBillListButton(new WLTButton[] { btn_importrefwf, btn_showall, btn_show });
		billlist_importrefwf.repaintBillListButton();

		btn_confirm = dialog_refwf.getBtn_confirm();
		btn_confirm.addActionListener(this);
		canClose = false;
		dialog_refwf.setVisible(true);
		if (dialog_refwf.getCloseType() == -1) {
			addtempMap.clear();
			insertsqls.clear();
		}
	}

	private void onDeleterefwf() {
		BillVO billvo = billlist_refwf.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.showConfirmDialog(this, "您确定要删除吗?") != JOptionPane.YES_OPTION) {
			return; //
		}
		try {
			UIUtil.executeUpdateByDS(null, "delete from cmp_cmpfile_refwf where id=" + billvo.getStringValue("id"));
			billlist_refwf.removeRow(billlist_refwf.getSelectedRow()); //
			MessageBox.show(this, "删除成功!");
			tempMap.size();
			tempMap.remove(billvo.getStringValue("ref_wfprocess_id"));
			tempMap.size();
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onImportRule() {
		try {
			BillVO billvo = billlist_importrefwf.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.showSelectOne(this);
				return;
			}
			String refwfid = billvo.getStringValue("id");
			if (tempMap.containsKey(refwfid) || addtempMap.containsKey(refwfid)) {
				MessageBox.show(this, "该流程已加入，请勿重复加入!");
				return;
			}
			BillVO billvo1 = null;
			if (billlist_refwf.getRowCount() > 0) {
				billvo1 = billlist_refwf.getBillVO(0);
			} else {
				int a = billlist_refwf.addEmptyRow();
				billvo1 = billlist_refwf.getBillVO(a);
				billlist_refwf.removeRow();
			}
			billvo1.setObject("cmpfile_name", new StringItemVO(this.cmpfilename));
			billvo1.setObject("wfprocess_name", new StringItemVO(this.processname));
			billvo1.setObject("ref_cmpfile_name", new StringItemVO(billvo.getStringValue("cmpfilename")));
			billvo1.setObject("ref_wfprocess_name", new StringItemVO(billvo.getStringValue("name")));
			addtempMap.put(refwfid, billvo1);

			InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_refwf"); // 
			String str_id = UIUtil.getSequenceNextValByDS(null, "S_CMP_CMPFILE_REFWF");
			isql_insert.putFieldValue("id", str_id);
			isql_insert.putFieldValue("cmpfile_id", this.cmpfileid);
			isql_insert.putFieldValue("cmpfile_name", this.cmpfilename);
			isql_insert.putFieldValue("wfprocess_id", this.processid);
			isql_insert.putFieldValue("wfprocess_code", this.processcode);
			isql_insert.putFieldValue("wfprocess_name", this.processname);
			isql_insert.putFieldValue("ref_cmpfile_id", billvo.getStringValue("cmpfileid"));
			isql_insert.putFieldValue("ref_cmpfile_name", billvo.getStringValue("cmpfilename"));
			isql_insert.putFieldValue("ref_wfprocess_id", billvo.getStringValue("id"));
			isql_insert.putFieldValue("ref_wfprocess_code", billvo.getStringValue("code"));
			isql_insert.putFieldValue("ref_wfprocess_name", billvo.getStringValue("name"));

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
		BillListDialog billdialog = new BillListDialog(dialog_refwf, "所有相关流程", "CMP_CMPFILE_REFWF_CODE1", 850, 550);
		BillListPanel billlist_showrefwf = billdialog.getBilllistPanel();
		if (cmpfileid == null) {
			billlist_showrefwf.getTempletItemVO("cmpfile_name").setCardisshowable(false);//如果不是流程文件里的关联，则不显示流程文件名称【李春娟/2012-05-11】
			billlist_showrefwf.setItemVisible("cmpfile_name", false);//因为上面的billlist_showrefwf 已经初始化完了，故用setListisshowable(false) 是不起作用的，只能这样设置。
		}
		billlist_showrefwf.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		billlist_showrefwf.repaintBillListButton();
		billlist_showrefwf.setQuickQueryPanelVisiable(false);
		Iterator it1 = tempMap.keySet().iterator();
		Iterator it2 = addtempMap.keySet().iterator();
		while (it1.hasNext()) {
			BillVO billvo = (BillVO) tempMap.get(it1.next());
			billlist_showrefwf.addRow(billvo);
		}
		while (it2.hasNext()) {
			BillVO billvo = (BillVO) addtempMap.get(it2.next());
			billlist_showrefwf.addRow(billvo);
		}
		billdialog.getBtn_confirm().setVisible(false);
		billdialog.getBtn_cancel().setText("关闭");
		billdialog.setVisible(true);
	}

	private void onConfirm() {
		if (!canClose) {
			MessageBox.show(this, "请选择一个流程，点击加入");
			return;
		}
		if (insertsqls.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, insertsqls);
				billlist_refwf.QueryDataByCondition(null);
				tempMap.putAll(addtempMap);
				addtempMap.clear();
				insertsqls.clear();
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
		dialog_refwf.setCloseType(1);
		dialog_refwf.dispose();
	}
}
