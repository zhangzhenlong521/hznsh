package cn.com.infostrategy.ui.workflow.pbom;

import java.util.EventObject;

public class BomItemClickEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private String bomcode, itemkey, itemname, bindtype, bindbomcode, bindclass, bindmenu, loadtype; //
	
	/**
	 * 
	 * @param source
	 */
	private BomItemClickEvent(Object source) {
		super(source);
	}

	/**
	 * _bingmenu
	 * @param _bomCode
	 * @param _itemkey
	 * @param _itemname
	 * @param _bindbomcode
	 * @param source
	 */
	protected BomItemClickEvent(String _bomCode, String _itemkey, String _itemname, String _bindtype, String _bindbomcode, String _bindclass, String _bingmenu, String _loadtype, Object source) {
		super(source); //
		this.bomcode = _bomCode; //
		this.itemkey = _itemkey;
		this.itemname = _itemname; //
		this.bindtype = _bindtype; //
		this.bindbomcode = _bindbomcode; //
		this.bindclass = _bindclass; //
		this.bindmenu = _bingmenu; //
		this.loadtype = _loadtype; //
	}

	public String getItemkey() {
		return itemkey;
	}

	public String getItemname() {
		return itemname;
	}

	public String getBindbomcode() {
		return bindbomcode;
	}

	public Object getSource() {
		return super.getSource();
	}

	public String getBomcode() {
		return bomcode;
	}

	public String getBindclass() {
		return bindclass;
	}

	public void setBindclass(String bindclass) {
		this.bindclass = bindclass;
	}

	public String getBindtype() {
		return bindtype;
	}

	public String getBindmenu() {
		return bindmenu;
	}

	public void setBindmenu(String bindmenu) {
		this.bindmenu = bindmenu;
	}

	public String getLoadtype() {
		return loadtype;
	}

	public void setLoadtype(String loadtype) {
		this.loadtype = loadtype;
	}
	//强制设置点击Label打开的Bom编号。用于点击同一个按钮，可以根据不同原因跳转到不同bom图。
	public void setBindbomcode(String bindbomcode) {
		this.bindbomcode = bindbomcode;
	}

}
