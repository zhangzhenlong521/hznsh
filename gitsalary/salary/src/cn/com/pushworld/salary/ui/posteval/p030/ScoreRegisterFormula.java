package cn.com.pushworld.salary.ui.posteval.p030;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC;

public class ScoreRegisterFormula implements IClassJepFormulaParseIFC {

	/**
	 * 违规积分登记的扣分详情引用子表，先前配置的加载公式如下，后来王雨提出最好把分数也显示出来，故写此类【李春娟/2013-06-06】
	 *setRefItemName("USERIDS",getMultiRefName("pub_user","name","id",replaceall(getSQLValue("数组","select userid from score_user where id in("+replaceallandsubString(getItemValue("USERIDS"),"\;",",")+")","null"),",","\;")))
	 */

	public String getForMulaValue(String[] _pars) throws Exception {
		if (_pars == null || _pars.length == 0 || _pars[0] == null || _pars[0].trim().equals("")) {
			return "";
		}
		if (_pars[0].startsWith(";")) {
			_pars[0] = _pars[0].substring(1);
		}
		if (_pars[0].endsWith(";")) {
			_pars[0] = _pars[0].substring(0, _pars[0].length() - 1);
		}

		TBUtil tbUtil = new TBUtil();
		String ids = tbUtil.replaceAll(_pars[0], ";", ",");
		if (ids.trim().equals("")) {
			return "";
		}
		String[][] names = new CommDMO().getStringArrayByDS(null, "select t2.name,t1.score  from score_user t1 left join pub_user t2 on t1.userid=t2.id where t1.id in(" + ids + ") order by t1.id");//不同数据库拼接方法不同，故先查出来再代码拼接【李春娟/2013-09-24】
		if (names.length == 0) {
			return "";
		}
		StringBuffer sb_names = new StringBuffer();
		for (int i = 0; i < names.length; i++) {
			sb_names.append(names[i][0]);
			sb_names.append("-");
			sb_names.append(names[i][1]);
			sb_names.append("分");
			sb_names.append(";");
		}
		return sb_names.substring(0, sb_names.length() - 1);
	}

}
