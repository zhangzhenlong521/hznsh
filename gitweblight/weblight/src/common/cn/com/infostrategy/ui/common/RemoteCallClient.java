/**************************************************************************
 * $RCSfile: RemoteCallClient.java,v $  $Revision: 1.53 $  $Date: 2013/02/22 02:45:06 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.naming.java.javaURLContextFactory;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.RemoteCallParVO;
import cn.com.infostrategy.to.common.RemoteCallReturnVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.common.WLTRemoteFatalException;
import cn.com.infostrategy.ui.sysapp.login.LoginAppletLoader;

/**
 * Զ�̷���֮�ͻ���ͨѶ��
 * @author xch
 *
 */
public class RemoteCallClient implements Serializable {

	private static final long serialVersionUID = 2669192990614491136L;

	private String str_remotecallservlerurl = null;
	private static Logger logger = null;

	// private boolean bo_iszipstream = false; //

	public RemoteCallClient() {
		str_remotecallservlerurl = System.getProperty("RemoteCallServletURL"); // http://127.0.0.1:9001/pushgrc/RemoteCallServlet
	}

	public RemoteCallClient(String _url) {
		str_remotecallservlerurl = _url;
	}

	public synchronized Object callServlet(String _servicename, String _methodname, Class[] _parclasses, Object[] _parobjs, int _readTimeOut) throws WLTRemoteException, Exception {
		URL url = null;
		HttpURLConnection conn = null; //
		try {
			if (ClientEnvironment.getInstance().get("timeouttime") != null && str_remotecallservlerurl.equals(System.getProperty("RemoteCallServletURL"))) {
				ClientEnvironment.getInstance().put("timeoutlefttime", ClientEnvironment.getInstance().get("timeouttime"));
			}
			long ll_1 = System.currentTimeMillis();
			String str_currThreadCallStack = getCurrThreadCallStackStr(); // �ȼ������ǰ���õĶ�ջ,��������������ٶ�λ!!!
			debugClientRemoteCall(_servicename, _methodname, _parclasses, _parobjs, str_currThreadCallStack); // Debug�ͻ�����־

			// �����..�����Զ��һ�� VectorMap
			RemoteCallParVO parVO = new RemoteCallParVO(); // !!!!!
			parVO.setClientIP(getLocalHostIP()); // /
			CurrSessionVO sessionVO = ClientEnvironment.getCurrSessionVO(); //
			if (System.getProperty("LV") != null) { // ���ָ������LV����,��˵����LR�ڵ���!!
				sessionVO.setLRCall(true); //
			}
			parVO.setCurrSessionVO(sessionVO); //
			parVO.setServiceName(_servicename); //
			parVO.setMethodName(_methodname); //
			parVO.setParClasses(_parclasses); //
			parVO.setParObjs(_parobjs); //
			parVO.setCurrCallMenuName(getCallMenuName()); // ��ǰ���õĲ˵�����!
			parVO.setCurrThreadCallStack(str_currThreadCallStack); // �ͻ��˵��ö�ջ!
			parVO.setCurrVersion(System.getProperty("LAST_STARTTIME")); // ��ǰ��ʽ��

			url = new URL(str_remotecallservlerurl); // ����Զ��CallServlet��url!!!
			conn = (HttpURLConnection) url.openConnection(); //
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
			conn.setDoOutput(true);
			conn.setUseCaches(true); // �л���!!
			conn.setConnectTimeout(_readTimeOut * 1000); // ֻ��8��//��������Ϊ��0��Ҳ�������ã��ʸĻ�ԭ���롾���/2018-06-25��
			conn.setReadTimeout(_readTimeOut * 1000); // _readTimeOut

			// conn.setRequestProperty("Content-type",
			// "application/x-java-serialized-object");
			// //����˵�����������ȶ�,��Ϊ�е�web�����������Զ��б�!
			conn.setRequestProperty("Content-type", "application/x-compress"); // ��������!!�߷��ڰ���ũ�������е�Win7�ͻ��˷��ʺ���,�︻����������Ҳ�����еĵ��Ծ�����,�����Ƿ�û��ָ�����,���·���ǽ���ж�ʱ��ʱ̫��!
			// conn.setRequestProperty("Host", "168.7.6.210"); //����˵�������п���ʡȥ
			// C:\windows\system32\drivers\host ������,��ʡȥDNS��������,�Ӷ����״��������Ӻܿ�!

			OutputStream request_out = null; // �����!!!!
			long ll_1_conn = System.currentTimeMillis(); // �������ӳɹ���ʱ���!!
			// ���������������ʷ����ٶȷǳ���,����·��̫��!
			// ���ɹ����ֵ�ʱ���ر�!!
			parVO.setConnTime((int) (ll_1_conn - ll_1)); //

			long ll_transbegin = 0;
			long ll_transend = 0;
			String str_in_desc = "���ֺ�ʱ" + (ll_1_conn - ll_1) + ","; //
			if (System.getProperty("ISZIPSTREAM") != null && System.getProperty("ISZIPSTREAM").equals("Y")) { // �Ƿ����ѹ����
				byte[] bytes = serialize(parVO); // �����л�����..
				// ���ܴ���!!
				if (System.getProperty("ISENCRYPT") != null && System.getProperty("ISENCRYPT").equals("Y")) { // �Ƿ����,����Ǽ��ܵ��������ܴ���!!
					// ���ü���ʱ������ѹ��״̬!!
					// ����ᵼ�����ݰ�̫��!
					long ll_des_1 = System.currentTimeMillis(); //
					int li_len_1 = bytes.length; //
					DESKeyTool desTool = new DESKeyTool(); //
					bytes = desTool.encrypt(bytes); // ʹ��DES����!!!
					int li_len_2 = bytes.length; //
					long ll_des_2 = System.currentTimeMillis(); //
					str_in_desc = str_in_desc + "���ܺ�ʱ" + (ll_des_2 - ll_des_1) + ",���ܳ���" + li_len_1 + "->" + li_len_2 + ","; //
				}
				// ѹ������!!
				long ll_comp_1 = System.currentTimeMillis(); //
				byte[] compressBytes = compressBytes(bytes); // ѹ���ֽ�..
				long ll_comp_2 = System.currentTimeMillis(); //
				str_in_desc = str_in_desc + "ѹ����ʱ" + (ll_comp_2 - ll_comp_1) + "," + bytes.length + "->" + compressBytes.length + "Bytes=" + (bytes.length / 1024) + "->" + (compressBytes.length / 1024) + "K,ѹ����" + (compressBytes.length * 100 / bytes.length) + "%";
				if (ClientEnvironment.isOutPutCallObjToFile) { // �Ƿ񽫰���16���Ƹ�ʽ������ļ�,������ΪLoadRunner....
					writeCallParHexStrToFile(true, parVO, compressBytes); // �����õĲ���������ļ�!�Ա���LoadRunderʱʹ��!
				}
				ll_transbegin = System.currentTimeMillis(); // ���紫�俪ʼ
				conn.setRequestProperty("Content-Length", "" + compressBytes.length); // ���ô�С!һ��Ҫ����,�е�Web������û��������!
				// �������е�Win7�º������ܾ������ԭ��!!!
				request_out = conn.getOutputStream(); // /
				request_out.write(compressBytes); // �������ѹ�����ĵط�!!!����ʼ����ͨѶ��.........
			} else { // ���������ѹ����..
				byte[] bytes = serialize(parVO); // ���л�!!!
				int li_length = bytes.length; //
				str_in_desc = str_in_desc + li_length + "Bytes=" + (li_length / 1024) + "K"; //
				if (ClientEnvironment.isOutPutCallObjToFile) { // �Ƿ�д�ļ�,��LoadRunder��!
					writeCallParHexStrToFile(false, parVO, bytes); // �����õĲ���������ļ�!�Ա���LoadRunderʱʹ��!
				}
				ll_transbegin = System.currentTimeMillis(); // ���紫�俪ʼ
				conn.setRequestProperty("Content-Length", "" + bytes.length); // ���ô�С!һ��Ҫ����,�е�Web������û��������!
				// �������е�Win7�º������ܾ������ԭ��!!!
				request_out = conn.getOutputStream(); // /
				request_out.write(bytes); // �������ѹ�����ĵط�!!!����ʼ����ͨѶ��.........
			}
			request_out.flush(); //
			request_out.close(); //

			String str_out_desc = ""; //
			Object responseObj = null; //
			InputStream response_in = conn.getInputStream(); // ȡ��������...
			// ������������ص�HTTP Header,��������֤���������ص�Head�е�Content-type��length������ʲô!!!
			// for (int i = 0;; i++) {
			// String headerName = conn.getHeaderFieldKey(i);
			// String headerValue = conn.getHeaderField(i);
			// if (headerName == null && headerValue == null) {
			// break;
			// }
			// System.out.println("[" + headerName + "]=[" + headerValue + "]");
			// //
			// }

			if (System.getProperty("ISZIPSTREAM") != null && System.getProperty("ISZIPSTREAM").equals("Y")) { // �Ƿ����ѹ����
				byte[] inBytes = getByteFromInputStream(response_in); // ������ȡ�ö���������
				ll_transend = System.currentTimeMillis(); // ���紫�����
				int li_in_length = inBytes.length; //

				// ��ѹ
				long ll_decomp_1 = System.currentTimeMillis(); //
				byte[] decompressBytes = decompressBytes(inBytes); // ��ѹ....
				long ll_decomp_2 = System.currentTimeMillis(); //
				str_out_desc = "" + li_in_length + "/" + decompressBytes.length + "Bytes=" + (li_in_length / 1024) + "/" + (decompressBytes.length / 1024) + "K,��ѹ��" + (li_in_length * 100 / decompressBytes.length) + "%,��ѹ��ʱ" + (ll_decomp_2 - ll_decomp_1); // //
				responseObj = deserialize(decompressBytes); // �ӽ�ѹ��Ķ����������л�һ������...
			} else {
				ObjectInputStream request_in_objStream = new ObjectInputStream(response_in);
				responseObj = request_in_objStream.readObject(); //
				ll_transend = System.currentTimeMillis(); // ���紫�����
				int li_length = serialize(responseObj).length; //
				str_out_desc = li_length + "Bytes=" + (li_length / 1024) + "K"; //
			}

			response_in.close(); // �ر������....

			RemoteCallReturnVO returnVO = (RemoteCallReturnVO) responseObj; // ǿ������ת��
			String str_sessionid = returnVO.getSessionID(); // ȡ�ûỰ��ʶ.
			if (str_sessionid != null) {
				if (ClientEnvironment.getCurrSessionVO().getHttpsessionid() == null) { // //
					ClientEnvironment.getCurrSessionVO().setHttpsessionid(str_sessionid); // ��Զ�̵��õ�����Ҳ�������
					System.setProperty("SESSIONID", str_sessionid); //
				}
			}

			Object returnObj = returnVO.getReturnObject(); // �������ж��Ƿ���NovaԶ���쳣!!,��������˹��׳��쳣!!
			long ll_dealtime = returnVO.getDealtime(); //
			int li_stmtcount = returnVO.getCallDBCount(); //
			long ll_jvm1 = returnVO.getJVM1(); //
			long ll_jvm2 = returnVO.getJVM2(); //
			long ll_2 = System.currentTimeMillis();
			long ll_all = ll_2 - ll_1; //
			long ll_trans = (ll_transend - ll_transbegin) - ll_dealtime; // ���紫��ʱ��,�ÿ�ʼ����ʱ��,��ȥʵ������ʱ��
			long ll_others = ll_all - ll_dealtime - ll_trans; // ����ʱ��

			String str_currssionTrackMsg = returnVO.getCallTrackMsg(); // �˴�Զ�̵��ûỰ����ʷ���!!!
			if (str_currssionTrackMsg != null && !str_currssionTrackMsg.trim().equals("")) {
				ClientEnvironment.setCurrCallSessionTrackMsg(str_currssionTrackMsg); // ����������ȡ�õ�ֵ����һ��,�����Ժ�˭���������??������ڷ���������������־,��ͻ��˱����ж�Ӧ�Ĵ���ȥȡ��,���������ڴ�й¶!!!
			}

			if (returnObj instanceof WLTRemoteException) { // ���Զ�̵����쳣!!
				WLTRemoteException serverNovaEx = (WLTRemoteException) returnObj; // ȡ��server�˵��쳣!!
				if (serverNovaEx instanceof WLTRemoteFatalException) { // ����ǰ汾�쳣,���⴦��!!
					WLTRemoteFatalException fatalEx = (WLTRemoteFatalException) serverNovaEx; //
					JOptionPane.showMessageDialog(LoginAppletLoader.mainPanel, serverNovaEx.getMessage(), "����", JOptionPane.ERROR_MESSAGE); //
					if (fatalEx.isExit()) {
						System.exit(0); // �����������쳣�����صñ����˳���,���˳�ϵͳ!!!!
					}
					return null;
				} else { // Զ�̵����쳣!!
					String str_bsexName = serverNovaEx.getServerTargetEx().getClass().getName(); //
					String ste_exName = null; //
					if (serverNovaEx.getServerTargetEx() instanceof WLTAppException) {
						ste_exName = serverNovaEx.getMessage(); // Ϊ�˵���ʱֱ����ʾ�쳣���ݣ���������ʾJava������!!
					} else {
						ste_exName = "(" + str_bsexName + ")" + serverNovaEx.getMessage(); //
					}

					WLTRemoteException clientNovaEx = new WLTRemoteException("" + ste_exName); // �����ͻ����쳣
					clientNovaEx.setServerTargetEx(serverNovaEx.getServerTargetEx()); // ���ö�ջ��ϸ��Ϣ!
					throw clientNovaEx; // ���׿ͻ����쳣!!!
				}
			} else {
				String str_msg = "����Զ�̷������[" + _servicename + "][" + _methodname + "],����ʱ[" + ll_all + "],����Ӧ�ú�ʱ[" + ll_dealtime + "],����DB[" + li_stmtcount + "]��,���紫��[" + ll_trans + "],��������[" + ll_others + "],��Ρ�" + str_in_desc + "��,���ء�" + str_out_desc + "��,������JVM�仯[" + ll_jvm2 + "-" + ll_jvm1 + "=" + (ll_jvm2 - ll_jvm1) + "K," + ((ll_jvm2 - ll_jvm1) / 1024) + "M]";
				if (logger != null) {
					logger.info(str_msg); // ���������Log4j,����log4j���!!
				} else {
					logger = WLTLogger.getLogger(RemoteCallClient.class);
					if (logger != null) {
						logger.info(str_msg); // ���������Log4j,����log4j���!!
					} else {
						System.out.println("[" + UIUtil.getCurrTime() + "]" + str_msg); // ���û�ж���Log,�����ڵ�¼�������֮ǰ!!��ֱ�����
					}
				}
				return returnObj;
			}
		} catch (Exception ex) {
			debugClientRemoteCallErr("�ͻ���Զ������[" + this.str_remotecallservlerurl + "]�����쳣[" + ex.getClass().getName() + "]:[" + ex.getMessage() + "]");
			if (ex instanceof java.net.SocketTimeoutException || ex instanceof java.net.ConnectException) { // Ԭ����
				// 20180523�޸ģ����SocketTimeoutException�쳣��Ϣ����
				// Ӧ��������������Ŀ
				MessageBox.show("���緢���쳣����������!", 2);// ����
				return null;
			} else {
				throw ex;
			}
		} finally {
			// conn.
		}
	}

	// ȡ�õ�ǰѡ�еĲ˵�
	private String getCallMenuName() {
		try {
			Class cls = Class.forName("cn.com.infostrategy.ui.sysapp.login.DeskTopPanel"); //
			Method method_ = cls.getMethod("getRealSelectedTabbedName", null);// ������˵�����һ���˵���ʱ����ǰ���߼�����
			String str_title = (String) method_.invoke(null, null);
			if (str_title == null) {
				Method method = cls.getMethod("getSelectedTabbedName", null); //
				str_title = (String) method.invoke(null, null); //
			}
			if (str_title != null && str_title.length() > 5) {
				str_title = str_title.substring(5, str_title.length()); // ȥ��[��ǰλ�ã�]5����!
			}
			return str_title;
		} catch (Throwable ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	private String getLocalHostIP() {
		try {
			String str_ip = System.getProperty("ClientLocalIP"); //
			if (str_ip != null) {
				return str_ip; //
			}
			str_ip = InetAddress.getLocalHost().getHostAddress(); //
			System.setProperty("ClientLocalIP", str_ip); //
			return str_ip; //
		} catch (Exception ex) {
			System.setProperty("ClientLocalIP", "0.0.0.0"); //
			return "0.0.0.0"; //
		}
	}

	private void debugClientRemoteCall(String _servicename, String _methodname, Class[] _parclasses, Object[] _parobjs, String _threadCallStack) {
		if (logger != null) {
			logger.info(getParToString(_servicename, _methodname, _parclasses, _parobjs, _threadCallStack)); // ���������Log4j,����log4j���!!
		} else {
			logger = WLTLogger.getLogger(RemoteCallClient.class);
			if (logger != null) {
				logger.info(getParToString(_servicename, _methodname, _parclasses, _parobjs, _threadCallStack)); // ���������Log4j,����log4j���!!
			} else {
				System.out.println("[" + UIUtil.getCurrTime() + "]" + getParToString(_servicename, _methodname, _parclasses, _parobjs, _threadCallStack)); // ���û�ж���Log,�����ڵ�¼�������֮ǰ!!��ֱ�����
			}
		}
	}

	private void debugClientRemoteCallErr(String _msg) {
		if (logger != null) {
			logger.error(_msg); //
		} else {
			logger = WLTLogger.getLogger(RemoteCallClient.class);
			if (logger != null) {
				logger.error(_msg); //
			} else {
				System.err.println(_msg); //
			}
		}
	}

	private String getParToString(String _servicename, String _methodname, Class[] _parclasses, Object[] _parobjs, String _threadCallStack) {
		String str_return = "��ʼ����Զ�̷���[" + _servicename + "][" + _methodname + "],����:"; //
		if (_parobjs != null) {
			for (int i = 0; i < _parobjs.length; i++) {
				String str_parvalue = "" + _parobjs[i]; //
				if (str_parvalue.length() > 50) {
					str_parvalue = str_parvalue.substring(0, 50) + "......"; //
				}
				str_return = str_return + "[<" + _parclasses[i].getName() + ">" + str_parvalue + "]"; // //
			}
		} else {
			str_return = str_return + "[null]"; //
		}

		if (ClientEnvironment.getInstance().getLog4jConfigVO() != null && ClientEnvironment.getInstance().getLog4jConfigVO().getClient_level().equalsIgnoreCase("DEBUG")) {
			str_return = str_return + "  StackTrace:" + _threadCallStack;
		}
		return str_return;
	}

	/**
	 * ȡ�õ�ǰ�̵߳��ö�ջ���ַ���!
	 * @return
	 */
	private String getCurrThreadCallStackStr() {
		StackTraceElement stackTrace[] = Thread.currentThread().getStackTrace(); // �õ����ö�ջ...

		ArrayList al_str = new ArrayList(); //
		for (int i = 0; i < stackTrace.length; i++) {
			String str_calssname = stackTrace[i].getClassName(); // �õ�����
			int li_pos = str_calssname.lastIndexOf("."); // ���������һ��
			String str_realclsname = str_calssname.substring(li_pos + 1, str_calssname.length()); // ʵ�ʵ�����
			if (str_realclsname.equals("Thread") || str_realclsname.startsWith("$") || str_realclsname.equals("RemoteCallClient") || str_realclsname.equals("RemoteCallHandler") || str_realclsname.equals("EventQueue") || str_realclsname.equals("EventDispatchThread") || str_realclsname.equals("AWTEventMulticaster") || str_realclsname.equals("LightweightDispatcher") || str_realclsname.equals("Container") || str_realclsname.equals("Component") || str_realclsname.equals("JComponent")
					|| str_realclsname.startsWith("SplashWindow$")) { // ����Ƕ�̬�����RemoteCallClient��ͷ��������!
				continue; //
			}
			al_str.add("[" + (i + 1) + "]" + str_realclsname + "." + stackTrace[i].getMethodName() + "(" + stackTrace[i].getLineNumber() + ")"); // ��
		}

		StringBuffer sb_trace = new StringBuffer(); //
		if (al_str.size() <= 20) { // ���С��10����ȫ�����!!!
			for (int i = 0; i < al_str.size(); i++) {
				sb_trace.append((String) al_str.get(i)); //
				if (i != al_str.size() - 1) {
					sb_trace.append("��"); // �������һ����Ӽ�ͷ!!
				}
			}
		} else { // ֻ��ֻ���ǰ5�����5��!
			for (int i = 0; i < 5; i++) { // ǰ5��
				sb_trace.append(al_str.get(i) + "��"); //
			}
			sb_trace.append("....."); // �м������,�Է�̫��!
			for (int i = (al_str.size() - 5); i < al_str.size(); i++) { // ���5��
				sb_trace.append(al_str.get(i)); //
				if (i != al_str.size() - 1) {
					sb_trace.append("��"); // �������һ����Ӽ�ͷ!!
				}
			}
		}
		return sb_trace.toString(); //
	}

	/**
	 * ��һ��������ȡ�ö���������
	 * @param _inputstream
	 * @return
	 */
	private byte[] getByteFromInputStream(InputStream _inputstream) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buf = new byte[2048]; // ����ʱ��Ҫ��!���ȷ��������µ�Ҫ��һ��ź�,��������Ƿ��������µĿ���ͻ��˳����,����ܳ��������벢�������������!!
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
	 * ���л�һ������..
	 * @param _obj
	 * @return
	 */
	private byte[] serialize(Object _obj) {
		ByteArrayOutputStream buf = null; //
		ObjectOutputStream out = null; //
		try {
			buf = new ByteArrayOutputStream();
			out = new ObjectOutputStream(buf);
			out.writeObject(_obj);
			byte[] bytes = buf.toByteArray();
			return bytes; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		} finally {
			try {
				out.close(); //
			} catch (Exception e) {
			}
		}
	}

	/**
	 * �������л���һ������
	 * @return
	 */
	private Object deserialize(byte[] _bytes) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new ByteArrayInputStream(_bytes));
			Object obj = in.readObject();
			in.close();
			return obj; //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				in.close(); //
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * ѹ��..
	 * @param _initbytes
	 * @return
	 */
	public byte[] compressBytes(byte[] _initbytes) {
		// long ll_1 = System.currentTimeMillis(); //
		if (_initbytes.length <= 10240) { // ���С��10K,��ѹ������ֱ�ӷ���!!!�������ܻ����Щ!��Ϊ��ʱѹ���������Ѳ�����!!
			// System.out.println("��Ϊ������̫С����ѹ��!"); //
			byte[] returnBytes = new byte[_initbytes.length + 1]; // ����ж���ѹ������,��ֱ�ӷ���ԭ����ֵ,���������,���緵��һ��rar���͵�����!!
			returnBytes[0] = 0; // ��ʾ��ѹ��!
			System.arraycopy(_initbytes, 0, returnBytes, 1, _initbytes.length); // ��Դ������Ŀ������!
			return returnBytes; //
		}

		// ����������ʱѹ���ռ�,��̫����̫С������,Ӧ�������!!!
		int li_size = _initbytes.length / 20; // ������������,��ɶ��ѹ����,��Ϊѹ��10��,��ô�ռ�Ŷ��!!!
		if (li_size < 5120) { // ���С��2K,����С��2K
			li_size = 5120;
		}
		if (li_size > 102400) { // �������50K,�����Ϊ50K
			li_size = 102400;
		}

		java.util.zip.Deflater compressor = new java.util.zip.Deflater();
		compressor.setLevel(java.util.zip.Deflater.BEST_COMPRESSION); //
		compressor.setInput(_initbytes); //
		compressor.finish(); // ��������ѹ��...
		ByteArrayOutputStream bos = new ByteArrayOutputStream(_initbytes.length); // //
		byte[] buf = new byte[li_size]; // ��100K�Ŀռ�����ѹ������ʱ�ռ�,��Ϊ�����ڿͻ���,���Կռ���Դ��!!
		long li_cyclecount = 0; //
		boolean isBreakZip = false; // �Ƿ��ж���ѹ������!!!
		long ll_maxcycleCount = (long) ((long) _initbytes.length * 0.8 / li_size) + 2; // ���ѹ��������µ�ѹ������!!!����60%�Ͳ�������!
		// System.out.println("���ѹ������=[" + ll_maxcycleCount + "]!"); //
		while (!compressor.finished()) { // ����������Ĳ���,���п��ܻ���
			// ����ѭ��!!!�γ�������֢״!!!���б�����һ�����������Ĵ���,���˳�ѭ��,������ѹ������,���Ƿ���ԭ��ֵ,��ϵͳ�Ľ�׳�Դ�Ϊ��ǿ!!!!!��Ϊѹ����ʱ����������С֮���и�����ì��!����ѹ���������,��ѹ��̫��ʱ,����ѹ�����˶���,����û������!!������Ҫһ��ƽ�⴦��,����Ǹô���ĳ���!!
			li_cyclecount++;
			if (li_cyclecount > ll_maxcycleCount) { // �������20��,��ǿ���˳�,����Щ�ļ��������ѹ������ļ�,��û���ٱ�ѹ���Ŀռ���,��ֱ�ӷ���ԭʼ��������!!����������ѭ��,ʵ��������ҷ���һ��rar�ļ�,��Ϊ��ѹ���ռ�,�����ɷ�������ѹ����ʱ8��,�ͻ��˽�ѹ��Ҫ8��,����ǻ�������,�ò���ʧ!!!
				isBreakZip = true; //
				break; //
			}
			int count = compressor.deflate(buf); // ������ѹ����buf��,һ����˵,�������һ���ⶼ�᷵��1024,��������!!!!!
			bos.write(buf, 0, count);
		}
		try {
			bos.close();
		} catch (Exception e) {
			e.printStackTrace(); //
		}

		byte[] returnBytes = null; //
		if (isBreakZip) {
			returnBytes = new byte[_initbytes.length + 1]; // ����ж���ѹ������,��ֱ�ӷ���ԭ����ֵ,���������,���緵��һ��rar���͵�����!!
			returnBytes[0] = 0; // ��ʾ��ѹ��!
			System.arraycopy(_initbytes, 0, returnBytes, 1, _initbytes.length); // ��Դ������Ŀ������!
		} else {
			byte[] compressedData = bos.toByteArray(); // ѹ����Ĵ�С
			returnBytes = new byte[compressedData.length + 1];
			returnBytes[0] = 1; // ��ʾ��ѹ����!
			System.arraycopy(compressedData, 0, returnBytes, 1, compressedData.length);
		}
		// long ll_2 = System.currentTimeMillis(); //
		// System.out.println("�ͻ����ύʱѹ������,�Ƿ��ж�ѹ��=[" + isBreakZip + "],ԭ���ݴ�С[" +
		// _initbytes.length + "],ѹ����ʱ�ռ�[" + li_size + "],ѹ����[" + li_cyclecount
		// + "]��ѭ��,ѹ��������ݴ�С=[" + returnBytes.length + "],�����ʱ[" + (ll_2 - ll_1)
		// + "]"); //
		return returnBytes; //
	}

	/**
	 * ��ѹĳ���ֽ�����
	 * @param _initbyte
	 * @return
	 */
	public byte[] decompressBytes(byte[] _initbyte) {
		// long ll_1 = System.currentTimeMillis();
		byte[] realDataBytes = new byte[_initbyte.length - 1]; // ʵ������,��ȥ����һλ�ı�ʶ��!!!
		System.arraycopy(_initbyte, 1, realDataBytes, 0, realDataBytes.length); // ��������!!!
		if (_initbyte[0] == 0) { // ��һλ�Ǳ�ʶ��,�����0,��˵����ûѹ��,�����1��˵����ѹ����!
			// long ll_2 = System.currentTimeMillis(); //
			// System.out.println("�ͻ���û�н�ѹ��������,����ʱ[" + (ll_2 - ll_1) + "]"); //
			return realDataBytes; // ���ûѹ��,��ֱ�ӷ���ԭ����
		} else {
			int li_size = _initbyte.length / 5; // ����50%��ѹ����,Ȼ����������20��,�������Ǻ���Ŀռ�!
			if (li_size < 5120) { // ���С��5K,����С��5K
				li_size = 5120;
			}
			if (li_size > 102400) { // �������50K,�����Ϊ50K
				li_size = 102400;
			}
			java.util.zip.Inflater decompressor = new java.util.zip.Inflater();
			decompressor.setInput(realDataBytes);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(realDataBytes.length);
			byte[] buf = new byte[li_size]; // ��100K�Ŀռ���н�ѹ,��Ϊ�����ڿͻ���,���Կռ���Դ��!!
			long ll_cyclecount = 0;
			while (!decompressor.finished()) {
				ll_cyclecount++;
				try {
					int count = decompressor.inflate(buf);
					bos.write(buf, 0, count);
				} catch (DataFormatException e) {
				}
			}
			try {
				bos.close();
			} catch (IOException e) {
			}

			byte[] decompressedData = bos.toByteArray();
			// long ll_2 = System.currentTimeMillis(); //
			// System.out.println("�ͻ��˽�ѹ�˷�������,��ѹ��ʱ�ռ�=[" + li_size + "],��ѹ��[" +
			// ll_cyclecount + "]��"); //,����ʱ[" + (ll_2 - ll_1) + "]"); //
			return decompressedData; //
		}
	}

	private void writeCallParHexStrToFile(boolean _isCompress, RemoteCallParVO _parVO, byte[] _bytes) {
		try {
			TBUtil tbUtil = new TBUtil(); //
			String str_currtime = tbUtil.getCurrTime(false, false); // //
			String str_16text = tbUtil.convertBytesToHexString(_bytes, true); // ת����16����!!
			File fileDir = new File("C:/156"); //
			if (!fileDir.exists()) { // ���û�����Ŀ¼,�򴴽�֮!
				fileDir.mkdirs();
			}

			// 16�����ļ�!!
			FileOutputStream fout_hex = new FileOutputStream("C:/156/" + (_isCompress ? "Y" : "N") + "_par_" + str_currtime + ".hex", false); //
			fout_hex.write(str_16text.getBytes()); // ���!!
			fout_hex.close(); //

			// ˵��
			FileOutputStream fout_info = new FileOutputStream("C:/156/" + (_isCompress ? "Y" : "N") + "_par_" + str_currtime + ".info", false); //
			StringBuilder sb_info = new StringBuilder(); //
			sb_info.append("ServiceName=[" + _parVO.getServiceName() + "]\r\n"); //
			sb_info.append("MethodName=[" + _parVO.getMethodName() + "]\r\n"); //
			sb_info.append("\r\n"); // //
			Class[] clses = _parVO.getParClasses(); //
			Object[] objs = _parVO.getParObjs(); //
			if (clses != null && clses.length > 0) {
				for (int i = 0; i < clses.length; i++) {
					sb_info.append("Class[" + (i + 1) + "]=[" + clses[i] + "]\r\n"); //
					sb_info.append("Object[" + (i + 1) + "]=[" + objs[i] + "]\r\n"); //
					sb_info.append("\r\n");
				}
			}
			fout_info.write(sb_info.toString().getBytes()); // ���!!
			fout_info.close(); //

		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

}
