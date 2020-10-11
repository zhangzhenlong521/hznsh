package cn.com.infostrategy.ui.workflow.gunter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class GunterWKPanel extends AbstractWorkPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7165231787513369148L;
	WLTButton btn_run = new WLTButton("时标网络图");
	BillListPanel listpanel = new BillListPanel("PUB_GUNTER_CODE1");

	@Override
	public void initialize() {
		WLTButton btn_add = WLTButton.createButtonByType(WLTButton.LIST_ROWINSERT);
		WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETEROW);
		WLTButton btn_save = WLTButton.createButtonByType(WLTButton.LIST_SAVE);
		WLTButton btn_up = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP);
		WLTButton btn_down = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN);
		btn_run.addActionListener(this);
		listpanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_delete, btn_save, btn_up, btn_down, btn_run });
		listpanel.repaintBillListButton();
		this.add(listpanel);
	}

	public void actionPerformed(ActionEvent arg0) {
		BillVO vos[] = listpanel.getAllBillVOs();
		if (vos.length == 0) {
			MessageBox.show(this, "请先维护好时标流程");
			return;
		}
		HashVO hvos[] = new HashVO[vos.length];
		for (int i = 0; i < hvos.length; i++) {
			hvos[i] = vos[i].convertToHashVO();
		}
		JPanel jp = new GunterPanel(hvos);
		BillDialog dialog = new BillDialog(this);
		dialog.getContentPane().add(jp);
		dialog.setSize(1024, 700);
		dialog.locationToCenterPosition();
		dialog.setVisible(true);
	}
}
