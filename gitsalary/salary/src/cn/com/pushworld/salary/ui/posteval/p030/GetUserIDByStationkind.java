package cn.com.pushworld.salary.ui.posteval.p030;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 岗位价值评估选择岗位归类，显示该归类的评分人【李春娟/2013-10-24】
 *
 */
public class GetUserIDByStationkind implements IClassJepFormulaParseIFC {

	public String getForMulaValue(String[] pars) throws Exception {
		String stationkind = pars[0];

		String sql = "SELECT a.userid FROM v_pub_user_post_1 a LEFT JOIN pub_post b ON a.postid = b.id WHERE a.isdefault = 'Y' AND b.stationkind in (" + TBUtil.getTBUtil().getInCondition(stationkind) + ") ORDER BY linkcode, usercode";
		try {
			String[] strs = UIUtil.getStringArrayFirstColByDS(null, sql);
			if (strs != null && strs.length > 0) {
				StringBuffer sb_return = new StringBuffer(";");
				for (int i = 0; i < strs.length; i++) {
					sb_return.append(strs[i]);
					sb_return.append(";");
				}
				return sb_return.toString();
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}
