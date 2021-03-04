package cn.com.infostrategy.ui.workflow;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkflowUIUtil;

/**
 * 工作流风格面板!! 这个类与TaskAndMsgCenterPanel关系很密切!! 其中一些逻辑都是相通的!
 * 工作流是非常重要的应用,有时甚至是核心应用!! 所以简化工作流的开发量就等于大大降低了项目成本!!! 而工作流是完全可以封装与抽象的!!! 所以必须把它做好!!!
 * 以前的工作流面板的风格面板是 Style19,但新的方法与思路将与原来有所不同! 即不是从单据出发绑定流程,而是从流程出发绑定单据!!!!
 * 
 * 草稿箱的逻辑与以前一样,直接判断prinstance is null
 * select * from %业务表名% where prinstance is null and create_userid=%登录人员%
 * 
 * 待办箱的逻辑是:
 * 从表pub_task_deal出发,查询出 select t1.id t1_id,t1.taskclass t1_taskclass,t1.dealuser...,t2.* from pub_task_deal t1,%业务表名% t2 where t1.tabname=%业务表名% and t1.pkvalue=t2.id and t1.dealuser=%登录人员% order by createtime desc
 * 即存在于待处理任务表中的属于我的所有记录,然后通过关联查询出业务表的记录,然后模板又是业务表的模板,自然能对上数据!
 * 
 * 已办箱的逻辑是:
 * 从表pub_task_off出发,查询出实际是我处理的记录!!!
 * select t1.id t1_id,t1.taskclass t1_taskclass,t1.dealuser...,t2.* from pub_task_off t1,%业务表名% t2 where t1.tabname=%业务表名% and t1.pkvalue=t2.id and t1.realdealuser=%登录人员% order by dealtime desc
 * 
 * 如果某人处理了某条任务后,则自动将待办任务搬家到已办任务表中去!!!
 * 如果是抄送的消息,则查看后,有个【确认】按钮,也是进入已办箱中!!!
 * 如果某人从来没看过待办箱中的任务,则意味着他仍然存在这条任务,如果这时这个任务已过期(环节已过了,甚至流程已结束),则到时提醒他已是过期消息了!!! 然后也是点击【确认】键搬家到已办箱中!
 * 
 * 这样的设计将会使撤回操作也变得简单,即将已办箱中的任务再重新回写到待办箱中去!!
 * 
 * 工作流以后还需提供Web展示的界面,然后可以点击提交直接进入系统处理!! 即如何与第三方系统接口的问题!!!
 * @author xch
 *
 */
public abstract class AbstractWorkFlowStylePanel extends AbstractWorkPanel {

	private static final long serialVersionUID = 5538656513105393516L;

	private JTabbedPane tabbedPanel = null; //
	private WLTTabbedPane wlttabbedPanel = null; //

	private Pub_Templet_1VO pubTempletVO = null; //模板定义VO
	private BillListPanel billList_1, billList_2, billList_3; //三外列表

	private WorkflowUIUtil wfUIUtil = null; //
	private String str_templetCode = null; //
	private String str_tableName = null; //查询的业务表名,非常重要!!因为待办箱与已办箱中的查询SQL,将是通过这个表名来动态拼接的!!!
	private String str_savedTableName = null; //保存的表名!
	private String str_pkName = null; //

	/**
	 * 模板编码,即单据模板!!!
	 * @return
	 */
	public abstract String getBillTempletCode();

	/**
	 * 显示草稿箱,待办箱,已办箱本个页签,默认三个都显示!!
	 * @return
	 */
	public boolean[] getShowTabIndex() {
		return new boolean[] { true, true, true };
	}

	//页签位置..
	public int getTabbedPos() {
		if ("上面".equals(getMenuConfMapValueAsStr("页签位置"))) {
			return JTabbedPane.TOP; //
		}
		return JTabbedPane.LEFT; //
	}

	/**
	 * 初始化逻辑
	 */
	@Override
	public void initialize() {
		try {
			this.setLayout(new BorderLayout());
			pubTempletVO = UIUtil.getPub_Templet_1VO(getBillTempletCode()); //为了提高性能只需要一个模板定义VO就可以了!!!
			str_templetCode = pubTempletVO.getTempletcode(); //模板编码
			str_tableName = pubTempletVO.getTablename(); //查询的表名!!!
			str_savedTableName = pubTempletVO.getSavedtablename(); //保存的表名!
			str_pkName = pubTempletVO.getPkname(); //主键名
			pubTempletVO.setIsshowlistquickquery(Boolean.TRUE); //显示快速查询

			if (getTabbedPos() == JTabbedPane.TOP) { //如果是在顶上,则使用我们自己多页签暂时不支持左边效果!! 以后需要支持!!!
				wlttabbedPanel = new WLTTabbedPane(); //
			} else {
				tabbedPanel = new JTabbedPane(getTabbedPos()); //
			}
			//tabbedPanel.addChangeListener(this);  //

			boolean[] showTables = getShowTabIndex(); //
			//草稿箱
			int li_indexcount = -1; //累计总共有几个页面!
			if (showTables != null && showTables.length >= 1 && showTables[0]) {
				billList_1 = new BillListPanel(pubTempletVO); //
				billList_1.setItemsVisible(getWorkflowUIUtil().getDraftTaskHiddenFields(), false); //处理时间,处理意见隐藏
				billList_1.addWorkFlowDealPanel(); //
				billList_1.getWorkflowDealBtnPanel().hiddenAllBtns();
				billList_1.getWorkflowDealBtnPanel().setInsertBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setUpdateBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setDeleteBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);

				String str_userid = ClientEnvironment.getInstance().getLoginUserID(); //登录人员主键
				billList_1.setDataFilterCustCondition("create_userid = '" + str_userid + "' and wfprinstanceid is null"); //制单人等于我,且没有启动流程的!
				if (wlttabbedPanel != null) { //如果有图片!!
					wlttabbedPanel.addTab(getTabTitle(1, getTabbedPos()), UIUtil.getImage(getTabImage(1)), billList_1); //
				} else {
					tabbedPanel.addTab(getTabTitle(1, getTabbedPos()), billList_1); //
				}
				li_indexcount++; //
			}

			//待办箱..
			if (showTables != null && showTables.length >= 2 && showTables[1]) {
				billList_2 = new BillListPanel(pubTempletVO); //
				//billList_2.setAllBillListBtnVisiable(false); //隐藏所有业务按钮!!
				billList_2.setItemsVisible(getWorkflowUIUtil().getDealTaskHiddenFields(), false); //如果是待办箱则强制隐藏这些字段!
				billList_2.setItemsVisible(getWorkflowUIUtil().getDealTaskShowFields(), true); //如果是待办任务,则强制显示这些字段!
				billList_2.addWorkFlowDealPanel(); //
				billList_2.getWorkflowDealBtnPanel().hiddenAllBtns(); //隐藏所有按钮!!!
				billList_2.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
				billList_2.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList_2.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //显示导出按钮!!!

				billList_2.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onBillListQuery_2(); //
					}
				}); //
				if (wlttabbedPanel != null) { //如果有图片!!
					wlttabbedPanel.addTab(getTabTitle(2, getTabbedPos()), UIUtil.getImage(getTabImage(2)), billList_2); //
				} else {
					tabbedPanel.addTab(getTabTitle(2, getTabbedPos()), billList_2); //
				}
				li_indexcount++; //

				if (wlttabbedPanel != null) { //如果有图片!!
					wlttabbedPanel.setSelectedIndex(li_indexcount); //
				} else {
					tabbedPanel.setSelectedIndex(li_indexcount); //
				}
			}

			//已办箱..
			if (showTables != null && showTables.length >= 3 && showTables[2]) {
				billList_3 = new BillListPanel(pubTempletVO); //
				//billList_3.setAllBillListBtnVisiable(false); //隐藏所有业务按钮!!
				billList_3.setItemsVisible(getWorkflowUIUtil().getOffTaskHiddenFields(), false); //如果是已办箱则强制隐藏这些字段!
				billList_3.setItemsVisible(getWorkflowUIUtil().getOffTaskShowFields(), true); //如果是已办任务,则强制显示这些字段!
				billList_3.addWorkFlowDealPanel(); //
				billList_3.getWorkflowDealBtnPanel().hiddenAllBtns();
				billList_3.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList_3.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //显示导出按钮!!!
				billList_3.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onBillListQuery_3(); //
					}
				}); //
				if (wlttabbedPanel != null) { //如果有图片!!
					wlttabbedPanel.addTab(getTabTitle(3, getTabbedPos()), UIUtil.getImage(getTabImage(3)), billList_3); //
				} else {
					tabbedPanel.addTab(getTabTitle(3, getTabbedPos()), billList_3); //
				}
				li_indexcount++;
			}

			if (billList_2 != null) { //如果有待办箱,则自动将待办箱中的任务直接查出来!!
				String str_sql = new WorkflowUIUtil().getDealTaskSQL(str_templetCode, str_tableName, str_savedTableName, str_pkName, ClientEnvironment.getInstance().getLoginUserID(), null, false); //
				billList_2.queryDataByDS(null, str_sql, true); //直接通过SQL查询!!
			}
			if (wlttabbedPanel != null) { //如果有图片!!
				this.add(wlttabbedPanel, BorderLayout.CENTER); ////
			} else {
				this.add(tabbedPanel, BorderLayout.CENTER); ////
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 待办箱中的查询!!
	 */
	private void onBillListQuery_2() {
		BillQueryPanel billQueryPanel = billList_2.getQuickQueryPanel(); //取得查询面板!!
		String str_condition = billQueryPanel.getQuerySQLCondition(true, "t2"); //取得查询条件!
		if (str_condition == null) {
			return; //如果校验不过,则不做查询!
		}
		String str_sql = new WorkflowUIUtil().getDealTaskSQL(str_templetCode, this.str_tableName, str_savedTableName, this.str_pkName, ClientEnvironment.getCurrSessionVO().getLoginUserId(), str_condition, false); //
		billList_2.queryDataByDS(null, str_sql, true); //直接通过SQL关联!!!
	}

	/**
	 * 已办箱中的查询!!
	 */
	private void onBillListQuery_3() {
		BillQueryPanel billQueryPanel = billList_3.getQuickQueryPanel(); //取得查询面板!!!
		String str_condition = billQueryPanel.getQuerySQLCondition(true, "t2"); //取得查询条件!,忽略模板中定义的条件!!
		if (str_condition == null) {
			return; //如果校验不过,则不做查询!
		}
		String str_sql = new WorkflowUIUtil().getOffTaskSQL(str_templetCode, this.str_tableName, str_savedTableName, this.str_pkName, ClientEnvironment.getInstance().getLoginUserID(), str_condition, false);
		billList_3.queryDataByDS(null, str_sql, true); //直接通过SQL关联!!!
	}

	private WorkflowUIUtil getWorkflowUIUtil() {
		if (wfUIUtil != null) {
			return wfUIUtil;
		}
		wfUIUtil = new WorkflowUIUtil(); //
		return wfUIUtil;
	}

	//取得图标的图片名称!!
	private String getTabImage(int _type) {
		if (_type == 1) {
			return "office_167.gif"; //草稿箱
		} else if (_type == 2) {
			return "zt_057.gif"; //
		} else if (_type == 3) {
			return "office_138.gif"; //
		} else {
			return null; //
		}
	}

	/**
	 * 标签说明,为了好看,使用Html风格使字显得大一点!!!
	 * @param _type
	 * @return
	 */
	private String getTabTitle(int _type, int _tabbedPos) {
		if (_tabbedPos == JTabbedPane.LEFT) {
			if (_type == 1) {
				return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>草&nbsp;稿&nbsp;箱</font></b>&nbsp;</td></tr></table></html>"; //
			} else if (_type == 2) {
				return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>待&nbsp;办&nbsp;箱</font></b>&nbsp;</td></tr></table></html>"; //
			} else if (_type == 3) {
				return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>已&nbsp;办&nbsp;箱</font></b>&nbsp;</td></tr></table></html>"; //
			}
		} else { //在顶
			if (_type == 1) {
				return "草稿箱"; //
			} else if (_type == 2) {
				return "待办箱"; //
			} else if (_type == 3) {
				return "已办箱"; //
			}
		}
		return "";
	}

	public BillListPanel getBillList_1() {
		return billList_1;
	}

	public BillListPanel getBillList_2() {
		return billList_2;
	}

	public BillListPanel getBillList_3() {
		return billList_3;
	}

}
