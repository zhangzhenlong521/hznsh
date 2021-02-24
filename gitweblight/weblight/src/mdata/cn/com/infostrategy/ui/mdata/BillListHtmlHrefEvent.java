package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

public class BillListHtmlHrefEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private String itemkey = null; //������ж�Ӧ��ģ���е�itemkey��ֵ..
	private int row = -1;
	private int col = -1;
	private boolean isshiftdown = false;

	private BillListHtmlHrefEvent(Object source) {
		super(source);
	}

	public BillListHtmlHrefEvent(String _itemkey, int _row, int _tabcol, boolean _isshiftdown, Object source) {
		super(source); //
		this.itemkey = _itemkey; //��ť�¼�
		this.row = _row; //��ؼ�,��ʱ��������ȡ����ĳһ��ȡ��,����ȡ�����е�ֵ!!!
		this.col = _tabcol; //
		this.isshiftdown = _isshiftdown; //
	}

	public Object getSource() {
		return (BillListPanel) super.getSource();
	}

	public BillListPanel getBillListPanel() {
		return (BillListPanel) getSource();
	}

	public String getItemkey() {
		return itemkey;
	}

	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public boolean isShiftDown() {
		return isshiftdown;
	}
}
