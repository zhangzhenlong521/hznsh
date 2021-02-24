package cn.com.infostrategy.ui.workflow.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.to.mdata.templetvo.TempletItemVOComparator;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;

/**
 * 工作流处理历史意见列表的构造器!
 * 工作流处理意见历史是非常极其关键的一个UI,也就是所谓"流程意见浏览"的核心界面!! 是人性化要求非常高的地方! 
 * 它主要表现在:
 * 1.要按顺序显示处理步骤,处理人,处理时间,处理时耗等,每个客户多少都会对这个列表的顺序与名称提出一些需求,并要求与他们OA一样!
 * 2.相同部门要有分组概念,我们现在用颜色区分也挺好!
 * 3.要有显示[所有意见/部门最后意见]过滤之功能!
 * 4.要按组区分各组(涌道)中的的意见,而且默认根据本人机构类型计算出默认是哪个组!
 * 
 * 以前这个类中的方法是放在WorkflowUIUtil中的,但后来发现这个类功能非常重要,且代码量非常大,所以单独搞了一个类!!
 * 在流程处理界面与流程监控界面中都用到了这个类!
 * @author XCH
 */
public class WFHistListPanelBuilder implements ActionListener, ItemListener {

	private String prinstanceId = null; //
	private HashVO wfAssignHVO = null; //流程配置信息的VO

	private HashVO[] hvs; //
	private BillVO billVO; //
	private boolean isHiddenBtnVisiable = false; //
	private BillListPanel histBillList; //
	private BillVO[] initAllBillVOs = null; //初始化的所有BillVO!

	private WLTButton btn_viewAllMsg = null; //查看全部意见!
	private JComboBox combox_lastmsg = null; //[所有意见][最后意见]的下拉框!
	private JCheckBox[] allCheckBoxs = null; //动态计算出各个[组/涌道]的名称!! 

	private int li_maxDeptLevel = 2; //部门的最大层级,因为经常用将处室斦算成部门! 在处理最终意见与分组颜色显示时,都只需要到部门,而处室/科室都算成部门!
	private TBUtil tbUtil = null; //
	private WorkflowUIUtil wfUIUtil = null; //

	private WFHistListPanelBuilder() {
	}

	public WFHistListPanelBuilder(HashVO[] _hvs, BillVO _cardBillVO, boolean _isHiddenBtnVisiable) {
		this.billVO = _cardBillVO; //
		loadWfAssignVO(); //

		this.hvs = _hvs; //
		this.isHiddenBtnVisiable = _isHiddenBtnVisiable; //
		initData(); //初始化数据
	}

	public WFHistListPanelBuilder(String _prinstanceId, BillVO _cardBillVO, boolean _isHiddenBtnVisiable) {
		this.prinstanceId = _prinstanceId; //流程实例Id
		this.billVO = _cardBillVO; //
		loadWfAssignVO(); //加载流程配置信息

		try {
			WorkFlowServiceIfc service = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
			hvs = service.getMonitorTransitions(prinstanceId, isJiaMeMsg(), true); // 可以处理加密信息!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		this.isHiddenBtnVisiable = _isHiddenBtnVisiable; //
		initData(); //初始化数据
	}

	public BillListPanel getBillListPanel() {
		return histBillList; //
	}

	public HashVO[] getHashVODatas() {
		return hvs; //
	}

	//初始化流程配置信息VO!!
	private void loadWfAssignVO() {
		try {
			if (billVO == null) {
				return; //
			}
			String str_billtype = billVO.getStringValue("billtype"); //
			String str_busitype = billVO.getStringValue("busitype"); //
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"); //
			if (hvs != null && hvs.length > 0) {
				wfAssignHVO = hvs[0]; //赋值!!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

	}

	//构造数据
	private void initData() {
		try {
			//先把列表创建好!
			Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_wf_dealpool_CODE1.xml")); //
			Pub_Templet_1_ItemVO[] templetItemVOs = templetVO.getItemVos(); // 各项子表
			//邮储项目上遇到一定要重新排序这个,即要将流程意见放在前面!
			String str_wfmsgListOrder = TBUtil.getTBUtil().getSysOptionStringValue("工作处理意见列表各列顺序", null); //
			if (str_wfmsgListOrder != null && !str_wfmsgListOrder.trim().equals("")) { //
				String[] str_orderItems = TBUtil.getTBUtil().split(str_wfmsgListOrder, ";"); //
				Arrays.sort(templetItemVOs, new TempletItemVOComparator(str_orderItems)); //
			}

			histBillList = new BillListPanel(templetVO, false); //
			if (!TBUtil.getTBUtil().getSysOptionBooleanValue("工作处理是否支持上传附件", true)) { //邮储项目中提出不要上传附件,说是逼着人们在意见中填!!! 因为附件内容导不出来!
				histBillList.setItemVisible("submitmessagefile", false); //如果不支持,则隐藏!!!
			}
			if (isHiddenBtnVisiable) {
				histBillList.setHiddenUntitlePanelBtnvVisiable(true); //
			}
			histBillList.initialize(); //手工初始化!
			histBillList.setRowNumberChecked(true); //
			//histBillList.setItemVisible("submitisapprove", false); //隐藏掉
			histBillList.setItemVisible("submitmessage_viewreason", false); //隐藏掉

			ArrayList al_temp = new ArrayList(); //
			for (int i = 0; i < hvs.length; i++) { //遍历插入数据!!!
				if ("Y".equals(hvs[i].getStringValue("issubmit"))) { //只是提交过的才显示!!!
					al_temp.add(hvs[i]); //
				}
			}
			TBUtil tbUtil = new TBUtil(); //
			HashVO[] submitedHVS = (HashVO[]) al_temp.toArray(new HashVO[0]); //
			tbUtil.sortHashVOs(submitedHVS, new String[][] { { "submittime", "N", "N" } }); //排序一把,即按照提交时间的升序排序!!!
			LinkedHashSet lh_blcorp = new LinkedHashSet(); //

			//遍历在表格中插入所有数据!!!!!

			for (int i = 0; i < submitedHVS.length; i++) { //
				int li_row = histBillList.newRow(false); //新增一行!
				histBillList.getRowNumberVO(li_row).setRecordHVO(submitedHVS[i]); //
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("id")), li_row, "id"); //任务主键
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("prinstanceid")), li_row, "prinstanceid"); //实例id
				histBillList.setValueAt(new StringItemVO(getWFUIUtil().getWFActivityName(submitedHVS[i])), li_row, "curractivity_wfname"); //当前步骤!!!

				//当前环节所属组,邮储项目中遇到使用组进行计算与过滤!
				if (submitedHVS[i].getStringValue("parentinstanceid", "").equals("")) { //如果父流程为空,则表示是主流程
					histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("curractivity_belongdeptgroup")), li_row, "curractivity_bldeptname"); //当前环节所属组
				} else { //是子流程
					histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("prinstanceid_fromparentactivitybldeptgroup")), li_row, "curractivity_bldeptname"); //当前环节所属组
				}
				lh_blcorp.add(histBillList.getRealValueAtModel(li_row, "curractivity_bldeptname")); //当前环节所属部门..

				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("participant_user")), li_row, "participant_user"); //人员id
				String str_participant_username = submitedHVS[i].getStringValue("participant_username"); //授权人名称!!
				String str_realsubmitername = submitedHVS[i].getStringValue("realsubmitername"); //实际提交人名称!一直有客户提,要知道到底是谁干的!
				if (str_realsubmitername.indexOf("/") > 0) {
					str_realsubmitername = str_realsubmitername.substring(str_realsubmitername.indexOf("/") + 1, str_realsubmitername.length()); //
					submitedHVS[i].setAttributeValue("realsubmitername", str_realsubmitername);
				}

				String str_participant_yjbdusername = submitedHVS[i].getStringValue("participant_yjbdusername"); //如果发生补登
				if (str_participant_yjbdusername != null) {
					if (str_participant_username.equals(str_realsubmitername)) {//说明是在代办任务补登的 先搞对以后可能会有好的设计/sunfujun/20121121
						str_participant_username = str_participant_yjbdusername + "(" + str_realsubmitername + "补登)";
					} else {//在意见补登补登的
						str_participant_username = str_participant_username + "(" + str_realsubmitername + "补登)"; //记录下实际是谁补登的!
					}
				}

				histBillList.setValueAt(new StringItemVO(str_participant_username), li_row, "participant_username"); //人员名称

				String str_userdeptName = submitedHVS[i].getStringValue("realsubmitcorpname"); //
				int li_levelcount = getTBUtil().findCount(str_userdeptName, "-"); //
				if (li_levelcount > li_maxDeptLevel) {
					li_maxDeptLevel = li_levelcount; //
				}
				histBillList.setValueAt(new StringItemVO(str_userdeptName), li_row, "participant_userdeptname"); //机构名称
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("issubmit")), li_row, "issubmit"); //是否提交
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("submittime")), li_row, "submittime"); //提交时间

				String str_submitmessage = submitedHVS[i].getStringValue("submitmessage", ""); //意见
				String str_submitmessage_real = submitedHVS[i].getStringValue("submitmessage_real", ""); //,如果意见加密了,则这是加密前的!!
				histBillList.setValueAt(new RefItemVO(str_submitmessage, str_submitmessage_real, str_submitmessage), li_row, "submitmessage"); //意见!以前是文本框,后来因为有加密,所以弄成了参照VO
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("submitmessage_viewreason")), li_row, "submitmessage_viewreason"); //意见

				//文件附件要处理一下!
				String str_submitmessagefile = submitedHVS[i].getStringValue("submitmessagefile"); //附件名
				String str_submitmessagefile_real = submitedHVS[i].getStringValue("submitmessagefile_real"); //如果是加密了,则这是加密前的!
				if (submitedHVS[i].getStringValue("submitmessagefile") != null) { //
					if (str_submitmessagefile.trim().indexOf("/") != -1) { //如果带路径,则只显示最后的
						histBillList.setValueAt(new RefItemVO(str_submitmessagefile, str_submitmessagefile_real, getRefFileName(str_submitmessagefile)), li_row, "submitmessagefile"); //
					} else { //如果没有目录!
						histBillList.setValueAt(new RefItemVO(str_submitmessagefile, str_submitmessagefile_real, str_submitmessagefile), li_row, "submitmessagefile"); //
					}
				}
			} //遍历插入数据结束!!!
			initAllBillVOs = histBillList.getAllBillVOs(); //从页面上取得所有数据

			//取得这条流程的配置信息VO

			//处理上面的一排按钮!!!
			btn_viewAllMsg = new WLTButton("意见全览", "cascade.gif"); //
			btn_viewAllMsg.putClientProperty("BindBillVO", this.billVO); //
			btn_viewAllMsg.addActionListener(this); //

			WLTButton btn_exportExcel = WLTButton.createButtonByType(WLTButton.LIST_EXPORTEXCEL, "导出意见"); //icon_xls.gif
			btn_exportExcel.setIcon(UIUtil.getImage("icon_xls.gif")); //
			btn_exportExcel.setPreferredSize(new Dimension(90, 23)); //
			histBillList.addBatchBillListButton(new WLTButton[] { btn_viewAllMsg, btn_exportExcel }); //
			histBillList.repaintBillListButton(); //刷新

			//加入勾选框,用于快速过滤..
			String str_myCorpType = null; //
			HashVO[] hvs_myCorps = UIUtil.getParentCorpVOByMacro(1, null, "$本机构"); //
			if (hvs_myCorps != null && hvs_myCorps.length > 0) {
				str_myCorpType = hvs_myCorps[0].getStringValue("corptype"); //机构类型
			}

			JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
			checkPanel.setOpaque(false); //透明!
			combox_lastmsg = new JComboBox(); //经常有需求,只需要显示一个部门最终意见!!所以加上这样一个判断!!
			combox_lastmsg.addItem("全部意见"); //
			combox_lastmsg.addItem("部门最终意见"); //
			combox_lastmsg.setPreferredSize(new Dimension(100, 20)); //
			combox_lastmsg.setFocusable(false); //

			boolean isViewLastMsg = isDefaultViewLastMsg(); //
			if (isViewLastMsg) { //显示最终意见
				combox_lastmsg.setSelectedIndex(1); //
			} else {
				combox_lastmsg.setSelectedIndex(0); //显示所有意见
			}
			combox_lastmsg.addItemListener(this); //
			checkPanel.add(combox_lastmsg); //

			boolean isFindMycorp = false; //是否发现我的机构
			if (lh_blcorp.size() > 1) { //只有存在两个以上的才处理!
				String[] str_checkNames = (String[]) lh_blcorp.toArray(new String[0]); //
				allCheckBoxs = new JCheckBox[str_checkNames.length]; //
				//是否默认勾选上我的部门???邮储要勾选上,但兴业的叶律师总是因为不注意这个勾选而认为有的意见没了!!! 老是说系统有问题,所以干脆默认都放出来!
				boolean isDefaultChooseMydept = TBUtil.getTBUtil().getSysOptionBooleanValue("工作流历史清单是否默认勾选我的所有机构", true);
				for (int i = 0; i < str_checkNames.length; i++) {
					allCheckBoxs[i] = new JCheckBox(str_checkNames[i]); //
					allCheckBoxs[i].setOpaque(false); //
					allCheckBoxs[i].setToolTipText("按住Ctrl/Shift键可以多选"); //
					allCheckBoxs[i].setFocusable(false); //
					if (isDefaultChooseMydept && str_myCorpType != null && str_myCorpType.startsWith(str_checkNames[i])) { //如果我的机构类型正好以这个为开头的!说明我是属于这一溜中的!
						allCheckBoxs[i].setSelected(true); //
						isFindMycorp = true; //
					}
					allCheckBoxs[i].addActionListener(this); //
					checkPanel.add(allCheckBoxs[i]); //
				}
			}
			histBillList.getCustomerNavigationJPanel().add(checkPanel); //

			//如果不是全部意见,或者默认勾选上了某个机构,则要进行一下过滤!
			if (combox_lastmsg.getSelectedIndex() != 0 || isFindMycorp) {
				onFilterDealPoolByBlDeptName(histBillList, null, false); //再次过滤一下,因为直接从页面上取数,所以性能很快!..
			} else {
				setBackgroundBySameDept(histBillList); //将相同的部门的记录的背景色弄成一样!形成一组组的效果!!
				histBillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
			}

			histBillList.getMainScrollPane().getVerticalScrollBar().setValue(0); //滚到最上面
			histBillList.getMainScrollPane().getHorizontalScrollBar().setValue(0); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 兴业后来有复杂的加密校验逻辑,即一些超级管理员,条线管理员是不需要加密处理的!!!这样就在流程分配表中加了一个参数,指定哪些单据类型与业务类型需要加密的!
	 * 
	 * @param _billVO
	 * @return
	 */
	private boolean isJiaMeMsg() {
		//弹出流程监控窗口!!
		//对于是否加密还要做一个判断,一个是:条线超级管理员是不加密的,比如总行法律合规部看所有意见,分行的法律管理员查看所有分行消息!!
		//第二个是:有的流程就是分行层面的报送,根本不需要做逻辑处理! 
		String str_reason = ""; //
		boolean isJiaMi = getTBUtil().getSysOptionBooleanValue("工作流历史记录中是否屏蔽处理意见", true); //默认是加密的
		if (wfAssignHVO == null) {
			return isJiaMi; //
		}

		try {
			String str_openmsgroles = wfAssignHVO.getStringValue("openmsgroles"); //
			if (str_openmsgroles != null) { //如果定义了!
				String[] str_items = TBUtil.getTBUtil().split(str_openmsgroles, ";"); //分割一下!
				String[] str_myAllroles = ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes(); //我的所有角色!!

				for (int i = 0; i < str_items.length; i++) {
					if (TBUtil.getTBUtil().isExistInArray(str_items[i], new String[] { "一般用户", "一般员工", "所有人员", "所有员工", "所有用户", "不加密" })) {
						str_reason = "因为定义了\"一般用户/不加密\"之类的!"; //
						isJiaMi = false; //不加密!!
						break; //
					}

					boolean isFind = false; //
					for (int j = 0; j < str_myAllroles.length; j++) {
						if (str_myAllroles[j].indexOf(str_items[i]) >= 0) {
							str_reason = "因为其中一个条件[" + str_items[i] + "]与我的一个角色[" + str_myAllroles[j] + "]匹配上了!"; //
							isFind = true; //
							break; //
						}
					}
					if (isFind) { //如果找到
						isJiaMi = false; //不加密!!
						break; //
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return isJiaMi; //
	}

	/**
	 * 默认是否显示"部门最终意见"
	 * @return
	 */
	private boolean isDefaultViewLastMsg() {
		//弹出流程监控窗口!!
		//对于是否加密还要做一个判断,一个是:条线超级管理员是不加密的,比如总行法律合规部看所有意见,分行的法律管理员查看所有分行消息!!
		//第二个是:有的流程就是分行层面的报送,根本不需要做逻辑处理! 
		String str_reason = ""; //
		boolean isViewLastMsg = false; //默认是显示所有意见的!
		if (wfAssignHVO == null) {
			return isViewLastMsg; //
		}

		try {
			String str_openmsgroles = wfAssignHVO.getStringValue("viewlastmsgroles"); //显示最终意见的名单!
			if (str_openmsgroles != null) { //如果定义了!
				boolean isBlackList = false; //是否是黑名单
				if (str_openmsgroles.startsWith("!")) {
					str_openmsgroles = str_openmsgroles.substring(1, str_openmsgroles.length()); //
					isBlackList = true; //
				}
				String[] str_items = TBUtil.getTBUtil().split(str_openmsgroles, ";"); //分割一下!
				String[] str_myAllroles = ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes(); //我的所有角色!!

				for (int i = 0; i < str_items.length; i++) {
					if (TBUtil.getTBUtil().isExistInArray(str_items[i], new String[] { "一般用户", "一般员工", "所有人员", "所有员工", "所有用户", "不加密" })) {
						str_reason = "因为定义了\"一般用户/不加密\"之类的!"; //
						isViewLastMsg = true; //这些人显示最终意见!
						break; //
					}

					boolean isFind = false; //
					for (int j = 0; j < str_myAllroles.length; j++) {
						if (str_myAllroles[j].indexOf(str_items[i]) >= 0) {
							str_reason = "因为其中一个条件[" + str_items[i] + "]与我的一个角色[" + str_myAllroles[j] + "]匹配上了!"; //
							isFind = true; //
							break; //
						}
					}

					if (isFind) { //如果找到
						isViewLastMsg = true; //
						System.out.println("只显示最终意见的原因:" + str_reason); //
						break; //
					}
				}

				if (isBlackList) { //如果是黑名单,则再反一下!
					isViewLastMsg = !isViewLastMsg; //
				}

			} else {
				isViewLastMsg = false; //如果显示设置为空,则默认表示不查看所有意见,即默认显示所有意见!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return isViewLastMsg; //
	}

	/**
	 * 根据流程图中的部门名称过滤流程处理意见
	 * @param histBillList
	 * @param _checkBoxs
	 */
	protected void onFilterDealPoolByBlDeptName(BillListPanel histBillList, JCheckBox _clickedCheckBox, boolean _isCtrlDown) {
		HashSet hst_name = new HashSet(); //
		if (allCheckBoxs != null) {
			if (_clickedCheckBox != null && !_isCtrlDown) { //
				for (int i = 0; i < allCheckBoxs.length; i++) {
					if (allCheckBoxs[i] != _clickedCheckBox) { //
						allCheckBoxs[i].setSelected(false); //
					}
				}
			}
			for (int i = 0; i < allCheckBoxs.length; i++) {
				if (allCheckBoxs[i].isSelected()) { //如果已经勾选上
					hst_name.add(allCheckBoxs[i].getText()); //
				}
			}
		}

		if (hst_name.size() == 0 || (allCheckBoxs != null && hst_name.size() == allCheckBoxs.length)) { //如果全部不选或全部都选,则加入所有!
			if (combox_lastmsg.getSelectedIndex() == 0) { //如果选中的是全部意见!!!
				histBillList.clearTable(); //
				histBillList.addBillVOs(initAllBillVOs); //
			} else { //如果是最终意见
				LinkedHashMap hst_dept = new LinkedHashMap(); //
				for (int i = initAllBillVOs.length - 1; i >= 0; i--) {//意见是倒序排列的，故需要倒序取数，才能取到时间最靠后，最终意见【李春娟/2016-12-20】
					String str_dept = initAllBillVOs[i].getStringValue("participant_userdeptname"); //处理人机构名称
					str_dept = trimDeptNameByPos(str_dept); //将【总行-研究规划部-综合研究处】转换成【总行-研究规划部】
					hst_dept.put(str_dept, initAllBillVOs[i]);
				}
				BillVO[] filterVOs = (BillVO[]) hst_dept.values().toArray(new BillVO[0]); //
				histBillList.clearTable(); //
				for (int i = 0; i < filterVOs.length; i++) {
					int li_newRow = histBillList.addEmptyRow(false, false); //
					histBillList.setBillVOAt(li_newRow, filterVOs[i]); //
				}
			}
		} else { //
			histBillList.clearTable(); //先清空数据
			ArrayList al_vos = new ArrayList(); //
			for (int i = 0; i < initAllBillVOs.length; i++) { //循环加入!
				String str_bldept = initAllBillVOs[i].getStringValue("curractivity_bldeptname"); //组名
				if (hst_name.contains(str_bldept)) { //这一行是被勾选上的
					al_vos.add(initAllBillVOs[i]); //加入
				}
			}
			BillVO[] filterVOs = null; //
			if (combox_lastmsg.getSelectedIndex() == 0) { //如果选中的是全部意见!!!
				filterVOs = (BillVO[]) al_vos.toArray(new BillVO[0]);
			} else { //如果只显示部门最后意见!!!则要进行再次过滤!
				LinkedHashMap hst_dept = new LinkedHashMap(); //
				for (int i = 0; i < al_vos.size(); i++) {
					BillVO itemVO = (BillVO) al_vos.get(i); //
					String str_dept = itemVO.getStringValue("participant_userdeptname"); //处理人机构名称
					str_dept = trimDeptNameByPos(str_dept); //将【总行-研究规划部-综合研究处】转换成【总行-研究规划部】
					String str_submittime = itemVO.getStringValue("submittime");
					if (hst_dept.containsKey(str_dept)) { //如果曾经包含了!
						hst_dept.remove(str_dept); //先移掉!!!
						hst_dept.put(str_dept, itemVO);
					} else {
						hst_dept.put(str_dept, itemVO); //加入,因为原来就是按时间排序的,所以这样必然保证后面的会冲掉前面的,即自然存储的是最后一条!
					}
				}
				filterVOs = (BillVO[]) hst_dept.values().toArray(new BillVO[0]); //
			}

			for (int i = 0; i < filterVOs.length; i++) {
				int li_newRow = histBillList.addEmptyRow(false, false); //
				histBillList.setBillVOAt(li_newRow, filterVOs[i]); //
			}
		}
		if (combox_lastmsg.getSelectedIndex() == 0) { //只有显示所有意见时才分组颜色,最后意见一条就一个颜色,没有意义!!
			setBackgroundBySameDept(histBillList); //将相同的部门的记录的背景色弄成一样!形成一组组的效果!!
		} else {
			histBillList.clearItemBackGroundColor(); //
		}
		histBillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
	}

	/**
	 * 设置背景颜色!!!
	 */
	private void setBackgroundBySameDept(BillListPanel _billList) {
		String[] str_colors = new String[] { "E1FAFF", "FFE4DD", "E8D7FF", "D7FED3", "F3F3F3", "FFFFD9", "C6FF8C", "FFB38E", "CC9999", "53FF53" }; //
		HashMap colorMap = new HashMap(); //

		for (int i = 0; i < _billList.getRowCount(); i++) {
			String str_deptName = _billList.getRealValueAtModel(i, "participant_userdeptname"); //本行数据
			str_deptName = trimDeptNameByPos(str_deptName); //将【总行-研究规划部-综合研究处】转换成【总行-研究规划部】
			if (str_deptName == null) {
				str_deptName = ""; //
			}
			if (colorMap.containsKey(str_deptName)) { //如果已注册此颜色
				Color cor = (Color) colorMap.get(str_deptName); //
				_billList.setItemBackGroundColor(cor, i); //
			} else { //如果没有颜色!
				int li_index = colorMap.size() % str_colors.length; //哪个颜色从0..5
				Color cor = getTBUtil().getColor(str_colors[li_index]); //取得颜色!!
				_billList.setItemBackGroundColor(cor, i); //
				colorMap.put(str_deptName, cor); //
			}
		}
	}

	//只取前几位,以中杠分隔!!!
	private String trimDeptNameByPos(String _deptname) {
		int li_maxcount = li_maxDeptLevel; //这个参数以后可以根据其他项目而做调整!,默认是2,但在兴业中遇到过4层机构,折算时都当第3层,而不是第2层!
		if (li_maxcount > 0) { //
			int li_count = getTBUtil().findCount(_deptname, "-"); //看总共有几个中杠???
			if (li_count >= li_maxcount) { //如果超过了!!!
				String str_1 = ""; //
				String str_remain = _deptname; //
				for (int i = 0; i < li_maxcount; i++) {
					str_1 = str_1 + str_remain.substring(0, str_remain.indexOf("-")) + "-"; //第几个中杠??
					str_remain = str_remain.substring(str_remain.indexOf("-") + 1, str_remain.length()); //
				}
				str_1 = str_1.substring(0, str_1.length() - 1); //
				return str_1; //
			} else {
				return _deptname; //
			}
		} else {
			return _deptname; //
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_viewAllMsg) { //【查看全部意见】按钮!!
			onViewAllMsg((WLTButton) _event.getSource()); //
		} else if (_event.getSource() instanceof JCheckBox) {
			boolean isCtrlShift = ((_event.getModifiers() == 17 || _event.getModifiers() == 18) ? true : false);
			JCheckBox clickedCheckBox = (JCheckBox) _event.getSource(); //点击的是哪个勾选框!
			onFilterDealPoolByBlDeptName(histBillList, clickedCheckBox, isCtrlShift); //
		}
	}

	//下拉框变化时的触发
	public void itemStateChanged(ItemEvent e) {
		onFilterDealPoolByBlDeptName(histBillList, null, false); //
	}

	//查看流程意见!
	private void onViewAllMsg(WLTButton _button) {
		BillListPanel billList = (BillListPanel) _button.getBillPanelFrom(); //
		if (billList.getRowCount() <= 0) {
			MessageBox.show(billList, "没有历史意见记录可以查看!"); //
			return; //
		}

		BillVO bindBillVO = (BillVO) _button.getClientProperty("BindBillVO"); //
		BillVO[] checkBillVOs = billList.getCheckedBillVOs(); //
		if (checkBillVOs == null || checkBillVOs.length == 0) { //如果为空则表示所有!
			checkBillVOs = billList.getAllBillVOs(); //
		} else { //
			if (!MessageBox.confirm(billList, "您真的只想导出选中的意见吗?")) { //如果不选ok,则退出
				return; //
			}
		}

		String str_tile = "流程意见"; //
		if (bindBillVO != null) {
			str_tile = bindBillVO.getTempletName() + "-流程意见"; //
		}
		String str_prinstanceid = null; //
		//以前很简陋,现在专门弄了一个类,然后弄了多种风格,期望一劳永逸的解决客户对我们意见导出的不满意...
		BillDialog dialog = new LookWorkflowDealMsgDialog(billList, checkBillVOs, str_tile, str_prinstanceid);
		dialog.setVisible(true); //
	}

	//文件名前面有目录,而且是16进制,需要处理!!!
	private String getRefFileName(String _refId) {
		TBUtil tb = getTBUtil(); //
		StringBuilder sb_name = new StringBuilder(); //
		String[] sr_items = tb.split(_refId, ";"); //
		for (int i = 0; i < sr_items.length; i++) {
			String str_item = sr_items[i].trim(); //
			str_item = str_item.substring(str_item.lastIndexOf("/") + 1, str_item.length()); //去掉前面的目录!
			String str_item_convert = tb.convertHexStringToStr(str_item.substring(str_item.indexOf("_") + 1, str_item.lastIndexOf("."))) + (str_item.substring(str_item.lastIndexOf("."), str_item.length())); //16进制反转!
			sb_name.append(str_item_convert + ";"); //
		}
		return sb_name.toString(); //
	}

	private WorkflowUIUtil getWFUIUtil() {
		if (wfUIUtil != null) {
			return wfUIUtil;
		}
		wfUIUtil = new WorkflowUIUtil(); //
		return wfUIUtil; //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil();
		return tbUtil;
	}

}
