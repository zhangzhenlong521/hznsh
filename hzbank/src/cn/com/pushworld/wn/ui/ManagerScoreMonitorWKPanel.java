package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;

import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ManagerScoreMonitorWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener{
    
	private BillListPanel listPanel;
	@Override
	public void initialize() {
		listPanel=new BillListPanel("V_MANAGERSCORE_LOOK_ZPY_Q01");
		listPanel.addBillListHtmlHrefListener(this);
		this.add(listPanel);
	}
	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent e) {
		if(e.getSource()==listPanel){
			BillVO vo = listPanel.getSelectedBillVO();
			BillListDialog dialog = new BillListDialog(this, "²é¿´",
					"WN_MANAGERDX_TABLE_Q01_ZPY");
			dialog.getBilllistPanel().QueryDataByCondition(
					"USERCODE='" + vo.getStringValue("USERCODE") + "'");
			dialog.getBtn_confirm().setVisible(false);
			dialog.setVisible(true);
		}
	}
}