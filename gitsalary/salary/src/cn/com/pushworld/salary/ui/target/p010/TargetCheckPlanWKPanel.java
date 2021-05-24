package cn.com.pushworld.salary.ui.target.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.paymanage.RefDialog_Month;

/**
 * 全行绩效考核安排。这个名字真难起。
 * 
 * @author haoming create by 2013-7-9
 */
public class TargetCheckPlanWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener {
	private static final long serialVersionUID = 1803665179022243678L;
	private BillListPanel planListPanel;
	private WLTButton btn_endDeptJJ, btn_enddeptDL, btn_createPlan, btn_delPlan, btn_enddeptDx, btn_endPlan, btn_calc_persondx, btn_calc_persondltarget, btn_QQ_money, btn_xymoney, btn_persondl_levelcalc, btn_qz,btn_end, btn_calc_postduty, btn_person_dl_auto;
	private SalaryServiceIfc service;
	private WLTTabbedPane tabPane = new WLTTabbedPane();
	private String containsPostDutyCheck = TBUtil.getTBUtil().getSysOptionStringValue("是否包含岗位职责评价功能", "N"); // 涡阳提出的岗责指标评价。
	private boolean calcQQ = TBUtil.getTBUtil().getSysOptionBooleanValue("是否计算亲情工资", true); // 默认计算亲情工资。如果不计算亲情工资，总分排名还是需要算的。
	private boolean haveDeptJJTarget = TBUtil.getTBUtil().getSysOptionBooleanValue("是否显示部门计价指标", false); //阳城提出部门支行计价指标。
	private String debugStep = null;
	private String hid_btn;

	public void initialize() {
		// tabPane.addTab("当前考核", getCurrCheckPlanWKPanel());
		this.add(getAllCheckPlanWKPanel());
		// this.add(tabPane);
	}

	private JPanel getAllCheckPlanWKPanel() {
		debugStep = getMenuConfMapValueAsStr("是否显示单步调试计算按钮");
		hid_btn = getMenuConfMapValueAsStr("隐藏按钮", "");
		List<WLTButton> list = new ArrayList<WLTButton>();// 显示的所有按钮
		planListPanel = new BillListPanel("SAL_TARGET_CHECK_LOG_CODE1");
		planListPanel.addBillListSelectListener(this);
		btn_createPlan = new WLTButton("新建考核", UIUtil.getImage("add.gif"));
		btn_createPlan.addActionListener(this);
		list.add(btn_createPlan);

		btn_delPlan = new WLTButton("删除考核", UIUtil.getImage("zt_031.gif"));
		btn_delPlan.addActionListener(this);
		list.add(btn_delPlan);

		int index = 1;// 按钮编号

		if (!hid_btn.contains("部门定量")) {
			btn_enddeptDL = new WLTButton(index + "、部门定量");
			btn_enddeptDL.addActionListener(this);
			list.add(btn_enddeptDL);
			index++;
		}
		if (!hid_btn.contains("部门定性")) {
			btn_enddeptDx = new WLTButton(index + "、部门定性");
			btn_enddeptDx.addActionListener(this);
			list.add(btn_enddeptDx);
			index++;
		}
		if (haveDeptJJTarget && !hid_btn.contains("部门计价")) {
			btn_endDeptJJ = new WLTButton(index + "、部门计价");
			btn_endDeptJJ.addActionListener(this);
			list.add(btn_endDeptJJ);
			index++;
		}
		if (!hid_btn.contains("员工评议")) {
			btn_calc_persondx = new WLTButton(index + "、员工评议");
			btn_calc_persondx.addActionListener(this);
			list.add(btn_calc_persondx);
			index++;
		}
		if ("Y".equals(containsPostDutyCheck) && !hid_btn.contains("岗责评议")) {
			btn_calc_postduty = new WLTButton(index + "、岗责评议");
			btn_calc_postduty.addActionListener(this);
			list.add(btn_calc_postduty);
			index++;
		}
		if ("Y".equals(debugStep)) {
			btn_calc_persondltarget = new WLTButton(index + "、员工定量");
			list.add(btn_calc_persondltarget);
			btn_calc_persondltarget.addActionListener(this);
			index++;

			btn_xymoney = new WLTButton(index + "、效益工资");
			btn_xymoney.addActionListener(this);
			list.add(btn_xymoney);
			index++;
			try {
				String[] f = UIUtil.getStringArrayFirstColByDS(null, "select distinct(calbatch) from sal_person_check_list where state='参与考核' and targettype='员工定量指标'");
				if (f.length > 1) {
					btn_persondl_levelcalc = new WLTButton(index + "、后续指标计算");
					btn_persondl_levelcalc.addActionListener(this);
					list.add(btn_persondl_levelcalc);
					index++;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (!hid_btn.contains("员工定量")) {
				btn_person_dl_auto = new WLTButton(index + "、员工定量");
				list.add(btn_person_dl_auto);
				btn_person_dl_auto.addActionListener(this);
				index++;
			}
		}
		if (calcQQ) {
			btn_QQ_money = new WLTButton(index + "、亲情工资");
		} else {
			btn_QQ_money = new WLTButton(index + "、综合计算");
		}
		list.add(btn_QQ_money);
		index++;

		btn_endPlan = new WLTButton(index + "、结束考核");
		list.add(btn_endPlan);
		index++;

		btn_qz = new WLTButton(index + "、强改为考核中");
		btn_qz.addActionListener(this);
		list.add(btn_qz);
		btn_end = new WLTButton(index + "、强制结束考核");
		btn_end.addActionListener(this);
		list.add(btn_end);

		btn_endPlan.addActionListener(this);
		btn_QQ_money.addActionListener(this);
		planListPanel.addBatchBillListButton(list.toArray(new WLTButton[0]));
		planListPanel.repaintBillListButton();
		filterBillList();
		return planListPanel;
	}

	public void filterBillList() {
		try {
			String corpname = UIUtil.getCommonService().getSysOptionStringValue("工资结算最小单位", "行社");
			String blcorpid = UIUtil.getLoginUserParentCorpItemValueByType(corpname, "分行", "id");
			planListPanel.setDataFilterCustCondition("createcorp=" + blcorpid);
			planListPanel.queryDataByCondition(" 1=1 ", "checkdate desc");
		} catch (Exception e) {
			e.printStackTrace();
			planListPanel.setDataFilterCustCondition("1=2");
		}
	}

	public void actionPerformed(ActionEvent e) {
		String state = ((WLTButton) e.getSource()).getText() + "计算完毕";
		BillVO planvo = planListPanel.getSelectedBillVO();
		if (e.getSource() == btn_endDeptJJ) {
			onEndDeptJJ(e);
		} else if (e.getSource() == btn_createPlan || e.getSource() == big_btn_createPlan) {
			autoCreatePlan();
		} else if (e.getSource() == btn_delPlan) {
			delPlan();
		} else if (e.getSource() == btn_endPlan) {
			if (planvo == null) {
				MessageBox.showSelectOne(planListPanel);
				return;
			}
			planvo = planListPanel.getSelectedBillVO();
			endPlan(planListPanel, planvo.convertToHashVO(), state);
		} else if (e.getSource() == big_btn_endPlan) {
			HashVO hvo = getCurrCheckHashVO();
			if (hvo == null) {
				MessageBox.show(mainPanel, "没有执行中的考核");
				return;
			}
			endPlan(mainPanel, hvo, state);
		} else if (e.getSource() == btn_enddeptDx) {
			if (planvo == null) {
				MessageBox.showSelectOne(planListPanel);
				return;
			}
			planvo = planListPanel.getSelectedBillVO();
			endPlanDx(planListPanel, planvo.convertToHashVO(), 0, state);
		} else if (e.getSource() == btn_calc_persondx) {
			if (planvo == null) {
				MessageBox.showSelectOne(planListPanel);
				return;
			}
			planvo = planListPanel.getSelectedBillVO();
			endPlanDx(planListPanel, planvo.convertToHashVO(), 1, state);
		} else if (e.getSource() == big_btn_calcDetpPlan) { // 大按钮1、计算部门得分。
			HashVO hvo = getCurrCheckHashVO();
			if (hvo == null) {
				MessageBox.show(mainPanel, "没有执行中的考核");
				return;
			}
			// if (!PLAN_STATUS_DOING.equals(hvo.getStringValue("status"))) {
			// MessageBox.show(mainPanel, "当前考核处于【" +
			// hvo.getStringValue("status") + "】状态.");
			// return;
			// }
			endDeptScore(mainPanel, hvo, state);
		} else if (e.getSource() == big_btn_calc_person) { // 大按钮2、计算人员得分
			try {
				HashVO hvo = getCurrCheckHashVO();
				if (hvo == null) {
					MessageBox.show(mainPanel, "没有执行中的考核");
					return;
				}
				// if (!PLAN_STATUS_DX_END.equals(hvo.getStringValue("status")))
				// {
				// MessageBox.show(mainPanel, "当前考核处于【" +
				// hvo.getStringValue("status") + "】状态.计算完定性指标后再执行此此操作.");
				// return;
				// }
				endPersonScore(mainPanel, hvo, state);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == btn_calc_persondltarget) { // 员工定量指标计算
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 0, state);
				MessageBox.show(planListPanel, "执行成功");
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		} else if (e.getSource() == btn_QQ_money) { // 亲情工资
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 2, state);
				MessageBox.show(planListPanel, "执行成功");
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		} else if (e.getSource() == btn_xymoney) { // 效益工资
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 1, state);
				MessageBox.show(planListPanel, "执行成功");
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		} else if (e.getSource() == big_btn_revise) { // 调整部门考核得分
			MessageBox.show(mainPanel, "可以弹出调整界面");
			return;
		} else if (e.getSource() == btn_enddeptDL) {
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 10, state); // 计算部门定量指标得分.
				MessageBox.show(planListPanel, "执行成功");
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		} else if (e.getSource() == btn_qz) {
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				UIUtil.executeUpdateByDS(null, "update SAL_TARGET_CHECK_LOG set status='考核中' where id = " + planvo.getStringValue("id"));
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}else if(e.getSource() == btn_end){
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				UIUtil.executeUpdateByDS(null, "update SAL_TARGET_CHECK_LOG set status='考核结束' where id = " + planvo.getStringValue("id"));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if (e.getSource() == btn_calc_postduty) {// 岗位职责评价
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcPostDutytarget(planListPanel, planvo.convertToHashVO(), state); // 掉
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		} else if (e.getSource() == btn_persondl_levelcalc) { //员工定量指标分批计算
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 12, state);
				MessageBox.show(planListPanel, "执行成功");
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		} else if (e.getSource() == btn_person_dl_auto) {
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 13, state);
				MessageBox.show(planListPanel, "执行成功");
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		}

		planListPanel.refreshCurrSelectedRow();
	}

	private void onEndDeptJJ(ActionEvent e) {
		String state = ((WLTButton) e.getSource()).getText() + "计算完毕";
		BillVO planvo = planListPanel.getSelectedBillVO();
		try {
			if (planvo == null) {
				MessageBox.showSelectOne(planListPanel);
				return;
			}
			calcDLtarget(planListPanel, planvo.convertToHashVO(), 11, state); // 计算部门定量指标得分.
			MessageBox.show(planListPanel, "执行成功");
			String afterDo = TBUtil.getTBUtil().getSysOptionStringValue("指标运行后UI事件", "");
			if (!TBUtil.isEmpty(afterDo)) {
				Class cls = Class.forName(afterDo);
				Object obj = cls.newInstance();
				if (obj instanceof AbstractAfterCalcUIEvent) {
					AbstractAfterCalcUIEvent afterEvent = (AbstractAfterCalcUIEvent) obj;
					afterEvent.afterEndDetpJJTarget(planListPanel, planvo.getStringValue("id"));
				} else {
					MessageBox.show(planListPanel, afterDo + "需要继承AbstractAfterCalcUIEvent类");
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			MessageBox.showException(planListPanel, e1);
		}
	}

	private static String PLAN_STATUS_DOING = "考核中";
	private static String PLAN_STATUS_DX_END = "定性指标计算完毕";
	private static String PLAN_STATUS_DL_END = "定量指标计算完毕";

	/*
	 * 得到当前执行中的计划。
	 */
	private HashVO getCurrCheckHashVO() {
		try {
			HashVO[] vo = UIUtil.getHashVoArrayByDS(null, "select *from sal_target_check_log where status!='考核结束'");
			if (vo.length > 0) {
				return vo[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void endPlan(final Container _parent, final HashVO vo, final String state) {
		if (vo.getStringValue("status", "").equals("考核结束")) {
			MessageBox.show(_parent, "考核已结束,无需执行此操作!");
			return;
		}
		if (!MessageBox.confirm(_parent, "您确定执行结束考核操作吗?")) {
			return;
		}
		new SplashWindow(_parent, "玩命计算并存储中请稍候...", new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
					HashMap map = ifc.endPlan(vo.getStringValue("id"), false, state);
					String res = map.get("res") + "";
					if ("fail".equals(res)) {
						DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_CODE1", "");
						dr.getBillQueryPanel().setVisible(false);
						dr.getBillCellPanel().loadBillCellData((BillCellVO) map.get("vo"));
						MessageBox.showTextArea(_parent, "操作失败,以下为未完成评分的人员名单", dr, 500, 600);
						return;
					} else {
						MessageBox.show(planListPanel, "操作成功!");
					}
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox.show(_parent, "发生异常,请与系统管理员联系!异常信息:" + e.getMessage());
				}
			}
		}, false);
	}

	/*
	 * 计算部门总分。先计算定量、定性、再计算总分
	 */
	private void endDeptScore(Container _parent, final HashVO planVO, final String state) {
		if (planVO == null) {
			MessageBox.showSelectOne(_parent);
			return;
		}
		new SplashWindow(_parent, "玩命计算并存储中请稍候...", new AbstractAction() {
			public void actionPerformed(final ActionEvent arg0) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) arg0.getSource();

						public void run() { // 需要用线程来控制是否已经把数据加载进来了。
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("系统正在努力计算中...");
							}
						}
					}, 20, 1000);

					SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
					// 先把部门定量指标放入进来
					ifc.calcDeptDLtarget(planVO, state, "部门定量指标");
					ifc.endPlanDL_Dept(planVO.getStringValue("id"));
					HashMap map = ifc.endCalcDeptDXScore(planVO.getStringValue("id"), false, state);
					String res = map.get("res") + "";
					if ("fail".equals(res)) {
						DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_CODE1", "");
						dr.getBillQueryPanel().setVisible(false);
						dr.getBillCellPanel().loadBillCellData((BillCellVO) map.get("vo"));
						MessageBox.showTextArea(planListPanel, "操作失败,以下为未完成部门定性指标评分的人员名单", dr, 500, 600);
						timer.cancel();
						return;
					} else {
						ifc.calcDeptTotleScoreIntoReviseTable(planVO.getStringValue("id"), state); // 把部门的定量定性得分最终结果放到调整表中。
						MessageBox.show(planListPanel, "操作成功!");
					}
					timer.cancel();
				} catch (Exception e) {
					timer.cancel();
					e.printStackTrace();
					MessageBox.show(planListPanel, "发生异常,请与系统管理员联系!异常信息:" + e.getMessage());
				}
			}
		}, false);
	}

	private void endPersonScore(Container _parent, final HashVO planVO, final String state) {
		if (planVO == null) {
			MessageBox.showSelectOne(_parent);
			return;
		}
		new SplashWindow(_parent, "玩命计算并存储中请稍候...", new AbstractAction() {
			public void actionPerformed(final ActionEvent arg0) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) arg0.getSource();

						public void run() { // 需要用线程来控制是否已经把数据加载进来了。
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("系统正在努力计算中...");
							}
						}
					}, 20, 1000);
					SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
					// 先把部门定量指标放入进来
					HashMap map = ifc.endCalcPersonDXScore(planVO.getStringValue("id"), false, state); // 计算员工定性
					String res = map.get("res") + "";
					if ("fail".equals(res)) {
						DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_CODE1", "");
						dr.getBillQueryPanel().setVisible(false);
						dr.getBillCellPanel().loadBillCellData((BillCellVO) map.get("vo"));
						MessageBox.showTextArea(planListPanel, "操作失败,以下为未完成员工定性指标评分的人员名单", dr, 500, 600);
						return;
					} else {
						calcDLtarget(mainPanel, planVO, 0, state); // 计算员工定量指标
						calcDLtarget(mainPanel, planVO, 3, state);//
						MessageBox.show(planListPanel, "操作成功!");
					}
					timer.cancel();
				} catch (Exception e) {
					timer.cancel();
					e.printStackTrace();
					MessageBox.show(planListPanel, "发生异常,请与系统管理员联系!异常信息:" + e.getMessage());
				}
			}
		}, false);
	}

	/**
	 * 计算定性指标 type == 0 部门定性 type == 1 员工定性
	 */
	private void endPlanDx(Container _parent, final HashVO vo, final int type, final String state) {
		if (vo == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		new SplashWindow(planListPanel, "玩命计算并存储中请稍候...", new AbstractAction() {
			public void actionPerformed(final ActionEvent arg0) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) arg0.getSource();

						public void run() { // 需要用线程来控制是否已经把数据加载进来了。
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule(vo.getStringValue("id"), "指标计算");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("系统正在努力计算中...");
							}
						}
					}, 20, 1000);
					SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
					HashMap map = null;
					if (type == 0) {
						map = ifc.endCalcDeptDXScore(vo.getStringValue("id"), false, state);
					} else {
						map = ifc.endCalcPersonDXScore(vo.getStringValue("id"), false, state);
					}
					String res = map.get("res") + "";
					if ("fail".equals(res)) {
						DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_CODE1", "");
						dr.getBillQueryPanel().setVisible(false);
						dr.getBillCellPanel().loadBillCellData((BillCellVO) map.get("vo"));
						MessageBox.showTextArea(planListPanel, "操作失败,以下为未完成" + (type == 0 ? "部门" : "员工") + "定性指标评分的人员名单", dr, 500, 600);
						timer.cancel();
						return;
					} else {
						if (type == 0) {
							getService().calcDeptTotleScoreIntoReviseTable(vo.getStringValue("id"), state);
						} else {
							getService().onePlanCalcAllUserEveryDXTargetScore(vo.getStringValue("id"));
						}
						MessageBox.show(planListPanel, "操作成功!");
					}
					timer.cancel();
				} catch (Exception e) {
					timer.cancel();
					e.printStackTrace();
					MessageBox.show(planListPanel, "发生异常,请与系统管理员联系!异常信息:" + e.getMessage());
				}
			}
		}, false);
	}

	private void delPlan() {
		BillVO vo = planListPanel.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}

		// 要加一些控制。。。todo...
		if (MessageBox.showConfirmDialog(this, "[重要提示]该操作将删除已选中月份的所有考核结果,您确认要删除吗?", "提醒", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			DeleteSQLBuilder dsb1 = new DeleteSQLBuilder("sal_target_check_log");
			dsb1.setWhereCondition("id=" + vo.getPkValue());
			DeleteSQLBuilder dsb2 = new DeleteSQLBuilder("sal_dept_check_score");
			dsb2.setWhereCondition("logid=" + vo.getPkValue());
			DeleteSQLBuilder dsb3 = new DeleteSQLBuilder("sal_person_check_score");
			dsb3.setWhereCondition("logid=" + vo.getPkValue());
			DeleteSQLBuilder dsb4 = new DeleteSQLBuilder("sal_target_check_result");//1312
			dsb4.setWhereCondition("logid=" + vo.getPkValue());
			DeleteSQLBuilder dsb5 = new DeleteSQLBuilder("sal_target_check_revise_result");
			dsb5.setWhereCondition("logid=" + vo.getPkValue());
			DeleteSQLBuilder dsb6 = new DeleteSQLBuilder("sal_person_check_result");
			dsb6.setWhereCondition("logid=" + vo.getPkValue());
			DeleteSQLBuilder dsb7 = new DeleteSQLBuilder("sal_quantifytargetdate");
			dsb7.setWhereCondition("checkdate=" + vo.getStringValue("checkdate", ""));
			DeleteSQLBuilder dsb8 = new DeleteSQLBuilder("sal_person_postduty_score");
			dsb8.setWhereCondition("logid=" + vo.getPkValue());
			DeleteSQLBuilder dsb9 = new DeleteSQLBuilder("sal_person_check_result");
			dsb9.setWhereCondition("logid=" + vo.getPkValue());
			//增加删除表项目 Gwang 2016-01-22
			DeleteSQLBuilder dsb10 = new DeleteSQLBuilder("sal_person_check_target_score");
			dsb10.setWhereCondition("logid=" + vo.getPkValue());
			
			List sqllist = new ArrayList();
			sqllist.add(dsb1);
			sqllist.add(dsb2);
			sqllist.add(dsb3);
			sqllist.add(dsb4);
			sqllist.add(dsb5);
			sqllist.add(dsb6);
			sqllist.add(dsb7);
			sqllist.add(dsb8);
			sqllist.add(dsb9);
			sqllist.add(dsb10);
			try {
				UIUtil.executeBatchByDS(null, sqllist);
				MessageBox.show(planListPanel, "删除成功!");
				planListPanel.removeSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.show(planListPanel, "发生异常,请与系统管理员联系!");
			}
		}

	}

	/*
	 * private void repairScoreTable() { BillVO vo =
	 * planListPanel.getSelectedBillVO(); if (vo == null) {
	 * MessageBox.showSelectOne(planListPanel); return; } if
	 * (MessageBox.showConfirmDialog(this,
	 * "该操作一般在对指标进行修改后需要对考评打分表进行修正,该操作对正常的指标及打分结果无影响,您确认操作码?", "提醒",
	 * JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) { } }
	 */
	/*
	 * 自动创建计划。把所有相关数据插入到sal_dept_check_score表汇总
	 */
	private void autoCreatePlan() {
		try {
			String[] ids = UIUtil.getStringArrayFirstColByDS(null, "select ID from sal_target_check_log where status != '考核结束'");
			if (ids != null && ids.length > 1) {
				MessageBox.show(this, "还有未结束的考核,不能进行此操作!");
				return;
			}
			if (MessageBox.showConfirmDialog(this, "系统将根据考核指标生成新的考核表, 是否继续?", "提醒", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				RefDialog_Month chooseMonth = new RefDialog_Month(this, "请选择要考核的月份", null, null);
				chooseMonth.initialize();
				chooseMonth.setVisible(true);
				if (chooseMonth.getCloseType() == chooseMonth.CONFIRM) {
					final RefItemVO rtnvo = chooseMonth.getReturnRefItemVO();
					if (rtnvo != null) {
						HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select * from sal_target_check_log where checkdate = '" + rtnvo.getId() + "'");
						if (vos != null && vos.length > 1) {
							MessageBox.show(this, "该月份的考核已存在,不能重复进行此操作!");
							return;
						} else {
							//zzl [2020-5-18] 加入指标类型区分网格还是其他
							BillCardDialog dialog = new BillCardDialog(this,"考核指标类型选择","SAL_TARGET_CHECK_LOG_CODE1",400,200);
							for(int i=0;i<dialog.getBillcardPanel().getTempletVO().getItemKeys().length;i++){
								if(dialog.getBillcardPanel().getTempletVO().getItemKeys()[i].equals("zbtype")){
									dialog.getBillcardPanel().setVisiable("zbtype",true);
									dialog.getBillcardPanel().setEditable("zbtype",true);
								}else{
									dialog.getBillcardPanel().setVisiable(dialog.getBillcardPanel().getTempletVO().getItemKeys()[i],false);
								}
							}
							dialog.getBtn_save().setVisible(false);
							dialog.setVisible(true);
							final String zbtype;
							if(dialog.getCloseType()==1){
								zbtype = dialog.getBillcardPanel().getRealValueAt("zbtype");
							}else{
								return;
							}
							new SplashWindow(this, "玩命计算中,请稍候...", new AbstractAction() {
								public void actionPerformed(ActionEvent e) {
									createScoreTable(rtnvo.getId(),zbtype);
								}
							}, false);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "发生未知异常,请与管理员联系!");
		}
	}

	private void createScoreTable(String month,String zbtype) {
		try {
			SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			HashMap map = ifc.checkViladate(month);
				String res = map.get("res") + "";
				if ("fail".equals(res)) {
					MessageBox.show(this, map.get("msg") + "");
					return;
				} else if ("error".equals(res)) {
					MessageBox.show(this, "发生异常请与管理员联系!");
					return;
				} else if ("success".equals(res)) {
					if (map.containsKey("msginfo")) {
						if (MessageBox.showConfirmDialog(this, map.get("msginfo") + "是否继续?", "提醒", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
							return;
						}
					}
					map.put("loginuserid", ClientEnvironment.getCurrLoginUserVO().getId());
					map.put("logindeptid", ClientEnvironment.getCurrLoginUserVO().getPKDept());
					map.put("zbtype",zbtype);
					ifc.createScoreTable(map);
					MessageBox.show(this, "考核表生成完毕!");
					planListPanel.refreshData();
				} else {
					MessageBox.show(this, "发生异常请与管理员联系!");
					return;
				}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	// 计算定量指标.
	private void calcDLtarget(Container _parent, final HashVO planVO, final int type, final String state) throws Exception {
		if (planVO == null) {
			MessageBox.showSelectOne(_parent);
			return;
		}
		final List<Exception> errList = new ArrayList();
		new SplashWindow(_parent, "系统正在努力计算中...", new AbstractAction() {
			public void actionPerformed(final ActionEvent actionevent) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) actionevent.getSource();

						public void run() { // 需要用线程来控制是否已经把数据加载进来了。
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("系统正在努力计算中...");
							}
						}
					}, 20, 1000);
					// 先把定量指标的excel值放入score中,执行部门定量指标计算，然后计算员工定量指标。效益工资，员工考核分数以及亲情工资。
					String updateState = ""; // 执行后状态
					if (type == 0) {
						getService().onCalcPersonDLTarget(planVO, null); // 员工定量指标。
						updateState = "定量指标计算完毕";
					} else if (type == 1) {
						getService().calc_P_Pay(planVO, null);
						updateState = "效益工资计算完毕";
					} else if (type == 2) {
//						getService().calcDelayPay(planVO.getStringValue("id"));
//						getService().calc_QQ_Pay(planVO);
						if (calcQQ) {
							updateState = "个人得分/亲情工资完毕";
						} else {
							updateState = "个人综合得分";
						}
					} else if (type == 3) { // 计算亲情工资和效益工资。
						getService().calc_P_Pay(planVO, null);
						getService().calc_QQ_Pay(planVO);
						updateState = "可以结束";
					} else if (type == 4) { // 仅计算部门得分-暂时废弃了。
						getService().calcDeptTotleScoreIntoReviseTable(planVO.getStringValue("id"), state);
					} else if (type == 10 || type == 11) { //
						String targetType = "部门定量指标";
						if (type == 11) {
							targetType = "部门计价指标";
						}
						getService().calcDeptDLtarget(planVO, state, targetType);
						SplashWindow w = (SplashWindow) actionevent.getSource();
						w.setWaitInfo("系统正在玩命计算" + targetType + "得分...");
						if (type == 10 && !getService().endPlanDL_Dept(planVO.getStringValue("id"))) {
							MessageBox.show((SplashWindow) actionevent.getSource(), targetType + "未完成,执行失败!");
							timer.cancel();
							return;
						}
						timer.cancel(); // 停掉
						return;// 直接返回
					} else if (type == 12) {
						String batch[] = UIUtil.getStringArrayFirstColByDS(null, "select distinct(calbatch) from sal_person_check_list targe where targettype='员工定量指标' and calbatch is not null and calbatch!='' order by calbatch");
						for (int i = 0; i < batch.length; i++) {
							getService().onCalcPersonDLTarget(planVO, batch[i]); // 员工定量指标。
							getService().calc_P_Pay(planVO, batch[i]);
						}
						updateState = "后续指标计算";
					} else if (type == 13) { //员工定量指标自动计算
						String batch[] = UIUtil.getStringArrayFirstColByDS(null, "select distinct(calbatch) from sal_person_check_list targe where targettype='员工定量指标'  order by calbatch");
						for (int i = 0; i < batch.length; i++) {
							getService().onCalcPersonDLTarget(planVO, batch[i]); // 员工定量指标。
							getService().calc_P_Pay(planVO, batch[i]);
						}
						updateState = "后续指标计算";
					}
					UIUtil.executeUpdateByDS(null, "update sal_target_check_log set status='" + state + "' where id = " + planVO.getStringValue("id"));
					timer.cancel();
				} catch (Exception e1) {
					timer.cancel();// 报错停止
					errList.add(e1);
					e1.printStackTrace();
					((SplashWindow) actionevent.getSource()).closeSplashWindow();
				}
			}
		}, false);
		if (errList.size() > 0) {
			throw errList.get(0);
		}
	}

	private SalaryServiceIfc getService() {
		if (service == null) {
			try {
				service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return service;
	}

	// 计算岗位职责指标指标.
	private void calcPostDutytarget(Container _parent, final HashVO planVO, final String state) throws Exception {
		if (planVO == null) {
			MessageBox.showSelectOne(_parent);
			return;
		}
		final List<Exception> errList = new ArrayList();
		new SplashWindow(_parent, "系统正在努力计算中...", new AbstractAction() {
			public void actionPerformed(final ActionEvent actionevent) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) actionevent.getSource();

						public void run() { // 需要用线程来控制是否已经把数据加载进来了。
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("系统正在努力计算中...");
							}
						}
					}, 20, 1000);
					HashMap map = getService().calcPostDutyTargetScore(planVO, state);
					String res = map.get("res") + "";
					if ("fail".equals(res)) {
						DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_CODE1", "");
						dr.getBillQueryPanel().setVisible(false);
						dr.getBillCellPanel().loadBillCellData((BillCellVO) map.get("vo"));
						MessageBox.showTextArea(planListPanel, "操作失败,以下为未完成评分的人员名单", dr, 500, 600);
					} else {
						MessageBox.show(planListPanel, "操作成功!");
					}
					timer.cancel();
				} catch (Exception e1) {
					timer.cancel();// 报错停止
					errList.add(e1);
					e1.printStackTrace();
					((SplashWindow) actionevent.getSource()).closeSplashWindow();
				}
			}
		}, false);
		if (errList.size() > 0) {
			throw errList.get(0);
		}
	}

	private WLTButton big_btn_createPlan, big_btn_calcDetpPlan, big_btn_endPlan, big_btn_calc_person, big_btn_revise;
	private WLTTextArea currDescrTextArea;
	private WLTPanel mainPanel = null;

	/*
	 * 得到当前考核的计划
	 */
	private JPanel getCurrCheckPlanWKPanel() {
		mainPanel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new BorderLayout());
		WLTPanel btnPanel = new WLTPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
		btnPanel.setOpaque(false);
		big_btn_createPlan = new WLTButton("1、新建考核");
		big_btn_calcDetpPlan = new WLTButton("2、部门得分计算");
		big_btn_revise = new WLTButton("3、修订部门得分");
		big_btn_calc_person = new WLTButton("4、员工得分计算");
		big_btn_endPlan = new WLTButton("5、结束考核");

		big_btn_createPlan.setFont(new Font("宋体", Font.PLAIN, 14));
		big_btn_calcDetpPlan.setFont(new Font("宋体", Font.PLAIN, 14));
		big_btn_calc_person.setFont(new Font("宋体", Font.PLAIN, 14));
		big_btn_endPlan.setFont(new Font("宋体", Font.PLAIN, 14));
		big_btn_revise.setFont(new Font("宋体", Font.PLAIN, 14));

		big_btn_createPlan.setPreferredSize(new Dimension(110, 75));
		big_btn_calcDetpPlan.setPreferredSize(new Dimension(110, 75));
		big_btn_calc_person.setPreferredSize(new Dimension(110, 75));
		big_btn_endPlan.setPreferredSize(new Dimension(110, 75));
		big_btn_revise.setPreferredSize(new Dimension(110, 75));

		big_btn_createPlan.setBackground(TBUtil.getTBUtil().getColor("5F9EA0"));
		big_btn_calcDetpPlan.setBackground(TBUtil.getTBUtil().getColor("5F9EA0"));
		big_btn_calc_person.setBackground(TBUtil.getTBUtil().getColor("5F9EA0"));
		big_btn_endPlan.setBackground(TBUtil.getTBUtil().getColor("5F9EA0"));
		big_btn_revise.setBackground(TBUtil.getTBUtil().getColor("5F9EA0"));

		big_btn_createPlan.addActionListener(this);
		big_btn_calcDetpPlan.addActionListener(this);
		big_btn_calc_person.addActionListener(this);
		big_btn_endPlan.addActionListener(this);
		big_btn_revise.addActionListener(this);

		btnPanel.add(big_btn_createPlan);
		btnPanel.add(new JLabel(UIUtil.getImage("zt_050.gif")));
		btnPanel.add(big_btn_calcDetpPlan);
		btnPanel.add(new JLabel(UIUtil.getImage("zt_050.gif")));
		btnPanel.add(big_btn_revise);
		btnPanel.add(new JLabel(UIUtil.getImage("zt_050.gif")));
		btnPanel.add(big_btn_calc_person);
		btnPanel.add(new JLabel(UIUtil.getImage("zt_050.gif")));
		btnPanel.add(big_btn_endPlan);
		mainPanel.add(btnPanel, BorderLayout.NORTH);
		currDescrTextArea = new WLTTextArea();
		currDescrTextArea.setEditable(false);
		JScrollPane scrollPanel = new JScrollPane(currDescrTextArea);
		scrollPanel.setOpaque(false);
		scrollPanel.setPreferredSize(new Dimension(550, 330));

		WLTPanel textPanel = new WLTPanel(new FlowLayout(FlowLayout.CENTER));
		textPanel.setOpaque(false);
		textPanel.add(scrollPanel);
		mainPanel.add(textPanel, BorderLayout.CENTER);
		this.add(mainPanel);
		return mainPanel;
	}

	/**
	 * zzl[2020-6-12] 加入网格工资单所有需要控制按钮。
	 * @param _event
	 */
	@Override
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if(_event.getSource()==planListPanel){
			String ZBTYPE=planListPanel.getSelectedBillVO().getStringValue("ZBTYPE");
			if(ZBTYPE.equals("网格")){
				btn_enddeptDL.setEnabled(false);
				btn_enddeptDx.setEnabled(false);
				btn_endDeptJJ.setEnabled(false);
				btn_calc_persondx.setEnabled(false);
				btn_calc_postduty.setEnabled(false);
			}else{
				btn_enddeptDL.setEnabled(true);
				btn_enddeptDx.setEnabled(true);
				btn_endDeptJJ.setEnabled(true);
				btn_calc_persondx.setEnabled(true);
				btn_calc_postduty.setEnabled(true);
			}
		}
	}
}
