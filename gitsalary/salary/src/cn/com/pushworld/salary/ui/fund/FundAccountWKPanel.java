package cn.com.pushworld.salary.ui.fund;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 台帐基金页面
 * @author haoming
 * create by 2013-10-15
 */
public class FundAccountWKPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = -8858312318036811913L;
	boolean isadmin = false;
	private String loginUserid = ClientEnvironment.getInstance().getLoginUserID();

	public void initialize() {
		if ("admin".equals(ClientEnvironment.getInstance().getLoginUserCode())) {
			isadmin = true;
		}
		try {
			String role[] = UIUtil.getStringArrayFirstColByDS(null, "select rolecode from v_pub_user_role_1 where userid = '" + loginUserid + "' and (rolecode='高级管理员') ");
			if (role.length > 0) {
				isadmin = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.add(getDelayListPanel());
	}

	private WLTButton delay_abolish, delay_update;
	BillListPanel delayListPanel = new BillListPanel("SAL_PERSON_FUND_ACCOUNT_CODE1");

	public JPanel getDelayListPanel() {
		delay_abolish = new WLTButton("删除");
		delay_abolish.addActionListener(this);
		delay_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		if (isadmin) {
			delayListPanel.addBatchBillListButton(new WLTButton[] { delay_abolish, delay_update });
		}
		delayListPanel.repaintBillListButton();
		return delayListPanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == delay_abolish) {
			onDelayAbolish();
		}
	}

	/*
	 * 延期支付废除。
	 */
	private void onDelayAbolish() {
		BillVO vos[] = delayListPanel.getSelectedBillVOs();
		if (vos.length == 0) {
			MessageBox.show(delayListPanel, "请至少选择一条记录.");
			return;
		}
		String value = JOptionPane.showInputDialog(delayListPanel, "请填写要删除的原因", "提示", JOptionPane.WARNING_MESSAGE);
		if (value == null || value.equals("")) {
			return;
		}
		List sqlList = new ArrayList();
		StringBuffer descrsb = new StringBuffer();
		descrsb.append("此记录被[" + ClientEnvironment.getInstance().getLoginUserName() + "]于[" + UIUtil.getCurrTime() + "]删除,原因:" + value + "\r\n");
		for (int i = 0; i < vos.length; i++) {
			UpdateSQLBuilder sql = new UpdateSQLBuilder("SAL_PERSON_FUND_ACCOUNT");
			sql.putFieldValue("isdeleted", "Y");
			sql.putFieldValue("descr", descrsb.toString() + "" + vos[i].getStringValue("descr"));
			sql.setWhereCondition("id = " + vos[i].getStringValue("id"));
			sqlList.add(sql);
		}
		try {
			UIUtil.executeBatchByDS(null, sqlList);
			delayListPanel.removeSelectedRows();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
