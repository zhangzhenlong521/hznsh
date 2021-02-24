package cn.com.infostrategy.bs.report.style3;

/**
 * 缺省适配器模式,实现此类必须实现4个抽象方法，分别为得到数据的Hashvo方法，返回行，返回列，返回需要查询的列。
 * 其中有2个方法为普通方法，一个为是否需要分组，一个为统计类型，如果需要分组的话，那么必须指定统计类型。
 * 如果不需要分组的话，那么返回的Hashvo必须指定统计类型，也就是在sql里写好count或者sum等.
 */
import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

public abstract class AbstractStyleReport_3_BuildDataAdapter implements StyleReport_3_BuildDataIFC {

	public String getChartTitle() {
		return "图表";
	}

	public abstract HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO userVO) throws Exception;

	public abstract String getColHeadName(); // 得到列头

	//列头的显示顺序,即可以指定按什么顺序来显示!!
	public String[] getColHeaderViewSort() {
		return null; //
	}

	//列头是否是零汇报机制,所谓零汇报机制就是排序字段中存在的都必须输出，不管是否在实际数据中存在过，即如果没有发生也要以零数量的方式输出!!
	//就像"非典时期",如果哪个省即使没有发生非典也要报告，报告发生数量为0！故称零汇报机制！
	public boolean isColHeaderZeroReportType() {
		return false;
	}

	public abstract String getRowHeadName();// 得到行头

	//行头的显示顺序
	public String[] getRowHeaderViewSort() {
		return null; //
	}

	//行头是否是零汇报机制,所谓零汇报机制就是排序字段中存在的都必须输出，不管是否在实际数据中存在过，即如果没有发生也要以零数量的方式输出!!
	//就像"非典时期",如果哪个省即使没有发生非典也要报告，报告发生数量为0！故称零汇报机制！
	public boolean isRowHeaderZeroReportType() {
		return false; //默认为否,即是根据实际数据来显示,如果排序为空,该项为true也没有效果!!
	}

	public abstract String getComputeItemName(); //需要查询的列

	public abstract String getComputeType();// 统计的类型SELECT,COUNT,SUM,AVG

}
