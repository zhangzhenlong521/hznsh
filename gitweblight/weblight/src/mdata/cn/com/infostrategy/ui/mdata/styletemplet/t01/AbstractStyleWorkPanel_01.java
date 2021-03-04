/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_01.java,v $  $Revision: 1.7 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t01;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListEditEvent;
import cn.com.infostrategy.ui.mdata.BillListEditListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.StyleTempletServiceIfc;

/**
 * ���ģ��1�ĳ������1
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_01 extends AbstractWorkPanel implements BillListEditListener {
	protected BillListPanel mainBillListPanel = null; //����

	protected WLTButton btn_list_insert = null; //����
	protected WLTButton btn_list_delete = null; //ɾ��
	protected WLTButton btn_list_save = null; //ɾ��

	private IUIIntercept_01 uiIntercept = null; // ui��������
	private ActionListener buttonActionListener = null;

	public abstract String getTempletcode(); //���󷽷�!!ȡ��ģ�����!!!

	/**
	 * ʵ�ֳ��󷽷�!
	 */
	public void initialize() {
		try {
			this.setLayout(new BorderLayout()); //
			mainBillListPanel = new BillListPanel(getTempletcode()); //
			mainBillListPanel.getQuickQueryPanel().setVisible(true); //
			mainBillListPanel.addBillListEditListener(this); //
			btn_list_insert = new WLTButton(WLTConstants.BUTTON_TEXT_INSERT);
			btn_list_delete = new WLTButton(WLTConstants.BUTTON_TEXT_DELETE);
			btn_list_save = new WLTButton(WLTConstants.BUTTON_TEXT_SAVE);
			buttonActionListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (e.getSource() == btn_list_insert) {
							onInsert();
						} else if (e.getSource() == btn_list_delete) {
							onDelete();
						} else if (e.getSource() == btn_list_save) {
							onSave();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageBox.showException(AbstractStyleWorkPanel_01.this, ex); //
					}
				}
			};

			btn_list_insert.addActionListener(buttonActionListener);
			btn_list_delete.addActionListener(buttonActionListener);
			btn_list_save.addActionListener(buttonActionListener);

			mainBillListPanel.insertBatchBillListButton(new WLTButton[] { btn_list_insert, btn_list_delete, btn_list_save }); //
			mainBillListPanel.repaintBillListButton(); //
			this.add(mainBillListPanel, BorderLayout.CENTER); //

			afterInitialize(); //������ʼ��
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ��ʼ��������Ҫ������,���Ա����า��..
	 */
	public void afterInitialize() throws Exception {
		if (this.uiIntercept != null) {
			uiIntercept.afterInitialize(this); //
		}
	}

	public boolean isShowsystembutton() {
		return true;
	}

	public boolean isCanWorkFlowDeal() {
		return false;
	}

	public boolean isCanWorkFlowMonitor() {
		return false;
	}

	/**
	 * ȡ�ñ���
	 * @return
	 */
	public BillListPanel getBillListPanel() {
		return mainBillListPanel;
	}

	protected void onInsert() throws Exception {
		// ִ������������!!
		if (uiIntercept != null) {
			try {
				uiIntercept.actionBeforeInsert(mainBillListPanel, mainBillListPanel.getSelectedRow());
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return;
			}
		}

		int li_row = mainBillListPanel.newRow(); //
		if (mainBillListPanel.containsItemKey("VERSION")) {
			mainBillListPanel.setValueAt(new Integer(1).toString(), mainBillListPanel.getSelectedRow(), "VERSION");
		}

		mainBillListPanel.getTable().getCellEditor(li_row, 0).cancelCellEditing();
		mainBillListPanel.getTable().editCellAt(li_row, 0); //

		CellEditor cellEditor = mainBillListPanel.getTable().getCellEditor(li_row, 0);
		if (cellEditor instanceof DefaultCellEditor) {
			DefaultCellEditor defaultCellEditor = (DefaultCellEditor) cellEditor;
			JComponent comPent = (JComponent) defaultCellEditor.getComponent();
			comPent.requestFocus(); //
		}
	}

	protected void onDelete() {
		if (mainBillListPanel.getTable().getSelectedRowCount() <= 0) {
			return;
		}

		if (uiIntercept != null) {
			try {
				uiIntercept.actionBeforeDelete(mainBillListPanel, mainBillListPanel.getSelectedRow());
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.show(this, "����" + e.getMessage(), WLTConstants.MESSAGE_ERROR);
				return;
			}
		}

		mainBillListPanel.removeSelectedRows(); //ɾ������ѡ�е���!
	}

	/**
	 * ���水ť����!!
	 */
	protected void onSave() {
		mainBillListPanel.stopEditing();

		if (!mainBillListPanel.checkValidate()) { //У��
			return;
		}

		//HashMap BillVOMap = new HashMap();
		try {
			BillVO[] insertvo = mainBillListPanel.getInsertedBillVOs();
			BillVO[] updatevo = mainBillListPanel.getUpdatedBillVOs();
			BillVO[] deletevo = mainBillListPanel.getDeletedBillVOs();

			// ִ���ύǰ����.
			if (this.uiIntercept != null) {
				try {
					uiIntercept.dealBeforeCommit(mainBillListPanel, insertvo, deletevo, updatevo);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageBox.show(this, ex.getMessage(), WLTConstants.MESSAGE_WARN);
					return;
				}
			}

			//����Զ���ύ���ݿ�!!!!
			StyleTempletServiceIfc service = (StyleTempletServiceIfc) RemoteServiceFactory.getInstance().lookUpService(StyleTempletServiceIfc.class);
			HashMap returnMap = service.style01_dealCommit(mainBillListPanel.getDataSourceName(), null, insertvo, deletevo, updatevo); //����Զ�̷���!!

			mainBillListPanel.clearDeleteBillVOs();
			for (int i = 0; i < mainBillListPanel.getTable().getRowCount(); i++) {
				RowNumberItemVO itemvo = (RowNumberItemVO) mainBillListPanel.getValueAt(i, "_RECORD_ROW_NUMBER");
				if (itemvo.getState().equals("UPDATE")) {
					if (mainBillListPanel.containsItemKey("VERSION")) { //����а汾�ֶ�!!
						int version = new Integer(mainBillListPanel.getValueAt(i, "VERSION").toString()).intValue() + 1;
						mainBillListPanel.setValueAt(new Integer(version).toString(), i, "VERSION");
					}
				}
			}

			BillVO[] returnInsertVOs = (BillVO[]) returnMap.get("INSERT"); //...
			BillVO[] returnDeleteVOs = (BillVO[]) returnMap.get("DELETE"); //...
			BillVO[] returnUpdateVOs = (BillVO[]) returnMap.get("UPDATE"); //...

			// ִ���ύ������.
			if (this.uiIntercept != null) {
				uiIntercept.dealAfterCommit(mainBillListPanel, returnInsertVOs, returnDeleteVOs, returnUpdateVOs);
			}

			mainBillListPanel.stopEditing();
			mainBillListPanel.setAllRowStatusAs("INIT");

			String str_msg = ""; //
			StringBuilder sb_retuslt = new StringBuilder(); //
			if (returnInsertVOs != null && returnInsertVOs.length > 0) {
				sb_retuslt.append("�������ݡ�" + returnInsertVOs.length + "����;");
			}
			if (returnUpdateVOs != null && returnUpdateVOs.length > 0) {
				sb_retuslt.append("�޸����ݡ�" + returnUpdateVOs.length + "����;");
			}
			if (returnDeleteVOs != null && returnDeleteVOs.length > 0) {
				sb_retuslt.append("ɾ�����ݡ�" + returnDeleteVOs.length + "����;");
			}

			if (sb_retuslt.toString().equals("")) {
				str_msg = "û�з������ݸ���!";
			} else {
				str_msg = "�������ݳɹ�!\r\n" + sb_retuslt.toString();
			}
			MessageBox.show(AbstractStyleWorkPanel_01.this, str_msg); //
		} catch (Exception e) {
			MessageBox.show(AbstractStyleWorkPanel_01.this, e.getMessage(), WLTConstants.MESSAGE_ERROR);
			e.printStackTrace();
		}
	}

	protected void onSearch() {

	}

	/**
	 * �б�仯���������
	 */
	public void onBillListValueChanged(BillListEditEvent _evt) {
		if (uiIntercept != null) {
			BillListPanel list_temp = (BillListPanel) _evt.getSource(); //
			String tmp_itemkey = _evt.getItemKey(); //
			try {
				uiIntercept.actionBeforeUpdate(list_temp, list_temp.getSelectedRow(), tmp_itemkey);
			} catch (Exception e) {
				MessageBox.showException(this, e); //
			}
		}
	}

	public boolean isCanInsert() {
		return true;
	}

	public boolean isCanDelete() {
		return true;
	}

	public boolean isCanEdit() {
		return true;
	}
}
