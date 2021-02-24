/**************************************************************************
 * $RCSfile: ListCellEditor_Ref.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillListModel;

public class ListCellEditor_Ref extends AbstractCellEditor implements TableCellEditor, IBillCellEditor {

	private static final long serialVersionUID = 1L;

	private ListCPanel_Ref refPanel = null; //

	private Pub_Templet_1_ItemVO itemVO = null; //

	public ListCellEditor_Ref(Pub_Templet_1_ItemVO _itemvo) {
		itemVO = _itemvo;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		BillListModel model = (BillListModel) table.getModel(); //
		RefItemVO refOldVO = (RefItemVO) value;
		if (refOldVO != null) {
			refOldVO.setValueChanged(false); //初始状态!!
		}
		refPanel = new ListCPanel_Ref(itemVO, refOldVO, model.getBillListPanel()); //创建控件!!将初始值与BillListPanel句柄带入!!
		if (itemVO.getListiseditable() == null || !itemVO.getListiseditable().equals("1") || !itemVO.getListiseditable().equals("2") || !itemVO.getListiseditable().equals("3")) {
			refPanel.setItemEditable(true); //
		} else {
			refPanel.setItemEditable(false); //
		}

		return refPanel; //
	}

	public Object getCellEditorValue() {
		return refPanel.getObject();
	}

	public boolean isCellEditable(EventObject evt) {
		if (itemVO.getListiseditable() != null && itemVO.getListiseditable().equals("1")) {
			if (evt instanceof MouseEvent) {
				return ((MouseEvent) evt).getClickCount() >= 2;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public javax.swing.JComponent getCompent() {
		return refPanel;
	}

}
