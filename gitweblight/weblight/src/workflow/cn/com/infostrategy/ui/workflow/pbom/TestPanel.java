package cn.com.infostrategy.ui.workflow.pbom;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;

public class TestPanel extends AbstractWorkPanel implements BomItemClickListener {

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		BillBomPanel bomPanel = new BillBomPanel("001");
		//bomPanel.getBillbomPanel("007").addBillBomCLickListener(this); //
		this.add(bomPanel); //
	}

	public void onBomItemClicked(BomItemClickEvent _event) {
		MessageBox.show(this, "WWWWWW"); //

	}

}
