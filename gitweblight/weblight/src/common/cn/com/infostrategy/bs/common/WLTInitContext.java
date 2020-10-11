/**************************************************************************
 * $RCSfile: WLTInitContext.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: WLTInitContext.java,v $
 * Revision 1.8  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:48  Administrator
 * *** empty log message ***
 *
 * Revision 1.7  2011/10/10 06:31:34  wanggang
 * restore
 *
 * Revision 1.5  2011/03/04 11:51:04  xch123
 * *** empty log message ***
 *
 * Revision 1.4  2010/12/28 10:28:50  xch123
 * 12月28日提交
 *
 * Revision 1.3  2010/10/29 05:17:37  xch123
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
 * Revision 1.1  2009/02/20 06:10:13  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/08/27 03:06:59  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/08/18 00:53:52  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/28 06:17:35  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/06/04 02:46:57  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:07  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:12  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/10/23 06:30:42  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:05  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:32  xch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/02 01:44:30  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/01 06:44:42  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/01 06:22:49  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/01 06:10:26  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/02/28 09:17:54  shxch
 * *** empty log message ***
 *
 **************************************************************************/

package cn.com.infostrategy.bs.common;

import java.sql.SQLException;

import cn.com.infostrategy.to.common.CurrSessionVO;

/**
 * Nova环境上下文可以处理事务等...
 * @author user
 *
 */
public class WLTInitContext {

	public WLTInitContext() {
	}

	/**
	 * 取得指定数据源,并参与同一事务!
	 * @param _dsname
	 * @return
	 * @throws SQLException
	 */
	public WLTDBConnection getConn(String _dsname) throws Exception {
		if (_dsname == null) {
			_dsname = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}

		return SessionFactory.getInstance().getConnection(Thread.currentThread(), _dsname); //取得当前线程的连接!!
	}

	/**
	 * 得到客户端环境变量
	 * @return
	 */
	public CurrSessionVO getCurrSession() {
		return SessionFactory.getInstance().getClientEnv(Thread.currentThread()); //
	}

	protected boolean isGetConn() {
		return SessionFactory.getInstance().isGetConnection(Thread.currentThread());
	}

	protected WLTDBConnection[] GetAllConns() {
		return SessionFactory.getInstance().GetAllConnections(Thread.currentThread());
	}

	/**
	 * 注册整个客户端环境!!
	 * @param _clientEnv
	 */
	protected void regisCurrSession(CurrSessionVO _currSessionVO) {
		SessionFactory.getInstance().regisClientEnv(Thread.currentThread(), _currSessionVO);
	}

	/**
	 * 提交所有事务
	 * @param _initContext
	 */
	public int commitAllTrans() {
		int li_allStmtCount = 0;
		if (isGetConn()) {
			WLTDBConnection[] conns = GetAllConns();
			for (int i = 0; i < conns.length; i++) {
				try {
					li_allStmtCount = li_allStmtCount + conns[i].getOpenStmtCount(); //
					conns[i].transCommit();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return li_allStmtCount;
	}

	/**
	 * 回滚当前会话的所有连接
	 * @return
	 */
	public int rollbackAllTrans() {
		int li_allStmtCount = 0;
		if (isGetConn()) {
			WLTDBConnection[] conns = GetAllConns();
			for (int i = 0; i < conns.length; i++) {
				try {
					li_allStmtCount = li_allStmtCount + conns[i].getOpenStmtCount(); //
					conns[i].transRollback();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return li_allStmtCount;
	}

	/**
	 * 取得当前线程中的某个关键字的值!!!
	 * @param _keyName
	 * @return
	 */
	public Object getCurrSessionCustInfoByKey(String _keyName) {
		return SessionFactory.getInstance().getCustInfoByKey(Thread.currentThread(), _keyName); //
	}

	/**
	 * 设置当前线程的客户信息!!
	 * @param _keyName
	 * @param _items
	 */
	public void setCurrSessionCustInfoByKey(String _keyName, String[] _items) {
		SessionFactory.getInstance().setCustInfoByKey(Thread.currentThread(), _keyName, _items); //
	}

	public void setCurrSessionCustStrInfoByKey(String _keyName, String _value) {
		SessionFactory.getInstance().setCurrSessionCustStrInfoByKey(Thread.currentThread(), _keyName, _value); //
	}

	public void addCurrSessionForClientTrackMsg(String _value) {
		addCurrSessionCustStrInfoByKey("ForClientTackMsg", _value); //
	}

	public void addCurrSessionCustStrInfoByKey(String _keyName, String _value) {
		SessionFactory.getInstance().addCurrSessionCustStrInfoByKey(Thread.currentThread(), _keyName, _value); //
	}

	public String getCurrSessionCustStrInfoByKey(String _keyName) {
		return getCurrSessionCustStrInfoByKey(_keyName, false); //
	}

	public String getCurrSessionCustStrInfoByKey(String _keyName, boolean _afterGetAutoDel) {
		return SessionFactory.getInstance().getCurrSessionCustStrInfoByKey(Thread.currentThread(), _keyName, _afterGetAutoDel); //
	}

	/**
	 * 关闭所有数据库连接....
	 */
	public void closeAllConn() {
		WLTDBConnection[] allconn = GetAllConns(); //
		if (allconn != null && allconn.length > 0) {
			for (int i = 0; i < allconn.length; i++) {
				try {
					allconn[i].close(); //
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
			}
		}

		//再从缓存中删除记录..
		try {
			SessionFactory.getInstance().releaseCustConnection(Thread.currentThread()); //
		} catch (Throwable th) {
			th.printStackTrace(); //
		}
	}

	public void release() {
		try {
			SessionFactory.getInstance().releaseCustConnection(Thread.currentThread()); //
		} catch (Throwable th) {
		}

		try {
			SessionFactory.getInstance().releaseClientEnv(Thread.currentThread()); //
		} catch (Throwable th) {
		}
	}

}
