package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

import cn.com.infostrategy.to.mdata.BillVO;

/**
 * 列表双击事件
 * @author xch
 *
 */
public class BillListMouseDoubleClickedEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private int selectedRow = -1;
	private BillVO currVO = null;

	private BillListMouseDoubleClickedEvent(Object source) {
		super(source);
	}

	public BillListMouseDoubleClickedEvent(int _row, BillVO _currVO, Object source) {
		super(source); //
		this.selectedRow = _row; //当前选中的结点..
		this.currVO = _currVO; //当前数据!!
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public BillVO getCurrSelectedVO() {
		return this.currVO; //
	}

	public Object getSource() {
		return (BillListPanel) super.getSource();
	}

	public BillListPanel getBillListPanel() {
		return (BillListPanel) super.getSource();
	}

}
