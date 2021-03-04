package cn.com.infostrategy.ui.mdata.formatcomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;

/**
 * 通过一个注册面板来生成一个对话框!非常有用!
 * 以后都不要写BillDialog了!
 * @author xch
 *
 */
public class BillFormatDialog extends BillDialog implements ActionListener {
	private static final long serialVersionUID = 8541645633564644249L;

	private WLTButton btn_confirm, btn_cancel;
	private int closeType = -1;
	private BillFormatPanel billformatPanel = null; //

	public BillFormatDialog(Container _parent, String _regcode) {
		super(_parent, _regcode); //
		initialize(_parent, _regcode); //
	}

	public BillFormatDialog(Container _parent, String _title, String _regcode) {
		super(_parent, _title); //
		initialize(_parent, _regcode); //
	}

	/**
	 * 初始化页面...
	 * @param _parent
	 * @param _templetcode
	 */
	private void initialize(Container _parent, String _templetcode) {
		this.getContentPane().setLayout(new BorderLayout());
		billformatPanel = BillFormatPanel.getRegisterFormatPanel(_templetcode); //
		this.setSize(billformatPanel.getSuggestWidth(), billformatPanel.getSuggestHeight()); //设置建议宽度与高度
		int li_screenwidth = (int) UIUtil.getScreenMaxDimension().getWidth(); //
		int li_screenheight = (int) UIUtil.getScreenMaxDimension().getHeight(); //
		int li_x = (li_screenwidth - billformatPanel.getSuggestWidth()) / 2;
		int li_y = (li_screenheight - billformatPanel.getSuggestHeight()) / 2;
		li_x = (li_x < 0 ? 0 : li_x);
		li_y = (li_y < 0 ? 0 : li_y);
		this.setLocation(li_x, li_y); //

		this.getContentPane().add(billformatPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	public void onConfirm() {
		closeType = BillDialog.CONFIRM;
		this.dispose(); //
	}

	public void onCancel() {
		closeType = BillDialog.CANCEL;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	public BillFormatPanel getBillformatPanel() {
		return billformatPanel;
	}

}
