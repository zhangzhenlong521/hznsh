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
		//ȡ�������������
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
	 * �Ƚ�ֻ����1,������2�е�����
	 * @param _dsname_1
	 * @param _dsname_2
	 * @return
	 * @throws Exception
	 */
	private String getNewTabOrColumnHtml(String _dsname_1, String _dsname_2) throws Exception {
		//Դ1�е�����..
		VectorMap map1 = new VectorMap(); //
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds1.length; i++) {
			String str_tabcode = hvs_ds1[i].getStringValue("tabname").toLowerCase(); //����
			String str_colname = hvs_ds1[i].getStringValue("colname").toLowerCase(); //����
			String str_coltype = hvs_ds1[i].getStringValue("datatype").toLowerCase(); //����
			String str_coldesc = hvs_ds1[i].getStringValue("coldesc").toLowerCase(); //��˵��
			if (map1.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map1.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map1.put(str_tabcode, v_columns); //
			}
		}

		//Դ2�е�����
		VectorMap map2 = new VectorMap(); //
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds2.length; i++) {
			String str_tabcode = hvs_ds2[i].getStringValue("tabname").toLowerCase(); //����
			String str_colname = hvs_ds2[i].getStringValue("colname").toLowerCase(); //����
			String str_coltype = hvs_ds2[i].getStringValue("datatype".toLowerCase()); //����
			String str_coldesc = hvs_ds2[i].getStringValue("coldesc").toLowerCase(); //��˵��
			if (map2.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map2.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map2.put(str_tabcode, v_columns); //
			}
		}

		//�ҳ�ֻ������1��������2�е�����..
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<table width=\"75%\"   border=\"0\"   cellspacing=\"0\"   cellpadding=\"3\">\r\n"); //
		sb_html.append("<tr><td align=\"center\">����</td><td align=\"center\">����</td><td align=\"center\">������</td><td>SQL���</td></tr>\r\n"); //
		String[] str_alltabs = map1.getKeysAsString(); //�ӱ���Դ1�����б�ʼ!!
		int li_count = 0; //
		int li_count_newtab = 0; //
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //���Դ2���иñ�,��ֻ�Ƚ���..
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //ȡ��Դ2�еĵĸ�����������
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				for (int j = 0; j < str_allcolumns1.length; j++) { //����������
					if (!v_columns2.containsKey(str_allcolumns1[j])) { //���Դ2�иñ���������,�����
						if (!bo_iffind) {
							li_count++;
							if (li_count != 1) {
								sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
							}
							sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]����Դ[" + _dsname_1 + "]������Դ[" + _dsname_2 + "]�ڱ�[" + str_alltabs[i] + "]�������</td></tr>\r\n"); //
							bo_iffind = true;
						}
						String[] str_colinfo = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
						sb_html.append("<tr><td>" + str_alltabs[i] + "</td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td><td>alter table " + str_alltabs[i] + " add " + str_colinfo[0] + " " + str_colinfo[1] + ";</td></tr>\r\n"); //
					}
				}
			} else { //������ѭ�����������
				li_count++;
				if (li_count != 1) {
					sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
				}
				sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]����Դ[" + _dsname_1 + "]������Դ[" + _dsname_2 + "]����ı�[" + str_alltabs[i] + "]</td></tr>\r\n"); //

				li_count_newtab++; //
				VectorMap v_columns = (VectorMap) map1.get(str_alltabs[i]);
				String[] str_allcolumns = v_columns.getKeysAsString(); //
				for (int j = 0; j < str_allcolumns.length; j++) { //����������
					String[] str_colinfo = (String[]) v_columns.get(str_allcolumns[j]); //ȡ������Ϣ
					sb_html.append("<tr><td><font color=\"blue\">" + str_alltabs[i] + "</font></td><td><font color=\"blue\">" + str_colinfo[0] + "</font></td><td>" + str_colinfo[1] + "</td>"
							+ (j == 0 ? "<td rowspan=" + str_allcolumns.length + " valign=\"top\">" + getCreateTableSQL(str_alltabs[i], v_columns) + "</td>" : "") + "</tr>\r\n"); //
				}
			}
		}
		sb_html.append("</table>\r\n"); //

		String str_return = "<font size=2 color=\"red\">����������Դ[" + _dsname_1 + "]����������Դ[" + _dsname_2 + "]�е����:������[" + li_count_newtab + "]��</font><br>" + sb_html.toString(); //
		return str_return; //
	}

	private String getCreateTableSQL(String _tablename, VectorMap _allcolumns) {
		StringBuffer sb_sql = new StringBuffer(); //
		if (_tablename.startsWith("v_")) {
			sb_sql.append("<font color=\"red\">��������ͼ,������ִ��SQL!</font><br>");  //
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
	 * �Ƚ��в�һ����
	 * @return
	 * @throws Excpetion
	 */
	private String getCompareHtml(String _dsname_1, String _dsname_2) throws Exception {
		//Դ1�е�����..
		VectorMap map1 = new VectorMap(); //
		HashVO[] hvs_ds1 = commDMO.getHashVoArrayByDS(_dsname_1, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds1.length; i++) {
			String str_tabcode = hvs_ds1[i].getStringValue("tabname").toLowerCase(); //����
			String str_colname = hvs_ds1[i].getStringValue("colname").toLowerCase(); //����
			String str_coltype = hvs_ds1[i].getStringValue("datatype").toLowerCase(); //����
			String str_coldesc = hvs_ds1[i].getStringValue("coldesc").toLowerCase(); //��˵��
			if (map1.containsKey(str_tabcode)) {
				VectorMap v_columns = (VectorMap) map1.get(str_tabcode); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
			} else {
				VectorMap v_columns = new VectorMap(); //
				v_columns.put(str_colname, new String[] { str_colname, str_coltype, str_coldesc }); //
				map1.put(str_tabcode, v_columns); //
			}
		}

		//Դ2�е�����
		VectorMap map2 = new VectorMap(); //
		HashVO[] hvs_ds2 = commDMO.getHashVoArrayByDS(_dsname_2, "select * from v_pub_syscolumns order by tabname,colcode");
		for (int i = 0; i < hvs_ds2.length; i++) {
			String str_tabcode = hvs_ds2[i].getStringValue("tabname").toLowerCase(); //����
			String str_colname = hvs_ds2[i].getStringValue("colname").toLowerCase(); //����
			String str_coltype = hvs_ds2[i].getStringValue("datatype").toLowerCase(); //����
			String str_coldesc = hvs_ds2[i].getStringValue("coldesc").toLowerCase(); //��˵��
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
		sb_html.append("<tr><td align=\"center\">����</td><td align=\"center\">����</td><td align=\"center\">����Դ[" + _dsname_1 + "]������</td><td align=\"center\">����Դ[" + _dsname_2 + "]������</td></tr>\r\n"); //

		String[] str_alltabs = map1.getKeysAsString(); //�ӱ���Դ1�����б�ʼ!!
		int li_count = 0;
		for (int i = 0; i < str_alltabs.length; i++) {
			if (map2.containsKey(str_alltabs[i])) { //���Դ2���иñ�,
				VectorMap v_columns1 = (VectorMap) map1.get(str_alltabs[i]);
				VectorMap v_columns2 = (VectorMap) map2.get(str_alltabs[i]); //ȡ��Դ2�еĵĸ�����������
				String[] str_allcolumns1 = v_columns1.getKeysAsString(); //
				boolean bo_iffind = false; //
				for (int j = 0; j < str_allcolumns1.length; j++) { //����������
					if (v_columns2.containsKey(str_allcolumns1[j])) { //���Դ2Ҳ��������
						String[] str_colinfo1 = (String[]) v_columns1.get(str_allcolumns1[j]); //ȡ������Ϣ
						String[] str_colinfo2 = (String[]) v_columns2.get(str_allcolumns1[j]); //ȡ������Ϣ
						if (!str_colinfo1[1].equals(str_colinfo2[1])) {
							if (!bo_iffind) {
								if (!bo_iffind) {
									li_count++;
									if (li_count != 1) {
										sb_html.append("<tr><td colspan=4>&nbsp;<br><br></td></tr>");
									}
									sb_html.append("<tr><td colspan=4 bgcolor=\"cyan\">[" + li_count + "]������Դ�ڱ�[" + str_alltabs[i] + "]�������е��������Ͳ�һ��</td></tr>\r\n"); //
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
