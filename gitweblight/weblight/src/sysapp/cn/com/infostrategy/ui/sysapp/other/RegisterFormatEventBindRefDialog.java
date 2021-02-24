package cn.com.infostrategy.ui.sysapp.other;

import java.awt.Container;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 事件定义参照,在风格模板维护页面上的控件-"事件定义"中用到
 * @author xch
 *
 */
public class RegisterFormatEventBindRefDialog extends RegisterFormatPanelRefDialog {
	private static final long serialVersionUID = 1L;

	public RegisterFormatEventBindRefDialog(Container _parent, String _title, RefItemVO value, BillPanel panel) {
		super(_parent, _title, value, panel);
	}

	public void initialize() {
		super.initialize(); //
		contentpanel.getTabPanel().removeTabAt(0);
		contentpanel.addEventBindTab(); //
	}

}
