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
 * ����UI���ع�,ʵ�ֽ��䱳��
 * @author xch
 *
 */
public class WLTTreeUI extends MetalTreeUI {

	private BackGroundDrawingUtil util = new BackGroundDrawingUtil();
	private boolean isWindowsIcon = false;
	private boolean isDynamicChange = true; //�Ƿ�̬�仯
	private boolean isVisiableRect = true; //�Ƿ���ʾ����

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
		Rectangle rect = tree.getVisibleRect(); //һ��Ҫ����ʾ����!!Ч���ź�!!!
		if (!isVisiableRect) { //
			rect = tree.getBounds(); //
		}
		int rectWidth = (int) rect.getWidth();
		int rectHeight = (int) rect.getHeight();

		if (isDynamicChange) { //�����̬�仯,���Զ����ݱ�������̬�޸�Ч��!!!
			if (rectHeight > rectWidth * 2) {
				util.horizontalDraw(g, rect, Color.WHITE, bgColor);
			} else if (rectWidth > rectHeight * 2) {
				util.verticalDrawFromBottomToTop(g, rect, Color.WHITE, bgColor);
			} else {
				util.inclineDraw_NW_SE(g, rect, Color.WHITE, bgColor); //����д�ķ���,���ǴӰ׵�����ɫ����ÿ�!!
			}
		} else {
			util.inclineDraw_NW_SE(g, rect, Color.WHITE, bgColor); //����д�ķ���,���ǴӰ׵�����ɫ����ÿ�!!
		}
		super.paint(g, c); //
	}

	@Override
	/**
	 * ����״̬ʱ��ͼ��
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
	 * չ��״̬ʱ��ͼ��
	 */
	public Icon getExpandedIcon() {
		if (isWindowsIcon) {
			return WindowsTreeUI.CollapsedIcon.createExpandedIcon();
		} else {
			return ImageIconFactory.getExpandedIcon(); //
		}
	}

}
