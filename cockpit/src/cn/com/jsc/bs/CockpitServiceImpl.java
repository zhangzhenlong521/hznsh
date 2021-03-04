package cn.com.jsc.bs;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.jsc.ui.CockpitServiceIfc;
import com.ibm.db2.jcc.t2zos.e;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CockpitServiceImpl implements CockpitServiceIfc {
    CommDMO dmo=new CommDMO();

    /**
     * zzl 各项存款统计总额 较年初
     * @return
     */
    @Override
    public String [][] getCkStatistical() {
        String [][] data=null;
        try {
            data= dmo.getStringArrayByDS(null,"select to_char(dyck.dy,'fm999999990.00') dyye,to_char(dyck.dy-ncck.nc,'fm999999990.00') ncye from(\n" +
                    "select '1' a,ROUND(sum(F)/100000000,2) dy from(\n" +
                    "select sum(f) f \n" +
                    "from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and f>0 \n" +
                    "union all \n" +
                    "select sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0))dyck\n" +
                    "left join(\n" +
                    "select '1' a,ROUND(sum(F)/100000000,2) nc from(\n" +
                    "select sum(f) f \n" +
                    "from hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and f>0 \n" +
                    "union all \n" +
                    "select sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_sv_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and acct_bal>0)) ncck\n" +
                    "on dyck.a=ncck.a");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String[][] getCKHsCount() {
        String [][] data=null;
        try{
//            data= dmo.getStringArrayByDS(null,"select to_char(dy.num,'fm999999990.00'),to_char(dy.num-nc.num,'fm999999990.00') from(\n" +
//                    "select '1' a,count(dy.num)/10000 num from(\n" +
//                    "select b.EXTERNAL_CUSTOMER_IC num from(\n" +
//                    "select cust_no,sum(F) f from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' group by cust_no \n" +
//                    "union all\n" +
//                    "select cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' group by cust_no)\n" +
//                    "a left join (select * from hzbank.S_OFCR_CI_CUSTMAST_"+getqytMonth()+" where  load_dates='"+getQYTTime()+"') b on a.cust_no = b.COD_CUST_ID group by b.EXTERNAL_CUSTOMER_IC having sum(a.f)>1000) dy) dy\n" +
//                    "left join(\n" +
//                    "select '1' a,count(nc.num)/10000 num from(\n" +
//                    "select b.EXTERNAL_CUSTOMER_IC num from(\n" +
//                    "select cust_no,sum(F) f from hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+" group by cust_no \n" +
//                    "union all\n" +
//                    "select cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+" group by cust_no)\n" +
//                    "a left join hzbank.S_OFCR_CI_CUSTMAST_"+getYearYmTime()+" b on a.cust_no = b.COD_CUST_ID group by b.EXTERNAL_CUSTOMER_IC having sum(a.f)>1000) nc)\n" +
//                    "nc on dy.a=nc.a");
			data=dmo.getStringArrayByDS(null,"select to_char(dy.num,'fm999999990.00'),to_char(dy.num-nc.num,'fm999999990.00') from(\n" +
					"select '1' a,count(count(EXTERNAL_CUSTOMER_IC))/10000 num from(\n" +
					"select b.EXTERNAL_CUSTOMER_IC,sum(a.f) f from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" a left join hzbank.S_OFCR_CI_CUSTMAST_"+getqytMonth()+" b on a.cust_no=b.COD_CUST_ID\n" +
					"where a.biz_dt='"+getQYTTime()+"' and b.load_dates='"+getQYTTime()+"' group by EXTERNAL_CUSTOMER_IC having sum(a.f)>1000\n" +
					"union all\n" +
					"select b.EXTERNAL_CUSTOMER_IC,sum(a.acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" a left join hzbank.S_OFCR_CI_CUSTMAST_"+getqytMonth()+" b on a.cust_no=b.COD_CUST_ID\n" +
					"where a.biz_dt='"+getQYTTime()+"' and b.load_dates='"+getQYTTime()+"' group by EXTERNAL_CUSTOMER_IC having sum(a.acct_bal)>1000)\n" +
					"group by EXTERNAL_CUSTOMER_IC) dy\n" +
					"left join\n" +
					"(select '1' a,count(nc.num)/10000 num from(\n" +
					"select b.EXTERNAL_CUSTOMER_IC num from(\n" +
					"select cust_no,sum(F) f from hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+" group by cust_no \n" +
					"union all\n" +
					"select cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+" group by cust_no)\n" +
					"a left join hzbank.S_OFCR_CI_CUSTMAST_"+getYearYmTime()+" b on a.cust_no = b.COD_CUST_ID group by b.EXTERNAL_CUSTOMER_IC having sum(a.f)>1000)nc\n" +
					")nc on dy.a=nc.a");
        }catch (Exception e){
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public String[][] getCKGeRenCount() {
        String [][] data=null;
        try{
            data= dmo.getStringArrayByDS(null,"select to_char(ROUND(dy.f,2),'fm999999990.00'),to_char(ROUND(dy.f-nc.f,2),'fm999999990.00') from(\n" +
                    "select '1' a,sum(F)/100000000 f from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"') dy\n" +
                    "left join(\n" +
                    "select '1' a,sum(F)/100000000 f from hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+") nc on dy.a=nc.a");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String[][] getCKGeRenDQCount() {
        String [][] data=null;
        try{
            data= dmo.getStringArrayByDS(null,"select to_char(ROUND(dy.f,2),'fm999999990.00'),to_char(ROUND(dy.f-nc.f,2),'fm999999990.00') from(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"') dy\n" +
                    "left join(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+") nc on dy.a=nc.a");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String[][] getCKDgHqCount() {
        String [][] data=null;
        try{
            data= dmo.getStringArrayByDS(null,"select to_char(ROUND(dy.f,2),'fm999999990.00'),to_char(ROUND(dy.f-nc.f,2),'fm999999990.00') from(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.a_agr_dep_acct_ent_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"') dy\n" +
                    "left join(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.a_agr_dep_acct_ent_sv_"+getYearYmTime()+") nc on dy.a=nc.a");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String[][] getCKDgDqCount() {
        String [][] data=null;
        try{
            data= dmo.getStringArrayByDS(null,"select to_char(ROUND(dy.f,2),'fm999999990.00'),to_char(ROUND(dy.f-nc.f,2),'fm999999990.00') from(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.a_agr_dep_acct_ent_fx_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"') dy\n" +
                    "left join(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.a_agr_dep_acct_ent_fx_"+getYearYmTime()+") nc on dy.a=nc.a");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    /**
     * zzl 得到前一天的月份
     * @return
     */
    public String getqytMonth(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
	/**
	 * zzl 得到当前月份
	 * @return
	 */
	public String getdytMonth(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("MM");
		Date d = new Date();
		cal.setTime(d);
		int day = cal.get(Calendar.DATE);
		cal.set(Calendar.DATE, day - 1);
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}

    /**
     * 得到前一天的日期
     *
     * @return
     */
    public String getQYTTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     * 得到年初月末日期
     *
     * @return
     */
    public String getYearYmTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date();
        cal.setTime(d);
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.DAY_OF_YEAR,cal.getActualMaximum(Calendar.DAY_OF_YEAR));//最后一天
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }

	/**
	 * 得到年初月份 前一天的
	 *
	 * @return
	 */
	public String getYearYm() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		Date d = new Date();
		cal.setTime(d);
		cal.add(Calendar.DATE, -1);
		cal.setTime(cal.getTime());
		cal.add(Calendar.YEAR, -1);
		cal.set(Calendar.MONTH,cal.getActualMaximum(Calendar.MONTH));
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}
	/**
	 * 得到当前月末日期
	 *
	 * @return
	 */
	public String getMonthMaxDay() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date d = new Date();
		cal.setTime(d);
		cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));//最后一天
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}

	/**
	 *
	 *zzl 获得上个月的年份
	 * @param
	 */
	public String getSmonthtYear() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		Date d = new Date();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_YEAR,cal.getActualMaximum(Calendar.DAY_OF_YEAR));//最后一天
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}
	/**
	 *
	 *zzl 获得当前年份
	 * @param
	 */
	public String getDayYear() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		Date d = new Date();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -0);
		cal.set(Calendar.DAY_OF_YEAR,cal.getActualMaximum(Calendar.DAY_OF_YEAR));//最后一天
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}
	public static void main(String[] args) {
        CockpitServiceImpl cp=new CockpitServiceImpl();
//		String [] []data=cp.getqjData(3);
//		for(int i=0;i<data.length;i++){
//			System.out.println(">>>>>>>>>"+data[i][0]);
//		}
		System.out.println(">>>>>>>>>>>>>>>"+cp.getMonthTime("202101"));
    }

	@Override
	public int getCurYearQnyhs() {
		int result=0;
		// 获取到表名，判断表名是否存在
		String tableName="S_LOAN_QNYYX";
		List<String> tableList = getTableList("HZDB", tableName);
		String sql="";
		if(tableList.contains(tableName+"_"+getCurMonth().replace("-",""))){ // 判断当前存在表
			sql="SELECT count(*) FROM (SELECT c, e,f,TO_NUMBER(REPLACE(n,',',''))+TO_NUMBER(REPLACE(o,',',''))+TO_NUMBER(REPLACE(P,',',''))+TO_NUMBER(REPLACE(Q,',',''))+TO_NUMBER(REPLACE(R,',',''))+TO_NUMBER(REPLACE(S,',',''))+TO_NUMBER(REPLACE(T,',',''))+TO_NUMBER(REPLACE(U,',',''))+TO_NUMBER(REPLACE(V,',',''))+TO_NUMBER(REPLACE(w,',',''))+TO_NUMBER(REPLACE(x,',',''))+TO_NUMBER(REPLACE(Y,',',''))+TO_NUMBER(REPLACE(Z,',',''))+TO_NUMBER(REPLACE(aa,',',''))+TO_NUMBER(REPLACE(ab,',',''))+TO_NUMBER(REPLACE(ac,',',''))+TO_NUMBER(REPLACE(ad,',',''))   num   FROM hzdb.S_LOAN_QNYYX_"+getCurMonth().replace("-", "")+"  WHERE i>='"+getCurYear()+"-01-01 00:00:00' ) WHERE num >0";	
		}else{
			if(tableList.size()==0){
				return 0;
			}else {
				sql="SELECT count(*) FROM (SELECT c, e,f,TO_NUMBER(REPLACE(n,',',''))+TO_NUMBER(REPLACE(o,',',''))+TO_NUMBER(REPLACE(P,',',''))+TO_NUMBER(REPLACE(Q,',',''))+TO_NUMBER(REPLACE(R,',',''))+TO_NUMBER(REPLACE(S,',',''))+TO_NUMBER(REPLACE(T,',',''))+TO_NUMBER(REPLACE(U,',',''))+TO_NUMBER(REPLACE(V,',',''))+TO_NUMBER(REPLACE(w,',',''))+TO_NUMBER(REPLACE(x,',',''))+TO_NUMBER(REPLACE(Y,',',''))+TO_NUMBER(REPLACE(Z,',',''))+TO_NUMBER(REPLACE(aa,',',''))+TO_NUMBER(REPLACE(ab,',',''))+TO_NUMBER(REPLACE(ac,',',''))+TO_NUMBER(REPLACE(ad,',',''))   num   FROM hzdb."+tableList.get(tableList.size()-1)+"  WHERE i>='"+getCurYear()+"-01-01 00:00:00' ) WHERE num >0";
			}
		}
		try{
			String num = dmo.getStringValueByDS(null, sql);
			result=Integer.parseInt(num);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public String getCurMonth(){
		SimpleDateFormat formate=new SimpleDateFormat("yyyy-MM");
		String curMonth = formate.format(new Date());
		return curMonth;
	}
	
	public String getCurYear(){
		SimpleDateFormat formate=new SimpleDateFormat("yyyy");
		String curYear= formate.format(new Date());
		return curYear;
	}

	@Override
	public String getCurrYearQnyhyl() {
		String result="";
		try {
			result=dmo.getStringValueByDS(null,"select to_char(ROUND(yx.num/zj.num*100,2),'fm9999990.00')||'%' from(\n" +
					"select '1' code,sum(num) num from(\n" +
					"select F,count(F) num from hzdb.s_loan_qnyyx_"+getDateZdy("yyyyMM")+" where I>='"+getDateZdy("yyyy")+"-01-01 00:00:00' and I<='"+getDateZdy("yyyy")+"-12-31 00:00:00' group by F\n" +
					"  having sum(REPLACE(N,',',''))+sum(REPLACE(O,',',''))+sum(REPLACE(P,',',''))+sum(REPLACE(Q,',',''))+sum(REPLACE(R,',',''))+\n" +
					"  sum(REPLACE(S,',',''))+sum(REPLACE(T,',',''))+sum(REPLACE(U,',',''))+sum(REPLACE(V,',',''))+sum(REPLACE(W,',',''))+sum(REPLACE(X,',',''))+\n" +
					"  sum(REPLACE(Y,',',''))+sum(REPLACE(Z,',',''))+sum(REPLACE(AA,',',''))+sum(REPLACE(AB,',',''))+sum(REPLACE(AC,',',''))+sum(REPLACE(AD,',',''))>0\n" +
					")) yx\n" +
					"left join(\n" +
					"select '1' code,sum(num) num from(\n" +
					"select count(*) num from(\n" +
					"select f from hzdb.s_loan_qnyyx_"+getDateZdy("yyyyMM")+" where I>='"+getDateZdy("yyyy")+"-01-01 00:00:00' and I<='"+getDateZdy("yyyy")+"-12-31 00:00:00' group by F)\n" +
					"union all\n" +
					"select count(*) num from(\n" +
					"select g from hzdb.s_loan_qnywx_"+getDateZdy("yyyyMM")+" where J>='"+getDateZdy("yyyy")+"-01-01 00:00:00' and J<='"+getDateZdy("yyyy")+"-12-31 00:00:00' group by G))) zj on yx.code=zj.code");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * zpy 根据指定的表空间和表名，来获取对应表;
	 * @param tableSpaceName
	 * @param tableName
	 * @return
	 */
	public List<String> getTableList(String tableSpaceName,String tableName){
		List<String> list=new ArrayList<String>();
		try {
			String[] tableNames = dmo.getStringArrayFirstColByDS(null, "select TABLE_NAME from dba_tables WHERE tablespace_name='"+tableSpaceName.toUpperCase()+"' AND TABLE_NAME LIKE '"+tableName.toUpperCase()+"%' ORDER BY TABLE_NAME");
			list=new  ArrayList<String>(Arrays.asList(tableNames));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	
	@Override
	public int getCurrYearQned() {
		int result = 0;
		try{
			String tableName="ESIGN";
			List<String> tableList = getTableList("HZBANK", tableName);
			if(tableList.contains(tableName+"_"+getCurMonth().replace("-",""))){
				tableName=tableName+"_"+getCurMonth().replace("-","");
			}else {
				if(tableList.size()==0){
					return 0;
				}else {
					tableName=tableList.get(tableList.size()-1);
				}
			}
			String sql="SELECT count(*) FROM  HZBANK."+tableName+" WHERE o>='"+getCurYear()+"-01-01 00:00:00' AND  j ='生效'";
			result = Integer.parseInt(dmo.getStringValueByDS(null, sql));
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String getCurrYearQnedXszb() {
		 String result="";
		 try {
			String tableName="S_LOAN_DK";
			List<String> tableList = getTableList("HZBANK", tableName);
			if(tableList.contains(tableName+"_"+getCurMonth().replace("-", ""))){
				tableName=tableName+"_"+getCurMonth().replace("-", "");
			}else {
				if(tableList.size()==0){
					return "0";
				}else {
					tableName=tableList.get(tableList.size()-1);
				}
			}
			// 贷款总数
			String sql="SELECT sum(xd_col7) FROM (select * from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL1||BIZ_DT in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL22<>'05'group by XD_COL1 ) and XD_COL4<'"+getDayTime(getMonthMaxDay())+"')";
			Double  totalDKNum =Double.parseDouble(dmo.getStringValueByDS(null, sql));
			// 黔农E贷数量
			sql="SELECT sum(xd_col7) FROM (select * from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL1||BIZ_DT in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL22<>'05'group by XD_COL1 ) and XD_COL4<'"+getDayTime(getMonthMaxDay())+"') WHERE xd_col86 IN ('x_wd',\n" +
					"'x_wj')";
			 Double qned= Double.parseDouble(dmo.getStringValueByDS(null, sql));
			result= new BigDecimal((qned/totalDKNum)*100).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result+"%";
	}

	@Override
	public String[][] getCKRanking() {
		String [][] data=null;
		try {
			data=dmo.getStringArrayByDS(null,"select dept.name,ROUND(zj.num),dept.deptname from(\n" +
					"select dy.id,(dy.num-nc.num)/10000 num from(\n" +
					"select wg.g id,sum(ck.num) num from(select a.oact_inst_no||b.EXTERNAL_CUSTOMER_IC name,sum(a.f) num from \n" +
					"(select oact_inst_no,cust_no cust_no,sum(f) f from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where load_dates='"+getQYTTime()+"' group by oact_inst_no,cust_no \n" +
					"union all select oact_inst_no,cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where load_dates='"+getQYTTime()+"'  group by oact_inst_no,cust_no) a \n" +
					"left join (select * from hzbank.S_OFCR_CI_CUSTMAST_"+getqytMonth()+" where load_dates='"+getQYTTime()+"') b on a.cust_no = b.COD_CUST_ID group by a.oact_inst_no,b.EXTERNAL_CUSTOMER_IC having sum(a.f)>1000) \n" +
					"ck left join (select wg.f||xx.G name,wg.id id,wg.g g from(select deptcode,G,j,k from  hzdb.s_loan_khxx_202001 group by deptcode,G,j,k) xx \n" +
					"left join (select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') wg \n" +
					"on xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D) wg on UPPER(ck.name)=UPPER(wg.name) group by wg.g) dy left join\n" +
					"(select wg.g id,sum(ck.num) num from(select a.oact_inst_no||b.EXTERNAL_CUSTOMER_IC name,sum(a.f) num from \n" +
					"(select oact_inst_no,cust_no cust_no,sum(f) f from hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+" group by oact_inst_no,cust_no \n" +
					"union all select oact_inst_no,cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+" group by oact_inst_no,cust_no) a \n" +
					"left join hzbank.S_OFCR_CI_CUSTMAST_"+getYearYmTime()+" b on a.cust_no = b.COD_CUST_ID group by a.oact_inst_no,b.EXTERNAL_CUSTOMER_IC having sum(a.f)>1000) \n" +
					"ck left join (select wg.f||xx.G name,wg.id id,wg.g g from(select deptcode,G,j,k from  hzdb.s_loan_khxx_202001 group by deptcode,G,j,k) xx \n" +
					"left join (select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') wg \n" +
					"on xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D) wg on UPPER(ck.name)=UPPER(wg.name) group by wg.g) nc on dy.id=nc.id) zj \n" +
					"left join (select * from hzdb.v_sal_personinfo where stationkind='客户经理') dept on zj.id=dept.code where dept.name is not null order by zj.num desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**'
	 * 先去的报表中的数据上个月的
	 * @return
	 */
	@Override
	public String[][] getQhCkCompletion() {
		String [][] data=null;
		String [] [] times=getqjData(3);
		try{
			StringBuilder sb=new StringBuilder();
			for(int i=times.length-1;i>0;i--){
				sb.append("select * from ( select * from(\n" +
						"select ROUND(rwnum,2) zj,'目标任务' name,code||'月' code from( \n" +
						"select '01' code,sum(B)/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '02' code,(sum(B)+sum(C))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '03' code,(sum(B)+sum(C)+sum(D))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '04' code,(sum(B)+sum(C)+sum(D)+sum(E))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '05' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '06' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '07' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '08' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '09' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '10' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '11' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"' union all\n" +
						"select '12' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L)+sum(M))/10000 rwnum from hzdb.excel_tab_18 where year='"+times[i][3]+"'\n" +
						")union all (select Round(sum(f)/10000,2) zj,'实际完成' name,month||'月' code from hzdb.excel_tab_9 \n" +
						"where year='"+times[i][3]+"' and A not in('合计') group by month)) where code='"+times[i][2]+"月')");
				if(i==1){
				}else{
					sb.append(" union all ");
				}
			}
			System.out.println(">>>>>>>>>>"+sb.toString());
			data=dmo.getStringArrayByDS(null,sb.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * zzl 取得T+1的全行完成存款情况
	 * @return
	 */
	public String[][] getQhCkCompletion2() {
		String [][] data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select * from(\n" +
					"select to_char(rwnum,'fm99999990.00') num,'目标任务' name,code||'月' code from(\n" +
					"select '01' code,sum(B)/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '02' code,(sum(B)+sum(C))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '03' code,(sum(B)+sum(C)+sum(D))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '04' code,(sum(B)+sum(C)+sum(D)+sum(E))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '05' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '06' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '07' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '08' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '09' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '10' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '11' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"' union all\n" +
					"select '12' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L)+sum(M))/10000 rwnum from hzdb.excel_tab_18 where year='"+getDayYear()+"')\n" +
					"where code='"+getdytMonth()+"') union all\n" +
					"(select to_char(dyck.dy-ncck.nc,'fm99999990.00') num,'实际完成' name,'"+getdytMonth()+"月' code from(\n" +
					"select '1' a,ROUND(sum(F)/100000000,2) dy from(\n" +
					"select sum(f) f \n" +
					"from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and f>0 \n" +
					"union all \n" +
					"select sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0\n" +
					"union all\n" +
					"select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0\n" +
					"union all\n" +
					"select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0))dyck\n" +
					"left join(\n" +
					"select '1' a,ROUND(sum(F)/100000000,2) nc from(\n" +
					"select sum(f) f \n" +
					"from hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and f>0 \n" +
					"union all \n" +
					"select sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and acct_bal>0\n" +
					"union all\n" +
					"select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and acct_bal>0\n" +
					"union all\n" +
					"select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_sv_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and acct_bal>0)) ncck\n" +
					"on dyck.a=ncck.a)");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	@Override
	public String[][] getgzhWcqingkuang() {
		String [][] data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select * from(\n" +
					"select rw.num A,rw.rwnum b,rw.name c,wc.num d,'实际完成' e,rw.name f,wc.num-rw.num zjnum from(\n" +
					"select rw.num num,'目标任务' rwnum,dept.name name,dept.code code  from(\n" +
					"select A,sum(rwnum) num from(\n" +
					"select A,'01' code,sum(B)/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'02' code,(sum(B)+sum(C))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'03' code,(sum(B)+sum(C)+sum(D))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'04' code,(sum(B)+sum(C)+sum(D)+sum(E))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'05' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'06' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'07' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'08' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'09' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'10' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'11' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A union all\n" +
					"select A,'12' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L)+sum(M))/10000 rwnum from hzdb.excel_tab_18 where year='"+getSmonthtYear()+"' group by A\n" +
					") where code='"+getdytMonth()+"' group by A) rw\n" +
					"left join hzdb.pub_corp_dept dept on instr(dept.name,rw.A)>0) rw\n" +
					"left join\n" +
					"(select dy.oact_inst_no,ROUND((dy.num-nc.num)/100000000,2) num from(\n" +
					"select oact_inst_no,sum(f) num from(\n" +
					"select oact_inst_no,sum(f) f from  hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and f>0 group by oact_inst_no \n" +
					"union all \n" +
					"select oact_inst_no,sum(acct_bal) f from  hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0 group by oact_inst_no \n" +
					"union all\n" +
					"select oact_inst_no,sum(acct_bal) f from  hzbank.a_agr_dep_acct_ent_fx_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0 group by oact_inst_no \n" +
					"union all\n" +
					"select oact_inst_no,sum(acct_bal) f from  hzbank.a_agr_dep_acct_ent_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0 group by oact_inst_no)\n" +
					"group by oact_inst_no) dy\n" +
					"left join\n" +
					"(select oact_inst_no,sum(f) num from(\n" +
					"select oact_inst_no,sum(f) f from  hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and f>0 group by oact_inst_no \n" +
					"union all \n" +
					"select oact_inst_no,sum(acct_bal) f from  hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+" where biz_dt="+getYearYmTime()+" and acct_bal>0 group by oact_inst_no \n" +
					"union all\n" +
					"select oact_inst_no,sum(acct_bal) f from  hzbank.a_agr_dep_acct_ent_fx_"+getYearYmTime()+" where biz_dt="+getYearYmTime()+" and acct_bal>0 group by oact_inst_no \n" +
					"union all\n" +
					"select oact_inst_no,sum(acct_bal) f from  hzbank.a_agr_dep_acct_ent_sv_"+getYearYmTime()+" where biz_dt="+getYearYmTime()+" and acct_bal>0 group by oact_inst_no)\n" +
					"group by oact_inst_no) nc on dy.oact_inst_no=nc.oact_inst_no) wc\n" +
					"on rw.code=wc.oact_inst_no)zj where zj.zjnum is not null order by zj.zjnum desc");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public String[][] getqhDkWcCount() {
		String [][] data=null;
		String [][] time=getqjData(3);
		StringBuilder sb=new StringBuilder();
		sb.append("select * from(");
		for(int i=0;i<time.length;i++){
			if(i==time.length-1){
				sb.append("select ROUND(sum(dk.money)/10000,2) num,'实际完成' name,'"+time[i][2]+"月' code from(\n" +
						"select xx.a name,sum(dk.num)/10000 money from \n" +
						"(select * from(select  '283'||xd_col85 a,sum(xd_col7) num from hzbank.s_loan_dk_"+time[i][0]+" \n" +
						"where xd_col1||biz_dt in (select xd_col1||max(biz_dt) from hzbank.s_loan_dk_"+time[i][0]+" where xd_col22<>'05'group by xd_col1) \n" +
						"and xd_col4<'"+getDayTime(time[i][1])+"' AND xd_col7>0 group by xd_col85) dk union all (select INPUT_BR_ID a,sum(LOAN_BALANCE) num \n" +
						"from hzbank.S_CMIS_ACC_LOAN_"+time[i][0]+" where biz_dt='"+time[i][1]+"'and CLA<>'05'  group by INPUT_BR_ID)) dk left join (select a,b from hzdb.excel_tab_28) \n" +
						"xx on xx.b=dk.a group by xx.a) dk) wc left join(select ROUND(rw.rwnum,2),'目标任务' name,rw.code from(\n" +
						"select '01月' code,(sum(B))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '02月' code,(sum(B)+sum(C))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '03月' code,(sum(B)+sum(C)+sum(D))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '04月' code,(sum(B)+sum(C)+sum(D)+sum(E))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '05月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '06月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '07月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '08月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '09月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '10月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '11月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"'  union all\n" +
						"select '12月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L)+sum(M))/10000 rwnum from hzdb.excel_tab_21 where year='"+time[i][0].substring(0,4)+"' \n" +
						") rw) rw on wc.code=rw.code");
			}else{
				sb.append("select ROUND(sum(dk.money)/10000,2) num,'实际完成' name,'"+time[i][2]+"月' code from(\n" +
						"select xx.a name,sum(dk.num)/10000 money from \n" +
						"(select * from(select  '283'||xd_col85 a,sum(xd_col7) num from hzbank.s_loan_dk_"+time[i][0]+" \n" +
						"where xd_col1||biz_dt in (select xd_col1||max(biz_dt) from hzbank.s_loan_dk_"+time[i][0]+" where xd_col22<>'05'group by xd_col1) \n" +
						"and xd_col4<'"+getDayTime(time[i][1])+"' AND xd_col7>0 group by xd_col85) dk union all (select INPUT_BR_ID a,sum(LOAN_BALANCE) num \n" +
						"from hzbank.S_CMIS_ACC_LOAN_"+time[i][0]+" where biz_dt='"+time[i][1]+"'and CLA<>'05'  group by INPUT_BR_ID)) dk left join (select a,b from hzdb.excel_tab_28) \n" +
						"xx on xx.b=dk.a group by xx.a) dk");
				sb.append(" union all ");
			}
		}

		try{
			data=dmo.getStringArrayByDS(null,sb.toString());
			//zzl 得到年初的总数
			String yearData=dmo.getStringValueByDS(null,"select ROUND(sum(dk.money)/10000,2) num from(\n" +
					"select xx.a name,sum(dk.num)/10000 money from \n" +
					"(select * from(select  '283'||xd_col85 a,sum(xd_col7) num from hzbank.s_loan_dk_"+getYearYm()+" \n" +
					"where xd_col1||biz_dt in (select xd_col1||max(biz_dt) from hzbank.s_loan_dk_"+getYearYm()+" where xd_col22<>'05'group by xd_col1) \n" +
					"and xd_col4<'"+getMonthTime(getYearYm())+"' AND xd_col7>0 group by xd_col85) dk union all (select INPUT_BR_ID a,sum(LOAN_BALANCE) num \n" +
					"from hzbank.S_CMIS_ACC_LOAN_"+getYearYm()+" where biz_dt='"+getYearYm()+"31'and CLA<>'05'  group by INPUT_BR_ID)) dk left join (select a,b from hzdb.excel_tab_28) \n" +
					"xx on xx.b=dk.a group by xx.a) dk");
			DecimalFormat df= new DecimalFormat("######0.00");
			for(int i=0;i<data.length;i++){
				data[i][3]=df.format(Double.parseDouble(data[i][3])+Double.parseDouble(yearData));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}


	@Override
	public String[][] getDKgzhWcqingkuang() {
		String [][] data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select * from(\n" +
					"select rw.num,rw.name,rw.A,wc.money,'实际完成',wc.name namex,wc.money-rw.num zjnum from(\n" +
					"select rw.A,ROUND(rw.rwnum,2) num,'目标任务' name,rw.code from(\n" +
					"select A,'1月' code,(sum(B)+sum(C))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'2月' code,(sum(B)+sum(C)+sum(D))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'3月' code,(sum(B)+sum(C)+sum(D)+sum(E))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'4月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'5月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'6月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'7月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'8月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'9月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'10月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'11月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L)+sum(M))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A  union all\n" +
					"select A,'12月' code,(sum(B)+sum(C)+sum(D)+sum(E)+sum(F)+sum(G)+sum(H)+sum(I)+sum(J)+sum(K)+sum(L)+sum(M)+sum(N))/10000 rwnum from hzdb.excel_tab_21 where year='"+getSmonthtYear()+"' group by A \n" +
					") rw where code='"+getdytMonth()+"月') rw\n" +
					"left join(\n" +
					"select case when xx.a='营业部' then '信贷部' else xx.a end name,ROUND(sum(dk.num)/100000000,2) money from \n" +
					"(select * from(select  '283'||xd_col85 a,sum(xd_col7) num from hzbank.s_loan_dk_"+getqytMonth()+" \n" +
					"where xd_col1||biz_dt in (select xd_col1||max(biz_dt) from hzbank.s_loan_dk_"+getqytMonth()+" where xd_col22<>'05'group by xd_col1) \n" +
					"and xd_col4<'"+getDayTime(getMonthMaxDay())+"' AND xd_col7>0 group by xd_col85) dk union all (select INPUT_BR_ID a,sum(LOAN_BALANCE) num \n" +
					"from hzbank.S_CMIS_ACC_LOAN_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and CLA<>'05' group by INPUT_BR_ID)) dk left join (select a,b from hzdb.excel_tab_28) \n" +
					"xx on xx.b=dk.a group by xx.a ) wc on rw.A=wc.name)zj order by zj.zjnum desc");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public String[][] getNhFgMian() {
		String [][]data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select zj.*,'农户未贷款',zn.num-zj.num from(\n" +
					" select '1' code,'农户贷款' name,sum(zj.num) num from(\n" +
					"select code ,count(code) num from (select concat('283', bh), excel.c code from (select xd_col85 bh from hzbank.s_loan_dk_"+getqytMonth()+"\n" +
					" where xd_col1||biz_dt in(select xd_col1||max(biz_dt) from hzbank.s_loan_dk_"+getqytMonth()+" where xd_col22<>'05'group by xd_col1 ) \n" +
					" and xd_col4<'"+getDayTime(getMonthMaxDay())+"' and  xd_col63 ='905') dk left join (select a, b, c from hzdb.excel_tab_28) \n" +
					" excel on concat('283', dk.bh)= excel.b join (select name, shortname from hzdb.pub_corp_dept where name != '营业部') \n" +
					" dept on excel.a = dept.shortname)group by code)zj ) zj left join\n" +
					" (select '1' code,sum(num)  num from(\n" +
					"select excel.f code ,count(excel.f) num  from ((select g,j||k name from hzdb.s_loan_khxx_202001 slk  where b='农业家庭户口' and  c='户主') \n" +
					"khxx join  (select * from hzdb.excel_tab_85 where parentid ='2') excel on khxx.name=excel.e join (hzdb.pub_corp_dept) dept on dept.code=excel.f)  \n" +
					"group by excel.f)) zn on zj.code=zn.code");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public String[][] getDkStatistical() {
		String [] [] data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select dk.num,to_char(dk.num-ncdk.num,'fm9999999990.00') from(\n" +
					"select '1' code,ROUND(sum(dk.money)/10000,2) num from(\n" +
					"select xx.a name,sum(dk.num)/10000 money from \n" +
					"(select * from(select  '283'||xd_col85 a,sum(xd_col7) num from hzbank.s_loan_dk_"+getqytMonth()+" \n" +
					"where xd_col1||biz_dt in (select xd_col1||max(biz_dt) from hzbank.s_loan_dk_"+getqytMonth()+" where xd_col22<>'05'group by xd_col1) \n" +
					"and xd_col4<'"+getDayTime(getMonthMaxDay())+"' AND xd_col7>0 group by xd_col85) dk union all (select INPUT_BR_ID a,sum(LOAN_BALANCE) num \n" +
					"from hzbank.S_CMIS_ACC_LOAN_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"'and CLA<>'05'  group by INPUT_BR_ID)) dk left join (select a,b from hzdb.excel_tab_28) \n" +
					"xx on xx.b=dk.a group by xx.a)dk) dk left join\n" +
					"(select ROUND(sum(ncdk.money)/10000,2) num,'1' code from(\n" +
					"select xx.a name,sum(dk.num)/10000 money from \n" +
					"(select * from(select  '283'||xd_col85 a,sum(xd_col7) num from hzbank.s_loan_dk_"+getYearYm()+" \n" +
					"where xd_col1||biz_dt in (select xd_col1||max(biz_dt) from hzbank.s_loan_dk_"+getYearYm()+" where xd_col22<>'05'group by xd_col1) \n" +
					"and xd_col4<'"+getdyYear()+"-01-01 00:00:00' AND xd_col7>0 group by xd_col85) dk union all (select INPUT_BR_ID a,sum(LOAN_BALANCE) num \n" +
					"from hzbank.S_CMIS_ACC_LOAN_"+getYearYm()+" where biz_dt='"+getYearYmTime()+"'and CLA<>'05'  group by INPUT_BR_ID)) dk left join (select a,b from hzdb.excel_tab_28) \n" +
					"xx on xx.b=dk.a group by xx.a) ncdk) ncdk on dk.code=ncdk.code");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * zzl
	 * 获取一个区间段的日期
	 */
	public String [][] getqjData(int time){
		String date[][]=new String[time][4];
		for(int i=0;i<time;i++){
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat format3 = new SimpleDateFormat("MM");
			SimpleDateFormat format4 = new SimpleDateFormat("yyyy");
			Date d = new Date();
			cal.setTime(d);
			cal.add(Calendar.DATE, - 1);
			cal.setTime(cal.getTime());
			cal.add(Calendar.MONTH, -i);
			cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DAY_OF_MONTH));//最后一天
			date[i][0] = format.format(cal.getTime());
			date[i][1] = format2.format(cal.getTime());
			date[i][2] = format3.format(cal.getTime());
			date[i][3] = format4.format(cal.getTime());

		}
		return date;
	}
	/**
	 * zzl
	 * 得到指定日期的加天数 getYearYm
	 */
	public String getDayTime(String date){
		String lastDate=null;
		try{
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Date d = format.parse(date);
			cal.setTime(d);
			int day = cal.get(Calendar.DATE);
			cal.set(Calendar.DATE, day + 1);
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			lastDate = format2.format(cal.getTime());
		}catch (Exception e){
			e.printStackTrace();
		}
		return lastDate;
	}

	/**
	 * zzl
	 * 得到指定日期月份+1
	 */
	public String getMonthTime(String date){
		String lastDate=null;
		try{
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
			Date d = format.parse(date);
			cal.setTime(d);
			cal.add(Calendar.MONTH, +1);
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-01 00:00:00");
			lastDate = format2.format(cal.getTime());
		}catch (Exception e){
			e.printStackTrace();
		}
		return lastDate;
	}
	/**
	 * zzl
	 * 得到当前年份
	 */
	public String getdyYear(){
		String lastDate=null;
		try{
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyy");
			Date d = new Date();
			lastDate = format.format(d);
		}catch (Exception e){
			e.printStackTrace();
		}
		return lastDate;
	}
	public String [][] getDkStatisticalHs(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select dy.num,dy.num-nc.num from(\n" +
					"select '1' code,count(count(xd_col16)) num from hzbank.s_loan_dk_"+getqytMonth()+" where xd_col7>0 and xd_col22<>'05' group by xd_col16) dy left join(\n" +
					"select '1' code,count(count(xd_col16)) num from hzbank.s_loan_dk_"+getYearYm()+" where xd_col7>0 and xd_col22<>'05' group by xd_col16) nc on dy.code=nc.code\n");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getBmDkStatisticalHs(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select dy.num,dy.num-nc.num from(\n" +
					"select '1' code,count(count(xd_col16)) num from hzbank.s_loan_dk_"+getqytMonth()+" where xd_col7>0 and xd_col22<>'05' and xd_col170='2' group by xd_col16) dy left join(\n" +
					"select '1' code,count(count(xd_col16)) num from hzbank.s_loan_dk_"+getYearYm()+" where xd_col7>0 and xd_col22<>'05' and xd_col170='2' group by xd_col16) nc on dy.code=nc.code\n");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getGtDkStatisticalHs(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select dy.num,dy.num-nc.num from(\n" +
					"select '1' code,count(count(xd_col16)) num from hzbank.s_loan_dk_"+getqytMonth()+" where xd_col7>0 and xd_col22<>'05' and xd_col170<>'2' and xd_col72='11' group by xd_col16) dy left join(\n" +
					"select '1' code,count(count(xd_col16)) num from hzbank.s_loan_dk_"+getYearYm()+" where xd_col7>0 and xd_col22<>'05' and xd_col170<>'2' and xd_col72='11' group by xd_col16) nc on dy.code=nc.code\n");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getXdlDkStatisticalHs(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select dy.num,dy.num-nc.num from(\n" +
					"select '1' code,count(count(xd_col16)) num from hzbank.s_loan_dk_"+getqytMonth()+" where xd_col7>0 and xd_col22<>'05' and xd_col170<>'2' and xd_col72 not in ('19','06','15','01','16','12','10','11') group by xd_col16) dy left join(\n" +
					"select '1' code,count(count(xd_col16)) num from hzbank.s_loan_dk_"+getYearYm()+" where xd_col7>0 and xd_col22<>'05' and xd_col170<>'2' and xd_col72 not in ('19','06','15','01','16','12','10','11') group by xd_col16) nc on dy.code=nc.code\n");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getnhDkStatisticalHs(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select dy.num,dy.num-nc.num from(\n" +
					"select '1' code,count(count(xd_col16)) num from hzbank.s_loan_dk_"+getqytMonth()+" where xd_col7>0 and xd_col22<>'05' and xd_col170<>'2' and xd_col72 in('19','06','15','01','16') group by xd_col16) dy left join(\n" +
					"select '1' code,count(count(xd_col16)) num from hzbank.s_loan_dk_"+getYearYm()+" where xd_col7>0 and xd_col22<>'05' and xd_col170<>'2' and xd_col72 in('19','06','15','01','16') group by xd_col16) nc on dy.code=nc.code\n");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getDKRanking(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select * from(\n" +
					"select dept.name,ROUND(dy.num-nc.num,2) num,dept.deptname from(\n" +
					"select wg.g id,sum(dk.num) num from(select '283'||BH||AP name,case when sum(K)/10000>=7 then 7 else sum(K)/10000 end \n" +
					"num from (select case when   xd_col85='30100' then '30100-xd' else xd_col85 end BH,xd_col72 I,xd_col16 AP,xd_col7 k,xd_col170 from \n" +
					"hzbank.s_loan_dk_"+getqytMonth()+" where xd_col1||biz_dt in(select xd_col1||max(biz_dt) from hzbank.s_loan_dk_"+getqytMonth()+" where xd_col22<>'05'group by xd_col1 ) \n" +
					"and xd_col4<'"+getDayTime(getMonthMaxDay())+"' and xd_col7>0) group by BH,AP)\n" +
					"dk left join(select wg.code||xx.G name,wg.id id,wg.g g from(select deptcode,G,j,k from  hzdb.s_loan_khxx_202001 group by deptcode,G,j,k) xx \n" +
					"left join (select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') wg \n" +
					"on xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D) wg on UPPER(dk.name) = UPPER(wg.name) group by wg.g) dy\n" +
					"left join(\n" +
					"select wg.g id,sum(dk.num) num from(select '283'||BH||AP name,case when sum(K)/10000>=7 then 7 else sum(K)/10000 end \n" +
					"num from (select case when   xd_col85='30100' then '30100-xd' else xd_col85 end BH,xd_col72 I,xd_col16 AP,xd_col7 k,xd_col170 from \n" +
					"hzbank.s_loan_dk_"+getYearYm()+" where xd_col1||biz_dt in(select xd_col1||max(biz_dt) from hzbank.s_loan_dk_"+getYearYm()+" where xd_col22<>'05'group by xd_col1 ) \n" +
					"and xd_col4<'"+getdyYear()+"-01-01 00:00:00' and xd_col7>0) group by BH,AP)\n" +
					"dk left join(select wg.code||xx.G name,wg.id id,wg.g g from(select deptcode,G,j,k from  hzdb.s_loan_khxx_202001 group by deptcode,G,j,k) xx \n" +
					"left join (select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') wg \n" +
					"on xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D) wg on UPPER(dk.name) = UPPER(wg.name) group by wg.g) nc \n" +
					"on dy.id=nc.id left join (select * from hzdb.v_sal_personinfo where stationkind='客户经理') dept on dy.id= dept.code)where name is not null order by num desc\n");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	//to_char(dy.num,'fm999999990.00')
	public String [] []getBlDKCount(){
		String [] []data=null;
		try{
//			data=dmo.getStringArrayByDS(null,"select to_char(dy.num,'fm999999990.00'),to_char(dy.num-nc.num,'fm999999990.00') from(\n" +
//					"select '1' code,ROUND(sum(XD_COL7)/100000000,2) num  from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL1||BIZ_DT in(\n" +
//					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL22 in('03','04')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
//					"and XD_COL4<'"+getDayTime(getMonthMaxDay())+"' and XD_COL7>0) dy\n" +
//					"left join\n" +
//					"(select '1' code,ROUND(sum(XD_COL7)/100000000,2) num from hzbank.s_loan_dk_"+getYearYm()+" where XD_COL1||BIZ_DT in(\n" +
//					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getYearYm()+" where XD_COL22 in('03','04')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
//					"and XD_COL4<'"+getdyYear()+"-01-01 00:00:00' and XD_COL7>0 ) nc on dy.code=nc.code");
			data=dmo.getStringArrayByDS(null,"select to_char(dy.num,'fm999999990.00'),to_char(dy.num-nc.num,'fm999999990.00') from(\n" +
					"select '1' code,sum(to_number(replace(K,',','')))/100000000 num from hzdb.s_loan_dk_"+getqytMonth()+" where replace(Q,',','')>60\n" +
					") dy left join(\n" +
					"select '1' code,sum(to_number(replace(K,',','')))/100000000  num from hzdb.s_loan_dk_"+getYearYm()+" where replace(Q,',','')>60\n" +
					") nc on dy.code=nc.code");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public String[][] getBlDKCount2() {
		String [] []data=null;
		try{
//			data=dmo.getStringArrayByDS(null,"select to_char(dy.num,'fm999999990.00'),to_char(dy.num-nc.num,'fm999999990.00') from(\n" +
//					"select '1' code,ROUND(sum(XD_COL7)/100000000,2) num  from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL1||BIZ_DT in(\n" +
//					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL22 in('03','04')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
//					"and XD_COL4<'"+getDayTime(getMonthMaxDay())+"' and XD_COL7>0) dy\n" +
//					"left join\n" +
//					"(select '1' code,ROUND(sum(XD_COL7)/100000000,2) num from hzbank.s_loan_dk_"+getYearYm()+" where XD_COL1||BIZ_DT in(\n" +
//					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getYearYm()+" where XD_COL22 in('03','04')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
//					"and XD_COL4<'"+getdyYear()+"-01-01 00:00:00' and XD_COL7>0 ) nc on dy.code=nc.code");
			data=dmo.getStringArrayByDS(null,"select to_char(dy.num,'fm999999990.00'),to_char(dy.num-nc.num,'fm999999990.00') from(\n" +
					"select '1' code,sum(to_number(replace(K,',','')))/100000000 num from hzdb.s_loan_dk_"+getqytMonth()+" where replace(Q,',','')>90\n" +
					") dy left join(\n" +
					"select '1' code,sum(to_number(replace(K,',','')))/100000000  num from hzdb.s_loan_dk_"+getYearYm()+" where replace(Q,',','')>90\n" +
					") nc on dy.code=nc.code");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}

	public String [][] getBlDKLvCount(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select to_char(dy.num,'fm999999990.00'),to_char(dy.num-nc.num,'fm999999990.00') from(\n" +
					"select bl.code,ROUND(bl.num/dk.num*100,2) num from(\n" +
					"select '1' code,ROUND(sum(XD_COL7)/100000000,2) num  from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL1||BIZ_DT in(\n" +
					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL22 in('03','04')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
					"and XD_COL4<'"+getDayTime(getMonthMaxDay())+"' and XD_COL7>0) bl\n" +
					"left join(\n" +
					"select '1' code,ROUND(sum(XD_COL7)/100000000,2) num  from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL1||BIZ_DT in(\n" +
					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL22<>'05' group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
					"and XD_COL4<'"+getDayTime(getMonthMaxDay())+"' and XD_COL7>0) dk on bl.code=dk.code\n" +
					") dy left join(\n" +
					"select bl.code,ROUND(bl.num/dk.num*100,2) num from(\n" +
					"select '1' code,ROUND(sum(XD_COL7)/100000000,2) num  from hzbank.s_loan_dk_"+getYearYm()+" where XD_COL1||BIZ_DT in(\n" +
					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getYearYm()+" where XD_COL22 in('03','04')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
					"and XD_COL4<'"+getdyYear()+"-01-01 00:00:00' and XD_COL7>0) bl\n" +
					"left join(\n" +
					"select '1' code,ROUND(sum(XD_COL7)/100000000,2) num  from hzbank.s_loan_dk_"+getYearYm()+" where XD_COL1||BIZ_DT in(\n" +
					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getYearYm()+" where XD_COL22<>'05' group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
					"and XD_COL4<'"+getdyYear()+"-01-01 00:00:00' and XD_COL7>0) dk on bl.code=dk.code\n" +
					")nc on dy.code=nc.code");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}

	public String [][] getShBlDKLvCount(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select to_char(ROUND(sum(hk.num)/10000,2),'fm999999990.00') " +
					"from(select B XD_COL1 from hzdb.s_loan_dk_"+getqytMonth()+" where replace(Q,',','')>60 group by B) dk\n" +
					"left join(\n" +
					"select xd_col1,sum(xd_col5) num from hzbank.s_loan_hk where \n" +
					"to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')>='"+getDateMonthStart("-")+"' and \n" +
					"to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')<='"+getDateMonthEnd("-")+"' group by xd_col1) hk on dk.XD_COL1=hk.XD_COL1");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getxzBlDKLvCount(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select to_char(ROUND(sum(hk.num)/10000,2),'fm999999990.00') " +
					"from(select B XD_COL1 from hzdb.s_loan_dk_"+getqytMonth()+" where replace(Q,',','')>90 group by B) dk\n" +
					"left join(\n" +
					"select xd_col1,sum(xd_col5) num from hzbank.s_loan_hk where \n" +
					"to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')>='"+getDateMonthStart("-")+"' and \n" +
					"to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')<='"+getDateMonthEnd("-")+"' group by xd_col1) hk on dk.XD_COL1=hk.XD_COL1");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getShBwBlDKLvCount(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select to_char(ROUND(sum(hk.num)/10000,2),'fm9999990.00') from(\n" +
					"select B xd_col1 from hzdb.s_loan_dkbl_202012) dk\n" +
					"left join(\n" +
					"select xd_col1,sum(xd_col5) num from hzbank.s_loan_hk where \n" +
					"to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')>='"+getDateMonthStart("-")+"' and \n" +
					"to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')<='"+getDateMonthEnd("-")+"' group by xd_col1) hk on dk.XD_COL1=hk.XD_COL1");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getxzBwBlDKLvCount(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select case when nc.num is null then ROUND(dy.num-sg.num,2) else ROUND(dy.num-nc.num,2) end num  from(\n" +
					"select '1' code,sum(XD_COL7)/10000 num  from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL1||BIZ_DT in(\n" +
					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getqytMonth()+" where  XD_COL22='05' group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
					"and XD_COL4<'"+getDayTime(getMonthMaxDay())+"' and XD_COL7>0) dy\n" +
					"left join\n" +
					"(select '1' code,sum(XD_COL7)/10000 num  from hzbank.s_loan_dk_"+getYearYm()+" where XD_COL1||BIZ_DT in(\n" +
					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getYearYm()+" where  XD_COL22='05' group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
					"and XD_COL4<'"+getdyYear()+"-01-01 00:00:00' and XD_COL7>0) nc on dy.code=nc.code\n" +
					"left join\n" +
					"(select '1' code,sum(replace(K,',',''))/10000 num from hzdb.s_loan_bl_"+getYearYm()+") sg on dy.code=sg.code");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getqsBlDKLvCount(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select * from(\n" +
					"select case when dept.name is null then '0' else dept.name end name,hk.num num,dept.deptname from(\n" +
					"select wg.g id,case when sum(dk.num) is null then 0 else sum(dk.num) end num from(\n" +
					"select '283'||dk.XD_COL85||dk.XD_COL16 name,sum(hk.num) num from(\n" +
					"select XD_COL1,XD_COL85,XD_COL16,sum(XD_COL7) num from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL22 in('03','04') group by XD_COL1,XD_COL85,XD_COL16) dk\n" +
					"left join \n" +
					"(select xd_col1,sum(xd_col5) num from hzbank.s_loan_hk where \n" +
					"to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')>='"+getDateMonthStart("-")+"' and \n" +
					"to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')<='"+getDateMonthEnd("-")+"' group by xd_col1) hk on dk.XD_COL1=hk.XD_COL1 group by dk.XD_COL85,dk.XD_COL16)dk\n" +
					"left join (select wg.code||xx.G name,wg.id,wg.g from\n" +
					"(select deptcode,G,j,k from  hzdb.s_loan_khxx_202001 group by deptcode,G,j,k) xx left join \n" +
					"(select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') wg \n" +
					"on xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D) wg on UPPER(dk.name) = UPPER(wg.name) group by wg.g) hk\n" +
					"left join (select * from hzdb.v_sal_personinfo where stationkind='客户经理') dept on hk.id= dept.code) zj where zj.name<>'0' order by zj.num desc");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getqhBlDKLvCount(){
		String [] []data=null;
		String [][] time=getqjData(3);
		StringBuilder sb=new StringBuilder();
		sb.append("select * from(");
		try{
			for(int i=0;i<time.length;i++){
				if(i==time.length-1){
					sb.append("select bn.num,'表内不良贷款','"+time[i][2]+"月' code,bw.num bwnum,'表外不良贷款','"+time[i][2]+"月' code1 from(\n" +
							"select '1' code,ROUND(sum(XD_COL7)/100000000,2) num  from hzbank.s_loan_dk_"+time[i][0]+" where XD_COL1||BIZ_DT in(\n" +
							"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+time[i][0]+" where XD_COL22 in('03','04')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
							"and XD_COL4<'"+getDayTime(time[i][1])+"' and XD_COL7>0) bn\n" +
							"left join \n" +
							"(select '1' code,ROUND(sum(XD_COL7)/100000000,2) num  from hzbank.s_loan_dk_"+time[i][0]+" where XD_COL1||BIZ_DT in(\n" +
							"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+time[i][0]+" where XD_COL22 in('05')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
							"and XD_COL4<'"+getDayTime(time[i][1])+"' and XD_COL7>0) bw on bn.code=bw.code) order by code");
				}else{
					sb.append("select bn.num,'表内不良贷款','"+time[i][2]+"月' code,bw.num bwnum,'表外不良贷款','"+time[i][2]+"月' code1 from(\n" +
							"select '1' code,ROUND(sum(XD_COL7)/100000000,2) num  from hzbank.s_loan_dk_202012 where XD_COL1||BIZ_DT in(\n" +
							"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_202012 where XD_COL22 in('03','04')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
							"and XD_COL4<'"+getDayTime(time[i][1])+"' and XD_COL7>0) bn\n" +
							"left join \n" +
							"(select '1' code,ROUND(sum(XD_COL7)/100000000,2) num  from hzbank.s_loan_dk_"+time[i][0]+" where XD_COL1||BIZ_DT in(\n" +
							"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+time[i][0]+" where XD_COL22 in('05')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
							"and XD_COL4<'"+getDayTime(time[i][1])+"' and XD_COL7>0) bw on bn.code=bw.code");
					sb.append(" union all ");
				}
			}
			data=dmo.getStringArrayByDS(null,sb.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getgzhBlDKLvCount(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select wg.A,to_char(zj.num,'fm9999990.00') from(\n" +
					"select '283'||bl.code code,ROUND(bl.num/dk.num*100,2) num from(\n" +
					"select XD_COL85 code,sum(XD_COL7) num  from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL1||BIZ_DT in(\n" +
					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL22 in('03','04')group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
					"and XD_COL4<'"+getDayTime(getMonthMaxDay())+"' and XD_COL7>0 group by XD_COL85) bl\n" +
					"left join(\n" +
					"select XD_COL85 code,sum(XD_COL7) num  from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL1||BIZ_DT in(\n" +
					"select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getqytMonth()+" where XD_COL22<>'05' group by XD_COL1)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      \n" +
					"and XD_COL4<'"+getDayTime(getMonthMaxDay())+"' and XD_COL7>0 group by XD_COL85) dk on bl.code=dk.code) zj\n" +
					"left join hzdb.excel_tab_28 wg on zj.code=wg.b order by zj.num");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getQnyDKLvCount(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select * from(\n" +
					"select sal.name,yx.num,sal.deptname from(\n" +
					"select J code,count(j) num from hzdb.s_loan_qnyyx_"+getDateZdy("yyyyMM")+" where I>='"+getDateZdy("yyyy")+"-01-01 00:00:00' and I<='"+getDateZdy("yyyy")+"-12-31 00:00:00' group by j) yx\n" +
					"left join \n" +
					"hzdb.v_sal_personinfo sal on yx.code=sal.code) zj where zj.name is not null order by zj.num desc");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	public String [][] getQnyEDKLvCount(){
		String [] []data=null;
		try{
			data=dmo.getStringArrayByDS(null,"select * from(\n" +
					"select case when wg.name is null then '0' else wg.name end name,dk.num,wg.deptname from( \n" +
					"select wg.g id,count(wg.g) num from(select qy.*,case when dept.code is null then '2830001-xd' else dept.code end code from\n" +
					"(select * from hzbank.esign_"+getDateZdy("yyyyMM")+" where o>='"+getDateZdy("yyyy")+"-01-01 00:00:00' and o<'"+getDateZdy("yyyy")+"-12-31 00:00:00' and load_dates=(select max(load_dates) from \n" +
					"hzbank.esign_"+getDateZdy("yyyyMM")+") and j='生效') qy left join hzdb.pub_corp_dept dept on \n" +
					"(substr(a,length('贵州赫章农村商业银行股份有限公司')+1,length(a)-length('贵州赫章农村商业银行股份有限公司')))=dept.name ) \n" +
					"qy left join (select xx.deptcode||xx.G name,wg.id id,wg.g g from(select deptcode,G,j,k from  hzdb.s_loan_khxx_202001 group by deptcode,G,j,k) \n" +
					"xx left join (select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') \n" +
					"wg on xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D) wg on UPPER(qy.code||qy.c)=UPPER(wg.name) group by wg.g\n" +
					")dk left join hzdb.v_sal_personinfo wg on dk.id=wg.code)\n" +
					"where name<>'0' order by num desc");
		}catch (Exception e){
			e.printStackTrace();
		}
		return data;
	}
	/**
	 * zzl 当前月初日期
	 */
	public String getDateMonthStart(String type){
		Calendar cal = Calendar.getInstance();
		if(type==null || type.equals(null) || type.equals("")){
			type="yyyyMMdd";
		}else{
			type="yyyy-MM-dd";
		}
		SimpleDateFormat format = new SimpleDateFormat(type);
		Date d = new Date();
		cal.setTime(d);
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}
	/**
	 * zzl 当前月末日期
	 */
	public String getDateMonthEnd(String type){
		Calendar cal = Calendar.getInstance();
		if(type==null || type.equals(null) || type.equals("")){
			type="yyyyMMdd";
		}else{
			type="yyyy-MM-dd";
		}
		SimpleDateFormat format = new SimpleDateFormat(type);
		Date d = new Date();
		cal.setTime(d);
		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}
	/**
	 * zzl 得到上个月的日期 格式自定义
	 */
	public String getDateZdy(String type){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(type);
		Date d = new Date();
		cal.setTime(d);
		cal.add(Calendar.MONTH,-1);
		String lastDate = format.format(cal.getTime());
		return lastDate;
	}

}