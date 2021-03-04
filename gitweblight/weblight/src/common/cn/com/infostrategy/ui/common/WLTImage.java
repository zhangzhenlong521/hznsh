package cn.com.infostrategy.ui.common;

import javax.swing.Icon;
import javax.swing.JLabel;

public class WLTImage extends JLabel{
	public WLTImage(Icon _icon) {
		super(); //
		setUI(new WLTImageUI(_icon)); //

	}

	public WLTImage(String _text,Icon _icon) {
		super(_text); //
		setUI(new WLTImageUI(_icon)); //
	}

	public void updateUI() {
		repaint(); //
	}

}
