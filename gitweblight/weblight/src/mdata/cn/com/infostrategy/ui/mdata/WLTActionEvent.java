package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

public class WLTActionEvent extends EventObject {

	private static final long serialVersionUID = 7187138382994132713L;
	private BillPanel billPanelFrom = null; //
	private int row = -1; //
	private String itemkey = null; //

	public WLTActionEvent(Object source, BillPanel _fromPanel) {
		super(source);
		billPanelFrom = _fromPanel; //
	}

	public WLTActionEvent(Object source, BillPanel _fromPanel, int _row, String _itemkey) {
		super(source);
		billPanelFrom = _fromPanel; //
		this.row = _row; //
		this.itemkey = _itemkey; //
	}

	public WLTActionEvent(Object source, BillPanel _fromPanel, String _itemkey) {
		super(source);
		billPanelFrom = _fromPanel; //
		this.itemkey = _itemkey; //
	}

	public BillPanel getBillPanelFrom() {
		return billPanelFrom;
	}

	public BillFormatPanel getBillFormatPanelFrom() {
		if (billPanelFrom != null) {
			if (billPanelFrom instanceof BillListPanel) { //如果是列表
				BillListPanel billList = (BillListPanel) billPanelFrom; //
				return billList.getLoaderBillFormatPanel(); //
			} else if (billPanelFrom instanceof BillTreePanel) { //如果是树
				BillTreePanel billTree = (BillTreePanel) billPanelFrom; //
				return billTree.getLoaderBillFormatPanel(); //
			} else if (billPanelFrom instanceof BillCardPanel) { //如果是卡片
				BillCardPanel billCard = (BillCardPanel) billPanelFrom; //
				return billCard.getLoaderBillFormatPanel(); //
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public int getRow() {
		return row;
	}

	public String getItemkey() {
		return itemkey;
	}

}
