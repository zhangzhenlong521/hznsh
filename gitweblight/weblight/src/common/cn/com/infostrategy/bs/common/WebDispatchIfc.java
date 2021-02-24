package cn.com.infostrategy.bs.common;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 平台Servlet分派器转发的类的接口,使用这个的好处是不需要处理数据库连接池的问题了!
 * 如果是输出Word等格式,则要使用以下方式,所有格式参照BSUTil.getMimeTypeByDocType
 * _response.setContentType("application/msword");  //
 * _response.setHeader("Content-Disposition", "application/msword; filename=cmpfile.doc");  //
 * _response.setContentLength(unZipedBytes.length);  //
 * _response
 * @author xch
 *
 */
public interface WebDispatchIfc {

	/**
	 * 从DisPatchServlet中调用这个方法!
	 * 具体的实现类必须实现该接口,并完成该方法!!!
	 * @param _request
	 * @param _response
	 * @throws Exception
	 */
	public void service(HttpServletRequest _request, HttpServletResponse _response, HashMap _parMap) throws Exception;

}
