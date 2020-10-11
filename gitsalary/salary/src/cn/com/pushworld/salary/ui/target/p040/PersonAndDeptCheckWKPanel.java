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
 * Ա����������������������档
 * @author haoming
 * create by 2013-7-30
 */
public class PersonAndDeptCheckWKPanel extends AbstractWorkPanel {
	private static final long serialVersionUID = 4087096134754413041L;
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // ��ǰ��¼��ID
	private SalaryBomPanel planBomPanel = new SalaryBomPanel();
	private HashVO currLog = null; // ��ǰ�ƻ���־ID��

	HashVO dept_vos[];
	HashVO person_vos[];
	HashVO postduty_vos[];
	private boolean haveDeptCheck = false; //��ʶ�Ƿ��в������֣�����в������֣�ҳǩ����һ����

	private boolean editable = true; //�Ƿ�ɱ༭��

	private String inputCheckType = null;

	private boolean needDeptScore = true;

	private PersonMutualWKPanel personWKPanel;
	private PostDutyMutualWKPanel postdutyWKpanel; //��λְ�����۽���

	@Override
	public void initialize() {
		this.add(getFirstMainPanel());
	}

	/*
	 * �Զ�����á�
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
				dept_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(t1.id),t1.checkdate,t1.status from SAL_TARGET_CHECK_LOG t1,sal_dept_check_score t2 where t1.id = t2.logid and t1.status!='���˽���' and t2.scoreuser='" + userid + "' and t2.targettype='���Ŷ���ָ��'");
				person_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(t1.id),t1.checkdate,t1.status,t1.name from SAL_TARGET_CHECK_LOG t1,sal_person_check_score t2 where t1.id = t2.logid and t1.status!='���˽���' and t2.scoreuser='" + userid + "' and t2.targettype!='Ա������ָ��'");
				postduty_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(t1.id),t1.checkdate,t1.status,t1.name from SAL_TARGET_CHECK_LOG t1,sal_person_postduty_score  t2 where t1.id = t2.logid and t1.status!='���˽���' and t2.scoreuser='" + userid + "' and t2.targettype ='��������ָ��'");
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
				if (planvos.length == 0) {//���û��ִ���еļƻ�
					//�����һ���µġ�
					planvos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_TARGET_CHECK_LOG where 1=1 order by checkdate desc");
					if (planvos.length > 0) {
						planvos = new HashVO[] { planvos[0] };
						dept_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_dept_check_score where scoreuser='" + userid + "' and targettype='���Ŷ���ָ��' and logid =" + planvos[0].getStringValue("id"));
						person_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_person_check_score where scoreuser='" + userid + "' and targettype!='Ա������ָ��' and logid=" + planvos[0].getStringValue("id"));
						postduty_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_person_postduty_score  where scoreuser='" + userid + "' and targettype ='��������ָ��' and logid=" + planvos[0].getStringValue("id"));
					}
				}
			} else {
				planvos = new HashVO[] { currLog };
				dept_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_dept_check_score where scoreuser='" + userid + "' and targettype='���Ŷ���ָ��' and logid =" + planvos[0].getStringValue("id"));
				person_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_person_check_score where scoreuser='" + userid + "' and targettype!='Ա������ָ��' and logid=" + planvos[0].getStringValue("id"));
				postduty_vos = UIUtil.getHashVoArrayByDS(null, "select distinct(logid) id from sal_person_postduty_score  where scoreuser='" + userid + "' and targettype ='��������ָ��' and logid=" + planvos[0].getStringValue("id"));
			}
			TBUtil.getTBUtil().sortHashVOs(planvos, new String[][] { { "checkdate", "N", "N" } });
			if (planvos.length == 0 || (dept_vos.length == 0 && person_vos.length == 0)) {
				JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
				WLTLabel label = null;
				if (planvos.length != 0) {
					label = new WLTLabel("û���ҵ��ɴ�ֵ�����...");
				} else {
					label = new WLTLabel("Ŀǰȫ��û�п����еļƻ�...");
				}
				label.setFont(new Font("����", Font.BOLD, 16));
				panel.add(label);
				return panel;
			}
			if (planvos.length > 1) { // ����ж����bom��ʾ��
				return getPlanBomPanel(planvos);
			} else {// ������о�һ��ִ����
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
			String markStyle = TBUtil.getTBUtil().getSysOptionStringValue("���������ֽ���ģʽ", "���"); //ֵ����ϣ���ҳǩ
			if ("���".equals(markStyle)) {
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
			tabpane.addTab("����", UIUtil.getImage("180.png"), deptWKpanel);
		}
	}

	/*
	 * ����ѡ��ļƻ�����ȡ������塣
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
	 * ����Ƕ���ƻ���ִ���У���ô����һ��Bom����������ѡ��
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
				risk.setInfoalert("δ���");
			}
			bom.setRiskVO("��������", risk);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		super.beforeDispose();
	}

}
