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
	private String returnMenuId = null; //���صĲ˵�ID

	public ReverseAddCorpDialog(Container _parent, String _title, int _width, int li_height) {
		super(_parent, _title, _width, li_height); ////
		initialize(); //
	}

	//��ʼ��ҳ��!!
	private void initialize() {
		corpTree = new BillTreePanel(new cn.com.infostrategy.bs.sysapp.servertmo.TMO_Pub_Menu()); //
		corpTree.getBillTreeBtnPanel().setVisible(false); //
		corpTree.setMoveUpDownBtnVisiable(false); //�������ư�ť!!!
		corpTree.queryDataByCondition(null); //��ѯ��������!
		corpTree.getJTree().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); //ֻ�ܵ�ѡ

		JPanel btn_panel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, LookAndFeel.defaultShadeColor1, false); //
		btn_panel.setLayout(new FlowLayout()); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_cancel = new WLTButton("ȡ��"); //

		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //

		btn_panel.add(btn_confirm); //
		btn_panel.add(btn_cancel); //

		this.getContentPane().add(corpTree, BorderLayout.CENTER); //
		this.getContentPane().add(btn_panel, BorderLayout.SOUTH); //
	}

	/**
	 * ���صĲ˵�ID
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
			MessageBox.show(this, "��ѡ��һ�����!"); //
			return; //
		}
		if (!MessageBox.confirm(this, "����������ý��ô?")) { //
			return; //
		}
		returnMenuId = null; //
		if (selNode.isRoot()) { //����Ǹ����
			returnMenuId = "ROOT"; //
		} else {
			BillVO selBillVO = corpTree.getSelectedVO(); //ѡ�е�VO
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
