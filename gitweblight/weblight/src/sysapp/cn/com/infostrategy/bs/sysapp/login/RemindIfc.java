package cn.com.infostrategy.bs.sysapp.login;

import java.util.ArrayList;

/**
 * ��ҳ�������ݽӿ� �����/2013-06-05��
 */

public interface RemindIfc {
    
	//������ҳ�������� ArrayList��ŵ������������ַ���
	public ArrayList getRemindDatas(String _loginUserId) throws Exception;
	
}
