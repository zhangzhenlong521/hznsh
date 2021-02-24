package com.pushworld.ipushgrc.ui.score.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;
import cn.com.infostrategy.ui.mdata.cardcomp.ChildTableCommUIIntercept;

/**
 * 违规积分-》违规积分登记 卡片中扣分情况引用子表的拦截器类【李春娟/2013-05-30】
 * 主要逻辑是重写了子表的新增方法，已实现子表新增时设置应计分值为主表的标准分值，
 * 如果标准分值为分值区间，则取标准分值的最大值
 * @author lcj
 *
 */
public class ScoreUserTableUIIntercept implements ChildTableCommUIIntercept, ActionListener {
	private BillCardPanel mainCardPanel;
	private CardCPanel_ChildTable childtable;
	private BillListPanel billListPanel;
	private WLTButton btn_insert, btn_edit;

	public void afterInitialize(BillPanel _panel) throws Exception {
		billListPanel = (BillListPanel) _panel;
		childtable = billListPanel.getLoaderChildTable();
		mainCardPanel = (BillCardPanel) childtable.getBillPanel();
		btn_insert = childtable.getBtn_insert();
		btn_insert.addActionListener(this);
		btn_edit = childtable.getBtn_edit();
		btn_edit.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert();
		}else if (e.getSource() == btn_edit) {
			onEdit();
		}
	}

	private void onInsert() {
		try {
			BillCardPanel cardPanel = new BillCardPanel(this.billListPanel.templetVO); //创建一个卡片面板
			cardPanel.insertRow(); //卡片新增一行!
			String str_cardprimarykey = (mainCardPanel).getRealValueAt("id"); //
			cardPanel.setCompentObjectValue("registerid", new StringItemVO(str_cardprimarykey)); //
			String score = mainCardPanel.getBillVO().getStringValue("REFERSCORE");
			if (score != null && !score.trim().equals("")) {
				if (score.contains("-")) {
					score = score.substring(score.lastIndexOf("-") + 1); //积分是一个范围, 比如: 1-3
				}
				cardPanel.setCompentObjectValue("SCORE", new StringItemVO(score)); //设置应计分值的默认值【李春娟/2013-05-30】
				cardPanel.setCompentObjectValue("SCORE_STAND", new StringItemVO(score));
			}
			cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置
			BillCardDialog dialog = new BillCardDialog(mainCardPanel, "【新增】", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
			
			//积分是一个范围, 允许修改
			if (mainCardPanel.getBillVO().getStringValue("REFERSCORE").contains("-")) {
				cardPanel.setEditable("SCORE", true);
			}
			dialog.setVisible(true);

			if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
				if (billListPanel.getRowCount() > 0) {//保证数据在末尾添加【李春娟/2013-06-08】
					billListPanel.setSelectedRow(billListPanel.getRowCount() - 1);
				}
				int li_newrow = this.billListPanel.newRow(); //
				billListPanel.setBillVOAt(li_newrow, dialog.getBillVO());
				billListPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
				childtable.resetHeight(); //重置高度

				//子表新增时追加删除sql 【杨科/2013-03-26】
				String str_sql = "delete from " + billListPanel.templetVO.getSavedtablename() + " where " + billListPanel.templetVO.getPkname() + "='" + dialog.getBillVO().getStringValue(billListPanel.templetVO.getPkname()) + "'";
				childtable.getList_sqls(false).add(str_sql);
			}
		} catch (Exception ex) {
			MessageBox.showException(mainCardPanel, ex); //
		}
	}
	
	
	/***
	 * 修改时, 也要根据违规性质算分, 所以还是要把主表的积分标准取过来
	 * Gwang 2014-11-05
	 */
	private void onEdit() {
		try {
			BillVO billVO = billListPanel.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(mainCardPanel);
				return;
			}
			int n = billListPanel.getSelectedRow();
			BillCardPanel cardPanel = new BillCardPanel(billListPanel.getTempletVO());
			cardPanel.setBillVO(billVO);
			
			// 取标准积分
			String score = mainCardPanel.getBillVO().getStringValue("REFERSCORE");			
			if (score != null && !score.trim().equals("")) { // 如果积分是一个范围, 比如: 1-3. 取最大值
				if (score.contains("-")) { 
					score = score.substring(score.lastIndexOf("-") + 1);
				}
				cardPanel.setCompentObjectValue("SCORE_STAND", new StringItemVO(score));
			}

			cardPanel.setEditableByEditInit(); 
			BillCardDialog dialog = new BillCardDialog(mainCardPanel, "修改", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			
			//积分是一个范围, 允许修改
			if (mainCardPanel.getBillVO().getStringValue("REFERSCORE").contains("-")) {
				cardPanel.setEditable("SCORE", true);
			}
			
			dialog.setVisible(true);
			if (dialog.getCloseType() == 1) {
				//刷新列表中选中记录的数据显示
				billListPanel.setSelectedRow(n);
				billListPanel.setBillVOAt(n, dialog.getBillVO());
				billListPanel.setRowStatusAs(n, WLTConstants.BILLDATAEDITSTATE_INIT);
			}			
			
			
		} catch (Exception ex) {
			MessageBox.showException(mainCardPanel, ex);
		}
	}
}
