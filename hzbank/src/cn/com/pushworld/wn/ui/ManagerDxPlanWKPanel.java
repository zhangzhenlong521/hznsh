package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ManagerDxPlanWKPanel extends AbstractWorkPanel implements
		ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_start, btn_end, btn_delete;
	private String str;

	// private HashVO[] hashVos =null;

	@Override
	public void initialize() {
		// try {
		// hashVos=UIUtil.getHashVoArrayByDS(null,
		// "select * from sal_person_check_list where 1=1  and (type='61')  and (1=1)  order by seq");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		listPanel = new BillListPanel("WN_MANAGERPLAN_TABLE_Q01_ZPY");
		btn_start = new WLTButton("开始打分");
		btn_start.addActionListener(this);
		btn_end = new WLTButton("结束打分");
		btn_end.addActionListener(this);
		btn_delete = new WLTButton("删除");
		listPanel.addBatchBillListButton(new WLTButton[] { btn_delete,
				btn_start, btn_end });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) {// 删除数据
			deletePlan();
		} else if (e.getSource() == btn_start) {// 开始打分
			startGradeScore();
		} else if (e.getSource() == btn_end) {// 结束评分
			endGradeScore();
		}
	}

	private void endGradeScore() {
		try {
			// 结束当前考核计划
			BillVO selectedBillVO = listPanel.getSelectedBillVO();
			final String id = selectedBillVO.getStringValue("id");
			String state = selectedBillVO.getStringValue("state");// 获取到当前选中计划的状态
			if ("评分结束".equals(state)) {
				MessageBox.show(this, "当前考核计划已经结束,不需要重复结束");
				return;
			}
			// 判断当前评分是否结束
			String count = UIUtil
					.getStringValueByDS(
							null,
							"select count(*) num from WN_MANAGERDX_TABLE where state='评分中' and PFUSERNAME is null");
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			if (Double.parseDouble(count) > 0) {// 表示存在打分项尚未结束打分
				if (MessageBox.confirm("此项考核计划中存在客户经理打分项尚未完成，是否结束此项打分计划？")) {
					new SplashWindow(this, new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent e) {
							str = service.endManagerDXscore(id);
						}
					});
				} else {
					return;
				}
			} else {// 直接结束
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						str = service.endManagerDXscore(id);
					}
				});
			}
			UIUtil.executeUpdateByDS(null,
					"update wn_managerplan_table set state='评分结束' where id='"
							+ id + "'");
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startGradeScore() {// 为每个客户经理生成考核计划
		try {
			// 获取到当前选中计划
			BillVO selectedBillVO = listPanel.getSelectedBillVO();// 获取到当前选中计划
			final String id = selectedBillVO.getStringValue("id");// 获取到当前考核计划的ID
			String state = selectedBillVO.getStringValue("state");// 获取到计划状态
			if (!"未评分".equals(state)) {
				MessageBox.show(
						this,
						"当前客户经理定性考核计划【"
								+ selectedBillVO.getStringValue("planname")
								+ "】状态为【" + state + "】,无法再次生成！！！");
				return;
			}
			String countString = UIUtil.getStringValueByDS(null,
					"select count(*) from wn_managerplan_table where id!='"
							+ id + "' and state='评分中'");
			Integer count = Integer.parseInt(countString);
			if (count > 0) {
				HashVO[] plans = UIUtil.getHashVoArrayByDS(null,
						"select * from wn_managerplan_table where state='评分中'");
				StringBuilder buffer = new StringBuilder();
				for (int i = 0; i < plans.length; i++) {
					buffer.append(plans[i].getStringValue("planname"));
				}
				MessageBox.show(this, "存在客户经理定性考核计划【" + buffer
						+ "】评分尚未结束，请先结束其他计划");
				return;
			}
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					str = service.gradeManagerDXscore(id);
				}
			});
			MessageBox.show(this, str);
			UIUtil.executeUpdateByDS(null,
					"update wn_managerplan_table set state='评分中' where id='"
							+ id + "'");
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deletePlan() {// 删除计划
		try {
			BillVO[] billVos = listPanel.getSelectedBillVOs();
			if (billVos.length <= 0) {
				MessageBox.show(this, "请选中一行数据进行操作");
			}
			StringBuffer str = new StringBuffer();
			List<String> _sqllist = new ArrayList<String>();
			String state = "";
			String planname = "";
			String id = "";
			String _sql = "";
			for (int i = 0; i < billVos.length; i++) {
				state = billVos[i].getStringValue("state");// 获取到当前考核计划的状态
				planname = billVos[i].getStringValue("planname");
				id = billVos[i].getStringValue("id");
				if (!"未评分".equals(state)) {
					str.append(planname + " ");
				}
				_sql = "delete from wn_managerplan_table where id='" + id + "'";
				_sqllist.add(_sql);
			}
			UIUtil.executeBatchByDS(null, _sqllist);// 删除数据
			if (str.length() > 0) {
				MessageBox.show(this, "数据删除完成,计划名称为【" + str + "】无法删除。");
			} else {
				MessageBox.show(this, "数据删除完成");
			}
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}