package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.workflow.engine.DealActivityVO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 退回处理时的界面
 * 在招行项目中增加了三种退回分类：A-退回给发起人  B-退回给提交者  C-退回给历史曾经走过的任意一个人
 * @author xch
 *
 */
public class WorkflowRejectDealDialog extends BillDialog implements WindowListener, ActionListener {

	private BillVO billVO = null; //业务数据VO.
	private WFParVO firstTaskVO = null; //任务

	private JCheckBox checkBox = null;
	private JRadioButton radio_1, radio_2, radio_3;
	private BillListPanel billList = null; //
	private JButton btn_confirm, btn_monitor, btn_cancel; ////
	private int closetype = -1; //

	private TBUtil tbUtil = null; //

	public WorkflowRejectDealDialog(Container _parent, BillVO _billVO, WFParVO _firstTaskVO) {
		super(_parent, "退回处理", 750, 150, 150, 100); ////
		this.billVO = _billVO; //
		this.firstTaskVO = _firstTaskVO; //
		this.setResizable(true); //
		this.addWindowListener(this);
		initialize();
	}

	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //

		JPanel panel_all = new WLTPanel(WLTPanel.VERTICAL_TOP_TO_BOTTOM); //
		panel_all.setLayout(new BorderLayout()); //

		panel_all.add(getNorthPanel(), BorderLayout.NORTH); //
		billList = new BillListPanel("cn.com.infostrategy.bs.workflow.tmo.TMO_ChooseUser"); //
		billList.setItemVisible("usercode", false); //
		billList.setItemVisible("iseverprocessed", false); //

		panel_all.add(billList, BorderLayout.CENTER); //

		DealTaskVO[] taskVOS = firstTaskVO.getDealActivityVOs()[0].getDealTaskVOs(); //
		setDealTaskVOs(taskVOS); //
		billList.moveToTop(); //置顶
		billList.clearSelection(); //清除选择
		billList.setToolbarVisiable(false); //
		billList.setEnabled(true);
		billList.setItemEditable(false); //
		panel_all.add(getSouthPanel(), BorderLayout.SOUTH); //
		billList.setVisible(false); //

		this.add(panel_all, BorderLayout.CENTER); //
	}

	private JPanel getNorthPanel() { //
		JPanel panel = WLTPanel.createDefaultPanel(null); //

		checkBox = new JCheckBox("接收者是否直接提交给我?(即接收者不再按原来设计的路线逐级上报而是直接上报给我)", true); //
		checkBox.setToolTipText("<html>有时有这样一种情况,即直接退回给发起人补充一下资料,<br>然后发起人补充完后直接提交给我,而不再是按原来的流程路线提交!</html>"); //
		checkBox.setForeground(Color.BLUE); //
		checkBox.setBounds(5, 15, 600, 20); //
		checkBox.setOpaque(false);
		checkBox.setFocusable(false);
		if (getTBUtil().getSysOptionBooleanValue("工作流退回时是否指定接收人直接提交", true)) { //招行默认要勾上,兴业默认不要勾,所以只能搞个参数!! 默认是勾上的!
			checkBox.setSelected(true);
		} else {
			checkBox.setSelected(false);
		}
		panel.add(checkBox); //

		radio_1 = new JRadioButton("退回给发起人【" + this.firstTaskVO.getWfinstance_createusername() + "】(补充资料)", true); //
		radio_2 = new JRadioButton("退回给提交者【" + this.firstTaskVO.getDealpooltask_createusername() + "】"); //
		radio_3 = new JRadioButton("退回到历史任意一步"); //

		radio_2.setToolTipText(this.firstTaskVO.getDealpooltask_createusername()); //
		ButtonGroup btngroup = new ButtonGroup(); //
		btngroup.add(radio_2); //
		btngroup.add(radio_1); //
		btngroup.add(radio_3); //

		String str_defaultChoose = getTBUtil().getSysOptionStringValue("工作流退回时默认给谁", "1"); //兴业项目中要求默认交人,招行项目中默认要求给起草人,所以必须搞个参数!!
		if (str_defaultChoose.equals("1")) { //
			radio_1.setSelected(true); //
		} else if (str_defaultChoose.equals("2")) { //
			radio_2.setSelected(true); //
		}

		radio_1.setBounds(5, 45, 250, 20); //
		radio_2.setBounds(255, 45, 245, 20); //
		radio_3.setBounds(500, 45, 200, 20); //

		radio_1.setFocusable(false);
		radio_2.setFocusable(false);
		radio_3.setFocusable(false);

		radio_1.addActionListener(this);
		radio_2.addActionListener(this);
		radio_3.addActionListener(this);

		panel.add(radio_1); //
		panel.add(radio_2); //
		panel.add(radio_3); //
		radio_1.setOpaque(false);
		radio_2.setOpaque(false);
		radio_3.setOpaque(false);

		panel.setPreferredSize(new Dimension(900, 80)); //
		return panel;
	}

	/**
	 * 按钮面板
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER, 5, 10)); //
		btn_confirm = new WLTButton(UIUtil.getLanguage("确定")); //
		btn_monitor = new WLTButton("流程监控"); //
		btn_cancel = new WLTButton(UIUtil.getLanguage("取消")); //
		btn_confirm.setPreferredSize(new Dimension(70, 20)); //
		btn_cancel.setPreferredSize(new Dimension(70, 20)); //
		btn_monitor.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_confirm);
		panel.add(btn_monitor);
		panel.add(btn_cancel);

		return panel;
	}

	/**
	 * 往一个面板中塞入一个DealTaskVO..
	 */
	public void setDealTaskVOs(DealTaskVO[] _taskVO) {
		if(_taskVO==null){
			return;
		}
		for (int i = 0; i < _taskVO.length; i++) {
			//			if (containsUser(_taskVO[i].getParticipantUserId())) { //如果内存已包含该用户!则跳过
			//				continue;
			//			}

			String[] str_allkeys = billList.getTempletVO().getItemKeys(); //
			BillVO billVO = new BillVO(); //
			billVO.setKeys(str_allkeys); //
			Object[] objs = new Object[str_allkeys.length]; //
			for (int j = 0; j < str_allkeys.length; j++) {
				if (str_allkeys[j].equalsIgnoreCase("userid")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("usercode")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserCode()); //参与人员的编码
				} else if (str_allkeys[j].equalsIgnoreCase("username")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserName()); //参与人员的名称
				} else if (str_allkeys[j].equalsIgnoreCase("userdeptid")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserDeptId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("userdeptcode")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserDeptCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("userdeptname")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserDeptName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("userroleid")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserRoleId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("userrolecode")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserRoleCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("userrolename")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserRoleName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("accruserid")) {
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("accrusercode")) {
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("accrusername")) {
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("fromactivityid")) {
					objs[j] = new StringItemVO(_taskVO[i].getFromActivityId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("fromactivityname")) {
					objs[j] = new StringItemVO(_taskVO[i].getFromActivityName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionid")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitioncode")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionname")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionDealtype")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionDealtype()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionIntercept")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionIntercept()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionMailSubject")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionMailSubject()); //
				} else if (str_allkeys[j].equalsIgnoreCase("transitionMailContent")) {
					objs[j] = new StringItemVO(_taskVO[i].getTransitionMailContent()); //
				} else if (str_allkeys[j].equalsIgnoreCase("currActivityid")) {
					objs[j] = new StringItemVO(_taskVO[i].getCurrActivityId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("currActivitycode")) {
					objs[j] = new StringItemVO(_taskVO[i].getCurrActivityCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("currActivitytype")) {
					objs[j] = new StringItemVO(_taskVO[i].getCurrActivityType()); //
				} else if (str_allkeys[j].equalsIgnoreCase("currActivityname")) {
					objs[j] = new StringItemVO(_taskVO[i].getCurrActivityName()); //
				} else if (str_allkeys[j].equalsIgnoreCase("successparticipantreason")) {
					objs[j] = new StringItemVO(_taskVO[i].getSuccessParticipantReason()); //
				} else if (str_allkeys[j].equalsIgnoreCase("iseverprocessed")) {
					objs[j] = new StringItemVO(_taskVO[i].getIseverprocessed()); //
				}
			}

			billVO.setDatas(objs); //
			int li_newrow = billList.addEmptyRow(); //
			//vmap_users.put(_taskVO[i].getParticipantUserId(), billVO); //内存中记入
			billList.setBillVOAt(li_newrow, billVO); //页面中插入
		}
	}

	public WFParVO getReturnVO() {
		return firstTaskVO; //
	}

	public int getClosetype() {
		return closetype;
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil(); //
		return tbUtil;
	}

	//点击确定
	private void onConfirm() {
		DealTaskVO[] commitTaskVOs = null; //
		if (radio_1.isSelected()) {
			commitTaskVOs = getSelectedTaskVO_1(); ////
		} else if (radio_2.isSelected()) {
			commitTaskVOs = getSelectedTaskVO_2(); ////
		} else if (radio_3.isSelected()) {
			int li_selindex = billList.getSelectedRow(); //取得是第几个环节!!!
			if (li_selindex < 0) {
				MessageBox.show(this, "必须选择一个机构或人员!"); //
				return;
			}
			commitTaskVOs = getSelectedTaskVOs(); ////
		}

		clearAllDealTaskVOs(firstTaskVO); //清空所有第一次从服务器端返回的待处理任务,目的是为了提高性能.
		firstTaskVO.setCommitTaskVOs(commitTaskVOs); //设置处理任务..
		closetype = 1;
		this.dispose(); //
	}

	private void onCancel() {
		this.closetype = 2;
		this.dispose(); //
	}

	private void onMonitor() {
		try {
			WorkflowMonitorDialog dialog = new WorkflowMonitorDialog(this, this.firstTaskVO.getWfinstanceid(), this.billVO);
			dialog.setMaxWindowMenuBar();
			dialog.setVisible(true); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//直接给创建者
	private DealTaskVO[] getSelectedTaskVO_1() {
		DealTaskVO commitTaskVO = new DealTaskVO(); //
		commitTaskVO.setParticipantUserId(this.firstTaskVO.getWfinstance_createuserid()); //
		commitTaskVO.setParticipantUserCode(this.firstTaskVO.getWfinstance_createusercode()); //
		commitTaskVO.setParticipantUserName(this.firstTaskVO.getWfinstance_createusername()); //

		//授权人信息
		commitTaskVO.setAccrUserId(this.firstTaskVO.getDealpooltask_createuser_accruserid()); //
		commitTaskVO.setAccrUserCode(this.firstTaskVO.getDealpooltask_createuser_accrusercode()); //
		commitTaskVO.setAccrUserName(this.firstTaskVO.getDealpooltask_createuser_accrusername()); //

		//来源环节就是我当前任务的当前环节
		commitTaskVO.setFromActivityId(firstTaskVO.getCurractivity()); //从哪个环节过来的
		commitTaskVO.setFromActivityName(firstTaskVO.getCurractivityName()); //

		//当前环节应该就是创建者所处的环节!!即第一个开始的!!
		commitTaskVO.setCurrActivityId(this.firstTaskVO.getWfinstance_createactivityid()); //
		commitTaskVO.setCurrActivityCode(this.firstTaskVO.getWfinstance_createactivitycode()); //
		commitTaskVO.setCurrActivityName(this.firstTaskVO.getWfinstance_createactivityname()); //
		commitTaskVO.setCurrActivityType(this.firstTaskVO.getWfinstance_createactivitytype()); //

		commitTaskVO.setRejectedDirUp(checkBox.isSelected()); //设置是否退回者直接提交
		return new DealTaskVO[] { commitTaskVO };
	}

	//直接给上游提交者,即创建本流程任务的那个人!
	private DealTaskVO[] getSelectedTaskVO_2() {
		DealTaskVO commitTaskVO = new DealTaskVO(); //
		commitTaskVO.setParticipantUserId(this.firstTaskVO.getDealpooltask_createuserid()); //
		commitTaskVO.setParticipantUserCode(this.firstTaskVO.getDealpooltask_createusercode()); //
		commitTaskVO.setParticipantUserName(this.firstTaskVO.getDealpooltask_createusername()); //

		//授权人信息
		commitTaskVO.setAccrUserId(this.firstTaskVO.getDealpooltask_createuser_accruserid()); //
		commitTaskVO.setAccrUserCode(this.firstTaskVO.getDealpooltask_createuser_accrusercode()); //
		commitTaskVO.setAccrUserName(this.firstTaskVO.getDealpooltask_createuser_accrusername()); //

		//来源环节就是我当前任务的当前环节
		commitTaskVO.setFromActivityId(firstTaskVO.getCurractivity()); //从哪个环节过来的
		commitTaskVO.setFromActivityName(firstTaskVO.getCurractivityName()); //

		//当前环节应该就是上一步提交的环节
		commitTaskVO.setCurrActivityId(this.firstTaskVO.getFromactivity()); //目标环节是当前任务的来源环节,因为是乒乓式的,对踢的!它与提交时的效果是一样,目标与来源交换!
		commitTaskVO.setCurrActivityCode(this.firstTaskVO.getFromactivityCode()); //
		commitTaskVO.setCurrActivityName(this.firstTaskVO.getFromactivityName()); //
		commitTaskVO.setCurrActivityType(this.firstTaskVO.getFromactivityType()); //
		commitTaskVO.setRejectedDirUp(checkBox.isSelected()); //设置是否退回者直接提交
		return new DealTaskVO[] { commitTaskVO };
	}

	/**
	 * 从人员面板中返回所有待处理任务!!
	 * @return
	 */
	public DealTaskVO[] getSelectedTaskVOs() {
		BillVO[] billVOs = billList.getSelectedBillVOs(); //
		DealTaskVO[] commitTaskVO = new DealTaskVO[billVOs.length]; //
		for (int i = 0; i < billVOs.length; i++) {
			commitTaskVO[i] = new DealTaskVO(); //
			commitTaskVO[i].setParticipantUserId(billVOs[i].getStringValue("userid")); //
			commitTaskVO[i].setParticipantUserCode(billVOs[i].getStringValue("usercode")); //
			commitTaskVO[i].setParticipantUserName(billVOs[i].getStringValue("username")); //

			commitTaskVO[i].setParticipantUserDeptId(billVOs[i].getStringValue("userdeptid")); //
			commitTaskVO[i].setParticipantUserDeptCode(billVOs[i].getStringValue("userdeptcode")); //
			commitTaskVO[i].setParticipantUserDeptName(billVOs[i].getStringValue("userdeptname")); //

			commitTaskVO[i].setParticipantUserRoleId(billVOs[i].getStringValue("userroleid")); //
			commitTaskVO[i].setParticipantUserRoleCode(billVOs[i].getStringValue("userrolecode")); //
			commitTaskVO[i].setParticipantUserRoleName(billVOs[i].getStringValue("userrolename")); //

			commitTaskVO[i].setAccrUserId(billVOs[i].getStringValue("accruserid")); //授权人主键.
			commitTaskVO[i].setAccrUserCode(billVOs[i].getStringValue("accrusercode")); //授权人编码.
			commitTaskVO[i].setAccrUserName(billVOs[i].getStringValue("accrusername")); //授权人名称.

			commitTaskVO[i].setFromActivityId(billVOs[i].getStringValue("fromactivityid")); //
			commitTaskVO[i].setFromActivityName(billVOs[i].getStringValue("fromactivityname")); //

			commitTaskVO[i].setTransitionId(billVOs[i].getStringValue("transitionid")); //
			commitTaskVO[i].setTransitionCode(billVOs[i].getStringValue("transitioncode")); //
			commitTaskVO[i].setTransitionName(billVOs[i].getStringValue("transitionname")); //
			commitTaskVO[i].setTransitionDealtype(billVOs[i].getStringValue("transitionDealtype")); //
			commitTaskVO[i].setTransitionIntercept(billVOs[i].getStringValue("transitionIntercept")); //
			commitTaskVO[i].setTransitionMailSubject(billVOs[i].getStringValue("transitionMailSubject")); //
			commitTaskVO[i].setTransitionMailContent(billVOs[i].getStringValue("transitionMailContent")); //

			commitTaskVO[i].setCurrActivityId(billVOs[i].getStringValue("currActivityid")); //
			commitTaskVO[i].setCurrActivityCode(billVOs[i].getStringValue("currActivitycode")); //
			commitTaskVO[i].setCurrActivityName(billVOs[i].getStringValue("currActivityname")); //
			commitTaskVO[i].setCurrActivityType(billVOs[i].getStringValue("currActivitytype")); //
			commitTaskVO[i].setRejectedDirUp(checkBox.isSelected()); //设置是否退回者直接提交
		}

		return commitTaskVO; //
	}

	//
	private void onRadioSelectChanged() {
		if (radio_3.isSelected()) {
			billList.setVisible(true); //
			this.setSize(750, 550); //
		} else {
			billList.setVisible(false);
			this.setSize(750, 150);
		}
	}

	/**
	 * 清空所有从服务器端返回的参与部门与参与人员的信息,因为这些信息第二次提交时不需要了!!目的是为了提高性能!!
	 * @param _ParVO
	 */
	private void clearAllDealTaskVOs(WFParVO _ParVO) {
		DealActivityVO[] activityVOs = _ParVO.getDealActivityVOs(); //
		if (activityVOs != null) {
			for (int i = 0; i < activityVOs.length; i++) {
				activityVOs[i].setDealTaskVOs(null); //清空所有待处理任务
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //确定
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //取消
		} else if (e.getSource() == btn_monitor) {
			onMonitor(); //监控
		} else if (e.getSource() == radio_1 || e.getSource() == radio_2 || e.getSource() == radio_3) {
			onRadioSelectChanged(); //
		}
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

}
