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
 * 报表服务!!
 * @author xch
 *
 */
public interface ReportServiceIfc extends WLTRemoteCallServiceIfc {

	/**
	 * 进行一次远程调用，取得一个WebCall的SeeeionId，为下一次Web调用创造条件
	 * @param _parVO
	 * @return
	 * @throws Exception
	 */
	public String registerWebCallSessionID(WebCallParVO _parVO) throws Exception; //

	/**
	 * 进行一次远程调用，取得一个OfficeServletCall的SeeeionId,为下一次Office控件调用时用
	 * @param _parVO
	 * @return
	 * @throws Exception
	 */
	public String registerOfficeCallSessionID(OfficeCompentControlVO _controlVO) throws Exception; //

	/**
	 * 注册一个Html内容调用的SessionID
	 * @param _htmlContent
	 * @return
	 * @throws Exception
	 */
	public String registerHtmlContentSessionID(String _htmlContent) throws Exception; //

	/**
	 * 取得一个Html所有的内容!!
	 * @return
	 * @throws Exception
	 */
	public String getHtmlContent(WebCallParVO _parVO) throws Exception; //

	/**
	 * 根据主键删除一批Excel模板对象
	 * @param _id
	 * @throws Exception
	 */
	public void deleteOneBillCellTemplet(String _id) throws Exception; //

	/**
	 * 导出Excel模板SQL
	 * @param _code
	 * @param _dbtype
	 * @param _iswrap
	 * @return
	 * @throws Exception
	 */
	public String getExportCellTempletSQL(String _code, String _dbtype, boolean _iswrap) throws Exception;

	/***
	 * 统计BILLCELL求和
	 * @param billno
	 * @param cellkey
	 * @return
	 * @throws Exception
	 */
	public HashMap sumBillData(String[] billno, String[] cellkey) throws Exception;

	public HashMap getSumCorpBillDataByContent(String[] billno, String[] cellkey) throws Exception;

	/***
	 * 获得需要统计的机构Excel信息并求和
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
	 * 取得维度定义类
	 * @param _builderClassName
	 * @return
	 * @throws Exception
	 */
	public HashMap getMultiLevelReportGroup(String _builderClassName) throws Exception;

	/*
	 * 根据查询条件,创建报表实际数据
	 */
	public HashVO[] queryMultiLevelReportData(HashMap _queryCondition, String _builderClassName, String[] _colGroupFields, String[][] _computeFields,boolean _isCtrlDown) throws Exception; //

	//钻取明細!!
	public HashVO[] queryMultiLevelReportDataDrillDetail(HashMap _queryCondition, String _builderClassName, String _ids) throws Exception; //

	/**
	 * 将一个图表导出成一个Word文件!!!返回的是Wrod的XML格式
	 * @param _jFreeChart
	 * @return
	 * @throws Exception
	 */
	public String exportChartAsWordXML(JFreeChart _jFreeChart) throws Exception;

	/**
	 * 风格报表1生成构造数据
	 * @param _condition
	 * @param _className
	 * @param _loginUserVO
	 * @return
	 * @throws Exception
	 */
	public HashVO[] styleReport_1_BuildData(HashMap _condition, String _className, CurrLoginUserVO _loginUserVO) throws Exception;

	/**
	 * 风格报表2生成构造数据
	 * @param _condition
	 * @param _className
	 * @param _loginUserVO
	 * @return
	 * @throws Exception
	 */
	public BillCellVO styleReport_2_BuildData(HashMap _condition, String _className, CurrLoginUserVO _loginUserVO) throws Exception;

	/**
	 * 风格报表3生成构造数据(图表样式)
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
	 * 取得打印模板
	 */
	public PubPrintTempletVO getPubPrintTempletVO(String _templetCode) throws Exception;

	/**
	 * 导入模板
	 */
	public void importPrintTemplet(String _billtempletCode, String _templetCode) throws Exception;

	/**
	 * 保存模板定义
	 */
	public void savePrintTempletItemBands(String _templetcode, PubPrintItemBandVO[] _itemBandVOs) throws Exception;
	
	/**
	 * 根据主键删除打印模板
	 * @param _id
	 * @throws Exception
	 */
	public void deleteOnePrintTemplet(String _id) throws Exception; //

}
