package cn.com.infostrategy.bs.workflow.msg;

import cn.com.infostrategy.to.mdata.BillVO;

public interface MsgSendExtFunctionIFC {
/**
 * ע��ϵͳֻ�ܽ���functiontypeΪsysmsg����Ϣ
 * ��Ϊ��Щʱ����Ϣ��������ţ���ϢҪ���ҵ��ѷ�
 * ȴ��ͨ��ϵͳ���û�
 * @param vo
 */
 public void send(BillVO vo) ;
}
