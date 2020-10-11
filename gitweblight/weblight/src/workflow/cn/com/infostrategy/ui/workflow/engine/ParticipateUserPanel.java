package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.to.workflow.engine.DealActivityVO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ѡ����Ա�����..���������ļ���ĳ�������ϵ����н�����Ա�������Ա�Ľ���!!
 * ������չ�ɿ��Ե��[������Ա]��ť�����������µ���Ա! ������������!!! ������� ������Ҫ!!
 * @author xch
 *
 */
public class ParticipateUserPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 6178660830460024752L;

	private DealActivityVO currActivityVO = null; //��ǰ���ڵ���Ϣ!!!

	private Pub_Templet_1VO templet_VO = null; //ģ�����
	private BillVO billVO = null; //����VO,��Ϊ������Ȩģʽ,���ֵ��"������Ա"�ֹ������Աʱ,��Ҫ�õ���Ȩģ��!! ����Ȩģ�����Ǹ������̷���õ���! �����̷������Ǹ��ݵ���������ҵ�����ͼ��������! ������/ҵ���������Ǵ�ҳ����ȡ�õ�!������ҪBillVO

	private BillListPanel billListPanel_1 = null; //������Ա!!!
	private BillListPanel billListPanel_2 = null; //������Ա!!!
	private JTable table1 = null; //��1
	private JTable table2 = null; //��2

	private WLTButton btn_addReceiver = new WLTButton("��ӽ�����Ա"); //
	private WLTButton btn_addCCToUser = new WLTButton("��ӳ�����Ա"); //

	private WLTButton btn_delReceiver = new WLTButton("ɾ��������Ա"); //
	private WLTButton btn_delCCToUser = new WLTButton("ɾ��������Ա"); //

	private JRadioButton radioBtn_1, radioBtn_2, radioBtn_3; //��ռ/��ǩ/�����̻��
	private AddNewUserDialog freeAddUserdialog = null; //���������Ա�ĶԻ���
	private VectorMap vmap_users = new VectorMap(); //���ʵ�����ݵĶ���.Ϊ�˹�����

	private TBUtil tbUtil = null; //
	boolean isCheckList = false; //�Ƿ��ǹ�ѡ��ķ�ʽ,�Ͼ����˿ͻ�����ϲ����ѡ��ķ�ʽ!
	boolean isSingleSelectUser = false; //�Ƿ���뵥ѡ�û�?�����ʴ���Ŀ�� 
	private HashVO hvoCurrWFInfo = null;//��ǰ������Ϣ,�����ݵ���������ҵ������,���������ʲô����,�������ж�����ʲô�����������/2016-04-20��
	private HashVO hvoCurrActivityInfo = null;//��ǰ������Ϣ
	private WFParVO firstTaskVO = null; //����!!����������!!!

	public ParticipateUserPanel(DealActivityVO _currActivityVO, BillVO _billVO, HashVO _hvoCurrWFInfo, HashVO _hvoCurrActivityInfo, WFParVO _firstTaskVO) {
		this.currActivityVO = _currActivityVO; //
		this.billVO = _billVO;
		this.hvoCurrWFInfo = _hvoCurrWFInfo;
		this.hvoCurrActivityInfo = _hvoCurrActivityInfo;
		this.firstTaskVO = _firstTaskVO;
		try {
			this.templet_VO = UIUtil.getPub_Templet_1VO(new ServerTMODefine("cn.com.infostrategy.bs.workflow.tmo.TMO_ChooseUser")); //
			initialize(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void initialize() {
		this.setLayout(new BorderLayout()); //
		isCheckList = getTBUtil().getSysOptionBooleanValue("�������ύʱѡ����Ա�Ƿ��ǹ�ѡ��", false); //�Ƿ��ǹ�ѡ��ķ�ʽ?
		isSingleSelectUser = getTBUtil().getSysOptionBooleanValue("�������ύʱ��ռģʽ�Ƿ�ֻ�ܵ�ѡ", false); //Ĭ���Ƕ�ѡ��,����ǵ�ѡ,�����ζ����Զ����ռ��,û�л�ǩ�ĸ�����!

		//���յĽ���!
		btn_addReceiver.setToolTipText("�������µĽ�����Ա"); //
		btn_addReceiver.addActionListener(this); //
		btn_delReceiver.setToolTipText("ɾ��ѡ�еĽ�����Ա"); //
		btn_delReceiver.addActionListener(this); //

		billListPanel_1 = new BillListPanel(templet_VO); //������Ա

		if ("1".equals(currActivityVO.getApprovemodel()) && isSingleSelectUser) { //�������ռģʽ,�������жϵ�ǰ���ڵķ����ʲô
			billListPanel_1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //��ѡ
		} else { //����Ƕ�ѡ,��Ҫ�����Ƿ��ǹ�ѡ�����ʽ?
			billListPanel_1.setRowNumberChecked(isCheckList); //�Ƿ��ǹ�ѡ����ʽ!!�Ͼ����˿ͻ�����˵Ctrl��ѡ�Ļ��Ʋ���!!!
		}

		billListPanel_1.getTitlePanel().setVisible(false); //
		//billListPanel_1.getTitleLabel().setText("���������Ա");  //

		//sunfujun/20121126/���Ƿ���ʾ���հ�ť�ŵ�������
		if (getTBUtil().getSysOptionBooleanValue("�������ύʱ�Ƿ���ʾ������Ա��ť", true)) { //ϵͳȫ�ֲ���! Ӧ��������ͼ�еĻ��ڲ���! ��ȫ�ֲ��������ֲ���������?Ȼ���ֿ��Լ��ݾɰ汾?
			billListPanel_1.addBatchBillListButton(new WLTButton[] { btn_addReceiver, btn_delReceiver }); //
		} else {
			if ("Y".equals(currActivityVO.getCanselfaddparticipate())) {
				billListPanel_1.addBatchBillListButton(new WLTButton[] { btn_addReceiver, btn_delReceiver });
			}
		}
		billListPanel_1.repaintBillListButton(); //
		table1 = billListPanel_1.getTable(); //
		table1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent _event) {
				if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
					onDeleteRow((JTable) _event.getSource()); //
				}
			}
		}); //

		//���͵Ľ���!
		btn_addCCToUser.setToolTipText("�������µĳ�����Ա"); //
		btn_addCCToUser.addActionListener(this); //
		btn_delCCToUser.setToolTipText("ɾ��ѡ�еĳ�����Ա"); //
		btn_delCCToUser.addActionListener(this); //
		billListPanel_2 = new BillListPanel(templet_VO); //������Ա

		//����ʱ��Զ���Ը������,���Ƕ�ѡ������
		billListPanel_2.setRowNumberChecked(isCheckList); //�Ƿ��ǹ�ѡ����ʽ!!�Ͼ����˿ͻ�����˵Ctrl��ѡ�Ļ��Ʋ���!!!

		billListPanel_2.setItemVisible("iseverprocessed", false);
		billListPanel_2.getTitlePanel().setVisible(false); //
		if (getTBUtil().getSysOptionBooleanValue("�������ύʱ�Ƿ���ʾ������Ա��ť", true)) { //
			billListPanel_2.addBatchBillListButton(new WLTButton[] { btn_addCCToUser, btn_delCCToUser }); //
		}
		billListPanel_2.repaintBillListButton(); //
		table2 = billListPanel_2.getTable(); ////
		table2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent _event) {
				if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
					onDeleteRow((JTable) _event.getSource()); //
				}
			}
		}); //

		if (getTBUtil().getSysOptionBooleanValue("�������Ƿ��г��͹���", true)) { //�����Ҫ���͹���,��ʹ�÷ָ�������������!!
			WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billListPanel_1, billListPanel_2); //
			splitPanel.setDividerLocation(310); //���ø߶�
			this.add(splitPanel, BorderLayout.CENTER); //
		} else { //�������Ҫ���͹���,��ֱ��ֻ��һ��������Ա��!

			this.add(billListPanel_1, BorderLayout.CENTER); //
		}

		//�����˹��޸�����ģʽ,����ǰ���ں�̨�趨��,Ҫô����ռ,Ҫô�ǻ�ǩ,Ҫô��������! ����������ʱ������Ҫ���û���ʱ���������㻹�ǻ�ǩ!!!
		radioBtn_1 = new JRadioButton("��ռ"); //
		radioBtn_2 = new JRadioButton("��ǩ"); //
		radioBtn_3 = new JRadioButton("�����̻��"); //
		radioBtn_1.setOpaque(false);
		radioBtn_2.setOpaque(false);
		radioBtn_3.setOpaque(false);
		radioBtn_1.setFocusable(false);
		radioBtn_2.setFocusable(false);
		radioBtn_3.setFocusable(false);

		radioBtn_1.setToolTipText("ѡ�еĽ�����ֻҪ��һ�������ȴ�����,��ֱ�ӽ�����һ����"); //
		radioBtn_2.setToolTipText("����ѡ�е����н����߶�������,���ܽ�����һ����"); //
		radioBtn_3.setToolTipText("�����н�����Ϊ������,��Ŀ�껷�����ִ���һ��������,Ȼ�����������̶����������,�Զ�����"); //

		if ("1".equalsIgnoreCase(currActivityVO.getApprovemodel())) {
			radioBtn_1.setSelected(true); //
		} else if ("2".equalsIgnoreCase(currActivityVO.getApprovemodel())) {
			radioBtn_2.setSelected(true); //
		} else if ("3".equalsIgnoreCase(currActivityVO.getApprovemodel())) {
			radioBtn_3.setSelected(true); //
		} else {
			radioBtn_1.setSelected(true); //
		}

		ButtonGroup btnGroup = new ButtonGroup(); //
		btnGroup.add(radioBtn_1); //
		btnGroup.add(radioBtn_2); //
		btnGroup.add(radioBtn_3); //

		JPanel panel_radioBtn = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new FlowLayout(FlowLayout.LEFT, 0, 0), LookAndFeel.defaultShadeColor1, false); //
		panel_radioBtn.add(radioBtn_1); //
		panel_radioBtn.add(radioBtn_2); //
		panel_radioBtn.add(radioBtn_3); //

		//����Ҫ�������,һ����ȫ�ֲ���,һ���ǻ��ڲ���,���ڲ������Ը���ȫ�ֲ���!
		if (1 == 2) { //�Ȳ���,�Ժ���Ҫʱ���ϣ�һ����˵ֻ���ض����ڲŻ���!
			this.add(panel_radioBtn, BorderLayout.NORTH); //
		}
		//ƽ̨��׼�����̶�����������,��Ҫ������������ҵ�����ʹ���ȥ��������Ϊ������������Ҫ�ж�,ҲҪ����ǰ������Ϣ����ȥ,�������������ж�!!!
		if (hvoCurrWFInfo != null) {
			String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); //���̺���������,ͳһ�ĵ�UI��������!!!
			if (str_wfeguiintercept != null) {
				WLTHashMap parMap = new WLTHashMap();
				try {
					WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance();
					uiIntercept.afterOpenParticipateUserPanel(this, billVO, hvoCurrActivityInfo, firstTaskVO, parMap); //����ƽ̨���������������!!!
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} //
			}
		}
	}

	/**
	 * ɾ����Ա,����ʱ��Ա̫���˻�ѡ����,����ɾ�����е�ĳЩ��Ա!! 
	 * ����������ֹ������Ա,����ɾ������Ա,��û�к�ڵİ취,ֻ�ܹرմ������´�!!
	 */
	private void onDeleteRow(JTable _table) {
		if (_table == table1) {
			int[] li_rows = table1.getSelectedRows(); //
			if (li_rows.length <= 0) {
				MessageBox.show(this, "����ѡ��һ������������Ա���ܽ��д˲���!"); //
				return; //
			}

			if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��ѡ�еĽ�����Ա��?")) {
				return;
			}
			billListPanel_1.removeSelectedRows(); //
		} else if (_table == table2) {
			int[] li_rows = table2.getSelectedRows(); //
			if (li_rows.length <= 0) {
				MessageBox.show(this, "����ѡ��һ������������Ա���ܽ��д˲���!"); //
				return; //
			}
			if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��ѡ�еĳ�����Ա��?")) {
				return;
			}
			billListPanel_2.removeSelectedRows(); //
		}
	}

	/**
	 * ��һ�����������һ��DealTaskVO..
	 */
	public void setDealTaskVOs(DealTaskVO[] _taskVO, boolean _isAutoSel) {
		if (_taskVO == null) {
			return;
		}
		for (int i = 0; i < _taskVO.length; i++) {
			if (containsUser(_taskVO[i].getParticipantUserId())) { //����ڴ��Ѱ������û�!������
				//continue;
			}

			String[] str_allkeys = templet_VO.getItemKeys(); //
			BillVO billVO = new BillVO(); //
			billVO.setKeys(str_allkeys); //
			Object[] objs = new Object[str_allkeys.length]; //
			for (int j = 0; j < str_allkeys.length; j++) {
				if (str_allkeys[j].equalsIgnoreCase("userid")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("usercode")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("username")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserName()); //
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
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserId()); //��Ȩ������
				} else if (str_allkeys[j].equalsIgnoreCase("accrusercode")) {
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserCode()); //��Ȩ�˱���
				} else if (str_allkeys[j].equalsIgnoreCase("accrusername")) {
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserName()); //��Ȩ������
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
				} else if (str_allkeys[j].equalsIgnoreCase("currActivityname")) { //��������
					objs[j] = new StringItemVO(currActivityVO.getCurrActivityName(false)); //��ǰ��������,Ҫ���Ͼ��������!!!
				} else if (str_allkeys[j].equalsIgnoreCase("successparticipantreason")) {
					objs[j] = new StringItemVO(_taskVO[i].getSuccessParticipantReason()); //
				} else if (str_allkeys[j].equalsIgnoreCase("iseverprocessed")) {
					objs[j] = new StringItemVO(_taskVO[i].getIseverprocessed()); //
				}
			}

			billVO.setDatas(objs); //

			if (!_taskVO[i].isCCTo()) { //������ǳ��͵�,����ʵ���ύ��,�Ų�������ı���!
				int li_newrow = billListPanel_1.addEmptyRow(_isAutoSel, true); //
				billListPanel_1.setBillVOAt(li_newrow, billVO); //ҳ���в���
				if (billListPanel_1.isRowNumberChecked()) {
					billListPanel_1.setCheckedRow(li_newrow, _isAutoSel);
				}
				vmap_users.put(_taskVO[i].getParticipantUserId(), billVO); //�ڴ��м���
			} else { //����ǳ��͵�,�����ڶ��ű���!!
				int li_newrow = billListPanel_2.addEmptyRow(_isAutoSel, true); //
				billListPanel_2.setBillVOAt(li_newrow, billVO); //ҳ���в���
				billListPanel_2.setCheckedRow(li_newrow, true); //
				vmap_users.put(_taskVO[i].getParticipantUserId(), billVO); //�ڴ��м���
			}
		}
	}

	public BillVO[] getMapBillVOs() {
		Object[] objs = vmap_users.getValues(); //
		BillVO[] vos = new BillVO[objs.length]; //
		for (int i = 0; i < objs.length; i++) {
			vos[i] = (BillVO) objs[i];
		}
		return vos;
	}

	/**
	 * ҳ�����Ƿ��Ѱ������û�!
	 * @param _userid
	 * @return
	 */
	private boolean containsUser(String _userid) {
		return vmap_users.containsKey(_userid); //
	}

	/**
	 * ��ӽ�����Ա
	 * ����Ǹ�������,Ȼ��Ĭ���ǵ�ǰ��Ա�����ڲ���! �ұ�����Ա�嵥! �����ʾ������Ա�Ľ�ɫ!!��Ϊѡ��ʱ��Ҫ!!!
	 * Ȼ����ʱ�ͽ��������ӽ���!!���Զ�ѡ��!!! ����֮ǰҪ�ȼ���һ��,ҳ�������޴���,������,����ʾ���������!!!
	 * ��û���һ��ҳǩ,��ʾ�����������߹�����Ա!!���������Ի�,��Ϊʵ������Ǿ�������Ҫ�ύ�������߹�����Ա
	 */
	private void onAddReceiver() {
		dealAddReceiveAndCCToUser("����µĽ�����Ա", false); //
	}

	/**
	 * ��ӳ�����Ա
	 */
	private void onAddCCToUser() {
		dealAddReceiveAndCCToUser("����µĳ�����Ա", true); //
	}

	//������Ա!!
	private void dealAddReceiveAndCCToUser(String _title, boolean _isCC) {
		if (freeAddUserdialog == null) {
			freeAddUserdialog = new AddNewUserDialog(this, this.billVO); //Ϊ���������,��Dialog��������,�ڶ��ε��ʱ�ر��!!!
		}
		freeAddUserdialog.setTitle(_title); //
		freeAddUserdialog.setVisible(true); //
		if (freeAddUserdialog.getCloseType() == AddNewUserDialog.CONFIRM) {//sunfujun/20120717/�޸�ѡ����֮���XҲ����˵�bug
			//BillVO corpVO = freeAddUserdialog.getReturnCorpVO(); //
			BillVO[] userVOs = freeAddUserdialog.getReturnUserVOs(); //ѡ�е���Ա!!
			DealTaskVO[] taskVOS = new DealTaskVO[userVOs.length]; //
			for (int i = 0; i < taskVOS.length; i++) {
				taskVOS[i] = new DealTaskVO(); //
				taskVOS[i].setFromActivityId(currActivityVO.getFromActivityId()); //��Դ����id
				taskVOS[i].setFromActivityName(currActivityVO.getFromActivityName()); //��Դ����Name

				taskVOS[i].setTransitionId(currActivityVO.getFromTransitionId()); //���ĸ��߹�����
				taskVOS[i].setTransitionCode(currActivityVO.getFromTransitionCode()); //���߱���
				taskVOS[i].setTransitionName(currActivityVO.getFromTransitionName()); //��������
				taskVOS[i].setTransitionDealtype(currActivityVO.getFromtransitiontype()); //
				taskVOS[i].setTransitionIntercept(currActivityVO.getFromTransitionIntercept()); //
				taskVOS[i].setTransitionMailSubject(currActivityVO.getFromTransitionMailsubject()); //
				taskVOS[i].setTransitionMailContent(currActivityVO.getFromTransitionMailcontent()); //

				taskVOS[i].setCurrActivityId(currActivityVO.getActivityId()); //��ǰ����id
				taskVOS[i].setCurrActivityCode(currActivityVO.getActivityCode()); //����Code
				taskVOS[i].setCurrActivityName(currActivityVO.getActivityName()); //����Name
				taskVOS[i].setCurrActivityType(currActivityVO.getActivityType()); //����,��������ͨ/����/����
				taskVOS[i].setCurrActivityApproveModel(currActivityVO.getApprovemodel()); //��������ģʽ!!

				taskVOS[i].setParticipantUserId(userVOs[i].getStringValue("userid")); //
				taskVOS[i].setParticipantUserCode(userVOs[i].getStringValue("usercode")); //
				taskVOS[i].setParticipantUserName(userVOs[i].getStringValue("username")); //

				taskVOS[i].setParticipantUserRoleId(userVOs[i].getStringValue("userroleid")); //��Ա��ɫ
				taskVOS[i].setParticipantUserRoleCode(userVOs[i].getStringValue("userrolename")); //��ɫ����
				taskVOS[i].setParticipantUserRoleName(userVOs[i].getStringValue("userrolename")); //��ɫ����

				taskVOS[i].setParticipantUserDeptId(userVOs[i].getStringValue("userdeptid")); //����id
				taskVOS[i].setParticipantUserDeptCode(userVOs[i].getStringValue("userdeptcode")); //��������
				taskVOS[i].setParticipantUserDeptName(userVOs[i].getStringValue("userdeptname")); //��������

				taskVOS[i].setAccrUserId(userVOs[i].getStringValue("accruserid")); //��Ȩ��,Ӧ������Ȩ�˵�!
				taskVOS[i].setAccrUserCode(userVOs[i].getStringValue("accrusercode")); //��Ȩ�˱���,Ӧ������Ȩ�˵�!
				taskVOS[i].setAccrUserName(userVOs[i].getStringValue("accrusername")); //��Ȩ������,Ӧ������Ȩ�˵�!

				taskVOS[i].setCCTo(_isCC); //
				taskVOS[i].setRejectedDirUp(false); //

				taskVOS[i].setIseverprocessed("N"); //�Ƿ������߹�
				taskVOS[i].setSuccessParticipantReason("�������"); //�ɹ���ԭ��
			}

			setDealTaskVOs(taskVOS, true); //��������!!! 
			//�ʴ�ϣ��ѡ�е����Զ����ϻ�ѡ������ǵ�ѡ�ҿ�����������������˶����ֻ��ѡ�����һ�������ϲ�����/sunfujun/20130217
		}
	}

	/**
	 * �����ӽ�����Ա�ͳ�����Ա��ť���߼�!!!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_addReceiver) {
			onAddReceiver(); //��ӽ�����Ա!!!
		} else if (e.getSource() == btn_addCCToUser) {
			onAddCCToUser(); //
		} else if (e.getSource() == btn_delReceiver) {
			onDeleteRow(table1); //
		} else if (e.getSource() == btn_delCCToUser) {
			onDeleteRow(table2); //
		}
	}

	/**
	 * ����Ա����з������д���������!!
	 * @return
	 */
	public DealTaskVO[] getSelectedTaskVOs() {
		BillVO[] billVOs_1 = billListPanel_1.getSelectedBillVOs(true); //ȡ��һ���е���Ա!! һ��Ҫ�ĳ�Checked,����б�ѡ��ʽ�ǹ�ѡ��ʽ����ȡ�ù�ѡ�ļ�¼�����/2012-03-01��
		if (billVOs_1 == null || billVOs_1.length == 0) {
			MessageBox.show(this, "��ѡ�С�������Ա��!" + (billListPanel_1.isRowNumberChecked() ? "\r\n��ע��:ֻ��ѡ��ǰ��Ĺ�ѡ�����������ѡ��!" : "")); //
			return null;
		}

		//���ѡ�е��û�����Ȩ���,������һ��!!
		HashSet hst_allaccrUser = new HashSet(); //
		for (int i = 0; i < billListPanel_1.getRowCount(); i++) {
			String str_accruser = billListPanel_1.getRealValueAtModel(i, "accruserid"); //
			if (str_accruser != null) {
				hst_allaccrUser.add(str_accruser); //
			}
		}
		StringBuilder sb_allAccrUserNames = new StringBuilder(); //
		for (int i = 0; i < billVOs_1.length; i++) {
			String str_userid = billVOs_1[i].getStringValue("userid"); //
			String str_userName = billVOs_1[i].getStringValue("username"); //
			String str_accruserid = billVOs_1[i].getStringValue("accruserid"); //��Ȩ��
			if (hst_allaccrUser.contains(str_userid) || hst_allaccrUser.contains(str_accruserid)) { //
				sb_allAccrUserNames.append("��" + str_userName + "��"); //
			}
		}
		if (!sb_allAccrUserNames.toString().equals("")) { //��������ñ���Ȩ��Ҳ�ܽ���,����ѡ��***(����Ȩ***)���ӵ���Ա
			if (JOptionPane.showConfirmDialog(this, "��ѡ��������û�����Ȩ����:" + sb_allAccrUserNames.toString() + "\r\n��ȷ���ύ�Ƿ���ȷ?\r\n", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return null;
			}
		}

		ArrayList al_users = new ArrayList(); //
		ArrayList al_VOs = new ArrayList(); //
		for (int i = 0; i < billVOs_1.length; i++) { //
			if (al_users.contains(billVOs_1[i].getStringValue("userid"))) { //usercode,username
				MessageBox.show(this, "���ظ�ѡ���˶����Ϊ��" + billVOs_1[i].getStringValue("usercode") + "/" + getRealUserName(billVOs_1[i].getStringValue("username")) + "���Ľ�����Ա!\r\n���ǲ������,������ѡ�������Ա!"); //
				return null;
			}
			al_users.add(billVOs_1[i].getStringValue("userid")); //
			al_VOs.add(convertBillVOToTaskVO(billVOs_1[i], false)); //
		}

		BillVO[] billVOs_2 = billListPanel_2.getSelectedBillVOs(true); //���볭����Ա����Ϣ!!!����б�ѡ��ʽ�ǹ�ѡ��ʽ����ȡ�ù�ѡ�ļ�¼�����/2012-03-01��
		for (int i = 0; i < billVOs_2.length; i++) {
			if (al_users.contains(billVOs_2[i].getStringValue("userid"))) { //usercode,username
				MessageBox.show(this, "���ظ�ѡ���˶����Ϊ��" + billVOs_2[i].getStringValue("usercode") + "/" + getRealUserName(billVOs_2[i].getStringValue("username")) + "���ĳ�����Ա(���������Ա�ظ�)!\r\n���ǲ������,������ѡ������Ա!"); //
				return null;
			}
			al_users.add(billVOs_2[i].getStringValue("userid")); //�����и�bug����ǰ��al_users.add(billVOs_1[i].getStringValue("userid"));  billvo����ˣ����±�����Խ���쳣�����/2012-03-15��
			al_VOs.add(convertBillVOToTaskVO(billVOs_2[i], true)); //
		}
		return (DealTaskVO[]) al_VOs.toArray(new DealTaskVO[0]); //
	}

	private String getRealUserName(String _name) {
		if (_name.indexOf("(") > 0) {
			return _name.substring(0, _name.indexOf("(")); //
		} else {
			return _name; //
		}
	}

	/**
	 * ��ҳ���ϵ�BillVOת���ɹ��������������VO
	 * @return
	 */
	private DealTaskVO convertBillVOToTaskVO(BillVO _billVO, boolean _isCCTo) {
		DealTaskVO taskVO = new DealTaskVO(); //
		taskVO.setParticipantUserId(_billVO.getStringValue("userid")); //
		taskVO.setParticipantUserCode(_billVO.getStringValue("usercode")); //
		taskVO.setParticipantUserName(_billVO.getStringValue("username")); //

		taskVO.setParticipantUserDeptId(_billVO.getStringValue("userdeptid")); //
		taskVO.setParticipantUserDeptCode(_billVO.getStringValue("userdeptcode")); //
		taskVO.setParticipantUserDeptName(_billVO.getStringValue("userdeptname")); //

		taskVO.setParticipantUserRoleId(_billVO.getStringValue("userroleid")); //
		taskVO.setParticipantUserRoleCode(_billVO.getStringValue("userrolecode")); //
		taskVO.setParticipantUserRoleName(_billVO.getStringValue("userrolename")); //

		taskVO.setAccrUserId(_billVO.getStringValue("accruserid")); //��Ȩ������.
		taskVO.setAccrUserCode(_billVO.getStringValue("accrusercode")); //��Ȩ�˱���.
		taskVO.setAccrUserName(_billVO.getStringValue("accrusername")); //��Ȩ������.

		taskVO.setFromActivityId(_billVO.getStringValue("fromactivityid")); //
		taskVO.setFromActivityName(_billVO.getStringValue("fromactivityname")); //

		taskVO.setTransitionId(_billVO.getStringValue("transitionid")); //
		taskVO.setTransitionCode(_billVO.getStringValue("transitioncode")); //
		taskVO.setTransitionName(_billVO.getStringValue("transitionname")); //
		taskVO.setTransitionDealtype(_billVO.getStringValue("transitionDealtype")); //
		taskVO.setTransitionIntercept(_billVO.getStringValue("transitionIntercept")); //
		taskVO.setTransitionMailSubject(_billVO.getStringValue("transitionMailSubject")); //
		taskVO.setTransitionMailContent(_billVO.getStringValue("transitionMailContent")); //

		taskVO.setCurrActivityId(_billVO.getStringValue("currActivityid")); //
		taskVO.setCurrActivityCode(_billVO.getStringValue("currActivitycode")); //
		taskVO.setCurrActivityName(_billVO.getStringValue("currActivityname")); //
		taskVO.setCurrActivityType(_billVO.getStringValue("currActivitytype")); //
		taskVO.setCurrActivityApproveModel(this.currActivityVO.getApprovemodel()); //����ģʽ  

		taskVO.setCCTo(_isCCTo); //�Ƿ��ǳ���ģʽ
		return taskVO; //
	}

	public BillListPanel getBillListPanel() {
		return billListPanel_1;
	}

	public BillListPanel getBillListPanel2() {
		return billListPanel_2;
	}

	public void setBillVO(BillVO _billVO) {
		this.billVO = _billVO; //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil(); //
		return tbUtil;
	}

}
