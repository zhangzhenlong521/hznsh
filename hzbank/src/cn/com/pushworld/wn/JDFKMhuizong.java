package cn.com.pushworld.wn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class JDFKMhuizong extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel = null;
	private UIUtil uiUtil = new UIUtil();
	private WLTButton updateButton, removeButton, submitButton;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_JDFKMHZB_02_ZPY");
		submitButton = new WLTButton("�ύ");
		submitButton.addActionListener(this);
		updateButton = new WLTButton("�޸�");
		updateButton.addActionListener(this);
		removeButton = new WLTButton("ɾ��");
		removeButton.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { updateButton,
				removeButton, submitButton });
//		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);// ��ȡ�����ٲ�ѯ�¼�
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitButton) {//
			submitData();
		} else if (e.getSource() == removeButton) {
			removeData();
		} else if (e.getSource() == updateButton) {
			updateData();
//		} else if (listPanel.getQuickQueryPanel() == e.getSource()) {
//			QuickQuery();
		}
	}

	// ��д���ٲ�ѯ
	private void QuickQuery() {
		
		 String condition="1=1 "+listPanel.getQuickQueryPanel().getQuerySQLCondition(); 
		 String GY_Name=ClientEnvironment.getInstance().getLoginUserCode();
		 //��ȡ����ǰ�û���code
		 System.out.println("code:"+GY_Name);
		 condition=condition+" and GY_ID='"+GY_Name+"'";
		  System.out.println(condition);
		  listPanel.QueryDataByCondition(condition);
		 
	}

	// �޸�����
	private void updateData() {
		try {
			BillVO[] selected = listPanel.getSelectedBillVOs();
			if (selected.length != 1) {
				MessageBox.show(listPanel, "��ѡ��һ�����ݽ����޸�");
				return;
			}
			BillVO billvo = selected[0];// ��ȡ���û�ѡ�е�����
			String state = billvo.getStringValue("state");
			if (state.equals("δ�ύ") || state.equals("���˻�")) {
				BillCardPanel cardpanel = new BillCardPanel(
						"WN_JDFKMHZB_02_ZPY");
				cardpanel.setBillVO(billvo);
				BillCardDialog dialog = new BillCardDialog(listPanel, "�༭",
						cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				dialog.setVisible(true);
				listPanel.refreshCurrSelectedRow();
			} else {
				MessageBox.show("��ǰ����״̬Ϊ��" + state + "���������޸�");
			}
			listPanel.refreshCurrData();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// ɾ������
	private void removeData() {
		try {
			BillVO[] selected = listPanel.getSelectedBillVOs();
			if (selected.length <= 0) {
				MessageBox.show(listPanel, "��ѡ��һ���������¼��ִ�д˲���;");
				return;
			}
			String notId = "";
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < selected.length; i++) {
				String state = selected[i].getStringValue("state");
				String id = selected[i].getStringValue("id");
				if (!state.equals("δ�ύ")) {
					if (notId.isEmpty()) {
						notId = notId + id;
					} else {
						notId = notId + " " + id;
					}
				} else {
					String sql = "delete from WN_JDFKMHZB_02 where id='" + id
							+ "'";
					list.add(sql);
				}
			}
			if (list.size() > 0) {
				int check = MessageBox.showOptionDialog(this, "ȷ��ɾ��������", "��ʾ",
						new String[] { "ȷ��", "ȡ��" }, 1);
				if (check == 0) {
					uiUtil.executeBatchByDS(null, list);
				} else {
					return;
				}
			}
			if (!"".equals(notId.trim())) {
				MessageBox.show("idΪ" + notId + "������״̬�Ѹı䣬����ɾ��������");
			}
			listPanel.refreshCurrData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// �ύ�¼�
	private void submitData() {
		try {
			// ��ȡѡ������
			BillVO[] selected = listPanel.getSelectedBillVOs();
			if (selected.length <= 0) {
				MessageBox.show(listPanel, "��ѡ��һ���������¼��ִ�д˲���;");
				return;
			}
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < selected.length; i++) {
				String selectedId = selected[i].getStringValue("ID");
				String state = selected[i].getStringValue("state");
				String JFFSE = selected[i].getStringValue("JFFSE");
				String JFTJZJ = selected[i].getStringValue("JFTJZJ");
				String DFFSE = selected[i].getStringValue("DFFSE");
				String DFTJZJ = selected[i].getStringValue("DFTJZJ");
				String updateSQL = "";
				if (state.equals("δ�ύ") || state.equals("���˻�")) {
					if (Double.parseDouble(JFFSE) < Double.parseDouble(JFTJZJ)
							|| Double.parseDouble(DFFSE) < Double
									.parseDouble(DFTJZJ)) {
						MessageBox.show(this, "��ѡ���ύ�ļ�¼�д��ڽ跽������С�ڽ跽�����ʽ���ߴ���������С�ڴ��������ʽ�����ݣ���������д���ύ��");
					} else {
						updateSQL = "update WN_JDFKMHZB_02 set state='���ύ' where id='"
								+ selectedId + "'";
						list.add(updateSQL);
					}
				}
				System.out.println(list.size());
				if (list.size() >= 5000) {
					UIUtil.executeBatchByDS(null, list);
					list.clear();
				}
			}
			if (list.size() >= 0) {
				UIUtil.executeBatchByDS(null, list);
			} else {
				MessageBox.show("��ǰѡ�������Ѿ��ύ,������ѡ��");
			}
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
