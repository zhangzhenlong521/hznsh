package com.pushworld.ipushgrc.ui.score.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/***
 * 积分减免申请界面
 * @author 张营闯 2013-05-14
 * */
public class ReduceApplyEditWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel billListPanel;
	private WLTButton btn_add, btn_delete, btn_edit, btn_commit;//新增、删除、修改、流程处理

	@Override
	public void initialize() {
		billListPanel = new BillListPanel("SCORE_REDUCE_ZYC_E01");
		btn_add = new WLTButton("新增");
		btn_edit = new WLTButton("修改");
		btn_delete = new WLTButton("删除");
		btn_commit = new WLTButton("减免申请", "office_046.gif");
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_commit.addActionListener(this);
		billListPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_commit });
		billListPanel.repaintBillListButton();
		this.add(billListPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {//新增逻辑
			onInsert();
		} else if (e.getSource() == btn_edit) {//编辑逻辑
			onEdit();
		} else if (e.getSource() == btn_delete) {//删除逻辑
			onDelete();
		} else if (e.getSource() == btn_commit) {//减免申请的逻辑
			onCommit();
		}
	}

	/**
	 * 新增逻辑处理
	 * */
	private void onInsert() {
		BillCardPanel cardPanel = new BillCardPanel(billListPanel.getTempletVO());
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setGroupVisiable("审核信息", false);//设置【审核信息】不可见，在流程最终处理时可见
		BillCardDialog cardDialog = new BillCardDialog(this, "新增", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			int li_newrow = billListPanel.newRow(false); //
			billListPanel.setBillVOAt(li_newrow, cardDialog.getBillVO(), false);
			billListPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			billListPanel.setSelectedRow(li_newrow); //
		}
	}

	/**
	 * 修改逻辑处理
	 * */
	private void onEdit() {
		BillVO billVO = billListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(billListPanel);
			return;
		} else if (!"未审核".equals(billVO.getStringValue("state"))) {
			MessageBox.show(billListPanel, "该记录" + billVO.getStringValue("state") + ", 不能编辑!");
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(billListPanel.templetVO);
		cardPanel.setBillVO(billVO); //
		cardPanel.setGroupVisiable("审核信息", false);//设置【审核信息】不可见，在流程最终处理时可见
		BillCardDialog dialog = new BillCardDialog(this, billListPanel.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			billListPanel.setBillVOAt(billListPanel.getSelectedRow(), dialog.getBillVO());//即刷新列表中选中记录的数据显示
			billListPanel.setRowStatusAs(billListPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
		}
	}

	/**
	 * 删除逻辑处理
	 */
	private void onDelete() {
		BillVO billVO = billListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(billListPanel);
			return;
		} else if (!"未审核".equals(billVO.getStringValue("state"))) {
			MessageBox.show(billListPanel, "该记录" + billVO.getStringValue("state") + ", 不能删除!");
			return;
		}
		billListPanel.doDelete(false);
	}

	/**
	 * 发起减免申请流程的逻辑【李春娟/2013-05-16】
	 */
	private void onCommit() {
		BillVO billVO = billListPanel.getSelectedBillVO(); //	
		if (billVO == null) {
			MessageBox.showSelectOne(this); //
			return; //
		}
		if (billVO.getStringValue("WFPRINSTANCEID") == null || "未审核".equals(billVO.getStringValue("state"))) {
			String currdate = TBUtil.getTBUtil().getCurrDate();
			String userid = billVO.getStringValue("userid");
			String sql1 = "select sum(finalscore) from v_score_user where userid = " + userid + " and deptid=" + billVO.getStringValue("corpid") + " and EFFECTDATE like '" + currdate.substring(0, 4) + "%' and state='已生效'";
			String sql2 = "select sum(realscore) from score_reduce where userid = " + userid + " and corpid=" + billVO.getStringValue("corpid") + " and REALSCORE is not null";
			String str_score;
			try {
				str_score = UIUtil.getStringValueByDS(null, sql1);
				if (str_score == null || str_score.equals("")) {
					str_score = "0";
				}
				double score = Double.parseDouble(str_score);
				String str_reducescore = UIUtil.getStringValueByDS(null, sql2);
				if (str_reducescore == null || str_reducescore.equals("")) {
					str_reducescore = "0";
				}
				double reducescore = Double.parseDouble(str_reducescore);
				if (score <= reducescore) {//判断总违规积分是否比总减免积分小，如果是，则提示无需减免。
					double totalscore = score - reducescore;
					StringBuffer scoreBuffer = new StringBuffer("用户【" + billVO.getStringViewValue("userid") + "】在机构【" + billVO.getStringViewValue("corpid") + "】的积分如下：\r\n\r\n");
					scoreBuffer.append("总违规积分：");
					scoreBuffer.append(score);
					scoreBuffer.append("分\r\n总减免积分：");
					scoreBuffer.append(reducescore);
					scoreBuffer.append("分\r\n    总积分：");
					scoreBuffer.append(totalscore);
					scoreBuffer.append("分\r\n\r\n总积分=总违规积分-总减免积分\r\n\r\n 总积分<=0,故无需减免。");

					MessageBox.showTextArea(this, "提示", scoreBuffer.toString());
					return;
				}
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return;
			}
		}

		if (!billVO.containsKey("wfprinstanceid")) {
			MessageBox.show(billListPanel, "选中的记录中没有定义工作流字段(wfprinstanceid)!"); //
			return; //
		}
		String str_wfprinstanceid = billVO.getStringValue("wfprinstanceid"); //
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {//如果流程未发起，则发起流程，否则监控流程
			new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", billListPanel, null); //处理动作!
		} else {
			cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(billListPanel, str_wfprinstanceid, billVO); //
			wfMonitorDialog.setMaxWindowMenuBar();
			wfMonitorDialog.setVisible(true); //
		}
	}

}
