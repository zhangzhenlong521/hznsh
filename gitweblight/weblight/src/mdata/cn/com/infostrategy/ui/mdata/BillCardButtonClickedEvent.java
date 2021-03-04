package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

import cn.com.infostrategy.ui.common.WLTButton;

public class BillCardButtonClickedEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private String btnname = null;
	private WLTButton btn = null;

	private BillCardButtonClickedEvent(Object source) {
		super(source);
	}

	public BillCardButtonClickedEvent(String _btnname, WLTButton _btn, Object source) {
		super(source); //
		this.btnname = _btnname; //°´Å¥ÊÂ¼þ
		this.btn = _btn; //
	}

	public Object getSource() {
		return (BillCardPanel) super.getSource();
	}

	public String getClickedButtonName() {
		return btnname;
	}

	public WLTButton getClickedButton() {
		return btn;
	}

	public BillCardPanel getBilCardPanel() {
		return (BillCardPanel) getSource();
	}
}
