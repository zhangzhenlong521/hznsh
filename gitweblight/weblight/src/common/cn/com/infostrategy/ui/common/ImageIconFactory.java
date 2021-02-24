package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.UIManager;

/**
 * һЩ���õ�ֱ��ʹ��Java2D����ͼƬ�Ĺ���!!!
 * @author xch
 *
 */
public class ImageIconFactory {

	public static Icon getTextIcon(String _text) {
		return new TextIcon(_text); //
	}

	/**
	 * չ��
	 * @return
	 */
	public static Icon getExpandedIcon() {
		return new ExpandedIcon(); //
	}

	/**
	 * ���ҷ�������ͼ��
	 * @return
	 */
	public static Icon getCollapsedIcon() {
		return new CollapsedIcon(1); //
	}

	/**
	 * ����������ͼ�꡾���/2012-04-01��
	 * @return
	 */
	public static Icon getLeftCollapsedIcon() {
		return new CollapsedIcon(0); //
	}

	/**
	 * ����ͼ��
	 * @return
	 */
	public static Icon getUpArrowIcon() {
		return new UpDownArrow(0); //
	}

	/**
	 * ����ͼ��
	 * @return
	 */
	public static Icon getDownArrowIcon() {
		return new UpDownArrow(1); //
	}

	public static Icon getUpEntityArrowIcon(Color _color) {
		return new EntityArrowIcon(0, _color); //
	}

	public static Icon getDownEntityArrowIcon(Color _color) {
		return new EntityArrowIcon(1, _color); //
	}

	/**
	 * �����ļ�����һ��ͼƬ!!!
	 * @author xch
	 *
	 */
	static class TextIcon implements Icon, Serializable {
		private String text = null; //

		public TextIcon(String _text) {
			text = _text; //
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.RED); //
			g.setFont(new Font("����", Font.PLAIN, 12)); //
			g.drawString(text, 5, 25); //
		}

		public int getIconWidth() {
			return 100;
		}

		public int getIconHeight() {
			return 100;
		}

	}

	/**
	 *չ��ʱ��ͼ��,�������ν���!!!
	 * @author xch
	 *
	 */
	static class ExpandedIcon implements Icon, Serializable {
		public void paintIcon(Component c, Graphics g, int x, int y) {
			int li_color = 75;

			g.setColor(new Color(li_color, li_color, li_color)); //
			g.drawLine(x + 3, y + 8, x + 13, y + 8); //
			li_color = li_color + 25;
			//addColorNumber(li_color);

			g.setColor(new Color(li_color, li_color, li_color)); //
			g.drawLine(x + 4, y + 9, x + 12, y + 9); //
			li_color = addColorNumber(li_color);

			g.setColor(new Color(li_color, li_color, li_color)); //
			g.drawLine(x + 5, y + 10, x + 11, y + 10); //
			//li_color = addColorNumber(li_color);

			g.setColor(new Color(li_color, li_color, li_color)); //
			g.drawLine(x + 6, y + 11, x + 10, y + 11); //
			li_color = addColorNumber(li_color);

			g.setColor(new Color(li_color, li_color, li_color)); //
			g.drawLine(x + 7, y + 12, x + 9, y + 12); //
			li_color = addColorNumber(li_color);

			g.setColor(new Color(li_color, li_color, li_color)); //
			g.drawLine(x + 8, y + 13, x + 8, y + 13); //
			li_color = addColorNumber(li_color);
		}

		public int getIconWidth() {
			return 18;
		}

		public int getIconHeight() {
			return 18;
		}

		private int addColorNumber(int _num) {
			return _num + 22;
		}
	}

	/**
	 * ����ʱ��ͼ��,����������ҵ������εĽ���
	 * @author xch
	 */
	static class CollapsedIcon implements Icon, Serializable {
		private static final long serialVersionUID = 1L;
		public static final int LEFT = 0;
		public static final int RIGHT = 1;
		private int direction;

		public CollapsedIcon(int i) {
			direction = i;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			int li_color = 75;
			if (direction == 0) {
				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 10, y + 4, x + 10, y + 14); //
				li_color = li_color + 25 + 25;
				//li_color = addColorNumber(li_color);

				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 9, y + 5, x + 9, y + 13); //
				li_color = addColorNumber(li_color);

				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 8, y + 6, x + 8, y + 12); //
				//li_color = addColorNumber(li_color);

				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 7, y + 7, x + 7, y + 11); //
				li_color = addColorNumber(li_color);

				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 6, y + 8, x + 6, y + 10); //
				li_color = addColorNumber(li_color);

				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 5, y + 9, x + 5, y + 9); //
				li_color = addColorNumber(li_color);
			} else {
				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 7, y + 4, x + 7, y + 14); //
				li_color = li_color + 25 + 25;
				//li_color = addColorNumber(li_color);

				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 8, y + 5, x + 8, y + 13); //
				li_color = addColorNumber(li_color);

				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 9, y + 6, x + 9, y + 12); //
				//li_color = addColorNumber(li_color);

				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 10, y + 7, x + 10, y + 11); //
				li_color = addColorNumber(li_color);

				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 11, y + 8, x + 11, y + 10); //
				li_color = addColorNumber(li_color);

				g.setColor(new Color(li_color, li_color, li_color)); //
				g.drawLine(x + 12, y + 9, x + 12, y + 9); //
				li_color = addColorNumber(li_color);
			}
		}

		public int getIconWidth() {
			return 18;
		}

		public int getIconHeight() {
			return 18;
		}

		private int addColorNumber(int _num) {
			return _num + 22;
		}
	}

	/**
	 * ˫���б�����ʱ�������뵹���ͼ��,��������!!
	 * @author xch
	 *
	 */
	static class UpDownArrow implements Icon {
		private int size = 12;
		public static final int UP = 0;
		public static final int DOWN = 1;
		private int direction;

		public UpDownArrow(int i) {
			direction = i;
		}

		public int getIconHeight() {
			return size;
		}

		public int getIconWidth() {
			return size;
		}

		public void paintIcon(Component component, Graphics g, int _x, int _y) {
			int k = _x + size / 2;
			int l = _x + 1;
			int i1 = (_x + size) - 2;
			int j1 = _y + 1;
			int k1 = (_y + size) - 2;
			Color color = (Color) UIManager.get("controlDkShadow");
			if (direction == 0) {
				g.setColor(Color.white);
				g.drawLine(l, k1, i1, k1); //��һ����
				g.drawLine(i1, k1, k, j1); //�ڶ�����
				g.setColor(color);
				g.drawLine(l, k1, k, j1); //��������
			} else {
				g.setColor(color);
				g.drawLine(l, j1, i1, j1);
				g.drawLine(l, j1, k, k1);
				g.setColor(Color.white);
				g.drawLine(i1, j1, k, k1);
			}
		}
	}

	static class EntityArrowIcon implements Icon {
		private int size = 12;
		public static final int UP = 0;
		public static final int DOWN = 1;
		private int direction;
		private Color drawColor; //

		public EntityArrowIcon(int _dir, Color _color) {
			direction = _dir;
			drawColor = _color; //
		}

		public int getIconHeight() {
			return size;
		}

		public int getIconWidth() {
			return size;
		}

		public void paintIcon(Component component, Graphics g, int _x, int _y) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setColor(drawColor); //
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (direction == 0) {
				int[] li_pos_1 = new int[] { _x + 6, _y + 3 }; //
				int[] li_pos_2 = new int[] { _x + 0, _y + 9 }; //
				int[] li_pos_3 = new int[] { _x + 12, _y + 9 }; //
				g2d.fillPolygon(new int[] { li_pos_1[0], li_pos_2[0], li_pos_3[0] }, new int[] { li_pos_1[1], li_pos_2[1], li_pos_3[1] }, 3); //
			} else if (direction == 1) { //����!!
				int[] li_pos_1 = new int[] { _x + 0, _y + 3 }; //
				int[] li_pos_2 = new int[] { _x + 12, _y + 3 }; //
				int[] li_pos_3 = new int[] { _x + 6, _y + 9 }; //
				g2d.fillPolygon(new int[] { li_pos_1[0], li_pos_2[0], li_pos_3[0] }, new int[] { li_pos_1[1], li_pos_2[1], li_pos_3[1] }, 3); //
			}
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

}
