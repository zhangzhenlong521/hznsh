/**************************************************************************
 * $RCSfile: WLTDBConnection.java,v $  $Revision: 1.15 $  $Date: 2012/09/14 09:17:30 $
 *
 **************************************************************************/

package cn.com.infostrategy.bs.common;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;

import cn.com.infostrategy.to.common.DataSourceVO;

/**
 * 数据库连接类,就是将JDBC的数据库连接包装了一层!!
 *
 * MySQL的配法
 *<dbtype>MYSQL</dbtype>
 *<dbversion>5</dbversion>
 *<driver>com.mysql.jdbc.Driver</driver>
 *<dburl>jdbc:mysql://192.168.0.10:3000/ipushgrc?characterEncoding=GBK</dburl>
 *<user>root</user>
 *<pwd>111111</pwd>
 *<initsize>1</initsize>
 *<poolsize>10000</poolsize>
 *
 *Oracle的配法
 *<dbtype>ORACLE</dbtype>
 *<dbversion>10g</dbversion>
 *<driver>oracle.jdbc.OracleDriver</driver>  <!-- 以前是多一个driver,后来查资料说建议用这个 -->
 *<dburl>jdbc:oracle:thin:@192.168.0.12:1521:buick</dburl>
 *<user>cib</user>
 *<pwd>cib</pwd>
 *
 *SQLServer的配法
 *<dbtype>SQLSERVER</dbtype>
 *<dbversion>2005</dbversion>
 *<driver>com.microsoft.sqlserver.jdbc.SQLServerDriver</driver>
 *<dburl>jdbc:sqlserver://192.168.0.117:1433;DataBaseName=cmb</dburl>
 *<user>sa</user>
 *<pwd>sa</pwd>
 *
 *DB2的配法
 *<dbtype>DB2</dbtype>
 *<dbversion>9.5</dbversion>
 *<driver>com.ibm.db2.jcc.DB2Driver</driver>
 *<dburl>jdbc:db2://192.168.0.15:50000/databasename:currentSchema=PUSH_GRC;</dburl>
 *<user>db2admin</user>
 *<pwd>111111</pwd>
 *
 */
public class WLTDBConnection {

	private String dsName = null; //数据源名称!!

	private java.sql.Connection conn = null;

	private int openStmtCount = 0;

	public static int MAX_ACTIVES = 0; //
	public static int MAX_INSTANCES = 0; //

	/**
	 * 默认数据源
	 * @throws SQLException
	 */
	public WLTDBConnection() throws Exception {
		this.dsName = ServerEnvironment.getInstance().getDefaultDataSourceName();
		createConn();
	}

	/**
	 * 指定数据源
	 * @param _dsname
	 * @throws SQLException
	 */
	public WLTDBConnection(String _dsname) throws Exception {
		this.dsName = _dsname;
		createConn();
	}

	private void createConn() throws Exception {
		DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(this.dsName); //得到数据源定义的VO
		if (dsVO.getProvider_url() != null && dsVO.getInitial_context_factory() != null) { //如果有提供者url,即可能从中间件提供的数据源中取!!(中外运项目中客户科技部死活认为必须使用WebLogic中的数据源)
			Hashtable ht = new Hashtable();
			ht.put(Context.INITIAL_CONTEXT_FACTORY, dsVO.getInitial_context_factory());
			ht.put(Context.PROVIDER_URL, dsVO.getProvider_url());
			InitialContext _context = new InitialContext(ht);
			javax.sql.DataSource ds = (javax.sql.DataSource) _context.lookup(dsName);
			conn = ds.getConnection();
			setTransLevel(conn); //这一行可能会抛异常!!
		} else {
			if (dsVO.getInitsize() < 0) { //如果初始化大小是小于0,则每次都直接创建,即不用池技术..
				Properties myProperties = new Properties();
				myProperties.setProperty("user", dsVO.getUser());
				myProperties.setProperty("password", dsVO.getPwd());
				Class.forName(dsVO.getDriver()).newInstance(); //
				conn = DriverManager.getConnection(dsVO.getDburl(), myProperties); //创建连接
				setTransLevel(conn); //这一行可能会抛异常!!
			} else { //否则从dbcp池中取...
				PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:"); //先取得池驱动!!!
				GenericObjectPool pool = (GenericObjectPool) driver.getConnectionPool(this.dsName); //创建一个连接池!!!
				try {
					//conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:" + this.dsName); ////真正创建连接,其实也是从dbcp池中取的!!!这是以前的取法,后为为了能随时记录下系统曾经最大的连接数,改成了从池中取的方法!!
					int li_currBusy = pool.getNumActive(); //活动数!!!
					int li_currFree = pool.getNumIdle(); //空闲数!!!
					int li_currInstances = li_currBusy + li_currFree; //所有实例数
					dealMaxActives(li_currBusy, li_currInstances); //监控记下曾经发生过的最在忙碌数与最大实例数!!!!
					conn = (java.sql.Connection) pool.borrowObject(); //取个对象!!!!
					setTransLevel(conn); //这一行可能会抛异常!!
				} catch (Exception ex) {
					ex.printStackTrace(); //
					System.err.println("发生连接异常,如果是连接被重置的原因,则清空连接池中所有缓存,重新连接..."); //
					pool.clear(); //先清空连接池中所有对象！！！
					conn = (java.sql.Connection) pool.borrowObject(); //重新创建连接,如果不是连接重置造成的异常,则这里完全还可能产生异常!!!
					setTransLevel(conn); //
				}
			}
		}

		//System.out.println("当前线程[" + Thread.currentThread() + "]获取数据库连接[" + this.dsName + "]"); //看一次远程访问总共取了几次!!
	}

	//设置事务隔离级别
	private void setTransLevel(java.sql.Connection _conn) throws Exception {
		if ("Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISTRANSLEVELDOWN"))) { //如果需要降低事务隔离级别!
			_conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_UNCOMMITTED); //
		} else {
			_conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED); //TRANSACTION_READ_COMMITTED,TRANSACTION_REPEATABLE_READ,设置事务隔离级别!!设置死了!!
			_conn.setAutoCommit(false); //人工提交!!
		}
	}

	//方法同步!!
	private void dealMaxActives(int _currActives, int _currIns) {
		if (_currActives > MAX_ACTIVES) {
			MAX_ACTIVES = _currActives;
		}
		if (_currIns > MAX_INSTANCES) {
			MAX_INSTANCES = _currIns;
		}
	}

	protected void transCommit() throws SQLException {
		conn.commit();
	}

	protected void transRollback() throws SQLException {
		conn.rollback();
	}

	protected void close() throws SQLException {
		conn.close();
	}

	public java.sql.Connection getConn() {
		return conn;
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return conn.getMetaData();
	}

	/**
	 * 创建标准Statement
	 * @return
	 * @throws SQLException
	 */
	public java.sql.Statement createStatement() throws SQLException {
		openStmtCount = openStmtCount + 1;
		return conn.createStatement();
	}

	/**
	 * 创建PrepareStatement
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
		openStmtCount = openStmtCount + 1;
		return conn.prepareStatement(sql);
	}

	/**
	 * 创建CallableStatement,为存储过程与存储函数用!!!
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public java.sql.CallableStatement prepareCall(String sql) throws SQLException {
		openStmtCount = openStmtCount + 1;
		return conn.prepareCall(sql); //
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	//打开游标的数量!!
	public int getOpenStmtCount() {
		return openStmtCount;
	}

	public Connection getJDBCConnection() {
		return this.conn; //
	}

}
