package cn.com.pushworld.salary.ui.target.p040;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.bs.common.SystemOptions;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.DivComponentLayoutUtil;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.DivComponentLayoutUtil.DivComponentCustomResetRectListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextField;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessDialog;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomClickEvent;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomClickListener;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomPanel;

/**
 * 部门绩效评分界面
 * 
 * @author haoming create by 2013-6-28
 */
public class DeptTargetScoredWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 5488377030880962619L;
	private BillListPanel targetScoreListPanel = new BillListPanel("SAL_DEPT_CHECK_SCORE_CODE1"); // 指标评分详情。
	private WLTButton btn_submit, btn_apply_modify,btn_save; // 申请修改
	private SalaryTBUtil stbutil = new SalaryTBUtil();
	private HashVO currLogID = null; // 当前计划日志ID。
	private int currPageState = 0; // 当前页面状态.0直接评分状态 1申请修改表单选择 2具体申请修改维护。

	public static final String SCORE_STATUS_INIT = "待提交";
	public static final String SCORE_STATUS_COMMIT = "已提交";
	public static final String SCORE_STATUS_MODIFY = "申请修改";
	private boolean editable = true;
	private boolean autoCheckCommit = true;
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // 当前登录人ID
	private Boolean falg= SystemOptions.getBooleanValue("定性打分是否启用加权平均",false);//zzl 20201221 有的支行不喜欢加权平均

	public void initialize() {
		try {
			initBtn();
			this.add(getCheckScorePanel());
			resetButton();
			checkNeedPopTip();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initBtn() {
		btn_submit = new WLTButton("全部提交", UIUtil.getImage("zt_071.gif"));
		btn_save = new WLTButton("保存",UIUtil.getImage("zt_041.gif"));
		btn_submit.setPreferredSize(new Dimension(85, 21));
		btn_submit.addActionListener(this);
		btn_apply_modify = new WLTButton("申请修改", UIUtil.getImage("script--pencil.png"));
		btn_apply_modify.addActionListener(this);
		btn_save.addActionListener(this);
		targetScoreListPanel.addBatchBillListButton(new WLTButton[] { btn_submit, btn_apply_modify,btn_save});
		targetScoreListPanel.repaintBillListButton();
		targetScoreListPanel.setHeaderCanSort(false);
		targetScoreListPanel.setCanShowCardInfo(false);
		targetScoreListPanel.putClientProperty("reedit", "Y");
		targetScoreListPanel.getTable().setFont(new Font("黑体", Font.BOLD, 14));
	}

	/*
	 * 自定义初始化。从其他面板调用。
	 */
	public void customInit(String _userid, boolean _editable) {
		initBtn();
		userid = _userid;
		editable = _editable;
		targetScoreListPanel.putClientProperty("this", this);
		targetScoreListPanel.putClientProperty("editable", _editable);
		this.add(targetScoreListPanel);
	}

	/*
	 * 显示提示信息,强行设置显示。
	 */
	public void showHelpInfo() {
		new Timer().schedule(new TimerTask() {
			public void run() {
				while (true) {
					if (targetScoreListPanel.isShowing()) {
						help();
						break;
					}
					try {
						Thread.currentThread().sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, 0);
	}

	/*
	 *  检测是否需要提示输入位置。绿色框，标识。
	 */
	public void checkNeedPopTip() {
		BillVO[] allVO = targetScoreListPanel.getAllBillVOs();
		boolean haveSeleced = false; //是否已经填写过.
		for (int i = 0; i < allVO.length; i++) {
			String onescorevalue = allVO[i].getStringValue("checkdratio");
			if (onescorevalue != null && !"".equalsIgnoreCase(onescorevalue)) { // 如果全部没有填写。绿色标记
				haveSeleced = true;
				break;
			}
		}
		if (!haveSeleced) {
			showHelpInfo(); //标识提示填写范围。
		}
	}

	/*
	 * 标识哪儿就行选择，填写。
	 */
	private void help() {
		final MYAlphaPanel panel = new MYAlphaPanel();
		panel.setOpaque(false);
		Rectangle cell = targetScoreListPanel.getTable().getCellRect(0, 4, true);
		Rectangle rect = new Rectangle(cell.x, 0, cell.width, targetScoreListPanel.getTable().getVisibleRect().height);
		DivComponentLayoutUtil.getInstanse().addComponentOnDiv(targetScoreListPanel.getTable(), panel, rect, new DivComponentCustomResetRectListener() {
			public void resetComponentDivLocation(JComponent divComponent) {
				Point thisPoint = targetScoreListPanel.getTable().getLocationOnScreen(); //获取当前页面的位置
				SwingUtilities.convertPointFromScreen(thisPoint, targetScoreListPanel.getTable().getRootPane().getLayeredPane()); //取到当前面板相对layeredpane的位置.
				int tableHeaderHeight = targetScoreListPanel.getTable().getTableHeader().getHeight();
				Point headerPoint = targetScoreListPanel.getTable().getTableHeader().getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(headerPoint, targetScoreListPanel.getTable().getRootPane().getLayeredPane()); //取到当前面板相对layeredpane的位置.
				Rectangle cell = targetScoreListPanel.getTable().getCellRect(0, targetScoreListPanel.findColumnIndex("checkdratio"), true);
				Rectangle rect = new Rectangle(thisPoint.x + cell.x, headerPoint.y + tableHeaderHeight, cell.width, targetScoreListPanel.getTable().getVisibleRect().height);
				divComponent.setBounds(rect);
			}
		}, -1);
		final javax.swing.Timer timer = new javax.swing.Timer(20, new ActionListener() { //用一个
					int index = 0;

					public void actionPerformed(ActionEvent actionevent) {
						panel.setAlpha((float) Math.abs(Math.sin(Math.toRadians(index))));
						panel.repaint();
						if (index >= 540) {
							javax.swing.Timer tim = (javax.swing.Timer) actionevent.getSource();
							DivComponentLayoutUtil.getInstanse().removeComponentOnDiv(targetScoreListPanel.getTable(), panel);
							panel.setVisible(false);
							tim.stop();
						}
						index += 2;
					}
				});
		timer.start();
	}

	public void refreshListDataByLogVO(HashVO _logVO) {
		currLogID = _logVO;
		if (currLogID != null) {
			targetScoreListPanel.QueryDataByCondition(" logid='" + currLogID.getStringValue("id") + "' and targettype='部门定性指标'  and  scoreuser='" + userid + "'");
		}
		targetScoreListPanel.setTitleLabelText(currLogID.getStringValue("name") + "   " + targetScoreListPanel.getTempletVO().getTempletname());
		currPageState = 0;
		resetButton();
		checkNeedPopTip();
	}

	private JPanel getCheckScorePanel() {
		try {
			HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select t1.id,t1.checkdate,t1.status from SAL_TARGET_CHECK_LOG t1,sal_dept_check_score t2 where t1.id = t2.logid and t1.status!='考核结束' and t2.scoreuser='" + userid + "' and targettype='部门定性指标'  group by t1.id");
			if (vos == null || vos.length == 0) {
				JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
				WLTLabel label = new WLTLabel("目前全行没有考评中的计划...");
				label.setFont(new Font("宋体", Font.BOLD, 16));
				panel.add(label);
				return panel;
			}
			if (vos.length > 1) {
				return getPlanBomPanel(vos);
			} else {
				currLogID = vos[0];
			}
			if (currLogID != null) {
				targetScoreListPanel.QueryDataByCondition(" logid='" + currLogID.getStringValue("id") + "' and scoreuser='" + ClientEnvironment.getCurrSessionVO().getLoginUserId() + "' and targettype='部门定性指标'");
			}
			targetScoreListPanel.setTitleLabelText(currLogID.getStringValue("name") + "   " + targetScoreListPanel.getTempletVO().getTempletname());
			return targetScoreListPanel;
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	public void actionPerformed(ActionEvent e) {
		targetScoreListPanel.stopEditing(); // 只要是按钮，就停止当前编辑
		if (e.getSource() == btn_submit) {
			onBtn1();
		} else if (e.getSource() == btn_apply_modify) {
			onBtn2();
		}else if(e.getSource() == btn_save){
			onBtn3();
		}
	}

	// 第一个按钮的实际逻辑。此按钮起业务决定作用。主要用来做提交，确定动作。
	private void onBtn1() {
		switch (currPageState) {
		case 0:
			onSubmit(); // 提交所有考评记录
			break;
		case 1:
			goToModify();// 前往修改界面。
			break;
		case 2:
			onSubmitModifyByProcess();
			break;
		}
	}

	// 根据当前状态重置按钮
	private void resetButton() {
		BillVO vos[] = targetScoreListPanel.getAllBillVOs();
		boolean commited = false; // 是否已经提交过。
		boolean updatestate = false; //是否是更新
		if (vos.length > 0 && !SCORE_STATUS_INIT.equals(vos[0].getStringValue("status"))) {
			for (int i = 0; i < vos.length; i++) {
				String oneScoreStatus = vos[i].getStringValue("status");
				if (oneScoreStatus == null || oneScoreStatus.equals(SCORE_STATUS_INIT)) { // 如果有一条记录是初始记录
					commited = false;
					updatestate = false;
					break;
				} else if (SCORE_STATUS_MODIFY.equals(oneScoreStatus)) {
					updatestate = true;
					commited = true; //	
				} else {
					commited = true; //					
				}
			}
		}

		switch (currPageState) {
		case 0:
			btn_submit.setText("提交");
			btn_submit.setIcon(UIUtil.getImage("zt_050.gif"));
			btn_submit.setToolTipText("提交结果, 提交后不能修改");
			btn_apply_modify.setText("我想修改");
			btn_apply_modify.setIcon(UIUtil.getImage("script--pencil.png"));
			btn_apply_modify.setToolTipText("已提交的考评信息可以通过申请，修改实际扣分比例。");

			targetScoreListPanel.setItemVisible("recheckdratio", false);
			targetScoreListPanel.setRowNumberChecked(false);

			btn_submit.setVisible(!commited); // 已经提交过，就只能修改。
			if (commited && !updatestate) { //如果已经提交
				setCurrCellPanelStatus(targetScoreListPanel, "已 提 交");
			} else if (updatestate) {
				setCurrCellPanelStatus(targetScoreListPanel, "申请修改中");
			}
			btn_apply_modify.setVisible(commited);
			break;
		case 1:
			btn_submit.setText("下一步");
			btn_submit.setIcon(new UIUtil().getImage("office_190.gif"));
			btn_submit.setToolTipText("");

			btn_apply_modify.setText("取消");
			btn_apply_modify.setIcon(new UIUtil().getImage("office_078.gif"));
			targetScoreListPanel.setRowNumberChecked(true);

			btn_submit.setVisible(true); // 此界面显示两个按钮
			btn_apply_modify.setVisible(true);
			DivComponentLayoutUtil.getInstanse().removeComponentOnDiv(targetScoreListPanel, statusPanel);
			break;
		case 2:
			btn_submit.setText("提交申请");
			btn_submit.setIcon(new UIUtil().getImage("zt_028.gif"));
			btn_submit.setToolTipText("");
			btn_apply_modify.setText("返回");
			btn_apply_modify.setIcon(new UIUtil().getImage("pagefirst.gif"));
			btn_apply_modify.setToolTipText("返回评分界面");
			targetScoreListPanel.setItemVisible("recheckdratio", true);
			targetScoreListPanel.setRowNumberChecked(false);
			autoCheckCommit = true;//设置检测自动提交
			break;
		}
	}

	/*
	private void setStatusIfNeed() { //是否需要盖章.盖章是“已提交”　“修改申请中”

	}
	*/
	/*
	 * 此按钮是要做趋向性动作选择。例如 我要修改。取消，返回等.不会影响数据实际值
	 */
	private void onBtn2() {
		switch (currPageState) {
		case 0: // 我要修改
			iWantToModify();
			break;
		case 1: // 返回
			onSelectModifyBack();
			break;
		case 2: // 返回
			onModifyBack();
			break;
		}
	}

	// 提交申请修改的记录走工作流。
	private void onSubmitModifyByProcess() {
		BillVO allVOs[] = targetScoreListPanel.getAllBillVOs();
		for (int i = 0; i < allVOs.length; i++) {
			String recheckdratio = allVOs[i].getStringValue("recheckdratio");
			if (recheckdratio == null || recheckdratio.equals("")) {
				MessageBox.show(this, "至少有一条[新扣分比]没有填写.");
				return;
			}
		}
		InsertSQLBuilder sqlBuilder = new InsertSQLBuilder("sal_score_process");
		String newID = "";
		try {
			newID = UIUtil.getSequenceNextValByDS(null, "S_sal_score_process");
			sqlBuilder.putFieldValue("id", newID);
			sqlBuilder.putFieldValue("name", currLogID.getStringValue("name") + " 考核扣分修改申请.");
			sqlBuilder.putFieldValue("fid", currLogID.getStringValue("id"));
			sqlBuilder.putFieldValue("createtime", UIUtil.getServerCurrTime());
			sqlBuilder.putFieldValue("billtype", "绩效考评");
			sqlBuilder.putFieldValue("busitype", "打分修改申请");
			sqlBuilder.putFieldValue("type", "绩效考评结果修改申请");
			sqlBuilder.putFieldValue("createuser", ClientEnvironment.getCurrLoginUserVO().getId());
			sqlBuilder.putFieldValue("username", ClientEnvironment.getCurrLoginUserVO().getName());
			sqlBuilder.putFieldValue("deptid", ClientEnvironment.getCurrSessionVO().getLoginUserId());

			String itemids = stbutil.getMutilValueStr(stbutil.getBillVosItemList(allVOs, "id"));
			sqlBuilder.putFieldValue("itemids", itemids);
			int flag = UIUtil.executeUpdateByDS(null, sqlBuilder);
			if (flag > 0) { // 已经插入数据库
				BillListPanel listPanel = new BillListPanel("SAL_SCORE_PROCESS_CODE1");
				listPanel.QueryDataByCondition(" id = '" + newID + "'");
				if (listPanel.getRowCount() > 0) {
					listPanel.setSelectedRow(0);

					BillVO billVO = listPanel.getSelectedBillVO(); //
					String str_pkValue = billVO.getPkValue(); //
					if (str_pkValue == null || str_pkValue.trim().equals("")) {
						MessageBox.show(listPanel, "该处理任务关联的业务单据没有查询到数据!\n这是因为系统测试阶段的数据造成的,但流程仍然能够处理!\n系统上线后或清除垃圾数据后该问题应该不会再存在,请知悉!"); //
					}
					WorkFlowProcessDialog processDialog = null; //
					BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO()); // 创建一个卡片面板!!
					cardPanel.setBillVO(billVO.deepClone()); // 在卡片中设置数据,需要克隆一把!!
					processDialog = new WorkFlowProcessDialog(this, "流程处理", cardPanel, listPanel); // 抄送!!!既然这里流程实例为空，则消息任务id、流程任务id和流程实例id三个值都为null，故标题中不要显示了【李春娟/2012-03-28】
					processDialog.setVisible(true); //
					if (processDialog.getCloseType() != 1) { // 如果没有提交，直接删除。
						UIUtil.executeUpdateByDS(null, "delete from sal_score_process where id =" + newID);
					} else {
						// 可以放到工作流中
						UIUtil.executeUpdateByDS(null, "update sal_dept_check_score set status='" + SCORE_STATUS_MODIFY + "' where id in(" + TBUtil.getTBUtil().getInCondition(itemids) + ")");
						onModifyBack(); // 直接返回首页。
					}
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 我想修改。
	 */
	private void iWantToModify() {
		BillVO[] currVOs = targetScoreListPanel.getAllBillVOs();
		List<Integer> canotModifyRows = new ArrayList<Integer>(); // 找出不能修改的记录
		for (int i = 0; i < currVOs.length; i++) {
			String status = currVOs[i].getStringValue("status");
			if (!"已提交".equals(status)) { // 只有是已提交状态，才可以选择去提交修改申请
				canotModifyRows.add(i);
			}
		}
		if (canotModifyRows.size() == targetScoreListPanel.getRowCount()) { // 目前没有可以申请的内容
			MessageBox.show(this, "目前没有[已提交]的记录可以申请修改!");
			return;
		}
		int removeRows[] = new int[canotModifyRows.size()];
		for (int i = 0; i < removeRows.length; i++) {
			removeRows[i] = (Integer) canotModifyRows.get(i);
		}
		targetScoreListPanel.removeRows(removeRows);// 把多余的移除掉。只显示下【已提交】的。
		currPageState = 1; // 想要修改选择界面
		resetButton();
	}

	// 在选择修改界面返回。
	private void onSelectModifyBack() {
		targetScoreListPanel.refreshCurrData();
		currPageState = 0;
		resetButton();
	}
	private void onBtn3() {
		BillVO checkItemVos[] = targetScoreListPanel.getBillVOs();
		List updateSQLList = new ArrayList<UpdateSQLBuilder>();
		for (int i = 0; i < checkItemVos.length; i++) {
			UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_dept_check_score");
//			String checkdratio = checkItemVos[i].getStringValue("checkdratio"); // 扣分比例
//			String weights = checkItemVos[i].getStringValue("weights"); // 权重分值
//			float f_weights = Float.parseFloat(weights); // 权重分值
//			float f_checkratio = Float.parseFloat(checkdratio); // 扣分比例
//			float lastScore = f_weights * (100 - f_checkratio) / 100;
			updateSql.putFieldValue("checkscore", checkItemVos[i].getStringValue("checkdratio") + "");
			updateSql.putFieldValue("checkdratio", checkItemVos[i].getStringValue("checkdratio"));
			updateSql.putFieldValue("scoredeptname", ClientEnvironment.getLoginUserDeptName());
			updateSql.setWhereCondition("id=" + checkItemVos[i].getPkValue());
			updateSQLList.add(updateSql);
		}
		try {
			UIUtil.executeBatchByDS(null, updateSQLList, true, false);
		} catch (Exception e1) {
			MessageBox.showException(targetScoreListPanel, e1);
			e1.printStackTrace();
		}
	}
	/*
	 * 提交
	 */
	private void onSubmit() {
		StringBuffer notFill = new StringBuffer();
		BillVO checkItemVos[] = targetScoreListPanel.getBillVOs();
		int notFillCount = 0; // 记录未填写数量
		List updateSQLList = new ArrayList<UpdateSQLBuilder>();
			for (int i = 0; i < checkItemVos.length; i++) {
			if (checkItemVos[i].getStringValue("checkdratio") == null || checkItemVos[i].getStringValue("checkdratio").equals("")) {
				notFill.append("第" + (i + 1) + "行" + " " + checkItemVos[i].getStringValue("targetname") + "\r\n");
				notFillCount++;
			} else {//如果已经有值，那么就更新
				Double dfen=Double.parseDouble(checkItemVos[i].getStringValue("checkdratio"));//zzl 得分
				Double qzhong=Double.parseDouble(checkItemVos[i].getStringValue("weights"));// zzl 权重
				if(!falg){
					if(qzhong<0.0){
						if(dfen>=qzhong && dfen<=0){
							UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_dept_check_score");
							String checkdratio = checkItemVos[i].getStringValue("checkdratio"); // 扣分比例
//							String weights = checkItemVos[i].getStringValue("weights"); // 权重分值
//							float f_weights = Float.parseFloat(weights); // 权重分值
//							float f_checkratio = Float.parseFloat(checkdratio); // 扣分比例
//							float lastScore = f_weights * (100 - f_checkratio) / 100;
							updateSql.putFieldValue("checkscore", checkdratio);
							updateSql.putFieldValue("checkdratio", checkdratio);
							updateSql.putFieldValue("scoredeptname", ClientEnvironment.getLoginUserDeptName());
							updateSql.setWhereCondition("id=" + checkItemVos[i].getPkValue());
							updateSQLList.add(updateSql);
						}else{
							notFill.append("第" + (i + 1) + "行" + " " + checkItemVos[i].getStringValue("targetname") + "的得分大于权重分值，请重新打分。 \r\n");
						}
					}else if(Math.abs(dfen)>Math.abs(qzhong)){
						notFill.append("第" + (i + 1) + "行" + " " + checkItemVos[i].getStringValue("targetname") + "的得分大于权重分值，请重新打分。 \r\n");
					}else{
						UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_dept_check_score");
						String checkdratio = checkItemVos[i].getStringValue("checkdratio"); // 扣分比例
//						String weights = checkItemVos[i].getStringValue("weights"); // 权重分值
//						float f_weights = Float.parseFloat(weights); // 权重分值
//						float f_checkratio = Float.parseFloat(checkdratio); // 扣分比例
//						float lastScore = f_weights * (100 - f_checkratio) / 100;
						updateSql.putFieldValue("checkscore", checkdratio);
						updateSql.putFieldValue("checkdratio", checkdratio);
						updateSql.putFieldValue("scoredeptname", ClientEnvironment.getLoginUserDeptName());
						updateSql.setWhereCondition("id=" + checkItemVos[i].getPkValue());
						updateSQLList.add(updateSql);
					}
				}else{
					UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_dept_check_score");
					String checkdratio = checkItemVos[i].getStringValue("checkdratio"); // 扣分比例
//					String weights = checkItemVos[i].getStringValue("weights"); // 权重分值
//					float f_weights = Float.parseFloat(weights); // 权重分值
//					float f_checkratio = Float.parseFloat(checkdratio); // 扣分比例
//					float lastScore = f_weights * (100 - f_checkratio) / 100;
					updateSql.putFieldValue("checkscore", checkdratio);
					updateSql.putFieldValue("checkdratio", checkdratio);
					updateSql.putFieldValue("scoredeptname", ClientEnvironment.getLoginUserDeptName());
					updateSql.setWhereCondition("id=" + checkItemVos[i].getPkValue());
					updateSQLList.add(updateSql);

				}
			}
		}
		if (notFillCount > 0) {
			MessageBox.show(this, "您有" + notFillCount + "条记录没有评分.");
			return;
		}else if(notFill==null || notFill.equals("") || notFill.equals(null) ||notFill.length()<=0){
		}else{
			MessageBox.show(this, "您所选择的指标有部分评分异常：\r\n" + notFill.toString());
			return;
		}
		//把所有数据再保存一次到服务器.
		try {
			UIUtil.executeBatchByDS(null, updateSQLList, true, false);
		} catch (Exception e1) {
			MessageBox.showException(targetScoreListPanel, e1);
			e1.printStackTrace();
			return;
		}

		if (checkItemVos.length <= 0 || !MessageBox.confirm(this, "提交后若要再修改需要走流程申请\r\n确定要提交所有记录吗？")) {
			return;
		}
		String update = "update sal_dept_check_score set status='" + SCORE_STATUS_COMMIT + "' where id in(" + stbutil.getInCondition(checkItemVos, "id") + ") and checkdratio is not null and (status='" + SCORE_STATUS_INIT + "' or status is null)";
		try {
			UIUtil.executeUpdateByDS(null, update);
			targetScoreListPanel.refreshData();
			String[][] result = UIUtil.getStringArrayByDS(null, "select count(id),checktype from sal_person_check_score where scoreuser = " + userid + " and (status <> '已提交' or status is null) and logid=" + currLogID.getStringValue("id") + " group by checktype");
			StringBuffer msg = new StringBuffer("您还没有对 ");
			boolean have = false;
			for (int i = 0; i < result.length; i++) {
				if (!"0".equals(result[i][0])) {
					msg.append(result[i][1] + "、");
					have = true;
				}
			}
			if (have) {
				msg = new StringBuffer(msg.substring(0, msg.length() - 1));
			}
			if (have) {
				msg.append(" 进行评分或者提交，请点击【左上方页签】完成操作.");
				MessageBox.show(targetScoreListPanel, msg.toString());
			} else {
				MessageBox.show(targetScoreListPanel, "您已经完成了本次所有打分内容,谢谢！");
			}
			resetButton();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 前往修改界面
	private void goToModify() {
		BillVO selectVOs[] = targetScoreListPanel.getCheckedBillVOs();
		if (selectVOs.length == 0) {
			MessageBox.show(this, "请勾选要修改的数据.");
			return;
		}
		targetScoreListPanel.QueryDataByCondition(" id in(" + stbutil.getInCondition(selectVOs, "id") + ")");
		currPageState = 2;
		resetButton();
	}

	// 申请修改重新打分界面直接返回评分。
	private void onModifyBack() {
		targetScoreListPanel.QueryDataByCondition(" logid='" + currLogID.getStringValue("id") + "' and scoreuser='" + ClientEnvironment.getCurrSessionVO().getLoginUserId() + "'");
		targetScoreListPanel.setItemVisible("recheckdratio", false);
		targetScoreListPanel.setItemEditable("recheckdratio", false);
		targetScoreListPanel.setItemEditable("checkdratio", true);
		currPageState = 0;
		DivComponentLayoutUtil.getInstanse().removeComponentOnDiv(targetScoreListPanel, statusPanel);
		resetButton();
	}

	public void setSelectRow(int _row) {
		targetScoreListPanel.setSelectedRow(_row);
	}

	public void autoCheckIFCanCommit() {
		if (!autoCheckCommit) {//是否需要自动检测
			return;
		}
		if (currPageState == 0) {//评分
			BillVO vos[] = targetScoreListPanel.getAllBillVOs();
			for (int i = 0; i < vos.length; i++) {
				if (vos[i].getStringValue("checkdratio") == null || !SCORE_STATUS_INIT.equals(vos[i].getStringValue("status"))) { //如果没有填写内容
					return;
				}
			}
		} else if (currPageState == 2) {//申请修改
			BillVO vos[] = targetScoreListPanel.getAllBillVOs();
			for (int i = 0; i < vos.length; i++) {
				if (vos[i].getStringValue("recheckdratio") == null) { //
					return;
				}
			}
		}
		new Timer().schedule(new TimerTask() {
			public void run() {
				String msg = "";
				if (currPageState == 0) {
					msg = "此页面已评分完毕,是否提交结果?";
				} else if (currPageState == 2) {
					msg = "您要申请修改的记录可提交，是否提交结果?";
				}
				int index = MessageBox.showOptionDialog(targetScoreListPanel, msg, "系统提示", new String[] { "立即提交", "稍后提交" });
				if (index != 0) {
					autoCheckCommit = false;// 如果不喜欢自动提示。设置为false。
					return;
				}
				if (currPageState == 0) {
					onSubmit();
				} else if (currPageState == 2) {
					onSubmitModifyByProcess();
				}
			}
		}, 550); //延迟一会儿再弹出来.感受好一些。
	}

	/*
	 * 得到自己的计划
	 */
	SalaryBomPanel planBomPanel = new SalaryBomPanel();

	private SalaryBomPanel getPlanBomPanel(HashVO[] _planLogs) {
		for (int i = 0; i < _planLogs.length; i++) {
			_planLogs[i].setToStringFieldName("checkdate");
		}

		planBomPanel.addBomPanel(Arrays.asList(_planLogs));
		planBomPanel.addBomClickListener(new SalaryBomClickListener() {
			public void onBomClickListener(SalaryBomClickEvent event) {
				currLogID = event.getHashvo();
				targetScoreListPanel.QueryDataByCondition(" logid='" + currLogID.getStringValue("id") + "' and scoreuser='" + ClientEnvironment.getCurrSessionVO().getLoginUserId() + "'");
				planBomPanel.addBomPanel(targetScoreListPanel);
				currPageState = 0;
				resetButton();
			}
		});
		return planBomPanel;
	}

	private class MYAlphaPanel extends JPanel {
		private float alpha = 0;

		public void setAlpha(float alpha) {
			this.alpha = alpha;
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(Color.green);
			g2d.drawRect(1, 1, getWidth() - 2, getHeight() - 1);
		}
	}

	private JPanel statusPanel; //显示状态章的面板

	private void setCurrCellPanelStatus(final JComponent parent, final String _status) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				while (!parent.isShowing()) {
					try {
						Thread.currentThread().sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				statusPanel = new JPanel() {
					protected void paintComponent(Graphics g) {
						// 盖章
						if (parent != null && parent.isShowing()) { // 搞到bufferedimage中特别不清楚。
							super.paintComponent(g);
							Graphics2D g2d = (Graphics2D) g.create();
							g2d.translate(15, 8);
							g2d.setComposite(AlphaComposite.SrcOver.derive(0.6f));
							g2d.setColor(Color.red.brighter());
							g2d.rotate(0.4);
							g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2d.setStroke(new BasicStroke(3.5f));
							g2d.drawRect(1, 1, 150, 30);
							g2d.setFont(new Font("黑体", Font.BOLD, 25));
							g2d.drawString(_status, 15, 25);
						}
					}
				};
				statusPanel.setOpaque(false);
				Point p = parent.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(p, parent.getRootPane().getLayeredPane());
				BillListPanel cellPanel = (BillListPanel) parent;
				int tablewidth = (int) cellPanel.getTable().getVisibleRect().getWidth();
				int tableheight = (int) cellPanel.getTable().getVisibleRect().getHeight();
				Rectangle rect = new Rectangle((tablewidth / 2 - 40), tableheight / 2, 180, 130);
				DivComponentLayoutUtil.getInstanse().addComponentOnDiv(cellPanel, statusPanel, rect, null); //添加浮动层
			}
		}, 50);
	}
}

class ScoreDialog extends BillDialog implements ActionListener {
	private boolean canEdit;
	private WLTButton btn_forward, btn_next, btn_confirm;
	private BillListPanel listPanel;
	private BillCardPanel cardPanel;

	public ScoreDialog(Container _parent, boolean _canEdit, BillListPanel _listPanel) {
		super(_parent);
		this.canEdit = _canEdit;
		listPanel = _listPanel;
		init();
	}

	private void init() {
		cardPanel = new BillCardPanel("V_SAL_TARGET_ITEMSCORE_CODE1");
		this.setLayout(new BorderLayout());
		WLTPanel btnpanel = new WLTPanel(new FlowLayout(FlowLayout.CENTER));
		btn_forward = new WLTButton("上一条", UIUtil.getImage("zt_072.gif"));
		btn_next = new WLTButton("下一条", UIUtil.getImage("zt_073.gif"));
		btn_confirm = new WLTButton("确定", UIUtil.getImage("office_175.gif"));
		btn_forward.addActionListener(this);
		btn_next.addActionListener(this);
		btn_confirm.addActionListener(this);
		btnpanel.add(btn_forward);
		btnpanel.add(btn_next);
		btnpanel.add(btn_confirm);
		this.add(cardPanel, BorderLayout.CENTER);
		this.add(btnpanel, BorderLayout.SOUTH);
		BillVO score = listPanel.getSelectedBillVO();
		String scoreid = score.getStringValue("scoreid");
		cardPanel.queryDataByCondition(" scoreid = " + scoreid);
		setSize(650, 600);
		this.locationToCenterPosition();
		SwingUtilities.invokeLater(new Runnable() { // 延迟设定。否则光标没有默认位置。
					public void run() {
						setCurrPageEditState();
					}
				});

	}

	public BillCardPanel getCardPanel() {
		return cardPanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_forward) {
			onForward();
		} else if (e.getSource() == btn_next) {
			onNext();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		}
	}

	private void onForward() {
		if (!saveCurrPageData()) { //
			return;
		}
		int currIndex = listPanel.getSelectedRow();
		if (currIndex > 0) {
			listPanel.setSelectedRow(--currIndex);
		}
		BillVO score = listPanel.getSelectedBillVO();
		String scoreid = score.getStringValue("scoreid");
		cardPanel.queryDataByCondition(" scoreid = " + scoreid);
		setCurrPageEditState();
	}

	/*
	 * 保存当前页面值，并且刷新列表数据。
	 */
	private boolean saveCurrPageData() {
		cardPanel.stopEditing();
		if (!cardPanel.checkValidate()) {
			return false;
		}
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			BillVO scoreVO = cardPanel.getBillVO();
			UpdateSQLBuilder update = new UpdateSQLBuilder("sal_target_plan_score");
			update.putFieldValue("score", scoreVO.getStringValue("score"));
			update.putFieldValue("createdate", scoreVO.getStringValue("createdate"));
			update.putFieldValue("status", scoreVO.getStringValue("status"));
			update.setWhereCondition(" id =" + scoreVO.getStringValue("scoreid"));
			try {
				UIUtil.executeUpdateByDS(null, update);
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		listPanel.setValueAtRow(listPanel.getSelectedRow(), cardPanel.getBillVO());
		return true;
	}

	/*
	 * 下一条
	 */
	private void onNext() {
		if (!saveCurrPageData()) {
			return;
		}
		int currIndex = listPanel.getSelectedRow();
		if (currIndex < listPanel.getRowCount() - 1) {
			listPanel.setSelectedRow(++currIndex);
		}
		BillVO score = listPanel.getSelectedBillVO();
		String scoreid = score.getStringValue("scoreid");
		cardPanel.queryDataByCondition(" scoreid = " + scoreid);
		setCurrPageEditState();
	}

	/*
	 * 确定　关闭
	 */
	private void onConfirm() {
		if (!saveCurrPageData()) {
			return;
		}
		this.dispose();

	}

	/*
	 * 设置当前页面状态
	 */
	public void setCurrPageEditState() {
		BillVO vo = cardPanel.getBillVO();
		if (DeptTargetScoredWKPanel.SCORE_STATUS_COMMIT.equals(vo.getStringValue("status"))) {
			cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		} else {
			cardPanel.setEditableByEditInit();
			CardCPanel_TextField filed = (CardCPanel_TextField) cardPanel.getCompentByKey("score");
			filed.getTextField().requestFocus();
		}
		cardPanel.repaint();
	}
}
