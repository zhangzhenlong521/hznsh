/**************************************************************************
 * $RCSfile: BillListModel.java,v $  $Revision: 1.7 $  $Date: 2012/10/09 02:50:34 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.util.HashMap;

import javax.swing.table.DefaultTableModel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class BillListModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	private BillListPanel billListPanel = null;
	private Pub_Templet_1VO templetVO = null;

	private Pub_Templet_1_ItemVO[] templetItemVOs = null;

	public static String str_rownumberMark = "_RECORD_ROW_NUMBER";

	private TBUtil tBUtil = null; //转换工具!!

	public BillListModel(Object[][] data, Object[] columnNames, Pub_Templet_1VO _templetVO) {
		super(data, columnNames);
		this.templetVO = _templetVO;
		this.templetItemVOs = _templetVO.getItemVos();
	}

	public Object getValueAt(int _row, String _key) {
		int li_index = findModelIndex(_key);
		if (li_index >= 0) {
			return getValueAt(_row, li_index); //
		}
		return null;
	}

	public HashMap getValueAtRowWithHashMap(int _row) {
		HashMap map = new HashMap(); //
		map.put(str_rownumberMark, getValueAt(_row, 0)); //
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			String str_key = templetItemVOs[i].getItemkey();
			Object obj = getValueAt(_row, str_key);
			map.put(str_key, obj);
		}
		return map;
	}

	public VectorMap getValueAtRowWithVectorMap(int _row) {
		VectorMap map = new VectorMap(); //
		map.put(str_rownumberMark, getValueAt(_row, 0)); //
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			String str_key = templetItemVOs[i].getItemkey();
			Object obj = getValueAt(_row, str_key);
			map.put(str_key, obj);
		}
		return map;
	}

	public VectorMap getValueAtModelWithVectorMap(int _row) {
		VectorMap map = new VectorMap(); //
		for (int i = 0; i < templetVO.getRealViewItemVOs().length; i++) {
			String str_key = templetVO.getRealViewItemVOs()[i].getItemkey().toLowerCase();
			Object obj = getValueAt(_row, str_key);
			if (obj == null) {
				obj = "";
			}
			map.put(str_key, obj);
		}
		return map;
	}

	public VectorMap getSavedValueAtModelWithVectorMap(int _row) {
		VectorMap map = new VectorMap(); //
		for (int i = 0; i < templetVO.getRealViewItemVOs().length; i++) {
			if (!templetVO.getRealViewItemVOs()[i].getIssave().booleanValue())
				continue;
			String str_key = templetVO.getRealViewItemVOs()[i].getItemkey().toLowerCase();
			Object obj = getValueAt(_row, str_key);
			if (obj == null) {
				obj = "";
			}
			map.put(str_key, obj);
		}
		return map;
	}

	public Object[][] getValueAtAll() {
		int li_rowcount = getRowCount();
		int li_colcount = getColumnCount();
		Object[][] objs = new Object[li_rowcount][li_colcount];
		for (int i = 0; i < li_rowcount; i++) {
			for (int j = 0; j < objs[i].length; j++) {
				objs[i][j] = getValueAt(i, j);
			}
		}
		return objs;
	}

	public Object[] getValueAtRow(int _row) {
		Object[] objs = new Object[templetItemVOs.length + 1];
		for (int i = 0; i < objs.length; i++) {
			objs[i] = getValueAt(_row, i);
		}
		return objs;
	}

	public String getRealValueAtModel(int _row, int _col) {
		Object obj = getValueAt(_row, _col);
		return getObjectRealValue(obj);
	}

	public String getRealValueAtModel(int _row, String _key) {
		int li_pos = _key.indexOf(".");
		String str_itemkey = null;
		String str_subfix = null;
		if (li_pos > 0) {
			str_itemkey = _key.substring(0, li_pos); //
			str_subfix = _key.substring(li_pos + 1, _key.length()); //
		} else {
			str_itemkey = _key;
		}

		Object obj = getValueAt(_row, str_itemkey);
		if (li_pos > 0) { //如果有后辍
			if (obj instanceof ComBoxItemVO) {
				return ((ComBoxItemVO) obj).getItemValue(str_subfix); //
			} else if (obj instanceof RefItemVO) {
				return ((RefItemVO) obj).getItemValue(str_subfix); //
			} else {
				return ""; //
			}
		} else {
			return getObjectRealValue(obj);
		}
	}

	public String[][] getRealValueAtModel() {
		Object[][] allObjects = getValueAtAll();
		if (allObjects.length <= 0) {
			return null;
		}

		String[][] str_data = new String[allObjects.length][allObjects[0].length];
		for (int i = 0; i < str_data.length; i++) {
			for (int j = 0; j < str_data[0].length; j++) {
				str_data[i][j] = getObjectRealValue(allObjects[i][j]);
			}
		}
		return str_data;
	}

	private String getObjectRealValue(Object _obj) {
		if (_obj == null) {
			return null;
		}

		if (_obj instanceof String) {
			return (String) _obj;
		} else if (_obj instanceof StringItemVO) {
			return ((StringItemVO) _obj).getStringValue();
		} else if (_obj instanceof ComBoxItemVO) {
			ComBoxItemVO vo = (ComBoxItemVO) _obj;
			return vo.getId();
		} else if (_obj instanceof RefItemVO) {
			RefItemVO vo = (RefItemVO) _obj;
			return vo.getId();
		} else {
			return _obj.toString();
		}
	}

	public void setValueAt(Object aValue, int row, int column) {
		super.setValueAt(aValue, row, column); //	
	}

	public void setValueAt(Object _obj, int _row, String _key) {
		int li_index = findModelIndex(_key);
		if (li_index >= 0) {
			this.setValueAt(_obj, _row, li_index);
		}
	}

	//要补充一下
	public void setRealValueAt(String _value, int _row, String _key) {
		int li_index = findModelIndex(_key); //取得第几列!!
		if (li_index >= 0) {
			String str_type = getItemType(_key);
			if (str_type.equals(WLTConstants.COMP_TEXTFIELD)) { //文本框
				this.setValueAt(new StringItemVO(_value), _row, li_index);
			} else if (str_type.equals(WLTConstants.COMP_NUMBERFIELD)) { //数字框
				this.setValueAt(new StringItemVO(_value), _row, li_index); //
			} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //如果是下拉框!!
				ComBoxItemVO[] comItemVOs = getTempletItemVO(_key).getComBoxItemVos();
				if (comItemVOs != null) { //如果不为空
					for (int i = 0; i < comItemVOs.length; i++) {
						if (comItemVOs[i].getId().equals(_value)) {
							this.setValueAt(comItemVOs[i], _row, li_index); //
							return;
						}
					}
				} else { //如果为空!
					if (_value != null) {
						this.setValueAt(new ComBoxItemVO(_value, _value, _value), _row, li_index); //
					}
				}
			} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //
					str_type.equals(WLTConstants.COMP_DATE) || // 日历
					str_type.equals(WLTConstants.COMP_DATETIME) || // 时间
					str_type.equals(WLTConstants.COMP_FILECHOOSE) || // 文件选择框
					str_type.equals(WLTConstants.COMP_COLOR) || // 颜色选择框
					str_type.equals(WLTConstants.COMP_BIGAREA) || // 大文本框
					str_type.equals(WLTConstants.COMP_PICTURE) || // 图标选择框
					str_type.equals(WLTConstants.COMP_LINKCHILD) // 图标选择框
			) // 留言板
			{ //如果是各种参照1
				if (getValueAt(_row, li_index) == null) {
					if(str_type.equals(WLTConstants.COMP_PICTURE)){// 图标选择框)
						this.setValueAt(UIUtil.getImageFromServerRespath(_value),_row,li_index);
					} else{
						this.setValueAt(new RefItemVO(_value, _value, _value), _row, li_index); //如果为空则,直接设置!!
					}
					
				} else {
					if(str_type.equals(WLTConstants.COMP_PICTURE)){// 图标选择框)
						this.setValueAt(UIUtil.getImageFromServerRespath(_value),_row,li_index);
					} else{
						RefItemVO refitemVO = (RefItemVO) getValueAt(_row, li_index); //
						refitemVO.setId(_value); //
					}
				}
			} else { //如果是其他控件!!
				this.setValueAt(new StringItemVO(_value), _row, li_index);
			}
		}
	}

	public void setValueAtAll(Object[][] _data) {
		for (int i = 0; i < _data.length; i++) {
			int row = addEmptyRow();
			for (int j = 0; j < this.getColumnCount(); j++) {
				setValueAt(_data[i][j], row, j);
			}
		}
	}

	public int findModelIndex(String _key) {
		if (_key.equalsIgnoreCase(str_rownumberMark)) { // 如果是行号则直接返回
			return 0;
		}

		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(_key)) {
				return i + 1;
			}
		}
		return -1;
	}

	public int addEmptyRow() {
		int li_colcount = getColumnCount();
		Object[] allobjs = new Object[li_colcount];
		//allobjs[0] = new RowNumberItemVO("INSERT", getRowCount()); //
		addRow(allobjs);
		int li_newRow = getRowCount() - 1; //
		setValueAt(new RowNumberItemVO("INSERT", li_newRow), li_newRow, this.str_rownumberMark);
		return li_newRow;

	}

	public int insertEmptyRow(int _row) {
		int li_colcount = getColumnCount();
		Object[] allobjs = new Object[li_colcount];
		//allobjs[0] = new RowNumberItemVO("INSERT", _row); //
		insertRow(_row, allobjs);
		setValueAt(new RowNumberItemVO("INSERT", _row), _row, this.str_rownumberMark);
		return _row; //

	}

	public void insertRow(int _row, BillVO _billVO) {
		String[] str_keys = _billVO.getKeys(); //
		int li_newRow = insertEmptyRow(_row); //先插入一个新空行，并返回新增行的行号!
		for (int i = 0; i < str_keys.length; i++) {
			setValueAt(_billVO.getObject(str_keys[i]), li_newRow, str_keys[i]);
		}
	}

	public void addRow(HashMap _map) {
		String[] str_keys = (String[]) _map.keySet().toArray(new String[0]);
		int li_newRow = addEmptyRow();
		//setValueAt(new RowNumberItemVO("INSERT", li_newRow), li_newRow, this.str_rownumberMark);
		for (int i = 0; i < str_keys.length; i++) {
			setValueAt(_map.get(str_keys[i]), li_newRow, str_keys[i]);
		}
	}

	public void addRow(VectorMap _map) {
		String[] str_keys = _map.getKeysAsString();
		int li_newRow = addEmptyRow();
		//setValueAt(new RowNumberItemVO("INSERT", li_newRow), li_newRow, this.str_rownumberMark);
		for (int i = 0; i < str_keys.length; i++) {
			setValueAt(_map.get(str_keys[i]), li_newRow, str_keys[i]);
		}
	}

	public void addRow(BillVO _billVO) {
		String[] str_keys = _billVO.getKeys(); //
		int li_newRow = addEmptyRow();
		//setValueAt(new RowNumberItemVO("INSERT", li_newRow), li_newRow, this.str_rownumberMark);
		for (int i = 0; i < str_keys.length; i++) {
			setValueAt(_billVO.getObject(str_keys[i]), li_newRow, str_keys[i]);
		}
	}

	public Pub_Templet_1_ItemVO[] getTempletItemVOs() {
		return templetItemVOs;
	}

	private Pub_Templet_1_ItemVO getTempletItemVO(String _itemKey) {
		Pub_Templet_1_ItemVO[] vos = getTempletItemVOs();
		for (int i = 0; i < vos.length; i++) {
			if (vos[i].getItemkey().equalsIgnoreCase(_itemKey)) {
				return vos[i];
			}
		}
		return null;
	}

	public Pub_Templet_1VO getTempletVO() {
		return templetVO;
	}

	public boolean containsItemKey(String _itemKey) {
		return this.getTempletVO().containsItemKey(_itemKey);
	}

	private String getItemType(String _itemKey) {
		String[] str_keys = templetVO.getItemKeys(); //
		String[] str_itemTypes = templetVO.getItemTypes(); //
		for (int i = 0; i < str_keys.length; i++) {
			if (str_keys[i].equals(_itemKey)) {
				return str_itemTypes[i]; //
			}
		}
		return null;
	}

	/**
	 * 得到数据源名称
	 * @return
	 */
	public String getDataSourceName() {
		if (templetVO.getDatasourcename() == null || templetVO.getDatasourcename().trim().equals("null") || templetVO.getDatasourcename().trim().equals("")) {
			return ClientEnvironment.getInstance().getDefaultDataSourceName(); // 默认数据源
		} else {
			return getTBUtil().convertDataSourceName(ClientEnvironment.getInstance().getCurrSessionVO(), templetVO.getDatasourcename()); // 算出数据源!!
		}
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}

	public BillListPanel getBillListPanel() {
		return billListPanel;
	}

	public void setBillListPanel(BillListPanel billListPanel) {
		this.billListPanel = billListPanel;
	}
}
