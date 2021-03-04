package cn.com.pushworld.salary.ui.report.p030;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.pushworld.salary.to.SalaryReportVO;
import cn.com.pushworld.salary.ui.report.PersonStyleReportWKPanel;

/**
 * 员工延期支付金统计
 * @author haoming
 * create by 2013-10-30
 */
public class PersonDelayReportPanel extends AbstractWorkPanel {

	private static final long serialVersionUID = -679227613612609293L;

	@Override
	public void initialize() {
		SalaryReportVO srvo_0 = new SalaryReportVO();
		srvo_0.setTitle(new String[] { "序号", "姓名" });
		srvo_0.setField(new String[] { "序号", "username" });
		srvo_0.setField_len(new String[] { "序号", "100", "100" });
		srvo_0.setTypes(new String[] {});
		srvo_0.setTypes_nocou(new String[] { "剩余金额" });
		srvo_0.setTypes_field(new String[] {});
		srvo_0.setTypes_field_nocou(new String[] { "money" });
		srvo_0.setH_count_type(new String[] {});
		srvo_0.setTitlerows(1);
		srvo_0.setReportname("延期支付基金");
		PersonStyleReportWKPanel PersonStyleReportWKPanel_0 = new PersonStyleReportWKPanel(srvo_0);
		PersonStyleReportWKPanel_0.initialize();
		BillQueryPanel querypanel = PersonStyleReportWKPanel_0.getBillQueryPanel();
		if (querypanel != null) {
			querypanel.setVisible(false);
		}
		PersonStyleReportWKPanel_0.query("", ""); //自动查询
		this.add(PersonStyleReportWKPanel_0);
	}

}
