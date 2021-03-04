package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

/**
 * 取数时将模板VO瘦身,只要需要的信息! 比如加载公式等!
 * @author xch
 *
 */
public class Pub_Templet_1_ItemParVO implements Serializable {

	private static final long serialVersionUID = 7183080113328005505L;
	private String itemkey = null; //
	private String itemname = null; //
	private String itemtype = null;
	private String loadformula = null; //
	private String savedcolumndatatype = null; //
	private Boolean issave; //是否参与保存
	private Boolean isencrypt; //保存时是否加密,如果是加密的话,则查询时还要自动解密!!!
	private ComBoxItemVO[] comBoxItemVos = null; //

	public String getItemkey() {
		return itemkey;
	}

	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}

	public String getItemname() {
		return itemname; //
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getItemtype() {
		return itemtype;
	}

	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	public String getLoadformula() {
		return loadformula;
	}

	public void setLoadformula(String loadformula) {
		this.loadformula = loadformula;
	}

	public String getSavedcolumndatatype() {
		return savedcolumndatatype;
	}

	public void setSavedcolumndatatype(String savedcolumndatatype) {
		this.savedcolumndatatype = savedcolumndatatype;
	}

	public boolean getIssave() { //
		return issave;
	}

	public boolean isNeedSave() {
		return issave; //
	}

	public void setIssave(Boolean _issave) {
		issave = _issave; //
	}

	public Boolean getIsencrypt() {
		return isencrypt;
	}

	public void setIsencrypt(Boolean isencrypt) {
		this.isencrypt = isencrypt;
	}

	public ComBoxItemVO[] getComBoxItemVos() {
		return comBoxItemVos;
	}

	public void setComBoxItemVos(ComBoxItemVO[] comBoxItemVos) {
		this.comBoxItemVos = comBoxItemVos;
	}

}
