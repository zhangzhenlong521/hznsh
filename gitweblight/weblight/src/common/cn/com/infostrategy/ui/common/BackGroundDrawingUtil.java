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
 * ����������,Ϊ����UI����
 * @author xch
 *
 */
public final class BackGroundDrawingUtil {
	public static final int SINGLE_COLOR_UNCHANGE = 0;
	public static final int HORIZONTAL_LEFT_TO_RIGHT = 1;
	public static final int HORIZONTAL_RIGHT_TO_LEFT = 2;
	public static final int HORIZONTAL_FROM_MIDDLE = 9;

	public static final int VERTICAL_TOP_TO_BOTTOM = 3; //���ϵ���
	public static final int VERTICAL_BOTTOM_TO_TOP = 4; //��������
	public static final int VERTICAL_LIGHT = 41; //���³ʷ�����!
	public static final int VERTICAL_OUTSIDE_TO_MIDDLE = 42; //�������м���!��SwingĬ�ϵİ�ť��Ч��!!
	public static final int VERTICAL_LIGHT2 = 43; //���³ʷ�����!(�ڶ��ַ����Ч��)
	public static final int VERTICAL_TOP_TO_BOTTOM2 = 44; //
	public static final int VERTICAL_MIDDLE_TO_OUTSIDE = 45; //�������м���!��SwingĬ�ϵİ�ť��Ч��!!

	public static final int INCLINE_NW_TO_SE = 5;
	public static final int INCLINE_NE_TO_SW = 6;
	public static final int INCLINE_SE_TO_NW = 7;
	public static final int INCLINE_SW_TO_NE = 8;

	//ԴRGB����
	private float color_r_from;
	private float color_g_from;
	private float color_b_from;

	//Ŀ��RGB����
	private float color_r_to;
	private float color_g_to;
	private float color_b_to;

	//RGB����
	private float step_r;
	private float step_g;
	private float step_b;
	
	private BufferedImage bufImage = null; //�˲������ڻ��滭�ı���ͼ�������Ⱦ���ܣ�ƽ̨����������ܻ��кܴ�ռ�����

	/**
	 * ѡ�񻭷�
	 * @param _g
	 * @param _rect
	 * @param _colorBegin
	 * @param _colorEnd
	 */
	public void draw(int _directType, Graphics _g, Rectangle _rect, Color _colorBegin, Color _colorEnd) {
		switch (_directType) {
		case HORIZONTAL_LEFT_TO_RIGHT: //��������
			horizontalDraw(_g, _rect, _colorBegin, _colorEnd);
			break;
		case HORIZONTAL_RIGHT_TO_LEFT: //��������
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
		case VERTICAL_LIGHT: //����
			verticalDrawLight(_g, _rect, _colorBegin); //
			break;
		case VERTICAL_LIGHT2: //�ڶ��ַ���!
			verticalDrawLight2(_g, _rect, _colorBegin); //
			break;
		case VERTICAL_OUTSIDE_TO_MIDDLE: //��������
			verticalDrawFromOutSideToCenter(_g, _rect, _colorBegin, _colorEnd); //
			break;
		case VERTICAL_MIDDLE_TO_OUTSIDE: //��������
			verticalDrawFromOutSideToCenter(_g, _rect, _colorEnd, _colorBegin); //
			break;
		case INCLINE_NW_TO_SE:
			inclineDraw_NW_SE(_g, _rect, _colorEnd, _colorBegin); //������д��,��ʵ���԰����¶�д��!
			break;
		case INCLINE_SE_TO_NW:
			inclineDraw_NW_SE(_g, _rect, _colorBegin, _colorEnd); //���ǳ¶�д��
			break;
		case SINGLE_COLOR_UNCHANGE:
			unchangeDraw(_g, _rect, _colorBegin);
			break;
		default:
			System.out.println("UI���Ʒ��򲻶ԣ�����class WLTPanelUI��");
			break;
		}
	}

	/**
	 * �����һ�
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
	 * ���ҵ���
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
	 * ���������м仭
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
		if(bufImage == null || bufImage.getWidth()!=_rect.getWidth()){ //�����ʼ������ȱ任�ˣ����»���
			bufImage = GraphicsUtilities.createCompatibleImage(li_width, 1); //����һ���߶ȵĽ���ͼ��
			Graphics2D g2d = (Graphics2D) bufImage.getGraphics();//
			LinearGradientPaint paint = new LinearGradientPaint(0, 0, li_width, 0, new float[] { 0.0f, 0.5f, 0.8f }, new Color[] { _colorFrom, _colorTo, _colorFrom });
			g2d.setPaint(paint);
			g2d.fillRect(0, 0, li_width, 1);//���һ���߶�
		}
		Graphics2D g2d = (Graphics2D) _g;
		g2d.drawImage(bufImage,(int)_rect.getX(),(int)_rect.getY(),li_width,li_height,null);//ͼ���������� BillListPanel�Ŀ��ٻ����������󡣺���2013-5-23
	}

	/**
	 * ���ϵ��»�
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
	 * ���½���,���ֽ�����ǰһ�ֽ������������,ǰһ���Ǳ���䵽��ɫ,����һ�����Ǽ�ȥһ���־Ϳ�����!! �������б�ı�ͷ,�������ı䵽��ɫ�򲢲�һ��Ҫ��,��Ӧ���Ǳ䵽һ���̶Ⱦ͹���!
	 * @param _g
	 * @param _rect
	 * @param _colorFrom
	 * @param _colorTo
	 */
	public synchronized void verticalDraw2(Graphics _g, Rectangle _rect, Color _color1) {
		int li_start_x = (int) _rect.getX(); //
		int li_start_y = (int) _rect.getY(); //
		int li_width = (int) _rect.getSize().getWidth(); //
		int li_height = (int) _rect.getSize().getHeight(); //�߶�
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
	 * ���µ��ϻ�
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
	 * �������������
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
			int li_height = (int) _rect.getSize().getHeight(); //�߶�
			if (_color1.equals(_color2)) {
				_g.setColor(_color1); //
				_g.fillRect(li_start_x, li_start_y, li_width, li_height); //
				return; //
			}

			int li_scale = li_height / 2; //�������!!!
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
			int li_max_diff = (int) li_diffs[2]; //�����ɫ,ȡ����!!!Ҳ����ѭ������!!
			int li_onecycle_width = li_scale / li_max_diff; //һ�λ��Ŀռ�!
			if (li_onecycle_width <= 0) {
				li_onecycle_width = 1;
			}

			int li_drawcount = li_scale / li_onecycle_width; //
			float li_step_r = (float) (Math.abs(li_r_1 - li_r_2) / li_drawcount); //����
			float li_step_g = (float) (Math.abs(li_g_1 - li_g_2) / li_drawcount); //
			float li_step_b = (float) (Math.abs(li_b_1 - li_b_2) / li_drawcount); //

			float li_r = li_r_1; //
			float li_g = li_g_1; //
			float li_b = li_b_1; //
			int li_pos_y = li_start_y; //
			int li_havedraw_area = 0; //
			while (li_havedraw_area < li_scale) { //ֻҪ���ڷ�Χ֮��
				_g.setColor(new Color(li_r / 255, li_g / 255, li_b / 255)); //Ҫ�ø������!!
				_g.fillRect(li_start_x, li_pos_y, li_width, li_onecycle_width); //
				li_havedraw_area = li_havedraw_area + li_onecycle_width; //�ۼ�!!!
				li_pos_y = li_start_y + li_havedraw_area; //�ۼ�
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
			while (li_havedraw_area < li_scale) { //����һ��!!!
				_g.setColor(new Color(li_r / 255, li_g / 255, li_b / 255)); //Ҫ�ø������!!
				_g.fillRect(li_start_x, li_pos_y - li_onecycle_width, li_width, li_onecycle_width); //
				li_havedraw_area = li_havedraw_area + li_onecycle_width; //�ۼ�!!!
				li_pos_y = li_start_y + li_height - li_havedraw_area; //�ۼ�
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
			if (li_model != 0) { //�������
				_g.setColor(_color2); //Ҫ�ø������!!
				_g.fillRect(li_start_x, li_start_y + (li_height / 2), li_width, 1); //
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * ���򻭹�!
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
			int[][] li_delcolors = getDelColors(li_fromcolor, li_height); //���
			for (int i = 0; i < li_delcolors.length; i++) { //����!
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
				_g.setColor(new Color(li_r, li_g, li_b)); //������ɫ!!
				_g.fillRect(0, i, li_width, 1); //�����ɫ
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * �ڶ��ַ�����㷨,����������Ч��! ǰ��ķ���Ч��̫���ڴ���!! ��������Ϊ�嵭�������ʵ��Ч��������!!!
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
			int li_height = (int) _rect.getSize().getHeight(); //�߶�
			if (_color1.equals(_color2)) {
				_g.setColor(_color2); //
				_g.fillRect(li_start_x, li_start_y, li_width, li_height); //
				return; //
			}
			int li_scale = li_height / 2; //�������!!!
			float li_r_1 = _color1.getRed(); //
			float li_g_1 = _color1.getGreen(); //
			float li_b_1 = _color1.getBlue(); //
			float li_r_2 = _color2.getRed(); //
			float li_g_2 = _color2.getGreen(); //
			float li_b_2 = _color2.getBlue(); //
			float li_step_r = (float) (Math.abs(li_r_2 - li_r_1) / 50); //����!
			float li_step_g = (float) (Math.abs(li_g_2 - li_g_1) / 50); //����!
			float li_step_b = (float) (Math.abs(li_b_2 - li_b_1) / 50); //����!
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
			while (li_havedraw_area < li_scale) { //ֻҪ���ڷ�Χ֮��
				_g.setColor(new Color(li_r / 255, li_g / 255, li_b / 255)); //Ҫ�ø������!!
				_g.fillRect(li_start_x, li_pos_y - 1, li_width, 1); //
				li_havedraw_area = li_havedraw_area + 1; //�ۼ�!!!
				li_pos_y = li_pos_y - 1; //�ۼ�
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
			while (li_havedraw_area < li_scale) { //ֻҪ���ڷ�Χ֮��
				_g.setColor(new Color(li_r / 255, li_g / 255, li_b / 255)); //Ҫ�ø������!!
				_g.fillRect(li_start_x, li_pos_y, li_width, 1); //
				li_havedraw_area = li_havedraw_area + 1; //�ۼ�!!!
				li_pos_y = li_pos_y + 1; //�ۼ�
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
	 * �����Ͻ������½ǻ�,��ǰ�¶�д��һ��,����Ϊ��bug���Ҳ�ͨ��(������ɫ�������ûЧ����),��������д��һ��!!
	 * @param _g
	 * @param _rect
	 * @param _color1
	 * @param _color2
	 */
	public synchronized void inclineDraw_NW_SE(Graphics _g, Rectangle _rect, Color _color1, Color _color2) {
		try {
			int li_start_x = (int) _rect.getX(); //һ��Ҫȡ����ʼλ��!!��Ϊ���й�����ʱ��ʼλ�ò�����0!!!
			int li_start_y = (int) _rect.getY(); //
			int li_width = (int) _rect.getWidth(); //���
			int li_height = (int) _rect.getHeight(); //�߶�
			if (_color1.equals(_color2)) { //���������ɫһ��
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
		//System.out.println("��ʼ��λ��[" + li_start_x + "," + li_start_y + "],�ܿ��=[" + li_scale + "],��ɫ������[" + li_max_diff + "],����ѭ������[" + li_max_diff + "],һ�λ��Ŀռ�[" + li_onecycle_width + "],ʵ��ѭ����[" + li_cycle_count + "]��!!"); ////
	}

	/**
	 * ��һ��ɫ�ޱ仯
	 * @param _g
	 * @param _rect
	 * @param _color1
	 */
	public synchronized void unchangeDraw(Graphics _g, Rectangle _rect, Color _color1) {
		int li_start_x = (int) _rect.getX(); //
		int li_start_y = (int) _rect.getY(); //
		int li_width = (int) _rect.getSize().getWidth(); //
		int li_height = (int) _rect.getSize().getHeight(); //�߶�
		_g.setColor(_color1);
		_g.fillRect(li_start_x, li_start_y, li_width, li_height);
	}

	/**
	 * ������㷨,��ʵ�����ϰ벿�ǰ�ɫ����,�°벿�ǻ�ɫ����!
	 * @param _fromColor
	 * @param _height
	 * @return
	 */
	private int[][] getDelColors(int[] _fromColor, int _height) {
		//System.out.println("�߶�=["+_height+"]");  //
		int[][] delColor = new int[_height][3]; //22/2=11,0-10,11-21,�����23,��23/2=11,0-10,11-20
		int li_half = _height / 2; //һ��!
		if (li_half == 0) {
			li_half = 1;
		}
		int li_d1 = 100 / li_half; //�����10,��ݼ�10,�����100,��ݼ�1
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
		//System.out.println("�ϰ벿�ݼ�[" + li_d1 + "],�°벿�ݼ�[" + li_d2 + "]"); //
		return delColor;
	}

	private void initColorValueToRGB(Color _colorFrom, Color _colorTo, int totalStep) {
		//ԴRGB����
		color_r_from = _colorFrom.getRed();
		color_g_from = _colorFrom.getGreen();
		color_b_from = _colorFrom.getBlue();

		//Ŀ��RGB����
		color_r_to = _colorTo.getRed();
		color_g_to = _colorTo.getGreen();
		color_b_to = _colorTo.getBlue();

		//RGB����
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
