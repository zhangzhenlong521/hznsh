package cn.com.infostrategy.bs.common;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ƽ̨Servlet������ת������Ľӿ�,ʹ������ĺô��ǲ���Ҫ�������ݿ����ӳص�������!
 * ��������Word�ȸ�ʽ,��Ҫʹ�����·�ʽ,���и�ʽ����BSUTil.getMimeTypeByDocType
 * _response.setContentType("application/msword");  //
 * _response.setHeader("Content-Disposition", "application/msword; filename=cmpfile.doc");  //
 * _response.setContentLength(unZipedBytes.length);  //
 * _response
 * @author xch
 *
 */
public interface WebDispatchIfc {

	/**
	 * ��DisPatchServlet�е����������!
	 * �����ʵ�������ʵ�ָýӿ�,����ɸ÷���!!!
	 * @param _request
	 * @param _response
	 * @throws Exception
	 */
	public void service(HttpServletRequest _request, HttpServletResponse _response, HashMap _parMap) throws Exception;

}
