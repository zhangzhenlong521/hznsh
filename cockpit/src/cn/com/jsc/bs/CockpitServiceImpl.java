package cn.com.jsc.bs;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.jsc.ui.CockpitServiceIfc;

import java.math.BigDecimal;
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
            data= dmo.getStringArrayByDS(null,"select dyck.dy dyye,dyck.dy-ncck.nc ncye from(\n" +
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
            data= dmo.getStringArrayByDS(null,"select to_char(dy.num,'fm999999990.00'),to_char(dy.num-nc.num,'fm999999990.00') from(\n" +
                    "select '1' a,count(dy.num)/10000 num from(\n" +
                    "select b.EXTERNAL_CUSTOMER_IC num from(\n" +
                    "select cust_no,sum(F) f from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' group by cust_no \n" +
                    "union all\n" +
                    "select cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' group by cust_no)\n" +
                    "a left join (select * from hzbank.S_OFCR_CI_CUSTMAST_"+getqytMonth()+" where  load_dates='"+getQYTTime()+"') b on a.cust_no = b.COD_CUST_ID group by b.EXTERNAL_CUSTOMER_IC having sum(a.f)>1000) dy) dy\n" +
                    "left join(\n" +
                    "select '1' a,count(nc.num)/10000 num from(\n" +
                    "select b.EXTERNAL_CUSTOMER_IC num from(\n" +
                    "select cust_no,sum(F) f from hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+" group by cust_no \n" +
                    "union all\n" +
                    "select cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+" group by cust_no)\n" +
                    "a left join hzbank.S_OFCR_CI_CUSTMAST_"+getYearYmTime()+" b on a.cust_no = b.COD_CUST_ID group by b.EXTERNAL_CUSTOMER_IC having sum(a.f)>1000) nc)\n" +
                    "nc on dy.a=nc.a");
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

    public static void main(String[] args) {
        CockpitServiceImpl cp=new CockpitServiceImpl();
        System.out.print(cp.getYearYmTime());
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
			// 获取到当前表名
			String tableName="S_LOAN_QNYYX";
			List<String> tableList = getTableList("hzdb",tableName);
			String sql;
			if(tableList.contains(tableName+"_"+getCurMonth().replace("-",""))){
				// 计算黔农云有效户数
				tableName=tableName+"_"+getCurMonth().replace("-","");
			}else {
				if(tableList.size()==0){
					return "0";
				}else {
				  tableName=tableList.get(tableList.size()-1);
				}
			}
			sql="SELECT count(*) FROM (SELECT c, e,f,TO_NUMBER(REPLACE(n,',',''))+TO_NUMBER(REPLACE(o,',',''))+TO_NUMBER(REPLACE(P,',',''))+TO_NUMBER(REPLACE(Q,',',''))+TO_NUMBER(REPLACE(R,',',''))+TO_NUMBER(REPLACE(S,',',''))+TO_NUMBER(REPLACE(T,',',''))+TO_NUMBER(REPLACE(U,',',''))+TO_NUMBER(REPLACE(V,',',''))+TO_NUMBER(REPLACE(w,',',''))+TO_NUMBER(REPLACE(x,',',''))+TO_NUMBER(REPLACE(Y,',',''))+TO_NUMBER(REPLACE(Z,',',''))+TO_NUMBER(REPLACE(aa,',',''))+TO_NUMBER(REPLACE(ab,',',''))+TO_NUMBER(REPLACE(ac,',',''))+TO_NUMBER(REPLACE(ad,',',''))   num   FROM "+tableName+" WHERE num >0";
		    int yxNum = Integer.parseInt(dmo.getStringValueByDS(null,sql));
		    // 黔农云总数
		    sql="SELECT count(*) FROM"+tableName;
		    int totalNum = Integer.parseInt(dmo.getStringValueByDS(null, sql));
		    result=new BigDecimal((yxNum/totalNum)*100).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result+"%";
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
			String sql="SELECT count(*) FROM  HZBANK."+tableName+" WHERE xd_col4>='"+getCurYear()+"-01-01 00:00:00' AND xd_col7>0";
			int  totalDKNum =Integer.parseInt(dmo.getStringValueByDS(null, sql));
			// 黔农E贷数量
			sql="SELECT count(*) FROM HZBANK."+tableName+" WHERE xd_col86 IN ('x_wd','x_wj') AND xd_col4>='"+getCurYear()+"-01-01 00:00:00' AND xd_col7>0";
			int qned= Integer.parseInt(dmo.getStringValueByDS(null, sql));
			result= new BigDecimal((qned/totalDKNum)*100).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result+"%";
	}
}
