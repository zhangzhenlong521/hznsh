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
 * 构造Insert SQL的工具类
 * 使用该工具生成SQL至少有以下几点好处：
 * 1.会少写一半行数的代码,生成的SQL语句的格式也会标准统一
 * 2.不易发生列与值错位的错误
 * 3.杜绝造成中间少逗号或最后一位少个逗号的错误(这个经常发生)
 * 4.自动处理null的问题,无需再为此费劲处理了,而且支持如果为null的默认值处理,即nvl的概念!!
 * 5.杜绝字符串中有单引号从而发生异常的情况(这个也很烦人),即数据中有单引号会保存不少,需要反义处理一下,即在前面在加一个单引号!!!
 * 6.可扩展性强,以后万一发生跨数据库,日期类型等特殊情况时只需修改这里一处即可!!!
 * 
 * 缺点是:不能一眼清的看到该SQL的实际内容,需要调用getSQL()才行,有点像实体bean,这也是当初没有早点创建该类的原因!现在看来还是需要这个类的!!
 * @author xch
 *
 */
public class InsertSQLBuilder implements SQLBuilderIfc {

	private String tableName = null; //表名
	private VectorMap vm_value = new VectorMap(); //存储所有字段值的集合类

	private TBUtil tbUtil = null; //

	public InsertSQLBuilder() {
	}

	public InsertSQLBuilder(String _tableName) {
		this.tableName = _tableName; //
	}

	/**
	 * 在构造方法中直接送入数据，这样就可以通过一行代码生成SQL,比如 new InsertSQLBuilder("pub_user",new String[][]{{"id","100"},{"code","xch"},{"name","徐长华"}}).getSQL();  //
	 * @param _tableName
	 * @param _data,初始数据,必须是n行,两列的样子!
	 */
	public InsertSQLBuilder(String _tableName, String[][] _data) {
		this.tableName = _tableName; //
		if (_data != null) {
			for (int i = 0; i < _data.length; i++) {
				vm_value.put(_data[i][0], _data[i][1]); //
			}
		}
	}

	/**
	 * 塞入值
	 * @param _fieldName
	 * @param _value
	 */
	public void putFieldValue(String _fieldName, String _value) {
		vm_value.put(_fieldName, _value); //
	}

	/**
	 * 塞入值,如果值为null,则用nvl代替
	 * @param _fieldName
	 * @param _value
	 * @param _nvl 当_value为null时用该值代替!!!
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
	 * @return
	 */
	public String getSQL(boolean _isConvert) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("insert into " + getTableName() + " ("); //
		String[] str_keys = vm_value.getKeysAsString(); //
		for (int i = 0; i < str_keys.length; i++) {
			sb_sql.append(str_keys[i]); //
			if (i != str_keys.length - 1) { //如果不是最后一个..
				sb_sql.append(","); //
			}
		}
		sb_sql.append(") values ("); //
		String zfj = getTBUtil().getSysOptionStringValue("数据库字符集", "GBK");//以前只判断GBK，如果是UTF-8，一个汉字占三个字节，故校验可能通过，但保存数据库报错，故设置该参数【李春娟/2016-04-26】
		for (int i = 0; i < str_keys.length; i++) {
			Object obj = vm_value.get(str_keys[i]); //
			if (obj == null) {
				sb_sql.append("null"); //
			} else {
				if (obj instanceof String) {
					String str_value = (String) obj; //
					if (str_value.trim().equals("null") || str_value.trim().equals("")) {
						sb_sql.append("null"); //
					} else {
						if (_isConvert) { //如果需要转换才转换!!
							str_value = convertSQLValue(str_value); //转换一把!!!
							try {
								if (str_value != null && str_value.getBytes(zfj) != null && str_value.getBytes(zfj).length > 4000) { //如果超过4000,则还截取一下!按道理要能处理中文的!
									//str_value = str_value.substring(0, 4000); //这样截取下来后可能会造成执行报错,因为正好最后的单引号或逗号对不上了!!!所以干脆直接将单引号替换算了,彻底保证不报错!!!
									str_value = getTBUtil().replaceAll(str_value, "''", "~"); //
									str_value = getTBUtil().replaceAll(str_value, "'", "~"); //
									str_value = getTBUtil().replaceAll(str_value, "\\\\", "!"); //
									str_value = getTBUtil().replaceAll(str_value, "\\", "!"); //
									str_value = saveClobValue(str_value);
								}
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						} else {
							try {
								//以前这里if中判断的str_itemvalue，而str_itemvalue 在前面从未赋值，故导致主键为'null'，故修改之【李春娟/2016-09-06】
								if (str_value.getBytes(zfj) != null && str_value.getBytes(zfj).length > 4000) {
									str_value = saveClobValue(str_value);
								}
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
						sb_sql.append("'" + str_value + "'"); //
					}
				} else if (obj instanceof Integer) {
					Integer li_value = (Integer) obj; //
					sb_sql.append("'" + li_value + "'"); //
				} else if (obj instanceof Double) {
					Double ld_value = (Double) obj; //
					sb_sql.append("'" + ld_value + "'"); //
				} else if (obj instanceof Long) { //长整形
					Long ll_value = (Long) obj; //
					sb_sql.append("'" + ll_value + "'"); //
				} else {
					sb_sql.append("'" + obj.toString() + "'"); //
				}
			}

			if (i != str_keys.length - 1) { //如果不是最后一个..
				sb_sql.append(","); //
			}
		}
		sb_sql.append(")"); //
		return sb_sql.toString(); //
	}

	/**
	 * 替换SQL中的单引号,因为单引号会导致保存失败!!
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
	private String saveClobValue(String _value) {
		try {
			HashMap map = new HashMap();
			map.put("value", _value);
			HashMap rtn = null;
			if (getTBUtil().getJVSite() == 1) { //客户端
				rtn = UIUtil.getCommonService().commMethod("cn.com.infostrategy.bs.common.ClobUtil", "saveClob", map);
			} else {
				Class objclass = Class.forName("cn.com.infostrategy.bs.common.ClobUtil");
				Method objmethod = objclass.getMethod("saveClob", new Class[] { HashMap.class });
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

	/**
	 * 重构ToString()方法,返回SQL
	 */
	@Override
	public String toString() {
		return getSQL(); //
	}

	public static void main(String[] _args) {
		System.out.println("pushclob:1111".substring(9)); //
	}
}
