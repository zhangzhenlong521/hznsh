package cn.com.infostrategy.ui.workflow.design;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * @author Gaudenz Alder
 *
 */
public class CellView_Station extends VertexView {

	private static final long serialVersionUID = -2338206459636449407L;

	/**
	 */
	public JGraphEllipseRenderer renderer = new JGraphEllipseRenderer();

	/**
	 */
	public CellView_Station() {
		super();
	}

	/**
	 */
	public CellView_Station(Object cell) {
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
			this.setToolTipText("abcd"); //
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseEntered(MouseEvent e) {

					super.mouseEntered(e);
					System.out.println("mouseEntered"); //
				}

				@Override
				public void mouseExited(MouseEvent e) {

					super.mouseExited(e);
					System.out.println("mouseExited"); //
				}

			});
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

		public void paint(Graphics ggg) {
			DefaultGraphCell cell = (DefaultGraphCell) getCell();

			Graphics2D g2 = (Graphics2D) ggg;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Dimension d = getSize();

			setBorder(null);
			setOpaque(false);
			selected = false;

			//画线,即像勺子一样的一个勾子
			g2.setColor(GraphConstants.getBackground(cell.getAttributes())); //
			g2.setStroke(new BasicStroke(3)); //
			g2.drawLine(0, 5, 30, d.height - 3); //
			g2.drawLine(30, d.height - 3, 60, 5); //画线

			//画虚线
			g2.setStroke(new BasicStroke(3f, 0, 2, 1.3f, new float[] { 5.0f }, 0.3f));
			g2.drawLine(60, 3, d.width, 3); //画线

			//最后一个圆点
			//g2.fillOval(d.width - 13, 0, 6, 6); //

		}
	}
}