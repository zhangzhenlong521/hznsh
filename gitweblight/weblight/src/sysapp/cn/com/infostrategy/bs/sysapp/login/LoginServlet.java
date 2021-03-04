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
 * ��¼��Servlet������ǰ��login.jsp��wlapplet.jsp����һ����!
 * @author xch
 *
 */
public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static Logger logger = WLTLogger.getLogger(LoginServlet.class); ////
	public static HashMap clusterMap = new HashMap(); //��¼��Ⱥ����ʱ����������:�˿��ϵ�ʵ����������!!����Ҫ�Ĳο�����,�����Ƿ���Ĳ���ƽ̯Ч��?

	@Override
	/**
	 * �ͻ����������!!
	 */
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		_request.setCharacterEncoding("GBK"); //ISO-8859-1
		_response.setCharacterEncoding("GBK");
		_response.setContentType("text/html");
		_response.setBufferSize(10240); //10K

		StringBuffer sb_html = new StringBuffer(); //
		LoginJSPUtil jspUtil = new LoginJSPUtil(); //
		String str_loginType = _request.getParameter("logintype"); //��¼ģʽ!!!��ؼ���!!!
		String str_desktoplogin = _request.getParameter("desktopLogin"); //�Ƿ��������¼!!!
		String str_isAdmin = _request.getParameter("admin"); //�Ƿ��ǹ���Ա���
		if (ServerEnvironment.getProperty("MYSELFPORT") == null) {
			ServerEnvironment.setProperty("MYSELFPORT", "" + _request.getServerPort()); //���˿�ע�����,��Ϊ�ڷ������˵�һЩ�߳̾�����Ҫ֪���Լ��Ķ˿��Ƕ���,�����˵���һֱû�кܺõİ취����Լ��Ķ˿ں�,ֻ�ܽ��ÿͻ������������
		}
		if ("isdie".equals(str_loginType)) { //������ж��Ƿ���ŵ�!!
			sb_html.append("<html><body>LoginServlet is alive!!!</body></html>"); //
		} else if ("getCurrThreads".equals(str_loginType)) { //�����ȡ�õ�ǰ������!!!!
			sb_html.append(cn.com.infostrategy.bs.common.RemoteCallServlet.THREADCOUNT); //ֱ�ӷ��ص�ǰ������!!!!!!!
		} else if ("getClusterPort".equals(str_loginType)) { //��ʵ�ʷ���֮ǰ��һ��Ԥ��ȡһ����Ϣ,���缯Ⱥ�Ķ˿ں�,�������ʱ��
			String[] str_ip_port = new cn.com.infostrategy.bs.common.ClusterTool().getRealCallIPAndPort(_request.getParameter("inputip"), _request.getServerName(), _request.getServerPort(), _request.getContextPath()); //ȡ�ü�Ⱥ�Ķ˿ں�,�����!
			String str_newIP = str_ip_port[0]; //��Ⱥ��IP!
			String str_newPort = str_ip_port[1]; //��Ⱥ�Ķ˿�!
			String str_startVersion = ServerEnvironment.getProperty("LAST_STARTTIME"); //�������˵İ汾��!
			String str_clientVersion = cn.com.weblight.applet.WLTAppletUtil.APPLETVIEWER_VERSION; //�ͻ��˵İ汾��!�������жϷ����������¿ͻ���wltappletviewer.jar�Ƿ���ͻ��˵�һ��??�Ӷ���ʾ�����˿ͻ���
			sb_html.append(str_newPort + "," + str_startVersion + "," + str_clientVersion + "," + str_newIP); //����ʵ�ʼ�Ⱥ��Ķ˿�+��ǰ�������İ汾��!!!����������,�����˼�Ⱥ�������ķ���!
			//��¼�¸���������ʵ����������!
			String str_ipport_key = str_newIP + ":" + str_newPort; //
			if (clusterMap.containsKey(str_ipport_key)) { //��
				Long ll_count = (Long) clusterMap.get(str_ipport_key); //
				clusterMap.put(str_ipport_key, new Long(ll_count + 1)); //
			} else {
				clusterMap.put(str_ipport_key, new Long(1)); //
			}
		} else if ("getAVersion".equals(str_loginType)) { //�ͻ�������İ汾�ŵ�!!!
			sb_html.append(cn.com.weblight.applet.WLTAppletUtil.APPLETVIEWER_VERSION); ////����ʵ�ʼ�Ⱥ��Ķ˿�+��ǰ�������İ汾��!!!
		} else if ("IELogin".equals(str_loginType)) { //����ǵ����¼,��ֱ�ӽ���������!
			String str_isCall2 = _request.getParameter("isCall2"); //���Ƿ��ǵڶ�������!!!
			if ("Y".equals(str_isCall2)) { //����ǵڶ�������,�����<applet>��ǩ!!!
				sb_html.append(getAppletHtml(_request, _response, jspUtil, null)); //����ǵڶ�������!!
			} else { //�����IE
				sb_html.append("<html ><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"></head>\r\n");
				sb_html.append("<body>��¼������\"IELogin\",��\"isCall2=" + str_isCall2 + ",����Y,���������������������ɵ�(������\"#\")!</body>\r\n"); //�������̫ƽ�����������������¼ʱ,��Ȼ����,������������ԭ�����û�����̫����ʱ
				sb_html.append("</html >\r\n");
			}
		} else if ("single".equals(str_loginType)) { //����ǵ����¼,��ֱ�ӽ���������!
			String str_isCall2 = _request.getParameter("isCall2"); //���Ƿ��ǵڶ�������!!!
			if (!"Y".equals(str_isCall2)) { //����ǵ�һ������!!
				boolean isClose = "N".equalsIgnoreCase(_request.getParameter("isclose")) ? false : true; //Ĭ���ǹر��Լ�,ֻ����ʾָ��isClose=N,�Ų��ر��Լ�!
				sb_html.append(jspUtil.getSingleLogin(_request, _response, isClose, null)); //Ĭ���Ǵ򿪺������ر��Լ�!
			} else {
				sb_html.append(getAppletHtml(_request, _response, jspUtil, null)); //����ǵڶ�������!!
			}
		} else if ("skip".equals(str_loginType)) { //�������ת���Զ�������!!!!!�ǳ���Ҫ!!
			String str_isCall2 = _request.getParameter("isCall2"); //���Ƿ��ǵڶ�������!!!
			if (!"Y".equals(str_isCall2)) { //����ǵ�һ������!!
				boolean isClose = "Y".equalsIgnoreCase(_request.getParameter("isclose")) ? true : false; //Ĭ���ǲ��ر��Լ�,ֻ����ʾָ��isClose=Y,�Źر��Լ�!
				sb_html.append(jspUtil.getSingleLogin(_request, _response, isClose, "ϵͳ���ڽ��м��ش���...")); //
			} else { //�ڶ�������,���<applet>��ǩ!!
				sb_html.append(getAppletHtml(_request, _response, jspUtil, "cn.com.infostrategy.ui.sysapp.login.LoadWltPanelLoader")); //����ǵڶ�������,��ǿ��ָ�������loader!!
			}
		} else if ("skip2".equals(str_loginType)) { //����ǵڶ�����ת,��ֱ�Ӵ�ĳ�����ܵ�,Ȼ������������ܵ�,�Զ�����,����ʵ������������Ŀ������EAC����ʱ,��Ϊÿ�ζ�������������,������,�������ɵ����¼��skip���ɵ�һ��ģ��!!
			String str_isCall2 = _request.getParameter("isCall2"); //���Ƿ��ǵڶ�������!!!��һ�ο϶�û��!
			if (!"Y".equals(str_isCall2)) { //����ǵ�һ������!!
				boolean isClose = "Y".equalsIgnoreCase(_request.getParameter("isclose")) ? true : false; //Ĭ���ǲ��ر��Լ�,ֻ����ʾָ��isClose=Y,�Źر��Լ�!
				sb_html.append(jspUtil.getSingleLogin(_request, _response, isClose, null)); //Ĭ���ǲ��ر��Լ���!���Ժ���ܻ������ر��Լ�!���絯���´�����
			} else {
				sb_html.append(getAppletHtml(_request, _response, jspUtil, null)); //����ǵڶ�������!!
			}
		} else if ("desktop".equalsIgnoreCase(str_loginType) || "Y".equalsIgnoreCase(str_desktoplogin)) { //�������ݷ�ʽ������,֧���¾ɰ汾,����Ҫ���߼�!...
			sb_html.append(getAppletHtml(_request, _response, jspUtil, null)); //
		} else if ("activexsharejre".equals(str_loginType)) { //ͨ��Active����JRE
			sb_html.append(jspUtil.getActiveXShareJREHtml(_request)); //
		} else if ("help".equalsIgnoreCase(str_loginType)) { //�����help
			sb_html.append(getHelpHtml()); //
		} else if ("monitoriecookie".equalsIgnoreCase(str_loginType)) { //�������ݷ�ʽ������,֧���¾ɰ汾,����Ҫ���߼�!...
			sb_html.append(jspUtil.getMonitorIECookieHtml()); //
		} else { //û��ָ��logintype,����һ�δ��������ʽ������,�����Html��¼����,����һ��Ĭ�ϵĵ�¼!!!
			if (_request.getParameter("help") != null) { //���ֱ������help����!!!
				sb_html.append(getHelpHtml()); //
			} else {
				//������������������һ����Ҫ,���Ƽ�����Ϊ��Ȼ��OA�����¼��,�����ǿ�йر�Ĭ�ϵ�¼��ʽ!����Ҫ��һ��������xch/2012-05-30��
				if ("Y".equalsIgnoreCase(ServerEnvironment.getProperty("isCloseDefaultLogin")) && !"Y".equalsIgnoreCase(str_isAdmin)) { //����в���,ָ���Ƿ�رյ�¼=Y��̫ƽ��Ҫ�������棬��url����admin=y���ɵ�¼ϵͳ���ݲ�֧�ֿͻ��˵�¼�����/2016-12-29��
					sb_html.append("<html>\r\n<head><META http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"></head>\r\n");
					sb_html.append("<body><p style=\"font-size:12px\">\r\n" + ServerEnvironment.getProperty("CloseDefaultLoginWarn", "��ӹ�˾ͳһ��ڷ���ϵͳ!") + "\r\n</p></body>\r\n"); //�������̫ƽ�����������������¼ʱ,��Ȼ����,������������ԭ�����û�����̫����ʱ
					sb_html.append("</html >\r\n");
				} else {
					sb_html.append(jspUtil.getLoginPageHtml(_request, _response)); //����һ����¼����!�����������/login���ֵĵ�һ������!!
				}
			}
		}
		_response.getWriter().println(sb_html.toString()); //
	}

	/**
	 * �ڶ��������,���ذ���<applet></applet>���ݵ�html!!!
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
		sb_html.append("[ServerPort()]=[" + _request.getServerPort() + "]*<br>\r\n"); //����Ķ˿ں�,���簲ȫ���������˴�9001�ض�����8005�Ļ�,�򷵻ص���9001
		sb_html.append("[ServletPath()]=[" + _request.getServletPath() + "]<br>\r\n"); //
		sb_html.append("<Applet code=\"cn.com.infostrategy.wlappletloader.ui.WebLightAppletContainer.class\" width=\"800\" height=\"500\">\r\n");
		sb_html.append("  <PARAM NAME=\"code\"                  VALUE=\"cn.com.infostrategy.wlappletloader.ui.WebLightAppletContainer.class\" />\r\n"); //
		sb_html.append("  <PARAM NAME=\"archive\"               VALUE=\"./applet/wlapplet.jar\" />\r\n"); //
		sb_html.append("  <PARAM NAME=\"type\"                  VALUE=\"application/x-java-applet;version=1.6\" />\r\n"); //
		sb_html.append(_jspUtil.getAppletObjectParams(_request, _response, _loadClass)); //

		//request�д����������б���,Ҳ�����!Ȼ��һ�𴫸�Swing�е�Panel!!!
		Map allParMap = _request.getParameterMap(); //
		String[] str_parKeys = (String[]) allParMap.keySet().toArray(new String[0]); //
		sb_html.append("\r\n  <!-- �����Request�д����������в���!! -->\r\n"); //
		for (int i = 0; i < str_parKeys.length; i++) { //
			String str_value = ((String[]) allParMap.get(str_parKeys[i]))[0]; //
			try {
				str_value = new String(str_value.getBytes("ISO-8859-1"), "UTF-8"); //""�������Ҫתһ��!!!������������,�ں�����Ŀ�д���ǰ��ת����16����,���Ը��ȶ�,���������д���Ҳ��û�������!
				//System.out.println("�ͻ��˴���Ĳ���[" + str_parKeys[i] + "]=[" + str_value + "]"); //Debug�����Ƿ�������??
			} catch (Exception ex) {
				System.err.println("LoginServlet.getAppletHtml()ת���ַ������쳣:" + ex.getClass() + ":" + ex.getMessage()); //
			}
			sb_html.append("  <PARAM NAME=\"REQ_" + str_parKeys[i] + "\"" + _jspUtil.getSpace(str_parKeys[i]) + "VALUE=\"" + str_value + "\" />\r\n"); //���!!!
		}
		sb_html.append("</Applet>\r\n"); //������ǩ
		sb_html.append("</body>\r\n");
		sb_html.append("</html>"); //
		return sb_html.toString(); //
	}

	/**
	 * ��ʱ������Ա��֪�������¼��ֱ����ת�����,�����ṩһ������˵��!!
	 * @return
	 */
	private String getHelpHtml() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<html ><head><META http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"></head>\r\n");
		sb_html.append("<body>\r\n");
		sb_html.append("/login?logintype=single&usercode=admin  ��ʾ�ǵ����¼,����������¼�����ֱ�ӽ���������!<br><br>\r\n"); //
		sb_html.append("/login?logintype=skip&menuid=123&usercode=admin ����ֱ�Ӵ�ĳ���˵����ܵ�,��������¼������������!usercode�������,�򲻽��е�¼�߼�,�ڶ�Ӧ������Ҳ��ȡ������¼��Ա��Ϣ!<br><br>\r\n"); //
		sb_html.append("/login?logintype=skip&clsname=cn.com.pushworld.grc.TestWKPanel&usercode=admin ����ֱ�Ӵ�ĳ����!usercode�������,�򲻽��е�¼�߼�,�ڶ�Ӧ������Ҳ��ȡ������¼��Ա��Ϣ!<br><br>\r\n"); //
		sb_html.append("</body >\r\n");
		sb_html.append("</html >\r\n");
		return sb_html.toString(); //
	}
}
