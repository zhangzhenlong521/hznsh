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

	//ȡ��Ԫ����ģ����������,��Զ��Ĭ������Դȡ!!
	public Pub_Templet_1VO getPub_Templet_1VO(String _code) throws Exception;

	//һ��ȡ��һ��ģ��,�Ӷ�ʡȥ���紫��,ȡ��Ԫ����ģ����������,��Զ��Ĭ������Դȡ!!
	public Pub_Templet_1VO[] getPub_Templet_1VOs(String[] _codes) throws Exception;

	//ȡ��Ԫ����ģ����������,��Զ��Ĭ������Դȡ!!
	//public Pub_Templet_1VO getPub_Templet_1VO(AbstractTMO _tmo) throws Exception;

	public Pub_Templet_1VO getPub_Templet_1VO(HashVO _parentVO, HashVO[] _childVOs, String _buildFromType, String _buildFromInfo) throws Exception;

	public DefaultTMO getDefaultTMOByCode(String _code, int _fromType) throws Exception;

	//����ServerTMODefineȡ��ģ��VO,���ڷ�����������
	public Pub_Templet_1VO getPub_Templet_1VO(ServerTMODefine _serverTMO) throws Exception; //

	//ȡ�ò�ѯ����ϵ�SQL�Զ��崴�����Ĵ�����SQL
	public String getBillQueryPanelSQLCustCreate(String _className, String _itemKey, String _itemValue, HashMap _allItemValues, HashMap _allItemSQLs, String _wholeSQL) throws Exception;

	//ȡ�ÿ�Ƭ�е�����,��ָ������Դȡ!
	public Object[] getBillCardDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception;

	//����BillVO������,Ϊ���������
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, String _templetCode) throws Exception; //

	//����BillVO������,Ϊ���������
	public BillVOBuilder getBillVOBuilder(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception; //

	//ȡ��BillVO����!!!
	public BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception; //

	//ȡ��BillVO����!!!
	public BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula) throws Exception; //

	//ȡ���б��е�����,��ָ������Դȡ!
	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception;

	//ȡ���б��е�����,��ָ������Դȡ!
	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula) throws Exception;

	//ȡ���б��е�����
	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula, int[] _rowArea) throws Exception;

	//ȡ���б��е�����
	public Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO, boolean _isExecLoadFormula, int[] _rowArea, boolean _isRegHVOinRowNumberVO) throws Exception;

	//����ͨ��SQL,����ֱ��ͨ��HasVO����ģ���ʽ����
	public Object[][] getBillListDataByHashVOs(Pub_Templet_1ParVO _templetVO, HashVO[] hashVOs) throws Exception; // 

	//ȡ�����ͽṹ�е�����!!!
	public TableDataStruct getBillTreeData(String _datasourcename, String _sql, Pub_Templet_1ParVO _templetVO) throws Exception;

	public String resetTreeLinkName(String _datasourcename, String _tableName, String _pkId, String _nameField, String _parentId, String _linkName) throws Exception; //

	public String[] checkTreeOneFieldRule(String _datasourcename, String _tableName, String _pkfield, String _nameField, String _parentPKfield, String _seqField, String _checkField, String[] _rules) throws Exception; //

	//ȡ�ò�ѯģ���е�����,��ָ������Դȡ
	public Object[][] getQueryDataByDS(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws Exception;

	//�ύָ������Դ�е�BillVO!!
	public void commitBillVOByDS(String _dsName, BillVO[] _deleteVOs, BillVO[] _insertVOs, BillVO[] _updateVOs) throws Exception;

	//ȡ��һ����Ľṹ�����ݵ�SQL
	public String getTableStructDataSQL(String[] _tabnames, String _dbtype, int _exportarea, String _sqlviewtype) throws Exception; //

	public String getTableStructDataSQLAll(String[] _tabnames, String _dbtype, int _exportarea, String _sqlviewtype) throws Exception; //

	//ȡ��BillCellVO
	public BillCellVO getBillCellVO(String _templetCode, String _billNo, String _descr) throws Exception;

	//����BillCellPanel�ϵ�����.
	public void saveBillCellVO(String _dsName, BillCellVO _cellVO) throws Exception; //

	//����BillCellData
	public void copyBillCellData(String _templetcode, String _billNO, String _descr) throws Exception;

	//����10��������..
	public void createTenDemoRecords(String _datasourcename, String _tablename) throws Exception;

	//ͨ��ģ���޸ı�
	public void compareTemplateAndTable(String _datasourcename, String[] _tablename, String[] _template) throws Exception;

	//��BillCellPanel�ﱣ�����ݵķ���
	public void getCellSaveDate(String templethid, String _billno, HashMap hashmap) throws Exception;

	//��BillCellPanel���ȡ���ݵķ���
	public HashVO[] getCellLoadDate(String templethid, String _billno) throws Exception;

	//����ע����������..
	public String getRegFormatPanelSQL(String[] _regformatcode, String _dbtype, String _sqlviewtype) throws Exception; //

	//����һ��SQL,����<record></record>��һ��xml����!
	public String getXMlFromTableRecords(String _sourcedsname, String[] _sqls) throws Exception; //

	//����SQL������¼,ͬʱָ�������Լ��! �Ժ�ƽ̨����ר�Ŷ���һ�������Լ���ĵط�,�������ƽ̨�������ֵ��ж���,�������������ֱ�Ӵ������ֵ���ȡ��!
	public String getXMlFromTableRecords(String _sourcedsname, String[] _sqls, String[][] _pkConstraint, String[][] _foreignPKConstraint, String _xmlFile) throws Exception; //

	//��ȡ����ģ��
	public List<Object> getAllTemplate(String templetcode, String tablename, String type) throws Exception;

	//һ��ȡ1000����¼��
	public HashMap getXMlFromTable1000Records(String _dsName, String _tableName, String _pkName, int _beginNo) throws Exception;

	//��һ�������������XML,Ȼ�󻹿�������ĳ���ֶ�ֵ!!
	public String getXMLDataBySQLAsResetId(String _dsName, String _sql, String _tabName, String _resetIdField) throws Exception;

	//��xml�ļ��е����ݵ���ȥ���ݿ�
	public String importRecordsXMLToTable(String[] _xmlFileNams, String _dsName, boolean _isReCreateId, HashMap param) throws Exception;

	//����1000����¼!
	public void importXmlToTable1000Records(String _dsName, String _fileName, String _xml) throws Exception; //

	//����Ԫԭģ��SQL
	public String getExportBillTempletSQL(String _code, String _dbtype, boolean _iswrap) throws Exception; //

	//ת�����ݿ�.
	public int transferDB(String _sourcedsname, String _destdsname, String _table, boolean _iscreate, boolean _isinsert) throws Exception; //

	public String safeMoveData(String _sourcedsname, String _destdsname, String tablename, HashMap tableMap, String type, HashMap conditon) throws Exception; //

	//��֤���ݿ��Ƿ��йؼ���.
	public String getCollidePKByTableName(String[] _tablename, String _databasetype) throws Exception;

	//�����ɻ�Ҫ��ֱ������һ������,���������ڷ���!ͨ�����Լ���,����д��ʽ��!
	public HashMap getDataPolicyTargetCorpsByCorpId(String _deptId, String _datapolicy, String _returnCol) throws Exception;

	//�����ɻ�Ҫ��ֱ������һ����Ա,���������ڷ���!ͨ�����Լ���,����д��ʽ��!
	public HashMap getDataPolicyTargetCorpsByUserId(String _loginUserid, String _datapolicy, String _returnCol) throws Exception;

	//ָ��һ����������,Ȼ��ָ������,Ȼ��ָ�����˵��ֶ���,�����Ȼ����һ��������SQL����!!!
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, int _type, String _corpFieldName, String _userFieldName) throws Exception; ////

	//ȡ������Ȩ�޹��˵�SQL����!!
	public String[] getDataPolicyCondition(String _loginUserid, String _datapolicy, String _datapolicyMap) throws Exception; ////

	/**
	 * �õ���ǰϵͳ�����к͹ؼ�����ײ���ֶ�
	 * 
	 * @param _databasetype
	 * @return
	 * @throws Exception
	 */
	public String getAllCollidePK(String _databasetype) throws Exception;

	//����һ����ͼ....
	public void transferDBView(String _sourcedsname, String _destdsname, String _viewname) throws Exception; //

	//����XML��ʽģ��
	public void importXMLTemplet(String str_dataSource, String textarea) throws Exception; //

	//����XML��ʽģ��
	public String exportXMLTemplet(String[] str_templete_code, int[] selectrows) throws Exception; //

	//Ǩ��ģ�浽����Դ
	public void transportTempletToDataSource(String[] str_templete_code, String _sourceDS, Object[] _destDSs) throws Exception;

	//����XML��ʽע������
	public void importXMLRegFormat(String textarea) throws Exception; //

	//����XML��ʽע������
	public String exportXMLRegFormat(String[] ids) throws Exception;

	//����XML��ʽע�ᰴť
	public void importXMLRegButton(String textarea) throws Exception; //

	//����XML��ʽע�ᰴť
	public String exportXMLRegButton(String[] str_refbutton_code, int[] selectrows) throws Exception; //

	//����XML��ʽע�����
	public void importXMLRegRef(String textarea) throws Exception; //

	public void importXML(String text) throws Exception;

	public String exportXMLForExcel(String templetcode) throws Exception;

	//����XML��ʽע�����
	public String exportXMLRegRef(String[] str_refref_code, int[] selectrows) throws Exception; //

	//����XML��ʽ����
	public void importXMLProcess(String textarea) throws Exception; //

	public void importXMLProcess_Copy(String textarea) throws Exception; //

	//��������ͼ
	public String copyProcessById(String _processid, String _processcode, String _processname) throws Exception;

	public String importDBXMLProcess(String wf_code, String wf_name, String str_xml, String userdef01, String wf_securitylevel) throws Exception;//�ϲ�ͼ��xml��ʽ

	//����XML��ʽע�����
	public String exportXMLProcess(String[] str_process_code, int[] selectrows) throws Exception; //

	//����XML��ʽ������
	public void importXMLCombobox(String textarea) throws Exception; //

	//����XML��ʽ������
	public String exportXMLCombobox(String[] str_combobox_code, int[] selectrows) throws Exception; //

	//���浥�ݵ���ʷ�ۼ�
	public void saveKeepTrace(String _tablename, String _pkname, String _pkvalue, HashMap _fieldvalues, String _tracer) throws Exception;

	//��������
	public String getZipFileName(String[] filename) throws Exception;

	//ɾ����������office�ؼ����ļ�
	public boolean deleteOfficeFileName(String _filename) throws Exception;

	//ɾ���������ϵ��ļ�
	public boolean deleteZipFileName(String filename) throws Exception;

	public boolean fileExist(String filename) throws Exception;

	public boolean filesExist(String[] filenames) throws Exception;

	//��ģ���ӱ��������µ��ֶ�!
	public String insertTempletItem(String _templetId, String[][] _items) throws Exception; //

	//���渻�����ı����е�����!!����ϴ洢��pub_stylepaddoc����
	public String saveStylePadDocument(String _batchid, String _doc64code, String _text, String[] _sqls) throws Exception; //

	//����ͼƬ
	public String saveImageUploadDocument(String _batchid, String _doc64code, String _tabName, String _pkName, String _pkValue) throws Exception; //

	public void buildDemoData(String _saveTable, BillVO _vos[], int _num) throws Exception;

	public String getCardReportHtml(Pub_Templet_1VO template, BillVO vo);

	public byte[] getCardReportWord(Pub_Templet_1VO templetVO, BillVO billVO) throws Exception;

	public String getAddColumnSql(String _dbtype, String _tabName, String _cName, String _cType, String _cLength) throws Exception;

	public String getDropColumnSql(String _dbtype, String _tabName, String _cName) throws Exception;

	/**
	* �������ݿ������б�������ֵ䣬��������tables.xml�����/2012-08-27��
	*/
	public String exportAllTables() throws Exception;

	//excel�����ϴ�����У�顾���/2019-06-10��
	public HashMap checkExcelDataByPolicy(HashVO[] _hashVOs, String _id) throws Exception;
}
