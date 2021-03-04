/*******************************************************************************
 * $RCSfile: FrameWorkCommServiceImpl.java,v $ $Revision: 1.32 $ $Date:
 * 2011/11/18 11:18:09 $
 ******************************************************************************/

package cn.com.infostrategy.bs.common;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;

import cn.com.infostrategy.bs.report.util.UseCellTempletParseUtil;
import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.InitParamVO;
import cn.com.infostrategy.to.common.LinkForeignTableDefineVO;
import cn.com.infostrategy.to.common.Log4jConfigVO;
import cn.com.infostrategy.to.common.Queue;
import cn.com.infostrategy.to.common.SQLBuilderIfc;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;

/**
 * 平台通用服务,基本上都是查询数据库存,保存数据库,执行SQL,存储过程等!!
 * @author user
 */
public class FrameWorkCommServiceImpl implements FrameWorkCommServiceIfc {
	private HashMap<String, WLTJobTimer> hm_jobs = null; // liuxuanfei
	private CommDMO commDMO = null;

	public HashMap<String, WLTJobTimer> getHm_jobs() {
		if (hm_jobs == null) {
			hm_jobs = new HashMap<String, WLTJobTimer>();
		}
		return hm_jobs;
	}

	public CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}

	/**
	 * 默认构造方法
	 */
	public FrameWorkCommServiceImpl() {
	}

	/**
	 * 通用调用方法!!
	 */
	public HashMap commMethod(String _className, String _functionName, HashMap _parMap) throws Exception {
		try {
			Class objclass = Class.forName(_className);
			Method objmethod = objclass.getMethod(_functionName, new Class[] { HashMap.class }); //
			Object objInstance = Class.forName(_className).newInstance(); //
			Object returnObj = objmethod.invoke(objInstance, new Object[] { _parMap });
			return (HashMap) returnObj;
		} catch (InvocationTargetException ex) {
			ex.getTargetException().printStackTrace(); //
			throw new Exception(ex.getTargetException().getMessage());
		} catch (Exception ex) {
			throw ex;
		}
	} // 会抛远程调用异常

	/**
	 * 取得所有初始化参数
	 */
	public InitParamVO[] getInitParamVOs() throws Exception {
		return ServerEnvironment.getInstance().getInitParamVOs();
	}

	/**
	 * 取得Lof4j配置信息
	 */
	public Log4jConfigVO getLog4jConfigVO() throws Exception {
		return ServerEnvironment.getInstance().getLog4jConfigVO(); //
	}

	public Hashtable getLanguage() throws Exception // 一下子取得所有语言
	{
		CommDMO dmo = ServerEnvironment.getCommDMO();
		String str_sql = "select simplechinese,english,traditionalchinese from pub_language"; //
		String[][] str_data = dmo.getStringArrayByDS(null, str_sql);

		Hashtable ht = new Hashtable();
		if (str_data != null && str_data.length > 0) {
			for (int i = 0; i < str_data.length; i++) {
				ht.put(str_data[i][0], str_data[i]); // 送入语言
			}
		}
		return ht;
	}

	public String[] getLanguage(String _key) throws Exception // 根据某一个key取得该key对应的各种语言!!
	{
		CommDMO dmo = ServerEnvironment.getCommDMO();
		String str_sql = "select simplechinese,english,traditionalchinese from pub_language where simplechinese='" + _key + "'"; //
		String[][] str_data = dmo.getStringArrayByDS(null, str_sql);
		if (str_data != null && str_data.length > 0) {
			return str_data[0]; // 如果找到,则返回之
		} else // 如果没找到,则直接往里插入一条记录
		{
			String str_new_id = dmo.getSequenceNextValByDS(null, "s_pub_language"); //
			String str_insertsql = "insert into pub_language (id,simplechinese,english,traditionalchinese) values ('" + str_new_id + "','" + _key + "','" + _key + "','" + _key + "')";
			dmo.executeUpdateByDS(null, str_insertsql);
			return new String[] { _key, _key, _key }; //
		}
	}

	/**
	 * 取得服务器端的当前日期
	 * @return
	 */
	public String getServerCurrDate() throws Exception {
		return new TBUtil().getCurrDate(); // 返回服务器端当前日期
	}

	/**
	 * 取得服务器端的当前日期
	 * @return
	 */
	public String getServerCurrTime() throws Exception {
		return new TBUtil().getCurrTime(); // 返回服务器端当前时间
	}

	public long getServerCurrTimeLongValue() {
		return System.currentTimeMillis(); //
	}

	/**
	 * 取得系统参数
	 * @return
	 * @throws Exception
	 */
	public String[][] getServerSysOptions() throws Exception {
		return SystemOptions.getAllOptions(); //
	}

	/**
	 * 休息几秒
	 */
	public Integer sleep(Integer _second) throws Exception { // 休息几秒
		Thread.currentThread().sleep(_second.intValue() * 1000); //
		System.out.println("休息了[" + _second + "]秒,[" + Thread.currentThread().getName() + "]"); //
		return _second;
	}

	/**
	 * 使用机器人,抓取服务器端屏幕的方法,返回一个图像
	 * @return
	 * @throws Exception
	 */
	public byte[] captureScreen() throws Exception // 抓取服务器端屏幕的方法
	{
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize(); // 取得屏幕大小
		Image screen = new Robot().createScreenCapture(new Rectangle(0, 0, screenDims.width, screenDims.height)).getScaledInstance(screenDims.width, screenDims.height, Image.SCALE_FAST); // 截取本机屏幕
		BufferedImage image = new BufferedImage(screen.getWidth(null), screen.getHeight(null), BufferedImage.TYPE_INT_ARGB); //
		Graphics g = image.getGraphics();
		g.drawImage(screen, 0, 0, null);
		g.dispose();
		ByteArrayOutputStream out = new ByteArrayOutputStream(); // 输出流!!
		ImageIO.write(image, "jpg", out); //
		byte[] bytes = out.toByteArray();
		out.close(); //
		return bytes; //
	}

	// 增加某一个会话的SQL监听，太平集团的监控系统对我们系统远程报错会预警，为减少预警，这里修改返回提示信息【李春娟/2018-07-25】
	public String addSessionSQLListener(String _sessionid) throws Exception {
		if (ServerEnvironment.getSessionSqlListenerMap().containsKey(_sessionid)) {
			Queue queue = (Queue) ServerEnvironment.getSessionSqlListenerMap().get(_sessionid); //
			return "该会话已注册过了,而且已有[" + queue.size() + "]条SQL!"; //
		}

		if (!ServerEnvironment.getLoginUserMap().containsKey(_sessionid)) {
			return "该SessionID无效,因为系统中没发现该用户在线!"; //
		}

		ServerEnvironment.getSessionSqlListenerMap().put(_sessionid, new Queue(1000)); // 防止内存溢出,每个会话最多只存储最新的1000个SQL.
		return "注册该会话的SQL监听成功!!从现在开始服务器将监听与记录该会话每一步执行的SQL!!";
	}

	// 删除某一个会话的SQL监听，太平集团的监控系统对我们系统远程报错会预警，为减少预警，这里修改返回提示信息【李春娟/2018-07-25】
	public String removeSessionSQLListener(String _sessionid) throws Exception {
		if (!ServerEnvironment.getSessionSqlListenerMap().containsKey(_sessionid)) {
			return "该会话还没有注册过,移除动作无效!"; //
		}

		Queue queue = (Queue) ServerEnvironment.getSessionSqlListenerMap().get(_sessionid); //
		if (queue != null) {
			queue.clear(); //
		}
		ServerEnvironment.getSessionSqlListenerMap().remove(_sessionid); //
		return "删除该会话的SQL监听成功,从现在开始服务器将不再对该会话执行的SQL进行监听与记录!!!";
	}

	// 取得监听的SQL的所有语句
	public String getSessionSQLListenerText(String _sessionid, boolean _isclear) throws Exception {
		Queue queue = (Queue) ServerEnvironment.getSessionSqlListenerMap().get(_sessionid); //
		if (queue == null) {
			return "[" + new TBUtil().getCurrTime() + "]没有注册SessionSQL监听呢,请先注册监听!!\r\n"; //
		}

		if (queue.isEmpty()) {
			return "[" + new TBUtil().getCurrTime() + "]没有发现一条数据!!\r\n"; //
		}

		StringBuffer sb_sql = new StringBuffer();
		if (_isclear) {
			int li_count = 0; //
			while (!queue.empty()) {
				sb_sql.append("[" + (++li_count) + "]" + queue.pop() + ";\r\n"); //
			}
		} else {
			for (int i = 0; i < queue.size(); i++) {
				sb_sql.append("[" + (i + 1) + "]" + queue.get(i) + ";\r\n"); //
			}
		}

		return sb_sql.toString(); //
	}

	// 杀掉服务器端某个线程,在处理一个长时间操作时,等待框需要干掉服务器端远程处理线程时需要这个..
	public int killServerThreadBySessionId(String _sessionID) throws Exception {
		Thread[] threads = SessionFactory.getInstance().getAllThreads(); // 取得当前所有线程
		int li_count = 0; //
		for (int i = 0; i < threads.length; i++) {
			if (threads[i] != null) { // 由于服务器端运行太在太快,会造成随时释放的情况，所以需要判断是否为空
				CurrSessionVO sessionVO = SessionFactory.getInstance().getClientEnv(threads[i]); //
				if (sessionVO != null && sessionVO.getHttpsessionid().equals(_sessionID)) {
					if (threads[i] != Thread.currentThread()) { // 如果是当前线程则不能杀,一定是某个堵死在那儿的线程..
						li_count++;
						try {
							System.out.println("当前线程[" + Thread.currentThread() + "],想杀的线程[" + threads[i] + "]"); //
							// 应该拿到那个想杀线程中的OutputStream对象，然后直接向客户端输出，然后直接关闭之,再设为空！
							// threads[i].stop(new WLTAppException("强行中断线程[" +
							// threads[i] + "]")); //
						} catch (Throwable ex) {
							ex.printStackTrace(); //
						}
						try {
							// threads[i].destroy(); //
						} catch (Throwable ex) {
							ex.printStackTrace(); //
						}
					}
				}
			}
		}
		return li_count;
	}

	public byte[] mouseClick(int _type, int _x, int _y) throws Exception {
		Robot robot = new Robot();
		robot.mouseMove(_x, _y); //
		if (_type == MouseEvent.BUTTON1) { // 左键
			robot.mousePress(InputEvent.BUTTON1_MASK); // 点击按下
			robot.mouseRelease(InputEvent.BUTTON1_MASK); //
		}

		if (_type == MouseEvent.BUTTON3) { // 右键
			robot.mousePress(InputEvent.BUTTON3_MASK); // 点击按下
			robot.mouseRelease(InputEvent.BUTTON3_MASK); //
		}

		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize(); // 取得屏幕大小
		Image screen = robot.createScreenCapture(new Rectangle(0, 0, screenDims.width, screenDims.height)).getScaledInstance(screenDims.width, screenDims.height, Image.SCALE_FAST); // 截取本机屏幕
		BufferedImage image = new BufferedImage(screen.getWidth(null), screen.getHeight(null), BufferedImage.TYPE_INT_ARGB); //
		Graphics g = image.getGraphics();
		g.drawImage(screen, 0, 0, null);
		g.dispose();
		ByteArrayOutputStream out = new ByteArrayOutputStream(); // 输出流!!
		ImageIO.write(image, "jpg", out); //
		byte[] bytes = out.toByteArray();
		out.close(); //
		return bytes; //
	}

	/**
	 * 取得所有数据源
	 */
	public DataSourceVO[] getDataSourceVOs() throws Exception {
		return ServerEnvironment.getInstance().getDataSourceVOs();
	}

	/**
	 * 取得服务器端资源文件..
	 * @param _url
	 * @return
	 * @throws Exception
	 */
	public String getServerResourceFile(String _url, String _charencoding) throws Exception {
		try {
			java.net.URL fileurl = this.getClass().getResource(_url); //

			File file = null;
			// 通过URL地址生成File对象，URL地址中含有如空格等非法字符时，直接取一下服务端绝对路径
			try {
				file = new File(fileurl.toURI()); //				
			} catch (Exception e) {
				e.printStackTrace();
				file = new File(fileurl.getPath());
			}
			byte[] fileByte = new byte[(int) file.length()]; // 根据文件对象的大小,新建一个数组
			FileInputStream myFileInputStream = new FileInputStream(file); // 根据文件对象,新建文件输入流
			myFileInputStream.read(fileByte); // 将文件输入流中的数据输入数组
			String str_file = new String(fileByte, _charencoding); // 根据字节数组,新建字符串,这个字符串就是文件中的内容
			myFileInputStream.close(); //
			return str_file; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			throw new WLTAppException("取资源文件[" + _url + "]失败!"); //
		}
	}

	/**
	 * 取得服务器端资源文件..
	 * @param _url
	 * @return
	 * @throws Exception
	 */
	public byte[] getServerResourceFile2(String _url, String _charencoding) throws Exception {
		try {
			java.net.URL fileurl = this.getClass().getResource(_url); //

			File file = null;
			// 通过URL地址生成File对象，URL地址中含有如空格等非法字符时，直接取一下服务端绝对路径
			try {
				file = new File(fileurl.toURI()); //				
			} catch (Exception e) {
				e.printStackTrace();
				file = new File(fileurl.getPath());
			}
			byte[] fileByte = new byte[(int) file.length()]; // 根据文件对象的大小,新建一个数组
			FileInputStream myFileInputStream = new FileInputStream(file); // 根据文件对象,新建文件输入流
			myFileInputStream.read(fileByte); // 将文件输入流中的数据输入数组
			myFileInputStream.close(); //
			return fileByte; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			throw new WLTAppException("取资源文件[" + _url + "]失败!"); //
		}
	}

	/**
	 * 写文件
	 * @param _filename
	 * @param _filecontent
	 * @param _charencoding
	 * @throws Exception
	 */
	public void writeFile(String _filename, String _filecontent, String _charencoding) throws Exception {
		FileOutputStream myFileOutputStream = new FileOutputStream(_filename, false); // 新建文件输出流,准备在当前目录下建一个aaa.txt,第二个参数表示是否是内容递增(即如果该文件存在,则在原文件后面增加新内容)
		myFileOutputStream.write(_filecontent.getBytes(_charencoding)); // 文件输出流输出一个字符串(必须先转换成byte)
		myFileOutputStream.close(); // 关闭文件输出流
	}

	/**
	 * 取得服务器端配置文件
	 * @return
	 */
	public String getServerConfigXML() {
		try {
			InputStream ins = this.getClass().getResourceAsStream("/weblight.xml"); //
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); //
			byte[] byts = new byte[1024]; //
			int len = -1;
			while ((len = ins.read(byts)) != -1) {
				baos.write(byts, 0, len); //
			}
			byte[] byteCodes = baos.toByteArray(); //
			ins.close(); // 关闭流..
			String str_file = new String(byteCodes, "GBK");
			return str_file; //
		} catch (Exception _ex) {
			return new TBUtil().getExceptionStringBuffer(_ex); //
		}
	}

	/**
	 * 取得服务器端的文件
	 * @param filename
	 * @return
	 */
	public String getServerFile(String filename) {
		java.net.URL fileurl = this.getClass().getResource("/" + filename); //

		try {
			File file = new File(fileurl.toURI()); //
			return file.getPath();
		} catch (URISyntaxException _ex) {

			return new TBUtil().getExceptionStringBuffer(_ex); //
		}
	}

	/**
	 * 取得服务器端配置文件
	 * @return
	 */
	public String getServerLog() throws Exception {
		String str_filepath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/_weblight_log/weblight.txt"; //
		try {
			File file = new File(str_filepath); //
			if (file.exists()) { //
				RandomAccessFile accessFile = new RandomAccessFile(file, "r"); // 只读打开.
				String str_linetext = null; //
				ArrayList al_text = new ArrayList(); //
				long ll_count = 1; //
				for (;;) {
					str_linetext = accessFile.readLine(); //
					if (str_linetext == null) {
						break;
					}
					al_text.add(ll_count + ">" + str_linetext); // 不停的增加.
					ll_count++; //
				}

				accessFile.close(); // 关闭

				// 输出最后100行日志数据
				int li_showlines = 100; // 需要显示倒数的多少行.
				StringBuffer sb_text = new StringBuffer(); //
				int li_fileline = al_text.size();
				if (li_fileline > li_showlines) { // 如果大于100行
					for (int i = 0; i < li_showlines; i++) {
						String str_text = (String) al_text.get(li_fileline - (li_showlines - i)); // //
						sb_text.append(new String(str_text.getBytes("ISO8859-1"), "GBK") + "\r\n"); // //
					}
				} else { // 如果小于100行,则全部输出
					for (int i = 0; i < li_fileline; i++) {
						String str_text = (String) al_text.get(i); // //
						sb_text.append(new String(str_text.getBytes("ISO8859-1"), "GBK") + "\r\n"); // //
					}
				}
				return sb_text.toString(); //
			} else {
				return "没有找到[" + str_filepath + "]这个文件!"; //
			}
		} catch (Exception _ex) {
			return new TBUtil().getExceptionStringBuffer(_ex); //
		}
	}

	/**
	 * 取得服务器端控制台信息..
	 * @return
	 * @throws Exception
	 */
	public String getServerConsole(boolean _isclear) throws Exception {
		Queue queue = ServerEnvironment.getServerConsoleQueue(); //
		StringBuffer sb_text = new StringBuffer();
		if (queue.isEmpty()) {
			return "当前服务器控制台内容为空,可能是被某人清空过了!"; //
		}

		if (_isclear) {
			while (!queue.isEmpty()) {
				sb_text.append("" + queue.pop());
			}
		} else {
			for (int i = 0; i < queue.size(); i++) {
				sb_text.append("" + queue.get(i));
			}
		}
		return sb_text.toString(); //
	}

	/**
	 * 取得各个数据源的当前池中的数量
	 * @return
	 * @throws Exception
	 */
	public String[][] getDatasourcePoolActiveNumbers() throws Exception {
		PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
		DataSourceVO[] dsVOs = ServerEnvironment.getInstance().getDataSourceVOs(); //
		String[][] str_return = new String[dsVOs.length][5]; // 一共4列,分别是数据源名称,描述,活动数,实例数
		for (int i = 0; i < dsVOs.length; i++) {
			GenericObjectPool pool = (GenericObjectPool) driver.getConnectionPool(dsVOs[i].getName()); //
			int li_active = pool.getNumActive();
			int li_idle = pool.getNumIdle(); //
			int li_maxactive = pool.getMaxActive(); // 最大活动数
			int li_maxdle = pool.getMaxIdle(); // 最大可用数
			str_return[i] = new String[] { dsVOs[i].getName(), dsVOs[i].getDescr(), "" + li_active, "空闲:" + li_idle + "/总共:" + (li_active + li_idle), "最大活动数:" + li_maxactive + "/最大实例:" + li_maxdle, "有效" }; //
		}
		return str_return; //
	}

	public Thread[] getAllRemoteCallServletThreads() {
		return SessionFactory.getInstance().getAllThreads(); //
	}

	/**
	 * 取得所有远程服务当前连接数
	 * @return
	 * @throws Exception
	 */
	public String[][] getRemoteServicePoolActiveNumbers() throws Exception {
		String[] str_services = ServicePoolFactory.getInstance().getAllServiceNames(); //
		String[][] str_return = new String[str_services.length][4]; // 一共4列,分别是数据源名称,描述,活动数,实例数
		for (int i = 0; i < str_services.length; i++) {
			GenericObjectPool pool = ServicePoolFactory.getInstance().getPool(str_services[i]); //
			int li_active = pool.getNumActive();
			int li_idle = pool.getNumIdle(); //
			int li_maxactive = pool.getMaxActive(); // 最大活动数
			int li_maxdle = pool.getMaxIdle(); //
			str_return[i] = new String[] { str_services[i], "" + li_active, "空闲:" + li_idle + "/总共:" + (li_active + li_idle), "最大活动数:" + li_maxactive + "/最大实例:" + li_maxdle, "<html><font color=\"blue\">有效</font></html>" }; //
		}
		return str_return; //
	}

	/*
	 * 取得默认数据源名称!!
	 * @see gxlu.nova.framework.common.ui.FrameWorkCommService#getDeaultDataSource()
	 */
	public String getDeaultDataSource() throws Exception {
		return ServerEnvironment.getDefaultDataSourceName();
	}

	/**
	 * 取得服务器端的系统属性
	 */
	public Properties getServerSystemProperties() throws Exception {
		return System.getProperties(); //
	}

	/**
	 * 取得服务器端的Environment数据
	 * @return
	 * @throws Exception
	 */
	public String[][] getServerEnvironment() throws Exception {
		return ServerEnvironment.getInstance().getAllData();
	}

	/**
	 * 取得系统当前在线用户!! 原来只生成人员编码,现在生成人员名称与所属机构名称..
	 */
	public String[][] getServerOnlineUser() throws Exception {
		HashMap mapUser = ServerEnvironment.getInstance().getLoginUserMap(); //
		String[] str_sesions = (String[]) mapUser.keySet().toArray(new String[0]); // 当前在线客户端的清单!!!
		String[][] str_return = new String[str_sesions.length][11]; // 定义返回数组
		String str_currtime = new TBUtil().getCurrTime(); // 当前时间
		String[] str_usercodes = new String[str_return.length]; //
		for (int i = 0; i < str_return.length; i++) {
			String[] str_onlineusers = (String[]) mapUser.get(str_sesions[i]); // 某一个用户的详细时间
			str_return[i][0] = str_onlineusers[0]; // SessionID
			str_return[i][1] = str_onlineusers[1]; // IP1
			str_return[i][2] = str_onlineusers[2]; // IP2

			str_return[i][3] = str_onlineusers[3]; // 用户code..
			str_usercodes[i] = str_onlineusers[3]; //

			str_return[i][6] = str_onlineusers[4]; // 登录时间.
			str_return[i][7] = str_onlineusers[5]; // 最后一次访问时间.

			long ll_time_used = betweenTwoTimeSecond(str_onlineusers[4], str_onlineusers[5]); // 最后访问时间-登录时间=已使用时间
			long ll_minute_used = ll_time_used / 60; //
			str_return[i][8] = ll_time_used + "秒," + ll_minute_used + "分"; //

			str_return[i][9] = str_currtime; // 当前时间
			long ll_time_fadai = betweenTwoTimeSecond(str_onlineusers[5], str_currtime); // 发呆时间
			long ll_minute_fadai = ll_time_fadai / 60; //
			if (ll_minute_fadai >= 15) {
				str_return[i][10] = "<html><font color=\"red\">" + ll_time_fadai + "秒," + ll_minute_fadai + "分</font></html>"; //
			} else {
				str_return[i][10] = "<html><font color=\"blue\">" + ll_time_fadai + "秒," + ll_minute_fadai + "分</font></html>"; //
			}
		}

		String str_incondition = new TBUtil().getInCondition(str_usercodes); //

		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select t1.code usercode,t1.code1 usercode2,t1.name username,t2.deptname from pub_user t1 left join v_pub_user_post_1 t2 on t1.id=t2.userid where t1.code in (" + str_incondition + ") or t1.code1 in (" + str_incondition + ")"); //
		HashMap map = new HashMap(); //
		for (int i = 0; i < hvs.length; i++) {
			map.put(hvs[i].getStringValue("usercode"), new String[] { hvs[i].getStringValue("usercode2"), hvs[i].getStringValue("username"), hvs[i].getStringValue("deptname") }); //
		}

		for (int i = 0; i < str_return.length; i++) {
			String[] str_username_deptname = (String[]) map.get(str_return[i][3]); //
			if (str_username_deptname != null) {
				str_return[i][3] = str_return[i][3] + "/" + (str_username_deptname[0] == null ? "无" : str_username_deptname[0]);
				str_return[i][4] = str_username_deptname[1];
				str_return[i][5] = str_username_deptname[2];
			}
		}
		return str_return; //
	}

	private long betweenTwoTimeSecond(String _date1, String _date2) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date1 = sdf.parse(_date1);
		java.util.Date date2 = sdf.parse(_date2);

		long li_second = (date2.getTime() - date1.getTime()) / 1000; // 秒
		return li_second; //
	}

	public String getInCondition(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getInCondition(_datasourcename, _sql); //
	}

	// 得到子查询!!!
	public String getSubSQLFromTempSQLTableByIDs(String[] _ids) throws Exception {
		CommDMO commDMO = new CommDMO();
		return commDMO.getSubSQLFromTempSQLTableByIDs(_ids); //
	}

	/**
	 * 取得系统所有的表名
	 * @param _datasourcename
	 * @return
	 * @throws Exception
	 */
	public String[] getAllSysTables(String _datasourcename, String _tableNamePattern) throws Exception {
		return new CommDMO().getAllSysTables(_datasourcename, _tableNamePattern); //
	}

	public String[][] getAllSysTableAndDescr(String _datasourcename, String _tableNamePattern, boolean _isContainView, boolean _isGetDescrFromXML) throws Exception {
		return new CommDMO().getAllSysTableAndDescr(_datasourcename, _tableNamePattern, _isContainView, _isGetDescrFromXML); //
	}

	/**
	 * 直接返回一个字符串
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public String getStringValueByDS(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getStringValueByDS(_datasourcename, _sql); //
	}

	// 根据一个SQL直接生成一个二维数组!!性能更高,因为数据结构是轻量级的!
	public String[][] getStringArrayByDS(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = ServerEnvironment.getCommDMO();
		return commDMO.getStringArrayByDS(_datasourcename, _sql); //
	}

	// 返回一个SQL生成二维结构的第一列,常用于生成子查询用
	public String[] getStringArrayFirstColByDS(String _datasourcename, String _sql) throws Exception {
		return new CommDMO().getStringArrayFirstColByDS(_datasourcename, _sql); //
	}

	// 根据一个SQL返回一个HashMap,将SQL中的第一列作为key,第二列作为value.
	public HashMap getHashMapBySQLByDS(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		return new CommDMO().getHashMapBySQLByDS(_datasourcename, _sql); //
	}

	public HashMap getHashMapBySQLByDS(String _datasourcename, String _sql, boolean _appendSameKey) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		return new CommDMO().getHashMapBySQLByDS(_datasourcename, _sql, _appendSameKey); //
	}

	public TableDataStruct getTableDataStructByDS(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}

		CommDMO commDMO = new CommDMO();
		return commDMO.getTableDataStructByDS(_datasourcename, _sql); //
	}

	public HashVO[] getHashVoArrayBySubSQL(String _datasourcename, String _parentsql, LinkForeignTableDefineVO[] _childVOs) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getHashVoArrayBySubSQL(_datasourcename, _parentsql, _childVOs);
	}

	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getHashVoStructByDS(_datasourcename, _sql);
	}

	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql, int _topRecords) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getHashVoStructByDS(_datasourcename, _sql, _topRecords);
	}

	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getHashVoArrayByDS(_datasourcename, _sql);
	}

	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql, int _topRecords) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getHashVoArrayByDS(_datasourcename, _sql, _topRecords);
	}

	// 返回树型结构的hashVO
	public HashVO[] getHashVoArrayAsTreeStructByDS(String _datasourcename, String _sql, String _idField, String _parentIDField, String _seqField, String _rootNodeCondition) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getHashVoArrayAsTreeStructByDS(_datasourcename, _sql, _idField, _parentIDField, _seqField, _rootNodeCondition);
	}

	public HashVO[] getTreePathVOsByOneRecord(String _datasourcename, String _tableName, String _idFieldName, String _parentIdFieldName, String _whereField, String _whereCondition) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getTreePathVOsByOneRecord(_datasourcename, _tableName, _idFieldName, _parentIdFieldName, _whereField, _whereCondition); // //
	}

	public HashMap getTreePathNameByRecords(String _datasourcename, String _tableName, String _idFieldName, String _nameFieldName, String _parentIdFieldName, String[] _idValues) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getTreePathNameByRecords(_datasourcename, _tableName, _idFieldName, _nameFieldName, _parentIdFieldName, _idValues); // //
	}

	public HashVO[] getTreeChildVOsByOneRecord(String _datasourcename, String _tableName, String _idFieldName, String _parentIdFieldName, String _whereCondition) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getTreeChildVOsByOneRecord(_datasourcename, _tableName, _idFieldName, _parentIdFieldName, _whereCondition); //
	}

	public Vector getHashVoArrayReturnVectorByDS(String _datasourcename, String[] _sqls) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getHashVoArrayReturnVectorByDS(_datasourcename, _sqls); //
	}

	public Vector getHashVoStructReturnVectorByDS(String _datasourcename, String[] _sqls) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getHashVoStructReturnVectorByDS(_datasourcename, _sqls); //
	}

	public HashMap getHashVoArrayReturnMapByDS(String _datasourcename, String[] _sqls, String[] _keys) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getHashVoArrayReturnMapByDS(_datasourcename, _sqls, _keys); //
	}

	public Integer executeUpdateByDS(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return new Integer(commDMO.executeUpdateByDS(_datasourcename, _sql)); //
	}

	public Integer executeUpdateByDSPS(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		int[] ret = commDMO.executeBatchByDS(_datasourcename, Arrays.asList(new String[] { _sql }), true, true, true);
		if (ret != null && ret.length > 0) {
			return new Integer(ret[0]); //
		}
		return null;
	}

	//立即提交
	public Integer executeUpdateByDSAutoCommit(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.executeUpdateByDSAutoCommit(_datasourcename, _sql); //
	}

	// 执行一条SQL
	public Integer executeUpdateByDS(String _datasourcename, SQLBuilderIfc _sqlBuilder) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return new Integer(commDMO.executeUpdateByDS(_datasourcename, _sqlBuilder)); //
	}

	public Integer executeMacroUpdateByDS(String _datasourcename, String _sql, String[] _colvalues) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return new Integer(commDMO.executeMacroUpdateByDS(_datasourcename, _sql, _colvalues)); //
	}

	public void executeBatchByDS(String _datasourcename, String[] _sqls) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		commDMO.executeBatchByDS(_datasourcename, _sqls); //
	}

	public void executeBatchByDS(String _datasourcename, java.util.List _sqllist, boolean _isDebugLog, boolean _isDBLog) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		commDMO.executeBatchByDS(_datasourcename, _sqllist, _isDebugLog, _isDBLog); //
	}

	public void executeBatchByDSNoLog(String _datasourcename, String _sqls) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		commDMO.executeBatchByDSNoLog(_datasourcename, _sqls); //
	}

	// 执行一批SQL
	public void executeBatchByDS(String _datasourcename, SQLBuilderIfc[] _sqlBuilders) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		commDMO.executeBatchByDS(_datasourcename, _sqlBuilders); //
	}

	public void executeBatchByDS(String _datasourcename, java.util.List _sqllist) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		commDMO.executeBatchByDS(_datasourcename, _sqllist); //
	}

	// 取一批主键回来
	public Long[] getSequenceNextValByDS(String _datasourcename, String _sequenceName, Integer _batch) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}

		long[] ll_return = new CommDMO().getSequenceBatchNextValByDS(_datasourcename, _sequenceName, _batch.intValue()); //
		Long[] ll_return2 = new Long[ll_return.length]; //
		for (int i = 0; i < ll_return2.length; i++) {
			ll_return2[i] = new Long(ll_return[i]); //
		}
		return ll_return2; //
	}

	public void callProcedureByDS(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		commDMO.callProcedureByDS(_datasourcename, procedureName, parmeters, -1);
	}

	public void callProcedureByDSSqlServer(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		commDMO.callProcedureByDSSqlServer(_datasourcename, procedureName, parmeters);
	}

	public String callProcedureReturnStrByDS(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.callProcedureReturnStrByDS(_datasourcename, procedureName, parmeters); //
	}

	public String callFunctionReturnStrByDS(String _datasourcename, String functionName, String[] parmeters) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.callFunctionReturnStrByDS(_datasourcename, functionName, parmeters); //
	}

	public String[][] callFunctionReturnTableByDS(String _datasourcename, String functionName, String[] parmeters) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.callFunctionReturnTableByDS(_datasourcename, functionName, parmeters); //
	}

	//直接上传,而且前面有"N12345_"前辍,这样可以保证唯一性!
	public String uploadFile(ClassFileVO _vo) throws Exception {
		return uploadFile(_vo, true); //
	}

	//可以不加前辍上传
	public String uploadFile(ClassFileVO _fileVO, boolean _isChangeFileName) throws Exception {
		boolean isConvertHex = true; //
		boolean isAddSerialNo = true; //
		if (_isChangeFileName) {
			isConvertHex = true; //
			isAddSerialNo = true; //
		} else {
			isConvertHex = false; //
			isAddSerialNo = false; //
		}
		return new BSUtil().uploadFile(_fileVO, null, null, false, isConvertHex, isAddSerialNo); //
	}

	/**
	 * 上传文件!! 不要再有什么再搞一个上传文件方法了!
	 */
	public String upLoadFile(ClassFileVO _fileVO, String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir, boolean _isConvertHex, boolean _isAddSerialNo) throws Exception {
		return new BSUtil().uploadFile(_fileVO, _serverdir, _serverFileName, _isAbsoluteSeverDir, _isConvertHex, _isAddSerialNo); //
	}

	//标准的目录下载
	public ClassFileVO downloadFile(String _filename) throws Exception {
		return new BSUtil().downLoadFile(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/", _filename, true);
	}

	//根据绝对路径下载
	public ClassFileVO downloadToClientByAbsolutePath(String _filename) throws Exception {
		TBUtil tbutil = new TBUtil(); // 
		_filename = tbutil.replaceAll(_filename, "\\", "/"); //
		String str_dir = _filename.substring(0, _filename.lastIndexOf("/")); //
		String str_fileName = _filename.substring(_filename.lastIndexOf("/") + 1, _filename.length()); //
		return new BSUtil().downLoadFile(str_dir, str_fileName, true);
	}

	/**
	 * 从服务器端下载文件返回ClassFileVO
	 * @param _serverdir
	 * @param _serverFileName
	 * @param _isAbsoluteSeverDir
	 * @return
	 * @throws Exception
	 */
	public ClassFileVO downLoadFile(String _serverdir, String fileName, boolean absoluteServerDir) throws Exception {
		return new BSUtil().downLoadFile(_serverdir, fileName, absoluteServerDir);
	}

	public String[] getImageFileNames() throws Exception {
		return ServerEnvironment.getInstance().getImagesNames();
	}

	public ImageIcon getImageFromServerRespath(String _path) throws Exception {
		return new ImageIcon(this.getClass().getResource(_path)); //
	}

	/**
	 * 加密算法给某一个表的某一个字段加密
	 * @param _oldtablename
	 * @param _column
	 * @param _newtablename
	 * @param primarykey
	 * @throws Exception
	 */
	public void getJoinCipher(String _oldtablename, String _column, String _newtablename, String primarykey) {
		try {
			CommDMO commdmo = new CommDMO();
			HashVO[] hvs = commdmo.getHashVoArrayByDS(null, "select * from " + _oldtablename + ""); //
			for (int i = 0; i < hvs.length; i++) {
				String str_oldtext = hvs[i].getStringValue(_column);
				String str_img64code = new String(org.jfreechart.xml.util.Base64.encode(str_oldtext.getBytes("GBK"))); //jfreechart-1.0.14升级修改类路径jfree为jfreechart 【杨科/2013-06-09】
				commdmo.executeUpdateByDS(null, "update " + _newtablename + " set " + _column + " ='" + str_img64code + "' where " + primarykey + "='" + hvs[i].getStringValue(primarykey) + "'");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回平台参数配置
	 */
	public String[][] getAllPlatformOptions() throws Exception {
		return SystemOptions.getAllPlatformOptions();
	}

	/**
	 * 返回平台参数配置
	 */
	public String[][] getAllPlatformOptions(String _type) throws Exception {
		String[][] str_allOptions = SystemOptions.getAllPlatformOptions(); //所有参数
		ArrayList al_data = new ArrayList(); //
		for (int i = 0; i < str_allOptions.length; i++) {
			if (str_allOptions[i][0].equals(_type)) { //
				al_data.add(str_allOptions[i]); //
			}
		}
		String[][] str_data = (String[][]) al_data.toArray(new String[0][0]); //
		return str_data; //
	}

	/**
	 * 返回数据库参数配置
	 */
	public String[][] getAllOptions() throws Exception {
		return SystemOptions.getAllOptions();
	}

	// 刷新机构分类和机构类型缓存【李春娟/2016-05-25】
	public void refreshCorptypeFromDB() throws Exception {
		ServerCacheDataFactory.static_vos_corptypedef = getCommDMO().getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('机构分类','机构类型')"); // //
	}

	// 重新加载sysoption数据至缓存!!
	public String[][] reLoadDataFromDB(boolean _isJudgeTable) throws Exception {
		return SystemOptions.getInstance().reLoadDataFromDB(_isJudgeTable); //
	}

	public String getSysOptionStringValue(String _key, String _nvl) throws Exception {
		return SystemOptions.getStringValue(_key, _nvl);
	}

	public int getSysOptionIntegerValue(String _key, int _nvl) throws Exception {
		return SystemOptions.getIntegerValue(_key, _nvl);
	}

	public boolean getSysOptionBooleanValue(String _key, boolean _nvl) throws Exception {
		return SystemOptions.getBooleanValue(_key, _nvl);
	}

	public String getSysOptionHashItemStringValue(String _key, String _itemkey, String _nvl) throws Exception {
		return SystemOptions.getHashItemStringValue(_key, _itemkey, _nvl); //
	}

	public int getSysOptionHashItemIntegerValue(String _key, String _itemkey, int _nvl) throws Exception {
		return SystemOptions.getHashItemIntegerValue(_key, _itemkey, _nvl); //
	}

	public boolean getSysOptionHashItemBooleanValue(String _key, String _itemkey, boolean _nvl) throws Exception {
		return SystemOptions.getHashItemBooleanValue(_key, _itemkey, _nvl); //
	}

	/**
	 * 缓存里参数配置
	 */
	public HashMap getOptionsHashMap() throws Exception {
		return SystemOptions.getDataMap();
	}

	public void setOptions(HashMap _hashMap) throws Exception {
		SystemOptions.setDataMap(_hashMap);
	}

	public String encryptStr(String _str) throws Exception {
		return new DESKeyTool().encrypt(_str); //
	}

	/**
	 * 服务器word或excel文件是否含有关键字
	 * @param _fileNames 取数据库保存文件名
	 * @param _keywords 为关键字
	 * @param isAllContain 判断文件是否同时包含所有关键字
	 * @return
	 * @throws Exception
	 */
	public List<String> checkWordOrExcelContainKeys(String _uploadfiledir, List _filenames, String[] _keywords, boolean _isAllContain) throws Exception {
		BSUtil bsUtil = new BSUtil(); //
		return bsUtil.checkWordOrExcelContainKeys(_uploadfiledir, _filenames, _keywords, _isAllContain);
	}

	/**
	 * 全文检索逻辑
	 */
	public List<String> checkWordOrExcelContainKeys(String _serverdir, String[][] _filesInfo, String[] _keywords, boolean _isAllContain) throws Exception {
		List<String> list_ids = new ArrayList<String>();
		boolean isSearchWithIndex = getSysOptionBooleanValue("全文检索是否采用索引方式", false);
		if (isSearchWithIndex) {
			System.out.println("☆定义[全文检索是否采用索引方式]为\"TRUE\", 系统开始使用索引方式执行全文检索......");
			long li_1 = System.currentTimeMillis();
			String flag = "and";
			if (_isAllContain) {
				flag = "or";
			}

			StringBuffer sql = new StringBuffer();
			sql.append("select ruleid, contentfilename from rulecontentindex where ");
			sql.append("(");
			sql.append("(index0 like '%" + _keywords[0] + "%' or index1 like '%" + _keywords[0] + "%' or index2 like '%" + _keywords[0] + "%' or index3 like '%" + _keywords[0] + "%' or ");
			sql.append("index4 like '%" + _keywords[0] + "%' or index5 like '%" + _keywords[0] + "%' or index6 like '%" + _keywords[0] + "%' or index7 like '%" + _keywords[0] + "%' or ");
			sql.append("index8 like '%" + _keywords[0] + "%' or index9 like '%" + _keywords[0] + "%')");
			for (int j = 1; j < _keywords.length; j++) {
				sql.append(" " + flag);
				sql.append(" (index0 like '%" + _keywords[j] + "%' or index1 like '%" + _keywords[j] + "%' or index2 like '%" + _keywords[j] + "%' or index3 like '%" + _keywords[j] + "%' or ");
				sql.append("index4 like '%" + _keywords[j] + "%' or index5 like '%" + _keywords[j] + "%' or index6 like '%" + _keywords[j] + "%' or index7 like '%" + _keywords[j] + "%' or ");
				sql.append("index8 like '%" + _keywords[j] + "%' or index9 like '%" + _keywords[j] + "%')");
			}
			sql.append(")");

			HashMap<String, String> hmap_index = getHashMapBySQLByDS(null, sql.toString());
			if (hmap_index != null || hmap_index.size() > 0) { // 如果一条符合条件的索引都没有,
				// 直接就不用判断了!
				for (int i = 0; i < _filesInfo.length; i++) {
					if (_filesInfo[i][1] == null || _filesInfo[i][1].trim().equals("")) { // 没有指定正文,
						// 直接略过!!
						continue;
					}
					String filename = hmap_index.get(_filesInfo[i][0]);
					if (filename != null && _filesInfo[i][1].trim().equals(filename)) { // 存在该制度的索引,
						// 并且是同样名称的文件!
						list_ids.add(_filesInfo[i][0]);
					}
				}
			}

			long li_2 = System.currentTimeMillis();
			System.out.println("☆全文检索执行完毕, 输入的文件数[" + _filesInfo.length + "], 返回文件数[" + list_ids.size() + "], 共耗时[" + (li_2 - li_1) + "]毫秒");
			return list_ids;
		} else {
			String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //刘旋飞加的全文检索逻辑!
			if (str_newWebUrl != null) {
				try {
					System.out.println("定义了远程全文检索的地址[" + str_newWebUrl + "], 系统开始执行远程全文检索......");
					long li_1 = System.currentTimeMillis();
					TBUtil tbUtil = new TBUtil();
					HashMap requestMap = new HashMap();
					requestMap.put("Action", "fullSearch"); //全文检索!
					requestMap.put("ServerDir", _serverdir);
					requestMap.put("JvmUpLimit", "900");
					requestMap.put("FilesInfo", _filesInfo);
					requestMap.put("KeyWords", _keywords);
					requestMap.put("isAllContain", _isAllContain);
					URL url = new URL(str_newWebUrl + "/FullTextSearchServlet");
					URLConnection conn = url.openConnection();
					if (str_newWebUrl.startsWith("https")) {
						new BSUtil().addHttpsParam(conn);
					}
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setUseCaches(false);
					tbUtil.writeBytesToOutputStream(conn.getOutputStream(), tbUtil.serialize(requestMap));
					HashMap responseMap = (HashMap) tbUtil.deserialize(tbUtil.readFromInputStreamToBytes(conn.getInputStream()));
					Object obj_return = responseMap.get("SearchResult");
					long li_2 = System.currentTimeMillis();
					if (obj_return instanceof Exception) {
						throw (Exception) obj_return;
					} else {
						System.out.println("从远程全文检索的地址[" + str_newWebUrl + "]取数成功,输入的文件数[" + _filesInfo.length + "],返回文件数[" + ((ArrayList) obj_return).size() + "],共耗时[" + (li_2 - li_1) + "]");
						return (ArrayList) obj_return;
					}
				} catch (Exception e) {
					throw new WLTAppException(e.getMessage());
				}
			} else {
				// 就在这里做多线程处理...
				List<MyThread_c> list_threads = new ArrayList<MyThread_c>();
				int t_count = 0; // 分成几组
				if (_filesInfo.length <= 100) {
					t_count = 1;
				} else if (_filesInfo.length < 500) {
					t_count = 5;
				} else if (_filesInfo.length < 1000) {
					t_count = 10;
				} else {
					t_count = 20;
				}

				int number = _filesInfo.length / t_count; // 一组多少个
				for (int i = 0; i < t_count; i++) { // 分多少组, 就创建多少个线程,
					// 最后一个线程执行的数据量大于等于number,
					// 并且小于2*number,
					// 故相对于其他线程会花更多的时间
					// System.out.println("第" + i + "组, 从" + (i*number) + "到" +
					// ((i+1)==t_count?_filesInfo.length:(i+1)*number) + ":");
					MyThread_c t_c = new MyThread_c(list_ids, _serverdir, sub2DStr(_filesInfo, i * number, (i + 1) == t_count ? _filesInfo.length : (i + 1) * number), _keywords, _isAllContain);
					t_c.setName("MyThread_c-" + i); // 取个名字, 以便于监控
					list_threads.add(t_c); // 添加到监控中...
					t_c.start(); // 开始执行
				}
				while (true) {
					synchronized (list_ids) {
						boolean allTerminate = true;
						for (int i = 0; i < list_threads.size(); i++) {
							if (list_threads.get(i).isTerminate() == 1) { // 尚有线程未终止
								// System.out.println("> 线程" +
								// list_threads.get(i).getName() + "尚未终止...");
								allTerminate = false;
								break;
							}
						}
						if (allTerminate) { // 所有线程都终止
							// System.out.println("> 所有线程已经终止...");
							return list_ids;
						} else {
							try {
								Thread.currentThread().sleep(1000); // 每秒执行一次
							} catch (Exception e) {
								System.out.println("FF:监控线程sleep时出现异常!!");
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	// 截取二维数组
	public static String[][] sub2DStr(String[][] _2dStr, int _begin, int _end) {
		String[][] dStr = new String[_end - _begin][_2dStr[_begin].length];
		for (int i = 0; i < dStr.length; i++) {
			for (int j = 0; j < dStr[0].length; j++) {
				dStr[i][j] = _2dStr[_begin + i][j];
			}
		}
		return dStr;
	}

	/**
	 * 重新启动所有定制的JOB
	 */
	public String restartJobs(String _primarykey) throws Exception {
		StringBuffer message = new StringBuffer();
		// 首先, 销毁运行中的线程
		if (getHmJob().size() > 0) {
			message.append("停止以下JOB:\r\n");
			Iterator<String> it = getHmJob().keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				WLTJobTimer job = getHmJob().get(key);
				if (job != null && job.isAlive()) {
					job.closeme();
					message.append("【" + key.substring(key.indexOf("_") + 1) + "】");
				}
			}
		}
		getHmJob().clear();
		// 然后, 重新获取需要启动的任务
		HashVO[] hv_jobs = getCommDMO().getHashVoArrayByDS(null, "select * from PUB_JOB where ACTIVEFLAG = 'Y'");
		if (hv_jobs == null || hv_jobs.length <= 0) {
			if (message.length() <= 0) {
				return "没有任何JOB可停止或启动！";
			} else {
				return message.toString();
			}
		}
		message.append("\r\n启动以下JOB:\r\n");
		for (int i = 0; i < hv_jobs.length; i++) {
			WLTJobTimer job = new WLTJobTimer(hv_jobs[i]);
			getHmJob().put("JOB_" + hv_jobs[i].getStringValue(_primarykey, ""), job);
			job.start(); // 启动
			message.append("【" + hv_jobs[i].getStringValue(_primarykey, "") + "】");
		}
		return message.toString();
	}

	/**
	 * 停止某一个JOB
	 */
	public String closeJob(String _pkValue) throws Exception {
		WLTJobTimer job = getHmJob().get("JOB_" + _pkValue);
		if (job != null) {
			if (job.isAlive()) {
				job.closeme();
				getHmJob().remove("JOB_" + _pkValue);
				return "JOB停止, 成功!";
			} else {
				return "JOB已经停止, 请勿重复操作!";
			}
		} else {
			return "JOB未曾启动, 无法停止!";
		}
	}

	private HashMap<String, WLTJobTimer> getHmJob() {
		if (hm_jobs == null) {
			hm_jobs = new HashMap<String, WLTJobTimer>();
		}
		return hm_jobs;
	}

	public String[][] getTableItemAndDescr(String table) throws Exception {
		return new InstallDMO().getAllIntallTabColumnsDescr(table);
	}

	public BillCellVO parseCellTempetToWord(BillCellVO cellTemplet, HashVO baseHVO) throws Exception {
		return new UseCellTempletParseUtil().onParse(cellTemplet, baseHVO);
	}
	public String lookJobState(String _jobName) throws Exception {
		return cn.com.infostrategy.bs.common.WLTJobTimer.lookJobState(_jobName);
	}

	public String startJob(String _jobName) throws Exception {
		return cn.com.infostrategy.bs.common.WLTJobTimer.startJob(_jobName);
	}

	public String stopJob(String _jobName) throws Exception {
		return cn.com.infostrategy.bs.common.WLTJobTimer.stopJob(_jobName);
	}
}

/**
 * 应用于正文查询的(多)线程类
 * @author My Dream
 */
class MyThread_c extends Thread {
	private List<String> list_ids;
	private String uploadfiledir;
	private String[][] filesInfo;
	private String[] keywords;
	private boolean isAllContain;
	private int state = 1; // 线程状态 1-未终止、0-已终止

	public MyThread_c(List<String> _ids, String _uploadfiledir, String[][] _filesInfo, String[] _keywords, boolean _isAllContain) {
		this.list_ids = _ids; // 将所有线程都指向此数组
		this.uploadfiledir = _uploadfiledir;
		this.filesInfo = _filesInfo;
		this.keywords = _keywords;
		this.isAllContain = _isAllContain;
	}

	public void run() {
		BSUtil bsUtil = new BSUtil();
		List<String> ids = bsUtil.checkWordOrExcelContainKeys(uploadfiledir, filesInfo, keywords, isAllContain);
		for (int i = 0; i < ids.size(); i++) {
			synchronized (list_ids) { // 线程同步 可以吗? 测试一下看看....
				list_ids.add(ids.get(i));
			}
		}
		state = 0;
	}

	public int isTerminate() {
		return state;
	}
}
