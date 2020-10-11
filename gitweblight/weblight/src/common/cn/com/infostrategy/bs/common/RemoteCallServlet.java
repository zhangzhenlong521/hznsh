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
 * 所有远程访问调用的Servlet..
 * 该Servlet一定要注意线程安全问题!!,不要用类变量,否则就会出现多线程的安全问题!
 * sunfujun/20121208/邮储交易成功率
 * @author xch
 *
 */
public class RemoteCallServlet extends HttpServlet { //implements SingleThreadModel { //单线程模式,即每个请求都会创建一个实例,

	private static final long serialVersionUID = 7103285415882911348L;

	private boolean ifGetInstanceFromPool = true; //是否从池中取实例,还是直接新建实例.

	private Logger logger = WLTLogger.getLogger(RemoteCallServlet.class);

	//private boolean bo_iszipstream = false; //是否压缩流..
	private TBUtil tbUtil = new TBUtil(); //
	private BSUtil bsUtil = null; //

	public static Long THREADCOUNT = new Long(0); //记录线程并发数
	public static long MAXTHREADCOUNT = 0; //记录线程并发数
	public static long TOTALTHREADCOUNT = 0; //记录

	public static ArrayList callThreadTimeList = new ArrayList(); //记录每次线程调用的时间! 用于统计是否真的很忙!!
	public static Hashtable revokeThreadHashtable = new Hashtable(); //实际调用的线程的存储器!

	//public static Hashtable callThreadHashTable = new Hashtable(); //key是总序号_开始时间,value是结束时间!! 开始访问时,装入key,value是0,结束时送入Value! 由心跳线程来删除有value的值,或者那些value为0但超时实在太长的!!
	//从而计算出当前实际在处理的线程! 还有每个线程的处理时间! 还有每秒上线人数! 最后应有一个表,即每秒线程数,平均反应时长! 然后送入一个全局变量! 然后送入一张表! 一天8小时

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
			dealCall(_request, _response); //新的调用方法,不需要WrapperUtil了
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
	 * 增加线程数记录,方法要同步.. 
	 * 拿序号_当前时间插入到一个Vector中!然后将这个作为该次会话的key! 在调用结束时删除该值! 然后心跳线程循环删除其中 
	 */
	public String addThreadCount() { //
		long ll_newId = 0; //
		//synchronized (RemoteCallServlet.THREADCOUNT) { //锁定!
		ll_newId = RemoteCallServlet.THREADCOUNT++; //加1并返回作为我的key
		//}
		long ll_currtime = System.currentTimeMillis(); //
		//String str_threadId = ll_newId + "_" + ll_currtime; //
		//callThreadHashTable.put(str_threadId,new Long(0));  //加入这个! 以后就靠这一个东西来计算每秒的上线数(折算成秒)!总计访问次数(前辍)! 当前在线的线程数,以及各个线程的处理时间(当前时间减去后辍)!!!
		//如果发现每秒的访问量很大或者延时超过了2秒未处理,或者虽然已处理但时长也超过两秒,则说明负载饱和了!! 即其实并发人数多但反应快并不表示吃不消,并发人数少但反应也快也不是! 只有反应慢才是真正的忙了! 
		callThreadTimeList.add(ll_currtime); //在屁股后面加入!!!

		RemoteCallServlet.TOTALTHREADCOUNT++; //只加!
		if (RemoteCallServlet.THREADCOUNT > RemoteCallServlet.MAXTHREADCOUNT) { //如果当前线程数超过最大线程数,则更新最大线程数!!!
			RemoteCallServlet.MAXTHREADCOUNT = RemoteCallServlet.THREADCOUNT;
		}
		return null; //str_threadId; //
	}

	/**
	 * 减少线程数记录,方法要同步..
	 */
	public void delThreadCount(String _threadId) {
		//callThreadHashTable.put(str_threadId,new Long(System.currentTimeMillis()));  //转入Value,删除由心跳线程来完全,心跳线程同时负责将结果送入到数据库!! 即每秒的反应次数,总时长,平均时长!
		RemoteCallServlet.THREADCOUNT = RemoteCallServlet.THREADCOUNT - 1; //
	}

	/**
	 * 根据参数中的class类名与实际值,反射调用实际值
	 * @param _request
	 * @param _response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void dealCall(ServletRequest _request, ServletResponse _response) throws ServletException, IOException {
		//System.out.println("客户端请求的数据类型:[" + _request.getContentType() + "],请求的数据大小["  + _request.getContentLength() + "]");  //
		//logger.info("开始调用RemoteCallServlet....."); //
		long ll_1 = System.currentTimeMillis(); //到了服务器的时间
		_response.setCharacterEncoding("UTF-8"); //字符集!!指定死算了,万一遇到什么Unix系统系统默认字符集会变,可能会产生奇怪问题!
		_response.setBufferSize(10240); //缓冲区设为50K,ping 命令上限上64K,是否意味着太大的包也会造成网络不稳?如果是chunked模式的返回模式,遇到Win7下很慢,即使改成Content-Length,也要要将缓冲区搞大点,书上说会减少网络吞吐数量! 但我发现如果在同步是设了后,这里再设竟然没用,好象仍然用第一次设置的,难道会共享这个?
		_response.setContentType("text/html"); //直接写【text/html】是能跑的,但可能有的防火墙检测时间就短了?【application/x-compress】【application/x-java-serialized-object】二进制流!!高峰在安徽农信遇到有的Win7客户端访问很慢,孙富君在中铁建也遇到有的电脑就是慢,怀疑是否没有指定这个,导致防火墙在判定时耗时太久!
		String str_newsessionID = ((HttpServletRequest) _request).getSession().getId(); //取得SessionID,一次会话的唯一性标识,第一次访问时将它返给客户端!!;  //
		String str_clientsessionID = null; //
		RemoteCallParVO par_vo = null;
		try {
			//读取入参..!
			InputStream request_in = _request.getInputStream(); //得到输入流!!!!!
			String str_in_desc = ""; //
			Object obj = null;
			if (ServerEnvironment.getProperty("ISZIPSTREAM") != null && ServerEnvironment.getProperty("ISZIPSTREAM").equals("Y")) { //如果采用压缩流
				CompressBytesVO compVO = getByteFromInputStreamByMark(request_in); //从流中取得二进制数组
				//System.out.println("从客户端传过来的值是压缩否[" + compVO.isZip() + "]"); //
				int li_in_length = compVO.getBytes().length; //
				//解压处理!!!
				byte[] decompressBytes = null;
				long ll_decom_1 = System.currentTimeMillis(); //
				if (!compVO.isZip()) { //如果不是压缩的,则直接等于,这样性能会高许多!
					decompressBytes = compVO.getBytes(); //
				} else {
					decompressBytes = decompressBytes(compVO.getBytes()); //解压
				}
				long ll_decom_2 = System.currentTimeMillis(); //
				int li_in_compresslength = decompressBytes.length; //解压后大小
				str_in_desc = str_in_desc + "解压耗时" + (ll_decom_2 - ll_decom_1) + "," + li_in_length + "->" + li_in_compresslength + "Bytes=" + (li_in_length / 1024) + "->" + (li_in_compresslength / 1024) + "K,解压比" + (li_in_compresslength == 0 ? 0 : (li_in_length * 100 / li_in_compresslength)) + "%";
				//解密处理!!!
				if (ServerEnvironment.getProperty("ISENCRYPT") != null && ServerEnvironment.getProperty("ISENCRYPT").equals("Y")) { //只有定义参数对进行!! 只有在压缩状态下才支持加密与解密处理!!!
					long ll_des_1 = System.currentTimeMillis(); //
					int li_len_1 = decompressBytes.length; //
					DESKeyTool desTool = new DESKeyTool(); //
					decompressBytes = desTool.decrypt(decompressBytes); //
					int li_len_2 = decompressBytes.length; //
					long ll_des_2 = System.currentTimeMillis(); //
					str_in_desc = str_in_desc + ",解密耗时" + (ll_des_2 - ll_des_1) + ",解密长度" + li_len_1 + "->" + li_len_2 + ""; //
				}
				obj = deserialize(decompressBytes); //从解压后的二制数组序列化一个对象...
			} else { //如果不采用压缩流
				ObjectInputStream request_in_objStream = new ObjectInputStream(request_in); //
				obj = request_in_objStream.readObject(); //
				int li_length = serialize(obj).length;
				str_in_desc = li_length + "Bytes=" + (li_length / 1024) + "K"; //
				request_in_objStream.close();//以前没有关闭流，Loadrunner测试200并发会报错，建议及时关闭【李春娟/2017-03-16】
			}

			request_in.close(); //
			par_vo = (RemoteCallParVO) obj; //强制类型转换!!!
			int li_connTime = par_vo.getConnTime(); //客户端创建连接握手的时间! 高阳总在美国访问时速度很慢,怀疑是这个点的问题!!即路由太多了!!!
			str_in_desc = "握手耗时" + li_connTime + "," + str_in_desc; //

			//统计线程数!
			ThreadGroup threadGroup = Thread.currentThread().getThreadGroup(); //
			int li_ac = -1, li_agc = -1; //
			if (threadGroup != null) {
				li_ac = threadGroup.activeCount(); //JDK的API说这可能不准!!
				li_agc = threadGroup.activeGroupCount(); //
			}

			//如果内存接近980M,则不让访问了,保证系统不死机!!
			if (!ServerEnvironment.isLoadRunderCall) { //如果是LoadRunder在测试,则不做这些处理,从而保证性能很高!!
				long ll_jvm_total = Runtime.getRuntime().totalMemory() / (1024 * 1024); //
				long ll_jvm_busy = ll_jvm_total - (Runtime.getRuntime().freeMemory() / (1024 * 1024)); //
				int li_maxjvm = 1200; //JVM上限默认值!以前在招行中是900,太小了!实际上是可以达到1456的!
				if (ServerEnvironment.getProperty("MAXJVM") != null && !ServerEnvironment.getProperty("MAXJVM").equals("")) { //农行及邮储项目中遇到嫌太烦!所以搞亿
					li_maxjvm = Integer.parseInt(ServerEnvironment.getProperty("MAXJVM")); //
				}
				if (ll_jvm_busy > li_maxjvm) { //如果总开销超过1000M,正忙的内存超过800M,则提示不让访问,即在内存溢出之前提前警告!!!即不发发生成outOfMemorey的情况下仍然有人想访问!!
					System.gc(); //建议释放内存
					String str_errtext = "虚拟机堆栈[" + ll_jvm_busy + "," + ll_jvm_total + "]超过警界阀值[" + li_maxjvm + "M],请稍后再试!";
					logger.info(str_errtext); //
					returnExceptionMsg(str_errtext, _response, ll_1, false); //
					System.gc(); //建议释放内存
					return;
				}

				//如果超过100个并发,则不让访问了,保证不死机!!
				//				if (RemoteCallServlet.THREADCOUNT >100) { //警界值,一定要小于中间件服务器的上限才有意义!!!这样至少能保证中间件不会死掉!!!
				//					String str_errtext = "系统并发数超过100,可能有问题!!!"; //
				//					logger.warn(str_errtext); //
				//					returnExceptionMsg(str_errtext, _response, ll_1, false); //
				//					return;
				//				}

				//如果设置了这个参数 才进行并发数量的控制 这个数当出现真正的并发时不准但有参考意义 sunfujun
				// 其实将线程放到一个hashtable里并发数应该就准了 先这么搞吧 之前的逻辑
				int maxcurrthreadcount = SystemOptions.getIntegerValue("系统并发连接数", 0);
				if (maxcurrthreadcount > 0) {
					if (RemoteCallServlet.THREADCOUNT > maxcurrthreadcount) { //警界值,一定要小于中间件服务器的上限才有意义!!!这样至少能保证中间件不会死掉!!! 
						String str_errtext = "系统并发数超过" + maxcurrthreadcount + ",请稍后访问!"; //
						logger.warn(str_errtext); //
						returnExceptionMsg(str_errtext, _response, ll_1, false); //
						return;
					}
				}

				if (ServerEnvironment.getProperty("EFFECTLIMITDATE") != null) { //如果定义了系统有效期限!
					SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyyMMdd", Locale.SIMPLIFIED_CHINESE); //
					String str_currdate = sdf_curr.format(new Date(ll_1));
					if (str_currdate.compareTo(ServerEnvironment.getProperty("EFFECTLIMITDATE")) >= 0) {
						String str_errtext = "系统版本已过期,请与开发商联系申请最新的License码!"; //
						logger.info(str_errtext); //
						returnExceptionMsg(str_errtext, _response, ll_1, false); //
						return;
					}
				}

				//如果重启过了,则提醒!!!
				if (par_vo.getCurrVersion() != null && !par_vo.getCurrVersion().equals(ServerEnvironment.getProperty("LAST_STARTTIME"))) {
					String str_newversion = ServerEnvironment.getProperty("LAST_STARTTIME"); //新的版本号
					String str_datatime = str_newversion.substring(0, 4) + "-" + str_newversion.substring(4, 6) + "-" + str_newversion.substring(6, 8) + " " + str_newversion.substring(8, 10) + ":" + str_newversion.substring(10, 12) + ":" + str_newversion.substring(12, 14); //
//					String str_errtext = "服务器在[" + str_datatime + "]重新重启过,程序将强行退出,退出后请重新登录系统即可!!"; //
					String str_errtext = "登陆超时,程序将强行退出,退出后请重新登录系统即可!!"; //zzl[2019-2-13]
					logger.info(str_errtext); //
					returnExceptionMsg(str_errtext, _response, ll_1, true); //
					return;
				}
			}

			str_clientsessionID = par_vo.getCurrSessionVO().getHttpsessionid(); //取得客户端SesionID.
			if (str_clientsessionID == null) {
				str_clientsessionID = str_newsessionID; //如果是第一次,客户端过来的为空,则用当前的赋给他!
			}

			String clientIP1 = _request.getRemoteAddr(); //par_vo.getClientIP(); //取得Request中最后一个访问的来源IP地址
			String clientIP2 = par_vo.getClientIP(); //取得客户端IP地址
			String serverInsName = _request.getLocalAddr() + "_" + _request.getLocalPort();//IP加端口标示服务器实例名
			CurrSessionVO currSessionVO = par_vo.getCurrSessionVO(); //得到整个当前会话VO!!
			if (currSessionVO == null) {
				currSessionVO = new CurrSessionVO();
			}
			currSessionVO.setHttpsessionid(str_clientsessionID);
			currSessionVO.setClientIP1(clientIP1); //设置IP1,在服务器设置,会减少网络传输量,从而提高性能,而且这个IP也只能在服务器端取到,在客户端只能取得ip2
			currSessionVO.setClientIP2(clientIP2); //设置IP2!
			if (!ServerEnvironment.isLoadRunderCall) { //如果是LoadRunder在测试,则不做这些处理,从而保证性能很高!!
				currSessionVO.setSessionCallInfo(getRemoteCallParInfo(par_vo)); //设置所有调用信息
			}
			String serviceName = par_vo.getServiceName(); //服务名!
			String methodName = par_vo.getMethodName(); //方法名!

			String str_implClassName = ServicePoolFactory.getInstance().getImplClassName(serviceName); //实现类名!!
			RemoteCallReturnVO returnVO = null;

			long ll_beforeInvoke = 0; //
			if (str_implClassName == null) {
				throw new Exception("没有在启动配置文件[weblight.xml]中注册名为[" + serviceName + "]的远程访问服务!");
			} else {
				GenericObjectPool pool = null;
				Object instanceObj = null; //实例
				if (ifGetInstanceFromPool) {
					try {
						pool = ServicePoolFactory.getInstance().getPool(serviceName); //取得池
						instanceObj = pool.borrowObject(); //从池中得到实例!!!!
						//System.out.println("成功从池中抓取服务实例[" + str_implClassName + "],池中当前活动[" + pool.getNumActive() + "]"); //
					} catch (java.lang.ClassNotFoundException ex) {
						ex.printStackTrace();
						instanceObj = new WLTRemoteException("从池中抽取服务实例[" + str_implClassName + "]失败,原因:找不到该类!"); //	应该是这个类型以下雷同否则还要执行realinvoke/sunfujun/20121127
					} catch (Exception ex) {
						ex.printStackTrace();
						instanceObj = new WLTRemoteException("从池中抽取服务实例[" + str_implClassName + "]失败,原因:" + ex.getMessage()); //
					}
				} else {
					try {
						instanceObj = Class.forName(str_implClassName).newInstance(); //这里都是直接创建实例的,效率较低,以后可以考虑用Spring加载..
						//System.out.println("成功直接创建服务实例[" + str_implClassName + "]"); //
					} catch (Exception ex) {
						ex.printStackTrace();
						instanceObj = new WLTRemoteException("直接创建服务实例[" + str_implClassName + "]失败,原因:" + ex.getMessage()); //
					}
				}

				if (instanceObj instanceof WLTRemoteException) { //如果创建失败,即直接返回的是一个NovaRemoteException
					returnVO = new RemoteCallReturnVO(); //
					returnVO.setServiceName(serviceName); //直接的实现名
					returnVO.setServiceImplName(str_implClassName); //直接的实现名
					returnVO.setReturnObject(instanceObj); //
					returnVO.setDealtime(System.currentTimeMillis() - ll_1); //
					returnVO.setCallDBCount(0); //
					returnVO.setSessionID(str_clientsessionID); //
				} else { //如果创建实例成功!!
					long ll_beginInvoke = System.currentTimeMillis(); //真正开始处理
					ll_beforeInvoke = ll_beginInvoke - ll_1;
					returnVO = realInvoke(serverInsName, clientIP1, clientIP2, ll_beginInvoke, par_vo.getCurrCallMenuName(), par_vo.getCurrThreadCallStack(), serviceName, str_implClassName, instanceObj, methodName, par_vo.getParClasses(), par_vo.getParObjs(), currSessionVO); //真正调用!!,核心地带!!!!!!!!!!!!!!!
					returnVO.setSessionID(str_clientsessionID); //
					if (ifGetInstanceFromPool) { //如果是从池中取的则还要释放!!
						pool.returnObject(instanceObj); //释放!!
					}
				}
			}

			//处理当前在线用户!!!
			String str_loginuser = currSessionVO.getLoginUserCode(); //
			if (str_implClassName.equals("cn.com.infostrategy.bs.sysapp.login.SysAppServiceImpl") && methodName.equals("loginOut")) { //如果是退出系统方法,则不做
				//不记录
			} else {
				String str_currtime = tbUtil.getCurrTime(); //服务器端当前时间..
				Object objuser = ServerEnvironment.getLoginUserMap().get(str_clientsessionID); //根据SessionId去取
				if (SystemOptions.getBooleanValue("是否验证重复登陆", false)) {
					if (str_implClassName.equals("cn.com.infostrategy.bs.sysapp.login.SysAppServiceImpl") && methodName.equals("loginOneOff")) { // 如果是登陆
						if (returnVO.getReturnObject() instanceof LoginOneOffVO) {
							str_loginuser = ((LoginOneOffVO) returnVO.getReturnObject()).getCurrLoginUserVO().getCode();
							if (objuser == null) { //如果没取到
								ServerEnvironment.getLoginUserMap().put(str_clientsessionID, new String[] { str_clientsessionID, clientIP1, clientIP2, str_loginuser, str_currtime, str_currtime }); //新增一个
								int li_currAllUserCount = ServerEnvironment.getLoginUserMap().size(); //当前用户数量!!!
								if (li_currAllUserCount > ServerEnvironment.EVER_MAX_ONLINEUSERS) { //如果当前用户数超过曾经最大的在线用户数变量,则更新之!!!
									ServerEnvironment.EVER_MAX_ONLINEUSERS = li_currAllUserCount; //
								}
							} else { //如果取到
								String[] str_onlineusers = (String[]) objuser; //
								ServerEnvironment.getLoginUserMap().put(str_clientsessionID, new String[] { str_clientsessionID, clientIP1, clientIP2, str_loginuser, str_onlineusers[4], str_currtime }); //更改最后登录时间
							}
							// 挤掉别人 就是把别人的session干掉
							killOtherSession(str_clientsessionID, str_loginuser);
							//
						}
					} else {
						if (str_loginuser != null && objuser == null) { //如果没取到
							String str_errtext = "用户已在别处登陆,请退出系统!";
							logger.info(str_errtext); //
							returnExceptionMsg(str_errtext, _response, ll_1, true);
							return;
						}
					}
				} else {
					if (objuser == null) { //如果没取到
						ServerEnvironment.getLoginUserMap().put(str_clientsessionID, new String[] { str_clientsessionID, clientIP1, clientIP2, str_loginuser, str_currtime, str_currtime }); //新增一个
						int li_currAllUserCount = ServerEnvironment.getLoginUserMap().size(); //当前用户数量!!!
						if (li_currAllUserCount > ServerEnvironment.EVER_MAX_ONLINEUSERS) { //如果当前用户数超过曾经最大的在线用户数变量,则更新之!!!
							ServerEnvironment.EVER_MAX_ONLINEUSERS = li_currAllUserCount; //
						}
					} else { //如果取到
						String[] str_onlineusers = (String[]) objuser; //
						ServerEnvironment.getLoginUserMap().put(str_clientsessionID, new String[] { str_clientsessionID, clientIP1, clientIP2, str_loginuser, str_onlineusers[4], str_currtime }); //更改最后登录时间
					}
				}
			}

			int li_dbcount = returnVO.getCallDBCount(); ////
			long ll_beginpush = System.currentTimeMillis(); //服务器端开始输出的时间!
			String str_returnpars = outPutToclient(_response, returnVO); //输出至客户端!!!
			long ll_endpush = System.currentTimeMillis(); //输出结束!

			long li_busyJVM1 = returnVO.getJVM1(); //
			long li_busyJVM2 = returnVO.getJVM2(); //
			logger.info("[" + clientIP1 + "][" + clientIP2 + "][" + str_loginuser + "],调用服务[" + str_implClassName + "][" + methodName + "],逻辑运算耗时[" + returnVO.getDealtime() + "],运算前解压耗时[" + ll_beforeInvoke + "],输出耗时[" + (ll_endpush - ll_beginpush) + "],访问DB[" + li_dbcount + "]次,(" + Thread.currentThread().getName() + "),入参【" + str_in_desc + "】,出参【" + str_returnpars + "】,当前并发[" + li_ac + ","
					+ li_agc + "," + RemoteCallServlet.THREADCOUNT + "],最大并发[" + RemoteCallServlet.MAXTHREADCOUNT + "],JVM变化[" + li_busyJVM2 + "-" + li_busyJVM1 + "=" + (li_busyJVM2 - li_busyJVM1) + "K," + ((li_busyJVM2 - li_busyJVM1) / 1024) + "M]"); //
		} catch (Throwable ex) {
			ex.printStackTrace(); //
			RemoteCallReturnVO returnVO = new RemoteCallReturnVO(); //
			WLTRemoteException remoteEX = new WLTRemoteException(ex.getMessage());
			remoteEX.setServerTargetEx(ex);
			returnVO.setReturnObject(remoteEX); //
			returnVO.setDealtime(System.currentTimeMillis() - ll_1);
			returnVO.setCallDBCount(-1);
			returnVO.setSessionID(str_clientsessionID); //
			outPutToclient(_response, returnVO); //输出至客户端!!!
		}
	}

	/**
	 * 取得远程调用参数的!!
	 * @return
	 */
	private String getRemoteCallParInfo(RemoteCallParVO _parVO) {
		String str_currtime = tbUtil.getCurrTime(); //服务器端当前时间..
		StringBuilder sb_info = new StringBuilder(); //
		sb_info.append("时间:[" + str_currtime + "],IP:[" + _parVO.getClientIP() + "],用户名:[" + (_parVO.getCurrSessionVO() == null ? "null" : _parVO.getCurrSessionVO().getLoginUserName()) + "],类名:[" + _parVO.getServiceName() + "],方法名:[" + _parVO.getMethodName() + "()],参数:{<br>"); //
		sb_info.append(getCallParObjStr(_parVO.getParClasses(), _parVO.getParObjs(), true)); //
		sb_info.append("}");
		return sb_info.toString();
	}

	/**
	 * 用户重复登陆踢人操作
	 * @param newsessionid
	 * @param usercode
	 */
	private void killOtherSession(String newsessionid, String usercode) { // 把http://和上下文根也配到参数里了，是因为后来刷新其他集群实例缓存也要用到这个参数
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
					conn.setConnectTimeout(10000);//张珍龙改为了0，也不起作用，故改回原代码【李春娟/2018-06-25】
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
	 * 取得参数的字符串
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
				sb_info.append("〖" + clses[i].getName() + "〗=【" + str_obj + "】"); //
				if (_ishtml) {
					sb_info.append("<br>"); //
				} else {
					sb_info.append("§"); //
				}
			}
			String str_return = sb_info.toString(); //
			if (str_return.length() > 4000) { //如果超过4000个字符串则截取之！！
				str_return = str_return.substring(0, 4000); //
			}
			return str_return; //
		}

		return "";

	}

	/**
	 * 输出到客户端...
	 * @param _response
	 * @param _returnVO
	 */
	private String outPutToclient(ServletResponse _response, RemoteCallReturnVO _returnVO) {
		String str_return = ""; ////
		try {
			OutputStream outStream = _response.getOutputStream(); //
			if (ServerEnvironment.getProperty("ISZIPSTREAM") != null && ServerEnvironment.getProperty("ISZIPSTREAM").equals("Y")) { //是否压缩数据流
				byte[] bytes = serialize(_returnVO); //
				long ll_1 = System.currentTimeMillis(); //
				CompressBytesVO compressVO = compressBytes(bytes); //压缩字节
				long ll_2 = System.currentTimeMillis(); //
				byte[] compressBytes = compressVO.getBytes(); //
				str_return = compressBytes.length + "/" + bytes.length + "Bytes=" + (compressBytes.length / 1024) + "/" + (bytes.length / 1024) + "K,压缩比" + (compressBytes.length * 100 / bytes.length) + "%,压缩耗时" + (ll_2 - ll_1);
				_response.setContentLength(compressBytes.length + 1); //经过反复测试,发现如果这设这个就可能会造成连接非常慢,可能以前Win7下面的奇怪慢就是这个原因!!!
				streamOutPutToClient(outStream, new Boolean(compressVO.isZip()), compressBytes); //第一位是指定是否压缩!
			} else {
				byte[] bytes = serialize(_returnVO); //
				int li_length = bytes.length; //返回对象大小..
				str_return = li_length + "Bytes=" + (li_length / 1024) + "K"; //
				_response.setContentLength(bytes.length); //经过反复测试,发现如果这设这个就可能会造成连接非常慢,可能以前Win7下面的奇怪慢就是这个原因!!!
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
	 * 以流式输出对象，即可以控制流量，比如控制每秒最多只能输出多少KB的数据
	 * @param _out
	 * @param bytes
	 * @throws Exception
	 */
	private void streamOutPutToClient(OutputStream _out, Boolean _isZip, byte[] bytes) throws Exception {
		if (_isZip != null) {
			if (!_isZip) { //如果不压缩!!
				_out.write(new byte[] { 0 }); //
			} else { //如果是压缩!!
				_out.write(new byte[] { 1 }); //
			}
		}
		int li_cycleSize = 2048; //每次循环输出多少,越小越细越均匀,经过测试,发现1024是太小,而2048-5012则会明细提高性能!
		int li_start = 0; //定义坐标临时局部变量
		while (1 == 1) { //做死循环,向客户端输出数据,直接数据结束，其中通过每输出1K数据就休息一段时间的方式来控制流量,只要控制
			if ((li_start + li_cycleSize) >= bytes.length - 1) { //结果结尾坐标位置已超过文件长度
				_out.write(bytes, li_start, bytes.length - li_start); //
				break; //中断循环
			} else {
				_out.write(bytes, li_start, li_cycleSize); //每次循环只输出1024字节的数量,即1K的数据
				li_start = li_start + li_cycleSize; //位置坐标加1024
			}
		}
		_out.flush(); //清空缓冲区中内容,显式指定实际输出!!!
	}

	/**
	 * 真正调用!!
	 * @param _instanceObj
	 * @param _methodName
	 * @param _classes
	 * @param _parObjs
	 * @param _clientEnv 
	 * @return
	 */
	private RemoteCallReturnVO realInvoke(String serverInsName, String _clientIP1, String _clientIP2, long _beginTime, String _callMenuName, String _callThreadStack, String _serviceName, String _implClassName, Object _instanceObj, String _methodName, Class[] _classes, Object[] _parObjs, CurrSessionVO _currSessionVO) {
		WLTInitContext initContext = new WLTInitContext(); //
		initContext.regisCurrSession(_currSessionVO); //注册整个客户端环境,经LoadRunder压力测试,发现这是个耗时的地方! 而且问题就是因为该方法同步造成的! 即在并发情况下同步时会造成排队现象!!
		//long ll_endRegSession = System.currentTimeMillis(); //

		long li_busyJVM1 = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024; //
		String str_keyId = null; //
		if (!ServerEnvironment.isLoadRunderCall) {
			String[] str_initValue = new String[] { null, "" + _beginTime, "N", (_currSessionVO == null ? "" : _currSessionVO.getLoginUserName()), _implClassName, _methodName, getCallParObjStr(_classes, _parObjs, false), null, null, null, "" + li_busyJVM1, null, _callMenuName, _callThreadStack, "Y", "", serverInsName }; //"线程编号","开始时间","是否有反应","访问人","服务名","方法名","参数值","时长",取数大小,取数明细(sqls),访问前的JVM,访问后的JVM,菜单名,客户端堆栈!,是否成功,异常信息, 服务器实例名
			str_keyId = getNewId("" + _beginTime); //键值!!
			str_initValue[0] = str_keyId; //
			revokeThreadHashtable.put(str_keyId, str_initValue); //先送进去!!!
		}

		RemoteCallReturnVO returnVO = new RemoteCallReturnVO();
		try {
			Method mtehod = _instanceObj.getClass().getMethod(_methodName, _classes);
			Object obj = mtehod.invoke(_instanceObj, _parObjs); //调用对应方法返回值!!!
			//long ll_endRealInvoke = System.currentTimeMillis(); //
			int li_allStmtCount = commitTrans(initContext); //提交该次会话的所有事务!!经LoadRunder压力测试,发现这是个耗时的地方!
			long ll_endCommitTrans = System.currentTimeMillis(); //
			//logger.debug("Invoke调用共耗时[" + (ll_endCommitTrans - _beginTime) + "],其中注册session[" + (ll_endRegSession - _beginTime) + "],真正执行[" + (ll_endRealInvoke - ll_endRegSession) + "],提交事务耗时[" + (ll_endCommitTrans - ll_endRealInvoke) + "]"); //如果需要性能仔细跟踪,则打开这条注释!
			long li_busyJVM2 = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024; //

			returnVO.setServiceName(_serviceName); //服务接口名!
			returnVO.setServiceImplName(_implClassName); //真正的实现类名!
			returnVO.setReturnObject(obj); //
			returnVO.setDealtime(ll_endCommitTrans - _beginTime); //
			returnVO.setCallDBCount(li_allStmtCount); //访问DB次数!
			returnVO.setJVM1(li_busyJVM1); //
			returnVO.setJVM2(li_busyJVM2); //

			String str_forclientTackMsg = initContext.getCurrSessionCustStrInfoByKey("ForClientTackMsg", true); //
			if (str_forclientTackMsg != null && !str_forclientTackMsg.trim().equals("")) { //如果不为空!!!
				returnVO.setCallTrackMsg(str_forclientTackMsg); //
			}
			return returnVO; //
		} catch (InvocationTargetException ex) { //如果是反射调用异常
			int li_allStmtCount = rollbackTrans(initContext); //回滚该次会话的所有事务!必须保证
			//ex.getTargetException().printStackTrace(); //这里要包装远程异常			
			Throwable targetex = ex.getTargetException();
			//java.lang.IllegalStateException调低报错等级！【李春娟/2017-09-30】
			if (targetex instanceof java.sql.SQLException || targetex instanceof java.lang.IllegalStateException) { //太平提出SQL语句出错不要监控， 调低报错等级！ Gwang 2017-7-12 
				logger.warn("调用远程服务[" + _implClassName + "][" + _methodName + "()]错误", ex.getTargetException()); //
			} else if ("loginOneOff".equals(_methodName) || "cancelTask".equals(_methodName) || "getFirstTaskVO".equals(_methodName)) { //太平提出调低报错等级！ Gwang 2017-8-16
				logger.warn("调用远程服务[" + _implClassName + "][" + _methodName + "()]错误", ex.getTargetException()); //
			} else {
				logger.error("调用远程服务[" + _implClassName + "][" + _methodName + "()]错误", ex.getTargetException()); //
			}

			String str_message = targetex.getMessage(); //
			WLTRemoteException novaEx = new WLTRemoteException(str_message); //创建远程异常用以返回客户端!!
			novaEx.setServerTargetEx(targetex); //设置堆栈
			long ll_2 = System.currentTimeMillis(); //
			long li_busyJVM2 = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024; //
			returnVO.setServiceName(_serviceName); //服务接口名!
			returnVO.setServiceImplName(_implClassName); //真正的实现类名!
			returnVO.setReturnObject(novaEx); //
			returnVO.setDealtime(ll_2 - _beginTime); //耗时
			returnVO.setCallDBCount(li_allStmtCount); //访问DB次数!
			returnVO.setJVM1(li_busyJVM1); //
			returnVO.setJVM2(li_busyJVM2); //
			return returnVO;
		} catch (Throwable ex) {
			int li_allStmtCount = rollbackTrans(initContext); //回滚该次会话的所有事务
			ex.printStackTrace();
			WLTRemoteException novaEx = new WLTRemoteException(ex.getMessage()); //
			novaEx.setServerTargetEx(ex); //
			long ll_2 = System.currentTimeMillis(); //
			long li_busyJVM2 = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024; //
			returnVO.setServiceName(_serviceName); //服务接口名!
			returnVO.setServiceImplName(_implClassName); //真正的实现类名!
			returnVO.setReturnObject(novaEx); //
			returnVO.setDealtime(ll_2 - _beginTime); //耗时
			returnVO.setCallDBCount(li_allStmtCount); //访问DB次数!
			returnVO.setJVM1(li_busyJVM1); //
			returnVO.setJVM2(li_busyJVM2); //
			return returnVO;
		} finally { //关闭连接释放资源!!
			try {
				//long ll_xch_1 = System.currentTimeMillis(); //
				if (!ServerEnvironment.isLoadRunderCall) { //如果不是LoadRunder在调用才进行监控处理! 即当LoadRunder进行压力测试时,干脆直接关闭掉这个处理!!!
					long ll_2 = System.currentTimeMillis(); //
					long ll_jvmused = returnVO.getJVM2() - returnVO.getJVM1(); //
					ArrayList al_allGetDataSQLs = (ArrayList) initContext.getCurrSessionCustInfoByKey("取数大小"); //将当前会话中的取数大小得到
					long ll_dataSize = 0; //
					String str_sqls = null;
					if (al_allGetDataSQLs != null) { //如果有值!!!
						StringBuffer sb_sqls = new StringBuffer(); //
						for (int i = 0; i < al_allGetDataSQLs.size(); i++) { //
							String[] str_allGetDataSQLs = (String[]) al_allGetDataSQLs.get(i); //
							long ll_itemSize = Long.parseLong(str_allGetDataSQLs[1]); //
							ll_dataSize = ll_dataSize + ll_itemSize; //累加!!!
							//if (ll_itemSize > 10240) { //该条SQL语句超过10K,则将SQL记下来!
							sb_sqls.append(str_allGetDataSQLs[0] + "[" + ll_itemSize + "]§"); //分隔
							//}
						}
						str_sqls = sb_sqls.toString(); //
						if (str_sqls.length() > 3700) { //以后要使用中文方式判断!!!
							str_sqls = str_sqls.substring(0, 3700); //
						}
					}
					if ((ll_2 - _beginTime) < WLTConstants.THREAD_OVERTIME_VALUE && ll_dataSize < WLTConstants.THREAD_OVERWEIGHT_VALUE && ll_jvmused < WLTConstants.THREAD_OVERJVM_VALUE) { //如果在5秒内的,并且大小在300K以内的,且JVM内存耗用在5M以内,则删除之(因为不存在性能问题),否则永远留在那!!然后由另一个心跳线程去取之!!!
						if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {//如果监控交易成功率就不删除由心跳线程删掉
							String[] str_value = (String[]) revokeThreadHashtable.get(str_keyId); //////
							str_value[2] = "Y"; //标记已结束
							str_value[7] = "" + (ll_2 - _beginTime); //时长,超时的,不删除!!而是让心跳线程来删除它!!!反之,如果不是以[超时了]结束的记录,则说明是从来没返回过的,那更严重了!!!说明是阻塞的了!!
							str_value[8] = "" + ll_dataSize; //多少K
							str_value[9] = str_sqls; //
							str_value[11] = "" + returnVO.getJVM2(); //访问后的JVM,访问前的JVM在构造时已经设置了!!!
							revokeThreadHashtable.put(str_keyId, str_value); //再塞进去!!!!就是记录总共用了多少时间!!
						} else {
							revokeThreadHashtable.remove(str_keyId); //
						}
					} else { //如果超过10秒,或超重,则记录下来!!
						String[] str_value = (String[]) revokeThreadHashtable.get(str_keyId); //////
						str_value[2] = "Y"; //标记已结束
						str_value[7] = "" + (ll_2 - _beginTime); //时长,超时的,不删除!!而是让心跳线程来删除它!!!反之,如果不是以[超时了]结束的记录,则说明是从来没返回过的,那更严重了!!!说明是阻塞的了!!
						str_value[8] = "" + ll_dataSize; //多少K
						str_value[9] = str_sqls; //
						str_value[11] = "" + returnVO.getJVM2(); //访问后的JVM,访问前的JVM在构造时已经设置了!!!
						revokeThreadHashtable.put(str_keyId, str_value); //再塞进去!!!!就是记录总共用了多少时间!!
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
				//System.out.println("处理超时超重线程耗时[" + (ll_xch_2 - ll_xch_1) + "]"); //经过检测,并不耗时,即不是瓶颈!!
			} catch (Throwable th) {
				th.printStackTrace();
				if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
					String str_message = th.getMessage();
					WLTRemoteException novaEx = new WLTRemoteException(str_message);
					novaEx.setServerTargetEx(th);
					addIsSucessInf(str_keyId, "N", novaEx);
				}
			}
			closeConn(initContext); //回滚该次会话的所有连接!!!非常重要一定要执行!! 必须保证!!如果不关闭,则可能会使系统处于阻塞状态,然后客户端会产生read time out异常!!!
			releaseContext(initContext); //清除当前会话中的内容
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

	//直接输出一个异常信息,比如系统正忙,内存接近临界值!
	private void returnExceptionMsg(String _message, ServletResponse _response, long ll_1, boolean _isExit) throws Exception {
		RemoteCallReturnVO returnVO = new RemoteCallReturnVO(); //
		Exception exx = new Exception(_message); //
		WLTRemoteFatalException remoteEX = new WLTRemoteFatalException(exx.getMessage(), _isExit); //版本异常!!!
		remoteEX.setServerTargetEx(exx);
		returnVO.setReturnObject(remoteEX); //
		returnVO.setDealtime(System.currentTimeMillis() - ll_1);
		returnVO.setCallDBCount(-1);
		outPutToclient(_response, returnVO); //输出至客户端..
	}

	/**
	 * 提交所有事务
	 * @param _initContext
	 */
	private int commitTrans(WLTInitContext _initContext) {
		//System.out.println("提交该次远程访问所有事务!"); //
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
		//System.out.println("回滚该次远程访问所有事务!"); //
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
		//System.out.println("关闭该次远程访问所有事务!"); //
		try {
			if (_initContext.isGetConn()) {
				WLTDBConnection[] conns = _initContext.GetAllConns();
				for (int i = 0; i < conns.length; i++) {
					try {
						conns[i].close(); //关闭指定数据源连接
						//System.out.println("关闭当前远程访问用到的数据库连接[" + conns[i].getDsName() + "]");
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
		//System.out.println("释放该次远程访问所有资源!"); //
		try {
			_initContext.release(); //释放所有资源!!
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 从一个输入流取得二进制数组
	 * @param _inputstream
	 * @return
	 */
	private CompressBytesVO getByteFromInputStreamByMark(InputStream _inputstream) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream(); //创建输入流!!!
			byte[] buf = new byte[2048]; //5K,要大!!否则会报错,以前是1024,而且是先取第一位,再取后面的!在LoadRunder压力测试时老是报解压失败(数据格式不对)!在招行项目中也经常发生这个问题! 后来改成现在的这个样子兵力就不报错了!说明效果很明显!!
			int len = -1;
			while ((len = _inputstream.read(buf)) != -1) {
				baos.write(buf, 0, len); //
			}
			_inputstream.close(); //关闭输入流!快速释放连接!!
			_inputstream = null; //
			byte[] byteCodes = baos.toByteArray(); //第一位是标记位!如果是0,则表示是不压缩,如果是1则表示是压缩!!!
			byte firstMark = byteCodes[0]; //
			byte[] realByteCodes = new byte[byteCodes.length - 1]; //
			System.arraycopy(byteCodes, 1, realByteCodes, 0, realByteCodes.length); //拷贝一下!Java应该保证这个性能的! 否则不会放在System对象中!而且入参一般比较小!
			return new CompressBytesVO((firstMark == 0 ? false : true), realByteCodes); //如果第一位是0,则表示没压缩!!!,如果是1则表示是压缩的!
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
	 * 压缩!!
	 * @param _initbytes
	 * @return
	 */
	public CompressBytesVO compressBytes(byte[] _initbytes) {
		return getBSUtil().compressBytes(_initbytes); //
	}

	/**
	 * 解压某个字节数组,当参数本身就是一个压缩比非常高的文件时就会造成无穷次的压缩,甚至是死循环!!比如一个rar文件,所以会发生有时一些rar文件无法上传或下载的情况!!
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
		if (!revokeThreadHashtable.containsKey(_prefix)) { //如果没有该key则直接返回!!
			return _prefix;
		}

		int li_count = 1;
		while (1 == 1) {
			String str_newid = _prefix + "_" + li_count; //
			if (!revokeThreadHashtable.containsKey(str_newid)) { //如果没有该key则直接返回!!
				return str_newid;
			}
			li_count++; //否则加1,然后继续死循环直至找到一个新的键值!!
		}
	}

}
