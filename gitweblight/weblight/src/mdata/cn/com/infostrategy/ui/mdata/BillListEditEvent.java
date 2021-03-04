package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

public class BillListEditEvent extends EventObject {

	private static final long serialVersionUID = 2069205636506276127L;

	private Object newObject = null; //变化后的值..

	private int listrow = -1;

	private String itemKey = null;

	public BillListEditEvent(Object source) {
		super(source);
	}

	public BillListEditEvent(String _itemKey, Object _obj, int _row, Object source) {
		super(source);
		this.itemKey = _itemKey;
		this.newObject = _obj;
		this.listrow = _row;
	}

	public String getItemKey() {
		return itemKey;
	}

	public int getListrow() {
		return listrow;
	}

	public Object getNewObject() {
		return newObject;
	}

	public Object getSource() {
		return (BillListPanel) super.getSource(); //
	}
}
