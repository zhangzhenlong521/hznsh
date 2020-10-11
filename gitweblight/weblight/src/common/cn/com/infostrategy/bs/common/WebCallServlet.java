package cn.com.infostrategy.bs.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WebCallParVO;

/**
 * ���е�Web���ö���ͨ����������õ�!!����Ψһ�����
 * �൱��RemoteCallServletһ�������е�UI���ö��Ǵ�����һ��
 * �������һ�����ε���,֮������Ҫ�������ε���,����Ϊ��
 * ������ʱ�������Ǵ��ַ�����,���ǰ�������VO����,��ʹ���������һ��htmlʱ,��ֻ֧��һ��url,��ֻ����?key=value�ĸ�ʽ���ַ�������!!�����ʹ���һ�����⣬�������Ƚ���һ��Զ�̵��ã����������룬Ȼ��html����ã�Ȼ�󷵻�һ��htmlcontentid
 * Ȼ��ʹ��jdic��ֱ�����������url,��ҳ�棡��Servlet��ʱֻ��ӻ�����ȡ��html�������!!����ʵ���Ͼͽ��������ε��ã���
 * �������ֻ���ַ����Ĳ���,����Խ���һ�ε���!!��ʱԼ��һ���ض�����(StrParCallClassName)��Ȼ���װ��һ��HashMap,����ʵ����!!��ʵ���෵��html�ĵ�,Ȼ����UIUTil�з�װһ������,������ʱ�ͻ�ֱ�Ӵ�һ��IE�´���,�����������ʵ�������ɵ�html�ĵ�!
 * �������ͻ�ʡȥע�����Servlet���鷳,ͬʱʡȥ�����������鷳!!
 * @author xch
 *
 */
public class WebCallServlet extends HttpServlet {

	private static final long serialVersionUID = -1459640247581941476L;

	private Logger logger = WLTLogger.getLogger(WebCallServlet.class);

	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		_request.setCharacterEncoding("GBK"); //
		_response.setBufferSize(51200); //50K������!
		String str_StrParCallClassName = _request.getParameter("StrParCallClassName"); //һ�Ե��õ�
		if (str_StrParCallClassName != null) { //����Ǵ��ַ���һ���Է�ʽ���õ�!!!
			doOneOffStrParCall(_request, _response, str_StrParCallClassName); //һ������ʽ!!
		} else { //��һ��ע�Ỻ��,�ڶ��ε��õķ�ʽ!!!
			String str_callid = _request.getParameter("webcallid"); //
			String str_htmlContentid = _request.getParameter("htmlcontentid"); //
			if (str_callid != null) {
				doTwoOffWebCall(_request, _response, str_callid); //����webcallid,ȥ������ȡ�ò���,Ȼ��ִ��ʵ����!���
			} else if (str_htmlContentid != null) {
				doTwoOffHtmlContent(_request, _response, str_htmlContentid); //�ں�̨�����ɺ���html,Ȼ��ֱ��ȡ�����!!
			} else {
				_response.setCharacterEncoding("GBK"); //
				_response.setContentType("text/html"); //
				_response.getWriter().println("��û��ȡ��webcallid����,Ҳû��ȡ��htmlcontentid����!"); //
			}
		}
	}

	/**
	 * һ���Դ��ַ�����������!
	 * @param _request
	 * @param _response
	 * @param _StrParCallClassName
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doOneOffStrParCall(HttpServletRequest _request, HttpServletResponse _response, String _StrParCallClassName) throws ServletException, IOException {
		WLTInitContext initContext = new WLTInitContext();
		try {
			String str_realClassName = _StrParCallClassName; //tbUtil.convertHexStringToStr(_StrParCallClassName); //
			HashMap parMap = new HashMap(); //
			Enumeration enu = _request.getParameterNames();
			String str_allpars = ""; //
			while (enu.hasMoreElements()) {
				String str_key = (String) enu.nextElement(); //
				if (!str_key.equals("StrParCallClassName")) { //
					String str_value = _request.getParameter(str_key); //tbUtil.convertHexStringToStr(_request.getParameter(str_key));
					parMap.put(str_key, str_value); //��16�����ٷ�ת���������ַ���!
					str_allpars = str_allpars + "[" + str_key + "]=[" + str_value + "]<br>\r\n"; //
				}
			}
			String str_isdispatch = _request.getParameter("isdispatch"); //�Ƿ���ת����!����ǰ�ķ�������Ϊֻ����html,��ʵ������ʱ�뷵��Word,pdf,ͼƬ,��ʱ����Ҫת������request��response,Ȼ���Լ�ȥ��������!
			Object objInst = Class.forName(str_realClassName).newInstance(); //
			if ("Y".equals(str_isdispatch) || (objInst instanceof WebDispatchIfc)) {  //��һ����û��ָ��isdispatch=Y,��ʵ��������WebDispatchIfc,��Ҳǿ����Ϊ����ת����!��������!��xch/2012-05-14��
				WebDispatchIfc disPatchBean = (WebDispatchIfc) objInst; //����ʵ��!!
				disPatchBean.service(_request, _response, parMap); //
			} else {
				_response.setCharacterEncoding("GBK"); //
				_response.setContentType("text/html"); //
				String str_html = "<!-- ʵ�ʵ��õ�ҳ��������[" + str_realClassName + "](һ���Դ��ַ�������) -->\r\n"; ////
				WebCallBeanIfc bean = (WebCallBeanIfc) objInst; ////
				str_html = str_html + "<!--���в���:" + str_allpars + "-->\r\n"; //
				str_html = str_html + bean.getHtmlContent(parMap); ////
				_response.getWriter().println(str_html);
			}
			initContext.commitAllTrans(); //�ύ��������..
		} catch (Exception e) {
			initContext.rollbackAllTrans(); //�ع���������..
			e.printStackTrace();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintWriter(bos, true));
			byte exbytes[] = bos.toByteArray();
			bos.close();
			String sb_exstack = new String(exbytes, "GBK");
			sb_exstack = (new TBUtil()).replaceAll(sb_exstack, "\r", "<br>");
			StringBuffer sb_exception = new StringBuffer();
			sb_exception.append("<html>\r\n");
			sb_exception.append("<body>\r\n");
			sb_exception.append("<font size=2 color=\"red\">\r\n");
			sb_exception.append(sb_exstack);
			sb_exception.append("</font>\r\n");
			sb_exception.append("</body>\r\n");
			sb_exception.append("</html>\r\n");
			_response.setCharacterEncoding("GBK"); //
			_response.setContentType("text/html"); //
			_response.getWriter().println(sb_exception.toString());
		} finally {
			initContext.closeAllConn();
			initContext.release(); //
		}
	}

	/**
	 *���λ��淽ʽ! ȡ��WebCall������
	 */
	private void doTwoOffWebCall(HttpServletRequest _request, HttpServletResponse _response, String str_callid) throws ServletException, IOException {
		logger.info("��ʼ����WebCallServlet,ȡ��webcallid=[" + str_callid + "]....."); //
		/**
		 *��������ͣ�������2012-5-24��
		 *ie��Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)    
		 *�ѹ���Mozilla/4.0 (compatible; MSIE 5.00; Windows 98; SoDA) 
		 *360��Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727; 360SE)
		 */
		String browserType=_request.getHeader("user-agent");
		if(browserType!=null){// 360SE\SoDA\.NET CLR 2.0.50727
			browserType=browserType.substring(browserType.lastIndexOf(";")+1,browserType.trim().length()-1);
		}
		
		TBUtil tbUtil = new TBUtil(); //
		WebCallParVO parVOFromCache = WebCallIDFactory.getInstance().getWebCallParVO(str_callid); //
		String str_html = "";
		if (parVOFromCache == null) {
			str_html = "<html><body>\r\n<!-- WebCallId=[" + str_callid + "] -->\r\n�������ݰ�ȫ����,û���ڷ������˻��洦��Session,�����´�ҳ��!</body></html>";
		} else {
			WLTInitContext initContext = new WLTInitContext(); //
			try {
				WebCallParVO parVO = (WebCallParVO) tbUtil.deepClone(parVOFromCache); //��¡һ��!!!Ϊ����ɾ����׼��!!!
//				WebCallIDFactory.getInstance().clearWebCallSession(str_callid); //���һ��,�ͷ��ڴ�!!��������ɵĺ�����ǵڶ���ȡ��ʱ���û��!!�����������������е������(����360��)�ڿͻ�������ѭ�����󣬽������˵�Ҳ���������ļ���������,!!���ԾͲ��������д���!
				String str_className = parVO.getCallClassName(); //ȡ������
				java.util.HashMap parMap = parVO.getParsMap(); //ȡ�ò���!!
				String str_isdispatch = _request.getParameter("isdispatch"); //�Ƿ���ת����!������
				if ("Y".equals(str_isdispatch)) {
					WebDispatchIfc disPatchBean = (WebDispatchIfc) Class.forName(str_className).newInstance(); //����ʵ��!!
					disPatchBean.service(_request, _response, parMap); //
				} else {
					WebCallBeanIfc bean = (WebCallBeanIfc) Class.forName(str_className).newInstance(); //����ʵ��!!
					str_html = str_html + "<!-- ʵ�ʵ��õ�ҳ��������[" + str_className + "] -->\r\n"; //
					str_html = str_html + bean.getHtmlContent(parMap); //ȡ��ʵ������!!
					_response.setCharacterEncoding("GBK"); //
					_response.setContentType("text/html"); //
					_response.getWriter().println(str_html);
				}
				initContext.commitAllTrans(); //�ύ��������..
				if(!"SoDA".equalsIgnoreCase(browserType.trim())){//������ѹ���������Ͳ�ɾ����Ҫ�����ز�����
					WebCallIDFactory.getInstance().clearWebCallSession(str_callid);//֮ǰ��仰����146�У����³���ie֮�������������������غϹ��ֲ᡾����2012-5-24��
				}
			} catch (Exception e) {
				initContext.rollbackAllTrans(); //�ع���������..
				e.printStackTrace();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				e.printStackTrace(new PrintWriter(bos, true));
				byte exbytes[] = bos.toByteArray();
				bos.close();
				String sb_exstack = new String(exbytes, "GBK");
				sb_exstack = (new TBUtil()).replaceAll(sb_exstack, "\r", "<br>");
				StringBuffer sb_exception = new StringBuffer();
				sb_exception.append("<html>\r\n");
				sb_exception.append("<body>\r\n");
				sb_exception.append("<font size=2 color=\"red\">\r\n");
				sb_exception.append(sb_exstack);
				sb_exception.append("</font>\r\n");
				sb_exception.append("</body>\r\n");
				sb_exception.append("</html>\r\n");
				str_html = sb_exception.toString();
				_response.setCharacterEncoding("GBK"); //
				_response.setContentType("text/html"); //
				_response.getWriter().println(str_html);
			} finally {
				initContext.closeAllConn();
				initContext.release(); //
			}
		}
	}

	/**
	 * ���λ��淽ʽ,ֱ��ȡ��Html����
	 * @param _request
	 * @param _response
	 * @param str_htmlcontentid
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doTwoOffHtmlContent(HttpServletRequest _request, HttpServletResponse _response, String str_htmlcontentid) throws ServletException, IOException {
		logger.info("��ʼ����WebCallServlet,ȡ��htmlcontentid=[" + str_htmlcontentid + "]....."); //
		String str_html = WebCallIDFactory.getInstance().getHtmlContentByID(str_htmlcontentid); //
		String str_html2 = str_html; //��¡һ��,Ϊ��������ڴ���׼��
		WebCallIDFactory.getInstance().clearHtmlContentSession(str_htmlcontentid); //���,�ͷ��ڴ�!!
		_response.setCharacterEncoding("GBK"); //
		_response.setContentType("text/html"); //
		_response.getWriter().println(str_html2); //ֱ�����!!!û���κ��߼�!!!
	}

}
