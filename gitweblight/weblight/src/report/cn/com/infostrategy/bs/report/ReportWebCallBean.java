package cn.com.infostrategy.bs.report;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.WebCallBeanIfc;

/**
 * ר�����ڱ������WebCall��չʾ!!!
 * @author xch
 *
 */
public class ReportWebCallBean implements WebCallBeanIfc {

	public String getHtmlContent(HashMap _pars) throws Exception {
		String str_type = (String) _pars.get("calltype"); //
		if (str_type == null) {
			return "ReportWebCallBean��getHtmlContent()�����õ��˿յķ�������";
		}

		if (str_type.equals("getBillCardHtml")) {
			String str_templetcode = (String) _pars.get("templetCode"); //
			String str_sql = (String) _pars.get("sql"); //
			return new BillPanelExportHtmlDMO().getBillCardHtml(str_templetcode, str_sql);
		} else if (str_type.equals("getBillListHtml")) {
			String str_templetcode = (String) _pars.get("templetCode"); //
			String str_sql = (String) _pars.get("sql"); //
			return new BillPanelExportHtmlDMO().getBillListHtml(str_templetcode, str_sql);
		} else if (str_type.equals("getBillTreeHtml")) {
			String str_templetcode = (String) _pars.get("templetCode"); //
			String str_sql = (String) _pars.get("sql"); //
			return new BillPanelExportHtmlDMO().getBillListHtml(str_templetcode, str_sql);
		} else if (str_type.equals("getMultiHtml")) {
			String[][] str_sqls = (String[][]) _pars.get("multisqls"); //
			return new BillPanelExportHtmlDMO().getMultiHtml(str_sqls);
		} else {
			return "ReportWebCallBean��getHtmlContent()�����õ���δ֪�ķ�������" + str_type;
		}
	}

}
