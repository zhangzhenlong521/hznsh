package cn.com.infostrategy.ui.workflow.pbom;

import javax.swing.JPanel;

/**
 * Bom�����ʱ���򿪵ĳ������
 * @author xch
 *
 */
public class DefaultBillBomHrefPanel extends JPanel {

	protected BillBomPanel billBomPanel = null;

	public BillBomPanel getBillBomPanel() {
		return billBomPanel;
	}

	public void setBillBomPanel(BillBomPanel _billBomPanel) {
		this.billBomPanel = _billBomPanel;
	}

}
