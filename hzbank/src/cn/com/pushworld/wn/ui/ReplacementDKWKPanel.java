package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * @author zzl
 * 
 *         2019-6-19-����10:08:10 �û�����¼��
 */
public class ReplacementDKWKPanel extends AbstractWorkPanel {
	BillListPanel list = null;

	@Override
	public void initialize() {
		list = new BillListPanel("WN_ZHDK_IMP_CODE1");
		this.add(list);

	}

}
