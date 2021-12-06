package com.pushworld.ipushlbs.ui.casemanage.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * ���߰����������������߰�����������
 * 
 * @author wupeng
 * 
 */
public class LawCaseApplyIncpWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener {

	BillListPanel list = null;
	WLTButton edit = null;
	WLTButton delete = null;
	WLTButton monitor = null;// ���̼��

	@Override
	public void initialize() {
		list = new BillListPanel("LBS_CASE_SELF_CODE1");
		monitor = WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR);
		monitor.addActionListener(this);
		edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		list.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_POPINSERT), edit, delete,// ɾ��
				WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD),// ���
				monitor });
		list.repaintBillListButton();
		list.addBillListSelectListener(this);
		this.add(list);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == monitor) {
			wfStartOrMonitor();
		}
	}

	private void wfStartOrMonitor() {
		BillListPanel list = this.getbilListPanel();
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
				new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", list, null); // ��������!f��������
				// ����������ͬʱ����������״̬��Ϊ������
				String prinstanceid = UIUtil.getStringValueByDS(null, "select wfprinstanceid from  " + list.templetVO.getTablename() + " where id = " + vo.getStringValue("id"));
				if (prinstanceid == null) // ������̲�δ����
					return;
				if (prinstanceid.equals(""))
					return;
				else {
					String id = vo.getStringValue("id");
					String sql = "update " + list.getTempletVO().getTablename() + "  set endtype = '������' where id = " + id;
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

	private BillListPanel getbilListPanel() {
		return list;
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO vo = event.getCurrSelectedVO();
		if (vo != null) {
			if (!vo.containsKey("wfprinstanceid")) {// �ֶ��в������������ֶ�
				return; //
			}
			String wf_id = vo.getStringValue("wfprinstanceid");
			if (wf_id == null || wf_id.trim().isEmpty()) {// û�ж�Ӧ�Ĺ�����,�޸ĺ�ɾ����ť����
				edit.setEnabled(true);
				delete.setEnabled(true);
			} else {
				edit.setEnabled(false);
				delete.setEnabled(false);
			}
		}
	}

}