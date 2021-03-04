package cn.com.pushworld.salary.ui.targetcheck;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.pushworld.salary.ui.paymanage.SalaryImportPersonInfoDialog;

public class DeptTargetSetDialog extends BillDialog implements
		BillListSelectListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private BillListPanel target_list, scorer_list = null;
	private WLTSplitPane wsp = null;
	private BillVO planvo = null;
	private WLTButton addTarget, remvTarget, seqUpMove, seqDownMove,
			saveTarget, addScorer, remvScorer, saveScorer, seqUpMove1,
			seqDownMove1, sureBtn = null;

	public DeptTargetSetDialog(Container _parent, BillVO _planvo) {
		super(_parent, Toolkit.getDefaultToolkit().getScreenSize().width - 50,
				Toolkit.getDefaultToolkit().getScreenSize().height - 100);
		this.setTitle("指标设置");
		this.planvo = _planvo;
		this.locationToCenterPosition();
		init();
	}

	public void init() {
		target_list = new BillListPanel("SAL_TARGET_PLAN_ITEM_CODE1");
		target_list.QueryDataByCondition(" planid=" + planvo.getPkValue());
		target_list.addBillListSelectListener(this);
//		target_list.setRowNumberChecked(true);
		scorer_list = new BillListPanel("SAL_TARGET_PLAN_SCORE_CODE1");
		scorer_list.QueryDataByCondition(" 1=2 ");
		scorer_list.setRowNumberChecked(true);
		addTarget = new WLTButton("增加指标", "office_163.gif");
		remvTarget = new WLTButton("移除指标", "office_165.gif");
		seqUpMove = new WLTButton("指标上移", "office_081.gif");
		seqUpMove.setToolTipText("指标顺序调整");
		seqDownMove = new WLTButton("指标下移", "office_059.gif");
		seqDownMove.setToolTipText("指标顺序调整");
		saveTarget = new WLTButton("保存指标权重", "zt_068.gif");
		addScorer = new WLTButton("增加评分人", "office_163.gif");
		remvScorer = new WLTButton("移除评分人", "office_165.gif");
		saveScorer = new WLTButton("保存评分人权重", "zt_068.gif");
		seqUpMove1 = new WLTButton("评分人上移", "office_081.gif");
		seqUpMove1.setToolTipText("评分人顺序调整");
		seqDownMove1 = new WLTButton("评分人下移", "office_059.gif");
		seqDownMove1.setToolTipText("评分人顺序调整");
		addTarget.addActionListener(this);
		remvTarget.addActionListener(this);
		seqUpMove.addActionListener(this);
		seqDownMove.addActionListener(this);
		saveTarget.addActionListener(this);
		addScorer.addActionListener(this);
		remvScorer.addActionListener(this);
		saveScorer.addActionListener(this);
		target_list.addBatchBillListButton(new WLTButton[] { addTarget,
				remvTarget, seqUpMove, seqDownMove, saveTarget });
		scorer_list.addBatchBillListButton(new WLTButton[] { addScorer,
				remvScorer, seqUpMove1, seqDownMove1, saveScorer });
		target_list.repaintBillListButton();
		scorer_list.repaintBillListButton();
		wsp = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, target_list,
				scorer_list);
		wsp.setDividerLocation(550);
		this.add(wsp, BorderLayout.CENTER);
		this.add(getSouthPanel(), BorderLayout.SOUTH);
	}
	
	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		sureBtn = new WLTButton("确定");
		sureBtn.addActionListener(this);
		panel.add(sureBtn);
		return panel;
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		scorer_list.QueryDataByCondition("planitemid="
				+ event.getCurrSelectedVO().getPkValue());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addTarget) {
			onAddTarget();
		} else if (e.getSource() == remvTarget) {
			onDelTarget();
		} else if (e.getSource() == seqUpMove) {
			onSeqUpMoveF(target_list);
		} else if (e.getSource() == seqDownMove) {
			onSeqDownMoveF(target_list);
		} else if (e.getSource() == saveTarget) {
			onSaveTarget();
		} else if (e.getSource() == addScorer) {
			onAddScorer();
		} else if (e.getSource() == remvScorer) {
			onDelScorer();
		} else if (e.getSource() == saveScorer) {
			onSaveScorer();
		} else if (e.getSource() == seqUpMove1) {
			onSeqUpMoveF(scorer_list);
		} else if (e.getSource() == seqDownMove1) {
			onSeqDownMoveF(scorer_list);
		} else if (e.getSource() == sureBtn) {
			onSure();
		}
	}

	private void onSure() {
		if (scorer_list.checkValidate() && target_list.checkValidate()) {
			this.dispose();
		}
	}

	private void onSaveScorer() {
		if (scorer_list.checkValidate()) {
			if (scorer_list.saveData()) {
				MessageBox.show(scorer_list, "保存成功!");
			}
		}
	}

	private void onSaveTarget() {
		if (target_list.checkValidate()) {
			if (target_list.saveData()) {
				MessageBox.show(target_list, "保存成功!");
			}
		}
	}

	private void onDelScorer() {
		BillVO[] vos_ = scorer_list.getCheckedBillVOs();
		if (vos_ == null || vos_.length <= 0) {
			MessageBox.show(scorer_list, "请至少勾选一条评分人记录来进行此操作!");
			return;
		}
		List<String> delSql = new ArrayList<String>();
		for (int i = 0; i < vos_.length; i++) {
			DeleteSQLBuilder dsb2 = new DeleteSQLBuilder("sal_target_plan_score");
			dsb2.setWhereCondition("id=" + vos_[i].getPkValue());
			delSql.add(dsb2.getSQL());
		}
		try {
			UIUtil.executeBatchByDS(null, delSql);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(target_list, "移除评分人失败,请再次尝试或与系统管理员联系!");
			return;
		}
		MessageBox.show(scorer_list, "操作成功!");
		scorer_list.refreshData();
	}

	private void onAddScorer() {
		BillVO vo = target_list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(target_list, "请选择一条指标记录来进行此操作!");
			return;
		}
		SalaryImportPersonInfoDialog sipd = new SalaryImportPersonInfoDialog(scorer_list, vo){
			public void onConfirm() {
				BillVO[] vos = bl.getCheckedBillVOs();
				if (vos == null || vos.length <= 0) {
					MessageBox.show(bl, "请至少勾选一条人员信息进行此操作!");
					return;
				}
				
				BillVO[] selectvos = vos;
				try {
					HashMap map = UIUtil.getHashMapBySQLByDS(null, "select scoreuser, id from sal_target_plan_score where planitemid=" + accountvo.getStringValue("id"));
					String seq = UIUtil.getStringValueByDS(null, "select max(seq) from sal_target_plan_score where planitemid=" + accountvo.getStringValue("id"));
					if (seq == null || "".equals(seq)) {
						seq = "0";
					}
					int j = 1;
					List<String> sqls = new ArrayList<String>();
					for (int i = 0 ; i < selectvos.length; i++) {
						if (map.containsKey(selectvos[i].getStringValue("id"))) {
							continue;
						}
						InsertSQLBuilder isb = new InsertSQLBuilder();
						isb.setTableName("sal_target_plan_score");
						isb.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_SAL_TARGET_PLAN_SCORE"));
						isb.putFieldValue("planitemid", accountvo.getStringValue("id"));
						isb.putFieldValue("scoreuser", selectvos[i].getStringValue("id"));
						isb.putFieldValue("createdate", TBUtil.getTBUtil().getCurrDate());
						isb.putFieldValue("status", "未评分");
						isb.putFieldValue("seq", (Integer.parseInt(seq) + j));
						j = j + 1;
						sqls.add(isb.getSQL());
					}
					UIUtil.executeBatchByDS(null, sqls);
					parentlist.refreshData();
					MessageBox.show(bl, "操作成功!");
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox.show(bl, "增加人员失败!请再次尝试或与管理员联系!");
				}
			
			}
		};
		sipd.setSize(900, 600);
		sipd.setVisible(true);
	}

	private void onAddTarget() {
		BillListDialog bld = new BillListDialog(target_list, "选择指标",
				"SAL_TARGET_LIST_TARGETPLAN", 900, 600) {
			private static final long serialVersionUID = 1L;
			public void onConfirm() {
				try {
					if (billlistPanel.getCheckedBillVOs() == null
							|| billlistPanel.getCheckedBillVOs().length <= 0) {
						MessageBox.show(target_list, "请至少勾选一条记录来进行此操作!");
						return;
					}
					this.setCloseType(BillDialog.CONFIRM);
					this.dispose();
				} catch (Exception e) {
					MessageBox.showException(target_list, e);
				}
			}
		};
		String blcorp = planvo.getRealValue("deptid");
		HashVO[] allcorpids = null;
		List<String> ids = new ArrayList<String>();
		try {
			allcorpids = UIUtil.getHashVoArrayAsTreeStructByDS(null, "select * from pub_corp_dept", "id", "parentid", "", "id=" + blcorp);
		} catch (Exception e1) {
			e1.printStackTrace();
			ids.add(blcorp);
		}
		if (allcorpids != null && allcorpids.length > 0) {
			for (int j = 0; j < allcorpids.length; j++) {
				ids.add(allcorpids[j].getStringValue("id"));
			}
		}
		bld.getBilllistPanel().QueryDataByCondition(
				"deptid in (" + TBUtil.getTBUtil().getInCondition(ids)
						+ ") and ( type = '部门垂直指标' or type = '部门平行指标' )");
		bld.getBilllistPanel().setRowNumberChecked(true);
		bld.setVisible(true);
		if (bld.getCloseType() != BillDialog.CONFIRM) {
			return;
		}
		BillVO[] selectvos = bld.getBilllistPanel().getCheckedBillVOs();
		try {
			HashMap map = UIUtil.getHashMapBySQLByDS(null,
					"select targetid, id from sal_target_plan_item where planid="
							+ planvo.getStringValue("id"));
			List<String> sqls = new ArrayList<String>();
			for (int i = 0; i < selectvos.length; i++) {
				if (map.containsKey(selectvos[i].getStringValue("id"))) {
					continue;
				}
				InsertSQLBuilder isb = new InsertSQLBuilder();
				isb.setTableName("sal_target_plan_item");
				isb.putFieldValue("id", UIUtil.getSequenceNextValByDS(null,
						"S_SAL_TARGET_PLAN_ITEM"));
				isb.putFieldValue("planid", planvo.getPkValue());
				isb.putFieldValue("targetid", selectvos[i].getStringValue("id"));
				isb.putFieldValue("refvalue", "");
				isb.putFieldValue("weights", "100%");
				isb.putFieldValue("scoreusers", "");
				isb.putFieldValue("totlescore", "4");
				sqls.add(isb.getSQL());
			}
			UIUtil.executeBatchByDS(null, sqls);
			MessageBox.show(target_list, "操作成功!");
			target_list.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(target_list, "新增指标失败!请再次尝试或与管理员联系!");
		}
	}

	public void onDelTarget() {
		BillVO vos_ = target_list.getSelectedBillVO();
		if (vos_ == null) {
			MessageBox.show(target_list, "请选择一条指标记录来进行此操作!");
			return;
		}
		List<String> delSql = new ArrayList<String>();
		DeleteSQLBuilder dsb = new DeleteSQLBuilder("sal_target_plan_item");
		dsb.setWhereCondition("id=" + vos_.getPkValue());
		delSql.add(dsb.getSQL());
		DeleteSQLBuilder dsb2 = new DeleteSQLBuilder("sal_target_plan_score");
		dsb2.setWhereCondition("planitemid=" + vos_.getPkValue());
		delSql.add(dsb2.getSQL());
		try {
			UIUtil.executeBatchByDS(null, delSql);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(target_list, "移除指标失败,请再次尝试或与系统管理员联系!");
			return;
		}
		MessageBox.show(target_list, "操作成功!");
		target_list.refreshData();
	}
	
	public void onSeqUpMoveF(BillListPanel account_fa_list) {
		if(checkChecked(account_fa_list)) {
			moveUpRow(account_fa_list);
			try {
				resetSeqByUI(account_fa_list);
			} catch (Exception e) {
				MessageBox.show(account_fa_list, "界面操作成功但是数据库保存失败, 请再次尝试或与系统管理员联系!");
				return;
			}
		}
	}
	
	public void onSeqDownMoveF(BillListPanel account_fa_list) {
		if(checkChecked(account_fa_list)) {
			moveDownRow(account_fa_list);
			try {
				resetSeqByUI(account_fa_list);
			} catch (Exception e) {
				MessageBox.show(account_fa_list, "界面操作成功但是数据库保存失败, 请再次尝试或与管理员联系!");
				return;
			}
		}
	}
	
	public void resetSeqByUI(BillListPanel account_fa_list) throws Exception {
		BillVO[] vos = account_fa_list.getAllBillVOs();
		if (vos != null && vos.length > 0) {
			List<String> sqls = new ArrayList<String>();
			for (int i = 0 ; i < vos.length ; i++) {
				UpdateSQLBuilder usb = new UpdateSQLBuilder();
				usb.setTableName(account_fa_list.templetVO.getSavedtablename());
				usb.putFieldValue("seq", i + 1);
				usb.setWhereCondition("id=" + vos[i].getStringValue("id"));
				sqls.add(usb.getSQL());
				account_fa_list.setRealValueAt((i + 1) + "", i, "seq");
			}
			UIUtil.executeBatchByDS(null, sqls);
		}
	}
	
	public boolean checkChecked(BillListPanel bl) {
		BillVO[] vos_ = bl.getSelectedBillVOs(true);
		if (vos_ == null || vos_.length <= 0) {
			MessageBox.show(bl, "请至少勾选一条记录来进行此操作!");
			return false;
		}
		return true;
	}
	
	public boolean moveUpRow(BillListPanel account_fa_list) {
		account_fa_list.stopEditing();
		int[] li_currRows = account_fa_list.getSelectedRows(true);
		int sign_stop = -1;
		for (int i = 0; i < li_currRows.length; i++) {
			int li_currRow = li_currRows[i];
			if (li_currRow < 0) {
				return false;
			}
			if (li_currRow > 0 && (li_currRow - 1) != sign_stop) {
				account_fa_list.bo_ifProgramIsEditing = true;
				account_fa_list.getTableModel().moveRow(li_currRow, li_currRow, li_currRow - 1);
				account_fa_list.getTable().removeRowSelectionInterval(li_currRow, li_currRow);
				account_fa_list.getTable().addRowSelectionInterval(li_currRow - 1, li_currRow - 1);
				account_fa_list.bo_ifProgramIsEditing = false;
				sign_stop = li_currRow - 1;
			} else {
				sign_stop = li_currRow;
			}
		}
		return true;
	}

	public boolean moveDownRow(BillListPanel account_fa_list) {
		account_fa_list.stopEditing();
		int[] li_currRows = account_fa_list.getSelectedRows(true);
		int sign_stop = account_fa_list.getTableModel().getRowCount();
		for (int i = li_currRows.length - 1; i >= 0; i--) {
			int li_currRow = li_currRows[i];
			if (li_currRow < 0) {
				return false;
			}
			if (li_currRow >= 0 && (li_currRow + 1) != sign_stop) {
				account_fa_list.bo_ifProgramIsEditing = true;
				account_fa_list.getTableModel().moveRow(li_currRow, li_currRow, li_currRow + 1);
				account_fa_list.getTable().removeRowSelectionInterval(li_currRow, li_currRow);
				account_fa_list.getTable().addRowSelectionInterval(li_currRow + 1, li_currRow + 1);
				account_fa_list.bo_ifProgramIsEditing = false;
				sign_stop = li_currRow + 1;
			} else {
				sign_stop = li_currRow;
			}
		}
		return true;
	}
}
