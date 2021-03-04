package cn.com.pushworld.salary.ui.job;

import javax.swing.JComponent;

import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.sysapp.login2.I_WLTTabbedPane;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;

public class LoginCheckJobs implements WLTJobIFC {

	public String run() throws Exception {
		markDesktopPaneBom();
		return null;
	}

	private void markDesktopPaneBom() {
//		I_WLTTabbedPane tabedpanel = DeskTopPanel.getDeskTopPanel().getNewDeskTopTabPane();
//		if (tabedpanel != null) {
//			JComponent com = tabedpanel.getComponentAt(0);
//			if (!(com instanceof BillBomPanel)) {
//				if (com.getComponent(0) instanceof BillBomPanel) {
//					BillBomPanel bompanel = (BillBomPanel) com.getComponent(0);
//					if (bompanel != null) {
//						RiskVO risk = new RiskVO(0, 5, 0);
//						risk.setShape2(1);
//						bompanel.setRiskVO("基础信息梳理", risk);
//					}
//
//				}
//			}
//		}
	}
}
