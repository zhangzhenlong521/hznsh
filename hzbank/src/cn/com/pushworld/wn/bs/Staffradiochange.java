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
	public String run() throws Exception {//forѭ������
		List<Integer> currentDate = getDate();
		Integer year=currentDate.get(0);
		String currentYear=currentDate.get(0)+"";//��ȡ����ǰ��
		String currentMonth="";
		String handleDate="";
//		for (int i = 0; i<currentDate.get(1); i++) {//ѭ��������ÿ���¶�Ҫ����һ��
//			currentMonth=(i+1)+"";//��ȡ����ǰ��
//			handleDate=currentYear+"-"+(currentMonth.length()==1?"0"+currentMonth:currentMonth);//��ȡ����ǰ���������
//			//��д������ָ�����в�����ֵwn_workerStadio
//			insertStaffRadio(handleDate);
//		}
		insertStaffRadio(handleDate);
		return "ϵ������ɹ���";
	}
	
	//��ȡ����ǰʱ��(������)
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
	//��wn_workerStadio������Ϣ
	public void insertStaffRadio(String handleDate){
		try {
			WnSalaryServiceIfc service=new WnSalaryServiceImpl();
			service.insertStaffRadio(handleDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
