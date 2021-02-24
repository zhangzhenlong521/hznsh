package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

public class ImportDBXMLWFPanel extends AbstractWorkPanel {
	private WLTButton importButton;
	private JTextArea textArea = new JTextArea(); //
	JLabel wf_codeLabel;
	JLabel wf_nameLabel;
	JTextField wf_code;
	JTextField wf_name;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		textArea = new JTextArea(); //
		wf_codeLabel = new JLabel("    流程编码：  ");
		wf_nameLabel = new JLabel("    流程名称：  ");
		wf_code = new JTextField();
		wf_name = new JTextField();
		wf_code.setPreferredSize(new Dimension(160, 30));
		wf_name.setPreferredSize(new Dimension(160, 30));
		JLabel next = new JLabel("        ");
		importButton = new WLTButton("导入");
		importButton.setPreferredSize(new Dimension(80, 30));
		importButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onImport();

			}
		});

		this.setLayout(new FlowLayout());
		this.add(wf_codeLabel);
		this.add(wf_code);
		this.add(wf_nameLabel);
		this.add(wf_name);
		this.add(next);
		this.add(importButton);

	}

	private void onImport() {

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
						MessageBox.show("请输入需要导入的流程!!");
						return;
					}
					FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
					String text = textArea.getText();
					text = text.replace("utf-8", "GBK");

					//service.importDBXMLProcess(wf_code.getText().trim(), wf_name.getText().trim(), text, null);

					MessageBox.show("导入XML格式流程成功!!!");

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

	}

}
