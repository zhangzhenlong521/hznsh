package cn.com.pushworld.salary.ui.tools;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * ���ֱ����
 * 
 * @author Administrator
 */
public class CheckTableUpdateWKPanel extends AbstractWorkPanel implements ActionListener {

	private WLTButton checkTableUpdate, checkTableUpdate2, postdutyUpdate;
	private JTextField txtDate;

	public void initialize() {
		JPanel panelMain = new JPanel(new FlowLayout());
		WLTLabel labInfo = new WLTLabel("�������ڣ�");
		panelMain.add(labInfo);

		txtDate = new JTextField();
		txtDate.setText(new SalaryUIUtil().getCheckDate());
		txtDate.setPreferredSize(new Dimension(100, 22));
		panelMain.add(txtDate);

		checkTableUpdate = new WLTButton("���ֱ����(ȫ��)");
		checkTableUpdate.addActionListener(this);
		checkTableUpdate2 = new WLTButton("���ֱ����(����)");
		checkTableUpdate2.addActionListener(this);
		postdutyUpdate = new WLTButton("��������ָ��");
		postdutyUpdate.addActionListener(this);

		panelMain.add(checkTableUpdate);
		panelMain.add(checkTableUpdate2);
		panelMain.add(postdutyUpdate);
		Border border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "���ֱ����", TitledBorder.LEFT, TitledBorder.TOP, LookAndFeel.font); // �����߿�
		panelMain.setBorder(border);
		this.add(panelMain);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == checkTableUpdate) {
			new SplashWindow(checkTableUpdate, "����������,���Ժ�...", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					createScoreTable(txtDate.getText(), false);
				}
			}, false);
		} else if (e.getSource() == checkTableUpdate2) {
			new SplashWindow(checkTableUpdate, "����������,���Ժ�...", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					createScoreTable(txtDate.getText(), true);
				}
			}, false);
		} else if (e.getSource() == postdutyUpdate) {
			new SplashWindow(checkTableUpdate, "����������,���Ժ�...", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					updatePostDutyScoreTable(txtDate.getText());
				}
			}, false);
		}

	}

	private void createScoreTable(String month, boolean param) {
		try {
			SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			HashMap map = ifc.checkViladate(month);
			String res = map.get("res") + "";
			if ("fail".equals(res)) {
				MessageBox.show(this, map.get("msg") + "");
				return;
			} else if ("error".equals(res)) {
				MessageBox.show(this, "�����쳣�������Ա��ϵ!");
				return;
			} else if ("success".equals(res)) {
				if (map.containsKey("msginfo")) {
					if (MessageBox.showConfirmDialog(this, map.get("msginfo") + "�Ƿ����?", "����", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
						return;
					}
				}
				map.put("loginuserid", ClientEnvironment.getCurrLoginUserVO().getId());
				map.put("logindeptid", ClientEnvironment.getCurrLoginUserVO().getId());
				map.put("isupdate", "Y");
				if (param) {
					map.put("isdlonly", "Y");
				}
				ifc.createScoreTable(map);
				MessageBox.show(this, "���˱�������!");
			} else {
				MessageBox.show(this, "�����쳣�������Ա��ϵ!");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	private void updatePostDutyScoreTable(String month) {
		try {
			HashMap map = new HashMap();
			if (MessageBox.showConfirmDialog(this, "ȷ��Ҫ������?", "����", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
				return;
			}
			String plv = UIUtil.getStringValueByDS(null, "select code from PUB_COMBOBOXDICT where type ='н��_�¶ȿ���Ƶ��' and id='" + month.substring(5) + "'");
			map.put("loginuserid", ClientEnvironment.getCurrLoginUserVO().getId());
			map.put("logindeptid", ClientEnvironment.getCurrLoginUserVO().getId());
			map.put("isupdate", "Y");
			map.put("plv", plv);
			map.put("month", month);
			String logid = UIUtil.getStringValueByDS(null, "select id from sal_target_check_log where checkdate='" + month + "'");
			if (TBUtil.isEmpty(logid)) {
				MessageBox.show(this, "�����ڲ����ڿ��˼ƻ�.");
				return;
			}
			map.put("logid", logid);
			SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			ifc.createPostDutyScoreTable(map);
			MessageBox.show(this, "���˱�������!");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}
}
