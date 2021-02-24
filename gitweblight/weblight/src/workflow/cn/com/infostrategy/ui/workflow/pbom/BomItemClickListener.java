package cn.com.infostrategy.ui.workflow.pbom;

import java.util.EventListener;

public interface BomItemClickListener extends EventListener {

	public void onBomItemClicked(BomItemClickEvent _event); //选中的是哪一行!!

}
