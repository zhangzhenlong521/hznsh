package cn.com.infostrategy.ui.workflow.design;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

public class ChoosePhaseDialog extends BillDialog implements ActionListener, BillListSelectListener {

	private static final long serialVersionUID = 1L;
	private JTextField textField = null;
	private BillListPanel billlistPanel_phase = null;
	private WLTButton btn_confirm, btn_cancel;
	private boolean showpreviewdept = true;

	private int closeType = -1;

	public ChoosePhaseDialog(Container _parent, String _title, String _type, String _sqlcons, boolean _showpreviewdept) {
		super(_parent, _title, 350, 350); //选择阶段
		this.showpreviewdept = _showpreviewdept;
		if (showpreviewdept) {//因为农行一图两表中没有模板主表子表、下拉框注册字典、平台参数配置表等，根据传参_showpreviewdept来判断是否显示预设部门。
			billlistPanel_phase = new BillListPanel(new DefaultTMO(_type, new String[][] { { "名称", "250" } })); //
			billlistPanel_phase.QueryData("select name 名称 from pub_comboboxdict where type='" + _sqlcons + "' order by seq"); //
			billlistPanel_phase.addBillListSelectListener(this); //
			this.getContentPane().add(billlistPanel_phase, BorderLayout.CENTER); //
			this.getContentPane().add(getNorthPanel(_type), BorderLayout.NORTH); //
		} else {
			this.setTitle("填写" + _type);
			this.getContentPane().add(getNorthPanel(_type), BorderLayout.CENTER); //
			this.setSize(350, 130);
		}
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getNorthPanel(String _type) {
		JPanel panel = null;
		JLabel label = new JLabel(_type, SwingConstants.RIGHT); //阶段名称
		textField = new JTextField(); //
		label.setPreferredSize(new Dimension(50, 20));
		textField.setPreferredSize(new Dimension(175, 20));
		if (showpreviewdept) {
			panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
		} else {
			panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER, 5, 25));
		}
		panel.add(label);
		panel.add(textField);
		return panel; //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //

		return panel;
	}

	public int getCloseType() {
		return closeType;
	}

	public String getPhaseName() {
		return textField.getText();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			if (textField.getText() == null || textField.getText().trim().equals("")) {
				MessageBox.show(this, "机构/阶段名称不能为空!"); //
				return;
			}

			closeType = 1;
			this.dispose();
		} else if (e.getSource() == btn_cancel) {
			closeType = 2;
			this.dispose();
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		String str_name = _event.getCurrSelectedVO().getStringValue("名称");
		textField.setText(str_name); //
	}

}
