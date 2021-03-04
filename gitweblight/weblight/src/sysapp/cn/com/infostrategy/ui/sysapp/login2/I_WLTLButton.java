package cn.com.infostrategy.ui.sysapp.login2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import cn.com.infostrategy.ui.common.LookAndFeel;

public class I_WLTLButton extends JToggleButton {
	private boolean pressed = false;
	private boolean rollover = false;
	private ImageIcon icon;
	private String text;
	private ActionListener custActionListener;

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
		repaint();
	}

	public boolean isRollover() {
		return rollover;
	}

	public void setRollover(boolean rollover) {
		this.rollover = rollover;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public I_WLTLButton(String _text, ImageIcon _icon) {
		icon = _icon;
		text = _text;
		init();
	}

	private void init() {
		this.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				Cursor thisCurSor_Hand = new Cursor(Cursor.HAND_CURSOR); //
				setCursor(thisCurSor_Hand);
				rollover = true;
				I_WLTLButton.this.repaint();
			}

			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				Cursor thisCurSor_custuom = new Cursor(Cursor.DEFAULT_CURSOR); //
				setCursor(thisCurSor_custuom);
				rollover = false;
				I_WLTLButton.this.repaint();
			}

			public void mouseClicked(MouseEvent e) {
				doclick(e);
			}
		});
		this.setOpaque(false);
	}

	private void doclick(MouseEvent e) {
		ActionEvent action = new ActionEvent(this, 0, null);
		if (custActionListener != null) {
			custActionListener.actionPerformed(action);
		}
		for (int i = 0; i < list.size(); i++) {
			((ActionListener) list.get(i)).actionPerformed(action);
		}
	}

	List list = new ArrayList();

	public void addActionListener(ActionListener l) {
		list.add(l);
	}

	public void addCustActionListener(ActionListener l) {
		custActionListener = l;
	}

	Color fontFocusColor = new Color(5, 112, 186);
	Color lostFocusColor = new Color(65, 65, 65, 120);
	Color borderColor = new Color(4, 163, 255);

	public void paint(Graphics g) {
//		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(icon.getImage(), 0, 0, null);

		if (this.isRollover()) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			boolean isRollover = this.isRolloverEnabled() && model.isRollover(); // 如果是鼠标移上来，搞一个突出的效果
			g2d.scale(1.0, -1.0);
			g2d.drawImage(icon.getImage(), 0, -2 * icon.getIconHeight(), null);
			g2d.scale(1.0, -1.0);
			g2d.translate(0, icon.getIconHeight());
			GradientPaint paint = new GradientPaint(0, 0, new Color(1f, 1f, 1f, 0.1f), 0, icon.getIconHeight() / 3, new Color(1f, 1f, 1f, 0.8f));
			g2d.setPaint(paint);
			g2d.fillRoundRect(2, icon.getIconHeight() - 15, icon.getIconWidth() - 4, 12, 8, 8);
			g2d.setColor(Color.RED);
			if (this.getText() != null) {
				g2d.setFont(LookAndFeel.font_b);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g2d.drawString(this.getText(), 5, this.getHeight() - icon.getIconHeight() - 5);
			}
		} else {
//			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			GradientPaint paint = new GradientPaint(0, 0, new Color(1f, 1f, 1f, isPressed() ? 0.8f : 0.1f), 0, icon.getIconHeight() / 3, new Color(1f, 1f, 1f, isPressed() ? 0.9f : 0.4f));
//			g2d.setPaint(paint);
//			g2d.fillRoundRect(2, icon.getIconHeight() - 15, icon.getIconWidth() - 4, 12, 8, 8);
//			g2d.setPaint(isPressed() ? fontFocusColor : Color.WHITE);
//			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
//			g2d.drawString(this.getText(), 6, icon.getIconHeight() - 4);
		}
		if (isPressed()) { //如果按下状态
			g2d.setColor(Color.ORANGE);
			g2d.setStroke(new BasicStroke(2));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawRoundRect(1, 1, this.getWidth() - 2, this.getHeight() - 2, 10, 10);
		}
	}

}
