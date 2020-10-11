/**************************************************************************
 * $RCSfile: DebugApplet.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DebugApplet extends JApplet {
	private static final long serialVersionUID = 1L;

	private IAppletLoader applerLoader = null;
	private String str_sourceCode = null; // 
	private Hashtable appletParmMap = null; //

	/**
	 * 默认构造方法,即作为Applet程序不允许使用了,而是改用应用程序了!!即直接运行Main方法
	 */
	public DebugApplet() {
		System.err.println("开发环境不再作为Applet,而是改成Application方式运行了,参数只有一个,如:http://127.0.0.1:9001/cmbc16/login?admin=Y&usercode=admin&pwd=1"); //
		System.exit(0); //
	}

	public DebugApplet(String _url) {
		System.out.println("访问地址参数=[" + _url + "]"); //
		infuseClientSysProp(); //先强行指定一些系统属性
		try {
			str_sourceCode = getURlHtml(_url, "logintype=desktop"); //远程访问取得源代码
			appletParmMap = parseHtmlContent(str_sourceCode); //解析源码成哈希表
			final JFrame frame = new JFrame("德勤咨询WebPush开发环境 - " + System.getProperty("URL")); //
			frame.getRootPane().putClientProperty("WebPushFrameMark", "WebPushRootFrame"); //

			Font font = new Font("宋体", Font.PLAIN, 12); //
			Menu menu = new Menu("系统菜单"); //
			final MenuItem menuItem_1 = new MenuItem("源代码"); ////
			final MenuItem menuItem_2 = new MenuItem("对象属性"); ////
			final MenuItem menuItem_3 = new MenuItem("系统属性"); ////
			AbstractAction action = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == menuItem_1) {
						showSource(); //
					} else if (e.getSource() == menuItem_2) {
						showAppletParam(); //
					} else if (e.getSource() == menuItem_3) {
						showSysProp(); //
					}
				}
			};

			menuItem_1.addActionListener(action); //
			menuItem_2.addActionListener(action); //
			menuItem_3.addActionListener(action); //
			menu.setFont(font); //
			menuItem_1.setFont(font); //
			menuItem_2.setFont(font); //
			menuItem_3.setFont(font); //
			menu.add(menuItem_1); //
			menu.add(menuItem_2); //
			menu.add(menuItem_3); //
			MenuBar menuBar = new MenuBar(); //
			menuBar.add(menu); //
			frame.setMenuBar(menuBar); //
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					onExit();
				}
			});
			frame.setSize(1280, 770); //开发环境都是大屏幕,统一设成1280*800算了,以前是1024*768的!!
			frame.setVisible(true); //
			frame.toFront(); //先显示出来
			new SplashWindow(frame, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					loadApplet(frame, appletParmMap); ///直接使用一个新的Frame加载本Applet
				}
			}, 350, 350);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void init() {
		String str_jreversion = System.getProperty("java.version"); //
//		if (str_jreversion.indexOf("1.6") != 0) { //如果不是1.6版本
//			JLabel label = new JLabel("由于系统升级,客户端Jre版本最低必须是1.6,请升级JRE版本.."); //
//			this.getContentPane().add(label); //
//			return;
//		}

		addSysPropByServerReturnAsFirstTime(); //在访问过服务器后,根据服务器端的返回值,重新计算一些客户端属性,比如ClientCodeCache

		this.getContentPane().setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel();
		mainPanel.setName("mainPanel"); //
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setOpaque(false);
		mainPanel.setBackground(java.awt.Color.WHITE);
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		try {
			applerLoader = new cn.com.infostrategy.ui.sysapp.login.LoginAppletLoader();
			applerLoader.loadApplet(this, mainPanel);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//在访问过服务器后,根据服务器端的返回值,重新计算一些客户端属性
	private void addSysPropByServerReturnAsFirstTime() {
		System.setProperty("RemoteCallServletURL", System.getProperty("CALLURL") + "/RemoteCallServlet"); //
		System.setProperty("ClientCodeCache", getClientCodeCachePath()); //
	}

	/**
	 * 取得客户端缓存路径
	 * @return
	 */
	public String getClientCodeCachePath() {
		String str_dir = System.getProperty("user.home") + "\\WEBLIGHT_CODECACHE_DEBUG\\";
		File file = new File(str_dir);
		if (!file.exists()) {
			file.mkdir(); //创建目录
		}
		return str_dir; //
	}

	private void infuseClientSysProp() {
		System.setProperty("java.awt.im.style", "on-the-spot"); //中文输入不要弹出框
		System.setProperty("JVMSITE", "CLIENT"); //指定是客户端虚拟机
		System.setProperty("WORKMODEL", "DEBUG"); //工作模式,有开发模式与运行模式之分!
		System.setProperty("STARTMODEL", "DESKTOP"); //启动模式!!
		System.setProperty("DEPLOYMODEL", "SINGLE"); //单例模板部署
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("必须为此程序设置一个参数,值为[http://127.0.0.1:9001/cmbc/login]的样子!!");
			System.exit(0); //
		}
		new DebugApplet(args[0]); //
	}

	//根据URL,返回实际的html
	private String getURlHtml(String _url, String _requestPar) throws Exception {
		String str_convertUrl = _url;
		if (_requestPar != null && !_requestPar.equals("")) {
			if (_url.indexOf("?") < 0) {
				str_convertUrl = _url + "?" + _requestPar;
			} else {
				str_convertUrl = _url + "&" + _requestPar;
			}
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(str_convertUrl).openStream()));
		String str;
		StringBuffer sb_html = new StringBuffer();
		while ((str = in.readLine()) != null) {
			sb_html.append(str + "\r\n"); //
		}
		in.close();
		return sb_html.toString(); //
	}

	//显示源代码
	private void showSource() {
		showTextInFrame("查看源代码", str_sourceCode); //
	}

	//显示Applet的参数
	private void showAppletParam() {
		String[] str_keys = (String[]) appletParmMap.keySet().toArray(new String[0]);
		Arrays.sort(str_keys); //
		StringBuffer sb_text = new StringBuffer(); //
		for (int i = 0; i < str_keys.length; i++) {
			if (str_keys[i].equalsIgnoreCase("loginuserpwd") || str_keys[i].equalsIgnoreCase("loginuseradminpwd")) { //如果是密码数据，则不能显示..
				sb_text.append(str_keys[i] + "=[******]\r\n"); ////
			} else {
				sb_text.append(str_keys[i] + "=[" + appletParmMap.get(str_keys[i]) + "]\r\n"); ////
			}
		}
		showTextInFrame("查看WLTApplet所有参数", sb_text.toString()); //
	}

	//显示系统属性
	private void showSysProp() {
		StringBuffer sb_text = new StringBuffer();
		Properties sysProps = System.getProperties(); //
		String[] str_keys = (String[]) sysProps.keySet().toArray(new String[0]); //
		Arrays.sort(str_keys); //
		for (int i = 0; i < str_keys.length; i++) {
			sb_text.append(str_keys[i] + "=[" + sysProps.getProperty(str_keys[i]) + "]\r\n"); //
		}
		showTextInFrame("查看系统属性", sb_text.toString()); //
	}

	private void showTextInFrame(String _title, String _text) {
		JFrame frame = new JFrame(_title);
		frame.setSize(800, 450); //
		frame.setLocation(0, 0); //
		JTextArea textArea = new JTextArea(_text); //
		textArea.setFont(new Font("宋体", Font.PLAIN, 12)); //
		textArea.setEditable(false); //
		frame.getContentPane().add(new JScrollPane(textArea)); //
		frame.setVisible(true); //
	}

	private Hashtable parseHtmlContent(String _text) throws Exception {
		String str_text = _text; //从Servlet中返回的实际的html...
		int li_pos_begin = str_text.indexOf("<Applet"); //
		int li_pos_end = str_text.indexOf("</Applet>"); //
		String str_applettext = str_text.substring(li_pos_begin, li_pos_end + 9); ////截取字符串
		String str_xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + str_applettext; //用于解析XML的
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); ////
		factory.setValidating(false);
		Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(str_xml))); //加载XML!!!
		NodeList list = doc.getDocumentElement().getElementsByTagName("*"); //找出所有参数
		Hashtable ht_pars = new Hashtable(); //
		for (int i = 0; i < list.getLength(); i++) {
			Element childNode = (Element) list.item(i); //
			String str_name = childNode.getAttribute("NAME"); //
			String str_value = childNode.getAttribute("VALUE"); //
			//System.out.println("[" + str_name + "]=[" + str_value + "]"); //输出属性...
			if (str_value == null || str_value.equals("null")) {
				str_value = "";
			}
			ht_pars.put(str_name, str_value); //
			if (str_name != null) {
				if (str_value != null) {
					System.setProperty(str_name, str_value); //将所有PARAM中的值第一时间都一个不漏的送入客户端系统属性!!就是在还没有加载真正应用时就将<applet></applet>中定义的所有参数都Load进入了SystemPropties!!!
					//System.out.println("设置系统属性[" + str_name + "]=[" + str_value + "]"); //
				} else {
					System.setProperty(str_name, ""); //
				}
			}
		}

		return ht_pars;
	}

	private void loadApplet(JFrame frame, Hashtable parMap) {
		this.setStub(new MyAppletStub(parMap)); //
		this.init(); //
		this.start();
		frame.getContentPane().add(this); //加载...
		frame.validate(); //
	}

	private void onExit() {
		if (JOptionPane.showConfirmDialog(this, "您确定要退出系统吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}
		cn.com.infostrategy.ui.sysapp.login.LoginAppletLoader.exitSysByClientJVMRefCall(); //
		System.exit(0); //
	}
}

class MyAppletStub implements AppletStub {
	private Hashtable parMap = null; //

	MyAppletStub(Hashtable _parMap) {
		parMap = _parMap; ////
	}

	public void appletResize(int width, int height) {
	}

	public AppletContext getAppletContext() {
		return null;
	}

	public URL getCodeBase() {
		return null;
	}

	public URL getDocumentBase() {
		return null;
	}

	public String getParameter(String _key) {
		return (String) parMap.get(_key); //全部大写
	}

	public boolean isActive() {
		return false;
	}
}
