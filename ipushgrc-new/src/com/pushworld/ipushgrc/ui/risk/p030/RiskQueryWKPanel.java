package com.pushworld.ipushgrc.ui.risk.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.favorite.MyFavoriteQueryWKPanel;
import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFGraphEditItemDialog;

/**
 * 风险点查询!查询公司风险和流程风险中流程文件文件状态为“有效”的风险
 * @author xch
 *
 */
public class RiskQueryWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {

	private BillListPanel billlist_risk = null; //

	public BillListPanel getBilllist_risk() {
		return billlist_risk;
	}

	private WLTButton btn_lookrisk;

	@Override
	public void initialize() {
		billlist_risk = new BillListPanel("V_RISK_PROCESS_FILE_CODE1"); //
		billlist_risk.setDataFilterCustCondition("(wfprocess_id is null)or (wfprocess_id is not null and filestate='3')");
		btn_lookrisk = new WLTButton("浏览");
		btn_lookrisk.addActionListener(this);
		WLTButton btn_joinFavority = MyFavoriteQueryWKPanel.getJoinFavorityButton("风险点", this.getClass().getName(), "risk_name");//加入收藏夹按钮
		billlist_risk.addBatchBillListButton(new WLTButton[] { btn_lookrisk, btn_joinFavority });
		billlist_risk.repaintBillListButton();
		billlist_risk.addBillListHtmlHrefListener(this);//风险矩阵添加链接事件
		this.add(billlist_risk); //
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillVO billvo = _event.getBillListPanel().getSelectedBillVO();
		if (billvo == null) {
			return;
		}
		if ("wfprocess_name".equals(_event.getItemkey())) {
			WFGraphEditItemDialog itemdialog = new WFGraphEditItemDialog(this, "查看流程[" + billvo.getStringValue("wfprocess_name") + "]", billvo.getStringValue("wfprocess_id"), new String[] { billvo.getStringValue("wfactivity_id") }, false, true);
			itemdialog.setVisible(true);
		} else {
			CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "查看文件和流程", billvo.getStringValue("cmpfile_id"));
			dialog.setShowprocessid(billvo.getStringValue("wfprocess_id"));
			dialog.setVisible(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		onLookRisk();
	}

	private void onLookRisk() {
		BillVO billVO = billlist_risk.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_RISK_CODE3"); //
		cardPanel.queryDataByCondition("id=" + billVO.getStringValue("risk_id"));
		cardPanel.execEditFormula("finalost");
		cardPanel.execEditFormula("cmplost");
		cardPanel.execEditFormula("honorlost");
		BillCardDialog carddialog = new BillCardDialog(billlist_risk, "风险点[" + billVO.getStringValue("risk_name") + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		carddialog.setVisible(true); //	
	}
}
