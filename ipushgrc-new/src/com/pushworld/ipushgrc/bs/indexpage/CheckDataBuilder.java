package com.pushworld.ipushgrc.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 合规检查首页数据构造器.按检查开始日期排序，并且状态是在实施中，取得前20条。
 * @author lcj
 *
 */
public class CheckDataBuilder implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String _userCode) throws Exception {
		String dbType = ServerEnvironment.getDefaultDataSourceType().toUpperCase();
		HashVO[] hvs = null;
		String _tableName = "cmp_check";
		StringBuffer sql_sb = new StringBuffer();
		if (dbType.equalsIgnoreCase("MYSQL")) {
			sql_sb.append("select *  from cmp_check where status=2 order by checkbegindate desc limit 0,20");
		} else if (dbType.equalsIgnoreCase("ORACLE")) {
			sql_sb.append("select " + _tableName + ".* from (select " + _tableName + ".*,Rownum RN from (select " + _tableName + ".* from " + _tableName + " where status=2 order by checkbegindate desc )" + _tableName);
			sql_sb.append("  where Rownum <=20) " + _tableName);
			sql_sb.append(" where RN > " + 0);
		} else if (dbType.equals("SQLSERVER")) {
			StringBuilder sb_sql_new = new StringBuilder(); //
			sb_sql_new.append("with _t1 as "); //
			sb_sql_new.append("("); //
			sb_sql_new.append("select row_number() over (where status=2 order by checkbegindate desc) _rownum,");
			sb_sql_new.append(" * from " + _tableName); // 将原来的select后面开始的内容接上来!
			sb_sql_new.append(") ");
			sb_sql_new.append("select top " + (20) + " * from _t1 where _rownum >= " + 0 + ""); // 分页!!!
			sql_sb.append(sb_sql_new.toString()); //
		}else if(dbType.equals("DB2")){
			sql_sb.append("select * from " + _tableName + " where status=2 order by checkbegindate desc fetch first 20 rows only");
		}
		hvs = new CommDMO().getHashVoArrayByDS(null, sql_sb.toString());
		for (int i = 0; i < hvs.length; i++) {
			hvs[i].setToStringFieldName("checkname"); // 标题!!!
		}
		return hvs;
	}
}
