package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.HFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_CheckBox;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;

/**
 * �����������,�����ͻ������������Ҫ��һ��ҳ���Ͻ���!!!
 * 
 * @author xch      
 * 
 */
public class WorkFlowProcessPanel extends JPanel implements ActionListener, BillListHtmlHrefListener {

	private static final long serialVersionUID = 281604971802320941L;
	private Window parentWindow = null; // ���״���

	private String taskId = null; // ��Ϣ����id,��pub_task_deal.id
	private String prDealPoolId = null; // /�������������!!
	private String prInstanceId = null; // ����ʵ������!!
	private BillListPanel appBillListPanel = null; // Ӧ�õ��ݵ��б�!�����ĸ�ҵ�񵥾ݱ���ϴ�������!!
	// �������ﻹ������Ҫȡ��ҵ�񵥾��е������ֶ�ֵʲô��!!!

	private boolean isOnlyView = false; // �Ƿ�ֻ����鿴,��ǰ�ǽ�"�Ƿ��ǳ���ģʽ",������Ϊ�����������Ҳ�ǲ��ܴ����ֻ�ܲ鿴��,���Ժϲ���
	private boolean isYJBD = false;
	private String str_OnlyViewReason = null; // ֻ�ܲ鿴������!!!
	public JPanel dealMsgPanel = null; // �������������д���

	private VFlowLayoutPanel vflowPanel = null; //
	private BillCardPanel billCardPanel; // ��Ƭ
	private BillListPanel billList_hist = null; // ��ʷ����б�,������������Ҫ�����

	private JComboBox combobox_prioritylevel; // �����̶�
	private CardCPanel_Ref refDate_dealtimelimit; // Ҫ��������
	private JTextArea textarea_msg = null; // �����ı�����Ŀؼ�.
	private CardCPanel_Ref refFile = null; // �ϴ������Ŀؼ�.
	private CardCPanel_CheckBox ifSendEmail; // �Ƿ����ʼ�

	// ��ǰ�Ǵӱ���ȡ��,���������������̺�,�ĳ��˴��������ֱ��ȡ!!!
	public HashVO currDealTaskInfo = null; //

	private WLTButton btn_save, btn_submit, btn_reject, btn_end, btn_monitor, btn_monitor_, btn_ccConfirm, btn_transSend, btn_calcel, btn_Histsave, btn_mind; // ����ť
	// �ʴ��߶���
	// �����/2012-08-15��
	private WLTButton btn_saveMsg, btn_commonmsgs; // �������,�������

	private JTabbedPane tabbedPane;
	private WLTButton btn_todeal_1, btn_todeal_2;

	private boolean is_creater = false;
	protected BillVO workflowBillVO = null; // //
	private int closeType = -1;

	private HashVO hvoCurrWFInfo = null; // ��ǰ������Ϣ!!!�����ݵ���������ҵ������,���������ʲô����,�������ж�����ʲô������??
	private HashVO hvoCurrActivityInfo = null; // ��ǰ������Ϣ!!!���������������ǰ����ʲô����???
	private String unCheckMsgRole = null;

	private WorkFlowYjBdMsgPanel yjbdPanel = null; // ����������!!!

	private WorkFlowServiceIfc wfService = null; // ������Զ�̷�����!
	private WorkflowUIUtil wfUIUtil = null; // ������!
	private TBUtil tbUtil = null; //

	private Logger logger = WLTLogger.getLogger(WorkFlowProcessPanel.class); //

	private String[] str_wf_halfend_endbtn_setBy_Intercept = null;

	/**
	 * ���췽��..
	 * 
	 * @param _parent
	 * @param _title
	 */
	public WorkFlowProcessPanel(Window _parentWindow, BillCardPanel _cardPanel, BillListPanel _listPanel) {
		this(_parentWindow, _cardPanel, _listPanel, null, null, null, false, null);
	}

	public WorkFlowProcessPanel(Window _parentWindow, BillCardPanel _cardPanel, BillListPanel _listPanel, String _taskId, String _dealPoolId, String _prInstanceId, boolean _isOnlyView, String _onlyViewReason) {
		this(_parentWindow, _cardPanel, _listPanel, _taskId, _dealPoolId, _prInstanceId, _isOnlyView, _onlyViewReason, false);
	}

	public WorkFlowProcessPanel(Window _parentWindow, BillCardPanel _cardPanel, BillListPanel _listPanel, String _taskId, String _dealPoolId, String _prInstanceId, boolean _isOnlyView, String _onlyViewReason, boolean isyjbd_) {
		super();
		this.parentWindow = _parentWindow; //
		this.billCardPanel = _cardPanel; //
		this.appBillListPanel = _listPanel; //
		this.taskId = _taskId; // ��Ϣ����id,��pub_task_deal.id
		this.prDealPoolId = _dealPoolId; //
		this.prInstanceId = _prInstanceId; //
		this.isOnlyView = _isOnlyView; // �Ƿ��ǳ���!!!
		this.str_OnlyViewReason = _onlyViewReason; // ֻ�ܲ鿴��ԭ��!
		this.isYJBD = isyjbd_;

		// ׷�ӹ����������ҳǩ��ʽ �����/2013-04-08��
		if (getTBUtil().getSysOptionBooleanValue("��������������Ƿ�Ϊ��ҳǩ", false) && !isYJBD) {
			initialize_tab();
		} else {
			initialize();
		}
	}

	/**
	 * ��ʼ��ҳ��!!
	 */
	private void initialize() {
		try {
			this.setLayout(new BorderLayout(0, 0)); //
			this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			this.setBackground(LookAndFeel.defaultShadeColor1); //
			this.setUI(new WLTPanelUI(BackGroundDrawingUtil.INCLINE_NW_TO_SE));

			String str_loginuserid = ClientEnvironment.getCurrLoginUserVO().getId(); // ��¼��Աid
			billCardPanel.setGroupExpandable("������Ϣ", false); // ���Ҫȥ��!!�������е��������ʱ���!!
			billCardPanel.setGroupExpandable("*���沿��д*", true); //
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); // ����������!!!
			if (isOnlyView) { // �����ֻ�� �鿴,�򲻿ɱ༭!!
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //
				billCardPanel.setEditable(false); //
			} else {
				boolean isCanEditByinit = false; // �Ƿ�ɱ༭
				if (getTBUtil().getSysOptionBooleanValue("�������˻ط������Ƿ�����޸�", false)) { // ����в�����ָ��,���˴�������ʱ,�����˿����޸ĵ���״̬�Ļ�,����һ��!
					try {
						String str_prInstanceId = getPrinstanceId(); // ������ʵ��!!��ǰ�и�bug,����ҵ��Ŀ�з��������̵Ĵ�����Ҳ���޸�!
						// ����˵���������������뻹Ҫ�ҵ������̵Ĵ�����!!
						String str_wfCreater = wfservice.getRootInstanceCreater(str_prInstanceId); // ȡ�ñ�����ʵ���ĸ�����ʵ���Ĵ�����!
						if (str_wfCreater != null && str_wfCreater.equals(str_loginuserid)) { // ������̴����˾��Ǳ���,�����ǿɱ༭��!!!
							isCanEditByinit = true; //
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				if (isCanEditByinit) {
					billCardPanel.setEditableByEditInit(); // ���!
				} else {
					billCardPanel.setEditableByWorkFlowInit(); //
				}
			}

			currDealTaskInfo = getCurrTaskInfo(); // ��ǰ����������Ϣ,��������Ƿ���ʾ��·�������˻ذ�ť����Ҫ�����Ϣ!

			ArrayList al_compents = new ArrayList(); //
			al_compents.add(billCardPanel); // ҵ�񵥾� �����
			al_compents.add(getHistoryBillListPanel()); // �����ύ��ʷ �����

			// �������,��ǰ��һ�ﵽ��,���������пͻ�˵���������̫��ʱ,Ҫ��������Ϣ��Զ��������!!���Լ���һ������!!,��Ĭ�ϻ�����ǰ�ķ��,���ϵ�������ſ�������������!
			if (isOnlyView) { // ���ֻ����ʾ,��û�д���,����鿴,���͵�!
				vflowPanel = new VFlowLayoutPanel(al_compents); //
				this.add(vflowPanel, BorderLayout.CENTER); //
			} else {
				if (isWfDealMsgPanelFloat()) { // ����Ǹ�����!!
					vflowPanel = new VFlowLayoutPanel(al_compents); //
					JPanel tmpPanel = new JPanel(new BorderLayout()); //
					tmpPanel.setOpaque(false); // ͸��!!
					if (isYJBD) {
						yjbdPanel = new WorkFlowYjBdMsgPanel(prDealPoolId, null); // ����������
						if (getTBUtil().getSysOptionBooleanValue("��������������Ƿ���ʾ����Ϣ", false)) {
							tmpPanel.add(vflowPanel, BorderLayout.CENTER);
							tmpPanel.add(yjbdPanel, BorderLayout.SOUTH);
						} else {
							tmpPanel.add(yjbdPanel, BorderLayout.CENTER);
							this.parentWindow.setSize(yjbdPanel.getPreferredSize().width + 100, yjbdPanel.getPreferredSize().height + 100); // ��ʱ����Ҫ��С!
							if (this.parentWindow != null && this.parentWindow instanceof BillDialog) {
								BillDialog parentDialog = (BillDialog) this.parentWindow; //
								parentDialog.setTitle("�������-" + parentDialog.getTitle()); //
								parentDialog.locationToCenterPosition();
							}
						}
					} else {
						tmpPanel.add(vflowPanel, BorderLayout.CENTER); // ����
						tmpPanel.add(getDealMsgPanel(), BorderLayout.SOUTH); // ������������̴������������!!
					}
					this.add(tmpPanel, BorderLayout.CENTER); //
				} else { // ���������,����ǰ�Ĳ��ַ��,���������������ſ���
					al_compents.add(getDealMsgPanel()); // ������Ϣ
					vflowPanel = new VFlowLayoutPanel(al_compents); //
					this.add(vflowPanel, BorderLayout.CENTER); //
				}
			}
			// ���������ύ��ť!!
			this.add(getSouthPanel(), BorderLayout.SOUTH); // ����İ�ť����ʾ!!

			// ��ǰ�����˷�Ӧ����Ƭ����̫��ʱ,��֪�������и����������,������ʱ��ҪĬ�Ͻ�����������������ʾ!!
			if (!isOnlyView && !isWfDealMsgPanelFloat() && getTBUtil().getSysOptionBooleanValue("�����������������Ƿ�Ĭ�Ϲ�������ʾ", false)) { //
				int li_hh = (int) vflowPanel.getScollPanel().getViewport().getPreferredSize().getHeight(); // �������������ʵ�����ݵĸ߶�!!
				makeDealMsgScrollToVisible(li_hh); // ��������������ʾ!!
			}

			if (isOnlyView) { // �����ֻ�ܲ鿴,����������,��ֱ�ӷ���!
				return; //
			}

			// ҳ���ʼ����,ִ����������������
			BillVO billvo = billCardPanel.getBillVO(); // ����VO
			String str_billtype = billvo.getStringValue("billtype"); // ��������
			String str_busitype = billvo.getStringValue("busitype"); // ҵ������!
			String str_prinstanceid = getPrinstanceId(); // ȡ������ʵ������
			if (str_billtype != null && !str_billtype.equals("") && str_busitype != null && !str_busitype.equals("")) { // ����Ϊ��ô?
				HashVO[] hvs_wf_curract = wfservice.getWFDefineAndCurrActivityInfo(str_billtype, str_busitype, str_prinstanceid, prDealPoolId); // һ��Զ�̵��õõ����̶�����Ϣ�뵱ǰ������Ϣ!!
				hvoCurrWFInfo = hvs_wf_curract[0]; // ������Ϣ!!
				hvoCurrActivityInfo = hvs_wf_curract[1]; // ��ǰ������Ϣ!!
			}

			// �������̶�����������뻷�ڶ����������!!!
			try {
				if (hvoCurrWFInfo != null) {
					// ���̶����������������������,һ���Ǹ߷嶨���,һ����ƽ̨�����!!!
					// �߷嶨�������������!!!
					String str_flowintercept = hvoCurrWFInfo.getStringValue("flowintercept"); // �߷嶨���������!!!
					if (str_flowintercept != null) {
						WorkFlowUIIntercept3 intercept = (WorkFlowUIIntercept3) Class.forName(str_flowintercept).newInstance();
						intercept.afterOpenWFProcessPanel(this);
					}

					// ƽ̨��׼�����̶�����������,��Ҫ������������ҵ�����ʹ���ȥ��������Ϊ������������Ҫ�ж�,ҲҪ����ǰ������Ϣ����ȥ,�������������ж�!!!
					String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); // ���̺���������,ͳһ�ĵ�UI��������!!!
					if (str_wfeguiintercept != null) {
						WLTHashMap parMap = new WLTHashMap(); // ֮���������������һ��HashMap����,����Ϊ���ݾ���,������һ��ʼ���ǵö�ôȫ��,��ʵ����������������Ҫ�����µĲ��������,������һ���ͻ�Ӱ��������ʷ��Ŀ,��չ�Ժܲ�!
						// ������õķ�������Զ��һ������,Ȼ����������Ǹ�VO,Ȼ���Ժ�ֻҪ��VO�������µı�������!��������ȱ���ǻ������������,���Ի�������İ취����Զ��HashMap,��HashMap��ȱ���ǲ���һ����֪�������м�������!!!����Ū��һ��WLTHashMap,���ṩ��һ��˵������,������Debugʱ���ٲ鿴���м�������!
						// ����:
						// parMap.put("processpanel", this,
						// "����:WorkFlowProcessPanel,ֵ�������������̴������"); //
						// parMap.put("loginuserid",
						// ClientEnvironment.getCurrLoginUserVO().getId(),
						// "����:String,ֵ���ǵ�¼��Աid"); //
						WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance(); //
						uiIntercept.afterOpenWFProcessPanel(this, str_billtype, str_busitype, billvo, hvoCurrActivityInfo, parMap); // ����ƽ̨���������������!!!
					}

					// ���ڶ������������������Ҫ���ǵ����ݾɵİ汾!lxf�����ԭ���������޸�!
					if (hvoCurrActivityInfo != null) { // �����е�ǰ����,��Ϊ��������ʱ��һ����û�е�ǰ���ڵ�!!
						String str_intercept1 = hvoCurrActivityInfo.getStringValue("intercept1"); // ���ڶ����������!!!
						if (str_intercept1 != null && str_intercept1.trim().indexOf(".") > 0) { //
							Object actIntercept = Class.forName(str_intercept1).newInstance(); //
							if (actIntercept instanceof WorkFlowUIIntercept) { // ���뿼�ǵ����ݾɵİ汾,��ǰ�ɵ�����������WorkFlowUIIntercept
								((WorkFlowUIIntercept) actIntercept).afterOpenWFProcessPanel(this); //
							} else if (actIntercept instanceof WorkFlowEngineUIIntercept) { // �µ�������ͳһ��WorkFlowEngineUIIntercept
								WLTHashMap parMap = new WLTHashMap(); // ֮���Ը���������������ԭ����һ����!!
								((WorkFlowEngineUIIntercept) actIntercept).afterOpenWFProcessPanelByCurrActivity(this, str_billtype, str_busitype, billvo, hvoCurrActivityInfo, parMap); //
							}
						}
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace(); // �ȳԵ��쳣!!
			}
		} catch (Exception ex) {
			WLTLogger.getLogger(this.getClass()).error("��������ִ�пͻ���������ʱ�����쳣!", ex); //
		}
	}

	// ׷�ӹ����������ҳǩ��ʽ �����/2013-04-08��
	private void initialize_tab() {
		try {
			this.setLayout(new BorderLayout(0, 0)); //
			this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			this.setBackground(LookAndFeel.defaultShadeColor1); //
			this.setUI(new WLTPanelUI(BackGroundDrawingUtil.INCLINE_NW_TO_SE));

			String str_loginuserid = ClientEnvironment.getCurrLoginUserVO().getId(); // ��¼��Աid
			billCardPanel.setGroupExpandable("������Ϣ", false); // ���Ҫȥ��!!�������е��������ʱ���!!
			billCardPanel.setGroupExpandable("*���沿��д*", true); //
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); // ����������!!!
			if (isOnlyView) { // �����ֻ�� �鿴,�򲻿ɱ༭!!
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //
				billCardPanel.setEditable(false); //
			} else {
				boolean isCanEditByinit = false; // �Ƿ�ɱ༭
				if (getTBUtil().getSysOptionBooleanValue("�������˻ط������Ƿ�����޸�", false)) { // ����в�����ָ��,���˴�������ʱ,�����˿����޸ĵ���״̬�Ļ�,����һ��!
					try {
						String str_prInstanceId = getPrinstanceId(); // ������ʵ��!!��ǰ�и�bug,����ҵ��Ŀ�з��������̵Ĵ�����Ҳ���޸�!
						// ����˵���������������뻹Ҫ�ҵ������̵Ĵ�����!!
						String str_wfCreater = wfservice.getRootInstanceCreater(str_prInstanceId); // ȡ�ñ�����ʵ���ĸ�����ʵ���Ĵ�����!
						if (str_wfCreater != null && str_wfCreater.equals(str_loginuserid)) { // ������̴����˾��Ǳ���,�����ǿɱ༭��!!!
							isCanEditByinit = true; //
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				if (isCanEditByinit) {
					billCardPanel.setEditableByEditInit(); // ���!
				} else {
					billCardPanel.setEditableByWorkFlowInit(); //
				}
			}

			currDealTaskInfo = getCurrTaskInfo(); // ��ǰ����������Ϣ,��������Ƿ���ʾ��·�������˻ذ�ť����Ҫ�����Ϣ!

			getHistoryBillListPanel();

			tabbedPane = new JTabbedPane();
			tabbedPane.addTab("���̵���", getCardPanel());
			tabbedPane.addTab("���̴���", getDealPanel());

			this.add(tabbedPane);

			/*			ArrayList al_compents = new ArrayList(); //
						al_compents.add(panel_content); //ҵ�񵥾� �����
						al_compents.add(getHistoryBillListPanel()); //�����ύ��ʷ �����

						//�������,��ǰ��һ�ﵽ��,���������пͻ�˵���������̫��ʱ,Ҫ��������Ϣ��Զ��������!!���Լ���һ������!!,��Ĭ�ϻ�����ǰ�ķ��,���ϵ�������ſ�������������!
						if (isOnlyView) { //���ֻ����ʾ,��û�д���,����鿴,���͵�!
							vflowPanel = new VFlowLayoutPanel(al_compents); //
							this.add(vflowPanel, BorderLayout.CENTER); //
						} else {
							if (isWfDealMsgPanelFloat()) { //����Ǹ�����!!
								vflowPanel = new VFlowLayoutPanel(al_compents); //
								JPanel tmpPanel = new JPanel(new BorderLayout()); //
								tmpPanel.setOpaque(false); //͸��!!
								if (isYJBD) {
									yjbdPanel = new WorkFlowYjBdMsgPanel(prDealPoolId, null); //����������
									if (getTBUtil().getSysOptionBooleanValue("��������������Ƿ���ʾ����Ϣ", false)) {
										tmpPanel.add(vflowPanel, BorderLayout.CENTER);
										tmpPanel.add(yjbdPanel, BorderLayout.SOUTH);
									} else {
										tmpPanel.add(yjbdPanel, BorderLayout.CENTER);
										this.parentWindow.setSize(yjbdPanel.getPreferredSize().width + 100, yjbdPanel.getPreferredSize().height + 100); //��ʱ����Ҫ��С!
										if (this.parentWindow != null && this.parentWindow instanceof BillDialog) {
											BillDialog parentDialog = (BillDialog) this.parentWindow; //
											parentDialog.setTitle("�������-" + parentDialog.getTitle()); //
											parentDialog.locationToCenterPosition();
										}
									}
								} else {
									tmpPanel.add(vflowPanel, BorderLayout.CENTER); //����
									tmpPanel.add(getDealMsgPanel(), BorderLayout.SOUTH); //������������̴������������!!
								}
								this.add(tmpPanel, BorderLayout.CENTER); //
							} else { //���������,����ǰ�Ĳ��ַ��,���������������ſ���
								al_compents.add(getDealMsgPanel()); // ������Ϣ
								vflowPanel = new VFlowLayoutPanel(al_compents); //
								this.add(vflowPanel, BorderLayout.CENTER); //
							}
						}
						//���������ύ��ť!!
						this.add(getSouthPanel(), BorderLayout.SOUTH); //����İ�ť����ʾ!!

						//��ǰ�����˷�Ӧ����Ƭ����̫��ʱ,��֪�������и����������,������ʱ��ҪĬ�Ͻ�����������������ʾ!!
						if (!isOnlyView && !isWfDealMsgPanelFloat() && getTBUtil().getSysOptionBooleanValue("�����������������Ƿ�Ĭ�Ϲ�������ʾ", false)) { //
							int li_hh = (int) vflowPanel.getScollPanel().getViewport().getPreferredSize().getHeight(); //�������������ʵ�����ݵĸ߶�!!
							makeDealMsgScrollToVisible(li_hh); //��������������ʾ!!
						}*/

			if (isOnlyView) { // �����ֻ�ܲ鿴,����������,��ֱ�ӷ���!
				return; //
			}

			// ҳ���ʼ����,ִ����������������
			BillVO billvo = billCardPanel.getBillVO(); // ����VO
			String str_billtype = billvo.getStringValue("billtype"); // ��������
			String str_busitype = billvo.getStringValue("busitype"); // ҵ������!
			String str_prinstanceid = getPrinstanceId(); // ȡ������ʵ������
			if (str_billtype != null && !str_billtype.equals("") && str_busitype != null && !str_busitype.equals("")) { // ����Ϊ��ô?
				HashVO[] hvs_wf_curract = wfservice.getWFDefineAndCurrActivityInfo(str_billtype, str_busitype, str_prinstanceid, prDealPoolId); // һ��Զ�̵��õõ����̶�����Ϣ�뵱ǰ������Ϣ!!
				hvoCurrWFInfo = hvs_wf_curract[0]; // ������Ϣ!!
				hvoCurrActivityInfo = hvs_wf_curract[1]; // ��ǰ������Ϣ!!
			}

			// �������̶�����������뻷�ڶ����������!!!
			try {
				if (hvoCurrWFInfo != null) {
					// ���̶����������������������,һ���Ǹ߷嶨���,һ����ƽ̨�����!!!
					// �߷嶨�������������!!!
					String str_flowintercept = hvoCurrWFInfo.getStringValue("flowintercept"); // �߷嶨���������!!!
					if (str_flowintercept != null) {
						WorkFlowUIIntercept3 intercept = (WorkFlowUIIntercept3) Class.forName(str_flowintercept).newInstance();
						intercept.afterOpenWFProcessPanel(this);
					}

					// ƽ̨��׼�����̶�����������,��Ҫ������������ҵ�����ʹ���ȥ��������Ϊ������������Ҫ�ж�,ҲҪ����ǰ������Ϣ����ȥ,�������������ж�!!!
					String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); // ���̺���������,ͳһ�ĵ�UI��������!!!
					if (str_wfeguiintercept != null) {
						WLTHashMap parMap = new WLTHashMap(); // ֮���������������һ��HashMap����,����Ϊ���ݾ���,������һ��ʼ���ǵö�ôȫ��,��ʵ����������������Ҫ�����µĲ��������,������һ���ͻ�Ӱ��������ʷ��Ŀ,��չ�Ժܲ�!
						// ������õķ�������Զ��һ������,Ȼ����������Ǹ�VO,Ȼ���Ժ�ֻҪ��VO�������µı�������!��������ȱ���ǻ������������,���Ի�������İ취����Զ��HashMap,��HashMap��ȱ���ǲ���һ����֪�������м�������!!!����Ū��һ��WLTHashMap,���ṩ��һ��˵������,������Debugʱ���ٲ鿴���м�������!
						// ����:
						// parMap.put("processpanel", this,
						// "����:WorkFlowProcessPanel,ֵ�������������̴������"); //
						// parMap.put("loginuserid",
						// ClientEnvironment.getCurrLoginUserVO().getId(),
						// "����:String,ֵ���ǵ�¼��Աid"); //
						WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance(); //
						uiIntercept.afterOpenWFProcessPanel(this, str_billtype, str_busitype, billvo, hvoCurrActivityInfo, parMap); // ����ƽ̨���������������!!!
					}

					// ���ڶ������������������Ҫ���ǵ����ݾɵİ汾!lxf�����ԭ���������޸�!
					if (hvoCurrActivityInfo != null) { // �����е�ǰ����,��Ϊ��������ʱ��һ����û�е�ǰ���ڵ�!!
						String str_intercept1 = hvoCurrActivityInfo.getStringValue("intercept1"); // ���ڶ����������!!!
						if (str_intercept1 != null && str_intercept1.trim().indexOf(".") > 0) { //
							Object actIntercept = Class.forName(str_intercept1).newInstance(); //
							if (actIntercept instanceof WorkFlowUIIntercept) { // ���뿼�ǵ����ݾɵİ汾,��ǰ�ɵ�����������WorkFlowUIIntercept
								((WorkFlowUIIntercept) actIntercept).afterOpenWFProcessPanel(this); //
							} else if (actIntercept instanceof WorkFlowEngineUIIntercept) { // �µ�������ͳһ��WorkFlowEngineUIIntercept
								WLTHashMap parMap = new WLTHashMap(); // ֮���Ը���������������ԭ����һ����!!
								((WorkFlowEngineUIIntercept) actIntercept).afterOpenWFProcessPanelByCurrActivity(this, str_billtype, str_busitype, billvo, hvoCurrActivityInfo, parMap); //
							}
						}
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace(); // �ȳԵ��쳣!!
			}
		} catch (Exception ex) {
			WLTLogger.getLogger(this.getClass()).error("��������ִ�пͻ���������ʱ�����쳣!", ex); //
		}
	}

	// ׷�ӹ����������ҳǩ��ʽ��� �����/2013-04-08��
	private JPanel getCardPanel() {
		BillCardPanel _billCardPanel = billCardPanel;
		String str_wfprinstanceid = _billCardPanel.getBillVO().getStringValue("wfprinstanceid");
		if (str_wfprinstanceid != null && !str_wfprinstanceid.trim().equals("")) {
			_billCardPanel.setGroupVisiable("������������Ϣ", false);
		}

		// btn_todeal_1 = new WLTButton(" >> ");
		btn_todeal_1 = new WLTButton("���̴���", UIUtil.getImage("zt_073.gif"));
		/*		Image image = UIUtil.getImage("1093645.png").getImage();
				Image image_ = image.getScaledInstance(40, 20, Image.SCALE_AREA_AVERAGING);
				btn_todeal_1 = new WLTButton("", new ImageIcon(image_)); 
				btn_todeal_1.setPreferredSize(new Dimension(40,20));
				btn_todeal_1.setOpaque(false);
				btn_todeal_1.setBorderPainted(false);*/
		btn_todeal_1.addActionListener(this);

		JLabel empty = new JLabel();
		empty.setText("<html><body><br></body></html>");

		JPanel btnPanel = new JPanel(new BorderLayout());
		btnPanel.setOpaque(false); // ͸��!
		btnPanel.add(empty, BorderLayout.NORTH);
		btnPanel.add(btn_todeal_1, BorderLayout.EAST);

		JPanel cardPanel = new JPanel(new BorderLayout());
		cardPanel.setOpaque(false); // ͸��!
		cardPanel.setBackground(LookAndFeel.defaultShadeColor1);
		cardPanel.setUI(new WLTPanelUI(BackGroundDrawingUtil.INCLINE_NW_TO_SE));
		cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		cardPanel.add(_billCardPanel, BorderLayout.CENTER);
		cardPanel.add(btnPanel, BorderLayout.SOUTH);

		return cardPanel;
	}

	// ׷�ӹ����������ҳǩ��ʽ��� �����/2013-04-08��
	private JPanel getDealPanel() {
		JPanel workflowPanel = new JPanel(new BorderLayout());
		workflowPanel.setOpaque(false); // ͸��!
		workflowPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "��������Ϣ", TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font)); // ���ñ߿�!
		workflowPanel.add(getWorkFlowPanel(), BorderLayout.CENTER);

		JPanel dealMsgPanel = new JPanel(new BorderLayout());
		dealMsgPanel.setOpaque(false); // ͸��!
		dealMsgPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "������Ϣ", TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font)); // ���ñ߿�!
		dealMsgPanel.add(getDealMsgPanel(), BorderLayout.CENTER);

		JPanel btnPanel = getSouthPanel();
		btnPanel.setUI(null);

		// btn_todeal_2 = new WLTButton(" << ");
		btn_todeal_2 = new WLTButton("���̵���", UIUtil.getImage("zt_072.gif"));
		/*		Image image = UIUtil.getImage("1093644.png").getImage();
				Image image_ = image.getScaledInstance(40, 20, Image.SCALE_AREA_AVERAGING);
				btn_todeal_2 = new WLTButton("", new ImageIcon(image_)); 
				btn_todeal_2.setPreferredSize(new Dimension(40,20));
				btn_todeal_2.setOpaque(false);
				btn_todeal_2.setBorderPainted(false);*/
		btn_todeal_2.addActionListener(this);

		JLabel empty = new JLabel();
		empty.setText("<html><body><br></body></html>");

		JLabel empty_ = new JLabel();
		if (!isOnlyView) {
			empty_.setText("<html><body><br><br><br></body></html>");
		} else {
			empty_.setText("<html><body><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br></body></html>");
		}

		JPanel btnPanel_ = new JPanel(new BorderLayout());
		btnPanel_.setOpaque(false); // ͸��!
		btnPanel_.add(empty_, BorderLayout.NORTH);
		btnPanel_.add(btn_todeal_2, BorderLayout.WEST);

		ArrayList al_compents = new ArrayList();
		al_compents.add(workflowPanel);
		al_compents.add(empty);
		if (!isOnlyView) {
			al_compents.add(dealMsgPanel);
		}
		al_compents.add(btnPanel);
		al_compents.add(btnPanel_);

		VFlowLayoutPanel vPanel = new VFlowLayoutPanel(al_compents);

		JPanel dealPanel = new JPanel(new BorderLayout());
		dealPanel.setOpaque(false);
		dealPanel.setBackground(LookAndFeel.defaultShadeColor1);
		dealPanel.setUI(new WLTPanelUI(BackGroundDrawingUtil.INCLINE_NW_TO_SE));
		dealPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		dealPanel.add(vPanel);

		return dealPanel;
	}

	// ׷�ӹ����������ҳǩ��ʽ��� �����/2013-04-08��
	private VFlowLayoutPanel getWorkFlowPanel() {
		BillVO billvo = billCardPanel.getBillVO(); // ����VO
		String str_wfprinstanceid = billvo.getStringValue("wfprinstanceid");

		JLabel jl_task_curractivityname = new JLabel();
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) { // ����δ����
			jl_task_curractivityname.setText(" ��ǰ���裺���̷���               ");
		} else {
			jl_task_curractivityname.setText(" ��ǰ���裺" + billvo.getStringValue("task_curractivityname") + "               ");
		}

		JLabel jl_task = new JLabel();
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) { // ����δ����
			jl_task.setText("  �ύ�ˣ�" + ClientEnvironment.getCurrLoginUserVO().getCode() + "/" + ClientEnvironment.getCurrLoginUserVO().getName() + "            " + "�ύʱ�䣺" + getTBUtil().getCurrTime() + "            ");
		} else {
			if (billvo.getStringValue("task_creatername") != null && billvo.getStringValue("task_createtime") != null) {
				jl_task.setText("  �ύ�ˣ�" + billvo.getStringValue("task_creatername") + "            " + "�ύʱ�䣺" + billvo.getStringValue("task_createtime") + "            ");
			} else if (billvo.getStringValue("task_realdealusername") != null && billvo.getStringValue("task_dealtime") != null) {
				jl_task.setText("  �ύ�ˣ�" + billvo.getStringValue("task_realdealusername") + "            " + "�ύʱ�䣺" + billvo.getStringValue("task_dealtime") + "            ");
			} else {
				jl_task.setText("  �ύ�ˣ�                         �ύʱ�䣺");
			}
		}

		// btn_monitor_ = new WLTButton("���̼��");
		btn_monitor_ = new WLTButton("���̼��", UIUtil.getImage("office_046.gif"));
		btn_monitor_.addActionListener(this);

		JPanel jpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jpanel.setOpaque(false); // ͸��!
		jpanel.add(jl_task_curractivityname);
		jpanel.add(btn_monitor_);

		ArrayList al_compents = new ArrayList();
		al_compents.add(jpanel);
		al_compents.add(jl_task);

		return new VFlowLayoutPanel(al_compents);
	}

	/**
	 * ȡ�õ�ǰ����,���������ʵ�ظ���!!!
	 * @return
	 */
	private HashVO getCurrTaskInfo() {
		if (currDealTaskInfo != null) {
			return currDealTaskInfo; //
		}
		try {
			String str_prinstanceid = getPrinstanceId(); //
			if (str_prinstanceid != null && !str_prinstanceid.equals("")) {
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("select "); //
				sb_sql.append("pub_wf_activity.wfname,code,"); // ��������
				sb_sql.append("pub_wf_activity.belongdeptgroup,"); // ��������������
				sb_sql.append("pub_wf_activity.iscanback,"); // �Ƿ��л���!
				sb_sql.append("pub_wf_activity.canhalfend,"); // �Ƿ��н���!
				sb_sql.append("pub_wf_activity.isHideTransSend, "); // �Ƿ�����ת��,Ĭ����ʾ!
				sb_sql.append("pub_wf_activity.approvemodel ");// ���ģʽ
				sb_sql.append("from pub_wf_dealpool,pub_wf_activity "); //
				sb_sql.append("where pub_wf_dealpool.curractivity=pub_wf_activity.id ");
				sb_sql.append("and pub_wf_dealpool.id='" + this.prDealPoolId + "' "); // ��������id

				HashVO[] hvo = UIUtil.getHashVoArrayByDS(null, sb_sql.toString()); //
				if (hvo == null || hvo.length == 0) {
					return null;
				}

				currDealTaskInfo = hvo[0]; //
				return currDealTaskInfo; //		
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace(); //
			return null;
		}
	}

	/**
	 * ��ʷ�����¼�б�!
	 * @return
	 */
	public BillListPanel getHistoryBillListPanel() {
		if (billList_hist != null) {
			return billList_hist;
		}
		try {
			BillVO billVO = getBillCardPanel().getBillVO(); //
			String str_prinstanceid = getPrinstanceId(); // ����ط�Ҫ����һ��,��������Ҫ��ʾ����root������ͬ�ļ�¼
			WFHistListPanelBuilder histListBuilder = new WFHistListPanelBuilder(str_prinstanceid, billVO, true); // �����
			billList_hist = histListBuilder.getBillListPanel(); // �������!!

			boolean isCanEditableMsgFile = getTBUtil().getSysOptionBooleanValue("�������Ƿ�����޸���ʷ����", false); //
			billList_hist.setItemEditable("submitmessagefile", isCanEditableMsgFile); //
			billList_hist.setBillListOpaque(false); // ͸����
			billList_hist.getMainScrollPane().setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0)); //
			billList_hist.setToolBarPanelBackground(LookAndFeel.cardbgcolor); //
			billList_hist.getTempletVO().setTablename(this.billCardPanel.getTempletVO().getTablename()); //
			billList_hist.getTempletVO().setSavedtablename(this.billCardPanel.getTempletVO().getSavedtablename()); //

			// ��ǰ�Ƕ�̬�����!���������ʴ�������������һ�Ź�ѡ�����ѡ��ָ��ӿ���еļ�¼,��ֻ��ʾ���е�����!Ȼ������һ�Ÿ߶Ⱦͻᶯ̬�仯
			// �����µ��߼��Ǽ�����������,��ȫ����ѡ���ܹ��ж��,Ȼ�󳬹����޾Ͷ�Ϊ350
			// �����ĺô���,һ��ʼֻ�м�����¼ʱ,�����д����հ�!!!���Ǻ�ǡ��,������ķǳ���ʱҲ����̫�ѿ�!
			int liheight = histListBuilder.getHashVODatas().length * 22 + 95;
			if (liheight > 350) {
				liheight = 350; //
			}
			billList_hist.setPreferredSize(new Dimension(790, liheight));
			billList_hist.addBillListHtmlHrefListener(this); //
			return billList_hist; //
		} catch (Exception e) {
			e.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * ����������������!! ��ǰ�Ƿ������,���������еĿͻ����ÿ�Ƭ���̫��ʱ,Ҫ�����������֪��!������!
	 * ���Ըĳ����Ǹ���������!!
	 * @return
	 */
	private JPanel getDealMsgPanel() {
		JPanel panel = new WLTPanel(new BorderLayout(0, 5)); //
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); //
		panel.setBackground(LookAndFeel.cardbgcolor); //

		JPanel panel_btn_info = new JPanel(new BorderLayout(0, 2)); //
		panel_btn_info.setOpaque(false); //
		String str_helpinfo = "��ʾ:" + getCurrActivityName(); //
		// ׷�ӹ����������ҳǩ��ʽ��� �����/2013-04-08��
		if (billList_hist.getRowCount() > 0 && !getTBUtil().getSysOptionBooleanValue("��������������Ƿ�Ϊ��ҳǩ", false)) { // �������ʷ�����,����ʾ!!
			// ����һ���ǲ���ʾ��!
			str_helpinfo = str_helpinfo + "(�����������˵�������Խ��俽������!)";
		}
		JLabel label_info = new JLabel(str_helpinfo); //
		label_info.setToolTipText(str_helpinfo); //
		label_info.setPreferredSize(new Dimension(1000, 20)); //
		label_info.setForeground(Color.RED); //
		panel_btn_info.add(label_info, BorderLayout.NORTH); //

		// �����һ�Ž����̶�ʲô��..
		JPanel panel_north = new WLTPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // ����İ�ť
		panel_north.setBackground(LookAndFeel.cardbgcolor); //

		JLabel label_prioritylevel = new JLabel("�����̶�", SwingConstants.RIGHT);
		label_prioritylevel.setForeground(Color.BLACK); //
		combobox_prioritylevel = new JComboBox(); //
		combobox_prioritylevel.setPreferredSize(new Dimension(85, 20)); //
		combobox_prioritylevel.addItem("һ��"); //
		combobox_prioritylevel.addItem("����"); //
		combobox_prioritylevel.addItem("�ǳ�����"); //
		combobox_prioritylevel.setSelectedIndex(0); //

		refDate_dealtimelimit = new CardCPanel_Ref("dealtimelimit", "Ҫ�����������", null, WLTConstants.COMP_DATETIME, null, null); // //
		refDate_dealtimelimit.getLabel().setForeground(Color.BLACK); //
		refDate_dealtimelimit.getLabel().setHorizontalAlignment(SwingConstants.RIGHT);
		// �Ƿ���ʾ �����̶� �� ��������� �Ϊtrue����ʾ��Ĭ��Ϊtrue.
		boolean isAddedBtn = false; //
		if ((getTBUtil()).getSysOptionBooleanValue("����������ҳ�����Ƿ���ʾ�����̶�", true)) {
			panel_north.add(label_prioritylevel); //
			panel_north.add(combobox_prioritylevel); //
			isAddedBtn = true;
		}
		if ((getTBUtil()).getSysOptionBooleanValue("����������ҳ�����Ƿ���ʾ���������", true)) {
			panel_north.add(refDate_dealtimelimit); //
			isAddedBtn = true;
		}
		if (isAddedBtn) {
			panel_btn_info.add(panel_north, BorderLayout.SOUTH); //
		}
		panel.add(panel_btn_info, BorderLayout.NORTH); //

		// �������
		JLabel label = new JLabel("<html><font color='#FF0000'>&nbsp;*</font>����&nbsp;&nbsp;<br>&nbsp;&nbsp;���&nbsp;&nbsp;</html>");// Ԭ����20121108���ģ���Խϼ�����
		// ����������ÿ���
		label.setForeground(Color.BLUE); //
		JPanel panel_west = new WLTPanel(new BorderLayout()); //
		panel_west.setBackground(LookAndFeel.cardbgcolor); //
		panel_west.add(label, BorderLayout.NORTH); //
		panel.add(panel_west, BorderLayout.WEST); //
		textarea_msg = new WLTTextArea(15, 50); //
		unCheckMsgRole = getTBUtil().getSysOptionStringValue("����������ҳ���д������������֤��ɫ", "");
		if ((getTBUtil()).getSysOptionBooleanValue("����������ҳ���д�������Ƿ���ʾΪ����ύ�����", false)) {// ��ҵ���
			try {
				String str_prinstanceid = getPrinstanceId(); //
				if (str_prinstanceid != null && !str_prinstanceid.equals("")) {
					textarea_msg.setText(UIUtil.getStringValueByDS(null, "select lastsubmitmsg from pub_wf_prinstance where id = '" + str_prinstanceid + "'"));
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		textarea_msg.setLineWrap(true);
		textarea_msg.setForeground(LookAndFeel.inputforecolor_enable); // ��Чʱ��ǰ��ɫ
		textarea_msg.setBackground(LookAndFeel.inputbgcolor_enable); // ��Чʱ�ı���ɫ
		panel.add(new JScrollPane(textarea_msg), BorderLayout.CENTER); //

		// �ϴ�����
		refFile = new CardCPanel_Ref("messagefile", "���� ", null, WLTConstants.COMP_FILECHOOSE, null, billCardPanel); // //
		refFile.getBtn_ref().setToolTipText("�ϴ�����"); //
		refFile.getBtn_ref().setBackground(LookAndFeel.systembgcolor); //
		refFile.getBtn_ref().setForeground(Color.RED); //
		refFile.getBtn_ref().setPreferredSize(new Dimension(110, 23)); //
		refFile.getBtn_ref().setText("�ϴ�����"); //
		refFile.getLabel().setForeground(Color.BLACK); //
		refFile.getLabel().setPreferredSize(new Dimension(30, 23)); //
		refFile.getTextField().setPreferredSize(new Dimension(350, 23)); //
		refFile.setPreferredSize(new Dimension(50 + 350 + 100, 23)); //
		refFile.setItemEditable(true); //

		// ����������������������
		btn_saveMsg = new WLTButton("�������", UIUtil.getImage("save.gif")); //
		btn_commonmsgs = new WLTButton("���òο����", UIUtil.getImage("office_043.gif")); //
		btn_saveMsg.addActionListener(this); //
		btn_commonmsgs.addActionListener(this); //

		JPanel panel_msg = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5)); //
		panel_msg.setOpaque(false); //
		if (TBUtil.getTBUtil().getSysOptionBooleanValue("���������Ƿ�֧���ϴ�����", true)) { // �ʴ���Ŀ�������Ҫ�ϴ�����,˵�Ǳ����������������!!!
			// ��Ϊ�������ݵ�������!)
			// {
			// //ǰ���в����������ܲ������ÿؼ�,�ʴ���Ŀ�о�Ȼ������������Ҫ����!
			panel_msg.add(refFile); //
		}
		panel_msg.add(btn_saveMsg); //
		panel_msg.add(btn_commonmsgs); //

		// ���ʼ���ѡ��!!
		ifSendEmail = new CardCPanel_CheckBox("ifSendEmail", "�Ƿ����ʼ�");
		boolean sendEmailFlag = getTBUtil().getSysOptionBooleanValue("������Ĭ�Ϸ����ʼ�", false);
		ifSendEmail.getCheckBox().setSelected(sendEmailFlag);
		if (getTBUtil().getSysOptionBooleanValue("�������Ƿ���ʾ�����ʼ�ѡ��", false)) { // ����Ƿ��ʼ�ѡ��,
			JPanel jp_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			jp_1.setOpaque(false); // ͸��!!!
			jp_1.setBackground(LookAndFeel.cardbgcolor); //
			JLabel label_1 = new JLabel("�Ƿ����ʼ�");
			label_1.setOpaque(false); // ͸��!!
			label_1.setPreferredSize(new Dimension(72, 20)); //
			// jp_1.add(label_1);
			jp_1.add(ifSendEmail);
			JPanel jp = new JPanel(new BorderLayout());
			jp.setOpaque(false); // ͸��!!
			jp.setBackground(LookAndFeel.cardbgcolor); //
			jp.add(panel_msg, BorderLayout.NORTH); // �����ǡ��������/�ϴ�������
			jp.add(jp_1, BorderLayout.CENTER); // �����Ƿ����ʼ�
			panel.add(jp, BorderLayout.SOUTH); // ��������������������!
		} else {
			panel.add(panel_msg, BorderLayout.SOUTH); //
		}

		int li_width = 750; //
		int li_initheight = 158; //
		String str_wh_option = getTBUtil().getSysOptionStringValue("����������������С", null); // �ʴ�����Ŀ�������,��Ϊһ��ҪС!�����������ͻ���Ϊһ��Ҫ��!
		if (str_wh_option != null && !str_wh_option.trim().equals("")) {
			str_wh_option = str_wh_option.trim(); //
			String str_width = str_wh_option.substring(0, str_wh_option.indexOf("*")); //
			String str_height = str_wh_option.substring(str_wh_option.indexOf("*") + 1, str_wh_option.length()); //
			li_width = Integer.parseInt(str_width); //
			li_initheight = Integer.parseInt(str_height); //
		}

		int li_height = li_initheight + 23 + (isAddedBtn ? 22 : 0) + 30; // �߶�!!
		panel.setPreferredSize(new Dimension(li_width, li_height)); // ���������̫С��,�ͻ��������!!��������,̫���˲��ÿ�,�Ͷ���700,
		HFlowLayoutPanel hflow = new HFlowLayoutPanel(new JComponent[] { panel }); // ˮƽ����

		// ������Ϣ
		String str_prinstanceid = getPrinstanceId(); //
		if (str_prinstanceid != null && !str_prinstanceid.trim().equals("")) { // �������ʵ��id��Ϊ��!!!
			try {
				// String str_sql = "select submitmessage,submitmessagefile from
				// pub_wf_dealpool where prinstanceid='" +
				// getTBUtil().getNullCondition(str_prinstanceid) + "' and
				// participant_user='" + str_loginuserid + "' and issubmit='N'
				// and isprocess='N'";
				String str_sql = "select submitmessage,submitmessagefile from pub_wf_dealpool where id='" + prDealPoolId + "'";
				HashVO[] hvs_msg = UIUtil.getHashVoArrayByDS(null, str_sql); //
				if (hvs_msg.length > 0) {
					textarea_msg.setText(hvs_msg[0].getStringValue("submitmessage", "")); // //�������!!
					if (hvs_msg[0].getStringValue("submitmessagefile") != null) {
						String str_encryFileName = hvs_msg[0].getStringValue("submitmessagefile"); //
						String[] str_fileNames = getTBUtil().split(str_encryFileName, ";"); //
						StringBuilder sb_newFileName = new StringBuilder(); //
						for (int i = 0; i < str_fileNames.length; i++) {
							if (str_fileNames[i].lastIndexOf(".") > 0) {
								String str_fileexttype = str_fileNames[i].substring(str_fileNames[i].lastIndexOf(".") + 1, str_fileNames[i].length()); //
								str_fileNames[i] = str_fileNames[i].substring(str_fileNames[i].indexOf("_") + 1, str_fileNames[i].lastIndexOf(".")); //
								str_fileNames[i] = getTBUtil().convertHexStringToStr(str_fileNames[i]) + str_fileexttype; //
							} else {
								str_fileNames[i] = str_fileNames[i].substring(str_fileNames[i].indexOf("_") + 1, str_fileNames[i].length()); //
								str_fileNames[i] = getTBUtil().convertHexStringToStr(str_fileNames[i]); //
							}
							sb_newFileName.append(str_fileNames[i] + ";"); //
						}
						RefItemVO refVO = new RefItemVO(hvs_msg[0].getStringValue("submitmessagefile"), null, sb_newFileName.toString()); // //
						refFile.setObject(refVO); // //
					}
				}
			} catch (Exception _ex) {
				logger.error("���ع�����������������ʧ��", _ex);
			}
		}
		dealMsgPanel = hflow;//
		return hflow;
	}

	/**
	 * ����İ�ť��!!!
	 * ��ť���滹��һ����ʾ!!
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // �Ű�ť�����!!
		btnPanel.setOpaque(false); // ͸��
		String str_btn_reject_name = getTBUtil().getSysOptionStringValue("�������˻ذ�ť����", " �� �� "); //
		String str_btn_submit_name = getTBUtil().getSysOptionStringValue("�������ύ��ť����", " �� �� "); // ���̿ͻ�˵�С���һ��������
		btn_reject = new WLTButton(str_btn_reject_name, UIUtil.getImage("office_161.gif")); // �������˻�,�������˻ص�ǰ������һ��!
		btn_submit = new WLTButton(str_btn_submit_name, UIUtil.getImage("office_008.gif")); // �������ύ,Ҳ����һ��!office_190.gif
		if (ClientEnvironment.isAdmin()) { // ��������������¼��,���ڰ�ť��ʾ��ֱ��˵��������Щ��������!
			btn_reject.setToolTipText("���ɲ������������˻ذ�ť���ơ�������"); //
			btn_submit.setToolTipText("���ɲ������������ύ��ť���ơ�������"); //
		}
		btn_save = new WLTButton(" �� �� "); // ����
		btn_end = new WLTButton(" �� �� "); //
		btn_monitor = new WLTButton("���̼��"); // ���̼��
		btn_ccConfirm = new WLTButton(" ȷ �� "); // ���͵�ȷ�ϼ�!
		btn_calcel = new WLTButton(" ȡ �� "); // ������ȡ��
		btn_transSend = new WLTButton(" ת �� ");
		btn_mind = new WLTButton(" �߶��� ");
		btn_save.addActionListener(this); //
		btn_submit.addActionListener(this); //
		btn_reject.addActionListener(this); //
		btn_end.addActionListener(this); //
		btn_monitor.addActionListener(this); //
		btn_ccConfirm.addActionListener(this); // ���Ӽ���!!!
		btn_calcel.addActionListener(this); //
		btn_transSend.addActionListener(this);
		btn_mind.addActionListener(this);
		btn_end.setVisible(false); //
		if (isOnlyView) { // ����ǳ���ģʽ,��ֻ��ʾ������ť,�������̼�ء��롾ȡ����!!!
			if (!getTBUtil().getSysOptionBooleanValue("��������������Ƿ�Ϊ��ҳǩ", false)) {// ׷�ӹ����������ҳǩ��ʽ���
				// �����/2013-04-08��
				btnPanel.add(btn_monitor); // ���̼��
			}
			btnPanel.add(btn_ccConfirm); // ȷ��!
			btnPanel.add(btn_calcel); // ȡ��
			if (getTBUtil().getSysOptionBooleanValue("�������Ƿ��д߶��찴ť", false)) {
				btnPanel.add(btn_mind); // �߶��� ��ʱ�����⣬���۲�Ӧ���� Ϊ����
			}
		} else {
			if (isCanBack()) { // �Ƿ����˻�?
				btnPanel.add(btn_reject); // �ܾ�
			}
			btnPanel.add(btn_submit); // �ύ,Ҳ����һ��!!
			if (isCanSend()) {
				if (getTBUtil().getSysOptionBooleanValue("�������Ƿ���ʾת�찴ť", true)) {// sunfujun/20120820/shaochunyun��Ҫ����
					btnPanel.add(btn_transSend);
				}
			}
			if (getTBUtil().getSysOptionBooleanValue("����������ҳ�����Ƿ���ʾ���水ť", false)) { // �Ƿ���ʾ���水ť��ϵͳ����������ȡ��Ĭ��Ϊ����ʾ���水ť
				btnPanel.add(btn_save); // ����,���е��������Ҫ"���水ť"���߼�,ֱ���ύ,���ύǰֱ��Ĭ�����������!!(��ֱ����ʡ��)
			}
			if (isCanHalfEnd()) { // �Ƿ���԰�·����?
				btnPanel.add(btn_end); // �ܾ� ��˭����Ŀ��Կ�����¼ |
				// ��Ŀ��Ҫ������ϣ����ǲ���ʾ����������������ȥ��ʵ by
				// haoming20160422
				// btn_end.setVisible(true); //
			}
			if (!getTBUtil().getSysOptionBooleanValue("��������������Ƿ�Ϊ��ҳǩ", false)) {// ׷�ӹ����������ҳǩ��ʽ���
				// �����/2013-04-08��
				btnPanel.add(btn_monitor); // ���̼��
			}
			btnPanel.add(btn_calcel); // ȡ��
		}

		JLabel infoLabel = new JLabel(); //
		infoLabel.setForeground(Color.RED); // Ϊ����Ŀ,���Ǻ�ɫ!!!
		infoLabel.setFont(LookAndFeel.font_big); //
		if (str_OnlyViewReason != null) {
			infoLabel.setText("��ʾ��" + str_OnlyViewReason); //
		}

		JPanel southPanel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new BorderLayout(), LookAndFeel.defaultShadeColor1, false); // ���ص����!!!VERTICAL_BOTTOM_TO_TOP
		southPanel.add(infoLabel, BorderLayout.NORTH); //
		southPanel.add(btnPanel, BorderLayout.SOUTH); //
		return southPanel; //
	}

	/**
	 * �����桿��ťִ�еĶ���!! ��ͬʱ�Ա��������������!!!
	 */
	private void onSave() {
		try {
			if (!this.billCardPanel.newCheckValidate("save", this)) { // ��У������,���û���򲻼���
				return;
			}
			this.billCardPanel.updateData(); // ֻ�����˿�Ƭ������
			saveDealPoolMsg(); // �����������,���豣�����������е��ҵĴ������!!!!!
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ֻ���湤���������!!!
	 * @throws Exception
	 */
	private void saveDealPoolMsg() throws Exception {
		if (!validateMsg()) {
			return;
		}
		BillVO billVO = this.billCardPanel.getBillVO(); // ȡ��ҳ���ϵ�����!!!
		if (!billVO.isHaveKey("WFPRINSTANCEID")) { // ���ҳ���ϰ�������ʵ������!!
			logger.error("�������̴������ʱ,����û�ж��幤������Ҫ����[wfprinstanceid],��ֱ�ӷ���!"); //
			return; //
		}

		String str_wfinstance = getPrinstanceId(); // ��������ʵ��,����������ʵ��ID!!!
		if (str_wfinstance == null || str_wfinstance.equals("")) { // //�������ʵ���ֶε�ֵΪ��,��˵����Ҫ��������!!
			MessageBox.show(this, "��������ݳɹ�!\n����Ϊ��û��������,���Դ���������ܱ���,��֪Ϥ!"); //
			return;
		}

		UpdateSQLBuilder isql = new UpdateSQLBuilder("pub_wf_dealpool"); //
		isql.setWhereCondition("id='" + prDealPoolId + "'"); // ���ֱ��ָ������������id,���µĻ���!!
		String str_message = null;
		String str_msgfile = null;
		if (isYJBD) {
			str_message = yjbdPanel.getMsgText(); //
			str_msgfile = yjbdPanel.getMsgFile(); //
		} else {
			str_message = textarea_msg.getText(); //
			str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
		}
		isql.putFieldValue("submitmessage", str_message); // ���!!���ı���
		isql.putFieldValue("submitmessagefile", str_msgfile); // �������
		int li_count = UIUtil.executeUpdateByDS(null, isql); //
		logger.debug("���洦���������ʱ������[" + li_count + "]����¼."); //
		MessageBox.show(this, "������������ɹ�!"); //
	}

	/**
	 * @param _billType
	 * @param _busitype
	 * @param _billVO
	 * @return
	 */
	private int checkBeforeSubmit(String _billType, String _busitype, BillVO _billVO) {
		try {
			// �ȴ���������ע���������!!
			int li_result = 0; //
			if (hvoCurrWFInfo != null) {
				String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); // ���̺���������,ͳһ�ĵ�UI��������!!!
				if (str_wfeguiintercept != null && !str_wfeguiintercept.trim().equals("")) {
					WLTHashMap parMap = new WLTHashMap(); // ֮���������������һ��HashMap����,����Ϊ���ݾ���,������һ��ʼ���ǵö�ôȫ��,��ʵ����������������Ҫ�����µĲ��������,������һ���ͻ�Ӱ��������ʷ��Ŀ,��չ�Ժܲ�!
					// ������õķ�������Զ��һ������,Ȼ����������Ǹ�VO,Ȼ���Ժ�ֻҪ��VO�������µı�������!��������ȱ���ǻ������������,���Ի�������İ취����Զ��HashMap,��HashMap��ȱ���ǲ���һ����֪�������м�������!!!����Ū��һ��WLTHashMap,���ṩ��һ��˵������,������Debugʱ���ٲ鿴���м�������!
					// ����:
					// parMap.put("processpanel", this,
					// "����:WorkFlowProcessPanel,ֵ�������������̴������"); //
					// parMap.put("loginuserid",
					// ClientEnvironment.getCurrLoginUserVO().getId(),
					// "����:String,ֵ���ǵ�¼��Աid"); //
					WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance(); //
					li_result = uiIntercept.beforeSubmitWFProcessPanel(this, _billType, _busitype, _billVO, hvoCurrActivityInfo, parMap); // ����ƽ̨���������������!!!
				}
			}

			if (li_result < 0) { // ������̾�У��ʧ��,��ֱ�ӷ���,�������!
				return li_result; //
			}

			// �ٴ�������ע���������!!
			if (hvoCurrActivityInfo != null) { // �����е�ǰ����,��Ϊ��������ʱ��һ����û�е�ǰ���ڵ�!!
				String str_intercept1 = hvoCurrActivityInfo.getStringValue("intercept1"); // ���ڶ����������!!!
				if (str_intercept1 != null && str_intercept1.trim().indexOf(".") > 0) { //
					Object actIntercept = Class.forName(str_intercept1).newInstance(); //
					if (actIntercept instanceof WorkFlowEngineUIIntercept) { // �µ�������ͳһ��WorkFlowEngineUIIntercept
						WLTHashMap parMap = new WLTHashMap(); // ֮���Ը���������������ԭ����һ����!!
						li_result = ((WorkFlowEngineUIIntercept) actIntercept).beforeSubmitWFProcessPanelByCurrActivity(this, _billType, _busitype, _billVO, hvoCurrActivityInfo, parMap); //
					}
				}
			}
			return li_result;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.show(this, "�ύǰ������У��ʧ��,��������̨�鿴��ϸ��Ϣ!"); //
			return -1; //
		}
	}

	/**
	 * ���������ύ����ť�Ķ���..
	 */
	private void onSubmit() {
		if (isCanHalfEnd()) { // ������԰�·�ύ!! btn_end != null &&
			// btn_end.isVisible()
			String str_wf_halfend_msg = null; //
			String str_wf_halfend_endbtn1_name = null; // Ҳ���ܽС�����ͨ����
			String str_wf_halfend_endbtn2_name = null; //
			String str_wf_halfend_keepupbtn_name = null; //
			// sunfujun/20121130/����һ����������������
			if (getPrinstanceId() == null || getPrinstanceId().equals(billCardPanel.getBillVO().getStringValue("wfprinstanceid"))) {// ��֤ͬ����������
				str_wf_halfend_msg = getTBUtil().getSysOptionStringValue("��������·����ʱ������˵��", "��ǰ����ȿ���ѡ����һ���������ύ,Ҳ����ֱ������ͨ������������!\r\n��ѡ��ʵ�ʲ���!"); //
				str_wf_halfend_endbtn1_name = getTBUtil().getSysOptionStringValue("��������·����ʱ������������ť����", "������������"); // Ҳ���ܽС�����ͨ����
				str_wf_halfend_endbtn2_name = getTBUtil().getSysOptionStringValue("��������·����ʱ�ķ�����������ť����", "��������������"); //
				str_wf_halfend_keepupbtn_name = getTBUtil().getSysOptionStringValue("��������·����ʱ�ļ�����ť����", "�����ύ"); //
			} else {// ������������
				str_wf_halfend_msg = getTBUtil().getSysOptionStringValue("�����̹�������·����ʱ������˵��", "��ǰ����ȿ���ѡ����һ���������ύ,Ҳ����ֱ������ͨ������������!\r\n��ѡ��ʵ�ʲ���!"); //
				str_wf_halfend_endbtn1_name = getTBUtil().getSysOptionStringValue("�����̹�������·����ʱ������������ť����", "������������"); // Ҳ���ܽС�����ͨ����
				str_wf_halfend_endbtn2_name = getTBUtil().getSysOptionStringValue("�����̹�������·����ʱ�ķ�����������ť����", "��������������"); //
				str_wf_halfend_keepupbtn_name = getTBUtil().getSysOptionStringValue("�����̹�������·����ʱ�ļ�����ť����", "�����ύ"); //
			}
			str_wf_halfend_endbtn1_name = "[office_175.gif]" + str_wf_halfend_endbtn1_name; // ��������/����ͨ����ͼƬ
			str_wf_halfend_endbtn2_name = "[office_061.gif]" + str_wf_halfend_endbtn2_name; // ����������/������ͨ��/����ĳ�����������!
			str_wf_halfend_keepupbtn_name = "[office_008.gif]" + str_wf_halfend_keepupbtn_name; // �Ӹ�ͼƬ!
			if (str_wf_halfend_endbtn_setBy_Intercept != null) {
				if (str_wf_halfend_endbtn_setBy_Intercept.length > 0 && !getTBUtil().isEmpty(str_wf_halfend_endbtn_setBy_Intercept[0])) {
					str_wf_halfend_endbtn1_name = str_wf_halfend_endbtn_setBy_Intercept[0]; // ��������/����ͨ����ͼƬ
				}
				if (str_wf_halfend_endbtn_setBy_Intercept.length > 1 && !getTBUtil().isEmpty(str_wf_halfend_endbtn_setBy_Intercept[1])) {
					str_wf_halfend_endbtn2_name = str_wf_halfend_endbtn_setBy_Intercept[1]; // ����������/������ͨ��/����ĳ�����������!
				}
				if (str_wf_halfend_endbtn_setBy_Intercept.length > 2 && !getTBUtil().isEmpty(str_wf_halfend_endbtn_setBy_Intercept[2])) {
					str_wf_halfend_keepupbtn_name = str_wf_halfend_endbtn_setBy_Intercept[2]; // �Ӹ�ͼƬ!
				}
			}
			if (ClientEnvironment.isAdmin()) { // ����Ǵ����������¼��,�������ʾ!��TextAreaDialog�е�276�д���{}�е��������˴���,
				str_wf_halfend_endbtn1_name = str_wf_halfend_endbtn1_name + "{���ɲ�������������·����ʱ������������ť���ơ�������}"; //
				str_wf_halfend_endbtn2_name = str_wf_halfend_endbtn2_name + "{���ɲ�������������·����ʱ�ķ�����������ť���ơ�������}"; //
				str_wf_halfend_keepupbtn_name = str_wf_halfend_keepupbtn_name + "{���ɲ�������������·����ʱ�ļ�����ť���ơ�������}"; //
			}
			boolean isHalfEnd_twotype = getTBUtil().getSysOptionBooleanValue("����������ʱ�Ƿ�ѡ���������", true); // ����ʱ�Ƿ������?

			if (isHalfEnd_twotype) { // ��������ֽ������!!
				int li_result = MessageBox.showOptionDialog(this, str_wf_halfend_msg, "��ʾ", new String[] { str_wf_halfend_endbtn1_name, str_wf_halfend_endbtn2_name, str_wf_halfend_keepupbtn_name, " ȡ �� " }, 2, 600, 150);// Ĭ��Ϊ�����ύ!!
				if (li_result == 3 || li_result == -1) { //
					return; //
				}
				if (li_result == 0) {
					onHalfEnd("��������"); // ֱ�ӽ���
					return;
				} else if (li_result == 1) {
					onHalfEnd("����������"); // ֱ�ӽ���
					return; //
				}
			} else if (!getBtn_end().isShowing()) { // by haoming
				// 2016-05-04���end��ť�Ƿ���ʾ�������ʾ�ˣ��������ֱ�ӽ�����û��Ҫ����ʾ
				int li_result = MessageBox.showOptionDialog(this, str_wf_halfend_msg, "��ʾ", new String[] { str_wf_halfend_endbtn1_name, str_wf_halfend_keepupbtn_name, " ȡ �� " }, 1, 500, 150);// Ĭ��Ϊ�����ύ!!
				if (li_result == 2 || li_result == -1) { //
					return; //
				}
				if (li_result == 0) {
					onHalfEnd("��������"); // ֱ�ӽ���
					return;
				}
			}
		}

		// �����ύ���߼�!!
		try {
			if (!this.billCardPanel.newCheckValidate("submit", this)) { // ��У��һ��,���У�鲻�����˳�,������Ӧ�����ж�һ���Ƿ����ݷ����˱仯,������ݷ����˱仯����ʾ�Ƿ񱣴�,���ѡ�񱣴�ʱ����У��!
				// �����Ƚ�����е�����,�Ժ����Ż�!
				return;
			}

			this.billCardPanel.updateData(); // �ύǰ�����ȱ���һ��!!
			this.billCardPanel.dealChildTable(true);//������Ҫ����һ�£����������ӱ�����ݽ���ɾ����ֻ��ҳ����ɾ�������ݿ���δɾ�����´δ򿪻�����ɾ�������ݡ����/2018-06-22��
			setPrinstanceID(); // ��ȡһ��,��������ʵ��id
			BillVO billVO = this.billCardPanel.getBillVO(); // ȡ��ҳ���ϵ�����!!!
			if (!billVO.isHaveKey("WFPRINSTANCEID")) { // ���ҳ���ϰ�������ʵ������!!
				MessageBox.show(this, "�ü�¼û�ж��幤������Ҫ����[wfprinstanceid],���ȶ���֮!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			String str_billtype = billVO.getStringValue("billtype"); // ��������
			String str_busitype = billVO.getStringValue("busitype"); // ҵ������!
			int li_interCeptCheck = checkBeforeSubmit(str_billtype, str_busitype, billVO); // �ύ֮ǰ��У��!!
			// �ؼ���ҵ�������,�������������У������,Ȼ������������ύ!
			// �����
			if (li_interCeptCheck < 0) { // �����-1�򷵻�!
				return;
			}

			try {
				billCardPanel.getChangedItemOldValueSQL(); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			// ����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
			// ��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
			String str_wfinstance = getPrinstanceId(); // ��������ʵ��,����������ʵ��ID!!!

			// �������ʵ���ֶε�ֵΪ��,��˵����Ҫ��������,����������!!
			if (str_wfinstance == null || str_wfinstance.equals("")) {
				ActivityVO[] startVOs = getWFUIUtil().getStartActivityVOs(this, billVO); // ȡ�ÿ���������VO
				if (startVOs != null && startVOs.length > 0) {
					ActivityVO startActivityVO = null; //
					if (startVOs.length == 1) { // ���ֻ��һ����������,������֮
						startActivityVO = startVOs[0]; //
					} else { // ���򵯳�����ͼ,�������������ĵط���������û�ѡ��һ������!
						WorkFlowChooseStartActivityDialog dialog = new WorkFlowChooseStartActivityDialog(this, startVOs); //
						dialog.setVisible(true); //
						if (dialog.getCloseType() == 1) { // �����ȷ������!
							startActivityVO = dialog.getReturnActivityVO(); //
						} else {
							return; //
						}
					}

					if (startActivityVO != null) { // ����ҵ�����������!���������ڲ�Ϊ��!!
						str_wfinstance = getWFUIUtil().startWorkFlow(this, billVO, startActivityVO); // �ȴ���һ������,����������ʵ������!!
						if (str_wfinstance != null) { // ��дҳ��,����BillListPanel��BillCardPanel�л�д����..
							processDeal(str_wfinstance, billVO, "SUBMIT", true); // �ٴ������ύ������,�ӿ�ʼ�����ڻ����Ͻ��������η���,��һ����ȡ�ÿ����𶯵Ļ���,�ڶ�������������,��������ִ������!
						}
					} else {
						MessageBox.show(this, "��������Ϊ��,�����޷�����!");
						return;
					}
				} else {
					MessageBox.show(this, "û���ҵ�һ����������!�����޷�����!");
					return;
				}
			} else { // �����е�ĳ�������ύ,����ͨ�������ύ!
				processDeal(str_wfinstance, billVO, "SUBMIT", false); // ���������е�
				// ������
			}

		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * �ύ�ȶ���ʵ�ʵ��õĴ����߼�!!!
	 * @param _prinstanceId
	 * @param _billVO
	 * @param _dealtype
	 * @param _isstart
	 * @throws Exception
	 */
	private void processDeal(String _prinstanceId, BillVO _billVO, String _dealtype, boolean _isstart) throws Exception {
		String str_loginuserid = ClientEnvironment.getCurrLoginUserVO().getId(); // ��ǰ��Աid
		String str_loginuserDeptID = ClientEnvironment.getCurrLoginUserVO().getBlDeptId(); // ��ǰ��Ա��������

		// ����һ�����ύ�Ĳ���Ϊ׼ �����������Ա��ְ���� �����/2013-03-13��
		if (getTBUtil().getSysOptionBooleanValue("�������Ƿ�����Ա��ְ", false)) {
			if (!_isstart) {
				String participant_userdept = hvoCurrActivityInfo.getStringValue("dealpool$participant_userdept");
				if (participant_userdept != null && !participant_userdept.equals("") && !participant_userdept.equals(str_loginuserDeptID)) {
					List list = new ArrayList();
					list.add("update pub_user_post set isdefault='Y' where userid=" + str_loginuserid + " and userdept=" + participant_userdept);
					list.add("update pub_user_post set isdefault='' where userid=" + str_loginuserid + " and userdept=" + str_loginuserDeptID);
					UIUtil.executeBatchByDS(null, list);
				}
			}
		}

		WFParVO firstTaskVO = getWFService().getFirstTaskVO(_prinstanceId, this.prDealPoolId, str_loginuserid, str_loginuserDeptID, _billVO, _dealtype); // ȡ������!!!����ǰ�����˵��账������!!!
		if (firstTaskVO == null) {
			MessageBox.show(this, "��û�и����̵�����!");
			return; //
		}
		if (!_isstart && firstTaskVO.isRejectedDirUp()) { // �����������ʱ,Ȼ����ֱ���ύ���쵼
			if ("SEND".equals(_dealtype)) {// �˻��Ȳ�����ת��ϸ���/sunfujun/20120810
				MessageBox.show(this, "�˻ص���������ת��!");
				return;
			}
			dirUpSubmit(firstTaskVO, _billVO); //
			return;
		}

		// boolean isExecInterceptAfterWfEnd =
		// getTBUtil().getSysOptionBooleanValue("�����������̽���ʱ�Ƿ�Ҫִ�к�̨������", false);
		// //
		WFParVO secondCallVO = null; // �ڶ����ٴ�����Ĳ���VO,��ʵ���ǵ�һ��ȡ�õĲ����ٴ��ύ������!!
		if (firstTaskVO.isIsprocessed() || "SEND".equals(_dealtype)) { // ������ս���!!!
			// ֱ���ύ!!!�������ǰ�������Ͳ�Ϊ��,�ҵ�ǰ����������END,����������,�򲻽��еڶ����ύ!
			if (firstTaskVO.getCurractivityType() != null && firstTaskVO.getCurractivityType().equals("END") && !"SEND".equals(_dealtype)) {
				if (MessageBox.showConfirmDialog(this, "�����������,�����������̵����һ��!\r\n��ȷ��Ҫ�ύ����������?", "����������", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}// sunfujun/20120810/bug�޸�
				String str_message = textarea_msg.getText(); //
				String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
				String str_wfegbsintercept = (this.hvoCurrWFInfo == null ? null : hvoCurrWFInfo.getStringValue("process$wfegbsintercept")); //
				String str_endResult = getWFService().endWorkFlow(_prinstanceId, firstTaskVO, ClientEnvironment.getCurrLoginUserVO().getId(), str_message, str_msgfile, "��������", _billVO, str_wfegbsintercept); // Զ�̵��÷���,�������̣�����
				execAfterWorkFlowEnd(_billVO); // �߷�ӵ�UI�����̽���������߼�,����ˢ��ҳ���,���������̽�����,�ͻ�����������˶�����������,��������Ҫ������ΪҪ��������!!�ͻ���Ҫ������Ϊ��UIҳ�洦��(��������ĳ����ť)!
				MessageBox.show(this, "�������һ����ִ����,�������̳ɹ�!" + (str_endResult == null ? "" : "\r\n" + str_endResult)); // //
				closeParentWindow(1); //
			} else { // ������ѡ��ĳ���û��ύ
				WorkFlowDealChooseUserDialog dialog = new WorkFlowDealChooseUserDialog(this, _billVO, firstTaskVO, _dealtype, _isstart, hvoCurrWFInfo, hvoCurrActivityInfo); // ѡ���û�!!!���,��ؼ���ҳ��!!!
				dialog.setVisible(true); //
				if (dialog.getClosetype() == BillDialog.CONFIRM) { // ��������ȷ������,��ѡ����ĳЩ��!!
					secondCallVO = dialog.getReturnVO(); //
					if (isYJBD) {
						secondCallVO.setMessage(yjbdPanel.getMsgText()); // ������ǵ����
						secondCallVO.setMessagefile(yjbdPanel.getMsgFile()); // ���ʵ���ĸ���
						secondCallVO.setDealpooltask_yjbduserid(yjbdPanel.getBdObjUserVO().getId()); //
						secondCallVO.setDealpooltask_yjbdusercode(yjbdPanel.getBdObjUserVO().getCode()); //
						secondCallVO.setDealpooltask_yjbdusername(yjbdPanel.getBdObjUserVO().getName()); //
					} else {
						secondCallVO.setMessage(textarea_msg.getText()); // ���ô������
						String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
						secondCallVO.setMessagefile(str_msgfile); // ���ø�����..
						secondCallVO.setIfSendEmail(ifSendEmail.getValue());
						secondCallVO.setPrioritylevel((String) combobox_prioritylevel.getSelectedItem()); // �����̶�
						secondCallVO.setDealtimelimit(refDate_dealtimelimit.getObject() == null ? null : ((RefItemVO) refDate_dealtimelimit.getObject()).getId()); // ���Ҫ��������
					}
					if (!secondCallVO.getWfinstanceid().equals(_prinstanceId)) {
						MessageBox.show(this, "����ĳ��δ֪ԭ��,����secondCallVO������ʵ��id[" + secondCallVO.getWfinstanceid() + "]�봫���[" + _prinstanceId + "]��һ����,����ϵͳ��������ϵ!"); // ����������ũ������Ŀ�з����е������̽���ʱĪ���������ֹ������,�������ﲻһ����,���Լ�����һ������!!
						return; //
					}
					BillVO returnBillVO = getWFService().secondCall(secondCallVO, ClientEnvironment.getCurrLoginUserVO().getId(), _billVO, _dealtype); // �ڶ����ٴ�����!!��ʵ��ִ�й�����!!!
					workflowBillVO = returnBillVO;
					closeParentWindow(1); // �رմ���!!!Ӧ���ڹرպ��ˢ���б�����!!!
				} else if (dialog.getClosetype() == BillDialog.CANCEL) { // �����ȡ��,��Ҫ��һ���ж�:�������������,��Ҫ����ɾ����������!!!,�����ֱ�ӵ����Ͻǵ�X�ر���?�᲻��������???
					if (_isstart) { // ����ǵ�һ��������,���ɾ����ʵ��!!!��һ���ǳ���Ҫ!!!
						String str_sql_1 = "delete from pub_wf_dealpool   where prinstanceid='" + _prinstanceId + "'"; // ɾ����������
						String str_sql_2 = "delete from pub_task_deal     where prinstanceid='" + _prinstanceId + "'"; // ɾ����Ϣ����!!
						String str_sql_3 = "delete from pub_wf_prinstance where id='" + _prinstanceId + "'"; // ɾ����������ʵ��!!
						String str_sql_4 = "update " + _billVO.getSaveTableName() + " set wfprinstanceid=null where " + _billVO.getPkName() + "='" + _billVO.getPkValue() + "'";
						UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3, str_sql_4 }); //
					}
				} else if (dialog.getClosetype() == 3) { // ���������ť���ص�!!!
					// ������ص�������3,������!!!
					String str_message = textarea_msg.getText(); //
					String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
					String str_wfegbsintercept = (this.hvoCurrWFInfo == null ? null : hvoCurrWFInfo.getStringValue("process$wfegbsintercept")); //
					String str_endResult = getWFService().endWorkFlow(_prinstanceId, firstTaskVO, ClientEnvironment.getCurrLoginUserVO().getId(), str_message, str_msgfile, "��������", _billVO, str_wfegbsintercept); // Զ�̵��÷���,�������̣�����
					execAfterWorkFlowEnd(_billVO); // �߷�ӵ�UI�����̽���������߼�,����ˢ��ҳ���,���������̽�����,�ͻ�����������˶�����������,��������Ҫ������ΪҪ��������!!�ͻ���Ҫ������Ϊ��UIҳ�洦��(��������ĳ����ť)!
					MessageBox.show(this, "�������̳ɹ�!" + (str_endResult == null ? "" : "\r\n" + str_endResult)); // //
					// ���̽���ʱ�Ƿ�Ҫִ�к�̨������
					closeParentWindow(1); //
				}
			}
		} else { // ��������ս���,���ǻ�ǩ�м��ĳһ��,��Ҫ�ٵ���ѡ����Ա����,����ֱ���ٴ��ύ!!!
			secondCallVO = firstTaskVO; //
			secondCallVO.setMessage(textarea_msg.getText()); // �����ύ�����!!!!
			secondCallVO.setIfSendEmail(ifSendEmail.getValue()); // �����Ƿ����ʼ�!!!!
			String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
			secondCallVO.setMessagefile(str_msgfile); //
			getWFService().secondCall(secondCallVO, ClientEnvironment.getCurrLoginUserVO().getId(), _billVO, _dealtype); // �ڶ����ٴ�����!!��ʵ��ִ�й�����!!!
			MessageBox.show(this, "�������ǩ����һ����ɹ�,�û��ڻ���ȴ������˻�ǩ���ܽ���!"); //
			closeParentWindow(1); //
		}
	}

	/**
	 * ���̽�����ִ�е�������! �о��ں�̨������!!
	 * �߷����ӵ����̽��������������!!! һ���������ط�������,һ���ǰ�·������һ���������ύʱ���������һ���Ľ������ڣ�һ����
	 * @param _billVO
	 * @throws Exception
	 */
	private void execAfterWorkFlowEnd(BillVO _billVO) throws Exception {
		// ���̽�����UI����Ҫһ��������,���������̽�����,�ڿͻ������������Ӧ����������,������������Ϊ��������Ҫ��,�ͻ���������Ϊ��UI�˵Ŀؼ�����,��������ĳ����ť!!!
		// ����һ��������������,һ���Ǹ߷������̷�����ж����,һ���Ǻ���ƽ̨������������ж����������!!
		if (hvoCurrWFInfo != null) {
			String str_flowintercept_ui = this.hvoCurrWFInfo.getStringValue("flowintercept_ui"); // �Ժ��������������!!!
			if (str_flowintercept_ui != null && str_flowintercept_ui.trim().indexOf(".") > 0) {
				WorkFlowUIIntercept2 intercept = (WorkFlowUIIntercept2) Class.forName(str_flowintercept_ui).newInstance(); //
				intercept.afterWorkFlowEnd(this, _billVO);
			}

			// ƽ̨������������Ĺ�����UI�˵�������!!!
			String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); // ���̺���������,ͳһ�ĵ�UI��������!!!
			if (str_wfeguiintercept != null && str_wfeguiintercept.trim().indexOf(".") > 0) { // ����ж���
				WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance(); //
				String str_billtype = _billVO.getStringValue("billtype"); // ��������
				String str_busitype = _billVO.getStringValue("busitype"); // ҵ������!
				WLTHashMap parMap = new WLTHashMap(); //
				uiIntercept.afterWorkFlowEnd(this, str_billtype, str_busitype, _billVO, hvoCurrActivityInfo, parMap); // ������������ִ�е�������!!!
			}
		}
	}

	/**
	 * ֱ���ύ��ĳ����,��ƹ��ʽ�ύʱ��Ҫ�������,����Ҫ��ѡ����Ա��,����ֱ���ύ���Է�!!
	 * �����processDeal()������������!!!
	 * @param _firstTaskVO
	 * @param _billVO
	 */
	private void dirUpSubmit(WFParVO _firstTaskVO, BillVO _billVO) {
		try {
			if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫֱ���ύ����" + _firstTaskVO.getDealpooltask_createusername() + "����?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			WFParVO secondCallVO = _firstTaskVO; //
			if (isYJBD) {
				secondCallVO.setMessage(yjbdPanel.getMsgText()); // ������ǵ����
				secondCallVO.setMessagefile(yjbdPanel.getMsgFile()); // ���ʵ���ĸ���
				secondCallVO.setDealpooltask_yjbduserid(yjbdPanel.getBdObjUserVO().getId()); //
				secondCallVO.setDealpooltask_yjbdusercode(yjbdPanel.getBdObjUserVO().getCode()); //
				secondCallVO.setDealpooltask_yjbdusername(yjbdPanel.getBdObjUserVO().getName()); //
			} else {
				secondCallVO.setMessage(textarea_msg.getText()); // ���ô������
				secondCallVO.setIfSendEmail(ifSendEmail.getValue());
				String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
				secondCallVO.setMessagefile(str_msgfile); // ���ø�����..
				secondCallVO.setPrioritylevel((String) combobox_prioritylevel.getSelectedItem()); // �����̶�
				secondCallVO.setDealtimelimit(refDate_dealtimelimit.getObject() == null ? null : ((RefItemVO) refDate_dealtimelimit.getObject()).getId()); // ���Ҫ��������

			}
			DealTaskVO commitTaskVO = new DealTaskVO(); //
			commitTaskVO.setParticipantUserId(_firstTaskVO.getDealpooltask_createuserid()); // �����߱����Ǵ������������!��Ϊ��ƹ��ʽ��
			commitTaskVO.setParticipantUserCode(_firstTaskVO.getDealpooltask_createusercode()); //
			commitTaskVO.setParticipantUserName(_firstTaskVO.getDealpooltask_createusername()); //

			// ��Դ���ھ����ҵ�ǰ����ĵ�ǰ����
			commitTaskVO.setFromActivityId(_firstTaskVO.getCurractivity()); // ���ĸ����ڹ�����
			commitTaskVO.setFromActivityName(_firstTaskVO.getCurractivityName()); //

			// Ŀ�껷��Ӧ���Ǵ������������Դ����
			commitTaskVO.setCurrActivityId(_firstTaskVO.getFromactivity()); // Ŀ�껷�ڱ�Ȼ����Դ����,��Ϊ��ƹ��ʽ����!!
			commitTaskVO.setCurrActivityCode(_firstTaskVO.getFromactivityCode()); //
			commitTaskVO.setCurrActivityName(_firstTaskVO.getFromactivityName()); //
			commitTaskVO.setCurrActivityType(_firstTaskVO.getFromactivityType()); // ��Դ����

			commitTaskVO.setRejectedDirUp(false); // ��Ϊ���ύ,���Զ�Ŀ������˵��Ȼ��fasle,��ΪĿ����ͬʱӵ�С��˻ء��롾�ύ������!!
			secondCallVO.setCommitTaskVOs(new DealTaskVO[] { commitTaskVO }); //
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
			/* BillVO returnBillVO = */service.secondCall(secondCallVO, ClientEnvironment.getCurrLoginUserVO().getId(), _billVO, "SUBMIT"); // �ڶ����ٴ�����!!��ʵ��ִ�й�����!!!
			MessageBox.show(this, "ֱ���ύ����" + _firstTaskVO.getDealpooltask_createusername() + "���ɹ�!"); //
			closeParentWindow(1); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * ���˻ء���ťִ�еĶ���!!!
	 * �����˻ص�ǰ�������߹�������һ�������ϵĲ�����.. Ҳ�����˻ط����˻���һ�������ύ��!
	 */
	private void onReject() {
		try {
			setPrinstanceID(); // ��ȡһ��,��������ʵ��id
			BillVO billVO = this.billCardPanel.getBillVO(); // ȡ��ҳ���ϵ�����!!!
			if (!billVO.isHaveKey("WFPRINSTANCEID")) { // ���ҳ���ϰ�������ʵ������!!
				MessageBox.show(this, "�ü�¼û�ж��幤������Ҫ����[wfprinstanceid],���ȶ���֮!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			// ����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
			// ��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
			String str_wfinstance = getPrinstanceId(); // ��������ʵ��,����������ʵ��ID!!!
			if (str_wfinstance == null || str_wfinstance.equals("")) { // //�������ʵ���ֶε�ֵΪ��,��˵����Ҫ��������!!
				MessageBox.show(this, "����ʵ������Ϊ��,û����������!��������֮!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			this.billCardPanel.updateData(); // �ύǰ�����ȱ���һ��!!

			String str_loginuserid = ClientEnvironment.getCurrLoginUserVO().getId(); //
			String str_loginuserDeptID = ClientEnvironment.getCurrLoginUserVO().getBlDeptId(); //

			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class);
			WFParVO firstTaskVO = service.getFirstTaskVO_Reject(str_wfinstance, this.prDealPoolId, str_loginuserid, str_loginuserDeptID, billVO); // ȡ���˻ص�����,�������������߹���ÿһ������Ա��Ϣ!
			if (firstTaskVO == null) {
				MessageBox.show(this, "��û�и����̵�����!");
				return; //
			}
			// by haoming
			// 2016-04-05��̫ƽ��Ŀ�ϣ������������˻ص�ʱ����Ҫ�����߼����㣬ֻ�˻ظ���һ��ľ����ˣ�����ֻ��һ�ˡ�
			String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); // ���̺���������,ͳһ�ĵ�UI��������!!!
			WFParVO secondCallVO = null;
			if (str_wfeguiintercept != null) {
				WLTHashMap parMap = new WLTHashMap(); // ֮���������������һ��HashMap����,����Ϊ���ݾ���,������һ��ʼ���ǵö�ôȫ��,��ʵ����������������Ҫ�����µĲ��������,������һ���ͻ�Ӱ��������ʷ��Ŀ,��չ�Ժܲ�!
				WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance(); //
				String str_billtype = billVO.getStringValue("billtype"); // ��������
				String str_busitype = billVO.getStringValue("busitype"); // ҵ������!
				secondCallVO = uiIntercept.onRejectCustSelectWRPar(this, str_billtype, str_busitype, billVO, hvoCurrActivityInfo, firstTaskVO, parMap); // ����ƽ̨���������������!!!
			}
			// ���Ϊ�գ���ôִ��Ĭ���߼�
			if (secondCallVO == null || secondCallVO.getCommitTaskVOs() == null || secondCallVO.getCommitTaskVOs().length == 0) {
				WorkflowRejectDealDialog dialog = new WorkflowRejectDealDialog(this, billVO, firstTaskVO); // �˻ص�ѡ���û���������,��������ѡ��:�˸�������,�˸��ύ��,�˸�����һ��!
				dialog.setVisible(true); //
				if (dialog.getClosetype() != BillDialog.CONFIRM) { //
					return;
				}
				secondCallVO = dialog.getReturnVO(); // ȡ�ô������Ա
			} else {
				DealTaskVO dealTaskVO[] = secondCallVO.getCommitTaskVOs();
				if (!MessageBox.confirm(this, "ȷ��Ҫ�˻ظ���" + dealTaskVO[0].getCurrActivityName() + "����?")) {
					return;
				}
			}
			if (isYJBD) {
				secondCallVO.setMessage(yjbdPanel.getMsgText()); // ������ǵ����
				secondCallVO.setMessagefile(yjbdPanel.getMsgFile()); // ���ʵ���ĸ���
				secondCallVO.setDealpooltask_yjbduserid(yjbdPanel.getBdObjUserVO().getId()); //
				secondCallVO.setDealpooltask_yjbdusercode(yjbdPanel.getBdObjUserVO().getCode()); //
				secondCallVO.setDealpooltask_yjbdusername(yjbdPanel.getBdObjUserVO().getName()); //
			} else {
				secondCallVO.setMessage(textarea_msg.getText()); //
				secondCallVO.setIfSendEmail(ifSendEmail.getValue());
				String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
				secondCallVO.setMessagefile(str_msgfile); //
				secondCallVO.setPrioritylevel((String) combobox_prioritylevel.getSelectedItem()); // �����̶�
				secondCallVO.setDealtimelimit(refDate_dealtimelimit.getObject() == null ? null : ((RefItemVO) refDate_dealtimelimit.getObject()).getId()); // ���Ҫ��������
			}
			service.secondCall_Reject(secondCallVO, ClientEnvironment.getCurrLoginUserVO().getId(), billVO); // �ڶ����ٴ�����!!��ʵ��ִ�й�����!!!
			// refreshWorkFlowPanel(_prinstanceId); // ˢ���������!!
			if (getTBUtil().getSysOptionBooleanValue("�������˻ز������Ƿ�رո�����", true)) {
				MessageBox.show(this, "�˻ظ���" + secondCallVO.getCommitTaskVOs()[0].getParticipantUserName() + "�������ɹ�!"); //
				closeParentWindow(1); // �رո��״���?����Ҫ��ֱ�ӹرո��״���,�����н��鲻�ر�!!
				// ���Ի���Ҫ�������!!
			} else {
				MessageBox.show(this, "�˻ظ���" + secondCallVO.getCommitTaskVOs()[0].getParticipantUserName() + "���ɹ�!"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ����·��������ťִ�еĶ���!
	 * ��;��·��������!
	 */
	private void onHalfEnd(String _endType) {
		if (!this.billCardPanel.newCheckValidate("save")) { // ��У��һ��,���У�鲻�����˳�,������Ӧ�����ж�һ���Ƿ����ݷ����˱仯,������ݷ����˱仯����ʾ�Ƿ񱣴�,���ѡ�񱣴�ʱ����У��!
			// �����Ƚ�����е�����,�Ժ����Ż�!
			return;
		}

		try {
			this.billCardPanel.updateData(); // ����ǰ�ȱ����
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
			return;
		}

		setPrinstanceID(); // ��ȡһ��,��������ʵ��id

		// �۴�ܿ˵
		BillVO billVO = this.billCardPanel.getBillVO(); // ȡ��ҳ���ϵ�����!!!
		String str_billtype = billVO.getStringValue("billtype"); // ��������
		String str_busitype = billVO.getStringValue("busitype"); // ҵ������!
		int li_interCeptCheck = checkBeforeSubmit(str_billtype, str_busitype, billVO); // ִ���ύǰ�ļ��鶯��!!!
		if (li_interCeptCheck < 0) { // �����-1�򷵻�!
			return;
		}

		String str_prinstanceid = getPrinstanceId(); //
		if (str_prinstanceid != null) {
			try {
				String str_dealpoolid = UIUtil.getStringValueByDS(null, "select id from pub_wf_dealpool where id='" + this.prDealPoolId + "' and issubmit='N' and isprocess='N'"); // ���ܱ���������ռ������,�����ٲ�ѯһ��!
				if (str_dealpoolid == null) {
					MessageBox.show(this, "��û�е�ǰ���̵�����,���ܽ����κδ���!"); //
					return; //
				}
				String str_message = textarea_msg.getText(); //
				String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); // //
				WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class);

				WFParVO myvo = new WFParVO();
				if (this.hvoCurrActivityInfo != null) {
					myvo.setIntercept2(this.hvoCurrActivityInfo.getStringValue("intercept2")); // ������2,��ʵ����BS��������
				}
				if (currDealTaskInfo != null) {
					myvo.setApproveModel(currDealTaskInfo.getStringValue("approvemodel"));// ��·����ʱ��Ҫ����������ж��Ƿ��ͬ�����������
				}
				myvo.setDealpoolid(str_dealpoolid);// �����������һ�£�������������service.endWorkFlow()���õ������null��sql�������/2012-05-11��
				String str_wfegbsintercept = (this.hvoCurrWFInfo == null ? null : hvoCurrWFInfo.getStringValue("process$wfegbsintercept")); //
				String str_endResult = service.endWorkFlow(str_prinstanceid, myvo, ClientEnvironment.getCurrLoginUserVO().getId(), str_message, str_msgfile, _endType, billVO, str_wfegbsintercept); // ��������!�����н��!
				execAfterWorkFlowEnd(billVO); // ִ�����̽������UI��������!!!
				MessageBox.show(this, (str_endResult == null ? "�������̳ɹ�!" : str_endResult)); // //
				this.btn_end.setEnabled(false); //
				closeParentWindow(1); //
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
			}
		}
	}

	/**
	 * ��ȷ�ϡ���ťִ�еĶ���,���Գ��͵��������ȷ��!
	 * ���͵�ȷ��ֵ!!!
	 */
	private void onCCToConfirm() {
		// ����dealpoolidɾ����Ϣ���еļ�¼,���޸�pub_wf_dealpool�е�ֵ!!!
		if (this.prDealPoolId == null) {
			MessageBox.show(this, "����[" + prDealPoolId + "]Ϊ��,�����Ǿɵ������������,��ʹ���µ������������!"); //
			return; //
		}
		if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫ�����������Ƶ��Ѱ���������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { //
			return; //
		}
		try {
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
			String msg = service.confirmUnEffectTask(prDealPoolId, this.str_OnlyViewReason, ClientEnvironment.getCurrLoginUserVO().getId()); // ����Զ�̷���!!!
			if (msg != null && !msg.equals("")) {//����ֻ�Ǹ���ʾ��̫ƽ���Ž��鲻Ҫ�׳��쳣�����/2018-07-25��
				MessageBox.show(this, msg);
			}
		} catch (Exception _ex) {
			_ex.printStackTrace(); // ��ǰ�����Ǳ����,������Ǳ������ط���������,�򱨴���û�������,����ν�Ҳ���������������!���Ըɴ��ɳԵ��쳣!!!
		}
		closeParentWindow(1); // �رո��״���
	}

	/**
	 * ��ת�졿��ťִ�еĶ���!!!
	 */
	private void onTransSend() {
		try {
			setPrinstanceID(); // ��ȡһ��,��������ʵ��id
			BillVO billVO = this.billCardPanel.getBillVO(); // ȡ��ҳ���ϵ�����!!!
			if (!billVO.isHaveKey("WFPRINSTANCEID")) { // ���ҳ���ϰ�������ʵ������!!
				MessageBox.show(this, "�ü�¼û�ж��幤������Ҫ����[wfprinstanceid],���ȶ���֮!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			try {
				billCardPanel.getChangedItemOldValueSQL(); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
			this.billCardPanel.updateData();
			String str_wfinstance = getPrinstanceId();
			if (str_wfinstance == null || str_wfinstance.equals("")) {
				MessageBox.show(this, "����δ�������ܽ���ת�����!", WLTConstants.MESSAGE_WARN); //
				return; //
			} else {
				processDeal(str_wfinstance, billVO, "SEND", false);
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ���߶��졿��ťִ�еĶ���!!��ν"�߶���"�������ѵ���˼!!!!
	 */
	private void onMind() { // �߶��� ����Ϣ �����/2012-08-15��
		// ���ݵ�ǰ����ʵ��ID�ж�״̬��Ϊend�Ȳ�������״̬��ΪEND������о������������̵�currowner�ֶ�,���û�о�ֱ���ұ�ʵ����currowner
		// Ҫ��2������һ���߰����һ���߰��¼
		try {
			getWFUIUtil().dealCDB(this, this.getBillCardPanel().getTempletVO().getTempletname(), getPrinstanceId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����̼�ء���ťִ�еĶ���!!!
	 */
	private void onMonitor() {
		setPrinstanceID(); // ��ȡһ��,��������ʵ��id
		BillVO billVO = this.billCardPanel.getBillVO(); // ȡ��ҳ���ϵ�����!!!
		if (!billVO.isHaveKey("WFPRINSTANCEID")) { // ���ҳ���ϰ�������ʵ������!!
			MessageBox.show(this, "�ü�¼û�ж��幤������Ҫ����[wfprinstanceid],���ȶ���֮!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		if (!billVO.isHaveKey("WFPRINSTANCEID")) {
			MessageBox.show(this, "û��BillType��Busitype,WFPRINSTANCEID��Ϣ,���ܽ������̴���!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		// ����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
		// ��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
		String str_wfinstance = getPrinstanceId(); // ����ʵ��ID..
		if (str_wfinstance == null || str_wfinstance.equals("")) { // ��������!�������ʵ��Ϊ��
			MessageBox.show(this, "�ü�¼��û����������,�����ύ��������!", WLTConstants.MESSAGE_WARN); //
			return;
		} else {
			WorkflowMonitorDialog dialog = new WorkflowMonitorDialog(parentWindow, str_wfinstance, billVO);
			dialog.setMaxWindowMenuBar();
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true); //
		}
	}

	/**
	 * ѡ�����������...
	 */
	private void onChooseCommMsg() {
		BillVO[] billvos = billList_hist.getBillVOs();
		ShowCommMsgDialog ss = null;
		if (billvos != null && billvos.length > 0) {
			String msgstr = (String) billvos[billvos.length - 1].getStringValue("dealmsg"); //
			ss = new ShowCommMsgDialog(parentWindow, msgstr); // Ҫ�ĳ�OneSQLBillListConfirmDialog
		} else {
			ss = new ShowCommMsgDialog(parentWindow);
		}
		ss.setVisible(true);
		if (ss.getCloseType() == 1) {
			textarea_msg.setText(ShowCommMsgDialog.msg);
		}
		ss.dispose();
	}

	// �����ʷ����ϵ����,���Կ����������������!!
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		int li_row = _event.getRow(); //
		RefItemVO refVO = (RefItemVO) _event.getBillListPanel().getValueAt(li_row, _event.getItemkey()); //
		String str_msg = refVO.getId(); // ��Ϣ!
		String str_msg_real = refVO.getCode(); // ������������,������Ǽ���ǰ������!
		String str_reason = _event.getBillListPanel().getRealValueAtModel(li_row, "submitmessage_viewreason"); // ԭ��!
		if (textarea_msg != null && textarea_msg.isVisible()) {
			CopyHistMsgDialog dialog = new CopyHistMsgDialog(this, "�鿴���", str_msg, str_reason, str_reason, true, billCardPanel.getBillVO()); //
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) {
				textarea_msg.append(str_msg); //
			}
		} else {
			CopyHistMsgDialog dialog = new CopyHistMsgDialog(this, "�鿴���", str_msg, str_reason, str_reason, false, billCardPanel.getBillVO()); //
			dialog.setVisible(true); //
		}
	}

	/**
	 * ��ȡ������ťִ�еĶ���...
	 */
	private void onCancel() {
		closeParentWindow(2); //
	}

	// �������ȡ��ҵ��Ƭ���!!!
	public BillCardPanel getBillCardPanel() {
		return billCardPanel; // //
	}

	private WorkFlowServiceIfc getWFService() throws Exception {
		if (wfService != null) {
			return wfService; //
		}
		wfService = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
		return wfService; //
	}

	/**
	 * ȡ������ʵ��id,��ǰ��ֱ�Ӵ�ҳ����ȡ��,������������������,���Բ����ٴ�ҳ����ȡ��,�������ж�������Ƿ���,���û�вŴ�ҳ����ȡ!
	 * �������ܼ��ݾɵ��߼�!!!
	 * 
	 * @return
	 */
	public String getPrinstanceId() {
		if (this.prInstanceId != null) {
			return this.prInstanceId; // ���������,
		}
		return billCardPanel.getBillVO().getStringValue("wfprinstanceid"); //
	}

	private boolean isWfDealMsgPanelFloat() {
		return getTBUtil().getSysOptionBooleanValue("�����������������Ƿ񸡶���ʾ������", false); // ���̴�������Ƿ񸡶�������???
	}

	/**
	 * У�����!!
	 * @return
	 */
	private boolean validateMsg() {
		if (isYJBD) {
			return yjbdPanel.checkDataValidate();
		}

		if (getTBUtil().getSysOptionBooleanValue("���̴������ύʱ��������Ƿ����", false)) {
			if ("".equals(textarea_msg.getText().trim())) {
				if (!getTBUtil().containTwoArrayCompare(ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes(), getTBUtil().split(unCheckMsgRole, ";")) && !((getPrinstanceId() == null || "".equals(getPrinstanceId())) && getTBUtil().getSysOptionBooleanValue("����������ҳ���п�ʼ���ڴ������������֤", false))) {
					textarea_msg.requestFocus(); //
					if (!isWfDealMsgPanelFloat()) { // ���û�и�������,���������ʱ��,���Զ������������!
						makeDealMsgScrollToVisible(-1); // �ô���������������ʾ!!
					}
					MessageBox.show(this, "����д�������!\r\n�˴����ܳ���2000������\r\n�����������2000������ʱ���ø����ϴ�!");
					return false;
				}
			}
		}
		try {
			int li_length = textarea_msg.getText().getBytes("GBK").length; //
			if (li_length > 4000) { // ���ܳ���4000���ֽ�,һ�������������ֽ�!
				MessageBox.show(this, "����������ܳ���2000������,���ø����ϴ�!");
				return false;
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return true;
	}

	/**
	 * �ô�����Ϣ������ʾ!!
	 */
	private void makeDealMsgScrollToVisible(int _y) {
		if (textarea_msg != null && vflowPanel != null) { // ���������Ϣ��岻Ϊ��,��vflowPanel��Ϊ��,��
			if (_y <= 0) {
				Point point = SwingUtilities.convertPoint(textarea_msg, 0, 0, vflowPanel.getScollPanel().getViewport()); // �����������ڹ������е�λ��!
				vflowPanel.getScollPanel().getViewport().scrollRectToVisible(new Rectangle(0, (int) point.getY(), 750, 200)); // ��������ʾ���������λ������!
			} else {
				vflowPanel.getScollPanel().getViewport().scrollRectToVisible(new Rectangle(0, _y, 750, 200)); // ��������ʾ���������λ������!
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			if (!validateMsg()) { // ��ǰ�ύʱ��У��,������ʱû��!��ҵ��Ŀ�б��ͻ�������! ���Լ���!
				return;
			}
			closeType = 2;
			onSave();
		} else if (e.getSource() == btn_submit) {
			if (!validateMsg()) {
				return;
			}
			if (is_creater) {
				try {
					billCardPanel.updateData();
				} catch (Exception e1) {

					e1.printStackTrace();
				}
			}
			onSubmit(); // �ύ
		} else if (e.getSource() == btn_reject) { // ���˻ء���ť!!
			if (isYJBD) {
				if (!yjbdPanel.checkDataValidate()) {
					return;
				}
			} else {
				if (getTBUtil().getSysOptionBooleanValue("���̴������˻�ʱ��������Ƿ����", false)) {
					if ("".equals(textarea_msg.getText().trim())) {
						MessageBox.show(this, "����д�������!\r\n�˴����ܳ���2000������\r\n�����������2000������ʱ���ø����ϴ�!");
						return;
					}
				}
			}
			if (is_creater) {
				try {
					billCardPanel.updateData();
				} catch (Exception e1) {
					e1.printStackTrace(); //
				}
			}
			onReject(); // �˻أ�����ȥ!
		} else if (e.getSource() == btn_end) {
			onHalfEnd("��������"); // ��;����
		} else if (e.getSource() == btn_monitor) {
			closeType = 2;
			onMonitor(); // ���̼��
		} else if (e.getSource() == btn_monitor_) {// ׷�ӹ����������ҳǩ��ʽ���
			// �����/2013-04-08��
			closeType = 2;
			onMonitor(); // ���̼��
		} else if (e.getSource() == btn_calcel) {
			onCancel(); //
		} else if (e.getSource() == btn_ccConfirm) { // ���͵�ȷ�ϼ�
			onCCToConfirm(); // ���͵�ȷ�ϼ�
		} else if (e.getSource() == btn_transSend) {
			onTransSend();
		} else if (e.getSource() == btn_mind) { // �߶���
			onMind();
		} else if (e.getSource() == btn_commonmsgs) { // �������
			onChooseCommMsg();
		} else if (e.getSource() == btn_saveMsg) { // ��ʱ���湤�������!
			try {
				saveDealPoolMsg(); //
			} catch (Exception ex) {
				MessageBox.showException(this, ex); //
			}
		} else if (e.getSource() == btn_todeal_1) {// ׷�ӹ����������ҳǩ��ʽ���
			// �����/2013-04-08��
			tabbedPane.setSelectedIndex(1);
		} else if (e.getSource() == btn_todeal_2) {// ׷�ӹ����������ҳǩ��ʽ���
			// �����/2013-04-08��
			tabbedPane.setSelectedIndex(0);
		}
	}

	private void closeParentWindow(int _closeType) {
		this.closeType = _closeType; //
		this.parentWindow.dispose(); //
	}

	public int getCloseType() {
		return closeType;
	}

	// ����һ�����̵�id
	private void setPrinstanceID() {
		try {
			BillVO billVO = this.billCardPanel.getBillVO(); //
			if (getPrinstanceId() == null) { // �������ʵ��Ϊ��,����ȡһ��!��Ϊʲô���������ʵ��Ϊ�յ������???��ʱΪʲôҪ��ô����?
				String str_sql = "select id from pub_wf_prinstance where billtablename='" + billVO.getSaveTableName() + "' and billpkname='" + billVO.getPkName() + "' and billpkvalue='" + billVO.getPkValue() + "'"; //
				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql); //
				if (hvs != null && hvs.length > 0) {
					billCardPanel.setValueAt("WFPRINSTANCEID", new StringItemVO(hvs[0].getStringValue("id"))); //
				} else {
					billCardPanel.setValueAt("WFPRINSTANCEID", null); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private String getCurrActivityName() {
		if (this.currDealTaskInfo == null) {
			return "���������,������ύ����ťѡ����һ������������!";
		}
		String str_text = ""; //
		String str_bldeptname = currDealTaskInfo.getStringValue("belongdeptgroup"); //
		String str_actname = currDealTaskInfo.getStringValue("wfname"); //

		if (str_bldeptname != null && !str_bldeptname.trim().equals("")) {
			str_text = str_text + str_bldeptname + "-"; //
		}
		str_text = str_text + str_actname; //
		str_text = getTBUtil().replaceAll(str_text, " ", ""); //
		str_text = getTBUtil().replaceAll(str_text, "\r", ""); //
		str_text = getTBUtil().replaceAll(str_text, "\n", ""); //
		return "��ǰ���衾" + str_text + "��"; //
	}

	private boolean isCanBack() {
		if (this.currDealTaskInfo == null) {
			return false;
		}
		return currDealTaskInfo.getBooleanValue("iscanback", false); //
	}

	private boolean isCanSend() {
		if (this.currDealTaskInfo == null) {
			return false;
		}
		return !currDealTaskInfo.getBooleanValue("isHideTransSend", false); //
	}

	private boolean isCanHalfEnd() {
		if (this.currDealTaskInfo == null) {
			return false;
		}
		return currDealTaskInfo.getBooleanValue("canhalfend", false); //
	}

	public TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil;
	}

	private WorkflowUIUtil getWFUIUtil() {
		if (wfUIUtil != null) {
			return wfUIUtil; //
		}
		wfUIUtil = new WorkflowUIUtil();
		return wfUIUtil; //
	}

	public String getMessage() {
		return textarea_msg.getText();
	}

	public void setMessage(String newmsg) {
		textarea_msg.setText(newmsg);
	}

	public WLTButton getBtn_save() {
		return btn_save;
	}

	public void setBtn_save(WLTButton btn_save) {
		this.btn_save = btn_save;
	}

	public WLTButton getBtn_submit() {
		return btn_submit;
	}

	public void setBtn_submit(WLTButton btn_submit) {
		this.btn_submit = btn_submit;
	}

	public WLTButton getBtn_Reject() {
		return btn_reject;
	}

	public void setBtn_Reject(WLTButton btn_reject) {
		this.btn_reject = btn_reject;
	}

	public WLTButton getBtn_end() {
		return btn_end;
	}

	public void setBtn_end(WLTButton btn_end) {
		this.btn_end = btn_end;
	}

	public WLTButton getBtn_monitor() {
		return btn_monitor;
	}

	public void setBtn_monitor(WLTButton btn_monitor) {
		this.btn_monitor = btn_monitor;
	}

	public WLTButton getBtn_Confirm() {
		return btn_ccConfirm;
	}

	public void setBtn_ccConfirm(WLTButton btn_ccConfirm) {
		this.btn_ccConfirm = btn_ccConfirm;
	}

	public WLTButton getBtn_calcel() {
		return btn_calcel;
	}

	public void setBtn_calcel(WLTButton btn_calcel) {
		this.btn_calcel = btn_calcel;
	}

	public WLTButton getBtn_Histsave() {
		return btn_Histsave;
	}

	public void setBtn_Histsave(WLTButton btn_Histsave) {
		this.btn_Histsave = btn_Histsave;
	}

	// ��ȡ�߶��찴ť �����/2012-11-28��
	public WLTButton getBtn_mind() {
		return btn_mind;
	}

	public void setBtn_mind(WLTButton btn_mind) {
		this.btn_mind = btn_mind;
	}

	// ͨ��UI�����������ð�·������ť����
	public void setStr_wf_halfend_endbtn_setBy_Intercept(String[] btn_name) {
		str_wf_halfend_endbtn_setBy_Intercept = btn_name;
	}

	// ��ȡ�����
	public JPanel getMsgPanel() {
		if (dealMsgPanel == null) {
			dealMsgPanel = getDealMsgPanel();
		}
		return dealMsgPanel;
	}

}
