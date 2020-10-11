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
 * ����Update SQL�Ĺ�����
 * 
 * @author xch
 * 
 */
public class UpdateSQLBuilder implements SQLBuilderIfc {

	private String tableName = null; // ����
	private String whereCondition = null; // where����

	private VectorMap vm_value = new VectorMap(); // �洢�����ֶ�ֵ�ļ�����

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
	 * �ڹ��췽����ֱ���������ݣ������Ϳ���ͨ��һ�д�������SQL,���� new
	 * UpdateSQLBuilder("pub_user","id=100",new
	 * String[][]{{"code","xch"},{"name","�쳤��"}}).getSQL(); //
	 * 
	 * @param _tableName
	 * @param _whereCondition
	 * @param _data,��ʼ����,������n��,���е�����!
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
	 * ����ֵ
	 * 
	 * @param _fieldName
	 * @param _value
	 */
	public void putFieldValue(String _fieldName, String _value) {
		vm_value.put(_fieldName, _value); //
	}

	/**
	 * ����ֵ,���ֵΪnull,����nvl����
	 * 
	 * @param _fieldName
	 * @param _value
	 * @param _nvl
	 *            ��_valueΪnullʱ�ø�ֵ����!!!
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
	 * ƴ��SQL
	 * 
	 * @return
	 */
	public String getSQL(boolean _isconvert) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("update " + getTableName() + " set "); //
		String[] str_keys = vm_value.getKeysAsString(); //
		String zfj = getTBUtil().getSysOptionStringValue("���ݿ��ַ���", "GBK");//��ǰֻ�ж�GBK�������UTF-8��һ������ռ�����ֽڣ���У�����ͨ�������������ݿⱨ�������øò��������/2016-04-26��
		for (int i = 0; i < str_keys.length; i++) {
			sb_sql.append(str_keys[i] + "="); // �Ȱ��ֶμ���.
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
							if (str_value.getBytes(zfj).length > 4000) { //�������4000
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
				} else if (obj instanceof Long) { // ������
					Long ll_value = (Long) obj; //
					sb_sql.append("'" + ll_value + "'"); //
				} else {
					sb_sql.append("'" + obj.toString() + "'"); //
				}
			}

			if (i != str_keys.length - 1) { // ����������һ��..
				sb_sql.append(","); //
			}
		}

		if (getWhereCondition() != null && !"".equals(getWhereCondition())) {
			sb_sql.append(" where " + getWhereCondition()); //	
		}

		return sb_sql.toString(); //
	}

	/**
	 * �滻SQL�еĵ�����,��Ϊ�����Żᵼ�±���ʧ��!!
	 * 
	 * @param _value
	 * @return
	 */
	private String convertSQLValue(String _value) {
		if (_value == null) {
			return "null";
		} else {
			_value = getTBUtil().replaceAll(_value, "'", "''"); //������Ҫ�滻!!!
			if (getTBUtil().getJVSite() == 1) { //�ͻ���
				if ("MYSQL".equalsIgnoreCase(ClientEnvironment.getInstance().getDefaultDataSourceType())) { //�����Mysql�Ż��滻!!! ֱ����Ĭ������Դ�ȿ��ܻ�������,��Ϊ��������������Դ!!! �Ժ����Ż�!!!
					_value = getTBUtil().replaceAll(_value, "\\", "\\\\"); //ֻ��Mysql����Ҫ�滻,Oracle��SQLServer����Ҫ�滻\
				}
			} else {
				if ("MYSQL".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) { //�����Mysql�Ż��滻!!! ֱ����Ĭ������Դ�ȿ��ܻ�������,��Ϊ��������������Դ!!! �Ժ����Ż�!!!
					_value = getTBUtil().replaceAll(_value, "\\", "\\\\"); //ֻ��Mysql����Ҫ�滻,Oracle��SQLServer����Ҫ�滻\
				}
			}
			return _value; //
		}
	}

	/**
	 * ����Ǵ��ֶ�����д���
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
			if (getTBUtil().getJVSite() == 1) { //�ͻ���
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
	 * �ع�ToString()����,����SQL
	 */
	@Override
	public String toString() {
		return getSQL(); //
	}

	// public static void main(String[] _args) {
	// System.out.println(new UpdateSQLBuilder("pub_user", "id=100", new
	// String[][] { { "code", "xch" }, { "name", "�쳤��" } }).getSQL()); //
	// }

}
