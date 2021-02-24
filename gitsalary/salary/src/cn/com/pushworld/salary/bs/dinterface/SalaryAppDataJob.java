package cn.com.pushworld.salary.bs.dinterface;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.jsc.bs.CockpitServiceImpl;
import cn.com.jsc.ui.CockpitServiceIfc;
import cn.com.jsc.ui.DateUIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * SalaryAppDataJob
 * zzl
 * 绩效APP的数据统计任务，为了提高接口性能，需要每天生成统计的临时表
 * @author Dragon
 * @date 2021/2/23
 */
public class SalaryAppDataJob implements WLTJobIFC {
    private CommDMO dmo = new CommDMO();
    @Override
    public String run() throws Exception {
        StringBuilder sb=new StringBuilder();
        try{
            String count=dmo.getStringValueByDS(null,"select dates from s_app_count where dates='"+DateUIUtil.getQYTTime()+"' and type='存款' and rownum=1");
            if(count==null){
                String grhqs=dmo.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_psn_sv_"+ DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                String grdqs=dmo.getStringValueByDS(null,"select biz_dt from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                String dghgs=dmo.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_ent_fx_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                String dgdqs=dmo.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_ent_sv_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                String kuxxs=dmo.getStringValueByDS(null,"select biz_dt from hzbank.S_OFCR_CI_CUSTMAST_"+DateUIUtil.getqytMonth()+" where load_dates='"+DateUIUtil.getQYTTime()+"' and rownum=1");

                if(grhqs==null || grdqs==null || dghgs==null || dgdqs==null || kuxxs==null) {

                }else{
                    CockpitServiceImpl service=new CockpitServiceImpl();
                    String [][] gxck=service.getCkStatistical();
                    String [][] ckhs=service.getCKHsCount();
                    String [][] grhq=service.getCKGeRenCount();
                    String [][] grdq=service.getCKGeRenDQCount();
                    String [][] dghq=service.getCKDgHqCount();
                    String [][] dgdq=service.getCKDgDqCount();
                    List list=new ArrayList();
                    InsertSQLBuilder insertSQLBuilder=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder.putFieldValue("typename","各项存款总额:"+gxck[0][0]+"亿元");
                    insertSQLBuilder.putFieldValue("typevalue","较年初新增:"+gxck[0][1]+"亿元");
                    insertSQLBuilder.putFieldValue("type","存款");
                    insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder.getSQL());
                    InsertSQLBuilder insertSQLBuilder1=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder1.putFieldValue("typename","有效存款户数:"+ckhs[0][0]+"万户");
                    insertSQLBuilder1.putFieldValue("typevalue","较年初新增:"+ckhs[0][1]+"户");
                    insertSQLBuilder1.putFieldValue("type","存款");
                    insertSQLBuilder1.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder1.getSQL());
                    InsertSQLBuilder insertSQLBuilder2=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder2.putFieldValue("typename","个人活期存款:"+grhq[0][0]+"亿元");
                    insertSQLBuilder2.putFieldValue("typevalue","较年初新增:"+grhq[0][1]+"亿元");
                    insertSQLBuilder2.putFieldValue("type","存款");
                    insertSQLBuilder2.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder2.getSQL());
                    InsertSQLBuilder insertSQLBuilder3=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder3.putFieldValue("typename","个人定期存款:"+grdq[0][0]+"亿元");
                    insertSQLBuilder3.putFieldValue("typevalue","较年初新增:"+grdq[0][1]+"亿元");
                    insertSQLBuilder3.putFieldValue("type","存款");
                    insertSQLBuilder3.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder3.getSQL());
                    InsertSQLBuilder insertSQLBuilder4=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder4.putFieldValue("typename","对公活期存款:"+dghq[0][0]+"亿元");
                    insertSQLBuilder4.putFieldValue("typevalue","较年初新增:"+dghq[0][1]+"亿元");
                    insertSQLBuilder4.putFieldValue("type","存款");
                    insertSQLBuilder4.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder4.getSQL());
                    InsertSQLBuilder insertSQLBuilder5=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder5.putFieldValue("typename","对公定期存款:"+dgdq[0][0]+"亿元");
                    insertSQLBuilder5.putFieldValue("typevalue","较年初新增:"+dgdq[0][1]+"亿元");
                    insertSQLBuilder5.putFieldValue("type","存款");
                    insertSQLBuilder5.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder5.getSQL());
                    dmo.executeBatchByDS(null,list);
                }
            }
            sb.append(getLoanCount());
            sb.append(getBlLoanCount());
            sb.append(getCKChart());
            sb.append(getDKChart());
            return "统计存款数据成功"+sb.toString();
        }catch(Exception e){
            e.printStackTrace();
            return "统计存款数据失败"+sb.toString();
        }
    }

    /**
     * 存款图表
     * @return
     */
    public String getCKChart(){
        try{
            String count=dmo.getStringValueByDS(null,"select dates from hzdb.s_app_chart where dates='"+DateUIUtil.getQYTTime()+"' and type='存款' and rownum=1");
            if(count==null) {
                String grhqs = dmo.getStringValueByDS(null, "select biz_dt from hzbank.a_agr_dep_acct_psn_sv_" + DateUIUtil.getqytMonth() + " where biz_dt='" + DateUIUtil.getQYTTime() + "' and rownum=1");
                String grdqs = dmo.getStringValueByDS(null, "select biz_dt from hzbank.A_AGR_DEP_ACCT_PSN_FX_" + DateUIUtil.getqytMonth() + " where biz_dt='" + DateUIUtil.getQYTTime() + "' and rownum=1");
                String dghgs = dmo.getStringValueByDS(null, "select biz_dt from hzbank.a_agr_dep_acct_ent_fx_" + DateUIUtil.getqytMonth() + " where biz_dt='" + DateUIUtil.getQYTTime() + "' and rownum=1");
                String dgdqs = dmo.getStringValueByDS(null, "select biz_dt from hzbank.a_agr_dep_acct_ent_sv_" + DateUIUtil.getqytMonth() + " where biz_dt='" + DateUIUtil.getQYTTime() + "' and rownum=1");
                String kuxxs = dmo.getStringValueByDS(null, "select biz_dt from hzbank.S_OFCR_CI_CUSTMAST_" + DateUIUtil.getqytMonth() + " where load_dates='" + DateUIUtil.getQYTTime() + "' and rownum=1");

                if (grhqs == null || grdqs == null || dghgs == null || dgdqs == null || kuxxs == null) {

                } else {
                    CockpitServiceImpl service=new CockpitServiceImpl();
                    String [][] data=service.getQhCkCompletion();
                    String [][] data2=service.getQhCkCompletion2();
                    List list=new ArrayList();
                    InsertSQLBuilder insertSQLBuilder=new InsertSQLBuilder("s_app_chart");
                    for(int i=0;i<data.length;i++){
                        insertSQLBuilder.putFieldValue("value",data[i][0]);
                        insertSQLBuilder.putFieldValue("typename",data[i][1]);
                        insertSQLBuilder.putFieldValue("month",data[i][2]);
                        insertSQLBuilder.putFieldValue("type","存款");
                        insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                        list.add(insertSQLBuilder.getSQL());
                    }
                    for(int i=0;i<data2.length;i++){
                        insertSQLBuilder.putFieldValue("value",data2[i][0]);
                        insertSQLBuilder.putFieldValue("typename",data2[i][1]);
                        insertSQLBuilder.putFieldValue("month",data2[i][2]);
                        insertSQLBuilder.putFieldValue("type","存款");
                        insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                        list.add(insertSQLBuilder.getSQL());
                    }
                    dmo.executeBatchByDS(null,list);
                }
            }
            return "存款图表统计成功";
        }catch (Exception e){
            e.printStackTrace();
            return "存款图表统计失败";
        }
    }
    /**
     * 贷款图表
     * @return
     */
    public String getDKChart(){
        try{
            String count=dmo.getStringValueByDS(null,"select dates from hzdb.s_app_chart where dates='"+DateUIUtil.getQYTTime()+"' and type='贷款' and rownum=1");
            if(count==null) {
                String dk=dmo.getStringValueByDS(null,"select biz_dt from hzbank.s_loan_dk_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                if(dk==null) {

                } else {
                    CockpitServiceImpl service=new CockpitServiceImpl();
                    String [][] data=service.getqhDkWcCount();
                    List list=new ArrayList();
                    for(int i=0;i<data.length;i++){
                        InsertSQLBuilder insertSQLBuilder=new InsertSQLBuilder("s_app_chart");
                        insertSQLBuilder.putFieldValue("value",data[i][0]);
                        insertSQLBuilder.putFieldValue("typename",data[i][1]);
                        insertSQLBuilder.putFieldValue("month",data[i][2]);
                        insertSQLBuilder.putFieldValue("type","贷款");
                        insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                        list.add(insertSQLBuilder.getSQL());
                        InsertSQLBuilder insertSQLBuilder2=new InsertSQLBuilder("s_app_chart");
                        insertSQLBuilder2.putFieldValue("value",data[i][3]);
                        insertSQLBuilder2.putFieldValue("typename",data[i][4]);
                        insertSQLBuilder2.putFieldValue("month",data[i][5]);
                        insertSQLBuilder2.putFieldValue("type","贷款");
                        insertSQLBuilder2.putFieldValue("dates",DateUIUtil.getQYTTime());
                        list.add(insertSQLBuilder2.getSQL());
                    }
                    dmo.executeBatchByDS(null,list);
                }
            }
            return "贷款图表统计成功";
        }catch (Exception e){
            e.printStackTrace();
            return "贷款图表统计失败";
        }
    }
    /**
     * zzl
     * 统计贷款
     */
    public String getLoanCount(){
        try{
            String count=dmo.getStringValueByDS(null,"select dates from s_app_count where dates='"+DateUIUtil.getQYTTime()+"' and type='贷款' and rownum=1");
            if(count==null){
                String dk=dmo.getStringValueByDS(null,"select biz_dt from hzbank.s_loan_dk_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                if(dk==null) {

                }else{
                    CockpitServiceImpl service=new CockpitServiceImpl();
                     String [][] gxdk=service.getDkStatistical();
                     String [][] dkhs=service.getDkStatisticalHs();
                     String [][] bmdk=service.getBmDkStatisticalHs();
                     String [][] gtdk=service.getGtDkStatisticalHs();
                     String [][] xdldk=service.getXdlDkStatisticalHs();
                     String [][] nhdk=service.getnhDkStatisticalHs();
                    List list=new ArrayList();
                    InsertSQLBuilder insertSQLBuilder=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder.putFieldValue("typename","各项贷款总额:"+gxdk[0][0]+"亿元");
                    insertSQLBuilder.putFieldValue("typevalue","较年初新增:"+gxdk[0][1]+"亿元");
                    insertSQLBuilder.putFieldValue("type","贷款");
                    insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder.getSQL());
                    InsertSQLBuilder insertSQLBuilder1=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder1.putFieldValue("typename","有效贷款户数:"+dkhs[0][0]+"万户");
                    insertSQLBuilder1.putFieldValue("typevalue","较年初新增:"+dkhs[0][1]+"户");
                    insertSQLBuilder1.putFieldValue("type","贷款");
                    insertSQLBuilder1.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder1.getSQL());
                    InsertSQLBuilder insertSQLBuilder2=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder2.putFieldValue("typename","便民快贷:"+bmdk[0][0]+"户");
                    insertSQLBuilder2.putFieldValue("typevalue","较年初新增:"+bmdk[0][1]+"户");
                    insertSQLBuilder2.putFieldValue("type","贷款");
                    insertSQLBuilder2.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder2.getSQL());
                    InsertSQLBuilder insertSQLBuilder3=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder3.putFieldValue("typename","个体工商户:"+gtdk[0][0]+"户");
                    insertSQLBuilder3.putFieldValue("typevalue","较年初新增:"+gtdk[0][1]+"户");
                    insertSQLBuilder3.putFieldValue("type","贷款");
                    insertSQLBuilder3.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder3.getSQL());
                    InsertSQLBuilder insertSQLBuilder4=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder4.putFieldValue("typename","薪动力:"+xdldk[0][0]+"户");
                    insertSQLBuilder4.putFieldValue("typevalue","较年初新增:"+xdldk[0][1]+"户");
                    insertSQLBuilder4.putFieldValue("type","贷款");
                    insertSQLBuilder4.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder4.getSQL());
                    InsertSQLBuilder insertSQLBuilder5=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder5.putFieldValue("typename","农户:"+nhdk[0][0]+"户");
                    insertSQLBuilder5.putFieldValue("typevalue","较年初新增:"+nhdk[0][1]+"户");
                    insertSQLBuilder5.putFieldValue("type","贷款");
                    insertSQLBuilder5.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder5.getSQL());
                    dmo.executeBatchByDS(null,list);
                }
            }
            return "统计贷款数据成功";
        }catch(Exception e){
            e.printStackTrace();
            return "统计贷款数据失败";
        }
    }

    /**
     * zzl 不良贷款统计
     * @return
     */
    public String getBlLoanCount(){
        try{
            String count=dmo.getStringValueByDS(null,"select dates from s_app_count where dates='"+DateUIUtil.getQYTTime()+"' and type='不良贷款' and rownum=1");
            if(count==null){
                String dk=dmo.getStringValueByDS(null,"select biz_dt from hzbank.s_loan_dk_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                if(dk==null) {

                }else{
                    CockpitServiceImpl service=new CockpitServiceImpl();
                    String [][] gxck=service.getBlDKCount();
                    String [][] gxck2=service.getBlDKCount2();
                    String [][] grhq=service.getShBlDKLvCount();
                    String [][] grdq=service.getxzBlDKLvCount();
                    String [][] dghq=service.getShBwBlDKLvCount();
                    List list=new ArrayList();
                    InsertSQLBuilder insertSQLBuilder=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder.putFieldValue("typename","60天以上不良贷款总额:"+gxck[0][0]+"亿元");
                    insertSQLBuilder.putFieldValue("typevalue","较年初新增:"+gxck[0][1]+"亿元");
                    insertSQLBuilder.putFieldValue("type","不良贷款");
                    insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder.getSQL());
                    InsertSQLBuilder insertSQLBuilder1=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder1.putFieldValue("typename","90天以上不良贷款总额:"+gxck2[0][0]+"亿元");
                    insertSQLBuilder1.putFieldValue("typevalue","较年初新增:"+gxck2[0][1]+"亿元");
                    insertSQLBuilder1.putFieldValue("type","不良贷款");
                    insertSQLBuilder1.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder1.getSQL());
                    InsertSQLBuilder insertSQLBuilder2=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder2.putFieldValue("typename","本月收回60以上不良贷款:"+grhq[0][0]+"万元");
                    insertSQLBuilder2.putFieldValue("typevalue","");
                    insertSQLBuilder2.putFieldValue("type","不良贷款");
                    insertSQLBuilder2.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder2.getSQL());
                    InsertSQLBuilder insertSQLBuilder3=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder3.putFieldValue("typename","本月收回90以上不良贷款:"+grdq[0][0]+"万元");
                    insertSQLBuilder3.putFieldValue("typevalue","");
                    insertSQLBuilder3.putFieldValue("type","不良贷款");
                    insertSQLBuilder3.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder3.getSQL());
                    InsertSQLBuilder insertSQLBuilder4=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder4.putFieldValue("typename","本月收回表外不良贷款:"+dghq[0][0]+"万元");
                    insertSQLBuilder4.putFieldValue("typevalue","");
                    insertSQLBuilder4.putFieldValue("type","不良贷款");
                    insertSQLBuilder4.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder4.getSQL());
                    dmo.executeBatchByDS(null,list);
                }
            }
            return "统计不良贷款数据成功";
        }catch(Exception e){
            e.printStackTrace();
            return "统计不良贷款数据失败";
        }
    }
}
