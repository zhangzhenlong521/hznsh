/**************************************************************************
 * $RCSfile: LoginAppletLoader.java,v $  $Revision: 1.39 $  $Date: 2013/02/20 09:43:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.ConsoleMsgVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopVO;
import cn.com.infostrategy.to.sysapp.login.LoginOneOffVO;
import cn.com.infostrategy.ui.common.ClientConsolePrintStream;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.IAppletLoader;
import cn.com.infostrategy.ui.common.RemoteCallClient;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * ������
 * 
 * @author Administrator
 * 
 */
public class LoginAppletLoader implements IAppletLoader, Serializable {

	private static final long serialVersionUID = -6361060376004420293L;

	private JApplet applet = null;
	public DeskTopPanel deskTopPanel = null; //�������
	public static JPanel mainPanel = null;
	private static LoginAppletLoader appletLoader = null; //�Լ�����.
	private LoginUtil loginUtil = new LoginUtil(); //
	private TBUtil tbUtil = new TBUtil(); 

	/**
	 * ���췽��,�����дӷ������˼���log4j������Ϣ���ʼ����
	 */
	public LoginAppletLoader() {
		this(true);
	}

	public LoginAppletLoader(boolean _isInit) {
		if (_isInit) {
			appletLoader = this; //
			ClientConsolePrintStream mySystemOut = new ClientConsolePrintStream(ConsoleMsgVO.SYSTEM_OUT, System.out); //�ض������̨
			System.setOut(mySystemOut);
			ClientConsolePrintStream mySystemErr = new ClientConsolePrintStream(ConsoleMsgVO.SYSTEM_ERROR, System.err); //�����.
			System.setErr(mySystemErr); //
		}
	}

	/**
	 * ֻ�е�һ�μ���ʱ�Ż�����!!
	 */
	public void loadApplet(JApplet _applet, JPanel _mainPanel) {
		try {
			this.applet = _applet;
			this.mainPanel = _mainPanel; //

			//�������ʵĵ�ַ!!
			System.out.println("Զ�̷��ʵ�ַ(CallURL):[" + System.getProperty("CALLURL") + "]"); //
			System.out.println("�ͻ��˴��뻺��·��:[" + System.getProperty("ClientCodeCache") + "]"); //
			System.out.println("����ģʽ:[" + System.getProperty("WORKMODEL") + "],��¼ģʽ:[" + System.getProperty("LOGINMODEL") + "],����ģʽ:[" + System.getProperty("STARTMODEL") + "],����ģʽ:[" + System.getProperty("DEPLOYMODEL") + "],�Ƿ�ѹ��������[" + System.getProperty("ISZIPSTREAM") + "]"); //����ģʽ
			String str_innersystemtext = System.getProperty("INNERSYSTEM"); //������ϵͳ!!!
			if (str_innersystemtext != null && !str_innersystemtext.trim().equals("") && !str_innersystemtext.trim().equalsIgnoreCase("nulll")) {
				String[] str_items1 = split(str_innersystemtext, ";"); //
				String[][] str_return = new String[str_items1.length][2]; //
				for (int i = 0; i < str_items1.length; i++) {
					str_return[i] = split(str_items1[i], ",");
				}
				ClientEnvironment.setInnerSys(str_return); //������ϵͳ
			}
			String str_losginusercode = _applet.getParameter("LOGINUSERCODE");
			String str_losginuserpwd = _applet.getParameter("LOGINUSERPWD");
			String str_losginuseradminpwd = _applet.getParameter("LOGINUSERADMINPWD"); //��������
//			System.out.println("�û�����[" + str_losginusercode + "],�û�����[" + str_losginuserpwd + "],�û���������[" + str_losginuseradminpwd + "]"); //
			if (str_losginusercode != null) { //������������¼,��ָ���˵�¼�û�(���п���������IE��¼,Ҳ�������Ż������¼��)
				try {
					boolean bo_isadmin = false;
					if (System.getProperty("LOGINMODEL").equals("ADMIN")) {
						bo_isadmin = true;
					}
					String str_language = WLTConstants.SIMPLECHINESE; //Ĭ������...
					if (_applet.getParameter("LANGUAGE") != null) {
						str_language = _applet.getParameter("LANGUAGE");
					}

					boolean isquicklogin = false; //�Ƿ���ٵ�¼,��ν���ٵ�¼����ָ��ҪУ��������!!! ���絥���¼ʱ!!!
					String str_logintype = _applet.getParameter("REQ_logintype"); //��¼����
					if ("single".equals(str_logintype) || "skip2".equals(str_logintype) || "Y".equalsIgnoreCase(System.getProperty("AutoLogin"))) { //����ǵ����¼!����������skip2,��ʵҲ�ǵ����¼!!
						//System.out.println("�����ǵ����¼ģʽ...."); //
						isquicklogin = true; //���ٵ�¼!!!
					}
					String checkcode = _applet.getParameter("REQ_checkcode");
					dealLogin(false, str_losginusercode, str_losginuserpwd, str_losginuseradminpwd, _applet.getParameter("ISYS"), bo_isadmin, isquicklogin, str_language, checkcode); //
				} catch (Exception ex) { //���dealLoginʧ��,��ص���¼����!!
					ex.printStackTrace();

				}
			} else { //���û��ָ����¼�û�,�������¼��ҳ
				//System.out.println("�û�����Ϊ��,���ص�¼����..."); //
				loadLoginPanel(null, null); //���ص�¼ҳ��!!!	
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			//loginUtil.startClientRefreshThread(); //�����߳�!!!
		}
		//showMeInIeBrowse(); //
	}

	/**
	 * ���ص�¼ҳ��!!!
	 */
	public void loadLoginPanel(String _userCode, String _errMsg) {
		mainPanel.removeAll();
		deskTopPanel = null;
		mainPanel.setLayout(new BorderLayout()); //
		LoginPanel contentPanel = new LoginPanel(this, _userCode, _errMsg); //
		mainPanel.add(contentPanel);
		loginUtil.setDefaultLookAndFeel(mainPanel); //�������..
		mainPanel.updateUI();
	}

	/**
	 * ������ҳ��!!!
	 * @param _userVO 
	 * @param deskTopVO 
	 * @param _usercode �û�����
	 * @param _logintime
	 */
	public void loadDeskTopPanel(CurrLoginUserVO _userVO, DeskTopVO _deskTopVO) {
		mainPanel.removeAll();
		deskTopPanel = null;
		loginUtil.setWLTLookAndFeel(_userVO.getAllLookAndFeels(), mainPanel); //���÷��.
		mainPanel.setLayout(new BorderLayout()); //
		deskTopPanel = new DeskTopPanel(null, _deskTopVO); //
		mainPanel.add(deskTopPanel, BorderLayout.CENTER);
		mainPanel.repaint();
		mainPanel.revalidate(); //
		System.gc(); //
		//mainPanel.updateUI();
	}

	/**
	 * 
	 * @param _userCode �û�����
	 * @param _pwd  �û�����
	 * @param _adminpwd  ��������
	 * @param _isys  ѡ�����ĸ���ϵͳ
	 * @param _isquicklogin �Ƿ��ݵ�¼
	 * @return
	 */
	public void dealLogin(boolean _isSplash, final String _userCode, final String _pwd, final String _adminpwd, final String _isys, final boolean _isadmin, final boolean _isquicklogin, String _language, final String checkcode) {
		closeAllOtherFrame(); //�ر���������Frame����!!!��Ϊ���µ�¼,���л��û�����ʱ,����������frame��������ȹر�,����ĳ���б�鿴��!!
		ClientEnvironment.getInstance().setDefaultLanguageType(_language); //��������..
		ClientEnvironment.chooseISys = _isys; //����ѡ�е���ϵͳ!!
		if (_isSplash) { //����ǵȴ����ȥ����,���������ʱ�Ͳ�Ҫ��,���������Ҫ
			new SplashWindow(mainPanel, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					realdealLogin(_userCode, _pwd, _adminpwd, _isys, _isadmin, _isquicklogin, checkcode); //
				}
			}, 366, 366);
		} else { //����������������,��ȴ�������һ��ʼ������,���������ȴ���!!!
			realdealLogin(_userCode, _pwd, _adminpwd, _isys, _isadmin, _isquicklogin, checkcode); //
		}
	}

	private String getLocalHostIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception ex) {
			return "0.0.0.0"; //
		}
	}

	/**
	 * ֱ�Ӵ���
	 * @param _userCode
	 * @param _pwd
	 * @param _adminpwd
	 * @param _isadmin
	 * @param _isquicklogin
	 */
	public void realdealLogin(String _userCode, String _pwd, String _adminpwd, String _isys, boolean _isadmin, boolean _isquicklogin, String checkcode) {
		//		if (_userCode == null || _userCode.trim().equals("")) {
		//			MessageBox.show(mainPanel, "�û�������Ϊ��!", WLTConstants.MESSAGE_WARN);   //��Ϊ�еȴ����ס�߳�,�������������Զ����ס��������,ϵͳ��һֱ����һ�ּ��ص�״̬!! ����������һ��!!! ����û������! ע������!!!
		//			return;
		//		}
		try {
			LoginOneOffVO loginOneOffVO = doLogin(_userCode, _pwd, _adminpwd, _isys, _isadmin, _isquicklogin, checkcode); //������¼�߼�����! ���ͻ��˻���������ֵ!!!
			loadDeskTopPanel(loginOneOffVO.getCurrLoginUserVO(), loginOneOffVO.getDeskTopVO()); // ����������!!!!!!!!!!!!!!!!!!!!!
			if (UIUtil.getCommonService().getSysOptionBooleanValue("�ͻ����Ƿ��������������", false)) {
				// �����������ſ�ʼִ�������߳�
				new Timer().schedule(new TimerTask() {
					public void run() {
						try {
							RemoteCallClient rc = new RemoteCallClient(System.getProperty("URL") + "/WebCallServlet?StrParCallClassName=cn.com.infostrategy.bs.common.ClientOLMaService&isdispatch=Y&actype=reg");
							rc.callServlet("", "", null, null, 5000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 10000, 15000);
				// 
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //���ʧ��,
			loadLoginPanel(_userCode, ex.getMessage()); //���ʧ��,����ص�¼ҳ��,����������Ϣ��ʾ�ڵ�¼������!!!
		}
	}

	public LoginOneOffVO doLogin(String _userCode, String _pwd, String _adminpwd, String _isys, boolean _isadmin, boolean _isquicklogin, String checkcode) throws Exception {
		//������¼!!!
		LoginOneOffVO loginOneOffVO = getSysAppService().loginOneOff(_userCode, _pwd, _adminpwd, new Boolean(_isadmin), new Boolean(_isquicklogin), System.getProperties(), checkcode); //ֻ��һ�ε�¼

		//����Log4j����...
		ClientEnvironment.getInstance().setLog4jConfigVO(loginOneOffVO.getLog4jConfigVO()); //

		WLTLogger.config(System.getProperty("ClientCodeCache"), loginOneOffVO.getLog4jConfigVO().getClient_level(), loginOneOffVO.getLog4jConfigVO().getClient_outputtype()); //����Log4J����
		WLTLogger.getLogger(LoginAppletLoader.class).info("�ɹ�����Log4j,level[" + loginOneOffVO.getLog4jConfigVO().getClient_level() + "],outputtype[" + loginOneOffVO.getLog4jConfigVO().getClient_outputtype() + "]");

		//������ϵͳ,ȥ��������ȡ��ϵͳ����!
		String str_innersystemtext = System.getProperty("INNERSYSTEM"); //
		if (ClientEnvironment.getInnerSys() == null && str_innersystemtext != null && !str_innersystemtext.trim().equals("") && !str_innersystemtext.trim().equalsIgnoreCase("nulll")) {
			String[] str_items1 = split(str_innersystemtext, ";"); //
			String[][] str_return = new String[str_items1.length][2]; //
			for (int i = 0; i < str_items1.length; i++) {
				str_return[i] = split(str_items1[i], ","); //
			}
			ClientEnvironment.setInnerSys(str_return); //������ϵͳ
		}

		//��������Դ...
		System.setProperty("defaultdatasource", loginOneOffVO.getDataSourceVOs()[0].getName()); //����Ĭ������Դ
		ClientEnvironment.getInstance().put("defaultdatasource", loginOneOffVO.getDataSourceVOs()[0].getName()); //
		ClientEnvironment.getInstance().setDataSourceVOs(loginOneOffVO.getDataSourceVOs()); //

		//���õ�¼��ԱSession��Ϣ
		CurrLoginUserVO userVO = loginOneOffVO.getCurrLoginUserVO(); //��¼�û���Ϣ
		ClientEnvironment.setCurrLoginUserVO(userVO); //���ÿͻ��˻����еĵ�ǰ��¼�û�����Ϣ

		if (_isadmin) { //����ǹ���Ա��¼!!
			ClientEnvironment.getInstance().setLoginModel(ClientEnvironment.LOGINMODEL_ADMIN); //
		} else { //�������ͨ�û���¼!!
			ClientEnvironment.getInstance().setLoginModel(ClientEnvironment.LOGINMODEL_NORMAL); //
		}

		//����Session�еĵ�¼��Ա��Ϣ
		ClientEnvironment.getCurrSessionVO().setLoginUserId(userVO.getId()); //
		ClientEnvironment.getCurrSessionVO().setLoginUserCode(userVO.getCode()); //
		ClientEnvironment.getCurrSessionVO().setLoginUserName(userVO.getName()); //
		ClientEnvironment.getInstance().put("clientip", getLocalHostIP()); //��¼�ͻ���ip
		ClientEnvironment.getCurrSessionVO().setLoginUserPKDept(userVO.getPKDept()); //
		return loginOneOffVO; //
	}

	public String[] split(String _par, String _separator) {
		Vector v_return = new Vector();
		StringTokenizer st = new StringTokenizer(_par, _separator);
		while (st.hasMoreTokens()) {
			v_return.add(st.nextToken());
		}
		return (String[]) v_return.toArray(new String[0]); //
	}

	private void showMeInIeBrowse() {
		//		if (ClientEnvironment.getInstance().getStartModel() == ClientEnvironment.STARTMODEL_BROWSE) { //������������ʽ����!!!
		//			JSObject win = JSObject.getWindow(this.getApplet());
		//			win.eval("window.showWLDiv()"); //��ʾҳ���!!!
		//		}
	}

	/**
	 * �ر���������Frame!��Ϊ��ʱ��򿪶��Frame,��ʱ���µ�¼ʱ,Ӧ����Ҫ�ر��Ѵ򿪹���Frame,ֻ�����������Frame!
	 */
	private void closeAllOtherFrame() {
		try {
			Frame rootFrame = null; //
			if (this.mainPanel != null) {
				rootFrame = JOptionPane.getFrameForComponent(this.mainPanel); //
			}
			Frame[] allFrames = Frame.getFrames(); //�ҵ�����Frame
			for (int i = 0; i < allFrames.length; i++) {
				//System.out.println("��[" + (i + 1) + "]��Frame=" + allFrames[i]); //
				if (allFrames[i].isVisible() && allFrames[i].isShowing()) { //�������ʾ״̬!!
					String str_clientprop = null; //
					if (allFrames[i] instanceof JFrame) {
						str_clientprop = (String) ((JFrame) allFrames[i]).getRootPane().getClientProperty("WebPushFrameMark"); //
					}
					if ("WebPushLoadFrame".equals(str_clientprop) || "WebPushRootFrame".equals(str_clientprop)) { //�����LoadFrame������RootFrame,����������!!!
						continue; //
					}

					String str_name = allFrames[i].getClass().getName(); //
					if (str_name.equals("javax.swing.SwingUtilities$SharedOwnerFrame") || str_name.equals("cn.com.weblight.applet.WLTAppletMainFrame")) { //
						continue; //
					}
					if (allFrames[i] == rootFrame) {
						continue;
					}
					allFrames[i].dispose(); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * �˳�ϵͳ,��ʵ�������Session��ֵ!!
	 */
	private void exitSys() {
		try {
			System.out.println("׼���˳�ϵͳ�ˣ���������"); //
			//ɾ������skip2��¼ģʽ�������ļ�
			deleteSkip2File(); //

			String str_sessionid = System.getProperty("SESSIONID"); //
			//Զ�̵�����շ������˵�Session�����е�ֵ,�����鿴�����û��Ż�������׼ȷ!!!
			if (str_sessionid != null) {
				SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class, 10); //ֻ��10��,����10���ǿ���ж�!����Ϊ�˱�֤���˳�,��Ϊ�ܿ��ܷ�����ͣ��!
				//�˳�ϵͳ����¼��־
				CurrLoginUserVO loginUserVO = ClientEnvironment.getCurrLoginUserVO();
				service.loginOut(loginUserVO.getId(), str_sessionid); //
			}
			Method md = Class.forName("cn.com.infostrategy.ui.mdata.QuickSearchHisMap").getDeclaredMethod("writeQuickSearchHisToCache"); //��������ʷ��ŵ��ͻ���
			md.invoke(md, null);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ��ʱɱ�����˳���,��������ʱ�Ͳ����Զ�ɾ���������ļ���,����Ҫ�ֶ�ɾ��!!!!
	 */
	private void deleteSkip2File() {
		String str_fileName1 = System.getProperty("user.home") + "\\PushSkip2Menu.txt";
		String str_fileName2 = System.getProperty("user.home") + "\\PushSkip2Alive.txt";
		try {
			File file1 = new File(str_fileName1); //
			if (file1.exists()) {
				boolean delResult = file1.delete(); //
				System.out.println("ɾ���ļ�[" + str_fileName1 + "],���=[" + delResult + "]"); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		try {
			File file2 = new File(str_fileName2); //
			if (file2.exists()) {
				boolean delResult = file2.delete(); //
				System.out.println("ɾ���ļ�[" + str_fileName2 + "],���=[" + delResult + "]"); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//���ͻ��������������õ��˳�ϵͳǰ�Ĳ���!!!
	public static void exitSysByClientJVMRefCall() {
		if (appletLoader != null) {
			appletLoader.exitSys();
		}
	}

	/**
	 * ˢ����ҳ����
	 */
	private void refreahIndexTaskGroup() {
		if (deskTopPanel != null) {
			//System.out.println("ˢ����ҳ...");  //
			((DeskTopPanel) deskTopPanel).refreshAllTaskGroup(); //ˢ����ҳ
		}
	}

	public JApplet getApplet() {
		return applet;
	}

	public void setApplet(JApplet applet) {
		this.applet = applet;
	}

	public JPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(JPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	private FrameWorkCommServiceIfc getCommService() throws Exception {
		return (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class); //����Զ�̷���
	}

	public static LoginAppletLoader getAppletLoader() {
		return appletLoader;
	}

	/**
	 * ȡ��SysAppService
	 * @return
	 */
	private SysAppServiceIfc getSysAppService() throws Exception {
		return (SysAppServiceIfc) RemoteServiceFactory.getInstance().lookUpService(SysAppServiceIfc.class); //����Զ�̷���
	}

}
