package cn.com.pushworld.salary.ui.operateplan;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
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
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_ChildTableImport;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

/**
 * 年度经营计划维护【李春娟/2014-01-06】
 * 
 */
public class OperateplanWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener, BillListAfterQueryListener {
	private static final long serialVersionUID = 6046546556192612380L;
	private BillListPanel planListPanel, itemListPanel, targetListPanel;
	private WLTButton btn_delete, btn_copy;
	private WLTButton btn_additem, btn_edititem, btn_hiddentarget;
	private ImageIcon icon_up, icon_down;
	private WLTSplitPane splitPane1;
	private WLTButton btn_importTarget, btn_removeTarget;
	private SalaryServiceIfc salaryService;

	public void initialize() {
		Pub_Templet_1VO[] templetVOs;
		try {
			templetVOs = UIUtil.getPub_Templet_1VOs(new String[] { "SAL_YEAR_OPERATEPLAN_YQ_E01", "SAL_YEAR_PLAN_ITEM_ZYC_E01", "SAL_TARGET_LIST_LCJ_Q01" });
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		planListPanel = new BillListPanel(templetVOs[0]);
		planListPanel.QueryDataByCondition(null);//全部查出
		planListPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//单选

		btn_delete = new WLTButton("删除");
		btn_copy = new WLTButton("复制");
		btn_delete.addActionListener(this);
		btn_copy.addActionListener(this);
		planListPanel.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_POPINSERT), WLTButton.createButtonByType(WLTButton.LIST_POPEDIT), btn_delete, btn_copy });
		planListPanel.repaintBillListButton();
		planListPanel.addBillListSelectListener(this);
		planListPanel.addBillListAfterQueryListener(this);

		itemListPanel = new BillListPanel(templetVOs[1]);
		itemListPanel.setQuickQueryPanelVisiable(false);

		btn_additem = new WLTButton("新增");
		btn_additem.addActionListener(this);

		btn_edititem = new WLTButton("编辑");
		btn_edititem.addActionListener(this);

		TBUtil tbutil = TBUtil.getTBUtil();
		icon_up = UIUtil.getImage("office_136.gif");
		icon_down = new ImageIcon(tbutil.getImageRotate(icon_up.getImage(), 180)); //180度!

		btn_hiddentarget = new WLTButton(icon_up); //
		btn_hiddentarget.setPreferredSize(new Dimension(18, 18)); //
		btn_hiddentarget.setToolTipText("隐藏/显示指标"); //
		btn_hiddentarget.addActionListener(this);

		itemListPanel.addBatchBillListButton(new WLTButton[] { btn_additem, btn_edititem, WLTButton.createButtonByType(WLTButton.LIST_DELETE), btn_hiddentarget });
		itemListPanel.repaintBillListButton();
		itemListPanel.addBillListSelectListener(this);

		targetListPanel = new BillListPanel(templetVOs[2]);
		targetListPanel.setQuickQueryPanelVisiable(false);
		btn_importTarget = new WLTButton("导入");
		btn_removeTarget = new WLTButton("移除");

		btn_importTarget.addActionListener(this);
		btn_removeTarget.addActionListener(this);

		targetListPanel.addBatchBillListButton(new WLTButton[] { btn_importTarget, btn_removeTarget, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
		targetListPanel.repaintBillListButton();

		splitPane1 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, itemListPanel, targetListPanel);
		splitPane1.setDividerLocation(300);

		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, planListPanel, splitPane1);
		splitPane.setDividerLocation(420);
		this.add(splitPane);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_copy) {
			onCopy();
		} else if (e.getSource() == btn_additem) {
			onAddItem();
		} else if (e.getSource() == btn_edititem) {
			onEditItem();
		} else if (e.getSource() == btn_hiddentarget) {
			if (targetListPanel.isVisible()) {
				btn_hiddentarget.setIcon(icon_down);
				targetListPanel.setVisible(false);
			} else {
				btn_hiddentarget.setIcon(icon_up);
				splitPane1.setDividerLocation(300);
				targetListPanel.setVisible(true);
			}
		} else if (e.getSource() == btn_importTarget) {
			onImportTarget();
		} else if (e.getSource() == btn_removeTarget) {
			onRemoveTarget();
		}
	}

	/**
	 * 年度经营计划删除逻辑
	 */
	private void onDelete() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}

		if (MessageBox.confirmDel(this)) {
			String planid = billVO.getStringValue("id");
			try {
				UIUtil.executeBatchByDS(null, new String[] { "delete from " + planListPanel.getTempletVO().getSavedtablename() + " where id=" + planid, "delete from " + itemListPanel.getTempletVO().getSavedtablename() + " where planid=" + planid });
			} catch (Exception e) {
				e.printStackTrace();
			}
			planListPanel.refreshData();
			itemListPanel.clearTable();
		}
	}

	/**
	 * 复制年度经营计划
	 */
	private void onCopy() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(planListPanel);
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel(planListPanel.getTempletVO()); //创建一个卡片面板
		cardPanel.setLoaderBillFormatPanel(planListPanel.getLoaderBillFormatPanel()); //将列表的BillFormatPanel的句柄传给卡片
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setValueAt("name", new StringItemVO(billVO.getStringValue("name") + "_复制"));
		try {
			cardPanel.setValueAt("planitem", TBUtil.getTBUtil().deepClone(billVO.getObject("planitem")));//这里需要深度克隆，后面添加和移入岗位时需要修改评估方案的postlist字段的对象，如果这里不克隆，复制和被复制的记录都会改变
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置
		BillCardDialog dialog = new BillCardDialog(this, planListPanel.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		dialog.setVisible(true); //显示卡片窗口
		if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = planListPanel.newRow(false); //
			planListPanel.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			String newplanid = dialog.getBillVO().getStringValue("id");
			try {
				getService().copyOperatePlan(billVO.getStringValue("id"), newplanid);
				planListPanel.refreshData();
				if (newplanid != null && planListPanel.getRowCount() > 0) {//设置选中复制后的年度经营计划
					for (int i = 0; i < planListPanel.getRowCount(); i++) {
						if (newplanid.equals(planListPanel.getRealValueAtModel(i, "id"))) {
							planListPanel.setSelectedRow(i);
							break;
						}
					}
				}
				MessageBox.show(this, "复制成功!");
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}

	}

	private void onAddItem() {
		BillVO billVO = planListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.show(this, "请选择一条年度经营计划记录再进行此操作.");
			return;
		}
		HashMap map = new HashMap();
		map.put("planid", billVO.getStringValue("id"));
		itemListPanel.doInsert(map);
		try {
			updateitemListPanel(billVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onEditItem() {
		BillVO billVO = itemListPanel.getSelectedBillVO();
		int row = itemListPanel.getSelectedRow();
		boolean resave = false;
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel(itemListPanel.getTempletVO());
		cardPanel.setLoaderBillFormatPanel(itemListPanel.getLoaderBillFormatPanel());
		cardPanel.setBillVO(billVO); //
		String unitvalue = billVO.getStringValue("unitvalue");
		BillCardDialog dialog = new BillCardDialog(this, itemListPanel.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			if (itemListPanel.getSelectedRow() != -1) {
				BillVO returnVO = dialog.getBillVO();
				if (unitvalue != null && !unitvalue.equals(returnVO.getStringValue("unitvalue"))) {//如果单位不同，强制清空关联的指标
					returnVO.setObject("targetids", new RefItemVO("", "", ""));
					resave = true;
					targetListPanel.clearTable();
				}
				itemListPanel.setBillVOAt(itemListPanel.getSelectedRow(), returnVO);
				itemListPanel.setRowStatusAs(itemListPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		}
		try {
			if (resave) {
				String updatesql = " update sal_year_plan_item set targetids='' where id ='" + billVO.getStringValue("id") + "'";
				UIUtil.executeUpdateByDS(null, updatesql);
			}
			BillVO billVO2 = planListPanel.getSelectedBillVO();
			if (billVO2 != null) {
				updateitemListPanel(billVO2);
				itemListPanel.setSelectedRow(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导入指标
	 */
	private void onImportTarget() {
		BillVO billVO = itemListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.show(this, "请选择一条" + itemListPanel.getTempletVO().getTempletname() + "记录再进行此操作.");
			return;
		}

		try {
			RefDialog_ChildTableImport importDialog = new RefDialog_ChildTableImport(this, "导入", billVO.getRefItemVOValue("targetids"), null, itemListPanel.getTempletItemVO("targetids").getUCDfVO());
			StringBuffer tmp_targetids = new StringBuffer();
			for (int i = 0; i < itemListPanel.getRowCount(); i++) {
				String targetids = itemListPanel.getRealValueAtModel(i, "targetids");
				if (targetids != null && targetids.length() > 0) {
					tmp_targetids.append(targetids);
				}
			}
			//设置关联指标的过滤条件：相同单位，在本计划中其他条目中未关联的指标
			importDialog.getBillListPanel_1().setDataFilterCustCondition("unitvalue='" + billVO.getStringValue("unitvalue") + "' and id not in(" + TBUtil.getTBUtil().getInCondition(tmp_targetids.toString()) + ")");
			importDialog.setVisible(true);
			if (importDialog.getCloseType() == 1) {
				if (importDialog.getReturnRealValue() != null && importDialog.getReturnRealValue().length() > 0) {
					RefItemVO value = importDialog.getReturnRefItemVO();
					if (value != null && value.getId() != null && value.getId().length() > 0) {
						value.setId(";" + value.getId());
						targetListPanel.queryDataByCondition("id in(" + TBUtil.getTBUtil().getInCondition(value.getId()) + ")", "type,code");
						itemListPanel.setValueAt(value, itemListPanel.getSelectedRow(), "targetids");
						itemListPanel.setRowStatusAs(itemListPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_UPDATE);
						itemListPanel.saveData();
					}
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 移除指标
	 */
	private void onRemoveTarget() {
		BillVO billVO = itemListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.show(this, "请选择一条" + itemListPanel.getTempletVO().getTempletname() + "记录再进行此操作.");
			return;
		}

		int[] rows = targetListPanel.getSelectedRows();
		int allcount = targetListPanel.getRowCount();
		if (rows.length == 0) {
			MessageBox.show(this, "请至少选择一条指标记录再进行此操作.");
			return;
		}
		String targetids = billVO.getStringValue("targetids");
		if (rows.length == allcount) {
			targetids = "";
		} else {
			for (int i = 0; i < rows.length; i++) {
				String targetid = targetListPanel.getRealValueAtModel(rows[i], "id");
				targetids = TBUtil.getTBUtil().replaceAll(targetids, targetid + ";", "");
			}
		}
		targetListPanel.removeRows(rows);//移除所选条目

		itemListPanel.getSelectedBillVO().getRowNumberItemVO().setState(RowNumberItemVO.UPDATE);
		RefItemVO targetidsVO = billVO.getRefItemVOValue("targetids");
		targetidsVO.setId(targetids);
		billVO.setObject("targetids", targetidsVO);
		itemListPanel.setBillVOAt(itemListPanel.getSelectedRow(), billVO);
		itemListPanel.saveData();
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO billVO = _event.getCurrSelectedVO();
		if (billVO == null) {
			return;
		}
		if (_event.getSource() == planListPanel) {
			try {
				updateitemListPanel(billVO);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (_event.getSource() == itemListPanel) {
			String targetids = billVO.getStringValue("targetids");
			targetListPanel.queryDataByCondition("id in(" + TBUtil.getTBUtil().getInCondition(targetids) + ")", "type,code");
		}
	}

	public void onBillListAfterQuery(BillListAfterQueryEvent event) {
		itemListPanel.clearTable();
		itemListPanel.reload();
		targetListPanel.clearTable();
	}

	public SalaryServiceIfc getService() {
		if (salaryService == null) {
			try {
				salaryService = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return salaryService;
	}

	/**
	 * 动态加载itemListPanel列表
	 * @author 张营闯
	 * @throws Exception 
	 * @throws  
	 * */
	public void updateitemListPanel(BillVO billVO) throws Exception {
		itemListPanel.reload();
		itemListPanel.queryDataByCondition("planid= " + billVO.getStringValue("id"), "id");
		targetListPanel.clearTable();
		BillVO billVO2[] = itemListPanel.getAllBillVOs();
		if (billVO2 == null || billVO2.length == 0) {
			return;
		}
		List yearlist = new ArrayList();
		String yearstart = billVO.getStringValue("curryear");
		int endyear = Integer.parseInt(yearstart) - 4;
		String keyname[] = new String[5];
		String keynamevalue[] = new String[5];
		for (int i = 0; i < billVO2.length; i++) {
			String sql = "select t1.curryear ,t2.* from sal_year_plan_item t2  left join sal_year_operateplan t1 on t2.planid = t1.id where t2.name ='" + billVO2[i].getStringValue("name") + "' and t1.curryear <= '" + yearstart + "' and t1.curryear>='" + endyear + "' order by t1.curryear desc";
			HashVO hashVO[] = UIUtil.getHashVoArrayByDS(null, sql);
			for (int j = 0; j < hashVO.length; j++) {
				String yearindex = hashVO[j].getStringValue("curryear");
				if (!yearlist.contains(yearindex)) {
					yearlist.add(yearindex);
					keyname[j] = "计划值0" + yearlist.size();
					keynamevalue[j] = yearindex + "计划值";
					itemListPanel.insertColumn(2 + j, keyname[j]);
				}
				itemListPanel.setValueAt(new StringItemVO(hashVO[j].getStringValue("planvalue")), i, keyname[j]);
				itemListPanel.getTable().getColumn(keyname[j]).setHeaderValue(keynamevalue[j]);
			}
		}
		itemListPanel.repaint();
	}
}
