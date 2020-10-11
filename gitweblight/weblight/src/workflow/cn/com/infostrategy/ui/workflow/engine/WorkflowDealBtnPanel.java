package cn.com.infostrategy.ui.workflow.engine;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory;
import cn.com.infostrategy.ui.workflow.WorkFlowDealEvent;
import cn.com.infostrategy.ui.workflow.WorkFlowDealListener;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;
import cn.com.infostrategy.ui.workflow.msg.PassReadBtn;

/**
 * ����������ť�����,������Ҫ,�Ժ�����һ�����ֻҪ����������Ϳ��Դ���������! ������һ���ɲ�ε����һ��,����������뵽һ�������...
 * ��������һ����װ���õĿؼ�!!�������������ɢ���..
 * 
 * @author xch
 * 
 */
public class WorkflowDealBtnPanel extends JPanel implements ActionListener, BillListSelectListener {

	private static final long serialVersionUID = 2787155705172269162L; //
	private BillCardPanel bindBillCardPanel = null; // �󶨵Ŀ�Ƭ���
	private BillListPanel bindBillListPanel = null; // �󶨵��б����

	private String BTN_WORKFLOW_INSERT = "�½�"; // �½�����
	private String BTN_WORKFLOW_DELETE = "ɾ��"; // �������������ɾ��������!!
	private String BTN_WORKFLOW_UPDATE = "�޸�"; // ��������������޸ĸ�����!!
	// ��������ť������
	private String BTN_WORKFLOW_PROCESS = "����"; // ���̴���
	private String BTN_WORKFLOW_SUBMIT = "�ύ"; // ��ͬ���ύ
	private String BTN_WORKFLOW_RECEIVE = "����"; // ���ո�����.
	private String BTN_WORKFLOW_REJECT = "�˻�"; // ����ͬ��
	private String BTN_WORKFLOW_BACK = "�ܾ�"; // ����ֱ�ӷ�����һ��,Ҳ���ǽ������Ҫ���·�����һ���ߵ�����!
	private String BTN_WORKFLOW_CANCEL = "����"; // ����վ���������Գ���
	private String BTN_WORKFLOW_HOLD = "��ͣ";
	private String BTN_WORKFLOW_RESTART = "����";
	private String BTN_WORKFLOW_MONITOR = "���̼��";
	private String BTN_WORKFLOW_VIEW = "�������"; //�������,�����б��е������һ��,��Ϊ����Ҫͬʱ�г���ʷ���!!!��ʵ���Ǵ������!
	private String BTN_WORKFLOW_EXPORT = "����";
	private String BTN_WORKFLOW_EXPORT_ALL = "������ҳ";
	private String BTN_WORKFLOW_CDB = "�߶���";
	private String BTN_WORKFLOW_PASSREAD = "����"; //���������� �����/2012-11-28��
	private String BTN_WORKFLOW_YJBD = "�������";
	protected JButton btn_workflow_insert, btn_workflow_update, btn_workflow_process, btn_workflow_submit, btn_workflow_receive, btn_workflow_reject, btn_workflow_back, btn_workflow_cancel, btn_workflow_delete, btn_workflow_monitor, btn_workflow_view, btn_workflow_export, btn_workflow_export_all, btn_workflow_hold, btn_workflow_restart, btn_workflow_cdb, btn_workflow_passread, btn_workflow_yjbd; // ������ϵͳ��ť

	private ArrayList v_btns = new ArrayList(); // ������¼���а�ť..

	private WorkFlowServiceIfc workFlowService = null; //
	private WorkFlowInsertDilaog insertdialog = null;
	private WorkFlowProcessDialog processDialog = null;

	private Vector v_WFDealListener = new Vector(); //���̼�����!

	private String filePath = "";

	// ͨ����Ƭ����
	public WorkflowDealBtnPanel(BillCardPanel _billCardPanel) {
		bindBillCardPanel = _billCardPanel; //
		init();
	}

	// ͨ���б���
	public WorkflowDealBtnPanel(BillListPanel _billListPanel) {
		this.bindBillListPanel = _billListPanel; //
		init();
	}

	private void init() {
		this.setOpaque(false); //����͸��
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0)); //
		this.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		btn_workflow_insert = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_INSERT), "insert.gif");// �������½�
		btn_workflow_update = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_UPDATE), "office_030.gif"); // �������޸�
		btn_workflow_process = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_PROCESS), "office_026.gif"); // ����������
		btn_workflow_submit = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_SUBMIT), "office_036.gif"); // �������ύ
		btn_workflow_receive = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_RECEIVE), "office_160.gif"); // ����������
		btn_workflow_reject = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_REJECT), "closewindow.gif"); // �������ܾ�
		btn_workflow_back = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_BACK), "office_020.gif"); // ������������һ��
		btn_workflow_cancel = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_CANCEL), "undo.gif"); // ������������һ��
		btn_workflow_hold = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_HOLD), "office_047.gif"); // ��������ͣ
		btn_workflow_restart = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_RESTART), "office_048.gif"); // ����������.
		btn_workflow_delete = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_DELETE), "del.gif"); // ɾ��
		btn_workflow_monitor = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_MONITOR), "office_046.gif"); // ���̼��
		btn_workflow_view = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_VIEW), "office_148.gif"); // �������
		btn_workflow_export = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_EXPORT), "office_141.gif"); // ���̵���!!
		btn_workflow_export_all = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_EXPORT_ALL), "office_141.gif"); // ���̵���!!
		btn_workflow_cdb = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_CDB), "zt_062.gif"); //
		btn_workflow_passread = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_PASSREAD), "zt_062.gif"); //���������� �����/2012-11-28��
		btn_workflow_yjbd = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_YJBD), "office_036.gif");
		if (ClientEnvironment.isAdmin()) { //ÿ���������ť�ĺ������Ķ��Ұ���,�ɴ���ʾ����[xch/2012-09-27,�ʴ��ֳ�]
			btn_workflow_process.setToolTipText("�߼�λ��:WorkflowDealBtnPanel.onProcess()"); //
			btn_workflow_cancel.setToolTipText("�߼�λ��:WorkflowDealBtnPanel.onCancel()"); //
			btn_workflow_monitor.setToolTipText("�߼�λ��:WorkflowDealBtnPanel.onMonitor()"); //
			btn_workflow_view.setToolTipText("�߼�λ��:WorkflowDealBtnPanel.onViewWorkFlow()"); //
			btn_workflow_export.setToolTipText("�߼�λ��:WorkflowDealBtnPanel.onExportReport()"); //
		}
		v_btns.add(btn_workflow_insert); // ����������.
		v_btns.add(btn_workflow_update); // �������޸�
		v_btns.add(btn_workflow_receive); // ����������..
		v_btns.add(btn_workflow_process); // ����������..
		v_btns.add(btn_workflow_yjbd);
		// v_btns.add(btn_workflow_submit); //
		// v_btns.add(btn_workflow_reject); //
		// v_btns.add(btn_workflow_back); //
		v_btns.add(btn_workflow_cancel); //
		// v_btns.add(btn_workflow_hold); //
		// v_btns.add(btn_workflow_restart); //
		v_btns.add(btn_workflow_delete); //
		v_btns.add(btn_workflow_monitor); //
		v_btns.add(btn_workflow_view); //
		v_btns.add(btn_workflow_export); //
		v_btns.add(btn_workflow_export_all);
		v_btns.add(btn_workflow_cdb);
		v_btns.add(btn_workflow_passread);
		for (int i = 0; i < v_btns.size(); i++) {
			WLTButton btn = (WLTButton) v_btns.get(i);
			btn.addActionListener(this);
			this.add(btn); //
		}

		if (bindBillListPanel != null) {
			bindBillListPanel.addBillListSelectListener(this); //
		}
	}

	public void hiddenAllBtns() {
		for (int i = 0; i < v_btns.size(); i++) {
			JButton btn = (JButton) v_btns.get(i);
			btn.setVisible(false); //
		}
	}

	private String strWitchBox = null;

	public void setWitchBox(String newstrBox) {
		this.strWitchBox = newstrBox;
	}

	public String getWitchBox() {
		return this.strWitchBox;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_workflow_process) { //�Ժ�Ļ�����:���б���������ֻ�д���ť,��û���ύ,�˻صȰ�ť,�ύ��ť���ڵ��������г���! 
			//������ǿ�Ƭ����? ����˵�ɴ�Ͳ����ڿ�Ƭ�����,��Ϊ�⿨Ƭ�ǲ�����,��Ϊ������ʷ�����¼Ҫ��ʾ!
			onProcess(); //����!!!
		} else if (e.getSource() == btn_workflow_submit) {
			onSubmit();
		} else if (e.getSource() == btn_workflow_receive) {
			onReceive(); // ����
		} else if (e.getSource() == btn_workflow_reject) {
			onReject();
		} else if (e.getSource() == btn_workflow_back) {
			onBack();
		} else if (e.getSource() == btn_workflow_cancel) {
			onCancel();
		} else if (e.getSource() == btn_workflow_hold) {
			onHold();
		} else if (e.getSource() == btn_workflow_restart) {
			onRestart();
		} else if (e.getSource() == btn_workflow_delete) {
			onDelete(); //
		} else if (e.getSource() == btn_workflow_monitor) {
			onMonitor(); //
		} else if (e.getSource() == btn_workflow_export) {
			onExportReport(); //
		} else if (e.getSource() == btn_workflow_export_all) {
			//������ҳ��ť �����/2013-06-05��
			if (MessageBox.confirm(this, "�����������ݻ�����Ӱ�����������,��ֻ�ܵ�����ҳ����(��ҳ�������ɵ���),��ȷ��Ҫ������ҳ������?")) {
				onExportReport_All();
			}
		} else if (e.getSource() == btn_workflow_view) {
			onViewWorkFlow(); //���,��ֻ�ɿ���Ƭ����ʷ���!!
		} else if (e.getSource() == btn_workflow_insert) {
			onInsert(); //
		} else if (e.getSource() == btn_workflow_update) {
			onUpdate(); //
		} else if (e.getSource() == btn_workflow_cdb) {
			onCDB();
		} else if (e.getSource() == btn_workflow_passread) {
			onPassRead(); //���������� �����/2012-11-28��
		} else if (e.getSource() == btn_workflow_yjbd) {
			onYJBD();
		}
	}

	public JButton getButton(String _text) {
		for (int i = 0; i < v_btns.size(); i++) {
			JButton btn = (JButton) v_btns.get(i);
			if (btn.getText().equals(_text)) {
				return btn;
			}
		}
		return null;
	}

	public void setAllButtonVisiable(boolean _visiable) {
		for (int i = 0; i < v_btns.size(); i++) {
			JButton btn = (JButton) v_btns.get(i);
			btn.setVisible(_visiable); //
		}
	}

	// �����½���ť�Ƿ���ʾ
	public void setInsertBtnVisiable(boolean _visiable) {
		btn_workflow_insert.setVisible(_visiable); //
	}

	// �����޸İ�ť�Ƿ���ʾ
	public void setUpdateBtnVisiable(boolean _visiable) {
		btn_workflow_update.setVisible(_visiable); //
	}

	// ����ɾ����ť�Ƿ���ʾ
	public void setDeleteBtnVisiable(boolean _visiable) {
		btn_workflow_delete.setVisible(_visiable); //
	}

	//��������ť
	// ���ô���ť�Ƿ���ʾ
	public void setProcessBtnVisiable(boolean _visiable) {
		btn_workflow_process.setVisible(_visiable); //
	}

	// �����ύ��ť�Ƿ���ʾ
	public void setSubmitBtnVisiable(boolean _visiable) {
		btn_workflow_submit.setVisible(_visiable); //
	}

	// ���ý��հ�ť�Ƿ���ʾ
	public void setReceiveBtnVisiable(boolean _visiable) {
		btn_workflow_receive.setVisible(_visiable); //
	}

	// �����˻ذ�ť�Ƿ���ʾ
	public void setRejectBtnVisiable(boolean _visiable) {
		btn_workflow_reject.setVisible(_visiable); //
	}

	// ���þܾ���ť�Ƿ���ʾ
	public void setBackBtnVisiable(boolean _visiable) {
		btn_workflow_back.setVisible(_visiable); //
	}

	// ���ó��ذ�ť�Ƿ���ʾ
	public void setCancelBtnVisiable(boolean _visiable) {
		btn_workflow_cancel.setVisible(_visiable); //
	}

	// ������ͣ��ť�Ƿ���ʾ
	public void setHoldBtnVisiable(boolean _visiable) {
		btn_workflow_hold.setVisible(_visiable); //
	}

	// ���ü�����ť�Ƿ���ʾ
	public void setRestartBtnVisiable(boolean _visiable) {
		btn_workflow_restart.setVisible(_visiable); //
	}

	// �������̼�ذ�ť�Ƿ���ʾ
	public void setMonitorBtnVisiable(boolean _visiable) {
		btn_workflow_monitor.setVisible(_visiable); //
	}

	//
	public void setViewWFBtnVisiable(boolean _visiable) {
		btn_workflow_view.setVisible(_visiable); //
	}

	//���õ����Ƿ���ʾ!!!
	public void setExportBtnVisiable(boolean _visiable) {
		btn_workflow_export.setVisible(_visiable); //
	}

	//���õ���ȫ���Ƿ���ʾ!!!
	public void setExportAllBtnVisiable(boolean _visiable) {
		btn_workflow_export_all.setVisible(_visiable); //
	}

	public void setCDBBtnVisiable(boolean _visiable) {
		btn_workflow_cdb.setVisible(_visiable);
	}

	public void setPassReadBtnVisiable(boolean _visiable) {
		btn_workflow_passread.setVisible(_visiable);
	}

	public void setYJBDBtnVisiable(boolean _visiable) {
		btn_workflow_yjbd.setVisible(_visiable);
	}

	// �����½���ť����
	public void setInsertBtnText(String _text) {
		btn_workflow_insert.setText(_text); //
	}

	// ����ɾ����ť����
	public void setDeleteBtnText(String _text) {
		btn_workflow_delete.setText(_text); //
	}

	// �����޸İ�ť����
	public void setUpdateBtnText(String _text) {
		btn_workflow_update.setText(_text); //
	}

	//��������ť	
	// ���ô���ť����
	public void setProcessBtnText(String _text) {
		btn_workflow_process.setText(_text); //
	}

	// �����ύ��ť����	
	public void setSubmitBtnText(String _text) {
		btn_workflow_submit.setText(_text); //
	}

	// ���ý��հ�ť����
	public void setReceiveBtnText(String _text) {
		btn_workflow_receive.setText(_text); //
	}

	// �����˻ذ�ť����
	public void setRejectBtnText(String _text) {
		btn_workflow_reject.setText(_text); //
	}

	// ���þܾ���ť����
	public void setBackBtnText(String _text) {
		btn_workflow_back.setText(_text); //
	}

	// ���ó��ذ�ť����
	public void setCancelBtnText(String _text) {
		btn_workflow_cancel.setText(_text); //
	}

	// ������ͣ��ť����
	public void setHoldBtnText(String _text) {
		btn_workflow_hold.setText(_text); //
	}

	// ���ü�����ť����
	public void setRestartBtnText(String _text) {
		btn_workflow_restart.setText(_text); //
	}

	// �������̼�ذ�ť����
	public void setMonitorBtnText(String _text) {
		btn_workflow_monitor.setText(_text); //
	}

	public void setCDBBtnText(String _text) {
		btn_workflow_cdb.setText(_text); //
	}

	public void setPassReadBtnText(String _text) {
		btn_workflow_passread.setText(_text); //
	}

	public void setYJBDBtnText(String _text) {
		btn_workflow_yjbd.setText(_text); //
	}

	//ȡ������ʵ��id
	private String getTaskId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //���û�ж���hvo,����ǰ�Ļ���!!��ֱ��ȡ
			return null; //
		} else {
			return hvo.getStringValue("task_taskdealid"); //���к���ȡ
		}
	}

	//ȡ������ʵ��id
	private String getTaskOffId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //���û�ж���hvo,����ǰ�Ļ���!!��ֱ��ȡ
			return null; //
		} else {
			return hvo.getStringValue("task_taskoffid"); //���к���ȡ
		}
	}

	//ȡ������ʵ��id
	private String getPrDealPoolId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //���û�ж���hvo,����ǰ�Ļ���!!��ֱ��ȡ
			return null; //
		} else {
			return hvo.getStringValue("task_prdealpoolid"); //���к���ȡ
		}
	}

	//ȡ������ʵ��id
	private String getPrinstanceId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //���û�ж���hvo,����ǰ�Ļ���!!��ֱ��ȡ
			return _billVO.getStringValue("WFPRINSTANCEID"); //ֱ�Ӵ�BillVO��ȡ
		} else {
			return hvo.getStringValue("task_prinstanceid"); //���к���ȡ
		}
	}

	/**
	 * �������޸�
	 */
	private void onUpdate() {
		if (this.bindBillListPanel.getSelectedBillVO() == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		WorkFlowUpdateDilaog dialog = new WorkFlowUpdateDilaog(this, "���̴���", this.bindBillListPanel.getTempletVO().getTempletcode(), this.bindBillListPanel, this.bindBillListPanel.getSelectedBillVO().getStringValue("id")); //
		dialog.setVisible(true); //
	}

	/**
	 * �߶���
	 */
	private void onCDB() {
		if (this.bindBillListPanel.getSelectedBillVO() == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		try {
			new WorkflowUIUtil().dealCDB(bindBillListPanel, bindBillListPanel.getTempletVO().getTempletname(), getWFPrinstanceID(this.bindBillListPanel.getSelectedBillVO()));
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				MessageBox.show(bindBillListPanel, e.getMessage());
			} else {
				MessageBox.show(bindBillListPanel, "����δ֪�쳣!");
			}
		}
	}

	/**
	 * ���������� �����/2012-11-28��
	 */
	private void onPassRead() {
		BillVO billVO = getWorkFlowDealBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		String str_pkValue = billVO.getPkValue();
		if (str_pkValue == null || str_pkValue.trim().equals("")) {
			MessageBox.show(this, "�ô������������ҵ�񵥾�û�в�ѯ������!\n������Ϊϵͳ���Խ׶ε�������ɵ�,��������Ȼ�ܹ�����!\nϵͳ���ߺ������������ݺ������Ӧ�ò����ٴ���,��֪Ϥ!");
		}

		String str_task_id = getTaskId(billVO); //��Ϣ����id
		String str_prDealPool_id = getPrDealPoolId(billVO); //��������id
		String str_wfinstanceid = getPrinstanceId(billVO); //����ʵ��!

		try {
			new PassReadBtn(bindBillListPanel, "������������Ϣ", str_task_id, str_prDealPool_id, str_wfinstanceid);
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				MessageBox.show(bindBillListPanel, e.getMessage());
			} else {
				MessageBox.show(bindBillListPanel, "����δ֪�쳣!");
			}
		}
	}

	/**
	 * �������½�
	 */
	private void onInsert() {
		insertdialog = new WorkFlowInsertDilaog(this, "���̴���", this.bindBillListPanel.getTempletVO().getTempletcode(), this.bindBillListPanel); //
		insertdialog.setVisible(true); //

	}

	public WorkFlowInsertDilaog getWorkFlowInsertDialog() {
		return insertdialog;
	}

	/**
	 * ������ϵİ�ť!!!
	 */
	private void onProcess() {
		new WorkFlowDealActionFactory().dealAction("deal", this.bindBillListPanel, getAllDealListeners()); //������!
	}

	private void onYJBD() {
		new WorkFlowDealActionFactory().dealAction("yjbd", this.bindBillListPanel, getAllDealListeners());
	}

	private WorkFlowDealListener[] getAllDealListeners() {
		WorkFlowDealListener[] listeners = (WorkFlowDealListener[]) v_WFDealListener.toArray(new WorkFlowDealListener[0]); //
		return listeners; //
	}

	/**
	 * �������̼���!!
	 * @param _listener
	 */
	public void addWorkFlowDealListener(WorkFlowDealListener _listener) {
		v_WFDealListener.add(_listener); //
	}

	//�������̽�����,��Ҫˢ���б�
	private void afterDealWorkFlow(int _dealType, String _prInstanceId, String _prdealPoolId) {
		if (this.bindBillListPanel != null) {
			bindBillListPanel.refreshData(true); //���²�ѯ��һ��???
		}
		for (int i = 0; i < v_WFDealListener.size(); i++) {
			WorkFlowDealListener listener = (WorkFlowDealListener) v_WFDealListener.get(i); //
			WorkFlowDealEvent event = new WorkFlowDealEvent(this, _dealType, _prInstanceId, _prdealPoolId); //
			listener.onDealWorkFlow(event); //���ü�����!!!
		}
	}

	public WorkFlowProcessPanel getWorkFlowProcessPanel() {
		return processDialog.getWordFlowProcessPanel();
	}

	// �ύ
	private void onSubmit() {
		onWorkFlowDeal("SUBMIT"); //
		// MessageBox.show(this, "�ύ�ɹ�!"); //
	}

	// ����..
	private void onReceive() {
		BillVO billVO = getWorkFlowDealBillVO(); //
		if (billVO == null) {
			// MessageBox.show(this, "No Record will be deal!",
			// WLTConstants.MESSAGE_WARN); //
			if (new TBUtil().getSysOptionBooleanValue("����������ť��������Ƿ���ʾ���հ�ť", true)) {
				MessageBox.show(this, "û����Ҫ���������!"); //
			}
			return; //
		}

		if (!billVO.isHaveKey("WFPRINSTANCEID")) {
			MessageBox.show(this, "�ü�¼û�ж��幤������Ҫ����[wfprinstanceid],���ȶ���֮!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		// ����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
		// ��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
		String str_wfinstance = getPrinstanceId(billVO); // ��������ʵ��,����������ʵ��ID!!!
		if (str_wfinstance == null || str_wfinstance.equals("")) { // //�������ʵ���ֶε�ֵΪ��,��˵����Ҫ��������!
			MessageBox.show(this, "û����������,������������!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		try {
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
			service.receiveDealTask(str_wfinstance, ClientEnvironment.getInstance().getLoginUserID()); // ���յ�ǰ����
			// btn_workflow_receive.setEnabled(false); //
			if (new TBUtil().getSysOptionBooleanValue("����������ť��������Ƿ���ʾ���հ�ť", true)) {
				MessageBox.show(this, "��������ɹ�!");
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	// �ܾ�
	private void onReject() {
		onWorkFlowDeal("REJECT"); // �ܾ�����!!
	}

	/**
	 * ����������֮���˲��� ��վ������,��������������
	 * 
	 * @throws Exception
	 */
	protected void onBack() {
		onWorkFlowDeal("BACK"); // ���˲���
	}

	/**
	 * ȡ�����ݶ���
	 * 
	 * @return
	 */
	private BillVO getWorkFlowDealBillVO() {
		if (this.bindBillListPanel != null) {
			return bindBillListPanel.getSelectedBillVO(false, true); //��Ҫȡ�������ӱ��е�����!
		} else if (this.bindBillCardPanel != null) {
			return bindBillCardPanel.getBillVO();
		} else {
			return null;
		}
	}

	private Pub_Templet_1VO getWorkFlowDealBillTempletVO() {
		if (this.bindBillListPanel != null) {
			return bindBillListPanel.getTempletVO();
		} else if (this.bindBillCardPanel != null) {
			return bindBillCardPanel.getTempletVO();
		} else {
			return null;
		}
	}

	/**
	 * �������ύ...
	 * 
	 * @throws Exception
	 */
	private void onWorkFlowDeal(String _dealtype) {
		try {
			BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				// MessageBox.show(this, "No Record will be deal!",
				// WLTConstants.MESSAGE_WARN); //
				MessageBox.show(this, "û����Ҫ���������!"); //
				return; //
			}

			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "�ü�¼û�ж��幤������Ҫ����[wfprinstanceid],���ȶ���֮!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			// ����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
			// ��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
			String str_wfinstance = getPrinstanceId(billVO); // ��������ʵ��,����������ʵ��ID!!!
			if (str_wfinstance == null || str_wfinstance.equals("")) { // //�������ʵ���ֶε�ֵΪ��,��˵����Ҫ��������!!
				str_wfinstance = new WorkflowUIUtil().startWorkFlow(this, billVO); // �ȴ���һ������,����������ʵ������!!!
				if (str_wfinstance != null) {
					// ��дҳ��,����BillListPanel��BillCardPanel�л�д����..
					writeBackBillPanel(str_wfinstance); //
					processDeal(str_wfinstance, billVO, _dealtype); // ������ִ�и�����!!!
				}
			} else { // ���̽���
				processDeal(str_wfinstance, billVO, _dealtype); // ���������е�
			}

		} catch (Exception ex) {
			ex.printStackTrace(); //
			MessageBox.showWarn(this, ex.getMessage()); //
		}
	}

	/**
	 * 
	 */
	private void writeBackBillPanel(String _instanceid) {
		if (this.bindBillListPanel != null) {
			int li_selrow = bindBillListPanel.getSelectedRow(); // //
			bindBillListPanel.setValueAt(new StringItemVO(_instanceid), li_selrow, "WFPRINSTANCEID"); // //
		} else if (this.bindBillCardPanel != null) {
			bindBillCardPanel.setValueAt("WFPRINSTANCEID", new StringItemVO(_instanceid)); //
		}
	}

	/**
	 * ֱ�Ӵ��������е�����
	 * 
	 * @param _prinstanceId
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	private void processDeal(String _prinstanceId, BillVO _billVO, String _dealtype) throws Exception {
		String str_loginuserid = ClientEnvironment.getInstance().getLoginUserID(); //
		String str_loginuserDeptID = ClientEnvironment.getCurrLoginUserVO().getBlDeptId(); //
		String str_prdealPoolId = getPrDealPoolId(_billVO); //
		WorkFlowServiceIfc service = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); // //
		WFParVO firstTaskVO = service.getFirstTaskVO(_prinstanceId, str_prdealPoolId, str_loginuserid, str_loginuserDeptID, _billVO, _dealtype); // ȡ������!
		if (firstTaskVO == null) {
			MessageBox.show(this, "��û�и����̵�����!");
			return; //
		}

		WFParVO secondCallVO = null; // �ڶ����ٴ�����Ĳ���VO,��ʵ���ǵ�һ��ȡ�õĲ����ٴ��ύ������!!
		if ((firstTaskVO.isIsprocessed() && firstTaskVO.isIsassignapprover()) || firstTaskVO.isIsneedmsg()) // ������ս��߲�����Ҫ�˹�ѡ�������,������Ҫ��������,�򵯳��Ի���,����ֱ���ύ
		{
			WorkFlowDealDialog dialog = new WorkFlowDealDialog(this, _billVO, firstTaskVO, _dealtype); ////
			dialog.setVisible(true); //
			if (dialog.getClosetype() == 1) { // ��������ȷ������
				secondCallVO = dialog.getReturnVO(); //
			} else if (dialog.getClosetype() == 2) {

				// UIUtil.executeUpdateByDS(null,"delete from pub_wf_prinstance
				// where billpkvalue='"+_billVO.getStringValue("id")+"'");

			}
		} else { // ֱ���ٴ��ύ
			secondCallVO = firstTaskVO; //
		}

		if (secondCallVO != null) {
			BillVO returnBillVO = service.secondCall(secondCallVO, str_loginuserid, _billVO, _dealtype); // �ڶ����ٴ�����!!
			refreshWorkFlowPanel(_prinstanceId); // ˢ���������!!
		}
	}

	/**
	 * 
	 * @param _prinstanceid
	 */
	public void refreshWorkFlowPanel(String _prinstanceid) {
		if (1 == 1) {
			return;
		}

		hiddenAllBtns(); // ���������а�ť!!!

		if (_prinstanceid == null || _prinstanceid.equals("")) { // ���û������ʵ��
			btn_workflow_submit.setVisible(true); // ��Ĭ������Ϊ����
			return;
		}

		try {
			String str_sql_1 = "select status from pub_wf_prinstance where id=" + _prinstanceid; //
			String str_status = UIUtil.getHashVoArrayByDS(null, str_sql_1)[0].getStringValue("status"); //
			if (str_status.equals("HOLD")) {
				btn_workflow_restart.setVisible(true); //
				btn_workflow_monitor.setVisible(true); //
			} else if (str_status.equals("END")) {
				btn_workflow_monitor.setVisible(true); //
			} else if (str_status.equals("RUN")) {
				String str_userid = ClientEnvironment.getInstance().getLoginUserID(); // ��ȡ�õ�¼��Ա������!!
				String str_sql_2 = "select * from v_pub_wf_dealpool_1 where prinstanceid=" + _prinstanceid + " and participant_user=" + str_userid + " and issubmit='N' and isprocess='N'"; //

				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql_2); //
				if (hvs != null && hvs.length > 0) {
					for (int i = 0; i < hvs.length; i++) {
						if (hvs[i].getIntegerValue("submitcount").intValue() > 0) {
							btn_workflow_submit.setVisible(true); //
						}

						if (hvs[i].getIntegerValue("rejectcount").intValue() > 0) { // ����оܾ��ĵ��ߣ�����ʾ�ܾ��İ�ť
							btn_workflow_reject.setVisible(true); //
						}

						if (hvs[i].getStringValue("iscanback").equals("Y")) { // ���������ˣ�����ʾ���˰�ť
							btn_workflow_back.setVisible(true); //
						}

						if (hvs[i].getStringValue("curractivitytype").equals("END")) {
							btn_workflow_submit.setVisible(true); //
						}
					}
				}

				btn_workflow_hold.setVisible(true); // ��Ĭ������Ϊ����
				btn_workflow_monitor.setVisible(true); // ��ذ�ť��Զ��ʾ!!!
				btn_workflow_cancel.setVisible(true); //
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ���
	private void onMonitor() {
		BillVO billVO = getWorkFlowDealBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "û�ж�Ӧ�Ĵ�������!"); //
			return; //
		}

		if (!billVO.isHaveKey("WFPRINSTANCEID")) {
			MessageBox.show(this, "û��BillType��Busitype,WFPRINSTANCEID��Ϣ,���ܽ������̴���!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		// ����е���������ҵ������,������ʵ��Ϊ��,��������������ʵ��!!!!
		// ��������ʵ���ҳ�����������Ƿ��������ҵĴ���������,�����,�򵯳�һ�����м���һ��"ͬ��/�ܾ�",��һ�����ı���,��һ���ύ��ť,��Ȼ�Ա߻�Ҫ��һ��������̵İ�ť!!!
		String str_wfinstance = getWFPrinstanceID(billVO); // ����ʵ��ID..
		if (str_wfinstance == null || str_wfinstance.equals("")) { // ��������!�������ʵ��Ϊ��
			MessageBox.show(this, "�ü�¼��û��������������,�����ύ��������!", WLTConstants.MESSAGE_WARN); //
			return;
		}

		WorkflowMonitorDialog dialog = new WorkflowMonitorDialog(this, str_wfinstance, billVO); //�������!!
		dialog.setMaxWindowMenuBar();
		dialog.setVisible(true); //
	}

	/**
	 * �������������
	 */
	private void onViewWorkFlow() {
		BillVO billVO = getWorkFlowDealBillVO(); //
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return; //
		}
		String str_pkValue = billVO.getPkValue(); //
		if (str_pkValue == null || str_pkValue.trim().equals("")) {
			MessageBox.show(this, "�ô������������ҵ�񵥾�û�в�ѯ������!\n������Ϊϵͳ���Խ׶ε�������ɵ�,��������Ȼ�ܹ�����!\nϵͳ���ߺ������������ݺ������Ӧ�ò����ٴ���,��֪Ϥ!"); //
		}
		String str_task_id = getTaskId(billVO); //��Ϣ����id
		String str_prDealPool_id = getPrDealPoolId(billVO); //��������id
		String str_wfinstanceid = getPrinstanceId(billVO); //����ʵ��!
		BillCardPanel cardPanel = new BillCardPanel(this.bindBillListPanel.getTempletVO()); //����һ����Ƭ���!!
		cardPanel.setBillVO(billVO.deepClone()); //�ڿ�Ƭ����������,��Ҫ��¡һ��!!
		String str_title = "���[" + str_task_id + "][" + str_prDealPool_id + "][" + str_wfinstanceid + "]"; //
		WorkFlowProcessFrame processFrame = new WorkFlowProcessFrame(this, str_title, cardPanel, this.bindBillListPanel, str_task_id, str_prDealPool_id, str_wfinstanceid, true, "�������������״̬,���Բ��ܽ����ύ�Ȳ�����"); //
		processFrame.getWfProcessPanel().getBtn_Confirm().setVisible(false); //����ȷ�ϰ�ť
		processFrame.setVisible(true); //
		processFrame.toFront(); //
	}

	//������ҳ���� �����/2013-06-05��
	private void onExportReport_All() {
		BillVO[] billVOs = bindBillListPanel.getBillVOs();
		if (billVOs.length == 0) {
			MessageBox.show(this, "�ɵ�������Ϊ0!");
			return;
		}

		try {
			JFileChooser chooser = new JFileChooser("C:\\");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setApproveButtonText("ѡ��");
			int li_result = chooser.showOpenDialog(this);
			if (li_result == JFileChooser.APPROVE_OPTION) {
				filePath = chooser.getSelectedFile().getAbsolutePath();
				File f = new File(filePath);
				if (!f.exists()) {
					MessageBox.show(this, "·��:" + filePath + " ������!");
					return;
				}

				new SplashWindow(this, "�ò�����ʱ�ϳ�,�����ĵȴ�������", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						doExportReport_All((SplashWindow) e.getSource(), bindBillListPanel.getBillVOs(), filePath);
					}
				}, false);
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}
	}

	//������ҳ
	private void doExportReport_All(SplashWindow sw, BillVO[] billVOs, String filePath) {
		try {
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class);

			if (billVOs.length > 30) {
				sw.setWaitInfo("���ڵ���1-30������,��" + billVOs.length + "�����ݡ�����");
			} else {
				sw.setWaitInfo("���ڵ���1-" + billVOs.length + "������,��" + billVOs.length + "�����ݡ�����");
			}

			for (int i = 0; i < billVOs.length; i++) {
				File file = new File(filePath + "\\" + billVOs[i].getTempletName() + "_������ҳ����\\" + (i + 1) + "��" + StringFilter(billVOs[i].toString()) + ".html");
				File parent = file.getParentFile();
				if (parent == null || !parent.exists()) {
					parent.mkdirs();
				}

				FileOutputStream fout = new FileOutputStream(file, false);
				fout.write(getHtml(billVOs[i], service).getBytes("GBK"));
				fout.close();

				if (i > 0 && (i + 1) % 30 == 0) {
					Thread.sleep(60000); //ÿ30��ͣ��1����,����������������ʱ��,��֤������������ ��������ʵӦ�ü�ͬ����
					int end = i + 31;
					if (end > billVOs.length) {
						end = billVOs.length;
					}
					sw.setWaitInfo("���ڵ���" + (i + 2) + "-" + end + "������,��" + billVOs.length + "�����ݡ�����");
				}
			}
			MessageBox.show(this, "������ҳ�ɹ�!");
		} catch (Exception e) {
			MessageBox.show(this, "������ҳʧ��!");
		}
	}

	//�ļ����� �����/2013-06-05��
	private String getHtml(BillVO billVO, WorkFlowServiceIfc service) {
		BillCardPanel cardPanel = new BillCardPanel(this.bindBillListPanel.getTempletVO()); //����һ����Ƭ���
		cardPanel.setBillVO(billVO);
		cardPanel.setGroupExpandable("������Ϣ", false);
		cardPanel.setGroupExpandable("*���沿��д*", true);
		cardPanel.removeAll();

		String str_html = "";
		try {
			String str_wfinstanceid = getPrinstanceId(billVO); //����ʵ��
			HashVO[] hvs = service.getMonitorTransitions(str_wfinstanceid, false, (ClientEnvironment.isAdmin() ? false : true)); //���Դ��������Ϣ

			WFHistListPanelBuilder histListBuilder = new WFHistListPanelBuilder(hvs, billVO, true);
			BillListPanel billList_hist = histListBuilder.getBillListPanel(); //�������
			billList_hist.setBillListOpaque(false); //͸����
			billList_hist.getMainScrollPane().setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
			billList_hist.setToolBarPanelBackground(LookAndFeel.cardbgcolor);
			System.out.println(billList_hist.getTable().getPreferredSize().getWidth() + ">>>>>>>");
			String str_cardhtml = cardPanel.getExportHtml();
			str_html = str_cardhtml.substring(0, str_cardhtml.length() - 18);
			str_html += "\r\n" + getHtmlTableText(billList_hist) + "</body>\r\n</html>\r\n";
			billList_hist.removeAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return str_html;
	}

	public String getHtmlTableText(BillListPanel _billList_hist) {
		JTable table = _billList_hist.getTable();
		int li_row_count = table.getRowCount() + 1; // +1 is table head
		int li_col_count = table.getColumnCount();
		StringBuffer strbf_html = new StringBuffer("<table id=\"list\" cellpadding=\"3\" cellspacing=\"1\" width=720>\r\n");
		//generate table head
		strbf_html.append("<tr>\r\n");

		List allcolumnWidth = new ArrayList(); //���п��
		double totle_width = 0;
		double msgwidth = 0;
		for (int i = 0; i < li_col_count; i++) {
			String columnName = table.getColumnName(i);
			String width = table.getColumn(columnName).getWidth() + "";
			if ("�������".equals(_billList_hist.getColumnName(i))) {
				msgwidth = Integer.parseInt(width);
			}
			totle_width += Integer.parseInt(width);
		}

		for (int i = 0; i < li_col_count; i++) {
			String columnName = table.getColumnName(i);
			double width = 0;
			if ("�������".equals(_billList_hist.getColumnName(i))) {
				width = 300;
			} else {
				width = table.getColumn(columnName).getWidth();
				width = (width / (totle_width - msgwidth)) * (totle_width - 300);
			}
			strbf_html.append("<th width=\"").append(((int)(width*100 / totle_width) + "%")).append("\">").append(_billList_hist.getColumnName(i)).append("</th>\r\n");
		}
		strbf_html.append("</tr>\r\n");

		//generate table body
		for (int i = 1; i < li_row_count; i++) {
			strbf_html.append("<tr>\r\n");
			for (int j = 0; j < li_col_count; j++) {
				strbf_html.append("<td>");
				Object obj = table.getValueAt(i - 1, j); //
				if (obj != null) {
					String str_itemkey = table.getColumnName(j);
					String str_itemType = _billList_hist.getTempletItemVO(str_itemkey).getItemtype();
					//Ҫ����cell���ݵ����;������ɵ�html����
					if (str_itemType.equals(WLTConstants.COMP_OFFICE) || str_itemType.equals(WLTConstants.COMP_FILECHOOSE)) {
						strbf_html.append(getComponentToHrefHtml(_billList_hist.getBillVO(j), obj.toString(), str_itemkey, str_itemType));
					} else {
						strbf_html.append(obj.toString());
					}
				}
				strbf_html.append("</td>\r\n");
			}
			strbf_html.append("</tr>\r\n");
		}

		//match the non-complete html content
		strbf_html.append("</table>\r\n");
		return strbf_html.toString();
	}

	protected String getComponentToHrefHtml(final BillVO _billVO, final String _objectValueText, final String _itemkey, final String _itemtype) {
		StringBuffer strbf_html = new StringBuffer("&nbsp;&nbsp;");
		StringBuffer strbf_url = new StringBuffer();
		if (_itemtype.equals(WLTConstants.COMP_OFFICE)) {
			//��htmlҳ��������ļ����ӳ�����
			String str_file = _billVO.getStringValue(_itemkey); //
			strbf_url.append(System.getProperty("CALLURL") + "/DownLoadFileServlet?pathtype=" + "office" + "&filename=" + str_file); //��ǰ����Office�ؼ��򿪵�,��������ҵ�ͻ����־�Ȼoffice�ؼ����ܱ���,���Ըɴ�����ֱ�����ص�!
			strbf_html.append("<a target=\"_blank\" href=\"").append(strbf_url.toString()).append("\" >").append(_objectValueText).append("</a>");
		} else if (_itemtype.equals(WLTConstants.COMP_FILECHOOSE)) {
			//��htmlҳ��ĸ����ļ����ӡ����ء��ĳ�����
			String str_file = _billVO.getStringValue(_itemkey);
			if (str_file == null || "".endsWith(str_file)) {
				return "";
			}
			String[] files = str_file.split(";");
			String[] viewfiles = _objectValueText.split(";");
			for (int i = 0; i < viewfiles.length; i++) {
				strbf_url.append(System.getProperty("CALLURL")).append("/DownLoadFileServlet?pathtype=upload&filename=").append(files[i]); //
				strbf_html.append("<a target=\"_blank\" href=\"").append(strbf_url.toString()).append("\" >").append(viewfiles[i]).append("</a><br>&nbsp;&nbsp;");
				strbf_url.delete(0, strbf_url.length());
			}
		}
		return strbf_html.toString();
	}

	//�ļ������� �����/2013-06-05��
	public String StringFilter(String str) {
		String regEx = "[\\\\/*:?\"<>|]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	private void onExportReport() {
		try {
			final BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "û�ж�Ӧ�Ĵ�������!"); //
				return; //
			}
			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "û��BillType��Busitype,WFPRINSTANCEID��Ϣ,���ܽ������̴���!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			final String str_wfinstance = getWFPrinstanceID(billVO); // ����ʵ��ID..
			if (str_wfinstance == null || str_wfinstance.equals("")) { // ��������!�������ʵ��Ϊ��
				MessageBox.show(this, "�ü�¼��û��������������,�����ύ��������!", WLTConstants.MESSAGE_WARN); //
				return;
			}
			//��������Զ����� �����/2013-01-18��
			String str_class = TBUtil.getTBUtil().getSysOptionStringValue("������������Ŀ�Զ�����", "cn.com.infostrategy.bs.workflow.WorkflowHtmlReportDMO"); //
			String str_isForceUseDefaultExport = TBUtil.getTBUtil().getSysOptionStringValue("�����������Ƿ�ʹ��Ĭ���߼�", "Y"); //
			if ("Y".equals(str_isForceUseDefaultExport)) { //�����ǿ��ʹ��Ĭ���߼�!!!
				String str_msg = "������뵼���������ô?\r\n"; //
				if (ClientEnvironment.isAdmin()) {
					str_msg = str_msg + "��Ϊָ����ϵͳ����[�����������Ƿ�ʹ��Ĭ���߼�]=[Y],����ʹ��Ĭ�ϵ����߼�!(����Ϣֻ��Admin=Y��Ч)"; // 
				}
				//				if (!MessageBox.confirm(this, str_msg)) {
				//					return;
				//				}
				String str_wfExportFormula = getWorkFlowDealBillTempletVO().getWfcustexport(); //"����������������=aa,bb,cc;���̻���=���պϹ沿��,���쵼;��ɫ=���Ÿ�����,���Ҹ�����,���쵼��"; //
				if (str_wfExportFormula == null || str_wfExportFormula.trim().equals("")) {
					HashMap map_par = new HashMap(); //
					map_par.put("showType", "1"); //
					map_par.put("prinstanceid", str_wfinstance); //
					map_par.put("billvo", billVO); //
					map_par.put("roles", ClientEnvironment.getInstance().getLoginUserRoleIds());
					map_par.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
					UIUtil.loadHtml(str_class, map_par); //
				} else {
					String[] str_forItems = TBUtil.getTBUtil().split(str_wfExportFormula, "��"); //
					for (int i = 0; i < str_forItems.length; i++) {
						str_forItems[i] = TBUtil.getTBUtil().replaceAll(str_forItems[i], "��", ""); //
						str_forItems[i] = TBUtil.getTBUtil().replaceAll(str_forItems[i], "��", ""); //
					}

					HashVO[] hvs_mycorp = UIUtil.getParentCorpVOByMacro(1, null, "$������"); //
					String str_myCorpName = null; //
					String str_myCorpType = null; //
					if (hvs_mycorp.length > 0) {
						str_myCorpName = hvs_mycorp[0].getStringValue("name"); //
						str_myCorpType = hvs_mycorp[0].getStringValue("corptype"); //
					}

					ArrayList al_list = new ArrayList(); //
					for (int i = 0; i < str_forItems.length; i++) {
						String str_pp = str_forItems[i]; //
						String str_name = str_pp.substring(0, str_pp.indexOf("��")); //
						String str_fornula = str_pp.substring(str_pp.indexOf("��") + 1, str_pp.length()); //
						if (str_name.indexOf("@") > 0) {
							String str_ct = str_name.substring(str_name.indexOf("@") + 1, str_name.length()); //
							if (str_myCorpType != null && str_myCorpType.indexOf(str_ct) >= 0) { //���Ƕ������в���,�����Ƕ�������
								al_list.add(new String[] { str_name.substring(0, str_name.indexOf("@")), str_fornula }); //
								//System.out.println("��������[" + str_myCorpType + "][" + str_ct + "],����"); //
							} else {
								//System.out.println("����������[" + str_myCorpType + "][" + str_ct + "],������"); //
							}
						} else {
							al_list.add(new String[] { str_name, str_fornula }); //
							//System.out.println("û����,ֱ�Ӽ���"); //
						}
					}
					String[][] str_ops = new String[al_list.size() + 1][2];
					str_ops[0] = new String[] { "���������", null }; //
					for (int i = 0; i < al_list.size(); i++) {
						str_ops[i + 1] = (String[]) al_list.get(i); //
					}

					String[] str_btns = new String[str_ops.length]; //
					for (int i = 0; i < str_btns.length; i++) {
						str_btns[i] = str_ops[i][0]; //
					}

					int li_rt = MessageBox.showOptionDialog(this, "��ѡ�񵼳�����", "��ʾ", str_btns, 500, 150); //
					if (li_rt == 0) { //���������
						HashMap map_par = new HashMap(); //
						map_par.put("showType", "1"); //
						map_par.put("prinstanceid", str_wfinstance); //
						map_par.put("billvo", billVO); //
						map_par.put("����", str_ops[0][0]); //�Ƿ��Զ�������
						map_par.put("roles", ClientEnvironment.getInstance().getLoginUserRoleIds());
						map_par.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
						UIUtil.loadHtml(str_class, map_par); //
					} else { //���������
						if (li_rt > 0) {
							HashMap map_par = new HashMap(); //
							map_par.put("showType", "1"); //
							map_par.put("prinstanceid", str_wfinstance); //
							map_par.put("billvo", billVO); //
							map_par.put("�Ƿ��Զ�������", "Y"); //�Ƿ��Զ�������
							map_par.put("����", str_ops[li_rt][0]); //�Ƿ��Զ�������
							map_par.put("������ʽ", str_ops[li_rt][1]); //
							map_par.put("roles", ClientEnvironment.getInstance().getLoginUserRoleIds());
							map_par.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
							UIUtil.loadHtml(str_class, map_par); //
						}
					}
				}
			} else {
				VectorMap vm = new WorkflowUIUtil().getAllReport(billVO.getStringValue("billtype"), billVO.getStringValue("busitype"), billVO.getSaveTableName());
				if (vm != null && vm.size() > 0) { //
					if (vm.size() == 1) { //���ֻ��һ������,��ֱ�ӵ���!
						String str_msg = "������뵼���������ô?\r\n"; //
						if (ClientEnvironment.isAdmin()) {
							str_msg = str_msg + "�⽫���ݵ�������[" + billVO.getStringValue("billtype") + "],ҵ������[" + billVO.getStringValue("busitype") + "]�����̷����в��Ҷ�Ӧ�ı�������!(����Ϣֻ��Admin=Y��Ч)"; // 
						}
						if (!MessageBox.confirm(this, str_msg)) {
							return;
						}
						HashMap map_par = (HashMap) vm.get(0);
						map_par.put("showType", "3");
						map_par.put("prinstanceid", str_wfinstance); //
						map_par.put("billvo", billVO); //
						map_par.put("roles", ClientEnvironment.getInstance().getLoginUserRoleIds()); //
						map_par.put("userid", ClientEnvironment.getInstance().getLoginUserID());
						UIUtil.loadHtml("cn.com.infostrategy.bs.workflow.WorkflowHtmlReportDMO", map_par); //
					} else {
						JPopupMenu popm = new JPopupMenu();
						String itemname = null;
						int length = 115;
						for (int i = 0; i < vm.size(); i++) {
							final HashMap param = (HashMap) vm.get(i);
							itemname = (String) param.get("��������");
							JMenuItem menuItem = new JMenuItem(itemname);
							menuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									if ("N".equals((String) param.get("�Ƿ�����"))) {
										MessageBox.showInfo(btn_workflow_export, "��Ǹ��û��ʹ�ô˱����Ȩ��!");
										return;
									}
									param.put("prinstanceid", str_wfinstance); //
									param.put("billvo", billVO); //
									param.put("roles", ClientEnvironment.getInstance().getLoginUserRoleIds()); //
									param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
									try {
										UIUtil.loadHtml("cn.com.infostrategy.bs.workflow.WorkflowHtmlReportDMO", param);
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							});
							popm.add(menuItem);
						}
						popm.show(btn_workflow_export, 0, (int) btn_workflow_export.getPreferredSize().getHeight());
					}

				} else {
					MessageBox.showInfo(this, "û�ж��屨���������Ա��ϵ");
					return;
				}
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	private String getWFPrinstanceID(BillVO _billVO) {
		try {
			RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
			HashVO hvo = rowNumVO.getRecordHVO(); //
			if (hvo != null) {
				return hvo.getStringValue("task_prinstanceid"); //����к�����,��˵�����µĻ���,��ֱ�ӷ���!!
			}

			if (_billVO.getStringValue("WFPRINSTANCEID") == null) {
				String str_sql = "select id from pub_wf_prinstance where billtablename='" + _billVO.getSaveTableName() + "' and billpkname='" + _billVO.getPkName() + "' and billpkvalue='" + _billVO.getPkValue() + "'"; //
				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql); //
				if (hvs != null && hvs.length > 0) {
					if (this.bindBillListPanel != null) {
						bindBillListPanel.setValueAt(new StringItemVO(hvs[0].getStringValue("id")), bindBillListPanel.getSelectedRow(), "WFPRINSTANCEID"); //
					}

					if (this.bindBillCardPanel != null) {
						bindBillCardPanel.setValueAt("WFPRINSTANCEID", new StringItemVO(hvs[0].getStringValue("id"))); //
					}

					String str_instanceId = hvs[0].getStringValue("id"); //
					_billVO.setObject("WFPRINSTANCEID", new StringItemVO(str_instanceId)); //
					return str_instanceId;
				}
			} else {
				return _billVO.getStringValue("WFPRINSTANCEID"); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return null;
	}

	/**
	 * ���ز���!!! ��ǰ�Ļ������رȽ��鷳! ���µĻ�����
	 */
	private void onCancel() {
		try {
			BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "��ѡ����Ӧ�ļ�¼���д���!"); //
				return; //
			}
			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "û��BillType��Busitype,WFPRINSTANCEID��Ϣ,���ܽ������̴���!", WLTConstants.MESSAGE_WARN); //
				return; //
			}
			String str_prinstanceId = getPrinstanceId(billVO);
			if (str_prinstanceId == null || str_prinstanceId.trim().equals("")) { // ��������!�������ʵ��Ϊ��
				MessageBox.show(this, "�ü�¼��û��������������,�����ύ��������!", WLTConstants.MESSAGE_WARN); //
				return;
			}
			if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ�������ύ�ĸ�������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			String str_dealPoolId = getPrDealPoolId(billVO); //
			String str_taskOffId = getTaskOffId(billVO); //
			getWorkFlowService().cancelTask(str_prinstanceId, str_dealPoolId, str_taskOffId, ClientEnvironment.getCurrLoginUserVO().getId(), null); ////
			afterDealWorkFlow(1, str_prinstanceId, str_dealPoolId); ////
			MessageBox.show(this, "���ظ�����ɹ�!���������������²�ѯ�������ݼ����ύ!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); // ��ʾ�쳣��Ϣ!!!
		}
	}

	/**
	 * ɾ������!
	 */
	private void onDelete() {
		try {
			BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				// MessageBox.show(this, "��ѡ����Ӧ�ļ�¼���д���!",
				// WLTConstants.MESSAGE_WARN); //
				MessageBox.show(this, "��ѡ����Ӧ�ļ�¼���д���!"); //
				return; //
			}

			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "û��BillType��Busitype,WFPRINSTANCEID��Ϣ,���ܽ������̴���!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			String str_wfinstance = getPrinstanceId(billVO); //����ʵ��ID..
			if (str_wfinstance == null || str_wfinstance.trim().equals("")) { // //��������!�������ʵ��Ϊ��
				if (JOptionPane.showConfirmDialog(this, "���Ƿ������ɾ���ü�¼��?", "��ʾ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					String str_sql = "delete from " + billVO.getSaveTableName() + " where " + billVO.getPkName() + "='" + billVO.getPkValue() + "'";
					UIUtil.executeUpdateByDS(null, str_sql); //
					if (this.bindBillListPanel != null) {
						bindBillListPanel.removeSelectedRows(); //
					}
				}
				return; //
			}

			if (JOptionPane.showConfirmDialog(this, "���Ƿ������ɾ���ü�¼��?", "��ʾ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				getWorkFlowService().deleteTask(str_wfinstance, ClientEnvironment.getCurrLoginUserVO().getId()); // //����Զ�̷���ȡ��ĳһ�����ύ������!!!
				if (this.bindBillListPanel != null) {
					this.bindBillListPanel.removeSelectedRows(); //
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); // ��ʾ�쳣��Ϣ!!!
		}

	}

	// ��ͣ
	private void onHold() {
	}

	// ��������
	private void onRestart() {
	}

	/**
	 * ȡ�ù�����F����!!!
	 */
	private WorkFlowServiceIfc getWorkFlowService() throws Exception {
		if (workFlowService != null) {
			return workFlowService;
		}

		workFlowService = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); //
		return workFlowService;
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
	}

	public JButton getBtn_workflow_process() {
		return btn_workflow_process;
	}

}
