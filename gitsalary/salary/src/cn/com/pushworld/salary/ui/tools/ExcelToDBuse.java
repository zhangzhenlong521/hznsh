package cn.com.pushworld.salary.ui.tools;

import java.util.ArrayList;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.sysapp.other.ExcelToDBIfc;

public class ExcelToDBuse implements ExcelToDBIfc{

	public void Action(String state, String year, String month, String tablename) throws Exception {
		String sql = "select * from "+tablename+" where year='" + year + "' and month='" + month + "' order by id";
		HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, sql);
		
		ArrayList list_sqls = new ArrayList();
		String creattime = UIUtil.getServerCurrTime();
		list_sqls.add("delete from sal_person_fund_account where importmonth='"+year+"-"+month+"'");
		for (int i = 2; i < hvs.length; i++) {
			InsertSQLBuilder insert = new InsertSQLBuilder("sal_person_fund_account");
			insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_" + "sal_person_fund_account".toUpperCase()));
			insert.putFieldValue("username", hvs[i].getStringValue("A", ""));
			insert.putFieldValue("formtype", "延期支付基金");
			insert.putFieldValue("busitype", hvs[i].getStringValue("D", ""));
			insert.putFieldValue("calc", "-");
			insert.putFieldValue("money", hvs[i].getStringValue("E", ""));
			insert.putFieldValue("descr", hvs[i].getStringValue("F", ""));
			insert.putFieldValue("datadate", hvs[i].getStringValue("C", ""));
			insert.putFieldValue("importmonth", year+"-"+month);
			insert.putFieldValue("createtime", creattime);
			insert.putFieldValue("createuser", ClientEnvironment.getCurrLoginUserVO().getId());
			list_sqls.add(insert.getSQL());
		}
		
		list_sqls.add("update sal_person_fund_account a, v_sal_personinfo b " +
				"set a.userid = b.id, a.username = b.name where a.username = b.code and importmonth = '"+year+"-"+month+"'");
		
		UIUtil.executeBatchByDS(null, list_sqls);
	}

}
