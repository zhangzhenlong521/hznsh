package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

import sun.swing.SwingUtilities2;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_MenuItemUI 
 * @Description: 
 * @author haoming
 * 
 * @date Apr 22, 2013 2:17:06 PM
 *  
*/ 
public class I_MenuItemUI extends BasicMenuItemUI {
	  public static ComponentUI createUI(JComponent c) {
	        return new I_MenuItemUI();
	    }
	@Override
	protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
		if(I_LookAndFeel.windows_word_antialias){
			ButtonModel model = menuItem.getModel();
			FontMetrics fm = SwingUtilities2.getFontMetrics(menuItem, g);
			int mnemIndex = menuItem.getDisplayedMnemonicIndex();

			if(!model.isEnabled()) {
			    if ( UIManager.get("MenuItem.disabledForeground") instanceof Color ) {
				g.setColor( UIManager.getColor("MenuItem.disabledForeground") );
				
				LAFUtil.I_SwingUtilities2.swingUtil.drawStringUnderlineCharAt(menuItem, g,text,
		                          mnemIndex, textRect.x,  textRect.y + fm.getAscent());
			    } else {
				g.setColor(menuItem.getBackground().brighter());
				LAFUtil.I_SwingUtilities2.swingUtil.drawStringUnderlineCharAt(menuItem, g, text,
		                           mnemIndex, textRect.x, textRect.y + fm.getAscent());
				g.setColor(menuItem.getBackground().darker());
				LAFUtil.I_SwingUtilities2.swingUtil.drawStringUnderlineCharAt(menuItem, g,text,
		                           mnemIndex,  textRect.x - 1, textRect.y +
		                           fm.getAscent() - 1);
			    }
			} else {
			    if (model.isArmed()|| (menuItem instanceof JMenu && model.isSelected())) {
				g.setColor(selectionForeground); 
			    }
			    LAFUtil.I_SwingUtilities2.swingUtil.drawStringUnderlineCharAt(menuItem, g,text,
		                           mnemIndex, textRect.x, textRect.y + fm.getAscent());
			}
		}else{
			super.paintText(g, menuItem, textRect, text);
		}
	}
}
