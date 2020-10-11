/**************************************************************************
 * $RCSfile: ListCellEditor_Button.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListModel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class ListCellEditor_Button extends AbstractCellEditor implements TableCellEditor, IBillCellEditor {

	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO itemVO = null;

	private JTable table = null; //
	private JButton button = null; //

	public ListCellEditor_Button(Pub_Templet_1_ItemVO _itemvo) {
		this.itemVO = _itemvo;
	}

	public Component getTableCellEditorComponent(JTable _table, Object value, boolean isSelected, final int row, final int column) {
		this.table = _table; //
		button = new JButton(); //
		button.setText("<html><font color=blue><u>" + itemVO.getItemname() + "</u></font></html>"); //如果是Html显示,则用下划线显示
		button.setBackground(new Color(184, 207, 229));//
		button.setBorder(BorderFactory.createEmptyBorder()); //
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onButtonClicked(row, column); //
			}
		}); //
		return button; //
	}

	/**
	 * 点击按钮执行什么!!
	 * @param row
	 * @param column
	 */
	protected void onButtonClicked(int row, int column) {
		try {
			BillListPanel billListPanel = ((BillListModel) table.getModel()).getBillListPanel(); //
			String str_itemkey = itemVO.getItemkey(); //
			if (str_itemkey.equalsIgnoreCase("deleterow")) { //如果是删除行
				billListPanel.removeRow(row); //
			} else if (str_itemkey.equalsIgnoreCase("editrow")) { //如果编辑行
				BillVO billVO = billListPanel.getSelectedBillVO();
				BillCardPanel cardPanel = new BillCardPanel(billListPanel.templetVO);
				cardPanel.setLoaderBillFormatPanel(billListPanel.getLoaderBillFormatPanel());
				cardPanel.setBillVO(billVO); //
				BillCardDialog dialog = new BillCardDialog(billListPanel, billListPanel.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				dialog.setVisible(true); //
				if (dialog.getCloseType() == 1) {
					billListPanel.setBillVOAt(billListPanel.getSelectedRow(), dialog.getBillVO());
					billListPanel.setRowStatusAs(billListPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
				}
			} else {
				String str_actionname = itemVO.getRefdesc(); //取得参照定义
				if (str_actionname == null || str_actionname.trim().equals("")) {
					throw new WLTAppException("没有定义按钮的监听事件类!!");
				}
				str_actionname = str_actionname.replaceAll("\"", "");
				WLTActionListener actionListener = (WLTActionListener) Class.forName(str_actionname).newInstance(); //
				WLTActionEvent event = new WLTActionEvent(button, billListPanel, row, itemVO.getItemkey()); //
				actionListener.actionPerformed(event); //执行按钮动作!!
			}
		} catch (Exception ex) {
			MessageBox.showException(button, ex);
		} finally {
			((BillListModel) table.getModel()).getBillListPanel().stopEditing(); //
		}
	}

	/**
	 * 
	 */
	public boolean isCellEditable(EventObject evt) {
		return true; //不可编辑
	}

	public Object getCellEditorValue() {
		return new StringItemVO(button.getText()); //
	}

	public javax.swing.JComponent getCompent() {
		return button;
	}

}
