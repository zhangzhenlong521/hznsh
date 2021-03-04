/**************************************************************************
 * $RCSfile: RemoteServiceFactory.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.lang.reflect.Proxy;

import cn.com.infostrategy.to.common.WLTRemoteException;

public class RemoteServiceFactory {

	private static RemoteServiceFactory factory = null;

	private RemoteServiceFactory() {

	}

	public static RemoteServiceFactory getInstance() {
		if (factory == null) {
			factory = new RemoteServiceFactory();

		}
		return factory;
	}

	/**
	 * 返回一个实例!!
	 * @param _class
	 * @param _implname
	 * @return
	 * @throws Exception 
	 */
	public synchronized Object lookUpService(Class _class) throws WLTRemoteException, Exception {
		return lookUpService(_class, 0);
	}
	/**
	 * 
	 * @param _class
	 * @param _readTimeOut  超过多少秒就强行中断!!!
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public synchronized Object lookUpService(Class _class, int _readTimeOut) throws WLTRemoteException, Exception {
		return lookUpService(_class, null, _readTimeOut);
	}

	public synchronized Object lookUpService(Class _class, String _otherIPAndPort) throws WLTRemoteException, Exception {
		return lookUpService(_class, _otherIPAndPort, 0);
	}
	public synchronized Object lookUpService(Class<?> _class, String implClassName,String CRCB) throws WLTRemoteException, Exception {
		return lookUpService(_class, 0, implClassName);
	}
	public synchronized Object lookUpService(Class<?> _class, int _readTimeOut, String implClassName) throws WLTRemoteException, Exception {
		return lookUpService(_class, null, _readTimeOut, implClassName);
	}

	

	/**
	 * 
	 * @param _class
	 * @param _otherIPAndPort 可以在直接强行调另一个机器的IP与端口
	 * @param _readTimeOut
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public synchronized Object lookUpService(Class _class, String _otherIPAndPort, int _readTimeOut) throws WLTRemoteException, Exception {
		String str_serviceName = _class.getName();
		Object serviceobj = Proxy.newProxyInstance(_class.getClassLoader(), new Class[] { _class }, new RemoteCallHandler(str_serviceName, _otherIPAndPort, _readTimeOut));
		if (serviceobj instanceof WLTRemoteCallServiceIfc) { //是否Nova服务
			return serviceobj;
		} else {
			throw new Exception(str_serviceName + "不是INova2RemoteCallService的子类!!");
		}
	}
	
	public synchronized Object lookUpService(Class<?> _class, String _otherIPAndPort, int _readTimeOut, String implClassName) throws WLTRemoteException, Exception {
		String str_serviceName = _class.getName();
		Object serviceobj = Proxy.newProxyInstance(_class.getClassLoader(), new Class[] { _class }, new RemoteCallHandler(str_serviceName, _otherIPAndPort, _readTimeOut, implClassName));
		if (serviceobj instanceof WLTRemoteCallServiceIfc) { //是否Nova服务
			return serviceobj;
		} else {
			throw new Exception(str_serviceName + "不是INova2RemoteCallService的子类!!");
		}
	}

}

/**************************************************************************
 * $RCSfile: RemoteServiceFactory.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: RemoteServiceFactory.java,v $
 * Revision 1.4  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:50  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:36  wanggang
 * restore
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
 * Revision 1.1  2009/02/20 06:10:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/12/26 03:21:06  xuchanghua
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
 * Revision 1.2  2007/03/21 05:52:19  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/01/31 10:03:25  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/01/31 09:37:50  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 03:41:28  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
