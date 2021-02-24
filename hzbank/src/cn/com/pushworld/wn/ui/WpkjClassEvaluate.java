package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 委派会计评级考核
 * @author 85378
 *
 */
public class WpkjClassEvaluate extends AbstractWorkPanel implements
		ActionListener {

	private UIUtil uiutil = new UIUtil();
	private String date = uiutil.getCurrDate();
	private BillListPanel billListPanel;
	private WLTButton btn_evaluate;//评级考核按钮

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_evaluate) {
			try {
				WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				String str = service.getWpkjClass(date);
				MessageBox.show(this, str);
			} catch (Exception e2) {
				e2.printStackTrace();
				MessageBox.show("评级失败，请联系管理员");
			}
		}

	}

	@Override
	public void initialize() {
		billListPanel = new BillListPanel("V_WN_WPKJ_PJ_CODE1");
		btn_evaluate = new WLTButton("评级考核");
		btn_evaluate.addActionListener(this);
		billListPanel.addBatchBillListButton(new WLTButton[] { btn_evaluate });
		billListPanel.repaintBillListButton();
		billListPanel.setVisible(true);
		this.add(billListPanel);

	}

}
