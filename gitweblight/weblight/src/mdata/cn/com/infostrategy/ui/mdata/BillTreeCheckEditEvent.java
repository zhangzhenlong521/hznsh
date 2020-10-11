package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

import cn.com.infostrategy.to.mdata.BillTreeDefaultMutableTreeNode;

public class BillTreeCheckEditEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private BillTreeDefaultMutableTreeNode node = null;
	private boolean checked = false; //

	private BillTreeCheckEditEvent(Object source) {
		super(source);
	}

	public BillTreeCheckEditEvent(BillTreeDefaultMutableTreeNode _node, boolean _checked, Object source) {
		super(source); //
		this.node = _node; //
		this.checked = _checked; //
	}

	public Object getSource() {
		return super.getSource();
	}

	public BillTreePanel getBillTreePanel() {
		return (BillTreePanel) super.getSource();
	}

	public BillTreeDefaultMutableTreeNode getEditNode() {
		return node;
	}

	public boolean isChecked() {
		return checked;
	}

}
