package cn.com.pushworld.wn.bs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.mdata.jepfunctions.GetDateDifference;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

public class Staffradiochange  implements WLTJobIFC{

	@Override
	public String run() throws Exception {//for循环遍历
		List<Integer> currentDate = getDate();
		Integer year=currentDate.get(0);
		String currentYear=currentDate.get(0)+"";//获取到当前年
		String currentMonth="";
		String handleDate="";
//		for (int i = 0; i<currentDate.get(1); i++) {//循环遍历，每个月都要插入一次
//			currentMonth=(i+1)+"";//获取到当前月
//			handleDate=currentYear+"-"+(currentMonth.length()==1?"0"+currentMonth:currentMonth);//获取到当前处理的年月
//			//编写方法向指定表中插入数值wn_workerStadio
//			insertStaffRadio(handleDate);
//		}
		insertStaffRadio(handleDate);
		return "系数插入成功啦";
	}
	
	//获取到当前时间(年月日)
	public List<Integer> getDate(){
		List<Integer> result=new ArrayList<Integer>();
		try {
			Calendar  cal=Calendar.getInstance();
			result.add(cal.get(Calendar.YEAR));
			result.add(cal.get(cal.get(Calendar.MONTH)+1));
			result.add(cal.get(Calendar.DATE));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	//向wn_workerStadio插入信息
	public void insertStaffRadio(String handleDate){
		try {
			WnSalaryServiceIfc service=new WnSalaryServiceImpl();
			service.insertStaffRadio(handleDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
