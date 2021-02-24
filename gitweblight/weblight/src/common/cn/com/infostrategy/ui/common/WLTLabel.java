package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JLabel;

/**
 * ������JTextAreaһ�����е�JLabel,�ǳ�NB
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
	 * �����ַ�����ĳЩ�����ַ�����ɫ!! key��������,value��ָ������ɫ!!!�ù��ܿ�������html�Ĺ���!!!
	 * �Ժ���չ����ָ�������!!!
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
