package cn.com.infostrategy.ui.sysapp.login2;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;


public class I_WLTButtonUI extends BasicButtonUI {
	private int currStyle = 0;

	public I_WLTButtonUI(int _style) {
		currStyle = _style;
	}

	public void paint(Graphics g, JComponent c) {
		AbstractButton button = (AbstractButton) c;
		ButtonModel model = button.getModel();
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int p = 0; // 偏移量
		int d = 20; // 直径
		int r = d / 2; // 半径
		float width = c.getWidth();
		float height = c.getHeight();
		if (model.isArmed() || model.isPressed()) { // 按下去的状态!!
			g2d.setColor(button.getBackground().darker());
		} else {
			GradientPaint paint = new GradientPaint(0, 0, button.getBackground(), 0, (float) g.getClipBounds().getHeight(), Color.WHITE);
			g2d.setPaint(paint);
		}
		if (currStyle == 0) {
			g2d.fillRect(p, p, c.getWidth(), c.getHeight());
		} else if (currStyle == 1) {
			g2d.fillRoundRect(0, 0, c.getWidth() - 1, c.getHeight() - 1, 20, 20);
		} else if (currStyle == 2) {
			GeneralPath path = new GeneralPath();
			path.moveTo(0, r);
			float rz = (float) (Math.sqrt(2) * r / 2);
			path.quadTo(r - rz, r - rz, r, 0);
			path.lineTo(width - r, 0);
			path.quadTo(width - r + rz, r - rz, width, r);
			path.lineTo(width, height);
			path.lineTo(0, height);
			path.lineTo(0, r);
			g2d.fill(path);
		} else if (currStyle == 3) {
			GeneralPath path = new GeneralPath();
			path.moveTo(0, r);
			float rz = (float) (Math.sqrt(2) * r / 2);
			path.quadTo(r - rz, r - rz, r, 0);
			path.lineTo(width, 0);
			path.lineTo(width, height);
			path.lineTo(0, height);
			path.lineTo(0, r);
			g2d.fill(path);
		}
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		super.paint(g, c); // 父类画图!!!
	}
}
