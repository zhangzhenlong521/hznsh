package com.pushworld.ipushgrc.ui.cmpcheck.p020;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;

public class Btn_CheckPointsActionListener implements WLTActionListener {
	BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			MessageBox.show(cardPanel, "浏览状态不允许执行此操作！");
			return;
		}
		onRefCheckPoints();
	}

	/**
	 * 点击 参考检查要点 按钮的逻辑。根据已导入的流程文件 弹出 检查要点面板。选择检查要点 按照
	 * [文件名称][流程名称][环节][检查要点说明][抽象方法] 格式化文字放到 检查要点说明字段中。 hm
	 */
	private void onRefCheckPoints() {
		if (cardPanel != null) {
			CardCPanel_ChildTable childTable = (CardCPanel_ChildTable) cardPanel.getCompentByKey("items");
			if (childTable.getBillListPanel().getBillVOs().length == 0) {
				MessageBox.show(cardPanel, "请在检查项目添加流程文件！");
				return;
			} else {
				BillVO[] vos = childTable.getBillListPanel().getBillVOs();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < vos.length; i++) {
					String cmpfileID = vos[i].getStringValue("cmp_cmpfile_id");
					if ((i == 0 || sb.length() == 0) && cmpfileID != null && !cmpfileID.equals("")) {
						sb.append("'" + cmpfileID + "'");
					} else if (cmpfileID != null && !cmpfileID.equals("")) {
						sb.append(",'" + cmpfileID + "'");
					}
				}
				if (sb.length() == 0) {
					sb.append("'-99999'");
				}
				BillListDialog listDialog = new BillListDialog(cardPanel, "检查要点", "CMP_CMPFILE_CHECKPOINT_CODE1", 650, 400);
				BillListPanel listpanel = listDialog.getBilllistPanel();
				listpanel.QueryDataByCondition(" cmpfile_id in (" + sb.toString() + ")");
				if (listpanel.getRowCount() == 0) {
					listDialog.dispose();
					MessageBox.show(cardPanel, "检查项目中对应的流程文件没有检查要点!");//如果没有检查要点，要进行提示，不需要弹出空空的列表【李春娟/2012-03-28】
					return;
				}
				listpanel.setItemVisible("checktype_name", false);
				listpanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
				listpanel.repaintBillListButton();
				listDialog.setVisible(true);
				if (listDialog.getCloseType() != 1) {
					return;
				}
				BillVO[] returnVOs = listDialog.getReturnBillVOs();
				StringBuffer pointsb = new StringBuffer();
				for (int i = 0; i < returnVOs.length; i++) {
					if (returnVOs[i].getStringValue("checkitem_point") != null) {
						pointsb.append("[" + returnVOs[i].getStringValue("cmpfile_name", "无") + "]文件-[" + returnVOs[i].getStringValue("wfprocess_name", "无") + "]流程-[" + returnVOs[i].getStringValue("wfactivity_name", "无") + "]环节\r\n");
						pointsb.append(returnVOs[i].getStringValue("checkitem_point") + "\r\n");
					}
				}
				if (pointsb.length() > 0) {
					String str = cardPanel.getBillVO().getStringValue("checkpoints");
					if (str == null || str.equals("")) {
						cardPanel.setValueAt("checkpoints", new StringItemVO(pointsb.toString()));
					} else {
						cardPanel.setValueAt("checkpoints", new StringItemVO(str + "\r\n" + pointsb.toString()));
					}
				}
			}
		}
	}
}
