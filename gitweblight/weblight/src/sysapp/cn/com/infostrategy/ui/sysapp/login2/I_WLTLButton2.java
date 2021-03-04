package cn.com.infostrategy.ui.sysapp.login2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;

public class I_WLTLButton2 extends JComponent {
	private boolean pressed = false;
	private boolean rollover = false;
	private ImageIcon icon;
	private String text;
	private ActionListener custActionListener;
	private TBUtil tbutil = TBUtil.getTBUtil();

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

	public I_WLTLButton2(String _text, ImageIcon _icon) {
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
				I_WLTLButton2.this.repaint();
			}

			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				Cursor thisCurSor_custuom = new Cursor(Cursor.DEFAULT_CURSOR); //
				setCursor(thisCurSor_custuom);
				rollover = false;
				I_WLTLButton2.this.repaint();
			}

			public void mouseReleased(MouseEvent e) {
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

	Color fontFocusColor = new Color(2, 112, 186);
	Color showdowColor_start = new Color(244, 244, 244, 20);
	Color showdowColor_end = new Color(244, 244, 244, 220);
	Color lostFocusColor = new Color(220, 220, 220, 220);
	Color borderColor = new Color(4, 163, 255);
	public Font font_big = new Font("新宋体", Font.PLAIN, 13); //大字体,民生客户喜欢大的字!!
	public static int displace_h = 5;//水平位移
	public static int displace_v = 5;//垂直位移
	private int promptNum = 0;
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g.drawImage(icon.getImage(), displace_h, displace_v, null);
		if (isPressed()) {
			g2d.scale(1.0, -1.0);
			g2d.drawImage(icon.getImage(), displace_h, -2 * icon.getIconHeight() -1, null);
			g2d.scale(1.0, -1.0);
			g2d.translate(displace_h, icon.getIconHeight() + displace_v);
			GradientPaint paint1 = new GradientPaint(0, 0, new Color(1f, 1f, 1f, 0.1f), 0, icon.getIconHeight() / 5, new Color(1f, 1f, 1f, 0.8f));
			g2d.setPaint(paint1);
			g2d.fillRoundRect(2, icon.getIconHeight() - 15, icon.getIconWidth() - 2, 12, 8, 8);
			g2d.setColor(Color.RED);
		} else {
			g2d.translate(displace_h, icon.getIconHeight() + displace_v);
		}

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.translate(0, -displace_v);
		if (!isPressed()) { //如果按下状态
			GradientPaint paint = new GradientPaint(0, 0, showdowColor_start, 0, icon.getIconHeight() / 5, showdowColor_end);
			g2d.setPaint(paint);
			g2d.fillRoundRect(1, 0, icon.getIconWidth() - 2, icon.getIconHeight() - displace_v, 25, 25);
		} else {
			GradientPaint paint = new GradientPaint(0, 0, showdowColor_start, 0, icon.getIconHeight() / 5, showdowColor_end);
			g2d.setPaint(paint);
			g2d.fillRoundRect(1, 2, icon.getIconWidth() - 2, icon.getIconHeight() - 2, 25, 25);
		}
		g2d.setPaint(fontFocusColor);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2d.setFont(font_big);
		int text_x = (icon.getIconWidth() - tbutil.getStrWidth(this.getText())) / 2 - 2;
		g2d.translate(0, 17);
		g2d.drawString(this.getText(), text_x, 0);
		g2d.translate(0, -17);

		if (!isPressed()) { //如果按下状态
			g2d.translate(0, -icon.getIconHeight() + displace_v);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			RadialGradientPaint paint = new RadialGradientPaint(getWidth() / 2, icon.getIconHeight() / 2, getWidth() * 0.95f, new float[] { 0.0f, 1.0f }, new Color[] { lostFocusColor, lostFocusColor.darker() });
			g2d.setPaint(paint);
			g2d.fillRoundRect(1, 0, icon.getIconWidth() - 2, icon.getIconHeight() - 2, 12, 12);
		} else {
			g2d.translate(0, -icon.getIconHeight() + displace_v);
			int s_x1 = (icon.getIconWidth() - displace_h - 2) / 2 - 8;
			int s_y1 = icon.getIconHeight() - 3;
			g2d.setColor(Color.orange);
			g2d.translate(displace_h, 0);
			g2d.fillPolygon(new int[] { s_x1, s_x1 + 8, s_x1 + 16 }, new int[] { s_y1, s_y1 - 8, s_y1 }, 3);
			g2d.translate(-displace_h, 0);
		}
		paintNum(g2d);
	}
	private Color red_1 = new Color(200,51,55); //提示标记符号背景颜色，淡红色
	private Stroke stroke = new BasicStroke(2);
	private Font font = new Font("Dialog",Font.BOLD,12);
	private void paintNum(Graphics2D g2d) {
		if(promptNum<=0){
			return;
		}
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		RoundRectangle2D rr2d = new RoundRectangle2D.Float();
		g2d.setPaint(red_1);
		g2d.translate(icon.getIconWidth() - 17, -5);
		rr2d.setRoundRect(0, 0, 24, 14, 10, 10);
		g2d.fill(rr2d);
		g2d.setColor(Color.WHITE);
		g2d.setStroke(stroke);
		g2d.draw(rr2d);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g2d.setFont(font);
		int x = 9;
		if(promptNum>10 && promptNum<100){
			x-=3;
			g2d.drawString(promptNum+"", x, 12);
		}else if(promptNum>=100){
			x-=5;
			g2d.drawString("99+", x, 12);
		}else{
			g2d.drawString(promptNum+"", x, 12);	
		}
		
		
		g2d.setFont(LookAndFeel.font_b);
		g2d.translate(-(icon.getIconWidth() - 17), 5);
	}
	/**
	 * 
	* @Title: setPromptNum 
	* @Description: 设置按钮提示数字，类似于手机的推送提醒
	* @param @param _num
	* @return void
	* @throws
	 */
	public void setPromptNum(int _num){
		promptNum = _num;
		repaint();
	}

}