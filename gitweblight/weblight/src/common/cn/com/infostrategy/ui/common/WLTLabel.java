package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JLabel;

/**
 * 可以像JTextArea一样折行的JLabel,非常NB
 * @author xch
 *
 */
public class WLTLabel extends JLabel {

	private static final long serialVersionUID = -7716976203120311207L;

	public WLTLabel() {
		super(); //
		setUI(new WLTLabelUI()); //

	}

	public WLTLabel(String _text) {
		super(_text); //
		setUI(new WLTLabelUI()); //
	}

	public WLTLabel(String _text, int _directType) {
		super(_text); //
		setUI(new WLTLabelUI(_directType)); //
	}

	public void updateUI() {
		repaint(); //
	}

	/**
	 * 设置字符串中某些特殊字符的颜色!! key中特殊字,value是指定的颜色!!!该功能可以舍弃html的功能!!!
	 * 以后扩展可以指定字体等!!!
	 */
	public void addStrItemColor(String _key, Color _color) {
		if (this.getClientProperty("TextItemColor") == null) {
			HashMap colorMap = new HashMap(); //
			colorMap.put(_key.toLowerCase(), _color); //
			this.putClientProperty("TextItemColor", colorMap); //
		} else {
			HashMap colorMap = (HashMap) this.getClientProperty("TextItemColor"); //
			colorMap.put(_key.toLowerCase(), _color); //
		}
	}
}
