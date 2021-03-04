package cn.com.infostrategy.ui.sysapp.login;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupDefineVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.to.sysapp.login.LoginOneOffVO;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

public interface SysAppServiceIfc extends WLTRemoteCallServiceIfc {

	//��¼���
	public LoginOneOffVO loginOneOff(String _usercode, String _pwd, String _adminpwd, Boolean isAdmin, Boolean _isquicklogin, Properties _clientProp, String checkcode) throws Exception; // 

	//��¼�߼�!!
	public CurrLoginUserVO login(String _usercode, String _pwd, String _adminpwd, Boolean isAdmin, Boolean _isquicklogin, String checkcode) throws Exception; //

	/**
	 * �û��˳�
	 * @param _userID String
	 */
	public void loginOut(String _userid, String _httpsessionId) throws Exception; //

	/**
	 * �����û�����,
	 *
	 * @param _userid:�û���
	 * @param _pwd:����
	 * @return
	 */
	public String modifyPasswd(String _pwd) throws Exception;

	/**
	 * �����޸�����
	 *
	 * @param _loginuser
	 * @param _pwd
	 * @return
	 */
	//Ԭ���� 20180725�޸� ��Ҫ�����̫ƽ��Ŀ����־��صı����ͻ�Ҫ������error
	public String resetPwd(String _loginuser, String _oldpwd, String _newpwd) throws Exception;

	/**
	 * ��¼��Ա����˵�����־.��¼�ͻ���ip
	 * @param _usercode
	 * @param _username
	 * @param _deptname
	 * @param _clicktime
	 * @param _menuname
	 * @throws Exception
	 */
	public void addClickedMenuLog(String _usercode, String _username, String _deptID, String _deptname, String _menuname, String _menupath, String _wasteTime) throws Exception; //

	/**
	 * �����ť����¼�.��¼�ͻ���ip
	 * @param _usercode
	 * @param _username
	 * @param _deptname
	 * @param _billname
	 * @param _btncode
	 * @param _btnname
	 * @param _menupath
	 * @throws Exception
	 */
	public void addClickButtonLog(String _usercode, String _username, String _deptname, String _billname, String _btncode, String _btnname, String _menupath) throws Exception; //

	/**
	 * ����ϵͳ����.
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void setSystemOptions(String key, String value) throws Exception; //

	/**
	 * ϵͳ�ܵķ�����
	 * @return
	 * @throws Exception
	 */
	public String setTotalLoginCount() throws Exception;

	/**
	 * �û�������
	 * @return
	 * @throws Exception
	 */
	public String setUserLoginCount() throws Exception;

	/**
	 * ȡ��ϵͳ����.
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public String getSystemOptions(String key, String dvl) throws Exception; //

	/**
	 * Ϊһ���û�����һ����ɫ..
	 * @return
	 * @throws Exception
	 */
	public String assignUserRole(String[] _userIds, String[] _roleIds) throws Exception; //

	/**
	 * Ϊһ���û�����һ����ɫ..
	 * @return
	 * @throws Exception
	 */
	public String assignUserRole(String[] _userIds, String[] _roleIds, String _deptid) throws Exception; //

	/**
	 * ȡ��������ҳĳ�������е�ʵ������,����װ��ʱ��Ҫһ������������!!
	 * @param _className
	 * @param _loginuserCode
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getDeskTopNewGroupVOData(String _className, String _loginuserCode,DeskTopNewGroupDefineVO defineVO) throws Exception; //

	/**
	 * ȡ����������������Ϣ,����ҳ����Ϣ����һ��������..
	 * @return
	 * @throws Exception
	 */
	public DeskTopNewGroupVO[] getDeskTopNewGroupVOs(String _loginuserCode) throws Exception; //

	/**
	 * ȡ��ĳһ������ķ���������..
	 * @param _loginuserCode
	 * @param _title
	 * @return
	 * @throws Exception
	 */
	public DeskTopNewGroupVO getDeskTopNewGroupVOs(String _loginuserCode, String _title, boolean _isall) throws Exception;

	/**
	 * ϵͳ�������ŵĹ�����Ϣ!
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getSysBoardRollImage() throws Exception; //

	//ȡ�ù�������
	public HashVO[] getSysBoardRollMsg(boolean _isTrim) throws Exception; //

	//ȡ��ͼƬ��64λ��
	public String getImageUpload64Code(String _batchid) throws Exception; //

	/*
	 * ����һ����ݷ�ʽ
	 */
	public void addShortCut(String _userId, String _menuId) throws Exception;

	/**
	 * ����
	 * @param _formula
	 * @return
	 * @throws Exception
	 */
	public String[] getLoginUserCorpAreasByRoleAndCorpTypeFormula(String _formula) throws Exception;

	/**
	 * ȡ�õ�¼��Աĳ�����͵��ϼ�������id
	 * @param _corpTypeCase
	 * @return
	 * @throws Exception
	 */
	public String getLoginUserCorpAreasRootIDByTypeCase(String _corpTypeCase) throws Exception;

	/**
	 * ȡ�õ�¼��Ա�Ļ�����Χ!!!
	 * @param _corpTypeCase
	 * @return
	 * @throws Exception
	 */
	public ArrayList getLoginUserCorpAreasByTypeCase(String _corpTypeCase) throws Exception;

	/**
	 * ȡ��ĳ����Ա�Ļ�����Χ,��������
	 * @param _userId
	 * @param _corpTypeCase
	 * @return
	 * @throws Exception
	 */
	public ArrayList getOneUserCorpAreasByTypeCase(String _userId, String _corpTypeCase) throws Exception;

	/**
	 * �жϵ�¼��Ա�Ƿ����ĳЩ��ɫ
	 * @param _roleCodes
	 * @return
	 * @throws Exception
	 */
	public boolean isLoginUserContainsRole(String _roleCodes) throws Exception;

	public boolean isLoginUserContainsRole(String[] _roleCodes) throws Exception;

	/**
	 * �ж�ĳ����Ա�Ƿ����ĳЩ��ɫ
	 * @param _userid
	 * @param _roleCodes
	 * @return
	 * @throws Exception
	 */
	public boolean isOneUserContainsRole(String _userid, String _roleCodes) throws Exception;

	/**
	 * �õ���¼ҳ��������ȵ�
	 * @return
	 * @throws Exception
	 */
	public String[][] getLoginHrefs() throws Exception;

	/**
	 * ȡ�����п��԰�װ��SQL�ļ�
	 * @return
	 * @throws Exception
	 */
	public String[] getAllInstallSQLTexts() throws Exception;

	/**
	 * ת������_������
	 * @param _transfernames
	 * @throws Exception
	 */
	public void transferDB_CreateColdata(String[] _transfernames) throws Exception;

	/**
	 * ת������_����������
	 * @param _transfernames
	 * @throws Exception
	 */
	public void transferDB_import(String[] _transfernames) throws Exception;

	/**
	 * ȡ��ĳ��������ֱ���ϼ�����.
	 * @param _deptid
	 * @return
	 * @throws Exception
	 */
	public String[] getOneDeptDirtDepts(String _deptid) throws Exception;

	//ȡ�õ�¼��Ա���ϼ������е�ĳ��ָ�����͵Ļ���,���������е�ָ���ֶε�ֵ!!
	public String getLoginUserParentCorpItemValueByType(String _corpType, String _nvlCorpType, String _itemName) throws Exception;

	//���ݺ�����ҳ�ĳ����Ա/������ĳ�����׻���,��������Ϊ��,���ҳ����и��׼�¼!
	public HashVO[] getParentCorpVOByMacro(int _type, String _consValue, String _macroName) throws Exception; //

	/**
	 * ȡ��һ�����ŵ������Ӳ��ŵ�����
	 * @param _parentdeptid
	 * @return
	 * @throws Exception
	 */
	public String[] getSubDeptID(String _parentdeptid) throws Exception;

	//�������ж���ı���
	public String[][] getAllTableDefineNames() throws Exception;

	/**
	 * ���ݱ���ģ����ѯ
	 */
	public String[][] getAllTableDefineNames(String tableName) throws Exception;

	/**
	 * ȡ������ֻ�ڶ������еı�
	 */
	public String[][] getAllTableOnlyDFhave() throws Exception;

	/**
	 * �õ�����ֻ����Դ�еı�
	 * @return
	 */
	public String[][] getAllTableOnlyDBhave() throws Exception;

	/**
	 * �õ�����ƽ̨������Դ���еı�
	 * @return
	 */
	public String[][] getAllTableBHhave() throws Exception;

	public List getCompareLISTByTabName(String _dataSourceName, String _tabName) throws Exception;

	/**
	 * ���ݱ�����ȡ�ñ���������Ϣ
	 */
	public String[][] getAllColumnsDefineNames(String _tabName) throws Exception;

	//���ݱ��ȡ�ö����Create�ű�
	public String getCreateSQLByTabDefineName(String _tabName) throws Exception;

	//���ݔ�������ͱ����ֶ����L������alter�ű�
	public String getAlterSQLByTabDefineName(String _dbtype, String _tabName, String _cName, String _cType, String _cLength) throws Exception;

	//ȡ�����ж��еı��alter���
	public String getAllAlterSQLByTabDefineName() throws Exception;

	//����ĳһ����ıȽ���Ϣ
	public String getCompareSQLByTabName(String _tabName) throws Exception;

	//����ʵ�ʱ�����������Java����
	public String reverseCreateJavaCode(String _tableName) throws Exception;

	/**
	 *  ��Դ���ݿ�����ݱ�ṹ���ַ�����ʽ����
	 * @param _sourceDB
	 * @param _destDB
	 * @return
	 * @throws Exception
	 */
	public String exportTableSchema(DataSourceVO _sourceDB, DataSourceVO _destDB) throws Exception;

	/**
	 *  �����ݿ����ͼ���ַ�����ʽ����
	 * @param _sourceDB
	 * @param _destDB
	 * @return
	 * @throws Exception
	 */
	public String exportTableView(DataSourceVO _sourceDB, DataSourceVO _destDB) throws Exception;

	/**
	 * 
	 * 
	 * @param _sourceDB
	 * @param _destDB
	 * @return
	 * @throws Exception
	 */
	public void transferDBDataByds(DataSourceVO _sourceDB, DataSourceVO _destDB) throws Exception;

	public Hashtable exportTableDataAsText(DataSourceVO _sourcedb, DataSourceVO _destdb) throws Exception;

	public Vector updateSequence(DataSourceVO _sourcedb, DataSourceVO _destdb) throws Exception;

	public File[] getSystemFiles(File file) throws Exception;

	public boolean hasDirectory(File file) throws Exception;

	public void xchTest() throws Exception;

	//�������л���������������id,�Ժ���Գ����BillTreePanel��ͨ�ù���,��ר������������Ϳؼ���ѡ���׽���Զ���ѯ�������ӽ��Ĺ���! ͬʱ��������ڽ��Ĺ���!!! �������źܳ�ʱ���һ������
	public void resetAllCorpBlParentCorpIds() throws Exception;

	public HashMap getServerLoginUserMap() throws Exception;

	public BillCellVO dataAccessPolicySetBuildCellVO(HashMap condition, CurrLoginUserVO _loginUserVO) throws Exception;

	public void registeTableCacheData(String _keyName, String _tableName) throws Exception;

	public void registeTreeCacheData(String _keyName, String _tableName, String _idField, String _parentId, String _seqField) throws Exception;

	public HashVO[] getCacheTableDataByAutoCreate(String _keyName, String _tableName) throws Exception;

	public HashVO[] getCacheTreeDataByAutoCreate(String _keyName, String _tableName, String _idField, String _parentId, String _seqField) throws Exception;

	public HashVO[] getCorpCacheDataByAutoCreate() throws Exception;

	public void registeCorpCacheData() throws Exception;

	public HashVO[] getUserCacheDataByAutoCreate() throws Exception;

	public void registeUserCacheData() throws Exception;

	//��Ϊ��ᵼ�µ�¼ʱ�ͻ����,���Ըɴ����ȥ��
	//public cn.com.infostrategy.to.sysapp.runtime.RtActionTempletVO getRunTimeActionTempletVO(String _templetName) throws Exception; //

	//���붯̬Java����
	public String compileRunTimeActionCode(String _ActionName, String _codeText, boolean _isSave) throws Exception; //

	//����Java��̬����
	public HashMap loadRuntimeActionCode(String _actionName) throws Exception; //

	public String[][] compareDictByDB() throws Exception;

	public String[][] compareMenuDateByDB() throws Exception;

	public void dealOneMenuCommit(String codeValue) throws Exception;

	public String[][] compareRegbuttonDateByDB() throws Exception;

	public String[][] compareUserDateByDB() throws Exception;

	public String[][] comparetempletDateByDB() throws Exception;

	public String[][] compareRegformatPanelDateByDB() throws Exception;

	public String[][] compareRegregisterDateByDB() throws Exception;

	public String[][] compareComboboxdictDateByDB() throws Exception;

	public String[][] compareOptionDateByDB() throws Exception;

	public String[][] compareLookandfeelDateByDB() throws Exception;

	public String[][] compareViewByDB() throws Exception;

	public ArrayList getLoginUserDeptIDs(String[] _filter) throws Exception;

	public HashVO getLoginUserInfo() throws Exception;

	//ȡ�����а�װ��
	public String[][] getAllInstallPackages(String _subdir) throws Exception; //

	//�õ�������Ҫ��װ�ı��嵥!!
	public String[] getAllIntallTablesByPackagePrefix(String _package_prefix) throws Exception; //

	//ʵ�ʰ�װĳ����!!
	public String createTableByPackagePrefix(String _package_prefix, String _tabName) throws Exception; //

	//ȡ��������ͼ�嵥!!
	public String[] getAllIntallViewsByPackagePrefix(String _package_prefix) throws Exception; //

	//��װĳ����ͼ!!
	public String createViewByPackagePrefix(String _package_prefix, String _viewName) throws Exception; //

	//ȡ����Ҫ��ʼ�����ݵı���嵥!
	public String[] getAllIntallInitDataByPackagePrefix(String _package_prefix, String _xtdatadir) throws Exception;

	//��ĳ������г�ʼ������!!
	public String InsertInitDataByPackagePrefix(String _package_prefix, String _xtdatadir, String _tabName) throws Exception;

	//ȡ����չ���ݰ�װ�嵥
	public String[][] getExt3DataXmlFiles(String _xmlFileName) throws Exception;

	//��װExt3����!!!
	public String installExt3Data(String _package_prefix) throws Exception;

	//ȡ��ȡ��ע��Ĳ˵�,����ƽ̨���Ʒ��һ��ϲ�����!!!
	public ArrayList getAllRegistMenu() throws Exception;

	//ȡ��ĳ��ע��˵�������!!!
	public String[] getOneRegMenuCommand(String _xmlFile, String _menuName) throws Exception;

	//ȡ�ü���ɾ����SQL
	public String[] getCascadeDeleteSQL(String _table, String _field, String _value) throws Exception;

	//ȡ�ü���ɾ��������ֵ!!
	public String[] doCascadeDeleteSQL(String _table, String[][] _fieldValues, String _sql, boolean _isAutoExec) throws Exception;

	//�����޸ĵ�����SQL
	public String[] getCascadeUpdateSQL(String _table, String _field, String _oldvalue, String _newValue) throws Exception;

	public String[] doCascadeUpdateSQL(String _table, String[][] _changedValues, String _sql, boolean _isAutoExec) throws Exception;

	//ȡ�ü��������SQL
	public String[] getCascadeWarnSQL(boolean _isPreSelect) throws Exception; //

	/**
	 * ȡ�����б��ļ�¼���Ľ��,������װ�̻�ʵʩ�����ж���Ҫ���!!! ��xch/2012-02-23��
	 */
	public String getAllTableRecordCountInfo(String[] _tables) throws Exception; //

	/**
	 * �����������б��е�ֵ!!��ʱ�����µ���ĳ���������ΪXML,���ŵ���װĿ¼��ʱ,�������ǵ�����Ӧ������ֵ(��ʹ����Ҳ���鷳),�����ͻ���ɰ�װ����¼������ʱ,��������ͻ!!! ��xch/2012-02-23��
	 * ��ʵ���ϴ������װ����������ǡ�S_������������Ҫһ�ֻ��ƿ��Է�����������,Ȼ���ڰ�װ������һ��������һ��!!! �����Ἣ��Ľ�������װ�̵Ĺ�����!! ����װ������û��pub_sequence_10001.xml��,�����ڰ�װ�����һ��ʱ���·�������֮!!
	 * @return
	 */
	public String reverseSetSequenceValue(String _packageName) throws Exception;

	/**
	 * ȡ��ĳһ�����ܵ�����߰���,���߰�����ǰֻ��һ��,���ڲ˵����ý������ϴ�word����,Ȼ����ν�鿴�������Ǵ����word����!!
	 * ����������,Word������ʱ����û���ϴ�,����Word�ļ�����̫�࣬�ͻ���ʵʩ��Ա������������,�������Բ���Ϊ��!!������Ҫһ��������˼·�ļ���ı��ļ�˵��!!!
	 * �������ڵ�˼·��,����а�������˵��,��Ĭ�ϴ�֮,Ȼ������ť�ٴ�Word����!!  
	 * ��xch/2012-02-27��
	 * @param _menuId �˵�id,��������Ѱ��word������!
	 * @param _clasName ����,��������Ѱ��help�ı�!
	 * @return �ַ�������,��һλ��help�ı�,�ڶ�λ��word������!!!
	 * @throws Exception
	 */
	public String[] getMenuHelpInfo(String _menuId, String _clasName) throws Exception;

	//�ж�ĳ����Ա�Ƿ������Ϊ��������Ա����ĳЩ����!!!
	//��������Ա(��ɫͨ��)��ƽ̨����������һ���ش����!!��������򻯾����Ȩ������,��������ϵͳ��ʹ�ù��̳�һ������Ȩ�޿��ƹ��ϡ�ʱ�޷�����ʱ!!����鿴�������������!
	//����һ�����ſ���ִ������!�ö���֮,һЩ��Ҫ��Աϰ�����������,���ܾ�ʡȥ���ǵ�ʵʩ��ά��������!!!
	public HashMap isCanDoAsSuperAdmin(String _loginUserId, String _queryTableName, String _savedTableName) throws Exception;

	public HashVO getLoginUserCorpVO() throws Exception;

	/***
	 * Gwang 2012-07-17
	 * ����ϵͳ������Sequence
	 * @return
	 * @throws Exception
	 */
	public int resetAllSequence() throws Exception;
	
	/***
	 * �����/2014-02-28��
	 * ���ò���Sequence
	 * @return
	 * @throws Exception
	 */
	public int resetSequence(ArrayList _tablenames) throws Exception;
	
	/*------------------�µİ�װģ��Զ�̵��÷���---------------------------*/
	/*
	 * �õ������Ѿ���װ���߿ɰ�װ��ģ��״̬��Ϣ
	 */
	public HashVO [] getAllInstallModuleStatus() throws Exception;
	//��ʼִ�а�װ���߸��²���
	public String installOrUpdateOperateModule(HashVO _install_updateConfig,String _operateType) throws Exception;
	//�õ���װ����
	public List getInstallOrUpdateSchedule() throws Exception;
	
	public void refreshModuleOn_OffByIds(List _onoffids) throws Exception;
	
	//��ȡ��ҳ�������� �����/2013-06-05��
	public ArrayList getRemindDatas(String _loginUserId) throws Exception;
	
	//��ɫ�͹��ܵ㷢������ͳ��
	public BillCellVO getRolesAndMenuRelation() throws Exception;
}
