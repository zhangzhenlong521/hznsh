package cn.com.pushworld.wn;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

/**
 * 
 * 
 * @author zzl
 * 
 *         2019-3-19-上午10:13:27
 * 
 *         日均存款余额已计发保存
 */
public class RIJunEffectiveDeposit {
	public String impCkYxHsTj(String usercount) {
		String countsb = new String();
		CommDMO dmo = new CommDMO();
		String[] str = usercount.split(";");
		InsertSQLBuilder insert = new InsertSQLBuilder("WN_RJ_CKYXHSTJ");
		try {
			String id = dmo.getSequenceNextValByDS(null, "S_WN_RJ_CKYXHSTJ");
			HashVO[] vo = dmo.getHashVoArrayByDS(null,
					"select * from pub_user where code='" + str[0].toString()
							+ "'");
			insert.putFieldValue("id", id);
			insert.putFieldValue("A", vo[0].getStringValue("code"));
			insert.putFieldValue("B", vo[0].getStringValue("name"));
			insert.putFieldValue("C", str[1].toString());// 已计发的
			insert.putFieldValue("D", str[2].toString());// 没有计发的
			insert.putFieldValue("E", str[3].toString());
			dmo.executeUpdateByDS(null, insert.getSQL());
			countsb = "0";
		} catch (Exception e) {
			countsb = "0000000";
			e.printStackTrace();
		}
		return countsb;

	}
}
