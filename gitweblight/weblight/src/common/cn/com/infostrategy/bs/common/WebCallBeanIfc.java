package cn.com.infostrategy.bs.common;

import java.util.HashMap;

public interface WebCallBeanIfc {

	/**
	 * ���ݲ���ȡ��HTML����,ʵ�ʵ�������ӿڵ�ʵ�����ǡ�cn.com.infostrategy.bs.common.WebCallServlet��������
	 * ֮���Ը������Ļ�����Ϊ��ʡȥÿ����һ��Servlet��������web.xml������һ������(�����벿�����)!!ͬʱ����ʵ��һ����������,�������ε���
	 * ��Ҫ�ر�ָ������,�����ָ����mimetype��,���ص��ַ���������64λ��!!!!��Ϊ���������������ݿ��еĶ���64λ��!!!
	 * дһ�������С�cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean��,�ǵ��÷����ǡ�http://...../WebCallServlet?StrParCallClassName=cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean��
	 * ��UIUtil���и�������openRemoteServerHtml("cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean",HashMap),�����ڿͻ��˵��������url
	 * @param _parMap
	 * @return ����һ��HTML,�������
	 * @throws Exception
	 */
	public String getHtmlContent(HashMap _parMap) throws Exception; //
}
