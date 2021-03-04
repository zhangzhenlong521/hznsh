package cn.com.infostrategy.bs.mdata.dataaccessfuns;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.mdata.DataAccessFunctionIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 取得某张表的某个字段的值,经常用于根据子表的id取得子表的name
 * @author xch
 *
 */
public class GetColValueByItemId implements DataAccessFunctionIFC {

	private String str_tablename = null; //表名
	private String str_returnField = null; //返回的字段名
	private String str_idField = null; //id字段的值
	private String str_idFieldValue = null; //id字段的值
	private String str_whereCondition = null; //过滤条件

	private HashMap map_data = null; //

	public GetColValueByItemId() {

	}

	public String getFunValue(String[] _pars) {
		if (map_data == null) { //如果是第一次取数,则需要进行缓存处理
			map_data = new HashMap(); //

			str_tablename = _pars[0];
			str_returnField = _pars[1];
			str_idField = _pars[2];
			str_idFieldValue = _pars[3];
			str_whereCondition = _pars[4];

			String str_sql = "select " + str_idField + "," + str_returnField + " from " + str_tablename; //
			if (str_whereCondition != null && !str_whereCondition.trim().equals("")) {//如果条件不为空
				str_sql = str_sql + " where " + str_whereCondition;
			}
			
			try {
				HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql); //
				for (int i = 0; i < hvs.length; i++) {
					map_data.put(hvs[i].getStringValue(str_idField), hvs[i].getStringValue(str_returnField)); //作为键值注入
				}
			} catch (Exception e) {
			}
		}

		
		return null;
	}

}
