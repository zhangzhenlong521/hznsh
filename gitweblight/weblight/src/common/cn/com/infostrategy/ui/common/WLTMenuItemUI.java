package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cn.com.infostrategy.ui.mdata.hmui.I_MenuItemUI;

/**
 * �����а�ť�н���,���˵�û��,��ҪҲ����,��ֻҪ�ع���paintBackground()����!!!
 * @author xch
 *
 */

public class WLTMenuItemUI extends I_MenuItemUI {

	private BackGroundDrawingUtil drawutil = new BackGroundDrawingUtil(); //

	@Override
	protected void paintBackground(Graphics g, JMenuItem menuItem, Color bgColor) {
		ButtonModel model = menuItem.getModel();
		Color oldColor = g.getColor();
		int menuWidth = menuItem.getWidth();
		int menuHeight = menuItem.getHeight();
		//menuItem.getco

		if (menuItem.isOpaque()) { //��͸��!!
			if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) { //ѡ��!
				g.setColor(bgColor);
				g.fillRect(0, 0, menuWidth, menuHeight);
			} else {
				//g.setColor(menuItem.getBackground());
				//g.fillRect(0, 0, menuWidth, menuHeight);
				drawutil.draw(BackGroundDrawingUtil.HORIZONTAL_RIGHT_TO_LEFT, g, menuItem.getVisibleRect(), menuItem.getBackground(), Color.WHITE); //
			}
			g.setColor(oldColor);
		} else if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) { //���͸��!!!
			g.setColor(bgColor);
			g.fillRect(0, 0, menuWidth, menuHeight);
			g.setColor(oldColor);
		}
	}

}
