package cn.com.infostrategy.ui.common;

import javax.swing.JToolTip;

/**
 * ֧�ֶ��з�ҳ��ʾ��ToolTip
 * @author xch
 *
 */
public class MultiLineToolTip extends JToolTip {
	public MultiLineToolTip() {
		setUI(new MultiLineToolTipUI());
	}
}
