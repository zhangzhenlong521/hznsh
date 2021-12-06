package com.pushworld.icheck.ui.p040;

import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * 三级目录维护,编码帮助提示
 * @author shaochunyun
 *
 */
public class WLT_Help3 implements WLTActionListener {

	/**
	 * 编码帮助说明
	 */

	private BillCardPanel card_panle;

	public void actionPerformed(WLTActionEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		card_panle = (BillCardPanel) arg0.getBillPanelFrom();

		String str = UIUtil.getStringValueByDS(null, "select descr from PUB_COMBOBOXDICT where 1=1  and  code='检查内容编码'  ");
		if (str == null || str.equals("")) {
			str = "没有维护!";
		}
		// 按钮帮助说明
		MessageBox.showTextArea(card_panle, str);
	}

}
