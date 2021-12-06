package com.pushworld.ipushgrc.ui.score.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 违规积分-》基础信息-》风险等级定义【李春娟/2013-05-09】
 * @author lcj
 *
 */
public class RiskRankEditWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_save, btn_refresh;
	private HashMap riskMap = new HashMap();

	@Override
	public void initialize() {
		listPanel = new BillListPanel("PUB_COMBOBOXDICT_LCJ_E01");
		btn_refresh = new WLTButton("刷新");
		btn_refresh.addActionListener(this);
		btn_save = listPanel.getBillListBtn("$列表保存");
		btn_save.addActionListener(this);//列表保存按钮增加事件
		
		listPanel.addBillListButton(btn_refresh);
		listPanel.repaintBillListButton();
		
		this.setLayout(new BorderLayout());
		this.add(listPanel);
		onRefresh();//自动查询一下
		onUpdateRiskMap();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			onSave();
		} else if (e.getSource() == btn_refresh) {
			onRefresh();
		}
	}

	/**
	 * 保存按钮逻辑
	 */
	private void onSave() {
		if (!listPanel.checkValidate()) {
			return;
		}
		listPanel.saveData();
		ArrayList sqlList = new ArrayList();
		for (int i = 0; i < listPanel.getRowCount(); i++) {
			String pkvalue = listPanel.getRealValueAtModel(i, "PK_PUB_COMBOBOXDICT");
			String oldvalue = (String) riskMap.get(pkvalue);
			String updatevalue = listPanel.getRealValueAtModel(i, "id");
			if (oldvalue != null && !oldvalue.equals(updatevalue)) {
				sqlList.add("update score_standard set riskrank='" + updatevalue + "' where riskrank='" + oldvalue + "'");//如果修改了数据则同时修改违规标准中记录
			}
		}
		BillVO[] delBillVOs = listPanel.getDeletedBillVOs();
		if (delBillVOs != null && delBillVOs.length > 0) {
			for (int i = 0; i < delBillVOs.length; i++) {
				sqlList.add("delete from score_standard where riskrank='" + delBillVOs[i].getStringValue("id") + "'");//如果删除了数据则同时删除违规标准中记录
			}
		}
		if (sqlList.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, sqlList);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
		onUpdateRiskMap();//更新风险等级map
	}

	/**
	 * 刷新按钮逻辑，即查询一下
	 */
	private void onRefresh() {
		listPanel.QueryDataByCondition("type='违规积分_风险等级'");
	}

	/**
	 * 更新风险等级map
	 */
	private void onUpdateRiskMap() {
		riskMap.clear();
		for (int i = 0; i < listPanel.getRowCount(); i++) {
			riskMap.put(listPanel.getRealValueAtModel(i, "PK_PUB_COMBOBOXDICT"), listPanel.getRealValueAtModel(i, "id"));
		}
	}
}
