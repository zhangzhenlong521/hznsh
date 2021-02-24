/**************************************************************************
 * $RCSfile: SessionFactory.java,v $  $Revision: 1.10 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: SessionFactory.java,v $
 * Revision 1.10  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:48  Administrator
 * *** empty log message ***
 *
 * Revision 1.9  2011/10/10 06:31:35  wanggang
 * restore
 *
 * Revision 1.7  2010/12/28 10:28:50  xch123
 * 12月28日提交
 *
 * Revision 1.6  2010/11/18 11:14:12  xch123
 * *** empty log message ***
 *
 * Revision 1.5  2010/10/29 05:17:37  xch123
 * *** empty log message ***
 *
 * Revision 1.4  2010/06/07 12:03:21  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/05/30 10:16:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2010/05/30 06:31:07  xuchanghua
 * *** empty log message ***
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
 * Revision 1.2  2009/07/09 09:01:06  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:13  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/08/21 06:13:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/08/18 00:53:52  xuchanghua
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
 * Revision 1.1  2007/03/02 01:44:30  shxch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/01 09:06:56  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/01 06:44:42  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/01 06:22:49  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/01 06:10:25  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/02/28 09:17:54  shxch
 * *** empty log message ***
 *
 **************************************************************************/

package cn.com.infostrategy.bs.common;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import cn.com.infostrategy.to.common.CurrSessionVO;


/**
 * 会话工厂,非常重要,是一个单例模式,在这里存储了各个线程的数据库连接与客户端变量的信息!!
 * @author user
 *
 */
public class SessionFactory {

	private static SessionFactory factory = new SessionFactory();

	private static Hashtable dbConnectionMap = new Hashtable(); //指定数据源存储对象,
	private static Hashtable clientEnvMap = new Hashtable(); //存储客户端环境变量的HashMap,类似于Session的机制!!必须使用Hashtable,因为它是线程安全的!

	private SessionFactory() {
	}

	public static SessionFactory getInstance() {
		return factory;
	}

	/**
	 * 取得指定数据源，并参与同一事务
	 * @param thread
	 * @param _dsname
	 * @return
	 * @throws SQLException 
	 */
	public WLTDBConnection getConnection(Thread _thread, String _dsname) throws Exception {
		Object obj = dbConnectionMap.get(_thread); //指定数据源存储对象!!
		if (obj != null) { //如果找到了该线程的对象!!当前线程可以处理多个数据源!!!!!!
			HashMap map = (HashMap) obj;
			Object objConn = map.get(_dsname);
			if (objConn != null) {
				return (WLTDBConnection) objConn; //
			} else {
				WLTDBConnection conn = new WLTDBConnection(_dsname); //创建指定数据源!!!这里会不会阻塞???
				map.put(_dsname, conn); //
				return conn; //
			}
		} else {
			HashMap map = new HashMap();
			WLTDBConnection conn = new WLTDBConnection(_dsname); //创建指定数据源!!!这里会不会阻塞???
			map.put(_dsname, conn); //
			dbConnectionMap.put(_thread, map); //
			return conn; //
		}
	}

	/**
	 * @param _thread
	 * @param _dsName
	 * @return
	 */
	protected boolean isGetConnection(Thread _thread) {
		return dbConnectionMap.containsKey(_thread); //
	}

	protected WLTDBConnection[] GetAllConnections(Thread _thread) {
		Object obj = dbConnectionMap.get(_thread); //指定数据源存储对象!!
		if (obj == null) {
			return null;
		} else {
			Vector vector = new Vector();
			HashMap map = (HashMap) obj; // 
			return (WLTDBConnection[]) (new ArrayList(map.values()).toArray(new WLTDBConnection[0])); //
		}
	}

	/**
	 * 取得当前线程的客户端环境变量!!
	 * @param _thread
	 * @return
	 */
	public CurrSessionVO getClientEnv(Thread _thread) {
		Object obj = clientEnvMap.get(_thread); //指定数据源存储对象!!
		return (CurrSessionVO) obj; //
	}

	public Object getCustInfoByKey(Thread _thread, String _keyName) {
		CurrSessionVO sessionVO = SessionFactory.getInstance().getClientEnv(_thread); //
		if (sessionVO == null) {
			return null;
		}
		HashMap custMap = sessionVO.getCustMap(); //
		return custMap.get(_keyName); // 
	}

	/**
	 * 设置自定义信息!!
	 * @param _thread
	 * @param _keyName
	 * @param _items
	 */
	public void setCustInfoByKey(Thread _thread, String _keyName, String[] _items) {
		CurrSessionVO sessionVO = SessionFactory.getInstance().getClientEnv(_thread); //
		if (sessionVO == null) {
			return;
		}
		HashMap custMap = sessionVO.getCustMap(); //
		Object obj_value = custMap.get(_keyName); //
		if (obj_value == null) { //如果包含了该key
			ArrayList al_items = new ArrayList(); //
			al_items.add(_items); ////
			custMap.put(_keyName, al_items); //
		} else {
			ArrayList al_items = (ArrayList) obj_value; //
			al_items.add(_items); ////
			//custMap.put(_keyName, al_items); //要再重新置入么?感觉是按句柄传递,所以不需要重置!!!
		}
	}

	public void setCurrSessionCustStrInfoByKey(Thread _thread, String _keyName, String _value) {
		CurrSessionVO sessionVO = SessionFactory.getInstance().getClientEnv(_thread); //
		if (sessionVO == null) {
			return;
		}
		HashMap custMap = sessionVO.getCustMap(); //
		custMap.put(_keyName, _value); //
	}

	/**
	 * 在当前Session中增加某个key的值!
	 * @param _thread
	 * @param _keyName
	 * @param _value
	 */
	public void addCurrSessionCustStrInfoByKey(Thread _thread, String _keyName, String _value) {
		CurrSessionVO sessionVO = SessionFactory.getInstance().getClientEnv(_thread); //
		if (sessionVO == null) {
			return;
		}
		HashMap custMap = sessionVO.getCustMap(); //
		if (!custMap.containsKey(_keyName)) { //如果没有包含该Key,则送入该key!
			custMap.put(_keyName, _value); //
		} else { //否则取出原来的再加上新的!!!
			String oldValue = (String) custMap.get(_keyName); //
			custMap.put(_keyName, oldValue + _value); //
		}
	}

	//取得某个key值!!
	public String getCurrSessionCustStrInfoByKey(Thread _thread, String _keyName, boolean _afterGetAutoDel) {
		CurrSessionVO sessionVO = SessionFactory.getInstance().getClientEnv(_thread); //
		if (sessionVO == null) {
			return null;
		}
		HashMap custMap = sessionVO.getCustMap(); //
		String str_value = (String) custMap.get(_keyName); //
		if (_afterGetAutoDel) {
			custMap.remove(_keyName); //
		}
		return str_value; //
	}

	/**
	 * 注册客户端环境变量,以前这个方法是做成同步的(synchronized),结果在招行项目中进行压力测试时性能很慢!!!一度搞得非常痛苦!!
	 * 然后经过反复仔细跟踪后,发现砂来是这个方法做成同步造成的! 这也是我以前一直怀疑与担心的地方! 
	 * 当去掉同步锁后,就表现为数据库的CPU是100%环跑,即中间件的吞吐量非常高!! 将所有压力都给了数据库!! 这样只要生产环境的数据库够高! 就能保证性能了!
	 * @param _thread
	 */
	public void regisClientEnv(Thread _thread, CurrSessionVO _currSessionVO) {
		if (_currSessionVO == null) {
			CurrSessionVO newVO = new CurrSessionVO(); //
			newVO.setRegisteTime(System.currentTimeMillis()); //
			clientEnvMap.put(_thread, newVO); //注册当前线程的客户端环境变量!
		} else {
			_currSessionVO.setRegisteTime(System.currentTimeMillis()); //注册时间
			clientEnvMap.put(_thread, _currSessionVO); //注册当前线程的客户端环境变量!
		}
	}

	/**
	 * 得到所有线程
	 * @return
	 */
	public Thread[] getAllThreads() {
		Thread[] threads = (Thread[]) clientEnvMap.keySet().toArray(new Thread[0]); //
		return threads;
	}

	public void releaseCustConnection(Thread _thread) {
		dbConnectionMap.remove(_thread); //
	}

	public void releaseClientEnv(Thread _thread) {
		clientEnvMap.remove(_thread); //
	}

}
