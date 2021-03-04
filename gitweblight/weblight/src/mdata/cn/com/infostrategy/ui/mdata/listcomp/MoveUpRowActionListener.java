package cn.com.infostrategy.ui.mdata.listcomp;

import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * ����һ��
 * @author xch
 *
 */
public class MoveUpRowActionListener implements WLTActionListener {

	public void actionPerformed(WLTActionEvent e) throws Exception {
		BillListPanel billListPanel = (BillListPanel) e.getBillPanelFrom(); //
		billListPanel.moveUpRow();  //
	}

}
