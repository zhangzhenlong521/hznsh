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
	public static int BUTTON_UI_STYLE_NORMAL = 0; // ��ֱͨ�ǰ�ť
	public static int BUTTON_UI_STYLE_ROUND = 1; // Բ�ǰ�ť
	public static int BUTTON_UI_STYLE_TOP_ROUND = 2;// �����Ͻ�Բ�ǰ�ť
	public static int BUTTON_UI_STYLE_TOP_LEFT_ROUND = 3; // ���Ͻ�Բ�ǰ�ť
	public static int BUTTON_UI_STYLE_TOP_CUT = 4; // �����Ͻ��нǰ�ť
	public static int BUTTON_UI_STYLE_TOP_LEFT_CUT = 5;// ���Ͻ��нǰ�ť

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
		boolean isRollover = button.isRolloverEnabled() && model.isRollover(); // ������������������һ��ͻ����Ч��
		int p = 0; // ƫ����
		int d = 10; // ֱ��
		int r = d / 2; // �뾶
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
			if (button.isFocusOwner() && !model.isRollover() && button.isRolloverEnabled()) { // ������������!!
				g2d.setColor(new Color(255, 171, 193)); // �ۺ�ɫ!!! ���Կ�����������ɫ
				g2d.drawRect(p + 1, p + 1, width - 2, height - 2);
			}
		} else if (currStyle == 1) {
			g2d.drawRoundRect(p, p, width, height, d, d);
			if (button.isFocusOwner() && !model.isRollover() && button.isRolloverEnabled()) { // ������������!!
				g2d.setColor(new Color(255, 171, 193)); // �ۺ�ɫ!!! ���Կ�����������ɫ
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
			if (button.isFocusOwner() && !model.isRollover() && button.isRolloverEnabled()) { // ������������!!
				g2d.setColor(new Color(255, 171, 193)); // �ۺ�ɫ!!! ���Կ�����������ɫ
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
