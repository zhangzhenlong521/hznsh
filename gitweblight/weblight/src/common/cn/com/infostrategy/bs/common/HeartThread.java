package cn.com.infostrategy.bs.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;

/**
 * 心跳程序!!
 * 不断的将阻塞的线程或运算时间超过20秒的线程送到数据库中!!!!
 * 如果是阻塞的且超过30秒的,则送入数据库,且不从缓存中删除!!!!
 * 如果是有返回的,但超过20秒的!!则送入数据库,且从缓存中删除!!!!
 * 每天主表大概会插入5千条记录!!!!子表可能会多10倍
 * 交易成功率会记录当天的所有操作记录（10分钟左右之内），
 * 需要在weblight.xml中配置ISMONITORREMOTECALLRATE=Y 
 * sunfujun/20121208/邮储交易成功率
 * @author xch
 *
 */
public class HeartThread extends Thread {

	private CommDMO commDMO = null; //
	private long ll_cycle_count = 0;
	private String serverInsName = null;

	@Override
	public void run() {
		if ("Y".equalsIgnoreCase(ServerEnvironment.getProperty("HeartThreadStop"))) { //如果有参数指定心跳暂停,则直接返回!!
			return; //
		}
		if (ServerEnvironment.vc_alltables == null || ServerEnvironment.vc_alltables.size() <= 0 || !ServerEnvironment.vc_alltables.contains("PUB_SEQUENCE") || !ServerEnvironment.vc_alltables.contains("PUB_THREADMONITOR") || !ServerEnvironment.vc_alltables.contains("PUB_THREADMONITOR_B")) { //如果没有表或者没有这些表则退出,比如在首次安装时!!
			return;
		}

		WLTInitContext context = new WLTInitContext(); //
		while (1 == 1) {
			//System.out.println("心跳线程在跑...."); //
			System.gc(); //建议虚拟机释放内存.....
			if (ServerEnvironment.isLoadRunderCall) { //如果是在做压力测试,则根本不做!
				try {
					RemoteCallServlet.revokeThreadHashtable.clear(); //清空所有,保存内存不崩溃!
					clearCallCountCache(); //
					Thread.currentThread().sleep(10000); //每10秒跑一次!!
				} catch (Exception e) {
					e.printStackTrace(); //
				}
				continue;
			}

			try {
				long ll_currtime = System.currentTimeMillis(); //当前时间

				ArrayList al_sqls = new ArrayList();
				if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
					ll_cycle_count = ll_cycle_count + 1;
					if (ll_cycle_count >= 60) {//大约10分钟或可能更长时间执行一把删除操作，删除和监控无关记录，只保留10分钟前的，以防止记录太多
						al_sqls.add(" delete from pub_threadmonitor_b where begintime < '" + getTimeStr(ll_currtime - (10 * 60 * 1000)) + "' and iscallend='Y' and overtype is null  and jvmused<" + WLTConstants.THREAD_OVERJVM_VALUE);//删除10分钟之外的记录 全面监控的记录不删掉就是那些超重 由原来的逻辑删掉
						ll_cycle_count = 0;
					}
				}
				String str_currtime = getTimeStr(ll_currtime); //
				String str_currhour = getTimeStrAsHour(ll_currtime); //
				//				if (str_currhour.compareTo("08") >= 0 && str_currhour.compareTo("21") <= 0) { //只有在08:00-21:59点之间才做这样的逻辑!!! 
				if ((str_currhour.compareTo("08") >= 0 && str_currhour.compareTo("21") <= 0) || !RemoteCallServlet.revokeThreadHashtable.isEmpty()) {//
					Runtime runTime = Runtime.getRuntime();//
					long ll_freeJVM = runTime.freeMemory() / (1024 * 1024); //空闲的
					long ll_totalJVM = runTime.totalMemory() / (1024 * 1024); //总共的
					long ll_busyJVM = ll_totalJVM - ll_freeJVM; ////忙的!!!

					if (ll_totalJVM > ServerEnvironment.EVER_MAX_TOTALMEMORY) { //如果最大内存数超过曾过最大内存数的变量,则更新之!!!
						ServerEnvironment.EVER_MAX_TOTALMEMORY = ll_totalJVM; //
					}

					//在线用户..
					int[] li_onLineUsers = getOnlineUser(); //在线用户,活动/全部

					//主要线程并发量
					long ll_syn_currthreads = cn.com.infostrategy.wlappletloader.bs.SynchronizerServlet.THREADCOUNT; //同步的当前线程数!
					long ll_syn_maxthreads = cn.com.infostrategy.wlappletloader.bs.SynchronizerServlet.MAXTHREADCOUNT; //同步的最大线程数!
					long ll_syn_totalthreads = cn.com.infostrategy.wlappletloader.bs.SynchronizerServlet.TOTALTHREADCOUNT; //同步线程的总计!

					long ll_call_currthreads = cn.com.infostrategy.bs.common.RemoteCallServlet.THREADCOUNT; //当前线程数!!
					long ll_call_maxthreads = cn.com.infostrategy.bs.common.RemoteCallServlet.MAXTHREADCOUNT; //最大线程数!!
					long ll_call_totalthreads = cn.com.infostrategy.bs.common.RemoteCallServlet.TOTALTHREADCOUNT; //所有线程数

					int[] li_dsPoolActives = getDefaultDsActives(); //

					Hashtable htThreadCache = RemoteCallServlet.revokeThreadHashtable; //缓存!!!!!!
					String[] str_allkeys = (String[]) htThreadCache.keySet().toArray(new String[0]); //取得所有数据!!!!
					Arrays.sort(str_allkeys); //排下序!!!
					int li_blockcount = 0; //
					int li_defercount = 0; //
					int li_overweight = 0; //超重的,即取数的大小太大!!!
					int li_overjvm = 0; //虚拟机内存消耗过大的
					ArrayList al_childDatas = new ArrayList(); //子表数据的列表!! 
					for (int i = 0; i < str_allkeys.length; i++) { //遍历所有值!
						String[] str_values = (String[]) htThreadCache.get(str_allkeys[i]); ////
						if (str_values[2].equals("N")) { //如果还没有反应!!!
							long ll_beginTime = Long.parseLong(str_values[1]); //
							if ((ll_currtime - ll_beginTime) > WLTConstants.THREAD_OVERTIME_VALUE) { //如果超过5秒还没反应的!!则要塞入数据库,但不从缓存中清除!
								li_blockcount++; //超时未反应!!!
								al_childDatas.add(str_values); //加入,但不从缓存中删除!!!
							} else { //如果虽然没反应,但并没有超过10秒,则不处理,即这可能是正常的逻辑!!!
							}
						} else { //如果已有了反馈!则送入数据库,且从缓存中删除
							if (str_values[7] != null && Integer.parseInt(str_values[7]) > WLTConstants.THREAD_OVERTIME_VALUE) { //如果是超时
								li_defercount++;
							}
							if (str_values[8] != null && Integer.parseInt(str_values[8]) > WLTConstants.THREAD_OVERWEIGHT_VALUE) { //如果是超重
								li_overweight++;
							}

							if (str_values[11] != null && (Long.parseLong(str_values[11]) - Long.parseLong(str_values[10])) > WLTConstants.THREAD_OVERJVM_VALUE) { //如果虚拟机开销超过5M
								li_overjvm++; //
							}

							al_childDatas.add(str_values); //
							RemoteCallServlet.revokeThreadHashtable.remove(str_allkeys[i]); //从缓存中删除!!!
						}
					}

					//系统监控主表,子表
					//主表包括:监控时间/当前并发数/最大并发数/超时未反应数/超时反应数
					//子表包括:监控时间/类型(超时未反应,超时反应)/耗时/调用者/调用时间/服务名/方法名/参数值
					String str_pkValue = getCommDMO().getSequenceNextValByDS(null, "S_PUB_THREADMONITOR", false); //主表主键值!!!
					if (str_currhour.equals("08") || str_currhour.equals("21")) { //只在早晨与晚上删除以前数据,以提高性能!
						long ll_beforeTwoDays = ll_currtime - (2 * 24 * 60 * 60 * 1000); //减去两天!
						String str_beforeTwoDays = getTimeStrAsDay(ll_beforeTwoDays); //到秒的时间!
						al_sqls.add("delete from pub_threadmonitor   where monitortime<'" + str_beforeTwoDays + " 00:00:00'"); //先删除2天的数据,以防止数据太多,影响性能,即实际上只需要存储3天之内的数据即可,即大概在1万条左右!!!
						al_sqls.add("delete from pub_threadmonitor_b where monitortime<'" + str_beforeTwoDays + " 00:00:00'"); //
					}
					al_sqls.add(getSQL_Parent(str_pkValue, str_currtime, ll_busyJVM, ll_freeJVM, ll_totalJVM, li_onLineUsers, ll_syn_totalthreads, ll_syn_currthreads, ll_syn_maxthreads, ll_call_totalthreads, ll_call_currthreads, ll_call_maxthreads, li_blockcount, li_defercount, li_overweight,
							li_overjvm, li_dsPoolActives)); //主表的记录
					getAllChildSqls(str_pkValue, ll_currtime, str_currtime, al_sqls, al_childDatas);
				}
				if (al_sqls.size() > 0) {
					getCommDMO().executeBatchByDS(null, al_sqls, false); //插入数据库!,不输日志,否则控制台会不停的滚动,导致开发环境无法看日志!!!以后可能要改成,如果是开发环境则不输出,但如果是运行环境则要输出!!!
				}
			} catch (Exception ex) { //
				context.rollbackAllTrans(); //回滚了!!!
				context.closeAllConn(); //关闭所有连接!
				ex.printStackTrace(); //
				RemoteCallServlet.revokeThreadHashtable.clear();//这样合理不出现异常就清空一下内存
				//				Thread.currentThread().sleep(600000);//出现异常就停十分钟，而不退出以防止异常打印大量日志 停10分钟有可能内存崩溃
			} finally {
				context.commitAllTrans(); //
				context.closeAllConn(); //关闭所有连接!!!
				if (RemoteCallServlet.revokeThreadHashtable.size() > 30) { //如果刚刚取完并处理后,发现立即又有了30个,说明进水太快了,为了保证内存不崩溃,立即清空所有算了! 因为在招行现场做压力测试时就遇到过这个问题,即疯狂并发时,进水速度大大高于出水速度,结果系统跑5小时后就死了!!!!
					RemoteCallServlet.revokeThreadHashtable.clear(); //清空所有,保存内存不崩溃!
				}
				try {
					clearCallCountCache(); //
					Thread.currentThread().sleep(10000); //每10秒跑一次!!
				} catch (Exception e) {
					e.printStackTrace(); //
				}
				System.gc(); //建议虚拟机释放内存.....
			}

		} //end while...
	}

	private void getAllChildSqls(String str_pkValue, long ll_currtime, String str_currtime, List al_sqls, List al_childDatas) throws Exception {
		if (al_childDatas != null && al_childDatas.size() > 0) {//减少数据库访问量
			HashMap<String, HashMap<String, Integer[]>> param = new HashMap<String, HashMap<String, Integer[]>>();//日期_一级模块名->（小时->[成功数,失败数]）
			String[] onenote = null;
			String daystr = null;//日期
			String hourstr = null;//小时
			String modelname = null;//一级模块名
			String currKey = null;//时期_一级模块名
			HashMap<String, Integer[]> currMap = null;
			for (int i = 0; i < al_childDatas.size(); i++) { //遍历子表的数据!!!
				al_sqls.add(getSQL_Child(str_pkValue, ll_currtime, str_currtime, (String[]) al_childDatas.get(i))); //子表的记录!!!!
				if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
					onenote = (String[]) al_childDatas.get(i);
					if (onenote[2].equals("N")) {//如果还没反馈就不算
						continue;
					}
					daystr = getTimeStrAsDay(Long.parseLong(onenote[1]));
					hourstr = getTimeStrAsHour(Long.parseLong(onenote[1]));
					modelname = onenote[12] == null ? "首页" : (onenote[12].split(" → ")[0]);
					if (onenote[4].equals("cn.com.infostrategy.bs.sysapp.login.SysAppServiceImpl") && onenote[5].equals("loginOneOff")) {
						modelname = "登陆";
					}
					currKey = daystr + "_" + modelname;
					if (serverInsName == null) {
						serverInsName = getServerInsName(onenote[16]);
					}
					if ("Y".equals(onenote[14])) {//如果成功了
						if (param.containsKey(currKey)) {
							currMap = param.get(currKey);
							if (currMap.containsKey(hourstr)) {
								currMap.put(hourstr, new Integer[] { currMap.get(hourstr)[0] + 1, currMap.get(hourstr)[1] });
							} else {
								currMap.put(hourstr, new Integer[] { 1, 0 });
							}
							param.put(currKey, currMap);
						} else {
							currMap = new HashMap<String, Integer[]>();
							currMap.put(hourstr, new Integer[] { 1, 0 });
							param.put(currKey, currMap);
						}
					} else {//否则失败
						if (param.containsKey(currKey)) {
							currMap = param.get(currKey);
							if (currMap.containsKey(hourstr)) {
								currMap.put(hourstr, new Integer[] { currMap.get(hourstr)[0], currMap.get(hourstr)[1] + 1 });
							} else {
								currMap.put(hourstr, new Integer[] { 0, 1 });
							}
							param.put(currKey, currMap);
						} else {
							currMap = new HashMap<String, Integer[]>();
							currMap.put(hourstr, new Integer[] { 0, 1 });
							param.put(currKey, currMap);
						}
					}
				}
			}

			if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
				if (param.size() > 0) {//如果有记录 每天个一级模块一条交易成功率记录
					String[] keys = (String[]) param.keySet().toArray(new String[0]);
					HashMap<String, Integer[]> currMap_ = null;
					String coo = null;//0->00
					for (int o = 0; o < param.size(); o++) {
						HashVO[] tdrateinf = getCommDMO().getHashVoArrayByDS(null, " select * from pub_remotecallsucrate where serverinsname='" + serverInsName + "' and daystr='" + keys[o].split("_")[0] + "' and monitortype='" + keys[o].split("_")[1] + "' ");
						if (tdrateinf == null || tdrateinf.length <= 0) {
							InsertSQLBuilder isb = new InsertSQLBuilder();
							isb.setTableName("pub_remotecallsucrate");
							isb.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "s_pub_remotecallsucrate"));
							isb.putFieldValue("serverinsname", serverInsName);
							isb.putFieldValue("daystr", keys[o].split("_")[0]);
							isb.putFieldValue("monitortype", keys[o].split("_")[1]);
							currMap_ = param.get(keys[o]);
							for (int oo = 0; oo < 24; oo++) {
								coo = (oo < 10 ? "0" + oo : oo + "");
								if (currMap_ != null && currMap_.containsKey(coo + "")) {
									isb.putFieldValue("sc" + coo, currMap_.get(coo)[0]);
									isb.putFieldValue("fc" + coo, currMap_.get(coo)[1]);
								} else {
									isb.putFieldValue("sc" + coo, "0");
									isb.putFieldValue("fc" + coo, "0");
								}
							}
							al_sqls.add(isb.getSQL());
						} else {
							UpdateSQLBuilder usb = new UpdateSQLBuilder();
							usb.setTableName("pub_remotecallsucrate");
							usb.setWhereCondition("id=" + tdrateinf[0].getStringValue("id"));
							usb.putFieldValue("serverinsname", serverInsName);
							usb.putFieldValue("daystr", keys[o].split("_")[0]);
							usb.putFieldValue("monitortype", keys[o].split("_")[1]);
							currMap_ = param.get(keys[o]);
							for (int oo = 0; oo < 24; oo++) {
								coo = (oo < 10 ? "0" + oo : oo + "");
								if (currMap_ != null && currMap_.containsKey(coo + "")) {
									usb.putFieldValue("sc" + coo, tdrateinf[0].getIntegerValue("sc" + coo, 0) + currMap_.get(coo)[0]);
									usb.putFieldValue("fc" + coo, tdrateinf[0].getIntegerValue("fc" + coo, 0) + currMap_.get(coo)[1]);
								}
							}
							al_sqls.add(usb.getSQL());
						}
					}
				}
			}
		}
	}

	//清空30秒之前的所有数据!!!
	private void clearCallCountCache() {
		try {
			long ll_currtime = System.currentTimeMillis(); //
			long ll_beforeTime = ll_currtime - 25000; //25秒之前!
			List vc_calltimes = RemoteCallServlet.callThreadTimeList; //调用的时间
			Long[] ll_allItems = (Long[]) vc_calltimes.toArray(new Long[0]); //必须一下子取到!
			int li_count = 0; //
			for (int i = 0; i < ll_allItems.length; i++) {
				long ll_item = ll_allItems[i]; //时间!!!!
				if (ll_item < ll_beforeTime) { //如果比之小!
					li_count++; //加上!
				} else {
					break; //终止!!
				}
			}

			for (int i = 0; i < li_count; i++) {
				vc_calltimes.remove(0); //循环删除干净!!!因为有锁,可能导致其他线程等待!!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//取得在线用户人数!!!
	private int[] getOnlineUser() {
		String str_currtime = new TBUtil().getCurrTime(); // 当前时间
		HashMap mapUser = ServerEnvironment.getLoginUserMap(); //
		String[] str_sesions = (String[]) mapUser.keySet().toArray(new String[0]); // 当前在线客户端的清单!!!
		if (str_sesions == null) {
			return new int[] { 0, 0 };
		}
		int li_fadaiCount = 0;
		for (int i = 0; i < str_sesions.length; i++) {
			String[] str_onlineusers = (String[]) mapUser.get(str_sesions[i]); // 某一个用户的详细时间
			long ll_time_fadai = betweenTwoTimeSecond(str_onlineusers[5], str_currtime); // 发呆时间
			if (ll_time_fadai > 15 * 60) { //如果发呆时间超过15分钟,则记下来
				li_fadaiCount++; //
			}
		}
		return new int[] { str_sesions.length, str_sesions.length - li_fadaiCount }; //活动/全部
	}

	private long betweenTwoTimeSecond(String _date1, String _date2) {
		if (_date1 == null || _date2 == null || _date1.trim().equals("") || _date2.trim().equals("")) {
			return 0;
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date date1 = sdf.parse(_date1);
			java.util.Date date2 = sdf.parse(_date2);

			long li_second = (date2.getTime() - date1.getTime()) / 1000; // 秒
			return li_second; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return 0;
		}
	}

	/**
	 * 取得数据源连接数据情况!!!
	 * @return
	 */
	private int[] getDefaultDsActives() {
		try {
			String str_dbcp = "org.apache.commons.dbcp.PoolingDriver"; //DBCP驱动!!
			List drivers = Collections.list(DriverManager.getDrivers()); //列出所有驱动!
			boolean isFindDbcp = false; //
			for (int i = 0; i < drivers.size(); i++) { //遍历,似乎默认系统就已加载了三个驱动[sun.jdbc.odbc.JdbcOdbcDriver][com.mysql.jdbc.Driver][oracle.jdbc.driver.OracleDriver]
				Driver driver = (Driver) drivers.get(i); //
				String clsname = driver.getClass().getName(); //
				if (clsname.equals(str_dbcp)) { //
					isFindDbcp = true; //找到了!!
					break; //
				}
			}
			if (!isFindDbcp) { //如果还没加载,则加载之!!因为在中外运项目中,数据源使用的是WebLogic的数据源,则总是报一个错!!原因就是在BootServlet中没有加载DBCP驱动!!!
				Class.forName(str_dbcp); // 创建dbcp池驱动
			}
			PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
			GenericObjectPool pool = (GenericObjectPool) driver.getConnectionPool(ServerEnvironment.getDefaultDataSourceName()); //取得默认数据源!!
			int li_busys = pool.getNumActive(); //目前正忙的
			int li_frees = pool.getNumIdle(); //目前空闲的
			int li_total = li_busys + li_frees; //目前池中最大数!
			return new int[] { li_busys, li_total, WLTDBConnection.MAX_ACTIVES, WLTDBConnection.MAX_INSTANCES };
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return new int[] { -999, -999, WLTDBConnection.MAX_ACTIVES, WLTDBConnection.MAX_INSTANCES };
		}
	}

	/**
	 * 创建主表的SQL..
	 * @param _id
	 * @param _monitorTime
	 * @param _syncthreadcurrs
	 * @param _syncthreadmaxs
	 * @param _callthreadcurrs
	 * @param _callthreadmaxs
	 * @return
	 */
	private String getSQL_Parent(String _id, String _monitorTime, long _busyJVM, long _freeJVM, long _totalJVM, int[] _onLineUsers, long _syn_totalthreads, long _syncthreadcurrs, long _syncthreadmaxs, long _callTotalThreads, long _callthreadcurrs, long _callthreadmaxs, int _blockcount,
			int _defercount, int _overweights, int _overjvms, int[] _dsPoolActives) {
		InsertSQLBuilder isql_1 = new InsertSQLBuilder("pub_threadmonitor"); //线程监控
		isql_1.putFieldValue("id", _id); //
		isql_1.putFieldValue("clusterport", ServerEnvironment.getProperty("MYSELFPORT")); //集群端口号!!!在没有发生客户端访问时是为空的,一旦发生访问就知道了!!,在LoginServlet的一开始做了注册!!
		isql_1.putFieldValue("monitortime", _monitorTime); //监控时间
		isql_1.putFieldValue("serverstarttime", ServerEnvironment.getProperty("SERVERREALSTARTTIME")); //实际启动时间
		isql_1.putFieldValue("jvmbusy", _busyJVM); //忙的虚拟机
		isql_1.putFieldValue("jvmfree", _freeJVM); //空闲的虚拟机
		isql_1.putFieldValue("jvmtotal", _totalJVM); //总共的虚拟机
		isql_1.putFieldValue("evermaxjvmtotal", ServerEnvironment.EVER_MAX_TOTALMEMORY); //曾经最大内存数!!!
		isql_1.putFieldValue("onlineuserbusys", _onLineUsers[1]); //在线用户活动数
		isql_1.putFieldValue("onlineusertotals", _onLineUsers[0]); //在线用户全部
		isql_1.putFieldValue("evermaxonlineusers", ServerEnvironment.EVER_MAX_ONLINEUSERS); //曾经在线用户的最大数!!!
		isql_1.putFieldValue("syncthreadtotals", _syn_totalthreads); //同步线程的总计!!
		isql_1.putFieldValue("syncthreadcurrs", _syncthreadcurrs); //同步线程的当前并发数!!
		isql_1.putFieldValue("syncthreadmaxs", _syncthreadmaxs); //同步线程的最大数
		isql_1.putFieldValue("callthreadtotals", _callTotalThreads); //访问线程的总计数!!
		isql_1.putFieldValue("callthreadcurrs", _callthreadcurrs); //同步线程的当前并发数
		isql_1.putFieldValue("callthreadmaxs", _callthreadmaxs); //同步线程的最大数
		isql_1.putFieldValue("blockthreads", "" + _blockcount); //阻塞的线程数
		isql_1.putFieldValue("deferthreads", "" + _defercount); //延期的线程数,即有反应了,但超过了10秒钟,这些方法都是存在性能隐患的!!!!
		isql_1.putFieldValue("weighterthreads", "" + _overweights); //超重的线程数
		isql_1.putFieldValue("jvmoverthreads", "" + _overjvms); //超重的线程数
		isql_1.putFieldValue("dspoolbusy", _dsPoolActives[0]); //连接池中正忙的
		isql_1.putFieldValue("dspooltotal", _dsPoolActives[1]); //连接池中所有的
		isql_1.putFieldValue("dspoolmaxbusy", _dsPoolActives[2]); //连接池中曾经最大忙碌数!!
		isql_1.putFieldValue("dspoolmaxtotal", _dsPoolActives[3]); //连接池中曾经最大实例数!!
		return isql_1.getSQL(); //
	}

	/**
	 * 子表的SQL语句!!!!
	 * @param _parentid
	 * @param _monitorTime
	 * @param _callPars
	 * @return
	 */
	private String getSQL_Child(String _parentid, long _llmonitortime, String _monitorTime, String[] _callPars) throws Exception {
		InsertSQLBuilder isql_1 = new InsertSQLBuilder("pub_threadmonitor_b"); //线程监控
		isql_1.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_THREADMONITOR_B", false)); //主键
		isql_1.putFieldValue("threadmonitor_id", _parentid); //
		isql_1.putFieldValue("monitortime", _monitorTime); //监控时间
		isql_1.putFieldValue("threadno", _callPars[0]); //线程编号
		isql_1.putFieldValue("begintime", getTimeStr(Long.parseLong(_callPars[1]))); //开始时间
		isql_1.putFieldValue("iscallend", _callPars[2]); //开始时间
		isql_1.putFieldValue("calluser", _callPars[3]); //访问人
		isql_1.putFieldValue("servicename", _callPars[4]); //服务名!
		isql_1.putFieldValue("methodname", _callPars[5]); //方法名!
		isql_1.putFieldValue("parameters", _callPars[6]); //方法名!

		long ll_delay = 0;
		if (_callPars[7] == null) { //如果为空,则拿当前时间减去开始时间
			ll_delay = _llmonitortime - Long.parseLong(_callPars[1]); //所耗时间
		} else {
			ll_delay = Long.parseLong(_callPars[7]); //
		}
		isql_1.putFieldValue("usedsecond", "" + ll_delay); //所耗时长!!!
		isql_1.putFieldValue("weightersize", _callPars[8]); //超重的大小
		isql_1.putFieldValue("weightersqls", _callPars[9]); //超重的SQL

		isql_1.putFieldValue("jvm1", _callPars[10]); //调用前的虚拟机内存
		isql_1.putFieldValue("jvm2", _callPars[11]); //调用后的虚拟机内存
		long ll_jvmused = 0; //
		if (_callPars[11] != null) {
			ll_jvmused = Long.parseLong(_callPars[11]) - Long.parseLong(_callPars[10]); //
			isql_1.putFieldValue("jvmused", ll_jvmused); //调用的菜单名!!!
		}

		if (_callPars[2] != null && _callPars[2].equals("Y")) {
			String str_overtype = "";//
			if (ll_delay > WLTConstants.THREAD_OVERTIME_VALUE) {
				str_overtype = str_overtype + "超时"; //
			}

			if ((_callPars[8] != null && Integer.parseInt(_callPars[8]) > WLTConstants.THREAD_OVERWEIGHT_VALUE) || (_callPars[11] != null && ll_jvmused > WLTConstants.THREAD_OVERJVM_VALUE)) { //如果是超重
				str_overtype = str_overtype + "超重"; //
			}
			isql_1.putFieldValue("overtype", str_overtype); //
		}

		isql_1.putFieldValue("callmenuname", _callPars[12]); //调用的菜单名!!!
		isql_1.putFieldValue("callstack", _callPars[13]); //调用的堆栈!!!
		//在子表中增加了2个字段 是否成功，异常信息
		if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
			isql_1.putFieldValue("issucess", _callPars[14]);
			if (_callPars[15] != null) {
				if (_callPars[15].length() > 1000) {
					isql_1.putFieldValue("errormsg", _callPars[15].substring(0, 1000));
				} else {
					isql_1.putFieldValue("errormsg", _callPars[15]);
				}
			}
			String modelname = null;
			
			if (_callPars[4].equals("cn.com.infostrategy.bs.sysapp.login.SysAppServiceImpl") && _callPars[5].equals("loginOneOff")) {
				modelname = "登陆";
			} else {
				modelname = _callPars[12] == null ? "首页" : (_callPars[12].split(" → ")[0]);
				if(modelname.indexOf(".") >= 0) {
					modelname = modelname.split("\\.")[0];
				}
			}
			isql_1.putFieldValue("monitortype", modelname);//一级模块名 只有登陆是取不到的
			if (serverInsName == null) {
				serverInsName = getServerInsName(_callPars[16]);
			}
			isql_1.putFieldValue("serverinsname", serverInsName);//服务器实例名IP:PORT
		}
		return isql_1.getSQL(); //
	}

	/**
	 * 送入一个url取得其际的文本
	 * @param _url
	 * @return
	 */
	public String getUrlRetrunText(String _url) {
		try {
			URLConnection conn = new URL(_url).openConnection();
			if (_url.startsWith("https")) {
				new BSUtil().addHttpsParam(conn);
			}
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			InputStream intStream = conn.getInputStream(); //
			BufferedReader in = new BufferedReader(new InputStreamReader(intStream));
			String str;
			StringBuffer sb_html = new StringBuffer();
			while ((str = in.readLine()) != null) {
				sb_html.append(str); //
			}
			in.close();
			return sb_html.toString(); //
		} catch (Exception ex) {
			System.err.println("监听心跳线程在取得自己端口时,请求[" + _url + "]时发生异常[" + ex.getMessage() + "]"); ///////
			//printStackTrace(); //
			return null; //
		}
	}

	private String getTimeStr(long _lltime) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(_lltime)); //
	}

	private String getTimeStrAsDay(long _lltime) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(_lltime)); //
	}

	private String getTimeStrAsHour(long _lltime) {
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(new Date(_lltime)); //
	}

	/**
	 * 获取当前实例名
	 * @param defaultName
	 * @return
	 */
	private String getServerInsName(String defaultName) {
		if (ServerEnvironment.getProperty("SERVERINSNAME") != null && !ServerEnvironment.getProperty("SERVERINSNAME").equals("")) {
			return ServerEnvironment.getProperty("SERVERINSNAME");
		}
		try {
			String str_ip = InetAddress.getLocalHost().getHostAddress();
			return str_ip + "_" + defaultName.split("_")[1];
		} catch (Exception ex) {
			return defaultName;
		}
	}

	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}
}
