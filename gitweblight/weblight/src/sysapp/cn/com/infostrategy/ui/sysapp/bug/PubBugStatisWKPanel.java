package cn.com.infostrategy.ui.sysapp.bug;

import javax.swing.JLabel;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * Bug问题统计! 兴业项目开发中做的一个简单的bug管理功能,使用过程还是觉得很方便的!!
 * 因为以后各个项目中都会遇到Bug管理的问题,使用第三方的Bug管理系统还是不够方便! 所以干脆自己做一个简单适用的!!! 
 * FORMATFORMULA=getMultiReport("BUG_REPORT_SHOW","cn.com.cib.bs.common.BugAnalyseReport")>
 * @author xch
 *
 */
public class PubBugStatisWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		this.add(new JLabel("PubBugStatisWKPanel")); //

	}

}
