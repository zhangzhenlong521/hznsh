/*
 * ScrollablePopupFactory.java
 *
 * Created on June 12, 2007, 9:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;
import javax.swing.JWindow;
import javax.swing.MenuElement;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.NinePatch;

import com.sun.awt.AWTUtilities;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.common.ScrollablePopupFactory 
 * @Description: 滚动式弹出窗口工厂类,这个类在2013-03-19郝明将其修改为圆角阴影边框。更加圆润。本质弹出的一系列容器大部分都是JWindow。所以只要得到window句柄，来对他进行处理即可。透明。
 * @author haoming
 * @date Mar 19, 2013 4:16:44 PM
 *  
*/
public class ScrollablePopupFactory extends PopupFactory {
	//是否横向滚动，缺省不
	private boolean horizontalExtending;
	//是否垂直滚动，缺省不
	private boolean verticalExtending;
	public static final int POP_TYPE_HORIZONTAL = 11;
	public static final int POP_TYPE_VERTICAL = 22;
	public static final int POP_TYPE_ALL = 33;
	public static final int POP_TYPE_NONE = 44; //强行设定没有效果展开。

	/**
	 * Creates a new instance of ScrollablePopupFactory
	 */
	public ScrollablePopupFactory() {
	}

	/**
	 * @param h 是否横向滚动
	 * @param v 是否垂直滚动
	 */
	public ScrollablePopupFactory(boolean h, boolean v) {
		horizontalExtending = h;
		verticalExtending = v;
	}

	/**
	 * 覆盖PopupFactory.getPopup方法提供自定义Popup代理
	 *
	 */
	public Popup getPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
		//如果纵横都不滚动，直接使用缺省的弹出式窗口
		if (owner instanceof JComponent) {
			JComponent ow = (JComponent) owner;
			Object obj = ow.getClientProperty("POPMENUTYPE");
			int type = (obj == null ? 0 : (Integer) obj);
			switch (type) {
			case POP_TYPE_HORIZONTAL:
				verticalExtending = false;
				horizontalExtending = true;
				break;
			case POP_TYPE_VERTICAL:
				verticalExtending = true;
				horizontalExtending = false;
				break;
			case POP_TYPE_NONE:
				verticalExtending = false;
				horizontalExtending = false;
				break;
			default:
				verticalExtending = true;
				horizontalExtending = true;
			}
		}
		if (!(horizontalExtending || verticalExtending))
			return super.getPopup(owner, contents, x, y);

		Dimension preferedSize = contents.getPreferredSize();
		//设置窗口的初始最优化尺寸，注意Popup根据这个尺寸决定最初窗口大小，
		//为避免第一帧窗口就全部显示，需要重置一下preferred size
		contents.setPreferredSize(new Dimension(horizontalExtending ? 0 : preferedSize.width, verticalExtending ? 0 : preferedSize.height));
		//获取缺省的Popup
		Popup popup = super.getPopup(owner, contents, x, y);
		//目前没有好办法判断弹出窗口是何种类型，只好用类名字来判断
		String name = popup.getClass().getName();
		if (name.equals("javax.swing.PopupFactory$HeavyWeightPopup")) {
			//重量级的弹出窗口，其顶层容器为JWindow
			return new PopupProxy(popup, contents, SwingUtilities.getWindowAncestor(contents), preferedSize);
		} else {
			//轻量级的弹出窗口
			if (contents instanceof JToolTip)
				//如果组件是JToolTip，则其父亲容器就是顶层容器
				return new PopupProxy(popup, contents, contents.getParent(), preferedSize);
			else
				//其他弹出式窗口则组件本身就是顶层容器
				return new PopupProxy(popup, contents, contents, preferedSize);
		}
	}

	/**
	 * 这个类是一个Popup代理，将真实Popup的显示过程动画弹出
	 */
	class PopupProxy extends Popup implements ActionListener {
		//一些常量
		private static final int ANIMATION_FRAME_INTERVAL = 15;
		private static final int ANIMATION_FRAMES = 10;
		//被代理的弹出式窗口，这个弹出式窗口是从缺省工厂那儿获得的。
		private Popup popupProxy;
		//当前弹出窗口的顶层容器
		private Component popupContainer;
		//当前弹出窗口的内容组件
		private Component popupContent;
		//弹出式窗口最终尺寸
		private Dimension fullSize;
		//动画时钟
		private Timer timer;
		//动画的当前帧
		private int frameIndex;

		public PopupProxy(Popup popup, Component contents, Component container, Dimension size) {
			//初始化
			fullSize = size;
			popupProxy = popup;
			popupContent = contents;
			popupContainer = container;
			boolean isTooltip = (JComponent) contents instanceof JToolTip; //判断是否是Tip。
			AWTUtilities.setWindowOpaque((JWindow) container, false); //设置透明
			AWTUtilities.setWindowOpacity((JWindow) container, isTooltip ? 1.0F : 0.95F);
			if (!isTooltip) { //常见到的为popMenu,下拉框
				ImageBgPane imageContentPane = new ImageBgPane(isTooltip ? IconFactory.getInstance().getTooltipBg() : IconFactory.getInstance().getPopupBg());
				imageContentPane.setLayout(new BorderLayout());
				imageContentPane.add(contents, "Center");
				if ((contents instanceof JComponent)) {
					((JComponent) contents).setOpaque(false);
					if (!isTooltip) {
						((JComponent) contents).setBorder(BorderFactory.createEmptyBorder(5, 3, 6, 3));
					}
				}
				if ((contents instanceof JPopupMenu)) {
					MenuElement[] mes = ((JPopupMenu) contents).getSubElements();
					for (int i = 0; i < mes.length; i++) {
						if ((mes[i] instanceof JMenuItem)) {
							((JMenuItem) mes[i]).setOpaque(false);
						}
					}
				}
				((JWindow) container).setContentPane(imageContentPane);
				contents.invalidate();
			}
		}

		/*
		 * 自动适应大小。
		 */
		private void pack() {
			Component component = popupContainer;
			if ((component instanceof Window)) {
				((Window) component).pack();
			}
		}

		/*
		 * NinePatch技术获取背景。
		 */
		protected class ImageBgPane extends JPanel {
			private NinePatch np = null;

			public ImageBgPane(NinePatch np) {
				this.np = np;
			}

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (this.np != null) {
					int width = getWidth();
					int height = getHeight();
					this.np.draw((Graphics2D) g, 0, 0, width, height);
				}
			}
		}

		/**
		 * 覆盖show方法启动动画线程
		 */
		@Override
		public void show() {
			//代理窗口显示
			popupProxy.show();
			//恢复原始preferred size
			popupContent.setPreferredSize(null);
			//初始化为第一帧
			frameIndex = 5;
			//启动动画时钟
			timer = new Timer(ANIMATION_FRAME_INTERVAL, this);
			timer.start();
		}

		/**
		 * 重载hide，关闭可能的时钟
		 */
		@Override
		public void hide() {
			if (timer != null && timer.isRunning())
				closeTimer();
			//代理弹出窗口关闭
			if ((popupContainer instanceof JWindow)) {
				((JWindow) popupContainer).getContentPane().removeAll();
				((Window) popupContainer).dispose(); //
				popupContainer.hide();
				popupContainer = null;//必须执行，否则会再此弹出时，出现旧的影儿
			}
		}

		//动画时钟事件的处理，其中一帧
		public void actionPerformed(ActionEvent e) {
			//设置当前帧弹出窗口组件的尺寸
			Dimension dim = new Dimension(horizontalExtending ? Math.min(fullSize.width * frameIndex / ANIMATION_FRAMES, fullSize.width) : fullSize.width, verticalExtending ? Math.min(fullSize.height * frameIndex / ANIMATION_FRAMES, fullSize.height) : fullSize.height);
			popupContainer.setSize(dim);
			if (frameIndex == ANIMATION_FRAMES) {
				//最后一帧
				closeTimer();
				pack(); //关闭时自适应一把
			} else
				//前进一帧
				frameIndex++;
		}

		private void closeTimer() {
			//关闭时钟
			timer.stop();
			timer = null;
		}
	}
}
