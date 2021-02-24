package cn.com.infostrategy.ui.workflow.msg;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;

public interface MsgReadExtFunctionIFC {
	/**
	 * @param msgvo为消息VO
	 * @param parent MyMsgCenterWFPanel
	 * @param c 是那个页签的查看
	 */
	public void read(BillVO msgvo, JPanel parent, int c);
}
