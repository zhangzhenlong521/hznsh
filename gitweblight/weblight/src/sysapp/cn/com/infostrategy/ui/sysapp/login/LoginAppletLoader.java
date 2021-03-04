/**************************************************************************
 * $RCSfile: LoginAppletLoader.java,v $  $Revision: 1.39 $  $Date: 2013/02/20 09:43:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.ConsoleMsgVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopVO;
import cn.com.infostrategy.to.sysapp.login.LoginOneOffVO;
import cn.com.infostrategy.ui.common.ClientConsolePrintStream;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.IAppletLoader;
import cn.com.infostrategy.ui.common.RemoteCallClient;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 加载器
 * 
 * @author Administrator
 * 
 */
public class LoginAppletLoader implements IAppletLoader, Serializable {

	private static final long serialVersionUID = -6361060376004420293L;

	private JApplet applet = null;
	public DeskTopPanel deskTopPanel = null; //桌面面板
	public static JPanel mainPanel = null;
	private static LoginAppletLoader appletLoader = null; //自己本身.
	private LoginUtil loginUtil = new LoginUtil(); //
	private TBUtil tbUtil = new TBUtil(); 

	/**
	 * 构造方法,在其中从服务器端加载log4j配置信息与初始参数
	 */
	public LoginAppletLoader() {
		this(true);
	}

	public LoginAppletLoader(boolean _isInit) {
		if (_isInit) {
			appletLoader = this; //
			ClientConsolePrintStream mySystemOut = new ClientConsolePrintStream(ConsoleMsgVO.SYSTEM_OUT, System.out); //重定向控制台
			System.setOut(mySystemOut);
			ClientConsolePrintStream mySystemErr = new ClientConsolePrintStream(ConsoleMsgVO.SYSTEM_ERROR, System.err); //错误的.
			System.setErr(mySystemErr); //
		}
	}

	/**
	 * 只有第一次加载时才会运行!!
	 */
	public void loadApplet(JApplet _applet, JPanel _mainPanel) {
		try {
			this.applet = _applet;
			this.mainPanel = _mainPanel; //

			//真正访问的地址!!
			System.out.println("远程访问地址(CallURL):[" + System.getProperty("CALLURL") + "]"); //
			System.out.println("客户端代码缓存路径:[" + System.getProperty("ClientCodeCache") + "]"); //
			System.out.println("工作模式:[" + System.getProperty("WORKMODEL") + "],登录模式:[" + System.getProperty("LOGINMODEL") + "],启动模式:[" + System.getProperty("STARTMODEL") + "],部署模式:[" + System.getProperty("DEPLOYMODEL") + "],是否压缩数据流[" + System.getProperty("ISZIPSTREAM") + "]"); //部署模式
			String str_innersystemtext = System.getProperty("INNERSYSTEM"); //所有子系统!!!
			if (str_innersystemtext != null && !str_innersystemtext.trim().equals("") && !str_innersystemtext.trim().equalsIgnoreCase("nulll")) {
				String[] str_items1 = split(str_innersystemtext, ";"); //
				String[][] str_return = new String[str_items1.length][2]; //
				for (int i = 0; i < str_items1.length; i++) {
					str_return[i] = split(str_items1[i], ",");
				}
				ClientEnvironment.setInnerSys(str_return); //设置子系统
			}
			String str_losginusercode = _applet.getParameter("LOGINUSERCODE");
			String str_losginuserpwd = _applet.getParameter("LOGINUSERPWD");
			String str_losginuseradminpwd = _applet.getParameter("LOGINUSERADMINPWD"); //管理密码
//			System.out.println("用户编码[" + str_losginusercode + "],用户密码[" + str_losginuserpwd + "],用户管理密码[" + str_losginuseradminpwd + "]"); //
			if (str_losginusercode != null) { //如果是浏览器登录,即指定了登录用户(但有可能是正常IE登录,也可能是门户单点登录的)
				try {
					boolean bo_isadmin = false;
					if (System.getProperty("LOGINMODEL").equals("ADMIN")) {
						bo_isadmin = true;
					}
					String str_language = WLTConstants.SIMPLECHINESE; //默认语言...
					if (_applet.getParameter("LANGUAGE") != null) {
						str_language = _applet.getParameter("LANGUAGE");
					}

					boolean isquicklogin = false; //是否快速登录,所谓快速登录就是指不要校验密码了!!! 比如单点登录时!!!
					String str_logintype = _applet.getParameter("REQ_logintype"); //登录类型
					if ("single".equals(str_logintype) || "skip2".equals(str_logintype) || "Y".equalsIgnoreCase(System.getProperty("AutoLogin"))) { //如果是单点登录!后来增加了skip2,其实也是单点登录!!
						//System.out.println("发现是单点登录模式...."); //
						isquicklogin = true; //快速登录!!!
					}
					String checkcode = _applet.getParameter("REQ_checkcode");
					dealLogin(false, str_losginusercode, str_losginuserpwd, str_losginuseradminpwd, _applet.getParameter("ISYS"), bo_isadmin, isquicklogin, str_language, checkcode); //
				} catch (Exception ex) { //如果dealLogin失败,则回到登录界面!!
					ex.printStackTrace();

				}
			} else { //如果没有指定登录用户,则输出登录面页
				//System.out.println("用户编码为空,加载登录界面..."); //
				loadLoginPanel(null, null); //加载登录页面!!!	
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			//loginUtil.startClientRefreshThread(); //启动线程!!!
		}
		//showMeInIeBrowse(); //
	}

	/**
	 * 加载登录页面!!!
	 */
	public void loadLoginPanel(String _userCode, String _errMsg) {
		mainPanel.removeAll();
		deskTopPanel = null;
		mainPanel.setLayout(new BorderLayout()); //
		LoginPanel contentPanel = new LoginPanel(this, _userCode, _errMsg); //
		mainPanel.add(contentPanel);
		loginUtil.setDefaultLookAndFeel(mainPanel); //设置外观..
		mainPanel.updateUI();
	}

	/**
	 * 加载主页面!!!
	 * @param _userVO 
	 * @param deskTopVO 
	 * @param _usercode 用户编码
	 * @param _logintime
	 */
	public void loadDeskTopPanel(CurrLoginUserVO _userVO, DeskTopVO _deskTopVO) {
		mainPanel.removeAll();
		deskTopPanel = null;
		loginUtil.setWLTLookAndFeel(_userVO.getAllLookAndFeels(), mainPanel); //设置风格.
		mainPanel.setLayout(new BorderLayout()); //
		deskTopPanel = new DeskTopPanel(null, _deskTopVO); //
		mainPanel.add(deskTopPanel, BorderLayout.CENTER);
		mainPanel.repaint();
		mainPanel.revalidate(); //
		System.gc(); //
		//mainPanel.updateUI();
	}

	/**
	 * 
	 * @param _userCode 用户编码
	 * @param _pwd  用户密码
	 * @param _adminpwd  管理密码
	 * @param _isys  选的是哪个子系统
	 * @param _isquicklogin 是否快捷登录
	 * @return
	 */
	public void dealLogin(boolean _isSplash, final String _userCode, final String _pwd, final String _adminpwd, final String _isys, final boolean _isadmin, final boolean _isquicklogin, String _language, final String checkcode) {
		closeAllOtherFrame(); //关闭其他所有Frame界面!!!因为重新登录,或切换用户界面时,如果打开了许多frame，则必须先关闭,比如某个列表查看等!!
		ClientEnvironment.getInstance().setDefaultLanguageType(_language); //设置语言..
		ClientEnvironment.chooseISys = _isys; //设置选中的子系统!!
		if (_isSplash) { //如果是等待框的去处理,浏览器过来时就不要了,但桌面就需要
			new SplashWindow(mainPanel, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					realdealLogin(_userCode, _pwd, _adminpwd, _isys, _isadmin, _isquicklogin, checkcode); //
				}
			}, 366, 366);
		} else { //如果是浏览器过来的,则等待框是在一开始就有了,不搞两个等待框!!!
			realdealLogin(_userCode, _pwd, _adminpwd, _isys, _isadmin, _isquicklogin, checkcode); //
		}
	}

	private String getLocalHostIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception ex) {
			return "0.0.0.0"; //
		}
	}

	/**
	 * 直接处理
	 * @param _userCode
	 * @param _pwd
	 * @param _adminpwd
	 * @param _isadmin
	 * @param _isquicklogin
	 */
	public void realdealLogin(String _userCode, String _pwd, String _adminpwd, String _isys, boolean _isadmin, boolean _isquicklogin, String checkcode) {
		//		if (_userCode == null || _userCode.trim().equals("")) {
		//			MessageBox.show(mainPanel, "用户名不能为空!", WLTConstants.MESSAGE_WARN);   //因为有等待框堵住线程,这个弹出框是永远被卡住而看不到,系统就一直处于一种加载的状态!! 好象是死了一般!!! 所以没有意义! 注销算了!!!
		//			return;
		//		}
		try {
			LoginOneOffVO loginOneOffVO = doLogin(_userCode, _pwd, _adminpwd, _isys, _isadmin, _isquicklogin, checkcode); //真正登录逻辑处理! 给客户端环境变量赋值!!!
			loadDeskTopPanel(loginOneOffVO.getCurrLoginUserVO(), loginOneOffVO.getDeskTopVO()); // 进入主界面!!!!!!!!!!!!!!!!!!!!!
			if (UIUtil.getCommonService().getSysOptionBooleanValue("客户端是否定期向服务器报到", false)) {
				// 进入主界面后才开始执行心跳线程
				new Timer().schedule(new TimerTask() {
					public void run() {
						try {
							RemoteCallClient rc = new RemoteCallClient(System.getProperty("URL") + "/WebCallServlet?StrParCallClassName=cn.com.infostrategy.bs.common.ClientOLMaService&isdispatch=Y&actype=reg");
							rc.callServlet("", "", null, null, 5000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 10000, 15000);
				// 
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //如果失败,
			loadLoginPanel(_userCode, ex.getMessage()); //如果失败,则加载登录页面,并将错误信息显示在登录界面中!!!
		}
	}

	public LoginOneOffVO doLogin(String _userCode, String _pwd, String _adminpwd, String _isys, boolean _isadmin, boolean _isquicklogin, String checkcode) throws Exception {
		//真正登录!!!
		LoginOneOffVO loginOneOffVO = getSysAppService().loginOneOff(_userCode, _pwd, _adminpwd, new Boolean(_isadmin), new Boolean(_isquicklogin), System.getProperties(), checkcode); //只是一次登录

		//设置Log4j配置...
		ClientEnvironment.getInstance().setLog4jConfigVO(loginOneOffVO.getLog4jConfigVO()); //

		WLTLogger.config(System.getProperty("ClientCodeCache"), loginOneOffVO.getLog4jConfigVO().getClient_level(), loginOneOffVO.getLog4jConfigVO().getClient_outputtype()); //设置Log4J配置
		WLTLogger.getLogger(LoginAppletLoader.class).info("成功设置Log4j,level[" + loginOneOffVO.getLog4jConfigVO().getClient_level() + "],outputtype[" + loginOneOffVO.getLog4jConfigVO().getClient_outputtype() + "]");

		//设置子系统,去服务器端取得系统参数!
		String str_innersystemtext = System.getProperty("INNERSYSTEM"); //
		if (ClientEnvironment.getInnerSys() == null && str_innersystemtext != null && !str_innersystemtext.trim().equals("") && !str_innersystemtext.trim().equalsIgnoreCase("nulll")) {
			String[] str_items1 = split(str_innersystemtext, ";"); //
			String[][] str_return = new String[str_items1.length][2]; //
			for (int i = 0; i < str_items1.length; i++) {
				str_return[i] = split(str_items1[i], ","); //
			}
			ClientEnvironment.setInnerSys(str_return); //设置子系统
		}

		//设置数据源...
		System.setProperty("defaultdatasource", loginOneOffVO.getDataSourceVOs()[0].getName()); //设置默认数据源
		ClientEnvironment.getInstance().put("defaultdatasource", loginOneOffVO.getDataSourceVOs()[0].getName()); //
		ClientEnvironment.getInstance().setDataSourceVOs(loginOneOffVO.getDataSourceVOs()); //

		//设置登录人员Session信息
		CurrLoginUserVO userVO = loginOneOffVO.getCurrLoginUserVO(); //登录用户信息
		ClientEnvironment.setCurrLoginUserVO(userVO); //重置客户端缓存中的当前登录用户的信息

		if (_isadmin) { //如果是管理员登录!!
			ClientEnvironment.getInstance().setLoginModel(ClientEnvironment.LOGINMODEL_ADMIN); //
		} else { //如果是普通用户登录!!
			ClientEnvironment.getInstance().setLoginModel(ClientEnvironment.LOGINMODEL_NORMAL); //
		}

		//重置Session中的登录人员信息
		ClientEnvironment.getCurrSessionVO().setLoginUserId(userVO.getId()); //
		ClientEnvironment.getCurrSessionVO().setLoginUserCode(userVO.getCode()); //
		ClientEnvironment.getCurrSessionVO().setLoginUserName(userVO.getName()); //
		ClientEnvironment.getInstance().put("clientip", getLocalHostIP()); //记录客户端ip
		ClientEnvironment.getCurrSessionVO().setLoginUserPKDept(userVO.getPKDept()); //
		return loginOneOffVO; //
	}

	public String[] split(String _par, String _separator) {
		Vector v_return = new Vector();
		StringTokenizer st = new StringTokenizer(_par, _separator);
		while (st.hasMoreTokens()) {
			v_return.add(st.nextToken());
		}
		return (String[]) v_return.toArray(new String[0]); //
	}

	private void showMeInIeBrowse() {
		//		if (ClientEnvironment.getInstance().getStartModel() == ClientEnvironment.STARTMODEL_BROWSE) { //如果是浏览器方式启动!!!
		//			JSObject win = JSObject.getWindow(this.getApplet());
		//			win.eval("window.showWLDiv()"); //显示页面层!!!
		//		}
	}

	/**
	 * 关闭其他所有Frame!因为有时会打开多个Frame,这时重新登录时,应该需要关闭已打开过的Frame,只保留主界面的Frame!
	 */
	private void closeAllOtherFrame() {
		try {
			Frame rootFrame = null; //
			if (this.mainPanel != null) {
				rootFrame = JOptionPane.getFrameForComponent(this.mainPanel); //
			}
			Frame[] allFrames = Frame.getFrames(); //找到所有Frame
			for (int i = 0; i < allFrames.length; i++) {
				//System.out.println("第[" + (i + 1) + "]个Frame=" + allFrames[i]); //
				if (allFrames[i].isVisible() && allFrames[i].isShowing()) { //如果是显示状态!!
					String str_clientprop = null; //
					if (allFrames[i] instanceof JFrame) {
						str_clientprop = (String) ((JFrame) allFrames[i]).getRootPane().getClientProperty("WebPushFrameMark"); //
					}
					if ("WebPushLoadFrame".equals(str_clientprop) || "WebPushRootFrame".equals(str_clientprop)) { //如果是LoadFrame或者是RootFrame,则跳过处理!!!
						continue; //
					}

					String str_name = allFrames[i].getClass().getName(); //
					if (str_name.equals("javax.swing.SwingUtilities$SharedOwnerFrame") || str_name.equals("cn.com.weblight.applet.WLTAppletMainFrame")) { //
						continue; //
					}
					if (allFrames[i] == rootFrame) {
						continue;
					}
					allFrames[i].dispose(); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 退出系统,其实就是清空Session中值!!
	 */
	private void exitSys() {
		try {
			System.out.println("准备退出系统了！！！！！"); //
			//删除两个skip2登录模式产生的文件
			deleteSkip2File(); //

			String str_sessionid = System.getProperty("SESSIONID"); //
			//远程调用清空服务器端的Session缓存中的值,这样查看在线用户才会真正的准确!!!
			if (str_sessionid != null) {
				SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class, 10); //只等10秒,超过10秒就强行中断!都是为了保证能退出,因为很可能服务器停了!
				//退出系统，记录日志
				CurrLoginUserVO loginUserVO = ClientEnvironment.getCurrLoginUserVO();
				service.loginOut(loginUserVO.getId(), str_sessionid); //
			}
			Method md = Class.forName("cn.com.infostrategy.ui.mdata.QuickSearchHisMap").getDeclaredMethod("writeQuickSearchHisToCache"); //把搜索历史存放到客户端
			md.invoke(md, null);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 有时杀进程退出后,再启进程时就不会自动删除这两个文件了,所以要手动删除!!!!
	 */
	private void deleteSkip2File() {
		String str_fileName1 = System.getProperty("user.home") + "\\PushSkip2Menu.txt";
		String str_fileName2 = System.getProperty("user.home") + "\\PushSkip2Alive.txt";
		try {
			File file1 = new File(str_fileName1); //
			if (file1.exists()) {
				boolean delResult = file1.delete(); //
				System.out.println("删除文件[" + str_fileName1 + "],结果=[" + delResult + "]"); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		try {
			File file2 = new File(str_fileName2); //
			if (file2.exists()) {
				boolean delResult = file2.delete(); //
				System.out.println("删除文件[" + str_fileName2 + "],结果=[" + delResult + "]"); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//被客户端虚拟机反射调用的退出系统前的操作!!!
	public static void exitSysByClientJVMRefCall() {
		if (appletLoader != null) {
			appletLoader.exitSys();
		}
	}

	/**
	 * 刷新首页数据
	 */
	private void refreahIndexTaskGroup() {
		if (deskTopPanel != null) {
			//System.out.println("刷新首页...");  //
			((DeskTopPanel) deskTopPanel).refreshAllTaskGroup(); //刷新首页
		}
	}

	public JApplet getApplet() {
		return applet;
	}

	public void setApplet(JApplet applet) {
		this.applet = applet;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	private FrameWorkCommServiceIfc getCommService() throws Exception {
		return (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class); //定义远程服务
	}

	public static LoginAppletLoader getAppletLoader() {
		return appletLoader;
	}

	/**
	 * 取得SysAppService
	 * @return
	 */
	private SysAppServiceIfc getSysAppService() throws Exception {
		return (SysAppServiceIfc) RemoteServiceFactory.getInstance().lookUpService(SysAppServiceIfc.class); //定义远程服务
	}

}
