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
 * 风格模板1的抽象面板1
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_01 extends AbstractWorkPanel implements BillListEditListener {
	protected BillListPanel mainBillListPanel = null; //主表

	protected WLTButton btn_list_insert = null; //新增
	protected WLTButton btn_list_delete = null; //删除
	protected WLTButton btn_list_save = null; //删除

	private IUIIntercept_01 uiIntercept = null; // ui端拦截器
	private ActionListener buttonActionListener = null;

	public abstract String getTempletcode(); //抽象方法!!取得模板编码!!!

	/**
	 * 实现抽象方法!
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

			afterInitialize(); //后续初始化
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 初始化结束后要做的事,可以被子类覆盖..
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
	 * 取得表格框
	 * @return
	 */
	public BillListPanel getBillListPanel() {
		return mainBillListPanel;
	}

	protected void onInsert() throws Exception {
		// 执行拦截器操作!!
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
				MessageBox.show(this, "错误：" + e.getMessage(), WLTConstants.MESSAGE_ERROR);
				return;
			}
		}

		mainBillListPanel.removeSelectedRows(); //删除所有选中的行!
	}

	/**
	 * 保存按钮操作!!
	 */
	protected void onSave() {
		mainBillListPanel.stopEditing();

		if (!mainBillListPanel.checkValidate()) { //校验
			return;
		}

		//HashMap BillVOMap = new HashMap();
		try {
			BillVO[] insertvo = mainBillListPanel.getInsertedBillVOs();
			BillVO[] updatevo = mainBillListPanel.getUpdatedBillVOs();
			BillVO[] deletevo = mainBillListPanel.getDeletedBillVOs();

			// 执行提交前拦截.
			if (this.uiIntercept != null) {
				try {
					uiIntercept.dealBeforeCommit(mainBillListPanel, insertvo, deletevo, updatevo);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageBox.show(this, ex.getMessage(), WLTConstants.MESSAGE_WARN);
					return;
				}
			}

			//真正远程提交数据库!!!!
			StyleTempletServiceIfc service = (StyleTempletServiceIfc) RemoteServiceFactory.getInstance().lookUpService(StyleTempletServiceIfc.class);
			HashMap returnMap = service.style01_dealCommit(mainBillListPanel.getDataSourceName(), null, insertvo, deletevo, updatevo); //真正远程访问!!

			mainBillListPanel.clearDeleteBillVOs();
			for (int i = 0; i < mainBillListPanel.getTable().getRowCount(); i++) {
				RowNumberItemVO itemvo = (RowNumberItemVO) mainBillListPanel.getValueAt(i, "_RECORD_ROW_NUMBER");
				if (itemvo.getState().equals("UPDATE")) {
					if (mainBillListPanel.containsItemKey("VERSION")) { //如果有版本字段!!
						int version = new Integer(mainBillListPanel.getValueAt(i, "VERSION").toString()).intValue() + 1;
						mainBillListPanel.setValueAt(new Integer(version).toString(), i, "VERSION");
					}
				}
			}

			BillVO[] returnInsertVOs = (BillVO[]) returnMap.get("INSERT"); //...
			BillVO[] returnDeleteVOs = (BillVO[]) returnMap.get("DELETE"); //...
			BillVO[] returnUpdateVOs = (BillVO[]) returnMap.get("UPDATE"); //...

			// 执行提交后拦截.
			if (this.uiIntercept != null) {
				uiIntercept.dealAfterCommit(mainBillListPanel, returnInsertVOs, returnDeleteVOs, returnUpdateVOs);
			}

			mainBillListPanel.stopEditing();
			mainBillListPanel.setAllRowStatusAs("INIT");

			String str_msg = ""; //
			StringBuilder sb_retuslt = new StringBuilder(); //
			if (returnInsertVOs != null && returnInsertVOs.length > 0) {
				sb_retuslt.append("新增数据【" + returnInsertVOs.length + "】条;");
			}
			if (returnUpdateVOs != null && returnUpdateVOs.length > 0) {
				sb_retuslt.append("修改数据【" + returnUpdateVOs.length + "】条;");
			}
			if (returnDeleteVOs != null && returnDeleteVOs.length > 0) {
				sb_retuslt.append("删除数据【" + returnDeleteVOs.length + "】条;");
			}

			if (sb_retuslt.toString().equals("")) {
				str_msg = "没有发现数据更改!";
			} else {
				str_msg = "保存数据成功!\r\n" + sb_retuslt.toString();
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
	 * 列表变化会调用这里
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
