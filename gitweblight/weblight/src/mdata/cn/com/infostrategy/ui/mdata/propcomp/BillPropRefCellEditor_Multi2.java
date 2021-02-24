package cn.com.infostrategy.ui.mdata.propcomp;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillPropTable;
import cn.com.infostrategy.ui.mdata.listcomp.ListCPanel_Ref;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;

/**
 * �������Ա༭��
 * @author xch
 *
 */
public class BillPropRefCellEditor_Multi2 extends AbstractPropertyEditor {
	private Pub_Templet_1_ItemVO itemVO = null;
	private RefItemVO str_value = null; //

	public BillPropRefCellEditor_Multi2(Pub_Templet_1_ItemVO _itemvo, RefItemVO _value, BillPropTable _billPropTable) {
		this.itemVO = _itemvo; //
		this.str_value = _value; //

		//ListCPanel_Ref refPanel = new ListCPanel_Ref(itemVO, new RefItemVO((String) _value, (String) _value, (String) _value), _billPropTable.getBillPropPanel()); //���ñ��β��յ����,��Ϊ�߼���һ����!!
		ListCPanel_Ref refPanel = new ListCPanel_Ref(itemVO, _value, _billPropTable.getBillPropPanel()); //���ñ��β��յ����,��Ϊ�߼���һ����!!
		editor = refPanel;
	}

	public Object getValue() {
		ListCPanel_Ref panel = (ListCPanel_Ref) editor;
		return panel.getObject(); //
		//return panel.getRefID(); //�����Ƿ���id,��ΪҪ��Bean�е�����һ��!!��Bean�е�������String,���Ա�����String....
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
