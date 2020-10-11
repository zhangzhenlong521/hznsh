package cn.com.infostrategy.bs.sysapp.login;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.style1.StyleReport_1_BuildDataIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

public class UserLoginCountReportBuilder implements StyleReport_1_BuildDataIFC {

	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO userVO) throws Exception {
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("select dealusername username,count(*) logincount from pub_sysdeallog where dealtype='"+WLTConstants.SYS_LOGIN+"'");
		if(_condition.get("username")!=null&&!"".equals(_condition.get("username"))){
			sb_sql.append(" and dealusername like '%"+_condition.get("username")+"%'");
		}
		if(_condition.get("betweentime")!=null&&!"".equals(_condition.get("betweentime"))){
			sb_sql.append(" and "+new ReportUtil().getWhereConditionByReplaceTYpe(_condition, "betweentime", "dealtime"));
		}
		sb_sql.append(" group by dealusername order by logincount");
		return new CommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //
	}
}
