/**************************************************************************
 * $RCSfile: PieChartPanel.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class PieChartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// 初始化每块饼的颜色
	private static int dia = 240;// 设置饼图透明度

	private static Color c1 = new Color(0, 255, 0, dia);

	private static Color c2 = new Color(255, 255, 0, dia);

	private static Color c3 = new Color(255, 0, 0, dia);

	private static Color c4 = new Color(255, 128, 64, dia);

	private static Color c5 = new Color(255, 128, 255, dia);

	private static Color c6 = new Color(255, 0, 128, dia);

	private static Color c7 = new Color(233, 124, 24, dia);

	private static Color c8 = new Color(204, 119, 115, dia);

	private static Color c9 = new Color(89, 159, 230, dia);

	private static Color c10 = new Color(148, 140, 179, dia);

	private static Color c11 = new Color(128, 0, 64, dia);

	private static Color c12 = new Color(174, 197, 208, dia);

	public final static Color[] Colors = new Color[] { c1, c2, c3, c12, c11,
			c6, c4, c8, c9, c10, c5, c7 };

	private final static Font textFont = new Font("宋体", Font.PLAIN, 12);

	private final static Font titleFont = new Font("黑体", Font.BOLD, 18);

	private String title = null;

	private String[] data = null;

	private double values[] = null;

	private int originX = 0;

	private int originY = 0;

	private static int inset = 60;

	private int radius = 0;

	private int xGap = 5;

	private int width = 0;// 统计图的宽度和高度

	private boolean[] click = null;

	private int[] angle = null;

	private int mouseX = 0;

	private int mouseY = 0;

	private int ovalX = 0;

	private int ovalY = 0;

	private int cornerX = 0;

	private int cornerY = 0;

	private int selectedKey = -1;

	private static double d2r = Math.PI / 180.0;

	private int titleStart_x;// 定义标题起始坐标变量

	private int titleStart_y;
	
	private Component cpn_parent = null;

	/**
	 * 不推荐使用该构造方法
	 * @param _parent
	 */
	public PieChartPanel(Component _parent) {
		this.values = null;
		this.data = null;
	}

	/**
	 * @param _parent：母板
	 * @param _title：标题
	 * @param _data：元素名称
	 * @param _values：元素相对应的比重
	 */
	public PieChartPanel(Component _parent,String _title, String[] _data, double[] _values) {
		cpn_parent = _parent;
		this.title = _title + "统计（饼形统计图）";
		this.data = _data;
		this.values = _values;
		this.width = this.getSize().width;
		this.click = new boolean[_values.length];
		this.angle = new int[_values.length];
		intitialPanel();
	}

	private void intitialPanel() {
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
					mouseX = e.getX();
					mouseY = e.getY();
					getAngle();
				}
			}
		});
		repaint();
	}

	private void resetClick() {
		for (int i = 0; i < angle.length; i++) {
			click[i] = false;
		}
		repaint();
	}

	/**
	 * 判断该点是否点击在饼图上，然后获取鼠标单击点和
	 * 饼图中心点的连线和基准线之间的夹角，判断单击点
	 * 点击在哪块饼图上
	 */
	private void getAngle() {
		int temp_angle = 0;
		int temp_cornerX = cornerX + ovalX / 2;
		int temp_cornerY = cornerY + ovalY / 2;

		int tempX = mouseX - temp_cornerX;
		int tempY = mouseY - temp_cornerY;

		float temp = (float) (tempX * tempX) / (ovalX * ovalX / 4)
				+ (float) (tempY * tempY) / (ovalY * ovalY / 4);

		if (temp < 1.0f) {
			if (tempX > 0 && tempY < 0) {
				temp_angle = -(int) (Math.atan((float) tempY / tempX) * 180 / Math.PI);
			} else if (tempX > 0 && tempY > 0) {
				temp_angle = 360 - (int) (Math.atan((float) tempY / tempX) * 180 / Math.PI);
			} else if (tempX < 0 && tempY < 0) {
				temp_angle = 180 - (int) (Math.atan((float) tempY / tempX) * 180 / Math.PI);
			} else if (tempX < 0 && tempY > 0) {
				temp_angle = 180 - (int) (Math.atan((float) tempY / tempX) * 180 / Math.PI);
			}

			resetClick();
			for (int i = 0; i < angle.length; i++) {
				if (angle[i] > temp_angle) {
					click[i - 1] = true;
					selectedKey = i - 1;
					break;
				}
				if (i == angle.length - 1) {
					selectedKey = i;
					click[i] = true;
				}
			}
			//处理母板中的数据表相对应行的选中
			((ShowColumnDataAsPieChartDialog)cpn_parent).setSelectedRow(selectedKey);
			repaint();
		} else {
			selectedKey = -1;
			resetClick();
			return;
		}
	}

	/**
	 * 跟据母板数据表传过来的选中的行，
	 * 对饼图中相对应的行进行分离处理
	 * @param _key
	 */
	public void setSelectedKey(int _key) {
		resetClick();
		if (_key == -1) {
			repaint();
			return;
		}
		selectedKey = _key;
		click[_key] = true;
		repaint();
	}

	/**
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Color BackStartColor = Color.white;
		Color BackLastColor = new Color(162, 189, 230);
		Color titleColor = Color.black;
		// 标题背景颜色
		Color titleBackColor = new Color(147, 179, 225);

		Graphics2D g2 = (Graphics2D) g;
		RenderingHints hints = new RenderingHints(null);
		hints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(hints);

		GradientPaint gradient = new GradientPaint(0, 0, BackStartColor, 0,
				400, BackLastColor, false);
		g2.setPaint(gradient);
		Rectangle2D rect = new Rectangle2D.Double(0, 0, this.getSize()
				.getWidth(), this.getSize().getHeight());
		g2.fill(rect);
		// 绘制标题背景
		g2.setColor(titleBackColor);
		g2.fill3DRect(0, 0, (int) this.getSize().getWidth(), 30, true);
		// 绘制统计图标题
		g2.setFont(titleFont);
		g2.setColor(titleColor);

		this.width = (int) this.getSize().getWidth();
		titleStart_x = 22;
		titleStart_y = (width / 2) - (title.length() * 15 / 2);
		g2.drawString(title, titleStart_y, titleStart_x);

		Dimension size = this.getSize();
		originX = size.width / 2;
		originY = size.height / 2 + size.height / 25;

		int diameter = (originX < originY ? size.width - inset : size.height
				- inset);

		radius = (diameter / 2);
		cornerX = (originX - (diameter / 2));
		cornerY = (originY - (diameter / 2));
		ovalX = diameter;// - diameter/10;// + diameter/5;
		ovalY = diameter;// - diameter/10;// - diameter/5;

		int startAngle = 0;
		int arcAngle = 0;
		int moveX = 0;
		int moveY = 0;
		for (int i = 0; i < values.length; i++) {
			arcAngle = (int) (i < values.length - 1 ? Math
					.round(values[i] * 360) : 360 - startAngle);
			g.setColor(Colors[i % Colors.length]);

			Arc2D arc = new Arc2D.Float(Arc2D.PIE);

			//避免相邻两块元素颜色一致，主要是避免首元素和尾元素颜色一致
			if (i > 0 && i % Colors.length == 0) {
				g2.setColor(Colors[Colors.length - 3]);
			} else {
				g2.setColor(Colors[i % Colors.length]);
			}

			if (click[i]) {//让选中的元素突出显示
				int temp_angle = startAngle + arcAngle / 2;
				moveX = (int) (10 * Math
						.cos((float) temp_angle * Math.PI / 180));
				moveY = (int) (10 * Math
						.sin((float) temp_angle * Math.PI / 180));
				if (temp_angle <= 90) {
					arc.setFrame(cornerX + moveX, cornerY - moveY, ovalX,
									ovalY);
				} else if (temp_angle <= 180) {
					arc.setFrame(cornerX + moveX, cornerY - moveY, ovalX,
									ovalY);
				} else if (temp_angle <= 270) {
					arc.setFrame(cornerX + moveX, cornerY - moveY, ovalX,
									ovalY);
				} else if (temp_angle <= 360) {
					arc.setFrame(cornerX + moveX, cornerY - moveY, ovalX,
									ovalY);
				}
				xGap = xGap + 10;
			} else {
				arc.setFrame(cornerX, cornerY, ovalX, ovalY);
			}
			arc.setAngleStart(startAngle);
			arc.setAngleExtent(arcAngle);
			g2.fill(arc);

			angle[i] = startAngle;
			drawLabel(g, data[i], startAngle + (arcAngle / 2));
			startAngle += arcAngle;
		}
	}

	/**
	 * 显示饼图元素的说明文字
	 * @param g
	 * @param _text
	 * @param _angle
	 */
	private void drawLabel(Graphics g, String _text, double _angle) {
		g.setFont(textFont);
		g.setColor(Color.BLACK);
		double radians = _angle * d2r;
		int x = (int) ((radius + xGap) * Math.cos(radians));
		int y = (int) ((radius + xGap) * Math.sin(radians));
		xGap = 5;
		if (x < 0) {
			x -= SwingUtilities.computeStringWidth(g.getFontMetrics(), _text);
		}
		if (y < 0) {
			y -= g.getFontMetrics().getHeight();
		}
		g.drawString(_text, x + originX, originY - y);
	}
}
/**************************************************************************
 * $RCSfile: PieChartPanel.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: PieChartPanel.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:41:00  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:44  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:57  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:25  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:31  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:43  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:20  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:14:31  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/