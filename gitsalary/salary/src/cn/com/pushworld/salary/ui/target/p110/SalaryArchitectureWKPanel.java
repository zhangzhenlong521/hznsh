package cn.com.pushworld.salary.ui.target.p110;

import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * zzl 2020-8-31
 * 绩效架构的预览功能wn-fsn
 */
public class SalaryArchitectureWKPanel extends AbstractWorkPanel {
    BillCellPanel billCellPanel=null;
    @Override
    public void initialize() {
        billCellPanel=new BillCellPanel("wn-fsn");
    }
}
