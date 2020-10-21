package com.pushworld.ipushgrc.ui.cmpevent.p170;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 整改跟踪维护
 * @author hm
 *
 */
public class CmpAdjustTrackWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener, BillListAfterQueryListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5423260581405561469L;
	private BillListPanel billlist_proj = null; //
	private BillListPanel billList_eventtrack = null; //
	private WLTButton btn_insert, btn_update, btn_delete, btn_list;

	private BillCardDialog updateDialog;
	private boolean ischange;
	private BillCardPanel cardPanel = null;

	private String getUpCardPanelCode() {//得到分割器上面的卡片code
		return "CMP_EVENT_ADJUSTPROJECT_CODE3";
	}

	private String getDownListPanelCode() {//得到分割器的下方的列表code
		return "CMP_EVENT_TRACK_CODE1_x";
	}

	private String getDownLinkedColumn() {//得到下方列表与上方面板的关联字段,下方列表有个字段参照上方的id
		return "projectid";
	}

	public void initialize() {
		billlist_proj = new BillListPanel(getUpCardPanelCode()); //方案
		billlist_proj.addBillListSelectListener(this);
		billlist_proj.addBillListAfterQueryListener(this);//增加查询后事件，清空子表【李春娟/2012-08-08】
		WLTButton view_btn = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "查看明细");
		billlist_proj.addBatchBillListButton(new WLTButton[] { view_btn });
		billlist_proj.repaintBillListButton();
		billList_eventtrack = new BillListPanel(this.getDownListPanelCode()); //方案跟踪
		btn_update = new WLTButton("修改");
		btn_update.addActionListener(this);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		btn_insert = new WLTButton("新增");
		btn_insert.addActionListener(this);
		billList_eventtrack.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list }); //
		billList_eventtrack.repaintBillListButton(); //刷新按钮!!!
		btn_insert.setEnabled(false);
		btn_update.setEnabled(false);
		btn_delete.setEnabled(false);
		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billlist_proj, billList_eventtrack); //
		split.setDividerLocation(380); //
		this.add(split); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) { //插入跟踪
			onInsert();
		}
		if (e.getSource() == btn_update) { //修改跟踪
			onupdate();
		}
	}

	/**
	 * 修改跟踪数据
	 */
	public void onupdate() {
		BillVO eventItemVO = billList_eventtrack.getSelectedBillVO();
		if (eventItemVO == null || "".equals(eventItemVO.getStringValue("id"))) {
			MessageBox.showSelectOne(this);
			return;
		}

		String sql0 = "select * from CMP_EVENT_ADJUSTSTEP where PROJECTID = " + billlist_proj.getSelectedBillVO().getStringValue("id");
		try {//检查是否符合  已整改 的条件
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, sql0);
			for (HashVO vo : vos) {
				if ("已落实".equals(vo.getStringValue("status"))) {//如果有状态是 没有落实的
					ischange = false;
				} else {
					ischange = true;
				}
			}
		} catch (Exception e1) {
			// TODO: handle exception
		}
		cardPanel = new BillCardPanel("CMP_EVENT_TRACK_CODE1_x");
		cardPanel.setBillVO(eventItemVO);
		updateDialog = new BillCardDialog(this, "修改", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);

		updateDialog.getBtn_save().addActionListener(new ActionListener() { //保存事件
					public void actionPerformed(ActionEvent e) {

						//状态控制, 只有所有子表的落实状态都是"已落实"时, 主表状态才能是"达到整改效果"
						//主表状态设置为"达到整改效果"时, 整改方法状态变为"已整改"
						if ("达到整改效果".equals(updateDialog.getBillcardPanel().getBillVO().getStringValue("result")) && ischange) {
							MessageBox.show(updateDialog, "[整改结果]设置无效\r\n系统发现有未落实的[整改措施]");
							return;
						} else if ("达到整改效果".equals(updateDialog.getBillcardPanel().getBillVO().getStringValue("result")) && !ischange) {
							//更新方案, 事件
							String sql1 = "update CMP_EVENT_ADJUSTPROJECT set status = '已整改' " + "where id = " + billlist_proj.getSelectedBillVO().getStringValue("id");
							String sql2 = "update CMP_EVENT set adjustresulttype='已整改' where id = " + billlist_proj.getSelectedBillVO().getStringValue("eventid");
							try {
								UIUtil.executeBatchByDS(null, new String[] { sql1, sql2 });
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							btn_insert.setEnabled(false);
							btn_update.setEnabled(false);
							btn_delete.setEnabled(false);
							billlist_proj.refreshCurrSelectedRow();
							updateDialog.onSave();
							return;
						} else {
							updateDialog.onSave();
						}
					}
				});
		updateDialog.getBtn_confirm().addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				//状态控制, 只有所有子表的落实状态都是"已落实"时, 主表状态才能是"达到整改效果"
				//主表状态设置为"达到整改效果"时, 整改方法状态变为"已整改"
				if ("达到整改效果".equals(updateDialog.getBillcardPanel().getBillVO().getStringValue("result")) && ischange) {
					MessageBox.show(updateDialog, "[整改结果]设置无效\r\n系统发现有未落实的[整改措施]");
					return;
				} else if ("达到整改效果".equals(updateDialog.getBillcardPanel().getBillVO().getStringValue("result")) && !ischange) {
					//更新方案, 事件
					String sql1 = "update CMP_EVENT_ADJUSTPROJECT set status = '已整改' " + "where id = " + billlist_proj.getSelectedBillVO().getStringValue("id");
					String sql2 = "update CMP_EVENT set adjustresulttype='已整改' where id = " + billlist_proj.getSelectedBillVO().getStringValue("eventid");
					try {
						UIUtil.executeBatchByDS(null, new String[] { sql1, sql2 });
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					btn_insert.setEnabled(false);
					btn_update.setEnabled(false);
					btn_delete.setEnabled(false);
					billlist_proj.refreshCurrSelectedRow();
					updateDialog.onConfirm();
					billList_eventtrack.refreshCurrSelectedRow();
					return;
				} else {
					updateDialog.onConfirm();
					billList_eventtrack.refreshCurrSelectedRow();
				}
			}
		});
		updateDialog.setVisible(true);
	}

	/**
	 * 插入跟踪数据
	 */
	public void onInsert() {
		BillVO eventItemVO = billlist_proj.getSelectedBillVO();
		if (eventItemVO == null || "".equals(eventItemVO.getStringValue("id"))) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_EVENT_TRACK_CODE1_x");
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setValueAt(this.getDownLinkedColumn(), new StringItemVO(eventItemVO.getStringValue("id")));
		cardPanel.setValueAt("projectname", new StringItemVO(eventItemVO.getStringValue("projectname")));
		final BillCardDialog insertDialog = new BillCardDialog(this, "新增", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);

		insertDialog.getBtn_confirm().removeAll();
		insertDialog.getBtn_confirm().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insertTraceMsg(true, insertDialog);
			}
		});
		insertDialog.getBtn_save().removeAll();
		insertDialog.getBtn_save().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				insertTraceMsg(false, insertDialog);
			}
		});
		insertDialog.setVisible(true);
	}

	public void insertTraceMsg(boolean isDispose, BillCardDialog insertDialog) {
		BillCardPanel card = insertDialog.getBillcardPanel();
		card.stopEditing(); //
		if (!card.checkValidate()) {
			return;
		}
		String selectedStatus = card.getBillVO().getStringValue("result");

		if ("达到整改效果".equals(selectedStatus)) {
			boolean ok = true;
			String sql0 = "select * from CMP_EVENT_ADJUSTSTEP where PROJECTID = " + billlist_proj.getSelectedBillVO().getStringValue("id");
			try {
				//状态控制, 只有所有子表的落实状态都是"已落实"时, 主表状态才能是"达到整改效果"				
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, sql0);
				for (HashVO vo : vos) {
					if (!"已落实".equals(vo.getStringValue("status"))) {//如果有状态是 没有落实的
						ok = false;
						break;
					}
				}
			} catch (WLTRemoteException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			//主表状态设置为"达到整改效果"时, 整改方案, 事件状态变为"已整改"
			if (ok) {//符合条件，可以插入
				try {
					card.updateData();
					if (isDispose)
						insertDialog.dispose(); //
					else
						MessageBox.show(this, "保存数据成功!");

					//更新方案, 事件
					String sql1 = "update CMP_EVENT_ADJUSTPROJECT set status = '已整改' " + "where id = " + billlist_proj.getSelectedBillVO().getStringValue("id");
					String sql2 = "update CMP_EVENT set adjustresulttype='已整改' where id = " + billlist_proj.getSelectedBillVO().getStringValue("eventid");
					UIUtil.executeBatchByDS(null, new String[] { sql1, sql2 });
					billlist_proj.refreshCurrSelectedRow();
					btn_insert.setEnabled(false);
					btn_update.setEnabled(false);
					btn_delete.setEnabled(false);
				} catch (Exception e1) {
					e1.printStackTrace();
				}//真正保存数据
			} else {
				MessageBox.show(this, "[整改结果]设置无效\r\n系统发现有未落实的[整改措施]");
				return;
			}
		} else {
			try {
				card.updateData();//真正保存数据				
			} catch (Exception e1) {
				e1.printStackTrace();
			} //保存数据了??
			if (isDispose)
				insertDialog.dispose(); //
			else
				MessageBox.show(this, "保存数据成功!");
		}

		billList_eventtrack.QueryDataByCondition(" projectid = " + billlist_proj.getSelectedBillVO().getStringValue("id"));
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		billList_eventtrack.QueryDataByCondition(" " + this.getDownLinkedColumn() + "=" + _event.getCurrSelectedVO().getStringValue("id"));
		if ("已整改".equals(_event.getCurrSelectedVO().getStringValue("status"))) {
			btn_insert.setEnabled(false);
			btn_update.setEnabled(false);
			btn_delete.setEnabled(false);
		} else {
			btn_insert.setEnabled(true);
			btn_update.setEnabled(true);
			btn_delete.setEnabled(true);
		}
	}

	//增加查询后事件，清空子表【李春娟/2012-08-08】
	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		billList_eventtrack.clearTable();
	}
}
