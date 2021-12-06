package com.pushworld.ipushgrc.ui.score.p020;

import java.math.BigDecimal;

import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * 违规积分-》违规积分登记 卡片中选择标准按钮的点击事件【李春娟/2013-05-29】
 * @author lcj
 * */

public class SelectScoreStandardWLTAction implements WLTActionListener, BillTreeSelectListener {
	private BillListPanel listPanel;
	private BillCardPanel cardPanel;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();//取得按钮事件父面板
		if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(cardPanel.getEditState())) {
			MessageBox.show(cardPanel, "非编辑状态,不能选择!");
			return;
		}

		BillVO billVO = cardPanel.getBillVO();
		if (billVO == null) {
			MessageBox.show(cardPanel, "当前记录为空,不能选择!");
			return;
		}

		BillListDialog listDialog = new BillListDialog(cardPanel, "请选择一条违规行为", "SCORE_STANDARD2_LCJ_E01");
		listDialog.setSize(850, 500);
		listPanel = listDialog.getBilllistPanel();
		AbstractWLTCompentPanel compentPanel = listPanel.getQuickQueryPanel().getCompentByKey("STATE");
		if (compentPanel != null) {
			compentPanel.setVisible(false);
		}
		listPanel.setDataFilterCustCondition("state='有效'");
		listPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		BillTreePanel typeTreePanel = new BillTreePanel("SCORE_TYPE_LCJ_E01");
		typeTreePanel.setMoveUpDownBtnVisiable(false);
		typeTreePanel.queryDataByCondition(null);//树查询一下

		String scoretype = billVO.getStringValue("SCORETYPEID");
		if (scoretype != null && !"".equals(scoretype)) {//这里进行优化，如果表单中选择了违规类型，则在树中展开，并查询该类型的记录【李春娟/2014-09-25】
			typeTreePanel.expandOneNodeByKey(scoretype);
			listPanel.QueryDataByCondition("scoretype=" + scoretype);
		}

		typeTreePanel.addBillTreeSelectListener(this);
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, typeTreePanel, listPanel);
		splitPane.setDividerLocation(220);
		listDialog.add(splitPane);
		listDialog.setVisible(true);
		if (listDialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO[] returnBillVOs = listDialog.getReturnBillVOs();
			setRealScore(billVO, returnBillVOs);
		}

	}

	private void setRealScore(BillVO billVO, BillVO[] returnBillVOs) {
		if (returnBillVOs != null && returnBillVOs.length > 0 && returnBillVOs[0] != null) {
			BillVO returnBillVO = returnBillVOs[0];
			String score = returnBillVO.getStringValue("SCORE");

			//可能在选择违规行为前未选择违规类型，故这里需要设置一下违规类型【李春娟/2013-05-30】
			cardPanel.setValueAt("SCORETYPEID", new RefItemVO(returnBillVO.getStringValue("SCORETYPE"), "", returnBillVO.getStringViewValue("SCORETYPE")));//违规类型id
			cardPanel.setRealValueAt("SCORETYPE", returnBillVO.getStringViewValue("SCORETYPE"));//违规类型

			cardPanel.setRealValueAt("SCORESTANDARDID", returnBillVO.getStringValue("id"));//违规行为id
			cardPanel.setRealValueAt("SCORESTANDARD2", returnBillVO.getStringValue("POINT"));//违规行为
			cardPanel.setRealValueAt("STANDARDSCORE", score);//违规分值
			ComBoxItemVO itemVO = billVO.getComBoxItemVOValue("FINDRANK");
			if (itemVO != null && itemVO.getCode() != null && score != null && !score.trim().equals("")) {
				BigDecimal findrankscore = new BigDecimal(itemVO.getCode());//为了保证精度准确不能用float*float,否则会出现3*1.2=3.6000001 的情况【李春娟/2014-03-13】

				if (score.contains("-")) {//如果是分值区间，则截取两个分值
					BigDecimal float_score1 = new BigDecimal(score.substring(0, score.indexOf("-")));
					BigDecimal float_score2 = new BigDecimal(score.substring(score.indexOf("-") + 1));

					Float float_realscore1 = findrankscore.multiply(float_score1).floatValue();
					Float float_realscore2 = findrankscore.multiply(float_score2).floatValue();
					String str_score = "";
					if (float_realscore1.intValue() == float_realscore1.floatValue()) {//判断该分值小数部分是否为0，如果为0则舍去小数点，如2.0-5.5变为2-5.5【李春娟/2013-05-30】
						str_score += float_realscore1.intValue();
					} else {
						str_score += float_realscore1;
					}
					str_score += "-";
					if (float_realscore2.intValue() == float_realscore2.floatValue()) {
						str_score += float_realscore2.intValue();
					} else {
						str_score += float_realscore2;
					}
					cardPanel.setRealValueAt("REFERSCORE", str_score);
				} else {
					BigDecimal float_score = new BigDecimal(score);
					Float float_realscore = findrankscore.multiply(float_score).floatValue();
					if (float_realscore.intValue() == float_realscore.floatValue()) {
						cardPanel.setRealValueAt("REFERSCORE", float_realscore.intValue() + "");
					} else {
						cardPanel.setRealValueAt("REFERSCORE", float_realscore + "");
					}
				}
			} else {
				cardPanel.setRealValueAt("REFERSCORE", "");
			}
		}

	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (_event.getCurrSelectedNode().isRoot() || _event.getCurrSelectedVO() == null) {
			listPanel.clearTable();
		} else {
			BillVO billVO = _event.getCurrSelectedVO();
			listPanel.QueryDataByCondition("scoretype=" + billVO.getStringValue("id"));
		}

	}
}
