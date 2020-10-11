/*******************************************************************************
 * $RCSfile: FrameWorkCommServiceIfc.java,v $ $Revision: 1.23 $ $Date:
 * 2011/11/18 11:18:03 $
 ******************************************************************************/
package cn.com.infostrategy.ui.common;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.ImageIcon;

import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.InitParamVO;
import cn.com.infostrategy.to.common.LinkForeignTableDefineVO;
import cn.com.infostrategy.to.common.Log4jConfigVO;
import cn.com.infostrategy.to.common.SQLBuilderIfc;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellVO;

/**
 * 平台通用服务,基本上都是查询数据库存,保存数据库,执行SQL,存储过程等!!
 * @author user
 */
public interface FrameWorkCommServiceIfc extends WLTRemoteCallServiceIfc {

	/**
	 * 通用方法!!!通过该方法,只要直接在服务器端写一个类就行了,也就是连在接口中增加方法都不要干了!!!!!
	 * @param _className
	 * @param _functionName
	 * @param _parMap
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public HashMap commMethod(String _className, String _functionName, HashMap _parMap) throws Exception; // 会抛远程调用异常

	/**
	 * 取得所有初始化参数!
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public InitParamVO[] getInitParamVOs() throws Exception;

	public Log4jConfigVO getLog4jConfigVO() throws Exception;

	public Hashtable getLanguage() throws Exception; // 一下子取得所有语言

	public String[] getLanguage(String _key) throws Exception; // 根据某一个key取得该key对应的各种语言!!

	public String getServerCurrDate() throws Exception; // 取得服务器端的当前日期..

	public String getServerCurrTime() throws Exception; // 取得服务器端的当前时间..

	public long getServerCurrTimeLongValue() throws Exception; // 取得服务器端当前时间的Long值!

	public String[][] getServerSysOptions() throws Exception; // 取得服务器端参数

	public String getServerFile(String filename); // 取得服务器端文件

	/**
	 * 一个死等几秒的测试方法
	 * @param _second
	 * @return
	 * @throws Exception
	 */
	public Integer sleep(Integer _second) throws Exception; // 休息几秒

	/**
	 * 抓取服务器端屏幕的方法,返回一个图像
	 * @return
	 * @throws Exception
	 */
	public byte[] captureScreen() throws Exception; // 抓取服务器端屏幕的方法

	public byte[] mouseClick(int _type, int _x, int _y) throws Exception; //

	// 增加某一个会话的SQL监听，太平集团的监控系统对我们系统远程报错会预警，为减少预警，这里修改返回提示信息【李春娟/2018-07-25】
	public String addSessionSQLListener(String _sessionid) throws Exception;

	// 删除某一个会话的SQL监听，太平集团的监控系统对我们系统远程报错会预警，为减少预警，这里修改返回提示信息【李春娟/2018-07-25】
	public String removeSessionSQLListener(String _sessionid) throws Exception;

	// 取得监听的SQL
	public String getSessionSQLListenerText(String _sessionid, boolean _isclear) throws Exception;

	// 杀掉服务器端某个线程,在处理一个长时间操作时,等待框需要干掉服务器端远程处理线程时需要这个..
	public int killServerThreadBySessionId(String _sessionID) throws Exception;

	/**
	 * 取得所有数据源
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public DataSourceVO[] getDataSourceVOs() throws Exception;

	/**
	 * 取得服务器端日志数据
	 * @return
	 * @throws Exception
	 */
	public String getServerLog() throws Exception;

	/**
	 * 取得服务器端控制台信息..
	 * @return
	 * @throws Exception
	 */
	public String getServerConsole(boolean _isclear) throws Exception;

	/**
	 * 取得服务器端配置文件内容
	 * @return
	 * @throws Exception
	 */
	public String getServerConfigXML() throws Exception;

	/**
	 * 取得各个数据源的池中情况
	 * @return
	 * @throws Exception
	 */
	public String[][] getDatasourcePoolActiveNumbers() throws Exception;

	/**
	 * 取得各个远程服务的池中情况
	 * @return
	 * @throws Exception
	 */
	public String[][] getRemoteServicePoolActiveNumbers() throws Exception;

	// 取得服务器端资源图片!!!
	public ImageIcon getImageFromServerRespath(String _path) throws Exception;

	/**
	 * 取得所有图片的名称
	 * @return
	 * @throws Exception
	 */
	public String[] getImageFileNames() throws Exception;

	/**
	 * 获得服务器端的系统属性
	 * @return
	 * @throws Exception
	 */
	public Properties getServerSystemProperties() throws Exception;

	/**
	 * 取得服务器端的Environment数据
	 * @return
	 * @throws Exception
	 */
	public String[][] getServerEnvironment() throws Exception;

	public String[][] getServerOnlineUser() throws Exception;

	/**
	 * 取得默认数据源
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public String getDeaultDataSource() throws Exception; // 取得默认数据源

	// 取一批主键回来
	public Long[] getSequenceNextValByDS(String _datasourcename, String _sequenceName, Integer _batch) throws Exception;

	// 返回In条件
	public String getInCondition(String _datasourcename, String _sql) throws Exception;

	// 得到子查询的SQL,专门用来解决SQL太长的问题!!!
	public String getSubSQLFromTempSQLTableByIDs(String[] _ids) throws Exception; //

	// 取得系统所有的表名,_tableNamePattern可以作为过滤条件,比如"pub_%"
	public String[] getAllSysTables(String _datasourcename, String _tableNamePattern) throws Exception; //

	// 取得所有系统表,可指定是否包含视图,是否从xml注册中取得说明
	public String[][] getAllSysTableAndDescr(String _datasourcename, String _tableNamePattern, boolean _isContainView, boolean _isGetDescrFromXML) throws Exception; //

	// 直接返回一个字符串
	public String getStringValueByDS(String _datasourcename, String _sql) throws Exception;

	// 返回二维数组
	public String[][] getStringArrayByDS(String _datasourcename, String _sql) throws Exception;

	// 返回一个SQL生成二维结构的第一列,常用于生成子查询用
	public String[] getStringArrayFirstColByDS(String _datasourcename, String _sql) throws Exception;

	// 根据SQL返回HashMap
	public HashMap getHashMapBySQLByDS(String _datasourcename, String _sql) throws Exception;

	// 根据SQL返回HashMap,如果发生相同的key则自动累加成
	public HashMap getHashMapBySQLByDS(String _datasourcename, String _sql, boolean _appendSameKey) throws Exception;

	// 返回表结构
	public TableDataStruct getTableDataStructByDS(String _datasourcename, String _sql) throws Exception;

	// 返回带结构的HashVO数组
	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql) throws Exception;

	// 返回结果结构! 指定前多少行!
	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql, int _topRecords) throws Exception; //

	// 子查询返回
	public HashVO[] getHashVoArrayBySubSQL(String _datasourcename, String _parentsql, LinkForeignTableDefineVO[] _childVOs) throws Exception;

	// 返回HashVO[]
	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql) throws Exception;

	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql, int _topRecords) throws Exception; //

	// 返回树型结构的hashVO
	public HashVO[] getHashVoArrayAsTreeStructByDS(String _datasourcename, String _sql, String _idField, String _parentIDField, String _seqField, String _rootNodeCondition) throws Exception;

	// 返回一条记录在一个树型结构中的所有父亲路径结点的数据!!!!!
	public HashVO[] getTreePathVOsByOneRecord(String _datasourcename, String _tableName, String _idFieldName, String _parentIdFieldName, String _whereField, String _whereCondition) throws Exception;

	// 返回一条记录的所有子结点!
	public HashVO[] getTreeChildVOsByOneRecord(String _datasourcename, String _tableName, String _idFieldName, String _parentIdFieldName, String _whereCondition) throws Exception;

	// 返回一批记录的所有id与路径的哈希表
	public HashMap getTreePathNameByRecords(String _datasourcename, String _tableName, String _idFieldName, String _nameFieldName, String _parentIdFieldName, String[] _idValues) throws Exception;

	// 送一组SQL,返回Vector
	public Vector getHashVoArrayReturnVectorByDS(String _datasourcename, String[] _sqls) throws Exception; // 送入一组SQL,Vector中每一项都是一个HashVO[]

	public Vector getHashVoStructReturnVectorByDS(String _datasourcename, String[] _sqls) throws Exception; // 送入一组SQL,Vector中每一项都是一个HashVOStruct

	// 送一组SQL,返回HashMap
	public HashMap getHashVoArrayReturnMapByDS(String _datasourcename, String[] _sqls, String[] _keys) throws Exception; // 送一组SQL返回HashMap,Keys对应SQLs位置，是返回HashMap中的key

	// 执行一条SQL
	public Integer executeUpdateByDS(String _datasourcename, String _sql) throws Exception; // 在指定数据源上,执行一条数据库修改语句,比如insert,delete,update

	// 执行一条SQL
	public Integer executeUpdateByDSPS(String _datasourcename, String _sql) throws Exception; // 在指定数据源上,执行一条数据库修改语句,比如insert,delete,update

	//直接提交!!!没有事务锁!在邮储中,性能测试时发现切换用户皮肤时造成行锁,实际应该那条SQL不需要事务!
	public Integer executeUpdateByDSAutoCommit(String _datasourcename, String _sql) throws Exception;

	// 执行一条SQL
	public Integer executeUpdateByDS(String _datasourcename, SQLBuilderIfc _sqlBuilder) throws Exception; // 在指定数据源上,执行一条数据库修改语句,比如insert,delete,update

	// 执行一条SQL,带宏!!!!
	public Integer executeMacroUpdateByDS(String _datasourcename, String _sql, String[] _colvalues) throws Exception;

	// 执行一批SQL
	public void executeBatchByDS(String _datasourcename, String[] _sqls) throws Exception; // 在指定数据源上,执行一批数据库修改语句,比如insert,delete,update

	public void executeBatchByDS(String _datasourcename, java.util.List _sqllist, boolean _isDebugLog, boolean _isDBLog) throws Exception; // 在指定数据源上,执行一批数据库修改语句,比如insert,delete,update,是否打印sql，是否记录执行日志

	public void executeBatchByDSNoLog(String _datasourcename, String _sqls) throws Exception; // 在指定数据源上,执行一批数据库修改语句,比如insert,delete,update无日志

	// 执行一批SQL
	public void executeBatchByDS(String _datasourcename, SQLBuilderIfc[] _sqlBuilders) throws Exception; // 在指定数据源上,执行一批数据库修改语句,比如insert,delete,update

	// 执行一批SQL
	public void executeBatchByDS(String _datasourcename, java.util.List _sqllist) throws Exception; // 在指定数据源上,执行一批数据库修改语句,比如insert,delete,update

	// 存储过程,没有返回值
	public void callProcedureByDS(String _datasourcename, String procedureName, String[] parmeters) throws Exception;

	// 存储过程,没有返回值
	public void callProcedureByDSSqlServer(String _datasourcename, String procedureName, String[] parmeters) throws Exception;

	// 存储过程,返回String!
	public String callProcedureReturnStrByDS(String _datasourcename, String procedureName, String[] parmeters) throws Exception;

	// 存储函数,返回String
	public String callFunctionReturnStrByDS(String _datasourcename, String functionName, String[] parmeters) throws Exception;

	// 存储函数,返回二维结构!
	public String[][] callFunctionReturnTableByDS(String _datasourcename, String functionName, String[] parmeters) throws Exception;

	// 上传文件!!
	public String uploadFile(ClassFileVO _vo) throws Exception; //

	// 上传文件!!
	public String uploadFile(ClassFileVO _vo, boolean ifChangeName) throws Exception; //

	// 下载文件!!
	public ClassFileVO downloadFile(String _filename) throws Exception;

	public ClassFileVO downloadToClientByAbsolutePath(String _filename) throws Exception;

	/**
	 * 取得服务器端资源文件..
	 * @param _url
	 * @return
	 * @throws Exception
	 */
	public String getServerResourceFile(String _url, String _charencoding) throws Exception;

	public byte[] getServerResourceFile2(String _url, String _charencoding) throws Exception;

	/**
	 * 从服务器端下载文件返回ClassFileVO
	 * @param _serverdir
	 * @param _serverFileName
	 * @param _isAbsoluteSeverDir
	 * @return
	 * @throws Exception
	 */
	public ClassFileVO downLoadFile(String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir) throws Exception;

	/**
	 * 上传文件,将客户端文件上传至服务器端!
	 * @param ClassFileVO 上传到服务器端的文件对象
	 * @return 返回上传到服务器端文件的绝对路径!!
	 * @throws Exception
	 */
	public String upLoadFile(ClassFileVO _vo, String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir, boolean _isConvertHex, boolean _isAddSerialNo) throws Exception;

	/**
	 * 加密算法给某一个表的某一个字段加密
	 * @param _oldtablename
	 * @param _column
	 * @param _newtablename
	 * @param primarykey
	 * @throws Exception
	 */
	public void getJoinCipher(String _oldtablename, String _column, String _newtablename, String primarykey) throws Exception;

	// 平台参数
	public String[][] getAllPlatformOptions() throws Exception;

	//取得某种类型的系统参数
	public String[][] getAllPlatformOptions(String _type) throws Exception;

	// 取得系统配置参数的值
	public String[][] getAllOptions() throws Exception;

	// 刷新机构分类和机构类型缓存【李春娟/2016-05-25】
	public void refreshCorptypeFromDB() throws Exception;

	public String[][] reLoadDataFromDB(boolean _isJudgeTable) throws Exception; //

	public String getSysOptionStringValue(String _key, String _nvl) throws Exception;

	public int getSysOptionIntegerValue(String _key, int _nvl) throws Exception;

	public boolean getSysOptionBooleanValue(String _key, boolean _nvl) throws Exception;

	public String getSysOptionHashItemStringValue(String _key, String _itemkey, String _nvl) throws Exception;

	public int getSysOptionHashItemIntegerValue(String _key, String _itemkey, int _nvl) throws Exception;

	public boolean getSysOptionHashItemBooleanValue(String _key, String _itemkey, boolean _nvl) throws Exception;

	// 缓存参数配置
	public HashMap getOptionsHashMap() throws Exception;

	public void setOptions(HashMap _hashMap) throws Exception;

	// 加密字符串!
	public String encryptStr(String _str) throws Exception;

	/**
	 * 服务器word或excel文件是否含有关键字
	 * @param fileNames 服务器绝对路径
	 * @param _keywords 为关键字
	 * @param isAllContain 判断文件是否同时包含所有关键字
	 * @return
	 * @throws Exception
	 */
	public List checkWordOrExcelContainKeys(String _uploadfiledir, List _filenames, String[] _keywords, boolean _isAllContain) throws Exception;

	/** start liuxuanfei * */
	public List checkWordOrExcelContainKeys(String _uploadfiledir, String[][] _fileInfo, String[] _keywords, boolean _isAllContain) throws Exception;

	/**
	 * 重新启动所有定制的JOB
	 * @param _primarykey 用于区分Job的唯一性字段名
	 */
	public String restartJobs(String _primarykey) throws Exception;

	/**
	 * 停止某一个JOB
	 * @param _pkValue 区分Job的唯一性字段对应的值
	 */
	public String closeJob(String _pkValue) throws Exception;

	/*********************公式远程调用****************************/
	// 得到一个表的字段和描述 从table.xml中取
	public String[][] getTableItemAndDescr(String _table) throws Exception;

	public BillCellVO parseCellTempetToWord(BillCellVO _cellTemplet, HashVO _baseHVO) throws Exception;
	// 查看任务状态
	public String lookJobState(String _jobName) throws Exception;

	// 重新启动所有定制的JOB
	public String startJob(String _jobName) throws Exception;

	// 停止某一个JOB
	public String stopJob(String _jobName) throws Exception;

}

/*******************************************************************************
 * $RCSfile: FrameWorkCommServiceIfc.java,v $ $Revision: 1.23 $ $Date:
 * 2011/11/18 11:18:03 $ $Log: FrameWorkCommServiceIfc.java,v $
 * 2011/11/18 11:18:03 $ Revision 1.23  2012/10/08 02:22:50  xch123
 * 2011/11/18 11:18:03 $ *** empty log message ***
 * 2011/11/18 11:18:03 $
 * 2011/11/18 11:18:03 $ Revision 1.22  2012/09/14 09:17:30  xch123
 * 2011/11/18 11:18:03 $ *** empty log message ***
 * 2011/11/18 11:18:03 $
 * 2011/11/18 11:18:03 $ Revision 1.2  2012/09/13 05:57:16  Administrator
 * 2011/11/18 11:18:03 $ ds
 * 2011/11/18 11:18:03 $
 * 2011/11/18 11:18:03 $ Revision 1.1  2012/08/28 09:40:50  Administrator
 * 2011/11/18 11:18:03 $ *** empty log message ***
 * 2011/11/18 11:18:03 $
 * 2011/11/18 11:18:03 $ Revision 1.21  2012/03/06 09:44:21  xch123
 * 2011/11/18 11:18:03 $ *** empty log message ***
 * 2011/11/18 11:18:03 $
 * 2011/11/18 11:18:03 $ Revision 1.20  2012/02/14 01:34:14  lichunjuan
 * 2011/11/18 11:18:03 $ *** empty log message ***
 * 2011/11/18 11:18:03 $
 * 2011/11/18 11:18:03 $ Revision 1.19  2012/02/13 10:16:54  lichunjuan
 * 2011/11/18 11:18:03 $ *** empty log message ***
 * 2011/11/18 11:18:03 $
 * 2011/11/18 11:18:03 $ Revision 1.18  2011/12/14 11:59:17  liuxuanfei
 * 2011/11/18 11:18:03 $ *** empty log message ***
 * 2011/11/18 11:18:03 $ Revision 1.17
 * 2011/11/18 11:18:03 xch123 *** empty log message *** Revision 1.16 2011/11/18
 * 11:04:47 xch123 *** empty log message *** Revision 1.15 2011/10/10 06:31:35
 * wanggang restore Revision 1.13 2011/08/22 13:38:53 sunfujun *** empty log
 * message *** Revision 1.12 2011/07/27 11:33:36 xch123 *** empty log message
 * *** Revision 1.11 2011/05/05 10:20:05 xch123 *** empty log message ***
 * Revision 1.10 2011/05/05 07:18:14 xch123 *** empty log message *** Revision
 * 1.9 2011/03/31 11:14:40 xch123 *** empty log message *** Revision 1.8
 * 2011/03/17 10:54:45 xch123 *** empty log message *** Revision 1.7 2011/01/27
 * 09:55:52 xch123 兴业春节前回来 Revision 1.6 2010/12/28 10:29:11 xch123 12月28日提交
 ******************************************************************************/
