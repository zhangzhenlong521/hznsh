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
 * ץȡ����������Ļ�Ĺ���,�ǳ�����,��ʱԶ���ն˲�����,��Ҫ�ù���.
 * ʹ�ÿͻ�����������˵�Robot����,�Ժ������չ����һ����PcAnywhereһ���Ĺ���,����һ���ǳ��������
 * @author xch
 *
 */
public class CaptureServerScreenFrame extends JFrame implements ActionListener, MouseListener {

	private JButton btn_1 = null; //

	private JLabel label = null;
	private JScrollPane scrollPane = null; //

	public CaptureServerScreenFrame() {
		this.setTitle("ץȡServer��Ļ"); //
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
	 * ��ʼ��ҳ��
	 */
	private void initialize() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		btn_1 = new WLTButton("ץȡ"); //
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
	 * ץ��
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
