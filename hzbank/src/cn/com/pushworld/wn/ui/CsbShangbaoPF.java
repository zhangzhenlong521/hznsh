package cn.com.pushworld.wn.ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions.IF;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CsbShangbaoPF extends AbstractWorkPanel implements ActionListener,BillListHtmlHrefListener {

	private BillListPanel listPanel = null;
	private JComboBox comboBox = null;//��ѡ��
	private WLTButton updateButton, vertifyButton, vertifyBatchButton,
			backBatchButton;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == updateButton) {// �޸�
			updateData();
		} else if (e.getSource() == vertifyButton) {// ����
			vertifyData();
		} else if (e.getSource() == vertifyBatchButton) {// �������
			vertifyBatchData();
		} else if (e.getSource() == backBatchButton) {// �����˻�
			backBatchData();
		}else if (e.getSource() == listPanel.getQuickQueryPanel()) {
			QuickQuery();
		}
	}

	private void QuickQuery() {// ��ѯ���Ľ��ֻ���ǹ�Ա�Ѿ��ύ ���˻� ������
		try {
			String condition = "1=1 "
					+ listPanel.getQuickQueryPanel().getQuerySQLCondition();
			String state = "δ�ύ";
			condition = condition + " and STATE !='" + state + "'";
			listPanel.QueryDataByCondition(condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateData() {// �޸�����
		try {
			BillVO billvo = listPanel.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(listPanel, "��ѡ��һ�����ݣ�����");
				return;
			}
			String state = billvo.getStringValue("state");// ��ȡ��ѡ�����ݵ�״̬
			if ("�����".equals(state) || "���˻�".equals(state)) {
				BillCardPanel cardpanel = new BillCardPanel(
						"WN_CSBHZ_01_ZPY_Q01");
				cardpanel.setBillVO(billvo);
				BillCardDialog dialog = new BillCardDialog(listPanel, "�༭",
						cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				dialog.setVisible(true);
				cardpanel.setEditable("STATE", false);// �����޸�ʱ�������޸�״̬
				listPanel.refreshCurrSelectedRow();
			} else {
				MessageBox.show(this, "��ǰ����δ����,�޷��޸�");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void vertifyData() {
		try {
			BillVO billvo = listPanel.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(this, "��ѡ��һ�����ݣ�����");
				return;
			}
			String state = billvo.getStringValue("state");// ��ȡ��ѡ�����ݵ�״̬
			if ("���ύ".equals(state.trim())) {
				BillCardPanel cardpanel = new BillCardPanel(
						"WN_CSBHZ_01_ZPY_Q01");
				cardpanel.setBillVO(billvo);
				BillCardDialog dialog = new BillCardDialog(listPanel, "�༭",
						cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				dialog.setVisible(true);
				listPanel.refreshCurrSelectedRow();
			} else {
				MessageBox.show(this, "��ǰ����״̬�ǡ�" + state + "��,�޷�����");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void vertifyBatchData() {
		try {
			BillVO[] billvos = listPanel.getCheckedBillVOs();
			if (billvos == null || billvos.length == 0) {
				MessageBox.show(this, "��ѡ��һ�����ݣ�����");
				return;
			}
			String ids = "";
			String notIds = "";
			String ratifyPerson = ClientEnvironment.getInstance()
					.getLoginUserName();
			String ratifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			for (int i = 0; i < billvos.length; i++) {
				String state = billvos[i].getStringValue("state");
				String id = billvos[i].getStringValue("id");
				if ("�����".equals(state) || "���˻�".equals(state)) {
					if ("".equals(notIds)) {
						notIds = notIds + id;
					} else {
						notIds = notIds + " " + id;
					}
				} else if ("���ύ".equals(state)) {
					if ("".equals(ids)) {
						ids = ids + "'" + id + "'";
					} else {
						ids = ids + ",'" + id + "'";
					}
				}
			}
			if (notIds != null && !"".equals(notIds.trim())) {
				MessageBox.show(this, "ѡ�������д��ڡ�����ˡ������˻ء�����");
				return;
			} else {
				Frame frame = new Frame();
				String inputValue = JOptionPane.showInputDialog("����������:");
				UIUtil.executeUpdateByDS(null,
						"update WN_CSBHZ_01 set ratifyReason='" + inputValue
								+ "',ratifyPerson='" + ratifyPerson
								+ "',ratifyTime='" + ratifyTime
								+ "',state='�����' where id in (" + ids + ")");
				listPanel.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void backBatchData() {
		try {
			BillVO[] billvos = listPanel.getCheckedBillVOs();
			if (billvos == null || billvos.length == 0) {
				MessageBox.show(this, "��ѡ��һ�����ݣ�����");
				return;
			}
			String ids = "";
			String notIds = "";
			String ratifyPerson = ClientEnvironment.getInstance()
					.getLoginUserName();
			String ratifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			for (int i = 0; i < billvos.length; i++) {
				String state = billvos[i].getStringValue("state");
				String id = billvos[i].getStringValue("id");
				if ("�����".equals(state) || "���˻�".equals(state)) {
					if ("".equals(notIds)) {
						notIds = notIds + id;
					} else {
						notIds = notIds + " " + id;
					}
				} else if ("���ύ".equals(state)) {
					if ("".equals(ids)) {
						ids = ids + "'" + id + "'";
					} else {
						ids = ids + ",'" + id + "'";
					}
				}
			}
			if (notIds != null && !"".equals(notIds.trim())) {
				MessageBox.show(this, "ѡ�������д��ڡ�����ˡ������˻ء�����");
				return;
			} else {
				Frame frame = new Frame();
				String inputValue = JOptionPane.showInputDialog("����������:");
				UIUtil.executeUpdateByDS(null,
						"update WN_CSBHZ_01 set ratifyReason='" + inputValue
								+ "',ratifyPerson='" + ratifyPerson
								+ "',ratifyTime='" + ratifyTime
								+ "',state='���˻�' where id in (" + ids + ")");
				listPanel.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_CSBHZ_01_ZPY_Q01");
		updateButton = new WLTButton("�޸�");
		vertifyButton = new WLTButton("����");
		vertifyBatchButton = new WLTButton("�������");
		backBatchButton = new WLTButton("�����˻�");
		updateButton.addActionListener(this);
		vertifyButton.addActionListener(this);
		vertifyBatchButton.addActionListener(this);
		backBatchButton.addActionListener(this);
		listPanel.setRowNumberChecked(true);//��������
		listPanel.addBillListHtmlHrefListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { updateButton,
				vertifyButton, vertifyBatchButton, backBatchButton });
		listPanel.repaintBillListButton();
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(listPanel);
	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}