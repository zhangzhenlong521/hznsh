package cn.com.infostrategy.ui.sysapp.login.taskcenter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.to.sysapp.login.RoleVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTreeUI;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;
import cn.com.infostrategy.ui.workflow.WorkFlowDealEvent;
import cn.com.infostrategy.ui.workflow.WorkFlowDealListener;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessFrame;
import cn.com.infostrategy.ui.workflow.engine.WorkflowUIUtil;
import cn.com.infostrategy.ui.workflow.msg.MessageShowHtml;
import cn.com.infostrategy.ui.workflow.msg.MyMsgCenter_ReadedWFPanel;
import cn.com.infostrategy.ui.workflow.msg.MyMsgCenter_SendedWFPanel;
import cn.com.infostrategy.ui.workflow.msg.MyMsgCenter_UnReadWFPanel;

/**
 * 消息任务中心面板,关键类!! 这个类与AbstractWorkFlowStylePanel关系很密切!! 其中一些逻辑都是相通的!
 * 即以后的工作流都将在首页以任务中心的方式显示!!!
 * 现在的任务主要都是工作流,以后的任务应该可以是任意类型的,比如体系文件提交,不走工作流!!但也需要提交给某个人,然后这个人再进行某种处理!!!
 * 消息任务基本上分为两种,一种是是需要接收者再提交的,一种是只需要接收者查看的(也可理解成是抄送)!!
 * 如果是第一种,即需要接收者处理的,则理论上全部可以采用工作流!! 抄送的则也可以采用送入任务表的模式处理!!
 * 
 * 首页消息提醒有两种模式:
 * 1.只在业务表中修改某个状态,然后登录人员根据权限逻辑去查询,即是主动查询业务表的逻辑!
 * 2.在对业务表进行逻辑处理时,往pub_task_deal表中插入数据,即往消息中心表塞数据,然后登录人员是从这个任务表中查询! 而这个任务又分为需要处理与不需要处理两种!!
 * 第一种模式适用于新闻公告一类的,因为如果使用第二种模式,则不可能对每一个人都插入一条数据!!即这种模式是针对某一个角色送消息的!!
 * 第二种模式最明显的特征是针对某一个或几个绝对的人员送消息!! 即一人一任务!!! 而不是角色!! 
 * 总之第一种是新闻公告型的消息，第二种是待办流程型的消息!! 要根据实际情况合理选择!!
 * 
 * 后来觉得还是需要一张专门的消息中心表,像抄送的流程任务,或者流程结束时后的给发起人的提醒! 或者种系统消息,比如系统于什么时候重启过,系统发布了新版本,某某人员加入系统等!!
 * 这个消息表叫pub_msgcenter,这种表里有三种重要字段：接收人,接收角色,接收机构范围!! 即有时不是直接给某个人,而给某些角色,或某个机构范围下的某些角色!
 * 由于存在给某些角色的情况,所以还需要一张表记录某条消息是否已被某人阅读过,即有"未读消息"与"已读消息"之分!! 已读消息表名叫 pub_msgcenter_readed
 * @author xch
 *
 */
public class TaskAndMsgCenterPanel extends JPanel implements TreeSelectionListener, ActionListener, WorkFlowDealListener, BillListAfterQueryListener {

	private static final long serialVersionUID = 33442168166033497L;

	private boolean isSuperShow = false; //
	private JTree classTree = null; //分类树!
	private WLTButton refreshBtn, superShowBtn = null; //
	private DefaultMutableTreeNode rootNode, dealTaskNode, offTaskNode, bdTaskNode, unreadMsgNode, readedMsgNode, sendedMsgNode, msgRootNode; //

	private WorkflowUIUtil wfUIUtil = null; //
	private JPanel rightContentPanel = new JPanel(); //分割器右边的容器面板!!!
	private BillListPanel billList_allDealTask, billList_allOffTask, billList_allBDTask = null; //
	private HashMap billListPanelMap = new HashMap(); //为了提高性能,凡是选择过的列表模板,都统统缓存起来,这样再选择时就会不查询了!!
	private HashSet ht_alreadyQuery = new HashSet(); //记录是否曾经查询过!
	private WLTSplitPane splitPane = null; //
	boolean isTriggerTreeSelectEvent = true; //是否触发树的选择事件!!!
	private JPopupMenu popMenu = null; //弹出菜单!!
	private JMenuItem menuItem_1, menuItem_2, menuItem_3; //
	private TBUtil tbUtil = new TBUtil();
	private boolean ispopmsgpanel = true;
	private String templetCode = null;//传入一个模板编码可以只显示此流程的任务
	private boolean isshowmsg = true;//是否显示消息中心
	private boolean isshowbd = true;//是否显示意见补登
	private String bdinsql = null;

	//传阅 【杨科/2012-12-14】
	private boolean isshowPR = true;//是否显示传阅
	private DefaultMutableTreeNode PRNode, generalNode, unreadPRNode, readedPRNode, sendPRNode, workFlowNode, unreadWFNode, readedWFNode, sendWFNode;
	private String unreadsql, readedsql, sendsql = null;
	private BillListPanel billListPanel, opinionListPanel = null;
	private WLTButton select = null;

	//共享远程调用取过来的数据 【杨科/2013-02-18】
	private HashMap dealTaskCountMap = new HashMap();
	private HashMap offTaskCountMap = new HashMap();
	private HashMap bdMap = new HashMap();

	public TaskAndMsgCenterPanel() {
		initialize(); //
	}

	public TaskAndMsgCenterPanel(String _templetCode, boolean _isshowmsg) {
		this.templetCode = _templetCode;
		this.isshowmsg = _isshowmsg;
		initialize(); //
	}

	public TaskAndMsgCenterPanel(boolean _isSuperShow) {
		isSuperShow = _isSuperShow; //是否是管理身份页面!!
		initialize(); //
	}

	/**
	 * 
	 * @param _parent
	 * @param _tile
	 * @param _type
	 */
	public static JPanel getChildWorkPanel(String templetCode, boolean _ishsowmsg) {
		TaskAndMsgCenterPanel child = new TaskAndMsgCenterPanel(templetCode, _ishsowmsg);
		return child;
	}

	/**
	 * 初始化页面
	 */
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		rightContentPanel.setLayout(new BorderLayout()); //
		billList_allDealTask = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_task_deal_CODE1.xml")); //待办任务的列表
		rightContentPanel.add(billList_allDealTask); //
		isshowbd = TBUtil.getTBUtil().getSysOptionBooleanValue("工作流处理是否显示意见补登", false);
		JPanel leftPanel = new JPanel(new BorderLayout()); //
		refreshBtn = new WLTButton("刷新"); //
		refreshBtn.addActionListener(this); //

		superShowBtn = new WLTButton("所有数据"); //
		superShowBtn.addActionListener(this); //

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)); //
		btnPanel.setBackground(LookAndFeel.defaultShadeColor1); //
		btnPanel.add(refreshBtn); //加入按钮!
		if (ClientEnvironment.getInstance().isAdmin() && !isSuperShow) { //如果是管理员身份!!
			btnPanel.add(superShowBtn); //加入按钮!
		}

		leftPanel.add(btnPanel, BorderLayout.NORTH); //

		// 加载任务树
		leftPanel.add(new JScrollPane(getClassTree()), BorderLayout.CENTER); //

		splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, leftPanel, rightContentPanel); //
		billList_allDealTask.queryDataByDS(null, getAllDealTaskSQL(null, isSuperShow)); //直接通过SQL关联!!!
		billList_allDealTask.setTitleLabelText("待办任务，最新查询时间【" + tbUtil.getCurrTime() + "】"); //
		billList_allDealTask.setIsRefreshParent("1"); //设置关闭子页面不刷新父窗口    袁江晓  20130418
		dealTaskList();
		splitPane.setDividerLocation(200); //
		this.add(splitPane); //
	}

	//补齐任务中心待办任务列表模板名为空的行 【杨科/2013-02-18】
	private void dealTaskList() {
		BillVO[] BillVOs = billList_allDealTask.getBillVOs();
		for (int i = 0; i < BillVOs.length; i++) {
			String templetname = BillVOs[i].getRealValue("TEMPLETNAME");
			if (templetname == null || templetname.equals("")) {
				String[] str_values = (String[]) dealTaskCountMap.get(BillVOs[i].getRealValue("TEMPLETCODE"));
				String str_templetName = str_values[0];
				billList_allDealTask.setRealValueAt(str_templetName, i, "TEMPLETNAME");
			}
		}
	}

	//补齐任务中心已办任务列表模板名为空的行 【杨科/2013-02-18】
	private void offTaskList() {
		BillVO[] BillVOs = billList_allOffTask.getBillVOs();
		for (int i = 0; i < BillVOs.length; i++) {
			String templetname = BillVOs[i].getRealValue("TEMPLETNAME");
			if (templetname == null || templetname.equals("")) {
				String[] str_values = (String[]) offTaskCountMap.get(BillVOs[i].getRealValue("TEMPLETCODE"));
				String str_templetName = str_values[0];
				billList_allOffTask.setRealValueAt(str_templetName, i, "TEMPLETNAME");
			}
		}
	}

	//补齐任务中心意见补登列表模板名为空的行 【杨科/2013-02-18】
	private void bDTaskList() {
		BillVO[] BillVOs = billList_allBDTask.getBillVOs();
		for (int i = 0; i < BillVOs.length; i++) {
			String templetname = BillVOs[i].getRealValue("TEMPLETNAME");
			if (templetname == null || templetname.equals("")) {
				String[] str_values = (String[]) bdMap.get(BillVOs[i].getRealValue("TEMPLETCODE"));
				String str_templetName = str_values[0];
				billList_allBDTask.setRealValueAt(str_templetName, i, "TEMPLETNAME");
			}
		}
	}

	/**
	 * 取得分类树,模仿FoxMail的收发件箱
	 * @return
	 */
	private JTree getClassTree() {
		if (classTree != null) {
			return classTree;
		}
		HashVO hvoRoot = new HashVO(); //
		hvoRoot.setAttributeValue("$NodeType", "0"); //根结点
		hvoRoot.setAttributeValue("text", "所有任务"); //
		hvoRoot.setAttributeValue("count", "0"); //
		rootNode = new DefaultMutableTreeNode(hvoRoot); //

		HashVO hvoDealTask = new HashVO(); //
		hvoDealTask.setAttributeValue("$NodeType", "1"); //根结点
		hvoDealTask.setAttributeValue("text", "待办任务"); //
		hvoDealTask.setAttributeValue("count", "0"); //
		dealTaskNode = new DefaultMutableTreeNode(hvoDealTask); //

		HashVO hvoOffTask = new HashVO(); //
		hvoOffTask.setAttributeValue("$NodeType", "2"); //根结点
		hvoOffTask.setAttributeValue("text", "已办任务"); //
		hvoOffTask.setAttributeValue("count", "0"); //
		offTaskNode = new DefaultMutableTreeNode(hvoOffTask); //
		rootNode.add(dealTaskNode); //
		rootNode.add(offTaskNode); //

		//是否显示意见补登!
		if (isshowbd) {
			HashVO hvoBDTask = new HashVO();
			hvoBDTask.setAttributeValue("$NodeType", "9");
			hvoBDTask.setAttributeValue("text", "意见补登");
			hvoBDTask.setAttributeValue("count", "0");
			bdTaskNode = new DefaultMutableTreeNode(hvoBDTask);
			rootNode.add(bdTaskNode);
		}

		//是否显示消息中心!
		if (isshowmsg) {
			String str_df = TBUtil.getTBUtil().getSysOptionStringValue("消息中心父菜单定义", null); //
			HashVO hvoUnReadMsg = new HashVO();
			hvoUnReadMsg.setAttributeValue("$NodeType", "5");
			hvoUnReadMsg.setAttributeValue("text", "未读消息");
			hvoUnReadMsg.setAttributeValue("count", "0");
			unreadMsgNode = new DefaultMutableTreeNode(hvoUnReadMsg);

			HashVO hvoReadedMsg = new HashVO();
			hvoReadedMsg.setAttributeValue("$NodeType", "6");
			hvoReadedMsg.setAttributeValue("text", "已读消息");
			hvoReadedMsg.setAttributeValue("count", "0");
			readedMsgNode = new DefaultMutableTreeNode(hvoReadedMsg);

			HashVO hvoSendedMsg = new HashVO();
			hvoSendedMsg.setAttributeValue("$NodeType", "7");
			hvoSendedMsg.setAttributeValue("text", "已发消息");
			hvoSendedMsg.setAttributeValue("count", "0");
			sendedMsgNode = new DefaultMutableTreeNode(hvoSendedMsg);
			if (str_df == null) {
				rootNode.add(unreadMsgNode);
				rootNode.add(readedMsgNode);
				rootNode.add(sendedMsgNode);
			} else {
				HashVO msgRoot = new HashVO();
				msgRoot.setAttributeValue("$NodeType", "8");
				msgRoot.setAttributeValue("text", str_df);
				msgRoot.setAttributeValue("count", "0");
				msgRootNode = new DefaultMutableTreeNode(msgRoot);
				msgRootNode.add(unreadMsgNode);
				msgRootNode.add(readedMsgNode);
				msgRootNode.add(sendedMsgNode);
				rootNode.add(msgRootNode);
			}
		}

		//传阅 【杨科/2012-12-14】
		if (isshowPR) {
			HashVO PRRoot = new HashVO();
			PRRoot.setAttributeValue("$NodeType", "100"); //根结点
			PRRoot.setAttributeValue("text", "传阅消息");
			PRRoot.setAttributeValue("count", "0");
			PRNode = new DefaultMutableTreeNode(PRRoot);

			HashVO hvoGeneral = new HashVO();
			hvoGeneral.setAttributeValue("$NodeType", "100");
			hvoGeneral.setAttributeValue("text", "普通传阅");
			hvoGeneral.setAttributeValue("count", "0");
			generalNode = new DefaultMutableTreeNode(hvoGeneral);

			HashVO hvoUnreadPR = new HashVO();
			hvoUnreadPR.setAttributeValue("$NodeType", "101");
			hvoUnreadPR.setAttributeValue("text", "待阅消息");
			hvoUnreadPR.setAttributeValue("count", "0");
			unreadPRNode = new DefaultMutableTreeNode(hvoUnreadPR);

			HashVO hvoReadedPR = new HashVO();
			hvoReadedPR.setAttributeValue("$NodeType", "102");
			hvoReadedPR.setAttributeValue("text", "已阅消息");
			hvoReadedPR.setAttributeValue("count", "0");
			readedPRNode = new DefaultMutableTreeNode(hvoReadedPR);

			HashVO hvoSendPR = new HashVO();
			hvoSendPR.setAttributeValue("$NodeType", "103");
			hvoSendPR.setAttributeValue("text", "我的传阅");
			hvoSendPR.setAttributeValue("count", "0");
			sendPRNode = new DefaultMutableTreeNode(hvoSendPR);

			HashVO hvoWorkFlow = new HashVO();
			hvoWorkFlow.setAttributeValue("$NodeType", "100");
			hvoWorkFlow.setAttributeValue("text", "工作流传阅");
			hvoWorkFlow.setAttributeValue("count", "0");
			workFlowNode = new DefaultMutableTreeNode(hvoWorkFlow);

			HashVO hvoUnreadWF = new HashVO();
			hvoUnreadWF.setAttributeValue("$NodeType", "101");
			hvoUnreadWF.setAttributeValue("text", "待阅消息");
			hvoUnreadWF.setAttributeValue("count", "0");
			unreadWFNode = new DefaultMutableTreeNode(hvoUnreadWF);

			HashVO hvoReadedWF = new HashVO();
			hvoReadedWF.setAttributeValue("$NodeType", "102");
			hvoReadedWF.setAttributeValue("text", "已阅消息");
			hvoReadedWF.setAttributeValue("count", "0");
			readedWFNode = new DefaultMutableTreeNode(hvoReadedWF);

			HashVO hvoSendWF = new HashVO();
			hvoSendWF.setAttributeValue("$NodeType", "103");
			hvoSendWF.setAttributeValue("text", "我的传阅");
			hvoSendWF.setAttributeValue("count", "0");
			sendWFNode = new DefaultMutableTreeNode(hvoSendWF);

			rootNode.add(PRNode);

			PRNode.add(generalNode);
			generalNode.add(unreadPRNode);
			generalNode.add(readedPRNode);
			generalNode.add(sendPRNode);

			PRNode.add(workFlowNode);
			workFlowNode.add(unreadWFNode);
			workFlowNode.add(readedWFNode);
			workFlowNode.add(sendWFNode);
		}

		classTree = new JTree(rootNode); //"任务与消息"
		classTree.setUI(new WLTTreeUI(true)); //
		classTree.setOpaque(false);
		//classTree.setShowsRootHandles(true); //根据结点的左边有没有那个加减号图标,表示是否有儿子!!!!
		classTree.setRowHeight(19); //
		classTree.setCellRenderer(new MyTreeCellRender()); //
		classTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //只能单选!

		loadTaskNode(); //加载数据!!!

		expandAll(classTree, new TreePath(rootNode), false); //收起全部!
		expandAll(classTree, new TreePath(dealTaskNode.getPath()), true); //再将已办箱收起来!

		//传阅 【杨科/2012-12-14】
		//		if (isshowPR) {
		//			expandAll(classTree, new TreePath(unreadPRNode.getPath()), false); //再将传阅收起来!
		//			expandAll(classTree, new TreePath(readedPRNode.getPath()), false); //再将传阅收起来!
		//			expandAll(classTree, new TreePath(sendPRNode.getPath()), false); //再将传阅收起来!
		//			expandAll(classTree, new TreePath(unreadWFNode.getPath()), false); //再将传阅收起来!
		//			expandAll(classTree, new TreePath(readedWFNode.getPath()), false); //再将传阅收起来!
		//			expandAll(classTree, new TreePath(sendWFNode.getPath()), false); //再将传阅收起来!
		//		}

		setSelectNode(classTree, dealTaskNode); ////默认选中待办任务!
		classTree.getSelectionModel().addTreeSelectionListener(this); //最后才增加监听事件,如果在前面增加,当设置选中待办任务结点时,可能会造成触发选择事件!!!

		classTree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent _event) {
				super.mouseClicked(_event);
				if (_event.getButton() == MouseEvent.BUTTON3) { //如果是右键
					onShowPopMenu((JTree) _event.getSource(), _event.getX(), _event.getY()); //
				}
			}

		}); //
		return classTree; //

	}

	//加载待办箱与已办箱的所有任务!!
	private void loadTaskNode() {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("isSuperShow", new Boolean(isSuperShow)); //
			parMap.put("templetcode", templetCode);
			parMap.put("model", "dealTask");
			//			//传阅 【杨科/2012-12-14】 //放到lazyLoadPr方法里了
			//			if (isshowPR) {
			//				parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			//				parMap.put("corpid", ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
			//				parMap.put("roleids", ClientEnvironment.getCurrLoginUserVO().getAllRoleIds());
			//				//两次远程调用合成一次
			//				parMap.put("待阅SQL", unreadsql);
			//				parMap.put("已阅SQL", readedsql);
			//				parMap.put("传阅SQL", sendsql);
			//			}

			HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getTaskClassCountMap", parMap); //在服务器端实现的!!

			// 待办任务
			dealTaskCountMap = (HashMap) classCountMap.get("待办任务"); //
			if (dealTaskCountMap != null) { //如果有待办任务!!
				addTaskItemNode(dealTaskCountMap, dealTaskNode, "3"); //待办任务某模板明细!
			} else {
				((HashVO) dealTaskNode.getUserObject()).setAttributeValue("count", "0"); //
			}

			//			 已办任务
			//						offTaskCountMap = (HashMap) classCountMap.get("已办任务"); //
			//						if (offTaskCountMap != null) { //如果有已办任务!!
			//							addTaskItemNode(offTaskCountMap, offTaskNode, "4"); //已办任务某模板明细!
			//						} else {
			//							((HashVO) offTaskNode.getUserObject()).setAttributeValue("count", "0"); //
			//						}
			lazyLoadOffTask();

			//如果显示意见补登
			if (isshowbd) {
				lazyLoadBd();
				//								bdMap = (HashMap) classCountMap.get("意见补登");
				//								if (bdMap != null) {
				//									addTaskItemNode(bdMap, bdTaskNode, "10");
				//								} else {
				//									((HashVO) bdTaskNode.getUserObject()).setAttributeValue("count", "0"); //
				//								}
			}

			//如果显示消息!
			if (isshowmsg) {
				lazyLoadMsg();
				//								HashMap msgMap = (HashMap) classCountMap.get("消息中心");
				//								if (msgMap != null) {
				//									((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("未读消息"));
				//									((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("未读SQL"));
				//				
				//									((HashVO) readedMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("已读消息"));
				//									((HashVO) readedMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("已读SQL"));
				//				
				//									((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("已发消息"));
				//									((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("已发SQL"));
				//				
				//									if (msgRootNode != null) {
				//										((HashVO) msgRootNode.getUserObject()).setAttributeValue("count", msgMap.get("未读消息"));
				//										((HashVO) msgRootNode.getUserObject()).setAttributeValue("sql", msgMap.get("未读SQL"));
				//									}
				//								} else {
				//									((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("count", "0");
				//									((HashVO) readedMsgNode.getUserObject()).setAttributeValue("count", "0");
				//									((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("count", "0");
				//									if (msgRootNode != null) {
				//										((HashVO) msgRootNode.getUserObject()).setAttributeValue("count", "0");
				//									}
				//								}
			}

			//传阅 【杨科/2012-12-14】
			if (isshowPR) {
				//				HashMap PRMap = (HashMap) classCountMap.get("传阅"); //传阅 【杨科/2012-12-14】
				//				if (unreadsql == null || unreadsql.equals("")) {
				//					unreadsql = (String) PRMap.get("待阅SQL");
				//					readedsql = (String) PRMap.get("已阅SQL");
				//					sendsql = (String) PRMap.get("传阅SQL");
				//				}
				//
				//				String[] keys = { "普通待阅消息", "普通已阅消息", "普通传阅消息", "工作流待阅消息", "工作流已阅消息", "工作流传阅消息" };
				//				DefaultMutableTreeNode[] nodes = { unreadPRNode, readedPRNode, sendPRNode, unreadWFNode, readedWFNode, sendWFNode };
				//				for (int i = 0; i < keys.length; i++) {
				//					HashMap hm = (HashMap) PRMap.get(keys[i]);
				//					if (hm != null) {
				//						addItemNode(hm, nodes[i], "11" + i);
				//					} else {
				//						((HashVO) nodes[i].getUserObject()).setAttributeValue("count", "0");
				//					}
				//				}
				lazyLoadPr();
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 懒加载已办箱
	 */
	private void lazyLoadOffTask() {
		new Timer().schedule(new TimerTask() {
			public void run() {
				try {
					HashMap parMap = new HashMap(); //
					parMap.put("isSuperShow", new Boolean(isSuperShow)); //
					parMap.put("templetcode", templetCode);
					parMap.put("model", "offTask");
					HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getTaskClassCountMap", parMap);
					// 已办任务
					offTaskCountMap = (HashMap) classCountMap.get("已办任务"); //
					if (offTaskCountMap != null) { //如果有已办任务!!
						addTaskItemNode(offTaskCountMap, offTaskNode, "4"); //已办任务某模板明细!
					} else {
						((HashVO) offTaskNode.getUserObject()).setAttributeValue("count", "0"); //
					}
					updateJtree();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000);
	}

	/**
	 * 懒加载任务补登
	 */
	private void lazyLoadBd() {
		new Timer().schedule(new TimerTask() {
			public void run() {
				try {
					HashMap parMap = new HashMap();
					parMap.put("isSuperShow", new Boolean(isSuperShow));
					parMap.put("templetcode", templetCode);
					parMap.put("model", "bd");
					HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getTaskClassCountMap", parMap);
					bdMap = (HashMap) classCountMap.get("意见补登");
					if (bdMap != null) {
						addTaskItemNode(bdMap, bdTaskNode, "10");
					} else {
						((HashVO) bdTaskNode.getUserObject()).setAttributeValue("count", "0");
					}
					updateJtree();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000);
	}

	/**
	 * 懒加载消息中心
	 */
	private void lazyLoadMsg() {
		new Timer().schedule(new TimerTask() {
			public void run() {
				try {
					HashMap parMap = new HashMap(); //
					parMap.put("isSuperShow", new Boolean(isSuperShow)); //
					parMap.put("templetcode", templetCode);
					parMap.put("model", "msg");
					HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getTaskClassCountMap", parMap);
					HashMap msgMap = (HashMap) classCountMap.get("消息中心");
					if (msgMap != null) {
						((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("未读消息"));
						((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("未读SQL"));

						((HashVO) readedMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("已读消息"));
						((HashVO) readedMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("已读SQL"));

						((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("已发消息"));
						((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("已发SQL"));

						if (msgRootNode != null) {
							((HashVO) msgRootNode.getUserObject()).setAttributeValue("count", msgMap.get("未读消息"));
							((HashVO) msgRootNode.getUserObject()).setAttributeValue("sql", msgMap.get("未读SQL"));
						}
					} else {
						((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("count", "0");
						((HashVO) readedMsgNode.getUserObject()).setAttributeValue("count", "0");
						((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("count", "0");
						if (msgRootNode != null) {
							((HashVO) msgRootNode.getUserObject()).setAttributeValue("count", "0");
						}
					}
					updateJtree();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000);
	}

	/**
	 * 懒加载传阅信息
	 */
	private void lazyLoadPr() {
		new Timer().schedule(new TimerTask() {
			public void run() {
				try {
					HashMap parMap = new HashMap(); //
					parMap.put("isSuperShow", new Boolean(isSuperShow)); //
					parMap.put("templetcode", templetCode);
					parMap.put("model", "pr");
					parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
					parMap.put("corpid", ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
					parMap.put("roleids", ClientEnvironment.getCurrLoginUserVO().getAllRoleIds());
					//两次远程调用合成一次
					parMap.put("待阅SQL", unreadsql);
					parMap.put("已阅SQL", readedsql);
					parMap.put("传阅SQL", sendsql);
					HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getTaskClassCountMap", parMap);
					HashMap PRMap = (HashMap) classCountMap.get("传阅"); //传阅 【杨科/2012-12-14】
					if (unreadsql == null || unreadsql.equals("")) {
						unreadsql = (String) PRMap.get("待阅SQL");
						readedsql = (String) PRMap.get("已阅SQL");
						sendsql = (String) PRMap.get("传阅SQL");
					}
					String[] keys = { "普通待阅消息", "普通已阅消息", "普通传阅消息", "工作流待阅消息", "工作流已阅消息", "工作流传阅消息" };
					DefaultMutableTreeNode[] nodes = { unreadPRNode, readedPRNode, sendPRNode, unreadWFNode, readedWFNode, sendWFNode };
					for (int i = 0; i < keys.length; i++) {
						HashMap hm = (HashMap) PRMap.get(keys[i]);
						if (hm != null) {
							addItemNode(hm, nodes[i], "11" + i);
						} else {
							((HashVO) nodes[i].getUserObject()).setAttributeValue("count", "0");
						}
					}
					updateJtree();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1000);
	}

	//显示弹出菜单!!
	private void onShowPopMenu(JTree _tree, int _x, int _y) {
		TreePath parentPath = _tree.getClosestPathForLocation(_x, _y);
		if (parentPath != null) { //如果不为空!!
			_tree.setSelectionPath(parentPath); //
		}

		if (popMenu == null) {
			popMenu = new JPopupMenu(); //
			menuItem_1 = new JMenuItem("查看冗余任务"); //
			menuItem_2 = new JMenuItem("删除冗余任务"); //
			menuItem_3 = new JMenuItem("结点自身属性"); //

			menuItem_1.addActionListener(this); //
			menuItem_2.addActionListener(this); //
			menuItem_3.addActionListener(this); //

			popMenu.add(menuItem_1); //
			popMenu.add(menuItem_2); //
			popMenu.add(menuItem_3); //
		}

		//取得选中的结点!!!
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent(); //
		if (selNode.isRoot()) {
			return;
		}
		HashVO hvo = (HashVO) selNode.getUserObject(); //
		final String str_nodeType = hvo.getStringValue("$NodeType"); //结点类型!!
		if (!(str_nodeType.equals("3") || str_nodeType.equals("4"))) { //如果不是3与4则禁用
			menuItem_1.setEnabled(false); //
			menuItem_2.setEnabled(false); //
		} else {
			menuItem_1.setEnabled(true); //
			menuItem_2.setEnabled(true); //
		}
		popMenu.show(this.classTree, _x, _y); //显示!!
	}

	public void openBDTask() {
		if (billList_allBDTask == null) {
			billList_allBDTask = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_task_deal_CODE1.xml"));
		}
		billList_allBDTask.queryDataByDS(null, getAllBDTaskSQL(null, isSuperShow));
		billList_allBDTask.setTitleLabelText("意见补登，最新查询时间【" + tbUtil.getCurrTime() + "】"); //
		billList_allBDTask.setIsRefreshParent("1");//设置不关闭子页面不刷新父页面   袁江晓   20130418
		rightContentPanel.removeAll();
		rightContentPanel.setLayout(new BorderLayout());
		rightContentPanel.add(billList_allBDTask);
		bDTaskList();
		rightContentPanel.updateUI();
	}

	public void openBDTaskDetail(HashVO hvo) throws Exception {
		final String str_nodeType = hvo.getStringValue("$NodeType");
		String str_templetCode = hvo.getStringValue("templetcode"); //模板编码
		String str_tableName = hvo.getStringValue("tablename"); //查询的表名
		String str_savedtableName = hvo.getStringValue("savedtablename"); //查询的表名
		String str_pkName = hvo.getStringValue("pkname"); //主键字段名!
		String str_key = str_nodeType + "$" + str_templetCode; //
		BillListPanel billList = (BillListPanel) billListPanelMap.get(str_key); //已办箱与待办箱的模板是两个独立的,否则设置隐藏列会有问题!!!!
		if (billList == null) {
			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(str_templetCode); //先根据模板编码取得模板VO
			if (str_nodeType.equals("3")) { //如果是待办任务,则加上一些新列..
				templetVO.appendNewItemVOs("工作流处理信息", getWorkflowUIUtil().getDealTaskShowFieldNames(), true); //在原来VO上增加新的字段!
			} else if (str_nodeType.equals("4")) { //待办任务永远显示分页
				templetVO.setIsshowlistpagebar(true); //如果是已办任务,则必须有分页,否则会有性能问题!
				templetVO.appendNewItemVOs("工作流处理信息", getWorkflowUIUtil().getOffTaskShowFieldNames(), true); //在原来VO上增加新的字段!
			} else if (str_nodeType.equals("10")) { //待办任务永远显示分页
				templetVO.appendNewItemVOs("工作流处理信息", getWorkflowUIUtil().getDealTaskShowFieldNames(), true);
			}

			billList = new BillListPanel(templetVO, false); //根据模板VO创建
			billList.setIsRefreshParent("1");//设置如果关闭子页面则不刷新父页面
			billList.getTempletVO().setAutoLoads(0); //去掉自动加载数据的功能!!!
			billList.initialize(); //手动初始! 因为构建这设置成了不自动初始始化页面!!
			billList.setAllBillListBtnVisiable(false); //隐藏所有业务按钮!!
			billList.addWorkFlowDealPanel(); //
			billList.getWorkflowDealBtnPanel().addWorkFlowDealListener(this); //
			billList.getWorkflowDealBtnPanel().hiddenAllBtns(); //隐藏所有按钮!!!
			billList.getWorkflowDealBtnPanel().setViewWFBtnVisiable(true); //流程浏览的按钮要显示!!!

			if (str_nodeType.equals("3")) { //如果是待办箱,则只显示【处理】【监控】【导出】三个按钮!
				billList.setItemsVisible(getWorkflowUIUtil().getDealTaskHiddenFields(), false); //如果是待办箱则强制隐藏这些字段!
				billList.setItemsVisible(getWorkflowUIUtil().getDealTaskShowFields(), true); //如果是待办任务,则强制显示这些字段!
				billList.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
				billList.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //显示导出按钮!!!
				billList.getWorkflowDealBtnPanel().setPassReadBtnVisiable(true); //显示传阅按钮 【杨科/2012-11-28】

				if (isshowbd) {
					billList.getWorkflowDealBtnPanel().setYJBDBtnVisiable(true);
				}
			} else if (str_nodeType.equals("4")) { //如果是已办箱,则只显示【撤消】【监控】【导出】三个按钮!
				billList.setItemsVisible(getWorkflowUIUtil().getOffTaskHiddenFields(), false); //如果是已办任务,则强制隐藏这些字段!
				billList.setItemsVisible(getWorkflowUIUtil().getOffTaskShowFields(), true); //如果是已办任务,则强制显示这些字段!
				billList.getWorkflowDealBtnPanel().setCancelBtnVisiable(true); //显示撤回按钮!
				billList.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true); //显示监控按钮!
				billList.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //显示导出按钮!!!
				//管理员追加导出本页按钮 【杨科/2013-06-05】
				if (ClientEnvironment.isAdmin()) {
					billList.getWorkflowDealBtnPanel().setExportAllBtnVisiable(true); //显示导出全部按钮!!!
				}
				RoleVO[] loginUserVOs = ClientEnvironment.getCurrLoginUserVO().getRoleVOs();
				String canExportAllUserRoles = tbUtil.getSysOptionStringValue("工作流已处理任务显示全部导出的角色", null);
				if (!tbUtil.isEmpty(canExportAllUserRoles)) { //by haoming 2016-01-13
					String roles[] = tbUtil.split(canExportAllUserRoles, ";");
					for (int i = 0; i < roles.length; i++) {
						for (int j = 0; j < loginUserVOs.length; j++) {
							if (roles[i] != null && roles[i].equals(loginUserVOs[j].getCode())) {
								billList.getWorkflowDealBtnPanel().setExportAllBtnVisiable(true); //显示导出全部按钮!!! 
								break;
							}
						}
					}
				}
				billList.getWorkflowDealBtnPanel().setCDBBtnVisiable(true);
				billList.getWorkflowDealBtnPanel().setPassReadBtnVisiable(true); //显示传阅按钮 【杨科/2012-11-28】
				billList.addBillListAfterQueryListener(this); //
			} else if (str_nodeType.equals("10")) { //如果是意见补登,则只显示【意见补登】【监控】【导出】三个按钮!
				billList.setItemsVisible(getWorkflowUIUtil().getDealTaskHiddenFields(), false);
				billList.setItemsVisible(getWorkflowUIUtil().getDealTaskShowFields(), true);
				if (isshowbd) {
					billList.getWorkflowDealBtnPanel().setYJBDBtnVisiable(true);
				}
				billList.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList.getWorkflowDealBtnPanel().setExportBtnVisiable(true);
			}

			billList.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onQuickQuery((BillQueryPanel) e.getSource(), str_nodeType);
				}
			}); //

			billListPanelMap.put(str_key, billList); //先注册起来
		} //如果还没创建,则创建之,并注册起来!!!

		if (!ht_alreadyQuery.contains(str_key)) { //如果包含!!
			String str_sql = null; //
			if (str_nodeType.equals("3")) { //如果是待办的,则使用待办的SQL
				str_sql = getWorkflowUIUtil().getDealTaskSQL(str_templetCode, str_tableName, str_savedtableName, str_pkName, ClientEnvironment.getCurrSessionVO().getLoginUserId(), null, isSuperShow); //
			} else if (str_nodeType.equals("4")) { //如果是已办的,则使用已办的SQL
				str_sql = getWorkflowUIUtil().getOffTaskSQL(str_templetCode, str_tableName, str_savedtableName, str_pkName, ClientEnvironment.getCurrSessionVO().getLoginUserId(), null, isSuperShow); //
			} else if (str_nodeType.equals("10")) { //如果是已办的,则使用已办的SQL
				str_sql = getWorkflowUIUtil().getBDTaskSQL(str_templetCode, str_tableName, str_savedtableName, str_pkName, getBDInsql(), null, isSuperShow); //
			}
			billList.getQuickQueryPanel().resetAllQuickQueryCompent(); //清空所有条件!
			billList.queryDataByDS(null, str_sql, true); //直接通过SQL关联!!! //★★★★★★★★★★★★★★查询数据★★★★★★★★★★★★
			if (str_nodeType.equals("4")) { //如果是已办任务,则需要将相同的合并!原因是待办任务中有多条任务其实是一件事!!但任务是多条!
				resetBackColor4(billList); //重置背景颜色!!
			}
			if (billList.getRowCount() > 0) {
				//billList.setSelectedRow(0); //自动选择第一行!!
			}
			ht_alreadyQuery.add(str_key); //
		}
		rightContentPanel.removeAll(); //
		rightContentPanel.setLayout(new BorderLayout()); //
		rightContentPanel.add(billList); //重新加入!!
		rightContentPanel.updateUI(); //
	}

	public void openMsgCenter(String type) {
		rightContentPanel.removeAll();
		rightContentPanel.setLayout(new BorderLayout());
		if ("5".equals(type) || "8".equals(type)) {
			MyMsgCenter_UnReadWFPanel unReadmsgPanel = (MyMsgCenter_UnReadWFPanel) billListPanelMap.get("未读消息");
			if (unReadmsgPanel == null) {
				unReadmsgPanel = new MyMsgCenter_UnReadWFPanel(this, ((HashVO) unreadMsgNode.getUserObject()).getStringValue("sql"));
				unReadmsgPanel.initialize();
				billListPanelMap.put("未读消息", unReadmsgPanel);
				ht_alreadyQuery.add("未读消息");
			}
			if (!ht_alreadyQuery.contains("未读消息")) {
				unReadmsgPanel.query();
				ht_alreadyQuery.add("未读消息");
			}
			rightContentPanel.add(unReadmsgPanel);
		} else if ("6".equals(type)) {
			MyMsgCenter_ReadedWFPanel readedmsgPanel = (MyMsgCenter_ReadedWFPanel) billListPanelMap.get("已读消息");
			if (readedmsgPanel == null) {
				readedmsgPanel = new MyMsgCenter_ReadedWFPanel(this, ((HashVO) readedMsgNode.getUserObject()).getStringValue("sql"));
				readedmsgPanel.initialize();
				billListPanelMap.put("已读消息", readedmsgPanel);
				ht_alreadyQuery.add("已读消息");
			}
			if (!ht_alreadyQuery.contains("已读消息")) {
				readedmsgPanel.query();
				ht_alreadyQuery.add("已读消息");
			}
			rightContentPanel.add(readedmsgPanel);
		} else if ("7".equals(type)) {
			MyMsgCenter_SendedWFPanel sendedmsgPanel = (MyMsgCenter_SendedWFPanel) billListPanelMap.get("已发消息");
			if (sendedmsgPanel == null) {
				sendedmsgPanel = new MyMsgCenter_SendedWFPanel(this, ((HashVO) sendedMsgNode.getUserObject()).getStringValue("sql"));
				sendedmsgPanel.initialize();
				billListPanelMap.put("已发消息", sendedmsgPanel);
				ht_alreadyQuery.add("已发消息");
			}
			if (!ht_alreadyQuery.contains("已发消息")) {
				sendedmsgPanel.query();
				ht_alreadyQuery.add("已发消息");
			}
			rightContentPanel.add(sendedmsgPanel);
		}
		rightContentPanel.updateUI();
	}

	public void onRefresh() {
		onRefresh(true); //
	}

	public void onRefreshMsgOnly() {
		try {
			ht_alreadyQuery.remove("未读消息");
			ht_alreadyQuery.remove("已读消息");
			ht_alreadyQuery.remove("已发消息");
			HashMap parMap = new HashMap();
			parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			parMap.put("corpid", ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
			parMap.put("roleids", ClientEnvironment.getCurrLoginUserVO().getAllRoleIds());
			HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.msg.MsgBsUtil", "getMsgCountMap", parMap); //在服务器端实现的!!
			if (classCountMap != null) {
				((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("count", classCountMap.get("未读消息"));
				((HashVO) readedMsgNode.getUserObject()).setAttributeValue("count", classCountMap.get("已读消息"));
				((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("count", classCountMap.get("已发消息"));
				if (msgRootNode != null) {
					((HashVO) msgRootNode.getUserObject()).setAttributeValue("count", classCountMap.get("未读消息"));
				}
			} else {
				((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("count", "0");
				((HashVO) readedMsgNode.getUserObject()).setAttributeValue("count", "0");
				((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("count", "0");
				if (msgRootNode != null) {
					((HashVO) msgRootNode.getUserObject()).setAttributeValue("count", "0");
				}
			}
			updateJtree(); //
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//刷新整个页面!!
	private void onRefresh(boolean _autoSelAndRefresh) {
		isTriggerTreeSelectEvent = false;//
		ht_alreadyQuery.clear(); //清空!
		dealTaskNode.removeAllChildren(); //
		offTaskNode.removeAllChildren(); //
		if (isshowbd) {
			bdTaskNode.removeAllChildren();
		}

		//传阅 【杨科/2012-12-14】
		if (isshowPR) {
			unreadPRNode.removeAllChildren();
			readedPRNode.removeAllChildren();
			sendPRNode.removeAllChildren();
			unreadWFNode.removeAllChildren();
			readedWFNode.removeAllChildren();
			sendWFNode.removeAllChildren();
		}

		loadTaskNode(); //加载所有数据..

		expandAll(classTree, new TreePath(rootNode), false); //收起全部!
		expandAll(classTree, new TreePath(dealTaskNode.getPath()), true); //再将待办箱展开!

		//传阅 【杨科/2012-12-14】
		//		if (isshowPR) {
		//			expandAll(classTree, new TreePath(unreadPRNode.getPath()), false); //再将传阅收起来!
		//			expandAll(classTree, new TreePath(readedPRNode.getPath()), false); //再将传阅收起来!
		//			expandAll(classTree, new TreePath(sendPRNode.getPath()), false); //再将传阅收起来!
		//			expandAll(classTree, new TreePath(unreadWFNode.getPath()), false); //再将传阅收起来!
		//			expandAll(classTree, new TreePath(readedWFNode.getPath()), false); //再将传阅收起来!
		//			expandAll(classTree, new TreePath(sendWFNode.getPath()), false); //再将传阅收起来!
		//		}

		updateJtree(); //必须要做一下,否则页面刷新不过来!!!
		if (_autoSelAndRefresh) { //如果需要自动选中根结点并且刷新另边的列表!!!
			setSelectNode(classTree, dealTaskNode); ////默认选中待办任务!
			billList_allDealTask.queryDataByDS(null, getAllDealTaskSQL(null, isSuperShow)); //直接通过SQL关联!!!
			billList_allDealTask.setTitleLabelText("待办任务，最新查询时间【" + tbUtil.getCurrTime() + "】"); //

			rightContentPanel.removeAll(); //
			rightContentPanel.setLayout(new BorderLayout()); //
			rightContentPanel.add(billList_allDealTask); //
			dealTaskList();
			rightContentPanel.updateUI(); //必须要做一下!!!
		}
		isTriggerTreeSelectEvent = true;//打开开关!!
	}

	//显示超级界面!即不做任何权限过滤的界面! 因为在项目过程中经常遇到一些问题,需要不做任何权限逻辑,穿透查询出所有任务!!
	private void onSuperShow() {
		TaskAndMsgCenterPanel taskPanel = new TaskAndMsgCenterPanel(true); //

		JFrame frame = new JFrame("显示所有工作流任务"); //
		frame.setLocation(100, 100); //
		frame.setSize(900, 600); ////
		frame.getContentPane().add(taskPanel); ////
		frame.setVisible(true); ////
	}

	//显示垃圾数据!!
	private void onShowRubbishData() {
		try {
			TreePath selectedPath = this.classTree.getSelectionPath(); //
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent(); //选中的结点!!
			HashVO hvo = (HashVO) selNode.getUserObject(); //
			String str_nodeName = hvo.toString(); //结点名称!!
			String str_nodeType = hvo.getStringValue("$NodeType"); //结点类型!!
			String str_templetCode = hvo.getStringValue("templetcode"); //模板编码
			String str_tableName = hvo.getStringValue("tablename"); //查询的表名
			String str_savedtableName = hvo.getStringValue("savedtablename"); //查询的表名
			String str_pkName = hvo.getStringValue("pkname"); //主键字段名!
			if (str_nodeType.equals("3")) {
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("select count(*) from pub_task_deal where ");
				sb_sql.append("templetcode='" + str_templetCode + "' "); //模板编码!!
				sb_sql.append("and tabname='" + str_savedtableName + "' "); //表名!!
				sb_sql.append("and pkvalue not in (select " + str_pkName + " from " + str_tableName + ") "); //主键值!!
				String str_result = UIUtil.getStringValueByDS(null, sb_sql.toString()); //
				MessageBox.show(classTree, "[" + str_nodeName + "]的待办箱中共有[" + str_result + "]条冗余数据!"); //
			} else if (str_nodeType.equals("4")) {
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("select count(*) from pub_task_off where ");
				sb_sql.append("templetcode='" + str_templetCode + "' "); //模板编码!!
				sb_sql.append("and tabname='" + str_savedtableName + "' "); //表名!!
				sb_sql.append("and pkvalue not in (select " + str_pkName + " from " + str_tableName + ") "); //主键值!!
				String str_result = UIUtil.getStringValueByDS(null, sb_sql.toString()); //
				MessageBox.show(classTree, "[" + str_nodeName + "]的已办箱中共有[" + str_result + "]条冗余数据!"); //
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	private void onDeleteRubbishData() {
		try {
			TreePath selectedPath = this.classTree.getSelectionPath(); //
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent(); //选中的结点!!
			HashVO hvo = (HashVO) selNode.getUserObject(); //
			String str_nodeName = hvo.toString(); //结点名称!!
			String str_nodeType = hvo.getStringValue("$NodeType"); //结点类型!!
			String str_templetCode = hvo.getStringValue("templetcode"); //模板编码
			String str_tableName = hvo.getStringValue("tablename"); //查询的表名
			String str_savedtableName = hvo.getStringValue("savedtablename"); //查询的表名
			String str_pkName = hvo.getStringValue("pkname"); //主键字段名!
			if (str_nodeType.equals("3")) {
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("select distinct prinstanceid from pub_task_deal where "); //找出流程实例的id
				sb_sql.append("templetcode='" + str_templetCode + "' "); //模板编码!!
				sb_sql.append("and tabname='" + str_savedtableName + "' "); //表名!!
				sb_sql.append("and pkvalue not in (select " + str_pkName + " from " + str_tableName + ") "); //主键值!!
				String[] str_ids = UIUtil.getStringArrayFirstColByDS(null, sb_sql.toString()); //
				if (str_ids == null || str_ids.length <= 0) {
					MessageBox.show(classTree, "[" + str_nodeName + "]的待办箱中没有冗余数据!"); //
					return; //
				}
				String str_incondition = tbUtil.getInCondition(str_ids); //
				String str_sql_1 = "delete from pub_task_deal where prinstanceid in (" + str_incondition + ")"; //
				String str_sql_2 = "delete from pub_wf_dealpool where prinstanceid in (" + str_incondition + ")"; //
				String str_sql_3 = "delete from pub_wf_prinstance where id in (" + str_incondition + ")"; //
				UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3 }); //
				MessageBox.show(classTree, "删除[" + str_nodeName + "]的待办箱中冗余数据成功!"); //
			} else if (str_nodeType.equals("4")) {
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("select distinct prinstanceid from pub_task_off where ");
				sb_sql.append("templetcode='" + str_templetCode + "' "); //模板编码!!
				sb_sql.append("and tabname='" + str_savedtableName + "' "); //表名!!
				sb_sql.append("and pkvalue not in (select " + str_pkName + " from " + str_tableName + ") "); //主键值!!
				String[] str_ids = UIUtil.getStringArrayFirstColByDS(null, sb_sql.toString()); //
				if (str_ids == null || str_ids.length <= 0) {
					MessageBox.show(classTree, "[" + str_nodeName + "]的待办箱中没有冗余数据!"); //
					return; //
				}
				String str_incondition = tbUtil.getInCondition(str_ids); //
				String str_sql_1 = "delete from pub_task_off where prinstanceid in (" + str_incondition + ")"; //
				String str_sql_2 = "delete from pub_wf_dealpool where prinstanceid in (" + str_incondition + ")"; //
				String str_sql_3 = "delete from pub_wf_prinstance where id in (" + str_incondition + ")"; //
				UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3 }); //
				MessageBox.show(classTree, "删除[" + str_nodeName + "]的已办箱中冗余数据成功!"); //
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * 在待办任务与已办任务的结点下增加新的子结点!!!
	 * @param _returnMap
	 * @param _parentNode
	 * @param _nodeType
	 */
	private void addTaskItemNode(HashMap _returnMap, DefaultMutableTreeNode _parentNode, String _nodeType) {
		String[] str_keys = (String[]) _returnMap.keySet().toArray(new String[0]); //得到所有的key
		int li_allCount = 0; //
		HashVO[] allItemVOs = new HashVO[str_keys.length]; //
		for (int i = 0; i < str_keys.length; i++) {
			String str_templetCode = str_keys[i]; //
			String[] str_values = (String[]) _returnMap.get(str_keys[i]); //
			String str_templetName = str_values[0]; //
			String str_tableName = str_values[1]; //查询的表名
			String str_savedtablename = str_values[2]; //保存的表名
			String str_pkName = str_values[3]; //主键字段名!
			String str_count = str_values[4]; //
			allItemVOs[i] = new HashVO(); //
			allItemVOs[i].setAttributeValue("$NodeType", _nodeType); //待办任务之某模板!
			allItemVOs[i].setAttributeValue("templetcode", str_templetCode); //模板编码!
			allItemVOs[i].setAttributeValue("templetname", str_templetName); //模板名称
			allItemVOs[i].setAttributeValue("tablename", str_tableName); //查询的表名
			allItemVOs[i].setAttributeValue("savedtablename", str_savedtablename); //保存的表名
			allItemVOs[i].setAttributeValue("pkname", str_pkName); //主键字段名!
			allItemVOs[i].setAttributeValue("text", (str_templetName == null ? str_templetCode : str_templetName)); //显示的名称
			allItemVOs[i].setAttributeValue("count", str_count); ////
			li_allCount = li_allCount + Integer.parseInt(str_count); //
		}

		//为了顺序对所有任务进行排序!!!以后可能 会按指定的顺序排序!!!
		tbUtil.sortHashVOs(allItemVOs, new String[][] { { "templetname", "N", "N" } }); //

		//遍历创建结点!!!

		//邮储项目中有个需求,就是觉得待办任务中的子结点太多,想再加一层,使用一级目录模块!作为分类!
		//这有很多种方法,但最后想还是使用一个全局参数,最简洁!!,
		String str_df = TBUtil.getTBUtil().getSysOptionStringValue("工作流中心树型目录分类定义", null); //
		HashMap map = null; //
		String[] str_dfdirkeys = null; //
		if (str_df != null && !str_df.trim().equals("")) { //
			map = new HashMap(); //
			str_df = TBUtil.getTBUtil().replaceAll(str_df, " ", ""); //
			str_df = TBUtil.getTBUtil().replaceAll(str_df, "\r", ""); //
			str_df = TBUtil.getTBUtil().replaceAll(str_df, "\n", ""); //
			HashMap initMap = TBUtil.getTBUtil().convertStrToMapByExpress(str_df, ";", "="); //
			str_dfdirkeys = (String[]) initMap.keySet().toArray(new String[0]); //所有定义的目录
			for (int i = 0; i < str_dfdirkeys.length; i++) {
				String str_models = (String) initMap.get(str_dfdirkeys[i]); //
				String[] str_modelArray = TBUtil.getTBUtil().split(str_models, ","); //
				for (int j = 0; j < str_modelArray.length; j++) {
					map.put(str_modelArray[j], str_dfdirkeys[i]); //
				}
			}
		}

		HashMap alreadyAddedDirNodeMap = new HashMap(); //用来记录曾经创建过的目录结点!!!
		ArrayList al_onlyLeafNode = new ArrayList(); // 
		for (int i = 0; i < allItemVOs.length; i++) {
			String str_thisNodeText = allItemVOs[i].toString(); //
			//System.out.println("model:" + str_thisNodeText); //
			if (map != null && map.containsKey(str_thisNodeText)) { //如果发现我被指定了一个目录,则先创建目录!
				String str_dirName = (String) map.get(str_thisNodeText); //
				if (!alreadyAddedDirNodeMap.containsKey(str_dirName)) { //如果还没登记,则先登录一下,即相同的目录只算一个!
					HashVO modelDirVO = new HashVO(); //
					modelDirVO.setAttributeValue("$NodeType", "-1"); //是目录结点
					modelDirVO.setAttributeValue("text", str_dirName); //
					modelDirVO.setAttributeValue("count", "0"); //
					modelDirVO.setToStringFieldName("text"); //
					DefaultMutableTreeNode addDirNode = new DefaultMutableTreeNode(modelDirVO); //
					alreadyAddedDirNodeMap.put(str_dirName, addDirNode); //
				}

				DefaultMutableTreeNode dirNode = (DefaultMutableTreeNode) alreadyAddedDirNodeMap.get(str_dirName); //目录结点!
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(allItemVOs[i]); //本结点
				dirNode.add(node); //目录结点下面加上本结点

				//_parentNode.add(dirNode); //将目录结点加入!!
			} else {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(allItemVOs[i]); //
				al_onlyLeafNode.add(node); //
				//_parentNode.add(node); //增加结点
			}
		}

		//加入有目录的结点,必须按指定的先后顺序排序!
		String[] str_realdirs = (String[]) alreadyAddedDirNodeMap.keySet().toArray(new String[0]); //
		if (str_realdirs != null && str_realdirs.length > 0) {
			TBUtil.getTBUtil().sortStrsByOrders(str_realdirs, str_dfdirkeys); //
			for (int i = 0; i < str_realdirs.length; i++) { //
				DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) alreadyAddedDirNodeMap.get(str_realdirs[i]); //
				_parentNode.add(tmpNode); //将目录结点加入!!
			}
		}

		//加入那些没有目录的结点!
		for (int i = 0; i < al_onlyLeafNode.size(); i++) {
			DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) al_onlyLeafNode.get(i); //
			_parentNode.add(tmpNode); //将目录结点加入!!
		}

		HashVO hvo = (HashVO) _parentNode.getUserObject(); //
		hvo.setAttributeValue("count", "" + li_allCount); //设置父亲结点的总数
	}

	/**
	 * 选择树结点变化事件
	 */
	public void valueChanged(TreeSelectionEvent _event) {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			if (!isTriggerTreeSelectEvent) { //如果不让触发,则直接返回!!
				return;
			}
			TreePath selectedPath = classTree.getSelectionPath(); //选中的路径!!!
			if (selectedPath == null) { //如果选中的路径为空,则返回!!!
				return; //
			}
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent(); //
			HashVO hvo = (HashVO) selNode.getUserObject(); //
			final String str_nodeType = hvo.getStringValue("$NodeType"); //结点类型!!
			if (str_nodeType.equals("1")) { //所有待办任务,刷新所有待办任务!
				billList_allDealTask.queryDataByDS(null, getAllDealTaskSQL(null, isSuperShow)); //直接通过SQL关联!!!
				billList_allDealTask.setTitleLabelText("待办任务，最新查询时间【" + tbUtil.getCurrTime() + "】"); //
				rightContentPanel.removeAll(); //
				rightContentPanel.setLayout(new BorderLayout()); //
				rightContentPanel.add(billList_allDealTask); //重新加入!!
				dealTaskList();
				rightContentPanel.updateUI(); //
			} else if (str_nodeType.equals("2")) { //所有已办任务!
				if (billList_allOffTask == null) { //如果为空,则创建之!!
					billList_allOffTask = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_task_off_CODE1.xml")); //已办任务的列表
					billList_allOffTask.setQuickQueryPanelVisiable(false);//现在模板导出xml，sql中就查询不到记录，故隐藏查询面板【李春娟/2016-06-04】
					//					billList_allOffTask.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
					//						public void actionPerformed(ActionEvent e) {
					//							onAllOffTaskQuickQuery((BillQueryPanel) e.getSource()); //
					//						}
					//					});
				}
				billList_allOffTask.queryDataByDS(null, getAllOffTaskSQL(null, isSuperShow)); //直接通过SQL关联!!!
				offTaskList();
				rightContentPanel.removeAll(); //
				rightContentPanel.setLayout(new BorderLayout()); //
				rightContentPanel.add(billList_allOffTask); //重新加入!!
				rightContentPanel.updateUI(); //
			} else if (str_nodeType.equals("3") || str_nodeType.equals("4")) { //某个具体业务的待办任务/已办任务!及明细!
				openBDTaskDetail(hvo); //
			} else if (str_nodeType.equals("5") || str_nodeType.equals("6") || str_nodeType.equals("7") || str_nodeType.equals("8")) { //5-未读;6-已读;7-已发;8-消息的根结点
				openMsgCenter(str_nodeType);
			} else if (str_nodeType.equals("9")) { //意见补登的目录结点!
				openBDTask();
			} else if (str_nodeType.equals("10")) { //意见补登的明细!
				openBDTaskDetail(hvo);
			} else if (str_nodeType.equals("100") || str_nodeType.equals("101") || str_nodeType.equals("102") || str_nodeType.equals("103")) { //传阅 【杨科/2012-12-14】
				return;
			} else if (str_nodeType.equals("110") || str_nodeType.equals("111") || str_nodeType.equals("112") || str_nodeType.equals("113") || str_nodeType.equals("114") || str_nodeType.equals("115")) { //传阅 【杨科/2012-12-14】
				dealPR(hvo);
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	//传阅处理树节点 【杨科/2012-12-14】
	private void addItemNode(HashMap _returnMap, DefaultMutableTreeNode _parentNode, String _nodeType) {
		String[] str_keys = (String[]) _returnMap.keySet().toArray(new String[0]); //得到所有的key
		int li_allCount = 0;
		HashVO[] allItemVOs = new HashVO[str_keys.length];
		for (int i = 0; i < str_keys.length; i++) {
			String str_templetCode = str_keys[i];
			String[] str_values = (String[]) _returnMap.get(str_keys[i]);
			String str_templetName = str_values[0];
			String str_count = str_values[1];
			allItemVOs[i] = new HashVO();
			allItemVOs[i].setAttributeValue("$NodeType", _nodeType); //待办任务之某模板!
			allItemVOs[i].setAttributeValue("templetcode", str_templetCode); //模板编码!
			allItemVOs[i].setAttributeValue("templetname", str_templetName); //模板名称
			allItemVOs[i].setAttributeValue("text", (str_templetName == null ? str_templetCode : str_templetName)); //显示的名称
			allItemVOs[i].setAttributeValue("count", str_count);
			li_allCount = li_allCount + Integer.parseInt(str_count);
		}

		//为了顺序对所有任务进行排序!!!以后可能 会按指定的顺序排序!!!
		tbUtil.sortHashVOs(allItemVOs, new String[][] { { "templetname", "N", "N" } });

		//邮储项目中有个需求,就是觉得待办任务中的子结点太多,想再加一层,使用一级目录模块!作为分类!
		//这有很多种方法,但最后想还是使用一个全局参数,最简洁!!
		String str_df = TBUtil.getTBUtil().getSysOptionStringValue("工作流中心树型目录分类定义", null);
		HashMap map = null;
		String[] str_dfdirkeys = null;
		if (str_df != null && !str_df.trim().equals("")) {
			map = new HashMap();
			str_df = TBUtil.getTBUtil().replaceAll(str_df, " ", "");
			str_df = TBUtil.getTBUtil().replaceAll(str_df, "\r", "");
			str_df = TBUtil.getTBUtil().replaceAll(str_df, "\n", "");
			HashMap initMap = TBUtil.getTBUtil().convertStrToMapByExpress(str_df, ";", "=");
			str_dfdirkeys = (String[]) initMap.keySet().toArray(new String[0]); //所有定义的目录
			for (int i = 0; i < str_dfdirkeys.length; i++) {
				String str_models = (String) initMap.get(str_dfdirkeys[i]);
				String[] str_modelArray = TBUtil.getTBUtil().split(str_models, ",");
				for (int j = 0; j < str_modelArray.length; j++) {
					map.put(str_modelArray[j], str_dfdirkeys[i]);
				}
			}
		}

		HashMap alreadyAddedDirNodeMap = new HashMap(); //用来记录曾经创建过的目录结点!!!
		ArrayList al_onlyLeafNode = new ArrayList();
		for (int i = 0; i < allItemVOs.length; i++) {
			String str_thisNodeText = allItemVOs[i].toString();
			if (map != null && map.containsKey(str_thisNodeText)) { //如果发现我被指定了一个目录,则先创建目录!
				String str_dirName = (String) map.get(str_thisNodeText);
				if (!alreadyAddedDirNodeMap.containsKey(str_dirName)) { //如果还没登记,则先登录一下,即相同的目录只算一个!
					HashVO modelDirVO = new HashVO();
					modelDirVO.setAttributeValue("$NodeType", "100"); //是目录结点
					modelDirVO.setAttributeValue("text", str_dirName);
					modelDirVO.setAttributeValue("count", "0");
					modelDirVO.setToStringFieldName("text");
					DefaultMutableTreeNode addDirNode = new DefaultMutableTreeNode(modelDirVO);
					alreadyAddedDirNodeMap.put(str_dirName, addDirNode);
				}

				DefaultMutableTreeNode dirNode = (DefaultMutableTreeNode) alreadyAddedDirNodeMap.get(str_dirName); //目录结点!
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(allItemVOs[i]); //本结点
				dirNode.add(node); //目录结点下面加上本结点
			} else {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(allItemVOs[i]);
				al_onlyLeafNode.add(node);
			}
		}

		//加入有目录的结点,必须按指定的先后顺序排序!
		String[] str_realdirs = (String[]) alreadyAddedDirNodeMap.keySet().toArray(new String[0]);
		if (str_realdirs != null && str_realdirs.length > 0) {
			TBUtil.getTBUtil().sortStrsByOrders(str_realdirs, str_dfdirkeys);
			for (int i = 0; i < str_realdirs.length; i++) {
				DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) alreadyAddedDirNodeMap.get(str_realdirs[i]);
				_parentNode.add(tmpNode); //将目录结点加入!!
			}
		}

		//加入那些没有目录的结点!
		for (int i = 0; i < al_onlyLeafNode.size(); i++) {
			DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) al_onlyLeafNode.get(i);
			_parentNode.add(tmpNode); //将目录结点加入!!
		}

		HashVO hvo = (HashVO) _parentNode.getUserObject();
		hvo.setAttributeValue("count", "" + li_allCount); //设置父亲结点的总数
	}

	//传阅节点事件 【杨科/2012-12-14】
	private void dealPR(HashVO hvo) {
		billListPanel = new BillListPanel("PUB_MESSAGE_PASSREAD");
		billListPanel.addBillListHtmlHrefListener(new BillListHtmlHrefListener() {

			public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
				try {
					String messageHtmlClass = tbUtil.getSysOptionStringValue("收藏夹传阅红头文件关联类", "");
					MessageShowHtml messageShowHtml = (MessageShowHtml) Class.forName(messageHtmlClass).newInstance();
					String templetcode = billListPanel.getSelectedBillVO().getStringValue("templetcode");
					String pvalue = billListPanel.getSelectedBillVO().getStringValue("pvalue");
					if (messageShowHtml.onBillListHtml(billListPanel, pvalue, templetcode)) {
						selected();
						readed();
					}
				} catch (Exception e) {
					selected();
					readed();
				}
			}

		});

		select = WLTButton.createButtonByType(WLTButton.COMM, "浏览");
		select.addActionListener(this);
		billListPanel.addBillListButton(select);
		billListPanel.repaintBillListButton();

		String str_nodeType = hvo.getStringValue("$NodeType");
		String templetcode = hvo.getStringValue("templetcode");

		String[] keys = { "普通待阅消息", "普通已阅消息", "普通传阅消息", "工作流待阅消息", "工作流已阅消息", "工作流传阅消息" };
		String[] msgtypes = { "普通传阅消息", "普通传阅消息", "普通传阅消息", "工作流传阅消息", "工作流传阅消息", "工作流传阅消息" };
		String[] sqls = { unreadsql, readedsql, sendsql, unreadsql, readedsql, sendsql };
		for (int i = 0; i < keys.length; i++) {
			if (str_nodeType.equals("11" + i)) {
				billListPanel.setTitleLabelText(keys[i]);
				if (templetcode == null && templetcode.equals("")) {
					billListPanel.QueryDataByCondition(" 1=2 ");
				} else {
					billListPanel.QueryDataByCondition(" templetcode='" + hvo.getStringValue("templetcode") + "' and msgtype='" + msgtypes[i] + "' and " + sqls[i]);
				}
			}
		}
		rightContentPanel.removeAll();
		rightContentPanel.setLayout(new BorderLayout());
		rightContentPanel.add(billListPanel);
		rightContentPanel.updateUI();
	}

	//传阅列表表单浏览 【杨科/2012-12-14】
	private void selected() {
		String id = billListPanel.getSelectedBillVO().getStringValue("id");
		String templetcode = billListPanel.getSelectedBillVO().getStringValue("templetcode");
		String pkey = billListPanel.getSelectedBillVO().getStringValue("pkey");
		String pvalue = billListPanel.getSelectedBillVO().getStringValue("pvalue");

		BillCardPanel billCardPanel = new BillCardPanel(templetcode);
		Pub_Templet_1VO templetVO = billCardPanel.getTempletVO();
		templetVO.setDataconstraint("");
		billCardPanel.setTempletVO(templetVO);
		billCardPanel.queryDataByCondition(pkey + "=" + pvalue);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFocusable(false);

		String title = billListPanel.getTitleLabel().getText();
		if (title.equals("※工作流待阅消息") || title.equals("※工作流已阅消息") || title.equals("※工作流传阅消息")) {
			String taskid = billListPanel.getSelectedBillVO().getStringValue("taskid");
			String dealpoolid = billListPanel.getSelectedBillVO().getStringValue("dealpoolid");
			String prinstanceid = billListPanel.getSelectedBillVO().getStringValue("prinstanceid");

			WorkFlowProcessFrame processFrame = new WorkFlowProcessFrame(this, "", billCardPanel, null, taskid, dealpoolid, prinstanceid, true, "现在是浏览数据状态,所以不能进行提交等操作。");
			processFrame.getWfProcessPanel().getBtn_Confirm().setVisible(false); //隐藏确认按钮
			processFrame.getWfProcessPanel().getBtn_calcel().setVisible(false); //隐藏取消按钮
			processFrame.getWfProcessPanel().getBtn_mind().setVisible(false); //隐藏催督办按钮
			//processFrame.setVisible(true); 
			//processFrame.toFront();	

			tabbedPane.addTab("消息内容", processFrame.getWfProcessPanel());
		} else {
			tabbedPane.addTab("消息内容", billCardPanel);
		}

		BillCardPanel passReadPanel = new BillCardPanel("PUB_MESSAGE_BTN");
		passReadPanel.setEditable("opinion", true);
		CardCPanel_ChildTable opinionListPanel = (CardCPanel_ChildTable) passReadPanel.getCompentByKey("opinion");
		opinionListPanel.getBtn_edit().setVisible(false);
		opinionListPanel.getBtn_delete().setVisible(false);
		passReadPanel.queryDataByCondition(" id =" + id);
		tabbedPane.addTab("传阅信息", passReadPanel);

		BillDialog dialog = new BillDialog(billListPanel, "消息内容", 800, 600);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(tabbedPane, "Center");
		dialog.setVisible(true);
	}

	//传阅处理已读 【杨科/2012-12-14】
	private void readed() {
		String title = billListPanel.getTitleLabel().getText();
		if (title.equals("※普通待阅消息") || title.equals("※工作流待阅消息")) {
			String msgid = billListPanel.getSelectedBillVO().getStringValue("id");
			String msgtitle = billListPanel.getSelectedBillVO().getStringValue("msgtitle");
			String sender = billListPanel.getSelectedBillVO().getStringValue("sender");
			String senddate = billListPanel.getSelectedBillVO().getStringValue("senddate");
			String templetcode = billListPanel.getSelectedBillVO().getStringValue("templetcode");

			try {
				String newid = UIUtil.getSequenceNextValByDS(null, "s_pub_message_readed");
				UIUtil.executeUpdateByDS(null, new InsertSQLBuilder("pub_message_readed", new String[][] { { "id", newid }, { "msgid", msgid }, { "msgtitle", msgtitle }, { "sender", sender }, { "senddate", senddate }, { "userid", ClientEnvironment.getCurrLoginUserVO().getId() }, { "readtime", UIUtil.getCurrTime() }, { "isdelete", "N" } }).getSQL());
			} catch (Exception e) {
				e.printStackTrace();
			}
			readRefresh(title, templetcode);
		}
	}

	//传阅已读刷新 【杨科/2012-12-14】
	private void readRefresh(String title, String templetcode) {
		unreadPRNode.removeAllChildren();
		readedPRNode.removeAllChildren();
		sendPRNode.removeAllChildren();
		unreadWFNode.removeAllChildren();
		readedWFNode.removeAllChildren();
		sendWFNode.removeAllChildren();

		loadTaskNode(); //加载所有数据..

		if (title.equals("※普通待阅消息")) {
			expandAll(classTree, new TreePath(rootNode), false); //全部收缩
			expandAll(classTree, new TreePath(generalNode.getPath()), true); //再将普通传阅展开!
		} else {
			expandAll(classTree, new TreePath(rootNode), false); //全部收缩
			expandAll(classTree, new TreePath(workFlowNode.getPath()), false); //再将工作流传阅展开!			
		}
		updateJtree(); //必须要做一下,否则页面刷新不过来!!!
		if (title.equals("※普通待阅消息")) {
			setSelectNode(classTree, unreadPRNode);
			billListPanel.QueryDataByCondition(" templetcode='" + templetcode + "' and msgtype='普通传阅消息' and " + unreadsql);
		} else {
			setSelectNode(classTree, unreadWFNode);
			billListPanel.QueryDataByCondition(" templetcode='" + templetcode + "' and msgtype='工作流传阅消息' and " + unreadsql);
		}
		rightContentPanel.removeAll();
		rightContentPanel.setLayout(new BorderLayout());
		rightContentPanel.add(billListPanel);
		rightContentPanel.updateUI(); //必须要做一下!!
	}

	//点击查询所有待办任务
	protected void onAllOffTaskQuickQuery(BillQueryPanel _billQueryPanel) {
		String str_condition = _billQueryPanel.getQuerySQLCondition(); //取得查询条件!
		if (str_condition == null) {
			return;
		}
		String str_sql = getAllOffTaskSQL(str_condition, isSuperShow); //
		billList_allOffTask.queryDataByDS(null, str_sql); //
		offTaskList();
	}

	protected void onAllBDTaskQuickQuery(BillQueryPanel _billQueryPanel) {
		String str_condition = _billQueryPanel.getQuerySQLCondition();
		if (str_condition == null) {
			return;
		}
		String str_sql = getAllOffTaskSQL(str_condition, isSuperShow);
		billList_allBDTask.queryDataByDS(null, str_sql);
		bDTaskList();
	}

	/**
	 * 点击查询面板的逻辑!!
	 * @param _billQueryPanel
	 */
	protected void onQuickQuery(BillQueryPanel _billQueryPanel, String _noteType) {
		try {
			//这里获得的查询条件不应该有策略过滤，否则已办任务等就无法查询出来了，第四个参数_isUseDataAccess=false【李春娟/2017-08-07】
			String str_condition = _billQueryPanel.getQuerySQLCondition(_billQueryPanel.getAllQuickQueryCompents(), false, true, false, "t2", null); //  //取得查询条件!
			if (str_condition == null) {
				return;
			}
			String str_templetCode = _billQueryPanel.getBillListPanel().getTempletVO().getTempletcode(); //模板编码
			String str_tabName = _billQueryPanel.getBillListPanel().getTempletVO().getTablename(); //查询的表名!
			String str_savedtabName = _billQueryPanel.getBillListPanel().getTempletVO().getSavedtablename(); //保存的表名!
			String str_pkName = _billQueryPanel.getBillListPanel().getTempletVO().getPkname(); //主键字段名!
			String str_sql = null;
			if (_noteType.equals("3")) {
				str_sql = getWorkflowUIUtil().getDealTaskSQL(str_templetCode, str_tabName, str_savedtabName, str_pkName, ClientEnvironment.getCurrSessionVO().getLoginUserId(), str_condition, isSuperShow); //拼成待办任务SQL,因为这个SQL会重用,所以放在UIUtil中!
			} else if (_noteType.equals("4")) {
				str_sql = getWorkflowUIUtil().getOffTaskSQL(str_templetCode, str_tabName, str_savedtabName, str_pkName, ClientEnvironment.getCurrSessionVO().getLoginUserId(), str_condition, isSuperShow); //
			} else if (_noteType.equals("10")) {
				str_sql = getWorkflowUIUtil().getBDTaskSQL(str_templetCode, str_tabName, str_savedtabName, str_pkName, getBDInsql(), str_condition, isSuperShow);
			}
			_billQueryPanel.getBillListPanel().queryDataByDS(null, str_sql, true); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 已办任务查询与分页后都需要重置背景色..
	 */
	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		BillListPanel list = (BillListPanel) _event.getSource(); //
		resetBackColor4(list); //
	}

	/**
	 * 重置待办任务中的背景色,因为待办任务有个特殊情况是多条记录是同一件事!混在一起看不清楚!
	 */
	private void resetBackColor4(BillListPanel billList) {
		String str_pk = "id"; //
		if (billList.getTempletVO() != null && billList.getTempletVO().getPkname() != null) {
			str_pk = billList.getTempletVO().getPkname(); //
		}

		BillVO[] billVOS = billList.getAllBillVOs(); //
		Color[] cols = new Color[] { LookAndFeel.tablebgcolor, LookAndFeel.table_bgcolor_odd }; //new Color(236, 245, 255), new Color(240, 255, 240)
		int li_colcount = 0; //
		for (int i = 0; i < billVOS.length; i++) {
			String str_thisid = billVOS[i].getStringValue(str_pk, ""); //
			String str_lastid = null; //
			if (i != 0) {
				str_lastid = billVOS[i - 1].getStringValue(str_pk, ""); //
			}
			if (str_thisid.equals(str_lastid)) { //如果我与前面一样,则用原来的
				billList.setItemBackGroundColor(cols[li_colcount % cols.length], i); //

			} else { //如果不一样!!!
				li_colcount++; //
				billList.setItemBackGroundColor(cols[li_colcount % cols.length], i); //
			}
		}
	}

	private WorkflowUIUtil getWorkflowUIUtil() {
		if (wfUIUtil != null) {
			return wfUIUtil;
		}
		wfUIUtil = new WorkflowUIUtil(); //
		return wfUIUtil;
	}

	/**
	 * 取得所有待办任务!
	 * 以后还要关联流程实例表,以知道当前流程状态,当前流程到了哪一环节,当前到了哪个人那里!!
	 */
	private String getAllDealTaskSQL(String _appendCondition, boolean _isSuperShow) {
		String str_loginUserId = ClientEnvironment.getCurrSessionVO().getLoginUserId(); //
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select t1.*,");
		sb_sql.append("t2.templetname, ");
		sb_sql.append("t3.curractivityname task_curractivityname ");
		sb_sql.append("from pub_task_deal t1 ");
		sb_sql.append("left join pub_templet_1 t2 on t1.templetcode=t2.templetcode ");
		sb_sql.append("left join pub_wf_prinstance t3 on t1.rootinstanceid=t3.id ");
		sb_sql.append("where 1=1 "); //
		if (!_isSuperShow) { //如果不是超级查看,则根据登录人员过滤!! 而超级查询则显示所有!!
			sb_sql.append("and (t1.dealuser='" + str_loginUserId + "' or t1.accruserid='" + str_loginUserId + "') "); //
		}
		if (_appendCondition != null) {
			sb_sql.append(_appendCondition + " "); //
		}
		if (templetCode != null && !"".equals(templetCode)) {
			sb_sql.append(" and t1.templetcode='" + templetCode + "' "); //
		}
		sb_sql.append("order by t1.createtime desc,templetname"); //
		return sb_sql.toString(); //
	}

	/**
	 * 意见补登的sql
	 * @param _appendCondition
	 * @param _isSuperShow
	 * @return
	 */
	private String getAllBDTaskSQL(String _appendCondition, boolean _isSuperShow) {
		String str_loginUserId = ClientEnvironment.getCurrSessionVO().getLoginUserId();
		StringBuilder sb_sql = new StringBuilder();
		sb_sql.append("select t1.*,");
		sb_sql.append("t2.templetname, ");
		sb_sql.append("t3.curractivityname task_curractivityname ");
		sb_sql.append("from pub_task_deal t1 ");
		sb_sql.append("left join pub_templet_1 t2 on t1.templetcode=t2.templetcode ");
		sb_sql.append("left join pub_wf_prinstance t3 on t1.rootinstanceid=t3.id ");
		sb_sql.append("where 1=1 ");
		try {
			if (!_isSuperShow) {
				sb_sql.append("and (t1.dealuser in (" + getBDInsql() + ")) ");
			}
			if (_appendCondition != null) {
				sb_sql.append(_appendCondition + " ");
			}
			if (templetCode != null && !"".equals(templetCode)) {
				sb_sql.append(" and t1.templetcode='" + templetCode + "' ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			sb_sql.append("where 1=2 ");
		}
		sb_sql.append("order by t1.createtime desc,templetname");
		return sb_sql.toString(); //
	}

	/**
	 * 意见补登的SQL..
	 * @return
	 * @throws Exception
	 */
	public String getBDInsql() throws Exception {
		if (bdinsql == null) {
			HashMap map = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getUserIdsByDataPolicy", null);
			bdinsql = TBUtil.getTBUtil().getInCondition((String[]) map.get("AllUserIDs"));
		}
		return bdinsql;
	}

	/**
	 * 取得所有已办任务!
	 * 以后还要关联流程实例表,以知道当前流程状态,当前流程到了哪一环节,当前到了哪个人那里!!
	 */
	private String getAllOffTaskSQL(String _appendCondition, boolean _isSuperShow) {
		String str_loginUserId = ClientEnvironment.getCurrSessionVO().getLoginUserId(); //
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select ");
		sb_sql.append("t1.*,");
		sb_sql.append("t2.templetname, ");
		sb_sql.append("t3.curractivityname task_curractivityname ");
		sb_sql.append("from pub_task_off t1 ");
		sb_sql.append("left join pub_templet_1 t2 on t1.templetcode=t2.templetcode ");
		sb_sql.append("left join pub_wf_prinstance t3 on t1.rootinstanceid=t3.id ");

		//已办任务相同任务合并 【杨科/2013-03-08】
		if (tbUtil.getSysOptionBooleanValue("已办任务同一任务是否只显示最后一条", false)) {
			sb_sql.append(" inner join ( select pkvalue, max(dealtime) dealtime from pub_task_off where 1=1 ");
			if (!_isSuperShow) { //如果不是超级查看,则根据登录人员过滤!! 而超级查询则显示所有!!
				sb_sql.append(" and (realdealuser='" + str_loginUserId + "' or accruserid='" + str_loginUserId + "') "); //实际处理人与授权人都能看到!! order by t1.dealtime desc
			}
			if (templetCode != null && !"".equals(templetCode)) {
				sb_sql.append(" and templetcode='" + templetCode + "' "); //
			}
			sb_sql.append(" group by pkvalue ) t4 on t1.pkvalue=t4.pkvalue and t1.dealtime=t4.dealtime ");
		}

		sb_sql.append("where 1=1 "); //
		if (!_isSuperShow) { //如果不是超级查看,则根据登录人员过滤!! 而超级查询则显示所有!!
			sb_sql.append("and (t1.realdealuser='" + str_loginUserId + "' or t1.accruserid='" + str_loginUserId + "') "); //实际处理人与授权人都能看到!! order by t1.dealtime desc
		}
		if (_appendCondition != null) {
			sb_sql.append(_appendCondition + " "); //
		}
		if (templetCode != null && !"".equals(templetCode)) {
			sb_sql.append(" and t1.templetcode='" + templetCode + "' "); //
		}
		sb_sql.append(" order by t1.dealtime desc,templetname"); //
		return sb_sql.toString(); //
	}

	/**
	 * 展开整个树!!
	 * @param tree
	 * @param _treePath
	 */
	private void expandAll(JTree tree, TreePath _treePath, boolean _isExpand) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) _treePath.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = _treePath.pathByAddingChild(n); //取得子结点的路径
				expandAll(tree, path, _isExpand);
			}
		}
		if (_isExpand) {
			tree.expandPath(_treePath);
		} else {
			if (!node.isRoot()) {
				tree.collapsePath(_treePath);
			}
		}
	}

	/**
	 * 刷新
	 */
	private void updateJtree() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				classTree.setUI(new WLTTreeUI(true));
				classTree.setCellRenderer(new MyTreeCellRender());
				classTree.validate();
				classTree.repaint();
			}
		});
	}

	/**
	 * 选中树中的某一个结点!
	 * @param _tree
	 * @param _node
	 */
	private void setSelectNode(JTree _tree, TreeNode _node) {
		if (_node == null) {
			return; ////
		}
		DefaultTreeModel model = (DefaultTreeModel) _tree.getModel();
		TreeNode[] nodes = model.getPathToRoot(_node);
		TreePath path = new TreePath(nodes); //
		_tree.setSelectionPath(path); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == refreshBtn) {
			onRefresh(); //
		} else if (e.getSource() == superShowBtn) { //
			onSuperShow(); //显示超级界面,即不做任何权限过滤的,显示所有任务的界面!!
		} else if (e.getSource() == menuItem_1) {
			onShowRubbishData(); //显示垃圾数据!!
		} else if (e.getSource() == menuItem_2) {
			onDeleteRubbishData(); //
		} else if (e.getSource() == menuItem_3) {
			onShowNodeInfo(); //显示结点自身信息!
		} else if (e.getSource() == select) {
			if (billListPanel.getSelectedBillVO() == null) {
				MessageBox.show(billListPanel, "请选择需要浏览的消息!");
				return;
			} else {
				selected();
				readed();
			}
		}
	}

	/**
	 * 显示结点自身信息,在调试与监控时特别有用!
	 */
	private void onShowNodeInfo() {
		TreePath selectedPath = this.classTree.getSelectionPath(); //
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent(); //选中的结点!!
		HashVO hvo = (HashVO) selNode.getUserObject(); //
		String[] str_keys = hvo.getKeys(); //
		StringBuilder sb_info = new StringBuilder(); //
		for (int i = 0; i < str_keys.length; i++) {
			sb_info.append("[" + str_keys[i] + "]=[" + hvo.getStringValue(str_keys[i], "") + "]\r\n"); //
		}
		MessageBox.show(classTree, sb_info.toString()); //
	}

	/**
	 * 流程处理后监听!!
	 */
	public void onDealWorkFlow(WorkFlowDealEvent _event) {
		if (_event.getDealType() == 1) {
			onRefresh(false); //
		}
	}

	class MyTreeCellRender extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 5500760588674948602L;
		private Icon icon_dir1 = UIUtil.getImage("office_200.gif");
		private Icon icon_dir2 = UIUtil.getImage("office_169.gif");
		private Icon icon1 = UIUtil.getImage("office_026.gif"); //待办任务
		private Icon icon2 = UIUtil.getImage("office_034.gif"); //已办任务
		private Icon icon_dir5 = UIUtil.getImage("zt_062.gif"); //未读
		private Icon icon_dir6 = UIUtil.getImage("zt_063.gif"); //已读
		private Icon icon_dir7 = UIUtil.getImage("office_039.gif"); //未读
		private Icon icon_modeldir = UIUtil.getImage("office_151.gif"); //邮储项目中想将菜单目录作为第二层!
		private Icon icon_dir8 = UIUtil.getImage("bug_1.png");
		private Icon icon_dir9 = UIUtil.getImage("office_039.gif");//意见补登

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			HashVO hvo = (HashVO) ((DefaultMutableTreeNode) value).getUserObject(); //
			int li_type = hvo.getIntegerValue("$NodeType"); //
			String str_text = hvo.getStringValue("text"); //
			int li_count = hvo.getIntegerValue("count"); //
			if (li_count > 0) {
				str_text = str_text + "(" + li_count + ")";
			}
			JLabel label = new JLabel(str_text); //

			//设置图标!
			if (li_type == 0) {
				label.setIcon(this.getDefaultOpenIcon()); //
			} else if (li_type == 1) { //如果是目录
				if (expanded) {
					label.setIcon(icon_dir1); //
				} else {
					label.setIcon(this.getDefaultClosedIcon()); //
				}
			} else if (li_type == 2) { //如果是目录
				if (expanded) {
					label.setIcon(icon_dir2); //
				} else {
					label.setIcon(this.getDefaultClosedIcon()); //
				}
			} else if (li_type == 3) { //如果待办的项目结点
				label.setIcon(icon1); //
			} else if (li_type == 4) { //如果是已办的项目结点
				label.setIcon(icon2); //
			} else if (li_type == 5) {
				label.setIcon(icon_dir5);
			} else if (li_type == 6) {
				label.setIcon(icon_dir6);
			} else if (li_type == 7) {
				label.setIcon(icon_dir7);
			} else if (li_type == -1) {
				label.setIcon(icon_modeldir);
			} else if (li_type == 8) {//消息中心帽子
				if (expanded) {
					label.setIcon(icon_dir8);
				} else {
					label.setIcon(this.getDefaultClosedIcon()); //
				}
			} else if (li_type == 9) {//意见补登
				if (expanded) {
					label.setIcon(icon_dir9);
				} else {
					label.setIcon(this.getDefaultClosedIcon()); //
				}
			} else if (li_type == 10) {
				label.setIcon(icon1);
			} else if (li_type == 101) { //以下传阅 【杨科/2012-12-14】
				if (expanded) {
					label.setIcon(icon_dir2);
				} else {
					label.setIcon(icon_dir5);
				}
			} else if (li_type == 102) {
				if (expanded) {
					label.setIcon(icon_dir2);
				} else {
					label.setIcon(icon_dir6);
				}
			} else if (li_type == 103) {
				if (expanded) {
					label.setIcon(icon_dir2);
				} else {
					label.setIcon(icon_dir7);
				}
			} else {
				label.setIcon(this.getDefaultClosedIcon());
			}

			//设置字体颜色!
			if (sel) {
				label.setOpaque(true); //如果选中的话,则不透明..
				label.setForeground(Color.RED); //
				label.setBackground(Color.YELLOW); //
			} else {
				label.setOpaque(false); //透明!
			}
			return label;
		}
	}

}
