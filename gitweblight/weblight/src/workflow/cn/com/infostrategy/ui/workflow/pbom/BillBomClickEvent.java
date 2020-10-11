package cn.com.infostrategy.ui.workflow.pbom;

import java.util.EventObject;

public class BillBomClickEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private String bomcode, itemkey, itemname, bindbomcode; //

	/**
	 * 
	 * @param source
	 */
	private BillBomClickEvent(Object source) {
		super(source);
	}

	/**
	 * 
	 * @param _code
	 * @param _itemkey
	 * @param _itemname
	 * @param _bindbomcode
	 * @param source
	 */
	protected BillBomClickEvent(String _code, String _itemkey, String _itemname, String _bindbomcode, Object source) {
		super(source); //
		this.bomcode = _code; //
		this.itemkey = _itemkey;
		this.itemname = _itemname; //
		this.bindbomcode = _bindbomcode; //
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

}
