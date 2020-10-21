package com.pushworld.ipushgrc.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;
/**
 * 我的收藏夹首页数据生成器
 * @author hm
 *
 */
public class MyFavoriteDataBuilder implements DeskTopNewsDataBuilderIFC{
	public HashVO[] getNewData(String userCode) throws Exception {
		String userid = new WLTInitContext().getCurrSession().getLoginUserId();
		String dbType = ServerEnvironment.getDefaultDataSourceType().toUpperCase();
		HashVO[] hvs = null;
		String _tableName = "my_favorites";
		StringBuffer sql_sb = new StringBuffer();
		if (dbType.equalsIgnoreCase("MYSQL")) {
			sql_sb.append("select *  from "+_tableName+" where creater ='"+ userid +"'  order by createdate desc limit 0,20");
		} else if (dbType.equalsIgnoreCase("ORACLE")) {
			sql_sb.append("select " + _tableName + ".* from (select " + _tableName + ".*,Rownum RN from (select " + _tableName + ".* from " + _tableName + " where creater ='"+ userid +"' order by createdate desc )" + _tableName);
			sql_sb.append("  where Rownum <=20) " + _tableName);
			sql_sb.append(" where RN > " + 0);
		} else if (dbType.equals("SQLSERVER")) {
			StringBuilder sb_sql_new = new StringBuilder(); //
			sb_sql_new.append("with _t1 as "); //
			sb_sql_new.append("("); //
			sb_sql_new.append("select row_number() over (order by createdate desc) _rownum,");
			sb_sql_new.append(" * from " + _tableName +" where creater ='"+ userid +"'"); // 将原来的select后面开始的内容接上来!
			sb_sql_new.append(") ");
			sb_sql_new.append("select top " + (20) + " * from _t1 where _rownum >= " + 0 + ""); // 分页!!!
			sql_sb.append(sb_sql_new.toString()); //
		}else if(dbType.equals("DB2")){
			sql_sb.append("select * from " + _tableName + " where creater ='"+ userid +"'  order by createdate desc fetch first 20 rows only");
		}
		hvs = new CommDMO().getHashVoArrayByDS(null, sql_sb.toString());
		for (int i = 0; i < hvs.length; i++) {
			hvs[i].setToStringFieldName("itemname"); // 标题!!!
		}
		return hvs;
	}
}
