package com.pushworld.ipushgrc.ui.risk.p070;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.BillCellPanel;

import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFGraphEditItemDialog;

/**
 * 风险矩阵!!
 * @author xch
 *
 */
public class RiskMatrixWKPanel extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener, ClickRiskNumListener {

	private BillQueryPanel billQuery = null; //风险矩阵查询面板
	private BillCellPanel billCell_matrix = null; //风险矩阵面板
	private RiskDistributePanel riskDistributePanel = new RiskDistributePanel(); //风险分布面板
	private StringBuffer sqlCondition = new StringBuffer();//风险矩阵查询sql条件
	private String cmpfile_id;//当前流程文件id
	private BillListPanel listpanel_risk;
	private WLTButton btn_lookrisk;
	private String initCondition;
	private String templetcode_risk = "V_RISK_PROCESS_FILE_CODE2";

	public String getTempletcode_risk() {
		return templetcode_risk;
	}

	public void setTempletcode_risk(String templetcode_risk) {
		this.templetcode_risk = templetcode_risk;
	}

	public String getInitCondition() {
		return initCondition;
	}

	public void setInitCondition(String initCondition) {
		this.initCondition = initCondition;
	}

	@Override
	public void initialize() {
		billQuery = new BillQueryPanel("MATRIX_QUERY"); //查询面板!!
		billQuery.addBillQuickActionListener(this);//查询面板添加查询事件
		billCell_matrix = new BillCellPanel("riskmetrix", true, false, true); //风险矩阵图!
		billCell_matrix.setAllowShowPopMenu(false);//屏蔽右键弹出菜单
		billCell_matrix.addBillCellHtmlHrefListener(this);//风险矩阵添加链接事件
		JTabbedPane tab = new JTabbedPane(); //
		tab.add("矩阵图", billCell_matrix);
		tab.add("分布图", riskDistributePanel);
		riskDistributePanel.addClickRiskNumListener(this);
		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout(5, 5)); //
		contentPanel.add(billQuery, BorderLayout.NORTH); //
		contentPanel.add(tab, BorderLayout.CENTER); //
		this.setLayout(new BorderLayout());
		this.add(contentPanel, BorderLayout.CENTER); //
	}

	/**
	 * 
	 * @param _key
	 * @param is_matrix1_query true则是矩阵图查询，false代表分布图查询
	 */
	public void onRisk(final String _key, boolean is_matrix1_query) {
		if (_key == null) {
			return;
		}
		if (is_matrix1_query) {
			BillCellItemVO itemvo = billCell_matrix.getBillCellItemVOAt(_key);
			if (!"Y".equals(itemvo.getIshtmlhref())) {
				return;
			}
		}
		try {
			new SplashWindow(billCell_matrix, "正在查找相关风险点,请稍等...", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					String keys[] = _key.split("_");
					BillListDialog dialog = new BillListDialog(billCell_matrix, "风险点查询",templetcode_risk, 1000, 700);
					listpanel_risk = dialog.getBilllistPanel();
					listpanel_risk.setQuickQueryPanelVisiable(false);//不显示查询面板，直接分页查看
					listpanel_risk.setAllBillListBtnVisiable(false);
					btn_lookrisk = new WLTButton("浏览");
					btn_lookrisk.addActionListener(RiskMatrixWKPanel.this);
					listpanel_risk.addBillListButton(btn_lookrisk);
					listpanel_risk.repaintBillListButton();
					listpanel_risk.setDataFilterCustCondition("possible= '" + keys[0] + "' and serious = '" + keys[1] + "' " + sqlCondition);
					listpanel_risk.QueryDataByCondition(null);
					listpanel_risk.addBillListHtmlHrefListener(new BillListHtmlHrefListener() {
						public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
							BillVO billvo = _event.getBillListPanel().getSelectedBillVO();
							if (billvo == null) {
								return;
							}
							onBillListHtml(billvo, _event.getItemkey());
						}
					});
					dialog.getBtn_confirm().setVisible(false);
					SplashWindow.window.dispose();
					dialog.setVisible(true);
				}
			});
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	private void onBillListHtml(BillVO _billvo, String _itemname) {
		if ("wfprocess_name".equals(_itemname)) {
			WFGraphEditItemDialog itemdialog = new WFGraphEditItemDialog(this, "查看流程[" + _billvo.getStringValue("wfprocess_name") + "]", _billvo.getStringValue("wfprocess_id"), false, true);
			itemdialog.setVisible(true);
		} else {
			cmpfile_id = _billvo.getStringValue("cmpfile_id");
			CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "查看文件和流程", cmpfile_id);
			dialog.setShowprocessid(_billvo.getStringValue("wfprocess_id"));//指定先显示风险所在的那个流程
			dialog.setVisible(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_lookrisk) {
			onLookRisk();
		} else {
			onQuery();
		}
	}

	private void onLookRisk() {
		BillVO billVO = listpanel_risk.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_RISK_CODE3"); //
		cardPanel.queryDataByCondition("id=" + billVO.getStringValue("risk_id"));
		cardPanel.execEditFormula("finalost");
		cardPanel.execEditFormula("cmplost");
		cardPanel.execEditFormula("honorlost");
		BillCardDialog carddialog = new BillCardDialog(listpanel_risk, "风险点[" + billVO.getStringValue("risk_name") + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		carddialog.setVisible(true); //	
	}

	private void onQuery() {		
		if (!billQuery.checkValidate()) {
			return;
		}
		String bsactid = billQuery.getCompentRealValue("bsactid");
		String blcorpid = billQuery.getCompentRealValue("blcorpid");
		String filestate = billQuery.getCompentRealValue("filestate");
		StringBuffer sb_sql = new StringBuffer("select possible,serious,count(risk_id) c1 from v_risk_process_file where 1=1 ");
		TBUtil tbutil = new TBUtil();
		sqlCondition.delete(0, sqlCondition.length());
		if (initCondition != null && !"".equals(initCondition)) {
			sqlCondition.append("and " + initCondition + " ");
		}
		if (bsactid != null && !"".equals(bsactid)) {
			sqlCondition.append("and bsactid in(");
			sqlCondition.append(tbutil.getInCondition(bsactid));
			sqlCondition.append(") ");
		}
		if (blcorpid != null && !"".equals(blcorpid)) {
			sqlCondition.append("and blcorpid in(");
			sqlCondition.append(tbutil.getInCondition(blcorpid));
			sqlCondition.append(") ");
		}
		if (filestate != null && !"".equals(filestate)) {
			sqlCondition.append("and filestate in(");
			sqlCondition.append(tbutil.getInCondition(filestate));
			sqlCondition.append(") ");
		}
		sb_sql.append(sqlCondition.toString());
		sb_sql.append("group by possible,serious");
		try {
			HashVO[] hashovo = UIUtil.getHashVoArrayByDS(null, sb_sql.toString());
			int rowcount = billCell_matrix.getRowCount();
			int columncount = billCell_matrix.getColumnCount();
			for (int i = 0; i < rowcount; i++) {
				for (int j = 0; j < columncount; j++) {
					String value = billCell_matrix.getValueAt(i, j);
					if (value == null || !value.contains("(")) {
						continue;
					}
					value = value.substring(0, value.indexOf("(") - 1);
					BillCellItemVO itemvo = billCell_matrix.getBillCellItemVOAt(i, j);
					billCell_matrix.setValueAt(itemvo.getCellkey(), value);
					billCell_matrix.setCellItemIshtmlhref(i, j, false);
				}
			}
			HashMap<String, Integer> data_map = new HashMap<String, Integer>();
			for (int i = 0; i < hashovo.length; i++) {
				String map_key = hashovo[i].getStringValue("serious") + "_" + hashovo[i].getStringValue("possible"); //
				data_map.put(map_key, Integer.parseInt(hashovo[i].getStringValue("c1")));
				String cellkey = hashovo[i].getStringValue("possible") + "_" + hashovo[i].getStringValue("serious"); //
				String str_oldvalue = billCell_matrix.getValueAt(cellkey);
				String cellvalue = hashovo[i].getStringValue("c1");
				BillCellItemVO itemvo = billCell_matrix.getBillCellItemVOAt(cellkey);
				if (itemvo != null) {
					itemvo.setIshtmlhref("Y");
					billCell_matrix.setValueAt(cellkey, str_oldvalue + " (" + cellvalue + ")"); //
				}
			}
			riskDistributePanel.setData_map(data_map);
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent _event) {
		onRisk(_event.getCellItemKey(), true); //
	}

	public void onMouseClickRiskNum(String riskKey) {
		onRisk(riskKey, false);
	}

	public BillQueryPanel getBillQuery() {
		return billQuery;
	}

	public void setBillQuery(BillQueryPanel billQuery) {
		this.billQuery = billQuery;
	}
}
