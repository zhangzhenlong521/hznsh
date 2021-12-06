package com.pushworld.ipushgrc.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * �Ϲ�����ҳ���ݹ�����.����鿪ʼ�������򣬲���״̬����ʵʩ�У�ȡ��ǰ20����
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
			sb_sql_new.append(" * from " + _tableName); // ��ԭ����select���濪ʼ�����ݽ�����!
			sb_sql_new.append(") ");
			sb_sql_new.append("select top " + (20) + " * from _t1 where _rownum >= " + 0 + ""); // ��ҳ!!!
			sql_sb.append(sb_sql_new.toString()); //
		}else if(dbType.equals("DB2")){
			sql_sb.append("select * from " + _tableName + " where status=2 order by checkbegindate desc fetch first 20 rows only");
		}
		hvs = new CommDMO().getHashVoArrayByDS(null, sql_sb.toString());
		for (int i = 0; i < hvs.length; i++) {
			hvs[i].setToStringFieldName("checkname"); // ����!!!
		}
		return hvs;
	}
}
