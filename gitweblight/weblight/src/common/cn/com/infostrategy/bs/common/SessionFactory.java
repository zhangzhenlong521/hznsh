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
 * 12��28���ύ
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
 * �Ự����,�ǳ���Ҫ,��һ������ģʽ,������洢�˸����̵߳����ݿ�������ͻ��˱�������Ϣ!!
 * @author user
 *
 */
public class SessionFactory {

	private static SessionFactory factory = new SessionFactory();

	private static Hashtable dbConnectionMap = new Hashtable(); //ָ������Դ�洢����,
	private static Hashtable clientEnvMap = new Hashtable(); //�洢�ͻ��˻���������HashMap,������Session�Ļ���!!����ʹ��Hashtable,��Ϊ�����̰߳�ȫ��!

	private SessionFactory() {
	}

	public static SessionFactory getInstance() {
		return factory;
	}

	/**
	 * ȡ��ָ������Դ��������ͬһ����
	 * @param thread
	 * @param _dsname
	 * @return
	 * @throws SQLException 
	 */
	public WLTDBConnection getConnection(Thread _thread, String _dsname) throws Exception {
		Object obj = dbConnectionMap.get(_thread); //ָ������Դ�洢����!!
		if (obj != null) { //����ҵ��˸��̵߳Ķ���!!��ǰ�߳̿��Դ���������Դ!!!!!!
			HashMap map = (HashMap) obj;
			Object objConn = map.get(_dsname);
			if (objConn != null) {
				return (WLTDBConnection) objConn; //
			} else {
				WLTDBConnection conn = new WLTDBConnection(_dsname); //����ָ������Դ!!!����᲻������???
				map.put(_dsname, conn); //
				return conn; //
			}
		} else {
			HashMap map = new HashMap();
			WLTDBConnection conn = new WLTDBConnection(_dsname); //����ָ������Դ!!!����᲻������???
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
		Object obj = dbConnectionMap.get(_thread); //ָ������Դ�洢����!!
		if (obj == null) {
			return null;
		} else {
			Vector vector = new Vector();
			HashMap map = (HashMap) obj; // 
			return (WLTDBConnection[]) (new ArrayList(map.values()).toArray(new WLTDBConnection[0])); //
		}
	}

	/**
	 * ȡ�õ�ǰ�̵߳Ŀͻ��˻�������!!
	 * @param _thread
	 * @return
	 */
	public CurrSessionVO getClientEnv(Thread _thread) {
		Object obj = clientEnvMap.get(_thread); //ָ������Դ�洢����!!
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
	 * �����Զ�����Ϣ!!
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
		if (obj_value == null) { //��������˸�key
			ArrayList al_items = new ArrayList(); //
			al_items.add(_items); ////
			custMap.put(_keyName, al_items); //
		} else {
			ArrayList al_items = (ArrayList) obj_value; //
			al_items.add(_items); ////
			//custMap.put(_keyName, al_items); //Ҫ����������ô?�о��ǰ��������,���Բ���Ҫ����!!!
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
	 * �ڵ�ǰSession������ĳ��key��ֵ!
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
		if (!custMap.containsKey(_keyName)) { //���û�а�����Key,�������key!
			custMap.put(_keyName, _value); //
		} else { //����ȡ��ԭ�����ټ����µ�!!!
			String oldValue = (String) custMap.get(_keyName); //
			custMap.put(_keyName, oldValue + _value); //
		}
	}

	//ȡ��ĳ��keyֵ!!
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
	 * ע��ͻ��˻�������,��ǰ�������������ͬ����(synchronized),�����������Ŀ�н���ѹ������ʱ���ܺ���!!!һ�ȸ�÷ǳ�ʹ��!!
	 * Ȼ�󾭹�������ϸ���ٺ�,����ɰ���������������ͬ����ɵ�! ��Ҳ������ǰһֱ�����뵣�ĵĵط�! 
	 * ��ȥ��ͬ������,�ͱ���Ϊ���ݿ��CPU��100%����,���м�����������ǳ���!! ������ѹ�����������ݿ�!! ����ֻҪ�������������ݿ⹻��! ���ܱ�֤������!
	 * @param _thread
	 */
	public void regisClientEnv(Thread _thread, CurrSessionVO _currSessionVO) {
		if (_currSessionVO == null) {
			CurrSessionVO newVO = new CurrSessionVO(); //
			newVO.setRegisteTime(System.currentTimeMillis()); //
			clientEnvMap.put(_thread, newVO); //ע�ᵱǰ�̵߳Ŀͻ��˻�������!
		} else {
			_currSessionVO.setRegisteTime(System.currentTimeMillis()); //ע��ʱ��
			clientEnvMap.put(_thread, _currSessionVO); //ע�ᵱǰ�̵߳Ŀͻ��˻�������!
		}
	}

	/**
	 * �õ������߳�
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
