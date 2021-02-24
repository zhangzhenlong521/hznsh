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
import cn.com.infostrategy.to.workflow.design.GroupVO;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * @author Gaudenz Alder
 *
 */
public class CellView_8 extends VertexView {

	private static final long serialVersionUID = -2338206459636449407L;

	public JGraphEllipseRenderer renderer = new JGraphEllipseRenderer();

	public CellView_8() {
		super();
	}

	public CellView_8(Object cell) {
		super(cell);
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}

	public class JGraphEllipseRenderer extends VertexRenderer {

		public JGraphEllipseRenderer() {
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					super.mouseExited(e);
				}

			});
		}

		public void paint(Graphics ggg) {
			DefaultGraphCell cell = (DefaultGraphCell) getCell();
			String str_text = "";
			String str_img = null;
			Object obj = cell.getUserObject();
			String sort = "";
			if (obj instanceof ActivityVO) {
				str_text = ((ActivityVO) obj).getWfname();
				if (str_text == null) {
					str_text = "";
				}
				str_img = ((ActivityVO) obj).getImgstr();
				if (str_img == null || "".equals(str_img)) {
					str_img = "workflow/start.gif";
				}
				
				sort = ((ActivityVO) obj).getUiname();
				 if(sort==null){
					 sort = "";
				 }
			} else {
				str_text = ((GroupVO) obj).getWfname();
				if (str_text == null) {
					str_text = "";
				}
			}
			Graphics2D g2 = (Graphics2D) ggg;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setFont(GraphConstants.getFont(cell.getAttributes())); //
			Dimension d = getSize();
			Font font = g2.getFont(); //cell
			//画框+
			if (GraphConstants.getBorder(cell.getAttributes()) instanceof LineBorder) {//因为流程监控时候需要所以还要有这段代码
				LineBorder border = (LineBorder) GraphConstants.getBorder(cell.getAttributes()); //
				g2.setColor(border.getLineColor()); //
				g2.setStroke(new BasicStroke(border.getThickness())); //在流程监控时,需要指定是红色的!
				g2.drawRect(0, 0, d.width - 1, d.height - 1); //
			}

			//画字..
			g2.setFont(GraphConstants.getFont(cell.getAttributes())); //
			if(cell.getAttributes().get("isshoworder") != null && (Boolean)cell.getAttributes().get("isshoworder")) {
				g2.setColor(Color.BLUE);
				g2.drawString(sort, 1, 15);
			}
			
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2.setColor(GraphConstants.getForeground(cell.getAttributes())); //
			String[] str_textarrays = getTextArrays(font, str_text, d.width); //取得所有分割的数据!!
			int textheight = GraphConstants.getFont(cell.getAttributes()).getSize() * str_textarrays.length;

			//画图片
			java.awt.Image img = UIUtil.getImage(str_img).getImage();
			int img_starty = (d.height - textheight - img.getHeight(null)) / 2;
			if (img_starty < 0) {
				img_starty = 0;
			}
			int img_startx = (d.width - img.getWidth(null)) / 2;
			if (img_startx < 0) {
				img_startx = 0;
			}
			g2.drawImage(img, img_startx, img_starty, img.getWidth(null), img.getHeight(null), null);
			//int li_starty = getStartY(17, d.height, str_textarrays.length * GraphConstants.getFont(cell.getAttributes()).getSize()); //SwingUtilities.computeStringWidth(fm, str_row)
			int li_starty = img_starty + img.getHeight(null) + 15;
			if (li_starty < 0) {
				li_starty = 0;
			}
			if (li_starty > d.height) {
				li_starty = d.height;
			}
			for (int i = 0; i < str_textarrays.length; i++) {
				int li_x = getStartX(5, d.width, new TBUtil().getStrWidth(font, str_textarrays[i]));//
				g2.drawString(str_textarrays[i], li_x, li_starty); //
				li_starty = li_starty + GraphConstants.getFont(cell.getAttributes()).getSize();
			}
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (obj instanceof ActivityVO) {
				//描风险点
				RiskVO riskVO = ((ActivityVO) obj).getRiskVO();
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
					int li_length = new TBUtil().getStrWidth(_font, str_row);
					if (li_length >= _width - 30) { //如果超过一行!
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
			int _tmpy = li_height / 2 + 5;
			return _tmpy;
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
			int _tmpx = ((li_width - _textlength) / 2);
			if (_tmpx < li_x) {
				return li_x;
			} else {
				return _tmpx;
			}

		}

	}
}