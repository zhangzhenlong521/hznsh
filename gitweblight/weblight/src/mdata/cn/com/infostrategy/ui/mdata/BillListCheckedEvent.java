package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

import cn.com.infostrategy.to.mdata.BillVO;

/**
 * 该事件主要为勾选框模式时点击每一行的勾选框的监听
 * @author yuanjiangxiao
 * 20131029
 */

public class BillListCheckedEvent extends EventObject {
	private int checkedRow = -1;
	private BillVO currVO = null;

	public BillListCheckedEvent(Object source) {
		super(source);
	}

	public BillListCheckedEvent(int _row, BillVO _currVO, Object source) {
		super(source);
		this.checkedRow = _row;//当然选中的结点
		this.currVO = _currVO;//当前数据
	}

	public int getCheckedRow() {
		return checkedRow;
	}

	public BillVO getCurrCheckedVO() {
		return currVO;
	}

	public Object getSource() {
		return (BillListPanel) super.getSource();
	}

	public BillListPanel getBillListPanel() {
		return (BillListPanel) super.getSource();
	}
}
