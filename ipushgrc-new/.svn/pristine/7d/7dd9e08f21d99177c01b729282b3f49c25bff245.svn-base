package com.pushworld.ipushgrc.ui.score.p070;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.score.ScoreUIUtil;

/**
 * 违规积分-》部门积分查询【李春娟/2013-05-21】
 * @author lcj
 *
 */
public class QueryScoreByDeptWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_show, btn_showscore;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("SCORE_USER_LCJ_Q03");
		btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		btn_show.setText("浏览");
		btn_show.setToolTipText("查看详细信息");
		btn_showscore = new WLTButton("查看总积分", "refsearch.gif");
		btn_showscore.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_show, btn_showscore });
		listPanel.repaintBillListButton();
		String curryear = TBUtil.getTBUtil().getCurrDate().substring(0, 4);
		listPanel.QueryDataByCondition("EFFECTDATE like '" + curryear + "%'");//查询本年度的记录
		this.setLayout(new BorderLayout());
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_showscore) {
			onShowScore();
		}
	}

	/**
	 * 查看积分
	 */
	private void onShowScore() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {//如果未选择，则显示该部门的积分，否则显示选择的违规人的积分
			String deptid = ClientEnvironment.getInstance().getLoginUserDeptId();
			String deptname = ClientEnvironment.getInstance().getLoginUserDeptName();
			new ScoreUIUtil().showDeptScore(this, null, deptid, deptname);
		} else {
			new ScoreUIUtil().showOneUserScore(this, null, null, billVO.getStringValue("userid"), billVO.getStringViewValue("userid"));
		}

	}
}
