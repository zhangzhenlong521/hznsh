package com.pushworld.icheck.ui.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * Ŀ¼ά��,����
 * 
 * @author scy
 * 
 */
public abstract class CK_ListManageWorkPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener {

	//Ŀ¼��
	private BillTreePanel tree = null;
	private BillCardDialog treeDialog = null; //����dialog
	private BillCardPanel treepPanel = null; //����panel
	private BillListPanel listPro = null; // ���������׼
	private BillListPanel listOut = null; // ������
	private BillCardDialog cardDialog = null; // Ŀ¼ά��dialog
	private BillCardPanel cardPanel = null; // Ŀ¼ά��panel
	private WLTButton tree_insert, tree_edit, tree_delete = null; // Ŀ¼�������༭
	private WLTButton pro_add, pro_edit; //���������׼��btn
	private WLTButton outline_add, outline_edit; //�����ٿ�btn
	private WLTButton btn_movedown_pro, btn_moveup_pro; //�������� ��������
	private WLTButton btn_movedown_out, btn_moveup_out; //�������� ��������
	private WLTTabbedPane panel = new WLTTabbedPane();

	@Override
	public void initialize() {
		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT);

		btn_moveup_pro = new WLTButton("����");
		btn_movedown_pro = new WLTButton("����");

		btn_moveup_out = new WLTButton("����");
		btn_movedown_out = new WLTButton("����");

		btn_moveup_out.addActionListener(this);
		btn_movedown_out.addActionListener(this);

		btn_moveup_pro.addActionListener(this);
		btn_movedown_pro.addActionListener(this);

		split.setDividerLocation(280);
		split.setDividerSize(2);
		split.add(getTreePanel());
		split.add(getWLTTabPanel());
		pro_add.setEnabled(false);
		outline_add.setEnabled(false);
		this.setLayout(new BorderLayout());
		this.add(split, BorderLayout.CENTER);
	}

	private BillTreePanel getTreePanel() {
		if (tree == null) {
			tree = new BillTreePanel(getTreeCode());
			// ֻ��Ŀ¼ά������Ҫ������������ť
			if ("list".equals(getSource())) {
				tree_insert = new WLTButton("����");
				tree_edit = new WLTButton("�༭");
				tree_delete = WLTButton.createButtonByType(WLTButton.TREE_DELETE);
				tree.getBillTreeBtnPanel().insertBatchButton(new WLTButton[] { tree_insert, tree_edit, tree_delete }); // ����,����,ɾ������ǰɾ����ť��ģ�������õģ�ģ�岻ͨ�á����/2016-09-29��
				tree.repaintBillTreeButton();
				tree_insert.addActionListener(this);
				tree_edit.addActionListener(this);
			}
			tree.addBillTreeSelectListener(this);
			//			//�жϵ�ǰ�˽�ɫ
			tree.queryDataByCondition(" 1=1");
		}
		return tree;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == tree_insert) {//Ŀ¼����
				onItemAdd();
			} else if (e.getSource() == tree_edit) {//����Ŀ¼�༭
				onTreeEdit();
			} else if (e.getSource() == pro_add) {
				onProInsert();
			} else if (e.getSource() == outline_add) {
				OutlineInsert();
			} else if (e.getSource() == btn_moveup_pro) {
				onMoveUp(listPro);
			} else if (e.getSource() == btn_movedown_pro) {
				onMoveDown(listPro);
			} else if (e.getSource() == btn_moveup_out) {
				onMoveUp(listOut);
			} else if (e.getSource() == btn_movedown_out) {
				onMoveDown(listOut);
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private WLTTabbedPane getWLTTabPanel() {
		// Ŀ¼ά��
		if ("list".equals(getSource())) {
			panel.addTab("���������׼", getListPanelByPro());
			panel.addTab("������", getListPanelByOut());
		}// ���������׼ά��
		else if ("problem".equals(getSource())) {
			panel.addTab("���������׼", getListPanelByPro());
		}
		// ������ά��
		else if ("outline".equals(getSource())) {
			panel.addTab("������", getListPanelByOut());
		}
		return panel;
	}

	/**
	 * ���������׼��
	 * 
	 * @param list
	 * @return
	 */
	private BillListPanel getListPanelByPro() {
		if (listPro == null) {
			listPro = new BillListPanel(getListProCode());
			pro_add = new WLTButton("����");
			pro_add.addActionListener(this);
			pro_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "�༭");
			listPro.addBatchBillListButton(new WLTButton[] { pro_add, pro_edit, btn_movedown_pro, btn_moveup_pro });
			listPro.repaintBillListButton();
		}
		return listPro;
	}

	/*
	 * �����ٿ�
	 */
	private BillListPanel getListPanelByOut() {
		if (listOut == null) {
			listOut = new BillListPanel(getListOutCode());
			outline_add = new WLTButton("����");
			outline_add.addActionListener(this);
			outline_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "�༭");
			listOut.addBatchBillListButton(new WLTButton[] { outline_add, outline_edit, btn_movedown_out, btn_moveup_out });
			listOut.repaintBillListButton();
		}
		return listOut;
	}

	/**
	 * Ŀ¼���༭
	 */
	public void onTreeEdit() {
		BillVO vo = tree.getSelectedVO();
		if (null == vo) {
			MessageBox.showSelectOne(this);
			return;
		}
		treepPanel = new BillCardPanel(getTreeCode());
		treepPanel.setBillVO(vo);
		treeDialog = new BillCardDialog(this, "�༭", treepPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		treeDialog.getBtn_save().setVisible(false);
		treeDialog.setVisible(true);
		if (treeDialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = treepPanel.getBillVO(); //
			newVO.setToStringFieldName(tree.getTempletVO().getTreeviewfield()); //
			tree.setBillVOForCurrSelNode(newVO); //�����л�д����
			tree.updateUI(); //��ǰ��billTree.getJTree().updateUI();��ı�չ��������ͼ�꣬���2012-02-23�޸�
		}
	}

	/**
	 * �����׼����
	 */
	public void onProInsert() {
		BillVO vo = tree.getSelectedVO();
		BillVO[] vos = tree.getSelectedParentPathVOs();
		String leveldesc = vo.getStringValue("leveldesc");

		BillCardDialog cardDialog = new BillCardDialog(this, "���������׼����", "CK_PROBLEM_DICT_SCY_E01", 650, 600);
		cardDialog.getBtn_save().setVisible(false);
		BillCardPanel cardPanel = cardDialog.getBillcardPanel();
		cardPanel.setEditState("INSERT");

		if ("����Ŀ¼".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vos[0] + "")); //
			cardPanel.setCompentObjectValue("secondname", new StringItemVO(vos[1] + "")); //
			cardPanel.setCompentObjectValue("thirdname", new StringItemVO(vos[2] + "")); //

			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("firstid"))); //
			cardPanel.setCompentObjectValue("secondid", new StringItemVO(vo.getStringValue("secondid"))); //
			cardPanel.setCompentObjectValue("thirdid", new StringItemVO(vo.getStringValue("thirdid"))); //
			cardPanel.setCompentObjectValue("parentid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		} else if ("����Ŀ¼".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vos[0] + "")); //
			cardPanel.setCompentObjectValue("secondname", new StringItemVO(vos[1] + "")); //

			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("firstid"))); //
			cardPanel.setCompentObjectValue("secondid", new StringItemVO(vo.getStringValue("secondid"))); //
			cardPanel.setCompentObjectValue("parentid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		} else if ("һ��Ŀ¼".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vo.getStringValue("listname"))); //
			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		}
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			String id = vo.getStringValue("id");
			if ("һ��Ŀ¼".equals(leveldesc)) {
				listPro.QueryDataByCondition("firstid = '" + id + "' ");
				listOut.QueryDataByCondition("firstid = '" + id + "' ");
			} else if ("����Ŀ¼".equals(leveldesc)) {
				listPro.QueryDataByCondition("secondid = '" + id + "' ");
				listOut.QueryDataByCondition("secondid = '" + id + "' ");
			} else if ("����Ŀ¼".equals(leveldesc)) {
				listPro.QueryDataByCondition("parentid = '" + id + "' ");
				listOut.QueryDataByCondition("parentid = '" + id + "' ");
			}
		}
	}

	/**
	 * ����������
	 */
	public void OutlineInsert() {
		BillVO vo = tree.getSelectedVO();
		String leveldesc = vo.getStringValue("leveldesc");
		BillVO[] vos = tree.getSelectedParentPathVOs();

		BillCardDialog cardDialog = new BillCardDialog(this, "����������", "CK_OUTLINE_SCY_E01", 650, 600);
		cardDialog.getBtn_save().setVisible(false);
		BillCardPanel cardPanel = cardDialog.getBillcardPanel();
		cardPanel.setEditState("INSERT");

		if ("����Ŀ¼".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vos[0] + "")); //
			cardPanel.setCompentObjectValue("secondname", new StringItemVO(vos[1] + "")); //
			cardPanel.setCompentObjectValue("thirdname", new StringItemVO(vos[2] + "")); //

			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("firstid"))); //
			cardPanel.setCompentObjectValue("secondid", new StringItemVO(vo.getStringValue("secondid"))); //
			cardPanel.setCompentObjectValue("thirdid", new StringItemVO(vo.getStringValue("thirdid"))); //
			cardPanel.setCompentObjectValue("parentid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		} else if ("����Ŀ¼".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vos[0] + "")); //
			cardPanel.setCompentObjectValue("secondname", new StringItemVO(vos[1] + "")); //

			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("firstid"))); //
			cardPanel.setCompentObjectValue("secondid", new StringItemVO(vo.getStringValue("secondid"))); //
			cardPanel.setCompentObjectValue("parentid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		} else if ("һ��Ŀ¼".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vo.getStringValue("listname"))); //
			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		}
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			String id = vo.getStringValue("id");
			if ("һ��Ŀ¼".equals(leveldesc)) {
				listPro.QueryDataByCondition("firstid = '" + id + "' ");
				listOut.QueryDataByCondition("firstid = '" + id + "' ");
			} else if ("����Ŀ¼".equals(leveldesc)) {
				listPro.QueryDataByCondition("secondid = '" + id + "' ");
				listOut.QueryDataByCondition("secondid = '" + id + "' ");
			} else if ("����Ŀ¼".equals(leveldesc)) {
				listPro.QueryDataByCondition("parentid = '" + id + "' ");
				listOut.QueryDataByCondition("parentid = '" + id + "' ");
			}
		}
	}

	/**
	 * Ŀ¼����
	 */
	public void onItemAdd() {
		String level = "һ��Ŀ¼";
		//����Ŀ¼ID
		String one = null;
		String two = null;
		String three = null;
		if (tree.getSelectedNode() == null) {
			MessageBox.show(this, "��ѡ��һ������������������!"); //
			return;
		}
		BillVO billVO = tree.getSelectedVO();
		BillCardPanel insertCardPanel = new BillCardPanel(tree.getTempletVO()); //
		insertCardPanel.insertRow(); // ����һ��
		insertCardPanel.setEditableByInsertInit(); //
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) tree.getSelectedNode();
		one = insertCardPanel.getRealValueAt("id");
		if (billVO != null) {
			BillVO parentVO = billVO; //
			String parent_id = ((StringItemVO) parentVO.getObject(tree.getTempletVO().getTreepk())).getStringValue(); //
			insertCardPanel.setCompentObjectValue(tree.getTempletVO().getTreeparentpk(), new StringItemVO(parent_id)); //

			if ("һ��Ŀ¼".equals(billVO.getStringValue("leveldesc"))) {
				level = "����Ŀ¼";
				one = billVO.getStringValue("firstid");
				two = insertCardPanel.getRealValueAt("id");
			}

			if ("����Ŀ¼".equals(billVO.getStringValue("leveldesc"))) {
				level = "����Ŀ¼";
				one = billVO.getStringValue("firstid");
				two = billVO.getStringValue("secondid");
				three = insertCardPanel.getRealValueAt("id");
			} else {

			}
		}
		BillCardDialog dialog = new BillCardDialog(tree, "����", insertCardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //
		insertCardPanel.setCompentObjectValue("leveldesc", new StringItemVO(level)); //
		//����Ŀ¼ID
		insertCardPanel.setCompentObjectValue("firstid", new StringItemVO(one)); //
		insertCardPanel.setCompentObjectValue("secondid", new StringItemVO(two)); //
		insertCardPanel.setCompentObjectValue("thirdid", new StringItemVO(three)); //
		//����Ĭ��
		dialog.getBtn_save().setVisible(false);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = insertCardPanel.getBillVO(); //
			newVO.setToStringFieldName(tree.getTempletVO().getTreeviewfield()); //
			tree.addNode(newVO); //
		}
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billVO = _event.getCurrSelectedVO();
		if (null == billVO) {
			tree_insert.setEnabled(true); //
			pro_add.setEnabled(false);
			outline_add.setEnabled(false);
			if (listPro != null) {//���ѡ�и��ڵ㣬��δѡ�У���Ҫ����б������/2016-09-29��
				listPro.clearTable();
			}
			if (listOut != null) {
				listOut.clearTable();
			}
			return;
		}
		String id = billVO.getStringValue("id");
		String level = billVO.getStringValue("leveldesc");
		if ("list".equals(getSource())) {
			if ("һ��Ŀ¼".equals(level)) {
				listPro.QueryDataByCondition("firstid = '" + id + "' ");
				listOut.QueryDataByCondition("firstid = '" + id + "' ");
				tree_insert.setEnabled(true);
				pro_add.setEnabled(false);
				outline_add.setEnabled(false);
			} else if ("����Ŀ¼".equals(level)) {
				listPro.QueryDataByCondition("secondid = '" + id + "' ");
				listOut.QueryDataByCondition("secondid = '" + id + "' ");
				tree_insert.setEnabled(true);
				pro_add.setEnabled(false);
				outline_add.setEnabled(false);
			} else if ("����Ŀ¼".equals(level)) {
				listPro.QueryDataByCondition("parentid = '" + id + "' ");
				listOut.QueryDataByCondition("parentid = '" + id + "' ");
				tree_insert.setEnabled(false); //
				pro_add.setEnabled(true);
				outline_add.setEnabled(true);
			}
		} else if ("problem".equals(getSource())) {
			if ("һ��Ŀ¼".equals(level)) {
				listPro.QueryDataByCondition("firstid = '" + id + "' ");
				pro_add.setEnabled(false);
			} else if ("����Ŀ¼".equals(level)) {
				listPro.QueryDataByCondition("secondid = '" + id + "' ");
				pro_add.setEnabled(false);
			} else if ("����Ŀ¼".equals(level)) {
				listPro.QueryDataByCondition("parentid = '" + id + "' ");
				pro_add.setEnabled(true);
			}
		} else if ("outline".equals(getSource())) {

			if ("һ��Ŀ¼".equals(level)) {
				listOut.QueryDataByCondition("firstid = '" + id + "' ");
			} else if ("����Ŀ¼".equals(level)) {
				listOut.QueryDataByCondition("secondid = '" + id + "' ");
			} else if ("����Ŀ¼".equals(level)) {
				listOut.QueryDataByCondition("parentid = '" + id + "' ");
			}
		}
	}

	/**
	 * ����
	 */
	private void onMoveUp(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillListPanel billList = listPanel; //
		billList.moveUpRow(); //����
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();//�������������������У����ø��ֶ����򣬷���Ĭ��Ϊseq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//��ǰ��������һ�������������ƻ����Ʋ���������󣬷������ݲ�û�б䣬��Ϊ��ǰ����û�д������seqΪ�յ���������Ҵ�����seqfildֵ�����ֵ������
			//ע��ڶ����ж���billList.getRealValueAtModel()�õ������ַ�������billList.getValueAt()�õ�����StringItemVO����
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(billList.getRowNumberEditState(i))) {//����ǳ�ʼ״̬�����ø��£���������״̬������ִ��update���ܱ��桾���/2014-10-31��
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
		billList.saveData();
	}

	/**
	 * ����
	 */
	private void onMoveDown(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillListPanel billList = listPanel; //
		billList.moveDownRow(); //����
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();//�������������������У����ø��ֶ����򣬷���Ĭ��Ϊseq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//��ǰ��������һ�������������ƻ����Ʋ���������󣬷������ݲ�û�б䣬��Ϊ��ǰ����û�д������seqΪ�յ����
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(billList.getRowNumberEditState(i))) {//����ǳ�ʼ״̬�����ø��£���������״̬������ִ��update���ܱ��桾���/2014-10-31��
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
		billList.saveData();
	}

	/**
	 * Ŀ¼
	 * 
	 * @return
	 */
	public abstract String getTreeCode();

	/**
	 * ���������׼
	 * 
	 * @return
	 */
	public abstract String getListProCode();

	/**
	 * ������
	 * 
	 * @return
	 */
	public abstract String getListOutCode();

	/**
	 * ��Դ Ŀ¼ά��������ά�������
	 * 
	 * @return
	 */
	public abstract String getSource();

	public static void main(String[] args) {

	}
}