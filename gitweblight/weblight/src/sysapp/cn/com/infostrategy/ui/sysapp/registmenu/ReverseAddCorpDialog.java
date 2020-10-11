package cn.com.infostrategy.ui.sysapp.registmenu;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

public class ReverseAddCorpDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 7996463950623022627L;

	private BillTreePanel corpTree = null; //
	private JButton btn_confirm, btn_cancel; //
	private String returnMenuId = null; //返回的菜单ID

	public ReverseAddCorpDialog(Container _parent, String _title, int _width, int li_height) {
		super(_parent, _title, _width, li_height); ////
		initialize(); //
	}

	//初始化页面!!
	private void initialize() {
		corpTree = new BillTreePanel(new cn.com.infostrategy.bs.sysapp.servertmo.TMO_Pub_Menu()); //
		corpTree.getBillTreeBtnPanel().setVisible(false); //
		corpTree.setMoveUpDownBtnVisiable(false); //上移下移按钮!!!
		corpTree.queryDataByCondition(null); //查询所有数据!
		corpTree.getJTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //只能单选

		JPanel btn_panel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, LookAndFeel.defaultShadeColor1, false); //
		btn_panel.setLayout(new FlowLayout()); //
		btn_confirm = new WLTButton("确定"); //
		btn_cancel = new WLTButton("取消"); //

		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //

		btn_panel.add(btn_confirm); //
		btn_panel.add(btn_cancel); //

		this.getContentPane().add(corpTree, BorderLayout.CENTER); //
		this.getContentPane().add(btn_panel, BorderLayout.SOUTH); //
	}

	/**
	 * 返回的菜单ID
	 * @return
	 */
	public String getRetrunMenuId() {
		return returnMenuId; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) { //
			onCancel(); //
		}
	}

	private void onConfirm() {
		DefaultMutableTreeNode selNode = corpTree.getSelectedNode(); //
		if (selNode == null) {
			MessageBox.show(this, "请选中一个结点!"); //
			return; //
		}
		if (!MessageBox.confirm(this, "您真的想加入该结点么?")) { //
			return; //
		}
		returnMenuId = null; //
		if (selNode.isRoot()) { //如果是根结点
			returnMenuId = "ROOT"; //
		} else {
			BillVO selBillVO = corpTree.getSelectedVO(); //选中的VO
			returnMenuId = selBillVO.getStringValue("id"); //
		}
		this.setCloseType(1); //
		this.dispose(); //
	}

	private void onCancel() {
		this.setCloseType(2); //
		this.dispose(); //
	}

}
