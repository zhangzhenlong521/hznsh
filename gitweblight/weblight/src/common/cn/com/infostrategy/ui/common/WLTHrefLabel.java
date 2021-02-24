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
 * һ��JLabel,���ܳ���Html������Ч��,���������ȥ������ͼ��!!! �����Զ����»��߳���!!!
 * @author xch
 *
 */
public class WLTHrefLabel extends JLabel implements MouseListener {

	private Color initForeGround = null; //һ��ʼ���ֵ���ɫ!��Ϊ�����������ȥ����Ҫ֪��ԭ������ɫ!!
	private Vector v_clickListeners = new Vector(); //
	private Vector v_mouseEnterListeners = new Vector(); //������ļ���
	private Vector v_mouseExitListeners = new Vector(); //����ƿ��ļ���

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
		 * �ع���ͼ...
		 */
		public void paint(Graphics g, JComponent c) {
			if (initForeGround == null) {
				initForeGround = c.getForeground(); //�ȼ�¼��ԭ������ɫ!!
			}
			super.paint(g, c);
			JLabel label = (JLabel) c; //
			Boolean bo_isHand = (Boolean) c.getClientProperty("ishand"); //�Ƿ�������!!!!
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
	 * ����
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
			initForeGround = this.getForeground(); //�ȼ�¼��ԭ������ɫ!!
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
		this.repaint(); //һ��Ҫ֪ͨ�ػ�!!
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

}
