package cn.com.infostrategy.bs.report.style2;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

/**
 * 风格报表2的数据构造器
 * 风格报表2的样子是一个BillCell的面板,根据查询条件生成一个BillCellPanel
 * 数据构造器类要指定返回的数据集,排序列,需要纵向相同合并的列..
 * 以后还可以指定分组小计的列
 * @author xch
 *
 */
public interface StyleReport_2_BuildDataIFC {

	/**
	 * 报表标题
	 * @return
	 */
	public String getTitle(); //

	/**
	 * 排序的列
	 * @return 返回一个n行三列的数组,其中第一列是列名,必须与返回的HashVO中的列名品配,第二列指明是否是倒序(Y/N),第三列是指明是否是数字("Y"/"N")
	 * 比如 new String[][]{{"工号","N","N"},{"姓名","Y","N"}}
	 */
	public String[][] getSortColumns(); //

	/**
	 * 定义哪些列需要进行相同行的值进行合并显示!!,按道理合并的列应该必须定义排序
	 * @return 指定需要相一合并的列,比如 new String[]{"工号","姓名"}
	 */
	public String[] getSpanColumns(); //需要纵向

	/**
	 * 构造裸数据,根本不要关心排序与分组合并的事,只要把业务数据"转"出来就行!!!
	 * @param _condition   查询面板中送入的查询条件!!!
	 * @param _loginUserVO 登录人员的相关信息
	 * @return
	 * @throws Exception
	 */
	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO _loginUserVO) throws Exception; //

}
