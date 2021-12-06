package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;

/**
 * ����ͼ��ƴ���
 * ע���޸Ĵ��࣬Ҫ�����Ƿ�ͬʱ�޸�WFGraphEditFrame�࣡����
 * @author lcj
 *
 */
public class WFGraphEditDialog extends BillDialog implements ActionListener {
	private String cmpfileid;
	private String cmpfilename;
	private String[][] processes;
	private boolean editable = true;
	private boolean showRefPanel = true;//�Ƿ���ʾ��ť���
	private WFGraphEditPanel graphPanel;
	private WLTButton btn_confirm, btn_cancel;

	public WFGraphEditDialog(Container _parent, String _title, int _width, int li_height, String _cmpfileid, String _cmpfilename, String[][] _processes, boolean _editable) {
		this(_parent, _title, _width, li_height, _cmpfileid, _cmpfilename, _processes, _editable, true);
	}

	/**
	 * ����һ�����췽�������������Ƿ���ʾ��һ�����̣���ʱ��Ҫ��WFGraphEditPanel��һЩ���ԣ���WFGraphEditItemPanel���������������壩����ʾ��ͬԪ�ء����/2012-06-13��
	 * @param _parent
	 * @param _title
	 * @param _width
	 * @param li_height
	 * @param _cmpfileid
	 * @param _cmpfilename
	 * @param _processes
	 * @param _editable
	 * @param _showlevel
	 */
	public WFGraphEditDialog(Container _parent, String _title, int _width, int li_height, String _cmpfileid, String _cmpfilename, String[][] _processes, boolean _editable, boolean _showlevel) {
		super(_parent, _title, _width, li_height);
		this.cmpfileid = _cmpfileid;
		this.cmpfilename = _cmpfilename;
		this.processes = _processes;
		this.editable = _editable;
		initialize();
		if (_showlevel) {
			showFirstProcess();
		}
	}

	public WFGraphEditPanel getGraphPanel() {
		return graphPanel;
	}

	public int getCloseType() {
		return closeType;
	}

	public void setCloseType(int closeType) {
		this.closeType = closeType;
	}

	/**
	 * ��������ʼ������
	 */
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		graphPanel = new WFGraphEditPanel(this, this.cmpfileid, this.cmpfilename, this.processes, editable); //
		this.getContentPane().add(graphPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		if (editable) {
			btn_confirm = new WLTButton("ȷ��");
			btn_cancel = new WLTButton("ȡ��");
			btn_cancel.addActionListener(this); //
			btn_confirm.addActionListener(this); //
			panel.add(btn_confirm); //
			panel.add(btn_cancel); //
		} else {
			btn_cancel = new WLTButton("�ر�");
			btn_cancel.addActionListener(this); //
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

	public void showFirstProcess() {
		if (processes != null && processes.length > 0) {//�����/2014-08-21��
			graphPanel.showLevel(processes[0][0]);
		}
	}

	public void onConfirm() {
		setCloseType(BillDialog.CONFIRM);
		graphPanel.isWfEditChanged();
		this.dispose(); //
	}

	public void onCancel() {
		graphPanel.deleteAllAddActivity();//ȡ��Ĭ�ϲ����棬��Ҫ�������Ļ��ڼ������Ϣɾ�����������������ݡ����/2012-08-20��
		setCloseType(BillDialog.CANCEL);
		this.dispose();
	}

	/**
	 * ����������Ͻǵ�X���ڴ��ڹر�ǰ��!
	 */
	public void closeMe() {
		onConfirm();
	}

	public boolean isShowRefPanel() {
		return showRefPanel;
	}

	public void setShowRefPanel(boolean _showRefPanel) {
		if (this.showRefPanel == _showRefPanel) {
			return;
		}
		this.showRefPanel = _showRefPanel;
		graphPanel.setShowRefPanel(showRefPanel);
	}

}
