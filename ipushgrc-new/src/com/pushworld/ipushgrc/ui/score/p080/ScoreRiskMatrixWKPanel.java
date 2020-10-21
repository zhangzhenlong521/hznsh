package com.pushworld.ipushgrc.ui.score.p080;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.BillCellPanel;

import com.pushworld.ipushgrc.ui.risk.p070.ClickRiskNumListener;
import com.pushworld.ipushgrc.ui.risk.p070.RiskDistributePanel;

/**
 * ����Υ����ֵǼǼ�¼�ķ��վ������/2016-05-18��
 * @author lcj
 *
 */
public class ScoreRiskMatrixWKPanel extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener, ClickRiskNumListener {

	private BillQueryPanel billQuery = null; //���վ����ѯ���
	private BillCellPanel billCell_matrix = null; //���վ������
	private RiskDistributePanel riskDistributePanel = new RiskDistributePanel(); //���շֲ����
	private String sqlCondition = null;
	private BillListPanel listpanel_risk;
	private String initCondition;
	private String templetcode_risk = "SCORE_REGISTER_LCJ_E01";

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
		billQuery = new BillQueryPanel("SCORE_REGISTER_LCJ_E01"); //��ѯ���!!
		billQuery.addBillQuickActionListener(this);//��ѯ�����Ӳ�ѯ�¼�
		billCell_matrix = new BillCellPanel("riskmetrix", true, false, true); //���վ���ͼ!
		billCell_matrix.setAllowShowPopMenu(false);//�����Ҽ������˵�
		billCell_matrix.addBillCellHtmlHrefListener(this);//���վ�����������¼�
		JTabbedPane tab = new JTabbedPane(); //
		tab.add("����ͼ", billCell_matrix);
		tab.add("�ֲ�ͼ", riskDistributePanel);
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
	 * @param is_matrix1_query true���Ǿ���ͼ��ѯ��false����ֲ�ͼ��ѯ
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
			new SplashWindow(billCell_matrix, "���ڲ�����ؼ�¼,���Ե�...", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					String keys[] = _key.split("_");
					BillListDialog dialog = new BillListDialog(billCell_matrix, "��ѯ",templetcode_risk, 1000, 700);
					listpanel_risk = dialog.getBilllistPanel();
					listpanel_risk.setQuickQueryPanelVisiable(false);//����ʾ��ѯ��壬ֱ�ӷ�ҳ�鿴
					listpanel_risk.setAllBillListBtnVisiable(false);
					listpanel_risk.repaintBillListButton();
					listpanel_risk.setDataFilterCustCondition("possible= '" + keys[0] + "' and serious = '" + keys[1] + "' " + sqlCondition);
					listpanel_risk.QueryDataByCondition(null);
					dialog.getBtn_confirm().setVisible(false);
					SplashWindow.window.dispose();
					dialog.setVisible(true);
				}
			});
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}
 

	public void actionPerformed(ActionEvent e) {
			onQuery();
	}
 

	private void onQuery() {		
		if (!billQuery.checkValidate()) {
			return;
		}
		StringBuffer sb_sql = new StringBuffer("select possible,serious,count(id) c1 from SCORE_REGISTER where 1=1 ");
		sqlCondition= billQuery.getQuerySQLCondition();
		sb_sql.append(sqlCondition);
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
