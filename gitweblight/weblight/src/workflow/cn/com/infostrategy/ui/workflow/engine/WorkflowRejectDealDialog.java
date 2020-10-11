package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.workflow.engine.DealActivityVO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * �˻ش���ʱ�Ľ���
 * ��������Ŀ�������������˻ط��ࣺA-�˻ظ�������  B-�˻ظ��ύ��  C-�˻ظ���ʷ�����߹�������һ����
 * @author xch
 *
 */
public class WorkflowRejectDealDialog extends BillDialog implements WindowListener, ActionListener {

	private BillVO billVO = null; //ҵ������VO.
	private WFParVO firstTaskVO = null; //����

	private JCheckBox checkBox = null;
	private JRadioButton radio_1, radio_2, radio_3;
	private BillListPanel billList = null; //
	private JButton btn_confirm, btn_monitor, btn_cancel; ////
	private int closetype = -1; //

	private TBUtil tbUtil = null; //

	public WorkflowRejectDealDialog(Container _parent, BillVO _billVO, WFParVO _firstTaskVO) {
		super(_parent, "�˻ش���", 750, 150, 150, 100); ////
		this.billVO = _billVO; //
		this.firstTaskVO = _firstTaskVO; //
		this.setResizable(true); //
		this.addWindowListener(this);
		initialize();
	}

	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //

		JPanel panel_all = new WLTPanel(WLTPanel.VERTICAL_TOP_TO_BOTTOM); //
		panel_all.setLayout(new BorderLayout()); //

		panel_all.add(getNorthPanel(), BorderLayout.NORTH); //
		billList = new BillListPanel("cn.com.infostrategy.bs.workflow.tmo.TMO_ChooseUser"); //
		billList.setItemVisible("usercode", false); //
		billList.setItemVisible("iseverprocessed", false); //

		panel_all.add(billList, BorderLayout.CENTER); //

		DealTaskVO[] taskVOS = firstTaskVO.getDealActivityVOs()[0].getDealTaskVOs(); //
		setDealTaskVOs(taskVOS); //
		billList.moveToTop(); //�ö�
		billList.clearSelection(); //���ѡ��
		billList.setToolbarVisiable(false); //
		billList.setEnabled(true);
		billList.setItemEditable(false); //
		panel_all.add(getSouthPanel(), BorderLayout.SOUTH); //
		billList.setVisible(false); //

		this.add(panel_all, BorderLayout.CENTER); //
	}

	private JPanel getNorthPanel() { //
		JPanel panel = WLTPanel.createDefaultPanel(null); //

		checkBox = new JCheckBox("�������Ƿ�ֱ���ύ����?(�������߲��ٰ�ԭ����Ƶ�·�����ϱ�����ֱ���ϱ�����)", true); //
		checkBox.setToolTipText("<html>��ʱ������һ�����,��ֱ���˻ظ������˲���һ������,<br>Ȼ�����˲������ֱ���ύ����,�������ǰ�ԭ��������·���ύ!</html>"); //
		checkBox.setForeground(Color.BLUE); //
		checkBox.setBounds(5, 15, 600, 20); //
		checkBox.setOpaque(false);
		checkBox.setFocusable(false);
		if (getTBUtil().getSysOptionBooleanValue("�������˻�ʱ�Ƿ�ָ��������ֱ���ύ", true)) { //����Ĭ��Ҫ����,��ҵĬ�ϲ�Ҫ��,����ֻ�ܸ������!! Ĭ���ǹ��ϵ�!
			checkBox.setSelected(true);
		} else {
			checkBox.setSelected(false);
		}
		panel.add(checkBox); //

		radio_1 = new JRadioButton("�˻ظ������ˡ�" + this.firstTaskVO.getWfinstance_createusername() + "��(��������)", true); //
		radio_2 = new JRadioButton("�˻ظ��ύ�ߡ�" + this.firstTaskVO.getDealpooltask_createusername() + "��"); //
		radio_3 = new JRadioButton("�˻ص���ʷ����һ��"); //

		radio_2.setToolTipText(this.firstTaskVO.getDealpooltask_createusername()); //
		ButtonGroup btngroup = new ButtonGroup(); //
		btngroup.add(radio_2); //
		btngroup.add(radio_1); //
		btngroup.add(radio_3); //

		String str_defaultChoose = getTBUtil().getSysOptionStringValue("�������˻�ʱĬ�ϸ�˭", "1"); //��ҵ��Ŀ��Ҫ��Ĭ�Ͻ���,������Ŀ��Ĭ��Ҫ��������,���Ա���������!!
		if (str_defaultChoose.equals("1")) { //
			radio_1.setSelected(true); //
		} else if (str_defaultChoose.equals("2")) { //
			radio_2.setSelected(true); //
		}

		radio_1.setBounds(5, 45, 250, 20); //
		radio_2.setBounds(255, 45, 245, 20); //
		radio_3.setBounds(500, 45, 200, 20); //

		radio_1.setFocusable(false);
		radio_2.setFocusable(false);
		radio_3.setFocusable(false);

		radio_1.addActionListener(this);
		radio_2.addActionListener(this);
		radio_3.addActionListener(this);

		panel.add(radio_1); //
		panel.add(radio_2); //
		panel.add(radio_3); //
		radio_1.setOpaque(false);
		radio_2.setOpaque(false);
		radio_3.setOpaque(false);

		panel.setPreferredSize(new Dimension(900, 80)); //
		return panel;
	}

	/**
	 * ��ť���
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER, 5, 10)); //
		btn_confirm = new WLTButton(UIUtil.getLanguage("ȷ��")); //
		btn_monitor = new WLTButton("���̼��"); //
		btn_cancel = new WLTButton(UIUtil.getLanguage("ȡ��")); //
		btn_confirm.setPreferredSize(new Dimension(70, 20)); //
		btn_cancel.setPreferredSize(new Dimension(70, 20)); //
		btn_monitor.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_confirm);
		panel.add(btn_monitor);
		panel.add(btn_cancel);

		return panel;
	}

	/**
	 * ��һ�����������һ��DealTaskVO..
	 */
	public void setDealTaskVOs(DealTaskVO[] _taskVO) {
		if(_taskVO==null){
			return;
		}
		for (int i = 0; i < _taskVO.length; i++) {
			//			if (containsUser(_taskVO[i].getParticipantUserId())) { //����ڴ��Ѱ������û�!������
			//				continue;
			//			}

			String[] str_allkeys = billList.getTempletVO().getItemKeys(); //
			BillVO billVO = new BillVO(); //
			billVO.setKeys(str_allkeys); //
			Object[] objs = new Object[str_allkeys.length]; //
			for (int j = 0; j < str_allkeys.length; j++) {
				if (str_allkeys[j].equalsIgnoreCase("userid")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("usercode")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserCode()); //������Ա�ı���
				} else if (str_allkeys[j].equalsIgnoreCase("username")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserName()); //������Ա������
				} else if (str_allkeys[j].equalsIgnoreCase("userdeptid")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserDeptId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("userdeptcode")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserDeptCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("userdeptname")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserDeptName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("userroleid")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserRoleId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("userrolecode")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserRoleCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("userrolename")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserRoleName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("accruserid")) {
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("accrusercode")) {
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("accrusername")) {
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("fromactivityid")) {
					objs[j] = new StringItemVO(_taskVO[i].getFromActivityId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("fromactivityname")) {
					objs[j] = new StringItemVO(_taskVO[i].getFromActivityName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionid")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitioncode")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionname")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionDealtype")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionDealtype()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionIntercept")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionIntercept()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionMailSubject")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionMailSubject()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionMailContent")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionMailContent()); //
				} else if (str_allkeys[j].equalsIgnoreCase("currActivityid")) {
					objs[j] = new StringItemVO(_taskVO[i].getCurrActivityId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("currActivitycode")) {
					objs[j] = new StringItemVO(_taskVO[i].getCurrActivityCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("currActivitytype")) {
					objs[j] = new StringItemVO(_taskVO[i].getCurrActivityType()); //
				} else if (str_allkeys[j].equalsIgnoreCase("currActivityname")) {
					objs[j] = new StringItemVO(_taskVO[i].getCurrActivityName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("successparticipantreason")) {
					objs[j] = new StringItemVO(_taskVO[i].getSuccessParticipantReason()); //
				} else if (str_allkeys[j].equalsIgnoreCase("iseverprocessed")) {
					objs[j] = new StringItemVO(_taskVO[i].getIseverprocessed()); //
				}
			}

			billVO.setDatas(objs); //
			int li_newrow = billList.addEmptyRow(); //
			//vmap_users.put(_taskVO[i].getParticipantUserId(), billVO); //�ڴ��м���
			billList.setBillVOAt(li_newrow, billVO); //ҳ���в���
		}
	}

	public WFParVO getReturnVO() {
		return firstTaskVO; //
	}

	public int getClosetype() {
		return closetype;
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil(); //
		return tbUtil;
	}

	//���ȷ��
	private void onConfirm() {
		DealTaskVO[] commitTaskVOs = null; //
		if (radio_1.isSelected()) {
			commitTaskVOs = getSelectedTaskVO_1(); ////
		} else if (radio_2.isSelected()) {
			commitTaskVOs = getSelectedTaskVO_2(); ////
		} else if (radio_3.isSelected()) {
			int li_selindex = billList.getSelectedRow(); //ȡ���ǵڼ�������!!!
			if (li_selindex < 0) {
				MessageBox.show(this, "����ѡ��һ����������Ա!"); //
				return;
			}
			commitTaskVOs = getSelectedTaskVOs(); ////
		}

		clearAllDealTaskVOs(firstTaskVO); //������е�һ�δӷ������˷��صĴ���������,Ŀ����Ϊ���������.
		firstTaskVO.setCommitTaskVOs(commitTaskVOs); //���ô�������..
		closetype = 1;
		this.dispose(); //
	}

	private void onCancel() {
		this.closetype = 2;
		this.dispose(); //
	}

	private void onMonitor() {
		try {
			WorkflowMonitorDialog dialog = new WorkflowMonitorDialog(this, this.firstTaskVO.getWfinstanceid(), this.billVO);
			dialog.setMaxWindowMenuBar();
			dialog.setVisible(true); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//ֱ�Ӹ�������
	private DealTaskVO[] getSelectedTaskVO_1() {
		DealTaskVO commitTaskVO = new DealTaskVO(); //
		commitTaskVO.setParticipantUserId(this.firstTaskVO.getWfinstance_createuserid()); //
		commitTaskVO.setParticipantUserCode(this.firstTaskVO.getWfinstance_createusercode()); //
		commitTaskVO.setParticipantUserName(this.firstTaskVO.getWfinstance_createusername()); //

		//��Ȩ����Ϣ
		commitTaskVO.setAccrUserId(this.firstTaskVO.getDealpooltask_createuser_accruserid()); //
		commitTaskVO.setAccrUserCode(this.firstTaskVO.getDealpooltask_createuser_accrusercode()); //
		commitTaskVO.setAccrUserName(this.firstTaskVO.getDealpooltask_createuser_accrusername()); //

		//��Դ���ھ����ҵ�ǰ����ĵ�ǰ����
		commitTaskVO.setFromActivityId(firstTaskVO.getCurractivity()); //���ĸ����ڹ�����
		commitTaskVO.setFromActivityName(firstTaskVO.getCurractivityName()); //

		//��ǰ����Ӧ�þ��Ǵ����������Ļ���!!����һ����ʼ��!!
		commitTaskVO.setCurrActivityId(this.firstTaskVO.getWfinstance_createactivityid()); //
		commitTaskVO.setCurrActivityCode(this.firstTaskVO.getWfinstance_createactivitycode()); //
		commitTaskVO.setCurrActivityName(this.firstTaskVO.getWfinstance_createactivityname()); //
		commitTaskVO.setCurrActivityType(this.firstTaskVO.getWfinstance_createactivitytype()); //

		commitTaskVO.setRejectedDirUp(checkBox.isSelected()); //�����Ƿ��˻���ֱ���ύ
		return new DealTaskVO[] { commitTaskVO };
	}

	//ֱ�Ӹ������ύ��,������������������Ǹ���!
	private DealTaskVO[] getSelectedTaskVO_2() {
		DealTaskVO commitTaskVO = new DealTaskVO(); //
		commitTaskVO.setParticipantUserId(this.firstTaskVO.getDealpooltask_createuserid()); //
		commitTaskVO.setParticipantUserCode(this.firstTaskVO.getDealpooltask_createusercode()); //
		commitTaskVO.setParticipantUserName(this.firstTaskVO.getDealpooltask_createusername()); //

		//��Ȩ����Ϣ
		commitTaskVO.setAccrUserId(this.firstTaskVO.getDealpooltask_createuser_accruserid()); //
		commitTaskVO.setAccrUserCode(this.firstTaskVO.getDealpooltask_createuser_accrusercode()); //
		commitTaskVO.setAccrUserName(this.firstTaskVO.getDealpooltask_createuser_accrusername()); //

		//��Դ���ھ����ҵ�ǰ����ĵ�ǰ����
		commitTaskVO.setFromActivityId(firstTaskVO.getCurractivity()); //���ĸ����ڹ�����
		commitTaskVO.setFromActivityName(firstTaskVO.getCurractivityName()); //

		//��ǰ����Ӧ�þ�����һ���ύ�Ļ���
		commitTaskVO.setCurrActivityId(this.firstTaskVO.getFromactivity()); //Ŀ�껷���ǵ�ǰ�������Դ����,��Ϊ��ƹ��ʽ��,���ߵ�!�����ύʱ��Ч����һ��,Ŀ������Դ����!
		commitTaskVO.setCurrActivityCode(this.firstTaskVO.getFromactivityCode()); //
		commitTaskVO.setCurrActivityName(this.firstTaskVO.getFromactivityName()); //
		commitTaskVO.setCurrActivityType(this.firstTaskVO.getFromactivityType()); //
		commitTaskVO.setRejectedDirUp(checkBox.isSelected()); //�����Ƿ��˻���ֱ���ύ
		return new DealTaskVO[] { commitTaskVO };
	}

	/**
	 * ����Ա����з������д���������!!
	 * @return
	 */
	public DealTaskVO[] getSelectedTaskVOs() {
		BillVO[] billVOs = billList.getSelectedBillVOs(); //
		DealTaskVO[] commitTaskVO = new DealTaskVO[billVOs.length]; //
		for (int i = 0; i < billVOs.length; i++) {
			commitTaskVO[i] = new DealTaskVO(); //
			commitTaskVO[i].setParticipantUserId(billVOs[i].getStringValue("userid")); //
			commitTaskVO[i].setParticipantUserCode(billVOs[i].getStringValue("usercode")); //
			commitTaskVO[i].setParticipantUserName(billVOs[i].getStringValue("username")); //

			commitTaskVO[i].setParticipantUserDeptId(billVOs[i].getStringValue("userdeptid")); //
			commitTaskVO[i].setParticipantUserDeptCode(billVOs[i].getStringValue("userdeptcode")); //
			commitTaskVO[i].setParticipantUserDeptName(billVOs[i].getStringValue("userdeptname")); //

			commitTaskVO[i].setParticipantUserRoleId(billVOs[i].getStringValue("userroleid")); //
			commitTaskVO[i].setParticipantUserRoleCode(billVOs[i].getStringValue("userrolecode")); //
			commitTaskVO[i].setParticipantUserRoleName(billVOs[i].getStringValue("userrolename")); //

			commitTaskVO[i].setAccrUserId(billVOs[i].getStringValue("accruserid")); //��Ȩ������.
			commitTaskVO[i].setAccrUserCode(billVOs[i].getStringValue("accrusercode")); //��Ȩ�˱���.
			commitTaskVO[i].setAccrUserName(billVOs[i].getStringValue("accrusername")); //��Ȩ������.

			commitTaskVO[i].setFromActivityId(billVOs[i].getStringValue("fromactivityid")); //
			commitTaskVO[i].setFromActivityName(billVOs[i].getStringValue("fromactivityname")); //

			commitTaskVO[i].setTransitionId(billVOs[i].getStringValue("transitionid")); //
			commitTaskVO[i].setTransitionCode(billVOs[i].getStringValue("transitioncode")); //
			commitTaskVO[i].setTransitionName(billVOs[i].getStringValue("transitionname")); //
			commitTaskVO[i].setTransitionDealtype(billVOs[i].getStringValue("transitionDealtype")); //
			commitTaskVO[i].setTransitionIntercept(billVOs[i].getStringValue("transitionIntercept")); //
			commitTaskVO[i].setTransitionMailSubject(billVOs[i].getStringValue("transitionMailSubject")); //
			commitTaskVO[i].setTransitionMailContent(billVOs[i].getStringValue("transitionMailContent")); //

			commitTaskVO[i].setCurrActivityId(billVOs[i].getStringValue("currActivityid")); //
			commitTaskVO[i].setCurrActivityCode(billVOs[i].getStringValue("currActivitycode")); //
			commitTaskVO[i].setCurrActivityName(billVOs[i].getStringValue("currActivityname")); //
			commitTaskVO[i].setCurrActivityType(billVOs[i].getStringValue("currActivitytype")); //
			commitTaskVO[i].setRejectedDirUp(checkBox.isSelected()); //�����Ƿ��˻���ֱ���ύ
		}

		return commitTaskVO; //
	}

	//
	private void onRadioSelectChanged() {
		if (radio_3.isSelected()) {
			billList.setVisible(true); //
			this.setSize(750, 550); //
		} else {
			billList.setVisible(false);
			this.setSize(750, 150);
		}
	}

	/**
	 * ������дӷ������˷��صĲ��벿���������Ա����Ϣ,��Ϊ��Щ��Ϣ�ڶ����ύʱ����Ҫ��!!Ŀ����Ϊ���������!!
	 * @param _ParVO
	 */
	private void clearAllDealTaskVOs(WFParVO _ParVO) {
		DealActivityVO[] activityVOs = _ParVO.getDealActivityVOs(); //
		if (activityVOs != null) {
			for (int i = 0; i < activityVOs.length; i++) {
				activityVOs[i].setDealTaskVOs(null); //������д���������
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //ȷ��
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //ȡ��
		} else if (e.getSource() == btn_monitor) {
			onMonitor(); //���
		} else if (e.getSource() == radio_1 || e.getSource() == radio_2 || e.getSource() == radio_3) {
			onRadioSelectChanged(); //
		}
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

}
