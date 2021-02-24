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
 * 工作流处理框,民生客户提出的需求，想要在一个页面上进行!!!
 * 
 * @author xch      
 * 
 */
public class WorkFlowProcessPanel extends JPanel implements ActionListener, BillListHtmlHrefListener {

	private static final long serialVersionUID = 281604971802320941L;
	private Window parentWindow = null; // 父亲窗口

	private String taskId = null; // 消息任务id,即pub_task_deal.id
	private String prDealPoolId = null; // /流程任务表主键!!
	private String prInstanceId = null; // 流程实例主键!!
	private BillListPanel appBillListPanel = null; // 应用单据的列表!即从哪个业务单据表格上传过来的!!
	// 因在这里还可以需要取得业务单据中的其他字段值什么的!!!

	private boolean isOnlyView = false; // 是否只允许查看,以前是叫"是否是抄送模式",后来因为还有其他情况也是不能处理而只能查看的,所以合并了
	private boolean isYJBD = false;
	private String str_OnlyViewReason = null; // 只能查看的有因!!!
	public JPanel dealMsgPanel = null; // 用于隐藏意见填写面板

	private VFlowLayoutPanel vflowPanel = null; //
	private BillCardPanel billCardPanel; // 卡片
	private BillListPanel billList_hist = null; // 历史意见列表,这两个是最重要的面板

	private JComboBox combobox_prioritylevel; // 紧急程度
	private CardCPanel_Ref refDate_dealtimelimit; // 要求处理期限
	private JTextArea textarea_msg = null; // 输入文本意见的控件.
	private CardCPanel_Ref refFile = null; // 上传附件的控件.
	private CardCPanel_CheckBox ifSendEmail; // 是否发送邮件

	// 以前是从表单上取的,但后来增加子流程后,改成了从任务表中直接取!!!
	public HashVO currDealTaskInfo = null; //

	private WLTButton btn_save, btn_submit, btn_reject, btn_end, btn_monitor, btn_monitor_, btn_ccConfirm, btn_transSend, btn_calcel, btn_Histsave, btn_mind; // 处理按钮
	// 邮储催督办
	// 【杨科/2012-08-15】
	private WLTButton btn_saveMsg, btn_commonmsgs; // 保存意见,常用意见

	private JTabbedPane tabbedPane;
	private WLTButton btn_todeal_1, btn_todeal_2;

	private boolean is_creater = false;
	protected BillVO workflowBillVO = null; // //
	private int closeType = -1;

	private HashVO hvoCurrWFInfo = null; // 当前流程信息!!!即根据单据类型与业务类型,计算出属于什么流程,该流程中定义了什么拦截器??
	private HashVO hvoCurrActivityInfo = null; // 当前环节信息!!!即计算出工作流当前处理什么环节???
	private String unCheckMsgRole = null;

	private WorkFlowYjBdMsgPanel yjbdPanel = null; // 意见补登面板!!!

	private WorkFlowServiceIfc wfService = null; // 工作流远程服务句柄!
	private WorkflowUIUtil wfUIUtil = null; // 工具类!
	private TBUtil tbUtil = null; //

	private Logger logger = WLTLogger.getLogger(WorkFlowProcessPanel.class); //

	private String[] str_wf_halfend_endbtn_setBy_Intercept = null;

	/**
	 * 构造方法..
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
		this.taskId = _taskId; // 消息任务id,即pub_task_deal.id
		this.prDealPoolId = _dealPoolId; //
		this.prInstanceId = _prInstanceId; //
		this.isOnlyView = _isOnlyView; // 是否是抄送!!!
		this.str_OnlyViewReason = _onlyViewReason; // 只能查看的原因!
		this.isYJBD = isyjbd_;

		// 追加工作流处理多页签方式 【杨科/2013-04-08】
		if (getTBUtil().getSysOptionBooleanValue("工作流处理界面是否为多页签", false) && !isYJBD) {
			initialize_tab();
		} else {
			initialize();
		}
	}

	/**
	 * 初始化页面!!
	 */
	private void initialize() {
		try {
			this.setLayout(new BorderLayout(0, 0)); //
			this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
			this.setBackground(LookAndFeel.defaultShadeColor1); //
			this.setUI(new WLTPanelUI(BackGroundDrawingUtil.INCLINE_NW_TO_SE));

			String str_loginuserid = ClientEnvironment.getCurrLoginUserVO().getId(); // 登录人员id
			billCardPanel.setGroupExpandable("其他信息", false); // 这个要去掉!!是民生中的需求而临时搞的!!
			billCardPanel.setGroupExpandable("*法规部填写*", true); //
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); // 工作流服务!!!
			if (isOnlyView) { // 如果是只能 查看,则不可编辑!!
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //
				billCardPanel.setEditable(false); //
			} else {
				boolean isCanEditByinit = false; // 是否可编辑
				if (getTBUtil().getSysOptionBooleanValue("工作流退回发起人是否可以修改", false)) { // 如果有参数择指定,到了创建人那时,创建人可以修改单据状态的话,则处理一下!
					try {
						String str_prInstanceId = getPrinstanceId(); // 本流程实例!!以前有个bug,在兴业项目中发现子流程的创建人也能修改!
						// 所以说如果有子流程则必须还要找到根流程的创建人!!
						String str_wfCreater = wfservice.getRootInstanceCreater(str_prInstanceId); // 取得本流程实例的根流程实例的创建者!
						if (str_wfCreater != null && str_wfCreater.equals(str_loginuserid)) { // 如果流程创建人就是本人,则我是可编辑的!!!
							isCanEditByinit = true; //
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				if (isCanEditByinit) {
					billCardPanel.setEditableByEditInit(); // 冲掉!
				} else {
					billCardPanel.setEditableByWorkFlowInit(); //
				}
			}

			currDealTaskInfo = getCurrTaskInfo(); // 当前流程任务信息,下面计算是否显示半路结束与退回按钮都需要这个信息!

			ArrayList al_compents = new ArrayList(); //
			al_compents.add(billCardPanel); // 业务单据 ★★★★
			al_compents.add(getHistoryBillListPanel()); // 流程提交历史 ★★★★

			// 构造面板,以前是一溜到底,后来遇到有客户说当面板内容太多时,要将处理信息永远浮在外面!!所以加了一个参数!!,但默认还是以前的风格,即拖到最下面才看到处理意见面板!
			if (isOnlyView) { // 如果只是显示,而没有处理,比如查看,抄送的!
				vflowPanel = new VFlowLayoutPanel(al_compents); //
				this.add(vflowPanel, BorderLayout.CENTER); //
			} else {
				if (isWfDealMsgPanelFloat()) { // 如果是浮动的!!
					vflowPanel = new VFlowLayoutPanel(al_compents); //
					JPanel tmpPanel = new JPanel(new BorderLayout()); //
					tmpPanel.setOpaque(false); // 透明!!
					if (isYJBD) {
						yjbdPanel = new WorkFlowYjBdMsgPanel(prDealPoolId, null); // 意见补登面板
						if (getTBUtil().getSysOptionBooleanValue("工作流意见补登是否显示表单信息", false)) {
							tmpPanel.add(vflowPanel, BorderLayout.CENTER);
							tmpPanel.add(yjbdPanel, BorderLayout.SOUTH);
						} else {
							tmpPanel.add(yjbdPanel, BorderLayout.CENTER);
							this.parentWindow.setSize(yjbdPanel.getPreferredSize().width + 100, yjbdPanel.getPreferredSize().height + 100); // 这时窗口要变小!
							if (this.parentWindow != null && this.parentWindow instanceof BillDialog) {
								BillDialog parentDialog = (BillDialog) this.parentWindow; //
								parentDialog.setTitle("意见补登-" + parentDialog.getTitle()); //
								parentDialog.locationToCenterPosition();
							}
						}
					} else {
						tmpPanel.add(vflowPanel, BorderLayout.CENTER); // 加入
						tmpPanel.add(getDealMsgPanel(), BorderLayout.SOUTH); // 在下面加入流程处理意见等内容!!
					}
					this.add(tmpPanel, BorderLayout.CENTER); //
				} else { // 如果不浮动,即以前的布局风格,即必须滚动最下面才看见
					al_compents.add(getDealMsgPanel()); // 处理消息
					vflowPanel = new VFlowLayoutPanel(al_compents); //
					this.add(vflowPanel, BorderLayout.CENTER); //
				}
			}
			// 最下面是提交按钮!!
			this.add(getSouthPanel(), BorderLayout.SOUTH); // 下面的按钮与提示!!

			// 以前总有人反应当卡片内容太多时,不知道下面有个处理意见框,所以有时需要默认将意见处理滚动至可显示!!
			if (!isOnlyView && !isWfDealMsgPanelFloat() && getTBUtil().getSysOptionBooleanValue("工作流处理意见面板是否默认滚动至显示", false)) { //
				int li_hh = (int) vflowPanel.getScollPanel().getViewport().getPreferredSize().getHeight(); // 计算出滚动框中实际内容的高度!!
				makeDealMsgScrollToVisible(li_hh); // 让意见框滚动至显示!!
			}

			if (isOnlyView) { // 如果是只能查看,则不做拦截器,而直接返回!
				return; //
			}

			// 页面初始化后,执行拦截器！！！！
			BillVO billvo = billCardPanel.getBillVO(); // 单据VO
			String str_billtype = billvo.getStringValue("billtype"); // 单据类型
			String str_busitype = billvo.getStringValue("busitype"); // 业务类型!
			String str_prinstanceid = getPrinstanceId(); // 取得流程实例主健
			if (str_billtype != null && !str_billtype.equals("") && str_busitype != null && !str_busitype.equals("")) { // 可能为空么?
				HashVO[] hvs_wf_curract = wfservice.getWFDefineAndCurrActivityInfo(str_billtype, str_busitype, str_prinstanceid, prDealPoolId); // 一次远程调用得到流程定义信息与当前环节信息!!
				hvoCurrWFInfo = hvs_wf_curract[0]; // 流程信息!!
				hvoCurrActivityInfo = hvs_wf_curract[1]; // 当前环节信息!!
			}

			// 处理流程定义的拦截器与环节定义的拦截器!!!
			try {
				if (hvoCurrWFInfo != null) {
					// 流程定义的拦截器！！！有两个,一个是高峰定义的,一个是平台定义的!!!
					// 高峰定义的流程拦截器!!!
					String str_flowintercept = hvoCurrWFInfo.getStringValue("flowintercept"); // 高峰定义的拦截器!!!
					if (str_flowintercept != null) {
						WorkFlowUIIntercept3 intercept = (WorkFlowUIIntercept3) Class.forName(str_flowintercept).newInstance();
						intercept.afterOpenWFProcessPanel(this);
					}

					// 平台标准的流程定义义拦截器,需要将单据类型与业务类型传进去！！！因为在拦截器里面要判断,也要将当前环节信息传进去,在拦截器里面判断!!!
					String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); // 流程后来升级的,统一的的UI端拦截器!!!
					if (str_wfeguiintercept != null) {
						WLTHashMap parMap = new WLTHashMap(); // 之所以最后总是增加一个HashMap参数,是因为根据经验,无论你一开始考虑得多么全面,在实践过程中总遇到需要增加新的参数的情况,但这样一来就会影响所有历史项目,扩展性很差!
						// 所以最好的方法是永远就一个参数,然后这个参数是个VO,然后以后只要在VO中增加新的变量即可!但这样的缺点是会产生大量的类,所以还有最简洁的办法是永远用HashMap,但HashMap的缺点是不能一眼清知道到底有几个参数!!!所以弄了一个WLTHashMap,它提供了一个说明变量,可以在Debug时快速查看共有几个变量!
						// 比如:
						// parMap.put("processpanel", this,
						// "类型:WorkFlowProcessPanel,值就是整个个流程处理面板"); //
						// parMap.put("loginuserid",
						// ClientEnvironment.getCurrLoginUserVO().getId(),
						// "类型:String,值就是登录人员id"); //
						WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance(); //
						uiIntercept.afterOpenWFProcessPanel(this, str_billtype, str_busitype, billvo, hvoCurrActivityInfo, parMap); // 拦截平台定义的流程拦截器!!!
					}

					// 环节定义的拦截器！！！需要考虑到兼容旧的版本!lxf搞的则将原来的类名修改!
					if (hvoCurrActivityInfo != null) { // 必须有当前环节,因为存有启动时第一步是没有当前环节的!!
						String str_intercept1 = hvoCurrActivityInfo.getStringValue("intercept1"); // 环节定义的拦截器!!!
						if (str_intercept1 != null && str_intercept1.trim().indexOf(".") > 0) { //
							Object actIntercept = Class.forName(str_intercept1).newInstance(); //
							if (actIntercept instanceof WorkFlowUIIntercept) { // 必须考虑到兼容旧的版本,以前旧的拦截器类是WorkFlowUIIntercept
								((WorkFlowUIIntercept) actIntercept).afterOpenWFProcessPanel(this); //
							} else if (actIntercept instanceof WorkFlowEngineUIIntercept) { // 新的拦截器统一成WorkFlowEngineUIIntercept
								WLTHashMap parMap = new WLTHashMap(); // 之所以搞这个参数与上面的原因是一样的!!
								((WorkFlowEngineUIIntercept) actIntercept).afterOpenWFProcessPanelByCurrActivity(this, str_billtype, str_busitype, billvo, hvoCurrActivityInfo, parMap); //
							}
						}
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace(); // 先吃掉异常!!
			}
		} catch (Exception ex) {
			WLTLogger.getLogger(this.getClass()).error("工作流在执行客户端拦截器时发生异常!", ex); //
		}
	}

	// 追加工作流处理多页签方式 【杨科/2013-04-08】
	private void initialize_tab() {
		try {
			this.setLayout(new BorderLayout(0, 0)); //
			this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			this.setBackground(LookAndFeel.defaultShadeColor1); //
			this.setUI(new WLTPanelUI(BackGroundDrawingUtil.INCLINE_NW_TO_SE));

			String str_loginuserid = ClientEnvironment.getCurrLoginUserVO().getId(); // 登录人员id
			billCardPanel.setGroupExpandable("其他信息", false); // 这个要去掉!!是民生中的需求而临时搞的!!
			billCardPanel.setGroupExpandable("*法规部填写*", true); //
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); // 工作流服务!!!
			if (isOnlyView) { // 如果是只能 查看,则不可编辑!!
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //
				billCardPanel.setEditable(false); //
			} else {
				boolean isCanEditByinit = false; // 是否可编辑
				if (getTBUtil().getSysOptionBooleanValue("工作流退回发起人是否可以修改", false)) { // 如果有参数择指定,到了创建人那时,创建人可以修改单据状态的话,则处理一下!
					try {
						String str_prInstanceId = getPrinstanceId(); // 本流程实例!!以前有个bug,在兴业项目中发现子流程的创建人也能修改!
						// 所以说如果有子流程则必须还要找到根流程的创建人!!
						String str_wfCreater = wfservice.getRootInstanceCreater(str_prInstanceId); // 取得本流程实例的根流程实例的创建者!
						if (str_wfCreater != null && str_wfCreater.equals(str_loginuserid)) { // 如果流程创建人就是本人,则我是可编辑的!!!
							isCanEditByinit = true; //
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				billCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				if (isCanEditByinit) {
					billCardPanel.setEditableByEditInit(); // 冲掉!
				} else {
					billCardPanel.setEditableByWorkFlowInit(); //
				}
			}

			currDealTaskInfo = getCurrTaskInfo(); // 当前流程任务信息,下面计算是否显示半路结束与退回按钮都需要这个信息!

			getHistoryBillListPanel();

			tabbedPane = new JTabbedPane();
			tabbedPane.addTab("流程单据", getCardPanel());
			tabbedPane.addTab("流程处理", getDealPanel());

			this.add(tabbedPane);

			/*			ArrayList al_compents = new ArrayList(); //
						al_compents.add(panel_content); //业务单据 ★★★★
						al_compents.add(getHistoryBillListPanel()); //流程提交历史 ★★★★

						//构造面板,以前是一溜到底,后来遇到有客户说当面板内容太多时,要将处理信息永远浮在外面!!所以加了一个参数!!,但默认还是以前的风格,即拖到最下面才看到处理意见面板!
						if (isOnlyView) { //如果只是显示,而没有处理,比如查看,抄送的!
							vflowPanel = new VFlowLayoutPanel(al_compents); //
							this.add(vflowPanel, BorderLayout.CENTER); //
						} else {
							if (isWfDealMsgPanelFloat()) { //如果是浮动的!!
								vflowPanel = new VFlowLayoutPanel(al_compents); //
								JPanel tmpPanel = new JPanel(new BorderLayout()); //
								tmpPanel.setOpaque(false); //透明!!
								if (isYJBD) {
									yjbdPanel = new WorkFlowYjBdMsgPanel(prDealPoolId, null); //意见补登面板
									if (getTBUtil().getSysOptionBooleanValue("工作流意见补登是否显示表单信息", false)) {
										tmpPanel.add(vflowPanel, BorderLayout.CENTER);
										tmpPanel.add(yjbdPanel, BorderLayout.SOUTH);
									} else {
										tmpPanel.add(yjbdPanel, BorderLayout.CENTER);
										this.parentWindow.setSize(yjbdPanel.getPreferredSize().width + 100, yjbdPanel.getPreferredSize().height + 100); //这时窗口要变小!
										if (this.parentWindow != null && this.parentWindow instanceof BillDialog) {
											BillDialog parentDialog = (BillDialog) this.parentWindow; //
											parentDialog.setTitle("意见补登-" + parentDialog.getTitle()); //
											parentDialog.locationToCenterPosition();
										}
									}
								} else {
									tmpPanel.add(vflowPanel, BorderLayout.CENTER); //加入
									tmpPanel.add(getDealMsgPanel(), BorderLayout.SOUTH); //在下面加入流程处理意见等内容!!
								}
								this.add(tmpPanel, BorderLayout.CENTER); //
							} else { //如果不浮动,即以前的布局风格,即必须滚动最下面才看见
								al_compents.add(getDealMsgPanel()); // 处理消息
								vflowPanel = new VFlowLayoutPanel(al_compents); //
								this.add(vflowPanel, BorderLayout.CENTER); //
							}
						}
						//最下面是提交按钮!!
						this.add(getSouthPanel(), BorderLayout.SOUTH); //下面的按钮与提示!!

						//以前总有人反应当卡片内容太多时,不知道下面有个处理意见框,所以有时需要默认将意见处理滚动至可显示!!
						if (!isOnlyView && !isWfDealMsgPanelFloat() && getTBUtil().getSysOptionBooleanValue("工作流处理意见面板是否默认滚动至显示", false)) { //
							int li_hh = (int) vflowPanel.getScollPanel().getViewport().getPreferredSize().getHeight(); //计算出滚动框中实际内容的高度!!
							makeDealMsgScrollToVisible(li_hh); //让意见框滚动至显示!!
						}*/

			if (isOnlyView) { // 如果是只能查看,则不做拦截器,而直接返回!
				return; //
			}

			// 页面初始化后,执行拦截器！！！！
			BillVO billvo = billCardPanel.getBillVO(); // 单据VO
			String str_billtype = billvo.getStringValue("billtype"); // 单据类型
			String str_busitype = billvo.getStringValue("busitype"); // 业务类型!
			String str_prinstanceid = getPrinstanceId(); // 取得流程实例主健
			if (str_billtype != null && !str_billtype.equals("") && str_busitype != null && !str_busitype.equals("")) { // 可能为空么?
				HashVO[] hvs_wf_curract = wfservice.getWFDefineAndCurrActivityInfo(str_billtype, str_busitype, str_prinstanceid, prDealPoolId); // 一次远程调用得到流程定义信息与当前环节信息!!
				hvoCurrWFInfo = hvs_wf_curract[0]; // 流程信息!!
				hvoCurrActivityInfo = hvs_wf_curract[1]; // 当前环节信息!!
			}

			// 处理流程定义的拦截器与环节定义的拦截器!!!
			try {
				if (hvoCurrWFInfo != null) {
					// 流程定义的拦截器！！！有两个,一个是高峰定义的,一个是平台定义的!!!
					// 高峰定义的流程拦截器!!!
					String str_flowintercept = hvoCurrWFInfo.getStringValue("flowintercept"); // 高峰定义的拦截器!!!
					if (str_flowintercept != null) {
						WorkFlowUIIntercept3 intercept = (WorkFlowUIIntercept3) Class.forName(str_flowintercept).newInstance();
						intercept.afterOpenWFProcessPanel(this);
					}

					// 平台标准的流程定义义拦截器,需要将单据类型与业务类型传进去！！！因为在拦截器里面要判断,也要将当前环节信息传进去,在拦截器里面判断!!!
					String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); // 流程后来升级的,统一的的UI端拦截器!!!
					if (str_wfeguiintercept != null) {
						WLTHashMap parMap = new WLTHashMap(); // 之所以最后总是增加一个HashMap参数,是因为根据经验,无论你一开始考虑得多么全面,在实践过程中总遇到需要增加新的参数的情况,但这样一来就会影响所有历史项目,扩展性很差!
						// 所以最好的方法是永远就一个参数,然后这个参数是个VO,然后以后只要在VO中增加新的变量即可!但这样的缺点是会产生大量的类,所以还有最简洁的办法是永远用HashMap,但HashMap的缺点是不能一眼清知道到底有几个参数!!!所以弄了一个WLTHashMap,它提供了一个说明变量,可以在Debug时快速查看共有几个变量!
						// 比如:
						// parMap.put("processpanel", this,
						// "类型:WorkFlowProcessPanel,值就是整个个流程处理面板"); //
						// parMap.put("loginuserid",
						// ClientEnvironment.getCurrLoginUserVO().getId(),
						// "类型:String,值就是登录人员id"); //
						WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance(); //
						uiIntercept.afterOpenWFProcessPanel(this, str_billtype, str_busitype, billvo, hvoCurrActivityInfo, parMap); // 拦截平台定义的流程拦截器!!!
					}

					// 环节定义的拦截器！！！需要考虑到兼容旧的版本!lxf搞的则将原来的类名修改!
					if (hvoCurrActivityInfo != null) { // 必须有当前环节,因为存有启动时第一步是没有当前环节的!!
						String str_intercept1 = hvoCurrActivityInfo.getStringValue("intercept1"); // 环节定义的拦截器!!!
						if (str_intercept1 != null && str_intercept1.trim().indexOf(".") > 0) { //
							Object actIntercept = Class.forName(str_intercept1).newInstance(); //
							if (actIntercept instanceof WorkFlowUIIntercept) { // 必须考虑到兼容旧的版本,以前旧的拦截器类是WorkFlowUIIntercept
								((WorkFlowUIIntercept) actIntercept).afterOpenWFProcessPanel(this); //
							} else if (actIntercept instanceof WorkFlowEngineUIIntercept) { // 新的拦截器统一成WorkFlowEngineUIIntercept
								WLTHashMap parMap = new WLTHashMap(); // 之所以搞这个参数与上面的原因是一样的!!
								((WorkFlowEngineUIIntercept) actIntercept).afterOpenWFProcessPanelByCurrActivity(this, str_billtype, str_busitype, billvo, hvoCurrActivityInfo, parMap); //
							}
						}
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace(); // 先吃掉异常!!
			}
		} catch (Exception ex) {
			WLTLogger.getLogger(this.getClass()).error("工作流在执行客户端拦截器时发生异常!", ex); //
		}
	}

	// 追加工作流处理多页签方式相关 【杨科/2013-04-08】
	private JPanel getCardPanel() {
		BillCardPanel _billCardPanel = billCardPanel;
		String str_wfprinstanceid = _billCardPanel.getBillVO().getStringValue("wfprinstanceid");
		if (str_wfprinstanceid != null && !str_wfprinstanceid.trim().equals("")) {
			_billCardPanel.setGroupVisiable("工作流处理信息", false);
		}

		// btn_todeal_1 = new WLTButton(" >> ");
		btn_todeal_1 = new WLTButton("流程处理", UIUtil.getImage("zt_073.gif"));
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
		btnPanel.setOpaque(false); // 透明!
		btnPanel.add(empty, BorderLayout.NORTH);
		btnPanel.add(btn_todeal_1, BorderLayout.EAST);

		JPanel cardPanel = new JPanel(new BorderLayout());
		cardPanel.setOpaque(false); // 透明!
		cardPanel.setBackground(LookAndFeel.defaultShadeColor1);
		cardPanel.setUI(new WLTPanelUI(BackGroundDrawingUtil.INCLINE_NW_TO_SE));
		cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		cardPanel.add(_billCardPanel, BorderLayout.CENTER);
		cardPanel.add(btnPanel, BorderLayout.SOUTH);

		return cardPanel;
	}

	// 追加工作流处理多页签方式相关 【杨科/2013-04-08】
	private JPanel getDealPanel() {
		JPanel workflowPanel = new JPanel(new BorderLayout());
		workflowPanel.setOpaque(false); // 透明!
		workflowPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "工作流信息", TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font)); // 设置边框!
		workflowPanel.add(getWorkFlowPanel(), BorderLayout.CENTER);

		JPanel dealMsgPanel = new JPanel(new BorderLayout());
		dealMsgPanel.setOpaque(false); // 透明!
		dealMsgPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "处理信息", TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font)); // 设置边框!
		dealMsgPanel.add(getDealMsgPanel(), BorderLayout.CENTER);

		JPanel btnPanel = getSouthPanel();
		btnPanel.setUI(null);

		// btn_todeal_2 = new WLTButton(" << ");
		btn_todeal_2 = new WLTButton("流程单据", UIUtil.getImage("zt_072.gif"));
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
		btnPanel_.setOpaque(false); // 透明!
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

	// 追加工作流处理多页签方式相关 【杨科/2013-04-08】
	private VFlowLayoutPanel getWorkFlowPanel() {
		BillVO billvo = billCardPanel.getBillVO(); // 单据VO
		String str_wfprinstanceid = billvo.getStringValue("wfprinstanceid");

		JLabel jl_task_curractivityname = new JLabel();
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) { // 流程未发起
			jl_task_curractivityname.setText(" 当前步骤：流程发起               ");
		} else {
			jl_task_curractivityname.setText(" 当前步骤：" + billvo.getStringValue("task_curractivityname") + "               ");
		}

		JLabel jl_task = new JLabel();
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) { // 流程未发起
			jl_task.setText("  提交人：" + ClientEnvironment.getCurrLoginUserVO().getCode() + "/" + ClientEnvironment.getCurrLoginUserVO().getName() + "            " + "提交时间：" + getTBUtil().getCurrTime() + "            ");
		} else {
			if (billvo.getStringValue("task_creatername") != null && billvo.getStringValue("task_createtime") != null) {
				jl_task.setText("  提交人：" + billvo.getStringValue("task_creatername") + "            " + "提交时间：" + billvo.getStringValue("task_createtime") + "            ");
			} else if (billvo.getStringValue("task_realdealusername") != null && billvo.getStringValue("task_dealtime") != null) {
				jl_task.setText("  提交人：" + billvo.getStringValue("task_realdealusername") + "            " + "提交时间：" + billvo.getStringValue("task_dealtime") + "            ");
			} else {
				jl_task.setText("  提交人：                         提交时间：");
			}
		}

		// btn_monitor_ = new WLTButton("流程监控");
		btn_monitor_ = new WLTButton("流程监控", UIUtil.getImage("office_046.gif"));
		btn_monitor_.addActionListener(this);

		JPanel jpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jpanel.setOpaque(false); // 透明!
		jpanel.add(jl_task_curractivityname);
		jpanel.add(btn_monitor_);

		ArrayList al_compents = new ArrayList();
		al_compents.add(jpanel);
		al_compents.add(jl_task);

		return new VFlowLayoutPanel(al_compents);
	}

	/**
	 * 取得当前任务,这个方法其实重复了!!!
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
				sb_sql.append("pub_wf_activity.wfname,code,"); // 环节名称
				sb_sql.append("pub_wf_activity.belongdeptgroup,"); // 环节所属机构组
				sb_sql.append("pub_wf_activity.iscanback,"); // 是否有回退!
				sb_sql.append("pub_wf_activity.canhalfend,"); // 是否有结束!
				sb_sql.append("pub_wf_activity.isHideTransSend, "); // 是否隐藏转办,默认显示!
				sb_sql.append("pub_wf_activity.approvemodel ");// 审核模式
				sb_sql.append("from pub_wf_dealpool,pub_wf_activity "); //
				sb_sql.append("where pub_wf_dealpool.curractivity=pub_wf_activity.id ");
				sb_sql.append("and pub_wf_dealpool.id='" + this.prDealPoolId + "' "); // 流程任务id

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
	 * 历史处理记录列表!
	 * @return
	 */
	public BillListPanel getHistoryBillListPanel() {
		if (billList_hist != null) {
			return billList_hist;
		}
		try {
			BillVO billVO = getBillCardPanel().getBillVO(); //
			String str_prinstanceid = getPrinstanceId(); // 这个地方要处理一下,即可能需要显示整个root流程相同的记录
			WFHistListPanelBuilder histListBuilder = new WFHistListPanelBuilder(str_prinstanceid, billVO, true); // ★★★★
			billList_hist = histListBuilder.getBillListPanel(); // 创建面板!!

			boolean isCanEditableMsgFile = getTBUtil().getSysOptionBooleanValue("工作流是否可以修改历史附件", false); //
			billList_hist.setItemEditable("submitmessagefile", isCanEditableMsgFile); //
			billList_hist.setBillListOpaque(false); // 透明的
			billList_hist.getMainScrollPane().setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0)); //
			billList_hist.setToolBarPanelBackground(LookAndFeel.cardbgcolor); //
			billList_hist.getTempletVO().setTablename(this.billCardPanel.getTempletVO().getTablename()); //
			billList_hist.getTempletVO().setSavedtablename(this.billCardPanel.getTempletVO().getSavedtablename()); //

			// 以前是动态计算的!但后来在邮储中遇到上面有一排勾选框可以选择指定涌道中的记录,即只显示分行的数据!然后这样一排高度就会动态变化
			// 所以新的逻辑是计算出最大的情况,即全部勾选上总共有多高,然后超过上限就定为350
			// 这样的好处是,一开始只有几条记录时,不会有大量空白!!!而是很恰当,而当真的非常多时也不会太难看!
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
	 * 工作流处理意见面板!! 以前是放在最后,后来遇到有的客户觉得卡片面板太大时,要滚动最下面才知道!不方便!
	 * 所以改成总是浮在最外面!!
	 * @return
	 */
	private JPanel getDealMsgPanel() {
		JPanel panel = new WLTPanel(new BorderLayout(0, 5)); //
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); //
		panel.setBackground(LookAndFeel.cardbgcolor); //

		JPanel panel_btn_info = new JPanel(new BorderLayout(0, 2)); //
		panel_btn_info.setOpaque(false); //
		String str_helpinfo = "提示:" + getCurrActivityName(); //
		// 追加工作流处理多页签方式相关 【杨科/2013-04-08】
		if (billList_hist.getRowCount() > 0 && !getTBUtil().getSysOptionBooleanValue("工作流处理界面是否为多页签", false)) { // 如果有历史意见了,则提示!!
			// 即第一次是不提示的!
			str_helpinfo = str_helpinfo + "(点击上面各个人的意见可以将其拷贝过来!)";
		}
		JLabel label_info = new JLabel(str_helpinfo); //
		label_info.setToolTipText(str_helpinfo); //
		label_info.setPreferredSize(new Dimension(1000, 20)); //
		label_info.setForeground(Color.RED); //
		panel_btn_info.add(label_info, BorderLayout.NORTH); //

		// 上面的一排紧急程度什么的..
		JPanel panel_north = new WLTPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // 上面的按钮
		panel_north.setBackground(LookAndFeel.cardbgcolor); //

		JLabel label_prioritylevel = new JLabel("紧急程度", SwingConstants.RIGHT);
		label_prioritylevel.setForeground(Color.BLACK); //
		combobox_prioritylevel = new JComboBox(); //
		combobox_prioritylevel.setPreferredSize(new Dimension(85, 20)); //
		combobox_prioritylevel.addItem("一般"); //
		combobox_prioritylevel.addItem("紧急"); //
		combobox_prioritylevel.addItem("非常紧急"); //
		combobox_prioritylevel.setSelectedIndex(0); //

		refDate_dealtimelimit = new CardCPanel_Ref("dealtimelimit", "要求最后处理期限", null, WLTConstants.COMP_DATETIME, null, null); // //
		refDate_dealtimelimit.getLabel().setForeground(Color.BLACK); //
		refDate_dealtimelimit.getLabel().setHorizontalAlignment(SwingConstants.RIGHT);
		// 是否显示 紧急程度 和 最后处理期限 项，为true则显示，默认为true.
		boolean isAddedBtn = false; //
		if ((getTBUtil()).getSysOptionBooleanValue("工作流处理页面中是否显示紧急程度", true)) {
			panel_north.add(label_prioritylevel); //
			panel_north.add(combobox_prioritylevel); //
			isAddedBtn = true;
		}
		if ((getTBUtil()).getSysOptionBooleanValue("工作流处理页面中是否显示最后处理期限", true)) {
			panel_north.add(refDate_dealtimelimit); //
			isAddedBtn = true;
		}
		if (isAddedBtn) {
			panel_btn_info.add(panel_north, BorderLayout.SOUTH); //
		}
		panel.add(panel_btn_info, BorderLayout.NORTH); //

		// 审批意见
		JLabel label = new JLabel("<html><font color='#FF0000'>&nbsp;*</font>处理&nbsp;&nbsp;<br>&nbsp;&nbsp;意见&nbsp;&nbsp;</html>");// 袁江晓20121108更改，张越霞提出的
		// 处理意见整好看点
		label.setForeground(Color.BLUE); //
		JPanel panel_west = new WLTPanel(new BorderLayout()); //
		panel_west.setBackground(LookAndFeel.cardbgcolor); //
		panel_west.add(label, BorderLayout.NORTH); //
		panel.add(panel_west, BorderLayout.WEST); //
		textarea_msg = new WLTTextArea(15, 50); //
		unCheckMsgRole = getTBUtil().getSysOptionStringValue("工作流处理页面中处理意见不做验证角色", "");
		if ((getTBUtil()).getSysOptionBooleanValue("工作流处理页面中处理意见是否显示为最后提交者意见", false)) {// 兴业提出
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
		textarea_msg.setForeground(LookAndFeel.inputforecolor_enable); // 有效时的前景色
		textarea_msg.setBackground(LookAndFeel.inputbgcolor_enable); // 有效时的背景色
		panel.add(new JScrollPane(textarea_msg), BorderLayout.CENTER); //

		// 上传附件
		refFile = new CardCPanel_Ref("messagefile", "附件 ", null, WLTConstants.COMP_FILECHOOSE, null, billCardPanel); // //
		refFile.getBtn_ref().setToolTipText("上传附件"); //
		refFile.getBtn_ref().setBackground(LookAndFeel.systembgcolor); //
		refFile.getBtn_ref().setForeground(Color.RED); //
		refFile.getBtn_ref().setPreferredSize(new Dimension(110, 23)); //
		refFile.getBtn_ref().setText("上传附件"); //
		refFile.getLabel().setForeground(Color.BLACK); //
		refFile.getLabel().setPreferredSize(new Dimension(30, 23)); //
		refFile.getTextField().setPreferredSize(new Dimension(350, 23)); //
		refFile.setPreferredSize(new Dimension(50 + 350 + 100, 23)); //
		refFile.setItemEditable(true); //

		// 【保存意见】【常用意见】
		btn_saveMsg = new WLTButton("保存意见", UIUtil.getImage("save.gif")); //
		btn_commonmsgs = new WLTButton("常用参考意见", UIUtil.getImage("office_043.gif")); //
		btn_saveMsg.addActionListener(this); //
		btn_commonmsgs.addActionListener(this); //

		JPanel panel_msg = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5)); //
		panel_msg.setOpaque(false); //
		if (TBUtil.getTBUtil().getSysOptionBooleanValue("工作处理是否支持上传附件", true)) { // 邮储项目中提出不要上传附件,说是逼着人们在意见中填!!!
			// 因为附件内容导不出来!)
			// {
			// //前面有参数决定可能不创建该控件,邮储项目中竟然遇到工作流不要附件!
			panel_msg.add(refFile); //
		}
		panel_msg.add(btn_saveMsg); //
		panel_msg.add(btn_commonmsgs); //

		// 发邮件的选项!!
		ifSendEmail = new CardCPanel_CheckBox("ifSendEmail", "是否发送邮件");
		boolean sendEmailFlag = getTBUtil().getSysOptionBooleanValue("工作流默认发送邮件", false);
		ifSendEmail.getCheckBox().setSelected(sendEmailFlag);
		if (getTBUtil().getSysOptionBooleanValue("工作流是否显示发送邮件选项", false)) { // 如果是发邮件选项,
			JPanel jp_1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			jp_1.setOpaque(false); // 透明!!!
			jp_1.setBackground(LookAndFeel.cardbgcolor); //
			JLabel label_1 = new JLabel("是否发送邮件");
			label_1.setOpaque(false); // 透明!!
			label_1.setPreferredSize(new Dimension(72, 20)); //
			// jp_1.add(label_1);
			jp_1.add(ifSendEmail);
			JPanel jp = new JPanel(new BorderLayout());
			jp.setOpaque(false); // 透明!!
			jp.setBackground(LookAndFeel.cardbgcolor); //
			jp.add(panel_msg, BorderLayout.NORTH); // 上面是【保存意见/上传附件】
			jp.add(jp_1, BorderLayout.CENTER); // 下面是发送邮件
			panel.add(jp, BorderLayout.SOUTH); // 将这个面板加入整个面板中!
		} else {
			panel.add(panel_msg, BorderLayout.SOUTH); //
		}

		int li_width = 750; //
		int li_initheight = 158; //
		String str_wh_option = getTBUtil().getSysOptionStringValue("工作流处理意见框大小", null); // 邮储的项目极其纠结,认为一定要小!民生等其他客户认为一定要大!
		if (str_wh_option != null && !str_wh_option.trim().equals("")) {
			str_wh_option = str_wh_option.trim(); //
			String str_width = str_wh_option.substring(0, str_wh_option.indexOf("*")); //
			String str_height = str_wh_option.substring(str_wh_option.indexOf("*") + 1, str_wh_option.length()); //
			li_width = Integer.parseInt(str_width); //
			li_initheight = Integer.parseInt(str_height); //
		}

		int li_height = li_initheight + 23 + (isAddedBtn ? 22 : 0) + 30; // 高度!!
		panel.setPreferredSize(new Dimension(li_width, li_height)); // 处理意见框太小了,客户提议搞大点!!但问题是,太大了不好看,就定在700,
		HFlowLayoutPanel hflow = new HFlowLayoutPanel(new JComponent[] { panel }); // 水平布局

		// 加载信息
		String str_prinstanceid = getPrinstanceId(); //
		if (str_prinstanceid != null && !str_prinstanceid.trim().equals("")) { // 如果流程实例id不为空!!!
			try {
				// String str_sql = "select submitmessage,submitmessagefile from
				// pub_wf_dealpool where prinstanceid='" +
				// getTBUtil().getNullCondition(str_prinstanceid) + "' and
				// participant_user='" + str_loginuserid + "' and issubmit='N'
				// and isprocess='N'";
				String str_sql = "select submitmessage,submitmessagefile from pub_wf_dealpool where id='" + prDealPoolId + "'";
				HashVO[] hvs_msg = UIUtil.getHashVoArrayByDS(null, str_sql); //
				if (hvs_msg.length > 0) {
					textarea_msg.setText(hvs_msg[0].getStringValue("submitmessage", "")); // //处理意见!!
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
				logger.error("加载工作流曾经保存的意见失败", _ex);
			}
		}
		dealMsgPanel = hflow;//
		return hflow;
	}

	/**
	 * 下面的按钮栏!!!
	 * 按钮上面还有一个提示!!
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 放按钮的面板!!
		btnPanel.setOpaque(false); // 透明
		String str_btn_reject_name = getTBUtil().getSysOptionStringValue("工作流退回按钮名称", " 退 回 "); //
		String str_btn_submit_name = getTBUtil().getSysOptionStringValue("工作流提交按钮名称", " 提 交 "); // 徽商客户说叫【下一步】更好
		btn_reject = new WLTButton(str_btn_reject_name, UIUtil.getImage("office_161.gif")); // 工作流退回,即可以退回到前面任意一步!
		btn_submit = new WLTButton(str_btn_submit_name, UIUtil.getImage("office_008.gif")); // 工作流提交,也叫下一步!office_190.gif
		if (ClientEnvironment.isAdmin()) { // 如果带管理密码登录的,则在按钮提示是直接说明可由哪些参数配置!
			btn_reject.setToolTipText("可由参数【工作流退回按钮名称】重命名"); //
			btn_submit.setToolTipText("可由参数【工作流提交按钮名称】重命名"); //
		}
		btn_save = new WLTButton(" 保 存 "); // 保存
		btn_end = new WLTButton(" 结 束 "); //
		btn_monitor = new WLTButton("流程监控"); // 流程监控
		btn_ccConfirm = new WLTButton(" 确 认 "); // 抄送的确认键!
		btn_calcel = new WLTButton(" 取 消 "); // 工作流取消
		btn_transSend = new WLTButton(" 转 办 ");
		btn_mind = new WLTButton(" 催督办 ");
		btn_save.addActionListener(this); //
		btn_submit.addActionListener(this); //
		btn_reject.addActionListener(this); //
		btn_end.addActionListener(this); //
		btn_monitor.addActionListener(this); //
		btn_ccConfirm.addActionListener(this); // 增加监听!!!
		btn_calcel.addActionListener(this); //
		btn_transSend.addActionListener(this);
		btn_mind.addActionListener(this);
		btn_end.setVisible(false); //
		if (isOnlyView) { // 如果是抄送模式,则只显示两个按钮,即【流程监控】与【取消】!!!
			if (!getTBUtil().getSysOptionBooleanValue("工作流处理界面是否为多页签", false)) {// 追加工作流处理多页签方式相关
				// 【杨科/2013-04-08】
				btnPanel.add(btn_monitor); // 流程监控
			}
			btnPanel.add(btn_ccConfirm); // 确认!
			btnPanel.add(btn_calcel); // 取消
			if (getTBUtil().getSysOptionBooleanValue("工作流是否有催督办按钮", false)) {
				btnPanel.add(btn_mind); // 催督办 暂时加在这，理论不应该有 为测试
			}
		} else {
			if (isCanBack()) { // 是否有退回?
				btnPanel.add(btn_reject); // 拒绝
			}
			btnPanel.add(btn_submit); // 提交,也叫下一步!!
			if (isCanSend()) {
				if (getTBUtil().getSysOptionBooleanValue("工作流是否显示转办按钮", true)) {// sunfujun/20120820/shaochunyun非要这样
					btnPanel.add(btn_transSend);
				}
			}
			if (getTBUtil().getSysOptionBooleanValue("工作流处理页面中是否显示保存按钮", false)) { // 是否显示保存按钮，系统参数配置里取。默认为不显示保存按钮
				btnPanel.add(btn_save); // 保存,招行的人提出不要"保存按钮"与逻辑,直接提交,而提交前直接默认做保存操作!!(最直接与省事)
			}
			if (isCanHalfEnd()) { // 是否可以半路结束?
				btnPanel.add(btn_end); // 拒绝 是谁搞掉的可以看看记录 |
				// 项目需要先添加上，但是不显示。可以在拦截器中去现实 by
				// haoming20160422
				// btn_end.setVisible(true); //
			}
			if (!getTBUtil().getSysOptionBooleanValue("工作流处理界面是否为多页签", false)) {// 追加工作流处理多页签方式相关
				// 【杨科/2013-04-08】
				btnPanel.add(btn_monitor); // 流程监控
			}
			btnPanel.add(btn_calcel); // 取消
		}

		JLabel infoLabel = new JLabel(); //
		infoLabel.setForeground(Color.RED); // 为了醒目,总是红色!!!
		infoLabel.setFont(LookAndFeel.font_big); //
		if (str_OnlyViewReason != null) {
			infoLabel.setText("提示：" + str_OnlyViewReason); //
		}

		JPanel southPanel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new BorderLayout(), LookAndFeel.defaultShadeColor1, false); // 返回的面板!!!VERTICAL_BOTTOM_TO_TOP
		southPanel.add(infoLabel, BorderLayout.NORTH); //
		southPanel.add(btnPanel, BorderLayout.SOUTH); //
		return southPanel; //
	}

	/**
	 * 【保存】按钮执行的动作!! 即同时对表单与流程意见保存!!!
	 */
	private void onSave() {
		try {
			if (!this.billCardPanel.newCheckValidate("save", this)) { // 先校验数据,如果没过则不继续
				return;
			}
			this.billCardPanel.updateData(); // 只保存了卡片的数据
			saveDealPoolMsg(); // 保存流程意见,还需保存流程引擎中的我的处理意见!!!!!
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 只保存工作处理意见!!!
	 * @throws Exception
	 */
	private void saveDealPoolMsg() throws Exception {
		if (!validateMsg()) {
			return;
		}
		BillVO billVO = this.billCardPanel.getBillVO(); // 取得页面上的数据!!!
		if (!billVO.isHaveKey("WFPRINSTANCEID")) { // 如果页面上包括流程实例主键!!
			logger.error("保存流程处理意见时,发现没有定义工作流需要的列[wfprinstanceid],故直接返回!"); //
			return; //
		}

		String str_wfinstance = getPrinstanceId(); // 创建流程实例,并返回流程实例ID!!!
		if (str_wfinstance == null || str_wfinstance.equals("")) { // //如果流程实例字段的值为空,则说明是要启动流程!!
			MessageBox.show(this, "保存表单数据成功!\n但因为还没启动流程,所以处理意见不能保存,请知悉!"); //
			return;
		}

		UpdateSQLBuilder isql = new UpdateSQLBuilder("pub_wf_dealpool"); //
		isql.setWhereCondition("id='" + prDealPoolId + "'"); // 如果直接指定了流程任务id,即新的机制!!
		String str_message = null;
		String str_msgfile = null;
		if (isYJBD) {
			str_message = yjbdPanel.getMsgText(); //
			str_msgfile = yjbdPanel.getMsgFile(); //
		} else {
			str_message = textarea_msg.getText(); //
			str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
		}
		isql.putFieldValue("submitmessage", str_message); // 意见!!大文本框
		isql.putFieldValue("submitmessagefile", str_msgfile); // 意见附件
		int li_count = UIUtil.executeUpdateByDS(null, isql); //
		logger.debug("保存处理流程意见时共处理到[" + li_count + "]条记录."); //
		MessageBox.show(this, "保存流程意见成功!"); //
	}

	/**
	 * @param _billType
	 * @param _busitype
	 * @param _billVO
	 * @return
	 */
	private int checkBeforeSubmit(String _billType, String _busitype, BillVO _billVO) {
		try {
			// 先处理流程中注册的拦截器!!
			int li_result = 0; //
			if (hvoCurrWFInfo != null) {
				String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); // 流程后来升级的,统一的的UI端拦截器!!!
				if (str_wfeguiintercept != null && !str_wfeguiintercept.trim().equals("")) {
					WLTHashMap parMap = new WLTHashMap(); // 之所以最后总是增加一个HashMap参数,是因为根据经验,无论你一开始考虑得多么全面,在实践过程中总遇到需要增加新的参数的情况,但这样一来就会影响所有历史项目,扩展性很差!
					// 所以最好的方法是永远就一个参数,然后这个参数是个VO,然后以后只要在VO中增加新的变量即可!但这样的缺点是会产生大量的类,所以还有最简洁的办法是永远用HashMap,但HashMap的缺点是不能一眼清知道到底有几个参数!!!所以弄了一个WLTHashMap,它提供了一个说明变量,可以在Debug时快速查看共有几个变量!
					// 比如:
					// parMap.put("processpanel", this,
					// "类型:WorkFlowProcessPanel,值就是整个个流程处理面板"); //
					// parMap.put("loginuserid",
					// ClientEnvironment.getCurrLoginUserVO().getId(),
					// "类型:String,值就是登录人员id"); //
					WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance(); //
					li_result = uiIntercept.beforeSubmitWFProcessPanel(this, _billType, _busitype, _billVO, hvoCurrActivityInfo, parMap); // 拦截平台定义的流程拦截器!!!
				}
			}

			if (li_result < 0) { // 如果流程就校验失败,则直接返回,否则继续!
				return li_result; //
			}

			// 再处理环节中注册的拦截器!!
			if (hvoCurrActivityInfo != null) { // 必须有当前环节,因为存有启动时第一步是没有当前环节的!!
				String str_intercept1 = hvoCurrActivityInfo.getStringValue("intercept1"); // 环节定义的拦截器!!!
				if (str_intercept1 != null && str_intercept1.trim().indexOf(".") > 0) { //
					Object actIntercept = Class.forName(str_intercept1).newInstance(); //
					if (actIntercept instanceof WorkFlowEngineUIIntercept) { // 新的拦截器统一成WorkFlowEngineUIIntercept
						WLTHashMap parMap = new WLTHashMap(); // 之所以搞这个参数与上面的原因是一样的!!
						li_result = ((WorkFlowEngineUIIntercept) actIntercept).beforeSubmitWFProcessPanelByCurrActivity(this, _billType, _busitype, _billVO, hvoCurrActivityInfo, parMap); //
					}
				}
			}
			return li_result;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.show(this, "提交前拦截器校验失败,请至控制台查看详细信息!"); //
			return -1; //
		}
	}

	/**
	 * 工作流【提交】按钮的动作..
	 */
	private void onSubmit() {
		if (isCanHalfEnd()) { // 如果可以半路提交!! btn_end != null &&
			// btn_end.isVisible()
			String str_wf_halfend_msg = null; //
			String str_wf_halfend_endbtn1_name = null; // 也可能叫【审批通过】
			String str_wf_halfend_endbtn2_name = null; //
			String str_wf_halfend_keepupbtn_name = null; //
			// sunfujun/20121130/区分一下子流程与主流程
			if (getPrinstanceId() == null || getPrinstanceId().equals(billCardPanel.getBillVO().getStringValue("wfprinstanceid"))) {// 相同证明是主流程
				str_wf_halfend_msg = getTBUtil().getSysOptionStringValue("工作流半路结束时的提醒说明", "当前步骤既可以选择下一步接受者提交,也可以直接审批通过并结束流程!\r\n请选择实际操作!"); //
				str_wf_halfend_endbtn1_name = getTBUtil().getSysOptionStringValue("工作流半路结束时的正常结束按钮名称", "正常结束流程"); // 也可能叫【审批通过】
				str_wf_halfend_endbtn2_name = getTBUtil().getSysOptionStringValue("工作流半路结束时的非正常结束按钮名称", "非正常结束流程"); //
				str_wf_halfend_keepupbtn_name = getTBUtil().getSysOptionStringValue("工作流半路结束时的继续按钮名称", "继续提交"); //
			} else {// 否则是子流程
				str_wf_halfend_msg = getTBUtil().getSysOptionStringValue("子流程工作流半路结束时的提醒说明", "当前步骤既可以选择下一步接受者提交,也可以直接审批通过并结束流程!\r\n请选择实际操作!"); //
				str_wf_halfend_endbtn1_name = getTBUtil().getSysOptionStringValue("子流程工作流半路结束时的正常结束按钮名称", "正常结束流程"); // 也可能叫【审批通过】
				str_wf_halfend_endbtn2_name = getTBUtil().getSysOptionStringValue("子流程工作流半路结束时的非正常结束按钮名称", "非正常结束流程"); //
				str_wf_halfend_keepupbtn_name = getTBUtil().getSysOptionStringValue("子流程工作流半路结束时的继续按钮名称", "继续提交"); //
			}
			str_wf_halfend_endbtn1_name = "[office_175.gif]" + str_wf_halfend_endbtn1_name; // 正常结束/审批通过的图片
			str_wf_halfend_endbtn2_name = "[office_061.gif]" + str_wf_halfend_endbtn2_name; // 非正常结束/审批不通过/带有某种意见结束的!
			str_wf_halfend_keepupbtn_name = "[office_008.gif]" + str_wf_halfend_keepupbtn_name; // 加个图片!
			if (str_wf_halfend_endbtn_setBy_Intercept != null) {
				if (str_wf_halfend_endbtn_setBy_Intercept.length > 0 && !getTBUtil().isEmpty(str_wf_halfend_endbtn_setBy_Intercept[0])) {
					str_wf_halfend_endbtn1_name = str_wf_halfend_endbtn_setBy_Intercept[0]; // 正常结束/审批通过的图片
				}
				if (str_wf_halfend_endbtn_setBy_Intercept.length > 1 && !getTBUtil().isEmpty(str_wf_halfend_endbtn_setBy_Intercept[1])) {
					str_wf_halfend_endbtn2_name = str_wf_halfend_endbtn_setBy_Intercept[1]; // 非正常结束/审批不通过/带有某种意见结束的!
				}
				if (str_wf_halfend_endbtn_setBy_Intercept.length > 2 && !getTBUtil().isEmpty(str_wf_halfend_endbtn_setBy_Intercept[2])) {
					str_wf_halfend_keepupbtn_name = str_wf_halfend_endbtn_setBy_Intercept[2]; // 加个图片!
				}
			}
			if (ClientEnvironment.isAdmin()) { // 如果是带管理密码登录的,则加上提示!在TextAreaDialog中第276行处对{}中的内容做了处理,
				str_wf_halfend_endbtn1_name = str_wf_halfend_endbtn1_name + "{可由参数【工作流半路结束时的正常结束按钮名称】重命名}"; //
				str_wf_halfend_endbtn2_name = str_wf_halfend_endbtn2_name + "{可由参数【工作流半路结束时的非正常结束按钮名称】重命名}"; //
				str_wf_halfend_keepupbtn_name = str_wf_halfend_keepupbtn_name + "{可由参数【工作流半路结束时的继续按钮名称】重命名}"; //
			}
			boolean isHalfEnd_twotype = getTBUtil().getSysOptionBooleanValue("工作流结束时是否选择正常与否", true); // 结束时是否分两种?

			if (isHalfEnd_twotype) { // 如果有两种结束情况!!
				int li_result = MessageBox.showOptionDialog(this, str_wf_halfend_msg, "提示", new String[] { str_wf_halfend_endbtn1_name, str_wf_halfend_endbtn2_name, str_wf_halfend_keepupbtn_name, " 取 消 " }, 2, 600, 150);// 默认为继续提交!!
				if (li_result == 3 || li_result == -1) { //
					return; //
				}
				if (li_result == 0) {
					onHalfEnd("正常结束"); // 直接结束
					return;
				} else if (li_result == 1) {
					onHalfEnd("非正常结束"); // 直接结束
					return; //
				}
			} else if (!getBtn_end().isShowing()) { // by haoming
				// 2016-05-04添加end按钮是否显示。如果显示了，外面可以直接结束。没必要再提示
				int li_result = MessageBox.showOptionDialog(this, str_wf_halfend_msg, "提示", new String[] { str_wf_halfend_endbtn1_name, str_wf_halfend_keepupbtn_name, " 取 消 " }, 1, 500, 150);// 默认为继续提交!!
				if (li_result == 2 || li_result == -1) { //
					return; //
				}
				if (li_result == 0) {
					onHalfEnd("正常结束"); // 直接结束
					return;
				}
			}
		}

		// 继续提交的逻辑!!
		try {
			if (!this.billCardPanel.newCheckValidate("submit", this)) { // 先校验一把,如果校验不过则退出,按道理应是先判断一下是否数据发生了变化,如果数据发生了变化则提示是否保存,如果选择保存时才做校验!
				// 这里先解决招行的需求,以后再优化!
				return;
			}

			this.billCardPanel.updateData(); // 提交前必须先保存一下!!
			this.billCardPanel.dealChildTable(true);//这里需要设置一下，否则引用子表的数据进行删除后，只是页面上删除，数据库中未删除，下次打开还会有删除的数据【李春娟/2018-06-22】
			setPrinstanceID(); // 先取一下,设置流程实例id
			BillVO billVO = this.billCardPanel.getBillVO(); // 取得页面上的数据!!!
			if (!billVO.isHaveKey("WFPRINSTANCEID")) { // 如果页面上包括流程实例主键!!
				MessageBox.show(this, "该记录没有定义工作流需要的列[wfprinstanceid],请先定义之!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			String str_billtype = billVO.getStringValue("billtype"); // 单据类型
			String str_busitype = billVO.getStringValue("busitype"); // 业务类型!
			int li_interCeptCheck = checkBeforeSubmit(str_billtype, str_busitype, billVO); // 提交之前的校验!!
			// 关键的业务需求点,即到处有特殊的校验需求,然后决定不允许提交!
			// ★★★★
			if (li_interCeptCheck < 0) { // 如果是-1则返回!
				return;
			}

			try {
				billCardPanel.getChangedItemOldValueSQL(); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			// 如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
			// 根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
			String str_wfinstance = getPrinstanceId(); // 创建流程实例,并返回流程实例ID!!!

			// 如果流程实例字段的值为空,则说明是要启动流程,即启动流程!!
			if (str_wfinstance == null || str_wfinstance.equals("")) {
				ActivityVO[] startVOs = getWFUIUtil().getStartActivityVOs(this, billVO); // 取得可以启动的VO
				if (startVOs != null && startVOs.length > 0) {
					ActivityVO startActivityVO = null; //
					if (startVOs.length == 1) { // 如果只有一个可以启动,则启动之
						startActivityVO = startVOs[0]; //
					} else { // 否则弹出流程图,并将可以启动的地方标出来让用户选择一个返回!
						WorkFlowChooseStartActivityDialog dialog = new WorkFlowChooseStartActivityDialog(this, startVOs); //
						dialog.setVisible(true); //
						if (dialog.getCloseType() == 1) { // 如果是确定返回!
							startActivityVO = dialog.getReturnActivityVO(); //
						} else {
							return; //
						}
					}

					if (startActivityVO != null) { // 如果找到了启动环节!即启动环节不为空!!
						str_wfinstance = getWFUIUtil().startWorkFlow(this, billVO, startActivityVO); // 先创建一个流程,并返回流程实例主键!!
						if (str_wfinstance != null) { // 回写页面,即往BillListPanel或BillCardPanel中回写主键..
							processDeal(str_wfinstance, billVO, "SUBMIT", true); // 再次立即提交该流程,从开始到现在基本上进行了三次访问,第一次是取得可以起动的环节,第二次是启动流程,第三次是执行流程!
						}
					} else {
						MessageBox.show(this, "启动环节为空,流程无法启动!");
						return;
					}
				} else {
					MessageBox.show(this, "没有找到一个启动环节!流程无法启动!");
					return;
				}
			} else { // 过程中的某个环节提交,即普通的正常提交!
				processDeal(str_wfinstance, billVO, "SUBMIT", false); // 处理流程中的
				// ★★★★★
			}

		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 提交等动作实际调用的处理逻辑!!!
	 * @param _prinstanceId
	 * @param _billVO
	 * @param _dealtype
	 * @param _isstart
	 * @throws Exception
	 */
	private void processDeal(String _prinstanceId, BillVO _billVO, String _dealtype, boolean _isstart) throws Exception {
		String str_loginuserid = ClientEnvironment.getCurrLoginUserVO().getId(); // 当前人员id
		String str_loginuserDeptID = ClientEnvironment.getCurrLoginUserVO().getBlDeptId(); // 当前人员所属机构

		// 以上一环节提交的部门为准 解决工作流人员兼职问题 【杨科/2013-03-13】
		if (getTBUtil().getSysOptionBooleanValue("工作流是否处理人员兼职", false)) {
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

		WFParVO firstTaskVO = getWFService().getFirstTaskVO(_prinstanceId, this.prDealPoolId, str_loginuserid, str_loginuserDeptID, _billVO, _dealtype); // 取得任务!!!即当前处理人的需处理任务!!!
		if (firstTaskVO == null) {
			MessageBox.show(this, "您没有该流程的任务!");
			return; //
		}
		if (!_isstart && firstTaskVO.isRejectedDirUp()) { // 如果不是启动时,然又是直接提交给领导
			if ("SEND".equals(_dealtype)) {// 退回先不允许转办较复杂/sunfujun/20120810
				MessageBox.show(this, "退回的任务不允许转办!");
				return;
			}
			dirUpSubmit(firstTaskVO, _billVO); //
			return;
		}

		// boolean isExecInterceptAfterWfEnd =
		// getTBUtil().getSysOptionBooleanValue("工作流中流程结束时是否要执行后台拦截器", false);
		// //
		WFParVO secondCallVO = null; // 第二次再次请求的参数VO,其实就是第一次取得的参数再次提交服务器!!
		if (firstTaskVO.isIsprocessed() || "SEND".equals(_dealtype)) { // 如果是终结者!!!
			// 直接提交!!!即如果当前环节类型不为空,且当前环节类型是END,即最后结束者,则不进行第二次提交!
			if (firstTaskVO.getCurractivityType() != null && firstTaskVO.getCurractivityType().equals("END") && !"SEND".equals(_dealtype)) {
				if (MessageBox.showConfirmDialog(this, "根据流程设计,您现在是流程的最后一步!\r\n您确定要提交并结束流程?", "结束工作流", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}// sunfujun/20120810/bug修改
				String str_message = textarea_msg.getText(); //
				String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
				String str_wfegbsintercept = (this.hvoCurrWFInfo == null ? null : hvoCurrWFInfo.getStringValue("process$wfegbsintercept")); //
				String str_endResult = getWFService().endWorkFlow(_prinstanceId, firstTaskVO, ClientEnvironment.getCurrLoginUserVO().getId(), str_message, str_msgfile, "正常结束", _billVO, str_wfegbsintercept); // 远程调用服务,结束流程！！！
				execAfterWorkFlowEnd(_billVO); // 高峰加的UI端流程结束后处理的逻辑,比如刷新页面等,理论上流程结束后,客户端与服务器端都会有拦截器,服务器端要有是因为要控制事务!!客户端要有是因为有UI页面处理(比如隐藏某个按钮)!
				MessageBox.show(this, "您是最后一步的执行者,结束流程成功!" + (str_endResult == null ? "" : "\r\n" + str_endResult)); // //
				closeParentWindow(1); //
			} else { // 正常的选择某个用户提交
				WorkFlowDealChooseUserDialog dialog = new WorkFlowDealChooseUserDialog(this, _billVO, firstTaskVO, _dealtype, _isstart, hvoCurrWFInfo, hvoCurrActivityInfo); // 选择用户!!!最复杂,最关键的页面!!!
				dialog.setVisible(true); //
				if (dialog.getClosetype() == BillDialog.CONFIRM) { // 如果点击了确定返回,即选择了某些人!!
					secondCallVO = dialog.getReturnVO(); //
					if (isYJBD) {
						secondCallVO.setMessage(yjbdPanel.getMsgText()); // 意见补登的意见
						secondCallVO.setMessagefile(yjbdPanel.getMsgFile()); // 意见实例的附件
						secondCallVO.setDealpooltask_yjbduserid(yjbdPanel.getBdObjUserVO().getId()); //
						secondCallVO.setDealpooltask_yjbdusercode(yjbdPanel.getBdObjUserVO().getCode()); //
						secondCallVO.setDealpooltask_yjbdusername(yjbdPanel.getBdObjUserVO().getName()); //
					} else {
						secondCallVO.setMessage(textarea_msg.getText()); // 设置处理意见
						String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
						secondCallVO.setMessagefile(str_msgfile); // 设置附件名..
						secondCallVO.setIfSendEmail(ifSendEmail.getValue());
						secondCallVO.setPrioritylevel((String) combobox_prioritylevel.getSelectedItem()); // 紧急程度
						secondCallVO.setDealtimelimit(refDate_dealtimelimit.getObject() == null ? null : ((RefItemVO) refDate_dealtimelimit.getObject()).getId()); // 最后要求处理期限
					}
					if (!secondCallVO.getWfinstanceid().equals(_prinstanceId)) {
						MessageBox.show(this, "由于某种未知原因,发现secondCallVO的流程实例id[" + secondCallVO.getWfinstanceid() + "]与传入的[" + _prinstanceId + "]不一样了,请与系统开发商联系!"); // 王雷在深圳农商行项目中发现有的子流程结束时莫名其妙的终止主流程,怀疑这里不一致了,所以加这样一个警告!!
						return; //
					}
					BillVO returnBillVO = getWFService().secondCall(secondCallVO, ClientEnvironment.getCurrLoginUserVO().getId(), _billVO, _dealtype); // 第二次再次请求!!即实际执行工作流!!!
					workflowBillVO = returnBillVO;
					closeParentWindow(1); // 关闭窗口!!!应该在关闭后才刷新列表数据!!!
				} else if (dialog.getClosetype() == BillDialog.CANCEL) { // 如果是取消,则要做一个判断:如果是启动环节,则要反向删除所有数据!!!,如果是直接点右上角的X关闭呢?会不会有问题???
					if (_isstart) { // 如果是第一次启动的,则回删流程实例!!!这一步非常重要!!!
						String str_sql_1 = "delete from pub_wf_dealpool   where prinstanceid='" + _prinstanceId + "'"; // 删除流程任务
						String str_sql_2 = "delete from pub_task_deal     where prinstanceid='" + _prinstanceId + "'"; // 删除消息任务!!
						String str_sql_3 = "delete from pub_wf_prinstance where id='" + _prinstanceId + "'"; // 删除流程流程实例!!
						String str_sql_4 = "update " + _billVO.getSaveTableName() + " set wfprinstanceid=null where " + _billVO.getPkName() + "='" + _billVO.getPkValue() + "'";
						UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3, str_sql_4 }); //
					}
				} else if (dialog.getClosetype() == 3) { // 点击结束按钮返回的!!!
					// 如果返回的是类型3,即结束!!!
					String str_message = textarea_msg.getText(); //
					String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
					String str_wfegbsintercept = (this.hvoCurrWFInfo == null ? null : hvoCurrWFInfo.getStringValue("process$wfegbsintercept")); //
					String str_endResult = getWFService().endWorkFlow(_prinstanceId, firstTaskVO, ClientEnvironment.getCurrLoginUserVO().getId(), str_message, str_msgfile, "正常结束", _billVO, str_wfegbsintercept); // 远程调用服务,结束流程！！！
					execAfterWorkFlowEnd(_billVO); // 高峰加的UI端流程结束后处理的逻辑,比如刷新页面等,理论上流程结束后,客户端与服务器端都会有拦截器,服务器端要有是因为要控制事务!!客户端要有是因为有UI页面处理(比如隐藏某个按钮)!
					MessageBox.show(this, "结束流程成功!" + (str_endResult == null ? "" : "\r\n" + str_endResult)); // //
					// 流程结束时是否要执行后台拦截器
					closeParentWindow(1); //
				}
			}
		} else { // 如果不是终结者,即是会签中间的某一步,则不要再弹出选择人员窗口,而是直接再次提交!!!
			secondCallVO = firstTaskVO; //
			secondCallVO.setMessage(textarea_msg.getText()); // 设置提交的意见!!!!
			secondCallVO.setIfSendEmail(ifSendEmail.getValue()); // 设置是否发送邮件!!!!
			String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
			secondCallVO.setMessagefile(str_msgfile); //
			getWFService().secondCall(secondCallVO, ClientEnvironment.getCurrLoginUserVO().getId(), _billVO, _dealtype); // 第二次再次请求!!即实际执行工作流!!!
			MessageBox.show(this, "您参与会签其中一步骤成功,该环节还需等待其他人会签才能结束!"); //
			closeParentWindow(1); //
		}
	}

	/**
	 * 流程结束后执行的拦截器! 感觉在后台做更对!!
	 * 高峰增加的流程结束后处理的拦截器!!! 一共有三个地方调用了,一个是半路结束；一个是正常提交时正好是最后一步的结束环节；一个是
	 * @param _billVO
	 * @throws Exception
	 */
	private void execAfterWorkFlowEnd(BillVO _billVO) throws Exception {
		// 流程结束后，UI端需要一个拦截器,理论上流程结束后,在客户端与服务器都应该有拦截器,服务器有是因为有事务处理要求,客户端有是因为有UI端的控件处理,比如隐藏某个按钮!!!
		// 这里一共用两个拦截器,一个是高峰在流程分配表中定义的,一个是后来平台升级后的流程中定义的拦截器!!
		if (hvoCurrWFInfo != null) {
			String str_flowintercept_ui = this.hvoCurrWFInfo.getStringValue("flowintercept_ui"); // 以后尽量不用这个定义!!!
			if (str_flowintercept_ui != null && str_flowintercept_ui.trim().indexOf(".") > 0) {
				WorkFlowUIIntercept2 intercept = (WorkFlowUIIntercept2) Class.forName(str_flowintercept_ui).newInstance(); //
				intercept.afterWorkFlowEnd(this, _billVO);
			}

			// 平台后来升级后定义的工作流UI端的拦截器!!!
			String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); // 流程后来升级的,统一的的UI端拦截器!!!
			if (str_wfeguiintercept != null && str_wfeguiintercept.trim().indexOf(".") > 0) { // 如果有定义
				WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance(); //
				String str_billtype = _billVO.getStringValue("billtype"); // 单据类型
				String str_busitype = _billVO.getStringValue("busitype"); // 业务类型!
				WLTHashMap parMap = new WLTHashMap(); //
				uiIntercept.afterWorkFlowEnd(this, str_billtype, str_busitype, _billVO, hvoCurrActivityInfo, parMap); // 工作流结束后执行的拦截器!!!
			}
		}
	}

	/**
	 * 直接提交给某个人,即乒乓式提交时需要这个功能,即不要再选择人员了,而是直接提交给对方!!
	 * 上面的processDeal()方法会调用这个!!!
	 * @param _firstTaskVO
	 * @param _billVO
	 */
	private void dirUpSubmit(WFParVO _firstTaskVO, BillVO _billVO) {
		try {
			if (MessageBox.showConfirmDialog(this, "您确定要直接提交给【" + _firstTaskVO.getDealpooltask_createusername() + "】吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}

			WFParVO secondCallVO = _firstTaskVO; //
			if (isYJBD) {
				secondCallVO.setMessage(yjbdPanel.getMsgText()); // 意见补登的意见
				secondCallVO.setMessagefile(yjbdPanel.getMsgFile()); // 意见实例的附件
				secondCallVO.setDealpooltask_yjbduserid(yjbdPanel.getBdObjUserVO().getId()); //
				secondCallVO.setDealpooltask_yjbdusercode(yjbdPanel.getBdObjUserVO().getCode()); //
				secondCallVO.setDealpooltask_yjbdusername(yjbdPanel.getBdObjUserVO().getName()); //
			} else {
				secondCallVO.setMessage(textarea_msg.getText()); // 设置处理意见
				secondCallVO.setIfSendEmail(ifSendEmail.getValue());
				String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
				secondCallVO.setMessagefile(str_msgfile); // 设置附件名..
				secondCallVO.setPrioritylevel((String) combobox_prioritylevel.getSelectedItem()); // 紧急程度
				secondCallVO.setDealtimelimit(refDate_dealtimelimit.getObject() == null ? null : ((RefItemVO) refDate_dealtimelimit.getObject()).getId()); // 最后要求处理期限

			}
			DealTaskVO commitTaskVO = new DealTaskVO(); //
			commitTaskVO.setParticipantUserId(_firstTaskVO.getDealpooltask_createuserid()); // 参与者必须是创建本任务的人!因为是乒乓式的
			commitTaskVO.setParticipantUserCode(_firstTaskVO.getDealpooltask_createusercode()); //
			commitTaskVO.setParticipantUserName(_firstTaskVO.getDealpooltask_createusername()); //

			// 来源环节就是我当前任务的当前环节
			commitTaskVO.setFromActivityId(_firstTaskVO.getCurractivity()); // 从哪个环节过来的
			commitTaskVO.setFromActivityName(_firstTaskVO.getCurractivityName()); //

			// 目标环节应该是创建我任务的来源环节
			commitTaskVO.setCurrActivityId(_firstTaskVO.getFromactivity()); // 目标环节必然是来源环节,因为是乒乓式对踢!!
			commitTaskVO.setCurrActivityCode(_firstTaskVO.getFromactivityCode()); //
			commitTaskVO.setCurrActivityName(_firstTaskVO.getFromactivityName()); //
			commitTaskVO.setCurrActivityType(_firstTaskVO.getFromactivityType()); // 来源环节

			commitTaskVO.setRejectedDirUp(false); // 因为是提交,所以对目标者来说必然是fasle,因为目标者同时拥有【退回】与【提交】操作!!
			secondCallVO.setCommitTaskVOs(new DealTaskVO[] { commitTaskVO }); //
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
			/* BillVO returnBillVO = */service.secondCall(secondCallVO, ClientEnvironment.getCurrLoginUserVO().getId(), _billVO, "SUBMIT"); // 第二次再次请求!!即实际执行工作流!!!
			MessageBox.show(this, "直接提交给【" + _firstTaskVO.getDealpooltask_createusername() + "】成功!"); //
			closeParentWindow(1); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * 【退回】按钮执行的动作!!!
	 * 可以退回到前面曾经走过的任意一个环节上的参与者.. 也可以退回发起人或上一步流程提交者!
	 */
	private void onReject() {
		try {
			setPrinstanceID(); // 先取一下,设置流程实例id
			BillVO billVO = this.billCardPanel.getBillVO(); // 取得页面上的数据!!!
			if (!billVO.isHaveKey("WFPRINSTANCEID")) { // 如果页面上包括流程实例主键!!
				MessageBox.show(this, "该记录没有定义工作流需要的列[wfprinstanceid],请先定义之!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			// 如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
			// 根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
			String str_wfinstance = getPrinstanceId(); // 创建流程实例,并返回流程实例ID!!!
			if (str_wfinstance == null || str_wfinstance.equals("")) { // //如果流程实例字段的值为空,则说明是要启动流程!!
				MessageBox.show(this, "流程实例主键为空,没有启动流程!请先启动之!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			this.billCardPanel.updateData(); // 提交前必须先保存一下!!

			String str_loginuserid = ClientEnvironment.getCurrLoginUserVO().getId(); //
			String str_loginuserDeptID = ClientEnvironment.getCurrLoginUserVO().getBlDeptId(); //

			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class);
			WFParVO firstTaskVO = service.getFirstTaskVO_Reject(str_wfinstance, this.prDealPoolId, str_loginuserid, str_loginuserDeptID, billVO); // 取得退回的任务,即流程中曾经走过的每一步的人员信息!
			if (firstTaskVO == null) {
				MessageBox.show(this, "您没有该流程的任务!");
				return; //
			}
			// by haoming
			// 2016-04-05在太平项目上，遇到工作流退回的时候，需要根据逻辑计算，只退回给上一层的经办人，而且只有一人。
			String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); // 流程后来升级的,统一的的UI端拦截器!!!
			WFParVO secondCallVO = null;
			if (str_wfeguiintercept != null) {
				WLTHashMap parMap = new WLTHashMap(); // 之所以最后总是增加一个HashMap参数,是因为根据经验,无论你一开始考虑得多么全面,在实践过程中总遇到需要增加新的参数的情况,但这样一来就会影响所有历史项目,扩展性很差!
				WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance(); //
				String str_billtype = billVO.getStringValue("billtype"); // 单据类型
				String str_busitype = billVO.getStringValue("busitype"); // 业务类型!
				secondCallVO = uiIntercept.onRejectCustSelectWRPar(this, str_billtype, str_busitype, billVO, hvoCurrActivityInfo, firstTaskVO, parMap); // 拦截平台定义的流程拦截器!!!
			}
			// 如果为空，那么执行默认逻辑
			if (secondCallVO == null || secondCallVO.getCommitTaskVOs() == null || secondCallVO.getCommitTaskVOs().length == 0) {
				WorkflowRejectDealDialog dialog = new WorkflowRejectDealDialog(this, billVO, firstTaskVO); // 退回的选择用户操作界面,即有三个选择:退给发起人,退给提交人,退给任意一步!
				dialog.setVisible(true); //
				if (dialog.getClosetype() != BillDialog.CONFIRM) { //
					return;
				}
				secondCallVO = dialog.getReturnVO(); // 取得处理的人员
			} else {
				DealTaskVO dealTaskVO[] = secondCallVO.getCommitTaskVOs();
				if (!MessageBox.confirm(this, "确定要退回给【" + dealTaskVO[0].getCurrActivityName() + "】吗?")) {
					return;
				}
			}
			if (isYJBD) {
				secondCallVO.setMessage(yjbdPanel.getMsgText()); // 意见补登的意见
				secondCallVO.setMessagefile(yjbdPanel.getMsgFile()); // 意见实例的附件
				secondCallVO.setDealpooltask_yjbduserid(yjbdPanel.getBdObjUserVO().getId()); //
				secondCallVO.setDealpooltask_yjbdusercode(yjbdPanel.getBdObjUserVO().getCode()); //
				secondCallVO.setDealpooltask_yjbdusername(yjbdPanel.getBdObjUserVO().getName()); //
			} else {
				secondCallVO.setMessage(textarea_msg.getText()); //
				secondCallVO.setIfSendEmail(ifSendEmail.getValue());
				String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); //
				secondCallVO.setMessagefile(str_msgfile); //
				secondCallVO.setPrioritylevel((String) combobox_prioritylevel.getSelectedItem()); // 紧急程度
				secondCallVO.setDealtimelimit(refDate_dealtimelimit.getObject() == null ? null : ((RefItemVO) refDate_dealtimelimit.getObject()).getId()); // 最后要求处理期限
			}
			service.secondCall_Reject(secondCallVO, ClientEnvironment.getCurrLoginUserVO().getId(), billVO); // 第二次再次请求!!即实际执行工作流!!!
			// refreshWorkFlowPanel(_prinstanceId); // 刷新流程面板!!
			if (getTBUtil().getSysOptionBooleanValue("工作流退回操作后是否关闭父窗口", true)) {
				MessageBox.show(this, "退回给【" + secondCallVO.getCommitTaskVOs()[0].getParticipantUserName() + "】操作成功!"); //
				closeParentWindow(1); // 关闭父亲窗口?民生要求直接关闭父亲窗口,但招行建议不关闭!!
				// 所以还是要搞个参数!!
			} else {
				MessageBox.show(this, "退回给【" + secondCallVO.getCommitTaskVOs()[0].getParticipantUserName() + "】成功!"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 【半路结束】按钮执行的动作!
	 * 中途半路结束流程!
	 */
	private void onHalfEnd(String _endType) {
		if (!this.billCardPanel.newCheckValidate("save")) { // 先校验一把,如果校验不过则退出,按道理应是先判断一下是否数据发生了变化,如果数据发生了变化则提示是否保存,如果选择保存时才做校验!
			// 这里先解决招行的需求,以后再优化!
			return;
		}

		try {
			this.billCardPanel.updateData(); // 结束前先保存表单
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
			return;
		}

		setPrinstanceID(); // 先取一下,设置流程实例id

		// 邵春芸说
		BillVO billVO = this.billCardPanel.getBillVO(); // 取得页面上的数据!!!
		String str_billtype = billVO.getStringValue("billtype"); // 单据类型
		String str_busitype = billVO.getStringValue("busitype"); // 业务类型!
		int li_interCeptCheck = checkBeforeSubmit(str_billtype, str_busitype, billVO); // 执行提交前的检验动作!!!
		if (li_interCeptCheck < 0) { // 如果是-1则返回!
			return;
		}

		String str_prinstanceid = getPrinstanceId(); //
		if (str_prinstanceid != null) {
			try {
				String str_dealpoolid = UIUtil.getStringValueByDS(null, "select id from pub_wf_dealpool where id='" + this.prDealPoolId + "' and issubmit='N' and isprocess='N'"); // 可能被其他人抢占处理了,所以再查询一下!
				if (str_dealpoolid == null) {
					MessageBox.show(this, "您没有当前流程的任务,不能进行任何处理!"); //
					return; //
				}
				String str_message = textarea_msg.getText(); //
				String str_msgfile = (refFile.getObject() == null ? null : ((RefItemVO) refFile.getObject()).getId()); // //
				WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class);

				WFParVO myvo = new WFParVO();
				if (this.hvoCurrActivityInfo != null) {
					myvo.setIntercept2(this.hvoCurrActivityInfo.getStringValue("intercept2")); // 拦截器2,其实就是BS端拦截器
				}
				if (currDealTaskInfo != null) {
					myvo.setApproveModel(currDealTaskInfo.getStringValue("approvemodel"));// 半路结束时需要根据这个来判断是否把同批的任务过掉
				}
				myvo.setDealpoolid(str_dealpoolid);// 这里必须设置一下，否则在下两行service.endWorkFlow()中用到，获得null，sql报错【李春娟/2012-05-11】
				String str_wfegbsintercept = (this.hvoCurrWFInfo == null ? null : hvoCurrWFInfo.getStringValue("process$wfegbsintercept")); //
				String str_endResult = service.endWorkFlow(str_prinstanceid, myvo, ClientEnvironment.getCurrLoginUserVO().getId(), str_message, str_msgfile, _endType, billVO, str_wfegbsintercept); // 结束流程!可能有结果!
				execAfterWorkFlowEnd(billVO); // 执行流程结束后的UI端拦截器!!!
				MessageBox.show(this, (str_endResult == null ? "结束流程成功!" : str_endResult)); // //
				this.btn_end.setEnabled(false); //
				closeParentWindow(1); //
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
			}
		}
	}

	/**
	 * 【确认】按钮执行的动作,即对抄送的任务进行确认!
	 * 抄送的确认值!!!
	 */
	private void onCCToConfirm() {
		// 根据dealpoolid删除消息表中的记录,再修改pub_wf_dealpool中的值!!!
		if (this.prDealPoolId == null) {
			MessageBox.show(this, "变量[" + prDealPoolId + "]为空,可能是旧的流程引擎机制,请使用新的流程引擎机制!"); //
			return; //
		}
		if (MessageBox.showConfirmDialog(this, "您确定要将这条任务移到已办任务中吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { //
			return; //
		}
		try {
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
			String msg = service.confirmUnEffectTask(prDealPoolId, this.str_OnlyViewReason, ClientEnvironment.getCurrLoginUserVO().getId()); // 调用远程服务!!!
			if (msg != null && !msg.equals("")) {//这里只是个提示，太平集团建议不要抛出异常【李春娟/2018-07-25】
				MessageBox.show(this, msg);
			}
		} catch (Exception _ex) {
			_ex.printStackTrace(); // 以前这里是报错的,但如果是被其他地方处理的情况,则报错是没有意义的,即所谓找不到任务是正常的!所以干脆搞成吃掉异常!!!
		}
		closeParentWindow(1); // 关闭父亲窗口
	}

	/**
	 * 【转办】按钮执行的动作!!!
	 */
	private void onTransSend() {
		try {
			setPrinstanceID(); // 先取一下,设置流程实例id
			BillVO billVO = this.billCardPanel.getBillVO(); // 取得页面上的数据!!!
			if (!billVO.isHaveKey("WFPRINSTANCEID")) { // 如果页面上包括流程实例主键!!
				MessageBox.show(this, "该记录没有定义工作流需要的列[wfprinstanceid],请先定义之!", WLTConstants.MESSAGE_WARN); //
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
				MessageBox.show(this, "流程未启动不能进行转办操作!", WLTConstants.MESSAGE_WARN); //
				return; //
			} else {
				processDeal(str_wfinstance, billVO, "SEND", false);
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 【催督办】按钮执行的动作!!所谓"催督办"就是提醒的意思!!!!
	 */
	private void onMind() { // 催督办 发消息 【杨科/2012-08-15】
		// 根据当前流程实例ID判断状态不为end先查子流程状态不为END的如果有就找所有子流程的currowner字段,如果没有就直接找本实例的currowner
		// 要有2个界面一个催办界面一个催办记录
		try {
			getWFUIUtil().dealCDB(this, this.getBillCardPanel().getTempletVO().getTempletname(), getPrinstanceId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 【流程监控】按钮执行的动作!!!
	 */
	private void onMonitor() {
		setPrinstanceID(); // 先取一下,设置流程实例id
		BillVO billVO = this.billCardPanel.getBillVO(); // 取得页面上的数据!!!
		if (!billVO.isHaveKey("WFPRINSTANCEID")) { // 如果页面上包括流程实例主键!!
			MessageBox.show(this, "该记录没有定义工作流需要的列[wfprinstanceid],请先定义之!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		if (!billVO.isHaveKey("WFPRINSTANCEID")) {
			MessageBox.show(this, "没有BillType与Busitype,WFPRINSTANCEID信息,不能进行流程处理!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		// 如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
		// 根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
		String str_wfinstance = getPrinstanceId(); // 流程实例ID..
		if (str_wfinstance == null || str_wfinstance.equals("")) { // 启动流程!如果流程实例为空
			MessageBox.show(this, "该记录还没有启动流程,请先提交启动流程!", WLTConstants.MESSAGE_WARN); //
			return;
		} else {
			WorkflowMonitorDialog dialog = new WorkflowMonitorDialog(parentWindow, str_wfinstance, billVO);
			dialog.setMaxWindowMenuBar();
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true); //
		}
	}

	/**
	 * 选择常用流程意见...
	 */
	private void onChooseCommMsg() {
		BillVO[] billvos = billList_hist.getBillVOs();
		ShowCommMsgDialog ss = null;
		if (billvos != null && billvos.length > 0) {
			String msgstr = (String) billvos[billvos.length - 1].getStringValue("dealmsg"); //
			ss = new ShowCommMsgDialog(parentWindow, msgstr); // 要改成OneSQLBillListConfirmDialog
		} else {
			ss = new ShowCommMsgDialog(parentWindow);
		}
		ss.setVisible(true);
		if (ss.getCloseType() == 1) {
			textarea_msg.setText(ShowCommMsgDialog.msg);
		}
		ss.dispose();
	}

	// 点击历史意见上的意见,可以拷贝到本意见框中来!!
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		int li_row = _event.getRow(); //
		RefItemVO refVO = (RefItemVO) _event.getBillListPanel().getValueAt(li_row, _event.getItemkey()); //
		String str_msg = refVO.getId(); // 消息!
		String str_msg_real = refVO.getCode(); // 如果加密了意见,则这就是加密前的数据!
		String str_reason = _event.getBillListPanel().getRealValueAtModel(li_row, "submitmessage_viewreason"); // 原因!
		if (textarea_msg != null && textarea_msg.isVisible()) {
			CopyHistMsgDialog dialog = new CopyHistMsgDialog(this, "查看意见", str_msg, str_reason, str_reason, true, billCardPanel.getBillVO()); //
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) {
				textarea_msg.append(str_msg); //
			}
		} else {
			CopyHistMsgDialog dialog = new CopyHistMsgDialog(this, "查看意见", str_msg, str_reason, str_reason, false, billCardPanel.getBillVO()); //
			dialog.setVisible(true); //
		}
	}

	/**
	 * 【取消】按钮执行的动作...
	 */
	private void onCancel() {
		closeParentWindow(2); //
	}

	// 定义可以取得业务卡片面板!!!
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
	 * 取得流程实例id,以前是直接从页面上取的,但后来增加了子流程,所以不能再从页面上取了,而是先判断类变量是否有,如果没有才从页面上取!
	 * 这样才能兼容旧的逻辑!!!
	 * 
	 * @return
	 */
	public String getPrinstanceId() {
		if (this.prInstanceId != null) {
			return this.prInstanceId; // 如果定义了,
		}
		return billCardPanel.getBillVO().getStringValue("wfprinstanceid"); //
	}

	private boolean isWfDealMsgPanelFloat() {
		return getTBUtil().getSysOptionBooleanValue("工作流处理意见面板是否浮动显示在外面", false); // 流程处理面板是否浮动在外面???
	}

	/**
	 * 校验意见!!
	 * @return
	 */
	private boolean validateMsg() {
		if (isYJBD) {
			return yjbdPanel.checkDataValidate();
		}

		if (getTBUtil().getSysOptionBooleanValue("流程处理中提交时处理意见是否必填", false)) {
			if ("".equals(textarea_msg.getText().trim())) {
				if (!getTBUtil().containTwoArrayCompare(ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes(), getTBUtil().split(unCheckMsgRole, ";")) && !((getPrinstanceId() == null || "".equals(getPrinstanceId())) && getTBUtil().getSysOptionBooleanValue("工作流处理页面中开始环节处理意见不做验证", false))) {
					textarea_msg.requestFocus(); //
					if (!isWfDealMsgPanelFloat()) { // 如果没有浮在外面,即在里面的时候,则自动滚动到意见框!
						makeDealMsgScrollToVisible(-1); // 让处理意见框滚动至显示!!
					}
					MessageBox.show(this, "请填写处理意见!\r\n此处不能超过2000个汉字\r\n处理意见超过2000个汉字时请用附件上传!");
					return false;
				}
			}
		}
		try {
			int li_length = textarea_msg.getText().getBytes("GBK").length; //
			if (li_length > 4000) { // 不能超过4000个字节,一个汉字算两个字节!
				MessageBox.show(this, "处理意见不能超过2000个汉字,请用附件上传!");
				return false;
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return true;
	}

	/**
	 * 让处理消息面板可显示!!
	 */
	private void makeDealMsgScrollToVisible(int _y) {
		if (textarea_msg != null && vflowPanel != null) { // 如果处理消息面板不为空,且vflowPanel不为空,则
			if (_y <= 0) {
				Point point = SwingUtilities.convertPoint(textarea_msg, 0, 0, vflowPanel.getScollPanel().getViewport()); // 计算出意见框在滚动框中的位置!
				vflowPanel.getScollPanel().getViewport().scrollRectToVisible(new Rectangle(0, (int) point.getY(), 750, 200)); // 滚动并显示计算出来的位置区域!
			} else {
				vflowPanel.getScollPanel().getViewport().scrollRectToVisible(new Rectangle(0, _y, 750, 200)); // 滚动并显示计算出来的位置区域!
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			if (!validateMsg()) { // 以前提交时有校验,但保存时没有!兴业项目中被客户发现了! 所以加上!
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
			onSubmit(); // 提交
		} else if (e.getSource() == btn_reject) { // 【退回】按钮!!
			if (isYJBD) {
				if (!yjbdPanel.checkDataValidate()) {
					return;
				}
			} else {
				if (getTBUtil().getSysOptionBooleanValue("流程处理中退回时处理意见是否必填", false)) {
					if ("".equals(textarea_msg.getText().trim())) {
						MessageBox.show(this, "请填写处理意见!\r\n此处不能超过2000个汉字\r\n处理意见超过2000个汉字时请用附件上传!");
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
			onReject(); // 退回，或打回去!
		} else if (e.getSource() == btn_end) {
			onHalfEnd("正常结束"); // 中途结束
		} else if (e.getSource() == btn_monitor) {
			closeType = 2;
			onMonitor(); // 流程监控
		} else if (e.getSource() == btn_monitor_) {// 追加工作流处理多页签方式相关
			// 【杨科/2013-04-08】
			closeType = 2;
			onMonitor(); // 流程监控
		} else if (e.getSource() == btn_calcel) {
			onCancel(); //
		} else if (e.getSource() == btn_ccConfirm) { // 抄送的确认键
			onCCToConfirm(); // 抄送的确认键
		} else if (e.getSource() == btn_transSend) {
			onTransSend();
		} else if (e.getSource() == btn_mind) { // 催督办
			onMind();
		} else if (e.getSource() == btn_commonmsgs) { // 常用意见
			onChooseCommMsg();
		} else if (e.getSource() == btn_saveMsg) { // 临时保存工作流意见!
			try {
				saveDealPoolMsg(); //
			} catch (Exception ex) {
				MessageBox.showException(this, ex); //
			}
		} else if (e.getSource() == btn_todeal_1) {// 追加工作流处理多页签方式相关
			// 【杨科/2013-04-08】
			tabbedPane.setSelectedIndex(1);
		} else if (e.getSource() == btn_todeal_2) {// 追加工作流处理多页签方式相关
			// 【杨科/2013-04-08】
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

	// 设置一下流程的id
	private void setPrinstanceID() {
		try {
			BillVO billVO = this.billCardPanel.getBillVO(); //
			if (getPrinstanceId() == null) { // 如果流程实例为空,重新取一下!但为什么会产生流程实例为空的情况呢???当时为什么要这么处理?
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
			return "请输入意见,点击【提交】按钮选择下一步骤启动流程!";
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
		return "当前步骤【" + str_text + "】"; //
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

	// 获取催督办按钮 【杨科/2012-11-28】
	public WLTButton getBtn_mind() {
		return btn_mind;
	}

	public void setBtn_mind(WLTButton btn_mind) {
		this.btn_mind = btn_mind;
	}

	// 通过UI拦截器中设置半路启动按钮文字
	public void setStr_wf_halfend_endbtn_setBy_Intercept(String[] btn_name) {
		str_wf_halfend_endbtn_setBy_Intercept = btn_name;
	}

	// 获取意见框
	public JPanel getMsgPanel() {
		if (dealMsgPanel == null) {
			dealMsgPanel = getDealMsgPanel();
		}
		return dealMsgPanel;
	}

}
