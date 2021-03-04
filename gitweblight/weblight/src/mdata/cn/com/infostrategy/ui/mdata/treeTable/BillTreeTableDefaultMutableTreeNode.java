package cn.com.infostrategy.ui.mdata.treeTable;

import javax.swing.tree.DefaultMutableTreeNode;

public class BillTreeTableDefaultMutableTreeNode extends DefaultMutableTreeNode {
	private int height = 30;
	private boolean ischecked = false; //勾选状态

	public boolean isChecked() {
		return ischecked;
	}

	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}

	public BillTreeTableDefaultMutableTreeNode(Object _obj) {
		super(_obj);
	}

	//设置节点高度
	public synchronized void setNodeHeight(int _height) {
		if (_height > 0) {
			height = _height;
		}
	}

	public int getNodeHeight() {
		return height;
	}
}
