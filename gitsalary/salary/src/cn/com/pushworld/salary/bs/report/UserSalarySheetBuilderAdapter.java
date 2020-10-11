package cn.com.pushworld.salary.bs.report;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.salary.to.baseinfo.FormulaTool;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class UserSalarySheetBuilderAdapter extends MultiLevelReportDataBuilderAdapter {
    private CommDMO commdmo = null;
    private ReportUtil reportutil = null;
    private TBUtil tbutil = null;
    private String[] types = null;
    private String[] types_id = null;
    private String month_start;
    private String month_end;
    private LinkedHashMap hm = new LinkedHashMap();
    private HashMap map=new HashMap();
    public HashVO[] buildReportData(HashMap consMap) throws Exception {
        month_start = (String) consMap.get("month_start");
        month_end = (String) consMap.get("month_end");
        String counttype = (String) consMap.get("counttype");
        String salaryitems = (String) consMap.get("salaryitems");
        String account_set = new TBUtil().getSysOptionStringValue("综合报表工资统计帐套", "普通账套");
        HashVO[] hvs_log = getCommDMO().getHashVoArrayByDS(null, "select id, monthly from sal_salarybill where " + "sal_account_setid in (select id from sal_account_set where name in('" + account_set.replace(";", "','") + "')) " + "and monthly>='" + month_start + "' and monthly<='" + month_end + "' order by monthly");
        HashVO[] hvs_items = getCommDMO().getHashVoArrayByDS(null, "select factorid, viewname from sal_account_factor " + "where accountid in(select id from sal_account_set where name in('" + account_set.replace(";", "','") + "')) " + "and factorid in('" + salaryitems.replace(";", "','") + "') order by seq");
        for (int i = 0; i < hvs_log.length; i++) {
            String id = hvs_log[i].getStringValue("id", "");
            String monthly = hvs_log[i].getStringValue("monthly", "");
            if (hm.containsKey(monthly)) {
                hm.put(monthly, id + "," + hm.get(monthly));
                map.put(id,monthly);
            } else {
                hm.put(monthly, id);
                map.put(id,monthly);
            }
        }
        String[] str_keys = (String[]) hm.keySet().toArray(new String[0]);
        String[] checkdates = new String[str_keys.length];
        String[] checkids = new String[str_keys.length];
        for (int i = 0; i < str_keys.length; i++) {
            checkids[i] = (String) hm.get(str_keys[i]);
            checkdates[i] = str_keys[i];
        }

        types = new String[hvs_items.length];
        types_id = new String[hvs_items.length];
        for (int i = 0; i < hvs_items.length; i++) {
            types_id[i] = hvs_items[i].getStringValue("factorid", "");
            types[i] = hvs_items[i].getStringValue("viewname", "");
        }
        HashVO[] hvs = getPersonSalary(checkids, types_id);
        return hvs;
    }

    public String[] getGroupFieldNames() {
        return new String[]{"人员","部门","岗位归类","2020-05"};
    }

    public String[] getSumFiledNames() {
        return new String[]{"2020-05"};
    }

    public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
        ArrayList al_vos = new ArrayList();
        BeforeHandGroupTypeVO bhGroupVO = null;

        bhGroupVO = new BeforeHandGroupTypeVO();
        bhGroupVO.setName((al_vos.size() + 1 + "-部门"));
        bhGroupVO.setRowHeaderGroupFields(new String[] {});
        bhGroupVO.setColHeaderGroupFields(new String[] { "部门" });
        bhGroupVO.setComputeGroupFields(new String[][] { { "2020-5", "sum" }});
        bhGroupVO.setColGroupTiled(true);
        bhGroupVO.setType("GRID");
        al_vos.add(bhGroupVO);


        return (BeforeHandGroupTypeVO[]) al_vos.toArray(new BeforeHandGroupTypeVO[0]);
    }

    public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
        ArrayList al_vos = new ArrayList();
        BeforeHandGroupTypeVO bhGroupVO = null;

        bhGroupVO = new BeforeHandGroupTypeVO();
        bhGroupVO.setName((al_vos.size() + 1 + "-部门"));
        bhGroupVO.setRowHeaderGroupFields(new String[] { "部门" });
        bhGroupVO.setColHeaderGroupFields(new String[] {});
        bhGroupVO.setComputeGroupFields(new String[][] { { "2020-05", "sum" }});
        bhGroupVO.setColGroupTiled(true);
        bhGroupVO.setType("CHART");
        al_vos.add(bhGroupVO);

        return (BeforeHandGroupTypeVO[]) al_vos.toArray(new BeforeHandGroupTypeVO[0]);
    }

    public String getDrillTempletCode() throws Exception {
        return "REPORTQUERY_CODE4";
    }

    public HashMap getGroupFieldOrderConfig() {
        HashMap map = new HashMap();
        try {
            map.put("部门", getCommDMO().getStringArrayFirstColByDS(null, "select name from pub_corp_dept"));
            map.put("人员", getCommDMO().getStringArrayFirstColByDS(null, "select name from v_sal_personinfo where isuncheck='N'"));
//            map.put("时间", getCommDMO().getStringArrayFirstColByDS(null, "SELECT TO_CHAR (ADD_MONTHS (TO_DATE ('"+month_start+"', 'yyyy-mm'), ROWNUM - 1),'YYYY-MM') AS yearmonth FROM all_objects WHERE ROWNUM <=(SELECT MONTHS_BETWEEN (TO_DATE ('"+month_end+"', 'yyyy-mm'),TO_DATE ('"+month_start+"', 'yyyy-mm')) FROM DUAL)"));
        } catch (Exception e) {
            WLTLogger.getLogger(PersonReportBuilderAdapter.class).error("", e);
        }
        return map;
    }

    public CommDMO getCommDMO() {
        if (commdmo == null) {
            commdmo = new CommDMO();
        }
        return commdmo;
    }

    public ReportUtil getReportUtil() {
        if (reportutil == null) {
            reportutil = new ReportUtil();
        }
        return reportutil;
    }

    public TBUtil getTBUtil() {
        if (tbutil == null) {
            tbutil = new TBUtil();
        }
        return tbutil;
    }
    // 员工工资汇总
    public HashVO[] getPersonSalary(String[] checkids, String[] types_id) throws Exception {
        if (checkids != null && checkids.length > 0) {
            StringBuffer sb_sql = new StringBuffer();

            String finalres = "";
            String logids = "";
            for (int i = 0; i < checkids.length; i++) {
                if (i == 0) {
                    logids += "'" + checkids[i].replace(",", "','") + "'";
                } else {
                    logids += ",'" + checkids[i].replace(",", "','") + "'";
                }

                for (int j = 0; j < types_id.length; j++) {
                    finalres += ", a" + i + "_" + j + ".sum \""+map.get(checkids[i])+"\"";
                }
            }

            sb_sql.append(" select a.name 人员, b.name 部门, a.stationkind 岗位归类" + finalres);
            sb_sql.append(" from v_sal_personinfo a left join pub_corp_dept b on a.maindeptid=b.id ");
            for (int i = 0; i < checkids.length; i++) {
                for (int j = 0; j < types_id.length; j++) {
                    String str= getCommDMO().getStringValueByDS(null,"select SOURCETYPE from sal_factor_def where 1=1  and  id='"+types_id[j]+"'");
                    if(str.equals("数字")){
                        sb_sql.append(" left join (select userid,sum(factorvalue) sum from sal_salarybill_detail " + "where salarybillid in('" + checkids[i].replace(",", "','") + "') and factorid='" + types_id[j] + "' group by userid)" + " a" + i + "_" + j + " on a" + i + "_" + j + ".userid = a.id ");
                    }else{
                        sb_sql.append(" left join (select userid,factorvalue sum from sal_salarybill_detail " + "where salarybillid in('" + checkids[i].replace(",", "','") + "') and factorid='" + types_id[j] + "' group by userid,factorvalue)" + " a" + i + "_" + j + " on a" + i + "_" + j + ".userid = a.id ");
                    }
                }
            }
            sb_sql.append(" where a.id in(select distinct userid from sal_salarybill_detail where salarybillid in (" + logids + ")) order by a.code ");

            return getCommDMO().getHashVoArrayByDS(null, sb_sql.toString());
        }
        return null;
    }

}
