package com.pushworld.ipushgrc.ui.risk.p010;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

import com.pushworld.ipushgrc.ui.wfrisk.CmpFileHistoryViewDialog;
import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFGraphEditItemDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFRiskUIUtil;
import com.pushworld.ipushgrc.ui.wfrisk.p010.WFGraphEditDialog;
import com.pushworld.ipushgrc.ui.wfrisk.p010.WFGraphEditItemPanel;

/**
 * 风险识别与评估! 即还是原来的一图两表编辑界面! 只不过只要风险点的编辑!但还需要同时有不基于流程的风险点评估!
 * 主页面就是流程单列表，过滤条件：流程文件状态为“编辑中”和“有效”的所有流程。也就是说文件状态为“申请发布中”、“申请废止中”、“废止”的流程是不允许风险管理员来评估风险的！
 * A.按钮【直接评估】的逻辑：打开选中流程，
 *      如果该流程所在流程文件的文件状态为“编辑中”则需要点击【开始编辑】按钮将文件的风险状态变为“编辑中”，此时才可以新增修改风险点，
 * 当点击【结束编辑】时将文件的风险状态变为“有效”，这时流程文件管理员才可以发起申请发布或废止流程；
 *      如果文件状态为“有效”，风险管理员要评估风险，同理点击【开始编辑】按钮，
 * 但当点击【结束编辑】按钮时不仅要修改风险状态为“有效”，同时还要生成一个小版本流程文件，如果已存在一个小版本流程文件则要覆盖存在的小版本流程文件；
 *      如果文件状态为“申请发布中”或“申请废止中”则提示 等流程走完才能评估！
 *      如果文件状态为“废止”，则提示 废止的文件不能进行风险评估！
 * 
 * B.按钮【处理申请】的逻辑：直接显示走完流程并且未评估完（iseval=null）的评估申请记录窗口，选择一条记录（只能单选），点击【确定】按钮，可根据参数配置来显示页面（
 * 评估申请记录窗口和流程编辑窗口分开显示或不分开显示），这时也要有【开始编辑】(逻辑同上)和【结束编辑】按钮。当点击【结束编辑】时，更新评估申请表中字段iseval='Y'（下次点击
 * 【处理申请】将不能看到该记录），并且要执行A中的结束逻辑！
 *      因为风险评估申请流程走完后没有处理逻辑，只能在申请中查看领导是否同意修改和修改意见，如果不同意修改是否需要在【开始编辑】按钮后面加一个【不作处理】按钮，
 * 意思是申请不合理，不需要识别风险点，这时【不做处理】的逻辑只是修改 评估申请表中字段iseval='Y'，也没必要再发布小版本！
 * 
 *
 * C.按钮【查看申请历史】的逻辑：如果主页面有选中记录，则打开该文件相关的评估申请，否则打开流程文件选择窗口，选择一条流程文件信息，然后查看相关的评估申请记录。
 * 
 * @author xch
 *
 */
public class RiskEvalWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {

	private BillListPanel billlist_main_process = null;//主界面列表
	private WLTButton btn_directeval, btn_dealapply, btn_showhistapply;//主界面列表上的按钮
	private WLTButton btn_beginedit, btn_risk, btn_endedit, btn_closeitemdialog;

	private WFGraphEditItemDialog itemdialog = null;//直接评估打开的一个流程图的窗口
	private WFGraphEditDialog dealEditDialog = null;//处理申请，打开的某个流程文件的所有流程图的窗口
	private WorkFlowDesignWPanel workFlowPanel = null;//当前显示的流程图面板

	private boolean editable = true;
	private boolean evalable = true;
	private boolean isdirecteval = true;
	private boolean dealSelected = false;//默认不对选中的流程文件进行处理申请，而是弹出可以处理申请的流程文件列表【李春娟/2012-06-01】

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());

		billlist_main_process = new BillListPanel("V_PROCESS_FILE_CODE1");
		//billlist_main_process.setDataFilterCustCondition("CMPFILE_ID is not null");//filestate in('1','3'),文件状态为"编辑中"或"有效"的流程
		billlist_main_process.getQuickQueryPanel().addBillQuickActionListener(this);
		btn_directeval = new WLTButton("直接评估");
		btn_dealapply = new WLTButton("处理申请");
		btn_showhistapply = new WLTButton("查看申请历史");
		btn_directeval.addActionListener(this);
		btn_dealapply.addActionListener(this);
		btn_showhistapply.addActionListener(this);
		billlist_main_process.addBatchBillListButton(new WLTButton[] { btn_directeval, btn_dealapply, btn_showhistapply, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		billlist_main_process.repaintBillListButton();
		billlist_main_process.addBillListHtmlHrefListener(this);
		this.add(billlist_main_process);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == billlist_main_process.getQuickQueryPanel()) {
			onQuery();//主页面上查询按钮的逻辑
		} else if (e.getSource() == btn_directeval) {
			onDirectEval();//主页面上直接评估按钮的逻辑
		} else if (e.getSource() == btn_dealapply) {
			onDealAppley();//主页面上处理申请按钮的逻辑
		} else if (e.getSource() == btn_showhistapply) {
			onShowHistApply();//主页面上查看申请历史按钮的逻辑
		} else if (e.getSource() == btn_risk) {
			onEvaluateProcessRisk();//直接评估或申请处理 ，打开的流程图，上面的风险点按钮
		} else if (e.getSource() == btn_closeitemdialog) {
			onCloseItemDialog();//直接评估，流程图上的关闭按钮
		} else if (e.getSource() == btn_beginedit) {
			onBeginEditRisk();//风险点列表界面的开始编辑按钮逻辑
		} else if (e.getSource() == btn_endedit) {
			onEndEditRisk();//风险点列表界面的结束编辑按钮逻辑
		}
	}

	/**
	 * 重写查询按钮的事件
	 */
	private void onQuery() {
		String str_sql = billlist_main_process.getQuickQueryPanel().getQuerySQL();
		if (str_sql.contains("risk_name like")) {//是否要查询风险名称，如果要查询的话，需要从风险流程表中查出流程，select * from V_PROCESS_FILE where 1=1  and (filestate in('1','3'))  and (riskname like '%3%' and riskname like '%规范%')
			str_sql = str_sql.substring(0, str_sql.indexOf("risk_name like") - 1) + " wfprocess_id in(select wfprocess_id from v_risk_process_file where " + str_sql.substring(str_sql.indexOf("risk_name like") - 1, str_sql.length()) + ")";
		}
		billlist_main_process.QueryData(str_sql);
	}

	/**
	 * 【直接评估】的逻辑，如果选择的流程所属流程文件状态为申请发布中、申请废止中或废止则只可查看，编辑中或有效则可以对风险点进行识别评估
	 */
	private void onDirectEval() {
		BillVO billvo = billlist_main_process.getSelectedBillVO();
		if (billvo == null) {//如果没有选中记录，则需要选择一个流程进行评估，否则直接对选中流程进行评估
			MessageBox.showSelectOne(this);
			return;
		}
		billlist_main_process.refreshCurrSelectedRow();
		billvo = billlist_main_process.getSelectedBillVO();
		String filestate = billvo.getStringValue("filestate");
		if ("2".equals(filestate) || "4".equals(filestate) || "5".equals(filestate)) {//申请发布中、申请废止中和废止状态的文件是不允许进行风险评估的
			filestate = billvo.getStringValue("filestatename");//得到文件状态显示名称
			if (MessageBox.showConfirmDialog(this, "该流程文件的状态为[" + filestate + "],不能进行编辑,是否需要查看？") != JOptionPane.YES_OPTION) {//提示是否查看
				return;
			}
			editable = false;//修改编辑状态为不可编辑
		} else {
			editable = true;
		}
		onShowItemDialog(billvo.getStringValue("cmpfile_id"), billvo.getStringValue("wfprocess_id"), billvo.getStringValue("wfprocess_name"));
	}

	/**
	 * 【直接评估】的评估页面，是选择了一条流程信息，打开该流程图，进行风险的增删改操作！
	 * @param _wfid   流程id 
	 * @param _wfname 流程名称
	 */
	private void onShowItemDialog(String _cmpfileid, String _wfid, String _wfname) {
		isdirecteval = true;
		if (this.editable) {
			itemdialog = new WFGraphEditItemDialog(this, "识别风险点[" + _wfname + "]", _wfid, false, false);
		} else {
			itemdialog = new WFGraphEditItemDialog(this, "查看风险点[" + _wfname + "]", _wfid, false, false);
		}
		itemdialog.setMaxWindowMenuBar();
		WLTPanel northbtnPanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new BorderLayout(), LookAndFeel.defaultShadeColor1, false); //
		WLTPanel southbtnPanel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new FlowLayout(FlowLayout.CENTER), LookAndFeel.defaultShadeColor1, false); //

		WFGraphEditItemPanel itempanel = itemdialog.getItempanel();
		workFlowPanel = itempanel.getWorkFlowPanel();
		//btn_closeitemdialog = new WLTButton(" 关闭 ");
		//btn_closeitemdialog.addActionListener(this);
		WLTPanel northEastPanel = new WLTPanel(WLTPanel.HORIZONTAL_RIGHT_TO_LEFT, new FlowLayout(FlowLayout.CENTER), LookAndFeel.defaultShadeColor1, false); //
		if (this.editable) {
			try {
				String riskstate = UIUtil.getStringValueByDS(null, "select riskstate from cmp_cmpfile where id =" + _cmpfileid);
				btn_endedit = new WLTButton("结束编辑");
				btn_endedit.addActionListener(this);
				if ("编辑中".equals(riskstate)) {//风险状态：编辑中；有效。如果是编辑中的话，有识别、修改、删除、结束编辑等按钮，否则需要重新点击【开始编辑】并且隐藏其他按钮。
					this.evalable = true;
					btn_risk = new WLTButton("风险点", "office_016.gif");
					northEastPanel.add(btn_risk);
					northEastPanel.add(btn_endedit);
				} else {
					this.evalable = false;
					btn_risk = new WLTButton("查看风险点", "office_016.gif");
					btn_beginedit = new WLTButton("开始编辑");
					btn_beginedit.addActionListener(this);
					btn_endedit.setVisible(false);
					northEastPanel.add(btn_beginedit);
					northEastPanel.add(btn_risk);
					northEastPanel.add(btn_endedit);
				}
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		} else {
			this.evalable = false;
			btn_risk = new WLTButton("查看风险点", "office_016.gif");
			northEastPanel.add(btn_risk);
		}
		btn_risk.addActionListener(this);
		northbtnPanel.add(northEastPanel, BorderLayout.EAST);

		JRadioButton radioBtn = new JRadioButton(_wfname); //
		radioBtn.setOpaque(false);
		radioBtn.setSelected(true);
		northbtnPanel.add(radioBtn, BorderLayout.CENTER);

		//southbtnPanel.add(btn_closeitemdialog); //去除重复按钮[关闭], Gwang 2013/4/15 
		itempanel.add(northbtnPanel, BorderLayout.NORTH);
		itempanel.add(southbtnPanel, BorderLayout.SOUTH);
		itemdialog.setVisible(true);
	}

	/**
	 * 【处理申请】的逻辑，因为评估申请是相对于某个流程文件，而非流程，所以这里打开的是流程文件的所有流程图！
	 */
	private void onDealAppley() {
		BillListDialog listdialog = new BillListDialog(this, "请选择一条记录进行处理", "CMP_RISKEVAL_CODE1");
		BillListPanel listpanel_apply = listdialog.getBilllistPanel();
		if (this.dealSelected) {//张越霞认为选择了一个流程进行处理申请有些歧义，因为申请是基于流程文件的，所以这里设置可以配置，是否每次都弹出走完流程的申请
			BillVO billvo = billlist_main_process.getSelectedBillVO();
			if (billvo == null) {//如果没有选中记录，则需要选择一个流程文件进行评估，否则直接对选中流程文件进行评估
				listpanel_apply.QueryData("select eval.* from CMP_RISKEVAL eval,pub_wf_prinstance prinstance where eval.wfprinstanceid=prinstance.id and prinstance.status='END' and (eval.iseval is null or eval.iseval='') order by eval.cmpfile_id,eval.evaldate desc");//查询申请流程已结束并且未处理过的
			} else {
				listpanel_apply.QueryData("select eval.* from CMP_RISKEVAL eval,pub_wf_prinstance prinstance where eval.wfprinstanceid=prinstance.id and prinstance.status='END' and (eval.iseval is null or eval.iseval='') and eval.cmpfile_id=" + billvo.getStringValue("cmpfile_id")
						+ " order by eval.cmpfile_id,eval.evaldate desc");//查询申请流程已结束并且未处理过的
			}
		} else {
			listpanel_apply.QueryData("select eval.* from CMP_RISKEVAL eval,pub_wf_prinstance prinstance where eval.wfprinstanceid=prinstance.id and prinstance.status='END' and (eval.iseval is null or eval.iseval='') order by eval.cmpfile_id,eval.evaldate desc");//查询申请流程已结束并且未处理过的
		}
		if (listpanel_apply.getRowCount() == 0) {
			listdialog.dispose();
			MessageBox.show(this, "没有要处理的申请!");
			return;
		} else if (listpanel_apply.getRowCount() > 1) {//如果需要处理的记录不止一条，则不要选择处理哪一条，如果只有一条，则直接打开
			listpanel_apply.getBillListBtnPanel().setVisible(false);
			listpanel_apply.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
			listpanel_apply.repaintBillListButton();
			listpanel_apply.setQuickQueryPanelVisiable(false);
			listpanel_apply.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选
			listdialog.setVisible(true);
			if (listdialog.getCloseType() != 1) {//如果不是点击确定关闭的则直接退出！
				return;
			}
		} else {
			listpanel_apply.setSelectedRow(0);
			listdialog.onConfirm();
		}

		final String eval_id = listdialog.getReturnBillVOs()[0].getStringValue("id");
		final String cmpfileid = listdialog.getReturnBillVOs()[0].getStringValue("cmpfile_id");
		final String cmpfilename = listdialog.getReturnBillVOs()[0].getStringValue("cmpfile_name");
		String[][] processes = null;// 流程文件的所有流程
		try {
			processes = UIUtil.getStringArrayByDS(null, "select id,code,name from pub_wf_process where cmpfileid =" + cmpfileid + " order by userdef04");
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		if (processes == null || processes.length == 0) {
			MessageBox.show(this, "该申请没有对应的流程，不能进行处理!");
			return;
		}
		BillFrame billframe = new BillFrame("评估申请");
		BillCardPanel cardpanel = new BillCardPanel("CMP_RISKEVAL_CODE1");
		cardpanel.queryDataByCondition("id=" + eval_id);
		cardpanel.setGroupExpandable("评估信息", false);//隐藏评估信息组
		if (new TBUtil().getSysOptionBooleanValue("风险识别评估中处理申请时申请和流程图是否分开显示", true)) {
			billframe.add(cardpanel);
			billframe.setSize(530, 760);//设置评估申请窗口的大小
			billframe.setAlwaysOnTop(true);//设置评估申请窗口一直在最前面，如果不设置的话，点击流程图时评估申请窗口就会跑到后面去
			billframe.setVisible(true);//如果两个窗口显示时，显示评估申请窗口

			isdirecteval = false;
			dealEditDialog = new WFGraphEditDialog(this, "风险编辑", 1000, 700, cmpfileid, cmpfilename, processes, false); //处理申请，弹出某个流程文件的所有流程图的窗口
			workFlowPanel = dealEditDialog.getGraphPanel().getCurrSelectedItemPanel().getWorkFlowPanel();//重新获得当前的流程图设计面板
			dealEditDialog.setShowRefPanel(false);//设置不显示流程相关及环节相关的按钮面板
			dealEditDialog.getGraphPanel().setDividerLocation(200);//设置分隔条的右边按钮的固定宽度
			dealEditDialog.setMaxWindowMenuBar();//设置窗口可最大最小化

			if (this.editable) {
				WLTPanel northEastPanel = new WLTPanel(WLTPanel.HORIZONTAL_RIGHT_TO_LEFT, new FlowLayout(FlowLayout.CENTER), LookAndFeel.defaultShadeColor1, false); //
				try {
					String riskstate = UIUtil.getStringValueByDS(null, "select riskstate from cmp_cmpfile where id =" + cmpfileid);
					btn_endedit = new WLTButton("结束编辑");
					btn_endedit.addActionListener(this);
					if ("编辑中".equals(riskstate)) {//风险状态：编辑中；有效。如果是编辑中的话，有识别、修改、删除等按钮，否则需要重新点击【开始编辑】并且隐藏其他按钮。
						this.evalable = true;//设置风险点可以识别
						btn_risk = new WLTButton("风险点", "office_016.gif");
						northEastPanel.add(btn_risk);
						northEastPanel.add(btn_endedit);
					} else {
						this.evalable = false;//因没有点击开始编辑，流程文件的风险状态(riskstate)为有效，故设置风险点不可识别，只可查看
						btn_risk = new WLTButton("查看风险点", "office_016.gif");
						btn_beginedit = new WLTButton("开始编辑");
						btn_beginedit.addActionListener(this);
						btn_endedit.setVisible(false);
						northEastPanel.add(btn_beginedit);
						northEastPanel.add(btn_risk);
						northEastPanel.add(btn_endedit);
					}
				} catch (Exception e) {
					MessageBox.showException(this, e);
				}
				dealEditDialog.setSize(1024, 760);
				dealEditDialog.setLocation(0, 0);
				dealEditDialog.getGraphPanel().getNorthPanel().add(northEastPanel);
			} else {
				this.evalable = false;//因流程文件状态为"发布申请中"或"废止申请中"或"废止"，故不可编辑，设置风险点不可识别，只可查看
				btn_risk = new WLTButton("查看风险点", "office_016.gif");
				dealEditDialog.getGraphPanel().getNorthPanel().add(btn_risk);
			}
			btn_risk.addActionListener(this);
			dealEditDialog.setVisible(true);
		} else {
			//这里有问题，流程图应该是多个的！！！
			billframe.maxToScreenSizeBy1280AndLocationCenter();
			//onShowItemDialog(cmpfileid, billvo.getStringValue("wfprocess_id"), billvo.getStringValue("wfprocess_name"));

			JScrollPane scrollpanel = new JScrollPane(itemdialog.getItempanel());

			WLTSplitPane split = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, cardpanel, scrollpanel);
			split.setOneTouchExpandable(true);
			split.setDividerSize(10);
			split.setDividerLocation(500);
			billframe.add(split);
			billframe.setVisible(true);
		}
	}

	/**
	 * 【查看申请历史】的逻辑
	 */
	private void onShowHistApply() {
		BillVO billvo = null;//billlist_main_process.getSelectedBillVO(); //因张越霞认为选择了一个流程进行查看有些歧义，因为申请是基于流程文件的，所以这里先注释掉，每次都让选择流程文件进行查看
		if (billvo == null) {//如果没有选中记录，则需要选择一个流程文件进行查看
			BillListDialog listdialog_file = new BillListDialog(this, "请选择流程文件进行查看", TBUtil.getTBUtil().getSysOptionHashItemStringValue("各功能中流程文件的模板", "维护", "CMP_CMPFILE_CODE2"));//只能查看本部门的流程文件的申请记录【李春娟/2012-07-13】
			BillListPanel billlist_file = listdialog_file.getBilllistPanel();
			billlist_file.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));//增加浏览按钮【李春娟/2012-03-28】
			billlist_file.repaintBillListButton();
			billlist_file.setDataFilterCustCondition("id in(select distinct(cmpfile_id) from cmp_riskeval where wfprinstanceid is not null)");//已经有了评估申请流程的文件
			billlist_file.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 单选
			billlist_file.addBillListHtmlHrefListener(this);
			billlist_file.getQuickQueryPanel().resetAllQuickQueryCompent();//重置所有默认查询条件【李春娟、2012-03-28】
			listdialog_file.setVisible(true);
			if (listdialog_file.getCloseType() == 1) {
				BillVO[] billvos = listdialog_file.getReturnBillVOs();
				BillListDialog listdialog_apply = new BillListDialog(this, "流程文件【" + billvos[0].getStringValue("cmpfilename") + "】的申请历史", "CMP_RISKEVAL_CODE1");
				BillListPanel listpanel_apply = listdialog_apply.getBilllistPanel();
				listpanel_apply.QueryDataByCondition("cmpfile_id=" + billvos[0].getStringValue("id"));
				if (listpanel_apply.getRowCount() == 0) {
					listdialog_apply.dispose();
					MessageBox.show(this, "该流程文件没有评估申请!");
					return;
				}

				listpanel_apply.getBillListBtnPanel().setVisible(false);
				listpanel_apply.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
				listpanel_apply.repaintBillListButton();
				listpanel_apply.setItemVisible("cmpfile_name", false);//窗口标题已有文件名称
				listpanel_apply.setQuickQueryPanelVisiable(false);
				listdialog_apply.getBtn_confirm().setVisible(false);
				listdialog_apply.getBtn_cancel().setText("关闭");
				listdialog_apply.setVisible(true);
			}
		} else {
			String cmpfile_id = billvo.getStringValue("cmpfile_id");
			String cmpfilename = billvo.getStringValue("cmpfilename");
			BillListDialog listdialog_apply = new BillListDialog(this, "流程文件【" + cmpfilename + "】的申请历史", "CMP_RISKEVAL_CODE1");
			BillListPanel listpanel_apply = listdialog_apply.getBilllistPanel();
			listpanel_apply.QueryData("select * from CMP_RISKEVAL where cmpfile_id=" + cmpfile_id);//
			if (listpanel_apply.getRowCount() == 0) {
				listdialog_apply.dispose();
				MessageBox.show(this, "该流程文件没有评估申请!");
				return;
			}
			listpanel_apply.getBillListBtnPanel().setVisible(false);
			listpanel_apply.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
			listpanel_apply.repaintBillListButton();
			listpanel_apply.setItemVisible("cmpfile_name", false);//窗口标题已有文件名称
			listpanel_apply.setQuickQueryPanelVisiable(false);
			listdialog_apply.getBtn_confirm().setVisible(false);
			//listdialog_apply.getBtn_cancel().setText("关闭");			
			listdialog_apply.setVisible(true);
		}
	}

	/**
	 * 直接评估 ，打开的流程图，上面的关闭按钮的逻辑
	 */

	private void onCloseItemDialog() {
		if (billlist_main_process.getSelectedBillVO() != null) {
			billlist_main_process.refreshData();
		}
		itemdialog.dispose();
	}

	private void onEvaluateProcessRisk() {
		if (!this.isdirecteval) {//如果是处理申请，因为是基于某个流程文件的，而流程文件包括多个流程，所以workFlowPanel 不确定，需要在这里设置一下！
			this.workFlowPanel = dealEditDialog.getGraphPanel().getCurrSelectedItemPanel().getWorkFlowPanel();
		}
		ActivityVO activityvo = workFlowPanel.getSelectedActivityVO();
		if (activityvo == null) {
			MessageBox.show(itemdialog == null ? dealEditDialog : itemdialog, "请选择一个环节进行此操作");
			return;
		}
		String cmpfile_id = workFlowPanel.getCurrentProcessVO().getCmpfileid();
		String wfprocess_id = workFlowPanel.getCurrentProcessVO().getId();
		String wfprocess_code = workFlowPanel.getCurrentProcessVO().getCode();
		String wfprocess_name = workFlowPanel.getCurrentProcessVO().getName();
		String cmpfile_name = null;
		if (this.isdirecteval) {
			BillVO billvo = billlist_main_process.getSelectedBillVO();
			cmpfile_name = billvo.getStringValue("cmpfilename");
		} else {
			try {
				cmpfile_name = UIUtil.getStringValueByDS(null, "select cmpfilename from cmp_cmpfile where id=" + cmpfile_id);
			} catch (Exception e) {
				MessageBox.showException(itemdialog == null ? dealEditDialog : itemdialog, e);
			}
		}
		String activityid = activityvo.getId() + "";
		//系统可根据平台参数配置来配置一图两表中相关风险点的界面，为了与其关联界面统一，可在风险识别中通过平台参数配置“自定义风险直接评估窗口类”来自定义风险识别界面。
		//注意：配置的类需要继承BillDialog类，并且有规定的构造方法
		//构造方法中的参数分别表示：父亲面板，窗口标题，流程文件id，流程文件名称，流程id，流程编码，流程名称，环节id，环节编码，环节名称，关联类型（比如"流程文件";"流程";"环节" ），是否可编辑（Boolean类）【李春娟/2012-07-16】
		String str_clsName = TBUtil.getTBUtil().getSysOptionStringValue("自定义风险直接评估窗口类", "com.pushworld.ipushgrc.ui.wfrisk.p010.LookRiskDialog");
		try {
			Class dialog_class = Class.forName(str_clsName);
			Class cp[] = { java.awt.Container.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, Boolean.class }; //构造方法中的参数类型！
			Constructor constructor = dialog_class.getConstructor(cp);
			BillDialog dialog = (BillDialog) constructor.newInstance(new Object[] { itemdialog == null ? dealEditDialog : itemdialog, null, cmpfile_id, cmpfile_name, wfprocess_id, wfprocess_code, wfprocess_name, activityid, activityvo.getCode(), activityvo.getWfname(), "环节",
					this.editable && this.evalable }); //
			dialog.setVisible(true);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}

		if (this.isdirecteval) {
			itemdialog.getItempanel().resetRisk(activityid);
		} else {//如果是处理申请，因为是基于某个流程文件的，而流程文件包括多个流程，所以workFlowPanel 不确定，需要在这里设置一下！
			dealEditDialog.getGraphPanel().getCurrSelectedItemPanel().resetRisk(activityid);
		}
	}

	/**
	 * 【开始编辑】按钮的逻辑，将流程文件的风险状态（riskstate）修改为“编辑中”，并且流程文件就不能发起发布流程，必须等风险管理员修改完风险点，点击【结束编辑】按钮后才可以发布
	 */
	private void onBeginEditRisk() {
		//这里需要提醒 流程文件就不能发起发布流程，必须等风险管理员修改完风险点，点击【结束编辑】按钮后才可以发布
		this.evalable = true;
		btn_beginedit.setVisible(false);
		btn_endedit.setVisible(true);
		btn_risk.setText("风险点");
		btn_risk.setToolTipText("风险点");
		String cmpfile_id = workFlowPanel.getCurrentProcessVO().getCmpfileid();
		try {
			UpdateSQLBuilder isql = new UpdateSQLBuilder("cmp_cmpfile", "id=" + cmpfile_id); //
			isql.putFieldValue("riskstate", "编辑中");
			UIUtil.executeUpdateByDS(null, isql.getSQL());
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 【结束编辑】按钮的逻辑，将流程文件的风险状态（riskstate）修改为“有效”，此后流程文件就可以发布或废止
	 */
	private void onEndEditRisk() {

		String cmpfile_id = null;
		String filestate = null;
		String cmpfilename = null;
		try {
			if (isdirecteval) {//直接评估
				if (MessageBox.showConfirmDialog(itemdialog, "你确定要结束编辑吗?") != JOptionPane.YES_OPTION) {
					return;
				}
				billlist_main_process.refreshCurrSelectedRow();//刷新一下，后面还需要获得文件状态呢
				BillVO billvo = billlist_main_process.getSelectedBillVO();
				cmpfile_id = billvo.getStringValue("cmpfile_id");
				filestate = billvo.getStringValue("filestate");
				cmpfilename = billvo.getStringValue("cmpfilename");
			} else {
				if (MessageBox.showConfirmDialog(dealEditDialog, "该申请是否处理完成,如果选择【是】，将不能再处理?") != JOptionPane.YES_OPTION) {
					return;
				}
				ProcessVO processvo = dealEditDialog.getGraphPanel().getCurrSelectedItemPanel().getWorkFlowPanel().getCurrentProcessVO();
				cmpfile_id = processvo.getCmpfileid();
				String[][] cmpfile = UIUtil.getStringArrayByDS(null, "select filestate,cmpfilename from cmp_cmpfile where id =" + cmpfile_id);
				if (cmpfile == null || cmpfile.length == 0) {
					return;
				}
				filestate = cmpfile[0][0];
				cmpfilename = cmpfile[0][1];
				UIUtil.executeUpdateByDS(null, "update cmp_riskeval set iseval='Y' where cmpfile_id=" + cmpfile_id);
			}

			if ("3".equals(filestate)) {//如果是有效的流程文件，则需要重新发布一个小版本
				String maxversionno = UIUtil.getStringValueByDS(null, "select versionno from cmp_cmpfile where id=" + cmpfile_id);
				String newversionno = "1.01";
				if (maxversionno == null) {//如果没有版本号，除非没有发布，手动修改了流程文件的状态为"有效"
					return;
				} else if (maxversionno.contains(".")) {
					int length = maxversionno.substring(maxversionno.indexOf("."), maxversionno.length()).length();
					if (length == 1) {
						newversionno = maxversionno + "01";
					} else if (length == 2) {
						newversionno = maxversionno + "1";
					} else if (length == 3) {
						newversionno = maxversionno;
					}
				} else {
					newversionno = maxversionno + ".01";
				}

				final String tmp_cmpfile_id = cmpfile_id;
				final String tmp_cmpfilename = cmpfilename;
				final String tmp_newversionno = newversionno;
				final boolean showreffile = new TBUtil().getSysOptionBooleanValue("流程文件是否由正文生成word", true);
				new SplashWindow(itemdialog == null ? dealEditDialog : itemdialog, "该流程文件的文件状态为[有效],正在发布小版本,请等待...", new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						new WFRiskUIUtil().dealpublish(itemdialog == null ? dealEditDialog : itemdialog, tmp_cmpfile_id, tmp_cmpfilename, showreffile, tmp_newversionno, true);
					}
				});

			}
			UpdateSQLBuilder isql = new UpdateSQLBuilder("cmp_cmpfile", "id=" + cmpfile_id); //
			isql.putFieldValue("riskstate", "有效");
			UIUtil.executeUpdateByDS(null, isql.getSQL());//将流程文件的风险状态变为“有效”，此后流程文件才可以发布或废止
		} catch (Exception e) {
			MessageBox.showException(itemdialog == null ? dealEditDialog : itemdialog, e);
		}
		if (isdirecteval) {
			if (billlist_main_process.getSelectedBillVO() != null) {
				billlist_main_process.refreshData();
			}
			itemdialog.dispose();
		} else {
			dealEditDialog.dispose();
		}
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getBillListPanel() == billlist_main_process) {//主页面的链接
			BillVO billvo = billlist_main_process.getSelectedBillVO();
			if ("wfprocess_name".equalsIgnoreCase(_event.getItemkey())) {
				WFGraphEditItemDialog itemdialog = new WFGraphEditItemDialog(this, "查看流程[" + billvo.getStringValue("wfprocess_name") + "]", billvo.getStringValue("wfprocess_id"), false, true);
				itemdialog.setVisible(true);
			} else {
				CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "查看文件和流程", billvo.getStringValue("cmpfile_id"));
				dialog.setShowprocessid(billvo.getStringValue("wfprocess_id"));
				dialog.setVisible(true);
			}
		} else {//直接评估，查看文件和历史版本的链接
			if ("cmpfilename".equalsIgnoreCase(_event.getItemkey())) {//文件名称-链接，查看流程文件及其所有流程
				CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "查看文件和流程", _event.getBillListPanel().getSelectedBillVO().getStringValue("id"));
				dialog.setVisible(true);
			} else {// 历史版本-链接的逻辑
				BillVO billvo = _event.getBillListPanel().getSelectedBillVO();//流程文件记录
				CmpFileHistoryViewDialog dialog = new CmpFileHistoryViewDialog(this, "文件[" + billvo.getStringValue("cmpfilename") + "]的历史版本", billvo.getStringValue("id"), false); //
				dialog.setVisible(true); //
			}
		}
	}

	public WLTButton getBtn_directeval() {
		return btn_directeval;
	}

	public void setBtn_directeval(WLTButton btn_directeval) {
		this.btn_directeval = btn_directeval;
	}

	public WLTButton getBtn_dealapply() {
		return btn_dealapply;
	}

	public void setBtn_dealapply(WLTButton btn_dealapply) {
		this.btn_dealapply = btn_dealapply;
	}

	public WLTButton getBtn_showhistapply() {
		return btn_showhistapply;
	}

	public void setBtn_showhistapply(WLTButton btn_showhistapply) {
		this.btn_showhistapply = btn_showhistapply;
	}

	public BillListPanel getBilllist_main_process() {
		return billlist_main_process;
	}

	public boolean isDealSelected() {
		return dealSelected;
	}

	public void setDealSelected(boolean dealSelected) {
		this.dealSelected = dealSelected;
	}
}
