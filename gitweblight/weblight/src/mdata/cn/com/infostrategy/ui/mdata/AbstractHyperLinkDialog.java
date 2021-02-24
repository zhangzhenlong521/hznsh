package cn.com.infostrategy.ui.mdata;

import java.awt.Container;

import cn.com.infostrategy.ui.common.BillDialog;

public abstract class AbstractHyperLinkDialog extends BillDialog {

	private String itemKey,itemName = null;
	private BillPanel billPanel = null;

	public AbstractHyperLinkDialog(Container _parent) {
		super(_parent,800,500); //
	}

	/**
	 * 初始化方法!!!
	 */
	public abstract void initialize();

	public BillPanel getBillPanel() {
		return billPanel;
	}

	public void setBillPanel(BillPanel billPanel) {
		this.billPanel = billPanel;
	}

	public String getItemKey() {
		return itemKey;
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

}
