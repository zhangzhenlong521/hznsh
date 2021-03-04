package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

import cn.com.infostrategy.ui.common.WLTButton;

public class BillListButtonClickedEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private BillCardPanel cardPanel = null; //
	private String btnname = null;
	private WLTButton btn = null;

	private BillListButtonClickedEvent(Object source) {
		super(source);
	}

	public BillListButtonClickedEvent(String _btnname, WLTButton _btn, Object source) {
		super(source); //
		this.btnname = _btnname; //按钮事件
		this.btn = _btn; //
	}

	public BillListButtonClickedEvent(String _btnname, WLTButton _btn, BillCardPanel _cardPanel, Object source) {
		super(source); //
		this.btnname = _btnname; //按钮事件
		this.btn = _btn; //
		this.cardPanel = _cardPanel;  //
	}

	public Object getSource() {
		return (BillListPanel) super.getSource();
	}

	public String getClickedButtonName() {
		return btnname;
	}

	public WLTButton getClickedButton() {
		return btn;
	}

	public BillListPanel getBillListPanel() {
		return (BillListPanel) getSource();
	}

	public BillCardPanel getCardPanel() {
		return cardPanel;
	}
}
