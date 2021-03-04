package cn.com.infostrategy.ui.common;

import java.awt.Color;

import cn.com.infostrategy.ui.mdata.hmui.I_ButtonUI;

public class WLTButtonUI extends I_ButtonUI {

	public WLTButtonUI() {
		this(BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM); //默认是
	}

	public WLTButtonUI(int _directType) {
		this(_directType, Color.WHITE);
	}

	/**
	 * @param int   _direct   指定绘制的方向
	 * @param Color _colorEnd 指定颜色的变化目标（从原本的背景色渐变到参数指定的颜色）
	 */
	public WLTButtonUI(int _directType, Color _colorEnd) {
		super(_directType, _colorEnd);
	}

}
