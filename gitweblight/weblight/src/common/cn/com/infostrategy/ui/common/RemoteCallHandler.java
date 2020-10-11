/**************************************************************************
 * $RCSfile: RemoteCallHandler.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import cn.com.infostrategy.to.common.WLTRemoteException;

public class RemoteCallHandler implements InvocationHandler {

	private String serviceName = null;
	private String otherIPAndPort = null; //����������IP��˿�,�Ǹ�ʽ[192.168.0.10:9015]������!!!
	private int readTimeOutSecond = 0; //��ȡ��ʱ����..
	private String implClassName;
	public RemoteCallHandler(String _serviceName, String _otherIPAndPort, int _readTimeOutSecond) {
		this.serviceName = _serviceName; //Զ�̷�����
		this.otherIPAndPort = _otherIPAndPort; //���������IP��˿�
		this.readTimeOutSecond = _readTimeOutSecond; //
	}
	public RemoteCallHandler(String _serviceName, String _otherIPAndPort, int _readTimeOutSecond, String implClassName) {
		this.serviceName = _serviceName; //Զ�̷�����
		this.otherIPAndPort = _otherIPAndPort; //���������IP��˿�
		this.readTimeOutSecond = _readTimeOutSecond; //
		this.implClassName = implClassName;
	}
	public Object invoke(Object proxy, Method method, Object[] args) throws WLTRemoteException, Throwable {
		Class[] pars_class = method.getParameterTypes(); //
		RemoteCallClient rcc = null; //
		if (otherIPAndPort == null) { //���û��ָ���µĶ˿����ַ..
			rcc = new RemoteCallClient();
		} else {
			String str_url = "http://" + otherIPAndPort + System.getProperty("APP_CONTEXT") + "/RemoteCallServlet"; //http://127.0.0.1:9001/pushgrc/RemoteCallServlet
			rcc = new RemoteCallClient(str_url); //��ָ����URL!! �ڼ�Ⱥʱ��Ҫ!! �����ϴ�����������Զ����������!!!
		}
		Object return_obj = rcc.callServlet(serviceName, method.getName(), pars_class, args, readTimeOutSecond); //�õ�����!!
		if (return_obj instanceof WLTRemoteCallServiceIfc) {
			throw new Exception("����[" + serviceName + "]û��ʵ��WLRemoteCallServiceIfc�ӿ�,���ܽ���Զ�̷���!");
		}
		return return_obj; //
	}
	
}

/**************************************************************************
 * $RCSfile: RemoteCallHandler.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: RemoteCallHandler.java,v $
 * Revision 1.5  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:50  Administrator
 * *** empty log message ***
 *
 * Revision 1.4  2011/10/10 06:31:35  wanggang
 * restore
 *
 * Revision 1.2  2010/12/28 10:29:11  xch123
 * 12��28���ύ
 *
 * Revision 1.1  2010/05/17 10:23:02  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:52  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2010/03/02 13:00:23  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:57  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:43  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/05/13 07:51:07  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/12/26 03:21:12  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/28 06:17:40  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:10  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:16  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:14  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:29  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/21 05:52:19  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/02/10 06:20:48  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/01/31 10:03:25  shxch
 *
 **************************************************************************/
