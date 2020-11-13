package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * zzl
 * 各支行情况存款目标完成情况
 */
public class CKgzhWcQkWKPanel extends AbstractWorkPanel {
    BillListPanel billListPanel=new BillListPanel("V_PUB_USER_POST_zpy");
    @Override
    public void initialize() {
        this.add(billListPanel);
    }
    public BillListPanel getBillListPanel(){
        return billListPanel;
    }
}
