package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.mdata.BillVO;

public class BillTreeMoveEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private DefaultMutableTreeNode node = null;

	private BillVO currVO = null;

	private BillTreeMoveEvent(Object source) {
		super(source);
	}

	public BillTreeMoveEvent(Object source, DefaultMutableTreeNode _node, BillVO _currVO) {
		super(source); //
		this.node = _node; //��ǰѡ�еĽ��..
		this.currVO = _currVO; //��ǰ����!!
	}

	public DefaultMutableTreeNode getMovedNode() {
		return this.node; //
	}

	public BillVO getMovedBillVO() {
		return this.currVO; //
	}

	public Object getSource() {
		return (BillTreePanel) super.getSource(); //�����������!!
	}
}
