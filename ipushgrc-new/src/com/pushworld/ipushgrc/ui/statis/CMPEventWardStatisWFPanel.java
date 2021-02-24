package com.pushworld.ipushgrc.ui.statis;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;

import com.pushworld.ipushgrc.ui.cmpevent.p110.CmpWardStatisWKPanel;
import com.pushworld.ipushgrc.ui.cmpevent.p120.CmpEventStatisWKPanel;

/***
 * 合规事件统计: 包含2个页签成功防范统计\违规事件统计
 * @author Gwang
 *
 */
public class CMPEventWardStatisWFPanel extends AbstractWorkPanel {

	private CmpWardStatisWKPanel panel_ward = null; //成功防范
	private CmpEventStatisWKPanel panel_enent = null; //违规事件
	
	@Override
	public void initialize() {
		panel_ward = new CmpWardStatisWKPanel();
		panel_ward.setLayout(new BorderLayout());
		panel_ward.initialize();		
		
		panel_enent = new CmpEventStatisWKPanel();
		panel_enent.setLayout(new BorderLayout());
		panel_enent.initialize();
		
		WLTTabbedPane tabbedPane = new WLTTabbedPane();
		tabbedPane.addTab("成功防范统计", panel_ward);
		tabbedPane.addTab("违规事件统计", panel_enent);

		this.add(tabbedPane);
	}
	
}
