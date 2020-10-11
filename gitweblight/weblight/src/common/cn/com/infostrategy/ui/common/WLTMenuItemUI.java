package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cn.com.infostrategy.ui.mdata.hmui.I_MenuItemUI;

/**
 * 以闪有按钮有渐变,但菜单没有,需要也搞下,它只要重构下paintBackground()方法!!!
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

		if (menuItem.isOpaque()) { //不透明!!
			if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) { //选中!
				g.setColor(bgColor);
				g.fillRect(0, 0, menuWidth, menuHeight);
			} else {
				//g.setColor(menuItem.getBackground());
				//g.fillRect(0, 0, menuWidth, menuHeight);
				drawutil.draw(BackGroundDrawingUtil.HORIZONTAL_RIGHT_TO_LEFT, g, menuItem.getVisibleRect(), menuItem.getBackground(), Color.WHITE); //
			}
			g.setColor(oldColor);
		} else if (model.isArmed() || (menuItem instanceof JMenu && model.isSelected())) { //如果透明!!!
			g.setColor(bgColor);
			g.fillRect(0, 0, menuWidth, menuHeight);
			g.setColor(oldColor);
		}
	}

}
