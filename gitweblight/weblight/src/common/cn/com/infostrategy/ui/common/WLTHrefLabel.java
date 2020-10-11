package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicLabelUI;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 一种JLabel,但能呈现Html超链的效果,即鼠标移上去是手形图标!!! 并且自动有下划线出现!!!
 * @author xch
 *
 */
public class WLTHrefLabel extends JLabel implements MouseListener {

	private Color initForeGround = null; //一开始的字的颜色!因为在做鼠标移上去后需要知道原来的颜色!!
	private Vector v_clickListeners = new Vector(); //
	private Vector v_mouseEnterListeners = new Vector(); //鼠标进入的监听
	private Vector v_mouseExitListeners = new Vector(); //鼠标移开的监听

	public WLTHrefLabel() {
		super();
		initialize(); //
	}

	public WLTHrefLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		initialize(); //
	}

	public WLTHrefLabel(Icon image) {
		super(image);
		initialize(); //
	}

	public WLTHrefLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		initialize(); //
	}

	public WLTHrefLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		initialize(); //
	}

	public WLTHrefLabel(String text) {
		super(text);
		initialize(); //
	}

	private void initialize() {
		this.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		this.setUI(new MyLabelUI()); //
		this.addMouseListener(this); //
	}

	class MyLabelUI extends BasicLabelUI {

		TBUtil tbUtil = new TBUtil(); //

		/**
		 * 重构绘图...
		 */
		public void paint(Graphics g, JComponent c) {
			if (initForeGround == null) {
				initForeGround = c.getForeground(); //先记录下原来的颜色!!
			}
			super.paint(g, c);
			JLabel label = (JLabel) c; //
			Boolean bo_isHand = (Boolean) c.getClientProperty("ishand"); //是否是手形!!!!
			if (bo_isHand != null && bo_isHand.booleanValue()) {
				g.setColor(Color.RED); //
				int li_strwidth = tbUtil.getStrWidth(c.getFont(), label.getText()); //
				int li_text_startX = 0; //
				g.drawLine(li_text_startX + 2, 19, li_text_startX + li_strwidth - 2, 19); //
			}
		}
	}

	public void addActionListener(ActionListener _listener) {
		v_clickListeners.add(_listener); //
	}

	public void addMouseEnterListener(ActionListener _listener) {
		v_mouseEnterListeners.add(_listener); //
	}

	public void addMouseExitListener(ActionListener _listener) {
		v_mouseExitListeners.add(_listener); //
	}

	/**
	 * 单击
	 */
	public void mouseClicked(MouseEvent e) {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			for (int i = 0; i < v_clickListeners.size(); i++) {
				ActionListener actLis = (ActionListener) v_clickListeners.get(i); //
				actLis.actionPerformed(new ActionEvent(e.getSource(), 0, "MouseClicked")); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	public void mouseEntered(MouseEvent e) {
		if (initForeGround == null) {
			initForeGround = this.getForeground(); //先记录下原来的颜色!!
		}

		this.setForeground(Color.RED); //
		this.putClientProperty("ishand", Boolean.TRUE); //
		for (int i = 0; i < v_mouseEnterListeners.size(); i++) {
			ActionListener actLis = (ActionListener) v_mouseEnterListeners.get(i); //
			actLis.actionPerformed(new ActionEvent(e.getSource(), 0, "MouseEnter")); //
		}
		this.revalidate(); //
		this.repaint(); //
	}

	public void mouseExited(MouseEvent e) {
		this.setForeground(initForeGround == null ? this.getForeground() : initForeGround); //
		this.putClientProperty("ishand", Boolean.FALSE); //

		for (int i = 0; i < v_mouseExitListeners.size(); i++) {
			ActionListener actLis = (ActionListener) v_mouseExitListeners.get(i); //
			actLis.actionPerformed(new ActionEvent(e.getSource(), 0, "MouseExit")); //
		}
		this.revalidate(); //
		this.repaint(); //一定要通知重绘!!
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

}
