package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.bs.WnSalaryServiceImpl;

public class KJScorePlanWKPanel extends AbstractWorkPanel implements
		ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_delete, btn_grade, btn_end;
	private String str;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_KJDXPLAN_TABLE_Q01_ZPY");
		btn_delete = new WLTButton("删除");
		btn_delete.addActionListener(this);
		btn_grade = new WLTButton("开始考核");
		btn_grade.addActionListener(this);
		btn_end = new WLTButton("结束考核");
		btn_end.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_delete,
				btn_grade, btn_end });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) {// 删除打分计划
			try {
				BillVO vo = listPanel.getSelectedBillVO();
				if (vo == null) {
					MessageBox.show(this, "请选中一行计划进行操作");
					return;
				}
				String state = vo.getStringValue("state");
				if (!"未评分".equals(state)) {
					MessageBox.show(this,
							"当前委派会计考核计划【" + vo.getStringValue("PLANNAME")
									+ "】的状态为【" + state + "】，无法删除");
					return;
				}
				UIUtil.executeUpdateByDS(
						null,
						"delete from wn_kjdxplan_table where id='"
								+ vo.getStringValue("id") + "'");
				listPanel.refreshData();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == btn_grade) {// 生成打分计划
			try {
				BillVO vo = listPanel.getSelectedBillVO();
				final String id = vo.getStringValue("id");
				if (vo == null) {
					MessageBox.show(this, "请选中一行考核计划操作");
					return;
				}
				String state = vo.getStringValue("state");// 获取到当前状态
				if (!"未评分".equals(state)) {
					MessageBox.show(this,
							"当前考核计划为【" + vo.getStringValue("PLANNAME")
									+ "】的状态为【" + state + "】,无法再次生成。");
					return;
				}
				// 判断是否存在其他打分计划处于评分中
				HashVO[] gradingScore = UIUtil.getHashVoArrayByDS(null,
						"select * from wn_kjdxplan_table where state='评分中'");
				if (gradingScore.length > 0) {
					MessageBox.show(this, "存在其他考核计划处于评分中，请先结束其他考核计划");
					return;
				}
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						str = service.getKJDXScore(id);
					}
				});
				UIUtil.executeUpdateByDSPS(null,
						"update wn_kjdxplan_table set state='评分中' where id='"
								+ id + "'");
				MessageBox.show(this, str);
				listPanel.refreshData();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == btn_end) {// 结束当前考核计划
			try {
				BillVO vo = listPanel.getSelectedBillVO();
				final String id = vo.getStringValue("id");
				if (vo == null) {
					MessageBox.show(this, "请选中一条考核计划进行操作");
					return;
				}
				String state = vo.getStringValue("state");
				if (!"评分中".equals(state)) {
					MessageBox.show(this,
							"当前考核计划【" + vo.getStringValue("PLANNAME")
									+ "】的状态为【" + state + "】,无法结束");
					return;
				}
				HashVO[] scoreNotEnd = UIUtil.getHashVoArrayByDS(null,
						"select * from wn_kjscore_table where  state='评分中'");
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				if (scoreNotEnd.length > 0) {
					if (MessageBox
							.confirm("在当前考核计划中，存在尚未结束评分的委派会计，是否已需要强制结束呢？")) {// 确定强制结束
						new SplashWindow(this, new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								str = service.getKJDXEnd(id);
							}
						});
					} else {
						return;
					}
				} else {
					new SplashWindow(this, new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent e) {
							str = service.getKJDXEnd(id);
						}
					});
				}
				UIUtil.executeUpdateByDS(null,
						"update wn_kjdxplan_table set state='评分结束' where id="
								+ id + "");
				MessageBox.show(this, str);
				listPanel.refreshData();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}