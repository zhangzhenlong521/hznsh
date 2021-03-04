package cn.com.pushworld.salary.ui.report;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;

public class PersonReportWKPanel extends AbstractWorkPanel {

    private static final long serialVersionUID = -1303612160615140713L;

    public void initialize() {
        BillReportPanel reportPanel = new BillReportPanel("REPORTQUERY_CODE2",
        		"cn.com.pushworld.salary.bs.report.PersonReportBuilderAdapter");
        this.add(reportPanel); 
    }

}

