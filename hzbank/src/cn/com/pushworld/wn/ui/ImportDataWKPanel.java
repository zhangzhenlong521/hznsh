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
	// ��ȡ����¼�˵���Ϣ
	private String loginUserCode = ClientEnvironment.getInstance()
			.getLoginUserCode();

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_IMPORTLOG_ZPY_Q01");
		importAll = new WLTButton("ȫ������");
		importAll.addActionListener(this);
		importOne = new WLTButton("������");
		importOne.addActionListener(this);
		importDay = new WLTButton("ȫ�쵼��");
		importDay.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { importAll,
				importOne, importDay });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == importAll) {// ȫ�����ݵ���
			ImportAll();
		} else if (e.getSource() == importDay) {// ȫ�����ݵ���
			ImportDay();
		} else if (e.getSource() == importOne) {// �������ݵ���
			ImportOne();
		}
	}

	private void ImportOne() {// ���뵥������
		try {
			final String filePath = JOptionPane.showInputDialog("�����뵼���ļ�·��:");
			if (filePath == null || filePath.length() <= 0) {
				MessageBox.show(this, "�����ļ�·��Ϊ��,��������ȷ�������ļ�·��");
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

	private void ImportDay() {// ����һ�������
		// ����һ�������
		try {
			// ���������
			final String date = JOptionPane.showInputDialog("����������:");
			if (date == null || date.length() <= 0) {
				MessageBox.show(this, "�����ļ�·��Ϊ��,��������ȷ�������ļ�·��");
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

	private void ImportAll() {// ȫ�����ݵ���
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