package com.pushworld.ipushgrc.ui.risk.p130;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;

/**
 * ����Ǩ��ͼ
 * @author lcj
 *
 */
public class RiskChangeStatisWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		String str_sql = "select (select count(id) from v_cmp_risk_editlog where edittype='�������յ�' and filestate=3) �������յ�,(select count(id) from v_cmp_risk_editlog where edittype='ɾ�����յ�' and filestate=3) ɾ�����յ�,(select count(id) from v_cmp_risk_editlog  where rankcode >rankcode2 and edittype='�޸ķ��յ�' and filestate=3) ���յȼ����,(select count(id) from v_cmp_risk_editlog  where rankcode <rankcode2 and edittype='�޸ķ��յ�' and filestate=3) ���յȼ����,(select count(id) from v_cmp_risk_editlog  where ctrlcode>ctrlcode2 and edittype='�޸ķ��յ�' and filestate=3) ������Ч�Ա��,(select count(id) from v_cmp_risk_editlog  where ctrlcode<ctrlcode2 and edittype='�޸ķ��յ�' and filestate=3) ������Ч�Ա��,(select count(id) from v_cmp_risk_editlog  where rankcode=rankcode2 and (ctrlcode=ctrlcode2 or (ctrlcode is null and ctrlcode is null))and edittype='�޸ķ��յ�' and filestate=3) �����޸� from wltdual";
		try {
			String[][] counts = UIUtil.getStringArrayByDS(null, str_sql);
			double[] count = new double[] { Double.parseDouble(counts[0][0]), Double.parseDouble(counts[0][1]), Double.parseDouble(counts[0][2]), Double.parseDouble(counts[0][3]), Double.parseDouble(counts[0][4]), Double.parseDouble(counts[0][5]), Double.parseDouble(counts[0][6]) };
			String[] colname = new String[] { "�������յ�", "ɾ�����յ�", "���յȼ����", "���յȼ����", "������Ч�Ա��", "������Ч�Ա��", "�����޸�" };
			BillChartPanel chartpanel = new BillChartPanel("����Ǩ��ͼ", colname, count);
			this.add(chartpanel);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
