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

	//登录入口
	public LoginOneOffVO loginOneOff(String _usercode, String _pwd, String _adminpwd, Boolean isAdmin, Boolean _isquicklogin, Properties _clientProp, String checkcode) throws Exception; // 

	//登录逻辑!!
	public CurrLoginUserVO login(String _usercode, String _pwd, String _adminpwd, Boolean isAdmin, Boolean _isquicklogin, String checkcode) throws Exception; //

	/**
	 * 用户退出
	 * @param _userID String
	 */
	public void loginOut(String _userid, String _httpsessionId) throws Exception; //

	/**
	 * 加密用户密码,
	 *
	 * @param _userid:用户名
	 * @param _pwd:密码
	 * @return
	 */
	public String modifyPasswd(String _pwd) throws Exception;

	/**
	 * 处理修改密码
	 *
	 * @param _loginuser
	 * @param _pwd
	 * @return
	 */
	//袁江晓 20180725修改 主要解决在太平项目中日志监控的报错，客户要求不能抛error
	public String resetPwd(String _loginuser, String _oldpwd, String _newpwd) throws Exception;

	/**
	 * 记录人员点击菜单的日志.记录客户端ip
	 * @param _usercode
	 * @param _username
	 * @param _deptname
	 * @param _clicktime
	 * @param _menuname
	 * @throws Exception
	 */
	public void addClickedMenuLog(String _usercode, String _username, String _deptID, String _deptname, String _menuname, String _menupath, String _wasteTime) throws Exception; //

	/**
	 * 点击按钮监控事件.记录客户端ip
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
	 * 设置系统参数.
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void setSystemOptions(String key, String value) throws Exception; //

	/**
	 * 系统总的访问量
	 * @return
	 * @throws Exception
	 */
	public String setTotalLoginCount() throws Exception;

	/**
	 * 用户访问量
	 * @return
	 * @throws Exception
	 */
	public String setUserLoginCount() throws Exception;

	/**
	 * 取得系统参数.
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public String getSystemOptions(String key, String dvl) throws Exception; //

	/**
	 * 为一批用户分配一批角色..
	 * @return
	 * @throws Exception
	 */
	public String assignUserRole(String[] _userIds, String[] _roleIds) throws Exception; //

	/**
	 * 为一批用户分配一批角色..
	 * @return
	 * @throws Exception
	 */
	public String assignUserRole(String[] _userIds, String[] _roleIds, String _deptid) throws Exception; //

	/**
	 * 取得桌面首页某个分组中的实际数据,在懒装入时需要一个个加载数据!!
	 * @param _className
	 * @param _loginuserCode
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getDeskTopNewGroupVOData(String _className, String _loginuserCode,DeskTopNewGroupDefineVO defineVO) throws Exception; //

	/**
	 * 取得桌面所有新闻信息,即首页上信息栏的一个个框组..
	 * @return
	 * @throws Exception
	 */
	public DeskTopNewGroupVO[] getDeskTopNewGroupVOs(String _loginuserCode) throws Exception; //

	/**
	 * 取得某一个具体的分组框的数据..
	 * @param _loginuserCode
	 * @param _title
	 * @return
	 * @throws Exception
	 */
	public DeskTopNewGroupVO getDeskTopNewGroupVOs(String _loginuserCode, String _title, boolean _isall) throws Exception;

	/**
	 * 系统滚动新闻的公告消息!
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getSysBoardRollImage() throws Exception; //

	//取得滚动新闻
	public HashVO[] getSysBoardRollMsg(boolean _isTrim) throws Exception; //

	//取得图片的64位码
	public String getImageUpload64Code(String _batchid) throws Exception; //

	/*
	 * 加入一个快捷方式
	 */
	public void addShortCut(String _userId, String _menuId) throws Exception;

	/**
	 * 根据
	 * @param _formula
	 * @return
	 * @throws Exception
	 */
	public String[] getLoginUserCorpAreasByRoleAndCorpTypeFormula(String _formula) throws Exception;

	/**
	 * 取得登录人员某种类型的上级机构的id
	 * @param _corpTypeCase
	 * @return
	 * @throws Exception
	 */
	public String getLoginUserCorpAreasRootIDByTypeCase(String _corpTypeCase) throws Exception;

	/**
	 * 取得登录人员的机构范围!!!
	 * @param _corpTypeCase
	 * @return
	 * @throws Exception
	 */
	public ArrayList getLoginUserCorpAreasByTypeCase(String _corpTypeCase) throws Exception;

	/**
	 * 取得某个人员的机构范围,根据条件
	 * @param _userId
	 * @param _corpTypeCase
	 * @return
	 * @throws Exception
	 */
	public ArrayList getOneUserCorpAreasByTypeCase(String _userId, String _corpTypeCase) throws Exception;

	/**
	 * 判断登录人员是否具有某些角色
	 * @param _roleCodes
	 * @return
	 * @throws Exception
	 */
	public boolean isLoginUserContainsRole(String _roleCodes) throws Exception;

	public boolean isLoginUserContainsRole(String[] _roleCodes) throws Exception;

	/**
	 * 判断某个人员是否具有某些角色
	 * @param _userid
	 * @param _roleCodes
	 * @return
	 * @throws Exception
	 */
	public boolean isOneUserContainsRole(String _userid, String _roleCodes) throws Exception;

	/**
	 * 得到登录页面的所有热点
	 * @return
	 * @throws Exception
	 */
	public String[][] getLoginHrefs() throws Exception;

	/**
	 * 取得所有可以安装的SQL文件
	 * @return
	 * @throws Exception
	 */
	public String[] getAllInstallSQLTexts() throws Exception;

	/**
	 * 转移数据_生成列
	 * @param _transfernames
	 * @throws Exception
	 */
	public void transferDB_CreateColdata(String[] _transfernames) throws Exception;

	/**
	 * 转移数据_真正导数据
	 * @param _transfernames
	 * @throws Exception
	 */
	public void transferDB_import(String[] _transfernames) throws Exception;

	/**
	 * 取得某个机构的直级上级机构.
	 * @param _deptid
	 * @return
	 * @throws Exception
	 */
	public String[] getOneDeptDirtDepts(String _deptid) throws Exception;

	//取得登录人员的上级机构中的某种指定类型的机构,并返回其中的指定字段的值!!
	public String getLoginUserParentCorpItemValueByType(String _corpType, String _nvlCorpType, String _itemName) throws Exception;

	//根据宏代码找出某个人员/机构的某个父亲机构,如果宏代码为空,则找出所有父亲记录!
	public HashVO[] getParentCorpVOByMacro(int _type, String _consValue, String _macroName) throws Exception; //

	/**
	 * 取得一个部门的所有子部门的主键
	 * @param _parentdeptid
	 * @return
	 * @throws Exception
	 */
	public String[] getSubDeptID(String _parentdeptid) throws Exception;

	//所有所有定义的表名
	public String[][] getAllTableDefineNames() throws Exception;

	/**
	 * 根据表名模糊查询
	 */
	public String[][] getAllTableDefineNames(String tableName) throws Exception;

	/**
	 * 取得所有只在定义中有的表
	 */
	public String[][] getAllTableOnlyDFhave() throws Exception;

	/**
	 * 得到所有只数据源有的表
	 * @return
	 */
	public String[][] getAllTableOnlyDBhave() throws Exception;

	/**
	 * 得到所有平台与数据源都有的表
	 * @return
	 */
	public String[][] getAllTableBHhave() throws Exception;

	public List getCompareLISTByTabName(String _dataSourceName, String _tabName) throws Exception;

	/**
	 * 根据表名获取该表所有列信息
	 */
	public String[][] getAllColumnsDefineNames(String _tabName) throws Exception;

	//根据表各取得定义的Create脚本
	public String getCreateSQLByTabDefineName(String _tabName) throws Exception;

	//根据數據庫類型表名字段名長度生成alter脚本
	public String getAlterSQLByTabDefineName(String _dbtype, String _tabName, String _cName, String _cType, String _cLength) throws Exception;

	//取得所有都有的表的alter语句
	public String getAllAlterSQLByTabDefineName() throws Exception;

	//生成某一个表的比较信息
	public String getCompareSQLByTabName(String _tabName) throws Exception;

	//根据实际表名反向生成Java代码
	public String reverseCreateJavaCode(String _tableName) throws Exception;

	/**
	 *  把源数据库的数据表结构以字符串形式返回
	 * @param _sourceDB
	 * @param _destDB
	 * @return
	 * @throws Exception
	 */
	public String exportTableSchema(DataSourceVO _sourceDB, DataSourceVO _destDB) throws Exception;

	/**
	 *  把数据库的视图以字符串形式返回
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

	//重置所有机构的所属机构的id,以后可以抽象成BillTreePanel的通用功能,即专门用来解决树型控件的选择父亲结点自动查询出所有子结点的功能! 同时还包含归口结点的功能!!! 这是困扰很长时间的一个问题
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

	//因为这会导致登录时就会加载,所以干脆就先去掉
	//public cn.com.infostrategy.to.sysapp.runtime.RtActionTempletVO getRunTimeActionTempletVO(String _templetName) throws Exception; //

	//编译动态Java代码
	public String compileRunTimeActionCode(String _ActionName, String _codeText, boolean _isSave) throws Exception; //

	//加载Java动态代码
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

	//取得所有安装包
	public String[][] getAllInstallPackages(String _subdir) throws Exception; //

	//得到所有需要安装的表清单!!
	public String[] getAllIntallTablesByPackagePrefix(String _package_prefix) throws Exception; //

	//实际安装某个表!!
	public String createTableByPackagePrefix(String _package_prefix, String _tabName) throws Exception; //

	//取得所有视图清单!!
	public String[] getAllIntallViewsByPackagePrefix(String _package_prefix) throws Exception; //

	//安装某个视图!!
	public String createViewByPackagePrefix(String _package_prefix, String _viewName) throws Exception; //

	//取得需要初始化数据的表的清单!
	public String[] getAllIntallInitDataByPackagePrefix(String _package_prefix, String _xtdatadir) throws Exception;

	//对某个表进行初始化数据!!
	public String InsertInitDataByPackagePrefix(String _package_prefix, String _xtdatadir, String _tabName) throws Exception;

	//取得扩展数据安装清单
	public String[][] getExt3DataXmlFiles(String _xmlFileName) throws Exception;

	//安装Ext3数据!!!
	public String installExt3Data(String _package_prefix) throws Exception;

	//取得取得注册的菜单,包括平台与产品的一起合并返回!!!
	public ArrayList getAllRegistMenu() throws Exception;

	//取得某个注册菜单的命令!!!
	public String[] getOneRegMenuCommand(String _xmlFile, String _menuName) throws Exception;

	//取得级联删除的SQL
	public String[] getCascadeDeleteSQL(String _table, String _field, String _value) throws Exception;

	//取得级联删除的所有值!!
	public String[] doCascadeDeleteSQL(String _table, String[][] _fieldValues, String _sql, boolean _isAutoExec) throws Exception;

	//级联修改的所有SQL
	public String[] getCascadeUpdateSQL(String _table, String _field, String _oldvalue, String _newValue) throws Exception;

	public String[] doCascadeUpdateSQL(String _table, String[][] _changedValues, String _sql, boolean _isAutoExec) throws Exception;

	//取得级联警告的SQL
	public String[] getCascadeWarnSQL(boolean _isPreSelect) throws Exception; //

	/**
	 * 取得所有表格的记录数的结果,在做安装盘或实施过程中都需要这个!!! 【xch/2012-02-23】
	 */
	public String getAllTableRecordCountInfo(String[] _tables) throws Exception; //

	/**
	 * 反向生成序列表中的值!!有时在重新导出某个表格数据为XML,并放到安装目录中时,经常忘记导出对应的序列值(即使导出也很麻烦),这样就会造成安装后再录入数据时,报主键冲突!!! 【xch/2012-02-23】
	 * 而实际上大多数安装表的主键都是“S_表名”所以需要一种机制可以反向生成序列,然后在安装结束后一下子重做一遍!!! 这样会极大的降低做安装盘的工作量!! 即安装盘中是没有pub_sequence_10001.xml的,而是在安装的最后一步时重新反向生成之!!
	 * @return
	 */
	public String reverseSetSequenceValue(String _packageName) throws Exception;

	/**
	 * 取得某一个功能点的在线帮助,在线帮助以前只有一个,在在菜单配置界面中上传word附件,然后所谓查看帮助就是打开这个word附件!!
	 * 但后来发现,Word附件有时经常没有上传,而且Word文件内容太多，客户与实施人员经常根本不看,且内容以操作为主!!所以需要一个表达管理思路的简短文本文件说明!!!
	 * 所以现在的思路是,如果有帮助文字说明,则默认打开之,然后点击按钮再打开Word附件!!  
	 * 【xch/2012-02-27】
	 * @param _menuId 菜单id,根据它来寻找word附件名!
	 * @param _clasName 类名,根据它来寻找help文本!
	 * @return 字符串数组,第一位是help文本,第二位是word附件名!!!
	 * @throws Exception
	 */
	public String[] getMenuHelpInfo(String _menuId, String _clasName) throws Exception;

	//判断某个人员是否可以作为超级管理员操作某些对象!!!
	//超级管理员(绿色通道)是平台后来新增的一个重大概念!!它将极大简化经后的权限问题,尤其是在系统在使用过程成一发生“权限控制过严”时无法操作时!!比如查看工作流加密意见!
	//总有一个后门可以执行所有!久而久之,一些主要人员习惯了这个后门,可能就省去我们的实施或维护精力了!!!
	public HashMap isCanDoAsSuperAdmin(String _loginUserId, String _queryTableName, String _savedTableName) throws Exception;

	public HashVO getLoginUserCorpVO() throws Exception;

	/***
	 * Gwang 2012-07-17
	 * 重置系统中所有Sequence
	 * @return
	 * @throws Exception
	 */
	public int resetAllSequence() throws Exception;
	
	/***
	 * 【李春娟/2014-02-28】
	 * 重置部分Sequence
	 * @return
	 * @throws Exception
	 */
	public int resetSequence(ArrayList _tablenames) throws Exception;
	
	/*------------------新的安装模块远程调用方法---------------------------*/
	/*
	 * 得到所有已经安装或者可安装的模块状态信息
	 */
	public HashVO [] getAllInstallModuleStatus() throws Exception;
	//开始执行安装或者更新操作
	public String installOrUpdateOperateModule(HashVO _install_updateConfig,String _operateType) throws Exception;
	//得到安装进度
	public List getInstallOrUpdateSchedule() throws Exception;
	
	public void refreshModuleOn_OffByIds(List _onoffids) throws Exception;
	
	//获取首页提醒数据 【杨科/2013-06-05】
	public ArrayList getRemindDatas(String _loginUserId) throws Exception;
	
	//角色和功能点发布关联统计
	public BillCellVO getRolesAndMenuRelation() throws Exception;
}
