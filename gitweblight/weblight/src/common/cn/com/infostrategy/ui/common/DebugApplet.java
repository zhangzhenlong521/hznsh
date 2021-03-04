/**************************************************************************
 * $RCSfile: DebugApplet.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DebugApplet extends JApplet {
	private static final long serialVersionUID = 1L;

	private IAppletLoader applerLoader = null;
	private String str_sourceCode = null; // 
	private Hashtable appletParmMap = null; //

	/**
	 * Ĭ�Ϲ��췽��,����ΪApplet��������ʹ����,���Ǹ���Ӧ�ó�����!!��ֱ������Main����
	 */
	public DebugApplet() {
		System.err.println("��������������ΪApplet,���Ǹĳ�Application��ʽ������,����ֻ��һ��,��:http://127.0.0.1:9001/cmbc16/login?admin=Y&usercode=admin&pwd=1"); //
		System.exit(0); //
	}

	public DebugApplet(String _url) {
		System.out.println("���ʵ�ַ����=[" + _url + "]"); //
		infuseClientSysProp(); //��ǿ��ָ��һЩϵͳ����
		try {
			str_sourceCode = getURlHtml(_url, "logintype=desktop"); //Զ�̷���ȡ��Դ����
			appletParmMap = parseHtmlContent(str_sourceCode); //����Դ��ɹ�ϣ��
			final JFrame frame = new JFrame("������ѯWebPush�������� - " + System.getProperty("URL")); //
			frame.getRootPane().putClientProperty("WebPushFrameMark", "WebPushRootFrame"); //

			Font font = new Font("����", Font.PLAIN, 12); //
			Menu menu = new Menu("ϵͳ�˵�"); //
			final MenuItem menuItem_1 = new MenuItem("Դ����"); ////
			final MenuItem menuItem_2 = new MenuItem("��������"); ////
			final MenuItem menuItem_3 = new MenuItem("ϵͳ����"); ////
			AbstractAction action = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == menuItem_1) {
						showSource(); //
					} else if (e.getSource() == menuItem_2) {
						showAppletParam(); //
					} else if (e.getSource() == menuItem_3) {
						showSysProp(); //
					}
				}
			};

			menuItem_1.addActionListener(action); //
			menuItem_2.addActionListener(action); //
			menuItem_3.addActionListener(action); //
			menu.setFont(font); //
			menuItem_1.setFont(font); //
			menuItem_2.setFont(font); //
			menuItem_3.setFont(font); //
			menu.add(menuItem_1); //
			menu.add(menuItem_2); //
			menu.add(menuItem_3); //
			MenuBar menuBar = new MenuBar(); //
			menuBar.add(menu); //
			frame.setMenuBar(menuBar); //
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					onExit();
				}
			});
			frame.setSize(1280, 770); //�����������Ǵ���Ļ,ͳһ���1280*800����,��ǰ��1024*768��!!
			frame.setVisible(true); //
			frame.toFront(); //����ʾ����
			new SplashWindow(frame, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					loadApplet(frame, appletParmMap); ///ֱ��ʹ��һ���µ�Frame���ر�Applet
				}
			}, 350, 350);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void init() {
		String str_jreversion = System.getProperty("java.version"); //
//		if (str_jreversion.indexOf("1.6") != 0) { //�������1.6�汾
//			JLabel label = new JLabel("����ϵͳ����,�ͻ���Jre�汾��ͱ�����1.6,������JRE�汾.."); //
//			this.getContentPane().add(label); //
//			return;
//		}

		addSysPropByServerReturnAsFirstTime(); //�ڷ��ʹ���������,���ݷ������˵ķ���ֵ,���¼���һЩ�ͻ�������,����ClientCodeCache

		this.getContentPane().setLayout(new BorderLayout());
		JPanel mainPanel = new JPanel();
		mainPanel.setName("mainPanel"); //
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setOpaque(false);
		mainPanel.setBackground(java.awt.Color.WHITE);
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		try {
			applerLoader = new cn.com.infostrategy.ui.sysapp.login.LoginAppletLoader();
			applerLoader.loadApplet(this, mainPanel);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//�ڷ��ʹ���������,���ݷ������˵ķ���ֵ,���¼���һЩ�ͻ�������
	private void addSysPropByServerReturnAsFirstTime() {
		System.setProperty("RemoteCallServletURL", System.getProperty("CALLURL") + "/RemoteCallServlet"); //
		System.setProperty("ClientCodeCache", getClientCodeCachePath()); //
	}

	/**
	 * ȡ�ÿͻ��˻���·��
	 * @return
	 */
	public String getClientCodeCachePath() {
		String str_dir = System.getProperty("user.home") + "\\WEBLIGHT_CODECACHE_DEBUG\\";
		File file = new File(str_dir);
		if (!file.exists()) {
			file.mkdir(); //����Ŀ¼
		}
		return str_dir; //
	}

	private void infuseClientSysProp() {
		System.setProperty("java.awt.im.style", "on-the-spot"); //�������벻Ҫ������
		System.setProperty("JVMSITE", "CLIENT"); //ָ���ǿͻ��������
		System.setProperty("WORKMODEL", "DEBUG"); //����ģʽ,�п���ģʽ������ģʽ֮��!
		System.setProperty("STARTMODEL", "DESKTOP"); //����ģʽ!!
		System.setProperty("DEPLOYMODEL", "SINGLE"); //����ģ�岿��
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("����Ϊ�˳�������һ������,ֵΪ[http://127.0.0.1:9001/cmbc/login]������!!");
			System.exit(0); //
		}
		new DebugApplet(args[0]); //
	}

	//����URL,����ʵ�ʵ�html
	private String getURlHtml(String _url, String _requestPar) throws Exception {
		String str_convertUrl = _url;
		if (_requestPar != null && !_requestPar.equals("")) {
			if (_url.indexOf("?") < 0) {
				str_convertUrl = _url + "?" + _requestPar;
			} else {
				str_convertUrl = _url + "&" + _requestPar;
			}
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(str_convertUrl).openStream()));
		String str;
		StringBuffer sb_html = new StringBuffer();
		while ((str = in.readLine()) != null) {
			sb_html.append(str + "\r\n"); //
		}
		in.close();
		return sb_html.toString(); //
	}

	//��ʾԴ����
	private void showSource() {
		showTextInFrame("�鿴Դ����", str_sourceCode); //
	}

	//��ʾApplet�Ĳ���
	private void showAppletParam() {
		String[] str_keys = (String[]) appletParmMap.keySet().toArray(new String[0]);
		Arrays.sort(str_keys); //
		StringBuffer sb_text = new StringBuffer(); //
		for (int i = 0; i < str_keys.length; i++) {
			if (str_keys[i].equalsIgnoreCase("loginuserpwd") || str_keys[i].equalsIgnoreCase("loginuseradminpwd")) { //������������ݣ�������ʾ..
				sb_text.append(str_keys[i] + "=[******]\r\n"); ////
			} else {
				sb_text.append(str_keys[i] + "=[" + appletParmMap.get(str_keys[i]) + "]\r\n"); ////
			}
		}
		showTextInFrame("�鿴WLTApplet���в���", sb_text.toString()); //
	}

	//��ʾϵͳ����
	private void showSysProp() {
		StringBuffer sb_text = new StringBuffer();
		Properties sysProps = System.getProperties(); //
		String[] str_keys = (String[]) sysProps.keySet().toArray(new String[0]); //
		Arrays.sort(str_keys); //
		for (int i = 0; i < str_keys.length; i++) {
			sb_text.append(str_keys[i] + "=[" + sysProps.getProperty(str_keys[i]) + "]\r\n"); //
		}
		showTextInFrame("�鿴ϵͳ����", sb_text.toString()); //
	}

	private void showTextInFrame(String _title, String _text) {
		JFrame frame = new JFrame(_title);
		frame.setSize(800, 450); //
		frame.setLocation(0, 0); //
		JTextArea textArea = new JTextArea(_text); //
		textArea.setFont(new Font("����", Font.PLAIN, 12)); //
		textArea.setEditable(false); //
		frame.getContentPane().add(new JScrollPane(textArea)); //
		frame.setVisible(true); //
	}

	private Hashtable parseHtmlContent(String _text) throws Exception {
		String str_text = _text; //��Servlet�з��ص�ʵ�ʵ�html...
		int li_pos_begin = str_text.indexOf("<Applet"); //
		int li_pos_end = str_text.indexOf("</Applet>"); //
		String str_applettext = str_text.substring(li_pos_begin, li_pos_end + 9); ////��ȡ�ַ���
		String str_xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + str_applettext; //���ڽ���XML��
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); ////
		factory.setValidating(false);
		Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(str_xml))); //����XML!!!
		NodeList list = doc.getDocumentElement().getElementsByTagName("*"); //�ҳ����в���
		Hashtable ht_pars = new Hashtable(); //
		for (int i = 0; i < list.getLength(); i++) {
			Element childNode = (Element) list.item(i); //
			String str_name = childNode.getAttribute("NAME"); //
			String str_value = childNode.getAttribute("VALUE"); //
			//System.out.println("[" + str_name + "]=[" + str_value + "]"); //�������...
			if (str_value == null || str_value.equals("null")) {
				str_value = "";
			}
			ht_pars.put(str_name, str_value); //
			if (str_name != null) {
				if (str_value != null) {
					System.setProperty(str_name, str_value); //������PARAM�е�ֵ��һʱ�䶼һ����©������ͻ���ϵͳ����!!�����ڻ�û�м�������Ӧ��ʱ�ͽ�<applet></applet>�ж�������в�����Load������SystemPropties!!!
					//System.out.println("����ϵͳ����[" + str_name + "]=[" + str_value + "]"); //
				} else {
					System.setProperty(str_name, ""); //
				}
			}
		}

		return ht_pars;
	}

	private void loadApplet(JFrame frame, Hashtable parMap) {
		this.setStub(new MyAppletStub(parMap)); //
		this.init(); //
		this.start();
		frame.getContentPane().add(this); //����...
		frame.validate(); //
	}

	private void onExit() {
		if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ�˳�ϵͳ��?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}
		cn.com.infostrategy.ui.sysapp.login.LoginAppletLoader.exitSysByClientJVMRefCall(); //
		System.exit(0); //
	}
}

class MyAppletStub implements AppletStub {
	private Hashtable parMap = null; //

	MyAppletStub(Hashtable _parMap) {
		parMap = _parMap; ////
	}

	public void appletResize(int width, int height) {
	}

	public AppletContext getAppletContext() {
		return null;
	}

	public URL getCodeBase() {
		return null;
	}

	public URL getDocumentBase() {
		return null;
	}

	public String getParameter(String _key) {
		return (String) parMap.get(_key); //ȫ����д
	}

	public boolean isActive() {
		return false;
	}
}
