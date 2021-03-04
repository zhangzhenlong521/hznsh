package cn.com.pushworld.salary.ui.targetcheck;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ButtonDefineVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
/**
 * 部门绩效计划制定
 * @author Administrator
 *
 */
public class DeptTargetSetWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel plan_list = null;
	private BillCellPanel bc = null;
	private WLTButton create_plan, edit_plan, del_plan, watch_plan, submit_plan, target_set = null;
	private WLTSplitPane wsp = null;
	public void initialize() {
		plan_list = new BillListPanel("SAL_TARGET_PLAN_CODE1");
//		target_list = new BillListPanel("SAL_TARGET_PLAN_ITEM_CODE1");
		initButton();
//		wsp = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, plan_list, getCellPanel());
//		wsp.setDividerLocation(250);
		this.add(plan_list, BorderLayout.CENTER);
	}
	
	public JPanel getCellPanel() {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		bc = new BillCellPanel();
		bc.setToolBarVisiable(false);
		bc.setAllowShowPopMenu(false);
		bc.setEditable(false);
		bc.setOpaque(false);
		jp.add(bc, BorderLayout.CENTER);
		return jp;
	}
	
	public void initButton() {
		ButtonDefineVO btnDefineVo = new ButtonDefineVO("制定计划");
		btnDefineVo.setBtntype(WLTButton.LIST_POPINSERT);
		btnDefineVo.setBtntext("制定计划");
		btnDefineVo.setBtnimg("zt_045.gif");
		btnDefineVo.setBtndescr("制定计划");
		create_plan = new WLTButton(btnDefineVo);
		target_set = new WLTButton("指标设置", "folder_star.png");
		target_set.setToolTipText("选中计划进行指标设置");
		edit_plan = new WLTButton("修改计划", "cog_edit.png");
		del_plan = new WLTButton("删除计划", "del.gif");
		submit_plan = new WLTButton("启用计划", "zt_028.gif");
		watch_plan = new WLTButton("计划总览", "zt_010.gif");
		del_plan.addActionListener(this);
		submit_plan.addActionListener(this);
		watch_plan.addActionListener(this);
		target_set.addActionListener(this);
		edit_plan.addActionListener(this);
		plan_list.addBatchBillListButton(new WLTButton[]{create_plan, target_set, edit_plan, del_plan, watch_plan, submit_plan});
		plan_list.repaintBillListButton();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == target_set) {
			onTargetSet();
		} else if (e.getSource() == del_plan) {
			onDelPlan();
		} else if (e.getSource() == submit_plan) {
			onSubmitPlan();
		} else if (e.getSource() == watch_plan) {
			onWatchPlan();
		} else if (e.getSource() == edit_plan) {
			onEdit();
		}
	}

	private void onWatchPlan() {
		if (checkSelectOne()) {
			try {
				BillVO vo = plan_list.getSelectedBillVO();
				HashMap _parMap = new HashMap();
				_parMap.put("planid", vo.getStringValue("id"));
				HashMap rtn = UIUtil.commMethod("cn.com.pushworld.salary.bs.targetcheck.TargetCheckTool", "getTargetCheckPlanCellVO", _parMap);
				BillCellVO vo_ = (BillCellVO)rtn.get("vo");
				BillCellPanel cp = new BillCellPanel();
				cp.loadBillCellData(vo_);
				cp.setToolBarVisiable(false);
				cp.setAllowShowPopMenu(false);
				cp.setEditable(false);
				cp.setOpaque(false);
				BillDialog bd = new BillDialog(plan_list);
				bd.setTitle("计划总览");
				bd.addConfirmButtonPanel(2);
				bd.add(cp, BorderLayout.CENTER);
				bd.setSize(800, 600);
				bd.locationToCenterPosition();
				bd.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void onEdit() {
		if (checkSelectOne()) {
			BillVO billVO = plan_list.getSelectedBillVO();
//			if ("已启用".equals(billVO.getStringValue("status"))) {
//				MessageBox.show(plan_list, "该计划已经启用,无法执行此操作!");
//				return;
//			}
			BillCardPanel cardPanel = new BillCardPanel(plan_list.templetVO);
			cardPanel.setBillVO(billVO);
			BillCardDialog dialog = new BillCardDialog(plan_list, plan_list.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dialog.setVisible(true);
			if (dialog.getCloseType() == 1) {
				plan_list.setBillVOAt(plan_list.getSelectedRow(), dialog.getBillVO(), false); //
				plan_list.setRowStatusAs(plan_list.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		}
	}

	private void onSubmitPlan() {
		if (checkSelectOne()) {
			BillVO vo = plan_list.getSelectedBillVO();
//			if ("已启用".equals(vo.getStringValue("status"))) {
//				MessageBox.show(plan_list, "该计划已经启用,无需执行此操作!");
//				return;
//			}
			if (MessageBox.showConfirmDialog(plan_list, "启用后无法进行指标设置以及删除操作,您确认启用该计划吗?", "提醒", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				UpdateSQLBuilder usb = new UpdateSQLBuilder(plan_list.getTempletVO().getSavedtablename());
				usb.putFieldValue("status", "已启用");
				usb.setWhereCondition(vo.getPkName() + "=" + vo.getPkValue());
				try {
					UIUtil.executeUpdateByDS(null, usb);
					MessageBox.show(plan_list, "操作成功!");
					plan_list.refreshCurrSelectedRow();
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox.show(plan_list, "操作失败!请再次尝试或与管理员联系!");
				}
			}
		}
	}

	private void onDelPlan() {
		if (checkSelectOne()) {
			BillVO vo = plan_list.getSelectedBillVO();
//			if ("已启用".equals(vo.getStringValue("status"))) {
//				MessageBox.show(plan_list, "该计划已经启用,无法执行此操作!");
//				return;
//			}
			if (MessageBox.showConfirmDialog(plan_list, "您确认删除该计划吗?", "提醒", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				DeleteSQLBuilder dsb = new DeleteSQLBuilder(plan_list.getTempletVO().getSavedtablename());
				dsb.setWhereCondition(vo.getPkName() + "=" + vo.getPkValue());
				DeleteSQLBuilder dsb2 = new DeleteSQLBuilder("sal_target_plan_item");
				dsb2.setWhereCondition("planid=" + vo.getPkValue());
				DeleteSQLBuilder dsb3 = new DeleteSQLBuilder("sal_target_plan_score");
				dsb3.setWhereCondition("planitemid in (select id from sal_target_plan_item where planid=" + vo.getPkValue() + ")");
				try {
					UIUtil.executeBatchByDS(null, new String[]{dsb.getSQL(), dsb2.getSQL(), dsb3.getSQL()});
					MessageBox.show(plan_list, "操作成功!");
					plan_list.removeSelectedRow();
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox.show(plan_list, "操作失败!请再次尝试或与管理员联系!");
				}
			}
		}
	}

	private void onTargetSet() {
		if (checkSelectOne()) {
//			BillVO vo = plan_list.getSelectedBillVO();
//			if ("已启用".equals(vo.getStringValue("status"))) {
//				MessageBox.show(plan_list, "该计划已经启用,无法执行此操作!");
//				return;
//			}
			DeptTargetSetDialog dtsd = new DeptTargetSetDialog(plan_list, plan_list.getSelectedBillVO());
			dtsd.setVisible(true);
		}
	}
	
	private boolean checkSelectOne() {
		BillVO vo = plan_list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(plan_list);
			return false;
		}
		return true;
	}

}
