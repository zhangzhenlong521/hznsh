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
 * ��ЧAPP������ͳ������Ϊ����߽ӿ����ܣ���Ҫÿ������ͳ�Ƶ���ʱ��
 * @author Dragon
 * @date 2021/2/23
 */
public class SalaryAppDataJob implements WLTJobIFC {
    private CommDMO dmo = new CommDMO();
    @Override
    public String run() throws Exception {
        StringBuilder sb=new StringBuilder();
        try{
            String count=dmo.getStringValueByDS(null,"select dates from s_app_count where dates='"+DateUIUtil.getQYTTime()+"' and type='���' and rownum=1");
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
                    insertSQLBuilder.putFieldValue("typename","�������ܶ�:"+gxck[0][0]+"��Ԫ");
                    insertSQLBuilder.putFieldValue("typevalue","���������:"+gxck[0][1]+"��Ԫ");
                    insertSQLBuilder.putFieldValue("type","���");
                    insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder.getSQL());
                    InsertSQLBuilder insertSQLBuilder1=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder1.putFieldValue("typename","��Ч����:"+ckhs[0][0]+"��");
                    insertSQLBuilder1.putFieldValue("typevalue","���������:"+ckhs[0][1]+"��");
                    insertSQLBuilder1.putFieldValue("type","���");
                    insertSQLBuilder1.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder1.getSQL());
                    InsertSQLBuilder insertSQLBuilder2=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder2.putFieldValue("typename","���˻��ڴ��:"+grhq[0][0]+"��Ԫ");
                    insertSQLBuilder2.putFieldValue("typevalue","���������:"+grhq[0][1]+"��Ԫ");
                    insertSQLBuilder2.putFieldValue("type","���");
                    insertSQLBuilder2.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder2.getSQL());
                    InsertSQLBuilder insertSQLBuilder3=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder3.putFieldValue("typename","���˶��ڴ��:"+grdq[0][0]+"��Ԫ");
                    insertSQLBuilder3.putFieldValue("typevalue","���������:"+grdq[0][1]+"��Ԫ");
                    insertSQLBuilder3.putFieldValue("type","���");
                    insertSQLBuilder3.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder3.getSQL());
                    InsertSQLBuilder insertSQLBuilder4=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder4.putFieldValue("typename","�Թ����ڴ��:"+dghq[0][0]+"��Ԫ");
                    insertSQLBuilder4.putFieldValue("typevalue","���������:"+dghq[0][1]+"��Ԫ");
                    insertSQLBuilder4.putFieldValue("type","���");
                    insertSQLBuilder4.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder4.getSQL());
                    InsertSQLBuilder insertSQLBuilder5=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder5.putFieldValue("typename","�Թ����ڴ��:"+dgdq[0][0]+"��Ԫ");
                    insertSQLBuilder5.putFieldValue("typevalue","���������:"+dgdq[0][1]+"��Ԫ");
                    insertSQLBuilder5.putFieldValue("type","���");
                    insertSQLBuilder5.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder5.getSQL());
                    dmo.executeBatchByDS(null,list);
                }
            }
            sb.append(getLoanCount());
            sb.append(getBlLoanCount());
            sb.append(getCKChart());
            sb.append(getDKChart());
            return "ͳ�ƴ�����ݳɹ�"+sb.toString();
        }catch(Exception e){
            e.printStackTrace();
            return "ͳ�ƴ������ʧ��"+sb.toString();
        }
    }

    /**
     * ���ͼ��
     * @return
     */
    public String getCKChart(){
        try{
            String count=dmo.getStringValueByDS(null,"select dates from hzdb.s_app_chart where dates='"+DateUIUtil.getQYTTime()+"' and type='���' and rownum=1");
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
                        insertSQLBuilder.putFieldValue("type","���");
                        insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                        list.add(insertSQLBuilder.getSQL());
                    }
                    for(int i=0;i<data2.length;i++){
                        insertSQLBuilder.putFieldValue("value",data2[i][0]);
                        insertSQLBuilder.putFieldValue("typename",data2[i][1]);
                        insertSQLBuilder.putFieldValue("month",data2[i][2]);
                        insertSQLBuilder.putFieldValue("type","���");
                        insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                        list.add(insertSQLBuilder.getSQL());
                    }
                    dmo.executeBatchByDS(null,list);
                }
            }
            return "���ͼ��ͳ�Ƴɹ�";
        }catch (Exception e){
            e.printStackTrace();
            return "���ͼ��ͳ��ʧ��";
        }
    }
    /**
     * ����ͼ��
     * @return
     */
    public String getDKChart(){
        try{
            String count=dmo.getStringValueByDS(null,"select dates from hzdb.s_app_chart where dates='"+DateUIUtil.getQYTTime()+"' and type='����' and rownum=1");
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
                        insertSQLBuilder.putFieldValue("type","����");
                        insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                        list.add(insertSQLBuilder.getSQL());
                        InsertSQLBuilder insertSQLBuilder2=new InsertSQLBuilder("s_app_chart");
                        insertSQLBuilder2.putFieldValue("value",data[i][3]);
                        insertSQLBuilder2.putFieldValue("typename",data[i][4]);
                        insertSQLBuilder2.putFieldValue("month",data[i][5]);
                        insertSQLBuilder2.putFieldValue("type","����");
                        insertSQLBuilder2.putFieldValue("dates",DateUIUtil.getQYTTime());
                        list.add(insertSQLBuilder2.getSQL());
                    }
                    dmo.executeBatchByDS(null,list);
                }
            }
            return "����ͼ��ͳ�Ƴɹ�";
        }catch (Exception e){
            e.printStackTrace();
            return "����ͼ��ͳ��ʧ��";
        }
    }
    /**
     * zzl
     * ͳ�ƴ���
     */
    public String getLoanCount(){
        try{
            String count=dmo.getStringValueByDS(null,"select dates from s_app_count where dates='"+DateUIUtil.getQYTTime()+"' and type='����' and rownum=1");
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
                    insertSQLBuilder.putFieldValue("typename","��������ܶ�:"+gxdk[0][0]+"��Ԫ");
                    insertSQLBuilder.putFieldValue("typevalue","���������:"+gxdk[0][1]+"��Ԫ");
                    insertSQLBuilder.putFieldValue("type","����");
                    insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder.getSQL());
                    InsertSQLBuilder insertSQLBuilder1=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder1.putFieldValue("typename","��Ч�����:"+dkhs[0][0]+"��");
                    insertSQLBuilder1.putFieldValue("typevalue","���������:"+dkhs[0][1]+"��");
                    insertSQLBuilder1.putFieldValue("type","����");
                    insertSQLBuilder1.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder1.getSQL());
                    InsertSQLBuilder insertSQLBuilder2=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder2.putFieldValue("typename","������:"+bmdk[0][0]+"��");
                    insertSQLBuilder2.putFieldValue("typevalue","���������:"+bmdk[0][1]+"��");
                    insertSQLBuilder2.putFieldValue("type","����");
                    insertSQLBuilder2.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder2.getSQL());
                    InsertSQLBuilder insertSQLBuilder3=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder3.putFieldValue("typename","���幤�̻�:"+gtdk[0][0]+"��");
                    insertSQLBuilder3.putFieldValue("typevalue","���������:"+gtdk[0][1]+"��");
                    insertSQLBuilder3.putFieldValue("type","����");
                    insertSQLBuilder3.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder3.getSQL());
                    InsertSQLBuilder insertSQLBuilder4=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder4.putFieldValue("typename","н����:"+xdldk[0][0]+"��");
                    insertSQLBuilder4.putFieldValue("typevalue","���������:"+xdldk[0][1]+"��");
                    insertSQLBuilder4.putFieldValue("type","����");
                    insertSQLBuilder4.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder4.getSQL());
                    InsertSQLBuilder insertSQLBuilder5=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder5.putFieldValue("typename","ũ��:"+nhdk[0][0]+"��");
                    insertSQLBuilder5.putFieldValue("typevalue","���������:"+nhdk[0][1]+"��");
                    insertSQLBuilder5.putFieldValue("type","����");
                    insertSQLBuilder5.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder5.getSQL());
                    dmo.executeBatchByDS(null,list);
                }
            }
            return "ͳ�ƴ������ݳɹ�";
        }catch(Exception e){
            e.printStackTrace();
            return "ͳ�ƴ�������ʧ��";
        }
    }

    /**
     * zzl ��������ͳ��
     * @return
     */
    public String getBlLoanCount(){
        try{
            String count=dmo.getStringValueByDS(null,"select dates from s_app_count where dates='"+DateUIUtil.getQYTTime()+"' and type='��������' and rownum=1");
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
                    insertSQLBuilder.putFieldValue("typename","60�����ϲ��������ܶ�:"+gxck[0][0]+"��Ԫ");
                    insertSQLBuilder.putFieldValue("typevalue","���������:"+gxck[0][1]+"��Ԫ");
                    insertSQLBuilder.putFieldValue("type","��������");
                    insertSQLBuilder.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder.getSQL());
                    InsertSQLBuilder insertSQLBuilder1=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder1.putFieldValue("typename","90�����ϲ��������ܶ�:"+gxck2[0][0]+"��Ԫ");
                    insertSQLBuilder1.putFieldValue("typevalue","���������:"+gxck2[0][1]+"��Ԫ");
                    insertSQLBuilder1.putFieldValue("type","��������");
                    insertSQLBuilder1.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder1.getSQL());
                    InsertSQLBuilder insertSQLBuilder2=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder2.putFieldValue("typename","�����ջ�60���ϲ�������:"+grhq[0][0]+"��Ԫ");
                    insertSQLBuilder2.putFieldValue("typevalue","");
                    insertSQLBuilder2.putFieldValue("type","��������");
                    insertSQLBuilder2.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder2.getSQL());
                    InsertSQLBuilder insertSQLBuilder3=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder3.putFieldValue("typename","�����ջ�90���ϲ�������:"+grdq[0][0]+"��Ԫ");
                    insertSQLBuilder3.putFieldValue("typevalue","");
                    insertSQLBuilder3.putFieldValue("type","��������");
                    insertSQLBuilder3.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder3.getSQL());
                    InsertSQLBuilder insertSQLBuilder4=new InsertSQLBuilder("s_app_count");
                    insertSQLBuilder4.putFieldValue("typename","�����ջر��ⲻ������:"+dghq[0][0]+"��Ԫ");
                    insertSQLBuilder4.putFieldValue("typevalue","");
                    insertSQLBuilder4.putFieldValue("type","��������");
                    insertSQLBuilder4.putFieldValue("dates",DateUIUtil.getQYTTime());
                    list.add(insertSQLBuilder4.getSQL());
                    dmo.executeBatchByDS(null,list);
                }
            }
            return "ͳ�Ʋ����������ݳɹ�";
        }catch(Exception e){
            e.printStackTrace();
            return "ͳ�Ʋ�����������ʧ��";
        }
    }
}
