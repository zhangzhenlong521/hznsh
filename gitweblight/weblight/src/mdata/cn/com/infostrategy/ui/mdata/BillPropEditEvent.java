package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

public class BillPropEditEvent extends EventObject {

	private static final long serialVersionUID = 2069205636506276127L;

	private Object beanInstance = null; //变化后的值..

	private String itemKey = null;

	public BillPropEditEvent(Object source) {
		super(source);
	}

	public BillPropEditEvent(String _itemKey, Object _obj, Object source) {
		super(source);
		this.itemKey = _itemKey; //
		this.beanInstance = _obj; //
	}

	public String getItemKey() {
		return itemKey;
	}

	public Object getBeanInstance() {
		return beanInstance;
	}

	public Object getSource() {
		return (BillPropPanel) super.getSource();
	}
}
