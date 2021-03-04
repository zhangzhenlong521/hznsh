package cn.com.pushworld.salary.ui.target.p040;

import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.ChildTableCommUIIntercept;

/**
 * 评分修改流程中导入子表拦截器。
 * 
 * @author haoming create by 2013-7-11
 */
public class ScoreModifyProcessChildTableImpl implements ChildTableCommUIIntercept {

	public void afterInitialize(BillPanel panel) throws Exception {
		if (panel instanceof BillListPanel) {
			BillListPanel scoreItems = (BillListPanel) panel;
			scoreItems.getBillListBtnPanel().getPanel_flow().setVisible(false);
			scoreItems.setCanShowCardInfo(false); // 不允许双击卡片显示
		}
	}

}
