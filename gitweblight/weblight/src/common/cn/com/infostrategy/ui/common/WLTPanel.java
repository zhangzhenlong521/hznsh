package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * 可以实现渐变效果的Panel,同时可以实现透明!即两种都有!
 * 
 * @author xch
 *
 */
public class WLTPanel extends JPanel {
	public static final int SINGLE_COLOR_UNCHANGE = BackGroundDrawingUtil.SINGLE_COLOR_UNCHANGE;
	public static final int HORIZONTAL_LEFT_TO_RIGHT = BackGroundDrawingUtil.HORIZONTAL_LEFT_TO_RIGHT;
	public static final int HORIZONTAL_RIGHT_TO_LEFT = BackGroundDrawingUtil.HORIZONTAL_RIGHT_TO_LEFT;
	public static final int VERTICAL_TOP_TO_BOTTOM = BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM; //从上到下
	public static final int VERTICAL_TOP_TO_BOTTOM2 = BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM2; //从上到下
	public static final int VERTICAL_BOTTOM_TO_TOP = BackGroundDrawingUtil.VERTICAL_BOTTOM_TO_TOP; //从下向上
	public static final int VERTICAL_LIGHT = BackGroundDrawingUtil.VERTICAL_LIGHT; //从下向上发光!!
	public static final int VERTICAL_LIGHT2 = BackGroundDrawingUtil.VERTICAL_LIGHT2; //从下向上发光!!
	public static final int VERTICAL_OUTSIDE_TO_MIDDLE = BackGroundDrawingUtil.VERTICAL_OUTSIDE_TO_MIDDLE; //外面向中间变!!

	public static final int INCLINE_NW_TO_SE = BackGroundDrawingUtil.INCLINE_NW_TO_SE;
	public static final int INCLINE_NE_TO_SW = BackGroundDrawingUtil.INCLINE_NE_TO_SW;
	public static final int INCLINE_SE_TO_NW = BackGroundDrawingUtil.INCLINE_SE_TO_NW;
	public static final int INCLINE_SW_TO_NE = BackGroundDrawingUtil.INCLINE_SW_TO_NE;
	public static final int HORIZONTAL_FROM_MIDDLE = BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE;

	private static final long serialVersionUID = 1L;
	private boolean isCrystal = false; //是否透明,默认是不透明!!setOpaque
	private int shadowType = 0; //单色：0； 渐变样式,1,2,3,4,5,6，定义参见class cn.com.infostrategy.ui.common.BackGroundDrawingUtil
	private Color colorTo = Color.WHITE; //如果有渐变渲染，那么默认方式是从容器背景色向colorTo变量指定的颜色变化

	private boolean isDynamicChange = true; //是否动态变化,即后来李燕杰做了个效果,随着页面宽度与高度变化,动态改变渐变效果!! 默认是变化的!
	private boolean isViewRect = true; //

	/**
	 * 透明面板，无布局
	 */
	public WLTPanel() {
		this.isCrystal = true; //是否透明=Y
		this.setOpaque(!isCrystal);
	}

	/**
	 * 透明面板,有布局
	 * @param layout  面板布局类型
	 */
	public WLTPanel(LayoutManager layout) {
		super(layout);
		this.isCrystal = true; //
		this.setOpaque(!isCrystal); //
	}

	/**
	 * 不透明面板（无布局），背景色渐变渲染
	 * @param _shadowType  颜色渐变类型
	 */
	public WLTPanel(int _shadowType) {
		super();
		this.shadowType = _shadowType;
		initialize();
	}

	/**
	 * 不透明面板（无布局），背景色渐变渲染
	 * @param _shadowType  颜色渐变类型
	 * @param _bgColor      背景色
	 */
	public WLTPanel(int _shadowType, Color _bgColor) {
		super();
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		initialize();
	}

	public WLTPanel(int _shadowType, Color _bgColor, boolean _isDynamicChange) {
		super();
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		this.isDynamicChange = _isDynamicChange; //
		initialize();
	}

	/**
	 * 不透明面板（有布局），背景色渐变渲染
	 * @param _shadowType  颜色渐变类型
	 * @param layout       面板布局类型
	 */
	public WLTPanel(int _shadowType, LayoutManager layout) {
		super(layout);
		this.shadowType = _shadowType;
		initialize();
	}

	public WLTPanel(int _shadowType, LayoutManager layout, boolean _isDynamicChange) {
		super(layout);
		this.shadowType = _shadowType;
		this.isDynamicChange = _isDynamicChange; //
		initialize();
	}

	public WLTPanel(int _shadowType, LayoutManager layout, Color _bgColor, boolean _isDynamicChange) {
		super(layout);
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		this.isDynamicChange = _isDynamicChange; //
		initialize();
	}

	public WLTPanel(int _shadowType, LayoutManager layout, Color _bgColor, boolean _isDynamicChange, boolean _isViewRect) {
		super(layout);
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		this.isDynamicChange = _isDynamicChange; //
		this.isViewRect = _isViewRect; //
		initialize();
	}

	/**
	 * 不透明面板（有布局），背景色渐变渲染
	 * @param _shadowType   颜色渐变类型
	 * @param layout        面板布局类型
	 * @param _bgColor      背景色
	 */
	public WLTPanel(int _shadowType, LayoutManager layout, Color _bgColor) {
		super(layout);
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		initialize();
	}

	/**
	 * 不透明面板（有布局），背景色渐变渲染，从_bgColor渐变到_colorTo
	 * @param _shadowType   颜色渐变类型
	 * @param layout        面板布局类型
	 * @param _bgColor      背景色
	 * @param _colorTo      渲染颜色的终值
	 */
	public WLTPanel(int _shadowType, LayoutManager layout, Color _bgColor, Color _colorTo) {
		super(layout);
		this.shadowType = _shadowType;
		this.setBackground(_bgColor);
		this.colorTo = _colorTo;
		initialize();
	}

	private void initialize() {
		this.setOpaque(!isCrystal);
		//assert shadowType >= BackGroundDrawingUtil.SINGLE_COLOR_UNCHANGE && shadowType <= BackGroundDrawingUtil.INCLINE_SW_TO_NE : "参数值越界！";
		if (!isCrystal && shadowType > 0) {//如果不是透明面板并且设置了渐变类型，那么设置UI
			this.setUI(new WLTPanelUI(shadowType, colorTo, isDynamicChange, isViewRect));
		}
	}

	public static JPanel createDefaultPanel() {
		WLTPanel panel = new WLTPanel(WLTPanel.INCLINE_SE_TO_NW, LookAndFeel.defaultShadeColor1); //
		return panel; //
	}

	public static JPanel createDefaultPanel(LayoutManager _layOut) {
		WLTPanel panel = new WLTPanel(WLTPanel.INCLINE_SE_TO_NW, _layOut, LookAndFeel.defaultShadeColor1); //
		return panel; //
	}

	public static JPanel createDefaultPanel(LayoutManager _layOut, int _shadowType) {
		WLTPanel panel = new WLTPanel(_shadowType, _layOut, LookAndFeel.defaultShadeColor1, false); //
		return panel; //
	}

	@Override
	public void setBackground(Color bg) {
		if (isCrystal) { //如果是透明背景，则不允许设置背景色
			return;
		}
		super.setBackground(bg);
	}

}
