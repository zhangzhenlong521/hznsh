package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ImportDataWKPanel extends AbstractWorkPanel implements
		ActionListener {
	private BillListPanel listPanel;
	private WLTButton importAll, importOne, importDay;
	private String str;
	// 获取到登录人的信息
	private String loginUserCode = ClientEnvironment.getInstance()
			.getLoginUserCode();

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_IMPORTLOG_ZPY_Q01");
		importAll = new WLTButton("全量导入");
		importAll.addActionListener(this);
		importOne = new WLTButton("单表导入");
		importOne.addActionListener(this);
		importDay = new WLTButton("全天导入");
		importDay.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { importAll,
				importOne, importDay });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == importAll) {// 全量数据导入
			ImportAll();
		} else if (e.getSource() == importDay) {// 全天数据导入
			ImportDay();
		} else if (e.getSource() == importOne) {// 单表数据导入
			ImportOne();
		}
	}

	private void ImportOne() {// 导入单表数据
		try {
			final String filePath = JOptionPane.showInputDialog("请输入导入文件路径:");
			if (filePath == null || filePath.length() <= 0) {
				MessageBox.show(this, "数据文件路径为空,请输入正确的数据文件路径");
				return;
			}
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					str = service.ImportOne(filePath);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		MessageBox.show(str);
	}

	private void ImportDay() {// 导入一天的数据
		// 导入一天的数据
		try {
			// 弹出输入框
			final String date = JOptionPane.showInputDialog("请输入日期:");
			if (date == null || date.length() <= 0) {
				MessageBox.show(this, "数据文件路径为空,请输入正确的数据文件路径");
				return;
			}
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					str = service.ImportDay(date);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		MessageBox.show(str);
	}

	private void ImportAll() {// 全量数据导入
		try {

			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					str = service.ImportAll();
				}
			});
			MessageBox.show(this, str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}