package cn.com.pushworld.salary.bs.dinterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
 * @author zzl
 * create by 2021-01-19
 */
	public class SynDataRunTimer implements WLTJobIFC {
	private CommDMO dmo = new CommDMO();
	private DataInterfaceDMO ifcdmo = new DataInterfaceDMO();
	Logger logger = WLTLogger.getLogger(SynDataRunTimer.class); //
	TBUtil tbUtil=new TBUtil();
	String date=tbUtil.getSysOptionStringValue("T+1����н������",null);
	public String run() throws Exception {
		StringBuffer state=new StringBuffer();
//		//
//		String files[] = ifcdmo.getFiledesc();
//		boolean flag = true;
//		if (files == null || files.length == 0) {
//			if (TBUtil.isEmpty(ifcdmo.ftppath)) {
//				throw new Exception("Ŀǰ������û������FTP·����������ϵͳ����[���ݽӿ�FTP·��]");
//			} else {
//				throw new Exception("Ŀǰ������·��" + ifcdmo.ftppath + "û���ҵ��κ��ϴ���¼");
//			}
//		}
//		HashVO hisjob[] = dmo.getHashVoArrayByDS(null, "select * from SAL_SYN_DATA_JOB order by datadate desc "); //��ʷ����job
//		List loglist = new ArrayList();
//		//�Ƚ�������ʷ��Ŀ¼
//		if (hisjob != null && hisjob.length > 0) {
//			HashVO nearJob = hisjob[0];//���������
//			String nearJobDate = nearJob.getStringValue("datadate"); //2014-07-04
//			List needdo = new ArrayList();
//			for (int i = 0; i < files.length; i++) {
//				if (!files[i].equals(nearJobDate.replace("-", ""))) {
//					needdo.add(files[i]);
//				} else {
//					break; //����ҵ�ͬһ�죬�����˳�
//				}
//			}
//			for (int i = 0; i < needdo.size(); i++) { //��ִ�������һ�������һ�Ρ�
//				String filefolder = (String) needdo.get(needdo.size() - i - 1);
//				boolean rtfalg = onedataJob(filefolder, loglist); //ִ��һ������
//				if (flag && !rtfalg) {
//					flag = false;
//				}
//			}
//		} else if (hisjob.length == 0) {
//			for (int i = 0; i < files.length; i++) {
//				boolean rtfalg = onedataJob(files[files.length - i - 1], loglist);
//				if (flag && !rtfalg) {
//					flag = false;
//				}
//			}
//		}
//		dmo.executeBatchByDS(null, loglist);A_AGR_DEP_ACCT_ENT_FX_202101
//		System.gc(); //����������ͷ��ڴ�.....
//		if (!flag) {
//			logger.error("���ݽӿ�ͬ����ʱ����ִ��ʧ��");
//			return "ʧ��";
//		}
//		logger.info("���ݽӿ�ͬ����ʱ����ִ�гɹ�");
		try{
			String dates=dmo.getStringValueByDS(null,"select distinct(datadate) from hzdb.sal_person_check_auto_score where datadate='"+getQYTTime("yyyy-MM-dd")+"'");
			String deptDates=dmo.getStringValueByDS(null,"select distinct(checkdate) from hzdb.sal_person_check_dept_score where checkdate='"+getQYTTime("yyyy-MM-dd")+"'");
			String [] createDate= dmo.getStringArrayFirstColByDS(null,"select CREATED from dba_objects where object_name = 'GRID_DATA_"+getQYTTime("yyyy-MM-dd").replace("-","")+"' and OBJECT_TYPE='TABLE'");
			String count=dmo.getStringValueByDS(null,"select dates from hzdb.count_avg where dates='"+getQYTTime("yyyy-MM-dd").replace("-","")+"'");
			String qny=dmo.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'S_LOAN_QNY_"+getQYTMonth("yyyyMM")+"' and OBJECT_TYPE='TABLE'");
			if(qny==null){
				dmo.executeUpdateByDS(null,"create table hzdb.S_LOAN_QNY_"+getQYTTime("yyyyMM")+" as select * from hzdb.S_LOAN_QNY_"+getSMonth("yyyyMM")+"");
			}
			String dk=dmo.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'S_LOAN_DK_"+getQYTMonth("yyyyMM")+"' and OBJECT_TYPE='TABLE'");
			if(dk==null){
				dmo.executeUpdateByDS(null,"create table hzdb.s_loan_dk_"+getQYTTime("yyyyMM")+" as select * from hzdb.s_loan_dk_"+getSMonth("yyyyMM")+"");
			}
			String dgdk=dmo.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'S_LOAN_DK_DG_"+getQYTMonth("yyyyMM")+"' and OBJECT_TYPE='TABLE'");
			if(dgdk==null){
				dmo.executeUpdateByDS(null,"create table hzdb.s_loan_dk_dg_"+getQYTTime("yyyyMM")+" as select * from hzdb.s_loan_dk_dg_"+getSMonth("yyyyMM")+"");
			}
			String xygc=dmo.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'S_LOAN_XYGC_"+getQYTMonth("yyyyMM")+"' and OBJECT_TYPE='TABLE'");
			if(xygc==null){
				dmo.executeUpdateByDS(null,"create table hzdb.s_loan_xygc_"+getQYTTime("yyyyMM")+" as select * from hzdb.s_loan_xygc_"+getSMonth("yyyyMM")+"");
			}
			String qnyyx=dmo.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'S_LOAN_QNYYX_"+getQYTMonth("yyyyMM")+"' and OBJECT_TYPE='TABLE'");
			if(qnyyx==null){
				dmo.executeUpdateByDS(null,"create table hzdb.s_loan_qnyyx_"+getQYTTime("yyyyMM")+" as select * from hzdb.s_loan_qnyyx_"+getSMonth("yyyyMM")+"");
			}
			String eloan=dmo.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'S_LOAN_ESIGN_"+getQYTMonth("yyyyMM")+"' and OBJECT_TYPE='TABLE'");
			if(eloan==null){
				dmo.executeUpdateByDS(null,"create table hzdb.S_LOAN_ESIGN_"+getQYTTime("yyyyMM")+" as select * from hzdb.S_LOAN_ESIGN_"+getSMonth("yyyyMM")+"");
			}
			String hxdg=dmo.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'S_LOAN_HXDG_"+getQYTMonth("yyyyMM")+"' and OBJECT_TYPE='TABLE'");
			if(hxdg==null){
				dmo.executeUpdateByDS(null,"create table hzdb.s_loan_hxdg_"+getQYTTime("yyyyMM")+" as select * from hzdb.s_loan_hxdg_"+getSMonth("yyyyMM")+"");
			}
			if(dates==null){
				if(createDate.length>0 && count!=null){
					new SalaryFormulaDMO().autoCalcPersonDLTargetByTimer("", getQYTTime("yyyy-MM-dd"));
					state.append("����ָ�����ɹ�");
				}
			}else{
				state.append(getQYTTime("yyyy-MM-dd")+"����ָ���Ѿ�����");
			}
            //zzl ��Ӳ���ָ�����
			if(deptDates==null){
				if(createDate.length>0 && count!=null){
					new SalaryFormulaDMO().countDeptScore("", getQYTTime("yyyy-MM-dd"));
					state.append("����ָ�����ɹ�");
				}
			}else{
				state.append(getQYTTime("yyyy-MM-dd")+"����ָ�������Ѿ�����");
			}
		}catch (Exception e){
			state.append("ʧ��"+e.toString());
			e.printStackTrace();
		}
		return state.toString();
	}
	/**
	 * �õ�ǰһ�������
	 *
	 * @return
	 */
	public String getQYTTime(String dates) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(dates);
		if(date==null || date.equals("") || date.equals(null)){
			Date d = new Date();
			cal.setTime(d);
			int day = cal.get(Calendar.DATE);
			cal.set(Calendar.DATE, day - 1);
		}else{
			try {
				Date date1=format.parse(date);
				cal.setTime(date1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}
	/**
	 * �õ�ǰһ����·�
	 *
	 * @return
	 */
	public String getQYTMonth(String dates) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(dates);
		if(date==null || date.equals("") || date.equals(null)){
			Date d = new Date();
			cal.setTime(d);
			int day = cal.get(Calendar.DATE);
			cal.set(Calendar.DATE, day - 1);
		}else{
			try {
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
				Date date1=format2.parse(date);
				cal.setTime(date1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}
	/**
	 * �õ�ǰһ�����µ��·�
	 *
	 * @return
	 */
	public String getSMonth(String dates) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(dates);
		if(date==null || date.equals("") || date.equals(null)){
			Date d = new Date();
			cal.setTime(d);
			int day = cal.get(Calendar.DATE);
			cal.set(Calendar.DATE, day - 1);
			cal.setTime(cal.getTime());
			cal.add(Calendar.MONTH,- 1);
		}else{
			try {
				SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
				Date date1=format2.parse(date);
				cal.setTime(date1);
				cal.setTime(cal.getTime());
				cal.add(Calendar.MONTH,- 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}
	public static void main(String[] args) {
		SynDataRunTimer synDataRunTimer=new SynDataRunTimer();
		synDataRunTimer.date="2021-01-31";
		System.out.println(synDataRunTimer.getSMonth("yyyyMM"));
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
