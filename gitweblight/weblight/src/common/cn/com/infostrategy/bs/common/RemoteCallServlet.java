/**************************************************************************
 * $RCSfile: RemoteCallServlet.java,v $  $Revision: 1.73 $  $Date: 2012/12/11 05:32:25 $
 **************************************************************************/
package cn.com.infostrategy.bs.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.RemoteCallParVO;
import cn.com.infostrategy.to.common.RemoteCallReturnVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.common.WLTRemoteFatalException;
import cn.com.infostrategy.to.sysapp.login.LoginOneOffVO;

/**
 * ����Զ�̷��ʵ��õ�Servlet..
 * ��Servletһ��Ҫע���̰߳�ȫ����!!,��Ҫ�������,����ͻ���ֶ��̵߳İ�ȫ����!
 * sunfujun/20121208/�ʴ����׳ɹ���
 * @author xch
 *
 */
public class RemoteCallServlet extends HttpServlet { //implements SingleThreadModel { //���߳�ģʽ,��ÿ�����󶼻ᴴ��һ��ʵ��,

	private static final long serialVersionUID = 7103285415882911348L;

	private boolean ifGetInstanceFromPool = true; //�Ƿ�ӳ���ȡʵ��,����ֱ���½�ʵ��.

	private Logger logger = WLTLogger.getLogger(RemoteCallServlet.class);

	//private boolean bo_iszipstream = false; //�Ƿ�ѹ����..
	private TBUtil tbUtil = new TBUtil(); //
	private BSUtil bsUtil = null; //

	public static Long THREADCOUNT = new Long(0); //��¼�̲߳�����
	public static long MAXTHREADCOUNT = 0; //��¼�̲߳�����
	public static long TOTALTHREADCOUNT = 0; //��¼

	public static ArrayList callThreadTimeList = new ArrayList(); //��¼ÿ���̵߳��õ�ʱ��! ����ͳ���Ƿ���ĺ�æ!!
	public static Hashtable revokeThreadHashtable = new Hashtable(); //ʵ�ʵ��õ��̵߳Ĵ洢��!

	//public static Hashtable callThreadHashTable = new Hashtable(); //key�������_��ʼʱ��,value�ǽ���ʱ��!! ��ʼ����ʱ,װ��key,value��0,����ʱ����Value! �������߳���ɾ����value��ֵ,������ЩvalueΪ0����ʱʵ��̫����!!
	//�Ӷ��������ǰʵ���ڴ�����߳�! ����ÿ���̵߳Ĵ���ʱ��! ����ÿ����������! ���Ӧ��һ����,��ÿ���߳���,ƽ����Ӧʱ��! Ȼ������һ��ȫ�ֱ���! Ȼ������һ�ű�! һ��8Сʱ

	public void init() throws ServletException {
		super.init();
	}

	protected void doGet(HttpServletRequest arg0, HttpServletResponse _response) throws ServletException, IOException {
		super.doGet(arg0, _response); //
	}

	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		super.doPost(arg0, arg1);
	}

	protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		super.service(arg0, arg1);
	}

	public void service(ServletRequest _request, ServletResponse _response) throws ServletException, IOException {
		String str_threadid = addThreadCount();
		try {
			dealCall(_request, _response); //�µĵ��÷���,����ҪWrapperUtil��
		} catch (ServletException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			e.printStackTrace(); //
		} finally {
			delThreadCount(str_threadid); //
		}
	}

	/**
	 * �����߳�����¼,����Ҫͬ��.. 
	 * �����_��ǰʱ����뵽һ��Vector��!Ȼ�������Ϊ�ôλỰ��key! �ڵ��ý���ʱɾ����ֵ! Ȼ�������߳�ѭ��ɾ������ 
	 */
	public String addThreadCount() { //
		long ll_newId = 0; //
		//synchronized (RemoteCallServlet.THREADCOUNT) { //����!
		ll_newId = RemoteCallServlet.THREADCOUNT++; //��1��������Ϊ�ҵ�key
		//}
		long ll_currtime = System.currentTimeMillis(); //
		//String str_threadId = ll_newId + "_" + ll_currtime; //
		//callThreadHashTable.put(str_threadId,new Long(0));  //�������! �Ժ�Ϳ���һ������������ÿ���������(�������)!�ܼƷ��ʴ���(ǰ�)! ��ǰ���ߵ��߳���,�Լ������̵߳Ĵ���ʱ��(��ǰʱ���ȥ���)!!!
		//�������ÿ��ķ������ܴ������ʱ������2��δ����,������Ȼ�Ѵ���ʱ��Ҳ��������,��˵�����ر�����!! ����ʵ���������൫��Ӧ�첢����ʾ�Բ���,���������ٵ���ӦҲ��Ҳ����! ֻ�з�Ӧ������������æ��! 
		callThreadTimeList.add(ll_currtime); //��ƨ�ɺ������!!!

		RemoteCallServlet.TOTALTHREADCOUNT++; //ֻ��!
		if (RemoteCallServlet.THREADCOUNT > RemoteCallServlet.MAXTHREADCOUNT) { //�����ǰ�߳�����������߳���,���������߳���!!!
			RemoteCallServlet.MAXTHREADCOUNT = RemoteCallServlet.THREADCOUNT;
		}
		return null; //str_threadId; //
	}

	/**
	 * �����߳�����¼,����Ҫͬ��..
	 */
	public void delThreadCount(String _threadId) {
		//callThreadHashTable.put(str_threadId,new Long(System.currentTimeMillis()));  //ת��Value,ɾ���������߳�����ȫ,�����߳�ͬʱ���𽫽�����뵽���ݿ�!! ��ÿ��ķ�Ӧ����,��ʱ��,ƽ��ʱ��!
		RemoteCallServlet.THREADCOUNT = RemoteCallServlet.THREADCOUNT - 1; //
	}

	/**
	 * ���ݲ����е�class������ʵ��ֵ,�������ʵ��ֵ
	 * @param _request
	 * @param _response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void dealCall(ServletRequest _request, ServletResponse _response) throws ServletException, IOException {
		//System.out.println("�ͻ����������������:[" + _request.getContentType() + "],��������ݴ�С["  + _request.getContentLength() + "]");  //
		//logger.info("��ʼ����RemoteCallServlet....."); //
		long ll_1 = System.currentTimeMillis(); //���˷�������ʱ��
		_response.setCharacterEncoding("UTF-8"); //�ַ���!!ָ��������,��һ����ʲôUnixϵͳϵͳĬ���ַ������,���ܻ�����������!
		_response.setBufferSize(10240); //��������Ϊ50K,ping ����������64K,�Ƿ���ζ��̫��İ�Ҳ��������粻��?�����chunkedģʽ�ķ���ģʽ,����Win7�º���,��ʹ�ĳ�Content-Length,ҲҪҪ������������,����˵�����������������! ���ҷ��������ͬ�������˺�,�������边Ȼû��,������Ȼ�õ�һ�����õ�,�ѵ��Ṳ�����?
		_response.setContentType("text/html"); //ֱ��д��text/html�������ܵ�,�������еķ���ǽ���ʱ��Ͷ���?��application/x-compress����application/x-java-serialized-object����������!!�߷��ڰ���ũ�������е�Win7�ͻ��˷��ʺ���,�︻����������Ҳ�����еĵ��Ծ�����,�����Ƿ�û��ָ�����,���·���ǽ���ж�ʱ��ʱ̫��!
		String str_newsessionID = ((HttpServletRequest) _request).getSession().getId(); //ȡ��SessionID,һ�λỰ��Ψһ�Ա�ʶ,��һ�η���ʱ���������ͻ���!!;  //
		String str_clientsessionID = null; //
		RemoteCallParVO par_vo = null;
		try {
			//��ȡ���..!
			InputStream request_in = _request.getInputStream(); //�õ�������!!!!!
			String str_in_desc = ""; //
			Object obj = null;
			if (ServerEnvironment.getProperty("ISZIPSTREAM") != null && ServerEnvironment.getProperty("ISZIPSTREAM").equals("Y")) { //�������ѹ����
				CompressBytesVO compVO = getByteFromInputStreamByMark(request_in); //������ȡ�ö���������
				//System.out.println("�ӿͻ��˴�������ֵ��ѹ����[" + compVO.isZip() + "]"); //
				int li_in_length = compVO.getBytes().length; //
				//��ѹ����!!!
				byte[] decompressBytes = null;
				long ll_decom_1 = System.currentTimeMillis(); //
				if (!compVO.isZip()) { //�������ѹ����,��ֱ�ӵ���,�������ܻ�����!
					decompressBytes = compVO.getBytes(); //
				} else {
					decompressBytes = decompressBytes(compVO.getBytes()); //��ѹ
				}
				long ll_decom_2 = System.currentTimeMillis(); //
				int li_in_compresslength = decompressBytes.length; //��ѹ���С
				str_in_desc = str_in_desc + "��ѹ��ʱ" + (ll_decom_2 - ll_decom_1) + "," + li_in_length + "->" + li_in_compresslength + "Bytes=" + (li_in_length / 1024) + "->" + (li_in_compresslength / 1024) + "K,��ѹ��" + (li_in_compresslength == 0 ? 0 : (li_in_length * 100 / li_in_compresslength)) + "%";
				//���ܴ���!!!
				if (ServerEnvironment.getProperty("ISENCRYPT") != null && ServerEnvironment.getProperty("ISENCRYPT").equals("Y")) { //ֻ�ж�������Խ���!! ֻ����ѹ��״̬�²�֧�ּ�������ܴ���!!!
					long ll_des_1 = System.currentTimeMillis(); //
					int li_len_1 = decompressBytes.length; //
					DESKeyTool desTool = new DESKeyTool(); //
					decompressBytes = desTool.decrypt(decompressBytes); //
					int li_len_2 = decompressBytes.length; //
					long ll_des_2 = System.currentTimeMillis(); //
					str_in_desc = str_in_desc + ",���ܺ�ʱ" + (ll_des_2 - ll_des_1) + ",���ܳ���" + li_len_1 + "->" + li_len_2 + ""; //
				}
				obj = deserialize(decompressBytes); //�ӽ�ѹ��Ķ����������л�һ������...
			} else { //���������ѹ����
				ObjectInputStream request_in_objStream = new ObjectInputStream(request_in); //
				obj = request_in_objStream.readObject(); //
				int li_length = serialize(obj).length;
				str_in_desc = li_length + "Bytes=" + (li_length / 1024) + "K"; //
				request_in_objStream.close();//��ǰû�йر�����Loadrunner����200�����ᱨ�����鼰ʱ�رա����/2017-03-16��
			}

			request_in.close(); //
			par_vo = (RemoteCallParVO) obj; //ǿ������ת��!!!
			int li_connTime = par_vo.getConnTime(); //�ͻ��˴����������ֵ�ʱ��! ����������������ʱ�ٶȺ���,����������������!!��·��̫����!!!
			str_in_desc = "���ֺ�ʱ" + li_connTime + "," + str_in_desc; //

			//ͳ���߳���!
			ThreadGroup threadGroup = Thread.currentThread().getThreadGroup(); //
			int li_ac = -1, li_agc = -1; //
			if (threadGroup != null) {
				li_ac = threadGroup.activeCount(); //JDK��API˵����ܲ�׼!!
				li_agc = threadGroup.activeGroupCount(); //
			}

			//����ڴ�ӽ�980M,���÷�����,��֤ϵͳ������!!
			if (!ServerEnvironment.isLoadRunderCall) { //�����LoadRunder�ڲ���,������Щ����,�Ӷ���֤���ܸܺ�!!
				long ll_jvm_total = Runtime.getRuntime().totalMemory() / (1024 * 1024); //
				long ll_jvm_busy = ll_jvm_total - (Runtime.getRuntime().freeMemory() / (1024 * 1024)); //
				int li_maxjvm = 1200; //JVM����Ĭ��ֵ!��ǰ����������900,̫С��!ʵ�����ǿ��Դﵽ1456��!
				if (ServerEnvironment.getProperty("MAXJVM") != null && !ServerEnvironment.getProperty("MAXJVM").equals("")) { //ũ�м��ʴ���Ŀ��������̫��!���Ը���
					li_maxjvm = Integer.parseInt(ServerEnvironment.getProperty("MAXJVM")); //
				}
				if (ll_jvm_busy > li_maxjvm) { //����ܿ�������1000M,��æ���ڴ泬��800M,����ʾ���÷���,�����ڴ����֮ǰ��ǰ����!!!������������outOfMemorey���������Ȼ���������!!
					System.gc(); //�����ͷ��ڴ�
					String str_errtext = "�������ջ[" + ll_jvm_busy + "," + ll_jvm_total + "]�������緧ֵ[" + li_maxjvm + "M],���Ժ�����!";
					logger.info(str_errtext); //
					returnExceptionMsg(str_errtext, _response, ll_1, false); //
					System.gc(); //�����ͷ��ڴ�
					return;
				}

				//�������100������,���÷�����,��֤������!!
				//				if (RemoteCallServlet.THREADCOUNT >100) { //����ֵ,һ��ҪС���м�������������޲�������!!!���������ܱ�֤�м����������!!!
				//					String str_errtext = "ϵͳ����������100,����������!!!"; //
				//					logger.warn(str_errtext); //
				//					returnExceptionMsg(str_errtext, _response, ll_1, false); //
				//					return;
				//				}

				//���������������� �Ž��в��������Ŀ��� ����������������Ĳ���ʱ��׼���вο����� sunfujun
				// ��ʵ���̷߳ŵ�һ��hashtable�ﲢ����Ӧ�þ�׼�� ����ô��� ֮ǰ���߼�
				int maxcurrthreadcount = SystemOptions.getIntegerValue("ϵͳ����������", 0);
				if (maxcurrthreadcount > 0) {
					if (RemoteCallServlet.THREADCOUNT > maxcurrthreadcount) { //����ֵ,һ��ҪС���м�������������޲�������!!!���������ܱ�֤�м����������!!! 
						String str_errtext = "ϵͳ����������" + maxcurrthreadcount + ",���Ժ����!"; //
						logger.warn(str_errtext); //
						returnExceptionMsg(str_errtext, _response, ll_1, false); //
						return;
					}
				}

				if (ServerEnvironment.getProperty("EFFECTLIMITDATE") != null) { //���������ϵͳ��Ч����!
					SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE); //
					String str_currdate = sdf_curr.format(new Date(ll_1));
					if (str_currdate.compareTo(ServerEnvironment.getProperty("EFFECTLIMITDATE")) >= 0) {
						String str_errtext = "ϵͳ�汾�ѹ���,���뿪������ϵ�������µ�License��!"; //
						logger.info(str_errtext); //
						returnExceptionMsg(str_errtext, _response, ll_1, false); //
						return;
					}
				}

				//�����������,������!!!
				if (par_vo.getCurrVersion() != null && !par_vo.getCurrVersion().equals(ServerEnvironment.getProperty("LAST_STARTTIME"))) {
					String str_newversion = ServerEnvironment.getProperty("LAST_STARTTIME"); //�µİ汾��
					String str_datatime = str_newversion.substring(0, 4) + "-" + str_newversion.substring(4, 6) + "-" + str_newversion.substring(6, 8) + " " + str_newversion.substring(8, 10) + ":" + str_newversion.substring(10, 12) + ":" + str_newversion.substring(12, 14); //
//					String str_errtext = "��������[" + str_datatime + "]����������,����ǿ���˳�,�˳��������µ�¼ϵͳ����!!"; //
					String str_errtext = "��½��ʱ,����ǿ���˳�,�˳��������µ�¼ϵͳ����!!"; //zzl[2019-2-13]
					logger.info(str_errtext); //
					returnExceptionMsg(str_errtext, _response, ll_1, true); //
					return;
				}
			}

			str_clientsessionID = par_vo.getCurrSessionVO().getHttpsessionid(); //ȡ�ÿͻ���SesionID.
			if (str_clientsessionID == null) {
				str_clientsessionID = str_newsessionID; //����ǵ�һ��,�ͻ��˹�����Ϊ��,���õ�ǰ�ĸ�����!
			}

			String clientIP1 = _request.getRemoteAddr(); //par_vo.getClientIP(); //ȡ��Request�����һ�����ʵ���ԴIP��ַ
			String clientIP2 = par_vo.getClientIP(); //ȡ�ÿͻ���IP��ַ
			String serverInsName = _request.getLocalAddr() + "_" + _request.getLocalPort();//IP�Ӷ˿ڱ�ʾ������ʵ����
			CurrSessionVO currSessionVO = par_vo.getCurrSessionVO(); //�õ�������ǰ�ỰVO!!
			if (currSessionVO == null) {
				currSessionVO = new CurrSessionVO();
			}
			currSessionVO.setHttpsessionid(str_clientsessionID);
			currSessionVO.setClientIP1(clientIP1); //����IP1,�ڷ���������,��������紫����,�Ӷ��������,�������IPҲֻ���ڷ�������ȡ��,�ڿͻ���ֻ��ȡ��ip2
			currSessionVO.setClientIP2(clientIP2); //����IP2!
			if (!ServerEnvironment.isLoadRunderCall) { //�����LoadRunder�ڲ���,������Щ����,�Ӷ���֤���ܸܺ�!!
				currSessionVO.setSessionCallInfo(getRemoteCallParInfo(par_vo)); //�������е�����Ϣ
			}
			String serviceName = par_vo.getServiceName(); //������!
			String methodName = par_vo.getMethodName(); //������!

			String str_implClassName = ServicePoolFactory.getInstance().getImplClassName(serviceName); //ʵ������!!
			RemoteCallReturnVO returnVO = null;

			long ll_beforeInvoke = 0; //
			if (str_implClassName == null) {
				throw new Exception("û�������������ļ�[weblight.xml]��ע����Ϊ[" + serviceName + "]��Զ�̷��ʷ���!");
			} else {
				GenericObjectPool pool = null;
				Object instanceObj = null; //ʵ��
				if (ifGetInstanceFromPool) {
					try {
						pool = ServicePoolFactory.getInstance().getPool(serviceName); //ȡ�ó�
						instanceObj = pool.borrowObject(); //�ӳ��еõ�ʵ��!!!!
						//System.out.println("�ɹ��ӳ���ץȡ����ʵ��[" + str_implClassName + "],���е�ǰ�[" + pool.getNumActive() + "]"); //
					} catch (java.lang.ClassNotFoundException ex) {
						ex.printStackTrace();
						instanceObj = new WLTRemoteException("�ӳ��г�ȡ����ʵ��[" + str_implClassName + "]ʧ��,ԭ��:�Ҳ�������!"); //	Ӧ�����������������ͬ����Ҫִ��realinvoke/sunfujun/20121127
					} catch (Exception ex) {
						ex.printStackTrace();
						instanceObj = new WLTRemoteException("�ӳ��г�ȡ����ʵ��[" + str_implClassName + "]ʧ��,ԭ��:" + ex.getMessage()); //
					}
				} else {
					try {
						instanceObj = Class.forName(str_implClassName).newInstance(); //���ﶼ��ֱ�Ӵ���ʵ����,Ч�ʽϵ�,�Ժ���Կ�����Spring����..
						//System.out.println("�ɹ�ֱ�Ӵ�������ʵ��[" + str_implClassName + "]"); //
					} catch (Exception ex) {
						ex.printStackTrace();
						instanceObj = new WLTRemoteException("ֱ�Ӵ�������ʵ��[" + str_implClassName + "]ʧ��,ԭ��:" + ex.getMessage()); //
					}
				}

				if (instanceObj instanceof WLTRemoteException) { //�������ʧ��,��ֱ�ӷ��ص���һ��NovaRemoteException
					returnVO = new RemoteCallReturnVO(); //
					returnVO.setServiceName(serviceName); //ֱ�ӵ�ʵ����
					returnVO.setServiceImplName(str_implClassName); //ֱ�ӵ�ʵ����
					returnVO.setReturnObject(instanceObj); //
					returnVO.setDealtime(System.currentTimeMillis() - ll_1); //
					returnVO.setCallDBCount(0); //
					returnVO.setSessionID(str_clientsessionID); //
				} else { //�������ʵ���ɹ�!!
					long ll_beginInvoke = System.currentTimeMillis(); //������ʼ����
					ll_beforeInvoke = ll_beginInvoke - ll_1;
					returnVO = realInvoke(serverInsName, clientIP1, clientIP2, ll_beginInvoke, par_vo.getCurrCallMenuName(), par_vo.getCurrThreadCallStack(), serviceName, str_implClassName, instanceObj, methodName, par_vo.getParClasses(), par_vo.getParObjs(), currSessionVO); //��������!!,���ĵش�!!!!!!!!!!!!!!!
					returnVO.setSessionID(str_clientsessionID); //
					if (ifGetInstanceFromPool) { //����Ǵӳ���ȡ����Ҫ�ͷ�!!
						pool.returnObject(instanceObj); //�ͷ�!!
					}
				}
			}

			//����ǰ�����û�!!!
			String str_loginuser = currSessionVO.getLoginUserCode(); //
			if (str_implClassName.equals("cn.com.infostrategy.bs.sysapp.login.SysAppServiceImpl") && methodName.equals("loginOut")) { //������˳�ϵͳ����,����
				//����¼
			} else {
				String str_currtime = tbUtil.getCurrTime(); //�������˵�ǰʱ��..
				Object objuser = ServerEnvironment.getLoginUserMap().get(str_clientsessionID); //����SessionIdȥȡ
				if (SystemOptions.getBooleanValue("�Ƿ���֤�ظ���½", false)) {
					if (str_implClassName.equals("cn.com.infostrategy.bs.sysapp.login.SysAppServiceImpl") && methodName.equals("loginOneOff")) { // ����ǵ�½
						if (returnVO.getReturnObject() instanceof LoginOneOffVO) {
							str_loginuser = ((LoginOneOffVO) returnVO.getReturnObject()).getCurrLoginUserVO().getCode();
							if (objuser == null) { //���ûȡ��
								ServerEnvironment.getLoginUserMap().put(str_clientsessionID, new String[] { str_clientsessionID, clientIP1, clientIP2, str_loginuser, str_currtime, str_currtime }); //����һ��
								int li_currAllUserCount = ServerEnvironment.getLoginUserMap().size(); //��ǰ�û�����!!!
								if (li_currAllUserCount > ServerEnvironment.EVER_MAX_ONLINEUSERS) { //�����ǰ�û��������������������û�������,�����֮!!!
									ServerEnvironment.EVER_MAX_ONLINEUSERS = li_currAllUserCount; //
								}
							} else { //���ȡ��
								String[] str_onlineusers = (String[]) objuser; //
								ServerEnvironment.getLoginUserMap().put(str_clientsessionID, new String[] { str_clientsessionID, clientIP1, clientIP2, str_loginuser, str_onlineusers[4], str_currtime }); //��������¼ʱ��
							}
							// �������� ���ǰѱ��˵�session�ɵ�
							killOtherSession(str_clientsessionID, str_loginuser);
							//
						}
					} else {
						if (str_loginuser != null && objuser == null) { //���ûȡ��
							String str_errtext = "�û����ڱ𴦵�½,���˳�ϵͳ!";
							logger.info(str_errtext); //
							returnExceptionMsg(str_errtext, _response, ll_1, true);
							return;
						}
					}
				} else {
					if (objuser == null) { //���ûȡ��
						ServerEnvironment.getLoginUserMap().put(str_clientsessionID, new String[] { str_clientsessionID, clientIP1, clientIP2, str_loginuser, str_currtime, str_currtime }); //����һ��
						int li_currAllUserCount = ServerEnvironment.getLoginUserMap().size(); //��ǰ�û�����!!!
						if (li_currAllUserCount > ServerEnvironment.EVER_MAX_ONLINEUSERS) { //�����ǰ�û��������������������û�������,�����֮!!!
							ServerEnvironment.EVER_MAX_ONLINEUSERS = li_currAllUserCount; //
						}
					} else { //���ȡ��
						String[] str_onlineusers = (String[]) objuser; //
						ServerEnvironment.getLoginUserMap().put(str_clientsessionID, new String[] { str_clientsessionID, clientIP1, clientIP2, str_loginuser, str_onlineusers[4], str_currtime }); //��������¼ʱ��
					}
				}
			}

			int li_dbcount = returnVO.getCallDBCount(); ////
			long ll_beginpush = System.currentTimeMillis(); //�������˿�ʼ�����ʱ��!
			String str_returnpars = outPutToclient(_response, returnVO); //������ͻ���!!!
			long ll_endpush = System.currentTimeMillis(); //�������!

			long li_busyJVM1 = returnVO.getJVM1(); //
			long li_busyJVM2 = returnVO.getJVM2(); //
			logger.info("[" + clientIP1 + "][" + clientIP2 + "][" + str_loginuser + "],���÷���[" + str_implClassName + "][" + methodName + "],�߼������ʱ[" + returnVO.getDealtime() + "],����ǰ��ѹ��ʱ[" + ll_beforeInvoke + "],�����ʱ[" + (ll_endpush - ll_beginpush) + "],����DB[" + li_dbcount + "]��,(" + Thread.currentThread().getName() + "),��Ρ�" + str_in_desc + "��,���Ρ�" + str_returnpars + "��,��ǰ����[" + li_ac + ","
					+ li_agc + "," + RemoteCallServlet.THREADCOUNT + "],��󲢷�[" + RemoteCallServlet.MAXTHREADCOUNT + "],JVM�仯[" + li_busyJVM2 + "-" + li_busyJVM1 + "=" + (li_busyJVM2 - li_busyJVM1) + "K," + ((li_busyJVM2 - li_busyJVM1) / 1024) + "M]"); //
		} catch (Throwable ex) {
			ex.printStackTrace(); //
			RemoteCallReturnVO returnVO = new RemoteCallReturnVO(); //
			WLTRemoteException remoteEX = new WLTRemoteException(ex.getMessage());
			remoteEX.setServerTargetEx(ex);
			returnVO.setReturnObject(remoteEX); //
			returnVO.setDealtime(System.currentTimeMillis() - ll_1);
			returnVO.setCallDBCount(-1);
			returnVO.setSessionID(str_clientsessionID); //
			outPutToclient(_response, returnVO); //������ͻ���!!!
		}
	}

	/**
	 * ȡ��Զ�̵��ò�����!!
	 * @return
	 */
	private String getRemoteCallParInfo(RemoteCallParVO _parVO) {
		String str_currtime = tbUtil.getCurrTime(); //�������˵�ǰʱ��..
		StringBuilder sb_info = new StringBuilder(); //
		sb_info.append("ʱ��:[" + str_currtime + "],IP:[" + _parVO.getClientIP() + "],�û���:[" + (_parVO.getCurrSessionVO() == null ? "null" : _parVO.getCurrSessionVO().getLoginUserName()) + "],����:[" + _parVO.getServiceName() + "],������:[" + _parVO.getMethodName() + "()],����:{<br>"); //
		sb_info.append(getCallParObjStr(_parVO.getParClasses(), _parVO.getParObjs(), true)); //
		sb_info.append("}");
		return sb_info.toString();
	}

	/**
	 * �û��ظ���½���˲���
	 * @param newsessionid
	 * @param usercode
	 */
	private void killOtherSession(String newsessionid, String usercode) { // ��http://�������ĸ�Ҳ�䵽�������ˣ�����Ϊ����ˢ��������Ⱥʵ������ҲҪ�õ��������
		if (!"".equals(ServerEnvironment.getProperty("ClusterIPPorts", ""))) {
			String[] realadress = TBUtil.getTBUtil().split(ServerEnvironment.getProperty("ClusterIPPorts"), ";");
			for (int i = 0; i < realadress.length; i++) {
				try {
					HttpURLConnection conn = (HttpURLConnection) new URL(realadress[i] + "/WebCallServlet?StrParCallClassName=cn.com.infostrategy.bs.common.ClientOLMaService&isdispatch=Y&actype=kil&sessionid=" + newsessionid + "&usercode=" + usercode).openConnection();
					if (realadress[i].startsWith("https")) {
						new BSUtil().addHttpsParam(conn);
					}
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setUseCaches(false);
					conn.setConnectTimeout(10000);//��������Ϊ��0��Ҳ�������ã��ʸĻ�ԭ���롾���/2018-06-25��
					conn.setReadTimeout(10000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			HashMap olusermap = ServerEnvironment.getLoginUserMap();
			if (olusermap != null && olusermap.size() > 0) {
				String[] keys = (String[]) olusermap.keySet().toArray(new String[0]);
				for (int i = 0; i < olusermap.size(); i++) {
					if (!newsessionid.equals(keys[i]) && usercode.equals(((String[]) olusermap.get(keys[i]))[3])) {
						olusermap.remove(keys[i]);
					}
				}
			}
		}
	}

	/**
	 * ȡ�ò������ַ���
	 * @param clses
	 * @param objs
	 * @param _ishtml
	 * @return
	 */
	private String getCallParObjStr(Class[] clses, Object[] objs, boolean _ishtml) {
		if (clses != null && clses.length > 0) {
			StringBuffer sb_info = new StringBuffer(); //
			for (int i = 0; i < clses.length; i++) {
				String str_obj = "" + objs[i]; ////
				if (str_obj.length() > 500) {
					str_obj = str_obj.substring(0, 500); //
				}
				sb_info.append("��" + clses[i].getName() + "��=��" + str_obj + "��"); //
				if (_ishtml) {
					sb_info.append("<br>"); //
				} else {
					sb_info.append("��"); //
				}
			}
			String str_return = sb_info.toString(); //
			if (str_return.length() > 4000) { //�������4000���ַ������ȡ֮����
				str_return = str_return.substring(0, 4000); //
			}
			return str_return; //
		}

		return "";

	}

	/**
	 * ������ͻ���...
	 * @param _response
	 * @param _returnVO
	 */
	private String outPutToclient(ServletResponse _response, RemoteCallReturnVO _returnVO) {
		String str_return = ""; ////
		try {
			OutputStream outStream = _response.getOutputStream(); //
			if (ServerEnvironment.getProperty("ISZIPSTREAM") != null && ServerEnvironment.getProperty("ISZIPSTREAM").equals("Y")) { //�Ƿ�ѹ��������
				byte[] bytes = serialize(_returnVO); //
				long ll_1 = System.currentTimeMillis(); //
				CompressBytesVO compressVO = compressBytes(bytes); //ѹ���ֽ�
				long ll_2 = System.currentTimeMillis(); //
				byte[] compressBytes = compressVO.getBytes(); //
				str_return = compressBytes.length + "/" + bytes.length + "Bytes=" + (compressBytes.length / 1024) + "/" + (bytes.length / 1024) + "K,ѹ����" + (compressBytes.length * 100 / bytes.length) + "%,ѹ����ʱ" + (ll_2 - ll_1);
				_response.setContentLength(compressBytes.length + 1); //������������,���������������Ϳ��ܻ�������ӷǳ���,������ǰWin7�����������������ԭ��!!!
				streamOutPutToClient(outStream, new Boolean(compressVO.isZip()), compressBytes); //��һλ��ָ���Ƿ�ѹ��!
			} else {
				byte[] bytes = serialize(_returnVO); //
				int li_length = bytes.length; //���ض����С..
				str_return = li_length + "Bytes=" + (li_length / 1024) + "K"; //
				_response.setContentLength(bytes.length); //������������,���������������Ϳ��ܻ�������ӷǳ���,������ǰWin7�����������������ԭ��!!!
				streamOutPutToClient(outStream, null, bytes); //
			}
			outStream.flush(); //
			outStream.close(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		return str_return; ////
	}

	/**
	 * ����ʽ������󣬼����Կ����������������ÿ�����ֻ���������KB������
	 * @param _out
	 * @param bytes
	 * @throws Exception
	 */
	private void streamOutPutToClient(OutputStream _out, Boolean _isZip, byte[] bytes) throws Exception {
		if (_isZip != null) {
			if (!_isZip) { //�����ѹ��!!
				_out.write(new byte[] { 0 }); //
			} else { //�����ѹ��!!
				_out.write(new byte[] { 1 }); //
			}
		}
		int li_cycleSize = 2048; //ÿ��ѭ���������,ԽСԽϸԽ����,��������,����1024��̫С,��2048-5012�����ϸ�������!
		int li_start = 0; //����������ʱ�ֲ�����
		while (1 == 1) { //����ѭ��,��ͻ����������,ֱ�����ݽ���������ͨ��ÿ���1K���ݾ���Ϣһ��ʱ��ķ�ʽ����������,ֻҪ����
			if ((li_start + li_cycleSize) >= bytes.length - 1) { //�����β����λ���ѳ����ļ�����
				_out.write(bytes, li_start, bytes.length - li_start); //
				break; //�ж�ѭ��
			} else {
				_out.write(bytes, li_start, li_cycleSize); //ÿ��ѭ��ֻ���1024�ֽڵ�����,��1K������
				li_start = li_start + li_cycleSize; //λ�������1024
			}
		}
		_out.flush(); //��ջ�����������,��ʽָ��ʵ�����!!!
	}

	/**
	 * ��������!!
	 * @param _instanceObj
	 * @param _methodName
	 * @param _classes
	 * @param _parObjs
	 * @param _clientEnv 
	 * @return
	 */
	private RemoteCallReturnVO realInvoke(String serverInsName, String _clientIP1, String _clientIP2, long _beginTime, String _callMenuName, String _callThreadStack, String _serviceName, String _implClassName, Object _instanceObj, String _methodName, Class[] _classes, Object[] _parObjs, CurrSessionVO _currSessionVO) {
		WLTInitContext initContext = new WLTInitContext(); //
		initContext.regisCurrSession(_currSessionVO); //ע�������ͻ��˻���,��LoadRunderѹ������,�������Ǹ���ʱ�ĵط�! �������������Ϊ�÷���ͬ����ɵ�! ���ڲ��������ͬ��ʱ������Ŷ�����!!
		//long ll_endRegSession = System.currentTimeMillis(); //

		long li_busyJVM1 = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024; //
		String str_keyId = null; //
		if (!ServerEnvironment.isLoadRunderCall) {
			String[] str_initValue = new String[] { null, "" + _beginTime, "N", (_currSessionVO == null ? "" : _currSessionVO.getLoginUserName()), _implClassName, _methodName, getCallParObjStr(_classes, _parObjs, false), null, null, null, "" + li_busyJVM1, null, _callMenuName, _callThreadStack, "Y", "", serverInsName }; //"�̱߳��","��ʼʱ��","�Ƿ��з�Ӧ","������","������","������","����ֵ","ʱ��",ȡ����С,ȡ����ϸ(sqls),����ǰ��JVM,���ʺ��JVM,�˵���,�ͻ��˶�ջ!,�Ƿ�ɹ�,�쳣��Ϣ, ������ʵ����
			str_keyId = getNewId("" + _beginTime); //��ֵ!!
			str_initValue[0] = str_keyId; //
			revokeThreadHashtable.put(str_keyId, str_initValue); //���ͽ�ȥ!!!
		}

		RemoteCallReturnVO returnVO = new RemoteCallReturnVO();
		try {
			Method mtehod = _instanceObj.getClass().getMethod(_methodName, _classes);
			Object obj = mtehod.invoke(_instanceObj, _parObjs); //���ö�Ӧ��������ֵ!!!
			//long ll_endRealInvoke = System.currentTimeMillis(); //
			int li_allStmtCount = commitTrans(initContext); //�ύ�ôλỰ����������!!��LoadRunderѹ������,�������Ǹ���ʱ�ĵط�!
			long ll_endCommitTrans = System.currentTimeMillis(); //
			//logger.debug("Invoke���ù���ʱ[" + (ll_endCommitTrans - _beginTime) + "],����ע��session[" + (ll_endRegSession - _beginTime) + "],����ִ��[" + (ll_endRealInvoke - ll_endRegSession) + "],�ύ�����ʱ[" + (ll_endCommitTrans - ll_endRealInvoke) + "]"); //�����Ҫ������ϸ����,�������ע��!
			long li_busyJVM2 = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024; //

			returnVO.setServiceName(_serviceName); //����ӿ���!
			returnVO.setServiceImplName(_implClassName); //������ʵ������!
			returnVO.setReturnObject(obj); //
			returnVO.setDealtime(ll_endCommitTrans - _beginTime); //
			returnVO.setCallDBCount(li_allStmtCount); //����DB����!
			returnVO.setJVM1(li_busyJVM1); //
			returnVO.setJVM2(li_busyJVM2); //

			String str_forclientTackMsg = initContext.getCurrSessionCustStrInfoByKey("ForClientTackMsg", true); //
			if (str_forclientTackMsg != null && !str_forclientTackMsg.trim().equals("")) { //�����Ϊ��!!!
				returnVO.setCallTrackMsg(str_forclientTackMsg); //
			}
			return returnVO; //
		} catch (InvocationTargetException ex) { //����Ƿ�������쳣
			int li_allStmtCount = rollbackTrans(initContext); //�ع��ôλỰ����������!���뱣֤
			//ex.getTargetException().printStackTrace(); //����Ҫ��װԶ���쳣			
			Throwable targetex = ex.getTargetException();
			//java.lang.IllegalStateException���ͱ���ȼ��������/2017-09-30��
			if (targetex instanceof java.sql.SQLException || targetex instanceof java.lang.IllegalStateException) { //̫ƽ���SQL������Ҫ��أ� ���ͱ���ȼ��� Gwang 2017-7-12 
				logger.warn("����Զ�̷���[" + _implClassName + "][" + _methodName + "()]����", ex.getTargetException()); //
			} else if ("loginOneOff".equals(_methodName) || "cancelTask".equals(_methodName) || "getFirstTaskVO".equals(_methodName)) { //̫ƽ������ͱ���ȼ��� Gwang 2017-8-16
				logger.warn("����Զ�̷���[" + _implClassName + "][" + _methodName + "()]����", ex.getTargetException()); //
			} else {
				logger.error("����Զ�̷���[" + _implClassName + "][" + _methodName + "()]����", ex.getTargetException()); //
			}

			String str_message = targetex.getMessage(); //
			WLTRemoteException novaEx = new WLTRemoteException(str_message); //����Զ���쳣���Է��ؿͻ���!!
			novaEx.setServerTargetEx(targetex); //���ö�ջ
			long ll_2 = System.currentTimeMillis(); //
			long li_busyJVM2 = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024; //
			returnVO.setServiceName(_serviceName); //����ӿ���!
			returnVO.setServiceImplName(_implClassName); //������ʵ������!
			returnVO.setReturnObject(novaEx); //
			returnVO.setDealtime(ll_2 - _beginTime); //��ʱ
			returnVO.setCallDBCount(li_allStmtCount); //����DB����!
			returnVO.setJVM1(li_busyJVM1); //
			returnVO.setJVM2(li_busyJVM2); //
			return returnVO;
		} catch (Throwable ex) {
			int li_allStmtCount = rollbackTrans(initContext); //�ع��ôλỰ����������
			ex.printStackTrace();
			WLTRemoteException novaEx = new WLTRemoteException(ex.getMessage()); //
			novaEx.setServerTargetEx(ex); //
			long ll_2 = System.currentTimeMillis(); //
			long li_busyJVM2 = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024; //
			returnVO.setServiceName(_serviceName); //����ӿ���!
			returnVO.setServiceImplName(_implClassName); //������ʵ������!
			returnVO.setReturnObject(novaEx); //
			returnVO.setDealtime(ll_2 - _beginTime); //��ʱ
			returnVO.setCallDBCount(li_allStmtCount); //����DB����!
			returnVO.setJVM1(li_busyJVM1); //
			returnVO.setJVM2(li_busyJVM2); //
			return returnVO;
		} finally { //�ر������ͷ���Դ!!
			try {
				//long ll_xch_1 = System.currentTimeMillis(); //
				if (!ServerEnvironment.isLoadRunderCall) { //�������LoadRunder�ڵ��òŽ��м�ش���! ����LoadRunder����ѹ������ʱ,�ɴ�ֱ�ӹرյ��������!!!
					long ll_2 = System.currentTimeMillis(); //
					long ll_jvmused = returnVO.getJVM2() - returnVO.getJVM1(); //
					ArrayList al_allGetDataSQLs = (ArrayList) initContext.getCurrSessionCustInfoByKey("ȡ����С"); //����ǰ�Ự�е�ȡ����С�õ�
					long ll_dataSize = 0; //
					String str_sqls = null;
					if (al_allGetDataSQLs != null) { //�����ֵ!!!
						StringBuffer sb_sqls = new StringBuffer(); //
						for (int i = 0; i < al_allGetDataSQLs.size(); i++) { //
							String[] str_allGetDataSQLs = (String[]) al_allGetDataSQLs.get(i); //
							long ll_itemSize = Long.parseLong(str_allGetDataSQLs[1]); //
							ll_dataSize = ll_dataSize + ll_itemSize; //�ۼ�!!!
							//if (ll_itemSize > 10240) { //����SQL��䳬��10K,��SQL������!
							sb_sqls.append(str_allGetDataSQLs[0] + "[" + ll_itemSize + "]��"); //�ָ�
							//}
						}
						str_sqls = sb_sqls.toString(); //
						if (str_sqls.length() > 3700) { //�Ժ�Ҫʹ�����ķ�ʽ�ж�!!!
							str_sqls = str_sqls.substring(0, 3700); //
						}
					}
					if ((ll_2 - _beginTime) < WLTConstants.THREAD_OVERTIME_VALUE && ll_dataSize < WLTConstants.THREAD_OVERWEIGHT_VALUE && ll_jvmused < WLTConstants.THREAD_OVERJVM_VALUE) { //�����5���ڵ�,���Ҵ�С��300K���ڵ�,��JVM�ڴ������5M����,��ɾ��֮(��Ϊ��������������),������Զ������!!Ȼ������һ�������߳�ȥȡ֮!!!
						if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {//�����ؽ��׳ɹ��ʾͲ�ɾ���������߳�ɾ��
							String[] str_value = (String[]) revokeThreadHashtable.get(str_keyId); //////
							str_value[2] = "Y"; //����ѽ���
							str_value[7] = "" + (ll_2 - _beginTime); //ʱ��,��ʱ��,��ɾ��!!�����������߳���ɾ����!!!��֮,���������[��ʱ��]�����ļ�¼,��˵���Ǵ���û���ع���,�Ǹ�������!!!˵������������!!
							str_value[8] = "" + ll_dataSize; //����K
							str_value[9] = str_sqls; //
							str_value[11] = "" + returnVO.getJVM2(); //���ʺ��JVM,����ǰ��JVM�ڹ���ʱ�Ѿ�������!!!
							revokeThreadHashtable.put(str_keyId, str_value); //������ȥ!!!!���Ǽ�¼�ܹ����˶���ʱ��!!
						} else {
							revokeThreadHashtable.remove(str_keyId); //
						}
					} else { //�������10��,����,���¼����!!
						String[] str_value = (String[]) revokeThreadHashtable.get(str_keyId); //////
						str_value[2] = "Y"; //����ѽ���
						str_value[7] = "" + (ll_2 - _beginTime); //ʱ��,��ʱ��,��ɾ��!!�����������߳���ɾ����!!!��֮,���������[��ʱ��]�����ļ�¼,��˵���Ǵ���û���ع���,�Ǹ�������!!!˵������������!!
						str_value[8] = "" + ll_dataSize; //����K
						str_value[9] = str_sqls; //
						str_value[11] = "" + returnVO.getJVM2(); //���ʺ��JVM,����ǰ��JVM�ڹ���ʱ�Ѿ�������!!!
						revokeThreadHashtable.put(str_keyId, str_value); //������ȥ!!!!���Ǽ�¼�ܹ����˶���ʱ��!!
					}
					if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
						if (returnVO.getReturnObject() != null && returnVO.getReturnObject() instanceof WLTRemoteException) {
							addIsSucessInf(str_keyId, "N", ((WLTRemoteException) returnVO.getReturnObject()));
						} else {
							addIsSucessInf(str_keyId, "Y", null);
						}
					}
				}
				//long ll_xch_2 = System.currentTimeMillis(); //
				//System.out.println("����ʱ�����̺߳�ʱ[" + (ll_xch_2 - ll_xch_1) + "]"); //�������,������ʱ,������ƿ��!!
			} catch (Throwable th) {
				th.printStackTrace();
				if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
					String str_message = th.getMessage();
					WLTRemoteException novaEx = new WLTRemoteException(str_message);
					novaEx.setServerTargetEx(th);
					addIsSucessInf(str_keyId, "N", novaEx);
				}
			}
			closeConn(initContext); //�ع��ôλỰ����������!!!�ǳ���Ҫһ��Ҫִ��!! ���뱣֤!!������ر�,����ܻ�ʹϵͳ��������״̬,Ȼ��ͻ��˻����read time out�쳣!!!
			releaseContext(initContext); //�����ǰ�Ự�е�����
		}
	}

	private void addIsSucessInf(String str_keyId, String tag, WLTRemoteException obj) {
		String[] str_value = (String[]) revokeThreadHashtable.get(str_keyId);
		if (str_value != null) {
			str_value[14] = tag;
			if (obj != null) {
				if (obj.getServerTargetEx() != null) {
					String str_serverStack = obj.getServerStackDetail(); //
					if (str_serverStack != null && !str_serverStack.equals("")) {
						str_value[15] = obj.getServerTargetEx().getClass().getName() + ": " + obj.getServerTargetEx().getMessage() + "\r\n" + str_serverStack;
					}
				}
			}
			revokeThreadHashtable.put(str_keyId, str_value);
		}
	}

	//ֱ�����һ���쳣��Ϣ,����ϵͳ��æ,�ڴ�ӽ��ٽ�ֵ!
	private void returnExceptionMsg(String _message, ServletResponse _response, long ll_1, boolean _isExit) throws Exception {
		RemoteCallReturnVO returnVO = new RemoteCallReturnVO(); //
		Exception exx = new Exception(_message); //
		WLTRemoteFatalException remoteEX = new WLTRemoteFatalException(exx.getMessage(), _isExit); //�汾�쳣!!!
		remoteEX.setServerTargetEx(exx);
		returnVO.setReturnObject(remoteEX); //
		returnVO.setDealtime(System.currentTimeMillis() - ll_1);
		returnVO.setCallDBCount(-1);
		outPutToclient(_response, returnVO); //������ͻ���..
	}

	/**
	 * �ύ��������
	 * @param _initContext
	 */
	private int commitTrans(WLTInitContext _initContext) {
		//System.out.println("�ύ�ô�Զ�̷�����������!"); //
		int li_allStmtCount = 0;
		if (_initContext.isGetConn()) {
			WLTDBConnection[] conns = _initContext.GetAllConns();
			for (int i = 0; i < conns.length; i++) {
				try {
					li_allStmtCount = li_allStmtCount + conns[i].getOpenStmtCount(); //
					conns[i].transCommit();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return li_allStmtCount;
	}

	private int rollbackTrans(WLTInitContext _initContext) {
		//System.out.println("�ع��ô�Զ�̷�����������!"); //
		int li_allStmtCount = 0;
		if (_initContext.isGetConn()) {
			WLTDBConnection[] conns = _initContext.GetAllConns();
			for (int i = 0; i < conns.length; i++) {
				try {
					li_allStmtCount = li_allStmtCount + conns[i].getOpenStmtCount(); //
					conns[i].transRollback();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
		return li_allStmtCount;
	}

	private void closeConn(WLTInitContext _initContext) {
		//System.out.println("�رոô�Զ�̷�����������!"); //
		try {
			if (_initContext.isGetConn()) {
				WLTDBConnection[] conns = _initContext.GetAllConns();
				for (int i = 0; i < conns.length; i++) {
					try {
						conns[i].close(); //�ر�ָ������Դ����
						//System.out.println("�رյ�ǰԶ�̷����õ������ݿ�����[" + conns[i].getDsName() + "]");
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	private void releaseContext(WLTInitContext _initContext) {
		//System.out.println("�ͷŸô�Զ�̷���������Դ!"); //
		try {
			_initContext.release(); //�ͷ�������Դ!!
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ��һ��������ȡ�ö���������
	 * @param _inputstream
	 * @return
	 */
	private CompressBytesVO getByteFromInputStreamByMark(InputStream _inputstream) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream(); //����������!!!
			byte[] buf = new byte[2048]; //5K,Ҫ��!!����ᱨ��,��ǰ��1024,��������ȡ��һλ,��ȡ�����!��LoadRunderѹ������ʱ���Ǳ���ѹʧ��(���ݸ�ʽ����)!��������Ŀ��Ҳ���������������! �����ĳ����ڵ�������ӱ����Ͳ�������!˵��Ч��������!!
			int len = -1;
			while ((len = _inputstream.read(buf)) != -1) {
				baos.write(buf, 0, len); //
			}
			_inputstream.close(); //�ر�������!�����ͷ�����!!
			_inputstream = null; //
			byte[] byteCodes = baos.toByteArray(); //��һλ�Ǳ��λ!�����0,���ʾ�ǲ�ѹ��,�����1���ʾ��ѹ��!!!
			byte firstMark = byteCodes[0]; //
			byte[] realByteCodes = new byte[byteCodes.length - 1]; //
			System.arraycopy(byteCodes, 1, realByteCodes, 0, realByteCodes.length); //����һ��!JavaӦ�ñ�֤������ܵ�! ���򲻻����System������!�������һ��Ƚ�С!
			return new CompressBytesVO((firstMark == 0 ? false : true), realByteCodes); //�����һλ��0,���ʾûѹ��!!!,�����1���ʾ��ѹ����!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		} finally {
			try {
				if (_inputstream != null) {
					_inputstream.close(); //
				}
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
			try {
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
	 * ѹ��!!
	 * @param _initbytes
	 * @return
	 */
	public CompressBytesVO compressBytes(byte[] _initbytes) {
		return getBSUtil().compressBytes(_initbytes); //
	}

	/**
	 * ��ѹĳ���ֽ�����,�������������һ��ѹ���ȷǳ��ߵ��ļ�ʱ�ͻ��������ε�ѹ��,��������ѭ��!!����һ��rar�ļ�,���Իᷢ����ʱһЩrar�ļ��޷��ϴ������ص����!!
	 * @param _initbyte
	 * @return
	 */
	public byte[] decompressBytes(byte[] _initbyte) {
		return getBSUtil().decompressBytes(_initbyte); //
	}

	private BSUtil getBSUtil() {
		if (bsUtil != null) {
			return bsUtil;
		}
		bsUtil = new BSUtil(); //
		return bsUtil;
	}

	private String getNewId(String _prefix) {
		if (!revokeThreadHashtable.containsKey(_prefix)) { //���û�и�key��ֱ�ӷ���!!
			return _prefix;
		}

		int li_count = 1;
		while (1 == 1) {
			String str_newid = _prefix + "_" + li_count; //
			if (!revokeThreadHashtable.containsKey(str_newid)) { //���û�и�key��ֱ�ӷ���!!
				return str_newid;
			}
			li_count++; //�����1,Ȼ�������ѭ��ֱ���ҵ�һ���µļ�ֵ!!
		}
	}

}
