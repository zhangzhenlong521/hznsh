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
 * SQL监听面板,即有时查看服务器端日志实在不方便,或因为大量用户并发,导致日志太多,不便查看.
 * 这时就需要一个只监听本次会话的SQL
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

	private int refreshPeriod = 2; //自动刷新的频率

	@Override
	public void initialize() {
		JLabel label = new JLabel("SessionId", SwingConstants.RIGHT); //
		label.setPreferredSize(new Dimension(60, 20)); //

		textField = new JTextField(); //
		textField.setPreferredSize(new Dimension(255, 20)); //
		textField.setText(ClientEnvironment.getCurrSessionVO().getHttpsessionid()); //

		btn_addListener = new WLTButton("注册监听"); //
		btn_removeListener = new WLTButton("移除监听"); //
		btn_queryListenerSQL = new WLTButton("查询SQL"); //
		btn_queryAndClearListenerSQL = new WLTButton("查询并清空SQL"); //
		btn_clear = new WLTButton("清空页面"); //
		check_isautorefresh = new JCheckBox("是否自动刷新并拷贝", false); //

		btn_queryListenerSQL.setToolTipText("按住Shift键可以在浏览器中查看"); ///
		btn_queryAndClearListenerSQL.setToolTipText("按住Shift键可以在浏览器中查看"); ///

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
		textArea.setLineWrap(true); //自动换行
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
				MessageBox.show(this, "请输入SessionId"); //
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
				MessageBox.show(this, "请输入SessionId"); //
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
	 * 查询SQL
	 */
	private void onQuerySQLListenerText(boolean _isclean) {
		try {
			String str_sessionid = textField.getText(); //
			if (str_sessionid == null || str_sessionid.trim().equals("")) {
				MessageBox.show(this, "请输入SessionId"); //
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
	 * 自动刷新..
	 */
	protected void onRefresh() {
		WLTLogger.getLogger(this).debug("刷新SQL跟踪..."); //
		try {
			String str_sessionid = textField.getText(); //
			if (str_sessionid == null || str_sessionid.trim().equals("")) {
				return;
			}
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			String str_text = service.getSessionSQLListenerText(str_sessionid, false); //
			textArea.setText(str_text); //
			//StringSelection ss = new StringSelection(str_text);
			//Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null); //因为有时是模式窗口,没法看页面,所以干脆拷贝到系统的剪贴板中,这样就好拷贝了!
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onClear() {
		this.textArea.setText(""); //
	}

	/**
	 * 关闭前做的事
	 */
	public void beforeDispose() {
		timer.stop(); //
		timer = null; //
	}

}
