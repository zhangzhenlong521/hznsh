package cn.com.infostrategy.bs.sysapp.login;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;

public interface DeskTopNewsDataBuilderIFC2 {
	public HashVO[] getNewData(String _loginUserCode, HashMap _param) throws Exception;
}
