package cn.com.infostrategy.ui.workflow.msg;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;

public interface MsgReadExtFunctionIFC {
	/**
	 * @param msgvoΪ��ϢVO
	 * @param parent MyMsgCenterWFPanel
	 * @param c ���Ǹ�ҳǩ�Ĳ鿴
	 */
	public void read(BillVO msgvo, JPanel parent, int c);
}
