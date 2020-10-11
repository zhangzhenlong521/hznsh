package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.to.mdata.templetvo.TempletItemVOComparator;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.engine.MsgVO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.HFlowLayoutPanel;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;
import cn.com.infostrategy.ui.workflow.msg.MyMsgUIUtil;

/**
 * 工作流客户端工具!!!
 * @author xch
 *
 */
public class WorkflowUIUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	private WorkFlowServiceIfc workFlowService; //

	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * 构造方法
	 */
	public WorkflowUIUtil() {
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil();
		return tbUtil;
	}

	/**
	 * 启协一个流程!!
	 * @param _parent
	 * @param billVO
	 * @return
	 * @throws Exception
	 */
	public String startWorkFlow(Container _parent, BillVO billVO, ActivityVO _startActivityVO) throws Exception {
		if (_startActivityVO != null) {//这里已经有_startActivityVO 启动环节了，故不需要在判断流程了。
			String str_prinstanceid = getWorkFlowService().startWFProcess(_startActivityVO.getProcessid(), billVO, ClientEnvironment.getInstance().getLoginUserID(), _startActivityVO); // 创建一个流程!!
			return str_prinstanceid;
		}
		if (!billVO.isHaveKey("billtype") || !billVO.isHaveKey("busitype")) {
			MessageBox.show(_parent, "No BillType and Busitype,Cant't be deal!", WLTConstants.MESSAGE_WARN); //
			return null; //
		}

		if (billVO.getEditType().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { // 如果是处理新增状态,则不能提交工作流
			MessageBox.show(_parent, "记录还没有保存,请先保存!", WLTConstants.MESSAGE_WARN); //
			return null; //
		}

		String str_billtype = billVO.getStringValue("billtype"); // 单据类型..
		String str_busitype = billVO.getStringValue("busitype"); // 业务类型..
		try {
			String str_sql = "select * from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"; //
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, str_sql); //
			if (vos.length == 0) {
				MessageBox.show(_parent, "没有为BillType[" + str_billtype + "],BusiType[" + str_busitype + "]指定流程,不能进行流程处理!", WLTConstants.MESSAGE_INFO); //
				return null;
			}

			String str_processId = vos[0].getStringValue("processid"); //
			if (str_processId == null || str_processId.trim().equals("")) {
				MessageBox.show(_parent, "有为BillType[" + str_billtype + "],BusiType[" + str_busitype + "]指定流程的流程ID为空,不能进行流程处理!", WLTConstants.MESSAGE_INFO); //
				return null;
			}

			if (_startActivityVO == null) {
				MessageBox.show(_parent, "启动环节为空,无法启动流程!");//
				return null;
			}
			String str_prinstanceid = getWorkFlowService().startWFProcess(str_processId, billVO, ClientEnvironment.getInstance().getLoginUserID(), _startActivityVO); // 创建一个流程!!
			return str_prinstanceid; //
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.show(_parent, "启动流程失败,原因:" + ex.getMessage(), WLTConstants.MESSAGE_ERROR); //
			return null;
		}
	}

	public String startWorkFlow(Container _parent, BillVO billVO) throws Exception {
		ActivityVO[] startVOs = getStartActivityVOs(_parent, billVO); //
		if (startVOs != null && startVOs.length > 0) {
			ActivityVO startActivityVO = null; //
			if (startVOs.length == 1) { //如果只有一个可以启动,则启动之
				startActivityVO = startVOs[0]; //
			} else { //否则弹出流程图,并将可以启动的地方标出来让用户选择一个返回!
				WorkFlowChooseStartActivityDialog dialog = new WorkFlowChooseStartActivityDialog(_parent, startVOs); //
				dialog.setVisible(true); //
				if (dialog.getCloseType() == 1) { //如果是确定返回!
					startActivityVO = dialog.getReturnActivityVO(); //
				} else {
					return null; //
				}
			}

			if (startActivityVO != null) {
				return startWorkFlow(_parent, billVO, startActivityVO); //
			} else {
				MessageBox.show(_parent, "启动环节为空,流程无法启动!");
				return null;
			}
		} else {
			MessageBox.show(_parent, "没有找到一个启动环节!流程无法启动!");
			return null;
		}
	}

	/**
	 * 取得所有可以启动的环节
	 * @return
	 */
	public ActivityVO[] getStartActivityVOs(Container _parent, BillVO billVO) throws Exception {
		if (billVO.getEditType().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { // 如果是处理新增状态,则不能提交工作流
			MessageBox.show(_parent, "记录还没有保存,请先保存!", WLTConstants.MESSAGE_WARN); //
			return null; //
		}
		String str_billtype = billVO.getStringValue("billtype"); // 单据类型..
		String str_busitype = billVO.getStringValue("busitype"); // 业务类型..
		String str_processId = null;
		String str_processCode = null;
		String str_processName = null;
		if ("".equals(str_billtype)) {//如果单据类型为空，则直接选择非流程文件内的流程进行发起【李春娟/2012-05-18】
			BillListDialog listdialog = new BillListDialog(_parent, "该表单未配置单据类型,请选择流程", "PUB_WF_PROCESS_CODE3"); //);
			BillListPanel listpanel_wf = listdialog.getBilllistPanel();
			listpanel_wf.setQuickQueryPanelVisiable(true);
			listpanel_wf.setDataFilterCustCondition("cmpfileid is null");//
			listdialog.setVisible(true);
			if (listdialog.getCloseType() == 1) {
				str_processId = listdialog.getReturnBillVOs()[0].getStringValue("id");
				str_processCode = listdialog.getReturnBillVOs()[0].getStringValue("code");
				str_processName = listdialog.getReturnBillVOs()[0].getStringValue("name");
			} else {
				return null;
			}
		} else if ("".equals(str_busitype)) {//如果单据类型不为空，但业务类型为空，则找出所有该单据类型配置的流程
			BillListDialog listdialog = new BillListDialog(_parent, "该表单未配置业务类型,请选择流程", "PUB_WF_PROCESS_CODE3"); //);
			BillListPanel listpanel_wf = listdialog.getBilllistPanel();
			listpanel_wf.setQuickQueryPanelVisiable(true);
			listpanel_wf.setDataFilterCustCondition("id in(select processid from pub_workflowassign where billtypecode='" + str_billtype + "')");///////////
			listdialog.setVisible(true);
			if (listdialog.getCloseType() == 1) {
				str_processId = listdialog.getReturnBillVOs()[0].getStringValue("id");
				str_processCode = listdialog.getReturnBillVOs()[0].getStringValue("code");
				str_processName = listdialog.getReturnBillVOs()[0].getStringValue("name");
			} else {
				return null;
			}
		} else {//如果单据类型和业务类型都不为空，则从流程分配中查找对应的流程
			String str_sql = "select pub_workflowassign.processid,pub_wf_process.code processcode,pub_wf_process.name processname  from pub_workflowassign left outer join pub_wf_process on pub_workflowassign.processid=pub_wf_process.id where pub_workflowassign.billtypecode='" + str_billtype + "' and pub_workflowassign.busitypecode='" + str_busitype + "'"; //
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, str_sql); //
			if (vos.length == 0) {
				MessageBox.show(_parent, "没有为BillType[" + str_billtype + "],BusiType[" + str_busitype + "]指定流程,不能进行流程处理!", WLTConstants.MESSAGE_INFO); //
				return null;
			}

			str_processId = vos[0].getStringValue("processid"); //
			str_processCode = vos[0].getStringValue("processcode"); //
			str_processName = vos[0].getStringValue("processname"); //

			if (str_processId == null || str_processId.trim().equals("")) {
				MessageBox.show(_parent, "有为BillType[" + str_billtype + "],BusiType[" + str_busitype + "]指定流程的流程ID为空,不能进行流程处理!", WLTConstants.MESSAGE_INFO); //
				return null;
			}
		}

		WorkFlowServiceIfc workFlowservice = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
		ActivityVO[] returnActivityVOS = workFlowservice.getStartActivityVOs(str_processId, ClientEnvironment.getInstance().getLoginUserID()); //
		if (returnActivityVOS == null || returnActivityVOS.length == 0) {
			throw new WLTAppException("根据单据类型[" + str_billtype + "],业务类型[" + str_busitype + "]成功找到品配的流程,流程编码是:[" + str_processCode + "],流程名称是:[" + str_processName + "].\r\n但没有在该流程上找到一个可以启动的环节!你需要在启动环节上定义[可以启动的角色]配置项,如果在该配置项上加入名为[所有人员]的角色则表示所有人都可以启动该流程!"); //
		}
		return returnActivityVOS; //
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

	/**
	 * 工作流通用处理!
	 * @param _type 有[deal][reject][monitor]
	 * @param _parent
	 * @param _billList
	 */
	public void dealWorkFlowAcion(String _type, BillListPanel _billList) {
		if (_type.equalsIgnoreCase("deal")) { //如果是处理

		} else if (_type.equalsIgnoreCase("reject")) { //如果是退回!

		} else if (_type.equalsIgnoreCase("monitor")) { //如果是监控

		}
	}

	public BillListPanel getWFHistoryDealPoolBillList(HashVO[] _hvs, BillVO _cardBillVO) throws Exception {
		return getWFHistoryDealPoolBillList(_hvs, _cardBillVO, false); //
	}

	/**
	 * 取得流程处理历史列表面板,则于WorkFlowProcessPanel与流程监控面板中都用到该面板,为了重用所以放在这里!!!
	 * @param _hvs
	 * @return
	 */
	public BillListPanel getWFHistoryDealPoolBillList(HashVO[] _hvs, BillVO _cardBillVO, boolean _isHiddenBtnVisiable) throws Exception {
		ArrayList al_temp = new ArrayList(); //
		for (int i = 0; i < _hvs.length; i++) { //遍历插入数据!!!
			if ("Y".equals(_hvs[i].getStringValue("issubmit"))) { //只是提交过的才显示!!!
				al_temp.add(_hvs[i]); //
			}
		}
		TBUtil tbUtil = new TBUtil(); //
		HashVO[] submitedHVS = (HashVO[]) al_temp.toArray(new HashVO[0]); //
		tbUtil.sortHashVOs(submitedHVS, new String[][] { { "submittime", "N", "N" } }); //排序一把,即按照提交时间的升序排序!!!

		Pub_Templet_1VO templetVO = UIUtil.getPub_Templet_1VO(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_wf_dealpool_CODE1.xml")); //
		Pub_Templet_1_ItemVO[] templetItemVOs = templetVO.getItemVos(); // 各项子表

		//邮储项目上遇到一定要重新排序这个,即要将流程意见放在前面!
		String str_wfmsgListOrder = TBUtil.getTBUtil().getSysOptionStringValue("工作处理意见列表各列顺序", null); //
		if (str_wfmsgListOrder != null && !str_wfmsgListOrder.trim().equals("")) { //
			String[] str_orderItems = TBUtil.getTBUtil().split(str_wfmsgListOrder, ";"); //
			Arrays.sort(templetItemVOs, new TempletItemVOComparator(str_orderItems)); //
		}

		final BillListPanel histBillList = new BillListPanel(templetVO, false); //
		if (!TBUtil.getTBUtil().getSysOptionBooleanValue("工作处理是否支持上传附件", true)) { //邮储项目中提出不要上传附件,说是逼着人们在意见中填!!! 因为附件内容导不出来!
			histBillList.setItemVisible("submitmessagefile", false); //如果不支持,则隐藏!!!
		}
		if (_isHiddenBtnVisiable) {
			histBillList.setHiddenUntitlePanelBtnvVisiable(true); //
		}
		histBillList.initialize(); //手工初始化!
		histBillList.setRowNumberChecked(true); //
		//histBillList.setItemVisible("submitisapprove", false); //隐藏掉
		histBillList.setItemVisible("submitmessage_viewreason", false); //隐藏掉

		LinkedHashSet lh_blcorp = new LinkedHashSet(); //
		for (int i = 0; i < submitedHVS.length; i++) { //遍历插入数据!!!
			int li_row = histBillList.newRow(false); //
			histBillList.getRowNumberVO(li_row).setRecordHVO(submitedHVS[i]); //
			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("id")), li_row, "id"); //任务主键
			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("prinstanceid")), li_row, "prinstanceid"); //实例id
			histBillList.setValueAt(new StringItemVO(getWFActivityName(submitedHVS[i])), li_row, "curractivity_wfname"); //当前步骤!!!

			//当前环节所属组,邮储项目中遇到使用组进行计算与过滤!
			if (submitedHVS[i].getStringValue("parentinstanceid", "").equals("")) { //如果父流程为空,则表示是主流程
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("curractivity_belongdeptgroup")), li_row, "curractivity_bldeptname"); //当前环节所属组
			} else { //是子流程
				histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("prinstanceid_fromparentactivitybldeptgroup")), li_row, "curractivity_bldeptname"); //当前环节所属组
			}
			lh_blcorp.add(histBillList.getRealValueAtModel(li_row, "curractivity_bldeptname")); //当前环节所属部门..

			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("participant_user")), li_row, "participant_user"); //人员id
			String str_realsubmitername = submitedHVS[i].getStringValue("realsubmitername"); //实际提交人名称!一直有客户提,要知道到底是谁干的!
			String str_participant_accrusername = submitedHVS[i].getStringValue("participant_accrusername"); //授权人名称!!
			if (str_participant_accrusername != null) { //如果有授权
				if (str_participant_accrusername.indexOf("/") > 0) {
					str_participant_accrusername = str_participant_accrusername.substring(str_participant_accrusername.indexOf("/") + 1, str_participant_accrusername.length()); //
				}
				String str_realsubmitername_real = null; //
				if (str_realsubmitername.indexOf("/") > 0) {
					str_realsubmitername_real = str_realsubmitername.substring(str_realsubmitername.indexOf("/") + 1, str_realsubmitername.length()); //
				} else {
					str_realsubmitername_real = str_realsubmitername;
				}
				if (!str_realsubmitername_real.equals(str_participant_accrusername)) { //如果两者不等!
					str_realsubmitername = str_participant_accrusername + "(授权" + str_realsubmitername + ")"; //始终显示 授权人(授权被授权人)的形式
				}
			}

			//邮储遇到死活不要前面的工号
			if (!TBUtil.getTBUtil().getSysOptionBooleanValue("工作流处理意见列表中的人员是否显示工号", true)) {
				if (str_realsubmitername.indexOf("/") > 0) {
					str_realsubmitername = str_realsubmitername.substring(str_realsubmitername.indexOf("/") + 1, str_realsubmitername.length()); //
				}
			}
			histBillList.setValueAt(new StringItemVO(str_realsubmitername), li_row, "participant_username"); //人员名称
			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("realsubmitcorpname")), li_row, "participant_userdeptname"); //机构名称
			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("issubmit")), li_row, "issubmit"); //是否提交
			histBillList.setValueAt(new StringItemVO(submitedHVS[i].getStringValue("submittime")), li_row, "submittime"); //提交时间

			//提交/退加,先隐藏掉,以后会根据一个参数决定是否显示!!
			//			String str_isapprove = submitedHVS[i].getStringValue("submitisapprove", ""); //
			//			StringItemVO sivo_isapprove = null; //
			//			if (str_isapprove.equals("N")) {
			//				sivo_isapprove = new StringItemVO("退回"); //
			//				sivo_isapprove.setForeGroundColor("FF0000"); //
			//			} else {
			//				sivo_isapprove = new StringItemVO("提交"); //
			//			}			
			//			histBillList.setValueAt(sivo_isapprove, li_row, "submitisapprove"); //处理类型
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
		}
		setBackgroundBySameDept(histBillList); //将相同的部门的记录的背景色弄成一样!形成一组组的效果!!
		histBillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //

		WLTButton btn_viewAllMsg = new WLTButton("意见全览", "cascade.gif"); //
		btn_viewAllMsg.putClientProperty("BindBillVO", _cardBillVO); //
		btn_viewAllMsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onViewAllMsg((WLTButton) e.getSource()); //
			}
		}); //

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
		if (lh_blcorp.size() > 1) { //只有存在两个以上的才处理!
			JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
			checkPanel.setOpaque(false); //
			String[] str_checkNames = (String[]) lh_blcorp.toArray(new String[0]); //
			final JCheckBox[] checkBoxs = new JCheckBox[str_checkNames.length]; //
			boolean isFindMycorp = false; //是否发现我的机构
			for (int i = 0; i < str_checkNames.length; i++) {
				checkBoxs[i] = new JCheckBox(str_checkNames[i]); //
				checkBoxs[i].setOpaque(false); //
				checkBoxs[i].setFocusable(false); //
				if (str_myCorpType != null && str_myCorpType.startsWith(str_checkNames[i])) { //如果我的机构类型正好以这个为开头的!说明我是属于这一溜中的!
					checkBoxs[i].setSelected(true); //
					isFindMycorp = true; //
				}
				checkBoxs[i].addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent _event) {
						onFilterDealPoolByBlDeptName(histBillList, checkBoxs); //
					}
				}); //
				checkPanel.add(checkBoxs[i]); //
			}
			histBillList.getCustomerNavigationJPanel().add(checkPanel); //
			if (isFindMycorp) { //如果匹配上我的所在行,则直接计算!
				onFilterDealPoolByBlDeptName(histBillList, checkBoxs); //
			}
		}
		return histBillList; //
	}

	/**
	 * 根据流程图中的部门名称过滤流程处理意见
	 * @param histBillList
	 * @param _checkBoxs
	 */
	protected void onFilterDealPoolByBlDeptName(BillListPanel histBillList, JCheckBox[] _checkBoxs) {
		HashSet hst_name = new HashSet(); //
		for (int i = 0; i < _checkBoxs.length; i++) {
			if (_checkBoxs[i].isSelected()) { //如果已经勾选上
				hst_name.add(_checkBoxs[i].getText()); //
			}
		}
		BillVO[] oldBillVOs = (BillVO[]) histBillList.getClientProperty("OldAllBillVOs"); //
		if (oldBillVOs == null) { //
			oldBillVOs = histBillList.getAllBillVOs(); //
			histBillList.putClientProperty("OldAllBillVOs", oldBillVOs); //
		}
		if (hst_name.size() == 0 || hst_name.size() == _checkBoxs.length) { //如果全部不选或全部都选,则加入所有!
			histBillList.clearTable(); //
			histBillList.addBillVOs(oldBillVOs); //
		} else { //
			histBillList.clearTable(); //先清空数据
			for (int i = 0; i < oldBillVOs.length; i++) { //循环加入!
				String str_bldept = oldBillVOs[i].getStringValue("curractivity_bldeptname"); //
				if (hst_name.contains(str_bldept)) { //这一行是被勾选上的
					int li_newRow = histBillList.addEmptyRow(false, false); //
					histBillList.setBillVOAt(li_newRow, oldBillVOs[i]); //
				}
			}
		}
		setBackgroundBySameDept(histBillList); //将相同的部门的记录的背景色弄成一样!形成一组组的效果!!
		histBillList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
	}

	//curractivity_wfname
	private void setBackgroundBySameDept(BillListPanel _billList) {
		String[] str_colors = new String[] { "E1FAFF", "FFE4DD", "E8D7FF", "D7FED3", "F3F3F3", "FFFFD9", "C6FF8C", "FFB38E", "CC9999", "53FF53" }; //
		HashMap colorMap = new HashMap(); //
		for (int i = 0; i < _billList.getRowCount(); i++) {
			String str_deptName = _billList.getRealValueAtModel(i, "participant_userdeptname"); //本行数据
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

	/**
	 * 催督办  还缺少一个页签即我对于本流程督办记录 且督办过的人要给予提示
	 * @return
	 */
	public void dealCDB(Container _parent, final String processName, String wf_instanceid) throws Exception {
		if (wf_instanceid == null || "".equals(wf_instanceid)) {
			MessageBox.show(_parent, "流程未启动没有需要催督办的人员!");
			return;
		}
		wf_instanceid = UIUtil.getStringValueByDS(null, " select rootinstanceid from pub_wf_prinstance where id = '" + wf_instanceid + "' ");
		if (wf_instanceid == null || "".equals(wf_instanceid)) {
			MessageBox.show(_parent, "相关流程信息已被删除!请您确认!");
			return;
		}
		String[][] wfi = UIUtil.getStringArrayByDS(null, " select status, currowner, curractivityname from pub_wf_prinstance where id = '" + wf_instanceid + "' ");
		if (wfi == null || wfi.length < 1) {
			MessageBox.show(_parent, "相关流程信息已被删除!请您确认!");
			return;
		}
		String[][] undealBill = null;
		if ("END".equals(wfi[0][0])) {//主流程已经结束 也应该弹出 可以查看催督办的记录
			MessageBox.show(_parent, "相关流程已经结束!请您确认!");
			return;
		} else {
			undealBill = UIUtil.getStringArrayByDS(null, " select t1.dealuser,t1.dealusername,t1.dealusercorpname, t3.curractivityname, t3.curractivity from pub_task_deal t1 left join pub_wf_dealpool t2 on t2.id = t1.prdealpoolid left join pub_wf_prinstance t3 on t1.prinstanceid=t3.id where t1.rootinstanceid='" + wf_instanceid
					+ "' and t2.issubmit='N' and t1.isccto='N' and t2.isprocess='N'  order by t3.curractivityname desc ");
		}
		final BillDialog bd = new BillDialog(_parent, "催督办");
		DefaultTMO tmo = new DefaultTMO("未处理流程人员", new String[][] { { "环节名称", "环节名称", "250", "Y" }, { "未处理人", "未处理人", "100", "Y" }, { "未处理人ID", "未处理人ID", "100", "N" }, { "所属部门", "所属部门", "100", "Y" }, { "手机号", "手机号", "100", "Y" } });
		final BillListPanel bl = new BillListPanel(tmo);
		bl.setCanShowCardInfo(false);
		bl.setRowNumberChecked(true);
		bl.getTitleLabel().setFont(LookAndFeel.font);
		if (undealBill != null && undealBill.length > 0) {
			for (int i = 0; i < undealBill.length; i++) {
				if (undealBill[i][0] != null && !"".equals(undealBill[i][0])) {
					String mobile = UIUtil.getStringValueByDS(null, " select mobile from pub_user where id = '" + undealBill[i][0] + "' ");
					HashMap _values = new HashMap();
					if (undealBill[i][3] == null || "".equals(undealBill[i][3])) {
						String[][] activityname = UIUtil.getStringArrayByDS(null, "select wfname, belongdeptgroup from pub_wf_activity where id='" + undealBill[i][4] + "' ");
						if (activityname != null && activityname.length > 0) {
							if (activityname[0][1] != null && !"".equals(activityname[0][1])) {
								_values.put("环节名称", activityname[0][1] + "-" + activityname[0][0]);
							} else {
								_values.put("环节名称", activityname[0][0]);
							}
						}
					} else {
						_values.put("环节名称", undealBill[i][3]);
					}
					_values.put("未处理人", undealBill[i][1]);
					_values.put("未处理人ID", undealBill[i][0]);
					if (undealBill[i][2] == null || "".equals(undealBill[i][2])) {
						String corpname = getUserCorp(undealBill[i][0]);
						if (corpname != null && !"".equals(corpname)) {
							_values.put("所属部门", corpname);
						}
					} else {
						_values.put("所属部门", undealBill[i][2]);
					}
					_values.put("手机号", mobile);
					bl.insertRowWithInitStatus(-1, _values);
					bl.setCheckedRow(bl.getRowCount() - 1, true);
				}
			}
		}
		JPanel tmpPanel = new JPanel(new BorderLayout());
		tmpPanel.setBackground(LookAndFeel.defaultShadeColor1);
		tmpPanel.setUI(new WLTPanelUI(BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE, Color.WHITE, false));
		JPanel panel = new WLTPanel(new BorderLayout(0, 5));
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 15, 0)); //
		panel.setBackground(LookAndFeel.cardbgcolor);
		panel.setOpaque(false);
		tmpPanel.add(bl, BorderLayout.NORTH);
		JLabel label = new JLabel("※督办意见", JLabel.LEFT);
		label.setFont(new Font("System", Font.PLAIN, 12));
		label.setForeground(new Color(149, 149, 255));
		label.setOpaque(false);
		panel.add(label, BorderLayout.NORTH);
		final WLTTextArea textarea_msg = new WLTTextArea(10, 50);
		textarea_msg.setText(processName + "有您的未处理任务，请您及时处理！");
		textarea_msg.setLineWrap(true);
		textarea_msg.setForeground(LookAndFeel.inputforecolor_enable);
		textarea_msg.setBackground(LookAndFeel.inputbgcolor_enable);
		panel.add(new JScrollPane(textarea_msg), BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(600, 200));
		JLabel ifsendMsg = new JLabel("※是否发送短信", JLabel.LEFT);
		ifsendMsg.setFont(new Font("System", Font.PLAIN, 12));
		ifsendMsg.setForeground(new Color(149, 149, 255));
		ifsendMsg.setOpaque(false);
		final JCheckBox jb = new JCheckBox();
		final boolean ischeck = getTBUtil().getSysOptionBooleanValue("催督办是否有发短信勾选框", false);
		if (ischeck) {
			jb.setSelected(true);
			jb.setOpaque(false);
			panel.add(new HFlowLayoutPanel(new JComponent[] { ifsendMsg, jb }), BorderLayout.SOUTH);
		}
		HFlowLayoutPanel hflow = new HFlowLayoutPanel(new JComponent[] { panel });
		hflow.setOpaque(false);
		tmpPanel.add(hflow, BorderLayout.CENTER);
		JPanel btnpanel = new JPanel(new FlowLayout());
		btnpanel.setOpaque(false);
		WLTButton confirm = new WLTButton("确定");
		WLTButton cancel = new WLTButton("取消");
		btnpanel.add(confirm);
		btnpanel.add(cancel);
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BillVO[] checkedvos = bl.getCheckedBillVOs();
				String senduserids = "";
				boolean ifhavenullMb = false;
				if (checkedvos == null || checkedvos.length <= 0) {
					MessageBox.show(bd, "请选择需要督办的人员!");
					return;
				} else {
					StringBuffer receiveids = new StringBuffer();
					for (int i = 0; i < checkedvos.length; i++) {
						if (checkedvos[i].getStringValue("未处理人ID") != null && !"".equals(checkedvos[i].getStringValue("未处理人ID"))) {
							receiveids.append(checkedvos[i].getStringValue("未处理人ID") + ";");
						}
						if (checkedvos[i].getStringValue("手机号") == null || "".equals(checkedvos[i].getStringValue("手机号"))) {
							ifhavenullMb = true;
						}
					}
					if (receiveids.length() > 0) {
						senduserids = ";" + receiveids.toString();
					}
				}
				if (textarea_msg.getText() == null || "".equals(textarea_msg.getText().trim())) {
					MessageBox.show(bd, "请填写督办意见!");
					return;
				} else {
					try {
						String zfj = getTBUtil().getSysOptionStringValue("数据库字符集", "GBK");//以前只判断GBK，如果是UTF-8，一个汉字占三个字节，故校验可能通过，但保存数据库报错，故设置该参数【李春娟/2016-04-26】
						int li_length = textarea_msg.getText().getBytes(zfj).length;
						if (li_length > 3000) {
							if ("UTF-8".equalsIgnoreCase(zfj)) {//这里应该将所有占三字节的字符集列举出来
								MessageBox.show(bd, "督办意见不能超过1000个字!");
							} else {
								MessageBox.show(bd, "督办意见不能超过1500个字!");
							}
							return;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				ArrayList msgs = new ArrayList();
				int msgtype = -1;
				if (ifhavenullMb && ischeck && jb.isSelected()) {
					msgtype = MessageBox.showOptionDialog(bd, "选择的人员存在手机号为空的情况,您确定发送吗?", "提示", new String[] { "确定", "取消" }, 0);
				} else {
					msgtype = MessageBox.showOptionDialog(bd, "您确定发送吗?", "提示", new String[] { "确定", "取消" }, 0);
				}
				if (msgtype == -1 || msgtype == 1) {
					return;
				}
				try {
					MsgVO msg = new MsgVO();
					msg.setMsgtype("催督办");
					msg.setSender(ClientEnvironment.getCurrSessionVO().getLoginUserId());
					msg.setSendercorp(ClientEnvironment.getCurrLoginUserVO().getBlDeptId());
					msg.setIsdelete("N");
					msg.setFunctiontype("sysmsg");
					msg.setMsgcontent(textarea_msg.getText());
					msg.setMsgtitle(processName + "-工作流催督办");
					msg.setReceiver(senduserids);
					msg.setCreatetime(getTBUtil().getCurrTime());
					if (ischeck && !jb.isSelected()) {
						msg.setState("不发短信");
					}
					msgs.add(msg);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				if (new MyMsgUIUtil().commSendMsg(msgs)) {
					if (ischeck && jb.isSelected()) {
						MessageBox.show(bd, "发送成功!在接收人任务中心的未读消息和您的已发消息中可以查看此条督办信息!系统同时会给接收人发送短信信息!");
					} else {
						MessageBox.show(bd, "发送成功!在接收人任务中心的未读消息和您的已发消息中可以查看此条督办信息!");
					}
					bd.dispose();
				} else {
					MessageBox.show(bd, "发送失败!");
					bd.dispose();
				}
			}
		});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bd.dispose();
			}
		});
		tmpPanel.add(btnpanel, BorderLayout.SOUTH);
		bd.setSize(620, 500);
		bd.add(tmpPanel);
		bd.locationToCenterPosition();
		bd.setVisible(true);
	}

	private String getUserCorp(String _loginUserid) throws Exception {
		String str_myCorpId = null;
		String str_myCorpName = null;
		HashVO[] hvs_myCorpInfo = UIUtil.getHashVoArrayByDS(null, "select t1.userid,t1.userdept,t1.isdefault,t2.corptype,t2.DISPATCHNAME userdeptname,t2.extcorptype from pub_user_post t1 left join pub_corp_dept t2 on t1.userdept=t2.id where t1.userid='" + _loginUserid + "'"); //找出我的所在机构!!
		if (hvs_myCorpInfo != null && hvs_myCorpInfo.length > 0) {
			for (int i = 0; i < hvs_myCorpInfo.length; i++) {
				if ("Y".equals(hvs_myCorpInfo[i].getStringValue("isdefault"))) {
					str_myCorpId = hvs_myCorpInfo[i].getStringValue("userdept");
					str_myCorpName = hvs_myCorpInfo[i].getStringValue("userdeptname");
					break;
				}
			}
			if (str_myCorpId == null) {
				str_myCorpId = hvs_myCorpInfo[0].getStringValue("userdept");
				str_myCorpName = hvs_myCorpInfo[0].getStringValue("userdeptname");
			}
		}
		HashMap a = UIUtil.getCommonService().getTreePathNameByRecords(null, "pub_corp_dept", "id", "name", "parentid", new String[] { str_myCorpId });
		if (a != null && a.size() > 0) {
			return (String) a.get(str_myCorpId);
		}
		return "";
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

	//计算当前环节名称,则于涉及到部门矩阵,子流程会办等,要加上这些信息!!
	public String getWFActivityName(HashVO _hvo) {
		String str_FromParentWFActivityName = _hvo.getStringValue("prinstanceid_fromparentactivityName"); //父流程的环节名称
		String str_curractivityBLDeptName = _hvo.getStringValue("curractivity_belongdeptgroup"); //当前环节的矩阵
		String str_curractivityName = _hvo.getStringValue("curractivity_wfname"); //环节名称
		StringBuilder sb_text = new StringBuilder(); //
		//sb_text.append("[");
		if (str_FromParentWFActivityName != null && !str_FromParentWFActivityName.trim().equals("")) { //如果父流程的环节不为空,即这是个子流程,所以要记下父流程的来源环节名称!
			sb_text.append(str_FromParentWFActivityName + "-"); //
		}
		if (str_curractivityBLDeptName != null && !str_curractivityBLDeptName.trim().equals("")) { //如果所属的部门矩阵不为空!
			sb_text.append(str_curractivityBLDeptName + "-"); //
		}
		sb_text.append(str_curractivityName); //应兴业的石瑜的强烈要求加上当前环节,即必须知道到了哪一步!!后来想想也是有道理的! 只不过界面有点乱!
		//sb_text.append("]");
		String str_return = sb_text.toString();
		str_return = getTBUtil().replaceAll(str_return, "\r", ""); //
		str_return = getTBUtil().replaceAll(str_return, "\n", ""); //
		return str_return; //
	}

	/**
	 * 复制工作流
	 * @param _hvo
	 * @param _old_flowid
	 * @param _type
	 * @throws Exception
	 */
	public void CopyFlow(HashVO _hvo, String _old_flowid, int _type) throws Exception {
		getWorkFlowService().CopyFlow(_hvo, _old_flowid, _type);
	}

	//草稿箱中需要隐藏的字段,其实就是所有的字段(13个)!
	public String[] getDraftTaskHiddenFields() {
		return new String[] { "task_curractivityname", "task_creatername", "task_createtime", "task_createrdealmsg", "task_realdealusername", "task_dealtime", "task_dealmsg", "prins_curractivityname", "prins_lastsubmitername", "prins_lastsubmittime", "prins_lastsubmitmsg", "prins_mylastsubmittime", "prins_mylastsubmitmsg" }; //
	}

	//待办任务面板中隐藏的字段!
	public String[] getDealTaskHiddenFields() {
		return new String[] { "task_realdealusername", "task_dealtime", "task_dealmsg", "prins_curractivityname", "prins_lastsubmitername", "prins_lastsubmittime", "prins_lastsubmitmsg", "prins_mylastsubmittime", "prins_mylastsubmitmsg" };
	}

	//待办箱中需要显示的字段!
	public String[] getDealTaskShowFields() {
		String[][] str_data = getDealTaskShowFieldNames(); //
		String[] str_rt = new String[str_data.length]; //
		for (int i = 0; i < str_rt.length; i++) {
			str_rt[i] = str_data[i][0];
		}
		return str_rt; //
	}

	//待办箱中需要显示的字段!
	public String[][] getDealTaskShowFieldNames() {
		return new String[][] { { "task_curractivityname", "当前环节", "文本框", "120", "400", "Y" }, //
				{ "task_creatername", "提交人", "文本框", "75", "140", "Y" },//
				{ "task_createtime", "提交时间", "文本框", "120", "138", "N" }, // 
				{ "task_createrdealmsg", "提交意见", "多行文本框", "135", "400*100", "Y" } }; //
	}

	//待办任务面板中隐藏的字段!
	public String[] getOffTaskHiddenFields() {
		return new String[] { "task_creatername", "task_createtime", "task_createrdealmsg", "prins_curractivityname", "prins_lastsubmitername", "prins_lastsubmittime", "prins_lastsubmitmsg", "prins_mylastsubmittime", "prins_mylastsubmitmsg" };
	}

	//待办箱中需要显示的字段!
	public String[] getOffTaskShowFields() {
		String[][] str_data = getOffTaskShowFieldNames(); //
		String[] str_rt = new String[str_data.length]; //
		for (int i = 0; i < str_rt.length; i++) {
			str_rt[i] = str_data[i][0];
		}
		return str_rt; //
	}

	//待办箱中需要显示的字段!
	public String[][] getOffTaskShowFieldNames() {
		return new String[][] { { "task_curractivityname", "当前环节", "文本框", "120", "400", "Y" }, //
				{ "task_realdealusername", "处理人", "文本框", "75", "140", "Y" }, //
				{ "task_dealtime", "处理时间", "文本框", "120", "138", "N" }, //
				{ "task_dealmsg", "处理意见", "多行文本框", "135", "400*100", "Y" } }; //
	}

	/**
	 * 生成待办任务的SQL,因为首页与风格模板中都会用到,所以封装在这里!!
	 * 以后这里还要关联流程实例表pub_wf_prinstance,查询出当前到了哪一阶段,到了哪个人!当前的流程状态!!!
	 * @param _tableName
	 * @param _pkName
	 * @param _loginUserId
	 * @param _appendCondition
	 * @return
	 */
	public String getDealTaskSQL(String _templetCode, String _queryTableName, String _savedTableName, String _pkName, String _loginUserId, String _appendCondition, boolean _isSuperShow) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		sb_sql.append("t1.*,"); //getCommColumns(1)因为待办箱与已办箱中的许多列是一样的,所以做了个可重用的方法
		sb_sql.append("t2.* ");
		sb_sql.append("from v_pub_task_deal t1 left join " + _queryTableName + " t2 on t2." + _pkName + "=t1.task_pkvalue "); //从查询表取
		sb_sql.append("where 1=1 ");
		sb_sql.append("and t1.task_templetcode='" + _templetCode + "' "); //必须是对应的模板,因为可以有两个功能点,存的是一张表,但模板不一样！
		sb_sql.append("and t1.task_tabname='" + _savedTableName + "' "); //表名必须是保存表!
		if (!_isSuperShow) {
			sb_sql.append("and (t1.task_dealuser='" + _loginUserId + "' or t1.task_accruserid='" + _loginUserId + "') "); //必须是本人的消息,可能还包括授权的!!
		}
		if (_appendCondition != null) {
			sb_sql.append(_appendCondition); //
		}
		sb_sql.append(" order by t1.task_createtime desc");
		return sb_sql.toString(); //
	}

	/**
	 * 生成已办任务的SQL,因为首页与风格模板中都会用到,所以封装在这里!!
	 * 以后这里还要关联流程实例表pub_wf_prinstance,查询出当前到了哪一阶段,到了哪个人!当前的流程状态!!!
	 * @param _queryTableName
	 * @param _savedTableName
	 * @param _pkName
	 * @param _loginUserId
	 * @param _appendCondition
	 * @return
	 */
	public String getOffTaskSQL(String _templetCode, String _queryTableName, String _savedTableName, String _pkName, String _loginUserId, String _appendCondition, boolean _isSuperShow) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		//sb_sql.append("t1.task_taskoffid,"); //已办箱主键
		sb_sql.append("t1.*,"); //getCommColumns(2)因为待办箱与已办箱中的许多列是一样的,所以做了个可重用的方法
		sb_sql.append("t2.* ");
		sb_sql.append("from v_pub_task_off t1 left join " + _queryTableName + " t2 on t2." + _pkName + "=t1.task_pkvalue "); //从查询表取

		//已办任务相同任务合并 【杨科/2013-03-08】
		if (tbUtil.getSysOptionBooleanValue("已办任务同一任务是否只显示最后一条", false)) {
			sb_sql.append(" inner join ( select task_pkvalue, max(task_dealtime) task_dealtime from v_pub_task_off where 1=1 ");
			if (!_isSuperShow) {
				sb_sql.append(" and (task_realdealuser='" + _loginUserId + "' or task_accruserid='" + _loginUserId + "') "); //实际处理人必须是本人处理的,后来汪云霞提出授权人也可以查看!
			}
			sb_sql.append(" group by task_pkvalue ) t3 on t1.task_pkvalue=t3.task_pkvalue and t1.task_dealtime=t3.task_dealtime ");
		}

		sb_sql.append("where 1=1 "); //
		sb_sql.append("and t1.task_templetcode='" + _templetCode + "' "); //必须是对应的模板,因为可以有两个功能点,存的是一张表,但模板不一样！
		sb_sql.append("and t1.task_tabname='" + _savedTableName + "' "); //表名必须是保存表
		if (!_isSuperShow) {
			sb_sql.append("and (t1.task_realdealuser='" + _loginUserId + "' or t1.task_accruserid='" + _loginUserId + "') "); //实际处理人必须是本人处理的,后来汪云霞提出授权人也可以查看!
		}
		if (_appendCondition != null) {
			sb_sql.append(_appendCondition); //
		}
		sb_sql.append(" order by t1.task_dealtime desc"); //
		return sb_sql.toString(); //
	}

	public String getBDTaskSQL(String _templetCode, String _queryTableName, String _savedTableName, String _pkName, String dbinsql, String _appendCondition, boolean _isSuperShow) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		sb_sql.append("t1.*,"); //getCommColumns(1)因为待办箱与已办箱中的许多列是一样的,所以做了个可重用的方法
		sb_sql.append("t2.* ");
		sb_sql.append("from v_pub_task_deal t1 left join " + _queryTableName + " t2 on t2." + _pkName + "=t1.task_pkvalue "); //从查询表取
		sb_sql.append("where 1=1 ");
		sb_sql.append("and t1.task_templetcode='" + _templetCode + "' "); //必须是对应的模板,因为可以有两个功能点,存的是一张表,但模板不一样！
		sb_sql.append("and t1.task_tabname='" + _savedTableName + "' "); //表名必须是保存表!
		if (!_isSuperShow) {
			sb_sql.append("and (t1.task_dealuser in (" + dbinsql + ") ) "); //实际处理人必须是本人处理的,后来汪云霞提出授权人也可以查看!// or t1.task_accruserid in (" + dbinsql + ")
		}
		if (_appendCondition != null) {
			sb_sql.append(_appendCondition); //
		}
		sb_sql.append(" order by t1.task_createtime desc"); //
		return sb_sql.toString(); //
	}

	/**
	 * 取得流程分配中定义的html与word报表名称,孙富君后来又做了添加! 
	 */
	public VectorMap getAllReport(String str_billtype, String str_busitype, String tablename) {
		String str_sql = "select id,processid,htmlreport,wordreport from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"; //
		VectorMap reportl = new VectorMap();
		String[] allRoles = ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes();
		try {
			HashVO[] hvo_report = UIUtil.getHashVoArrayByDS(null, str_sql);//理论上有且只有一条记录
			if (hvo_report != null && hvo_report.length > 0) {
				String htmlrdesc = hvo_report[0].getStringValue("htmlreport"); //html方式
				String wordrdesc = hvo_report[0].getStringValue("wordreport"); //word方式
				if (htmlrdesc != null && !"".equals(htmlrdesc.trim())) {
					if (htmlrdesc.indexOf("=>") >= 0) {//兼容
						HashMap param = new HashMap();
						param.put("报表名称", "全部审批意见");
						param.put("html文件名", htmlrdesc);
						param.put("showStyle", "2");
						param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
						HashMap tmpMap = getTBUtil().convertStrToMapByExpress(htmlrdesc, ";", "=>", true); //
						String str_savetableName = tablename; //保存的表名!
						if (tmpMap.containsKey(str_savetableName.toLowerCase())) { //如果找到
							param.put("html文件名", (String) tmpMap.get(str_savetableName.toLowerCase()));
						} else if (tmpMap.containsKey("*")) {
							param.put("html文件名", (String) tmpMap.get("*"));
						} else {
							param.put("html文件名", "");
						}
						reportl.put("全部审批意见", param);
					} else if (htmlrdesc.indexOf("=>") < 0 && htmlrdesc.indexOf("=") >= 0) {//新逻辑
						String[] reports = getTBUtil().split(htmlrdesc, ";");
						if (reports != null && reports.length > 0) {
							for (int i = 0; i < reports.length; i++) {
								HashMap key_value = getTBUtil().convertStrToMapByExpress(reports[i], ",", "=");
								if (!key_value.containsKey("表名") || key_value.get("表名").toString().toUpperCase().equals(tablename.toUpperCase())) {
									if (key_value.get("允许角色") != null) {
										//权限定义
										String rrights = key_value.get("允许角色").toString();
										HashMap norightmap = new HashMap();
										if (!"".equals(rrights.trim())) {
											String[] keys = getTBUtil().split(rrights.trim(), "&");
											if (keys != null && keys.length > 0) {
												if (getTBUtil().getInterSectionFromTwoArray(keys, allRoles).length <= 0) {
													key_value.put("是否允许", "N");
												}
											}
										}
									}
									reportl.put(key_value.get("报表名称"), key_value);
								}
							}
						}
					} else { //
						String[] reports = getTBUtil().split(htmlrdesc, ";");
						HashMap param = new HashMap();
						param.put("报表名称", "全部审批意见");
						param.put("html文件名", reports[0]);
						param.put("showStyle", "2");
						param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
						reportl.put("全部审批意见", param);
					}
				} else if (wordrdesc != null && !"".equals(wordrdesc.trim())) {//word的逻辑不变
					HashMap param = new HashMap();
					param.put("报表名称", "全部审批意见");
					param.put("word文件名", wordrdesc);
					param.put("showStyle", "2");
					param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
					reportl.put("全部审批意见", param);
				} else {
					HashMap param = new HashMap();
					param.put("报表名称", "全部审批意见");
					param.put("showStyle", "2");
					param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
					reportl.put("全部审批意见", param);
				}
			}
			if (ClientEnvironment.getCurrLoginUserVO().getCode().equals("admin")) {
				HashMap param = new HashMap();
				param.put("showType", "3");
				param.put("userid", ClientEnvironment.getCurrLoginUserVO().getId());
				if (reportl.containsKey("全部审批意见")) {
					param.put("html文件名", ((HashMap) reportl.get("全部审批意见")).get("html文件名"));
					param.put("word文件名", ((HashMap) reportl.get("全部审批意见")).get("word文件名"));
				}
				String name = UIUtil.getLanguage("审批意见历史版本");
				param.put("报表名称", name);
				reportl.put(name, param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reportl;
	}

}
