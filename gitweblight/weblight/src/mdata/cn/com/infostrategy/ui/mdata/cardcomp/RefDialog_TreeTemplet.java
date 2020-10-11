package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * ¼��ʱ�е����Ͳ��մ���!!!
 * һ��ͨ����ע��������ת����һ���Զ�����յĻ���!!
 * ��һ��һ���Զ�����ն�����ͨ��һ��ע�����������ҳ��,Ȼ������������һ��ȷ����ȡ����Ť�Ӷ��γ�һ������!!!
 * ������ģ������н�·��Ҳ��������!!
 * @author xch
 *
 */
public class RefDialog_TreeTemplet extends AbstractRefDialog implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_confirm, btn_cancel; //
	private RefItemVO returnRefItemVO = null; //
	private RefItemVO refItemVO = null;
	private BillTreePanel billTreePanel = null, billTreePanel2 = null; //
	private JButton bnt_1, bnt_2, bnt_3, bnt_4, bnt_5; //

	private JTabbedPane tabbedPanel = null; //
	private JPanel tabContentPanel_1, tabContentPanel_2 = null; //

	private JCheckBox[] checkBoxs = null; //
	private DefaultMutableTreeNode[] tree2AllNodes = null; //
	private BillListPanel billListPanel2 = null; //���ӵĹ��ﳵģʽ�ұߵı��!!
	private JPanel southPanel = null; //
	private CommUCDefineVO dfvo = null; //�������Ҫȥ��!!!
	private TBUtil tbUtil = new TBUtil(); //

	private BillDialog copySelGroupDialog = null; //������ʱ�Ĵ���!
	private JCheckBox isOnlyFirstCheck = null;
	private JCheckBox[] copySelGroupConsCheck = null; //
	private JTextField copySelGroupCustConText = null; //
	private boolean isTabLoaded_1 = false;
	private boolean isTabLoaded_2 = false; //

	public RefDialog_TreeTemplet(Container _parent, String _title, RefItemVO _initRefItemVO, BillPanel panel, CommUCDefineVO _dfvo) {
		super(_parent, _title, _initRefItemVO, panel);
		this.refItemVO = _initRefItemVO;
		dfvo = _dfvo;
		if (ClientEnvironment.isAdmin()) {
			this.setTitle(this.getTitle() + ",ʵ������[RefDialog_TreeTemplet]"); //
		}
	}

	@Override
	public void initialize() {
		//���ﳵģʽ�����������,�ұ��Ǳ��,Ȼ����԰�������еĽ������ұ߱����!!!
		String str_isChooseCopy = dfvo.getConfValue("�Ƿ�֧������ģʽ", "N"); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		if (str_isChooseCopy.startsWith("Y")) { //
			tabContentPanel_1 = new JPanel(new BorderLayout()); //
			tabContentPanel_2 = new JPanel(new BorderLayout()); //

			tabbedPanel = new JTabbedPane(); //
			tabbedPanel.setFocusable(false); //
			tabbedPanel.addTab("��ģʽ", UIUtil.getImage("office_045.gif"), tabContentPanel_1); //
			tabbedPanel.addTab("����ģʽ", UIUtil.getImage("office_020.gif"), tabContentPanel_2); //

			if (str_isChooseCopy.equals("Y2")) {
				tabbedPanel.setSelectedIndex(1);
				initTab2();
			} else {
				tabbedPanel.setSelectedIndex(0);
				initTab1();
			}
			tabbedPanel.addChangeListener(this); //
			this.getContentPane().add(tabbedPanel, BorderLayout.CENTER); //
			this.setSize(800, 600); //
			this.locationToCenterPosition(); //
		} else {
			tabContentPanel_1 = new JPanel(new BorderLayout()); //
			this.getContentPane().add(tabContentPanel_1, BorderLayout.CENTER);
			initTab1(); //���ص�һ��ҳ��
		}
	}

	/**
	 * ��һ��ҳǩ,��ģʽ!
	 */
	private void initTab1() {
		if (tabbedPanel != null) {
			tabbedPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
		}
		tabContentPanel_1.removeAll(); //
		tabContentPanel_1.setLayout(new BorderLayout()); //

		billTreePanel = new BillTreePanel(dfvo.getConfValue("ģ�����")); //ͨ��ע��������һ����ʽ���
		String str_isMultiChoose = dfvo.getConfValue("���Զ�ѡ"); //
		if (str_isMultiChoose != null) { //�����ʽָ����,����,�����û�˲���!��ԭ������ģ���ж������ɶ����ɶ! ��ǰ����������! ��ǰû����,��ǿ�б����N
			if (str_isMultiChoose.equalsIgnoreCase("Y")) {
				if (!billTreePanel.isChecked()) { //���ԭ�����ǹ�ѡ�������
					billTreePanel.reSetTreeChecked(true); //����ɹ�ѡ��
				}
			} else if (str_isMultiChoose.equalsIgnoreCase("N")) {
				if (billTreePanel.isChecked()) { //���ԭ��ģ�嶨����Ƕ�ѡ,��Ҫ��ԭ�ɵ�ѡ!
					billTreePanel.reSetTreeChecked(false);
				}
			}
		}

		if (billTreePanel.isChecked()) {
			boolean isLinkCheck = dfvo.getConfBooleanValue("�Ƿ�������ѡ", true); //
			billTreePanel.setDefaultLinkedCheck(isLinkCheck); //������!
		} else {
			//billTreePanel.getJTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //��ѡ
		}

		if (!dfvo.getConfBooleanValue("�Ƿ���ʾ��ť���", false)) {//��������ģ���Ƿ���ʾ��ť���Ĳ��������ģ�������������Ͱ�ť����ѡ�����ʱ�����û�ж�Ӧ�ļ�¼���ɽ������������������/2012-03-05��
			billTreePanel.getBtnPanel().setVisible(false); //
		}

		billTreePanel.setDragable(false); //

		//��ѯ����!!!
		String queryStr = dfvo.getConfValue("����SQL����");
		String str_dataPolicy = dfvo.getConfValue("����Ȩ�޲���"); //���ݹ���Ȩ�ޣ���
		if (str_dataPolicy != null) { //���������һ������,��ǿ���ֹ��޸�ģ���е�����Ȩ�޲���!֮������ô������Ϊ������������������Ϊÿ������������һ��ģ�壡Ȼ�����������ģ�壡���������һ������,��ֻ��Ҫһ��ģ��!���ҿ���������ǰ��ĳ��ģ�壨�������Ѿ������˲��ԣ�!��Ϊ�����һ���֮��
			billTreePanel.setDataPolicy(str_dataPolicy, dfvo.getConfValue("����Ȩ�޲���ӳ��")); //
		}
		billTreePanel.queryDataByCondition(queryStr, getViewOnlyLevel()); //��ѯ����!!!

		//�����������Ʋ���  չ��ĳһ��
		if (dfvo.getConfBooleanValue("չ��", false)) {
			billTreePanel.myExpandAll();
		}

		String[] str_expandnoneNodes = dfvo.getAllConfKeys("չ��ĳ�����", true); //
		if (str_expandnoneNodes.length > 0) { //
			for (int i = 0; i < str_expandnoneNodes.length; i++) {
				String str_idValue = dfvo.getConfValue(str_expandnoneNodes[i]); //ȡ��ʵ�ʵ�ֵ!!
				billTreePanel.expandOneNodeByKey(str_idValue);
			}
		}

		int li_allNodeCount = billTreePanel.getAllNodesCount(); //
		if (li_allNodeCount > 0 && li_allNodeCount <= 20) { //������С��20��,���Զ�ȫ��չ��!ʡ�÷ѹ���!
			billTreePanel.myExpandAll();
		}

		if (refItemVO != null && refItemVO.getId() != null && !refItemVO.getId().equals("")) {
			billTreePanel.findNodesByKeysAndScrollTo(refItemVO.getId().split(";")); //����ģ����յķ���ѡ �����/2012-08-30��
		}

		billTreePanel.setHelpInfoVisiable(true);

		if (ClientEnvironment.isAdmin()) {
			if (billTreePanel.isChecked() && billTreePanel.isDefaultLinkedCheck()) { //����ǹ�ѡ,������,��ǿ�Ҿ���
				JTextField warnTextField = new JTextField("�¿�����Ա����:��ѡ���Ͳ�����ά������ʱ,��Ҫ���ó�����ѡ��,��ֻ�洢�������!(�����ڲ���)"); //
				warnTextField.setPreferredSize(new Dimension(2000, 22)); //
				warnTextField.setSelectionStart(0);
				warnTextField.setSelectionEnd(0); //
				warnTextField.setEditable(false); //
				warnTextField.setBackground(Color.RED); //
				warnTextField.setForeground(Color.BLACK); //
				warnTextField.setBorder(BorderFactory.createEmptyBorder()); //�հױ߿�
				tabContentPanel_1.add(warnTextField, BorderLayout.NORTH); //
			}
		}

		//���ֻ��һ���ڵ�, ����ģ����"�Ƿ���ʾ���ڵ�"ΪTrue, �Ͳ���ʾ���û�õ�����! Gwang 2012-09-19

		JPanel treePanel = new JPanel(new BorderLayout()); //
		if (li_allNodeCount == 1 && billTreePanel.getTempletVO().getTreeisshowroot()) {
			String templetName = billTreePanel.getTempletVO().getTempletname();
			String nodataAlert = dfvo.getConfValue("����Ϊ��ʱ����ʾ"); //Ԭ���� 20130526 ��� �������������
			String tempstr = "";
			if (null == nodataAlert || "".equals(nodataAlert)) {
				tempstr = "û���ҵ�[" + templetName + "]���������.";
			} else {
				tempstr = nodataAlert;
			}
			WLTLabel lbmsg = new WLTLabel(tempstr);
			treePanel.add(lbmsg, BorderLayout.NORTH);
			btn_confirm.setVisible(false);
		} else {
			billTreePanel.setHelpInfoVisiable(true);
			treePanel.add(billTreePanel, BorderLayout.CENTER);
		}

		tabContentPanel_1.add(treePanel); //

		if (tabbedPanel != null) {
			tabbedPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}

		isTabLoaded_1 = true; //
	}

	/**
	 * �ڶ���ҳǩ,����ģʽ!�����ﳵѡ��ʽ!
	 */
	private void initTab2() {
		if (tabbedPanel != null) {
			tabbedPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
		}
		JPanel dragTreeItemPanel = getDragItemTreeItemPanel(); //���ﳵ���!!
		tabContentPanel_2.removeAll(); //
		tabContentPanel_2.setLayout(new BorderLayout()); //
		tabContentPanel_2.add(dragTreeItemPanel);
		if (tabbedPanel != null) {
			tabbedPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
		isTabLoaded_2 = true; //
	}

	//�л�״̬!
	public void stateChanged(ChangeEvent _event) {
		int li_pos = tabbedPanel.getSelectedIndex(); //
		if (li_pos == 0 && !isTabLoaded_1) {
			initTab1(); //
		} else if (li_pos == 1 && !isTabLoaded_2) {
			initTab2(); //
		}
	}

	/**
	 * ���ﳵģʽ�����,��������������Ͳ��ձ�����ǿ��Ķ���ģʽѡ�������!
	 * @return
	 */
	private JPanel getDragItemTreeItemPanel() {
		JPanel panel = new JPanel(new BorderLayout()); //

		billTreePanel2 = new BillTreePanel(dfvo.getConfValue("ģ�����")); //
		if (billTreePanel2.isChecked()) { //���ԭ���ǹ�ѡ��!��ǿ��Ū�ɲ��ǹ�ѡ��!!!
			billTreePanel2.reSetTreeChecked(false); //
		}

		String queryStr = dfvo.getConfValue("����SQL����");
		String str_dataPolicy = dfvo.getConfValue("����Ȩ�޲���"); //���ݹ���Ȩ�ޣ���
		if (str_dataPolicy != null) { //���������һ������,��ǿ���ֹ��޸�ģ���е�����Ȩ�޲���!֮������ô������Ϊ������������������Ϊÿ������������һ��ģ�壡Ȼ�����������ģ�壡���������һ������,��ֻ��Ҫһ��ģ��!���ҿ���������ǰ��ĳ��ģ�壨�������Ѿ������˲��ԣ�!��Ϊ�����һ���֮��
			billTreePanel2.setDataPolicy(str_dataPolicy, dfvo.getConfValue("����Ȩ�޲���ӳ��")); //
		}
		billTreePanel2.queryDataByCondition(queryStr, getViewOnlyLevel()); //��ѯ����!!!

		TBUtil tbUtil = new TBUtil(); //

		JPanel rightPanel = new JPanel(new BorderLayout()); //
		billListPanel2 = new BillListPanel(new DefaultTMO("ѡ�еĻ���", new String[][] { { "id", "50" }, { "����", "250" } })); //
		billListPanel2.setItemVisible("id", false); //

		if (this.refItemVO != null && refItemVO.getId() != null) { //
			String str_refid = refItemVO.getId(); //
			String str_refname = refItemVO.getName(); //
			String[] str_ids = tbUtil.split(str_refid, ";"); //
			String[] str_names = tbUtil.split(str_refname, ";"); //

			for (int i = 0; i < str_ids.length; i++) {
				int li_newrow = billListPanel2.addEmptyRow(false, false); //
				billListPanel2.setValueAt(new StringItemVO(str_ids[i]), li_newrow, "id"); //
				billListPanel2.setValueAt(new StringItemVO(str_names[i]), li_newrow, "����"); //
			}
		}
		rightPanel.add(billListPanel2, BorderLayout.CENTER); //

		String[] str_conKeys = dfvo.getAllConfKeys("����ģʽԤ����", true); //
		if (str_conKeys != null && str_conKeys.length > 0) {
			checkBoxs = new JCheckBox[str_conKeys.length]; // 
			JPanel btnPanel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT)); //
			for (int i = 0; i < str_conKeys.length; i++) { //
				String str_value = dfvo.getConfValue(str_conKeys[i]); //
				HashMap itemConMap = tbUtil.convertStrToMapByExpress(str_value, ";", "="); //
				String str_name = (String) itemConMap.get("����"); //
				checkBoxs[i] = new JCheckBox(str_name); //
				checkBoxs[i].putClientProperty("ConfMap", itemConMap); //
				checkBoxs[i].setToolTipText("<html>��סCtrl�����Զ�ѡ!<br>" + itemConMap + "</html>"); //
				checkBoxs[i].addActionListener(this); //
				checkBoxs[i].setOpaque(false); //͸��!!
				checkBoxs[i].setFocusable(false); //
				btnPanel.add(checkBoxs[i]); //
			}
			rightPanel.add(btnPanel, BorderLayout.NORTH); //
		}

		rightPanel.add(getCopyBtnPanel(), BorderLayout.WEST); //

		WLTSplitPane split = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTreePanel2, rightPanel); //
		split.setDividerLocation(270); //

		panel.add(split); //
		return panel; //
	}

	private JPanel getCopyBtnPanel() {
		bnt_1 = new JButton(" >> ");
		bnt_2 = new JButton(" >  ");
		bnt_3 = new JButton(" >% ");
		bnt_4 = new JButton(" <  ");
		bnt_5 = new JButton(" << ");

		bnt_3.setToolTipText("����ָ��������ѡ�еķ�Χ�й���!"); //
		//bnt_3.setMargin(new Insets(0, 0, 0, 0)); //

		bnt_1.setPreferredSize(new Dimension(200, 20));
		bnt_2.setPreferredSize(new Dimension(200, 20));
		bnt_3.setPreferredSize(new Dimension(200, 20));
		bnt_4.setPreferredSize(new Dimension(200, 20));
		bnt_5.setPreferredSize(new Dimension(200, 20));

		bnt_1.addActionListener(this); //
		bnt_2.addActionListener(this); //
		bnt_3.addActionListener(this); //
		bnt_4.addActionListener(this); //
		bnt_5.addActionListener(this); //

		Box box = Box.createVerticalBox();
		box.add(Box.createGlue());
		box.add(bnt_1); //
		box.add(Box.createVerticalStrut(10));
		box.add(bnt_2); //
		box.add(Box.createVerticalStrut(10));
		box.add(bnt_3); //
		box.add(Box.createVerticalStrut(10));
		box.add(bnt_4); //
		box.add(Box.createVerticalStrut(10));
		box.add(bnt_5); //
		box.add(Box.createGlue());

		JPanel panel = new WLTPanel(WLTPanel.VERTICAL_OUTSIDE_TO_MIDDLE, new BorderLayout(), LookAndFeel.defaultShadeColor1, false);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); //
		panel.setPreferredSize(new Dimension(100, 2000)); //
		panel.add(box);

		return panel; //
	}

	private void onCopyAll() {
		if (!MessageBox.confirm(this, "�������ѡ����������ô?")) {
			return; //
		}
		doCopyNodes(billTreePanel2.getAllNodes()); //
	}

	//����ѡ�еĽ��
	private void onCopySel() { //
		DefaultMutableTreeNode[] selNodes = billTreePanel2.getSelectedNodes(); //
		if (selNodes == null || selNodes.length <= 0) {
			MessageBox.show(this, "��ѡ��һ�������д˲���!"); //
			return; //
		}

		if (selNodes.length == 1 && selNodes[0].isRoot()) {
			MessageBox.show(this, "����ѡ��������п���,��ѡ�����ݽ��!"); //
			return; //
		}
		doCopyNodes(selNodes); //
	}

	private void onCopyCust() {
		DefaultMutableTreeNode[] selNodes = billTreePanel2.getSelectedNodes(); //
		if (selNodes == null || selNodes.length <= 0) {
			MessageBox.show(this, "��ѡ��һ�����������д˲���!"); //
			return; //
		}

		//����һ������
		if (copySelGroupDialog != null) {
			copySelGroupDialog.setVisible(true); //
		} else {
			String[] str_defs = dfvo.getAllConfKeys("����ģʽѡ����", true); //
			String[][] str_allDefs = new String[str_defs.length][2]; //new String[][] { { "���ɺϹ�����", "�Ϲ�/����/����" }, { "�Ʋ�����", "�ƻ�����/�ƲƲ�" }}; //
			for (int i = 0; i < str_defs.length; i++) {
				String str_defValue = dfvo.getConfValue(str_defs[i]); //
				HashMap defMap = tbUtil.convertStrToMapByExpress(str_defValue, ";", "="); //����!
				str_allDefs[i][0] = (String) defMap.get("����"); //
				str_allDefs[i][1] = (String) defMap.get("����"); //
			}

			JPanel panel = WLTPanel.createDefaultPanel(new BorderLayout()); //
			isOnlyFirstCheck = new JCheckBox("����ֻȡ��һ��?", true); //
			isOnlyFirstCheck.setToolTipText("���������,���ѡ�н�������������м���!"); //
			isOnlyFirstCheck.setOpaque(false); //͸��
			isOnlyFirstCheck.setFocusable(false);

			JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.CENTER)); //
			panel_north.setOpaque(false);
			copySelGroupConsCheck = new JCheckBox[str_allDefs.length]; //
			for (int i = 0; i < copySelGroupConsCheck.length; i++) { //
				copySelGroupConsCheck[i] = new JCheckBox(str_allDefs[i][0]); //
				copySelGroupConsCheck[i].setToolTipText(str_allDefs[i][1]); //
				copySelGroupConsCheck[i].setOpaque(false); //͸��
				copySelGroupConsCheck[i].setFocusable(false); //
				panel_north.add(copySelGroupConsCheck[i]); //
			}
			panel.add(panel_north, BorderLayout.NORTH); //

			JPanel panel_south = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
			panel_south.setOpaque(false);
			JLabel label = new JLabel("�Զ�������", SwingConstants.RIGHT); //
			label.setPreferredSize(new Dimension(60, 22)); //
			label.setToolTipText("����Ԥ�õ�������,��������ʱ�¼�һЩ����!����:�Ʋ�/������Դ"); //
			copySelGroupCustConText = new JTextField(); //
			copySelGroupCustConText.setToolTipText("����:�Ϲ�/����/����/�Ʋ�");
			copySelGroupCustConText.setPreferredSize(new Dimension(200, 22)); //

			panel_south.add(label); //
			panel_south.add(copySelGroupCustConText); //
			panel_south.add(isOnlyFirstCheck); //

			panel.add(panel_south, BorderLayout.SOUTH); //
			int li_width = str_allDefs.length * 85 + 100;
			if (li_width < 450) {
				li_width = 450;
			}
			copySelGroupDialog = new BillDialog(this, "ѡ��������", li_width, str_allDefs.length > 0 ? 135 : 100); //
			copySelGroupDialog.getContentPane().add(panel); //
			copySelGroupDialog.addConfirmButtonPanel(); //
			copySelGroupDialog.setVisible(true); //
		}

		if (copySelGroupDialog.getCloseType() != 1) {
			return;
		}

		ArrayList al_sels = new ArrayList(); //
		for (int i = 0; i < copySelGroupConsCheck.length; i++) {
			if (copySelGroupConsCheck[i].isSelected()) {
				al_sels.add(copySelGroupConsCheck[i].getToolTipText()); //
			}
		}
		if (!copySelGroupCustConText.getText().trim().equals("")) {
			al_sels.add(copySelGroupCustConText.getText().trim()); //
		}

		if (al_sels.size() <= 0) {
			MessageBox.show(this, "������ѡ�������һ������!"); //
			return; //
		}

		ArrayList aldefAll = new ArrayList(); //
		for (int i = 0; i < al_sels.size(); i++) {
			String[] str_items = tbUtil.split((String) al_sels.get(i), "/"); //
			aldefAll.addAll(Arrays.asList(str_items)); //
		}
		String[] str_text = (String[]) aldefAll.toArray(new String[0]); //

		//���ݵ������ڵķ���ֵ���м���!
		boolean isOnlyFirstLevelChild = isOnlyFirstCheck.isSelected(); //
		HashSet hst_childs = new LinkedHashSet(); //
		if (isOnlyFirstLevelChild) { //���ֻȡ��һ��!
			for (int i = 0; i < selNodes.length; i++) {
				for (int j = 0; j < selNodes[i].getChildCount(); j++) { //����һ���ӽ��!
					DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) selNodes[i].getChildAt(j); //
					hst_childs.add(childNode); //
				}
			}
		} else { //
			for (int i = 0; i < selNodes.length; i++) {
				DefaultMutableTreeNode[] allChilds = billTreePanel2.getOneNodeChildPathNodes(selNodes[i], false); //
				for (int j = 0; j < allChilds.length; j++) {
					hst_childs.add(allChilds[j]); //
				}
			}
		}
		DefaultMutableTreeNode[] allSelNodeChildrens = (DefaultMutableTreeNode[]) hst_childs.toArray(new DefaultMutableTreeNode[0]); //�����ӽ��!

		HashSet hst = getOneNodeAllChildNodesByCons(allSelNodeChildrens, -1, str_text, null); //
		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) hst.toArray(new DefaultMutableTreeNode[0]); //
		billListPanel2.clearTable(); //���������!!!
		doCopyNodes(allNodes); //
	}

	//ɾ��ѡ�е�!
	private void onDelSel() {
		billListPanel2.removeSelectedRows(); //
	}

	private void onDelAll() {
		billListPanel2.clearTable(); //
	}

	private void doCopyNodes(DefaultMutableTreeNode[] _nodes) {
		boolean isTrimFirstLevel = dfvo.getConfBooleanValue("�Ƿ�ص���һ��", true);
		for (int i = 0; i < _nodes.length; i++) { //
			if (_nodes[i].isRoot()) {
				continue;
			}
			TreeNode[] allParentNodes = _nodes[i].getPath(); //
			StringBuilder sb_name = new StringBuilder(); // 
			for (int j = 1; j < allParentNodes.length; j++) {
				sb_name.append(((DefaultMutableTreeNode) allParentNodes[j]).getUserObject().toString()); //
				if (j != allParentNodes.length - 1) {
					sb_name.append("-");
				}
			}
			BillVO itemVO = billTreePanel2.getBillVOFromNode(_nodes[i]); //
			if (itemVO.isVirtualNode()) { //�����������������!
				continue;
			}
			String str_id = itemVO.getStringValue(dfvo.getConfValue("ID�ֶ�", "id")); ///
			String str_name = sb_name.toString(); //
			if (isTrimFirstLevel && str_name.indexOf("-") > 0) {
				str_name = str_name.substring(str_name.indexOf("-") + 1, str_name.length()); //
			}
			int li_newrow = billListPanel2.addEmptyRow(false, false); //
			billListPanel2.setValueAt(new StringItemVO(str_id), li_newrow, "id"); //
			billListPanel2.setValueAt(new StringItemVO(str_name), li_newrow, "����"); //
		}
	}

	//��ȡ�ڼ���!
	private int getViewOnlyLevel() {
		int li_level = 0; //
		if (dfvo.getConfValue("ֻ��ǰ����") != null) { //
			try {
				li_level = Integer.parseInt(dfvo.getConfValue("ֻ��ǰ����")); //
			} catch (Exception e) {
				e.printStackTrace(); //�������ֲ��Ϸ�,��Ե��쳣������0����,��֤�����ܳ���.
			}
		}
		return li_level; //
	}

	public JPanel getSouthPanel() {
		if (southPanel != null) {
			return southPanel;
		}
		southPanel = WLTPanel.createDefaultPanel(new FlowLayout(), BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE);
		btn_cancel = new WLTButton("ȡ��");
		btn_cancel.addActionListener(this); //
		btn_confirm = new WLTButton("ȷ��");
		btn_confirm.addActionListener(this); //
		southPanel.add(btn_confirm); // 
		southPanel.add(btn_cancel); //
		return southPanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //�����ȷ���򷵻�����
			onConfirm(); //ȷ��!!!
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //ȡ��
		} else if (e.getSource() == bnt_1) { //��������
			onCopyAll(); //
		} else if (e.getSource() == bnt_2) { //������������
			onCopySel(); //
		} else if (e.getSource() == bnt_3) { //
			onCopyCust(); //ѡ��ָ����!
		} else if (e.getSource() == bnt_4) { //
			onDelSel(); //ɾ��ѡ�е�
		} else if (e.getSource() == bnt_5) { //
			onDelAll(); //ɾ������
		} else if (e.getSource() instanceof JCheckBox) { //�������ǹ�ѡ��!
			if (e.getModifiers() == 17 || e.getModifiers() == 18) { //�����ס��Ctrl��Shift��,��,���Զ�ѡ!
			} else { //������ȡ��������ѡ��!
				JCheckBox checkBox = (JCheckBox) e.getSource();
				for (int i = 0; i < checkBoxs.length; i++) {
					if (checkBoxs[i] != checkBox) {
						checkBoxs[i].setSelected(false); //
					}
				}
			}
			onQuickChooseDept(); //
		}
	}

	//����ѡ�����!!!
	private void onQuickChooseDept() {
		billListPanel2.clearTable(); //���������!!!
		DefaultMutableTreeNode rootNode = billTreePanel2.getRootNode(); //
		if (tree2AllNodes == null) {
			tree2AllNodes = billTreePanel2.getAllNodes(); //
		}
		HashSet hstAll = new LinkedHashSet(); //
		for (int i = 0; i < checkBoxs.length; i++) {
			if (checkBoxs[i].isSelected()) { //����Ѿ���ѡ��
				HashMap confMap = (HashMap) checkBoxs[i].getClientProperty("ConfMap"); //
				String str_startRoot = (String) confMap.get("���"); //"����=���и�����;���=12;�㼶=2;����=����/�Ϲ�/�ƻ�"
				int li_level = -1; //
				if (confMap.containsKey("�㼶")) {
					li_level = Integer.parseInt((String) confMap.get("�㼶")); //
				}

				String[] str_texts = null; //
				if (confMap.containsKey("����")) {
					str_texts = tbUtil.split((String) confMap.get("����"), "/"); //
				}

				String[] str_exceptTexts = null; //
				if (confMap.containsKey("��ȥ")) {
					str_exceptTexts = tbUtil.split((String) confMap.get("��ȥ"), "/"); //
				}

				if (str_startRoot == null) {
					HashSet hst = getOneNodeAllChildNodesByCons(tree2AllNodes, li_level, str_texts, str_exceptTexts); //
					hstAll.addAll(hst); //
				} else {
					DefaultMutableTreeNode[] startRootNodes = findStartNodes(tree2AllNodes, str_startRoot); //
					for (int j = 0; j < startRootNodes.length; j++) {
						DefaultMutableTreeNode[] allchildNodes = billTreePanel2.getOneNodeChildPathNodes(startRootNodes[j], false); //
						HashSet hst = getOneNodeAllChildNodesByCons(allchildNodes, li_level, str_texts, str_exceptTexts); //
						hstAll.addAll(hst); //
					}
				}

			}
		}

		DefaultMutableTreeNode[] allnodes = (DefaultMutableTreeNode[]) hstAll.toArray(new DefaultMutableTreeNode[0]); //
		doCopyNodes(allnodes); //������ȥ!
	}

	//�ҵ�ĳ��Щ�������!���ֻ��һ������,ʵ����ֻ�᷵��һ��!
	private DefaultMutableTreeNode[] findStartNodes(DefaultMutableTreeNode[] _allNodes, String _startRoot) {
		HashSet hst = new LinkedHashSet(); //
		String[] str_rootIds = tbUtil.split(_startRoot, "/"); //������[12/15/17]
		for (int i = 0; i < str_rootIds.length; i++) { //
			String str_id = str_rootIds[i]; //
			for (int j = 0; j < _allNodes.length; j++) { //�������н��!!!
				BillVO billVO = billTreePanel2.getBillVOFromNode(_allNodes[j]); //
				if (billVO != null) {
					String str_idValue = billVO.getStringValue(dfvo.getConfValue("ID�ֶ�", "id")); ///
					if (str_idValue != null && str_idValue.equals(str_id)) {
						hst.add(_allNodes[j]); //
						break; //
					}
				}
			}
		}
		DefaultMutableTreeNode[] nodes = (DefaultMutableTreeNode[]) hst.toArray(new DefaultMutableTreeNode[0]); //
		return nodes;
	}

	//��������!ȡ��һ�������������������
	private HashSet getOneNodeAllChildNodesByCons(DefaultMutableTreeNode[] allChildNodes, int _level, String[] _textItems, String[] _exceptTextItems) {
		LinkedHashSet hst = new LinkedHashSet(); ////
		//DefaultMutableTreeNode[] allChildNodes = billTreePanel2.getOneNodeChildPathNodes(_rootNode, false); //

		for (int i = 0; i < allChildNodes.length; i++) {
			boolean isLevelSucc = true; //
			if (_level > 0) { //���������!
				if (allChildNodes[i].getLevel() == _level) {
					isLevelSucc = true; //������
				} else {
					isLevelSucc = false; //û����!
				}
			}

			boolean isTextSucc = true; //
			String str_nodeText = allChildNodes[i].getUserObject().toString(); //
			if (_textItems != null) {
				boolean isMatched = false; //
				for (int j = 0; j < _textItems.length; j++) {
					if (str_nodeText.indexOf(_textItems[j]) >= 0) { //ֻҪ��һ�����������˾��Ƕ���!����ͻ��Ƿ�!!!
						isMatched = true; //
						break; //
					}
				}
				isTextSucc = isMatched; //
			}

			if (_exceptTextItems != null && isTextSucc) { //��������˳�ȥ������,��ԭ���������Ϊtrue
				TreeNode[] thisParentNodes = allChildNodes[i].getPath(); //
				StringBuilder sb_fulltext = new StringBuilder(); //
				for (int p = 0; p < thisParentNodes.length; p++) {
					sb_fulltext.append(((DefaultMutableTreeNode) thisParentNodes[p]).getUserObject().toString()); //
					if (p != thisParentNodes.length - 1) {
						sb_fulltext.append("-"); //
					}
				}
				String str_fulltext = sb_fulltext.toString(); //
				for (int j = 0; j < _exceptTextItems.length; j++) {
					if (str_fulltext.indexOf(_exceptTextItems[j]) >= 0) { //ֻҪ��һ�����������˾��Ƕ��ϾͲ���!
						isTextSucc = false; //�ٴα�ɷ�!
						break; //
					}
				}
			}

			if (isLevelSucc && isTextSucc) {
				hst.add(allChildNodes[i]); //
			}
		}

		return hst; //
	}

	private void onConfirm() {
		if (this.tabbedPanel == null) { //���û�ж�ҳǩ,��϶��ǵ�һ��!
			onConfirmTab_1(); //
		} else {
			if (tabbedPanel.getSelectedIndex() == 0) {
				onConfirmTab_1(); //
			} else if (tabbedPanel.getSelectedIndex() == 1) {
				onConfirmTab_2(); //
			}
		}
	}

	//���ȷ��!
	private void onConfirmTab_1() {
		String str_id_field = dfvo.getConfValue("ID�ֶ�"); //
		String str_name_field = dfvo.getConfValue("NAME�ֶ�"); //
		boolean isTrimFirstLevel = dfvo.getConfBooleanValue("�Ƿ�ص���һ��", true); //�Ƿ�ص���һ��??Ĭ���ǽص�!
		boolean isReturnPathName = dfvo.getConfBooleanValue("����·������", true); //��ǰĬ���Ƿ�,��������ʵ���Ͼ����������Ҫ����ȫ·����,�ɴ�Ĭ����true
		boolean isOnlyChooseLeafNode = dfvo.getConfBooleanValue("ֻ��ѡҶ��", false); //ֻ��ѡҶ�ӽ��,Ĭ���Ƿ�

		if (str_id_field == null) {
			str_id_field = billTreePanel.getTempletVO().getPkname(); //
		}
		if (str_id_field == null) {
			MessageBox.show(this, "��ʽ��û�ж��巵�ص�ID�ֶ���,ģ����Ҳû�ж�����������"); //
			return; //
		}
		if (!billTreePanel.getTempletVO().containsItemKey(str_id_field)) { //
			MessageBox.show(this, "ָ���ӡ�" + str_id_field + "���ֶη��ز���ID,��ģ����û�и��"); //
			return; //
		}

		if (billTreePanel.isChecked() && billTreePanel.isDefaultLinkedCheck() && ClientEnvironment.isAdmin()) { //�����������������,�����Ƕ�ѡ��!!����һ��ǿ������!!
			MessageBox.show(this, getHelpMsg_1()); //

		}
		if (!billTreePanel.isChecked()) { //������Ƕ�ѡ���,����ѡ��
			BillVO billvo = billTreePanel.getSelectedVO(); //ѡ��һ����¼!!!
			if (billvo == null) {
				MessageBox.showSelectOne(this); //
				return; //
			}
			if (isOnlyChooseLeafNode) { //�ж��Ƿ�ֻ��ѡ��Ҷ�ӽ��!!!
				if (!billTreePanel.getSelectedNode().isLeaf()) {
					MessageBox.show(this, "ֻ��ѡ��Ҷ�ӽ��,����ĩ���Ľ��!"); //
					return; //
				}
			}

			if (billvo.isVirtualNode()) { //������Ҳ����!,�Ȳ�����!��Ϊ����¼��ʱ������б�����Ҷ�ӽ���Լ��!!
				MessageBox.show(this, "б������Ľ�㲻����ѡ��!\r\n������Ϊ����Ȩ�޼���,��û��ѡ��ý���Ȩ��!\r\n��Ϊ�˱�֤������ʾ����·����������������,�����뿴��,���ֲ���ѡ��!"); //
				return; //
			}

			HashVO hvo = billvo.convertToHashVO(); //ת��һ��!!!
			hvo.setAttributeValue("$ReturnPathName", hvo.getStringValue("$parentpathname")); //��һ��ָ�����������!!��ʵֻҪһ��������,������Ϊ�˼��ݾɵ�!

			//��������RefVO
			returnRefItemVO = new RefItemVO(hvo); //
			//������vo��id
			returnRefItemVO.setId(billvo.getStringValue(str_id_field)); //

			//��ǰ���и��ж�[����·������],����ͳһǿ�з���·��!!
			String str_pathName = hvo.getStringValue("$parentpathname"); //ǿ����Զ��·��!!!
			if (isTrimFirstLevel && str_pathName != null && str_pathName.indexOf("-") > 0) { //���Ҫ�õ���һ��!
				str_pathName = str_pathName.substring(str_pathName.indexOf("-") + 1, str_pathName.length()); //
			}
			returnRefItemVO.setName(str_pathName); //ֱ�ӽ������Ľ�����Ʒ���!!(String) billvo.getUserObject("$ParentPathName")
		} else { //����Ƕ�ѡ��..
			BillVO[] billVOs = billTreePanel.getCheckedVOs(); //�µĻ������˸�$ParentPathNames�û��ֶ�������!!
			if (billVOs == null || billVOs.length == 0) {
				MessageBox.show(this, "�����ٹ�ѡһ������!\r\n��ܰ��ʾ:���ǿ��Զ�ѡ��,������ǰ���Ǹ���ѡ��,��������ѡ��!"); //
				return; //
			}

			//�����Ƶ�ȫ·������ȥ!
			if (dfvo.getConfBooleanValue("ֻ��ѡҶ��", false)) { //ֻ��ѡҶ��Ҷ��,��ҪУ��!!
				DefaultMutableTreeNode[] allCheckedNodes = billTreePanel.getCheckedNodes(); //
				for (int i = 0; i < allCheckedNodes.length; i++) {
					if (!allCheckedNodes[i].isLeaf()) {
						MessageBox.show(this, "ֻ��ѡ��Ҷ�ӽ��,����ĩ���Ľ��!"); //
						return; //
					}
				}
			}

			returnRefItemVO = new RefItemVO(); //
			StringBuffer sb_ids = new StringBuffer(";");
			StringBuffer sb_names = new StringBuffer(); //
			int virtualmark = 0; //����ڵ��� �����/2012-09-10��

			//ѭ������!!!
			for (int i = 0; i < billVOs.length; i++) { //�������ж���!!
				if (billVOs[i] == null) {
					continue;
				}

				//�����㲻��ѡ��!
				if (billVOs[i].isVirtualNode()) { //������Ҳ����!,�Ȳ�����!��Ϊ����¼��ʱ������б�����Ҷ�ӽ���Լ��!!
					//MessageBox.show(this, "б������Ľ�㲻����ѡ��!\r\n������Ϊ����Ȩ�޼���,��û��ѡ��ý���Ȩ��!\r\n��Ϊ�˱�֤������ʾ����·����������������,�����뿴��,���ֲ���ѡ��!"); //
					virtualmark++;
					continue;
				}
				sb_ids.append(billVOs[i].getStringValue(str_id_field) + ";"); //
				String str_pathName = "";
				if (isReturnPathName) {
					str_pathName = (String) billVOs[i].getUserObject("$ParentPathName"); //ǿ����Զ��·��!!!
					if (isTrimFirstLevel && str_pathName != null && str_pathName.indexOf("-") > 0) { //���Ҫ�õ���һ��!
						str_pathName = str_pathName.substring(str_pathName.indexOf("-") + 1, str_pathName.length()); //
					}
				}else{ //�����������·����
					str_pathName = billVOs[i].getStringValue(str_name_field);
				}
				sb_names.append(str_pathName + ";");
			}

			if (billVOs.length > 0 && virtualmark == billVOs.length) {
				MessageBox.show(this, "б������Ľ�㲻����ѡ��!\r\n������Ϊ����Ȩ�޼���,��û��ѡ��ý���Ȩ��!\r\n��Ϊ�˱�֤������ʾ����·����������������,�����뿴��,���ֲ���ѡ��!");
			}

			returnRefItemVO.setId(sb_ids.toString()); //
			returnRefItemVO.setName(sb_names.toString()); //
		}
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose(); //
	}

	private void onConfirmTab_2() {
		BillVO[] billVOs = billListPanel2.getAllBillVOs(); //
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "��ѡ��һ����¼!"); //
			return;
		}

		returnRefItemVO = new RefItemVO(); //
		StringBuffer sb_ids = new StringBuffer(";");
		StringBuffer sb_names = new StringBuffer(); //
		for (int i = 0; i < billVOs.length; i++) { //�������ж���!!
			sb_ids.append(billVOs[i].getStringValue("id") + ";"); //
			sb_names.append(billVOs[i].getStringValue("����") + ";");
		}

		returnRefItemVO.setId(sb_ids.toString()); //
		returnRefItemVO.setName(sb_names.toString()); //
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose(); //
	}

	//���ȡ��!!!
	private void onCancel() {
		returnRefItemVO = null; //
		this.setCloseType(BillDialog.CANCEL);
		this.dispose(); //
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	/**
	 * ��ʼ���
	 * @return
	 */
	public int getInitWidth() {
		return 500;
	}

	public int getInitHeight() {
		return 500;
	}

	private String getHelpMsg_1() {
		StringBuilder sb_help = new StringBuilder(); //
		sb_help.append("���ֵ�ǰ���οؼ�Ϊ��ѡģʽ,����Ϊ������ѡ,���鲻Ҫ������\r\n"); //
		sb_help.append("����ͨ������[\"�Ƿ�������ѡ\",\"N\"]���޸ģ�\r\n");
		sb_help.append("\r\n");
		return sb_help.toString(); //
	}

}
