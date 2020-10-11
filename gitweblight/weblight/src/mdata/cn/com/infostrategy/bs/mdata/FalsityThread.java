package cn.com.infostrategy.bs.mdata;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVOStruct;

//虚拟线程! 为了LR测试用!!!
public class FalsityThread extends Thread {

	private String dsName = null; //
	private String sql = null; //
	private boolean isDebugLog = true; //
	private boolean isUpperLimit = true; //
	private int[] rowArea = null; //

	public static Long ll_falthread_1 = new Long(0); //
	public static Long ll_falthread_2 = new Long(0); //

	public FalsityThread() {
	}

	public void getHashVoStructByDS(String _datasourcename, String _sql, boolean _isDebugLog, boolean _isUpperLimit, int[] _rowArea) {
		this.dsName = _datasourcename; //
		this.sql = _sql; //
		this.isDebugLog = _isDebugLog; //
		this.isUpperLimit = _isUpperLimit; //
		this.rowArea = _rowArea; //
		this.start(); //启动线程!!!
	}

	@Override
	public void run() {
		try {
			synchronized (ll_falthread_1) {
				ll_falthread_1++; //
			}
			long li_currThreads = ll_falthread_1 - ll_falthread_2; //长的数量-降的数量=当前数量!
			//System.out.println("增加数[" + ll_falthread_1 + "],减少数[" + ll_falthread_2 + "],差额[" + li_currThreads + "]"); //
			if (li_currThreads > ServerEnvironment.falsityThreadLimit) { //如果超过300个,则不做了,即可能会导致内存溢出或者把SQLServer搞死了!! 所以必须有个保险!
				return; //
			}
			CommDMO commDMO = new CommDMO(); ////
			HashVOStruct hvsStruct = commDMO.getHashVoStructByDS(dsName, sql, isDebugLog, isUpperLimit, rowArea, false, true); //使用独立事务查询!!可能很费时!!!
			//无需返回
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			synchronized (ll_falthread_2) {
				ll_falthread_2++; //
			}
		}
	}

	public static void main(String[] _args) {
		//new FalsityThread().getHashV(); //
		System.out.println("应该立即显现!!!"); //
	}

}
