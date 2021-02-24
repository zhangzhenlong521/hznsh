package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

/**
 * 该事件主要为勾选框模式时点击表头的勾选框的事件
 * @author yuanjiangxiao
 * 20131029
 */
public class BillListCheckedAllEvent extends EventObject {

	public BillListCheckedAllEvent(Object source) {
		super(source);
	}

	public Object getSource() {
		return (BillListPanel) super.getSource();
	}

	public BillListPanel getBillListPanel() {
		return (BillListPanel) super.getSource();
	}
}
