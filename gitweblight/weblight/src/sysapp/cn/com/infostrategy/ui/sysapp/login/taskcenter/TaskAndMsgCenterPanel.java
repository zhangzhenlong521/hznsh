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
 * ��Ϣ�����������,�ؼ���!! �������AbstractWorkFlowStylePanel��ϵ������!! ����һЩ�߼�������ͨ��!
 * ���Ժ�Ĺ�������������ҳ���������ĵķ�ʽ��ʾ!!!
 * ���ڵ�������Ҫ���ǹ�����,�Ժ������Ӧ�ÿ������������͵�,������ϵ�ļ��ύ,���߹�����!!��Ҳ��Ҫ�ύ��ĳ����,Ȼ��������ٽ���ĳ�ִ���!!!
 * ��Ϣ��������Ϸ�Ϊ����,һ��������Ҫ���������ύ��,һ����ֻ��Ҫ�����߲鿴��(Ҳ�������ǳ���)!!
 * ����ǵ�һ��,����Ҫ�����ߴ����,��������ȫ�����Բ��ù�����!! ���͵���Ҳ���Բ�������������ģʽ����!!
 * 
 * ��ҳ��Ϣ����������ģʽ:
 * 1.ֻ��ҵ������޸�ĳ��״̬,Ȼ���¼��Ա����Ȩ���߼�ȥ��ѯ,����������ѯҵ�����߼�!
 * 2.�ڶ�ҵ�������߼�����ʱ,��pub_task_deal���в�������,������Ϣ���ı�������,Ȼ���¼��Ա�Ǵ����������в�ѯ! ����������ַ�Ϊ��Ҫ�����벻��Ҫ��������!!
 * ��һ��ģʽ���������Ź���һ���,��Ϊ���ʹ�õڶ���ģʽ,�򲻿��ܶ�ÿһ���˶�����һ������!!������ģʽ�����ĳһ����ɫ����Ϣ��!!
 * �ڶ���ģʽ�����Ե����������ĳһ���򼸸����Ե���Ա����Ϣ!! ��һ��һ����!!! �����ǽ�ɫ!! 
 * ��֮��һ�������Ź����͵���Ϣ���ڶ����Ǵ��������͵���Ϣ!! Ҫ����ʵ���������ѡ��!!
 * 
 * �������û�����Ҫһ��ר�ŵ���Ϣ���ı�,���͵���������,�������̽���ʱ��ĸ������˵�����! ������ϵͳ��Ϣ,����ϵͳ��ʲôʱ��������,ϵͳ�������°汾,ĳĳ��Ա����ϵͳ��!!
 * �����Ϣ���pub_msgcenter,���ֱ�����������Ҫ�ֶΣ�������,���ս�ɫ,���ջ�����Χ!! ����ʱ����ֱ�Ӹ�ĳ����,����ĳЩ��ɫ,��ĳ��������Χ�µ�ĳЩ��ɫ!
 * ���ڴ��ڸ�ĳЩ��ɫ�����,���Ի���Ҫһ�ű��¼ĳ����Ϣ�Ƿ��ѱ�ĳ���Ķ���,����"δ����Ϣ"��"�Ѷ���Ϣ"֮��!! �Ѷ���Ϣ������ pub_msgcenter_readed
 * @author xch
 *
 */
public class TaskAndMsgCenterPanel extends JPanel implements TreeSelectionListener, ActionListener, WorkFlowDealListener, BillListAfterQueryListener {

	private static final long serialVersionUID = 33442168166033497L;

	private boolean isSuperShow = false; //
	private JTree classTree = null; //������!
	private WLTButton refreshBtn, superShowBtn = null; //
	private DefaultMutableTreeNode rootNode, dealTaskNode, offTaskNode, bdTaskNode, unreadMsgNode, readedMsgNode, sendedMsgNode, msgRootNode; //

	private WorkflowUIUtil wfUIUtil = null; //
	private JPanel rightContentPanel = new JPanel(); //�ָ����ұߵ��������!!!
	private BillListPanel billList_allDealTask, billList_allOffTask, billList_allBDTask = null; //
	private HashMap billListPanelMap = new HashMap(); //Ϊ���������,����ѡ������б�ģ��,��ͳͳ��������,������ѡ��ʱ�ͻ᲻��ѯ��!!
	private HashSet ht_alreadyQuery = new HashSet(); //��¼�Ƿ�������ѯ��!
	private WLTSplitPane splitPane = null; //
	boolean isTriggerTreeSelectEvent = true; //�Ƿ񴥷�����ѡ���¼�!!!
	private JPopupMenu popMenu = null; //�����˵�!!
	private JMenuItem menuItem_1, menuItem_2, menuItem_3; //
	private TBUtil tbUtil = new TBUtil();
	private boolean ispopmsgpanel = true;
	private String templetCode = null;//����һ��ģ��������ֻ��ʾ�����̵�����
	private boolean isshowmsg = true;//�Ƿ���ʾ��Ϣ����
	private boolean isshowbd = true;//�Ƿ���ʾ�������
	private String bdinsql = null;

	//���� �����/2012-12-14��
	private boolean isshowPR = true;//�Ƿ���ʾ����
	private DefaultMutableTreeNode PRNode, generalNode, unreadPRNode, readedPRNode, sendPRNode, workFlowNode, unreadWFNode, readedWFNode, sendWFNode;
	private String unreadsql, readedsql, sendsql = null;
	private BillListPanel billListPanel, opinionListPanel = null;
	private WLTButton select = null;

	//����Զ�̵���ȡ���������� �����/2013-02-18��
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
		isSuperShow = _isSuperShow; //�Ƿ��ǹ������ҳ��!!
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
	 * ��ʼ��ҳ��
	 */
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		rightContentPanel.setLayout(new BorderLayout()); //
		billList_allDealTask = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_task_deal_CODE1.xml")); //����������б�
		rightContentPanel.add(billList_allDealTask); //
		isshowbd = TBUtil.getTBUtil().getSysOptionBooleanValue("�����������Ƿ���ʾ�������", false);
		JPanel leftPanel = new JPanel(new BorderLayout()); //
		refreshBtn = new WLTButton("ˢ��"); //
		refreshBtn.addActionListener(this); //

		superShowBtn = new WLTButton("��������"); //
		superShowBtn.addActionListener(this); //

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)); //
		btnPanel.setBackground(LookAndFeel.defaultShadeColor1); //
		btnPanel.add(refreshBtn); //���밴ť!
		if (ClientEnvironment.getInstance().isAdmin() && !isSuperShow) { //����ǹ���Ա���!!
			btnPanel.add(superShowBtn); //���밴ť!
		}

		leftPanel.add(btnPanel, BorderLayout.NORTH); //

		// ����������
		leftPanel.add(new JScrollPane(getClassTree()), BorderLayout.CENTER); //

		splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, leftPanel, rightContentPanel); //
		billList_allDealTask.queryDataByDS(null, getAllDealTaskSQL(null, isSuperShow)); //ֱ��ͨ��SQL����!!!
		billList_allDealTask.setTitleLabelText("�����������²�ѯʱ�䡾" + tbUtil.getCurrTime() + "��"); //
		billList_allDealTask.setIsRefreshParent("1"); //���ùر���ҳ�治ˢ�¸�����    Ԭ����  20130418
		dealTaskList();
		splitPane.setDividerLocation(200); //
		this.add(splitPane); //
	}

	//�����������Ĵ��������б�ģ����Ϊ�յ��� �����/2013-02-18��
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

	//�������������Ѱ������б�ģ����Ϊ�յ��� �����/2013-02-18��
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

	//��������������������б�ģ����Ϊ�յ��� �����/2013-02-18��
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
	 * ȡ�÷�����,ģ��FoxMail���շ�����
	 * @return
	 */
	private JTree getClassTree() {
		if (classTree != null) {
			return classTree;
		}
		HashVO hvoRoot = new HashVO(); //
		hvoRoot.setAttributeValue("$NodeType", "0"); //�����
		hvoRoot.setAttributeValue("text", "��������"); //
		hvoRoot.setAttributeValue("count", "0"); //
		rootNode = new DefaultMutableTreeNode(hvoRoot); //

		HashVO hvoDealTask = new HashVO(); //
		hvoDealTask.setAttributeValue("$NodeType", "1"); //�����
		hvoDealTask.setAttributeValue("text", "��������"); //
		hvoDealTask.setAttributeValue("count", "0"); //
		dealTaskNode = new DefaultMutableTreeNode(hvoDealTask); //

		HashVO hvoOffTask = new HashVO(); //
		hvoOffTask.setAttributeValue("$NodeType", "2"); //�����
		hvoOffTask.setAttributeValue("text", "�Ѱ�����"); //
		hvoOffTask.setAttributeValue("count", "0"); //
		offTaskNode = new DefaultMutableTreeNode(hvoOffTask); //
		rootNode.add(dealTaskNode); //
		rootNode.add(offTaskNode); //

		//�Ƿ���ʾ�������!
		if (isshowbd) {
			HashVO hvoBDTask = new HashVO();
			hvoBDTask.setAttributeValue("$NodeType", "9");
			hvoBDTask.setAttributeValue("text", "�������");
			hvoBDTask.setAttributeValue("count", "0");
			bdTaskNode = new DefaultMutableTreeNode(hvoBDTask);
			rootNode.add(bdTaskNode);
		}

		//�Ƿ���ʾ��Ϣ����!
		if (isshowmsg) {
			String str_df = TBUtil.getTBUtil().getSysOptionStringValue("��Ϣ���ĸ��˵�����", null); //
			HashVO hvoUnReadMsg = new HashVO();
			hvoUnReadMsg.setAttributeValue("$NodeType", "5");
			hvoUnReadMsg.setAttributeValue("text", "δ����Ϣ");
			hvoUnReadMsg.setAttributeValue("count", "0");
			unreadMsgNode = new DefaultMutableTreeNode(hvoUnReadMsg);

			HashVO hvoReadedMsg = new HashVO();
			hvoReadedMsg.setAttributeValue("$NodeType", "6");
			hvoReadedMsg.setAttributeValue("text", "�Ѷ���Ϣ");
			hvoReadedMsg.setAttributeValue("count", "0");
			readedMsgNode = new DefaultMutableTreeNode(hvoReadedMsg);

			HashVO hvoSendedMsg = new HashVO();
			hvoSendedMsg.setAttributeValue("$NodeType", "7");
			hvoSendedMsg.setAttributeValue("text", "�ѷ���Ϣ");
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

		//���� �����/2012-12-14��
		if (isshowPR) {
			HashVO PRRoot = new HashVO();
			PRRoot.setAttributeValue("$NodeType", "100"); //�����
			PRRoot.setAttributeValue("text", "������Ϣ");
			PRRoot.setAttributeValue("count", "0");
			PRNode = new DefaultMutableTreeNode(PRRoot);

			HashVO hvoGeneral = new HashVO();
			hvoGeneral.setAttributeValue("$NodeType", "100");
			hvoGeneral.setAttributeValue("text", "��ͨ����");
			hvoGeneral.setAttributeValue("count", "0");
			generalNode = new DefaultMutableTreeNode(hvoGeneral);

			HashVO hvoUnreadPR = new HashVO();
			hvoUnreadPR.setAttributeValue("$NodeType", "101");
			hvoUnreadPR.setAttributeValue("text", "������Ϣ");
			hvoUnreadPR.setAttributeValue("count", "0");
			unreadPRNode = new DefaultMutableTreeNode(hvoUnreadPR);

			HashVO hvoReadedPR = new HashVO();
			hvoReadedPR.setAttributeValue("$NodeType", "102");
			hvoReadedPR.setAttributeValue("text", "������Ϣ");
			hvoReadedPR.setAttributeValue("count", "0");
			readedPRNode = new DefaultMutableTreeNode(hvoReadedPR);

			HashVO hvoSendPR = new HashVO();
			hvoSendPR.setAttributeValue("$NodeType", "103");
			hvoSendPR.setAttributeValue("text", "�ҵĴ���");
			hvoSendPR.setAttributeValue("count", "0");
			sendPRNode = new DefaultMutableTreeNode(hvoSendPR);

			HashVO hvoWorkFlow = new HashVO();
			hvoWorkFlow.setAttributeValue("$NodeType", "100");
			hvoWorkFlow.setAttributeValue("text", "����������");
			hvoWorkFlow.setAttributeValue("count", "0");
			workFlowNode = new DefaultMutableTreeNode(hvoWorkFlow);

			HashVO hvoUnreadWF = new HashVO();
			hvoUnreadWF.setAttributeValue("$NodeType", "101");
			hvoUnreadWF.setAttributeValue("text", "������Ϣ");
			hvoUnreadWF.setAttributeValue("count", "0");
			unreadWFNode = new DefaultMutableTreeNode(hvoUnreadWF);

			HashVO hvoReadedWF = new HashVO();
			hvoReadedWF.setAttributeValue("$NodeType", "102");
			hvoReadedWF.setAttributeValue("text", "������Ϣ");
			hvoReadedWF.setAttributeValue("count", "0");
			readedWFNode = new DefaultMutableTreeNode(hvoReadedWF);

			HashVO hvoSendWF = new HashVO();
			hvoSendWF.setAttributeValue("$NodeType", "103");
			hvoSendWF.setAttributeValue("text", "�ҵĴ���");
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

		classTree = new JTree(rootNode); //"��������Ϣ"
		classTree.setUI(new WLTTreeUI(true)); //
		classTree.setOpaque(false);
		//classTree.setShowsRootHandles(true); //���ݽ��������û���Ǹ��Ӽ���ͼ��,��ʾ�Ƿ��ж���!!!!
		classTree.setRowHeight(19); //
		classTree.setCellRenderer(new MyTreeCellRender()); //
		classTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //ֻ�ܵ�ѡ!

		loadTaskNode(); //��������!!!

		expandAll(classTree, new TreePath(rootNode), false); //����ȫ��!
		expandAll(classTree, new TreePath(dealTaskNode.getPath()), true); //�ٽ��Ѱ���������!

		//���� �����/2012-12-14��
		//		if (isshowPR) {
		//			expandAll(classTree, new TreePath(unreadPRNode.getPath()), false); //�ٽ�����������!
		//			expandAll(classTree, new TreePath(readedPRNode.getPath()), false); //�ٽ�����������!
		//			expandAll(classTree, new TreePath(sendPRNode.getPath()), false); //�ٽ�����������!
		//			expandAll(classTree, new TreePath(unreadWFNode.getPath()), false); //�ٽ�����������!
		//			expandAll(classTree, new TreePath(readedWFNode.getPath()), false); //�ٽ�����������!
		//			expandAll(classTree, new TreePath(sendWFNode.getPath()), false); //�ٽ�����������!
		//		}

		setSelectNode(classTree, dealTaskNode); ////Ĭ��ѡ�д�������!
		classTree.getSelectionModel().addTreeSelectionListener(this); //�������Ӽ����¼�,�����ǰ������,������ѡ�д���������ʱ,���ܻ���ɴ���ѡ���¼�!!!

		classTree.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent _event) {
				super.mouseClicked(_event);
				if (_event.getButton() == MouseEvent.BUTTON3) { //������Ҽ�
					onShowPopMenu((JTree) _event.getSource(), _event.getX(), _event.getY()); //
				}
			}

		}); //
		return classTree; //

	}

	//���ش��������Ѱ������������!!
	private void loadTaskNode() {
		try {
			HashMap parMap = new HashMap(); //
			parMap.put("isSuperShow", new Boolean(isSuperShow)); //
			parMap.put("templetcode", templetCode);
			parMap.put("model", "dealTask");
			//			//���� �����/2012-12-14�� //�ŵ�lazyLoadPr��������
			//			if (isshowPR) {
			//				parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			//				parMap.put("corpid", ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
			//				parMap.put("roleids", ClientEnvironment.getCurrLoginUserVO().getAllRoleIds());
			//				//����Զ�̵��úϳ�һ��
			//				parMap.put("����SQL", unreadsql);
			//				parMap.put("����SQL", readedsql);
			//				parMap.put("����SQL", sendsql);
			//			}

			HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getTaskClassCountMap", parMap); //�ڷ�������ʵ�ֵ�!!

			// ��������
			dealTaskCountMap = (HashMap) classCountMap.get("��������"); //
			if (dealTaskCountMap != null) { //����д�������!!
				addTaskItemNode(dealTaskCountMap, dealTaskNode, "3"); //��������ĳģ����ϸ!
			} else {
				((HashVO) dealTaskNode.getUserObject()).setAttributeValue("count", "0"); //
			}

			//			 �Ѱ�����
			//						offTaskCountMap = (HashMap) classCountMap.get("�Ѱ�����"); //
			//						if (offTaskCountMap != null) { //������Ѱ�����!!
			//							addTaskItemNode(offTaskCountMap, offTaskNode, "4"); //�Ѱ�����ĳģ����ϸ!
			//						} else {
			//							((HashVO) offTaskNode.getUserObject()).setAttributeValue("count", "0"); //
			//						}
			lazyLoadOffTask();

			//�����ʾ�������
			if (isshowbd) {
				lazyLoadBd();
				//								bdMap = (HashMap) classCountMap.get("�������");
				//								if (bdMap != null) {
				//									addTaskItemNode(bdMap, bdTaskNode, "10");
				//								} else {
				//									((HashVO) bdTaskNode.getUserObject()).setAttributeValue("count", "0"); //
				//								}
			}

			//�����ʾ��Ϣ!
			if (isshowmsg) {
				lazyLoadMsg();
				//								HashMap msgMap = (HashMap) classCountMap.get("��Ϣ����");
				//								if (msgMap != null) {
				//									((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("δ����Ϣ"));
				//									((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("δ��SQL"));
				//				
				//									((HashVO) readedMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("�Ѷ���Ϣ"));
				//									((HashVO) readedMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("�Ѷ�SQL"));
				//				
				//									((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("�ѷ���Ϣ"));
				//									((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("�ѷ�SQL"));
				//				
				//									if (msgRootNode != null) {
				//										((HashVO) msgRootNode.getUserObject()).setAttributeValue("count", msgMap.get("δ����Ϣ"));
				//										((HashVO) msgRootNode.getUserObject()).setAttributeValue("sql", msgMap.get("δ��SQL"));
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

			//���� �����/2012-12-14��
			if (isshowPR) {
				//				HashMap PRMap = (HashMap) classCountMap.get("����"); //���� �����/2012-12-14��
				//				if (unreadsql == null || unreadsql.equals("")) {
				//					unreadsql = (String) PRMap.get("����SQL");
				//					readedsql = (String) PRMap.get("����SQL");
				//					sendsql = (String) PRMap.get("����SQL");
				//				}
				//
				//				String[] keys = { "��ͨ������Ϣ", "��ͨ������Ϣ", "��ͨ������Ϣ", "������������Ϣ", "������������Ϣ", "������������Ϣ" };
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
	 * �������Ѱ���
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
					// �Ѱ�����
					offTaskCountMap = (HashMap) classCountMap.get("�Ѱ�����"); //
					if (offTaskCountMap != null) { //������Ѱ�����!!
						addTaskItemNode(offTaskCountMap, offTaskNode, "4"); //�Ѱ�����ĳģ����ϸ!
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
	 * ���������񲹵�
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
					bdMap = (HashMap) classCountMap.get("�������");
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
	 * ��������Ϣ����
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
					HashMap msgMap = (HashMap) classCountMap.get("��Ϣ����");
					if (msgMap != null) {
						((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("δ����Ϣ"));
						((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("δ��SQL"));

						((HashVO) readedMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("�Ѷ���Ϣ"));
						((HashVO) readedMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("�Ѷ�SQL"));

						((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("count", msgMap.get("�ѷ���Ϣ"));
						((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("sql", msgMap.get("�ѷ�SQL"));

						if (msgRootNode != null) {
							((HashVO) msgRootNode.getUserObject()).setAttributeValue("count", msgMap.get("δ����Ϣ"));
							((HashVO) msgRootNode.getUserObject()).setAttributeValue("sql", msgMap.get("δ��SQL"));
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
	 * �����ش�����Ϣ
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
					//����Զ�̵��úϳ�һ��
					parMap.put("����SQL", unreadsql);
					parMap.put("����SQL", readedsql);
					parMap.put("����SQL", sendsql);
					HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getTaskClassCountMap", parMap);
					HashMap PRMap = (HashMap) classCountMap.get("����"); //���� �����/2012-12-14��
					if (unreadsql == null || unreadsql.equals("")) {
						unreadsql = (String) PRMap.get("����SQL");
						readedsql = (String) PRMap.get("����SQL");
						sendsql = (String) PRMap.get("����SQL");
					}
					String[] keys = { "��ͨ������Ϣ", "��ͨ������Ϣ", "��ͨ������Ϣ", "������������Ϣ", "������������Ϣ", "������������Ϣ" };
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

	//��ʾ�����˵�!!
	private void onShowPopMenu(JTree _tree, int _x, int _y) {
		TreePath parentPath = _tree.getClosestPathForLocation(_x, _y);
		if (parentPath != null) { //�����Ϊ��!!
			_tree.setSelectionPath(parentPath); //
		}

		if (popMenu == null) {
			popMenu = new JPopupMenu(); //
			menuItem_1 = new JMenuItem("�鿴��������"); //
			menuItem_2 = new JMenuItem("ɾ����������"); //
			menuItem_3 = new JMenuItem("�����������"); //

			menuItem_1.addActionListener(this); //
			menuItem_2.addActionListener(this); //
			menuItem_3.addActionListener(this); //

			popMenu.add(menuItem_1); //
			popMenu.add(menuItem_2); //
			popMenu.add(menuItem_3); //
		}

		//ȡ��ѡ�еĽ��!!!
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent(); //
		if (selNode.isRoot()) {
			return;
		}
		HashVO hvo = (HashVO) selNode.getUserObject(); //
		final String str_nodeType = hvo.getStringValue("$NodeType"); //�������!!
		if (!(str_nodeType.equals("3") || str_nodeType.equals("4"))) { //�������3��4�����
			menuItem_1.setEnabled(false); //
			menuItem_2.setEnabled(false); //
		} else {
			menuItem_1.setEnabled(true); //
			menuItem_2.setEnabled(true); //
		}
		popMenu.show(this.classTree, _x, _y); //��ʾ!!
	}

	public void openBDTask() {
		if (billList_allBDTask == null) {
			billList_allBDTask = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_task_deal_CODE1.xml"));
		}
		billList_allBDTask.queryDataByDS(null, getAllBDTaskSQL(null, isSuperShow));
		billList_allBDTask.setTitleLabelText("������ǣ����²�ѯʱ�䡾" + tbUtil.getCurrTime() + "��"); //
		billList_allBDTask.setIsRefreshParent("1");//���ò��ر���ҳ�治ˢ�¸�ҳ��   Ԭ����   20130418
		rightContentPanel.removeAll();
		rightContentPanel.setLayout(new BorderLayout());
		rightContentPanel.add(billList_allBDTask);
		bDTaskList();
		rightContentPanel.updateUI();
	}

	public void openBDTaskDetail(HashVO hvo) throws Exception {
		final String str_nodeType = hvo.getStringValue("$NodeType");
		String str_templetCode = hvo.getStringValue("templetcode"); //ģ�����
		String str_tableName = hvo.getStringValue("tablename"); //��ѯ�ı���
		String str_savedtableName = hvo.getStringValue("savedtablename"); //��ѯ�ı���
		String str_pkName = hvo.getStringValue("pkname"); //�����ֶ���!
		String str_key = str_nodeType + "$" + str_templetCode; //
		BillListPanel billList = (BillListPanel) billListPanelMap.get(str_key); //�Ѱ�����������ģ��������������,�������������л�������!!!!
		if (billList == null) {
			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(str_templetCode); //�ȸ���ģ�����ȡ��ģ��VO
			if (str_nodeType.equals("3")) { //����Ǵ�������,�����һЩ����..
				templetVO.appendNewItemVOs("������������Ϣ", getWorkflowUIUtil().getDealTaskShowFieldNames(), true); //��ԭ��VO�������µ��ֶ�!
			} else if (str_nodeType.equals("4")) { //����������Զ��ʾ��ҳ
				templetVO.setIsshowlistpagebar(true); //������Ѱ�����,������з�ҳ,���������������!
				templetVO.appendNewItemVOs("������������Ϣ", getWorkflowUIUtil().getOffTaskShowFieldNames(), true); //��ԭ��VO�������µ��ֶ�!
			} else if (str_nodeType.equals("10")) { //����������Զ��ʾ��ҳ
				templetVO.appendNewItemVOs("������������Ϣ", getWorkflowUIUtil().getDealTaskShowFieldNames(), true);
			}

			billList = new BillListPanel(templetVO, false); //����ģ��VO����
			billList.setIsRefreshParent("1");//��������ر���ҳ����ˢ�¸�ҳ��
			billList.getTempletVO().setAutoLoads(0); //ȥ���Զ��������ݵĹ���!!!
			billList.initialize(); //�ֶ���ʼ! ��Ϊ���������ó��˲��Զ���ʼʼ��ҳ��!!
			billList.setAllBillListBtnVisiable(false); //��������ҵ��ť!!
			billList.addWorkFlowDealPanel(); //
			billList.getWorkflowDealBtnPanel().addWorkFlowDealListener(this); //
			billList.getWorkflowDealBtnPanel().hiddenAllBtns(); //�������а�ť!!!
			billList.getWorkflowDealBtnPanel().setViewWFBtnVisiable(true); //��������İ�ťҪ��ʾ!!!

			if (str_nodeType.equals("3")) { //����Ǵ�����,��ֻ��ʾ����������ء���������������ť!
				billList.setItemsVisible(getWorkflowUIUtil().getDealTaskHiddenFields(), false); //����Ǵ�������ǿ��������Щ�ֶ�!
				billList.setItemsVisible(getWorkflowUIUtil().getDealTaskShowFields(), true); //����Ǵ�������,��ǿ����ʾ��Щ�ֶ�!
				billList.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
				billList.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //��ʾ������ť!!!
				billList.getWorkflowDealBtnPanel().setPassReadBtnVisiable(true); //��ʾ���İ�ť �����/2012-11-28��

				if (isshowbd) {
					billList.getWorkflowDealBtnPanel().setYJBDBtnVisiable(true);
				}
			} else if (str_nodeType.equals("4")) { //������Ѱ���,��ֻ��ʾ������������ء���������������ť!
				billList.setItemsVisible(getWorkflowUIUtil().getOffTaskHiddenFields(), false); //������Ѱ�����,��ǿ��������Щ�ֶ�!
				billList.setItemsVisible(getWorkflowUIUtil().getOffTaskShowFields(), true); //������Ѱ�����,��ǿ����ʾ��Щ�ֶ�!
				billList.getWorkflowDealBtnPanel().setCancelBtnVisiable(true); //��ʾ���ذ�ť!
				billList.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true); //��ʾ��ذ�ť!
				billList.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //��ʾ������ť!!!
				//����Ա׷�ӵ�����ҳ��ť �����/2013-06-05��
				if (ClientEnvironment.isAdmin()) {
					billList.getWorkflowDealBtnPanel().setExportAllBtnVisiable(true); //��ʾ����ȫ����ť!!!
				}
				RoleVO[] loginUserVOs = ClientEnvironment.getCurrLoginUserVO().getRoleVOs();
				String canExportAllUserRoles = tbUtil.getSysOptionStringValue("�������Ѵ���������ʾȫ�������Ľ�ɫ", null);
				if (!tbUtil.isEmpty(canExportAllUserRoles)) { //by haoming 2016-01-13
					String roles[] = tbUtil.split(canExportAllUserRoles, ";");
					for (int i = 0; i < roles.length; i++) {
						for (int j = 0; j < loginUserVOs.length; j++) {
							if (roles[i] != null && roles[i].equals(loginUserVOs[j].getCode())) {
								billList.getWorkflowDealBtnPanel().setExportAllBtnVisiable(true); //��ʾ����ȫ����ť!!! 
								break;
							}
						}
					}
				}
				billList.getWorkflowDealBtnPanel().setCDBBtnVisiable(true);
				billList.getWorkflowDealBtnPanel().setPassReadBtnVisiable(true); //��ʾ���İ�ť �����/2012-11-28��
				billList.addBillListAfterQueryListener(this); //
			} else if (str_nodeType.equals("10")) { //������������,��ֻ��ʾ��������ǡ�����ء���������������ť!
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

			billListPanelMap.put(str_key, billList); //��ע������
		} //�����û����,�򴴽�֮,��ע������!!!

		if (!ht_alreadyQuery.contains(str_key)) { //�������!!
			String str_sql = null; //
			if (str_nodeType.equals("3")) { //����Ǵ����,��ʹ�ô����SQL
				str_sql = getWorkflowUIUtil().getDealTaskSQL(str_templetCode, str_tableName, str_savedtableName, str_pkName, ClientEnvironment.getCurrSessionVO().getLoginUserId(), null, isSuperShow); //
			} else if (str_nodeType.equals("4")) { //������Ѱ��,��ʹ���Ѱ��SQL
				str_sql = getWorkflowUIUtil().getOffTaskSQL(str_templetCode, str_tableName, str_savedtableName, str_pkName, ClientEnvironment.getCurrSessionVO().getLoginUserId(), null, isSuperShow); //
			} else if (str_nodeType.equals("10")) { //������Ѱ��,��ʹ���Ѱ��SQL
				str_sql = getWorkflowUIUtil().getBDTaskSQL(str_templetCode, str_tableName, str_savedtableName, str_pkName, getBDInsql(), null, isSuperShow); //
			}
			billList.getQuickQueryPanel().resetAllQuickQueryCompent(); //�����������!
			billList.queryDataByDS(null, str_sql, true); //ֱ��ͨ��SQL����!!! //����������������ѯ���ݡ������������
			if (str_nodeType.equals("4")) { //������Ѱ�����,����Ҫ����ͬ�ĺϲ�!ԭ���Ǵ����������ж���������ʵ��һ����!!�������Ƕ���!
				resetBackColor4(billList); //���ñ�����ɫ!!
			}
			if (billList.getRowCount() > 0) {
				//billList.setSelectedRow(0); //�Զ�ѡ���һ��!!
			}
			ht_alreadyQuery.add(str_key); //
		}
		rightContentPanel.removeAll(); //
		rightContentPanel.setLayout(new BorderLayout()); //
		rightContentPanel.add(billList); //���¼���!!
		rightContentPanel.updateUI(); //
	}

	public void openMsgCenter(String type) {
		rightContentPanel.removeAll();
		rightContentPanel.setLayout(new BorderLayout());
		if ("5".equals(type) || "8".equals(type)) {
			MyMsgCenter_UnReadWFPanel unReadmsgPanel = (MyMsgCenter_UnReadWFPanel) billListPanelMap.get("δ����Ϣ");
			if (unReadmsgPanel == null) {
				unReadmsgPanel = new MyMsgCenter_UnReadWFPanel(this, ((HashVO) unreadMsgNode.getUserObject()).getStringValue("sql"));
				unReadmsgPanel.initialize();
				billListPanelMap.put("δ����Ϣ", unReadmsgPanel);
				ht_alreadyQuery.add("δ����Ϣ");
			}
			if (!ht_alreadyQuery.contains("δ����Ϣ")) {
				unReadmsgPanel.query();
				ht_alreadyQuery.add("δ����Ϣ");
			}
			rightContentPanel.add(unReadmsgPanel);
		} else if ("6".equals(type)) {
			MyMsgCenter_ReadedWFPanel readedmsgPanel = (MyMsgCenter_ReadedWFPanel) billListPanelMap.get("�Ѷ���Ϣ");
			if (readedmsgPanel == null) {
				readedmsgPanel = new MyMsgCenter_ReadedWFPanel(this, ((HashVO) readedMsgNode.getUserObject()).getStringValue("sql"));
				readedmsgPanel.initialize();
				billListPanelMap.put("�Ѷ���Ϣ", readedmsgPanel);
				ht_alreadyQuery.add("�Ѷ���Ϣ");
			}
			if (!ht_alreadyQuery.contains("�Ѷ���Ϣ")) {
				readedmsgPanel.query();
				ht_alreadyQuery.add("�Ѷ���Ϣ");
			}
			rightContentPanel.add(readedmsgPanel);
		} else if ("7".equals(type)) {
			MyMsgCenter_SendedWFPanel sendedmsgPanel = (MyMsgCenter_SendedWFPanel) billListPanelMap.get("�ѷ���Ϣ");
			if (sendedmsgPanel == null) {
				sendedmsgPanel = new MyMsgCenter_SendedWFPanel(this, ((HashVO) sendedMsgNode.getUserObject()).getStringValue("sql"));
				sendedmsgPanel.initialize();
				billListPanelMap.put("�ѷ���Ϣ", sendedmsgPanel);
				ht_alreadyQuery.add("�ѷ���Ϣ");
			}
			if (!ht_alreadyQuery.contains("�ѷ���Ϣ")) {
				sendedmsgPanel.query();
				ht_alreadyQuery.add("�ѷ���Ϣ");
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
			ht_alreadyQuery.remove("δ����Ϣ");
			ht_alreadyQuery.remove("�Ѷ���Ϣ");
			ht_alreadyQuery.remove("�ѷ���Ϣ");
			HashMap parMap = new HashMap();
			parMap.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			parMap.put("corpid", ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
			parMap.put("roleids", ClientEnvironment.getCurrLoginUserVO().getAllRoleIds());
			HashMap classCountMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.msg.MsgBsUtil", "getMsgCountMap", parMap); //�ڷ�������ʵ�ֵ�!!
			if (classCountMap != null) {
				((HashVO) unreadMsgNode.getUserObject()).setAttributeValue("count", classCountMap.get("δ����Ϣ"));
				((HashVO) readedMsgNode.getUserObject()).setAttributeValue("count", classCountMap.get("�Ѷ���Ϣ"));
				((HashVO) sendedMsgNode.getUserObject()).setAttributeValue("count", classCountMap.get("�ѷ���Ϣ"));
				if (msgRootNode != null) {
					((HashVO) msgRootNode.getUserObject()).setAttributeValue("count", classCountMap.get("δ����Ϣ"));
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

	//ˢ������ҳ��!!
	private void onRefresh(boolean _autoSelAndRefresh) {
		isTriggerTreeSelectEvent = false;//
		ht_alreadyQuery.clear(); //���!
		dealTaskNode.removeAllChildren(); //
		offTaskNode.removeAllChildren(); //
		if (isshowbd) {
			bdTaskNode.removeAllChildren();
		}

		//���� �����/2012-12-14��
		if (isshowPR) {
			unreadPRNode.removeAllChildren();
			readedPRNode.removeAllChildren();
			sendPRNode.removeAllChildren();
			unreadWFNode.removeAllChildren();
			readedWFNode.removeAllChildren();
			sendWFNode.removeAllChildren();
		}

		loadTaskNode(); //������������..

		expandAll(classTree, new TreePath(rootNode), false); //����ȫ��!
		expandAll(classTree, new TreePath(dealTaskNode.getPath()), true); //�ٽ�������չ��!

		//���� �����/2012-12-14��
		//		if (isshowPR) {
		//			expandAll(classTree, new TreePath(unreadPRNode.getPath()), false); //�ٽ�����������!
		//			expandAll(classTree, new TreePath(readedPRNode.getPath()), false); //�ٽ�����������!
		//			expandAll(classTree, new TreePath(sendPRNode.getPath()), false); //�ٽ�����������!
		//			expandAll(classTree, new TreePath(unreadWFNode.getPath()), false); //�ٽ�����������!
		//			expandAll(classTree, new TreePath(readedWFNode.getPath()), false); //�ٽ�����������!
		//			expandAll(classTree, new TreePath(sendWFNode.getPath()), false); //�ٽ�����������!
		//		}

		updateJtree(); //����Ҫ��һ��,����ҳ��ˢ�²�����!!!
		if (_autoSelAndRefresh) { //�����Ҫ�Զ�ѡ�и���㲢��ˢ����ߵ��б�!!!
			setSelectNode(classTree, dealTaskNode); ////Ĭ��ѡ�д�������!
			billList_allDealTask.queryDataByDS(null, getAllDealTaskSQL(null, isSuperShow)); //ֱ��ͨ��SQL����!!!
			billList_allDealTask.setTitleLabelText("�����������²�ѯʱ�䡾" + tbUtil.getCurrTime() + "��"); //

			rightContentPanel.removeAll(); //
			rightContentPanel.setLayout(new BorderLayout()); //
			rightContentPanel.add(billList_allDealTask); //
			dealTaskList();
			rightContentPanel.updateUI(); //����Ҫ��һ��!!!
		}
		isTriggerTreeSelectEvent = true;//�򿪿���!!
	}

	//��ʾ��������!�������κ�Ȩ�޹��˵Ľ���! ��Ϊ����Ŀ�����о�������һЩ����,��Ҫ�����κ�Ȩ���߼�,��͸��ѯ����������!!
	private void onSuperShow() {
		TaskAndMsgCenterPanel taskPanel = new TaskAndMsgCenterPanel(true); //

		JFrame frame = new JFrame("��ʾ���й���������"); //
		frame.setLocation(100, 100); //
		frame.setSize(900, 600); ////
		frame.getContentPane().add(taskPanel); ////
		frame.setVisible(true); ////
	}

	//��ʾ��������!!
	private void onShowRubbishData() {
		try {
			TreePath selectedPath = this.classTree.getSelectionPath(); //
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent(); //ѡ�еĽ��!!
			HashVO hvo = (HashVO) selNode.getUserObject(); //
			String str_nodeName = hvo.toString(); //�������!!
			String str_nodeType = hvo.getStringValue("$NodeType"); //�������!!
			String str_templetCode = hvo.getStringValue("templetcode"); //ģ�����
			String str_tableName = hvo.getStringValue("tablename"); //��ѯ�ı���
			String str_savedtableName = hvo.getStringValue("savedtablename"); //��ѯ�ı���
			String str_pkName = hvo.getStringValue("pkname"); //�����ֶ���!
			if (str_nodeType.equals("3")) {
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("select count(*) from pub_task_deal where ");
				sb_sql.append("templetcode='" + str_templetCode + "' "); //ģ�����!!
				sb_sql.append("and tabname='" + str_savedtableName + "' "); //����!!
				sb_sql.append("and pkvalue not in (select " + str_pkName + " from " + str_tableName + ") "); //����ֵ!!
				String str_result = UIUtil.getStringValueByDS(null, sb_sql.toString()); //
				MessageBox.show(classTree, "[" + str_nodeName + "]�Ĵ������й���[" + str_result + "]����������!"); //
			} else if (str_nodeType.equals("4")) {
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("select count(*) from pub_task_off where ");
				sb_sql.append("templetcode='" + str_templetCode + "' "); //ģ�����!!
				sb_sql.append("and tabname='" + str_savedtableName + "' "); //����!!
				sb_sql.append("and pkvalue not in (select " + str_pkName + " from " + str_tableName + ") "); //����ֵ!!
				String str_result = UIUtil.getStringValueByDS(null, sb_sql.toString()); //
				MessageBox.show(classTree, "[" + str_nodeName + "]���Ѱ����й���[" + str_result + "]����������!"); //
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	private void onDeleteRubbishData() {
		try {
			TreePath selectedPath = this.classTree.getSelectionPath(); //
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent(); //ѡ�еĽ��!!
			HashVO hvo = (HashVO) selNode.getUserObject(); //
			String str_nodeName = hvo.toString(); //�������!!
			String str_nodeType = hvo.getStringValue("$NodeType"); //�������!!
			String str_templetCode = hvo.getStringValue("templetcode"); //ģ�����
			String str_tableName = hvo.getStringValue("tablename"); //��ѯ�ı���
			String str_savedtableName = hvo.getStringValue("savedtablename"); //��ѯ�ı���
			String str_pkName = hvo.getStringValue("pkname"); //�����ֶ���!
			if (str_nodeType.equals("3")) {
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("select distinct prinstanceid from pub_task_deal where "); //�ҳ�����ʵ����id
				sb_sql.append("templetcode='" + str_templetCode + "' "); //ģ�����!!
				sb_sql.append("and tabname='" + str_savedtableName + "' "); //����!!
				sb_sql.append("and pkvalue not in (select " + str_pkName + " from " + str_tableName + ") "); //����ֵ!!
				String[] str_ids = UIUtil.getStringArrayFirstColByDS(null, sb_sql.toString()); //
				if (str_ids == null || str_ids.length <= 0) {
					MessageBox.show(classTree, "[" + str_nodeName + "]�Ĵ�������û����������!"); //
					return; //
				}
				String str_incondition = tbUtil.getInCondition(str_ids); //
				String str_sql_1 = "delete from pub_task_deal where prinstanceid in (" + str_incondition + ")"; //
				String str_sql_2 = "delete from pub_wf_dealpool where prinstanceid in (" + str_incondition + ")"; //
				String str_sql_3 = "delete from pub_wf_prinstance where id in (" + str_incondition + ")"; //
				UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3 }); //
				MessageBox.show(classTree, "ɾ��[" + str_nodeName + "]�Ĵ��������������ݳɹ�!"); //
			} else if (str_nodeType.equals("4")) {
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("select distinct prinstanceid from pub_task_off where ");
				sb_sql.append("templetcode='" + str_templetCode + "' "); //ģ�����!!
				sb_sql.append("and tabname='" + str_savedtableName + "' "); //����!!
				sb_sql.append("and pkvalue not in (select " + str_pkName + " from " + str_tableName + ") "); //����ֵ!!
				String[] str_ids = UIUtil.getStringArrayFirstColByDS(null, sb_sql.toString()); //
				if (str_ids == null || str_ids.length <= 0) {
					MessageBox.show(classTree, "[" + str_nodeName + "]�Ĵ�������û����������!"); //
					return; //
				}
				String str_incondition = tbUtil.getInCondition(str_ids); //
				String str_sql_1 = "delete from pub_task_off where prinstanceid in (" + str_incondition + ")"; //
				String str_sql_2 = "delete from pub_wf_dealpool where prinstanceid in (" + str_incondition + ")"; //
				String str_sql_3 = "delete from pub_wf_prinstance where id in (" + str_incondition + ")"; //
				UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3 }); //
				MessageBox.show(classTree, "ɾ��[" + str_nodeName + "]���Ѱ������������ݳɹ�!"); //
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * �ڴ����������Ѱ�����Ľ���������µ��ӽ��!!!
	 * @param _returnMap
	 * @param _parentNode
	 * @param _nodeType
	 */
	private void addTaskItemNode(HashMap _returnMap, DefaultMutableTreeNode _parentNode, String _nodeType) {
		String[] str_keys = (String[]) _returnMap.keySet().toArray(new String[0]); //�õ����е�key
		int li_allCount = 0; //
		HashVO[] allItemVOs = new HashVO[str_keys.length]; //
		for (int i = 0; i < str_keys.length; i++) {
			String str_templetCode = str_keys[i]; //
			String[] str_values = (String[]) _returnMap.get(str_keys[i]); //
			String str_templetName = str_values[0]; //
			String str_tableName = str_values[1]; //��ѯ�ı���
			String str_savedtablename = str_values[2]; //����ı���
			String str_pkName = str_values[3]; //�����ֶ���!
			String str_count = str_values[4]; //
			allItemVOs[i] = new HashVO(); //
			allItemVOs[i].setAttributeValue("$NodeType", _nodeType); //��������֮ĳģ��!
			allItemVOs[i].setAttributeValue("templetcode", str_templetCode); //ģ�����!
			allItemVOs[i].setAttributeValue("templetname", str_templetName); //ģ������
			allItemVOs[i].setAttributeValue("tablename", str_tableName); //��ѯ�ı���
			allItemVOs[i].setAttributeValue("savedtablename", str_savedtablename); //����ı���
			allItemVOs[i].setAttributeValue("pkname", str_pkName); //�����ֶ���!
			allItemVOs[i].setAttributeValue("text", (str_templetName == null ? str_templetCode : str_templetName)); //��ʾ������
			allItemVOs[i].setAttributeValue("count", str_count); ////
			li_allCount = li_allCount + Integer.parseInt(str_count); //
		}

		//Ϊ��˳������������������!!!�Ժ���� �ᰴָ����˳������!!!
		tbUtil.sortHashVOs(allItemVOs, new String[][] { { "templetname", "N", "N" } }); //

		//�����������!!!

		//�ʴ���Ŀ���и�����,���Ǿ��ô��������е��ӽ��̫��,���ټ�һ��,ʹ��һ��Ŀ¼ģ��!��Ϊ����!
		//���кܶ��ַ���,������뻹��ʹ��һ��ȫ�ֲ���,����!!,
		String str_df = TBUtil.getTBUtil().getSysOptionStringValue("��������������Ŀ¼���ඨ��", null); //
		HashMap map = null; //
		String[] str_dfdirkeys = null; //
		if (str_df != null && !str_df.trim().equals("")) { //
			map = new HashMap(); //
			str_df = TBUtil.getTBUtil().replaceAll(str_df, " ", ""); //
			str_df = TBUtil.getTBUtil().replaceAll(str_df, "\r", ""); //
			str_df = TBUtil.getTBUtil().replaceAll(str_df, "\n", ""); //
			HashMap initMap = TBUtil.getTBUtil().convertStrToMapByExpress(str_df, ";", "="); //
			str_dfdirkeys = (String[]) initMap.keySet().toArray(new String[0]); //���ж����Ŀ¼
			for (int i = 0; i < str_dfdirkeys.length; i++) {
				String str_models = (String) initMap.get(str_dfdirkeys[i]); //
				String[] str_modelArray = TBUtil.getTBUtil().split(str_models, ","); //
				for (int j = 0; j < str_modelArray.length; j++) {
					map.put(str_modelArray[j], str_dfdirkeys[i]); //
				}
			}
		}

		HashMap alreadyAddedDirNodeMap = new HashMap(); //������¼������������Ŀ¼���!!!
		ArrayList al_onlyLeafNode = new ArrayList(); // 
		for (int i = 0; i < allItemVOs.length; i++) {
			String str_thisNodeText = allItemVOs[i].toString(); //
			//System.out.println("model:" + str_thisNodeText); //
			if (map != null && map.containsKey(str_thisNodeText)) { //��������ұ�ָ����һ��Ŀ¼,���ȴ���Ŀ¼!
				String str_dirName = (String) map.get(str_thisNodeText); //
				if (!alreadyAddedDirNodeMap.containsKey(str_dirName)) { //�����û�Ǽ�,���ȵ�¼һ��,����ͬ��Ŀ¼ֻ��һ��!
					HashVO modelDirVO = new HashVO(); //
					modelDirVO.setAttributeValue("$NodeType", "-1"); //��Ŀ¼���
					modelDirVO.setAttributeValue("text", str_dirName); //
					modelDirVO.setAttributeValue("count", "0"); //
					modelDirVO.setToStringFieldName("text"); //
					DefaultMutableTreeNode addDirNode = new DefaultMutableTreeNode(modelDirVO); //
					alreadyAddedDirNodeMap.put(str_dirName, addDirNode); //
				}

				DefaultMutableTreeNode dirNode = (DefaultMutableTreeNode) alreadyAddedDirNodeMap.get(str_dirName); //Ŀ¼���!
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(allItemVOs[i]); //�����
				dirNode.add(node); //Ŀ¼���������ϱ����

				//_parentNode.add(dirNode); //��Ŀ¼������!!
			} else {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(allItemVOs[i]); //
				al_onlyLeafNode.add(node); //
				//_parentNode.add(node); //���ӽ��
			}
		}

		//������Ŀ¼�Ľ��,���밴ָ�����Ⱥ�˳������!
		String[] str_realdirs = (String[]) alreadyAddedDirNodeMap.keySet().toArray(new String[0]); //
		if (str_realdirs != null && str_realdirs.length > 0) {
			TBUtil.getTBUtil().sortStrsByOrders(str_realdirs, str_dfdirkeys); //
			for (int i = 0; i < str_realdirs.length; i++) { //
				DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) alreadyAddedDirNodeMap.get(str_realdirs[i]); //
				_parentNode.add(tmpNode); //��Ŀ¼������!!
			}
		}

		//������Щû��Ŀ¼�Ľ��!
		for (int i = 0; i < al_onlyLeafNode.size(); i++) {
			DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) al_onlyLeafNode.get(i); //
			_parentNode.add(tmpNode); //��Ŀ¼������!!
		}

		HashVO hvo = (HashVO) _parentNode.getUserObject(); //
		hvo.setAttributeValue("count", "" + li_allCount); //���ø��׽�������
	}

	/**
	 * ѡ�������仯�¼�
	 */
	public void valueChanged(TreeSelectionEvent _event) {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			if (!isTriggerTreeSelectEvent) { //������ô���,��ֱ�ӷ���!!
				return;
			}
			TreePath selectedPath = classTree.getSelectionPath(); //ѡ�е�·��!!!
			if (selectedPath == null) { //���ѡ�е�·��Ϊ��,�򷵻�!!!
				return; //
			}
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent(); //
			HashVO hvo = (HashVO) selNode.getUserObject(); //
			final String str_nodeType = hvo.getStringValue("$NodeType"); //�������!!
			if (str_nodeType.equals("1")) { //���д�������,ˢ�����д�������!
				billList_allDealTask.queryDataByDS(null, getAllDealTaskSQL(null, isSuperShow)); //ֱ��ͨ��SQL����!!!
				billList_allDealTask.setTitleLabelText("�����������²�ѯʱ�䡾" + tbUtil.getCurrTime() + "��"); //
				rightContentPanel.removeAll(); //
				rightContentPanel.setLayout(new BorderLayout()); //
				rightContentPanel.add(billList_allDealTask); //���¼���!!
				dealTaskList();
				rightContentPanel.updateUI(); //
			} else if (str_nodeType.equals("2")) { //�����Ѱ�����!
				if (billList_allOffTask == null) { //���Ϊ��,�򴴽�֮!!
					billList_allOffTask = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_task_off_CODE1.xml")); //�Ѱ�������б�
					billList_allOffTask.setQuickQueryPanelVisiable(false);//����ģ�嵼��xml��sql�оͲ�ѯ������¼�������ز�ѯ��塾���/2016-06-04��
					//					billList_allOffTask.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
					//						public void actionPerformed(ActionEvent e) {
					//							onAllOffTaskQuickQuery((BillQueryPanel) e.getSource()); //
					//						}
					//					});
				}
				billList_allOffTask.queryDataByDS(null, getAllOffTaskSQL(null, isSuperShow)); //ֱ��ͨ��SQL����!!!
				offTaskList();
				rightContentPanel.removeAll(); //
				rightContentPanel.setLayout(new BorderLayout()); //
				rightContentPanel.add(billList_allOffTask); //���¼���!!
				rightContentPanel.updateUI(); //
			} else if (str_nodeType.equals("3") || str_nodeType.equals("4")) { //ĳ������ҵ��Ĵ�������/�Ѱ�����!����ϸ!
				openBDTaskDetail(hvo); //
			} else if (str_nodeType.equals("5") || str_nodeType.equals("6") || str_nodeType.equals("7") || str_nodeType.equals("8")) { //5-δ��;6-�Ѷ�;7-�ѷ�;8-��Ϣ�ĸ����
				openMsgCenter(str_nodeType);
			} else if (str_nodeType.equals("9")) { //������ǵ�Ŀ¼���!
				openBDTask();
			} else if (str_nodeType.equals("10")) { //������ǵ���ϸ!
				openBDTaskDetail(hvo);
			} else if (str_nodeType.equals("100") || str_nodeType.equals("101") || str_nodeType.equals("102") || str_nodeType.equals("103")) { //���� �����/2012-12-14��
				return;
			} else if (str_nodeType.equals("110") || str_nodeType.equals("111") || str_nodeType.equals("112") || str_nodeType.equals("113") || str_nodeType.equals("114") || str_nodeType.equals("115")) { //���� �����/2012-12-14��
				dealPR(hvo);
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	//���Ĵ������ڵ� �����/2012-12-14��
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
					modelDirVO.setAttributeValue("$NodeType", "100"); //��Ŀ¼���
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

	//���Ľڵ��¼� �����/2012-12-14��
	private void dealPR(HashVO hvo) {
		billListPanel = new BillListPanel("PUB_MESSAGE_PASSREAD");
		billListPanel.addBillListHtmlHrefListener(new BillListHtmlHrefListener() {

			public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
				try {
					String messageHtmlClass = tbUtil.getSysOptionStringValue("�ղؼд��ĺ�ͷ�ļ�������", "");
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

		select = WLTButton.createButtonByType(WLTButton.COMM, "���");
		select.addActionListener(this);
		billListPanel.addBillListButton(select);
		billListPanel.repaintBillListButton();

		String str_nodeType = hvo.getStringValue("$NodeType");
		String templetcode = hvo.getStringValue("templetcode");

		String[] keys = { "��ͨ������Ϣ", "��ͨ������Ϣ", "��ͨ������Ϣ", "������������Ϣ", "������������Ϣ", "������������Ϣ" };
		String[] msgtypes = { "��ͨ������Ϣ", "��ͨ������Ϣ", "��ͨ������Ϣ", "������������Ϣ", "������������Ϣ", "������������Ϣ" };
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

	//�����б����� �����/2012-12-14��
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
		if (title.equals("��������������Ϣ") || title.equals("��������������Ϣ") || title.equals("��������������Ϣ")) {
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
		} else {
			tabbedPane.addTab("��Ϣ����", billCardPanel);
		}

		BillCardPanel passReadPanel = new BillCardPanel("PUB_MESSAGE_BTN");
		passReadPanel.setEditable("opinion", true);
		CardCPanel_ChildTable opinionListPanel = (CardCPanel_ChildTable) passReadPanel.getCompentByKey("opinion");
		opinionListPanel.getBtn_edit().setVisible(false);
		opinionListPanel.getBtn_delete().setVisible(false);
		passReadPanel.queryDataByCondition(" id =" + id);
		tabbedPane.addTab("������Ϣ", passReadPanel);

		BillDialog dialog = new BillDialog(billListPanel, "��Ϣ����", 800, 600);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(tabbedPane, "Center");
		dialog.setVisible(true);
	}

	//���Ĵ����Ѷ� �����/2012-12-14��
	private void readed() {
		String title = billListPanel.getTitleLabel().getText();
		if (title.equals("����ͨ������Ϣ") || title.equals("��������������Ϣ")) {
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

	//�����Ѷ�ˢ�� �����/2012-12-14��
	private void readRefresh(String title, String templetcode) {
		unreadPRNode.removeAllChildren();
		readedPRNode.removeAllChildren();
		sendPRNode.removeAllChildren();
		unreadWFNode.removeAllChildren();
		readedWFNode.removeAllChildren();
		sendWFNode.removeAllChildren();

		loadTaskNode(); //������������..

		if (title.equals("����ͨ������Ϣ")) {
			expandAll(classTree, new TreePath(rootNode), false); //ȫ������
			expandAll(classTree, new TreePath(generalNode.getPath()), true); //�ٽ���ͨ����չ��!
		} else {
			expandAll(classTree, new TreePath(rootNode), false); //ȫ������
			expandAll(classTree, new TreePath(workFlowNode.getPath()), false); //�ٽ�����������չ��!			
		}
		updateJtree(); //����Ҫ��һ��,����ҳ��ˢ�²�����!!!
		if (title.equals("����ͨ������Ϣ")) {
			setSelectNode(classTree, unreadPRNode);
			billListPanel.QueryDataByCondition(" templetcode='" + templetcode + "' and msgtype='��ͨ������Ϣ' and " + unreadsql);
		} else {
			setSelectNode(classTree, unreadWFNode);
			billListPanel.QueryDataByCondition(" templetcode='" + templetcode + "' and msgtype='������������Ϣ' and " + unreadsql);
		}
		rightContentPanel.removeAll();
		rightContentPanel.setLayout(new BorderLayout());
		rightContentPanel.add(billListPanel);
		rightContentPanel.updateUI(); //����Ҫ��һ��!!
	}

	//�����ѯ���д�������
	protected void onAllOffTaskQuickQuery(BillQueryPanel _billQueryPanel) {
		String str_condition = _billQueryPanel.getQuerySQLCondition(); //ȡ�ò�ѯ����!
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
	 * �����ѯ�����߼�!!
	 * @param _billQueryPanel
	 */
	protected void onQuickQuery(BillQueryPanel _billQueryPanel, String _noteType) {
		try {
			//�����õĲ�ѯ������Ӧ���в��Թ��ˣ������Ѱ�����Ⱦ��޷���ѯ�����ˣ����ĸ�����_isUseDataAccess=false�����/2017-08-07��
			String str_condition = _billQueryPanel.getQuerySQLCondition(_billQueryPanel.getAllQuickQueryCompents(), false, true, false, "t2", null); //  //ȡ�ò�ѯ����!
			if (str_condition == null) {
				return;
			}
			String str_templetCode = _billQueryPanel.getBillListPanel().getTempletVO().getTempletcode(); //ģ�����
			String str_tabName = _billQueryPanel.getBillListPanel().getTempletVO().getTablename(); //��ѯ�ı���!
			String str_savedtabName = _billQueryPanel.getBillListPanel().getTempletVO().getSavedtablename(); //����ı���!
			String str_pkName = _billQueryPanel.getBillListPanel().getTempletVO().getPkname(); //�����ֶ���!
			String str_sql = null;
			if (_noteType.equals("3")) {
				str_sql = getWorkflowUIUtil().getDealTaskSQL(str_templetCode, str_tabName, str_savedtabName, str_pkName, ClientEnvironment.getCurrSessionVO().getLoginUserId(), str_condition, isSuperShow); //ƴ�ɴ�������SQL,��Ϊ���SQL������,���Է���UIUtil��!
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
	 * �Ѱ������ѯ���ҳ����Ҫ���ñ���ɫ..
	 */
	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		BillListPanel list = (BillListPanel) _event.getSource(); //
		resetBackColor4(list); //
	}

	/**
	 * ���ô��������еı���ɫ,��Ϊ���������и���������Ƕ�����¼��ͬһ����!����һ�𿴲����!
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
			if (str_thisid.equals(str_lastid)) { //�������ǰ��һ��,����ԭ����
				billList.setItemBackGroundColor(cols[li_colcount % cols.length], i); //

			} else { //�����һ��!!!
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
	 * ȡ�����д�������!
	 * �Ժ�Ҫ��������ʵ����,��֪����ǰ����״̬,��ǰ���̵�����һ����,��ǰ�����ĸ�������!!
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
		if (!_isSuperShow) { //������ǳ����鿴,����ݵ�¼��Ա����!! ��������ѯ����ʾ����!!
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
	 * ������ǵ�sql
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
	 * ������ǵ�SQL..
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
	 * ȡ�������Ѱ�����!
	 * �Ժ�Ҫ��������ʵ����,��֪����ǰ����״̬,��ǰ���̵�����һ����,��ǰ�����ĸ�������!!
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

		//�Ѱ�������ͬ����ϲ� �����/2013-03-08��
		if (tbUtil.getSysOptionBooleanValue("�Ѱ�����ͬһ�����Ƿ�ֻ��ʾ���һ��", false)) {
			sb_sql.append(" inner join ( select pkvalue, max(dealtime) dealtime from pub_task_off where 1=1 ");
			if (!_isSuperShow) { //������ǳ����鿴,����ݵ�¼��Ա����!! ��������ѯ����ʾ����!!
				sb_sql.append(" and (realdealuser='" + str_loginUserId + "' or accruserid='" + str_loginUserId + "') "); //ʵ�ʴ���������Ȩ�˶��ܿ���!! order by t1.dealtime desc
			}
			if (templetCode != null && !"".equals(templetCode)) {
				sb_sql.append(" and templetcode='" + templetCode + "' "); //
			}
			sb_sql.append(" group by pkvalue ) t4 on t1.pkvalue=t4.pkvalue and t1.dealtime=t4.dealtime ");
		}

		sb_sql.append("where 1=1 "); //
		if (!_isSuperShow) { //������ǳ����鿴,����ݵ�¼��Ա����!! ��������ѯ����ʾ����!!
			sb_sql.append("and (t1.realdealuser='" + str_loginUserId + "' or t1.accruserid='" + str_loginUserId + "') "); //ʵ�ʴ���������Ȩ�˶��ܿ���!! order by t1.dealtime desc
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
	 * չ��������!!
	 * @param tree
	 * @param _treePath
	 */
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

	/**
	 * ˢ��
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
	 * ѡ�����е�ĳһ�����!
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
			onSuperShow(); //��ʾ��������,�������κ�Ȩ�޹��˵�,��ʾ��������Ľ���!!
		} else if (e.getSource() == menuItem_1) {
			onShowRubbishData(); //��ʾ��������!!
		} else if (e.getSource() == menuItem_2) {
			onDeleteRubbishData(); //
		} else if (e.getSource() == menuItem_3) {
			onShowNodeInfo(); //��ʾ���������Ϣ!
		} else if (e.getSource() == select) {
			if (billListPanel.getSelectedBillVO() == null) {
				MessageBox.show(billListPanel, "��ѡ����Ҫ�������Ϣ!");
				return;
			} else {
				selected();
				readed();
			}
		}
	}

	/**
	 * ��ʾ���������Ϣ,�ڵ�������ʱ�ر�����!
	 */
	private void onShowNodeInfo() {
		TreePath selectedPath = this.classTree.getSelectionPath(); //
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent(); //ѡ�еĽ��!!
		HashVO hvo = (HashVO) selNode.getUserObject(); //
		String[] str_keys = hvo.getKeys(); //
		StringBuilder sb_info = new StringBuilder(); //
		for (int i = 0; i < str_keys.length; i++) {
			sb_info.append("[" + str_keys[i] + "]=[" + hvo.getStringValue(str_keys[i], "") + "]\r\n"); //
		}
		MessageBox.show(classTree, sb_info.toString()); //
	}

	/**
	 * ���̴�������!!
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
		private Icon icon1 = UIUtil.getImage("office_026.gif"); //��������
		private Icon icon2 = UIUtil.getImage("office_034.gif"); //�Ѱ�����
		private Icon icon_dir5 = UIUtil.getImage("zt_062.gif"); //δ��
		private Icon icon_dir6 = UIUtil.getImage("zt_063.gif"); //�Ѷ�
		private Icon icon_dir7 = UIUtil.getImage("office_039.gif"); //δ��
		private Icon icon_modeldir = UIUtil.getImage("office_151.gif"); //�ʴ���Ŀ���뽫�˵�Ŀ¼��Ϊ�ڶ���!
		private Icon icon_dir8 = UIUtil.getImage("bug_1.png");
		private Icon icon_dir9 = UIUtil.getImage("office_039.gif");//�������

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

			//����ͼ��!
			if (li_type == 0) {
				label.setIcon(this.getDefaultOpenIcon()); //
			} else if (li_type == 1) { //�����Ŀ¼
				if (expanded) {
					label.setIcon(icon_dir1); //
				} else {
					label.setIcon(this.getDefaultClosedIcon()); //
				}
			} else if (li_type == 2) { //�����Ŀ¼
				if (expanded) {
					label.setIcon(icon_dir2); //
				} else {
					label.setIcon(this.getDefaultClosedIcon()); //
				}
			} else if (li_type == 3) { //����������Ŀ���
				label.setIcon(icon1); //
			} else if (li_type == 4) { //������Ѱ����Ŀ���
				label.setIcon(icon2); //
			} else if (li_type == 5) {
				label.setIcon(icon_dir5);
			} else if (li_type == 6) {
				label.setIcon(icon_dir6);
			} else if (li_type == 7) {
				label.setIcon(icon_dir7);
			} else if (li_type == -1) {
				label.setIcon(icon_modeldir);
			} else if (li_type == 8) {//��Ϣ����ñ��
				if (expanded) {
					label.setIcon(icon_dir8);
				} else {
					label.setIcon(this.getDefaultClosedIcon()); //
				}
			} else if (li_type == 9) {//�������
				if (expanded) {
					label.setIcon(icon_dir9);
				} else {
					label.setIcon(this.getDefaultClosedIcon()); //
				}
			} else if (li_type == 10) {
				label.setIcon(icon1);
			} else if (li_type == 101) { //���´��� �����/2012-12-14��
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

			//����������ɫ!
			if (sel) {
				label.setOpaque(true); //���ѡ�еĻ�,��͸��..
				label.setForeground(Color.RED); //
				label.setBackground(Color.YELLOW); //
			} else {
				label.setOpaque(false); //͸��!
			}
			return label;
		}
	}

}
