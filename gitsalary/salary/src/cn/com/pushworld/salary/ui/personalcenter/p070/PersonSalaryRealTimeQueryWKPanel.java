package cn.com.pushworld.salary.ui.personalcenter.p070;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

/**
 * 员工绩效实时查询
 * @author haoming 
 * create by 2014-7-15
 */
public class PersonSalaryRealTimeQueryWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = -1692747832354355327L;
	private BillListPanel listpanel;
	private String usercode;
	private String userid;
	private BillQueryPanel queryPanel;

	@Override
	public void initialize() {
		usercode = ClientEnvironment.getInstance().getLoginUserCode();
		userid = ClientEnvironment.getInstance().getLoginUserID();
		listpanel = new BillListPanel("SAL_PERSON_CHECK_AUTO_SCORE_CODE1");
		queryPanel = listpanel.getQuickQueryPanel();
		queryPanel.addBillQuickActionListener(this);
		this.add(listpanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (usercode.equalsIgnoreCase("admin")) {
			listpanel.QueryData(queryPanel.getQuerySQL() + " order by datadate desc");
		} else {
			listpanel.QueryData(queryPanel.getQuerySQL() + " and checkeduser ='" + userid + "' order by datadate desc");
		}
	}

}
