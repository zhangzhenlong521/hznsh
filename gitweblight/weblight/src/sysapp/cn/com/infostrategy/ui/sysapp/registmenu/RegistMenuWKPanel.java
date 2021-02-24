package cn.com.infostrategy.ui.sysapp.registmenu;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * 注册菜单维护!! 即以后平台与标准产品都将以注册菜单的方式提供!!!
 * 就是直接从xml文件读取,然后可以阅读!!然后实际的系统菜单可以直接引用或继承于这些注册的功能菜单!!
 * @author xch
 *
 */
public class RegistMenuWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		RegistMenuTreePanel panel = new RegistMenuTreePanel(); //
		panel.initialize(); //
		this.add(panel); //加入
	}

}
