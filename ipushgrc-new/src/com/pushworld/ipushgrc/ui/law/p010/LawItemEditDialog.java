package com.pushworld.ipushgrc.ui.law.p010;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * ������Ŀ��д�Ի��� 
 * @author hm
 */
public class LawItemEditDialog extends BillDialog implements ActionListener, BillTreeSelectListener {

	private BillTreePanel billTree_lawitem = null; // ��Ŀ����
	private BillCardPanel billCard_lawitem = null; // ��Ŀ��Ƭ�༭���

	private BillVO selectLaw = null; //��ǰѡ��ķ���VO

	private JMenuItem item_insert, item_delete; //���ӣ�ɾ�� 

	private WLTButton btn_save = new WLTButton("����", UIUtil.getImage("zt_054.gif"));

	private boolean haveChanged = false;

	public LawItemEditDialog(Container _parent, String _title, int _width, int li_height) {
		this(_parent, _title, _width, li_height, null);
	}

	public LawItemEditDialog(Container _parent, String _title, int _width, int li_height, BillVO _selectLaw) {
		super(_parent, _title, _width, li_height);
		selectLaw = _selectLaw;
		initialize();
	}

	private void initialize() {
		billTree_lawitem = new BillTreePanel("LAW_LAW_ITEM_CODE1"); //
		billCard_lawitem = new BillCardPanel("LAW_LAW_ITEM_CODE1"); //
		billCard_lawitem.addBillCardButton(btn_save); // �ѱ��水ť���ӵ���Ƭ��壡
		billCard_lawitem.repaintBillCardButton();
		BillTreeNodeVO treenodevo = new BillTreeNodeVO(-1, selectLaw.getStringValue("lawname"));
		billTree_lawitem.getRootNode().setUserObject(treenodevo);
		billTree_lawitem.queryData("select * from law_law_item where lawid = " + selectLaw.getStringValue("id") + " order by abs(id)");
		WLTSplitPane split = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_lawitem, billCard_lawitem); //
		split.setDividerLocation(250);
		billTree_lawitem.addBillTreeSelectListener(this);
		item_insert = new JMenuItem("����");
		item_delete = new JMenuItem("ɾ��");
		item_insert.addActionListener(this);
		item_delete.addActionListener(this);
		billTree_lawitem.setAppMenuItems(new JMenuItem[] { item_insert, item_delete });
		btn_save.addActionListener(this);
		billTree_lawitem.setDragable(true);
		billTree_lawitem.setMoveUpDownBtnVisiable(true);
		this.getContentPane().add(split); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == item_insert) {
			onItemInsert();
		} else if (obj == item_delete) {
			onItemDelete();
		} else if (obj == btn_save) {
			onItemSave();
		}
	}

	/**
	 * �Ҽ����������
	 */
	private void onItemInsert() {
		billCard_lawitem.insertRow();
		billCard_lawitem.setEditableByInsertInit();
		if (billTree_lawitem.getSelectedNode().getChildCount() > 0) { // �ж���û���ӽڵ�
			String max_str_seq = billTree_lawitem.getBillVOFromNode((DefaultMutableTreeNode) billTree_lawitem.getSelectedNode().getLastChild()).getStringValue("seq");
			if (max_str_seq == null) {
				billTree_lawitem.resetChildSeq();
				max_str_seq = billTree_lawitem.getBillVOFromNode((DefaultMutableTreeNode) billTree_lawitem.getSelectedNode().getLastChild()).getStringValue("seq");
			}
			billCard_lawitem.setValueAt("seq", new StringItemVO(Integer.parseInt(max_str_seq) + 1 + ""));
		} else {
			billCard_lawitem.setValueAt("seq", new StringItemVO("1"));
		}
		String treeid = null;
		if (!billTree_lawitem.getSelectedNode().isRoot()) {
			treeid = billTree_lawitem.getSelectedVO().getStringValue("id");
		}
		billCard_lawitem.setValueAt("parentid", new StringItemVO(treeid));
		billCard_lawitem.setValueAt("lawid", new StringItemVO(selectLaw.getStringValue("id")));
	}

	/**
	 * �Ҽ������ɾ��
	 */
	private void onItemDelete() {
		if (billTree_lawitem.getSelectedNode().isRoot()) {
			MessageBox.show("���ڵ㲻��ɾ����");
			return;
		}
		DefaultMutableTreeNode snode = billTree_lawitem.getSelectedNode();
		if (!snode.isLeaf()) {
			if (MessageBox.showConfirmDialog(billTree_lawitem, "�Ƿ�ɾ�����ڵ��µ���������?", "ɾ����?", 0) != 0)
				return;
			try {
				BillVO billvo[] = billTree_lawitem.getSelectedChildPathBillVOs();
				ArrayList al_sql = new ArrayList();
				for (int i = 0; i < billvo.length; i++) {
					String id = billvo[i].getStringValue("id");
					al_sql.add("delete from  law_law_item where id=" + id + "");
				}
				UIUtil.executeBatchByDS(null, al_sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (snode.isLeaf()) {
			if (MessageBox.showConfirmDialog(billTree_lawitem, "�Ƿ�ɾ����ǰ����?", "ɾ����?", 0) != 0)
				return;
			try {
				UIUtil.executeUpdateByDS(null, (new StringBuilder("delete from law_law_item where id=")).append(billTree_lawitem.getSelectedVO().getStringValue("id")).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		billTree_lawitem.delCurrNode();
		billCard_lawitem.clear();
		setHaveChanged(true);
	}

	/**
	 * ������Ŀ����
	 */
	private void onItemSave() {
		if (!billCard_lawitem.checkValidate()) {
			return;
		}
		try {
			String stade = billCard_lawitem.getEditState();
			if (stade.equals("INSERT")) {
				billCard_lawitem.updateData();
				billTree_lawitem.addNode(billCard_lawitem.getBillVO());
				billTree_lawitem.updateUI();
			} else if (stade.equals("UPDATE")) {
				String str_newsubject = ((StringItemVO) billCard_lawitem.getValueAt(billTree_lawitem.getTempletVO().getTreeviewfield())).getStringValue();
				billTree_lawitem.setBillVOForCurrSelNode(billCard_lawitem.getBillVO());
				billCard_lawitem.updateData();
				billTree_lawitem.updateUI();
			}
			MessageBox.show(this, "����ɹ���");
			billTree_lawitem.resetChildSeq();
			setHaveChanged(true);
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}

	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billvo = _event.getCurrSelectedVO();
		if (billvo == null) {
			billCard_lawitem.clear();
			billCard_lawitem.setEditable(false);
		} else {
			String str_id = billvo.getStringValue(billCard_lawitem.getTempletVO().getPkname());
			billCard_lawitem.queryDataByCondition((new StringBuilder(String.valueOf(billCard_lawitem.getTempletVO().getPkname()))).append("=").append(str_id).toString());
			billCard_lawitem.setEditState("UPDATE");
			billCard_lawitem.setEditableByEditInit();
		}
	}

	public boolean isHaveChanged() {
		return haveChanged;
	}

	public void setHaveChanged(boolean haveChanged) {
		this.haveChanged = haveChanged;
	}

}