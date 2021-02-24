package cn.com.infostrategy.bs.sysapp.login;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * 登录的Servlet，将以前的login.jsp与wlapplet.jsp并在一起了!
 * @author xch
 *
 */
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger logger = WLTLogger.getLogger(LoginServlet.class); ////
	public static HashMap clusterMap = new HashMap(); //记录集群访问时各个服务器:端口上的实际上线人数!!有重要的参考意义,即看是否真的产生平摊效果?

	@Override
	/**
	 * 客户端请求入口!!
	 */
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		_request.setCharacterEncoding("GBK"); //ISO-8859-1
		_response.setCharacterEncoding("GBK");
		_response.setContentType("text/html");
		_response.setBufferSize(10240); //10K

		StringBuffer sb_html = new StringBuffer(); //
		LoginJSPUtil jspUtil = new LoginJSPUtil(); //
		String str_loginType = _request.getParameter("logintype"); //登录模式!!!最关键的!!!
		String str_desktoplogin = _request.getParameter("desktopLogin"); //是否是桌面登录!!!
		String str_isAdmin = _request.getParameter("admin"); //是否是管理员身份
		if (ServerEnvironment.getProperty("MYSELFPORT") == null) {
			ServerEnvironment.setProperty("MYSELFPORT", "" + _request.getServerPort()); //将端口注册变量,因为在服务器端的一些线程经常需要知道自己的端口是多少,但恼人的是一直没有很好的办法获得自己的端口号,只能借用客户端请求来获得
		}
		if ("isdie".equals(str_loginType)) { //如果是判断是否活着的!!
			sb_html.append("<html><body>LoginServlet is alive!!!</body></html>"); //
		} else if ("getCurrThreads".equals(str_loginType)) { //如果是取得当前连接数!!!!
			sb_html.append(cn.com.infostrategy.bs.common.RemoteCallServlet.THREADCOUNT); //直接返回当前连接数!!!!!!!
		} else if ("getClusterPort".equals(str_loginType)) { //在实际访问之前第一次预先取一下信息,比如集群的端口号,最后启动时间
			String[] str_ip_port = new cn.com.infostrategy.bs.common.ClusterTool().getRealCallIPAndPort(_request.getParameter("inputip"), _request.getServerName(), _request.getServerPort(), _request.getContextPath()); //取得集群的端口号,如果是!
			String str_newIP = str_ip_port[0]; //集群的IP!
			String str_newPort = str_ip_port[1]; //集群的端口!
			String str_startVersion = ServerEnvironment.getProperty("LAST_STARTTIME"); //服务器端的版本号!
			String str_clientVersion = cn.com.weblight.applet.WLTAppletUtil.APPLETVIEWER_VERSION; //客户端的版本号!即用以判断服务器的最新客户端wltappletviewer.jar是否与客户端的一样??从而提示更新了客户端
			sb_html.append(str_newPort + "," + str_startVersion + "," + str_clientVersion + "," + str_newIP); //返回实际集群后的端口+当前服务器的版本号!!!后来升级后,增加了集群服务器的返回!
			//记录下各个服务器实际请求总数!
			String str_ipport_key = str_newIP + ":" + str_newPort; //
			if (clusterMap.containsKey(str_ipport_key)) { //记
				Long ll_count = (Long) clusterMap.get(str_ipport_key); //
				clusterMap.put(str_ipport_key, new Long(ll_count + 1)); //
			} else {
				clusterMap.put(str_ipport_key, new Long(1)); //
			}
		} else if ("getAVersion".equals(str_loginType)) { //客户端引擎的版本号等!!!
			sb_html.append(cn.com.weblight.applet.WLTAppletUtil.APPLETVIEWER_VERSION); ////返回实际集群后的端口+当前服务器的版本号!!!
		} else if ("IELogin".equals(str_loginType)) { //如果是单点登录,即直接进入主界面!
			String str_isCall2 = _request.getParameter("isCall2"); //看是否是第二次请求!!!
			if ("Y".equals(str_isCall2)) { //如果是第二次请求,则输出<applet>标签!!!
				sb_html.append(getAppletHtml(_request, _response, jspUtil, null)); //如果是第二次请求!!
			} else { //如果是IE
				sb_html.append("<html ><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"></head>\r\n");
				sb_html.append("<body>登录类型是\"IELogin\",但\"isCall2=" + str_isCall2 + ",不是Y,可能是密码有特殊符号造成的(比如有\"#\")!</body>\r\n"); //李国利在太平保险中遇到浏览器登录时,竟然不行,后来经过分析原因是用户密码太复杂时
				sb_html.append("</html >\r\n");
			}
		} else if ("single".equals(str_loginType)) { //如果是单点登录,即直接进入主界面!
			String str_isCall2 = _request.getParameter("isCall2"); //看是否是第二次请求!!!
			if (!"Y".equals(str_isCall2)) { //如果是第一次请求!!
				boolean isClose = "N".equalsIgnoreCase(_request.getParameter("isclose")) ? false : true; //默认是关闭自己,只能显示指定isClose=N,才不关闭自己!
				sb_html.append(jspUtil.getSingleLogin(_request, _response, isClose, null)); //默认是打开后立即关闭自己!
			} else {
				sb_html.append(getAppletHtml(_request, _response, jspUtil, null)); //如果是第二次请求!!
			}
		} else if ("skip".equals(str_loginType)) { //如果是跳转到自定义界面的!!!!!非常重要!!
			String str_isCall2 = _request.getParameter("isCall2"); //看是否是第二次请求!!!
			if (!"Y".equals(str_isCall2)) { //如果是第一次请求!!
				boolean isClose = "Y".equalsIgnoreCase(_request.getParameter("isclose")) ? true : false; //默认是不关闭自己,只能显示指定isClose=Y,才关闭自己!
				sb_html.append(jspUtil.getSingleLogin(_request, _response, isClose, "系统正在进行加载处理...")); //
			} else { //第二次请求,输出<applet>标签!!
				sb_html.append(getAppletHtml(_request, _response, jspUtil, "cn.com.infostrategy.ui.sysapp.login.LoadWltPanelLoader")); //如果是第二次请求,则强行指定特殊的loader!!
			}
		} else if ("skip2".equals(str_loginType)) { //如果是第二种跳转,即直接打开某个功能点,然后监听其他功能点,自动加载,它其实是在中铁建项目中做与EAC集成时,因为每次都加载启动过程,性能慢,所以想搞成单点登录与skip集成的一种模型!!
			String str_isCall2 = _request.getParameter("isCall2"); //看是否是第二次请求!!!第一次肯定没有!
			if (!"Y".equals(str_isCall2)) { //如果是第一次请求!!
				boolean isClose = "Y".equalsIgnoreCase(_request.getParameter("isclose")) ? true : false; //默认是不关闭自己,只能显示指定isClose=Y,才关闭自己!
				sb_html.append(jspUtil.getSingleLogin(_request, _response, isClose, null)); //默认是不关闭自己的!但以后可能会遇到关闭自己!比如弹出新窗口中
			} else {
				sb_html.append(getAppletHtml(_request, _response, jspUtil, null)); //如果是第二次请求!!
			}
		} else if ("desktop".equalsIgnoreCase(str_loginType) || "Y".equalsIgnoreCase(str_desktoplogin)) { //从桌面快捷方式过来的,支持新旧版本,最重要的逻辑!...
			sb_html.append(getAppletHtml(_request, _response, jspUtil, null)); //
		} else if ("activexsharejre".equals(str_loginType)) { //通过Active共享JRE
			sb_html.append(jspUtil.getActiveXShareJREHtml(_request)); //
		} else if ("help".equalsIgnoreCase(str_loginType)) { //如果是help
			sb_html.append(getHelpHtml()); //
		} else if ("monitoriecookie".equalsIgnoreCase(str_loginType)) { //从桌面快捷方式过来的,支持新旧版本,最重要的逻辑!...
			sb_html.append(jspUtil.getMonitorIECookieHtml()); //
		} else { //没有指定logintype,即第一次从浏览器方式进来的,则输出Html登录界面,即第一次默认的登录!!!
			if (_request.getParameter("help") != null) { //如果直接输入help参数!!!
				sb_html.append(getHelpHtml()); //
			} else {
				//耿东华在中铁建遇到一个需要,即科技部认为既然从OA单点登录了,则必须强行关闭默认登录方式!所以要加一个参数【xch/2012-05-30】
				if ("Y".equalsIgnoreCase(ServerEnvironment.getProperty("isCloseDefaultLogin")) && !"Y".equalsIgnoreCase(str_isAdmin)) { //如果有参数,指定是否关闭登录=Y，太平需要留个后面，故url增加admin=y即可登录系统，暂不支持客户端登录【李春娟/2016-12-29】
					sb_html.append("<html>\r\n<head><META http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"></head>\r\n");
					sb_html.append("<body><p style=\"font-size:12px\">\r\n" + ServerEnvironment.getProperty("CloseDefaultLoginWarn", "请从公司统一入口访问系统!") + "\r\n</p></body>\r\n"); //李国利在太平保险中遇到浏览器登录时,竟然不行,后来经过分析原因是用户密码太复杂时
					sb_html.append("</html >\r\n");
				} else {
					sb_html.append(jspUtil.getLoginPageHtml(_request, _response)); //即第一个登录界面!即浏览器输入/login出现的第一个界面!!
				}
			}
		}
		_response.getWriter().println(sb_html.toString()); //
	}

	/**
	 * 第二次请求的,返回包括<applet></applet>内容的html!!!
	 */
	private String getAppletHtml(HttpServletRequest _request, HttpServletResponse _response, LoginJSPUtil _jspUtil, String _loadClass) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<html >\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<title>WebPush IE</title>\r\n");
		sb_html.append("</head>\r\n");
		sb_html.append("<body>\r\n");
		sb_html.append("<font size=\"2\">Load WLTApplet....</font><br></br>\r\n");
		sb_html.append("[LocalAddr()]=[" + _request.getLocalAddr() + "]<br>\r\n"); //
		sb_html.append("[LocalName()]=[" + _request.getLocalName() + "]<br>\r\n"); //
		sb_html.append("[LocalPort()]=[" + _request.getLocalPort() + "]<br>\r\n"); //
		sb_html.append("[RemoteAddr()]=[" + _request.getRemoteAddr() + "]<br>\r\n"); //
		sb_html.append("[RemoteHost()]=[" + _request.getRemoteHost() + "]<br>\r\n"); //
		sb_html.append("[RemotePort()]=[" + _request.getRemotePort() + "]<br>\r\n"); //
		sb_html.append("[ServerName()]=[" + _request.getServerName() + "]*<br>\r\n"); //
		sb_html.append("[ServerPort()]=[" + _request.getServerPort() + "]*<br>\r\n"); //请求的端口号,比如安全服务器做了从9001重定向至8005的话,则返回的是9001
		sb_html.append("[ServletPath()]=[" + _request.getServletPath() + "]<br>\r\n"); //
		sb_html.append("<Applet code=\"cn.com.infostrategy.wlappletloader.ui.WebLightAppletContainer.class\" width=\"800\" height=\"500\">\r\n");
		sb_html.append("  <PARAM NAME=\"code\"                  VALUE=\"cn.com.infostrategy.wlappletloader.ui.WebLightAppletContainer.class\" />\r\n"); //
		sb_html.append("  <PARAM NAME=\"archive\"               VALUE=\"./applet/wlapplet.jar\" />\r\n"); //
		sb_html.append("  <PARAM NAME=\"type\"                  VALUE=\"application/x-java-applet;version=1.6\" />\r\n"); //
		sb_html.append(_jspUtil.getAppletObjectParams(_request, _response, _loadClass)); //

		//request中传过来的所有变量,也输出来!然后一起传给Swing中的Panel!!!
		Map allParMap = _request.getParameterMap(); //
		String[] str_parKeys = (String[]) allParMap.keySet().toArray(new String[0]); //
		sb_html.append("\r\n  <!-- 输出从Request中传过来的所有参数!! -->\r\n"); //
		for (int i = 0; i < str_parKeys.length; i++) { //
			String str_value = ((String[]) allParMap.get(str_parKeys[i]))[0]; //
			try {
				str_value = new String(str_value.getBytes("ISO-8859-1"), "UTF-8"); //""好象必须要转一下!!!否则中文乱码,在航天项目中传入前就转成了16进制,所以更稳定,但经过这行处理也是没有问题的!
				//System.out.println("客户端传入的参数[" + str_parKeys[i] + "]=[" + str_value + "]"); //Debug中文是否乱码了??
			} catch (Exception ex) {
				System.err.println("LoginServlet.getAppletHtml()转换字符发生异常:" + ex.getClass() + ":" + ex.getMessage()); //
			}
			sb_html.append("  <PARAM NAME=\"REQ_" + str_parKeys[i] + "\"" + _jspUtil.getSpace(str_parKeys[i]) + "VALUE=\"" + str_value + "\" />\r\n"); //输出!!!
		}
		sb_html.append("</Applet>\r\n"); //结束标签
		sb_html.append("</body>\r\n");
		sb_html.append("</html>"); //
		return sb_html.toString(); //
	}

	/**
	 * 有时开发人员不知道单点登录与直接跳转如果搞,这里提供一个帮助说明!!
	 * @return
	 */
	private String getHelpHtml() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<html ><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"></head>\r\n");
		sb_html.append("<body>\r\n");
		sb_html.append("/login?logintype=single&usercode=admin  表示是单点登录,可以跳过登录界面而直接进入主界面!<br><br>\r\n"); //
		sb_html.append("/login?logintype=skip&menuid=123&usercode=admin 可以直接打开某个菜单功能点,而跳过登录界面与主界面!usercode如果不设,则不进行登录逻辑,在对应界面中也将取不到登录人员信息!<br><br>\r\n"); //
		sb_html.append("/login?logintype=skip&clsname=cn.com.pushworld.grc.TestWKPanel&usercode=admin 可以直接打开某个类!usercode如果不设,则不进行登录逻辑,在对应界面中也将取不到登录人员信息!<br><br>\r\n"); //
		sb_html.append("</body >\r\n");
		sb_html.append("</html >\r\n");
		return sb_html.toString(); //
	}
}
