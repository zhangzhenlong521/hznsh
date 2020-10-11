package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.HFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class WLTButton_Process_Copy implements WLTActionListener {
	private String wfcode, wfname;
	private BillListPanel listpanel = null;

	public void actionPerformed(WLTActionEvent e) throws Exception {
		listpanel = (BillListPanel) e.getBillPanelFrom();
		if (listpanel.getSelectedBillVO() == null) {
			MessageBox.show(listpanel, "请选择要复制的流程!");
			return;
		}
		BillImportDialog importdialog = new BillImportDialog(e.getBillPanelFrom());
		importdialog.setVisible(true);
		if (importdialog.getCloseType() == 1) {
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //

			int[] selected_rows = listpanel.getSelectedRows();
			String[] tempcode = new String[selected_rows.length];
			for (int i = 0; i < selected_rows.length; i++) {
				tempcode[i] = listpanel.getValueAt(selected_rows[i], "id").toString(); //
			}
			String str_xml = service.exportXMLProcess(tempcode, selected_rows);

			StringBuffer sb_xml = new StringBuffer();
			sb_xml.append(str_xml.substring(0, str_xml.indexOf("<code>")));
			sb_xml.append("<code>" + wfcode + "</code><name>" + wfname + "</name>");
			sb_xml.append(str_xml.substring(str_xml.indexOf("</name>") + 7));

			service.importXMLProcess_Copy(sb_xml.toString());
			listpanel.refreshData();
		}

	}

	class BillImportDialog extends BillDialog implements ActionListener {
		private static final long serialVersionUID = 8541645633564644249L;
		JTextField codefiled, namefiled;
		private WLTButton btn_confirm, btn_cancel;
		private int closeType = -1;

		public BillImportDialog(Container _parent) {
			super(_parent, "复制流程");
			initialize();
		}

		private void initialize() {
			this.getContentPane().setLayout(new BorderLayout());
			this.setSize(400, 300); //设置建议宽度与高度
			int li_screenwidth = (int) UIUtil.getScreenMaxDimension().getWidth(); //
			int li_screenheight = (int) UIUtil.getScreenMaxDimension().getHeight(); //
			int li_x = (li_screenwidth - 400) / 2;
			int li_y = (li_screenheight - 300) / 2;
			li_x = (li_x < 0 ? 0 : li_x);
			li_y = (li_y < 0 ? 0 : li_y);
			this.setLocation(li_x, li_y); //

			JPanel jpanel = new JPanel();
			jpanel.setLayout(new BorderLayout());
			JLabel codelabel = new JLabel("      *流程编码：");
			JLabel namelabel = new JLabel("      *流程名称：");
			codefiled = new JTextField();
			namefiled = new JTextField();
			codefiled.setPreferredSize(new Dimension(200, 25));
			namefiled.setPreferredSize(new Dimension(200, 25));

			ArrayList hlist = new ArrayList();
			hlist.add(codelabel);
			hlist.add(codefiled);
			ArrayList hlist1 = new ArrayList();
			hlist1.add(namelabel);
			hlist1.add(namefiled);

			HFlowLayoutPanel hfp = new HFlowLayoutPanel(hlist);
			HFlowLayoutPanel hfp1 = new HFlowLayoutPanel(hlist1);

			ArrayList vlist = new ArrayList();
			JLabel label = new JLabel("");
			label.setPreferredSize(new Dimension(40, 40));
			JLabel label1 = new JLabel("");
			label.setPreferredSize(new Dimension(20, 40));
			vlist.add(label);
			vlist.add(hfp);
			vlist.add(label1);
			vlist.add(hfp1);
			VFlowLayoutPanel vfp = new VFlowLayoutPanel(vlist);

			jpanel.add(vfp, BorderLayout.NORTH);
			this.getContentPane().add(jpanel, BorderLayout.CENTER); //
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
			if (codefiled.getText() == null || "".equals(codefiled.getText().trim())) {
				MessageBox.show(this, "请输入流程编码！");
				return;
			}
			if (namefiled.getText() == null || "".equals(namefiled.getText().trim())) {
				MessageBox.show(this, "请输入流程名称！");
				return;
			}
			wfcode = codefiled.getText().trim();
			wfname = namefiled.getText().trim();
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
	}
}
