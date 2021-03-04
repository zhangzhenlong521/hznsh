package cn.com.pushworld.salary.ui.feedback.p010;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;

public class CreatePanel extends JComponent implements ActionListener {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_submit = new WLTButton("提交");
	private WLTButton btn_cancel = new WLTButton("取消");
	BillCardPanel cardpanel ;
	public CreatePanel(String cardTemplet){
		this.setLayout(new BorderLayout());
		cardpanel = new BillCardPanel(cardTemplet);
		cardpanel.insertRow();
		cardpanel.setEditableByInsertInit();
		this.add(cardpanel,BorderLayout.CENTER);
		JPanel btnpanel  = new JPanel(new FlowLayout(FlowLayout.CENTER,3,3));
		btnpanel.setOpaque(false);
		btnpanel.add(btn_submit);
		btnpanel.add(btn_cancel);
		btn_submit.addActionListener(this);
		btn_cancel.addActionListener(this);
		this.add(btnpanel,BorderLayout.SOUTH);
	}
	public void actionPerformed(ActionEvent actionevent) {
		if(actionevent.getSource() == btn_cancel){
			this.setVisible(false);
		}else if(actionevent.getSource() == btn_submit){
			if(!cardpanel.checkValidate()){
				return;
			}
			String sql = cardpanel.getInsertSQL();
			try {
				UIUtil.executeUpdateByDS(null, sql);
				this.setVisible(false);
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
