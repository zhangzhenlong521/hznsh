package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class GyClassEvaluate extends AbstractWorkPanel implements
		ActionListener {

	private UIUtil uiutil = new UIUtil();
	private BillListPanel billListPanel;
	private WLTButton btn_evaluation;
	private String date = uiutil.getCurrDate();

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_evaluation) {
			try {
				WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				String str = service.getGyClass(date);
				MessageBox.show(this, str);
			} catch (Exception e2) {
				e2.printStackTrace();
				MessageBox.show(this, "评级失败！请联系系统管理员！");
			}
		}

	}

	@Override
	public void initialize() {
		billListPanel = new BillListPanel("V_WN_GYPJ_fj_CODE1");
		btn_evaluation = new WLTButton("评级考核");
		btn_evaluation.addActionListener(this);
		billListPanel
				.addBatchBillListButton(new WLTButton[] { btn_evaluation });
		billListPanel.repaintBillListButton();
		billListPanel.setVisible(true);
		this.add(billListPanel);
	}

}
