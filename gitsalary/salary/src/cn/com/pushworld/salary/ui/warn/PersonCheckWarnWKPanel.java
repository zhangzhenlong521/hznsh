package cn.com.pushworld.salary.ui.warn;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.BillReportDrillActionIfc;
import cn.com.infostrategy.ui.report.BillReportPanel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;
import cn.com.pushworld.salary.ui.person.p020.PersonMutualWKPanel;
import cn.com.pushworld.salary.ui.target.p040.PersonAndDeptCheckWKPanel;

public class PersonCheckWarnWKPanel extends AbstractWorkPanel implements BillReportDrillActionIfc {

	private static final long serialVersionUID = -4541100290687904473L;

	@Override
	public void initialize() {
		BillReportPanel reportPanel = new BillReportPanel("SAL_TARGET_CHECK_LOG_CODE1_REPORT_WARN", "cn.com.pushworld.salary.bs.report.CheckWarnCommDMO");
		String checkDate = new SalaryUIUtil().getCheckDate();
		reportPanel.getBillQueryPanel().setCompentObjectValue("checkdate", new RefItemVO(checkDate, "", checkDate));
		this.add(reportPanel);
	}

	/*
	 * 
	 * 
	 */
	public void drillAction(String ids, Object itemVO, Container parent,HashMap<String, String> query) {
		if (itemVO != null) {
			BillCellItemVO cellitemvo = (BillCellItemVO) itemVO;
			HashMap<String, String> custMa = cellitemvo.getCustPropMap();
			try {
				if (custMa != null) {
					String colname = custMa.get("ColHeaderLevelNameValue");
					String rowname = custMa.get("RowHeaderLevelNameValue");
					String user = null;
					String target = null;
					if ((colname != null && colname.contains("打分人"))) {
						user = colname;
					} else if ((rowname != null && rowname.contains("打分人"))) {
						user = rowname;
					}

					if ((colname != null && colname.contains("指标名称"))) {
						target = colname;
					} else if ((rowname != null && rowname.contains("指标名称"))) {
						target = rowname;
					}
					if (user != null) {
						BillCellPanel cellpanel = (BillCellPanel) parent;
						BillReportPanel reportpanel = (BillReportPanel) cellpanel.getParent().getParent().getParent().getParent();
						String checkdate = String.valueOf(reportpanel.getBillQueryPanel().getValueAt("checkdate"));
						String checktype = String.valueOf(reportpanel.getBillQueryPanel().getValueAt("checktype"));
						String username = user.substring(user.indexOf("打分人") + 4, user.length() - 1);
						String userid = UIUtil.getStringValueByDS(null, "select id from pub_user where name = '" + username + "'");
						if (userid == null) {
							return;
						}
						HashVO logvo[] = UIUtil.getHashVoArrayByDS(null, "select * from sal_target_check_log where checkdate='" + checkdate + "'");
						if (logvo.length > 0) {
							BillDialog dialog = new BillDialog(parent, username + "打分详情", 1000, 800);
							PersonAndDeptCheckWKPanel p = new PersonAndDeptCheckWKPanel();
							p.setLayout(new BorderLayout());
							p.custinit(false, userid, logvo[0], checktype, false);
							if (target != null) {
								PersonMutualWKPanel personmwkpanel = p.getPersonMutualWKPanel();
								if (personmwkpanel != null) {
									if (target != null) {
										String targetname = target.substring(target.indexOf("指标名称") + 5, target.length() - 1);
										String color = cellitemvo.getBackground();
										if (TBUtil.isEmpty(color)) {
											color = "20,240,50";
										}
										personmwkpanel.markTargetScoresByName(checktype, new String[] { targetname }, color);
									}
								}
							}
							dialog.getContentPane().add(p);
							dialog.setVisible(true);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
