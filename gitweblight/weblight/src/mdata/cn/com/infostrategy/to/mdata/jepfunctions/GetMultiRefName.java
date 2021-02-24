package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 *将一个多选参照中的id转换成名称,比如数据库中存储的是【15;17;19】,我要转换成【张三;李四;王五】,为了减少表(少一个关联表)，我们许多地方采用多选参照,出报表时也是在Java中计算，而不是使用SQL计算！所有这种应用很有价值
 *其实除了多选参照外,有时通过加载公式将另外一张子表的所以与之关联的所有数据拉出来,把各行记录的某个字段拼成一个以分号隔开的字符串也是经常使用的一个应用!!
 *比如一张人员表,现在我加一个字段叫"该人员的角色",然后自动加载将这个人的所有角色取出来拼成一个字符串,比如:系统管理员;法律岗;合规岗;等!其实就是相当于引用子表，只不过将其拉出来拼与一条！这种应用只适用于列表/卡片查询,是否可以直接封装成另一种引用子表格式(点击弹出式)是值的商榷的！！
 * 
 */
public class GetMultiRefName extends PostfixMathCommand {

	private int callType = -1; //
	private BillPanel billPanel = null;

	//BS传入的参数
	private HashMap colDataTypeMap = null; //一行数据!!
	private HashMap rowDataMap = null; //一行数据!!

	/*-----------李燕杰添加开始-------------*/
	private HashMap cacheMap = new HashMap();

	/*-----------李燕杰添加结束-------------*/

	private cn.com.infostrategy.bs.common.CommDMO commDMO = null; //
	private TBUtil tbUtil = null; //

	public GetMultiRefName() {
		numberOfParameters = 4; //
	}

	/**
	 * 取得某一项的值!!
	 */
	public GetMultiRefName(BillPanel _billPanel) {
		numberOfParameters = 4; //4个参数,第一个是表名,第二个是要查询的列名,第三个是关联的列名,第四个是传入的关联列名的值
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 在服务器端调用的构造方法
	 * @param dataMap
	 */
	public GetMultiRefName(HashMap _dataMap) {
		numberOfParameters = 4; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		//先取得参数!!
		try {
			checkStack(inStack);
			Object param_1 = inStack.pop();
			Object param_2 = inStack.pop();
			Object param_3 = inStack.pop();
			Object param_4 = inStack.pop();
			String str_tablename = (String) param_4; ////
			String str_selectlistname = (String) param_3; ////
			String str_unionlistname = (String) param_2; ////关联字段,一般是子表的主键,比如ID
			String str_unionlistvalue = (String) param_1; ////本表的本条记录的值!!(如果是字段值就好了!)

			if (str_unionlistvalue == null || str_unionlistvalue.trim().equals("")) { //如果本表的该字段的值为空,则不需要计算直接返回
				inStack.push(""); //
				return;
			}

			String str_inCon = getTBUtil().getInCondition(str_unionlistvalue); //
			StringBuilder sb_return = new StringBuilder(); //
			HashMap map_id_name = null;
			if (callType == WLTConstants.JEPTYPE_UI) { //如果是UI端则很好办
				String str_sql = "select " + str_unionlistname + "," + str_selectlistname + " from " + str_tablename + " where " + str_unionlistname + " in (" + str_inCon + ")"; //
				map_id_name = UIUtil.getHashMapBySQLByDS(null, str_sql); //

			} else if (callType == WLTConstants.JEPTYPE_BS) { //如果是BS端,则做缓存,即一下子将所有子表的数据都取出来,然后在中间件中取!!这样会保证只访问一次数据库,但数据量大时也较慢!
				if (str_tablename.equalsIgnoreCase("pub_corp_dept")) {//如果机构,从缓存取!!
					HashVO[] hvs_corpCache = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //处到机构缓存
					map_id_name = getTBUtil().getHashMapFromHashVOs(hvs_corpCache, str_unionlistname, str_selectlistname); //将id,name抽出来作为一个哈希表,以便查询快速!!
				} else {
					String str_sql = "select " + str_unionlistname + "," + str_selectlistname + " from " + str_tablename + " where " + str_unionlistname + " in (" + str_inCon + ")"; //SQL 这里查处所有的应该加个条件和UI端一样 信息中心就几条数据就很慢就是这里的原因/sunfujun/20121122
					map_id_name = getCacheMapData(str_sql); //
				}
			}
			if (map_id_name == null || map_id_name.size() == 0) {
				inStack.push(""); //
				return;
			}
			//我需要id值和名称一一对应，sql不好控制，所以这里处理一下【李春娟/2016-03-21】
			String[] str_ids = getTBUtil().split(str_unionlistvalue, ";");
			for (int i = 0; i < str_ids.length; i++) {
				String str_name = (String) map_id_name.get(str_ids[i]); //
				str_name = (str_name == null ? "" : str_name); ////
				sb_return.append(str_name + ";"); //
			}
			inStack.push(sb_return.toString()); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			inStack.push(""); //
		}
	}

	private cn.com.infostrategy.bs.common.CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new cn.com.infostrategy.bs.common.CommDMO();
		}
		return commDMO;
	}

	private HashMap getCacheMapData(String _sql) throws Exception {
		if (cacheMap.containsKey(_sql)) {
			return (HashMap) cacheMap.get(_sql); //
		} else {
			HashMap mapData = getCommDMO().getHashMapBySQLByDS(null, _sql); //
			cacheMap.put(_sql, mapData); //
			return mapData;
		}
	}

	private TBUtil getTBUtil() {
		if (tbUtil == null) {
			tbUtil = new TBUtil();
		}
		return tbUtil;
	}

	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}

}
