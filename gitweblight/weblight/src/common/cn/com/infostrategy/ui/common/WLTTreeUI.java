package cn.com.infostrategy.ui.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.plaf.metal.MetalTreeUI;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;

/**
 * 树型UI的重构,实现渐变背景
 * @author xch
 *
 */
public class WLTTreeUI extends MetalTreeUI {

	private BackGroundDrawingUtil util = new BackGroundDrawingUtil();
	private boolean isWindowsIcon = false;
	private boolean isDynamicChange = true; //是否动态变化
	private boolean isVisiableRect = true; //是否显示区域

	public WLTTreeUI() {

	}

	public WLTTreeUI(boolean _windowsIcon) {
		isWindowsIcon = _windowsIcon;
	}

	public WLTTreeUI(boolean _windowsIcon, boolean _isDynamicChange) {
		isWindowsIcon = _windowsIcon;
		this.isDynamicChange = _isDynamicChange; //
	}

	public WLTTreeUI(boolean _windowsIcon, boolean _isDynamicChange, boolean _isVisiableRect) {
		isWindowsIcon = _windowsIcon; //
		isDynamicChange = _isDynamicChange; //
		isVisiableRect = _isVisiableRect; //
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		JTree tree = (JTree) c; //
		Color bgColor = tree.getBackground(); //
		Rectangle rect = tree.getVisibleRect(); //一定要拿显示区域!!效果才好!!!
		if (!isVisiableRect) { //
			rect = tree.getBounds(); //
		}
		int rectWidth = (int) rect.getWidth();
		int rectHeight = (int) rect.getHeight();

		if (isDynamicChange) { //如果动态变化,则自动根据比例来动态修改效果!!!
			if (rectHeight > rectWidth * 2) {
				util.horizontalDraw(g, rect, Color.WHITE, bgColor);
			} else if (rectWidth > rectHeight * 2) {
				util.verticalDrawFromBottomToTop(g, rect, Color.WHITE, bgColor);
			} else {
				util.inclineDraw_NW_SE(g, rect, Color.WHITE, bgColor); //我重写的方法,还是从白的向深色渐变好看!!
			}
		} else {
			util.inclineDraw_NW_SE(g, rect, Color.WHITE, bgColor); //我重写的方法,还是从白的向深色渐变好看!!
		}
		super.paint(g, c); //
	}

	@Override
	/**
	 * 收缩状态时的图标
	 */
	public Icon getCollapsedIcon() {
		if (isWindowsIcon) {
			return WindowsTreeUI.CollapsedIcon.createCollapsedIcon();
		} else {
			return ImageIconFactory.getCollapsedIcon();
		}
	}

	@Override
	/**
	 * 展开状态时的图标
	 */
	public Icon getExpandedIcon() {
		if (isWindowsIcon) {
			return WindowsTreeUI.CollapsedIcon.createExpandedIcon();
		} else {
			return ImageIconFactory.getExpandedIcon(); //
		}
	}

}
