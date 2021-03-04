package cn.com.infostrategy.ui.report;

import java.util.HashMap;

import org.jfree.chart.JFreeChart;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WebCallParVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.to.print.PubPrintItemBandVO;
import cn.com.infostrategy.to.print.PubPrintTempletVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

/**
 * �������!!
 * @author xch
 *
 */
public interface ReportServiceIfc extends WLTRemoteCallServiceIfc {

	/**
	 * ����һ��Զ�̵��ã�ȡ��һ��WebCall��SeeeionId��Ϊ��һ��Web���ô�������
	 * @param _parVO
	 * @return
	 * @throws Exception
	 */
	public String registerWebCallSessionID(WebCallParVO _parVO) throws Exception; //

	/**
	 * ����һ��Զ�̵��ã�ȡ��һ��OfficeServletCall��SeeeionId,Ϊ��һ��Office�ؼ�����ʱ��
	 * @param _parVO
	 * @return
	 * @throws Exception
	 */
	public String registerOfficeCallSessionID(OfficeCompentControlVO _controlVO) throws Exception; //

	/**
	 * ע��һ��Html���ݵ��õ�SessionID
	 * @param _htmlContent
	 * @return
	 * @throws Exception
	 */
	public String registerHtmlContentSessionID(String _htmlContent) throws Exception; //

	/**
	 * ȡ��һ��Html���е�����!!
	 * @return
	 * @throws Exception
	 */
	public String getHtmlContent(WebCallParVO _parVO) throws Exception; //

	/**
	 * ��������ɾ��һ��Excelģ�����
	 * @param _id
	 * @throws Exception
	 */
	public void deleteOneBillCellTemplet(String _id) throws Exception; //

	/**
	 * ����Excelģ��SQL
	 * @param _code
	 * @param _dbtype
	 * @param _iswrap
	 * @return
	 * @throws Exception
	 */
	public String getExportCellTempletSQL(String _code, String _dbtype, boolean _iswrap) throws Exception;

	/***
	 * ͳ��BILLCELL���
	 * @param billno
	 * @param cellkey
	 * @return
	 * @throws Exception
	 */
	public HashMap sumBillData(String[] billno, String[] cellkey) throws Exception;

	public HashMap getSumCorpBillDataByContent(String[] billno, String[] cellkey) throws Exception;

	/***
	 * �����Ҫͳ�ƵĻ���Excel��Ϣ�����
	 * @param _tablename
	 * @param _excelitemkey
	 * @param _corpid
	 * @param _keys
	 * @return
	 * @throws Exception
	 */
	public HashMap getSumCorpBillDataByID(String _tablename, String _excelitemkey, String _corpitemkey, String _corpid, String[] _keys) throws Exception;

	/**
	 * 
	 * @param _hvs
	 * @param _groupFunc
	 * @param _groupField
	 * @return
	 * @throws Exception
	 */
	public HashVO[] groupHashVOs(HashVO[] _hvs, String _groupFunc, String _groupField) throws Exception;

	/**
	 * ȡ��ά�ȶ�����
	 * @param _builderClassName
	 * @return
	 * @throws Exception
	 */
	public HashMap getMultiLevelReportGroup(String _builderClassName) throws Exception;

	/*
	 * ���ݲ�ѯ����,��������ʵ������
	 */
	public HashVO[] queryMultiLevelReportData(HashMap _queryCondition, String _builderClassName, String[] _colGroupFields, String[][] _computeFields,boolean _isCtrlDown) throws Exception; //

	//��ȡ����!!
	public HashVO[] queryMultiLevelReportDataDrillDetail(HashMap _queryCondition, String _builderClassName, String _ids) throws Exception; //

	/**
	 * ��һ��ͼ������һ��Word�ļ�!!!���ص���Wrod��XML��ʽ
	 * @param _jFreeChart
	 * @return
	 * @throws Exception
	 */
	public String exportChartAsWordXML(JFreeChart _jFreeChart) throws Exception;

	/**
	 * ��񱨱�1���ɹ�������
	 * @param _condition
	 * @param _className
	 * @param _loginUserVO
	 * @return
	 * @throws Exception
	 */
	public HashVO[] styleReport_1_BuildData(HashMap _condition, String _className, CurrLoginUserVO _loginUserVO) throws Exception;

	/**
	 * ��񱨱�2���ɹ�������
	 * @param _condition
	 * @param _className
	 * @param _loginUserVO
	 * @return
	 * @throws Exception
	 */
	public BillCellVO styleReport_2_BuildData(HashMap _condition, String _className, CurrLoginUserVO _loginUserVO) throws Exception;

	/**
	 * ��񱨱�3���ɹ�������(ͼ����ʽ)
	 * @param _condition
	 * @param _className
	 * @param _loginUserVO
	 * @return
	 * @throws Exception
	 */
	public BillChartVO styleReport_3_BuildData(HashMap _condition, String _className, CurrLoginUserVO _loginUserVO) throws Exception;

	public void clearTempTableSql() throws Exception;

	public String getOracleLongInSql(String[] itemvalues, String itemkey) throws Exception;

	/**
	 * ȡ�ô�ӡģ��
	 */
	public PubPrintTempletVO getPubPrintTempletVO(String _templetCode) throws Exception;

	/**
	 * ����ģ��
	 */
	public void importPrintTemplet(String _billtempletCode, String _templetCode) throws Exception;

	/**
	 * ����ģ�嶨��
	 */
	public void savePrintTempletItemBands(String _templetcode, PubPrintItemBandVO[] _itemBandVOs) throws Exception;
	
	/**
	 * ��������ɾ����ӡģ��
	 * @param _id
	 * @throws Exception
	 */
	public void deleteOnePrintTemplet(String _id) throws Exception; //

}
