/**************************************************************************
 * $RCSfile: BillFrame.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.WLTLogger;

public class BillFrame extends JFrame {

	public static int CONFIRM = 1;
	public static int CANCEL = 2;

	private static final long serialVersionUID = 1L;

	private Vector v_closeListeners = new Vector(); //
	private Container parentContainer = null; //
	private Logger logger = WLTLogger.getLogger(cn.com.infostrategy.ui.common.BillFrame.class); //

	private int closeType = -1; //

	public BillFrame() throws HeadlessException {
		super();
		initDisPoseEventListener();
	}

	public BillFrame(String _title) throws HeadlessException {
		super(_title);
		initDisPoseEventListener();
	}

	public BillFrame(Dialog owner) {
		super();
		this.parentContainer = owner; //
		initDisPoseEventListener();
	}

	public BillFrame(Dialog owner, String title) {
		super(title);
		this.parentContainer = owner; //
		initDisPoseEventListener();
	}

	public BillFrame(Frame owner) {
		super();
		this.parentContainer = owner; //
		initDisPoseEventListener();
	}

	public BillFrame(Frame owner, String title) {
		super(title);
		this.parentContainer = owner; //
		initDisPoseEventListener();
	}

	public BillFrame(Container _parent) {
		super();
		this.parentContainer = _parent; //
		initDisPoseEventListener();
	}

	public BillFrame(Container _parent, String _title) {
		super(_title);
		this.parentContainer = _parent; //
		initDisPoseEventListener();
	}

	public BillFrame(Container _parent, int _width, int li_height) {
		this(_parent, "", _width, li_height);
		initDisPoseEventListener();
	}

	public BillFrame(Container _parent, String _title, int _width, int li_height, int _x, int _y) {
		super(_title);
		this.parentContainer = _parent; //
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //
		if (_width <= 0) {
			maxToScreenSize(); //
		} else {
			this.setSize(_width, li_height); //
			this.setLocation(new Double(_x).intValue(), new Double(_y).intValue());
		}
	}

	public BillFrame(Container _parent, int _width, int li_height, int _x, int _y) {
		super();
		this.parentContainer = _parent; //
		Frame frame = JOptionPane.getFrameForComponent(_parent);
		if (_width <= 0) {
			maxToScreenSize(); //
		} else {
			this.setSize(_width, li_height); //
			this.setLocation(new Double(_x).intValue(), new Double(_y).intValue());
		}
		initDisPoseEventListener();
	}

	public BillFrame(Container _parent, String _title, int _width, int li_height) {
		super(_title);
		this.setLocationRelativeTo(_parent); //
		this.parentContainer = _parent; //
		if (_width <= 0) {
			maxToScreenSize(); //满屏
		} else {
			Frame frame = JOptionPane.getFrameForComponent(_parent);
			double ld_width = frame.getSize().getWidth();
			double ld_height = frame.getSize().getHeight();
			double ld_x = frame.getLocation().getX();
			double ld_y = frame.getLocation().getY();

			this.setSize(_width, li_height); //
			double ld_thisX = ld_x + ld_width / 2 - _width / 2;
			double ld_thisY = ld_y + ld_height / 2 - li_height / 2;
			if (ld_thisX < 0) {
				ld_thisX = 0;
			}

			if (ld_thisY < 0) {
				ld_thisY = 0;
			}

			this.setLocation(new Double(ld_thisX).intValue(), new Double(ld_thisY).intValue());
		}
		initDisPoseEventListener();
	}

	/**
	 * 初始化关闭事件监听
	 */
	private void initDisPoseEventListener() {
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //
		this.toFront(); //
	}

	/**
	 * 重构Dispose方法,当API调用时也保证会执行所有的监听器
	 */
	@Override
	public void dispose() {
		beforeClosingWindow(); //
		super.dispose();
	}
	public int getCloseType() {
		return closeType;
	}

	public void setCloseType(int closeType) {
		this.closeType = closeType;
	}

	public void beforeClosingWindow() {
		for (int i = 0; i < v_closeListeners.size(); i++) {
			AbstractAction action = (AbstractAction) v_closeListeners.get(i);
			action.actionPerformed(new ActionEvent(this, 0, "closewindow")); //
		}
	}

	public void addWindowCloseListener(AbstractAction _action) {
		v_closeListeners.add(_action);
	}

	public void maxToScreenSize() {
		this.setSize(UIUtil.getScreenMaxDimension()); //
		this.setLocation(0, 0); //
	}

	public void maxToScreenSizeBy1280AndLocationCenter() {
		Dimension dimens = UIUtil.getScreenMaxDimension(); //屏幕大小,如果是1024则是1024.如果超过1280了,则认为是1280
		int li_width = (int) dimens.getWidth(); //
		int li_height = (int) dimens.getHeight(); //
		if (li_width > 1280) {
			li_width = 1280;
		}
		if (li_height > 800) {
			li_height = 800;
		}
		this.setSize(li_width, li_height); //
		locationToCenterPosition(); //
	}

	/**
	 * 移至中间位置
	 */
	public void locationToCenterPosition() {
		if (parentContainer != null) {
			Frame frame = JOptionPane.getFrameForComponent(parentContainer);
			double ld_width = frame.getSize().getWidth();
			double ld_height = frame.getSize().getHeight();
			double ld_x = frame.getLocation().getX();
			double ld_y = frame.getLocation().getY();

			int li_maxscreen_width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int li_maxscreen_height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60;

			int li_thisWidth = this.getWidth(); //本身宽度
			int li_thisHeight = this.getHeight(); //本身高度
			boolean isCross = false;
			if (li_thisWidth > li_maxscreen_width) { //如果本身宽超过了屏幕宽，则直接修改,强行保证永远不会越界!!
				li_thisWidth = li_maxscreen_width;
				isCross = true;
			}
			if (li_thisHeight > li_maxscreen_height) {
				li_thisHeight = li_maxscreen_height;
				isCross = true;
			}
			if (isCross) {
				this.setSize(li_thisWidth, li_thisHeight); //强行保证大小不可越屏幕！！！
			}

			double ld_thisX = ld_x + ld_width / 2 - li_thisWidth / 2;
			double ld_thisY = ld_y + ld_height / 2 - li_thisHeight / 2;
			if (ld_thisX + li_thisWidth >= li_maxscreen_width) { //如果右边出界了
				ld_thisX = (li_maxscreen_width - li_thisWidth) / 2; //则居屏幕中间
			}

			if (ld_thisY + li_thisHeight >= li_maxscreen_height) { //如果下边出界了
				ld_thisY = (li_maxscreen_height - li_thisHeight) / 2; //则居屏幕中间
			}

			if (ld_thisX <= 0) { //防止上面出界
				ld_thisX = 0;
			}

			if (ld_thisY <= 0) {
				ld_thisY = 0;
			}
			this.setLocation(new Double(ld_thisX).intValue(), new Double(ld_thisY).intValue());
		} else {
			int li_maxscreen_width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
			int li_maxscreen_height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 60;

			int li_thisWidth = this.getWidth(); //本身宽度
			int li_thisHeight = this.getHeight(); //本身高度
			boolean isCross = false;
			if (li_thisWidth > li_maxscreen_width) { //如果本身宽超过了屏幕宽，则直接修改,强行保证永远不会越界!!
				li_thisWidth = li_maxscreen_width;
				isCross = true;
			}
			if (li_thisHeight > li_maxscreen_height) {
				li_thisHeight = li_maxscreen_height;
				isCross = true;
			}

			if (isCross) {
				this.setSize(li_thisWidth, li_thisHeight); //强行保证大小不可越屏幕！！！
			}

			double ld_thisX = li_maxscreen_width / 2 - li_thisWidth / 2;
			double ld_thisY = li_maxscreen_height / 2 - li_thisHeight / 2;
			if (ld_thisX + li_thisWidth >= li_maxscreen_width) { //如果右边出界了
				ld_thisX = (li_maxscreen_width - li_thisWidth) / 2; //则居屏幕中间
			}

			if (ld_thisY + li_thisHeight >= li_maxscreen_height) { //如果下边出界了
				ld_thisY = (li_maxscreen_height - li_thisHeight) / 2; //则居屏幕中间
			}

			if (ld_thisX <= 0) { //防止上面出界
				ld_thisX = 0;
			}

			if (ld_thisY <= 0) {
				ld_thisY = 0;
			}
			this.setLocation(new Double(ld_thisX).intValue(), new Double(ld_thisY).intValue());
		}
	}

	private void changeFrameAppContext() {
		//		AppContext newAppContext = SunToolkit.targetToAppContext(this);
		//		System.out.println("_newAppContext" + newAppContext + "HashCode:" + newAppContext.hashCode());
		//		//		//SunToolkit.insertTargetMapping(this, newAppContext);
		//		Vector<WeakReference<Window>> windowList = (Vector) newAppContext.get(Window.class);
		//		//System.out.println("windowList=" + windowList);
		//		if (windowList == null) {
		//			windowList = new Vector<WeakReference<Window>>();
		//			newAppContext.put(Window.class, windowList);
		//		} else {
		//			for (int i = 0; i < windowList.size(); i++) {
		//				WeakReference ref = (WeakReference) windowList.get(i);
		//				System.out.println("第[" + i + "]个" + ref.get()); //
		//			}
		//		}
	}

	protected void finalize() throws Throwable {
		super.finalize(); //
		logger.debug("JVM GC回收器成功回收了BillFrame【" + this.getClass().getName() + "】(" + this.hashCode() + ")的资源...");
	}

}
