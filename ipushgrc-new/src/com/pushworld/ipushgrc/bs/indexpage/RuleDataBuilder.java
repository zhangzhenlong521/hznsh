package com.pushworld.ipushgrc.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * ��ҳ���ݹ�����-�ڲ��ƶ�-��������������ȡǰ20����
 * @author hm
 *
 */
public class RuleDataBuilder implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String _userCode) throws Exception {
		String dbType = ServerEnvironment.getDefaultDataSourceType().toUpperCase();
		HashVO[] hvs = null;
		String _tableName = "rule_rule";
		StringBuffer sql_sb = new StringBuffer();
		//����ũ���������ҳ������ƶȵ������ʱ��ʾ���У������Ӹò�����-1��0 ��ʾ���У�Ĭ��Ϊ20�����/2018-05-23��
		//������Ŀ��������ʾ���У���¼ʱ��������ݣ��϶�����ǰ��¼Ҫ�������/2018-05-23��
		int count = TBUtil.getTBUtil().getSysOptionIntegerValue("��ҳ������ƶȵ��������ʾ������¼", 20);
		if (count > 0) {
			if (dbType.equalsIgnoreCase("MYSQL")) {
				sql_sb.append("select *  from " + _tableName + " order by publishdate desc limit 0,"+count);
			} else if (dbType.equalsIgnoreCase("ORACLE")) {
				sql_sb.append("select " + _tableName + ".* from (select " + _tableName + ".*,Rownum RN from (select " + _tableName + ".* from " + _tableName + " order by publishdate desc )" + _tableName);
				sql_sb.append("  where Rownum <="+count+") " + _tableName);
				sql_sb.append(" where RN > " + 0);
			} else if (dbType.equals("SQLSERVER")) {
				StringBuilder sb_sql_new = new StringBuilder(); //
				sb_sql_new.append("with _t1 as "); //
				sb_sql_new.append("("); //
				sb_sql_new.append("select row_number() over (order by publishdate desc) _rownum,");
				sb_sql_new.append(" * from " + _tableName); // ��ԭ����select���濪ʼ�����ݽ�����!
				sb_sql_new.append(") ");
				sb_sql_new.append("select top " + (count) + " * from _t1 where _rownum >= " + 0 + ""); // ��ҳ!!!
				sql_sb.append(sb_sql_new.toString()); //
			} else if (dbType.equals("DB2")) {
				sql_sb.append("select * from " + _tableName + " order by publishdate desc fetch first "+count+" rows only");
			}
		} else {//��ʾ����
			sql_sb.append("select *  from " + _tableName + " order by publishdate desc ");
		}

		hvs = new CommDMO().getHashVoArrayByDS(null, sql_sb.toString());
		for (int i = 0; i < hvs.length; i++) {
			hvs[i].setToStringFieldName("rulename"); // ����!!!
		}
		return hvs;
	}
}