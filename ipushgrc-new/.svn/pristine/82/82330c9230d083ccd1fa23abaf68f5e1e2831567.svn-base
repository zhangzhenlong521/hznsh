package com.pushworld.ipushgrc.ui.law.p030;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
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
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

import com.pushworld.ipushgrc.ui.law.LawAndRuleShowDetailDialog;
import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;

/**
 * 法规映射维护! 法规映射实现购物车效果！
 * 
 * @author xch
 * 
 */
public class LawMappingEditWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener, BillListHtmlHrefListener {

	private BillTreePanel billTree_bsact = null; // 业务活动树
	public BillListPanel billList_mapping = null; // 映射列表

	private WLTButton btn_mapping, btn_delete, btn_join, btn_confirm, btn_cancel, btn_viewDetail, btn_remove; // 映射，删除，加入映射，确定，取消，查看详细,移除(在查看详细里面可以移除选择条目)

	private BillTreePanel billTree_item; // 条目页面的树
	private BillCardPanel billCard_item; // 条目页面的卡片

	private BillListDialog billListDialog_selectLaw, billList_Detail;
	private BillDialog dialog2;
	private HashMap selectMappingItem;

	public void initialize() {

		billTree_bsact = new BillTreePanel("LAW_BSACT_CODE1");
		billList_mapping = new BillListPanel("LAW_MAPPING_CODE1");
		billList_mapping.addBillListHtmlHrefListener(this);
		billTree_bsact.setMoveUpDownBtnVisiable(false);
		billTree_bsact.queryDataByCondition(null);
		billTree_bsact.addBillTreeSelectListener(this);

		btn_mapping = new WLTButton("映射");
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_mapping.addActionListener(this);
		billList_mapping.addBatchBillListButton(new WLTButton[] { btn_mapping, btn_delete });
		billList_mapping.repaintBillListButton();

		WLTSplitPane split = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_bsact, billList_mapping); //
		split.setDividerLocation(210);
		this.add(split); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_mapping) {
			onMapping();
		} else if (obj == btn_join) {
			onJoin();
		} else if (obj == btn_confirm) {
			onConfirm();
		} else if (obj == btn_viewDetail) {
			onViewDetail();
		} else if (obj == btn_remove) {
			onRemove();
		} else if (obj == btn_cancel) {
			onCancel();
		}
	}

	/**
	 * 点击加入按钮 加入到购物车
	 */
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
			billVo.setTempletCode("LAW_MAPPING_CODE1");
			billVo.setKeys(billList_mapping.getTempletVO().getItemKeys());
			Object obj[] = new Object[8];
			billVo.setDatas(obj);
			billVo.setObject("bsactid", new StringItemVO(billTree_bsact.getSelectedVO().getStringValue("id")));
			billVo.setObject("lawid", new StringItemVO(billListDialog_selectLaw.getReturnBillVOs()[0].getStringValue("id")));
			billVo.setObject("lawname", new StringItemVO(billListDialog_selectLaw.getReturnBillVOs()[0].getStringValue("lawname")));
			billVo.setObject("lawitemid", new StringItemVO(itemVO.getStringValue("id")));
			billVo.setObject("lawitemtitle", new StringItemVO(itemVO.getStringValue("itemtitle")));
			billVo.setObject("lawitemcontent", new StringItemVO(itemVO.getStringValue("itemcontent")));
			selectMappingItem.put(itemVO.getStringValue("id"), billVo);
			setButtonContent();
			MessageBox.show(dialog2, "映射成功，浏览或移除映射请点击[查看详细]按钮!");
		}
	}

	/**
	 * 确定，会保存新映射的条目。
	 */
	public void onConfirm() {
		Iterator it = selectMappingItem.entrySet().iterator();
		List insertSql = new ArrayList();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			BillVO billvo = (BillVO) entry.getValue();
			if (billvo.getStringValue("id") == null) {
				InsertSQLBuilder sqlBuilder = new InsertSQLBuilder("law_mapping");
				try {
					sqlBuilder.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_LAW_MAPPING"));
				} catch (Exception e) {
					e.printStackTrace();
				}
				sqlBuilder.putFieldValue("bsactid", billTree_bsact.getSelectedVO().getStringValue("id"));
				sqlBuilder.putFieldValue("lawid", billListDialog_selectLaw.getReturnBillVOs()[0].getStringValue("id"));
				sqlBuilder.putFieldValue("lawname", billListDialog_selectLaw.getReturnBillVOs()[0].getStringValue("lawname"));
				BillVO itemVO = (BillVO) entry.getValue();
				sqlBuilder.putFieldValue("lawitemid", itemVO.getStringValue("lawitemid"));
				sqlBuilder.putFieldValue("lawitemtitle", itemVO.getStringValue("lawitemtitle"));
				sqlBuilder.putFieldValue("lawitemcontent", itemVO.getStringValue("lawitemcontent"));
				insertSql.add(sqlBuilder.getSQL());
			}
		}
		try {
			UIUtil.executeBatchByDS(null, insertSql);
			billList_mapping.refreshData();
			dialog2.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点击查看详细按钮。
	 */
	public void onViewDetail() {
		billList_Detail = new BillListDialog(dialog2, "详细", "LAW_MAPPING_CODE1");
		billList_Detail.getBtn_confirm().setVisible(false);
		billList_Detail.getBtn_cancel().setText("返回");
		btn_remove = new WLTButton("移除");
		btn_remove.addActionListener(this);
		BillListPanel listPanel = billList_Detail.getBilllistPanel();
		listPanel.addBillListHtmlHrefListener(this);
		listPanel.addBillListButton(btn_remove);
		listPanel.repaintBillListButton();
		Iterator it = selectMappingItem.entrySet().iterator();
		BillVO[] vos = new BillVO[selectMappingItem.size()];
		int index = 0;
		while (it.hasNext()) {
			Entry en = (Entry) it.next();
			vos[index] = (BillVO) en.getValue();
			index++;
		}
		listPanel.addBillVOs(vos);
		billList_Detail.setVisible(true);
	}

	/**
	 * 移除按钮。 如果是新映射的条目，记录中没有ID，那么这条记录直接从Map和列表中中remove就可以了。
	 * 如果是已经映射过的条目，需要删除此条记录，移除本列表中内容，并且移除最父亲的面板中的此条记录！
	 * 
	 */
	public void onRemove() {
		BillVO[] selectVOs = billList_Detail.getBilllistPanel().getSelectedBillVOs();
		if (selectVOs.length == 0) {
			MessageBox.showSelectOne(billList_Detail);
			return;
		}
		if (MessageBox.showConfirmDialog(billList_Detail, "你确实要删除此记录吗？") != JOptionPane.YES_OPTION) {
			return;
		}
		StringBuffer deleteSQLsb = new StringBuffer("delete from law_mapping where id in (-99999");
		boolean ifDelete = false;
		for (int i = 0; i < selectVOs.length; i++) {
			String mappingID = selectVOs[i].getStringValue("id");
			if (mappingID != null) { // 如果为空 说明还没有映射保存
				try {
					deleteSQLsb.append("," + mappingID);
					billList_mapping.removeRow(billList_mapping.findRow("id", mappingID));
					ifDelete = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
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

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		Object obj = _event.getSource();
		if (obj == billTree_bsact) {
			if (billTree_bsact.getSelectedNode() == null || billTree_bsact.getSelectedNode().isRoot()) {
				return;
			}
			billList_mapping.QueryDataByCondition(" bsactid = " + billTree_bsact.getSelectedVO().getStringValue("id"));
		} else if (obj == billTree_item) {
			if (billTree_item.getSelectedNode() == null) {
				return;
			}
			if (billTree_item.getSelectedNode().isRoot()) {
				billCard_item.clear();
				return;
			}
			billCard_item.setBillVO(billTree_item.getSelectedVO());
		}
	}

	/**
	 * 第一个面板的映射按钮。会弹出一个法规列表，进行选择，然后点击下一步加入到真正映射的页面。
	 */
	public void onMapping() {
		selectMappingItem = new HashMap();
		BillVO[] mappingVOs = billList_mapping.getAllBillVOs();
		for (int i = 0; i < mappingVOs.length; i++) {
			System.out.println(mappingVOs[i].getEditType());
			selectMappingItem.put(mappingVOs[i].getStringValue("lawitemid"), mappingVOs[i]);
		}
		BillVO bsd_bsactVO = billTree_bsact.getSelectedVO();
		if (bsd_bsactVO == null) {
			MessageBox.show(this, "请选择一条[业务活动]记录!");
			return;
		}
		billListDialog_selectLaw = new BillListDialog(this, "请选择一条法规，点击下一步", "LAW_LAW_CODE2", 850, 650);
		billListDialog_selectLaw.getBilllistPanel().addBillListHtmlHrefListener(this);
		billListDialog_selectLaw.getBtn_confirm().setText("下一步");
		billListDialog_selectLaw.setVisible(true);
		if (billListDialog_selectLaw.getCloseType() != 1) {
			return; //
		}

		BillVO[] billVOs = billListDialog_selectLaw.getReturnBillVOs();
		if (billVOs == null || billVOs.length == 0) {
			return;
		}
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
		btn_viewDetail = new WLTButton("查看详细(" + count + ")", UIUtil.getImage(count == 0 ? "office_062.gif" : "office_199.gif"));
		setButtonContent();
		btn_viewDetail.addActionListener(this);
		WLTPanel wp_southPane = new WLTPanel(WLTPanel.HORIZONTAL_RIGHT_TO_LEFT, new FlowLayout(), LookAndFeel.defaultShadeColor1, false); // 顶部状态面板，购物车状态
		wp_southPane.add(btn_confirm);
		wp_southPane.add(btn_cancel);
		dialog2 = new BillDialog(this, 750, 550);

		billTree_item = new BillTreePanel("LAW_LAW_ITEM_CODE1");
		billTree_item.setMoveUpDownBtnVisiable(false);
		BillTreeNodeVO treenode = new BillTreeNodeVO(-1, lawname);
		billTree_item.getRootNode().setUserObject(treenode);
		billTree_item.queryDataByCondition(" lawid = " + lawID);
		billTree_item.addBillTreeSelectListener(this);
		billCard_item = new BillCardPanel("LAW_LAW_ITEM_CODE1");
		billCard_item.addBatchBillCardButton(new WLTButton[] { btn_join, btn_viewDetail });
		billCard_item.repaintBillCardButton();
		billCard_item.setEditable(false);

		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_item, billCard_item);
		splitPane.setDividerLocation(250);
		main_panel.add(splitPane, BorderLayout.CENTER);
		main_panel.add(wp_southPane, BorderLayout.SOUTH);
		dialog2.setTitle("请选择法规【" + lawname + "】的条目进行映射");
		dialog2.add(main_panel);
		dialog2.setVisible(true);
	}

	/**
	 * 设置查看详细按钮的内容和图片。初始化此面板时候不能直接调用此方法，因为这个东西会按照字内容设置大小！
	 */
	private void setButtonContent() {
		int count = selectMappingItem.size();
		if (count > 0) {
			btn_viewDetail.setText("<html>查看详细(<font color=red>" + count + "</font>)<html>");
		} else {
			btn_viewDetail.setText("查看详细(" + count + ")");
		}
		btn_viewDetail.setIcon(UIUtil.getImage(count == 0 ? "office_062.gif" : "office_199.gif"));
	}

	private void onCancel() {
		dialog2.dispose();
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getBillListPanel().getTempletVO().getTablename().equalsIgnoreCase("law_mapping")) {
			String lawid = _event.getBillListPanel().getSelectedBillVO().getStringValue("lawid");
			showLawHtml(lawid,_event.getBillListPanel().getSelectedBillVO().getStringValue("LAWITEMID"));
		} else {
			new LawAndRuleShowDetailDialog("LAW_LAW_ITEM_CODE1", _event.getBillListPanel(), 850, 550);
		}
	}

	/*
	 * 点击映射列表法规名称时 弹出html。 
	 */
	private void showLawHtml(String lawid,String itemid) {
		new LawShowHtmlDialog(this, lawid,null,new String[]{itemid});
	}

}
