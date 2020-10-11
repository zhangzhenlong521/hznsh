package cn.com.pushworld.salary.ui.report.p020;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.pushworld.salary.to.baseinfo.FormulaTool;

/**
 * 员工所有信息查询
 * @author haoming
 * create by 2013-10-15
 */
public class AllUserInfoWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = -529838604585902114L;
	private BillQueryPanel billQueryPanel = null;
	private BillListPanel billListPanel = null;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());
		billListPanel = new BillListPanel("SAL_PERSONINFO_CODE2");
		billListPanel.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_EXPORTEXCEL) });
		billListPanel.repaintBillListButton();

		billQueryPanel = billListPanel.getQuickQueryPanel();
		billQueryPanel.addBillQuickActionListener(this);

		this.add(billListPanel, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == billQueryPanel) {
				String age = billQueryPanel.getRealValueAt("age");
				String workage = billQueryPanel.getRealValueAt("workage");
				String name = billQueryPanel.getRealValueAt("name");
				String stationkind = billQueryPanel.getRealValueAt("stationkind");
				String politicalstatus = billQueryPanel.getRealValueAt("politicalstatus");
				String degree = billQueryPanel.getRealValueAt("degree");
				String posttitle = billQueryPanel.getRealValueAt("posttitle");

				if ((age == null || age.equals("")) && (workage == null || workage.equals(""))) {
					billListPanel.QueryData(billQueryPanel.getQuerySQL());
					return;
				}

				StringBuffer sb_sql = new StringBuffer();
				sb_sql.append("select * from v_sal_personinfo where 1=1");
				if (!(name == null || name.equals(""))) {
					sb_sql.append(" and name like '%" + name + "%'");
				}
				if (!(stationkind == null || stationkind.equals(""))) {
					sb_sql.append(" and stationkind='" + stationkind + "'");
				}
				if (!(politicalstatus == null || politicalstatus.equals(""))) {
					sb_sql.append(" and politicalstatus='" + politicalstatus + "'");
				}
				if (!(degree == null || degree.equals(""))) {
					sb_sql.append(" and degree='" + degree + "'");
				}
				if (!(posttitle == null || posttitle.equals(""))) {
					sb_sql.append(" and posttitle='" + posttitle + "'");
				}

				HashVO[] hvos = UIUtil.getHashVoArrayByDS(null, sb_sql.toString());

				FormulaTool tool = new FormulaTool();
				for (int i = 0; i < hvos.length; i++) {
					hvos[i].setAttributeValue("年龄", tool.getAgeByIdCard(hvos[i].getStringValue("cardid", "")));
					hvos[i].setAttributeValue("工龄", tool.getWorkAgeByWorkDate(hvos[i].getStringValue("joinworkdate", "")));
				}

				String ids = "";
				if (!(age == null || age.equals(""))) {
					double start = Double.parseDouble(age.split(";")[0]);
					double end = Double.parseDouble(age.split(";")[1]);
					for (int i = 0; i < hvos.length; i++) {
						double hvo_age = Double.parseDouble(hvos[i].getStringValue("年龄", "-1"));
						if (!(hvo_age >= start && hvo_age <= end)) {
							ids += "'" + hvos[i].getStringValue("id", "-9999") + "',";
						}
					}
				}

				if (!(workage == null || workage.equals(""))) {
					double start = Double.parseDouble(workage.split(";")[0]);
					double end = Double.parseDouble(workage.split(";")[1]);
					for (int i = 0; i < hvos.length; i++) {
						double hvo_workage = Double.parseDouble(hvos[i].getStringValue("工龄", "-1"));
						if (!(hvo_workage >= start && hvo_workage <= end)) {
							ids += "'" + hvos[i].getStringValue("id", "-9999") + "',";
						}
					}
				}

				sb_sql.append(" and id not in (" + ids + "'')");
				billListPanel.QueryData(sb_sql.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
