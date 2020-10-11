package cn.com.infostrategy.bs.common;

import java.util.HashMap;

public interface WebCallBeanIfc {

	/**
	 * 根据参数取得HTML内容,实际调用这个接口的实现类是【cn.com.infostrategy.bs.common.WebCallServlet】！！！
	 * 之所以搞出这个的机制是为了省去每增加一个Servlet都必须在web.xml中增加一个配置(升级与部署费事)!!同时可以实现一个其他功能,比如两次调用
	 * 需要特别指出的是,如果是指定了mimetype的,返回的字符串必须是64位码!!!!因为大多数情况存在数据库中的都是64位码!!!
	 * 写一个类比如叫【cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean】,那调用方法是【http://...../WebCallServlet?StrParCallClassName=cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean】
	 * 在UIUtil中有个方法叫openRemoteServerHtml("cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean",HashMap),等于在客户端调用上面的url
	 * @param _parMap
	 * @return 返回一段HTML,比如表格等
	 * @throws Exception
	 */
	public String getHtmlContent(HashMap _parMap) throws Exception; //
}
