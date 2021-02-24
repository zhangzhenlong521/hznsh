package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.border.AbstractBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * <p>Title: OpenSwing</p>
 * <p>Description:����ʽ�˵���ť </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
 * @version 1.0
 */

public class WLTPopupButton extends JComboBox implements Serializable {

	private static final long serialVersionUID = 2337924909459029947L;

	/**
	 * ��ͨ��ť
	 */
	public static final int TYPE_NORMAL = 0;

	/**
	 * ������ť��ϳɵĵ����˵���ť
	 */
	public static final int TYPE_WITH_RIGHT_TOGGLE = 1;

	/**
	 * ��ť����
	 */
	private int style = -1;

	/**
	 * �ϳɰ�ť��ť���¼�
	 */
	private int actionIndex = -1;

	/**
	 * �����˵�
	 */
	private JPopupMenu popup = null;

	/**
	 * �Ƿ���Ҫ����
	 */
	private boolean mustRefresh = false;

	/**
	 * ��ť
	 */
	private JButton bttLeft;

	/**
	 * �Ұ�ť
	 */
	private JButton bttRight;

	/**
	 * ��ť�¼�
	 */
	private PopupButtonListener listener = new PopupButtonListener();

	/**
	 * <p>Title: OpenSwing</p>
	 * <p>Description: �������ʱ�ı߽�</p>
	 * <p>Copyright: Copyright (c) 2004</p>
	 * <p>Company: </p>
	 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
	 * @version 1.0
	 */
	public class UpBorder extends AbstractBorder {
		int thickness = 1;

		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(Color.white);
			g.drawLine(0, 0, width - 1, 0);
			g.drawLine(0, 0, 0, height - 1);
			g.setColor(Color.gray);
			g.drawLine(width - 1, 0, width - 1, height);
			g.drawLine(0, height - 1, width, height - 1);
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(thickness, thickness, thickness, thickness);
		}
	}

	/**
	 * <p>Title: OpenSwing</p>
	 * <p>Description: ��갴��ʱ�ı߽�</p>
	 * <p>Copyright: Copyright (c) 2004</p>
	 * <p>Company: </p>
	 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
	 * @version 1.0
	 */
	public class DownBorder extends AbstractBorder {
		int thickness = 1;

		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(Color.gray);
			g.drawLine(0, 0, width - 1, 0);
			g.drawLine(0, 0, 0, height - 1);
			g.setColor(Color.white);
			g.drawLine(width - 1, 0, width - 1, height);
			g.drawLine(0, height - 1, width, height - 1);
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(thickness, thickness, thickness, thickness);
		}
	}

	/**
	 * <p>Title: OpenSwing</p>
	 * <p>Description: ��ť���ֶ���ʱ�߽�仯�Լ��¼�����</p>
	 * <p>Copyright: Copyright (c) 2004</p>
	 * <p>Company: </p>
	 * @author <a href="mailto:sunkingxie@hotmail.com">SunKing</a>
	 * @version 1.0
	 */
	private class PopupButtonListener implements MouseListener, PopupMenuListener, Serializable {
		UpBorder upBorder = new UpBorder();
		DownBorder downBorder = new DownBorder();

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (!WLTPopupButton.this.isEnabled()) {
				return;
			}
			if (e.getSource() == bttLeft) {
				//bttLeft.setBorder(downBorder);
				//bttRight.setBorder(downBorder);
				if (getStyle() == TYPE_NORMAL) {
					WLTPopupButton.this.showPopupMenu();
				} else if (actionIndex != -1 && actionIndex < popup.getSubElements().length) {
					AbstractButton btt = (AbstractButton) (popup.getSubElements()[actionIndex].getComponent());
					btt.doClick();
				}
			} else {
				//bttLeft.setBorder(upBorder);
				//bttRight.setBorder(downBorder);
				bttRight.setSelected(true);
				WLTPopupButton.this.showPopupMenu();
			}
		}

		public void mouseReleased(MouseEvent e) {
			bttRight.setSelected(false);
		}

		public void mouseEntered(MouseEvent e) {
			if (!WLTPopupButton.this.isEnabled()) {
				return;
			}
			if (!WLTPopupButton.this.popup.isShowing()) {
				//bttLeft.setBorder(upBorder);
				//bttRight.setBorder(upBorder);
			}
		}

		public void mouseExited(MouseEvent e) {
			if (!WLTPopupButton.this.isEnabled()) {
				return;
			}

			if (!WLTPopupButton.this.popup.isShowing()) {
				//bttLeft.setBorder(null);
				//bttRight.setBorder(null);
			}
		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			if (!WLTPopupButton.this.isEnabled()) {
				return;
			}

			//bttLeft.setBorder(null);
			//bttRight.setBorder(null);
		}

		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	}

	public WLTPopupButton() {
		this(TYPE_NORMAL);
	}

	public WLTPopupButton(int style) {
		this(style, null);
	}

	public WLTPopupButton(int style, String text) {
		this(style, text, null);
	}

	public WLTPopupButton(int style, String text, Icon icon) {
		this(style, text, icon, new JPopupMenu());
	}

	/**
	 * @param style int ��ť����
	 * @param text String ��ʾ����
	 * @param icon Icon ��ʾͼ��
	 * @param popup JPopupMenu �����˵�
	 */
	public WLTPopupButton(int style, String text, Icon icon, JPopupMenu popup) {
		createButtons();
		setText(text);
		setIcon(icon);
		setPopup(popup);
		setStyle(style);
	}

	/**
	 * ��ʾ�˵�
	 */
	protected void showPopupMenu() {
		if (popup == null) {
			return;
		}
		popup.show(this, 0, this.getHeight());  //
	}

	protected void createButtons() {
		if (bttLeft == null) {
			bttLeft = new JButton(); //
			bttLeft.setUI(new WLTButtonUI(BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM)); //������UI�ı�׼�ǲ�ѯ���������ť�ǽ����!!!
			bttLeft.setMargin(new Insets(5, 1, 5, 1));
		}

		if (bttRight == null) {
			bttRight = new JButton() {
				public void paint(Graphics g) {
					super.paint(g);
					g.setColor(Color.BLACK);
					Polygon p = new Polygon();
					int w = getWidth();
					int y = (getHeight() - 4) / 2;
					int x = (w - 6) / 2;
					if (isSelected()) {
						x += 1;
					}
					p.addPoint(x, y);
					p.addPoint(x + 3, y + 3);
					p.addPoint(x + 6, y);
					g.fillPolygon(p);
					g.drawPolygon(p);
				}
			};

			bttRight.setUI(new BasicButtonUI());
			bttRight.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.GRAY)); //
			//bttLeft.setMargin(new Insets(0, 0, 0, 0));
		}
	}

	/**
	 * �����������
	 */
	protected void refreshUI() {
		if (!mustRefresh) {
			return;
		}
		super.removeAll();
		//this.setBorder(null);
		this.setLayout(new BorderLayout());
		this.add(bttLeft, BorderLayout.CENTER);
		if (style == TYPE_WITH_RIGHT_TOGGLE) {
			this.add(bttRight, BorderLayout.EAST);
		}
		bttLeft.setFocusable(false);

		//bttLeft.setBorder(null);
		//bttLeft.addMouseListener(listener);
		bttRight.setFocusable(false);
		bttRight.setPreferredSize(new Dimension(13, 1));
		//bttRight.setBorder(null);
		bttRight.addMouseListener(listener);
		this.doLayout();
	}

	public JButton getButton() {
		return bttLeft;
	}

	public JButton getPopButton() {
		return bttRight;
	}

	/**
	 * ���ð�ť����
	 * @param style int
	 */
	public void setStyle(int style) {
		if (this.style != style) {
			mustRefresh = true;
		}
		this.style = style;
		refreshUI();
	}

	/**
	 * ȡ�ð�ť����
	 * @return int
	 */
	public int getStyle() {
		return style;
	}

	/**
	 * ������ʾ����
	 * @param text String
	 */
	public void setText(String text) {
		bttLeft.setText(text);
	}

	/**
	 * ȡ����ʾ����
	 * @return String
	 */
	public String getText() {
		return bttLeft.getText();
	}

	/**
	 * ���ûһ�״̬
	 * @param enabled boolean
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		bttLeft.setEnabled(enabled);
		bttRight.setEnabled(enabled);
	}

	/**
	 * ������ʾͼ��
	 * @param icon Icon
	 */
	public void setIcon(Icon icon) {
		bttLeft.setIcon(icon);
	}

	/**
	 * ȡ����ʾͼ��
	 * @return Icon
	 */
	public Icon getIcon() {
		return bttLeft.getIcon();
	}

	/**
	 * ��ϰ�ťʱ������ť�൱�ڲ˵�ĳ��Ķ���
	 * @param index int
	 */
	public void setActionSameAsPopup(int index) {
		this.actionIndex = index;
	}

	/**
	 * ȡ����ϰ�ť��ť�൱�ڲ˵�ĳ��Ķ���
	 * @return int
	 */
	public int getActionSameAsPopup() {
		return actionIndex;
	}

	/**
	 * ���õ���ʽ�˵�
	 * @param pop JPopupMenu
	 */
	public void setPopup(JPopupMenu pop) {
		if (this.popup != null) {
			popup.removePopupMenuListener(listener);
		}
		this.popup = pop;
		popup.removePopupMenuListener(listener);
		popup.addPopupMenuListener(listener);
	}

	/**
	 * ȡ�õ���ʽ�˵�
	 * @return JPopupMenu
	 */
	public JPopupMenu getPopup() {
		return popup;
	}

	/**
	 * ���Դ���
	 * @param args String[]
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("JPopupButton Demo");
		frame.setSize(500, 200); //
		JPopupMenu popup = new JPopupMenu("PopupMenu");
		popup.add(new JMenuItem("ͨ�ò�ѯ"));
		popup.add(new JMenuItem("���Ӳ�ѯ"));

		WLTPopupButton btt2 = new WLTPopupButton(WLTPopupButton.TYPE_WITH_RIGHT_TOGGLE, "���ٲ�ѯ", null, popup);
		btt2.setActionSameAsPopup(0);
		btt2.setPreferredSize(new Dimension(100, 25));
		btt2.setSize(new Dimension(100, 25));
		btt2.setMaximumSize(new Dimension(100, 25));

		JToolBar bar = new JToolBar("Toolbar");
		bar.setRollover(true);
		bar.add(btt2);

		frame.getContentPane().add(bar, BorderLayout.NORTH);
		frame.setVisible(true);
	}

}
