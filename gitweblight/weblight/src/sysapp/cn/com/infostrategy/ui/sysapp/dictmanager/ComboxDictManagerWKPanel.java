package cn.com.infostrategy.ui.sysapp.dictmanager;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

public class ComboxDictManagerWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		/***
		 * 增加下拉框的类型过滤 Gwang 2013-06-22
		 * 用户只配置本模块的下拉框, 如果要新增还是要去平台配置中操作
		 * 通过菜单参数"数据类型"指定, 按Like方式查询
		 */
		String comboxType = this.getMenuConfMapValueAsStr("数据类型", "");
		ComboxDictManagerPanelNew contentPanel = null;
		if ("".equals(comboxType)) {
			contentPanel = new ComboxDictManagerPanelNew(true);
		}else {
			contentPanel = new ComboxDictManagerPanelNew(comboxType,false);
		}
		this.add(contentPanel); //
	}

}
