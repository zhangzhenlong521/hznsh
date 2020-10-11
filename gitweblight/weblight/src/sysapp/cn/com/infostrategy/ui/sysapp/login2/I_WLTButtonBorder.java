package cn.com.infostrategy.ui.sysapp.login2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.AbstractBorder;

import cn.com.infostrategy.ui.common.LookAndFeel;


public class I_WLTButtonBorder extends AbstractBorder {
	public static int BUTTON_UI_STYLE_NORMAL = 0; // 普通直角按钮
	public static int BUTTON_UI_STYLE_ROUND = 1; // 圆角按钮
	public static int BUTTON_UI_STYLE_TOP_ROUND = 2;// 左右上角圆角按钮
	public static int BUTTON_UI_STYLE_TOP_LEFT_ROUND = 3; // 左上角圆角按钮
	public static int BUTTON_UI_STYLE_TOP_CUT = 4; // 左右上角切角按钮
	public static int BUTTON_UI_STYLE_TOP_LEFT_CUT = 5;// 左上角切角按钮

	private int currStyle = 0;

	public I_WLTButtonBorder(int _style) {
		currStyle = _style;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(LookAndFeel.compBorderLineColor);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AbstractButton button = (AbstractButton) c;
		ButtonModel model = button.getModel();
		boolean isRollover = button.isRolloverEnabled() && model.isRollover(); // 如果是鼠标移上来，搞一个突出的效果
		int p = 0; // 偏移量
		int d = 10; // 直径
		int r = d / 2; // 半径
		if (width > height) {
			d = 20;
		} else {
			d = 20;
		}
		if (isRollover) {
			p = 1;
			width = width - 3;
			height = height - 3;
		} else {
			width--;
			height--;
		}
		if (currStyle == 0) {
			g2d.drawRect(p, p, width, height);
			if (button.isFocusOwner() && !model.isRollover() && button.isRolloverEnabled()) { // 如果光标在上面!!
				g2d.setColor(new Color(255, 171, 193)); // 粉红色!!! 可以考虑是其他颜色
				g2d.drawRect(p + 1, p + 1, width - 2, height - 2);
			}
		} else if (currStyle == 1) {
			g2d.drawRoundRect(p, p, width, height, d, d);
			if (button.isFocusOwner() && !model.isRollover() && button.isRolloverEnabled()) { // 如果光标在上面!!
				g2d.setColor(new Color(255, 171, 193)); // 粉红色!!! 可以考虑是其他颜色
				g2d.drawRoundRect(1, 1, button.getWidth() - 3, button.getHeight() - 3, d, d);
			}
		} else if (currStyle == 2) {
			GeneralPath path = new GeneralPath();
			path.moveTo(p, r);
			float rz = (float) (Math.sqrt(2) * r / 2);
			path.quadTo(r - rz, r - rz, r, p);
			path.lineTo(width - r, p);
			path.quadTo(width - r + rz, r - rz, width, r);
			path.lineTo(width, height);
			path.lineTo(p, height);
			path.lineTo(p, r);
			g2d.draw(path);
			if (button.isFocusOwner() && !model.isRollover() && button.isRolloverEnabled()) { // 如果光标在上面!!
				g2d.setColor(new Color(255, 171, 193)); // 粉红色!!! 可以考虑是其他颜色
				GeneralPath path2 = new GeneralPath();
				p = 1;
				path2.moveTo(p, r);
				rz = (float) (Math.sqrt(2) * r / 2);
				height--;
				width--;
				path2.quadTo(r - rz, r - rz, r, p);
				path2.lineTo(width - r, p);
				path2.quadTo(width - r + rz, r - rz, width, r);
				path2.lineTo(width, height);
				path2.lineTo(p, height);
				path2.lineTo(p, r);
				g2d.draw(path2);
			}
		} else if (currStyle == 3) {
			GeneralPath path = new GeneralPath();
			path.moveTo(0, r);
			float rz = (float) (Math.sqrt(2) * r / 2);
			path.quadTo(r - rz, r - rz, r, 0);
			path.lineTo(width, 0);
			path.lineTo(width, height);
			path.lineTo(0, height);
			path.lineTo(0, r);
			g2d.draw(path);
		}
	}
}
