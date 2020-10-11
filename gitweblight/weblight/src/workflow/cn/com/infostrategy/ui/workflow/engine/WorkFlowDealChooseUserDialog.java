package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.ActivityVOComparator;
import cn.com.infostrategy.to.workflow.engine.DealActivityVO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RecordShowDialog;

/**
 * 工作流UI端核心类!!堪称五星级!!★★★★★★,与之关联的另一个重要类是ParticipateUserPanel
 * 即:工作流选择参与者!!使用列表选择!! 这其实是工作流中最重要的一个界面!!也是人性化要求最高的界面!!!
 * 需要进一步大大优化,即需要列出机构树+人,让用户自由选择人,万一人员变动,但角色没有及时同步更新,则还可以用这种办法自由选择人,驱动流程!!
 * @author xch
 *
 */
public class WorkFlowDealChooseUserDialog extends BillDialog implements ActionListener, WindowListener, MouseListener {

	private static final long serialVersionUID = 8323211408012241414L;
	private BillVO billVO = null; //业务数据VO.
	private WFParVO firstTaskVO = null; //任务!!真正的任务!!!
	private boolean isStartStep = false; //是否是启动步骤?
	private DealActivityVO[] dealactivityVOS = null; //

	private JPanel activityButtonsPanel = null; //环节按钮面板!!!

	private JTabbedPane tabbedPane_activity = null; //加载几个环节的!!!
	private JButton btn_confirm, btn_monitor, btn_cancel, btn_return; ////
	private int closetype = -1; //

	private JPopupMenu popMenu = null; //
	private TBUtil tbUtil = null; //

	private ImageIcon imgSelected = UIUtil.getImage("office_036.gif"); //
	private ImageIcon imgUnSelected = UIUtil.getImage("office_138.gif"); //
	private ImageIcon imgSelfCycle = UIUtil.getImage("office_036.gif"); //自循环！！！

	private StringBuilder sb_allHelpMsg = new StringBuilder(); //

	private HashVO[] currActSelfCycleRoleMapVOs = null; //当前环节如果是自循环,且自循环定义角色绑定,则计算出其内容!!
	private String[][] selfCycleCurrRoleCodeName = null; //
	private HashVO hvoCurrWFInfo = null;//当前流程信息,即根据单据类型与业务类型,计算出属于什么流程,该流程中定义了什么拦截器【李春娟/2016-04-20】
	private HashVO hvoCurrActivityInfo = null;//当前环节信息

	/**
	 * 
	 * @param _parent
	 * @param _billVO
	 * @param _firstTaskVO
	 * @param _dealtype
	 * @param _isStartStep
	 * @param _hvoCurrWFInfo 当前流程信息
	 * @param _hvoCurrActivityInfo 当前环节信息【李春娟/2016-04-20】
	 */
	public WorkFlowDealChooseUserDialog(Container _parent, BillVO _billVO, WFParVO _firstTaskVO, String _dealtype, boolean _isStartStep, HashVO _hvoCurrWFInfo, HashVO _hvoCurrActivityInfo) {
		super(_parent, "选择接收者", 860, 600); //nagive
		if (ClientEnvironment.isAdmin()) {
			this.setTitle("选择接收者[强烈提醒:右键页签可以查看参与者定义信息,双击记录可以查看成功参与的原因!]"); //
		}
		this.billVO = _billVO; //
		this.firstTaskVO = _firstTaskVO; //
		this.isStartStep = _isStartStep; //是否是启动环节步骤
		this.firstTaskVO.setStartStep(_isStartStep); //将任务对象也指定成是否是启动步骤！！！
		this.hvoCurrWFInfo = _hvoCurrWFInfo;
		this.hvoCurrActivityInfo = _hvoCurrActivityInfo;
		this.setResizable(true); //
		this.addWindowListener(this);
		initialize();
	}

	/**
	 * 初始化页面..
	 */
	private void initialize() {
		try {
			dealactivityVOS = this.firstTaskVO.getDealActivityVOs(); //从后台服务器返回的所有处理环节的对象!!!
			if (dealactivityVOS.length > 1) { //如果有两个以上的环节
				Arrays.sort(dealactivityVOS, new ActivityVOComparator()); //如果有多个环节,则按环节编码排序!!!但需要把自循环的永远放在第一位!!
			}
			boolean isTwoStepCommit = getTBUtil().getSysOptionBooleanValue("工作流提交时是否先环节后人员", false); //是否两步提交,兴业银行等客户喜欢先选环节,后选人! 像他们OA一样!因为这样一来不容易选错环节! 而且不会可以有直接提交给机构的概念!!
			if (dealactivityVOS.length > 1 && isTwoStepCommit) { //如果是多个环节,而且是分两步提交,则使用层,然后先创建环节按钮,即如果只有一个环节则没必须分两步了,因为有意义!!
				this.getContentPane().setLayout(new BorderLayout()); //
				this.getContentPane().add(getActivityButtonPanel(dealactivityVOS)); //
			} else { //传统的多页签!!!
				this.getContentPane().setLayout(new BorderLayout()); //
				this.getContentPane().add(getActivityTabbedPanel(dealactivityVOS, null, false, false), BorderLayout.CENTER); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private WLTButton getClickActivityBtn(String _text, ImageIcon _icon, int _index) {
		WLTButton btn = new WLTButton(_text, _icon); //
		btn.setPreferredSize(new Dimension(250, 45)); //在太平项目发现如果部门名称太长会换行，加上环节名称，三行占不下，故按钮加宽【李春娟/2019-01-07】
		btn.putClientProperty("tab_index", new Integer(_index)); //
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickActivityBtn((WLTButton) e.getSource()); //点击事件
			}
		});
		return btn; //
	}

	/**
	 * 取得各个环节按钮面板!! 即分两步提交时,第一步先显示各个环节大按钮,点击后再显示该环节的参与者!!
	 * @param _dealactivityVOs
	 * @return
	 */
	private JPanel getActivityButtonPanel(DealActivityVO[] _dealactivityVOs) { //
		if (activityButtonsPanel != null) {
			return activityButtonsPanel; //
		}
		//遍历所有环节！！
		int li_count = 0; //

		sb_allHelpMsg.append("\r\n\r\n◆◆◆◆◆◆◆◆◆◆计算待办环节逻辑[【如果点击返回,可能会出现多次!】◆◆◆◆◆◆◆◆◆◆\r\n\r\n"); //

		ArrayList al_selfCycleBtn = new ArrayList(); //自循环内部流转的按钮
		ArrayList al_outoffBtn = new ArrayList(); //出去的按钮
		for (int i = 0; i < _dealactivityVOs.length; i++) { //遍历所有环节!
			//如果没有数据,且不是结束类型!
			if ((_dealactivityVOs[i].getDealTaskVOs() == null || _dealactivityVOs[i].getDealTaskVOs().length <= 0) && (!"END".equalsIgnoreCase(_dealactivityVOs[i].getActivityType()))) {
				if (_dealactivityVOs[i].getWnParam() != null && "Y".equalsIgnoreCase((String) _dealactivityVOs[i].getWnParam().get("无参与者是否隐藏"))) {
					continue; //如果没有参与者,则跳过!
				}
			}

			if (i != 0) {
				sb_allHelpMsg.append("\r\n\r\n"); //
			}
			sb_allHelpMsg.append("计算第[" + (i + 1) + "]个待办环节:\r\n"); ////

			//System.out.println("环节【" + _dealactivityVOs[i].getCurrActivityName(false) + "】的扩展参数：" + _dealactivityVOs[i].getFromtransitionExtConfMap()); //  //
			//如果当前环节是自循,且这个环节其实就是本自循环环节！！！
			if (this.firstTaskVO.getCurractivityIsSelfCycle() && _dealactivityVOs[i].getSortIndex() == 0) { //如果当前是自循环！！！
				String[][] str_selfLinkRoles = getSelfCycleRoleLinkMaps(); //取得登录人员可以走的所有角色!!!
				if (str_selfLinkRoles != null && str_selfLinkRoles.length > 0) { //要计算出本自己循环到底可以走几个内部流程的角色!
					//System.out.println("循环加入[" + str_selfLinkRoles.length + "]个按钮"); //
					for (int j = 0; j < str_selfLinkRoles.length; j++) {
						WLTButton btn = getClickActivityBtn(str_selfLinkRoles[j][3], imgSelfCycle, i); //按钮名称就是角色名称!
						btn.putClientProperty("SelfCycleBtn", "Y"); //
						btn.putClientProperty("BindSelfCycleFromRoleCode", str_selfLinkRoles[j][0]); //绑定的角色名称！！
						btn.putClientProperty("BindSelfCycleFromRoleName", str_selfLinkRoles[j][1]); //绑定的角色名称！！
						btn.putClientProperty("BindSelfCycleToRoleCode", str_selfLinkRoles[j][2]); //绑定的角色名称！！
						btn.putClientProperty("BindSelfCycleToRoleName", str_selfLinkRoles[j][3]); //绑定的角色名称！！

						if (ClientEnvironment.isAdmin()) { //如果是管理身份
							StringBuilder sb_tip = new StringBuilder(); //
							sb_tip.append("<html>"); //
							sb_tip.append(_dealactivityVOs[i].getActivityBelongDeptGroupName() + "-" + _dealactivityVOs[i].getActivityName() + "<br>"); //
							sb_tip.append("来源角色:【" + str_selfLinkRoles[j][0] + "/" + str_selfLinkRoles[j][1] + "】<br>"); //
							sb_tip.append("目标角色:【" + str_selfLinkRoles[j][2] + "/" + str_selfLinkRoles[j][3] + "】<br>"); //
							sb_tip.append("</html>"); //
							btn.setToolTipText(sb_tip.toString()); //
						} else {
							btn.setToolTipText(_dealactivityVOs[i].getCurrActivityName(true)); //
						}
						al_selfCycleBtn.add(btn); //
						li_count++; //
					}
				} else { //如果没找到,则不加入任何按钮!即根据计算,本角色没有一个可以提交的!!!即老早以前的方式!!
					WLTButton btn = getClickActivityBtn(_dealactivityVOs[i].getCurrActivityName(true), imgSelfCycle, i); //imgUnSelected
					btn.putClientProperty("SelfCycleBtn", "Y"); //

					btn.setToolTipText("这是个自循环环节,但没有定义内部流程的角色映射流程图!"); //
					al_selfCycleBtn.add(btn); //
					li_count++; //
				}

				if (isStartStep) { //如果这个环节正好是启动环节,则其他的就不出去了!
					sb_allHelpMsg.append("因为当前是启动环节,且又是自循环,所以后面的其他出口直接不输出了..\r\n"); //
					break; //其他的出去的线就不做的!即只出来本部门的!
				}
			} else { //如果是出去的环节!即不是内部流转的!!
				//如果上面计算出来的自循环对应的"小流程"中的所有角色中如果有end限制的,则做如此控制:只有当前的角色是end中指定的,则才显示其他环节!!!!
				//否则统统不显示其他环节!
				HashSet hst_ends = getSelfCycleRoleWFEnds(); //看有多少是结束环节
				if (hst_ends != null && hst_ends.size() > 0) { //如果发现竟然定义了结束环节!则判断本人环节是否存在于这些结束环节中! 如果在! 说明,只有可以出去! 如果没有! 则直接
					String[] str_ayy = (String[]) hst_ends.toArray(new String[0]); //
					String str_endname = ""; //
					for (int k = 0; k < str_ayy.length; k++) {
						str_endname = str_endname + "【" + str_ayy[k] + "】";
					}
					sb_allHelpMsg.append("当前环节[" + _dealactivityVOs[i].getCurrActivityName(false) + "]是自循环,且绑定的角色映射图中有结束的角色[" + str_endname + "]环节!!则进行计算..." + "\r\n"); //
					boolean isInEnd = isMyInEnds(hst_ends); //是否在某中!
					if (!isInEnd) { ////如果指定了结束的限制,而我又不在其中!!则直接退出!
						sb_allHelpMsg.append("发现绑定的角色映射图中有结束的角色环节!!而我不在其中,说明我是没有\"资格\"出去的,所以直接剔除可以出去的路线.." + "\r\n"); //
						break; //直接退出,即所有按钮都不输出!!
					} else {
						sb_allHelpMsg.append("发现绑定的角色映射图中有结束的角色环节!!而我在其中,说明我是有资格出去的,则继续进行连线上的条件计算.." + "\r\n"); //
					}
				}

				WLTButton outPutButton = null; //
				//如果决定显示其他环节,则再计算线上面的条件!!即下面的这段逻辑!!!即如果结束环节没定义或者我是在结束环节当中!即"小流程"中一旦指定可以结束的"角色",则就是个总开关,一下子就关掉了!!!
				if (_dealactivityVOs[i].getFromtransitionExtConfMap() != null && _dealactivityVOs[i].getFromtransitionExtConfMap().containsKey("源头环节出去的二次角色条件")) { //
					String str_outPutRoleCons = (String) _dealactivityVOs[i].getFromtransitionExtConfMap().get("源头环节出去的二次角色条件"); //
					if (str_outPutRoleCons != null && !str_outPutRoleCons.trim().equals("")) {
						String[] str_roleConItems = getTBUtil().split(str_outPutRoleCons, "/"); //可能有多个!即这根线可能不通！
						if (uiSecondCheckActivityByRoleName(str_roleConItems)) { //我必须有这种角色才出来！
							outPutButton = getClickActivityBtn(_dealactivityVOs[i].getCurrActivityName(true), imgUnSelected, i); //
							if (ClientEnvironment.isAdmin()) {
								outPutButton.setToolTipText("该环节有前台角色条件[" + str_outPutRoleCons + "],我是因为匹配成功才加入的!"); //
							}
						} else { //补删除了!
							String str_helpText = "很遗憾，环节【" + _dealactivityVOs[i].getCurrActivityName(false) + "】在后台已经成功匹配,但因为连线上有角色条件限制[" + str_outPutRoleCons + "],而我又没有这种角色,所以在前台被二次剔除了"; //
							sb_allHelpMsg.append(str_helpText + "\r\n"); //
						}
					} else { //如果没有定义条件!则直接出来！
						sb_allHelpMsg.append("连线上二次过滤条件为空,则直接加入\r\n"); //
						outPutButton = getClickActivityBtn(_dealactivityVOs[i].getCurrActivityName(true), imgUnSelected, i); //
					}
				} else { //如果没有定义条件则加入!//
					sb_allHelpMsg.append("连线上没有二次过滤条件,则直接加入\r\n"); //
					outPutButton = getClickActivityBtn(_dealactivityVOs[i].getCurrActivityName(true), imgUnSelected, i); //
				}

				//
				if (outPutButton != null) {
					//如果是结束环节,则要特别处理一下!
					if ("END".equalsIgnoreCase(_dealactivityVOs[i].getActivityType())) { //如果是结束环节
						outPutButton.setText(_dealactivityVOs[i].getActivityName()); //如果是结束环节只显示名称!邮储重庆分行"李承蔚"认为结束环节只要名称,不要组名!!!
						outPutButton.setForeground(Color.RED); //
						if (_dealactivityVOs[i].getDealTaskVOs() == null || _dealactivityVOs[i].getDealTaskVOs().length <= 0) { //如果结束环节没有参与人,则
							outPutButton.putClientProperty("DirEndWF", "Y"); //是直接结束的按钮
						}
					}

					//如果目标环节也是个自循环,则打上自循环对应的小流程!!!
					if (_dealactivityVOs[i].isActivityIselfcycle() && _dealactivityVOs[i].getActivitySelfcyclerolemap() != null) {
						outPutButton.putClientProperty("NextStepSelfCycleRoleMapWF", _dealactivityVOs[i].getActivitySelfcyclerolemap()); //
					}

					al_outoffBtn.add(outPutButton); //
					li_count++; //

				}
			}
		}

		boolean isMultiChecked = false; //是否是多选的勾选方式???即兵分多路的方式!!!

		activityButtonsPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		if (al_selfCycleBtn.size() > 0 && al_outoffBtn.size() > 0) { //如果两个都有内容!!要搞成两片,左边是内部流转的,右边是出去的!
			JScrollPane left_btnpanel = getBtnsPanel(al_selfCycleBtn, false); //左边的自循环的按钮
			JScrollPane right_btnpanel = getBtnsPanel(al_outoffBtn, isMultiChecked); //右边的出去的

			JPanel btn_left = WLTPanel.createDefaultPanel(new BorderLayout()); //
			btn_left.add(left_btnpanel); //
			JLabel label_left = new JLabel("【内部流转的步骤】", SwingConstants.CENTER); //
			label_left.setForeground(Color.RED); //
			label_left.setPreferredSize(new Dimension(2000, 25)); //
			btn_left.add(label_left, BorderLayout.NORTH); //

			JPanel btn_right = WLTPanel.createDefaultPanel(new BorderLayout()); //
			btn_right.add(right_btnpanel); //
			JLabel label_right = new JLabel("【出去的步骤】", SwingConstants.CENTER); //
			label_right.setForeground(Color.RED); //
			label_right.setPreferredSize(new Dimension(2000, 25)); //
			btn_right.add(label_right, BorderLayout.NORTH); //

			WLTSplitPane split = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, btn_left, btn_right); //整体布局
			split.setDividerLocation(420); //
			activityButtonsPanel.add(split, BorderLayout.CENTER); //
		} else { //如果只有一个有内容!
			ArrayList al_allBtns = new ArrayList(); //
			al_allBtns.addAll(al_selfCycleBtn); //如果为空,自然等于没做处理!
			al_allBtns.addAll(al_outoffBtn); //如果为空,自然等于没做处理!
			activityButtonsPanel.add(getBtnsPanel(al_allBtns, false), BorderLayout.CENTER); //
		}

		activityButtonsPanel.add(getMsgPanel(), BorderLayout.NORTH); //
		activityButtonsPanel.add(getSouthPanel(false, false), BorderLayout.SOUTH); //
		return activityButtonsPanel; //
	}

	private JScrollPane getBtnsPanel(ArrayList _list, boolean _isChecked) {
		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20)); //
		btnPanel.setOpaque(false); //透明!!!
		//btnPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE));  //去掉注释后,为了调试时显示用

		if (_list.size() > 0) {
			if (!_isChecked || _list.size() == 1) { //如果是非勾选方式,或者是只有一个!因为就一个也搞成勾选模式是没有意义的!
				for (int i = 0; i < _list.size(); i++) {
					WLTButton btnItem = (WLTButton) _list.get(i); //
					btnPanel.add(btnItem); //
				}
			} else { //如果是勾选方式的!!!
				JCheckBox[] checks = new JCheckBox[_list.size()]; //
				for (int i = 0; i < _list.size(); i++) {
					WLTButton btnItem = (WLTButton) _list.get(i); //得到按钮!!!
					btnItem.putClientProperty("BindCheckBoxs", checks); //绑定的所有勾选框!!!

					checks[i] = new JCheckBox(); //勾选框
					checks[i].setOpaque(false); //透明
					checks[i].setFocusable(false); //
					checks[i].putClientProperty("tab_index", (Integer) btnItem.getClientProperty("tab_index")); //在勾选框上!!
					checks[i].putClientProperty("NextStepSelfCycleRoleMapWF", (String) btnItem.getClientProperty("NextStepSelfCycleRoleMapWF"));

					JPanel tmpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); //再搞个面板
					tmpPanel.setOpaque(false); //透明!!!
					tmpPanel.add(checks[i]);
					tmpPanel.add(btnItem); //

					btnPanel.add(tmpPanel); //
				}
			}

		}

		btnPanel.setPreferredSize(new Dimension(300, 65 * _list.size() + 50)); //

		JPanel btnContainerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)); //整个内容面板!
		btnContainerPanel.setOpaque(false); //透明!!
		btnContainerPanel.add(btnPanel); //
		//btnContainerPanel.setBorder(BorderFactory.createLineBorder(Color.RED));  //去掉注释后,为了调试时显示用

		JScrollPane scrollPanel = new JScrollPane(btnContainerPanel); //
		scrollPanel.setOpaque(false); //透明!!
		scrollPanel.getViewport().setOpaque(false); //透明!!

		return scrollPanel; //
	}

	private String getToRoleName() {
		return this.firstTaskVO.getDealpooltask_selfcycle_torolename(); //要使用数据库中的!
	}

	private boolean isMyInEnds(HashSet _hst) {
		String str_toRoleName = getToRoleName(); //假设我是综合员!
		if (str_toRoleName != null) {
			if (_hst.contains(str_toRoleName)) { //如果在其中
				//sb_allHelpMsg.append("isInEnd计算,我从数据库中取得的roleName[" + str_toRoleName + "]匹配上了...\r\n"); //
				return true;
			} else {
				//sb_allHelpMsg.append("isInEnd计算,我从数据库中取得的roleName[" + str_toRoleName + "]匹配不上...\r\n"); //
				return false; //
			}
		} else {
			String[] str_myAllRoles = ClientEnvironment.getCurrLoginUserVO().getAllRoleNames(); //角色名称!
			if (str_myAllRoles == null || str_myAllRoles.length <= 0) {
				return false;
			}

			String[] str_endRoles = (String[]) _hst.toArray(new String[0]); //
			for (int i = 0; i < str_myAllRoles.length; i++) {
				for (int j = 0; j < str_endRoles.length; j++) {
					if (str_myAllRoles[i].indexOf(str_endRoles[j]) >= 0) { //如果我的角色有此
						sb_allHelpMsg.append("isInEnd计算,我的一个环节=[" + str_myAllRoles[i] + "]匹配上了...\r\n"); //
						return true; //
					}
				}
			}
		}
		return false; //
	}

	//自循环计算时,必须要知道自己到底是谁,如果从上一次能得到,则用上一次的,如果上一次没有,则登录人员所有角色中取!!
	//从登录人员所有中取时,还要做一个处理,就是只返回第一个角色!!!这将解决如果一个人同时有"处室负责人"与"一般员工"时,只当处室负责人!!
	private String[][] getSelfCycleCurrRoleCodeName() {
		if (selfCycleCurrRoleCodeName != null) { //为了得高性能,直接返回!
			return selfCycleCurrRoleCodeName;
		}

		String str_dbRoleCode = this.firstTaskVO.getDealpooltask_selfcycle_torolecode(); //
		String str_dbRoleName = this.firstTaskVO.getDealpooltask_selfcycle_torolename(); //
		if (str_dbRoleCode != null) { //如果数据库中存在,则直接返回!!!
			selfCycleCurrRoleCodeName = new String[][] { { str_dbRoleCode, str_dbRoleName } }; //
			sb_allHelpMsg.append("★从数据库中得到当前人的匹配于自流转小流程中的唯一角色名[" + str_dbRoleCode + "][" + str_dbRoleName + "]\r\n"); //
			return selfCycleCurrRoleCodeName; //
		}

		String[][] str_myAllRoles = ClientEnvironment.getCurrLoginUserVO().getAllRoleCodeNames(); //角色名称!
		if (str_myAllRoles == null || str_myAllRoles.length <= 0) {
			return new String[0][0]; //
		}

		String[] str_orderCons = new String[] { "部门负责人", "处室负责人", "综合员", "员工" }; //排序条件
		if (str_orderCons != null) { //如果有排序条件
			for (int i = 0; i < str_orderCons.length; i++) {
				for (int j = 0; j < str_myAllRoles.length; j++) {
					if (str_myAllRoles[j][1].indexOf(str_orderCons[i]) >= 0) { //如果有多个角色,则只算第一个!这样就不会出现因为当前人有多个角色时,出来一堆!!!!
						selfCycleCurrRoleCodeName = new String[][] { { str_myAllRoles[j][0], str_myAllRoles[j][1] } }; //
						sb_allHelpMsg.append("★从登录人的所有角色中找到最高优先级的角色:[" + str_myAllRoles[j][0] + "][" + str_myAllRoles[j][1] + "]\r\n"); //
						return selfCycleCurrRoleCodeName; //
					}
				}
			}
		}

		sb_allHelpMsg.append("★返回登录人的所有角色!!一共[" + str_myAllRoles.length + "]个");
		selfCycleCurrRoleCodeName = str_myAllRoles; //
		return selfCycleCurrRoleCodeName; //
	}

	//取得登录人员自循环时,角色映射！！
	private String[][] getSelfCycleRoleLinkMaps() {
		try {
			//逻辑是:先看当前环节自循环绑定的角色映射的流程图!
			//然后查查询这个流程图所有环节,然后检索每一根线,看线的来源与线的结束环节是什么!然后来源环节就key,结束环节就是value
			//然后找出本人所有角色,然后遍历两个数组,只要我的角色中有一个indexOf(图中的key)>0,则加入一个Map! 最后返回这个Map的所有Value数组
			String str_roleMapProcessid = this.firstTaskVO.getCurractivitySelfCycleRoleMap(); //当前环节的角色映射的环节！！
			if (str_roleMapProcessid == null || str_roleMapProcessid.trim().equals("")) {
				return null; //如果没有定义,则直接返回!!
			}

			//这里应该不是根据当前登录人的角色来判断!! 而是根据流程引擎数据库中的当前任务"torolename"字段来判断!!
			//即前面的提交给我时,我虽然有很多角色,比如既有"综合员"又有"处室负责人",但当时前者点击的是"处室负责人"!!!则我这里就应该认为是只有"处室负责人"这一个角色了!而不是多个角色!!!
			//当然,如果前者是从外面环节来的,然后选中了我,这时torolename是为空的! 这时只能还是使用登录人所有角色计算!!

			//但在生成 各个待办任务时,如果发现出去的环节就是一个自循环,且通过查询数据库得到了启动环节,这时应该把那些任务打上rolename标记,这时只要启动环节只有一个(比如综合员)!!则仍然能准确计算出当前应该出来什么环节的!
			//即只出来综合员指定的路线,而不会出现,因为我同时有一般员工与综合员,结果出来了一堆!!!  kkkkkkkkkkk

			String[][] str_currRoleCodeNames = getSelfCycleCurrRoleCodeName(); //假设我是综合员!
			HashVO[] hvs_roleMapVOs = getCurrActivitySelfCycleMaps(); //取得当前环节VO,即在小流程图中定义的"什么角色"可以往什么角色走!!!
			if (hvs_roleMapVOs.length <= 0) { //可能当前环节根本就没有定义!!
				return null; //
			}
			ArrayList al_list = new ArrayList(); //
			for (int i = 0; i < hvs_roleMapVOs.length; i++) { //遍历!
				boolean isFind = false; //是否发现
				for (int j = 0; j < str_currRoleCodeNames.length; j++) { //
					if (str_currRoleCodeNames[j][1].indexOf(hvs_roleMapVOs[i].getStringValue("fromname")) >= 0) { //我的角色中有这个一个,比如我的角色叫【二级分行处室负责人】,环节名称叫【处室负责人】
						String str_fromCode = hvs_roleMapVOs[i].getStringValue("fromcode"); //应该就是流程图中有名称这样下次匹配才准确!!
						String str_fromName = hvs_roleMapVOs[i].getStringValue("fromname");
						String str_toCode = hvs_roleMapVOs[i].getStringValue("tocode"); //必须是流程图中的名称!
						String str_toName = hvs_roleMapVOs[i].getStringValue("toname"); //
						al_list.add(new String[] { str_fromCode, str_fromName, str_toCode, str_toName }); //

						String str_helpText = "自循环中进行角色映射计算,本人角色[" + str_currRoleCodeNames[j][1] + "],因为匹配上角色[" + hvs_roleMapVOs[i].getStringValue("fromname") + "],所以找到下一个角色[" + hvs_roleMapVOs[i].getStringValue("toname") + "]"; //
						sb_allHelpMsg.append(str_helpText + "\r\n"); //这里一定要有一个角色之间的排序!即如果一个人同时具有【处室负责人】【综合员】【一般员工】,则认为首先是处室负责人！
						break; //
					}
				}
			}
			String[][] str_rt = new String[al_list.size()][4]; //
			for (int i = 0; i < str_rt.length; i++) {
				str_rt[i] = (String[]) al_list.get(i); //
			}
			return str_rt; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	//取得所有结束的环节!!
	private HashSet getSelfCycleRoleWFEnds() {
		try {
			HashVO[] hvs = getCurrActivitySelfCycleMaps(); //
			if (hvs.length <= 0) {
				return null; //
			}
			HashSet hst = new HashSet(); //
			for (int i = 0; i < hvs.length; i++) {
				if (hvs[i].getStringValue("totype", "").equalsIgnoreCase("END") || hvs[i].getBooleanValue("toisend", false)) { //如果End类型,或有半路结束!则加入!
					hst.add(hvs[i].getStringValue("toname")); //
				}
			}
			return hst; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	//计算出当前环节的自循环定义!为了保证只取一次!
	private HashVO[] getCurrActivitySelfCycleMaps() throws Exception { //
		if (currActSelfCycleRoleMapVOs != null) {
			return currActSelfCycleRoleMapVOs; //
		}
		String str_roleMapProcessid = this.firstTaskVO.getCurractivitySelfCycleRoleMap(); //当前环节的角色映射的环节！！
		if (str_roleMapProcessid == null || str_roleMapProcessid.trim().equals("")) {
			return new HashVO[0]; //如果没有定义,则直接返回!!
		}
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select t1.fromactivity,t2.code fromcode,t2.wfname fromname,t2.activitytype fromtype,t2.canhalfstart fromisstart,t2.canhalfend fromisend,"); //
		sb_sql.append("t1.toactivity,t3.code tocode,t3.wfname toname,t3.activitytype totype,t3.canhalfstart toisstart,t3.canhalfend toisend ");
		sb_sql.append("from pub_wf_transition t1,pub_wf_activity t2,pub_wf_activity t3 ");
		sb_sql.append("where t1.processid=" + str_roleMapProcessid + " ");
		sb_sql.append("and t1.fromactivity = t2.id ");
		sb_sql.append("and t1.toactivity = t3.id "); //
		sb_sql.append("order by tocode "); //排序!

		HashVO[] tmpHvs = UIUtil.getHashVoArrayByDS(null, sb_sql.toString()); //
		if (tmpHvs != null && tmpHvs.length > 0) {
			for (int i = 0; i < tmpHvs.length; i++) {
				String str_fromname = tmpHvs[i].getStringValue("fromname"); //
				String str_toname = tmpHvs[i].getStringValue("toname"); //
				tmpHvs[i].setAttributeValue("fromname", trimAndReplace(str_fromname)); //重新赋值!
				tmpHvs[i].setAttributeValue("toname", trimAndReplace(str_toname)); //重新赋值!
			}
		}
		currActSelfCycleRoleMapVOs = tmpHvs; //
		return currActSelfCycleRoleMapVOs; //
	}

	//将环节上的换行符等去掉!
	private String trimAndReplace(String _str) {
		_str = _str.trim(); //
		_str = getTBUtil().replaceAll(_str, "\r", ""); //
		_str = getTBUtil().replaceAll(_str, "\n", ""); //
		_str = getTBUtil().replaceAll(_str, "（", "("); //
		_str = getTBUtil().replaceAll(_str, "）", ")"); //
		_str = getTBUtil().replaceAll(_str, " ", ""); //
		return _str;
	}

	//计算小流程中的启动环节!!!
	private ArrayList getStartRoles(String _processId) { //取得
		try {
			String str_sql = "select wfname from pub_wf_activity where processid=" + _processId + " and (activitytype='START' or canhalfstart='Y')"; //
			String[] str_roles = UIUtil.getStringArrayFirstColByDS(null, str_sql); //
			if (str_roles != null && str_roles.length > 0) {
				for (int i = 0; i < str_roles.length; i++) {
					str_roles[i] = trimAndReplace(str_roles[i]); //一定要替换一下!因为有人在环节名称上换行!!
				}
			}
			return new ArrayList(Arrays.asList(str_roles)); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	private String getStrByList(ArrayList _list) {
		if (_list == null) {
			return null;
		}
		StringBuilder sb_text = new StringBuilder(); //
		for (int i = 0; i < _list.size(); i++) {
			sb_text.append("【" + _list.get(i) + "】"); //
		}
		return sb_text.toString(); //
	}

	/**
	 * 点击一第一步的一个环节按钮!!!!
	 * @param _btn
	 */
	protected void onClickActivityBtn(WLTButton _btn) {
		boolean isDirEndWF = ("Y".equals(_btn.getClientProperty("DirEndWF")) ? true : false); //
		if (isDirEndWF) { //如果是直接结束的按钮,则直接结束流程!
			onEndWF(); //
			return; //
		}

		Integer li_index = (Integer) _btn.getClientProperty("tab_index"); ///还要取得计算出来的角色名称!
		boolean isSelfBtn = ("Y".equals(_btn.getClientProperty("SelfCycleBtn")) ? true : false);//""
		String str_bindFromRoleCode = (String) _btn.getClientProperty("BindSelfCycleFromRoleCode"); //绑定的角色名称！！！
		String str_bindFromRoleName = (String) _btn.getClientProperty("BindSelfCycleFromRoleName"); //绑定的角色名称！！！
		String str_bindToRoleCode = (String) _btn.getClientProperty("BindSelfCycleToRoleCode"); //绑定的角色名称！！！
		String str_bindToRoleName = (String) _btn.getClientProperty("BindSelfCycleToRoleName"); //绑定的角色名称！！！

		String str_filerRoleName = null; //

		DealActivityVO[] choosedActVOs = null; //选中环节VO
		if (isSelfBtn) { //如果发现是点击的内部自循环的按钮!!这样提交时后台就会塞入数据库!
			this.firstTaskVO.setSecondIsSelfcycleclick(true); //
			this.firstTaskVO.setSecondSelfcycle_fromrolecode(str_bindFromRoleCode); //
			this.firstTaskVO.setSecondSelfcycle_fromrolename(str_bindFromRoleName); //
			this.firstTaskVO.setSecondSelfcycle_torolecode(str_bindToRoleCode); //
			this.firstTaskVO.setSecondSelfcycle_torolename(str_bindToRoleName); //
			if (str_bindFromRoleCode != null) { //如果有确绑定了某个角色,才进行过滤!
				str_filerRoleName = _btn.getText(); //直接是按钮的名称!!!
			}

			DealActivityVO choosedActivityVO = dealactivityVOS[li_index];
			if (choosedActivityVO.getWnParam() != null) {
				String str_warn = (String) choosedActivityVO.getWnParam().get("选择提醒"); //
				if (str_warn != null && !str_warn.trim().equals("")) {
					MessageBox.showWarn(this, str_warn);
				}
			}
			choosedActVOs = new DealActivityVO[] { dealactivityVOS[li_index] }; //
		} else { //如果是出去的!
			this.firstTaskVO.setSecondIsSelfcycleclick(false); //
			this.firstTaskVO.setSecondSelfcycle_fromrolecode(null); //
			this.firstTaskVO.setSecondSelfcycle_fromrolename(null); //
			this.firstTaskVO.setSecondSelfcycle_torolecode(null); //
			this.firstTaskVO.setSecondSelfcycle_torolename(null); //

			JCheckBox[] checks = (JCheckBox[]) _btn.getClientProperty("BindCheckBoxs"); //绑定的勾选框!
			if (checks != null) { //如果发现是可以兵分多路出去的,即分支出发!!
				JCheckBox thisBindChecks = findThisCheck(checks, li_index); //
				thisBindChecks.setSelected(true); //

				JCheckBox[] allCheckeds = findAllCheckeds(checks); //找到所有勾选上的勾选框!!!
				if (!MessageBox.confirm(this, "此步骤支持多路提交模式,您现在一共选择了【" + allCheckeds.length + "】条路,\r\n请确认要提交至这些分支吗?")) { //
					return;
				}

				choosedActVOs = new DealActivityVO[allCheckeds.length]; //
				for (int i = 0; i < choosedActVOs.length; i++) { ////遍历所有...
					int li_index_item = (Integer) allCheckeds[i].getClientProperty("tab_index"); //
					String str_NextStepSelfCycleRoleMapWF = (String) allCheckeds[i].getClientProperty("NextStepSelfCycleRoleMapWF"); //
					choosedActVOs[i] = dealactivityVOS[li_index_item]; //
				}
			} else {
				//下一步的自循环是否有??
				String str_NextStepSelfCycleRoleMapWF = (String) _btn.getClientProperty("NextStepSelfCycleRoleMapWF"); //
				if (str_NextStepSelfCycleRoleMapWF != null) {
					ArrayList al_nextStartRoles = getStartRoles(str_NextStepSelfCycleRoleMapWF); //查询数据库,看是否有启动环节!!!
					if (al_nextStartRoles != null && al_nextStartRoles.size() > 0) {
						al_nextStartRoles.add("取消"); //
						String[] str_nextRoles = (String[]) al_nextStartRoles.toArray(new String[0]); //
						for (int i = 0; i < str_nextRoles.length; i++) {
							if (i == str_nextRoles.length - 1) {
								str_nextRoles[i] = "[office_078.gif]" + str_nextRoles[i]; //取消按钮
							} else {
								str_nextRoles[i] = "[office_036.gif]" + str_nextRoles[i]; //
							}
						}

						int li_return = MessageBox.showOptionDialog(this, "您确认要向该部门提交吗?\r\n根据流程设计,你只能提交该部门的以下角色,请注意正确选择!", "提示", str_nextRoles); //
						if (li_return < str_nextRoles.length - 1 && li_return >= 0) { //3+1=4    012 bug//sunfujun//20130515
							//MessageBox.show(this, "你选中的是[" + str_nextRoles[li_return] + "]"); //
							String str_chooseBtn = str_nextRoles[li_return]; //
							if (str_chooseBtn.indexOf("]") > 0) {
								str_chooseBtn = str_chooseBtn.substring(str_chooseBtn.indexOf("]") + 1, str_chooseBtn.length()); //去掉前面的"[office_078.gif]"
							}
							str_filerRoleName = str_chooseBtn; //
						} else { //如果
							return; //
						}
					} //如果没找到启动环节!!!则参与者为空!即找出原来所有!
				}

				DealActivityVO choosedActivityVO = dealactivityVOS[li_index]; //选中的环节VO...
				if (choosedActivityVO.getWnParam() != null) {
					String str_warn = (String) choosedActivityVO.getWnParam().get("选择提醒"); //
					if (str_warn != null && !str_warn.trim().equals("")) {
						MessageBox.showWarn(this, str_warn);
					}
				}
				choosedActVOs = new DealActivityVO[] { dealactivityVOS[li_index] }; //
			}
		}

		this.getContentPane().removeAll(); //把中间的内容去掉！
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getActivityTabbedPanel(choosedActVOs, str_filerRoleName, true, isSelfBtn), BorderLayout.CENTER); //
		this.getContentPane().validate(); //
		this.getContentPane().repaint(); //
	}

	private JCheckBox findThisCheck(JCheckBox[] _checks, Integer _index) {
		for (int i = 0; i < _checks.length; i++) {
			Integer li_index = (Integer) _checks[i].getClientProperty("tab_index"); //
			if (li_index.equals(_index)) {
				return _checks[i]; //
			}
		}
		return null; //
	}

	private JCheckBox[] findAllCheckeds(JCheckBox[] _checks) {
		ArrayList al_checks = new ArrayList(); //
		for (int i = 0; i < _checks.length; i++) {
			if (_checks[i].isSelected()) {
				al_checks.add(_checks[i]); //
			}
		}

		return (JCheckBox[]) al_checks.toArray(new JCheckBox[0]); //
	}

	private int getCheckCount(JCheckBox[] _checks) {
		int li_count = 0; //
		for (int i = 0; i < _checks.length; i++) {
			if (_checks[i].isSelected()) {
				li_count++; //
			}
		}
		return li_count;
	}

	/**
	 * 返回上一步!!!
	 */
	private void onRetrun() {
		this.getContentPane().removeAll(); //
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getActivityButtonPanel(dealactivityVOS), BorderLayout.CENTER); //
		this.getContentPane().validate(); //
		this.getContentPane().repaint(); //
	}

	/**
	 * 取得各个环节的多页签的面板!!!
	 * 一开始是永远都是个多页签,后来改成了先点环节按钮,后选该环节中的参与者!!
	 * 再后来增加了兵分多路,可以勾选多路!!
	 * @param _dealactivityVOs 
	 * @return
	 */
	private JPanel getActivityTabbedPanel(DealActivityVO[] _dealactivityVOs, String _filterRoleName, boolean _isReturn, boolean _isSelfBtn) {
		JPanel contentPanel = new JPanel(new BorderLayout()); // 

		contentPanel.add(getMsgPanel(), BorderLayout.NORTH); //str_filerRoleName
		tabbedPane_activity = new JTabbedPane(); //多页签!!!
		tabbedPane_activity.setFocusable(false); //

		JPanel[] panel_activitys = new JPanel[_dealactivityVOs.length]; //环节的面板数
		ParticipateUserPanel[] parUserPanels = new ParticipateUserPanel[_dealactivityVOs.length]; //创建多个环节面板中人员面板!

		//遍历个各环节!!
		for (int i = 0; i < _dealactivityVOs.length; i++) { //遍历每个环节
			DealTaskVO[] taskVOS = _dealactivityVOs[i].getDealTaskVOs(); //该环节所有的参与任务!!
			parUserPanels[i] = new ParticipateUserPanel(_dealactivityVOs[i], billVO, hvoCurrWFInfo, hvoCurrActivityInfo, firstTaskVO); //使用该环节信息创建参与者面板,即一个环节一个参与者面板!!
			//parUserPanels[i].getBillListPanel().setToolbarVisiable(false); //隐藏状态栏!!
			if (_dealactivityVOs[i].getShowparticimode() != null && _dealactivityVOs[i].getShowparticimode().equals("3")) {
				parUserPanels[i].getBillListPanel().setItemVisible("iseverprocessed", true); //是否曾经走过
			} else {
				parUserPanels[i].getBillListPanel().setItemVisible("iseverprocessed", false); //
			}

			String str_oldHelp = _dealactivityVOs[i].getParticiptMsg(); //
			String str_mark = "<br><br>※※※点击后计算逻辑※※※<br>"; //
			int li_pos = str_oldHelp.indexOf(str_mark); //
			if (li_pos > 0) { //如果曾经加过!,则裁掉再加!!!
				_dealactivityVOs[i].setParticiptMsg(str_oldHelp.substring(0, li_pos) + str_mark); //
			} else {
				_dealactivityVOs[i].setParticiptMsg(str_oldHelp + str_mark); //
			}

			//如果是自循环过滤的,或线上面定义二次过滤的角色！（即只想出什么人）
			if (taskVOS != null && taskVOS.length > 0) { //遍历所有任务,循环加入所有任务!!!
				if (_isSelfBtn) { //如果点击的自循环中的按钮!则强行根据按钮名称过滤!!!
					if (_filterRoleName != null) { //如果过滤的角色不为空！！这种情况是直接点击的
						DealTaskVO[] newTaskVOS = uiSecondFilterTaskByRoleName(taskVOS, new String[] { _filterRoleName }); //
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>★点击的是自循环中的按钮,前台根据自循环上的按钮对应的角色名【" + _filterRoleName + "】再次进行过滤！过滤后共得到[" + newTaskVOS.length + "]条!"); //
						parUserPanels[i].setDealTaskVOs(newTaskVOS, false); //
					} else {
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>★点击的是自循环中的按钮,但角色名称为空?(可能没绑定小流程或角色设置不对),所以列出所有待办人员.."); //
						parUserPanels[i].setDealTaskVOs(taskVOS, false); //加入一个空框!
					}
				} else { //★★★如果是外面的出去的!!!
					_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>★点击的不是自循环中的内部流转按钮,而是出去外部的按钮,将根据连线与目标环节进行计算..."); //
					ArrayList al_filterRoleNames = new ArrayList(); //

					//先看连线有无条件!
					if (_dealactivityVOs[i].getFromtransitionExtConfMap() != null && _dealactivityVOs[i].getFromtransitionExtConfMap().containsKey("目标环节进入的二次角色过滤")) {
						String str_inputRoleFilter = (String) _dealactivityVOs[i].getFromtransitionExtConfMap().get("目标环节进入的二次角色过滤"); //需要
						if (str_inputRoleFilter != null && !str_inputRoleFilter.trim().equals("")) { //如果有定义！！
							String[] str_filterRoles = getTBUtil().split(str_inputRoleFilter, "/"); //分隔
							al_filterRoleNames.addAll(Arrays.asList(str_filterRoles)); //加入!
							_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>★前台根据来源连线上的条件【目标环节进入的二次角色过滤】=【" + str_inputRoleFilter + "】再次进行过滤！!"); //
						}
					} else {
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>★出去的连线上没定义参数[目标环节进入的二次角色过滤],所以跳过计算..."); //
					}

					if (_filterRoleName != null) {
						al_filterRoleNames.add(_filterRoleName); //
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>★发现目标环节同样也是个自循环,且绑定了一个角色映射[" + _dealactivityVOs[i].getActivitySelfcyclerolemap() + "],且映射的\"小流程\"中有启动环节:" + al_filterRoleNames + ",并选中之,则进行过滤!!!"); //
					} else { //
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>★出去的目标环节不是自循环或者没有定义角色映射,所以跳过二次过滤计算(这将找出所有原有参与者)...");
					}

					if (al_filterRoleNames.size() > 0) { //如果有过滤的角色条件!!则过滤
						DealTaskVO[] newTaskVOS = uiSecondFilterTaskByRoleName(taskVOS, (String[]) al_filterRoleNames.toArray(new String[0])); //对这个环节上的人员进行过滤!!
						parUserPanels[i].setDealTaskVOs(newTaskVOS, false); //
						_dealactivityVOs[i].setParticiptMsg(_dealactivityVOs[i].getParticiptMsg() + "<br>★根据连线与目标环节自循环中的启动角色进行[并集]过滤后,过滤条件是:" + getStrByList(al_filterRoleNames) + ",共得到[" + newTaskVOS.length + "]条数据(过滤前的数据是[" + taskVOS.length + "]条)!");
					} else { //如果没有,反而是列出所有!
						parUserPanels[i].setDealTaskVOs(taskVOS, false); //加入一个空框!
					}
					parUserPanels[i].getBillListPanel().moveToTop(); //置顶
					parUserPanels[i].getBillListPanel().clearSelection(); //清除选择
				}
			} else { //如果没数据
				if (_dealactivityVOs[i].getWnParam() != null && "Y".equalsIgnoreCase((String) _dealactivityVOs[i].getWnParam().get("无参与者是否隐藏"))) {
					//continue;
				} else {
					parUserPanels[i].setDealTaskVOs(taskVOS, false); //加入一个空框!
				}
			}

			panel_activitys[i] = new JPanel(new BorderLayout()); //某一个环节的面板
			String str_activitypartmsg = "<html>审批模式[" + getApproveModel(_dealactivityVOs[i].getApprovemodel()) + "],是否自循环[" + (_dealactivityVOs[i].getSortIndex() == 0 ? "是" : "否") + "]"; // 
			if (ClientEnvironment.getInstance().isAdmin()) { //如果是管理员,则多一点!
				String str_tipText = _dealactivityVOs[i].getParticiptMsg().substring(0, _dealactivityVOs[i].getParticiptMsg().indexOf("###")); //
				str_activitypartmsg = str_activitypartmsg + "<br>" + str_tipText + "</html>"; //参与信息
			} else { //如果不是管理员,则直接加上</html>
				str_activitypartmsg = str_activitypartmsg + "</html>";
			}
			if (_dealactivityVOs[i].getActivityType() != null && _dealactivityVOs[i].getActivityType().equalsIgnoreCase("END") && parUserPanels[i].getBillListPanel().getRowCount() == 0) { //如果当前环节类型是结束
				WLTButton btn_endwf = new WLTButton("<html><center>结束本流程</center></html>"); //
				btn_endwf.setForeground(Color.RED); //
				btn_endwf.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onEndWF(); //
					}
				}); //
				btn_endwf.setPreferredSize(new Dimension(250, 65)); //
				JPanel tempPanel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER, 10, 150)); //
				tempPanel.setOpaque(false); //
				tempPanel.add(btn_endwf); //
				//Box box = new Box(BoxLayout.Y_AXIS); //
				//box.add(Box.createVerticalStrut(150));
				//box.add(tempPanel); //
				panel_activitys[i].add(tempPanel, BorderLayout.CENTER); //直接结束.
			} else {
				//如果参与部门为空,则只加入人员!!!!! 即有时有的客户只想选择部门,并不知道选择具体的人,即在部门间提交时,比如他并不知道总行是何人接收!!! 这也是工作流中一个常见的恼人问题,这种问题如何解决?????? 如何选部门肯定太绕(不好实现),应该在部门之间设定一个"综合分发员"，然后由综合员分发!!即在图中多画一个环节!! 					
				panel_activitys[i].add(parUserPanels[i], BorderLayout.CENTER);
			}

			String str_tabTitle = _dealactivityVOs[i].getCurrActivityName(true); //
			tabbedPane_activity.addTab(str_tabTitle, imgUnSelected, panel_activitys[i]); //加入这个环节!!!即第一层页签面板!!!
			tabbedPane_activity.setToolTipTextAt(i, str_activitypartmsg); //

			tabbedPane_activity.putClientProperty("tab_activity_" + i, panel_activitys[i]); //环节!!
			tabbedPane_activity.putClientProperty("tab_user_" + i, parUserPanels[i]); //用户!!
			tabbedPane_activity.putClientProperty("tab_vo_" + i, _dealactivityVOs[i]); //用户!!
		}

		tabbedPane_activity.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane_activity.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON3) {
					showPopMenu(e.getX(), e.getY());
				}
			}
		}); //

		tabbedPane_activity.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				onTabChangeSelected(); //
			}
		});

		//tabbedPane_activity.setForegroundAt(0, Color.RED); //
		tabbedPane_activity.setIconAt(0, imgSelected); //

		//弹出菜单!
		popMenu = new JPopupMenu(); //
		JMenuItem menuItem_showpart = new JMenuItem("查看该环节参与者定义"); //
		JMenuItem menuItem_acivityinfo = new JMenuItem("查看该环节的实际定义"); //
		menuItem_showpart.setPreferredSize(new Dimension(135, 19));
		menuItem_acivityinfo.setPreferredSize(new Dimension(135, 19));
		menuItem_showpart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showParticipanInfo(); //
			}
		}); //
		menuItem_acivityinfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showActivityDBRecord(); //
			}
		}); //
		popMenu.add(menuItem_showpart); //
		popMenu.add(menuItem_acivityinfo); //

		contentPanel.add(tabbedPane_activity, BorderLayout.CENTER); //
		contentPanel.add(getSouthPanel(true, _isReturn), BorderLayout.SOUTH); //

		return contentPanel; //返回面板!!
	}

	//UI端对已经计算出来的待办环节根据其对应的连线上定义的角色条件！与本人的实际角色进行匹配！
	//必须是我有的角色才能出来！
	private boolean uiSecondCheckActivityByRoleName(String[] _roleCons) {
		String[] str_myAllRoles = ClientEnvironment.getCurrLoginUserVO().getAllRoleNames(); //
		if (str_myAllRoles == null || str_myAllRoles.length <= 0) {
			return false; //如果有一个角色没有,则返回！
		}
		for (int i = 0; i < str_myAllRoles.length; i++) {
			for (int j = 0; j < _roleCons.length; j++) {
				if (str_myAllRoles[i].indexOf(_roleCons[j]) >= 0) { //比如我的环节是叫【一行分行综合员】,而条件中的角色叫【综合员】,则表示我是有权利的！
					return true; //
				}
			}
		}
		return false; //
	}

	/**
	 * UI端针对对参与者根据角色名称进行二次过滤！！！而且是模糊匹配！！！
	 * @param taskVOS
	 * @return
	 */
	private DealTaskVO[] uiSecondFilterTaskByRoleName(DealTaskVO[] taskVOS, String[] _roleNames) {
		ArrayList al_newTasks = new ArrayList(); //
		for (int j = 0; j < taskVOS.length; j++) { //遍历所有任务！
			String str_parUserRoleName = taskVOS[j].getParticipantUserRoleName(); //这个参与者角色的名称!
			System.out.println("角色名称:[" + str_parUserRoleName + "]"); //
			if (str_parUserRoleName != null && !str_parUserRoleName.equals("")) { //如果这条待办任务人员的角色名称不为空!
				String[] str_roleNameItems = getTBUtil().split(str_parUserRoleName, ";"); //分割一下！！！
				boolean isMatch = false; //
				for (int k = 0; k < str_roleNameItems.length; k++) { //遍历！！！
					for (int r = 0; r < _roleNames.length; r++) { //
						if (str_roleNameItems[k].indexOf(_roleNames[r]) >= 0) { //如果这个角色包含所要过滤的角色名,则加入！！
							isMatch = true; //
							break; //
						}
					}
					if (isMatch) { //如果有一个对上了，则不再继续了！
						break;
					}
				}
				if (isMatch) { //如果对得上
					al_newTasks.add(taskVOS[j]); //
				}
			}
		}

		return (DealTaskVO[]) al_newTasks.toArray(new DealTaskVO[0]); //
	}

	private JPanel getMsgPanel() {
		String str_currUserName = "【" + firstTaskVO.getCurrParticipantUserName() + "】"; //当前参与人员的名称
		String str_currActBLDeptName = firstTaskVO.getCurractivityBLDeptName(); //当前环节的所属部门矩阵的名称
		String str_currActName = firstTaskVO.getCurractivityName(); //当前环节的名称!!
		str_currActName = "【" + (str_currActBLDeptName == null ? "" : (str_currActBLDeptName + "-")) + str_currActName + "】"; //
		str_currActName = getTBUtil().replaceAll(str_currActName, "\r", ""); //
		str_currActName = getTBUtil().replaceAll(str_currActName, "\n", ""); //
		WLTLabel label = new WLTLabel("提示:当前处理步骤" + str_currActName + ",当前处理人" + str_currUserName + ",请选择下个步骤中的人员进行提交!"); //
		label.setPreferredSize(new Dimension(2000, 20)); //
		label.setForeground(Color.BLUE); //
		label.addStrItemColor(str_currActName, Color.RED); //
		label.addStrItemColor(str_currUserName, Color.RED); //
		label.addMouseListener(this); //
		WLTPanel helpMsgPanel = new WLTPanel(WLTPanel.VERTICAL_TOP_TO_BOTTOM, LookAndFeel.defaultShadeColor1, false); //
		helpMsgPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5)); //
		helpMsgPanel.add(label); //

		return helpMsgPanel; //
	}

	/**
	 * 按钮面板
	 * @return
	 */
	private JPanel getSouthPanel(boolean _isConfirm, boolean _isReturn) {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_return = new WLTButton("返回"); //
		btn_confirm = new WLTButton("确定"); //
		btn_monitor = new WLTButton("流程监控"); //
		btn_cancel = new WLTButton(UIUtil.getLanguage("取消")); //

		btn_return.setPreferredSize(new Dimension(70, 25)); //
		btn_monitor.setPreferredSize(new Dimension(70, 25)); //
		btn_confirm.setPreferredSize(new Dimension(70, 25)); //
		btn_cancel.setPreferredSize(new Dimension(70, 25)); //

		btn_return.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_monitor.addActionListener(this); //
		btn_cancel.addActionListener(this); //

		if (_isReturn) {
			panel.add(btn_return);
		}
		if (_isConfirm) {
			panel.add(btn_confirm);
		}
		panel.add(btn_monitor);
		panel.add(btn_cancel);

		if (ClientEnvironment.isAdmin()) { //为了解决问题时快速找到这个类!
			panel.setToolTipText("WorkFlowDealChooseUserDialog,ParticipateUserPanel"); //
		}
		return panel;
	}

	private String getApproveModel(String _model) {
		if (_model == null) {
			return "抢占";
		} else if (_model.equals("1")) { //
			return "抢占";
		} else if (_model.equals("2")) { //
			return "会签";
		} else if (_model.equals("3")) { //
			return "会办子流程";
		} else {
			return "未知";
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //确定
		} else if (e.getSource() == btn_monitor) {
			onMonitor(); //流程监控
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //取消
		} else if (e.getSource() == btn_return) {
			onRetrun(); //返回
		}
	}

	/**
	 * 点击确认,即提交!!!!
	 */
	private void onConfirm() {
		DealTaskVO[] commitTaskVOs = null; //
		int li_selindex = this.tabbedPane_activity.getSelectedIndex(); //取得是第几个环节!!!
		DealActivityVO choosedActivityVO = (DealActivityVO) tabbedPane_activity.getClientProperty("tab_vo_" + li_selindex); //选中页签上绑定的VO....
		ParticipateUserPanel choosedParUserPanel = (ParticipateUserPanel) tabbedPane_activity.getClientProperty("tab_user_" + li_selindex); //人员面板!

		if ("END".equalsIgnoreCase(choosedActivityVO.getActivityType()) && choosedParUserPanel.getBillListPanel().getRowCount() == 0) { //可能会有问题!
			MessageBox.show(this, "现在是结束环节,请点击中间的按钮结束流程!"); //
			return;
		}

		boolean isMust = getTBUtil().getSysOptionBooleanValue("抄送人员是否必选", false); //竟然有客户要求必须选择抄送者????
		if (isMust) {
			//如果抄送人员不为空,则必须选择...
			BillListPanel list_2 = choosedParUserPanel.getBillListPanel2();
			if (list_2 != null && list_2.getRowCount() > 0 && list_2.getSelectedRows(true) != null && list_2.getSelectedRows(true).length <= 0) {//如果列表选择方式是勾选方式，则取得勾选的记录【李春娟/2012-03-01】
				MessageBox.show(this, "必须选择一个抄送人员!" + (list_2.isRowNumberChecked() ? "\r\n请注意:只有选中前面的勾选框才算是真正选择!" : ""));
				return;
			}
		}

		commitTaskVOs = choosedParUserPanel.getSelectedTaskVOs(); //从参与人员中取得所有待处理任务!
		if (commitTaskVOs == null) {
			return;
		}

		////
		String str_activityName = choosedActivityVO.getCurrActivityName(false); //
		if (getTBUtil().getSysOptionBooleanValue("工作流提交时是否显示提醒", true)) { //
			String desc = getTBUtil().getSysOptionStringValue("工作流提交时的提醒信息", "确定要提交给环节【" + str_activityName + "】吗?");
			desc = getTBUtil().replaceAll(desc, "$环节名称$", str_activityName);
			if (!MessageBox.confirm(this, desc)) {
				return; //
			}
		}

		clearAllDealTaskVOs(firstTaskVO); //清空所有第一次从服务器端返回的待处理任务,目的是为了提高性能.
		firstTaskVO.setCommitTaskVOs(commitTaskVOs); //设置处理任务..
		closetype = 1;
		this.dispose(); //

	}

	private void showHelpMsg() {
		MessageBox.show(this, sb_allHelpMsg.toString()); //
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

	private void onEndWF() {
		if (JOptionPane.showConfirmDialog(this, "您确定要结束流程吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		closetype = 3;
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

	private void showPopMenu(int _x, int _y) {
		popMenu.show(tabbedPane_activity, _x, _y); //
	}

	/**
	 * 显示环节参与者信息!!
	 */
	private void showParticipanInfo() {
		try {
			int li_index = tabbedPane_activity.getSelectedIndex(); //
			DealActivityVO choosedActivityVO = (DealActivityVO) tabbedPane_activity.getClientProperty("tab_vo_" + li_index); //
			String str_msg = choosedActivityVO.getParticiptMsg(); //
			TBUtil tbUtil = new TBUtil(); //
			str_msg = tbUtil.replaceAll(str_msg, "<html>", ""); //
			str_msg = tbUtil.replaceAll(str_msg, "</html>", ""); //
			str_msg = tbUtil.replaceAll(str_msg, "###", ""); //
			str_msg = tbUtil.replaceAll(str_msg, "<br>", "\r\n"); //
			str_msg = "环节名称=【" + choosedActivityVO.getActivityName() + "】\r\n" + str_msg;
			MessageBox.showTextArea(this, str_msg); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 
	 * @return
	 */
	private String getConvertShowpartimode(String _type) {
		if (_type == null || _type.trim().equals("") || _type.trim().equals("1")) {
			return "总显示计算的"; //
		} else if (_type.trim().equals("2")) {
			return "总显示走过的"; //
		} else if (_type.trim().equals("3")) {
			return "两者都显示"; //
		} else {
			return "未知的类型[" + _type + "]";
		}
	}

	private void showActivityDBRecord() {
		try {
			int li_index = tabbedPane_activity.getSelectedIndex(); //
			DealActivityVO choosedActivityVO = (DealActivityVO) tabbedPane_activity.getClientProperty("tab_vo_" + li_index); //
			String str_paruser = choosedActivityVO.getActivityId(); //
			new RecordShowDialog(this, "pub_wf_activity", "id", str_paruser);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void onCancel() {
		closetype = 2;
		this.dispose(); //
	}

	public int getClosetype() {
		return closetype;
	}

	public WFParVO getReturnVO() {
		return firstTaskVO;
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}

	/**
	 * 页签选择变化!!
	 */
	private void onTabChangeSelected() {
		int li_count = tabbedPane_activity.getTabCount(); //
		int li_sel = tabbedPane_activity.getSelectedIndex(); //
		for (int i = 0; i < li_count; i++) {
			if (i == li_sel) {
				DealActivityVO choosedActivityVO = (DealActivityVO) tabbedPane_activity.getClientProperty("tab_vo_" + li_sel);
				if (choosedActivityVO.getWnParam() != null && choosedActivityVO.getWnParam().get("选择提醒") != null && !"".equals((String) choosedActivityVO.getWnParam().get("选择提醒"))) {
					MessageBox.showWarn(this, (String) choosedActivityVO.getWnParam().get("选择提醒"));
				}
				tabbedPane_activity.setIconAt(i, imgSelected); //	
			} else {
				tabbedPane_activity.setIconAt(i, imgUnSelected); //
			}
		}

	}

	public BillVO getBillVO() {
		return billVO;
	}

	public void setBillVO(BillVO billVO) {
		this.billVO = billVO;
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		closetype = 2;
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			showHelpMsg(); //
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

}
