package cn.com.infostrategy.ui.mdata.listcomp;

import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * ÏÂÒÆÒ»ÐÐ
 * @author xch
 *
 */
public class MoveDownRowActionListener implements WLTActionListener {

	public void actionPerformed(WLTActionEvent e) throws Exception {
		BillListPanel billListPanel = (BillListPanel) e.getBillPanelFrom(); //
		billListPanel.moveDownRow(); //
	}

}
