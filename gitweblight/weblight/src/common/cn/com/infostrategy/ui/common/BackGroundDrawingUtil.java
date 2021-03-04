package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import cn.com.infostrategy.ui.mdata.hmui.ninepatch.GraphicsUtilities;

/**
 * 背景处理工具,为所有UI调用
 * @author xch
 *
 */
public final class BackGroundDrawingUtil {
	public static final int SINGLE_COLOR_UNCHANGE = 0;
	public static final int HORIZONTAL_LEFT_TO_RIGHT = 1;
	public static final int HORIZONTAL_RIGHT_TO_LEFT = 2;
	public static final int HORIZONTAL_FROM_MIDDLE = 9;

	public static final int VERTICAL_TOP_TO_BOTTOM = 3; //从上到下
	public static final int VERTICAL_BOTTOM_TO_TOP = 4; //从下向上
	public static final int VERTICAL_LIGHT = 41; //上下呈发光型!
	public static final int VERTICAL_OUTSIDE_TO_MIDDLE = 42; //外面向中间变白!即Swing默认的按钮的效果!!
	public static final int VERTICAL_LIGHT2 = 43; //上下呈发光型!(第二种发光的效果)
	public static final int VERTICAL_TOP_TO_BOTTOM2 = 44; //
	public static final int VERTICAL_MIDDLE_TO_OUTSIDE = 45; //外面向中间变白!即Swing默认的按钮的效果!!

	public static final int INCLINE_NW_TO_SE = 5;
	public static final int INCLINE_NE_TO_SW = 6;
	public static final int INCLINE_SE_TO_NW = 7;
	public static final int INCLINE_SW_TO_NE = 8;

	//源RGB分量
	private float color_r_from;
	private float color_g_from;
	private float color_b_from;

	//目标RGB分量
	private float color_r_to;
	private float color_g_to;
	private float color_b_to;

	//RGB增量
	private float step_r;
	private float step_g;
	private float step_b;
	
	private BufferedImage bufImage = null; //此参数用于缓存画的背景图像，提高渲染性能，平台绘制这块性能还有很大空间提升

	/**
	 * 选择画法
	 * @param _g
	 * @param _rect
	 * @param _colorBegin
	 * @param _colorEnd
	 */
	public void draw(int _directType, Graphics _g, Rectangle _rect, Color _colorBegin, Color _colorEnd) {
		switch (_directType) {
		case HORIZONTAL_LEFT_TO_RIGHT: //从左向右
			horizontalDraw(_g, _rect, _colorBegin, _colorEnd);
			break;
		case HORIZONTAL_RIGHT_TO_LEFT: //从右向左
			horizontalDrawFromRightToLeft(_g, _rect, _colorBegin, _colorEnd);
			break;
		case HORIZONTAL_FROM_MIDDLE: //
			horizontalDrawFromMiddle(_g, _rect, _colorBegin, _colorEnd);
			break;
		case VERTICAL_TOP_TO_BOTTOM:
			verticalDraw(_g, _rect, _colorBegin, _colorEnd); //
			break;
		case VERTICAL_TOP_TO_BOTTOM2:
			verticalDraw2(_g, _rect, _colorBegin); //
			break;
		case VERTICAL_BOTTOM_TO_TOP:
			verticalDrawFromBottomToTop(_g, _rect, _colorBegin, _colorEnd); //
			break;
		case VERTICAL_LIGHT: //发光
			verticalDrawLight(_g, _rect, _colorBegin); //
			break;
		case VERTICAL_LIGHT2: //第二种发光!
			verticalDrawLight2(_g, _rect, _colorBegin); //
			break;
		case VERTICAL_OUTSIDE_TO_MIDDLE: //从外向里
			verticalDrawFromOutSideToCenter(_g, _rect, _colorBegin, _colorEnd); //
			break;
		case VERTICAL_MIDDLE_TO_OUTSIDE: //从里向外
			verticalDrawFromOutSideToCenter(_g, _rect, _colorEnd, _colorBegin); //
			break;
		case INCLINE_NW_TO_SE:
			inclineDraw_NW_SE(_g, _rect, _colorEnd, _colorBegin); //这是我写的,其实可以包含陈都写的!
			break;
		case INCLINE_SE_TO_NW:
			inclineDraw_NW_SE(_g, _rect, _colorBegin, _colorEnd); //这是陈都写的
			break;
		case SINGLE_COLOR_UNCHANGE:
			unchangeDraw(_g, _rect, _colorBegin);
			break;
		default:
			System.out.println("UI绘制方向不对，看看class WLTPanelUI！");
			break;
		}
	}

	/**
	 * 从左到右画
	 * @param _g
	 * @param _rect
	 * @param _color1
	 * @param _color2
	 */
	public synchronized void horizontalDraw(Graphics _g, Rectangle _rect, Color _colorFrom, Color _colorTo) {
		int li_start_x = (int) _rect.getX(); //
		int li_start_y = (int) _rect.getY(); //
		int li_width = (int) _rect.getSize().getWidth();
		int li_height = (int) _rect.getSize().getHeight();
		if (_colorFrom.equals(_colorTo)) {
			_g.setColor(_colorFrom); //
			_g.fillRect((int) _rect.getX(), (int) _rect.getY(), li_width, li_height); //
			return; //
		}
		GradientPaint paint = new GradientPaint(0, 0, _colorFrom, li_width, 0, _colorTo);
		Graphics2D g2d = (Graphics2D) _g;
		g2d.setPaint(paint);
		g2d.fillRect(li_start_x, li_start_y, li_start_x, li_start_y);
	}

	/**
	 * 从右到左画
	 * @param _g
	 * @param _rect
	 * @param _color1
	 * @param _color2
	 */
	public synchronized void horizontalDrawFromRightToLeft(Graphics _g, Rectangle _rect, Color _colorFrom, Color _colorTo) {
		int li_width = (int) _rect.getSize().getWidth();
		int li_height = (int) _rect.getSize().getHeight();
		if (_colorFrom.equals(_colorTo)) {
			_g.setColor(_colorFrom); //
			_g.fillRect((int) _rect.getX(), (int) _rect.getY(), li_width, li_height); //
			return; //
		}
		GradientPaint paint = new GradientPaint(0, 0, _colorTo, li_width, 0, _colorFrom);
		Graphics2D g2d = (Graphics2D) _g;
		g2d.setPaint(paint);
		g2d.fillRect(0, 0, li_width, li_height);
	}
	/**
	 * 从两边往中间画
	 * @param _g
	 * @param _rect
	 * @param _colorFrom
	 * @param _colorTo
	 */
	public synchronized void horizontalDrawFromMiddle(Graphics _g, Rectangle _rect, Color _colorFrom, Color _colorTo) {
		int li_width = (int) _rect.getSize().getWidth();
		int li_height = (int) _rect.getSize().getHeight();
		if (_colorFrom.equals(_colorTo)) {
			_g.setColor(_colorFrom); //
			_g.fillRect((int) _rect.getX(), (int) _rect.getY(), li_width, li_height); //
			return; //
		}
		if(bufImage == null || bufImage.getWidth()!=_rect.getWidth()){ //如果初始化，宽度变换了，重新绘制
			bufImage = GraphicsUtilities.createCompatibleImage(li_width, 1); //创建一个高度的渐变图像
			Graphics2D g2d = (Graphics2D) bufImage.getGraphics();//
			LinearGradientPaint paint = new LinearGradientPaint(0, 0, li_width, 0, new float[] { 0.0f, 0.5f, 0.8f }, new Color[] { _colorFrom, _colorTo, _colorFrom });
			g2d.setPaint(paint);
			g2d.fillRect(0, 0, li_width, 1);//填充一个高度
		}
		Graphics2D g2d = (Graphics2D) _g;
		g2d.drawImage(bufImage,(int)_rect.getX(),(int)_rect.getY(),li_width,li_height,null);//图像拉伸解决了 BillListPanel的快速滑动卡屏现象。郝明2013-5-23
	}

	/**
	 * 从上到下画
	 * @param _g
	 * @param _rect
	 * @param _color1
	 * @param _color2
	 */
	public synchronized void verticalDraw(Graphics _g, Rectangle _rect, Color _colorFrom, Color _colorTo) {
		int li_start_x = (int) _rect.getX(); //
		int li_start_y = (int) _rect.getY(); //
		int li_width = (int) _rect.getSize().getWidth();
		int li_height = (int) _rect.getSize().getHeight();
		if (_colorFrom.equals(_colorTo)) {
			_g.setColor(_colorTo); //
			_g.fillRect((int) _rect.getX(), (int) _rect.getY(), li_width, li_height); //
			return; //
		}
		GradientPaint paint = new GradientPaint(0, 0, _colorFrom, 0, li_height, _colorTo);
		Graphics2D g2d = (Graphics2D) _g;
		g2d.setPaint(paint);
		g2d.fillRect(0, 0, li_width, li_height);
	}

	/**
	 * 上下渐变,这种渐变与前一种渐变的区别在于,前一种是必须变到白色,而这一种则是减去一部分就可以了!! 比如像列表的表头,如果是真的变到白色则并不一定要看,而应该是变到一定程度就够了!
	 * @param _g
	 * @param _rect
	 * @param _colorFrom
	 * @param _colorTo
	 */
	public synchronized void verticalDraw2(Graphics _g, Rectangle _rect, Color _color1) {
		int li_start_x = (int) _rect.getX(); //
		int li_start_y = (int) _rect.getY(); //
		int li_width = (int) _rect.getSize().getWidth(); //
		int li_height = (int) _rect.getSize().getHeight(); //高度
		if (_color1.equals(Color.WHITE)) {
			_g.setColor(_color1); //
			_g.fillRect(li_start_x, li_start_y, li_width, li_height); //
			return; //
		}
		GradientPaint paint = new GradientPaint(0, 0, _color1, 0, li_height, _color1.brighter());
		Graphics2D g2d = (Graphics2D) _g;
		g2d.setPaint(paint);
		g2d.fillRect(0, 0, li_width, li_height);
	}

	/**
	 * 从下到上画
	 * @param _g
	 * @param _rect
	 * @param _color1
	 * @param _color2
	 */
	public synchronized void verticalDrawFromBottomToTop(Graphics _g, Rectangle _rect, Color _colorFrom, Color _colorTo) {
		int li_width = (int) _rect.getSize().getWidth();
		int li_height = (int) _rect.getSize().getHeight();
		if (_colorFrom.equals(_colorTo)) {
			_g.setColor(_colorFrom); //
			_g.fillRect((int) _rect.getX(), (int) _rect.getY(), li_width, li_height); //
			return; //
		}
		GradientPaint paint = new GradientPaint(0, li_height+(int) _rect.getY(), _colorFrom, 0, (int) _rect.getY(), _colorTo);
		Graphics2D g2d = (Graphics2D) _g;
		g2d.setPaint(paint);
		g2d.fillRect(0, (int) _rect.getY(), li_width, li_height);
	}

	/**
	 * 从外面向里面变
	 * @param _g
	 * @param _rect
	 * @param _colorFrom
	 * @param _colorTo
	 */
	public synchronized void verticalDrawFromOutSideToCenter(Graphics _g, Rectangle _rect, Color _color1, Color _color2) { //
		try {
			int li_start_x = (int) _rect.getX(); //
			int li_start_y = (int) _rect.getY(); //
			int li_width = (int) _rect.getSize().getWidth(); //
			int li_height = (int) _rect.getSize().getHeight(); //高度
			if (_color1.equals(_color2)) {
				_g.setColor(_color1); //
				_g.fillRect(li_start_x, li_start_y, li_width, li_height); //
				return; //
			}

			int li_scale = li_height / 2; //变的像素!!!
			float li_r_1 = _color1.getRed(); //
			float li_g_1 = _color1.getGreen(); //
			float li_b_1 = _color1.getBlue(); //
			float li_r_2 = _color2.getRed(); //
			float li_g_2 = _color2.getGreen(); //
			int li_b_2 = _color2.getBlue(); //
			float li_r_diff = Math.abs(li_r_1 - li_r_2); //
			float li_g_diff = Math.abs(li_g_1 - li_g_2); //
			float li_b_diff = Math.abs(li_b_1 - li_b_2); //
			float[] li_diffs = new float[] { li_r_diff, li_g_diff, li_b_diff }; //
			Arrays.sort(li_diffs); //
			int li_max_diff = (int) li_diffs[2]; //差几个颜色,取最大的!!!也就是循环几次!!
			int li_onecycle_width = li_scale / li_max_diff; //一次画的空间!
			if (li_onecycle_width <= 0) {
				li_onecycle_width = 1;
			}

			int li_drawcount = li_scale / li_onecycle_width; //
			float li_step_r = (float) (Math.abs(li_r_1 - li_r_2) / li_drawcount); //增步
			float li_step_g = (float) (Math.abs(li_g_1 - li_g_2) / li_drawcount); //
			float li_step_b = (float) (Math.abs(li_b_1 - li_b_2) / li_drawcount); //

			float li_r = li_r_1; //
			float li_g = li_g_1; //
			float li_b = li_b_1; //
			int li_pos_y = li_start_y; //
			int li_havedraw_area = 0; //
			while (li_havedraw_area < li_scale) { //只要还在范围之内
				_g.setColor(new Color(li_r / 255, li_g / 255, li_b / 255)); //要用浮点计算!!
				_g.fillRect(li_start_x, li_pos_y, li_width, li_onecycle_width); //
				li_havedraw_area = li_havedraw_area + li_onecycle_width; //累加!!!
				li_pos_y = li_start_y + li_havedraw_area; //累加
				if (li_r_1 > li_r_2) {
					li_r = li_r - li_step_r; //
					if (li_r < li_r_2) {
						li_r = li_r_2;
					}
				} else {
					li_r = li_r + li_step_r; //
					if (li_r > li_r_2) {
						li_r = li_r_2;
					}
				}
				if (li_g_1 > li_g_2) {
					li_g = li_g - li_step_g; //
					if (li_g < li_g_2) {
						li_g = li_g_2;
					}
				} else {
					li_g = li_g + li_step_g; //
					if (li_g > li_g_2) {
						li_g = li_g_2;
					}
				}
				if (li_b_1 > li_b_2) {
					li_b = li_b - li_step_b; //
					if (li_b < li_b_2) {
						li_b = li_b_2;
					}
				} else {
					li_b = li_b + li_step_b; //
					if (li_b > li_b_2) {
						li_b = li_b_2;
					}
				}
			}

			li_r = li_r_1; //
			li_g = li_g_1; //
			li_b = li_b_1; //
			li_pos_y = li_start_y + li_height; //
			li_havedraw_area = 0; //
			while (li_havedraw_area < li_scale) { //再来一次!!!
				_g.setColor(new Color(li_r / 255, li_g / 255, li_b / 255)); //要用浮点计算!!
				_g.fillRect(li_start_x, li_pos_y - li_onecycle_width, li_width, li_onecycle_width); //
				li_havedraw_area = li_havedraw_area + li_onecycle_width; //累加!!!
				li_pos_y = li_start_y + li_height - li_havedraw_area; //累加
				if (li_r_1 > li_r_2) {
					li_r = li_r - li_step_r; //
					if (li_r < li_r_2) {
						li_r = li_r_2;
					}
				} else {
					li_r = li_r + li_step_r; //
					if (li_r > li_r_2) {
						li_r = li_r_2;
					}
				}
				if (li_g_1 > li_g_2) {
					li_g = li_g - li_step_g; //
					if (li_g < li_g_2) {
						li_g = li_g_2;
					}
				} else {
					li_g = li_g + li_step_g; //
					if (li_g > li_g_2) {
						li_g = li_g_2;
					}
				}
				if (li_b_1 > li_b_2) {
					li_b = li_b - li_step_b; //
					if (li_b < li_b_2) {
						li_b = li_b_2;
					}
				} else {
					li_b = li_b + li_step_b; //
					if (li_b > li_b_2) {
						li_b = li_b_2;
					}
				}

			}

			int li_model = li_height % 2; //
			if (li_model != 0) { //如果有余
				_g.setColor(_color2); //要用浮点计算!!
				_g.fillRect(li_start_x, li_start_y + (li_height / 2), li_width, 1); //
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * 纵向画光!
	 * @param _g
	 * @param _rect
	 * @param _colorFrom
	 */
	public synchronized void verticalDrawLight(Graphics _g, Rectangle _rect, Color _colorFrom) {
		try {
			int li_width = (int) _rect.getSize().getWidth(); //
			int li_height = (int) _rect.getSize().getHeight(); //
			if (_colorFrom.equals(Color.WHITE)) {
				_g.setColor(_colorFrom); //
				_g.fillRect((int) _rect.getX(), (int) _rect.getY(), li_width, li_height); //
				return; //
			}

			int[] li_fromcolor = new int[] { _colorFrom.getRed(), _colorFrom.getGreen(), _colorFrom.getBlue() }; // 3, 26, 61
			int[][] li_delcolors = getDelColors(li_fromcolor, li_height); //差额
			for (int i = 0; i < li_delcolors.length; i++) { //区域!
				int li_r = li_fromcolor[0] + li_delcolors[i][0]; //
				int li_g = li_fromcolor[1] + li_delcolors[i][1]; //
				int li_b = li_fromcolor[2] + li_delcolors[i][2]; //
				if (li_r > 255) {
					li_r = 255;
				}
				if (li_g > 255) {
					li_g = 255;
				}
				if (li_b > 255) {
					li_b = 255;
				}
				_g.setColor(new Color(li_r, li_g, li_b)); //设置颜色!!
				_g.fillRect(0, i, li_width, 1); //填充颜色
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * 第二种发光的算法,即中铁建的效果! 前面的发光效果太过于刺眼!! 在总体风格为清淡的情况下实际效果并不好!!!
	 * @param _g
	 * @param _rect
	 * @param _colorFrom
	 */
	public synchronized void verticalDrawLight2(Graphics _g, Rectangle _rect, Color _color1) {
		try {
			Color _color2 = Color.WHITE; //
			int li_start_x = (int) _rect.getX(); //
			int li_start_y = (int) _rect.getY(); //
			int li_width = (int) _rect.getSize().getWidth(); //
			int li_height = (int) _rect.getSize().getHeight(); //高度
			if (_color1.equals(_color2)) {
				_g.setColor(_color2); //
				_g.fillRect(li_start_x, li_start_y, li_width, li_height); //
				return; //
			}
			int li_scale = li_height / 2; //变的像素!!!
			float li_r_1 = _color1.getRed(); //
			float li_g_1 = _color1.getGreen(); //
			float li_b_1 = _color1.getBlue(); //
			float li_r_2 = _color2.getRed(); //
			float li_g_2 = _color2.getGreen(); //
			float li_b_2 = _color2.getBlue(); //
			float li_step_r = (float) (Math.abs(li_r_2 - li_r_1) / 50); //增步!
			float li_step_g = (float) (Math.abs(li_g_2 - li_g_1) / 50); //增步!
			float li_step_b = (float) (Math.abs(li_b_2 - li_b_1) / 50); //增步!
			if (li_step_r <= 0) {
				li_step_r = 1;
			}
			if (li_step_g <= 0) {
				li_step_g = 1;
			}
			if (li_step_b <= 0) {
				li_step_b = 1;
			}

			float li_r = li_r_1 + li_step_r * 12; //
			float li_g = li_g_1 + li_step_g * 12; //
			float li_b = li_b_1 + li_step_b * 12; //
			if (li_r > li_r_2) {
				li_r = li_r_2;
			}
			if (li_g > li_g_2) {
				li_g = li_g_2;
			}
			if (li_b > li_b_2) {
				li_b = li_b_2;
			}
			int li_pos_y = li_start_y + li_scale; //
			int li_havedraw_area = 0; //
			while (li_havedraw_area < li_scale) { //只要还在范围之内
				_g.setColor(new Color(li_r / 255, li_g / 255, li_b / 255)); //要用浮点计算!!
				_g.fillRect(li_start_x, li_pos_y - 1, li_width, 1); //
				li_havedraw_area = li_havedraw_area + 1; //累加!!!
				li_pos_y = li_pos_y - 1; //累加
				li_r = (float) (li_r + li_step_r * 1.2); //
				if (li_r > li_r_2) {
					li_r = li_r_2;
				}
				li_g = (float) (li_g + li_step_g * 1.2); //
				if (li_g > li_g_2) {
					li_g = li_g_2;
				}
				li_b = (float) (li_b + li_step_b * 1.2); //
				if (li_b > li_b_2) {
					li_b = li_b_2;
				}
			}

			li_r = li_r_1; //
			li_g = li_g_1; //
			li_b = li_b_1; //
			li_pos_y = li_start_y + li_scale; //
			li_havedraw_area = 0; //
			while (li_havedraw_area < li_scale) { //只要还在范围之内
				_g.setColor(new Color(li_r / 255, li_g / 255, li_b / 255)); //要用浮点计算!!
				_g.fillRect(li_start_x, li_pos_y, li_width, 1); //
				li_havedraw_area = li_havedraw_area + 1; //累加!!!
				li_pos_y = li_pos_y + 1; //累加
				li_r = (float) (li_r + li_step_r * 1.5); //
				if (li_r > li_r_2) {
					li_r = li_r_2;
				}
				li_g = (float) (li_g + li_step_g * 1.5); //
				if (li_g > li_g_2) {
					li_g = li_g_2;
				}
				li_b = (float) (li_b + li_step_b * 1.5); //
				if (li_b > li_b_2) {
					li_b = li_b_2;
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * 从左上角向右下角画,以前陈都写了一个,但因为有bug而且不通用(两个颜色互调后就没效果了),所以我重写了一个!!
	 * @param _g
	 * @param _rect
	 * @param _color1
	 * @param _color2
	 */
	public synchronized void inclineDraw_NW_SE(Graphics _g, Rectangle _rect, Color _color1, Color _color2) {
		try {
			int li_start_x = (int) _rect.getX(); //一定要取得起始位置!!因为在有滚动框时启始位置并不是0!!!
			int li_start_y = (int) _rect.getY(); //
			int li_width = (int) _rect.getWidth(); //宽度
			int li_height = (int) _rect.getHeight(); //高度
			if (_color1.equals(_color2)) { //如果两个颜色一样
				_g.setColor(_color1); //
				_g.fillRect(li_start_x, li_start_y, li_width, li_height); //
				return; //
			}
			GradientPaint paint = new GradientPaint(li_start_x, li_start_y, _color1, li_width, li_height, _color2);
			Graphics2D g2d = (Graphics2D) _g;
			g2d.setPaint(paint);
			g2d.fillRect(li_start_x, li_start_y, li_width, li_height);
		} catch (Exception e) {
			e.printStackTrace(); //
		}
		//System.out.println("开始的位置[" + li_start_x + "," + li_start_y + "],总宽度=[" + li_scale + "],颜色最大差异[" + li_max_diff + "],理论循环次数[" + li_max_diff + "],一次画的空间[" + li_onecycle_width + "],实际循环了[" + li_cycle_count + "]次!!"); ////
	}

	/**
	 * 单一颜色无变化
	 * @param _g
	 * @param _rect
	 * @param _color1
	 */
	public synchronized void unchangeDraw(Graphics _g, Rectangle _rect, Color _color1) {
		int li_start_x = (int) _rect.getX(); //
		int li_start_y = (int) _rect.getY(); //
		int li_width = (int) _rect.getSize().getWidth(); //
		int li_height = (int) _rect.getSize().getHeight(); //高度
		_g.setColor(_color1);
		_g.fillRect(li_start_x, li_start_y, li_width, li_height);
	}

	/**
	 * 发光的算法,其实就是上半部是白色渐变,下半部是灰色渐变!
	 * @param _fromColor
	 * @param _height
	 * @return
	 */
	private int[][] getDelColors(int[] _fromColor, int _height) {
		//System.out.println("高度=["+_height+"]");  //
		int[][] delColor = new int[_height][3]; //22/2=11,0-10,11-21,如果是23,则23/2=11,0-10,11-20
		int li_half = _height / 2; //一半!
		if (li_half == 0) {
			li_half = 1;
		}
		int li_d1 = 100 / li_half; //如果是10,则递减10,如果是100,则递减1
		if (li_d1 == 0) {
			li_d1 = 1;
		}
		int li_prefix1 = 60; //
		for (int i = li_half - 1; i >= 0; i--) { //
			delColor[i][0] = li_prefix1 + (li_half - 1 - i) * li_d1;
			delColor[i][1] = li_prefix1 + (li_half - 1 - i) * li_d1;
			delColor[i][2] = li_prefix1 + (li_half - 1 - i) * li_d1;
		}

		int li_d2 = (int) ((100 / li_half) * 0.7); ////
		if (li_d2 == 0) {
			li_d2 = 1;
		}
		int li_prefix2 = 0; //
		for (int i = li_half; i < _height; i++) {
			delColor[i][0] = li_prefix2 + (i - li_half) * li_d2; //
			delColor[i][1] = li_prefix2 + (i - li_half) * li_d2; //
			delColor[i][2] = li_prefix2 + (i - li_half) * li_d2; //
		}
		//System.out.println("上半部递减[" + li_d1 + "],下半部递减[" + li_d2 + "]"); //
		return delColor;
	}

	private void initColorValueToRGB(Color _colorFrom, Color _colorTo, int totalStep) {
		//源RGB分量
		color_r_from = _colorFrom.getRed();
		color_g_from = _colorFrom.getGreen();
		color_b_from = _colorFrom.getBlue();

		//目标RGB分量
		color_r_to = _colorTo.getRed();
		color_g_to = _colorTo.getGreen();
		color_b_to = _colorTo.getBlue();

		//RGB增量
		step_r = (color_r_from == color_r_to ? 0 : (float) (color_r_to - color_r_from)) / totalStep;
		step_g = (color_g_from == color_g_to ? 0 : (float) (color_g_to - color_g_from)) / totalStep;
		step_b = (color_b_from == color_b_to ? 0 : (float) (color_b_to - color_b_from)) / totalStep;
	}

	private void calculateNewColorRGB() {
		if (color_r_from < color_r_to) {
			color_r_from = color_r_from + step_r >= color_r_to ? color_r_to : color_r_from + step_r;
		} else {
			color_r_from = color_r_from + step_r <= color_r_to ? color_r_to : color_r_from + step_r;
		}

		if (color_g_from < color_g_to) {
			color_g_from = color_g_from + step_g >= color_g_to ? color_g_to : color_g_from + step_g;
		} else {
			color_g_from = color_g_from + step_g <= color_g_to ? color_g_to : color_g_from + step_g;
		}

		if (color_b_from < color_b_to) {
			color_b_from = color_b_from + step_b >= color_b_to ? color_b_to : color_b_from + step_b;
		} else {
			color_b_from = color_b_from + step_b <= color_b_to ? color_b_to : color_b_from + step_b;
		}
	}

}
