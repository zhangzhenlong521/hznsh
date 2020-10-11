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
 * �ͻ��˹�����test
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
		ht_loginLanguage.put("�ҵĿ�ݷ�ʽ", "My Favorites");
		ht_loginLanguage.put("����Ȩ��", "Process");
		ht_loginLanguage.put("ϵͳ֧��", "System Setup");
		ht_loginLanguage.put("ƽ̨��Ϣ", "System Index");
		ht_loginLanguage.put("�鿴�ͻ������л�������", "Client Environment");
		ht_loginLanguage.put("�鿴�����������л�������", "Server Enironment");
		ht_loginLanguage.put("�޸���������", "Operation Style Configuration");
		ht_loginLanguage.put("�޸�����", "Modify Password");
		ht_loginLanguage.put("��ѯ������", "SQL Query Tools");
		ht_loginLanguage.put("�鿴����������־", "Server Log");
		ht_loginLanguage.put("�鿴����������Ļ", "Server Screen");
		ht_loginLanguage.put("�������й��ܵ�", "Collapse All Node");
		ht_loginLanguage.put("չ�����й��ܵ�", "Expand All Nodes");
		ht_loginLanguage.put("�����ֵ����", "Data Dictionary");
		ht_loginLanguage.put("Ԫԭģ�����", "Meta Data");
		ht_loginLanguage.put("�˵�����", "Menu Definition");
		ht_loginLanguage.put("�������ֵ����", "Dropdown Options");
		ht_loginLanguage.put("��ѯģ�����", "Query Templet");
		ht_loginLanguage.put("����ƽ̨����", "Export PlatData");
		ht_loginLanguage.put("�������༭", "WorkFlow Edit");
		ht_loginLanguage.put("ϵͳ������", "System Bulletin");
		ht_loginLanguage.put("����������", "Bulletin Information");
		ht_loginLanguage.put("�����ݷ�ʽ", "Add to My Favorites");
		ht_loginLanguage.put("��¼�û�:", "Login User:");
		ht_loginLanguage.put("�򿪽��ʧ��", "Open frame fail");
		ht_loginLanguage.put("����ҳ��", "Load Frame");
		ht_loginLanguage.put("�ɹ�", "Success");
		ht_loginLanguage.put("��ʱ", "Used");
		ht_loginLanguage.put("����ҳ��", "Load Frame");
		ht_loginLanguage.put("ʧ��", "Processing faild.");
		ht_loginLanguage.put("ԭ��", "Reason");
		ht_loginLanguage.put("��ǰλ��", "Function Point");
		ht_loginLanguage.put("����", "Add");
		ht_loginLanguage.put("��ݷ�ʽ", "Shotcut");
		ht_loginLanguage.put("ɾ��", "Delete");
		ht_loginLanguage.put("�ر��Լ�", "Close");
		ht_loginLanguage.put("�ر�����", "Close Others");
		ht_loginLanguage.put("�ر�����", "Close All");
		ht_loginLanguage.put("��������˳�ϵͳ��?", "Will exit system, confrim?");
		ht_loginLanguage.put("��ʾ", "Prompt");
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
	 * ȡ������һ̨�����ϵķ���!!
	 * @param _otherIPAndPort �Ǹ�ʽ[192.168.0.10:9016]������
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
	 * ͨ�÷���..
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
	 * ����һ̨�����ϵ�һ������!!���籾��������WebSphere,��ĳ������Ӧ�ÿ��ܻ������һ���������ϵ�!��������ϵͳ�еĽӿ�,�������Tomcat�е�WebSphere����!!!
	 * ���⼯ȺӦ��Ҳ��Ҫ�÷���!!
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
	 * ȡ�õ�¼��Ա�����������͵�����
	 * @return
	 */
	public synchronized static String getLoginUserBLDeptCondition() {
		String str_filterCondition = null;
		CurrLoginUserVO loginUserVO = ClientEnvironment.getCurrLoginUserVO(); //ȡ�õ�¼��Ա��Ϣ!
		String str_loginuser_depttype = loginUserVO.getBlDept_corptype(); //��ȡ�õ�¼��Ա�����Ļ�������!!
		if (str_loginuser_depttype != null) {
			if (str_loginuser_depttype.equals("����")) { //��������е���,�����κ�Ȩ�޹���
			} else { //�������"���в���"��ͷ.
				if (1 == 1) { //����ǹ���Ա,�����־���̫�鷳��,���ָ�Ȩ���Ǽ�,��ʱȨ�޷ſ�����,��Ϊ��������ʵ����Ȩ�޺�ͻ��־��ò�ˬ��! loginUserVO.isCorpDeptAdmin()
					if (str_loginuser_depttype.indexOf("����") == 0) { //��������е�
					} else if (str_loginuser_depttype.indexOf("��ҵ��") == 0) { //�������ҵ��
						String str_bl_shiyb = loginUserVO.getBlDept_bl_shiyb(); //��¼��Ա������ҵ��
						str_filterCondition = "bl_shiyb='" + str_bl_shiyb + "'"; //�ҳ�������ҵ�����ڱ��˵�������ҵ��
					} else if (str_loginuser_depttype.indexOf("����") == 0) { //����Ƿ��е���
						String str_bl_fengh = loginUserVO.getBlDept_bl_fengh(); //��¼��Ա��������
						str_filterCondition = "bl_fengh='" + str_bl_fengh + "'"; //�ҳ��������е��ڱ��˵���������
					} else if (str_loginuser_depttype.indexOf("֧��") == 0) { //֧�е���
						String str_bl_zhih = loginUserVO.getBlDept_bl_zhih(); //��¼��Ա����֧��
						str_filterCondition = "bl_zhih='" + str_bl_zhih + "'"; //�ҳ�����֧�е��ڱ��˵�����֧��
					}
				} //
				//else { //������ǹ���Ա,����и��Ľ���...
				//				if (str_loginuser_depttype.equals("���в���") || str_loginuser_depttype.equals("���в���_��������")) { //
				//					String str_bl_zhbm = loginUserVO.getBlDept_bl_zhonghbm(); //��¼��Ա�������в���
				//					str_filterCondition = "bl_zhonghbm='" + str_bl_zhbm + "'"; //�ҳ��������в��ŵ��ڱ��˵��������в��ŵ���Ա
				//				} else if (str_loginuser_depttype.equals("��ҵ��") || str_loginuser_depttype.equals("��ҵ��_��������")) { //
				//					String str_bl_shiyb = loginUserVO.getBlDept_bl_shiyb(); //��¼��Ա������ҵ��
				//					str_filterCondition = "bl_shiyb='" + str_bl_shiyb + "'"; //�ҳ�������ҵ�����ڱ��˵�������ҵ��
				//				} else if (str_loginuser_depttype.equals("��ҵ���ֲ�") || str_loginuser_depttype.equals("��ҵ���ֲ�_��������")) { //
				//					String str_bl_shiybfb = loginUserVO.getBlDept_bl_shiybfb(); //��¼��Ա������ҵ���ֲ�
				//					str_filterCondition = "bl_shiybfb='" + str_bl_shiybfb + "'"; //�ҳ�������ҵ���ֲ����ڱ��˵�������ҵ���ֲ�
				//				} else if (str_loginuser_depttype.equals("����")) { //
				//					String str_bl_fengh = loginUserVO.getBlDept_bl_fengh(); //��¼��Ա��������
				//					str_filterCondition = "bl_fengh='" + str_bl_fengh + "'"; //�ҳ��������е��ڱ��˵���������
				//				} else if (str_loginuser_depttype.equals("���в���") || str_loginuser_depttype.equals("���в���_��������")) { //
				//					String str_bl_fenghbm = loginUserVO.getBlDept_bl_fenghbm(); //��¼��Ա�������в���
				//					str_filterCondition = "bl_fenghbm='" + str_bl_fenghbm + "'"; //�ҳ��������в��ŵ��ڱ��˵��������в���
				//				} else if (str_loginuser_depttype.equals("֧��") || str_loginuser_depttype.equals("֧��_��������")) { //
				//					String str_bl_zhih = loginUserVO.getBlDept_bl_zhih(); //��¼��Ա�������в���
				//					str_filterCondition = "bl_zhih='" + str_bl_zhih + "'"; //�ҳ��������в��ŵ��ڱ��˵��������в���
				//				}
				//}
			}
		}
		return str_filterCondition; //
	}

	/**
	 * ȡ�õ�¼��Ա���л�����Ȩ��!!,Ӧ��������Ȩ�޶�Ӧ����!!
	 * ������Ȩ��ģ��Ӧ���ṩһ������,����һ������,ָ��һ����Դ����(�������),ָ��һ������/��ͼ��(����pub_corp_dept),ָ��һ������봮,Ȼ����ܷ���һ��ID[]!!!
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

	//ȡ�õ�¼��Ա���ϼ������е�ĳ��ָ�����͵Ļ���,���������е�ָ���ֶε�ֵ!!
	public synchronized static String getLoginUserParentCorpItemValueByType(String _corpType, String _nvlCorpType, String _itemName) throws Exception {
		cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc sysService = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class);
		return sysService.getLoginUserParentCorpItemValueByType(_corpType, _nvlCorpType, _itemName);
	}

	/**
	 * ���������ֵ��ж��Ļ��������еĺ����,�ҳ���¼��Ա/ĳ����Ա/ĳ������,�ġ�$�����š���$��������
	 * ������ڷ������˶�Ӧ���� new sysAppDMO().getParentCorpVOByMacro()...
	 * @param _type,������ȡֵ,1,2,3������1��ʾ���ǵ�¼��Ա,2��ʾĳ��������Ա(�ڶ�������������Աid)��3��ʾ��ĳ��ʵ�ʻ���(�ڶ����������ǻ���id)
	 * @param _consValue ���ݵ�һ������������,�ֱ��ﲻͬ�ĺ���!!
	 * @param _macroName,����룬���硾$�����š���$����������$��һ�����С���$���������С�,������������ֵ��ж���,���Ϊ��,����ǰ����и��׻�����ȡ������
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
		if (ClientEnvironment.getInstance().isEngligh()) { // �����Ӣ�Ļ���,��Ҫת��
			String str_language = (String) ht_loginLanguage.get(_key); //
			return str_language == null ? ("." + _key) : str_language; //
		} else {
			return _key; //
		}
	}

	/**
	 * ȡ�÷������˵ĵ�ǰ����,����"2008-08-20"������
	 * @return
	 */
	public synchronized static String getServerCurrDate() throws Exception {
		return getCommonService().getServerCurrDate(); //
	}

	/**
	 * ȡ�÷������˲���,
	 * @param _module
	 * @param _key
	 * @return
	 * @throws Exception
	 */
	//�������ò��ԣ���������module��key����ȡ��value,��ʵ����moduleû��,��Ƚ��߼���������
	//���ݺ�����ʦ���ۣ�������ʱɾ��������
	//	public synchronized static String getSysOptionValue(String _module, String _key) {
	//		try {
	//			String[][] str_options = ClientEnvironment.getClientSysOptions(); //
	//			if (str_options == null) {
	//				str_options = getCommonService().getServerSysOptions(); //ȥ��������ȡ
	//				ClientEnvironment.setClientSysOptions(str_options); //����ͻ��˻���
	//			}
	//
	//			for (int i = 0; i < str_options.length; i++) {
	//              str_options��ά����ʵ�ʴ�ŵ�[i][0]��keyֵ����[i][1]��valueֵ����û��moduleֵ����if������Զ����Ϊtrue
	//				if (str_options[i][0].equalsIgnoreCase(_module) && str_options[i][1].equalsIgnoreCase(_key)) {
	//					return str_options[i][2]; //
	//				}
	//			}
	//
	//			return ""; //����Ҳ���,��ֱ��""
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//			return "";
	//		}
	//	}
	public synchronized static long getServerCurrTimeLongValue() throws Exception {
		return getCommonService().getServerCurrTimeLongValue(); //
	}

	/**
	 * ȡ�÷������˵ĵ�ǰʱ��,����"2008-08-20 10:23:15"������
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
			System.out.println("\r\nȡͼƬ[" + _fileName + "]ʧ��!!");//
			// ex.printStackTrace();
			return null;
		}
	}

	/**
	 * �ӷ������˵�WebRootĿ¼��ȡ�ļ�! ����[/applet/login_crcc.gif]
	 * @param _fileName
	 * @return
	 */
	public synchronized static ImageIcon getImageFromServer(String _fileName) {//httpsʱ�˴�������/sunfujun/20130222
		try {
			String str_imgurl = System.getProperty("CALLURL") + _fileName;
			long ll_1 = System.currentTimeMillis(); //
			System.out.println("[" + getCurrTime(true) + "]��ʼ����ͼƬ[" + str_imgurl + "]..." + System.getProperty("transpro")); //
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
				conn.setConnectTimeout(5000);//��������Ϊ��0��Ҳ�������ã��ʸĻ�ԭ���롾���/2018-06-25��
				conn.setReadTimeout(5000);
				conn.setRequestProperty("Content-type", "text/html"); //
				InputStream intStream = conn.getInputStream(); //
				ByteArrayOutputStream baos = null;
				try {
					baos = new ByteArrayOutputStream();
					byte[] buf = new byte[2048]; //����ʱ��Ҫ��!���ȷ��������µ�Ҫ��һ��ź�,��������Ƿ��������µĿ���ͻ��˳����,����ܳ��������벢�������������!!
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
					System.out.println("\r\nȡͼƬ[" + _fileName + "]ʧ��!!");//
					return null; //
				} else {
					System.out.println("[" + getCurrTime(true) + "]��������ͼƬ[" + str_imgurl + "],����ʱ[" + (ll_2 - ll_1) + "]"); //
				}
				return icon; //
			}
		} catch (Exception ex) {
			System.out.println("\r\nȡͼƬ[" + _fileName + "]ʧ��!!");//
			return null;
		}
	}

	//�ӷ������˵���Դ·����ȡͼƬ,��ClassPath��ȡ!! ����/com/pushworld/ipushgrc/bs/newindex.jpg
	public synchronized static ImageIcon getImageFromServerRespath(String _imgPathName) {
		try {
			ImageIcon icon = getCommonService().getImageFromServerRespath(_imgPathName); //
			return icon; //
		} catch (Exception ex) {
			System.out.println("\r\nȡͼƬ[" + _imgPathName + "]ʧ��!!");//
			// ex.printStackTrace();
			return null;
		}

	}

	/**
	 * ȡ��һ��ͼƬ
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
			icon.setDescription(_fileName); //����˵��!
			return icon; //
		} catch (Exception ex) {
			System.err.println("\r\nȡͼƬ[" + _fileName + "]ʧ��!");//
			ImageIcon icon = new ImageIcon(UIUtil.class.getResource("/cn/com/weblight/images/" + "office_001.gif"));
			return icon; //
		}
	}

	/**
	 * ����һ���µ��߳�ȥ�޸Ŀؼ�.
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

	//���Ծ�ȷ������!!
	public synchronized static String getCurrTime(boolean _isMill) {
		if (_isMill) { //����Ǿ�ȷ������!!
			currDate.setTime(System.currentTimeMillis());
			return sdf2.format(currDate); //
		} else {
			currDate.setTime(System.currentTimeMillis()); //
			return sdf.format(currDate); //
		}
	}

	/**
	 * ��÷����������е�ͼ���ļ����ļ���
	 * 
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public synchronized static String[] getImageFileNames() throws WLTRemoteException, Exception { //
		String[] str_imgNames = getCommonService().getImageFileNames(); //
		System.out.println("ͼƬ����=[" + str_imgNames.length + "]"); //
		if (str_imgNames != null) {
			return str_imgNames; //����ͼƬ
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

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static String getSequenceNextValByDS(String _datasourcename, String _sequenceName) throws WLTRemoteException, Exception {
		return "" + ClientSequenceFactory.getInstance().getSequence(_sequenceName); // �ӿͻ��˻��湤��ȡ���ڹ����о����Ƿ��Զ��ȡ������һȡ����һ��!
	}

	/**
	 * ����In����
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public synchronized static String getInCondition(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getInCondition(_datasourcename, _sql);
	}

	//������ID,����һ���Ӳ�ѯ���,ԭ�����Ȳ���һ����ʱ����,Ȼ�󷵻ش���ʱ���в�ѯ��SQL
	public synchronized static String getSubSQLFromTempSQLTableByIDs(String[] _ids) throws Exception {
		return getCommonService().getSubSQLFromTempSQLTableByIDs(_ids);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static String getStringValueByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getStringValueByDS(_datasourcename, _sql);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static HashMap getHashMapBySQLByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getHashMapBySQLByDS(_datasourcename, _sql);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static String[][] getStringArrayByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getStringArrayByDS(_datasourcename, _sql);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static String[] getStringArrayFirstColByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getStringArrayFirstColByDS(_datasourcename, _sql);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static TableDataStruct getTableDataStructByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getTableDataStructByDS(_datasourcename, _sql);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoStructByDS(_datasourcename, _sql);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql, int _topRecords) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoStructByDS(_datasourcename, _sql, _topRecords);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoArrayByDS(_datasourcename, _sql);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!����ֻȡǰ������!! ��̨���ݿ��Զ����и�������Դ����ѡ��ͬ�ķ�ҳ����
	public synchronized static HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql, int _topRecords) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoArrayByDS(_datasourcename, _sql, _topRecords);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static Vector getHashVoArrayReturnVectorByMark(String _datasourcename, String[] _sqls) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoArrayReturnVectorByDS(_datasourcename, _sqls);
	}

	// Զ��ȡ��,���_datasourcenameΪnull,���Ĭ������Դȡ��!
	public synchronized static HashMap getHashVoArrayReturnMapByMark(String _datasourcename, String[] _sqls, String[] _keys) throws WLTRemoteException, Exception {
		return getCommonService().getHashVoArrayReturnMapByDS(_datasourcename, _sqls, _keys);
	}

	// �������ͽṹ��hashVO
	public synchronized static HashVO[] getHashVoArrayAsTreeStructByDS(String _datasourcename, String _sql, String _idField, String _parentIDField, String _seqField, String _rootNodeCondition) throws Exception {
		return getCommonService().getHashVoArrayAsTreeStructByDS(_datasourcename, _sql, _idField, _parentIDField, _seqField, _rootNodeCondition); //
	}

	// ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static int executeUpdateByDS(String _dsName, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().executeUpdateByDS(_dsName, _sql).intValue();
	}

	public synchronized static int executeUpdateByDSAutoCommit(String _dsName, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().executeUpdateByDSAutoCommit(_dsName, _sql).intValue();
	}

	// ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static int executeUpdateByDSPS(String _dsName, String _sql) throws WLTRemoteException, Exception {
		return getCommonService().executeUpdateByDSPS(_dsName, _sql).intValue();
	}

	/**
	 * ִ�д�SQLBuilderIfc������sql
	 */
	public synchronized static int executeUpdateByDS(String _datasourcename, SQLBuilderIfc _sqlBuilder) throws WLTRemoteException, Exception {
		return getCommonService().executeUpdateByDS(_datasourcename, _sqlBuilder);
	}

	// ִ�к�SQL!!!!!
	public synchronized static int executeMacroUpdateByDS(String _dsName, String _sql, String[] _colvalues) throws WLTRemoteException, Exception {
		return getCommonService().executeMacroUpdateByDS(_dsName, _sql, _colvalues).intValue();
	}

	// ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static void executeBatchByDS(String _dsName, String[] _sqls) throws Exception {
		getCommonService().executeBatchByDS(_dsName, _sqls); //
	}

	public synchronized static void executeBatchByDSNoLog(String _dsName, String _sqls) throws Exception {
		getCommonService().executeBatchByDSNoLog(_dsName, _sqls); //
	}

	// ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static void executeBatchByDS(String _dsName, java.util.List _sqllist) throws Exception {
		getCommonService().executeBatchByDS(_dsName, _sqllist); //
	}

	// ��ָ������Դ��,ִ��һ�����ݿ��޸����,����insert,delete,update,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static void executeBatchByDS(String _dsName, java.util.List _sqllist, boolean _isDebugLog, boolean _isDBLog) throws Exception {
		getCommonService().executeBatchByDS(_dsName, _sqllist, _isDebugLog, _isDBLog); //
	}

	// �洢����,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static void callProcedure(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		getCommonService().callProcedureByDS(_datasourcename, procedureName, parmeters);
	}

	// �洢����,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static void callProcedureSqlserver(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		getCommonService().callProcedureByDSSqlServer(_datasourcename, procedureName, parmeters);
	}

	// �洢����,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static String callProcedureByReturn(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		return getCommonService().callProcedureReturnStrByDS(_datasourcename, procedureName, parmeters);
	}

	// �洢����,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static String callFunctionByReturnVarchar(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		return getCommonService().callFunctionReturnStrByDS(_datasourcename, procedureName, parmeters);
	}

	// �洢����,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static String[][] callFunctionByReturnTable(String _datasourcename, String functionName, String[] parmeters) throws Exception {
		return getCommonService().callFunctionReturnTableByDS(_datasourcename, functionName, parmeters);
	}

	// ȡԪ����������Ϣ
	public synchronized static Pub_Templet_1VO getPub_Templet_1VO(String _code) throws Exception {
		return getMetaDataService().getPub_Templet_1VO(_code);
	}

	// ȡԪ����������Ϣ
	public synchronized static Pub_Templet_1VO[] getPub_Templet_1VOs(String[] _codes) throws Exception {
		return getMetaDataService().getPub_Templet_1VOs(_codes);
	}

	// ȡԪ����������Ϣ
	public synchronized static Pub_Templet_1VO getPub_Templet_1VO(AbstractTMO _tmo) throws Exception {
		Pub_Templet_1VO templetVO = getMetaDataService().getPub_Templet_1VO(_tmo.getPub_templet_1Data(), _tmo.getPub_templet_1_itemData(), "CLASS", "�ͻ�����:" + _tmo.getClass().getName()); //����ת����HashvOȥȡ,�����е�TMO������ڲ�������ܱ�logger���л��������Ϣ!
		return templetVO;
	}

	// ȡԪ����������Ϣ
	public synchronized static Pub_Templet_1VO getPub_Templet_1VO(ServerTMODefine _serverTMO) throws Exception {
		Pub_Templet_1VO templetVO = getMetaDataService().getPub_Templet_1VO(_serverTMO); //
		return templetVO;
	}

	// Ԫ����--ȡ��Ƭ����!,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static Object[] getBillCardDataByDS(String _dsName, String _sql, Pub_Templet_1VO _templetVO) throws WLTRemoteException, Exception {
		return getMetaDataService().getBillCardDataByDS(_dsName, _sql, _templetVO.getParPub_Templet_1VO());
	}

	// Ԫ����--ȡ�б�����!,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static Object[][] getBillListDataByDS(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws WLTRemoteException, Exception {
		return getMetaDataService().getBillListDataByDS(_datasourcename, _sql, _templetVO.getParPub_Templet_1VO());
	}

	// Ԫ����--ȡ�б�����!,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static BillVO[] getBillVOsByDS(String _datasourcename, String _sql, Pub_Templet_1VO _templetVO) throws WLTRemoteException, Exception {
		return getMetaDataService().getBillVOsByDS(_datasourcename, _sql, _templetVO.getParPub_Templet_1VO());
	}

	// Ԫ����--ȡ��ѯģ������!,���_datasourcenameΪnull,�����Ĭ������Դ!
	public synchronized static Object[][] getQueryData(String _dsName, String _sql, Pub_Templet_1VO _templetVO) throws WLTRemoteException, Exception {
		return getMetaDataService().getQueryDataByDS(_dsName, _sql, _templetVO);

	}

	/**
	 * ���ݸ�������������
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

		DefaultMutableTreeNode node_root = new DefaultMutableTreeNode(par_roottitle); // ���������

		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[hashVOs.length]; // �������н������
		for (int i = 0; i < hashVOs.length; i++) {
			node_level_1[i] = new DefaultMutableTreeNode(hashVOs[i]); // �����������

			node_root.add(node_level_1[i]); // ��������
		}

		// ������
		for (int i = 0; i < node_level_1.length; i++) {
			HashVO nodeVO = (HashVO) node_level_1[i].getUserObject();
			String str_pk = nodeVO.getStringValue(_keyname); // ����
			String str_pk_parentPK = nodeVO.getStringValue(_parentkeyname); // ��������
			if (str_pk_parentPK == null || str_pk_parentPK.equals(""))
				continue;
			for (int j = 0; j < node_level_1.length; j++) {
				HashVO nodeVO_2 = (HashVO) node_level_1[j].getUserObject();
				String str_pk_2 = nodeVO_2.getStringValue(_keyname); // ����
				String str_pk_parentPK_2 = nodeVO_2.getStringValue(_parentkeyname); // ��������
				if (str_pk_2.equals(str_pk_parentPK)) // ������ָý�������������ϲ�ѭ���ĸ��׽��,������Ϊ�ҵĶ��Ӵ������
				{
					try {
						node_level_1[j].add(node_level_1[i]);
					} catch (Exception ex) {
						System.out.println("��[" + node_level_1[j] + "]�¼����ӽ��[" + node_level_1[i] + "]ʧ��"); // /
						ex.printStackTrace(); //
					}
				}
			}
		}
		JTree aJTree = new JTree(new DefaultTreeModel(node_root));
		return aJTree;
	}

	//�ϴ��ļ�
	public synchronized static String uploadFileFromClient(ClassFileVO _vo) throws Exception {
		return getCommonService().uploadFile(_vo);
	}

	//�ϴ��ļ� �ļ����Ƿ�ת��
	public synchronized static String uploadFileFromClient(ClassFileVO _vo, boolean ifChangeName) throws Exception {
		return getCommonService().uploadFile(_vo, ifChangeName);
	}

	//�����ļ�
	public synchronized static ClassFileVO downloadToClient(String filename) throws Exception {
		return getCommonService().downloadFile(filename);
	}

	/**
	 * �ӷ������������ļ����ͻ���
	 * 
	 * @param _serverdir
	 *            ��������·��,������D:/wltuploadfile�ľ���·��,Ҳ������/upload�����·����ʽ,��������·��,����Ҫʹ�û�������WLTUPLOADFILEDIR��·���������·��
	 * @param _serverFileName
	 *            �������˵��ļ���
	 * @param _isAbsoluteSeverDir
	 *            �Ƿ��Ǿ���·��,true=��,false=��
	 * @param _clientdir
	 *            �ͻ���·��,ͬ��������·��,�ȿ����Ǿ���·��,Ҳ���������·��,��������·��,����Ҫʹ�û�������CLIENTCODECACHE��·���������·��
	 * @param _clientFileName
	 *            �ͻ����ļ���
	 * @param _isAbsoluteClientDir
	 *            �ͻ���·���Ƿ��Ǿ���·��
	 * @return �������ص��ͻ��˺��ļ��ľ���·��!!
	 * @throws Exception
	 */
	public synchronized static String downLoadFile(String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir, String _clientdir, String _clientFileName, boolean _isAbsoluteClientDir) throws Exception {
		// дһ��Զ�̷���,������߼���:
		// ����_isAbsoluteSeverDir,�����false,���������·��(��������·��,��ҪУ���׸���ĸ������/,������ĸ���벻����/),����Ǿ���·��,�������ĸ���벻����/
		// ��ȡ���������ļ�����,����ClassFileVO,���ؿͻ���,
		// �ͻ����߼�:
		// ����_isAbsoluteSeverDir,�����false,���������·��
		// ���ݷ������˷��ص��ļ�����,�ڿͻ��˴����ļ�!!!
		// ���ؿͻ����ļ�����·�����ַ���

		ClassFileVO classFileVO = getCommonService().downLoadFile(_serverdir, _serverFileName, _isAbsoluteSeverDir);
		if (_isAbsoluteClientDir) {
			if (_clientdir == null || _clientdir.trim().equals("")) {
				throw new WLTAppException("�����ļ�ʱ,��··������Ϊ��!!");
			}
			_clientdir = UIUtil.replaceAll(_clientdir, "\\", "/"); //
			if (_clientdir.endsWith("/")) {
				throw new WLTAppException("�����ļ�ʱ,��··��������/��β!!");
			}
		} else {
			if (_clientdir != null && !_clientdir.trim().equals("")) {
				_clientdir = UIUtil.replaceAll(_clientdir, "\\", "/"); //
				if (!_clientdir.startsWith("/")) {
					throw new WLTAppException("�����ļ�ʱ,���·��������/��ʼ!!");
				}

				if (_clientdir.endsWith("/")) {
					throw new WLTAppException("�����ļ�ʱ,���·��������/��β!!");
				}
			}
		}

		String str_filepathname = null; //
		if (_isAbsoluteClientDir) {// ����Ǿ���·��
			str_filepathname = _clientdir + "/" + _clientFileName; //
		} else {
			String str_ClientCodeCache = System.getProperty("ClientCodeCache");
			if (str_ClientCodeCache.indexOf("\\") >= 0) {// �任�ͻ��˵�\\Ϊ/
				str_ClientCodeCache = UIUtil.replaceAll(str_ClientCodeCache, "\\", "/"); //
			}
			if (str_ClientCodeCache.endsWith("/")) {// ����ͻ���·�����һλΪ/��ȥ��
				str_ClientCodeCache = str_ClientCodeCache.substring(0, str_ClientCodeCache.length() - 1);
			}
			if (_clientdir == null || _clientdir.trim().equals("")) { // ��������·��,��û�ж������·��,��ֱ�ӷ���ϵͳ�ϴ�Ŀ¼�ĸ�Ŀ¼��!
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
	 * �ϴ��ļ�,���ͻ����ļ��ϴ�����������!
	 * 
	 * @param _serverdir
	 *            ��������·��,������D:/wltuploadfile�ľ���·��,Ҳ������/upload�����·����ʽ,��������·��,����Ҫʹ�û�������WLTUPLOADFILEDIR��·���������·��
	 * @param _serverFileName
	 *            �������˵��ļ���
	 * @param _isAbsoluteSeverDir
	 *            �Ƿ��Ǿ���·��,true=��,false=��
	 * @param _clientdir
	 *            �ͻ���·��,ͬ��������·��,�ȿ����Ǿ���·��,Ҳ���������·��,��������·��,����Ҫʹ�û�������CLIENTCODECACHE��·���������·��
	 * @param _clientFileName
	 *            �ͻ����ļ���
	 * @param _isAbsoluteClientDir
	 *            �ͻ���·���Ƿ��Ǿ���·��
	 * @return �����ϴ������������ļ��ľ���·��!!
	 * @throws Exception
	 */
	public synchronized static String upLoadFile(String _serverdir, String _serverFileName, boolean _isAbsoluteSeverDir, String _clientdir, String _clientFileName, boolean _isAbsoluteClientDir, boolean _isConvertHex, boolean _isAddSerialNo) throws Exception {
		if (_isAbsoluteClientDir) {
			if (_clientdir == null || _clientdir.trim().equals("")) {
				throw new WLTAppException("�ϴ��ļ�ʱ,��··������Ϊ��!!");
			}
			_clientdir = UIUtil.replaceAll(_clientdir, "\\", "/"); //
			if (_clientdir.endsWith("/")) {
				throw new WLTAppException("�ϴ��ļ�ʱ,��··��������/��β!!");
			}
		} else {
			if (_clientdir != null && !_clientdir.trim().equals("")) {
				_clientdir = UIUtil.replaceAll(_clientdir, "\\", "/"); //
				if (!_clientdir.startsWith("/")) {
					throw new WLTAppException("�ϴ��ļ�ʱ,���·��������/��ʼ!!");
				}

				if (_clientdir.endsWith("/")) {
					throw new WLTAppException("�ϴ��ļ�ʱ,���·��������/��β!!");
				}
			}
		}

		String str_filepathname = null; //
		if (_isAbsoluteClientDir) {// ����Ǿ���·��
			str_filepathname = _clientdir + "/" + _clientFileName; //
		} else {
			String str_ClientCodeCache = System.getProperty("ClientCodeCache");
			if (str_ClientCodeCache.indexOf("\\") >= 0) {// �任�ͻ��˵�\\Ϊ/
				str_ClientCodeCache = UIUtil.replaceAll(str_ClientCodeCache, "\\", "/"); //
			}
			if (str_ClientCodeCache.endsWith("/")) {// ����ͻ���·�����һλΪ/��ȥ��
				str_ClientCodeCache = str_ClientCodeCache.substring(0, str_ClientCodeCache.length() - 1);
			}

			if (_clientdir == null || _clientdir.trim().equals("")) { // ��������·��,��û�ж������·��,��ֱ�ӷ���ϵͳ�ϴ�Ŀ¼�ĸ�Ŀ¼��!
				str_filepathname = str_ClientCodeCache + "/" + _clientFileName; //
			} else {
				str_filepathname = str_ClientCodeCache + _clientdir + "/" + _clientFileName; //
			}
		}

		str_filepathname = UIUtil.replaceAll(str_filepathname, "\\", "/"); //
		File uploadfile = new File(str_filepathname);
		if (!uploadfile.exists()) {
			throw new WLTAppException("�ļ�[" + str_filepathname + "]������!!");
		}

		byte[] fileBytes = TBUtil.getTBUtil().readFromInputStreamToBytes(new FileInputStream(str_filepathname));//
		cn.com.infostrategy.to.common.ClassFileVO filevo = new cn.com.infostrategy.to.common.ClassFileVO();
		filevo.setClassFileName(_clientFileName); //�ļ���..
		filevo.setByteCodes(fileBytes); //�ļ�����..
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
	 * ���ز����ļ�!!
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
	 * �����ļ�,�Լ�ѡ��·��
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
		BillDialog dialog = new BillDialog(_parent, "Ԥ���ļ�", 1024, 738); //
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
	 * ��������ʽ,��һ��ע�Ỻ��,�ڶ�������!!
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
			str_url = str_url + "&isdispatch=Y"; //�Ƿ��ɵ�,��ʵ���������WebDispatchIfc,������WebCallBeanIfc��
		}
		try {
			Desktop.browse(new URL(str_url)); //�ȳ���ʹ��JDIC����,��Ϊ��ᱣ֤������Ĵ�����ǰ����ʾ,�������ܵ�����ȥ!!!
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

	//��������ʽ!����֧�ָ��ӵ�Java����,����ArrayList
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
	 * ��������ʽ!!
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

	//һ������ʽ,����_parMap�е�value�������ַ���!!!
	public synchronized static void openRemoteServerHtml(String _StrParCallClassName, HashMap _parMap) {
		openRemoteServerHtml(_StrParCallClassName, _parMap, false); //
	}

	/**
	 * ֱ�Ӵ�һ���ļ�,������������һ���ļ���,���Ƿ�������?�����,����ļ�!!
	 * Ϊʲô��һ���ļ�Ҫ��ô������?��Ϊ���ļ������ᷢ�����ļ�(����һ��Word)�Ĵ��ڻ���ں���!!��������ʾ����ǰ��!������ͻ�ûע��,��Ϊû��,Ȼ�����Bug
	 * �����������ʹ��Desktop.open(_file);�ͻ���ʾ��ǰ��,���Է�װ������һ������!!��xch/2012-06-15��
	 * @param _parent
	 * @param _file
	 */
	public synchronized static void openFile(java.awt.Container _parent, File _file) {
		if (!_file.exists()) {
			MessageBox.show(_parent, "�ļ�[" + _file + "]������!"); //
			return;
		}
		try {
			Desktop.open(_file); //ʹ��Desktop��,�ᱣ֤���ļ��Ĵ�������ʾ��ǰ��,�����Ƕ��ں���!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			try {
				Runtime.getRuntime().exec("explorer.exe \"" + _file.getAbsolutePath() + "\""); //�������������ִ��,���JDIC�򲻿���Ҫ����ʹ�������ֱ�Ӵ�!!!��Ϊ����������JDIC�򲻿�����,�Ƕ�����ΪĬ�����������IE��ɵ�!!NND
			} catch (Exception exx) {
				exx.printStackTrace();
			}
		}
	}

	/**
	 * ������������ʱ���б����ļ������ص�����,��ÿ�ζ���д������һ�Ѵ���,ʵ������ȫ���Է�װ��һ������!!
	 * �ؼ�����������������������һ���˲�ע�⣬һ��,������ļ��Ѵ���,Ҫ��ʾ�Ƿ񸲸ǣ��� ����,����ɹ���,Ҫ��ʾ�Ƿ�����������ļ���
	 * �˷�����ʵ����������!
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
						if (file.isDirectory()) { //��ʾ�ļ���, ����ֵֹ�! Gwang 2012-11-06
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
						if (!MessageBox.confirm(_parent, "�ļ�[" + curFile.getAbsolutePath() + "]�Ѵ���,�Ƿ񸲸�?", 450, 150)) {
							return null;
						}
					}
					FileOutputStream fout = new FileOutputStream(curFile, false); //
					fout.write(_bytes); //
					fout.close(); //
					if (MessageBox.confirm(_parent, "�����ļ�[" + curFile.getAbsolutePath() + "]�ɹ�!\r\n���Ƿ��������򿪸��ļ�?", 500, 175)) {
						UIUtil.openFile(_parent, curFile); //���ļ�
					}
					return curFile.getAbsolutePath(); //����ʵ�ʱ�����ļ���
				}
			}
			return null;
		} catch (Exception _ex) {
			MessageBox.showException(_parent, _ex); //
			return null;
		}
	}

	/**
	 * һ������ʽ!������_parMap�е�ֵ������get�����ķ�ʽ!! ���Բ���_parMap�е�value�������ַ���!!!
	 * �򿪷�����һ��Html,ָ��һ������(ʵ��WebCallBeanIfc�ӿ�),ָ������,Ȼ�󽫷�����ʵ�����Զ�����һ��html����!!
	 * @param _StrParCallClassName
	 * @param _parMap
	 */
	public synchronized static void openRemoteServerHtml(String _StrParCallClassName, HashMap _parMap, boolean _isDisPatch) {
		TBUtil tbUtil = new TBUtil(); //
		StringBuilder sb_url = new StringBuilder(); //
		sb_url.append(System.getProperty("CALLURL") + "/WebCallServlet?StrParCallClassName=" + _StrParCallClassName); //tbUtil.convertStrToHexString(_StrParCallClassName)
		if (_isDisPatch) { //�����dispatch
			sb_url.append("&isdispatch=Y"); //�Ƿ��ɵ�,��ʵ���������WebDispatchIfc,������WebCallBeanIfc��
		}
		if (_parMap != null) {
			String[] str_keys = (String[]) _parMap.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) { //���в�����key
				sb_url.append("&" + str_keys[i] + "=" + "" + _parMap.get(str_keys[i])); //tbUtil.convertStrToHexString()ѭ�������в���������,Ϊ�˽����������,ͳһת��16����!!!!
			}
		}
		try {
			Desktop.browse(new URL(sb_url.toString())); //�ȳ���ʹ��JDIC����,��Ϊ��ᱣ֤������Ĵ�����ǰ����ʾ,�������ܵ�����ȥ!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			try {
				Runtime.getRuntime().exec("explorer.exe \"" + sb_url.toString() + "\""); //�������������ִ��,���JDIC�򲻿���Ҫ����ʹ�������ֱ�Ӵ�!!!��Ϊ����������JDIC�򲻿�����,�Ƕ�����ΪĬ�����������IE��ɵ�!!NND
			} catch (Exception exx) {
				exx.printStackTrace();
			}
		}
	}

	/**
	 * ���ߴ򿪷������˵��ļ�,�����wps,doc,xls,pdf�����ֱ����������д�,������������
	 * @param _pathType ����,null��ʾֱ�Ӵ�WebRootĿ¼������,��������upload��office
	 * @param _filename
	 */
	public synchronized static void openRemoteServerFile(String _pathType, String _filename) {
		String str_url = null;
		if (_pathType == null) {
			str_url = System.getProperty("CALLURL") + "/DownLoadFileServlet?filename=" + _filename; //��WebRootĿ¼������
		} else {
			//			try {
			//				String temp = URLEncoder.encode(_filename, "GBK");//�����ȶԺ��ֱ��룬��ͨ��url����ȥ������Servlet���յ��Ժ����ײ����������롾����/2012-6-19��
			//				_filename = temp;//
			//������Ϊ�����룿��ʵ���߼����ڷ�������Ҫ����ȡֵ��encoding,Ȼ�����ȡ��ֵ���������ת������!! ������Ӧ����������д���!!��Ϊ����������ж����������룬��һ����UTF-8,�����һ���ע����!���쳤��/2012-06-20��
			//			} catch (UnsupportedEncodingException e) {
			//				e.printStackTrace();
			//			}
			str_url = System.getProperty("CALLURL") + "/DownLoadFileServlet?pathtype=" + _pathType + "&filename=" + _filename; //���Դ�ĳ������Ŀ¼������
		}
		try {
			Desktop.browse(new URL(str_url)); //�ȳ���ʹ��JDIC����,��Ϊ��ᱣ֤������Ĵ�����ǰ����ʾ,�������ܵ�����ȥ!!!
		} catch (Throwable th) {
			th.printStackTrace(); //
			try {
				Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\"");
				//Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler \"" + str_url + "\"");  //���ַ����᲻�ᱣ֤���ڲ��㵽����ȥ?
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
		BillDialog dialog = new BillDialog(_parent, "Ԥ���ļ�", 1024, 738); //
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
	 * ��д�ͻ��������ļ�,���ͻ���Java_Home\binĿ¼�½��и��ļ�prop.ini,����洢���пͻ�����Ҫ��չ��ϵͳ����(System.getProperty())
	 * ��ǰ������ֻ�ܷ�������������,�ǳ���������չ,������IE����ʱ,�ǵ�ע����еĲ�����,���Ա���Ҫ��һ�ֻ���,������չ����ϵͳ����! ��Ҫ�и��ļ�! ������������,�ϴε�¼�û�����!
	 * ��Ϊ����ϵͳʱ,�����ȶ�ȡһ������ļ�,Ȼ����ص�System.property��,����������������ͨ��-Dkey=value��Ч����һ������!!!
	 * @param _key
	 * @param _value
	 */
	public synchronized static void writeBackClientPropFile(String _key, String _value) {
		try {
			String str_fileName = System.getProperty("java.home") + "\\bin\\prop.ini"; //д������ļ���!!!
			File file = new File(str_fileName); //
			if (file.exists()) { //����ļ�����,����غ�,����,�ٻ�д!!!
				FileInputStream fins = new FileInputStream(file); //
				Properties prop = new Properties(); //
				prop.load(fins); //���ؽ���!!
				fins.close(); //
				prop.setProperty(_key, _value); //��д!!!
				FileOutputStream fout = new FileOutputStream(file, false); //��������д�ļ�!
				prop.store(fout, "Client User Props"); //�洢�����ļ�!!
				fout.close(); //�ر���!!
			} else { //����ļ�������,��ֱ��д!!!
				Properties prop = new Properties(); //
				prop.setProperty(_key, _value); //��д!!!
				FileOutputStream fout = new FileOutputStream(file, false); //
				prop.store(fout, "Client User Props"); //�洢�����ļ�!!
				fout.close(); //�ر���!!
			}
			System.setProperty(_key, _value); //������ϵͳ������һ��!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * �滻�ַ�
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
			} // ����Ҳ���,�򷵻�
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // ������ַ�������ԭ��ǰ�
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // ��ʣ��ļ���
		return str_return;
	}

	//
	public static synchronized String[] getLoginUserCorpIdName(String _deptId, String _deptLinkCode, String _deptCorpType) {
		try {
			if (_deptLinkCode != null) { //�����Linkcode,������һ��!
				String str_linkCode = _deptLinkCode; //ȡ��linkCode
				int li_cycle = str_linkCode.length() / 4; //ѭ������!!
				String[] str_parentCode = new String[li_cycle]; //
				for (int i = 0; i < li_cycle; i++) {
					str_parentCode[i] = str_linkCode.substring(i * 4, (i + 1) * 4); //
				}
				//System.out.println("����LinkCode��Ϊ��[" + str_linkCode + "],�����LinkCode����ȡ!!"); //�Ժ���ʹ��Link���Ƹ���!
				TBUtil tbUtil = new TBUtil(); //
				HashVO[] hvs_parCorps = UIUtil.getHashVoArrayByDS(null, "select id,code,name,corptype from pub_corp_dept where linkcode in (" + tbUtil.getInCondition(str_parentCode) + ") order by linkcode asc"); //
				HashVO[] hvs_allCorpMap = UIUtil.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('��������','��������')"); //���ж���

				String str_formula = null; //
				for (int i = 0; i < hvs_allCorpMap.length; i++) {
					if (hvs_allCorpMap[i].getStringValue("id").equals(_deptCorpType)) { //�ҵ��ҵĻ�������
						str_formula = hvs_allCorpMap[i].getStringValue("code"); //
						break; //
					}
				}

				if (str_formula != null && !str_formula.equals("")) {
					HashMap map = TBUtil.getTBUtil().convertStrToMapByExpress(str_formula, ";", "="); //
					String str_findCotypeType = (String) map.get("$������"); //�ҵ���ҪѰ�ҵĻ�������!!���ݺ�����ҵ�ʵ�����ҵĻ������ͣ�����
					if (str_findCotypeType != null) { //�������ҵ��ĸ�����???
						for (int i = 0; i < hvs_parCorps.length; i++) { //
							if (hvs_parCorps[i].getStringValue("corptype", "").equals(str_findCotypeType)) { //�˳�!!!
								String str_rt_id = hvs_parCorps[i].getStringValue("id"); //
								String str_rt_name = hvs_parCorps[i].getStringValue("id"); //
								return new String[] { str_rt_id, str_rt_name };
								//System.out.println("�����ҵı�������[" + userVO.getCorpID() + "/" + userVO.getDeptname() + "]");
							}
						}
					}
				}
			} else { //���û�в㼶,��ʹ�õݹ��ѯ!!
				//System.out.println("����LinkCodeΪ��,��ʹ�õݹ���Ʋ���...."); //
				HashVO[] hvs_parent = UIUtil.getParentCorpVOByMacro(3, _deptId, "$������"); //
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
	 * �жϵ�ǰ��¼��Ա�Ƿ���ĳ��ɫ�����/2016-04-25��
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
	 * �ж�ĳ���û��Ƿ���ĳ����ɫ�����/2016-04-25��
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
