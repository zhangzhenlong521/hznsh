package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class WLTButton_RegButtonImport implements WLTActionListener {
	private JTextArea textArea = new JTextArea(); //
	private BillPanel billpanel = null;

	public void actionPerformed(WLTActionEvent e) throws Exception {
		billpanel = e.getBillPanelFrom();
		final BillDialog dialog = new BillDialog(billpanel, "导入XML", 1000, 700);
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
						MessageBox.show(dialog, "请输入需要导入的注册按钮!!");
						return;
					}
					FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
					service.importXMLRegButton(textArea.getText());

					MessageBox.show(dialog, "导入XML格式注册按钮成功!!!");

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
