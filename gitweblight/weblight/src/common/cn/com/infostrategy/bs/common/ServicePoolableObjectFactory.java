/**************************************************************************
 * $RCSfile: ServicePoolableObjectFactory.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.bs.common;


import org.apache.commons.pool.PoolableObjectFactory;

import cn.com.infostrategy.to.common.WLTLogger;

public class ServicePoolableObjectFactory implements PoolableObjectFactory {

	private String str_servicename = null; //服务标识名!!

	private String str_serviceimplclass = null; //服务实现类名!!

	public ServicePoolableObjectFactory(String _servicename, String _serviceimplclass) {
		this.str_servicename = _servicename;
		this.str_serviceimplclass = _serviceimplclass;
	}

	public Object makeObject() throws Exception {
		Object obj = Class.forName(str_serviceimplclass).newInstance();
		WLTLogger.getLogger(this).debug("创建SOA服务实例[" + str_serviceimplclass + "]");
		return obj;
	}

	public void activateObject(Object arg0) throws Exception {
		//System.out.println("池中激活实例[" + str_serviceimplclass + "]!!");
	}

	public void passivateObject(Object arg0) throws Exception {
		//System.out.println("池中钝化实例[" + str_serviceimplclass + "]!!");
	}

	public void destroyObject(Object arg0) throws Exception {
		//System.out.println("池中消毁实例[" + str_serviceimplclass + "]!!");
	}

	public boolean validateObject(Object arg0) {
		return true; //永远校验成功,暂时不做校验的功能!!按道理应该校验实现类必须实现服务名接口!!
	}

}
/**************************************************************************
 * $RCSfile: ServicePoolableObjectFactory.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: ServicePoolableObjectFactory.java,v $
 * Revision 1.4  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:48  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:35  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:22:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:47  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:01  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:40  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:13  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/28 06:17:35  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:07  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:12  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:05  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:32  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/27 03:36:32  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/02/10 07:28:30  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/01/31 09:53:10  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 03:37:33  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
