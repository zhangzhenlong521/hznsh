package cn.com.infostrategy.ui.mdata;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;

public class BillListGroupableTableHeader extends JTableHeader {

	private static final long serialVersionUID = 8026092835016604663L;

	protected Vector columnGroups = null;

	private Pub_Templet_1VO templet_1VO = null;

	private Hashtable ht_colvalueconverter = new Hashtable(); //列名转换器

	public BillListGroupableTableHeader(TableColumnModel model, Pub_Templet_1VO _templet_1VO) {
		super(model);
		templet_1VO = _templet_1VO;
		setUI(new BillListGroupableTableHeaderUI(_templet_1VO)); //设置显示
		setReorderingAllowed(false); //不可以移动列顺序
	}

	public void setReorderingAllowed(boolean b) {
		reorderingAllowed = false; //不可以移动列顺序
	}

	public void addColumnGroup(BillListGroupColumn g) {
		if (columnGroups == null) {
			columnGroups = new Vector();
		}
		columnGroups.addElement(g);
		ht_colvalueconverter.put(g.getHeaderValue(), g.getHeaderValue()); //
	}

	public boolean containsGroup(BillListGroupColumn _group) {
		if (columnGroups == null) {
			return false;
		} else {
			if (columnGroups.contains(_group)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public Enumeration getColumnGroups(TableColumn col) {
		if (columnGroups == null) {
			return null;
		}
		Enumeration enums = columnGroups.elements();
		while (enums.hasMoreElements()) {
			BillListGroupColumn cGroup = (BillListGroupColumn) enums.nextElement();
			Vector v_ret = (Vector) cGroup.getColumnGroups(col, new Vector());
			if (v_ret != null) {
				return v_ret.elements();
			}
		}
		return null;
	}

	public void setColumnMargin() {
		if (columnGroups == null) {
			return;
		}
		int columnMargin = getColumnModel().getColumnMargin(); //
		Enumeration enums = columnGroups.elements();
		while (enums.hasMoreElements()) {
			BillListGroupColumn cGroup = (BillListGroupColumn) enums.nextElement();
			cGroup.setColumnMargin(columnMargin);
		}
	}

	public void updateUI() {
		//setUI(new BillListGroupableTableHeaderUI(templet_1VO)); //设置显示
		super.updateUI(); //
	}

	public Hashtable getColValueConverter() {
		return ht_colvalueconverter;
	}

	public void setColValueConverter(Hashtable _colvalueconverter) {
		this.ht_colvalueconverter = _colvalueconverter;
	}

}
