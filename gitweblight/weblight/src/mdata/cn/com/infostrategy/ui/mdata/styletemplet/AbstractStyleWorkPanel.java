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
 * 十大风格模板的工作面板抽象类!!
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel extends AbstractWorkPanel implements ActionListener {

	public static int CARDTYPE = 1, LISTTYPE = 2;
	public static final String BTN_INSERT = "新建"; //
	public static final String BTN_DELETE = "删除"; //
	public static final String BTN_EDIT = "修改"; //
	public static final String BTN_SAVE = "保存"; //
	public static final String BTN_SAVE_RETURN = "保存返回"; //
	public static final String BTN_CANCEL_RETURN = "取消返回"; //
	public static final String BTN_RETURN = "返回"; //
	public static final String BTN_SEARCH = "查询"; //
	public static final String BTN_LIST = "浏览"; //
	public static final String BTN_REFRESH = "刷新"; //
	public static final String BTN_PRINT = "打印"; //打印

	public static final String BTN_QUICKSEARCH = "快速查询"; //

	//工作流按钮....
	public static final String BTN_WORKFLOW_SUBMIT = "提交"; ////工作流提交!!
	public static final String BTN_WORKFLOW_REJECT = "拒绝"; ////工作流拒绝
	public static final String BTN_WORKFLOW_BACK = "退回"; ////工作流返回上一步
	public static final String BTN_WORKFLOW_CANCEL = "取消"; ////工作流返回上一步
	public static final String BTN_WORKFLOW_HOLD = "暂停"; ////工作流暂停
	public static final String BTN_WORKFLOW_RESTART = "继续"; ////工作流继续
	public static final String BTN_WORKFLOW_MONITOR = "流程监控"; //流程监控

	protected JButton btn_insert, btn_delete, btn_edit, btn_save, btn_save_return, btn_cancel_return, btn_search, btn_return, btn_list, btn_quicksearch, btn_refresh, btn_print;
	protected JButton btn_workflow_submit, btn_workflow_reject, btn_workflow_back, btn_workflow_cancel, btn_workflow_monitor, btn_workflow_hold, btn_workflow_restart; //工作流系统按钮

	private Vector v_btns = new Vector(); //

	private Vector v_workflow_btns = new Vector(); //
	private JPanel sysBtnPanel = null; //系统按钮面板

	private AbstractCustomerButtonBarPanel custBtnPanel = null;

	//private BillVO workFlowDealBillVO = null;

	private JPanel workFlowPanel = null; //

	private int currShowType = 0; //

	//十种风格模板都必须要有的抽象方法!!!
	public abstract String getCustBtnPanelName(); //自定义面板名称,被某种风格default类实现

	public abstract boolean isShowsystembutton(); //是否显示系统按钮面板,被某种风格default类实现

	public abstract boolean isCanWorkFlowDeal(); //是否可以处理工作流

	public abstract boolean isCanWorkFlowMonitor(); //是否可以监控工作流

	public abstract boolean isCanInsert(); //是否允许新增,被某种风格Abstract类实现

	public abstract boolean isCanDelete(); //是否允许删除,被某种风格Abstract类实现

	public abstract boolean isCanEdit(); //是否允许编辑,被某种风格Abstract类实现..

	public abstract String getUiinterceptor(); //ui端拦截器的名称,被某种风格default类实现..

	public abstract String getBsinterceptor(); //bs端拦截器的名称 ,被某种风格default类实现

	/**
	 * 初始化
	 */
	public void initialize() {
		//普通按钮
		btn_insert = new WLTButton(UIUtil.getLanguage(BTN_INSERT)); //
		btn_edit = new WLTButton(UIUtil.getLanguage(BTN_EDIT)); //编辑
		btn_delete = new WLTButton(UIUtil.getLanguage(BTN_DELETE));
		btn_save = new WLTButton(UIUtil.getLanguage(BTN_SAVE)); //保存
		btn_save_return = new WLTButton(UIUtil.getLanguage(BTN_SAVE_RETURN)); //保存返回
		btn_cancel_return = new WLTButton(UIUtil.getLanguage(BTN_CANCEL_RETURN)); //取消返回
		btn_search = new WLTButton(UIUtil.getLanguage(BTN_SEARCH)); //查询
		btn_return = new WLTButton(UIUtil.getLanguage(BTN_RETURN)); //返回
		btn_list = new WLTButton(UIUtil.getLanguage(BTN_LIST)); //查看
		btn_quicksearch = new WLTButton(UIUtil.getLanguage(BTN_QUICKSEARCH)); //快速查询
		btn_refresh = new WLTButton(UIUtil.getLanguage(BTN_REFRESH)); //刷新
		btn_print = new WLTButton(UIUtil.getLanguage(BTN_PRINT)); //打印

		//工作流按钮
		btn_workflow_submit = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_SUBMIT), "office_036.gif"); //工作流提交
		btn_workflow_reject = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_REJECT), "closewindow.gif"); //工作流拒绝
		btn_workflow_back = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_BACK), "office_020.gif"); //工作流返回上一步
		btn_workflow_cancel = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_CANCEL), "undo.gif"); //工作流返回上一步
		btn_workflow_hold = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_HOLD), "office_047.gif"); //工作流暂停
		btn_workflow_restart = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_RESTART), "office_048.gif"); //工作流继续.
		btn_workflow_monitor = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_MONITOR), "office_046.gif"); //流程监控

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
		v_btns.add(btn_print); //打印按钮

		v_workflow_btns.add(btn_workflow_submit); //工作流提交
		v_workflow_btns.add(btn_workflow_reject); //工作流拒绝
		v_workflow_btns.add(btn_workflow_back); //工作流返回上一步
		v_workflow_btns.add(btn_workflow_cancel); //工作流返回上一步
		v_workflow_btns.add(btn_workflow_hold); //工作流暂停
		v_workflow_btns.add(btn_workflow_restart); //工作流继续
		v_workflow_btns.add(btn_workflow_monitor); //工作流

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

		//工作流按钮!
		btn_workflow_submit.addActionListener(this); //流程提交
		btn_workflow_reject.addActionListener(this); //流程拒绝
		btn_workflow_back.addActionListener(this); //流程回退
		btn_workflow_cancel.addActionListener(this); //流程回退
		btn_workflow_hold.addActionListener(this); //流程暂停.
		btn_workflow_restart.addActionListener(this); //流程继续.
		btn_workflow_monitor.addActionListener(this); //流程监控!!
	}

	/**
	 * 按钮处理的动作.
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
				onWorkFlowSubmit(); //提交
			} else if (e.getSource() == btn_workflow_reject) {
				onWorkFlowReject(); //拒
			} else if (e.getSource() == btn_workflow_back) {
				onWorkFlowBack(); //
			} else if (e.getSource() == btn_workflow_cancel) {
				onWorkFlowCancel(); //
			} else if (e.getSource() == btn_workflow_hold) { //流程暂停
				onWorkFlowHold(); //流程暂停
			} else if (e.getSource() == btn_workflow_restart) { //流程继续
				onWorkFlowRestart(); //流程继续
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
	 * 工作流提交...
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

			//如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
			//根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
			String str_wfinstance = billVO.getStringValue("WFPRINSTANCEID"); //流程实例ID..
			if (str_wfinstance == null || str_wfinstance.equals("")) { //如果流程实例字段的值为空,则说明是要启动流程!!
				str_wfinstance = startWorkFlow(billVO); //先创建一个流程
				if (str_wfinstance != null) {
					processDeal(str_wfinstance, billVO, _dealtype); //再立即执行该流程
				}
			} else { //流程进行
				processDeal(str_wfinstance, billVO, _dealtype); //处理流程中的
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 直接处理流程中的数据
	 * @param _prinstanceId
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	private void processDeal(String _prinstanceId, BillVO _billVO, String _dealtype) throws Exception {
		String str_loginuserid = ClientEnvironment.getInstance().getLoginUserID(); //
		String str_loginuserDeptID = ClientEnvironment.getCurrLoginUserVO().getBlDeptId(); //
		WorkFlowServiceIfc service = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class);
		WFParVO firstTaskVO = service.getFirstTaskVO(_prinstanceId, null, str_loginuserid, str_loginuserDeptID, _billVO, _dealtype); //取得任务!
		if (firstTaskVO == null) {
			MessageBox.show(this, "No My Task!");
			return; //
		}

		WFParVO secondCallVO = null; //第二次再次请求的参数VO,其实就是第一次取得的参数再次提交服务器!!
		if ((firstTaskVO.isIsprocessed() && firstTaskVO.isIsassignapprover()) || firstTaskVO.isIsneedmsg()) //如果是终结者并且需要人工选择参与者,或者需要输入批语,则弹出对话框,否则直接提交
		{
			WorkFlowDealDialog dialog = new WorkFlowDealDialog(this, _billVO, firstTaskVO, _dealtype); //
			dialog.setVisible(true); //
			if (dialog.getClosetype() == 1) { //如果点击了确定返回
				secondCallVO = dialog.getReturnVO(); //
			}
		} else { //直接再次提交
			secondCallVO = firstTaskVO; //
		}

		if (secondCallVO != null) {
			BillVO returnBillVO = service.secondCall(secondCallVO, str_loginuserid, _billVO, _dealtype); //第二次再次请求!!
			writeBackWFPrinstance(returnBillVO); //向页面上,回写流程实例主键(以后要改成回写整个BillVO)
			refreshWorkFlowPanel(_prinstanceId); //刷新流程面板!!
		}
	}

	private String startWorkFlow(BillVO billVO) throws Exception {
		if (!billVO.isHaveKey("billtype") || !billVO.isHaveKey("busitype")) {
			MessageBox.show(this, "No BillType and Busitype,Cant't be deal!", WLTConstants.MESSAGE_WARN); //
			return null; //
		}

		if (billVO.getEditType().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { //如果是处理新增状态,则不能提交工作流
			MessageBox.show(this, "Order is not save,Cant't be Submit!", WLTConstants.MESSAGE_WARN); //
			return null; //
		}

		String str_billtype = billVO.getStringValue("billtype"); //单据类型..
		String str_busitype = billVO.getStringValue("busitype"); //业务类型..
		try {
			String str_sql = "select * from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"; //
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, str_sql); //
			if (vos.length == 0) {
				MessageBox.show(this, "没有为BillType[" + str_billtype + "],BusiType[" + str_busitype + "]指定流程,不能进行流程处理!", WLTConstants.MESSAGE_INFO); //
				return null;
			}

			String str_processId = vos[0].getStringValue("processid"); //
			if (str_processId == null || str_processId.trim().equals("")) {
				MessageBox.show(this, "有为BillType[" + str_billtype + "],BusiType[" + str_busitype + "]指定流程的流程ID为空,不能进行流程处理!", WLTConstants.MESSAGE_INFO); //
				return null;
			}

			String str_prinstanceid = new WorkflowUIUtil().startWorkFlow(this, billVO); //创建一个流程!!
			return str_prinstanceid; //
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
			return null;
		}
	}

	/**
	 * 提交操作
	 */
	protected void onWorkFlowSubmit() throws Exception {
		onWorkFlowDeal("SUBMIT"); //提交操作!!
	}

	/**
	 * 工作流处理之拒绝操作
	 * @throws Exception
	 */
	protected void onWorkFlowReject() throws Exception {
		onWorkFlowDeal("REJECT"); //拒绝操作!!
	}

	/**
	 * 工作流处理之回退操作
	 * 即站在下游,将该任务打回上游
	 * @throws Exception
	 */
	protected void onWorkFlowBack() throws Exception {
		onWorkFlowDeal("BACK"); //回退操作
	}

	/**
	 * 工作流处理之取消操作
	 * 即站在上游,将我刚提交的任务,从下游拉上来
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

			//如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
			//根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
			String str_wfinstance = billVO.getStringValue("WFPRINSTANCEID"); //流程实例ID..
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
	 * 流程暂停..
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

			//如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
			//根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
			String str_wfinstance = billVO.getStringValue("WFPRINSTANCEID"); //流程实例ID..
			if (str_wfinstance == null || str_wfinstance.trim().equals("")) {
				MessageBox.show(this, "WFPRINSTANCEID is null,Can't be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			WorkFlowServiceIfc service = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class);
			service.holdWorkflow(str_wfinstance, ClientEnvironment.getInstance().getLoginUserID()); //暂停流程...
			refreshWorkFlowPanel(str_wfinstance); //刷新流程面板!!
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 流程继续..
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

			//如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
			//根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
			String str_wfinstance = billVO.getStringValue("WFPRINSTANCEID"); //流程实例ID..
			if (str_wfinstance == null || str_wfinstance.trim().equals("")) {
				MessageBox.show(this, "WFPRINSTANCEID is null,Can't be deal!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			WorkFlowServiceIfc service = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class);
			service.restartWorkflow(str_wfinstance, ClientEnvironment.getInstance().getLoginUserID()); //重新启动流程..  
			refreshWorkFlowPanel(str_wfinstance); //刷新流程面板!!
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 工作流监控...
	 * @throws Exception
	 */
	protected void onWorkFlowMonitor() throws Exception {
		BillVO billVO = getWorkFlowDealBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "没有对应的处理数据!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		if (!billVO.isHaveKey("WFPRINSTANCEID")) {
			MessageBox.show(this, "没有BillType与Busitype,WFPRINSTANCEID信息,不能进行流程处理!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		//如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
		//根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
		String str_wfinstance = billVO.getStringValue("WFPRINSTANCEID"); //流程实例ID..
		if (str_wfinstance == null || str_wfinstance.equals("")) { //启动流程!如果流程实例为空
			MessageBox.show(this, "没有绑定到某一实例!", WLTConstants.MESSAGE_WARN); //
			return;
		} else {
			WorkflowMonitorDialog dialog = new WorkflowMonitorDialog(this, str_wfinstance, billVO);
			dialog.setMaxWindowMenuBar();
			dialog.setVisible(true); //
		}
	}

	/**
	 * 取得工作流处理的BillVO
	 * @return
	 */
	public BillVO getWorkFlowDealBillVO() {
		return null;
	}

	/**
	 * 向页面回写流程实例
	 * @param _prInstanceId
	 */
	public void writeBackWFPrinstance(BillVO _billVO) {

	}

	/**
	 * 系统按钮栏,非常关键,不能被覆盖
	 * @return
	 */
	protected final JPanel getBtnBarPanel() {
		JPanel panel_btnbar = new JPanel(); //整个面板..
		panel_btnbar.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		panel_btnbar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1)); //水平布局

		//系统按钮栏...

		if (isShowsystembutton()) { //如果显示系统按钮栏
			JPanel panel_sysbtn = getSysBtnPanel(); //系统按钮栏...
			if (panel_sysbtn != null) {
				panel_btnbar.add(panel_sysbtn); //
			}
		}

		//用户自定义按钮栏...
		JPanel custBtnPanel = getCustBtnPanel();
		if (custBtnPanel != null) {
			panel_btnbar.add(custBtnPanel); //
		}

		//如果显示WorkFlow按钮,则加入工作流面板...
		hiddenAllSysButtons(); //一开始先将所有按钮隐藏!!
		return panel_btnbar; //
	}

	/***
	 * 系统按钮栏,需要被覆盖
	 * @return
	 */
	protected final JPanel getSysBtnPanel() {
		if (sysBtnPanel == null) {
			sysBtnPanel = new JPanel(); //
			sysBtnPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor);
			sysBtnPanel.setLayout(getFlowLayout()); //
			for (int i = 0; i < v_btns.size(); i++) {
				sysBtnPanel.add((JButton) v_btns.get(i)); //先将所有按扭加入
			}
		}
		return sysBtnPanel;
	}

	/**
	 * 隐藏所有按钮
	 */
	public void hiddenAllSysButtons() {
		for (int i = 0; i < v_btns.size(); i++) {
			((JButton) v_btns.get(i)).setVisible(false); //隐藏所有按钮!!
		}
		for (int i = 0; i < v_workflow_btns.size(); i++) {
			((JButton) v_workflow_btns.get(i)).setVisible(false); //隐藏所有按钮!!
		}
	}

	/**
	 * 用户自定义按钮栏面板
	 * @return
	 */
	public final AbstractCustomerButtonBarPanel getCustBtnPanel() {
		if (custBtnPanel == null) {
			if (getCustBtnPanelName() != null) {
				try {
					custBtnPanel = (AbstractCustomerButtonBarPanel) Class.forName(getCustBtnPanelName()).newInstance(); //创建用户自定义按钮栏
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
	 * 工作流面板
	 * @return
	 */
	public JPanel getWorkFlowPanel() {
		if (workFlowPanel == null) {
			workFlowPanel = new JPanel(); //
			workFlowPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1)); //

			for (int i = 0; i < v_workflow_btns.size(); i++) {
				JButton btn_wf = ((JButton) v_workflow_btns.get(i)); //
				workFlowPanel.add(btn_wf); //
				btn_wf.setVisible(false); //隐藏所有按钮!!
			}
		}
		return workFlowPanel;
	}

	/**
	 * 
	 * @param _prinstanceid
	 */
	public void refreshWorkFlowPanel(String _prinstanceid) {
		btn_workflow_submit.setVisible(false); //先默认设置为隐藏
		btn_workflow_reject.setVisible(false); //先默认设置为隐藏
		btn_workflow_back.setVisible(false); //先默认设置为隐藏
		btn_workflow_hold.setVisible(false); //先默认设置为隐藏
		btn_workflow_restart.setVisible(false); //先默认设置为隐藏
		btn_workflow_monitor.setVisible(false); //先默认设置为隐藏

		if (_prinstanceid == null || _prinstanceid.equals("")) { //如果没有流程实例
			btn_workflow_submit.setVisible(true); //先默认设置为隐藏
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
				String str_userid = ClientEnvironment.getInstance().getLoginUserID(); //先取得登录人员的主键!!
				String str_sql_2 = "select * from v_pub_wf_dealpool_1 where prinstanceid=" + _prinstanceid + " and participant_user=" + str_userid + " and issubmit='N' and isprocess='N'"; //

				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql_2); //
				if (hvs != null && hvs.length > 0) {
					for (int i = 0; i < hvs.length; i++) {
						if (hvs[i].getIntegerValue("submitcount").intValue() > 0) {
							btn_workflow_submit.setVisible(true); //
						}

						if (hvs[i].getIntegerValue("rejectcount").intValue() > 0) { //如果有拒绝的的线，则显示拒绝的按钮
							btn_workflow_reject.setVisible(true); //
						}

						if (hvs[i].getStringValue("iscanback").equals("Y")) { //如果允许回退，则显示回退按钮
							btn_workflow_back.setVisible(true); //
						}

						if (hvs[i].getStringValue("curractivitytype").equals("END")) {
							btn_workflow_submit.setVisible(true); //
						}
					}
				}

				btn_workflow_hold.setVisible(true); //先默认设置为隐藏
				btn_workflow_monitor.setVisible(true); //监控按钮永远显示!!!
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
	 * 水平布局..
	 * @return
	 */
	public FlowLayout getFlowLayout() {
		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 7, 1);
		return flowLayout; //
	}

	/**
	 * 取得元数据服务!!
	 * @return
	 * @throws Exception
	 */
	protected StyleTempletServiceIfc getMetaService() throws Exception {
		StyleTempletServiceIfc service = (StyleTempletServiceIfc) RemoteServiceFactory.getInstance().lookUpService(StyleTempletServiceIfc.class);
		return service;
	}

	/**
	 * 取得某个系统按钮!
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
