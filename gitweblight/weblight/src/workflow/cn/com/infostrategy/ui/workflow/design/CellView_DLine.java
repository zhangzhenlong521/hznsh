package cn.com.infostrategy.ui.workflow.design;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * 工作流标尺 画虚线 【杨科/2012-11-12】
 */

public class CellView_DLine extends VertexView {

	private static final long serialVersionUID = -2338206459636449407L;

	public JGraphEllipseRenderer renderer = new JGraphEllipseRenderer();

	public CellView_DLine() {
		super();
	}

	public CellView_DLine(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public class JGraphEllipseRenderer extends VertexRenderer {

		public void paint(Graphics g) {
			DefaultGraphCell cell = (DefaultGraphCell) getCell();

			Graphics2D g2 = (Graphics2D) g;
			Dimension d = getSize();
			
			//画虚线
			g2.setStroke(new BasicStroke(1f, 1, 1, 1f, new float[] { 5.0f }, 0.5f));
			g2.drawLine(0, 0, d.width, d.height); 
		}
	}
}