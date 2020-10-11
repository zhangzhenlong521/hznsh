/**************************************************************************
 * $RCSfile: BillListDialog.java,v $  $Revision: 1.11 $  $Date: 2012/10/08 02:22:49 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;

public class BillListDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	protected BillListPanel billlistPanel = null;
	private String str_initSQL = null;
	private WLTButton btn_confirm, btn_cancel;
	private int closeType = -1;
	private BillVO[] returnBillVOs = null;
	public  int ButtonID =0;


	private boolean isMustReturn = true;

	public BillListDialog(Container _parent, String _title, String _templetcode) {
		super(_parent, _title, 800, 500); //
		initialize(_templetcode); //
	}

	public BillListDialog(Container _parent, String _title, String _templetcode, int _width, int _height) {
		super(_parent, _title, _width, _height); //
		initialize(_templetcode); //
	}

	public BillListDialog(Container _parent, String _title, String _templetcode, String _initSQL, int _width, int _height) {
		super(_parent, _title, _width, _height); //
		this.str_initSQL = _initSQL;
		initialize(_templetcode); //
	}

	public BillListDialog(Container _parent, String _title, String _templetcode, String _initSQL, int _width, int _height, boolean _mustReturn) {
		super(_parent, _title, _width, _height); //
		this.str_initSQL = _initSQL;
		this.isMustReturn = _mustReturn;
		initialize(_templetcode); //
	}

	public BillListDialog(Container _parent, String _title, BillListPanel _listPanel) {
		super(_parent, _title, 800, 600); //
		billlistPanel = _listPanel;
		initialize2();
	}

	public BillListDialog(Container _parent, String _title, BillListPanel _listPanel, int _width, int _height, boolean _mustReturn) {
		super(_parent, _title, _width, _height); //
		billlistPanel = _listPanel;
		this.isMustReturn = _mustReturn;
		initialize2();
	}

	private void initialize2() {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billlistPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private void initialize(String _templetcode) {
		this.getContentPane().setLayout(new BorderLayout());
		billlistPanel = new BillListPanel(_templetcode); //
		if (str_initSQL != null) {
			billlistPanel.QueryDataByCondition(str_initSQL); //如果定义了初始化条件,则查询之
		}
		//billlistPanel.getPagePanel().setVisible(false); //
		this.getContentPane().add(billlistPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //


	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		if (isMustReturn) {
			panel.add(btn_cancel); //
		}
		return panel;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}
	public int ButtonID(){
		int count=ButtonID;
		return count;
	}
	public void onConfirm() {
		try {
			ButtonID=1;
			if (isMustReturn) {
				if (billlistPanel.getSelectedRow() < 0) {
					MessageBox.showSelectOne(this);
					return; //
				}
				returnBillVOs = billlistPanel.getSelectedBillVOs(); //
				billlistPanel.saveData(); //为啥要保存数据??
			}
			closeType = BillDialog.CONFIRM;
			this.dispose(); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	public void onCancel() {
		closeType = BillDialog.CANCEL;
		returnBillVOs = null;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	public BillListPanel getListPanel() {
		return billlistPanel;
	}

	public BillVO[] getReturnBillVOs() {
		return returnBillVOs;
	}

	public BillListPanel getBilllistPanel() {
		return billlistPanel;
	}

	public WLTButton getBtn_confirm() {
		return btn_confirm;
	}

	public void setBtn_confirm(WLTButton btn_confirm) {
		this.btn_confirm = btn_confirm;
	}

	public WLTButton getBtn_cancel() {
		return btn_cancel;
	}

	public void setBtn_cancel(WLTButton btn_cancel) {
		this.btn_cancel = btn_cancel;
	}

	public void setCloseType(int closeType) {
		this.closeType = closeType;
	}
	/**
	 * 
	 * @param fag
	 * @return zzl[2019-5-20]
	 * 是否显示确定按钮
	 */
	public void setBtn_confirmVisible(Boolean fag){
		btn_confirm.setVisible(fag);
	}

}
