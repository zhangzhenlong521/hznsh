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
 * ���������̼�صĽ���!! �ǳ��ؼ���ҳ��!! Ҳ�����Ի�Ҫ��ǳ��ߵĽ���!!!
 * @author Administrator
 *
 */
public class WorkflowMonitorDialog extends BillDialog implements BillListSelectListener, TreeSelectionListener, BillListHtmlHrefListener, ActionListener {

	private static final long serialVersionUID = 4894976596858838094L;

	private String str_wfinstance = null;
	private String str_rootInstanceId = null; //
	private BillVO billVO = null; // �����е�����
	private WLTPopupButton btn_exporthtmlreport = null; // ����html����.
	private WorkFlowDesignWPanel graphPanel = null;

	private JSplitPane splitPanel_main = null;
	private JTree dealPoolTree = null, dealPoolTree2 = null; //
	private BillListPanel dealPoolBillList = null; //
	private JPopupMenu popMenu, popMenu2, confMenu = null; //

	//private HashMap map_par = null;
	private boolean ifJiaMi = true;//Ĭ�ϼ���??

	private TBUtil tbUtil = new TBUtil(); //
	private WorkflowUIUtil wfUIUtil = new WorkflowUIUtil(); //
	private VectorMap vm_allReport = null;

	private WLTButton btn_confirm, btn_showWFInfo, btn_exportWFReport, btn_wfOptions;

	private boolean isTreeSelectChanging = false, isTableSelectChanging = false; //

	private String processStatus = null; //���̵�ǰ״̬

	public WorkflowMonitorDialog() {
	}

	/**
	 * ���췽��
	 * 
	 * @param _parent
	 * @param _wfinstance
	 * @param _billVO
	 */
	public WorkflowMonitorDialog(Container _parent, String _wfinstance, BillVO _billVO) {
		super(_parent, "����ִ��������", 1020, 740);
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
		super(_parent, "����ִ��������", 1020, 740);
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
		super(_parent, "����ִ��������", 1020, 740);
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
		if (!tbUtil.getSysOptionBooleanValue("���������̼���Ƿ�Ĭ�����ص�������", false)) { //sunfujun/20120829/�ʴ��ܾ��������Ϊ�صĺ���
			JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
			getWLTPopupButton();
			if (btn_exporthtmlreport != null) {
				panel_north.add(btn_exporthtmlreport);
			}
			this.add(panel_north, BorderLayout.NORTH); //
		}

		//���ҳ�������!!!
		HashVO instanceVO[] = UIUtil.getHashVoArrayByDS(null, "select rootinstanceid,status from pub_wf_prinstance where id='" + this.str_wfinstance + "'"); //
		if (instanceVO != null && instanceVO.length > 0) {
			str_rootInstanceId = instanceVO[0].getStringValue("rootinstanceid");
			processStatus = instanceVO[0].getStringValue("status");
		}
		if (str_rootInstanceId == null || str_rootInstanceId.trim().equals("")) { //���ǵ��ɵĻ���,���ܸ�������ʵ��idΪ��!
			str_rootInstanceId = str_wfinstance; //
		}
		//��ѯ������,���ܻ����ύ������ܴ���!
		if (tbUtil.getSysOptionBooleanValue("���������̼���Ƿ�Ĭ������ͼ��ʾ������", true)) {
			splitPanel_main = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getGraphPanel(), getDealPoolPanel());
		} else {
			splitPanel_main = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getDealPoolPanel(), getGraphPanel());
		}
		splitPanel_main.setDividerSize(10);
		splitPanel_main.setOneTouchExpandable(true); //
		if (tbUtil.getSysOptionBooleanValue("���������̼���Ƿ�Ĭ����������ͼ", false)) {
			splitPanel_main.setDividerLocation(0);
			splitPanel_main.setLastDividerLocation(350);
		} else {
			splitPanel_main.setDividerLocation(350);
		}
		this.add(splitPanel_main, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //

		autoScrolltoNode(dealPoolTree2); //�Զ����Ҳ������ڶ�����

		this.locationToCenterPosition();
	}

	private void autoScrolltoNode(JTree _tree) {
		//�Զ�ѡ�������ҵĴ�������Ľ��,����еĻ�!! ��Ϊ������������Ҫ֪��������ʲô����!!
		if (_tree != null) {
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) _tree.getModel().getRoot(); //
			DefaultMutableTreeNode[] allNodes = getAllChildrenNodes(rootNode, false); //
			String str_loginUserid = ClientEnvironment.getInstance().getLoginUserID(); //��¼��Աid
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

					if (str_dealuserid != null && str_dealuserid.equals(str_loginUserid) && !"Y".equals(str_issubmit)) { //��������˵��ڵ�ǰ��!
						_tree.setSelectionPath(new TreePath(allNodes[i].getPath())); //ѡ�����н��!����������Զ�չ�����·��!!
						isFinded = true; //
						break; //
					}
				}
			}

			if (!isFinded) { //���û�ҵ��ҵ�����,���Զ�������ʾ������!!
				if (notSubMitNode != null) { //
					_tree.setSelectionPath(new TreePath(notSubMitNode.getPath())); //ѡ�����н��!����������Զ�չ�����·��!!
				}
				reLoadGraphFromTree(_tree); //����ͼ�еĻ���
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			this.setCloseType(1); //
			this.dispose(); //
		} else if (e.getSource() == btn_showWFInfo) { //��ʾ������Ϣ
			graphPanel.exportConf(); //����������Ϣ
		} else if (e.getSource() == btn_exportWFReport) { //�������̱���
			HashMap map_par = new HashMap();
			map_par.put("showType", "1");
			onExportHtmlReport(map_par); //
		} else if (e.getSource() == btn_wfOptions) {
			BillDialog dialog = new SystemOptionsDialog(this, "ƽ̨_������", new String[] { "���������̼���Ƿ�Ĭ�����ص�������", "���������̼���Ƿ�Ĭ������ͼ��ʾ������", //
					"���������̼���Ƿ�Ĭ����������ͼ", "���������̼���Ƿ�Ĭ���������δ����¼", "��������������б��е���Ա�Ƿ���ʾ����", //
					"������������б����˳��", "���������Ƿ�֧���ϴ�����", "�������鿴���ʱ�Ƿ���ʾ���ܽ��ܰ�ť", "�����������̽���ʱ����ʾ��" }); //
			dialog.setVisible(true); //
		}
	}

	//����İ�ť�����
	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_showWFInfo = new WLTButton("�鿴��������"); //
		btn_showWFInfo.setToolTipText("���ѡ��ĳ�����ڻ��������Զ�ѡ�����¼"); //
		btn_exportWFReport = new WLTButton("�����������"); //
		btn_exportWFReport.setToolTipText("ʹ��ƽ̨��׼�ĵ������̱���,��Ҫ���ÿ�Ƭ������ͼ�е��Ƿ񵼳�"); //

		btn_wfOptions = new WLTButton("������ϵͳ����"); //
		btn_wfOptions.setToolTipText("�ѹ�������ص�ϵͳ�����������г���,Ȼ������޸�"); //

		btn_confirm.addActionListener(this); //
		btn_showWFInfo.addActionListener(this); //
		btn_exportWFReport.addActionListener(this); //
		btn_wfOptions.addActionListener(this); //

		panel.add(btn_confirm); //���밴ť
		if (ClientEnvironment.isAdmin()) {
			panel.add(btn_showWFInfo); //���밴ť
			panel.add(btn_exportWFReport); //���밴ť
			panel.add(btn_wfOptions); //���밴ť
		}
		return panel; //
	}

	//������ʷ����������������!
	public JPanel getDealPoolPanel() {
		JPanel dealpoolPanel = new JPanel(new BorderLayout());
		try {
			WFHistListPanelBuilder wfHistBuilder = new WFHistListPanelBuilder(str_rootInstanceId, billVO, false); //
			HashVO[] hvs = wfHistBuilder.getHashVODatas(); //��������!!!
			JTree tree1 = getDealPoolTree(hvs); //��һ����!
			JTree tree2 = getDealPoolTree2(hvs); //�ڶ�����!

			dealPoolBillList = wfHistBuilder.getBillListPanel(); //
			dealPoolBillList.getTempletVO().setTablename(this.billVO.getQueryTableName()); //
			dealPoolBillList.getTempletVO().setSavedtablename(this.billVO.getQueryTableName()); //
			dealPoolBillList.addBillListSelectListener(this); //
			dealPoolBillList.addBillListHtmlHrefListener(this); //

			if (tbUtil.getSysOptionBooleanValue("���������̼���Ƿ�Ĭ���������δ����¼", false) && !ClientEnvironment.isAdmin()) { //
				dealpoolPanel.add(dealPoolBillList); //�����б��е�����!!!
			} else {
				//��������пͻ�Ҫ��ֻҪһ����,����Ĭ����ʾ�ĸ���
				JTabbedPane tabb = new JTabbedPane(); //
				JPanel panel_1 = WLTPanel.createDefaultPanel(new BorderLayout()); //
				JLabel label_1 = new JLabel("�������ύ��֮��Ĺ�ϵ��·չʾ"); //
				label_1.setToolTipText("<html>�������Ŀ����������������֮��Ĺ�ϵ!<br>������ͬʱ�ύ������˻�ǩʱ,�����ֳ�������֧!<br>����뷢�ʼ�һ��,��������˭?�ռ����м���?�շ���ϵ��������!</html>"); //
				label_1.setPreferredSize(new Dimension(-1, 22)); //
				label_1.setForeground(Color.RED); //

				panel_1.add(label_1, BorderLayout.NORTH); //
				panel_1.add(new JScrollPane(tree1), BorderLayout.CENTER);
				tabb.addTab("��Ա��ϵ", UIUtil.getImage("office_050.gif"), panel_1); //

				JPanel panel_2 = WLTPanel.createDefaultPanel(new BorderLayout()); //
				JLabel label_2 = new JLabel("�����ջ����뻷�ڵ���ת˳��չʾ"); //
				label_2.setToolTipText("<html>���г����������뻷��(����),���г��ڲ���Ա<br>����ǩ���ڲ���������Ա֮��Ľ��չ�ϵ��������ͬ��֧!</html>"); //
				label_2.setPreferredSize(new Dimension(-1, 22)); //
				label_2.setForeground(Color.RED); //

				panel_2.add(label_2, BorderLayout.NORTH); //
				panel_2.add(new JScrollPane(tree2), BorderLayout.CENTER);
				tabb.addTab("������ϵ", UIUtil.getImage("office_123.gif"), panel_2); //
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
		if (allReport != null || allReport.size() > 0) { //�����ж��ַ�ʽ����,ÿ�ֵĵ���
			JPopupMenu popup = new JPopupMenu("PopupMenu");
			for (int i = 0; i < allReport.size(); i++) {
				final HashMap param = (HashMap) allReport.get(i);
				JMenuItem menuItem = new JMenuItem((String) param.get("��������"));
				menuItem.setPreferredSize(new Dimension(115, 19));
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onExportHtmlReport(param); // ����Html����
					}
				});
				popup.add(menuItem);
			}
			btn_exporthtmlreport = new WLTPopupButton(WLTPopupButton.TYPE_WITH_RIGHT_TOGGLE, UIUtil.getLanguage("��������"), null, popup); //
			btn_exporthtmlreport.setPreferredSize(new Dimension(120, 20));

			if (allReport.containsKey("ȫ���������")) {//����
				btn_exporthtmlreport.getButton().addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						HashMap map_par = new HashMap();
						map_par.put("showType", "1");
						map_par.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
						map_par.put("html�ļ���", ((HashMap) allReport.get("ȫ���������")).get("html�ļ���"));
						map_par.put("word�ļ���", ((HashMap) allReport.get("ȫ���������")).get("word�ļ���"));
						onExportHtmlReport(map_par); // ����Html����
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
			if (str_id != null && str_id.equals(_id)) { //��������˵� �ڵ�ǰ��!
				dealPoolTree.setSelectionPath(new TreePath(allNodes[i].getPath())); //ѡ�����н��!����������Զ�չ�����·��!!
			}
		}
	}

	/**
	 * ȡ�ù�����ͼ!
	 * @return
	 * @throws Exception
	 */
	private WorkFlowDesignWPanel getGraphPanel() throws Exception {
		if (graphPanel != null) {
			return graphPanel;
		}
		String str_processcode = null; //
		if (str_wfinstance != null && !str_wfinstance.equals("")) {
			String str_sql = "select t2.code from pub_wf_prinstance t1,pub_wf_process t2 where t1.processid=t2.id and t1.id='" + this.str_rootInstanceId + "'"; //�ҳ�������ͼ!!
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql); //
			if (hvs == null || hvs.length <= 0) {
				throw new WLTAppException("��������ʵ��ID[" + this.str_rootInstanceId + "]û���ҵ���Ӧ�Ĺ���������,�����ǲ���������ɵ�,�������Ա��ϵ!"); //
			}
			str_processcode = hvs[0].getStringValue("code"); //
		}
		graphPanel = new WorkFlowDesignWPanel(str_processcode, false); //
		graphPanel.setToolBarVisiable(false);//�������ع�����
		graphPanel.lockGroupAndOnlyDoSelect(); //����,��ֻ����ѡ�����
		return graphPanel;
	}

	/**
	 * ȡ�ô�����ʷ�����ͽṹ!!!�ǳ��ؼ�!!
	 * @return
	 */
	private JTree getDealPoolTree(HashVO[] hvs) throws Exception {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("�����"); //
		HashMap map_node = new HashMap(); //
		DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[hvs.length]; //

		ArrayList al_childWFNode = new ArrayList(); //��¼���������̵��б�,��Ϊ��Ĭ����������Щ���!
		for (int i = 0; i < nodes.length; i++) {
			String str_userName = hvs[i].getStringValue("participant_username"); //�û�����

			String str_approveModel = hvs[i].getStringValue("currapprovemodel"); //����ģʽ!!
			String str_parentwfcreatebyid = hvs[i].getStringValue("parentwfcreatebyid"); //�����̵Ĵ���id!
			String str_submittoactivity_approvemodel = hvs[i].getStringValue("submittoactivity_approvemodel"); //�����̵Ĵ���id!

			StringBuilder sb_text = new StringBuilder(); //��������ÿ��������ʾ����!!
			if (hvs[i].getBooleanValue("isccto", false)) { //����ǳ��͵�,����ʾ����-
				sb_text.append("����-"); //
			}

			sb_text.append(str_userName); //������
			if (!hvs[i].getBooleanValue("isccto", false)) {
				sb_text.append("[" + wfUIUtil.getWFActivityName(hvs[i]) + "]"); //
			}

			if (!hvs[i].getBooleanValue("isccto", false)) { //������ǳ��͵�
				if ("2".equals(str_approveModel)) {
					sb_text.append("<��ǩ>"); //
				} else if ("3".equals(str_approveModel)) {
					sb_text.append("<���>"); //
				}
			}

			if ("3".equals(str_submittoactivity_approvemodel)) {
				sb_text.append("<������>"); //
			}

			if (str_parentwfcreatebyid != null && !str_parentwfcreatebyid.trim().equals("")) {
				sb_text.append("<������>"); //
			}

			if (!hvs[i].getBooleanValue("issubmit", false)) {
				if (hvs[i].getBooleanValue("isccto", false)) {
					sb_text.append("(δȷ��)"); //
				} else {
					sb_text.append("(δ����)"); //
				}
			}

			hvs[i].setAttributeValue("$TreeNodeText", sb_text.toString()); //
			hvs[i].setToStringFieldName("$TreeNodeText"); //�����˵��û���
			nodes[i] = new DefaultMutableTreeNode(hvs[i]); //
			if ("3".equals(str_approveModel)) { //����ǻ���,���¼����,����Ҫ�Ի��Ľ�������!
				al_childWFNode.add(nodes[i]); //
			}
			rootNode.add(nodes[i]); //
			map_node.put(hvs[i].getStringValue("id"), nodes[i]); //
		}

		//Ѱ�Ҹ����,���й���!!
		for (int i = 0; i < nodes.length; i++) {
			HashVO hvo_item = (HashVO) nodes[i].getUserObject();
			String str_createby = hvo_item.getStringValue("createbyid"); //������˭������!!!������������������������ʾʱ��Ҫ�ص�ǰһ��,������Ҫ��һ���ֶ�!! ��¼
			String str_parentWFCreateBy = hvo_item.getStringValue("parentwfcreatebyid"); //�������̵�ʵ��id
			if (str_parentWFCreateBy != null && !str_parentWFCreateBy.trim().equals("")) { //��������и������̵Ĵ�����,��˵��������¼�������̻�����ʱ�������Ǹ�����! ����ֱ�ӹҵ�ԭ�������˵ĸ�������,����ԭ��������ƽ����!! �������γ�һ�ֻع��Ч��!!
				str_createby = str_parentWFCreateBy; //�����п�����-99999,�������!!!
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
		dealPoolTree.setOpaque(false); //һ��Ҫ���͸��,������ٹ���ʱ����ְ���,������Ϊ����ĥ�úܾ�!!!!!!
		//dealPoolTree.setShowsRootHandles(true); //
		dealPoolTree.setRowHeight(17); //
		//tree.setOpaque(false);  //
		dealPoolTree.setRootVisible(false); //
		//tree.setBackground(Color.WHITE); //
		expandAll(dealPoolTree, new TreePath(rootNode), true); //չ�����н��,���滹Ҫ�ٴ�������������������
		for (int i = 0; i < al_childWFNode.size(); i++) { //�������еĻ������!
			DefaultMutableTreeNode itemNode = (DefaultMutableTreeNode) al_childWFNode.get(i); ////
			expandAll(dealPoolTree, new TreePath(itemNode.getPath()), false); //�������еĻ������!
		}
		dealPoolTree.setCellRenderer(new MyTreeCellRender()); //
		dealPoolTree.getSelectionModel().addTreeSelectionListener(this); //ѡ�����!!!
		dealPoolTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.getButton() == MouseEvent.BUTTON3) {
					showPopMenu(evt.getComponent(), evt.getX(), evt.getY()); // �����˵�
				} else {
					super.mousePressed(evt); //
				}
			}
		});

		return dealPoolTree; //
	}

	/**
	 * �ʴ���Ŀ�������ͻ�����ԭ������,��������һ�ַ��!!!
	 * @param hvs
	 * @return
	 * @throws Exception
	 */
	private JTree getDealPoolTree2(HashVO[] _dealpool) throws Exception {
		HashVO[] hvs_dealpool = new HashVO[_dealpool.length]; //
		for (int i = 0; i < hvs_dealpool.length; i++) {
			hvs_dealpool[i] = _dealpool[i].clone(); //
			if (hvs_dealpool[i].getStringValue("submittime", "").equals("")) {
				hvs_dealpool[i].setAttributeValue("submittime", "9999-12-31 23:59:59"); //֮��������һ����̬�����,��Ϊ�˺�������ʱ�Ѵ�������Ū�����!
			}
		}
		TBUtil.getTBUtil().sortHashVOs(hvs_dealpool, new String[][] { { "submittime", "N", "N" } }); //����һ��,�������ύʱ�����������!!!�������б��������ио�!!
		for (int i = 0; i < hvs_dealpool.length; i++) { //����˳���Ҫ��Ū�ؿ�!!!
			if (hvs_dealpool[i].getStringValue("submittime", "").equals("9999-12-31 23:59:59")) {
				hvs_dealpool[i].setAttributeValue("submittime", null); //
			}
		}

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("�Ѵ���Ļ���/����"); //
		dealPoolTree2 = new JTree(rootNode); //
		dealPoolTree2.setToolTipText("����Ҽ���չ��/����,����չʾ�ȹ���!"); //

		DefaultMutableTreeNode lastNode_level_1 = null; //
		DefaultMutableTreeNode lastNode_level_2 = null; //
		for (int i = 0; i < hvs_dealpool.length; i++) { //
			String str_toStringName = ""; //
			if ("Y".equals(hvs_dealpool[i].getStringValue("issubmit"))) { //����Ѵ���
				String str_subUser = hvs_dealpool[i].getStringValue("realsubmitername"); //
				if (str_subUser.indexOf("/") > 0) {
					str_subUser = str_subUser.substring(str_subUser.indexOf("/") + 1, str_subUser.length()); //
				}
				if (str_subUser.length() <= 2) {
					str_subUser = str_subUser + "  "; //����ֻ����λ���ٲ����ո�,�������������,��ʾЧ���ܺ�!
				}
				str_toStringName = str_subUser + " " + hvs_dealpool[i].getStringValue("realsubmitcorpname"); //
				hvs_dealpool[i].setAttributeValue("$ToString", str_toStringName); //
			} else { //����Ǵ���!
				str_toStringName = hvs_dealpool[i].getStringValue("participant_username") + " " + hvs_dealpool[i].getStringValue("participant_userdeptname"); //
				hvs_dealpool[i].setAttributeValue("$ToString", str_toStringName); //
			}
			hvs_dealpool[i].setToStringFieldName("$ToString"); //

			String str_blcorpName = getOneItemCorpName(hvs_dealpool[i]); //����/һ��
			String str_actName = getOneItemActName(hvs_dealpool[i]); //��������
			if (str_blcorpName == null) {
				str_blcorpName = "δ֪����"; //
			}
			if (str_actName == null) {
				str_actName = "δ֪����"; //
			}

			String str_lastBlCorpName = (i == 0 ? null : getOneItemCorpName(hvs_dealpool[i - 1])); //��һ��������
			String str_lastActName = (i == 0 ? null : getOneItemActName(hvs_dealpool[i - 1])); //��һ��������
			if (str_blcorpName.equals(str_lastBlCorpName)) { //�����ǰ�����һ��
				if (str_actName.equals(str_lastActName)) { //�����������ǰ�滷��һ��,����ԭ���Ķ����ڵ����һ��
					DefaultMutableTreeNode node_this = new DefaultMutableTreeNode(hvs_dealpool[i]); //�ȴ���һ��һ�����!
					lastNode_level_2.add(node_this); //
				} else { //�����ǰ�滷�ڲ�һ��!��Ҫ����һ������
					DefaultMutableTreeNode node_1_1 = new DefaultMutableTreeNode(str_actName); //�ȴ���һ��һ�����!
					DefaultMutableTreeNode node_this = new DefaultMutableTreeNode(hvs_dealpool[i]); //�ȴ���һ��һ�����!
					node_1_1.add(node_this); //
					lastNode_level_1.add(node_1_1); //
					lastNode_level_2 = node_1_1; //
				}
			} else { //ֱ�Ӵ���һ��
				DefaultMutableTreeNode node_1 = new DefaultMutableTreeNode(str_blcorpName); //�ȴ���һ��һ�����!
				DefaultMutableTreeNode node_1_1 = new DefaultMutableTreeNode(str_actName); //�ȴ���һ��һ�����!
				DefaultMutableTreeNode node_this = new DefaultMutableTreeNode(hvs_dealpool[i]); //�ȴ���һ��һ�����!
				node_1_1.add(node_this); //
				node_1.add(node_1_1); //
				rootNode.add(node_1); //
				lastNode_level_1 = node_1; //
				lastNode_level_2 = node_1_1; //
			}
		}

		dealPoolTree2 = new JTree(rootNode); //
		dealPoolTree2.setUI(new WLTTreeUI(true));
		dealPoolTree2.setOpaque(false); //һ��Ҫ���͸��,������ٹ���ʱ����ְ���,������Ϊ����ĥ�úܾ�!!!!!!
		//dealPoolTree.setShowsRootHandles(true); //
		dealPoolTree2.setRowHeight(17); //
		//tree.setOpaque(false);  //
		//dealPoolTree2.setRootVisible(false); //

		dealPoolTree2.setCellRenderer(new MyTreeCellRender2()); //
		dealPoolTree2.getSelectionModel().addTreeSelectionListener(this); //ѡ�����!!!

		dealPoolTree2.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.getButton() == MouseEvent.BUTTON3) {
					showPopMenu2(evt.getComponent(), evt.getX(), evt.getY()); // �����˵�
				}
			}
		});

		return dealPoolTree2; //
	}

	private String getOneItemCorpName(HashVO _hvo) {
		String str_blcorpName = _hvo.getStringValue("curractivity_belongdeptgroup"); //����/һ��
		String str_parentid = _hvo.getStringValue("parentinstanceid"); //
		if (str_parentid != null && !str_parentid.equals("")) { //�����Ϊ��,��˵����������!!
			str_blcorpName = _hvo.getStringValue("prinstanceid_fromparentactivitybldeptgroup"); //�������̵�����
		}
		return str_blcorpName; //
	}

	private String getOneItemActName(HashVO _hvo) {
		String str_blcorpName = _hvo.getStringValue("curractivity_wfname"); //����/һ��
		String str_parentid = _hvo.getStringValue("parentinstanceid"); //
		if (str_parentid != null && !str_parentid.equals("")) { //�����Ϊ��,��˵����������!!
			str_blcorpName = _hvo.getStringValue("prinstanceid_fromparentactivityname"); //�������̵�����
		}
		return str_blcorpName; //
	}

	/**
	 * ����ѡ��仯!
	 * @param e
	 */
	public void valueChanged(TreeSelectionEvent _event) {
		onTreeSelectedChanged(_event); //
	}

	//���Ϳؼ�ѡ��仯!!!
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
				HashVO hvo = (HashVO) node.getUserObject(); //ȡ�ý��
				String str_dealpoolId = hvo.getStringValue("id"); //����
				for (int i = 0; i < dealPoolBillList.getRowCount(); i++) {
					String str_id = ((StringItemVO) dealPoolBillList.getValueAt(i, "id")).getStringValue(); //
					if (str_id.equals(str_dealpoolId)) { //����ҵ�!!!
						dealPoolBillList.addSelectedRow(i); //
						break; //
					}
				}
			}
			reLoadGraphFromTree(dealPoolTree); //��������ѡ�еĽ��,ˢ������ͼ
		} else if (_event.getSource() == dealPoolTree2.getSelectionModel()) {
			TreePath[] selectedPaths = dealPoolTree2.getSelectionPaths(); //
			if (selectedPaths == null) {
				return;
			}

			for (int r = 0; r < selectedPaths.length; r++) { //
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedPaths[r].getLastPathComponent(); //
				Object selObj = node.getUserObject(); //ȡ�ý��;  //
				if (selObj instanceof HashVO) {
					HashVO hvo = (HashVO) selObj; //
					String str_dealpoolId = hvo.getStringValue("id"); //����
					for (int i = 0; i < dealPoolBillList.getRowCount(); i++) {
						String str_id = ((StringItemVO) dealPoolBillList.getValueAt(i, "id")).getStringValue(); //
						if (str_id.equals(str_dealpoolId)) { //����ҵ�!!!
							dealPoolBillList.addSelectedRow(i); //
							break; //
						}
					}
				}
			}
			reLoadGraphFromTree(dealPoolTree2); //��������ѡ�еĽ��,ˢ������ͼ
		}
		isTreeSelectChanging = false; //
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == dealPoolBillList) {
			onSelectedDealPoolList();
		}
	}

	/**
	 * ѡ�б���еļ�¼,��������
	 */
	private void onSelectedDealPoolList() {
		if (isTreeSelectChanging) {
			return; //���������ѡ�б仯��,��Ҫ��ȥˢ������,����������ѭ��!!
		}
		isTableSelectChanging = true;
		BillVO[] selectedVOs = dealPoolBillList.getSelectedBillVOs(); //
		if (selectedVOs == null || selectedVOs.length == 0) {
			return;
		}
		for (int r = 0; r < selectedVOs.length; r++) {
			String str_id = selectedVOs[r].getStringValue("id"); //ȡ������(pub_wf_dealpool.id)
			findAndScrollToTreeOneNode(dealPoolTree, str_id); //
			findAndScrollToTreeOneNode(dealPoolTree2, str_id); //
		}

		reLoadGraphFromTree(dealPoolTree); //Ҳ�Ǵ�����ȡ��,ˢ��ͼ!
		isTableSelectChanging = false;
	}

	private void findAndScrollToTreeOneNode(JTree _tree, String _id) {
		_tree.clearSelection(); //
		DefaultMutableTreeNode[] allNodes = getAllChildrenNodes((DefaultMutableTreeNode) _tree.getModel().getRoot(), true); //ȡ�����н��!!!!
		for (int i = 1; i < allNodes.length; i++) { //
			Object obj = allNodes[i].getUserObject(); //
			if (obj instanceof HashVO) { //������HashVO
				HashVO hvo = (HashVO) obj; //
				String str_treeObjId = hvo.getStringValue("id"); //
				if (_id.equals(str_treeObjId)) {
					scrollToOneNode(_tree, allNodes[i]); //ѡ�в���������һ��!!!
					break; //
				}
			}
		}
	}

	private HashVO endActivity[] = null;

	//��ȡ��������
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

	//������Ľ������� by haoming 2016-04-29
	//��̫ƽ��������ϵͳ���������һ�������ǽ��������Ǵ����¼û��û�д˻��ڡ����ʱ�������ڲ����ǳ����� һ�ֽ���������ڷ�����������һ��������ʷ���ڶ��־����� ui���ж��Ƿ�����һ�������õڶ���
	public void addEndAcitivity(ArrayList al_temp) {
		try {
			if ("end".equalsIgnoreCase(processStatus) && al_temp.size() > 0) { //������̽�����
				//�ж�hvs�������ʱ�Ļ����Ƿ�����ͨ���ڣ���Ҫ�����end�Ľ�������������ϡ��������̽����ˣ����Ǽ�ؿ�������by haoming 2016-04-28
				HashVO lastAcitivityVO = (HashVO) al_temp.get(al_temp.size() - 1); //��ǰ�����һ��
				//�ж����һ�������ǲ��ǰ�·��������
				String processid = lastAcitivityVO.getStringValue("processid");
				HashVO[] hvs_actsInfos = getEndAcitivty(processid); //��ȡ��������
				if (hvs_actsInfos != null && hvs_actsInfos.length > 0) {
					for (int i = 0; i < hvs_actsInfos.length; i++) {
						if (hvs_actsInfos[i].getStringValue("fromactivityid", "").equals(lastAcitivityVO.getStringValue("curractivity"))) {
							HashVO cp = (HashVO) tbUtil.deepClone(lastAcitivityVO);
							cp.setAttributeValue("id", "-999");
							cp.setAttributeValue("batchno", "1000"); //�㹻��
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
		if (selectedPaths != null && selectedPaths.length > 0) { //�����ѡ�еĽ��
			ArrayList al_temp = new ArrayList(); //
			for (int i = 0; i < selectedPaths.length; i++) { //��������ѡ�е�·��!!!
				DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selectedPaths[i].getLastPathComponent(); //
				if (selNode.isRoot()) {
					continue;
				}
				Object obj = selNode.getUserObject(); //
				if (obj instanceof HashVO) {
					HashVO hvoItem = (HashVO) obj; //
					if (str_processId == null) {
						str_processId = hvoItem.getStringValue("processid"); //��Ϊ����һ��ѡ�е��������̶���һ��ѡ�е���������!! ����ֻ����һ��!
					}
					al_temp.add(hvoItem); //��¼����!
				}
			}
			DefaultMutableTreeNode[] allNodes = getAllChildrenNodes((DefaultMutableTreeNode) _tree.getModel().getRoot(), true); //ȡ�����н��!!!!
			if (allNodes.length > 0 && allNodes[allNodes.length - 1].getUserObject() == al_temp.get(al_temp.size() - 1)) {//�ж�ѡ�е��Ƿ������һ������
				addEndAcitivity(al_temp);
			}
			hvs = (HashVO[]) al_temp.toArray(new HashVO[0]); //
		} else { //���û��ѡ�еĽ��,��ֱ��ʹ�ø����!
			ArrayList al_temp = new ArrayList(); //
			DefaultMutableTreeNode[] allNodes = getAllChildrenNodes((DefaultMutableTreeNode) _tree.getModel().getRoot(), true); //ȡ�����н��!!!!
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
			//������������
			ArrayList al_temp = new ArrayList(); //
			if (!getGraphPanel().getCurrentProcessVO().getId().equals(str_processId)) {
				getGraphPanel().loadGraphByID(str_processId); //���¼����µ�����ͼ!
			}

			//�Ȼ�Ŀ�껷��
			for (int i = 0; i < hvs.length; i++) {
				if (hvs[i].getStringValue("processid").equals(str_processId)) { //�����ͬһ������
					if (hvs[i].getStringValue("submittoactivity") != null) {
						al_temp.add(new String[] { hvs[i].getStringValue("submittoactivity"), "To" }); //ȥ����!!
					}
				}
			}
			//�ٻ���Դ����
			for (int i = 0; i < hvs.length; i++) {
				if (hvs[i].getStringValue("processid").equals(str_processId)) { //�����ͬһ������
					al_temp.add(new String[] { hvs[i].getStringValue("fromactivity"), "From" }); //���ĸ���������
				}
			}
			//��󻭵�ǰ����,��֤��ǰ������Զ����ʾ,�����ᱻ�������ڳ��!
			for (int i = 0; i < hvs.length; i++) {
				if (hvs[i].getStringValue("processid").equals(str_processId)) { //�����ͬһ������
					al_temp.add(new String[] { hvs[i].getStringValue("curractivity"), "Y" }); //��ǰ�ĸ�����
				}
			}

			String[][] str_allcells = new String[al_temp.size()][2]; //
			for (int i = 0; i < str_allcells.length; i++) {
				str_allcells[i] = (String[]) al_temp.get(i); //
			}
			getGraphPanel().lightCell(str_allcells); //������ʾ���л���!!

			//����������������!
			ArrayList al_lines = new ArrayList(); //
			for (int i = 0; i < hvs.length; i++) {
				if (hvs[i].getStringValue("processid").equals(str_processId)) { //�����ͬһ������
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

	//�õ������ӽ��!
	private DefaultMutableTreeNode[] getAllChildrenNodes(TreeNode _rootNode, boolean _isContainSelf) {
		Vector vector = new Vector();
		visitAllNodes(vector, _rootNode, 0, _isContainSelf); //
		return (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //
	}

	private void visitAllNodes(Vector _vector, TreeNode _node, int _count, boolean _isContainSelf) {
		if (_isContainSelf) { //��������ӽ��,����Զ����!
			_vector.add(_node); // ����ý��
		} else { //����������ӽ��!
			if (_count > 0) { //����ǵ�һ��,����!
				_vector.add(_node); // ����ý��
			}
		}
		int li_count = _count + 1; //
		if (_node.getChildCount() >= 0) {
			for (Enumeration e = _node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // �ҵ��ö���!!
				visitAllNodes(_vector, childNode, li_count, _isContainSelf); // �������Ҹö���
			}
		}
	}

	//����ĳĳһ�����
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
	 * ����Html����.
	 */
	private void onExportHtmlReport(HashMap map_par) {
		try {
			if ("N".equals((String) map_par.get("�Ƿ�����"))) {
				MessageBox.showInfo(this, "��Ǹ��û��ʹ�ô˱����Ȩ��!");
				return;
			}
			map_par.put("prinstanceid", str_wfinstance); //
			map_par.put("billvo", this.billVO); //
			map_par.put("roles", ClientEnvironment.getInstance().getLoginUserRoleIds());
			map_par.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
			if (map_par.containsKey("�Զ���Ȩ��")) {//һ���˲�����������Ͳ�������
				String classname = (String) map_par.get("�Զ���Ȩ��");
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
			//��������Զ����� �����/2013-01-18��
			String str_class = TBUtil.getTBUtil().getSysOptionStringValue("������������Ŀ�Զ�����", "cn.com.infostrategy.bs.workflow.WorkflowHtmlReportDMO");
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
			final JMenuItem menuItem_1 = new JMenuItem("�鿴��ϸ��Ϣ"); //
			final JMenuItem menuItem_2 = new JMenuItem("ֱ�ӳ���ĳһ��"); //
			final JMenuItem menuItem_delalltask = new JMenuItem("ɾ������������"); //
			final JMenuItem menuItem_3 = new JMenuItem("�Ƚ϶�����Ϣ"); //
			ActionListener actListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == menuItem_1) {
						onShowTreeNodeInfo(thisTree); //
					} else if (e.getSource() == menuItem_2) {
						onDirCancelOneStep(); //ֱ�ӳ���ĳһ��!
					} else if (e.getSource() == menuItem_delalltask) {
						onDeleteAllChildrenTask(); //ֱ�ӳ���ĳһ��!
					} else if (e.getSource() == menuItem_3) {
						onCompareSomRecords(); //�Ƚ϶�����Ϣ!
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
			final JMenuItem menuItem_1 = new JMenuItem("չ������", UIUtil.getImage("office_163.gif")); //
			final JMenuItem menuItem_2 = new JMenuItem("��������", UIUtil.getImage("office_078.gif")); //
			final JMenuItem menuItem_3 = new JMenuItem("����ȥ��", UIUtil.getImage("office_200.gif")); //
			final JMenuItem menuItem_4 = new JMenuItem("����չʾ", UIUtil.getImage("office_109.gif")); //
			final JMenuItem menuItem_5 = new JMenuItem("��ϸ��Ϣ", UIUtil.getImage("office_200.gif")); //

			final JMenuItem menuItem_6 = new JMenuItem("���������ɾ��", UIUtil.getImage("zt_021.gif"));
			final JMenuItem menuItem_7 = new JMenuItem("�����˻�", UIUtil.getImage("zt_072.gif"));

			menuItem_3.setToolTipText("��ʾһ����������˭����,�������˭!"); //
			menuItem_4.setToolTipText("����������ƽ�̿���չʾ,����һ���㵥����һ����!"); //

			menuItem_6.setToolTipText("�����������,ɾ��ĳһ������");
			menuItem_7.setToolTipText("�������ѽ���,�˻�����һ��δ����ʱ״̬");

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

				//�Ѱ�����׷�ӽ����˻�
				String str_taskOffId = getTaskOffId(billVO);
				if (str_taskOffId != null && !"".equals(str_taskOffId)) {
					popMenu2.add(menuItem_7);
				}
			}
		}
		popMenu2.show(_compent, _x, _y); //
	}

	//����Ա���ƻ�������̽��� �����/2013-05-29��
	private void delWFPerson(JTree _tree) {
		TreePath seledPath = _tree.getSelectionPath();
		if (seledPath == null) {
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent();
		Object selobj = node.getUserObject();
		if (!(selobj instanceof HashVO)) {
			MessageBox.show(this, "��ѡ������Ա�����д˲���!");
			return;
		}

		if (!MessageBox.confirm(this, "��ȷ��Ҫɾ���û�������̽ڵ��´�����Ա�����?")) {
			return;
		}

		HashVO hvo = (HashVO) selobj;
		String str_dealpoolid = hvo.getStringValue("id", "");
		String str_prinstanceid = hvo.getStringValue("prinstanceid", "");
		String str_parentinstanceid = hvo.getStringValue("parentinstanceid", "");
		String participant_user = hvo.getStringValue("participant_user", ""); //������ID

		if ("".equals(str_parentinstanceid)) {
			MessageBox.show(this, "��ѡ���������̽ڵ��´�����Ա�����д˲���!");
			return;
		}

		if ("".equals(str_dealpoolid) || "".equals(str_prinstanceid)) {
			MessageBox.show(this, "�����쳣!���̴���ID������ʵ��IDΪ��!�޷�ɾ��!");
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
			String str_endResult = service.endWorkFlow_admin(str_prinstanceid, myvo, participant_user, str_message, str_msgfile, "����������", billVO, null);
			MessageBox.show(this, str_endResult);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	//����Ա���������̽������� �����/2013-05-29��
	private void endWFRevoke(JTree _tree) {
		TreePath seledPath = _tree.getSelectionPath();
		if (seledPath == null) {
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent();
		Object selobj = node.getUserObject();
		if (!(selobj instanceof HashVO)) {
			MessageBox.show(this, "��ѡ������Ա�����д˲���!");
			return;
		}

		if (!MessageBox.confirm(this, "��ȷ��Ҫ���ظý����ڵ���?")) {
			return;
		}

		HashVO hvo = (HashVO) selobj;
		String str_dealpoolid = hvo.getStringValue("id", "");
		String str_prinstanceid = hvo.getStringValue("prinstanceid", "");
		String str_parentinstanceid = hvo.getStringValue("parentinstanceid", "");
		String participant_user = hvo.getStringValue("participant_user", ""); //������ID
		String str_taskOffId = getTaskOffId(billVO);

		if (!"".equals(str_parentinstanceid)) {
			MessageBox.show(this, "�Ǹ����̽����ڵ㲻�ܳ���!");
			return;
		}

		if ("".equals(str_dealpoolid) || "".equals(str_prinstanceid)) {
			MessageBox.show(this, "�����쳣!���̴���ID������ʵ��ID!�޷�����!");
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
		if (hvo == null) { //���û�ж���hvo,����ǰ�Ļ���!!��ֱ��ȡ
			return null;
		} else {
			return hvo.getStringValue("task_taskoffid"); //���к���ȡ
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
			MessageBox.show(this, "��ѡ������Ա�����д˲���!"); //
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
		sb_text.append("�˹����Ǽ��������������˭�ύ������,Ȼ�����ύ�˸�˭!\r\n�������������Դ���������һ��,Ŀ�����������һ��,���ڻ�ǩ��粿��ʱ,���Ǻ�����,��ʱ�˹��ܾͻ������ó�!\r\n\r\n"); //
		sb_text.append("����������:[" + hvo.toString() + "]\r\n\r\n");

		sb_text.append("--------------------- �������� ---------------------\r\n ��Ա/����:[" + str_creatername + "  " + str_createcorpname + "]\r\n������ʱ��:[" + str_createtime + "]\r\n"); //

		sb_text.append("\r\n\r\n"); //
		String str_totitle = "--------------------- ȥ������ ---------------------\r\n"; //
		if (str_submittime.equals("")) {
			sb_text.append(str_totitle + "��û�ύ��,˵���⻹��һ��δ��������..."); //
		} else {
			if (str_submittousername.equals("")) {
				sb_text.append(str_totitle + "���ǻ�ǩ�����̵Ľ�������,����ϵͳ�Զ�����,�����˹��ύ��,����ʱ��:" + str_submittime); //
			} else {
				sb_text.append(str_totitle);
				sb_text.append(" ��Ա/����:");
				String[] str_users = TBUtil.getTBUtil().split(str_submittousername, ";"); //
				String[] str_corps = TBUtil.getTBUtil().split(str_submittocorpname, ";"); //
				for (int i = 0; i < str_users.length; i++) {
					sb_text.append("[" + str_users[i] + "  " + str_corps[i] + "]");
				}
				sb_text.append("\r\n");
				sb_text.append("��ȥ��ʱ��:[" + str_submittime + "]\r\n");
			}
		}

		sb_text.append("\r\n\r\n");
		sb_text.append("��������ʾ:\r\n");
		sb_text.append("1.����ͼ�к�ɫ���ʾ��ѡ�еĵ�ǰ����,�ۺ�ɫ���ʾ�Ǵ���������,��ɫ���ʾ��ȥ������!\r\n  �����Դ��ȥ����ͬһ����,��ֻ�з�ɫ��û����ɫ!����ǲ����ڲ���ת��,��ֻ��һ�����!\r\n");
		sb_text.append("2.�������ת����Դ����ť�����Զ�ѡ����Դ����!\r\n");
		sb_text.append("3.���ڻ�ǩʱ���ܴ��ڶ��������,���ԡ���ת��Ŀ�����񡿲��ÿ���,��ϵͳ��ʱ���ṩ�˹���!\r\n");

		int li_result = MessageBox.showOptionDialog(this, sb_text.toString(), "��ʾ", new String[] { "[office_078.gif]��ת����Դ", " ȡ  �� " }); //
		if (li_result == 0) {
			if (str_createbyid == null || str_createbyid.equals("")) {
				MessageBox.show(this, "���������ĵ�һ������,�޷�����ת����!"); //
				return; //
			}
			findAndScrollToTreeOneNode(_tree, str_createbyid); //
		}
	}

	/**
	 * ����չʾ...
	 */
	private void onHalignView(JTree _tree) {
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) _tree.getModel().getRoot(); //
		int li_count = rootNode.getChildCount(); //

		int li_itemwidth = 210; //
		JPanel allContentPanel = WLTPanel.createDefaultPanel(null);
		JPanel[] panels = new JPanel[li_count]; //
		for (int i = 0; i < li_count; i++) { //���������ӽ��...
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) rootNode.getChildAt(i); //
			String hvo = (String) childNode.getUserObject(); //

			DefaultMutableTreeNode[] oldNodes = getAllChildrenNodes(childNode, false); //���оɵĽ��....
			DefaultMutableTreeNode[] newNodes = new DefaultMutableTreeNode[oldNodes.length]; //
			HashMap mapOldNew = new HashMap(); //
			HashMap mapOldNew2 = new HashMap(); //
			for (int j = 0; j < oldNodes.length; j++) {
				newNodes[j] = (DefaultMutableTreeNode) oldNodes[j].clone(); //��¡..
				mapOldNew.put(newNodes[j], oldNodes[j]); //
				mapOldNew2.put(oldNodes[j], newNodes[j]); //
			}
			for (int j = 0; j < newNodes.length; j++) { //
				DefaultMutableTreeNode myOldLink = (DefaultMutableTreeNode) mapOldNew.get(newNodes[j]); //�Ҷ�Ӧ�ľɵĽ��
				DefaultMutableTreeNode myOldLinkParent = (DefaultMutableTreeNode) myOldLink.getParent(); //�Ҷ�Ӧ�ľɽ��ĸ���!
				if (myOldLinkParent != childNode) { //ֻҪ���ײ��Ǹ�!
					DefaultMutableTreeNode myNewParent = (DefaultMutableTreeNode) mapOldNew2.get(myOldLinkParent); //�ɹ��ҵ��ҵĸ���!!
					myNewParent.add(newNodes[j]); //
				}
			}

			DefaultMutableTreeNode itemRootNode = new DefaultMutableTreeNode("��" + hvo + "��"); //
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
			allContentPanel.add(panels[i]); //����!!!
		}
		allContentPanel.setPreferredSize(new Dimension(5 + li_count * li_itemwidth, 505)); //
		JPanel ctPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		ctPanel.add(new JScrollPane(allContentPanel)); //

		int li_dialog_width = li_count * li_itemwidth + 25; //
		if (li_dialog_width > 1000) {
			li_dialog_width = 1000; //
		}
		BillDialog dialog = new BillDialog(this, "����չʾ", li_dialog_width, 570); //
		dialog.getContentPane().add(ctPanel); //
		dialog.setVisible(true); //
	}

	//��ʾʾ���ͽ�����Ϣ!!!
	private void onShowTreeNodeInfo(JTree _tree) {
		TreePath seledPath = _tree.getSelectionPath();
		if (seledPath == null) {
			return;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent(); //
		Object selobj = node.getUserObject(); //
		if (!(selobj instanceof HashVO)) {
			MessageBox.show(this, "��ѡ������Ա�����д˲���!"); //
			return; //
		}
		HashVO hvo = (HashVO) selobj; //
		String[] str_keys = hvo.getKeys(); //
		StringBuilder sb_text = new StringBuilder(); //
		sb_text.append("�û�=[" + hvo.getStringValue("participant_usercode") + "/" + hvo.getStringValue("participant_username") + "]\r\n"); ////
		sb_text.append("��������=[" + getEmptyStr(hvo.getStringValue("curractivity_wfname")) + "]\r\n"); ////
		sb_text.append("�Ƿ��͵�=[" + getEmptyStr(hvo.getStringValue("isccto")) + "]\r\n"); ////
		sb_text.append("�Ƿ���=[" + getEmptyStr(hvo.getStringValue("issubmit")) + "]\r\n"); ////
		sb_text.append("����ʱ��=[" + getEmptyStr(hvo.getStringValue("submittime")) + "]\r\n"); ////
		sb_text.append("��������=[" + getEmptyStr(hvo.getStringValue("submitisapprove")) + "]\r\n"); ////

		if (ClientEnvironment.getInstance().isAdmin()) {
			sb_text.append("\r\n"); //
			sb_text.append("������ϸ��Ϣ(pub_wf_dealpool):\n"); //
			for (int i = 0; i < str_keys.length; i++) {
				sb_text.append("[" + str_keys[i] + "]=[" + getEmptyStr(hvo.getStringValue(str_keys[i])) + "]\r\n"); //
			}

			try {
				sb_text.append("\r\n������ʵ����ϸ��Ϣ(pub_wf_prinstance.id=[" + hvo.getStringValue("rootinstanceid", "") + "]):\n"); //
				HashVO[] hvs2 = UIUtil.getHashVoArrayByDS(null, "select * from pub_wf_prinstance where id='" + hvo.getStringValue("rootinstanceid", "") + "'"); //
				if (hvs2.length > 0) {
					String[] str_keys2 = hvs2[0].getKeys(); //
					for (int i = 0; i < str_keys2.length; i++) {
						String str_text = "[" + str_keys2[i] + "]=[" + getEmptyStr(hvs2[0].getStringValue(str_keys2[i])) + "]";
						if (str_keys2[i].equalsIgnoreCase("routemark")) {
							str_text = str_text + " ����������Ǵ�˵�е�·�ɱ�ǣ�����������";
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

	//ֱ�ӳ���ĳһ��!!!
	private void onDirCancelOneStep() {
		TreePath seledPath = dealPoolTree.getSelectionPath();
		if (seledPath == null) {
			MessageBox.show(this, "ѡ�еĽ��Ϊ��!");
			return;
		}

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent(); //
		if (node.isRoot()) {
			MessageBox.show(this, "����ֱ���˻ص������,��Ϊ���Ƿ��������!"); //�Ժ�Ҳ����,�����������������!!
			return; //
		}

		HashVO hvo = (HashVO) node.getUserObject(); //
		String str_text = hvo.toString(); //
		if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ�˻ص�[" + str_text + "]��һ����?\r\n�⽫����������߹������в���!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}

		try {
			String str_sel_prinstanceid = hvo.getStringValue("prinstanceid"); //����ʵ��id
			String str_sel_prdealpoolid = hvo.getStringValue("id"); //����ʵ��id
			String str_sel_taskid = UIUtil.getStringValueByDS(null, "select id from pub_task_off where createbydealpoolid='" + str_sel_prdealpoolid + "'"); //
			//			if (str_sel_taskid == null) {
			//				MessageBox.show(this, "û���ҵ��ý����Ѱ�����,��������������������!\r\n������ѡ��������������ز���!"); //
			//				return; //
			//			}

			//�ҵ������������������! Ȼ������ű��г���ɾ����Щid�ļ�¼!Ȼ���޸����������еı��˵ļ�¼! Ȼ���Ѱ�����Ӵ���������лؿ�����! Ȼ��
			DefaultMutableTreeNode[] allChildNodes = getAllChildrenNodes(node, false); //
			System.out.println("�ӽ������=" + allChildNodes.length); //
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
			MessageBox.show(this, "���سɹ�,���˳�ҳ��ˢ��!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//ֱ�ӳ���ĳһ��!!!
	private void onDeleteAllChildrenTask() {
		try {
			TreePath seledPath = dealPoolTree.getSelectionPath();
			if (seledPath == null) {
				MessageBox.show(this, "ѡ�еĽ��Ϊ��!");
				return;
			}

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) seledPath.getLastPathComponent(); //
			if (node.isRoot()) {
				MessageBox.show(this, "����ֱ���˻ص������,��Ϊ���Ƿ��������!"); //�Ժ�Ҳ����,�����������������!!
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

			if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫɾ���ö���������?�⽫ͬʱɾ��������������,������ִ������SQL:\r\n" + sb_text.toString(), "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			UIUtil.executeBatchByDS(null, str_sqls); //
			MessageBox.show(this, "ɾ����������ɹ�!���˳�ˢ��ҳ��!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//ȡ��ѡ�н���VO����!
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

	//�Ƚ϶�����Ϣ!����ʱ���ݷ�������ʱ���Կ��ٺ���Ƚ϶�����Ϣ!������һ�����֪����Щ��һ��!�Ӷ����ٶ�λ����!
	private void onCompareSomRecords() {
		HashVO[] hvs = getSelectedNodeVOs(); //
		if (hvs == null) {
			MessageBox.show(this, "������ѡ��һ�����!"); //
			return; //
		}
		LookDealPoolAndTaskInfoDialog dialog = new LookDealPoolAndTaskInfoDialog(this, "�Ƚϲ鿴", 900, 700, hvs); //
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
		String str_msg = refVO.getId(); //��Ϣ!
		String str_msg_real = refVO.getCode(); //������������,������Ǽ���ǰ������!
		String str_reason = _event.getBillListPanel().getRealValueAtModel(li_row, "submitmessage_viewreason"); //ԭ��!
		CopyHistMsgDialog dialog = new CopyHistMsgDialog(this, "�鿴���", str_msg, str_msg_real, str_reason, false, billVO); //
		dialog.setVisible(true); //
	}

	/**
	 * �˷������������ڵ�ǰ���ı���Ҳ���ǻ���ԭ����˼�� ����=>����·��;
	 * ��չΪ ����=>����·��->����������;
	 * ���б���·��������Ǳ����
	 * ���˳���ǰ�����ı���
	 * û��Ȩ�޵�Ҳ�ܿ������ǲ��ܵ���
	 * ����������һ���ֶΣ�report_roles����������Ȩ�ޣ��� ����·��=>��ɫ1||��ɫ2;
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
	 * ��ʾ��Ϣ..
	 */
	private void onShowMsg() {
		try {
			String str_billtype = billVO.getStringValue("billtype"); // ��������
			String str_busitype = billVO.getStringValue("busitype"); // ҵ������

			String str_sql = "select t1.processid,t2.code wfcode,t2.name wfname from pub_wf_prinstance t1,pub_wf_process t2 where t1.processid=t2.id and t1.id='" + str_wfinstance + "'"; //
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql); //
			String str_wfcode = hvs[0].getStringValue("wfcode"); //
			String str_wfname = hvs[0].getStringValue("wfname"); //

			StringBuffer sb_text = new StringBuffer(); //
			sb_text.append("��������:[" + str_billtype + "]\r\n"); //
			sb_text.append("ҵ������:[" + str_busitype + "]\r\n"); //
			sb_text.append("���̱���:[" + str_wfcode + "]\r\n"); //
			sb_text.append("��������:[" + str_wfname + "]\r\n"); //
			MessageBox.showTextArea(this, "������Ϣ�鿴", sb_text.toString()); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	class MyTreeCellRender extends DefaultTreeCellRenderer {
		private static final long serialVersionUID = -4155023337948316932L;
		private Color color1 = new Color(100, 100, 100);
		private Icon icon1 = UIUtil.getImage("office_034.gif"); //���ύ��
		private Icon icon2 = UIUtil.getImage("office_138.gif"); //û�ύ��
		private Icon iconChildWF = UIUtil.getImage("user.gif"); //������

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value; //
			HashVO hvo = (HashVO) node.getUserObject(); //
			boolean isSubmit = hvo.getBooleanValue("issubmit", false); //
			String str_parentInstanceId = hvo.getStringValue("parentinstanceid"); //����ʵ��,�����Ϊ��,��˵����������
			String str_text = value.toString(); //
			JLabel label = new JLabel(str_text); //

			if (str_parentInstanceId != null && !str_parentInstanceId.trim().equals("")) { //�����������,Ϊ��ǿ�����ֻ��Ч��,���������̵�ͼ�겻һ��!!
				label.setIcon(iconChildWF); //
			} else {
				if (isSubmit) { //������ύ
					label.setIcon(icon1); //
				} else {
					label.setIcon(icon2);
				}
			}

			if (sel) { //���ѡ��!!
				label.setOpaque(true); //��͸��!!
				label.setBackground(Color.YELLOW); //
				if (isSubmit) { //������ύ
					label.setForeground(color1); //
				} else {
					label.setForeground(Color.RED); //
				}
			} else {
				label.setOpaque(false); //͸��!!
				if (isSubmit) { //������ύ
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
		private Icon icon_root = UIUtil.getImage("office_030.gif"); //Ŀ¼���
		private Icon icon_dir1 = UIUtil.getImage("office_123.gif"); //����
		private Icon icon_dir2 = UIUtil.getImage("office_113.gif"); //����

		private Icon icon1 = UIUtil.getImage("office_021.gif"); //���ύ��
		private Icon icon2 = UIUtil.getImage("office_043.gif"); //û�ύ��
		private Color color1 = new Color(100, 100, 100); //
		private Color color_user = new Color(0, 100, 0);

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value; //
			Object obj = node.getUserObject(); //
			String str_text = value.toString(); //
			JLabel label = new JLabel(str_text); //

			if (obj instanceof String) { //Ŀ¼���
				label.setForeground(color1); //
				if (node.isRoot()) { //�����
					label.setIcon(icon_root); //
				} else {
					if (node.getLevel() == 1) {
						label.setIcon(icon_dir1); //
					} else if (node.getLevel() == 2) {
						label.setIcon(icon_dir2); //
					}
				}

				if (sel) { //���ѡ��!!
					label.setOpaque(true); //��͸��!!
					label.setBackground(Color.YELLOW); //
				} else {
					label.setOpaque(false); //͸��!!
				}
			} else if (obj instanceof HashVO) {
				HashVO hvo = (HashVO) obj; //
				boolean isSubmit = hvo.getBooleanValue("issubmit", false); //
				if (sel) { //���ѡ��!!
					label.setOpaque(true); //��͸��!!
					label.setBackground(Color.YELLOW); //
					if (isSubmit) { //������ύ
						label.setForeground(color_user); //
						label.setIcon(icon1); //
					} else {
						label.setIcon(icon2); //
						label.setForeground(Color.RED); //
					}
				} else { //���ûѡ��
					label.setOpaque(false); //͸��!!
					if (isSubmit) { //������ύ
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
