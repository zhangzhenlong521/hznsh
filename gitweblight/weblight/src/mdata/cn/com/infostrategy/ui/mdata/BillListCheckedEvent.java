package cn.com.infostrategy.ui.mdata;

import java.util.EventObject;

import cn.com.infostrategy.to.mdata.BillVO;

/**
 * ���¼���ҪΪ��ѡ��ģʽʱ���ÿһ�еĹ�ѡ��ļ���
 * @author yuanjiangxiao
 * 20131029
 */

public class BillListCheckedEvent extends EventObject {
	private int checkedRow = -1;
	private BillVO currVO = null;

	public BillListCheckedEvent(Object source) {
		super(source);
	}

	public BillListCheckedEvent(int _row, BillVO _currVO, Object source) {
		super(source);
		this.checkedRow = _row;//��Ȼѡ�еĽ��
		this.currVO = _currVO;//��ǰ����
	}

	public int getCheckedRow() {
		return checkedRow;
	}

	public BillVO getCurrCheckedVO() {
		return currVO;
	}

	public Object getSource() {
		return (BillListPanel) super.getSource();
	}

	public BillListPanel getBillListPanel() {
		return (BillListPanel) super.getSource();
	}
}
