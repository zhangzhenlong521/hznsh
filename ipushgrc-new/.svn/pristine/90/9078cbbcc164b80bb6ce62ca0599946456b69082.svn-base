package com.pushworld.ipushgrc.ui.law.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;
/**
 * 外规关联相关修订的外规【李春娟/2015-04-20】
 * @author lichunjuan
 *
 */
public class ImportLawDialog extends BillListDialog implements BillTreeSelectListener, ActionListener, BillListHtmlHrefListener, BillListMouseDoubleClickedListener {
	private String lawid;
	private String lawname;
	private WLTButton btn_linkAll, btn_linkItem, btn_importlawitem, btn_showall, btn_confirm, btn_cancel;
	private BillTreePanel treePanel = null; //树型面板
	private BillCardPanel cardPanel = null;
	private BillListPanel billlist_law;
	private BillDialog dialog_lawitem;
	private HashMap tempMap = new HashMap();
	private HashMap addtempMap = new HashMap();
	private WLTButton btn_show = null;
	boolean canClose;
	BillVO[] returnVO = null;
	private BillListPanel list;//主卡片显示的导入子表列表

	public ImportLawDialog(Container _parent, String _title, BillListPanel list) {
		super(_parent, _title, "LAW_LAW_CODE2", 900, 650);
		this.list = list;
		billlist_law = this.getBilllistPanel();
		billlist_law.addBillListHtmlHrefListener(this);
		billlist_law.setItemVisible("showdetail", false);
		btn_linkAll = this.getBtn_confirm();
		btn_linkAll.setText("关联全文");
		btn_linkAll.setToolTipText("关联全文");
		btn_linkAll.setPreferredSize(new Dimension(100, 23));
		btn_linkAll.setIcon(UIUtil.getImage("book_link.png"));
		btn_linkAll.addActionListener(this);

		//设置为关联条目 Gwang 2012-10-24
		btn_linkItem = this.getBtn_cancel();
		btn_linkItem.setText("关联条目");
		btn_linkItem.setToolTipText("关联条目");
		btn_linkItem.setPreferredSize(new Dimension(100, 23));
		btn_linkItem.setIcon(UIUtil.getImage("page_link.png"));
		btn_linkItem.addActionListener(this);

		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //卡片浏览按钮
		billlist_law.addBatchBillListButton(new WLTButton[] { btn_show });
		billlist_law.repaintBillListButton();
		//设置为Checkbox Gwang 2012-10-24
		billlist_law.setRowNumberChecked(true);
		int initRow = list.getRowCount();
		for (int i = 0; i < initRow; i++) {
			BillVO vo = list.getBillVO(i);
			if (vo.getStringValue("itemid") == null || "".equals(vo.getStringValue("itemid"))) {
				tempMap.put(vo.getStringValue("id"), vo);
			} else {
				tempMap.put(vo.getStringValue("id") + "$" + vo.getStringValue("itemid"), vo);
			}
		}
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_linkAll) {
			onLinkAll();
		} else if (e.getSource() == btn_importlawitem) { //
			onImportLawItem();
		} else if (e.getSource() == btn_showall) {
			onShow();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == btn_show) {
			onBillListSelect();
		} else if (e.getSource() == btn_linkItem) {
			onLinkItem();
		}
	}

	private void onBillListSelect() {
		BillVO billVO = billlist_law.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(billlist_law.templetVO); //
		cardPanel.setBillVO(billVO); //
		String str_recordName = billVO.toString(); //
		BillCardDialog dialog = new BillCardDialog(billlist_law, billlist_law.templetVO.getTempletname() + "[" + str_recordName + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT, true);
		dialog.getBtn_confirm().setVisible(false); //
		dialog.getBtn_save().setVisible(false); //
		cardPanel.setEditable(false); //
		dialog.setVisible(true); //
	}

	private BillTreePanel getTreePanel() {
		treePanel = new BillTreePanel("LAW_LAW_ITEM_CODE1");
		treePanel.setMoveUpDownBtnVisiable(false);
		BillTreeNodeVO treenode = new BillTreeNodeVO(-1, this.lawname);
		treePanel.getRootNode().setUserObject(treenode);
		treePanel.queryDataByCondition("lawid=" + this.lawid);
		treePanel.addBillTreeSelectListener(this);
		return treePanel;
	}

	private BillCardPanel getCardPanel() {
		cardPanel = new BillCardPanel("LAW_LAW_ITEM_CODE1");
		btn_importlawitem = new WLTButton("加入", "office_199.gif");
		btn_showall = new WLTButton("查看(" + tempMap.size() + ")", "office_062.gif");

		btn_importlawitem.addActionListener(this);
		btn_showall.addActionListener(this);

		cardPanel.addBatchBillCardButton(new WLTButton[] { btn_importlawitem, btn_showall });
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

	private void onImportLawItem() {
		try {
			String lawitem_id = null;
			String lawitem_title = null;
			String lawitem_content;
			if (treePanel.getSelectedNode() == treePanel.getRootNode()) {
				if (tempMap.containsKey(this.lawid) || addtempMap.containsKey(this.lawid)) {
					MessageBox.show(this, "该法规已加入，请勿重复加入!");
					return;
				}
				lawitem_title = "【全文】";
				lawitem_content = "【全文】";
			} else {
				BillVO billvo = treePanel.getSelectedVO();
				if (billvo == null) {
					MessageBox.show(this, "请选择一个节点进行此操作!");
					return;
				}
				lawitem_id = billvo.getStringValue("id");
				lawitem_title = billvo.getStringValue("itemtitle");
				lawitem_content = billvo.getStringValue("itemcontent");
				if (tempMap.containsKey(this.lawid + "$" + lawitem_id) || addtempMap.containsKey(this.lawid + "$" + lawitem_id)) {
					MessageBox.show(this, "该法规条文已加入，请勿重复加入!");
					return;
				}
			}
			BillVO billvo;
			if (this.list.getRowCount() > 0) {
				billvo = this.list.getBillVO(0);
			} else {
				int a = this.list.addEmptyRow();
				billvo = this.list.getBillVO(a);
				this.list.removeRow();
			}
			billvo.setObject("id", new StringItemVO(this.lawid));
			billvo.setObject("lawname", new StringItemVO(this.lawname));
			billvo.setObject("itemid", new StringItemVO(lawitem_id));
			billvo.setObject("itemtitle", new StringItemVO(lawitem_title));
			billvo.setObject("itemcontent", new StringItemVO(lawitem_content));

			addtempMap.put(this.lawid + (lawitem_id == null ? "" : "$" + lawitem_id), billvo);

			btn_showall.setText("查看(" + (tempMap.size() + addtempMap.size()) + ")");
			btn_showall.setToolTipText("查看(" + (tempMap.size() + addtempMap.size()) + ")");
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
		BillListDialog billdialog = new BillListDialog(dialog_lawitem, "相关法规", "LAW_LAW_LCJ_Q01", 850, 550);
		BillListPanel billlist_showlaw = billdialog.getBilllistPanel();

		billlist_showlaw.addBillListHtmlHrefListener(this);
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD2);
		btn_show.setText("浏览");
		btn_show.setToolTipText("浏览");
		btn_show.setPreferredSize(new Dimension(45, 23));
		billlist_showlaw.addBillListButton(btn_show);
		billlist_showlaw.repaintBillListButton();

		billlist_showlaw.setQuickQueryPanelVisiable(false);
		Iterator it1 = tempMap.keySet().iterator();
		Iterator it2 = addtempMap.keySet().iterator();
		while (it1.hasNext()) {
			BillVO billvo = (BillVO) tempMap.get(it1.next());
			billlist_showlaw.addRow(billvo);
		}
		while (it2.hasNext()) {
			BillVO billvo = (BillVO) addtempMap.get(it2.next());
			billlist_showlaw.addRow(billvo);
		}
		billdialog.getBtn_confirm().setVisible(false);
		billdialog.getBtn_cancel().setText("关闭");
		billdialog.getBtn_cancel().setToolTipText("关闭");
		billdialog.setVisible(true);
	}

	public void onConfirm() {
		if (!canClose) {
			MessageBox.show(this, "请选择一条法规条文，点击加入");
			return;
		}
		tempMap.putAll(addtempMap);
		addtempMap.clear();
		returnVO = new BillVO[tempMap.size()];
		Iterator it1 = tempMap.keySet().iterator();
		int i = 0;
		while (it1.hasNext()) {
			returnVO[i] = (BillVO) tempMap.get(it1.next());
			i++;
		}

		dialog_lawitem.setCloseType(1);
		dialog_lawitem.dispose();
		this.setCloseType(1);
		this.dispose();
	}

	public void onCancel() {
		addtempMap.clear();
		dialog_lawitem.setCloseType(2);
		dialog_lawitem.dispose();
		this.setCloseType(2);
		this.dispose();
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillVO vo = _event.getBillListPanel().getSelectedBillVO();
		if (vo.getStringValue("itemid") == null || "".equals(vo.getStringValue("itemid"))) {
			new LawShowHtmlDialog(this, vo.getStringValue("id"));
		} else {
			new LawShowHtmlDialog(this, vo.getStringValue("id"), null, new String[] { vo.getStringValue("itemid") }); // 高亮出来关联的条目标题。
		}
	}

	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
		onBillListSelect();
	}

	/***
	 * 关联法规全文
	 * Gwang 2012-10-24
	 */
	private void onLinkAll() {
		if (billlist_law.getCheckedRows().length < 1) {
			MessageBox.show(this, "请至少选择一条记录,执行此操作");
			return; //
		}
		//如果通过CheckBox选中, 就直接关联已选中的外规. 不要选择条目了!(项目上提出这个可以提高效率, 因为大多数情况下都是不关联到条目的!)

		BillVO[] checkedVOs = billlist_law.getCheckedBillVOs();
		String lawitem_title = "【全文】";
		String lawitem_content = "【全文】";
		String key = "";
		for (BillVO billVO : checkedVOs) {
			key = billVO.getStringValue("id");
			if (tempMap.containsKey(key)) {
				continue;
			}

			BillVO billvo = null;
			if (this.list.getRowCount() > 0) {
				billvo = this.list.getBillVO(0);
			} else {
				int a = this.list.addEmptyRow();
				billvo = this.list.getBillVO(a);
				this.list.removeRow();
			}
			billvo.setObject("id", new StringItemVO(billVO.getStringValue("id")));
			billvo.setObject("lawname", new StringItemVO(billVO.getStringValue("lawname")));
			billvo.setObject("itemtitle", new StringItemVO(lawitem_title));
			billvo.setObject("itemcontent", new StringItemVO(lawitem_content));
			tempMap.put(key, billvo);
		}
		returnVO = new BillVO[tempMap.size()];
		Iterator it1 = tempMap.keySet().iterator();
		int i = 0;
		while (it1.hasNext()) {
			returnVO[i] = (BillVO) tempMap.get(it1.next());
			i++;
		}

		this.setCloseType(1);
		this.dispose();
	}

	/***
	 * 关联法规条目  Gwang 2012-10-24
	 */
	private void onLinkItem() {
		if (billlist_law.getCheckedRows().length != 1) {
			MessageBox.showSelectOne(this);
			return; //
		}
		BillVO billvo_law = billlist_law.getCheckedBillVOs()[0];
		this.setVisible(false);
		this.lawid = billvo_law.getStringValue("id");
		this.lawname = billvo_law.getStringValue("lawname");

		dialog_lawitem = new BillDialog(this, "请选择一条法规条文，点击加入", 800, 600);//法规条文
		WLTSplitPane splitPanel = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, getTreePanel(), getCardPanel());
		splitPanel.setDividerLocation(230);

		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		panel.setLayout(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		canClose = false;
		dialog_lawitem.getContentPane().add(splitPanel, BorderLayout.CENTER);
		dialog_lawitem.getContentPane().add(panel, BorderLayout.SOUTH);
		dialog_lawitem.setVisible(true);
		if (dialog_lawitem.getCloseType() == -1) {
			this.onCancel();
		}
	}

	public BillVO[] getReturnRealValue() {
		return returnVO;
	}
}
