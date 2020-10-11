package cn.com.infostrategy.ui.workflow.design;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.workflow.engine.WorkflowUIUtil;

/**
 * ���յ�ʶ��
 * 
 * 
 * 
 */
public class Dialog_FlowCopy extends BillDialog implements BillListSelectListener, WorkFlowCellSelectedListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private BillListPanel billListpanel_workflow = null; //
	private WorkFlowDesignWPanel workflowPanel = null;
	private BillListPanel billListpanel_point = null; //
	private BillListPanel list_cmpfile = null;
	private RefItemVO returnRefItemVO = null;
	private WLTButton btn_confirm, btn_cancel;
	private WLTButton btn_risk;

	private int closeType = -1;
	private HashVO hvo = null;
	int type = 0;

	public Dialog_FlowCopy(Container _parent, String _title, int _width, int li_height, HashVO _hvs, int _type) {
		super(_parent, _title, _width, li_height); //
		this.setTitle(_title);
		this.setSize(_width, li_height);
		hvo = _hvs;
		type = _type;
		initialize();
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
		billListpanel_workflow = new BillListPanel("pub_wf_process_event"); //  ����
		if (hvo.getStringValue("type").equals("����涨")) {
			billListpanel_workflow.QueryDataByCondition("code='����涨/�����淶###'  and name='����涨/�����淶###'");
			billListpanel_workflow.setDataFilterCustCondition("code='����涨/�����淶###'  and name='����涨/�����淶###'");
		} else {
			billListpanel_workflow.setDataFilterCustCondition("wftype='��ϵ����'");
		}
		billListpanel_workflow.getQuickQueryPanel().setVisible(true); //

		billListpanel_workflow.setAllBillListBtnVisiable(false); //
		billListpanel_workflow.addBillListSelectListener(this); //

		workflowPanel = new WorkFlowDesignWPanel(false); //  ����ͼ  ����
		workflowPanel.addWorkFlowCellSelectedListener(this); //
		WLTSplitPane splitPanel = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billListpanel_workflow, workflowPanel); //����
		splitPanel.setDividerLocation(350); //

		this.add(splitPanel); //
		this.add(getSouthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * ���÷��յ�������а�ť�Ƿ���ʾ
	 * ��ȥ�����ť
	 * 
	 * @param b
	 */
	public void setRiskPanelBtnVisible(boolean b) {
		billListpanel_point.getBillListBtnPanel().setVisible(b);
		billListpanel_point.getBillListBtn("comm_listselect").setVisible(true);
	}

	public void setCellAddRisk(String _id, RiskVO _riskVO) {
		workflowPanel.setCellAddRisk(_id, _riskVO); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("����");
		btn_cancel = new WLTButton("ȡ��");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();

		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	private void onCancel() {
		closeType = BillDialog.CANCEL;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	private void onConfirm() {
		ArrayList<String> al_sql = new ArrayList<String>();
		if (JOptionPane.showConfirmDialog(this, "�������̲�����ɾ����ԭ�е�����ͼ,���Ƿ��룿", "�Ƿ���", 0) != 0) {
			return;
		} else {
			closeType = BillDialog.CONFIRM;
			try {
				String old_flowid = billListpanel_workflow.getSelectedBillVO().getStringValue("id");
				al_sql.add("delete   from    pub_wf_activity  where  processid='" + hvo.getStringValue("flowid") + "'");
				al_sql.add("delete   from    pub_wf_transition  where  processid='" + hvo.getStringValue("flowid") + "'");
				al_sql.add("delete   from    pub_wf_group  where  processid='" + hvo.getStringValue("flowid") + "'");
				UIUtil.executeBatchByDS(null, al_sql);
				WorkflowUIUtil wfUIUtil = new WorkflowUIUtil();     //���ƹ�����������
				wfUIUtil.CopyFlow(hvo, old_flowid, type);
				MessageBox.show("���̵���ɹ���");
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.show("���̵���ʧ�ܣ�");
			}
		}
		this.dispose();
	}

	public String getcode() {
		return billListpanel_workflow.getSelectedBillVO().getStringValue("code");
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getSource() == list_cmpfile) {
			billListpanel_workflow.QueryDataByCondition("userdef01='" + list_cmpfile.getSelectedBillVO().getStringValue("id") + "'");
		} else {
			final String str_wfid = _event.getCurrSelectedVO().getStringValue("id"); //
			if (str_wfid != null) {
				workflowPanel.loadGraphByID(str_wfid); //
			}
		}

	}

	private HashVO convertHashVO(BillVO _billvo) {
		String[] strkeys = _billvo.getKeys(); //
		HashVO hvo = new HashVO();
		for (int i = 1; i < strkeys.length; i++) {
			hvo.setAttributeValue(strkeys[i], _billvo.getStringValue(strkeys[i])); //
		}
		return hvo;
	}

	public void onWorkFlowCellSelected(WorkFlowCellSelectedEvent _event) {
		String str_processid = "" + workflowPanel.getCurrentProcessVO().getId();
		String str_activityid = "" + _event.getActivityVO().getId();
		//billListpanel_point.QueryDataByCondition("wfprocess_id='" + str_processid + "' and wfactivity_id='" + str_activityid + "'"); //
	}

}
