package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.UIManager;

import org.jdesktop.jdic.browser.WebBrowser;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.sysapp.login.StyleTempletDefineBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;

/**
 * ��Ϊ������������Ҫ���Ե�����һ�����ܵ�! ���������ط���Ҫ���ø���һ���˵����ݴ�һ�����ܵ�! ���Ը��˸ù�����!!Ȼ���������е���ǵ�����!!�����򿪹��ܵ�Ҳ�ǵ�����!!! 
 * @author xch
 */
public class LoginUtil {

	private TBUtil tbUtil = new TBUtil(); //
	private Timer timer_1, timer_2 = null; //��̬����!!!
	private SimpleDateFormat sdf_curr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE); //

	/**
	 * ���ݲ˵�idȡ�øò˵����!!!
	 * @param _menuid
	 * @return
	 * @throws Exception
	 */
	public AbstractWorkPanel getWorkPanelByMenuVO(String _menuid) throws Exception {
		if (_menuid == null || _menuid.trim().equals("")) {
			throw new Exception("�˵�idΪ��!"); //
		}
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_menu where id='" + _menuid + "'"); //
		if (hvs == null || hvs.length == 0) {
			throw new Exception("û���ҵ�id=[" + _menuid + "]�Ĳ˵�!!!"); //
		}
		return getWorkPanelByMenuVO(hvs[0]); //
	}

	//���ݲ˵�VOȡ��
	public AbstractWorkPanel getWorkPanelByMenuVO(HashVO _hvo) throws Exception {
		String str_menuname = _hvo.getStringValue("name"); // 
		String str_usecmdtype = _hvo.getStringValue("usecmdtype"); //
		String str_commandtype = null; //
		String str_command = null; //
		String confStr = null;
		if (str_usecmdtype == null || str_usecmdtype.equals("1")) {
			str_commandtype = _hvo.getStringValue("commandtype");
			str_command = _hvo.getStringValue("command");
		} else if (str_usecmdtype.equals("2")) {
			str_commandtype = _hvo.getStringValue("commandtype2"); //
			str_command = _hvo.getStringValue("command2"); //
		} else if (str_usecmdtype.equals("3")) {
			str_commandtype = _hvo.getStringValue("commandtype3"); //
			str_command = _hvo.getStringValue("command3"); //
		}
		_hvo.setAttributeValue("$commandtype", str_commandtype); //
		_hvo.setAttributeValue("$command", str_command); //

		AbstractWorkPanel workItemPanel = null;
		JepFormulaParse jepParse = null; //
		if (str_commandtype == null || str_commandtype.trim().equals("null") || str_commandtype.trim().equals("")) {
			throw new Exception("�򿪽��[" + str_menuname + "]ʧ��(��Ϊ·������Ϊ��),CommandType[" + str_commandtype + "],Command[" + str_command + "]"); //
		} else if (str_commandtype.equals("99")) { // ֱ��Frame,��command��������·��
			if (str_command.startsWith("http://")) {
				//�Զ�����url
				WLTTabbedPane tabpanel = DeskTopPanel.getDeskTopPanel().getWorkTabbedPanel();
				Point pt = tabpanel.getLocationOnScreen();
				StringBuffer urlsb = new StringBuffer();
				String[] str1 = tbUtil.split(str_command.trim(), "?"); //�ʺ�ǰ��
				urlsb.append(str1[0] + "?");
				HashMap parm = TBUtil.getTBUtil().convertStrToMapByExpress(str1[1], "&", "=", true); //&����
				if (!parm.containsKey("frame_x")) {
					parm.put("FRAME_X", (int) pt.getX());
				}
				if (!parm.containsKey("frame_y")) {
					parm.put("FRAME_Y", (int) pt.getY());
				}
				if (!parm.containsKey("frame_width")) {
					parm.put("FRAME_WIDTH", (int) tabpanel.getWidth());
				}
				if (!parm.containsKey("frame_height")) {
					parm.put("FRAME_HEIGHT", (int) tabpanel.getHeight());
				}
				Iterator it = parm.entrySet().iterator();
				while (it.hasNext()) {
					Entry en = (Entry) it.next();
					String key = (String) en.getKey();
					String value = "" + en.getValue();
					urlsb.append(key + "=" + value + "&");
				}
				if (parm.size() > 0) {
					urlsb = new StringBuffer(urlsb.substring(0, urlsb.length() - 1));
				}
				WebBrowser wb = new WebBrowser(new URL(urlsb.toString()), false); //
				JWindow win = new JWindow(); //
				win.setSize(0, 0); //
				win.getContentPane().add(wb); //
				win.setVisible(true); //
			} else {
				JFrame frame = (JFrame) Class.forName(str_command.trim()).newInstance();
				frame.setVisible(true); //
				frame.toFront(); //
			}
			return null; //
		} else if (str_commandtype.equals("00")) { // �Զ��幤�����!!
			String str_classname = str_command.trim(); //
			if (str_classname.indexOf("(") > 0) {
				String str_prefix = str_classname.substring(0, str_classname.indexOf("(")); //
				String str_pars = str_classname.substring(str_classname.indexOf("(") + 1, str_classname.indexOf(")")); //
				String[] str_par_items = tbUtil.split(str_pars, ","); //
				for (int i = 0; i < str_par_items.length; i++) {
					str_par_items[i] = str_par_items[i].trim(); //
					str_par_items[i] = str_par_items[i].trim(); //
					str_par_items[i] = tbUtil.replaceAll(str_par_items[i], "\"", "");
				}
				Class clss = Class.forName(str_prefix);
				Class[] consTransParCls = new Class[str_par_items.length]; //
				for (int i = 0; i < consTransParCls.length; i++) {
					consTransParCls[i] = String.class; //
				}
				Constructor constructor = clss.getConstructor(consTransParCls);
				workItemPanel = (AbstractWorkPanel) constructor.newInstance(str_par_items); //
			} else {
				workItemPanel = (AbstractWorkPanel) Class.forName(str_classname).newInstance(); //
			}
		} else if (str_commandtype.equalsIgnoreCase("11")) { //XMLע���ϵͳ����!!!
			String str_cmd = str_command.trim(); //
			String str_menuName = str_cmd.substring(0, str_cmd.indexOf(";")); //�˵���!!
			String str_xmlFile = str_cmd.substring(str_cmd.indexOf(";") + 1, str_cmd.length()); //xml�ļ�!!
			String[] str_realcommdnad = getSysAppService().getOneRegMenuCommand(str_xmlFile, str_menuName); //

			if (str_realcommdnad == null || str_realcommdnad.length == 0) {
				throw new Exception("�˵�������XMLע�Ṧ�ܵ㣬XML��·���Ѹı䣬����������!");
			}
			String xml_str_commandtype = "00";
			if (str_realcommdnad != null && str_realcommdnad.length >= 3) {
				if (str_realcommdnad[2] != null) {
					xml_str_commandtype = str_realcommdnad[2];
					_hvo.setAttributeValue("$commandtype", xml_str_commandtype); //
					_hvo.setAttributeValue("$command", str_realcommdnad[0]); //
					_hvo.setAttributeValue("command", str_realcommdnad[0]);
					_hvo.setAttributeValue("commandtype", xml_str_commandtype);
				}
			}
			if (!"00".equals(xml_str_commandtype)) {
				workItemPanel = getWorkPanelByMenuVO(_hvo);
			} else {
				workItemPanel = (AbstractWorkPanel) Class.forName(str_realcommdnad[0]).newInstance(); //����ʵ��!!!
			}
			if (ClientEnvironment.isAdmin()) { //����ǹ���Ա��½����ô��ע�Ṧ��ʵ�ʵ��õ�·�����뻺���С����ڿ����߲鿴��
				workItemPanel.putClientProperty("$realpath", str_realcommdnad[0]);
			}
			confStr = str_realcommdnad[1];
		} else if (str_commandtype.equalsIgnoreCase("0A")) { //Formatģ��,����A,����ŶA,��������ʾʱ����һ��,�����׳�����!! 
			workItemPanel = (AbstractWorkPanel) Class.forName("cn.com.infostrategy.ui.mdata.styletemplet.format.DefaultStyleWorkPanel_0A").newInstance();
		} else if (str_commandtype.equalsIgnoreCase("ST")) { // ���ģ��!!!
			if (str_command == null || str_command.trim().equals("")) {
				throw new Exception("�˵������Ƿ��ģ��,��û�ж��幫ʽ!!"); //
			}
			str_command = str_command.trim(); //
			if (str_command.endsWith(",")) { //
				str_command = str_command.substring(0, str_command.length() - 1); //
			}
			if (str_command.contains("setStyleType(")) { //������˷��ģ�壬���ݲ�Ʒǰ��ϵͳ haoming
				StringBuffer sb = new StringBuffer();
				String str[] = str_command.split(";");
				for (int i = 0; i < str.length; i++) {
					if (str[i].contains("setStyleType(")) {
						sb.append("\"���ģ������\",");
						sb.append(str[i].substring(str[i].indexOf("(") + 1, str[i].indexOf(")")) + ",");
					} else {
						sb.append(str[i].substring(str[i].indexOf("(") + 1, str[i].indexOf(",")) + ",");
						sb.append(str[i].substring(str[i].indexOf(",") + 1, str[i].indexOf(")")) + ",");
					}
				}
				if (sb.length() > 0) {
					str_command = sb.substring(0, sb.length() - 1);
				}
			}
			String str_formula = "getParAsMap(" + str_command + ")"; ////
			jepParse = JepFormulaParse.createUIJepParse(); //����������!!
			HashMap confMap = (HashMap) jepParse.execFormula(str_formula); //ִ�й�ʽ,�õ���ϣ��!!!
			String str_styleType = (String) confMap.get("���ģ������"); //
			if (str_styleType == null) {
				throw new Exception("û�ж���ؼ�����[���ģ������]!");
			}

			StyleTempletDefineBuilder stdb = new StyleTempletDefineBuilder(); //
			String str_defaultClassName = stdb.getDefaultClassName(str_styleType); //
			if (str_defaultClassName == null) {
				throw new Exception("δ֪�ķ��ģ������[" + str_styleType + "]!"); //
			}
			workItemPanel = (AbstractWorkPanel) Class.forName(str_defaultClassName).newInstance(); // ���䶯̬�������ģ���Ĭ��ʵ����
			workItemPanel.addMenuConfMap(confMap); //�������!!
		}

		//����˵��Զ���Ĳ���!!!
		String str_conf = _hvo.getStringValue("conf"); //����!!

		if (str_conf != null && !str_conf.trim().equals("")) {
			HashMap confMap = convertConfStrToHashMap(str_conf, jepParse);
			workItemPanel.addMenuConfMap(confMap); //�����Զ������!!
		} else {//ֻ�е��Լ�����ΪNULLʱ������ͼ�������ò˵��Ĳ���
			//if ((workItemPanel instanceof ParameterCreatedWKPanelIfc) && confStr != null && !confStr.trim().equals("")) { //
			if (confStr != null && !confStr.trim().equals("")) { //��ҫ����ǰ���˸��ж�,(workItemPanel instanceof ParameterCreatedWKPanelIfc),�����˸��ӿ�,��û���κη���,��֪����???Ҳû��Ϊʲô��ô���ĳ����ڱ�ע��д���!!!��ע��,����������!!
				//���������Ϊ���ж�һ�������Ƿ���ĳ������,��û����ʹ��һ���ӿ�����,�ڻ���������һ������,Ȼ�����������ع��������,Ȼ��ʹ����������ж�!! ����ֱ��ʹ��putClientProperty(key, value),�����ض����ֵ,Ȼ��������ȡ�����ж�!!��ʵ�����������취,��֮û�б��뵥����һ���ӿ������������ж�!!!
				HashMap confMap = convertConfStrToHashMap(confStr, jepParse);
				workItemPanel.addMenuConfMap(confMap); //�������ò˵��Ĳ���!!
			}
		}

		workItemPanel.setSelectedMenuVOs(_hvo); // ѡ�еĲ˵���!!
		workItemPanel.setLayout(new BorderLayout()); //
		return workItemPanel; //
	}

	/**
	 * ���ò����ַ���ת��ΪHashMap
	 * @param str_conf
	 * @param jepParse
	 * @return
	 */
	private HashMap convertConfStrToHashMap(String str_conf, JepFormulaParse jepParse) {
		str_conf = str_conf.trim(); //
		if (str_conf.endsWith(",")) { //
			str_conf = str_conf.substring(0, str_conf.length() - 1); //
		}
		String str_formula = "getParAsMap(" + str_conf + ")"; ////
		if (jepParse == null) {
			jepParse = JepFormulaParse.createUIJepParse(); //����������!!
		}
		HashMap confMap = (HashMap) jepParse.execFormula(str_formula); //ִ�й�ʽ,�õ���ϣ��!!!
		return confMap;
	}

	/**
	 * ����Ĭ�ϵ�
	 */
	public void setDefaultLookAndFeel(JComponent _compent) {
		try {
			cn.com.infostrategy.ui.mdata.hmui.I_LookAndFeel wltLookAndFeel = new cn.com.infostrategy.ui.mdata.hmui.I_LookAndFeel();
			javax.swing.UIManager.setLookAndFeel(wltLookAndFeel); //��������
			UIManager.put("ScrollBar.gradient", LookAndFeel.getScrollPanelGradient()); //��������֪ΪʲôҪ������һ��!!!��ǰ��Desktoppanel�����,����������Ŀ�з��ִ�EAC��¼ʱ������ûЧ��!���Ը�����!!
			javax.swing.SwingUtilities.updateComponentTreeUI(_compent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ���÷��...
	 * @param _strings 
	 */
	public void setWLTLookAndFeel(String[][] _strings, JComponent _compent) {
		try {
			cn.com.infostrategy.ui.common.LookAndFeel wltLookAndFeel = new cn.com.infostrategy.ui.mdata.hmui.I_LookAndFeel();
			if (_strings != null) {
				for (int i = 0; i < _strings.length; i++) {
					wltLookAndFeel.map_value.put(_strings[i][0], _strings[i][1]); //������ϣ��������ֵ
				}
				wltLookAndFeel.setLookAndFeelValues(false); //�������е�ֵ
			} else {
				wltLookAndFeel.setLookAndFeelValues(true); //�������е�ֵ
			}
			javax.swing.UIManager.setLookAndFeel(wltLookAndFeel); //��������
			UIManager.put("ScrollBar.gradient", LookAndFeel.getScrollPanelGradient()); //��������֪ΪʲôҪ������һ��!!!��ǰ��Desktoppanel�����,����������Ŀ�з��ִ�EAC��¼ʱ������ûЧ��!���Ը�����!!
			javax.swing.SwingUtilities.updateComponentTreeUI(_compent); ////
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * �����ͻ����߳�!!!���ͻ�����Ҫһ������ˢ�µ��߳�!!! ����鿴ϵͳ��ճ��! Զ�̵��ò鿴������ĳЩ��Ϣ��!
	 * ����һЩϵͳĬ�ϵ�ˢ�´���,Ӧ�û�������չ,��ע��һ��ˢ����!Ȼ�����������!!!
	 */
	public void startClientRefreshThread() {
		startTimer_1(); //����д�ļ�
		startTimer_2(); //���skip2���ļ�!!ʹ�ù���JVM�򿪶�Ӧ���ܵ�!!
		String str_cookieKey = System.getProperty("PushMonitorIEExit"); //
		if (str_cookieKey != null && !str_cookieKey.trim().equals("")) {
			String str_logintype = System.getProperty("REQ_logintype"); //��¼ģʽ!!
			if ("IELogin".equals(str_logintype) || "single".equals(str_logintype) || "skip".equals(str_logintype) || "skip2".equals(str_logintype)) { //ֻ����ʹ��IE��ʽ�����,��Ҫ�����!���ֱ��ͨ�������ݷ�ʽ��¼��,����������ڼ���IE�˳�������!!!
				tbUtil.reflectCallMethod("cn.com.infostrategy.ui.sysapp.login.click2.MonitorIECookieDialog", "createInstance", null); //����һ��ʵ��!!!MonitorIECookieDialog.createInstance()
			} else {
				tbUtil.refectCallClassStaticMethod("cn.com.infostrategy.ui.sysapp.login.click2.MonitorIECookieDialog", "setNotLoadReason", new Object[] { "����[IELogin/skip/skip2/single]����ģʽ�е�һ�ֵ�¼��ʽ,��������������ݵ�¼!" });
			}
		} else {
			tbUtil.refectCallClassStaticMethod("cn.com.infostrategy.ui.sysapp.login.click2.MonitorIECookieDialog", "setNotLoadReason", new Object[] { "û�ж������[PushMonitorIEExit]" });
		}
	}

	//д�ļ�,��֪
	private void startTimer_1() {
		if (!"skip2".equals(System.getProperty("REQ_logintype"))) { //�����skip2�ĵ�¼ģʽ���������!
			return; //
		}
		if (timer_1 != null) {
			return; //
		}
		timer_1 = new Timer(); //
		timer_1.schedule(new MyTimerTask(1), 0, 10000); //ÿ��10��дһ��,�ñ���֪���һ�����,��Ϊ��һ��ɱ�����˳���,��skip2��Զ�򲻿���,�����ֶ�ɾ���ļ�,���������,�����10���Ӻ���ֿ�����,��Ϊ����ǰ�ȿ�������ļ�����޸�ʱ���Ǽ���,Ȼ�󳬹�10������Ϊ������!!!
	}

	/**
	 * ˢ��skip2ģʽ���ļ�������Ӧ�Ĺ��ܵ�,����������Ŀ
	 */
	private void startTimer_2() {
		if (!"skip2".equals(System.getProperty("REQ_logintype"))) { //�����skip2�ĵ�¼ģʽ���������!
			return; //
		}
		if (timer_2 != null) {
			return; //
		}
		timer_2 = new Timer(); //
		timer_2.schedule(new MyTimerTask(2), 0, 500); //ÿ��0.5��ˢ��һ��!!!
	}

	/**
	 * ��д���ű�ǵ��ļ�!!
	 * ������ֻд����!!
	 */
	private void writeAliveMarkFile() {
		try {
			String str_filename = System.getProperty("user.home") + "\\PushSkip2Alive.txt"; //�ı��ļ�
			File file = new File(str_filename); //
			String str_currtime = sdf_curr2.format(new Date(System.currentTimeMillis())); //��ǰʱ��!!!
			if (!file.exists()) { //����ļ�������!�򴴽�!!
				file.createNewFile(); //
				file.deleteOnExit(); //�˳�ʱ�ر�!!
			}
			PrintWriter pf = new PrintWriter(new FileOutputStream(file, false)); //
			pf.println(str_currtime); //д�ļ�
			pf.flush(); //
			pf.close(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ���skip2��¼ģʽ�е��ļ�!
	 * ������ֻ����д!!!
	 */
	private void monitorSkip2Menu() {
		try {
			String str_filename = System.getProperty("user.home") + "\\PushSkip2Menu.txt"; //�ı��ļ�
			File file = new File(str_filename); //
			if (file.exists()) { //����ļ�����,������ļ�����,����������û�м��ص�ҳ��!!!
				ArrayList al_menuid = new ArrayList(); //
				RandomAccessFile raFile = new RandomAccessFile(file, "r"); //ֻ����ʽ��д!
				String str_linetext = null; //
				while ((str_linetext = raFile.readLine()) != null) { //���������!
					al_menuid.add(str_linetext); //�ȶ�����!!
				}
				raFile.close(); //�ر��ļ���!!!
				String str_alCount = System.getProperty("PushSkip2Count"); //ȡϵͳ����!!!
				if (str_alCount == null) {
					str_alCount = "1"; //��ʼ��һ��!
				}
				int li_alCount = Integer.parseInt(str_alCount); //�Ѽ����˼���!��������������5��!
				if (al_menuid.size() > li_alCount) { //������������Ĺ��ܵ�!
					for (int i = li_alCount; i <= al_menuid.size() - 1; i++) { //��������������!!!
						String str_menuid = (String) al_menuid.get(i); //�˵�id
						System.out.println("Skip2��¼ģʽ�����̷߳�����Ҫ׷�Ӵ򿪵Ĺ��ܵ�[" + str_menuid + "],��֮..."); //
						DeskTopPanel.deskTopPanel.openAppMainFrameWindowByIdAsSplash(str_menuid); //ʹ�õȴ������
					}
					System.setProperty("PushSkip2Count", "" + al_menuid.size()); //�´δ��ĸ���ʼд��ϵͳ����!!!
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private SysAppServiceIfc getSysAppService() {
		try {
			return (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null; //
		}
	}

	class MyTimerTask extends TimerTask {
		private int type = 0; //

		public MyTimerTask(int _type) {
			type = _type;
		}

		@Override
		public void run() {
			if (type == 1) {
				writeAliveMarkFile(); //����д����һ����ŵ��ļ�!
			} else if (type == 2) {
				monitorSkip2Menu(); //����´����ļ�,Ȼ���!
			}
		}

	}
}
