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

/**
 * 
 * @author zzl
 * 
 *         2019-3-28-����05:01:24 �������Ŀ����
 */
public class JDFKMhuizongSp extends AbstractWorkPanel implements ActionListener,BillListHtmlHrefListener{

	private BillListPanel list = null;
	private JComboBox comboBox = null;//��ѡ��
	private WLTButton updateButton, verifyButton, vertiybatchButton,
			backbatchButton;

	@Override
	public void initialize() {
		list = new BillListPanel("WN_JDFKMHZB_02_Q02_ZPY");
		updateButton = new WLTButton("�޸�");
		updateButton.addActionListener(this);
		verifyButton = new WLTButton("����");
		verifyButton.addActionListener(this);
		vertiybatchButton = new WLTButton("�������");
		vertiybatchButton.addActionListener(this);
		backbatchButton = new WLTButton("�����˻�");
		backbatchButton.addActionListener(this);
		list.addBatchBillListButton(new WLTButton[] { updateButton,
				verifyButton, vertiybatchButton, backbatchButton });
		list.getQuickQueryPanel().addBillQuickActionListener(this);// ��ȡ�����ٲ�ѯ�¼�
		list.setRowNumberChecked(true);//��������
		list.addBillListHtmlHrefListener(this);
		list.repaintBillListButton();
		this.add(list);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == updateButton) {// �޸ģ�ֻ�����޸���������
			updateReason();
		} else if (e.getSource() == verifyButton) {// ������ť
			verifyData();
		} else if (list.getQuickQueryPanel() == e.getSource()) {
			QuickQuery();
		} else if (e.getSource() == vertiybatchButton) {
			vertiybatchData();
		} else if (e.getSource() == backbatchButton) {
			backbatchData();
		}
	}

	private void vertiybatchData() {
		try {
			BillVO[] billvos = list.getCheckedBillVOs();
			if (billvos == null || billvos.length == 0) {
				MessageBox.show(list, "��ѡ��һ�����ݣ�����");
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
						"update WN_JDFKMHZB_02 set ratifyReason='" + inputValue
								+ "',ratifyPerson='" + ratifyPerson
								+ "',ratifyTime='" + ratifyTime
								+ "',state='�����' where id in (" + ids + ")");
				list.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void backbatchData() {// �����˻�
		try {
			BillVO[] billvos = list.getCheckedBillVOs();
			if (billvos == null || billvos.length == 0) {
				MessageBox.show(list, "��ѡ��һ�����ݣ�����");
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
						"update WN_JDFKMHZB_02 set ratifyReason='" + inputValue
								+ "',ratifyPerson='" + ratifyPerson
								+ "',ratifyTime='" + ratifyTime
								+ "',state='���˻�' where id in (" + ids + ")");
				list.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void QuickQuery() {// ��ѯ��������ֻ���ǹ�Ա�Ѿ��ύ�����˻ػ�������˵�;
		try {
			// ��ȡ����ǰ��¼�˵Ļ���
			String dept = ClientEnvironment.getInstance().getLoginUserDeptId();
			String code = UIUtil.getStringValueByDS(null,
					"SELECT code FROM pub_corp_dept WHERE ID='" + dept + "'");
			String condition = "1=1 "
					+ list.getQuickQueryPanel().getQuerySQLCondition();
			String state = "δ�ύ";
			condition = condition + " and STATE !='" + state + "' and JG_ID='"
					+ code + "'";
			list.QueryDataByCondition(condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void backData() {
	// BillVO billvo = list.getSelectedBillVO();//��ȡ����ǰѡ������
	// if (billvo == null) {
	// MessageBox.show(this, "��ѡ��һ�����ݣ�����");
	// return;
	// }
	// String state = billvo.getStringValue("state");//��ȡ��ѡ�����ݵ�״̬
	// if (state.equals("���ύ")) {//�����ύ��״̬�¿����˻�����
	// BillCardPanel cardpanel = new BillCardPanel("WN_JDFKMHZB_02_Q02_ZPY");
	// cardpanel.setBillVO(billvo);
	// BillCardDialog dialog = new BillCardDialog(list, "�༭", cardpanel,
	// WLTConstants.BILLDATAEDITSTATE_UPDATE);
	// dialog.setVisible(true);
	// list.refreshCurrSelectedRow();
	// } else {
	// MessageBox.show(this, "��ǰ����״̬�ǡ�" + state + "��,�޷��˻�");
	// }
	//
	// }

	private void verifyData() {// ��������
		BillVO billvo = list.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.show(list, "��ѡ��һ�����ݣ�����");
			return;
		}
		String state = billvo.getStringValue("state");// ��ȡ��ѡ�����ݵ�״̬
		if ("���ύ".equals(state.trim())) {
			BillCardPanel cardpanel = new BillCardPanel(
					"WN_JDFKMHZB_02_Q02_ZPY");
			cardpanel.setBillVO(billvo);
			BillCardDialog dialog = new BillCardDialog(list, "�༭", cardpanel,
					WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dialog.setVisible(true);
			list.refreshCurrSelectedRow();
		} else {
			MessageBox.show(this, "��ǰ����״̬�ǡ�" + state + "��,�޷�����");
		}
	}

	private void updateReason() {
		BillVO billvo = list.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.show(list, "��ѡ��һ�����ݣ�����");
			return;
		}
		String state = billvo.getStringValue("state");// ��ȡ��ѡ�����ݵ�״̬
		if ("�����".equals(state) || "���˻�".equals(state)) {
			BillCardPanel cardpanel = new BillCardPanel(
					"WN_JDFKMHZB_02_Q02_ZPY");
			cardpanel.setBillVO(billvo);
			BillCardDialog dialog = new BillCardDialog(list, "�༭", cardpanel,
					WLTConstants.BILLDATAEDITSTATE_UPDATE);
			cardpanel.setEditable("STATE", false);
			dialog.setVisible(true);
			list.refreshCurrSelectedRow();
		} else {
			MessageBox.show(list, "��ǰ����δ����,�޷��޸�");
		}

	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent arg0) {
		// TODO Auto-generated method stub
		
	}



}
