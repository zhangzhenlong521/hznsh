package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ί�ɻ����������
 * @author 85378
 *
 */
public class WpkjClassEvaluate extends AbstractWorkPanel implements
		ActionListener {

	private UIUtil uiutil = new UIUtil();
	private String date = uiutil.getCurrDate();
	private BillListPanel billListPanel;
	private WLTButton btn_evaluate;//�������˰�ť

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
				MessageBox.show("����ʧ�ܣ�����ϵ����Ա");
			}
		}

	}

	@Override
	public void initialize() {
		billListPanel = new BillListPanel("V_WN_WPKJ_PJ_CODE1");
		btn_evaluate = new WLTButton("��������");
		btn_evaluate.addActionListener(this);
		billListPanel.addBatchBillListButton(new WLTButton[] { btn_evaluate });
		billListPanel.repaintBillListButton();
		billListPanel.setVisible(true);
		this.add(billListPanel);

	}

}
