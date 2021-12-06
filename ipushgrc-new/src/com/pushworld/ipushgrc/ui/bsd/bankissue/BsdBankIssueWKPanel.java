package com.pushworld.ipushgrc.ui.bsd.bankissue;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;

public class BsdBankIssueWKPanel extends AbstractWorkPanel implements BillTreeSelectListener, BillListSelectListener, ActionListener, BillListHtmlHrefListener {
	/**
	 * 银行业问题维护，徽商提出！
	 */
	private static final long serialVersionUID = 1L;
	private BillTreePanel billTree;
	private BillTreePanel billTree_item;
	private BillCardPanel billCard_item;
	private BillListDialog billListDialog_selectLaw;
	private BillListDialog billList_Detail;
	private BillDialog dialog2;
	private HashMap selectMappingItem;
	private BillListPanel billlist;
	private BillListPanel billList1;
	private BillListPanel billList2;
	private WLTButton btn_mapping1;
	private WLTButton btn_mapping2;
	private WLTButton btn_join;
	private WLTButton btn_confirm;
	private WLTButton btn_cancel;
	private WLTButton btn_viewDetail;
	private WLTButton btn_remove;
	private WLTButton btn_delete1;
	private WLTButton btn_delete2;
	private TBUtil tbUtil;
	private String type;

	public BsdBankIssueWKPanel() {
		billTree = null;
		billlist = null;
		billList1 = null;
		billList2 = null;
		tbUtil = new TBUtil();
		type = null;
	}

	public void initialize() {
		Pub_Templet_1VO[] templetvos = null;
		try {
			templetvos = UIUtil.getPub_Templet_1VOs(new String[] { "BSD_ISSUE_CODE1", "BAD_ISSUE_LAW_MAPPING_CODE1" });// 一次远程调用获得所有模板vo【李春娟/2012-04-18】
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return;
		}
		billTree = new BillTreePanel(templetvos[0]);
		billlist = new BillListPanel(templetvos[0]);
		billTree.setMoveUpDownBtnVisiable(false);
		billTree.queryDataByCondition(null);
		billTree.addBillTreeSelectListener(this);
		billList1 = new BillListPanel(templetvos[1]);
		billList1.addBillListSelectListener(this);
		btn_mapping1 = new WLTButton("映射");
		btn_mapping1.addActionListener(this);
		btn_mapping2 = new WLTButton("映射");
		btn_mapping2.addActionListener(this);
		btn_delete1 = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		billList2 = new BillListPanel(templetvos[1]);
		billList2.setTitleLabelText("相关监管措施和处理依据");// 设置模板标题，否则两个列表很混淆【李春娟/2012-04-18】
		billList2.addBillListSelectListener(this);
		btn_delete2 = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		billList1.addBatchBillListButton(new WLTButton[] { btn_mapping1, btn_delete1 });
		billList1.repaintBillListButton();
		billList2.addBatchBillListButton(new WLTButton[] { btn_mapping2, btn_delete2 });
		billList2.repaintBillListButton();
		WLTSplitPane split1 = new WLTSplitPane(0, billList1, billList2);
		split1.setDividerLocation(180);
		WLTSplitPane split2 = new WLTSplitPane(0, billlist, split1);
		split2.setDividerLocation(160);
		WLTSplitPane split3 = new WLTSplitPane(1, billTree, split2);
		split3.setDividerLocation(180);
		billTree.addBillTreeSelectListener(this);
		add(split3);
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (_event.getSource() == billTree) {
			billlist.clearTable();
			billList1.clearTable();
			billList2.clearTable();
			BillVO billVO = _event.getCurrSelectedVO();
			if (billVO == null)
				return;
			String str_id = billVO.getStringValue("id");
			String str_linkcode = billVO.getStringValue("linkcode");
			if (str_linkcode.length() == 8) {
				billlist.QueryData((new StringBuilder("select * from bsd_issue where  parentid ='")).append(tbUtil.getNullCondition(str_id)).append("'").toString());
				billList1.queryDataByDS(null, (new StringBuilder("select * from BAD_ISSUE_LAW_MAPPING where mappingtype = '相关定性依据' and issueid = ")).append(tbUtil.getNullCondition(str_id)).toString());
				billList2.queryDataByDS(null, (new StringBuilder("select * from BAD_ISSUE_LAW_MAPPING where mappingtype = '相关监管措施和处理依据' and issueid = ")).append(tbUtil.getNullCondition(str_id)).toString());
			}
		} else if (_event.getSource() == billTree_item) {
			if (billTree_item.getSelectedNode() == null)
				return;
			if (billTree_item.getSelectedNode().isRoot()) {
				billCard_item.clear();
				return;
			}
			billCard_item.setBillVO(billTree_item.getSelectedVO());
		}
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_mapping1) {
			type = "相关定性依据";
			onMapping();
			billList1.refreshData();
		} else if (obj == btn_mapping2) {
			type = "相关监管措施和处理依据";
			onMapping();
			billList2.refreshData();
		} else if (obj == btn_join)
			onJoin();
		else if (obj == btn_confirm)
			onConfirm();
		else if (obj == btn_viewDetail)
			onViewDetail();
		else if (obj == btn_remove)
			onRemove();
		else if (obj == btn_cancel)
			onCancel();
	}

	public void onJoin() {
		BillVO itemVO = billTree_item.getSelectedVO();
		if (itemVO == null) {
			MessageBox.showSelectOne(dialog2);
			return;
		}
		if (selectMappingItem.containsKey(itemVO.getStringValue("id"))) {
			MessageBox.show(dialog2, "此记录已存在！");
			return;
		} else {
			BillVO billVo = new BillVO();
			billVo.setTempletCode("BAD_ISSUE_LAW_MAPPING_CODE1");
			billVo.setKeys(billList2.getTempletVO().getItemKeys());
			Object obj[] = new Object[8];
			billVo.setDatas(obj);
			billVo.setObject("issueid", new StringItemVO(billTree.getSelectedVO().getStringValue("id")));
			billVo.setObject("lawid", new StringItemVO(billListDialog_selectLaw.getReturnBillVOs()[0].getStringValue("id")));
			billVo.setObject("lawname", new StringItemVO(billListDialog_selectLaw.getReturnBillVOs()[0].getStringValue("lawname")));
			billVo.setObject("lawitemid", new StringItemVO(itemVO.getStringValue("id")));
			billVo.setObject("lawitemtitle", new StringItemVO(itemVO.getStringValue("itemtitle")));
			billVo.setObject("lawitemcontent", new StringItemVO(itemVO.getStringValue("itemcontent")));
			selectMappingItem.put(itemVO.getStringValue("id"), billVo);
			setButtonContent();
			MessageBox.show(dialog2, "映射成功，浏览或移除映射请点击[查看详细]按钮!");
			return;
		}
	}

	public void onConfirm() {
		Iterator it = selectMappingItem.entrySet().iterator();
		java.util.List insertSql = new ArrayList();
		while (it.hasNext()) {
			java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
			BillVO billvo = (BillVO) entry.getValue();
			if (billvo.getStringValue("id") == null) {
				InsertSQLBuilder sqlBuilder = new InsertSQLBuilder("BAD_ISSUE_LAW_MAPPING");
				try {
					sqlBuilder.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_BAD_ISSUE_LAW_MAPPING"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				sqlBuilder.putFieldValue("issueid", billTree.getSelectedVO().getStringValue("id"));
				sqlBuilder.putFieldValue("lawid", billListDialog_selectLaw.getReturnBillVOs()[0].getStringValue("id"));
				sqlBuilder.putFieldValue("lawname", billListDialog_selectLaw.getReturnBillVOs()[0].getStringValue("lawname"));
				BillVO itemVO = (BillVO) entry.getValue();
				sqlBuilder.putFieldValue("lawitemid", itemVO.getStringValue("lawitemid"));
				sqlBuilder.putFieldValue("lawitemtitle", itemVO.getStringValue("lawitemtitle"));
				sqlBuilder.putFieldValue("lawitemcontent", itemVO.getStringValue("lawitemcontent"));
				sqlBuilder.putFieldValue("MAPPINGTYPE", type);
				insertSql.add(sqlBuilder.getSQL());
			}
		}
		try {
			UIUtil.executeBatchByDS(null, insertSql);
			billList2.refreshData();
			dialog2.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onViewDetail() {
		billList_Detail = new BillListDialog(dialog2, "详细", "BAD_ISSUE_LAW_MAPPING_CODE1");
		billList_Detail.getBtn_confirm().setVisible(false);
		billList_Detail.getBtn_cancel().setText("返回");
		btn_remove = new WLTButton("移除");
		btn_remove.addActionListener(this);
		BillListPanel listPanel = billList_Detail.getBilllistPanel();
		listPanel.addBillListHtmlHrefListener(this);
		listPanel.addBillListButton(btn_remove);
		listPanel.repaintBillListButton();
		Iterator it = selectMappingItem.entrySet().iterator();
		BillVO vos[] = new BillVO[selectMappingItem.size()];
		for (int index = 0; it.hasNext(); index++) {
			java.util.Map.Entry en = (java.util.Map.Entry) it.next();
			vos[index] = (BillVO) en.getValue();
		}

		listPanel.addBillVOs(vos);
		billList_Detail.setVisible(true);
	}

	public void onRemove() {
		BillVO selectVOs[] = billList_Detail.getBilllistPanel().getSelectedBillVOs();
		if (selectVOs.length == 0) {
			MessageBox.showSelectOne(billList_Detail);
			return;
		}
		if (MessageBox.showConfirmDialog(billList_Detail, "你确实要删除此记录吗？") != 0)
			return;
		StringBuffer deleteSQLsb = new StringBuffer("delete from BAD_ISSUE_LAW_MAPPING where id in (-99999");
		boolean ifDelete = false;
		for (int i = 0; i < selectVOs.length; i++) {
			String mappingID = selectVOs[i].getStringValue("id");
			if (mappingID != null)
				try {
					deleteSQLsb.append((new StringBuilder(",")).append(mappingID).toString());
					if ("相关定性依据".equals(type))
						billList1.removeRow(billList1.findRow("id", mappingID));
					else if ("相关监管措施和处理依据".equals(type))
						billList2.removeRow(billList2.findRow("id", mappingID));
					ifDelete = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			selectMappingItem.remove(selectVOs[i].getStringValue("lawitemid"));
		}

		deleteSQLsb.append(")");
		try {
			if (ifDelete)
				UIUtil.executeBatchByDS(null, new String[] { deleteSQLsb.toString() });
			billList_Detail.getBilllistPanel().removeSelectedRows();
			setButtonContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onMapping() {
		selectMappingItem = new HashMap();
		BillVO mappingVOs[] = (BillVO[]) null;
		if ("相关定性依据".equals(type)) {
			mappingVOs = billList1.getAllBillVOs();
		} else if ("相关监管措施和处理依据".equals(type)) {
			mappingVOs = billList2.getAllBillVOs();
		}
		for (int i = 0; i < mappingVOs.length; i++) {
			selectMappingItem.put(mappingVOs[i].getStringValue("lawitemid"), mappingVOs[i]);
		}

		BillVO billvo = billTree.getSelectedVO();
		if (billvo == null) {
			MessageBox.show(this, "请选择一个问题类别！");
			return;
		}
		String str_linkcode = billvo.getStringValue("linkcode");
		if (str_linkcode.length() != 8) {
			MessageBox.show(this, "请选择一个问题类别!");
			return;
		}
		billListDialog_selectLaw = new BillListDialog(this, "请选择一条法规，点击下一步", "LAW_LAW_CODE2", 850, 650);
		billListDialog_selectLaw.getBilllistPanel().addBillListHtmlHrefListener(this);
		billListDialog_selectLaw.getBtn_confirm().setText("下一步");
		billListDialog_selectLaw.setVisible(true);
		if (billListDialog_selectLaw.getCloseType() != 1)
			return;
		BillVO billVOs[] = billListDialog_selectLaw.getReturnBillVOs();
		if (billVOs == null || billVOs.length == 0) {
			return;
		} else {
			WLTPanel main_panel = new WLTPanel(new BorderLayout());
			String lawID = billVOs[0].getStringValue("id");
			String lawname = billVOs[0].getStringValue("lawname");
			btn_join = new WLTButton("加入映射");
			btn_join.addActionListener(this);
			btn_confirm = new WLTButton("确定");
			btn_confirm.addActionListener(this);
			btn_cancel = new WLTButton("取消");
			btn_cancel.addActionListener(this);
			int count = selectMappingItem.size();
			btn_viewDetail = new WLTButton((new StringBuilder("查看详细(")).append(count).append(")").toString(), UIUtil.getImage(count != 0 ? "office_199.gif" : "office_062.gif"));
			setButtonContent();
			btn_viewDetail.addActionListener(this);
			WLTPanel wp_southPane = new WLTPanel(2, new FlowLayout(), LookAndFeel.defaultShadeColor1, false);
			wp_southPane.add(btn_confirm);
			wp_southPane.add(btn_cancel);
			dialog2 = new BillDialog(this, 750, 550);
			billTree_item = new BillTreePanel("LAW_LAW_ITEM_CODE1");
			billTree_item.setMoveUpDownBtnVisiable(false);
			BillTreeNodeVO treenode = new BillTreeNodeVO(-1, lawname);
			billTree_item.getRootNode().setUserObject(treenode);
			billTree_item.queryDataByCondition((new StringBuilder(" lawid = ")).append(lawID).toString());
			billTree_item.addBillTreeSelectListener(this);
			billCard_item = new BillCardPanel("LAW_LAW_ITEM_CODE1");
			billCard_item.addBatchBillCardButton(new WLTButton[] { btn_join, btn_viewDetail });
			billCard_item.repaintBillCardButton();
			billCard_item.setEditable(false);
			WLTSplitPane splitPane = new WLTSplitPane(1, billTree_item, billCard_item);
			splitPane.setDividerLocation(250);
			main_panel.add(splitPane, "Center");
			main_panel.add(wp_southPane, "South");
			dialog2.setTitle((new StringBuilder("请选择法规【")).append(lawname).append("】的条目进行映射").toString());
			dialog2.add(main_panel);
			dialog2.setVisible(true);
			return;
		}
	}

	private void setButtonContent() {
		int count = selectMappingItem.size();
		if (count > 0)
			btn_viewDetail.setText((new StringBuilder("<html>查看详细(<font color=red>")).append(count).append("</font>)<html>").toString());
		else
			btn_viewDetail.setText((new StringBuilder("查看详细(")).append(count).append(")").toString());
		btn_viewDetail.setIcon(UIUtil.getImage(count != 0 ? "office_199.gif" : "office_062.gif"));
	}

	private void onCancel() {
		dialog2.dispose();
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getBillListPanel().getTempletVO().getTablename().equalsIgnoreCase("BAD_ISSUE_LAW_MAPPING")) {
			String lawid = _event.getBillListPanel().getSelectedBillVO().getStringValue("lawid");
			showLawHtml(lawid);
		} else {
			String lawid = _event.getBillListPanel().getSelectedBillVO().getStringValue("id");
			showLawHtml(lawid);
		}
	}

	private void showLawHtml(String lawid) {
		new LawShowHtmlDialog(this, lawid);
	}

	public void onBillListSelectChanged(BillListSelectionEvent billlistselectionevent) {
	}

}
