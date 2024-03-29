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
 * 目录维护,发布
 * 
 * @author scy
 * 
 */
public abstract class CK_ListManageWorkPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener {

	//目录树
	private BillTreePanel tree = null;
	private BillCardDialog treeDialog = null; //树形dialog
	private BillCardPanel treepPanel = null; //树形panel
	private BillListPanel listPro = null; // 问题归属标准
	private BillListPanel listOut = null; // 检查提纲
	private BillCardDialog cardDialog = null; // 目录维护dialog
	private BillCardPanel cardPanel = null; // 目录维护panel
	private WLTButton tree_insert, tree_edit, tree_delete = null; // 目录新增、编辑
	private WLTButton pro_add, pro_edit; //问题归属标准库btn
	private WLTButton outline_add, outline_edit; //检查提纲库btn
	private WLTButton btn_movedown_pro, btn_moveup_pro; //问题上移 问题下移
	private WLTButton btn_movedown_out, btn_moveup_out; //问题上移 问题下移
	private WLTTabbedPane panel = new WLTTabbedPane();

	@Override
	public void initialize() {
		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT);

		btn_moveup_pro = new WLTButton("上移");
		btn_movedown_pro = new WLTButton("下移");

		btn_moveup_out = new WLTButton("上移");
		btn_movedown_out = new WLTButton("下移");

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
			// 只有目录维护才需要发布、新增按钮
			if ("list".equals(getSource())) {
				tree_insert = new WLTButton("新增");
				tree_edit = new WLTButton("编辑");
				tree_delete = WLTButton.createButtonByType(WLTButton.TREE_DELETE);
				tree.getBillTreeBtnPanel().insertBatchButton(new WLTButton[] { tree_insert, tree_edit, tree_delete }); // 发布,新增,删除，以前删除按钮在模板中配置的，模板不通用【李春娟/2016-09-29】
				tree.repaintBillTreeButton();
				tree_insert.addActionListener(this);
				tree_edit.addActionListener(this);
			}
			tree.addBillTreeSelectListener(this);
			//			//判断当前人角色
			tree.queryDataByCondition(" 1=1");
		}
		return tree;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == tree_insert) {//目录新增
				onItemAdd();
			} else if (e.getSource() == tree_edit) {//三级目录编辑
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
		// 目录维护
		if ("list".equals(getSource())) {
			panel.addTab("问题归属标准", getListPanelByPro());
			panel.addTab("检查提纲", getListPanelByOut());
		}// 问题归属标准维护
		else if ("problem".equals(getSource())) {
			panel.addTab("问题归属标准", getListPanelByPro());
		}
		// 检查提纲维护
		else if ("outline".equals(getSource())) {
			panel.addTab("检查提纲", getListPanelByOut());
		}
		return panel;
	}

	/**
	 * 问题归属标准库
	 * 
	 * @param list
	 * @return
	 */
	private BillListPanel getListPanelByPro() {
		if (listPro == null) {
			listPro = new BillListPanel(getListProCode());
			pro_add = new WLTButton("新增");
			pro_add.addActionListener(this);
			pro_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "编辑");
			listPro.addBatchBillListButton(new WLTButton[] { pro_add, pro_edit, btn_movedown_pro, btn_moveup_pro });
			listPro.repaintBillListButton();
		}
		return listPro;
	}

	/*
	 * 检查提纲库
	 */
	private BillListPanel getListPanelByOut() {
		if (listOut == null) {
			listOut = new BillListPanel(getListOutCode());
			outline_add = new WLTButton("新增");
			outline_add.addActionListener(this);
			outline_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "编辑");
			listOut.addBatchBillListButton(new WLTButton[] { outline_add, outline_edit, btn_movedown_out, btn_moveup_out });
			listOut.repaintBillListButton();
		}
		return listOut;
	}

	/**
	 * 目录树编辑
	 */
	public void onTreeEdit() {
		BillVO vo = tree.getSelectedVO();
		if (null == vo) {
			MessageBox.showSelectOne(this);
			return;
		}
		treepPanel = new BillCardPanel(getTreeCode());
		treepPanel.setBillVO(vo);
		treeDialog = new BillCardDialog(this, "编辑", treepPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		treeDialog.getBtn_save().setVisible(false);
		treeDialog.setVisible(true);
		if (treeDialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = treepPanel.getBillVO(); //
			newVO.setToStringFieldName(tree.getTempletVO().getTreeviewfield()); //
			tree.setBillVOForCurrSelNode(newVO); //向树中回写数据
			tree.updateUI(); //以前是billTree.getJTree().updateUI();会改变展开和收缩图标，李春娟2012-02-23修改
		}
	}

	/**
	 * 问题标准新增
	 */
	public void onProInsert() {
		BillVO vo = tree.getSelectedVO();
		BillVO[] vos = tree.getSelectedParentPathVOs();
		String leveldesc = vo.getStringValue("leveldesc");

		BillCardDialog cardDialog = new BillCardDialog(this, "问题归属标准新增", "CK_PROBLEM_DICT_SCY_E01", 650, 600);
		cardDialog.getBtn_save().setVisible(false);
		BillCardPanel cardPanel = cardDialog.getBillcardPanel();
		cardPanel.setEditState("INSERT");

		if ("三级目录".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vos[0] + "")); //
			cardPanel.setCompentObjectValue("secondname", new StringItemVO(vos[1] + "")); //
			cardPanel.setCompentObjectValue("thirdname", new StringItemVO(vos[2] + "")); //

			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("firstid"))); //
			cardPanel.setCompentObjectValue("secondid", new StringItemVO(vo.getStringValue("secondid"))); //
			cardPanel.setCompentObjectValue("thirdid", new StringItemVO(vo.getStringValue("thirdid"))); //
			cardPanel.setCompentObjectValue("parentid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		} else if ("二级目录".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vos[0] + "")); //
			cardPanel.setCompentObjectValue("secondname", new StringItemVO(vos[1] + "")); //

			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("firstid"))); //
			cardPanel.setCompentObjectValue("secondid", new StringItemVO(vo.getStringValue("secondid"))); //
			cardPanel.setCompentObjectValue("parentid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		} else if ("一级目录".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vo.getStringValue("listname"))); //
			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		}
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			String id = vo.getStringValue("id");
			if ("一级目录".equals(leveldesc)) {
				listPro.QueryDataByCondition("firstid = '" + id + "' ");
				listOut.QueryDataByCondition("firstid = '" + id + "' ");
			} else if ("二级目录".equals(leveldesc)) {
				listPro.QueryDataByCondition("secondid = '" + id + "' ");
				listOut.QueryDataByCondition("secondid = '" + id + "' ");
			} else if ("三级目录".equals(leveldesc)) {
				listPro.QueryDataByCondition("parentid = '" + id + "' ");
				listOut.QueryDataByCondition("parentid = '" + id + "' ");
			}
		}
	}

	/**
	 * 检查提纲新增
	 */
	public void OutlineInsert() {
		BillVO vo = tree.getSelectedVO();
		String leveldesc = vo.getStringValue("leveldesc");
		BillVO[] vos = tree.getSelectedParentPathVOs();

		BillCardDialog cardDialog = new BillCardDialog(this, "检查提纲新增", "CK_OUTLINE_SCY_E01", 650, 600);
		cardDialog.getBtn_save().setVisible(false);
		BillCardPanel cardPanel = cardDialog.getBillcardPanel();
		cardPanel.setEditState("INSERT");

		if ("三级目录".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vos[0] + "")); //
			cardPanel.setCompentObjectValue("secondname", new StringItemVO(vos[1] + "")); //
			cardPanel.setCompentObjectValue("thirdname", new StringItemVO(vos[2] + "")); //

			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("firstid"))); //
			cardPanel.setCompentObjectValue("secondid", new StringItemVO(vo.getStringValue("secondid"))); //
			cardPanel.setCompentObjectValue("thirdid", new StringItemVO(vo.getStringValue("thirdid"))); //
			cardPanel.setCompentObjectValue("parentid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		} else if ("二级目录".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vos[0] + "")); //
			cardPanel.setCompentObjectValue("secondname", new StringItemVO(vos[1] + "")); //

			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("firstid"))); //
			cardPanel.setCompentObjectValue("secondid", new StringItemVO(vo.getStringValue("secondid"))); //
			cardPanel.setCompentObjectValue("parentid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		} else if ("一级目录".equals(leveldesc)) {
			cardPanel.setCompentObjectValue("firstname", new StringItemVO(vo.getStringValue("listname"))); //
			cardPanel.setCompentObjectValue("firstid", new StringItemVO(vo.getStringValue("id"))); //
			cardPanel.setCompentObjectValue("belongType", new StringItemVO(vo.getStringValue("busitype"))); //
		}
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			String id = vo.getStringValue("id");
			if ("一级目录".equals(leveldesc)) {
				listPro.QueryDataByCondition("firstid = '" + id + "' ");
				listOut.QueryDataByCondition("firstid = '" + id + "' ");
			} else if ("二级目录".equals(leveldesc)) {
				listPro.QueryDataByCondition("secondid = '" + id + "' ");
				listOut.QueryDataByCondition("secondid = '" + id + "' ");
			} else if ("三级目录".equals(leveldesc)) {
				listPro.QueryDataByCondition("parentid = '" + id + "' ");
				listOut.QueryDataByCondition("parentid = '" + id + "' ");
			}
		}
	}

	/**
	 * 目录新增
	 */
	public void onItemAdd() {
		String level = "一级目录";
		//三级目录ID
		String one = null;
		String two = null;
		String three = null;
		if (tree.getSelectedNode() == null) {
			MessageBox.show(this, "请选择一个父结点进行新增操作!"); //
			return;
		}
		BillVO billVO = tree.getSelectedVO();
		BillCardPanel insertCardPanel = new BillCardPanel(tree.getTempletVO()); //
		insertCardPanel.insertRow(); // 新增一行
		insertCardPanel.setEditableByInsertInit(); //
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) tree.getSelectedNode();
		one = insertCardPanel.getRealValueAt("id");
		if (billVO != null) {
			BillVO parentVO = billVO; //
			String parent_id = ((StringItemVO) parentVO.getObject(tree.getTempletVO().getTreepk())).getStringValue(); //
			insertCardPanel.setCompentObjectValue(tree.getTempletVO().getTreeparentpk(), new StringItemVO(parent_id)); //

			if ("一级目录".equals(billVO.getStringValue("leveldesc"))) {
				level = "二级目录";
				one = billVO.getStringValue("firstid");
				two = insertCardPanel.getRealValueAt("id");
			}

			if ("二级目录".equals(billVO.getStringValue("leveldesc"))) {
				level = "三级目录";
				one = billVO.getStringValue("firstid");
				two = billVO.getStringValue("secondid");
				three = insertCardPanel.getRealValueAt("id");
			} else {

			}
		}
		BillCardDialog dialog = new BillCardDialog(tree, "新增", insertCardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //
		insertCardPanel.setCompentObjectValue("leveldesc", new StringItemVO(level)); //
		//三级目录ID
		insertCardPanel.setCompentObjectValue("firstid", new StringItemVO(one)); //
		insertCardPanel.setCompentObjectValue("secondid", new StringItemVO(two)); //
		insertCardPanel.setCompentObjectValue("thirdid", new StringItemVO(three)); //
		//条线默认
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
			if (listPro != null) {//如果选中根节点，或未选中，需要清空列表【李春娟/2016-09-29】
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
			if ("一级目录".equals(level)) {
				listPro.QueryDataByCondition("firstid = '" + id + "' ");
				listOut.QueryDataByCondition("firstid = '" + id + "' ");
				tree_insert.setEnabled(true);
				pro_add.setEnabled(false);
				outline_add.setEnabled(false);
			} else if ("二级目录".equals(level)) {
				listPro.QueryDataByCondition("secondid = '" + id + "' ");
				listOut.QueryDataByCondition("secondid = '" + id + "' ");
				tree_insert.setEnabled(true);
				pro_add.setEnabled(false);
				outline_add.setEnabled(false);
			} else if ("三级目录".equals(level)) {
				listPro.QueryDataByCondition("parentid = '" + id + "' ");
				listOut.QueryDataByCondition("parentid = '" + id + "' ");
				tree_insert.setEnabled(false); //
				pro_add.setEnabled(true);
				outline_add.setEnabled(true);
			}
		} else if ("problem".equals(getSource())) {
			if ("一级目录".equals(level)) {
				listPro.QueryDataByCondition("firstid = '" + id + "' ");
				pro_add.setEnabled(false);
			} else if ("二级目录".equals(level)) {
				listPro.QueryDataByCondition("secondid = '" + id + "' ");
				pro_add.setEnabled(false);
			} else if ("三级目录".equals(level)) {
				listPro.QueryDataByCondition("parentid = '" + id + "' ");
				pro_add.setEnabled(true);
			}
		} else if ("outline".equals(getSource())) {

			if ("一级目录".equals(level)) {
				listOut.QueryDataByCondition("firstid = '" + id + "' ");
			} else if ("二级目录".equals(level)) {
				listOut.QueryDataByCondition("secondid = '" + id + "' ");
			} else if ("三级目录".equals(level)) {
				listOut.QueryDataByCondition("parentid = '" + id + "' ");
			}
		}
	}

	/**
	 * 上移
	 */
	private void onMoveUp(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillListPanel billList = listPanel; //
		billList.moveUpRow(); //上移
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();//如果配置了树面板排序列，则用该字段排序，否则默认为seq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//以前遇到过，一个表进行行上移或下移操作并保存后，发现数据并没有变，因为以前这里没有处理如果seq为空的情况。并且处理了seqfild值非数字的情况，
			//注意第二个判断用billList.getRealValueAtModel()得到的是字符串，而billList.getValueAt()得到的是StringItemVO对象
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(billList.getRowNumberEditState(i))) {//如果是初始状态再设置更新，否则新增状态的数据执行update不能保存【李春娟/2014-10-31】
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
		billList.saveData();
	}

	/**
	 * 下移
	 */
	private void onMoveDown(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillListPanel billList = listPanel; //
		billList.moveDownRow(); //下移
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();//如果配置了树面板排序列，则用该字段排序，否则默认为seq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//以前遇到过，一个表进行行上移或下移操作并保存后，发现数据并没有变，因为以前这里没有处理如果seq为空的情况
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(billList.getRowNumberEditState(i))) {//如果是初始状态再设置更新，否则新增状态的数据执行update不能保存【李春娟/2014-10-31】
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
		billList.saveData();
	}

	/**
	 * 目录
	 * 
	 * @return
	 */
	public abstract String getTreeCode();

	/**
	 * 问题归属标准
	 * 
	 * @return
	 */
	public abstract String getListProCode();

	/**
	 * 检查提纲
	 * 
	 * @return
	 */
	public abstract String getListOutCode();

	/**
	 * 来源 目录维护、问题维护和提纲
	 * 
	 * @return
	 */
	public abstract String getSource();

	public static void main(String[] args) {

	}
}
