package cn.com.pushworld.salary.ui.operateplan.p20;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

/**
 * 年度经营计划统计之年度计划完成率【李春娟/2014-01-08】
 * @author lcj
 */
public class OperatePlanReportWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = -8277964157166966951L;
	private BillListPanel listPanel;
	private SalaryServiceIfc salaryService;

	public void initialize() {
		listPanel = new BillListPanel("REPORTQUERY_LCJ_Q01");
		try {
			String[][] plans = UIUtil.getStringArrayByDS(null, "select id,name from SAL_YEAR_OPERATEPLAN where curryear in(select max(curryear) from SAL_YEAR_OPERATEPLAN) order by id");
			if (plans != null && plans.length > 0) {
				QueryCPanel_UIRefPanel refpanel = (QueryCPanel_UIRefPanel) listPanel.getQuickQueryPanel().getCompentByKey("planid");
				refpanel.setObject(new RefItemVO(plans[0][0], "", plans[0][1]));
			}
			QueryCPanel_UIRefPanel refpanel2 = (QueryCPanel_UIRefPanel) listPanel.getQuickQueryPanel().getCompentByKey("months");
			refpanel2.setObject(new RefItemVO("1-12", "", "从1月至12月"));
			listPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == listPanel.getQuickQueryPanel()) {
			onQuery();
		}
	}

	private void onQuery() {
		if (listPanel.getQuickQueryPanel().checkValidate()) {
			String planid = listPanel.getQuickQueryPanel().getRealValueAt("planid");
			String months = listPanel.getQuickQueryPanel().getRealValueAt("months");
			try {
				HashVO[] hashvos = getService().getOperatePlanFullfillRate(planid, months);
				listPanel.clearTable();
				if (hashvos != null && hashvos.length > 0) {
					String[] keys = hashvos[0].getKeys();
					for (int i = 0; i < hashvos.length; i++) {
						int row = listPanel.newRow(false, false);

						for (int j = 0; j < keys.length; j++) {
							if ("formulatype".equalsIgnoreCase(keys[j]) || "unitvalue".equalsIgnoreCase(keys[j])) {
								String unitvalue = hashvos[i].getStringValue(keys[j]);
								listPanel.setValueAt(new ComBoxItemVO(unitvalue, "", unitvalue) ,row,keys[j]);
							} else if ("targetids".equalsIgnoreCase(keys[j])) {
								String targetids = hashvos[i].getStringValue("targetids");
								listPanel.setValueAt(new   RefItemVO(targetids, "", targetids) ,row,keys[j]);
							} else {
								listPanel.setValueAt(new StringItemVO(hashvos[i].getStringValue(keys[j])) ,row,keys[j]);
							}
						}
						listPanel.setValueAt(new RefItemVO(hashvos[i].getStringValue("planvalue"),hashvos[i].getStringValue("fullfillrate"),hashvos[i].getStringValue("realvalue")) ,row,"tmpstatic");
					}
					listPanel.repaint();
				}
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
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
}
