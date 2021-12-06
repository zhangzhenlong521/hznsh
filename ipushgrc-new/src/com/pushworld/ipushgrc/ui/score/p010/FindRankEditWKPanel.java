package com.pushworld.ipushgrc.ui.score.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 违规积分-》基础信息-》发现渠道定义【李春娟/2013-05-09】
 * @author lcj
 *
 */
public class FindRankEditWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_help, btn_save, btn_refresh;
	private HashMap findMap = new HashMap();

	@Override
	public void initialize() {
		int model = TBUtil.getTBUtil().getSysOptionIntegerValue("违规积分扣分模式", 1);
		if (model == 1) {
			listPanel = new BillListPanel("PUB_COMBOBOXDICT_LCJ_E02");
		} else {
			listPanel = new BillListPanel("PUB_COMBOBOXDICT_LCJ_E03");
		}
		btn_refresh = new WLTButton("刷新");
		btn_refresh.addActionListener(this);
		btn_save = listPanel.getBillListBtn("$列表保存");
		btn_save.addActionListener(this);//列表保存按钮增加事件

		listPanel.addBillListButton(btn_refresh);
		if (model != 1) {
			btn_help = new WLTButton("帮助");
			btn_help.addActionListener(this);
			listPanel.addBillListButton(btn_help);
		}
		listPanel.repaintBillListButton();
		this.add(listPanel, BorderLayout.CENTER);
		onRefresh();//自动查询一下
		onUpdateRiskMap();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			onSave();
		} else if (e.getSource() == btn_refresh) {
			onRefresh();
		} else if (e.getSource() == btn_help) {
			onShowHelp();
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
			String oldvalue = (String) findMap.get(pkvalue);
			String updatevalue = listPanel.getRealValueAtModel(i, "id");
			if (oldvalue != null && !oldvalue.equals(updatevalue)) {
				sqlList.add("update score_standard set findrank='" + updatevalue + "' where findrank='" + oldvalue + "'");//如果修改了数据则同时修改违规标准中记录
			}
		}
		BillVO[] delBillVOs = listPanel.getDeletedBillVOs();
		if (delBillVOs != null && delBillVOs.length > 0) {
			for (int i = 0; i < delBillVOs.length; i++) {
				sqlList.add("delete from score_standard where findrank='" + delBillVOs[i].getStringValue("id") + "'");//如果删除了数据则同时删除违规标准中记录
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
		listPanel.QueryDataByCondition("type='违规积分_发现渠道'");
	}

	/**
	 * 更新风险等级map
	 */
	private void onUpdateRiskMap() {
		findMap.clear();
		for (int i = 0; i < listPanel.getRowCount(); i++) {
			findMap.put(listPanel.getRealValueAtModel(i, "PK_PUB_COMBOBOXDICT"), listPanel.getRealValueAtModel(i, "id"));
		}
	}

	private void onShowHelp() {
		String text = "假如一项违规行为规定的积分是3分，\r\n发现渠道分为员工自查、上级机构检查、外部监管部门检查三类，\r\n积分倍数依次为1倍、1.5倍、2倍，\r\n则积分依次为1倍*3分=3分，1.5倍*3分=4.5分，2倍*3分=6分。";
		MessageBox.showTextArea(this, text);
	}

}
