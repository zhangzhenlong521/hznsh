package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;

public class ShowServerFrame extends JFrame implements ActionListener {

	private WLTButton btn_refresh, btn_clear, btn_close = null; //
	private JTextArea textArea = null; //

	public ShowServerFrame() {
		super("查看服务器端日志"); //
		this.setSize(1000, 700); //

		initialize(); //

		this.setVisible(true); //
	}
	
	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		ShowServerFrame frame = new ShowServerFrame();
		frame.toFront(); //
	}


	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //

		JPanel panel_south = new JPanel(new FlowLayout(FlowLayout.CENTER)); //
		btn_refresh = new WLTButton("刷新"); //
		btn_clear = new WLTButton("清空"); //
		btn_close = new WLTButton("关闭"); //
		btn_refresh.addActionListener(this); //
		btn_clear.addActionListener(this); //
		btn_close.addActionListener(this); //
		panel_south.add(btn_refresh); //
		panel_south.add(btn_clear); //
		panel_south.add(btn_close); //
		this.add(panel_south, BorderLayout.SOUTH); //

		textArea = new JTextArea(); //
		textArea.setBackground(new Color(253, 253, 253)); //
		textArea.setEditable(false); //

		JScrollPane scrollPanel = new JScrollPane(textArea); //
		this.add(scrollPanel, BorderLayout.CENTER); //

		refreshLog(); //刷新日志数据
	}

	/**
	 * 刷新日志
	 */
	private void refreshLog() {
		textArea.setText(""); //清空数据
		try {
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			String str_serverlog = service.getServerLog(); //服务器端日志..
			textArea.setText(str_serverlog); ////
		} catch (Exception e) {
			e.printStackTrace(); //
			textArea.setText("取服务器日志发生异常,请至控制台查看异常!!"); //
		}

	}

	/**
	 * 点击按钮事件
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_refresh) {
			refreshLog();
		} else if (e.getSource() == btn_clear) {
			textArea.setText(""); //清空数据
		} else if (e.getSource() == btn_close) {
			this.dispose(); //
		}
	}
}
