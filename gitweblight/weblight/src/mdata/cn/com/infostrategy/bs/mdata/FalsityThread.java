package cn.com.infostrategy.bs.mdata;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVOStruct;

//�����߳�! Ϊ��LR������!!!
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
		this.start(); //�����߳�!!!
	}

	@Override
	public void run() {
		try {
			synchronized (ll_falthread_1) {
				ll_falthread_1++; //
			}
			long li_currThreads = ll_falthread_1 - ll_falthread_2; //��������-��������=��ǰ����!
			//System.out.println("������[" + ll_falthread_1 + "],������[" + ll_falthread_2 + "],���[" + li_currThreads + "]"); //
			if (li_currThreads > ServerEnvironment.falsityThreadLimit) { //�������300��,������,�����ܻᵼ���ڴ�������߰�SQLServer������!! ���Ա����и�����!
				return; //
			}
			CommDMO commDMO = new CommDMO(); ////
			HashVOStruct hvsStruct = commDMO.getHashVoStructByDS(dsName, sql, isDebugLog, isUpperLimit, rowArea, false, true); //ʹ�ö��������ѯ!!���ܷܺ�ʱ!!!
			//���践��
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
		System.out.println("Ӧ����������!!!"); //
	}

}
