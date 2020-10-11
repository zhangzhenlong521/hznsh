package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * 服务器端控制台.
 * @author xch
 *
 */
public class ShowServerConsoleWFPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L; //

	private WLTButton btn_query = null;
	private WLTButton btn_querAndClear = null;

	private WLTButton btn_clear = null; //
	private JCheckBox check_wrapline = null; //
	private WLTButton btn_copy = null; //

	private JTextArea textArea = null;

	@Override
	public void initialize() {
		btn_query = new WLTButton("刷新服务器控制台"); //
		btn_querAndClear = new WLTButton("刷新并清空服务器控制台"); //
		btn_copy = new WLTButton("拷贝"); //
		btn_clear = new WLTButton("清空页面"); //
		check_wrapline = new JCheckBox("是否自动换行", false); //

		btn_query.addActionListener(this);
		btn_querAndClear.addActionListener(this);
		btn_copy.addActionListener(this);
		btn_clear.addActionListener(this);
		check_wrapline.addActionListener(this); //

		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel_north.add(btn_query); //
		panel_north.add(btn_querAndClear); //
		panel_north.add(btn_clear); //
		panel_north.add(check_wrapline); //
		panel_north.add(btn_copy); //

		textArea = new JTextArea(); //
		textArea.setEditable(false); //
		textArea.setLineWrap(false);
		textArea.setFont(new Font("宋体", Font.PLAIN, 12)); //

		try {
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			String str_text = service.getServerConsole(false); //
			textArea.setText(str_text); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		this.setLayout(new BorderLayout()); //
		this.add(panel_north, BorderLayout.NORTH); //
		this.add(new JScrollPane(textArea), BorderLayout.CENTER); //
	}
	
	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		try {
			JFrame frame = new JFrame("服务器端控制台"); //
			frame.setSize(900, 500); //
			frame.setIconImage(UIUtil.getImage("office_194.gif").getImage()); //
			ShowServerConsoleWFPanel panel = new ShowServerConsoleWFPanel(); // 之所以用反射,是因为不起在登录时下载该包
			panel.initialize(); //
			frame.getContentPane().add(panel);
			frame.setVisible(true); //
			frame.toFront(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}


	/**
	 * 动作监听
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_query) {
			onQueryServerConsole(false); //
		} else if (e.getSource() == btn_querAndClear) {
			onQueryServerConsole(true); //
		} else if (e.getSource() == btn_copy) {
			StringSelection ss = new StringSelection(textArea.getText());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
		} else if (e.getSource() == btn_clear) {
			textArea.setText(""); //
		} else if (e.getSource() == check_wrapline) {
			textArea.setLineWrap(check_wrapline.isSelected()); //
			//textArea.updateUI(); //
		}
	}

	/**
	 * 查看服务器端控制台..
	 */
	private void onQueryServerConsole(boolean _isclear) {
		try {
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			String str_text = service.getServerConsole(_isclear); //
			textArea.setText(str_text); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

}
