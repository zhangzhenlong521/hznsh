/**************************************************************************
 * $RCSfile: WorkFlowFrame.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:18:02 $
 **************************************************************************/
package cn.com.infostrategy.ui.workflow.design;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;

public class WorkFlowFrame extends BillFrame implements ActionListener {
	private String processid = null;
	private boolean editable = true;
	private WorkFlowDesignWPanel wfpanel;
	private WLTButton btn_confirm, btn_cancel = null; //

	public WorkFlowFrame(Container _parentContainer, String _title, String _processid) {
		this(_parentContainer, _title, _processid, 1000, 700);
	}

	public WorkFlowFrame(Container _parentContainer, String _title, String _processid, int _width, int _height) {
		super(_parentContainer, _title, _width, _height);
		this.processid = _processid;
		initialize();
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
		wfpanel = new WorkFlowDesignWPanel(); //
		wfpanel.loadGraphByID(processid);
		wfpanel.setAllBtnEnable();
		this.getContentPane().add(wfpanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
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

	public void onConfirm() {
		setCloseType(BillFrame.CONFIRM);
		wfpanel.onSaveWfProcess();
		this.dispose(); //
	}

	public void onCancel() {
		setCloseType(BillFrame.CANCEL);
		this.dispose();
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		wfpanel.setToolBarVisiable(editable);
		btn_confirm.setVisible(editable);
		if (editable) {
			btn_cancel.setText("取消");
		} else {
			btn_cancel.setText("关闭");
		}
	}

}
