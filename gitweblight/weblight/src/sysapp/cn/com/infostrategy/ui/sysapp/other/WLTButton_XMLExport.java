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
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class WLTButton_XMLExport implements WLTActionListener {

	private JTextArea textArea = null;
	private BillDialog dialog = null;
	private WLTButton confirm = null;
	private WLTButton cancel = null;
	private String input = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		textArea = new JTextArea();
		dialog = new BillDialog(_event.getBillPanelFrom(), "导入XML", 1000, 700);
		dialog.setLayout(new BorderLayout());
		textArea.setBackground(Color.WHITE);
		textArea.setForeground(Color.BLUE);
		textArea.setFont(new Font("宋体", Font.PLAIN, 12));
		textArea.select(0, 0);

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		confirm = new WLTButton("确定");
		cancel = new WLTButton("取消");
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				input = textArea.getText();
				StringBuffer text = null;
				try {
					if (input == null || input.trim().equals("")) {
						return;
					} else {
						text = new StringBuffer();
						text.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n");
						text.append("<root>\r\n");
						text.append(input + "\r\n");
						text.append("</root>");
						FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
						service.importXML(text.toString());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				MessageBox.show(dialog, "导入成功！");
				textArea.setText("");
				dialog.dispose();
			}

		});
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}

		});
		panel.add(confirm);
		panel.add(cancel);

		dialog.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER); //
		dialog.getContentPane().add(panel, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

}
