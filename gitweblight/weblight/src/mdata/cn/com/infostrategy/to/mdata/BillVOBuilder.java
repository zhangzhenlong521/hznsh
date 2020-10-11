package cn.com.infostrategy.to.mdata;

import java.io.Serializable;
import java.util.HashMap;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTConstants;

/**
 * BillVO������,֮����������������Ϊ��������!!
 * ��ΪBillVO��������������Ϣ�ǳ���,���Զ�̷��ʷ��ش���BillVO,��ᷢ�����ص���������,����ʱ�ӿ�������ǶȽ��ֱ�����ҪBillVO
 * ������ȷ��������Զ�̵���ʱ����һ��TableDataStruct��Pub_Templet_1VO,Ȼ���ڿͻ����ٸ���Pub_Templet_1VO�Ķ��幹���BillVO
 * ���仰˵����Զ�̴������С����,ȡ�����ݺ��ٹ��츴�Ӷ��󣬼���������API�����Է���ȡ��ƽ��
 * �����ַ���Ҳ�и���������޷������ӵļ��ع�ʽ!!����setRefItemName()���ֹ�ʽ!!!���Ҹо��Ժ����и�����İ취���׽����������ع�ʽ֮���ì��!!!!
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
		if (map_key_pos == null) { //�ȰѴ�SQLȡ����ʵ�����ݵ�λ�����
			map_key_pos = new HashMap(); //
			for (int i = 0; i < tableDataStruct.getHeaderName().length; i++) {
				map_key_pos.put(tableDataStruct.getHeaderName()[i].toUpperCase(), new Integer(i)); //
			}
		}

		Integer li_pos = (Integer) map_key_pos.get(_itemkey.toUpperCase()); //
		if (li_pos == null) {
			return null;
		} else {
			return tableDataStruct.getBodyData()[_row][li_pos.intValue()]; //����ģ���е�����ȥʵ���������ҵ���λλ���ϵ�ֵ!!
		}
	}

	/**
	 * ����BillVO
	 * @return
	 */
	public BillVO[] builderBillVOs() {
		if (returnBillVOs != null) {
			return returnBillVOs;
		}

		if (getTableDataStruct() == null || getTableDataStruct().getBodyData() == null || getTempletVO() == null) {
			return null;
		}

		returnBillVOs = new BillVO[getTableDataStruct().getBodyData().length]; //������������
		for (int r = 0; r < getTableDataStruct().getBodyData().length; r++) {
			returnBillVOs[r] = new BillVO(); //�ȴ���,�������ֿ�ָ���쳣!!
			returnBillVOs[r].setTempletCode(templetVO.getTempletcode()); //
			returnBillVOs[r].setTempletName(templetVO.getTempletname()); //
			returnBillVOs[r].setQueryTableName(templetVO.getTablename());
			returnBillVOs[r].setSaveTableName(templetVO.getSavedtablename()); //
			returnBillVOs[r].setPkName(templetVO.getPkname()); // �����ֶ���
			returnBillVOs[r].setSequenceName(templetVO.getPksequencename()); // ������

			int li_length = templetVO.getItemVos().length;
			// ����ItemKey
			String[] all_Keys = new String[li_length + 1]; //
			all_Keys[0] = this.str_rownumberMark; // �к�
			for (int i = 1; i < all_Keys.length; i++) {
				all_Keys[i] = this.templetVO.getItemKeys()[i - 1];
			}

			//itemName
			String[] all_Names = new String[li_length + 1]; //
			all_Names[0] = " "; // �к�
			for (int i = 1; i < all_Names.length; i++) {
				all_Names[i] = this.templetVO.getItemNames()[i - 1];
			}

			//����ItemType
			String[] all_Types = new String[li_length + 1]; //
			all_Types[0] = " "; // �к�
			for (int i = 1; i < all_Types.length; i++) {
				all_Types[i] = this.templetVO.getItemTypes()[i - 1];
			}

			//����������
			String[] all_ColumnTypes = new String[li_length + 1]; //
			all_ColumnTypes[0] = "NUMBER"; // �к�
			for (int i = 1; i < all_ColumnTypes.length; i++) {
				all_ColumnTypes[i] = templetVO.getItemVos()[i - 1].getSavedcolumndatatype(); //
			}

			//�Ƿ���Ҫ����
			boolean[] bo_isNeedSaves = new boolean[li_length + 1];
			bo_isNeedSaves[0] = false; // �к�
			for (int i = 1; i < bo_isNeedSaves.length; i++) {
				bo_isNeedSaves[i] = templetVO.getItemVos()[i - 1].isNeedSave();
			}

			returnBillVOs[r].setKeys(all_Keys); //
			returnBillVOs[r].setNames(all_Names); //
			returnBillVOs[r].setItemType(all_Types); // �ؼ�����!!
			returnBillVOs[r].setColumnType(all_ColumnTypes); // ���ݿ�����!!
			returnBillVOs[r].setNeedSaves(bo_isNeedSaves); // �Ƿ���Ҫ����!!

			//����ʵ������..
			Object[] obj_datas = new Object[li_length + 1]; // 
			obj_datas[0] = new RowNumberItemVO(RowNumberItemVO.INIT, r); //
			for (int i = 1; i < obj_datas.length; i++) {
				String str_item = findStringValue(r, all_Keys[i]); //
				if (str_item == null) { //���Ϊ��,�򷵻ؿ�
					obj_datas[i] = null; //
				} else {
					if (all_Types[i].equals(WLTConstants.COMP_LABEL) || //
							all_Types[i].equals(WLTConstants.COMP_TEXTFIELD) || //
							all_Types[i].equals(WLTConstants.COMP_NUMBERFIELD) || //
							all_Types[i].equals(WLTConstants.COMP_PASSWORDFIELD) || //
							all_Types[i].equals(WLTConstants.COMP_TEXTAREA) || //
							all_Types[i].equals(WLTConstants.COMP_BUTTON)) {
						obj_datas[i] = new StringItemVO(str_item); //
					} else if (all_Types[i].equals(WLTConstants.COMP_COMBOBOX)) { //������
						ComBoxItemVO matchVO = findComBoxItemVO(templetVO.getItemVo(all_Keys[i]).getComBoxItemVos(), str_item); //
						if (matchVO != null) {
							obj_datas[i] = matchVO;
						} else {
							obj_datas[i] = new ComBoxItemVO(str_item, null, str_item); // ������VO
						}
					} else if (all_Types[i].equals(WLTConstants.COMP_REFPANEL) || //����
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_TREE) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_MULTI) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_CUST) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //
							all_Types[i].equals(WLTConstants.COMP_REFPANEL_REGEDIT)) {
						obj_datas[i] = new RefItemVO(str_item, null, str_item); //
					} else if (all_Types[i].equals(WLTConstants.COMP_DATE) || all_Types[i].equals(WLTConstants.COMP_DATETIME)) { //������ʱ��
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
						obj_datas[i] = new RefItemVO(str_item, null, (str_item.indexOf("#") > 0 ? str_item.substring(str_item.indexOf("#") + 1, str_item.length()) : "����鿴Excel����")); //
					} else if (all_Types[i].equals(WLTConstants.COMP_OFFICE)) { //Excel
						if (str_item.contains("_") || !str_item.contains(".")) {
							obj_datas[i] = new RefItemVO(str_item, null, "����鿴"); //
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
			returnBillVOs[r].setDatas(obj_datas); // ��������������!!!!
			returnBillVOs[r].setToStringFieldName(templetVO.getTreeviewfield()); //
		}

		return returnBillVOs; //
	}

	private ComBoxItemVO findComBoxItemVO(ComBoxItemVO[] _vos, String _id) {
		if (_vos == null || _vos.length == 0) {
			return null;
		}
		for (int k = 0; k < _vos.length; k++) {
			if (_vos[k].getId() != null && _vos[k].getId().equals(_id)) { //����������������Ϊ��!!
				return _vos[k];
			}
		}
		return null;
	}

}
