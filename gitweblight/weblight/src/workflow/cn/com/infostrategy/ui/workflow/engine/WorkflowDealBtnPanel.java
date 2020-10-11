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
 * 工作流处理按钮的面板,至关重要,以后任意一个面板只要加入这个面板就可以处理工作流了! 它就像一个可插拔的面板一样,可以任意加入到一个面板中...
 * 它必须是一个封装极好的控件!!与其他面板是松散耦合..
 * 
 * @author xch
 * 
 */
public class WorkflowDealBtnPanel extends JPanel implements ActionListener, BillListSelectListener {

	private static final long serialVersionUID = 2787155705172269162L; //
	private BillCardPanel bindBillCardPanel = null; // 绑定的卡片面板
	private BillListPanel bindBillListPanel = null; // 绑定的列表面板

	private String BTN_WORKFLOW_INSERT = "新建"; // 新建流程
	private String BTN_WORKFLOW_DELETE = "删除"; // 流程启动后可以删除该流程!!
	private String BTN_WORKFLOW_UPDATE = "修改"; // 流程启动后可以修改该流程!!
	// 工作流按钮的文字
	private String BTN_WORKFLOW_PROCESS = "处理"; // 流程处理
	private String BTN_WORKFLOW_SUBMIT = "提交"; // 即同意提交
	private String BTN_WORKFLOW_RECEIVE = "接收"; // 接收该流程.
	private String BTN_WORKFLOW_REJECT = "退回"; // 即不同意
	private String BTN_WORKFLOW_BACK = "拒绝"; // 就是直接返回上一步,也就是解决不需要重新反向拖一根线的问题!
	private String BTN_WORKFLOW_CANCEL = "撤回"; // 就是站在上流可以撤回
	private String BTN_WORKFLOW_HOLD = "暂停";
	private String BTN_WORKFLOW_RESTART = "继续";
	private String BTN_WORKFLOW_MONITOR = "流程监控";
	private String BTN_WORKFLOW_VIEW = "流程浏览"; //流程浏览,它与列表中的浏览不一样,因为它需要同时列出历史意见!!!其实就是处理面板!
	private String BTN_WORKFLOW_EXPORT = "导出";
	private String BTN_WORKFLOW_EXPORT_ALL = "导出本页";
	private String BTN_WORKFLOW_CDB = "催督办";
	private String BTN_WORKFLOW_PASSREAD = "传阅"; //工作流传阅 【杨科/2012-11-28】
	private String BTN_WORKFLOW_YJBD = "意见补登";
	protected JButton btn_workflow_insert, btn_workflow_update, btn_workflow_process, btn_workflow_submit, btn_workflow_receive, btn_workflow_reject, btn_workflow_back, btn_workflow_cancel, btn_workflow_delete, btn_workflow_monitor, btn_workflow_view, btn_workflow_export, btn_workflow_export_all, btn_workflow_hold, btn_workflow_restart, btn_workflow_cdb, btn_workflow_passread, btn_workflow_yjbd; // 工作流系统按钮

	private ArrayList v_btns = new ArrayList(); // 用来记录所有按钮..

	private WorkFlowServiceIfc workFlowService = null; //
	private WorkFlowInsertDilaog insertdialog = null;
	private WorkFlowProcessDialog processDialog = null;

	private Vector v_WFDealListener = new Vector(); //流程监听者!

	private String filePath = "";

	// 通过卡片构建
	public WorkflowDealBtnPanel(BillCardPanel _billCardPanel) {
		bindBillCardPanel = _billCardPanel; //
		init();
	}

	// 通过列表构建
	public WorkflowDealBtnPanel(BillListPanel _billListPanel) {
		this.bindBillListPanel = _billListPanel; //
		init();
	}

	private void init() {
		this.setOpaque(false); //设置透明
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0)); //
		this.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		btn_workflow_insert = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_INSERT), "insert.gif");// 工作流新建
		btn_workflow_update = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_UPDATE), "office_030.gif"); // 工作流修改
		btn_workflow_process = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_PROCESS), "office_026.gif"); // 工作流处理
		btn_workflow_submit = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_SUBMIT), "office_036.gif"); // 工作流提交
		btn_workflow_receive = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_RECEIVE), "office_160.gif"); // 工作流接收
		btn_workflow_reject = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_REJECT), "closewindow.gif"); // 工作流拒绝
		btn_workflow_back = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_BACK), "office_020.gif"); // 工作流返回上一步
		btn_workflow_cancel = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_CANCEL), "undo.gif"); // 工作流返回上一步
		btn_workflow_hold = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_HOLD), "office_047.gif"); // 工作流暂停
		btn_workflow_restart = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_RESTART), "office_048.gif"); // 工作流继续.
		btn_workflow_delete = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_DELETE), "del.gif"); // 删除
		btn_workflow_monitor = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_MONITOR), "office_046.gif"); // 流程监控
		btn_workflow_view = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_VIEW), "office_148.gif"); // 流程浏览
		btn_workflow_export = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_EXPORT), "office_141.gif"); // 流程导出!!
		btn_workflow_export_all = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_EXPORT_ALL), "office_141.gif"); // 流程导出!!
		btn_workflow_cdb = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_CDB), "zt_062.gif"); //
		btn_workflow_passread = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_PASSREAD), "zt_062.gif"); //工作流传阅 【杨科/2012-11-28】
		btn_workflow_yjbd = new WLTButton(UIUtil.getLanguage(BTN_WORKFLOW_YJBD), "office_036.gif");
		if (ClientEnvironment.isAdmin()) { //每次找这个按钮的函数在哪都找半天,干脆提示算了[xch/2012-09-27,邮储现场]
			btn_workflow_process.setToolTipText("逻辑位置:WorkflowDealBtnPanel.onProcess()"); //
			btn_workflow_cancel.setToolTipText("逻辑位置:WorkflowDealBtnPanel.onCancel()"); //
			btn_workflow_monitor.setToolTipText("逻辑位置:WorkflowDealBtnPanel.onMonitor()"); //
			btn_workflow_view.setToolTipText("逻辑位置:WorkflowDealBtnPanel.onViewWorkFlow()"); //
			btn_workflow_export.setToolTipText("逻辑位置:WorkflowDealBtnPanel.onExportReport()"); //
		}
		v_btns.add(btn_workflow_insert); // 工作流新增.
		v_btns.add(btn_workflow_update); // 工作流修改
		v_btns.add(btn_workflow_receive); // 工作流接收..
		v_btns.add(btn_workflow_process); // 工作流处理..
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
		if (e.getSource() == btn_workflow_process) { //以后的机制是:在列表主界面中只有处理按钮,而没有提交,退回等按钮,提交按钮是在弹出界面中出现! 
			//但如果是卡片中呢? 或者说干脆就不存在卡片的情况,因为光卡片是不够的,因为还有历史处理记录要显示!
			onProcess(); //处理!!!
		} else if (e.getSource() == btn_workflow_submit) {
			onSubmit();
		} else if (e.getSource() == btn_workflow_receive) {
			onReceive(); // 接收
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
			//导出本页按钮 【杨科/2013-06-05】
			if (MessageBox.confirm(this, "导出所有数据会严重影响服务器运行,故只能导出本页数据(本页数据量可调整),您确定要导出本页数据吗?")) {
				onExportReport_All();
			}
		} else if (e.getSource() == btn_workflow_view) {
			onViewWorkFlow(); //浏览,即只可看卡片与历史意见!!
		} else if (e.getSource() == btn_workflow_insert) {
			onInsert(); //
		} else if (e.getSource() == btn_workflow_update) {
			onUpdate(); //
		} else if (e.getSource() == btn_workflow_cdb) {
			onCDB();
		} else if (e.getSource() == btn_workflow_passread) {
			onPassRead(); //工作流传阅 【杨科/2012-11-28】
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

	// 设置新建按钮是否显示
	public void setInsertBtnVisiable(boolean _visiable) {
		btn_workflow_insert.setVisible(_visiable); //
	}

	// 设置修改按钮是否显示
	public void setUpdateBtnVisiable(boolean _visiable) {
		btn_workflow_update.setVisible(_visiable); //
	}

	// 设置删除按钮是否显示
	public void setDeleteBtnVisiable(boolean _visiable) {
		btn_workflow_delete.setVisible(_visiable); //
	}

	//工作流按钮
	// 设置处理按钮是否显示
	public void setProcessBtnVisiable(boolean _visiable) {
		btn_workflow_process.setVisible(_visiable); //
	}

	// 设置提交按钮是否显示
	public void setSubmitBtnVisiable(boolean _visiable) {
		btn_workflow_submit.setVisible(_visiable); //
	}

	// 设置接收按钮是否显示
	public void setReceiveBtnVisiable(boolean _visiable) {
		btn_workflow_receive.setVisible(_visiable); //
	}

	// 设置退回按钮是否显示
	public void setRejectBtnVisiable(boolean _visiable) {
		btn_workflow_reject.setVisible(_visiable); //
	}

	// 设置拒绝按钮是否显示
	public void setBackBtnVisiable(boolean _visiable) {
		btn_workflow_back.setVisible(_visiable); //
	}

	// 设置撤回按钮是否显示
	public void setCancelBtnVisiable(boolean _visiable) {
		btn_workflow_cancel.setVisible(_visiable); //
	}

	// 设置暂停按钮是否显示
	public void setHoldBtnVisiable(boolean _visiable) {
		btn_workflow_hold.setVisible(_visiable); //
	}

	// 设置继续按钮是否显示
	public void setRestartBtnVisiable(boolean _visiable) {
		btn_workflow_restart.setVisible(_visiable); //
	}

	// 设置流程监控按钮是否显示
	public void setMonitorBtnVisiable(boolean _visiable) {
		btn_workflow_monitor.setVisible(_visiable); //
	}

	//
	public void setViewWFBtnVisiable(boolean _visiable) {
		btn_workflow_view.setVisible(_visiable); //
	}

	//设置导出是否显示!!!
	public void setExportBtnVisiable(boolean _visiable) {
		btn_workflow_export.setVisible(_visiable); //
	}

	//设置导出全部是否显示!!!
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

	// 设置新建按钮文字
	public void setInsertBtnText(String _text) {
		btn_workflow_insert.setText(_text); //
	}

	// 设置删除按钮文字
	public void setDeleteBtnText(String _text) {
		btn_workflow_delete.setText(_text); //
	}

	// 设置修改按钮文字
	public void setUpdateBtnText(String _text) {
		btn_workflow_update.setText(_text); //
	}

	//工作流按钮	
	// 设置处理按钮文字
	public void setProcessBtnText(String _text) {
		btn_workflow_process.setText(_text); //
	}

	// 设置提交按钮文字	
	public void setSubmitBtnText(String _text) {
		btn_workflow_submit.setText(_text); //
	}

	// 设置接收按钮文字
	public void setReceiveBtnText(String _text) {
		btn_workflow_receive.setText(_text); //
	}

	// 设置退回按钮文字
	public void setRejectBtnText(String _text) {
		btn_workflow_reject.setText(_text); //
	}

	// 设置拒绝按钮文字
	public void setBackBtnText(String _text) {
		btn_workflow_back.setText(_text); //
	}

	// 设置撤回按钮文字
	public void setCancelBtnText(String _text) {
		btn_workflow_cancel.setText(_text); //
	}

	// 设置暂停按钮文字
	public void setHoldBtnText(String _text) {
		btn_workflow_hold.setText(_text); //
	}

	// 设置继续按钮文字
	public void setRestartBtnText(String _text) {
		btn_workflow_restart.setText(_text); //
	}

	// 设置流程监控按钮文字
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

	//取得流程实例id
	private String getTaskId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //如果没有定义hvo,即以前的机制!!则直接取
			return null; //
		} else {
			return hvo.getStringValue("task_taskdealid"); //从行号中取
		}
	}

	//取得流程实例id
	private String getTaskOffId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //如果没有定义hvo,即以前的机制!!则直接取
			return null; //
		} else {
			return hvo.getStringValue("task_taskoffid"); //从行号中取
		}
	}

	//取得流程实例id
	private String getPrDealPoolId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //如果没有定义hvo,即以前的机制!!则直接取
			return null; //
		} else {
			return hvo.getStringValue("task_prdealpoolid"); //从行号中取
		}
	}

	//取得流程实例id
	private String getPrinstanceId(BillVO _billVO) {
		if (_billVO == null) {
			return null;
		}
		RowNumberItemVO rowNumVO = (RowNumberItemVO) _billVO.getObject(-1); //
		HashVO hvo = rowNumVO.getRecordHVO(); //
		if (hvo == null) { //如果没有定义hvo,即以前的机制!!则直接取
			return _billVO.getStringValue("WFPRINSTANCEID"); //直接从BillVO中取
		} else {
			return hvo.getStringValue("task_prinstanceid"); //从行号中取
		}
	}

	/**
	 * 工作流修改
	 */
	private void onUpdate() {
		if (this.bindBillListPanel.getSelectedBillVO() == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		WorkFlowUpdateDilaog dialog = new WorkFlowUpdateDilaog(this, "流程处理", this.bindBillListPanel.getTempletVO().getTempletcode(), this.bindBillListPanel, this.bindBillListPanel.getSelectedBillVO().getStringValue("id")); //
		dialog.setVisible(true); //
	}

	/**
	 * 催督办
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
				MessageBox.show(bindBillListPanel, "发生未知异常!");
			}
		}
	}

	/**
	 * 工作流传阅 【杨科/2012-11-28】
	 */
	private void onPassRead() {
		BillVO billVO = getWorkFlowDealBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		String str_pkValue = billVO.getPkValue();
		if (str_pkValue == null || str_pkValue.trim().equals("")) {
			MessageBox.show(this, "该处理任务关联的业务单据没有查询到数据!\n这是因为系统测试阶段的数据造成的,但流程仍然能够处理!\n系统上线后或清除垃圾数据后该问题应该不会再存在,请知悉!");
		}

		String str_task_id = getTaskId(billVO); //消息任务id
		String str_prDealPool_id = getPrDealPoolId(billVO); //流程任务id
		String str_wfinstanceid = getPrinstanceId(billVO); //流程实例!

		try {
			new PassReadBtn(bindBillListPanel, "工作流传阅消息", str_task_id, str_prDealPool_id, str_wfinstanceid);
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				MessageBox.show(bindBillListPanel, e.getMessage());
			} else {
				MessageBox.show(bindBillListPanel, "发生未知异常!");
			}
		}
	}

	/**
	 * 工作流新建
	 */
	private void onInsert() {
		insertdialog = new WorkFlowInsertDilaog(this, "流程处理", this.bindBillListPanel.getTempletVO().getTempletcode(), this.bindBillListPanel); //
		insertdialog.setVisible(true); //

	}

	public WorkFlowInsertDilaog getWorkFlowInsertDialog() {
		return insertdialog;
	}

	/**
	 * 主面板上的按钮!!!
	 */
	private void onProcess() {
		new WorkFlowDealActionFactory().dealAction("deal", this.bindBillListPanel, getAllDealListeners()); //处理动作!
	}

	private void onYJBD() {
		new WorkFlowDealActionFactory().dealAction("yjbd", this.bindBillListPanel, getAllDealListeners());
	}

	private WorkFlowDealListener[] getAllDealListeners() {
		WorkFlowDealListener[] listeners = (WorkFlowDealListener[]) v_WFDealListener.toArray(new WorkFlowDealListener[0]); //
		return listeners; //
	}

	/**
	 * 增加流程监听!!
	 * @param _listener
	 */
	public void addWorkFlowDealListener(WorkFlowDealListener _listener) {
		v_WFDealListener.add(_listener); //
	}

	//处理流程结束后,需要刷新列表
	private void afterDealWorkFlow(int _dealType, String _prInstanceId, String _prdealPoolId) {
		if (this.bindBillListPanel != null) {
			bindBillListPanel.refreshData(true); //重新查询了一把???
		}
		for (int i = 0; i < v_WFDealListener.size(); i++) {
			WorkFlowDealListener listener = (WorkFlowDealListener) v_WFDealListener.get(i); //
			WorkFlowDealEvent event = new WorkFlowDealEvent(this, _dealType, _prInstanceId, _prdealPoolId); //
			listener.onDealWorkFlow(event); //调用监听者!!!
		}
	}

	public WorkFlowProcessPanel getWorkFlowProcessPanel() {
		return processDialog.getWordFlowProcessPanel();
	}

	// 提交
	private void onSubmit() {
		onWorkFlowDeal("SUBMIT"); //
		// MessageBox.show(this, "提交成功!"); //
	}

	// 接收..
	private void onReceive() {
		BillVO billVO = getWorkFlowDealBillVO(); //
		if (billVO == null) {
			// MessageBox.show(this, "No Record will be deal!",
			// WLTConstants.MESSAGE_WARN); //
			if (new TBUtil().getSysOptionBooleanValue("工作流处理按钮的面板中是否显示接收按钮", true)) {
				MessageBox.show(this, "没有需要处理的数据!"); //
			}
			return; //
		}

		if (!billVO.isHaveKey("WFPRINSTANCEID")) {
			MessageBox.show(this, "该记录没有定义工作流需要的列[wfprinstanceid],请先定义之!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		// 如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
		// 根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
		String str_wfinstance = getPrinstanceId(billVO); // 创建流程实例,并返回流程实例ID!!!
		if (str_wfinstance == null || str_wfinstance.equals("")) { // //如果流程实例字段的值为空,则说明是要启动流程!
			MessageBox.show(this, "没有启动流程,请先启动流程!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		try {
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
			service.receiveDealTask(str_wfinstance, ClientEnvironment.getInstance().getLoginUserID()); // 接收当前任务
			// btn_workflow_receive.setEnabled(false); //
			if (new TBUtil().getSysOptionBooleanValue("工作流处理按钮的面板中是否显示接收按钮", true)) {
				MessageBox.show(this, "接收任务成功!");
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	// 拒绝
	private void onReject() {
		onWorkFlowDeal("REJECT"); // 拒绝操作!!
	}

	/**
	 * 工作流处理之回退操作 即站在下游,将该任务打回上游
	 * 
	 * @throws Exception
	 */
	protected void onBack() {
		onWorkFlowDeal("BACK"); // 回退操作
	}

	/**
	 * 取得数据对象
	 * 
	 * @return
	 */
	private BillVO getWorkFlowDealBillVO() {
		if (this.bindBillListPanel != null) {
			return bindBillListPanel.getSelectedBillVO(false, true); //需要取出引用子表中的数据!
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
	 * 工作流提交...
	 * 
	 * @throws Exception
	 */
	private void onWorkFlowDeal(String _dealtype) {
		try {
			BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				// MessageBox.show(this, "No Record will be deal!",
				// WLTConstants.MESSAGE_WARN); //
				MessageBox.show(this, "没有需要处理的数据!"); //
				return; //
			}

			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "该记录没有定义工作流需要的列[wfprinstanceid],请先定义之!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			// 如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
			// 根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
			String str_wfinstance = getPrinstanceId(billVO); // 创建流程实例,并返回流程实例ID!!!
			if (str_wfinstance == null || str_wfinstance.equals("")) { // //如果流程实例字段的值为空,则说明是要启动流程!!
				str_wfinstance = new WorkflowUIUtil().startWorkFlow(this, billVO); // 先创建一个流程,并返回流程实例主键!!!
				if (str_wfinstance != null) {
					// 回写页面,即往BillListPanel或BillCardPanel中回写主键..
					writeBackBillPanel(str_wfinstance); //
					processDeal(str_wfinstance, billVO, _dealtype); // 再立即执行该流程!!!
				}
			} else { // 流程进行
				processDeal(str_wfinstance, billVO, _dealtype); // 处理流程中的
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
	 * 直接处理流程中的数据
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
		WFParVO firstTaskVO = service.getFirstTaskVO(_prinstanceId, str_prdealPoolId, str_loginuserid, str_loginuserDeptID, _billVO, _dealtype); // 取得任务!
		if (firstTaskVO == null) {
			MessageBox.show(this, "你没有该流程的任务!");
			return; //
		}

		WFParVO secondCallVO = null; // 第二次再次请求的参数VO,其实就是第一次取得的参数再次提交服务器!!
		if ((firstTaskVO.isIsprocessed() && firstTaskVO.isIsassignapprover()) || firstTaskVO.isIsneedmsg()) // 如果是终结者并且需要人工选择参与者,或者需要输入批语,则弹出对话框,否则直接提交
		{
			WorkFlowDealDialog dialog = new WorkFlowDealDialog(this, _billVO, firstTaskVO, _dealtype); ////
			dialog.setVisible(true); //
			if (dialog.getClosetype() == 1) { // 如果点击了确定返回
				secondCallVO = dialog.getReturnVO(); //
			} else if (dialog.getClosetype() == 2) {

				// UIUtil.executeUpdateByDS(null,"delete from pub_wf_prinstance
				// where billpkvalue='"+_billVO.getStringValue("id")+"'");

			}
		} else { // 直接再次提交
			secondCallVO = firstTaskVO; //
		}

		if (secondCallVO != null) {
			BillVO returnBillVO = service.secondCall(secondCallVO, str_loginuserid, _billVO, _dealtype); // 第二次再次请求!!
			refreshWorkFlowPanel(_prinstanceId); // 刷新流程面板!!
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

		hiddenAllBtns(); // 先隐藏所有按钮!!!

		if (_prinstanceid == null || _prinstanceid.equals("")) { // 如果没有流程实例
			btn_workflow_submit.setVisible(true); // 先默认设置为隐藏
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
				String str_userid = ClientEnvironment.getInstance().getLoginUserID(); // 先取得登录人员的主键!!
				String str_sql_2 = "select * from v_pub_wf_dealpool_1 where prinstanceid=" + _prinstanceid + " and participant_user=" + str_userid + " and issubmit='N' and isprocess='N'"; //

				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, str_sql_2); //
				if (hvs != null && hvs.length > 0) {
					for (int i = 0; i < hvs.length; i++) {
						if (hvs[i].getIntegerValue("submitcount").intValue() > 0) {
							btn_workflow_submit.setVisible(true); //
						}

						if (hvs[i].getIntegerValue("rejectcount").intValue() > 0) { // 如果有拒绝的的线，则显示拒绝的按钮
							btn_workflow_reject.setVisible(true); //
						}

						if (hvs[i].getStringValue("iscanback").equals("Y")) { // 如果允许回退，则显示回退按钮
							btn_workflow_back.setVisible(true); //
						}

						if (hvs[i].getStringValue("curractivitytype").equals("END")) {
							btn_workflow_submit.setVisible(true); //
						}
					}
				}

				btn_workflow_hold.setVisible(true); // 先默认设置为隐藏
				btn_workflow_monitor.setVisible(true); // 监控按钮永远显示!!!
				btn_workflow_cancel.setVisible(true); //
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 监控
	private void onMonitor() {
		BillVO billVO = getWorkFlowDealBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "没有对应的处理数据!"); //
			return; //
		}

		if (!billVO.isHaveKey("WFPRINSTANCEID")) {
			MessageBox.show(this, "没有BillType与Busitype,WFPRINSTANCEID信息,不能进行流程处理!", WLTConstants.MESSAGE_WARN); //
			return; //
		}

		// 如果有单据类型与业务类型,但流程实例为空,则立即创建流程实例!!!!
		// 根据流程实例找出待处理池中是否有属于我的待处理任务,如果有,则弹出一个框中间有一个"同意/拒绝",有一个大文本框,有一个提交按钮,当然旁边还要有一个监控流程的按钮!!!
		String str_wfinstance = getWFPrinstanceID(billVO); // 流程实例ID..
		if (str_wfinstance == null || str_wfinstance.equals("")) { // 启动流程!如果流程实例为空
			MessageBox.show(this, "该记录还没有启动流程流程,请先提交启动流程!", WLTConstants.MESSAGE_WARN); //
			return;
		}

		WorkflowMonitorDialog dialog = new WorkflowMonitorDialog(this, str_wfinstance, billVO); //处理加密!!
		dialog.setMaxWindowMenuBar();
		dialog.setVisible(true); //
	}

	/**
	 * 浏览工作流数据
	 */
	private void onViewWorkFlow() {
		BillVO billVO = getWorkFlowDealBillVO(); //
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return; //
		}
		String str_pkValue = billVO.getPkValue(); //
		if (str_pkValue == null || str_pkValue.trim().equals("")) {
			MessageBox.show(this, "该处理任务关联的业务单据没有查询到数据!\n这是因为系统测试阶段的数据造成的,但流程仍然能够处理!\n系统上线后或清除垃圾数据后该问题应该不会再存在,请知悉!"); //
		}
		String str_task_id = getTaskId(billVO); //消息任务id
		String str_prDealPool_id = getPrDealPoolId(billVO); //流程任务id
		String str_wfinstanceid = getPrinstanceId(billVO); //流程实例!
		BillCardPanel cardPanel = new BillCardPanel(this.bindBillListPanel.getTempletVO()); //创建一个卡片面板!!
		cardPanel.setBillVO(billVO.deepClone()); //在卡片中设置数据,需要克隆一把!!
		String str_title = "浏览[" + str_task_id + "][" + str_prDealPool_id + "][" + str_wfinstanceid + "]"; //
		WorkFlowProcessFrame processFrame = new WorkFlowProcessFrame(this, str_title, cardPanel, this.bindBillListPanel, str_task_id, str_prDealPool_id, str_wfinstanceid, true, "现在是浏览数据状态,所以不能进行提交等操作。"); //
		processFrame.getWfProcessPanel().getBtn_Confirm().setVisible(false); //隐藏确认按钮
		processFrame.setVisible(true); //
		processFrame.toFront(); //
	}

	//导出本页功能 【杨科/2013-06-05】
	private void onExportReport_All() {
		BillVO[] billVOs = bindBillListPanel.getBillVOs();
		if (billVOs.length == 0) {
			MessageBox.show(this, "可导出数据为0!");
			return;
		}

		try {
			JFileChooser chooser = new JFileChooser("C:\\");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setApproveButtonText("选择");
			int li_result = chooser.showOpenDialog(this);
			if (li_result == JFileChooser.APPROVE_OPTION) {
				filePath = chooser.getSelectedFile().getAbsolutePath();
				File f = new File(filePath);
				if (!f.exists()) {
					MessageBox.show(this, "路径:" + filePath + " 不存在!");
					return;
				}

				new SplashWindow(this, "该操作耗时较长,请耐心等待。。。", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						doExportReport_All((SplashWindow) e.getSource(), bindBillListPanel.getBillVOs(), filePath);
					}
				}, false);
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}
	}

	//导出本页
	private void doExportReport_All(SplashWindow sw, BillVO[] billVOs, String filePath) {
		try {
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class);

			if (billVOs.length > 30) {
				sw.setWaitInfo("正在导出1-30条数据,共" + billVOs.length + "条数据。。。");
			} else {
				sw.setWaitInfo("正在导出1-" + billVOs.length + "条数据,共" + billVOs.length + "条数据。。。");
			}

			for (int i = 0; i < billVOs.length; i++) {
				File file = new File(filePath + "\\" + billVOs[i].getTempletName() + "_导出本页数据\\" + (i + 1) + "、" + StringFilter(billVOs[i].toString()) + ".html");
				File parent = file.getParentFile();
				if (parent == null || !parent.exists()) {
					parent.mkdirs();
				}

				FileOutputStream fout = new FileOutputStream(file, false);
				fout.write(getHtml(billVOs[i], service).getBytes("GBK"));
				fout.close();

				if (i > 0 && (i + 1) % 30 == 0) {
					Thread.sleep(60000); //每30条停顿1分钟,给服务器垃圾回收时间,保证服务器不崩溃 这块代码其实应该加同步锁
					int end = i + 31;
					if (end > billVOs.length) {
						end = billVOs.length;
					}
					sw.setWaitInfo("正在导出" + (i + 2) + "-" + end + "条数据,共" + billVOs.length + "条数据。。。");
				}
			}
			MessageBox.show(this, "导出本页成功!");
		} catch (Exception e) {
			MessageBox.show(this, "导出本页失败!");
		}
	}

	//文件内容 【杨科/2013-06-05】
	private String getHtml(BillVO billVO, WorkFlowServiceIfc service) {
		BillCardPanel cardPanel = new BillCardPanel(this.bindBillListPanel.getTempletVO()); //创建一个卡片面板
		cardPanel.setBillVO(billVO);
		cardPanel.setGroupExpandable("其他信息", false);
		cardPanel.setGroupExpandable("*法规部填写*", true);
		cardPanel.removeAll();

		String str_html = "";
		try {
			String str_wfinstanceid = getPrinstanceId(billVO); //流程实例
			HashVO[] hvs = service.getMonitorTransitions(str_wfinstanceid, false, (ClientEnvironment.isAdmin() ? false : true)); //可以处理加密信息

			WFHistListPanelBuilder histListBuilder = new WFHistListPanelBuilder(hvs, billVO, true);
			BillListPanel billList_hist = histListBuilder.getBillListPanel(); //创建面板
			billList_hist.setBillListOpaque(false); //透明的
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

		List allcolumnWidth = new ArrayList(); //所有宽度
		double totle_width = 0;
		double msgwidth = 0;
		for (int i = 0; i < li_col_count; i++) {
			String columnName = table.getColumnName(i);
			String width = table.getColumn(columnName).getWidth() + "";
			if ("处理意见".equals(_billList_hist.getColumnName(i))) {
				msgwidth = Integer.parseInt(width);
			}
			totle_width += Integer.parseInt(width);
		}

		for (int i = 0; i < li_col_count; i++) {
			String columnName = table.getColumnName(i);
			double width = 0;
			if ("处理意见".equals(_billList_hist.getColumnName(i))) {
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
					//要根据cell内容的类型决定生成的html内容
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
			//给html页面的正文文件附加超链接
			String str_file = _billVO.getStringValue(_itemkey); //
			strbf_url.append(System.getProperty("CALLURL") + "/DownLoadFileServlet?pathtype=" + "office" + "&filename=" + str_file); //以前是用Office控件打开的,但后来兴业客户发现竟然office控件都能保存,所以干脆搞成了直接下载的!
			strbf_html.append("<a target=\"_blank\" href=\"").append(strbf_url.toString()).append("\" >").append(_objectValueText).append("</a>");
		} else if (_itemtype.equals(WLTConstants.COMP_FILECHOOSE)) {
			//给html页面的附件文件增加“下载”的超链接
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

	//文件名过滤 【杨科/2013-06-05】
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
				MessageBox.show(this, "没有对应的处理数据!"); //
				return; //
			}
			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "没有BillType与Busitype,WFPRINSTANCEID信息,不能进行流程处理!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			final String str_wfinstance = getWFPrinstanceID(billVO); // 流程实例ID..
			if (str_wfinstance == null || str_wfinstance.equals("")) { // 启动流程!如果流程实例为空
				MessageBox.show(this, "该记录还没有启动流程流程,请先提交启动流程!", WLTConstants.MESSAGE_WARN); //
				return;
			}
			//意见导出自定义类 【杨科/2013-01-18】
			String str_class = TBUtil.getTBUtil().getSysOptionStringValue("工作流导出项目自定义类", "cn.com.infostrategy.bs.workflow.WorkflowHtmlReportDMO"); //
			String str_isForceUseDefaultExport = TBUtil.getTBUtil().getSysOptionStringValue("工作流导出是否使用默认逻辑", "Y"); //
			if ("Y".equals(str_isForceUseDefaultExport)) { //如果是强行使用默认逻辑!!!
				String str_msg = "您真的想导出流程意见么?\r\n"; //
				if (ClientEnvironment.isAdmin()) {
					str_msg = str_msg + "因为指定了系统参数[工作流导出是否使用默认逻辑]=[Y],所以使用默认导出逻辑!(此信息只对Admin=Y有效)"; // 
				}
				//				if (!MessageBox.confirm(this, str_msg)) {
				//					return;
				//				}
				String str_wfExportFormula = getWorkFlowDealBillTempletVO().getWfcustexport(); //"【法律意见书◆表单项=aa,bb,cc;流程环节=风险合规部门,行领导;角色=部门负责人,处室负责人,行领导】"; //
				if (str_wfExportFormula == null || str_wfExportFormula.trim().equals("")) {
					HashMap map_par = new HashMap(); //
					map_par.put("showType", "1"); //
					map_par.put("prinstanceid", str_wfinstance); //
					map_par.put("billvo", billVO); //
					map_par.put("roles", ClientEnvironment.getInstance().getLoginUserRoleIds());
					map_par.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
					UIUtil.loadHtml(str_class, map_par); //
				} else {
					String[] str_forItems = TBUtil.getTBUtil().split(str_wfExportFormula, "】"); //
					for (int i = 0; i < str_forItems.length; i++) {
						str_forItems[i] = TBUtil.getTBUtil().replaceAll(str_forItems[i], "【", ""); //
						str_forItems[i] = TBUtil.getTBUtil().replaceAll(str_forItems[i], "】", ""); //
					}

					HashVO[] hvs_mycorp = UIUtil.getParentCorpVOByMacro(1, null, "$本机构"); //
					String str_myCorpName = null; //
					String str_myCorpType = null; //
					if (hvs_mycorp.length > 0) {
						str_myCorpName = hvs_mycorp[0].getStringValue("name"); //
						str_myCorpType = hvs_mycorp[0].getStringValue("corptype"); //
					}

					ArrayList al_list = new ArrayList(); //
					for (int i = 0; i < str_forItems.length; i++) {
						String str_pp = str_forItems[i]; //
						String str_name = str_pp.substring(0, str_pp.indexOf("◆")); //
						String str_fornula = str_pp.substring(str_pp.indexOf("◆") + 1, str_pp.length()); //
						if (str_name.indexOf("@") > 0) {
							String str_ct = str_name.substring(str_name.indexOf("@") + 1, str_name.length()); //
							if (str_myCorpType != null && str_myCorpType.indexOf(str_ct) >= 0) { //我是二级分行部门,条件是二级分行
								al_list.add(new String[] { str_name.substring(0, str_name.indexOf("@")), str_fornula }); //
								//System.out.println("满足条件[" + str_myCorpType + "][" + str_ct + "],加入"); //
							} else {
								//System.out.println("不满足条件[" + str_myCorpType + "][" + str_ct + "],不加入"); //
							}
						} else {
							al_list.add(new String[] { str_name, str_fornula }); //
							//System.out.println("没条件,直接加入"); //
						}
					}
					String[][] str_ops = new String[al_list.size() + 1][2];
					str_ops[0] = new String[] { "流程意见单", null }; //
					for (int i = 0; i < al_list.size(); i++) {
						str_ops[i + 1] = (String[]) al_list.get(i); //
					}

					String[] str_btns = new String[str_ops.length]; //
					for (int i = 0; i < str_btns.length; i++) {
						str_btns[i] = str_ops[i][0]; //
					}

					int li_rt = MessageBox.showOptionDialog(this, "请选择导出类型", "提示", str_btns, 500, 150); //
					if (li_rt == 0) { //流程意见单
						HashMap map_par = new HashMap(); //
						map_par.put("showType", "1"); //
						map_par.put("prinstanceid", str_wfinstance); //
						map_par.put("billvo", billVO); //
						map_par.put("标题", str_ops[0][0]); //是否自定义条件
						map_par.put("roles", ClientEnvironment.getInstance().getLoginUserRoleIds());
						map_par.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
						UIUtil.loadHtml(str_class, map_par); //
					} else { //法律意见书
						if (li_rt > 0) {
							HashMap map_par = new HashMap(); //
							map_par.put("showType", "1"); //
							map_par.put("prinstanceid", str_wfinstance); //
							map_par.put("billvo", billVO); //
							map_par.put("是否自定义条件", "Y"); //是否自定义条件
							map_par.put("标题", str_ops[li_rt][0]); //是否自定义条件
							map_par.put("条件公式", str_ops[li_rt][1]); //
							map_par.put("roles", ClientEnvironment.getInstance().getLoginUserRoleIds());
							map_par.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
							UIUtil.loadHtml(str_class, map_par); //
						}
					}
				}
			} else {
				VectorMap vm = new WorkflowUIUtil().getAllReport(billVO.getStringValue("billtype"), billVO.getStringValue("busitype"), billVO.getSaveTableName());
				if (vm != null && vm.size() > 0) { //
					if (vm.size() == 1) { //如果只有一个配置,则直接弹出!
						String str_msg = "您真的想导出流程意见么?\r\n"; //
						if (ClientEnvironment.isAdmin()) {
							str_msg = str_msg + "这将根据单据类型[" + billVO.getStringValue("billtype") + "],业务类型[" + billVO.getStringValue("busitype") + "]在流程分配中查找对应的报表名称!(此信息只对Admin=Y有效)"; // 
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
							itemname = (String) param.get("报表名称");
							JMenuItem menuItem = new JMenuItem(itemname);
							menuItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									if ("N".equals((String) param.get("是否允许"))) {
										MessageBox.showInfo(btn_workflow_export, "抱歉您没有使用此报表的权限!");
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
					MessageBox.showInfo(this, "没有定义报表请与管理员联系");
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
				return hvo.getStringValue("task_prinstanceid"); //如果行号中有,则说明是新的机制,则直接返回!!
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
	 * 撤回操作!!! 以前的机构撤回比较麻烦! 但新的机构将
	 */
	private void onCancel() {
		try {
			BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "请选择相应的记录进行处理!"); //
				return; //
			}
			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "没有BillType与Busitype,WFPRINSTANCEID信息,不能进行流程处理!", WLTConstants.MESSAGE_WARN); //
				return; //
			}
			String str_prinstanceId = getPrinstanceId(billVO);
			if (str_prinstanceId == null || str_prinstanceId.trim().equals("")) { // 启动流程!如果流程实例为空
				MessageBox.show(this, "该记录还没有启动流程流程,请先提交启动流程!", WLTConstants.MESSAGE_WARN); //
				return;
			}
			if (JOptionPane.showConfirmDialog(this, "您确定要撤回您提交的该任务吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			String str_dealPoolId = getPrDealPoolId(billVO); //
			String str_taskOffId = getTaskOffId(billVO); //
			getWorkFlowService().cancelTask(str_prinstanceId, str_dealPoolId, str_taskOffId, ClientEnvironment.getCurrLoginUserVO().getId(), null); ////
			afterDealWorkFlow(1, str_prinstanceId, str_dealPoolId); ////
			MessageBox.show(this, "撤回该任务成功!请至待办箱中重新查询出该数据继续提交!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); // 显示异常信息!!!
		}
	}

	/**
	 * 删除流程!
	 */
	private void onDelete() {
		try {
			BillVO billVO = getWorkFlowDealBillVO(); //
			if (billVO == null) {
				// MessageBox.show(this, "请选择相应的记录进行处理!",
				// WLTConstants.MESSAGE_WARN); //
				MessageBox.show(this, "请选择相应的记录进行处理!"); //
				return; //
			}

			if (!billVO.isHaveKey("WFPRINSTANCEID")) {
				MessageBox.show(this, "没有BillType与Busitype,WFPRINSTANCEID信息,不能进行流程处理!", WLTConstants.MESSAGE_WARN); //
				return; //
			}

			String str_wfinstance = getPrinstanceId(billVO); //流程实例ID..
			if (str_wfinstance == null || str_wfinstance.trim().equals("")) { // //启动流程!如果流程实例为空
				if (JOptionPane.showConfirmDialog(this, "您是否真的想删除该记录吗?", "提示", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					String str_sql = "delete from " + billVO.getSaveTableName() + " where " + billVO.getPkName() + "='" + billVO.getPkValue() + "'";
					UIUtil.executeUpdateByDS(null, str_sql); //
					if (this.bindBillListPanel != null) {
						bindBillListPanel.removeSelectedRows(); //
					}
				}
				return; //
			}

			if (JOptionPane.showConfirmDialog(this, "您是否真的想删除该记录吗?", "提示", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				getWorkFlowService().deleteTask(str_wfinstance, ClientEnvironment.getCurrLoginUserVO().getId()); // //调用远程服务取消某一个已提交的任务!!!
				if (this.bindBillListPanel != null) {
					this.bindBillListPanel.removeSelectedRows(); //
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); // 显示异常信息!!!
		}

	}

	// 暂停
	private void onHold() {
	}

	// 重新启动
	private void onRestart() {
	}

	/**
	 * 取得工作流F服务!!!
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
