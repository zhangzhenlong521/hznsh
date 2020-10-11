package cn.com.pushworld.salary.ui.target;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/***
 * ���Ŷ���ָ��ά��
 * @author Gwang
 * 2013-6-24 ����02:43:14
 */
public class TargetListWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener, ChangeListener {

	private BillTreePanel treePanel; //ָ��������
	private BillListPanel bp_targetlist, listPanel; //ָ���б�
	private WLTButton btn_add0, btn_edit0, btn_delete0, btn_add, btn_edit, btn_delete; //�б��ϵ����а�ť
	private WLTButton btn_tree_add, btn_tree_edit, btn_tree_del; //�������ϵİ�ť
	private WLTTabbedPane maintab = null;

	public void initialize() {
		bp_targetlist = new BillListPanel("SAL_TARGET_LIST_CODE1");
		bp_targetlist.setDataFilterCustCondition("type='���Ŷ���ָ��'");
		initBtn();
		bp_targetlist.QueryDataByCondition(null);
		maintab = new WLTTabbedPane();
		maintab.addTab("ȫ����ʾ", bp_targetlist);
		JPanel blankPanel = new JPanel(new BorderLayout());
		maintab.addTab("��ָ�������ʾ", blankPanel);
		maintab.addChangeListener(this);
		this.add(maintab);
	}

	public void initBtn() {
		btn_add0 = new WLTButton("����");
		btn_edit0 = new WLTButton("�޸�");
		btn_delete0 = new WLTButton("ɾ��");
		btn_add0.addActionListener(this);
		btn_edit0.addActionListener(this);
		btn_delete0.addActionListener(this);
		bp_targetlist.addBatchBillListButton(new WLTButton[] { btn_add0, btn_edit0, btn_delete0 });
		bp_targetlist.repaintBillListButton();
	}

	public WLTSplitPane getWLTSplitPane() {
		treePanel = new BillTreePanel("SAL_TARGET_CATALOG_CODE1");
		treePanel.queryDataByCondition(null);
		treePanel.addBillTreeSelectListener(this);
		btn_tree_add = WLTButton.createButtonByType(WLTButton.TREE_INSERT);
		btn_tree_edit = WLTButton.createButtonByType(WLTButton.TREE_EDIT);
		btn_tree_del = new WLTButton("ɾ��");
		btn_tree_del.addActionListener(this);
		treePanel.addBatchBillTreeButton(new WLTButton[] { btn_tree_add, btn_tree_edit, btn_tree_del });
		treePanel.repaintBillTreeButton();
		listPanel = new BillListPanel("SAL_TARGET_LIST_CODE1");
		listPanel.setDataFilterCustCondition("type='���Ŷ���ָ��'");
		listPanel.setBillQueryPanelVisible(false);
		btn_add = new WLTButton("����");
		btn_edit = new WLTButton("�޸�");
		btn_delete = new WLTButton("ɾ��");
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete });
		listPanel.repaintBillListButton();
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, treePanel, listPanel);
		splitPane.setDividerLocation(220);
		return splitPane;
	}

	//������������÷����µ�ָ��, ��������Ҷ�ӽڵ�����
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (_event.getCurrSelectedNode().isRoot()) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					listPanel.QueryDataByCondition(null);
				}
			});
		} else if (_event.getCurrSelectedVO() == null) {
			listPanel.clearTable();
		} else {
			if (_event.getCurrSelectedNode().isLeaf()) {
				BillVO billVO = _event.getCurrSelectedVO();
				final String catalogID = billVO.getStringValue("id");
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						listPanel.QueryDataByCondition("catalogid=" + catalogID);
					}
				});
			} else {
				final DefaultMutableTreeNode node = _event.getCurrSelectedNode();
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						ArrayList<String> ids = new ArrayList<String>();
						getAllChildIDs(node, ids);
						listPanel.QueryDataByCondition("catalogid in (" + TBUtil.getTBUtil().getInCondition(ids) + ")");
					}
				});
			}
		}
	}

	//�ݹ�ó�����Ҷ�ӽڵ��ID
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

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAdd(listPanel, 1);
		} else if (e.getSource() == btn_edit) {
			onEdit(listPanel);
		} else if (e.getSource() == btn_delete) {
			onDelete(listPanel);
		} else if (e.getSource() == btn_edit0) {
			onEdit(bp_targetlist);
		} else if (e.getSource() == btn_delete0) {
			onDelete(bp_targetlist);
		} else if (e.getSource() == btn_add0) {
			onAdd(bp_targetlist, 0);
		} else if (e.getSource() == btn_tree_del) {
			onTreeDelete();
		}
	}

	/**
	 * ����
	 */
	private void onAdd(BillListPanel listPanel, int tab) {
		BillCardPanel cardPanel = null;
		String type = null;
		if (tab == 1) {
			BillVO billVO = treePanel.getSelectedVO();
			if (billVO == null) {
				MessageBox.show(this, "����ѡ��һ��ָ�����.");
				return;
			}
			if (!treePanel.getSelectedNode().isLeaf()) {
				MessageBox.show(this, "��ѡ����ĩ���ķ���.");
				return;
			}
			//����һ����Ƭ���
			cardPanel = new BillCardPanel(listPanel.getTempletVO());
			type = "���Ŷ���ָ��";
			cardPanel.insertRow();
			cardPanel.setEditableByInsertInit();
			//����Ĭ��ֵ
			cardPanel.setValueAt("catalogid", new RefItemVO(billVO.getStringValue("id"), "", billVO.getStringValue("name")));
		} else {
			//����һ����Ƭ���
			cardPanel = new BillCardPanel(listPanel.getTempletVO());
			type = "���Ŷ���ָ��";
			cardPanel.insertRow();
			cardPanel.setEditableByInsertInit();
		}
		cardPanel.setRealValueAt("type", type);

		//������
		BillCardDialog cardDialog = new BillCardDialog(listPanel, "����" + type, cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);

		//ȷ������
		if (cardDialog.getCloseType() == 1) {
			int li_newrow = listPanel.newRow(false); //
			listPanel.setBillVOAt(li_newrow, cardDialog.getBillVO(), false);
			listPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.
			listPanel.setSelectedRow(li_newrow); //			
		}
	}

	/**
	 * �༭
	 */
	private void onEdit(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		BillCardPanel cardPanel = null;
		//����һ����Ƭ���
		cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.setBillVO(billVO);
		cardPanel.setVisiable("evalstandard", false);
		//������
		BillCardDialog dialog = new BillCardDialog(this, "�޸�ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);
		//ȷ������
		if (dialog.getCloseType() == 1) {
			if (listPanel.getSelectedRow() == -1) {
			} else {
				listPanel.setBillVOAt(listPanel.getSelectedRow(), dialog.getBillVO());
				listPanel.setRowStatusAs(listPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		}
	}

	/**
	 * ɾ��
	 */
	private void onDelete(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		listPanel.doDelete(false);
	}

	//ָ�������ɾ��, �������Լ��÷����µ�ָ��һ��ɾ��!
	private void onTreeDelete() {
		if (treePanel.getSelectedNode() == null || !treePanel.getSelectedNode().isLeaf()) {
			MessageBox.show(this, "��ѡ��һ��ĩ��������ɾ������!"); //
			return;
		}
		BillVO billVO = treePanel.getSelectedVO();
		try {
			String count = UIUtil.getStringValueByDS(null, "select count(id) from sal_target_list where catalogid=" + billVO.getStringValue("id"));
			//			if (!"0".equals(count)) {
			//				MessageBox.show(this, "�������Ѿ���ʹ��,����ɾ��!"); //
			//				return;
			//			}

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
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	/**
	 * ����
	 */
	/*
	private void onPublish() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("��Ч".equals(billVO.getStringValue("state"))) {
			MessageBox.show(this, "�ü�¼�ѷ���,�����ظ�����.");
			return;
		}
		try {

			UIUtil.executeUpdateByDSPS(null, "update " + listPanel.getTempletVO().getSavedtablename() + " set state='��Ч' where id=" + billVO.getPkValue());
			listPanel.refreshCurrSelectedRow();
			//MessageBox.show(this, "�����ɹ�!");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	/**
	 * ��ֹ
	 */
	/*
	private void onAbolish() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("��ֹ".equals(billVO.getStringValue("state"))) {
			MessageBox.show(this, "�ü�¼�ѷ�ֹ,�����ظ�����.");
			return;
		}
		if (MessageBox.confirm(this, "ȷ��Ҫ��ֹ?")) {
			try {
				UIUtil.executeUpdateByDSPS(null, "update " + listPanel.getTempletVO().getSavedtablename() + " set state='��ֹ' where id=" + billVO.getPkValue());
				listPanel.refreshCurrSelectedRow();
				//MessageBox.show(this, "��ֹ�ɹ�!");
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showException(this, e);
			}
		}
	}
	*/
	public void stateChanged(ChangeEvent e) {
		if (maintab.getSelectedIndex() == 1 && listPanel == null) {
			onClickSecTab();
		}
	}

	private void onClickSecTab() {
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				maintab.getComponentAt(1).add(getWLTSplitPane(), BorderLayout.CENTER);
			}
		});
	}

	/**
	 * �鿴��־���״̬�Ƿ�
	 * @return
	 */
	public boolean checkEditable() {
		return true;
	}

}
