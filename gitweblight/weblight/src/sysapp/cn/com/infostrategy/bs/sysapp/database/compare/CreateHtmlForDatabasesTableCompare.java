package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.VectorMap;

public class CreateHtmlForDatabasesTableCompare implements WebCallBeanIfc {

	private CommDMO commDMO = null;

	@SuppressWarnings("unchecked")
	public String getHtmlContent(HashMap map) throws Exception {
		//取得两个表的数据
		String type = (String) map.get("type"); //
		String dataSource1 = map.get("datasource1").toString();
		String dataSource2 = map.get("datasource2").toString();

		commDMO = new CommDMO();

		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<META http-equiv=Content-Type content=\"text/html; charset=GBK\">\r\n"); //
		sb_html.append("<style   type=\"text/css\"> \r\n");
		sb_html.append("<!--   \r\n");
		sb_html.append(" table   {  border-collapse:   collapse; }   \r\n");
		sb_html.append("td   {  font-size: 12px; border:solid   1px   #888888;  }   \r\n");
		sb_html.append(" -->   \r\n");
		sb_html.append(" </style>   \r\n");
		sb_html.append("</head>\r\n");
		sb_html.append("<body>\r\n");
		if (type.equals("0")) {
			sb_html.append(getNewTabOrColumnHtml(dataSource1, dataSource2)); //
		} else if (type.equals("1")) {
			sb_html.append(getNewTabOrColumnHtml(dataSource2, dataSource1)); //
		} else if (type.equals("2")) {
			sb_html.append(getCompareHtml(dataSource1, dataSource2)); //
		}

		sb_html.append("</body>\r\n");
		sb_html.append("</html>\r\n");
		return sb_html.toString(); //
	}

	/**
	 * 比较只存于1,不存于2中的数据
	 * @param _dsname_1
	 * @param _dsname_2
	 * @return
	 * @throws Exception
	 */
	private String getNewTabOrColumnHtml(String _dsname_1, String _dsname_2) throws Exception {
		//源1中的数据..
		VectorMap map1 = new VectorMap(); //
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds1.length; i++) {
			String str_tabcode = hvs_ds1[i].getStringValue("tabname").toLowerCase(); //表名
			String str_colname = hvs_ds1[i].getStringValue("colname").toLowerCase(); //列名
			String str_coltype = hvs_ds1[i].getStringValue("datatype").toLowerCase(); //列名
			String str_coldesc = hvs_ds1[i].getStringValue("coldesc").toLowerCase(); //列说明
			if (map1.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map1.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map1.put(str_tabcode, v_columns); //
			}
		}

		//源2中的数据
		VectorMap map2 = new VectorMap(); //
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds2.length; i++) {
			String str_tabcode = hvs_ds2[i].getStringValue("tabname").toLowerCase(); //表名
			String str_colname = hvs_ds2[i].getStringValue("colname").toLowerCase(); //列名
			String str_coltype = hvs_ds2[i].getStringValue("datatype".toLowerCase()); //列名
			String str_coldesc = hvs_ds2[i].getStringValue("coldesc").toLowerCase(); //列说明
			if (map2.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map2.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map2.put(str_tabcode, v_columns); //
			}
		}

		//找出只存在于1而不存于2中的数据..
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table width=\"75%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">表名</td><td align=\"center\">列名</td><td align=\"center\">列类型</td><td>SQL语句</td></tr>\r\n"); //
		String[] str_alltabs = map1.getKeysAsString(); //从遍历源1中所有表开始!!
		int li_count = 0; //
		int li_count_newtab = 0; //
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //如果源2中有该表,则只比较列..
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //取得源2中的的该所有所有列
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				for (int j = 0; j < str_allcolumns1.length; j++) { //遍历所有列
					if (!v_columns2.containsKey(str_allcolumns1[j])) { //如果源2中该表不包含该列,则输出
						if (!bo_iffind) {
							li_count++;
							if (li_count != 1) {
								sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
							}
							sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]数据源[" + _dsname_1 + "]比数据源[" + _dsname_2 + "]在表[" + str_alltabs[i] + "]多出的列</td></tr>\r\n"); //
							bo_iffind = true;
						}
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
						sb_html.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td><td>alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " " + str_colinfo[1] + ";</td></tr>\r\n"); //
					}
				}
			} else { //否则死循环输出所有列
				li_count++;
				if (li_count != 1) {
					sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
				}
				sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]数据源[" + _dsname_1 + "]比数据源[" + _dsname_2 + "]多出的表[" + str_alltabs[i] + "]</td></tr>\r\n"); //

				li_count_newtab++; //
				VectorMap v_columns = (VectorMap) map1.get(str_alltabs[i]);
				String[] str_allcolumns = v_columns.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns.length; j++) { //遍历所有列
					String[] str_colinfo = (String[]) v_columns.get(str_allcolumns[j]); //取得列信息
					sb_html.append("<tr><td><font color=\"blue\">" + str_alltabs[i] + "</font></td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td>"
							+ (j == 0 ? "<td rowspan=" + str_allcolumns.length + " valign=\"top\">" + getCreateTableSQL(str_alltabs[i], v_columns) + "</td>" : "") + "</tr>\r\n"); //
				}
			}
		}
		sb_html.append("</table>\r\n"); //

		String str_return = "<font size=2 color=\"red\">存在于数据源[" + _dsname_1 + "]而不在数据源[" + _dsname_2 + "]中的情况:新增表[" + li_count_newtab + "]个</font><br>" + sb_html.toString(); //
		return str_return; //
	}

	private String getCreateTableSQL(String _tablename, VectorMap _allcolumns) {
		StringBuffer sb_sql = new StringBuffer(); //
		if (_tablename.startsWith("v_")) {
			sb_sql.append("<font color=\"red\">好象是视图,请慎重执行SQL!</font><br>");  //
		}

		sb_sql.append("create table " + _tablename + "<br>\r\n"); //
		sb_sql.append("(<br>\r\n");
		String[] all_keys = _allcolumns.getKeysAsString(); //
		for (int i = 0; i < all_keys.length; i++) {
			String[] str_colinfo = (String[]) _allcolumns.get(all_keys[i]); //
			sb_sql.append("  " + str_colinfo[0] + " " + str_colinfo[1] + ",<br>\r\n");
		}
		sb_sql.append("  constraint pk_" + _tablename + " primary key (id)<br>\r\n");
		sb_sql.append(") DEFAULT CHARSET=gb2312;<br><br>\r\n");
		return sb_sql.toString();
	}

	/**
	 * 比较列不一样的
	 * @return
	 * @throws Excpetion
	 */
	private String getCompareHtml(String _dsname_1, String _dsname_2) throws Exception {
		//源1中的数据..
		VectorMap map1 = new VectorMap(); //
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds1.length; i++) {
			String str_tabcode = hvs_ds1[i].getStringValue("tabname").toLowerCase(); //表名
			String str_colname = hvs_ds1[i].getStringValue("colname").toLowerCase(); //列名
			String str_coltype = hvs_ds1[i].getStringValue("datatype").toLowerCase(); //列名
			String str_coldesc = hvs_ds1[i].getStringValue("coldesc").toLowerCase(); //列说明
			if (map1.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map1.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map1.put(str_tabcode, v_columns); //
			}
		}

		//源2中的数据
		VectorMap map2 = new VectorMap(); //
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds2.length; i++) {
			String str_tabcode = hvs_ds2[i].getStringValue("tabname").toLowerCase(); //表名
			String str_colname = hvs_ds2[i].getStringValue("colname").toLowerCase(); //列名
			String str_coltype = hvs_ds2[i].getStringValue("datatype").toLowerCase(); //列名
			String str_coldesc = hvs_ds2[i].getStringValue("coldesc").toLowerCase(); //列说明
			if (map2.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map2.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map2.put(str_tabcode, v_columns); //
			}
		}

		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table width=\"75%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">表名</td><td align=\"center\">列名</td><td align=\"center\">数据源[" + _dsname_1 + "]列类型</td><td align=\"center\">数据源[" + _dsname_2 + "]列类型</td></tr>\r\n"); //

		String[] str_alltabs = map1.getKeysAsString(); //从遍历源1中所有表开始!!
		int li_count = 0;
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //如果源2中有该表,
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //取得源2中的的该所有所有列
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				for (int j = 0; j < str_allcolumns1.length; j++) { //遍历所有列
					if (v_columns2.containsKey(str_allcolumns1[j])) { //如果源2也包含该列
						String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[j]); //取得列信息
						String[] str_colinfo2 = (String[]) v_columns2.get(str_allcolumns1[j]); //取得列信息
						if (!str_colinfo1[1].equals(str_colinfo2[1])) {
							if (!bo_iffind) {
								if (!bo_iffind) {
									li_count++;
									if (li_count != 1) {
										sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
									}
									sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]两数据源在表[" + str_alltabs[i] + "]的以下列的数据类型不一样</td></tr>\r\n"); //
									bo_iffind = true;
								}
							}
							sb_html.append("<tr><td><font color=\"blue\">" + str_alltabs[i] + "</font></td><td><font color=\"blue\">" + str_allcolumns1[j] + "</font></td><td>" + str_colinfo1[1] + "</td><td>" + str_colinfo2[1] + "</td></tr>\r\n"); //
						}
					}
				}

			}
		}

		sb_html.append("</table>\r\n"); //
		return sb_html.toString(); //
	}

}
