package cn.com.infostrategy.ui.sysapp.registmenu;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * ע��˵�ά��!! ���Ժ�ƽ̨���׼��Ʒ������ע��˵��ķ�ʽ�ṩ!!!
 * ����ֱ�Ӵ�xml�ļ���ȡ,Ȼ������Ķ�!!Ȼ��ʵ�ʵ�ϵͳ�˵�����ֱ�����û�̳�����Щע��Ĺ��ܲ˵�!!
 * @author xch
 *
 */
public class RegistMenuWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		RegistMenuTreePanel panel = new RegistMenuTreePanel(); //
		panel.initialize(); //
		this.add(panel); //����
	}

}
