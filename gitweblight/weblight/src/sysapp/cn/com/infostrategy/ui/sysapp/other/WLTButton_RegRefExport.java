package cn.com.infostrategy.ui.sysapp.other;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class WLTButton_RegRefExport implements WLTActionListener {

	public void actionPerformed(WLTActionEvent e) throws Exception {
		BillListPanel listpanel = (BillListPanel) e.getBillPanelFrom();
		if (listpanel.getSelectedBillVO() == null) {
			MessageBox.show(listpanel, "请选择一个注册参照!");
			return;
		}
		FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //

		int[] selected_rows = listpanel.getSelectedRows();
		String[] tempcode = new String[selected_rows.length];
		for (int i = 0; i < selected_rows.length; i++) {
			tempcode[i] = listpanel.getValueAt(selected_rows[i], "id").toString(); //
		}
		String sb_xml = service.exportXMLRegRef(tempcode, selected_rows);

		BillDialog dialog = new BillDialog(listpanel, "导出XML", 1000, 700);
		JTextArea textArea = new JTextArea(sb_xml); //
		textArea.setBackground(new Color(240, 240, 240)); //
		textArea.setForeground(Color.BLACK); //
		textArea.setFont(new Font("宋体", Font.PLAIN, 12));
		textArea.select(0, 0); //
		dialog.getContentPane().add(new JScrollPane(textArea)); //
		dialog.setVisible(true); //

	}

}
