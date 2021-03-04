package cn.com.pushworld.salary.ui.job;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;
import cn.com.infostrategy.ui.workflow.pbom.BomItemPanel;
import cn.com.pushworld.salary.to.SalaryTBUtil;

/**
 * 绩效系统登录个人中心，bom图加载后的事件。
 * @author haoming
 * create by 2014-1-10
 */
public class LoginOpenPersonalCenterAction {
	public LoginOpenPersonalCenterAction(BillBomPanel bompanel, BomItemPanel _itemp, String[] _par) throws WLTRemoteException, Exception {
		if (bompanel != null) {
			int count = new SalaryTBUtil().getLoginUserUnCheckedScore();
			if (count > 0) {
				RiskVO risk = new RiskVO();
				risk.setInfoalert("未完成");
				_itemp.setRiskVO("考核评分", risk);
			}
		}

	}
}
