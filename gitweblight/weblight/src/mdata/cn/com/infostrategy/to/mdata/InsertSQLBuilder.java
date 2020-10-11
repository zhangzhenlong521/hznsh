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
 * ����Insert SQL�Ĺ�����
 * ʹ�øù�������SQL���������¼���ô���
 * 1.����дһ�������Ĵ���,���ɵ�SQL���ĸ�ʽҲ���׼ͳһ
 * 2.���׷�������ֵ��λ�Ĵ���
 * 3.�ž�����м��ٶ��Ż����һλ�ٸ����ŵĴ���(�����������)
 * 4.�Զ�����null������,������Ϊ�˷Ѿ�������,����֧�����Ϊnull��Ĭ��ֵ����,��nvl�ĸ���!!
 * 5.�ž��ַ������е����ŴӶ������쳣�����(���Ҳ�ܷ���),���������е����Żᱣ�治��,��Ҫ���崦��һ��,����ǰ���ڼ�һ��������!!!
 * 6.����չ��ǿ,�Ժ���һ���������ݿ�,�������͵��������ʱֻ���޸�����һ������!!!
 * 
 * ȱ����:����һ����Ŀ�����SQL��ʵ������,��Ҫ����getSQL()����,�е���ʵ��bean,��Ҳ�ǵ���û����㴴�������ԭ��!���ڿ���������Ҫ������!!
 * @author xch
 *
 */
public class InsertSQLBuilder implements SQLBuilderIfc {

	private String tableName = null; //����
	private VectorMap vm_value = new VectorMap(); //�洢�����ֶ�ֵ�ļ�����

	private TBUtil tbUtil = null; //

	public InsertSQLBuilder() {
	}

	public InsertSQLBuilder(String _tableName) {
		this.tableName = _tableName; //
	}

	/**
	 * �ڹ��췽����ֱ���������ݣ������Ϳ���ͨ��һ�д�������SQL,���� new InsertSQLBuilder("pub_user",new String[][]{{"id","100"},{"code","xch"},{"name","�쳤��"}}).getSQL();  //
	 * @param _tableName
	 * @param _data,��ʼ����,������n��,���е�����!
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
	 * ����ֵ
	 * @param _fieldName
	 * @param _value
	 */
	public void putFieldValue(String _fieldName, String _value) {
		vm_value.put(_fieldName, _value); //
	}

	/**
	 * ����ֵ,���ֵΪnull,����nvl����
	 * @param _fieldName
	 * @param _value
	 * @param _nvl ��_valueΪnullʱ�ø�ֵ����!!!
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
	 * @return
	 */
	public String getSQL(boolean _isConvert) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("insert into " + getTableName() + " ("); //
		String[] str_keys = vm_value.getKeysAsString(); //
		for (int i = 0; i < str_keys.length; i++) {
			sb_sql.append(str_keys[i]); //
			if (i != str_keys.length - 1) { //����������һ��..
				sb_sql.append(","); //
			}
		}
		sb_sql.append(") values ("); //
		String zfj = getTBUtil().getSysOptionStringValue("���ݿ��ַ���", "GBK");//��ǰֻ�ж�GBK�������UTF-8��һ������ռ�����ֽڣ���У�����ͨ�������������ݿⱨ�������øò��������/2016-04-26��
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
						if (_isConvert) { //�����Ҫת����ת��!!
							str_value = convertSQLValue(str_value); //ת��һ��!!!
							try {
								if (str_value != null && str_value.getBytes(zfj) != null && str_value.getBytes(zfj).length > 4000) { //�������4000,�򻹽�ȡһ��!������Ҫ�ܴ������ĵ�!
									//str_value = str_value.substring(0, 4000); //������ȡ��������ܻ����ִ�б���,��Ϊ�������ĵ����Ż򶺺ŶԲ�����!!!���Ըɴ�ֱ�ӽ��������滻����,���ױ�֤������!!!
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
								//��ǰ����if���жϵ�str_itemvalue����str_itemvalue ��ǰ���δ��ֵ���ʵ�������Ϊ'null'�����޸�֮�����/2016-09-06��
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
				} else if (obj instanceof Long) { //������
					Long ll_value = (Long) obj; //
					sb_sql.append("'" + ll_value + "'"); //
				} else {
					sb_sql.append("'" + obj.toString() + "'"); //
				}
			}

			if (i != str_keys.length - 1) { //����������һ��..
				sb_sql.append(","); //
			}
		}
		sb_sql.append(")"); //
		return sb_sql.toString(); //
	}

	/**
	 * �滻SQL�еĵ�����,��Ϊ�����Żᵼ�±���ʧ��!!
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
	private String saveClobValue(String _value) {
		try {
			HashMap map = new HashMap();
			map.put("value", _value);
			HashMap rtn = null;
			if (getTBUtil().getJVSite() == 1) { //�ͻ���
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
	 * �ع�ToString()����,����SQL
	 */
	@Override
	public String toString() {
		return getSQL(); //
	}

	public static void main(String[] _args) {
		System.out.println("pushclob:1111".substring(9)); //
	}
}
