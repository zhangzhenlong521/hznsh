package cn.com.infostrategy.to.mdata;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.SQLBuilderIfc;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 构造Update SQL的工具类
 * 
 * @author xch
 * 
 */
public class UpdateSQLBuilder implements SQLBuilderIfc {

	private String tableName = null; // 表名
	private String whereCondition = null; // where条件

	private VectorMap vm_value = new VectorMap(); // 存储所有字段值的集合类

	private TBUtil tbUtil = null; //

	public UpdateSQLBuilder() {
	}

	public UpdateSQLBuilder(String _tableName) {
		this.tableName = _tableName; //
	}

	public UpdateSQLBuilder(String _tableName, String _whereCondition) {
		this.tableName = _tableName; //
		this.whereCondition = _whereCondition; //
	}

	/**
	 * 在构造方法中直接送入数据，这样就可以通过一行代码生成SQL,比如 new
	 * UpdateSQLBuilder("pub_user","id=100",new
	 * String[][]{{"code","xch"},{"name","徐长华"}}).getSQL(); //
	 * 
	 * @param _tableName
	 * @param _whereCondition
	 * @param _data,初始数据,必须是n行,两列的样子!
	 */
	public UpdateSQLBuilder(String _tableName, String _whereCondition, String[][] _data) {
		this.tableName = _tableName; //
		this.whereCondition = _whereCondition; //
		if (_data != null) {
			for (int i = 0; i < _data.length; i++) {
				vm_value.put(_data[i][0], _data[i][1]); //
			}
		}
	}

	/**
	 * 塞入值
	 * 
	 * @param _fieldName
	 * @param _value
	 */
	public void putFieldValue(String _fieldName, String _value) {
		vm_value.put(_fieldName, _value); //
	}

	/**
	 * 塞入值,如果值为null,则用nvl代替
	 * 
	 * @param _fieldName
	 * @param _value
	 * @param _nvl
	 *            当_value为null时用该值代替!!!
	 */
	public void putFieldValue(String _fieldName, String _value, String _nvl) {
		if (_value == null && _nvl != null) {
			vm_value.put(_fieldName, _nvl); //
		} else {
			vm_value.put(_fieldName, _value); //
		}
	}

	public void putFieldValue(String _fieldName, int _value) {
		vm_value.put(_fieldName, new Integer(_value)); //
	}

	public void putFieldValue(String _fieldName, Integer _value) {
		vm_value.put(_fieldName, _value); //
	}

	public void putFieldValue(String _fieldName, double _value) {
		vm_value.put(_fieldName, new Double(_value)); //
	}

	public void putFieldValue(String _fieldName, Double _value) {
		vm_value.put(_fieldName, _value); //
	}

	public void putFieldValue(String _fieldName, long _value) {
		vm_value.put(_fieldName, new Long(_value)); //
	}

	public void putFieldValue(String _fieldName, Long _value) {
		vm_value.put(_fieldName, _value); //
	}

	public String getSQL() {
		return getSQL(true); //
	}

	/**
	 * 拼成SQL
	 * 
	 * @return
	 */
	public String getSQL(boolean _isconvert) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("update " + getTableName() + " set "); //
		String[] str_keys = vm_value.getKeysAsString(); //
		String zfj = getTBUtil().getSysOptionStringValue("数据库字符集", "GBK");//以前只判断GBK，如果是UTF-8，一个汉字占三个字节，故校验可能通过，但保存数据库报错，故设置该参数【李春娟/2016-04-26】
		for (int i = 0; i < str_keys.length; i++) {
			sb_sql.append(str_keys[i] + "="); // 先把字段加上.
			Object obj = vm_value.get(str_keys[i]); //
			if (obj == null) {
				sb_sql.append("null"); //
			} else {
				if (obj instanceof String) {
					String str_value = (String) obj; //
					if (str_value.trim().equals("null") || str_value.trim().equals("")) {
						sb_sql.append("null"); //
					} else {
						if (_isconvert) {
							str_value = convertSQLValue(str_value); //
						}
						try {
							if (str_value.getBytes(zfj).length > 4000) { //如果超过4000
								str_value = getTBUtil().replaceAll(str_value, "''", "~"); //
								str_value = getTBUtil().replaceAll(str_value, "'", "~"); //
								str_value = getTBUtil().replaceAll(str_value, "\\\\", "!"); //
								str_value = getTBUtil().replaceAll(str_value, "\\", "!"); //
								str_value = updateClobValue(str_value, getTableName(), str_keys[i], getWhereCondition());
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						sb_sql.append("'" + str_value + "'"); //
					}
				} else if (obj instanceof Integer) {
					Integer li_value = (Integer) obj; //
					sb_sql.append("'" + li_value + "'"); //
				} else if (obj instanceof Double) {
					Double ld_value = (Double) obj; //
					sb_sql.append("'" + ld_value + "'"); //
				} else if (obj instanceof Long) { // 长整形
					Long ll_value = (Long) obj; //
					sb_sql.append("'" + ll_value + "'"); //
				} else {
					sb_sql.append("'" + obj.toString() + "'"); //
				}
			}

			if (i != str_keys.length - 1) { // 如果不是最后一个..
				sb_sql.append(","); //
			}
		}

		if (getWhereCondition() != null && !"".equals(getWhereCondition())) {
			sb_sql.append(" where " + getWhereCondition()); //	
		}

		return sb_sql.toString(); //
	}

	/**
	 * 替换SQL中的单引号,因为单引号会导致保存失败!!
	 * 
	 * @param _value
	 * @return
	 */
	private String convertSQLValue(String _value) {
		if (_value == null) {
			return "null";
		} else {
			_value = getTBUtil().replaceAll(_value, "'", "''"); //单引号要替换!!!
			if (getTBUtil().getJVSite() == 1) { //客户端
				if ("MYSQL".equalsIgnoreCase(ClientEnvironment.getInstance().getDefaultDataSourceType())) { //如果是Mysql才会替换!!! 直接拿默认数据源比可能会有问题,因为可能是其他数据源!!! 以后再优化!!!
					_value = getTBUtil().replaceAll(_value, "\\", "\\\\"); //只有Mysql才需要替换,Oracle与SQLServer都不要替换\
				}
			} else {
				if ("MYSQL".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) { //如果是Mysql才会替换!!! 直接拿默认数据源比可能会有问题,因为可能是其他数据源!!! 以后再优化!!!
					_value = getTBUtil().replaceAll(_value, "\\", "\\\\"); //只有Mysql才需要替换,Oracle与SQLServer都不要替换\
				}
			}
			return _value; //
		}
	}

	/**
	 * 如果是大字段则进行处理
	 * @param _value
	 * @return
	 */
	private String updateClobValue(String _value, String tablename, String columnname, String whereCondition) {
		try {
			HashMap map = new HashMap();
			map.put("value", _value);
			map.put("tablename", tablename);
			map.put("columnname", columnname);
			map.put("whereCondition", whereCondition);
			HashMap rtn = null;
			if (getTBUtil().getJVSite() == 1) { //客户端
				rtn = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.common.ClobUtil", "updateClob", map);
			} else {
				Class objclass = Class.forName("cn.com.infostrategy.bs.common.ClobUtil");
				Method objmethod = objclass.getMethod("updateClob", new Class[] { HashMap.class });
				Object objInstance = objclass.newInstance(); //
				Object returnObj = objmethod.invoke(objInstance, new Object[] { map });
				rtn = (HashMap) returnObj;
			}
			return (String) rtn.get("value");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _value;
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil();
		return tbUtil; //
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getWhereCondition() {
		return whereCondition;
	}

	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}

	/**
	 * 重构ToString()方法,返回SQL
	 */
	@Override
	public String toString() {
		return getSQL(); //
	}

	// public static void main(String[] _args) {
	// System.out.println(new UpdateSQLBuilder("pub_user", "id=100", new
	// String[][] { { "code", "xch" }, { "name", "徐长华" } }).getSQL()); //
	// }

}
