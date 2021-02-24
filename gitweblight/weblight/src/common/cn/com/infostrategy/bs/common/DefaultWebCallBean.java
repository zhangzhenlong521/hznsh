package cn.com.infostrategy.bs.common;

import java.util.HashMap;

/**
 * 直接从客户端传过来的Html,直接输出!!
 * 有时Html是在客户端计算出来的,比如卡片导出Html,然后借用服务器转一下就行了!!
 * @author xch
 *
 */
public class DefaultWebCallBean implements WebCallBeanIfc {

	public String getHtmlContent(HashMap map) throws Exception {
		return (String) map.get("html");
	}

}
