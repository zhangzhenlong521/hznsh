package com.pushworld.ipushgrc.ui.icheck.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;
import cn.com.infostrategy.ui.mdata.cardcomp.ChildTableCommUIIntercept;

import com.pushworld.ipushgrc.ui.icheck.p040.ICheckUIUtil;

/**
 *单机版检查管理检查实施，发现问题，问题引用违规子表的拦截器类【李春娟/2016-09-05】
 *修改主键生成机制
 * @author lcj
 *
 */
public class CheckEventTableUIIntercept implements ChildTableCommUIIntercept, ActionListener {
	private BillCardPanel mainCardPanel;
	private CardCPanel_ChildTable childtable;
	private BillListPanel billListPanel;
	private WLTButton btn_insert;

	public void afterInitialize(BillPanel _panel) throws Exception {
		billListPanel = (BillListPanel) _panel;
		childtable = billListPanel.getLoaderChildTable();
		mainCardPanel = (BillCardPanel) childtable.getBillPanel();
		btn_insert = childtable.getBtn_insert();
		btn_insert.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert();
		}
	}

	private void onInsert() {
		try {
			BillCardPanel cardPanel = new BillCardPanel(this.billListPanel.templetVO); //创建一个卡片面板
			cardPanel.insertRow(); //卡片新增一行!
			String problemid = (mainCardPanel).getRealValueAt("id"); //
			String schemeid = (mainCardPanel).getRealValueAt("schemeid"); //
			String deptid = (mainCardPanel).getRealValueAt("deptid"); //
			String implid = (mainCardPanel).getRealValueAt("implid"); //
			cardPanel.setCompentObjectValue("problemid", new StringItemVO(problemid)); //
			cardPanel.setCompentObjectValue("schemeid", new StringItemVO(schemeid)); //
			cardPanel.setCompentObjectValue("deptid", new StringItemVO(deptid)); //
			cardPanel.setCompentObjectValue("implid", new StringItemVO(implid)); //
			cardPanel.setCompentObjectValue("id", new StringItemVO(ICheckUIUtil.getSequenceNextVal())); //自定义主键生成机制【李春娟/2016-09-05】

			cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置
			BillCardDialog dialog = new BillCardDialog(mainCardPanel, "【新增】", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);

			dialog.setVisible(true);

			if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
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

}
