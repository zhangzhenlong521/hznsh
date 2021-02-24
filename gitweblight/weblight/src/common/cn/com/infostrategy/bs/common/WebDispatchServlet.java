package cn.com.infostrategy.bs.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 为了省去在Web.xml不断的注册servlet,专门提供了一个分派器!
 * 使用分派器的好处是在这里处理了数据库连接氾的释放!!
 * @author xch
 *
 */
public class WebDispatchServlet extends HttpServlet {

	private static final long serialVersionUID = -7995259787164186009L;

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		WLTInitContext initContext = new WLTInitContext(); //
		try {
			_request.setCharacterEncoding("GBK"); //
			String str_className = _request.getParameter("cls"); //
			if (str_className == null || str_className.trim().equals("")) {
				_response.setCharacterEncoding("GBK"); //
				_response.setContentType("text/html"); //
				_response.getWriter().println("必须定义参数[cls]!"); //
			} else {
				WebDispatchIfc dispatch = (WebDispatchIfc) Class.forName(str_className).newInstance(); //
				dispatch.service(_request, _response, null); //直接执行!!!
			}
			initContext.commitAllTrans(); //提交所有事务
		} catch (Exception ex) {
			initContext.rollbackAllTrans(); //回滚所有事务..
			ex.printStackTrace(); //服务器输出!!
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ex.printStackTrace(new PrintWriter(bos, true)); //将异常打印到二进流中!!!
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
			initContext.closeAllConn(); //关闭所有连接!!
			initContext.release(); //释放Session资源!!!
		}
	}

}
