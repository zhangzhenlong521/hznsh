package cn.com.infostrategy.bs.workflow.msg;

import cn.com.infostrategy.to.mdata.BillVO;

public interface MsgSendExtFunctionIFC {
/**
 * 注意系统只能接收functiontype为sysmsg的消息
 * 因为有些时候发消息，比如短信，消息要在我的已发
 * 却不通过系统给用户
 * @param vo
 */
 public void send(BillVO vo) ;
}
