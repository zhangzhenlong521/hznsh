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
 * �����¼��Servlet,��ǰ�ĵ����¼�߼����Լ�д��jsp,�����ǲ����˲���Ϥ!!
 * ���Է�װһ��! ����˼���Ƕ���һ����,Ȼ��Request����ȥ,Ȼ��ͨ���Ż�ϵͳ�ṩ��API�õ���¼�û�,Ȼ�󷵻�,Ȼ�󱾽����ٿͻ�����ת��./login?logintype=single&usercode=%ȡ�õı���%
 * ��./login?logintype=single �о��ǵ���ע���ִ�б��س���!!
 * ��Web.xmlע���Servlet��url����/sllogin
 * @author xch
 *
 */
public class SingleLoginServlet extends HttpServlet {

	private static final long serialVersionUID = 8849653314352875697L;

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		_response.setCharacterEncoding("GBK"); //�ַ���
		PrintWriter out = _response.getWriter(); //

		String str_user = null; //
		//ͨ������ȡ�ö������,Ȼ��ȡ�õ�¼�û���!������������ 
		//Cookie[] ck = request.getCookies();
		//String reqSid = request.getParameter("tokenId");
		//URL url = new URL("http://s1as.cmbc.com.cn:81/AuthServlet/authservlet");
		//userProp.load(new DataInputStream(con.getInputStream()));
		//String str_loginuser = userProp.getProperty("userid");

		String str_singleLoginClass = ServerEnvironment.getProperty("SINGLELOGINCLASS"); //�����¼�ĵ�����!!
		if (str_singleLoginClass == null || str_singleLoginClass.trim().equals("")) {
			out.println(getHtml("������weblight.xm�ж������[SINGLELOGINCLASS],�ò�����һ����ͨ����(������Ҫ�̳��κνӿڻ������,ƽ̨��ֱ�ӷ������)!<br>\r\n����������и������С�public String getLoginUser(HttpServletRequest _request) throws Exception{}��,Ȼ���ڸ�����ʹ���Ż��ṩ��APIȡ�õ�¼�û������ؼ���!")); //
			return; //
		}

		try {
			Class cls = Class.forName(str_singleLoginClass); //
			Method method = cls.getMethod("getLoginUser", new Class[] { HttpServletRequest.class }); //����!!
			Object obj = cls.newInstance(); //
			str_user = (String) method.invoke(obj, new Object[] { _request }); ////
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			out.println(getErorrHtml("���������õ����¼������[" + str_singleLoginClass + "]ʱ�����쳣:", _ex)); //
			return; //
		}

		if (str_user == null) {
			out.println(getHtml("û�д��Ż�ȡ�õ�¼�û���,�������Ա��ϵ!")); //
			return; //
		}

		out.println("<html>\r\n");
		out.println("<head>\r\n");
		out.println("<title>�����½</title>\r\n");
		out.println("<meta http-equiv=\"refresh\" content=\"1;url=./login?logintype=single&usercode=" + str_user + "\">\r\n");
		out.println("</head>\r\n");
		out.println("<body>\r\n");
		out.println("<font color=blue size=2>�ɹ�ȡ�õ�¼�û�[" + str_user + "],1���ϵͳ���Զ���ת...</font>\r\n");
		out.println("</body>\r\n"); //
		out.println("</html>\r\n"); //
	}

	private String getHtml(String _text) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<title>�����½</title>\r\n");
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("</head>\r\n");
		sb_html.append("<body>\r\n");
		sb_html.append("<font color=red size=2>\r\n" + _text + "\r\n</font>\r\n");
		sb_html.append("</body>\r\n"); //
		sb_html.append("</html>\r\n"); //
		return sb_html.toString(); //
	}

	//ȡ���쳣��Ϣ!
	private String getErorrHtml(String _text, Exception _ex) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<title>�����½</title>\r\n");
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
