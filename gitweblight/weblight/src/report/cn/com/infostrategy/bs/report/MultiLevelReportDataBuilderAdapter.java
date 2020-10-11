package cn.com.infostrategy.bs.report;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;

/**
 * 报表造数据的适配器,
 * 可以定义demo数据
 * @author xch
 *
 */
public abstract class MultiLevelReportDataBuilderAdapter implements MultiLevelReportDataBuilderIFC {

	/**
	 * 最重要的方法,即构造明细数据！！
	 * _queryConsMap中有两个特殊的key,即【$SQL】【$SQL_1】,分别是前台拼接好的SQL,其中SQL_1是将权限策略也计算好了! 但要注意查询字段名是否要进行替换?即可能一个查询模板可能被多个地方共用，而不同地方的字段名不一样？？
	 * 李国利提出,每次在后台拼接SQL太费事了,直接将前台拼接好了的SQL放在这个变量中传过来，后台拿过来直接加在【select * from 表名 where 1=1】+【$SQL】 该有多爽??但要注意字段名是否匹配,如果不匹配,还要进行字符串替换!!所以加上这个【xch/2012-06-20】
	 */
	public abstract HashVO[] buildReportData(HashMap _queryConsMap) throws Exception; //

	/**
	 * 构造Demo数据!!!
	 * @return
	 * @throws Exception
	 */
	public HashVO[] buildDemoData() throws Exception {
		return null;
	} //

	/**
	 * 所有参与分组的列!
	 */
	public abstract String[] getGroupFieldNames();

	/**
	 * 所有参与计算的列
	 */
	public abstract String[] getSumFiledNames();

	//网格的预置的统计类型!!
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		return null;
	}

	//网格的预置的统计类型!!
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return null;
	}

	//网络的预置雷达图
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider() {
		return null;
	}

	/**
	 * 获取排序配置 
	 * 定义方式  key=表头名称，value=为排序顺序的数组
	 * 例如： key = 文件状态 value=[文件编写中][发布申请中][已发布]
	 * @return
	 */
	public HashMap getGroupFieldOrderConfig() {
		return null;
	}

	/**
	 * 零汇报机制定义,即有大量维度,如果数据库中只有两条记录下,不是就显示两条,而是将其他没有发生的也显示出来！！
	 * 就像“非典”时的零汇报机制一样,如果没有发生也要报告！就显示为零！这样保证报表的“架子”永远是“丰满”的！！
	 * @return
	 */
	public HashMap getZeroReportConfMap() {
		return null;
	}

	/**
	 * 指定哪些维度是【日期/时间】维度,为什么要特别说明这一点呢?
	 * 因为像同比,环比等计算,是专门针对时间计算的!而且计算方法很特别!即时间维度是一种“非常特别”的维度!!只有指定了哪些维度是时间维度,然后才能进行这种计算!
	 * 时间维度的字段的自动补零机制也与其他字段不一样,其他字段都是指定一个静态数组！ 但时间维度是可以找出【最小值-最大值/或者今天】然后将中间的断掉的自动补全!(这个可以不急)
	 * 时间又分三种:年,季,月
	 * @return
	 */
	public HashMap getDateGroupDefineMap() {
		return null;
	}

	/**
	 * 钻取维度相互绑定的定义,即经常有这种需要,先出现维度是季度,然后季度后面的钻取维度必然是月份,
	 * 再比如:区域与分行，分行与支行都是这种逻辑!!! 即这些维度之间钻取的先后顺序是预先绑定好的！！
	 */
	public HashMap getDrilGroupBind() {
		return null;
	}

	/*
	 * 自定义钻去数据的实现类,优先级1
	 * 需要实现：BillReportDrillActionIfc
	 */
	public String getDrillActionClassPath() throws Exception {
		return null;
	}

	/*
	 * 自定义默认钻取真实数据弹出列表的模板编码.优先级2
	 */
	public String getDrillTempletCode() throws Exception {
		return null;
	}

	/**
	 * 取得机构Demo数据
	 * @param _type
	 * @return
	 */
	public String[] getDemoDataByCorp(int _type) {
		if (_type == 1) {
			return new String[] { "总行", "上海分行", "北京分行", "江苏分行", "浙江分行", "河北分行", "山西分行" }; //
		} else if (_type == 2) {
			return new String[] { "浦东支行", "朝阳支行", "鼓楼支行", "解放街支行" }; //
		} else {
			return new String[] { "北京分行", "上海分行" };
		}
	}

	/**
	 * 取得机构Demo数据
	 * @param _type
	 * @return
	 */
	public String[] getDemoDataBySeasonMonth(int _type) {
		if (_type == 1) {
			return new String[] { "2012年1季度", "2012年2季度", "2012年3季度", "2012年4季度" }; //
		} else if (_type == 2) {
			return new String[] { "2012年01月", "2012年02月", "2012年03月", "2012年04月", "2012年05月", "2012年06月", "2012年07月", "2012年08月", "2012年09月", "2012年10月", "2012年11月", "2012年12月" }; //
		} else {
			return new String[] { "2012年01月" }; //
		}
	}
}
