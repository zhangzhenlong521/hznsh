/**************************************************************************
 * $RCSfile: HashVO.java,v $  $Revision: 1.19 $  $Date: 2012/10/08 02:22:50 $
 **************************************************************************/

package cn.com.infostrategy.to.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;

/**
 * HashVO ��һ��VectorMap�����洢�����ݽṹ
 * �ڷ��ش�����HashVO���ݸ��ͻ���ʱ,��һ��������ƿ��.
 */
public class HashVO implements Serializable, Cloneable {

	private static final long serialVersionUID = -2432521772081284764L;

	private VectorMap m_hData = new VectorMap(); //VectorMap,������˳������,ʵ���Ͼ��������������洢����������,��������ÿһ�����ݶ���洢��ôһ������,��������ʱ��һ��ƿ��

	private String equalsFieldName = null;
	private String toStringFieldName = null;

	private HashMap ht_userobject = null; //

	private boolean isVisible = true; //�Ƿ���ʾ!!�������ݶ��󲻴����Ƿ���ʾ������,�����ǵ�����Ȩ�����ʱ�ǳ���Ҫ����ĳ������,���������һ�������򷽱�Ķ�!

	public HashVO() {
		super();
	}

	public Object getAttributeValue(String _attributeName) {
		return m_hData.get(_attributeName.toLowerCase());
	}

	public void setAttributeValue(String key, Object value) {
		m_hData.put(key.toLowerCase(), value);
	}

	public int length() {
		return getKeys().length;
	}

	public java.lang.String[] getKeys() {
		return m_hData.getKeysAsString();

	}

	/**
	 * �Ƿ����ĳһ��key
	 * @param _key
	 * @return
	 */
	public boolean containsKey(String _key) {
		return m_hData.containsKey(_key);
	}

	/**
	 * �Ƿ����ĳһ��key,���Դ�Сд,����û����һ����
	 * @param _key
	 * @return
	 */
	public boolean containsKeyIgnoreCasel(String _key) {
		String[] str_keys = getKeys(); //
		for (int i = 0; i < str_keys.length; i++) {
			if (str_keys[i].equalsIgnoreCase(_key)) {
				return true;
			}
		}
		return false;
	}

	public java.lang.String[] getValuesAsString() {
		int li_length = length();
		String[] str_return = new String[li_length];
		for (int i = 0; i < li_length; i++) {
			str_return[i] = getStringValue(i);
		}
		return str_return;
	}

	public Object getObjectValue(String attrname) {
		return getAttributeValue(attrname);
	}

	public Object getObjectValue(int _pos) {
		return getObjectValue(getKeys()[_pos]);
	}

	public String getStringValue(String attrname) {
		return getStringValue(attrname, false); //
	}

	public String getStringValue(String attrname, boolean _isNullConvertEmptyStr) {
		if (_isNullConvertEmptyStr) { //
			return getStringValue(attrname, ""); //
		} else {
			return getStringValue(attrname, null); //
		}

	}

	/**
	 * ȡ���ַ�ֵ
	 * 
	 * @param attrname
	 * @param _isNullConvertEmptyStr �Ƿ���Ϊnullֵʱ�Զ�ת���ɿ��ַ���"",��ʱ�����ɱ���ʱ,�����ת���ɻ�����ַ���"null"
	 * @return
	 */
	public String getStringValue(String attrname, String _isNullConvertStr) {
		return getStringValue(attrname, _isNullConvertStr, false); //
	}

	/**
	 * ȡ���ַ�ֵ
	 * 
	 * @param attrname
	 * @param _isNullConvertEmptyStr �Ƿ���Ϊnullֵʱ�Զ�ת���ɿ��ַ���"",��ʱ�����ɱ���ʱ,�����ת���ɻ�����ַ���"null"
	 * @return
	 */
	public String getStringValue(String attrname, String _isNullConvertStr, boolean _isEmptyStrAsNull) {
		Object o = getAttributeValue(attrname);
		if (o == null) {
			if (_isNullConvertStr != null) {
				return _isNullConvertStr; //
			} else {
				return null;
			}
		}

		String str_toStr = o.toString();
		if (_isEmptyStrAsNull && str_toStr.trim().equals("") && _isNullConvertStr != null) { //����ǿշ���,��ָ���˿��ַ���
			return _isNullConvertStr; //
		}

		if (o instanceof String) {
			return str_toStr;
		} else if (o instanceof BigDecimal) {
			return str_toStr;
		} else if (o instanceof java.sql.Timestamp) {
			java.sql.Timestamp ts = (java.sql.Timestamp) o;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str_date = sdf.format(new java.util.Date(ts.getTime())); //
			return str_date;
		} else if (o instanceof Integer) {
			return str_toStr;
		} else if (o instanceof Long) {
			return str_toStr;
		} else if (o instanceof Double) {
			return str_toStr;
		} else if (o instanceof java.util.Date) {
			java.util.Date date = (java.util.Date) o;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str_date = sdf.format(date); //
			return str_date;
		} else {
			return str_toStr;
		}
	}

	/**
	 * ǿ�н�����ת����2006-12-25�����ӵ��ַ���
	 * @param attrname
	 * @return
	 */
	public String getStringValueForDay(String attrname) {
		Object o = getAttributeValue(attrname);
		if (o == null) {
			return null;
		}

		if (o instanceof java.sql.Timestamp) {
			java.sql.Timestamp ts = (java.sql.Timestamp) o;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String str_date = sdf.format(new java.util.Date(ts.getTime())); //
			return str_date;
		} else if (o instanceof java.util.Date) {
			java.util.Date date = (java.util.Date) o;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String str_date = sdf.format(date); //
			return str_date;
		} else {
			return o.toString();
		}
	}

	/**
	 * ǿ�н�����ת����2006-12-25 12:25:32�����ӵ��ַ���
	 * @param attrname
	 * @return
	 */
	public String getStringValueForSecond(String attrname) {
		Object o = getAttributeValue(attrname);
		if (o == null) {
			return null;
		}

		if (o instanceof java.sql.Timestamp) {
			java.sql.Timestamp ts = (java.sql.Timestamp) o;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str_date = sdf.format(new java.util.Date(ts.getTime())); //
			return str_date;
		} else if (o instanceof java.util.Date) {
			java.util.Date date = (java.util.Date) o;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str_date = sdf.format(date); //
			return str_date;
		} else {
			return o.toString();
		}
	}

	public String getStringValue(int _pos) {
		String[] str_keys = getKeys(); //
		if (str_keys != null && str_keys.length > _pos) {
			return getStringValue(getKeys()[_pos]);
		} else {
			return null;
		}
	}

	/**
	 * ȡ��Integerֵ
	 * 
	 * @param attrname
	 * @return
	 */
	public Integer getIntegerValue(String attrname) {
		Object o = getAttributeValue(attrname);
		if (o == null || "".equals(o))
			return null;

		if (o instanceof Integer) {
			return (Integer) o;
		} else if (o instanceof BigDecimal) { //�����BigDecimal
			BigDecimal bd = (BigDecimal) o;
			return new Integer(bd.intValue());
		} else if (o instanceof RefItemVO) { //�����������
			RefItemVO bd = (RefItemVO) o;
			return new Integer(bd.getId());
		} else if (o instanceof ComBoxItemVO) { //�����������
			ComBoxItemVO bd = (ComBoxItemVO) o;
			return new Integer(bd.getId());
		} else {
			String str_value = o.toString();
			if (str_value.trim().equals("")) {
				return null; //
			}

			int li_pos = str_value.indexOf(".");
			if (li_pos >= 0) {
				str_value = str_value.substring(0, li_pos);
			}
			return new Integer(str_value.trim());
		}
	}

	public int getIntegerValue(String attrname, int _nvl) {
		Object o = getAttributeValue(attrname);
		if (o == null || "".equals(o)) {
			return _nvl;
		} else {
			return getIntegerValue(attrname).intValue();
		}
	}

	public Integer getIntegerValue(int _pos) {
		return getIntegerValue(getKeys()[_pos]);
	}

	public Long getLognValue(String attrname) {
		return getLognValue(attrname, -1); //
	}

	/**
	 * ȡ��Integerֵ
	 * 
	 * @param attrname
	 * @return
	 */
	public Long getLognValue(String attrname, long _nvl) {
		Object o = getAttributeValue(attrname);
		if (o == null)
			if (_nvl >= 0) {
				return _nvl;
			} else {
				return null;
			}
		if (o instanceof Long) {
			return (Long) o;
		} else if (o instanceof BigDecimal) { //�����BigDecimal
			BigDecimal bd = (BigDecimal) o;
			return new Long(bd.longValue());
		} else {
			String str_value = o.toString(); //
			return new Long(str_value); //
		}
	}

	public Long getLognValue(int _pos) {
		return getLognValue(getKeys()[_pos]);
	}

	/**
	 * ȡ��Doubleֵ
	 * 
	 * @param attrname
	 * @return
	 */
	public Double getDoubleValue(String attrname) {
		return getDoubleValue(attrname, null); //
	}

	public Double getDoubleValue(String attrname, Double _nvldouble) {
		Object o = getAttributeValue(attrname);
		if (o == null || ("" + o).equals("")) {
			if (_nvldouble != null) {
				return _nvldouble; //
			} else {
				return null;
			}
		} else if (o instanceof Double) {
			return (Double) o;
		} else if (o instanceof BigDecimal) { //�����BigDecimal
			BigDecimal bd = (BigDecimal) o;
			return new Double(bd.doubleValue());
		} else {
			Double d = 0d;
			try {
				d = new Double(o.toString());
			} catch (Exception ex) {
			}
			return d;
		}
	}

	public Double getDoubleValue(int _pos) {
		return getDoubleValue(getKeys()[_pos]);
	}

	public Double getDoubleValue(int _pos, double _nvl) {
		return getDoubleValue(getKeys()[_pos], _nvl);
	}

	/**
	 * ȡ��Doubleֵ
	 * 
	 * @param attrname
	 * @return
	 */
	public BigDecimal getBigDecimalValue(String attrname) {
		Object o = getAttributeValue(attrname);
		if (o == null) {
			return new BigDecimal("0");
		} else if (o instanceof BigDecimal) { //�����BigDecimal
			BigDecimal bd = (BigDecimal) o;
			return bd;
		} else {
			return new BigDecimal(o.toString());
		}
	}

	/**
	 * ȡ��Doubleֵ
	 * 
	 * @param attrname
	 * @return
	 */
	public BigDecimal getBigDecimalValue(String attrname, BigDecimal _nvl) {
		Object o = getAttributeValue(attrname);
		if (o == null) {
			return _nvl;
		} else if (o instanceof BigDecimal) { //�����BigDecimal
			BigDecimal bd = (BigDecimal) o;
			return bd;
		} else {
			return new BigDecimal(o.toString());
		}
	}

	public BigDecimal getBigDecimalValue(int _pos) {
		return getBigDecimalValue(getKeys()[_pos]);
	}

	/**
	 * ȡ��Booleanֵ
	 * 
	 * @param attrname
	 * @return
	 */
	public Boolean getBooleanValue(String attrname) {
		Object o = getAttributeValue(attrname);
		if (o == null)
			return Boolean.TRUE;

		if (o instanceof Boolean)
			return (Boolean) o;

		if (o.toString().trim().equalsIgnoreCase("Y") || o.toString().trim().equalsIgnoreCase("yes") || o.toString().trim().equalsIgnoreCase("��") || o.toString().trim().equalsIgnoreCase("1")) {
			return Boolean.TRUE;
		} else if (o.toString().trim().equalsIgnoreCase("N") || o.toString().trim().equalsIgnoreCase("no") || o.toString().trim().equalsIgnoreCase("��") || o.toString().trim().equalsIgnoreCase("0")) {
			return Boolean.FALSE;
		}

		return new Boolean(o.toString()); // ��ֱ������
	}

	/**
	 * ȡ��Booleanֵ,���ֵΪ��ʱ���صڶ�������!
	 * @param attrname
	 * @param _nvl
	 * @return
	 */
	public Boolean getBooleanValue(String attrname, boolean _nvl) {
		Object o = getAttributeValue(attrname);
		if (o == null)
			return _nvl;

		if (o instanceof Boolean)
			return (Boolean) o;

		if (o.toString().trim().equalsIgnoreCase("Y") || o.toString().trim().equalsIgnoreCase("yes") || o.toString().trim().equalsIgnoreCase("��") || o.toString().trim().equalsIgnoreCase("1")) {
			return Boolean.TRUE;
		} else if (o.toString().trim().equalsIgnoreCase("N") || o.toString().trim().equalsIgnoreCase("no") || o.toString().trim().equalsIgnoreCase("��") || o.toString().trim().equalsIgnoreCase("0")) {
			return Boolean.FALSE;
		}

		return new Boolean(o.toString()); // ��ֱ������
	}

	public Boolean getBooleanValue(int _pos) {
		return getBooleanValue(getKeys()[_pos]);
	}

	/**
	 * ǿ�н�����ת����Date,��ȷ����!!
	 * 
	 * @param attrname
	 * @return
	 */
	public java.sql.Date getDateValue(String attrname) {
		Object o = getAttributeValue(attrname);
		if (o == null)
			return null;

		if (o instanceof java.sql.Timestamp) {
			java.sql.Timestamp ts = (java.sql.Timestamp) o;
			return new java.sql.Date(ts.getTime());
		} else if (o instanceof java.sql.Date) {
			return (java.sql.Date) o;
		} else {
			String str_value = o.toString().trim();
			try {
				if (str_value.length() == 10) { //�����10λ
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date date = sdf.parse(str_value);
					return new java.sql.Date(date.getTime());
				} else if (str_value.length() == 19) { //�����19λ
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date date = sdf.parse(str_value);
					return new java.sql.Date(date.getTime());
				} else {
					return null;
				}
			} catch (Exception ex) {
				System.out.println("���ַ���[" + str_value + "]ת������ʧ��!!!");
				ex.printStackTrace();
				return null;
			}
		}
	}

	public java.sql.Date getDateValue(int _pos) {
		return getDateValue(getKeys()[_pos]);
	}

	/**
	 * ȡ��TimeStampֵ
	 * 
	 * @param attrname
	 * @return
	 */
	public java.sql.Timestamp getTimeStampValue(String attrname) {
		Object o = getAttributeValue(attrname);
		if (o == null)
			return null;

		if (o instanceof java.sql.Timestamp) {
			java.sql.Timestamp ts = (java.sql.Timestamp) o;
			return ts;
		} else if (o instanceof java.sql.Date) {
			java.sql.Date date = (java.sql.Date) o;
			return new java.sql.Timestamp(date.getTime());
		} else {
			String str_value = o.toString().trim();
			try {
				if (str_value.length() == 10) { //�����10λ
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date date = sdf.parse(str_value);
					return new java.sql.Timestamp(date.getTime());
				} else if (str_value.length() == 19) { //�����19λ
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					java.util.Date date = sdf.parse(str_value);
					return new java.sql.Timestamp(date.getTime());
				} else {
					return null;
				}
			} catch (Exception ex) {
				System.out.println("���ַ���[" + str_value + "]ת��Timestampʧ��!!!");
				ex.printStackTrace();
				return null;
			}
		}
	}

	public java.sql.Timestamp getTimeStampValue(int _pos) {
		return getTimeStampValue(getKeys()[_pos]);
	}

	/**
	 * �ع�ToString����
	 */
	public String toString() {
		try {
			//�˴���Ҫ�������.�����ݿ�ȡ����ֵ��һ����name,����һ���ڶ���λ��.
			//�������ݿ�õ���VO��name��һ���ڵڶ���λ��(BillCard��getAllObjectValuesWithHashMap()�����ԭ��˳��).
			if (getToStringFieldName() != null) {
				return getStringValue(getToStringFieldName()); //
			} else {
				String name = getStringValue(2); //ǿ��ȡ��3��!
				if (name == null) {
					return "";
				} else {
					return name;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * �ڵ���ʱ������Ҫ���������һ��HashVO�е�����,��StringBuilder���ϻ��з���ӡ
	 * @return
	 */
	public String getSBStr() {
		StringBuilder sb_str = new StringBuilder(); //
		String[] str_keys = this.getKeys(); //
		for (int i = 0; i < str_keys.length; i++) {
			sb_str.append("[" + str_keys[i] + "]=[" + getStringValue(str_keys[i]) + "]\r\n"); //
		}
		return sb_str.toString(); //
	}

	@Override
	public boolean equals(Object obj) {
		if (this.equalsFieldName != null) {
			HashVO hvo2 = (HashVO) obj;
			return this.getStringValue(equalsFieldName).equals(hvo2.getStringValue(equalsFieldName));
		} else {
			return super.equals(obj);
		}
	}

	/**
	 * ���ֿ�¡ֻ�ʺ��ڴ洢���Ǽ����ݶ���,����String,Inetger��,��UserObjcet�е�Map��������
	 * ���Ӷ������ʹ��deepClone�㶨����deepClone����û�������!!!��xch/2012-08-11��
	 */
	public HashVO clone() {
		HashVO vo = new HashVO(); //����һ���µ�VO
		String[] str_keys = this.getKeys(); //
		for (int i = 0; i < str_keys.length; i++) {
			vo.setAttributeValue(str_keys[i], getObjectValue(str_keys[i])); //
		}
		return vo;
	}

	public void removeItem(String _key) {
		if (_key != null) {
			m_hData.remove(_key.toLowerCase());
		}
	}

	public VectorMap getM_hData() {
		return m_hData;
	}

	public void setM_hData(VectorMap data) {
		m_hData = data;
	}

	public HashMap getHt_userobject() {
		return ht_userobject;
	}

	public void setHt_userobject(HashMap ht_userobject) {
		this.ht_userobject = ht_userobject;
	}

	public void setUserObject(Object _key, Object _value) {
		if (ht_userobject == null) {
			ht_userobject = new HashMap();
		}
		ht_userobject.put(_key, _value); //
	}

	//�ж��Ƿ���ʾ!
	public boolean isVisible() {
		return isVisible;
	}

	//�����Ƿ���ʾ!
	public void setVisible(boolean _isVisible) {
		this.isVisible = _isVisible;
	}

	public Object getUserObject(Object _key) {
		if (ht_userobject == null) {
			return null;
		} else {
			return ht_userobject.get(_key); //
		}
	}

	/**
	 * ��ȿ�¡,������һ������!!��Ӱ��ԭ���Ķ���!!
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public HashVO deepClone() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
			return (HashVO) in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	public String getToStringFieldName() {
		return toStringFieldName;
	}

	public void setToStringFieldName(String toStringFieldName) {
		this.toStringFieldName = toStringFieldName;
	}

	public void setEqualsFieldName(String _equalsFieldName) {
		this.equalsFieldName = _equalsFieldName;
	}

	/**
	 * ����HashVOת����һ���������������VO,������!
	 * @param _class
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object convertToRealOBJ(java.lang.Class _class) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object returnobj = _class.newInstance(); //����ʵ��!!
		Method[] methods = _class.getMethods(); //ȡ�����з���!!!�������и����!!!
		String[] all_keys = this.getKeys(); //
		for (int i = 0; i < all_keys.length; i++) {
			for (int j = 0; j < methods.length; j++) {
				Class[] parClass = methods[j].getParameterTypes(); //ȡ�����в��������б�
				//System.out.println("������:" + methods[j].getName());
				if (methods[j].getName().equalsIgnoreCase("set" + all_keys[i]) && parClass.length == 1) { //�÷�������������set��ͷ,��set����������������Ǹ�Ū������,��������ֻ��һ������!!
					//System.out.println("�ҵ���Ӧ����:" + methods[j].getName()); //
					String str_realvalue = getStringValue(all_keys[i]); //ȡ��������ֵ
					if (str_realvalue == null) { //����ǿն���
						methods[j].invoke(returnobj, new Object[] { null }); //����ֵ!!
					} else { //
						Object setObject = null;
						if (parClass[0].equals(java.lang.String.class)) { //�����String����
							setObject = str_realvalue;
						} else if (parClass[0].equals(java.lang.Integer.class)) { //�����Integer����
							setObject = new Integer((int) Double.parseDouble(str_realvalue.trim())); //���ﴦ��һ�£�����ǡ�123.456���ĸ�ʽ���ȡ����123
						} else if (parClass[0].equals(java.lang.Long.class)) { //�����Long����
							setObject = new java.lang.Long(str_realvalue.trim()); //
						} else if (parClass[0].equals(java.lang.Double.class)) { //�����Double����
							setObject = new java.lang.Double(str_realvalue.trim()); //
						} else if (parClass[0].equals(java.math.BigDecimal.class)) { //�����BigDecimal����
							setObject = new java.math.BigDecimal(str_realvalue.trim()); //
						} else {
							setObject = str_realvalue; //
						}

						try {
							methods[j].invoke(returnobj, new Object[] { setObject }); //����ֵ!!!
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					break; //�ж��ڲ�ѭ��!!�ύЧ��,���ҵ���Ͳ���������!!
				}
			}
		}
		return returnobj;
	}

}
