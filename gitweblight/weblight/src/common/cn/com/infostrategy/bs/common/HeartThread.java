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
 * ��������!!
 * ���ϵĽ��������̻߳�����ʱ�䳬��20����߳��͵����ݿ���!!!!
 * ������������ҳ���30���,���������ݿ�,�Ҳ��ӻ�����ɾ��!!!!
 * ������з��ص�,������20���!!���������ݿ�,�Ҵӻ�����ɾ��!!!!
 * ÿ�������Ż����5ǧ����¼!!!!�ӱ���ܻ��10��
 * ���׳ɹ��ʻ��¼��������в�����¼��10��������֮�ڣ���
 * ��Ҫ��weblight.xml������ISMONITORREMOTECALLRATE=Y 
 * sunfujun/20121208/�ʴ����׳ɹ���
 * @author xch
 *
 */
public class HeartThread extends Thread {

	private CommDMO commDMO = null; //
	private long ll_cycle_count = 0;
	private String serverInsName = null;

	@Override
	public void run() {
		if ("Y".equalsIgnoreCase(ServerEnvironment.getProperty("HeartThreadStop"))) { //����в���ָ��������ͣ,��ֱ�ӷ���!!
			return; //
		}
		if (ServerEnvironment.vc_alltables == null || ServerEnvironment.vc_alltables.size() <= 0 || !ServerEnvironment.vc_alltables.contains("PUB_SEQUENCE") || !ServerEnvironment.vc_alltables.contains("PUB_THREADMONITOR") || !ServerEnvironment.vc_alltables.contains("PUB_THREADMONITOR_B")) { //���û�б����û����Щ�����˳�,�������״ΰ�װʱ!!
			return;
		}

		WLTInitContext context = new WLTInitContext(); //
		while (1 == 1) {
			//System.out.println("�����߳�����...."); //
			System.gc(); //����������ͷ��ڴ�.....
			if (ServerEnvironment.isLoadRunderCall) { //���������ѹ������,���������!
				try {
					RemoteCallServlet.revokeThreadHashtable.clear(); //�������,�����ڴ治����!
					clearCallCountCache(); //
					Thread.currentThread().sleep(10000); //ÿ10����һ��!!
				} catch (Exception e) {
					e.printStackTrace(); //
				}
				continue;
			}

			try {
				long ll_currtime = System.currentTimeMillis(); //��ǰʱ��

				ArrayList al_sqls = new ArrayList();
				if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
					ll_cycle_count = ll_cycle_count + 1;
					if (ll_cycle_count >= 60) {//��Լ10���ӻ���ܸ���ʱ��ִ��һ��ɾ��������ɾ���ͼ���޹ؼ�¼��ֻ����10����ǰ�ģ��Է�ֹ��¼̫��
						al_sqls.add(" delete from pub_threadmonitor_b where begintime < '" + getTimeStr(ll_currtime - (10 * 60 * 1000)) + "' and iscallend='Y' and overtype is null  and jvmused<" + WLTConstants.THREAD_OVERJVM_VALUE);//ɾ��10����֮��ļ�¼ ȫ���صļ�¼��ɾ��������Щ���� ��ԭ�����߼�ɾ��
						ll_cycle_count = 0;
					}
				}
				String str_currtime = getTimeStr(ll_currtime); //
				String str_currhour = getTimeStrAsHour(ll_currtime); //
				//				if (str_currhour.compareTo("08") >= 0 && str_currhour.compareTo("21") <= 0) { //ֻ����08:00-21:59��֮������������߼�!!! 
				if ((str_currhour.compareTo("08") >= 0 && str_currhour.compareTo("21") <= 0) || !RemoteCallServlet.revokeThreadHashtable.isEmpty()) {//
					Runtime runTime = Runtime.getRuntime();//
					long ll_freeJVM = runTime.freeMemory() / (1024 * 1024); //���е�
					long ll_totalJVM = runTime.totalMemory() / (1024 * 1024); //�ܹ���
					long ll_busyJVM = ll_totalJVM - ll_freeJVM; ////æ��!!!

					if (ll_totalJVM > ServerEnvironment.EVER_MAX_TOTALMEMORY) { //�������ڴ���������������ڴ����ı���,�����֮!!!
						ServerEnvironment.EVER_MAX_TOTALMEMORY = ll_totalJVM; //
					}

					//�����û�..
					int[] li_onLineUsers = getOnlineUser(); //�����û�,�/ȫ��

					//��Ҫ�̲߳�����
					long ll_syn_currthreads = cn.com.infostrategy.wlappletloader.bs.SynchronizerServlet.THREADCOUNT; //ͬ���ĵ�ǰ�߳���!
					long ll_syn_maxthreads = cn.com.infostrategy.wlappletloader.bs.SynchronizerServlet.MAXTHREADCOUNT; //ͬ��������߳���!
					long ll_syn_totalthreads = cn.com.infostrategy.wlappletloader.bs.SynchronizerServlet.TOTALTHREADCOUNT; //ͬ���̵߳��ܼ�!

					long ll_call_currthreads = cn.com.infostrategy.bs.common.RemoteCallServlet.THREADCOUNT; //��ǰ�߳���!!
					long ll_call_maxthreads = cn.com.infostrategy.bs.common.RemoteCallServlet.MAXTHREADCOUNT; //����߳���!!
					long ll_call_totalthreads = cn.com.infostrategy.bs.common.RemoteCallServlet.TOTALTHREADCOUNT; //�����߳���

					int[] li_dsPoolActives = getDefaultDsActives(); //

					Hashtable htThreadCache = RemoteCallServlet.revokeThreadHashtable; //����!!!!!!
					String[] str_allkeys = (String[]) htThreadCache.keySet().toArray(new String[0]); //ȡ����������!!!!
					Arrays.sort(str_allkeys); //������!!!
					int li_blockcount = 0; //
					int li_defercount = 0; //
					int li_overweight = 0; //���ص�,��ȡ���Ĵ�С̫��!!!
					int li_overjvm = 0; //������ڴ����Ĺ����
					ArrayList al_childDatas = new ArrayList(); //�ӱ����ݵ��б�!! 
					for (int i = 0; i < str_allkeys.length; i++) { //��������ֵ!
						String[] str_values = (String[]) htThreadCache.get(str_allkeys[i]); ////
						if (str_values[2].equals("N")) { //�����û�з�Ӧ!!!
							long ll_beginTime = Long.parseLong(str_values[1]); //
							if ((ll_currtime - ll_beginTime) > WLTConstants.THREAD_OVERTIME_VALUE) { //�������5�뻹û��Ӧ��!!��Ҫ�������ݿ�,�����ӻ��������!
								li_blockcount++; //��ʱδ��Ӧ!!!
								al_childDatas.add(str_values); //����,�����ӻ�����ɾ��!!!
							} else { //�����Ȼû��Ӧ,����û�г���10��,�򲻴���,����������������߼�!!!
							}
						} else { //��������˷���!���������ݿ�,�Ҵӻ�����ɾ��
							if (str_values[7] != null && Integer.parseInt(str_values[7]) > WLTConstants.THREAD_OVERTIME_VALUE) { //����ǳ�ʱ
								li_defercount++;
							}
							if (str_values[8] != null && Integer.parseInt(str_values[8]) > WLTConstants.THREAD_OVERWEIGHT_VALUE) { //����ǳ���
								li_overweight++;
							}

							if (str_values[11] != null && (Long.parseLong(str_values[11]) - Long.parseLong(str_values[10])) > WLTConstants.THREAD_OVERJVM_VALUE) { //����������������5M
								li_overjvm++; //
							}

							al_childDatas.add(str_values); //
							RemoteCallServlet.revokeThreadHashtable.remove(str_allkeys[i]); //�ӻ�����ɾ��!!!
						}
					}

					//ϵͳ�������,�ӱ�
					//�������:���ʱ��/��ǰ������/��󲢷���/��ʱδ��Ӧ��/��ʱ��Ӧ��
					//�ӱ����:���ʱ��/����(��ʱδ��Ӧ,��ʱ��Ӧ)/��ʱ/������/����ʱ��/������/������/����ֵ
					String str_pkValue = getCommDMO().getSequenceNextValByDS(null, "S_PUB_THREADMONITOR", false); //��������ֵ!!!
					if (str_currhour.equals("08") || str_currhour.equals("21")) { //ֻ���糿������ɾ����ǰ����,���������!
						long ll_beforeTwoDays = ll_currtime - (2 * 24 * 60 * 60 * 1000); //��ȥ����!
						String str_beforeTwoDays = getTimeStrAsDay(ll_beforeTwoDays); //�����ʱ��!
						al_sqls.add("delete from pub_threadmonitor   where monitortime<'" + str_beforeTwoDays + " 00:00:00'"); //��ɾ��2�������,�Է�ֹ����̫��,Ӱ������,��ʵ����ֻ��Ҫ�洢3��֮�ڵ����ݼ���,�������1��������!!!
						al_sqls.add("delete from pub_threadmonitor_b where monitortime<'" + str_beforeTwoDays + " 00:00:00'"); //
					}
					al_sqls.add(getSQL_Parent(str_pkValue, str_currtime, ll_busyJVM, ll_freeJVM, ll_totalJVM, li_onLineUsers, ll_syn_totalthreads, ll_syn_currthreads, ll_syn_maxthreads, ll_call_totalthreads, ll_call_currthreads, ll_call_maxthreads, li_blockcount, li_defercount, li_overweight,
							li_overjvm, li_dsPoolActives)); //����ļ�¼
					getAllChildSqls(str_pkValue, ll_currtime, str_currtime, al_sqls, al_childDatas);
				}
				if (al_sqls.size() > 0) {
					getCommDMO().executeBatchByDS(null, al_sqls, false); //�������ݿ�!,������־,�������̨�᲻ͣ�Ĺ���,���¿��������޷�����־!!!�Ժ����Ҫ�ĳ�,����ǿ������������,����������л�����Ҫ���!!!
				}
			} catch (Exception ex) { //
				context.rollbackAllTrans(); //�ع���!!!
				context.closeAllConn(); //�ر���������!
				ex.printStackTrace(); //
				RemoteCallServlet.revokeThreadHashtable.clear();//�������������쳣�����һ���ڴ�
				//				Thread.currentThread().sleep(600000);//�����쳣��ͣʮ���ӣ������˳��Է�ֹ�쳣��ӡ������־ ͣ10�����п����ڴ����
			} finally {
				context.commitAllTrans(); //
				context.closeAllConn(); //�ر���������!!!
				if (RemoteCallServlet.revokeThreadHashtable.size() > 30) { //����ո�ȡ�겢�����,��������������30��,˵����ˮ̫����,Ϊ�˱�֤�ڴ治����,���������������! ��Ϊ�������ֳ���ѹ������ʱ���������������,����񲢷�ʱ,��ˮ�ٶȴ����ڳ�ˮ�ٶ�,���ϵͳ��5Сʱ�������!!!!
					RemoteCallServlet.revokeThreadHashtable.clear(); //�������,�����ڴ治����!
				}
				try {
					clearCallCountCache(); //
					Thread.currentThread().sleep(10000); //ÿ10����һ��!!
				} catch (Exception e) {
					e.printStackTrace(); //
				}
				System.gc(); //����������ͷ��ڴ�.....
			}

		} //end while...
	}

	private void getAllChildSqls(String str_pkValue, long ll_currtime, String str_currtime, List al_sqls, List al_childDatas) throws Exception {
		if (al_childDatas != null && al_childDatas.size() > 0) {//�������ݿ������
			HashMap<String, HashMap<String, Integer[]>> param = new HashMap<String, HashMap<String, Integer[]>>();//����_һ��ģ����->��Сʱ->[�ɹ���,ʧ����]��
			String[] onenote = null;
			String daystr = null;//����
			String hourstr = null;//Сʱ
			String modelname = null;//һ��ģ����
			String currKey = null;//ʱ��_һ��ģ����
			HashMap<String, Integer[]> currMap = null;
			for (int i = 0; i < al_childDatas.size(); i++) { //�����ӱ������!!!
				al_sqls.add(getSQL_Child(str_pkValue, ll_currtime, str_currtime, (String[]) al_childDatas.get(i))); //�ӱ�ļ�¼!!!!
				if (ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE") != null && ServerEnvironment.getProperty("ISMONITORREMOTECALLRATE").equals("Y")) {
					onenote = (String[]) al_childDatas.get(i);
					if (onenote[2].equals("N")) {//�����û�����Ͳ���
						continue;
					}
					daystr = getTimeStrAsDay(Long.parseLong(onenote[1]));
					hourstr = getTimeStrAsHour(Long.parseLong(onenote[1]));
					modelname = onenote[12] == null ? "��ҳ" : (onenote[12].split(" �� ")[0]);
					if (onenote[4].equals("cn.com.infostrategy.bs.sysapp.login.SysAppServiceImpl") && onenote[5].equals("loginOneOff")) {
						modelname = "��½";
					}
					currKey = daystr + "_" + modelname;
					if (serverInsName == null) {
						serverInsName = getServerInsName(onenote[16]);
					}
					if ("Y".equals(onenote[14])) {//����ɹ���
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
					} else {//����ʧ��
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
				if (param.size() > 0) {//����м�¼ ÿ���һ��ģ��һ�����׳ɹ��ʼ�¼
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

	//���30��֮ǰ����������!!!
	private void clearCallCountCache() {
		try {
			long ll_currtime = System.currentTimeMillis(); //
			long ll_beforeTime = ll_currtime - 25000; //25��֮ǰ!
			List vc_calltimes = RemoteCallServlet.callThreadTimeList; //���õ�ʱ��
			Long[] ll_allItems = (Long[]) vc_calltimes.toArray(new Long[0]); //����һ����ȡ��!
			int li_count = 0; //
			for (int i = 0; i < ll_allItems.length; i++) {
				long ll_item = ll_allItems[i]; //ʱ��!!!!
				if (ll_item < ll_beforeTime) { //�����֮С!
					li_count++; //����!
				} else {
					break; //��ֹ!!
				}
			}

			for (int i = 0; i < li_count; i++) {
				vc_calltimes.remove(0); //ѭ��ɾ���ɾ�!!!��Ϊ����,���ܵ��������̵߳ȴ�!!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//ȡ�������û�����!!!
	private int[] getOnlineUser() {
		String str_currtime = new TBUtil().getCurrTime(); // ��ǰʱ��
		HashMap mapUser = ServerEnvironment.getLoginUserMap(); //
		String[] str_sesions = (String[]) mapUser.keySet().toArray(new String[0]); // ��ǰ���߿ͻ��˵��嵥!!!
		if (str_sesions == null) {
			return new int[] { 0, 0 };
		}
		int li_fadaiCount = 0;
		for (int i = 0; i < str_sesions.length; i++) {
			String[] str_onlineusers = (String[]) mapUser.get(str_sesions[i]); // ĳһ���û�����ϸʱ��
			long ll_time_fadai = betweenTwoTimeSecond(str_onlineusers[5], str_currtime); // ����ʱ��
			if (ll_time_fadai > 15 * 60) { //�������ʱ�䳬��15����,�������
				li_fadaiCount++; //
			}
		}
		return new int[] { str_sesions.length, str_sesions.length - li_fadaiCount }; //�/ȫ��
	}

	private long betweenTwoTimeSecond(String _date1, String _date2) {
		if (_date1 == null || _date2 == null || _date1.trim().equals("") || _date2.trim().equals("")) {
			return 0;
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date date1 = sdf.parse(_date1);
			java.util.Date date2 = sdf.parse(_date2);

			long li_second = (date2.getTime() - date1.getTime()) / 1000; // ��
			return li_second; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return 0;
		}
	}

	/**
	 * ȡ������Դ�����������!!!
	 * @return
	 */
	private int[] getDefaultDsActives() {
		try {
			String str_dbcp = "org.apache.commons.dbcp.PoolingDriver"; //DBCP����!!
			List drivers = Collections.list(DriverManager.getDrivers()); //�г���������!
			boolean isFindDbcp = false; //
			for (int i = 0; i < drivers.size(); i++) { //����,�ƺ�Ĭ��ϵͳ���Ѽ�������������[sun.jdbc.odbc.JdbcOdbcDriver][com.mysql.jdbc.Driver][oracle.jdbc.driver.OracleDriver]
				Driver driver = (Driver) drivers.get(i); //
				String clsname = driver.getClass().getName(); //
				if (clsname.equals(str_dbcp)) { //
					isFindDbcp = true; //�ҵ���!!
					break; //
				}
			}
			if (!isFindDbcp) { //�����û����,�����֮!!��Ϊ����������Ŀ��,����Դʹ�õ���WebLogic������Դ,�����Ǳ�һ����!!ԭ�������BootServlet��û�м���DBCP����!!!
				Class.forName(str_dbcp); // ����dbcp������
			}
			PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
			GenericObjectPool pool = (GenericObjectPool) driver.getConnectionPool(ServerEnvironment.getDefaultDataSourceName()); //ȡ��Ĭ������Դ!!
			int li_busys = pool.getNumActive(); //Ŀǰ��æ��
			int li_frees = pool.getNumIdle(); //Ŀǰ���е�
			int li_total = li_busys + li_frees; //Ŀǰ���������!
			return new int[] { li_busys, li_total, WLTDBConnection.MAX_ACTIVES, WLTDBConnection.MAX_INSTANCES };
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return new int[] { -999, -999, WLTDBConnection.MAX_ACTIVES, WLTDBConnection.MAX_INSTANCES };
		}
	}

	/**
	 * ���������SQL..
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
		InsertSQLBuilder isql_1 = new InsertSQLBuilder("pub_threadmonitor"); //�̼߳��
		isql_1.putFieldValue("id", _id); //
		isql_1.putFieldValue("clusterport", ServerEnvironment.getProperty("MYSELFPORT")); //��Ⱥ�˿ں�!!!��û�з����ͻ��˷���ʱ��Ϊ�յ�,һ���������ʾ�֪����!!,��LoginServlet��һ��ʼ����ע��!!
		isql_1.putFieldValue("monitortime", _monitorTime); //���ʱ��
		isql_1.putFieldValue("serverstarttime", ServerEnvironment.getProperty("SERVERREALSTARTTIME")); //ʵ������ʱ��
		isql_1.putFieldValue("jvmbusy", _busyJVM); //æ�������
		isql_1.putFieldValue("jvmfree", _freeJVM); //���е������
		isql_1.putFieldValue("jvmtotal", _totalJVM); //�ܹ��������
		isql_1.putFieldValue("evermaxjvmtotal", ServerEnvironment.EVER_MAX_TOTALMEMORY); //��������ڴ���!!!
		isql_1.putFieldValue("onlineuserbusys", _onLineUsers[1]); //�����û����
		isql_1.putFieldValue("onlineusertotals", _onLineUsers[0]); //�����û�ȫ��
		isql_1.putFieldValue("evermaxonlineusers", ServerEnvironment.EVER_MAX_ONLINEUSERS); //���������û��������!!!
		isql_1.putFieldValue("syncthreadtotals", _syn_totalthreads); //ͬ���̵߳��ܼ�!!
		isql_1.putFieldValue("syncthreadcurrs", _syncthreadcurrs); //ͬ���̵߳ĵ�ǰ������!!
		isql_1.putFieldValue("syncthreadmaxs", _syncthreadmaxs); //ͬ���̵߳������
		isql_1.putFieldValue("callthreadtotals", _callTotalThreads); //�����̵߳��ܼ���!!
		isql_1.putFieldValue("callthreadcurrs", _callthreadcurrs); //ͬ���̵߳ĵ�ǰ������
		isql_1.putFieldValue("callthreadmaxs", _callthreadmaxs); //ͬ���̵߳������
		isql_1.putFieldValue("blockthreads", "" + _blockcount); //�������߳���
		isql_1.putFieldValue("deferthreads", "" + _defercount); //���ڵ��߳���,���з�Ӧ��,��������10����,��Щ�������Ǵ�������������!!!!
		isql_1.putFieldValue("weighterthreads", "" + _overweights); //���ص��߳���
		isql_1.putFieldValue("jvmoverthreads", "" + _overjvms); //���ص��߳���
		isql_1.putFieldValue("dspoolbusy", _dsPoolActives[0]); //���ӳ�����æ��
		isql_1.putFieldValue("dspooltotal", _dsPoolActives[1]); //���ӳ������е�
		isql_1.putFieldValue("dspoolmaxbusy", _dsPoolActives[2]); //���ӳ����������æµ��!!
		isql_1.putFieldValue("dspoolmaxtotal", _dsPoolActives[3]); //���ӳ����������ʵ����!!
		return isql_1.getSQL(); //
	}

	/**
	 * �ӱ��SQL���!!!!
	 * @param _parentid
	 * @param _monitorTime
	 * @param _callPars
	 * @return
	 */
	private String getSQL_Child(String _parentid, long _llmonitortime, String _monitorTime, String[] _callPars) throws Exception {
		InsertSQLBuilder isql_1 = new InsertSQLBuilder("pub_threadmonitor_b"); //�̼߳��
		isql_1.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_THREADMONITOR_B", false)); //����
		isql_1.putFieldValue("threadmonitor_id", _parentid); //
		isql_1.putFieldValue("monitortime", _monitorTime); //���ʱ��
		isql_1.putFieldValue("threadno", _callPars[0]); //�̱߳��
		isql_1.putFieldValue("begintime", getTimeStr(Long.parseLong(_callPars[1]))); //��ʼʱ��
		isql_1.putFieldValue("iscallend", _callPars[2]); //��ʼʱ��
		isql_1.putFieldValue("calluser", _callPars[3]); //������
		isql_1.putFieldValue("servicename", _callPars[4]); //������!
		isql_1.putFieldValue("methodname", _callPars[5]); //������!
		isql_1.putFieldValue("parameters", _callPars[6]); //������!

		long ll_delay = 0;
		if (_callPars[7] == null) { //���Ϊ��,���õ�ǰʱ���ȥ��ʼʱ��
			ll_delay = _llmonitortime - Long.parseLong(_callPars[1]); //����ʱ��
		} else {
			ll_delay = Long.parseLong(_callPars[7]); //
		}
		isql_1.putFieldValue("usedsecond", "" + ll_delay); //����ʱ��!!!
		isql_1.putFieldValue("weightersize", _callPars[8]); //���صĴ�С
		isql_1.putFieldValue("weightersqls", _callPars[9]); //���ص�SQL

		isql_1.putFieldValue("jvm1", _callPars[10]); //����ǰ��������ڴ�
		isql_1.putFieldValue("jvm2", _callPars[11]); //���ú��������ڴ�
		long ll_jvmused = 0; //
		if (_callPars[11] != null) {
			ll_jvmused = Long.parseLong(_callPars[11]) - Long.parseLong(_callPars[10]); //
			isql_1.putFieldValue("jvmused", ll_jvmused); //���õĲ˵���!!!
		}

		if (_callPars[2] != null && _callPars[2].equals("Y")) {
			String str_overtype = "";//
			if (ll_delay > WLTConstants.THREAD_OVERTIME_VALUE) {
				str_overtype = str_overtype + "��ʱ"; //
			}

			if ((_callPars[8] != null && Integer.parseInt(_callPars[8]) > WLTConstants.THREAD_OVERWEIGHT_VALUE) || (_callPars[11] != null && ll_jvmused > WLTConstants.THREAD_OVERJVM_VALUE)) { //����ǳ���
				str_overtype = str_overtype + "����"; //
			}
			isql_1.putFieldValue("overtype", str_overtype); //
		}

		isql_1.putFieldValue("callmenuname", _callPars[12]); //���õĲ˵���!!!
		isql_1.putFieldValue("callstack", _callPars[13]); //���õĶ�ջ!!!
		//���ӱ���������2���ֶ� �Ƿ�ɹ����쳣��Ϣ
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
				modelname = "��½";
			} else {
				modelname = _callPars[12] == null ? "��ҳ" : (_callPars[12].split(" �� ")[0]);
				if(modelname.indexOf(".") >= 0) {
					modelname = modelname.split("\\.")[0];
				}
			}
			isql_1.putFieldValue("monitortype", modelname);//һ��ģ���� ֻ�е�½��ȡ������
			if (serverInsName == null) {
				serverInsName = getServerInsName(_callPars[16]);
			}
			isql_1.putFieldValue("serverinsname", serverInsName);//������ʵ����IP:PORT
		}
		return isql_1.getSQL(); //
	}

	/**
	 * ����һ��urlȡ����ʵ��ı�
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
			System.err.println("���������߳���ȡ���Լ��˿�ʱ,����[" + _url + "]ʱ�����쳣[" + ex.getMessage() + "]"); ///////
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
	 * ��ȡ��ǰʵ����
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
