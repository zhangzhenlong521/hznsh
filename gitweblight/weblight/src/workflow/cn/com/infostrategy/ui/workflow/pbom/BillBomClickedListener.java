package cn.com.infostrategy.ui.workflow.pbom;

import java.util.EventListener;

public interface BillBomClickedListener extends EventListener {

	public void onBillBomClicked(BillBomClickEvent _event); //选中的是哪一行!!

}
