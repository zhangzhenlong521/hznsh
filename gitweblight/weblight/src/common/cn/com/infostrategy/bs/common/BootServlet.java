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
 * 平台的入口启动类,非常关键!!
 * 该类是平台启动类,该类声名为final类型是为了保证该类所有方法不被覆盖,项目初始化的逻辑只要重写一个BootServlet就可以了
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
	 * 初始化数据库连接
	 */
	
	public void init() throws ServletException {
		ServerConsolePrintStream mySystemOut = new ServerConsolePrintStream(System.out);
		System.setOut(mySystemOut);
		ServerConsolePrintStream mySystemErr = new ServerConsolePrintStream(System.err);
		System.setErr(mySystemErr);
		long ll_1 = System.currentTimeMillis();

		ServerEnvironment.setProperty("JVMSITE", "SERVER"); // 系统属性,用来判断当前虚拟机是客户端还是服务器端
		try {
			loadConfigXML(); // 加载配置文件,这是最关键的第一步!!!这一步如果失败,系统直接退出!
		} catch (Throwable tr) {
			tr.printStackTrace(); //
			System.exit(0); //
		}

		try {
			// 初始化一些最基本的参数
			try {
				// debugMsg(" ");
				initSomePars(); //
			} catch (Throwable tr) {
				tr.printStackTrace(); //
			}

			// 打印Weblith平台版本信息 Gwang 2013/4/28
			this.printWeblighVersion();

			// 初始化log4j
			try {
				initLog4j(); //
			} catch (Throwable tr) {
				tr.printStackTrace(); //
			}

			try {
				if (!checkMachineIdentify()) { //先校验机器是否是合法的如果校验不过则不做了!!
					return;
				}
			} catch (Exception e) {
				e.printStackTrace(); //
			}

			// 子系统..
			try {
				initInnerSystem(); //
			} catch (Throwable tr) {
				tr.printStackTrace(); //
			}

			// 初始化数据库连接池..
			try {
				// debugMsg(" ");
				initDataBasePool();
			} catch (Throwable tr) {
				tr.printStackTrace();
			}

			// 初始化ModuleService池..
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
			// 初始化登录页面的热点.
			try {
				initLoginHref(); //
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			//加载extJar文件名清单..
			try {
				initExt3JarFileNames(); //
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			//加载bin3Dll文件名清单..
			try {
				initBin3DllFileNames(); //
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			//设置图片名称!
			try {
				String str_realpath = ServerEnvironment.getProperty("SERVERREALPATH"); //
				str_realpath = str_realpath.replace('\\', '/');
				str_realpath = str_realpath + "/WEB-INF/lib/weblight_images.jar";
				File jarfile = new File(str_realpath); //
				if (jarfile.exists()) { //如果文件存在!!!
					JarInputStream jin = new JarInputStream(new FileInputStream(jarfile)); //
					JarEntry jarEntry = null; //
					ArrayList al_imgNames = new ArrayList(); //
					while ((jarEntry = jin.getNextJarEntry()) != null) { //只要下载一个,同时一下子将包中所有兄弟都缓存!! 从而极大提高性能!! 但会消耗内存!! 但客户端不要紧!!
						String str_itemFileName = jarEntry.getName();
						if (str_itemFileName != null && str_itemFileName.startsWith("cn/com/weblight/images/") && !str_itemFileName.startsWith("cn/com/weblight/images/workflow/") && !str_itemFileName.startsWith("cn/com/weblight/images/pushineui/") && !str_itemFileName.startsWith("cn/com/weblight/images/scrollbar/")
								&& (str_itemFileName.endsWith(".gif") || str_itemFileName.endsWith(".jpg") || str_itemFileName.endsWith(".png"))) {
							str_itemFileName = str_itemFileName.substring(str_itemFileName.lastIndexOf("/") + 1, str_itemFileName.length()); //
							al_imgNames.add(str_itemFileName); //
						}
					}
					String[] str_imgNames = (String[]) al_imgNames.toArray(new String[0]); //
					ServerEnvironment.getInstance().setImagesNames(str_imgNames); //设置图片名称!!
					//System.out.println("设置了多少个图片=[" + str_imgNames.length + "]"); //
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			// 初始化jar缓存
			try {
				// -DRunModel=DEVELOPE,在Eclipse的Tomcat配置的JDK配置中加入JVM启动参数,则不做jar缓存了!实际运行时肯定不会有这个参数,所以缓存!即在服务器端也有一个开发模式启动的概念!!
				if (ServerEnvironment.getProperty("RunModel") != null && ServerEnvironment.getProperty("RunModel").equals("DEVELOPE")) { // 如果是开发模式,则不做缓存JAR操作,以提高效率!!
				} else {
					if (doc != null) {
						String[] str_newInitJarCacheFiles = null; //
						String[] str_initcacheJars = getInitJars(); //初始化的jar缓存!
						if (str_initcacheJars == null || str_initcacheJars.length == 0) {
							str_newInitJarCacheFiles = new String[] { "wlappletloader.jar" }; //强行对wlappletloader.jar做缓存!
						} else {
							str_newInitJarCacheFiles = new String[str_initcacheJars.length + 1]; //
							str_newInitJarCacheFiles[0] = "wlappletloader.jar"; //
							System.arraycopy(str_initcacheJars, 0, str_newInitJarCacheFiles, 1, str_initcacheJars.length); //
						}
						vec_images = (new InitJarPackageCache(ServerEnvironment.getProperty("SERVERREALPATH"), str_newInitJarCacheFiles)).getImagesVec(); //做缓存!!!
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			// 初始化最后起动时间
			try {
				initLastStartTime();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}

			try {
				initSysThread(); //启动系统线程,即有一些系统本身需要不停跑的线程!!
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
			commitTrans(initContext); // 提交事务!各项初始化都是吃掉异常的,即各步初始化互相独立,不参与一次事务回滚,所以这里永远是提交!!
			closeConn(initContext); // 关闭连接!
			releaseContext(initContext); // 释放资源
		} catch (Throwable ex) {
			ex.printStackTrace();
		} finally {
			long ll_2 = System.currentTimeMillis();
			infoMsg("☆结束应用[" + this.getServletContext().getRealPath("/") + "]的初始化[" + this.getClass().getName() + "],总共耗时[" + (ll_2 - ll_1) + "]"); //
		}

	}

	/**
	 * 加载XML配置文件!!
	 * 
	 */
	private void loadConfigXML() {
		long ll_1 = System.currentTimeMillis();
		java.io.InputStream ins = null; //
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			ins = this.getClass().getResourceAsStream("/weblight.xml"); //
			doc = builder.build(ins); // 加载XML
			long ll_2 = System.currentTimeMillis();
			System.out.println("加载配置文件:[weblight.xml]成功,耗时[" + (ll_2 - ll_1) + "]毫秒."); //
		} catch (Throwable ex) {
			System.out.println("加载配置文件:[weblight.xml]失败,系统直接退出,请确认ClassPath中是否存在该文件!!"); //
			ex.printStackTrace();
			System.exit(0); // 系统直接退出!
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
	 * 基本参数
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
							//如果是"${}"的样子,则转一下,从系统属性中取! 之所以这么做是仿照Tomcat的动态配置,因为在集群时,有些参数不一样（比如日志文件目录,上传文件目录）,
							//为了不复制整个webapp,只需配置启动文件参数,从cmd中直接使用-D传入,所以搞了这样一个设计!!即本质上是为了做集群更方便!!
							//在深圳农商行项目中,王雷发现高并发时,下载ext3文件非常慢,感觉第一次同时使用系统时,的确会因为高并发产生性能问题(但我们培训时为什么没有发现这个问题呢?难道是因为区域网的原因?)
							//所以说在系统使用初期强烈建议最好使用集群!!然后等高峰期过后再改回单个服务(比如一周以后)!!
							if (str_value.indexOf("${") >= 0) {
								str_value = replaceMacroPar(str_value); //
							}
							ServerEnvironment.setProperty(str_key, str_value); // 在系统属性中加入!!
							ServerEnvironment.getInstance().put(str_key, str_descr, str_value); // 往缓存中送入...
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
			// debugMsg("初始化基本参数结束,耗时[" + (ll_2 - ll_1) + "]");
		} catch (Throwable tr) {
			tr.printStackTrace();
		}

		if ("Y".equals(ServerEnvironment.getProperty("ISLOADRUNDERCALL"))) { //是否是压力测试状态,即在压力测试状态,去掉了许多写监控日志的功能,从而保证压力测试效果更好!!默认是False!! 如果以后能自动判断出这个状态就更好的!!
			ServerEnvironment.isLoadRunderCall = true; //
		}

		if ("N".equals(ServerEnvironment.getProperty("isOutPutToQueue"))) { //是否将控制台输出至缓存队列?邮储项目中遇到因为这个输出而JVM溢出!
			ServerEnvironment.isOutPutToQueue = false; //
		}

		if ("Y".equals(ServerEnvironment.getProperty("ISPAGEPKINCACHE"))) { //分页时是否做缓存,有两种缓存,一个是count()缓存,一个是首页主键缓存!!默认是False
			ServerEnvironment.isPaginationInCache = true; //
		}

		String str_pageCount = ServerEnvironment.getProperty("ISPAGECOUNTINCACHE"); //
		if (str_pageCount != null && str_pageCount.toUpperCase().startsWith("Y")) { //分页时的求和是否做缓存!
			ServerEnvironment.isPageCountInCache = true; //
			if (str_pageCount.length() > 1) {
				int li_cycle = Integer.parseInt(str_pageCount.substring(1, str_pageCount.length())); //
				ServerEnvironment.pacgeCountCacheCycle = li_cycle; //
			}
		}

		String str_pageFalsity = ServerEnvironment.getProperty("ISPAGEFALSITY"); //
		if (str_pageFalsity != null && str_pageFalsity.toUpperCase().startsWith("Y")) { //分页时的求和是否做缓存!
			ServerEnvironment.isPageFalsity = true; //
			if (str_pageFalsity.length() > 1) {
				int li_sleep = Integer.parseInt(str_pageFalsity.substring(1, str_pageFalsity.length())); //
				ServerEnvironment.falsitySleep = li_sleep; //循环次数,越大越快也越假!!
			}
		}

		if (ServerEnvironment.getProperty("SERVERTYPE") != null && ServerEnvironment.getProperty("SERVERTYPE").equals("TOMCAT")) {
			ServerEnvironment.setProperty("SERVERREALPATH", this.getServletContext().getRealPath("/").replace('\\', '/')); // /
		}
		ServerEnvironment.setProperty("WebAppRealPath", ServerEnvironment.getProperty("SERVERREALPATH")); //  
		ServerEnvironment.getInstance().put("WebAppRealPath", "本应用的绝对地址", ServerEnvironment.getProperty("SERVERREALPATH")); //
		long ll_2 = System.currentTimeMillis(); //
		System.out.println("初始化系统<init-param>参数成功,耗时[" + (ll_2 - ll_1) + "]"); //
	}

	/**
	 * 校验机器的唯一性
	 * @throws Exception
	 */
	private boolean checkMachineIdentify() {
		String initpolicy = ServerEnvironment.getProperty("SERVERINITPOLICY"); //服务器启动效验策略,A策略是原有机制 B策略是校验"计算机名称"+"用户名"+"项目名"
		if (initpolicy == null || initpolicy.equals("")) {
			initpolicy = "A";
		}
		String str_validatepwd = System.getenv("COMPUTERNAME"); //机器名!
		str_validatepwd = (new StringBuilder(String.valueOf(str_validatepwd))).append("_").append(System.getenv("USERNAME")).toString(); //用户名,发现在WebSphere中取得为null,而且有的系统如果换一个用户后就不能用了,所以考虑是否
		if ("B".equals(initpolicy)) { //义乌是一个虚拟机多个硬件服务器，通过软件给虚拟机自动分配硬件cpu去跑。所以把cpu效验信息去掉，通过项目名称效验。[郝明2012-07-26]
			String p_name = ServerEnvironment.getProperty("PROJECT_NAME"); //获取项目名称。
			str_validatepwd = (new StringBuilder(String.valueOf(str_validatepwd))).append("_").append(p_name).toString(); //
		} else if ("C".equals(initpolicy)) {
			str_validatepwd = ServerEnvironment.getProperty("PROJECT_NAME"); //获取项目名称。
		} else { //采用默认机制
			str_validatepwd = (new StringBuilder(String.valueOf(str_validatepwd))).append("_").append(System.getenv("PROCESSOR_IDENTIFIER")).toString(); //CPU的唯一标识符
			str_validatepwd = (new StringBuilder(String.valueOf(str_validatepwd))).append("_").append(System.getenv("PROCESSOR_REVISION")).toString(); //
			str_validatepwd = (new StringBuilder(String.valueOf(str_validatepwd))).append("_").append(System.getenv("NUMBER_OF_PROCESSORS")).toString(); //CPU数量,比如单核或双核			
		}
		//System.out.println("计算的识别码=[" + str_validatepwd + "]"); ////
		String str_md5 = md5(str_validatepwd.toUpperCase());
		String str_sha = sha(str_md5);

		String str_sn = ServerEnvironment.getProperty("SN"); //设置的SN码
		String str_sn1 = ServerEnvironment.getProperty("SN1"); //设置的SN码
		if (str_sn == null) {
			errorMsg((new StringBuilder("系统设置了防盗版校验,你没有设置验证码或者验证码不对,请将识别码[")).append(str_md5).append("]发给系统供应商以生成校验码,然后将校验码设置在SN参数上!").toString());
			return false;
		}
		//if (str_sn.equals("pushworld2012")) { //中外运项目中客户强烈要求因为集群问题要去掉,暂时有个万能码!!!
		//	return true; //
		//}
		if (str_sn.startsWith("${")) { //每次从10服务器上拷贝WebRoot目录解压后,都要手工再次修改weblight.xml中的SN参数值,太费事,所以将SN设置成${PUSH_SN},然后在启动命令中定义,这样每次拷贝WebRoot目录时,就不要再修改SN了!所以这里加个判断!!【xch/2012-03-08】
			str_sn = replaceMacroPar(str_sn); //
		}

		if (str_sn.equals(str_sha) || (str_sn1 != null && str_sn1.equals(str_sha))) { //如果直接相等,则认为是ok的,兼容原来的!
			return true; //
		} else {
			if (str_sn.length() != str_sha.length() + 18) {
				errorMsg("系统设置了正版校验功能,你设置的验证码是非法的,请将识别码[" + str_md5 + "]发给系统供应商以生成新的验证码,然后将验证码设置在SN参数上!"); //
				return false; //
			}
			int li_result = unEncrySnCode(str_sn, str_sha); //解密并校验!!!
			if (li_result == 0) { //如果成功!!
				//设置有效日期在系统缓存中,这样每次远程调用时都会进行比较!!!
				return true;
			} else if (li_result == -1) {
				errorMsg("系统设置了正版校验功能,你设置的验证码不对,请将识别码[" + str_md5 + "]发给系统供应商以生成验证码,然后将验证码设置在SN参数上!"); //
				return false;
			} else if (li_result == -2) {
				errorMsg("系统设置了正版校验功能,你设置的验证码已过试用期,请将识别码[" + str_md5 + "]发给系统供应商以生成新的验证码,然后将验证码设置在SN参数上!"); //
				return false;
			} else {
				errorMsg("系统设置了正版校验功能,你设置的验证码是非法格式的,请将识别码[" + str_md5 + "]发给系统供应商以生成新的验证码,然后将验证码设置在SN参数上!"); //
				return false;
			}
		}

	}

	//解密SN码!!
	private int unEncrySnCode(String _snCode, String _sha) {
		try {
			String str_1 = _snCode.substring(0, 1); //第一个字母
			String str_2 = _snCode.substring(1, 2); //第二个字母
			String str_snCode = _snCode.substring(2, _snCode.length()); //后面的
			int li_space = getSapceByTwoWord(str_1, str_2); //相距位置
			if (li_space < 0) {
				return -999;
			}
			String str_prefix = str_snCode.substring(0, li_space); //
			String str_encrydate = str_snCode.substring(li_space, li_space + 16); //加密的日期
			String str_subfix = str_snCode.substring(li_space + 16, str_snCode.length()); //后辍
			String str_sncode = str_prefix + str_subfix; //实际的SN码
			if (!str_sncode.equals(_sha)) { //如果不等,则认为是非法的
				return -1; //
			}

			String str_unencrydate = unEncryDate(str_encrydate); //解密后的日期
			if (str_unencrydate == null) { //如果解密日期失败!!
				return -999;
			}

			SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE);
			String str_currdate = sdf_curr.format(new Date()); //当前日期
			if (str_currdate.compareTo(str_unencrydate) >= 0) { //如果是过期
				return -2; //
			} else {
				ServerEnvironment.setProperty("EFFECTLIMITDATE", str_unencrydate); //在系统缓存中注册有效期限!以便每次远程调用时都计算!
				return 0; //最终认为是成功的
			}
		} catch (Exception e) {
			System.err.println("对SN码进行解密时,发生异常[" + e.getMessage() + "]"); //
			return -999; //
		}
	}

	/**
	 * 加密SN码,包括日期!!
	 * 原理是生成两个随机字母,他们之间的位置表示的是在SN的第多少位个插入了日期,然后将SN截成两段!中间插入的是日期!
	 * 这样会防止有人知道截取尾部的方法而改成正式码!!!
	 * @param _sn
	 * @param _limitDate
	 * @return
	 */
	private String encryIdentifyCodeAndDate(String _identifyCode, String _limitDate) {
		String str_initSNCode = sha(_identifyCode); //先对识别码进行sha加密!!!
		String[] str_36words = getRanDom36Words(); //随机的字符串
		Random ranDom = new Random(); //
		String str_1 = str_36words[ranDom.nextInt(36)]; ////第一个字母
		int li_length = str_initSNCode.length(); //SN码的长度,比如30位
		if (li_length > 35) {
			li_length = 35; //
		}
		int li_space = ranDom.nextInt(li_length); //0-30
		String str_2 = getNextPosStr(str_1, li_space); //第二位!
		String str_prefix = str_initSNCode.substring(0, li_space); //前辍
		String str_subfix = str_initSNCode.substring(li_space, str_initSNCode.length()); //后辍
		String str_encryDateCode = entryDate(_limitDate); //
		String str_return = str_1 + str_2 + str_prefix + str_encryDateCode + str_subfix; //
		return str_return; //
	}

	/**
	 * 加密日期,原理是先生成一个8位数的随机码,然后将日期拆分成8个数字,以这个数字作为位差,生成加密码!!!
	 * @param _date
	 * @return
	 */
	private String entryDate(String _date) {
		char[] chars = _date.toCharArray();//将日期拆成数字
		String[] str_randomStrs = getRanDom8Strs(); //取得8个随机的字符串
		String[] str_encryStrs = new String[8]; //加密后的数组
		for (int i = 0; i < 8; i++) {
			int li_incre = Integer.parseInt("" + chars[i]); //将日期转换成数字,该数字就是一个增量!!!
			str_encryStrs[i] = getNextPosStr(str_randomStrs[i], li_incre); //取得随机字符串后面某段距离位置的新字符串
			//System.out.println(str_randomStrs[i] + "往前推[" + li_incre + "]位,得到" + str_encryStrs[i]); //

		}
		StringBuilder sb_return = new StringBuilder(); //
		for (int i = 0; i < 8; i++) {
			sb_return.append(str_randomStrs[i]); //原数据!!!
		}
		for (int i = 0; i < 8; i++) {
			sb_return.append(str_encryStrs[7 - i]); //加密数据!!
		}
		//System.out.println(sb_return.toString()); //
		return sb_return.toString(); //
	}

	//解密日期
	private String unEncryDate(String _date) {
		StringBuilder sb_date = new StringBuilder(); //
		for (int i = 0; i < 8; i++) { //
			String str_1 = _date.substring(i, i + 1); //从前往 后
			String str_2 = _date.substring(16 - i - 1, 16 - i); //从后往前,7,8
			int li_space = getSapceByTwoWord(str_1, str_2); //
			if (li_space < 0 || li_space >= 10) { //不应该大于10
				return null;
			}
			//System.out.println("字母[" + str_1 + "," + str_2 + "]间的距离是["+li_space+"]"); //
			sb_date.append("" + li_space); //
		}
		return sb_date.toString(); //
	}

	//取得随机的8个字符串
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
		//System.out.println("字母1[" + _word1 + "]的位置[" + li_pos_1 + "],字母2[" + _word2 + "]的位置[" + li_pos_2 + "]"); //
		if (li_pos_2 >= li_pos_1) { //如果后者大于前者,则直接拿后者减去前者位置
			return li_pos_2 - li_pos_1; //
		} else {
			return li_pos_2 + (36 - li_pos_1); //前者加上36再减去后者
		}
	}

	//取得某个字符串后多少位的字符串!!
	private String getNextPosStr(String _str1, int _space) {
		int li_pos = getPos(_str1); //先计算出该字符串的位置!
		int li_newPos = li_pos + _space; //新位置
		if (li_newPos > 35) { //如果越界了,则倒过来从头找
			return getRanDom36Words()[li_newPos - 36]; //前面的位置!!
		} else {
			return getRanDom36Words()[li_newPos]; //
		}
	}

	//取得一个字符串在数组中的位置!!
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
	 * 取得随机顺序的36个字符串!
	 * @return
	 */
	public String[] getRanDom36Words() {
		return new String[] { "i", "v", "b", "z", "w", "8", "o", "q", "l", "9", "x", "j", "0", "2", "f", "g", "e", "h", "a", "p", "k", "c", "4", "u", "6", "t", "5", "1", "m", "d", "n", "7", "s", "y", "3", "r" }; //随机顺序的36个字母这是密钥!!无论是加密还是解密必须是一样!!
	}

	private String getStr(ArrayList _alTmp, String[] _aa) {
		int li_pos = new Random().nextInt(36); //0-35
		if (!_alTmp.contains("" + li_pos)) { //如果不包含
			_alTmp.add("" + li_pos); //
			return _aa[li_pos]; //
		} else {
			return getStr(_alTmp, _aa); //继续找
		}
	}

	public static void main(String[] args) {
		System.out.println(new BootServlet().sha("29b4b82a53y5a319a46b7z8799xaaa95"));
	}

	public final String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'x', 'y', 'z' }; // 我们自己的钥串
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
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'x', 'y', 'z' }; // 我们自己的钥串
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
			} // 如果找不到,则返回
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // 将结果字符串加上原来前辍
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // 将剩余的加上
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
	 * 从Nova2Config.xml获得要初始化的所有jar文件名
	 * 
	 * @return
	 */
	private String[] getInitJars() {
		if (doc == null) {
			return null;
		}

		org.jdom.Element datasources = doc.getRootElement().getChild("jarcachefiles"); // 得到datasources子结点!!
		String[] str_jars = null;
		if (datasources != null) {
			if (datasources != null) {
				java.util.List sources = datasources.getChildren(); // 得到所有子结点!!
				str_jars = new String[sources.size()];
				for (int i = 0; i < sources.size(); i++) { // 遍历所有子结点!!
					if (sources.get(i) instanceof org.jdom.Element) { //
						org.jdom.Element node = (org.jdom.Element) sources.get(i); //
						str_jars[i] = node.getText(); // 得到属性
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
	 * 初始化数据库连接
	 * 
	 */
	public void initDataBasePool() {
		if (doc == null) {
			return;
		}

		long ll_1 = System.currentTimeMillis(); //
		// debugMsg("开始初始化数据库连接池...");
		org.jdom.Element datasources = doc.getRootElement().getChild("datasources"); // 得到datasources子结点!!
		if (datasources != null) {
			java.util.List sources = datasources.getChildren(); // 得到所有子结点!!
			Vector v_dsVOs = new Vector(); //
			boolean isDefaultDSActive = false; //默认数据源是否是激活状态??
			for (int i = 0; i < sources.size(); i++) { // 遍历所有子结点!!
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

					String str_datasourcename = node.getAttributeValue("name"); // 得到属性
					String str_datasourcedescr = node.getAttributeValue("descr"); // 得到属性

					String str_dbtype = null;
					if (node.getChild("dbtype") != null) {
						str_dbtype = node.getChild("dbtype").getText(); //
					}
					String str_dbversion = null;
					if (node.getChild("dbversion") != null) {
						str_dbversion = node.getChild("dbversion").getText(); //数据库版本
					}
					String str_dbdriver = null;
					if (node.getChild("driver") != null) {
						str_dbdriver = node.getChild("driver").getText();
					}
					String str_dburl = null;
					if (node.getChild("dburl") != null) {
						str_dburl = node.getChild("dburl").getText();
					}
					//为了让部署更方便!!可以从启动命令中传入动态参数!!
					if (str_dburl.indexOf("${") >= 0) {
						str_dburl = replaceMacroPar(str_dburl); //
					}

					String str_user = null;
					if (node.getChild("user") != null) {
						str_user = node.getChild("user").getText(); // 用户名
					}
					if (str_user.indexOf("${") >= 0) {
						str_user = replaceMacroPar(str_user); //
					}

					String str_pwd = null;
					if (node.getChild("pwd") != null) {
						str_pwd = node.getChild("pwd").getText(); // 密码
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

					if (node.getChild("isencryuser") != null && "Y".equalsIgnoreCase(node.getChild("isencryuser").getText())) { //如果指定了 isencryuser=Y,则表示加密的,则立即进行解密! 
						//这是上海农商行强烈提出的要求!! 也有一定的道理!! 如果没有这个参数,则表示是明码,即仍然兼容原来的模式!!!
						str_user = new DESKeyTool().decrypt(str_user); //如果定义是加密的,则进行解密!!!
					}

					if (node.getChild("isencrypwd") != null && "Y".equalsIgnoreCase(node.getChild("isencrypwd").getText())) { //如果指定了 isencrypwd=Y,则表示密码是加密的,则立即进行解密! 
						//这是上海农商行强烈提出的要求!! 也有一定的道理!! 如果没有这个参数,则表示是明码,即仍然兼容原来的模式!!!
						str_pwd = new DESKeyTool().decrypt(str_pwd); //如果定义是加密的,则立即对密码进行解密!!!
					}

					str_dbdriver = str_dbdriver.trim();
					str_dburl = str_dburl.trim(); //
					if (provider_url == null && initial_context_factory == null) {//如果没配这2个参数则执行连接，配了就不连接了，直接放到数据源里
						Properties myProperties = new Properties();
						myProperties.setProperty("user", str_user);
						myProperties.setProperty("password", str_pwd);
						try {
							Class.forName(str_dbdriver); // 创建数据源
							GenericObjectPool connectionPool = new GenericObjectPool(null); //创建一个连接池!!
							connectionPool.setMaxActive(li_maxsize); // 设置最大活动数
							connectionPool.setMaxIdle(li_maxsize); //

							ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(str_dburl, myProperties);
							PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true); //
							Class.forName("org.apache.commons.dbcp.PoolingDriver"); // 创建dbcp池驱动
							PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
							driver.registerPool(str_datasourcename, connectionPool); //注册连接池
							try {
								if (li_initsize > 0) {
									WLTLogger.getLogger(this).info("正在尝试连接数据库[" + str_datasourcename + "][" + str_dburl + "][" + str_user + "/***],如果该数据源不通,则这里可能会阻塞很长时间....");
								}
								for (int j = 0; j < li_initsize; j++) {
									connectionPool.addObject(); //要错就错在这里!!!如果数据库连不上,这里会报错!
								}

								if (li_initsize > 0 && i == 0) {
									ServerEnvironment.getInstance().setDefaultdatasource(str_datasourcename); // 设置默认数据源!!
									WLTLogger.getLogger(this).info("创建数据库连接池[" + str_datasourcename + "][" + str_dburl + "][" + str_user + "/***][" + li_initsize + "](默认数据源)");
									isDefaultDSActive = true; //
								} else {
									WLTLogger.getLogger(this).info("创建数据库连接池[" + str_datasourcename + "][" + str_dburl + "][" + str_user + "/***][" + li_initsize + "]");
								}
							} catch (Exception ex) {
								WLTLogger.getLogger(this).error("创建数据库连接池[" + str_datasourcename + "][" + str_dburl + "][" + str_user + "/***]失败!!!原因:");
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
							WLTLogger.getLogger(this).info("连接JNDI数据源[" + str_datasourcename + "]成功！");
							if (i == 0) {
								ServerEnvironment.getInstance().setDefaultdatasource(str_datasourcename); // 设置默认数据源!!
								isDefaultDSActive = true; //
							}
						} catch (Exception ex) {
							WLTLogger.getLogger(this).error("连接JNDI数据源[" + str_datasourcename + "]失败!!!原因:");
							ex.printStackTrace(); //
						}
					}

					DataSourceVO dsVO = new DataSourceVO();
					dsVO.setName(str_datasourcename); //
					dsVO.setDescr(str_datasourcedescr); //
					dsVO.setDbtype(str_dbtype); //
					dsVO.setDbversion(str_dbversion); //数据库版本!
					dsVO.setDriver(str_dbdriver); //
					dsVO.setDburl(str_dburl);
					dsVO.setUser(str_user);
					dsVO.setPwd(str_pwd); //
					dsVO.setInitsize(li_initsize); // 初始化大小
					dsVO.setProvider_url(provider_url);
					dsVO.setInitial_context_factory(initial_context_factory);
					v_dsVOs.add(dsVO); // 注册一个池...
					ServerEnvironment.getInstance().put(str_datasourcename, str_datasourcedescr, str_dburl);
					if (i == 0) {
						ServerEnvironment.getInstance().put("DEFAULTDATASOURCENAME", "默认数据源名称", str_datasourcename); //
					}
				}
			}
			DataSourceVO[] allVOs = (DataSourceVO[]) v_dsVOs.toArray(new DataSourceVO[0]); //
			ServerEnvironment.getInstance().setDataSourceVOs(allVOs); // 设置所有数据源!!
			if (isDefaultDSActive) { //如果默认数据源是活动的!
				try {
					//九台农商行需要在应用服务启动时刷新平台参数，
					//太平导出需要导出全量，因CommDMO中用到新增平台参数“列表最大查询条数”，故在应用服务启动时先设置参数缓存，然后再查数据库【李春娟/2017-09-26】
					cn.com.infostrategy.bs.common.SystemOptions.getInstance().reLoadDataFromDB(false);
					long ll_c1 = System.currentTimeMillis(); //
					String[] str_allTables = new CommDMO().getAllSysTables(null, null); //
					for (int i = 0; i < str_allTables.length; i++) {
						str_allTables[i] = str_allTables[i].toUpperCase(); //
					}
					long ll_c2 = System.currentTimeMillis(); //
					WLTLogger.getLogger(this).info("计算默认数据源一共有[" + str_allTables.length + "]个表,耗时[" + (ll_c2 - ll_c1) + "]毫秒!"); //
					ServerEnvironment.vc_alltables = new Vector(Arrays.asList(str_allTables)); //
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
			}
		}

		long ll_2 = System.currentTimeMillis();
		// debugMsg("初始化数据库连接池结束,耗时[" + (ll_2 - ll_1) + "]");
	}

	/**
	 * 初始化服务池
	 * 
	 */
	public void initServicePool() {
		if (doc == null) {
			return;
		}

		long ll_1 = System.currentTimeMillis();
		// debugMsg("开始初始化SOA服务池...");
		org.jdom.Element serviceroot = doc.getRootElement().getChild("moduleservices"); // 得到moduleservices子结点!!
		if (serviceroot != null) {
			java.util.List sources = serviceroot.getChildren(); // 得到所有子结点!!
			for (int i = 0; i < sources.size(); i++) { // 遍历所有子结点!!
				if (sources.get(i) instanceof org.jdom.Element) { //
					org.jdom.Element node = (org.jdom.Element) sources.get(i); //
					String str_servicename = node.getAttributeValue("name"); // 得到属性
					String str_implclass = node.getChild("implclass").getText(); //
					String str_initsize = node.getChild("initsize").getText(); //
					String str_maxsize = node.getChild("poolsize").getText(); //

					str_implclass = str_implclass.trim(); //

					int li_initsize = Integer.parseInt(str_initsize); //
					int li_maxsize = Integer.parseInt(str_maxsize); //

					ServicePoolableObjectFactory factory = new ServicePoolableObjectFactory(str_servicename, str_implclass); //
					GenericObjectPool pool = new GenericObjectPool(factory); // 创建对象池,最多100个实例!!!!
					pool.setMaxActive(li_maxsize); // 最大活动数
					pool.setMaxIdle(li_maxsize); // 最大句柄数
					try {
						for (int j = 0; j < li_initsize; j++) { // 先创建3个实例!!
							pool.addObject(); // 先创建一个实例
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					ServicePoolFactory.getInstance().registPool(str_servicename, str_implclass, pool); // 注册一个池!!
					infoMsg("初始化SOA服务池[" + str_servicename + "][" + str_implclass + "]成功,共创建[" + str_initsize + "]个实例");
				}
			}
		}

		long ll_2 = System.currentTimeMillis();
		// debugMsg("初始化SOA服务池结束,耗时[" + (ll_2 - ll_1) + "]");
	}

	private void initSysThread() {
		new HeartThread().start(); //
	}

	/**
	 * 执行任务!!
	 */
	public void initJob() {
		try {
			//袁江晓 20170411 更改 换用新的定时任务机制
			if (ServerEnvironment.getProperty("ISSTARTJOB") != null && ServerEnvironment.getProperty("ISSTARTJOB").equals("Y") && ServerEnvironment.vc_alltables != null && ServerEnvironment.vc_alltables.contains("PUB_JOB")) { //如果weblight.xml没有指定暂停JOB则执行JOB
				HashVO[] hvs_jobs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_job where activeflag='Y'"); //
				for (int i = 0; i < hvs_jobs.length; i++) {
					new WLTJobTimer(hvs_jobs[i]).start(); // 启动线程
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 加载ext3 jar文件名
	 */
	private void initExt3JarFileNames() {
		try {
			org.jdom.Element ext3jars = doc.getRootElement().getChild("ext3files"); //得到ext3jars子结点!!
			if (ext3jars != null) {
				long ll_1 = System.currentTimeMillis();
				HashSet hst_ext3Cls = new HashSet(); //
				java.util.List jarFiles = ext3jars.getChildren(); // 得到所有子结点!!
				if (jarFiles != null && jarFiles.size() > 0) {
					String[] str_allNames = new String[jarFiles.size()]; //
					for (int i = 0; i < jarFiles.size(); i++) {
						org.jdom.Element jarFileName = (org.jdom.Element) jarFiles.get(i); //
						str_allNames[i] = jarFileName.getText(); //设置文件名...
						File ext3ItemFile = new File(ServerEnvironment.getProperty("SERVERREALPATH") + str_allNames[i]); //SERVERREALPATH
						if (ext3ItemFile.exists()) { //如果这个文件存在!
							HashSet hst_item = getOneFileAllPackages(ext3ItemFile); //
							if (hst_item != null) {
								hst_ext3Cls.addAll(hst_item); //
							}
						}
					}
					ServerEnvironment.setExt3Jars(str_allNames); //
					ServerEnvironment.setExt3JarClsNames((String[]) hst_ext3Cls.toArray(new String[0])); //缓存起来客户端需要!!!在中铁建项目中遇到王律师的Win7从EAC中登录较慢,经过跟踪与分析,发现在缓存Ext3目录下的所有文件名耗时2.5秒,一是因为高峰搞了许多Ext3jar,二是可能Win7下Java读本地文件就是比WinXP慢一些!
				}
				long ll_2 = System.currentTimeMillis();
				infoMsg("初始化ext3文件清单结束,共发现[" + jarFiles.size() + "]个jar文件,其中共有[" + hst_ext3Cls.size() + "]个Class文件,共耗时[" + (ll_2 - ll_1) + "]毫秒"); //
			} else {
				System.err.println("没有指定<ext3files>结点,这可能导致客户端因缺少第三方的包而不能运行.."); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//取得一个文件中的所有类名!!!
	private HashSet getOneFileAllPackages(File _file) {
		JarInputStream myJarInputStream = null; //
		try {
			HashSet hst_packs = new HashSet(); //
			myJarInputStream = new JarInputStream(new FileInputStream(_file)); //
			while (1 == 1) {
				JarEntry myJarEntry = myJarInputStream.getNextJarEntry();
				if (myJarEntry == null) { // 如果jarEntry为空则中断循环
					break;
				}
				if (!myJarEntry.isDirectory()) {
					String str_entry = myJarEntry.getName(); //格式是[org/jdesktop/jdic/desktop/Desktop.class]
					str_entry = str_entry.replace('/', '.'); //需要替换一下!!!
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
	 * 加载bin3文件名
	 */
	private void initBin3DllFileNames() {
		try {
			org.jdom.Element bin3Dlls = doc.getRootElement().getChild("bin3files"); //得到ext3jars子结点!!
			if (bin3Dlls != null) {
				long ll_1 = System.currentTimeMillis();
				java.util.List jarFiles = bin3Dlls.getChildren(); // 得到所有子结点!!
				if (jarFiles != null && jarFiles.size() > 0) {
					String[] str_allNames = new String[jarFiles.size()]; //
					for (int i = 0; i < jarFiles.size(); i++) {
						org.jdom.Element dllFileName = (org.jdom.Element) jarFiles.get(i); //
						str_allNames[i] = dllFileName.getText(); //设置文件名...
					}
					ServerEnvironment.setBin3Dlls(str_allNames); //
				}
				long ll_2 = System.currentTimeMillis();
				infoMsg("初始化bin3文件清单结束,共发现[" + jarFiles.size() + "]个文件,耗时[" + (ll_2 - ll_1) + "]毫秒"); //
			} else {
				System.err.println("没有指定<bin3files>结点,这可能导致客户端因缺少第三方的包而不能运行.."); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void initLastStartTime() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String str_currDateTime = sdf.format(new Date()); //当前时间
			ServerEnvironment.setProperty("SERVERREALSTARTTIME", str_currDateTime); //服务器实际启动的时间,因为在监控日志就是要要实际时间,而并不是版本号!!!

			String str_date = null;
			//新版本发布时。如果配置文件weblight.xml中如果没有设置属性VERSIONNUMBER 或者属性值为空。则根据当前时间设置版本号。并下载缓存文件
			if (ServerEnvironment.getProperty("VERSIONNUMBER") == null || ServerEnvironment.getProperty("VERSIONNUMBER") == "" || ServerEnvironment.getProperty("VERSIONNUMBER").equals("")) { //如果定义了版本号!!!
				str_date = str_currDateTime; //
			} else {//新版本发布时。如果配置文件weblight.xml中如果设置了VERSIONNUMBER。 则根据属性的值生成版本号。如果版本号与以前的相同。则不需要下载缓存文件
				str_date = ServerEnvironment.getProperty("VERSIONNUMBER");
			}

			ServerEnvironment.setProperty("LAST_STARTTIME", str_date); //设置变量,客户端缓存的目录就是以该名称定义的!!!
			ServerEnvironment.getInstance().put("LAST_STARTTIME", "TomCat启动的时间", str_date); //

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
			if (ServerEnvironment.vc_alltables != null && ServerEnvironment.vc_alltables.contains("PUB_LOGINHREF")) { //如果有这个表才做,否则首次安装时会报错!!
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
				infoMsg("初始化登录页面热点结束,共设置了[" + hvs.length + "]个热点!"); //
			}
		} catch (Exception e) {
			errorMsg("初始化登录页面热点失败,原因:" + e.getMessage()); //
			e.printStackTrace(); //
		}
	}

	private void initSecondProjectBoot() {
		if (doc == null) {
			return;
		}

		long ll_1 = System.currentTimeMillis();
		// WLTLogger.getLogger(this).debug("开始初始化二次开发项目Boot启动类...");
		org.jdom.Element datasources = doc.getRootElement().getChild("secondprojectboot"); // 得到datasources子结点!!
		String[] str_bootclass = null;
		if (datasources != null) {
			if (datasources != null) {
				java.util.List sources = datasources.getChildren(); // 得到所有子结点!!
				str_bootclass = new String[sources.size()];
				for (int i = 0; i < sources.size(); i++) { // 遍历所有子结点!!
					if (sources.get(i) instanceof org.jdom.Element) { //
						org.jdom.Element node = (org.jdom.Element) sources.get(i); //
						str_bootclass[i] = node.getText(); // 得到属性
					}
				}
			}

			for (int i = 0; i < str_bootclass.length; i++) {
				if (str_bootclass[i] != null && !str_bootclass[i].trim().equals("")) {
					try {
						System.out.println("开始创建二次开发项目启动类:[" + str_bootclass[i] + "].."); //
						Class.forName(str_bootclass[i].trim()).newInstance(); //
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		}

		long ll_2 = System.currentTimeMillis();
		WLTLogger.getLogger(this).debug("初始化二次开发项目Boot启动类结束,耗时[" + (ll_2 - ll_1) + "]");
	}

	/**
	 * 初始化Log4j配置
	 */
	public void initLog4j() {
		String str_dir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //
		if (str_dir != null && !str_dir.equals("")) {
			File filedir = new File(str_dir); //
			if (!filedir.exists()) {
				filedir.mkdirs(); //
			}
		}

		org.jdom.Element log4j = doc.getRootElement().getChild("log4j"); // 得到log4j子结点!!
		if (log4j == null) {
			System.out.println("配置文件中没有定义log4j,请按如下格式定义:"); //
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

		// 重启服务器时,总是先将当天的日志清空一下!在系统测试阶段需要不停重启,需要这个动作!
		try {
			File file = new File(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/_weblight_log/weblight.txt"); //
			if (file.exists()) {
				file.delete(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); // 可能该文件会被锁住,从而删不掉.
		}
		String str_logdir = ServerEnvironment.getProperty("WLTLOGDIR"); //以前直接与WLTUPLOADFILEDIR在一起的,但后来集群模式使用了网络驱动器,为了保证写日志的性能,所以必须将日志与上传文件分开来!
		if (str_logdir == null) {
			str_logdir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //
		}
		WLTLogger.config(str_logdir, server_level, server_outputtype); //
		getLogger().info("初始化Log4j成功,level=" + server_level + ",outputtype=" + server_outputtype + ""); //输出日志
	}

	/**
	 * 初始化子系统..
	 */
	private void initInnerSystem() {
		String str_text = ServerEnvironment.getProperty("INNERSYSTEM"); //
		if (str_text != null) {
			String[] str_items1 = tbUtil.split(str_text, ";"); //
			String[][] str_return = new String[str_items1.length][2]; //
			for (int i = 0; i < str_items1.length; i++) {
				str_return[i] = tbUtil.split(str_items1[i], ",");
			}
			ServerEnvironment.setInnerSys(str_return); // 设置子系统
		}
	}

	/**
	 * 提交所有事务
	 * 
	 * @param _initContext
	 */
	private void commitTrans(WLTInitContext _initContext) {
		// System.out.println("提交该次远程访问所有事务!"); //
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
		// System.out.println("回滚该次远程访问所有事务!"); //
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
		// System.out.println("关闭该次远程访问所有事务!"); //
		if (_initContext.isGetConn()) {
			WLTDBConnection[] conns = _initContext.GetAllConns();
			for (int i = 0; i < conns.length; i++) {
				try {
					conns[i].close(); // 关闭指定数据源连接
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void releaseContext(WLTInitContext _initContext) {
		// System.out.println("释放该次远程访问所有资源!"); //
		try {
			_initContext.release(); // 释放所有资源!!
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
					str_newPar = tbUtil.replaceAll(str_newPar, "${" + str_pars[i] + "}", getPushSysPropValue(str_pars[i])); //替换!!!
				}
			}
			System.out.println("变量[" + _par + "]有需要动态替换的宏码,经过转换后得到新值[" + str_newPar + "]"); //
			return str_newPar; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return _par;
		}
	}

	/**
	 * 取系统属性!!为了更具有扩展性,以后统一弄一个固定参数PUSH.OTHERPAR,将其他参数直接设置在这里,这样的话,新加参数时,连cmd命令都不要改了!!
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
	 * 取weblight.jar中的 MANIFEST.MF的相关信息, 并打印
	 * 如果找不到weblight.jar则使用默认值
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
			System.out.println("☆开始执行初始化启动程序[" + this.getClass().getName() + "]......"); //			

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
