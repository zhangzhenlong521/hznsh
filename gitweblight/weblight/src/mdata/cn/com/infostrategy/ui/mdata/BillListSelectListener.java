package cn.com.infostrategy.ui.mdata;

import java.util.EventListener;

public interface BillListSelectListener extends EventListener {

	public void onBillListSelectChanged(BillListSelectionEvent _event); //选中的是哪一行!!

}
