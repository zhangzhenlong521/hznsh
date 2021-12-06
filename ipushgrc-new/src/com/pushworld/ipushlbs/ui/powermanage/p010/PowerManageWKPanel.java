package com.pushworld.ipushlbs.ui.powermanage.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ��Ȩ����
 * 
 * @author yinliang
 * 
 */
public class PowerManageWKPanel extends AbstractWorkPanel implements ActionListener, BillListMouseDoubleClickedListener {
	private BillListPanel billlist = null;
	private BillCardPanel cardPanel; // ����������card
	private WLTButton btn_insert, btn_edit, btn_del, btn_query, btn_wfmonitor, btn_commit;

	@Override
	public void initialize() {
		billlist = new BillListPanel("LBS_POWERMANAGE_CODE1");
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_query = new WLTButton("���"); // �����ť��д��ȥ����Ƭ�еİ�ť
		btn_del = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_commit = new WLTButton("����"); // �ύ
		// ��ť��Ӽ���
		btn_insert.addActionListener(this);
		btn_commit.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_del.addActionListener(this);
		btn_query.addActionListener(this);
		// billlist��Ӽ���
		billlist.addBillListMouseDoubleClickedListener(this);
		billlist.addBatchBillListButton(new WLTButton[] { btn_insert, btn_edit, btn_del, btn_query, btn_commit });
		billlist.repaintBillListButton();
		this.add(billlist);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_insert)
			onInsert(); // ����״̬
		else if (obj == btn_edit)
			onEdit(); // ����޸İ�ť
		else if (obj == btn_del)
			onDelete(); // ���ɾ����ť
		else if (obj == btn_wfmonitor)
			onWfmonitor(); // �������������/��ذ�ť
		else if (obj == btn_commit)
			onCommit(); // �ύ
		else if (obj == btn_query)
			onQuery();
	}

	// �����ť�¼�
	private void onQuery() {
		BillVO billvo = billlist.getSelectedBillVO(); // ȡ�õ�ǰѡ����е�billvo
		if (billvo == null) {
			MessageBox.showSelectOne(billlist);
			return;
		}
		QueryInfo(billvo);
	}

	// ˫���¼�
	private void QueryInfo(BillVO billvo) {
		BillCardPanel cardPanel = new BillCardPanel("LBS_POWERMANAGE_CODE2"); // ��ǰ��Ƭpanel
		cardPanel.setBillVO(billvo); //
		BillCardDialog dialog = new BillCardDialog(billlist, "��Ȩ��Ϣ�鿴", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true);
	}

	private void onInsert() {
		// BillVO billvo = billlist.getSelectedBillVO();
		cardPanel = new BillCardPanel(billlist.templetVO); // ����һ����Ƭ���
		cardPanel.setLoaderBillFormatPanel(billlist.getLoaderBillFormatPanel()); // ���б��BillFormatPanel�ľ��������Ƭ
		cardPanel.insertRow(); // ��Ƭ����һ��!
		cardPanel.setEditableByInsertInit(); // ���ÿ�Ƭ�༭״̬Ϊ����ʱ������
		BillCardDialog dialog = new BillCardDialog(billlist, billlist.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); // ������Ƭ������
		dialog.setVisible(true); // ��ʾ��Ƭ����

		// btn_apply.addActionListener(this);
		if (dialog.getCloseType() == 1) { // �����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
			int li_newrow = billlist.newRow(false); //
			billlist.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			billlist.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); // �����б���е�����Ϊ��ʼ��״̬.
			billlist.setSelectedRow(li_newrow);

		}
	}

	// �ύ��Ȩ
	private void onCommit() {
		BillVO billVO = billlist.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(billlist); //
			return; //
		}
		if (billVO.getStringValue("commitstate").equals("�ѷ���")) {
			MessageBox.show(billlist, "����Ϣ�ѷ���,�����ظ�����!");
			return;
		}
		if (MessageBox.confirm(billlist, "��ȷ��Ҫ��������Ϣ��?")) {
			// ������Ȩ�·������е�״̬Ϊ�ѷ���
			String sql_commit = "update " + billVO.getSaveTableName() + " set commitstate = '�ѷ���' where id = '" + billVO.getStringValue("id") + "'";
			try {
				UIUtil.executeUpdateByDS(null, sql_commit);
				// �����ǰ��������Ȩ��ϢΪ�����������õģ�����Ҫ����Ȩ�����е�������Ϣ�ķ���״̬��Ϊ�ϼ��ѷ���
				if (billVO.getStringValue("REFAPPLY") != null && !billVO.getStringValue("REFAPPLY").equals("")) {
					sql_commit = "update lbs_powerapply set releasestate = '�ϼ��ѷ���' where id = '" + billVO.getStringValue("REFAPPLY") + "'";
					UIUtil.executeUpdateByDS(null, sql_commit);
					// ��Ȩ����ҳ���ǲ���Ҫ�Զ�ˢ�£��о��Զ�ˢ�²��Ǻܺã�����¼���λ����ִ���������������ܻ���Ӱ�졣
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			billlist.refreshCurrSelectedRow();
		}
	}

	// ����������/���
	private void onWfmonitor() {
		BillVO billVO = billlist.getSelectedBillVO(); //	
		if (billVO == null) {
			MessageBox.showSelectOne(billlist); //
			return; //
		}
		if (!billVO.containsKey("wfprinstanceid")) {
			MessageBox.show(billlist, "ѡ�еļ�¼��û�ж��幤�����ֶ�(wfprinstanceid)!"); //
			return; //
		}
		int flag = billlist.getSelectedRow(); // ��ǰѡ����
		String str_wfprinstanceid = billVO.getStringValue("wfprinstanceid"); // ȡ�ô˹���������ID

		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {// �������δ�����������̣�����������
			onBillListWorkFlowProcess(billVO, flag);
		} else { // ��ع�����
			cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(billlist, str_wfprinstanceid, billVO); //
			wfMonitorDialog.setMaxWindowMenuBar();
			wfMonitorDialog.setVisible(true); //
		}
	}

	// ��������
	private void onBillListWorkFlowProcess(BillVO billvo, int flag) {
		try {
			new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", billlist, null); // ������!
			// ����������ͬʱ����������״̬��Ϊ�ѷ���
			billlist.setSelectedRow(flag);
			if (billlist.getSelectedBillVO().getStringValue("wfprinstanceid") == null) // ������̲�δ����
				return;
			else if (billlist.getSelectedBillVO().getStringValue("wfprinstanceid").equals(""))
				return;
			else {
				String sql_update = "update lbs_powermanage set flowstate = '�ѷ���' where id = '" + billvo.getStringValue("id") + "'";
				UIUtil.executeUpdateByDS(null, sql_update);
				billlist.refreshCurrSelectedRow();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onDelete() {
		BillVO billvo = billlist.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (billvo.getStringValue("commitstate").equals("δ����")) {
			if (MessageBox.confirmDel(this)) {
				try {
					UIUtil.executeBatchByDS(null, new String[] { "delete from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id") });
					billlist.removeSelectedRow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			MessageBox.showNotAllow(this,billvo.getStringValue("commitstate") );
		}
	}

	private void onEdit() {
		BillVO billvo = billlist.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (billvo.getStringValue("commitstate").equals("δ����")) {
			BillCardPanel cardpanel = new BillCardPanel(billlist.getTempletVO());
			cardpanel.setBillVO(billvo);
			cardpanel.setEditableByEditInit();
			BillCardDialog carddialog = new BillCardDialog(this, "�޸�", cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			carddialog.setVisible(true);
			if (carddialog.getCloseType() == 1)
				billlist.refreshCurrSelectedRow();
		} else {
			MessageBox.showNotAllow(this,billvo.getStringValue("commitstate") );
		}
	}

	// list˫���¼�
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
		QueryInfo(_event.getBillListPanel().getSelectedBillVO());
	}
}
