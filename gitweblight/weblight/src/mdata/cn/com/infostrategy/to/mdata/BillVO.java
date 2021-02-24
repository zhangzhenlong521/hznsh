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

	private String templetCode = null; //ģ�����,����ɲ���!!
	private String templetName = null; //ģ������,����ɲ���!!
	private int modelRowNoInTree = -1; //�����ݶ�Ӧ��Model�����еĵڼ���,Ŀǰ��BillTree�ؼ����õõ�,��ΪBillTree�������Ǵ洢��һ����ά������,��ͨ���к����󶨵ġ�Ϊ���������,BillList��BillCard�Ժ�Ҳ���Կ���ʹ�����ַ�������������������Ǻ��б�Ҫ��!
	private int modelRowNoInList = -1; //���б�ģ���е�λ��
	private int modelRowNoInCard = -1; //�ڿ�Ƭģ���е�λ��

	// �Ĵ�ؼ�����!!
	private String queryTableName = null; // ��ѯ����
	private String saveTableName = null; // �������!!

	private String pkName = null; // �����ֶ���
	private String sequenceName = null; // ������

	// �����Ķ���!!!
	private Object[] datas = null; // �����Ķ���

	// �������!!!���ܶ���ȥ��,��Ϊ���ǵ����л�ʱ������,���ṩ������datas��ȡ
	private String[] keys = null; // ����,��Ӧ��SQL�е�key!!
	private String[] names = null; // ����,��Ӧ��SQL�е�key!!
	private String[] itemType = null; // �ؼ�����
	private String[] columnType = null; // �����е�����
	private boolean[] needSave = null; // �Ƿ���Ҫ����!!!

	private String toStringFieldName; //toString��ʾ��Key������!!
	private HashMap linkChildDataMap = null; //�洢�����ӱ����ݵ�Map
	private HashMap ht_userobject = new HashMap(); //

	private boolean isVisible = true; //�Ƿ���ʾ!!�������ݶ��󲻴����Ƿ���ʾ������,�����ǵ�����Ȩ�����ʱ�ǳ���Ҫ����ĳ������,���������һ�������򷽱�Ķ�!
	private boolean isEnabled = true; //�Ƿ���Ч,������Ȩ�����ʱ,������ʱ���Բ鿴�嵥,�����ܵ���鿴��ϸ,��visible=true,��enabled=false,��ֻ�ܿ����ܶ�!!!!���Խ����˿ؼ��е�������������!
	private boolean isVirtualNode = false; //�Ƿ�������VO?���������ͽṹ������������ĸ���,Ϊ����õ����,��BillVO��Ҳ���Ӹ�����,�Ķ���С,Ҳ��ʵ����ǿ����չ!��xch/2012-04-22��

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
	 * ȡ������..
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
	 * ȡ������..
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
	 * ȡ������..
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
	 * ȡ����ʾ����!!!����ʾʱ������ʲô???
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
			if ("�����ӱ�".equals(getItemTypeByKey(_key)) || "�����ӱ�".equals(getItemTypeByKey(_key))) { //����������ӱ�,��Ƚ�����,��Ҫ�ҳ������ӱ������!
				BillVO[] childVOs = getLinkChildBillVos(_key); //�����ӱ������!!!
				if (childVOs == null || childVOs.length <= 0) {
					return null;
				} else {
					StringBuilder sb_text = new StringBuilder(); //
					for (int i = 0; i < childVOs.length; i++) { //������������!!!
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
	 * ȡ������..
	 * @param _key
	 * @return
	 */
	public String getStringValue(int _index) {
		StringItemVO strItemVO = (StringItemVO) getObject(_index); //
		return strItemVO == null ? null : strItemVO.getStringValue(); //
	}

	/**
	 * ȡ������..
	 * @param _key
	 * @return
	 */
	public ComBoxItemVO getComBoxItemVOValue(String _key) {
		return (ComBoxItemVO) getObject(_key);
	}

	/**
	 * ȡ������..
	 * @param _key
	 * @return
	 */
	public RefItemVO getRefItemVOValue(String _key) {
		return (RefItemVO) getObject(_key);
	}

	/**
	 * ��������..
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

	//ȡ��ĳһ�������,����������Ҫ�������,���Լ���!��xch/2012-11-13��
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
	 * ���ص�ǰ�༭״̬!!
	 * 
	 * @return
	 */
	public String getEditType() {
		RowNumberItemVO rowNumberVO = (RowNumberItemVO) getDatas()[0]; // �к�VO!!!��Զ�ڵ�һ��!!
		return rowNumberVO.getState(); //
	}

	/**
	 * ���ص�ǰ�༭״̬!!
	 * 
	 * @return
	 */
	public void setEditType(String _type) {
		RowNumberItemVO rowNumberVO = (RowNumberItemVO) getDatas()[0]; // �к�VO!!!��Զ�ڵ�һ��!!
		rowNumberVO.setState(_type);
	}

	/**
	 * ���ص�ǰ�༭״̬!!
	 * 
	 * @return
	 */
	public RowNumberItemVO getRowNumberItemVO() {
		RowNumberItemVO rowNumberVO = (RowNumberItemVO) getDatas()[0]; // �к�VO!!!��Զ�ڵ�һ��!!
		return rowNumberVO; //
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	// ���¶�����Ҫ��������ݵ���,����,�ؼ�����,�������͵�
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
	 * �õ���Ҫ�������ݵ�ֱ������Oracle�е�ʵ��ֵ,����Ҫ�Ƕ�������VO�����VO������һ�´���,�����Ķ���ֱ���� toString����!!
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

	//����Insert���...
	public String getInsertSQL() {
		return getInsertSQL(null); //
	}

	//����Insert���
	public String getInsertSQL(String[] _encryptKeys) {
		String[] str_keys = getNeedSaveKeys(); //ȡ��������Ҫ���뱣�����!!
		String[] str_realValue = getNeedSaveDataRealValue(); //
		InsertSQLBuilder isql = new InsertSQLBuilder(getSaveTableName()); //
		for (int i = 0; i < str_keys.length; i++) {
			if (_encryptKeys != null && getTBUtil().isExistInArray(str_keys[i], _encryptKeys, true) && str_realValue[i] != null && !str_realValue[i].trim().equals("")) { //�������ֶ���Ҫ����!!!
				isql.putFieldValue(str_keys[i], encryptStr(str_realValue[i])); //���ܴ�������!��DES�����㷨!!!
			} else {
				isql.putFieldValue(str_keys[i], str_realValue[i]); //��Ҫ�����Ƿ���ܴ���!!
			}
		}
		return isql.getSQL(); //
	}

	//����Update���....
	public String getUpdateSQL() {
		return getUpdateSQL(null); //
	}

	//����Update���!!
	public String getUpdateSQL(String[] _encryptKeys) {
		String[] str_keys = getNeedSaveKeys(); //
		String[] str_realValue = getNeedSaveDataRealValue(); // ȡ��������ֵ!!!!
		//DESKeyTool desTool = new DESKeyTool(); //���ܹ���
		UpdateSQLBuilder isql = new UpdateSQLBuilder(getSaveTableName(), getUpdateWhereCondition()); //
		for (int i = 0; i < str_keys.length; i++) {
			if (_encryptKeys != null && getTBUtil().isExistInArray(str_keys[i], _encryptKeys, true) && str_realValue[i] != null && !str_realValue[i].trim().equals("")) { //�������ֶ���Ҫ����!!!
				isql.putFieldValue(str_keys[i], encryptStr(str_realValue[i])); //���ܴ�������!��DES�����㷨!!!
			} else {
				isql.putFieldValue(str_keys[i], str_realValue[i]); //
			}
		}
		return isql.getSQL(); //
	}

	/**
	 * ȡ��ɾ����SQL!!!!!!
	 * 
	 * @return
	 */
	public String getDeleteSQL() {
		return "delete from " + getSaveTableName() + " where " + getUpdateWhereCondition(); //
	}

	public String getPkValue() {
		return findPkValue();
	}

	//��������!!
	private String encryptStr(String _str) {
		try {
			if (getTBUtil().getJVSite() == TBUtil.JVMSITE_CLIENT) { //������ڿͻ��˵���,��Զ������һ�£���Ϊ�ϵ�JREû��JCE�İ�!!!
				return UIUtil.getCommonService().encryptStr(_str); //
			} else {
				return new DESKeyTool().encrypt(_str); //����������ֱ�Ӽ���!!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * �ҵ�������ֵ
	 * 
	 * @return
	 */
	public String findPkValue() {
		if (pkName == null || pkName.trim().equals("")) {
			return ""; //
		} else { //
			// ȥ���пؼ�����,����ҵ��򷵻�֮
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].equalsIgnoreCase(pkName)) {
					return "" + ((datas[i] == null) ? "" : datas[i]); //
				}
			} // ..
			return ""; // ���û�ҵ��򷵻ؿ�
		}
	} // ....

	/**
	 * ƴ����updateʱwhere���������!!!ר��Ϊ��update����ʱ��!!! ��ʱֻҪ�� where �������ָʾ��Щ��������!!!
	 */
	public String getUpdateWhereCondition() {
		String str_return = " ";
		if (pkName != null && !pkName.trim().equals("")) { // ���������Ϊnull,��ͬʱ���ǿ��ַ���!!!!!������������!!!
			str_return = str_return + pkName + "='" + findPkValue() + "' "; // //..
		}
		return str_return;
	}

	/**
	 * ȡ��Where����..
	 * @return
	 */
	public String getWhereCondition() {
		return getUpdateWhereCondition();
	}

	/**
	 * �滻SQL�еĵ�����,��Ϊ�����Żᵼ�±���ʧ��!!
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
	 * �滻�ַ�
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
			} // ����Ҳ���,�򷵻�
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // ������ַ�������ԭ��ǰ�
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // ��ʣ��ļ���
		return str_return;
	}

	/**
	 * �ж��Ƿ�Ҫ����汾��,���Ƿ���Ҫ�����ֹ���!!!
	 * ��Ҫ�����ֹ�����ǰ����,������һ���ֶν�version,���Ҹ����ǲ��뱣���!!!
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
	 * ȡ�õ�ǰ�汾��!!
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
	 * ����BillVO����ת����ĳһ�������ʵ����!!�����ʵ������ǰ���set,get������һЩʵ����!!������������һЩAPI��ɷ�������!!
	 * �÷�����:
	 * gxlu.nova.system.login.vo.TestRealVO realVO = (gxlu.nova.system.login.vo.TestRealVO) billVO.convertToRealOBJ(gxlu.nova.system.login.vo.TestRealVO.class); //
	 * 
	 * @param _class ʵ�ʵ�����
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object convertToRealOBJ(java.lang.Class _class) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object returnobj = _class.newInstance(); //����ʵ��!!
		Method[] methods = _class.getMethods(); //ȡ�����з���!!!�������и����!!!
		String[] all_keys = this.getKeys();
		for (int i = 0; i < all_keys.length; i++) {
			for (int j = 0; j < methods.length; j++) {
				Class[] parClass = methods[j].getParameterTypes();
				//System.out.println("������:" + methods[j].getName());
				if (methods[j].getName().equalsIgnoreCase("set" + all_keys[i]) && parClass.length == 1) { //�÷�������������set��ͷ,��set����������������Ǹ�Ū������,��������ֻ��һ������!!
					//System.out.println("�ҵ���Ӧ����:" + methods[j].getName()); //
					String str_realvalue = getRealValue(all_keys[i]); //ȡ��������ֵ
					if (str_realvalue == null) { //����ǿն���
						methods[j].invoke(returnobj, new Object[] { null }); //����ֵ!!
					} else { //
						Object setObject = null;
						if (parClass[0].equals(java.lang.String.class)) { //�����String����
							setObject = str_realvalue;
						} else if (parClass[0].equals(java.lang.Integer.class)) { //�����Integer����
							setObject = new Integer(str_realvalue); //
						} else if (parClass[0].equals(java.lang.Long.class)) { //�����Long����
							setObject = new java.lang.Long(str_realvalue); //
						} else if (parClass[0].equals(java.lang.Double.class)) { //�����Double����
							setObject = new java.lang.Double(str_realvalue); //
						} else if (parClass[0].equals(java.math.BigDecimal.class)) { //�����BigDecimal����
							setObject = new java.math.BigDecimal(str_realvalue); //
						} else {
							setObject = str_realvalue; //
						}

						methods[j].invoke(returnobj, new Object[] { setObject }); //����ֵ!!!
					}
					break; //�ж��ڲ�ѭ��!!�ύЧ��,���ҵ���Ͳ���������!!
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
		cloneBillVO.setDatas(this.getDatas()); //����������,�п��ܻ��ǻ��������
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

	//���������toString��ʾ������,����ʾ֮,������ʾ������!!!!
	public String toString() {
		if (getToStringFieldName() == null) { //���Ϊ����ȡ��3��,����ʹ��ToStringFieldName����е�ֵ���Ժ��������֧��{}�ĺ�������!!!
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
		HashMap billVOUserMap = this.getUserObjectMap(); //���Զ������!!!
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
	 * ����ĳ�������ӱ��ֶΰ󶨵��ӱ������VO..
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
	 * ���ĳ�������ӱ��ֶΰ󶨵ĵ�BillVOs
	 * @param _itemkey
	 * @return
	 */
	public BillVO[] getLinkChildBillVos(String _itemkey) {
		if (linkChildDataMap == null) {
			return null; //
		}
		return (BillVO[]) linkChildDataMap.get(_itemkey.toUpperCase()); ////....
	}

	//xch,��Ҫ�������ֵ!
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

	//�����Ƿ���ʾ,��������Ȩ��!!!
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
