package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.browser.WebBrowserEvent;
import org.jdesktop.jdic.browser.WebBrowserListener;

/**
 *以前通过监听IE退出时调用Java程序写剪粘板来关闭JavaSwing,但反复试验发现有问题,因为安全原因触发不了,即关闭窗口后既不能通过self.location=webpush20100601://localjava?来调用,也不能通过弹出窗口来调用!
 * 所以后来重新想了办法就是退出时往IE中写Cookie值!然后在Swing中通过JDIC在后台加载一个Html,这个html中不断刷新cooike中的值,发现IE退出时写的值,则立即通过修改窗口标题来通知Swing,Swing接到后就退出Java!!
 * 本类就是那个监听的Frame!!! 这个Frame要一直处于隐藏状态!!!
 * 必须定义了参数PushMonitorIEExit,而且是使用IE方式(包括单点登录,skip/skip2/正常IE)的情况下才会创建这个窗口!!!
 * 以后这个窗口可以作为一个通过的专门监听Cookie的基础类,即要可扩展支持其他通过Cookie的应用,从而实现与IE之间有通讯,比如注销/审批/记帐/发布风险点等!!即将所有注册的cookie拼成一串,用分号与等号隔开,然后在这里转成一个哈希表,然后去处理不同逻辑!!
 * @author xch
 *
 */
public class MonitorIECookieDialog extends JDialog implements WebBrowserListener {

	private static final long serialVersionUID = -4587721446225016107L;
	private static MonitorIECookieDialog dialog = null; //
	private static String str_reason = null; //
	private WebBrowser wb = null; //

	private MonitorIECookieDialog() {
		super(); //非模式!
		this.setModal(false); //
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE); //默认是隐藏!
		this.setTitle("Cookie监听窗口"); //
		this.setSize(650, 250);
		this.setLocation(-2000, -2000); //
		initialize(); //
		this.toBack(); //在后面!!!防止屏幕闪!
		this.setVisible(true); //必须先显示后再隐藏,否则触发不了!
		this.setVisible(false); //隐藏掉!
	}

	public static JDialog createInstance() {
		if (dialog == null) {
			dialog = new MonitorIECookieDialog(); //
		}
		return dialog; //
	}

	public static JDialog getInstance() {
		return dialog; //
	}

	private void initialize() {
		try {
			String str_url = System.getProperty("CALLURL") + "/login?logintype=monitoriecookie"; //访问地址!
			wb = new WebBrowser(new java.net.URL(str_url)); //
			this.getContentPane().add(wb, BorderLayout.CENTER); //
			wb.addWebBrowserListener(this); //监听事件!!!
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 监听到标题变化了,在Swing中调用!
	 * @param _newTitle
	 */
	private void callSwing(String _newTitle) {
		String str_cookieKey = System.getProperty("PushMonitorIEExit"); //
		if (_newTitle != null && _newTitle.equals(str_cookieKey + "@exit")) { //如果是退出标记
			System.out.println("MonitorIECookieDialog监听器从IE的Cookie中取到退出标记[" + _newTitle + "],所以直接退出系统!!"); //
			System.exit(0); //直接退出系统!!!
		}
	}

	//设置不加载的原因!!比如是桌面方式!没有设置参数等!!
	public static void setNotLoadReason(String _reason) {
		str_reason = _reason; //
	}

	public static String getNotLoadReason() {
		return str_reason;
	}

	//标题变化了!
	public void titleChange(WebBrowserEvent arg0) {
		callSwing(arg0.getData()); //
	}

	public void documentCompleted(WebBrowserEvent arg0) {
	}

	public void downloadCompleted(WebBrowserEvent arg0) {
	}

	public void downloadError(WebBrowserEvent arg0) {
	}

	public void downloadProgress(WebBrowserEvent arg0) {
	}

	public void downloadStarted(WebBrowserEvent arg0) {
	}

	public void statusTextChange(WebBrowserEvent arg0) {
	}

	public void windowClose(WebBrowserEvent arg0) {
	}

}
