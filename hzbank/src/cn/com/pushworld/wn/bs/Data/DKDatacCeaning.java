package cn.com.pushworld.wn.bs.Data;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.wn.bs.DepositTimingJob;

import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * zzl
 * ������������
 */
public class DKDatacCeaning implements WLTJobIFC {
    private CommDMO dmo = new CommDMO();
    @Override
    public String run() throws Exception {
        StringBuilder sb=new StringBuilder();
        StringBuilder rzsb=new StringBuilder();
        try{
            String [] column=dmo.getStringArrayFirstColByDS("hzbank","select COLUMN_NAME from user_tab_columns  WHERE TABLE_NAME='S_LOAN_DK_"+getKHMonthTime(1)+"' ORDER BY COLUMN_ID");
            for(int i=0;i<column.length;i++){
                if(i==column.length-1){
                    sb.append(column[i]);
                }else{
                    sb.append(column[i]+",");
                }
            }
            //zzl ÿ���µ�1�Ž��ϸ��µĴ������ݲ��뵽�����
            System.out.println(">>>>>>>>>>>>>>>"+getKHDQMonth());
            if(getKHDQMonth().equals("01")) {
                String count[]=dmo.getStringArrayFirstColByDS(null,"select biz_dt from hzbank.s_loan_dk_"+getKHMonthTime(0)+" where biz_dt<'"+getYCTime(0)+"' and rownum=1");
                if(count.length==0){
                    System.out.println(">>>>>>>>>>>>>>>>>>>>"+"insert into hzbank.s_loan_dk_"+getKHMonthTime(0) +" \n" +
                            "select * from hzbank.s_loan_dk_"+getKHMonthTime(1)+" \n" +
                            "where XD_COL1||BIZ_DT in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getKHMonthTime(1)+" group by XD_COL1)");
                    dmo.executeUpdateByDS(null, "insert into hzbank.s_loan_dk_"+getKHMonthTime(0) +" \n" +
                            "select * from hzbank.s_loan_dk_"+getKHMonthTime(1)+" \n" +
                            "where XD_COL1||BIZ_DT in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getKHMonthTime(1)+" group by XD_COL1)");
                }
            }
            //zzl ÿ�촴����������������ҪΪ����߲�ѯ�ٶ�
            //dk �����
              String dk=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.s_loan_dk_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'");
            if(dk==null){

            }else{
                //zzl ���ݵ�ǰ��������µ�����״̬�޸��弶����
                dmo.executeUpdateByDS(null,"MERGE INTO hzbank.s_loan_dk_"+getQYDayMonth()+" a\n" +
                        "USING (select XD_COL1,XD_COL22 from hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT in(\n" +
                        "select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" where BIZ_DT='"+getQYTTime()+"' group by XD_COL1)) b\n" +
                        "ON (a.XD_COL1=b.XD_COL1) WHEN MATCHED THEN UPDATE SET a.XD_COL22=b.XD_COL22");//BIZ_DT='"+getXZTime()+"' BIZ_DT>'"+getSYMTime(1)+"'
            }
            //dk �����
            String hk=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.s_loan_hk where biz_dt='"+getQYTTime()+"'");
            if(dk==null || hk==null){

            }else{
                //zzl ��ʱ��ʡ����������ݲ����·���ֻ��ȥ������Ϣ����Ȼ���޸����-----������
//                dmo.executeUpdateByDS(null,"MERGE INTO hzbank.s_loan_dk_"+getQYDayMonth()+" a USING (\n" +
//                        "select XD_COL1,sum(XD_COL5) khye from hzbank.s_loan_hk where XD_COL1||xd_col4 in(select XD_COL1||max(xd_col4) from hzbank.s_loan_hk group by XD_COL1)\n" +
//                        "and to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')<='"+getDQymTime()+"' group by XD_COL1) \n" +
//                        "b ON (a.XD_COL1=b.XD_COL1) WHEN MATCHED THEN UPDATE SET a.XD_COL7=a.XD_COL7-b.khye");
            }
            String dkstate=dmo.getStringValueByDS(null,"select dkdates from hzdb.s_count_cdk where dkdates='"+getQYTTime()+"' and ckdates='"+getQYTTime()+"'");
            if(dkstate==null){
                return "OK";
            }
            String [] createDate= dmo.getStringArrayFirstColByDS(null,"select CREATED from dba_objects where object_name = 'GRID_DATA_"+getQYTTime()+"' and OBJECT_TYPE='TABLE'");
            if(createDate.length>0){

            }else{
                //���ڴ��
                String ck=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.a_agr_dep_acct_psn_sv_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'");
                //���ڴ��
                String dqck=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'\n");
                //���ͻ�����
                String ckkhxx=dmo.getStringValueByDS("hzbank","select distinct(load_dates) from  hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" where load_dates='"+getQYTTime()+"'");
                //ǭũ��
                int xj=1;
                String qnyyx=dmo.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'S_LOAN_QNYYX_"+getSMonth(xj)+"' and OBJECT_TYPE='TABLE'");
                if(qnyyx==null){
                    xj=2;
                }
                //�Թ����ڴ��
                String dgck=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.a_agr_dep_acct_ent_sv_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'");
                //�Թ����ڴ��
                String dgdqck=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.a_agr_dep_acct_ent_fx_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'\n");
                if(dk==null || ck==null || dqck==null || ckkhxx==null || dgck==null || dgdqck==null){

                }else{
                    dmo.executeUpdateByDS(null,"create table hzdb.grid_data_"+getQYTTime()+" as select wg.*,ck.oact_inst_no,ck.name,ck.ckye,dk.dkye,case when jd.jdxx is null then '��' else jd.jdxx end jdxx ,\n" +
                            "case when qny.sf is null then '��' else qny.sf end qny ,'��ͥ��Ա' num,dg.ckye dgck,dgdk.dgdkye dgdkye " +
                            "from (select wg.*,dept.B code from hzdb.S_LOAN_KHXX_202001 wg left join hzdb.excel_tab_28 dept on (case when INSTR(wg.deptcode,'-')>0  then substr(wg.deptcode,0,INSTR(deptcode,'-')-1) else wg.deptcode end)=dept.C) wg left join \n" +
                            "(select  a.oact_inst_no,b.EXTERNAL_CUSTOMER_IC name,sum(a.f) ckye from \n" +
                            "(select oact_inst_no,cust_no cust_no,sum(f) f \n" +
                            "from hzbank.a_agr_dep_acct_psn_sv_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"' group by oact_inst_no,cust_no union all \n" +
                            "select oact_inst_no,cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'\n" +
                            "group by oact_inst_no,cust_no) a left join (select * from hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" where load_dates='"+getQYTTime()+"')  b on a.cust_no = b.COD_CUST_ID \n" +
                            "group by a.oact_inst_no,b.EXTERNAL_CUSTOMER_IC) ck on  wg.deptcode=ck.oact_inst_no and UPPER(wg.G)=UPPER(ck.name)\n" +
                            "left join (select '283'||XD_COL85 XD_COL85,XD_COL16,sum(XD_COL7) dkye from hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT in(\n" +
                            "select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" group by XD_COL1)\n" +
                            "and XD_COL4<'"+getXZTime()+" 00:00:00' group by XD_COL85,XD_COL16) dk on wg.code=dk.XD_COL85 and UPPER(wg.G)=UPPER(dk.XD_COL16)\n" +
                            "left join(select '283'||XD_COL2 XD_COL2,XD_COL7 XD_COL7,'�ѽ���' jdxx from hzbank.s_loan_khxx_"+getQYDayMonth()+" where to_char(cast (cast (XD_COL3 as timestamp) as date),'yyyy-mm-dd')>='"+getKHMonthTime()+"'\n" +
                            "and to_char(cast (cast (XD_COL3 as timestamp) as date),'yyyy-mm-dd')<='"+getYmMonthTime()+"' and XD_COL32 >0 and XD_COL158>0 \n" +
                            "and  XD_COL10 in ('����','�ػݼ�','һ��','����','�Ϻ�') group by XD_COL2,XD_COL7) jd on wg.code=jd.XD_COL2 and UPPER(wg.G)=UPPER(jd.XD_COL7)\n" +
                            "left join (select C,F,'��' sf  from hzdb.s_loan_qnyyx_"+getSMonth(xj)+" group by C,F\n) qny\n" +
                            "on wg.deptcode=qny.c and UPPER(wg.G)=UPPER(qny.f) left join(\n" +
                            "select a.deptcode deptcode,b.EXTERNAL_CUSTOMER_IC g,sum(a.ckye) ckye from(\n" +
                            "select cust_no g,oact_inst_no deptcode,sum(f )ckye from(\n" +
                            "select cust_no,oact_inst_no,acct_bal f from hzbank.a_agr_dep_acct_ent_fx_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0\n" +
                            "union all\n" +
                            "select cust_no,oact_inst_no,acct_bal f from hzbank.a_agr_dep_acct_ent_sv_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0)\n" +
                            "group by cust_no,oact_inst_no) a \n" +
                            "left join(select * from hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" where load_dates='"+getQYTTime()+"')b\n" +
                            "on a.g=b.COD_CUST_ID group by a.deptcode,b.EXTERNAL_CUSTOMER_IC) dg on wg.deptcode=dg.deptcode and UPPER(wg.G)=UPPER(dg.G) left join\n" +
                            "(select '2830001-xd' deptcode,xx.CERT_CODE sfz,sum(dk.LOAN_BALANCE)  dgdkye from(\n" +
                            "select * from hzbank.S_CMIS_ACC_LOAN_"+getQYDayMonth()+" where CUS_ID||biz_dt in(\n" +
                            "select CUS_ID||max(biz_dt) from hzbank.S_CMIS_ACC_LOAN_"+getQYDayMonth()+" group by CUS_ID)) dk left join\n" +
                            "hzbank.S_CMIS_CUS_BASE_"+getQYDayMonth()+" xx on dk.CUS_ID=xx.CUS_ID where dk.LOAN_BALANCE>0 group by xx.CERT_CODE\n" +
                            ") dgdk on wg.deptcode=dgdk.deptcode and UPPER(wg.G)=UPPER(dgdk.sfz)");
                    //zzl ���¶Թ�������
                    dmo.executeUpdateByDS(null,"update hzdb.GRID_DATA_"+getQYTTime()+" set ckye=dgck where J='�Թ����' and K='�Թ����'");
                    //zzl ���¶Թ�������
                    dmo.executeUpdateByDS(null,"update hzdb.GRID_DATA_"+getQYTTime()+" set dkye=dgdkye where deptcode='2830001-xd' and dkye is null");
                    //zzl ��ÿ��Ļ���������������������������վ�
                    dmo.executeUpdateByDS(null,"insert into hzdb.s_dept_ave\n" +
                            "select deptcode,sum(ckye),sum(dkye),'"+getQYTTime()+"' from hzdb.GRID_DATA_"+getQYTTime()+" group by deptcode");
                    //zzl ��ÿ��Ŀͻ���������������������ͻ��������վ�
                    dmo.executeUpdateByDS(null,"insert into hzdb.s_user_ave\n" +
                            "select wg.g,sum(ck.ckye),sum(dkye),'"+getQYTTime()+"' from hzdb.GRID_DATA_"+getQYTTime()+" ck left join hzdb.EXCEL_TAB_85 wg on ck.j=wg.c and ck.k=wg.d and ck.deptcode=wg.f\n" +
                            "group by wg.g");

                    }
            }
            String [] createDate2= dmo.getStringArrayFirstColByDS(null,"select CREATED from dba_objects where object_name = 'GRID_DATA_"+getQYTTime()+"' and OBJECT_TYPE='TABLE'");
            if(getKHDQMonth().equals("01")) {
                //zzl ÿ����1�Ŵ�������վ���
                if(createDate2.length>0){
                    dmo.executeUpdateByDS(null,"create table hzdb.s_user_ckavg_"+getQYDayMonth()+" as\n" +
                            "select deptcode deptcode,g code,sum(ckye) avgnum,1 daynum from hzdb.GRID_DATA_"+getQYTTime()+" where ckye>0 and ckye is not null group by deptcode,g");
                }
                //zzl ÿ����1�Ŵ��������վ���
                if(createDate2.length>0){
                    dmo.executeUpdateByDS(null,"create table hzdb.s_user_dkavg_"+getQYDayMonth()+" as\n" +
                            "select * from(select XD_COL85 deptcode,XD_COL16 code,sum(XD_COL7) avgnum,1 daynum from(\n" +
                            "select XD_COL1,XD_COL85,case when XD_COL7>=70000 then 70000 else XD_COL7 end XD_COL7,XD_COL22,XD_COL16 from hzbank.s_loan_dk_"+getQYDayMonth()+" \n" +
                            "where XD_COL1||BIZ_DT in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL22<>'05'  group by XD_COL1)and \n" +
                            "XD_COL4<'"+getDownMonth(getQYDayMonth())+" 00:00:00' and XD_COL7>0 and biz_dt<='"+getQYTTime()+"') group by XD_COL85,XD_COL16)" +
                            " union all (select '30100' deptcode,xx.CERT_CODE code,case when sum(dk.LOAN_BALANCE)>70000 then 70000 else sum(dk.LOAN_BALANCE) end  avgnum,1 daynum from(\n" +
                            "select * from hzbank.S_CMIS_ACC_LOAN_"+getQYDayMonth()+" where CUS_ID||biz_dt in(\n" +
                            "select CUS_ID||max(biz_dt) from hzbank.S_CMIS_ACC_LOAN_"+getQYDayMonth()+" group by CUS_ID)) dk left join\n" +
                            "hzbank.S_CMIS_CUS_BASE_"+getQYDayMonth()+" xx on dk.CUS_ID=xx.CUS_ID where dk.LOAN_BALANCE>0 and dk.cla<>'05' group by xx.CERT_CODE)" );
                }
                //zzl ÿ����1�Ŵ������Ŵ����վ���
                if(createDate2.length>0){
                    dmo.executeUpdateByDS(null,"create table hzdb.s_dept_dkavg_"+getQYDayMonth()+" as\n" +
                            "select dept.c deptcode,sum(dk.num) avgnum,1 daynum from(\n" +
                            "select * from(\n" +
                            "select case when XD_COL85='30100' then '28330100-xd' else '283'||XD_COL85 end code,sum(XD_COL7) num from \n" +
                            "hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" \n" +
                            "where XD_COL22<>'05' and biz_dt<='"+getQYTTime()+"' group by XD_COL1) and XD_COL4<'"+getDownMonth(getQYDayMonth())+" 00:00:00' group by XD_COL85)\n" +
                            "union all(\n" +
                            "select '28330100-xd' code,sum(LOAN_BALANCE) num from hzbank.S_CMIS_ACC_LOAN_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"' and CLA<>'05')) dk\n" +
                            "left join hzdb.excel_tab_28 dept on dk.code=dept.b group by dept.c");
                }
                dmo.executeUpdateByDS(null,"insert into count_avg(dates) values('"+getQYTTime()+"')");
            }else if(createDate2.length>0){
                String count=dmo.getStringValueByDS(null,"select dates from hzdb.count_avg where dates='"+getQYTTime()+"'");
                if(count==null){
                    //zzl �����˾��վ������������� �����1���ǲ������
                    //zzl 1 ---------2.������ʱ��----
                    dmo.executeUpdateByDS(null,"create table hzdb.s_user_ckavg_"+getQYDayMonth()+"_ls as\n" +
                            "select deptcode deptcode,g code,sum(ckye) avgnum,1 daynum from hzdb.GRID_DATA_"+getQYTTime()+" where ckye>0 and ckye is not null group by deptcode,g");
                    //------3.��ʱ�������µ����ݸ����޸���ʳ�������----
                    dmo.executeUpdateByDS(null,"MERGE INTO hzdb.s_user_ckavg_"+getQYDayMonth()+"_ls a\n" +
                            "USING(select * from hzdb.s_user_ckavg_"+getQYDayMonth()+") b\n" +
                            "on(a.deptcode=b.deptcode and a.code=b.code) \n" +
                            "WHEN MATCHED THEN UPDATE SET a.avgnum=(case when a.avgnum is null then 0 else a.avgnum end)+(case when b.avgnum is null then 0 else b.avgnum end),\n" +
                            "a.daynum=(case when a.daynum is null then 0 else a.daynum end)+(case when b.daynum is null then 0 else b.daynum end)");
                    //-----4.ɾ���ɽ����
                    dmo.executeUpdateByDS(null,"drop table hzdb.s_user_ckavg_"+getQYDayMonth()+"");
                    //----5.����ʱ���½���ָ����ɱ���
                    dmo.executeUpdateByDS(null,"create table hzdb.s_user_ckavg_"+getQYDayMonth()+" as select * from hzdb.s_user_ckavg_"+getQYDayMonth()+"_ls");
                    //----6.ɾ����ʱ��-----
                    dmo.executeUpdateByDS(null,"drop table hzdb.s_user_ckavg_"+getQYDayMonth()+"_ls");


                    //--------------�����-------------//
                    //---------2.������ʱ��----
                    dmo.executeUpdateByDS(null,"create table hzdb.s_user_dkavg_"+getQYDayMonth()+"_ls as\n" +
                            "select * from(select XD_COL85 deptcode,XD_COL16 code,sum(XD_COL7) avgnum,1 daynum from(\n" +
                            "select XD_COL1,XD_COL85,case when XD_COL7>=70000 then 70000 else XD_COL7 end XD_COL7,XD_COL22,XD_COL16 from hzbank.s_loan_dk_"+getQYDayMonth()+" \n" +
                            "where XD_COL1||BIZ_DT in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL22<>'05'  group by XD_COL1)and \n" +
                            "XD_COL4<'"+getDownMonth(getQYDayMonth())+" 00:00:00' and XD_COL7>0 and biz_dt<='"+getQYTTime()+"') group by XD_COL85,XD_COL16)" +
                            "union all\n" +
                            "(select '30100' deptcode,xx.CERT_CODE code,case when sum(dk.LOAN_BALANCE)>70000 then 70000 else sum(dk.LOAN_BALANCE) end  avgnum,1 daynum from(\n" +
                            "select * from hzbank.S_CMIS_ACC_LOAN_"+getQYDayMonth()+" where CUS_ID||biz_dt in(\n" +
                            "select CUS_ID||max(biz_dt) from hzbank.S_CMIS_ACC_LOAN_"+getQYDayMonth()+" group by CUS_ID)) dk left join\n" +
                            "hzbank.S_CMIS_CUS_BASE_"+getQYDayMonth()+" xx on dk.CUS_ID=xx.CUS_ID where dk.LOAN_BALANCE>0 and dk.cla<>'05' group by xx.CERT_CODE)");
                    ///------3.��ʱ�������µ����ݸ����޸���ʳ�������----//
                    dmo.executeUpdateByDS(null,"MERGE INTO hzdb.s_user_dkavg_"+getQYDayMonth()+"_ls a\n" +
                            "USING(select * from hzdb.s_user_dkavg_"+getQYDayMonth()+") b\n" +
                            "on(a.deptcode=b.deptcode and a.code=b.code) \n" +
                            "WHEN MATCHED THEN UPDATE SET a.avgnum=(case when a.avgnum is null then 0 else a.avgnum end)+(case when b.avgnum is null then 0 else b.avgnum end),\n" +
                            "  a.daynum=(case when a.daynum is null then 0 else a.daynum end)+(case when b.daynum is null then 0 else b.daynum end)");
                    //-----4.ɾ���ɽ����
                    dmo.executeUpdateByDS(null,"drop table hzdb.s_user_dkavg_"+getQYDayMonth()+"");
                    //----5.����ʱ���½���ָ����ɱ���
                    dmo.executeUpdateByDS(null,"create table hzdb.s_user_dkavg_"+getQYDayMonth()+" as select * from hzdb.s_user_dkavg_"+getQYDayMonth()+"_ls");
                    //----6.ɾ����ʱ��-----
                    dmo.executeUpdateByDS(null,"drop table hzdb.s_user_dkavg_"+getQYDayMonth()+"_ls");

                    //-------------------�����վ�����-----------------------------//
                    //---------2.������ʱ��----
                    dmo.executeUpdateByDS(null,"create table hzdb.s_dept_dkavg_"+getQYDayMonth()+"_ls as\n" +
                            "select dept.c deptcode,sum(dk.num) avgnum,1 daynum from(\n" +
                            "select * from(\n" +
                            "select case when XD_COL85='30100' then '28330100-xd' else '283'||XD_COL85 end code,sum(XD_COL7) num from \n" +
                            "hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" \n" +
                            "where XD_COL22<>'05' and biz_dt<='"+getQYTTime()+"' group by XD_COL1) and XD_COL4<'"+getDownMonth(getQYDayMonth())+" 00:00:00' group by XD_COL85)\n" +
                            "union all(\n" +
                            "select '28330100-xd' code,sum(LOAN_BALANCE) num from hzbank.S_CMIS_ACC_LOAN_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"' and CLA<>'05')) dk\n" +
                            "left join hzdb.excel_tab_28 dept on dk.code=dept.b group by dept.c");
                    ///------3.��ʱ�������µ����ݸ����޸���ʳ�������----//
                    dmo.executeUpdateByDS(null,"MERGE INTO hzdb.s_dept_dkavg_"+getQYDayMonth()+"_ls a\n" +
                            "USING(select * from hzdb.s_dept_dkavg_"+getQYDayMonth()+") b\n" +
                            "on(a.deptcode=b.deptcode) \n" +
                            "WHEN MATCHED THEN UPDATE SET a.avgnum=(case when a.avgnum is null then 0 else a.avgnum end)+(case when b.avgnum is null then 0 else b.avgnum end),\n" +
                            "a.daynum=(case when a.daynum is null then 0 else a.daynum end)+(case when b.daynum is null then 0 else b.daynum end)");
                    //-----4.ɾ���ɽ����
                    dmo.executeUpdateByDS(null,"drop table hzdb.s_dept_dkavg_"+getQYDayMonth()+"");
                    //----5.����ʱ���½���ָ����ɱ���
                    dmo.executeUpdateByDS(null,"create table hzdb.s_dept_dkavg_"+getQYDayMonth()+" as select * from hzdb.s_dept_dkavg_"+getQYDayMonth()+"_ls");
                    //----6.ɾ����ʱ��-----
                    dmo.executeUpdateByDS(null,"drop table hzdb.s_dept_dkavg_"+getQYDayMonth()+"_ls");

                    //��¼��û�������˾�������оͲ�������
                    dmo.executeUpdateByDS(null,"insert into count_avg(dates) values('"+getQYTTime()+"')");

                }

            }
            return "��ϴ�������ݳɹ�";
        }catch (Exception e){
            e.printStackTrace();
            return "��ϴ��������ʧ��"+e.getStackTrace();
        }
    }
    public static void main(String[] args) {
        DKDatacCeaning a = new DKDatacCeaning();
        String inputParam = a.getKHMonthTime(0);
        System.out.println(">>>>>>>>>>>>>>" + inputParam);
    }
    /**
     * �õ�ʵ�ʵ������³�����zzl
     *
     * @return
     */
    public String getYCTime(int a) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -a);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(otherDate);
    }
    /**
     * �õ�ǰһ����³�����
     *
     * @return
     */
    public String getKHMonthTime(int a) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -a);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        return dateFormat.format(otherDate);
    }
    /**
     * �õ�ǰһ����³�����
     *
     * @return
     */
    public String getKHMonthTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, Calendar.DATE - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(otherDate);
    }

    /**
     * �õ�ǰһ�����ĩ����
     *
     * @return
     */
    public String getYmMonthTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, Calendar.DATE - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(otherDate);
    }
    /**
     * �õ���ǰ��ĩ����
     *
     * @return
     */
    public String getDQymTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, Calendar.DATE - 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(otherDate);
    }
    /**
     * �õ����ڵ�����
     *
     * @return
     */
    public String getXZTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     * ��ǰ�������� zzlǰһ��
     *cal.getActualMinimum(Calendar.DATE)
     * @return
     */
    public String getKHDQMonth() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     * �õ�ǰһ����·�
     *cal.getActualMinimum(Calendar.DATE)
     * @return
     */
    public String getQYDayMonth() {
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
     * �õ��¸��µ��³�����
     *cal.getActualMinimum(Calendar.DATE)
     * @return
     */
    public String getDownMonth(String dates) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        try {
            cal.setTime(format.parse(dates));
            cal.add(Calendar.MONTH, +1);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
        String lastDate = format2.format(cal.getTime());
        return lastDate;
    }

    /**
     * �õ�ǰһ�������
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
     * �õ�ָ�����·ݵ�����
     *
     * @return
     */
    public String getSMonth(int a) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date d = new Date();
        cal.setTime(d);
        cal.add(Calendar.MONTH, - a);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
}
