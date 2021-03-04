package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;

public class AnnouncementDialog extends BillDialog {
	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WLTButton wLTButton_back = new WLTButton("¹Ø±Õ");

	private BillCardPanel bcp1;

	JTabbedPane pane1 = null;

	public AnnouncementDialog(Container _parent, String title, int _width, int li_height, String tempcode, String _id) {
		super(_parent, title, _width, li_height);
		bcp1 = new BillCardPanel(tempcode);
		bcp1.queryDataByCondition("id=" + _id);

		bcp1.setEditable(false);

		JPanel jPanel_north = new JPanel();
		jPanel_north.setLayout(new FlowLayout(FlowLayout.CENTER));

		wLTButton_back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Onback();
			}
		});

		jPanel_north.add(wLTButton_back);

		this.getContentPane().add(jPanel_north, BorderLayout.SOUTH);
		this.getContentPane().add(bcp1, BorderLayout.CENTER);

	}

	public void Onback() {
		try {
			this.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public BillCardPanel getBillCardPanel(){
		return bcp1;
	}
}
