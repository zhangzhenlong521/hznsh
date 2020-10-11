/**************************************************************************
 * $RCSfile: UIUtil.java,v $  $Revision: 1.41 $  $Date: 2013/02/22 02:46:54 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.jdesktop.jdic.browser.WebBrowser;
import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.SQLBuilderIfc;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.common.WebCallParVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

/**
 * 客户端工具类test
 * 
 * @author xch
 * 
 */
public class UIUtil implements Serializable {

	private static final long serialVersionUID = -1940900464803858712L;

	private static Hashtable ht_loginLanguage = new Hashtable(); //
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
	private static Date currDate = new Date(); //

	static {
		ht_loginLanguage.put("我的快捷方式", "My Favorites");
		ht_loginLanguage.put("所有权限", "Process");
		ht_loginLanguage.put("系统支持", "System Setup");
		ht_loginLanguage.put("平台信息", "System Index");
		ht_loginLanguage.put("查看客户端所有环境变量", "Client Environment");
		ht_loginLanguage.put("查看服务器端所有环境变量", "Server Enironment");
		ht_loginLanguage.put("修改主界面风格", "Operation Style Configuration");
		ht_loginLanguage.put("修改密码", "Modify Password");
		ht_loginLanguage.put("查询分析器", "SQL Query Tools");
		ht_loginLanguage.put("查看服务器端日志", "Server Log");
		ht_loginLanguage.put("查看服务器端屏幕", "Server Screen");
		ht_loginLanguage.put("收缩所有功能点", "Collapse All Node");
		ht_loginLanguage.put("展开所有功能点", "Expand All Nodes");
		ht_loginLanguage.put("数据字典管理", "Data Dictionary");
		ht_loginLanguage.put("元原模板管理", "Meta Data");
		ht_loginLanguage.put("菜单管理", "Menu Definition");
		ht_loginLanguage.put("下拉框字典管理", "Dropdown Options");
		ht_loginLanguage.put("查询模板管理", "Query Templet");
		ht_loginLanguage.put("导出平台数据", "Export PlatData");
		ht_loginLanguage.put("工作流编辑", "WorkFlow Edit");
		ht_loginLanguage.put("系统公告栏", "System Bulletin");
		ht_loginLanguage.put("公告栏管理", "Bulletin Information");
		ht_loginLanguage.put("加入快捷方式", "Add to My Favorites");
		ht_loginLanguage.put("登录用户:", "Login User:");
		ht_loginLanguage.put("打开结点失败", "Open frame fail");
		ht_loginLanguage.put("加载页面", "Load Frame");
		ht_loginLanguage.put("成功", "Success");
		ht_loginLanguage.put("耗时", "Used");
		ht_loginLanguage.put("加载页面", "Load Frame");
		ht_loginLanguage.put("失败", "Processing faild.");
		ht_loginLanguage.put("原因", "Reason");
		ht_loginLanguage.put("当前位置", "Function Point");
		ht_loginLanguage.put("增加", "Add");
		ht_loginLanguage.put("快捷方式", "Shotcut");
		ht_loginLanguage.put("删除", "Delete");
		ht_loginLanguage.put("关闭自己", "Close");
		ht_loginLanguage.put("关闭其他", "Close Others");
		ht_loginLanguage.put("关闭所有", "Close All");
		ht_loginLanguage.put("您真的想退出系统吗?", "Will exit system, confrim?");
		ht_loginLanguage.put("提示", "Prompt");
	}

	public static WLTRemoteCallServiceIfc lookUpRemoteService(Class _class) throws WLTRemoteException, Exception {
		WLTRemoteCallServiceIfc service = (WLTRemoteCallServiceIfc) RemoteServiceFactory.getInstance().lookUpService(_class);
		return service;
	}

	public static WLTRemoteCallServiceIfc lookUpRemoteService(Class _class, int _readTimeOut) throws WLTRemoteException, Exception {
		WLTRemoteCallServiceIfc service = (WLTRemoteCallServiceIfc) RemoteServiceFactory.getInstance().lookUpService(_class, _readTimeOut);
		return service;
	}

	public static WLTRemoteCallServiceIfc lookUpRemoteService(String _otherIPAndPort, Class _class) throws WLTRemoteException, Exception {
		WLTRemoteCallServiceIfc service = (WLTRemoteCallServiceIfc) RemoteServiceFactory.getInstance().lookUpService(_class, _otherIPAndPort);
		return service;
	}

	public static FrameWorkCommServiceIfc getCommonService() throws WLTRemoteException, Exception {
		// FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc)
		// RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class);
		return (FrameWorkCommServiceIfc) lookUpRemoteService(FrameWorkCommServiceIfc.class);
	}
	public static WLTRemoteCallServiceIfc lookUpRemoteService(Class<?> _class, String implClassName) throws WLTRemoteException, Exception {
		WLTRemoteCallServiceIfc service = (WLTRemoteCallServiceIfc) RemoteServiceFactory.getInstance().lookUpService(_class, implClassName);
		return service;
	}

	/**
	 * 取得另外一台机器上的服务!!
	 * @param _otherIPAndPort 是格式[192.168.0.10:9016]的样子
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public static FrameWorkCommServiceIfc getCommonService(String _otherIPAndPort) throws WLTRemoteException, Exception {
		return (FrameWorkCommServiceIfc) lookUpRemoteService(_otherIPAndPort, FrameWorkCommServiceIfc.class);
	}

	public static FrameWorkMetaDataServiceIfc getMetaDataService() throws WLTRemoteException, Exception {
		return (FrameWorkMetaDataServiceIfc) lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
	}

	/**
	 * 通用方法..
	 * 
	 * @param _className
	 * @param _functionName
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public synchronized static HashMap commMethod(String _className, String _functionName, HashMap _parMap) throws Exception {
		return getCommonService().commMethod(_className, _functionName, _parMap);
	}

	/**
	 * 调另一台机器上的一个服务!!比如本服务器是WebSphere,但某个特殊应用可能会调到另一个服务器上的!比如招行系统中的接口,死活就是Tomcat行但WebSphere不行!!!
	 * 另外集群应用也需要该方法!!
	 * @param _otherIPAndPort
	 * @param _className
	 * @param _functionName
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public synchronized static HashMap commMethod(String _otherIPAndPort, String _className, String _functionName, HashMap _parMap) throws Exception {
		return getCommonService(_otherIPAndPort).commMethod(_className, _functionName, _parMap);
	}

	/**
	 * 取得登录人员所属机构类型的条件
	 * @return
	 */
	public synchronized static String getLoginUserBLDeptCondition() {
		String str_filterCondition = null;
		CurrLoginUserVO loginUserVO = ClientEnvironment.getCurrLoginUserVO(); //取得登录人员信息!
		String str_loginuser_depttype = loginUserVO.getBlDept_corptype(); //先取得登录人员所属的机构类型!!
		if (str_loginuser_depttype != null) {
			if (str_loginuser_depttype.equals("总行")) { //如果是总行的人,则不做任何权限过滤
			} else { //如果是以"总行部门"开头.
				if (1 == 1) { //如果是管理员,后来又觉得太麻烦了,发现搞权限是假,有时权限放开才是,因为当你真正实现了权限后客户又觉得不爽了! loginUserVO.isCorpDeptAdmin()
					if (str_loginuser_depttype.indexOf("总行") == 0) { //如果是总行的
					} else if (str_loginuser_depttype.indexOf("事业部") == 0) { //如果是事业部
						String str_bl_shiyb = loginUserVO.getBlDept_bl_shiyb(); //登录人员所属事业部
						str_filterCondition = "bl_shiyb='" + str_bl_shiyb + "'"; //找出所属事业部等于本人的所属事业部
					} else if (str_loginuser_depttype.indexOf("分行") == 0) { //如果是分行的人
						String str_bl_fengh = loginUserVO.getBlDept_bl_fengh(); //登录人员所属分行
						str_filterCondition = "bl_fengh='" + str_bl_fengh + "'"; //找出所属分行等于本人的所属分行
					} else if (str_loginuser_depttype.indexOf("支行") == 0) { //支行的人
						String str_bl_zhih = loginUserVO.getBlDept_bl_zhih(); //登录人员所属支行
						str_filterCondition = "bl_zhih='" + str_bl_zhih + "'"; //找出所属支行等于本人的所属支行
					}
				} //
				//else { //如果不是管理员,则各有各的讲究...
				//				if (str_loginuser_depttype.equals("总行部门") || str_loginuser_depttype.equals("总行部门_下属机构")) { //
				//					String str_bl_zhbm = loginUserVO.getBlDept_bl_zhonghbm(); //登录人员所属总行部门
				//					str_filterCondition = "bl_zhonghbm='" + str_bl_zhbm + "'"; //找出所属总行部门等于本人的所属总行部门的人员
				//				} else if (str_loginuser_depttype.equals("事业部") || str_loginuser_depttype.equals("事业部_下属机构")) { //
				//					String str_bl_shiyb = loginUserVO.getBlDept_bl_shiyb(); //登录人员所属事业部
				//					str_filterCondition = "bl_shiyb='" + str_bl_shiyb + "'"; //找出所属事业部等于本人的所属事业部
				//				} else if (str_loginuser_depttype.equals("事业部分部") || str_loginuser_depttype.equals("事业部分部_下属机构")) { //
				//					String str_bl_shiybfb = loginUserVO.getBlDept_bl_shiybfb(); //登录人员所属事业部分部
				//					str_filterCondition = "bl_shiybfb='" + str_bl_shiybfb + "'"; //找出所属事业部分部等于本人的所属事业部分部
				//				} else if (str_loginuser_depttype.equals("分行")) { //
				//					String str_bl_fengh = loginUserVO.getBlDept_bl_fengh(); //登录人员所属分行
				//					str_filterCondition = "bl_fengh='" + str_bl_fengh + "'"; //找出所属分行等于本人的所属分行
				//				} else if (str_loginuser_depttype.equals("分行部门") || str_loginuser_depttype.equals("分行部门_下属机构")) { //
				//					String str_bl_fenghbm = loginUserVO.getBlDept_bl_fenghbm(); //登录人员所属分行部门
				//					str_filterCondition = "bl_fenghbm='" + str_bl_fenghbm + "'"; //找出所属分行部门等于本人的所属分行部门
				//				} else if (str_loginuser_depttype.equals("支行") || str_loginuser_depttype.equals("支行_下属机构")) { //
				//					String str_bl_zhih = loginUserVO.getBlDept_bl_zhih(); //登录人员所属分行部门
				//					str_filterCondition = "bl_zhih='" + str_bl_zhih + "'"; //找出所属分行部门等于本人的所属分行部门
				//				}
				//}
			}
		}
		return str_filterCondition; //
	}

	/**
	 * 取得登录人员所有机构的权限!!,应该与数据权限对应起来!!
	 * 即数据权限模块应该提供一个功能,送入一个参数,指定一个资源类型(比如机构),指定一个表名/视图名(比如pub_corp_dept),指定一个宏代码串,然后就能返回一组ID[]!!!
	 * @return
	 */
	public synchronized static String[] getLoginUserBLDeptIDs() { //
		try {
			String str_condition = getLoginUserBLDeptCondition(); //
			if (str_condition != null) {
				String[][] str_data = getStringArrayByDS(null, "select id from pub_corp_dept where " + str_condition); //
				if (str_data != null) {
					String[] str_alldeptids = new String[str_data.length]; //
					for (int i = 0; i < str_alldeptids.length; i++) {
						str_alldeptids[i] = str_data[i][0]; //
					}
					return str_alldeptids; //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	//取得登录人员的上级机构中的某种指定类型的机构,并返回其中的指定字段的值!!
	public synchronized static String getLoginUserParentCorpItemValueByType(String _corpType, String _nvlCorpType, String _itemName) throws Exception {
		cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc sysService = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class);
		return sysService.getLoginUserParentCorpItemValueByType(_corpType, _nvlCorpType, _itemName);
	}

	/**
	 * 根据下拉字典中定的机构类型中的宏代码,找出登录人员/某个人员/某个机构,的【$本部门】【$本机构】
	 * 这个类在服务器端对应的是 new sysAppDMO().getParentCorpVOByMacro()...
	 * @param _type,有三个取值,1,2,3，其中1表示就是登录人员,2表示某个其他人员(第二个参数就是人员id)，3表示是某个实际机构(第二个参数就是机构id)
	 * @param _consValue 根据第一个参数的类型,分别表达不同的含义!!
	 * @param _macroName,宏代码，比如【$本部门】【$本机构】【$本一级分行】【$本二级分行】,这必须在下拉字典中定义,如果为空,则就是把所有父亲机构都取出来！
	 * @return
	 * @throws Exception
	 */
	public synchronized static HashVO[] getParentCorpVOByMacro(int _type, String _consValue, String _macroName) throws Exception {
		cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc sysService = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class);
		return sysService.getParentCorpVOByMacro(_type, _consValue, _macroName); //
	}

	public synchronized static String getLanguage(String _key) {
		return ClientLanguageCache.getInstance().getLanguage(_key);
	}

	public synchronized static String getLoginLanguage(String _key) {
		if (ClientEnvironment.getInstance().isEngligh()) { // 如果是英文环境,则要转换
			String str_language = (String) ht_loginLanguage.get(_key); //
			return str_language == null ? ("." + _key) : str_language; //
		} else {
			return _key; //
		}
	}

	/**
	 * 取得服务器端的当前日期,返回"2008-08-20"的样子
	 * @return
	 */
	public synchronized static String getServerCurrDate() throws Exception {
		return getCommonService().getServerCurrDate(); //
	}

	/**
	 * 取得服务器端参数,
	 * @param _module
	 * @param _key
	 * @return
	 * @throws Exception
	 */
	//参数设置不对，本意是用module和key联合取得value,但实际上module没用,则比较逻辑发生错误。
	//根据和徐老师讨论，决定暂时删除本方法
	//	public synchronized static String getSysOptionValue(String _module, String _key) {
	//		try {
	//			String[][] str_options = ClientEnvironment.getClientSysOptions(); //
	//			if (str_options == null) {
	//				str_options = getCommonService().getServerSysOptions(); //去服务器端取
	//				ClientEnvironment.setClientSysOptions(str_options); //加入客户端缓存
	//			}
	//
	//			for (int i = 0; i < str_options.length; i++) {
	//              str_options二维数组实际存放的[i][0]是key值，而[i][1]是value值，并没有module值，则if条件永远不会为true
	//				if (str_options[i][0].equalsIgnoreCase(_module) && str_options[i][1].equalsIgnoreCase(_key)) {
	//					return str_options[i][2]; //
	//				}
	//			}
	//
	//			return ""; //如果找不到,则直接""
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//			return "";
	//		}
	//	}
	public synchronized static long getServerCurrTimeLongValue() throws Exception {
		return getCommonService().getServerCurrTimeLongValue(); //
	}

	/**
	 * 取得服务器端的当前时间,返回"2008-08-20 10:23:15"的样子
	 * @return
	 */
	public synchronized static String getServerCurrTime() throws Exception {
		return getCommonService().getServerCurrTime(); //
	}

	public synchronized static ImageIcon getUploadImae(String _fileName) {
		try {
			String str_imgurl = System.getProperty("CALLURL") + "/upload/" + _fileName;
			// String str_newUrl = URLEncoder.encode(str_imgurl, "GBK");
			// System.out.println(str_imgurl);
			// System.out.println(str_newUrl);
			return new ImageIcon(new URL(str_imgurl)); //
		} catch (Exception ex) {
			System.out.println("\r\n取图片[" + _fileName + "]失败!!");//
			// ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 从服务器端的WebRoot目录下取文件! 比如[/applet/login_crcc.gif]
	 * @param _fileName
	 * @return
	 */
	public synchronized static ImageIcon getImageFromServer(String _fileName) {//https时此处有问题/sunfujun/20130222
		try {
			String str_imgurl = System.getProperty("CALLURL") + _fileName;
			long ll_1 = System.currentTimeMillis(); //
			System.out.println("[" + getCurrTime(true) + "]开始下载图片[" + str_imgurl + "]..." + System.getProperty("transpro")); //
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
				HttpsURLConnection conn = (HttpsURLConnection) new URL(str_imgurl).openConnection();
				conn.setSSLSocketFactory(sc.getSocketFactory());
				conn.setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}
				});
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setUseCaches(false);
				conn.setConnectTimeout(5000);//张珍龙改为了0，也不起作用，故改回原代码【李春娟/2018-06-25】
				conn.setReadTimeout(5000);
				conn.setRequestProperty("Content-type", "text/html"); //
				InputStream intStream = conn.getInputStream(); //
				ByteArrayOutputStream baos = null;
				try {
					baos = new ByteArrayOutputStream();
					byte[] buf = new byte[2048]; //读的时候要快!即比服务器端吐的要快一点才好,否则如果是服务器端吐的快而客户端抽的慢,则可能出现阻塞与并发数上升的情况!!
					int len = -1;
					while ((len = intStream.read(buf)) != -1) {
						baos.write(buf, 0, len);
					}
					byte[] byteCodes = baos.toByteArray();
					ImageIcon icon = new ImageIcon(byteCodes); //
					return icon;
				} catch (Exception ex) {
					ex.printStackTrace(); //
					return null;
				} finally {
					try {
						intStream.close(); //
						baos.close(); //
					} catch (Exception exx) {
						exx.printStackTrace(); //
					}
				}
			} else {
				ImageIcon icon = new ImageIcon(new URL(str_imgurl)); //
				long ll_2 = System.currentTimeMillis(); //
				if (icon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) {
					System.out.println("\r\n取图片[" + _fileName + "]失败!!");//
					return null; //
				} else {
					System.out.println("[" + getCurrTime(true) + "]结束下载图片[" + str_imgurl + "],共耗时[" + (ll_2 - ll_1) + "]"); //
				}
				return icon; //
			}
		} catch (Exception ex) {
			System.out.println("\r\n取图片[" + _fileName + "]失败!!");//
			return null;
		}
	}

	//从服务器端的资源路径中取图片,即ClassPath中取!! 比如/com/pushworld/ipushgrc/bs/newindex.jpg
	public synchronized static ImageIcon getImageFromServerRespath(String _imgPathName) {
		try {
			ImageIcon icon = getCommonService().getImageFromServerRespath(_imgPathName); //
			return icon; //
		} catch (Exception ex) {
			System.out.println("\r\n取图片[" + _imgPathName + "]失败!!");//
			// ex.printStackTrace();
			return null;
		}

	}

	/**
	 * 取得一个图片
	 * 
	 * @param _fileName
	 * @return
	 */
	public synchronized static ImageIcon getImage(String _fileName) {
		if (_fileName == null) {
			return null;
		}
		_fileName = _fileName.toLowerCase(); //
		try {
			ImageIcon icon = new ImageIcon(UIUtil.class.getResource("/cn/com/weblight/images/" + _fileName));
			icon.setDescription(_fileName); //设置说明!
			return icon; //
		} catch (Exception ex) {
			System.err.println("\r\n取图片[" + _fileName + "]失败!");//
			ImageIcon icon = new ImageIcon(UIUtil.class.getResource("/cn/com/weblight/images/" + "office_001.gif"));
			return icon; //
		}
	}

	/**
	 * 调用一个新的线程去修改控件.
	 * @param _listener
	 */
	public synchronized static void invokeNewThreadUpdateUI(ActionListener _listener) {
		javax.swing.Timer timer = new javax.swing.Timer(0, _listener); //
		timer.setRepeats(false); //
		timer.start(); //
		//timer.stop(); //
	}

	public synchronized static String getCurrDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str_date = sdf.format(new Date());
		return str_date;
	}

	public synchronized static String getCurrTime() {
		return getCurrTime(false);
	}

	//可以精确到毫秒!!
	public synchronized static String getCurrTime(boolean _isMill) {
		if (_isMill) { //如果是精确到毫秒!!
			currDate.setTime(System.currentTimeMillis());
			return sdf2.format(currDate); //
		} else {
			currDate.setTime(System.currentTimeMillis()); //
			return sdf.format(currDate); //
		}
	}

	/**
	 * 获得服务器端所有的图标文件的文件名
	 * 
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public synchronized static String[] getImageFileNames() throws WLTRemoteException, Exception { //
		String[] str_imgNames = getCommonService().getImageFileNames(); //
		System.out.println("图片个数=[" + str_imgNames.length + "]"); //
		if (str_imgNames != null) {
			return str_imgNames; //返回图片
		} else {
			String[] str_aa = new String[] { "1.jpg", "10.jpg", "11.jpg", "12.JPG", "13.JPG", "14.jpg", "15.JPG", "16.JPG", "17.JPG", "18.jpg", "2.JPG", "3.JPG", "4.JPG", "5.jpg", "6.JPG", "7.JPG", "8.jpg", "9.jpg", "active.gif", "add.jpg", "align_center.gif", "align_left.gif", "align_right.gif", "archive.gif", "background.gif", "biggif_1.gif", "biggif_2.gif", "biggif_3.gif", "biggif_4.gif",
					"biggif_5.gif", "biggif_6.gif", "bigtextarea.gif", "blank.gif", "cascade.gif", "clear.gif", "clock.gif", "closeallwindow.gif", "closewin.gif", "closewindow.gif", "colorchoose.gif", "config.gif", "copy.gif", "date.gif", "del.gif", "delete.gif", "down.gif", "down1.jpg", "end.gif", "error.gif", "exit.gif", "filepath.gif", "find.gif", "foot.gif", "foreground.gif", "gotopage.gif",
					"hand1.gif", "hand2.gif", "icon.gif", "info.gif", "insert.gif", "login.gif", "logo_status.gif", "minuse.jpg", "modify.gif", "pageDown2.gif", "pageFirst.gif", "pageLast.gif", "pageUp2.gif", "paste.gif", "pic2.gif", "platinfo.gif", "processprop.gif", "query.gif", "question.gif", "redo.gif", "refresh.gif", "refsearch.gif", "relogin.gif", "resetpwd.gif", "save.gif",
					"savedata.gif", "scrollpanel_corn.gif", "site.gif", "start.gif", "tilehoriz.gif", "time.gif", "title.gif", "undo.gif", "up.gif", "up1.jpg", "user.gif", "valign_bottom.gif", "valign_middle.gif", "valign_top.gif", "warn.gif", "zoom.gif", "zoomin.gif", "zoomnormal.gif", "zoomout.gif" };
			Vector v_images = new Vector(); //
			for (int i = 1; i <= 201; i++) {
				String str_filename = "office_" + ("" + (1000 + i)).substring(1, 4) + ".gif"; //
				v_images.add(str_filename); //
			}
			String[] str_bb = (String[]) v_images.toArray(new String[0]); //
			String[] str_cc = new String[str_aa.length + str_bb.length]; //
			System.arraycopy(str_aa, 0, str_cc, 0, str_aa.length); //
			System.arraycopy(str_bb, 0, str_cc, str_aa.length, str_bb.length); //
			return str_cc; //
		}
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static String getSequenceNextValByDS(String _datasourcename, String _sequenceName) throws WLTRemoteException, Exception {
		return "" + ClientSequenceFactory.getInstance().getSequence(_sequenceName); // 从客户端缓存工厂取，在工厂中决定是否从远程取，而且一取就是一批!
	}

	/**
	 * 返回In条件
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public synchronized static String getInCondition(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getInCondition(_datasourcename, _sql);
	}

	//送入组ID,返回一个子查询语句,原理是先插在一个临时表中,然后返回从临时表中查询的SQL
	public synchronized static String getSubSQLFromTempSQLTableByIDs(String[] _ids) throws Exception {
		return getCommonService().getSubSQLFromTempSQLTableByIDs(_ids);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static String getStringValueByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getStringValueByDS(_datasourcename, _sql);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static HashMap getHashMapBySQLByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getHashMapBySQLByDS(_datasourcename, _sql);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static String[][] getStringArrayByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getStringArrayByDS(_datasourcename, _sql);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static String[] getStringArrayFirstColByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getStringArrayFirstColByDS(_datasourcename, _sql);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static TableDataStruct getTableDataStructByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getTableDataStructByDS(_datasourcename, _sql);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoStructByDS(_datasourcename, _sql);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql, int _topRecords) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoStructByDS(_datasourcename, _sql, _topRecords);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoArrayByDS(_datasourcename, _sql);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!而且只取前多少条!! 后台数据库自动进行根据数据源类型选择不同的分页机制
	public synchronized static HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql, int _topRecords) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoArrayByDS(_datasourcename, _sql, _topRecords);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static Vector getHashVoArrayReturnVectorByMark(String _datasourcename, String[] _sqls) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoArrayReturnVectorByDS(_datasourcename, _sqls);
	}

	// 远程取数,如果_datasourcename为null,则从默认数据源取数!
	public synchronized static HashMap getHashVoArrayReturnMapByMark(String _datasourcename, String[] _sqls, String[] _keys) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoArrayReturnMapByDS(_datasourcename, _sqls, _keys);
	}

	// 返回树型结构的hashVO
	public synchronized static HashVO[] getHashVoArrayAsTreeStructByDS(String _datasourcename, String _sql, String _idField, String _parentIDField, String _seqField, String _rootNodeCondition) throws Exception {
		return getCommonService().getHashVoArrayAsTreeStructByDS(_datasourcename, _sql, _idField, _parentIDField, _seqField, _rootNodeCondition); //
	}

	// 在指定数据源上,执行一条数据库修改语句,比如insert,delete,update,如果_datasourcename为null,则操作默认数据源!
	public synchronized static int executeUpdateByDS(String _dsName, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().executeUpdateByDS(_dsName, _sql).intValue();
	}

	public synchronized static int executeUpdateByDSAutoCommit(String _dsName, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().executeUpdateByDSAutoCommit(_dsName, _sql).intValue();
	}

	// 在指定数据源上,执行一条数据库修改语句,比如insert,delete,update,如果_datasourcename为null,则操作默认数据源!
	public synchronized static int executeUpdateByDSPS(String _dsName, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().executeUpdateByDSPS(_dsName, _sql).intValue();
	}

	/**
	 * 执行带SQLBuilderIfc参数的sql
	 */
	public synchronized static int executeUpdateByDS(String _datasourcename, SQLBuilderIfc _sqlBuilder) throws WLTRemoteException, Exception {
		return getCommonService().executeUpdateByDS(_datasourcename, _sqlBuilder);
	}

	// 执行宏SQL!!!!!
	public synchronized static int executeMacroUpdateByDS(String _dsName, String _sql, String[] _colvalues) throws WLTRemoteException, Exception {
		return getCommonService().executeMacroUpdateByDS(_dsName, _sql, _colvalues).intValue();
	}

	// 在指定数据源上,执行一批数据库修改语句,比如insert,delete,update,如果_datasourcename为null,则操作默认数据源!
	public synchronized static void executeBatchByDS(String _dsName, String[] _sqls) throws Exception {
		getCommonService().executeBatchByDS(_dsName, _sqls); //
	}

	public synchronized static void executeBatchByDSNoLog(String _dsName, String _sqls) throws Exception {
		getCommonService().executeBatchByDSNoLog(_dsName, _sqls); //
	}

	// 在指定数据源上,执行一批数据库修改语句,比如insert,delete,update,如果_datasourcename为null,则操作默认数据源!
	public synchronized static void executeBatchByDS(String _dsName, java.util.List _sqllist) throws Exception {
		getCommonService().executeBatchByDS(_dsName, _sqllist); //
	}

	// 在指定数据源上,执行一批数据库修改语句,比如insert,delete,update,如果_datasourcename为null,则操作默认数据源!
	public synchronized static void executeBatchByDS(String _dsName, java.util.List _sqllist, boolean _isDebugLog, boolean _isDBLog) throws Exception {
		getCommonService().executeBatchByDS(_dsName, _sqllist, _isDebugLog, _isDBLog); //
	}

	// 存储过程,如果_datasourcename为null,则操作默认数据源!
	public synchronized static void callProcedure(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		getCommonService().callProcedureByDS(_datasourcename, procedureName, parmeters);
	}

	// 存储过程,如果_datasourcename为null,则操作默认数据源!
	public synchronized static void callProcedureSqlserver(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		getCommonService().callProcedureByDSSqlServer(_datasourcename, procedureName, parmeters);
	}

	// 存储过程,如果_datasourcename为null,则操作默认数据源!
	public synchronized static String callProcedureByReturn(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		return getCommonService().callProcedureReturnStrByDS(_datasourcename, procedureName, parmeters);
	}

	// 存储函数,如果_datasourcename为null,则操作默认数据源!
	public synchronized static String callFunctionByReturnVarchar(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		return getCommonService().callFunctionReturnStrByDS(_datasourcename, procedureName, parmeters);
	}

	// 存储函数,如果_datasourcename为null,则操作默认数据源!
	public synchronized static String[][] callFunctionByReturnTable(String _datasourcename, String functionName, String[] parmeters) throws Exception {
		return getCommonService().callFunctionReturnTableByDS(_datasourcename, functionName, parmeters);
	}

	// 取元数据配置信息
	public synchronized static Pub_Templet_1VO getPub_Templet_1VO(String _code) throws Exception {
		return getMetaDataService().getPub_Templet_1VO(_code);
	}

	// 取元数据配置信息
	public synchronized static Pub_Templet_1VO[] getPub_Templet_1VOs(String[] _codes) throws Exception {
		return getMetaDataService().getPub_Templet_1VOs(_codes);
	}

	// 取元数据配置信息
	public synchronized static Pub_Templet_1VO getPub_Templet_1VO(AbstractTMO _tmo) throws Exception {
		Pub_Templet_1VO templetVO = getMetaDataService().getPub_Templet_1VO(_tmo.getPub_templet_1Data(), _tmo.getPub_templet_1_itemData(), "CLASS", "客户端类:" + _tmo.getClass().getName()); //必须转换成HashvO去取,否则有的TMO如果是内部类则可能报logger序列化出错等信息!
		return templetVO;
	}

	// 取元数据配置信息
	public synchronized static Pub_Templet_1VO getPub_Templet_1VO(ServerTMODefine _serverTMO) throws Exception {
		Pub_Templet_1VO templetVO = getMetaDataService().getPub_Templet_1VO(_serverTMO); //
		return templetVO;
	}

	// 元数据--取卡片数据!,如果_datasourcename为null,则操作默认数据源!
	public synchronized static Object[] getBillCardDataByDS(String _dsName, String _sql, Pub_Templet_1VO _templetVO) throws WLTRemoteException, Exception {
		return getMetaDataService().getBillCardDataByDS(_dsName, _sql, _templetVO.getParPub_Templet_1VO());
	}

	// 元数据--取列表数据!,如果_datasourcename为null,则操作默认数据源!
	public synchronized static Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws WLTRemoteException, Exception {
		return getMetaDataService().getBillListDataByDS(_datasourcename, _sql, _templetVO.getParPub_Templet_1VO());
	}

	// 元数据--取列表数据!,如果_datasourcename为null,则操作默认数据源!
	public synchronized static BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws WLTRemoteException, Exception {
		return getMetaDataService().getBillVOsByDS(_datasourcename, _sql, _templetVO.getParPub_Templet_1VO());
	}

	// 元数据--取查询模板数据!,如果_datasourcename为null,则操作默认数据源!
	public synchronized static Object[][] getQueryData(String _dsName, String _sql, Pub_Templet_1VO _templetVO) throws WLTRemoteException, Exception {
		return getMetaDataService().getQueryDataByDS(_dsName, _sql, _templetVO);

	}

	/**
	 * 根据父亲主键构成树
	 * 
	 * @param par_roottitle
	 * @param par_tablename
	 * @param par_key
	 * @param par_code
	 * @param par_name
	 * @param par_coderule
	 * @param par_wherecondition
	 * @return
	 */
	public synchronized static JTree getJTreeByParentPK_HashVO(String _dsName, String par_roottitle, String _sql, String _keyname, String _parentkeyname) {
		String str_sql = _sql;
		HashVO[] hashVOs;
		try {
			hashVOs = getCommonService().getHashVoArrayByDS(_dsName, str_sql);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} //

		DefaultMutableTreeNode node_root = new DefaultMutableTreeNode(par_roottitle); // 创建根结点

		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[hashVOs.length]; // 创建所有结点数组
		for (int i = 0; i < hashVOs.length; i++) {
			node_level_1[i] = new DefaultMutableTreeNode(hashVOs[i]); // 创建各个结点

			node_root.add(node_level_1[i]); // 加入根结点
		}

		// 构建树
		for (int i = 0; i < node_level_1.length; i++) {
			HashVO nodeVO = (HashVO) node_level_1[i].getUserObject();
			String str_pk = nodeVO.getStringValue(_keyname); // 主键
			String str_pk_parentPK = nodeVO.getStringValue(_parentkeyname); // 父亲主键
			if (str_pk_parentPK == null || str_pk_parentPK.equals(""))
				continue;
			for (int j = 0; j < node_level_1.length; j++) {
				HashVO nodeVO_2 = (HashVO) node_level_1[j].getUserObject();
				String str_pk_2 = nodeVO_2.getStringValue(_keyname); // 主键
				String str_pk_parentPK_2 = nodeVO_2.getStringValue(_parentkeyname); // 父亲主键
				if (str_pk_2.equals(str_pk_parentPK)) // 如果发现该结点主键正好是上层循环的父亲结点,则将其作为我的儿子处理加入
				{
					try {
						node_level_1[j].add(node_level_1[i]);
					} catch (Exception ex) {
						System.out.println("在[" + node_level_1[j] + "]下加入子结点[" + node_level_1[i] + "]失败"); // /
						ex.printStackTrace(); //
					}
				}
			}
		}
		JTree aJTree = new JTree(new DefaultTreeModel(node_root));
		return aJTree;
	}

	//上传文件
	public synchronized static String uploadFileFromClient(ClassFileVO _vo) throws Exception {
		return getCommonService().uploadFile(_vo);
	}

	//上传文件 文件名是否转码
	public synchronized static String uploadFileFromClient(ClassFileVO _vo, boolean ifChangeName) throws Exception {
		return getCommonService().uploadFile(_vo, ifChangeName);
	}

	//下载文件
	public synchronized static ClassFileVO downloadToClient(String filename) throws Exception {
		return getCommonService().downloadFile(filename);
	}

	/**
	 * 从服务器端下载文件到客户端
	 * 
	 * @param _serverdir
	 *            服务器端路径,可以是D:/wltuploadfile的绝对路径,也可以是/upload的相对路径方式,如果是相对路径,则需要使用环境变量WLTUPLOADFILEDIR的路径加上相对路径
	 * @param _serverFileName
	 *            服务器端的文件名
	 * @param _isAbsoluteSeverDir
	 *            是否是绝对路径,true=是,false=否
	 * @param _clientdir
	 *            客户端路径,同服务器端路径,既可以是绝对路径,也可以是相对路径,如果是相对路径,则需要使用环境变量CLIENTCODECACHE的路径加上相对路径
	 * @param _clientFileName
	 *            客户端文件名
	 * @param _isAbsoluteClientDir
	 *            客户端路径是否是绝对路径
	 * @return 返回下载到客户端后文件的绝对路径!!
	 * @throws Exception
	 */
	public synchronized static String downLoadFile(String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir, String _clientdir, String _clientFileName, boolean _isAbsoluteClientDir) throws Exception {
		// 写一个远程服务,服务端逻辑是:
		// 根据_isAbsoluteSeverDir,如果是false,则算出绝对路径(如果是相对路径,则要校验首个字母必须是/,结束字母必须不能是/),如果是绝对路径,则结束字母必须不能是/
		// 读取服务器端文件内容,生成ClassFileVO,返回客户端,
		// 客户端逻辑:
		// 根据_isAbsoluteSeverDir,如果是false,则算出绝对路径
		// 根据服务器端返回的文件内容,在客户端创建文件!!!
		// 返回客户端文件绝对路径的字符串

		ClassFileVO classFileVO = getCommonService().downLoadFile(_serverdir, _serverFileName, _isAbsoluteSeverDir);
		if (_isAbsoluteClientDir) {
			if (_clientdir == null || _clientdir.trim().equals("")) {
				throw new WLTAppException("下载文件时,绝路路径不能为空!!");
			}
			_clientdir = UIUtil.replaceAll(_clientdir, "\\", "/"); //
			if (_clientdir.endsWith("/")) {
				throw new WLTAppException("下载文件时,绝路路径不能以/结尾!!");
			}
		} else {
			if (_clientdir != null && !_clientdir.trim().equals("")) {
				_clientdir = UIUtil.replaceAll(_clientdir, "\\", "/"); //
				if (!_clientdir.startsWith("/")) {
					throw new WLTAppException("下载文件时,相对路径必须以/开始!!");
				}

				if (_clientdir.endsWith("/")) {
					throw new WLTAppException("下载文件时,相对路径不能以/结尾!!");
				}
			}
		}

		String str_filepathname = null; //
		if (_isAbsoluteClientDir) {// 如果是绝对路径
			str_filepathname = _clientdir + "/" + _clientFileName; //
		} else {
			String str_ClientCodeCache = System.getProperty("ClientCodeCache");
			if (str_ClientCodeCache.indexOf("\\") >= 0) {// 变换客户端的\\为/
				str_ClientCodeCache = UIUtil.replaceAll(str_ClientCodeCache, "\\", "/"); //
			}
			if (str_ClientCodeCache.endsWith("/")) {// 如果客户端路径最后一位为/则去掉
				str_ClientCodeCache = str_ClientCodeCache.substring(0, str_ClientCodeCache.length() - 1);
			}
			if (_clientdir == null || _clientdir.trim().equals("")) { // 如果是相对路径,且没有定义相对路径,则直接放在系统上传目录的根目录下!
				str_filepathname = str_ClientCodeCache + "/" + _clientFileName; //
			} else {
				str_filepathname = str_ClientCodeCache + _clientdir + "/" + _clientFileName; //
			}
		}

		str_filepathname = UIUtil.replaceAll(str_filepathname, "\\", "/"); //
		File downloadfile = new File(str_filepathname); //
		downloadfile.createNewFile();
		FileOutputStream out = new FileOutputStream(downloadfile);
		out.write(classFileVO.getByteCodes());
		out.close();
		//		}

		return str_filepathname;
	}

	public synchronized static String upLoadFile(String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir, String _clientdir, String _clientFileName, boolean _isAbsoluteClientDir) throws Exception {
		return upLoadFile(_serverdir, _serverFileName, _isAbsoluteSeverDir, _clientdir, _clientFileName, _isAbsoluteClientDir, false, true); //
	}

	/**
	 * 上传文件,将客户端文件上传至服务器端!
	 * 
	 * @param _serverdir
	 *            服务器端路径,可以是D:/wltuploadfile的绝对路径,也可以是/upload的相对路径方式,如果是相对路径,则需要使用环境变量WLTUPLOADFILEDIR的路径加上相对路径
	 * @param _serverFileName
	 *            服务器端的文件名
	 * @param _isAbsoluteSeverDir
	 *            是否是绝对路径,true=是,false=否
	 * @param _clientdir
	 *            客户端路径,同服务器端路径,既可以是绝对路径,也可以是相对路径,如果是相对路径,则需要使用环境变量CLIENTCODECACHE的路径加上相对路径
	 * @param _clientFileName
	 *            客户端文件名
	 * @param _isAbsoluteClientDir
	 *            客户端路径是否是绝对路径
	 * @return 返回上传到服务器端文件的绝对路径!!
	 * @throws Exception
	 */
	public synchronized static String upLoadFile(String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir, String _clientdir, String _clientFileName, boolean _isAbsoluteClientDir, boolean _isConvertHex, boolean _isAddSerialNo) throws Exception {
		if (_isAbsoluteClientDir) {
			if (_clientdir == null || _clientdir.trim().equals("")) {
				throw new WLTAppException("上传文件时,绝路路径不能为空!!");
			}
			_clientdir = UIUtil.replaceAll(_clientdir, "\\", "/"); //
			if (_clientdir.endsWith("/")) {
				throw new WLTAppException("上传文件时,绝路路径不能以/结尾!!");
			}
		} else {
			if (_clientdir != null && !_clientdir.trim().equals("")) {
				_clientdir = UIUtil.replaceAll(_clientdir, "\\", "/"); //
				if (!_clientdir.startsWith("/")) {
					throw new WLTAppException("上传文件时,相对路径必须以/开始!!");
				}

				if (_clientdir.endsWith("/")) {
					throw new WLTAppException("上传文件时,相对路径不能以/结尾!!");
				}
			}
		}

		String str_filepathname = null; //
		if (_isAbsoluteClientDir) {// 如果是绝对路径
			str_filepathname = _clientdir + "/" + _clientFileName; //
		} else {
			String str_ClientCodeCache = System.getProperty("ClientCodeCache");
			if (str_ClientCodeCache.indexOf("\\") >= 0) {// 变换客户端的\\为/
				str_ClientCodeCache = UIUtil.replaceAll(str_ClientCodeCache, "\\", "/"); //
			}
			if (str_ClientCodeCache.endsWith("/")) {// 如果客户端路径最后一位为/则去掉
				str_ClientCodeCache = str_ClientCodeCache.substring(0, str_ClientCodeCache.length() - 1);
			}

			if (_clientdir == null || _clientdir.trim().equals("")) { // 如果是相对路径,且没有定义相对路径,则直接放在系统上传目录的根目录下!
				str_filepathname = str_ClientCodeCache + "/" + _clientFileName; //
			} else {
				str_filepathname = str_ClientCodeCache + _clientdir + "/" + _clientFileName; //
			}
		}

		str_filepathname = UIUtil.replaceAll(str_filepathname, "\\", "/"); //
		File uploadfile = new File(str_filepathname);
		if (!uploadfile.exists()) {
			throw new WLTAppException("文件[" + str_filepathname + "]不存在!!");
		}

		byte[] fileBytes = TBUtil.getTBUtil().readFromInputStreamToBytes(new FileInputStream(str_filepathname));//
		cn.com.infostrategy.to.common.ClassFileVO filevo = new cn.com.infostrategy.to.common.ClassFileVO();
		filevo.setClassFileName(_clientFileName); //文件名..
		filevo.setByteCodes(fileBytes); //文件内容..
		String server_dir = getCommonService().upLoadFile(filevo, _serverdir, _serverFileName, _isAbsoluteSeverDir, _isConvertHex, _isAddSerialNo);
		return server_dir; //
	}

	public synchronized static ClassFileVO downloadToClientByAbsolutePath(String filename) throws Exception {
		return getCommonService().downloadToClientByAbsolutePath(filename);
	}

	public synchronized static Dimension getScreenMaxDimension() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		return new Dimension((int) dim.getWidth(), (int) (dim.getHeight() - 30)); //
	}

	/**
	 * 下载并打开文件!!
	 * 
	 * @param _parent
	 * @param _filename
	 * @throws Exception
	 */
	public synchronized static void downLoadAndOpenFile(Container _parent, String _filename) throws Exception {
		ClassFileVO cvo = downloadToClient(_filename);
		while (_filename.indexOf("\\") > -1) { //
			_filename = _filename.substring(_filename.indexOf("\\") + 1, _filename.length()); //
		}
		String str_clientfilename = System.getProperty("ClientCodeCache") + _filename; //

		File file = new File(str_clientfilename);
		if (file.exists()) {
		} else {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(cvo.getByteCodes());
			out.close();
		}

		Desktop.open(new File(str_clientfilename)); //		
	}

	/**
	 * 下载文件,自己选择路径
	 * 
	 * @param _parent
	 * @param _filename
	 * @param _newFileName
	 * @throws Exception
	 */
	public synchronized static void downLoadToEveryWhere(String downfile, String _filename) throws Exception {
		String str_dir = downfile + "\\" + _filename;

		ClassFileVO cvo = downloadToClient(_filename);
		while (_filename.indexOf("\\") > -1) {
			_filename = _filename.substring(_filename.indexOf("\\") + 1, _filename.length());
		}
		String str_clientfilename = System.getProperty("ClientCodeCache") + _filename;

		File file = new File(str_dir);
		if (file.exists()) {
		} else {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(cvo.getByteCodes());
			out.close();
		}

	}

	public synchronized static void downLoadAndBrowseFile(Container _parent, String _filename) throws Exception {
		ClassFileVO cvo = downloadToClient(_filename);
		while (_filename.indexOf("\\") > -1) {
			_filename = _filename.substring(_filename.indexOf("\\") + 1, _filename.length());
		}
		String str_clientfilename = System.getProperty("ClientCodeCache") + _filename;

		File file = new File(str_clientfilename);
		if (file.exists()) {
		} else {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(cvo.getByteCodes());
			out.close();
		}
		final WebBrowser wb = new WebBrowser(new java.net.URL("file:///" + str_clientfilename)); //
		BillDialog dialog = new BillDialog(_parent, "预览文件", 1024, 738); //
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//wb.dispose();  //
			}
		});

		dialog.setLocation(0, 0); //
		dialog.getContentPane().add(wb); //
		dialog.setVisible(true); //
	}
	
	public synchronized static String getCurrYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String str_date = sdf.format(new Date());
		return str_date;
	}

	public synchronized static void loadHtml(String _classname, HashMap _map) throws Exception {
		loadHtml(_classname, _map, false); //
	}

	/**
	 * 两次请求方式,第一次注册缓存,第二次请求!!
	 * @param _classname
	 * @param _map
	 * @throws Exception
	 */
	public synchronized static void loadHtml(String _classname, HashMap _map, boolean _isDisPatch) throws Exception {
		WebCallParVO parVO = new WebCallParVO(); //
		parVO.setCallClassName(_classname); //
		parVO.setParsMap(_map); //
		cn.com.infostrategy.ui.report.ReportServiceIfc reportService = (cn.com.infostrategy.ui.report.ReportServiceIfc) RemoteServiceFactory.getInstance().lookUpService(cn.com.infostrategy.ui.report.ReportServiceIfc.class); //
		String str_sessionid = reportService.registerWebCallSessionID(parVO); //
		String str_url = System.getProperty("CALLURL") + "/WebCallServlet?webcallid=" + str_sessionid; //
		if (_isDisPatch) {
			str_url = str_url + "&isdispatch=Y"; //是分派的,即实现类必须是WebDispatchIfc,而不是WebCallBeanIfc了
		}
		try {
			Desktop.browse(new URL(str_url)); //先尝试使用JDIC来打开,因为这会保证浏览器的窗口在前端显示,而不是跑到后面去!!!
		} catch (Throwable th) {
			th.printStackTrace(); //
			try {
				Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\"");
				//Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler \"" + str_url + "\"");
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
		}
	}

	//两次请求方式!参数支持复杂的Java对象,比如ArrayList
	public synchronized static void openBillHtmlDialog(Container _parent, String _title, String _classname, HashMap _map) throws Exception {
		WebCallParVO parVO = new WebCallParVO(); //
		parVO.setCallClassName(_classname); //
		parVO.setParsMap(_map); //
		cn.com.infostrategy.ui.report.ReportServiceIfc reportService = (cn.com.infostrategy.ui.report.ReportServiceIfc) RemoteServiceFactory.getInstance().lookUpService(cn.com.infostrategy.ui.report.ReportServiceIfc.class); //
		String str_sessionid = reportService.registerWebCallSessionID(parVO); //
		String str_url = System.getProperty("CALLURL") + "/WebCallServlet?webcallid=" + str_sessionid; //
		cn.com.infostrategy.ui.report.BillHtmlDialog dialog = new cn.com.infostrategy.ui.report.BillHtmlDialog(_parent, _title, new URL(str_url)); //
		dialog.setVisible(true); //
	}

	/**
	 * 两次请求方式!!
	 * @param _title
	 * @param _classname
	 * @param _map
	 * @throws Exception
	 */
	public synchronized static void openBillHtmlFrame(String _title, String _classname, HashMap _map) throws Exception {
		WebCallParVO parVO = new WebCallParVO(); //
		parVO.setCallClassName(_classname); //
		parVO.setParsMap(_map); //
		cn.com.infostrategy.ui.report.ReportServiceIfc reportService = (cn.com.infostrategy.ui.report.ReportServiceIfc) RemoteServiceFactory.getInstance().lookUpService(cn.com.infostrategy.ui.report.ReportServiceIfc.class); //
		String str_sessionid = reportService.registerWebCallSessionID(parVO); //
		String str_url = System.getProperty("CALLURL") + "/WebCallServlet?webcallid=" + str_sessionid; //
		cn.com.infostrategy.ui.report.BillHtmlFrame dialog = new cn.com.infostrategy.ui.report.BillHtmlFrame(_title, new URL(str_url)); //
		dialog.setVisible(true); //
	}

	//一次请求方式,参数_parMap中的value必须是字符型!!!
	public synchronized static void openRemoteServerHtml(String _StrParCallClassName, HashMap _parMap) {
		openRemoteServerHtml(_StrParCallClassName, _parMap, false); //
	}

	/**
	 * 直接打开一个文件,经常遇到导出一个文件后,问是否立即打开?如果是,则打开文件!!
	 * 为什么打开一个文件要这么复杂呢?因为打开文件经常会发生打开文件(比如一个Word)的窗口会躲在后面!!而不是显示在最前面!结果许多客户没注意,以为没打开,然后提个Bug
	 * 后来发现如果使用Desktop.open(_file);就会显示在前面,所以封装成这样一个方法!!【xch/2012-06-15】
	 * @param _parent
	 * @param _file
	 */
	public synchronized static void openFile(java.awt.Container _parent, File _file) {
		if (!_file.exists()) {
			MessageBox.show(_parent, "文件[" + _file + "]不存在!"); //
			return;
		}
		try {
			Desktop.open(_file); //使用Desktop打开,会保证打开文件的窗口在显示在前面,而不是躲在后面!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			try {
				Runtime.getRuntime().exec("explorer.exe \"" + _file.getAbsolutePath() + "\""); //调用浏览器程序执行,如果JDIC打不开还要尝试使用浏览器直接打开!!!因为曾经遇到过JDIC打不开的人,那都是因为默认浏览器不是IE造成的!!NND
			} catch (Exception exx) {
				exx.printStackTrace();
			}
		}
	}

	/**
	 * 到处导出数据时都有保存文件到本地的需求,但每次都重写下面这一堆代码,实际上完全可以封装成一个方法!!
	 * 关键是这里面有两个核心需求一般人不注意，一是,如果该文件已存在,要提示是否覆盖？？ 二是,保存成功后,要提示是否立即打开这个文件？
	 * 此方法都实现了这两者!
	 * @param _parent
	 * @param _filterType
	 * @param _defaultFileName
	 * @param _bytes
	 * @return
	 */
	public synchronized static String saveFile(java.awt.Container _parent, final String _filterType, String _defaultFileName, byte[] _bytes) {
		try {
			JFileChooser chooser = new JFileChooser();
			if (_filterType != null) {
				chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
					public boolean accept(File file) {
						if (file.isDirectory()) { //显示文件夹, 否则怪怪的! Gwang 2012-11-06
							return true;
						} else {
							String filename = file.getName();
							return filename.endsWith("." + _filterType);
						}
					}

					public String getDescription() {
						return "*." + _filterType;
					}
				});
			}

			if (_defaultFileName != null) {
				if (_filterType != null) {
					File f = new File(new File("C:\\" + _defaultFileName + "." + _filterType).getCanonicalPath());
					chooser.setSelectedFile(f);
				} else {
					File f = new File(new File("C:\\" + _defaultFileName).getCanonicalPath());
					chooser.setSelectedFile(f);
				}
			}
			int li_rewult = chooser.showSaveDialog(_parent);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {
				File curFile = chooser.getSelectedFile(); //
				if (curFile != null) {
					if (curFile.exists()) {
						if (!MessageBox.confirm(_parent, "文件[" + curFile.getAbsolutePath() + "]已存在,是否覆盖?", 450, 150)) {
							return null;
						}
					}
					FileOutputStream fout = new FileOutputStream(curFile, false); //
					fout.write(_bytes); //
					fout.close(); //
					if (MessageBox.confirm(_parent, "保存文件[" + curFile.getAbsolutePath() + "]成功!\r\n您是否想立即打开该文件?", 500, 175)) {
						UIUtil.openFile(_parent, curFile); //打开文件
					}
					return curFile.getAbsolutePath(); //返回实际保存的文件名
				}
			}
			return null;
		} catch (Exception _ex) {
			MessageBox.showException(_parent, _ex); //
			return null;
		}
	}

	/**
	 * 一次请求方式!将参数_parMap中的值解析成get参数的方式!! 所以参数_parMap中的value必须是字符型!!!
	 * 打开服务器一个Html,指定一个类名(实现WebCallBeanIfc接口),指定参数,然后将服务器实现类自动生成一个html返回!!
	 * @param _StrParCallClassName
	 * @param _parMap
	 */
	public synchronized static void openRemoteServerHtml(String _StrParCallClassName, HashMap _parMap, boolean _isDisPatch) {
		TBUtil tbUtil = new TBUtil(); //
		StringBuilder sb_url = new StringBuilder(); //
		sb_url.append(System.getProperty("CALLURL") + "/WebCallServlet?StrParCallClassName=" + _StrParCallClassName); //tbUtil.convertStrToHexString(_StrParCallClassName)
		if (_isDisPatch) { //如果是dispatch
			sb_url.append("&isdispatch=Y"); //是分派的,即实现类必须是WebDispatchIfc,而不是WebCallBeanIfc了
		}
		if (_parMap != null) {
			String[] str_keys = (String[]) _parMap.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) { //所有参数的key
				sb_url.append("&" + str_keys[i] + "=" + "" + _parMap.get(str_keys[i])); //tbUtil.convertStrToHexString()循环将所有参数都送入,为了解决中文问题,统一转成16进制!!!!
			}
		}
		try {
			Desktop.browse(new URL(sb_url.toString())); //先尝试使用JDIC来打开,因为这会保证浏览器的窗口在前端显示,而不是跑到后面去!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			try {
				Runtime.getRuntime().exec("explorer.exe \"" + sb_url.toString() + "\""); //调用浏览器程序执行,如果JDIC打不开还要尝试使用浏览器直接打开!!!因为曾经遇到过JDIC打不开的人,那都是因为默认浏览器不是IE造成的!!NND
			} catch (Exception exx) {
				exx.printStackTrace();
			}
		}
	}

	/**
	 * 在线打开服务器端的文件,如果是wps,doc,xls,pdf则可以直接在浏览器中打开,否则会出现下载
	 * @param _pathType 三种,null表示直接从WebRoot目录下下载,还可以是upload与office
	 * @param _filename
	 */
	public synchronized static void openRemoteServerFile(String _pathType, String _filename) {
		String str_url = null;
		if (_pathType == null) {
			str_url = System.getProperty("CALLURL") + "/DownLoadFileServlet?filename=" + _filename; //从WebRoot目录下下载
		} else {
			//			try {
			//				String temp = URLEncoder.encode(_filename, "GBK");//这里先对汉字编码，再通过url传过去，否则Servlet接收到以后容易产生中文乱码【吴鹏/2012-6-19】
			//				_filename = temp;//
			//吴鹏认为会乱码？但实际逻辑是在服务器端要设置取值的encoding,然后或者取到值后进行中文转换处理!! 而不是应该在这里进行处理!!因为浏览器本身有定义的请求编码，即一般上UTF-8,所以我还是注销掉!【徐长华/2012-06-20】
			//			} catch (UnsupportedEncodingException e) {
			//				e.printStackTrace();
			//			}
			str_url = System.getProperty("CALLURL") + "/DownLoadFileServlet?pathtype=" + _pathType + "&filename=" + _filename; //可以从某种类型目录下下载
		}
		try {
			Desktop.browse(new URL(str_url)); //先尝试使用JDIC来打开,因为这会保证浏览器的窗口在前端显示,而不是跑到后面去!!!
		} catch (Throwable th) {
			th.printStackTrace(); //
			try {
				Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\"");
				//Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler \"" + str_url + "\"");  //这种方法会不会保证窗口不躲到后面去?
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
		}
	}

	public synchronized static void downLoadAndBrowseNewFile(Container _parent, String _filename, String _newFileName) throws Exception {
		ClassFileVO cvo = downloadToClient(_filename);
		while (_filename.indexOf("\\") > -1) {
			_filename = _filename.substring(_filename.indexOf("\\") + 1, _filename.length());
		}
		String str_clientfilename = System.getProperty("ClientCodeCache") + _filename;

		File file = new File(_newFileName);
		if (file.exists()) {
		} else {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file, false);
			out.write(cvo.getByteCodes());
			out.close();
		}

		WebBrowser wb = new WebBrowser(new java.net.URL(System.getProperty("CALLURL") + "/applet/viewword.jsp")); //
		BillDialog dialog = new BillDialog(_parent, "预览文件", 1024, 738); //
		dialog.setDefaultCloseOperation(BillDialog.DISPOSE_ON_CLOSE); //
		dialog.setModal(false); //
		// dialog.addWindowListener(new WindowAdapter() {
		// @Override
		// public void windowClosing(WindowEvent e) {
		// wb.dispose(); //
		// }
		// });
		dialog.setLocation(0, 0); //

		dialog.getContentPane().add(wb); //
		dialog.setVisible(true); //

		// Runtime.getRuntime().exec("explorer.exe \"" +
		// System.getProperty("CALLURL") + "/applet/viewword.jsp\""); //
	}

	/**
	 * 回写客户端属性文件,即客户端Java_Home\bin目录下将有个文件prop.ini,负责存储所有客户端需要扩展有系统属性(System.getProperty())
	 * 以前的属性只能放在启动命令中,非常不容易扩展,尤其是IE访问时,是调注册表中的参数的,所以必须要有一种机制,可以扩展更多系统属性! 即要有个文件! 比如代理服务器,上次登录用户名等!
	 * 因为启动系统时,总是先读取一下这个文件,然后加载到System.property中,所以这与在命令行通过-Dkey=value的效果是一样的了!!!
	 * @param _key
	 * @param _value
	 */
	public synchronized static void writeBackClientPropFile(String _key, String _value) {
		try {
			String str_fileName = System.getProperty("java.home") + "\\bin\\prop.ini"; //写在这个文件中!!!
			File file = new File(str_fileName); //
			if (file.exists()) { //如果文件存在,则加载后,重置,再回写!!!
				FileInputStream fins = new FileInputStream(file); //
				Properties prop = new Properties(); //
				prop.load(fins); //加载进来!!
				fins.close(); //
				prop.setProperty(_key, _value); //重写!!!
				FileOutputStream fout = new FileOutputStream(file, false); //再立即回写文件!
				prop.store(fout, "Client User Props"); //存储进入文件!!
				fout.close(); //关闭流!!
			} else { //如果文件不存在,则直接写!!!
				Properties prop = new Properties(); //
				prop.setProperty(_key, _value); //重写!!!
				FileOutputStream fout = new FileOutputStream(file, false); //
				prop.store(fout, "Client User Props"); //存储进入文件!!
				fout.close(); //关闭流!!
			}
			System.setProperty(_key, _value); //立即往系统中设置一下!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 替换字符
	 * 
	 * @param str_par
	 * @param old_item
	 * @param new_item
	 * @return
	 */
	public static synchronized String replaceAll(String str_par, String old_item, String new_item) {
		String str_return = "";
		String str_remain = str_par;
		boolean bo_1 = true;
		while (bo_1) {
			int li_pos = str_remain.indexOf(old_item);
			if (li_pos < 0) {
				break;
			} // 如果找不到,则返回
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // 将结果字符串加上原来前辍
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // 将剩余的加上
		return str_return;
	}

	//
	public static synchronized String[] getLoginUserCorpIdName(String _deptId, String _deptLinkCode, String _deptCorpType) {
		try {
			if (_deptLinkCode != null) { //如果有Linkcode,则找上一层!
				String str_linkCode = _deptLinkCode; //取得linkCode
				int li_cycle = str_linkCode.length() / 4; //循环次数!!
				String[] str_parentCode = new String[li_cycle]; //
				for (int i = 0; i < li_cycle; i++) {
					str_parentCode[i] = str_linkCode.substring(i * 4, (i + 1) * 4); //
				}
				//System.out.println("发现LinkCode不为空[" + str_linkCode + "],则根据LinkCode机制取!!"); //以后还是使用Link机制更好!
				TBUtil tbUtil = new TBUtil(); //
				HashVO[] hvs_parCorps = UIUtil.getHashVoArrayByDS(null, "select id,code,name,corptype from pub_corp_dept where linkcode in (" + tbUtil.getInCondition(str_parentCode) + ") order by linkcode asc"); //
				HashVO[] hvs_allCorpMap = UIUtil.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('机构分类','机构类型')"); //所有定义

				String str_formula = null; //
				for (int i = 0; i < hvs_allCorpMap.length; i++) {
					if (hvs_allCorpMap[i].getStringValue("id").equals(_deptCorpType)) { //找到我的机构类型
						str_formula = hvs_allCorpMap[i].getStringValue("code"); //
						break; //
					}
				}

				if (str_formula != null && !str_formula.equals("")) {
					HashMap map = TBUtil.getTBUtil().convertStrToMapByExpress(str_formula, ";", "="); //
					String str_findCotypeType = (String) map.get("$本机构"); //找到需要寻找的机构类型!!根据宏代码找到实际想找的机构类型！！！
					if (str_findCotypeType != null) { //到底是我的哪个父亲???
						for (int i = 0; i < hvs_parCorps.length; i++) { //
							if (hvs_parCorps[i].getStringValue("corptype", "").equals(str_findCotypeType)) { //退出!!!
								String str_rt_id = hvs_parCorps[i].getStringValue("id"); //
								String str_rt_name = hvs_parCorps[i].getStringValue("id"); //
								return new String[] { str_rt_id, str_rt_name };
								//System.out.println("发现我的本机构是[" + userVO.getCorpID() + "/" + userVO.getDeptname() + "]");
							}
						}
					}
				}
			} else { //如果没有层级,则使用递归查询!!
				//System.out.println("发现LinkCode为空,则使用递归机制查找...."); //
				HashVO[] hvs_parent = UIUtil.getParentCorpVOByMacro(3, _deptId, "$本机构"); //
				if (hvs_parent != null && hvs_parent.length > 0) {
					String str_rt_id = hvs_parent[0].getStringValue("id");
					String str_rt_name = hvs_parent[0].getStringValue("name");
					return new String[] { str_rt_id, str_rt_name };
				}
			}
			return null; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	public static synchronized String getInitParamValue(String _key) {
		return System.getProperty(_key); //
	}

	public static synchronized String getProjectName() {
		return getInitParamValue("PROJECT_NAME"); //
	}

	public static synchronized String[] getLoginUserRoleCodes() {
		return ClientEnvironment.getInstance().getLoginUserRoleCodes(); //
	}

	/**
	 * 判断当前登录人员是否有某角色【李春娟/2016-04-25】
	 * @param _rolecode
	 * @return
	 */
	public static synchronized boolean isLoginUserHasRoleCode(String _rolecode) {
		if (TBUtil.isEmpty(_rolecode)) {
			return true;
		}
		String[] roles = ClientEnvironment.getInstance().getLoginUserRoleCodes(); //
		if (roles == null || roles.length == 0) {
			return false;
		}
		for (int i = 0; i < roles.length; i++) {
			if (_rolecode.equals(roles[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断某个用户是否有某个角色【李春娟/2016-04-25】
	 * @param _rolecode
	 * @return
	 */
	public static synchronized boolean isUserHasRoleCode(String _userid, String _rolecode) {
		if (TBUtil.isEmpty(_rolecode)) {
			return true;
		}
		String count = null;
		try {
			count = UIUtil.getStringValueByDS(null, "select count(userid) from  v_pub_user_role_1 where rolecode='" + _rolecode + "' and userid ='" + _userid + "'");
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (count == null || "".equals(count.trim()) || "0".equals(count)) {
			return false;
		}
		return true;
	}
}
