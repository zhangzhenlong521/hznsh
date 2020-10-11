package cn.com.infostrategy.ui.mdata.styletemplet;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.AbstractCustomerButtonBarPanel;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowDealDialog;
import cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog;
import cn.com.infostrategy.ui.workflow.engine.WorkflowUIUtil;

/**
 * ʮ����ģ��Ĺ�����������!!
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel extends AbstractWorkPanel implements ActionListener {

	public static int CARDTYPE = 1, LISTTYPE = 2;
	public static final String BTN_INSERT = "�½�"; //
	public static final String BTN_DELETE = "ɾ��"; //
	public static final String BTN_EDIT = "�޸�"; //
	public static final String BTN_SAVE = "����"; //
	public static final String BTN_SAVE_RETURN = "���淵��"; //
	public static final String BTN_CANCEL_RETURN = "ȡ������"; //
	public static final String BTN_RETURN = "����"; //
	public static final String BTN_SEARCH = "��ѯ"; //
	public static final String BTN_LIST = "���"; //
	public static final String BTN_REFRESH = "ˢ��"; //
	public static final String BTN_PRINT = "��ӡ"; //��ӡ

	public static final String BTN_QUICKSEARCH = "���ٲ�ѯ"; //

	//��������ť....
	public static final String BTN_WORKFLOW_SUBMIT = "�ύ"; ////�������ύ!!
	public static final String BTN_WORKFLOW_REJECT = "�ܾ�"; ////�������ܾ�
	public static final String BTN_WORKFLOW_BACK = "�˻�"; ////������������һ��
	public static final String BTN_WORKFLOW_CANCEL = "ȡ��"; ////������������һ��
	public static final String BTN_WORKFLOW_HOLD = "��ͣ"; ////��������ͣ
	public static final String BTN_WORKFLOW_RESTART = "����"; ////����������
	public static final String BTN_WORKFLOW_MONITOR = "���̼��"; //���̼��

	protected JButton btn_insert, btn_delete, btn_edit, btn_save, btn_save_return, btn_cancel_return, btn_search, btn_return, btn_list, btn_quicksearch, btn_refresh, btn_print;
	protected JButton btn_workflow_submit, btn_workflow_reject, btn_workflow_back, btn_workflow_cancel, btn_workflow_monitor, btn_workflow_hold, btn_workflow_restart; //������ϵͳ��ť

	private Vector v_btns = new Vector(); //

	private Vector v_workflow_btns = new Vector(); //
	private JPanel sysBtnPanel = null; //ϵͳ��ť���

	private AbstractCustomerButtonBarPanel custBtnPanel = null;

	//private BillVO workFlowDealBillVO = null;

	private JPanel workFlowPanel = null; //

	private int currShowType = 0; //

	//ʮ�ַ��ģ�嶼����Ҫ�еĳ��󷽷�!!!
	public abstract String getCustBtnPanelName(); //�Զ����������,��ĳ�ַ��default��ʵ��

	public abstract boolean isShowsystembutton(); //�Ƿ���ʾϵͳ��ť���,��ĳ�ַ��default��ʵ��

	public abstract boolean isCanWorkFlowDeal(); //�Ƿ���Դ�������

	public abstract boolean isCanWorkFlowMonitor(); //�Ƿ���Լ�ع�����

	public abstract boolean isCanInsert(); //�Ƿ���������,��ĳ�ַ��Abstract��ʵ��

	public abstract boolean isCanDelete(); //�Ƿ�����ɾ��,��ĳ�ַ��Abstract��ʵ��

	public abstract boolean isCanEdit(); //�Ƿ�����༭,��ĳ�ַ��Abstract��ʵ��..

	public abstract String getUiinterceptor(); //ui��������������,��ĳ�ַ��default��ʵ��..

	public abstract String getBsinterceptor(); //bs�������������� ,��ĳ�ַ��default��ʵ��

	/**
	 * ��ʼ��
	 */
	public void initialize() {
		//��ͨ��ť
		btn_insert = new WLTButton(UIUtil.getLanguage(BTN_INSERT)); //
		btn_edit = new WLTButton(UIUtil.getLanguage(BTN_EDIT)); //�༭
		btn_delete = new WLTButton(UIUtil.getLanguage(BTN_DELETE));
		btn_save = new WLTButton(UIUtil.getLanguage(BTN_SAVE)); //����
		btn_save_return = new WLTButton(UIUtil.getLanguage(BTN_SAVE_RETURN)); //���淵��
		btn_cancel_return = new WLTButton(UIUtil.getLanguage(BTN_CANCEL_RETURN)); //ȡ������
		btn_search = new WLTButton(UIUtil.getLanguage(BTN_SEARCH)); //��ѯ
		btn_return = new WLTButton(UIUtil.getLanguage(BTN_RETURN)); //����
		btn_list = new WLTButton(UIUtil.getLanguage(BTN_LIST)); //�鿴
		btn_quicksearch = new WLTButton(UIUtil.getLanguage(BTN_QUICKSEARCH)); //���ٲ�ѯ
		btn_refresh = new WLTButton(UIUtil.getLanguage(BTN_REFRESH)); //ˢ��
		btn_print = new WLTButton(UIUtil.getLanguage(BTN_PRINT)); //��ӡ

		//��������ť
		btn_workflow_submit = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_SUBMIT), "office_036.gif"); //�������ύ
		btn_workflow_reject = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_REJECT), "closewindow.gif"); //�������ܾ�
		btn_workflow_back = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_BACK), "office_020.gif"); //������������һ��
		btn_workflow_cancel = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_CANCEL), "undo.gif"); //������������һ��
		btn_workflow_hold = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_HOLD), "office_047.gif"); //��������ͣ
		btn_workflow_restart = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_RESTART), "office_048.gif"); //����������.
		btn_workflow_monitor = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_MONITOR), "office_046.gif"); //���̼��

		v_btns.add(btn_insert); //
		v_btns.add(btn_edit); //
		v_btns.add(btn_delete); //
		v_btns.add(btn_save); //
		v_btns.add(btn_save_return); //
		v_btns.add(btn_cancel_return); //
		v_btns.add(btn_search); //
		v_btns.add(btn_return); //
		v_btns.add(btn_list); //
		v_btns.add(btn_quicksearch); //
		v_btns.add(btn_refresh); //
		v_btns.add(btn_print); //��ӡ��ť

		v_workflow_btns.add(btn_workflow_submit); //�������ύ
		v_workflow_btns.add(btn_workflow_reject); //�������ܾ�
		v_workflow_btns.add(btn_workflow_back); //������������һ��
		v_workflow_btns.add(btn_workflow_cancel); //������������һ��
		v_workflow_btns.add(btn_workflow_hold); //��������ͣ
		v_workflow_btns.add(btn_workflow_restart); //����������
		v_workflow_btns.add(btn_workflow_monitor); //������

		btn_insert.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_save.addActionListener(this);
		btn_save_return.addActionListener(this);
		btn_cancel_return.addActionListener(this);
		btn_search.addActionListener(this);
		btn_return.addActionListener(this);
		btn_list.addActionListener(this);
		btn_refresh.addActionListener(this);
		btn_print.addActionListener(this);

		//��������ť!
		btn_workflow_submit.addActionListener(this); //�����ύ
		btn_workflow_reject.addActionListener(this); //���ܾ̾�
		btn_workflow_back.addActionListener(this); //���̻���
		btn_workflow_cancel.addActionListener(this); //���̻���
		btn_workflow_hold.addActionListener(this); //������ͣ.
		btn_workflow_restart.addActionListener(this); //���̼���.
		btn_workflow_monitor.addActionListener(this); //���̼��!!
	}

	/**
	 * ��ť����Ķ���.
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == btn_insert) {
				onInsert(); //
			} else if (e.getSource() == btn_delete) {
				onDelete(); //
			} else if (e.getSource() == btn_edit) {
				onEdit(); //
			} else if (e.getSource() == btn_save) {
				onSave(); //
			} else if (e.getSource() == btn_save_return) {
				onSaveReturn(); //
			} else if (e.getSource() == btn_cancel_return) {
				onCancelReturn(); //
			} else if (e.getSource() == btn_search) {
				onSearch(); //
			} else if (e.getSource() == btn_return) {
				onReturn(); //
			} else if (e.getSource() == btn_list) {
				onList(); //
			} else if (e.getSource() == btn_refresh) {
				onRefresh(); //
			} else if (e.getSource() == btn_print) {
				onPrint(); //
			} else if (e.getSource() == btn_workflow_submit) {
				onWorkFlowSubmit(); //�ύ
			} else if (e.getSource() == btn_workflow_reject) {
				onWorkFlowReject(); //��
			} else if (e.getSource() == btn_workflow_back) {
				onWorkFlowBack(); //
			} else if (e.getSource() == btn_workflow_cancel) {
				onWorkFlowCancel(); //
			} else if (e.getSource() == btn_workflow_hold) { //������ͣ
				onWorkFlowHold(); //������ͣ
			} else if (e.getSource() == btn_workflow_restart) { //���̼���
				onWorkFlowRestart(); //���̼���
			} else if (e.getSource() == btn_workflow_monitor) {
				onWorkFlowMonitor(); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	protected void onInsert() throws Exception {
	}

	protected void onDelete() throws Exception {
	}

	protected void onEdit() throws Exception {
	}

	protected void onSave() throws Exception {
	}

	protected void onSaveReturn() throws Exception {
	}

	protected void onCancelReturn() throws Exception {
	}

	protected void onSearch() throws Exception {
	}

	protected void onReturn() throws Exception {
	}

	protected void onList() throws Exception {
	}

	protected void onRefresh() throws Exception {
	}

	protected void onPrint() throws Exception {
	}

	/**
	 * �������ύ...
	 * @throws Exception
	 */
	public void onWorkFlowDeal(String _dealtype) throws Exception {
		try {
			BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "No Record will be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "No WFPRINSTANCEID,Can't be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			//����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
			//��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
			String str_wfinstance = billVO.getStringValue("WFPRINSTANCEID"); //����ʵ��ID..
			if (str_wfinstance == null || str_wfinstance.equals("")) { //�������ʵ���ֶε�ֵΪ��,��˵����Ҫ��������!!
				str_wfinstance = startWorkFlow(billVO); //�ȴ���һ������
				if (str_wfinstance != null) {
					processDeal(str_wfinstance, billVO, _dealtype); //������ִ�и�����
				}
			} else { //���̽���
				processDeal(str_wfinstance, billVO, _dealtype); //���������е�
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ֱ�Ӵ��������е�����
	 * @param _prinstanceId
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	private void processDeal(String _prinstanceId, BillVO _billVO, String _dealtype) throws Exception {
		String str_loginuserid = ClientEnvironment.getInstance().getLoginUserID(); //
		String str_loginuserDeptID = ClientEnvironment.getCurrLoginUserVO().getBlDeptId(); //
		WorkFlowServiceIfc service = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class);
		WFParVO firstTaskVO = service.getFirstTaskVO(_prinstanceId, null, str_loginuserid, str_loginuserDeptID, _billVO, _dealtype); //ȡ������!
		if (firstTaskVO == null) {
			MessageBox.show(this, "No My Task!");
			return; //
		}

		WFParVO secondCallVO = null; //�ڶ����ٴ�����Ĳ���VO,��ʵ���ǵ�һ��ȡ�õĲ����ٴ��ύ������!!
		if ((firstTaskVO.isIsprocessed() && firstTaskVO.isIsassignapprover()) || firstTaskVO.isIsneedmsg()) //������ս��߲�����Ҫ�˹�ѡ�������,������Ҫ��������,�򵯳��Ի���,����ֱ���ύ
		{
			WorkFlowDealDialog dialog = new WorkFlowDealDialog(this, _billVO, firstTaskVO, _dealtype); //
			dialog.setVisible(true); //
			if (dialog.getClosetype() == 1) { //��������ȷ������
				secondCallVO = dialog.getReturnVO(); //
			}
		} else { //ֱ���ٴ��ύ
			secondCallVO = firstTaskVO; //
		}

		if (secondCallVO != null) {
			BillVO returnBillVO = service.secondCall(secondCallVO, str_loginuserid, _billVO, _dealtype); //�ڶ����ٴ�����!!
			writeBackWFPrinstance(returnBillVO); //��ҳ����,��д����ʵ������(�Ժ�Ҫ�ĳɻ�д����BillVO)
			refreshWorkFlowPanel(_prinstanceId); //ˢ���������!!
		}
	}

	private String startWorkFlow(BillVO billVO) throws Exception {
		if (!billVO.isHaveKey("billtype") || !billVO.isHaveKey("busitype")) {
			MessageBox.show(this, "No BillType and Busitype,Cant't be deal!", WLTConstants.MESSAGE_WARN); //
			return null; //
		}

		if (billVO.getEditType().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { //����Ǵ�������״̬,�����ύ������
			MessageBox.show(this, "Order is not save,Cant't be Submit!", WLTConstants.MESSAGE_WARN); //
			return null; //
		}

		String str_billtype = billVO.getStringValue("billtype"); //��������..
		String str_busitype = billVO.getStringValue("busitype"); //ҵ������..
		try {
			String str_sql = "select * from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"; //
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, str_sql); //
			if (vos.length == 0) {
				MessageBox.show(this, "û��ΪBillType[" + str_billtype + "],BusiType[" + str_busitype + "]ָ������,���ܽ������̴���!", WLTConstants.MESSAGE_INFO); //
				return null;
			}

			String str_processId = vos[0].getStringValue("processid"); //
			if (str_processId == null || str_processId.trim().equals("")) {
				MessageBox.show(this, "��ΪBillType[" + str_billtype + "],BusiType[" + str_busitype + "]ָ�����̵�����IDΪ��,���ܽ������̴���!", WLTConstants.MESSAGE_INFO); //
				return null;
			}

			String str_prinstanceid = new WorkflowUIUtil().startWorkFlow(this, billVO); //����һ������!!
			return str_prinstanceid; //
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
			return null;
		}
	}

	/**
	 * �ύ����
	 */
	protected void onWorkFlowSubmit() throws Exception {
		onWorkFlowDeal("SUBMIT"); //�ύ����!!
	}

	/**
	 * ����������֮�ܾ�����
	 * @throws Exception
	 */
	protected void onWorkFlowReject() throws Exception {
		onWorkFlowDeal("REJECT"); //�ܾ�����!!
	}

	/**
	 * ����������֮���˲���
	 * ��վ������,��������������
	 * @throws Exception
	 */
	protected void onWorkFlowBack() throws Exception {
		onWorkFlowDeal("BACK"); //���˲���
	}

	/**
	 * ����������֮ȡ������
	 * ��վ������,���Ҹ��ύ������,������������
	 * @throws Exception
	 */
	protected void onWorkFlowCancel() throws Exception {
		try {
			BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "No Records can be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "No WFPRINSTANCEID,Can't be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			//����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
			//��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
			String str_wfinstance = billVO.getStringValue("WFPRINSTANCEID"); //����ʵ��ID..
			if (str_wfinstance != null && !str_wfinstance.equals("")) {
				WorkFlowServiceIfc service = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class);
				String str_loginuserid = ClientEnvironment.getInstance().getLoginUserID(); //
				service.cancelTask(str_wfinstance, null, null, str_loginuserid, null);
				refreshWorkFlowPanel(str_wfinstance); //
				MessageBox.show(this, "Cancel Success"); //
			} else {
				MessageBox.show(this, "No WFPRINSTANCEID,Can't be deal!", WLTConstants.MESSAGE_WARN); //
				return;
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ������ͣ..
	 * @throws Exception
	 */
	protected void onWorkFlowHold() throws Exception {
		try {
			BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "No Record will be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "No WFPRINSTANCEID,Can't be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			//����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
			//��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
			String str_wfinstance = billVO.getStringValue("WFPRINSTANCEID"); //����ʵ��ID..
			if (str_wfinstance == null || str_wfinstance.trim().equals("")) {
				MessageBox.show(this, "WFPRINSTANCEID is null,Can't be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			WorkFlowServiceIfc service = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class);
			service.holdWorkflow(str_wfinstance, ClientEnvironment.getInstance().getLoginUserID()); //��ͣ����...
			refreshWorkFlowPanel(str_wfinstance); //ˢ���������!!
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ���̼���..
	 * @throws Exception
	 */
	protected void onWorkFlowRestart() throws Exception {
		try {
			BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "No Record will be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "No WFPRINSTANCEID,Can't be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			//����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
			//��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
			String str_wfinstance = billVO.getStringValue("WFPRINSTANCEID"); //����ʵ��ID..
			if (str_wfinstance == null || str_wfinstance.trim().equals("")) {
				MessageBox.show(this, "WFPRINSTANCEID is null,Can't be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			WorkFlowServiceIfc service = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class);
			service.restartWorkflow(str_wfinstance, ClientEnvironment.getInstance().getLoginUserID()); //������������..  
			refreshWorkFlowPanel(str_wfinstance); //ˢ���������!!
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ���������...
	 * @throws Exception
	 */
	protected void onWorkFlowMonitor() throws Exception {
		BillVO billVO = getWorkFlowDealBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "û�ж�Ӧ�Ĵ�������!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		if (!billVO.isHaveKey("WFPRINSTANCEID")) {
			MessageBox.show(this, "û��BillType��Busitype,WFPRINSTANCEID��Ϣ,���ܽ������̴���!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		//����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
		//��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
		String str_wfinstance = billVO.getStringValue("WFPRINSTANCEID"); //����ʵ��ID..
		if (str_wfinstance == null || str_wfinstance.equals("")) { //��������!�������ʵ��Ϊ��
			MessageBox.show(this, "û�а󶨵�ĳһʵ��!", WLTConstants.MESSAGE_WARN); //
			return;
		} else {
			WorkflowMonitorDialog dialog = new WorkflowMonitorDialog(this, str_wfinstance, billVO);
			dialog.setMaxWindowMenuBar();
			dialog.setVisible(true); //
		}
	}

	/**
	 * ȡ�ù����������BillVO
	 * @return
	 */
	public BillVO getWorkFlowDealBillVO() {
		return null;
	}

	/**
	 * ��ҳ���д����ʵ��
	 * @param _prInstanceId
	 */
	public void writeBackWFPrinstance(BillVO _billVO) {

	}

	/**
	 * ϵͳ��ť��,�ǳ��ؼ�,���ܱ�����
	 * @return
	 */
	protected final JPanel getBtnBarPanel() {
		JPanel panel_btnbar = new JPanel(); //�������..
		panel_btnbar.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		panel_btnbar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1)); //ˮƽ����

		//ϵͳ��ť��...

		if (isShowsystembutton()) { //�����ʾϵͳ��ť��
			JPanel panel_sysbtn = getSysBtnPanel(); //ϵͳ��ť��...
			if (panel_sysbtn != null) {
				panel_btnbar.add(panel_sysbtn); //
			}
		}

		//�û��Զ��尴ť��...
		JPanel custBtnPanel = getCustBtnPanel();
		if (custBtnPanel != null) {
			panel_btnbar.add(custBtnPanel); //
		}

		//�����ʾWorkFlow��ť,����빤�������...
		hiddenAllSysButtons(); //һ��ʼ�Ƚ����а�ť����!!
		return panel_btnbar; //
	}

	/***
	 * ϵͳ��ť��,��Ҫ������
	 * @return
	 */
	protected final JPanel getSysBtnPanel() {
		if (sysBtnPanel == null) {
			sysBtnPanel = new JPanel(); //
			sysBtnPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor);
			sysBtnPanel.setLayout(getFlowLayout()); //
			for (int i = 0; i < v_btns.size(); i++) {
				sysBtnPanel.add((JButton) v_btns.get(i)); //�Ƚ����а�Ť����
			}
		}
		return sysBtnPanel;
	}

	/**
	 * �������а�ť
	 */
	public void hiddenAllSysButtons() {
		for (int i = 0; i < v_btns.size(); i++) {
			((JButton) v_btns.get(i)).setVisible(false); //�������а�ť!!
		}
		for (int i = 0; i < v_workflow_btns.size(); i++) {
			((JButton) v_workflow_btns.get(i)).setVisible(false); //�������а�ť!!
		}
	}

	/**
	 * �û��Զ��尴ť�����
	 * @return
	 */
	public final AbstractCustomerButtonBarPanel getCustBtnPanel() {
		if (custBtnPanel == null) {
			if (getCustBtnPanelName() != null) {
				try {
					custBtnPanel = (AbstractCustomerButtonBarPanel) Class.forName(getCustBtnPanelName()).newInstance(); //�����û��Զ��尴ť��
					custBtnPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
					custBtnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1)); //
					custBtnPanel.setParentWorkPanel(this); //
					custBtnPanel.initialize(); //
				} catch (Exception ex) {
					MessageBox.showException(this, ex);
				}
			}
		}
		return custBtnPanel;
	}

	/**
	 * ���������
	 * @return
	 */
	public JPanel getWorkFlowPanel() {
		if (workFlowPanel == null) {
			workFlowPanel = new JPanel(); //
			workFlowPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1)); //

			for (int i = 0; i < v_workflow_btns.size(); i++) {
				JButton btn_wf = ((JButton) v_workflow_btns.get(i)); //
				workFlowPanel.add(btn_wf); //
				btn_wf.setVisible(false); //�������а�ť!!
			}
		}
		return workFlowPanel;
	}

	/**
	 * 
	 * @param _prinstanceid
	 */
	public void refreshWorkFlowPanel(String _prinstanceid) {
		btn_workflow_submit.setVisible(false); //��Ĭ������Ϊ����
		btn_workflow_reject.setVisible(false); //��Ĭ������Ϊ����
		btn_workflow_back.setVisible(false); //��Ĭ������Ϊ����
		btn_workflow_hold.setVisible(false); //��Ĭ������Ϊ����
		btn_workflow_restart.setVisible(false); //��Ĭ������Ϊ����
		btn_workflow_monitor.setVisible(false); //��Ĭ������Ϊ����

		if (_prinstanceid == null || _prinstanceid.equals("")) { //���û������ʵ��
			btn_workflow_submit.setVisible(true); //��Ĭ������Ϊ����
			return;
		}

		try {
			String str_sql_1 = "select status from pub_wf_prinstance where id=" + _prinstanceid; //
			String str_status = UIUtil.getHashVoArrayByDS(null, str_sql_1)[0].getStringValue("status"); //
			if (str_status.equals("HOLD")) {
				btn_workflow_restart.setVisible(true); //
				btn_workflow_monitor.setVisible(true); //
			} else if (str_status.equals("END")) {
				btn_workflow_monitor.setVisible(true); //
			} else if (str_status.equals("RUN")) {
				String str_userid = ClientEnvironment.getInstance().getLoginUserID(); //��ȡ�õ�¼��Ա������!!
				String str_sql_2 = "select * from v_pub_wf_dealpool_1 where prinstanceid=" + _prinstanceid + " and participant_user=" + str_userid + " and issubmit='N' and isprocess='N'"; //

				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql_2); //
				if (hvs != null && hvs.length > 0) {
					for (int i = 0; i < hvs.length; i++) {
						if (hvs[i].getIntegerValue("submitcount").intValue() > 0) {
							btn_workflow_submit.setVisible(true); //
						}

						if (hvs[i].getIntegerValue("rejectcount").intValue() > 0) { //����оܾ��ĵ��ߣ�����ʾ�ܾ��İ�ť
							btn_workflow_reject.setVisible(true); //
						}

						if (hvs[i].getStringValue("iscanback").equals("Y")) { //���������ˣ�����ʾ���˰�ť
							btn_workflow_back.setVisible(true); //
						}

						if (hvs[i].getStringValue("curractivitytype").equals("END")) {
							btn_workflow_submit.setVisible(true); //
						}
					}
				}

				btn_workflow_hold.setVisible(true); //��Ĭ������Ϊ����
				btn_workflow_monitor.setVisible(true); //��ذ�ť��Զ��ʾ!!!
				btn_workflow_cancel.setVisible(true); //
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	public int getCurrShowType() {
		return currShowType;
	}

	public void setCurrShowType(int currShowType) {
		this.currShowType = currShowType;
	}

	/**
	 * ˮƽ����..
	 * @return
	 */
	public FlowLayout getFlowLayout() {
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 7, 1);
		return flowLayout; //
	}

	/**
	 * ȡ��Ԫ���ݷ���!!
	 * @return
	 * @throws Exception
	 */
	protected StyleTempletServiceIfc getMetaService() throws Exception {
		StyleTempletServiceIfc service = (StyleTempletServiceIfc) RemoteServiceFactory.getInstance().lookUpService(StyleTempletServiceIfc.class);
		return service;
	}

	/**
	 * ȡ��ĳ��ϵͳ��ť!
	 * @param _btnname
	 * @return
	 */
	public JButton getSysButton(String _btnname) {
		if (_btnname.equals(BTN_INSERT)) {
			return btn_insert;
		} else if (_btnname.equals(BTN_DELETE)) {
			return btn_delete;
		} else if (_btnname.equals(BTN_EDIT)) {
			return btn_edit;
		} else if (_btnname.equals(BTN_SAVE)) {
			return btn_save;
		} else if (_btnname.equals(BTN_SAVE_RETURN)) {
			return btn_save_return;
		} else if (_btnname.equals(BTN_CANCEL_RETURN)) {
			return btn_cancel_return;
		} else if (_btnname.equals(BTN_SEARCH)) {
			return btn_search;
		} else if (_btnname.equals(BTN_LIST)) {
			return btn_list;
		} else if (_btnname.equals(BTN_QUICKSEARCH)) {
			return btn_quicksearch;
		} else if (_btnname.equals(BTN_RETURN)) {
			return btn_return;
		} else if (_btnname.equals(BTN_REFRESH)) {
			return btn_refresh;
		} else if (_btnname.equals(BTN_PRINT)) {
			return btn_print; //
		} else if (_btnname.equals(BTN_WORKFLOW_SUBMIT)) {
			return btn_workflow_submit; //
		} else if (_btnname.equals(BTN_WORKFLOW_REJECT)) {
			return btn_workflow_reject; //
		} else if (_btnname.equals(BTN_WORKFLOW_BACK)) {
			return btn_workflow_back; //
		} else if (_btnname.equals(BTN_WORKFLOW_CANCEL)) {
			return btn_workflow_cancel; //
		} else if (_btnname.equals(BTN_WORKFLOW_HOLD)) {
			return btn_workflow_hold; //
		} else if (_btnname.equals(BTN_WORKFLOW_RESTART)) {
			return btn_workflow_restart; //
		} else if (_btnname.equals(BTN_WORKFLOW_MONITOR)) {
			return btn_workflow_monitor; //
		} else {
			return null;
		}
	}

	protected void updateButtonUI() {
		btn_insert.updateUI(); //
		btn_delete.updateUI(); //
		btn_edit.updateUI(); //
		btn_save.updateUI(); //
		btn_save_return.updateUI(); //
		btn_cancel_return.updateUI(); //
		btn_search.updateUI(); //
		btn_list.updateUI(); //
		btn_quicksearch.updateUI(); //
		btn_return.updateUI(); //
		btn_refresh.updateUI(); //
		btn_print.updateUI(); //
		btn_workflow_submit.updateUI(); //
		btn_workflow_reject.updateUI(); //
		btn_workflow_back.updateUI(); //
		btn_workflow_cancel.updateUI(); //
		btn_workflow_monitor.updateUI(); //
	}

}
