package cn.com.pushworld.salary.ui.personalcenter;

import javax.swing.JComponent;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.WLTTabbedPane;

public interface SelfPortfolioBuildIFC {
	public JComponent initialize(WLTTabbedPane tabpane, HashVO config, int index, String _currDate);

	public void onQuery(String _newDate);
}
