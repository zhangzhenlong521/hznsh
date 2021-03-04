package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * SQL�������,����ʱ�鿴����������־ʵ�ڲ�����,����Ϊ�����û�����,������־̫��,����鿴.
 * ��ʱ����Ҫһ��ֻ�������λỰ��SQL
 * @author xch
 *
 */
public class SessionSQLListenerWFPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTextField textField = null; //
	private WLTButton btn_addListener = null; //
	private WLTButton btn_removeListener = null; //
	private WLTButton btn_queryListenerSQL = null; //
	private WLTButton btn_queryAndClearListenerSQL = null; //
	private WLTButton btn_clear = null; //
	private JCheckBox check_isautorefresh = null; //
	private JTextArea textArea = null; //

	private javax.swing.Timer timer = null;

	private int refreshPeriod = 2; //�Զ�ˢ�µ�Ƶ��

	@Override
	public void initialize() {
		JLabel label = new JLabel("SessionId", SwingConstants.RIGHT); //
		label.setPreferredSize(new Dimension(60, 20)); //

		textField = new JTextField(); //
		textField.setPreferredSize(new Dimension(255, 20)); //
		textField.setText(ClientEnvironment.getCurrSessionVO().getHttpsessionid()); //

		btn_addListener = new WLTButton("ע�����"); //
		btn_removeListener = new WLTButton("�Ƴ�����"); //
		btn_queryListenerSQL = new WLTButton("��ѯSQL"); //
		btn_queryAndClearListenerSQL = new WLTButton("��ѯ�����SQL"); //
		btn_clear = new WLTButton("���ҳ��"); //
		check_isautorefresh = new JCheckBox("�Ƿ��Զ�ˢ�²�����", false); //

		btn_queryListenerSQL.setToolTipText("��סShift��������������в鿴"); ///
		btn_queryAndClearListenerSQL.setToolTipText("��סShift��������������в鿴"); ///

		btn_addListener.addActionListener(this);
		btn_removeListener.addActionListener(this);
		btn_queryListenerSQL.addActionListener(this);
		btn_queryAndClearListenerSQL.addActionListener(this);
		btn_clear.addActionListener(this);
		check_isautorefresh.addActionListener(this); //

		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel_north.add(label); //
		panel_north.add(textField); //

		panel_north.add(btn_addListener); //
		panel_north.add(btn_removeListener); //
		panel_north.add(btn_queryListenerSQL); //
		panel_north.add(btn_queryAndClearListenerSQL); //
		panel_north.add(btn_clear); //
		panel_north.add(check_isautorefresh); //

		textArea = new JTextArea(); //
		textArea.setEditable(false); //
		textArea.setLineWrap(true); //�Զ�����
		JScrollPane scrollPanel = new JScrollPane(textArea); //

		this.setLayout(new BorderLayout()); //
		this.add(panel_north, BorderLayout.NORTH); //
		this.add(scrollPanel, BorderLayout.CENTER); //
		timer = new javax.swing.Timer(refreshPeriod * 1000, this); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_addListener) {
			onAddSQLListener(); //
		} else if (e.getSource() == btn_removeListener) {
			onRemoveSQLListener(); //
		} else if (e.getSource() == btn_queryListenerSQL) {
			if (e.getModifiers() == 17) {
				try {
					String str_url = System.getProperty("CALLURL") + "/state?type=sessionsql&sid=" + System.getProperty("SESSIONID") + "&isclear=N";
					Desktop.browse(new URL(str_url)); //
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
			} else {
				onQuerySQLListenerText(false); //
			}
		} else if (e.getSource() == btn_queryAndClearListenerSQL) {
			if (e.getModifiers() == 17) {
				try {
					String str_url = System.getProperty("CALLURL") + "/state?type=sessionsql&sid=" + System.getProperty("SESSIONID") + "&isclear=Y";
					Desktop.browse(new URL(str_url)); //
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
			} else {
				onQuerySQLListenerText(true);
			}
		} else if (e.getSource() == btn_clear) {
			onClear(); //
		} else if (e.getSource() == check_isautorefresh) {
			if (check_isautorefresh.isSelected()) {
				timer.start(); //
			} else {
				timer.stop();
			}
		} else if (e.getSource() == timer) {
			onRefresh(); //
		}
	}

	private void onAddSQLListener() {
		try {
			String str_sessionid = textField.getText(); //
			if (str_sessionid == null || str_sessionid.trim().equals("")) {
				MessageBox.show(this, "������SessionId"); //
				return;
			}

			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			String msg = service.addSessionSQLListener(str_sessionid); //
			MessageBox.show(this, msg); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onRemoveSQLListener() {
		try {
			String str_sessionid = textField.getText(); //
			if (str_sessionid == null || str_sessionid.trim().equals("")) {
				MessageBox.show(this, "������SessionId"); //
				return;
			}
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			String msg = service.removeSessionSQLListener(str_sessionid); //
			MessageBox.show(this, msg); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ��ѯSQL
	 */
	private void onQuerySQLListenerText(boolean _isclean) {
		try {
			String str_sessionid = textField.getText(); //
			if (str_sessionid == null || str_sessionid.trim().equals("")) {
				MessageBox.show(this, "������SessionId"); //
				return;
			}
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			String str_text = service.getSessionSQLListenerText(str_sessionid, _isclean); //
			textArea.setText(str_text); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * �Զ�ˢ��..
	 */
	protected void onRefresh() {
		WLTLogger.getLogger(this).debug("ˢ��SQL����..."); //
		try {
			String str_sessionid = textField.getText(); //
			if (str_sessionid == null || str_sessionid.trim().equals("")) {
				return;
			}
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			String str_text = service.getSessionSQLListenerText(str_sessionid, false); //
			textArea.setText(str_text); //
			//StringSelection ss = new StringSelection(str_text);
			//Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); //��Ϊ��ʱ��ģʽ����,û����ҳ��,���Ըɴ࿽����ϵͳ�ļ�������,�����ͺÿ�����!
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onClear() {
		this.textArea.setText(""); //
	}

	/**
	 * �ر�ǰ������
	 */
	public void beforeDispose() {
		timer.stop(); //
		timer = null; //
	}

}
