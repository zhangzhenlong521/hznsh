package com.pushworld.ipushgrc.ui.cmpreport.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * �Ϲ�ר���ά�����ϱ�!!!
 * ���ǶԺϹ汨��(cmp_report2)�ĵ��б�,�����а�ť�����������޸ġ���ɾ�������ύ�������̼�ء�
 * @author xch
 *
 */
public class CmpReport2EditWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billList = null; //

	private WLTButton btn_insert, btn_edit, btn_del, btn_wfmonitor; //

	public void initialize() {
		billList = new BillListPanel("CMP_REPORT2_CODE1"); //
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //
		btn_edit.addActionListener(this);
		btn_del = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		btn_del.addActionListener(this);
		boolean wf = TBUtil.getTBUtil().getSysOptionBooleanValue("�Ϲ汨���Ƿ��߹�����", true);
		if (wf) {
			btn_wfmonitor = WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR); //
			btn_wfmonitor.addActionListener(this);
			billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_edit, btn_del, btn_wfmonitor }); //
		} else {
			billList.setItemVisible("state", false);//������߹�������������״̬��loj/2015-05-21��
			billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_edit, btn_del }); //
		}

		billList.repaintBillListButton(); //
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_edit) {
			onEdit();
		} else if (obj == btn_del) {
			onDelete();
		} else if (obj == btn_wfmonitor) {
			wfStartOrMonitor();
		}
	}

	private void onEdit() {
		BillVO selectVO = billList.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("�½�".equals(selectVO.getStringValue("state"))) {
			BillCardPanel cardpanel = new BillCardPanel(billList.getTempletVO());
			cardpanel.setBillVO(selectVO);
			cardpanel.setEditableByEditInit();
			BillCardDialog cardDialog = new BillCardDialog(this, "�޸�", cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			cardDialog.setVisible(true);
			if (cardDialog.getCloseType() == 1) {
				billList.refreshCurrSelectedRow();
			}
		} else {
			MessageBox.show(this, "��״̬�����ݲ����Ա༭!");
		}
	}

	private void onDelete() {
		BillVO selectVO = billList.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("�½�".equals(selectVO.getStringValue("state"))) {
			if (MessageBox.confirmDel(this)) {
				try {
					UIUtil.executeBatchByDS(null, new String[] { "delete from " + selectVO.getSaveTableName() + " where id = " + selectVO.getStringValue("id") });
					billList.removeSelectedRow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			MessageBox.show(this, "��״̬�����ݲ�����ɾ��!");
		}
	}

	private void wfStartOrMonitor() {
		BillListPanel list = billList;
		BillVO vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		if (!vo.containsKey("wfprinstanceid")) {
			MessageBox.show(list, "ѡ�еļ�¼��û�ж��幤�����ֶ�(wfprinstanceid)!"); //
			return; //
		}

		String str_wfprinstanceid = vo.getStringValue("wfprinstanceid"); //
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {// �������δ�����������̣�����������
			try {
				new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", list, null); // ������!f��������
				// ����������ͬʱ����������״̬��Ϊ������
				String prinstanceid = UIUtil.getStringValueByDS(null, "select wfprinstanceid from  " + list.templetVO.getTablename() + " where id = " + vo.getStringValue("id"));
				if (prinstanceid == null) // ������̲�δ����
					return;
				if (prinstanceid.equals(""))
					return;
				else {
					String id = vo.getStringValue("id");
					String sql = "update " + list.getTempletVO().getTablename() + "  set STATE = '������' where id = " + id + " and STATE not like '���̽���'";
					UIUtil.executeUpdateByDS(null, sql);
					list.refreshData();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// ���̼��
			cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(list, str_wfprinstanceid, vo); //
			wfMonitorDialog.setMaxWindowMenuBar();
			wfMonitorDialog.setVisible(true);
		}
	}
}
