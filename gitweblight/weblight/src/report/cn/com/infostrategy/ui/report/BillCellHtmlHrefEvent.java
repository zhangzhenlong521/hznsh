package cn.com.infostrategy.ui.report;

import java.util.EventObject;

import cn.com.infostrategy.to.mdata.BillCellItemVO;

/**
 * 点击了BillCellPanel上的超链接的事件!!!
 * @author xch
 *
 */
public class BillCellHtmlHrefEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private int row = -1;
	private int col = -1;
	private BillCellItemVO cellItemVO = null; //
	private boolean isCtrlDown = false; //
	private boolean isShiftDown = false; //
	private boolean isAltDown = false; //

	private BillCellHtmlHrefEvent(Object source) {
		super(source);
	}

	public BillCellHtmlHrefEvent(int _row, int _col, BillCellItemVO _cellItemVO, Object _source) {
		super(_source); //
		this.row = _row; //点的是第几行
		this.col = _col; //点的是第几列
		this.cellItemVO = _cellItemVO; //对象绑定上!!
	}

	public Object getSource() {
		return (BillCellPanel) super.getSource();
	}

	public BillCellPanel getBillCellPanel() {
		return (BillCellPanel) getSource();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public BillCellItemVO getCellItemVO() {
		return cellItemVO;
	}

	//取得点击的格子中的数据!!
	public String getCellItemValue() {
		if (cellItemVO == null) {
			return null;
		}
		return cellItemVO.getCellvalue(); //
	}

	//取得点击的格子中的key
	public String getCellItemKey() {
		if (cellItemVO == null) {
			return null;
		}
		return cellItemVO.getCellkey(); //
	}

	//判断这个格子是否是超链接的效果,因为有时只对有超链接做处理!!
	public boolean isCellItemHtmlHref() {
		if (cellItemVO == null) {
			return false;
		}
		return "Y".equals(cellItemVO.getIshtmlhref()); //
	}

	public boolean isCtrlDown() {
		return isCtrlDown;
	}

	public void setCtrlDown(boolean isCtrlDown) {
		this.isCtrlDown = isCtrlDown;
	}

	public boolean isShiftDown() {
		return isShiftDown;
	}

	public void setShiftDown(boolean isShiftDown) {
		this.isShiftDown = isShiftDown;
	}

	public boolean isAltDown() {
		return isAltDown;
	}

	public void setAltDown(boolean isAltDown) {
		this.isAltDown = isAltDown;
	}

}
