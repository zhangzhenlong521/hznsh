package cn.com.pushworld.salary.ui.report;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.pushworld.salary.to.SalaryReportVO;

/**
 * 此页面是: 查询统计--基金统计
 * @author yangke,haoming
 * create by 2013-10-30
 * 
 */
public class PersonOtherrReportWKPanel extends AbstractWorkPanel {
	private WLTTabbedPane tabbedPane = null;

	public void initialize() {
		this.setLayout(new BorderLayout());
		tabbedPane = new WLTTabbedPane();
		tabbedPane.setFocusable(false);

		/*		SalaryReportVO srvo_0 = new SalaryReportVO();
				srvo_0.setTitle(new String[] { "序号", "姓名", "主部门", "岗位归类" });
				srvo_0.setField(new String[] { "序号", "username", "corpname", "stationkind" });
				srvo_0.setField_len(new String[] { "序号", "80", "150", "100" });
				srvo_0.setTypes(new String[] { "发生金额" });
				srvo_0.setTypes_nocou(new String[] { });
				srvo_0.setTypes_field(new String[] { "C" });
				srvo_0.setTypes_field_nocou(new String[] { "D" });
				srvo_0.setV_count_type(new String[] { "合计" });
				srvo_0.setH_count_type(new String[] { "合计" });
				srvo_0.setTitlerows(2);
				srvo_0.setExcel_code("B");
				srvo_0.setReportname("延期支付");

				PersonStyleeReportWKPanel PersonStyleeReportWKPanel_0 = new PersonStyleeReportWKPanel(srvo_0);
				PersonStyleeReportWKPanel_0.initialize();
				tabbedPane.addTab("延期支付", PersonStyleeReportWKPanel_0);*/

		/*		SalaryReportVO srvo_0 = new SalaryReportVO();
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
				tabbedPane.addTab("延期支付基金", PersonStyleReportWKPanel_0);*/

		PersonYqzfReportWKPanel PersonYqzfReportWKPanel_0 = new PersonYqzfReportWKPanel("延期支付基金");
		PersonYqzfReportWKPanel_0.initialize();
		tabbedPane.addTab("延期支付金", PersonYqzfReportWKPanel_0);

		SalaryReportVO srvo_1 = new SalaryReportVO();
		srvo_1.setTitle(new String[] { "序号", "姓名", "主部门", "岗位归类" });
		srvo_1.setField(new String[] { "序号", "username", "corpname", "stationkind" });
		srvo_1.setField_len(new String[] { "序号", "80", "150", "100" });
		srvo_1.setTypes(new String[] { "发生金额" });
		srvo_1.setTypes_nocou(new String[] {});
		srvo_1.setTypes_field(new String[] { "B" });
		srvo_1.setTypes_field_nocou(new String[] {});
		srvo_1.setV_count_type(new String[] {});
		srvo_1.setH_count_type(new String[] { "合计" });
		srvo_1.setTitlerows(2);
		srvo_1.setExcel_code("A");
		srvo_1.setReportname("免责基金");

		PersonStyleeReportWKPanel PersonStyleeReportWKPanel_1 = new PersonStyleeReportWKPanel(srvo_1);
		PersonStyleeReportWKPanel_1.initialize();
		tabbedPane.addTab("虚拟免责金", PersonStyleeReportWKPanel_1);

		PersonGqjlReportWKPanel PersonGqjlReportWKPanel_2 = new PersonGqjlReportWKPanel("股权激励基金");
		PersonGqjlReportWKPanel_2.addMenuConfMap(getMenuConfMap());
		PersonGqjlReportWKPanel_2.initialize();
		tabbedPane.addTab("股权激励", PersonGqjlReportWKPanel_2);

		this.add(tabbedPane, BorderLayout.CENTER);

		//PersonStyleReportWKPanel_0.query("", ""); //自动查询
	}

}
