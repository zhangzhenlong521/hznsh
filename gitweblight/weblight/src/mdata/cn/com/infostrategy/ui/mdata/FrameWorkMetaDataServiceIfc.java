/**************************************************************************
 * $RCSfile: FrameWorkMetaDataServiceIfc.java,v $  $Revision: 1.50 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.BillVOBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1ParVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

public interface FrameWorkMetaDataServiceIfc extends WLTRemoteCallServiceIfc {

	public void importOneTempletVO(String _tablename, String _templetCode, String _templetName) throws Exception;

	//取得元数据模板配置数据,永远从默认数据源取!!
	public Pub_Templet_1VO getPub_Templet_1VO(String _code) throws Exception;

	//一次取得一批模板,从而省去网络传输,取得元数据模板配置数据,永远从默认数据源取!!
	public Pub_Templet_1VO[] getPub_Templet_1VOs(String[] _codes) throws Exception;

	//取得元数据模板配置数据,永远从默认数据源取!!
	//public Pub_Templet_1VO getPub_Templet_1VO(AbstractTMO _tmo) throws Exception;

	public Pub_Templet_1VO getPub_Templet_1VO(HashVO _parentVO, HashVO[] _childVOs, String _buildFromType, String _buildFromInfo) throws Exception;

	public DefaultTMO getDefaultTMOByCode(String _code, int _fromType) throws Exception;

	//根据ServerTMODefine取得模板VO,即在服务器端生成
	public Pub_Templet_1VO getPub_Templet_1VO(ServerTMODefine _serverTMO) throws Exception; //

	//取得查询面板上的SQL自定义创建器的创建的SQL
	public String getBillQueryPanelSQLCustCreate(String _className, String _itemKey, String _itemValue, HashMap _allItemValues, HashMap _allItemSQLs, String _wholeSQL) throws Exception;

	//取得卡片中的数据,从指定数据源取!
	public Object[] getBillCardDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception;

	//生成BillVO构造器,为了提高性能
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, String _templetCode) throws Exception; //

	//生成BillVO构造器,为了提高性能
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception; //

	//取得BillVO数组!!!
	public BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception; //

	//取得BillVO数组!!!
	public BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula) throws Exception; //

	//取得列表中的数据,从指定数据源取!
	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception;

	//取得列表中的数据,从指定数据源取!
	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula) throws Exception;

	//取得列表中的数据
	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula, int[] _rowArea) throws Exception;

	//取得列表中的数据
	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula, int[] _rowArea, boolean _isRegHVOinRowNumberVO) throws Exception;

	//不是通过SQL,而是直接通过HasVO创建模板格式数据
	public Object[][] getBillListDataByHashVOs(Pub_Templet_1ParVO _templetVO, HashVO[] hashVOs) throws Exception; // 

	//取得树型结构中的数据!!!
	public TableDataStruct getBillTreeData(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception;

	public String resetTreeLinkName(String _datasourcename, String _tableName, String _pkId, String _nameField, String _parentId, String _linkName) throws Exception; //

	public String[] checkTreeOneFieldRule(String _datasourcename, String _tableName, String _pkfield, String _nameField, String _parentPKfield, String _seqField, String _checkField, String[] _rules) throws Exception; //

	//取得查询模板中的数据,从指定数据源取
	public Object[][] getQueryDataByDS(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception;

	//提交指定数据源中的BillVO!!
	public void commitBillVOByDS(String _dsName, BillVO[] _deleteVOs, BillVO[] _insertVOs, BillVO[] _updateVOs) throws Exception;

	//取得一批表的结构与数据的SQL
	public String getTableStructDataSQL(String[] _tabnames, String _dbtype, int _exportarea, String _sqlviewtype) throws Exception; //

	public String getTableStructDataSQLAll(String[] _tabnames, String _dbtype, int _exportarea, String _sqlviewtype) throws Exception; //

	//取得BillCellVO
	public BillCellVO getBillCellVO(String _templetCode, String _billNo, String _descr) throws Exception;

	//保存BillCellPanel上的数据.
	public void saveBillCellVO(String _dsName, BillCellVO _cellVO) throws Exception; //

	//拷贝BillCellData
	public void copyBillCellData(String _templetcode, String _billNO, String _descr) throws Exception;

	//创建10条假数据..
	public void createTenDemoRecords(String _datasourcename, String _tablename) throws Exception;

	//通过模板修改表
	public void compareTemplateAndTable(String _datasourcename, String[] _tablename, String[] _template) throws Exception;

	//往BillCellPanel里保存数据的方法
	public void getCellSaveDate(String templethid, String _billno, HashMap hashmap) throws Exception;

	//往BillCellPanel里读取数据的方法
	public HashVO[] getCellLoadDate(String templethid, String _billno) throws Exception;

	//导出注册样板数据..
	public String getRegFormatPanelSQL(String[] _regformatcode, String _dbtype, String _sqlviewtype) throws Exception; //

	//根据一批SQL,导出<record></record>的一批xml数据!
	public String getXMlFromTableRecords(String _sourcedsname, String[] _sqls) throws Exception; //

	//根据SQL导出记录,同时指定主外键约束! 以后平台可以专门定义一个主外键约束的地方,比如就在平台的数据字典中定义,这样连主外键都直接从数据字典中取了!
	public String getXMlFromTableRecords(String _sourcedsname, String[] _sqls, String[][] _pkConstraint, String[][] _foreignPKConstraint, String _xmlFile) throws Exception; //

	//获取所有模板
	public List<Object> getAllTemplate(String templetcode, String tablename, String type) throws Exception;

	//一次取1000条记录的
	public HashMap getXMlFromTable1000Records(String _dsName, String _tableName, String _pkName, int _beginNo) throws Exception;

	//将一个表的数据生成XML,然后还可以重置某个字段值!!
	public String getXMLDataBySQLAsResetId(String _dsName, String _sql, String _tabName, String _resetIdField) throws Exception;

	//将xml文件中的数据导入去数据库
	public String importRecordsXMLToTable(String[] _xmlFileNams, String _dsName, boolean _isReCreateId, HashMap param) throws Exception;

	//导入1000条记录!
	public void importXmlToTable1000Records(String _dsName, String _fileName, String _xml) throws Exception; //

	//导出元原模板SQL
	public String getExportBillTempletSQL(String _code, String _dbtype, boolean _iswrap) throws Exception; //

	//转移数据库.
	public int transferDB(String _sourcedsname, String _destdsname, String _table, boolean _iscreate, boolean _isinsert) throws Exception; //

	public String safeMoveData(String _sourcedsname, String _destdsname, String tablename, HashMap tableMap, String type, HashMap conditon) throws Exception; //

	//验证数据库是否含有关键字.
	public String getCollidePKByTableName(String[] _tablename, String _databasetype) throws Exception;

	//刘旋飞机要的直接送入一个机构,返回其所在分行!通过策略计算,不是写公式了!
	public HashMap getDataPolicyTargetCorpsByCorpId(String _deptId, String _datapolicy, String _returnCol) throws Exception;

	//刘旋飞机要的直接送入一个人员,返回其所在分行!通过策略计算,不是写公式了!
	public HashMap getDataPolicyTargetCorpsByUserId(String _loginUserid, String _datapolicy, String _returnCol) throws Exception;

	//指定一个策略名称,然后指定类型,然后指定过滤的字段名,最后自然返回一个完整的SQL条件!!!
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, int _type, String _corpFieldName, String _userFieldName) throws Exception; ////

	//取得数据权限过滤的SQL条件!!
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, String _datapolicyMap) throws Exception; ////

	/**
	 * 得到当前系统表所有和关键字碰撞的字段
	 * 
	 * @param _databasetype
	 * @return
	 * @throws Exception
	 */
	public String getAllCollidePK(String _databasetype) throws Exception;

	//导出一个视图....
	public void transferDBView(String _sourcedsname, String _destdsname, String _viewname) throws Exception; //

	//导入XML格式模版
	public void importXMLTemplet(String str_dataSource, String textarea) throws Exception; //

	//导出XML格式模版
	public String exportXMLTemplet(String[] str_templete_code, int[] selectrows) throws Exception; //

	//迁移模版到数据源
	public void transportTempletToDataSource(String[] str_templete_code, String _sourceDS, Object[] _destDSs) throws Exception;

	//导入XML格式注册样板
	public void importXMLRegFormat(String textarea) throws Exception; //

	//导出XML格式注册样板
	public String exportXMLRegFormat(String[] ids) throws Exception;

	//导入XML格式注册按钮
	public void importXMLRegButton(String textarea) throws Exception; //

	//导出XML格式注册按钮
	public String exportXMLRegButton(String[] str_refbutton_code, int[] selectrows) throws Exception; //

	//导入XML格式注册参照
	public void importXMLRegRef(String textarea) throws Exception; //

	public void importXML(String text) throws Exception;

	public String exportXMLForExcel(String templetcode) throws Exception;

	//导出XML格式注册参照
	public String exportXMLRegRef(String[] str_refref_code, int[] selectrows) throws Exception; //

	//导入XML格式流程
	public void importXMLProcess(String textarea) throws Exception; //

	public void importXMLProcess_Copy(String textarea) throws Exception; //

	//复制流程图
	public String copyProcessById(String _processid, String _processcode, String _processname) throws Exception;

	public String importDBXMLProcess(String wf_code, String wf_name, String str_xml, String userdef01, String wf_securitylevel) throws Exception;//迪博图的xml格式

	//导出XML格式注册参照
	public String exportXMLProcess(String[] str_process_code, int[] selectrows) throws Exception; //

	//导入XML格式下拉框
	public void importXMLCombobox(String textarea) throws Exception; //

	//导出XML格式下拉框
	public String exportXMLCombobox(String[] str_combobox_code, int[] selectrows) throws Exception; //

	//保存单据的历史痕迹
	public void saveKeepTrace(String _tablename, String _pkname, String _pkvalue, HashMap _fieldvalues, String _tracer) throws Exception;

	//批量下载
	public String getZipFileName(String[] filename) throws Exception;

	//删除服务器上office控件的文件
	public boolean deleteOfficeFileName(String _filename) throws Exception;

	//删除服务器上的文件
	public boolean deleteZipFileName(String filename) throws Exception;

	public boolean fileExist(String filename) throws Exception;

	public boolean filesExist(String[] filenames) throws Exception;

	//往模板子表中增加新的字段!
	public String insertTempletItem(String _templetId, String[][] _items) throws Exception; //

	//保存富多行文本框中的内容!!即打断存储在pub_stylepaddoc表中
	public String saveStylePadDocument(String _batchid, String _doc64code, String _text, String[] _sqls) throws Exception; //

	//保存图片
	public String saveImageUploadDocument(String _batchid, String _doc64code, String _tabName, String _pkName, String _pkValue) throws Exception; //

	public void buildDemoData(String _saveTable, BillVO _vos[], int _num) throws Exception;

	public String getCardReportHtml(Pub_Templet_1VO template, BillVO vo);

	public byte[] getCardReportWord(Pub_Templet_1VO templetVO, BillVO billVO) throws Exception;

	public String getAddColumnSql(String _dbtype, String _tabName, String _cName, String _cType, String _cLength) throws Exception;

	public String getDropColumnSql(String _dbtype, String _tabName, String _cName) throws Exception;

	/**
	* 导出数据库中所有表的数据字典，反向生成tables.xml【李春娟/2012-08-27】
	*/
	public String exportAllTables() throws Exception;

	//excel数据上传规则校验【李春娟/2019-06-10】
	public HashMap checkExcelDataByPolicy(HashVO[] _hashVOs, String _id) throws Exception;
}
