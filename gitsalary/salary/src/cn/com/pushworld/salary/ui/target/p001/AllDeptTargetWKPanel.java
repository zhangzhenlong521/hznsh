package cn.com.pushworld.salary.ui.target.p001;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.paymanage.RefDialog_Month;

/**
 * ָ���ά��
 * 
 * @author haoming create by 2013-11-6
 */
public class AllDeptTargetWKPanel extends AbstractWorkPanel implements BillListSelectListener, BillTreeSelectListener, ActionListener, ChangeListener, BillListMouseDoubleClickedListener {

	private static final long serialVersionUID = -3811922248138731326L;
	private WLTButton btn_add, btn_delete, btn_update, btn_confirm, btn_cancel, btn_action, btn_action1; // ����ҳ��İ�ť�Ѿ��������ڵİ�ť
	private WLTButton btn_tree_add, btn_tree_edit, btn_tree_del; // �������ϵİ�ť
	private BillListPanel listpanel; // ��Ҫ�����
	private boolean inittab2 = false; // �Ƿ�����˰��շ�����ʾ��
	private WLTTabbedPane tabpanel = null; // ��ҳ���ҳǩ��
	private int edittype = 0; // ��ֵΪ0��1��0Ϊȫ����ʾҳ���ά����1Ϊָ�����ҳ���ά��
	private int addPanelCurrShowRadioIndex = -1; // ��������radio��ǰѡ��λ�á�
	private boolean haveDeptJJTarget = TBUtil.getTBUtil().getSysOptionBooleanValue("�Ƿ���ʾ���żƼ�ָ��", false); //�����������֧�мƼ�ָ�ꡣ
	private HashMap<String, Pub_Templet_1VO> templetMap = new HashMap<String, Pub_Templet_1VO>();
	private static final Logger logger = WLTLogger.getLogger(AllDeptTargetWKPanel.class);

	public void initialize() {
		tabpanel = new WLTTabbedPane();
		listpanel = new BillListPanel(getTempletVO("SAL_TARGET_LIST_CODE3"));
		listpanel.getQuickQueryPanel().addBillQuickActionListener(this);
		listpanel.setCanShowCardInfo(false);
		listpanel.addBillListSelectListener(this);
		listpanel.addBillListMouseDoubleClickedListener(this);
		btn_add = new WLTButton("����");
		btn_add.addActionListener(this);
		btn_update = new WLTButton("�޸�");
		btn_update.addActionListener(this);
		btn_delete = new WLTButton("ɾ��");
		btn_delete.addActionListener(this);
		btn_action = new WLTButton("����", UIUtil.getImage("bug_2.png"));
		btn_action.setToolTipText("ֻ�ж���ָ��ſ���ִ��");
		btn_action.addActionListener(this);
		listpanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_update, btn_delete, btn_action });
		listpanel.repaintBillListButton();
		tabpanel.addTab("ȫ����ʾ", listpanel);
		tabpanel.addTab("��ָ�������ʾ", new JPanel()); // ��ӿ�
		tabpanel.addChangeListener(this);
		this.add(tabpanel);
	}

	/*
	 * ͳһȡģ��
	 */
	private Pub_Templet_1VO getTempletVO(String _code) {
		if (!templetMap.containsKey(_code)) {
			try {
				templetMap.put(_code, UIUtil.getPub_Templet_1VO(_code));
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
		return templetMap.get(_code);
	}

	/*
	 * ����ָ��ά����
	 */
	private BillTreePanel treePanel; // ָ��������
	private WLTButton btn_add1, btn_edit1, btn_delete1; // ����ҳǩ���ָ���б�ť
	private BillListPanel listpanel_1; // �б�

	public WLTSplitPane getWLTSplitPane() {
		treePanel = new BillTreePanel(getTempletVO("SAL_TARGET_CATALOG_CODE1"));
		treePanel.queryDataByCondition(null); // ������
		treePanel.addBillTreeSelectListener(this);
		btn_tree_add = WLTButton.createButtonByType(WLTButton.TREE_INSERT);
		btn_tree_edit = WLTButton.createButtonByType(WLTButton.TREE_EDIT);
		btn_tree_del = new WLTButton("ɾ��");
		btn_tree_del.addActionListener(this);
		treePanel.addBatchBillTreeButton(new WLTButton[] { btn_tree_add, btn_tree_edit, btn_tree_del });
		treePanel.repaintBillTreeButton();
		listpanel_1 = new BillListPanel(getTempletVO("SAL_TARGET_LIST_CODE3"));
		listpanel_1.setCanShowCardInfo(false); //
		listpanel_1.addBillListSelectListener(this);
		listpanel_1.setBillQueryPanelVisible(false);
		listpanel_1.addBillListMouseDoubleClickedListener(this);
		btn_add1 = new WLTButton("����");
		btn_edit1 = new WLTButton("�޸�");
		btn_delete1 = new WLTButton("ɾ��");
		btn_action1 = new WLTButton("����", UIUtil.getImage("bug_2.png"));
		btn_action1.setToolTipText("ֻ�ж���ָ��ſ���ִ��");
		btn_add1.addActionListener(this);
		btn_edit1.addActionListener(this);
		btn_delete1.addActionListener(this);
		btn_action1.addActionListener(this);
		listpanel_1.addBatchBillListButton(new WLTButton[] { btn_add1, btn_edit1, btn_delete1, btn_action1 });
		listpanel_1.repaintBillListButton();
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, treePanel, listpanel_1);
		splitPane.setDividerLocation(220);
		return splitPane;
	}

	/*
	 * �����¼� radioҳ�棬�������ԣ��л��¼���ѵ�ǰҳ��ֵ��������ѡ��ҳ�档
	 */
	private BillCardPanel dxcardpanel = null; // ����ָ�ꡣ
	private BillCardPanel dlcardpanel = null;// ����ָ�ꡣ
	private BillCardPanel jjcardpanel = null; // ���żƼ�ָ�ꡣ
	private WLTRadioPane radio = null;//
	private BillDialog dialog;

	public void onAdd() {
		addPanelCurrShowRadioIndex = -1;
		radio = new WLTRadioPane(new Color(100, 180, 30));
		radio.addChangeListener(this);
		if (edittype == 1) { //
			BillVO billVO = treePanel.getSelectedVO();
			if (billVO == null) {
				MessageBox.show(this, "����ѡ��һ��ָ�����.");
				return;
			}
			if (!treePanel.getSelectedNode().isLeaf()) {
				MessageBox.show(this, "��ѡ����ĩ���ķ���.");
				return;
			}
		}
		dialog = new BillDialog(this, "����", 650, 800) {
			public boolean beforeWindowClosed() { // ɾ���ӱ����ݡ�
				if (radio.getSelectIndex() == 1) { // �ð��ӱ�ɾ����.
					BillVO billvo = dlcardpanel.getBillVO();
					try {
						UIUtil.executeUpdateByDS(null, "delete from sal_target_checkeddept where targetid = '" + billvo.getPkValue() + "'");
					} catch (Exception e) {
						MessageBox.showException(this, e);
					}
				}
				return true;
			}
		};
		dialog.beforeWindowClosed();
		dialog.setAddDefaultWindowListener(true);
		dialog.setLayout(new BorderLayout());

		dxcardpanel = new BillCardPanel(getTempletVO("SAL_TARGET_LIST_CODE1"));
		dlcardpanel = new BillCardPanel(getTempletVO("SAL_TARGET_LIST_CODE_QUANTIFY"));
		dxcardpanel.insertRow();
		dxcardpanel.setEditableByInsertInit();
		jjcardpanel = new BillCardPanel(getTempletVO("SAL_TARGET_LIST_CODE_JJ"));
		//
		if (edittype == 1) {
			BillVO billVO = treePanel.getSelectedVO();
			dxcardpanel.setValueAt("catalogid", new RefItemVO(billVO.getStringValue("id"), "", billVO.getStringValue("name")));
		}
		dxcardpanel.setRealValueAt("type", "���Ŷ���ָ��");
		radio.addTab("����ָ��", "����ָ��", "flag_green.png", dxcardpanel);
		radio.addTab("����ָ��", "����ָ��", "flag_red.png", dlcardpanel);
		if (haveDeptJJTarget) {
			radio.addTab("�Ƽ�ָ��", "�Ƽ�ָ��", "office_150.gif", jjcardpanel);
		}
		dialog.add(radio, BorderLayout.CENTER);
		WLTPanel btn_panel = new WLTPanel(new FlowLayout(FlowLayout.CENTER));
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);
		btn_panel.add(btn_confirm);
		btn_panel.add(btn_cancel);
		dialog.add(btn_panel, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAdd();
		} else if (e.getSource() == btn_update) {
			edittype = 0;
			onUpdate();
		} else if (e.getSource() == btn_delete) {
			edittype = 0;
			ondelete(listpanel);
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == btn_tree_del) {
			onTreeDelete();
		} else if (e.getSource() == btn_action) {
			onAction(listpanel);
		} else if (e.getSource() == btn_action1) {
			onAction(listpanel_1);
		} else if (e.getSource() == btn_add1) {
			edittype = 1;
			onAdd();
		} else if (e.getSource() == btn_edit1) {
			edittype = 1;
			onUpdate();
		} else if (e.getSource() == btn_delete1) {
			edittype = 1;
			ondelete(listpanel_1);
		} else {
			onquery();
		}
	}

	private void ondelete(BillListPanel _listpanel) {
		BillVO billVO = _listpanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(_listpanel);
			return;
		}
		_listpanel.doDelete(false);
		if ("���Ŷ���ָ��".equals(billVO.getStringValue("type"))) {
			try {
				UIUtil.executeBatchByDS(null, new String[] { "delete from sal_target_checkeddept where targetid=" + billVO.getPkValue() });
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/*
	 * ��ʾ
	 */
	private void onAction(BillListPanel bp_targetlist) {
		BillVO targetVO = bp_targetlist.getSelectedBillVO();
		if (targetVO == null) {
			MessageBox.showSelectOne(bp_targetlist);
			return;
		}
		String selectDate = getDate(bp_targetlist);
		if (TBUtil.isEmpty(selectDate)) {
			return;
		}
		try {
			String msg = getService().calcOneDeptTargetDL(targetVO.convertToHashVO(), selectDate);
			MessageBox.show(bp_targetlist, msg);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private SalaryServiceIfc services;

	private SalaryServiceIfc getService() {
		if (services == null) {
			try {
				services = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return services;
	}

	private String getDate(Container _parent) {
		String selectDate = "2013-08";
		try {
			RefDialog_Month chooseMonth = new RefDialog_Month(_parent, "��ѡ��Ҫ���˵��·�", null, null);
			chooseMonth.initialize();
			chooseMonth.setVisible(true);
			if (chooseMonth.getCloseType() != 1) {
				return null;
			}
			selectDate = chooseMonth.getReturnRefItemVO().getName();
			return selectDate;
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		return "2013-08";
	}

	// ָ�������ɾ��, �������Լ��÷����µ�ָ��һ��ɾ��!
	private void onTreeDelete() {
		if (treePanel.getSelectedNode() == null || !treePanel.getSelectedNode().isLeaf()) {
			MessageBox.show(this, "��ѡ��һ��ĩ��������ɾ������!"); //
			return;
		}
		BillVO billVO = treePanel.getSelectedVO();
		try {
			String count = UIUtil.getStringValueByDS(null, "select count(id) from sal_target_list where catalogid=" + billVO.getStringValue("id"));
			String catalogName = billVO.getStringValue("name");
			String catalogID = billVO.getStringValue("id");
			if (!MessageBox.confirm(this, "Ҫɾ���ķ��ࡾ" + catalogName + "���´��ڡ�" + count + "����ָ������, ����ؽ�������!\nȷ��Ҫ����ɾ����Щ������?")) {
				return;
			}
			ArrayList<String> sqlList = new ArrayList<String>();
			sqlList.add("delete from sal_target_catalog where  id=" + catalogID);
			sqlList.add("delete from sal_target_list where catalogid=" + catalogID);
			UIUtil.executeBatchByDS(null, sqlList);
			treePanel.delCurrNode(); //
			treePanel.updateUI();
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onUpdate() {
		BillListPanel editlistpanel = (edittype == 0 ? listpanel : listpanel_1);
		BillVO billvo = editlistpanel.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(editlistpanel);
			return;
		}
		String type = billvo.getStringValue("type");
		BillCardPanel cardPanel = null;
		if ("���Ŷ���ָ��".equals(type)) {
			cardPanel = new BillCardPanel(getTempletVO("SAL_TARGET_LIST_CODE1"));
		} else if ("���Ŷ���ָ��".equals(type)) {
			cardPanel = new BillCardPanel(getTempletVO("SAL_TARGET_LIST_CODE_QUANTIFY"));
		} else if ("���żƼ�ָ��".equals(type)) {
			cardPanel = new BillCardPanel(getTempletVO("SAL_TARGET_LIST_CODE_JJ"));
		}
		cardPanel.queryDataByCondition(" id = " + billvo.getPkValue());
		BillCardDialog dialog = new BillCardDialog(editlistpanel, "�޸�" + type, cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);
		if (dialog.getCloseType() == 1) {
			editlistpanel.refreshCurrSelectedRow();
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() instanceof WLTTabbedPane) {// ��ҳ��ȫ���ͷ���ҳǩ�л�
			if (!inittab2) {
				WLTTabbedPane tabp = (WLTTabbedPane) e.getSource();
				JPanel jPanel = (JPanel) tabp.getComponentAt(1);
				jPanel.removeAll();
				jPanel.setLayout(new BorderLayout());
				jPanel.add(getWLTSplitPane());
				jPanel.updateUI();
				inittab2 = true;
			}
		} else if (e.getSource() == radio) {// ��������radio�л���
			if (radio != null) {
				if (addPanelCurrShowRadioIndex == radio.getSelectIndex()) {
					return;
				}
				if (radio.getSelectIndex() == 1) { // ѡ����
					BillVO billvo = (addPanelCurrShowRadioIndex < 0 || addPanelCurrShowRadioIndex == 0) ? dxcardpanel.getBillVO() : jjcardpanel.getBillVO();
					dlcardpanel.setBillVO(billvo);
					dlcardpanel.setEditableByInsertInit();
					dlcardpanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
					dlcardpanel.setRealValueAt("type", "���Ŷ���ָ��");
				} else if (radio.getSelectIndex() == 0) {// ѡ���ԡ�
					BillVO billvo = addPanelCurrShowRadioIndex == 1 ? dlcardpanel.getBillVO() : jjcardpanel.getBillVO();
					String vlaue = billvo.getStringValue("checkeddept");
					if (!TBUtil.isEmpty(vlaue)) { // �����Ϊ�գ���ʾ
						if (!MessageBox.confirm(radio, "�л�ָ�����ͻ�ɾ����¼��ı����˲������ݣ��Ƿ����")) {
							radio.showOneTab("����ָ��");
							return;
						}
						try {
							// �л���ɾ������
							UIUtil.executeUpdateByDS(null, "delete from sal_target_checkeddept where targetid = '" + billvo.getPkValue() + "'");
						} catch (Exception e1) {
							MessageBox.showException(this, e1);
						}
					}
					dxcardpanel.setBillVO(billvo);
					dxcardpanel.setEditableByInsertInit();
					dxcardpanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
					dxcardpanel.setRealValueAt("type", "���Ŷ���ָ��");
					dxcardpanel.setRealValueAt("checkeddept", "");
				} else if (radio.getSelectIndex() == 2) { //�Ƽ�ָ��
					BillVO billvo = (addPanelCurrShowRadioIndex < 0 || addPanelCurrShowRadioIndex == 0) ? dxcardpanel.getBillVO() : dlcardpanel.getBillVO();
					jjcardpanel.setBillVO(billvo);
					jjcardpanel.setEditableByInsertInit();
					jjcardpanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
					jjcardpanel.setRealValueAt("type", "���żƼ�ָ��");
				}
				addPanelCurrShowRadioIndex = radio.getSelectIndex();
			}
		}
	}

	/*
	 * ������ȷ���¼���
	 */
	private void onConfirm() {
		BillVO billvo = null;
		if (radio.getSelectIndex() == 0) {
			if (!dxcardpanel.checkValidate()) {
				return;
			}
			String sql = dxcardpanel.getInsertSQL();
			try {
				billvo = dxcardpanel.getBillVO();
				UIUtil.executeBatchByDS(null, new String[] { sql });
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		} else if (radio.getSelectIndex() == 1) {
			if (!dlcardpanel.checkValidate()) {
				return;
			}
			String sql = dlcardpanel.getInsertSQL();
			try {
				billvo = dlcardpanel.getBillVO();
				UIUtil.executeBatchByDS(null, new String[] { sql });
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}else {
			if (!jjcardpanel.checkValidate()) {
				return;
			}
			String sql = jjcardpanel.getInsertSQL();
			try {
				billvo = jjcardpanel.getBillVO();
				UIUtil.executeBatchByDS(null, new String[] { sql });
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
		if (billvo == null) {
			return;
		}
		BillListPanel editlistpanel = (edittype == 0 ? listpanel : listpanel_1);
		int li_newrow = editlistpanel.newRow(false, false); //
		editlistpanel.setBillVOAt(li_newrow, billvo, false);
		editlistpanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); // �����б���е�����Ϊ��ʼ��״̬.
		editlistpanel.setSelectedRow(li_newrow); //
		dialog.dispose();
	}

	/*
	 * ��������Ĺرա�
	 */
	private void onCancel() {
		dialog.closeMe(); // ��ִ��ɾ���ӱ����ݡ�
	}

	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent event) {
		BillVO vo = event.getCurrSelectedVO();
		if (vo == null) {
			return;
		}
		String type = vo.getStringValue("type");
		String billtemplet = null;
		if ("���Ŷ���ָ��".equals(type)) {
			billtemplet = "SAL_TARGET_LIST_CODE_QUANTIFY";
		} else if ("���Ŷ���ָ��".equals(type)) {
			billtemplet = "SAL_TARGET_LIST_CODE1";
		} else if ("���żƼ�ָ��".equals(type)) {
			billtemplet = "SAL_TARGET_LIST_CODE_JJ";
		}
		BillCardPanel cardpanel = new BillCardPanel(getTempletVO(billtemplet));
		BillCardDialog carddialog = new BillCardDialog(listpanel, "���", cardpanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		carddialog.getBillcardPanel().queryDataByCondition(" id = " + vo.getPkValue());
		carddialog.setCardEditable(false);
		carddialog.setVisible(true);
	}

	public void onquery() {
		BillQueryPanel querypanel = listpanel.getQuickQueryPanel();
		HashMap map = querypanel.getQuickQueryConditionAsMap(true);
		String str_sql = querypanel.getQuerySQL(); // ���û�ж���SQL������,���׼���߼�ƴ��!!!
		if (str_sql == null) {
			return;
		}
		String str_currquicksql = str_sql; //

		if (map.get("checkeddept") != null) {
			String checkeddept = (String) map.get("checkeddept");
			String deptids[] = TBUtil.getTBUtil().split(checkeddept, ";");
			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			if (deptids != null && deptids.length > 0) {
				for (int i = 0; i < deptids.length; i++) {
					sb1.append(" checkeddept " + " like '%;" + deptids[i] + ";%'"); //
					sb2.append(" deptid " + " like '%;" + deptids[i] + ";%'"); //
					if (i != deptids.length - 1) { //
						sb1.append(" or ");
						sb2.append(" or ");
					}
				}
			}
			if (sb1.length() != 0) {
				str_currquicksql += " and (" + sb1.toString() + " or id in (select targetid from sal_target_checkeddept where " + sb2.toString() + ")) ";
			}
		}
		if (listpanel.getOrderCondition() != null) {
			str_currquicksql = str_currquicksql + " order by " + listpanel.getOrderCondition(); // ����!!
		}
		listpanel.queryDataByDS(null, str_currquicksql); // ��ƴ����������SQL���в�ѯ!!!
	}

	// ������������÷����µ�ָ��, ��������Ҷ�ӽڵ�����
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (_event.getCurrSelectedNode().isRoot()) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					listpanel_1.QueryDataByCondition(null);
				}
			});
		} else if (_event.getCurrSelectedVO() == null) {
			listpanel_1.clearTable();
		} else {
			if (_event.getCurrSelectedNode().isLeaf()) {
				BillVO billVO = _event.getCurrSelectedVO();
				final String catalogID = billVO.getStringValue("id");
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						listpanel_1.QueryDataByCondition("catalogid=" + catalogID);
					}
				});
			} else {
				final DefaultMutableTreeNode node = _event.getCurrSelectedNode();
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						ArrayList<String> ids = new ArrayList<String>();
						getAllChildIDs(node, ids);
						listpanel_1.QueryDataByCondition("catalogid in (" + TBUtil.getTBUtil().getInCondition(ids) + ")");
					}
				});
			}
		}
	}

	// �ݹ�ó�����Ҷ�ӽڵ��ID
	private void getAllChildIDs(DefaultMutableTreeNode node, ArrayList<String> ids) {
		Enumeration<DefaultMutableTreeNode> children = node.children();
		BillVO nodeVO = null;
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode child = children.nextElement();
			if (child.isLeaf()) { // �Ƿ�Ҷ�ӽڵ�
				nodeVO = treePanel.getBillVOFromNode(child);
				ids.add(nodeVO.getStringValue("id"));
			} else {
				// ��Ҷ�ӽڵ�������ݹ������
				getAllChildIDs(child, ids);
			}
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO vo = event.getCurrSelectedVO();
		if (vo == null) {
			return;
		}
		boolean edit = false;
		if ("���Ŷ���ָ��".equals(vo.getStringValue("type")) || "���żƼ�ָ��".equals(vo.getStringValue("type"))) {
			edit = true;
		}
		if (event.getBillListPanel() == listpanel) {
			btn_action.setEnabled(edit);
		} else {
			btn_action1.setEnabled(edit);
		}
	}
}
