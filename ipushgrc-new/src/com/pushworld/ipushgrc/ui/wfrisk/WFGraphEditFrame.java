package com.pushworld.ipushgrc.ui.wfrisk;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;

import com.pushworld.ipushgrc.ui.wfrisk.p010.WFGraphEditPanel;

/**
 * ����ͼ���Frame����
 * ע���޸Ĵ��࣬Ҫ�����Ƿ�ͬʱ�޸�WFGraphEditDialog�࣡����
 * @author lcj
 *
 */
public class WFGraphEditFrame extends BillFrame implements ActionListener {
	private String cmpfileid;
	private String cmpfilename;
	private String[][] processes;
	private boolean editable;
	private WFGraphEditPanel graphPanel;
	private WLTButton btn_confirm, btn_cancel;

	public WFGraphEditFrame(Container _parent, String _title, int _width, int li_height, String _cmpfileid, String _cmpfilename, String[][] _processes, boolean _editable) {
		super(_parent, _title, _width, li_height);
		this.cmpfileid = _cmpfileid;
		this.cmpfilename = _cmpfilename;
		this.processes = _processes;
		this.editable = _editable;
		initialize();
	}

	public WFGraphEditPanel getGraphPanel() {
		return graphPanel;
	}

	/**
	 * ��������ʼ������
	 */
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		graphPanel = new WFGraphEditPanel(this, this.cmpfileid, this.cmpfilename, this.processes, editable); //
		graphPanel.showLevel(processes[0][0]);
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

	public void onConfirm() {
		setCloseType(BillFrame.CONFIRM);
		graphPanel.isWfEditChanged();
		this.dispose(); //
	}

	public void onCancel() {
		graphPanel.deleteAllAddActivity();//ȡ��Ĭ�ϲ����棬��Ҫ�������Ļ��ڼ������Ϣɾ�����������������ݡ����/2012-08-20��
		setCloseType(BillFrame.CANCEL);
		this.dispose();
	}

	/**
	 * ����������Ͻǵ�X���ڴ��ڹر�ǰ��!
	 */
	public void closeMe() {
		onConfirm();
	}

	public WLTButton getBtn_cancel() {
		return btn_cancel;
	}
}
