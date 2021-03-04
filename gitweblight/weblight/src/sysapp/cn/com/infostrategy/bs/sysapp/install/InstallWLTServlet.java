package cn.com.infostrategy.bs.sysapp.install;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.InitParamVO;

/**
 * ��װ����!!
 * @author xch
 *
 */
public class InstallWLTServlet extends HttpServlet {
	private static final long serialVersionUID = 3480058184705757565L;

	@Override
	/**
	 * �ͻ����������
	 */
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		String str_serverName = _request.getServerName(); //����������!!
		int li_port = _request.getServerPort(); //�˿ں�!!
		String str_context = _request.getContextPath(); //WebӦ��!!
		String str_html = getDefaultHtml("cn.com.infostrategy.ui.sysapp.install.ModuleStatusWKPanel", str_serverName, li_port, str_context); //

		_response.setCharacterEncoding("GBK");
		_response.setContentType("text/html");
		_response.getWriter().println(str_html); //���
	}

	/**
	 * һ��Ĭ�ϱ�׼��html���!!!
	 * @param _loadClass
	 * @param _serverName
	 * @param _port
	 * @param _context
	 * @return
	 */
	public String getDefaultHtml(String _loadClass, String _serverName, int _port, String _context) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<TITLE>��װ����</TITLE>\r\n"); //������
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("</head>\r\n");
		sb_html.append("\r\n");
		sb_html.append("<body>\r\n");
		sb_html.append("<Applet code=\"cn.com.infostrategy.wlappletloader.ui.WebLightAppletContainer.class\" width=\"800\" height=\"500\">\r\n"); //
		sb_html.append("  <PARAM NAME=\"code\"                  VALUE=\"cn.com.infostrategy.wlappletloader.ui.WebLightAppletContainer.class\" />\r\n"); //����Ҫ��
		sb_html.append("  <PARAM NAME=\"archive\"               VALUE=\"./applet/wlapplet.jar\" />\r\n"); //
		sb_html.append("  <PARAM NAME=\"type\"                  VALUE=\"application/x-java-applet;version=1.6\" />\r\n");

		//��һ����©���������weblight.xml�ж����init-param
		InitParamVO[] initParmVOs = ServerEnvironment.getInstance().getInitParamVOs(); //
		sb_html.append("\r\n  <!-- ��ʼ�������weblight.xml�е�init-param -->\r\n"); //
		for (int i = 0; i < initParmVOs.length; i++) { //
			sb_html.append("  <PARAM NAME=\"" + initParmVOs[i].getKey() + "\"  " + getSpace(initParmVOs[i].getKey()) + "VALUE=\"" + initParmVOs[i].getValue() + "\" />\r\n"); //��һ����©���������weblight.xml�����init-param
		}
		sb_html.append("  <!-- �������weblight.xml�е�init-param���� -->\r\n\r\n"); //

		sb_html.append("  <PARAM NAME=\"SERVERCLIENTVERSION\"   VALUE=\"1.6.0_18\" />\r\n"); //����Ҫ��
		sb_html.append("  <PARAM NAME=\"URL\"                   VALUE=\"http://" + _serverName + ":" + _port + _context + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"CALLURL\"               VALUE=\"http://" + _serverName + ":" + _port + _context + "\" />\r\n");
		sb_html.append("  <PARAM NAME=\"APP_CONTEXT\"           VALUE=\"" + _context + "\" />\r\n"); //����Ҫ��
		sb_html.append("  <PARAM NAME=\"SERVER_HOST_NAME\"      VALUE=\"" + _serverName + "\" />\r\n"); //
		sb_html.append("  <PARAM NAME=\"SERVER_PORT\"           VALUE=\"" + _port + "\" />\r\n"); //
		sb_html.append("  <PARAM NAME=\"LoaderAppletClass\"     VALUE=\"" + _loadClass + "\" />\r\n"); //��װ��,����:cn.com.infostrategy.ui.sysapp.install.InstallLoader
		sb_html.append("</Applet>\r\n");
		sb_html.append("</body>\r\n");
		sb_html.append("</html>\r\n");
		return sb_html.toString(); //
	}

	//��ȫ�ո�!! �Եö���ÿ�!!!
	private String getSpace(String _key) {
		int li_length = _key.length(); //
		String str_space = "";
		for (int i = 0; i < (30 - li_length); i++) {
			str_space = str_space + " ";
		}
		return str_space;
	}

}
