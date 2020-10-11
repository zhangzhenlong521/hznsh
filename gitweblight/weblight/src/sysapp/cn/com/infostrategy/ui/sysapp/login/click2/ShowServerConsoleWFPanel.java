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
 * �������˿���̨.
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
		btn_query = new WLTButton("ˢ�·���������̨"); //
		btn_querAndClear = new WLTButton("ˢ�²���շ���������̨"); //
		btn_copy = new WLTButton("����"); //
		btn_clear = new WLTButton("���ҳ��"); //
		check_wrapline = new JCheckBox("�Ƿ��Զ�����", false); //

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
		textArea.setFont(new Font("����", Font.PLAIN, 12)); //

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
			JFrame frame = new JFrame("�������˿���̨"); //
			frame.setSize(900, 500); //
			frame.setIconImage(UIUtil.getImage("office_194.gif").getImage()); //
			ShowServerConsoleWFPanel panel = new ShowServerConsoleWFPanel(); // ֮�����÷���,����Ϊ�����ڵ�¼ʱ���ظð�
			panel.initialize(); //
			frame.getContentPane().add(panel);
			frame.setVisible(true); //
			frame.toFront(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}


	/**
	 * ��������
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
	 * �鿴�������˿���̨..
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
