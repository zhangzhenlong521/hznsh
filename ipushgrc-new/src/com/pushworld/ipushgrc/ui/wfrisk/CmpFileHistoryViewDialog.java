package com.pushworld.ipushgrc.ui.wfrisk;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * ��ϵ�ļ���ʷ�汾��ʾ����!!!
 * @author xch
 *
 */
public class CmpFileHistoryViewDialog extends BillDialog implements ActionListener {

	private String str_cmpfileId = null; //��ϵ�ļ�id
	private BillListPanel billList = null; //
	private boolean editable = false;//�Ƿ�ɱ༭����ɾ����ʷ�汾
	private IPushGRCServiceIfc service;//��Ʒ����ӿ�
	private WLTButton btn_delete, btn_deleteall, btn_close;

	public CmpFileHistoryViewDialog(Container _parent, String _title, String _cmpFileId, boolean _editable) {
		super(_parent, _title, 600, 400); //
		this.str_cmpfileId = _cmpFileId; //
		this.editable = _editable;
		initialize(); //
	}

	//����ҳ��!!!
	private void initialize() {
		String str_sql = "select * from cmp_cmpfile_hist where cmpfile_id='" + str_cmpfileId + "' and cmpfile_versionno not like '%._1'  order by cmpfile_versionno desc"; //����ʾС�汾�ŵļ�¼
		billList = new BillListPanel("CMP_CMPFILE_HIST_CODE1"); //
		billList.queryDataByDS(null, str_sql); //
		if (this.editable) {
			try {
				service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
			btn_delete = new WLTButton("ɾ��");
			btn_deleteall = new WLTButton("ɾ������");
			btn_delete.addActionListener(this);
			btn_deleteall.addActionListener(this);
			billList.addBatchBillListButton(new WLTButton[] { btn_delete, btn_deleteall });
			billList.repaintBillListButton();
		}
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billList, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) {
			onDeleteOne();
		} else if (e.getSource() == btn_deleteall) {
			onDeleteAll();
		} else if (e.getSource() == btn_close) {
			onClose();
		}

	}

	private void onDeleteOne() {
		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (billList.getSelectedRow() == 0) {
			MessageBox.show(this, "���°汾������ɾ��!"); //
			return;
		}
		if (MessageBox.showConfirmDialog(this, "�ò�����ɾ���ð汾��������Ϣ,�Ƿ����?") != JOptionPane.YES_OPTION) {
			return;
		} else {
			try {
				service.deleteCmpFileHistById(billVO.getStringValue("id"));
				billList.removeSelectedRow();
				MessageBox.show(this, "ɾ���ɹ�!");
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
	}

	private void onDeleteAll() {
		if (billList.getRowCount() == 0) {
			MessageBox.show(this, "�������ļ��Ѳ�������ʷ�汾!"); //
			return;
		}
		if (MessageBox.showConfirmDialog(this, "�Ƿ�Ҫɾ���������ļ���������ʷ�汾?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		if (MessageBox.showConfirmDialog(this, "�ò�����ɾ���������ļ���������ʷ�汾(������ɾ��),�Ƿ����?") != JOptionPane.YES_OPTION) {
			return;
		} else {
			try {
				service.deleteAllCmpFileHistByCmpfileId(str_cmpfileId);
				billList.removeAllRows();
				MessageBox.show(this, "ɾ���ɹ�!");
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
	}

	private void onClose() {
		this.dispose();
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_close = new WLTButton("�ر�");
		btn_close.addActionListener(this); //
		panel.add(btn_close); //
		return panel;
	}
}
