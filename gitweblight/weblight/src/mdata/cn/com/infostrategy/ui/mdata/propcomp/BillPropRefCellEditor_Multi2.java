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
public class BillPropRefCellEditor_Multi2 extends AbstractPropertyEditor {
	private Pub_Templet_1_ItemVO itemVO = null;
	private RefItemVO str_value = null; //

	public BillPropRefCellEditor_Multi2(Pub_Templet_1_ItemVO _itemvo, RefItemVO _value, BillPropTable _billPropTable) {
		this.itemVO = _itemvo; //
		this.str_value = _value; //

		//ListCPanel_Ref refPanel = new ListCPanel_Ref(itemVO, new RefItemVO((String) _value, (String) _value, (String) _value), _billPropTable.getBillPropPanel()); //借用表形参照的面板,因为逻辑是一样的!!
		ListCPanel_Ref refPanel = new ListCPanel_Ref(itemVO, _value, _billPropTable.getBillPropPanel()); //借用表形参照的面板,因为逻辑是一样的!!
		editor = refPanel;
	}

	public Object getValue() {
		ListCPanel_Ref panel = (ListCPanel_Ref) editor;
		return panel.getObject(); //
		//return panel.getRefID(); //必须是返回id,因为要与Bean中的类型一致!!因Bean中的类型是String,所以必须是String....
	}

	public void setValue(Object value) {
		ListCPanel_Ref panel = (ListCPanel_Ref) editor; //
		if (value == null) { //
			panel.reset(); //
		} else {
			//panel.setObject(new RefItemVO(value.toString(), value.toString(), value.toString())); ///
			panel.setObject((RefItemVO) value); //
		}
	}

}
