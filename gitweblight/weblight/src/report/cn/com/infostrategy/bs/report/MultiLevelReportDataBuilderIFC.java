package cn.com.infostrategy.bs.report;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;

/**
 * 多维报表数据生成器,非常有用!!以后大量报表多用这个!!
 * 该接口在服务器端,有三个方法,分别定义参与的维度与参与统计的字段,然后还有一个定义生成数据的方法
 * 生成数据的方法返回的是一个HashVO[],该数组是明细数据,传入的是一个HashMap,这个HashMap是从前台传入的,一般是通过一个查询框来实现的!!!
 * 理论是讲,所有的统计报表,前台都可以用一个类解决!!!然后通过一个远程方法,先取得维度定义,生成对话框,让用户选择哪个维度,然后再输入查询条件,
 * 输入查询条件后平台再调用该类的实现数据方法,生成数据,然后再调用工具类,根据选择的维度进行分组统计,生成结果返回,该结果应该只有维度列与统计列!!
 * 关键是该接口实现类只负责生成数据明细列,而不要进行任何汇总处理..所以对开发人员要求很简单.
 * 前台通过定义一个模板编码加一个该接口实现类的名称,就可以了,然后剩下的只是处理该接口实现类的生成数据方法!
 * 生成数据时还需要弄一个LeftOuterJoin
 * @author xch
 *
 */
public interface MultiLevelReportDataBuilderIFC {

	/**
	 * 构造报表数据...
	 * _queryConsMap中有两个特殊的key,即【$SQL】【$SQL_1】,分别是前台拼接好的SQL,其中SQL_1是将权限策略也计算好了! 但要注意查询字段名是否要进行替换?即可能一个查询模板可能被多个地方共用，而不同地方的字段名不一样？？
	 * 李国利提出,每次在后台拼接SQL太费事了,直接将前台拼接好了的SQL放在这个变量中传过来，后台拿过来直接加在【select * from 表名 where 1=1】+【$SQL】 该有多爽??但要注意字段名是否匹配,如果不匹配,还要进行字符串替换!!所以加上这个【xch/2012-06-20】
	 * @param _queryCondition
	 * @return
	 * @throws Exception
	 */
	public HashVO[] buildReportData(HashMap _queryCondition) throws Exception; //

	/**
	 * 定义可以参与分组统计的列
	 * @return
	 */
	public String[] getGroupFieldNames(); //

	/**
	 * 定义可以参与汇总的列
	 * @return
	 */
	public String[] getSumFiledNames(); //

	/**
	 * 报表分组排序配置
	 * @return
	 * @throws Exception
	 */
	public HashMap getGroupFieldOrderConfig() throws Exception;

}
