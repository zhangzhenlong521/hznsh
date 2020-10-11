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
 * ��Ϣ���Ĵ������ �����/2012-11-28��
 */

public class MessageShowWKPanel extends AbstractWorkPanel implements TreeSelectionListener, BillListHtmlHrefListener, ActionListener{
	private WLTButton refreshBtn = null;
	private JTree classTree = null; //������!
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
		
		refreshBtn = new WLTButton("ˢ��"); 
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
		
		select = WLTButton.createButtonByType(WLTButton.COMM, "���");
		select.addActionListener(this);
		billListPanel.addBillListButton(select);
		billListPanel.repaintBillListButton();
		
		rightContentPanel.setLayout(new BorderLayout());
		rightContentPanel.add(billListPanel);
		
		splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, leftPanel, rightContentPanel); 
		splitPane.setDividerLocation(200); 
		this.add(splitPane); 
	}
	
	//������
	private JTree getClassTree() {
		if (classTree != null) {
			return classTree;
		}
		
		HashVO hvoRoot = new HashVO(); 
		hvoRoot.setAttributeValue("$NodeType", "0"); //�����
		hvoRoot.setAttributeValue("text", "������Ϣ"); 
		hvoRoot.setAttributeValue("count", "0"); 
		rootNode = new DefaultMutableTreeNode(hvoRoot); 
		
		HashVO hvoGeneral = new HashVO(); 
		hvoGeneral.setAttributeValue("$NodeType", "1"); //�����
		hvoGeneral.setAttributeValue("text", "��ͨ����"); 
		hvoGeneral.setAttributeValue("count", "0"); 
		generalNode = new DefaultMutableTreeNode(hvoGeneral); 

		HashVO hvoUnreadPR = new HashVO(); 
		hvoUnreadPR.setAttributeValue("$NodeType", "2"); 
		hvoUnreadPR.setAttributeValue("text", "������Ϣ"); 
		hvoUnreadPR.setAttributeValue("count", "0"); 
		unreadPRNode = new DefaultMutableTreeNode(hvoUnreadPR); 
		
		HashVO hvoReadedPR = new HashVO(); 
		hvoReadedPR.setAttributeValue("$NodeType", "3"); 
		hvoReadedPR.setAttributeValue("text", "������Ϣ"); 
		hvoReadedPR.setAttributeValue("count", "0"); 
		readedPRNode = new DefaultMutableTreeNode(hvoReadedPR); 
		
		HashVO hvoSendPR = new HashVO(); 
		hvoSendPR.setAttributeValue("$NodeType", "4"); 
		hvoSendPR.setAttributeValue("text", "�ҵĴ���"); 
		hvoSendPR.setAttributeValue("count", "0"); 
		sendPRNode = new DefaultMutableTreeNode(hvoSendPR); 
		
		HashVO hvoWorkFlow = new HashVO(); 
		hvoWorkFlow.setAttributeValue("$NodeType", "1"); //�����
		hvoWorkFlow.setAttributeValue("text", "����������"); 
		hvoWorkFlow.setAttributeValue("count", "0"); 
		workFlowNode = new DefaultMutableTreeNode(hvoWorkFlow); 

		HashVO hvoUnreadWF = new HashVO(); 
		hvoUnreadWF.setAttributeValue("$NodeType", "2"); 
		hvoUnreadWF.setAttributeValue("text", "������Ϣ"); 
		hvoUnreadWF.setAttributeValue("count", "0"); 
		unreadWFNode = new DefaultMutableTreeNode(hvoUnreadWF); 
		
		HashVO hvoReadedWF = new HashVO(); 
		hvoReadedWF.setAttributeValue("$NodeType", "3"); 
		hvoReadedWF.setAttributeValue("text", "������Ϣ"); 
		hvoReadedWF.setAttributeValue("count", "0"); 
		readedWFNode = new DefaultMutableTreeNode(hvoReadedWF); 
		
		HashVO hvoSendWF = new HashVO(); 
		hvoSendWF.setAttributeValue("$NodeType", "4"); 
		hvoSendWF.setAttributeValue("text", "�ҵĴ���"); 
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

		loadTaskNode(); //��������!!!

		classTree = new JTree(rootNode); //��Ϣ����
		classTree.setUI(new WLTTreeUI(true)); 
		classTree.setOpaque(false);
		classTree.setRowHeight(19); 
		classTree.setCellRenderer(new MyTreeCellRender()); 
		classTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //ֻ�ܵ�ѡ!

		expandAll(classTree, new TreePath(rootNode), true); //չ��ȫ��!
		expandAll(classTree, new TreePath(readedPRNode.getPath()), false); 
		expandAll(classTree, new TreePath(sendPRNode.getPath()), false); 
		expandAll(classTree, new TreePath(workFlowNode.getPath()), false); //�ٽ�����������������!
		
		setSelectNode(classTree, rootNode);
		classTree.getSelectionModel().addTreeSelectionListener(this); 
		return classTree; 
	}
	
	//���ط���������
	private void loadTaskNode() {
		try {
			HashMap parMap = new HashMap(); 
			parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			parMap.put("corpid", ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
			parMap.put("roleids", ClientEnvironment.getCurrLoginUserVO().getAllRoleIds());
			//����Զ�̵��úϳ�һ��
			parMap.put("����SQL", unreadsql);
			parMap.put("����SQL", readedsql);
			parMap.put("����SQL", sendsql);
			
			HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.msg.MessageBSUtil", "getPassReadClassCountMap", parMap); 
			
			if(unreadsql==null||unreadsql.equals("")){
				unreadsql = (String) classCountMap.get("����SQL");
				readedsql = (String) classCountMap.get("����SQL");
				sendsql = (String) classCountMap.get("����SQL");
			}
			
			String[] keys = {"��ͨ������Ϣ", "��ͨ������Ϣ", "��ͨ������Ϣ", "������������Ϣ", "������������Ϣ", "������������Ϣ"}; 
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
	
	//�������ڵ�
	private void addItemNode(HashMap _returnMap, DefaultMutableTreeNode _parentNode, String _nodeType) {
		String[] str_keys = (String[]) _returnMap.keySet().toArray(new String[0]); //�õ����е�key
		int li_allCount = 0; 
		HashVO[] allItemVOs = new HashVO[str_keys.length]; 
		for (int i = 0; i < str_keys.length; i++) {
			String str_templetCode = str_keys[i]; 
			String[] str_values = (String[]) _returnMap.get(str_keys[i]); 
			String str_templetName = str_values[0]; 
			String str_count = str_values[1]; 
			allItemVOs[i] = new HashVO(); 
			allItemVOs[i].setAttributeValue("$NodeType", _nodeType); //��������֮ĳģ��!
			allItemVOs[i].setAttributeValue("templetcode", str_templetCode); //ģ�����!
			allItemVOs[i].setAttributeValue("templetname", str_templetName); //ģ������
			allItemVOs[i].setAttributeValue("text", (str_templetName == null ? str_templetCode : str_templetName)); //��ʾ������
			allItemVOs[i].setAttributeValue("count", str_count); 
			li_allCount = li_allCount + Integer.parseInt(str_count); 
		}

		//Ϊ��˳������������������!!!�Ժ���� �ᰴָ����˳������!!!
		tbUtil.sortHashVOs(allItemVOs, new String[][] { { "templetname", "N", "N" } }); 

		//�ʴ���Ŀ���и�����,���Ǿ��ô��������е��ӽ��̫��,���ټ�һ��,ʹ��һ��Ŀ¼ģ��!��Ϊ����!
		//���кܶ��ַ���,������뻹��ʹ��һ��ȫ�ֲ���,����!!
		String str_df = TBUtil.getTBUtil().getSysOptionStringValue("��������������Ŀ¼���ඨ��", null); 
		HashMap map = null; 
		String[] str_dfdirkeys = null; 
		if (str_df != null && !str_df.trim().equals("")) { 
			map = new HashMap(); 
			str_df = TBUtil.getTBUtil().replaceAll(str_df, " ", ""); 
			str_df = TBUtil.getTBUtil().replaceAll(str_df, "\r", ""); 
			str_df = TBUtil.getTBUtil().replaceAll(str_df, "\n", ""); 
			HashMap initMap = TBUtil.getTBUtil().convertStrToMapByExpress(str_df, ";", "="); 
			str_dfdirkeys = (String[]) initMap.keySet().toArray(new String[0]); //���ж����Ŀ¼
			for (int i = 0; i < str_dfdirkeys.length; i++) {
				String str_models = (String) initMap.get(str_dfdirkeys[i]); 
				String[] str_modelArray = TBUtil.getTBUtil().split(str_models, ","); 
				for (int j = 0; j < str_modelArray.length; j++) {
					map.put(str_modelArray[j], str_dfdirkeys[i]); 
				}
			}
		}

		HashMap alreadyAddedDirNodeMap = new HashMap(); //������¼������������Ŀ¼���!!!
		ArrayList al_onlyLeafNode = new ArrayList(); 
		for (int i = 0; i < allItemVOs.length; i++) {
			String str_thisNodeText = allItemVOs[i].toString();
			if (map != null && map.containsKey(str_thisNodeText)) { //��������ұ�ָ����һ��Ŀ¼,���ȴ���Ŀ¼!
				String str_dirName = (String) map.get(str_thisNodeText); 
				if (!alreadyAddedDirNodeMap.containsKey(str_dirName)) { //�����û�Ǽ�,���ȵ�¼һ��,����ͬ��Ŀ¼ֻ��һ��!
					HashVO modelDirVO = new HashVO(); 
					modelDirVO.setAttributeValue("$NodeType", "1"); //��Ŀ¼���
					modelDirVO.setAttributeValue("text", str_dirName); 
					modelDirVO.setAttributeValue("count", "0"); 
					modelDirVO.setToStringFieldName("text"); 
					DefaultMutableTreeNode addDirNode = new DefaultMutableTreeNode(modelDirVO); 
					alreadyAddedDirNodeMap.put(str_dirName, addDirNode); 
				}

				DefaultMutableTreeNode dirNode = (DefaultMutableTreeNode) alreadyAddedDirNodeMap.get(str_dirName); //Ŀ¼���!
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(allItemVOs[i]); //�����
				dirNode.add(node); //Ŀ¼���������ϱ����
			} else {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(allItemVOs[i]); 
				al_onlyLeafNode.add(node); 
			}
		}

		//������Ŀ¼�Ľ��,���밴ָ�����Ⱥ�˳������!
		String[] str_realdirs = (String[]) alreadyAddedDirNodeMap.keySet().toArray(new String[0]); 
		if (str_realdirs != null && str_realdirs.length > 0) {
			TBUtil.getTBUtil().sortStrsByOrders(str_realdirs, str_dfdirkeys); 
			for (int i = 0; i < str_realdirs.length; i++) { 
				DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) alreadyAddedDirNodeMap.get(str_realdirs[i]); 
				_parentNode.add(tmpNode); //��Ŀ¼������!!
			}
		}

		//������Щû��Ŀ¼�Ľ��!
		for (int i = 0; i < al_onlyLeafNode.size(); i++) {
			DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) al_onlyLeafNode.get(i); 
			_parentNode.add(tmpNode); //��Ŀ¼������!!
		}

		HashVO hvo = (HashVO) _parentNode.getUserObject(); 
		hvo.setAttributeValue("count", "" + li_allCount); //���ø��׽�������
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == refreshBtn) {
			onRefresh(); 
		} else if (e.getSource() == select) { 
			if(billListPanel.getSelectedBillVO() == null) {
				MessageBox.show(billListPanel, "��ѡ����Ҫ�������Ϣ!");
				return;
			} else {
				selected();
				readed();
			}
		}
		
	}
	
	//�б�����
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
		if(title.equals("��������������Ϣ")||title.equals("��������������Ϣ")||title.equals("��������������Ϣ")){
	    	String taskid = billListPanel.getSelectedBillVO().getStringValue("taskid");
	    	String dealpoolid = billListPanel.getSelectedBillVO().getStringValue("dealpoolid");
	    	String prinstanceid = billListPanel.getSelectedBillVO().getStringValue("prinstanceid");
	    	
        	WorkFlowProcessFrame processFrame = new WorkFlowProcessFrame(this, "", billCardPanel, null, taskid, dealpoolid, prinstanceid, true, "�������������״̬,���Բ��ܽ����ύ�Ȳ�����"); 
    		processFrame.getWfProcessPanel().getBtn_Confirm().setVisible(false); //����ȷ�ϰ�ť
    		processFrame.getWfProcessPanel().getBtn_calcel().setVisible(false); //����ȡ����ť
    		processFrame.getWfProcessPanel().getBtn_mind().setVisible(false); //���ش߶��찴ť
    		//processFrame.setVisible(true); 
    		//processFrame.toFront();	
    		
    		tabbedPane.addTab("��Ϣ����", processFrame.getWfProcessPanel());		
    	}else{
    		tabbedPane.addTab("��Ϣ����", billCardPanel);
    	}
		
		BillCardPanel passReadPanel = new BillCardPanel("PUB_MESSAGE_BTN");
		passReadPanel.setEditable("opinion", true);
		CardCPanel_ChildTable opinionListPanel = (CardCPanel_ChildTable)passReadPanel.getCompentByKey("opinion");
		opinionListPanel.getBtn_edit().setVisible(false);
		opinionListPanel.getBtn_delete().setVisible(false);
		passReadPanel.queryDataByCondition( " id =" +id);
		tabbedPane.addTab("������Ϣ", passReadPanel);

		BillDialog dialog = new BillDialog(billListPanel, "��Ϣ����", 800, 600);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(tabbedPane, "Center");
        dialog.setVisible(true);	
	}
	
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		try {    	
			String messageHtmlClass = tbUtil.getSysOptionStringValue("�ղؼд��ĺ�ͷ�ļ�������", "");
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
	
	//�����Ѷ�
	private void readed() {
		String title = billListPanel.getTitleLabel().getText();
		if(title.equals("����ͨ������Ϣ")||title.equals("��������������Ϣ")){
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
	
	//�Ѷ�ˢ��
	private void readRefresh(String title, String templetcode){
		unreadPRNode.removeAllChildren(); 
		readedPRNode.removeAllChildren(); 
		sendPRNode.removeAllChildren(); 
		unreadWFNode.removeAllChildren(); 
		readedWFNode.removeAllChildren(); 
		sendWFNode.removeAllChildren(); 

		loadTaskNode(); //������������..

		if(title.equals("����ͨ������Ϣ")){
			expandAll(classTree, new TreePath(rootNode), true); //չ��ȫ��!
			expandAll(classTree, new TreePath(readedPRNode.getPath()), false); 
			expandAll(classTree, new TreePath(sendPRNode.getPath()), false); 
			expandAll(classTree, new TreePath(workFlowNode.getPath()), false); //�ٽ�����������������!
		}else{
			expandAll(classTree, new TreePath(rootNode), true); //չ��ȫ��!
			expandAll(classTree, new TreePath(readedWFNode.getPath()), false); 
			expandAll(classTree, new TreePath(sendWFNode.getPath()), false); 
			expandAll(classTree, new TreePath(generalNode.getPath()), false); //�ٽ�����������������!			
		}

		classTree.setUI(new WLTTreeUI(true)); //����Ҫ��һ��,����ҳ��ˢ�²�����!!!
		classTree.validate(); 
		classTree.repaint(); 
		
		if(title.equals("����ͨ������Ϣ")){
			setSelectNode(classTree, unreadPRNode);
			billListPanel.QueryDataByCondition(" templetcode='"+templetcode+"' and msgtype='��ͨ������Ϣ' and " + unreadsql);	
		}else{
			setSelectNode(classTree, unreadWFNode);
			billListPanel.QueryDataByCondition(" templetcode='"+templetcode+"' and msgtype='������������Ϣ' and " + unreadsql);
		}
		
		rightContentPanel.removeAll(); 
		rightContentPanel.setLayout(new BorderLayout()); 
		rightContentPanel.add(billListPanel); 
		rightContentPanel.updateUI(); //����Ҫ��һ��!!
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode treeNode = getSelectedNode();
		 
		if(treeNode==null||treeNode.isRoot()){
			return;
		}
		
		HashVO hvo = (HashVO) treeNode.getUserObject(); 
		String str_nodeType = hvo.getStringValue("$NodeType");
		String templetcode = hvo.getStringValue("templetcode");
		
		String[] keys = {"��ͨ������Ϣ", "��ͨ������Ϣ", "��ͨ������Ϣ", "������������Ϣ", "������������Ϣ", "������������Ϣ"}; 
		String[] msgtypes = {"��ͨ������Ϣ", "��ͨ������Ϣ", "��ͨ������Ϣ", "������������Ϣ", "������������Ϣ", "������������Ϣ"}; 
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
	
	//������ˢ��
	private void onRefresh() {	
		unreadPRNode.removeAllChildren(); 
		readedPRNode.removeAllChildren(); 
		sendPRNode.removeAllChildren(); 
		unreadWFNode.removeAllChildren(); 
		readedWFNode.removeAllChildren(); 
		sendWFNode.removeAllChildren(); 

		loadTaskNode(); //������������..

		expandAll(classTree, new TreePath(rootNode), true); //չ��ȫ��!
		expandAll(classTree, new TreePath(readedPRNode.getPath()), false); 
		expandAll(classTree, new TreePath(sendPRNode.getPath()), false); 
		expandAll(classTree, new TreePath(workFlowNode.getPath()), false); //�ٽ�����������������!

		classTree.setUI(new WLTTreeUI(true)); //����Ҫ��һ��,����ҳ��ˢ�²�����!!!
		classTree.validate(); 
		classTree.repaint(); 
		
		setSelectNode(classTree, rootNode);
		billListPanel.QueryDataByCondition(" 1=2 ");
		
		rightContentPanel.removeAll(); 
		rightContentPanel.setLayout(new BorderLayout()); 
		rightContentPanel.add(billListPanel); 
		rightContentPanel.updateUI(); //����Ҫ��һ��!!
	}
	
	private void expandAll(JTree tree, TreePath _treePath, boolean _isExpand) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) _treePath.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = _treePath.pathByAddingChild(n); //ȡ���ӽ���·��
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
		private Icon icon_dir11 = UIUtil.getImage("zt_062.gif"); //����
		private Icon icon_dir12 = UIUtil.getImage("zt_063.gif"); //����
		private Icon icon_dir13 = UIUtil.getImage("office_039.gif"); //����

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			HashVO hvo = (HashVO) ((DefaultMutableTreeNode) value).getUserObject(); 
			int li_type = hvo.getIntegerValue("$NodeType"); 
			String str_text = hvo.getStringValue("text"); 
			int li_count = hvo.getIntegerValue("count"); 
			if (li_count > 0) {
				str_text = str_text + "(" + li_count + ")";
			}
			JLabel label = new JLabel(str_text); 

			//����ͼ��!
			if (li_type == 0) {
				label.setIcon(this.getDefaultOpenIcon()); 
			} else if (li_type == 1) { //�����Ŀ¼
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
			
			//����������ɫ!
			if (sel) {
				label.setOpaque(true); //���ѡ�еĻ�,��͸��..
				label.setForeground(Color.RED); 
				label.setBackground(Color.YELLOW); 
			} else {
				label.setOpaque(false); //͸��!
			}
			return label;
		}
	}
}
