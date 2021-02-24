package cn.com.infostrategy.bs.report;

import java.util.HashMap;
import java.util.Random;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/*
 * 我的测试报表数据生成器..
 */
public class DemoReportBuilder extends MultiLevelReportDataBuilderAdapter {

	private static HashVO[] cacheDatas = null; //为了保证每次出来的结果都是一样的!所以搞一个静态变量作为缓存!即只在第一次执行时构造数据,其余的直接从缓存中取!

	String[] str_citys = new String[] { "上海分行@华东区", "南京分行@华东区", "苏州分行@华东区", "杭州分行@华东区", "北京分行@华北区", "天津分行@华北区", "呼和浩特分行@华北区", "乌鲁木齐分行@西北区", "西宁分行@西北区", "重庆分行@西北区", "广州分行@华南区", "南宁分行@华南区", "深圳分行@华南区", "大连分行@东北区", "长春分行@东北区", "沈阳分行@东北区", "哈尔滨分行@东北区" }; //城市
	String[] str_busitypes = new String[] { "中间业务", "个人业务", "信用卡业务", "国际业务" }; //业务类型!,
	String[] str_findchannels = new String[] { "自我检查", "上级合规检查", "内部审计", "外部审计", "监管发现", "媒体曝光" }; //发现渠道!媒体曝光
	String[] str_risklevels = new String[] { "极大风险", "高风险", "中等风险", "低风险", "极小风险" }; //
	String[] str_ctrltypes = new String[] { "控制不足", "控制基本有效", "控制有效", "控制过度" }; //
	String[] str_reason = new String[] { "制度不清;没有制度", "执行不力;没人管", "人员素质不够" }; //
	String[] str_satus = new String[] { "已审核", "未审核" }; //
	private Random random = new Random(); //随机数!!

	/**
	 * 创建数据..
	 */
	public HashVO[] buildReportData(HashMap _map) throws Exception {
		String str_sql = (String) _map.get("$SQL"); //查询条件,李国利提出,每次在后台拼接SQL太费事了,直接将前台拼接好了的SQL放在这个变量中传过来，后台拿过来直接加在【select * from 表名 where 1=1】+【str_sql】 该有多爽??但要注意字段名是否匹配,如果不匹配,还要进行字符串替换!!所以加上这个【xch/2012-06-20】
		String str_sql_1 = (String) _map.get("$SQL_1"); //将权限策略的条件加上,进行过滤..
		//System.out.println("SQL:[" + str_sql + "]"); //
		//System.out.println("SQL_1:[" + str_sql_1 + "]"); //
		if (cacheDatas != null) {
			return cacheDatas; //
		}

		ReportUtil reportUtil = new ReportUtil(); //
		HashVO[] hvs = new HashVO[3000]; //总共几条记录
		for (int i = 0; i < hvs.length; i++) { //遍历所有记录!!
			hvs[i] = new HashVO(); //创建对象!!
			hvs[i].setAttributeValue("id", "" + i); //

			String str_city = getRanDomValue(i, str_citys); //
			String str_fh = str_city.substring(0, str_city.indexOf("@")); //分行!
			String str_area = str_city.substring(str_city.indexOf("@") + 1, str_city.length()); //区域!

			hvs[i].setAttributeValue("区域", str_area); //
			hvs[i].setAttributeValue("分行", str_fh); //

			hvs[i].setAttributeValue("业务类型", getRanDomValue(i, str_busitypes)); //业务类型!
			hvs[i].setAttributeValue("发现渠道", getRanDomValue(i, str_findchannels)); //发现渠道!
			hvs[i].setAttributeValue("风险等级", getRanDomValue(i, str_risklevels)); //风险等级
			hvs[i].setAttributeValue("控制执行效果", getRanDomValue(i, str_ctrltypes)); //控制执行效果
			hvs[i].setAttributeValue("原因分析", getRanDomValue(i, str_reason)); //事件原因!
			hvs[i].setAttributeValue("审批状态", getRanDomValue(i, str_satus)); //审批状态!

			String str_month = "" + (100 + (random.nextInt(12) + 1)); //1,12
			str_month = str_month.substring(1, str_month.length()); //去掉第一位!!

			String str_date = "" + (100 + (random.nextInt(28) + 1)); //
			str_date = str_date.substring(1, str_date.length()); //去掉第一位!!
			hvs[i].setAttributeValue("发现时间", (i % 3 == 0 ? "2011" : "2012") + "-" + str_month + "-" + str_date); //时间,为了造同比效果,必须跨两年1

			hvs[i].setAttributeValue("风险事件", "1"); //
			hvs[i].setAttributeValue("损失金额", random.nextInt(100) + 10); //
			hvs[i].setAttributeValue("挽回损失", random.nextInt(50) + 10); //
		}

		reportUtil.leftOuterJoin_YSMDFromDateTime(hvs, "发现时间(月份)", "发现时间", "月"); //折算成月份!
		reportUtil.leftOuterJoin_YSMDFromDateTime(hvs, "发现时间(季度)", "发现时间", "季"); //折算成季度!

		cacheDatas = hvs; //
		return cacheDatas; //
	}

	private String getRanDomValue(int _index, String[] _arrays) {
		if (_index % 15 == 0) {
			return _arrays[0]; //
		} else if (_index % 20 == 0) {
			return _arrays[_arrays.length - 1]; //
		} else {
			return _arrays[random.nextInt(_arrays.length)]; //
		}
	}

	//需要分组统计的维度!!
	public String[] getGroupFieldNames() {
		return new String[] { "区域", "分行", "发现时间(季度)", "发现时间(月份)", "业务类型", "发现渠道", "风险等级", "控制执行效果", "原因分析", "审批状态" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "风险事件", "损失金额", "挽回损失" };
	}

	@Override
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		BeforeHandGroupTypeVO[] bhGroupVOs = new BeforeHandGroupTypeVO[8]; //

		bhGroupVOs[0] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[0].setName("1.发现时间(季度)-区域"); //
		bhGroupVOs[0].setRowHeaderGroupFields(new String[] {}); //
		bhGroupVOs[0].setColHeaderGroupFields(new String[] { "区域", "发现时间(季度)" }); //
		bhGroupVOs[0].setComputeGroupFields(new String[][] { { "风险事件", "count", "警界规则={总平均数}*2,{总平均数}*1.5,{总平均数}" }, { "风险事件-环比", "CountChainIncrease", "警界规则=100" }, { "风险事件-连续增长", "CountChainSeries", "警界规则=3,2,1" } }); // "是否带百分号=Y"是否警界=Y;警界规则=250
		bhGroupVOs[0].setTotal(false); //
		bhGroupVOs[0].setSortByCpValue(true);  //按数组排序
		
		HashMap filterMap = new HashMap();
		filterMap.put("发现时间(季度)", new String[] { TBUtil.getTBUtil().getCurrDateSeason() }); //
		//bhGroupVOs[0].setFilterGroupValueMap(filterMap);  //
		bhGroupVOs[0].setType("GRID"); //

		bhGroupVOs[1] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[1].setName("2.发现时间(月份)-分行"); //
		bhGroupVOs[1].setRowHeaderGroupFields(new String[] { "发现时间(月份)" }); //
		bhGroupVOs[1].setColHeaderGroupFields(new String[] { "分行" }); //
		bhGroupVOs[1].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[1].setType("GRID"); //

		bhGroupVOs[2] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[2].setName("3.业务类型-分行"); //
		bhGroupVOs[2].setRowHeaderGroupFields(new String[] { "业务类型" }); //
		bhGroupVOs[2].setColHeaderGroupFields(new String[] { "分行" }); //
		bhGroupVOs[2].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[2].setSortByCpValue(true); //按金额排序!!!
		bhGroupVOs[2].setType("GRID"); //

		//这是有维度公式的！！！
		bhGroupVOs[3] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[3].setName("4.风险等级-控制执行效果"); //
		bhGroupVOs[3].setRowHeaderGroupFields(new String[] { "风险等级" }); //
		bhGroupVOs[3].setColHeaderGroupFields(new String[] { "控制执行效果" }); //
		HashMap forMulamap = new HashMap(); //
		forMulamap.put("风险等级", new String[][] { { "高与大风险之和", "{高风险}+{极大风险}", "是否带百分号=N" }, { "高与低的比率", "({高风险}*100)/{极小风险}", "是否带百分号=Y" } }); //
		bhGroupVOs[3].setRowHeaderFormulaGroupMap(forMulamap); //风险等级后面可以加两个公式组!
		bhGroupVOs[3].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[3].setType("GRID"); //

		//这是有计算列公式的！！！
		bhGroupVOs[4] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[4].setName("5.风险等级-发现时间(季度)"); //
		bhGroupVOs[4].setRowHeaderGroupFields(new String[] { "风险等级" }); //
		bhGroupVOs[4].setColHeaderGroupFields(new String[] { "发现时间(季度)" }); //
		bhGroupVOs[4].setComputeGroupFields(new String[][] { { "损失金额", "sum" }, { "挽回损失", "sum" }, { "两者比例", "FormulaCompute", "公式=(({T1}-{T2})*100)/{T1};是否带百分号=Y" } }); //
		bhGroupVOs[4].setType("GRID"); //

		bhGroupVOs[5] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[5].setName("6.区域-分行-业务类型"); //
		bhGroupVOs[5].setRowHeaderGroupFields(new String[] { "业务类型" }); //
		bhGroupVOs[5].setColHeaderGroupFields(new String[] { "区域", "分行" }); //
		bhGroupVOs[5].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[5].setType("GRID"); //

		bhGroupVOs[6] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[6].setName("7.风险等级-分行-控制执行效果"); //
		bhGroupVOs[6].setRowHeaderGroupFields(new String[] { "风险等级" }); //
		bhGroupVOs[6].setColHeaderGroupFields(new String[] { "分行", "控制执行效果" }); //
		bhGroupVOs[6].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[6].setType("GRID"); //

		bhGroupVOs[7] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[7].setName("8.发现时间(季度)-发现时间(月份)-区域-风险等级"); //
		bhGroupVOs[7].setRowHeaderGroupFields(new String[] { "发现时间(季度)", "发现时间(月份)" }); //
		bhGroupVOs[7].setColHeaderGroupFields(new String[] { "区域", "风险等级" }); //
		bhGroupVOs[7].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[7].setType("GRID"); //

		return bhGroupVOs;
	}

	@Override
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		BeforeHandGroupTypeVO[] bhGroupVOs = new BeforeHandGroupTypeVO[5]; //
		bhGroupVOs[0] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[0].setName("分行-发现时间(季度)"); //
		bhGroupVOs[0].setRowHeaderGroupFields(new String[] { "分行" }); //
		bhGroupVOs[0].setColHeaderGroupFields(new String[] { "发现时间(季度)" }); //
		bhGroupVOs[0].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[0].setType("CHART"); //

		bhGroupVOs[1] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[1].setName("业务类型-发现渠道"); //
		bhGroupVOs[1].setRowHeaderGroupFields(new String[] { "业务类型" }); //
		bhGroupVOs[1].setColHeaderGroupFields(new String[] { "发现渠道" }); //
		bhGroupVOs[1].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[1].setType("CHART"); //

		bhGroupVOs[2] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[2].setName("风险等级-控制执行效果"); //
		bhGroupVOs[2].setRowHeaderGroupFields(new String[] { "风险等级" }); //
		bhGroupVOs[2].setColHeaderGroupFields(new String[] { "控制执行效果" }); //
		bhGroupVOs[2].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[2].setType("CHART"); //

		bhGroupVOs[3] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[3].setName("风险等级-发现时间(季度)"); //
		bhGroupVOs[3].setRowHeaderGroupFields(new String[] { "风险等级" }); //
		bhGroupVOs[3].setColHeaderGroupFields(new String[] { "发现时间(季度)" }); //
		bhGroupVOs[3].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[3].setType("CHART"); //

		bhGroupVOs[4] = new BeforeHandGroupTypeVO(); //
		bhGroupVOs[4].setName("业务类型-发现时间(月份)"); //
		bhGroupVOs[4].setRowHeaderGroupFields(new String[] { "业务类型" }); //
		bhGroupVOs[4].setColHeaderGroupFields(new String[] { "发现时间(月份)" }); //
		bhGroupVOs[4].setComputeGroupFields(new String[][] { { "风险事件", "count" } }); //
		bhGroupVOs[4].setType("CHART"); //

		return bhGroupVOs;
	}

	@Override
	public HashMap getDrilGroupBind() {
		HashMap map = new HashMap(); //
		map.put("区域", "分行"); //
		map.put("发现时间(季度)", "发现时间(月份)"); //
		return map;
	}

	@Override
	/**
	 * 钻取明细时的模板编码!!
	 */
	public String getDrillTempletCode() throws Exception {
		return null; //
	}

	/**
	 * 排序条件!!!
	 */
	@Override
	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap(); //
		map.put("风险等级", new String[] { "极大风险", "高风险", "中等风险", "低风险", "极小风险" }); //
		map.put("业务类型", new String[] { "个人业务", "中间业务", "国际业务" }); //
		map.put("区域", new String[] { "华东区", "华南区", "华北区", "东北区", "西北区" }); //
		map.put("原因分析", str_reason); //
		return map;
	}

	@Override
	public HashMap getZeroReportConfMap() {
		HashMap map = new HashMap(); //
		map.put("风险等级", new String[] { "极大风险", "高风险", "中等风险", "低风险", "极小风险", "其他风险" }); //
		return map; //
	}

	@Override
	public HashMap getDateGroupDefineMap() {
		HashMap map = new HashMap(); //
		map.put("发现时间(季度)", "季度"); //
		map.put("发现时间(月份)", "月度"); //
		return map; //
	}

}
