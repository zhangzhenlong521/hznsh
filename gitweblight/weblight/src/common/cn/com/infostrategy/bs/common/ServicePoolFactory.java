/**************************************************************************
 * $RCSfile: ServicePoolFactory.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.bs.common;

import java.util.Hashtable;

import org.apache.commons.pool.impl.GenericObjectPool;

import cn.com.infostrategy.to.common.VectorMap;

public class ServicePoolFactory {

	private static ServicePoolFactory factory = null;

	private static VectorMap ht_pool = null;

	private static Hashtable ht_implclass = null;

	private ServicePoolFactory() {
		ht_pool = new VectorMap(); //
		ht_implclass = new Hashtable();
	}

	public static ServicePoolFactory getInstance() {
		if (factory != null) {
			return factory;
		}

		factory = new ServicePoolFactory(); //
		return factory;
	}

	public GenericObjectPool getPool(String _servicename) {
		return (GenericObjectPool) ht_pool.get(_servicename); //
	}

	public void registPool(String _servicename, String _implClassName, GenericObjectPool _pool) {
		ht_pool.put(_servicename, _pool);
		ht_implclass.put(_servicename, _implClassName); //
	}

	public String getImplClassName(String _servicename) {
		return (String) ht_implclass.get(_servicename); //
	}

	public String[] getAllServiceNames() {
		return ht_pool.getKeysAsString(); //
	}
}

/**************************************************************************
 * $RCSfile: ServicePoolFactory.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: ServicePoolFactory.java,v $
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
 * Revision 1.3  2007/09/20 05:08:31  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/02/10 06:24:01  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/01/31 07:19:33  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 03:36:24  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
