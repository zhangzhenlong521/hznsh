package cn.com.infostrategy.ui.report;

import java.util.EventObject;

import cn.com.infostrategy.to.mdata.BillCellItemVO;

/**
 * �����BillCellPanel�ϵĳ����ӵ��¼�!!!
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
		this.row = _row; //����ǵڼ���
		this.col = _col; //����ǵڼ���
		this.cellItemVO = _cellItemVO; //�������!!
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

	//ȡ�õ���ĸ����е�����!!
	public String getCellItemValue() {
		if (cellItemVO == null) {
			return null;
		}
		return cellItemVO.getCellvalue(); //
	}

	//ȡ�õ���ĸ����е�key
	public String getCellItemKey() {
		if (cellItemVO == null) {
			return null;
		}
		return cellItemVO.getCellkey(); //
	}

	//�ж���������Ƿ��ǳ����ӵ�Ч��,��Ϊ��ʱֻ���г�����������!!
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
