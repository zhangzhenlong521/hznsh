package cn.com.pushworld.wn.bs;

import java.math.BigDecimal;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;

/**
 * 
 * @author zzl
 * 
 *         2019-4-21-����12:11:16 ������ʵ����
 */
public class YearDeptCkscoleCountSD {
	private static CommDMO dmo = new CommDMO();

	/**
	 * ������ʵ�����ϼ�
	 * 
	 * @param date
	 * @param dept
	 * @return
	 */
	public HashMap<String, String> getCountYearRJ(String date,
			String[] deptids, HashMap<String, String> deptMap) {
		HashMap<String, String> map = new HashMap<String, String>();
		HashMap<String, String> dghqmap = getDGHQBMRJNC(date);// �Թ�����
		HashMap<String, String> dgdqmap = getDGDQRJNCSD(date);// �Թ�����
		HashMap<String, String> dshqmap = getDSHQBMRJNC(date);// ��˽����
		HashMap<String, String> dsdqmap = getDSDQBMRJNC(date);// ��˽����
		for (int i = 0; i < deptids.length; i++) {
			BigDecimal db = new BigDecimal("0.0");
			if (dghqmap.get(deptMap.get(deptids[i])) != null) {
				db = db.add(new BigDecimal(dghqmap.get(deptMap.get(deptids[i]))));
			}
			if (dgdqmap.get(deptMap.get(deptids[i])) != null) {
				db = db.add(new BigDecimal(dgdqmap.get(deptMap.get(deptids[i]))));
			}
			if (dshqmap.get(deptMap.get(deptids[i])) != null) {
				db = db.add(new BigDecimal(dshqmap.get(deptMap.get(deptids[i]))));
			}
			if (dsdqmap.get(deptMap.get(deptids[i])) != null) {
				db = db.add(new BigDecimal(dsdqmap.get(deptMap.get(deptids[i]))));
			}
			map.put(deptMap.get(deptids[i]), db.toString());
		}

		return map;
	}

	/**
	 * �Թ����ڲ����վ����-���ʵ��Map getDGDQRJNCSD �³����� ��ĩ���� ���µ��³����� ���µ���ĩ���� ����³����� ��ĩ����
	 * �������� �������� ������� ��������ǡ�������ڡ�
	 */
	public HashMap<String, String> getDGDQRJNCSD(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] time = date.split(";");
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select c.code code,a.acct_bal acct_bal from(select oact_inst_no oact_inst_no, sum(acct_bal) as acct_bal  from wnbank.a_agr_dep_acct_ent_fx where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ time[5]
									+ "' group by oact_inst_no) a left join excel_tab_28 b on a.oact_inst_no=b.a left join pub_corp_dept c on substr(b.b,12)=c.name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * �Թ������ڲ����վ����-���ʵ��Map getDGHQBMRJNC �³����� ��ĩ���� ���µ��³����� ���µ���ĩ���� ����³����� ��ĩ����
	 * �������� �������� �������
	 */
	public HashMap<String, String> getDGHQBMRJNC(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] time = date.split(";");
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select c.code code,a.acct_bal acct_bal from(select oact_inst_no, sum(acct_bal) as acct_bal  from wnbank.a_agr_dep_acct_ent_sv where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5]
									+ "' group by oact_inst_no) a left join excel_tab_28 b on a.oact_inst_no=b.a left join pub_corp_dept c on substr(b.b,12)=c.name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * ��˽���ڲ����վ����-���ʵ��Map getDSDQBMRJNC �³����� ��ĩ���� ���µ��³����� ���µ���ĩ���� ����³����� ��ĩ����
	 * �������� �������� �������
	 */
	public HashMap<String, String> getDSDQBMRJNC(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] time = date.split(";");
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select c.code code,a.acct_bal acct_bal from(select oact_inst_no, sum(acct_bal) as acct_bal  from wnbank.a_agr_dep_acct_psn_fx where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5]
									+ "' group by oact_inst_no) a left join excel_tab_28 b on a.oact_inst_no=b.a left join pub_corp_dept c on substr(b.b,12)=c.name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * ��˽�����ڲ����վ����-���ʵ��Map getDSHQBMRJNC �³����� ��ĩ���� ���µ��³����� ���µ���ĩ���� ����³����� ��ĩ����
	 * �������� �������� �������
	 */
	public HashMap<String, String> getDSHQBMRJNC(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] time = date.split(";");
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select c.code code,a.acct_bal acct_bal from(select oact_inst_no, sum(f) as acct_bal  from wnbank.a_agr_dep_acct_psn_sv where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5]
									+ "' group by oact_inst_no) a left join excel_tab_28 b on a.oact_inst_no=b.a left join pub_corp_dept c on substr(b.b,12)=c.name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
