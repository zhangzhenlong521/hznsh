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
 * ȫ�м�Ч���˰��š��������������
 * 
 * @author haoming create by 2013-7-9
 */
public class TargetCheckPlanWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener {
	private static final long serialVersionUID = 1803665179022243678L;
	private BillListPanel planListPanel;
	private WLTButton btn_endDeptJJ, btn_enddeptDL, btn_createPlan, btn_delPlan, btn_enddeptDx, btn_endPlan, btn_calc_persondx, btn_calc_persondltarget, btn_QQ_money, btn_xymoney, btn_persondl_levelcalc, btn_qz,btn_end, btn_calc_postduty, btn_person_dl_auto;
	private SalaryServiceIfc service;
	private WLTTabbedPane tabPane = new WLTTabbedPane();
	private String containsPostDutyCheck = TBUtil.getTBUtil().getSysOptionStringValue("�Ƿ������λְ�����۹���", "N"); // ��������ĸ���ָ�����ۡ�
	private boolean calcQQ = TBUtil.getTBUtil().getSysOptionBooleanValue("�Ƿ�������鹤��", true); // Ĭ�ϼ������鹤�ʡ�������������鹤�ʣ��ܷ�����������Ҫ��ġ�
	private boolean haveDeptJJTarget = TBUtil.getTBUtil().getSysOptionBooleanValue("�Ƿ���ʾ���żƼ�ָ��", false); //�����������֧�мƼ�ָ�ꡣ
	private String debugStep = null;
	private String hid_btn;

	public void initialize() {
		// tabPane.addTab("��ǰ����", getCurrCheckPlanWKPanel());
		this.add(getAllCheckPlanWKPanel());
		// this.add(tabPane);
	}

	private JPanel getAllCheckPlanWKPanel() {
		debugStep = getMenuConfMapValueAsStr("�Ƿ���ʾ�������Լ��㰴ť");
		;
		hid_btn = getMenuConfMapValueAsStr("���ذ�ť", "");
		List<WLTButton> list = new ArrayList<WLTButton>();// ��ʾ�����а�ť
		planListPanel = new BillListPanel("SAL_TARGET_CHECK_LOG_CODE1");
		planListPanel.addBillListSelectListener(this);
		btn_createPlan = new WLTButton("�½�����", UIUtil.getImage("add.gif"));
		btn_createPlan.addActionListener(this);
		list.add(btn_createPlan);

		btn_delPlan = new WLTButton("ɾ������", UIUtil.getImage("zt_031.gif"));
		btn_delPlan.addActionListener(this);
		list.add(btn_delPlan);

		int index = 1;// ��ť���

		if (!hid_btn.contains("���Ŷ���")) {
			btn_enddeptDL = new WLTButton(index + "�����Ŷ���");
			btn_enddeptDL.addActionListener(this);
			list.add(btn_enddeptDL);
			index++;
		}
		if (!hid_btn.contains("���Ŷ���")) {
			btn_enddeptDx = new WLTButton(index + "�����Ŷ���");
			btn_enddeptDx.addActionListener(this);
			list.add(btn_enddeptDx);
			index++;
		}
		if (haveDeptJJTarget && !hid_btn.contains("���żƼ�")) {
			btn_endDeptJJ = new WLTButton(index + "�����żƼ�");
			btn_endDeptJJ.addActionListener(this);
			list.add(btn_endDeptJJ);
			index++;
		}
		if (!hid_btn.contains("Ա������")) {
			btn_calc_persondx = new WLTButton(index + "��Ա������");
			btn_calc_persondx.addActionListener(this);
			list.add(btn_calc_persondx);
			index++;
		}
		if ("Y".equals(containsPostDutyCheck) && !hid_btn.contains("��������")) {
			btn_calc_postduty = new WLTButton(index + "����������");
			btn_calc_postduty.addActionListener(this);
			list.add(btn_calc_postduty);
			index++;
		}
		if ("Y".equals(debugStep)) {
			btn_calc_persondltarget = new WLTButton(index + "��Ա������");
			list.add(btn_calc_persondltarget);
			btn_calc_persondltarget.addActionListener(this);
			index++;

			btn_xymoney = new WLTButton(index + "��Ч�湤��");
			btn_xymoney.addActionListener(this);
			list.add(btn_xymoney);
			index++;
			try {
				String[] f = UIUtil.getStringArrayFirstColByDS(null, "select distinct(calbatch) from sal_person_check_list where state='���뿼��' and targettype='Ա������ָ��'");
				if (f.length > 1) {
					btn_persondl_levelcalc = new WLTButton(index + "������ָ�����");
					btn_persondl_levelcalc.addActionListener(this);
					list.add(btn_persondl_levelcalc);
					index++;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (!hid_btn.contains("Ա������")) {
				btn_person_dl_auto = new WLTButton(index + "��Ա������");
				list.add(btn_person_dl_auto);
				btn_person_dl_auto.addActionListener(this);
				index++;
			}
		}
		if (calcQQ) {
			btn_QQ_money = new WLTButton(index + "�����鹤��");
		} else {
			btn_QQ_money = new WLTButton(index + "���ۺϼ���");
		}
		list.add(btn_QQ_money);
		index++;

		btn_endPlan = new WLTButton(index + "����������");
		list.add(btn_endPlan);
		index++;

		btn_qz = new WLTButton(index + "��ǿ��Ϊ������");
		btn_qz.addActionListener(this);
		list.add(btn_qz);
		btn_end = new WLTButton(index + "��ǿ�ƽ�������");
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
			String corpname = UIUtil.getCommonService().getSysOptionStringValue("���ʽ�����С��λ", "����");
			String blcorpid = UIUtil.getLoginUserParentCorpItemValueByType(corpname, "����", "id");
			planListPanel.setDataFilterCustCondition("createcorp=" + blcorpid);
			planListPanel.queryDataByCondition(" 1=1 ", "checkdate desc");
		} catch (Exception e) {
			e.printStackTrace();
			planListPanel.setDataFilterCustCondition("1=2");
		}
	}

	public void actionPerformed(ActionEvent e) {
		String state = ((WLTButton) e.getSource()).getText() + "�������";
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
				MessageBox.show(mainPanel, "û��ִ���еĿ���");
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
		} else if (e.getSource() == big_btn_calcDetpPlan) { // ��ť1�����㲿�ŵ÷֡�
			HashVO hvo = getCurrCheckHashVO();
			if (hvo == null) {
				MessageBox.show(mainPanel, "û��ִ���еĿ���");
				return;
			}
			// if (!PLAN_STATUS_DOING.equals(hvo.getStringValue("status"))) {
			// MessageBox.show(mainPanel, "��ǰ���˴��ڡ�" +
			// hvo.getStringValue("status") + "��״̬.");
			// return;
			// }
			endDeptScore(mainPanel, hvo, state);
		} else if (e.getSource() == big_btn_calc_person) { // ��ť2��������Ա�÷�
			try {
				HashVO hvo = getCurrCheckHashVO();
				if (hvo == null) {
					MessageBox.show(mainPanel, "û��ִ���еĿ���");
					return;
				}
				// if (!PLAN_STATUS_DX_END.equals(hvo.getStringValue("status")))
				// {
				// MessageBox.show(mainPanel, "��ǰ���˴��ڡ�" +
				// hvo.getStringValue("status") + "��״̬.�����궨��ָ�����ִ�д˴˲���.");
				// return;
				// }
				endPersonScore(mainPanel, hvo, state);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == btn_calc_persondltarget) { // Ա������ָ�����
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 0, state);
				MessageBox.show(planListPanel, "ִ�гɹ�");
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		} else if (e.getSource() == btn_QQ_money) { // ���鹤��
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 2, state);
				MessageBox.show(planListPanel, "ִ�гɹ�");
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		} else if (e.getSource() == btn_xymoney) { // Ч�湤��
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 1, state);
				MessageBox.show(planListPanel, "ִ�гɹ�");
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		} else if (e.getSource() == big_btn_revise) { // �������ſ��˵÷�
			MessageBox.show(mainPanel, "���Ե�����������");
			return;
		} else if (e.getSource() == btn_enddeptDL) {
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 10, state); // ���㲿�Ŷ���ָ��÷�.
				MessageBox.show(planListPanel, "ִ�гɹ�");
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
				UIUtil.executeUpdateByDS(null, "update SAL_TARGET_CHECK_LOG set status='������' where id = " + planvo.getStringValue("id"));
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}else if(e.getSource() == btn_end){
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				UIUtil.executeUpdateByDS(null, "update SAL_TARGET_CHECK_LOG set status='���˽���' where id = " + planvo.getStringValue("id"));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if (e.getSource() == btn_calc_postduty) {// ��λְ������
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcPostDutytarget(planListPanel, planvo.convertToHashVO(), state); // ��
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		} else if (e.getSource() == btn_persondl_levelcalc) { //Ա������ָ���������
			try {
				if (planvo == null) {
					MessageBox.showSelectOne(planListPanel);
					return;
				}
				calcDLtarget(planListPanel, planvo.convertToHashVO(), 12, state);
				MessageBox.show(planListPanel, "ִ�гɹ�");
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
				MessageBox.show(planListPanel, "ִ�гɹ�");
			} catch (Exception e1) {
				e1.printStackTrace();
				MessageBox.showException(planListPanel, e1);
			}
		}

		planListPanel.refreshCurrSelectedRow();
	}

	private void onEndDeptJJ(ActionEvent e) {
		String state = ((WLTButton) e.getSource()).getText() + "�������";
		BillVO planvo = planListPanel.getSelectedBillVO();
		try {
			if (planvo == null) {
				MessageBox.showSelectOne(planListPanel);
				return;
			}
			calcDLtarget(planListPanel, planvo.convertToHashVO(), 11, state); // ���㲿�Ŷ���ָ��÷�.
			MessageBox.show(planListPanel, "ִ�гɹ�");
			String afterDo = TBUtil.getTBUtil().getSysOptionStringValue("ָ�����к�UI�¼�", "");
			if (!TBUtil.isEmpty(afterDo)) {
				Class cls = Class.forName(afterDo);
				Object obj = cls.newInstance();
				if (obj instanceof AbstractAfterCalcUIEvent) {
					AbstractAfterCalcUIEvent afterEvent = (AbstractAfterCalcUIEvent) obj;
					afterEvent.afterEndDetpJJTarget(planListPanel, planvo.getStringValue("id"));
				} else {
					MessageBox.show(planListPanel, afterDo + "��Ҫ�̳�AbstractAfterCalcUIEvent��");
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			MessageBox.showException(planListPanel, e1);
		}
	}

	private static String PLAN_STATUS_DOING = "������";
	private static String PLAN_STATUS_DX_END = "����ָ��������";
	private static String PLAN_STATUS_DL_END = "����ָ��������";

	/*
	 * �õ���ǰִ���еļƻ���
	 */
	private HashVO getCurrCheckHashVO() {
		try {
			HashVO[] vo = UIUtil.getHashVoArrayByDS(null, "select *from sal_target_check_log where status!='���˽���'");
			if (vo.length > 0) {
				return vo[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void endPlan(final Container _parent, final HashVO vo, final String state) {
		if (vo.getStringValue("status", "").equals("���˽���")) {
			MessageBox.show(_parent, "�����ѽ���,����ִ�д˲���!");
			return;
		}
		if (!MessageBox.confirm(_parent, "��ȷ��ִ�н������˲�����?")) {
			return;
		}
		new SplashWindow(_parent, "�������㲢�洢�����Ժ�...", new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
					HashMap map = ifc.endPlan(vo.getStringValue("id"), false, state);
					String res = map.get("res") + "";
					if ("fail".equals(res)) {
						DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_CODE1", "");
						dr.getBillQueryPanel().setVisible(false);
						dr.getBillCellPanel().loadBillCellData((BillCellVO) map.get("vo"));
						MessageBox.showTextArea(_parent, "����ʧ��,����Ϊδ������ֵ���Ա����", dr, 500, 600);
						return;
					} else {
						MessageBox.show(planListPanel, "�����ɹ�!");
					}
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox.show(_parent, "�����쳣,����ϵͳ����Ա��ϵ!�쳣��Ϣ:" + e.getMessage());
				}
			}
		}, false);
	}

	/*
	 * ���㲿���ܷ֡��ȼ��㶨�������ԡ��ټ����ܷ�
	 */
	private void endDeptScore(Container _parent, final HashVO planVO, final String state) {
		if (planVO == null) {
			MessageBox.showSelectOne(_parent);
			return;
		}
		new SplashWindow(_parent, "�������㲢�洢�����Ժ�...", new AbstractAction() {
			public void actionPerformed(final ActionEvent arg0) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) arg0.getSource();

						public void run() { // ��Ҫ���߳��������Ƿ��Ѿ������ݼ��ؽ����ˡ�
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("ϵͳ����Ŭ��������...");
							}
						}
					}, 20, 1000);

					SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
					// �ȰѲ��Ŷ���ָ��������
					ifc.calcDeptDLtarget(planVO, state, "���Ŷ���ָ��");
					ifc.endPlanDL_Dept(planVO.getStringValue("id"));
					HashMap map = ifc.endCalcDeptDXScore(planVO.getStringValue("id"), false, state);
					String res = map.get("res") + "";
					if ("fail".equals(res)) {
						DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_CODE1", "");
						dr.getBillQueryPanel().setVisible(false);
						dr.getBillCellPanel().loadBillCellData((BillCellVO) map.get("vo"));
						MessageBox.showTextArea(planListPanel, "����ʧ��,����Ϊδ��ɲ��Ŷ���ָ�����ֵ���Ա����", dr, 500, 600);
						timer.cancel();
						return;
					} else {
						ifc.calcDeptTotleScoreIntoReviseTable(planVO.getStringValue("id"), state); // �Ѳ��ŵĶ������Ե÷����ս���ŵ��������С�
						MessageBox.show(planListPanel, "�����ɹ�!");
					}
					timer.cancel();
				} catch (Exception e) {
					timer.cancel();
					e.printStackTrace();
					MessageBox.show(planListPanel, "�����쳣,����ϵͳ����Ա��ϵ!�쳣��Ϣ:" + e.getMessage());
				}
			}
		}, false);
	}

	private void endPersonScore(Container _parent, final HashVO planVO, final String state) {
		if (planVO == null) {
			MessageBox.showSelectOne(_parent);
			return;
		}
		new SplashWindow(_parent, "�������㲢�洢�����Ժ�...", new AbstractAction() {
			public void actionPerformed(final ActionEvent arg0) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) arg0.getSource();

						public void run() { // ��Ҫ���߳��������Ƿ��Ѿ������ݼ��ؽ����ˡ�
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("ϵͳ����Ŭ��������...");
							}
						}
					}, 20, 1000);
					SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
					// �ȰѲ��Ŷ���ָ��������
					HashMap map = ifc.endCalcPersonDXScore(planVO.getStringValue("id"), false, state); // ����Ա������
					String res = map.get("res") + "";
					if ("fail".equals(res)) {
						DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_CODE1", "");
						dr.getBillQueryPanel().setVisible(false);
						dr.getBillCellPanel().loadBillCellData((BillCellVO) map.get("vo"));
						MessageBox.showTextArea(planListPanel, "����ʧ��,����Ϊδ���Ա������ָ�����ֵ���Ա����", dr, 500, 600);
						return;
					} else {
						calcDLtarget(mainPanel, planVO, 0, state); // ����Ա������ָ��
						calcDLtarget(mainPanel, planVO, 3, state);//
						MessageBox.show(planListPanel, "�����ɹ�!");
					}
					timer.cancel();
				} catch (Exception e) {
					timer.cancel();
					e.printStackTrace();
					MessageBox.show(planListPanel, "�����쳣,����ϵͳ����Ա��ϵ!�쳣��Ϣ:" + e.getMessage());
				}
			}
		}, false);
	}

	/**
	 * ���㶨��ָ�� type == 0 ���Ŷ��� type == 1 Ա������
	 */
	private void endPlanDx(Container _parent, final HashVO vo, final int type, final String state) {
		if (vo == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}
		new SplashWindow(planListPanel, "�������㲢�洢�����Ժ�...", new AbstractAction() {
			public void actionPerformed(final ActionEvent arg0) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) arg0.getSource();

						public void run() { // ��Ҫ���߳��������Ƿ��Ѿ������ݼ��ؽ����ˡ�
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule(vo.getStringValue("id"), "ָ�����");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("ϵͳ����Ŭ��������...");
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
						MessageBox.showTextArea(planListPanel, "����ʧ��,����Ϊδ���" + (type == 0 ? "����" : "Ա��") + "����ָ�����ֵ���Ա����", dr, 500, 600);
						timer.cancel();
						return;
					} else {
						if (type == 0) {
							getService().calcDeptTotleScoreIntoReviseTable(vo.getStringValue("id"), state);
						} else {
							getService().onePlanCalcAllUserEveryDXTargetScore(vo.getStringValue("id"));
						}
						MessageBox.show(planListPanel, "�����ɹ�!");
					}
					timer.cancel();
				} catch (Exception e) {
					timer.cancel();
					e.printStackTrace();
					MessageBox.show(planListPanel, "�����쳣,����ϵͳ����Ա��ϵ!�쳣��Ϣ:" + e.getMessage());
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

		// Ҫ��һЩ���ơ�����todo...
		if (MessageBox.showConfirmDialog(this, "[��Ҫ��ʾ]�ò�����ɾ����ѡ���·ݵ����п��˽��,��ȷ��Ҫɾ����?", "����", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			DeleteSQLBuilder dsb1 = new DeleteSQLBuilder("sal_target_check_log");
			dsb1.setWhereCondition("id=" + vo.getPkValue());
			DeleteSQLBuilder dsb2 = new DeleteSQLBuilder("sal_dept_check_score");
			dsb2.setWhereCondition("logid=" + vo.getPkValue());
			DeleteSQLBuilder dsb3 = new DeleteSQLBuilder("sal_person_check_score");
			dsb3.setWhereCondition("logid=" + vo.getPkValue());
			DeleteSQLBuilder dsb4 = new DeleteSQLBuilder("sal_target_check_result");
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
			//����ɾ������Ŀ Gwang 2016-01-22
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
				MessageBox.show(planListPanel, "ɾ���ɹ�!");
				planListPanel.removeSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.show(planListPanel, "�����쳣,����ϵͳ����Ա��ϵ!");
			}
		}

	}

	/*
	 * private void repairScoreTable() { BillVO vo =
	 * planListPanel.getSelectedBillVO(); if (vo == null) {
	 * MessageBox.showSelectOne(planListPanel); return; } if
	 * (MessageBox.showConfirmDialog(this,
	 * "�ò���һ���ڶ�ָ������޸ĺ���Ҫ�Կ�����ֱ��������,�ò�����������ָ�꼰��ֽ����Ӱ��,��ȷ�ϲ�����?", "����",
	 * JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) { } }
	 */
	/*
	 * �Զ������ƻ���������������ݲ��뵽sal_dept_check_score�����
	 */
	private void autoCreatePlan() {
		try {
			String[] ids = UIUtil.getStringArrayFirstColByDS(null, "select ID from sal_target_check_log where status != '���˽���'");
			if (ids != null && ids.length > 1) {
				MessageBox.show(this, "����δ�����Ŀ���,���ܽ��д˲���!");
				return;
			}
			if (MessageBox.showConfirmDialog(this, "ϵͳ�����ݿ���ָ�������µĿ��˱�, �Ƿ����?", "����", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				RefDialog_Month chooseMonth = new RefDialog_Month(this, "��ѡ��Ҫ���˵��·�", null, null);
				chooseMonth.initialize();
				chooseMonth.setVisible(true);
				if (chooseMonth.getCloseType() == chooseMonth.CONFIRM) {
					final RefItemVO rtnvo = chooseMonth.getReturnRefItemVO();
					if (rtnvo != null) {
						HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select * from sal_target_check_log where checkdate = '" + rtnvo.getId() + "'");
						if (vos != null && vos.length > 1) {
							MessageBox.show(this, "���·ݵĿ����Ѵ���,�����ظ����д˲���!");
							return;
						} else {
							//zzl [2020-5-18] ����ָ��������������������
							BillCardDialog dialog = new BillCardDialog(this,"����ָ������ѡ��","SAL_TARGET_CHECK_LOG_CODE1",400,200);
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
							new SplashWindow(this, "����������,���Ժ�...", new AbstractAction() {
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
			MessageBox.show(this, "����δ֪�쳣,�������Ա��ϵ!");
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
				MessageBox.show(this, "�����쳣�������Ա��ϵ!");
				return;
			} else if ("success".equals(res)) {
				if (map.containsKey("msginfo")) {
					if (MessageBox.showConfirmDialog(this, map.get("msginfo") + "�Ƿ����?", "����", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
						return;
					}
				}
				map.put("loginuserid", ClientEnvironment.getCurrLoginUserVO().getId());
				map.put("logindeptid", ClientEnvironment.getCurrLoginUserVO().getPKDept());
				map.put("zbtype",zbtype);
				ifc.createScoreTable(map);
				MessageBox.show(this, "���˱��������!");
				planListPanel.refreshData();
			} else {
				MessageBox.show(this, "�����쳣�������Ա��ϵ!");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	// ���㶨��ָ��.
	private void calcDLtarget(Container _parent, final HashVO planVO, final int type, final String state) throws Exception {
		if (planVO == null) {
			MessageBox.showSelectOne(_parent);
			return;
		}
		final List<Exception> errList = new ArrayList();
		new SplashWindow(_parent, "ϵͳ����Ŭ��������...", new AbstractAction() {
			public void actionPerformed(final ActionEvent actionevent) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) actionevent.getSource();

						public void run() { // ��Ҫ���߳��������Ƿ��Ѿ������ݼ��ؽ����ˡ�
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("ϵͳ����Ŭ��������...");
							}
						}
					}, 20, 1000);
					// �ȰѶ���ָ���excelֵ����score��,ִ�в��Ŷ���ָ����㣬Ȼ�����Ա������ָ�ꡣЧ�湤�ʣ�Ա�����˷����Լ����鹤�ʡ�
					String updateState = ""; // ִ�к�״̬
					if (type == 0) {
						getService().onCalcPersonDLTarget(planVO, null); // Ա������ָ�ꡣ
						updateState = "����ָ��������";
					} else if (type == 1) {
						getService().calc_P_Pay(planVO, null);
						updateState = "Ч�湤�ʼ������";
					} else if (type == 2) {
//						getService().calcDelayPay(planVO.getStringValue("id"));
//						getService().calc_QQ_Pay(planVO);
						if (calcQQ) {
							updateState = "���˵÷�/���鹤�����";
						} else {
							updateState = "�����ۺϵ÷�";
						}
					} else if (type == 3) { // �������鹤�ʺ�Ч�湤�ʡ�
						getService().calc_P_Pay(planVO, null);
						getService().calc_QQ_Pay(planVO);
						updateState = "���Խ���";
					} else if (type == 4) { // �����㲿�ŵ÷�-��ʱ�����ˡ�
						getService().calcDeptTotleScoreIntoReviseTable(planVO.getStringValue("id"), state);
					} else if (type == 10 || type == 11) { //
						String targetType = "���Ŷ���ָ��";
						if (type == 11) {
							targetType = "���żƼ�ָ��";
						}
						getService().calcDeptDLtarget(planVO, state, targetType);
						SplashWindow w = (SplashWindow) actionevent.getSource();
						w.setWaitInfo("ϵͳ������������" + targetType + "�÷�...");
						if (type == 10 && !getService().endPlanDL_Dept(planVO.getStringValue("id"))) {
							MessageBox.show((SplashWindow) actionevent.getSource(), targetType + "δ���,ִ��ʧ��!");
							timer.cancel();
							return;
						}
						timer.cancel(); // ͣ��
						return;// ֱ�ӷ���
					} else if (type == 12) {
						String batch[] = UIUtil.getStringArrayFirstColByDS(null, "select distinct(calbatch) from sal_person_check_list targe where targettype='Ա������ָ��' and calbatch is not null and calbatch!='' order by calbatch");
						for (int i = 0; i < batch.length; i++) {
							getService().onCalcPersonDLTarget(planVO, batch[i]); // Ա������ָ�ꡣ
							getService().calc_P_Pay(planVO, batch[i]);
						}
						updateState = "����ָ�����";
					} else if (type == 13) { //Ա������ָ���Զ�����
						String batch[] = UIUtil.getStringArrayFirstColByDS(null, "select distinct(calbatch) from sal_person_check_list targe where targettype='Ա������ָ��'  order by calbatch");
						for (int i = 0; i < batch.length; i++) {
							getService().onCalcPersonDLTarget(planVO, batch[i]); // Ա������ָ�ꡣ
							getService().calc_P_Pay(planVO, batch[i]);
						}
						updateState = "����ָ�����";
					}
					UIUtil.executeUpdateByDS(null, "update sal_target_check_log set status='" + state + "' where id = " + planVO.getStringValue("id"));
					timer.cancel();
				} catch (Exception e1) {
					timer.cancel();// ����ֹͣ
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

	// �����λְ��ָ��ָ��.
	private void calcPostDutytarget(Container _parent, final HashVO planVO, final String state) throws Exception {
		if (planVO == null) {
			MessageBox.showSelectOne(_parent);
			return;
		}
		final List<Exception> errList = new ArrayList();
		new SplashWindow(_parent, "ϵͳ����Ŭ��������...", new AbstractAction() {
			public void actionPerformed(final ActionEvent actionevent) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) actionevent.getSource();

						public void run() { // ��Ҫ���߳��������Ƿ��Ѿ������ݼ��ؽ����ˡ�
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("ϵͳ����Ŭ��������...");
							}
						}
					}, 20, 1000);
					HashMap map = getService().calcPostDutyTargetScore(planVO, state);
					String res = map.get("res") + "";
					if ("fail".equals(res)) {
						DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_CODE1", "");
						dr.getBillQueryPanel().setVisible(false);
						dr.getBillCellPanel().loadBillCellData((BillCellVO) map.get("vo"));
						MessageBox.showTextArea(planListPanel, "����ʧ��,����Ϊδ������ֵ���Ա����", dr, 500, 600);
					} else {
						MessageBox.show(planListPanel, "�����ɹ�!");
					}
					timer.cancel();
				} catch (Exception e1) {
					timer.cancel();// ����ֹͣ
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
	 * �õ���ǰ���˵ļƻ�
	 */
	private JPanel getCurrCheckPlanWKPanel() {
		mainPanel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new BorderLayout());
		WLTPanel btnPanel = new WLTPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
		btnPanel.setOpaque(false);
		big_btn_createPlan = new WLTButton("1���½�����");
		big_btn_calcDetpPlan = new WLTButton("2�����ŵ÷ּ���");
		big_btn_revise = new WLTButton("3���޶����ŵ÷�");
		big_btn_calc_person = new WLTButton("4��Ա���÷ּ���");
		big_btn_endPlan = new WLTButton("5����������");

		big_btn_createPlan.setFont(new Font("����", Font.PLAIN, 14));
		big_btn_calcDetpPlan.setFont(new Font("����", Font.PLAIN, 14));
		big_btn_calc_person.setFont(new Font("����", Font.PLAIN, 14));
		big_btn_endPlan.setFont(new Font("����", Font.PLAIN, 14));
		big_btn_revise.setFont(new Font("����", Font.PLAIN, 14));

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
	 * zzl[2020-6-12] ���������ʵ�������Ҫ���ư�ť��
	 * @param _event
	 */
	@Override
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if(_event.getSource()==planListPanel){
			String ZBTYPE=planListPanel.getSelectedBillVO().getStringValue("ZBTYPE");
			if(ZBTYPE.equals("����")){
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
