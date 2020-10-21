package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import bsh.This;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.bs.WnSalaryServiceImpl;

public class GydxdfWKPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private BillListPanel listPanel;
	private WLTButton btn_ks, btn_end, btn_delete;// 开始打分，结束打分
	private String result;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_GYDXPLAN_ZPY_Q01");
		btn_ks = new WLTButton("开始打分");
		btn_ks.addActionListener(this);
		btn_end = new WLTButton("结束打分");
		btn_end.addActionListener(this);
		btn_delete = new WLTButton("删除");
		btn_delete.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_delete, btn_ks,
				btn_end });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_ks) {// 开始考核，生成打分计划
			gradeScore();
		} else if (e.getSource() == btn_end) {// 结束考核
			gradeEnd();
		} else if (e.getSource() == btn_delete) {
			deleteRow();
		}
	}

	private void deleteRow() {// 删除行，并不是随意删除
		try {
			BillVO[] selected = listPanel.getSelectedBillVOs();
			if (selected.length < 0) {
				MessageBox.show(this, "请选中数据进行操作！！！");
				return;
			}
			StringBuilder str = new StringBuilder();
			List<String> _sqlList = new ArrayList<String>();
			for (int i = 0; i < selected.length; i++) {// 删除
				String planname = selected[i].getStringValue("planname");
				String state = selected[i].getStringValue("state");// 获取到当前计划状态
				String id = selected[i].getStringValue("id");
				if (!"未评分".equals(state)) {
					str.append(planname + " ");
				} else {
					String sql = "delete from WN_GYDXPLAN where id='" + id
							+ "'";
					_sqlList.add(sql);
				}
			}
			UIUtil.executeBatchByDS(null, _sqlList);
			if (str == null || str.toString().isEmpty() || str.length() == 0) {
				MessageBox.show(this, "删除操作完成");
			} else {
				MessageBox.show(this, "数据删除完成,计划名称为【" + str + "】无法删除。");
			}
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void gradeScore() {// 开始考核,为每个柜员生成定性考核计划
		try {
			BillVO selectedBillVO = listPanel.getSelectedBillVO();// 获取到当前选中计划
			final String id = selectedBillVO.getStringValue("id");// 获取到当前选中计划的ID
			String state = selectedBillVO.getStringValue("state");// 获取到计划状态
			if (!"未评分".equals(state)) {// 获取到当前状态
				MessageBox.show(
						this,
						"当前柜员定性考核计划【"
								+ selectedBillVO.getStringValue("planname")
								+ "】状态为【" + state + "】,无法再次生成！！！");
				return;
			}
			// 在生成考核计划之前，判断是否存在其他考核计划状态是否结束
			String countString = UIUtil.getStringValueByDS(null,
					"select count(*) from WN_GYDXPLAN where id!='" + id
							+ "' and state='评分中'");
			Integer count = Integer.parseInt(countString);
			if (count != 0) {
				HashVO[] plans = UIUtil.getHashVoArrayByDS(null,
						"select * from WN_GYDXPLAN where state='评分中'");
				StringBuffer buffer = new StringBuffer("");
				for (int i = 0; i < plans.length; i++) {
					buffer.append(plans[i].getStringValue("planname"));
				}
				MessageBox.show(this, "存在柜员定性考核计划【" + buffer
						+ "】评分尚未结束，请先结束其他计划");
				return;
			}
			// 判断当前考核计划是否全部评分完成以及复核完成
			String countString2 = UIUtil
					.getStringValueByDS(null,
							"SELECT COUNT(*) FROM wn_gydx_table WHERE state='评分中' OR fhresult IS NULL");
			Integer count2 = Integer.parseInt(countString2);
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			// if(count2>0){
			// int sumcount=MessageBox.showOptionDialog(this,
			// "当前考核计划中存在是否评分尚未结束或尚未复核的柜员，是否结束当前考核计划","提示", new String[] { "是",
			// "否" },1);
			// if(sumcount!=0){
			// new SplashWindow(this, new AbstractAction() {
			// @Override
			// public void actionPerformed(ActionEvent e) {
			// result=service.gradeDXscore(id);
			// }
			// });
			// listPanel.refreshData();
			// }else{
			// return;
			// }
			// }else{
			// new SplashWindow(this, new AbstractAction() {
			// @Override
			//
			// });
			// listPanel.refreshData();
			// }
			//
			//
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					result = service.gradeDXscore(id);
				}
			});
			listPanel.refreshData();
			MessageBox.show(this, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void gradeEnd() {// 结束考核
		try {
			BillVO selectedBillVO = listPanel.getSelectedBillVO();// 获取到当前考核计划
			if (selectedBillVO == null) {
				MessageBox.show(this, "请选中一行数据结束");
				return;
			}
			// 获取到当前计划状态
			String state = selectedBillVO.getStringValue("state");// 获取当前计划的状态
			String planName = selectedBillVO.getStringValue("planname");// 获取到当前计划的状态
			final String planid = selectedBillVO.getStringValue("id");// 获取到当前计划的ID
			if (!"评分中".equals(state)) {
				MessageBox.show(this, "当前考核计划【" + planName + "】状态为【" + state
						+ "】,无法结束。");
				return;
			}
			// 当用户选择确定结束时,记得让用户确定一下
			String count = UIUtil
					.getStringValueByDS(null,
							"select count(*) from wn_gydx_table where state='评分中' or fhresult='复核未通过'");
			if (Integer.parseInt(count) > 0) {
				if (MessageBox.confirm(this, "有未打完的评分，确定要强制结束吗？")) {
					// 结束当前考核计划
					final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					new SplashWindow(this, new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent e) {
							result = service.gradeDXEnd(planid);
						}
					});
				} else {
					return;
				}
			} else {
				// 结束当前考核计划
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						result = service.gradeDXEnd(planid);
					}
				});
			}
			String sql = "update  WN_GYDXPLAN  set STATE='评分结束' where id='"
					+ planid + "'";
			UIUtil.executeUpdateByDS(null, sql);
			MessageBox.show(this, result);
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ----->定时任务开始启动执行计划
	public void gradeScore(int num) {
		try {
			// 为整个考核计划生成计划ID
			String maxId = UIUtil.getStringValueByDS(null,
					"select max(ID) from WN_GYDX_TABLE");
			if (maxId == null || maxId.isEmpty()) {
				maxId = "1";
			} else {
				maxId = String.valueOf((Integer.parseInt(maxId) + 1));
			}
			WnSalaryServiceIfc service = new WnSalaryServiceImpl();
			String str = service.gradeDXscore(maxId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ------>定时任务结束启动执行计划
	public void gradeEnd(int num) {// 定性考核计划结束
		try {
			String countStr = UIUtil
					.getStringValueByDS(null,
							"select count(*) from WN_GYDX_TABLE where state='评分中' or  fhresult is null ");
			if (Integer.parseInt(countStr) <= 0) {
				return;
			} else {
				String planId = UIUtil
						.getStringValueByDS(null,
								"select id from WN_GYDX_TABLE where state='评分中' or fhresult  IS null");
				WnSalaryServiceIfc service = new WnSalaryServiceImpl();
				String str = service.gradeDXEnd(planId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
