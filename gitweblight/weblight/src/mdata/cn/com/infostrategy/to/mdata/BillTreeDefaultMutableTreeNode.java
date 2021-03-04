package cn.com.infostrategy.to.mdata;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * BillTree�Ľ��,֮���Դ������������Ϊ���������˿��Թ�ѡ��ѡ��,
 * ����ѡ��ѡ��ʱ,����������ĵı���.
 * @author xch
 *
 */
public class BillTreeDefaultMutableTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 5178700134404175168L;

	private boolean checked = false; //�Ƿ�ѡ��,Ĭ���ǲ�����!!

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
	 * �Ƿ�ѡ��
	 * @return
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * �����Ƿ�ѡ��
	 * @param checked
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
