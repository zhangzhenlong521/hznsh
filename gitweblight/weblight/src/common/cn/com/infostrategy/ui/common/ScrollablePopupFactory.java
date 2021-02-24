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
 * @Description: ����ʽ�������ڹ�����,�������2013-03-19���������޸�ΪԲ����Ӱ�߿򡣸���Բ�󡣱��ʵ�����һϵ�������󲿷ֶ���JWindow������ֻҪ�õ�window��������������д����ɡ�͸����
 * @author haoming
 * @date Mar 19, 2013 4:16:44 PM
 *  
*/
public class ScrollablePopupFactory extends PopupFactory {
	//�Ƿ���������ȱʡ��
	private boolean horizontalExtending;
	//�Ƿ�ֱ������ȱʡ��
	private boolean verticalExtending;
	public static final int POP_TYPE_HORIZONTAL = 11;
	public static final int POP_TYPE_VERTICAL = 22;
	public static final int POP_TYPE_ALL = 33;
	public static final int POP_TYPE_NONE = 44; //ǿ���趨û��Ч��չ����

	/**
	 * Creates a new instance of ScrollablePopupFactory
	 */
	public ScrollablePopupFactory() {
	}

	/**
	 * @param h �Ƿ�������
	 * @param v �Ƿ�ֱ����
	 */
	public ScrollablePopupFactory(boolean h, boolean v) {
		horizontalExtending = h;
		verticalExtending = v;
	}

	/**
	 * ����PopupFactory.getPopup�����ṩ�Զ���Popup����
	 *
	 */
	public Popup getPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
		//����ݺᶼ��������ֱ��ʹ��ȱʡ�ĵ���ʽ����
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
		//���ô��ڵĳ�ʼ���Ż��ߴ磬ע��Popup��������ߴ����������ڴ�С��
		//Ϊ�����һ֡���ھ�ȫ����ʾ����Ҫ����һ��preferred size
		contents.setPreferredSize(new Dimension(horizontalExtending ? 0 : preferedSize.width, verticalExtending ? 0 : preferedSize.height));
		//��ȡȱʡ��Popup
		Popup popup = super.getPopup(owner, contents, x, y);
		//Ŀǰû�кð취�жϵ��������Ǻ������ͣ�ֻ�������������ж�
		String name = popup.getClass().getName();
		if (name.equals("javax.swing.PopupFactory$HeavyWeightPopup")) {
			//�������ĵ������ڣ��䶥������ΪJWindow
			return new PopupProxy(popup, contents, SwingUtilities.getWindowAncestor(contents), preferedSize);
		} else {
			//�������ĵ�������
			if (contents instanceof JToolTip)
				//��������JToolTip�����丸���������Ƕ�������
				return new PopupProxy(popup, contents, contents.getParent(), preferedSize);
			else
				//��������ʽ���������������Ƕ�������
				return new PopupProxy(popup, contents, contents, preferedSize);
		}
	}

	/**
	 * �������һ��Popup��������ʵPopup����ʾ���̶�������
	 */
	class PopupProxy extends Popup implements ActionListener {
		//һЩ����
		private static final int ANIMATION_FRAME_INTERVAL = 15;
		private static final int ANIMATION_FRAMES = 10;
		//������ĵ���ʽ���ڣ��������ʽ�����Ǵ�ȱʡ�����Ƕ���õġ�
		private Popup popupProxy;
		//��ǰ�������ڵĶ�������
		private Component popupContainer;
		//��ǰ�������ڵ��������
		private Component popupContent;
		//����ʽ�������ճߴ�
		private Dimension fullSize;
		//����ʱ��
		private Timer timer;
		//�����ĵ�ǰ֡
		private int frameIndex;

		public PopupProxy(Popup popup, Component contents, Component container, Dimension size) {
			//��ʼ��
			fullSize = size;
			popupProxy = popup;
			popupContent = contents;
			popupContainer = container;
			boolean isTooltip = (JComponent) contents instanceof JToolTip; //�ж��Ƿ���Tip��
			AWTUtilities.setWindowOpaque((JWindow) container, false); //����͸��
			AWTUtilities.setWindowOpacity((JWindow) container, isTooltip ? 1.0F : 0.95F);
			if (!isTooltip) { //��������ΪpopMenu,������
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
		 * �Զ���Ӧ��С��
		 */
		private void pack() {
			Component component = popupContainer;
			if ((component instanceof Window)) {
				((Window) component).pack();
			}
		}

		/*
		 * NinePatch������ȡ������
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
		 * ����show�������������߳�
		 */
		@Override
		public void show() {
			//��������ʾ
			popupProxy.show();
			//�ָ�ԭʼpreferred size
			popupContent.setPreferredSize(null);
			//��ʼ��Ϊ��һ֡
			frameIndex = 5;
			//��������ʱ��
			timer = new Timer(ANIMATION_FRAME_INTERVAL, this);
			timer.start();
		}

		/**
		 * ����hide���رտ��ܵ�ʱ��
		 */
		@Override
		public void hide() {
			if (timer != null && timer.isRunning())
				closeTimer();
			//���������ڹر�
			if ((popupContainer instanceof JWindow)) {
				((JWindow) popupContainer).getContentPane().removeAll();
				((Window) popupContainer).dispose(); //
				popupContainer.hide();
				popupContainer = null;//����ִ�У�������ٴ˵���ʱ�����־ɵ�Ӱ��
			}
		}

		//����ʱ���¼��Ĵ�������һ֡
		public void actionPerformed(ActionEvent e) {
			//���õ�ǰ֡������������ĳߴ�
			Dimension dim = new Dimension(horizontalExtending ? Math.min(fullSize.width * frameIndex / ANIMATION_FRAMES, fullSize.width) : fullSize.width, verticalExtending ? Math.min(fullSize.height * frameIndex / ANIMATION_FRAMES, fullSize.height) : fullSize.height);
			popupContainer.setSize(dim);
			if (frameIndex == ANIMATION_FRAMES) {
				//���һ֡
				closeTimer();
				pack(); //�ر�ʱ����Ӧһ��
			} else
				//ǰ��һ֡
				frameIndex++;
		}

		private void closeTimer() {
			//�ر�ʱ��
			timer.stop();
			timer = null;
		}
	}
}
