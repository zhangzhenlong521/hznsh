/**************************************************************************
 * $RCSfile: BillVO.java,v $  $Revision: 1.16 $  $Date: 2012/11/14 02:02:37 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.UIUtil;

public class BillVO implements Serializable {

	private static final long serialVersionUID = 153235551278950298L;

	private String templetCode = null; //模板编码,可设可不设!!
	private String templetName = null; //模板名称,可设可不设!!
	private int modelRowNoInTree = -1; //该数据对应于Model数据中的第几行,目前在BillTree控件中用得到,因为BillTree的数据是存储在一个二维数组中,是通过行号来绑定的。为了提高性能,BillList与BillCard以后也可以考虑使用这种方法，所以这个变量还是很有必要的!
	private int modelRowNoInList = -1; //在列表模型中的位置
	private int modelRowNoInCard = -1; //在卡片模型中的位置

	// 四大关键参数!!
	private String queryTableName = null; // 查询表名
	private String saveTableName = null; // 保存表名!!

	private String pkName = null; // 主键字段名
	private String sequenceName = null; // 序列名

	// 真正的对象!!!
	private Object[] datas = null; // 真正的对象

	// 五大数组!!!可能都到去掉,因为考虑到序列化时的性能,都提供方法从datas中取
	private String[] keys = null; // 列名,对应于SQL中的key!!
	private String[] names = null; // 列名,对应于SQL中的key!!
	private String[] itemType = null; // 控件类型
	private String[] columnType = null; // 数据列的类型
	private boolean[] needSave = null; // 是否需要保存!!!

	private String toStringFieldName; //toString显示的Key的名称!!
	private HashMap linkChildDataMap = null; //存储引用子表数据的Map
	private HashMap ht_userobject = new HashMap(); //

	private boolean isVisible = true; //是否显示!!本来数据对象不存在是否显示的问题,但考虑到数据权限设计时非常需要过滤某条数据,如果有这样一个属性则方便的多!
	private boolean isEnabled = true; //是否有效,在数据权限设计时,发现有时可以查看清单,但不能点击查看详细,即visible=true,但enabled=false,即只能看不能动!!!!所以借用了控件中的这个属性来表达!
	private boolean isVirtualNode = false; //是否是虚拟VO?后来在树型结构中有了虚拟结点的概念,为了最好的设计,将BillVO中也增加该属性,改动最小,也能实现最强的扩展!【xch/2012-04-22】

	private TBUtil tbUtil = null; //

	public Object[] getDatas() {
		return datas;
	}

	public void setDatas(Object[] datas) {
		this.datas = datas;
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public boolean isHaveKey(String _key) {
		String[] allkeys = getKeys();
		for (int i = 0; i < allkeys.length; i++) {
			if (allkeys[i].equalsIgnoreCase(_key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 取得数据..
	 * @param _key
	 * @return
	 */
	public Object getObject(String _key) {
		int li_index = findIndex(_key);
		if (li_index >= 0) {
			return getDatas()[li_index];
		}
		return null;
	}

	/**
	 * 取得数据..
	 * @param _key
	 * @return
	 */
	public Object getObject(int _index) {
		return getDatas()[_index + 1];
	}

	public String getStringValue(String _key) {
		return getStringValue(_key, null); //
	}

	/**
	 * 取得数据..
	 * @param _key
	 * @return
	 */
	public String getStringValue(String _key, String _nvl) {
		Object obj = getObject(_key);
		if (obj == null) {
			if (_nvl != null) {
				return _nvl;
			} else {
				return null;
			}
		}

		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof StringItemVO) {
			return ((StringItemVO) obj).getStringValue();
		} else if (obj instanceof ComBoxItemVO) {
			return ((ComBoxItemVO) obj).getId();
		} else if (obj instanceof RefItemVO) {
			return ((RefItemVO) obj).getId();
		} else {
			return obj.toString();
		}
	}

	/**
	 * 取得显示数据!!!即显示时到底是什么???
	 * @param _key
	 * @return
	 */
	public String getStringViewValue(String _key) {
		Object obj = getObject(_key);
		if (obj == null) {
			return null;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof StringItemVO) {
			return ((StringItemVO) obj).getStringValue();
		} else if (obj instanceof ComBoxItemVO) {
			return ((ComBoxItemVO) obj).toString(); //
		} else if (obj instanceof RefItemVO) {
			if ("引用子表".equals(getItemTypeByKey(_key)) || "导入子表".equals(getItemTypeByKey(_key))) { //如果是引用子表,则比较特殊,需要找出引用子表的数据!
				BillVO[] childVOs = getLinkChildBillVos(_key); //引用子表的数据!!!
				if (childVOs == null || childVOs.length <= 0) {
					return null;
				} else {
					StringBuilder sb_text = new StringBuilder(); //
					for (int i = 0; i < childVOs.length; i++) { //遍历各条数据!!!
						sb_text.append(childVOs[i].toString() + ";"); //
					}
					return sb_text.toString(); //
				}
			} else {
				return ((RefItemVO) obj).toString(); //
			}
		} else {
			return obj.toString();
		}
	}

	/**
	 * 取得数据..
	 * @param _key
	 * @return
	 */
	public String getStringValue(int _index) {
		StringItemVO strItemVO = (StringItemVO) getObject(_index); //
		return strItemVO == null ? null : strItemVO.getStringValue(); //
	}

	/**
	 * 取得数据..
	 * @param _key
	 * @return
	 */
	public ComBoxItemVO getComBoxItemVOValue(String _key) {
		return (ComBoxItemVO) getObject(_key);
	}

	/**
	 * 取得数据..
	 * @param _key
	 * @return
	 */
	public RefItemVO getRefItemVOValue(String _key) {
		return (RefItemVO) getObject(_key);
	}

	/**
	 * 设置数据..
	 * @param _key
	 * @param _obj
	 */
	public void setObject(String _key, Object _obj) {
		int li_index = findIndex(_key);
		if (li_index >= 0) {
			datas[li_index] = _obj; //
		}
	}

	private int findIndex(String _key) {
		for (int i = 0; i < getKeys().length; i++) {
			if (getKeys()[i] != null && getKeys()[i].trim().equalsIgnoreCase(_key)) {
				return i;
			}
		}
		return -1;
	}

	public String[] getColumnType() {
		return columnType;
	}

	public void setColumnType(String[] columnType) {
		this.columnType = columnType;
	}

	public String[] getItemType() {
		return itemType;
	}

	//取得某一项的类型,后来发现需要这个方法,所以加上!【xch/2012-11-13】
	public String getItemTypeByKey(String _key) {
		for (int i = 0; i < keys.length; i++) { //
			if (keys[i].equalsIgnoreCase(_key)) {
				return itemType[i]; //
			}
		}
		return null; //
	}

	public void setItemType(String[] itemType) {
		this.itemType = itemType;
	}

	public String getPkName() {
		return pkName;
	}

	public void setPkName(String pkName) {
		this.pkName = pkName;
	}

	public String getQueryTableName() {
		return queryTableName;
	}

	public void setQueryTableName(String queryTableName) {
		this.queryTableName = queryTableName;
	}

	public String getSaveTableName() {
		return saveTableName;
	}

	public void setSaveTableName(String saveTableName) {
		this.saveTableName = saveTableName;
	}

	public boolean[] isNeedSave() {
		return needSave;
	}

	public void setNeedSaves(boolean[] needSave) {
		this.needSave = needSave;
	}

	public boolean[] getNeedSaves() {
		return needSave;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	/**
	 * 返回当前编辑状态!!
	 * 
	 * @return
	 */
	public String getEditType() {
		RowNumberItemVO rowNumberVO = (RowNumberItemVO) getDatas()[0]; // 行号VO!!!永远在第一列!!
		return rowNumberVO.getState(); //
	}

	/**
	 * 返回当前编辑状态!!
	 * 
	 * @return
	 */
	public void setEditType(String _type) {
		RowNumberItemVO rowNumberVO = (RowNumberItemVO) getDatas()[0]; // 行号VO!!!永远在第一列!!
		rowNumberVO.setState(_type);
	}

	/**
	 * 返回当前编辑状态!!
	 * 
	 * @return
	 */
	public RowNumberItemVO getRowNumberItemVO() {
		RowNumberItemVO rowNumberVO = (RowNumberItemVO) getDatas()[0]; // 行号VO!!!永远在第一列!!
		return rowNumberVO; //
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	// 以下都是需要保存的数据的列,名称,控件类型,数据类型等
	public String[] getNeedSaveKeys() {
		Vector v_tmp = new Vector();
		for (int i = 0; i < keys.length; i++) {
			if (isNeedSave()[i]) {
				v_tmp.add(keys[i]);
			}
		}
		return (String[]) v_tmp.toArray(new String[0]);
	}

	public String[] getNeedSaveNames() {
		Vector v_tmp = new Vector();
		for (int i = 0; i < names.length; i++) {
			if (isNeedSave()[i]) {
				v_tmp.add(names[i]);
			}
		}
		return (String[]) v_tmp.toArray(new String[0]);
	}

	public String[] getNeedSaveItemType() {
		Vector v_tmp = new Vector();
		for (int i = 0; i < itemType.length; i++) {
			if (isNeedSave()[i]) {
				v_tmp.add(itemType[i]);
			}
		}
		return (String[]) v_tmp.toArray(new String[0]);
	}

	public String[] getNeedSaveColumnType() {
		Vector v_tmp = new Vector();
		for (int i = 0; i < columnType.length; i++) {
			if (isNeedSave()[i]) {
				v_tmp.add(columnType[i]);
			}
		}
		return (String[]) v_tmp.toArray(new String[0]);
	}

	public Object[] getNeedSaveData() {
		Vector v_tmp = new Vector();
		for (int i = 0; i < datas.length; i++) {
			if (isNeedSave()[i]) {
				v_tmp.add(datas[i]);
			}
		}
		return (Object[]) v_tmp.toArray(new Object[0]);
	}

	/**
	 * 得到需要保存数据的直接塞到Oracle中的实际值,它主要是对下拉框VO与参照VO进行了一下处理,其他的都是直接用 toString方法!!
	 * 
	 * @return
	 */
	public String[] getNeedSaveDataRealValue() {
		Object[] objs = getNeedSaveData();
		String[] str_return = new String[objs.length];
		for (int i = 0; i < str_return.length; i++) {
			if (objs[i] == null) {
				str_return[i] = null;
			} else {
				if (objs[i] instanceof String) {
					str_return[i] = (String) objs[i];
				}
				if (objs[i] instanceof StringItemVO) {
					str_return[i] = ((StringItemVO) objs[i]).getStringValue();
				} else if (objs[i] instanceof ComBoxItemVO) {
					ComBoxItemVO vo = (ComBoxItemVO) objs[i];
					str_return[i] = vo.getId();
				} else if (objs[i] instanceof RefItemVO) {
					RefItemVO vo = (RefItemVO) objs[i];
					str_return[i] = vo.getId();
				} else {
					str_return[i] = "" + objs[i]; //
				}

				if (str_return[i] != null && str_return[i].trim().equals("")) {
					str_return[i] = null;
				}
			}
		}

		return str_return;
	}

	//计算Insert语句...
	public String getInsertSQL() {
		return getInsertSQL(null); //
	}

	//计算Insert语句
	public String getInsertSQL(String[] _encryptKeys) {
		String[] str_keys = getNeedSaveKeys(); //取得所有需要参与保存的列!!
		String[] str_realValue = getNeedSaveDataRealValue(); //
		InsertSQLBuilder isql = new InsertSQLBuilder(getSaveTableName()); //
		for (int i = 0; i < str_keys.length; i++) {
			if (_encryptKeys != null && getTBUtil().isExistInArray(str_keys[i], _encryptKeys, true) && str_realValue[i] != null && !str_realValue[i].trim().equals("")) { //如果这个字段需要加密!!!
				isql.putFieldValue(str_keys[i], encryptStr(str_realValue[i])); //加密处理数据!是DES可逆算法!!!
			} else {
				isql.putFieldValue(str_keys[i], str_realValue[i]); //需要处理是否加密处理!!
			}
		}
		return isql.getSQL(); //
	}

	//计算Update语句....
	public String getUpdateSQL() {
		return getUpdateSQL(null); //
	}

	//计算Update语句!!
	public String getUpdateSQL(String[] _encryptKeys) {
		String[] str_keys = getNeedSaveKeys(); //
		String[] str_realValue = getNeedSaveDataRealValue(); // 取得真正的值!!!!
		//DESKeyTool desTool = new DESKeyTool(); //加密工具
		UpdateSQLBuilder isql = new UpdateSQLBuilder(getSaveTableName(), getUpdateWhereCondition()); //
		for (int i = 0; i < str_keys.length; i++) {
			if (_encryptKeys != null && getTBUtil().isExistInArray(str_keys[i], _encryptKeys, true) && str_realValue[i] != null && !str_realValue[i].trim().equals("")) { //如果这个字段需要加密!!!
				isql.putFieldValue(str_keys[i], encryptStr(str_realValue[i])); //加密处理数据!是DES可逆算法!!!
			} else {
				isql.putFieldValue(str_keys[i], str_realValue[i]); //
			}
		}
		return isql.getSQL(); //
	}

	/**
	 * 取得删除的SQL!!!!!!
	 * 
	 * @return
	 */
	public String getDeleteSQL() {
		return "delete from " + getSaveTableName() + " where " + getUpdateWhereCondition(); //
	}

	public String getPkValue() {
		return findPkValue();
	}

	//加密数据!!
	private String encryptStr(String _str) {
		try {
			if (getTBUtil().getJVSite() == TBUtil.JVMSITE_CLIENT) { //如果是在客户端调用,则远程请求一下，因为老的JRE没有JCE的包!!!
				return UIUtil.getCommonService().encryptStr(_str); //
			} else {
				return new DESKeyTool().encrypt(_str); //服务器端则直接加密!!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * 找到主键的值
	 * 
	 * @return
	 */
	public String findPkValue() {
		if (pkName == null || pkName.trim().equals("")) {
			return ""; //
		} else { //
			// 去所有控件中找,如果找到则返回之
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].equalsIgnoreCase(pkName)) {
					return "" + ((datas[i] == null) ? "" : datas[i]); //
				}
			} // ..
			return ""; // 如果没找到则返回空
		}
	} // ....

	/**
	 * 拼成做update时where后面的条件!!!专门为做update操作时用!!! 届时只要在 where 后面加上指示这些条件即可!!!
	 */
	public String getUpdateWhereCondition() {
		String str_return = " ";
		if (pkName != null && !pkName.trim().equals("")) { // 如果主键不为null,且同时不是空字符串!!!!!即是有主键的!!!
			str_return = str_return + pkName + "='" + findPkValue() + "' "; // //..
		}
		return str_return;
	}

	/**
	 * 取得Where条件..
	 * @return
	 */
	public String getWhereCondition() {
		return getUpdateWhereCondition();
	}

	/**
	 * 替换SQL中的单引号,因为单引号会导致保存失败!!
	 * @param _value
	 * @return
	 */
	private String convertSQLValue(String _value) {
		if (_value == null) {
			return null;
		} else {
			_value = replaceAll(_value, "'", "''"); //
			_value = replaceAll(_value, "\\", "\\\\"); //
			return _value; //
		}
	}

	/**
	 * 替换字符
	 * 
	 * @param str_par
	 * @param old_item
	 * @param new_item
	 * @return
	 */
	public String replaceAll(String str_par, String old_item, String new_item) {
		String str_return = "";
		String str_remain = str_par;
		boolean bo_1 = true;
		while (bo_1) {
			int li_pos = str_remain.indexOf(old_item);
			if (li_pos < 0) {
				break;
			} // 如果找不到,则返回
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // 将结果字符串加上原来前辍
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // 将剩余的加上
		return str_return;
	}

	/**
	 * 判断是否要处理版本号,即是否需要处理乐观锁!!!
	 * 需要处理乐观锁的前提是,必须有一个字段叫version,而且该列是参与保存的!!!
	 * @return
	 */
	public boolean isDealVersion() {
		String[] needSaveKeys = getNeedSaveKeys();
		for (int i = 0; i < needSaveKeys.length; i++) {
			if (needSaveKeys[i].equalsIgnoreCase("version")) {
				return true;
			}
		}
		return false; //
	}

	/**
	 * 取得当前版本号!!
	 * @return
	 */
	public Double getVersion() {
		Object obj = getObject("version");
		if (obj == null || obj.toString().trim().equals("")) {
			return null;
		}
		return new Double(obj.toString()); //
	}

	public String getRealValue(String _key) {
		Object obj = getObject(_key);
		if (obj == null) {
			return null;
		}
		if (obj instanceof String) {
			return (String) obj;
		} else if (obj instanceof ComBoxItemVO) {
			ComBoxItemVO vo = (ComBoxItemVO) obj;
			return vo.getId();
		} else if (obj instanceof RefItemVO) {
			RefItemVO vo = (RefItemVO) obj;
			return vo.getId();
		} else {
			return "" + obj; //
		}

	}

	/**
	 * 将该BillVO快速转换成某一个具体的实际类!!具体的实际类就是包括set,get方法的一些实际类!!即将参数级的一些API变成方法级了!!
	 * 用法如下:
	 * gxlu.nova.system.login.vo.TestRealVO realVO = (gxlu.nova.system.login.vo.TestRealVO) billVO.convertToRealOBJ(gxlu.nova.system.login.vo.TestRealVO.class); //
	 * 
	 * @param _class 实际的类名
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object convertToRealOBJ(java.lang.Class _class) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object returnobj = _class.newInstance(); //创建实例!!
		Method[] methods = _class.getMethods(); //取得所有方法!!!包括所有父类的!!!
		String[] all_keys = this.getKeys();
		for (int i = 0; i < all_keys.length; i++) {
			for (int j = 0; j < methods.length; j++) {
				Class[] parClass = methods[j].getParameterTypes();
				//System.out.println("方法名:" + methods[j].getName());
				if (methods[j].getName().equalsIgnoreCase("set" + all_keys[i]) && parClass.length == 1) { //该方法名正好是以set开头,且set后面的名称正好又是该弄的名称,并且又是只有一个参数!!
					//System.out.println("找到对应方法:" + methods[j].getName()); //
					String str_realvalue = getRealValue(all_keys[i]); //取得真正的值
					if (str_realvalue == null) { //如果是空对象
						methods[j].invoke(returnobj, new Object[] { null }); //设置值!!
					} else { //
						Object setObject = null;
						if (parClass[0].equals(java.lang.String.class)) { //如果是String类型
							setObject = str_realvalue;
						} else if (parClass[0].equals(java.lang.Integer.class)) { //如果是Integer类型
							setObject = new Integer(str_realvalue); //
						} else if (parClass[0].equals(java.lang.Long.class)) { //如果是Long类型
							setObject = new java.lang.Long(str_realvalue); //
						} else if (parClass[0].equals(java.lang.Double.class)) { //如果是Double类型
							setObject = new java.lang.Double(str_realvalue); //
						} else if (parClass[0].equals(java.math.BigDecimal.class)) { //如果是BigDecimal类型
							setObject = new java.math.BigDecimal(str_realvalue); //
						} else {
							setObject = str_realvalue; //
						}

						methods[j].invoke(returnobj, new Object[] { setObject }); //设置值!!!
					}
					break; //中断内层循环!!提交效率,即找到后就不往下找了!!
				}
			}
		}
		return returnobj;
	}

	public String getTempletCode() {
		return templetCode;
	}

	public void setTempletCode(String templetCode) {
		this.templetCode = templetCode;
	}

	public Object clone() {
		BillVO cloneBillVO = new BillVO();
		cloneBillVO.setTempletCode(this.getTempletCode());
		cloneBillVO.setColumnType(this.getColumnType());
		cloneBillVO.setDatas(this.getDatas()); //真正的数据,有可能还是会产生引用
		cloneBillVO.setEditType(this.getEditType());
		cloneBillVO.setItemType(this.getItemType());
		cloneBillVO.setKeys(this.getKeys());
		cloneBillVO.setNames(this.getNames());
		cloneBillVO.setNeedSaves(this.getNeedSaves());
		cloneBillVO.setPkName(this.getPkName());
		cloneBillVO.setQueryTableName(this.getQueryTableName()); //
		cloneBillVO.setSaveTableName(this.getSaveTableName());
		cloneBillVO.setSequenceName(this.getSequenceName());
		cloneBillVO.setToStringFieldName(this.getToStringFieldName());
		return cloneBillVO;
	}

	public BillVO deepClone() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
			return (BillVO) in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	public boolean containsKey(String _key) {
		String[] allkeys = getKeys();
		for (int i = 0; i < allkeys.length; i++) {
			if (allkeys[i].equalsIgnoreCase(_key)) {
				return true;
			}
		}
		return false;
	}

	//如果设置了toString显示的列名,则显示之,否则显示第三列!!!!
	public String toString() {
		if (getToStringFieldName() == null) { //如果为空则取第3列,否则使用ToStringFieldName这个列的值，以后这个可以支持{}的宏代码计算!!!
			String[] str_keys = getKeys(); //
			if (str_keys.length > 3) {
				String key = str_keys[3]; //..
				Object obj = getObject(key); //..
				return obj == null ? "" : ("" + obj); //..
			} else {
				return "";
			}
		} else {
			Object obj = getObject(getToStringFieldName());
			return obj == null ? "" : ("" + obj);
		}
	}

	public String getToStringFieldName() {
		return toStringFieldName;
	}

	public void setToStringFieldName(String _toStringKeyName) {
		this.toStringFieldName = _toStringKeyName;
	}

	/**
	 * 
	 * @return
	 */
	public HashMap convertToHashMap() {
		String[] str_keys = getKeys();
		HashMap map = new HashMap();
		for (int i = 0; i < str_keys.length; i++) {
			map.put(str_keys[i], getStringValue(str_keys[i])); //
		}
		return map; //
	}

	/**
	 * 
	 * @return
	 */
	public HashVO convertToHashVO() {
		HashVO hvo = new HashVO(); //
		String[] str_keys = getKeys();
		for (int i = 0; i < str_keys.length; i++) {
			hvo.setAttributeValue(str_keys[i], getStringValue(str_keys[i])); //
		}
		HashMap billVOUserMap = this.getUserObjectMap(); //有自定义对象!!!
		if (billVOUserMap.size() > 0) {
			Iterator its = billVOUserMap.keySet().iterator(); //
			while (its.hasNext()) {
				String str_key = (String) its.next(); //
				String str_value = "" + billVOUserMap.get(str_key); //
				hvo.setAttributeValue(str_key, str_value); //
			}
		}
		return hvo; //
	}

	public HashVO convertViewToHashVO() {
		HashVO hvo = new HashVO(); //
		String[] str_keys = getKeys();
		for (int i = 0; i < str_keys.length; i++) {
			hvo.setAttributeValue(str_keys[i], getStringViewValue(str_keys[i])); //
		}
		return hvo; //
	}

	private TBUtil getTBUtil() {
		if (this.tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}

	public void setUserObject(Object _key, Object _value) {
		ht_userobject.put(_key, _value); //
	}

	public Object getUserObject(Object _key) {
		return ht_userobject.get(_key); //
	}

	/**
	 * 设置某个引用子表字段绑定的子表的数据VO..
	 * @param _itemKey
	 * @param _vos
	 */
	public void setLinkChildBillVOs(String _itemKey, BillVO[] _billVOs) {
		if (linkChildDataMap == null) {
			linkChildDataMap = new HashMap(); //
		}
		linkChildDataMap.put(_itemKey.toUpperCase(), _billVOs); //
	}

	/**
	 * 获得某个引用子表字段绑定的的BillVOs
	 * @param _itemkey
	 * @return
	 */
	public BillVO[] getLinkChildBillVos(String _itemkey) {
		if (linkChildDataMap == null) {
			return null; //
		}
		return (BillVO[]) linkChildDataMap.get(_itemkey.toUpperCase()); ////....
	}

	//xch,需要返回这个值!
	public HashMap getUserObjectMap() {
		return ht_userobject;
	}

	public String getTempletName() {
		return templetName;
	}

	public void setTempletName(String templetName) {
		this.templetName = templetName;
	}

	public int getModelRowNoInTree() {
		return modelRowNoInTree;
	}

	public void setModelRowNoInTree(int modelRowNo) {
		this.modelRowNoInTree = modelRowNo;
	}

	public int getModelRowNoInList() {
		return modelRowNoInList;
	}

	public void setModelRowNoInList(int modelRowNoInList) {
		this.modelRowNoInList = modelRowNoInList;
	}

	public int getModelRowNoInCard() {
		return modelRowNoInCard;
	}

	public void setModelRowNoInCard(int modelRowNoInCard) {
		this.modelRowNoInCard = modelRowNoInCard;
	}

	public boolean isVisible() {
		return isVisible;
	}

	//设置是否显示,用于数据权限!!!
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isVirtualNode() {
		return isVirtualNode;
	}

	public void setVirtualNode(boolean isVirtualNode) {
		this.isVirtualNode = isVirtualNode;
	}

}
