package com.pushworld.ipushgrc.bs.login.p010;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;

/**
 * ���ŵ�¼ͳ��
 * @author YangQing/2013-11-27
 *
 */
public class DeptLoginReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	public HashVO[] buildReportData(HashMap consMap) throws WLTRemoteException, Exception {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String str_date = (String) consMap.get("date");//ѡ�������
		String today = new TBUtil().getCurrDate();//��������

		AnalyseLoginData loginInfo = new AnalyseLoginData();
		List<String> dayofweeklist = loginInfo.getDayofWeek(str_date);
		List<String> curr_dayofweek = loginInfo.getDayofWeek(today);
		String weekofyear = loginInfo.getWeekOfYear(dayofweeklist.get(0), dayofweeklist.get(dayofweeklist.size() - 2));

		Date searchMonday = sdf2.parse(dayofweeklist.get(0));//��ѯ������������һ
		Date currSunday = sdf2.parse(curr_dayofweek.get(curr_dayofweek.size() - 1));//��ǰ������������ĩ
		if (searchMonday.getTime() > currSunday.getTime()) {
			//��ѯ���Ǳ����Ժ�����ڣ�û�м�¼
			return new HashVO[0];
		}

		loginInfo.analyseDeptData(str_date);//�������ŵ�¼����

		String sql_deptlogin = "select ID,ONLINE_HOUR ����ʱ��,concat(concat('��',WEEKOFYEAR),'������ʱ��(Сʱ)') ����ʱ��_����,DEPARTNAME �������� From pub_deptlogindata where weekofyear='" + weekofyear + "' order by online_hour desc";
		HashVO[] deptloginVO = new CommDMO().getHashVoArrayByDS(null, sql_deptlogin);
		return deptloginVO;
	}

	public String[] getGroupFieldNames() {

		return null;
	}

	public String[] getSumFiledNames() {

		return null;
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType(2);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType(int _type) {
		ArrayList<BeforeHandGroupTypeVO> al = new ArrayList<BeforeHandGroupTypeVO>(); //

		BeforeHandGroupTypeVO typeVo1 = new BeforeHandGroupTypeVO();
		typeVo1.setName("����_����ʱ��");//ά������
		typeVo1.setRowHeaderGroupFields(new String[] { "��������" });
		typeVo1.setColHeaderGroupFields(new String[] { "����ʱ��_����" });
		typeVo1.setComputeGroupFields(new String[][] { { "����ʱ��", BeforeHandGroupTypeVO.INIT } });
		al.add(typeVo1);
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	/**
	 * ��ȡ��ʾ��ϸ����
	 */
	public String getDrillActionClassPath() throws Exception {
		return "com.pushworld.ipushgrc.ui.login.p010.DeptLoginStatisWKPanel";
	}
}
