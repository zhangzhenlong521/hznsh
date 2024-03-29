package com.pushworld.ipushgrc.ui.risk.p070;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JPanel;

/**
 * 风险分布图
 * @author yyb
 * Feb 14, 2012 3:48:23 PM
 */
public class RiskDistributePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private HashMap<String, Integer> data_map = null;
	private static HashMap<String, Color> color_map;
	private static HashMap<String, String> rank_map;
	private String clickedKey;
	private ClickRiskNumListener clickListener=null;
	private HashMap<Rectangle, String> paint_rect_map = null;
	static {
		color_map = new HashMap<String, Color>();
		color_map.put("极大风险", new Color(255, 0, 0));
		color_map.put("中等风险", new Color(255, 255, 0));
		color_map.put("高风险", new Color(255, 200, 0));
		color_map.put("低风险", new Color(0, 255, 0));
		color_map.put("极小风险", new Color(0, 255, 255));
		rank_map = new HashMap<String, String>();
		rank_map.put("不太可能_重大", "高风险");
		rank_map.put("几乎肯定_极小", "中等风险");
		rank_map.put("很可能_较大", "极大风险");
		rank_map.put("很可能_极小", "低风险");
		rank_map.put("几乎肯定_较小", "高风险");
		rank_map.put("几乎肯定_中等", "极大风险");
		rank_map.put("不太可能_较大", "中等风险");
		rank_map.put("可能_较小", "中等风险");
		rank_map.put("可能_重大", "极大风险");
		rank_map.put("可能_极小", "低风险");
		rank_map.put("不太可能_较小", "低风险");
		rank_map.put("可能_中等", "高风险");
		rank_map.put("罕见_极小", "极小风险");
		rank_map.put("不太可能_中等", "中等风险");
		rank_map.put("几乎肯定_重大", "极大风险");
		rank_map.put("很可能_中等", "高风险");
		rank_map.put("罕见_重大", "中等风险");
		rank_map.put("几乎肯定_较大", "极大风险");
		rank_map.put("罕见_较小", "极小风险");
		rank_map.put("不太可能_极小", "极小风险");
		rank_map.put("很可能_重大", "极大风险");
		rank_map.put("很可能_较小", "中等风险");
		rank_map.put("罕见_较大", "低风险");
		rank_map.put("可能_较大", "高风险");
		rank_map.put("罕见_中等", "低风险");
	}
	
	public RiskDistributePanel() {
		addDefaultMouseListner();
	}
	
	
	public RiskDistributePanel(HashMap<String, Integer> map) {
		this();
		data_map = map; 
	}
	
	private void addDefaultMouseListner(){
		MouseAdapter adaper = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (getClickedKey() != null&&clickListener!=null) {
						clickListener.onMouseClickRiskNum(getClickedKey());
				}
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				if (getPaintRectMap() != null) {
					HashMap<Rectangle, String> rect_paint_map = (HashMap<Rectangle, String>)getPaintRectMap();
					Rectangle[] rects = (Rectangle[]) (rect_paint_map).keySet().toArray(new Rectangle[0]);
					boolean isOverRect = false;
					Rectangle rect_key = null;
					for (int i = 0; i < rects.length; i++) {
						if (rects[i].contains(e.getX(), e.getY())) {
							isOverRect = true;
							rect_key = rects[i];
							setClikedKey((String) rect_paint_map.get(rect_key));
						}
					}
					if (isOverRect) {
						setCursor(new Cursor(Cursor.HAND_CURSOR));
					} else {
						setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						setClikedKey(null);
					}
				}
			}
		};
		addMouseListener(adaper);
		addMouseMotionListener(adaper);
	}
	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g); //
		this.setBackground(Color.WHITE); //
		Rectangle rect = this.getBounds(); //  //g.getClipBounds(); //
		int _width = (int) rect.getWidth(); //
		int _height = (int) rect.getHeight();
		int orig_point_x;
		int orig_point_y;
		double blank_percent = 0.12;
		orig_point_x = (int) (_width * blank_percent);
		orig_point_y = _height - (int) (_height * blank_percent);
		int line_width = (int) (_width * (1 - 2 * blank_percent));
		int line_height = (int) (_height * (1 - 2 * blank_percent));
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setPaint(new Color(255, 0, 0));
		int x_destination = (int) (_width * (1 - blank_percent));
		int y_destination = (int) (_height * blank_percent);
		
		//2.画箭头,坐标轴放到最后画，防止被覆盖
		drawArrowHead(true, x_destination, orig_point_y, _width, _height, blank_percent, g2d);
		drawArrowHead(false, orig_point_x, y_destination, _width, _height, blank_percent, g2d);
		//3.坐标分段
		int divXNum = 5;
		int divYNum = 5;
		int[] x_coords = this.divX(orig_point_x, line_width, divXNum);
		int[] y_coords = this.divY(orig_point_y, line_height, divYNum);
		//4,写坐标注释
		String[] annoX = new String[] { "极小", "较小", "中等", "较大", "重大" };
		String[] annoY = new String[] { "罕见", "不太可能", "可能", "很可能", "几乎肯定" };
		drawAnnoX(orig_point_x, orig_point_y, x_coords, annoX, _height, g2d);
		drawAnnoY(orig_point_x, orig_point_y, y_coords, annoY, _height, g2d);
		//5.各矩形着底色
		HashMap<String, Rectangle> map = getAnnoCoorMap(orig_point_x, orig_point_y, annoX, annoY, x_coords, y_coords);
		String[] allKeys = map.keySet().toArray(new String[0]);
		for (int i = 0; i < allKeys.length; i++) {
			Rectangle rec=map.get(allKeys[i]);
			if(rec!=null){
				String[] strArr = allKeys[i].split("_");
				String key = strArr[1] + "_" + strArr[0];
				Color color = (Color) color_map.get(rank_map.get(key));
				paintBGroundColor(rec,color,g2d);
			}
		}
		//6.画网格线
		drawNetLine(x_coords, y_coords, orig_point_x, orig_point_y, g2d);
	
		//7.画风险点圆及相应数量
		if (data_map != null) {
			String[] keys = (String[]) data_map.keySet().toArray(new String[0]);
			//找最大最小数量值
			int maxCount = 0;
			int minCount = 0;
			for (int i = 0; i < keys.length; i++) {
				int count = (Integer) data_map.get(keys[i]);
				if (count > maxCount)
					maxCount = count;
				if (count < minCount)
					minCount = count;
			}
			paint_rect_map = new HashMap<Rectangle, String>();
			for (int i = 0; i < keys.length; i++) {
				String[] strArr = keys[i].split("_");
				String key = strArr[1] + "_" + strArr[0];
				Color color = (Color) color_map.get(rank_map.get(key));
				drawRiskCircleAndCount((Rectangle) map.get(keys[i]), (Integer) data_map.get(keys[i]), g2d, Color.white, maxCount, minCount, key);

			}
		}
		
		g2d.setPaint(Color.BLACK);
		BasicStroke stro = new BasicStroke(1.8f, 0, 2, 2, new float[] {1.0f}, 0f);
		g2d.setStroke(stro);
		//1.画坐标轴
		g2d.drawLine(orig_point_x, orig_point_y, x_destination, orig_point_y);
		g2d.drawLine(orig_point_x, orig_point_y, orig_point_x, y_destination);
		
	}

	private void paintBGroundColor(Rectangle rec, Color color, Graphics2D g2d) {
		g2d.setColor(color);
		g2d.fillRect((int)rec.getX(),(int)rec.getY(),(int)rec.getWidth(),(int)rec.getHeight());
		g2d.setColor(Color.BLACK);
	}

	/**
	 * 画圆，写数,同时设置监听Map,key为key，value为相应的绘圆矩形
	 * @param rect
	 * @param count
	 * @param g2d
	 * @param minCount 
	 * @param maxCount 
	 * @param key 
	 */
	private void drawRiskCircleAndCount(Rectangle rect, Integer count, Graphics2D g2d, Color cir_color, int maxCount, int minCount, String key) {

		if (rect != null) {
			int rect_width = (int) rect.getWidth();
			int rect_height = (int) rect.getHeight();
			int rx = (int) rect.getX();
			int ry = (int) rect.getY();
			int line_len = rect_width > rect_height ? rect_height : rect_width;
			if (rect_width > rect_height) {
				rx = rx + (rect_width - rect_height) / 2;
				line_len = rect_height;
			} else {
				ry = ry + (rect_height - rect_width) / 2;
				line_len = rect_width;
			}
			//绘圆区域
			Rectangle paint_rect = getPaintRect(new Rectangle(rx, ry, line_len, line_len), count, maxCount, minCount);
			paint_rect_map.put(paint_rect, key);
			g2d.setColor(cir_color);
			/*====画圆============================*/
			g2d.fillOval((int) paint_rect.getX(), (int) paint_rect.getY(), (int) paint_rect.getWidth(), (int) paint_rect.getWidth());
			/*====标数量开始======================*/
			g2d.setColor(Color.BLUE);
			//1.设字体大小
			int default_font_size = 20;
			default_font_size = (int) (paint_rect.getWidth() * default_font_size / 29);//根据圆的大小设定字体
			if (count >= 100) { //达到两位数以上时，每增一位，字体大小减小
				int num_count = count.toString().length();//数字位数
				int scale_num = num_count - 2;//字体需要缩小的次数
				double scale = 1 - (double) scale_num / (double) num_count;
				default_font_size = (int) (default_font_size * scale);
			}
			g2d.setFont(new Font("宋体", Font.BOLD, default_font_size));
			//2.设位置
			int font_x;
			int font_y;
			if (count < 10) {
				font_x = (int) (paint_rect.getX() + paint_rect.getWidth() / 3);
				font_y = (int) (paint_rect.getY() + paint_rect.getWidth() * 2 / 3);
			} else if (count >= 10 && count < 99) {
				font_x = (int) (paint_rect.getX() + paint_rect.getWidth() * 3 / 24);
				font_y = (int) (paint_rect.getY() + paint_rect.getWidth() * 13 / 18);
			} else {
				font_x = (int) (paint_rect.getX() + paint_rect.getWidth() * 3 / 24);
				font_y = (int) (paint_rect.getY() + paint_rect.getWidth() * 12 / 18);
			}
			g2d.drawString(count.toString(), font_x, font_y);
			/*====标数量结束======================*/
		}
	}

	/**
	 * 根据数字大小动态获得绘圆区域
	 * @param rectangle
	 * @param count
	 * @param minCount 
	 * @param maxCount 
	 * @return
	 */
	private Rectangle getPaintRect(Rectangle rect, Integer count, int maxCount, int minCount) {
		double r_x = rect.getX();
		double r_y = rect.getY();
		double len = rect.getWidth();
		double p_r_x;
		double p_r_y;
		double p_len;
		//默认最小值
		p_len = len / 4;
		p_len = p_len + (len * 3 / 4) * (count - minCount) / (maxCount - minCount);
		p_r_x = r_x + (len - p_len) / 2;
		p_r_y = r_y + (len - p_len) / 2;
		return new Rectangle((int) p_r_x, (int) p_r_y, (int) p_len, (int) p_len);
	}

	/**
	 * 根据X，Y轴注释与相应的丢应矩形封装成Map
	 * @param orig_point_x   原点x
	 * @param orig_point_y	 原点Y
	 * @param annoX          x注释数组
	 * @param annoY			 y注释数组
	 * @param x_coords		 x各坐标点
	 * @param y_coords		 y各坐标点
	 * @return 
	 */
	private HashMap<String, Rectangle> getAnnoCoorMap(int orig_point_x, int orig_point_y, String[] annoX, String[] annoY, int[] x_coords, int[] y_coords) {
		if (y_coords == null || annoX == null || x_coords == null || annoY == null) {
			return null;
		}
		int numY = (y_coords.length > annoY.length) ? annoY.length : y_coords.length;
		int numX = (x_coords.length > annoX.length) ? annoX.length : x_coords.length;
		if (numX == 0 || numY == 0)
			return null;
		int rectWidth = x_coords[0] - orig_point_x;
		int rectHeight = orig_point_y - y_coords[0];
		HashMap<String, Rectangle> map = new HashMap<String, Rectangle>();
		for (int i = 0; i < numX; i++) {
			for (int j = 0; j < numY; j++) {
				map.put(annoX[i] + "_" + annoY[j], new Rectangle(x_coords[i] - rectWidth, y_coords[j], rectWidth, rectHeight));
			}
		}
		return map;
	}

	/**
	 * y轴加注解
	 * @param orig_point_x 原点X
	 * @param orig_point_y 原点y
	 * @param y_coords 分段点的坐标数组
	 * @param strings  注解文字
	 * @param _height  高度
	 * @param g2d 
	 */
	private void drawAnnoY(int orig_point_x, int orig_point_y, int[] y_coords, String[] strings, int _height, Graphics2D g2d) {
		g2d.setColor(Color.black);
		if (y_coords == null || strings == null) {
			return;
		}
		int num = (y_coords.length > strings.length) ? strings.length : y_coords.length;
		if (num == 0) {
			return;
		}
		int div_len = orig_point_y - y_coords[0];
		int str_x = (int) (orig_point_x * 0.4);
		for (int i = 0; i < num; i++) {
			int str_y = y_coords[i] + div_len * 2 / 3;
			g2d.drawString(strings[i], str_x, str_y);
		}
	}

	/**
	 * X轴加注解
	 * @param orig_point_x 原点X
	 * @param orig_point_y 原点y
	 * @param x_coords 分段点的坐标数组
	 * @param strings  注解文字
	 * @param _height  高度
	 * @param g2d 
	 */
	private void drawAnnoX(int orig_point_x, int orig_point_y, int[] x_coords, String[] strings, int _height, Graphics2D g2d) {
		g2d.setColor(Color.black);
		g2d.setFont(new Font("宋体", Font.BOLD, 14));
		if (x_coords == null || strings == null) {
			return;
		}
		int num = (x_coords.length > strings.length) ? strings.length : x_coords.length;
		if (num == 0) {
			return;
		}
		int div_len = x_coords[0] - orig_point_x;
		int str_y = orig_point_y + (_height - orig_point_y) * 1 / 3;
		for (int i = 0; i < num; i++) {
			int str_x = x_coords[i] - div_len * 2 / 3;
			g2d.drawString(strings[i], str_x, str_y);
		}
	}

	/**
	 * 绘制箭头
	 * @param is_right  用来标记，是向右的箭头，还是向上的
	 * @param x  		x或Y轴最右面或最上面那个点的坐标
	 * @param y			x或Y轴最右面或最上面那个点的坐标
	 * @param _width	面板宽度 
	 * @param _height	面板高度
	 * @param blank_percent  两端空白区域比例
	 * @param g2d
	 */
	private void drawArrowHead(boolean is_right, int x, int y, int _width, int _height, double blank_percent, Graphics2D g2d) {
		int[] xpoints = new int[3];
		int[] ypoints = new int[3];
		if (is_right) {//向右的箭头
			int arr_height = (int) ((_width * blank_percent) / 3);
			xpoints[0] = x + arr_height;
			ypoints[0] = y;
			xpoints[1] = x;
			ypoints[1] = y + arr_height / 5;
			xpoints[2] = x;
			ypoints[2] = y - arr_height / 5;
		} else {
			int arr_height = (int) ((_height * blank_percent) / 2);
			xpoints[0] = x;
			ypoints[0] = y - arr_height;
			xpoints[1] = x - arr_height / 4;
			ypoints[1] = y;
			xpoints[2] = x + arr_height / 4;
			ypoints[2] = y;
		}
		g2d.setColor(Color.red);
		Polygon pol = new Polygon(xpoints, ypoints, 3);
		g2d.fillPolygon(pol);
	}

	/**
	 * 平分X轴
	 * @param orig_x     x轴起始x坐标
	 * @param line_width x轴宽度
	 * @param divNum     划分成几段
	 * @return 均分点的x坐标        
	 */
	public int[] divX(int orig_x, int line_width, int divNum) {
		int line_part_len = (int) Math.floor((line_width / divNum));
		int[] x_point_coor = new int[divNum];
		for (int i = 0; i < divNum; i++) {
			x_point_coor[i] = orig_x + line_part_len * (i + 1);
		}
		return x_point_coor;
	}

	/**
	 * 平分y轴
	 * @param orig_x     y轴起始y坐标
	 * @param line_height y轴宽度
	 * @param divNum     划分成几段
	 * @return 均分点的y坐标        
	 */
	public int[] divY(int orig_y, int line_height, int divNum) {
		int line_part_len = (int) Math.floor((line_height / divNum));
		int[] Y_point_coor = new int[divNum];
		for (int i = 0; i < divNum; i++) {
			Y_point_coor[i] = orig_y - line_part_len * (i + 1);
		}
		return Y_point_coor;
	}

	/**
	 * 画网格线
	 * @param x x坐标数组
	 * @param y y坐标数组
	 * @param ori_x  原点X坐标
	 * @param _ori_y 原点Y坐标
	 * @param g2d
	 */
	public void drawNetLine(int[] x, int[] y, int ori_x, int _ori_y, Graphics2D g2d) {
		int max_x = x[x.length - 1];
		int min_y = y[y.length - 1];
		BasicStroke stro = new BasicStroke(1.0f, 0, 2, 2, new float[] { 5.0f, 5.0f }, 0f);
		g2d.setStroke(stro);
		g2d.setColor(Color.black);
		for (int i = 0; i < x.length; i++) {
			g2d.drawLine(x[i], _ori_y, x[i], min_y);
		}
		for (int i = 0; i < y.length; i++) {
			g2d.drawLine(ori_x, y[i], max_x, y[i]);
		}
	}

	public void addClickRiskNumListener(ClickRiskNumListener listener){
		clickListener=listener;
	}
	
	
	public String getClickedKey() {
		return clickedKey;
	}

	public void setClikedKey(String key) {
		this.clickedKey = key;
	}

	public HashMap<Rectangle, String> getPaintRectMap() {
		return this.paint_rect_map;
	}

	public HashMap<String, Integer> getData_map() {
		return data_map;
	}

	public void setData_map(HashMap<String, Integer> data_map) {
		this.data_map = data_map;
	}
	
}
