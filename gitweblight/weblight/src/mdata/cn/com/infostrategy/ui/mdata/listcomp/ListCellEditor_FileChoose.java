/**************************************************************************
 * $RCSfile: ListCellEditor_FileChoose.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:57 $
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

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillListModel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_File;
import cn.com.infostrategy.ui.sysapp.SysUIUtil;

public class ListCellEditor_FileChoose extends AbstractCellEditor implements TableCellEditor, IBillCellEditor {

	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO itemVO = null;
	private RefItemVO oldrefVO = null;
	private RefItemVO newrefVO = null;
	private BillListPanel billListPanel = null; //
	private JTable table = null; //
	private JButton button = null; //

	public ListCellEditor_FileChoose(Pub_Templet_1_ItemVO _itemvo) {
		this.itemVO = _itemvo;
	}

	public Component getTableCellEditorComponent(JTable _table, Object value, boolean isSelected, final int row, final int column) {
		this.table = _table; //
		this.billListPanel = ((BillListModel) table.getModel()).getBillListPanel(); //
		oldrefVO = (RefItemVO) value; //
		newrefVO = oldrefVO; // 
		button = new JButton(); //
		button.putClientProperty("itemkey", itemVO.getItemkey()); //
		if (oldrefVO != null) {
			button.setText("<html><font color=blue><u>" + oldrefVO.getName() + "</u></font></html>"); // 如果是Html显示,则用下划线显示
		}
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
	 * 
	 * @param row
	 * @param column
	 * @param oldrefVO
	 */
	protected void onButtonClicked(int row, int column) {
		if (oldrefVO != null && oldrefVO.getId().equals("*****")) { //如果是加密!!
			String str_queryTable = itemVO.getPub_Templet_1VO().getTablename(); //
			String str_saveTable = itemVO.getPub_Templet_1VO().getSavedtablename(); //
			boolean isCando = new SysUIUtil().isCanDoAsSuperAdmin(button, str_queryTable, str_saveTable, true); //先是安静模式搞一下!
			boolean isOpenfile = false; //
			if (!isCando) { //如果不能做,则提示一个框,可以第二次显式执行!!!
				if (MessageBox.confirm(button, "加密数据不能查看与编辑,你是否尝试使用超级管理员打开?")) { //
					boolean isCando2 = new SysUIUtil().isCanDoAsSuperAdmin(button, str_queryTable, str_saveTable, false); //先是安静模式搞一下!
					if (isCando2) { //第二次显式计算执行,如果成功则想打开文件,如果不成功,则自然会有对应的不成功的原因!
						isOpenfile = true;
					}
				} else { //如果先否,即不想尝试管理员打开，则什么都不做!
				}
			} else {
				isOpenfile = true;
			}
			if (isOpenfile) {
				String str_realValue = oldrefVO.getCode(); //
				RefItemVO unEncryRefVO = new RefItemVO(str_realValue, null, getRefFileName(str_realValue)); //
				openFileDialog(unEncryRefVO, row, column, true); //如果不是加密的!则打开文件
			}
			newrefVO = oldrefVO; //返回的仍是原来的数据!!
		} else {
			openFileDialog(oldrefVO, row, column, false); //如果不是加密的!则打开文件
		}
	}

	private void openFileDialog(RefItemVO _refVO, int row, int column, boolean _isJiaMi) {
		try {
			// 弹出文件选择框
			RefDialog_File refdialog_file = new RefDialog_File(button, "文件上传/下载", _refVO, this.billListPanel, this.itemVO.getUCDfVO());
			refdialog_file.setPubTempletItemVO(this.itemVO); //
			refdialog_file.initialize();
			if (_isJiaMi) {
				refdialog_file.getRefFileDealPanel().setEditabled(false); //如果加密,则只能看!!!
			}
			refdialog_file.setVisible(true);
			if (!_isJiaMi) { //如果不是加密的,则处理返回值!
				// 将弹出框中的值更新到列表中
				if (refdialog_file.getCloseType() == BillDialog.CONFIRM) { //可能上传了新的文件!!!
					newrefVO = refdialog_file.getReturnRefItemVO();
					// newrefVO =// new RefItemVO(refdialog_file..getId(), null,
					// refdialog_file.getName()); //
					String str_editable = itemVO.getListiseditable(); //
					if (str_editable != null && str_editable.equalsIgnoreCase("4")) {
						return;
					}
					table.setValueAt(newrefVO, row, column);
					billListPanel.setRowStatusAs(row, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				} else { //如果是取消!
					newrefVO = _refVO;
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(button, ex);
		} finally {
			((BillListModel) table.getModel()).getBillListPanel().stopEditing(); //
		}
	}

	//文件名前面有目录,而且是16进制,需要处理!!!
	private String getRefFileName(String _refId) {
		TBUtil tb = new TBUtil(); //
		StringBuilder sb_name = new StringBuilder(); //
		String[] sr_items = tb.split(_refId, ";"); //
		for (int i = 0; i < sr_items.length; i++) {
			String str_item = sr_items[i].trim(); //
			str_item = str_item.substring(str_item.lastIndexOf("/") + 1, str_item.length()); //去掉前面的目录!
			String str_item_convert = tb.convertHexStringToStr(str_item.substring(str_item.indexOf("_") + 1, str_item.lastIndexOf("."))) + (str_item.substring(str_item.lastIndexOf("."), str_item.length())); //16进制反转!
			sb_name.append(str_item_convert + ";"); //
		}
		return sb_name.toString(); //
	}

	/**
	 * 
	 */
	public boolean isCellEditable(EventObject evt) {
		return true; // 不可编辑
	}

	public Object getCellEditorValue() {
		return newrefVO; //
	}

	public javax.swing.JComponent getCompent() {
		return button;
	}

}
