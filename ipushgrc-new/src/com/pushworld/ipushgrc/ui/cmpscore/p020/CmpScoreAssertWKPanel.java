package com.pushworld.ipushgrc.ui.cmpscore.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 积分复议
 * @author yinliang
 * @since 2011.12.23
 * @msg. 本来想写成三个卡片，每个卡片点击时各自有触发事件，重新按照各自条件重新加载数据
 *       但由于各个卡片的按钮也不一样，每次重新加载感觉不方便，所以还是建三个模板吧
 */
public class CmpScoreAssertWKPanel extends AbstractWorkPanel implements ActionListener, BillListMouseDoubleClickedListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8848564849874424781L;
	WLTTabbedPane tabpane = null; //用页签的方式
	BillListPanel listpanel_query; // 第一个页签，积分复议->带查阅
	BillListPanel listpanel_wait; // 第二个页签，提交复议，流程进行中
	BillListPanel listpanel_result; // 第三个页签，复议结果，提交了复议下来结果，或者不复议直接结束 都到这里来
	WLTButton btn_start, btn_look, btn_end, btn_query, btn_wait, btn_result;

	@Override
	public void initialize() {
		tabpane = new WLTTabbedPane();

		listpanel_query = new BillListPanel("CMP_SCORE_RECORD_CODE1_5_1"); //积分复议待查阅
		listpanel_query.setDataFilterCustCondition(" sendstate = '2' and userid = " + ClientEnvironment.getInstance().getLoginUserID());
		tabpane.addTab("待处理", listpanel_query);

		listpanel_wait = new BillListPanel("CMP_SCORE_RECORD_CODE1_5"); //积分复议流程中
		listpanel_wait.setDataFilterCustCondition(" sendstate = '3' and userid = " + ClientEnvironment.getInstance().getLoginUserID());
		listpanel_wait.addBillListMouseDoubleClickedListener(this);
		tabpane.addTab("审批中", listpanel_wait);

		listpanel_result = new BillListPanel("CMP_SCORE_RECORD_CODE1_5"); //积分复议结果
		listpanel_result.setDataFilterCustCondition(" sendstate = '4' and userid = " + ClientEnvironment.getInstance().getLoginUserID());
		listpanel_result.addBillListMouseDoubleClickedListener(this);
		tabpane.addTab("复议结果", listpanel_result);

		//待处理界面按钮
		btn_start = new WLTButton("申请复议"); // 进行复议
		listpanel_query.addBillListButton(btn_start);
		btn_end = new WLTButton("确认"); // 不进行复议直接结束
		listpanel_query.addBillListButton(btn_end);
		listpanel_query.repaintBillListButton();
		btn_start.addActionListener(this);
		btn_end.addActionListener(this);

		//审批中界面布置
		btn_wait = listpanel_wait.getBillListBtn("comm_listselect");
		btn_wait.addActionListener(this);
		btn_look = new WLTButton("流程监控");
		listpanel_wait.addBillListButton(btn_look);
		listpanel_wait.repaintBillListButton();
		btn_look.addActionListener(this);

		//审批结束界面
		btn_result = listpanel_result.getBillListBtn("comm_listselect");
		btn_result.addActionListener(this);
		listpanel_wait.repaintBillListButton();

		this.add(tabpane);
	}

	// 监听方法
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_start)
			onApply(); //复议申请按钮
		if (obj == btn_end)
			onEnd(); // 不复议直接结束
		if (obj == btn_look)
			onLook(); //查看流程情况
		if (obj == btn_wait)
			onQuery_wait(); // 审批中界面查询按钮
		if (obj == btn_result)
			onQuery_result(); // 审批结束界面查询按钮
	}

	private void onQuery_result() {
		BillCardPanel cardPanel = new BillCardPanel("CMP_SCORE_RECORD_CODE1_5"); //当前卡片panel
		cardPanel.setBillVO(listpanel_result.getSelectedBillVO()); //
		cardPanel.setGroupVisiable("申请截止日期", false);
		BillCardDialog dialog = new BillCardDialog(listpanel_result, "复议结果详情", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true);
	}

	private void onQuery_wait() {
		BillCardPanel cardPanel = new BillCardPanel("CMP_SCORE_RECORD_CODE1_5"); //当前卡片panel
		cardPanel.setBillVO(listpanel_wait.getSelectedBillVO()); //
		cardPanel.setGroupVisiable("申请截止日期", false);
		cardPanel.setGroupVisiable("复议裁定", false);
		BillCardDialog dialog = new BillCardDialog(listpanel_result, "复议申请信息", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true);
	}

	// 不复议直接结束
	private void onEnd() {
		BillVO billvo = listpanel_query.getSelectedBillVO(); //	当前选择数据
		if (billvo == null) {
			MessageBox.showSelectOne(listpanel_query); //
			return; //
		}
		if (MessageBox.confirm(this, "你确定不提交复议流程，直接结束吗？")) { // 如果不复议直接结束
			// 将状态更新为已复议
			String sql_update = "update " + billvo.getSaveTableName() + " set sendstate = '4'," + "resultscore = scorelost where id = '" + billvo.getStringValue("id") + "'";
			try {
				UIUtil.executeUpdateByDS(null, sql_update);

				String currentYear = new SimpleDateFormat("yyyy").format(new Date());

				sql_update = " update " + billvo.getSaveTableName() + " set totalscore = " + "(select sum(resultscore) from  " + billvo.getSaveTableName() + "  where userid = " + billvo.getStringValue("userid") + " and scoredate like '%" + currentYear + "%' )" + " where userid = "
						+ billvo.getStringValue("userid") + "  and scoredate like '%" + currentYear + "%'";

				UIUtil.executeUpdateByDS(null, sql_update);
			} catch (Exception e) {
				e.printStackTrace();
			}
			listpanel_query.refreshData();// 更新当前界面
			listpanel_result.refreshData(); // 刷新结果界面
		}

	}

	private void onApply() {
		BillVO billVO = listpanel_query.getSelectedBillVO(); //	当前选择数据
		if (billVO == null) {
			MessageBox.showSelectOne(listpanel_query); //
			return; //
		}
		if (!billVO.containsKey("wfprinstanceid")) {
			MessageBox.show(listpanel_query, "选中的记录中没有定义工作流字段(wfprinstanceid)!"); //
			return; //
		}
		int flag = listpanel_query.getSelectedRow(); //当前选择行

		BillCardPanel cardPanel = new BillCardPanel(listpanel_query.templetVO); //当前卡片panel
		cardPanel.setBillVO(billVO); //
		// 插入 申请人 和 申请日期数据
		cardPanel.setValueAt("applyuser", new RefItemVO(ClientEnvironment.getInstance().getLoginUserID(), "", ClientEnvironment.getInstance().getLoginUserName()));
		cardPanel.setValueAt("applydate", new RefItemVO(UIUtil.getCurrDate(), "", UIUtil.getCurrDate()));
		BillCardDialog dialog = new BillCardDialog(this, listpanel_query.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);//更新
		dialog.getBtn_save().setVisible(false); //dialog保存按钮不可见
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) { //如果点击了保存,这个地方变成提交复议
			//1.更新数据
			listpanel_query.setBillVOAt(listpanel_query.getSelectedRow(), dialog.getBillVO());
			listpanel_query.setRowStatusAs(listpanel_query.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			//2.进入复议工作流
			String str_wfprinstanceid = billVO.getStringValue("wfprinstanceid"); // 取得此工作流序列ID
			if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {//如果流程未发起，则发起流程，否则监控流程
				onBillListWorkFlowProcess(billVO, flag);
			}
		}
	}

	//查看工作流进行情况
	private void onLook() {
		BillVO billVO = listpanel_wait.getSelectedBillVO(); //	当前选择数据
		if (billVO == null) {
			MessageBox.showSelectOne(listpanel_query); //
			return; //
		}
		//查看工作流进行情况
		String str_wfprinstanceid = billVO.getStringValue("wfprinstanceid"); // 取得此工作流序列ID
		cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(listpanel_wait, str_wfprinstanceid, billVO); //
		wfMonitorDialog.setMaxWindowMenuBar();
		wfMonitorDialog.setVisible(true); //
	}

	//发起工作流
	private void onBillListWorkFlowProcess(BillVO billvo, int flag) {
		try {
			new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", listpanel_query, null); //处理动作!
			//发起工作流的同时，将工作流状态改为审批中
			listpanel_query.setSelectedRow(flag);
			if (listpanel_query.getSelectedBillVO().getStringValue("wfprinstanceid") == null) //如果流程并未发起
				return;
			if (listpanel_query.getSelectedBillVO().getStringValue("wfprinstanceid").equals(""))
				return;
			else {
				String sql_update = "update " + billvo.getSaveTableName() + " set sendstate = '3' where id = '" + billvo.getStringValue("id") + "'";
				UIUtil.executeUpdateByDS(null, sql_update);
				listpanel_query.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	//列表双击事件
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent event) {
		if (event.getBillListPanel() == listpanel_wait)
			onQuery_wait();
		if (event.getBillListPanel() == listpanel_result)
			onQuery_result();
	}

}
