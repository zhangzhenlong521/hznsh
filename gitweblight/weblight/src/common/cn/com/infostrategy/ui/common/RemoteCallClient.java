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
 * 远程访问之客户端通讯类
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
			String str_currThreadCallStack = getCurrThreadCallStackStr(); // 先计算出当前调用的堆栈,用于性能问题跟踪定位!!!
			debugClientRemoteCall(_servicename, _methodname, _parclasses, _parobjs, str_currThreadCallStack); // Debug客户端日志

			// 传入参..入参永远是一个 VectorMap
			RemoteCallParVO parVO = new RemoteCallParVO(); // !!!!!
			parVO.setClientIP(getLocalHostIP()); // /
			CurrSessionVO sessionVO = ClientEnvironment.getCurrSessionVO(); //
			if (System.getProperty("LV") != null) { // 如果指定了有LV参数,则说明是LR在调用!!
				sessionVO.setLRCall(true); //
			}
			parVO.setCurrSessionVO(sessionVO); //
			parVO.setServiceName(_servicename); //
			parVO.setMethodName(_methodname); //
			parVO.setParClasses(_parclasses); //
			parVO.setParObjs(_parobjs); //
			parVO.setCurrCallMenuName(getCallMenuName()); // 当前调用的菜单名称!
			parVO.setCurrThreadCallStack(str_currThreadCallStack); // 客户端调用堆栈!
			parVO.setCurrVersion(System.getProperty("LAST_STARTTIME")); // 当前版式本

			url = new URL(str_remotecallservlerurl); // 定义远程CallServlet的url!!!
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
			conn.setUseCaches(true); // 有缓存!!
			conn.setConnectTimeout(_readTimeOut * 1000); // 只等8秒//张珍龙改为了0，也不起作用，故改回原代码【李春娟/2018-06-25】
			conn.setReadTimeout(_readTimeOut * 1000); // _readTimeOut

			// conn.setRequestProperty("Content-type",
			// "application/x-java-serialized-object");
			// //网上说设下这个会更稳定,因为有的web服务器不能自动判别!
			conn.setRequestProperty("Content-type", "application/x-compress"); // 二进制流!!高峰在安徽农信遇到有的Win7客户端访问很慢,孙富君在中铁建也遇到有的电脑就是慢,怀疑是否没有指定这个,导致防火墙在判定时耗时太久!
			// conn.setRequestProperty("Host", "168.7.6.210"); //网上说加上这行可以省去
			// C:\windows\system32\drivers\host 中设置,即省去DNS解析过程,从而让首次握手连接很快!

			OutputStream request_out = null; // 输出流!!!!
			long ll_1_conn = System.currentTimeMillis(); // 创建连接成功的时间点!!
			// 即高总在美国访问发现速度非常慢,怀疑路由太多!
			// 即成功握手的时间特别长!!
			parVO.setConnTime((int) (ll_1_conn - ll_1)); //

			long ll_transbegin = 0;
			long ll_transend = 0;
			String str_in_desc = "握手耗时" + (ll_1_conn - ll_1) + ","; //
			if (System.getProperty("ISZIPSTREAM") != null && System.getProperty("ISZIPSTREAM").equals("Y")) { // 是否采用压缩流
				byte[] bytes = serialize(parVO); // 先序列化对象..
				// 加密处理!!
				if (System.getProperty("ISENCRYPT") != null && System.getProperty("ISENCRYPT").equals("Y")) { // 是否加密,如果是加密的则做加密处理!!
					// 设置加密时必须是压缩状态!!
					// 否则会导致数据包太大!
					long ll_des_1 = System.currentTimeMillis(); //
					int li_len_1 = bytes.length; //
					DESKeyTool desTool = new DESKeyTool(); //
					bytes = desTool.encrypt(bytes); // 使用DES加密!!!
					int li_len_2 = bytes.length; //
					long ll_des_2 = System.currentTimeMillis(); //
					str_in_desc = str_in_desc + "加密耗时" + (ll_des_2 - ll_des_1) + ",加密长度" + li_len_1 + "->" + li_len_2 + ","; //
				}
				// 压缩处理!!
				long ll_comp_1 = System.currentTimeMillis(); //
				byte[] compressBytes = compressBytes(bytes); // 压缩字节..
				long ll_comp_2 = System.currentTimeMillis(); //
				str_in_desc = str_in_desc + "压缩耗时" + (ll_comp_2 - ll_comp_1) + "," + bytes.length + "->" + compressBytes.length + "Bytes=" + (bytes.length / 1024) + "->" + (compressBytes.length / 1024) + "K,压缩比" + (compressBytes.length * 100 / bytes.length) + "%";
				if (ClientEnvironment.isOutPutCallObjToFile) { // 是否将包的16进制格式输出至文件,这是因为LoadRunner....
					writeCallParHexStrToFile(true, parVO, compressBytes); // 将调用的参数输出至文件!以便做LoadRunder时使用!
				}
				ll_transbegin = System.currentTimeMillis(); // 网络传输开始
				conn.setRequestProperty("Content-Length", "" + compressBytes.length); // 设置大小!一定要设下,有的Web服务器没这个会很慢!
				// 曾遇到有的Win7下很慢可能就是这个原因!!!
				request_out = conn.getOutputStream(); // /
				request_out.write(compressBytes); // 真正输出压缩流的地方!!!即开始网络通讯了.........
			} else { // 如果不采用压缩流..
				byte[] bytes = serialize(parVO); // 序列化!!!
				int li_length = bytes.length; //
				str_in_desc = str_in_desc + li_length + "Bytes=" + (li_length / 1024) + "K"; //
				if (ClientEnvironment.isOutPutCallObjToFile) { // 是否写文件,让LoadRunder用!
					writeCallParHexStrToFile(false, parVO, bytes); // 将调用的参数输出至文件!以便做LoadRunder时使用!
				}
				ll_transbegin = System.currentTimeMillis(); // 网络传输开始
				conn.setRequestProperty("Content-Length", "" + bytes.length); // 设置大小!一定要设下,有的Web服务器没这个会很慢!
				// 曾遇到有的Win7下很慢可能就是这个原因!!!
				request_out = conn.getOutputStream(); // /
				request_out.write(bytes); // 真正输出压缩流的地方!!!即开始网络通讯了.........
			}
			request_out.flush(); //
			request_out.close(); //

			String str_out_desc = ""; //
			Object responseObj = null; //
			InputStream response_in = conn.getInputStream(); // 取得输入流...
			// 输出服务器返回的HTTP Header,从中能验证服务器返回的Head中的Content-type与length到底是什么!!!
			// for (int i = 0;; i++) {
			// String headerName = conn.getHeaderFieldKey(i);
			// String headerValue = conn.getHeaderField(i);
			// if (headerName == null && headerValue == null) {
			// break;
			// }
			// System.out.println("[" + headerName + "]=[" + headerValue + "]");
			// //
			// }

			if (System.getProperty("ISZIPSTREAM") != null && System.getProperty("ISZIPSTREAM").equals("Y")) { // 是否采用压缩流
				byte[] inBytes = getByteFromInputStream(response_in); // 从流中取得二进制数组
				ll_transend = System.currentTimeMillis(); // 网络传输结束
				int li_in_length = inBytes.length; //

				// 解压
				long ll_decomp_1 = System.currentTimeMillis(); //
				byte[] decompressBytes = decompressBytes(inBytes); // 解压....
				long ll_decomp_2 = System.currentTimeMillis(); //
				str_out_desc = "" + li_in_length + "/" + decompressBytes.length + "Bytes=" + (li_in_length / 1024) + "/" + (decompressBytes.length / 1024) + "K,解压比" + (li_in_length * 100 / decompressBytes.length) + "%,解压耗时" + (ll_decomp_2 - ll_decomp_1); // //
				responseObj = deserialize(decompressBytes); // 从解压后的二制数组序列化一个对象...
			} else {
				ObjectInputStream request_in_objStream = new ObjectInputStream(response_in);
				responseObj = request_in_objStream.readObject(); //
				ll_transend = System.currentTimeMillis(); // 网络传输结束
				int li_length = serialize(responseObj).length; //
				str_out_desc = li_length + "Bytes=" + (li_length / 1024) + "K"; //
			}

			response_in.close(); // 关闭输出流....

			RemoteCallReturnVO returnVO = (RemoteCallReturnVO) responseObj; // 强制类型转换
			String str_sessionid = returnVO.getSessionID(); // 取得会话标识.
			if (str_sessionid != null) {
				if (ClientEnvironment.getCurrSessionVO().getHttpsessionid() == null) { // //
					ClientEnvironment.getCurrSessionVO().setHttpsessionid(str_sessionid); // 在远程调用的类中也放入这个
					System.setProperty("SESSIONID", str_sessionid); //
				}
			}

			Object returnObj = returnVO.getReturnObject(); // 在这里判断是否是Nova远程异常!!,如果是则人工抛出异常!!
			long ll_dealtime = returnVO.getDealtime(); //
			int li_stmtcount = returnVO.getCallDBCount(); //
			long ll_jvm1 = returnVO.getJVM1(); //
			long ll_jvm2 = returnVO.getJVM2(); //
			long ll_2 = System.currentTimeMillis();
			long ll_all = ll_2 - ll_1; //
			long ll_trans = (ll_transend - ll_transbegin) - ll_dealtime; // 网络传输时间,拿开始传输时间,减去实际运算时间
			long ll_others = ll_all - ll_dealtime - ll_trans; // 其他时间

			String str_currssionTrackMsg = returnVO.getCallTrackMsg(); // 此次远程调用会话的历史意见!!!
			if (str_currssionTrackMsg != null && !str_currssionTrackMsg.trim().equals("")) {
				ClientEnvironment.setCurrCallSessionTrackMsg(str_currssionTrackMsg); // 将服务器端取得的值设置一下,设了以后谁来负责清空??即如果在服务器端设置了日志,则客户端必须有对应的代码去取掉,否则会产生内存泄露!!!
			}

			if (returnObj instanceof WLTRemoteException) { // 如果远程调用异常!!
				WLTRemoteException serverNovaEx = (WLTRemoteException) returnObj; // 取得server端的异常!!
				if (serverNovaEx instanceof WLTRemoteFatalException) { // 如果是版本异常,特殊处理!!
					WLTRemoteFatalException fatalEx = (WLTRemoteFatalException) serverNovaEx; //
					JOptionPane.showMessageDialog(LoginAppletLoader.mainPanel, serverNovaEx.getMessage(), "警告", JOptionPane.ERROR_MESSAGE); //
					if (fatalEx.isExit()) {
						System.exit(0); // 如果这个严重异常是严重得必须退出的,则退出系统!!!!
					}
					return null;
				} else { // 远程调用异常!!
					String str_bsexName = serverNovaEx.getServerTargetEx().getClass().getName(); //
					String ste_exName = null; //
					if (serverNovaEx.getServerTargetEx() instanceof WLTAppException) {
						ste_exName = serverNovaEx.getMessage(); // 为了弹出时直接显示异常内容，而不是显示Java的内容!!
					} else {
						ste_exName = "(" + str_bsexName + ")" + serverNovaEx.getMessage(); //
					}

					WLTRemoteException clientNovaEx = new WLTRemoteException("" + ste_exName); // 创建客户端异常
					clientNovaEx.setServerTargetEx(serverNovaEx.getServerTargetEx()); // 设置堆栈详细信息!
					throw clientNovaEx; // 重抛客户端异常!!!
				}
			} else {
				String str_msg = "调用远程服务结束[" + _servicename + "][" + _methodname + "],共耗时[" + ll_all + "],其中应用耗时[" + ll_dealtime + "],访问DB[" + li_stmtcount + "]次,网络传输[" + ll_trans + "],其他运算[" + ll_others + "],入参【" + str_in_desc + "】,返回【" + str_out_desc + "】,服务器JVM变化[" + ll_jvm2 + "-" + ll_jvm1 + "=" + (ll_jvm2 - ll_jvm1) + "K," + ((ll_jvm2 - ll_jvm1) / 1024) + "M]";
				if (logger != null) {
					logger.info(str_msg); // 如果定义是Log4j,则用log4j输出!!
				} else {
					logger = WLTLogger.getLogger(RemoteCallClient.class);
					if (logger != null) {
						logger.info(str_msg); // 如果定义是Log4j,则用log4j输出!!
					} else {
						System.out.println("[" + UIUtil.getCurrTime() + "]" + str_msg); // 如果没有定义Log,比如在登录界面出来之前!!则直接输出
					}
				}
				return returnObj;
			}
		} catch (Exception ex) {
			debugClientRemoteCallErr("客户端远程请求[" + this.str_remotecallservlerurl + "]发生异常[" + ex.getClass().getName() + "]:[" + ex.getMessage() + "]");
			if (ex instanceof java.net.SocketTimeoutException || ex instanceof java.net.ConnectException) { // 袁江晓
				// 20180523修改，添加SocketTimeoutException异常信息处理
				// 应用于马上消费项目
				MessageBox.show("网络发生异常，请检查网络!", 2);// 警告
				return null;
			} else {
				throw ex;
			}
		} finally {
			// conn.
		}
	}

	// 取得当前选中的菜单
	private String getCallMenuName() {
		try {
			Class cls = Class.forName("cn.com.infostrategy.ui.sysapp.login.DeskTopPanel"); //
			Method method_ = cls.getMethod("getRealSelectedTabbedName", null);// 当点击菜单树打开一个菜单的时候以前的逻辑不对
			String str_title = (String) method_.invoke(null, null);
			if (str_title == null) {
				Method method = cls.getMethod("getSelectedTabbedName", null); //
				str_title = (String) method.invoke(null, null); //
			}
			if (str_title != null && str_title.length() > 5) {
				str_title = str_title.substring(5, str_title.length()); // 去掉[当前位置：]5个字!
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
			logger.info(getParToString(_servicename, _methodname, _parclasses, _parobjs, _threadCallStack)); // 如果定义是Log4j,则用log4j输出!!
		} else {
			logger = WLTLogger.getLogger(RemoteCallClient.class);
			if (logger != null) {
				logger.info(getParToString(_servicename, _methodname, _parclasses, _parobjs, _threadCallStack)); // 如果定义是Log4j,则用log4j输出!!
			} else {
				System.out.println("[" + UIUtil.getCurrTime() + "]" + getParToString(_servicename, _methodname, _parclasses, _parobjs, _threadCallStack)); // 如果没有定义Log,比如在登录界面出来之前!!则直接输出
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
		String str_return = "开始调用远程服务[" + _servicename + "][" + _methodname + "],参数:"; //
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
	 * 取得当前线程调用堆栈的字符串!
	 * @return
	 */
	private String getCurrThreadCallStackStr() {
		StackTraceElement stackTrace[] = Thread.currentThread().getStackTrace(); // 得到调用堆栈...

		ArrayList al_str = new ArrayList(); //
		for (int i = 0; i < stackTrace.length; i++) {
			String str_calssname = stackTrace[i].getClassName(); // 得到类名
			int li_pos = str_calssname.lastIndexOf("."); // 类名的最后一个
			String str_realclsname = str_calssname.substring(li_pos + 1, str_calssname.length()); // 实际的类名
			if (str_realclsname.equals("Thread") || str_realclsname.startsWith("$") || str_realclsname.equals("RemoteCallClient") || str_realclsname.equals("RemoteCallHandler") || str_realclsname.equals("EventQueue") || str_realclsname.equals("EventDispatchThread") || str_realclsname.equals("AWTEventMulticaster") || str_realclsname.equals("LightweightDispatcher") || str_realclsname.equals("Container") || str_realclsname.equals("Component") || str_realclsname.equals("JComponent")
					|| str_realclsname.startsWith("SplashWindow$")) { // 如果是动态代理或RemoteCallClient开头的则跳过!
				continue; //
			}
			al_str.add("[" + (i + 1) + "]" + str_realclsname + "." + stackTrace[i].getMethodName() + "(" + stackTrace[i].getLineNumber() + ")"); // ←
		}

		StringBuffer sb_trace = new StringBuffer(); //
		if (al_str.size() <= 20) { // 如果小于10个则全部输出!!!
			for (int i = 0; i < al_str.size(); i++) {
				sb_trace.append((String) al_str.get(i)); //
				if (i != al_str.size() - 1) {
					sb_trace.append("←"); // 不是最后一个则加箭头!!
				}
			}
		} else { // 只则只输出前5个与后5个!
			for (int i = 0; i < 5; i++) { // 前5个
				sb_trace.append(al_str.get(i) + "←"); //
			}
			sb_trace.append("....."); // 中间的跳过,以防太长!
			for (int i = (al_str.size() - 5); i < al_str.size(); i++) { // 最后5个
				sb_trace.append(al_str.get(i)); //
				if (i != al_str.size() - 1) {
					sb_trace.append("←"); // 不是最后一个则加箭头!!
				}
			}
		}
		return sb_trace.toString(); //
	}

	/**
	 * 从一个输入流取得二进制数组
	 * @param _inputstream
	 * @return
	 */
	private byte[] getByteFromInputStream(InputStream _inputstream) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			byte[] buf = new byte[2048]; // 读的时候要快!即比服务器端吐的要快一点才好,否则如果是服务器端吐的快而客户端抽的慢,则可能出现阻塞与并发数上升的情况!!
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
	 * 序列化一个对象..
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
	 * 反向序列化成一个对象
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
	 * 压缩..
	 * @param _initbytes
	 * @return
	 */
	public byte[] compressBytes(byte[] _initbytes) {
		// long ll_1 = System.currentTimeMillis(); //
		if (_initbytes.length <= 10240) { // 如果小于10K,则不压缩则是直接返回!!!这样性能会更高些!因为这时压缩的意义已不大了!!
			// System.out.println("因为数据量太小而不压缩!"); //
			byte[] returnBytes = new byte[_initbytes.length + 1]; // 如果中断了压缩过程,则直接返回原来的值,以提高性能,比如返回一个rar类型的数据!!
			returnBytes[0] = 0; // 表示不压缩!
			System.arraycopy(_initbytes, 0, returnBytes, 1, _initbytes.length); // 将源拷贝到目标中来!
			return returnBytes; //
		}

		// 计算合理的临时压缩空间,即太大与太小都不好,应合理计算!!!
		int li_size = _initbytes.length / 20; // 假设最坏的情况下,即啥都压不了,认为压缩10次,那么空间放多大!!!
		if (li_size < 5120) { // 如果小于2K,则最小是2K
			li_size = 5120;
		}
		if (li_size > 102400) { // 如果大于50K,则最大为50K
			li_size = 102400;
		}

		java.util.zip.Deflater compressor = new java.util.zip.Deflater();
		compressor.setLevel(java.util.zip.Deflater.BEST_COMPRESSION); //
		compressor.setInput(_initbytes); //
		compressor.finish(); // 真正进行压缩...
		ByteArrayOutputStream bos = new ByteArrayOutputStream(_initbytes.length); // //
		byte[] buf = new byte[li_size]; // 开100K的空间用于压缩的临时空间,因为这是在客户端,所以空间可以大点!!
		long li_cyclecount = 0; //
		boolean isBreakZip = false; // 是否中断了压缩过程!!!
		long ll_maxcycleCount = (long) ((long) _initbytes.length * 0.8 / li_size) + 2; // 最差压缩比情况下的压缩次数!!!低于60%就不处理了!
		// System.out.println("最大压缩次数=[" + ll_maxcycleCount + "]!"); //
		while (!compressor.finished()) { // 如果参数传的不对,则有可能会造
			// 成死循环!!!形成阻塞的症状!!!所有必须有一个超过最坏情况的次数,则退出循环,不进行压缩处理,而是返回原来值,则系统的健壮性大为增强!!!!!因为压缩耗时与数据量大小之间有个性能矛盾!即不压缩传输会慢,而压缩太耗时,且又压缩不了多少,则又没有意义!!所以需要一个平衡处理,这就是该处理的初衷!!
			li_cyclecount++;
			if (li_cyclecount > ll_maxcycleCount) { // 如果超过20次,则强行退出,即有些文件本身就是压缩后的文件,即没有再被压缩的空间了,则直接返回原始内容算了!!否则会造成死循环,实际情况中我发现一个rar文件,因为无压缩空间,结果造成服务器端压缩耗时8秒,客户端解压又要8秒,结果是花蛇添足,得不偿失!!!
				isBreakZip = true; //
				break; //
			}
			int count = compressor.deflate(buf); // 将数据压缩到buf中,一般来说,除了最后一次外都会返回1024,即用满了!!!!!
			bos.write(buf, 0, count);
		}
		try {
			bos.close();
		} catch (Exception e) {
			e.printStackTrace(); //
		}

		byte[] returnBytes = null; //
		if (isBreakZip) {
			returnBytes = new byte[_initbytes.length + 1]; // 如果中断了压缩过程,则直接返回原来的值,以提高性能,比如返回一个rar类型的数据!!
			returnBytes[0] = 0; // 表示不压缩!
			System.arraycopy(_initbytes, 0, returnBytes, 1, _initbytes.length); // 将源拷贝到目标中来!
		} else {
			byte[] compressedData = bos.toByteArray(); // 压缩后的大小
			returnBytes = new byte[compressedData.length + 1];
			returnBytes[0] = 1; // 表示是压缩的!
			System.arraycopy(compressedData, 0, returnBytes, 1, compressedData.length);
		}
		// long ll_2 = System.currentTimeMillis(); //
		// System.out.println("客户端提交时压缩处理,是否中断压缩=[" + isBreakZip + "],原内容大小[" +
		// _initbytes.length + "],压缩临时空间[" + li_size + "],压缩了[" + li_cyclecount
		// + "]回循环,压缩后的内容大小=[" + returnBytes.length + "],处理耗时[" + (ll_2 - ll_1)
		// + "]"); //
		return returnBytes; //
	}

	/**
	 * 解压某个字节数组
	 * @param _initbyte
	 * @return
	 */
	public byte[] decompressBytes(byte[] _initbyte) {
		// long ll_1 = System.currentTimeMillis();
		byte[] realDataBytes = new byte[_initbyte.length - 1]; // 实际数据,即去掉第一位的标识符!!!
		System.arraycopy(_initbyte, 1, realDataBytes, 0, realDataBytes.length); // 拷贝过来!!!
		if (_initbyte[0] == 0) { // 第一位是标识符,如果是0,则说明是没压缩,如果是1则说明是压缩的!
			// long ll_2 = System.currentTimeMillis(); //
			// System.out.println("客户端没有解压返回内容,共耗时[" + (ll_2 - ll_1) + "]"); //
			return realDataBytes; // 如果没压缩,则直接返回原来的
		} else {
			int li_size = _initbyte.length / 5; // 假设50%的压缩比,然后最多就膨胀20次,这样就是合理的空间!
			if (li_size < 5120) { // 如果小于5K,则最小是5K
				li_size = 5120;
			}
			if (li_size > 102400) { // 如果大于50K,则最大为50K
				li_size = 102400;
			}
			java.util.zip.Inflater decompressor = new java.util.zip.Inflater();
			decompressor.setInput(realDataBytes);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(realDataBytes.length);
			byte[] buf = new byte[li_size]; // 开100K的空间进行解压,因为这是在客户端,所以空间可以大点!!
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
			// System.out.println("客户端解压了返回内容,解压临时空间=[" + li_size + "],解压了[" +
			// ll_cyclecount + "]回"); //,共耗时[" + (ll_2 - ll_1) + "]"); //
			return decompressedData; //
		}
	}

	private void writeCallParHexStrToFile(boolean _isCompress, RemoteCallParVO _parVO, byte[] _bytes) {
		try {
			TBUtil tbUtil = new TBUtil(); //
			String str_currtime = tbUtil.getCurrTime(false, false); // //
			String str_16text = tbUtil.convertBytesToHexString(_bytes, true); // 转换成16进制!!
			File fileDir = new File("C:/156"); //
			if (!fileDir.exists()) { // 如果没有这个目录,则创建之!
				fileDir.mkdirs();
			}

			// 16进制文件!!
			FileOutputStream fout_hex = new FileOutputStream("C:/156/" + (_isCompress ? "Y" : "N") + "_par_" + str_currtime + ".hex", false); //
			fout_hex.write(str_16text.getBytes()); // 输出!!
			fout_hex.close(); //

			// 说明
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
			fout_info.write(sb_info.toString().getBytes()); // 输出!!
			fout_info.close(); //

		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

}
