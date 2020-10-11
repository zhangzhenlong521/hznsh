package cn.com.pushworld.salary.ui;

import javax.swing.JComponent;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.sysapp.login.IndexPanel;
import cn.com.infostrategy.ui.sysapp.login2.I_DeskTopPanelBtnStyleActionIfc;

public class HomePageChatPanel implements I_DeskTopPanelBtnStyleActionIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JComponent afterClickComponent(HashVO configVO) {
		IndexPanel indexpanel = new IndexPanel(DeskTopPanel.getDeskTopPanel());
		return indexpanel;
	}

}
