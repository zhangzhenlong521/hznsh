package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicViewportUI;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_ViewportUI 
 * @Description: 自定义JscrollPanel容器的UI
 * @author haoming
 * @date Mar 20, 2013 3:01:45 PM
 *  
*/
public class I_ViewportUI extends BasicViewportUI {
	public static ComponentUI createUI(JComponent c) {
		c.setBorder(BorderFactory.createEmptyBorder());
		return new I_ViewportUI();
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.RED);
		g2d.fillRoundRect(c.getX(), c.getY(), c.getWidth(), c.getHeight(), 15, 15);
		g2d.dispose();
		//		super.paint(g, c);
	}
}
