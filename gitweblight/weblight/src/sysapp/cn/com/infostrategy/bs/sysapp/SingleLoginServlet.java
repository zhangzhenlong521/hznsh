package cn.com.infostrategy.bs.sysapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 单点登录的Servlet,以前的单点登录逻辑是自己写个jsp,但还是不少人不熟悉!!
 * 所以封装一下! 核心思想是定义一个类,然后将Request传过去,然后通过门户系统提供的API得到登录用户,然后返回,然后本界面再客户端跳转至./login?logintype=single&usercode=%取得的变量%
 * 在./login?logintype=single 中就是调到注册表执行本地程序!!
 * 在Web.xml注册该Servlet的url就是/sllogin
 * @author xch
 *
 */
public class SingleLoginServlet extends HttpServlet {

	private static final long serialVersionUID = 8849653314352875697L;

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		_response.setCharacterEncoding("GBK"); //字符集
		PrintWriter out = _response.getWriter(); //

		String str_user = null; //
		//通过反射取得定义的类,然后取得登录用户名!比如民生就是 
		//Cookie[] ck = request.getCookies();
		//String reqSid = request.getParameter("tokenId");
		//URL url = new URL("http://s1as.cmbc.com.cn:81/AuthServlet/authservlet");
		//userProp.load(new DataInputStream(con.getInputStream()));
		//String str_loginuser = userProp.getProperty("userid");

		String str_singleLoginClass = ServerEnvironment.getProperty("SINGLELOGINCLASS"); //单点登录的的类名!!
		if (str_singleLoginClass == null || str_singleLoginClass.trim().equals("")) {
			out.println(getHtml("必须在weblight.xm中定义参数[SINGLELOGINCLASS],该参数是一个普通类名(即不需要继承任何接口或抽象类,平台将直接反射调用)!<br>\r\n但该类必须有个方法叫【public String getLoginUser(HttpServletRequest _request) throws Exception{}】,然后在该类中使用门户提供的API取得登录用户名返回即可!")); //
			return; //
		}

		try {
			Class cls = Class.forName(str_singleLoginClass); //
			Method method = cls.getMethod("getLoginUser", new Class[] { HttpServletRequest.class }); //方法!!
			Object obj = cls.newInstance(); //
			str_user = (String) method.invoke(obj, new Object[] { _request }); ////
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			out.println(getErorrHtml("创建并调用单点登录处理类[" + str_singleLoginClass + "]时发生异常:", _ex)); //
			return; //
		}

		if (str_user == null) {
			out.println(getHtml("没有从门户取得登录用户名,请与管理员联系!")); //
			return; //
		}

		out.println("<html>\r\n");
		out.println("<head>\r\n");
		out.println("<title>单点登陆</title>\r\n");
		out.println("<meta http-equiv=\"refresh\" content=\"1;url=./login?logintype=single&usercode=" + str_user + "\">\r\n");
		out.println("</head>\r\n");
		out.println("<body>\r\n");
		out.println("<font color=blue size=2>成功取得登录用户[" + str_user + "],1秒后系统将自动跳转...</font>\r\n");
		out.println("</body>\r\n"); //
		out.println("</html>\r\n"); //
	}

	private String getHtml(String _text) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<title>单点登陆</title>\r\n");
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("</head>\r\n");
		sb_html.append("<body>\r\n");
		sb_html.append("<font color=red size=2>\r\n" + _text + "\r\n</font>\r\n");
		sb_html.append("</body>\r\n"); //
		sb_html.append("</html>\r\n"); //
		return sb_html.toString(); //
	}

	//取得异常信息!
	private String getErorrHtml(String _text, Exception _ex) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<title>单点登陆</title>\r\n");
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("</head>\r\n");
		sb_html.append("<body>\r\n"); //
		sb_html.append("<body>\r\n"); //
		sb_html.append("<font color=red size=2>" + _text + "</font><br>\r\n"); //
		sb_html.append(new TBUtil().getExceptionStringBuffer(_ex, true, false)); //
		sb_html.append("</body>\r\n"); //
		sb_html.append("</html>\r\n"); //
		return sb_html.toString(); //
	}

}
