package cn.com.infostrategy.bs.sysapp.login;

import java.util.ArrayList;

/**
 * 首页提醒数据接口 【杨科/2013-06-05】
 */

public interface RemindIfc {
    
	//返回首页提醒数据 ArrayList存放的是提醒文字字符串
	public ArrayList getRemindDatas(String _loginUserId) throws Exception;
	
}
