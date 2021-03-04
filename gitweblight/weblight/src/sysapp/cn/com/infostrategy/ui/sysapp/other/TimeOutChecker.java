package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;

/**
 * 超时检查
 * @author sfj
 *
 */
public class TimeOutChecker extends Thread {

	private static MessageDialog message = null;
	private static Reftitle reftitle = null;
	private Container parent = null;
	private int li_count = 0;
	private int shenyutime = 0;
	private int timeouttime = 0;
	private int count = 0;
	private int dialogtime = 20; //提示窗口等待时间

	public TimeOutChecker(Container parent, int _timeouttime) {
		this.parent = parent;
		this.shenyutime = _timeouttime;
		this.timeouttime = _timeouttime;
		ClientEnvironment.getInstance().put("timeoutlefttime", shenyutime);
		ClientEnvironment.getInstance().put("timeouttime", timeouttime);
	}

	public void realrun() {
		while (true) {
			if (message == null) {
				shenyutime = (Integer) ClientEnvironment.getInstance().get("timeoutlefttime");
				if (shenyutime > 0) {
					shenyutime = shenyutime - 1;
					ClientEnvironment.getInstance().put("timeoutlefttime", shenyutime);
				} else {
					message = new MessageDialog(parent, "系统超时提醒", 340, 100);
					reftitle = new Reftitle();
					reftitle.start();
					count = 0;
				}
			}
			try {
				Thread.currentThread().sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		try {
			realrun();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	class Reftitle extends Thread {
		private Timer timer = null; //

		public void run() {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					li_count++;
					if (li_count <= dialogtime) {
						message.setTitle("等待[" + li_count + "]秒"); //  
					} else {
						System.exit(0);
					}
				}
			}, 1000, 1000);
		}

		public Timer getTimer() {
			return timer;
		}

		public void setTimer(Timer timer) {
			this.timer = timer;
		}
	}

	class MessageDialog extends BillFrame implements ActionListener {
		protected WLTButton btn_confirm, btn_cancel;
		private java.awt.Container parentContainer = null; //
		private WLTLabel label_msg = null;

		public MessageDialog(Container _parent, String _title, int _width, int _height) {
			super(_parent, _title, _width, _height); //
			this.parentContainer = _parent; //
			this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); //
			this.setSize(340, 100); //
			this.setResizable(false);
			this.locationToCenterPosition(); //
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(new JLabel(UIUtil.getImage("clock.gif")), BorderLayout.WEST);
			label_msg = new WLTLabel();
			label_msg.setFont(LookAndFeel.font); ////
			label_msg.setText("  已达到系统空闲时间设置,系统将于" + dialogtime + "秒后关闭!"); //
			this.getContentPane().add(label_msg, BorderLayout.CENTER);
			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
			this.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					reftitle.getTimer().cancel();
					reftitle.yield();
					message = null;
					li_count = 0;
					System.gc(); //
				}
			});
			this.addWindowFocusListener(new WindowFocusListener() {
				public void windowGainedFocus(WindowEvent e) {

				}

				public void windowLostFocus(WindowEvent e) {
					toFront_();
				}
			});
			this.setVisible(true);
		}

		private void toFront_() {
			this.requestFocus();
			this.setState(JFrame.NORMAL);
			this.toFront();
		}

		private JPanel getSouthPanel() {
			JPanel panel = new JPanel(new FlowLayout());
			btn_confirm = new WLTButton("确定");
			btn_cancel = new WLTButton("取消");
			btn_confirm.addActionListener(this); //
			btn_cancel.addActionListener(this); //
			//			panel.add(btn_confirm); //
			panel.add(btn_cancel); //
			return panel;
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_confirm) {
				System.exit(0);
			} else if (e.getSource() == btn_cancel) {
				onconfirm();
			}
		}

		public void onconfirm() {
			ClientEnvironment.getInstance().put("timeoutlefttime", timeouttime);
			this.dispose();
			reftitle.getTimer().cancel();
			reftitle.yield();
			message = null;
			li_count = 0;
		}
	}
}
