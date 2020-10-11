package cn.com.infostrategy.ui.mdata;

import java.util.EventListener;

public interface BillTreeSelectListener extends EventListener {

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event); //选中的是哪一行!!

}
