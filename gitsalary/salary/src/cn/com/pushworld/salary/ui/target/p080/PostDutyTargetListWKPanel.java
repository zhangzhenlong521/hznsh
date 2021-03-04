package cn.com.pushworld.salary.ui.target.p080;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.paymanage.RefDialog_Month;

/**
 * Ա����λְ��ָ��ά�����������󡣸�λְ��ָ����ֱ���쵼��ֹ��쵼��֡�һ����ָ�겻�࣬��������������ָ�꣬���������Ͷ��Ե�.
 * ��λְ��ָ��Ӱ���λ���ι��ʡ�
 * @author haoming
 * create by 2014-3-18
 */
public class PostDutyTargetListWKPanel extends AbstractWorkPanel implements ChangeListener, BillListSelectListener, ActionListener {
	private static final long serialVersionUID = -7083761029869083036L;
	private BillListPanel listPanel = null;
	private WLTButton btn_add, btn_edit, btn_delete; // �б��ϵ����а�ť
	private WLTTabbedPane tabPane;
	private BillListPanel postTypeListPanel, targetListPanel;
	private WLTButton btn_add_2, btn_edit_2, btn_delete2, btn_test;

	private String containsPostDutyCheck = TBUtil.getTBUtil().getSysOptionStringValue("�Ƿ������λְ�����۹���", "N");

	public void initialize() {
		//ʹ�� sal_post_duty_target_list����������Ա����ָ����һ��������ɾ�ĵĽ��档
		if ("N".equals(containsPostDutyCheck)) {
			JLabel label = new JLabel("�˹����ѱ����á�����ϵͳ���������������ò���[�Ƿ������λְ�����۹���]ֵΪ[Y]");
			this.add(label);
			return;
		}
		tabPane = new WLTTabbedPane();
		tabPane.addChangeListener(this);
		tabPane.addTab("����λ��鿴", getPostTargetPanel());
		tabPane.addTab("ȫ��", getAllTargetPanel());
		this.add(tabPane);
	}

	private JComponent getPostTargetPanel() {
		postTypeListPanel = new BillListPanel("PUB_COMBOBOXDICT_SALARY");
		postTypeListPanel.QueryDataByCondition("type = 'н��_��λ����'");
		postTypeListPanel.setDataFilterCustCondition("type = 'н��_��λ����'");
		targetListPanel = new BillListPanel("V_SAL_POSTGROUP_DUTY_CODE1");
		postTypeListPanel.addBillListSelectListener(this);
		btn_add_2 = new WLTButton("����");
		btn_edit_2 = new WLTButton("�޸�");
		btn_delete2 = new WLTButton("ɾ��");
		btn_test = new WLTButton("����");
		btn_add_2.addActionListener(this);
		btn_edit_2.addActionListener(this);
		btn_delete2.addActionListener(this);
		btn_test.addActionListener(this);
		targetListPanel.addBatchBillListButton(new WLTButton[] { btn_add_2, btn_edit_2, btn_delete2, btn_test });
		targetListPanel.repaintBillListButton();
		WLTSplitPane splitpane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, postTypeListPanel, targetListPanel);
		return splitpane;
	}

	/*
	 * 
	 */
	private JPanel getAllTargetPanel() {
		listPanel = new BillListPanel("SAL_POST_DUTY_TARGET_LIST_CODE1");
		listPanel.QueryDataByCondition(null);
		btn_add = new WLTButton("����");
		btn_edit = new WLTButton("�޸�");
		btn_delete = new WLTButton("ɾ��");
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete });
		listPanel.repaintBillListButton();
		return listPanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAdd(listPanel, 1);
		} else if (e.getSource() == btn_edit) {
			onEdit(listPanel);
		} else if (e.getSource() == btn_delete) {
			onDelete(listPanel);
		} else if (e.getSource() == btn_add_2) {
			onAdd2();
		} else if (e.getSource() == btn_edit_2) {
			onEdit2();
		} else if (e.getSource() == btn_delete2) {
			onDelete2();
		} else if (e.getSource() == btn_test) {
			onAction();
		}
	}

	private void onAction() {
		try {
			SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			HashMap map = new HashMap();
			BillListDialog listdialog = new BillListDialog(this, "ѡ��һ���ƻ�", "SAL_TARGET_CHECK_LOG_CODE1");
			listdialog.getBilllistPanel().QueryDataByCondition(null);
			listdialog.setVisible(true);
			BillVO vos[] = listdialog.getReturnBillVOs();
			if (vos.length == 0) {
				return;
			}
			map.put("logid", vos[0].getStringValue("id"));
			map.put("month", vos[0].getStringValue("checkdate"));
			String count = UIUtil.getStringValueByDS(null, "select count(*) from sal_person_postduty_score where logid = " + vos[0].getStringValue("id") + " and targettype='��������ָ��' ");
			if (!"0".equals(count)) {
				if (MessageBox.confirm(this, "ϵͳ���Ѿ������ôο��˵ĸ�������ָ�꣬�Ƿ���ɾ��")) {
					UIUtil.executeUpdateByDS(null, "delete from sal_person_postduty_score where logid = '" + vos[0].getStringValue("id") + "' and targettype = '��������ָ��'");
				} else {
					return;
				}
			}
			ifc.createPostDutyScoreTable(map);
			MessageBox.show(this, "���ɳɹ�.");
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����
	 */
	private void onAdd(BillListPanel listPanel, int tab) {
		BillCardPanel cardPanel = null;
		// ����һ����Ƭ���
		cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		//		cardPanel.setRealValueAt("targettype", "Ա������ָ��");

		// ������
		BillCardDialog cardDialog = new BillCardDialog(listPanel, "������λ����ְ��ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);

		// ȷ������
		if (cardDialog.getCloseType() == 1) {
			int li_newrow = listPanel.newRow(false); //
			listPanel.setBillVOAt(li_newrow, cardDialog.getBillVO(), false);
			listPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); // �����б���е�����Ϊ��ʼ��״̬.
			listPanel.setSelectedRow(li_newrow); //		
			listPanel.refreshCurrSelectedRow();
		}
	}

	/**
	 * �༭
	 */
	private void onEdit(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		// ����һ����Ƭ���
		BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.setBillVO(billVO);

		// ������
		BillCardDialog dialog = new BillCardDialog(this, "�޸ĸ�λ����ְ��ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);
		// ȷ������
		if (dialog.getCloseType() == 1) {
			if (listPanel.getSelectedRow() == -1) {
			} else {
				listPanel.setBillVOAt(listPanel.getSelectedRow(), dialog.getBillVO());
				listPanel.setRowStatusAs(listPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
				listPanel.refreshCurrSelectedRow();
			}
		}
	}

	/**
	 * ɾ��
	 */
	private void onDelete(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		listPanel.doDelete(false);
		try {
			UIUtil.executeBatchByDS(null, new String[] { "delete from sal_post_duty_check_post where targetid=" + billVO.getPkValue() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onAdd2() {
		BillCardPanel cardPanel = null;
		// ����һ����Ƭ���
		cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();

		// ������
		BillCardDialog cardDialog = new BillCardDialog(listPanel, "������λ����ְ��ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			postTypeListPanel.QueryDataByCondition(null);
		}
	}

	private void onEdit2() {
		BillVO billVO = targetListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(targetListPanel);
			return;
		}
		// ����һ����Ƭ���
		BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.queryDataByCondition("id = " + billVO.getPkValue());

		// ������
		BillCardDialog dialog = new BillCardDialog(this, "�޸ĸ�λ����ְ��ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);
		// ȷ������
		if (dialog.getCloseType() == 1) {
			targetListPanel.refreshData();
		}
	}

	private void onDelete2() {
		BillVO billVO = targetListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(targetListPanel);
			return;
		}
		if (!MessageBox.confirmDel(this)) {
			return; //
		}
		try {
			UIUtil.executeBatchByDS(null, new String[] { "delete from sal_post_duty_target_list where id = " + billVO.getPkValue(), "delete from sal_post_duty_check_post where targetid=" + billVO.getPkValue() });
			targetListPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO selectVo = event.getCurrSelectedVO();
		targetListPanel.setDataFilterCustCondition("postid like '%;" + selectVo.getStringValue("id") + ";%'");
		targetListPanel.QueryDataByCondition("postid like '%;" + selectVo.getStringValue("id") + ";%'");
	}

	HashVO resuleVos[] = null;

	public void stateChanged(ChangeEvent changeevent) {
		if (tabPane.getSelectedIndex() == 0) {
			targetListPanel.QueryDataByCondition(null);
		} else {
			listPanel.refreshData();
		}
	}

	private SalaryServiceIfc services;

	private SalaryServiceIfc getService() {
		if (services == null) {
			try {
				services = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return services;
	}

	private String getDate(Container _parent) {
		String selectDate = "2013-08";
		try {
			RefDialog_Month chooseMonth = new RefDialog_Month(_parent, "��ѡ�����ϴ����ݵĿ����·�", null, null);
			chooseMonth.initialize();
			chooseMonth.setVisible(true);
			if (chooseMonth.getCloseType() != 1) {
				return null;
			}
			selectDate = chooseMonth.getReturnRefItemVO().getName();
			return selectDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "2013-08";
	}
}
