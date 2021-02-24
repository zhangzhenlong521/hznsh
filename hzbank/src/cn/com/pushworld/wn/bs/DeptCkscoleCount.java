package cn.com.pushworld.wn.bs;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.print.DocFlavor.STRING;

import com.ibm.db2.jcc.a.a;
import com.ibm.db2.jcc.a.de;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import freemarker.template.SimpleDate;

/**
 * 
 * @author zzl
 * 
 *         2019-4-10-����11:08:27 ���Ŵ��÷ּ���
 */
public class DeptCkscoleCount {
	private static CommDMO dmo = new CommDMO();
	private YearManAndWifeHouseholdsCount year = new YearManAndWifeHouseholdsCount();
	private HashVO[] vo = null;
	private String day = null;
	private static String[] deptid = null;
	private static HashMap deptCode, deptName = null;
	private YearDeptCkscoleCountRJ yearRJ = new YearDeptCkscoleCountRJ();// �������վ���
	private MonthDeptCkscoleCountRJ monthRJ = new MonthDeptCkscoleCountRJ();// �´���վ�
	private MonthDeptCkscoleCountSD monthSD = new MonthDeptCkscoleCountSD();// ������ʵ����
	private YearDeptCkscoleCountSD yearSD = new YearDeptCkscoleCountSD();// ������ʵ����
	private SMonthDeptCkscoleCountRJ smontnRJ = new SMonthDeptCkscoleCountRJ();// ���´���վ�
	private SMonthDeptCkscoleCountSD smontnSD = new SMonthDeptCkscoleCountSD();// ���´��ʵ����
	private DeptCkscoreJobCount job = new DeptCkscoreJobCount();// ������
	private String[] time = null;
	static {
		try {
			String[] deptIds = dmo
					.getStringArrayFirstColByDS(
							null,
							"select DEPTID from sal_target_checkeddept where 1=1  and (targetid='2721')  order by id");// ��ȡ������
			String deptId = new String();
			for (int i = 0; i < deptIds.length; i++) {
				deptId = deptId + deptIds[i];
			}
			deptId = deptId.replaceAll(";;", ";");
			deptid = deptId.substring(1, deptId.length() - 1).split(";");
			deptCode = dmo.getHashMapBySQLByDS(null,
					"SELECT id,CODE FROM PUB_CORP_DEPT");// ��ȡ������ID code
			deptName = dmo.getHashMapBySQLByDS(null,
					"SELECT id,name FROM PUB_CORP_DEPT");// ��ȡ������ID name
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���������
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, HashMap<String, String>> getResult(String date) {
		time = date.split(";");
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> yearRJMap = yearRJ.getCountYearRJ(date, deptid,
				deptCode);// �������վ���
		HashMap<String, String> monthRJMap = monthRJ.getCountYearRJ(date,
				deptid, deptCode);// �´���վ���
		HashMap<String, String> monthSDMap = monthSD.getCountYearRJ(date,
				deptid, deptCode);// ������ʵ����
		HashMap<String, String> yearSDMap = yearSD.getCountYearRJ(date, deptid,
				deptCode);// ������ʵ����
		HashMap<String, String> yearRJobMap = job.getJyearCount();// �������վ�����������������
		HashMap<String, String> yearSDjobMap = job.getJyearHourCount();// ������ʱ�������������������
		HashMap<String, String> map1 = new HashMap<String, String>();// ���������´���վ���-�������վ���/��������ֵ
		HashMap<String, String> map2 = new HashMap<String, String>();// ������ʵ����-������ʵ����/��������ֵ
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		int month = 0;
		try {
			month = Integer.parseInt(new SimpleDateFormat("MM").format(simple
					.parse(time[1])));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < deptid.length; i++) {
			BigDecimal db1 = new BigDecimal("0.0");
			// �´���վ���-�������վ���
			db1 = new BigDecimal(monthRJMap.get(deptCode.get(deptid[i])))
					.subtract(new BigDecimal(yearRJMap.get(deptCode
							.get(deptid[i]))));
			// �´���վ���-�������վ���/�������վ�����������������
			db1 = db1.divide(new BigDecimal("10000")).divide(
					new BigDecimal(yearRJobMap.get(deptName.get(deptid[i])
							.toString() + "_" + String.valueOf(month))), 2);
			map1.put(deptCode.get(deptid[i]).toString(), db1.toString());
			System.out.println(""
					+ deptName.get(deptid[i])
					+ "�´���վ���="
					+ monthRJMap.get(deptCode.get(deptid[i]))
					+ "�������վ���="
					+ yearRJMap.get(deptCode.get(deptid[i]))
					+ "�վ����������������� ="
					+ yearRJobMap.get(deptName.get(deptid[i]).toString() + "_"
							+ String.valueOf(month))
					+ "�´���վ���-�������վ���/�������վ����������������� =" + db1);
			// --------------------------------
			BigDecimal db2 = new BigDecimal("0.0");
			// ������ʵ����-������ʵ����
			db2 = new BigDecimal(monthSDMap.get(deptCode.get(deptid[i])))
					.subtract(new BigDecimal(yearSDMap.get(deptCode
							.get(deptid[i]))));
			// ������ʵ����-������ʵ����/ʱ�������������������
			db2 = db2.divide(new BigDecimal("10000")).divide(
					new BigDecimal(yearSDjobMap.get(deptName.get(deptid[i])
							.toString() + "_" + String.valueOf(month))), 2);
			map2.put(deptCode.get(deptid[i]).toString(), db2.toString());
			System.out.println(""
					+ deptName.get(deptid[i])
					+ "������ʵ����="
					+ monthSDMap.get(deptCode.get(deptid[i]))
					+ "������ʵ����="
					+ yearSDMap.get(deptCode.get(deptid[i]))
					+ "ʱ������������������� ="
					+ yearSDjobMap.get(deptName.get(deptid[i]).toString() + "_"
							+ String.valueOf(month))
					+ "������ʵ����-������ʵ����/ʱ������������������� =" + db2);
		}
		map.put("���վ���-����վ���/���������������", map1);
		map.put("��ʵ����-�������/����������������", map2);
		return map;
	}

	/**
	 * ����������
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, HashMap<String, String>> getResult1(String date) {
		String[] time = date.split(";");
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> monthRJMap = monthRJ.getCountYearRJ(date,
				deptid, deptCode);// �´���վ���
		HashMap<String, String> monthSDMap = monthSD.getCountYearRJ(date,
				deptid, deptCode);// ������ʵ����
		HashMap<String, String> smonthRJMap = smontnRJ.getCountYearRJ(date,
				deptid, deptCode);// ���´���վ�
		HashMap<String, String> smonthSDMap = smontnSD.getCountYearRJ(date,
				deptid, deptCode);// ���´��ʵ����
		HashMap<String, String> map3 = new HashMap<String, String>();// �´���վ���-�����´���վ���/��������ֵ
		HashMap<String, String> map4 = new HashMap<String, String>();// ��ʵ����-����ʵ����/��������ֵ
		HashMap<String, String> smonthRJobMap = job.getJmonthCount();// �������վ������������������
		HashMap<String, String> smonthSDjobMap = job.getJmonthHourCount();// ������ʱ��������������������
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		int month = 0;
		try {
			month = Integer.parseInt(new SimpleDateFormat("MM").format(simple
					.parse(time[2])));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < deptid.length; i++) {
			// --------------------------------
			BigDecimal db3 = new BigDecimal("0.0");
			// �´���վ���-�����´���վ���
			db3 = new BigDecimal(monthRJMap.get(deptCode.get(deptid[i])))
					.subtract(new BigDecimal(smonthRJMap.get(deptCode
							.get(deptid[i]))));
			// �´���վ���-�����´���վ���/��������ֵ
			db3 = db3.divide(new BigDecimal("10000")).divide(
					new BigDecimal(smonthRJobMap.get(deptName.get(deptid[i])
							.toString() + "_" + String.valueOf(month))), 2);
			map3.put(deptCode.get(deptid[i]).toString(), db3.toString());
			System.out.println(">>>>>>>"
					+ deptName.get(deptid[i])
					+ "�´���վ���="
					+ monthRJMap.get(deptCode.get(deptid[i]))
					+ "�����´���վ���="
					+ smonthRJMap.get(deptCode.get(deptid[i]))
					+ "�վ������������������ ="
					+ smonthRJobMap.get(deptName.get(deptid[i]).toString()
							+ "_" + String.valueOf(month))
					+ "�´���վ���-�����´���վ���/��������ֵ =" + db3);
			// --------------------------------
			BigDecimal db4 = new BigDecimal("0.0");
			// ������ʵ����-���´��ʵ����
			db4 = new BigDecimal(monthSDMap.get(deptCode.get(deptid[i])))
					.subtract(new BigDecimal(smonthSDMap.get(deptCode
							.get(deptid[i]))));
			// ������ʵ����-���´��ʵ����/��������ֵ
			db4 = db4.divide(new BigDecimal("10000")).divide(
					new BigDecimal(smonthSDjobMap.get(deptName.get(deptid[i])
							.toString() + "_" + String.valueOf(month))), 2);
			map4.put(deptCode.get(deptid[i]).toString(), db4.toString());
			System.out.println(""
					+ deptName.get(deptid[i])
					+ "��ʵ����="
					+ monthSDMap.get(deptCode.get(deptid[i]))
					+ "���´��ʵ����="
					+ smonthSDMap.get(deptCode.get(deptid[i]))
					+ "ʱ�������������������� ="
					+ smonthSDjobMap.get(deptName.get(deptid[i]).toString()
							+ "_" + String.valueOf(month))
					+ "������ʵ����-���´��ʵ����/��������ֵ =" + db4);
		}
		map.put("���վ���-�����վ���/�վ������������������", map3);
		map.put("��ʵ����-���µ���/ʱ��������������������", map4);
		return map;
	}
}