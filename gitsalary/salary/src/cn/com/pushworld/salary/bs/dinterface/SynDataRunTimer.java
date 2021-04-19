package cn.com.pushworld.salary.bs.dinterface;

import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.pushworld.salary.bs.SalaryFormulaDMO;

/**
 * 平台定时任务调用此类
 * 每次执行的总任务日志。
 * @author zzl
 * create by 2021-01-19
 */
	public class SynDataRunTimer implements WLTJobIFC {
	private CommDMO dmo = new CommDMO();
	private DataInterfaceDMO ifcdmo = new DataInterfaceDMO();
	Logger logger = WLTLogger.getLogger(SynDataRunTimer.class); //
	TBUtil tbUtil=new TBUtil();
	String date=tbUtil.getSysOptionStringValue("T+1计算薪酬日期",null);
	public String run() throws Exception {
		StringBuffer state=new StringBuffer();
//		//
//		String files[] = ifcdmo.getFiledesc();
//		boolean flag = true;
//		if (files == null || files.length == 0) {
//			if (TBUtil.isEmpty(ifcdmo.ftppath)) {
//				throw new Exception("目前服务器没有配置FTP路径，请配置系统参数[数据接口FTP路径]");
//			} else {
//				throw new Exception("目前服务器路径" + ifcdmo.ftppath + "没有找到任何上传记录");
//			}
//		}
//		HashVO hisjob[] = dmo.getHashVoArrayByDS(null, "select * from SAL_SYN_DATA_JOB order by datadate desc "); //历史所有job
//		List loglist = new ArrayList();
//		//比较任务历史和目录
//		if (hisjob != null && hisjob.length > 0) {
//			HashVO nearJob = hisjob[0];//最近的任务
//			String nearJobDate = nearJob.getStringValue("datadate"); //2014-07-04
//			List needdo = new ArrayList();
//			for (int i = 0; i < files.length; i++) {
//				if (!files[i].equals(nearJobDate.replace("-", ""))) {
//					needdo.add(files[i]);
//				} else {
//					break; //如果找到同一天，立马退出
//				}
//			}
//			for (int i = 0; i < needdo.size(); i++) { //先执行离最后一天最近的一次。
//				String filefolder = (String) needdo.get(needdo.size() - i - 1);
//				boolean rtfalg = onedataJob(filefolder, loglist); //执行一次任务。
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
//		System.gc(); //建议虚拟机释放内存.....
//		if (!flag) {
//			logger.error("数据接口同步定时任务执行失败");
//			return "失败";
//		}
//		logger.info("数据接口同步定时任务执行成功");
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
			//处理网格计算中的被考核网格，因为网格一直在变，T+1就需要计算的时候更新
			HashVO [] vos =dmo.getHashVoArrayByDS(null,"select * from sal_person_check_post_wg where id in (select replace(checkedwg,';','') from sal_person_check_list where 1=1  and (targettype='员工定量指标')  and state='参与考核' and catalogid='215')");
			String [] ids =dmo.getStringArrayFirstColByDS(null,"select id from hzdb.EXCEL_TAB_85 where parentid='2'");
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<ids.length;i++){
				if(i==ids.length-1){
					sb.append(";"+ids[i]+";");
				}else{
					sb.append(";"+ids[i]);
				}
			}
			for(int i=0;i<vos.length;i++){
				updateClob("sal_person_check_post_wg","postid","where id='"+vos[i].getStringValue("id")+"'",sb.toString());
			}
			if(dates==null){
				if(createDate.length>0 && count!=null){
					new SalaryFormulaDMO().autoCalcPersonDLTargetByTimer("", getQYTTime("yyyy-MM-dd"));
					state.append("个人指标计算成功");
				}else{
					state.append("没有数据");
				}
			}else{
				state.append("当前的数据已经生成");
			}
			if(deptDates==null){
				if(createDate.length>0 && count!=null){
					new SalaryFormulaDMO().countDeptScore("", getQYTTime("yyyy-MM-dd"));
					state.append("部门指标计算成功");
				}else{
					state.append("没有数据");
				}
			}else{
				state.append("当前的数据已经生成");
			}
		}catch (Exception e){
			state.append("失败"+e.toString());
			e.printStackTrace();
		}
		return state.toString();
	}
	/**
	 * 得到前一天的日期
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
	 * 得到前一天的月份
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
	 * 得到前一天上月的月份
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
	 * 执行一次任务。
	 * @param _currFolder 日期目录名称
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
		//传入为目录
		String datadate = _currFolder.substring(0, 4) + "-" + _currFolder.substring(4, 6) + "-" + _currFolder.substring(6, 8);
		String ex1 = "";
		try {
			flag_syn = ifcdmo.syn_data_bytimer(jobid, datadate); //任务第一步，执行接口数据同步
		} catch (Exception ex) {
			flag_syn = false;
			ex1 = ex.toString();
		}
		String time2 = TBUtil.getTBUtil().getCurrTime();
		long t2 = System.currentTimeMillis();
		descr.append("与数据平台同步数据始于:" + time1 + ",终于:" + time2 + ",耗时:" + getDifferTime(t2 - t1) + ",同步结果【" + (flag_syn ? "成功" : "失败") + "】");
		if (!flag_syn && ex1.length() > 0) {
			descr.append("\r\n原因:" + ex1);
			if (descr.length() > 1300) {
				descr = new StringBuffer(descr.substring(0, 1300)).append("...");
			}
		}
		boolean flag_convert = false;
		int flag_auto_calc = 1; //自动计算指标。
		String time4 = TBUtil.getTBUtil().getCurrTime();
		String ex2 = "";
		try {
			flag_convert = ifcdmo.convertIFCDataToReport(jobid, datadate);//任务第二步，执行接口数据报表转换,并执行数据应用
		} catch (Exception ex) {
			flag_convert = false;
			ex2 = ex.toString();
		}
		String time3 = TBUtil.getTBUtil().getCurrTime();
		long t3 = System.currentTimeMillis();
		descr.append("\r\n同步数据分析并转换始于:" + time2 + ",终于:" + time3 + ",耗时:" + getDifferTime(t3 - t2) + ",同步结果【" + (flag_convert ? "成功" : "失败") + "】");
		if (!flag_convert && ex2.length() > 0) {
			descr.append("\r\n原因:" + ex2);
			if (descr.length() > 2600) {
				descr = new StringBuffer(descr.substring(0, 2600)).append("...");
			}
		}
		String ex3 = "";
		time4 = TBUtil.getTBUtil().getCurrTime();
		if (flag_syn && flag_convert) {//
			try {
				flag_auto_calc = new SalaryFormulaDMO().autoCalcPersonDLTargetByTimer(jobid, datadate); //任务第三步，计算员工定量指标
			} catch (Exception ex) {
				flag_auto_calc = -1;
				ex3 = ex.toString();
			}
		}
		long t4 = System.currentTimeMillis();
		if (flag_auto_calc == 0) {
			descr.append("\r\n系统中未配置自动计算绩效的定量指标.");
		} else {
			descr.append("\r\n定量指标自动计算开始于:" + time3 + ",终于:" + time4 + ",耗时:" + getDifferTime(t4 - t3) + ",同步结果【" + (flag_auto_calc == 1 ? "成功" : "失败") + "】");
		}
		if (flag_auto_calc == -1) {
			descr.append("\r\n原因:" + ex3);
		}

		if (descr.length() > 3900) {
			descr = new StringBuffer(descr.substring(0, 3900));
		}
		InsertSQLBuilder insert = new InsertSQLBuilder("SAL_SYN_DATA_JOB");
		insert.putFieldValue("id", jobid);
		insert.putFieldValue("starttime", time1);
		insert.putFieldValue("endtime", time4);
		insert.putFieldValue("datadate", datadate);
		insert.putFieldValue("state", (!flag_syn || !flag_convert || flag_auto_calc == -1) ? "失败" : "成功");
		insert.putFieldValue("descr", descr.toString());
		joblog.add(insert);
		logger.info(descr.toString());
		if (!flag_syn || !flag_convert || flag_auto_calc == -1) {
			return false;
		}
		return true;
	}

	/*
	 * 根据一个毫秒时差，转换为时分秒差
	 */
	public String getDifferTime(long mss) {
		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		StringBuffer sb = new StringBuffer();
		if (days > 0) {
			sb.append(days + "天");
		}
		if (hours > 0 || sb.length() > 0) {
			sb.append(hours + "小时");
		}
		if (minutes > 0 || sb.length() > 0) {
			sb.append(minutes + "分");
		}
		if (seconds > 0 || sb.length() > 0) {
			sb.append(seconds + "秒");
		}
		if (sb.length() == 0) {
			sb.append(mss + "毫秒");
		}
		return sb.toString();
	}

	/**
	 * zzl 修改处理clob 字段
	 * @param tablename 表名
	 * @param col       修改的字段
	 * @param whereSql  条件
	 * @param clob_content  clob字段值
	 */
	public void updateClob(String tablename,String col,String whereSql,String clob_content) {
		Writer outStream = null;
		// 通过JDBC获得数据库连接
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(
					"jdbc:oracle:thin:@10.18.126.41:1521:GRCDB",
					"hzdb", "hzdb");
			con.setAutoCommit(false);
			Statement st = con.createStatement();
			// 插入一个空对象empty_clob()，这个是必须的
			// insert into EMS_CUST_JS(cust_code, js_name,
			// js_content)values('','', empty_clob())
			st.executeUpdate("update "+tablename+" set "+col+"='empty_clob()' "+whereSql);
			// 锁定数据行进行更新，注意“for update”语句,这里不用for update锁定不可以插入clob
			ResultSet rs = st.executeQuery("select "+col+" from "+tablename+" "+whereSql+" for update");
			if (rs.next()) {
				// 得到java.sql.Clob对象后强制转换为oracle.sql.CLOB
				oracle.sql.CLOB clob = (oracle.sql.CLOB) rs.getClob(col);
				outStream = clob.getCharacterOutputStream();
				// clob_content是传入的字符串
				char[] c = clob_content.toCharArray();
				outStream.write(c, 0, c.length);
			}
			outStream.flush();
			outStream.close();
			con.commit();
			con.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
