package com.pushworld.ipushgrc.ui.risk.p130;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;

/**
 * 风险迁徙图
 * @author lcj
 *
 */
public class RiskChangeStatisWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		String str_sql = "select (select count(id) from v_cmp_risk_editlog where edittype='新增风险点' and filestate=3) 新增风险点,(select count(id) from v_cmp_risk_editlog where edittype='删除风险点' and filestate=3) 删除风险点,(select count(id) from v_cmp_risk_editlog  where rankcode >rankcode2 and edittype='修改风险点' and filestate=3) 风险等级变高,(select count(id) from v_cmp_risk_editlog  where rankcode <rankcode2 and edittype='修改风险点' and filestate=3) 风险等级变低,(select count(id) from v_cmp_risk_editlog  where ctrlcode>ctrlcode2 and edittype='修改风险点' and filestate=3) 控制有效性变高,(select count(id) from v_cmp_risk_editlog  where ctrlcode<ctrlcode2 and edittype='修改风险点' and filestate=3) 控制有效性变低,(select count(id) from v_cmp_risk_editlog  where rankcode=rankcode2 and (ctrlcode=ctrlcode2 or (ctrlcode is null and ctrlcode is null))and edittype='修改风险点' and filestate=3) 其他修改 from wltdual";
		try {
			String[][] counts = UIUtil.getStringArrayByDS(null, str_sql);
			double[] count = new double[] { Double.parseDouble(counts[0][0]), Double.parseDouble(counts[0][1]), Double.parseDouble(counts[0][2]), Double.parseDouble(counts[0][3]), Double.parseDouble(counts[0][4]), Double.parseDouble(counts[0][5]), Double.parseDouble(counts[0][6]) };
			String[] colname = new String[] { "新增风险点", "删除风险点", "风险等级变高", "风险等级变低", "控制有效性变高", "控制有效性变低", "其他修改" };
			BillChartPanel chartpanel = new BillChartPanel("风险迁徙图", colname, count);
			this.add(chartpanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
