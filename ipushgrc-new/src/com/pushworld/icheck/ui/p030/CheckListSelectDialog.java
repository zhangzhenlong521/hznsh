package com.pushworld.icheck.ui.p030;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 检查底稿中导入问题词条或检查提纲【李春娟/2016-08-17】
 * 
 * @author lcj
 * 
 */
public class CheckListSelectDialog extends BillDialog implements BillTreeSelectListener, ActionListener {

	//目录树
	private BillTreePanel treePanel = null;
	private BillListPanel listPanel = null; // 问题词条或检查提纲
	private WLTButton btn_confirm, btn_cancel;
	private BillVO[] returnVOs;

	/**
	 * 
	 * @param _type  类型：问题词条；检查提纲
	 */
	public CheckListSelectDialog(BillListPanel _parentPanel, String _type) {
		super(_parentPanel, "导入" + _type, 900, 800);
		treePanel = new BillTreePanel("CK_PROJECT_LIST_SCY_E01");
		treePanel.getBillTreeBtnPanel().setVisible(false);
		treePanel.setMoveUpDownBtnVisiable(false);
		treePanel.addBillTreeSelectListener(this);
		treePanel.queryDataByCondition(" 1=1");
		if ("问题词条".equals(_type)) {//问题词条
			listPanel = new BillListPanel("CK_PROBLEM_DICT_SCY_E01");
		} else if ("检查提纲".equals(_type)) {// 检查提纲
			listPanel = new BillListPanel("CK_OUTLINE_SCY_E01");
		}
		listPanel.setRowNumberChecked(true);//设置为勾选【李春娟/2016-08-26】
		listPanel.getBillListBtnPanel().setVisible(false);
		this.setLayout(new BorderLayout());
		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, treePanel, listPanel);
		split.setDividerLocation(280);
		split.setDividerSize(2);
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);
		JPanel panel = WLTPanel.createDefaultPanel();
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		this.add(split, BorderLayout.CENTER);
		this.add(panel, BorderLayout.SOUTH);
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billVO = _event.getCurrSelectedVO();
		if (null == billVO) {
			return;
		}
		String id = billVO.getStringValue("id");
		String level = billVO.getStringValue("leveldesc");
		if ("一级目录".equals(level)) {
			listPanel.QueryDataByCondition("firstid = '" + id + "' ");
		} else if ("二级目录".equals(level)) {
			listPanel.QueryDataByCondition("secondid = '" + id + "' ");
		} else if ("三级目录".equals(level)) {
			listPanel.QueryDataByCondition("parentid = '" + id + "' ");
		}
	}

	public void actionPerformed(ActionEvent a) {
		if (a.getSource() == btn_confirm) {
			onConfirm();
		} else if (a.getSource() == btn_cancel) {
			onCancel();
		}
	}

	private void onConfirm() {
		returnVOs = listPanel.getCheckedBillVOs();
		if (returnVOs == null || returnVOs.length == 0) {
			MessageBox.show(this, "请勾选记录后再点击确定!");
			return;
		}
		this.setCloseType(1);
		this.dispose();
	}

	private void onCancel() {
		returnVOs = null;
		this.setCloseType(2);
		this.dispose();
	}

	public BillVO[] getReturnVOs() {
		return returnVOs;
	}
}
