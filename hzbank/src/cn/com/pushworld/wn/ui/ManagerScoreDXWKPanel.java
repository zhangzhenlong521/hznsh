package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.bs.WnSalaryServiceImpl;

/**
 * 客户经理等级评定
 * 
 * @author ZPY
 */
public class ManagerScoreDXWKPanel extends AbstractWorkPanel implements
		ActionListener {

	private BillListPanel listPanel = null;
	private WLTButton rateButton;
	private String message;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_MANAGERDXSCORE_ZPY_Q01");
		rateButton = new WLTButton("等级评定");
		rateButton.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { rateButton });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == rateButton) {// 客户经理等级评定
			try {
				// 允许客户去选择是对上半年进行考核还是对下半年进行考核 0 表示上半年，1表示下半年 -1表示取消
				final int dateNum = MessageBox.showOptionDialog(this,
						"请选择考核的时间段", "提示", new String[] { "上半年", "下半年" }, 0);
				MessageBox.show(this,"当前选中的值为:"+dateNum);
				final WnSalaryServiceIfc service = new WnSalaryServiceImpl();
				if (dateNum == -1) {
					return;
				}
				new SplashWindow(this, new AbstractAction() {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {// 对客户经理进行评级考核功能开发
						message = service.managerLevelCompute(dateNum);
					}
				});
				MessageBox.show(this, message);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}