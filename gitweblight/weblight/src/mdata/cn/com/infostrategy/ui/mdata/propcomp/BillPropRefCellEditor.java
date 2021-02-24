package cn.com.infostrategy.ui.mdata.propcomp;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillPropTable;
import cn.com.infostrategy.ui.mdata.listcomp.ListCPanel_Ref;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;

/**
 * 参照属性编辑器
 * @author xch
 *
 */
public class BillPropRefCellEditor extends AbstractPropertyEditor {
	private Pub_Templet_1_ItemVO itemVO = null;
	private String str_value = null; //

	public BillPropRefCellEditor(Pub_Templet_1_ItemVO _itemvo, String _value, BillPropTable _billPropTable) {
		this.itemVO = _itemvo; //
		this.str_value = _value; //
		editor = new ListCPanel_Ref(itemVO, new RefItemVO((String) _value, (String) _value, (String) _value), _billPropTable.getBillPropPanel()); //借用表形参照的面板,因为逻辑是一样的!!
	}

	public Object getValue() {
		ListCPanel_Ref panel = (ListCPanel_Ref) editor;
		return panel.getRefID();
	}

	public void setValue(Object value) {
		ListCPanel_Ref panel = (ListCPanel_Ref) editor; //
		if (value == null) { //
			panel.reset(); //
		} else {
			panel.setObject(new RefItemVO((String) value, (String) value, (String) value)); //
		}
	}

}
