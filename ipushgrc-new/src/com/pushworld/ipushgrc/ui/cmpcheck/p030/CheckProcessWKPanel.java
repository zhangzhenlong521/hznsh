package com.pushworld.ipushgrc.ui.cmpcheck.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.keywordreplace.TemplateToWordUIUtil;

/**
 * 检查过程实施! 即录入检查底稿,违规事件,成功防范,问题 
 * 
 * @author Gwang
 * 
 */
public class CheckProcessWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillCardPanel billCard_check;
	private BillListPanel billList_check_item = null; // 检查活动!

	private WLTButton btn_sportList, btn_manuscript, btn_addevent, btn_addward, btn_addissue;
	private WLTButton btn_exportWord;

	private String loginUserID = ClientEnvironment.getCurrSessionVO().getLoginUserId();// 登陆人员ID

	private WLTButton impIssue = null;

	private BillCardPanel cardPanel = null;

	@Override
	public void initialize() {
		// 检查活动主表
		billCard_check = new BillCardPanel("CMP_CHECK_CODE2");
		billCard_check.setVisiable("items", false);
		btn_sportList = new WLTButton("选择活动");
		btn_sportList.addActionListener(this);
		billCard_check.addBatchBillCardButton(new WLTButton[] { btn_sportList });
		billCard_check.repaintBillCardButton();

		// 检查活动子表
		billList_check_item = new CheckItemListPanel("CMP_CHECK_ITEM_CODE2", 1);
		btn_manuscript = new WLTButton("检查基本情况");
		btn_manuscript.addActionListener(this);
		btn_addissue = new WLTButton("问题录入");
		btn_addissue.addActionListener(this);
		btn_addevent = new WLTButton("违规事件录入");
		btn_addevent.addActionListener(this);
		btn_addward = new WLTButton("成功防范录入");
		btn_addward.addActionListener(this);
		btn_exportWord = new WLTButton("导出word");
		btn_exportWord.addActionListener(this);
		billList_check_item.addBatchBillListButton(new WLTButton[] { btn_manuscript, btn_addissue, btn_addevent, btn_addward, btn_exportWord }); //
		billList_check_item.repaintBillListButton(); // 重绘按钮!
		// 界面初始化时子表按钮全不可用， 必须先选择一条主表记录
		btn_manuscript.setEnabled(false);
		btn_addissue.setEnabled(false);
		btn_addevent.setEnabled(false);
		btn_addward.setEnabled(false);
		btn_exportWord.setEnabled(false);
		// 上下分割
		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billCard_check, billList_check_item); //
		split.setDividerLocation(310); // 上下界面大小
		this.add(split);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_sportList) { // 主表-选择活动
			onSportList();
		} else if (obj == btn_manuscript) { // 底稿录入
			onManuscript();
		} else if (obj == btn_addevent) { // 违规事件
			onAddEvent();
		} else if (obj == btn_addward) { // 成功防范
			onAddWard();
		} else if (obj == btn_addissue) { // 问题
			onAddIssue();
		} else if (obj == btn_exportWord) {
			exportWord();
		} else if (obj == impIssue) { //从问题中识别违规事件
			onImpIssue();
		}
	}

	/*
	 * 新建违规事件时  打开问题导入并且做关联
	 */
	private void onImpIssue() {

		BillListDialog dialog = new BillListDialog(cardPanel, "请选择发现的问题！", "CMP_ISSUE_CODE1");
		BillListPanel issuepanel = dialog.getBilllistPanel();
		issuepanel.QueryDataByCondition(" cmp_check_item_id = " + billList_check_item.getSelectedBillVO().getStringValue("id"));
		dialog.setVisible(true);
		if (dialog.getCloseType() != 1) {
			return;
		}
		BillVO selectvo = dialog.getReturnBillVOs()[0];
		cardPanel.setValueAt("eventdescr", new StringItemVO(selectvo.getStringValue("issuedescr")));
		cardPanel.setValueAt("findchannel", selectvo.getComBoxItemVOValue("findchannel"));
		cardPanel.setValueAt("finddate", selectvo.getRefItemVOValue("finddate"));
		cardPanel.setValueAt("issueid", new StringItemVO(selectvo.getStringValue("id")));
		cardPanel.setValueAt("eventcorpid", selectvo.getRefItemVOValue("eventcorpid"));

	}

	public void exportWord() {
		BillVO[] checkItemVOs = billList_check_item.getSelectedBillVOs();
		if (checkItemVOs == null || checkItemVOs.length == 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		int fileNum = checkItemVOs.length;
		String template_name = null;
		TemplateToWordUIUtil tem_word_util = new TemplateToWordUIUtil();
		try {
			template_name = TemplateToWordUIUtil.getTemplateName(this, billList_check_item.getTempletVO().getTempletcode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (template_name == null) {
			return;
		}
		HashMap[] maps = new HashMap[fileNum];
		String[] fileNames = new String[fileNum];
		String checkname = billCard_check.getBillVO().getStringValue("checkname");
		TBUtil tbutil = new TBUtil();
		for (int i = 0; i < maps.length; i++) {
			maps[i] = TemplateToWordUIUtil.convertBillVOToMap(checkItemVOs[i]);
			StringBuffer issue_sb = new StringBuffer();
			try {
				String[] issuedesr = UIUtil.getStringArrayFirstColByDS(null, "select issuedescr from cmp_issue where cmp_check_item_id = " + checkItemVOs[i].getStringValue("id"));
				for (int j = 0; j < issuedesr.length; j++) {
					if (j == (issuedesr.length - 1)) {
						issue_sb.append((j + 1) + "、" + issuedesr[j]);
					} else {
						issue_sb.append((j + 1) + "、" + issuedesr[j] + "</w:t></w:r></w:p><w:p wsp:rsidR=\"00DD0DF9\" wsp:rsidRDefault=\"00760576\"><w:r><w:rPr><w:rFonts w:hint=\"fareast\"/><wx:font wx:val=\"宋体\"/></w:rPr><w:t>");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			maps[i].put("issue", issue_sb.toString());
			fileNames[i] = checkname + "-" + checkItemVOs[i].getStringValue("checkitem_descr") + "_" + (i + 1);//文件名称,需要处理一下，因为文件名中不能包括一下特殊符号：\/:*?"<>|
			fileNames[i] = tbutil.replaceAll(fileNames[i], "\\", "＼");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "/", "／");
			fileNames[i] = tbutil.replaceAll(fileNames[i], ":", "：");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "*", "×");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "?", "？");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "\"", "“");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "<", "＜");
			fileNames[i] = tbutil.replaceAll(fileNames[i], ">", "＞");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "|", "│");//注意这两个竖线是不一样的哦
			fileNames[i] = tbutil.replaceAll(fileNames[i], " ", "");
		}
		try {
			tem_word_util.createWordByMap(maps, fileNames, template_name, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 主表-选择活动
	 */
	private void onSportList() {
		BillListDialog listDialog = new BillListDialog(this, "请选择一条检查活动", "CMP_CHECK_CODE2");
		// 过滤条件： 未完成 and 小组内成员或组长
		BillListPanel listpanel = listDialog.getBilllistPanel();
		listpanel.queryDataByCondition(" status < 3 and (teamleader = " + loginUserID + " or teamusers like '%;" + loginUserID + ";%')", "");
		listpanel.setDataFilterCustCondition(" status < 3 and (teamleader = " + loginUserID + " or teamusers like '%;" + loginUserID + ";%')");
		listpanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		listpanel.repaintBillListButton();
		listDialog.setVisible(true);
		if (listDialog.getCloseType() != 1) {
			return;
		}

		BillVO sportVo = listDialog.getReturnBillVOs()[0];

		if (sportVo.getStringValue("status").equals("1")) {
			int ret = MessageBox.showOptionDialog(this, "是否确定开始实施本检查?", "提示", new String[] { "确认", "取消" });
			if (ret == -1) {
				return;
			} else if (ret == 0) {
				updateStatus("2", sportVo.getStringValue("id"));
			} else {
				return;
			}
		}

		// 根据主表记录刷新子表
		billCard_check.setBillVO(sportVo);
		billList_check_item.removeAllRows();
		billList_check_item.QueryDataByCondition(" cmp_check_id = " + sportVo.getStringValue("id"));

		// 设置子表按钮可用
		btn_manuscript.setEnabled(true);
		btn_addissue.setEnabled(true);
		btn_addevent.setEnabled(true);
		btn_addward.setEnabled(true);
		btn_exportWord.setEnabled(true);
		billList_check_item.putClientProperty("checkedcorp", sportVo.getRefItemVOValue("checkedcorp"));
	}

	/*
	 * 底稿录入 实际就是对原子表记录进行修改
	 */
	private void onManuscript() {
		BillVO checkItemVO = billList_check_item.getSelectedBillVO();
		if (checkItemVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_CHECK_ITEM_CODE2");
		cardPanel.setBillVO(checkItemVO);
		BillCardDialog cardDialog = new BillCardDialog(this, "检查底稿录入", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		cardDialog.setVisible(true);

		if (cardDialog.getCloseType() == 1) {
			billList_check_item.refreshCurrSelectedRow();
		}
	}

	/*
	 * 违规事件录入 向合规事件表中增加数据， 并和检查活动做关联
	 */
	private void onAddEvent() {
		BillVO checkItemVO = billList_check_item.getSelectedBillVO();
		if (checkItemVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		addEvent(checkItemVO);
	}

	/*
	 * 新增违规事件
	 */
	private void addEvent(BillVO checkItemVO) {
		// 弹出新增窗口， 保存后刷新子表中对应数量
		if (showAddWin(checkItemVO, "新增-违规事件", "CMP_EVENT_CODE1")) {
			billList_check_item.refreshCurrSelectedRow();
		}
	}

	/*
	 * 成功防范录入 向合规成功防范表中增加数据， 并和检查活动做关联
	 */
	private void onAddWard() {
		BillVO checkItemVO = billList_check_item.getSelectedBillVO();
		if (checkItemVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		addWard(checkItemVO);
	}

	/*
	 * 新增成功防范
	 */
	private void addWard(BillVO checkItemVO) {
		// 弹出新增窗口， 保存后刷新子表中对应数量
		if (showAddWin(checkItemVO, "新增-成功防范", "CMP_WARD_CODE1")) {
			billList_check_item.refreshCurrSelectedRow();
		}
	}

	/*
	 * 发现问题录入 向合规问题表中增加数据， 并和检查活动做关联
	 */
	private void onAddIssue() {
		BillVO checkItemVO = billList_check_item.getSelectedBillVO();
		if (checkItemVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		addIssue(checkItemVO);
	}

	/*
	 * 新增检查发现问题
	 */
	private void addIssue(BillVO checkItemVO) {
		// 弹出新增窗口， 保存后刷新子表中对应数量
		if (showAddWin(checkItemVO, "新增-检查问题", "CMP_ISSUE_CODE1")) {
			billList_check_item.refreshCurrSelectedRow();
		}
	}

	/***************************************************************************
	 * 弹出新增对话框
	 * 
	 * @param pk
	 * @param title
	 * @param templetCode
	 * @return true-的确新增了
	 */
	private boolean showAddWin(BillVO checkItemVO, String title, String templetCode) {
		cardPanel = new BillCardPanel(templetCode);
		cardPanel.putClientProperty("checkedcorp", billCard_check.getBillVO().getRefItemVOValue("checkedcorp"));
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		if (cardPanel.getTempletVO().getSavedtablename().equalsIgnoreCase("cmp_event")) {
			impIssue = new WLTButton("建议从问题中识别");
			impIssue.setFont(LookAndFeel.font_b);
			impIssue.addActionListener(this);
			cardPanel.addBatchBillCardButton(new WLTButton[] { impIssue });
			cardPanel.repaintBillCardButton();
			cardPanel.setValueAt("sourcetype", new StringItemVO("检查发现"));
		} else if (cardPanel.getTempletVO().getSavedtablename().equalsIgnoreCase("cmp_ward")) {
			cardPanel.setValueAt("sourcetype", new StringItemVO("检查发现"));
		}
		BillCardDialog cardDialog = new BillCardDialog(this, title, cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardPanel.setValueAt("cmp_check_id", new StringItemVO(billCard_check.getBillVO().getStringValue("id"))); // 检查活动id
		cardPanel.setValueAt("cmp_check_name", new StringItemVO(billCard_check.getBillVO().getStringValue("checkname")));// 对应的检查活动名称
		cardPanel.setValueAt("cmp_check_item_id", new StringItemVO(checkItemVO.getStringValue("id")));// 对应的检查项的id

		String cmpfileID = checkItemVO.getStringValue("cmp_cmpfile_id");
		String cmpfileName = checkItemVO.getStringValue("cmp_cmpfile_name");
		cardPanel.setValueAt("cmp_cmpfile_id", new RefItemVO(cmpfileID, cmpfileID, cmpfileName)); // 对应的检查项的流程文件
		cardPanel.setValueAt("cmp_cmpfile_name", new StringItemVO(cmpfileName)); // 对应的检查项的流程文件		
		if (!TBUtil.isEmpty(cmpfileID)) {
			cardPanel.setEditable("cmp_cmpfile_id", false);
		}
		//cardPanel.setEditable("refrisks", false);
		cardDialog.setVisible(true);

		// 刷新子表中对应数量
		if (cardDialog.getCloseType() == 1) {
			//将billList_check_item传参过去,来用于“风险评估申请”对话框的父窗口，以避免某些问题，详见GeneralInsertIntoRiskEval类[YangQing/2013-09-18]
			new GeneralInsertIntoRiskEval(billList_check_item, cardPanel, cardPanel.getTempletVO().getTablename(), "录入");
			return true;
		} else {//如果点击没有保存直接取消，先判断下是否有添加了 涉及的客户 和 设计的员工。如果有则删除的。
			BillVO vo = cardPanel.getBillVO();
			TBUtil tbutil = new TBUtil();
			List deleteSQL = new ArrayList();
			if (vo.getStringValue("wardcust") != null && !vo.getStringValue("wardcust").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("wardcust"));
				deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // 删除涉及的客户子表数据
			}
			if (vo.getStringValue("warduser") != null && !vo.getStringValue("warduser").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("warduser"));
				deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // 删除涉及的员工子表数据
			}
			if (vo.getStringValue("eventcust") != null && !vo.getStringValue("eventcust").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("eventcust"));
				deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // 删除涉及的客户子表数据
			}
			if (vo.getStringValue("eventuser") != null && !vo.getStringValue("eventuser").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("eventuser"));
				deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // 删除涉及的员工子表数据
			}
			if (deleteSQL.size() > 0) {
				try {
					UIUtil.executeBatchByDS(null, deleteSQL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return false;
		}
	}

	private void updateStatus(String status, String pk) {
		String sql = "update cmp_check set status = '" + status + "' where id = " + pk;
		try {
			UIUtil.executeUpdateByDS(null, sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
