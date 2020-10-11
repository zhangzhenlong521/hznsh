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
 * ƽ̨ͨ�÷���,�����϶��ǲ�ѯ���ݿ��,�������ݿ�,ִ��SQL,�洢���̵�!!
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
	 * Ĭ�Ϲ��췽��
	 */
	public FrameWorkCommServiceImpl() {
	}

	/**
	 * ͨ�õ��÷���!!
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
	} // ����Զ�̵����쳣

	/**
	 * ȡ�����г�ʼ������
	 */
	public InitParamVO[] getInitParamVOs() throws Exception {
		return ServerEnvironment.getInstance().getInitParamVOs();
	}

	/**
	 * ȡ��Lof4j������Ϣ
	 */
	public Log4jConfigVO getLog4jConfigVO() throws Exception {
		return ServerEnvironment.getInstance().getLog4jConfigVO(); //
	}

	public Hashtable getLanguage() throws Exception // һ����ȡ����������
	{
		CommDMO dmo = ServerEnvironment.getCommDMO();
		String str_sql = "select simplechinese,english,traditionalchinese from pub_language"; //
		String[][] str_data = dmo.getStringArrayByDS(null, str_sql);

		Hashtable ht = new Hashtable();
		if (str_data != null && str_data.length > 0) {
			for (int i = 0; i < str_data.length; i++) {
				ht.put(str_data[i][0], str_data[i]); // ��������
			}
		}
		return ht;
	}

	public String[] getLanguage(String _key) throws Exception // ����ĳһ��keyȡ�ø�key��Ӧ�ĸ�������!!
	{
		CommDMO dmo = ServerEnvironment.getCommDMO();
		String str_sql = "select simplechinese,english,traditionalchinese from pub_language where simplechinese='" + _key + "'"; //
		String[][] str_data = dmo.getStringArrayByDS(null, str_sql);
		if (str_data != null && str_data.length > 0) {
			return str_data[0]; // ����ҵ�,�򷵻�֮
		} else // ���û�ҵ�,��ֱ���������һ����¼
		{
			String str_new_id = dmo.getSequenceNextValByDS(null, "s_pub_language"); //
			String str_insertsql = "insert into pub_language (id,simplechinese,english,traditionalchinese) values ('" + str_new_id + "','" + _key + "','" + _key + "','" + _key + "')";
			dmo.executeUpdateByDS(null, str_insertsql);
			return new String[] { _key, _key, _key }; //
		}
	}

	/**
	 * ȡ�÷������˵ĵ�ǰ����
	 * @return
	 */
	public String getServerCurrDate() throws Exception {
		return new TBUtil().getCurrDate(); // ���ط������˵�ǰ����
	}

	/**
	 * ȡ�÷������˵ĵ�ǰ����
	 * @return
	 */
	public String getServerCurrTime() throws Exception {
		return new TBUtil().getCurrTime(); // ���ط������˵�ǰʱ��
	}

	public long getServerCurrTimeLongValue() {
		return System.currentTimeMillis(); //
	}

	/**
	 * ȡ��ϵͳ����
	 * @return
	 * @throws Exception
	 */
	public String[][] getServerSysOptions() throws Exception {
		return SystemOptions.getAllOptions(); //
	}

	/**
	 * ��Ϣ����
	 */
	public Integer sleep(Integer _second) throws Exception { // ��Ϣ����
		Thread.currentThread().sleep(_second.intValue() * 1000); //
		System.out.println("��Ϣ��[" + _second + "]��,[" + Thread.currentThread().getName() + "]"); //
		return _second;
	}

	/**
	 * ʹ�û�����,ץȡ����������Ļ�ķ���,����һ��ͼ��
	 * @return
	 * @throws Exception
	 */
	public byte[] captureScreen() throws Exception // ץȡ����������Ļ�ķ���
	{
		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize(); // ȡ����Ļ��С
		Image screen = new Robot().createScreenCapture(new Rectangle(0, 0, screenDims.width, screenDims.height)).getScaledInstance(screenDims.width, screenDims.height, Image.SCALE_FAST); // ��ȡ������Ļ
		BufferedImage image = new BufferedImage(screen.getWidth(null), screen.getHeight(null), BufferedImage.TYPE_INT_ARGB); //
		Graphics g = image.getGraphics();
		g.drawImage(screen, 0, 0, null);
		g.dispose();
		ByteArrayOutputStream out = new ByteArrayOutputStream(); // �����!!
		ImageIO.write(image, "jpg", out); //
		byte[] bytes = out.toByteArray();
		out.close(); //
		return bytes; //
	}

	// ����ĳһ���Ự��SQL������̫ƽ���ŵļ��ϵͳ������ϵͳԶ�̱����Ԥ����Ϊ����Ԥ���������޸ķ�����ʾ��Ϣ�����/2018-07-25��
	public String addSessionSQLListener(String _sessionid) throws Exception {
		if (ServerEnvironment.getSessionSqlListenerMap().containsKey(_sessionid)) {
			Queue queue = (Queue) ServerEnvironment.getSessionSqlListenerMap().get(_sessionid); //
			return "�ûỰ��ע�����,��������[" + queue.size() + "]��SQL!"; //
		}

		if (!ServerEnvironment.getLoginUserMap().containsKey(_sessionid)) {
			return "��SessionID��Ч,��Ϊϵͳ��û���ָ��û�����!"; //
		}

		ServerEnvironment.getSessionSqlListenerMap().put(_sessionid, new Queue(1000)); // ��ֹ�ڴ����,ÿ���Ự���ֻ�洢���µ�1000��SQL.
		return "ע��ûỰ��SQL�����ɹ�!!�����ڿ�ʼ���������������¼�ûỰÿһ��ִ�е�SQL!!";
	}

	// ɾ��ĳһ���Ự��SQL������̫ƽ���ŵļ��ϵͳ������ϵͳԶ�̱����Ԥ����Ϊ����Ԥ���������޸ķ�����ʾ��Ϣ�����/2018-07-25��
	public String removeSessionSQLListener(String _sessionid) throws Exception {
		if (!ServerEnvironment.getSessionSqlListenerMap().containsKey(_sessionid)) {
			return "�ûỰ��û��ע���,�Ƴ�������Ч!"; //
		}

		Queue queue = (Queue) ServerEnvironment.getSessionSqlListenerMap().get(_sessionid); //
		if (queue != null) {
			queue.clear(); //
		}
		ServerEnvironment.getSessionSqlListenerMap().remove(_sessionid); //
		return "ɾ���ûỰ��SQL�����ɹ�,�����ڿ�ʼ�����������ٶԸûỰִ�е�SQL���м������¼!!!";
	}

	// ȡ�ü�����SQL���������
	public String getSessionSQLListenerText(String _sessionid, boolean _isclear) throws Exception {
		Queue queue = (Queue) ServerEnvironment.getSessionSqlListenerMap().get(_sessionid); //
		if (queue == null) {
			return "[" + new TBUtil().getCurrTime() + "]û��ע��SessionSQL������,����ע�����!!\r\n"; //
		}

		if (queue.isEmpty()) {
			return "[" + new TBUtil().getCurrTime() + "]û�з���һ������!!\r\n"; //
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

	// ɱ����������ĳ���߳�,�ڴ���һ����ʱ�����ʱ,�ȴ�����Ҫ�ɵ���������Զ�̴����߳�ʱ��Ҫ���..
	public int killServerThreadBySessionId(String _sessionID) throws Exception {
		Thread[] threads = SessionFactory.getInstance().getAllThreads(); // ȡ�õ�ǰ�����߳�
		int li_count = 0; //
		for (int i = 0; i < threads.length; i++) {
			if (threads[i] != null) { // ���ڷ�����������̫��̫��,�������ʱ�ͷŵ������������Ҫ�ж��Ƿ�Ϊ��
				CurrSessionVO sessionVO = SessionFactory.getInstance().getClientEnv(threads[i]); //
				if (sessionVO != null && sessionVO.getHttpsessionid().equals(_sessionID)) {
					if (threads[i] != Thread.currentThread()) { // ����ǵ�ǰ�߳�����ɱ,һ����ĳ���������Ƕ����߳�..
						li_count++;
						try {
							System.out.println("��ǰ�߳�[" + Thread.currentThread() + "],��ɱ���߳�[" + threads[i] + "]"); //
							// Ӧ���õ��Ǹ���ɱ�߳��е�OutputStream����Ȼ��ֱ����ͻ��������Ȼ��ֱ�ӹر�֮,����Ϊ�գ�
							// threads[i].stop(new WLTAppException("ǿ���ж��߳�[" +
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
		if (_type == MouseEvent.BUTTON1) { // ���
			robot.mousePress(InputEvent.BUTTON1_MASK); // �������
			robot.mouseRelease(InputEvent.BUTTON1_MASK); //
		}

		if (_type == MouseEvent.BUTTON3) { // �Ҽ�
			robot.mousePress(InputEvent.BUTTON3_MASK); // �������
			robot.mouseRelease(InputEvent.BUTTON3_MASK); //
		}

		Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize(); // ȡ����Ļ��С
		Image screen = robot.createScreenCapture(new Rectangle(0, 0, screenDims.width, screenDims.height)).getScaledInstance(screenDims.width, screenDims.height, Image.SCALE_FAST); // ��ȡ������Ļ
		BufferedImage image = new BufferedImage(screen.getWidth(null), screen.getHeight(null), BufferedImage.TYPE_INT_ARGB); //
		Graphics g = image.getGraphics();
		g.drawImage(screen, 0, 0, null);
		g.dispose();
		ByteArrayOutputStream out = new ByteArrayOutputStream(); // �����!!
		ImageIO.write(image, "jpg", out); //
		byte[] bytes = out.toByteArray();
		out.close(); //
		return bytes; //
	}

	/**
	 * ȡ����������Դ
	 */
	public DataSourceVO[] getDataSourceVOs() throws Exception {
		return ServerEnvironment.getInstance().getDataSourceVOs();
	}

	/**
	 * ȡ�÷���������Դ�ļ�..
	 * @param _url
	 * @return
	 * @throws Exception
	 */
	public String getServerResourceFile(String _url, String _charencoding) throws Exception {
		try {
			java.net.URL fileurl = this.getClass().getResource(_url); //

			File file = null;
			// ͨ��URL��ַ����File����URL��ַ�к�����ո�ȷǷ��ַ�ʱ��ֱ��ȡһ�·���˾���·��
			try {
				file = new File(fileurl.toURI()); //				
			} catch (Exception e) {
				e.printStackTrace();
				file = new File(fileurl.getPath());
			}
			byte[] fileByte = new byte[(int) file.length()]; // �����ļ�����Ĵ�С,�½�һ������
			FileInputStream myFileInputStream = new FileInputStream(file); // �����ļ�����,�½��ļ�������
			myFileInputStream.read(fileByte); // ���ļ��������е�������������
			String str_file = new String(fileByte, _charencoding); // �����ֽ�����,�½��ַ���,����ַ��������ļ��е�����
			myFileInputStream.close(); //
			return str_file; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			throw new WLTAppException("ȡ��Դ�ļ�[" + _url + "]ʧ��!"); //
		}
	}

	/**
	 * ȡ�÷���������Դ�ļ�..
	 * @param _url
	 * @return
	 * @throws Exception
	 */
	public byte[] getServerResourceFile2(String _url, String _charencoding) throws Exception {
		try {
			java.net.URL fileurl = this.getClass().getResource(_url); //

			File file = null;
			// ͨ��URL��ַ����File����URL��ַ�к�����ո�ȷǷ��ַ�ʱ��ֱ��ȡһ�·���˾���·��
			try {
				file = new File(fileurl.toURI()); //				
			} catch (Exception e) {
				e.printStackTrace();
				file = new File(fileurl.getPath());
			}
			byte[] fileByte = new byte[(int) file.length()]; // �����ļ�����Ĵ�С,�½�һ������
			FileInputStream myFileInputStream = new FileInputStream(file); // �����ļ�����,�½��ļ�������
			myFileInputStream.read(fileByte); // ���ļ��������е�������������
			myFileInputStream.close(); //
			return fileByte; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			throw new WLTAppException("ȡ��Դ�ļ�[" + _url + "]ʧ��!"); //
		}
	}

	/**
	 * д�ļ�
	 * @param _filename
	 * @param _filecontent
	 * @param _charencoding
	 * @throws Exception
	 */
	public void writeFile(String _filename, String _filecontent, String _charencoding) throws Exception {
		FileOutputStream myFileOutputStream = new FileOutputStream(_filename, false); // �½��ļ������,׼���ڵ�ǰĿ¼�½�һ��aaa.txt,�ڶ���������ʾ�Ƿ������ݵ���(��������ļ�����,����ԭ�ļ���������������)
		myFileOutputStream.write(_filecontent.getBytes(_charencoding)); // �ļ���������һ���ַ���(������ת����byte)
		myFileOutputStream.close(); // �ر��ļ������
	}

	/**
	 * ȡ�÷������������ļ�
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
			ins.close(); // �ر���..
			String str_file = new String(byteCodes, "GBK");
			return str_file; //
		} catch (Exception _ex) {
			return new TBUtil().getExceptionStringBuffer(_ex); //
		}
	}

	/**
	 * ȡ�÷������˵��ļ�
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
	 * ȡ�÷������������ļ�
	 * @return
	 */
	public String getServerLog() throws Exception {
		String str_filepath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/_weblight_log/weblight.txt"; //
		try {
			File file = new File(str_filepath); //
			if (file.exists()) { //
				RandomAccessFile accessFile = new RandomAccessFile(file, "r"); // ֻ����.
				String str_linetext = null; //
				ArrayList al_text = new ArrayList(); //
				long ll_count = 1; //
				for (;;) {
					str_linetext = accessFile.readLine(); //
					if (str_linetext == null) {
						break;
					}
					al_text.add(ll_count + ">" + str_linetext); // ��ͣ������.
					ll_count++; //
				}

				accessFile.close(); // �ر�

				// ������100����־����
				int li_showlines = 100; // ��Ҫ��ʾ�����Ķ�����.
				StringBuffer sb_text = new StringBuffer(); //
				int li_fileline = al_text.size();
				if (li_fileline > li_showlines) { // �������100��
					for (int i = 0; i < li_showlines; i++) {
						String str_text = (String) al_text.get(li_fileline - (li_showlines - i)); // //
						sb_text.append(new String(str_text.getBytes("ISO8859-1"), "GBK") + "\r\n"); // //
					}
				} else { // ���С��100��,��ȫ�����
					for (int i = 0; i < li_fileline; i++) {
						String str_text = (String) al_text.get(i); // //
						sb_text.append(new String(str_text.getBytes("ISO8859-1"), "GBK") + "\r\n"); // //
					}
				}
				return sb_text.toString(); //
			} else {
				return "û���ҵ�[" + str_filepath + "]����ļ�!"; //
			}
		} catch (Exception _ex) {
			return new TBUtil().getExceptionStringBuffer(_ex); //
		}
	}

	/**
	 * ȡ�÷������˿���̨��Ϣ..
	 * @return
	 * @throws Exception
	 */
	public String getServerConsole(boolean _isclear) throws Exception {
		Queue queue = ServerEnvironment.getServerConsoleQueue(); //
		StringBuffer sb_text = new StringBuffer();
		if (queue.isEmpty()) {
			return "��ǰ����������̨����Ϊ��,�����Ǳ�ĳ����չ���!"; //
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
	 * ȡ�ø�������Դ�ĵ�ǰ���е�����
	 * @return
	 * @throws Exception
	 */
	public String[][] getDatasourcePoolActiveNumbers() throws Exception {
		PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
		DataSourceVO[] dsVOs = ServerEnvironment.getInstance().getDataSourceVOs(); //
		String[][] str_return = new String[dsVOs.length][5]; // һ��4��,�ֱ�������Դ����,����,���,ʵ����
		for (int i = 0; i < dsVOs.length; i++) {
			GenericObjectPool pool = (GenericObjectPool) driver.getConnectionPool(dsVOs[i].getName()); //
			int li_active = pool.getNumActive();
			int li_idle = pool.getNumIdle(); //
			int li_maxactive = pool.getMaxActive(); // �����
			int li_maxdle = pool.getMaxIdle(); // ��������
			str_return[i] = new String[] { dsVOs[i].getName(), dsVOs[i].getDescr(), "" + li_active, "����:" + li_idle + "/�ܹ�:" + (li_active + li_idle), "�����:" + li_maxactive + "/���ʵ��:" + li_maxdle, "��Ч" }; //
		}
		return str_return; //
	}

	public Thread[] getAllRemoteCallServletThreads() {
		return SessionFactory.getInstance().getAllThreads(); //
	}

	/**
	 * ȡ������Զ�̷���ǰ������
	 * @return
	 * @throws Exception
	 */
	public String[][] getRemoteServicePoolActiveNumbers() throws Exception {
		String[] str_services = ServicePoolFactory.getInstance().getAllServiceNames(); //
		String[][] str_return = new String[str_services.length][4]; // һ��4��,�ֱ�������Դ����,����,���,ʵ����
		for (int i = 0; i < str_services.length; i++) {
			GenericObjectPool pool = ServicePoolFactory.getInstance().getPool(str_services[i]); //
			int li_active = pool.getNumActive();
			int li_idle = pool.getNumIdle(); //
			int li_maxactive = pool.getMaxActive(); // �����
			int li_maxdle = pool.getMaxIdle(); //
			str_return[i] = new String[] { str_services[i], "" + li_active, "����:" + li_idle + "/�ܹ�:" + (li_active + li_idle), "�����:" + li_maxactive + "/���ʵ��:" + li_maxdle, "<html><font color=\"blue\">��Ч</font></html>" }; //
		}
		return str_return; //
	}

	/*
	 * ȡ��Ĭ������Դ����!!
	 * @see gxlu.nova.framework.common.ui.FrameWorkCommService#getDeaultDataSource()
	 */
	public String getDeaultDataSource() throws Exception {
		return ServerEnvironment.getDefaultDataSourceName();
	}

	/**
	 * ȡ�÷������˵�ϵͳ����
	 */
	public Properties getServerSystemProperties() throws Exception {
		return System.getProperties(); //
	}

	/**
	 * ȡ�÷������˵�Environment����
	 * @return
	 * @throws Exception
	 */
	public String[][] getServerEnvironment() throws Exception {
		return ServerEnvironment.getInstance().getAllData();
	}

	/**
	 * ȡ��ϵͳ��ǰ�����û�!! ԭ��ֻ������Ա����,����������Ա������������������..
	 */
	public String[][] getServerOnlineUser() throws Exception {
		HashMap mapUser = ServerEnvironment.getInstance().getLoginUserMap(); //
		String[] str_sesions = (String[]) mapUser.keySet().toArray(new String[0]); // ��ǰ���߿ͻ��˵��嵥!!!
		String[][] str_return = new String[str_sesions.length][11]; // ���巵������
		String str_currtime = new TBUtil().getCurrTime(); // ��ǰʱ��
		String[] str_usercodes = new String[str_return.length]; //
		for (int i = 0; i < str_return.length; i++) {
			String[] str_onlineusers = (String[]) mapUser.get(str_sesions[i]); // ĳһ���û�����ϸʱ��
			str_return[i][0] = str_onlineusers[0]; // SessionID
			str_return[i][1] = str_onlineusers[1]; // IP1
			str_return[i][2] = str_onlineusers[2]; // IP2

			str_return[i][3] = str_onlineusers[3]; // �û�code..
			str_usercodes[i] = str_onlineusers[3]; //

			str_return[i][6] = str_onlineusers[4]; // ��¼ʱ��.
			str_return[i][7] = str_onlineusers[5]; // ���һ�η���ʱ��.

			long ll_time_used = betweenTwoTimeSecond(str_onlineusers[4], str_onlineusers[5]); // ������ʱ��-��¼ʱ��=��ʹ��ʱ��
			long ll_minute_used = ll_time_used / 60; //
			str_return[i][8] = ll_time_used + "��," + ll_minute_used + "��"; //

			str_return[i][9] = str_currtime; // ��ǰʱ��
			long ll_time_fadai = betweenTwoTimeSecond(str_onlineusers[5], str_currtime); // ����ʱ��
			long ll_minute_fadai = ll_time_fadai / 60; //
			if (ll_minute_fadai >= 15) {
				str_return[i][10] = "<html><font color=\"red\">" + ll_time_fadai + "��," + ll_minute_fadai + "��</font></html>"; //
			} else {
				str_return[i][10] = "<html><font color=\"blue\">" + ll_time_fadai + "��," + ll_minute_fadai + "��</font></html>"; //
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
				str_return[i][3] = str_return[i][3] + "/" + (str_username_deptname[0] == null ? "��" : str_username_deptname[0]);
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

		long li_second = (date2.getTime() - date1.getTime()) / 1000; // ��
		return li_second; //
	}

	public String getInCondition(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.getInCondition(_datasourcename, _sql); //
	}

	// �õ��Ӳ�ѯ!!!
	public String getSubSQLFromTempSQLTableByIDs(String[] _ids) throws Exception {
		CommDMO commDMO = new CommDMO();
		return commDMO.getSubSQLFromTempSQLTableByIDs(_ids); //
	}

	/**
	 * ȡ��ϵͳ���еı���
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
	 * ֱ�ӷ���һ���ַ���
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

	// ����һ��SQLֱ������һ����ά����!!���ܸ���,��Ϊ���ݽṹ����������!
	public String[][] getStringArrayByDS(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = ServerEnvironment.getCommDMO();
		return commDMO.getStringArrayByDS(_datasourcename, _sql); //
	}

	// ����һ��SQL���ɶ�ά�ṹ�ĵ�һ��,�����������Ӳ�ѯ��
	public String[] getStringArrayFirstColByDS(String _datasourcename, String _sql) throws Exception {
		return new CommDMO().getStringArrayFirstColByDS(_datasourcename, _sql); //
	}

	// ����һ��SQL����һ��HashMap,��SQL�еĵ�һ����Ϊkey,�ڶ�����Ϊvalue.
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

	// �������ͽṹ��hashVO
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

	//�����ύ
	public Integer executeUpdateByDSAutoCommit(String _datasourcename, String _sql) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		CommDMO commDMO = new CommDMO();
		return commDMO.executeUpdateByDSAutoCommit(_datasourcename, _sql); //
	}

	// ִ��һ��SQL
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

	// ִ��һ��SQL
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

	// ȡһ����������
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

	//ֱ���ϴ�,����ǰ����"N12345_"ǰ�,�������Ա�֤Ψһ��!
	public String uploadFile(ClassFileVO _vo) throws Exception {
		return uploadFile(_vo, true); //
	}

	//���Բ���ǰ��ϴ�
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
	 * �ϴ��ļ�!! ��Ҫ����ʲô�ٸ�һ���ϴ��ļ�������!
	 */
	public String upLoadFile(ClassFileVO _fileVO, String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir, boolean _isConvertHex, boolean _isAddSerialNo) throws Exception {
		return new BSUtil().uploadFile(_fileVO, _serverdir, _serverFileName, _isAbsoluteSeverDir, _isConvertHex, _isAddSerialNo); //
	}

	//��׼��Ŀ¼����
	public ClassFileVO downloadFile(String _filename) throws Exception {
		return new BSUtil().downLoadFile(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload/", _filename, true);
	}

	//���ݾ���·������
	public ClassFileVO downloadToClientByAbsolutePath(String _filename) throws Exception {
		TBUtil tbutil = new TBUtil(); // 
		_filename = tbutil.replaceAll(_filename, "\\", "/"); //
		String str_dir = _filename.substring(0, _filename.lastIndexOf("/")); //
		String str_fileName = _filename.substring(_filename.lastIndexOf("/") + 1, _filename.length()); //
		return new BSUtil().downLoadFile(str_dir, str_fileName, true);
	}

	/**
	 * �ӷ������������ļ�����ClassFileVO
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
	 * �����㷨��ĳһ�����ĳһ���ֶμ���
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
				String str_img64code = new String(org.jfreechart.xml.util.Base64.encode(str_oldtext.getBytes("GBK"))); //jfreechart-1.0.14�����޸���·��jfreeΪjfreechart �����/2013-06-09��
				commdmo.executeUpdateByDS(null, "update " + _newtablename + " set " + _column + " ='" + str_img64code + "' where " + primarykey + "='" + hvs[i].getStringValue(primarykey) + "'");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ƽ̨��������
	 */
	public String[][] getAllPlatformOptions() throws Exception {
		return SystemOptions.getAllPlatformOptions();
	}

	/**
	 * ����ƽ̨��������
	 */
	public String[][] getAllPlatformOptions(String _type) throws Exception {
		String[][] str_allOptions = SystemOptions.getAllPlatformOptions(); //���в���
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
	 * �������ݿ��������
	 */
	public String[][] getAllOptions() throws Exception {
		return SystemOptions.getAllOptions();
	}

	// ˢ�»�������ͻ������ͻ��桾���/2016-05-25��
	public void refreshCorptypeFromDB() throws Exception {
		ServerCacheDataFactory.static_vos_corptypedef = getCommDMO().getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('��������','��������')"); // //
	}

	// ���¼���sysoption����������!!
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
	 * �������������
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
	 * ������word��excel�ļ��Ƿ��йؼ���
	 * @param _fileNames ȡ���ݿⱣ���ļ���
	 * @param _keywords Ϊ�ؼ���
	 * @param isAllContain �ж��ļ��Ƿ�ͬʱ�������йؼ���
	 * @return
	 * @throws Exception
	 */
	public List<String> checkWordOrExcelContainKeys(String _uploadfiledir, List _filenames, String[] _keywords, boolean _isAllContain) throws Exception {
		BSUtil bsUtil = new BSUtil(); //
		return bsUtil.checkWordOrExcelContainKeys(_uploadfiledir, _filenames, _keywords, _isAllContain);
	}

	/**
	 * ȫ�ļ����߼�
	 */
	public List<String> checkWordOrExcelContainKeys(String _serverdir, String[][] _filesInfo, String[] _keywords, boolean _isAllContain) throws Exception {
		List<String> list_ids = new ArrayList<String>();
		boolean isSearchWithIndex = getSysOptionBooleanValue("ȫ�ļ����Ƿ����������ʽ", false);
		if (isSearchWithIndex) {
			System.out.println("���[ȫ�ļ����Ƿ����������ʽ]Ϊ\"TRUE\", ϵͳ��ʼʹ��������ʽִ��ȫ�ļ���......");
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
			if (hmap_index != null || hmap_index.size() > 0) { // ���һ������������������û��,
				// ֱ�ӾͲ����ж���!
				for (int i = 0; i < _filesInfo.length; i++) {
					if (_filesInfo[i][1] == null || _filesInfo[i][1].trim().equals("")) { // û��ָ������,
						// ֱ���Թ�!!
						continue;
					}
					String filename = hmap_index.get(_filesInfo[i][0]);
					if (filename != null && _filesInfo[i][1].trim().equals(filename)) { // ���ڸ��ƶȵ�����,
						// ������ͬ�����Ƶ��ļ�!
						list_ids.add(_filesInfo[i][0]);
					}
				}
			}

			long li_2 = System.currentTimeMillis();
			System.out.println("��ȫ�ļ���ִ�����, ������ļ���[" + _filesInfo.length + "], �����ļ���[" + list_ids.size() + "], ����ʱ[" + (li_2 - li_1) + "]����");
			return list_ids;
		} else {
			String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //�����ɼӵ�ȫ�ļ����߼�!
			if (str_newWebUrl != null) {
				try {
					System.out.println("������Զ��ȫ�ļ����ĵ�ַ[" + str_newWebUrl + "], ϵͳ��ʼִ��Զ��ȫ�ļ���......");
					long li_1 = System.currentTimeMillis();
					TBUtil tbUtil = new TBUtil();
					HashMap requestMap = new HashMap();
					requestMap.put("Action", "fullSearch"); //ȫ�ļ���!
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
						System.out.println("��Զ��ȫ�ļ����ĵ�ַ[" + str_newWebUrl + "]ȡ���ɹ�,������ļ���[" + _filesInfo.length + "],�����ļ���[" + ((ArrayList) obj_return).size() + "],����ʱ[" + (li_2 - li_1) + "]");
						return (ArrayList) obj_return;
					}
				} catch (Exception e) {
					throw new WLTAppException(e.getMessage());
				}
			} else {
				// �������������̴߳���...
				List<MyThread_c> list_threads = new ArrayList<MyThread_c>();
				int t_count = 0; // �ֳɼ���
				if (_filesInfo.length <= 100) {
					t_count = 1;
				} else if (_filesInfo.length < 500) {
					t_count = 5;
				} else if (_filesInfo.length < 1000) {
					t_count = 10;
				} else {
					t_count = 20;
				}

				int number = _filesInfo.length / t_count; // һ����ٸ�
				for (int i = 0; i < t_count; i++) { // �ֶ�����, �ʹ������ٸ��߳�,
					// ���һ���߳�ִ�е����������ڵ���number,
					// ����С��2*number,
					// ������������̻߳Ứ�����ʱ��
					// System.out.println("��" + i + "��, ��" + (i*number) + "��" +
					// ((i+1)==t_count?_filesInfo.length:(i+1)*number) + ":");
					MyThread_c t_c = new MyThread_c(list_ids, _serverdir, sub2DStr(_filesInfo, i * number, (i + 1) == t_count ? _filesInfo.length : (i + 1) * number), _keywords, _isAllContain);
					t_c.setName("MyThread_c-" + i); // ȡ������, �Ա��ڼ��
					list_threads.add(t_c); // ��ӵ������...
					t_c.start(); // ��ʼִ��
				}
				while (true) {
					synchronized (list_ids) {
						boolean allTerminate = true;
						for (int i = 0; i < list_threads.size(); i++) {
							if (list_threads.get(i).isTerminate() == 1) { // �����߳�δ��ֹ
								// System.out.println("> �߳�" +
								// list_threads.get(i).getName() + "��δ��ֹ...");
								allTerminate = false;
								break;
							}
						}
						if (allTerminate) { // �����̶߳���ֹ
							// System.out.println("> �����߳��Ѿ���ֹ...");
							return list_ids;
						} else {
							try {
								Thread.currentThread().sleep(1000); // ÿ��ִ��һ��
							} catch (Exception e) {
								System.out.println("FF:����߳�sleepʱ�����쳣!!");
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	// ��ȡ��ά����
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
	 * �����������ж��Ƶ�JOB
	 */
	public String restartJobs(String _primarykey) throws Exception {
		StringBuffer message = new StringBuffer();
		// ����, ���������е��߳�
		if (getHmJob().size() > 0) {
			message.append("ֹͣ����JOB:\r\n");
			Iterator<String> it = getHmJob().keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				WLTJobTimer job = getHmJob().get(key);
				if (job != null && job.isAlive()) {
					job.closeme();
					message.append("��" + key.substring(key.indexOf("_") + 1) + "��");
				}
			}
		}
		getHmJob().clear();
		// Ȼ��, ���»�ȡ��Ҫ����������
		HashVO[] hv_jobs = getCommDMO().getHashVoArrayByDS(null, "select * from PUB_JOB where ACTIVEFLAG = 'Y'");
		if (hv_jobs == null || hv_jobs.length <= 0) {
			if (message.length() <= 0) {
				return "û���κ�JOB��ֹͣ��������";
			} else {
				return message.toString();
			}
		}
		message.append("\r\n��������JOB:\r\n");
		for (int i = 0; i < hv_jobs.length; i++) {
			WLTJobTimer job = new WLTJobTimer(hv_jobs[i]);
			getHmJob().put("JOB_" + hv_jobs[i].getStringValue(_primarykey, ""), job);
			job.start(); // ����
			message.append("��" + hv_jobs[i].getStringValue(_primarykey, "") + "��");
		}
		return message.toString();
	}

	/**
	 * ֹͣĳһ��JOB
	 */
	public String closeJob(String _pkValue) throws Exception {
		WLTJobTimer job = getHmJob().get("JOB_" + _pkValue);
		if (job != null) {
			if (job.isAlive()) {
				job.closeme();
				getHmJob().remove("JOB_" + _pkValue);
				return "JOBֹͣ, �ɹ�!";
			} else {
				return "JOB�Ѿ�ֹͣ, �����ظ�����!";
			}
		} else {
			return "JOBδ������, �޷�ֹͣ!";
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
 * Ӧ�������Ĳ�ѯ��(��)�߳���
 * @author My Dream
 */
class MyThread_c extends Thread {
	private List<String> list_ids;
	private String uploadfiledir;
	private String[][] filesInfo;
	private String[] keywords;
	private boolean isAllContain;
	private int state = 1; // �߳�״̬ 1-δ��ֹ��0-����ֹ

	public MyThread_c(List<String> _ids, String _uploadfiledir, String[][] _filesInfo, String[] _keywords, boolean _isAllContain) {
		this.list_ids = _ids; // �������̶߳�ָ�������
		this.uploadfiledir = _uploadfiledir;
		this.filesInfo = _filesInfo;
		this.keywords = _keywords;
		this.isAllContain = _isAllContain;
	}

	public void run() {
		BSUtil bsUtil = new BSUtil();
		List<String> ids = bsUtil.checkWordOrExcelContainKeys(uploadfiledir, filesInfo, keywords, isAllContain);
		for (int i = 0; i < ids.size(); i++) {
			synchronized (list_ids) { // �߳�ͬ�� ������? ����һ�¿���....
				list_ids.add(ids.get(i));
			}
		}
		state = 0;
	}

	public int isTerminate() {
		return state;
	}
}
