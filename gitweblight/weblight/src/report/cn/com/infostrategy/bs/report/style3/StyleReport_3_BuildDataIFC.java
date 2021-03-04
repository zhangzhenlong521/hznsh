package cn.com.infostrategy.bs.report.style3;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

/**
 * 风格报表3的数据构造器 风格报表3的样子是一个BillChart的面板,根据查询条件生成一个BillChartPanel
 * 数据构造器类要指定返回的数据集,行，列,需要查询的列名。
 * 
 * @author xch
 * 
 */
public interface StyleReport_3_BuildDataIFC {

	//select(c1),count(*),sum(c1),avg(c1)
	
	public static String SELECT = "select"; //
	public static String COUNT = "count"; //
	public static String SUM = "sum"; //
	public static String MAX = "max"; //
	public static String MIN = "min"; //
	
	public String getColHeadName(); // 需要列的名字
	public String getRowHeadName(); // 需要行的名字
	
	public String getComputeType();  //
	public String getComputeItemName();  //

	/**
	 * 构造裸数据,根本不要关心排序与分组合并的事,只要把业务数据"转"出来就行!!!
	 * 这个方法将会比其他方法先行调用,所以可以在该方法里面进行一些类变量赋值,然后让其他方法可以调用!!
	 * @param _condition
	 *            查询面板中送入的查询条件!!!
	 * @param _loginUserVO
	 *            登录人员的相关信息
	 * @return
	 * @throws Exception
	 */
	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO _loginUserVO) throws Exception; //

}

