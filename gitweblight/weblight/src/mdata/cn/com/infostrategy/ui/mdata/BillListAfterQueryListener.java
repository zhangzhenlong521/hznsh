package cn.com.infostrategy.ui.mdata;

import java.util.EventListener;

public interface BillListAfterQueryListener extends EventListener {

	public void onBillListAfterQuery(BillListAfterQueryEvent _event); //选中的是哪一行!!

}
