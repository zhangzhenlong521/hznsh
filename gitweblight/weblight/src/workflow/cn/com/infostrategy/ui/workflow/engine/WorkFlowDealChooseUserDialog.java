package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.ActivityVOComparator;
import cn.com.infostrategy.to.workflow.engine.DealActivityVO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RecordShowDialog;

/**
 * ������UI�˺�����!!�������Ǽ�!!�������,��֮��������һ����Ҫ����ParticipateUserPanel
 * ��:������ѡ�������!!ʹ���б�ѡ��!! ����ʵ�ǹ�����������Ҫ��һ������!!Ҳ�����Ի�Ҫ����ߵĽ���!!!
 * ��Ҫ��һ������Ż�,����Ҫ�г�������+��,���û�����ѡ����,��һ��Ա�䶯,����ɫû�м�ʱͬ������,�򻹿��������ְ취����ѡ����,��������!!
 * @author xch
 *
 */
public class WorkFlowDealChooseUserDialog extends BillDialog implements ActionListener, WindowListener, MouseListener {

	private static final long serialVersionUID = 8323211408012241414L;
	private BillVO billVO = null; //ҵ������VO.
	private WFParVO firstTaskVO = null; //����!!����������!!!
	private boolean isStartStep = false; //�Ƿ�����������?
	private DealActivityVO[] dealactivityVOS = null; //

	private JPanel activityButtonsPanel = null; //���ڰ�ť���!!!

	private JTabbedPane tabbedPane_activity = null; //���ؼ������ڵ�!!!
	private JButton btn_confirm, btn_monitor, btn_cancel, btn_return; ////
	private int closetype = -1; //

	private JPopupMenu popMenu = null; //
	private TBUtil tbUtil = null; //

	private ImageIcon imgSelected = UIUtil.getImage("office_036.gif"); //
	private ImageIcon imgUnSelected = UIUtil.getImage("office_138.gif"); //
	private ImageIcon imgSelfCycle = UIUtil.getImage("office_036.gif"); //��ѭ��������

	private StringBuilder sb_allHelpMsg = new StringBuilder(); //

	private HashVO[] currActSelfCycleRoleMapVOs = null; //��ǰ�����������ѭ��,����ѭ�������ɫ��,������������!!
	private String[][] selfCycleCurrRoleCodeName = null; //
	private HashVO hvoCurrWFInfo = null;//��ǰ������Ϣ,�����ݵ���������ҵ������,���������ʲô����,�������ж�����ʲô�����������/2016-04-20��
	private HashVO hvoCurrActivityInfo = null;//��ǰ������Ϣ

	/**
	 * 
	 * @param _parent
	 * @param _billVO
	 * @param _firstTaskVO
	 * @param _dealtype
	 * @param _isStartStep
	 * @param _hvoCurrWFInfo ��ǰ������Ϣ
	 * @param _hvoCurrActivityInfo ��ǰ������Ϣ�����/2016-04-20��
	 */
	public WorkFlowDealChooseUserDialog(Container _parent, BillVO _billVO, WFParVO _firstTaskVO, String _dealtype, boolean _isStartStep, HashVO _hvoCurrWFInfo, HashVO _hvoCurrActivityInfo) {
		super(_parent, "ѡ�������", 860, 600); //nagive
		if (ClientEnvironment.isAdmin()) {
			this.setTitle("ѡ�������[ǿ������:�Ҽ�ҳǩ���Բ鿴�����߶�����Ϣ,˫����¼���Բ鿴�ɹ������ԭ��!]"); //
		}
		this.billVO = _billVO; //
		this.firstTaskVO = _firstTaskVO; //
		this.isStartStep = _isStartStep; //�Ƿ����������ڲ���
		this.firstTaskVO.setStartStep(_isStartStep); //���������Ҳָ�����Ƿ����������裡����
		this.hvoCurrWFInfo = _hvoCurrWFInfo;
		this.hvoCurrActivityInfo = _hvoCurrActivityInfo;
		this.setResizable(true); //
		this.addWindowListener(this);
		initialize();
	}

	/**
	 * ��ʼ��ҳ��..
	 */
	private void initialize() {
		try {
			dealactivityVOS = this.firstTaskVO.getDealActivityVOs(); //�Ӻ�̨���������ص����д����ڵĶ���!!!
			if (dealactivityVOS.length > 1) { //������������ϵĻ���
				Arrays.sort(dealactivityVOS, new ActivityVOComparator()); //����ж������,�򰴻��ڱ�������!!!����Ҫ����ѭ������Զ���ڵ�һλ!!
			}
			boolean isTwoStepCommit = getTBUtil().getSysOptionBooleanValue("�������ύʱ�Ƿ��Ȼ��ں���Ա", false); //�Ƿ������ύ,��ҵ���еȿͻ�ϲ����ѡ����,��ѡ��! ������OAһ��!��Ϊ����һ��������ѡ����! ���Ҳ��������ֱ���ύ�������ĸ���!!
			if (dealactivityVOS.length > 1 && isTwoStepCommit) { //����Ƕ������,�����Ƿ������ύ,��ʹ�ò�,Ȼ���ȴ������ڰ�ť,�����ֻ��һ��������û�����������,��Ϊ������!!
				this.getContentPane().setLayout(new BorderLayout()); //
				this.getContentPane().add(getActivityButtonPanel(dealactivityVOS)); //
			} else { //��ͳ�Ķ�ҳǩ!!!
				this.getContentPane().setLayout(new BorderLayout()); //
				this.getContentPane().add(getActivityTabbedPanel(dealactivityVOS, null, false, false), BorderLayout.CENTER); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private WLTButton getClickActivityBtn(String _text, ImageIcon _icon, int _index) {
		WLTButton btn = new WLTButton(_text, _icon); //
		btn.setPreferredSize(new Dimension(250, 45)); //��̫ƽ��Ŀ���������������̫���ỻ�У����ϻ������ƣ�����ռ���£��ʰ�ť�ӿ����/2019-01-07��
		btn.putClientProperty("tab_index", new Integer(_index)); //
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickActivityBtn((WLTButton) e.getSource()); //����¼�
			}
		});
		return btn; //
	}

	/**
	 * ȡ�ø������ڰ�ť���!! ���������ύʱ,��һ������ʾ�������ڴ�ť,���������ʾ�û��ڵĲ�����!!
	 * @param _dealactivityVOs
	 * @return
	 */
	private JPanel getActivityButtonPanel(DealActivityVO[] _dealactivityVOs) { //
		if (activityButtonsPanel != null) {
			return activityButtonsPanel; //
		}
		//�������л��ڣ���
		int li_count = 0; //

		sb_allHelpMsg.append("\r\n\r\n��������������������������컷���߼�[������������,���ܻ���ֶ��!����������������������\r\n\r\n"); //

		ArrayList al_selfCycleBtn = new ArrayList(); //��ѭ���ڲ���ת�İ�ť
		ArrayList al_outoffBtn = new ArrayList(); //��ȥ�İ�ť
		for (int i = 0; i < _dealactivityVOs.length; i++) { //�������л���!
			//���û������,�Ҳ��ǽ�������!
			if ((_dealactivityVOs[i].getDealTaskVOs() == null || _dealactivityVOs[i].getDealTaskVOs().length <= 0) && (!"END".equalsIgnoreCase(_dealactivityVOs[i].getActivityType()))) {
				if (_dealactivityVOs[i].getWnParam() != null && "Y".equalsIgnoreCase((String) _dealactivityVOs[i].getWnParam().get("�޲������Ƿ�����"))) {
					continue; //���û�в�����,������!
				}
			}

			if (i != 0) {
				sb_allHelpMsg.append("\r\n\r\n"); //
			}
			sb_allHelpMsg.append("�����[" + (i + 1) + "]�����컷��:\r\n"); ////

			//System.out.println("���ڡ�" + _dealactivityVOs[i].getCurrActivityName(false) + "������չ������" + _dealactivityVOs[i].getFromtransitionExtConfMap()); //  //
			//�����ǰ��������ѭ,�����������ʵ���Ǳ���ѭ�����ڣ�����
			if (this.firstTaskVO.getCurractivityIsSelfCycle() && _dealactivityVOs[i].getSortIndex() == 0) { //�����ǰ����ѭ��������
				String[][] str_selfLinkRoles = getSelfCycleRoleLinkMaps(); //ȡ�õ�¼��Ա�����ߵ����н�ɫ!!!
				if (str_selfLinkRoles != null && str_selfLinkRoles.length > 0) { //Ҫ��������Լ�ѭ�����׿����߼����ڲ����̵Ľ�ɫ!
					//System.out.println("ѭ������[" + str_selfLinkRoles.length + "]����ť"); //
					for (int j = 0; j < str_selfLinkRoles.length; j++) {
						WLTButton btn = getClickActivityBtn(str_selfLinkRoles[j][3], imgSelfCycle, i); //��ť���ƾ��ǽ�ɫ����!
						btn.putClientProperty("SelfCycleBtn", "Y"); //
						btn.putClientProperty("BindSelfCycleFromRoleCode", str_selfLinkRoles[j][0]); //�󶨵Ľ�ɫ���ƣ���
						btn.putClientProperty("BindSelfCycleFromRoleName", str_selfLinkRoles[j][1]); //�󶨵Ľ�ɫ���ƣ���
						btn.putClientProperty("BindSelfCycleToRoleCode", str_selfLinkRoles[j][2]); //�󶨵Ľ�ɫ���ƣ���
						btn.putClientProperty("BindSelfCycleToRoleName", str_selfLinkRoles[j][3]); //�󶨵Ľ�ɫ���ƣ���

						if (ClientEnvironment.isAdmin()) { //����ǹ������
							StringBuilder sb_tip = new StringBuilder(); //
							sb_tip.append("<html>"); //
							sb_tip.append(_dealactivityVOs[i].getActivityBelongDeptGroupName() + "-" + _dealactivityVOs[i].getActivityName() + "<br>"); //
							sb_tip.append("��Դ��ɫ:��" + str_selfLinkRoles[j][0] + "/" + str_selfLinkRoles[j][1] + "��<br>"); //
							sb_tip.append("Ŀ���ɫ:��" + str_selfLinkRoles[j][2] + "/" + str_selfLinkRoles[j][3] + "��<br>"); //
							sb_tip.append("</html>"); //
							btn.setToolTipText(sb_tip.toString()); //
						} else {
							btn.setToolTipText(_dealactivityVOs[i].getCurrActivityName(true)); //
						}
						al_selfCycleBtn.add(btn); //
						li_count++; //
					}
				} else { //���û�ҵ�,�򲻼����κΰ�ť!�����ݼ���,����ɫû��һ�������ύ��!!!��������ǰ�ķ�ʽ!!
					WLTButton btn = getClickActivityBtn(_dealactivityVOs[i].getCurrActivityName(true), imgSelfCycle, i); //imgUnSelected
					btn.putClientProperty("SelfCycleBtn", "Y"); //

					btn.setToolTipText("���Ǹ���ѭ������,��û�ж����ڲ����̵Ľ�ɫӳ������ͼ!"); //
					al_selfCycleBtn.add(btn); //
					li_count++; //
				}

				if (isStartStep) { //������������������������,�������ľͲ���ȥ��!
					sb_allHelpMsg.append("��Ϊ��ǰ����������,��������ѭ��,���Ժ������������ֱ�Ӳ������..\r\n"); //
					break; //�����ĳ�ȥ���߾Ͳ�����!��ֻ���������ŵ�!
				}
			} else { //����ǳ�ȥ�Ļ���!�������ڲ���ת��!!
				//�����������������ѭ����Ӧ��"С����"�е����н�ɫ�������end���Ƶ�,������˿���:ֻ�е�ǰ�Ľ�ɫ��end��ָ����,�����ʾ��������!!!!
				//����ͳͳ����ʾ��������!
				HashSet hst_ends = getSelfCycleRoleWFEnds(); //���ж����ǽ�������
				if (hst_ends != null && hst_ends.size() > 0) { //������־�Ȼ�����˽�������!���жϱ��˻����Ƿ��������Щ����������! �����! ˵��,ֻ�п��Գ�ȥ! ���û��! ��ֱ��
					String[] str_ayy = (String[]) hst_ends.toArray(new String[0]); //
					String str_endname = ""; //
					for (int k = 0; k < str_ayy.length; k++) {
						str_endname = str_endname + "��" + str_ayy[k] + "��";
					}
					sb_allHelpMsg.append("��ǰ����[" + _dealactivityVOs[i].getCurrActivityName(false) + "]����ѭ��,�Ұ󶨵Ľ�ɫӳ��ͼ���н����Ľ�ɫ[" + str_endname + "]����!!����м���..." + "\r\n"); //
					boolean isInEnd = isMyInEnds(hst_ends); //�Ƿ���ĳ��!
					if (!isInEnd) { ////���ָ���˽���������,�����ֲ�������!!��ֱ���˳�!
						sb_allHelpMsg.append("���ְ󶨵Ľ�ɫӳ��ͼ���н����Ľ�ɫ����!!���Ҳ�������,˵������û��\"�ʸ�\"��ȥ��,����ֱ���޳����Գ�ȥ��·��.." + "\r\n"); //
						break; //ֱ���˳�,�����а�ť�������!!
					} else {
						sb_allHelpMsg.append("���ְ󶨵Ľ�ɫӳ��ͼ���н����Ľ�ɫ����!!����������,˵���������ʸ��ȥ��,��������������ϵ���������.." + "\r\n"); //
					}
				}

				WLTButton outPutButton = null; //
				//���������ʾ��������,���ټ��������������!!�����������߼�!!!�������������û������������ڽ������ڵ���!��"С����"��һ��ָ�����Խ�����"��ɫ",����Ǹ��ܿ���,һ���Ӿ͹ص���!!!
				if (_dealactivityVOs[i].getFromtransitionExtConfMap() != null && _dealactivityVOs[i].getFromtransitionExtConfMap().containsKey("Դͷ���ڳ�ȥ�Ķ��ν�ɫ����")) { //
					String str_outPutRoleCons = (String) _dealactivityVOs[i].getFromtransitionExtConfMap().get("Դͷ���ڳ�ȥ�Ķ��ν�ɫ����"); //
					if (str_outPutRoleCons != null && !str_outPutRoleCons.trim().equals("")) {
						String[] str_roleConItems = getTBUtil().split(str_outPutRoleCons, "/"); //�����ж��!������߿��ܲ�ͨ��
						if (uiSecondCheckActivityByRoleName(str_roleConItems)) { //�ұ��������ֽ�ɫ�ų�����
							outPutButton = getClickActivityBtn(_dealactivityVOs[i].getCurrActivityName(true), imgUnSelected, i); //
							if (ClientEnvironment.isAdmin()) {
								outPutButton.setToolTipText("�û�����ǰ̨��ɫ����[" + str_outPutRoleCons + "],������Ϊƥ��ɹ��ż����!"); //
							}
						} else { //��ɾ����!
							String str_helpText = "���ź������ڡ�" + _dealactivityVOs[i].getCurrActivityName(false) + "���ں�̨�Ѿ��ɹ�ƥ��,����Ϊ�������н�ɫ��������[" + str_outPutRoleCons + "],������û�����ֽ�ɫ,������ǰ̨�������޳���"; //
							sb_allHelpMsg.append(str_helpText + "\r\n"); //
						}
					} else { //���û�ж�������!��ֱ�ӳ�����
						sb_allHelpMsg.append("�����϶��ι�������Ϊ��,��ֱ�Ӽ���\r\n"); //
						outPutButton = getClickActivityBtn(_dealactivityVOs[i].getCurrActivityName(true), imgUnSelected, i); //
					}
				} else { //���û�ж������������!//
					sb_allHelpMsg.append("������û�ж��ι�������,��ֱ�Ӽ���\r\n"); //
					outPutButton = getClickActivityBtn(_dealactivityVOs[i].getCurrActivityName(true), imgUnSelected, i); //
				}

				//
				if (outPutButton != null) {
					//����ǽ�������,��Ҫ�ر���һ��!
					if ("END".equalsIgnoreCase(_dealactivityVOs[i].getActivityType())) { //����ǽ�������
						outPutButton.setText(_dealactivityVOs[i].getActivityName()); //����ǽ�������ֻ��ʾ����!�ʴ��������"���ε"��Ϊ��������ֻҪ����,��Ҫ����!!!
						outPutButton.setForeground(Color.RED); //
						if (_dealactivityVOs[i].getDealTaskVOs() == null || _dealactivityVOs[i].getDealTaskVOs().length <= 0) { //�����������û�в�����,��
							outPutButton.putClientProperty("DirEndWF", "Y"); //��ֱ�ӽ����İ�ť
						}
					}

					//���Ŀ�껷��Ҳ�Ǹ���ѭ��,�������ѭ����Ӧ��С����!!!
					if (_dealactivityVOs[i].isActivityIselfcycle() && _dealactivityVOs[i].getActivitySelfcyclerolemap() != null) {
						outPutButton.putClientProperty("NextStepSelfCycleRoleMapWF", _dealactivityVOs[i].getActivitySelfcyclerolemap()); //
					}

					al_outoffBtn.add(outPutButton); //
					li_count++; //

				}
			}
		}

		boolean isMultiChecked = false; //�Ƿ��Ƕ�ѡ�Ĺ�ѡ��ʽ???�����ֶ�·�ķ�ʽ!!!

		activityButtonsPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		if (al_selfCycleBtn.size() > 0 && al_outoffBtn.size() > 0) { //���������������!!Ҫ�����Ƭ,������ڲ���ת��,�ұ��ǳ�ȥ��!
			JScrollPane left_btnpanel = getBtnsPanel(al_selfCycleBtn, false); //��ߵ���ѭ���İ�ť
			JScrollPane right_btnpanel = getBtnsPanel(al_outoffBtn, isMultiChecked); //�ұߵĳ�ȥ��

			JPanel btn_left = WLTPanel.createDefaultPanel(new BorderLayout()); //
			btn_left.add(left_btnpanel); //
			JLabel label_left = new JLabel("���ڲ���ת�Ĳ��衿", SwingConstants.CENTER); //
			label_left.setForeground(Color.RED); //
			label_left.setPreferredSize(new Dimension(2000, 25)); //
			btn_left.add(label_left, BorderLayout.NORTH); //

			JPanel btn_right = WLTPanel.createDefaultPanel(new BorderLayout()); //
			btn_right.add(right_btnpanel); //
			JLabel label_right = new JLabel("����ȥ�Ĳ��衿", SwingConstants.CENTER); //
			label_right.setForeground(Color.RED); //
			label_right.setPreferredSize(new Dimension(2000, 25)); //
			btn_right.add(label_right, BorderLayout.NORTH); //

			WLTSplitPane split = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, btn_left, btn_right); //���岼��
			split.setDividerLocation(420); //
			activityButtonsPanel.add(split, BorderLayout.CENTER); //
		} else { //���ֻ��һ��������!
			ArrayList al_allBtns = new ArrayList(); //
			al_allBtns.addAll(al_selfCycleBtn); //���Ϊ��,��Ȼ����û������!
			al_allBtns.addAll(al_outoffBtn); //���Ϊ��,��Ȼ����û������!
			activityButtonsPanel.add(getBtnsPanel(al_allBtns, false), BorderLayout.CENTER); //
		}

		activityButtonsPanel.add(getMsgPanel(), BorderLayout.NORTH); //
		activityButtonsPanel.add(getSouthPanel(false, false), BorderLayout.SOUTH); //
		return activityButtonsPanel; //
	}

	private JScrollPane getBtnsPanel(ArrayList _list, boolean _isChecked) {
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20)); //
		btnPanel.setOpaque(false); //͸��!!!
		//btnPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));  //ȥ��ע�ͺ�,Ϊ�˵���ʱ��ʾ��

		if (_list.size() > 0) {
			if (!_isChecked || _list.size() == 1) { //����Ƿǹ�ѡ��ʽ,������ֻ��һ��!��Ϊ��һ��Ҳ��ɹ�ѡģʽ��û�������!
				for (int i = 0; i < _list.size(); i++) {
					WLTButton btnItem = (WLTButton) _list.get(i); //
					btnPanel.add(btnItem); //
				}
			} else { //����ǹ�ѡ��ʽ��!!!
				JCheckBox[] checks = new JCheckBox[_list.size()]; //
				for (int i = 0; i < _list.size(); i++) {
					WLTButton btnItem = (WLTButton) _list.get(i); //�õ���ť!!!
					btnItem.putClientProperty("BindCheckBoxs", checks); //�󶨵����й�ѡ��!!!

					checks[i] = new JCheckBox(); //��ѡ��
					checks[i].setOpaque(false); //͸��
					checks[i].setFocusable(false); //
					checks[i].putClientProperty("tab_index", (Integer) btnItem.getClientProperty("tab_index")); //�ڹ�ѡ����!!
					checks[i].putClientProperty("NextStepSelfCycleRoleMapWF", (String) btnItem.getClientProperty("NextStepSelfCycleRoleMapWF"));

					JPanel tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); //�ٸ�����
					tmpPanel.setOpaque(false); //͸��!!!
					tmpPanel.add(checks[i]);
					tmpPanel.add(btnItem); //

					btnPanel.add(tmpPanel); //
				}
			}

		}

		btnPanel.setPreferredSize(new Dimension(300, 65 * _list.size() + 50)); //

		JPanel btnContainerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)); //�����������!
		btnContainerPanel.setOpaque(false); //͸��!!
		btnContainerPanel.add(btnPanel); //
		//btnContainerPanel.setBorder(BorderFactory.createLineBorder(Color.RED));  //ȥ��ע�ͺ�,Ϊ�˵���ʱ��ʾ��

		JScrollPane scrollPanel = new JScrollPane(btnContainerPanel); //
		scrollPanel.setOpaque(false); //͸��!!
		scrollPanel.getViewport().setOpaque(false); //͸��!!

		return scrollPanel; //
	}

	private String getToRoleName() {
		return this.firstTaskVO.getDealpooltask_selfcycle_torolename(); //Ҫʹ�����ݿ��е�!
	}

	private boolean isMyInEnds(HashSet _hst) {
		String str_toRoleName = getToRoleName(); //���������ۺ�Ա!
		if (str_toRoleName != null) {
			if (_hst.contains(str_toRoleName)) { //���������
				//sb_allHelpMsg.append("isInEnd����,�Ҵ����ݿ���ȡ�õ�roleName[" + str_toRoleName + "]ƥ������...\r\n"); //
				return true;
			} else {
				//sb_allHelpMsg.append("isInEnd����,�Ҵ����ݿ���ȡ�õ�roleName[" + str_toRoleName + "]ƥ�䲻��...\r\n"); //
				return false; //
			}
		} else {
			String[] str_myAllRoles = ClientEnvironment.getCurrLoginUserVO().getAllRoleNames(); //��ɫ����!
			if (str_myAllRoles == null || str_myAllRoles.length <= 0) {
				return false;
			}

			String[] str_endRoles = (String[]) _hst.toArray(new String[0]); //
			for (int i = 0; i < str_myAllRoles.length; i++) {
				for (int j = 0; j < str_endRoles.length; j++) {
					if (str_myAllRoles[i].indexOf(str_endRoles[j]) >= 0) { //����ҵĽ�ɫ�д�
						sb_allHelpMsg.append("isInEnd����,�ҵ�һ������=[" + str_myAllRoles[i] + "]ƥ������...\r\n"); //
						return true; //
					}
				}
			}
		}
		return false; //
	}

	//��ѭ������ʱ,����Ҫ֪���Լ�������˭,�������һ���ܵõ�,������һ�ε�,�����һ��û��,���¼��Ա���н�ɫ��ȡ!!
	//�ӵ�¼��Ա������ȡʱ,��Ҫ��һ������,����ֻ���ص�һ����ɫ!!!�⽫������һ����ͬʱ��"���Ҹ�����"��"һ��Ա��"ʱ,ֻ�����Ҹ�����!!
	private String[][] getSelfCycleCurrRoleCodeName() {
		if (selfCycleCurrRoleCodeName != null) { //Ϊ�˵ø�����,ֱ�ӷ���!
			return selfCycleCurrRoleCodeName;
		}

		String str_dbRoleCode = this.firstTaskVO.getDealpooltask_selfcycle_torolecode(); //
		String str_dbRoleName = this.firstTaskVO.getDealpooltask_selfcycle_torolename(); //
		if (str_dbRoleCode != null) { //������ݿ��д���,��ֱ�ӷ���!!!
			selfCycleCurrRoleCodeName = new String[][] { { str_dbRoleCode, str_dbRoleName } }; //
			sb_allHelpMsg.append("������ݿ��еõ���ǰ�˵�ƥ��������תС�����е�Ψһ��ɫ��[" + str_dbRoleCode + "][" + str_dbRoleName + "]\r\n"); //
			return selfCycleCurrRoleCodeName; //
		}

		String[][] str_myAllRoles = ClientEnvironment.getCurrLoginUserVO().getAllRoleCodeNames(); //��ɫ����!
		if (str_myAllRoles == null || str_myAllRoles.length <= 0) {
			return new String[0][0]; //
		}

		String[] str_orderCons = new String[] { "���Ÿ�����", "���Ҹ�����", "�ۺ�Ա", "Ա��" }; //��������
		if (str_orderCons != null) { //�������������
			for (int i = 0; i < str_orderCons.length; i++) {
				for (int j = 0; j < str_myAllRoles.length; j++) {
					if (str_myAllRoles[j][1].indexOf(str_orderCons[i]) >= 0) { //����ж����ɫ,��ֻ���һ��!�����Ͳ��������Ϊ��ǰ���ж����ɫʱ,����һ��!!!!
						selfCycleCurrRoleCodeName = new String[][] { { str_myAllRoles[j][0], str_myAllRoles[j][1] } }; //
						sb_allHelpMsg.append("��ӵ�¼�˵����н�ɫ���ҵ�������ȼ��Ľ�ɫ:[" + str_myAllRoles[j][0] + "][" + str_myAllRoles[j][1] + "]\r\n"); //
						return selfCycleCurrRoleCodeName; //
					}
				}
			}
		}

		sb_allHelpMsg.append("�ﷵ�ص�¼�˵����н�ɫ!!һ��[" + str_myAllRoles.length + "]��");
		selfCycleCurrRoleCodeName = str_myAllRoles; //
		return selfCycleCurrRoleCodeName; //
	}

	//ȡ�õ�¼��Ա��ѭ��ʱ,��ɫӳ�䣡��
	private String[][] getSelfCycleRoleLinkMaps() {
		try {
			//�߼���:�ȿ���ǰ������ѭ���󶨵Ľ�ɫӳ�������ͼ!
			//Ȼ����ѯ�������ͼ���л���,Ȼ�����ÿһ����,���ߵ���Դ���ߵĽ���������ʲô!Ȼ����Դ���ھ�key,�������ھ���value
			//Ȼ���ҳ��������н�ɫ,Ȼ�������������,ֻҪ�ҵĽ�ɫ����һ��indexOf(ͼ�е�key)>0,�����һ��Map! ��󷵻����Map������Value����
			String str_roleMapProcessid = this.firstTaskVO.getCurractivitySelfCycleRoleMap(); //��ǰ���ڵĽ�ɫӳ��Ļ��ڣ���
			if (str_roleMapProcessid == null || str_roleMapProcessid.trim().equals("")) {
				return null; //���û�ж���,��ֱ�ӷ���!!
			}

			//����Ӧ�ò��Ǹ��ݵ�ǰ��¼�˵Ľ�ɫ���ж�!! ���Ǹ��������������ݿ��еĵ�ǰ����"torolename"�ֶ����ж�!!
			//��ǰ����ύ����ʱ,����Ȼ�кܶ��ɫ,�������"�ۺ�Ա"����"���Ҹ�����",����ʱǰ�ߵ������"���Ҹ�����"!!!���������Ӧ����Ϊ��ֻ��"���Ҹ�����"��һ����ɫ��!�����Ƕ����ɫ!!!
			//��Ȼ,���ǰ���Ǵ����滷������,Ȼ��ѡ������,��ʱtorolename��Ϊ�յ�! ��ʱֻ�ܻ���ʹ�õ�¼�����н�ɫ����!!

			//�������� ������������ʱ,������ֳ�ȥ�Ļ��ھ���һ����ѭ��,��ͨ����ѯ���ݿ�õ�����������,��ʱӦ�ð���Щ�������rolename���,��ʱֻҪ��������ֻ��һ��(�����ۺ�Ա)!!����Ȼ��׼ȷ�������ǰӦ�ó���ʲô���ڵ�!
			//��ֻ�����ۺ�Աָ����·��,���������,��Ϊ��ͬʱ��һ��Ա�����ۺ�Ա,���������һ��!!!  kkkkkkkkkkk

			String[][] str_currRoleCodeNames = getSelfCycleCurrRoleCodeName(); //���������ۺ�Ա!
			HashVO[] hvs_roleMapVOs = getCurrActivitySelfCycleMaps(); //ȡ�õ�ǰ����VO,����С����ͼ�ж����"ʲô��ɫ"������ʲô��ɫ��!!!
			if (hvs_roleMapVOs.length <= 0) { //���ܵ�ǰ���ڸ�����û�ж���!!
				return null; //
			}
			ArrayList al_list = new ArrayList(); //
			for (int i = 0; i < hvs_roleMapVOs.length; i++) { //����!
				boolean isFind = false; //�Ƿ���
				for (int j = 0; j < str_currRoleCodeNames.length; j++) { //
					if (str_currRoleCodeNames[j][1].indexOf(hvs_roleMapVOs[i].getStringValue("fromname")) >= 0) { //�ҵĽ�ɫ�������һ��,�����ҵĽ�ɫ�С��������д��Ҹ����ˡ�,�������ƽС����Ҹ����ˡ�
						String str_fromCode = hvs_roleMapVOs[i].getStringValue("fromcode"); //Ӧ�þ�������ͼ�������������´�ƥ���׼ȷ!!
						String str_fromName = hvs_roleMapVOs[i].getStringValue("fromname");
						String str_toCode = hvs_roleMapVOs[i].getStringValue("tocode"); //����������ͼ�е�����!
						String str_toName = hvs_roleMapVOs[i].getStringValue("toname"); //
						al_list.add(new String[] { str_fromCode, str_fromName, str_toCode, str_toName }); //

						String str_helpText = "��ѭ���н��н�ɫӳ�����,���˽�ɫ[" + str_currRoleCodeNames[j][1] + "],��Ϊƥ���Ͻ�ɫ[" + hvs_roleMapVOs[i].getStringValue("fromname") + "],�����ҵ���һ����ɫ[" + hvs_roleMapVOs[i].getStringValue("toname") + "]"; //
						sb_allHelpMsg.append(str_helpText + "\r\n"); //����һ��Ҫ��һ����ɫ֮�������!�����һ����ͬʱ���С����Ҹ����ˡ����ۺ�Ա����һ��Ա����,����Ϊ�����Ǵ��Ҹ����ˣ�
						break; //
					}
				}
			}
			String[][] str_rt = new String[al_list.size()][4]; //
			for (int i = 0; i < str_rt.length; i++) {
				str_rt[i] = (String[]) al_list.get(i); //
			}
			return str_rt; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	//ȡ�����н����Ļ���!!
	private HashSet getSelfCycleRoleWFEnds() {
		try {
			HashVO[] hvs = getCurrActivitySelfCycleMaps(); //
			if (hvs.length <= 0) {
				return null; //
			}
			HashSet hst = new HashSet(); //
			for (int i = 0; i < hvs.length; i++) {
				if (hvs[i].getStringValue("totype", "").equalsIgnoreCase("END") || hvs[i].getBooleanValue("toisend", false)) { //���End����,���а�·����!�����!
					hst.add(hvs[i].getStringValue("toname")); //
				}
			}
			return hst; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	//�������ǰ���ڵ���ѭ������!Ϊ�˱�ֻ֤ȡһ��!
	private HashVO[] getCurrActivitySelfCycleMaps() throws Exception { //
		if (currActSelfCycleRoleMapVOs != null) {
			return currActSelfCycleRoleMapVOs; //
		}
		String str_roleMapProcessid = this.firstTaskVO.getCurractivitySelfCycleRoleMap(); //��ǰ���ڵĽ�ɫӳ��Ļ��ڣ���
		if (str_roleMapProcessid == null || str_roleMapProcessid.trim().equals("")) {
			return new HashVO[0]; //���û�ж���,��ֱ�ӷ���!!
		}
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select t1.fromactivity,t2.code fromcode,t2.wfname fromname,t2.activitytype fromtype,t2.canhalfstart fromisstart,t2.canhalfend fromisend,"); //
		sb_sql.append("t1.toactivity,t3.code tocode,t3.wfname toname,t3.activitytype totype,t3.canhalfstart toisstart,t3.canhalfend toisend ");
		sb_sql.append("from pub_wf_transition t1,pub_wf_activity t2,pub_wf_activity t3 ");
		sb_sql.append("where t1.processid=" + str_roleMapProcessid + " ");
		sb_sql.append("and t1.fromactivity = t2.id ");
		sb_sql.append("and t1.toactivity = t3.id "); //
		sb_sql.append("order by tocode "); //����!

		HashVO[] tmpHvs = UIUtil.getHashVoArrayByDS(null, sb_sql.toString()); //
		if (tmpHvs != null && tmpHvs.length > 0) {
			for (int i = 0; i < tmpHvs.length; i++) {
				String str_fromname = tmpHvs[i].getStringValue("fromname"); //
				String str_toname = tmpHvs[i].getStringValue("toname"); //
				tmpHvs[i].setAttributeValue("fromname", trimAndReplace(str_fromname)); //���¸�ֵ!
				tmpHvs[i].setAttributeValue("toname", trimAndReplace(str_toname)); //���¸�ֵ!
			}
		}
		currActSelfCycleRoleMapVOs = tmpHvs; //
		return currActSelfCycleRoleMapVOs; //
	}

	//�������ϵĻ��з���ȥ��!
	private String trimAndReplace(String _str) {
		_str = _str.trim(); //
		_str = getTBUtil().replaceAll(_str, "\r", ""); //
		_str = getTBUtil().replaceAll(_str, "\n", ""); //
		_str = getTBUtil().replaceAll(_str, "��", "("); //
		_str = getTBUtil().replaceAll(_str, "��", ")"); //
		_str = getTBUtil().replaceAll(_str, " ", ""); //
		return _str;
	}

	//����С�����е���������!!!
	private ArrayList getStartRoles(String _processId) { //ȡ��
		try {
			String str_sql = "select wfname from pub_wf_activity where processid=" + _processId + " and (activitytype='START' or canhalfstart='Y')"; //
			String[] str_roles = UIUtil.getStringArrayFirstColByDS(null, str_sql); //
			if (str_roles != null && str_roles.length > 0) {
				for (int i = 0; i < str_roles.length; i++) {
					str_roles[i] = trimAndReplace(str_roles[i]); //һ��Ҫ�滻һ��!��Ϊ�����ڻ��������ϻ���!!
				}
			}
			return new ArrayList(Arrays.asList(str_roles)); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	private String getStrByList(ArrayList _list) {
		if (_list == null) {
			return null;
		}
		StringBuilder sb_text = new StringBuilder(); //
		for (int i = 0; i < _list.size(); i++) {
			sb_text.append("��" + _list.get(i) + "��"); //
		}
		return sb_text.toString(); //
	}

	/**
	 * ���һ��һ����һ�����ڰ�ť!!!!
	 * @param _btn
	 */
	protected void onClickActivityBtn(WLTButton _btn) {
		boolean isDirEndWF = ("Y".equals(_btn.getClientProperty("DirEndWF")) ? true : false); //
		if (isDirEndWF) { //�����ֱ�ӽ����İ�ť,��ֱ�ӽ�������!
			onEndWF(); //
			return; //
		}

		Integer li_index = (Integer) _btn.getClientProperty("tab_index"); ///��Ҫȡ�ü�������Ľ�ɫ����!
		boolean isSelfBtn = ("Y".equals(_btn.getClientProperty("SelfCycleBtn")) ? true : false);//""
		String str_bindFromRoleCode = (String) _btn.getClientProperty("BindSelfCycleFromRoleCode"); //�󶨵Ľ�ɫ���ƣ�����
		String str_bindFromRoleName = (String) _btn.getClientProperty("BindSelfCycleFromRoleName"); //�󶨵Ľ�ɫ���ƣ�����
		String str_bindToRoleCode = (String) _btn.getClientProperty("BindSelfCycleToRoleCode"); //�󶨵Ľ�ɫ���ƣ�����
		String str_bindToRoleName = (String) _btn.getClientProperty("BindSelfCycleToRoleName"); //�󶨵Ľ�ɫ���ƣ�����

		String str_filerRoleName = null; //

		DealActivityVO[] choosedActVOs = null; //ѡ�л���VO
		if (isSelfBtn) { //��������ǵ�����ڲ���ѭ���İ�ť!!�����ύʱ��̨�ͻ��������ݿ�!
			this.firstTaskVO.setSecondIsSelfcycleclick(true); //
			this.firstTaskVO.setSecondSelfcycle_fromrolecode(str_bindFromRoleCode); //
			this.firstTaskVO.setSecondSelfcycle_fromrolename(str_bindFromRoleName); //
			this.firstTaskVO.setSecondSelfcycle_torolecode(str_bindToRoleCode); //
			this.firstTaskVO.setSecondSelfcycle_torolename(str_bindToRoleName); //
			if (str_bindFromRoleCode != null) { //�����ȷ����ĳ����ɫ,�Ž��й���!
				str_filerRoleName = _btn.getText(); //ֱ���ǰ�ť������!!!
			}

			DealActivityVO choosedActivityVO = dealactivityVOS[li_index];
			if (choosedActivityVO.getWnParam() != null) {
				String str_warn = (String) choosedActivityVO.getWnParam().get("ѡ������"); //
				if (str_warn != null && !str_warn.trim().equals("")) {
					MessageBox.showWarn(this, str_warn);
				}
			}
			choosedActVOs = new DealActivityVO[] { dealactivityVOS[li_index] }; //
		} else { //����ǳ�ȥ��!
			this.firstTaskVO.setSecondIsSelfcycleclick(false); //
			this.firstTaskVO.setSecondSelfcycle_fromrolecode(null); //
			this.firstTaskVO.setSecondSelfcycle_fromrolename(null); //
			this.firstTaskVO.setSecondSelfcycle_torolecode(null); //
			this.firstTaskVO.setSecondSelfcycle_torolename(null); //

			JCheckBox[] checks = (JCheckBox[]) _btn.getClientProperty("BindCheckBoxs"); //�󶨵Ĺ�ѡ��!
			if (checks != null) { //��������ǿ��Ա��ֶ�·��ȥ��,����֧����!!
				JCheckBox thisBindChecks = findThisCheck(checks, li_index); //
				thisBindChecks.setSelected(true); //

				JCheckBox[] allCheckeds = findAllCheckeds(checks); //�ҵ����й�ѡ�ϵĹ�ѡ��!!!
				if (!MessageBox.confirm(this, "�˲���֧�ֶ�·�ύģʽ,������һ��ѡ���ˡ�" + allCheckeds.length + "����·,\r\n��ȷ��Ҫ�ύ����Щ��֧��?")) { //
					return;
				}

				choosedActVOs = new DealActivityVO[allCheckeds.length]; //
				for (int i = 0; i < choosedActVOs.length; i++) { ////��������...
					int li_index_item = (Integer) allCheckeds[i].getClientProperty("tab_index"); //
					String str_NextStepSelfCycleRoleMapWF = (String) allCheckeds[i].getClientProperty("NextStepSelfCycleRoleMapWF"); //
					choosedActVOs[i] = dealactivityVOS[li_index_item]; //
				}
			} else {
				//��һ������ѭ���Ƿ���??
				String str_NextStepSelfCycleRoleMapWF = (String) _btn.getClientProperty("NextStepSelfCycleRoleMapWF"); //
				if (str_NextStepSelfCycleRoleMapWF != null) {
					ArrayList al_nextStartRoles = getStartRoles(str_NextStepSelfCycleRoleMapWF); //��ѯ���ݿ�,���Ƿ�����������!!!
					if (al_nextStartRoles != null && al_nextStartRoles.size() > 0) {
						al_nextStartRoles.add("ȡ��"); //
						String[] str_nextRoles = (String[]) al_nextStartRoles.toArray(new String[0]); //
						for (int i = 0; i < str_nextRoles.length; i++) {
							if (i == str_nextRoles.length - 1) {
								str_nextRoles[i] = "[office_078.gif]" + str_nextRoles[i]; //ȡ����ť
							} else {
								str_nextRoles[i] = "[office_036.gif]" + str_nextRoles[i]; //
							}
						}

						int li_return = MessageBox.showOptionDialog(this, "��ȷ��Ҫ��ò����ύ��?\r\n�����������,��ֻ���ύ�ò��ŵ����½�ɫ,��ע����ȷѡ��!", "��ʾ", str_nextRoles); //
						if (li_return < str_nextRoles.length - 1 && li_return >= 0) { //3+1=4    012 bug//sunfujun//20130515
							//MessageBox.show(this, "��ѡ�е���[" + str_nextRoles[li_return] + "]"); //
							String str_chooseBtn = str_nextRoles[li_return]; //
							if (str_chooseBtn.indexOf("]") > 0) {
								str_chooseBtn = str_chooseBtn.substring(str_chooseBtn.indexOf("]") + 1, str_chooseBtn.length()); //ȥ��ǰ���"[office_078.gif]"
							}
							str_filerRoleName = str_chooseBtn; //
						} else { //���
							return; //
						}
					} //���û�ҵ���������!!!�������Ϊ��!���ҳ�ԭ������!
				}

				DealActivityVO choosedActivityVO = dealactivityVOS[li_index]; //ѡ�еĻ���VO...
				if (choosedActivityVO.getWnParam() != null) {
					String str_warn = (String) choosedActivityVO.getWnParam().get("ѡ������"); //
					if (str_warn != null && !str_warn.trim().equals("")) {
						MessageBox.showWarn(this, str_warn);
					}
				}
				choosedActVOs = new DealActivityVO[] { dealactivityVOS[li_index] }; //
			}
		}

		this.getContentPane().removeAll(); //���м������ȥ����
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getActivityTabbedPanel(choosedActVOs, str_filerRoleName, true, isSelfBtn), BorderLayout.CENTER); //
		this.getContentPane().validate(); //
		this.getContentPane().repaint(); //
	}

	private JCheckBox findThisCheck(JCheckBox[] _checks, Integer _index) {
		for (int i = 0; i < _checks.length; i++) {
			Integer li_index = (Integer) _checks[i].getClientProperty("tab_index"); //
			if (li_index.equals(_index)) {
				return _checks[i]; //
			}
		}
		return null; //
	}

	private JCheckBox[] findAllCheckeds(JCheckBox[] _checks) {
		ArrayList al_checks = new ArrayList(); //
		for (int i = 0; i < _checks.length; i++) {
			if (_checks[i].isSelected()) {
				al_checks.add(_checks[i]); //
			}
		}

		return (JCheckBox[]) al_checks.toArray(new JCheckBox[0]); //
	}

	private int getCheckCount(JCheckBox[] _checks) {
		int li_count = 0; //
		for (int i = 0; i < _checks.length; i++) {
			if (_checks[i].isSelected()) {
				li_count++; //
			}
		}
		return li_count;
	}

	/**
	 * ������һ��!!!
	 */
	private void onRetrun() {
		this.getContentPane().removeAll(); //
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getActivityButtonPanel(dealactivityVOS), BorderLayout.CENTER); //
		this.getContentPane().validate(); //
		this.getContentPane().repaint(); //
	}

	/**
	 * ȡ�ø������ڵĶ�ҳǩ�����!!!
	 * һ��ʼ����Զ���Ǹ���ҳǩ,�����ĳ����ȵ㻷�ڰ�ť,��ѡ�û����еĲ�����!!
	 * �ٺ��������˱��ֶ�·,���Թ�ѡ��·!!
	 * @param _dealactivityVOs 
	 * @return
	 */
	private JPanel getActivityTabbedPanel(DealActivityVO[] _dealactivityVOs, String _filterRoleName, boolean _isReturn, boolean _isSelfBtn) {
		JPanel contentPanel = new JPanel(new BorderLayout()); // 

		contentPanel.add(getMsgPanel(), BorderLayout.NORTH); //str_filerRoleName
		tabbedPane_activity = new JTabbedPane(); //��ҳǩ!!!
		tabbedPane_activity.setFocusable(false); //

		JPanel[] panel_activitys = new JPanel[_dealactivityVOs.length]; //���ڵ������
		ParticipateUserPanel[] parUserPanels = new ParticipateUserPanel[_dealactivityVOs.length]; //������������������Ա���!

		//������������!!
		for (int i = 0; i < _dealactivityVOs.length; i++) { //����ÿ������
			DealTaskVO[] taskVOS = _dealactivityVOs[i].getDealTaskVOs(); //�û������еĲ�������!!
			parUserPanels[i] = new ParticipateUserPanel(_dealactivityVOs[i], billVO, hvoCurrWFInfo, hvoCurrActivityInfo, firstTaskVO); //ʹ�øû�����Ϣ�������������,��һ������һ�����������!!
			//parUserPanels[i].getBillListPanel().setToolbarVisiable(false); //����״̬��!!
			if (_dealactivityVOs[i].getShowparticimode() != null && _dealactivityVOs[i].getShowparticimode().equals("3")) {
				parUserPanels[i].getBillListPanel().setItemVisible("iseverprocessed", true); //�Ƿ������߹�
			} else {
				parUserPanels[i].getBillListPanel().setItemVisible("iseverprocessed", false); //
			}

			String str_oldHelp = _dealactivityVOs[i].getParticiptMsg(); //
			String str_mark = "<br><br>���������������߼�������<br>"; //
			int li_pos = str_oldHelp.indexOf(str_mark); //
			if (li_pos > 0) { //��������ӹ�!,��õ��ټ�!!!
				_dealactivityVOs[i].setParticiptMsg(str_oldHelp.substring(0, li_pos) + str_mark); //
			} else {
				_dealactivityVOs[i].setParticiptMsg(str_oldHelp + str_mark); //
			}

			//�������ѭ�����˵�,�������涨����ι��˵Ľ�ɫ������ֻ���ʲô�ˣ�
			if (taskVOS != null && taskVOS.length > 0) { //������������,ѭ��������������!!!
				if (_isSelfBtn) { //����������ѭ���еİ�ť!��ǿ�и��ݰ�ť���ƹ���!!!
					if (_filterRoleName != null) { //������˵Ľ�ɫ��Ϊ�գ������������ֱ�ӵ����
						DealTaskVO[] newTaskVOS = uiSecondFilterTaskByRoleName(taskVOS, new String[] { _filterRoleName }); //
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>����������ѭ���еİ�ť,ǰ̨������ѭ���ϵİ�ť��Ӧ�Ľ�ɫ����" + _filterRoleName + "���ٴν��й��ˣ����˺󹲵õ�[" + newTaskVOS.length + "]��!"); //
						parUserPanels[i].setDealTaskVOs(newTaskVOS, false); //
					} else {
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>����������ѭ���еİ�ť,����ɫ����Ϊ��?(����û��С���̻��ɫ���ò���),�����г����д�����Ա.."); //
						parUserPanels[i].setDealTaskVOs(taskVOS, false); //����һ���տ�!
					}
				} else { //�������������ĳ�ȥ��!!!
					_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>�����Ĳ�����ѭ���е��ڲ���ת��ť,���ǳ�ȥ�ⲿ�İ�ť,������������Ŀ�껷�ڽ��м���..."); //
					ArrayList al_filterRoleNames = new ArrayList(); //

					//�ȿ�������������!
					if (_dealactivityVOs[i].getFromtransitionExtConfMap() != null && _dealactivityVOs[i].getFromtransitionExtConfMap().containsKey("Ŀ�껷�ڽ���Ķ��ν�ɫ����")) {
						String str_inputRoleFilter = (String) _dealactivityVOs[i].getFromtransitionExtConfMap().get("Ŀ�껷�ڽ���Ķ��ν�ɫ����"); //��Ҫ
						if (str_inputRoleFilter != null && !str_inputRoleFilter.trim().equals("")) { //����ж��壡��
							String[] str_filterRoles = getTBUtil().split(str_inputRoleFilter, "/"); //�ָ�
							al_filterRoleNames.addAll(Arrays.asList(str_filterRoles)); //����!
							_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>��ǰ̨������Դ�����ϵ�������Ŀ�껷�ڽ���Ķ��ν�ɫ���ˡ�=��" + str_inputRoleFilter + "���ٴν��й��ˣ�!"); //
						}
					} else {
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>���ȥ��������û�������[Ŀ�껷�ڽ���Ķ��ν�ɫ����],������������..."); //
					}

					if (_filterRoleName != null) {
						al_filterRoleNames.add(_filterRoleName); //
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>�﷢��Ŀ�껷��ͬ��Ҳ�Ǹ���ѭ��,�Ұ���һ����ɫӳ��[" + _dealactivityVOs[i].getActivitySelfcyclerolemap() + "],��ӳ���\"С����\"������������:" + al_filterRoleNames + ",��ѡ��֮,����й���!!!"); //
					} else { //
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>���ȥ��Ŀ�껷�ڲ�����ѭ������û�ж����ɫӳ��,�����������ι��˼���(�⽫�ҳ�����ԭ�в�����)...");
					}

					if (al_filterRoleNames.size() > 0) { //����й��˵Ľ�ɫ����!!�����
						DealTaskVO[] newTaskVOS = uiSecondFilterTaskByRoleName(taskVOS, (String[]) al_filterRoleNames.toArray(new String[0])); //����������ϵ���Ա���й���!!
						parUserPanels[i].setDealTaskVOs(newTaskVOS, false); //
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>�����������Ŀ�껷����ѭ���е�������ɫ����[����]���˺�,����������:" + getStrByList(al_filterRoleNames) + ",���õ�[" + newTaskVOS.length + "]������(����ǰ��������[" + taskVOS.length + "]��)!");
					} else { //���û��,�������г�����!
						parUserPanels[i].setDealTaskVOs(taskVOS, false); //����һ���տ�!
					}
					parUserPanels[i].getBillListPanel().moveToTop(); //�ö�
					parUserPanels[i].getBillListPanel().clearSelection(); //���ѡ��
				}
			} else { //���û����
				if (_dealactivityVOs[i].getWnParam() != null && "Y".equalsIgnoreCase((String) _dealactivityVOs[i].getWnParam().get("�޲������Ƿ�����"))) {
					//continue;
				} else {
					parUserPanels[i].setDealTaskVOs(taskVOS, false); //����һ���տ�!
				}
			}

			panel_activitys[i] = new JPanel(new BorderLayout()); //ĳһ�����ڵ����
			String str_activitypartmsg = "<html>����ģʽ[" + getApproveModel(_dealactivityVOs[i].getApprovemodel()) + "],�Ƿ���ѭ��[" + (_dealactivityVOs[i].getSortIndex() == 0 ? "��" : "��") + "]"; // 
			if (ClientEnvironment.getInstance().isAdmin()) { //����ǹ���Ա,���һ��!
				String str_tipText = _dealactivityVOs[i].getParticiptMsg().substring(0, _dealactivityVOs[i].getParticiptMsg().indexOf("###")); //
				str_activitypartmsg = str_activitypartmsg + "<br>" + str_tipText + "</html>"; //������Ϣ
			} else { //������ǹ���Ա,��ֱ�Ӽ���</html>
				str_activitypartmsg = str_activitypartmsg + "</html>";
			}
			if (_dealactivityVOs[i].getActivityType() != null && _dealactivityVOs[i].getActivityType().equalsIgnoreCase("END") && parUserPanels[i].getBillListPanel().getRowCount() == 0) { //�����ǰ���������ǽ���
				WLTButton btn_endwf = new WLTButton("<html><center>����������</center></html>"); //
				btn_endwf.setForeground(Color.RED); //
				btn_endwf.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onEndWF(); //
					}
				}); //
				btn_endwf.setPreferredSize(new Dimension(250, 65)); //
				JPanel tempPanel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER, 10, 150)); //
				tempPanel.setOpaque(false); //
				tempPanel.add(btn_endwf); //
				//Box box = new Box(BoxLayout.Y_AXIS); //
				//box.add(Box.createVerticalStrut(150));
				//box.add(tempPanel); //
				panel_activitys[i].add(tempPanel, BorderLayout.CENTER); //ֱ�ӽ���.
			} else {
				//������벿��Ϊ��,��ֻ������Ա!!!!! ����ʱ�еĿͻ�ֻ��ѡ����,����֪��ѡ��������,���ڲ��ż��ύʱ,����������֪�������Ǻ��˽���!!! ��Ҳ�ǹ�������һ����������������,����������ν��?????? ���ѡ���ſ϶�̫��(����ʵ��),Ӧ���ڲ���֮���趨һ��"�ۺϷַ�Ա"��Ȼ�����ۺ�Ա�ַ�!!����ͼ�ж໭һ������!! 					
				panel_activitys[i].add(parUserPanels[i], BorderLayout.CENTER);
			}

			String str_tabTitle = _dealactivityVOs[i].getCurrActivityName(true); //
			tabbedPane_activity.addTab(str_tabTitle, imgUnSelected, panel_activitys[i]); //�����������!!!����һ��ҳǩ���!!!
			tabbedPane_activity.setToolTipTextAt(i, str_activitypartmsg); //

			tabbedPane_activity.putClientProperty("tab_activity_" + i, panel_activitys[i]); //����!!
			tabbedPane_activity.putClientProperty("tab_user_" + i, parUserPanels[i]); //�û�!!
			tabbedPane_activity.putClientProperty("tab_vo_" + i, _dealactivityVOs[i]); //�û�!!
		}

		tabbedPane_activity.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane_activity.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON3) {
					showPopMenu(e.getX(), e.getY());
				}
			}
		}); //

		tabbedPane_activity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				onTabChangeSelected(); //
			}
		});

		//tabbedPane_activity.setForegroundAt(0, Color.RED); //
		tabbedPane_activity.setIconAt(0, imgSelected); //

		//�����˵�!
		popMenu = new JPopupMenu(); //
		JMenuItem menuItem_showpart = new JMenuItem("�鿴�û��ڲ����߶���"); //
		JMenuItem menuItem_acivityinfo = new JMenuItem("�鿴�û��ڵ�ʵ�ʶ���"); //
		menuItem_showpart.setPreferredSize(new Dimension(135, 19));
		menuItem_acivityinfo.setPreferredSize(new Dimension(135, 19));
		menuItem_showpart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showParticipanInfo(); //
			}
		}); //
		menuItem_acivityinfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showActivityDBRecord(); //
			}
		}); //
		popMenu.add(menuItem_showpart); //
		popMenu.add(menuItem_acivityinfo); //

		contentPanel.add(tabbedPane_activity, BorderLayout.CENTER); //
		contentPanel.add(getSouthPanel(true, _isReturn), BorderLayout.SOUTH); //

		return contentPanel; //�������!!
	}

	//UI�˶��Ѿ���������Ĵ��컷�ڸ������Ӧ�������϶���Ľ�ɫ�������뱾�˵�ʵ�ʽ�ɫ����ƥ�䣡
	//���������еĽ�ɫ���ܳ�����
	private boolean uiSecondCheckActivityByRoleName(String[] _roleCons) {
		String[] str_myAllRoles = ClientEnvironment.getCurrLoginUserVO().getAllRoleNames(); //
		if (str_myAllRoles == null || str_myAllRoles.length <= 0) {
			return false; //�����һ����ɫû��,�򷵻أ�
		}
		for (int i = 0; i < str_myAllRoles.length; i++) {
			for (int j = 0; j < _roleCons.length; j++) {
				if (str_myAllRoles[i].indexOf(_roleCons[j]) >= 0) { //�����ҵĻ����ǽС�һ�з����ۺ�Ա��,�������еĽ�ɫ�С��ۺ�Ա��,���ʾ������Ȩ���ģ�
					return true; //
				}
			}
		}
		return false; //
	}

	/**
	 * UI����ԶԲ����߸��ݽ�ɫ���ƽ��ж��ι��ˣ�����������ģ��ƥ�䣡����
	 * @param taskVOS
	 * @return
	 */
	private DealTaskVO[] uiSecondFilterTaskByRoleName(DealTaskVO[] taskVOS, String[] _roleNames) {
		ArrayList al_newTasks = new ArrayList(); //
		for (int j = 0; j < taskVOS.length; j++) { //������������
			String str_parUserRoleName = taskVOS[j].getParticipantUserRoleName(); //��������߽�ɫ������!
			System.out.println("��ɫ����:[" + str_parUserRoleName + "]"); //
			if (str_parUserRoleName != null && !str_parUserRoleName.equals("")) { //�����������������Ա�Ľ�ɫ���Ʋ�Ϊ��!
				String[] str_roleNameItems = getTBUtil().split(str_parUserRoleName, ";"); //�ָ�һ�£�����
				boolean isMatch = false; //
				for (int k = 0; k < str_roleNameItems.length; k++) { //����������
					for (int r = 0; r < _roleNames.length; r++) { //
						if (str_roleNameItems[k].indexOf(_roleNames[r]) >= 0) { //��������ɫ������Ҫ���˵Ľ�ɫ��,����룡��
							isMatch = true; //
							break; //
						}
					}
					if (isMatch) { //�����һ�������ˣ����ټ����ˣ�
						break;
					}
				}
				if (isMatch) { //����Ե���
					al_newTasks.add(taskVOS[j]); //
				}
			}
		}

		return (DealTaskVO[]) al_newTasks.toArray(new DealTaskVO[0]); //
	}

	private JPanel getMsgPanel() {
		String str_currUserName = "��" + firstTaskVO.getCurrParticipantUserName() + "��"; //��ǰ������Ա������
		String str_currActBLDeptName = firstTaskVO.getCurractivityBLDeptName(); //��ǰ���ڵ��������ž��������
		String str_currActName = firstTaskVO.getCurractivityName(); //��ǰ���ڵ�����!!
		str_currActName = "��" + (str_currActBLDeptName == null ? "" : (str_currActBLDeptName + "-")) + str_currActName + "��"; //
		str_currActName = getTBUtil().replaceAll(str_currActName, "\r", ""); //
		str_currActName = getTBUtil().replaceAll(str_currActName, "\n", ""); //
		WLTLabel label = new WLTLabel("��ʾ:��ǰ������" + str_currActName + ",��ǰ������" + str_currUserName + ",��ѡ���¸������е���Ա�����ύ!"); //
		label.setPreferredSize(new Dimension(2000, 20)); //
		label.setForeground(Color.BLUE); //
		label.addStrItemColor(str_currActName, Color.RED); //
		label.addStrItemColor(str_currUserName, Color.RED); //
		label.addMouseListener(this); //
		WLTPanel helpMsgPanel = new WLTPanel(WLTPanel.VERTICAL_TOP_TO_BOTTOM, LookAndFeel.defaultShadeColor1, false); //
		helpMsgPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5)); //
		helpMsgPanel.add(label); //

		return helpMsgPanel; //
	}

	/**
	 * ��ť���
	 * @return
	 */
	private JPanel getSouthPanel(boolean _isConfirm, boolean _isReturn) {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_return = new WLTButton("����"); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_monitor = new WLTButton("���̼��"); //
		btn_cancel = new WLTButton(UIUtil.getLanguage("ȡ��")); //

		btn_return.setPreferredSize(new Dimension(70, 25)); //
		btn_monitor.setPreferredSize(new Dimension(70, 25)); //
		btn_confirm.setPreferredSize(new Dimension(70, 25)); //
		btn_cancel.setPreferredSize(new Dimension(70, 25)); //

		btn_return.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_monitor.addActionListener(this); //
		btn_cancel.addActionListener(this); //

		if (_isReturn) {
			panel.add(btn_return);
		}
		if (_isConfirm) {
			panel.add(btn_confirm);
		}
		panel.add(btn_monitor);
		panel.add(btn_cancel);

		if (ClientEnvironment.isAdmin()) { //Ϊ�˽������ʱ�����ҵ������!
			panel.setToolTipText("WorkFlowDealChooseUserDialog,ParticipateUserPanel"); //
		}
		return panel;
	}

	private String getApproveModel(String _model) {
		if (_model == null) {
			return "��ռ";
		} else if (_model.equals("1")) { //
			return "��ռ";
		} else if (_model.equals("2")) { //
			return "��ǩ";
		} else if (_model.equals("3")) { //
			return "���������";
		} else {
			return "δ֪";
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //ȷ��
		} else if (e.getSource() == btn_monitor) {
			onMonitor(); //���̼��
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //ȡ��
		} else if (e.getSource() == btn_return) {
			onRetrun(); //����
		}
	}

	/**
	 * ���ȷ��,���ύ!!!!
	 */
	private void onConfirm() {
		DealTaskVO[] commitTaskVOs = null; //
		int li_selindex = this.tabbedPane_activity.getSelectedIndex(); //ȡ���ǵڼ�������!!!
		DealActivityVO choosedActivityVO = (DealActivityVO) tabbedPane_activity.getClientProperty("tab_vo_" + li_selindex); //ѡ��ҳǩ�ϰ󶨵�VO....
		ParticipateUserPanel choosedParUserPanel = (ParticipateUserPanel) tabbedPane_activity.getClientProperty("tab_user_" + li_selindex); //��Ա���!

		if ("END".equalsIgnoreCase(choosedActivityVO.getActivityType()) && choosedParUserPanel.getBillListPanel().getRowCount() == 0) { //���ܻ�������!
			MessageBox.show(this, "�����ǽ�������,�����м�İ�ť��������!"); //
			return;
		}

		boolean isMust = getTBUtil().getSysOptionBooleanValue("������Ա�Ƿ��ѡ", false); //��Ȼ�пͻ�Ҫ�����ѡ������????
		if (isMust) {
			//���������Ա��Ϊ��,�����ѡ��...
			BillListPanel list_2 = choosedParUserPanel.getBillListPanel2();
			if (list_2 != null && list_2.getRowCount() > 0 && list_2.getSelectedRows(true) != null && list_2.getSelectedRows(true).length <= 0) {//����б�ѡ��ʽ�ǹ�ѡ��ʽ����ȡ�ù�ѡ�ļ�¼�����/2012-03-01��
				MessageBox.show(this, "����ѡ��һ��������Ա!" + (list_2.isRowNumberChecked() ? "\r\n��ע��:ֻ��ѡ��ǰ��Ĺ�ѡ�����������ѡ��!" : ""));
				return;
			}
		}

		commitTaskVOs = choosedParUserPanel.getSelectedTaskVOs(); //�Ӳ�����Ա��ȡ�����д���������!
		if (commitTaskVOs == null) {
			return;
		}

		////
		String str_activityName = choosedActivityVO.getCurrActivityName(false); //
		if (getTBUtil().getSysOptionBooleanValue("�������ύʱ�Ƿ���ʾ����", true)) { //
			String desc = getTBUtil().getSysOptionStringValue("�������ύʱ��������Ϣ", "ȷ��Ҫ�ύ�����ڡ�" + str_activityName + "����?");
			desc = getTBUtil().replaceAll(desc, "$��������$", str_activityName);
			if (!MessageBox.confirm(this, desc)) {
				return; //
			}
		}

		clearAllDealTaskVOs(firstTaskVO); //������е�һ�δӷ������˷��صĴ���������,Ŀ����Ϊ���������.
		firstTaskVO.setCommitTaskVOs(commitTaskVOs); //���ô�������..
		closetype = 1;
		this.dispose(); //

	}

	private void showHelpMsg() {
		MessageBox.show(this, sb_allHelpMsg.toString()); //
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

	private void onEndWF() {
		if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ����������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		closetype = 3;
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

	private void showPopMenu(int _x, int _y) {
		popMenu.show(tabbedPane_activity, _x, _y); //
	}

	/**
	 * ��ʾ���ڲ�������Ϣ!!
	 */
	private void showParticipanInfo() {
		try {
			int li_index = tabbedPane_activity.getSelectedIndex(); //
			DealActivityVO choosedActivityVO = (DealActivityVO) tabbedPane_activity.getClientProperty("tab_vo_" + li_index); //
			String str_msg = choosedActivityVO.getParticiptMsg(); //
			TBUtil tbUtil = new TBUtil(); //
			str_msg = tbUtil.replaceAll(str_msg, "<html>", ""); //
			str_msg = tbUtil.replaceAll(str_msg, "</html>", ""); //
			str_msg = tbUtil.replaceAll(str_msg, "###", ""); //
			str_msg = tbUtil.replaceAll(str_msg, "<br>", "\r\n"); //
			str_msg = "��������=��" + choosedActivityVO.getActivityName() + "��\r\n" + str_msg;
			MessageBox.showTextArea(this, str_msg); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 
	 * @return
	 */
	private String getConvertShowpartimode(String _type) {
		if (_type == null || _type.trim().equals("") || _type.trim().equals("1")) {
			return "����ʾ�����"; //
		} else if (_type.trim().equals("2")) {
			return "����ʾ�߹���"; //
		} else if (_type.trim().equals("3")) {
			return "���߶���ʾ"; //
		} else {
			return "δ֪������[" + _type + "]";
		}
	}

	private void showActivityDBRecord() {
		try {
			int li_index = tabbedPane_activity.getSelectedIndex(); //
			DealActivityVO choosedActivityVO = (DealActivityVO) tabbedPane_activity.getClientProperty("tab_vo_" + li_index); //
			String str_paruser = choosedActivityVO.getActivityId(); //
			new RecordShowDialog(this, "pub_wf_activity", "id", str_paruser);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void onCancel() {
		closetype = 2;
		this.dispose(); //
	}

	public int getClosetype() {
		return closetype;
	}

	public WFParVO getReturnVO() {
		return firstTaskVO;
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}

	/**
	 * ҳǩѡ��仯!!
	 */
	private void onTabChangeSelected() {
		int li_count = tabbedPane_activity.getTabCount(); //
		int li_sel = tabbedPane_activity.getSelectedIndex(); //
		for (int i = 0; i < li_count; i++) {
			if (i == li_sel) {
				DealActivityVO choosedActivityVO = (DealActivityVO) tabbedPane_activity.getClientProperty("tab_vo_" + li_sel);
				if (choosedActivityVO.getWnParam() != null && choosedActivityVO.getWnParam().get("ѡ������") != null && !"".equals((String) choosedActivityVO.getWnParam().get("ѡ������"))) {
					MessageBox.showWarn(this, (String) choosedActivityVO.getWnParam().get("ѡ������"));
				}
				tabbedPane_activity.setIconAt(i, imgSelected); //	
			} else {
				tabbedPane_activity.setIconAt(i, imgUnSelected); //
			}
		}

	}

	public BillVO getBillVO() {
		return billVO;
	}

	public void setBillVO(BillVO billVO) {
		this.billVO = billVO;
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		closetype = 2;
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			showHelpMsg(); //
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

}
