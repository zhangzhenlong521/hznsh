package cn.com.infostrategy.to.mdata;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * BillTree的结点,之所以创建这个类是因为树中增加了可以勾选框选择,
 * 当勾选框选中时,会设置这里的的变量.
 * @author xch
 *
 */
public class BillTreeDefaultMutableTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 5178700134404175168L;

	private boolean checked = false; //是否选中,默认是不勾的!!

	public BillTreeDefaultMutableTreeNode() {
		super();
	}

	public BillTreeDefaultMutableTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}

	public BillTreeDefaultMutableTreeNode(Object userObject) {
		super(userObject);
	}

	/**
	 * 是否选中
	 * @return
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * 设置是否选中
	 * @param checked
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
