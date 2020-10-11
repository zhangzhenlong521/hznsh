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
			button.setText("<html><font color=blue><u>" + oldrefVO.getName() + "</u></font></html>"); // �����Html��ʾ,�����»�����ʾ
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
	 * �����ťִ��ʲô!!
	 * 
	 * @param row
	 * @param column
	 * @param oldrefVO
	 */
	protected void onButtonClicked(int row, int column) {
		if (oldrefVO != null && oldrefVO.getId().equals("*****")) { //����Ǽ���!!
			String str_queryTable = itemVO.getPub_Templet_1VO().getTablename(); //
			String str_saveTable = itemVO.getPub_Templet_1VO().getSavedtablename(); //
			boolean isCando = new SysUIUtil().isCanDoAsSuperAdmin(button, str_queryTable, str_saveTable, true); //���ǰ���ģʽ��һ��!
			boolean isOpenfile = false; //
			if (!isCando) { //���������,����ʾһ����,���Եڶ�����ʽִ��!!!
				if (MessageBox.confirm(button, "�������ݲ��ܲ鿴��༭,���Ƿ���ʹ�ó�������Ա��?")) { //
					boolean isCando2 = new SysUIUtil().isCanDoAsSuperAdmin(button, str_queryTable, str_saveTable, false); //���ǰ���ģʽ��һ��!
					if (isCando2) { //�ڶ�����ʽ����ִ��,����ɹ�������ļ�,������ɹ�,����Ȼ���ж�Ӧ�Ĳ��ɹ���ԭ��!
						isOpenfile = true;
					}
				} else { //����ȷ�,�����볢�Թ���Ա�򿪣���ʲô������!
				}
			} else {
				isOpenfile = true;
			}
			if (isOpenfile) {
				String str_realValue = oldrefVO.getCode(); //
				RefItemVO unEncryRefVO = new RefItemVO(str_realValue, null, getRefFileName(str_realValue)); //
				openFileDialog(unEncryRefVO, row, column, true); //������Ǽ��ܵ�!����ļ�
			}
			newrefVO = oldrefVO; //���ص�����ԭ��������!!
		} else {
			openFileDialog(oldrefVO, row, column, false); //������Ǽ��ܵ�!����ļ�
		}
	}

	private void openFileDialog(RefItemVO _refVO, int row, int column, boolean _isJiaMi) {
		try {
			// �����ļ�ѡ���
			RefDialog_File refdialog_file = new RefDialog_File(button, "�ļ��ϴ�/����", _refVO, this.billListPanel, this.itemVO.getUCDfVO());
			refdialog_file.setPubTempletItemVO(this.itemVO); //
			refdialog_file.initialize();
			if (_isJiaMi) {
				refdialog_file.getRefFileDealPanel().setEditabled(false); //�������,��ֻ�ܿ�!!!
			}
			refdialog_file.setVisible(true);
			if (!_isJiaMi) { //������Ǽ��ܵ�,������ֵ!
				// ���������е�ֵ���µ��б���
				if (refdialog_file.getCloseType() == BillDialog.CONFIRM) { //�����ϴ����µ��ļ�!!!
					newrefVO = refdialog_file.getReturnRefItemVO();
					// newrefVO =// new RefItemVO(refdialog_file..getId(), null,
					// refdialog_file.getName()); //
					String str_editable = itemVO.getListiseditable(); //
					if (str_editable != null && str_editable.equalsIgnoreCase("4")) {
						return;
					}
					table.setValueAt(newrefVO, row, column);
					billListPanel.setRowStatusAs(row, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				} else { //�����ȡ��!
					newrefVO = _refVO;
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(button, ex);
		} finally {
			((BillListModel) table.getModel()).getBillListPanel().stopEditing(); //
		}
	}

	//�ļ���ǰ����Ŀ¼,������16����,��Ҫ����!!!
	private String getRefFileName(String _refId) {
		TBUtil tb = new TBUtil(); //
		StringBuilder sb_name = new StringBuilder(); //
		String[] sr_items = tb.split(_refId, ";"); //
		for (int i = 0; i < sr_items.length; i++) {
			String str_item = sr_items[i].trim(); //
			str_item = str_item.substring(str_item.lastIndexOf("/") + 1, str_item.length()); //ȥ��ǰ���Ŀ¼!
			String str_item_convert = tb.convertHexStringToStr(str_item.substring(str_item.indexOf("_") + 1, str_item.lastIndexOf("."))) + (str_item.substring(str_item.lastIndexOf("."), str_item.length())); //16���Ʒ�ת!
			sb_name.append(str_item_convert + ";"); //
		}
		return sb_name.toString(); //
	}

	/**
	 * 
	 */
	public boolean isCellEditable(EventObject evt) {
		return true; // ���ɱ༭
	}

	public Object getCellEditorValue() {
		return newrefVO; //
	}

	public javax.swing.JComponent getCompent() {
		return button;
	}

}
