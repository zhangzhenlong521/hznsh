package cn.com.pushworld.salary.ui.personalcenter;

import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02;

/**
 * 个人业务量查询配置界面。
 * @author haoming
 * create by 2014-8-9
 */
public class SelfPortfolioConfigWKPanel extends AbstractStyleWorkPanel_02 {
	private static final long serialVersionUID = 926376541791056024L;
	private BillListPanel listpanel = null;
	private WLTButton btn_add, btn_update, btn_del;

	@Override
	public String getTempletcode() {
		return "SAL_SELFPORTFOLIO_CONFIG_CODE1";
	}

}
