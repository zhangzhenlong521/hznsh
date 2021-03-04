package cn.com.infostrategy.ui.workflow.design;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.border.LineBorder;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.RiskVO;

/**
 * @author Gaudenz Alder
 *
 */
public class CellView_2 extends VertexView {

	private static final long serialVersionUID = -2338206459636449407L;

	/**
	 */
	public MyCellRender renderer = new MyCellRender();

	/**
	 */
	public CellView_2() {
		super();
	}

	/**
	 */
	public CellView_2(Object cell) {
		super(cell);
	}

	/**
	 */
	public CellViewRenderer getRenderer() {
		return renderer;
	}

	/**
	 */
	public class MyCellRender extends VertexRenderer {

		public MyCellRender() {
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
		 */
		//		public void paint(Graphics ggg) {
		//			DefaultGraphCell cell = (DefaultGraphCell) getCell();
		//			ActivityVO activityVO = (ActivityVO) cell.getUserObject(); //
		//			String str_text = activityVO.toString(); //
		//
		//			int b = borderWidth;
		//			Graphics2D g2 = (Graphics2D) ggg;
		//			Dimension d = getSize();
		//			setBorder(null);
		//			setOpaque(false);
		//			selected = false;
		//
		//			//画背景图
		//			g2.setColor(new Color(240, 240, 240)); //
		//			g2.fillRect(0, 0, d.width, d.height); //
		//
		//			//画背景框
		//			if (GraphConstants.getBorder(cell.getAttributes()) instanceof LineBorder) {
		//				LineBorder border = (LineBorder) GraphConstants.getBorder(cell.getAttributes()); //
		//				g2.setColor(border.getLineColor()); //
		//				int li_borderthick = border.getThickness(); //
		//				g2.setStroke(new BasicStroke(li_borderthick)); //
		//				g2.drawRect(1, 1, d.width - 2, d.height - 2); ////
		//
		//				g2.setStroke(new BasicStroke((li_borderthick - 2) > 1 ? (li_borderthick - 2) : 1)); //
		//				g2.drawLine(10, 1, 10, d.height - 2); //
		//				g2.drawLine(d.width - 10, 1, d.width - 10, d.height - 2); //
		//			} else {
		//				g2.setColor(Color.BLACK);
		//				g2.drawRect(1, 1, d.width - 2, d.height - 2); ////
		//				g2.drawLine(10, 1, 10, d.height - 2); //
		//				g2.drawLine(d.width - 10, 1, d.width - 10, d.height - 2); //
		//			}
		//
		//			g2.setColor(Color.BLACK);
		//			String[] str_textarrays = getTextArrays(str_text, d.width); //取得所有分割的数据!!
		//			int li_starty = getStartY(12, d.height, str_textarrays.length * 12); //
		//			for (int i = 0; i < str_textarrays.length; i++) {
		//				int li_x = getStartX(12, d.width, 12 * str_textarrays[i].length()); //
		//				g2.drawString(str_textarrays[i], li_x, li_starty); //
		//				li_starty = li_starty + 12;
		//			}
		//
		//			//处理风险点
		//			RiskVO riskVO = activityVO.getRiskVO();
		//			if (riskVO != null) {
		//				if (riskVO.getLevel1RiskCount() > 0) {
		//					g2.setColor(RiskVO.color1); //
		//					g2.fillOval(d.width - 16, 1, 15, 15); //
		//					g2.setColor(Color.WHITE); //
		//					g2.drawString(riskVO.getLevel1RiskCount() + "", d.width - 12, 13);
		//				}
		//
		//				if (riskVO.getLevel2RiskCount() > 0) {
		//					g2.setColor(RiskVO.color2); //黄色
		//					g2.fillOval(d.width - 32, 1, 15, 15); //
		//					g2.setColor(Color.BLACK); //
		//					g2.drawString(riskVO.getLevel2RiskCount() + "", d.width - 28, 13);
		//				}
		//
		//				if (riskVO.getLevel3RiskCount() > 0) {
		//					g2.setColor(RiskVO.color3); //橙色
		//					g2.fillOval(d.width - 16, 17, 15, 15); //
		//					g2.setColor(Color.BLACK); //
		//					g2.drawString(riskVO.getLevel3RiskCount() + "", d.width - 12, 30);
		//				}
		//			}
		//		}
		//
		//		private String[] getTextArrays(String str_text, int _width) {
		//			int li_rowwords = ((_width - 20) / 12); //一行有几个字
		//
		//			String str_remain = str_text; //
		//			Vector v_texts = new Vector(); //
		//			while (str_remain.length() > li_rowwords) {
		//				String str_prefix = str_remain.substring(0, li_rowwords); //取出前面几个字
		//				v_texts.add(str_prefix); //
		//				str_remain = str_remain.substring(li_rowwords, str_remain.length()); //
		//			}
		//
		//			if (!str_remain.equals("")) {
		//				v_texts.add(str_remain); //
		//			}
		//			return (String[]) v_texts.toArray(new String[0]); //
		//		}
		public void paint(Graphics ggg) {
			DefaultGraphCell cell = (DefaultGraphCell) getCell();
			ActivityVO activityVO = (ActivityVO) cell.getUserObject(); //
			String str_text = activityVO.toString(); //
			String sort = activityVO.getUiname();
			 if(sort==null){
				 sort = "";
			 }
			int b = borderWidth;
			Graphics2D g2 = (Graphics2D) ggg;
			Dimension d = getSize();
			//FontMetrics fm = SwingUtilities2.getFontMetrics(this, g2);
			Font font = g2.getFont(); //

			setBorder(null);
			setOpaque(false);
			selected = false;

			//画背景图
			g2.setColor(GraphConstants.getBackground(cell.getAttributes())); //
			//g2.setColor(new Color(232, 238, 247));
			g2.fillRect(0, 0, d.width, d.height); //

			//画背景框
			if (GraphConstants.getBorder(cell.getAttributes()) instanceof LineBorder) {
				LineBorder border = (LineBorder) GraphConstants.getBorder(cell.getAttributes()); //
				g2.setColor(border.getLineColor()); //
				int li_borderthick = border.getThickness(); //
				g2.setStroke(new BasicStroke(li_borderthick)); //
				g2.drawRect(1, 1, d.width - 2, d.height - 2); //
				g2.setStroke(new BasicStroke((li_borderthick - 1) > 1 ? (li_borderthick - 1) : 1)); //
				g2.drawLine(10, 1, 10, d.height - 2); //
				g2.drawLine(d.width - 10, 1, d.width - 10, d.height - 2); //
			} else {
				g2.setColor(Color.BLACK);
				g2.drawRect(1, 1, d.width - 2, d.height - 2); ////
				g2.drawLine(10, 1, 10, d.height - 2); //
				g2.drawLine(d.width - 10, 1, d.width - 10, d.height - 2); //
			}

			//画字
			g2.setFont(GraphConstants.getFont(cell.getAttributes())); //
			if(cell.getAttributes().get("isshoworder") != null && (Boolean)cell.getAttributes().get("isshoworder")) {
				g2.setColor(Color.BLUE);
				g2.drawString(sort, 1, 15);
			}
			g2.setColor(GraphConstants.getForeground(cell.getAttributes())); //
			String[] str_textarrays = getTextArrays(font, str_text, d.width); //取得所有分割的数据!!
			int li_starty = getStartY(12, d.height, str_textarrays.length * 12); //
			for (int i = 0; i < str_textarrays.length; i++) {
				int li_x = getStartX(12, d.width, new TBUtil().getStrWidth(font, str_textarrays[i])); //
				g2.drawString(str_textarrays[i], li_x, li_starty); //
				li_starty = li_starty + 12;
			}

			//处理风险点
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			RiskVO riskVO = activityVO.getRiskVO();
			if (riskVO != null) {
				//合规风险点
				Font font_new = g2.getFont().deriveFont(Font.BOLD); //
				g2.setFont(font_new);
				Shape a = null;
				if (riskVO.getLevel1RiskCount() > 0) {
					g2.setColor(riskVO.getColor1_()); //红色
					//g2.fillOval(d.width - 16, 1, 15, 15); //
					a = TBUtil.getTBUtil().getShape(d.width - 16, 1, 15, 15, riskVO.getShape1());
					g2.fill(a);
					g2.setColor(Color.WHITE); //
					//g2.drawString(riskVO.getLevel1RiskCount() + "", d.width - 12, 13);
					g2.drawString(riskVO.getLevel1RiskCount() + "", TBUtil.getTBUtil().getStrX(g2, a, "" + riskVO.getLevel1RiskCount()), TBUtil.getTBUtil().getStrY(g2, a, "" + riskVO.getLevel1RiskCount()));
				}

				if (riskVO.getLevel2RiskCount() > 0) {
					g2.setColor(riskVO.getColor2_()); //黄色
					//g2.fillOval(d.width - 32, 1, 15, 15); //
					a = TBUtil.getTBUtil().getShape(d.width - 32, 1, 15, 15, riskVO.getShape2());
					g2.fill(a);
					g2.setColor(Color.BLACK); //
					//g2.drawString(riskVO.getLevel2RiskCount() + "", d.width - 28, 13);
					g2.drawString(riskVO.getLevel2RiskCount() + "", TBUtil.getTBUtil().getStrX(g2, a, "" + riskVO.getLevel2RiskCount()), TBUtil.getTBUtil().getStrY(g2, a, "" + riskVO.getLevel2RiskCount()));
				}

				if (riskVO.getLevel3RiskCount() > 0) {
					g2.setColor(riskVO.getColor3_()); //绿色
					//g2.fillOval(d.width - 16, 17, 15, 15); //
					a = TBUtil.getTBUtil().getShape(d.width - 16, 17, 15, 15, riskVO.getShape3());
					g2.fill(a);
					g2.setColor(Color.BLACK); //
					//g2.drawString(riskVO.getLevel3RiskCount() + "", d.width - 12, 28);
					g2.drawString(riskVO.getLevel3RiskCount() + "", TBUtil.getTBUtil().getStrX(g2, a, "" + riskVO.getLevel3RiskCount()), TBUtil.getTBUtil().getStrY(g2, a, "" + riskVO.getLevel3RiskCount()));
				}
				//操作风险点
				if (riskVO.getLevel1RiskCount1() > 0) {
					g2.setColor(RiskVO.color1); //红色
					g2.fillRect(2, 2, 14, 14);
					g2.setColor(Color.WHITE); //
					g2.drawString(riskVO.getLevel1RiskCount1() + "", 5, 13);
				}

				if (riskVO.getLevel2RiskCount1() > 0) {
					g2.setColor(RiskVO.color2); //黄色
					g2.fillRect(18, 2, 14, 14);
					g2.setColor(Color.BLACK); //
					g2.drawString(riskVO.getLevel2RiskCount1() + "", 20, 13);
				}

				if (riskVO.getLevel3RiskCount1() > 0) {
					g2.setColor(RiskVO.color3); //绿色
					g2.fillRect(2, 18, 14, 14);
					g2.setColor(Color.BLACK); //
					g2.drawString(riskVO.getLevel3RiskCount1() + "", 5, 28);
				}
			}
		}

		private String[] getTextArrays(Font _font, String str_text, int _width) {
			ArrayList al_texts = new ArrayList();
			String str_row = ""; //一行的数据!!!
			if (str_text == null) {
				return new String[0];
			}
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
					if (li_length >= _width - 20) { //如果超过一行!
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
			int _tmpy = (li_height - _wordheight) / 2 + 12;
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
			int _tmpx = ((li_width - 10 - _textlength) / 2) + 5;
			if (_tmpx < li_x) {
				return li_x;
			} else {
				return _tmpx;
			}

		}

	}
}