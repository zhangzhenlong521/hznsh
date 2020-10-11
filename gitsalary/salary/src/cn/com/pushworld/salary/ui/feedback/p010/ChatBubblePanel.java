package cn.com.pushworld.salary.ui.feedback.p010;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;

/**
 * 聊天气泡面板容器
 * @author haoming
 * create by 2013-12-31
 */
public class ChatBubblePanel extends JComponent {
	public static final int DIRECTION_TYPE_LEFT = 0;
	public static final int DIRECTION_TYPE_RIGHT = 1;
	private ClassicChatBuffleUI ui = null;

	public ChatBubblePanel(int _directType) {
		ui = new ClassicChatBuffleUI(_directType);
		this.setUI(ui);
	}

	public void updataUI() {
		ui.repaint();
		repaint();
	}
}

class ClassicChatBuffleUI extends ComponentUI {
	//传入方向类型
	private int direction;

	public ClassicChatBuffleUI(int _directionType) {
		direction = _directionType;
	}

	public void installUI(JComponent jcomponent) {
		if (direction == ChatBubblePanel.DIRECTION_TYPE_LEFT) {
			jcomponent.setBorder(BorderFactory.createEmptyBorder(3, 15, 5, 5));
		} else if (direction == ChatBubblePanel.DIRECTION_TYPE_RIGHT) {
			jcomponent.setBorder(BorderFactory.createEmptyBorder(3, 5, 5, 15));
		}
	}

	BufferedImage bi;

	public void repaint() {
		bi = null;
	}

	@Override
	public void paint(Graphics g, JComponent jcomponent) {
		int p_width = (int) jcomponent.getPreferredSize().getWidth();
		int p_height = (int) jcomponent.getPreferredSize().getHeight();
		double width = p_width - 3;
		double height = p_height - 3;
		double r = 10;
		double start_x = 8;
		if (bi == null) {
			bi = new BufferedImage(p_width, p_height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) bi.getGraphics();
			float inner = 1;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Color dark = new Color(115, 115, 115);
			g2d.setColor(dark);
			GeneralPath path = new GeneralPath();
			path.moveTo(start_x, r); //定位起始点
			path.quadTo(start_x, inner, start_x + r, inner); //先画左上角
			path.lineTo(width - r, inner);
			path.quadTo(width, inner, width, r); //右上角
			path.lineTo(width, height - r);
			path.quadTo(width, height, width - r, height);//右下脚
			path.lineTo(start_x + r + 6, height);
			path.quadTo(start_x + 4, height, start_x + 4, height - 4);
			path.quadTo(start_x, height, inner, height);
			path.quadTo(start_x / 2 + 2, height - 2, start_x, height - 10);
			path.lineTo(start_x, r);
			g2d.draw(path); //180, 210, 210, 80
			g2d.setColor(jcomponent.getBackground());
			g2d.fill(path);
			//加阴影
			g2d.setColor(new Color(115, 115, 115, 40));
			width++;
			height++;
			start_x--;
			inner = 0;
			path = new GeneralPath();
			g2d.setStroke(new BasicStroke(2.5f));
			path.moveTo(start_x, r); //定位起始点
			path.quadTo(start_x, inner, start_x + r, inner); //先画左上角
			path.lineTo(width - r, inner);
			path.quadTo(width - inner, inner, width, r); //右上角
			path.lineTo(width - inner, height - r);
			path.quadTo(width - inner, height, width - r, height);//右下脚
			path.lineTo(start_x + r + 6, height);
			path.quadTo(start_x + 4, height, start_x + 4, height - 4);
			path.quadTo(start_x, height, inner, height);
			path.quadTo(start_x / 2 + 2, height - 2, start_x, height - 10);
			path.lineTo(start_x, r);
			g2d.draw(path);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2d.dispose();
		}
		Graphics2D g2d = (Graphics2D) g.create();
		if (direction == 1) {
			g2d.scale(-1, 1);
			g2d.translate(-width - 3, 0);
		}
		g2d.drawImage(bi, 0, 0, null);
		g2d.dispose();
	}
}

class ChatBuffleBorder extends AbstractBorder {
	Insets inset = new Insets(10, 3, 3, 3);

	public ChatBuffleBorder() {
		super();
	}

	public ChatBuffleBorder(Insets _insets) {
		inset = _insets;
	}

	public Insets getBorderInsets(Component c) {
		return inset;
	};
}
