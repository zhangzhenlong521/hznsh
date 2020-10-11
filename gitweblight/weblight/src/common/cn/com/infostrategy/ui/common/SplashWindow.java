/**************************************************************************
 * $RCSfile: SplashWindow.java,v $  $Revision: 1.51 $  $Date: 2013/02/28 06:14:41 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;

import com.sun.awt.AWTUtilities;

/**
 * 启动一个线程，来显示请求用户稍等的Splash
 *
 * @author Administrator
 *
 */
public class SplashWindow extends BillDialog implements ActionListener {
	private static final long serialVersionUID = 6351024182198896342L;

	private Container parent = null;
	private AbstractAction action = null;

	private JTextArea textarea_msg = new JTextArea(); //
	private String str_info = ""; //
	private JButton btn_cancel = null;//

	private java.util.Timer labeltimer = new Timer(); //以前用Thread很不稳定,而且会出现卡住的问题,现在统一弄成Timer对象!稳定多了,而且不会卡住!!
	private java.util.Timer actionTimer = new Timer(); // //动作Timer
	private int li_count = 0; //
	private boolean sysMsgVisible = true; //如果等待框时间过长，是否显示“首次登陆系统...”的提示。
	public static SplashWindow window = null; //
	private Logger logger = WLTLogger.getLogger(cn.com.infostrategy.ui.common.SplashWindow.class); //

	/**
	 * @param _info:面板要显示的提示信息
	 * @param _image:面板中间要显示的提示图片（非必须）
	 * @param _com:调用该等待框的母板sa
	 */

	public SplashWindow(Container _parent, String _str_info, AbstractAction _action, boolean _showSysMsg) {
		super(_parent, "等待", 320, 95); //如果等待超过10秒，会多出一行提示，以前高度为120，提示信息没有完全显示出来，故将高度改为130
		this.parent = _parent; //
		this.action = _action;
 		this.str_info = (_str_info == null ? str_info : _str_info);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		sysMsgVisible = _showSysMsg;
		initWindow();
	}

	public SplashWindow(Container _parent, AbstractAction _action) {
		this(_parent, null, _action, true);
	}

	/**
	 * @param _info:面板要显示的提示信息
	 * @param _image:面板中间要显示的提示图片（非必须）
	 * @param _com:调用该等待框的母板sa
	 */
	public SplashWindow(Container _parent, AbstractAction _action, int _x, int _y) {
		super(_parent, "等待", 320, 95, _x, _y);
		this.parent = _parent; //
		this.action = _action;
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		initWindow();
	}

	/**
	 * @param _info:面板要显示的提示信息
	 * @param _image:面板中间要显示的提示图片（非必须）
	 * @param _com:调用该等待框的母板sa
	 */
	public SplashWindow(Container _parent, AbstractAction _action, int _width, int _height, int _x, int _y) {
		this(_parent, _action, _width, _height, _x, _y, true);
	}

	/**
	 * [郝明2012-04-01]
	 * @param _parent
	 * @param _action
	 * @param _width
	 * @param _height
	 * @param _x
	 * @param _y
	 * @param _showSysMsg  是否显示系统默认添加：首次登陆系统的提示！
	 */
	public SplashWindow(Container _parent, AbstractAction _action, int _width, int _height, int _x, int _y, boolean _showSysMsg) {
		super(_parent, "等待", _width, _height, _x, _y);
		this.parent = _parent; //
		this.action = _action;
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		sysMsgVisible = _showSysMsg;
		initWindow();
	}

	/**
	 *
	 * @param _com
	 *            Container 调用该等待框的母板sa
	 * @param _str_info
	 *            String 面板要显示的提示信息
	 * @param _action
	 *            AbstractAction
	 */
	public SplashWindow(Container _parent, String _str_info, AbstractAction _action) {
		super(_parent, "等待", 320, 95);
		this.str_info = _str_info; //
		this.parent = _parent; //
		this.action = _action;
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		initWindow();
	}

	private void initWindow() {
		JPanel contPanel = new WLTPanel(); //
		contPanel.setUI(new WLTPanelUI(){
			BackGroundDrawingUtil drawutil = new BackGroundDrawingUtil();
//			Color c1 = new Color(163,218,104);
//			Color c2 = new Color(215,237,195);
			Color c1 = new Color(164,166,170);
			Color c2 = new Color(230,231,232);
			
//			Color c3 = new Color(163,218,104);
			LinearGradientPaint p;
			public void paint(Graphics g, JComponent c) {
				Graphics2D g2d = (Graphics2D) g;
				 p = new LinearGradientPaint(0,0,0,c.getHeight(),new float[]{0.0f,0.2f,0.4f,1f},new Color[]{c1,c2,c1,c2});
				 g2d.setPaint(p);
				 g2d.fill(c.getBounds());
			}
		});
		Boolean isNewProgressGif = true; //是否是新图片,中铁建的老肖就非要是进度条!
		javax.swing.ImageIcon icon = UIUtil.getImage("process.gif");
		if (icon == null) {
			icon = UIUtil.getImage("clock.gif");
			isNewProgressGif = false;
			System.err.println("请升级weblight_images.jar文件,其中必须要有process.gif!"); //
		}
		JLabel label_image = new JLabel(icon){
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
			}
		}; //
//		label_image.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0)); //
		//有新图片就用新布局, 上面是进度条, 中间是文字, 下面是按钮
		if (isNewProgressGif) {
			contPanel.add(label_image, BorderLayout.NORTH);
		} else {
			contPanel.add(label_image, BorderLayout.WEST);
		}

		//文本框
		textarea_msg.setEnabled(false); //
		textarea_msg.setFocusable(false); //
		textarea_msg.setDisabledTextColor(Color.BLACK); //
		textarea_msg.setOpaque(false); //透明??
		textarea_msg.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); //
		textarea_msg.setFont(new Font("宋体", Font.PLAIN, 12)); //
		textarea_msg.setText(str_info); //

		contPanel.add(textarea_msg, BorderLayout.CENTER);
		contPanel.add(getSouthPanel(), BorderLayout.SOUTH);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(contPanel, BorderLayout.CENTER);
		window = this; ////
		labeltimer.schedule(new TimerTask() {
			@Override
			public void run() {
				refreshLabel(); //刷新文字
			}
		}, 1000, 1000); //一秒做一次!!

		actionTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				doAction(); //执行动作!
			}
		}, 0); //
		this.setUndecorated(true);
		AWTUtilities.setWindowOpaque(this, false);
		AWTUtilities.setWindowOpacity(this, 0.95f);
		AWTUtilities.setWindowShape(this, new RoundRectangle2D.Float(0,0,this.getWidth()-2,this.getHeight()-5, 20, 20));
//		this.locationToCenterPosition();
		this.setVisible(true);
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); //
		panel.setOpaque(false); //透明!!
		btn_cancel = new WLTButton("取消"); //
		btn_cancel.setBackground(Color.DARK_GRAY);
		btn_cancel.setFocusable(false); //
		btn_cancel.setFont(new Font("宋体", Font.PLAIN, 12)); //
		btn_cancel.setPreferredSize(new Dimension(70, 20)); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_cancel) {
			forceCancelAction(); //
		}
	}

	/**
	 * 强行取消
	 */
	private void forceCancelAction() {
		JLabel confirmlabel = new JLabel("你是否真的想取消当前操作?"); //
		confirmlabel.setFont(new Font("宋体", Font.PLAIN, 12)); //
		if (JOptionPane.showConfirmDialog(this, confirmlabel, "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		//killServerThread(); //先杀服务器端的线程,否则如果服务器端堵在那儿的话,光杀客户端线程还是没效果..
		labeltimer.cancel(); //
		actionTimer.cancel(); //
		this.dispose(); //
	}

	/**
	 * 刷新文字与标题!
	 */
	private void refreshLabel() {
		li_count++; //
		String str_sysprop_waitinfo = System.getProperty("SYSPROPWAITINFO"); //系统属性中定义的等待信息,这个属性会在同步下载代码时塞入!
		String str_realtext = null; //
		if (str_sysprop_waitinfo != null && !str_sysprop_waitinfo.equals("")) { //如果有,则显示
			str_realtext = str_sysprop_waitinfo;
		} else {
			str_realtext = str_info;
		}
		if (sysMsgVisible && li_count >= 10 && (li_count % 2) == 0) { //如果超过10秒,则加一个提醒!
			str_realtext = str_realtext + "\r\n" + "提示:首次访问,或服务器重启过,加载会耗时一点!"; //有时速度较慢,其实是因为重起或首次访问,所以加这样一个提醒感觉好一些!!
		}
		textarea_msg.setText(str_realtext); //
		SplashWindow.this.setTitle(li_count + "秒"); //
	}

	/**
	 * 执行动作!!!
	 */
	private void doAction() {
		try {
			action.actionPerformed(new ActionEvent(SplashWindow.this, 0, ""));
			closeWindow(); //执行结束后关闭窗口
			if (DeskTopPanel.deskTopPanel != null) {
				DeskTopPanel.deskTopPanel.updateUI(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			closeWindow(); //关闭窗口
		}
	}

	public void closeWindow() {
		labeltimer.cancel(); //
		actionTimer.cancel(); //
		this.dispose(); //
		SplashWindow.window = null; //
	}

	public static void closeSplashWindow() {
		if (SplashWindow.window != null) {
			SplashWindow.window.closeWindow(); //
		}
	}

	/**
	 * 设置新的等待信息..
	 * @param _newText
	 */
	public void setWaitInfo(String _newText) {
		textarea_msg.setText(_newText); //
		str_info = _newText;
	}

	public void setSysMsgVisible(boolean _isVisible) {
		sysMsgVisible = _isVisible;
	}

	/**
	 * 杀掉服务器端的线程,比如你进行一系列复杂的远程服务逻辑处理，其中服务器端的逻辑已运行到了一半了，
	 * 这时光杀客户端线程起不到效果，仍然会出现白屏现象..除非先杀掉服务器的线程
	 */
	private void killServerThread() {
		try {
			CurrSessionVO sessionVO = ClientEnvironment.getCurrSessionVO();
			if (sessionVO != null && sessionVO.getHttpsessionid() != null) {
				FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
				int li_count = service.killServerThreadBySessionId(sessionVO.getHttpsessionid()); //先杀服务器端的
				logger.debug("成功杀掉[" + li_count + "]条服务器端当前SessionID[" + sessionVO.getHttpsessionid() + "]调用的非我线程"); //
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	protected void finalize() throws Throwable {
		super.finalize(); //
		logger.debug("JVM GC回收器成功回收了SplashWindow【" + this.getClass().getName() + "】(" + this.hashCode() + ")的资源..."); //
	}

}
