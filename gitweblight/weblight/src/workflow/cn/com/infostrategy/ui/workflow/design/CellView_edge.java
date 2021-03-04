package cn.com.infostrategy.ui.workflow.design;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;

/**
 * 流程图线控件的显示类 可以换行，保存位置
 * 
 * @author hm
 * 
 */
public class CellView_edge extends EdgeView {
	private JGraphEllipseRenderer renderer = new JGraphEllipseRenderer();
	public CellView_edge() {
		super();
	}

	public CellView_edge(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public CellHandle getHandle(GraphContext graphcontext) {
		return new MyEdgeHandle(this, graphcontext);
	}

	/**
	 * 流程图画线的Renderer。 主要把Label文字的绘制方式重写，支持位置保存，换行。
	 * 
	 */
	private class JGraphEllipseRenderer extends EdgeRenderer {
		private static final long serialVersionUID = 1L;

		protected void paintLabel(Graphics g, String s, Point2D point2d, boolean flag) {
			if (point2d != null && s != null && s.length() > 0 && metrics != null) {
				DefaultGraphCell cell = (DefaultGraphCell) getCell();
				Graphics2D graphics2d = (Graphics2D) g;
				graphics2d.translate(point2d.getX(), point2d.getY());
				g.setColor(fontColor);
				String[] str_textarrays = null;
				if (s == null) {
					s = "";
					str_textarrays = new String[] { s };
				} else {
					str_textarrays = getTextArrays(s);
				}
				for (int m = 0; m < str_textarrays.length; m++) {
					graphics2d.drawString(str_textarrays[m], 0, 0 + m * GraphConstants.getFont(cell.getAttributes()).getSize()); //
				}
			}
		}
	}

	// Defines a EdgeHandle that uses the Shift-Button (Instead of the Right
	// Mouse Button, which is Default) to add/remove point to/from an edge.
	public class MyEdgeHandle extends EdgeView.EdgeHandle {

		/**
		 * @param edge
		 * @param ctx
		 */
		public MyEdgeHandle(EdgeView edge, GraphContext ctx) {
			super(edge, ctx);
		}

		// Override Superclass Method
		public boolean isAddPointEvent(MouseEvent event) {
			return event.isShiftDown();
		}

		// Override Superclass Method
		public boolean isRemovePointEvent(MouseEvent event) {
			return event.isShiftDown();
		}
	}

	private String[] getTextArrays(String str_text) {
		ArrayList al_texts = new ArrayList();
		String str_row = ""; // 一行的数据!!!
		if (str_text == null) {
			return new String[] { "" };
		}
		return str_text.split("\n");
	}
}
