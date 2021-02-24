package cn.com.infostrategy.bs.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 全文检索的Servlet,在兴业项目中遇到4台中间件做集群,数据全部放在网络驱动器X:上,这时全文检索其实就是将1万5千多个制度内容从另一台机器上读到这台机器上,然后再检索!
 * 这样一是性能非常慢,二是容易耗尽中间件的内存!!
 * @author xch
 *
 */
public class FullTextSearchServlet extends HttpServlet {

	private static final long serialVersionUID = -5879770831393708648L;

	@Override
	public void service(ServletRequest _request, ServletResponse _response) throws ServletException, IOException {
		TBUtil tbUtil = new TBUtil(); //
		byte[] requestBytes = tbUtil.readFromInputStreamToBytes(_request.getInputStream()); //
		HashMap requestMap = (HashMap) tbUtil.deserialize(requestBytes); //入参肯定是个HashMap
		String str_serverdir = ServerEnvironment.getProperty("FullTextSearchRootDir"); //全文化检索的根目录!!
		if (str_serverdir == null || str_serverdir.trim().equals("")) {
			str_serverdir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //如果全文检索的根目录没定义则直接使用WLTUPLOADFILEDIR
		}
		String str_subdir = (String) requestMap.get("subdir"); //子目录!!
		if (str_subdir != null) {
			str_serverdir = str_serverdir + str_subdir; //
		}
		if (!str_serverdir.endsWith("/")) {
			str_serverdir = str_serverdir + "/"; //如果不是以
		}
		System.out.println("全文检索的根目录=[" + str_serverdir + "]"); //
		String[][] str_fileInfos = (String[][]) requestMap.get("FileInfo"); //
		String[] str_keywords = (String[]) requestMap.get("KeyWords"); //
		boolean isAllContain = (Boolean) requestMap.get("isAllContain"); //
		HashMap responseMap = new HashMap(); ////

		long ll_jvm_total = Runtime.getRuntime().totalMemory() / (1024 * 1024); //
		long ll_jvm_busy = ll_jvm_total - (Runtime.getRuntime().freeMemory() / (1024 * 1024)); //
		if (ll_jvm_busy > 900) { //防止内存溢出!上限900M
			responseMap.put("ReturnFileIds", new Exception("全文查询是个非常消耗资源的操作,系统做了限制只能同时有部分人员做此操作!\r\n现在做全文查询的人太多,请稍候再做全文查询!\r\n或去掉全文查询条件暂时只使用普通条件查询!")); //返回的文件ID
			System.gc(); //
		} else {
			ArrayList al_return = (ArrayList) new BSUtil().checkWordOrExcelContainKeys(str_serverdir, str_fileInfos, str_keywords, isAllContain); //本地找!!!!
			responseMap.put("ReturnFileIds", al_return); //返回的文件ID
			System.gc(); //
		}
		byte[] responseBytes = tbUtil.serialize(responseMap); //序列化
		tbUtil.writeBytesToOutputStream(_response.getOutputStream(), responseBytes); //输出!!!
	}
}
