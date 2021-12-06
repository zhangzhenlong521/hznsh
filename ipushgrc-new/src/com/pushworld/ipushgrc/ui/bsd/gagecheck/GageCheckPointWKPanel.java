package com.pushworld.ipushgrc.ui.bsd.gagecheck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 标准检查要点库 左右分割面板，左面检查要点类型，右面检查要点。左右都各自维护。左面树删除，右面的检查要点也随之删除。
 * 
 * @author xch
 * 
 */
public class GageCheckPointWKPanel extends AbstractWorkPanel implements BillTreeSelectListener, ActionListener {

	private WLTButton btn_list_edit = null;
	private WLTButton btn_list_delete = null;
	private WLTButton btn_list_insert = null;
	private JMenuItem item_insert, item_delete, item_edit; //
	private WLTButton btn_tree_insert = null;
	private WLTButton btn_tree_edit = null;
	private WLTButton btn_tree_delete = null;
	private WLTButton btn_tree_refresh = null;

	public String getTreeTempeltCode() {
		return "BSD_CHECKTYPE_CODE1";
	}

	public String getTableTempletCode() {
		return "BSD_CHECKITEM_CODE1";
	}

	public String getOuterField() { // 得到外键关联字段
		return "checktype_id";
	}

	private BillTreePanel billTreePanel;
	private BillListPanel billListPanel;

	public void initialize() {
		billTreePanel = new BillTreePanel(getTreeTempeltCode());
		billTreePanel.queryDataByCondition(null);
		billTreePanel.reSetTreeChecked(false); // 不带勾选框
		billTreePanel.addBillTreeSelectListener(this);
		billTreePanel.setAllBillTreeBtnVisiable(true);
		item_insert = new JMenuItem("新增", UIUtil.getImage("insert.gif")); // 新增
		item_delete = new JMenuItem("删除", UIUtil.getImage("del.gif")); // 删除
		item_edit = new JMenuItem("编辑", UIUtil.getImage("modify.gif")); // 编辑
		item_insert.addActionListener(this); // 添加事件
		item_edit.addActionListener(this);
		item_delete.addActionListener(this);
		billTreePanel.addAppMenuItems(new JMenuItem[] { item_insert, item_edit, item_delete }); // 类型树添加右键操作按钮
		btn_tree_insert = new WLTButton(WLTConstants.BUTTON_TEXT_INSERT);
		btn_tree_edit = new WLTButton(WLTConstants.BUTTON_TEXT_EDIT);
		btn_tree_delete = new WLTButton(WLTConstants.BUTTON_TEXT_DELETE);
		btn_tree_refresh = new WLTButton(WLTConstants.BUTTON_TEXT_REFRESH);
		btn_tree_insert.addActionListener(this);
		btn_tree_edit.addActionListener(this);
		btn_tree_delete.addActionListener(this);
		btn_tree_refresh.addActionListener(this);
		billTreePanel.insertBatchBillTreeButton(new WLTButton[] { btn_tree_insert, btn_tree_edit, btn_tree_delete, btn_tree_refresh }); //
		billTreePanel.repaintBillTreeButton(); //
		btn_list_insert = new WLTButton("新增");
		btn_list_insert.addActionListener(this);
		btn_list_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); // 添加系统修改按钮
		btn_list_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); // 添加系统删除按钮
		billListPanel = new BillListPanel(getTableTempletCode());
		billListPanel.addBatchBillListButton(new WLTButton[] { btn_list_insert, btn_list_edit, btn_list_delete }); // 检查要点
		// 添加操作按钮
		billListPanel.repaintBillListButton();
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel, billListPanel); // 分割面板
		splitPane.setDividerLocation(250);
		this.add(splitPane);
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO selectVo = _event.getCurrSelectedVO();
		if (selectVo == null) {
			return;
		}
		String checkTypeID = selectVo.getPkValue();
		billListPanel.QueryDataByCondition(getOuterField() + " = '" + checkTypeID + "'");
	}

	/*
	 * 新增检查要点类型
	 */
	public void onItemInsert() {
		String parentIDField = billTreePanel.getTempletVO().getTreeparentpk();
		String seqFiled = billTreePanel.getTempletVO().getTreeseqfield(); // 得到树的排序字段
		String parentID = "0";
		int seq = 1;
		if (getBillTreePanel().getSelectedPath() == null) {
			MessageBox.show(this, "请选择一个父亲结点进行新增操作!"); //
			return; //如果没有选择一个结点则直接返回
		}
		if (billTreePanel.getSelectedNode().getChildCount() > 0) { // 判断有没有子节点
			String max_str_seq = billTreePanel.getBillVOFromNode((DefaultMutableTreeNode) billTreePanel.getSelectedNode().getLastChild()).getStringValue(seqFiled);
			if (max_str_seq == null) {
				billTreePanel.resetChildSeq();
				max_str_seq = billTreePanel.getBillVOFromNode((DefaultMutableTreeNode) billTreePanel.getSelectedNode().getLastChild()).getStringValue(seqFiled);
			}
			seq = Integer.parseInt(max_str_seq) + 1;
		}
		BillVO selectVO = billTreePanel.getSelectedVO(); //
		if (selectVO != null) {
			parentID = selectVO.getPkValue();
		}
		BillCardPanel cardPanel = new BillCardPanel(getTreeTempeltCode());
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setValueAt(seqFiled, new StringItemVO(seq + ""));
		cardPanel.setValueAt(parentIDField, new StringItemVO(parentID));
		BillCardDialog cardDialog = new BillCardDialog(this, "新增", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			billTreePanel.addNode(cardPanel.getBillVO());
			billTreePanel.updateUI();
		}
	}

	/*
	 * 修改检查要点类型
	 */
	public void onItemEdit() {
		BillVO selectVO = billTreePanel.getSelectedVO(); //
		if (selectVO == null) {
			MessageBox.show(this, "请选择一个子结点进行此操作!"); //
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(getTreeTempeltCode());
		cardPanel.setBillVO(selectVO);
		cardPanel.setEditableByEditInit();
		BillCardDialog cardDialog = new BillCardDialog(this, "修改", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			billTreePanel.setBillVOForCurrSelNode(cardPanel.getBillVO());
			billTreePanel.updateUI();
		}
	}

	/*
	 * 删除类型树节点。
	 */
	public void onItemDelete() {
		BillVO selectVO = billTreePanel.getSelectedVO(); //
		if (selectVO == null) {
			MessageBox.show(this, "请选择一个子结点进行此操作!"); //
			return;
		}
		if (MessageBox.confirm(this, "此操作会删除子孙记录和子表数据,确认删除吗?")) {
			BillVO billvo[] = billTreePanel.getSelectedChildPathBillVOs();
			ArrayList al_sql = new ArrayList();
			for (int i = 0; i < billvo.length; i++) {
				String id = billvo[i].getPkValue();
				al_sql.add("delete from  " + selectVO.getSaveTableName() + " where " + selectVO.getPkName() + "=" + id + "");
				al_sql.add("delete from " + billListPanel.getTempletVO().getTablename() + " where " + getOuterField() + " = '" + id + "'");
			}
			try {
				UIUtil.executeBatchByDS(null, al_sql);
				billTreePanel.delCurrNode(); // 移除当前选择节点
				billListPanel.removeAllRows(); // 移除又列表所有数据
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/*
	 * 插入右检查要点数据.
	 */
	public void onListInsert() {
		BillVO selectVO = billTreePanel.getSelectedVO(); //
		if (selectVO == null) {
			MessageBox.show(this, "请选择一个" + billTreePanel.getTempletVO().getTempletname() + "数据执行此操作！");
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(getTableTempletCode());
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setValueAt(getOuterField(), new StringItemVO(selectVO.getPkValue()));
		BillCardDialog cardDialog = new BillCardDialog(this, "新增", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			billListPanel.insertRow(billListPanel.getRowCount(), cardPanel.getBillVO());
		}

	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == item_insert || e.getSource() == btn_tree_insert) { // 插入类型
			onItemInsert();
		} else if (obj == item_edit || e.getSource() == btn_tree_edit) { // 修改类型
			onItemEdit();
		} else if (obj == item_delete || e.getSource() == btn_tree_delete) { // 删除类型
			onItemDelete();
		} else if (obj == btn_list_insert) { // 插入检查要点
			onListInsert();
		} else if (e.getSource() == btn_tree_refresh) {
			onRefresh();
		}

	}

	public void onRefresh() {
		long ll_1 = System.currentTimeMillis();
		getBillTreePanel().queryDataByCondition("1=1"); //刷新数据,这里不能直接用refreshTree(),因为上次查询的sql中很可能不包括新增数据的id，故需要全查。【李春娟/2013-07-30】
		long ll_2 = System.currentTimeMillis();
		getBillTreePanel().clearSelection();
		billListPanel.clearTable();
		MessageBox.show(this, "刷新数据成功!共耗时[" + (ll_2 - ll_1) + "]毫秒"); //
	}

	private BillTreePanel getBillTreePanel() {
		return billTreePanel;
	}
}
