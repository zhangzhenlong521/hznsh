package cn.com.infostrategy.ui.sysapp.dictmanager;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

public class ComboxDictManagerWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		/***
		 * ��������������͹��� Gwang 2013-06-22
		 * �û�ֻ���ñ�ģ���������, ���Ҫ��������Ҫȥƽ̨�����в���
		 * ͨ���˵�����"��������"ָ��, ��Like��ʽ��ѯ
		 */
		String comboxType = this.getMenuConfMapValueAsStr("��������", "");
		ComboxDictManagerPanelNew contentPanel = null;
		if ("".equals(comboxType)) {
			contentPanel = new ComboxDictManagerPanelNew(true);
		}else {
			contentPanel = new ComboxDictManagerPanelNew(comboxType,false);
		}
		this.add(contentPanel); //
	}

}
