package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.mdata.BillVO;

public class BillTreeSelectionEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private DefaultMutableTreeNode node = null;

	private BillVO currVO = null;

	private BillTreeSelectionEvent(Object source) {
		super(source);
	}

	public BillTreeSelectionEvent(Object source, DefaultMutableTreeNode _node, BillVO _currVO) {
		super(source); //
		this.node = _node; //当前选中的结点..
		this.currVO = _currVO; //当前数据!!
	}

	public DefaultMutableTreeNode getCurrSelectedNode() {
		return this.node; //
	}

	public BillVO getCurrSelectedVO() {
		return this.currVO; //
	}

	public Object getSource() {
		return (BillTreePanel) super.getSource(); //返回树型面板!!
	}
}
