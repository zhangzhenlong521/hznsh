package cn.com.infostrategy.ui.sysapp.other;

import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class WLTButton_refresh implements WLTActionListener {

    public void actionPerformed(WLTActionEvent _event) throws Exception {
        BillListPanel list = (BillListPanel)_event.getBillPanelFrom();
        
        list.QueryData(list.getStr_realsql());

    }

}
