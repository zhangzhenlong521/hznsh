package com.pushworld.ipushgrc.ui.wfrisk.p050;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;

import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFGraphEditItemDialog;
import com.pushworld.ipushgrc.ui.wfrisk.p010.WFAndRiskEditWKPanel;

/**
 * 流程体系视图实现类
 *
 */
public class WFAndRiskBomViewByIcsysPanel extends WLTPanel implements ActionListener, BillListHtmlHrefListener {
	private WLTButton btn_lookrisk;
	private BillListPanel billlist_risk;

	public WFAndRiskBomViewByIcsysPanel(String _type, BillBomPanel _billBomPanel) {
		this.setLayout(new BorderLayout()); //
		String type[] = _type.split("%");

		if (type.length == 1) {//因为查看风险、流程、文件的三个视图中都有ictypeid，ictypename，所以这里也可直接配体系要素名称
			type = new String[] { "ictypename", type[0] };
		} else if (type.length != 2) {
			MessageBox.show("请检查Bom图的设置条件!");
			return;
		}

		JPanel mainPanel = new WLTPanel(BackGroundDrawingUtil.INCLINE_NW_TO_SE, new BorderLayout(), LookAndFeel.defaultShadeColor1, false); //
		String bomtype = (String) _billBomPanel.getClientProperty("BOMTYPE");
		if ("RISK".equals(bomtype)) {
			String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("各功能中流程文件的模板", "风险地图查看", "V_RISK_PROCESS_FILE_CODE1");//【李春娟/2012-07-18】
			billlist_risk = new BillListPanel(templetcode);
			billlist_risk.setDataFilterCustCondition(type[0] + "='" + type[1] + "' and filestate='3'");
			billlist_risk.QueryDataByCondition(null);
			if (billlist_risk.getTempletItemVO("bsactid").getItemname().contains("内控")) {
				billlist_risk.getQuickQueryPanel().setVisiable("bsactid", false);//内控系统中将cmp_risk 中的bsactid 字段来存的内控体系，bsactname存的內控体系名称
				billlist_risk.setItemVisible("bsactname", false);
			}
			btn_lookrisk = new WLTButton("浏览");
			btn_lookrisk.addActionListener(this);
			billlist_risk.addBillListButton(btn_lookrisk);
			billlist_risk.repaintBillListButton();
			billlist_risk.addBillListHtmlHrefListener(this);
			mainPanel.add(billlist_risk, BorderLayout.CENTER);
		} else if ("PROCESS".equals(bomtype)) {
			String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("各功能中流程文件的模板", "流程地图查看", "V_FILE_PROCESS_CODE1");//【李春娟/2012-07-18】
			BillListPanel billlist_process = new BillListPanel(templetcode);
			billlist_process.setDataFilterCustCondition(type[0] + "='" + type[1] + "' and filestate='3'");
			billlist_process.QueryDataByCondition(null);
			billlist_process.getQuickQueryPanel().setVisiable("ictypeid", false);
			billlist_process.addBillListHtmlHrefListener(this);
			mainPanel.add(billlist_process, BorderLayout.CENTER);
		} else if ("CMPFILE".equals(bomtype)) {
			WFAndRiskEditWKPanel panel_wfAndRisk = new WFAndRiskEditWKPanel();
			panel_wfAndRisk.setEditable(false);
			panel_wfAndRisk.initialize();
			BillListPanel billlist_cmpfile = panel_wfAndRisk.getBillList_cmpfile();
			billlist_cmpfile.setDataFilterCustCondition(type[0] + "='" + type[1] + "' and filestate='3'");
			billlist_cmpfile.QueryDataByCondition(null);
			billlist_cmpfile.getQuickQueryPanel().setVisiable("ictypeid", false);
			billlist_cmpfile.setItemVisible("ictypeid", false);
			mainPanel.add(panel_wfAndRisk, BorderLayout.CENTER);
		}
		String name = type[1];
		try {
			if (!"ictypename".equalsIgnoreCase(type[0])) {
				name = UIUtil.getStringValueByDS(null, "select name from bsd_icsys where id='" + type[1] + "'");
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		this.add(new JLabel("体系类型:" + name), BorderLayout.NORTH); //
		this.add(mainPanel, BorderLayout.CENTER); //
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
		String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("各功能中流程文件的模板", "风险地图浏览", "CMP_RISK_CODE3");//【李春娟/2012-07-18】
		BillCardPanel cardPanel = new BillCardPanel(templetcode); //
		cardPanel.queryDataByCondition("id=" + billVO.getStringValue("risk_id"));
		cardPanel.execEditFormula("finalost");
		cardPanel.execEditFormula("cmplost");
		cardPanel.execEditFormula("honorlost");
		BillCardDialog carddialog = new BillCardDialog(billlist_risk, "风险点[" + billVO.getStringValue("risk_name") + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		cardPanel.setEditable("serious", false);
		carddialog.setVisible(true); //	
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillVO billvo = _event.getBillListPanel().getSelectedBillVO();
		if (billvo == null) {
			return;
		}
		if ("wfprocess_name".equals(_event.getItemkey())) {
			WFGraphEditItemDialog itemdialog = new WFGraphEditItemDialog(this, "查看流程[" + billvo.getStringValue("wfprocess_name") + "]", billvo.getStringValue("wfprocess_id"), false, true);
			itemdialog.setVisible(true);
		} else {
			CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "查看文件和流程", billvo.getStringValue("cmpfile_id"));
			dialog.setShowprocessid(billvo.getStringValue("wfprocess_id"));
			dialog.setVisible(true);
		}
	}
}
