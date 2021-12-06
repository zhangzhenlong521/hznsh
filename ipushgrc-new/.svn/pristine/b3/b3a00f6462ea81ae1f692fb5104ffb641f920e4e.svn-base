package com.pushworld.ipushlbs.ui.lawyermanage.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

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
 * 律师信息维护
 * 
 * @author yinliang
 * @since 2012.01.17
 */
public class LawyerManageWKPanel extends AbstractWorkPanel implements BillTreeSelectListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private WLTSplitPane split; // 总页面分组
	private BillTreePanel billTree_dept = null; // 机构树!!
	private BillCardPanel billCard_dept = null; // 机构卡片
	private BillListPanel billList_lawyer = null; // 律师信息列表
	private WLTButton btn_insert, btn_update, btn_delete, btn_save, lawyer_insert; // 增,删,改
	private BillVO billvo;
	int flag = 0; // 用来标示点的是增加还是修改按钮 1为增加，2为修改

	@Override
	public void initialize() {
		billTree_dept = new BillTreePanel("LBS_LAWYER_DEPT_CODE1"); // 左侧机构树
		billTree_dept.setDragable(true);

		// 增加各个按钮
		btn_insert = new WLTButton("新增");
		btn_update = new WLTButton("修改");
		btn_delete = new WLTButton("删除");
		btn_insert.addActionListener(this);
		btn_update.addActionListener(this);
		btn_delete.addActionListener(this);
		billTree_dept.addBatchBillTreeButton(new WLTButton[] { btn_insert, btn_update, btn_delete });
		billTree_dept.repaintBillTreeButton();

		billCard_dept = new BillCardPanel("LBS_LAWYER_DEPT_CODE1"); // 右侧上部分机构信息
		btn_save = new WLTButton("保存");
		btn_save.addActionListener(this);
		billCard_dept.addBillCardButton(btn_save);
		billCard_dept.repaintBillCardButton();
		btn_save.setEnabled(false);

		billList_lawyer = new BillListPanel("LBS_LAWYER_INFO_CODE1");// 右侧下部分律师信息
		lawyer_insert = billList_lawyer.getBillListBtn("$列表弹出新增");
		lawyer_insert.addActionListener(this);

		billTree_dept.queryDataByCondition(null); // 查询所有数据,要有权限过滤!!
		billTree_dept.addBillTreeSelectListener(this); // 刷新事件监听!!

		WLTSplitPane split_1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billCard_dept, billList_lawyer); // 右侧上下分层
		split_1.setDividerLocation(260);

		split = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, split_1); // 全屏左右分层
		this.add(split); //
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billvo = billTree_dept.getSelectedVO();
		if (billvo == null) { // 点的是根节点，是没有信息的
			billCard_dept.clear();
			billCard_dept.setEditable(false);
			billList_lawyer.clearTable();
			return;
		}
		//保存按钮不可用
		btn_save.setEnabled(false);
		// 刷新机构卡片
		billCard_dept.setBillVO(billvo);
		billCard_dept.setEditable(false);
		// 刷新律师列表
		billList_lawyer.clearTable();
		billList_lawyer.QueryDataByCondition("PROXY_DEPT='" + billvo.getStringValue("id") + "'");// 更新律师表
		billList_lawyer.refreshData();

	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_insert) // 机构树插入
			onInsert();
		else if (obj == btn_update) // 更新机构信息
			onUpdate();
		else if (obj == btn_save) // 保存机构信息
			onSave();
		else if (obj == btn_delete) // 删除机构信息
			onDelete();
		else if (obj == lawyer_insert) // 增加律师信息
			onInsertLawyer();
	}

	// 增加律师信息
	private void onInsertLawyer() {
		BillVO billvo = billTree_dept.getSelectedVO();
		if (billvo == null) {
			MessageBox.show(split, "请选择一个机构！");
			return;
		}
		// 准备创建律师的新增卡片
		BillCardPanel billcard = new BillCardPanel("LBS_LAWYER_INFO_CODE1");
		billcard.insertRow();
		billcard.setValueAt("PROXY_DEPT", new StringItemVO(billvo.getStringValue("id"))); // 将机构的ID传进去
		BillCardDialog dialog = new BillCardDialog(split, "律师新增", billcard, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.setVisible(true);
		if (dialog.getCloseType() == 1) { // 如是是点击确定返回,将数据传入列表
			int li_newrow = billList_lawyer.newRow(false); //
			billList_lawyer.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			billList_lawyer.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); // 设置列表该行的数据为初始化状态.
			billList_lawyer.setSelectedRow(li_newrow);
		}
	}

	// 删除机构信息
	private void onDelete() {
		BillVO billvo = billTree_dept.getSelectedVO();
		if (billvo == null) {
			MessageBox.show(split, "请选择一个机构！");
			return;
		}
		if (MessageBox.showConfirmDialog(split, "删除机构同时将删除律师信息，确认删除吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}
		// 删除sql
		String str_sql = "delete from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id"); //
		String str_sql2 = "delete from LBS_LAWYER_INFO where proxy_dept = '" + billvo.getStringValue("id") + "'";
		List<String> list = new ArrayList<String>();
		list.add(str_sql);
		list.add(str_sql2);
		try {
			UIUtil.executeBatchByDS(null, list);
			billCard_dept.clear(); // 清空机构卡片信息
			billList_lawyer.clearTable(); // 清空律师表信息
			billCard_dept.setEditable(false); // 设置为不可修改
		} catch (Exception e) {
			e.printStackTrace();
		} //
		billTree_dept.refreshTree();
		billTree_dept.updateUI();
	}

	// 机构信息保存
	private void onSave() {
		String editState = billCard_dept.getEditState();
		try {
			if ("".equals(billCard_dept.getValueAt("NAME").toString()) || billCard_dept.getValueAt("NAME") == null || "".equals(billCard_dept.getValueAt("REG_ADDRESS").toString()) || billCard_dept.getValueAt("REG_ADDRESS") == null || "".equals(billCard_dept.getValueAt("CON_ADDRESS").toString())
					|| billCard_dept.getValueAt("CON_ADDRESS") == null) {
				MessageBox.show(split, "必输项存在空值！");
				return;
			}
			if (editState.equals("INIT")) { // 如果是初始
				MessageBox.show(split, "并未进行修改，不可保存！");
				return;
			} else if (editState.equals("INSERT")) { // 如果是插入\修改数据
				billCard_dept.updateData(); // 更新数据,如果是新建则insert，如果是修改则update
				// 自动的。。
				billCard_dept.setEditable(false);
				billTree_dept.addNode(billCard_dept.getBillVO());
			} else if (editState.equals("UPDATE")) {
				billCard_dept.updateData(); // 更新数据,如果是新建则insert，如果是修改则update
				// 自动的。。
				billCard_dept.setEditable(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 机构更新
	private void onUpdate() {
		BillVO billvo = billTree_dept.getSelectedVO();
		if (billvo == null) {
			MessageBox.show(split, "请选择一个机构！");
			return;
		}
		btn_save.setEnabled(true);
		billCard_dept.setEditState("UPDATE");
		billCard_dept.setEditable(true);
	}

	// 机构新建
	private void onInsert() {
		btn_save.setEnabled(true);
		BillVO billvo = billTree_dept.getSelectedVO();
		billCard_dept.clear();
		billCard_dept.insertRow();
		if (billvo == null) // 如果选择的是根节点或者没有选择
			billCard_dept.setValueAt("parentid", null);
		else
			billCard_dept.setValueAt("parentid", new StringItemVO(billvo.getStringValue("id")));
		billCard_dept.setEditableByInsertInit();
	}

}
