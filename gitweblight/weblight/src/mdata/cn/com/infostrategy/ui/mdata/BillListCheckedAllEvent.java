package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

/**
 * ���¼���ҪΪ��ѡ��ģʽʱ�����ͷ�Ĺ�ѡ����¼�
 * @author yuanjiangxiao
 * 20131029
 */
public class BillListCheckedAllEvent extends EventObject {

	public BillListCheckedAllEvent(Object source) {
		super(source);
	}

	public Object getSource() {
		return (BillListPanel) super.getSource();
	}

	public BillListPanel getBillListPanel() {
		return (BillListPanel) super.getSource();
	}
}
