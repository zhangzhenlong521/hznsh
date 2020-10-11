package cn.com.infostrategy.ui.workflow.msg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
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
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTreeUI;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessFrame;

/**
 * 消息中心传阅面板 【杨科/2012-11-28】
 */

public class MessageShowWKPanel extends AbstractWorkPanel implements TreeSelectionListener, BillListHtmlHrefListener, ActionListener{
	private WLTButton refreshBtn = null;
	private JTree classTree = null; //分类树!
	private BillListPanel billListPanel, opinionListPanel = null;
	private JPanel rightContentPanel = new JPanel(); 
	private WLTSplitPane splitPane = null; 
	private DefaultMutableTreeNode rootNode, generalNode, unreadPRNode, readedPRNode, sendPRNode, workFlowNode, unreadWFNode, readedWFNode, sendWFNode; 
	private TBUtil tbUtil = new TBUtil();
	private String unreadsql, readedsql, sendsql = null;
	private WLTButton delete, select, opinion = null;
	
	public MessageShowWKPanel() {
		initialize(); 
	}
	
	public void initialize() {
		this.setLayout(new BorderLayout());
		
		refreshBtn = new WLTButton("刷新"); 
		refreshBtn.addActionListener(this); 
		
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)); 
		btnPanel.setBackground(LookAndFeel.defaultShadeColor1); 
		btnPanel.add(refreshBtn); 
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(btnPanel, BorderLayout.NORTH); 
		leftPanel.add(new JScrollPane(getClassTree()), BorderLayout.CENTER); 
		
		
		billListPanel = new BillListPanel("PUB_MESSAGE_PASSREAD");	
		billListPanel.QueryDataByCondition(" 1=2 ");
		billListPanel.addBillListHtmlHrefListener(this);
		
		select = WLTButton.createButtonByType(WLTButton.COMM, "浏览");
		select.addActionListener(this);
		billListPanel.addBillListButton(select);
		billListPanel.repaintBillListButton();
		
		rightContentPanel.setLayout(new BorderLayout());
		rightContentPanel.add(billListPanel);
		
		splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, leftPanel, rightContentPanel); 
		splitPane.setDividerLocation(200); 
		this.add(splitPane); 
	}
	
	//分类树
	private JTree getClassTree() {
		if (classTree != null) {
			return classTree;
		}
		
		HashVO hvoRoot = new HashVO(); 
		hvoRoot.setAttributeValue("$NodeType", "0"); //根结点
		hvoRoot.setAttributeValue("text", "传阅消息"); 
		hvoRoot.setAttributeValue("count", "0"); 
		rootNode = new DefaultMutableTreeNode(hvoRoot); 
		
		HashVO hvoGeneral = new HashVO(); 
		hvoGeneral.setAttributeValue("$NodeType", "1"); //根结点
		hvoGeneral.setAttributeValue("text", "普通传阅"); 
		hvoGeneral.setAttributeValue("count", "0"); 
		generalNode = new DefaultMutableTreeNode(hvoGeneral); 

		HashVO hvoUnreadPR = new HashVO(); 
		hvoUnreadPR.setAttributeValue("$NodeType", "2"); 
		hvoUnreadPR.setAttributeValue("text", "待阅消息"); 
		hvoUnreadPR.setAttributeValue("count", "0"); 
		unreadPRNode = new DefaultMutableTreeNode(hvoUnreadPR); 
		
		HashVO hvoReadedPR = new HashVO(); 
		hvoReadedPR.setAttributeValue("$NodeType", "3"); 
		hvoReadedPR.setAttributeValue("text", "已阅消息"); 
		hvoReadedPR.setAttributeValue("count", "0"); 
		readedPRNode = new DefaultMutableTreeNode(hvoReadedPR); 
		
		HashVO hvoSendPR = new HashVO(); 
		hvoSendPR.setAttributeValue("$NodeType", "4"); 
		hvoSendPR.setAttributeValue("text", "我的传阅"); 
		hvoSendPR.setAttributeValue("count", "0"); 
		sendPRNode = new DefaultMutableTreeNode(hvoSendPR); 
		
		HashVO hvoWorkFlow = new HashVO(); 
		hvoWorkFlow.setAttributeValue("$NodeType", "1"); //根结点
		hvoWorkFlow.setAttributeValue("text", "工作流传阅"); 
		hvoWorkFlow.setAttributeValue("count", "0"); 
		workFlowNode = new DefaultMutableTreeNode(hvoWorkFlow); 

		HashVO hvoUnreadWF = new HashVO(); 
		hvoUnreadWF.setAttributeValue("$NodeType", "2"); 
		hvoUnreadWF.setAttributeValue("text", "待阅消息"); 
		hvoUnreadWF.setAttributeValue("count", "0"); 
		unreadWFNode = new DefaultMutableTreeNode(hvoUnreadWF); 
		
		HashVO hvoReadedWF = new HashVO(); 
		hvoReadedWF.setAttributeValue("$NodeType", "3"); 
		hvoReadedWF.setAttributeValue("text", "已阅消息"); 
		hvoReadedWF.setAttributeValue("count", "0"); 
		readedWFNode = new DefaultMutableTreeNode(hvoReadedWF); 
		
		HashVO hvoSendWF = new HashVO(); 
		hvoSendWF.setAttributeValue("$NodeType", "4"); 
		hvoSendWF.setAttributeValue("text", "我的传阅"); 
		hvoSendWF.setAttributeValue("count", "0"); 
		sendWFNode = new DefaultMutableTreeNode(hvoSendWF); 
		
		rootNode.add(generalNode);
		generalNode.add(unreadPRNode);
		generalNode.add(readedPRNode);
		generalNode.add(sendPRNode);
		
		rootNode.add(workFlowNode);
		workFlowNode.add(unreadWFNode);
		workFlowNode.add(readedWFNode);
		workFlowNode.add(sendWFNode);

		loadTaskNode(); //加载数据!!!

		classTree = new JTree(rootNode); //消息中心
		classTree.setUI(new WLTTreeUI(true)); 
		classTree.setOpaque(false);
		classTree.setRowHeight(19); 
		classTree.setCellRenderer(new MyTreeCellRender()); 
		classTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //只能单选!

		expandAll(classTree, new TreePath(rootNode), true); //展开全部!
		expandAll(classTree, new TreePath(readedPRNode.getPath()), false); 
		expandAll(classTree, new TreePath(sendPRNode.getPath()), false); 
		expandAll(classTree, new TreePath(workFlowNode.getPath()), false); //再将工作流传阅收起来!
		
		setSelectNode(classTree, rootNode);
		classTree.getSelectionModel().addTreeSelectionListener(this); 
		return classTree; 
	}
	
	//加载分类树数据
	private void loadTaskNode() {
		try {
			HashMap parMap = new HashMap(); 
			parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			parMap.put("corpid", ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
			parMap.put("roleids", ClientEnvironment.getCurrLoginUserVO().getAllRoleIds());
			//两次远程调用合成一次
			parMap.put("待阅SQL", unreadsql);
			parMap.put("已阅SQL", readedsql);
			parMap.put("传阅SQL", sendsql);
			
			HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.msg.MessageBSUtil", "getPassReadClassCountMap", parMap); 
			
			if(unreadsql==null||unreadsql.equals("")){
				unreadsql = (String) classCountMap.get("待阅SQL");
				readedsql = (String) classCountMap.get("已阅SQL");
				sendsql = (String) classCountMap.get("传阅SQL");
			}
			
			String[] keys = {"普通待阅消息", "普通已阅消息", "普通传阅消息", "工作流待阅消息", "工作流已阅消息", "工作流传阅消息"}; 
			DefaultMutableTreeNode[] nodes = {unreadPRNode, readedPRNode, sendPRNode, unreadWFNode, readedWFNode, sendWFNode};
			for (int i = 0; i < keys.length; i++) {
				HashMap hm = (HashMap) classCountMap.get(keys[i]); 
				if (hm != null) { 
					addItemNode(hm, nodes[i], "1"+i); 
				} else {
					((HashVO) nodes[i].getUserObject()).setAttributeValue("count", "0"); 
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); 
		}
	}
	
	//处理树节点
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
					modelDirVO.setAttributeValue("$NodeType", "1"); //是目录结点
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

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == refreshBtn) {
			onRefresh(); 
		} else if (e.getSource() == select) { 
			if(billListPanel.getSelectedBillVO() == null) {
				MessageBox.show(billListPanel, "请选择需要浏览的消息!");
				return;
			} else {
				selected();
				readed();
			}
		}
		
	}
	
	//列表表单浏览
	private void selected(){
    	String id = billListPanel.getSelectedBillVO().getStringValue("id");
    	String templetcode = billListPanel.getSelectedBillVO().getStringValue("templetcode");
    	String pkey = billListPanel.getSelectedBillVO().getStringValue("pkey");
    	String pvalue = billListPanel.getSelectedBillVO().getStringValue("pvalue");	
    	
    	BillCardPanel billCardPanel = new BillCardPanel(templetcode);	
    	Pub_Templet_1VO templetVO = billCardPanel.getTempletVO();
    	templetVO.setDataconstraint("");
    	billCardPanel.setTempletVO(templetVO);
    	billCardPanel.queryDataByCondition(pkey + "=" +pvalue);
		
    	JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFocusable(false);
		
		String title = billListPanel.getTitleLabel().getText();
		if(title.equals("※工作流待阅消息")||title.equals("※工作流已阅消息")||title.equals("※工作流传阅消息")){
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
    	}else{
    		tabbedPane.addTab("消息内容", billCardPanel);
    	}
		
		BillCardPanel passReadPanel = new BillCardPanel("PUB_MESSAGE_BTN");
		passReadPanel.setEditable("opinion", true);
		CardCPanel_ChildTable opinionListPanel = (CardCPanel_ChildTable)passReadPanel.getCompentByKey("opinion");
		opinionListPanel.getBtn_edit().setVisible(false);
		opinionListPanel.getBtn_delete().setVisible(false);
		passReadPanel.queryDataByCondition( " id =" +id);
		tabbedPane.addTab("传阅信息", passReadPanel);

		BillDialog dialog = new BillDialog(billListPanel, "消息内容", 800, 600);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(tabbedPane, "Center");
        dialog.setVisible(true);	
	}
	
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		try {    	
			String messageHtmlClass = tbUtil.getSysOptionStringValue("收藏夹传阅红头文件关联类", "");
			MessageShowHtml messageShowHtml = (MessageShowHtml) Class.forName(messageHtmlClass).newInstance();
			String templetcode = billListPanel.getSelectedBillVO().getStringValue("templetcode");
    	    String pvalue = billListPanel.getSelectedBillVO().getStringValue("pvalue");	
			if(messageShowHtml.onBillListHtml(billListPanel, pvalue, templetcode)){
				selected();
				readed();			
			}
		} catch (Exception e) {
			selected();
			readed();
		}
	}
	
	//处理已读
	private void readed() {
		String title = billListPanel.getTitleLabel().getText();
		if(title.equals("※普通待阅消息")||title.equals("※工作流待阅消息")){
			String msgid = billListPanel.getSelectedBillVO().getStringValue("id");
			String msgtitle = billListPanel.getSelectedBillVO().getStringValue("msgtitle");
			String sender = billListPanel.getSelectedBillVO().getStringValue("sender");
			String senddate = billListPanel.getSelectedBillVO().getStringValue("senddate");	
			String templetcode = billListPanel.getSelectedBillVO().getStringValue("templetcode");
					
		    try {
				String newid = UIUtil.getSequenceNextValByDS(null, "s_pub_message_readed");
				UIUtil.executeUpdateByDS(null, new InsertSQLBuilder("pub_message_readed", new String[][] {
						{ "id", newid }, 
						{ "msgid", msgid }, 
						{ "msgtitle", msgtitle }, 
						{ "sender", sender }, 
						{ "senddate", senddate }, 
						{ "userid", ClientEnvironment.getCurrLoginUserVO().getId() }, 
						{ "readtime", UIUtil.getCurrTime() }, 
						{ "isdelete", "N" } 
						}).getSQL());
			} catch (Exception e) {
				e.printStackTrace();
			}
			readRefresh(title, templetcode);
		}	 
	}
	
	//已读刷新
	private void readRefresh(String title, String templetcode){
		unreadPRNode.removeAllChildren(); 
		readedPRNode.removeAllChildren(); 
		sendPRNode.removeAllChildren(); 
		unreadWFNode.removeAllChildren(); 
		readedWFNode.removeAllChildren(); 
		sendWFNode.removeAllChildren(); 

		loadTaskNode(); //加载所有数据..

		if(title.equals("※普通待阅消息")){
			expandAll(classTree, new TreePath(rootNode), true); //展开全部!
			expandAll(classTree, new TreePath(readedPRNode.getPath()), false); 
			expandAll(classTree, new TreePath(sendPRNode.getPath()), false); 
			expandAll(classTree, new TreePath(workFlowNode.getPath()), false); //再将工作流传阅收起来!
		}else{
			expandAll(classTree, new TreePath(rootNode), true); //展开全部!
			expandAll(classTree, new TreePath(readedWFNode.getPath()), false); 
			expandAll(classTree, new TreePath(sendWFNode.getPath()), false); 
			expandAll(classTree, new TreePath(generalNode.getPath()), false); //再将工作流传阅收起来!			
		}

		classTree.setUI(new WLTTreeUI(true)); //必须要做一下,否则页面刷新不过来!!!
		classTree.validate(); 
		classTree.repaint(); 
		
		if(title.equals("※普通待阅消息")){
			setSelectNode(classTree, unreadPRNode);
			billListPanel.QueryDataByCondition(" templetcode='"+templetcode+"' and msgtype='普通传阅消息' and " + unreadsql);	
		}else{
			setSelectNode(classTree, unreadWFNode);
			billListPanel.QueryDataByCondition(" templetcode='"+templetcode+"' and msgtype='工作流传阅消息' and " + unreadsql);
		}
		
		rightContentPanel.removeAll(); 
		rightContentPanel.setLayout(new BorderLayout()); 
		rightContentPanel.add(billListPanel); 
		rightContentPanel.updateUI(); //必须要做一下!!
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode treeNode = getSelectedNode();
		 
		if(treeNode==null||treeNode.isRoot()){
			return;
		}
		
		HashVO hvo = (HashVO) treeNode.getUserObject(); 
		String str_nodeType = hvo.getStringValue("$NodeType");
		String templetcode = hvo.getStringValue("templetcode");
		
		String[] keys = {"普通待阅消息", "普通已阅消息", "普通传阅消息", "工作流待阅消息", "工作流已阅消息", "工作流传阅消息"}; 
		String[] msgtypes = {"普通传阅消息", "普通传阅消息", "普通传阅消息", "工作流传阅消息", "工作流传阅消息", "工作流传阅消息"}; 
		String[] sqls = {unreadsql, readedsql, sendsql, unreadsql, readedsql, sendsql};
		for (int i = 0; i < keys.length; i++) {
			if (str_nodeType.equals("1"+i)){
				billListPanel.setTitleLabelText(keys[i]);
				if(templetcode==null&&templetcode.equals("")){
					billListPanel.QueryDataByCondition(" 1=2 ");		
				}else{
					billListPanel.QueryDataByCondition(" templetcode='"+hvo.getStringValue("templetcode")+"' and msgtype='" + 
							msgtypes[i] + "' and " + sqls[i]);	
				}
			}			
		}
	}
	
	//分类树刷新
	private void onRefresh() {	
		unreadPRNode.removeAllChildren(); 
		readedPRNode.removeAllChildren(); 
		sendPRNode.removeAllChildren(); 
		unreadWFNode.removeAllChildren(); 
		readedWFNode.removeAllChildren(); 
		sendWFNode.removeAllChildren(); 

		loadTaskNode(); //加载所有数据..

		expandAll(classTree, new TreePath(rootNode), true); //展开全部!
		expandAll(classTree, new TreePath(readedPRNode.getPath()), false); 
		expandAll(classTree, new TreePath(sendPRNode.getPath()), false); 
		expandAll(classTree, new TreePath(workFlowNode.getPath()), false); //再将工作流传阅收起来!

		classTree.setUI(new WLTTreeUI(true)); //必须要做一下,否则页面刷新不过来!!!
		classTree.validate(); 
		classTree.repaint(); 
		
		setSelectNode(classTree, rootNode);
		billListPanel.QueryDataByCondition(" 1=2 ");
		
		rightContentPanel.removeAll(); 
		rightContentPanel.setLayout(new BorderLayout()); 
		rightContentPanel.add(billListPanel); 
		rightContentPanel.updateUI(); //必须要做一下!!
	}
	
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
	
	private DefaultMutableTreeNode getSelectedNode() {
		TreePath path = classTree.getSelectionPath();
		if (path == null) {
			return null;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
		return node;
	}
	
	private void setSelectNode(JTree _tree, TreeNode _node) {
		if (_node == null) {
			return; 
		}
		DefaultTreeModel model = (DefaultTreeModel) _tree.getModel();
		TreeNode[] nodes = model.getPathToRoot(_node);
		TreePath path = new TreePath(nodes); 
		_tree.setSelectionPath(path); 
	}
	
	class MyTreeCellRender extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = 5500760588674948602L;
		private Icon icon_dir = UIUtil.getImage("office_169.gif");
		private Icon icon_dir11 = UIUtil.getImage("zt_062.gif"); //待阅
		private Icon icon_dir12 = UIUtil.getImage("zt_063.gif"); //已阅
		private Icon icon_dir13 = UIUtil.getImage("office_039.gif"); //传阅

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			HashVO hvo = (HashVO) ((DefaultMutableTreeNode) value).getUserObject(); 
			int li_type = hvo.getIntegerValue("$NodeType"); 
			String str_text = hvo.getStringValue("text"); 
			int li_count = hvo.getIntegerValue("count"); 
			if (li_count > 0) {
				str_text = str_text + "(" + li_count + ")";
			}
			JLabel label = new JLabel(str_text); 

			//设置图标!
			if (li_type == 0) {
				label.setIcon(this.getDefaultOpenIcon()); 
			} else if (li_type == 1) { //如果是目录
				if (expanded) {
					label.setIcon(icon_dir); 
				} else {
					label.setIcon(this.getDefaultClosedIcon()); 
				}
			} else if (li_type == 2) {
				if (expanded) {
					label.setIcon(icon_dir); 
				} else {
					label.setIcon(icon_dir11); 
				}
			} else if (li_type == 3) { 
				if (expanded) {
					label.setIcon(icon_dir); 
				} else {
					label.setIcon(icon_dir12); 
				}
			} else if (li_type == 4) { 
				if (expanded) {
					label.setIcon(icon_dir); 
				} else {
					label.setIcon(icon_dir13); 
				}
			} else {
				label.setIcon(this.getDefaultClosedIcon()); 
			}
			
			//设置字体颜色!
			if (sel) {
				label.setOpaque(true); //如果选中的话,则不透明..
				label.setForeground(Color.RED); 
				label.setBackground(Color.YELLOW); 
			} else {
				label.setOpaque(false); //透明!
			}
			return label;
		}
	}
}
