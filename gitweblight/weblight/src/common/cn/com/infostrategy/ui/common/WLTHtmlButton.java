package cn.com.infostrategy.ui.common;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.metal.MetalButtonUI;

/**
 * 首页任务栏目中的【刷新】与【更多】按钮,可以仿照Html效果，且移上去时会呈现透明效果!!!
 * @author xch
 *
 */
public class WLTHtmlButton extends JButton implements MouseListener {

	private static final long serialVersionUID = 1L;
	float alpha = 1.0f;

	public WLTHtmlButton(String _text) {
		super("<html><u>" + _text + "</u></html>"); //
		this.addMouseListener(this); //
		this.setContentAreaFilled(false);
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
	
	public void updateUI() {
		   setUI((ButtonUI)new MetalButtonUI()); //重设ui。
	}
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha); //透明效果!!!
		g2d.setComposite(alphaComp);
		super.paint(g2d); //
	}

	public void setAlpha(float a) {
		alpha = a;
	}

	public void setEnabled(boolean _isEnabled) {
		super.setEnabled(_isEnabled);
		alpha = _isEnabled ? 1.0f : 0.5f;
	}

	public void mouseEntered(MouseEvent e) {
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		if (this.isEnabled()) {
			this.setAlpha(0.5f); //设置透明
		}
	}

	public void mouseExited(MouseEvent e) {
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		if (this.isEnabled()) {
			this.setAlpha(1.0f); //设置透明!!!
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {
	}
}
