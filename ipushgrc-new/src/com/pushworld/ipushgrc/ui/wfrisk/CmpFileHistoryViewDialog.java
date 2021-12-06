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
 * 体系文件历史版本显示窗口!!!
 * @author xch
 *
 */
public class CmpFileHistoryViewDialog extends BillDialog implements ActionListener {

	private String str_cmpfileId = null; //体系文件id
	private BillListPanel billList = null; //
	private boolean editable = false;//是否可编辑，即删除历史版本
	private IPushGRCServiceIfc service;//产品服务接口
	private WLTButton btn_delete, btn_deleteall, btn_close;

	public CmpFileHistoryViewDialog(Container _parent, String _title, String _cmpFileId, boolean _editable) {
		super(_parent, _title, 600, 400); //
		this.str_cmpfileId = _cmpFileId; //
		this.editable = _editable;
		initialize(); //
	}

	//构造页面!!!
	private void initialize() {
		String str_sql = "select * from cmp_cmpfile_hist where cmpfile_id='" + str_cmpfileId + "' and cmpfile_versionno not like '%._1'  order by cmpfile_versionno desc"; //不显示小版本号的记录
		billList = new BillListPanel("CMP_CMPFILE_HIST_CODE1"); //
		billList.queryDataByDS(null, str_sql); //
		if (this.editable) {
			try {
				service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
			btn_delete = new WLTButton("删除");
			btn_deleteall = new WLTButton("删除所有");
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
			MessageBox.show(this, "最新版本不建议删除!"); //
			return;
		}
		if (MessageBox.showConfirmDialog(this, "该操作将删除该版本的所有信息,是否继续?") != JOptionPane.YES_OPTION) {
			return;
		} else {
			try {
				service.deleteCmpFileHistById(billVO.getStringValue("id"));
				billList.removeSelectedRow();
				MessageBox.show(this, "删除成功!");
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
	}

	private void onDeleteAll() {
		if (billList.getRowCount() == 0) {
			MessageBox.show(this, "该流程文件已不存在历史版本!"); //
			return;
		}
		if (MessageBox.showConfirmDialog(this, "是否要删除该流程文件的所有历史版本?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		if (MessageBox.showConfirmDialog(this, "该操作将删除该流程文件的所有历史版本(不建议删除),是否继续?") != JOptionPane.YES_OPTION) {
			return;
		} else {
			try {
				service.deleteAllCmpFileHistByCmpfileId(str_cmpfileId);
				billList.removeAllRows();
				MessageBox.show(this, "删除成功!");
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
		btn_close = new WLTButton("关闭");
		btn_close.addActionListener(this); //
		panel.add(btn_close); //
		return panel;
	}
}
