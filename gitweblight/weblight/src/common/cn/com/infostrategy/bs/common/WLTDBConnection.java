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
 * ���ݿ�������,���ǽ�JDBC�����ݿ����Ӱ�װ��һ��!!
 *
 * MySQL���䷨
 *<dbtype>MYSQL</dbtype>
 *<dbversion>5</dbversion>
 *<driver>com.mysql.jdbc.Driver</driver>
 *<dburl>jdbc:mysql://192.168.0.10:3000/ipushgrc?characterEncoding=GBK</dburl>
 *<user>root</user>
 *<pwd>111111</pwd>
 *<initsize>1</initsize>
 *<poolsize>10000</poolsize>
 *
 *Oracle���䷨
 *<dbtype>ORACLE</dbtype>
 *<dbversion>10g</dbversion>
 *<driver>oracle.jdbc.OracleDriver</driver>  <!-- ��ǰ�Ƕ�һ��driver,����������˵��������� -->
 *<dburl>jdbc:oracle:thin:@192.168.0.12:1521:buick</dburl>
 *<user>cib</user>
 *<pwd>cib</pwd>
 *
 *SQLServer���䷨
 *<dbtype>SQLSERVER</dbtype>
 *<dbversion>2005</dbversion>
 *<driver>com.microsoft.sqlserver.jdbc.SQLServerDriver</driver>
 *<dburl>jdbc:sqlserver://192.168.0.117:1433;DataBaseName=cmb</dburl>
 *<user>sa</user>
 *<pwd>sa</pwd>
 *
 *DB2���䷨
 *<dbtype>DB2</dbtype>
 *<dbversion>9.5</dbversion>
 *<driver>com.ibm.db2.jcc.DB2Driver</driver>
 *<dburl>jdbc:db2://192.168.0.15:50000/databasename:currentSchema=PUSH_GRC;</dburl>
 *<user>db2admin</user>
 *<pwd>111111</pwd>
 *
 */
public class WLTDBConnection {

	private String dsName = null; //����Դ����!!

	private java.sql.Connection conn = null;

	private int openStmtCount = 0;

	public static int MAX_ACTIVES = 0; //
	public static int MAX_INSTANCES = 0; //

	/**
	 * Ĭ������Դ
	 * @throws SQLException
	 */
	public WLTDBConnection() throws Exception {
		this.dsName = ServerEnvironment.getInstance().getDefaultDataSourceName();
		createConn();
	}

	/**
	 * ָ������Դ
	 * @param _dsname
	 * @throws SQLException
	 */
	public WLTDBConnection(String _dsname) throws Exception {
		this.dsName = _dsname;
		createConn();
	}

	private void createConn() throws Exception {
		DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(this.dsName); //�õ�����Դ�����VO
		if (dsVO.getProvider_url() != null && dsVO.getInitial_context_factory() != null) { //������ṩ��url,�����ܴ��м���ṩ������Դ��ȡ!!(��������Ŀ�пͻ��Ƽ���������Ϊ����ʹ��WebLogic�е�����Դ)
			Hashtable ht = new Hashtable();
			ht.put(Context.INITIAL_CONTEXT_FACTORY, dsVO.getInitial_context_factory());
			ht.put(Context.PROVIDER_URL, dsVO.getProvider_url());
			InitialContext _context = new InitialContext(ht);
			javax.sql.DataSource ds = (javax.sql.DataSource) _context.lookup(dsName);
			conn = ds.getConnection();
			setTransLevel(conn); //��һ�п��ܻ����쳣!!
		} else {
			if (dsVO.getInitsize() < 0) { //�����ʼ����С��С��0,��ÿ�ζ�ֱ�Ӵ���,�����óؼ���..
				Properties myProperties = new Properties();
				myProperties.setProperty("user", dsVO.getUser());
				myProperties.setProperty("password", dsVO.getPwd());
				Class.forName(dsVO.getDriver()).newInstance(); //
				conn = DriverManager.getConnection(dsVO.getDburl(), myProperties); //��������
				setTransLevel(conn); //��һ�п��ܻ����쳣!!
			} else { //�����dbcp����ȡ...
				PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:"); //��ȡ�ó�����!!!
				GenericObjectPool pool = (GenericObjectPool) driver.getConnectionPool(this.dsName); //����һ�����ӳ�!!!
				try {
					//conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:" + this.dsName); ////������������,��ʵҲ�Ǵ�dbcp����ȡ��!!!������ǰ��ȡ��,��ΪΪ������ʱ��¼��ϵͳ��������������,�ĳ��˴ӳ���ȡ�ķ���!!
					int li_currBusy = pool.getNumActive(); //���!!!
					int li_currFree = pool.getNumIdle(); //������!!!
					int li_currInstances = li_currBusy + li_currFree; //����ʵ����
					dealMaxActives(li_currBusy, li_currInstances); //��ؼ�������������������æµ�������ʵ����!!!!
					conn = (java.sql.Connection) pool.borrowObject(); //ȡ������!!!!
					setTransLevel(conn); //��һ�п��ܻ����쳣!!
				} catch (Exception ex) {
					ex.printStackTrace(); //
					System.err.println("���������쳣,��������ӱ����õ�ԭ��,��������ӳ������л���,��������..."); //
					pool.clear(); //��������ӳ������ж��󣡣���
					conn = (java.sql.Connection) pool.borrowObject(); //���´�������,�����������������ɵ��쳣,��������ȫ�����ܲ����쳣!!!
					setTransLevel(conn); //
				}
			}
		}

		//System.out.println("��ǰ�߳�[" + Thread.currentThread() + "]��ȡ���ݿ�����[" + this.dsName + "]"); //��һ��Զ�̷����ܹ�ȡ�˼���!!
	}

	//����������뼶��
	private void setTransLevel(java.sql.Connection _conn) throws Exception {
		if ("Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISTRANSLEVELDOWN"))) { //�����Ҫ����������뼶��!
			_conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_UNCOMMITTED); //
		} else {
			_conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED); //TRANSACTION_READ_COMMITTED,TRANSACTION_REPEATABLE_READ,����������뼶��!!��������!!
			_conn.setAutoCommit(false); //�˹��ύ!!
		}
	}

	//����ͬ��!!
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
	 * ������׼Statement
	 * @return
	 * @throws SQLException
	 */
	public java.sql.Statement createStatement() throws SQLException {
		openStmtCount = openStmtCount + 1;
		return conn.createStatement();
	}

	/**
	 * ����PrepareStatement
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException {
		openStmtCount = openStmtCount + 1;
		return conn.prepareStatement(sql);
	}

	/**
	 * ����CallableStatement,Ϊ�洢������洢������!!!
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

	//���α������!!
	public int getOpenStmtCount() {
		return openStmtCount;
	}

	public Connection getJDBCConnection() {
		return this.conn; //
	}

}
