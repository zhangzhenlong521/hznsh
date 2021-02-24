package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.AbstractCustomerButtonBarPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.formatcomp.BillFormatDialog;
import cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02;

public class WMPanel_RegFormatPanel_CustBtnPanelCenter extends AbstractCustomerButtonBarPanel implements ActionListener {
	private static final long serialVersionUID = -5361637700923476284L;
	private WLTButton btn_1, btn_2, btn_3, btn_4; // 
	private TextArea textArea = new TextArea();

	@Override
	public void initialize() {
		this.setLayout(new FlowLayout()); //

		btn_3 = new WLTButton("导出XML格式注册样板"); //
		btn_4 = new WLTButton("导入XML格式注册样板"); //

		btn_3.addActionListener(this); //
		btn_4.addActionListener(this); // 

		this.add(btn_3); //
		this.add(btn_4); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_1) {
			onClicked_1();
		} else if (e.getSource() == btn_2) {
			onClicked_2(); //
		} else if (e.getSource() == btn_3) {
			exportRegFormat(); //
		} else if (e.getSource() == btn_4) {
			importRegFormat(); //
		}
	}

	//导入注册样板XML格式
	private void importRegFormat() {
		try {
			final BillDialog dialog = new BillDialog(this, "导入XML", 1000, 700);
			dialog.setLayout(new BorderLayout());
			textArea.setBackground(Color.WHITE); //
			textArea.setForeground(Color.BLUE); //
			textArea.setFont(new Font("宋体", Font.PLAIN, 12));
			textArea.select(0, 0); //

			JPanel jp = new JPanel();
			jp.setLayout(new FlowLayout(FlowLayout.CENTER));
			WLTButton confirm = new WLTButton("确定");
			WLTButton cancel = new WLTButton("取消");
			confirm.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					try {
						if (textArea == null || textArea.getText().trim().equals("") || textArea.getText() == null) {
							MessageBox.show("请输入需要导入的注册样板!");
							return;
						}
						FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
						service.importXMLRegFormat(textArea.getText());

						MessageBox.show("导入XML格式注册样板成功!");

					} catch (Exception ex) {
						ex.printStackTrace(); //
					}
					textArea.setText("");
					dialog.dispose();
				}
			});
			cancel.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					dialog.dispose();

				}
			});
			jp.add(confirm);
			jp.add(cancel);

			dialog.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER); //
			dialog.getContentPane().add(jp, BorderLayout.SOUTH);
			dialog.setVisible(true); //
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//导出注册样板XML格式
	private void exportRegFormat() {
		try {
			int li_count = ((AbstractStyleWorkPanel_02) this.getParentWorkPanel()).getBillListPanel().getTable().getSelectedRowCount();
			if (li_count <= 0) {
				MessageBox.showSelectOne(this);
				return;
			}

			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //

			int[] selected_rows = ((AbstractStyleWorkPanel_02) this.getParentWorkPanel()).getBillListPanel().getTable().getSelectedRows();
			String[] tempcode = new String[selected_rows.length];
			for (int i = 0; i < selected_rows.length; i++) {
				tempcode[i] = ((AbstractStyleWorkPanel_02) this.getParentWorkPanel()).getBillListPanel().getValueAt(selected_rows[i], "id").toString(); //
			}
			String sb_xml = service.exportXMLRegFormat(tempcode);
			BillDialog dialog = new BillDialog(this, "导出XML", 1000, 700);
			JTextArea textArea = new JTextArea(sb_xml); //
			textArea.setBackground(new Color(240, 240, 240)); //
			textArea.setForeground(Color.BLACK); //
			textArea.setFont(new Font("宋体", Font.PLAIN, 12));
			textArea.select(0, 0); //
			dialog.getContentPane().add(new JScrollPane(textArea)); //
			dialog.setVisible(true); //
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onClicked_1() {
		AbstractStyleWorkPanel_02 parentPanel = (AbstractStyleWorkPanel_02) this.getParentWorkPanel(); //
		BillVO billVO = ((AbstractStyleWorkPanel_02) this.getParentWorkPanel()).getBillListPanel().getSelectedBillVO();
		if (billVO == null) {
			return;
		}
		String str_code = billVO.getStringValue("code"); //
		BillFormatDialog dialog = new BillFormatDialog(this, "效果展示", str_code);
		dialog.setVisible(true); //
	}

	private void onClicked_2() {
		AbstractStyleWorkPanel_02 parentPanel = (AbstractStyleWorkPanel_02) this.getParentWorkPanel(); //
		try {
			BillVO[] billVOs = ((AbstractStyleWorkPanel_02) this.getParentWorkPanel()).getBillListPanel().getSelectedBillVOs(); //
			if (billVOs.length == 0) {
				MessageBox.showSelectOne(this);
				return;
			}

			JLabel label_1 = new JLabel("数据库类型:", JLabel.RIGHT); //
			label_1.setBounds(5, 5, 80, 20); //

			JRadioButton radio_sqlserver = new JRadioButton("SQLServer", true); //
			JRadioButton radio_oracle = new JRadioButton("Orcale"); //
			ButtonGroup group = new ButtonGroup(); //
			group.add(radio_sqlserver); //
			group.add(radio_oracle); //

			radio_sqlserver.setBounds(85, 5, 100, 20); ////
			radio_oracle.setBounds(185, 5, 80, 20); ////

			JLabel label_2 = new JLabel("SQL显示风格:"); //
			label_2.setBounds(265, 5, 80, 20); //

			JComboBox comBox_sqltype = new JComboBox(); //
			comBox_sqltype.addItem("横向"); //
			comBox_sqltype.addItem("纵向"); //
			comBox_sqltype.setBounds(345, 5, 80, 20); //

			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			panel.add(label_1); //
			panel.add(radio_sqlserver); //
			panel.add(radio_oracle); //
			panel.add(label_2); //
			panel.add(comBox_sqltype); //
			panel.setPreferredSize(new Dimension(440, 30)); //

			if (JOptionPane.showConfirmDialog(this, panel, "导出配置", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { //
				return;
			}

			String str_dbtype = ""; //
			if (radio_sqlserver.isSelected()) {
				str_dbtype = WLTConstants.SQLSERVER;
			} else if (radio_oracle.isSelected()) {
				str_dbtype = WLTConstants.ORACLE;
			}

			String[] str_codes = new String[billVOs.length]; //
			for (int i = 0; i < billVOs.length; i++) { //
				str_codes[i] = billVOs[i].getStringValue("code"); //
			}

			String str_sqls = UIUtil.getMetaDataService().getRegFormatPanelSQL(str_codes, str_dbtype, comBox_sqltype.getSelectedItem().toString()); //
			BillDialog dialog = new BillDialog(this, "导出数据", 1000, 700); //
			JTextArea textArea = new JTextArea(str_sqls); //
			textArea.setFont(new Font("宋体", Font.PLAIN, 12)); //
			textArea.select(0, 0); //
			dialog.getContentPane().add(new JScrollPane(textArea)); //
			dialog.setVisible(true); //
		} catch (Exception ex) {
			MessageBox.showException(parentPanel, ex); //
		}
	}

}
