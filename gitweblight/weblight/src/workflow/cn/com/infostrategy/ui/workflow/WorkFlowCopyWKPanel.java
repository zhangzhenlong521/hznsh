package cn.com.infostrategy.ui.workflow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.workflow.design.CreateWorkFlowDialog;

public class WorkFlowCopyWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel list_process;
	private WLTButton btn_copy;

	@Override
	public void initialize() {
		list_process = new BillListPanel("PUB_WF_PROCESS_CODE2");
		list_process.setItemVisible("cmpfilename", false);
		btn_copy = new WLTButton("��������ͼ");
		btn_copy.addActionListener(this);//
		list_process.addBatchBillListButton(new WLTButton[] { btn_copy });
		list_process.repaintBillListButton();
		list_process.setItemWidth("code", 300);
		list_process.setItemWidth("name", 300);
		this.add(list_process);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_copy) {
			onCopyProcess();
		}
	}

	private void onCopyProcess() {
		if (list_process.getSelectedBillVO() == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		CreateWorkFlowDialog dialog = new CreateWorkFlowDialog(this);
		dialog.setTitle("��������Ϣ");
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_processcode = dialog.getProcessCode();// �����µı���
			String str_processname = dialog.getProcessName();// �����µ�����
			try {
				FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
				service.copyProcessById(list_process.getSelectedBillVO().getStringValue("id"), str_processcode, str_processname);
				list_process.refreshData();
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}

	}

}
