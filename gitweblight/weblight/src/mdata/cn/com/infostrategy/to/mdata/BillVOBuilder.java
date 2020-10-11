package cn.com.infostrategy.to.mdata;

import java.io.Serializable;
import java.util.HashMap;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTConstants;

/**
 * BillVO构造器,之所以提出这个类是因为性能问题!!
 * 因为BillVO这个对象包含的信息非常大,如果远程访问返回大量BillVO,则会发生严重的性能问题,但有时从开发方便角度讲又必须需要BillVO
 * 所有正确的做法是远程调用时返回一个TableDataStruct与Pub_Templet_1VO,然后在客户端再根据Pub_Templet_1VO的定义构造出BillVO
 * 换句话说就是远程传输的是小对象,取到数据后再构造复杂对象，即在性能与API方便性方便取得平衡
 * 但这种方法也有个问题就是无法做复杂的加载公式!!比如setRefItemName()这种公式!!!但我感觉以后还是有更巧妙的办法彻底解决性能与加载公式之间的矛盾!!!!
 * @author xch
 *
 */
public class BillVOBuilder implements Serializable {

	private static final long serialVersionUID = 2211314700730510890L;

	public String str_rownumberMark = "_RECORD_ROW_NUMBER";
	private TableDataStruct tableDataStruct = null; //
	private Pub_Templet_1VO templetVO = null; //
	private HashMap map_key_pos = null; //

	private BillVO[] returnBillVOs = null; //

	public TableDataStruct getTableDataStruct() {
		return tableDataStruct;
	}

	public void setTableDataStruct(TableDataStruct tableDataStruct) {
		this.tableDataStruct = tableDataStruct;
	}

	public Pub_Templet_1VO getTempletVO() {
		return templetVO;
	}

	public void setTempletVO(Pub_Templet_1VO templetVO) {
		this.templetVO = templetVO;
	}

	private String findStringValue(int _row, String _itemkey) {
		if (map_key_pos == null) { //先把从SQL取到的实际数据的位置设好
			map_key_pos = new HashMap(); //
			for (int i = 0; i < tableDataStruct.getHeaderName().length; i++) {
				map_key_pos.put(tableDataStruct.getHeaderName()[i].toUpperCase(), new Integer(i)); //
			}
		}

		Integer li_pos = (Integer) map_key_pos.get(_itemkey.toUpperCase()); //
		if (li_pos == null) {
			return null;
		} else {
			return tableDataStruct.getBodyData()[_row][li_pos.intValue()]; //根据模板中的列名去实际数据中找到对位位置上的值!!
		}
	}

	/**
	 * 构造BillVO
	 * @return
	 */
	public BillVO[] builderBillVOs() {
		if (returnBillVOs != null) {
			return returnBillVOs;
		}

		if (getTableDataStruct() == null || getTableDataStruct().getBodyData() == null || getTempletVO() == null) {
			return null;
		}

		returnBillVOs = new BillVO[getTableDataStruct().getBodyData().length]; //遍历所有数据
		for (int r = 0; r < getTableDataStruct().getBodyData().length; r++) {
			returnBillVOs[r] = new BillVO(); //先创建,否则会出现空指针异常!!
			returnBillVOs[r].setTempletCode(templetVO.getTempletcode()); //
			returnBillVOs[r].setTempletName(templetVO.getTempletname()); //
			returnBillVOs[r].setQueryTableName(templetVO.getTablename());
			returnBillVOs[r].setSaveTableName(templetVO.getSavedtablename()); //
			returnBillVOs[r].setPkName(templetVO.getPkname()); // 主键字段名
			returnBillVOs[r].setSequenceName(templetVO.getPksequencename()); // 序列名

			int li_length = templetVO.getItemVos().length;
			// 所有ItemKey
			String[] all_Keys = new String[li_length + 1]; //
			all_Keys[0] = this.str_rownumberMark; // 行号
			for (int i = 1; i < all_Keys.length; i++) {
				all_Keys[i] = this.templetVO.getItemKeys()[i - 1];
			}

			//itemName
			String[] all_Names = new String[li_length + 1]; //
			all_Names[0] = " "; // 行号
			for (int i = 1; i < all_Names.length; i++) {
				all_Names[i] = this.templetVO.getItemNames()[i - 1];
			}

			//所有ItemType
			String[] all_Types = new String[li_length + 1]; //
			all_Types[0] = " "; // 行号
			for (int i = 1; i < all_Types.length; i++) {
				all_Types[i] = this.templetVO.getItemTypes()[i - 1];
			}

			//所有列类型
			String[] all_ColumnTypes = new String[li_length + 1]; //
			all_ColumnTypes[0] = "NUMBER"; // 行号
			for (int i = 1; i < all_ColumnTypes.length; i++) {
				all_ColumnTypes[i] = templetVO.getItemVos()[i - 1].getSavedcolumndatatype(); //
			}

			//是否需要保存
			boolean[] bo_isNeedSaves = new boolean[li_length + 1];
			bo_isNeedSaves[0] = false; // 行号
			for (int i = 1; i < bo_isNeedSaves.length; i++) {
				bo_isNeedSaves[i] = templetVO.getItemVos()[i - 1].isNeedSave();
			}

			returnBillVOs[r].setKeys(all_Keys); //
			returnBillVOs[r].setNames(all_Names); //
			returnBillVOs[r].setItemType(all_Types); // 控件类型!!
			returnBillVOs[r].setColumnType(all_ColumnTypes); // 数据库类型!!
			returnBillVOs[r].setNeedSaves(bo_isNeedSaves); // 是否需要保存!!

			//所有实际数据..
			Object[] obj_datas = new Object[li_length + 1]; // 
			obj_datas[0] = new RowNumberItemVO(RowNumberItemVO.INIT, r); //
			for (int i = 1; i < obj_datas.length; i++) {
				String str_item = findStringValue(r, all_Keys[i]); //
				if (str_item == null) { //如果为空,则返回空
					obj_datas[i] = null; //
				} else {
					if (all_Types[i].equals(WLTConstants.COMP_LABEL) || //
							all_Types[i].equals(WLTConstants.COMP_TEXTFIELD) || //
							all_Types[i].equals(WLTConstants.COMP_NUMBERFIELD) || //
							all_Types[i].equals(WLTConstants.COMP_PASSWORDFIELD) || //
							all_Types[i].equals(WLTConstants.COMP_TEXTAREA) || //
							all_Types[i].equals(WLTConstants.COMP_BUTTON)) {
						obj_datas[i] = new StringItemVO(str_item); //
					} else if (all_Types[i].equals(WLTConstants.COMP_COMBOBOX)) { //下拉框
						ComBoxItemVO matchVO = findComBoxItemVO(templetVO.getItemVo(all_Keys[i]).getComBoxItemVos(), str_item); //
						if (matchVO != null) {
							obj_datas[i] = matchVO;
						} else {
							obj_datas[i] = new ComBoxItemVO(str_item, null, str_item); // 下拉框VO
						}
					} else if (all_Types[i].equals(WLTConstants.COMP_REFPANEL) || //参照
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_TREE) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_MULTI) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_CUST) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_REGEDIT)) {
						obj_datas[i] = new RefItemVO(str_item, null, str_item); //
					} else if (all_Types[i].equals(WLTConstants.COMP_DATE) || all_Types[i].equals(WLTConstants.COMP_DATETIME)) { //日历与时间
						obj_datas[i] = new RefItemVO(str_item, null, str_item); //
					} else if (all_Types[i].equals(WLTConstants.COMP_BIGAREA) || //
							all_Types[i].equals(WLTConstants.COMP_FILECHOOSE) || //
							all_Types[i].equals(WLTConstants.COMP_COLOR) || //
							all_Types[i].equals(WLTConstants.COMP_CALCULATE) || //
							all_Types[i].equals(WLTConstants.COMP_PICTURE) || //
							all_Types[i].equals(WLTConstants.COMP_LINKCHILD)  //
							) {
						obj_datas[i] = new RefItemVO(str_item, null, str_item); //
					} else if (all_Types[i].equals(WLTConstants.COMP_EXCEL)) { //Excel
						obj_datas[i] = new RefItemVO(str_item, null, (str_item.indexOf("#") > 0 ? str_item.substring(str_item.indexOf("#") + 1, str_item.length()) : "点击查看Excel数据")); //
					} else if (all_Types[i].equals(WLTConstants.COMP_OFFICE)) { //Excel
						if (str_item.contains("_") || !str_item.contains(".")) {
							obj_datas[i] = new RefItemVO(str_item, null, "点击查看"); //
						} else {
							String name = str_item.substring(0, str_item.lastIndexOf("."));
							String type = str_item.substring(str_item.lastIndexOf("."));
							obj_datas[i] = new RefItemVO(str_item, null, new TBUtil().convertHexStringToStr(name) + type); //
						}
					} else {
						obj_datas[i] = new StringItemVO(str_item); //
					}
				}

			}
			returnBillVOs[r].setDatas(obj_datas); // 设置真正的数据!!!!
			returnBillVOs[r].setToStringFieldName(templetVO.getTreeviewfield()); //
		}

		return returnBillVOs; //
	}

	private ComBoxItemVO findComBoxItemVO(ComBoxItemVO[] _vos, String _id) {
		if (_vos == null || _vos.length == 0) {
			return null;
		}
		for (int k = 0; k < _vos.length; k++) {
			if (_vos[k].getId() != null && _vos[k].getId().equals(_id)) { //如果下拉框的主键不为空!!
				return _vos[k];
			}
		}
		return null;
	}

}
