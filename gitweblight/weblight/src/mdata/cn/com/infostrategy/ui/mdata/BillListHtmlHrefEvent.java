package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

public class BillListHtmlHrefEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private String itemkey = null; //点击的列对应的模板中的itemkey的值..
	private int row = -1;
	private int col = -1;
	private boolean isshiftdown = false;

	private BillListHtmlHrefEvent(Object source) {
		super(source);
	}

	public BillListHtmlHrefEvent(String _itemkey, int _row, int _tabcol, boolean _isshiftdown, Object source) {
		super(source); //
		this.itemkey = _itemkey; //按钮事件
		this.row = _row; //最关键,到时就用它来取数从某一行取数,包括取隐含列的值!!!
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
