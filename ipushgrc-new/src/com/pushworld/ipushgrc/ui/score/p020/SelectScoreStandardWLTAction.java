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
 * Υ�����-��Υ����ֵǼ� ��Ƭ��ѡ���׼��ť�ĵ���¼������/2013-05-29��
 * @author lcj
 * */

public class SelectScoreStandardWLTAction implements WLTActionListener, BillTreeSelectListener {
	private BillListPanel listPanel;
	private BillCardPanel cardPanel;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();//ȡ�ð�ť�¼������
		if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(cardPanel.getEditState())) {
			MessageBox.show(cardPanel, "�Ǳ༭״̬,����ѡ��!");
			return;
		}

		BillVO billVO = cardPanel.getBillVO();
		if (billVO == null) {
			MessageBox.show(cardPanel, "��ǰ��¼Ϊ��,����ѡ��!");
			return;
		}

		BillListDialog listDialog = new BillListDialog(cardPanel, "��ѡ��һ��Υ����Ϊ", "SCORE_STANDARD2_LCJ_E01");
		listDialog.setSize(850, 500);
		listPanel = listDialog.getBilllistPanel();
		AbstractWLTCompentPanel compentPanel = listPanel.getQuickQueryPanel().getCompentByKey("STATE");
		if (compentPanel != null) {
			compentPanel.setVisible(false);
		}
		listPanel.setDataFilterCustCondition("state='��Ч'");
		listPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		BillTreePanel typeTreePanel = new BillTreePanel("SCORE_TYPE_LCJ_E01");
		typeTreePanel.setMoveUpDownBtnVisiable(false);
		typeTreePanel.queryDataByCondition(null);//����ѯһ��

		String scoretype = billVO.getStringValue("SCORETYPEID");
		if (scoretype != null && !"".equals(scoretype)) {//��������Ż����������ѡ����Υ�����ͣ���������չ��������ѯ�����͵ļ�¼�����/2014-09-25��
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

			//������ѡ��Υ����Ϊǰδѡ��Υ�����ͣ���������Ҫ����һ��Υ�����͡����/2013-05-30��
			cardPanel.setValueAt("SCORETYPEID", new RefItemVO(returnBillVO.getStringValue("SCORETYPE"), "", returnBillVO.getStringViewValue("SCORETYPE")));//Υ������id
			cardPanel.setRealValueAt("SCORETYPE", returnBillVO.getStringViewValue("SCORETYPE"));//Υ������

			cardPanel.setRealValueAt("SCORESTANDARDID", returnBillVO.getStringValue("id"));//Υ����Ϊid
			cardPanel.setRealValueAt("SCORESTANDARD2", returnBillVO.getStringValue("POINT"));//Υ����Ϊ
			cardPanel.setRealValueAt("STANDARDSCORE", score);//Υ���ֵ
			ComBoxItemVO itemVO = billVO.getComBoxItemVOValue("FINDRANK");
			if (itemVO != null && itemVO.getCode() != null && score != null && !score.trim().equals("")) {
				BigDecimal findrankscore = new BigDecimal(itemVO.getCode());//Ϊ�˱�֤����׼ȷ������float*float,��������3*1.2=3.6000001 ����������/2014-03-13��

				if (score.contains("-")) {//����Ƿ�ֵ���䣬���ȡ������ֵ
					BigDecimal float_score1 = new BigDecimal(score.substring(0, score.indexOf("-")));
					BigDecimal float_score2 = new BigDecimal(score.substring(score.indexOf("-") + 1));

					Float float_realscore1 = findrankscore.multiply(float_score1).floatValue();
					Float float_realscore2 = findrankscore.multiply(float_score2).floatValue();
					String str_score = "";
					if (float_realscore1.intValue() == float_realscore1.floatValue()) {//�жϸ÷�ֵС�������Ƿ�Ϊ0�����Ϊ0����ȥС���㣬��2.0-5.5��Ϊ2-5.5�����/2013-05-30��
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
