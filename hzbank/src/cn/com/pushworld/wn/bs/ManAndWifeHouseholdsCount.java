package cn.com.pushworld.wn.bs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 
 * @author zzl
 * 
 *         2019-3-25-����05:23:44 ������Ч����
 *         [�����³�����]+";"+[���˵���ĩ����]+";"+[�����³�����]+";"+
 *         [����ָ��ĩ����]+";"+[����³�����]+";"+[�����ĩ����]+";"+[����]
 */
public class ManAndWifeHouseholdsCount {
	private UIUtil uiutil = new UIUtil();
	private CommDMO dmo = new CommDMO();
	private YearManAndWifeHouseholdsCount year = new YearManAndWifeHouseholdsCount();// ����Ļ���
	private MonthManAndWifeHouseholdsCount month = new MonthManAndWifeHouseholdsCount();// ���µĻ���
	private HashVO[] vo = null;
	private String day = null;

	/**
	 * ����ͱȽ���Ч���� �Ѵ���80�ĺ�С��80�����ȴ浽һ�ű���
	 * 
	 * @return
	 */
	public HashMap<String, String> getComputeMap(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] time = date.split(";");
		day = time[6].toString();
		try {

			vo = dmo.getHashVoArrayByDS(
					null,
					"select code,name from v_sal_personinfo where STATIONKIND in ('����ͻ�����','�����ͻ�����','�������㸱����','�������㸱����','�����μ�ְ�ͻ�����')");
			HashMap<String, String> yearmap = year.getYearCount(date, vo); // ����Ļ���
			HashMap<String, String> monthmap = month.getYearCount(date, vo); // ���µĻ���
			// UpdateSQLBuilder update=new UpdateSQLBuilder("WN_RJ_CKYXHSTJ");
			// InsertSQLBuilder insert=new
			// InsertSQLBuilder("WN_CKYXHSTJ");//��¼�ѼƷ���δ�Ʒ���
			InsertSQLBuilder insert = new InsertSQLBuilder("WN_deposit_number");// ��¼���������ɱ���
			InsertSQLBuilder insertHs = new InsertSQLBuilder("wn_ck_count");// ��¼����
			HashVO[] hsvo = dmo.getHashVoArrayByDS(
					null,
					"select * from wn_ck_count where date_time='"
							+ time[1].toString() + "'");
			if (hsvo.length > 0) {
				dmo.executeUpdateByDS(
						null,
						"delete from wn_ck_count where date_time='"
								+ time[1].toString() + "'");
			}
			List list = new ArrayList<String>();
			HashVO[] tjvos = dmo.getHashVoArrayByDS(null,
					"select * from WN_CKYXHSTJ where E='" + time[1].toString()
							+ "'");
			if (tjvos.length > 0) {
				dmo.executeUpdateByDS(null, "delete from WN_CKYXHSTJ where E='"
						+ time[1].toString() + "'");
			}
			HashVO[] ckbi = dmo.getHashVoArrayByDS(null,
					"select * from WN_deposit_number where date_time='"
							+ time[1].toString() + "'");
			if (ckbi.length > 0) {
				dmo.executeUpdateByDS(null,
						"delete from WN_deposit_number where date_time='"
								+ time[1].toString() + "'");
			}
			String[] name = dmo.getStringArrayFirstColByDS(null,
					"select name from wn_deposit_detail where date_time='"
							+ date + "'");
			if (name.length > 0) {
				dmo.executeUpdateByDS(null,
						"delete from wn_deposit_detail where date_time='"
								+ date + "'");
			}
			// �õ��ͻ������������
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(null,
					"select A,sum(B) from EXCEL_TAB_53 where year='"
							+ time[1].toString().substring(0, 4)
							+ "' group by A");
			// �ѼƷ�jfmap
			HashMap<String, String> jfmap = dmo.getHashMapBySQLByDS(null,
					"select B,sum(C) C from WN_CKYXHSTJ  group by B");
			// δ�Ʒ�
			HashMap<String, String> Wjfmap = dmo.getHashMapBySQLByDS(null,
					"select B,sum(D) D from WN_CKYXHSTJ group by B");
			HashMap<String, String> countMap = getCount(date);// ��ӵ�map
			map = getCount(date);
			for (int i = 0; i < vo.length; i++) {
				// String id=dmo.getSequenceNextValByDS(null,"S_WN_CKYXHSTJ");
				int a, b, c = 0;// a==�����»��� b===���»��� c===�������
				if (countMap.get(vo[i].getStringValue("name")) == null) {
					a = 0;
				} else {
					a = Integer.parseInt(countMap.get(vo[i]
							.getStringValue("name")));
				}
				if (monthmap.get(vo[i].getStringValue("name")) == null) {
					b = 0;
				} else {
					b = Integer.parseInt(monthmap.get(
							vo[i].getStringValue("name")).toString());
				}
				if ((a - b) > 80) {
					map.put(vo[i].getStringValue("name"), "80");
				} else if ((a - b) < -80) {
					map.put(vo[i].getStringValue("name"), "-80");
				} else {
					map.put(vo[i].getStringValue("name"), String.valueOf(a - b));
				}
				if (yearmap.get(vo[i].getStringValue("name")) == null) {
					c = 0;
				} else {
					c = Integer.parseInt(yearmap.get(
							vo[i].getStringValue("name")).toString());
				}
				insert.putFieldValue("name", vo[i].getStringValue("name"));
				insert.putFieldValue("passed", a - c);
				insert.putFieldValue("task",
						rwMap.get(vo[i].getStringValue("name")));
				insert.putFieldValue("date_time", time[1].toString());
				list.add(insert.getSQL());
				// insert.putFieldValue("id",id);
				// insert.putFieldValue("A",vo[i].getStringValue("code"));
				// insert.putFieldValue("B",vo[i].getStringValue("name"));
				// insert.putFieldValue("E",time[1].toString());
				// if(jfmap.size()>0){
				// int
				// count=Integer.parseInt(jfmap.get(vo[i].getStringValue("name")));//�ѼƷ���Map
				// int
				// wcount=Integer.parseInt(Wjfmap.get(vo[i].getStringValue("name")));//δ�Ʒ���Map
				// if(((a-b)-count+wcount)>80){
				// insert.putFieldValue("D",((a-b)-count+wcount)-80);//û�мƷ���
				// }else if(((a-b)-count+wcount)<-80){
				// insert.putFieldValue("D",((a-b)-count+wcount)-(-80));//û�мƷ���
				// }else{
				// insert.putFieldValue("C",((a-b)-count+wcount));//�ѼƷ���
				// insert.putFieldValue("D","0");//�ѼƷ���
				// }
				// map.put(vo[i].getStringValue("name"),
				// String.valueOf(((a-b)-count+wcount)));
				// }else{
				// if((a-b)>80){
				// insert.putFieldValue("D",(a-b)-80);//û�мƷ���
				// }else if((a-b)<-80){
				// insert.putFieldValue("D",(a-b)-(-80));//û�мƷ���
				// }else{
				// insert.putFieldValue("C",(a-b));//�ѼƷ���
				// insert.putFieldValue("D","0");//�ѼƷ���
				// }
				// map.put(vo[i].getStringValue("name"), String.valueOf((a-b)));
				// }
				// list.add(insert.getSQL());

			}
			// if(jfmap.size()>0){
			// update.setWhereCondition("E='"+time[3].toString()+"'");
			// update.putFieldValue("D","0");
			// list.add(update.getSQL());
			// }

			// for(String str:yearmap.keySet()){
			// System.out.println(""+str+"�������"+yearmap.get(str));
			// }
			// for(String str:countMap.keySet()){
			// System.out.println(""+str+"�����»���"+countMap.get(str));
			// }
			// zzl �ѻ�����¼��һ������
			for (String str : map.keySet()) {
				insertHs.putFieldValue("username", str);
				insertHs.putFieldValue("counths", map.get(str));
				insertHs.putFieldValue("date_time", time[1].toString());
				list.add(insertHs.getSQL());
			}
			dmo.executeBatchByDS(null, list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * �����еĻ�����ӡ�
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getCount(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] time = date.split(";");
		HashMap<String, String> DSHQDSMap = getDSHQDS(date);
		HashMap<String, String> DSHQFQMap = getDSHQFQ(date);
		HashMap<String, String> DSDQDSMap = getDSDQDS(date);
		HashMap<String, String> DSDQFQMap = getDSDQFQ(date);
		HashMap<String, String> DGHQMap = getDGHQ(date);
		HashMap<String, String> DGDQMap = getDGDQ(date);
		try {
			for (int i = 0; i < vo.length; i++) {
				int a = 0;
				if (DSHQDSMap.get(vo[i].getStringValue("name")) != null) {
					a = Integer.parseInt(DSHQDSMap.get(
							vo[i].getStringValue("name")).toString());
				}
				if (DSHQFQMap.get(vo[i].getStringValue("name")) != null) {
					a = a
							+ Integer.parseInt(DSHQFQMap.get(
									vo[i].getStringValue("name")).toString());
				}
				if (DSDQDSMap.get(vo[i].getStringValue("name")) != null) {
					a = a
							+ Integer.parseInt(DSDQDSMap.get(vo[i]
									.getStringValue("name")));
				}
				if (DSDQFQMap.get(vo[i].getStringValue("name")) != null) {
					a = a
							+ Integer.parseInt(DSDQFQMap.get(vo[i]
									.getStringValue("name")));
				}
				if (DGHQMap.get(vo[i].getStringValue("code")) != null) {
					a = a
							+ Integer.parseInt(DGHQMap.get(vo[i]
									.getStringValue("code")));
				}
				if (DGDQMap.get(vo[i].getStringValue("code")) != null) {
					a = a
							+ Integer.parseInt(DGDQMap.get(vo[i]
									.getStringValue("code")));
				}
				map.put(vo[i].getStringValue("name"), String.valueOf(a));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;

	}

	/**
	 * ---��˽���ڴ�� ����
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getDSHQDS(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2 xd_col2,count(yb.xd_col2) tj from(select c.xd_col96 as xd_col96,sum(a.f) as f from (  select  cust_no, (sum(f)/"
									+ day
									+ ") as f  from wnbank.a_agr_dep_acct_psn_sv where f > 100  and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "'  group by cust_no) a left join wnbank.S_OFCR_CI_CUSTMAST b on a.cust_no = b.COD_CUST_ID left join wnbank.S_LOAN_KHXX c on b.EXTERNAL_CUSTOMER_IC = c.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on c.XD_COL1 = gl.XD_COL1 where c.XD_COL32 > 1 and gl.xd_col3<>'01' and a.f>5000 group by c.xd_col7,c.xd_col96 )ffq left join wnbank.S_LOAN_RYB yb on ffq.XD_COL96=yb.xd_col1 group  by yb.xd_col2");
			String[][] data = dmo
					.getStringArrayByDS(
							null,
							"select c.xd_col7,c.xd_col5,c.xd_col96 as xd_col96,sum(a.acct_bal) as f from (select  cust_no, (sum(f)/"
									+ day
									+ ") as acct_bal  from wnbank.a_agr_dep_acct_psn_sv where f > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CI_CUSTMAST b on a.cust_no = b.COD_CUST_ID left join wnbank.S_LOAN_KHXX c on b.EXTERNAL_CUSTOMER_IC = c.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on c.XD_COL1 = gl.XD_COL1 where c.XD_COL32 > 1 and gl.xd_col3<>'01' and a.acct_bal>5000 group by c.xd_col5,c.xd_col7,c.xd_col96 ");
			getInsertSql(null, data, time[1].toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>>>>��˽���ڴ�� ����>>>>>>>>>>>>");
		for (String str : map.keySet()) {
			System.out.println(">>>>>>>" + str + ">>>>>>>>" + map.get(str));
		}
		return map;
	}

	/**
	 * ---��˽���ڴ�� ����
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getDSHQFQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String[][] count = dmo
					.getStringArrayByDS(
							null,
							"select xj.BAL_BOOK_AVG_M,xj.XD_COL7,yb.XD_COL2,xj.xd_col5,xj.khname,yb.XD_COL1 from(select sum(ck.BAL_BOOK_AVG_M) BAL_BOOK_AVG_M,xx.XD_COL7 XD_COL7,xx.XD_COL96 XD_COL96,gl.xd_col5 xd_col5,xx.xd_col5 khname from (select cust_no cust_no,(sum(f)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.a_agr_dep_acct_psn_sv  where f > 100  and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) ck  left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7  left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01' group by xx.xd_col7,xx.xd_col96,gl.xd_col5,xx.xd_col5) xj left join wnbank.S_LOAN_RYB yb on xj.XD_COL96=yb.xd_col1");
			HashMap<String, String> resultMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select xx.XD_COL7,ck.BAL_BOOK_AVG_M BAL_BOOK_AVG_M from (select cust_no cust_no,(sum(f)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.a_agr_dep_acct_psn_sv where f > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01'");
			List list = new ArrayList<String[]>();
			for (int i = 0; i < count.length; i++) {
				Double tj = 0.0;
				int a = 0;
				String[] str = new String[3];
				if (resultMap.get(count[i][3].toString()) != null) {
					tj = Double.parseDouble(count[i][0].toString())
							+ Double.parseDouble(resultMap.get(count[i][3]
									.toString()));
					if (tj > 5000) {
						a = a + 1;
						if (map.get(count[i][2].toString()) != null) {
							a = a
									+ Integer.parseInt(map.get(count[i][2]
											.toString()));
						}
						map.put(count[i][2].toString(), String.valueOf(a));
						str[0] = count[i][1].toString();
						str[1] = count[i][4].toString();
						str[2] = count[i][5].toString();
						list.add(str);
					}
				} else {
					if (Double.parseDouble(count[i][0].toString()) > 5000) {
						a = a + 1;
						if (map.get(count[i][2].toString()) != null) {
							a = a
									+ Integer.parseInt(map.get(count[i][2]
											.toString()));
						}
						map.put(count[i][2].toString(), String.valueOf(a));
						str[0] = count[i][1].toString();
						str[1] = count[i][4].toString();
						str[2] = count[i][5].toString();
						list.add(str);
					}
				}

			}
			getInsertSql(list, null, time[1].toString());
			System.out.println(">>>>>>>>>>>��˽���ڴ�� ����>>>>>>>>>>>>");
			for (String str : map.keySet()) {
				System.out.println(">>>>>>>" + str + ">>>>>>>>" + map.get(str));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}

	/**
	 * ---��˽���ڶ��� ����
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getDSDQDS(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2 xd_col2,count(yb.xd_col2) tj from(select c.xd_col96 as xd_col96,sum(a.acct_bal) as f from (select  cust_no, (sum(acct_bal)/"
									+ day
									+ ") as acct_bal  from wnbank.A_AGR_DEP_ACCT_PSN_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CI_CUSTMAST b on a.cust_no = b.COD_CUST_ID left join wnbank.S_LOAN_KHXX c on b.EXTERNAL_CUSTOMER_IC = c.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on c.XD_COL1 = gl.XD_COL1 where c.XD_COL32 > 1 and gl.xd_col3<>'01' and a.acct_bal>5000 group by c.xd_col7,c.xd_col96 )ffq left join wnbank.S_LOAN_RYB yb on ffq.XD_COL96=yb.xd_col1 group  by yb.xd_col2");
			String[][] data = dmo
					.getStringArrayByDS(
							null,
							"select c.xd_col7,c.xd_col5,c.xd_col96 as xd_col96,sum(a.acct_bal) as f from (select  cust_no, (sum(acct_bal)/"
									+ day
									+ ") as acct_bal  from wnbank.A_AGR_DEP_ACCT_PSN_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CI_CUSTMAST b on a.cust_no = b.COD_CUST_ID left join wnbank.S_LOAN_KHXX c on b.EXTERNAL_CUSTOMER_IC = c.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on c.XD_COL1 = gl.XD_COL1 where c.XD_COL32 > 1 and gl.xd_col3<>'01' and a.acct_bal>5000 group by c.xd_col5,c.xd_col7,c.xd_col96 ");
			getInsertSql(null, data, time[1].toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(">>>>>>��˽���ڶ��� ����>>>>>>>>>");
		for (String str : map.keySet()) {
			System.out.println(">>>>>>>" + str + ">>>>>>>>" + map.get(str));
		}
		return map;
	}

	/**
	 * ---��˽���ڴ�� ����
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getDSDQFQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String[][] count = dmo
					.getStringArrayByDS(
							null,
							"select xj.BAL_BOOK_AVG_M,xj.XD_COL7,yb.XD_COL2,xj.xd_col5,xj.khname,yb.XD_COL1 from (select sum(ck.BAL_BOOK_AVG_M) BAL_BOOK_AVG_M,xx.XD_COL7 XD_COL7,xx.XD_COL96 XD_COL96,gl.xd_col5 xd_col5,xx.xd_col5 khname from (select cust_no cust_no,(sum(acct_bal)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.A_AGR_DEP_ACCT_PSN_FX  where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01' group by xx.xd_col7,xx.xd_col96,gl.xd_col5,xx.xd_col5)  xj left join wnbank.S_LOAN_RYB yb on xj.XD_COL96=yb.xd_col1");
			HashMap<String, String> resultMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select xx.XD_COL7,ck.BAL_BOOK_AVG_M BAL_BOOK_AVG_M from (select cust_no cust_no,(sum(acct_bal)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.A_AGR_DEP_ACCT_PSN_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01'");
			List list = new ArrayList<String[]>();
			for (int i = 0; i < count.length; i++) {
				Double tj = 0.0;
				int a = 0;
				String[] str = new String[3];
				if (resultMap.get(count[i][3].toString()) != null) {
					tj = Double.parseDouble(count[i][0].toString())
							+ Double.parseDouble(resultMap.get(count[i][3]
									.toString()));
					if (tj > 5000) {
						a = a + 1;
						if (map.get(count[i][2].toString()) != null) {
							a = a
									+ Integer.parseInt(map.get(count[i][2]
											.toString()));
						}
						map.put(count[i][2].toString(), String.valueOf(a));
						str[0] = count[i][1].toString();
						str[1] = count[i][4].toString();
						str[2] = count[i][5].toString();
						list.add(str);
					}
				} else {
					if (Double.parseDouble(count[i][0].toString()) > 5000) {
						a = a + 1;
						if (map.get(count[i][2].toString()) != null) {
							a = a
									+ Integer.parseInt(map.get(count[i][2]
											.toString()));
						}
						map.put(count[i][2].toString(), String.valueOf(a));
						str[0] = count[i][1].toString();
						str[1] = count[i][4].toString();
						str[2] = count[i][5].toString();
						list.add(str);
					}
				}

			}
			getInsertSql(list, null, time[1].toString());
			System.out.println(">>>>>>��˽���ڴ�� ����>>>>>>>>>");
			for (String str : map.keySet()) {
				System.out.println(">>>>>>>" + str + ">>>>>>>>" + map.get(str));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * ---�Թ�������
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getDGHQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select aaa.CUS_MANAGER count1,count(aaa.CUS_MANAGER) tj from(select distinct(a.cust_no),c.CUS_NAME,c.CUS_MANAGER CUS_MANAGER from (select  cust_no cust_no, (sum(acct_bal)/"
									+ day
									+ ") as acct_bal  from wnbank.A_AGR_DEP_ACCT_ENT_SV where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CH_ACCT_MAST b on a.cust_no = b.COD_CUST left join (select * from wnbank.S_CMIS_ACC_LOAN where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ time[1].toString()
									+ "') c on b.NAM_CUST_SHRT = c.CUS_NAME where c.LOAN_BALANCE > 0 and a.acct_bal>5000) aaa group by aaa.CUS_MANAGER");
			String[][] data = dmo
					.getStringArrayByDS(
							null,
							"select distinct(a.cust_no),c.CUS_NAME,c.CUS_MANAGER CUS_MANAGER from (select  cust_no cust_no, (sum(acct_bal)/to_number(to_char(sysdate,'dd'))) as acct_bal  from wnbank.A_AGR_DEP_ACCT_ENT_SV where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CH_ACCT_MAST b on a.cust_no = b.COD_CUST left join (select * from wnbank.S_CMIS_ACC_LOAN where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ time[1].toString()
									+ "') c on b.NAM_CUST_SHRT = c.CUS_NAME where c.LOAN_BALANCE > 0 and a.acct_bal>5000");
			getInsertSql(null, data, time[1].toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(">>>>>>�Թ�������>>>>>>>>>");
		for (String str : map.keySet()) {
			System.out.println(">>>>>>>" + str + ">>>>>>>>" + map.get(str));
		}
		return map;
	}

	/**
	 * ---�Թ�����
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getDGDQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select c.CUS_MANAGER as CUS_MANAGER,count(c.CUS_MANAGER) tj from (select  cust_no, (sum(acct_bal)/"
									+ day
									+ ") as acct_bal  from wnbank.A_AGR_DEP_ACCT_ENT_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CH_ACCT_MAST b on a.cust_no = b.COD_CUST left join wnbank.S_CMIS_ACC_LOAN c on b.NAM_CUST_SHRT = c.CUS_NAME where c.LOAN_BALANCE > 0 and a.acct_bal>5000 group by c.cus_id,c.CUS_MANAGER");
			String[][] data = dmo
					.getStringArrayByDS(
							null,
							"select distinct(a.cust_no),c.CUS_NAME,c.CUS_MANAGER CUS_MANAGER from (select  cust_no cust_no, (sum(acct_bal)/"
									+ day
									+ ") as acct_bal  from wnbank.A_AGR_DEP_ACCT_ENT_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CH_ACCT_MAST b on a.cust_no = b.COD_CUST left join (select * from wnbank.S_CMIS_ACC_LOAN where to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ time[1].toString()
									+ "') c on b.NAM_CUST_SHRT = c.CUS_NAME where c.LOAN_BALANCE > 0 and a.acct_bal>5000");
			getInsertSql(null, data, time[1].toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(">>>>>>�Թ�����>>>>>>>>>");
		for (String str : map.keySet()) {
			System.out.println(">>>>>>>" + str + ">>>>>>>>" + map.get(str));
		}
		return map;
	}

	/**
	 * zzl [������Ч������ϸ���������ϸ��ѯʹ��]
	 * 
	 * @param list
	 *            ������Ч������ϸ
	 * @param data
	 *            �Ƿ�����Ч������ϸ
	 * @param date
	 *            ����
	 */
	public void getInsertSql(List<String[]> list, String[][] data, String date) {
		try {
			List sqllist = new ArrayList<String>();
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_deposit_detail");
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					String[] str = list.get(i);
					insert.putFieldValue("Idno", str[0].toString());
					insert.putFieldValue("name", str[1].toString());
					insert.putFieldValue("khjlid", str[2].toString());
					insert.putFieldValue("date_time", date);
					sqllist.add(insert.getSQL());
				}
			}
			if (data != null) {
				for (int i = 0; i < data.length; i++) {
					insert.putFieldValue("Idno", data[i][0].toString());
					insert.putFieldValue("name", data[i][1].toString());
					insert.putFieldValue("khjlid", data[i][2].toString());
					insert.putFieldValue("date_time", date);
					sqllist.add(insert.getSQL());
				}
			}
			dmo.executeBatchByDS(null, sqllist);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
