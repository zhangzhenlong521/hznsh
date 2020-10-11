/**************************************************************************
 * $RCSfile: GetColValue2.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;

/**
 * 根据ID从另一个表中取Name,它与GetColValue()的区别在于最一个参数是字段,而不是实际值!!
 * 每创建一个JepFormulaParseAtBS就会创建本对象一个实例!!!
 * @author xch
 *
 */
public class GetColValue2 extends PostfixMathCommand {

	private int li_type = -1;

	private HashMap cacheMap_keyValues = new HashMap(); //为了提高性能,用来做缓存的Map,关键之关键!!!
	private HashMap cacheMap_sqlDatas = new HashMap(); //为了提高性能,用来做缓存的Map,关键之关键!!!
	private HashMap cacheMap_sqlValues = new HashMap(); 
	//BS传入的参数
	private HashVO[] allDataHVS = null; //所有数据
	private HashMap colDataTypeMap = null; //一行数据!!
	private HashMap rowDataMap = null; //一行数据!!

	private CommDMO commDMO = null; //
	private TBUtil tBUtil = null; //

	public GetColValue2(int _type) {
		numberOfParameters = 4;
		li_type = _type;
	}

	public GetColValue2(int _type, HashVO[] _allDatas, HashMap _rowMap) {
		numberOfParameters = 4;
		li_type = _type;
		this.allDataHVS = _allDatas; //
		this.rowDataMap = _rowMap; //
	}

	public void run(Stack inStack) throws ParseException {
		try {
			//checkStack(inStack);
			Object param_1 = inStack.pop();
			Object param_2 = inStack.pop();
			Object param_3 = inStack.pop();
			Object param_4 = inStack.pop();

			String str_table_name = (String) param_4; //表名，比如"pub_user"
			String str_returnfieldname = (String) param_3; //想要取数的名称,比如"name"
			String str_wherefieldname = (String) param_2; //where条件的字段名,比如"id"
			String str_wherefieldEqualsLoadFieldName = (String) param_1; //另一个表中字段等同于加载的主表的哪个字段?? 比如"createuser"

			if (li_type == WLTConstants.JEPTYPE_UI) { //如果是UI端!!
				//				String str_sql_1 = "select " + str_returnfieldname + " from " + str_table_name + " where " + str_wherefieldname + "='" + str_wherefieldvalue + "'"; //
				//				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql_1); //
				//				if (hvs.length > 0) { //如果取到数据
				//					inStack.push(hvs[0].getStringValue(0)); //
				//				} else {
				//					inStack.push(""); //
				//				}
			} else { //如果是BS端的,则先要构建缓存!!即一次性取出来!!下次从缓存中取,这样会提高效率许多!!!
				if (!cacheMap_keyValues.containsKey(str_wherefieldEqualsLoadFieldName)) { //如果没包含某一列,则先将某一列所有值缓存,以防多个公式都需要取同一个列的值,比如有两个getColValue2都要取createuser的值!!!!
					StringBuffer sb_allPTFieldValues = new StringBuffer(); //
					if (allDataHVS != null && allDataHVS.length > 0) {
						String str_itemValue = null; //
						ArrayList al_allValues = new ArrayList(); //
						HashSet hs_distinct = new HashSet(); //用来做唯一性过滤!!因为可能主表中有大量记录是一样的,比如机构都是总行广州分行,这时拼查询子表的SQL就可以做一个唯一性过滤,大大提高性能!!以后还可以考虑同一张表,
						//根据同一个字段取同一个另外的字段,可以两者共享对方的数据!比如有两个与人员相关的字段,如果第二个字段的值的有些数据已在第一个中存在过了，则第二个可以只查询部分数据!即共享第一部分的数据!! 这个功能以后再优化吧!!
						for (int i = 0; i < allDataHVS.length; i++) {
							str_itemValue = allDataHVS[i].getStringValue(str_wherefieldEqualsLoadFieldName); //
							if (str_itemValue != null && !str_itemValue.trim().equals("") && !str_itemValue.trim().equals("null") && !hs_distinct.contains(str_itemValue)) { //如果不为空
								al_allValues.add(str_itemValue); //
								hs_distinct.add(str_itemValue); //
							}
						}
						if (al_allValues.size() > 0) {
							cacheMap_keyValues.put(str_wherefieldEqualsLoadFieldName, (String[]) al_allValues.toArray(new String[0])); //
						} else {
							cacheMap_keyValues.put(str_wherefieldEqualsLoadFieldName, null); //
						}
					}
				} else { //如果已包含了则不取了

				}

				if (cacheMap_keyValues.get(str_wherefieldEqualsLoadFieldName) != null) { //如果有数据 null强制类型转换为"null"有问题所以先判断一下
					String str_inSQLCons = null;
					if(cacheMap_sqlValues.containsKey(str_wherefieldEqualsLoadFieldName)){
						str_inSQLCons = cacheMap_sqlValues.get(str_wherefieldEqualsLoadFieldName).toString();
					}else{
						String[] str_values = (String[]) cacheMap_keyValues.get(str_wherefieldEqualsLoadFieldName); //
						str_inSQLCons = getCommDMO().getSubSQLFromTempSQLTableByIDs(str_values); //
						cacheMap_sqlValues.put(str_wherefieldEqualsLoadFieldName, str_inSQLCons);
					}
					String str_sql_1 = "select " + str_wherefieldname + "," + str_returnfieldname + " from " + str_table_name + " where " + str_wherefieldname + " in (" + str_inSQLCons + ")"; //
					if (!cacheMap_sqlDatas.containsKey(str_sql_1)) { //如果没取过数，则取一下,否则直接从缓存中取,换句话说就是只有第一次取一下，第二次都是从缓存中取
						HashMap sqlMapData = getCommDMO().getHashMapBySQLByDS(null, str_sql_1, true); //将值拼成一个字符串!!
						cacheMap_sqlDatas.put(str_sql_1, sqlMapData); //
					}

					HashMap dataMap = (HashMap) cacheMap_sqlDatas.get(str_sql_1); //从缓存中取得数据!
					String str_thisRowItemValue = getThisRowRealData(str_wherefieldEqualsLoadFieldName); //本行的实际数据,
					if (str_thisRowItemValue == null || str_thisRowItemValue.equals("")) {
						inStack.push("");
					} else {
						inStack.push((String) dataMap.get(str_thisRowItemValue)); ////去哈希表中取,即根据当前行ID值去取得Name的值!!
					}
				} else {
					inStack.push(""); //如果都没数据,则直接返回空了,即每条记录的createuser都是空值,那取name也必须为空!
				}
			}
		} catch (Throwable ex) {
			TBUtil.printStackTrace(ex); //
			inStack.push("");
		}
	}

	private String getThisRowRealData(String _itemKey) {
		Object obj = rowDataMap.get(_itemKey); //
		if (obj == null) {
			return null;
		}
		String str_value = null;
		if (obj instanceof String) {
			str_value = (String) obj; //
		} else if (obj instanceof StringItemVO) {
			StringItemVO itemVO = (StringItemVO) obj; //
			str_value = itemVO.getStringValue(); //
		} else if (obj instanceof ComBoxItemVO) {
			ComBoxItemVO itemVO = (ComBoxItemVO) obj; //
			str_value = itemVO.getId(); //
		} else if (obj instanceof RefItemVO) {
			RefItemVO itemVO = (RefItemVO) obj; //
			str_value = itemVO.getId(); //
		} else {
			str_value = obj.toString(); //
		}

		return str_value; //
	}

	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil;
	}

	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}
}
/**************************************************************************
 * $RCSfile: GetColValue2.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: GetColValue2.java,v $
 * Revision 1.8  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:54  Administrator
 * *** empty log message ***
 *
 * Revision 1.7  2012/02/09 13:13:51  sunfujun
 * *** empty log message ***
 *
 * Revision 1.6  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.4  2010/10/29 05:21:03  xch123
 * *** empty log message ***
 *
 * Revision 1.3  2010/08/23 09:32:32  wanglei
 * *** empty log message ***
 *
 * Revision 1.2  2010/08/11 08:44:00  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/03/11 12:32:06  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2010/03/11 12:19:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/03/08 15:04:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:53  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/10/14 08:05:41  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:29  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/02/09 09:41:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:09  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.6  2008/06/12 01:46:25  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2008/05/13 14:49:59  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2008/05/13 13:30:52  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/04/11 01:44:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/03/02 07:01:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:23  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/11/13 05:57:58  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:33  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:27  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/07 02:25:01  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/07 02:04:18  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/02 05:02:50  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:59:24  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
