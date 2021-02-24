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
 * 因为后来中铁建需要可以单独打开一个功能点! 即在两个地方需要调用根据一个菜单数据打开一个功能点! 所以搞了该工具类!!然后主界面中点击是调这里!!单独打开功能点也是调这里!!! 
 * @author xch
 */
public class LoginUtil {

	private TBUtil tbUtil = new TBUtil(); //
	private Timer timer_1, timer_2 = null; //静态变量!!!
	private SimpleDateFormat sdf_curr2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE); //

	/**
	 * 根据菜单id取得该菜单面板!!!
	 * @param _menuid
	 * @return
	 * @throws Exception
	 */
	public AbstractWorkPanel getWorkPanelByMenuVO(String _menuid) throws Exception {
		if (_menuid == null || _menuid.trim().equals("")) {
			throw new Exception("菜单id为空!"); //
		}
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_menu where id='" + _menuid + "'"); //
		if (hvs == null || hvs.length == 0) {
			throw new Exception("没有找到id=[" + _menuid + "]的菜单!!!"); //
		}
		return getWorkPanelByMenuVO(hvs[0]); //
	}

	//根据菜单VO取得
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
			throw new Exception("打开结点[" + str_menuname + "]失败(因为路径配置为空),CommandType[" + str_commandtype + "],Command[" + str_command + "]"); //
		} else if (str_commandtype.equals("99")) { // 直接Frame,在command中配置类路径
			if (str_command.startsWith("http://")) {
				//自动重置url
				WLTTabbedPane tabpanel = DeskTopPanel.getDeskTopPanel().getWorkTabbedPanel();
				Point pt = tabpanel.getLocationOnScreen();
				StringBuffer urlsb = new StringBuffer();
				String[] str1 = tbUtil.split(str_command.trim(), "?"); //问号前后
				urlsb.append(str1[0] + "?");
				HashMap parm = TBUtil.getTBUtil().convertStrToMapByExpress(str1[1], "&", "=", true); //&参数
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
		} else if (str_commandtype.equals("00")) { // 自定义工作面板!!
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
		} else if (str_commandtype.equalsIgnoreCase("11")) { //XML注册的系统功能!!!
			String str_cmd = str_command.trim(); //
			String str_menuName = str_cmd.substring(0, str_cmd.indexOf(";")); //菜单名!!
			String str_xmlFile = str_cmd.substring(str_cmd.indexOf(";") + 1, str_cmd.length()); //xml文件!!
			String[] str_realcommdnad = getSysAppService().getOneRegMenuCommand(str_xmlFile, str_menuName); //

			if (str_realcommdnad == null || str_realcommdnad.length == 0) {
				throw new Exception("菜单类型是XML注册功能点，XML中路径已改变，请重新配置!");
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
				workItemPanel = (AbstractWorkPanel) Class.forName(str_realcommdnad[0]).newInstance(); //创建实例!!!
			}
			if (ClientEnvironment.isAdmin()) { //如果是管理员登陆，那么把注册功能实际调用的路径存入缓存中。便于开发者查看！
				workItemPanel.putClientProperty("$realpath", str_realcommdnad[0]);
			}
			confStr = str_realcommdnad[1];
		} else if (str_commandtype.equalsIgnoreCase("0A")) { //Format模板,是零A,不是哦A,在宋体显示时两者一样,很容易出问题!! 
			workItemPanel = (AbstractWorkPanel) Class.forName("cn.com.infostrategy.ui.mdata.styletemplet.format.DefaultStyleWorkPanel_0A").newInstance();
		} else if (str_commandtype.equalsIgnoreCase("ST")) { // 风格模板!!!
			if (str_command == null || str_command.trim().equals("")) {
				throw new Exception("菜单类型是风格模板,但没有定义公式!!"); //
			}
			str_command = str_command.trim(); //
			if (str_command.endsWith(",")) { //
				str_command = str_command.substring(0, str_command.length() - 1); //
			}
			if (str_command.contains("setStyleType(")) { //如果用了风格模板，兼容产品前的系统 haoming
				StringBuffer sb = new StringBuffer();
				String str[] = str_command.split(";");
				for (int i = 0; i < str.length; i++) {
					if (str[i].contains("setStyleType(")) {
						sb.append("\"风格模板类型\",");
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
			jepParse = JepFormulaParse.createUIJepParse(); //创建解释器!!
			HashMap confMap = (HashMap) jepParse.execFormula(str_formula); //执行公式,得到哈希表!!!
			String str_styleType = (String) confMap.get("风格模板类型"); //
			if (str_styleType == null) {
				throw new Exception("没有定义关键参数[风格模板类型]!");
			}

			StyleTempletDefineBuilder stdb = new StyleTempletDefineBuilder(); //
			String str_defaultClassName = stdb.getDefaultClassName(str_styleType); //
			if (str_defaultClassName == null) {
				throw new Exception("未知的风格模板类型[" + str_styleType + "]!"); //
			}
			workItemPanel = (AbstractWorkPanel) Class.forName(str_defaultClassName).newInstance(); // 反射动态创建风格模板的默认实现类
			workItemPanel.addMenuConfMap(confMap); //加入参数!!
		}

		//加入菜单自定义的参数!!!
		String str_conf = _hvo.getStringValue("conf"); //参数!!

		if (str_conf != null && !str_conf.trim().equals("")) {
			HashMap confMap = convertConfStrToHashMap(str_conf, jepParse);
			workItemPanel.addMenuConfMap(confMap); //加入自定义参数!!
		} else {//只有当自己参数为NULL时，才试图调用引用菜单的参数
			//if ((workItemPanel instanceof ParameterCreatedWKPanelIfc) && confStr != null && !confStr.trim().equals("")) { //
			if (confStr != null && !confStr.trim().equals("")) { //岳耀彪以前加了个判断,(workItemPanel instanceof ParameterCreatedWKPanelIfc),即定了个接口,但没有任何方法,不知何意???也没将为什么这么做的初衷在备注中写清楚!!!先注销,等下周问他!!
				//如果仅仅是为了判断一个对象是否是某种类型,则没必须使用一个接口来做,在基类中增加一个方法,然后在子类中重构这个方法,然后使用这个方法判断!! 或者直接使用putClientProperty(key, value),置入特定标记值,然后在这里取出来判断!!其实还有其他许多办法,总之没有必须单独搞一个接口来进行这种判断!!!
				HashMap confMap = convertConfStrToHashMap(confStr, jepParse);
				workItemPanel.addMenuConfMap(confMap); //加入引用菜单的参数!!
			}
		}

		workItemPanel.setSelectedMenuVOs(_hvo); // 选中的菜单树!!
		workItemPanel.setLayout(new BorderLayout()); //
		return workItemPanel; //
	}

	/**
	 * 配置参数字符串转换为HashMap
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
			jepParse = JepFormulaParse.createUIJepParse(); //创建解释器!!
		}
		HashMap confMap = (HashMap) jepParse.execFormula(str_formula); //执行公式,得到哈希表!!!
		return confMap;
	}

	/**
	 * 设置默认的
	 */
	public void setDefaultLookAndFeel(JComponent _compent) {
		try {
			cn.com.infostrategy.ui.mdata.hmui.I_LookAndFeel wltLookAndFeel = new cn.com.infostrategy.ui.mdata.hmui.I_LookAndFeel();
			javax.swing.UIManager.setLookAndFeel(wltLookAndFeel); //真正设置
			UIManager.put("ScrollBar.gradient", LookAndFeel.getScrollPanelGradient()); //滚动条不知为什么要单独设一下!!!以前在Desktoppanel中设的,但在中铁项目中发现从EAC登录时滚动条没效果!所以改在这!!
			javax.swing.SwingUtilities.updateComponentTreeUI(_compent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 设置风格...
	 * @param _strings 
	 */
	public void setWLTLookAndFeel(String[][] _strings, JComponent _compent) {
		try {
			cn.com.infostrategy.ui.common.LookAndFeel wltLookAndFeel = new cn.com.infostrategy.ui.mdata.hmui.I_LookAndFeel();
			if (_strings != null) {
				for (int i = 0; i < _strings.length; i++) {
					wltLookAndFeel.map_value.put(_strings[i][0], _strings[i][1]); //先往哈希表中送满值
				}
				wltLookAndFeel.setLookAndFeelValues(false); //设置所有的值
			} else {
				wltLookAndFeel.setLookAndFeelValues(true); //设置所有的值
			}
			javax.swing.UIManager.setLookAndFeel(wltLookAndFeel); //真正设置
			UIManager.put("ScrollBar.gradient", LookAndFeel.getScrollPanelGradient()); //滚动条不知为什么要单独设一下!!!以前在Desktoppanel中设的,但在中铁项目中发现从EAC登录时滚动条没效果!所以改在这!!
			javax.swing.SwingUtilities.updateComponentTreeUI(_compent); ////
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 启动客户端线程!!!即客户端需要一个不断刷新的线程!!! 比如查看系统剪粘板! 远程调用查看服务器某些信息等!
	 * 除了一些系统默认的刷新处理,应该还可以扩展,即注册一个刷新类!然后这里面调用!!!
	 */
	public void startClientRefreshThread() {
		startTimer_1(); //不断写文件
		startTimer_2(); //检查skip2的文件!!使用共享JVM打开对应功能点!!
		String str_cookieKey = System.getProperty("PushMonitorIEExit"); //
		if (str_cookieKey != null && !str_cookieKey.trim().equals("")) {
			String str_logintype = System.getProperty("REQ_logintype"); //登录模式!!
			if ("IELogin".equals(str_logintype) || "single".equals(str_logintype) || "skip".equals(str_logintype) || "skip2".equals(str_logintype)) { //只有在使用IE方式进入的,才要有这个!如果直接通过桌面快捷方式登录的,则根本不存在监听IE退出的问题!!!
				tbUtil.reflectCallMethod("cn.com.infostrategy.ui.sysapp.login.click2.MonitorIECookieDialog", "createInstance", null); //创建一个实例!!!MonitorIECookieDialog.createInstance()
			} else {
				tbUtil.refectCallClassStaticMethod("cn.com.infostrategy.ui.sysapp.login.click2.MonitorIECookieDialog", "setNotLoadReason", new Object[] { "不是[IELogin/skip/skip2/single]四种模式中的一种登录方式,比如可能是桌面快捷登录!" });
			}
		} else {
			tbUtil.refectCallClassStaticMethod("cn.com.infostrategy.ui.sysapp.login.click2.MonitorIECookieDialog", "setNotLoadReason", new Object[] { "没有定义参数[PushMonitorIEExit]" });
		}
	}

	//写文件,告知
	private void startTimer_1() {
		if (!"skip2".equals(System.getProperty("REQ_logintype"))) { //如果是skip2的登录模式才启动这个!
			return; //
		}
		if (timer_1 != null) {
			return; //
		}
		timer_1 = new Timer(); //
		timer_1.schedule(new MyTimerTask(1), 0, 10000); //每隔10秒写一次,让别人知道我还活着,因为万一是杀进程退出的,则skip2永远打不开了,除非手动删除文件,有这个机制,则可以10秒钟后就又可以了,因为调用前先看下这个文件最后修改时间是几点,然后超过10秒则认为是死了!!!
	}

	/**
	 * 刷新skip2模式的文件启动对应的功能点,在中铁建项目
	 */
	private void startTimer_2() {
		if (!"skip2".equals(System.getProperty("REQ_logintype"))) { //如果是skip2的登录模式才启动这个!
			return; //
		}
		if (timer_2 != null) {
			return; //
		}
		timer_2 = new Timer(); //
		timer_2.schedule(new MyTimerTask(2), 0, 500); //每隔0.5秒刷新一次!!!
	}

	/**
	 * 回写活着标记的文件!!
	 * 这里是只写不读!!
	 */
	private void writeAliveMarkFile() {
		try {
			String str_filename = System.getProperty("user.home") + "\\PushSkip2Alive.txt"; //文本文件
			File file = new File(str_filename); //
			String str_currtime = sdf_curr2.format(new Date(System.currentTimeMillis())); //当前时间!!!
			if (!file.exists()) { //如果文件不存在!则创建!!
				file.createNewFile(); //
				file.deleteOnExit(); //退出时关闭!!
			}
			PrintWriter pf = new PrintWriter(new FileOutputStream(file, false)); //
			pf.println(str_currtime); //写文件
			pf.flush(); //
			pf.close(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 监控skip2登录模式中的文件!
	 * 这里是只读不写!!!
	 */
	private void monitorSkip2Menu() {
		try {
			String str_filename = System.getProperty("user.home") + "\\PushSkip2Menu.txt"; //文本文件
			File file = new File(str_filename); //
			if (file.exists()) { //如果文件存在,则加载文件内容,并加载其中没有加载的页面!!!
				ArrayList al_menuid = new ArrayList(); //
				RandomAccessFile raFile = new RandomAccessFile(file, "r"); //只读方式读写!
				String str_linetext = null; //
				while ((str_linetext = raFile.readLine()) != null) { //如果有数据!
					al_menuid.add(str_linetext); //先读进来!!
				}
				raFile.close(); //关闭文件流!!!
				String str_alCount = System.getProperty("PushSkip2Count"); //取系统属性!!!
				if (str_alCount == null) {
					str_alCount = "1"; //开始有一个!
				}
				int li_alCount = Integer.parseInt(str_alCount); //已加载了几个!比如曾经加载了5个!
				if (al_menuid.size() > li_alCount) { //如果发现新增的功能点!
					for (int i = li_alCount; i <= al_menuid.size() - 1; i++) { //遍历各个新增的!!!
						String str_menuid = (String) al_menuid.get(i); //菜单id
						System.out.println("Skip2登录模式监听线程发现需要追加打开的功能点[" + str_menuid + "],打开之..."); //
						DeskTopPanel.deskTopPanel.openAppMainFrameWindowByIdAsSplash(str_menuid); //使用等待框加载
					}
					System.setProperty("PushSkip2Count", "" + al_menuid.size()); //下次从哪个开始写入系统属性!!!
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
				writeAliveMarkFile(); //不断写标记我还活着的文件!
			} else if (type == 2) {
				monitorSkip2Menu(); //监控新窗口文件,然后打开!
			}
		}

	}
}
