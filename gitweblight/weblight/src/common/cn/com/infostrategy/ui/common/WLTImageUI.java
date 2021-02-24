package cn.com.infostrategy.ui.common;

import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicLabelUI;

public class WLTImageUI extends BasicLabelUI {
	private Icon icon = null;

	public WLTImageUI(Icon _icon) {
		this.icon = _icon;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		if(icon == null){
			return;
		}
		int iconWidth = icon.getIconWidth(); //
		int iconHeight = icon.getIconHeight(); //		

		int li_startx = 0;
		int li_starty = 0;
		while (true) {
			icon.paintIcon(c, g, li_startx, li_starty); //»­Í¼Æ¬
			if (li_startx >= c.getWidth() && li_starty >= c.getHeight()) {
				break;
			} else if (li_startx >= c.getWidth()) {
				li_startx = -iconWidth;
				li_starty += iconHeight;
			} 
			li_startx += iconWidth;
		}
	}

}
