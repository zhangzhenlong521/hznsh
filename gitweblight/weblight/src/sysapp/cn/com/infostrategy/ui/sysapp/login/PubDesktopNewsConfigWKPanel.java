package cn.com.infostrategy.ui.sysapp.login;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ��ҳ��Ϣ���ý���
 * @author xch
 *
 */
public class PubDesktopNewsConfigWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillListPanel billList = new BillListPanel("PUB_DESKTOP_NEW_CODE1");  //
		this.add(billList);  //
	}

}
