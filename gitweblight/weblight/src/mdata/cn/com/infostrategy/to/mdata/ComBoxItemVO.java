/**************************************************************************
 * $RCSfile: ComBoxItemVO.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

import cn.com.infostrategy.to.common.HashVO;

public class ComBoxItemVO extends BillItemVO implements Serializable {

	private static final long serialVersionUID = -611192143605005519L;

	private String id; // 主键
	private String code; // 编码
	private String name; // 名称
	private boolean isSelected = false; // 
	private HashVO hashVO = null; // 直接的数据

	public ComBoxItemVO() {

	}

	public ComBoxItemVO(HashVO _vo) {
		this.hashVO = _vo;
		this.id = this.hashVO.getStringValue(0); // 主键
		this.code = this.hashVO.getStringValue(1); // 编码
		this.name = this.hashVO.getStringValue(2); // 名称
	}

	public ComBoxItemVO(String _id, String _code, String _name) {
		this.id = _id;
		this.code = _code;
		this.name = _name;

		this.hashVO = new HashVO();
		this.hashVO.setAttributeValue("ID", _id);
		this.hashVO.setAttributeValue("CODE", _code);
		this.hashVO.setAttributeValue("NAME", _name);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashVO getHashVO() {
		return hashVO;
	}

	public void setHashVO(HashVO hashVO) {
		this.hashVO = hashVO;
	}

	/**
	 * 得到某一项的值
	 * 
	 * @param _itemkey
	 * @return
	 */
	public String getItemValue(String _itemkey) {
		if (hashVO != null) {
			return hashVO.getStringValue(_itemkey); //
		}
		return null;
	}

	public String toString() {
		return name;
	}

	public boolean equals(Object _obj) {
		if (_obj == null) {
			return false;
		}

		if (_obj instanceof ComBoxItemVO) {
			ComBoxItemVO new_name = (ComBoxItemVO) _obj;
			if (new_name.getId() != null && new_name.getId().equals(this.id)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
