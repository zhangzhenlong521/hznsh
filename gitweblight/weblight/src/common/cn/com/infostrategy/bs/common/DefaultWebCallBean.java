package cn.com.infostrategy.bs.common;

import java.util.HashMap;

/**
 * ֱ�Ӵӿͻ��˴�������Html,ֱ�����!!
 * ��ʱHtml���ڿͻ��˼��������,���翨Ƭ����Html,Ȼ����÷�����תһ�¾�����!!
 * @author xch
 *
 */
public class DefaultWebCallBean implements WebCallBeanIfc {

	public String getHtmlContent(HashMap map) throws Exception {
		return (String) map.get("html");
	}

}
