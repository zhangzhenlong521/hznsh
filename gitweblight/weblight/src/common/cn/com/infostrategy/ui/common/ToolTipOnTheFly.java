package cn.com.infostrategy.ui.common;

import java.awt.Point;

import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

/**
 * 在某一点显示一个提示框
 * @author gaofeng
 * 
 */
public class ToolTipOnTheFly {

	public static void showToolTip(String msg, Point pos) {
		JToolTip toolTip = new JToolTip();
		toolTip.setTipText(msg);
		final Popup pop = PopupFactory.getSharedInstance().getPopup(null, toolTip, pos.x, pos.y);
		pop.show();
		Runnable r = new Runnable() {
			public void run() {
				try {
					Thread.sleep(3000); //3秒自动隐藏
					pop.hide();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(r).start();
	}
}
