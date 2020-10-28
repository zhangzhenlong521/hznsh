package cn.com.pushworld.wn.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

public interface WnSalaryServiceIfc extends WLTRemoteCallServiceIfc {
	public String getHfWg();
	/**
	 * zzl[柜员服务质量评价]
	 * 
	 * @return
	 */
	public String getSqlInsert(String time);

	/**
	 * zpy[部门指标打分]
	 */
	public String getBMsql(String planid);

	public String gradeBMScoreEnd();

	/**
	 * zpy[导入全量数据]
	 * 
	 * @return
	 */
	public String ImportAll();

	/**
	 * zpy[导入一天的数据]
	 * 
	 * @param date
	 *            :日期，具体格式为:[20190301]
	 * @return
	 */
	public String ImportDay(String date);

	/**
	 * zpy[导入某张表某天的数据]
	 * 
	 * @param filePath
	 * @return
	 */
	public String ImportOne(String filePath);

	/**
	 * zzl[贷款客户经理信息更新]
	 * 
	 * @return
	 */
	public String getChange(String date1, String date2);
	
	/**
	 * ZPY 【2020-06-07】新贷款数据变更
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public String getNewChange(String startDate,String endDate);

	/**
	 * zzl[存款客户经理信息更新]
	 * 
	 * @return
	 */
	public String getCKChange(String date1, String date2);
	/**
	 * zzl[存款客户经理信息更新]
	 *
	 * @return
	 */
	public String getWgChange(String date1, String date2);

	/**
	 * zpy[2019-05-22] 为每个柜员生成定性考核计划
	 * 
	 * @return:返回最后执行结果
	 */
	public String gradeDXscore(String id);

	/**
	 * zpy[2019-05-22] 结束当前考核计划
	 * 
	 * @param planid
	 *            :当前计划id
	 * @return
	 */
	public String gradeDXEnd(String planid);

	/**
	 * ZPY[2019-05-23] 客户经理定性打分生成
	 * 
	 * @param id
	 * @return
	 */
	public String gradeManagerDXscore(String id);

	/**
	 * ZPY[2019-05-23] 结束所有客户经理定性考核
	 * 
	 * @param id
	 *            :计划ID
	 * @return
	 */
	public String endManagerDXscore(String id);

	/**
	 * zzl[贷款户数完成比]
	 */
	public String getDKFinishB(String date);

	/**
	 * zpy[黔农E贷的完成比计算]
	 * 
	 * @param date_time
	 *            :查询日期
	 * @return
	 */
	public String getQnedRate(String date_time);

	/**
	 * zpy[黔农E贷线上替代完成比计算]
	 * 
	 * @param date_time
	 *            :查询日期
	 * @param username
	 *            :客户经理名
	 * @return
	 */
	public String getQnedtdRate(String date_time);

	/**
	 * zpy[手机银行完成比计算]
	 * 
	 * @param date_time
	 *            :查询日期
	 * @return
	 */
	public String getsjRate(String date_time);

	/**
	 * zpy[助农商户维护完成比计算]
	 * 
	 * @param date_time
	 *            :查询日期
	 * @param username
	 *            :客户经理
	 * @return
	 */
	public String getZNRate(String date_time);

	/**
	 * zpy[特约小微商户完成比计算]
	 * 
	 * @param date_time
	 *            :日期
	 * @return
	 */
	public String getTyxwRate(String date_time);

	/**
	 * zzl[贷款余额新增完成比]
	 */
	public String getDKBalanceXZ(String date);

	/**
	 * zzl[贷款户数新增完成比]
	 */
	public String getDKHouseholdsXZ(String date);

	/**
	 * zzl [收回表外不良贷款完成比]
	 */
	public String getBadLoans(String date);

	/**
	 * zzl[收回存量不良贷款完成比&不良贷款压降]
	 */
	public String getTheStockOfLoan(String date);

	/**
	 * 为委派会计生成打分计划
	 * 
	 * @param id
	 * @return
	 */
	public String getKJDXScore(String id);

	/**
	 * 结束当前委派会计打分
	 * 
	 * @param id
	 * @return
	 */
	public String getKJDXEnd(String id);

	/**
	 * fj[农户建档指标完成比]
	 */
	public String getNhjdHs(String date);

	/**
	 * fj[黔农e贷线上替代完成率]
	 */
	public String getQnedXstd(String data);

	/**
	 * fj[单位职工，小微企业建档完成比]
	 * 
	 * @param data
	 * @return
	 */
	public String getDwzgXwqyRatio(String data);

	/**
	 * 
	 * fj柜员绩效工资等级评定
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public String getGyClass(String date) throws Exception;

	/**
	 * fj委派会计绩效工资等级评定
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public String getWpkjClass(String date) throws Exception;

	/**
	 * 对客户经理进行绩效考核
	 * 
	 * @return
	 */
	public String managerLevelCompute(int dateNum);
	/**
	 * 将监督查询结果插入到结果表中 ZPY 【2019-07-25】
	 * @param sql
	 */
	
	public void insertMonitorResult(String sql, Map<String, String> conditionMap);

	public String updateCheckState(BillVO[] checkUsers, Map<String, String> paraMap);

    
	
	
	/**
	 * fj农民工管理指标
	 * @param replace
	 * @return
	 */
	public String getNmggl(String replace);
    /**
     * 向员工岗位系数表中填充数据
     * @param handleDate
     */
	public void insertStaffRadio(String handleDate);
    /**
     * 员工异常行为数据监测
     * @param curSelectMonth  用户选中考核月
     * @param curSelectMonthStart  用户选择考核月初日期
     * @param curSelectDate  用户选中日期
     * @return
     */
	public String importMonitorData(String curSelectDate, String curSelectMonthStart, String curSelectMonth,boolean flag);
    /**
     * 处理员工异常行为信息
     * @param billVos
     * @return
     */
	public String dealExceptionData(BillVO[] billVos,Map<String,String> paraMap);
    /**
     * 结束柜员服务质量打分
     * @param bos
     * @param pfUserDept 
     * @param pfUserName 
     */
	public String finishGradeScore(BillVO[] bos, String pfUserName,String pfUserCode, String pfUserDept);
    /**
     * 保存柜员服务质量打分
     * @param bos
     */
	public String saveGradeScore(BillVO[] bos);

	/**
	 * 助农商户维护指标 户数统计
	 * @param curSelectMonthStart:选中月当月开始时间
	 * @param curSelectDate: 用户选中时间
	 * @param curSelectMonth:用户选中月
	 * @param b:是否重新计算
	 * @return
	 */
	public String znCount(String curSelectMonthStart, String curSelectDate,
			String curSelectMonth,String dateInterVal ,boolean b);
	/**
	 * 确认信息
	 * @param vos
	 * @param state
	 * @param confirmComment
	 * @return
	 */
	public String confirm(BillVO[] vos, Map<String, String> map);
	/**
	 * 计算得到，委派会计的三项分值: 现金管理  授权业务 集中作业
	 * @return
	 */
	public List<HashMap<String,String>> getKJHandSroce(String selectDate);
  
}
