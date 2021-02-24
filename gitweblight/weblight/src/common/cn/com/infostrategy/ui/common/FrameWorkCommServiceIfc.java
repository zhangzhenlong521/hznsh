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
 * ƽ̨ͨ�÷���,�����϶��ǲ�ѯ���ݿ��,�������ݿ�,ִ��SQL,�洢���̵�!!
 * @author user
 */
public interface FrameWorkCommServiceIfc extends WLTRemoteCallServiceIfc {

	/**
	 * ͨ�÷���!!!ͨ���÷���,ֻҪֱ���ڷ�������дһ���������,Ҳ�������ڽӿ������ӷ�������Ҫ����!!!!!
	 * @param _className
	 * @param _functionName
	 * @param _parMap
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public HashMap commMethod(String _className, String _functionName, HashMap _parMap) throws Exception; // ����Զ�̵����쳣

	/**
	 * ȡ�����г�ʼ������!
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public InitParamVO[] getInitParamVOs() throws Exception;

	public Log4jConfigVO getLog4jConfigVO() throws Exception;

	public Hashtable getLanguage() throws Exception; // һ����ȡ����������

	public String[] getLanguage(String _key) throws Exception; // ����ĳһ��keyȡ�ø�key��Ӧ�ĸ�������!!

	public String getServerCurrDate() throws Exception; // ȡ�÷������˵ĵ�ǰ����..

	public String getServerCurrTime() throws Exception; // ȡ�÷������˵ĵ�ǰʱ��..

	public long getServerCurrTimeLongValue() throws Exception; // ȡ�÷������˵�ǰʱ���Longֵ!

	public String[][] getServerSysOptions() throws Exception; // ȡ�÷������˲���

	public String getServerFile(String filename); // ȡ�÷��������ļ�

	/**
	 * һ�����ȼ���Ĳ��Է���
	 * @param _second
	 * @return
	 * @throws Exception
	 */
	public Integer sleep(Integer _second) throws Exception; // ��Ϣ����

	/**
	 * ץȡ����������Ļ�ķ���,����һ��ͼ��
	 * @return
	 * @throws Exception
	 */
	public byte[] captureScreen() throws Exception; // ץȡ����������Ļ�ķ���

	public byte[] mouseClick(int _type, int _x, int _y) throws Exception; //

	// ����ĳһ���Ự��SQL������̫ƽ���ŵļ��ϵͳ������ϵͳԶ�̱����Ԥ����Ϊ����Ԥ���������޸ķ�����ʾ��Ϣ�����/2018-07-25��
	public String addSessionSQLListener(String _sessionid) throws Exception;

	// ɾ��ĳһ���Ự��SQL������̫ƽ���ŵļ��ϵͳ������ϵͳԶ�̱����Ԥ����Ϊ����Ԥ���������޸ķ�����ʾ��Ϣ�����/2018-07-25��
	public String removeSessionSQLListener(String _sessionid) throws Exception;

	// ȡ�ü�����SQL
	public String getSessionSQLListenerText(String _sessionid, boolean _isclear) throws Exception;

	// ɱ����������ĳ���߳�,�ڴ���һ����ʱ�����ʱ,�ȴ�����Ҫ�ɵ���������Զ�̴����߳�ʱ��Ҫ���..
	public int killServerThreadBySessionId(String _sessionID) throws Exception;

	/**
	 * ȡ����������Դ
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public DataSourceVO[] getDataSourceVOs() throws Exception;

	/**
	 * ȡ�÷���������־����
	 * @return
	 * @throws Exception
	 */
	public String getServerLog() throws Exception;

	/**
	 * ȡ�÷������˿���̨��Ϣ..
	 * @return
	 * @throws Exception
	 */
	public String getServerConsole(boolean _isclear) throws Exception;

	/**
	 * ȡ�÷������������ļ�����
	 * @return
	 * @throws Exception
	 */
	public String getServerConfigXML() throws Exception;

	/**
	 * ȡ�ø�������Դ�ĳ������
	 * @return
	 * @throws Exception
	 */
	public String[][] getDatasourcePoolActiveNumbers() throws Exception;

	/**
	 * ȡ�ø���Զ�̷���ĳ������
	 * @return
	 * @throws Exception
	 */
	public String[][] getRemoteServicePoolActiveNumbers() throws Exception;

	// ȡ�÷���������ԴͼƬ!!!
	public ImageIcon getImageFromServerRespath(String _path) throws Exception;

	/**
	 * ȡ������ͼƬ������
	 * @return
	 * @throws Exception
	 */
	public String[] getImageFileNames() throws Exception;

	/**
	 * ��÷������˵�ϵͳ����
	 * @return
	 * @throws Exception
	 */
	public Properties getServerSystemProperties() throws Exception;

	/**
	 * ȡ�÷������˵�Environment����
	 * @return
	 * @throws Exception
	 */
	public String[][] getServerEnvironment() throws Exception;

	public String[][] getServerOnlineUser() throws Exception;

	/**
	 * ȡ��Ĭ������Դ
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public String getDeaultDataSource() throws Exception; // ȡ��Ĭ������Դ

	// ȡһ����������
	public Long[] getSequenceNextValByDS(String _datasourcename, String _sequenceName, Integer _batch) throws Exception;

	// ����In����
	public String getInCondition(String _datasourcename, String _sql) throws Exception;

	// �õ��Ӳ�ѯ��SQL,ר���������SQL̫��������!!!
	public String getSubSQLFromTempSQLTableByIDs(String[] _ids) throws Exception; //

	// ȡ��ϵͳ���еı���,_tableNamePattern������Ϊ��������,����"pub_%"
	public String[] getAllSysTables(String _datasourcename, String _tableNamePattern) throws Exception; //

	// ȡ������ϵͳ��,��ָ���Ƿ������ͼ,�Ƿ��xmlע����ȡ��˵��
	public String[][] getAllSysTableAndDescr(String _datasourcename, String _tableNamePattern, boolean _isContainView, boolean _isGetDescrFromXML) throws Exception; //

	// ֱ�ӷ���һ���ַ���
	public String getStringValueByDS(String _datasourcename, String _sql) throws Exception;

	// ���ض�ά����
	public String[][] getStringArrayByDS(String _datasourcename, String _sql) throws Exception;

	// ����һ��SQL���ɶ�ά�ṹ�ĵ�һ��,�����������Ӳ�ѯ��
	public String[] getStringArrayFirstColByDS(String _datasourcename, String _sql) throws Exception;

	// ����SQL����HashMap
	public HashMap getHashMapBySQLByDS(String _datasourcename, String _sql) throws Exception;

	// ����SQL����HashMap,���������ͬ��key���Զ��ۼӳ�
	public HashMap getHashMapBySQLByDS(String _datasourcename, String _sql, boolean _appendSameKey) throws Exception;

	// ���ر�ṹ
	public TableDataStruct getTableDataStructByDS(String _datasourcename, String _sql) throws Exception;

	// ���ش��ṹ��HashVO����
	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql) throws Exception;

	// ���ؽ���ṹ! ָ��ǰ������!
	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql, int _topRecords) throws Exception; //

	// �Ӳ�ѯ����
	public HashVO[] getHashVoArrayBySubSQL(String _datasourcename, String _parentsql, LinkForeignTableDefineVO[] _childVOs) throws Exception;

	// ����HashVO[]
	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql) throws Exception;

	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql, int _topRecords) throws Exception; //

	// �������ͽṹ��hashVO
	public HashVO[] getHashVoArrayAsTreeStructByDS(String _datasourcename, String _sql, String _idField, String _parentIDField, String _seqField, String _rootNodeCondition) throws Exception;

	// ����һ����¼��һ�����ͽṹ�е����и���·����������!!!!!
	public HashVO[] getTreePathVOsByOneRecord(String _datasourcename, String _tableName, String _idFieldName, String _parentIdFieldName, String _whereField, String _whereCondition) throws Exception;

	// ����һ����¼�������ӽ��!
	public HashVO[] getTreeChildVOsByOneRecord(String _datasourcename, String _tableName, String _idFieldName, String _parentIdFieldName, String _whereCondition) throws Exception;

	// ����һ����¼������id��·���Ĺ�ϣ��
	public HashMap getTreePathNameByRecords(String _datasourcename, String _tableName, String _idFieldName, String _nameFieldName, String _parentIdFieldName, String[] _idValues) throws Exception;

	// ��һ��SQL,����Vector
	public Vector getHashVoArrayReturnVectorByDS(String _datasourcename, String[] _sqls) throws Exception; // ����һ��SQL,Vector��ÿһ���һ��HashVO[]

	public Vector getHashVoStructReturnVectorByDS(String _datasourcename, String[] _sqls) throws Exception; // ����һ��SQL,Vector��ÿһ���һ��HashVOStruct

	// ��һ��SQL,����HashMap
	public HashMap getHashVoArrayReturnMapByDS(String _datasourcename, String[] _sqls, String[] _keys) throws Exception; // ��һ��SQL����HashMap,Keys��ӦSQLsλ�ã��Ƿ���HashMap�е�key

	// ִ��һ��SQL
	public Integer executeUpdateByDS(String _datasourcename, String _sql) throws Exception; // ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update

	// ִ��һ��SQL
	public Integer executeUpdateByDSPS(String _datasourcename, String _sql) throws Exception; // ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update

	//ֱ���ύ!!!û��������!���ʴ���,���ܲ���ʱ�����л��û�Ƥ��ʱ�������,ʵ��Ӧ������SQL����Ҫ����!
	public Integer executeUpdateByDSAutoCommit(String _datasourcename, String _sql) throws Exception;

	// ִ��һ��SQL
	public Integer executeUpdateByDS(String _datasourcename, SQLBuilderIfc _sqlBuilder) throws Exception; // ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update

	// ִ��һ��SQL,����!!!!
	public Integer executeMacroUpdateByDS(String _datasourcename, String _sql, String[] _colvalues) throws Exception;

	// ִ��һ��SQL
	public void executeBatchByDS(String _datasourcename, String[] _sqls) throws Exception; // ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update

	public void executeBatchByDS(String _datasourcename, java.util.List _sqllist, boolean _isDebugLog, boolean _isDBLog) throws Exception; // ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update,�Ƿ��ӡsql���Ƿ��¼ִ����־

	public void executeBatchByDSNoLog(String _datasourcename, String _sqls) throws Exception; // ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update����־

	// ִ��һ��SQL
	public void executeBatchByDS(String _datasourcename, SQLBuilderIfc[] _sqlBuilders) throws Exception; // ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update

	// ִ��һ��SQL
	public void executeBatchByDS(String _datasourcename, java.util.List _sqllist) throws Exception; // ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update

	// �洢����,û�з���ֵ
	public void callProcedureByDS(String _datasourcename, String procedureName, String[] parmeters) throws Exception;

	// �洢����,û�з���ֵ
	public void callProcedureByDSSqlServer(String _datasourcename, String procedureName, String[] parmeters) throws Exception;

	// �洢����,����String!
	public String callProcedureReturnStrByDS(String _datasourcename, String procedureName, String[] parmeters) throws Exception;

	// �洢����,����String
	public String callFunctionReturnStrByDS(String _datasourcename, String functionName, String[] parmeters) throws Exception;

	// �洢����,���ض�ά�ṹ!
	public String[][] callFunctionReturnTableByDS(String _datasourcename, String functionName, String[] parmeters) throws Exception;

	// �ϴ��ļ�!!
	public String uploadFile(ClassFileVO _vo) throws Exception; //

	// �ϴ��ļ�!!
	public String uploadFile(ClassFileVO _vo, boolean ifChangeName) throws Exception; //

	// �����ļ�!!
	public ClassFileVO downloadFile(String _filename) throws Exception;

	public ClassFileVO downloadToClientByAbsolutePath(String _filename) throws Exception;

	/**
	 * ȡ�÷���������Դ�ļ�..
	 * @param _url
	 * @return
	 * @throws Exception
	 */
	public String getServerResourceFile(String _url, String _charencoding) throws Exception;

	public byte[] getServerResourceFile2(String _url, String _charencoding) throws Exception;

	/**
	 * �ӷ������������ļ�����ClassFileVO
	 * @param _serverdir
	 * @param _serverFileName
	 * @param _isAbsoluteSeverDir
	 * @return
	 * @throws Exception
	 */
	public ClassFileVO downLoadFile(String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir) throws Exception;

	/**
	 * �ϴ��ļ�,���ͻ����ļ��ϴ�����������!
	 * @param ClassFileVO �ϴ����������˵��ļ�����
	 * @return �����ϴ������������ļ��ľ���·��!!
	 * @throws Exception
	 */
	public String upLoadFile(ClassFileVO _vo, String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir, boolean _isConvertHex, boolean _isAddSerialNo) throws Exception;

	/**
	 * �����㷨��ĳһ�����ĳһ���ֶμ���
	 * @param _oldtablename
	 * @param _column
	 * @param _newtablename
	 * @param primarykey
	 * @throws Exception
	 */
	public void getJoinCipher(String _oldtablename, String _column, String _newtablename, String primarykey) throws Exception;

	// ƽ̨����
	public String[][] getAllPlatformOptions() throws Exception;

	//ȡ��ĳ�����͵�ϵͳ����
	public String[][] getAllPlatformOptions(String _type) throws Exception;

	// ȡ��ϵͳ���ò�����ֵ
	public String[][] getAllOptions() throws Exception;

	// ˢ�»�������ͻ������ͻ��桾���/2016-05-25��
	public void refreshCorptypeFromDB() throws Exception;

	public String[][] reLoadDataFromDB(boolean _isJudgeTable) throws Exception; //

	public String getSysOptionStringValue(String _key, String _nvl) throws Exception;

	public int getSysOptionIntegerValue(String _key, int _nvl) throws Exception;

	public boolean getSysOptionBooleanValue(String _key, boolean _nvl) throws Exception;

	public String getSysOptionHashItemStringValue(String _key, String _itemkey, String _nvl) throws Exception;

	public int getSysOptionHashItemIntegerValue(String _key, String _itemkey, int _nvl) throws Exception;

	public boolean getSysOptionHashItemBooleanValue(String _key, String _itemkey, boolean _nvl) throws Exception;

	// �����������
	public HashMap getOptionsHashMap() throws Exception;

	public void setOptions(HashMap _hashMap) throws Exception;

	// �����ַ���!
	public String encryptStr(String _str) throws Exception;

	/**
	 * ������word��excel�ļ��Ƿ��йؼ���
	 * @param fileNames ����������·��
	 * @param _keywords Ϊ�ؼ���
	 * @param isAllContain �ж��ļ��Ƿ�ͬʱ�������йؼ���
	 * @return
	 * @throws Exception
	 */
	public List checkWordOrExcelContainKeys(String _uploadfiledir, List _filenames, String[] _keywords, boolean _isAllContain) throws Exception;

	/** start liuxuanfei * */
	public List checkWordOrExcelContainKeys(String _uploadfiledir, String[][] _fileInfo, String[] _keywords, boolean _isAllContain) throws Exception;

	/**
	 * �����������ж��Ƶ�JOB
	 * @param _primarykey ��������Job��Ψһ���ֶ���
	 */
	public String restartJobs(String _primarykey) throws Exception;

	/**
	 * ֹͣĳһ��JOB
	 * @param _pkValue ����Job��Ψһ���ֶζ�Ӧ��ֵ
	 */
	public String closeJob(String _pkValue) throws Exception;

	/*********************��ʽԶ�̵���****************************/
	// �õ�һ������ֶκ����� ��table.xml��ȡ
	public String[][] getTableItemAndDescr(String _table) throws Exception;

	public BillCellVO parseCellTempetToWord(BillCellVO _cellTemplet, HashVO _baseHVO) throws Exception;
	// �鿴����״̬
	public String lookJobState(String _jobName) throws Exception;

	// �����������ж��Ƶ�JOB
	public String startJob(String _jobName) throws Exception;

	// ֹͣĳһ��JOB
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
 * 09:55:52 xch123 ��ҵ����ǰ���� Revision 1.6 2010/12/28 10:29:11 xch123 12��28���ύ
 ******************************************************************************/
