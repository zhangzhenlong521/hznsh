package cn.com.pushworld.salary.ui.target.p040;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.person.p020.PersonMutualWKPanel;
import cn.com.pushworld.salary.ui.person.p021.PostDutyMutualWKPanel;
import cn.com.pushworld.salary.ui.person.p021.PostDutyMutualWKPanel_MultiTab;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomClickEvent;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomClickListener;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomPanel;

/**
 * 员工互评，部门评分整体界面。
 * @author haoming
 * create by 2013-7-30
 */
public class PersonAndDeptCheckWKPanel extends AbstractWorkPanel {
	private static final long serialVersionUID = 4087096134754413041L;
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // 当前登录人ID
	private SalaryBomPanel planBomPanel = new SalaryBomPanel();
	private HashVO currLog = null; // 当前计划日志ID。

	HashVO dept_vos[];
	HashVO person_vos[];
	HashVO postduty_vos[];
	private boolean haveDeptCheck = false; //标识是否有部门评分，如果有部门评分，页签会多出一个。

	private boolean editable = true; //是否可编辑。

	private String inputCheckType = null;

	private boolean needDeptScore = true;

	private PersonMutualWKPanel personWKPanel;
	private PostDutyMutualWKPanel postdutyWKpanel; //岗位职责评价界面

	@Override
	public void initialize() {
		this.add(getFirstMainPanel());
	}

	/*
	 * 自定义调用。
	 */
	public void custinit(boolean _edit, String _userid, HashVO _log) {
		userid = _userid;
		currLog = _log;
		editable = _edit;
		this.add(getFirstMainPanel());
	}

	public void custinit(boolean _edit, String _userid, HashVO _log, String _checktype, boolean _needDeptScore) {
		userid = _userid;
		currLog = _log;
		editable = _edit;
		inputCheckType = _checktype;
		needDeptScore = _needDeptScore;
		this.add(getFirstMainPanel());
	}

	private JComponent getFirstMainPanel() {
		HashVO planvos[] = null;
		try {
			if (currLog == null) {
				dept_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(t1.id),t1.checkdate,t1.status from SAL_TARGET_CHECK_LOG t1,sal_dept_check_score t2 where t1.id = t2.logid and t1.status!='考核结束' and t2.scoreuser='" + userid + "' and t2.targettype='部门定性指标'");
				person_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(t1.id),t1.checkdate,t1.status,t1.name from SAL_TARGET_CHECK_LOG t1,sal_person_check_score t2 where t1.id = t2.logid and t1.status!='考核结束' and t2.scoreuser='" + userid + "' and t2.targettype!='员工定量指标'");
				postduty_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(t1.id),t1.checkdate,t1.status,t1.name from SAL_TARGET_CHECK_LOG t1,sal_person_postduty_score  t2 where t1.id = t2.logid and t1.status!='考核结束' and t2.scoreuser='" + userid + "' and t2.targettype ='岗责评价指标'");
				HashMap<String, HashVO> commMap = new HashMap<String, HashVO>();
				for (int i = 0; i < dept_vos.length; i++) {
					commMap.put(dept_vos[i].getStringValue("id"), dept_vos[i]);
				}
				for (int i = 0; i < person_vos.length; i++) {
					commMap.put(person_vos[i].getStringValue("id"), person_vos[i]);
				}

				for (int i = 0; i < postduty_vos.length; i++) {
					commMap.put(postduty_vos[i].getStringValue("id"), postduty_vos[i]);
				}
				planvos = new HashVO[commMap.size()];
				int index = 0;
				for (Iterator iterator = commMap.entrySet().iterator(); iterator.hasNext();) {
					Entry type = (Entry) iterator.next();
					HashVO vo = (HashVO) type.getValue();
					planvos[index] = vo;
					index++;
				}
				if (planvos.length == 0) {//如果没有执行中的计划
					//查最近一个月的。
					planvos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_TARGET_CHECK_LOG where 1=1 order by checkdate desc");
					if (planvos.length > 0) {
						planvos = new HashVO[] { planvos[0] };
						dept_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_dept_check_score where scoreuser='" + userid + "' and targettype='部门定性指标' and logid =" + planvos[0].getStringValue("id"));
						person_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_person_check_score where scoreuser='" + userid + "' and targettype!='员工定量指标' and logid=" + planvos[0].getStringValue("id"));
						postduty_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_person_postduty_score  where scoreuser='" + userid + "' and targettype ='岗责评价指标' and logid=" + planvos[0].getStringValue("id"));
					}
				}
			} else {
				planvos = new HashVO[] { currLog };
				dept_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_dept_check_score where scoreuser='" + userid + "' and targettype='部门定性指标' and logid =" + planvos[0].getStringValue("id"));
				person_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_person_check_score where scoreuser='" + userid + "' and targettype!='员工定量指标' and logid=" + planvos[0].getStringValue("id"));
				postduty_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_person_postduty_score  where scoreuser='" + userid + "' and targettype ='岗责评价指标' and logid=" + planvos[0].getStringValue("id"));
			}
			TBUtil.getTBUtil().sortHashVOs(planvos, new String[][] { { "checkdate", "N", "N" } });
			if (planvos.length == 0 || (dept_vos.length == 0 && person_vos.length == 0)) {
				JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
				WLTLabel label = null;
				if (planvos.length != 0) {
					label = new WLTLabel("没有找到可打分的内容...");
				} else {
					label = new WLTLabel("目前全行没有考评中的计划...");
				}
				label.setFont(new Font("宋体", Font.BOLD, 16));
				panel.add(label);
				return panel;
			}
			if (planvos.length > 1) { // 如果有多个用bom显示。
				return getPlanBomPanel(planvos);
			} else {// 如果现有就一个执行中
				currLog = planvos[0];
				return loadTabPanel();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new JPanel();
	}

	WLTTabbedPane tabpane = new WLTTabbedPane(new Color(30, 172, 205), Color.DARK_GRAY);//

	private JComponent loadTabPanel() {
		if (needDeptScore) {
			loadDeptCheckByLogPlanVO();
		}
		loadPostDutyCheckByLogPanelVO();
		loadPersonCheckByLogPlanVO();
		return tabpane;
	}

	private void loadPostDutyCheckByLogPanelVO() {
		boolean flag = false;
		for (int i = 0; i < postduty_vos.length; i++) {
			if (postduty_vos[i].getStringValue("id").equals(currLog.getStringValue("id"))) {
				flag = true;
				break;
			}
		}
		if (flag) {
			String markStyle = TBUtil.getTBUtil().getSysOptionStringValue("岗责评议打分界面模式", "混合"); //值：混合；多页签
			if ("混合".equals(markStyle)) {
				postdutyWKpanel = new PostDutyMutualWKPanel();
				postdutyWKpanel.customInit(tabpane, currLog, userid, editable);
			} else {
				PostDutyMutualWKPanel_MultiTab postdutyWKpanel = new PostDutyMutualWKPanel_MultiTab();
				postdutyWKpanel.customInit(tabpane, currLog, userid, editable);
			}
		}
	}

	private void loadDeptCheckByLogPlanVO() {
		haveDeptCheck = false;
		for (int i = 0; i < dept_vos.length; i++) {
			if (dept_vos[i].getStringValue("id").equals(currLog.getStringValue("id"))) {
				haveDeptCheck = true;
				break;
			}
		}
		if (haveDeptCheck && currLog != null) {
			DeptTargetScoredWKPanel deptWKpanel = new DeptTargetScoredWKPanel();
			deptWKpanel.setLayout(new BorderLayout());
			deptWKpanel.customInit(userid, editable);
			deptWKpanel.refreshListDataByLogVO(currLog);
			tabpane.addTab("部门", UIUtil.getImage("180.png"), deptWKpanel);
		}
	}

	/*
	 * 根据选择的计划，获取评分面板。
	 */
	private void loadPersonCheckByLogPlanVO() {
		boolean flag = false;
		for (int i = 0; i < person_vos.length; i++) {
			if (person_vos[i].getStringValue("id").equals(currLog.getStringValue("id"))) {
				flag = true;
				break;
			}
		}
		if (flag) {
			personWKPanel = new PersonMutualWKPanel();
			personWKPanel.customInit(tabpane, currLog, userid, inputCheckType, editable);
		}
	}

	public PersonMutualWKPanel getPersonMutualWKPanel() {
		return personWKPanel;
	}

	/*
	 * 
	 */
	private void loadPostDutyWKPanel() {

	}

	/*
	 * 如果是多个计划在执行中，那么给出一个Bom面板进行日期选择
	 */
	private SalaryBomPanel getPlanBomPanel(HashVO[] _planLogs) {
		for (int i = 0; i < _planLogs.length; i++) {
			_planLogs[i].setToStringFieldName("checkdate");
		}

		planBomPanel.addBomPanel(Arrays.asList(_planLogs));
		planBomPanel.addBomClickListener(new SalaryBomClickListener() {
			public void onBomClickListener(SalaryBomClickEvent event) {
				currLog = event.getHashvo();
				planBomPanel.addBomPanel(loadTabPanel());
			}
		});
		return planBomPanel;
	}

	@Override
	public void beforeDispose() {
		if (DeskTopPanel.getDeskTopPanel().getNewDeskTopTabPane() == null) {
			return;
		}
		int tabCount = DeskTopPanel.getDeskTopPanel().getNewDeskTopTabPane().getTabCount();
		JComponent po = DeskTopPanel.getDeskTopPanel().getNewDeskTopTabPane().getComponentAt(tabCount - 1);
		BillBomPanel bom = null;
		if (po instanceof JPanel) {
			JComponent com = (JComponent) po.getComponent(0);
			if (com != null && com instanceof BillBomPanel) {
				bom = (BillBomPanel) com;
			}
		} else if (po instanceof BillBomPanel) {
			bom = (BillBomPanel) po;
		}
		if (bom == null) {
			return;
		}
		try {
			RiskVO risk = new RiskVO();
			int count = new SalaryTBUtil().getLoginUserUnCheckedScore();
			if (count > 0) {
				risk.setInfoalert("未完成");
			}
			bom.setRiskVO("考核评分", risk);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		super.beforeDispose();
	}

}
