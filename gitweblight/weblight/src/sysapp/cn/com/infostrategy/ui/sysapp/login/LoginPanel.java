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

	private JLabel label_errMsg = null; // 专门用来显示错误信息的label，只用于从浏览器过来用,因为这里再弹出一个框将会与原来的框绕在一起，造成显示不出来的问题!!
	private JTextField textField_user = null;
	private JPasswordField textField_pwd = null; //
	private JPasswordField textField_adminpwd = null; //
	private JTextField textField_checkcode = null;
	private JComboBox comboBox_language = null; // 登录语言..
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
		this.str_userCode = _userCode; // 用户编码..
		this.str_errMsg = _errMsg; // 错误信息,如果浏览器跳过来时密码错了后，还是回到登录信息，但会带来一个错误信息..
		initPanel(); //初始化界面!!
	}

	private void initPanel() {
		//耿东华在中铁建遇到一个需要,即科技部认为既然从OA单点登录了,则必须强行关闭默认登录方式!所以要加一个参数【xch/2012-05-30】
		if ("Y".equalsIgnoreCase(System.getProperty("isCloseDefaultLogin")) && str_errMsg == null) { //如果指定了参数!且错误信息为空(即浏览器跳转可能报错,比如密码错误,则不处理)!!
			this.setLayout(new FlowLayout()); //
			this.setBackground(Color.WHITE); //
			JLabel label = new JLabel(System.getProperty("CloseDefaultLoginWarn", "请从公司统一入口访问系统!")); //
			label.setFont(LookAndFeel.font); //
			label.setForeground(Color.RED); //
			this.add(label); //
			return; //直接返回
		}

		adapter = new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				dealKeyPressed(e);
			}

			public void keyTyped(KeyEvent e) {
				dealKeyTyped(e);
			}
		};

		//创建内容面板,通过层来实现图片与登录框的组装布局
		JPanel layerpanel = new JPanel(); //
		layerpanel.setLayout(null); //绝对坐标布局
		layerpanel.setBackground(Color.WHITE); //

		//先处理底图图片!

		int li_zorder = 0; //
		int li_comp_start_x = 700; //X坐标的起始位置!!关键点!,默认是700
		int li_comp_start_y = 279; //Y坐标的起始位置!!默认是279,
		try {
			String str_comp_startxy = tbUtil.getSysOptionStringValue("applet登录界面控件的起始位置", null); //这里是客户端登录时的参数配置，在LoginJSPUtil中也有个类似的配置“HTML登录界面控件的起始位置”，是浏览器登录时的参数配置！！！【李春娟/2012-07-03】
			if (str_comp_startxy != null && !str_comp_startxy.trim().equals("")) {
				li_comp_start_x = Integer.parseInt(str_comp_startxy.substring(0, str_comp_startxy.indexOf(","))); //
				li_comp_start_y = Integer.parseInt(str_comp_startxy.substring(str_comp_startxy.indexOf(",") + 1, str_comp_startxy.length())); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		int li_x = li_width_prefix + li_comp_start_x; // X坐标的起始位置!!关键点!,默认是700
		int li_y = li_height_prefix + li_comp_start_y; // Y坐标的起始位置!!默认是279,
		int li_textfield_width = 155; //默认是125,中铁宽些
		String str_proshort = loader.getApplet().getParameter("PROJECT_SHORTNAME"); //
		String str_isys = loader.getApplet().getParameter("ISYS"); //
		int li_imgwidth = 1003; //图片的宽度
		int li_imgheight = 603; //图片的高度
		if (str_proshort == null) {
			label_bgimage = new JLabel("开发环境,因为Applet没有定义参数PROJECT_SHORTNAME,故不能显示图片", JLabel.CENTER); //
			label_bgimage.setFont(new Font("System", Font.PLAIN, 12)); //
			label_bgimage.setForeground(java.awt.Color.BLUE); //
		} else {
			String str_login_bggif = "login_default"; //
			if (ClientEnvironment.getInnerSys() != null && ClientEnvironment.getInnerSys().length > 0) { // //如果有子系统..
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
			ImageIcon imgIcon = UIUtil.getImageFromServer("/applet/" + str_realimgName); //先取Jpg
			if (imgIcon == null || imgIcon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
				imgIcon = UIUtil.getImageFromServer("/applet/" + str_login_bggif + ".gif"); //再找gif
				if (imgIcon == null || imgIcon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
					label_bgimage = new JLabel("没有发现图片[" + "/applet/" + str_login_bggif + "]"); //
					str_realimgName = "没找到图片!"; //
				} else {
					str_realimgName = str_login_bggif + ".gif"; //
				}
			}
			label_bgimage = new JLabel(imgIcon); //
			label_bgimage.setToolTipText(str_realimgName); //
			li_imgwidth = imgIcon.getIconWidth(); //重置图片宽度!
			li_imgheight = imgIcon.getIconHeight(); //重置图片高度!
		}
		label_bgimage.setBounds(0, 0, li_imgwidth, li_imgheight); // 图片的大小是固定的,即1003/603

		Frame rootFrame = JOptionPane.getFrameForComponent(this); //
		rootFrame.setSize(li_imgwidth, li_imgheight); //设置大小为图片大小!!

		// 循环加入各个热点..
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

		// 上层加入各个控件...
		label_errMsg = new JLabel(); //
		label_errMsg.setForeground(Color.RED); //
		if (str_errMsg != null) {
			label_errMsg.setText(str_errMsg); //
			label_errMsg.setToolTipText(str_errMsg); //提示也是,因为该宽度不够
		}

		Border compBorder = BorderFactory.createLineBorder(Color.GRAY, 1); //
		JLabel label_1 = new JLabel("用户名：", SwingConstants.RIGHT);
		textField_user = new JTextField(); //
		if (str_userCode != null && !str_userCode.trim().equals("")) {
			textField_user.setText(str_userCode); //
			textField_user.setCaretPosition(str_userCode.length() - 1); //
		} else {
			if (tbUtil.getSysOptionBooleanValue("是否记录上次登录帐号", true)) { //默认是记的,因为许多客户觉得用户名自动保存,每次只输入密码就行了!
				String str_lastloginuser = System.getProperty("LastLoginUserCode"); //如果有!
				if (str_lastloginuser != null) { //
					textField_user.setText(str_lastloginuser); //
				}
			}
		}
		textField_user.setBorder(compBorder); //
		textField_user.addKeyListener(adapter);

		JLabel label_2 = new JLabel("密码：", SwingConstants.RIGHT);
		textField_pwd = new JPasswordField(); //
		textField_pwd.setBorder(compBorder); //
		textField_pwd.addKeyListener(adapter);

		//默认设置用户名与密码!!后来有客户不安全,故先暂时去掉,但以后还会启用!!!抑或使用参数配置!!!
		//		String[] str_default_userpwd = getDefaultUserAndPwd(); //
		//		if (str_default_userpwd != null) {
		//			textField_user.setText(str_default_userpwd[0]); //
		//			textField_pwd.setText(str_default_userpwd[1]); //
		//		}

		JLabel label_3 = new JLabel("管理密码：", SwingConstants.RIGHT);
		textField_adminpwd = new JPasswordField(""); //
		textField_adminpwd.setBorder(compBorder); //
		textField_adminpwd.addKeyListener(adapter);
		JLabel label_5 = new JLabel("验证码：", SwingConstants.RIGHT);
		textField_checkcode = new JTextField(); //
		textField_checkcode.setBorder(compBorder); //
		textField_checkcode.addKeyListener(adapter);
		JLabel label_4 = new JLabel("语言：", SwingConstants.RIGHT);
		comboBox_language = new JComboBox(); //
		comboBox_language.addItem("简体中文"); //
		comboBox_language.addItem("English"); //
		comboBox_language.addItem("繁体中文"); //
		comboBox_language.setSelectedIndex(0); //
		JTextField cmb_textField_language = ((JTextField) ((JComponent) comboBox_language.getEditor().getEditorComponent())); //
		cmb_textField_language.setFocusable(false); //
		cmb_textField_language.setBorder(compBorder); //

		// 是否透明
		btn_login = null;
		boolean isUnOpaque = tbUtil.getSysOptionBooleanValue("登录界面提交按钮是否透明", false); // 
		if (isUnOpaque) {
			btn_login = new cn.com.infostrategy.ui.common.WLTButton(""); //
			btn_login.setToolTipText("登录"); //
			btn_login.setOpaque(false); //
			btn_login.setBorder(BorderFactory.createEmptyBorder()); //
		} else {
			btn_login = new JButton("登录"); //
			btn_login.setToolTipText("登录"); //
			btn_login.setMargin(new Insets(0, 0, 0, 0)); //
		}
		btn_login.addActionListener(this);
		btn_login.addKeyListener(adapter);

		//btn_reset = new JButton("清空");
		//btn_reset.setMargin(new Insets(0, 0, 0, 0)); //
		//btn_reset.addActionListener(this);
		//btn_reset.addKeyListener(adapter);

		label_errMsg.setBounds(li_x + 70, li_y, 175, 20); //
		li_y = li_y + 25; //
		layerpanel.add(label_errMsg, li_zorder++); // ..

		String str_logintype = System.getProperty("REQ_logintype"); //
		if (str_errMsg != null && str_logintype != null && (str_logintype.equalsIgnoreCase("single") || str_logintype.equalsIgnoreCase("skip") || str_logintype.equalsIgnoreCase("skip2")) && !tbUtil.getSysOptionBooleanValue("单点登录失败时是否可以手工登录", false)) { //如果是单点登录!
			//如果有错误,且又是单点登录模式,则文本框啥的都不输出!!!
		} else {
			label_1.setBounds(li_x, li_y, 95, 20); // 用户名文本...
			textField_user.setBounds(li_x + 100, li_y, li_textfield_width, 20); //用户名输入框
			li_y = li_y + 25;
			layerpanel.add(label_1, li_zorder++); // ..
			layerpanel.add(textField_user, li_zorder++); // ..

			// 用户密码
			label_2.setBounds(li_x, li_y, 95, 20);
			textField_pwd.setBounds(li_x + 100, li_y, li_textfield_width, 20);
			li_y = li_y + 25;
			layerpanel.add(label_2, li_zorder++); // ..
			layerpanel.add(textField_pwd, li_zorder++); // ..

			// 管理密码
			if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) { // 如果是管理身份!!
				label_3.setBounds(li_x, li_y, 95, 20); //
				textField_adminpwd.setBounds(li_x + 100, li_y, li_textfield_width, 20); //
				li_y = li_y + 25;
				layerpanel.add(label_3, li_zorder++); // //
				layerpanel.add(textField_adminpwd, li_zorder++); //
			} else {
				boolean ischeckcode = tbUtil.getSysOptionBooleanValue("登录界面是否有验证码", false);
				if (ischeckcode) {
					label_5.setBounds(li_x, li_y, 95, 20); //
					textField_checkcode.setBounds(li_x + 100, li_y, li_textfield_width, 20); //
					li_y = li_y + 20;
					layerpanel.add(label_5, li_zorder++); // //
					layerpanel.add(textField_checkcode, li_zorder++); //
					label_link = new JLabel("<html><font  style=\"line-height:22px;color:#" + tbUtil.convertColor(LookAndFeel.htmlrefcolor) + "\"><u>获取验证码</u></font></html>", SwingConstants.LEFT);
					label_link.setToolTipText("点击获取");
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

			// 多语言..
			if ("YES".equalsIgnoreCase(System.getProperty("MULTILANGUAGE")) || "Y".equalsIgnoreCase(System.getProperty("MULTILANGUAGE"))) { //
				label_4.setBounds(li_x, li_y, 95, 20); //
				comboBox_language.setBounds(li_x + 100, li_y, li_textfield_width, 20); //
				li_y = li_y + 25;
				layerpanel.add(label_4, li_zorder++); // ..
				layerpanel.add(comboBox_language, li_zorder++); // ..
			}

			// 多系统..
			if (ClientEnvironment.getInnerSys() != null && ClientEnvironment.getInnerSys().length > 0) {
				JLabel label_isys = new JLabel("系统：", SwingConstants.RIGHT);
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
		layerpanel.add(label_bgimage, li_zorder++); //由于是数字最大的最新画,所以只能在最后处理
		this.setBackground(Color.WHITE); //
		boolean isLoginImgCenter = false; //是否登录图片居??如果是居中,则控制!!
		if ("Y".equalsIgnoreCase(System.getProperty("ISLOGINIMGCENTER"))) { //是否登录图片居中??
			isLoginImgCenter = true; //
		}
		if (isLoginImgCenter) { //如果居中,则强行设置图片大小在1003*603,然后周围是白色的!
			layerpanel.setPreferredSize(new Dimension(1003, 603)); //
			this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 70)); //
			this.add(layerpanel); //
		} else { //如果是居左,即以前的方式!!
			this.setLayout(new BorderLayout()); //
			this.add(layerpanel, BorderLayout.CENTER); //	
		}
	}

	private void getCheckCode() {
		if (textField_user.getText() == null || textField_user.getText().trim().equals("") || textField_pwd.getText() == null || textField_pwd.getText().trim().equals("")) {
			MessageBox.show(this, "用户名和密码不能为空!");
			return;
		}
		count = tbUtil.getSysOptionIntegerValue("短信验证码再次获取时间", 30);
		try {
			URL url = new URL(System.getProperty("CALLURL") + "/WebCallServlet?StrParCallClassName=" + tbUtil.getSysOptionStringValue("短信验证服务逻辑", "cn.com.psbc.bs.message.CheckCodeService") + "&usercode=" + textField_user.getText() + "&pwd=" + textField_pwd.getText());
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
			conn.setConnectTimeout(6000);//张珍龙改为了0，也不起作用，故改回原代码【李春娟/2018-06-25】
			conn.setReadTimeout(6000);
			conn.setRequestProperty("Content-type", "application/x-compress");
			InputStream response_in = conn.getInputStream();
			String rtn = new String(getByteFromInputStream(response_in));
			if ("1".equals(rtn)) {
				MessageBox.show(this, "在线用户已达上限!");
				return;
			} else if ("2".equals(rtn)) {
				MessageBox.show(this, "用户不存在!");
				return;
			} else if ("3".equals(rtn)) {
				MessageBox.show(this, "系统发现用户重复!");
				return;
			} else if ("4".equals(rtn)) {
				MessageBox.show(this, "用户权限尚未开通!");
				return;
			} else if ("5".equals(rtn)) {
				MessageBox.show(this, "用户已封停!");
				return;
			} else if ("6".equals(rtn)) {
				MessageBox.show(this, "发送成功!");
			} else if ("7".equals(rtn)) {
				MessageBox.show(this, "发送失败!");
				return;
			} else if ("9".equals(rtn)) {
				MessageBox.show(this, "手机号为空!");
				return;
			} else if ("10".equals(rtn)) {
				MessageBox.show(this, "验证码获取时间间隔为" + count + "秒!");
				return;
			} else if ("11".equals(rtn)) {
				MessageBox.show(this, "用户密码错误!");
				return;
			} else {
				MessageBox.show(this, "发生错误请稍后再试!");
				return;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			MessageBox.show(this, "发生错误请稍后再试!");
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
						label_link.setText("<html><font  style=\"line-height:22px;color:#" + tbUtil.convertColor(LookAndFeel.htmlrefcolor) + "\"><u>获取验证码</u></font></html>");
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
					label_link.setText("<html><font  style=\"line-height:22px;color:#" + tbUtil.convertColor(LookAndFeel.htmlrefcolor) + "\"><u>" + count + "秒后可再次获取!</u></font></html>");
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
	 * 取得登录页面的所有热点!
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
		if (tbUtil.getSysOptionBooleanValue("登录界面是否有验证码", false) && ClientEnvironment.getInstance().getLoginModel() != ClientEnvironment.LOGINMODEL_ADMIN) {
			String str_checkcode = textField_checkcode.getText();
			if (str_usercode == null || str_usercode.trim().equals("") || str_pwd == null || str_pwd.trim().equals("") || str_checkcode == null || str_checkcode.trim().equals("")) {
				MessageBox.show(this, "用户名、密码与验证码都不能为空!");
				return; //
			}
		} else {
			if (str_usercode == null || str_usercode.trim().equals("") || str_pwd == null || str_pwd.trim().equals("")) {
				MessageBox.show(this, "用户名与密码都不能为空!"); //
				return; //
			}
		}
		if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) { //管理员模式-管理密码必填[郝明2012-12-05]
			String str_adminpwd = new String(textField_adminpwd.getPassword()); //
			if (str_adminpwd == null || str_adminpwd.equals("")) {
				MessageBox.show(this, "管理密码不能为空!"); //
				return;
			}
		}
		if (tbUtil.getSysOptionBooleanValue("是否记录上次登录帐号", true)) { //如果回写上次登录帐号
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

		// 如果有多个系统...
		String str_sel_isys = null; // 选中的子系统!!
		if (comboBox_innersystems != null) {
			HashVO hvo = (HashVO) comboBox_innersystems.getSelectedItem(); //
			str_sel_isys = hvo.getStringValue("id"); //
		}

		if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) { // 如果是Admin登录
			loader.dealLogin(true, str_usercode, str_pwd, new String(textField_adminpwd.getPassword()), str_sel_isys, true, false, str_languString, null); //
		} else {
			loader.dealLogin(true, str_usercode, str_pwd, null, str_sel_isys, false, false, str_languString, textField_checkcode.getText()); //
		}
		updatePwdDialog();
	}

	/**
	 * 修改密码，因高阳老师提出需要在系统初次登录时提醒修改密码并且需要实现使用一段时间后必须修改密码的逻辑，故增加该方法。
	 * 
	 * 涉及到平台参数“密码修改周期”，只能配置数字（天数），如果小于或等于0天则不用提示修改密码【李春娟/2012-07-12】
	 * 
	 */
	public void updatePwdDialog() {
		int dayslenght = tbUtil.getSysOptionIntegerValue("密码修改周期", -1);
		if (dayslenght <= 0) {
			return;
		}
		String userid = ClientEnvironment.getCurrLoginUserVO().getId();
		if (userid == null) {//密码错误时，这里为空，故需要判断一下【李春娟/2016-07-07】
			return;
		}
		TableDataStruct struct;
		String update_date = null; // 密码修改的日期
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

			if (!iffind) {//如果人员表中没有pwd_update_date字段，就不进行提示了
				return;
			}

			update_date = struct.getBodyData()[0][i];
			if (update_date == null || update_date.equals("")) {
				if (MessageBox.showConfirmDialog(JOptionPane.getFrameForComponent(this), "系统首次登录是否进行密码修改?") == JOptionPane.YES_OPTION) {
					BillDialog dialog = new ResetPwdDialog(JOptionPane.getFrameForComponent(this));
					dialog.setVisible(true);
				} else {
					insertpublic_update_date(userid);
				}
			} else {
				long days = 24 * 3600 * 1000; //一天的毫秒数
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date d1 = df.parse(tbUtil.getCurrDate());
				Date d2 = (Date) df.parse(update_date);
				if ((d1.getTime() - d2.getTime()) / days > dayslenght) {
					if (MessageBox.showConfirmDialog(JOptionPane.getFrameForComponent(this), "系统登录密码使用时间大于" + dayslenght + "天,是否修改?") == JOptionPane.YES_OPTION) {
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
	 * 插入修改密码时间
	 * @param date  修改时间或登陆时间
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
	 * 重置输入值
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
			if (file.exists()) { //如果文件存在,必须存在!意味着该文件必须叫WebPushClient.cmd
				BufferedReader read = new BufferedReader(new FileReader(file)); //
				String str_oldurl = read.readLine();
				try {
					read.close(); //立即关闭,因为后面可能马上回写..
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
				if (System.getProperty("WLTUSERPWD") != null && !System.getProperty("WLTUSERPWD").trim().equals("")) { //如果已有参数!!则删除该参数!!!
					int li_pos = str_oldurl.indexOf("-DWLTUSERPWD="); //找参数位置13位
					String str_prefix = str_oldurl.substring(0, li_pos); //
					String str_subfix = str_oldurl.substring(li_pos + 13, str_oldurl.length()); ////
					int li_pos_2 = str_subfix.indexOf(" "); ////
					str_subfix = str_subfix.substring(li_pos_2 + 1, str_subfix.length()); ////
					String str_newcmd = str_prefix + str_subfix;
					pfw = new PrintWriter(new FileOutputStream(file, false)); //立即回写
					pfw.println(str_newcmd); //写文件
					System.setProperty("WLTUSERPWD", ""); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (pfw != null) {
				pfw.close(); //关闭
			}
		}

	}

	public void itemStateChanged(ItemEvent e) {
		HashVO hvo = (HashVO) e.getItem();
		String str_id = hvo.getStringValue("id"); //
		ImageIcon imgIcon = UIUtil.getImageFromServer("/applet/login_" + str_id + ".jpg"); //先找jpg!
		if (imgIcon == null) {
			imgIcon = UIUtil.getImageFromServer("/applet/login_" + str_id + ".gif"); //如果jpg没有,则找gif
			if (imgIcon == null || imgIcon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
				label_bgimage.setIcon(null); //
				label_bgimage.setText("没有找到图片[" + "/applet/login_" + str_id + ".gif" + "]");
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
	 * 超链接的Label
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
