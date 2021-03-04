package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.to.workflow.engine.DealActivityVO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 选择人员的面板..这是真正的计算某个环节上的所有接收人员与参与人员的界面!!
 * 后来扩展成可以点击[接收人员]按钮而自由新增新的人员! 即自由流程了!!! 这个功能 至关重要!!
 * @author xch
 *
 */
public class ParticipateUserPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 6178660830460024752L;

	private DealActivityVO currActivityVO = null; //当前环节的信息!!!

	private Pub_Templet_1VO templet_VO = null; //模板对象
	private BillVO billVO = null; //单据VO,因为后来授权模式,发现点击"接收人员"手工添加人员时,需要得到授权模块!! 而授权模块又是根据流程分配得到的! 而流程分配又是根据单据类型与业务类型计算出来的! 而单据/业务类型又是从页面上取得到!所以需要BillVO

	private BillListPanel billListPanel_1 = null; //接收人员!!!
	private BillListPanel billListPanel_2 = null; //抄送人员!!!
	private JTable table1 = null; //表1
	private JTable table2 = null; //表2

	private WLTButton btn_addReceiver = new WLTButton("添加接收人员"); //
	private WLTButton btn_addCCToUser = new WLTButton("添加抄送人员"); //

	private WLTButton btn_delReceiver = new WLTButton("删除接收人员"); //
	private WLTButton btn_delCCToUser = new WLTButton("删除抄送人员"); //

	private JRadioButton radioBtn_1, radioBtn_2, radioBtn_3; //抢占/会签/子流程会办
	private AddNewUserDialog freeAddUserdialog = null; //自由添加人员的对话框
	private VectorMap vmap_users = new VectorMap(); //存放实际数据的对象.为了过滤用

	private TBUtil tbUtil = null; //
	boolean isCheckList = false; //是否是勾选框的方式,南京邮运客户就是喜欢勾选框的方式!
	boolean isSingleSelectUser = false; //是否必须单选用户?即在邮储项目中 
	private HashVO hvoCurrWFInfo = null;//当前流程信息,即根据单据类型与业务类型,计算出属于什么流程,该流程中定义了什么拦截器【李春娟/2016-04-20】
	private HashVO hvoCurrActivityInfo = null;//当前环节信息
	private WFParVO firstTaskVO = null; //任务!!真正的任务!!!

	public ParticipateUserPanel(DealActivityVO _currActivityVO, BillVO _billVO, HashVO _hvoCurrWFInfo, HashVO _hvoCurrActivityInfo, WFParVO _firstTaskVO) {
		this.currActivityVO = _currActivityVO; //
		this.billVO = _billVO;
		this.hvoCurrWFInfo = _hvoCurrWFInfo;
		this.hvoCurrActivityInfo = _hvoCurrActivityInfo;
		this.firstTaskVO = _firstTaskVO;
		try {
			this.templet_VO = UIUtil.getPub_Templet_1VO(new ServerTMODefine("cn.com.infostrategy.bs.workflow.tmo.TMO_ChooseUser")); //
			initialize(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void initialize() {
		this.setLayout(new BorderLayout()); //
		isCheckList = getTBUtil().getSysOptionBooleanValue("工作流提交时选择人员是否是勾选框", false); //是否是勾选框的方式?
		isSingleSelectUser = getTBUtil().getSysOptionBooleanValue("工作流提交时抢占模式是否只能单选", false); //默认是多选的,如果是单选,则就意味着永远是抢占了,没有会签的概念了!

		//接收的界面!
		btn_addReceiver.setToolTipText("点击添加新的接收人员"); //
		btn_addReceiver.addActionListener(this); //
		btn_delReceiver.setToolTipText("删除选中的接收人员"); //
		btn_delReceiver.addActionListener(this); //

		billListPanel_1 = new BillListPanel(templet_VO); //接收人员

		if ("1".equals(currActivityVO.getApprovemodel()) && isSingleSelectUser) { //如果是抢占模式,还必须判断当前环节的风格是什么
			billListPanel_1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //单选
		} else { //如果是多选,则还要决定是否是勾选框的样式?
			billListPanel_1.setRowNumberChecked(isCheckList); //是否是勾选的样式!!南京油运客户死活说Ctrl多选的机制不好!!!
		}

		billListPanel_1.getTitlePanel().setVisible(false); //
		//billListPanel_1.getTitleLabel().setText("点击新增人员");  //

		//sunfujun/20121126/将是否显示接收按钮放到环节上
		if (getTBUtil().getSysOptionBooleanValue("工作流提交时是否显示接收人员按钮", true)) { //系统全局参数! 应该是流程图中的环节参数! 即全局参数如何与局部参数整合?然后又可以兼容旧版本?
			billListPanel_1.addBatchBillListButton(new WLTButton[] { btn_addReceiver, btn_delReceiver }); //
		} else {
			if ("Y".equals(currActivityVO.getCanselfaddparticipate())) {
				billListPanel_1.addBatchBillListButton(new WLTButton[] { btn_addReceiver, btn_delReceiver });
			}
		}
		billListPanel_1.repaintBillListButton(); //
		table1 = billListPanel_1.getTable(); //
		table1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent _event) {
				if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
					onDeleteRow((JTable) _event.getSource()); //
				}
			}
		}); //

		//抄送的界面!
		btn_addCCToUser.setToolTipText("点击添加新的抄送人员"); //
		btn_addCCToUser.addActionListener(this); //
		btn_delCCToUser.setToolTipText("删除选中的抄送人员"); //
		btn_delCCToUser.addActionListener(this); //
		billListPanel_2 = new BillListPanel(templet_VO); //抄送人员

		//抄送时永远可以给多个人,即是多选！！！
		billListPanel_2.setRowNumberChecked(isCheckList); //是否是勾选的样式!!南京油运客户死活说Ctrl多选的机制不好!!!

		billListPanel_2.setItemVisible("iseverprocessed", false);
		billListPanel_2.getTitlePanel().setVisible(false); //
		if (getTBUtil().getSysOptionBooleanValue("工作流提交时是否显示抄送人员按钮", true)) { //
			billListPanel_2.addBatchBillListButton(new WLTButton[] { btn_addCCToUser, btn_delCCToUser }); //
		}
		billListPanel_2.repaintBillListButton(); //
		table2 = billListPanel_2.getTable(); ////
		table2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent _event) {
				if (_event.getKeyCode() == KeyEvent.VK_DELETE) {
					onDeleteRow((JTable) _event.getSource()); //
				}
			}
		}); //

		if (getTBUtil().getSysOptionBooleanValue("工作流是否有抄送功能", true)) { //如果需要抄送功能,则使用分割器加入两个框!!
			WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billListPanel_1, billListPanel_2); //
			splitPanel.setDividerLocation(310); //设置高度
			this.add(splitPanel, BorderLayout.CENTER); //
		} else { //如果不需要抄送功能,则直接只有一个接收人员框!

			this.add(billListPanel_1, BorderLayout.CENTER); //
		}

		//可以人工修改审批模式,即以前是在后台设定好,要么是抢占,要么是会签,要么是子流程! 后来发现有时经常需要让用户临时决定是抢点还是会签!!!
		radioBtn_1 = new JRadioButton("抢占"); //
		radioBtn_2 = new JRadioButton("会签"); //
		radioBtn_3 = new JRadioButton("子流程会办"); //
		radioBtn_1.setOpaque(false);
		radioBtn_2.setOpaque(false);
		radioBtn_3.setOpaque(false);
		radioBtn_1.setFocusable(false);
		radioBtn_2.setFocusable(false);
		radioBtn_3.setFocusable(false);

		radioBtn_1.setToolTipText("选中的接收者只要有一个人抢先处理了,就直接进入下一环节"); //
		radioBtn_2.setToolTipText("必须选中的所有接收者都处理了,才能进入下一环节"); //
		radioBtn_3.setToolTipText("以所有接收者为发起人,在目标环节里又存在一个子流程,然后所有子流程都处理结束了,自动返回"); //

		if ("1".equalsIgnoreCase(currActivityVO.getApprovemodel())) {
			radioBtn_1.setSelected(true); //
		} else if ("2".equalsIgnoreCase(currActivityVO.getApprovemodel())) {
			radioBtn_2.setSelected(true); //
		} else if ("3".equalsIgnoreCase(currActivityVO.getApprovemodel())) {
			radioBtn_3.setSelected(true); //
		} else {
			radioBtn_1.setSelected(true); //
		}

		ButtonGroup btnGroup = new ButtonGroup(); //
		btnGroup.add(radioBtn_1); //
		btnGroup.add(radioBtn_2); //
		btnGroup.add(radioBtn_3); //

		JPanel panel_radioBtn = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new FlowLayout(FlowLayout.LEFT, 0, 0), LookAndFeel.defaultShadeColor1, false); //
		panel_radioBtn.add(radioBtn_1); //
		panel_radioBtn.add(radioBtn_2); //
		panel_radioBtn.add(radioBtn_3); //

		//这里要搞个参数,一个是全局参数,一个是环节参数,环节参数可以覆盖全局参数!
		if (1 == 2) { //先不加,以后需要时加上，一般来说只有特定环节才会有!
			this.add(panel_radioBtn, BorderLayout.NORTH); //
		}
		//平台标准的流程定义义拦截器,需要将单据类型与业务类型传进去！！！因为在拦截器里面要判断,也要将当前环节信息传进去,在拦截器里面判断!!!
		if (hvoCurrWFInfo != null) {
			String str_wfeguiintercept = hvoCurrWFInfo.getStringValue("process$wfeguiintercept"); //流程后来升级的,统一的的UI端拦截器!!!
			if (str_wfeguiintercept != null) {
				WLTHashMap parMap = new WLTHashMap();
				try {
					WorkFlowEngineUIIntercept uiIntercept = (WorkFlowEngineUIIntercept) Class.forName(str_wfeguiintercept).newInstance();
					uiIntercept.afterOpenParticipateUserPanel(this, billVO, hvoCurrActivityInfo, firstTaskVO, parMap); //拦截平台定义的流程拦截器!!!
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} //
			}
		}
	}

	/**
	 * 删除人员,即有时人员太多了或选错了,可以删除其中的某些人员!! 
	 * 但如果不能手工添加人员,又误删除了人员,则没有后悔的办法,只能关闭窗口重新打开!!
	 */
	private void onDeleteRow(JTable _table) {
		if (_table == table1) {
			int[] li_rows = table1.getSelectedRows(); //
			if (li_rows.length <= 0) {
				MessageBox.show(this, "必须选中一个或多个接收人员才能进行此操作!"); //
				return; //
			}

			if (!MessageBox.confirm(this, "您确定要删除选中的接收人员吗?")) {
				return;
			}
			billListPanel_1.removeSelectedRows(); //
		} else if (_table == table2) {
			int[] li_rows = table2.getSelectedRows(); //
			if (li_rows.length <= 0) {
				MessageBox.show(this, "必须选中一个或多个抄送人员才能进行此操作!"); //
				return; //
			}
			if (!MessageBox.confirm(this, "您确定要删除选中的抄送人员吗?")) {
				return;
			}
			billListPanel_2.removeSelectedRows(); //
		}
	}

	/**
	 * 往一个面板中塞入一个DealTaskVO..
	 */
	public void setDealTaskVOs(DealTaskVO[] _taskVO, boolean _isAutoSel) {
		if (_taskVO == null) {
			return;
		}
		for (int i = 0; i < _taskVO.length; i++) {
			if (containsUser(_taskVO[i].getParticipantUserId())) { //如果内存已包含该用户!则跳过
				//continue;
			}

			String[] str_allkeys = templet_VO.getItemKeys(); //
			BillVO billVO = new BillVO(); //
			billVO.setKeys(str_allkeys); //
			Object[] objs = new Object[str_allkeys.length]; //
			for (int j = 0; j < str_allkeys.length; j++) {
				if (str_allkeys[j].equalsIgnoreCase("userid")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserId()); //
				} else if (str_allkeys[j].equalsIgnoreCase("usercode")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserCode()); //
				} else if (str_allkeys[j].equalsIgnoreCase("username")) {
					objs[j] = new StringItemVO(_taskVO[i].getParticipantUserName()); //
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
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserId()); //授权人主键
				} else if (str_allkeys[j].equalsIgnoreCase("accrusercode")) {
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserCode()); //授权人编码
				} else if (str_allkeys[j].equalsIgnoreCase("accrusername")) {
					objs[j] = new StringItemVO(_taskVO[i].getAccrUserName()); //授权人名称
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
				} else if (str_allkeys[j].equalsIgnoreCase("currActivityname")) { //步骤名称
					objs[j] = new StringItemVO(currActivityVO.getCurrActivityName(false)); //当前环节名称,要补上矩阵的名称!!!
				} else if (str_allkeys[j].equalsIgnoreCase("successparticipantreason")) {
					objs[j] = new StringItemVO(_taskVO[i].getSuccessParticipantReason()); //
				} else if (str_allkeys[j].equalsIgnoreCase("iseverprocessed")) {
					objs[j] = new StringItemVO(_taskVO[i].getIseverprocessed()); //
				}
			}

			billVO.setDatas(objs); //

			if (!_taskVO[i].isCCTo()) { //如果不是抄送的,即是实际提交的,才插入上面的表中!
				int li_newrow = billListPanel_1.addEmptyRow(_isAutoSel, true); //
				billListPanel_1.setBillVOAt(li_newrow, billVO); //页面中插入
				if (billListPanel_1.isRowNumberChecked()) {
					billListPanel_1.setCheckedRow(li_newrow, _isAutoSel);
				}
				vmap_users.put(_taskVO[i].getParticipantUserId(), billVO); //内存中记入
			} else { //如果是抄送的,则插入第二张表中!!
				int li_newrow = billListPanel_2.addEmptyRow(_isAutoSel, true); //
				billListPanel_2.setBillVOAt(li_newrow, billVO); //页面中插入
				billListPanel_2.setCheckedRow(li_newrow, true); //
				vmap_users.put(_taskVO[i].getParticipantUserId(), billVO); //内存中记入
			}
		}
	}

	public BillVO[] getMapBillVOs() {
		Object[] objs = vmap_users.getValues(); //
		BillVO[] vos = new BillVO[objs.length]; //
		for (int i = 0; i < objs.length; i++) {
			vos[i] = (BillVO) objs[i];
		}
		return vos;
	}

	/**
	 * 页面中是否已包含该用户!
	 * @param _userid
	 * @return
	 */
	private boolean containsUser(String _userid) {
		return vmap_users.containsKey(_userid); //
	}

	/**
	 * 添加接收人员
	 * 左边是个机构树,然后默认是当前人员的所在部门! 右边是人员清单! 最好显示出该人员的角色!!因为选择时需要!!!
	 * 然后点击时就将这个人添加进来!!并自动选中!!! 加入之前要先计算一下,页面中有无此人,若已有,则提示不能添加了!!!
	 * 最好还有一个页签,显示流程中曾经走过的人员!!这样更人性化,因为实际情况是经常是需要提交给曾经走过的人员
	 */
	private void onAddReceiver() {
		dealAddReceiveAndCCToUser("添加新的接收人员", false); //
	}

	/**
	 * 添加抄送人员
	 */
	private void onAddCCToUser() {
		dealAddReceiveAndCCToUser("添加新的抄送人员", true); //
	}

	//增加人员!!
	private void dealAddReceiveAndCCToUser(String _title, boolean _isCC) {
		if (freeAddUserdialog == null) {
			freeAddUserdialog = new AddNewUserDialog(this, this.billVO); //为了提高性能,将Dialog缓存下来,第二次点击时特别快!!!
		}
		freeAddUserdialog.setTitle(_title); //
		freeAddUserdialog.setVisible(true); //
		if (freeAddUserdialog.getCloseType() == AddNewUserDialog.CONFIRM) {//sunfujun/20120717/修改选了人之后点X也会加人的bug
			//BillVO corpVO = freeAddUserdialog.getReturnCorpVO(); //
			BillVO[] userVOs = freeAddUserdialog.getReturnUserVOs(); //选中的人员!!
			DealTaskVO[] taskVOS = new DealTaskVO[userVOs.length]; //
			for (int i = 0; i < taskVOS.length; i++) {
				taskVOS[i] = new DealTaskVO(); //
				taskVOS[i].setFromActivityId(currActivityVO.getFromActivityId()); //来源环节id
				taskVOS[i].setFromActivityName(currActivityVO.getFromActivityName()); //来源环节Name

				taskVOS[i].setTransitionId(currActivityVO.getFromTransitionId()); //从哪根线过来的
				taskVOS[i].setTransitionCode(currActivityVO.getFromTransitionCode()); //连线编码
				taskVOS[i].setTransitionName(currActivityVO.getFromTransitionName()); //连线名称
				taskVOS[i].setTransitionDealtype(currActivityVO.getFromtransitiontype()); //
				taskVOS[i].setTransitionIntercept(currActivityVO.getFromTransitionIntercept()); //
				taskVOS[i].setTransitionMailSubject(currActivityVO.getFromTransitionMailsubject()); //
				taskVOS[i].setTransitionMailContent(currActivityVO.getFromTransitionMailcontent()); //

				taskVOS[i].setCurrActivityId(currActivityVO.getActivityId()); //当前环节id
				taskVOS[i].setCurrActivityCode(currActivityVO.getActivityCode()); //环节Code
				taskVOS[i].setCurrActivityName(currActivityVO.getActivityName()); //环节Name
				taskVOS[i].setCurrActivityType(currActivityVO.getActivityType()); //类型,比如是普通/启动/结束
				taskVOS[i].setCurrActivityApproveModel(currActivityVO.getApprovemodel()); //环节审批模式!!

				taskVOS[i].setParticipantUserId(userVOs[i].getStringValue("userid")); //
				taskVOS[i].setParticipantUserCode(userVOs[i].getStringValue("usercode")); //
				taskVOS[i].setParticipantUserName(userVOs[i].getStringValue("username")); //

				taskVOS[i].setParticipantUserRoleId(userVOs[i].getStringValue("userroleid")); //人员角色
				taskVOS[i].setParticipantUserRoleCode(userVOs[i].getStringValue("userrolename")); //角色编码
				taskVOS[i].setParticipantUserRoleName(userVOs[i].getStringValue("userrolename")); //角色名称

				taskVOS[i].setParticipantUserDeptId(userVOs[i].getStringValue("userdeptid")); //机构id
				taskVOS[i].setParticipantUserDeptCode(userVOs[i].getStringValue("userdeptcode")); //机构编码
				taskVOS[i].setParticipantUserDeptName(userVOs[i].getStringValue("userdeptname")); //机构名称

				taskVOS[i].setAccrUserId(userVOs[i].getStringValue("accruserid")); //授权人,应该有授权人的!
				taskVOS[i].setAccrUserCode(userVOs[i].getStringValue("accrusercode")); //授权人编码,应该有授权人的!
				taskVOS[i].setAccrUserName(userVOs[i].getStringValue("accrusername")); //授权人名称,应该有授权人的!

				taskVOS[i].setCCTo(_isCC); //
				taskVOS[i].setRejectedDirUp(false); //

				taskVOS[i].setIseverprocessed("N"); //是否曾经走过
				taskVOS[i].setSuccessParticipantReason("自由添加"); //成功的原因
			}

			setDealTaskVOs(taskVOS, true); //设置数据!!! 
			//邮储希望选中的人自动勾上或选择，如果是单选且可以自由添加如果添加了多个则只能选择最后一个理论上不存在/sunfujun/20130217
		}
	}

	/**
	 * 点击添加接收人员和抄送人员按钮的逻辑!!!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_addReceiver) {
			onAddReceiver(); //添加接收人员!!!
		} else if (e.getSource() == btn_addCCToUser) {
			onAddCCToUser(); //
		} else if (e.getSource() == btn_delReceiver) {
			onDeleteRow(table1); //
		} else if (e.getSource() == btn_delCCToUser) {
			onDeleteRow(table2); //
		}
	}

	/**
	 * 从人员面板中返回所有待处理任务!!
	 * @return
	 */
	public DealTaskVO[] getSelectedTaskVOs() {
		BillVO[] billVOs_1 = billListPanel_1.getSelectedBillVOs(true); //取第一个中的人员!! 一定要改成Checked,如果列表选择方式是勾选方式，则取得勾选的记录【李春娟/2012-03-01】
		if (billVOs_1 == null || billVOs_1.length == 0) {
			MessageBox.show(this, "请选中“接收人员”!" + (billListPanel_1.isRowNumberChecked() ? "\r\n请注意:只有选中前面的勾选框才算是真正选择!" : "")); //
			return null;
		}

		//如果选中的用户有授权情况,则提醒一下!!
		HashSet hst_allaccrUser = new HashSet(); //
		for (int i = 0; i < billListPanel_1.getRowCount(); i++) {
			String str_accruser = billListPanel_1.getRealValueAtModel(i, "accruserid"); //
			if (str_accruser != null) {
				hst_allaccrUser.add(str_accruser); //
			}
		}
		StringBuilder sb_allAccrUserNames = new StringBuilder(); //
		for (int i = 0; i < billVOs_1.length; i++) {
			String str_userid = billVOs_1[i].getStringValue("userid"); //
			String str_userName = billVOs_1[i].getStringValue("username"); //
			String str_accruserid = billVOs_1[i].getStringValue("accruserid"); //授权人
			if (hst_allaccrUser.contains(str_userid) || hst_allaccrUser.contains(str_accruserid)) { //
				sb_allAccrUserNames.append("【" + str_userName + "】"); //
			}
		}
		if (!sb_allAccrUserNames.toString().equals("")) { //如果您想让被授权能也能接收,必须选择***(已授权***)样子的人员
			if (JOptionPane.showConfirmDialog(this, "您选择的以下用户有授权处理:" + sb_allAccrUserNames.toString() + "\r\n请确认提交是否正确?\r\n", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return null;
			}
		}

		ArrayList al_users = new ArrayList(); //
		ArrayList al_VOs = new ArrayList(); //
		for (int i = 0; i < billVOs_1.length; i++) { //
			if (al_users.contains(billVOs_1[i].getStringValue("userid"))) { //usercode,username
				MessageBox.show(this, "您重复选择了多个名为【" + billVOs_1[i].getStringValue("usercode") + "/" + getRealUserName(billVOs_1[i].getStringValue("username")) + "】的接收人员!\r\n这是不允许的,请重新选择接收人员!"); //
				return null;
			}
			al_users.add(billVOs_1[i].getStringValue("userid")); //
			al_VOs.add(convertBillVOToTaskVO(billVOs_1[i], false)); //
		}

		BillVO[] billVOs_2 = billListPanel_2.getSelectedBillVOs(true); //塞入抄送人员的信息!!!如果列表选择方式是勾选方式，则取得勾选的记录【李春娟/2012-03-01】
		for (int i = 0; i < billVOs_2.length; i++) {
			if (al_users.contains(billVOs_2[i].getStringValue("userid"))) { //usercode,username
				MessageBox.show(this, "您重复选择了多个名为【" + billVOs_2[i].getStringValue("usercode") + "/" + getRealUserName(billVOs_2[i].getStringValue("username")) + "】的抄送人员(或与接收人员重复)!\r\n这是不允许的,请重新选择抄送人员!"); //
				return null;
			}
			al_users.add(billVOs_2[i].getStringValue("userid")); //这里有个bug，以前是al_users.add(billVOs_1[i].getStringValue("userid"));  billvo搞错了，导致报数组越界异常【李春娟/2012-03-15】
			al_VOs.add(convertBillVOToTaskVO(billVOs_2[i], true)); //
		}
		return (DealTaskVO[]) al_VOs.toArray(new DealTaskVO[0]); //
	}

	private String getRealUserName(String _name) {
		if (_name.indexOf("(") > 0) {
			return _name.substring(0, _name.indexOf("(")); //
		} else {
			return _name; //
		}
	}

	/**
	 * 将页面上的BillVO转换成工作流处理的任务VO
	 * @return
	 */
	private DealTaskVO convertBillVOToTaskVO(BillVO _billVO, boolean _isCCTo) {
		DealTaskVO taskVO = new DealTaskVO(); //
		taskVO.setParticipantUserId(_billVO.getStringValue("userid")); //
		taskVO.setParticipantUserCode(_billVO.getStringValue("usercode")); //
		taskVO.setParticipantUserName(_billVO.getStringValue("username")); //

		taskVO.setParticipantUserDeptId(_billVO.getStringValue("userdeptid")); //
		taskVO.setParticipantUserDeptCode(_billVO.getStringValue("userdeptcode")); //
		taskVO.setParticipantUserDeptName(_billVO.getStringValue("userdeptname")); //

		taskVO.setParticipantUserRoleId(_billVO.getStringValue("userroleid")); //
		taskVO.setParticipantUserRoleCode(_billVO.getStringValue("userrolecode")); //
		taskVO.setParticipantUserRoleName(_billVO.getStringValue("userrolename")); //

		taskVO.setAccrUserId(_billVO.getStringValue("accruserid")); //授权人主键.
		taskVO.setAccrUserCode(_billVO.getStringValue("accrusercode")); //授权人编码.
		taskVO.setAccrUserName(_billVO.getStringValue("accrusername")); //授权人名称.

		taskVO.setFromActivityId(_billVO.getStringValue("fromactivityid")); //
		taskVO.setFromActivityName(_billVO.getStringValue("fromactivityname")); //

		taskVO.setTransitionId(_billVO.getStringValue("transitionid")); //
		taskVO.setTransitionCode(_billVO.getStringValue("transitioncode")); //
		taskVO.setTransitionName(_billVO.getStringValue("transitionname")); //
		taskVO.setTransitionDealtype(_billVO.getStringValue("transitionDealtype")); //
		taskVO.setTransitionIntercept(_billVO.getStringValue("transitionIntercept")); //
		taskVO.setTransitionMailSubject(_billVO.getStringValue("transitionMailSubject")); //
		taskVO.setTransitionMailContent(_billVO.getStringValue("transitionMailContent")); //

		taskVO.setCurrActivityId(_billVO.getStringValue("currActivityid")); //
		taskVO.setCurrActivityCode(_billVO.getStringValue("currActivitycode")); //
		taskVO.setCurrActivityName(_billVO.getStringValue("currActivityname")); //
		taskVO.setCurrActivityType(_billVO.getStringValue("currActivitytype")); //
		taskVO.setCurrActivityApproveModel(this.currActivityVO.getApprovemodel()); //审批模式  

		taskVO.setCCTo(_isCCTo); //是否是抄送模式
		return taskVO; //
	}

	public BillListPanel getBillListPanel() {
		return billListPanel_1;
	}

	public BillListPanel getBillListPanel2() {
		return billListPanel_2;
	}

	public void setBillVO(BillVO _billVO) {
		this.billVO = _billVO; //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil(); //
		return tbUtil;
	}

}
