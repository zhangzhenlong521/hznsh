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
 * HashVO 是一个VectorMap用来存储的数据结构
 * 在返回大批量HashVO数据给客户端时,有一定的性能瓶颈.
 */
public class HashVO implements Serializable, Cloneable {

	private static final long serialVersionUID = -2432521772081284764L;

	private VectorMap m_hData = new VectorMap(); //VectorMap,按输入顺序排序,实际上就是在这个对象里存储了所有数据,但问题是每一行数据都会存储这么一个对象,数据量大时有一定瓶颈

	private String equalsFieldName = null;
	private String toStringFieldName = null;

	private HashMap ht_userobject = null; //

	private boolean isVisible = true; //是否显示!!本来数据对象不存在是否显示的问题,但考虑到数据权限设计时非常需要过滤某条数据,如果有这样一个属性则方便的多!

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
	 * 是否包括某一个key
	 * @param _key
	 * @return
	 */
	public boolean containsKey(String _key) {
		return m_hData.containsKey(_key);
	}

	/**
	 * 是否包括某一个key,忽略大小写,性能没有上一个高
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
	 * 取得字符值
	 * 
	 * @param attrname
	 * @param _isNullConvertEmptyStr 是否在为null值时自动转换成空字符串"",有时在生成报表时,如果不转换成会出现字符串"null"
	 * @return
	 */
	public String getStringValue(String attrname, String _isNullConvertStr) {
		return getStringValue(attrname, _isNullConvertStr, false); //
	}

	/**
	 * 取得字符值
	 * 
	 * @param attrname
	 * @param _isNullConvertEmptyStr 是否在为null值时自动转换成空字符串"",有时在生成报表时,如果不转换成会出现字符串"null"
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
		if (_isEmptyStrAsNull && str_toStr.trim().equals("") && _isNullConvertStr != null) { //如果是空符串,且指定了空字符串
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
	 * 强行将对象转换成2006-12-25的样子的字符串
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
	 * 强行将对象转换成2006-12-25 12:25:32的样子的字符串
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
	 * 取得Integer值
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
		} else if (o instanceof BigDecimal) { //如果是BigDecimal
			BigDecimal bd = (BigDecimal) o;
			return new Integer(bd.intValue());
		} else if (o instanceof RefItemVO) { //如果是下拉框
			RefItemVO bd = (RefItemVO) o;
			return new Integer(bd.getId());
		} else if (o instanceof ComBoxItemVO) { //如果是下拉框
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
	 * 取得Integer值
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
		} else if (o instanceof BigDecimal) { //如果是BigDecimal
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
	 * 取得Double值
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
		} else if (o instanceof BigDecimal) { //如果是BigDecimal
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
	 * 取得Double值
	 * 
	 * @param attrname
	 * @return
	 */
	public BigDecimal getBigDecimalValue(String attrname) {
		Object o = getAttributeValue(attrname);
		if (o == null) {
			return new BigDecimal("0");
		} else if (o instanceof BigDecimal) { //如果是BigDecimal
			BigDecimal bd = (BigDecimal) o;
			return bd;
		} else {
			return new BigDecimal(o.toString());
		}
	}

	/**
	 * 取得Double值
	 * 
	 * @param attrname
	 * @return
	 */
	public BigDecimal getBigDecimalValue(String attrname, BigDecimal _nvl) {
		Object o = getAttributeValue(attrname);
		if (o == null) {
			return _nvl;
		} else if (o instanceof BigDecimal) { //如果是BigDecimal
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
	 * 取得Boolean值
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

		if (o.toString().trim().equalsIgnoreCase("Y") || o.toString().trim().equalsIgnoreCase("yes") || o.toString().trim().equalsIgnoreCase("是") || o.toString().trim().equalsIgnoreCase("1")) {
			return Boolean.TRUE;
		} else if (o.toString().trim().equalsIgnoreCase("N") || o.toString().trim().equalsIgnoreCase("no") || o.toString().trim().equalsIgnoreCase("否") || o.toString().trim().equalsIgnoreCase("0")) {
			return Boolean.FALSE;
		}

		return new Boolean(o.toString()); // 先直接试下
	}

	/**
	 * 取得Boolean值,如果值为空时返回第二个参数!
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

		if (o.toString().trim().equalsIgnoreCase("Y") || o.toString().trim().equalsIgnoreCase("yes") || o.toString().trim().equalsIgnoreCase("是") || o.toString().trim().equalsIgnoreCase("1")) {
			return Boolean.TRUE;
		} else if (o.toString().trim().equalsIgnoreCase("N") || o.toString().trim().equalsIgnoreCase("no") || o.toString().trim().equalsIgnoreCase("否") || o.toString().trim().equalsIgnoreCase("0")) {
			return Boolean.FALSE;
		}

		return new Boolean(o.toString()); // 先直接试下
	}

	public Boolean getBooleanValue(int _pos) {
		return getBooleanValue(getKeys()[_pos]);
	}

	/**
	 * 强行将对象转换成Date,精确到天!!
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
				if (str_value.length() == 10) { //如果是10位
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date date = sdf.parse(str_value);
					return new java.sql.Date(date.getTime());
				} else if (str_value.length() == 19) { //如果是19位
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date date = sdf.parse(str_value);
					return new java.sql.Date(date.getTime());
				} else {
					return null;
				}
			} catch (Exception ex) {
				System.out.println("将字符串[" + str_value + "]转换日期失败!!!");
				ex.printStackTrace();
				return null;
			}
		}
	}

	public java.sql.Date getDateValue(int _pos) {
		return getDateValue(getKeys()[_pos]);
	}

	/**
	 * 取得TimeStamp值
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
				if (str_value.length() == 10) { //如果是10位
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					java.util.Date date = sdf.parse(str_value);
					return new java.sql.Timestamp(date.getTime());
				} else if (str_value.length() == 19) { //如果是19位
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					java.util.Date date = sdf.parse(str_value);
					return new java.sql.Timestamp(date.getTime());
				} else {
					return null;
				}
			} catch (Exception ex) {
				System.out.println("将字符串[" + str_value + "]转换Timestamp失败!!!");
				ex.printStackTrace();
				return null;
			}
		}
	}

	public java.sql.Timestamp getTimeStampValue(int _pos) {
		return getTimeStampValue(getKeys()[_pos]);
	}

	/**
	 * 重构ToString方法
	 */
	public String toString() {
		try {
			//此处需要重新设计.从数据库取出的值不一定有name,更不一定第二个位置.
			//不从数据库得到的VO里name不一定在第二个位置(BillCard的getAllObjectValuesWithHashMap()会打乱原先顺序).
			if (getToStringFieldName() != null) {
				return getStringValue(getToStringFieldName()); //
			} else {
				String name = getStringValue(2); //强行取第3列!
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
	 * 在调试时经常需要坚起来输出一个HashVO中的数据,用StringBuilder加上换行符打印
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
	 * 这种克隆只适合于存储的是简单数据对象,比如String,Inetger等,像UserObjcet中的Map就有问题
	 * 像复杂对象必须使用deepClone搞定，但deepClone性能没有这个快!!!【xch/2012-08-11】
	 */
	public HashVO clone() {
		HashVO vo = new HashVO(); //创建一个新的VO
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

	//判断是否显示!
	public boolean isVisible() {
		return isVisible;
	}

	//设置是否显示!
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
	 * 深度克隆,即复制一个对象!!不影响原来的对象!!
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
	 * 将该HashVO转换成一个真正具体的数据VO,很有用!
	 * @param _class
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object convertToRealOBJ(java.lang.Class _class) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object returnobj = _class.newInstance(); //创建实例!!
		Method[] methods = _class.getMethods(); //取得所有方法!!!包括所有父类的!!!
		String[] all_keys = this.getKeys(); //
		for (int i = 0; i < all_keys.length; i++) {
			for (int j = 0; j < methods.length; j++) {
				Class[] parClass = methods[j].getParameterTypes(); //取得所有参数类型列表
				//System.out.println("方法名:" + methods[j].getName());
				if (methods[j].getName().equalsIgnoreCase("set" + all_keys[i]) && parClass.length == 1) { //该方法名正好是以set开头,且set后面的名称正好又是该弄的名称,并且又是只有一个参数!!
					//System.out.println("找到对应方法:" + methods[j].getName()); //
					String str_realvalue = getStringValue(all_keys[i]); //取得真正的值
					if (str_realvalue == null) { //如果是空对象
						methods[j].invoke(returnobj, new Object[] { null }); //设置值!!
					} else { //
						Object setObject = null;
						if (parClass[0].equals(java.lang.String.class)) { //如果是String类型
							setObject = str_realvalue;
						} else if (parClass[0].equals(java.lang.Integer.class)) { //如果是Integer类型
							setObject = new Integer((int) Double.parseDouble(str_realvalue.trim())); //这里处理一下，如果是“123.456”的格式最后取出是123
						} else if (parClass[0].equals(java.lang.Long.class)) { //如果是Long类型
							setObject = new java.lang.Long(str_realvalue.trim()); //
						} else if (parClass[0].equals(java.lang.Double.class)) { //如果是Double类型
							setObject = new java.lang.Double(str_realvalue.trim()); //
						} else if (parClass[0].equals(java.math.BigDecimal.class)) { //如果是BigDecimal类型
							setObject = new java.math.BigDecimal(str_realvalue.trim()); //
						} else {
							setObject = str_realvalue; //
						}

						try {
							methods[j].invoke(returnobj, new Object[] { setObject }); //设置值!!!
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					break; //中断内层循环!!提交效率,即找到后就不往下找了!!
				}
			}
		}
		return returnobj;
	}

}
