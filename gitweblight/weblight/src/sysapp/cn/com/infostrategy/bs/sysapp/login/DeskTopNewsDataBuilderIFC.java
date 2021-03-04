package cn.com.infostrategy.bs.sysapp.login;

import cn.com.infostrategy.to.common.HashVO;

/**
 * 首页新闻数据生成对象..它在服务器端实现
 * 就是首页上有一个个框组,每个组生成一组数据..
 * @author xch
 *
 */
public interface DeskTopNewsDataBuilderIFC {

	/**
	 * 生成数据,根据登录人员,不同的模块去实现不同的模块..
	 * @param _loginUserCode
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getNewData(String _loginUserCode) throws Exception;

}

