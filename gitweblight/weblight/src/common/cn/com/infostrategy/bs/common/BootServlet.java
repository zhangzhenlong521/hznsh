/**************************************************************************
 * $RCSfile: BootServlet.java,v $  $Revision: 1.63 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/

package cn.com.infostrategy.bs.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.MessageDigest;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.InitParamVO;
import cn.com.infostrategy.to.common.Log4jConfigVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;


/**
 * ƽ̨�����������,�ǳ��ؼ�!!
 * ������ƽ̨������,��������Ϊfinal������Ϊ�˱�֤�������з�����������,��Ŀ��ʼ�����߼�ֻҪ��дһ��BootServlet�Ϳ�����
 * 
 * @author xch
 * 
 */
public final class BootServlet extends HttpServlet {

	private static final long serialVersionUID = 2565224486025176567L;

	private org.jdom.Document doc = null; // weblight.xml
	private Vector vec_images = new Vector();
	private static Logger logger = null;
	private TBUtil tbUtil = new TBUtil(); //

	public BootServlet() {
	}

	/**
	 * ��ʼ�����ݿ�����
	 */
	
	public void init() throws ServletException {
		ServerConsolePrintStream mySystemOut = new ServerConsolePrintStream(System.out);
		System.setOut(mySystemOut);
		ServerConsolePrintStream mySystemErr = new ServerConsolePrintStream(System.err);
		System.setErr(mySystemErr);
		long ll_1 = System.currentTimeMillis();

		ServerEnvironment.setProperty("JVMSITE", "SERVER"); // ϵͳ����,�����жϵ�ǰ������ǿͻ��˻��Ƿ�������
		try {
			loadConfigXML(); // ���������ļ�,������ؼ��ĵ�һ��!!!��һ�����ʧ��,ϵͳֱ���˳�!
		} catch (Throwable tr) {
			tr.printStackTrace(); //
			System.exit(0); //
		}

		try {
			// ��ʼ��һЩ������Ĳ���
			try {
				// debugMsg(" ");
				initSomePars(); //
			} catch (Throwable tr) {
				tr.printStackTrace(); //
			}

			// ��ӡWeblithƽ̨�汾��Ϣ Gwang 2013/4/28
			this.printWeblighVersion();

			// ��ʼ��log4j
			try {
				initLog4j(); //
			} catch (Throwable tr) {
				tr.printStackTrace(); //
			}

			try {
				if (!checkMachineIdentify()) { //��У������Ƿ��ǺϷ������У�鲻��������!!
					return;
				}
			} catch (Exception e) {
				e.printStackTrace(); //
			}

			// ��ϵͳ..
			try {
				initInnerSystem(); //
			} catch (Throwable tr) {
				tr.printStackTrace(); //
			}

			// ��ʼ�����ݿ����ӳ�..
			try {
				// debugMsg(" ");
				initDataBasePool();
			} catch (Throwable tr) {
				tr.printStackTrace();
			}

			// ��ʼ��ModuleService��..
			try {
				// debugMsg(" ");
				initServicePool();
			} catch (Throwable tr) {
				tr.printStackTrace();
			}

			try {
				tbUtil.reflectCallMethod("cn.com.infostrategy.bs.sysapp.install.quickInstall.QuickInstallDMO", "initBootServlet", new String[0]);
			} catch (Throwable tr) {
				tr.printStackTrace();
			}
			// ��ʼ����¼ҳ����ȵ�.
			try {
				initLoginHref(); //
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			//����extJar�ļ����嵥..
			try {
				initExt3JarFileNames(); //
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			//����bin3Dll�ļ����嵥..
			try {
				initBin3DllFileNames(); //
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			//����ͼƬ����!
			try {
				String str_realpath = ServerEnvironment.getProperty("SERVERREALPATH"); //
				str_realpath = str_realpath.replace('\\', '/');
				str_realpath = str_realpath + "/WEB-INF/lib/weblight_images.jar";
				File jarfile = new File(str_realpath); //
				if (jarfile.exists()) { //����ļ�����!!!
					JarInputStream jin = new JarInputStream(new FileInputStream(jarfile)); //
					JarEntry jarEntry = null; //
					ArrayList al_imgNames = new ArrayList(); //
					while ((jarEntry = jin.getNextJarEntry()) != null) { //ֻҪ����һ��,ͬʱһ���ӽ����������ֵܶ�����!! �Ӷ������������!! ���������ڴ�!! ���ͻ��˲�Ҫ��!!
						String str_itemFileName = jarEntry.getName();
						if (str_itemFileName != null && str_itemFileName.startsWith("cn/com/weblight/images/") && !str_itemFileName.startsWith("cn/com/weblight/images/workflow/") && !str_itemFileName.startsWith("cn/com/weblight/images/pushineui/") && !str_itemFileName.startsWith("cn/com/weblight/images/scrollbar/")
								&& (str_itemFileName.endsWith(".gif") || str_itemFileName.endsWith(".jpg") || str_itemFileName.endsWith(".png"))) {
							str_itemFileName = str_itemFileName.substring(str_itemFileName.lastIndexOf("/") + 1, str_itemFileName.length()); //
							al_imgNames.add(str_itemFileName); //
						}
					}
					String[] str_imgNames = (String[]) al_imgNames.toArray(new String[0]); //
					ServerEnvironment.getInstance().setImagesNames(str_imgNames); //����ͼƬ����!!
					//System.out.println("�����˶��ٸ�ͼƬ=[" + str_imgNames.length + "]"); //
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			// ��ʼ��jar����
			try {
				// -DRunModel=DEVELOPE,��Eclipse��Tomcat���õ�JDK�����м���JVM��������,����jar������!ʵ������ʱ�϶��������������,���Ի���!���ڷ�������Ҳ��һ������ģʽ�����ĸ���!!
				if (ServerEnvironment.getProperty("RunModel") != null && ServerEnvironment.getProperty("RunModel").equals("DEVELOPE")) { // ����ǿ���ģʽ,��������JAR����,�����Ч��!!
				} else {
					if (doc != null) {
						String[] str_newInitJarCacheFiles = null; //
						String[] str_initcacheJars = getInitJars(); //��ʼ����jar����!
						if (str_initcacheJars == null || str_initcacheJars.length == 0) {
							str_newInitJarCacheFiles = new String[] { "wlappletloader.jar" }; //ǿ�ж�wlappletloader.jar������!
						} else {
							str_newInitJarCacheFiles = new String[str_initcacheJars.length + 1]; //
							str_newInitJarCacheFiles[0] = "wlappletloader.jar"; //
							System.arraycopy(str_initcacheJars, 0, str_newInitJarCacheFiles, 1, str_initcacheJars.length); //
						}
						vec_images = (new InitJarPackageCache(ServerEnvironment.getProperty("SERVERREALPATH"), str_newInitJarCacheFiles)).getImagesVec(); //������!!!
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			// ��ʼ�������ʱ��
			try {
				initLastStartTime();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			try {
				initSysThread(); //����ϵͳ�߳�,����һЩϵͳ������Ҫ��ͣ�ܵ��߳�!!
			} catch (Throwable th) {
				th.printStackTrace(); //
			}

			try {
				initJob();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			try {
				initSecondProjectBoot();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			WLTInitContext initContext = new WLTInitContext(); //
			commitTrans(initContext); // �ύ����!�����ʼ�����ǳԵ��쳣��,��������ʼ���������,������һ������ع�,����������Զ���ύ!!
			closeConn(initContext); // �ر�����!
			releaseContext(initContext); // �ͷ���Դ
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			long ll_2 = System.currentTimeMillis();
			infoMsg("�����Ӧ��[" + this.getServletContext().getRealPath("/") + "]�ĳ�ʼ��[" + this.getClass().getName() + "],�ܹ���ʱ[" + (ll_2 - ll_1) + "]"); //
		}

	}

	/**
	 * ����XML�����ļ�!!
	 * 
	 */
	private void loadConfigXML() {
		long ll_1 = System.currentTimeMillis();
		java.io.InputStream ins = null; //
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			ins = this.getClass().getResourceAsStream("/weblight.xml"); //
			doc = builder.build(ins); // ����XML
			long ll_2 = System.currentTimeMillis();
			System.out.println("���������ļ�:[weblight.xml]�ɹ�,��ʱ[" + (ll_2 - ll_1) + "]����."); //
		} catch (Throwable ex) {
			System.out.println("���������ļ�:[weblight.xml]ʧ��,ϵͳֱ���˳�,��ȷ��ClassPath���Ƿ���ڸ��ļ�!!"); //
			ex.printStackTrace();
			System.exit(0); // ϵͳֱ���˳�!
		} finally {
			if (ins != null) {
				try {
					ins.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ��������
	 */
	private void initSomePars() {
		if (doc == null) {
			return;
		}
		long ll_1 = System.currentTimeMillis(); //
		try {
			java.util.List initparams = doc.getRootElement().getChildren("init-param");
			if (initparams != null) {
				ArrayList al_pars = new ArrayList(); //
				for (int i = 0; i < initparams.size(); i++) {
					if (initparams.get(i) instanceof org.jdom.Element) {
						org.jdom.Element param = (org.jdom.Element) initparams.get(i);
						if (param != null) {
							String str_key = param.getAttributeValue("key");
							String str_value = param.getAttributeValue("value");
							String str_descr = param.getAttributeValue("descr");
							//�����"${}"������,��תһ��,��ϵͳ������ȡ! ֮������ô���Ƿ���Tomcat�Ķ�̬����,��Ϊ�ڼ�Ⱥʱ,��Щ������һ����������־�ļ�Ŀ¼,�ϴ��ļ�Ŀ¼��,
							//Ϊ�˲���������webapp,ֻ�����������ļ�����,��cmd��ֱ��ʹ��-D����,���Ը�������һ�����!!����������Ϊ������Ⱥ������!!
							//������ũ������Ŀ��,���׷��ָ߲���ʱ,����ext3�ļ��ǳ���,�о���һ��ͬʱʹ��ϵͳʱ,��ȷ����Ϊ�߲���������������(��������ѵʱΪʲôû�з������������?�ѵ�����Ϊ��������ԭ��?)
							//����˵��ϵͳʹ�ó���ǿ�ҽ������ʹ�ü�Ⱥ!!Ȼ��ȸ߷��ڹ����ٸĻص�������(����һ���Ժ�)!!
							if (str_value.indexOf("${") >= 0) {
								str_value = replaceMacroPar(str_value); //
							}
							ServerEnvironment.setProperty(str_key, str_value); // ��ϵͳ�����м���!!
							ServerEnvironment.getInstance().put(str_key, str_descr, str_value); // ������������...
							InitParamVO parVO = new InitParamVO();
							parVO.setKey(str_key);
							parVO.setValue(str_value);
							parVO.setDescr(str_descr); //
							al_pars.add(parVO);
							//System.out.println("[" + str_key + "] = [" + str_value + "]");
						}
					}
				}

				InitParamVO[] allParVOs = (InitParamVO[]) al_pars.toArray(new InitParamVO[0]); //
				ServerEnvironment.getInstance().setInitParamVOs(allParVOs); //
			}
			long ll_2 = System.currentTimeMillis();
			// debugMsg("��ʼ��������������,��ʱ[" + (ll_2 - ll_1) + "]");
		} catch (Throwable tr) {
			tr.printStackTrace();
		}

		if ("Y".equals(ServerEnvironment.getProperty("ISLOADRUNDERCALL"))) { //�Ƿ���ѹ������״̬,����ѹ������״̬,ȥ�������д�����־�Ĺ���,�Ӷ���֤ѹ������Ч������!!Ĭ����False!! ����Ժ����Զ��жϳ����״̬�͸��õ�!!
			ServerEnvironment.isLoadRunderCall = true; //
		}

		if ("N".equals(ServerEnvironment.getProperty("isOutPutToQueue"))) { //�Ƿ񽫿���̨������������?�ʴ���Ŀ��������Ϊ��������JVM���!
			ServerEnvironment.isOutPutToQueue = false; //
		}

		if ("Y".equals(ServerEnvironment.getProperty("ISPAGEPKINCACHE"))) { //��ҳʱ�Ƿ�������,�����ֻ���,һ����count()����,һ������ҳ��������!!Ĭ����False
			ServerEnvironment.isPaginationInCache = true; //
		}

		String str_pageCount = ServerEnvironment.getProperty("ISPAGECOUNTINCACHE"); //
		if (str_pageCount != null && str_pageCount.toUpperCase().startsWith("Y")) { //��ҳʱ������Ƿ�������!
			ServerEnvironment.isPageCountInCache = true; //
			if (str_pageCount.length() > 1) {
				int li_cycle = Integer.parseInt(str_pageCount.substring(1, str_pageCount.length())); //
				ServerEnvironment.pacgeCountCacheCycle = li_cycle; //
			}
		}

		String str_pageFalsity = ServerEnvironment.getProperty("ISPAGEFALSITY"); //
		if (str_pageFalsity != null && str_pageFalsity.toUpperCase().startsWith("Y")) { //��ҳʱ������Ƿ�������!
			ServerEnvironment.isPageFalsity = true; //
			if (str_pageFalsity.length() > 1) {
				int li_sleep = Integer.parseInt(str_pageFalsity.substring(1, str_pageFalsity.length())); //
				ServerEnvironment.falsitySleep = li_sleep; //ѭ������,Խ��Խ��ҲԽ��!!
			}
		}

		if (ServerEnvironment.getProperty("SERVERTYPE") != null && ServerEnvironment.getProperty("SERVERTYPE").equals("TOMCAT")) {
			ServerEnvironment.setProperty("SERVERREALPATH", this.getServletContext().getRealPath("/").replace('\\', '/')); // /
		}
		ServerEnvironment.setProperty("WebAppRealPath", ServerEnvironment.getProperty("SERVERREALPATH")); //  
		ServerEnvironment.getInstance().put("WebAppRealPath", "��Ӧ�õľ��Ե�ַ", ServerEnvironment.getProperty("SERVERREALPATH")); //
		long ll_2 = System.currentTimeMillis(); //
		System.out.println("��ʼ��ϵͳ<init-param>�����ɹ�,��ʱ[" + (ll_2 - ll_1) + "]"); //
	}

	/**
	 * У�������Ψһ��
	 * @throws Exception
	 */
	private boolean checkMachineIdentify() {
		String initpolicy = ServerEnvironment.getProperty("SERVERINITPOLICY"); //����������Ч�����,A������ԭ�л��� B������У��"���������"+"�û���"+"��Ŀ��"
		if (initpolicy == null || initpolicy.equals("")) {
			initpolicy = "A";
		}
		String str_validatepwd = System.getenv("COMPUTERNAME"); //������!
		str_validatepwd = (new StringBuilder(String.valueOf(str_validatepwd))).append("_").append(System.getenv("USERNAME")).toString(); //�û���,������WebSphere��ȡ��Ϊnull,�����е�ϵͳ�����һ���û���Ͳ�������,���Կ����Ƿ�
		if ("B".equals(initpolicy)) { //������һ����������Ӳ����������ͨ�������������Զ�����Ӳ��cpuȥ�ܡ����԰�cpuЧ����Ϣȥ����ͨ����Ŀ����Ч�顣[����2012-07-26]
			String p_name = ServerEnvironment.getProperty("PROJECT_NAME"); //��ȡ��Ŀ���ơ�
			str_validatepwd = (new StringBuilder(String.valueOf(str_validatepwd))).append("_").append(p_name).toString(); //
		} else if ("C".equals(initpolicy)) {
			str_validatepwd = ServerEnvironment.getProperty("PROJECT_NAME"); //��ȡ��Ŀ���ơ�
		} else { //����Ĭ�ϻ���
			str_validatepwd = (new StringBuilder(String.valueOf(str_validatepwd))).append("_").append(System.getenv("PROCESSOR_IDENTIFIER")).toString(); //CPU��Ψһ��ʶ��
			str_validatepwd = (new StringBuilder(String.valueOf(str_validatepwd))).append("_").append(System.getenv("PROCESSOR_REVISION")).toString(); //
			str_validatepwd = (new StringBuilder(String.valueOf(str_validatepwd))).append("_").append(System.getenv("NUMBER_OF_PROCESSORS")).toString(); //CPU����,���絥�˻�˫��			
		}
		//System.out.println("�����ʶ����=[" + str_validatepwd + "]"); ////
		String str_md5 = md5(str_validatepwd.toUpperCase());
		String str_sha = sha(str_md5);

		String str_sn = ServerEnvironment.getProperty("SN"); //���õ�SN��
		String str_sn1 = ServerEnvironment.getProperty("SN1"); //���õ�SN��
		if (str_sn == null) {
			errorMsg((new StringBuilder("ϵͳ�����˷�����У��,��û��������֤�������֤�벻��,�뽫ʶ����[")).append(str_md5).append("]����ϵͳ��Ӧ��������У����,Ȼ��У����������SN������!").toString());
			return false;
		}
		//if (str_sn.equals("pushworld2012")) { //��������Ŀ�пͻ�ǿ��Ҫ����Ϊ��Ⱥ����Ҫȥ��,��ʱ�и�������!!!
		//	return true; //
		//}
		if (str_sn.startsWith("${")) { //ÿ�δ�10�������Ͽ���WebRootĿ¼��ѹ��,��Ҫ�ֹ��ٴ��޸�weblight.xml�е�SN����ֵ,̫����,���Խ�SN���ó�${PUSH_SN},Ȼ�������������ж���,����ÿ�ο���WebRootĿ¼ʱ,�Ͳ�Ҫ���޸�SN��!��������Ӹ��ж�!!��xch/2012-03-08��
			str_sn = replaceMacroPar(str_sn); //
		}

		if (str_sn.equals(str_sha) || (str_sn1 != null && str_sn1.equals(str_sha))) { //���ֱ�����,����Ϊ��ok��,����ԭ����!
			return true; //
		} else {
			if (str_sn.length() != str_sha.length() + 18) {
				errorMsg("ϵͳ����������У�鹦��,�����õ���֤���ǷǷ���,�뽫ʶ����[" + str_md5 + "]����ϵͳ��Ӧ���������µ���֤��,Ȼ����֤��������SN������!"); //
				return false; //
			}
			int li_result = unEncrySnCode(str_sn, str_sha); //���ܲ�У��!!!
			if (li_result == 0) { //����ɹ�!!
				//������Ч������ϵͳ������,����ÿ��Զ�̵���ʱ������бȽ�!!!
				return true;
			} else if (li_result == -1) {
				errorMsg("ϵͳ����������У�鹦��,�����õ���֤�벻��,�뽫ʶ����[" + str_md5 + "]����ϵͳ��Ӧ����������֤��,Ȼ����֤��������SN������!"); //
				return false;
			} else if (li_result == -2) {
				errorMsg("ϵͳ����������У�鹦��,�����õ���֤���ѹ�������,�뽫ʶ����[" + str_md5 + "]����ϵͳ��Ӧ���������µ���֤��,Ȼ����֤��������SN������!"); //
				return false;
			} else {
				errorMsg("ϵͳ����������У�鹦��,�����õ���֤���ǷǷ���ʽ��,�뽫ʶ����[" + str_md5 + "]����ϵͳ��Ӧ���������µ���֤��,Ȼ����֤��������SN������!"); //
				return false;
			}
		}

	}

	//����SN��!!
	private int unEncrySnCode(String _snCode, String _sha) {
		try {
			String str_1 = _snCode.substring(0, 1); //��һ����ĸ
			String str_2 = _snCode.substring(1, 2); //�ڶ�����ĸ
			String str_snCode = _snCode.substring(2, _snCode.length()); //�����
			int li_space = getSapceByTwoWord(str_1, str_2); //���λ��
			if (li_space < 0) {
				return -999;
			}
			String str_prefix = str_snCode.substring(0, li_space); //
			String str_encrydate = str_snCode.substring(li_space, li_space + 16); //���ܵ�����
			String str_subfix = str_snCode.substring(li_space + 16, str_snCode.length()); //���
			String str_sncode = str_prefix + str_subfix; //ʵ�ʵ�SN��
			if (!str_sncode.equals(_sha)) { //�������,����Ϊ�ǷǷ���
				return -1; //
			}

			String str_unencrydate = unEncryDate(str_encrydate); //���ܺ������
			if (str_unencrydate == null) { //�����������ʧ��!!
				return -999;
			}

			SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE);
			String str_currdate = sdf_curr.format(new Date()); //��ǰ����
			if (str_currdate.compareTo(str_unencrydate) >= 0) { //����ǹ���
				return -2; //
			} else {
				ServerEnvironment.setProperty("EFFECTLIMITDATE", str_unencrydate); //��ϵͳ������ע����Ч����!�Ա�ÿ��Զ�̵���ʱ������!
				return 0; //������Ϊ�ǳɹ���
			}
		} catch (Exception e) {
			System.err.println("��SN����н���ʱ,�����쳣[" + e.getMessage() + "]"); //
			return -999; //
		}
	}

	/**
	 * ����SN��,��������!!
	 * ԭ�����������������ĸ,����֮���λ�ñ�ʾ������SN�ĵڶ���λ������������,Ȼ��SN�س�����!�м�����������!
	 * �������ֹ����֪����ȡβ���ķ������ĳ���ʽ��!!!
	 * @param _sn
	 * @param _limitDate
	 * @return
	 */
	private String encryIdentifyCodeAndDate(String _identifyCode, String _limitDate) {
		String str_initSNCode = sha(_identifyCode); //�ȶ�ʶ�������sha����!!!
		String[] str_36words = getRanDom36Words(); //������ַ���
		Random ranDom = new Random(); //
		String str_1 = str_36words[ranDom.nextInt(36)]; ////��һ����ĸ
		int li_length = str_initSNCode.length(); //SN��ĳ���,����30λ
		if (li_length > 35) {
			li_length = 35; //
		}
		int li_space = ranDom.nextInt(li_length); //0-30
		String str_2 = getNextPosStr(str_1, li_space); //�ڶ�λ!
		String str_prefix = str_initSNCode.substring(0, li_space); //ǰ�
		String str_subfix = str_initSNCode.substring(li_space, str_initSNCode.length()); //���
		String str_encryDateCode = entryDate(_limitDate); //
		String str_return = str_1 + str_2 + str_prefix + str_encryDateCode + str_subfix; //
		return str_return; //
	}

	/**
	 * ��������,ԭ����������һ��8λ���������,Ȼ�����ڲ�ֳ�8������,�����������Ϊλ��,���ɼ�����!!!
	 * @param _date
	 * @return
	 */
	private String entryDate(String _date) {
		char[] chars = _date.toCharArray();//�����ڲ������
		String[] str_randomStrs = getRanDom8Strs(); //ȡ��8��������ַ���
		String[] str_encryStrs = new String[8]; //���ܺ������
		for (int i = 0; i < 8; i++) {
			int li_incre = Integer.parseInt("" + chars[i]); //������ת��������,�����־���һ������!!!
			str_encryStrs[i] = getNextPosStr(str_randomStrs[i], li_incre); //ȡ������ַ�������ĳ�ξ���λ�õ����ַ���
			//System.out.println(str_randomStrs[i] + "��ǰ��[" + li_incre + "]λ,�õ�" + str_encryStrs[i]); //

		}
		StringBuilder sb_return = new StringBuilder(); //
		for (int i = 0; i < 8; i++) {
			sb_return.append(str_randomStrs[i]); //ԭ����!!!
		}
		for (int i = 0; i < 8; i++) {
			sb_return.append(str_encryStrs[7 - i]); //��������!!
		}
		//System.out.println(sb_return.toString()); //
		return sb_return.toString(); //
	}

	//��������
	private String unEncryDate(String _date) {
		StringBuilder sb_date = new StringBuilder(); //
		for (int i = 0; i < 8; i++) { //
			String str_1 = _date.substring(i, i + 1); //��ǰ�� ��
			String str_2 = _date.substring(16 - i - 1, 16 - i); //�Ӻ���ǰ,7,8
			int li_space = getSapceByTwoWord(str_1, str_2); //
			if (li_space < 0 || li_space >= 10) { //��Ӧ�ô���10
				return null;
			}
			//System.out.println("��ĸ[" + str_1 + "," + str_2 + "]��ľ�����["+li_space+"]"); //
			sb_date.append("" + li_space); //
		}
		return sb_date.toString(); //
	}

	//ȡ�������8���ַ���
	private String[] getRanDom8Strs() {
		String[] str_36words = getRanDom36Words(); //
		String[] str_words = new String[8]; //
		Random ranDom = new Random(); //
		for (int i = 0; i < 8; i++) {
			str_words[i] = str_36words[ranDom.nextInt(36)]; //
		}
		return str_words; //
	}

	private int getSapceByTwoWord(String _word1, String _word2) {
		int li_pos_1 = getPos(_word1); //
		int li_pos_2 = getPos(_word2); //
		if (li_pos_1 < 0 || li_pos_2 < 0) {
			return -1;
		}
		//System.out.println("��ĸ1[" + _word1 + "]��λ��[" + li_pos_1 + "],��ĸ2[" + _word2 + "]��λ��[" + li_pos_2 + "]"); //
		if (li_pos_2 >= li_pos_1) { //������ߴ���ǰ��,��ֱ���ú��߼�ȥǰ��λ��
			return li_pos_2 - li_pos_1; //
		} else {
			return li_pos_2 + (36 - li_pos_1); //ǰ�߼���36�ټ�ȥ����
		}
	}

	//ȡ��ĳ���ַ��������λ���ַ���!!
	private String getNextPosStr(String _str1, int _space) {
		int li_pos = getPos(_str1); //�ȼ�������ַ�����λ��!
		int li_newPos = li_pos + _space; //��λ��
		if (li_newPos > 35) { //���Խ����,�򵹹�����ͷ��
			return getRanDom36Words()[li_newPos - 36]; //ǰ���λ��!!
		} else {
			return getRanDom36Words()[li_newPos]; //
		}
	}

	//ȡ��һ���ַ����������е�λ��!!
	private int getPos(String _str) {
		String[] str_36words = getRanDom36Words(); //
		for (int i = 0; i < str_36words.length; i++) {
			if (str_36words[i].equals(_str)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * ȡ�����˳���36���ַ���!
	 * @return
	 */
	public String[] getRanDom36Words() {
		return new String[] { "i", "v", "b", "z", "w", "8", "o", "q", "l", "9", "x", "j", "0", "2", "f", "g", "e", "h", "a", "p", "k", "c", "4", "u", "6", "t", "5", "1", "m", "d", "n", "7", "s", "y", "3", "r" }; //���˳���36����ĸ������Կ!!�����Ǽ��ܻ��ǽ��ܱ�����һ��!!
	}

	private String getStr(ArrayList _alTmp, String[] _aa) {
		int li_pos = new Random().nextInt(36); //0-35
		if (!_alTmp.contains("" + li_pos)) { //���������
			_alTmp.add("" + li_pos); //
			return _aa[li_pos]; //
		} else {
			return getStr(_alTmp, _aa); //������
		}
	}

	public static void main(String[] args) {
		System.out.println(new BootServlet().sha("29b4b82a53y5a319a46b7z8799xaaa95"));
	}

	public final String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'x', 'y', 'z' }; // �����Լ���Կ��
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5"); //
			mdTemp.update(s.getBytes());
			byte[] md = mdTemp.digest();

			int j = md.length;
			char str[] = new char[j * 2]; //
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i]; //
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; //
				str[k++] = hexDigits[byte0 & 0xf]; //
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	public final String sha(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'x', 'y', 'z' }; // �����Լ���Կ��
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("SHA");
			mdTemp.update(s.getBytes());
			byte[] md = mdTemp.digest();

			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	public String replaceAll(String str_par, String old_item, String new_item) {
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

	private String getMacAddr() {
		try {
			Process process = Runtime.getRuntime().exec("ipconfig /all");
			InputStreamReader ir = new InputStreamReader(process.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String line;
			while ((line = input.readLine()) != null)
				if (line.indexOf("Physical Address") > 0) {
					String MACAddr = line.substring(line.indexOf("-") - 2);
					// System.out.println("MAC address = [" + MACAddr + "]");
					return MACAddr; //
				}
		} catch (java.io.IOException e) {
			System.err.println("IOException " + e.getMessage());
		}
		return null;
	}

	/**
	 * ��Nova2Config.xml���Ҫ��ʼ��������jar�ļ���
	 * 
	 * @return
	 */
	private String[] getInitJars() {
		if (doc == null) {
			return null;
		}

		org.jdom.Element datasources = doc.getRootElement().getChild("jarcachefiles"); // �õ�datasources�ӽ��!!
		String[] str_jars = null;
		if (datasources != null) {
			if (datasources != null) {
				java.util.List sources = datasources.getChildren(); // �õ������ӽ��!!
				str_jars = new String[sources.size()];
				for (int i = 0; i < sources.size(); i++) { // ���������ӽ��!!
					if (sources.get(i) instanceof org.jdom.Element) { //
						org.jdom.Element node = (org.jdom.Element) sources.get(i); //
						str_jars[i] = node.getText(); // �õ�����
					}
				}
			}
		}

		return str_jars;
	}

	private void addToImageVec(String _image) {
		for (int i = 0; i < vec_images.size(); i++) {
			String str_temp = (String) vec_images.get(i);
			if (str_temp != null && str_temp.equals(_image)) {
				return;
			}
		}
		vec_images.add(_image);
	}

	/**
	 * ��ʼ�����ݿ�����
	 * 
	 */
	public void initDataBasePool() {
		if (doc == null) {
			return;
		}

		long ll_1 = System.currentTimeMillis(); //
		// debugMsg("��ʼ��ʼ�����ݿ����ӳ�...");
		org.jdom.Element datasources = doc.getRootElement().getChild("datasources"); // �õ�datasources�ӽ��!!
		if (datasources != null) {
			java.util.List sources = datasources.getChildren(); // �õ������ӽ��!!
			Vector v_dsVOs = new Vector(); //
			boolean isDefaultDSActive = false; //Ĭ������Դ�Ƿ��Ǽ���״̬??
			for (int i = 0; i < sources.size(); i++) { // ���������ӽ��!!
				if (sources.get(i) instanceof org.jdom.Element) { //
					org.jdom.Element node = (org.jdom.Element) sources.get(i); //

					String initial_context_factory = null;
					if (node.getChild("initial_context_factory") != null) {
						initial_context_factory = node.getChild("initial_context_factory").getText();
					}
					String provider_url = null; // 
					if (node.getChild("provider_url") != null) {
						provider_url = node.getChild("provider_url").getText(); // 
					}

					String str_datasourcename = node.getAttributeValue("name"); // �õ�����
					String str_datasourcedescr = node.getAttributeValue("descr"); // �õ�����

					String str_dbtype = null;
					if (node.getChild("dbtype") != null) {
						str_dbtype = node.getChild("dbtype").getText(); //
					}
					String str_dbversion = null;
					if (node.getChild("dbversion") != null) {
						str_dbversion = node.getChild("dbversion").getText(); //���ݿ�汾
					}
					String str_dbdriver = null;
					if (node.getChild("driver") != null) {
						str_dbdriver = node.getChild("driver").getText();
					}
					String str_dburl = null;
					if (node.getChild("dburl") != null) {
						str_dburl = node.getChild("dburl").getText();
					}
					//Ϊ���ò��������!!���Դ����������д��붯̬����!!
					if (str_dburl.indexOf("${") >= 0) {
						str_dburl = replaceMacroPar(str_dburl); //
					}

					String str_user = null;
					if (node.getChild("user") != null) {
						str_user = node.getChild("user").getText(); // �û���
					}
					if (str_user.indexOf("${") >= 0) {
						str_user = replaceMacroPar(str_user); //
					}

					String str_pwd = null;
					if (node.getChild("pwd") != null) {
						str_pwd = node.getChild("pwd").getText(); // ����
					}
					String str_initsize = null;
					if (node.getChild("initsize") != null) {
						str_initsize = node.getChild("initsize").getText(); //
					}
					String str_maxsize = null;
					if (node.getChild("poolsize") != null) {
						str_maxsize = node.getChild("poolsize").getText(); //
					}
					int li_initsize = 0;
					if (str_initsize != null) {
						li_initsize = Integer.parseInt(str_initsize); //
					}
					int li_maxsize = 0;
					if (str_maxsize != null) {
						li_maxsize = Integer.parseInt(str_maxsize); //
					}

					if (node.getChild("isencryuser") != null && "Y".equalsIgnoreCase(node.getChild("isencryuser").getText())) { //���ָ���� isencryuser=Y,���ʾ���ܵ�,���������н���! 
						//�����Ϻ�ũ����ǿ�������Ҫ��!! Ҳ��һ���ĵ���!! ���û���������,���ʾ������,����Ȼ����ԭ����ģʽ!!!
						str_user = new DESKeyTool().decrypt(str_user); //��������Ǽ��ܵ�,����н���!!!
					}

					if (node.getChild("isencrypwd") != null && "Y".equalsIgnoreCase(node.getChild("isencrypwd").getText())) { //���ָ���� isencrypwd=Y,���ʾ�����Ǽ��ܵ�,���������н���! 
						//�����Ϻ�ũ����ǿ�������Ҫ��!! Ҳ��һ���ĵ���!! ���û���������,���ʾ������,����Ȼ����ԭ����ģʽ!!!
						str_pwd = new DESKeyTool().decrypt(str_pwd); //��������Ǽ��ܵ�,��������������н���!!!
					}

					str_dbdriver = str_dbdriver.trim();
					str_dburl = str_dburl.trim(); //
					if (provider_url == null && initial_context_factory == null) {//���û����2��������ִ�����ӣ����˾Ͳ������ˣ�ֱ�ӷŵ�����Դ��
						Properties myProperties = new Properties();
						myProperties.setProperty("user", str_user);
						myProperties.setProperty("password", str_pwd);
						try {
							Class.forName(str_dbdriver); // ��������Դ
							GenericObjectPool connectionPool = new GenericObjectPool(null); //����һ�����ӳ�!!
							connectionPool.setMaxActive(li_maxsize); // ���������
							connectionPool.setMaxIdle(li_maxsize); //

							ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(str_dburl, myProperties);
							PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true); //
							Class.forName("org.apache.commons.dbcp.PoolingDriver"); // ����dbcp������
							PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
							driver.registerPool(str_datasourcename, connectionPool); //ע�����ӳ�
							try {
								if (li_initsize > 0) {
									WLTLogger.getLogger(this).info("���ڳ����������ݿ�[" + str_datasourcename + "][" + str_dburl + "][" + str_user + "/***],���������Դ��ͨ,��������ܻ������ܳ�ʱ��....");
								}
								for (int j = 0; j < li_initsize; j++) {
									connectionPool.addObject(); //Ҫ��ʹ�������!!!������ݿ�������,����ᱨ��!
								}

								if (li_initsize > 0 && i == 0) {
									ServerEnvironment.getInstance().setDefaultdatasource(str_datasourcename); // ����Ĭ������Դ!!
									WLTLogger.getLogger(this).info("�������ݿ����ӳ�[" + str_datasourcename + "][" + str_dburl + "][" + str_user + "/***][" + li_initsize + "](Ĭ������Դ)");
									isDefaultDSActive = true; //
								} else {
									WLTLogger.getLogger(this).info("�������ݿ����ӳ�[" + str_datasourcename + "][" + str_dburl + "][" + str_user + "/***][" + li_initsize + "]");
								}
							} catch (Exception ex) {
								WLTLogger.getLogger(this).error("�������ݿ����ӳ�[" + str_datasourcename + "][" + str_dburl + "][" + str_user + "/***]ʧ��!!!ԭ��:");
								ex.printStackTrace(); //
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} else {
						try {
							Hashtable ht = new Hashtable();
							ht.put(Context.INITIAL_CONTEXT_FACTORY, initial_context_factory);
							ht.put(Context.PROVIDER_URL, provider_url);
							InitialContext _context = new InitialContext(ht);
							javax.sql.DataSource ds = (javax.sql.DataSource) _context.lookup(str_datasourcename);
							WLTLogger.getLogger(this).info("����JNDI����Դ[" + str_datasourcename + "]�ɹ���");
							if (i == 0) {
								ServerEnvironment.getInstance().setDefaultdatasource(str_datasourcename); // ����Ĭ������Դ!!
								isDefaultDSActive = true; //
							}
						} catch (Exception ex) {
							WLTLogger.getLogger(this).error("����JNDI����Դ[" + str_datasourcename + "]ʧ��!!!ԭ��:");
							ex.printStackTrace(); //
						}
					}

					DataSourceVO dsVO = new DataSourceVO();
					dsVO.setName(str_datasourcename); //
					dsVO.setDescr(str_datasourcedescr); //
					dsVO.setDbtype(str_dbtype); //
					dsVO.setDbversion(str_dbversion); //���ݿ�汾!
					dsVO.setDriver(str_dbdriver); //
					dsVO.setDburl(str_dburl);
					dsVO.setUser(str_user);
					dsVO.setPwd(str_pwd); //
					dsVO.setInitsize(li_initsize); // ��ʼ����С
					dsVO.setProvider_url(provider_url);
					dsVO.setInitial_context_factory(initial_context_factory);
					v_dsVOs.add(dsVO); // ע��һ����...
					ServerEnvironment.getInstance().put(str_datasourcename, str_datasourcedescr, str_dburl);
					if (i == 0) {
						ServerEnvironment.getInstance().put("DEFAULTDATASOURCENAME", "Ĭ������Դ����", str_datasourcename); //
					}
				}
			}
			DataSourceVO[] allVOs = (DataSourceVO[]) v_dsVOs.toArray(new DataSourceVO[0]); //
			ServerEnvironment.getInstance().setDataSourceVOs(allVOs); // ������������Դ!!
			if (isDefaultDSActive) { //���Ĭ������Դ�ǻ��!
				try {
					//��̨ũ������Ҫ��Ӧ�÷�������ʱˢ��ƽ̨������
					//̫ƽ������Ҫ����ȫ������CommDMO���õ�����ƽ̨�������б�����ѯ������������Ӧ�÷�������ʱ�����ò������棬Ȼ���ٲ����ݿ⡾���/2017-09-26��
					cn.com.infostrategy.bs.common.SystemOptions.getInstance().reLoadDataFromDB(false);
					long ll_c1 = System.currentTimeMillis(); //
					String[] str_allTables = new CommDMO().getAllSysTables(null, null); //
					for (int i = 0; i < str_allTables.length; i++) {
						str_allTables[i] = str_allTables[i].toUpperCase(); //
					}
					long ll_c2 = System.currentTimeMillis(); //
					WLTLogger.getLogger(this).info("����Ĭ������Դһ����[" + str_allTables.length + "]����,��ʱ[" + (ll_c2 - ll_c1) + "]����!"); //
					ServerEnvironment.vc_alltables = new Vector(Arrays.asList(str_allTables)); //
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
			}
		}

		long ll_2 = System.currentTimeMillis();
		// debugMsg("��ʼ�����ݿ����ӳؽ���,��ʱ[" + (ll_2 - ll_1) + "]");
	}

	/**
	 * ��ʼ�������
	 * 
	 */
	public void initServicePool() {
		if (doc == null) {
			return;
		}

		long ll_1 = System.currentTimeMillis();
		// debugMsg("��ʼ��ʼ��SOA�����...");
		org.jdom.Element serviceroot = doc.getRootElement().getChild("moduleservices"); // �õ�moduleservices�ӽ��!!
		if (serviceroot != null) {
			java.util.List sources = serviceroot.getChildren(); // �õ������ӽ��!!
			for (int i = 0; i < sources.size(); i++) { // ���������ӽ��!!
				if (sources.get(i) instanceof org.jdom.Element) { //
					org.jdom.Element node = (org.jdom.Element) sources.get(i); //
					String str_servicename = node.getAttributeValue("name"); // �õ�����
					String str_implclass = node.getChild("implclass").getText(); //
					String str_initsize = node.getChild("initsize").getText(); //
					String str_maxsize = node.getChild("poolsize").getText(); //

					str_implclass = str_implclass.trim(); //

					int li_initsize = Integer.parseInt(str_initsize); //
					int li_maxsize = Integer.parseInt(str_maxsize); //

					ServicePoolableObjectFactory factory = new ServicePoolableObjectFactory(str_servicename, str_implclass); //
					GenericObjectPool pool = new GenericObjectPool(factory); // ���������,���100��ʵ��!!!!
					pool.setMaxActive(li_maxsize); // �����
					pool.setMaxIdle(li_maxsize); // �������
					try {
						for (int j = 0; j < li_initsize; j++) { // �ȴ���3��ʵ��!!
							pool.addObject(); // �ȴ���һ��ʵ��
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					ServicePoolFactory.getInstance().registPool(str_servicename, str_implclass, pool); // ע��һ����!!
					infoMsg("��ʼ��SOA�����[" + str_servicename + "][" + str_implclass + "]�ɹ�,������[" + str_initsize + "]��ʵ��");
				}
			}
		}

		long ll_2 = System.currentTimeMillis();
		// debugMsg("��ʼ��SOA����ؽ���,��ʱ[" + (ll_2 - ll_1) + "]");
	}

	private void initSysThread() {
		new HeartThread().start(); //
	}

	/**
	 * ִ������!!
	 */
	public void initJob() {
		try {
			//Ԭ���� 20170411 ���� �����µĶ�ʱ�������
			if (ServerEnvironment.getProperty("ISSTARTJOB") != null && ServerEnvironment.getProperty("ISSTARTJOB").equals("Y") && ServerEnvironment.vc_alltables != null && ServerEnvironment.vc_alltables.contains("PUB_JOB")) { //���weblight.xmlû��ָ����ͣJOB��ִ��JOB
				HashVO[] hvs_jobs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_job where activeflag='Y'"); //
				for (int i = 0; i < hvs_jobs.length; i++) {
					new WLTJobTimer(hvs_jobs[i]).start(); // �����߳�
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ����ext3 jar�ļ���
	 */
	private void initExt3JarFileNames() {
		try {
			org.jdom.Element ext3jars = doc.getRootElement().getChild("ext3files"); //�õ�ext3jars�ӽ��!!
			if (ext3jars != null) {
				long ll_1 = System.currentTimeMillis();
				HashSet hst_ext3Cls = new HashSet(); //
				java.util.List jarFiles = ext3jars.getChildren(); // �õ������ӽ��!!
				if (jarFiles != null && jarFiles.size() > 0) {
					String[] str_allNames = new String[jarFiles.size()]; //
					for (int i = 0; i < jarFiles.size(); i++) {
						org.jdom.Element jarFileName = (org.jdom.Element) jarFiles.get(i); //
						str_allNames[i] = jarFileName.getText(); //�����ļ���...
						File ext3ItemFile = new File(ServerEnvironment.getProperty("SERVERREALPATH") + str_allNames[i]); //SERVERREALPATH
						if (ext3ItemFile.exists()) { //�������ļ�����!
							HashSet hst_item = getOneFileAllPackages(ext3ItemFile); //
							if (hst_item != null) {
								hst_ext3Cls.addAll(hst_item); //
							}
						}
					}
					ServerEnvironment.setExt3Jars(str_allNames); //
					ServerEnvironment.setExt3JarClsNames((String[]) hst_ext3Cls.toArray(new String[0])); //���������ͻ�����Ҫ!!!����������Ŀ����������ʦ��Win7��EAC�е�¼����,�������������,�����ڻ���Ext3Ŀ¼�µ������ļ�����ʱ2.5��,һ����Ϊ�߷�������Ext3jar,���ǿ���Win7��Java�������ļ����Ǳ�WinXP��һЩ!
				}
				long ll_2 = System.currentTimeMillis();
				infoMsg("��ʼ��ext3�ļ��嵥����,������[" + jarFiles.size() + "]��jar�ļ�,���й���[" + hst_ext3Cls.size() + "]��Class�ļ�,����ʱ[" + (ll_2 - ll_1) + "]����"); //
			} else {
				System.err.println("û��ָ��<ext3files>���,����ܵ��¿ͻ�����ȱ�ٵ������İ�����������.."); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//ȡ��һ���ļ��е���������!!!
	private HashSet getOneFileAllPackages(File _file) {
		JarInputStream myJarInputStream = null; //
		try {
			HashSet hst_packs = new HashSet(); //
			myJarInputStream = new JarInputStream(new FileInputStream(_file)); //
			while (1 == 1) {
				JarEntry myJarEntry = myJarInputStream.getNextJarEntry();
				if (myJarEntry == null) { // ���jarEntryΪ�����ж�ѭ��
					break;
				}
				if (!myJarEntry.isDirectory()) {
					String str_entry = myJarEntry.getName(); //��ʽ��[org/jdesktop/jdic/desktop/Desktop.class]
					str_entry = str_entry.replace('/', '.'); //��Ҫ�滻һ��!!!
					hst_packs.add(str_entry); //
				}
			}
			return hst_packs;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		} finally {
			try {
				myJarInputStream.close(); //
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * ����bin3�ļ���
	 */
	private void initBin3DllFileNames() {
		try {
			org.jdom.Element bin3Dlls = doc.getRootElement().getChild("bin3files"); //�õ�ext3jars�ӽ��!!
			if (bin3Dlls != null) {
				long ll_1 = System.currentTimeMillis();
				java.util.List jarFiles = bin3Dlls.getChildren(); // �õ������ӽ��!!
				if (jarFiles != null && jarFiles.size() > 0) {
					String[] str_allNames = new String[jarFiles.size()]; //
					for (int i = 0; i < jarFiles.size(); i++) {
						org.jdom.Element dllFileName = (org.jdom.Element) jarFiles.get(i); //
						str_allNames[i] = dllFileName.getText(); //�����ļ���...
					}
					ServerEnvironment.setBin3Dlls(str_allNames); //
				}
				long ll_2 = System.currentTimeMillis();
				infoMsg("��ʼ��bin3�ļ��嵥����,������[" + jarFiles.size() + "]���ļ�,��ʱ[" + (ll_2 - ll_1) + "]����"); //
			} else {
				System.err.println("û��ָ��<bin3files>���,����ܵ��¿ͻ�����ȱ�ٵ������İ�����������.."); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void initLastStartTime() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String str_currDateTime = sdf.format(new Date()); //��ǰʱ��
			ServerEnvironment.setProperty("SERVERREALSTARTTIME", str_currDateTime); //������ʵ��������ʱ��,��Ϊ�ڼ����־����ҪҪʵ��ʱ��,�������ǰ汾��!!!

			String str_date = null;
			//�°汾����ʱ����������ļ�weblight.xml�����û����������VERSIONNUMBER ��������ֵΪ�ա�����ݵ�ǰʱ�����ð汾�š������ػ����ļ�
			if (ServerEnvironment.getProperty("VERSIONNUMBER") == null || ServerEnvironment.getProperty("VERSIONNUMBER") == "" || ServerEnvironment.getProperty("VERSIONNUMBER").equals("")) { //��������˰汾��!!!
				str_date = str_currDateTime; //
			} else {//�°汾����ʱ����������ļ�weblight.xml�����������VERSIONNUMBER�� ��������Ե�ֵ���ɰ汾�š�����汾������ǰ����ͬ������Ҫ���ػ����ļ�
				str_date = ServerEnvironment.getProperty("VERSIONNUMBER");
			}

			ServerEnvironment.setProperty("LAST_STARTTIME", str_date); //���ñ���,�ͻ��˻����Ŀ¼�����Ը����ƶ����!!!
			ServerEnvironment.getInstance().put("LAST_STARTTIME", "TomCat������ʱ��", str_date); //

			InitParamVO[] oldInitPars = ServerEnvironment.getInstance().getInitParamVOs(); //
			InitParamVO[] newInitPars = new InitParamVO[oldInitPars.length + 1]; //
			newInitPars[0] = new InitParamVO("LAST_STARTTIME", str_date, null); //
			System.arraycopy(oldInitPars, 0, newInitPars, 1, oldInitPars.length); //
			ServerEnvironment.getInstance().setInitParamVOs(newInitPars); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void initLoginHref() {
		try {
			if (ServerEnvironment.vc_alltables != null && ServerEnvironment.vc_alltables.contains("PUB_LOGINHREF")) { //�������������,�����״ΰ�װʱ�ᱨ��!!
				CommDMO commDMO = new CommDMO();
				HashVO[] hvs = commDMO.getHashVoArrayByDS(null, "select * from pub_loginhref"); //
				if (hvs.length > 0) {
					String[][] str_hrefs = new String[hvs.length][6];
					for (int i = 0; i < hvs.length; i++) {
						str_hrefs[i][0] = hvs[i].getStringValue("href_x"); //
						str_hrefs[i][1] = hvs[i].getStringValue("href_y"); //
						str_hrefs[i][2] = hvs[i].getStringValue("href_width"); //
						str_hrefs[i][3] = hvs[i].getStringValue("href_height"); //
						str_hrefs[i][4] = hvs[i].getStringValue("href_alt"); //
						str_hrefs[i][5] = hvs[i].getStringValue("href_url"); //
					}
					ServerEnvironment.setLoginHref(str_hrefs); //
				}
				infoMsg("��ʼ����¼ҳ���ȵ����,��������[" + hvs.length + "]���ȵ�!"); //
			}
		} catch (Exception e) {
			errorMsg("��ʼ����¼ҳ���ȵ�ʧ��,ԭ��:" + e.getMessage()); //
			e.printStackTrace(); //
		}
	}

	private void initSecondProjectBoot() {
		if (doc == null) {
			return;
		}

		long ll_1 = System.currentTimeMillis();
		// WLTLogger.getLogger(this).debug("��ʼ��ʼ�����ο�����ĿBoot������...");
		org.jdom.Element datasources = doc.getRootElement().getChild("secondprojectboot"); // �õ�datasources�ӽ��!!
		String[] str_bootclass = null;
		if (datasources != null) {
			if (datasources != null) {
				java.util.List sources = datasources.getChildren(); // �õ������ӽ��!!
				str_bootclass = new String[sources.size()];
				for (int i = 0; i < sources.size(); i++) { // ���������ӽ��!!
					if (sources.get(i) instanceof org.jdom.Element) { //
						org.jdom.Element node = (org.jdom.Element) sources.get(i); //
						str_bootclass[i] = node.getText(); // �õ�����
					}
				}
			}

			for (int i = 0; i < str_bootclass.length; i++) {
				if (str_bootclass[i] != null && !str_bootclass[i].trim().equals("")) {
					try {
						System.out.println("��ʼ�������ο�����Ŀ������:[" + str_bootclass[i] + "].."); //
						Class.forName(str_bootclass[i].trim()).newInstance(); //
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}

		long ll_2 = System.currentTimeMillis();
		WLTLogger.getLogger(this).debug("��ʼ�����ο�����ĿBoot���������,��ʱ[" + (ll_2 - ll_1) + "]");
	}

	/**
	 * ��ʼ��Log4j����
	 */
	public void initLog4j() {
		String str_dir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //
		if (str_dir != null && !str_dir.equals("")) {
			File filedir = new File(str_dir); //
			if (!filedir.exists()) {
				filedir.mkdirs(); //
			}
		}

		org.jdom.Element log4j = doc.getRootElement().getChild("log4j"); // �õ�log4j�ӽ��!!
		if (log4j == null) {
			System.out.println("�����ļ���û�ж���log4j,�밴���¸�ʽ����:"); //
			System.out.println("<log4j>");
			System.out.println("<server_level>debug</server_level>");
			System.out.println("<server_outputtype>3</server_outputtype>");
			System.out.println("<client_level>debug</client_level>");
			System.out.println("<client_outputtype>3</client_outputtype>");
			System.out.println("</log4j>");
			System.exit(1);
		}

		String server_level = log4j.getChildText("server_level");
		String server_outputtype = log4j.getChildText("server_outputtype");

		String client_level = log4j.getChildText("client_level"); //
		String client_outputtype = log4j.getChildText("client_outputtype"); //

		Log4jConfigVO lo4jConfigVO = new Log4jConfigVO();
		lo4jConfigVO.setServer_level(server_level); //
		lo4jConfigVO.setServer_outputtype(server_outputtype);
		lo4jConfigVO.setClient_level(client_level);
		lo4jConfigVO.setClient_outputtype(client_outputtype); //
		ServerEnvironment.getInstance().setLog4jConfigVO(lo4jConfigVO); //

		// ����������ʱ,�����Ƚ��������־���һ��!��ϵͳ���Խ׶���Ҫ��ͣ����,��Ҫ�������!
		try {
			File file = new File(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/_weblight_log/weblight.txt"); //
			if (file.exists()) {
				file.delete(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); // ���ܸ��ļ��ᱻ��ס,�Ӷ�ɾ����.
		}
		String str_logdir = ServerEnvironment.getProperty("WLTLOGDIR"); //��ǰֱ����WLTUPLOADFILEDIR��һ���,��������Ⱥģʽʹ��������������,Ϊ�˱�֤д��־������,���Ա��뽫��־���ϴ��ļ��ֿ���!
		if (str_logdir == null) {
			str_logdir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //
		}
		WLTLogger.config(str_logdir, server_level, server_outputtype); //
		getLogger().info("��ʼ��Log4j�ɹ�,level=" + server_level + ",outputtype=" + server_outputtype + ""); //�����־
	}

	/**
	 * ��ʼ����ϵͳ..
	 */
	private void initInnerSystem() {
		String str_text = ServerEnvironment.getProperty("INNERSYSTEM"); //
		if (str_text != null) {
			String[] str_items1 = tbUtil.split(str_text, ";"); //
			String[][] str_return = new String[str_items1.length][2]; //
			for (int i = 0; i < str_items1.length; i++) {
				str_return[i] = tbUtil.split(str_items1[i], ",");
			}
			ServerEnvironment.setInnerSys(str_return); // ������ϵͳ
		}
	}

	/**
	 * �ύ��������
	 * 
	 * @param _initContext
	 */
	private void commitTrans(WLTInitContext _initContext) {
		// System.out.println("�ύ�ô�Զ�̷�����������!"); //
		if (_initContext.isGetConn()) {
			WLTDBConnection[] conns = _initContext.GetAllConns();
			for (int i = 0; i < conns.length; i++) {
				try {
					conns[i].transCommit();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void rollbackTrans(WLTInitContext _initContext) {
		// System.out.println("�ع��ô�Զ�̷�����������!"); //
		if (_initContext.isGetConn()) {
			WLTDBConnection[] conns = _initContext.GetAllConns();
			for (int i = 0; i < conns.length; i++) {
				try {
					conns[i].transRollback();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void closeConn(WLTInitContext _initContext) {
		// System.out.println("�رոô�Զ�̷�����������!"); //
		if (_initContext.isGetConn()) {
			WLTDBConnection[] conns = _initContext.GetAllConns();
			for (int i = 0; i < conns.length; i++) {
				try {
					conns[i].close(); // �ر�ָ������Դ����
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void releaseContext(WLTInitContext _initContext) {
		// System.out.println("�ͷŸô�Զ�̷���������Դ!"); //
		try {
			_initContext.release(); // �ͷ�������Դ!!
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	private String replaceMacroPar(String _par) {
		try {
			String str_newPar = _par; //
			String[] str_pars = tbUtil.getFormulaMacPars(_par, "${", "}"); //
			if (str_pars != null && str_pars.length > 0) {
				for (int i = 0; i < str_pars.length; i++) {
					str_newPar = tbUtil.replaceAll(str_newPar, "${" + str_pars[i] + "}", getPushSysPropValue(str_pars[i])); //�滻!!!
				}
			}
			System.out.println("����[" + _par + "]����Ҫ��̬�滻�ĺ���,����ת����õ���ֵ[" + str_newPar + "]"); //
			return str_newPar; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return _par;
		}
	}

	/**
	 * ȡϵͳ����!!Ϊ�˸�������չ��,�Ժ�ͳһŪһ���̶�����PUSH.OTHERPAR,����������ֱ������������,�����Ļ�,�¼Ӳ���ʱ,��cmd�����Ҫ����!!
	 * @param _prop
	 * @return
	 */
	private String getPushSysPropValue(String _prop) {
		String str_value = System.getProperty(_prop); //
		if (str_value != null) {
			return str_value; //
		}
		str_value = System.getProperty("WEBLIGHT.XML"); //
		if (str_value == null) {
			return null;
		}
		HashMap map = tbUtil.convertStrToMapByExpress(str_value, ";", "="); //
		return (String) map.get(_prop); //
	}

	private void infoMsg(String _msg) {
		Logger logger_tmp = getLogger(); //
		if (logger_tmp == null) {
			System.out.println(_msg); //
		} else {
			logger_tmp.info(_msg); //
		}
	}

	private void errorMsg(String _msg) {
		Logger logger_tmp = getLogger(); //
		if (logger_tmp == null) {
			System.err.println(_msg); //
		} else {
			logger_tmp.error(_msg); //
		}
	}

	public Logger getLogger() {
		if (logger == null) {
			logger = WLTLogger.getLogger(BootServlet.class);
			return logger;
		} else {
			return logger;
		}
	}

	/***
	 * Gwang 2013/04/28
	 * ȡweblight.jar�е� MANIFEST.MF�������Ϣ, ����ӡ
	 * ����Ҳ���weblight.jar��ʹ��Ĭ��ֵ
	 */
	private void printWeblighVersion() {
		String jarPath = ServerEnvironment.getProperty("SERVERREALPATH");
		jarPath = jarPath.replace('\\', '/');
		jarPath = jarPath + "/WEB-INF/lib/weblight.jar";
		String ver = "5.0";
		String date = "2013-04-28";
		try {
			File f = new File(jarPath);
			if (f.exists()) {
				JarFile jf = new JarFile(jarPath);
				Manifest mf = jf.getManifest();
				Attributes atts = mf.getMainAttributes();
				ver = atts.getValue("Webliht-Version");
				date = atts.getValue("Webliht-Date");
			}
			System.out.println("\r\n");
			System.out.println("********************************************************************************"); // //
			System.out.println("*                  WebLight J2EE/SOA FrameWork " + ver + " [" + date + "]                *"); // //
			System.out.println("********************************************************************************"); // //
			System.out.println("�ʼִ�г�ʼ����������[" + this.getClass().getName() + "]......"); //			

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}

/**************************************************************************
 * $RCSfile: BootServlet.java,v $  $Revision: 1.63 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: BootServlet.java,v $
 * Revision 1.63  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:48  Administrator
 * *** empty log message ***
 *
 * Revision 1.62  2012/08/23 02:13:25  xch123
 * *** empty log message ***
 *
 * Revision 1.61  2012/07/30 01:51:41  haoming
 * *** empty log message ***
 *
 * Revision 1.60  2012/03/08 02:28:16  xch123
 * *** empty log message ***
 *
 * Revision 1.59  2012/02/09 09:14:05  xch123
 * *** empty log message ***
 *
 * Revision 1.58  2012/02/09 07:43:17  xch123
 * *** empty log message ***
 *
 * Revision 1.57  2012/02/09 07:19:57  xch123
 * *** empty log message ***
 *
 * Revision 1.56  2012/02/03 08:37:04  xch123
 * *** empty log message ***
 *
 * Revision 1.55  2011/11/15 08:55:20  xch123
 * *** empty log message ***
 *
 * Revision 1.54  2011/10/10 06:31:34  wanggang
 * restore
 *
 *
 **************************************************************************/
