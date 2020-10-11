/**************************************************************************
 * $RCSfile: LoginPanel.java,v $  $Revision: 1.32 $  $Date: 2013/02/17 07:15:52 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.sysapp.login.click2.ResetPwdDialog;

public class LoginPanel extends JPanel implements ActionListener, ItemListener {

	private static final long serialVersionUID = -4300687713407623152L;

	private LoginAppletLoader loader = null; //

	private String str_userCode = null; //
	private String str_errMsg = null; //
	private JLabel label_bgimage = null;

	private JLabel label_errMsg = null; // ר��������ʾ������Ϣ��label��ֻ���ڴ������������,��Ϊ�����ٵ���һ���򽫻���ԭ���Ŀ�����һ�������ʾ������������!!
	private JTextField textField_user = null;
	private JPasswordField textField_pwd = null; //
	private JPasswordField textField_adminpwd = null; //
	private JTextField textField_checkcode = null;
	private JComboBox comboBox_language = null; // ��¼����..
	private JComboBox comboBox_innersystems = null; // 

	private JButton btn_login;
	//private JButton btn_reset;

	private KeyAdapter adapter = null; //
	private int li_width_prefix = 0;
	private int li_height_prefix = 0;

	private TBUtil tbUtil = new TBUtil(); //
	private int count = 30;
	private JLabel label_link = null;

	public LoginPanel(LoginAppletLoader _loader, String _userCode, String _errMsg) {
		this.loader = _loader; //
		this.str_userCode = _userCode; // �û�����..
		this.str_errMsg = _errMsg; // ������Ϣ,��������������ʱ������˺󣬻��ǻص���¼��Ϣ���������һ��������Ϣ..
		initPanel(); //��ʼ������!!
	}

	private void initPanel() {
		//������������������һ����Ҫ,���Ƽ�����Ϊ��Ȼ��OA�����¼��,�����ǿ�йر�Ĭ�ϵ�¼��ʽ!����Ҫ��һ��������xch/2012-05-30��
		if ("Y".equalsIgnoreCase(System.getProperty("isCloseDefaultLogin")) && str_errMsg == null) { //���ָ���˲���!�Ҵ�����ϢΪ��(���������ת���ܱ���,�����������,�򲻴���)!!
			this.setLayout(new FlowLayout()); //
			this.setBackground(Color.WHITE); //
			JLabel label = new JLabel(System.getProperty("CloseDefaultLoginWarn", "��ӹ�˾ͳһ��ڷ���ϵͳ!")); //
			label.setFont(LookAndFeel.font); //
			label.setForeground(Color.RED); //
			this.add(label); //
			return; //ֱ�ӷ���
		}

		adapter = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				dealKeyPressed(e);
			}

			public void keyTyped(KeyEvent e) {
				dealKeyTyped(e);
			}
		};

		//�����������,ͨ������ʵ��ͼƬ���¼�����װ����
		JPanel layerpanel = new JPanel(); //
		layerpanel.setLayout(null); //�������겼��
		layerpanel.setBackground(Color.WHITE); //

		//�ȴ����ͼͼƬ!

		int li_zorder = 0; //
		int li_comp_start_x = 700; //X�������ʼλ��!!�ؼ���!,Ĭ����700
		int li_comp_start_y = 279; //Y�������ʼλ��!!Ĭ����279,
		try {
			String str_comp_startxy = tbUtil.getSysOptionStringValue("applet��¼����ؼ�����ʼλ��", null); //�����ǿͻ��˵�¼ʱ�Ĳ������ã���LoginJSPUtil��Ҳ�и����Ƶ����á�HTML��¼����ؼ�����ʼλ�á������������¼ʱ�Ĳ������ã����������/2012-07-03��
			if (str_comp_startxy != null && !str_comp_startxy.trim().equals("")) {
				li_comp_start_x = Integer.parseInt(str_comp_startxy.substring(0, str_comp_startxy.indexOf(","))); //
				li_comp_start_y = Integer.parseInt(str_comp_startxy.substring(str_comp_startxy.indexOf(",") + 1, str_comp_startxy.length())); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		int li_x = li_width_prefix + li_comp_start_x; // X�������ʼλ��!!�ؼ���!,Ĭ����700
		int li_y = li_height_prefix + li_comp_start_y; // Y�������ʼλ��!!Ĭ����279,
		int li_textfield_width = 155; //Ĭ����125,������Щ
		String str_proshort = loader.getApplet().getParameter("PROJECT_SHORTNAME"); //
		String str_isys = loader.getApplet().getParameter("ISYS"); //
		int li_imgwidth = 1003; //ͼƬ�Ŀ��
		int li_imgheight = 603; //ͼƬ�ĸ߶�
		if (str_proshort == null) {
			label_bgimage = new JLabel("��������,��ΪAppletû�ж������PROJECT_SHORTNAME,�ʲ�����ʾͼƬ", JLabel.CENTER); //
			label_bgimage.setFont(new Font("System", Font.PLAIN, 12)); //
			label_bgimage.setForeground(java.awt.Color.BLUE); //
		} else {
			String str_login_bggif = "login_default"; //
			if (ClientEnvironment.getInnerSys() != null && ClientEnvironment.getInnerSys().length > 0) { // //�������ϵͳ..
				if (str_isys != null) {
					str_login_bggif = "login_" + str_isys; //
				} else {
					str_login_bggif = "login_" + ClientEnvironment.getInnerSys()[0][0]; //
				}
			} else {
				if (str_proshort != null) {
					str_login_bggif = "login_" + str_proshort; //
				}
			}
			String str_realimgName = str_login_bggif + ".jpg"; //
			ImageIcon imgIcon = UIUtil.getImageFromServer("/applet/" + str_realimgName); //��ȡJpg
			if (imgIcon == null || imgIcon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
				imgIcon = UIUtil.getImageFromServer("/applet/" + str_login_bggif + ".gif"); //����gif
				if (imgIcon == null || imgIcon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
					label_bgimage = new JLabel("û�з���ͼƬ[" + "/applet/" + str_login_bggif + "]"); //
					str_realimgName = "û�ҵ�ͼƬ!"; //
				} else {
					str_realimgName = str_login_bggif + ".gif"; //
				}
			}
			label_bgimage = new JLabel(imgIcon); //
			label_bgimage.setToolTipText(str_realimgName); //
			li_imgwidth = imgIcon.getIconWidth(); //����ͼƬ���!
			li_imgheight = imgIcon.getIconHeight(); //����ͼƬ�߶�!
		}
		label_bgimage.setBounds(0, 0, li_imgwidth, li_imgheight); // ͼƬ�Ĵ�С�ǹ̶���,��1003/603

		Frame rootFrame = JOptionPane.getFrameForComponent(this); //
		rootFrame.setSize(li_imgwidth, li_imgheight); //���ô�СΪͼƬ��С!!

		// ѭ����������ȵ�..
		String[][] str_hrefs = getLoginHrefs(); //
		if (str_hrefs != null) {
			for (int i = 0; i < str_hrefs.length; i++) {
				int li_href_x = Integer.parseInt(str_hrefs[i][0]);
				int li_href_y = Integer.parseInt(str_hrefs[i][1]);
				int li_href_width = Integer.parseInt(str_hrefs[i][2]);
				int li_href_height = Integer.parseInt(str_hrefs[i][3]);
				String str_alt = str_hrefs[i][4]; //
				String str_url = str_hrefs[i][5]; //
				JLabel label_href = new HrefLabel(str_alt, str_url); //
				label_href.setBounds(li_href_x, li_href_y, li_href_width, li_href_height); //
				layerpanel.add(label_href, li_zorder++); // ..
			}
		}

		// �ϲ��������ؼ�...
		label_errMsg = new JLabel(); //
		label_errMsg.setForeground(Color.RED); //
		if (str_errMsg != null) {
			label_errMsg.setText(str_errMsg); //
			label_errMsg.setToolTipText(str_errMsg); //��ʾҲ��,��Ϊ�ÿ�Ȳ���
		}

		Border compBorder = BorderFactory.createLineBorder(Color.GRAY, 1); //
		JLabel label_1 = new JLabel("�û�����", SwingConstants.RIGHT);
		textField_user = new JTextField(); //
		if (str_userCode != null && !str_userCode.trim().equals("")) {
			textField_user.setText(str_userCode); //
			textField_user.setCaretPosition(str_userCode.length() - 1); //
		} else {
			if (tbUtil.getSysOptionBooleanValue("�Ƿ��¼�ϴε�¼�ʺ�", true)) { //Ĭ���Ǽǵ�,��Ϊ���ͻ������û����Զ�����,ÿ��ֻ�������������!
				String str_lastloginuser = System.getProperty("LastLoginUserCode"); //�����!
				if (str_lastloginuser != null) { //
					textField_user.setText(str_lastloginuser); //
				}
			}
		}
		textField_user.setBorder(compBorder); //
		textField_user.addKeyListener(adapter);

		JLabel label_2 = new JLabel("���룺", SwingConstants.RIGHT);
		textField_pwd = new JPasswordField(); //
		textField_pwd.setBorder(compBorder); //
		textField_pwd.addKeyListener(adapter);

		//Ĭ�������û���������!!�����пͻ�����ȫ,������ʱȥ��,���Ժ󻹻�����!!!�ֻ�ʹ�ò�������!!!
		//		String[] str_default_userpwd = getDefaultUserAndPwd(); //
		//		if (str_default_userpwd != null) {
		//			textField_user.setText(str_default_userpwd[0]); //
		//			textField_pwd.setText(str_default_userpwd[1]); //
		//		}

		JLabel label_3 = new JLabel("�������룺", SwingConstants.RIGHT);
		textField_adminpwd = new JPasswordField(""); //
		textField_adminpwd.setBorder(compBorder); //
		textField_adminpwd.addKeyListener(adapter);
		JLabel label_5 = new JLabel("��֤�룺", SwingConstants.RIGHT);
		textField_checkcode = new JTextField(); //
		textField_checkcode.setBorder(compBorder); //
		textField_checkcode.addKeyListener(adapter);
		JLabel label_4 = new JLabel("���ԣ�", SwingConstants.RIGHT);
		comboBox_language = new JComboBox(); //
		comboBox_language.addItem("��������"); //
		comboBox_language.addItem("English"); //
		comboBox_language.addItem("��������"); //
		comboBox_language.setSelectedIndex(0); //
		JTextField cmb_textField_language = ((JTextField) ((JComponent) comboBox_language.getEditor().getEditorComponent())); //
		cmb_textField_language.setFocusable(false); //
		cmb_textField_language.setBorder(compBorder); //

		// �Ƿ�͸��
		btn_login = null;
		boolean isUnOpaque = tbUtil.getSysOptionBooleanValue("��¼�����ύ��ť�Ƿ�͸��", false); // 
		if (isUnOpaque) {
			btn_login = new cn.com.infostrategy.ui.common.WLTButton(""); //
			btn_login.setToolTipText("��¼"); //
			btn_login.setOpaque(false); //
			btn_login.setBorder(BorderFactory.createEmptyBorder()); //
		} else {
			btn_login = new JButton("��¼"); //
			btn_login.setToolTipText("��¼"); //
			btn_login.setMargin(new Insets(0, 0, 0, 0)); //
		}
		btn_login.addActionListener(this);
		btn_login.addKeyListener(adapter);

		//btn_reset = new JButton("���");
		//btn_reset.setMargin(new Insets(0, 0, 0, 0)); //
		//btn_reset.addActionListener(this);
		//btn_reset.addKeyListener(adapter);

		label_errMsg.setBounds(li_x + 70, li_y, 175, 20); //
		li_y = li_y + 25; //
		layerpanel.add(label_errMsg, li_zorder++); // ..

		String str_logintype = System.getProperty("REQ_logintype"); //
		if (str_errMsg != null && str_logintype != null && (str_logintype.equalsIgnoreCase("single") || str_logintype.equalsIgnoreCase("skip") || str_logintype.equalsIgnoreCase("skip2")) && !tbUtil.getSysOptionBooleanValue("�����¼ʧ��ʱ�Ƿ�����ֹ���¼", false)) { //����ǵ����¼!
			//����д���,�����ǵ����¼ģʽ,���ı���ɶ�Ķ������!!!
		} else {
			label_1.setBounds(li_x, li_y, 95, 20); // �û����ı�...
			textField_user.setBounds(li_x + 100, li_y, li_textfield_width, 20); //�û��������
			li_y = li_y + 25;
			layerpanel.add(label_1, li_zorder++); // ..
			layerpanel.add(textField_user, li_zorder++); // ..

			// �û�����
			label_2.setBounds(li_x, li_y, 95, 20);
			textField_pwd.setBounds(li_x + 100, li_y, li_textfield_width, 20);
			li_y = li_y + 25;
			layerpanel.add(label_2, li_zorder++); // ..
			layerpanel.add(textField_pwd, li_zorder++); // ..

			// ��������
			if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) { // ����ǹ������!!
				label_3.setBounds(li_x, li_y, 95, 20); //
				textField_adminpwd.setBounds(li_x + 100, li_y, li_textfield_width, 20); //
				li_y = li_y + 25;
				layerpanel.add(label_3, li_zorder++); // //
				layerpanel.add(textField_adminpwd, li_zorder++); //
			} else {
				boolean ischeckcode = tbUtil.getSysOptionBooleanValue("��¼�����Ƿ�����֤��", false);
				if (ischeckcode) {
					label_5.setBounds(li_x, li_y, 95, 20); //
					textField_checkcode.setBounds(li_x + 100, li_y, li_textfield_width, 20); //
					li_y = li_y + 20;
					layerpanel.add(label_5, li_zorder++); // //
					layerpanel.add(textField_checkcode, li_zorder++); //
					label_link = new JLabel("<html><font  style=\"line-height:22px;color:#" + tbUtil.convertColor(LookAndFeel.htmlrefcolor) + "\"><u>��ȡ��֤��</u></font></html>", SwingConstants.LEFT);
					label_link.setToolTipText("�����ȡ");
					label_link.setBounds(li_x + 100, li_y, 195, 20);
					label_link.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent e) {
							getCheckCode();
						}

						public void mouseEntered(MouseEvent e) {
							label_link.setCursor(new Cursor(Cursor.HAND_CURSOR));
						}
					});
					li_y = li_y + 25;
					layerpanel.add(label_link, li_zorder++);
				}
			}

			// ������..
			if ("YES".equalsIgnoreCase(System.getProperty("MULTILANGUAGE")) || "Y".equalsIgnoreCase(System.getProperty("MULTILANGUAGE"))) { //
				label_4.setBounds(li_x, li_y, 95, 20); //
				comboBox_language.setBounds(li_x + 100, li_y, li_textfield_width, 20); //
				li_y = li_y + 25;
				layerpanel.add(label_4, li_zorder++); // ..
				layerpanel.add(comboBox_language, li_zorder++); // ..
			}

			// ��ϵͳ..
			if (ClientEnvironment.getInnerSys() != null && ClientEnvironment.getInnerSys().length > 0) {
				JLabel label_isys = new JLabel("ϵͳ��", SwingConstants.RIGHT);
				comboBox_innersystems = new JComboBox(); //
				for (int i = 0; i < ClientEnvironment.getInnerSys().length; i++) {
					HashVO hvo = new HashVO(); //
					hvo.setAttributeValue("id", ClientEnvironment.getInnerSys()[i][0]); //
					hvo.setAttributeValue("name", ClientEnvironment.getInnerSys()[i][1]); //
					hvo.setToStringFieldName("name"); //
					comboBox_innersystems.addItem(hvo); //
					if (str_isys != null && str_isys.equals(ClientEnvironment.getInnerSys()[i][0])) {
						comboBox_innersystems.setSelectedIndex(i); //
					}
				}
				comboBox_innersystems.addItemListener(this); //
				label_isys.setBounds(li_x, li_y, 95, 20); //
				comboBox_innersystems.setBounds(li_x + 100, li_y, li_textfield_width + 20, 20); //
				li_y = li_y + 25;
				layerpanel.add(label_isys, li_zorder++); // ..
				layerpanel.add(comboBox_innersystems, li_zorder++); // ..
			}

			btn_login.setBounds(li_x + 145, li_y + 5, 60, 20); //

			//btn_reset.setBounds(li_x + 100 + 63, li_y+5, 60, 20); //
			layerpanel.add(btn_login, li_zorder++); // ..
			//layerpanel.add(btn_reset, li_zorder++); // ..
		}
		layerpanel.add(label_bgimage, li_zorder++); //�����������������»�,����ֻ���������
		this.setBackground(Color.WHITE); //
		boolean isLoginImgCenter = false; //�Ƿ��¼ͼƬ��??����Ǿ���,�����!!
		if ("Y".equalsIgnoreCase(System.getProperty("ISLOGINIMGCENTER"))) { //�Ƿ��¼ͼƬ����??
			isLoginImgCenter = true; //
		}
		if (isLoginImgCenter) { //�������,��ǿ������ͼƬ��С��1003*603,Ȼ����Χ�ǰ�ɫ��!
			layerpanel.setPreferredSize(new Dimension(1003, 603)); //
			this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 70)); //
			this.add(layerpanel); //
		} else { //����Ǿ���,����ǰ�ķ�ʽ!!
			this.setLayout(new BorderLayout()); //
			this.add(layerpanel, BorderLayout.CENTER); //	
		}
	}

	private void getCheckCode() {
		if (textField_user.getText() == null || textField_user.getText().trim().equals("") || textField_pwd.getText() == null || textField_pwd.getText().trim().equals("")) {
			MessageBox.show(this, "�û��������벻��Ϊ��!");
			return;
		}
		count = tbUtil.getSysOptionIntegerValue("������֤���ٴλ�ȡʱ��", 30);
		try {
			URL url = new URL(System.getProperty("CALLURL") + "/WebCallServlet?StrParCallClassName=" + tbUtil.getSysOptionStringValue("������֤�����߼�", "cn.com.psbc.bs.message.CheckCodeService") + "&usercode=" + textField_user.getText() + "&pwd=" + textField_pwd.getText());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if ("https".equals(System.getProperty("transpro"))) {
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, new TrustManager[] { new X509TrustManager() {
					public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

					}

					public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

					}

					public X509Certificate[] getAcceptedIssuers() {
						return new X509Certificate[] {};
					}
				} }, new java.security.SecureRandom());
				((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
				((HttpsURLConnection) conn).setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}
				});
			}
			conn.setDoInput(true);
			conn.setConnectTimeout(6000);//��������Ϊ��0��Ҳ�������ã��ʸĻ�ԭ���롾���/2018-06-25��
			conn.setReadTimeout(6000);
			conn.setRequestProperty("Content-type", "application/x-compress");
			InputStream response_in = conn.getInputStream();
			String rtn = new String(getByteFromInputStream(response_in));
			if ("1".equals(rtn)) {
				MessageBox.show(this, "�����û��Ѵ�����!");
				return;
			} else if ("2".equals(rtn)) {
				MessageBox.show(this, "�û�������!");
				return;
			} else if ("3".equals(rtn)) {
				MessageBox.show(this, "ϵͳ�����û��ظ�!");
				return;
			} else if ("4".equals(rtn)) {
				MessageBox.show(this, "�û�Ȩ����δ��ͨ!");
				return;
			} else if ("5".equals(rtn)) {
				MessageBox.show(this, "�û��ѷ�ͣ!");
				return;
			} else if ("6".equals(rtn)) {
				MessageBox.show(this, "���ͳɹ�!");
			} else if ("7".equals(rtn)) {
				MessageBox.show(this, "����ʧ��!");
				return;
			} else if ("9".equals(rtn)) {
				MessageBox.show(this, "�ֻ���Ϊ��!");
				return;
			} else if ("10".equals(rtn)) {
				MessageBox.show(this, "��֤���ȡʱ����Ϊ" + count + "��!");
				return;
			} else if ("11".equals(rtn)) {
				MessageBox.show(this, "�û��������!");
				return;
			} else {
				MessageBox.show(this, "�����������Ժ�����!");
				return;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			MessageBox.show(this, "�����������Ժ�����!");
			return;
		}

		final MouseListener[] mls = label_link.getMouseListeners();
		if (mls != null && mls.length > 0) {
			for (int i = 0; i < mls.length; i++) {
				label_link.removeMouseListener(mls[i]);
			}
		}
		final Timer labeltimer = new Timer();
		labeltimer.schedule(new TimerTask() {
			public void run() {
				while (true) {
					count--;
					if (count <= 0) {
						label_link.setText("<html><font  style=\"line-height:22px;color:#" + tbUtil.convertColor(LookAndFeel.htmlrefcolor) + "\"><u>��ȡ��֤��</u></font></html>");
						count = 30;
						if (mls != null && mls.length > 0) {
							for (int i = 0; i < mls.length; i++) {
								label_link.addMouseListener(mls[i]);
							}
						}
						this.cancel();
						labeltimer.cancel();
						break;
					}
					label_link.setText("<html><font  style=\"line-height:22px;color:#" + tbUtil.convertColor(LookAndFeel.htmlrefcolor) + "\"><u>" + count + "�����ٴλ�ȡ!</u></font></html>");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, 0);
	}

	private byte[] getByteFromInputStream(InputStream _inputstream) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buf = new byte[2048];
			int len = -1;
			while ((len = _inputstream.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
			byte[] byteCodes = baos.toByteArray();
			return byteCodes; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		} finally {
			try {
				_inputstream.close(); //
				baos.close(); //
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
		}
	}

	/**
	 * ȡ�õ�¼ҳ��������ȵ�!
	 * 
	 * @return
	 */
	private String[][] getLoginHrefs() {
		try {
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			return service.getLoginHrefs(); //
		} catch (Exception ex) {
			ex.printStackTrace();//
			return null;//
		}
	}

	protected void dealKeyPressed(KeyEvent e) {
		Object obj = e.getSource();
		if (obj.equals(textField_user)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				getFocus(2);
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				onClean();
			}
		} else if (obj.equals(textField_pwd)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				onLogin();
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				onClean();
			}
		} else if (obj.equals(textField_adminpwd)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				onLogin();
			}
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				onClean();
			}
		} else if (obj.equals(btn_login)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				onLogin();
			}
		} else if (obj.equals(textField_checkcode)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				onLogin();
			}
		}
		//		else if (obj.equals(btn_reset)) {
		//			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		//				getFocus(1);
		//				onReset();
		//			}
		//		}
	}

	protected void dealKeyTyped(KeyEvent e) {
		Object obj = e.getSource();
		if (obj.equals(textField_user)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				onLogin();
			}
		} else if (obj.equals(textField_pwd)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				onLogin();
			}
		} else if (obj.equals(textField_adminpwd)) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				onLogin();
			}
		}
	}

	private void getFocus(int _type) {
		if (_type == 1) {
			textField_user.requestFocus();
		} else if (_type == 2) {
			textField_pwd.requestFocus();
			textField_pwd.setSelectionStart(0);
			textField_pwd.setSelectionEnd(textField_pwd.getWidth());
			textField_pwd.setCaretPosition(textField_pwd.getSelectionEnd());
		} else if (_type == 3) {
			textField_adminpwd.requestFocus();
			textField_adminpwd.setSelectionStart(0);
			textField_adminpwd.setSelectionEnd(textField_adminpwd.getWidth());
			textField_adminpwd.setCaretPosition(textField_adminpwd.getSelectionEnd());
		}
	}

	private void onClean() {
		this.textField_user.setText("");
		this.textField_pwd.setText("");
		this.textField_adminpwd.setText("");
	}

	/**
	 * 
	 */
	private void onLogin() {
		String str_usercode = textField_user.getText(); //
		String str_pwd = new String(textField_pwd.getPassword()); //
		if (tbUtil.getSysOptionBooleanValue("��¼�����Ƿ�����֤��", false) && ClientEnvironment.getInstance().getLoginModel() != ClientEnvironment.LOGINMODEL_ADMIN) {
			String str_checkcode = textField_checkcode.getText();
			if (str_usercode == null || str_usercode.trim().equals("") || str_pwd == null || str_pwd.trim().equals("") || str_checkcode == null || str_checkcode.trim().equals("")) {
				MessageBox.show(this, "�û�������������֤�붼����Ϊ��!");
				return; //
			}
		} else {
			if (str_usercode == null || str_usercode.trim().equals("") || str_pwd == null || str_pwd.trim().equals("")) {
				MessageBox.show(this, "�û��������붼����Ϊ��!"); //
				return; //
			}
		}
		if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) { //����Աģʽ-�����������[����2012-12-05]
			String str_adminpwd = new String(textField_adminpwd.getPassword()); //
			if (str_adminpwd == null || str_adminpwd.equals("")) {
				MessageBox.show(this, "�������벻��Ϊ��!"); //
				return;
			}
		}
		if (tbUtil.getSysOptionBooleanValue("�Ƿ��¼�ϴε�¼�ʺ�", true)) { //�����д�ϴε�¼�ʺ�
			UIUtil.writeBackClientPropFile("LastLoginUserCode", str_usercode); //
		}

		this.label_errMsg.setText(""); //
		String str_languString = ""; //
		if (comboBox_language.getSelectedIndex() == 0) { //
			str_languString = WLTConstants.SIMPLECHINESE; //
		} else if (comboBox_language.getSelectedIndex() == 1) { //
			str_languString = WLTConstants.ENGLISH; //
		} else if (comboBox_language.getSelectedIndex() == 2) { //
			str_languString = WLTConstants.TRADITIONALCHINESE; //
		}

		// ����ж��ϵͳ...
		String str_sel_isys = null; // ѡ�е���ϵͳ!!
		if (comboBox_innersystems != null) {
			HashVO hvo = (HashVO) comboBox_innersystems.getSelectedItem(); //
			str_sel_isys = hvo.getStringValue("id"); //
		}

		if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) { // �����Admin��¼
			loader.dealLogin(true, str_usercode, str_pwd, new String(textField_adminpwd.getPassword()), str_sel_isys, true, false, str_languString, null); //
		} else {
			loader.dealLogin(true, str_usercode, str_pwd, null, str_sel_isys, false, false, str_languString, textField_checkcode.getText()); //
		}
		updatePwdDialog();
	}

	/**
	 * �޸����룬�������ʦ�����Ҫ��ϵͳ���ε�¼ʱ�����޸����벢����Ҫʵ��ʹ��һ��ʱ�������޸�������߼��������Ӹ÷�����
	 * 
	 * �漰��ƽ̨�����������޸����ڡ���ֻ���������֣������������С�ڻ����0��������ʾ�޸����롾���/2012-07-12��
	 * 
	 */
	public void updatePwdDialog() {
		int dayslenght = tbUtil.getSysOptionIntegerValue("�����޸�����", -1);
		if (dayslenght <= 0) {
			return;
		}
		String userid = ClientEnvironment.getCurrLoginUserVO().getId();
		if (userid == null) {//�������ʱ������Ϊ�գ�����Ҫ�ж�һ�¡����/2016-07-07��
			return;
		}
		TableDataStruct struct;
		String update_date = null; // �����޸ĵ�����
		try {
			struct = UIUtil.getTableDataStructByDS(null, "select * from pub_user where id=" + userid);
			if (struct == null || struct.getBodyData() == null || struct.getBodyData().length == 0) {
				return;
			}
			String[] headnames = struct.getHeaderName();
			int i = 0;
			boolean iffind = false;
			for (; i < headnames.length; i++) {
				if ("pwd_update_date".equalsIgnoreCase(headnames[i])) {
					iffind = true;
					break;
				}
			}

			if (!iffind) {//�����Ա����û��pwd_update_date�ֶΣ��Ͳ�������ʾ��
				return;
			}

			update_date = struct.getBodyData()[0][i];
			if (update_date == null || update_date.equals("")) {
				if (MessageBox.showConfirmDialog(JOptionPane.getFrameForComponent(this), "ϵͳ�״ε�¼�Ƿ���������޸�?") == JOptionPane.YES_OPTION) {
					BillDialog dialog = new ResetPwdDialog(JOptionPane.getFrameForComponent(this));
					dialog.setVisible(true);
				} else {
					insertpublic_update_date(userid);
				}
			} else {
				long days = 24 * 3600 * 1000; //һ��ĺ�����
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date d1 = df.parse(tbUtil.getCurrDate());
				Date d2 = (Date) df.parse(update_date);
				if ((d1.getTime() - d2.getTime()) / days > dayslenght) {
					if (MessageBox.showConfirmDialog(JOptionPane.getFrameForComponent(this), "ϵͳ��¼����ʹ��ʱ�����" + dayslenght + "��,�Ƿ��޸�?") == JOptionPane.YES_OPTION) {
						BillDialog dialog = new ResetPwdDialog(JOptionPane.getFrameForComponent(this));
						dialog.setVisible(true);
					} else {
						insertpublic_update_date(userid);
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * �����޸�����ʱ��
	 * @param date  �޸�ʱ����½ʱ��
	 */
	private void insertpublic_update_date(String _userid) {
		try {
			UIUtil.executeUpdateByDS(null, "update pub_user set pwd_update_date='" + tbUtil.getCurrDate() + "' where id=" + _userid);
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_login) {
			btn_login.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			onLogin();
			btn_login.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
		//		else if (e.getSource() == btn_reset) {
		//			onReset();
		//		}
	}

	/**
	 * ��������ֵ
	 */
	private void onReset() {
		reset(); //
	}

	private void reset() {
		textField_user.setText("");
		textField_pwd.setText("");
		textField_adminpwd.setText("");

		PrintWriter pfw = null;
		try {
			String str_filepath = System.getProperty("java.home").replace('\\', '/') + "/bin/WebPushClient.cmd"; //
			File file = new File(str_filepath); //
			if (file.exists()) { //����ļ�����,�������!��ζ�Ÿ��ļ������WebPushClient.cmd
				BufferedReader read = new BufferedReader(new FileReader(file)); //
				String str_oldurl = read.readLine();
				try {
					read.close(); //�����ر�,��Ϊ����������ϻ�д..
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
				if (System.getProperty("WLTUSERPWD") != null && !System.getProperty("WLTUSERPWD").trim().equals("")) { //������в���!!��ɾ���ò���!!!
					int li_pos = str_oldurl.indexOf("-DWLTUSERPWD="); //�Ҳ���λ��13λ
					String str_prefix = str_oldurl.substring(0, li_pos); //
					String str_subfix = str_oldurl.substring(li_pos + 13, str_oldurl.length()); ////
					int li_pos_2 = str_subfix.indexOf(" "); ////
					str_subfix = str_subfix.substring(li_pos_2 + 1, str_subfix.length()); ////
					String str_newcmd = str_prefix + str_subfix;
					pfw = new PrintWriter(new FileOutputStream(file, false)); //������д
					pfw.println(str_newcmd); //д�ļ�
					System.setProperty("WLTUSERPWD", ""); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (pfw != null) {
				pfw.close(); //�ر�
			}
		}

	}

	public void itemStateChanged(ItemEvent e) {
		HashVO hvo = (HashVO) e.getItem();
		String str_id = hvo.getStringValue("id"); //
		ImageIcon imgIcon = UIUtil.getImageFromServer("/applet/login_" + str_id + ".jpg"); //����jpg!
		if (imgIcon == null) {
			imgIcon = UIUtil.getImageFromServer("/applet/login_" + str_id + ".gif"); //���jpgû��,����gif
			if (imgIcon == null || imgIcon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
				label_bgimage.setIcon(null); //
				label_bgimage.setText("û���ҵ�ͼƬ[" + "/applet/login_" + str_id + ".gif" + "]");
			} else {
				label_bgimage.setIcon(imgIcon); //
				label_bgimage.setText(null); //	
			}
		} else {
			label_bgimage.setIcon(imgIcon); //
			label_bgimage.setText(null); //
		}
		label_bgimage.setToolTipText("/applet/login_" + str_id + ".gif");
		label_bgimage.repaint(); //
	}

	/**
	 * �����ӵ�Label
	 * 
	 * @author xch
	 * 
	 */
	class HrefLabel extends JLabel {
		private static final long serialVersionUID = -5890504891260909832L;
		private String str_url = null; //

		public HrefLabel(String _alt, String _url) {
			this.str_url = _url; //
			this.setToolTipText(_alt); //
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			// this.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1)); //
			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					HrefLabel.this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); //
				}

				@Override
				public void mouseExited(MouseEvent e) {
					HrefLabel.this.setBorder(BorderFactory.createEmptyBorder()); //
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					onOpenUrl(); //
				}
			}); //
		}

		private void onOpenUrl() {
			try {
				this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
				org.jdesktop.jdic.desktop.Desktop.browse(new URL(System.getProperty("URL") + str_url)); //
			} catch (Exception e) {
				e.printStackTrace(); //
			} finally {
				this.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			}
		}
	}

}
