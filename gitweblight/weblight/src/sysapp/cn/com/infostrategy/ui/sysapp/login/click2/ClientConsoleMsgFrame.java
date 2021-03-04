package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import cn.com.infostrategy.to.common.ConsoleMsgVO;
import cn.com.infostrategy.to.common.Queue;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * �ͻ��˿���̨��ʷ��¼!
 * @author xch
 *
 */
public class ClientConsoleMsgFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L; //
	private JButton btn_clearText, btn_refresh, btn_copy, btn_showCookieDialog; //
	private JCheckBox checkBox_isAutoRefresh = null; //

	private JTextPane msgPanel = new NoWrapJTextPane(); //
	private StyledDocument doc; //
	private Style style1, style2; //
	private Font font = new Font("����", Font.PLAIN, 12);
	private Timer timer = new Timer(); //ѭ��ִ�е�.

	private static ClientConsoleMsgFrame consoleFrame = null; //����̨����!!!

	//��ʾ����̨����
	public static void showConsoleFrame() {
		if (consoleFrame == null) {
			consoleFrame = new ClientConsoleMsgFrame(); //			
		}
		consoleFrame.setVisible(true); //
		consoleFrame.toFront(); //��ǰ����
	}
	
	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		showConsoleFrame();
	}

	private ClientConsoleMsgFrame() {
		this.setTitle("�ͻ��˿���̨"); //
		this.setIconImage(UIUtil.getImage("office_197.gif").getImage()); //
		this.setSize(900, 600); //
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				closeWindow(); //
			}

		});
		msgPanel.setEditable(false); //
		msgPanel.setFont(font); ////
		doc = (StyledDocument) msgPanel.getDocument(); ////
		style1 = doc.addStyle("StyleName1", null);
		style2 = doc.addStyle("StyleName2", null);
		StyleConstants.setForeground(style1, Color.BLACK);
		StyleConstants.setForeground(style2, Color.RED);

		JScrollPane scrollPanel = new JScrollPane(msgPanel); //

		this.getContentPane().add(scrollPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getNorthPanel(), BorderLayout.NORTH); //
		loadClientMsg(); //

		this.toFront(); //
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				autoRefreshMsg(); //ÿ��һ��ˢ��
			}
		}, 1000, 900); //

		//this.setVisible(true); //
	}

	private Component getNorthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		btn_clearText = new JButton("���"); //
		btn_copy = new JButton("����"); //
		btn_refresh = new JButton("ˢ��"); //
		btn_showCookieDialog = new JButton("Cookie��������"); //

		checkBox_isAutoRefresh = new JCheckBox("�Ƿ��Զ�ˢ��", true);
		btn_clearText.setFont(font); //
		btn_copy.setFont(font); //
		btn_refresh.setFont(font); //
		checkBox_isAutoRefresh.setFont(font); //

		btn_clearText.setPreferredSize(new Dimension(65, 20)); //
		btn_copy.setPreferredSize(new Dimension(65, 20)); //
		btn_refresh.setPreferredSize(new Dimension(65, 20)); ////
		checkBox_isAutoRefresh.setPreferredSize(new Dimension(120, 20)); //
		btn_showCookieDialog.setPreferredSize(new Dimension(120, 20)); //

		btn_clearText.addActionListener(this);
		btn_copy.addActionListener(this);
		btn_refresh.addActionListener(this);
		checkBox_isAutoRefresh.addActionListener(this);
		btn_showCookieDialog.addActionListener(this); //

		panel.add(btn_clearText); //
		panel.add(btn_refresh); //
		panel.add(checkBox_isAutoRefresh); //
		panel.add(btn_copy); //
		panel.add(btn_showCookieDialog); //

		return panel; //
	}

	/**
	 * �Զ�ˢ��
	 */
	private void autoRefreshMsg() {
		if (this.isVisible() && checkBox_isAutoRefresh.isSelected()) { //�����ѡ��ѡ���˲�ˢ��
			loadClientMsg(); //
		}
	}

	/**
	 * ��������
	 */
	private void loadClientMsg() {
		Queue queue = ClientEnvironment.getClientConsoleQueue(); //�ͻ��˿���̨��Ϣ
		try {
			while (!queue.empty()) { //ֻҪû��
				ConsoleMsgVO msgVO = (ConsoleMsgVO) queue.pop(); //ȡ��ǰ���
				if (msgVO.getType() == ConsoleMsgVO.SYSTEM_OUT) {
					doc.insertString(doc.getLength(), msgVO.getMsg(), style1); //
				} else if (msgVO.getType() == ConsoleMsgVO.SYSTEM_ERROR) {
					doc.insertString(doc.getLength(), msgVO.getMsg(), style2); //�Ȳ���
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
		int li_currlength = msgPanel.getDocument().getLength();
		if (li_currlength > 50000) { //�������3��5ǧ���֣���ȥ��ǰ���.
			try {
				msgPanel.getDocument().remove(0, li_currlength - 50000); //��ɾ��
			} catch (Exception e) {
				e.printStackTrace(); //
			}
		}
	}

	/**
	 * �رմ���...
	 */
	private void closeWindow() {
		this.setVisible(false); ////
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_refresh) {
			loadClientMsg(); //�ֶ�ˢ��
		} else if (e.getSource() == btn_copy) { //����
			StringSelection ss = new StringSelection(msgPanel.getText());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			JOptionPane.showMessageDialog(this, "�ѳɹ�������ϵͳ��ճ��!��򿪼��±�Ctrl+Vճ����ȥ!"); //
		} else if (e.getSource() == btn_clearText) { //���
			msgPanel.setText(""); //
		} else if (e.getSource() == btn_showCookieDialog) { //
			JDialog dialog = MonitorIECookieDialog.getInstance(); //
			if (dialog == null) {
				JOptionPane.showMessageDialog(this, "û�д���MonitorIECookieDialog,ԭ��:\r\n" + MonitorIECookieDialog.getNotLoadReason() + "\r\n��ǰ��¼ģʽ[" + System.getProperty("REQ_logintype") + "]"); //
			} else {
				dialog.setLocation(0, 0); //
				dialog.setVisible(true); //
				dialog.toFront(); //
			}
		}
	}

	//�����еķ���ı���
	class NoWrapJTextPane extends JTextPane {
		private static final long serialVersionUID = -405583649748539560L;

		public boolean getScrollableTracksViewportWidth() {
			return false;
		}

		public void setSize(Dimension d) {
			int parentWidth = this.getParent().getWidth();
			if (parentWidth > d.width) {
				d.width = parentWidth;
			}
			super.setSize(d);
		}
	}

}
