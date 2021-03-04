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
 * 所有的Web调用都是通过这个来调用的!!它是唯一的入口
 * 相当于RemoteCallServlet一样，所有的UI调用都是从那走一样
 * 这里存在一个两次调用,之所以需要进行两次调用,是因为：
 * 传参数时往往不是纯字符串的,而是包括各种VO对象,而使用浏览器打开一个html时,则只支持一个url,即只能用?key=value的格式送字符串参数!!这样就存在一个问题，即必须先进行一次远程调用，将参数送入，然后将html计算好，然后返回一个htmlcontentid
 * 然后使用jdic或直接运行浏览器url,打开页面！该Servlet这时只需从缓存中取出html输出即可!!这样实际上就进行了两次调用！！
 * 但如果是只传字符串的参数,则可以进行一次调用!!这时约定一个特定参数(StrParCallClassName)，然后包装成一个HashMap,传给实现类!!由实现类返回html文档,然后在UIUTil中封装一个方法,调用它时就会直接打开一个IE新窗口,并在其中输出实现类生成的html文档!
 * 这样做就会省去注册许多Servlet的麻烦,同时省去解析参数的麻烦!!
 * @author xch
 *
 */
public class WebCallServlet extends HttpServlet {

	private static final long serialVersionUID = -1459640247581941476L;

	private Logger logger = WLTLogger.getLogger(WebCallServlet.class);

	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		_request.setCharacterEncoding("GBK"); //
		_response.setBufferSize(51200); //50K缓冲区!
		String str_StrParCallClassName = _request.getParameter("StrParCallClassName"); //一性调用的
		if (str_StrParCallClassName != null) { //如果是纯字符串一次性方式调用的!!!
			doOneOffStrParCall(_request, _response, str_StrParCallClassName); //一次请求方式!!
		} else { //第一次注册缓存,第二次调用的方式!!!
			String str_callid = _request.getParameter("webcallid"); //
			String str_htmlContentid = _request.getParameter("htmlcontentid"); //
			if (str_callid != null) {
				doTwoOffWebCall(_request, _response, str_callid); //根据webcallid,去缓存中取得参数,然后执行实际类!输出
			} else if (str_htmlContentid != null) {
				doTwoOffHtmlContent(_request, _response, str_htmlContentid); //在后台已生成好了html,然后直接取得输出!!
			} else {
				_response.setCharacterEncoding("GBK"); //
				_response.setContentType("text/html"); //
				_response.getWriter().println("既没有取到webcallid参数,也没有取到htmlcontentid参数!"); //
			}
		}
	}

	/**
	 * 一次性纯字符串参数调用!
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
					parMap.put(str_key, str_value); //把16进制再返转成真正的字符串!
					str_allpars = str_allpars + "[" + str_key + "]=[" + str_value + "]<br>\r\n"; //
				}
			}
			String str_isdispatch = _request.getParameter("isdispatch"); //是否是转发的!即以前的方法是认为只返回html,但实际上有时想返回Word,pdf,图片,这时就需要转发整个request与response,然后自己去处理算了!
			Object objInst = Class.forName(str_realClassName).newInstance(); //
			if ("Y".equals(str_isdispatch) || (objInst instanceof WebDispatchIfc)) {  //万一忘记没有指定isdispatch=Y,但实际类又中WebDispatchIfc,则也强制认为是是转发的!更智能了!【xch/2012-05-14】
				WebDispatchIfc disPatchBean = (WebDispatchIfc) objInst; //创建实例!!
				disPatchBean.service(_request, _response, parMap); //
			} else {
				_response.setCharacterEncoding("GBK"); //
				_response.setContentType("text/html"); //
				String str_html = "<!-- 实际调用的页面生成类[" + str_realClassName + "](一次性纯字符串参数) -->\r\n"; ////
				WebCallBeanIfc bean = (WebCallBeanIfc) objInst; ////
				str_html = str_html + "<!--所有参数:" + str_allpars + "-->\r\n"; //
				str_html = str_html + bean.getHtmlContent(parMap); ////
				_response.getWriter().println(str_html);
			}
			initContext.commitAllTrans(); //提交所有事务..
		} catch (Exception e) {
			initContext.rollbackAllTrans(); //回滚所有事务..
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
	 *两次缓存方式! 取得WebCall的内容
	 */
	private void doTwoOffWebCall(HttpServletRequest _request, HttpServletResponse _response, String str_callid) throws ServletException, IOException {
		logger.info("开始调用WebCallServlet,取得webcallid=[" + str_callid + "]....."); //
		/**
		 *浏览器类型：【吴鹏2012-5-24】
		 *ie：Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)    
		 *搜狗：Mozilla/4.0 (compatible; MSIE 5.00; Windows 98; SoDA) 
		 *360：Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727; 360SE)
		 */
		String browserType=_request.getHeader("user-agent");
		if(browserType!=null){// 360SE\SoDA\.NET CLR 2.0.50727
			browserType=browserType.substring(browserType.lastIndexOf(";")+1,browserType.trim().length()-1);
		}
		
		TBUtil tbUtil = new TBUtil(); //
		WebCallParVO parVOFromCache = WebCallIDFactory.getInstance().getWebCallParVO(str_callid); //
		String str_html = "";
		if (parVOFromCache == null) {
			str_html = "<html><body>\r\n<!-- WebCallId=[" + str_callid + "] -->\r\n基于数据安全考虑,没有在服务器端缓存处理Session,请重新打开页面!</body></html>";
		} else {
			WLTInitContext initContext = new WLTInitContext(); //
			try {
				WebCallParVO parVO = (WebCallParVO) tbUtil.deepClone(parVOFromCache); //克隆一下!!!为下面删除作准备!!!
//				WebCallIDFactory.getInstance().clearWebCallSession(str_callid); //清除一下,释放内存!!但这样造成的后果就是第二次取的时候就没了!!但好象曾经遇到过有的浏览器(比如360等)在客户端总是循环请求，结果总是说找不到，结果文件不能下载,!!所以就不能有这行代码!
				String str_className = parVO.getCallClassName(); //取得类名
				java.util.HashMap parMap = parVO.getParsMap(); //取得参数!!
				String str_isdispatch = _request.getParameter("isdispatch"); //是否是转发的!即可能
				if ("Y".equals(str_isdispatch)) {
					WebDispatchIfc disPatchBean = (WebDispatchIfc) Class.forName(str_className).newInstance(); //创建实例!!
					disPatchBean.service(_request, _response, parMap); //
				} else {
					WebCallBeanIfc bean = (WebCallBeanIfc) Class.forName(str_className).newInstance(); //创建实例!!
					str_html = str_html + "<!-- 实际调用的页面生成类[" + str_className + "] -->\r\n"; //
					str_html = str_html + bean.getHtmlContent(parMap); //取得实际数据!!
					_response.setCharacterEncoding("GBK"); //
					_response.setContentType("text/html"); //
					_response.getWriter().println(str_html);
				}
				initContext.commitAllTrans(); //提交所有事务..
				if(!"SoDA".equalsIgnoreCase(browserType.trim())){//如果是搜狗浏览器，就不删除，要不下载不下来
					WebCallIDFactory.getInstance().clearWebCallSession(str_callid);//之前这句话放在146行，导致出了ie之外的其他浏览器不能下载合规手册【吴鹏2012-5-24】
				}
			} catch (Exception e) {
				initContext.rollbackAllTrans(); //回滚所有事务..
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
	 * 两次缓存方式,直接取得Html内容
	 * @param _request
	 * @param _response
	 * @param str_htmlcontentid
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doTwoOffHtmlContent(HttpServletRequest _request, HttpServletResponse _response, String str_htmlcontentid) throws ServletException, IOException {
		logger.info("开始调用WebCallServlet,取得htmlcontentid=[" + str_htmlcontentid + "]....."); //
		String str_html = WebCallIDFactory.getInstance().getHtmlContentByID(str_htmlcontentid); //
		String str_html2 = str_html; //克隆一下,为下面清空内存做准备
		WebCallIDFactory.getInstance().clearHtmlContentSession(str_htmlcontentid); //清空,释放内存!!
		_response.setCharacterEncoding("GBK"); //
		_response.setContentType("text/html"); //
		_response.getWriter().println(str_html2); //直接输出!!!没有任何逻辑!!!
	}

}
