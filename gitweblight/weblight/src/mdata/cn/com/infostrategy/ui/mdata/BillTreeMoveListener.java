package cn.com.infostrategy.ui.mdata;

import java.util.EventListener;

public interface BillTreeMoveListener extends EventListener {

	public void onBillTreeNodeMoved(BillTreeMoveEvent _event); //移动了某个结点

}
