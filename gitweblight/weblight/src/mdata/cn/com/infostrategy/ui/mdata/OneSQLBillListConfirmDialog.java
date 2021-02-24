package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * 一条SQL构建一个查询界面的对话框
 * @author xch
 *
 */
public class OneSQLBillListConfirmDialog extends BillDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5638502120927195686L;
	private String sql = null;
	private BillListPanel billListPanel = null;

	private int closeType = -1; //
	private JButton btn_configm;
	private JButton btn_cancel;

	private boolean isHaveCancelBtn = true; //
	private boolean isMustReturn = false;

	public OneSQLBillListConfirmDialog(Container _parent, String _sql, String _title, int _width, int _height) {
		super(_parent, _title, _width, _height); //
		this.sql = _sql;
		initialize(); //
	}

	public OneSQLBillListConfirmDialog(Container _parent, String _sql, String _title, int _width, int _height, boolean _isHaveCancelBtn) {
		super(_parent, _title, _width, _height); //
		this.sql = _sql;
		this.isHaveCancelBtn = _isHaveCancelBtn; //
		initialize(); //
	}

	public OneSQLBillListConfirmDialog(Container _parent, String _sql, String _title, int _width, int _height, boolean _isHaveCancelBtn, boolean _isMustReturn) {
		super(_parent, _title, _width, _height); //
		this.sql = _sql;
		this.isHaveCancelBtn = _isHaveCancelBtn; //
		this.isMustReturn = _isMustReturn;
		initialize(); //
	}

	private void initialize() {
		billListPanel = new BillListPanel((String) null, this.sql); //
		billListPanel.setItemEditable(false); //
		billListPanel.getBillListBtnPanel().setVisible(false); //
		billListPanel.setToolbarVisiable(false); //
		billListPanel.deleteColumn("主键");
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(billListPanel, BorderLayout.CENTER); //
		JPanel panel = new JPanel(); //
		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON3) {
					showSQL(); //
				}
			}
		}); //

		panel.setLayout(new FlowLayout()); //
		btn_configm = new WLTButton("确定"); //
		btn_cancel = new WLTButton("取消"); //
		btn_configm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_configm); //
		if (isHaveCancelBtn) {
			panel.add(btn_cancel); //
		}
		this.getContentPane().add(panel, BorderLayout.SOUTH); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btn_configm)) {
			if (isMustReturn) {
				if (billListPanel.getSelectedRow() < 0) {
					MessageBox.showSelectOne(this);
					return; //
				}
			}
			closeType = 1; //
			this.dispose();
		} else if (e.getSource().equals(btn_cancel)) {
			closeType = 2; //
			this.dispose(); //
		}

	}

	public BillVO[] getSelectedBillVOs() {
		return billListPanel.getSelectedBillVOs(); //
	}

	private void showSQL() {
		MessageBox.showTextArea(this, this.sql); //
	}

	public int getCloseType() {
		return closeType;
	}

	public BillListPanel getBillListPanel() {
		return billListPanel;
	}
}
