package cn.com.infostrategy.bs.report.style1;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

/**
 * 风格报表1的构造数据的接口
 * @author xch
 *
 */
public interface StyleReport_1_BuildDataIFC {

	/**
	 * 构造数据
	 * @param _condition  查询面板中送入的查询条件!!!
	 * @param _loginUserVO 登录人员的相关信息
	 * @return
	 * @throws Exception
	 */
	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO _loginUserVO) throws Exception; //

}
