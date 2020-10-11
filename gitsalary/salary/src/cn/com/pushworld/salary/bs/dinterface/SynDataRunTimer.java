package cn.com.pushworld.salary.bs.dinterface;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.pushworld.salary.bs.SalaryFormulaDMO;

/**
 * ƽ̨��ʱ������ô���
 * ÿ��ִ�е���������־��
 * @author haoming
 * create by 2014-7-10
 */
public class SynDataRunTimer implements WLTJobIFC {
	private CommDMO dmo = new CommDMO();
	private DataInterfaceDMO ifcdmo = new DataInterfaceDMO();
	Logger logger = WLTLogger.getLogger(SynDataRunTimer.class); //

	public String run() throws Exception {
		//
		String files[] = ifcdmo.getFiledesc();
		boolean flag = true;
		if (files == null || files.length == 0) {
			if (TBUtil.isEmpty(ifcdmo.ftppath)) {
				throw new Exception("Ŀǰ������û������FTP·����������ϵͳ����[���ݽӿ�FTP·��]");
			} else {
				throw new Exception("Ŀǰ������·��" + ifcdmo.ftppath + "û���ҵ��κ��ϴ���¼");
			}
		}
		HashVO hisjob[] = dmo.getHashVoArrayByDS(null, "select * from SAL_SYN_DATA_JOB order by datadate desc "); //��ʷ����job
		List loglist = new ArrayList();
		//�Ƚ�������ʷ��Ŀ¼
		if (hisjob != null && hisjob.length > 0) {
			HashVO nearJob = hisjob[0];//���������
			String nearJobDate = nearJob.getStringValue("datadate"); //2014-07-04
			List needdo = new ArrayList();
			for (int i = 0; i < files.length; i++) {
				if (!files[i].equals(nearJobDate.replace("-", ""))) {
					needdo.add(files[i]);
				} else {
					break; //����ҵ�ͬһ�죬�����˳�
				}
			}
			for (int i = 0; i < needdo.size(); i++) { //��ִ�������һ�������һ�Ρ�
				String filefolder = (String) needdo.get(needdo.size() - i - 1);
				boolean rtfalg = onedataJob(filefolder, loglist); //ִ��һ������
				if (flag && !rtfalg) {
					flag = false;
				}
			}
		} else if (hisjob.length == 0) {
			for (int i = 0; i < files.length; i++) {
				boolean rtfalg = onedataJob(files[files.length - i - 1], loglist);
				if (flag && !rtfalg) {
					flag = false;
				}
			}
		}
		dmo.executeBatchByDS(null, loglist);
		System.gc(); //����������ͷ��ڴ�.....
		if (!flag) {
			logger.error("���ݽӿ�ͬ����ʱ����ִ��ʧ��");
			return "ʧ��";
		}
		logger.info("���ݽӿ�ͬ����ʱ����ִ�гɹ�");
		return "�ɹ�";
	}

	/**
	 * ִ��һ������
	 * @param _currFolder ����Ŀ¼����
	 * @param joblog 
	 * @return
	 * @throws Exception
	 */
	private boolean onedataJob(String _currFolder, List joblog) throws Exception {
		String jobid = dmo.getSequenceNextValByDS(null, "S_SAL_SYN_DATA_JOB");
		String time1 = TBUtil.getTBUtil().getCurrTime();
		long t1 = System.currentTimeMillis();
		StringBuffer descr = new StringBuffer();
		boolean flag_syn = false;
		//����ΪĿ¼
		String datadate = _currFolder.substring(0, 4) + "-" + _currFolder.substring(4, 6) + "-" + _currFolder.substring(6, 8);
		String ex1 = "";
		try {
			flag_syn = ifcdmo.syn_data_bytimer(jobid, datadate); //�����һ����ִ�нӿ�����ͬ��
		} catch (Exception ex) {
			flag_syn = false;
			ex1 = ex.toString();
		}
		String time2 = TBUtil.getTBUtil().getCurrTime();
		long t2 = System.currentTimeMillis();
		descr.append("������ƽ̨ͬ������ʼ��:" + time1 + ",����:" + time2 + ",��ʱ:" + getDifferTime(t2 - t1) + ",ͬ�������" + (flag_syn ? "�ɹ�" : "ʧ��") + "��");
		if (!flag_syn && ex1.length() > 0) {
			descr.append("\r\nԭ��:" + ex1);
			if (descr.length() > 1300) {
				descr = new StringBuffer(descr.substring(0, 1300)).append("...");
			}
		}
		boolean flag_convert = false;
		int flag_auto_calc = 1; //�Զ�����ָ�ꡣ
		String time4 = TBUtil.getTBUtil().getCurrTime();
		String ex2 = "";
		try {
			flag_convert = ifcdmo.convertIFCDataToReport(jobid, datadate);//����ڶ�����ִ�нӿ����ݱ���ת��,��ִ������Ӧ��
		} catch (Exception ex) {
			flag_convert = false;
			ex2 = ex.toString();
		}
		String time3 = TBUtil.getTBUtil().getCurrTime();
		long t3 = System.currentTimeMillis();
		descr.append("\r\nͬ�����ݷ�����ת��ʼ��:" + time2 + ",����:" + time3 + ",��ʱ:" + getDifferTime(t3 - t2) + ",ͬ�������" + (flag_convert ? "�ɹ�" : "ʧ��") + "��");
		if (!flag_convert && ex2.length() > 0) {
			descr.append("\r\nԭ��:" + ex2);
			if (descr.length() > 2600) {
				descr = new StringBuffer(descr.substring(0, 2600)).append("...");
			}
		}
		String ex3 = "";
		time4 = TBUtil.getTBUtil().getCurrTime();
		if (flag_syn && flag_convert) {//
			try {
				flag_auto_calc = new SalaryFormulaDMO().autoCalcPersonDLTargetByTimer(jobid, datadate); //���������������Ա������ָ��
			} catch (Exception ex) {
				flag_auto_calc = -1;
				ex3 = ex.toString();
			}
		}
		long t4 = System.currentTimeMillis();
		if (flag_auto_calc == 0) {
			descr.append("\r\nϵͳ��δ�����Զ����㼨Ч�Ķ���ָ��.");
		} else {
			descr.append("\r\n����ָ���Զ����㿪ʼ��:" + time3 + ",����:" + time4 + ",��ʱ:" + getDifferTime(t4 - t3) + ",ͬ�������" + (flag_auto_calc == 1 ? "�ɹ�" : "ʧ��") + "��");
		}
		if (flag_auto_calc == -1) {
			descr.append("\r\nԭ��:" + ex3);
		}

		if (descr.length() > 3900) {
			descr = new StringBuffer(descr.substring(0, 3900));
		}
		InsertSQLBuilder insert = new InsertSQLBuilder("SAL_SYN_DATA_JOB");
		insert.putFieldValue("id", jobid);
		insert.putFieldValue("starttime", time1);
		insert.putFieldValue("endtime", time4);
		insert.putFieldValue("datadate", datadate);
		insert.putFieldValue("state", (!flag_syn || !flag_convert || flag_auto_calc == -1) ? "ʧ��" : "�ɹ�");
		insert.putFieldValue("descr", descr.toString());
		joblog.add(insert);
		logger.info(descr.toString());
		if (!flag_syn || !flag_convert || flag_auto_calc == -1) {
			return false;
		}
		return true;
	}

	/*
	 * ����һ������ʱ�ת��Ϊʱ�����
	 */
	public String getDifferTime(long mss) {
		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		StringBuffer sb = new StringBuffer();
		if (days > 0) {
			sb.append(days + "��");
		}
		if (hours > 0 || sb.length() > 0) {
			sb.append(hours + "Сʱ");
		}
		if (minutes > 0 || sb.length() > 0) {
			sb.append(minutes + "��");
		}
		if (seconds > 0 || sb.length() > 0) {
			sb.append(seconds + "��");
		}
		if (sb.length() == 0) {
			sb.append(mss + "����");
		}
		return sb.toString();
	}
}
