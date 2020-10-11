package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SystemOptionsDialog;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPopupButton;
import cn.com.infostrategy.ui.common.WLTTreeUI;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

/**
 * 工作流流程监控的界面!! 非常关键的页面!! 也是人性化要求非常高的界面!!!
 * @author Administrator
 *
 */
public class WorkflowMonitorDialog extends BillDialog implements BillListSelectListener, TreeSelectionListener, BillListHtmlHrefListener, ActionListener {

	private static final long serialVersionUID = 4894976596858838094L;

	private String str_wfinstance = null;
	private String str_rootInstanceId = null; //
	private BillVO billVO = null; // 单据中的数据
	private WLTPopupButton btn_exporthtmlreport = null; // 导出html报表.
	private WorkFlowDesignWPanel graphPanel = null;

	private JSplitPane splitPanel_main = null;
	private JTree dealPoolTree = null, dealPoolTree2 = null; //
	private BillListPanel dealPoolBillList = null; //
	private JPopupMenu popMenu, popMenu2, confMenu = null; //

	//private HashMap map_par = null;
	private boolean ifJiaMi = true;//默认加密??

	private TBUtil tbUtil = new TBUtil(); //
	private WorkflowUIUtil wfUIUtil = new WorkflowUIUtil(); //
	private VectorMap vm_allReport = null;

	private WLTButton btn_confirm, btn_showWFInfo, btn_exportWFReport, btn_wfOptions;

	private boolean isTreeSelectChanging = false, isTableSelectChanging = false; //

	private String processStatus = null; //流程当前状态

	public WorkflowMonitorDialog() {
	}

	/**
	 * 构造方法
	 * 
	 * @param _parent
	 * @param _wfinstance
	 * @param _billVO
	 */
	public WorkflowMonitorDialog(Container _parent, String _wfinstance, BillVO _billVO) {
		super(_parent, "流程执行情况监控", 1020, 740);
		this.setLocation(0, 0); //
		this.str_wfinstance = _wfinstance;
		this.billVO = _billVO; //
		try {
			initialize();
		} catch (Exception ex) {
			ex.printStackTrace();
			this.getContentPane().removeAll(); //
			this.getContentPane().setLayout(new BorderLayout()); //
			this.getContentPane().add(new JLabel(ex.getMessage())); //
			this.setSize(550, 100); //
			this.locationToCenterPosition(); //
		}
	}

	public WorkflowMonitorDialog(Container _parent, String _wfinstance, BillVO _billVO, boolean ifjiami) {
		super(_parent, "流程执行情况监控", 1020, 740);
		this.setLocation(0, 0); //
		this.str_wfinstance = _wfinstance;
		this.ifJiaMi = ifjiami;
		this.billVO = _billVO; //
		try {
			initialize();
		} catch (Exception ex) {
			ex.printStackTrace();
			this.getContentPane().removeAll(); //
			this.getContentPane().setLayout(new BorderLayout()); //
			this.getContentPane().add(new JLabel(ex.getMessage())); //
			this.setSize(550, 100); //
			this.locationToCenterPosition(); //
		}
	}

	public WorkflowMonitorDialog(Container _parent, String _wfinstance, BillVO _billVO, boolean ifjiami, String _witchBox) {
		super(_parent, "流程执行情况监控", 1020, 740);
		this.setLocation(0, 0); //
		this.str_wfinstance = _wfinstance;
		this.ifJiaMi = ifjiami;
		this.billVO = _billVO; //
		try {
			initialize();
		} catch (Exception ex) {
			ex.printStackTrace();
			this.getContentPane().removeAll(); //
			this.getContentPane().setLayout(new BorderLayout()); //
			this.getContentPane().add(new JLabel(ex.getMessage())); //
			this.setSize(550, 100); //
			this.locationToCenterPosition(); //
		}
	}

	private void initialize() throws Exception {
		this.setLayout(new BorderLayout()); //
		if (!tbUtil.getSysOptionBooleanValue("工作流流程监控是否默认隐藏导出报表", false)) { //sunfujun/20120829/邮储很纠结这个认为藏的很深
			JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
			getWLTPopupButton();
			if (btn_exporthtmlreport != null) {
				panel_north.add(btn_exporthtmlreport);
			}
			this.add(panel_north, BorderLayout.NORTH); //
		}

		//先找出根流程!!!
		HashVO instanceVO[] = UIUtil.getHashVoArrayByDS(null, "select rootinstanceid,status from pub_wf_prinstance where id='" + this.str_wfinstance + "'"); //
		if (instanceVO != null && instanceVO.length > 0) {
			str_rootInstanceId = instanceVO[0].getStringValue("rootinstanceid");
			processStatus = instanceVO[0].getStringValue("status");
		}
		if (str_rootInstanceId == null || str_rootInstanceId.trim().equals("")) { //考虑到旧的机制,可能根据流程实例id为空!
			str_rootInstanceId = str_wfinstance; //
		}
		//查询出数据,可能会有提交意见加密处理!
		if (tbUtil.getSysOptionBooleanValue("工作流流程监控是否默认流程图显示在上面", true)) {
			splitPanel_main = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getGraphPanel(), getDealPoolPanel());
		} else {
			splitPanel_main = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getDealPoolPanel(), getGraphPanel());
		}
		splitPanel_main.setDividerSize(10);
		splitPanel_main.setOneTouchExpandable(true); //
		if (tbUtil.getSysOptionBooleanValue("工作流流程监控是否默认隐藏流程图", false)) {
			splitPanel_main.setDividerLocation(0);
			splitPanel_main.setLastDividerLocation(350);
		} else {
			splitPanel_main.setDividerLocation(350);
		}
		this.add(splitPanel_main, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //

		autoScrolltoNode(dealPoolTree2); //自动查找并锁定第二个树

		this.locationToCenterPosition();
	}

	private void autoScrolltoNode(JTree _tree) {
		//自动选中属于我的待办任务的结点,如果有的话!! 因为在子流程中需要知道我属于什么流程!!
		if (_tree != null) {
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) _tree.getModel().getRoot(); //
			DefaultMutableTreeNode[] allNodes = getAllChildrenNodes(rootNode, false); //
			String str_loginUserid = ClientEnvironment.getInstance().getLoginUserID(); //登录人员id
			boolean isFinded = false; //

			DefaultMutableTreeNode notSubMitNode = null; //
			for (int i = 0; i < allNodes.length; i++) {
				Object obj = allNodes[i].getUserObject(); //
				if (obj instanceof HashVO) {
					HashVO hvo_item = (HashVO) obj; //
					String str_dealuserid = hvo_item.getStringValue("participant_user"); //
					String str_issubmit = hvo_item.getStringValue("issubmit"); //
					if (str_issubmit == null || str_issubmit.equals("N")) {
						notSubMitNode = allNodes[i]; //
					}

					if (str_dealuserid != null && str_dealuserid.equals(str_loginUserid) && !"Y".equals(str_issubmit)) { //如果待办人等于当前人!
						_tree.setSelectionPath(new TreePath(allNodes[i].getPath())); //选中所有结点!这个动作会自动展开这个路径!!
						isFinded = true; //
						break; //
					}
				}
			}

			if (!isFinded) { //如果没找到我的任务,则自动光亮显示根流程!!
				if (notSubMitNode != null) { //
					_tree.setSelectionPath(new TreePath(notSubMitNode.getPath())); //选中所有结点!这个动作会自动展开这个路径!!
				}
				reLoadGraphFromTree(_tree); //光亮图中的环节
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			this.setCloseType(1); //
			this.dispose(); //
		} else if (e.getSource() == btn_showWFInfo) { //显示流程信息
			graphPanel.exportConf(); //导出流程信息
		} else if (e.getSource() == btn_exportWFReport) { //导出流程报表
			HashMap map_par = new HashMap();
			map_par.put("showType", "1");
			onExportHtmlReport(map_par); //
		} else if (e.getSource() == btn_wfOptions) {
			BillDialog dialog = new SystemOptionsDialog(this, "平台_工作流", new String[] { "工作流流程监控是否默认隐藏导出报表", "工作流流程监控是否默认流程图显示在上面", //
					"工作流流程监控是否默认隐藏流程图", "工作流流程监控是否默认隐藏树形处理记录", "工作流处理意见列表中的人员是否显示工号", //
					"工作处理意见列表各列顺序", "工作处理是否支持上传附件", "工作流查看意见时是否显示加密解密按钮", "工作流子流程结束时的提示语" }); //
			dialog.setVisible(true); //
		}
	}

	//下面的按钮面板栏
	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定"); //
		btn_showWFInfo = new WLTButton("查看流程配置"); //
		btn_showWFInfo.setToolTipText("如果选中某个环节或连线则自动选中其记录"); //
		btn_exportWFReport = new WLTButton("导出流程意见"); //
		btn_exportWFReport.setToolTipText("使用平台标准的导出流程报表,需要设置卡片与流程图中的是否导出"); //

		btn_wfOptions = new WLTButton("工作流系统参数"); //
		btn_wfOptions.setToolTipText("把工作流相关的系统参数都集中列出来,然后可以修改"); //

		btn_confirm.addActionListener(this); //
		btn_showWFInfo.addActionListener(this); //
		btn_exportWFReport.addActionListener(this); //
		btn_wfOptions.addActionListener(this); //

		panel.add(btn_confirm); //加入按钮
		if (ClientEnvironment.isAdmin()) {
			panel.add(btn_showWFInfo); //加入按钮
			panel.add(btn_exportWFReport); //加入按钮
			panel.add(btn_wfOptions); //加入按钮
		}
		return panel; //
	}

	//处理历史意见与两颗树的面板!
	public JPanel getDealPoolPanel() {
		JPanel dealpoolPanel = new JPanel(new BorderLayout());
		try {
			WFHistListPanelBuilder wfHistBuilder = new WFHistListPanelBuilder(str_rootInstanceId, billVO, false); //
			HashVO[] hvs = wfHistBuilder.getHashVODatas(); //所有数据!!!
			JTree tree1 = getDealPoolTree(hvs); //第一颗树!
			JTree tree2 = getDealPoolTree2(hvs); //第二颗树!

			dealPoolBillList = wfHistBuilder.getBillListPanel(); //
			dealPoolBillList.getTempletVO().setTablename(this.billVO.getQueryTableName()); //
			dealPoolBillList.getTempletVO().setSavedtablename(this.billVO.getQueryTableName()); //
			dealPoolBillList.addBillListSelectListener(this); //
			dealPoolBillList.addBillListHtmlHrefListener(this); //

			if (tbUtil.getSysOptionBooleanValue("工作流流程监控是否默认隐藏树形处理记录", false) && !ClientEnvironment.isAdmin()) { //
				dealpoolPanel.add(dealPoolBillList); //创建列表中的数据!!!
			} else {
				//这里可能有客户要求只要一个树,或者默认显示哪个树
				JTabbedPane tabb = new JTabbedPane(); //
				JPanel panel_1 = WLTPanel.createDefaultPanel(new BorderLayout()); //
				JLabel label_1 = new JLabel("※按照提交者之间的关系链路展示"); //
				label_1.setToolTipText("<html>能清晰的看到发送者与接收者之间的关系!<br>尤其是同时提交给多个人会签时,能区分出各个分支!<br>这就与发邮件一样,发件人是谁?收件人有几个?收发关系绝对清晰!</html>"); //
				label_1.setPreferredSize(new Dimension(-1, 22)); //
				label_1.setForeground(Color.RED); //

				panel_1.add(label_1, BorderLayout.NORTH); //
				panel_1.add(new JScrollPane(tree1), BorderLayout.CENTER);
				tabb.addTab("人员关系", UIUtil.getImage("office_050.gif"), panel_1); //

				JPanel panel_2 = WLTPanel.createDefaultPanel(new BorderLayout()); //
				JLabel label_2 = new JLabel("※按照机构与环节的流转顺序展示"); //
				label_2.setToolTipText("<html>先列出各个机构与环节(部门),再列出内部人员<br>但会签环节不能区分人员之间的接收关系及隶属不同分支!</html>"); //
				label_2.setPreferredSize(new Dimension(-1, 22)); //
				label_2.setForeground(Color.RED); //

				panel_2.add(label_2, BorderLayout.NORTH); //
				panel_2.add(new JScrollPane(tree2), BorderLayout.CENTER);
				tabb.addTab("机构关系", UIUtil.getImage("office_123.gif"), panel_2); //
				tabb.setSelectedIndex(1); //

				JSplitPane splitPanel_tree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dealPoolBillList, tabb); //
				splitPanel_tree.setDividerLocation(780); //
				splitPanel_tree.setOneTouchExpandable(true);
				//splitPanel_tree.setDividerLocation(0);  //
				dealpoolPanel.add(splitPanel_tree);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dealpoolPanel;
	}

	//
	public WLTPopupButton getWLTPopupButton() {
		final VectorMap allReport = getAllReport();
		if (allReport != null || allReport.size() > 0) { //可能有多种方式导出,每种的导出
			JPopupMenu popup = new JPopupMenu("PopupMenu");
			for (int i = 0; i < allReport.size(); i++) {
				final HashMap param = (HashMap) allReport.get(i);
				JMenuItem menuItem = new JMenuItem((String) param.get("报表名称"));
				menuItem.setPreferredSize(new Dimension(115, 19));
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onExportHtmlReport(param); // 导出Html报表
					}
				});
				popup.add(menuItem);
			}
			btn_exporthtmlreport = new WLTPopupButton(WLTPopupButton.TYPE_WITH_RIGHT_TOGGLE, UIUtil.getLanguage("导出报表"), null, popup); //
			btn_exporthtmlreport.setPreferredSize(new Dimension(120, 20));

			if (allReport.containsKey("全部审批意见")) {//兼容
				btn_exporthtmlreport.getButton().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						HashMap map_par = new HashMap();
						map_par.put("showType", "1");
						map_par.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
						map_par.put("html文件名", ((HashMap) allReport.get("全部审批意见")).get("html文件名"));
						map_par.put("word文件名", ((HashMap) allReport.get("全部审批意见")).get("word文件名"));
						onExportHtmlReport(map_par); // 导出Html报表
					}
				});
			}
			btn_exporthtmlreport.getButton().setPreferredSize(new Dimension(150, 20));
			btn_exporthtmlreport.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						onShowMsg(); //
					}
				}
			}); //
			return btn_exporthtmlreport; //
		}
		return null;
	}

	public void setSelectedNode(String _id) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) dealPoolTree.getModel().getRoot(); //
		DefaultMutableTreeNode[] allNodes = getAllChildrenNodes(rootNode, false); //
		for (int i = 0; i < allNodes.length; i++) {
			HashVO hvo_item = (HashVO) allNodes[i].getUserObject(); //
			String str_id = hvo_item.getStringValue("id"); //
			if (str_id != null && str_id.equals(_id)) { //如果待办人等 于当前人!
				dealPoolTree.setSelectionPath(new TreePath(allNodes[i].getPath())); //选中所有结点!这个动作会自动展开这个路径!!
			}
		}
	}

	/**
	 * 取得工作流图!
	 * @return
	 * @throws Exception
	 */
	private WorkFlowDesignWPanel getGraphPanel() throws Exception {
		if (graphPanel != null) {
			return graphPanel;
		}
		String str_processcode = null; //
		if (str_wfinstance != null && !str_wfinstance.equals("")) {
			String str_sql = "select t2.code from pub_wf_prinstance t1,pub_wf_process t2 where t1.processid=t2.id and t1.id='" + this.str_rootInstanceId + "'"; //找出根流程图!!
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql); //
			if (hvs == null || hvs.length <= 0) {
				throw new WLTAppException("根据流程实例ID[" + this.str_rootInstanceId + "]没有找到对应的工作流定义,可能是测试数据造成的,请与管理员联系!"); //
			}
			str_processcode = hvs[0].getStringValue("code"); //
		}
		graphPanel = new WorkFlowDesignWPanel(str_processcode, false); //
		graphPanel.setToolBarVisiable(false);//设置隐藏工具条
		graphPanel.lockGroupAndOnlyDoSelect(); //锁定,且只能做选择操作
		return graphPanel;
	}

	/**
	 * 取得处理历史的树型结构!!!非常关键!!
	 * @return
	 */
	private JTree getDealPoolTree(HashVO[] hvs) throws Exception {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("根结点"); //
		HashMap map_node = new HashMap(); //
		DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[hvs.length]; //

		ArrayList al_childWFNode = new ArrayList(); //记录所有子流程的列表,因为想默认收缩掉这些结点!
		for (int i = 0; i < nodes.length; i++) {
			String str_userName = hvs[i].getStringValue("participant_username"); //用户名称

			String str_approveModel = hvs[i].getStringValue("currapprovemodel"); //审批模式!!
			String str_parentwfcreatebyid = hvs[i].getStringValue("parentwfcreatebyid"); //父流程的创建id!
			String str_submittoactivity_approvemodel = hvs[i].getStringValue("submittoactivity_approvemodel"); //父流程的创建id!

			StringBuilder sb_text = new StringBuilder(); //创建树上每个结点的显示名称!!
			if (hvs[i].getBooleanValue("isccto", false)) { //如果是抄送的,则显示抄送-
				sb_text.append("抄送-"); //
			}

			sb_text.append(str_userName); //处理人
			if (!hvs[i].getBooleanValue("isccto", false)) {
				sb_text.append("[" + wfUIUtil.getWFActivityName(hvs[i]) + "]"); //
			}

			if (!hvs[i].getBooleanValue("isccto", false)) { //如果不是抄送的
				if ("2".equals(str_approveModel)) {
					sb_text.append("<会签>"); //
				} else if ("3".equals(str_approveModel)) {
					sb_text.append("<会办>"); //
				}
			}

			if ("3".equals(str_submittoactivity_approvemodel)) {
				sb_text.append("<发起会办>"); //
			}

			if (str_parentwfcreatebyid != null && !str_parentwfcreatebyid.trim().equals("")) {
				sb_text.append("<会办结束>"); //
			}

			if (!hvs[i].getBooleanValue("issubmit", false)) {
				if (hvs[i].getBooleanValue("isccto", false)) {
					sb_text.append("(未确认)"); //
				} else {
					sb_text.append("(未处理)"); //
				}
			}

			hvs[i].setAttributeValue("$TreeNodeText", sb_text.toString()); //
			hvs[i].setToStringFieldName("$TreeNodeText"); //处理人的用户名
			nodes[i] = new DefaultMutableTreeNode(hvs[i]); //
			if ("3".equals(str_approveModel)) { //如果是会办的,则记录下来,下面要对会办的进行收缩!
				al_childWFNode.add(nodes[i]); //
			}
			rootNode.add(nodes[i]); //
			map_node.put(hvs[i].getStringValue("id"), nodes[i]); //
		}

		//寻找父结点,进行勾结!!
		for (int i = 0; i < nodes.length; i++) {
			HashVO hvo_item = (HashVO) nodes[i].getUserObject();
			String str_createby = hvo_item.getStringValue("createbyid"); //我是由谁创建的!!!但后来发现子流程在树型显示时需要回到前一层,所以需要再一个字段!! 记录
			String str_parentWFCreateBy = hvo_item.getStringValue("parentwfcreatebyid"); //父亲流程的实例id
			if (str_parentWFCreateBy != null && !str_parentWFCreateBy.trim().equals("")) { //如果发现有父亲流程的创建者,则说明这条记录是子流程会办结束时回来的那个任务! 则将他直接挂到原来发起人的父亲下面,即与原来发起人平级了!! 这样就形成一种回归的效果!!
				str_createby = str_parentWFCreateBy; //这里有可能是-99999,即根结点!!!
			}

			if (str_createby != null && !str_createby.trim().equals("") && !str_createby.trim().equals("-99999")) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) map_node.get(str_createby); //
				if (parentNode != null) {
					parentNode.add(nodes[i]); //
				}
			}
		}

		dealPoolTree = new JTree(rootNode); //
		dealPoolTree.setUI(new WLTTreeUI(true));
		dealPoolTree.setOpaque(false); //一定要设成透明,否则快速滚动时会出现白条,我曾经为此折磨得很久!!!!!!
		//dealPoolTree.setShowsRootHandles(true); //
		dealPoolTree.setRowHeight(17); //
		//tree.setOpaque(false);  //
		dealPoolTree.setRootVisible(false); //
		//tree.setBackground(Color.WHITE); //
		expandAll(dealPoolTree, new TreePath(rootNode), true); //展开所有结点,后面还要再处理收缩掉所有子流程
		for (int i = 0; i < al_childWFNode.size(); i++) { //收缩所有的会办流程!
			DefaultMutableTreeNode itemNode = (DefaultMutableTreeNode) al_childWFNode.get(i); ////
			expandAll(dealPoolTree, new TreePath(itemNode.getPath()), false); //收缩所有的会办流程!
		}
		dealPoolTree.setCellRenderer(new MyTreeCellRender()); //
		dealPoolTree.getSelectionModel().addTreeSelectionListener(this); //选择监听!!!
		dealPoolTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.getButton() == MouseEvent.BUTTON3) {
					showPopMenu(evt.getComponent(), evt.getX(), evt.getY()); // 弹出菜单
				} else {
					super.mousePressed(evt); //
				}
			}
		});

		return dealPoolTree; //
	}

	/**
	 * 邮储项目中遇到客户反感原来的树,所以另外一种风格!!!
	 * @param hvs
	 * @return
	 * @throws Exception
	 */
	private JTree getDealPoolTree2(HashVO[] _dealpool) throws Exception {
		HashVO[] hvs_dealpool = new HashVO[_dealpool.length]; //
		for (int i = 0; i < hvs_dealpool.length; i++) {
			hvs_dealpool[i] = _dealpool[i].clone(); //
			if (hvs_dealpool[i].getStringValue("submittime", "").equals("")) {
				hvs_dealpool[i].setAttributeValue("submittime", "9999-12-31 23:59:59"); //之所搞这样一个变态的设计,是为了后面排序时把待办任务弄在最后!
			}
		}
		TBUtil.getTBUtil().sortHashVOs(hvs_dealpool, new String[][] { { "submittime", "N", "N" } }); //排序一把,即按照提交时间的升序排序!!!这样与列表联动很有感觉!!
		for (int i = 0; i < hvs_dealpool.length; i++) { //排完顺序后还要再弄回空!!!
			if (hvs_dealpool[i].getStringValue("submittime", "").equals("9999-12-31 23:59:59")) {
				hvs_dealpool[i].setAttributeValue("submittime", null); //
			}
		}

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("已处理的机构/部门"); //
		dealPoolTree2 = new JTree(rootNode); //
		dealPoolTree2.setToolTipText("点击右键有展开/收缩,横向展示等功能!"); //

		DefaultMutableTreeNode lastNode_level_1 = null; //
		DefaultMutableTreeNode lastNode_level_2 = null; //
		for (int i = 0; i < hvs_dealpool.length; i++) { //
			String str_toStringName = ""; //
			if ("Y".equals(hvs_dealpool[i].getStringValue("issubmit"))) { //如果已处理
				String str_subUser = hvs_dealpool[i].getStringValue("realsubmitername"); //
				if (str_subUser.indexOf("/") > 0) {
					str_subUser = str_subUser.substring(str_subUser.indexOf("/") + 1, str_subUser.length()); //
				}
				if (str_subUser.length() <= 2) {
					str_subUser = str_subUser + "  "; //名字只有两位的再补个空格,这样机构会对齐,显示效果很好!
				}
				str_toStringName = str_subUser + " " + hvs_dealpool[i].getStringValue("realsubmitcorpname"); //
				hvs_dealpool[i].setAttributeValue("$ToString", str_toStringName); //
			} else { //如果是待办!
				str_toStringName = hvs_dealpool[i].getStringValue("participant_username") + " " + hvs_dealpool[i].getStringValue("participant_userdeptname"); //
				hvs_dealpool[i].setAttributeValue("$ToString", str_toStringName); //
			}
			hvs_dealpool[i].setToStringFieldName("$ToString"); //

			String str_blcorpName = getOneItemCorpName(hvs_dealpool[i]); //二分/一分
			String str_actName = getOneItemActName(hvs_dealpool[i]); //环节名称
			if (str_blcorpName == null) {
				str_blcorpName = "未知机构"; //
			}
			if (str_actName == null) {
				str_actName = "未知环节"; //
			}

			String str_lastBlCorpName = (i == 0 ? null : getOneItemCorpName(hvs_dealpool[i - 1])); //上一机构名称
			String str_lastActName = (i == 0 ? null : getOneItemActName(hvs_dealpool[i - 1])); //上一环节名称
			if (str_blcorpName.equals(str_lastBlCorpName)) { //如果与前面机构一样
				if (str_actName.equals(str_lastActName)) { //如果本环节与前面环节一样,则在原来的二级节点面加一个
					DefaultMutableTreeNode node_this = new DefaultMutableTreeNode(hvs_dealpool[i]); //先创建一个一级结点!
					lastNode_level_2.add(node_this); //
				} else { //如果与前面环节不一样!则要创建一个环节
					DefaultMutableTreeNode node_1_1 = new DefaultMutableTreeNode(str_actName); //先创建一个一级结点!
					DefaultMutableTreeNode node_this = new DefaultMutableTreeNode(hvs_dealpool[i]); //先创建一个一级结点!
					node_1_1.add(node_this); //
					lastNode_level_1.add(node_1_1); //
					lastNode_level_2 = node_1_1; //
				}
			} else { //直接创建一个
				DefaultMutableTreeNode node_1 = new DefaultMutableTreeNode(str_blcorpName); //先创建一个一级结点!
				DefaultMutableTreeNode node_1_1 = new DefaultMutableTreeNode(str_actName); //先创建一个一级结点!
				DefaultMutableTreeNode node_this = new DefaultMutableTreeNode(hvs_dealpool[i]); //先创建一个一级结点!
				node_1_1.add(node_this); //
				node_1.add(node_1_1); //
				rootNode.add(node_1); //
				lastNode_level_1 = node_1; //
				lastNode_level_2 = node_1_1; //
			}
		}

		dealPoolTree2 = new JTree(rootNode); //
		dealPoolTree2.setUI(new WLTTreeUI(true));
		dealPoolTree2.setOpaque(false); //一定要设成透明,否则快速滚动时会出现白条,我曾经为此折磨得很久!!!!!!
		//dealPoolTree.setShowsRootHandles(true); //
		dealPoolTree2.setRowHeight(17); //
		//tree.setOpaque(false);  //
		//dealPoolTree2.setRootVisible(false); //

		dealPoolTree2.setCellRenderer(new MyTreeCellRender2()); //
		dealPoolTree2.getSelectionModel().addTreeSelectionListener(this); //选择监听!!!

		dealPoolTree2.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.getButton() == MouseEvent.BUTTON3) {
					showPopMenu2(evt.getComponent(), evt.getX(), evt.getY()); // 弹出菜单
				}
			}
		});

		return dealPoolTree2; //
	}

	private String getOneItemCorpName(HashVO _hvo) {
		String str_blcorpName = _hvo.getStringValue("curractivity_belongdeptgroup"); //二分/一分
		String str_parentid = _hvo.getStringValue("parentinstanceid"); //
		if (str_parentid != null && !str_parentid.equals("")) { //如果不为空,则说明是子流程!!
			str_blcorpName = _hvo.getStringValue("prinstanceid_fromparentactivitybldeptgroup"); //父亲流程的名称
		}
		return str_blcorpName; //
	}

	private String getOneItemActName(HashVO _hvo) {
		String str_blcorpName = _hvo.getStringValue("curractivity_wfname"); //二分/一分
		String str_parentid = _hvo.getStringValue("parentinstanceid"); //
		if (str_parentid != null && !str_parentid.equals("")) { //如果不为空,则说明是子流程!!
			str_blcorpName = _hvo.getStringValue("prinstanceid_fromparentactivityname"); //父亲流程的名称
		}
		return str_blcorpName; //
	}

	/**
	 * 树型选择变化!
	 * @param e
	 */
	public void valueChanged(TreeSelectionEvent _event) {
		onTreeSelectedChanged(_event); //
	}

	//树型控件选择变化!!!
	private void onTreeSelectedChanged(TreeSelectionEvent _event) {
		if (isTableSelectChanging) {
			return;
		}

		isTreeSelectChanging = true; //
		dealPoolBillList.clearSelection(); //
		if (_event.getSource() == dealPoolTree.getSelectionModel()) {
			TreePath[] selectedPaths = dealPoolTree.getSelectionPaths(); //
			if (selectedPaths == null) {
				return;
			}
			for (int r = 0; r < selectedPaths.length; r++) { //
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPaths[r].getLastPathComponent(); //
				HashVO hvo = (HashVO) node.getUserObject(); //取得结点
				String str_dealpoolId = hvo.getStringValue("id"); //主键
				for (int i = 0; i < dealPoolBillList.getRowCount(); i++) {
					String str_id = ((StringItemVO) dealPoolBillList.getValueAt(i, "id")).getStringValue(); //
					if (str_id.equals(str_dealpoolId)) { //如果找到!!!
						dealPoolBillList.addSelectedRow(i); //
						break; //
					}
				}
			}
			reLoadGraphFromTree(dealPoolTree); //根据树中选中的结点,刷新流程图
		} else if (_event.getSource() == dealPoolTree2.getSelectionModel()) {
			TreePath[] selectedPaths = dealPoolTree2.getSelectionPaths(); //
			if (selectedPaths == null) {
				return;
			}

			for (int r = 0; r < selectedPaths.length; r++) { //
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPaths[r].getLastPathComponent(); //
				Object selObj = node.getUserObject(); //取得结点;  //
				if (selObj instanceof HashVO) {
					HashVO hvo = (HashVO) selObj; //
					String str_dealpoolId = hvo.getStringValue("id"); //主键
					for (int i = 0; i < dealPoolBillList.getRowCount(); i++) {
						String str_id = ((StringItemVO) dealPoolBillList.getValueAt(i, "id")).getStringValue(); //
						if (str_id.equals(str_dealpoolId)) { //如果找到!!!
							dealPoolBillList.addSelectedRow(i); //
							break; //
						}
					}
				}
			}
			reLoadGraphFromTree(dealPoolTree2); //根据树中选中的结点,刷新流程图
		}
		isTreeSelectChanging = false; //
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == dealPoolBillList) {
			onSelectedDealPoolList();
		}
	}

	/**
	 * 选中表格中的记录,联动树型
	 */
	private void onSelectedDealPoolList() {
		if (isTreeSelectChanging) {
			return; //如果是树在选中变化的,则不要再去刷新树了,否则会造成死循环!!
		}
		isTableSelectChanging = true;
		BillVO[] selectedVOs = dealPoolBillList.getSelectedBillVOs(); //
		if (selectedVOs == null || selectedVOs.length == 0) {
			return;
		}
		for (int r = 0; r < selectedVOs.length; r++) {
			String str_id = selectedVOs[r].getStringValue("id"); //取得主键(pub_wf_dealpool.id)
			findAndScrollToTreeOneNode(dealPoolTree, str_id); //
			findAndScrollToTreeOneNode(dealPoolTree2, str_id); //
		}

		reLoadGraphFromTree(dealPoolTree); //也是从树中取数,刷新图!
		isTableSelectChanging = false;
	}

	private void findAndScrollToTreeOneNode(JTree _tree, String _id) {
		_tree.clearSelection(); //
		DefaultMutableTreeNode[] allNodes = getAllChildrenNodes((DefaultMutableTreeNode) _tree.getModel().getRoot(), true); //取得所有结点!!!!
		for (int i = 1; i < allNodes.length; i++) { //
			Object obj = allNodes[i].getUserObject(); //
			if (obj instanceof HashVO) { //必须是HashVO
				HashVO hvo = (HashVO) obj; //
				String str_treeObjId = hvo.getStringValue("id"); //
				if (_id.equals(str_treeObjId)) {
					scrollToOneNode(_tree, allNodes[i]); //选中并滚动到这一条!!!
					break; //
				}
			}
		}
	}

	private HashVO endActivity[] = null;

	//获取结束环节
	private HashVO[] getEndAcitivty(String processid) {
		if (endActivity == null) {
			try {

				//				endActivity = UIUtil.getHashVoArrayByDS(null, "select id,wfname,isneedreport,belongdeptgroup from pub_wf_activity where processid =" + processid + " and activitytype='END'");
				endActivity = UIUtil.getHashVoArrayByDS(null, "select t2.id fromactivityid,t3.id,t3.wfname,t3.isneedreport,t3.belongdeptgroup from pub_wf_transition t1 left join pub_wf_activity t2 on t1.fromactivity = t2.id left join pub_wf_activity t3 on t1.toactivity = t3.id where t3.activitytype='END' and t3.processid = " + processid);
			} catch (Exception e) {
				e.printStackTrace();
			} //
		}
		return endActivity;
	}

	//添加最后的结束环节 by haoming 2016-04-29
	//在太平做偿二代系统，流程最后一个环节是结束。但是处理记录没有没有此环节。监控时结束环节不会标记出来。 一种解决方案是在服务器端虚拟一个处理历史，第二种就是在 ui端判断是否虚拟一个。采用第二种
	public void addEndAcitivity(ArrayList al_temp) {
		try {
			if ("end".equalsIgnoreCase(processStatus) && al_temp.size() > 0) { //如果流程结束了
				//判断hvs里面结束时的环节是否是普通环节，需要把最后end的结束环节虚拟加上。否则流程结束了，但是监控看不到。by haoming 2016-04-28
				HashVO lastAcitivityVO = (HashVO) al_temp.get(al_temp.size() - 1); //当前的最后一步
				//判断最后一个环节是不是半路结束环节
				String processid = lastAcitivityVO.getStringValue("processid");
				HashVO[] hvs_actsInfos = getEndAcitivty(processid); //获取结束环节
				if (hvs_actsInfos != null && hvs_actsInfos.length > 0) {
					for (int i = 0; i < hvs_actsInfos.length; i++) {
						if (hvs_actsInfos[i].getStringValue("fromactivityid", "").equals(lastAcitivityVO.getStringValue("curractivity"))) {
							HashVO cp = (HashVO) tbUtil.deepClone(lastAcitivityVO);
							cp.setAttributeValue("id", "-999");
							cp.setAttributeValue("batchno", "1000"); //足够大
							cp.setAttributeValue("fromactivity", lastAcitivityVO.getStringValue("curractivity"));
							cp.setAttributeValue("curractivity", hvs_actsInfos[i].getStringValue("id"));
							cp.setAttributeValue("submitmessage", "");
							cp.setAttributeValue("participant_username", hvs_actsInfos[i].getStringValue("wfname"));
							lastAcitivityVO.setAttributeValue("submittoactivity", hvs_actsInfos[i].getStringValue("id"));
							al_temp.add(cp);
							break;
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void reLoadGraphFromTree(JTree _tree) {
		TreePath[] selectedPaths = _tree.getSelectionPaths(); //
		HashVO[] hvs = null; //
		String str_processId = null; //
		if (selectedPaths != null && selectedPaths.length > 0) { //如果有选中的结点
			ArrayList al_temp = new ArrayList(); //
			for (int i = 0; i < selectedPaths.length; i++) { //遍历所有选中的路径!!!
				DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedPaths[i].getLastPathComponent(); //
				if (selNode.isRoot()) {
					continue;
				}
				Object obj = selNode.getUserObject(); //
				if (obj instanceof HashVO) {
					HashVO hvoItem = (HashVO) obj; //
					if (str_processId == null) {
						str_processId = hvoItem.getStringValue("processid"); //因为可能一条选中的是主流程而另一条选中的是子流程!! 所以只能用一个!
					}
					al_temp.add(hvoItem); //记录下来!
				}
			}
			DefaultMutableTreeNode[] allNodes = getAllChildrenNodes((DefaultMutableTreeNode) _tree.getModel().getRoot(), true); //取得所有结点!!!!
			if (allNodes.length > 0 && allNodes[allNodes.length - 1].getUserObject() == al_temp.get(al_temp.size() - 1)) {//判断选中的是否是最后一个环节
				addEndAcitivity(al_temp);
			}
			hvs = (HashVO[]) al_temp.toArray(new HashVO[0]); //
		} else { //如果没有选中的结点,则直接使用根结点!
			ArrayList al_temp = new ArrayList(); //
			DefaultMutableTreeNode[] allNodes = getAllChildrenNodes((DefaultMutableTreeNode) _tree.getModel().getRoot(), true); //取得所有结点!!!!
			for (int i = 0; i < allNodes.length; i++) {
				if (allNodes[i].isRoot()) {
					continue;
				}
				Object obj = allNodes[i].getUserObject(); //
				if (obj instanceof HashVO) {
					HashVO hvo = (HashVO) obj; //
					if (str_processId == null) {
						str_processId = hvo.getStringValue("processid");
					}
					al_temp.add(hvo); //
				}
			}
			addEndAcitivity(al_temp);
			hvs = (HashVO[]) al_temp.toArray(new HashVO[0]); //
		}

		if (str_processId == null) {
			return; //
		}

		try {
			//遍历所有数据
			ArrayList al_temp = new ArrayList(); //
			if (!getGraphPanel().getCurrentProcessVO().getId().equals(str_processId)) {
				getGraphPanel().loadGraphByID(str_processId); //重新加载新的流程图!
			}

			//先画目标环节
			for (int i = 0; i < hvs.length; i++) {
				if (hvs[i].getStringValue("processid").equals(str_processId)) { //如果是同一个流程
					if (hvs[i].getStringValue("submittoactivity") != null) {
						al_temp.add(new String[] { hvs[i].getStringValue("submittoactivity"), "To" }); //去了哪!!
					}
				}
			}
			//再画来源环节
			for (int i = 0; i < hvs.length; i++) {
				if (hvs[i].getStringValue("processid").equals(str_processId)) { //如果是同一个流程
					al_temp.add(new String[] { hvs[i].getStringValue("fromactivity"), "From" }); //从哪个环节来的
				}
			}
			//最后画当前环节,保证当前环节永远能显示,而不会被其他环节充掉!
			for (int i = 0; i < hvs.length; i++) {
				if (hvs[i].getStringValue("processid").equals(str_processId)) { //如果是同一个流程
					al_temp.add(new String[] { hvs[i].getStringValue("curractivity"), "Y" }); //当前哪个环节
				}
			}

			String[][] str_allcells = new String[al_temp.size()][2]; //
			for (int i = 0; i < str_allcells.length; i++) {
				str_allcells[i] = (String[]) al_temp.get(i); //
			}
			getGraphPanel().lightCell(str_allcells); //光亮显示所有环节!!

			//光亮处理所有连线!
			ArrayList al_lines = new ArrayList(); //
			for (int i = 0; i < hvs.length; i++) {
				if (hvs[i].getStringValue("processid").equals(str_processId)) { //如果是同一个流程
					String str_fromId = hvs[i].getStringValue("fromactivity"); //
					String str_currId = hvs[i].getStringValue("curractivity"); //
					String str_toId = hvs[i].getStringValue("submittoactivity"); //
					al_lines.add(new String[] { str_fromId, str_currId }); //
					if (str_toId != null) {
						al_lines.add(new String[] { str_currId, str_toId }); //
					}
				}
			}
			String[][] str_allLines = new String[al_lines.size()][2]; //
			for (int i = 0; i < str_allLines.length; i++) {
				str_allLines[i] = (String[]) al_lines.get(i); //
			}
			getGraphPanel().lightLine(str_allLines); //
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				getGraphPanel().reSetAllLayer(false); //
			} catch (Exception ex) {
			}
		}
	}

	private void expandAll(JTree tree, boolean _isExpand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), _isExpand);
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

	//得到所有子结点!
	private DefaultMutableTreeNode[] getAllChildrenNodes(TreeNode _rootNode, boolean _isContainSelf) {
		Vector vector = new Vector();
		visitAllNodes(vector, _rootNode, 0, _isContainSelf); //
		return (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //
	}

	private void visitAllNodes(Vector _vector, TreeNode _node, int _count, boolean _isContainSelf) {
		if (_isContainSelf) { //如果包含子结点,则永远加入!
			_vector.add(_node); // 加入该结点
		} else { //如果不包含子结点!
			if (_count > 0) { //如果是第一次,则不算!
				_vector.add(_node); // 加入该结点
			}
		}
		int li_count = _count + 1; //
		if (_node.getChildCount() >= 0) {
			for (Enumeration e = _node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				visitAllNodes(_vector, childNode, li_count, _isContainSelf); // 继续查找该儿子
			}
		}
	}

	//滚动某某一个结点
	private void scrollToOneNode(JTree _tree, DefaultMutableTreeNode _node) {
		if (_node != null) {
			TreeNode[] nodes = ((DefaultTreeModel) _tree.getModel()).getPathToRoot(_node);
			TreePath path = new TreePath(nodes);
			_tree.makeVisible(path);
			_tree.scrollPathToVisible(path);
			_tree.addSelectionPath(path); //
			_tree.repaint(); //
		}
	}

	/**
	 * 导出Html报表.
	 */
	private void onExportHtmlReport(HashMap map_par) {
		try {
			if ("N".equals((String) map_par.get("是否允许"))) {
				MessageBox.showInfo(this, "抱歉您没有使用此报表的权限!");
				return;
			}
			map_par.put("prinstanceid", str_wfinstance); //
			map_par.put("billvo", this.billVO); //
			map_par.put("roles", ClientEnvironment.getInstance().getLoginUserRoleIds());
			map_par.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			if (map_par.containsKey("自定义权限")) {//一般人不用所以这里就不加类了
				String classname = (String) map_par.get("自定义权限");
				Class objclass = Class.forName(classname);
				Method objmethod = objclass.getMethod("selfCheck", new Class[] { HashMap.class });
				Object objInstance = objclass.newInstance();
				HashMap returnObj = (HashMap) objmethod.invoke(objInstance, new Object[] { map_par });
				if (returnObj != null && returnObj.size() > 0) {
					Boolean ifpass = (Boolean) returnObj.get("ifpass");
					String alert = (String) returnObj.get("alert");
					if (!ifpass) {
						MessageBox.show(this, alert);
						return;
					}
				}
			}
			//意见导出自定义类 【杨科/2013-01-18】
			String str_class = TBUtil.getTBUtil().getSysOptionStringValue("工作流导出项目自定义类", "cn.com.infostrategy.bs.workflow.WorkflowHtmlReportDMO");
			UIUtil.loadHtml(str_class, map_par); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} //
	}

	private void showPopMenu(Component _compent, int _x, int _y) {
		final JTree thisTree = (JTree) _compent; //
		int li_count = thisTree.getSelectionCount(); //
		if (li_count <= 1) {
			TreePath parentPath = thisTree.getClosestPathForLocation(_x, _y);
			((JTree) _compent).setSelectionPath(parentPath); //
		}
		if (popMenu == null) {
			popMenu = new JPopupMenu(); //
			final JMenuItem menuItem_1 = new JMenuItem("查看详细信息"); //
			final JMenuItem menuItem_2 = new JMenuItem("直接撤到某一步"); //
			final JMenuItem menuItem_delalltask = new JMenuItem("删除所有子任务"); //
			final JMenuItem menuItem_3 = new JMenuItem("比较多条信息"); //
			ActionListener actListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == menuItem_1) {
						onShowTreeNodeInfo(thisTree); //
					} else if (e.getSource() == menuItem_2) {
						onDirCancelOneStep(); //直接撤到某一步!
					} else if (e.getSource() == menuItem_delalltask) {
						onDeleteAllChildrenTask(); //直接撤到某一步!
					} else if (e.getSource() == menuItem_3) {
						onCompareSomRecords(); //比较多条信息!
					}
				}
			};

			menuItem_1.addActionListener(actListener); //
			menuItem_2.addActionListener(actListener); //
			menuItem_delalltask.addActionListener(actListener); //
			menuItem_3.addActionListener(actListener); //
			popMenu.add(menuItem_1); //
			if (ClientEnvironment.getInstance().isAdmin()) {
				popMenu.add(menuItem_2); //
				popMenu.add(menuItem_delalltask); //
				popMenu.add(menuItem_3); //
			}
		}
		popMenu.show(_compent, _x, _y); //
	}

	private void showPopMenu2(Component _compent, int _x, int _y) {
		final JTree thisTree = (JTree) _compent; //
		int li_count = thisTree.getSelectionCount(); //
		if (li_count <= 1) {
			TreePath parentPath = ((JTree) _compent).getClosestPathForLocation(_x, _y);
			((JTree) _compent).setSelectionPath(parentPath); //
		}
		if (popMenu2 == null) {
			popMenu2 = new JPopupMenu(); //
			final JMenuItem menuItem_1 = new JMenuItem("展开所有", UIUtil.getImage("office_163.gif")); //
			final JMenuItem menuItem_2 = new JMenuItem("收缩所有", UIUtil.getImage("office_078.gif")); //
			final JMenuItem menuItem_3 = new JMenuItem("来龙去脉", UIUtil.getImage("office_200.gif")); //
			final JMenuItem menuItem_4 = new JMenuItem("横向展示", UIUtil.getImage("office_109.gif")); //
			final JMenuItem menuItem_5 = new JMenuItem("详细信息", UIUtil.getImage("office_200.gif")); //

			final JMenuItem menuItem_6 = new JMenuItem("会办子流程删除", UIUtil.getImage("zt_021.gif"));
			final JMenuItem menuItem_7 = new JMenuItem("结束退回", UIUtil.getImage("zt_072.gif"));

			menuItem_3.setToolTipText("显示一个结点任务从谁那来,又提给了谁!"); //
			menuItem_4.setToolTipText("将机构横向平铺开来展示,即第一层结点单独是一个树!"); //

			menuItem_6.setToolTipText("会办子流程中,删除某一参与人");
			menuItem_7.setToolTipText("工作流已结束,退回至上一步未结束时状态");

			menuItem_1.setPreferredSize(new Dimension(85, 21)); //
			menuItem_2.setPreferredSize(new Dimension(85, 21)); //
			ActionListener actListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == menuItem_1) {
						expandAll(thisTree, true); //
					} else if (e.getSource() == menuItem_2) {
						expandAll(thisTree, false); //
					} else if (e.getSource() == menuItem_3) {
						onShowTreeNodeFromTo(thisTree); //
					} else if (e.getSource() == menuItem_4) {
						onHalignView(thisTree); //
					} else if (e.getSource() == menuItem_5) {
						onShowTreeNodeInfo(thisTree); //
					} else if (e.getSource() == menuItem_6) {
						delWFPerson(thisTree);
					} else if (e.getSource() == menuItem_7) {
						endWFRevoke(thisTree);
					}
				}
			};

			menuItem_1.addActionListener(actListener); //
			menuItem_2.addActionListener(actListener); //
			menuItem_3.addActionListener(actListener); //
			menuItem_4.addActionListener(actListener); //
			menuItem_5.addActionListener(actListener); //

			menuItem_6.addActionListener(actListener);
			menuItem_7.addActionListener(actListener);

			popMenu2.add(menuItem_1); //
			popMenu2.add(menuItem_2);
			popMenu2.add(menuItem_3);
			popMenu2.add(menuItem_4);
			if (ClientEnvironment.isAdmin()) {
				popMenu2.add(menuItem_5);

				popMenu2.add(menuItem_6);

				//已办任务追加结束退回
				String str_taskOffId = getTaskOffId(billVO);
				if (str_taskOffId != null && !"".equals(str_taskOffId)) {
					popMenu2.add(menuItem_7);
				}
			}
		}
		popMenu2.show(_compent, _x, _y); //
	}

	//管理员控制会办子流程结束 【杨科/2013-05-29】
	private void delWFPerson(JTree _tree) {
		TreePath seledPath = _tree.getSelectionPath();
		if (seledPath == null) {
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent();
		Object selobj = node.getUserObject();
		if (!(selobj instanceof HashVO)) {
			MessageBox.show(this, "请选择处理人员结点进行此操作!");
			return;
		}

		if (!MessageBox.confirm(this, "您确定要删除该会办子流程节点下处理人员结点吗?")) {
			return;
		}

		HashVO hvo = (HashVO) selobj;
		String str_dealpoolid = hvo.getStringValue("id", "");
		String str_prinstanceid = hvo.getStringValue("prinstanceid", "");
		String str_parentinstanceid = hvo.getStringValue("parentinstanceid", "");
		String participant_user = hvo.getStringValue("participant_user", ""); //处理人ID

		if ("".equals(str_parentinstanceid)) {
			MessageBox.show(this, "请选择会办子流程节点下处理人员结点进行此操作!");
			return;
		}

		if ("".equals(str_dealpoolid) || "".equals(str_prinstanceid)) {
			MessageBox.show(this, "流程异常!流程处理ID或流程实例ID为空!无法删除!");
			return;
		}

		String str_message = "";
		String str_msgfile = "";

		WFParVO myvo = new WFParVO();
		myvo.setApproveModel("");
		myvo.setDealpoolid(str_dealpoolid);
		myvo.setParentinstanceid(str_parentinstanceid);

		try {
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class);
			String str_endResult = service.endWorkFlow_admin(str_prinstanceid, myvo, participant_user, str_message, str_msgfile, "非正常结束", billVO, null);
			MessageBox.show(this, str_endResult);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	//管理员控制主流程结束撤回 【杨科/2013-05-29】
	private void endWFRevoke(JTree _tree) {
		TreePath seledPath = _tree.getSelectionPath();
		if (seledPath == null) {
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent();
		Object selobj = node.getUserObject();
		if (!(selobj instanceof HashVO)) {
			MessageBox.show(this, "请选择处理人员结点进行此操作!");
			return;
		}

		if (!MessageBox.confirm(this, "您确定要撤回该结束节点吗?")) {
			return;
		}

		HashVO hvo = (HashVO) selobj;
		String str_dealpoolid = hvo.getStringValue("id", "");
		String str_prinstanceid = hvo.getStringValue("prinstanceid", "");
		String str_parentinstanceid = hvo.getStringValue("parentinstanceid", "");
		String participant_user = hvo.getStringValue("participant_user", ""); //处理人ID
		String str_taskOffId = getTaskOffId(billVO);

		if (!"".equals(str_parentinstanceid)) {
			MessageBox.show(this, "非该流程结束节点不能撤回!");
			return;
		}

		if ("".equals(str_dealpoolid) || "".equals(str_prinstanceid)) {
			MessageBox.show(this, "流程异常!流程处理ID或流程实例ID!无法撤回!");
			return;
		}

		try {
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class);
			String str_endResult = service.cancelTask_admin(str_prinstanceid, str_dealpoolid, str_taskOffId, participant_user, null);
			MessageBox.show(this, str_endResult);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private String getTaskOffId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1);
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //如果没有定义hvo,即以前的机制!!则直接取
			return null;
		} else {
			return hvo.getStringValue("task_taskoffid"); //从行号中取
		}
	}

	private void onShowTreeNodeFromTo(JTree _tree) {
		TreePath seledPath = _tree.getSelectionPath();
		if (seledPath == null) {
			return;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent(); //
		Object selobj = node.getUserObject(); //
		if (!(selobj instanceof HashVO)) {
			MessageBox.show(this, "请选择处理人员结点进行此操作!"); //
			return; //
		}
		HashVO hvo = (HashVO) selobj; //

		String str_createbyid = hvo.getStringValue("createbyid");
		String str_creatername = hvo.getStringValue("creatername", ""); //
		String str_createcorpname = hvo.getStringValue("createcorpname", ""); //
		String str_createtime = hvo.getStringValue("createtime", ""); //

		String str_submittousername = hvo.getStringValue("submittousername", ""); //
		String str_submittocorpname = hvo.getStringValue("submittocorpname", ""); //
		String str_submittime = hvo.getStringValue("submittime", ""); //

		StringBuilder sb_text = new StringBuilder(); //
		sb_text.append("此功能是计算出这条任务是谁提交过来的,然后又提交了给谁!\r\n绝大数情况下来源任务就是上一条,目标任务就是下一条,但在会签或跨部门时,则不是很清晰,这时此功能就会派上用场!\r\n\r\n"); //
		sb_text.append("本条任务是:[" + hvo.toString() + "]\r\n\r\n");

		sb_text.append("--------------------- 从哪里来 ---------------------\r\n 人员/机构:[" + str_creatername + "  " + str_createcorpname + "]\r\n过来的时间:[" + str_createtime + "]\r\n"); //

		sb_text.append("\r\n\r\n"); //
		String str_totitle = "--------------------- 去了哪里 ---------------------\r\n"; //
		if (str_submittime.equals("")) {
			sb_text.append(str_totitle + "还没提交呢,说明这还是一条未处理任务..."); //
		} else {
			if (str_submittousername.equals("")) {
				sb_text.append(str_totitle + "这是会签或流程的结束步骤,是由系统自动处理,不是人工提交的,处理时间:" + str_submittime); //
			} else {
				sb_text.append(str_totitle);
				sb_text.append(" 人员/机构:");
				String[] str_users = TBUtil.getTBUtil().split(str_submittousername, ";"); //
				String[] str_corps = TBUtil.getTBUtil().split(str_submittocorpname, ";"); //
				for (int i = 0; i < str_users.length; i++) {
					sb_text.append("[" + str_users[i] + "  " + str_corps[i] + "]");
				}
				sb_text.append("\r\n");
				sb_text.append("出去的时间:[" + str_submittime + "]\r\n");
			}
		}

		sb_text.append("\r\n\r\n");
		sb_text.append("※其他提示:\r\n");
		sb_text.append("1.流程图中红色框表示是选中的当前环节,粉红色框表示是从哪里来的,蓝色框表示是去了哪里!\r\n  如果来源与去处是同一个人,则只有粉色而没有蓝色!如果是部门内部流转的,则只有一个红框!\r\n");
		sb_text.append("2.点击【跳转至来源】按钮可以自动选择到来源任务处!\r\n");
		sb_text.append("3.由于会签时可能存在多个接收者,所以【跳转至目标任务】不好控制,故系统暂时不提供此功能!\r\n");

		int li_result = MessageBox.showOptionDialog(this, sb_text.toString(), "提示", new String[] { "[office_078.gif]跳转至来源", " 取  消 " }); //
		if (li_result == 0) {
			if (str_createbyid == null || str_createbyid.equals("")) {
				MessageBox.show(this, "流程启动的第一条任务,无法做跳转操作!"); //
				return; //
			}
			findAndScrollToTreeOneNode(_tree, str_createbyid); //
		}
	}

	/**
	 * 横向展示...
	 */
	private void onHalignView(JTree _tree) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) _tree.getModel().getRoot(); //
		int li_count = rootNode.getChildCount(); //

		int li_itemwidth = 210; //
		JPanel allContentPanel = WLTPanel.createDefaultPanel(null);
		JPanel[] panels = new JPanel[li_count]; //
		for (int i = 0; i < li_count; i++) { //遍历所有子结点...
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootNode.getChildAt(i); //
			String hvo = (String) childNode.getUserObject(); //

			DefaultMutableTreeNode[] oldNodes = getAllChildrenNodes(childNode, false); //所有旧的结点....
			DefaultMutableTreeNode[] newNodes = new DefaultMutableTreeNode[oldNodes.length]; //
			HashMap mapOldNew = new HashMap(); //
			HashMap mapOldNew2 = new HashMap(); //
			for (int j = 0; j < oldNodes.length; j++) {
				newNodes[j] = (DefaultMutableTreeNode) oldNodes[j].clone(); //克隆..
				mapOldNew.put(newNodes[j], oldNodes[j]); //
				mapOldNew2.put(oldNodes[j], newNodes[j]); //
			}
			for (int j = 0; j < newNodes.length; j++) { //
				DefaultMutableTreeNode myOldLink = (DefaultMutableTreeNode) mapOldNew.get(newNodes[j]); //我对应的旧的结点
				DefaultMutableTreeNode myOldLinkParent = (DefaultMutableTreeNode) myOldLink.getParent(); //我对应的旧结点的父亲!
				if (myOldLinkParent != childNode) { //只要父亲不是根!
					DefaultMutableTreeNode myNewParent = (DefaultMutableTreeNode) mapOldNew2.get(myOldLinkParent); //成功找到我的父亲!!
					myNewParent.add(newNodes[j]); //
				}
			}

			DefaultMutableTreeNode itemRootNode = new DefaultMutableTreeNode("【" + hvo + "】"); //
			for (int j = 0; j < newNodes.length; j++) {
				if (newNodes[j].getParent() == null) {
					itemRootNode.add(newNodes[j]); //
				}
			}
			JTree itemTree = new JTree(itemRootNode); //
			itemTree.setUI(new WLTTreeUI(true, false));
			itemTree.setOpaque(false); //
			itemTree.setCellRenderer(new MyTreeCellRender2()); //

			panels[i] = new JPanel(new BorderLayout()); ////
			JLabel label = new JLabel(hvo, SwingConstants.CENTER); //
			label.setOpaque(true); //
			label.setForeground(Color.RED); //
			label.setBackground(new Color(255, 255, 206)); //
			label.setPreferredSize(new Dimension(1000, 22)); //
			panels[i].add(label, BorderLayout.NORTH); //
			panels[i].add(new JScrollPane(itemTree), BorderLayout.CENTER); ////
			panels[i].setBounds(5 + i * li_itemwidth, 5, li_itemwidth - 5, 500); //
			allContentPanel.add(panels[i]); //加入!!!
		}
		allContentPanel.setPreferredSize(new Dimension(5 + li_count * li_itemwidth, 505)); //
		JPanel ctPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		ctPanel.add(new JScrollPane(allContentPanel)); //

		int li_dialog_width = li_count * li_itemwidth + 25; //
		if (li_dialog_width > 1000) {
			li_dialog_width = 1000; //
		}
		BillDialog dialog = new BillDialog(this, "横向展示", li_dialog_width, 570); //
		dialog.getContentPane().add(ctPanel); //
		dialog.setVisible(true); //
	}

	//显示示树型结点的信息!!!
	private void onShowTreeNodeInfo(JTree _tree) {
		TreePath seledPath = _tree.getSelectionPath();
		if (seledPath == null) {
			return;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent(); //
		Object selobj = node.getUserObject(); //
		if (!(selobj instanceof HashVO)) {
			MessageBox.show(this, "请选择处理人员结点进行此操作!"); //
			return; //
		}
		HashVO hvo = (HashVO) selobj; //
		String[] str_keys = hvo.getKeys(); //
		StringBuilder sb_text = new StringBuilder(); //
		sb_text.append("用户=[" + hvo.getStringValue("participant_usercode") + "/" + hvo.getStringValue("participant_username") + "]\r\n"); ////
		sb_text.append("环节名称=[" + getEmptyStr(hvo.getStringValue("curractivity_wfname")) + "]\r\n"); ////
		sb_text.append("是否抄送的=[" + getEmptyStr(hvo.getStringValue("isccto")) + "]\r\n"); ////
		sb_text.append("是否处理=[" + getEmptyStr(hvo.getStringValue("issubmit")) + "]\r\n"); ////
		sb_text.append("处理时间=[" + getEmptyStr(hvo.getStringValue("submittime")) + "]\r\n"); ////
		sb_text.append("处理类型=[" + getEmptyStr(hvo.getStringValue("submitisapprove")) + "]\r\n"); ////

		if (ClientEnvironment.getInstance().isAdmin()) {
			sb_text.append("\r\n"); //
			sb_text.append("所有详细信息(pub_wf_dealpool):\n"); //
			for (int i = 0; i < str_keys.length; i++) {
				sb_text.append("[" + str_keys[i] + "]=[" + getEmptyStr(hvo.getStringValue(str_keys[i])) + "]\r\n"); //
			}

			try {
				sb_text.append("\r\n根流程实例详细信息(pub_wf_prinstance.id=[" + hvo.getStringValue("rootinstanceid", "") + "]):\n"); //
				HashVO[] hvs2 = UIUtil.getHashVoArrayByDS(null, "select * from pub_wf_prinstance where id='" + hvo.getStringValue("rootinstanceid", "") + "'"); //
				if (hvs2.length > 0) {
					String[] str_keys2 = hvs2[0].getKeys(); //
					for (int i = 0; i < str_keys2.length; i++) {
						String str_text = "[" + str_keys2[i] + "]=[" + getEmptyStr(hvs2[0].getStringValue(str_keys2[i])) + "]";
						if (str_keys2[i].equalsIgnoreCase("routemark")) {
							str_text = str_text + " ★★★★★这就是传说中的路由标记！！！★★★★★";
						}
						sb_text.append(str_text + "\r\n"); //
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
		MessageBox.show(this, sb_text.toString()); //

	}

	//直接撤到某一步!!!
	private void onDirCancelOneStep() {
		TreePath seledPath = dealPoolTree.getSelectionPath();
		if (seledPath == null) {
			MessageBox.show(this, "选中的结点为空!");
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent(); //
		if (node.isRoot()) {
			MessageBox.show(this, "不能直接退回到根结点,因为这是发起的任务!"); //以后也可以,即彻底清空流程数据!!
			return; //
		}

		HashVO hvo = (HashVO) node.getUserObject(); //
		String str_text = hvo.toString(); //
		if (JOptionPane.showConfirmDialog(this, "您确定要退回到[" + str_text + "]这一步吗?\r\n这将清空他后面走过的所有步骤!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}

		try {
			String str_sel_prinstanceid = hvo.getStringValue("prinstanceid"); //流程实例id
			String str_sel_prdealpoolid = hvo.getStringValue("id"); //流程实例id
			String str_sel_taskid = UIUtil.getStringValueByDS(null, "select id from pub_task_off where createbydealpoolid='" + str_sel_prdealpoolid + "'"); //
			//			if (str_sel_taskid == null) {
			//				MessageBox.show(this, "没有找到该结点的已办任务,即可能这是条待办任务!\r\n而不能选择待办任务做撤回操作!"); //
			//				return; //
			//			}

			//找到这个结点的所有子孙结点! 然后从三张表中彻底删除这些id的记录!然后修改流程任务中的本人的记录! 然后将已办任务从待办任务表中回拷回来! 然后
			DefaultMutableTreeNode[] allChildNodes = getAllChildrenNodes(node, false); //
			System.out.println("子结点数量=" + allChildNodes.length); //
			ArrayList al_ids = new ArrayList(); //
			for (int i = 0; i < allChildNodes.length; i++) {
				HashVO hvo_item = (HashVO) allChildNodes[i].getUserObject(); //
				String str_dealpoolid = hvo_item.getStringValue("id"); //
				al_ids.add(str_dealpoolid); //
			}
			String[] str_ids = (String[]) al_ids.toArray(new String[0]); //
			for (int i = 0; i < str_ids.length; i++) {
				System.out.println("id=[" + str_ids[i] + "]"); //
			}

			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
			service.cancelTask(str_sel_prinstanceid, str_sel_prdealpoolid, str_sel_taskid, ClientEnvironment.getInstance().getLoginUserID(), str_ids); //
			MessageBox.show(this, "撤回成功,请退出页面刷新!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//直接撤到某一步!!!
	private void onDeleteAllChildrenTask() {
		try {
			TreePath seledPath = dealPoolTree.getSelectionPath();
			if (seledPath == null) {
				MessageBox.show(this, "选中的结点为空!");
				return;
			}

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent(); //
			if (node.isRoot()) {
				MessageBox.show(this, "不能直接退回到根结点,因为这是发起的任务!"); //以后也可以,即彻底清空流程数据!!
				return; //
			}

			DefaultMutableTreeNode[] allChildNodes = getAllChildrenNodes(node, true); //
			String[] str_ids = new String[allChildNodes.length]; //
			for (int i = 0; i < allChildNodes.length; i++) {
				HashVO hvoItem = (HashVO) allChildNodes[i].getUserObject(); ////
				str_ids[i] = hvoItem.getStringValue("id"); //
			}
			String str_in_cons = tbUtil.getInCondition(str_ids); //
			String str_sql_1 = "delete from pub_wf_dealpool where id in (" + str_in_cons + ")"; //
			String str_sql_2 = "delete from pub_task_deal where prdealpoolid in (" + str_in_cons + ")"; //
			String str_sql_3 = "delete from pub_task_off where prdealpoolid in (" + str_in_cons + ")"; //
			String[] str_sqls = new String[] { str_sql_1, str_sql_2, str_sql_3 }; //
			StringBuffer sb_text = new StringBuffer(); //
			for (int i = 0; i < str_sqls.length; i++) {
				sb_text.append(str_sqls[i] + "\r\n"); //
			}

			if (JOptionPane.showConfirmDialog(this, "您确定要删除该多余任务吗?这将同时删除其所有子任务,具体是执行下列SQL:\r\n" + sb_text.toString(), "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			UIUtil.executeBatchByDS(null, str_sqls); //
			MessageBox.show(this, "删除多余任务成功!请退出刷新页面!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//取得选中结点的VO数组!
	private HashVO[] getSelectedNodeVOs() {
		TreePath[] selPaths = dealPoolTree.getSelectionPaths(); //
		if (selPaths == null || selPaths.length == 0) {
			return null;
		}

		HashVO[] hvs = new HashVO[selPaths.length]; //
		for (int i = 0; i < selPaths.length; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPaths[i].getLastPathComponent(); //
			hvs[i] = (HashVO) node.getUserObject(); //
		}
		return hvs; //
	}

	//比较多条信息!在有时数据发生问题时可以快速横向比较多条信息!这样能一眼清的知道哪些不一样!从而快速定位问题!
	private void onCompareSomRecords() {
		HashVO[] hvs = getSelectedNodeVOs(); //
		if (hvs == null) {
			MessageBox.show(this, "请至少选择一个结点!"); //
			return; //
		}
		LookDealPoolAndTaskInfoDialog dialog = new LookDealPoolAndTaskInfoDialog(this, "比较查看", 900, 700, hvs); //
		dialog.setVisible(true); //
	}

	private String getEmptyStr(String _str) {
		if (_str == null) {
			return "";
		}
		return _str; //
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		int li_row = _event.getRow(); //
		RefItemVO refVO = (RefItemVO) _event.getBillListPanel().getValueAt(li_row, _event.getItemkey()); //
		String str_msg = refVO.getId(); //消息!
		String str_msg_real = refVO.getCode(); //如果加密了意见,则这就是加密前的数据!
		String str_reason = _event.getBillListPanel().getRealValueAtModel(li_row, "submitmessage_viewreason"); //原因!
		CopyHistMsgDialog dialog = new CopyHistMsgDialog(this, "查看意见", str_msg, str_msg_real, str_reason, false, billVO); //
		dialog.setVisible(true); //
	}

	/**
	 * 此方法返回适用于当前表单的报表，也就是基于原来的思想 表名=>报表路径;
	 * 扩展为 表名=>报表路径->报表中文名;
	 * 其中报表路径与表名是必填的
	 * 过滤出当前表名的报表
	 * 没有权限的也能看到但是不能导出
	 * 此外增加了一个字段（report_roles）用于描述权限，即 报表路径=>角色1||角色2;
	 * @return
	 */
	public VectorMap getAllReport() {
		if (vm_allReport != null) {
			return vm_allReport; //
		}
		vm_allReport = new WorkflowUIUtil().getAllReport(billVO.getStringValue("billtype"), billVO.getStringValue("busitype"), billVO.getSaveTableName()); //
		return vm_allReport; //
	}

	public BillListPanel getDealPoolBillList() {
		return dealPoolBillList;
	}

	public void setDealPoolBillList(BillListPanel dealPoolBillList) {
		this.dealPoolBillList = dealPoolBillList;
	}

	/**
	 * 显示信息..
	 */
	private void onShowMsg() {
		try {
			String str_billtype = billVO.getStringValue("billtype"); // 单据类型
			String str_busitype = billVO.getStringValue("busitype"); // 业务类型

			String str_sql = "select t1.processid,t2.code wfcode,t2.name wfname from pub_wf_prinstance t1,pub_wf_process t2 where t1.processid=t2.id and t1.id='" + str_wfinstance + "'"; //
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql); //
			String str_wfcode = hvs[0].getStringValue("wfcode"); //
			String str_wfname = hvs[0].getStringValue("wfname"); //

			StringBuffer sb_text = new StringBuffer(); //
			sb_text.append("单据类型:[" + str_billtype + "]\r\n"); //
			sb_text.append("业务类型:[" + str_busitype + "]\r\n"); //
			sb_text.append("流程编码:[" + str_wfcode + "]\r\n"); //
			sb_text.append("流程名称:[" + str_wfname + "]\r\n"); //
			MessageBox.showTextArea(this, "报表信息查看", sb_text.toString()); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	class MyTreeCellRender extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = -4155023337948316932L;
		private Color color1 = new Color(100, 100, 100);
		private Icon icon1 = UIUtil.getImage("office_034.gif"); //已提交的
		private Icon icon2 = UIUtil.getImage("office_138.gif"); //没提交的
		private Icon iconChildWF = UIUtil.getImage("user.gif"); //子流程

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value; //
			HashVO hvo = (HashVO) node.getUserObject(); //
			boolean isSubmit = hvo.getBooleanValue("issubmit", false); //
			String str_parentInstanceId = hvo.getStringValue("parentinstanceid"); //父亲实例,如果不为空,则说明是子流程
			String str_text = value.toString(); //
			JLabel label = new JLabel(str_text); //

			if (str_parentInstanceId != null && !str_parentInstanceId.trim().equals("")) { //如果是子流程,为了强烈区分会办效果,所有子流程的图标不一样!!
				label.setIcon(iconChildWF); //
			} else {
				if (isSubmit) { //如果已提交
					label.setIcon(icon1); //
				} else {
					label.setIcon(icon2);
				}
			}

			if (sel) { //如果选中!!
				label.setOpaque(true); //不透明!!
				label.setBackground(Color.YELLOW); //
				if (isSubmit) { //如果已提交
					label.setForeground(color1); //
				} else {
					label.setForeground(Color.RED); //
				}
			} else {
				label.setOpaque(false); //透明!!
				if (isSubmit) { //如果已提交
					label.setForeground(color1); //
				} else {
					label.setForeground(Color.RED); //
				}
			}
			return label; //
		}
	}

	class MyTreeCellRender2 extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 6389753277952605261L;
		private Icon icon_root = UIUtil.getImage("office_030.gif"); //目录结点
		private Icon icon_dir1 = UIUtil.getImage("office_123.gif"); //机构
		private Icon icon_dir2 = UIUtil.getImage("office_113.gif"); //环节

		private Icon icon1 = UIUtil.getImage("office_021.gif"); //已提交的
		private Icon icon2 = UIUtil.getImage("office_043.gif"); //没提交的
		private Color color1 = new Color(100, 100, 100); //
		private Color color_user = new Color(0, 100, 0);

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value; //
			Object obj = node.getUserObject(); //
			String str_text = value.toString(); //
			JLabel label = new JLabel(str_text); //

			if (obj instanceof String) { //目录结点
				label.setForeground(color1); //
				if (node.isRoot()) { //根结点
					label.setIcon(icon_root); //
				} else {
					if (node.getLevel() == 1) {
						label.setIcon(icon_dir1); //
					} else if (node.getLevel() == 2) {
						label.setIcon(icon_dir2); //
					}
				}

				if (sel) { //如果选中!!
					label.setOpaque(true); //不透明!!
					label.setBackground(Color.YELLOW); //
				} else {
					label.setOpaque(false); //透明!!
				}
			} else if (obj instanceof HashVO) {
				HashVO hvo = (HashVO) obj; //
				boolean isSubmit = hvo.getBooleanValue("issubmit", false); //
				if (sel) { //如果选中!!
					label.setOpaque(true); //不透明!!
					label.setBackground(Color.YELLOW); //
					if (isSubmit) { //如果已提交
						label.setForeground(color_user); //
						label.setIcon(icon1); //
					} else {
						label.setIcon(icon2); //
						label.setForeground(Color.RED); //
					}
				} else { //如果没选中
					label.setOpaque(false); //透明!!
					if (isSubmit) { //如果已提交
						label.setIcon(icon1); //
						label.setForeground(color_user); //
					} else {
						label.setIcon(icon2); //
						label.setForeground(Color.RED); //
					}
				}
			}

			return label; //
		}
	}

}
