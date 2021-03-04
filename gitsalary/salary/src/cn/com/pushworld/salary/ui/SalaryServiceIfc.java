package cn.com.pushworld.salary.ui;

import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

public interface SalaryServiceIfc extends WLTRemoteCallServiceIfc {
	// 得到一个表的字段和描述 从table.xml中取
	public String[][] getTableItemAndDescr(String _table) throws Exception;

	// 插入评分详情表
	public void createScoreTable(HashMap param) throws Exception;

	// 检查指标合法性
	public HashMap checkViladate(String month) throws Exception;

	// 部门考核进度查询
	public BillCellVO getTargetCheckProcessVO(String month) throws Exception;

	// 员工考核进度查询
	public BillCellVO getPersonCheckProcessVO(String month) throws Exception;

	// 得分统计
	public BillCellVO getScoreVO(String month) throws Exception;

	public boolean endPlanDL_Dept(String logid) throws Exception;

	public BillCellVO getScoreVO_Person(String month) throws Exception;

	public BillCellVO getScoreVO_Person(String month, String[] checkeduserids) throws Exception;

	public BillCellVO getPersonTargetCheckResVO(String logid, String checkeduserid) throws Exception;

	// 评分详情
	public BillVO[] getScoreDetail(String sql, String templetcode) throws Exception;

	// 评分详情
	public BillCellVO getScoreDetailCellVO(String sql, String templetcode) throws Exception;

	public HashMap endPlan(String logid, boolean isquiet, String state) throws Exception;

	public HashMap endPlanDx(String logid, boolean isquiet, String state) throws Exception;

	public BillCellVO[] getExtDateDirect(String checkdate, String[] deptid) throws Exception;

	public BillCellVO getDeptCheckResVO(String checkdate, String checkedDeptid) throws Exception;

	public BillCellVO getTargetCheckResVO(String checkdate, String checkedDeptid) throws Exception;

	public BillCellVO getPostCheckResVO(String checkdate, String checkeduserid) throws Exception;

	public BillCellVO getPostTargetCheckResVO(String checkdate, String checkeduserid) throws Exception;

	public void onCreateSalaryBill(List sql, String salarybillid, String accountid, String checkdate,BillVO vo) throws Exception;

	public BillCellVO[] getTarget_Check_ReviseVO(String logid) throws Exception;

	public BillCellVO getSalaryVO(String[] column, String checkdate) throws Exception;

	public HashMap endCalcDeptDXScore(String logid, boolean isquiet, String state) throws Exception;

	public HashMap endCalcPersonDXScore(String logid, boolean isquiet, String state) throws Exception;

	public void onePlanCalcAllUserEveryDXTargetScore(String logid) throws Exception;

	public Object[][] calcYearPersonCheckReport(String[] logid) throws Exception;

	/** *** 以下是公式计算远程调用方法 **** */
	public Object[] onExecute(HashVO _factorVO, HashVO _baseDataHashVO) throws Exception;

	public void calcDeptDLtarget(HashVO planvo, String state, String _targetType) throws Exception;

	public void onCalcPersonDLTarget(HashVO planvo, String _calbatch) throws Exception;

	public Object getRemoteActionSchedule(String _key, String _billType) throws Exception;

	public void calc_P_Pay(HashVO _planVO, String _calbatch) throws Exception;

	public void calc_QQ_Pay(HashVO _planVO) throws Exception;

	public void calcDelayPay(String _logid) throws Exception;

	public void calcDeptTotleScoreIntoReviseTable(String _logid, String state) throws Exception;

	public HashVO[] calcOnePersonTarget_P_Money(String _targetID, String _checkdate) throws Exception;

	public String calcOneDeptTargetDL(HashVO targetVO, String selectDate) throws Exception;

	//部门指标计算结果列表展示
	public HashVO[] calcOneDeptTargetDL2(HashVO targetVO, String selectDate) throws Exception;

	/** ************* 结束 **************** */

	// 部门考核汇总
	public HashVO[] getDeptCheck(String[] checkids, String[] types) throws Exception;

	// 员工考核汇总
	public HashVO[] getPersonCheck(String[] checkids, String[] types, String[] types_nocou) throws Exception;

	// 员工业务汇总
	public HashVO[] getPersonBusiness(String[] checkids, String[] types_id) throws Exception;

	// 员工工资汇总
	public HashVO[] getPersonSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception;

	// 岗位工资汇总
	public HashVO[] getPostSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception;

	// 部门工资汇总
	public HashVO[] getDeptSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception;

	// 延期支付基金 免责基金 股权激励基金
	public HashVO[] getPersonStyle(String type, String month, String userid, String excel_code, String[] types, String[] types_nocou) throws Exception;

	// 延期支付基金 免责基金 股权激励基金 总计
	public HashVO[] getPersonStylee(String tablename, String[] dates, String excel_code, String[] types, String[] types_nocou, HashMap _condition) throws Exception;

	// 股权激励基金
	public HashVO[] getPersonGqjl(String tablename, int[] years, String excel_code, HashMap _condition) throws Exception;

	// 延期支付基金
	public HashVO[] getPersonYqzf(String year, String[] months, HashMap _condition) throws Exception;

	/** ************************************ */
	public BillCellVO parseCellTempetToWord(BillCellVO _cellTemplet, HashVO _baseHVO) throws Exception;

	/** **预警*** */

	public HashVO getWarnVoById(String _warnid, String _logid) throws Exception;

	public void sendMessage(String[] _session, String _message) throws Exception;

	public HashVO[] getAllInstallUser() throws Exception;

	public BillCellVO getReportCellVO(HashMap _where, String _filename) throws Exception;

	/** ==============================岗位价值评估--lcj================================== */

	// 生成岗位评估表记录【李春娟/2013-10-31】
	public void createPostEvalTable(String _planid) throws Exception;

	// 获得岗位价值评估界面数据【李春娟/2013-10-31】
	public BillCellVO getPostEvalVO(String _planid, String _planname, String _userid, String _postids) throws Exception;

	// 提交岗位价值评估【李春娟/2013-11-01】
	public void submitPostEvalTable(String _planid, String _userid) throws Exception;

	// 获得岗位价值评估进度统计数据【李春娟/2013-10-31】
	public BillCellVO getPostEvalScheduleVO(String _planid) throws Exception;

	// 获得岗位价值评估得分统计数据【李春娟/2013-10-31】
	public BillCellVO getPostEvalScoreVO(String _planid) throws Exception;

	// 获得岗位价值评估得分统计详细数据--某个岗位得分详情【李春娟/2013-10-31】
	public BillCellVO getPostEvalScoreDetailVO(String _planid, String _postid) throws Exception;

	/** ========================年度经营计划--lcj================================== */
	// 复制年度经营计划【李春娟/2014-01-07】
	public void copyOperatePlan(String _planid, String _newplanid) throws Exception;

	// 获得年度经营计划之完成率【李春娟/2014-01-09】
	public HashVO[] getOperatePlanFullfillRate(String _planid, String _months) throws Exception;

	/** ***************** 反馈功能**************************** */

	public List getFeedbackDataHashvo(String _sql, int[] __rowArea) throws Exception;

	public void createPostDutyScoreTable(HashMap param) throws Exception;

	/** *********************涡阳-岗位职责评议计算******************************* */

	public HashMap calcPostDutyTargetScore(HashVO _planvo, String state) throws Exception;

	public BillCellVO getPostDutyCheckProcess(String logid) throws Exception;

	public BillCellVO getPostDutyCheckCompute(String logid) throws Exception;

	public BillCellVO getPostDutyDetailCompute(String logid, String checkeduserid) throws Exception;

	public BillCellVO getDeptTargetQueryCellVO(HashMap hashMap) throws Exception;

	public BillCellVO getPersonPostTargetQueryCellVO(HashMap hashMap) throws Exception;

	public BillCellVO getPostMoneyDif(String _checkdate, String factor[]) throws Exception;

	public HashMap checkDataByPolicy(HashVO[] _data, String _excelID) throws Exception;

	/**********************接口开发***************************/

	public void createTableByConfig(String _dataIFCmainID) throws Exception;

	public void readDataFromFile(String _dataIFCmainID, String _datadate) throws Exception;
	//zzl[2018-11-23]數據接口批量同步
	public String impReadDataFromFile(String _dataIFCmainID, String _datadate) throws Exception;
	//zzl[2019-3-13]数据上传不显示数据模板
	public String impDataUpload(HashMap[] exceldatas,String table_name);

	public String[][] compareDBTOConfig(HashVO hashVO, HashVO[] hashVOs) throws Exception;

	public void convertIFCDataToReportByHand(String _mainid, String _datadate) throws Exception;

	/*****************************************************/
	//根据部门计价指标获取部门的绩效工资
	public BillCellVO getJJTargetDeptJXMoney(String _logid, String _deptids[], String[] _noJoinStationkind) throws Exception;

	//2020年7月12日12:46:01    fj   岗位工资汇总年度汇总
	public HashVO[] getPostGatherSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception;


}
