package com.pushworld.ipushgrc.ui.login.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.report.BillReportDrillActionIfc;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * 部门登录统计
 * @author YangQing/2013-11-27
 *
 */
public class DeptLoginStatisWKPanel extends AbstractWorkPanel implements BillReportDrillActionIfc {
	public void initialize() {
		BillReportPanel panel = new BillReportPanel("DEPTLOGINREPORT_YQ_Q01", "com.pushworld.ipushgrc.bs.login.p010.DeptLoginReportBuilderAdapter");
		RefItemVO returnRefItemVO = new RefItemVO();
		String today = UIUtil.getCurrDate();
		returnRefItemVO.setId(today); //
		returnRefItemVO.setName(today);
		panel.getBillQueryPanel().setCompentObjectValue("date", returnRefItemVO);//日期框赋默认值
		this.add(panel);
	}

	/**
	 * 钻取显示卡片窗口
	 * */
	public void drillAction(String id, Object _itemvo, Container _parent,HashMap map) {
		final BillDialog dialog = new BillDialog(_parent, "部门登录统计详情");
		DeptLoginInfoWKPanel deptpanel = new DeptLoginInfoWKPanel(id);
		deptpanel.initialize();
		JPanel panel = new JPanel(new FlowLayout());
		WLTButton btn_close = new WLTButton("关闭", "del.gif");
		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.closeMe();
				dialog.dispose();
			}
		});
		panel.add(btn_close);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(deptpanel, BorderLayout.CENTER);
		dialog.setSize(1000, 700);
		dialog.locationToCenterPosition();
		dialog.getContentPane().add(panel, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}
}
