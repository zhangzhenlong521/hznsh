package com.pushworld.ipushgrc.ui.target.p030;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * �����¼�����ʱ����ָ�� ϵͳ��ȡʵ����
 * 
 * @author hm
 * 
 */
public class EventFindTargetComputeImpl implements TargetSystemImportIFC {
	private String beginYear = "2010-01-01"; // ��ʼ�ꡣ ϵͳ�Զ�����2010���Ժ�����.
	private String currDate; // ��ǰʱ��
	private TBUtil tbutil = new TBUtil();
	private HashVO tarVO = null;
	private String corpname = ClientEnvironment.getLoginUserDeptName();
	private String corpid = ClientEnvironment.getCurrLoginUserVO().getPKDept();
	private int cycle = 2; // ���ڡ���������µ����ݣ���������������ڵġ�Ĭ���������ڡ�Ҳ���ǽ������£��������ȡ������ꡣ >0

	public void compute() throws Exception {
		String sql = "select * from CMP_TARGET where name = '�¼���������ʱ����'";
		currDate = UIUtil.getServerCurrDate();
		HashVO[] vo = UIUtil.getHashVoArrayByDS(null, sql);
		if (vo.length > 0) {
			tarVO = vo[0];
			String cycletype = tarVO.getStringValue("cycletype");
			HashVO[] exist_target_inst = UIUtil.getHashVoArrayByDS(null, " select * from cmp_target_inst where target_id ='" + tarVO.getStringValue("id") + "' order by instyear ");// �Ѿ��е�ָ��ʵ��
			if ("year".equals(cycletype)) { // ���ָ��
				isYear(exist_target_inst);
			} else if ("season".equals(cycletype)) { // ����ָ��
				isSeason(exist_target_inst);
			} else if ("month".equals("cycletype")) { // �¶�ָ��
				isMonth(exist_target_inst);
			}
		} else {
			throw new Exception("û�з���[�¼���������ʱ����]ָ��!");
		}

	}

	public void isYear(HashVO[] _vos) {
		if (_vos == null || _vos.length == 0) { // ֤����û�й���¼������Ҫ�����ڿ�ʼȫ���Զ����ɳ�����

		}
	}

	public void isSeason(HashVO[] _vos) { //
		LinkedHashMap insertMap = new LinkedHashMap(); // key = season value
														// ���һ��list��������ġ����ó�
		LinkedHashMap updateMap = new LinkedHashMap(); // key = season value
														// ���һ��list��������ġ����ó�
		HashVO[] insertvo = null;
		HashVO[] updatevo = null;
		int date[] = getCurrSeason(currDate);
		String[] seasonBeginAndEnd = getSeasonBeforeAndEnd(date[0], date[1]);
		List sqlList = new ArrayList();
		if (_vos == null || _vos.length == 0) { // ֤����û�й���¼������Ҫ�����ڿ�ʼȫ���Զ����ɳ�����
			try {
				insertvo = UIUtil.getHashVoArrayByDS(null, "select * from cmp_event where 1 = 1 and finddate<='" + seasonBeginAndEnd[1] + "'"); // С�ڱ�����
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { // ����Ѿ���ȡ���� Ӧ�ø�������������ڵġ� ���ǿ��ǵ�����ܾ�û�н��г�ȡ������ֿ����ݡ�
			HashVO lastVO = _vos[_vos.length - 1];
			String last_year = lastVO.getStringValue("instyear"); // ���
			String last_cyclevalue = lastVO.getStringValue("cyclevalue"); // ����
																			// ��������ڰ�����㡣���Ϊ�ա��������ͳ�ƣ����Ӧ�����·ݡ����������ͳ����Ϊ����.
			String[] nextdate = getNextSeason(Integer.parseInt(last_year.substring(0, 4)), Integer.parseInt(last_cyclevalue.substring(0, 1)));
			long l1 = getDate(nextdate[0]).getTime(); // �����ɵ�ָ������һ��ʱ������һ���ȵĳ�ʼʱ��
			String cycleDate = getFromDate();
			long l2 = getDate(seasonBeginAndEnd[0]).getTime(); // ��ǰ���ȵļ��ȳ�ʼʱ��
			long l3 = getDate(cycleDate).getTime(); // ��ǰʱ���ǰ�������ڵļ��ȳ�ʼʱ��
			if (l1 > l2) { // �������Ѿ�ͳ�ƹ� ���������������
				try {
					updatevo = UIUtil.getHashVoArrayByDS(null, "select * from cmp_event where 1 = 1 and finddate>='" + getFromDate() + "' and finddate<='" + seasonBeginAndEnd[1] + "'");
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (l1 < l2 && l1 > l3) { // ������û��ͳ�ƣ��ϼ���ͳ���ˡ�
				try {
					insertvo = UIUtil.getHashVoArrayByDS(null, "select * from cmp_event where 1 = 1 and finddate>='" + nextdate[0] + "' and finddate<='" + seasonBeginAndEnd[1] + "'");
					updatevo = UIUtil.getHashVoArrayByDS(null, "select * from cmp_event where 1 = 1 and finddate>='" + cycleDate + "' and finddate<'" + nextdate[0] + "'");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (l1 < l3) { // ȫ���µġ�
				try {
					insertvo = UIUtil.getHashVoArrayByDS(null, "select * from cmp_event where 1 = 1 and finddate>='" + nextdate[0] + "' and finddate<='" + seasonBeginAndEnd[1] + "'");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
		ReportUtil reportUtil = new ReportUtil();
		if (insertvo != null) {
			reportUtil.leftOuterJoin_YSMDFromDateTime(insertvo, "season", "finddate", "��");
			for (int i = 0; i < insertvo.length; i++) {
				String season = (String) insertvo[i].getAttributeValue("season");
				List list = null;
				if (insertMap.get(season) == null) {
					list = new ArrayList();
				} else {
					list = (List) insertMap.get(season);
				}
				CommonDate finddate = null;
				CommonDate happendate = null;
				finddate = new CommonDate(insertvo[i].getDateValue("finddate"));
				happendate = new CommonDate(insertvo[i].getDateValue("happendate"));
				int days = CommonDate.getDaysBetween(happendate, finddate);
				list.add(days);
				insertMap.put(season, list);
			}
			String str[][] = new String[insertMap.size()][2];
			Iterator it = insertMap.entrySet().iterator();
			int num = 0;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				str[num][0] = (String) entry.getKey();
				List list = (List) entry.getValue();
				double count = 0;
				for (int i = 0; i < list.size(); i++) {
					count += (Integer) list.get(i);
				}
				double d = count / list.size();
				DecimalFormat df = new DecimalFormat("0.0");
				str[num][1] = df.format(d);
				num++;
			}
			for (int i = 0; i < str.length; i++) {
				InsertSQLBuilder builder = new InsertSQLBuilder("cmp_target_inst");
				try {
					builder.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "s_cmp_target_inst"));
					builder.putFieldValue("target_id", tarVO.getStringValue("id"));
					builder.putFieldValue("target_name", tarVO.getStringValue("name"));
					builder.putFieldValue("instyear", str[i][0].substring(0, 4));
					builder.putFieldValue("cyclevalue", str[i][0].substring(5));
					builder.putFieldValue("targetvalue", str[i][1]);
					builder.putFieldValue("corpid", corpid);
					builder.putFieldValue("corpname", corpname);
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				sqlList.add(builder.getSQL());
			}
		}
		if (updatevo != null) {
			reportUtil.leftOuterJoin_YSMDFromDateTime(updatevo, "season", "finddate", "��");
			for (int i = 0; i < updatevo.length; i++) {
				String season = (String) updatevo[i].getAttributeValue("season");
				List list = null;
				if (updateMap.get(season) == null) {
					list = new ArrayList();
				} else {
					list = (List) updateMap.get(season);
				}
				CommonDate finddate = null;
				CommonDate happendate = null;
				finddate = new CommonDate(updatevo[i].getDateValue("finddate"));
				happendate = new CommonDate(updatevo[i].getDateValue("happendate"));
				int days = CommonDate.getDaysBetween(happendate, finddate);
				list.add(days);
				updateMap.put(season, list);
			}
			String str[][] = new String[updateMap.size()][2];
			Iterator it = updateMap.entrySet().iterator();
			int num = 0;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				str[num][0] = (String) entry.getKey();
				List list = (List) entry.getValue();
				double count = 0;
				for (int i = 0; i < list.size(); i++) {
					count += (Integer) list.get(i);
				}
				double d = count / list.size();
				DecimalFormat df = new DecimalFormat("0.0");
				str[num][1] = df.format(d);
				num++;
			}
			for (int i = 0; i < str.length; i++) {
				UpdateSQLBuilder builder = new UpdateSQLBuilder("cmp_target_inst");
				try {
					builder.putFieldValue("targetvalue", str[i][1]);
					builder.setWhereCondition(" target_id ='" + tarVO.getStringValue("id") + "' and " + " instyear = '" + str[i][0].substring(0, 4) + "' and cyclevalue ='" + str[i][0].substring(5) + "' and corpid ='" + corpid + "'");
				} catch (Exception e) {
					e.printStackTrace();
				}
				sqlList.add(builder.getSQL());
			}
		}
		try {
			UIUtil.executeBatchByDS(null, sqlList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void isMonth(HashVO[] _vos) {
		if (_vos == null || _vos.length == 0) { // ֤����û�й���¼������Ҫ�����ڿ�ʼȫ���Զ����ɳ�����

		}
	}

	public Date getDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public int[] getCurrSeason(String date) { // ������ ret[0]=2011 ret[1]=2;
		String[] str = date.split("-");
		int month = Integer.parseInt(str[1]);
		int season = 0;
		int[] ret = new int[2];
		if (month == 1 || month == 2 || month == 3) {
			season = 1;
		} else if (month == 4 || month == 5 || month == 6) {
			season = 2;
		} else if (month == 7 || month == 8 || month == 9) {
			season = 3;
		} else if (month == 10 || month == 11 || month == 12) {
			season = 4;
		}
		ret[0] = Integer.parseInt(str[0]); // ��
		ret[1] = season; // ����
		return ret;
	}

	public String[] getNextSeason(int year, int season) {
		if (season == 4) {
			year++;
			season = 1;
		} else {
			season++;
		}
		return getSeasonBeforeAndEnd(year, season);
	}

	public String[] getSeasonBeforeAndEnd(int year, int season) { // ȡ��ĳ����ĳ���ȵ���ʼ
		String str[] = new String[2];
		if (season == 1) {
			str[0] = year + "-01-01";
			str[1] = year + "-03-31";
		} else if (season == 2) {
			str[0] = year + "-04-01";
			str[1] = year + "-06-30";
		} else if (season == 3) {
			str[0] = year + "-07-01";
			str[1] = year + "-09-30";
		} else if (season == 4) {
			str[0] = year + "-10-01";
			str[1] = year + "-12-31";
		}
		return str;
	}

	public String getFromDate() { // �������й�ϵ���������ȵ���ʼʱ��
		int[] currSeason = getCurrSeason(currDate);
		int year = currSeason[0];
		int season = currSeason[1];
		int move_1 = cycle / 4;
		int move_2 = cycle % 4;
		year = year - move_1;
		season = season - move_2 + 1;
		if (season <= 0) {
			season = season + 4;
			year--;
		}
		return getSeasonBeforeAndEnd(year, season)[0];
	}

	public List getAllSeason(String begin, String end) { // ������ʼ
															// ���ҵ�����ʱ��֮������м���
															// beginС end ��
		long l_begin = tbutil.parseDateToLongValue(begin);
		long l_end = tbutil.parseDateToLongValue(end);
		if (l_begin > l_end) {
			MessageBox.show("begin�¼�Ӧ����end֮ǰ��");
		}
		int[] beginYear = getCurrSeason(begin);
		int[] endYear = getCurrSeason(end);

		int x_1 = beginYear[0]; // ��ʼ��
		int x_2 = beginYear[1]; // ��ʼ����

		int y_1 = endYear[0];
		int y_2 = endYear[1];
		List ret = new ArrayList();
		for (int i = x_1; i <= y_1; i++) {
			if (x_1 != y_1) {
				if (i == x_1) {
					for (int j = x_2; j <= 4; j++) {
						ret.add(i + "-" + j);
					}
				} else if (i == y_1) {
					for (int j = 1; j <= y_2; j++) {
						ret.add(i + "-" + j);
					}
				} else {
					for (int j = 1; j <= 4; j++) {
						ret.add(i + "-" + j);
					}
				}
			} else {
				for (int j = x_2; j <= y_2; j++) {
					ret.add(i + "-" + j);
				}
			}

		}
		return ret;
	}

	public static void main(String[] args) {
		List list = new EventFindTargetComputeImpl().getAllSeason("2009-04-09", "2011-04-26");
		for (Object obj : list) {
			System.out.println(obj);
		}
	}
}
