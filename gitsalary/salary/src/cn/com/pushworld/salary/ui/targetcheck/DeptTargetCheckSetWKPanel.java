package cn.com.pushworld.salary.ui.targetcheck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 全行计划启用后，维护部门对应的指标。操作一般由管理员来做。或者特定角色。
 * 
 * @author haoming create by 2013-7-5
 */
public class DeptTargetCheckSetWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener {
	private static final long serialVersionUID = -6342742052574757603L;
	private BillTreePanel deptTreePanel;
	private WLTSplitPane left_right_splitPane;
	private BillListPanel checklistPanel, targetListPanel; // 考核部门列表，被考核部门指标列表。
	private WLTButton btn_check_dept_import, btn_check_dept_reomve, btn_score_import, btn_score_remove, btn_target_import, btn_target_remove;
	private WLTTabbedPane tabedPane = new WLTTabbedPane();

	@Override
	public void initialize() {
		deptTreePanel = new BillTreePanel("PUB_CORP_DEPT_SELF");
		deptTreePanel.addBillTreeSelectListener(this);
		deptTreePanel.queryDataByCondition(null);

		targetListPanel = new BillListPanel("SAL_TARGET_LIST_CODE1"); // 该部门要考核的指标。根据指标的相关部门和考核人，自动生成 被考核记录
		btn_target_import = new WLTButton("添加考核指标");
		btn_target_remove = new WLTButton("从考核中移除");
		btn_target_import.addActionListener(this);
		btn_target_remove.addActionListener(this);
		targetListPanel.addBatchBillListButton(new WLTButton[] { btn_target_import, btn_target_remove });
		targetListPanel.repaintBillListButton();

		checklistPanel = new BillListPanel("PUB_CORP_DEPT_SELF"); // 考评机构 ,通过score表去计算有那些考核机构。distinct。
		btn_check_dept_import = new WLTButton("添加考评部门");
		btn_check_dept_reomve = new WLTButton("移除考评部门");
		btn_check_dept_import.addActionListener(this);
		btn_check_dept_reomve.addActionListener(this);
		checklistPanel.addBatchBillListButton(new WLTButton[] { btn_check_dept_import, btn_check_dept_reomve });
		checklistPanel.repaintBillListButton();

		BillListPanel scoreListPanel = new BillListPanel("SAL_DEPT_CHECK_SCORE_CODE1"); // 考评指标
		btn_score_import = new WLTButton("添加指标");
		btn_score_remove = new WLTButton("移除指标");
		scoreListPanel.addBatchBillListButton(new WLTButton[] { btn_score_import, btn_score_remove });
		scoreListPanel.repaintBillListButton();
		WLTSplitPane up_down_split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, checklistPanel, scoreListPanel);
		tabedPane.addTab("本次考核指标", targetListPanel);
		tabedPane.addTab("考核权重设定", up_down_split);
		left_right_splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, deptTreePanel, tabedPane);
		this.add(left_right_splitPane);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_check_dept_import) {
			onDeptImport();
		} else if (e.getSource() == btn_check_dept_reomve) {

		} else if (e.getSource() == btn_target_import) { // 部门指标导入到本次评价池中。
			onImportTargetToPond();
		} else if (e.getSource() == btn_target_remove) { // 部门指标导入到本次评价池中。
			onRemoveTargetToPond();
		}
	}

	private void onDeptImport() {
		BillVO checkedDeptVO = deptTreePanel.getSelectedVO();// 被考核的机构
		if (checkedDeptVO == null) {
			MessageBox.show(this, "请在左侧选择被考核部门!");
			return;
		}
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		BillVO checkedDeptVO = event.getCurrSelectedVO(); // 被考核机构
		if (checkedDeptVO == null) {
			return;
		}

		if (tabedPane.getSelectedIndex() == 0) {// 指标页面
			try {
				HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select *from sal_dept_check where deptid = '" + checkedDeptVO.getPkValue() + "'");
				if (vos.length > 0) {
					String targets = vos[0].getStringValue("targetlist");
					targetListPanel.QueryDataByCondition(" id in(" + TBUtil.getTBUtil().getInCondition(targets) + ")");
				} else {
					targetListPanel.removeAllRows();
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else { // 上下结构的指标权重界面。
			try {
				// 考核机构
				HashVO checkdept[] = UIUtil.getHashVoArrayByDS(null, "select distinct(scoredeptid) from SAL_DEPT_CHECK_SCORE where checkdept='" + checkedDeptVO.getPkValue() + "'");
				List ids = new ArrayList<String>();
				for (int i = 0; i < checkdept.length; i++) {
					ids.add(checkdept[i].getStringValue("scoredeptid"));
				}
				checklistPanel.QueryDataByCondition(" id in(" + TBUtil.getTBUtil().getInCondition(ids) + ")");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// 导入指标到本次考评池中，
	private void onImportTargetToPond() {
		BillVO checkedDeptVO = deptTreePanel.getSelectedVO();// 被考核的机构
		if (checkedDeptVO == null) {
			MessageBox.show(this, "请在左侧选择被考核部门!");
			return;
		}
		BillVO allBillVO[] = targetListPanel.getAllBillVOs();
		BillListDialog listDialog = new BillListDialog(this, "选择ＸＸ部门指标", "SAL_TARGET_LIST_CODE1",
				" deptid = " + checkedDeptVO.getPkValue() + " and id not in(" + getInCondition(allBillVO, "id") + ")", 800, 600);
		listDialog.getBilllistPanel().setRowNumberChecked(true);
		listDialog.setVisible(true);
		BillVO retTargetVos[] = listDialog.getReturnBillVOs();
		if (listDialog.getCloseType() == 1 && retTargetVos.length > 0) {
			targetListPanel.addBillVOs(retTargetVos);
			List<String> oldList = getBillVosItemList(allBillVO, "id"); // 旧记录
			List<String> newList = getBillVosItemList(retTargetVos, "id");
			oldList.addAll(newList);
			String targetIDs = getMutilValueStr(oldList); // 得到当前部门所有指标串。
			UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_dept_check");
			updateSql.putFieldValue("targetlist", targetIDs);
			updateSql.setTableName(" deptid = '" + checkedDeptVO.getPkValue() + "' ");
			try {
				UIUtil.executeUpdateByDS(null, updateSql);
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 从本次考核池中移除该记录。需要判断，是否有评过。
	private void onRemoveTargetToPond() {
		// 需要完善。

	}

	// 根据billVO的某列得到in
	private String getInCondition(BillVO billvo[], String _item) {
		return TBUtil.getTBUtil().getInCondition(getBillVosItemList(billvo, _item));
	}

	// 得到一个BIllVO某列值的集合。
	private List getBillVosItemList(BillVO[] _vos, String _item) {
		List l = new ArrayList();
		for (int i = 0; i < _vos.length; i++) {
			l.add(_vos[i].getStringValue(_item));
		}
		return l;
	}

	// 得到多个值存储格式;1;3;4;5;
	private String getMutilValueStr(List<String> _list) {
		if (_list == null || _list.size() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < _list.size(); i++) {
			String id = _list.get(i);
			if (id != null && !id.equals("")) {
				sb.append(";" + id);
			}
		}
		if (sb.length() > 0) {
			sb.append(";"); // 收尾
			return sb.toString();
		}
		return "";
	}
}
