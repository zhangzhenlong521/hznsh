package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * JPanel的UI,实现渐变效果
 * @author xch
 *
 */
public class WLTPanelUI extends BasicPanelUI {

	public static final int HORIZONTAL_LEFT_TO_RIGHT = BackGroundDrawingUtil.HORIZONTAL_LEFT_TO_RIGHT;
	public static final int HORIZONTAL_FROM_MIDDLE = BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE;
	public static final int INCLINE_NW_TO_SE = BackGroundDrawingUtil.INCLINE_NW_TO_SE;
	public static final int INCLINE_SE_TO_NW = BackGroundDrawingUtil.INCLINE_SE_TO_NW;

	private int directType = 0;
	private boolean isDynamicChange = true; //是否动态变化,即后来李燕杰做了个效果,随着页面宽度与高度变化,动态改变渐变效果!! 默认是变化的!
	//private Color colorBegin = null;
	private Color colorEnd = null;
	private boolean isViewRect = true; //

	private BackGroundDrawingUtil drawutil = new BackGroundDrawingUtil(); //

	public WLTPanelUI() {
		this(WLTPanelUI.INCLINE_SE_TO_NW, LookAndFeel.defaultShadeColor1, true);
	}

	public WLTPanelUI(int _direct) {
		this(_direct, Color.WHITE, false); //
	}

	public WLTPanelUI(int _direct, boolean _isDynamicChange) {
		this(_direct, Color.WHITE, _isDynamicChange); //
	}

	/**
	 * @param int   _direct   指定绘制的方向
	 * @param Color _colorEnd 指定颜色的变化目标（从原本的背景色渐变到参数指定的颜色）
	 */
	public WLTPanelUI(int _direct, Color _colorEnd, boolean _isDynamicChange) {
		this(_direct, _colorEnd, _isDynamicChange, true); //
	}

	public WLTPanelUI(int _direct, Color _colorEnd, boolean _isDynamicChange, boolean _isViewRect) {
		super();
		this.directType = _direct;
		this.colorEnd = _colorEnd;
		this.isDynamicChange = _isDynamicChange; //是否动态变化效果
		this.isViewRect = _isViewRect; //
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		Color colorBegin = c.getBackground(); //
		Rectangle rect = null; //
		if (isViewRect) {
			rect = c.getVisibleRect(); //一定要拿显示区域!!效果才好!!!
		} else {
			rect = c.getBounds(); //
		}
		if (isDynamicChange) { //如果需要动态显示效果!!!
			int panelWidth = (int) rect.getWidth(); //
			int panelHeight = (int) rect.getHeight(); //
			if (panelHeight > panelWidth * 1.2) {
				directType = BackGroundDrawingUtil.HORIZONTAL_RIGHT_TO_LEFT; //
			} else {
				if (panelWidth > panelHeight * 1.2) {
					directType = BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE; //
				} else {
					directType = BackGroundDrawingUtil.INCLINE_NW_TO_SE;
				}
			}
		}
		drawutil.draw(directType, g, rect, colorBegin, colorEnd); //
	}

}
