package cn.com.infostrategy.ui.workflow.design;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.ArrayList;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.workflow.design.GroupVO;

/**
 * @author lcj
 *
 */
public class CellView_Dept extends VertexView {

	private static final long serialVersionUID = -2338206459636449407L;

	/**
	 */
	public JGraphEllipseRenderer renderer = new JGraphEllipseRenderer();

	/**
	 */
	public CellView_Dept() {
		super();
	}

	/**
	 */
	public CellView_Dept(Object cell) {
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
			GroupVO groupvo = (GroupVO) cell.getUserObject();
			String str_groupname = groupvo.getWfname();
			if (str_groupname == null) {
				str_groupname = "";
			}
			String posts = groupvo.getPosts();
			Graphics2D g2 = (Graphics2D) ggg;
			Dimension d = getSize(); //
			Font font = g2.getFont(); //
			setOpaque(false);
			selected = false;
			int height = d.height, width = d.width;
			//画背景..
			g2.setColor(new Color(232, 238, 247));
			g2.fillRect(0, 0, width, 60); //用灰色填充
			//画框
			g2.setColor(Color.BLACK); //
			TBUtil tbutil = new TBUtil();
			if (posts == null || "".equals(posts.trim())) {//如果没有岗位，就直接画部门名称
				BasicStroke sub = new BasicStroke(1);
				g2.setStroke(sub); //
				g2.setColor(GraphConstants.getForeground(cell.getAttributes())); //
				g2.setFont(GraphConstants.getFont(cell.getAttributes())); //
				String[] str_textarrays = getTextArrays(font, str_groupname, width); //取得所有分割的数据!!
				int li_starty = getStartY(10, 50, str_textarrays.length * GraphConstants.getFont(cell.getAttributes()).getSize()); //SwingUtilities.computeStringWidth(fm, str_row)
				for (int i = 0; i < str_textarrays.length; i++) {
					int li_x = getStartX(5, width, tbutil.getStrWidth(font, str_textarrays[i]));//
					g2.drawString(str_textarrays[i], li_x, li_starty); //
					li_starty = li_starty + GraphConstants.getFont(cell.getAttributes()).getSize();
				}
			} else {
				//画线，部门和岗位的割线
				g2.drawLine(0, 30, width, 30);
				//画部门名称
				g2.setColor(GraphConstants.getForeground(cell.getAttributes())); //
				g2.setFont(GraphConstants.getFont(cell.getAttributes())); //
				String[] str_textarrays = getTextArrays(font, str_groupname, width); //取得所有分割的数据!!
				int li_starty = getStartY(5, 30, str_textarrays.length * GraphConstants.getFont(cell.getAttributes()).getSize()); //SwingUtilities.computeStringWidth(fm, str_row)
				for (int i = 0; i < str_textarrays.length; i++) {
					int li_x = getStartX(5, width, tbutil.getStrWidth(font, str_textarrays[i]));//
					g2.drawString(str_textarrays[i], li_x, li_starty); //
					li_starty = li_starty + GraphConstants.getFont(cell.getAttributes()).getSize();
				}
				//画岗位名称
				//袁江晓20120918修改以下逻辑，按照可视的宽度和次序来调整岗位                  begin
				int startx = 0, they = Math.max(30, li_starty);
				String[] str_posts = tbutil.split(posts, ";");//把返回的所有值用;隔开 格式为字+#+宽度
				String[] strs_temp = (String[]) ((null == str_posts) ? "" : str_posts[0].split("#"));
				if ((null != strs_temp) && strs_temp.length == 2) {//如果是新修改的逻辑
					String strs_fonts = "";//传过来的每个单元格的字，中间用分号隔开
					String strs_widths = "";//传过来的每个单元格的宽度，中间用分号隔开
					int all_len = 0;
					for (int i = 0; i < str_posts.length; i++) {
						String[] strs = str_posts[i].split("#");//每一个字符串用#分割开
						strs_fonts += strs[0] + ";";
						if (strs.length == 1) {
							strs_widths += 150 + ";";//如果有不符合要求的数据，比如"风险部#123;合规部;"，默认设置宽度为150，否则会报异常【李春娟/2014-10-30】
							all_len += 150;
						} else {
							strs_widths += strs[1] + ";";
							all_len += Integer.parseInt(strs[1]);
						}
					}
					String[] str_fonts = tbutil.split(strs_fonts, ";");//传过来的单元格的字数组
					String[] str_widths = tbutil.split(strs_widths, ";");//传过来的单元格的宽度数组
					FontMetrics metr = Toolkit.getDefaultToolkit().getFontMetrics(font);
					BasicStroke str = new BasicStroke(0.8f, 0, 2, 1.3f, new float[] { 5.0f }, 0.3f);
					g2.setStroke(str);
					for (int j = 0, len = str_fonts.length; j < len; j++) {
						String post = str_fonts[j];
						Float ff = Float.parseFloat(str_widths[j]) / all_len;//获得每个单元格占总的宽度的比例
						int add = (int) (ff * width);//取得每个单元格的实际宽度
						str_textarrays = getTextArrays(font, post, add); //取得所有分割的数据!!
						int li_starty1 = getStartY(they, 90, str_textarrays.length * GraphConstants.getFont(cell.getAttributes()).getSize());
						for (int i = 0; i < str_textarrays.length; i++) {
							String text = str_textarrays[i];//取得每一行的数据
							g2.setColor(GraphConstants.getForeground(cell.getAttributes())); //设置字体颜色为设置的颜色
							g2.drawString(text, startx + (add - metr.stringWidth(text)) / 2, li_starty1); //
							li_starty1 = li_starty1 + GraphConstants.getFont(cell.getAttributes()).getSize();
							//画线，岗位的垂直割线
							g2.setColor(new Color(200, 200, 200)); //
							g2.drawLine(startx, 31, startx, height);//如果只有一个岗位不用画线
						}
						startx += add;
					}
				} else if ((null != strs_temp) && strs_temp.length == 1) {//如果是旧的数据
					FontMetrics metr = Toolkit.getDefaultToolkit().getFontMetrics(font);
					BasicStroke str = new BasicStroke(0.8f, 0, 2, 1.3f, new float[] { 5.0f }, 0.3f);
					g2.setStroke(str);
					int alllen = 0, allwidth = 0;
					for (String text : str_posts) {
						alllen += text.length();
						allwidth += metr.stringWidth(text);
					}
					double the1 = Math.max(0, (double) (allwidth - width) / allwidth), the2 = 1 - the1;
					for (int j = 0, len = str_posts.length; j < len; j++) {
						String post = str_posts[j];
						int add = (int) ((the1 / len + the2 * post.length() / alllen) * width);
						str_textarrays = getTextArrays(font, post, width / len); //取得所有分割的数据!!
						int li_starty1 = getStartY(they, 90, str_textarrays.length * GraphConstants.getFont(cell.getAttributes()).getSize()); //SwingUtilities.computeStringWidth(fm, str_row)
						for (int i = 0; i < str_textarrays.length; i++) {
							String text = str_textarrays[i];
							g2.setColor(Color.BLACK); //
							g2.drawString(text, startx + (add - metr.stringWidth(text)) / 2, li_starty1); //
							li_starty1 = li_starty1 + GraphConstants.getFont(cell.getAttributes()).getSize();
							//画线，岗位的垂直割线
							g2.setColor(new Color(200, 200, 200)); //
							g2.drawLine(startx, 31, startx, height);//如果只有一个岗位不用画线
						}
						startx += add;
					}
				}
			}
			//袁江晓20120918修改逻辑，按照可视的宽度和次序来调整岗位                  end
			BasicStroke sub = new BasicStroke(1);
			g2.setStroke(sub); //
			g2.setColor(Color.BLACK);
			g2.drawLine(0, 60, width, 60);
			g2.drawRect(0, 0, width - 1, height - 1); //
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