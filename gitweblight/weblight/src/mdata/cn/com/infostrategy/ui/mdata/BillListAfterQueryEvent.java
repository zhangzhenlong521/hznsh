package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

public class BillListAfterQueryEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	public BillListAfterQueryEvent(Object source) {
		super(source); //
	}

	public Object getSource() {
		return (BillListPanel) super.getSource();
	}

}
