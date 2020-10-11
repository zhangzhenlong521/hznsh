/**************************************************************************
 * $RCSfile: SplashWindow.java,v $  $Revision: 1.51 $  $Date: 2013/02/28 06:14:41 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;

import com.sun.awt.AWTUtilities;

/**
 * ����һ���̣߳�����ʾ�����û��Եȵ�Splash
 *
 * @author Administrator
 *
 */
public class SplashWindow extends BillDialog implements ActionListener {
	private static final long serialVersionUID = 6351024182198896342L;

	private Container parent = null;
	private AbstractAction action = null;

	private JTextArea textarea_msg = new JTextArea(); //
	private String str_info = ""; //
	private JButton btn_cancel = null;//

	private java.util.Timer labeltimer = new Timer(); //��ǰ��Thread�ܲ��ȶ�,���һ���ֿ�ס������,����ͳһŪ��Timer����!�ȶ�����,���Ҳ��Ῠס!!
	private java.util.Timer actionTimer = new Timer(); // //����Timer
	private int li_count = 0; //
	private boolean sysMsgVisible = true; //����ȴ���ʱ��������Ƿ���ʾ���״ε�½ϵͳ...������ʾ��
	public static SplashWindow window = null; //
	private Logger logger = WLTLogger.getLogger(cn.com.infostrategy.ui.common.SplashWindow.class); //

	/**
	 * @param _info:���Ҫ��ʾ����ʾ��Ϣ
	 * @param _image:����м�Ҫ��ʾ����ʾͼƬ���Ǳ��룩
	 * @param _com:���øõȴ����ĸ��sa
	 */

	public SplashWindow(Container _parent, String _str_info, AbstractAction _action, boolean _showSysMsg) {
		super(_parent, "�ȴ�", 320, 95); //����ȴ�����10�룬����һ����ʾ����ǰ�߶�Ϊ120����ʾ��Ϣû����ȫ��ʾ�������ʽ��߶ȸ�Ϊ130
		this.parent = _parent; //
		this.action = _action;
 		this.str_info = (_str_info == null ? str_info : _str_info);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		sysMsgVisible = _showSysMsg;
		initWindow();
	}

	public SplashWindow(Container _parent, AbstractAction _action) {
		this(_parent, null, _action, true);
	}

	/**
	 * @param _info:���Ҫ��ʾ����ʾ��Ϣ
	 * @param _image:����м�Ҫ��ʾ����ʾͼƬ���Ǳ��룩
	 * @param _com:���øõȴ����ĸ��sa
	 */
	public SplashWindow(Container _parent, AbstractAction _action, int _x, int _y) {
		super(_parent, "�ȴ�", 320, 95, _x, _y);
		this.parent = _parent; //
		this.action = _action;
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		initWindow();
	}

	/**
	 * @param _info:���Ҫ��ʾ����ʾ��Ϣ
	 * @param _image:����м�Ҫ��ʾ����ʾͼƬ���Ǳ��룩
	 * @param _com:���øõȴ����ĸ��sa
	 */
	public SplashWindow(Container _parent, AbstractAction _action, int _width, int _height, int _x, int _y) {
		this(_parent, _action, _width, _height, _x, _y, true);
	}

	/**
	 * [����2012-04-01]
	 * @param _parent
	 * @param _action
	 * @param _width
	 * @param _height
	 * @param _x
	 * @param _y
	 * @param _showSysMsg  �Ƿ���ʾϵͳĬ����ӣ��״ε�½ϵͳ����ʾ��
	 */
	public SplashWindow(Container _parent, AbstractAction _action, int _width, int _height, int _x, int _y, boolean _showSysMsg) {
		super(_parent, "�ȴ�", _width, _height, _x, _y);
		this.parent = _parent; //
		this.action = _action;
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		sysMsgVisible = _showSysMsg;
		initWindow();
	}

	/**
	 *
	 * @param _com
	 *            Container ���øõȴ����ĸ��sa
	 * @param _str_info
	 *            String ���Ҫ��ʾ����ʾ��Ϣ
	 * @param _action
	 *            AbstractAction
	 */
	public SplashWindow(Container _parent, String _str_info, AbstractAction _action) {
		super(_parent, "�ȴ�", 320, 95);
		this.str_info = _str_info; //
		this.parent = _parent; //
		this.action = _action;
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //
		initWindow();
	}

	private void initWindow() {
		JPanel contPanel = new WLTPanel(); //
		contPanel.setUI(new WLTPanelUI(){
			BackGroundDrawingUtil drawutil = new BackGroundDrawingUtil();
//			Color c1 = new Color(163,218,104);
//			Color c2 = new Color(215,237,195);
			Color c1 = new Color(164,166,170);
			Color c2 = new Color(230,231,232);
			
//			Color c3 = new Color(163,218,104);
			LinearGradientPaint p;
			public void paint(Graphics g, JComponent c) {
				Graphics2D g2d = (Graphics2D) g;
				 p = new LinearGradientPaint(0,0,0,c.getHeight(),new float[]{0.0f,0.2f,0.4f,1f},new Color[]{c1,c2,c1,c2});
				 g2d.setPaint(p);
				 g2d.fill(c.getBounds());
			}
		});
		Boolean isNewProgressGif = true; //�Ƿ�����ͼƬ,����������Ф�ͷ�Ҫ�ǽ�����!
		javax.swing.ImageIcon icon = UIUtil.getImage("process.gif");
		if (icon == null) {
			icon = UIUtil.getImage("clock.gif");
			isNewProgressGif = false;
			System.err.println("������weblight_images.jar�ļ�,���б���Ҫ��process.gif!"); //
		}
		JLabel label_image = new JLabel(icon){
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				
			}
		}; //
//		label_image.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0)); //
		//����ͼƬ�����²���, �����ǽ�����, �м�������, �����ǰ�ť
		if (isNewProgressGif) {
			contPanel.add(label_image, BorderLayout.NORTH);
		} else {
			contPanel.add(label_image, BorderLayout.WEST);
		}

		//�ı���
		textarea_msg.setEnabled(false); //
		textarea_msg.setFocusable(false); //
		textarea_msg.setDisabledTextColor(Color.BLACK); //
		textarea_msg.setOpaque(false); //͸��??
		textarea_msg.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); //
		textarea_msg.setFont(new Font("����", Font.PLAIN, 12)); //
		textarea_msg.setText(str_info); //

		contPanel.add(textarea_msg, BorderLayout.CENTER);
		contPanel.add(getSouthPanel(), BorderLayout.SOUTH);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(contPanel, BorderLayout.CENTER);
		window = this; ////
		labeltimer.schedule(new TimerTask() {
			@Override
			public void run() {
				refreshLabel(); //ˢ������
			}
		}, 1000, 1000); //һ����һ��!!

		actionTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				doAction(); //ִ�ж���!
			}
		}, 0); //
		this.setUndecorated(true);
		AWTUtilities.setWindowOpaque(this, false);
		AWTUtilities.setWindowOpacity(this, 0.95f);
		AWTUtilities.setWindowShape(this, new RoundRectangle2D.Float(0,0,this.getWidth()-2,this.getHeight()-5, 20, 20));
//		this.locationToCenterPosition();
		this.setVisible(true);
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); //
		panel.setOpaque(false); //͸��!!
		btn_cancel = new WLTButton("ȡ��"); //
		btn_cancel.setBackground(Color.DARK_GRAY);
		btn_cancel.setFocusable(false); //
		btn_cancel.setFont(new Font("����", Font.PLAIN, 12)); //
		btn_cancel.setPreferredSize(new Dimension(70, 20)); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_cancel) {
			forceCancelAction(); //
		}
	}

	/**
	 * ǿ��ȡ��
	 */
	private void forceCancelAction() {
		JLabel confirmlabel = new JLabel("���Ƿ������ȡ����ǰ����?"); //
		confirmlabel.setFont(new Font("����", Font.PLAIN, 12)); //
		if (JOptionPane.showConfirmDialog(this, confirmlabel, "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		//killServerThread(); //��ɱ�������˵��߳�,��������������˶����Ƕ��Ļ�,��ɱ�ͻ����̻߳���ûЧ��..
		labeltimer.cancel(); //
		actionTimer.cancel(); //
		this.dispose(); //
	}

	/**
	 * ˢ�����������!
	 */
	private void refreshLabel() {
		li_count++; //
		String str_sysprop_waitinfo = System.getProperty("SYSPROPWAITINFO"); //ϵͳ�����ж���ĵȴ���Ϣ,������Ի���ͬ�����ش���ʱ����!
		String str_realtext = null; //
		if (str_sysprop_waitinfo != null && !str_sysprop_waitinfo.equals("")) { //�����,����ʾ
			str_realtext = str_sysprop_waitinfo;
		} else {
			str_realtext = str_info;
		}
		if (sysMsgVisible && li_count >= 10 && (li_count % 2) == 0) { //�������10��,���һ������!
			str_realtext = str_realtext + "\r\n" + "��ʾ:�״η���,�������������,���ػ��ʱһ��!"; //��ʱ�ٶȽ���,��ʵ����Ϊ������״η���,���Լ�����һ�����Ѹо���һЩ!!
		}
		textarea_msg.setText(str_realtext); //
		SplashWindow.this.setTitle(li_count + "��"); //
	}

	/**
	 * ִ�ж���!!!
	 */
	private void doAction() {
		try {
			action.actionPerformed(new ActionEvent(SplashWindow.this, 0, ""));
			closeWindow(); //ִ�н�����رմ���
			if (DeskTopPanel.deskTopPanel != null) {
				DeskTopPanel.deskTopPanel.updateUI(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			closeWindow(); //�رմ���
		}
	}

	public void closeWindow() {
		labeltimer.cancel(); //
		actionTimer.cancel(); //
		this.dispose(); //
		SplashWindow.window = null; //
	}

	public static void closeSplashWindow() {
		if (SplashWindow.window != null) {
			SplashWindow.window.closeWindow(); //
		}
	}

	/**
	 * �����µĵȴ���Ϣ..
	 * @param _newText
	 */
	public void setWaitInfo(String _newText) {
		textarea_msg.setText(_newText); //
		str_info = _newText;
	}

	public void setSysMsgVisible(boolean _isVisible) {
		sysMsgVisible = _isVisible;
	}

	/**
	 * ɱ���������˵��߳�,���������һϵ�и��ӵ�Զ�̷����߼��������з������˵��߼������е���һ���ˣ�
	 * ��ʱ��ɱ�ͻ����߳��𲻵�Ч������Ȼ����ְ�������..������ɱ�����������߳�
	 */
	private void killServerThread() {
		try {
			CurrSessionVO sessionVO = ClientEnvironment.getCurrSessionVO();
			if (sessionVO != null && sessionVO.getHttpsessionid() != null) {
				FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
				int li_count = service.killServerThreadBySessionId(sessionVO.getHttpsessionid()); //��ɱ�������˵�
				logger.debug("�ɹ�ɱ��[" + li_count + "]���������˵�ǰSessionID[" + sessionVO.getHttpsessionid() + "]���õķ����߳�"); //
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	protected void finalize() throws Throwable {
		super.finalize(); //
		logger.debug("JVM GC�������ɹ�������SplashWindow��" + this.getClass().getName() + "��(" + this.hashCode() + ")����Դ..."); //
	}

}
