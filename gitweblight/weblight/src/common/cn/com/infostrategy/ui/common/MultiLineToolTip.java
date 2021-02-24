package cn.com.infostrategy.ui.common;

import javax.swing.JToolTip;

/**
 * 支持多行分页显示的ToolTip
 * @author xch
 *
 */
public class MultiLineToolTip extends JToolTip {
	public MultiLineToolTip() {
		setUI(new MultiLineToolTipUI());
	}
}
