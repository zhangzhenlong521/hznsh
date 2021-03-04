package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * 抓取服务器端屏幕的工具,非常有用,有时远程终端不能用,需要该功能.
 * 使用客户端与服务器端的Robot工具,以后可以扩展做成一个像PcAnywhere一样的工具,将是一个非常大的亮点
 * @author xch
 *
 */
public class CaptureServerScreenFrame extends JFrame implements ActionListener, MouseListener {

	private JButton btn_1 = null; //

	private JLabel label = null;
	private JScrollPane scrollPane = null; //

	public CaptureServerScreenFrame() {
		this.setTitle("抓取Server屏幕"); //
		this.setSize(800, 600); //
		this.setLocation(0, 0); //
		initialize(); //
	}
	
	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		CaptureServerScreenFrame frame = new CaptureServerScreenFrame(); //
		frame.setVisible(true);
		frame.toFront(); //
	}


	/**
	 * 初始化页面
	 */
	private void initialize() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		btn_1 = new WLTButton("抓取"); //
		btn_1.addActionListener(this); //
		panel.add(btn_1); //

		this.getContentPane().add(panel, BorderLayout.NORTH); //

		label = new JLabel(); //
		label.addMouseListener(this); //
		scrollPane = new JScrollPane(label); //
		this.getContentPane().add(scrollPane, BorderLayout.CENTER); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_1) {
			onCapture(); //
		}

	}

	/**
	 * 抓屏
	 */
	private void onCapture() {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class);
			byte[] bytes = service.captureScreen(); //
			ImageIcon icon = new ImageIcon(bytes);
			label.setIcon(icon); //
			label.updateUI(); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	public void mouseClicked(MouseEvent e) {
		int li_type = e.getButton(); //
		int li_x = e.getX(); //
		int li_y = e.getY(); //
		//System.out.println("[" + li_x + "],[" + li_y + "]"); //

		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class);
			byte[] bytes = service.mouseClick(li_type, li_x, li_y); //
			ImageIcon image = new ImageIcon(bytes);
			label.setIcon(image); //
			label.updateUI(); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
