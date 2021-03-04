package cn.com.infostrategy.ui.workflow.design;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * @author Gaudenz Alder
 *
 */
public class CellView_Dept_1 extends VertexView {

	private static final long serialVersionUID = -2338206459636449407L;

	/**
	 */
	public JGraphEllipseRenderer renderer = new JGraphEllipseRenderer();

	/**
	 */
	public CellView_Dept_1() {
		super();
	}

	/**
	 */
	public CellView_Dept_1(Object cell) {
		super(cell);
	}

	/**
	 */
	public CellViewRenderer getRenderer() {
		return renderer;
	}

	/**
	 */
	public class JGraphEllipseRenderer extends VertexRenderer {

		public JGraphEllipseRenderer() {
		}

		/**
		 * Return a slightly larger preferred size than for a rectangle.
		 */
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			d.width += d.width / 8;
			d.height += d.height / 2;
			return d;
		}

		/**
		 */
		public void paint(Graphics ggg) {
			DefaultGraphCell cell = (DefaultGraphCell) getCell();
			String str_text = (String) cell.getUserObject();

			int b = borderWidth;
			Graphics2D g2 = (Graphics2D) ggg;
			Dimension d = getSize();
			//FontMetrics fm = SwingUtilities2.getFontMetrics(this, g2);
			Font font = g2.getFont(); //
			//setBorder(null);
			setOpaque(false);
			selected = false;

			//画背景..
			//			g2.setColor(GraphConstants.getBackground(cell.getAttributes())); //
			g2.setColor(new Color(232, 238, 247));
			g2.fillRect(0, 0, d.width - 0, d.height - 0); //用灰色填充

			//画框
			g2.setColor(Color.BLACK); //
			g2.setStroke(new BasicStroke(1)); //
			g2.drawRect(0, 0, d.width - 1, d.height - 1); //

			//画字..
			g2.setColor(GraphConstants.getForeground(cell.getAttributes())); //
			g2.setFont(GraphConstants.getFont(cell.getAttributes())); //
			String[] str_textarrays = getTextArrays(font, str_text, d.width); //取得所有分割的数据!!
			int li_starty = getStartY(5, d.height, str_textarrays.length * GraphConstants.getFont(cell.getAttributes()).getSize()); //SwingUtilities.computeStringWidth(fm, str_row)
			for (int i = 0; i < str_textarrays.length; i++) {
				int li_x = getStartX(5, d.width, new TBUtil().getStrWidth(font, str_textarrays[i]));//
				g2.drawString(str_textarrays[i], li_x, li_starty); //
				li_starty = li_starty + GraphConstants.getFont(cell.getAttributes()).getSize();
			}
		}

		private String[] getTextArrays(Font _font, String str_text, int _width) {
			ArrayList al_texts = new ArrayList();
			String str_row = ""; //一行的数据!!!
			for (int i = 0; i < str_text.length(); i++) {
				String str_item = str_text.substring(i, i + 1);
				if (str_item.equals("\n")) {
					if (!str_row.equals("")) {
						al_texts.add(str_row); //
						str_row = "";
					}
				} else {
					str_row = str_row + str_item; //取出某一位
					int li_length = new TBUtil().getStrWidth(_font, str_row); //算出当前字符的实际宽度.
					if (li_length >= _width - 17) { //如果超过一行!
						al_texts.add(str_row); //
						str_row = "";
					}
				}
			}
			if (!str_row.equals("")) {
				al_texts.add(str_row); //
			}
			return (String[]) al_texts.toArray(new String[0]); //
		}

		/**
		 * 取得所有数据的所有Y位置
		 * @param _vligntype
		 * @param li_y
		 * @param li_height
		 * @param _textheight
		 * @return
		 */
		private int getStartY(int li_y, int li_height, int _wordheight) {
			int _tmpy = (li_height - _wordheight - 5) / 2 + 15;
			if (_tmpy < li_y) {
				return li_y;
			} else {
				return _tmpy;
			}
		}

		/**
		 * 取得每行的X的坐标
		 * @param _aligntype
		 * @param li_x
		 * @param li_width
		 * @param _textlength
		 * @return
		 */
		private int getStartX(int li_x, int li_width, int _textlength) {
			int _tmpx = ((li_width - _textlength - 20) / 2) + 10;
			if (_tmpx < li_x) {
				return li_x;
			} else {
				return _tmpx;
			}

		}

	}
}