package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

public class BillCardEditEvent extends EventObject {

	private static final long serialVersionUID = 2069205636506276127L;

	private Object newObject = null; //变化后的值..

	private String itemKey = null;

	public BillCardEditEvent(Object source) {
		super(source);
	}

	public BillCardEditEvent(String _itemKey, Object _obj, Object source) {
		super(source);
		this.itemKey = _itemKey;
		this.newObject = _obj;
	}

	public String getItemKey() {
		return itemKey;
	}

	public Object getNewObject() {
		return newObject;
	}

	public Object getSource() {
		return (BillCardPanel) super.getSource();
	}
}
