package com.pushworld.ipushgrc.ui.wfrisk.p040;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;

import com.pushworld.ipushgrc.ui.wfrisk.p010.WFAndRiskEditWKPanel;

/**
 * 流程与风险查询! 核心界面之一!
 * A.有四个页签,分别是[扁平查看][体系视图][业务视图][机构视图],即能从不同角度查看!
 * B.上面的按钮有【浏览】【流程查看】【历史版本】【总浏览】
 * C.【总浏览】就是Html一眼清查看
 * @author xch
 *
 */
public class WFAndRiskQueryWKPanel extends AbstractWorkPanel implements ChangeListener {

	private WLTTabbedPane tab;
	private boolean ifclick2 = false;
	private boolean ifclick3 = false;
	private boolean ifclick4 = false;
	private JPanel jPanel2;
	private JPanel jPanel3;
	private JPanel jPanel4;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());
		tab = new WLTTabbedPane();
		jPanel2 = new JPanel();
		jPanel3 = new JPanel();
		jPanel4 = new JPanel();
		WFAndRiskEditWKPanel panel_wfAndRisk = new WFAndRiskEditWKPanel();
		panel_wfAndRisk.setEditable(false);
		panel_wfAndRisk.initialize();
		panel_wfAndRisk.getBillList_cmpfile().setItemVisible("filestate", true);
		tab.addTab("扁平查看", UIUtil.getImage("office_070.gif"), panel_wfAndRisk);
		tab.addTab("体系视图", UIUtil.getImage("office_074.gif"), jPanel2);
		tab.addTab("业务视图", UIUtil.getImage("office_074.gif"), jPanel3);
		tab.addTab("机构视图", UIUtil.getImage("office_074.gif"), jPanel4);
		tab.addChangeListener(this);
		this.add(tab, BorderLayout.CENTER); //
	}

	public void stateChanged(ChangeEvent e) {
		if (tab.getSelectedIndex() == 1 && !ifclick2) {
			jPanel2.setLayout(new BorderLayout());
			jPanel2.add(new WFAndRiskQueryByIcsysWKPanel(false));
			ifclick2 = true;
		} else if (tab.getSelectedIndex() == 2 && !ifclick3) {
			jPanel3.setLayout(new BorderLayout());
			jPanel3.add(new WFAndRiskQueryByBsactWKPanel(false));
			ifclick3 = true;
		} else if (tab.getSelectedIndex() == 3 && !ifclick4) {
			jPanel4.setLayout(new BorderLayout());
			jPanel4.add(new WFAndRiskQueryByDeptWKPanel(false));
			ifclick4 = true;
		}
	}
}
